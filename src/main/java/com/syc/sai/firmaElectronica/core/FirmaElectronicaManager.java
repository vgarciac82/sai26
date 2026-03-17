package com.syc.sai.firmaElectronica.core;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import com.axtel.egresos.viaticos.Agenda;
import com.axtel.egresos.viaticos.ViaticosBusinessLogic;
import com.axtel.egresos.viaticos.core.AgendaDAO;
import com.axtel.egresos.viaticos.core.ComisionDAO;
import com.axtel.egresos.viaticos.core.GeneraSolicitudViaticos;
import com.syc.egresos.core.EgresoEncabezado;
import com.syc.fortimax.core.Carpeta;
import com.syc.fortimax.core.CarpetaManager;
import com.syc.fortimax.core.DocumentoManager;
import com.syc.fortimax.core.Volumen;
import com.syc.fortimax.core.VolumenManager;
import com.syc.fortimax.exceptions.FortimaxException;
import com.syc.gestion.reportes.ConciliacionFirma;
import com.syc.gestion.reportes.EstadosFinancierosFirma;
import com.syc.gestion.reportes.FirmaElectronicaReporte;
import com.syc.gestion.servlet.GestionServlet;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import com.syc.sai.firmaElectronica.FirmaElectronicaBusinessLogic;
import com.syc.sai.firmaElectronica.FirmaElectronicaBusinessLogic.TramiteSolicitud;
import com.syc.sai.firmaElectronica.exceptions.FirmaElectronicaException;
import com.syc.sai.firmaElectronica.interfaces.SolicitudFirmaElectronica;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class FirmaElectronicaManager {

    private static final Logger log = LoggerFactory.getLogger(FirmaElectronicaManager.class);

    public static final Map<String, String> REPORTES_SOL_PAGO = new HashMap<String, String>();

    private static final StringBuilder queryExistsQuestionnaire = new StringBuilder("SELECT COUNT(*) AS existe FROM tCuestionarioPagosFIEL WITH(NOLOCK) WHERE cTipoPago = ? and nFolioPago = ?");

    static {
        REPORTES_SOL_PAGO.put("COMSINVIATICOS_FIEL", "PolizaInformeComision_FIEL.jasper");
        REPORTES_SOL_PAGO.put("ACUSE_COMSINVIATICOS_FIEL", "AcusePolizaInformeComision_FIEL.jasper");
        REPORTES_SOL_PAGO.put("CAJA_FIEL", "PolizaCaja_FIEL.jasper");
        REPORTES_SOL_PAGO.put("ACUSE_CAJA_FIEL", "PolizaCaja_FIEL.jasper");
        REPORTES_SOL_PAGO.put("POLIZA_FIEL", "PolizaManual_Fiel.jasper");
        REPORTES_SOL_PAGO.put("ACUSE_POLIZA_FIEL", "Acuse_PolizaManual_Fiel.jasper");
        REPORTES_SOL_PAGO.put("PAGODIVERSO_FIEL", "PolizaPagoN_FIEL.jasper");
        REPORTES_SOL_PAGO.put("ANEXO_PAGODIVERSO_FIEL", "Anexo1.jasper");
        REPORTES_SOL_PAGO.put("ACUSE_PAGODIVERSO_FIEL", "AcusePolizaPagoN_FIEL.jasper");
        REPORTES_SOL_PAGO.put("PAGOFEDERALIZADO_FIEL", "PolizaPagoN_FIEL.jasper");
        REPORTES_SOL_PAGO.put("ANEXO_PAGOFEDERALIZADO_FIEL", "Anexo1.jasper");
        REPORTES_SOL_PAGO.put("ACUSE_PAGOFEDERALIZADO_FIEL", "AcusePolizaPagoN_FIEL.jasper");
        REPORTES_SOL_PAGO.put("RELACIONGASTOS_FIEL", "PolizaPago_FIEL.jasper");
        REPORTES_SOL_PAGO.put("ANEXO_RELACIONGASTOS_FIEL", "Anexo1.jasper");
        REPORTES_SOL_PAGO.put("ACUSE_RELACIONGASTOS_FIEL", "AcusePolizaPago_FIEL.jasper");
        REPORTES_SOL_PAGO.put("PAGODIRECTO_FIEL", "PolizaPagoNuevoN_FIEL.jasper");
        REPORTES_SOL_PAGO.put("ANEXO_PAGODIRECTO_FIEL", "Anexo1.jasper");
        REPORTES_SOL_PAGO.put("ACUSE_PAGODIRECTO_FIEL", "AcusePolizaPagoNuevoN_FIEL.jasper");
        REPORTES_SOL_PAGO.put("PAGODIRECTO_15D_FIEL", "PolizaPagoN_15D_FIEL.jasper");
        REPORTES_SOL_PAGO.put("ANEXO_PAGODIRECTO_15D_FIEL", "Anexo1.jasper");
        REPORTES_SOL_PAGO.put("ACUSE_PAGODIRECTO_15D_FIEL", "PolizaPagoN_15D_FIEL.jasper");
        REPORTES_SOL_PAGO.put("PAGOOBRA_FIEL", "PolizaPagoN_FIEL.jasper");
        REPORTES_SOL_PAGO.put("ANEXO_PAGOOBRA_FIEL", "Anexo1.jasper");
        REPORTES_SOL_PAGO.put("ACUSE_PAGOOBRA_FIEL", "AcusePolizaPagoN_FIEL.jasper");
        REPORTES_SOL_PAGO.put("PAGOOBRA_FIEL", "PolizaPagoN_FIEL.jasper");
        REPORTES_SOL_PAGO.put("ANEXO_PAGOOBRA_FIEL", "Anexo1.jasper");
        REPORTES_SOL_PAGO.put("ACUSE_PAGOOBRA_FIEL", "AcusePolizaPagoN_FIEL.jasper");
        REPORTES_SOL_PAGO.put("OPERAJENAS_FIEL", "PolizaOperAjenas_FIEL.jasper");
        REPORTES_SOL_PAGO.put("ACUSE_OPERAJENAS_FIEL", "AcusePolizaOperAjenas_FIEL.jasper");
        REPORTES_SOL_PAGO.put("ANEXO_OPERAJENAS_FIEL", "Anexo1.jasper");
        REPORTES_SOL_PAGO.put("REINTEGROCAJA_FIEL", "PolizaReintegroCaja_FIEL.jasper");
        REPORTES_SOL_PAGO.put("ACUSE_REINTEGROCAJA_FIEL", "AcuseReintegroCaja_FIEL.jasper");
    }

    public static void actualizaMetodoAutorizacion(Connection conn, String tablaEnc, String campo, String valor, boolean firmaElectronica) throws Exception {
        StringBuilder query = new StringBuilder();
        query.append("UPDATE ");
        query.append(tablaEnc);
        query.append(" WITH(ROWLOCK) ");
        query.append(" SET cEsFirmaElectronica = '");
        query.append((firmaElectronica ? "S" : "N"));
        query.append("' WHERE ");
        query.append(campo);
        query.append(" = ");
        query.append(valor);
        Statement stmnt = null;
        try {
            stmnt = conn.createStatement();
            int actualizados = stmnt.executeUpdate(query.toString());
            log.info("Object: {}", "Se actualizaron " + actualizados + " registros en la tabla: " + tablaEnc + " con las condiciones: " + campo + "=" + valor);
        } finally {
            CloseObject.closeObject(stmnt);
        }
    }

    public static boolean avanzaEstatusReporte(Connection conn, FirmaElectronicaReporte fer) throws Exception {
        return avanzaEstatusReporte(conn, fer, "F");
    }

    public static boolean avanzaEstatusReporte(Connection conn, FirmaElectronicaReporte fer, String status) throws Exception {
        StringBuilder query = new StringBuilder();
        query.append("UPDATE	tFirmanteReporte ");
        query.append("   SET	cEstatus = ? ");
        query.append(" WHERE	nIDEdoFinanciero = ?");
        query.append("   AND	nOrden = ?");
        PreparedStatement psUpdate = null;
        ResultSet rs = null;
        try {
            psUpdate = conn.prepareStatement(query.toString());
            psUpdate.setString(1, status);
            psUpdate.setInt(2, ((EstadosFinancierosFirma) fer).getIdEstadoFinanciero());
            psUpdate.setInt(3, fer.getOrden());
            psUpdate.executeUpdate();
            boolean firmanteReporteSiguiente = firmanteReporteSiguiente(conn, fer);
            if (!firmanteReporteSiguiente)
                terminaFirmaReporte(conn, fer);
            return firmanteReporteSiguiente;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(psUpdate);
        }
    }

    public static boolean avanzaEstatusConciliacion(Connection conn, FirmaElectronicaReporte fer) throws Exception {
        StringBuilder query = new StringBuilder();
        query.append("UPDATE	tFirmanteConciliacion ");
        query.append("   SET	cEstatus = 'F' ");
        query.append(" WHERE	nConciliacion = ?");
        query.append("   AND	nOrden = ?");
        PreparedStatement psUpdate = null;
        ResultSet rs = null;
        try {
            psUpdate = conn.prepareStatement(query.toString());
            psUpdate.setInt(1, fer.getIdField());
            psUpdate.setInt(2, fer.getOrden());
            psUpdate.executeUpdate();
            boolean firmanteReporteSiguiente = firmanteReporteSiguiente(conn, fer);
            if (!firmanteReporteSiguiente)
                terminaFirmaReporte(conn, fer);
            return firmanteReporteSiguiente;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(psUpdate);
        }
    }

    public static boolean avanzaEstatusRM(Connection conn, RecepcionMaterialFIEL rmfiel) throws Exception {
        return avanzaEstatusRM(conn, rmfiel, SolicitudFirmaElectronica.ESTATUS_RM_EMITIDA);
    }

    private static boolean avanzaEstatusRM(Connection conn, RecepcionMaterialFIEL rmfiel, int estatusRM) throws SQLException {
        log.info("Object: {}", "Actualizando RM: " + rmfiel.getRecepcionMaterial());
        StringBuilder query = new StringBuilder();
        query.append("UPDATE	mrecepcionpmat ");
        query.append("  SET	nIdEstadoRecepMat = ? ");
        query.append(" WHERE	cIdpedContDef = ? ");
        query.append("   AND	cIdRecepMat = ?");
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, estatusRM);
            ps.setString(2, rmfiel.getRecepcionMaterial().getcIdPedContDef());
            ps.setString(3, rmfiel.getRecepcionMaterial().getcIdRecepcionMat());
            log.debug("Object: " + String.valueOf("Ejecutando: " + query.toString() + " [" + rmfiel.getRecepcionMaterial().getcIdPedContDef() + "][" + rmfiel.getRecepcionMaterial().getcIdRecepcionMat() + "]"));
            int actualizados = ps.executeUpdate();
            log.debug("Object: " + String.valueOf("Se actualizaron: " + actualizados + " registros."));
            return true;
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static boolean firmaAtentaNotaRM(Connection conn, RecepcionMaterialFIEL rmfiel) throws Exception {
        return firmaAtentaNotaRM(conn, rmfiel, SolicitudFirmaElectronica.ESTATUS_ATENTA_NOTA_FIRMADA);
    }

    private static boolean firmaAtentaNotaRM(Connection conn, RecepcionMaterialFIEL rmfiel, int estatusFirmaAtentaNotaRM) throws SQLException {
        log.info("Object: {}", "Actualizando RM: " + rmfiel.getRecepcionMaterial());
        StringBuilder query = new StringBuilder();
        query.append("update tnotaautorizarm set nIdEstatusAntentaNotaFirmada=? where cIdContratoDefinitivo=? and cIDRecepMat=? AND nIDNota=? ");
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, estatusFirmaAtentaNotaRM);
            ps.setString(2, rmfiel.getRecepcionMaterial().getcIdPedContDef());
            ps.setString(3, rmfiel.getRecepcionMaterial().getcIdRecepcionMat());
            ps.setInt(4, rmfiel.getRecepcionMaterial().getIdNota());
            log.debug("Object: " + String.valueOf("Ejecutando: " + query.toString() + " [" + rmfiel.getRecepcionMaterial().getcIdPedContDef() + "][" + rmfiel.getRecepcionMaterial().getcIdRecepcionMat() + "]" + "[" + rmfiel.getRecepcionMaterial().getIdNota() + "]"));
            int actualizados = ps.executeUpdate();
            log.debug("Object: " + String.valueOf("Se actualizaron: " + actualizados + " registros."));
            return true;
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static int avanzaEstatusSICOP(Connection conn, SolicitudFirmaElectronica solicitudFirmaElectronica, int nEstatusSICOP) throws SQLException {
        StringBuilder query = new StringBuilder();
        query.append(" UPDATE ");
        query.append(solicitudFirmaElectronica.getHeader());
        query.append(" set nEnviadoSICOP = ? WHERE ");
        query.append(solicitudFirmaElectronica.getField());
        query.append(" = ?");
        PreparedStatement ps = null;
        int afectados = 0;
        try {
            log.trace("Object: {}", query.toString());
            log.trace("Object: {}", solicitudFirmaElectronica.getHeader());
            log.trace("Object: {}", solicitudFirmaElectronica.getField());
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, nEstatusSICOP);
            ps.setInt(2, solicitudFirmaElectronica.getIdField());
            afectados = ps.executeUpdate();
            return afectados;
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static int avanzaEstatusSICOP(Connection conn, String table, String pkField, int pkValue, int nEstatusSICOP) throws Exception {
        StringBuilder query = new StringBuilder();
        query.append(" UPDATE ");
        query.append(table);
        query.append(" set nEnviadoSICOP = ? WHERE ");
        query.append(pkField).append(" = ?");
        PreparedStatement ps = null;
        int afectados = 0;
        try {
            log.trace("Object: {}", query.toString());
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, nEstatusSICOP);
            ps.setInt(2, pkValue);
            afectados = ps.executeUpdate();
            return afectados;
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static int avanzaEstatusSICOP(Connection conn, String table, String pkField, String pkValue, int nEstatusSICOP) throws Exception {
        StringBuilder query = new StringBuilder();
        query.append(" UPDATE ");
        query.append(table);
        query.append(" set nEnviadoSICOP = ? WHERE ");
        query.append(pkField).append(" = ?");
        PreparedStatement ps = null;
        int afectados = 0;
        try {
            log.trace("Object: {}", query.toString());
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, nEstatusSICOP);
            ps.setString(2, pkValue);
            afectados = ps.executeUpdate();
            return afectados;
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static void avanzaEstatusSICOPMasivo(Connection conn, SolicitudFirmaElectronica solicitudPagoPrinter, int estatusSICOP) throws Exception {
        StringBuilder sbUpdateHeader = new StringBuilder();
        sbUpdateHeader.append("UPDATE	").append(solicitudPagoPrinter.getMasiveHeader()).append(" WITH(ROWLOCK) ");
        sbUpdateHeader.append("   SET	nEnviadoSICOP = ?, ");
        sbUpdateHeader.append("      	cEsFirmaElectronica = 'S' ");
        sbUpdateHeader.append(" WHERE	").append(solicitudPagoPrinter.getMasiveField()).append(" = ?");
        log.trace("Object: {}", "Query update encabezado masivo: " + sbUpdateHeader);
        StringBuilder sbUpdate = new StringBuilder();
        sbUpdate.append("UPDATE ").append(solicitudPagoPrinter.getHeader()).append(" WITH(ROWLOCK) ");
        sbUpdate.append("   SET	nEnviadoSICOP = ?, ");
        sbUpdate.append("      	cEsFirmaElectronica = 'S' ");
        sbUpdate.append(" WHERE	nFolioCargaMasiva = ? ");
        log.trace("Object: {}", "Query update integradas masivo: " + sbUpdate);
        PreparedStatement psUpdateHeader = null;
        PreparedStatement psUpdate = null;
        try {
            psUpdateHeader = conn.prepareStatement(sbUpdateHeader.toString());
            psUpdate = conn.prepareStatement(sbUpdate.toString());
            psUpdateHeader.setInt(1, estatusSICOP);
            psUpdateHeader.setInt(2, solicitudPagoPrinter.getMasiveID());
            psUpdate.setInt(1, estatusSICOP);
            psUpdate.setInt(2, solicitudPagoPrinter.getMasiveID());
            psUpdateHeader.executeUpdate();
            psUpdate.execute();
        } finally {
            CloseObject.closeObject(psUpdate);
            CloseObject.closeObject(psUpdateHeader);
        }
    }

    public static void cancelaFirmaReporte(Connection conn, FirmaElectronicaReporte fer) throws Exception {
        StringBuilder query = new StringBuilder();
        query.append("UPDATE	tEdoFinancieroFirmaElectronica ");
        query.append("   SET	cEstatus = '").append(FirmaElectronicaReporte.REPORTE_CANCELADO).append("' ");
        query.append(" WHERE	nIDEdoFinanciero = ? ");
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, fer.getIdField());
            ps.executeUpdate();
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static void cancelaFirmaConciliacion(Connection conn, int folio) throws Exception {
        StringBuilder query = new StringBuilder();
        query.append("UPDATE	tConciliacionFirmaElectronica ");
        query.append("   SET	cEstatus = 'C' ");
        query.append(" WHERE	nConciliacion = ? ");
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, folio);
            ps.executeUpdate();
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static void regresaConciliacion(Connection conn, int folio) throws Exception {
        StringBuilder query = new StringBuilder();
        query.append("UPDATE	tConciliacion ");
        query.append("   SET	cEsFirmaElectronica = 'N',  nEstatus = 2 ");
        query.append(" WHERE	nConciliacion = ? ");
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, folio);
            ps.executeUpdate();
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    /**
     * Cambia el estatus de una recepcion de material a cancelada.
     *
     * @param conn
     *            Conexion activa a base datos
     * @param fer
     *            Recepcion de material a cancelar.
     * @throws SQLException
     */
    public static void cancelaRecepcionMaterial(Connection conn, RecepcionMaterialFIEL fer) throws SQLException {
        StringBuilder query = new StringBuilder();
        query.append("UPDATE	mRecepcionpMat ");
        query.append("   SET	nidestadorecepmat = ").append(SolicitudFirmaElectronica.ESTATUS_RM_CANCELADA).append(" ");
        query.append(" WHERE	cIdRecepMat = ? ");
        query.append("   AND	cIdpedContDef = ? ");
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, fer.getRecepcionMaterial().getcIdRecepcionMat());
            ps.setString(2, fer.getRecepcionMaterial().getcIdPedContDef());
            int afectados = ps.executeUpdate();
            log.info("Object: {}", "Cancelada " + afectados + " nota de recepcion de material: " + fer.getRecepcionMaterial());
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static void autVoBoRecepcionMaterial(Connection conn, RecepcionMaterialFIEL fer) throws SQLException {
        StringBuilder query = new StringBuilder();
        query.append("UPDATE	mRecepcionpMat ");
        query.append("   SET	nidestadorecepmat = ").append(SolicitudFirmaElectronica.ESTATUS_RM_EMITIDA).append(" ");
        query.append(" WHERE	cIdRecepMat = ? ");
        query.append("   AND	cIdpedContDef = ? ");
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, fer.getRecepcionMaterial().getcIdRecepcionMat());
            ps.setString(2, fer.getRecepcionMaterial().getcIdPedContDef());
            int afectados = ps.executeUpdate();
            log.info("Object: {}", "Visto bueno " + afectados + " nota de recepcion de material: " + fer.getRecepcionMaterial());
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static boolean cargaInformacionDelegatorioAut(Connection conn, SolicitudFirmaElectronica solicitudFirmaElectronica) throws Exception {
        StringBuilder query = new StringBuilder();
        query.append("SELECT	cFolioOficio AS folioOficioAut, ");
        query.append("         dFechaOficio AS fechaOficioAut, ");
        query.append("         cTipoSuplencia AS tipoSuplenciaAut, ");
        query.append("         NOMBREN AS nombre, ");
        query.append("         NOMBREP AS ApellidoPaterno, ");
        query.append("         NOMBREM AS ApellidoMaterno ");
        query.append("  FROM	tPagoFirmanteDelagatorio firmanteAut ");
        query.append("			INNER JOIN ");
        query.append("			tCatTipoSuplencia tipoSuplencia ");
        query.append("			ON firmanteAut.nTipoSuplencia = tipoSuplencia.nTipoSuplencia ");
        query.append("			INNER JOIN v_empleados_giro empleados ");
        query.append("			ON ");
        query.append("			firmanteAut.cNumeroEmpleado = empleados.CLAVE ");
        query.append(" WHERE	cTipoPago = ? ");
        query.append("   AND	nFolioPago =  ?");
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, solicitudFirmaElectronica.getDocument());
            ps.setInt(2, solicitudFirmaElectronica.getIdField());
            rs = ps.executeQuery();
            if (rs.next()) {
                solicitudFirmaElectronica.setFolioOficioAut(rs.getString("folioOficioAut"));
                solicitudFirmaElectronica.setFechaOficioAut(Util.parseSQLDate(rs.getDate("fechaOficioAut")));
                solicitudFirmaElectronica.setTipoSuplenciaAut(rs.getString("tipoSuplenciaAut"));
                solicitudFirmaElectronica.setNombreEmpleadoSuplidoAut(solicitudFirmaElectronica.getAutNombreOriginal(conn));
                return true;
            } else
                return false;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public static boolean cargaInformacionDelegatorioVoBo(Connection conn, SolicitudFirmaElectronica solicitudFirmaElectronica) throws Exception {
        StringBuilder query = new StringBuilder();
        query.append("SELECT	cFolioOficio AS folioOficioVoBo, ");
        query.append("         dFechaOficio AS fechaOficio, ");
        query.append("         cTipoSuplencia AS tipoSuplencia, ");
        query.append("         NOMBREN AS nombre, ");
        query.append("         NOMBREP AS ApellidoPaterno, ");
        query.append("         NOMBREM AS ApellidoMaterno ");
        query.append("  FROM	tPagoFirmanteDelegatorioVoBo firmanteVoBo ");
        query.append("		INNER JOIN  ");
        query.append("		tCatTipoSuplencia tipoSuplencia ");
        query.append("		ON firmanteVoBo.nTipoSuplencia = tipoSuplencia.nTipoSuplencia ");
        query.append("		INNER JOIN v_empleados_giro empleados ");
        query.append("		ON ");
        query.append("		firmanteVoBo.cNumeroEmpleado = empleados.CLAVE ");
        query.append(" WHERE	cTipoPago = ? ");
        query.append("   AND	nFolioPago =  ?");
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, solicitudFirmaElectronica.getDocument());
            ps.setInt(2, solicitudFirmaElectronica.getIdField());
            rs = ps.executeQuery();
            if (rs.next()) {
                solicitudFirmaElectronica.setFolioOficioVoBo(rs.getString("folioOficioVoBo"));
                solicitudFirmaElectronica.setFechaOficioVoBo(Util.parseSQLDate(rs.getDate("fechaOficio")));
                solicitudFirmaElectronica.setTipoSuplencia(rs.getString("tipoSuplencia"));
                solicitudFirmaElectronica.setNombreEmpleadoSuplido(solicitudFirmaElectronica.getVoBoNombreOriginal(conn));
                return true;
            } else
                return false;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public static TramiteSolicitud cargaInformacionTramite(Connection conn, FirmaElectronicaBusinessLogic firmaElectronicaBL, String nombreTramite) throws Exception {
        TramiteSolicitud solicitud = null;
        StringBuilder query = new StringBuilder();
        query.append("SELECT	idTipoCaso, ");
        query.append("         cTramite, ");
        query.append("         cClaseImplementa, ");
        query.append("         cDocumentoFirmar ");
        query.append("  FROM	tTramiteImplemetaFIEL WITH(NOLOCK) ");
        query.append(" WHERE	cTramite = ?");
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, nombreTramite);
            rs = ps.executeQuery();
            if (rs.next()) {
                solicitud = firmaElectronicaBL.new TramiteSolicitud();
                solicitud.setClaseImplementa(rs.getString("cClaseImplementa"));
                solicitud.setDocumentoFirmar(rs.getString("cDocumentoFirmar"));
                solicitud.setIdTipoCaso(rs.getInt("idTipoCaso"));
                solicitud.setTramite(rs.getString("cTramite"));
            }
            return solicitud;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public static void estatusCancelaReporte(Connection conn, FirmaElectronicaReporte fer) throws Exception {
        StringBuilder query = new StringBuilder();
        query.append("UPDATE	tFirmanteReporte ");
        query.append("   SET	cEstatus = 'C' ");
        query.append(" WHERE	nIDEdoFinanciero = ?");
        query.append("   AND	nOrden = ?");
        PreparedStatement psUpdate = null;
        ResultSet rs = null;
        try {
            psUpdate = conn.prepareStatement(query.toString());
            psUpdate.setInt(1, ((EstadosFinancierosFirma) fer).getIdEstadoFinanciero());
            psUpdate.setInt(2, fer.getOrden());
            psUpdate.executeUpdate();
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(psUpdate);
        }
    }

    public static void estatusCancelaConciliacion(Connection conn, ConciliacionFirma fer) throws Exception {
        PreparedStatement psUpdate = null;
        ResultSet rs = null;
        StringBuilder query = new StringBuilder();
        query.append("UPDATE	tFirmanteConciliacion ");
        query.append("   SET	cEstatus = 'C' ");
        query.append(" WHERE	nConciliacion = ?");
        try {
            psUpdate = conn.prepareStatement(query.toString());
            psUpdate.setInt(1, fer.getIdConciliacion());
            psUpdate.executeUpdate();
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(psUpdate);
        }
    }

    public static boolean firmanteReporteSiguiente(Connection conn, FirmaElectronicaReporte fer) throws SQLException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT	COUNT(*) ");
        query.append("  FROM	tFirmanteReporte FIRMANTE ");
        query.append(" WHERE	nIDEdoFinanciero = ? ");
        query.append("   AND	cEstatus='E'");
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, fer.getIdField());
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            } else
                return false;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public static boolean siguienteFirmanteConciliacion(Connection conn, FirmaElectronicaReporte fer) throws SQLException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT	COUNT(*) ");
        query.append("  FROM	tFirmanteConciliacion FIRMANTE ");
        query.append(" WHERE	nConciliacion = ? ");
        query.append("   AND	cEstatus='E'");
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, fer.getIdField());
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            } else
                return false;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public static String generaArchivoFirma(Connection conn, SolicitudFirmaElectronica printer, String folderName, boolean isSignedCopy) throws FirmaElectronicaException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String filename = "";
        StringBuilder query = new StringBuilder();
        query.append("SELECT ID_GABINETE FROM imx");
        query.append(printer.getDocument());
        query.append(" WITH(NOLOCK) WHERE folio LIKE '%-%-' + ?");
        try {
            Volumen vol = VolumenManager.getVolumen(conn);
            int idCabinet = -1;
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, String.valueOf(printer.getIdField()));
            rs = ps.executeQuery();
            if (rs.next())
                idCabinet = rs.getInt("ID_GABINETE");
            else
                throw new FirmaElectronicaException("No se encontro gabinete para la aplicacion: " + printer.getDocument() + " con folio " + printer.getIdField());
            Carpeta folder = CarpetaManager.getCarpetaByName(conn, printer.getDocument(), idCabinet, folderName);
            if (DocumentoManager.existeDocumentoCapturado(conn, folder.getTituloAplicacion(), folder.getIdGabinete(), folder.getIdCarpeta(), printer.getDocName()))
                return DocumentoManager.getDocumento(conn, folder.getTituloAplicacion(), folder.getIdGabinete(), folder.getIdCarpeta(), printer.getDocName()).getFullPathFilesNames()[0];
            filename = printer.generaArchivoFirma(conn, vol, folder, isSignedCopy);
            return filename;
        } catch (SQLException | FortimaxException e) {
            throw new FirmaElectronicaException(e);
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public static String generaArchivoInformeComision(Connection conn, SolicitudFirmaElectronica printer, String folderName, boolean isSignedCopy) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String filename = "";
        StringBuilder query = new StringBuilder();
        query.append("SELECT ID_GABINETE FROM imx");
        query.append(printer.getDocument());
        query.append(" WITH(NOLOCK) WHERE folio LIKE '%-%-' + ?");
        try {
            Volumen vol = VolumenManager.getVolumen(conn);
            int idCabinet = -1;
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, String.valueOf(printer.getIdField()));
            rs = ps.executeQuery();
            if (rs.next())
                idCabinet = rs.getInt("ID_GABINETE");
            else
                throw new FirmaElectronicaException("No se encontro gabinete para la aplicacion: " + printer.getDocument() + " con folio " + printer.getIdField());
            Carpeta folder = CarpetaManager.getCarpetaByName(conn, printer.getDocument(), idCabinet, folderName);
            if (DocumentoManager.existeDocumentoCapturado(conn, folder.getTituloAplicacion(), folder.getIdGabinete(), folder.getIdCarpeta(), printer.getDocName()))
                return DocumentoManager.getDocumento(conn, folder.getTituloAplicacion(), folder.getIdGabinete(), folder.getIdCarpeta(), printer.getDocName()).getFullPathFilesNames()[0];
            filename = printer.generaArchivoInformeComision(conn, vol, folder, isSignedCopy);
            return filename;
        } catch (SQLException | FortimaxException e) {
            throw new FirmaElectronicaException(e);
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public static void registraBitacora(Connection conn, String operacion, SolicitudFirmaElectronica sfe) throws Exception {
        StringBuilder query = new StringBuilder();
        query.append("INSERT INTO tBitacoraFirma ");
        query.append("        ( cTipoDocumento , ");
        query.append("          nFolioDocumento , ");
        query.append("          dFechaOperacion , ");
        query.append("          U_LOGIN , ");
        query.append("          cOperacion ");
        query.append("        ) ");
        query.append("VALUES  ( ?, ");
        query.append("          ?, ");
        query.append("          ?, ");
        query.append("          ?, ");
        query.append("          ? ");
        query.append("        ) ");
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, sfe.getDocument());
            ps.setInt(2, sfe.getIdField());
            ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            ps.setString(4, sfe.getUsuario().getLogin());
            ps.setString(5, operacion);
            ps.executeUpdate();
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static void registraBitacoraCancelacion(Connection conn, String motivo, SolicitudFirmaElectronica sfe) throws Exception {
        StringBuilder query = new StringBuilder();
        query.append("INSERT INTO tBitacoraCancelacionFIEL ");
        query.append("        ( cTipoDocumento , ");
        query.append("          nFolioDocumento , ");
        query.append("          cMotivo , ");
        query.append("          cEsCargaMasiva , ");
        query.append("          U_LOGIN  ");
        query.append("        ) ");
        query.append("VALUES  ( ?, ");
        query.append("          ?, ");
        query.append("          ?, ");
        query.append("          ?, ");
        query.append("          ? ");
        query.append("        ) ");
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, sfe.getDocument());
            ps.setInt(2, sfe.getIdField());
            ps.setString(3, motivo);
            ps.setString(4, sfe.isCargaMasiva() ? "S" : "N");
            ps.setString(5, sfe.getUsuario().getLogin());
            ps.executeUpdate();
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static void registraBitacoraRM(Connection conn, String operacion, RecepcionMaterialFIEL sfe) throws Exception {
        StringBuilder query = new StringBuilder();
        query.append("INSERT INTO tBitacoraFirma ");
        query.append("        ( cTipoDocumento , ");
        query.append("          nFolioDocumento , ");
        query.append("          dFechaOperacion , ");
        query.append("          U_LOGIN , ");
        query.append("          cOperacion ");
        query.append("        ) ");
        query.append("VALUES  ( ?, ");
        query.append("          ?, ");
        query.append("          ?, ");
        query.append("          ?, ");
        query.append("          ? ");
        query.append("        ) ");
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, sfe.getDocument());
            ps.setInt(2, sfe.getRecepcionMaterial().getIdNota());
            ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            ps.setString(4, sfe.getUsuario().getLogin());
            ps.setString(5, operacion);
            ps.executeUpdate();
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static void terminaFirmaReporte(Connection conn, FirmaElectronicaReporte fer) throws Exception {
        StringBuilder query = new StringBuilder();
        query.append("UPDATE tEdoFinancieroFirmaElectronica ");
        query.append("   SET cEstatus = '").append(FirmaElectronicaReporte.FIRMADO).append("' ");
        query.append(" WHERE nIDEdoFinanciero = ? ");
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, fer.getIdField());
            ps.executeUpdate();
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static boolean isQuestionnaireAnswered(Connection conn, SolicitudFirmaElectronica solicitudFirmaElectronica) throws FirmaElectronicaException {
        ScalarHandler scalarHandler = new ScalarHandler("existe");
        QueryRunner runner = new QueryRunner();
        int existe;
        try {
            existe = (int) runner.query(conn, queryExistsQuestionnaire.toString(), scalarHandler, solicitudFirmaElectronica.getDocument(), solicitudFirmaElectronica.getIdField());
            return existe > 0;
        } catch (SQLException e) {
            throw new FirmaElectronicaException(e);
        }
    }

    public static boolean isQuestionnaireAnswered(Connection conn, EgresoEncabezado egresoEncabezado) throws FirmaElectronicaException {
        ScalarHandler scalarHandler = new ScalarHandler("existe");
        QueryRunner runner = new QueryRunner();
        int existe;
        try {
            existe = (int) runner.query(conn, queryExistsQuestionnaire.toString(), scalarHandler, egresoEncabezado.getTipoPago(), egresoEncabezado.getFolioPago());
            return existe > 0;
        } catch (SQLException e) {
            throw new FirmaElectronicaException(e);
        }
    }

    public static int cancelaDocumento(Connection conn, SolicitudFirmaElectronica solicitudFirmaElectronica) throws SQLException {
        StringBuilder query = new StringBuilder();
        query.append(" UPDATE ");
        query.append(solicitudFirmaElectronica.getHeader());
        query.append(" set cdocumentohaplicado = 'C' WHERE ");
        query.append(solicitudFirmaElectronica.getField());
        query.append(" = ?");
        PreparedStatement ps = null;
        int afectados = 0;
        try {
            log.trace("Object: {}", query.toString());
            log.trace("Object: {}", solicitudFirmaElectronica.getHeader());
            log.trace("Object: {}", solicitudFirmaElectronica.getField());
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, solicitudFirmaElectronica.getIdField());
            afectados = ps.executeUpdate();
            return afectados;
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static void borraBitacora(Connection conn, int numero) throws SQLException {
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement("DELETE tBitacoraFirma WHERE nFolioDocumento = ? AND cTipoDocumento = 'CONCILIABANCOS'");
            ps.setInt(1, numero);
            ps.executeUpdate();
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static int consultaComision(Connection conn, int folio, String tabla) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        int comision = 0;
        String tipo = "";
        try {
            if ("tRELACIONGASTOSencabezado".equalsIgnoreCase(tabla)) {
                tipo = "RELACIONGASTOS";
            } else if ("tComisionesSinComprobacionEnc".equalsIgnoreCase(tabla)) {
                tipo = "COMSINVIATICOS";
            } else {
                tipo = "CAJA";
            }
            ps = conn.prepareStatement("SELECT ISNULL(nidComisionModulo,0 ) nIdComision FROM tRelacionComprobacionComisiones WITH (NOLOCK) WHERE nFolioTramite = ? and cTipoTramite = ?");
            ps.setInt(1, folio);
            ps.setString(2, tipo);
            rs = ps.executeQuery();
            if (rs.next()) {
                comision = rs.getInt(1);
            }
            return comision;
        } finally {
            CloseObject.closeObject(ps);
            CloseObject.closeObject(rs);
        }
    }

    public static void validaFinalizaComision(Connection conn, int folioComision) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder query = new StringBuilder();
        BigDecimal importe = new BigDecimal("0.00");
        try {
            query.append(" SELECT SUM(sumTotal ) sumTotal FROM (");
            query.append("	SELECT ISNULL(SUM(TOTAL),0) sumTotal");
            query.append("	FROM v_RelacionComision ");
            query.append("	WHERE nidComisionModulo = ? and tipo in ( 'A', 'X') AND estatus = 'Aplicado' AND nEnviadoSICOP IN (1,2) ");
            query.append(" UNION ");
            query.append(" SELECT ISNULL(SUM(-viaticos),0)  + ISNULL(SUM(-TRANSPORTE),0)  sumComprueba ");
            query.append(" FROM v_RelacionComision ");
            query.append(" WHERE nidComisionModulo = ? and tipo in ( 'D', 'X' ) AND estatus = 'Aplicado' AND nEnviadoSICOP IN (1,2) ");
            query.append(") as tbl");
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, folioComision);
            ps.setInt(2, folioComision);
            rs = ps.executeQuery();
            if (rs.next()) {
                importe = rs.getBigDecimal(1);
            }
            int existeDevengadoPend = validaExisteDevengadoPendiente(conn, folioComision);
            if (importe.compareTo(BigDecimal.ZERO) == 0 && existeDevengadoPend == 0) {
                Agenda agenda = AgendaDAO.consultaFechaAgendaAcumulada(conn, folioComision);
                int tienePagos = GeneraSolicitudViaticos.revisarPagos(conn, folioComision);
                ComisionDAO.insertarBitacoraRevertir(conn, folioComision, agenda, agenda.getIdEmpleado(), "Asistencia - Finaliza comisión automática");
                ComisionDAO.actualizarBitacoraComision(conn, folioComision, "SAI");
                ComisionDAO.finalizaComision(conn, folioComision);
                if (tienePagos == 0) {
                    GeneraSolicitudViaticos.actualizarAsistencia(conn, agenda, agenda.getIdEmpleado(), "Comisión");
                } else {
                    GeneraSolicitudViaticos.actualizarAsistencia(conn, agenda, agenda.getIdEmpleado(), "Viáticos");
                }
            }
        } finally {
            CloseObject.closeObject(ps);
            CloseObject.closeObject(rs);
        }
    }

    private static int validaExisteDevengadoPendiente(Connection conn, int folioComision) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        int existe = 0;
        try {
            ps = conn.prepareStatement("SELECT COUNT(*) FROM v_RelacionComision where nidComisionModulo = ? AND  nEnviadoSICOP <= 0 and tipo = 'X'");
            ps.setInt(1, folioComision);
            rs = ps.executeQuery();
            if (rs.next()) {
                existe = rs.getInt(1);
            }
        } finally {
            CloseObject.closeObject(ps);
            CloseObject.closeObject(rs);
        }
        return existe;
    }

    public static boolean autorizacionFirmada(Connection conn, String documento, int folio) throws SQLException {
        StringBuilder query = new StringBuilder("SELECT COUNT(*) AS firmado FROM tBitacoraFirma WITH(NOLOCK) WHERE cTipoDocumento = ?  AND nFolioDocumento = ?  AND cOperacion = 'AUT'");
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, documento);
            ;
            ps.setInt(2, folio);
            rs = ps.executeQuery();
            return rs.next() && rs.getInt("firmado") > 0;
        } finally {
            CloseObject.closeObject(ps, rs);
        }
    }

    public static boolean voBoFirmado(Connection conn, String documento, int folio) throws SQLException {
        StringBuilder query = new StringBuilder("SELECT COUNT(*) AS firmado FROM tBitacoraFirma WITH(NOLOCK) WHERE cTipoDocumento = ?  AND nFolioDocumento = ?  AND cOperacion = 'VOBO'");
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, documento);
            ;
            ps.setInt(2, folio);
            rs = ps.executeQuery();
            return rs.next() && rs.getInt("firmado") > 0;
        } finally {
            CloseObject.closeObject(ps, rs);
        }
    }

    public static String esLaudoIF(Connection conn, int valorLlave) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String laudo_IF = "";
        try {
            ps = conn.prepareStatement("SELECT CASE WHEN COUNT(*) = 0 THEN 'NO' ELSE 'SI' END esLaudo_IF FROM tComprobacionLaudos_Ingreso WITH (NOLOCK) WHERE nFolioRELACIONGASTOS = ?");
            ps.setInt(1, valorLlave);
            rs = ps.executeQuery();
            if (rs.next()) {
                laudo_IF = rs.getString(1);
            }
        } finally {
            CloseObject.closeObject(ps);
            CloseObject.closeObject(rs);
        }
        return laudo_IF;
    }

    public static int avanzaEstatusSICOPRG(Connection conn, String table, String pkField, int pkValue, int nEstatusSICOP) throws Exception {
        StringBuilder query = new StringBuilder();
        query.append(" UPDATE t1 ");
        query.append(" SET t1.nEnviadoSICOP = CASE WHEN t2.nFolioRELACIONGASTOS IS NULL THEN ? ELSE 1 END FROM ");
        query.append(table).append(" t1 WITH (NOLOCK)");
        query.append(" LEFT JOIN tComprobacionLaudos_INGRESO t2 WITH (NOLOCK) ");
        query.append(" ON t1.nFolioRELACIONGASTOS = t2.nFolioRELACIONGASTOS WHERE t1.");
        query.append(pkField).append(" = ?");
        PreparedStatement ps = null;
        int afectados = 0;
        try {
            log.trace("Object: {}", query.toString());
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, nEstatusSICOP);
            ps.setInt(2, pkValue);
            afectados = ps.executeUpdate();
            return afectados;
        } finally {
            CloseObject.closeObject(ps);
        }
    }
}
