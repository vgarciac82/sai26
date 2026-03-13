package com.syc.sai.contabilidad.caja;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.core.Caso;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CajaManager extends DataSourceManager {

    private static Logger log = LoggerFactory.getLogger(CajaManager.class);

    public static int ejecutaQueryRI(String query) throws Exception {
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        Connection conn = CajaManager.getConnection("jdbc/gestion");
        int val = -1;
        try {
            pStatement = conn.prepareStatement(query);
            rs = pStatement.executeQuery();
            while (rs.next()) {
                val = rs.getInt(1);
            }
        } catch (Exception e) {
            log.warn("Problema ejecutando query:\n\n" + query + "\n\n" + e.toString());
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(pStatement);
            CloseObject.closeObject(conn);
        }
        return val;
    }

    public static String ejecutaQueryRS(String query) throws Exception {
        return ejecutaQueryRS(null, query);
    }

    public static String ejecutaQueryRS(Connection conn, String query) throws Exception {
        boolean cierraTransaccion = false;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        if (conn == null) {
            conn = CajaManager.getConnection("jdbc/gestion");
            cierraTransaccion = true;
        }
        String val = "";
        try {
            pStatement = conn.prepareStatement(query);
            rs = pStatement.executeQuery();
            while (rs.next()) {
                val = rs.getString(1);
            }
            return val;
        } catch (Exception e) {
            log.warn("Problema ejecutando query:\n\n" + query + "\n\n" + e.toString());
            throw e;
        } finally {
            CloseObject.closeObject(pStatement);
            CloseObject.closeObject(rs);
            if (cierraTransaccion) {
                log.trace("Se cierra conexion");
                CloseObject.closeObject(conn);
            }
        }
    }

    public static String ejecutaQueryCadena(String query, String campo) throws Exception {
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        Connection conn = CajaManager.getConnection("jdbc/gestion");
        String val = "";
        String Cadena = "'";
        int longitud = 0;
        try {
            pStatement = conn.prepareStatement(query);
            rs = pStatement.executeQuery();
            while (rs.next()) {
                val = rs.getString(campo);
                Cadena = Cadena + val + "','";
            }
            longitud = Cadena.length();
            // log.debug(Cadena);
            // log.debug("tamañao:"+longitud);
            // el -2 es ára quitar
            Cadena = Cadena.substring(0, longitud - 2);
            // el ultimo ,'
        } catch (Exception e) {
            log.warn("Problema ejecutando query:\n\n" + query + "\n\n" + e.toString());
        } finally {
            if (pStatement != null)
                try {
                    pStatement.close();
                } catch (Exception e2) {
                    log.warn("Problemas cerrando PreparedStatement " + e2.toString());
                }
            if (rs != null)
                try {
                    rs.close();
                } catch (Exception e2) {
                    log.warn("Problemas cerrando ResultSet " + e2.toString());
                }
            conn.close();
        }
        return Cadena;
    }

    public static void cancelaCheque(String update) throws SQLException {
        cancelaCheque(null, update);
    }

    public static void cancelaCheque(Connection conn, String update) throws SQLException {
        boolean cierraTransaccion = false;
        if (conn == null) {
            conn = CajaManager.getConnection("jdbc/gestion");
            cierraTransaccion = true;
        }
        Statement pstmnt = null;
        try {
            pstmnt = conn.createStatement();
            log.debug("Ejecuta query: " + update);
            pstmnt.executeUpdate(update);
            if (cierraTransaccion)
                conn.commit();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if (cierraTransaccion && conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Error realizando rollback: " + e2, e2);
                }
            throw new SQLException(e);
        } finally {
            CloseObject.closeObject(pstmnt);
            if (cierraTransaccion)
                CloseObject.closeObject(conn);
        }
    }

    public static void updateQuery(String update) throws SQLException {
        updateQuery(null, update);
    }

    public static void updateQuery(Connection conn, String update) throws SQLException {
        boolean cierraTransaccion = false;
        if (conn == null) {
            conn = CajaManager.getConnection("jdbc/gestion");
            cierraTransaccion = true;
        }
        Statement pstmnt = null;
        try {
            pstmnt = conn.createStatement();
            log.debug("Ejecuta query: " + update);
            pstmnt.executeUpdate(update);
            if (cierraTransaccion)
                conn.commit();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if (cierraTransaccion && conn != null) {
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Error realizando rollback: " + e2, e2);
                }
            }
            throw new SQLException(e);
        } finally {
            CloseObject.closeObject(pstmnt);
            if (cierraTransaccion)
                CloseObject.closeObject(conn);
        }
    }

    public static void descartaCaja(Connection conn, String uLogin, Caso c) throws Exception {
        int nFolioCaja = Integer.parseInt(c.getFolio().substring(c.getFolio().lastIndexOf("-") + 1));
        descartaCaja(conn, uLogin, nFolioCaja);
    }

    public static void descartaCaja(Connection conn, String uLogin, int nFolioCaja) throws Exception {
        String queryEncabezado = "SELECT nFolioComprobacion FROM tCajaEncabezado WITH(nolock) WHERE nFolioCaja = ? ";
        String queryEvento = "SELECT cEvento FROM tCajaDetalle WITH(nolock) WHERE nFolioCaja = ? ";
        String querySaldoInicial = "SELECT ISNULL(esSaldoInicial,0) AS esSI FROM dbo.tcajaencabezado (NOLOCK) WHERE nFoliocaja = ?";
        String queryExiste = "SELECT COUNT(*) AS Existe FROM tCajaEncabezado (NOLOCK) WHERE nFolioCaja = ?";
        PreparedStatement psEncabezado = null, psEvento = null, psUpdate = null, psExiste = null;
        // Saldo Inicial
        PreparedStatement psSI = null, psSICancel = null, psCajaNoSi = null;
        // Bonificaciones
        PreparedStatement psCajaDescartaComp = null, psBonifDelete = null, psEdoCtaDescarta = null, psEcCompDelete = null;
        ResultSet rsEventos = null, rsComprobacion = null, rsSI = null, rsExiste = null;
        Boolean rsu, rsSICancel, rsCajaNoSI, rsCajaDescartaComp, rsBonifDelete, rsEdoCtaDescarta;
        int rsEcCompDelete;
        String esSaldoInicial = "0";
        int existe = 0;
        try {
            psExiste = conn.prepareStatement(queryExiste);
            psExiste.setInt(1, nFolioCaja);
            rsExiste = psExiste.executeQuery();
            if (rsExiste.next()) {
                existe = rsExiste.getInt("Existe");
            }
            if (existe > 0) {
                psSI = conn.prepareStatement(querySaldoInicial);
                psSI.setInt(1, nFolioCaja);
                rsSI = psSI.executeQuery();
                if (rsSI.next())
                    esSaldoInicial = rsSI.getString("esSI");
                if ("1".equals(esSaldoInicial)) {
                    String querySICancel = "UPDATE dbo.tSaldoAntiguedadEncabezado SET cEstatus = 'Cancelado' WHERE nFolioCaja = ? ";
                    String queryCajaNoSI = "UPDATE dbo.tcajaencabezado SET esSaldoInicial = 0 WHERE nFoliocaja = ?";
                    psCajaNoSi = conn.prepareStatement(queryCajaNoSI);
                    psCajaNoSi.setInt(1, nFolioCaja);
                    psSICancel = conn.prepareStatement(querySICancel);
                    psSICancel.setInt(1, nFolioCaja);
                    rsCajaNoSI = psCajaNoSi.execute();
                    rsSICancel = psSICancel.execute();
                    if (rsSICancel)
                        throw new Exception("No se Cancelo el Saldo Inicial para la solicitud: " + nFolioCaja);
                    if (rsCajaNoSI)
                        throw new Exception("No se Modifico el registro esSaldoInicial de la Solicitud: " + nFolioCaja);
                } else {
                    psEncabezado = conn.prepareStatement(queryEncabezado);
                    psEncabezado.setInt(1, nFolioCaja);
                    psEvento = conn.prepareStatement(queryEvento);
                    psEvento.setInt(1, nFolioCaja);
                    rsEventos = psEvento.executeQuery();
                    rsComprobacion = psEncabezado.executeQuery();
                    while (rsEventos.next()) {
                        String evento = rsEventos.getString("cEvento");
                        String[] componentesEvento = evento.split("_");
                        if ("35_1_2_A".equals(evento) || "35_2_7".equals(evento)) {
                            String queryCajaDescartaComp = "UPDATE tcajaencabezado SET comprobado=0, nFolioComprobacion=0 WHERE nfoliocaja IN (SELECT nfoliocaja  FROM tBonificacion_Comision WHERE nFolioComprobacion= ?)";
                            psCajaDescartaComp = conn.prepareStatement(queryCajaDescartaComp);
                            psCajaDescartaComp.setInt(1, nFolioCaja);
                            rsCajaDescartaComp = psCajaDescartaComp.execute();
                            if (rsCajaDescartaComp) {
                                String queryBonifDelete = "DELETE FROM tBonificacion_Comision WHERE nFolioComprobacion= ?";
                                String queryEdoCtaDescarta = "UPDATE tEstadoDeCuentaComprobacionesDetalle SET cDocumentoHaplicado='C' WHERE nfoliocomprobacion= ?";
                                psBonifDelete = conn.prepareStatement(queryBonifDelete);
                                psBonifDelete.setInt(1, nFolioCaja);
                                psEdoCtaDescarta = conn.prepareStatement(queryEdoCtaDescarta);
                                psEdoCtaDescarta.setInt(1, nFolioCaja);
                                rsBonifDelete = psBonifDelete.execute();
                                rsEdoCtaDescarta = psEdoCtaDescarta.execute();
                                if (rsBonifDelete)
                                    throw new Exception("No se Elimino la Boinificacion de la solicitud: " + nFolioCaja);
                                if (rsEdoCtaDescarta)
                                    throw new Exception("No se Modifico el registro cDocumentoHaplicado de la Comprobacion para de la solicitud: " + nFolioCaja);
                            }
                        } else if ("35_2_1_A".equals(evento) || "35_1_3".equals(evento)) {
                            String queryExisteComp = "SELECT COUNT(*) AS Existe FROM tEstadoDeCuentaComprobacionesEncabezado (NOLOCK) WHERE nFolioCaja= ?";
                            String queryEcCompDelete = "DELETE FROM tEstadoDeCuentaComprobacionesEncabezado WHERE nFolioCaja= ?";
                            boolean existeEdoCuenta = false;
                            PreparedStatement psExisteComp = null;
                            ResultSet rsExisteComp = null;
                            try {
                                psExisteComp = conn.prepareStatement(queryExisteComp);
                                psExisteComp.setInt(1, nFolioCaja);
                                rsExisteComp = psExisteComp.executeQuery();
                                if (rsExisteComp.next())
                                    existeEdoCuenta = rsExisteComp.getInt(1) > 0;
                            } finally {
                                CloseObject.closeObject(psExisteComp);
                                CloseObject.closeObject(rsExisteComp);
                            }
                            if (existeEdoCuenta) {
                                psEcCompDelete = conn.prepareStatement(queryEcCompDelete);
                                psEcCompDelete.setInt(1, nFolioCaja);
                                rsEcCompDelete = psEcCompDelete.executeUpdate();
                                if (rsEcCompDelete == 0)
                                    throw new Exception("No se Elimino la Comprobación del Estado de Cuenta para la solicitud: " + nFolioCaja);
                            }
                        } else if ("5".equals(componentesEvento[0]) && "2".equals(componentesEvento[2])) {
                            // Obtiene el folio de comprobacion
                            // Hacer update
                            while (rsComprobacion.next()) {
                                int folioComprobacion = rsComprobacion.getInt("nFolioComprobacion");
                                String queryUpdate = "UPDATE tcajaencabezado SET comprobado = NULL WHERE nFoliocaja = ?";
                                psUpdate = conn.prepareStatement(queryUpdate);
                                psUpdate.setInt(1, folioComprobacion);
                                rsu = psUpdate.execute();
                                if (rsu)
                                    throw new Exception("No se pudo enviar a NULL la solicitud " + nFolioCaja);
                            }
                        }
                    }
                }
            }
        } finally {
            CloseObject.closeObject(rsEventos, false);
            CloseObject.closeObject(rsComprobacion, false);
            CloseObject.closeObject(rsSI, false);
            CloseObject.closeObject(psEncabezado, false);
            CloseObject.closeObject(psEvento, false);
            CloseObject.closeObject(psUpdate, false);
            CloseObject.closeObject(psSI, false);
            CloseObject.closeObject(psSICancel, false);
            CloseObject.closeObject(psCajaNoSi, false);
            CloseObject.closeObject(psCajaDescartaComp, false);
            CloseObject.closeObject(psBonifDelete, false);
            CloseObject.closeObject(psEdoCtaDescarta, false);
            CloseObject.closeObject(psEcCompDelete, false);
        }
    }

    public static void actualizaRemanentesComprobacion(Connection conn, String nFolio) throws Exception {
        log.debug("Se Cancela un evento de Comprobacion se intentara actualizar el Remanente");
        PreparedStatement psSel = null;
        PreparedStatement psUpd = null, psUpd2 = null;
        ResultSet rs = null;
        ResultSet rs2 = null, rs3 = null;
        String query = "SELECT nfoliocaja FROM tBonificacion_Comision WITH(NOLOCK) WHERE nfoliocomprobacion=?";
        int nfoliocomprobacion = Integer.parseInt(nFolio), nfoliocaja = 0;
        float montoComprobacion = 0, montoRemanente = 0, montoPago = 0;
        try {
            psSel = conn.prepareStatement(query);
            psSel.setInt(1, nfoliocomprobacion);
            log.debug(psSel + "[" + nfoliocomprobacion + "]");
            rs = psSel.executeQuery();
            while (rs.next()) {
                nfoliocaja = rs.getInt("nfoliocaja");
                String query2 = "SELECT mMontoComprobacion FROM tEstadoDeCuentaComprobacionesDetalle WITH(NOLOCK) WHERE nFolioCaja=? AND nFolioComprobacion=?";
                psSel = conn.prepareStatement(query2);
                psSel.setInt(1, nfoliocaja);
                psSel.setInt(2, nfoliocomprobacion);
                log.debug("Intentando leer monto de la comprobacion del folio: " + nfoliocaja);
                log.debug(psSel + "[" + nfoliocaja + "]" + "[" + nfoliocomprobacion + "]");
                rs2 = psSel.executeQuery();
                if (rs2.next())
                    montoComprobacion = rs2.getFloat("mMontoComprobacion");
                else
                    throw new Exception("No se pudo obtener monto de la comprobacion de la solicitud " + nfoliocaja);
                query2 = "SELECT mMontoRemanente FROM tEstadoDeCuentaComprobacionesEncabezado WITH(NOLOCK) WHERE nFolioCaja = ?";
                psSel = conn.prepareStatement(query2);
                psSel.setInt(1, nfoliocaja);
                log.debug("Intentando leer monto remante del folio: " + nfoliocaja);
                log.debug(psSel + "[" + nfoliocaja + "]");
                rs3 = psSel.executeQuery();
                if (rs3.next())
                    montoRemanente = rs3.getFloat("mMontoRemanente");
                else
                    throw new Exception("No se pudo obtener el remanente de la solicitud " + nfoliocaja);
                montoRemanente = montoRemanente + montoComprobacion;
                query2 = "SELECT mMontoPago FROM tEstadoDeCuentaComprobacionesEncabezado WITH(NOLOCK) WHERE nFolioCaja =?";
                psSel = conn.prepareStatement(query2);
                psSel.setInt(1, nfoliocaja);
                log.debug("Intentando leer monto Pago del folio: " + nfoliocaja);
                log.debug(psSel + "[" + nfoliocaja + "]");
                rs2 = psSel.executeQuery();
                if (rs2.next())
                    montoPago = rs2.getFloat("mMontoPago");
                else
                    throw new Exception("No se pudo obtener el Monto de pago de la solicitud " + nfoliocaja);
                if (montoRemanente <= montoPago) {
                    query2 = "UPDATE tEstadoDeCuentaComprobacionesEncabezado set mMontoRemanente=? WHERE nFolioCaja=? ";
                    psUpd = conn.prepareStatement(query2);
                    psUpd.setFloat(1, montoRemanente);
                    psUpd.setInt(2, nfoliocaja);
                    log.debug("Actualizado remante de la solicitud: " + nfoliocaja);
                    log.debug(psUpd + "[" + montoRemanente + "]" + "[" + nfoliocaja + "]");
                    psUpd.executeUpdate();
                    query2 = "UPDATE tEstadoDeCuentaComprobacionesDetalle SET cDocumentoHaplicado='C' WHERE nFolioCaja=? AND nFolioComprobacion=?";
                    psUpd2 = conn.prepareStatement(query2);
                    psUpd2.setInt(1, nfoliocaja);
                    psUpd2.setInt(2, nfoliocomprobacion);
                    log.debug("Actualizado cDocumentoHaplicado='C' en tEstadoDeCuentaComprobacionesDetalle: " + nfoliocaja + "-" + nfoliocomprobacion);
                    log.debug(psUpd2 + "[" + nfoliocaja + "]" + "[" + nfoliocomprobacion + "]");
                    psUpd2.executeUpdate();
                } else {
                    throw new Exception("Problema al calcular el monto remanente de la Solicitud" + nfoliocaja);
                }
            }
        } finally {
            CloseObject.closeObject(psSel, false);
            CloseObject.closeObject(psUpd, false);
            CloseObject.closeObject(psUpd2, false);
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(rs2, false);
        }
    }
}
