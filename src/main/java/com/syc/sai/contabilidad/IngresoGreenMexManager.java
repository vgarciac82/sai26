package com.syc.sai.contabilidad;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.axtel.egresos.exceptions.EgresoException;
import com.syc.contable.AccountingEngine;
import com.syc.contable.AccountingEngineException;
import com.syc.contable.DocumentAppliedException;
import com.syc.gestion.core.CFSequenceManager;
import com.syc.gestion.core.Caso;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IngresoGreenMexManager {

    private static final String CONCEPTO_POLIZA = "Poliza de Ingreso por el reconocimiento del gasto del proyecto GREENMEX";

    private static final String TIPO_POLIZA = "DI";

    private static final String RFC = "GREENMEX";

    private static final String EVENTO = "IP_GM";

    private static final String CRI = "79900/GREENMEX";

    private static final String CRIR = "79900/CNF010405EG1";

    private static final Logger log = LoggerFactory.getLogger(IngresoGreenMexManager.class);

    public static boolean aplicarIngresoGreenMex(Connection conn, String documento, String nFolio, String tablaEncabezado, String tablaDetalle, String campo) throws EgresoException {
        boolean exito = true;
        PreparedStatement psEncabezado = null;
        PreparedStatement psDetalle = null, psDetalleR = null;
        try {
            CFSequenceManager seq = CFSequenceManager.getInstance(GestionInterface.ATT_CONEXION);
            int nFolioIngresoGreenMex = seq.nextVal("INGRESOGREENMEX");
            String queryInsertaEncabezado = "INSERT INTO tIngresoGreenMexEncabezado(nFolioIngresoGreenMex, cTipoPago, nFolioPago, fAplicacion, cRamo, caNoContrarrecibo, aEjercicioFiscal, cTipoPoliza, cUnidadResponsableContable, cDescripcionPoliza, cIdUsuarioCaptura,cUnidadResponsable)" + "SELECT " + nFolioIngresoGreenMex + ", '" + documento + "', " + nFolio + ", fAplicacion, cRamo, caNoContrarrecibo, aEjercicioFiscal, '" + TIPO_POLIZA + "', cUnidadResponsableContable, '" + CONCEPTO_POLIZA + "', cIdUsuarioCaptura, cUnidadResponsable" + "  FROM " + tablaEncabezado + " WITH(NOLOCK) " + " WHERE " + campo + " = " + nFolio;
            String queryInsertaDetalle = "INSERT INTO tIngresoGreenMexDetalle(nFolioIngresoGreenMex, nDocRenglon, cMes, cEvento, cEjercicio, cCentroContable, EP, mImporte, RFC, cUnidadResponsable, CRI, mImporteMod)" + "SELECT " + nFolioIngresoGreenMex + ", nDocRenglon, cMes, '" + EVENTO + "', cEjercicio, cCentroContable, EP, mImporteMasIva, '" + RFC + "'" + ", cUnidadResponsable, '" + CRI + "', 0.00 FROM " + tablaDetalle + " WITH(NOLOCK) " + " WHERE " + campo + " = " + nFolio;
            String queryInsertaDetalleR = "INSERT INTO tIngresoGreenMexDetalle(nFolioIngresoGreenMex, nDocRenglon, cMes, cEvento, cEjercicio, cCentroContable, EP, mImporte, RFC, cUnidadResponsable, CRI, mImporteMod)" + "SELECT " + nFolioIngresoGreenMex + ", nDocRenglon + (SELECT COUNT(nFolioIngresoGreenMex) FROM tIngresoGreenMexDetalle WITH (NOLOCK) WHERE nFolioIngresoGreenMex = " + nFolioIngresoGreenMex + "), cMes, '" + EVENTO + "', cEjercicio, cCentroContable, EP, 0, '" + RFC + "'" + ", cUnidadResponsable, '" + CRIR + "', mImporte FROM tIngresoGreenMexDetalle WITH(NOLOCK) " + " WHERE nFolioIngresoGreenMex = " + nFolioIngresoGreenMex;
            psEncabezado = conn.prepareStatement(queryInsertaEncabezado);
            psDetalle = conn.prepareStatement(queryInsertaDetalle);
            psDetalleR = conn.prepareStatement(queryInsertaDetalleR);
            int insertados = psEncabezado.executeUpdate();
            insertados += psDetalle.executeUpdate();
            insertados += psDetalleR.executeUpdate();
            log.debug("Object: {}", "Se insertaron " + insertados + " campos para aplicar el Ingreso para GREENMEX ");
            log.info("Object: {}", " Aplicando motor para el Ingreso para GREENMEX " + nFolioIngresoGreenMex);
            AccountingEngine accEng = new AccountingEngine();
            accEng.setValidaInsuficienciaDeSaldo(true);
            accEng.makeAccountingApplication(conn, "INGRESOGREENMEX", String.valueOf(nFolioIngresoGreenMex), "tIngresoGreenMexEncabezado", "tIngresoGreenMexDetalle", "nFolioIngresoGreenMex");
            return exito;
        } catch (SQLException | DocumentAppliedException | AccountingEngineException e) {
            throw new EgresoException("Problemas aplicando el Ingreso para GREENMEX: " + e, e);
        } finally {
            CloseObject.closeObject(psEncabezado);
            CloseObject.closeObject(psDetalle);
        }
    }

    public static void cancelarIngresoGreenMex(Connection conn, String documento, String nFolioDocumento) throws Exception {
        try {
            int nFolioIngresoGreenMex = getFolioIngresoGreenMex(conn, documento, Integer.parseInt(nFolioDocumento));
            if (nFolioIngresoGreenMex > 0) {
                AccountingEngine accEng = new AccountingEngine();
                accEng.setValidaInsuficienciaDeSaldo(true);
                actualizaEstadoCuenta(conn, nFolioDocumento);
                accEng.cancelAccountingApplication(conn, "INGRESOGREENMEX", String.valueOf(nFolioIngresoGreenMex), "tIngresoGreenMexEncabezado", "tIngresoGreenMexDetalle", "nFolioIngresoGreenMex");
            }
        } catch (AccountingEngineException e) {
            if (e.toString().indexOf("sido cancelado") < 0)
                throw e;
        }
    }

    private static void actualizaEstadoCuenta(Connection conn, String nFolioDocumento) throws Exception {
        PreparedStatement ps = null, ps2 = null, ps3 = null;
        ResultSet rs = null;
        String query = "";
        String query2 = "";
        try {
            query = "UPDATE ENC SET  mMontoRemanente = mMontoRemanente + mMontoComprobacion\r\n" + "FROM tEstadoDeCuentaGreenMexEncabezado ENC\r\n" + "INNER JOIN tEstadoDeCuentaGreenMexDetalle DET\r\n" + "ON ENC.nFolioCaja = DET.nFolioCaja\r\n" + "WHERE nFolioComprobacion = ?";
            query2 = "DELETE tEstadoDeCuentaGreenMexDetalle WHERE nFolioComprobacion = ?";
            ps = conn.prepareStatement(query);
            ps.setInt(1, Integer.parseInt(nFolioDocumento));
            ps.execute();
            ps2 = conn.prepareStatement(query2);
            ps2.setInt(1, Integer.parseInt(nFolioDocumento));
            ps2.execute();
            log.info("Se actualizo el estado de cuenta.");
        } finally {
            CloseObject.closeObject(ps);
            CloseObject.closeObject(ps2);
            CloseObject.closeObject(ps3);
            CloseObject.closeObject(rs);
        }
    }

    private static int getFolioIngresoGreenMex(Connection conn, String documento, int nFolioDocumento) throws Exception {
        String query = "SELECT nFolioIngresoGreenMex  FROM tIngresoGreenMexEncabezado WITH(NOLOCK) WHERE cTipoPago = ? AND nFolioPago = ?";
        PreparedStatement ps = null;
        ResultSet rs = null;
        int nFolioIngresoGreenMex = -1;
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, documento);
            ps.setInt(2, nFolioDocumento);
            rs = ps.executeQuery();
            if (rs.next())
                nFolioIngresoGreenMex = rs.getInt(1);
            return nFolioIngresoGreenMex;
        } catch (SQLException e) {
            throw new EgresoException(e);
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public static void deleteEstadoCuentaDetalle(Connection conn, Caso c) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "";
        try {
            int nFolioDocumento = Integer.parseInt(c.getFolio().substring(c.getFolio().lastIndexOf("-") + 1));
            query = "DELETE tEstadoDeCuentaGreenMexDetalle WHERE nFolioComprobacion = ?";
            ps = conn.prepareStatement(query);
            ps.setInt(1, nFolioDocumento);
            ps.execute();
            log.info("Se actualizo el estado de cuenta.");
        } finally {
            CloseObject.closeObject(ps);
            CloseObject.closeObject(rs);
        }
    }
}
