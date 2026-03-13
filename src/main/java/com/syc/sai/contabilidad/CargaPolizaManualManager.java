package com.syc.sai.contabilidad;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.syc.contable.core.Poliza;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CargaPolizaManualManager {

    private static Logger log = LoggerFactory.getLogger(CargaPolizaManualManager.class);

    public static int insertaRenglonPol(Connection conn, Map<String, String> infoRenglon, int nRenglon) throws Exception {
        Statement stmnt = null;
        int r = 0;
        try {
            stmnt = conn.createStatement();
            log.info("Inicia Insert del renglon" + nRenglon);
            r = stmnt.executeUpdate(genInsertFromMap("tCargaPololizaPorLayout", infoRenglon));
            log.info("Se inserto " + r + " registros");
            return r;
        } finally {
            CloseObject.closeObject(stmnt, false);
        }
    }

    public static String genInsertFromMap(String tableName, Map<String, String> info) {
        String queryEncabezado = "INSERT INTO " + tableName + "(";
        String queryValores = "VALUES(";
        String tokenEncabezado = "";
        String tokenValores = "";
        int numeric;
        for (Iterator<String> i = info.keySet().iterator(); i.hasNext(); ) {
            String encabezado = i.next();
            queryEncabezado = queryEncabezado + tokenEncabezado + encabezado;
            //	log.info( info.get(encabezado));
            if (//|| "".equals(info.get(encabezado)) || info.get(encabezado)=="null" )
            encabezado == "nCargo" || encabezado == "nAbono" || encabezado == "nFolioDocPoliza" || encabezado == "nFolioPoliza" || encabezado == "nMes" || encabezado == "nFolioPolizaCancelacion" || encabezado == "nCambio" || encabezado == "nIdCasoOrigen" || encabezado == "nTipoAjuste" || encabezado == "nFormatoPoliza" || encabezado == "fCancelacion" || encabezado == "cIdUsuarioRevision" || encabezado == "sFirmanteRev" || encabezado == "sPuestoCap" || encabezado == "cDocumentoHaplicado" || encabezado == "cIdUsuarioAprobacion" || encabezado == "sFirmanteAut" || encabezado == "sFirmanteCap" || encabezado == "sPuestoRev") {
                queryValores = queryValores + tokenValores + info.get(encabezado);
            } else
                queryValores = queryValores + tokenValores + "'" + info.get(encabezado) + "'";
            tokenEncabezado = ", ";
            tokenValores = ", ";
        }
        log.info(queryEncabezado + ")" + queryValores + ")");
        return queryEncabezado + ")" + queryValores + ")";
    }

    public static String validaRenglon(Connection conn, String Cuenta, String SubCuenta, int renglon) throws Exception {
        String resp = "";
        resp = validaCuenta(conn, Cuenta, SubCuenta);
        if (resp != "ok") {
            resp = "Error en el renglon " + renglon + ", " + resp;
            log.info("Error en el renglon " + renglon + ", " + resp);
        }
        return resp;
    }

    // Fin de validar renglon
    public static String validaCuenta(Connection con, String Cuenta, String subCuenta) throws Exception {
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        String sql = "Select c.nCuenta, isnull(c.cSubCuenta,'NA')cSubCuenta from tCuentas c with(nolock) where AplicacionCuenta ='S' and not (TipoBalance='P' and TipoCuenta='P') and nCuenta ='" + Cuenta + "'";
        String rCuenta = "";
        String rTipoSubCuenta = " ";
        String resp = "";
        try {
            log.info("Inicia validacion de la cuenta: " + Cuenta);
            log.info("Ejectua query: " + sql);
            pstmnt = con.prepareStatement(sql);
            rs = pstmnt.executeQuery();
            while (rs.next()) {
                rCuenta = rs.getString("nCuenta");
                rTipoSubCuenta = rs.getString("cSubCuenta");
            }
            log.info("valor de tipo subcuenta: " + rTipoSubCuenta.toString());
            if (rCuenta.equals("")) {
                log.info("La cuenta:" + Cuenta + " no existe o no es de Aplicacion");
                resp = "Cuenta inexistente o no es de Aplicacion.";
            } else {
                if (subCuenta == "x") {
                    if (rTipoSubCuenta.equals("NA")) {
                        log.info("La cuenta no necesita subCuenta");
                        resp = "ok";
                    } else {
                        log.info("La cuenta necesita una subCuenta y no se capturó en el archivo");
                        resp = "La cuenta " + Cuenta + " necesita un " + rTipoSubCuenta + " y NO se capturó.";
                    }
                    //fin del segundo if
                } else //FIN DEL PRIMER if
                {
                    if (rTipoSubCuenta.equals("NA")) {
                        log.info("la cuenta NO necesita subCuenta y se CAPTURÓ en el archivo!!...");
                        resp = "La cuenta " + Cuenta + " NO necesita SubCuenta y se capturó en el archivo.";
                    } else {
                        log.info("La Cuenta necesita una subcuenta del tipo: " + rTipoSubCuenta + " validando SubCuenta");
                        //regresa la validacion de la subcuenta
                        resp = validaSubCuenta(con, subCuenta, rTipoSubCuenta);
                    }
                    //fin del if
                }
            }
            // fin del if principal
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            rs = null;
            pstmnt = null;
        }
        return resp;
    }

    // Fin de valida Cuenta
    public static String validaSubCuenta(Connection con, String subCuenta, String tipoSubCuenta) throws Exception {
        PreparedStatement pstm = null;
        ResultSet rs = null;
        String sql = "select *from vSubCuentas where dSubCuenta = '" + subCuenta + "' and cSubCuenta='" + tipoSubCuenta + "'";
        String rsubCuenta = "";
        String resp = "";
        try {
            log.info("Inicia Validacion de SubCuneta:" + subCuenta);
            log.info("Ejecuta query de subcuenta: " + sql);
            pstm = con.prepareStatement(sql);
            rs = pstm.executeQuery();
            while (rs.next()) {
                rsubCuenta = rs.getString("dSubCuenta");
            }
            if (rsubCuenta == "") {
                log.info("La SubCuenta:" + subCuenta + " no existe.");
                resp = "La SubCuenta no existe.";
            } else {
                log.info("SubCuenta Encontrada!!...");
                resp = "ok";
            }
            //fin del else principal
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (rs != null)
                rs.close();
            if (pstm != null)
                pstm.close();
            rs = null;
            pstm = null;
        }
        //fin finally
        return resp;
    }

    //fin del método
    //                                     METODOS PARA INSERTAR EN LA BITACORA EN EL ENCABEZADO Y DETALLE DE DOCPOLIZA CUANDO EL EXCEL NO TENGA ERRORES
    public static int genInsertFromMap(Connection conn, Map<String, String> infoRenglon, String table) throws Exception {
        Statement stmnt = null;
        int r = 0;
        try {
            stmnt = conn.createStatement();
            log.info("Inicia Insert en " + table);
            r = stmnt.executeUpdate(genInsertFromMap(table, infoRenglon));
            log.info("Se inserto " + r + " registros en " + table);
            return r;
        } finally {
            CloseObject.closeObject(stmnt, false);
        }
    }

    //fin del método inserta Bitacora
    public static String insertaPolizaDetalle(Connection con, String subCuenta, String tipoSubCuenta) throws Exception {
        PreparedStatement pstm = null;
        ResultSet rs = null;
        String sql = "select *from vSubCuentas where dSubCuenta = '" + subCuenta + "' and cSubCuenta='" + tipoSubCuenta + "'";
        String rsubCuenta = "";
        String resp = "";
        try {
            log.info("Inicia Validacion de SubCuneta:" + subCuenta);
            log.info("Ejecuta query de subcuenta: " + sql);
            pstm = con.prepareStatement(sql);
            rs = pstm.executeQuery();
            while (rs.next()) {
                rsubCuenta = rs.getString("dSubCuenta");
            }
            if (rsubCuenta == "") {
                log.info("La SubCuenta:" + subCuenta + " no existe.");
                resp = "La SubCuenta no existe.";
            } else {
                log.info("SubCuenta Encontrada!!...");
                resp = "ok";
            }
            //fin del else principal
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (rs != null)
                rs.close();
            if (pstm != null)
                pstm.close();
            rs = null;
            pstm = null;
        }
        //fin finally
        return resp;
    }

    //fin del método inserta Poliza Detalle
    @SuppressWarnings("rawtypes")
    public static List resultExcel(Connection conn, String foliodoc, String cCentroContable) throws SQLException {
        List polizaList = new ArrayList();
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        String sql = "select p.nCuenta,c.dCuenta,p.nSubCuenta,p.nCargo,p.nAbono from tCargaPololizaPorLayout p with(nolock)" + " inner join tCuentas c with(nolock) on p.nCuenta=c.nCuenta where p.cCentroContable='" + cCentroContable + "' and p.nFolioDocumento=" + foliodoc;
        try {
            pstmnt = conn.prepareStatement(sql);
            log.info("Ejecuta query: " + sql);
            rs = pstmnt.executeQuery();
            while (rs.next()) {
                Poliza cPoliza = new Poliza();
                cPoliza.setnCuenta(rs.getString("nCuenta"));
                cPoliza.setcDescripcionCuenta(rs.getString("dCuenta"));
                cPoliza.setcAuxiliar(rs.getString("nSubCuenta"));
                cPoliza.setcDescripcionMov(" ");
                cPoliza.setmCargo(rs.getString("nCargo"));
                cPoliza.setmAbono(rs.getString("nAbono"));
                cPoliza.setcGrupo(" ");
                cPoliza.setcSubGrupo(" ");
                cPoliza.setcEvento(" ");
                cPoliza.setcParcial("N");
                //va en blanco por que es captura por cuentas y este campo se llena cuando es captura por eventos
                cPoliza.setcPartida("");
                //solo se usa para polizas por eventos
                cPoliza.setcNumEvento(" ");
                polizaList.add(cPoliza);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            rs = null;
            pstmnt = null;
        }
        return polizaList;
    }

    //fin del metodo resultado excel
    public static void deleteEncabezado(Connection conn, String folioDocPoliza, String centroContable) throws SQLException {
        Statement pstmnt = null;
        //ResultSet rs = null;
        String sql = "delete from tdocpolizaEncabezado where cCentroContable='" + centroContable + "' and nfolioDocPoliza=" + folioDocPoliza;
        //primero se borra el detalle si es que existirta
        deleteDetalle(conn, folioDocPoliza);
        try {
            pstmnt = conn.createStatement();
            log.info("Ejecuta query: " + sql);
            pstmnt.executeUpdate(sql);
            conn.commit();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            //conn.close();
            pstmnt = null;
        }
    }

    public static void deleteDetalle(Connection conn, String folioDocPoliza) throws SQLException {
        Statement pstmnt = null;
        //ResultSet rs = null;
        String sql = "delete from tdocpolizaDetalle where  nfolioDocPoliza=" + folioDocPoliza;
        try {
            pstmnt = conn.createStatement();
            log.info("Ejecuta query: " + sql);
            //pstmnt.executeQuery(sql);
            pstmnt.executeUpdate(sql);
            conn.commit();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        /*
		 * 
		 		Statement stmnt = null;
		int r = 0;
		try {
			
			stmnt = conn.createStatement();
			
			log.info("Inicia Insert en "+table);
			r = stmnt.executeUpdate(genInsertFromMap(table, infoRenglon));		
		 */
    }
}
