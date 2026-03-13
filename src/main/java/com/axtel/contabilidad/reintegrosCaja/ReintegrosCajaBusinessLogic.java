package com.axtel.contabilidad.reintegrosCaja;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;
import com.syc.contable.AccountingEngine;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import com.syc.sai.firmaElectronica.core.FirmaElectronicaManager;
import com.syc.sai.firmaElectronica.interfaces.SolicitudFirmaElectronica;
import com.axtel.contabilidad.reintegrosCaja.core.ReintegrosCajaDetalle;
import com.axtel.contabilidad.reintegrosCaja.core.ReintegrosCajaEncabezado;
import com.axtel.contabilidad.reintegrosCaja.core.SolicitudReintegroCajaFirmaElectronica;
import com.axtel.contabilidad.reintegrosCaja.core.ReintegrosCaja;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ana
 * @param <ReintegroCaja>
 */
public class ReintegrosCajaBusinessLogic extends DataSourceManager {

    public ReintegrosCaja reintegrosCaja = null;

    private String reportPath;

    private static Logger log = LoggerFactory.getLogger(ReintegrosCajaBusinessLogic.class);

    private String header;

    private String detail;

    private String document;

    private String field;

    private String folderName;

    private String documentName;

    public void setReportPath(String reportPath) {
        this.reportPath = reportPath;
    }

    public String getReportPath() {
        return this.reportPath;
    }

    private String getDetail() {
        return detail;
    }

    private void setDetail(String detail) {
        this.detail = detail;
    }

    private String getDocumentName() {
        return documentName;
    }

    private void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    private String getDocument() {
        return document;
    }

    private String setDocument() {
        return document;
    }

    private String getField() {
        return this.field;
    }

    private void setField(String field) {
        this.field = field;
    }

    private String getHeader() {
        return this.header;
    }

    private void setHeader(String header) {
        this.header = header;
    }

    private String getFolderName() {
        return folderName;
    }

    private void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    private void setDocument(String document) {
        this.document = document;
    }

    public ReintegrosCajaBusinessLogic() {
        init(null);
    }

    public ReintegrosCajaBusinessLogic(String jniName) {
        init(jniName);
    }

    public void init(String jniName) {
        if (StringUtils.isBlank(jniName))
            super.init();
        else
            super.init(jniName);
        setHeader("tReintegroCajaEncabezado");
        setDetail("tReintegroCajaDetalle");
        setDocument("REINTEGROCAJA");
        setFolderName("Documentacion Comprobatoria");
        setDocumentName("Solicitud Firmada");
        setField("nFolioReintegroCaja");
    }

    public ReintegrosCajaEncabezado getReintegrosCajaEncabezado(int folio) throws Exception {
        //DATOS SOLICITADOS POR EGRESOSFIRMANTES
        Connection conn = null;
        ReintegrosCajaEncabezado rce;
        try {
            conn = getConnection();
            rce = ReintegrosCajaManager.getReintegrosCajaEncabezado(conn, folio);
        } catch (Exception exc) {
            log.error(exc);
            throw new Exception(exc);
        } finally {
            CloseObject.closeObject(conn);
        }
        return rce;
    }

    public ReintegrosCajaDetalle getReintegrosCajaDetalle(int folio) throws Exception {
        //DATOS SOLICITADOS POR EGRESOSFIRMANTES
        Connection conn = null;
        ReintegrosCajaDetalle res;
        try {
            conn = getConnection();
            res = ReintegrosCajaManager.getReintegrosCajaDetalle(conn, folio);
        } catch (Exception exc) {
            log.error(exc);
            throw new Exception(exc);
        } finally {
            CloseObject.closeObject(conn);
        }
        return res;
    }

    public String getTipoPolizaEvento(String cEvento) throws Exception {
        Connection conn = null;
        String tipoPoliza = null;
        try {
            conn = getConnection();
            tipoPoliza = ReintegrosCajaManager.getTipoPolizaEvento(conn, cEvento);
        } catch (Exception e) {
            log.error(e);
            throw new Exception(e);
        } finally {
            CloseObject.closeObject(conn);
        }
        return tipoPoliza;
    }

    public boolean insertarDatos(ReintegrosCajaEncabezado encabezado, ReintegrosCajaDetalle detalle) {
        Connection conn = null;
        boolean inserto = false;
        try {
            conn = getConnection();
            ReintegrosCajaManager.insertarEncabezado(conn, encabezado);
            ReintegrosCajaManager.insertarDetalle(conn, detalle);
            conn.commit();
            inserto = true;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Problemas en rollback: " + e2);
                }
        } finally {
            CloseObject.closeObject(conn);
        }
        return inserto;
    }

    public String aplicaReintegroCaja(Caso c, String cFechaAplicacion, String tablaEncabezado, String tablaDetalle, String documento, String campoFolio) throws Exception {
        String mensaje = "";
        String folio = "";
        AccountingEngine ae = new AccountingEngine();
        Connection conn = null;
        String validaMes = "";
        try {
            conn = getConnection();
            validaMes = ReintegrosCajaManager.validaMes(conn, c, cFechaAplicacion);
            if ("S".equals(validaMes)) {
                folio = c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1);
                //Actualiza fecha de aplicacion
                ReintegrosCajaManager.actializaFecha(conn, folio, cFechaAplicacion);
                //Aplicación contable de la retencion
                log.debug("Inicia aplicacion contable" + new Timestamp(System.currentTimeMillis()) + " Para  Folio:" + folio);
                ae.makeAccountingApplication(conn, documento, folio, tablaEncabezado, tablaDetalle, campoFolio);
                log.debug("Termina Aplicacion contable " + new Timestamp(System.currentTimeMillis()) + " Para  Folio:" + folio);
                conn.commit();
            } else {
                mensaje = "El mes contable esta cerrado. Notifique al administrador";
                throw new Exception(mensaje);
            }
        } catch (Exception exc) {
            mensaje = exc.getMessage();
            try {
                conn.rollback();
            } catch (Exception ex) {
                log.warn("En Rollback", ex);
            }
            throw exc;
        } finally {
            CloseObject.closeObject(conn);
        }
        return mensaje;
    }

    public boolean insertarDatosAut(String tablaEncabezadoAut, String tablaDetalleAut, String tipoPoliza, HttpServletRequest request) {
        Connection conn = null;
        boolean inserto = false;
        try {
            conn = getConnection();
            ReintegrosCajaManager.insertarEncabezadoAut(conn, tipoPoliza, request);
            ReintegrosCajaManager.insertarDetalleAut(conn, request);
            conn.commit();
            inserto = true;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Problemas en rollback: " + e2);
                }
        } finally {
            CloseObject.closeObject(conn);
        }
        return inserto;
    }

    public String buscaDatos(int nFolioReinegroCaja) throws Exception {
        Connection conn = null;
        String existe = null;
        try {
            conn = getConnection();
            existe = ReintegrosCajaManager.existenDatos(conn, nFolioReinegroCaja);
        } catch (Exception e) {
            log.error(e);
            throw new Exception(e);
        } finally {
            CloseObject.closeObject(conn);
        }
        return existe;
    }

    public String buscaDatosAut(int nFolioReinegroCaja) throws Exception {
        Connection conn = null;
        String existe = null;
        try {
            conn = getConnection();
            existe = ReintegrosCajaManager.existenDatosAut(conn, nFolioReinegroCaja);
        } catch (Exception e) {
            log.error(e);
            throw new Exception(e);
        } finally {
            CloseObject.closeObject(conn);
        }
        return existe;
    }

    public String[] PolizaCancelacion(String string, String Encabezado, String Detalle, String Folio) throws Exception {
        Connection conn = null;
        String[] datos = new String[4];
        try {
            conn = getConnection();
            datos = ReintegrosCajaManager.PolizaCancelacion(conn, string, Encabezado, Detalle, Folio);
        } catch (Exception e) {
            log.error(e);
            throw new Exception(e);
        } finally {
            CloseObject.closeObject(conn);
        }
        return datos;
    }

    public String readfCancelacion(String nFolioReintegroCaja, String Encabezado, String Folio) throws Exception {
        String fCaptura = "";
        Connection conn = null;
        try {
            conn = getConnection();
            fCaptura = ReintegrosCajaManager.readfCancelacion(conn, nFolioReintegroCaja, Encabezado, Folio);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
        return fCaptura;
    }

    public void solicitaFirmaElectronica(Caso c, Usuario u) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            int nFolioReintegroCaja = Integer.parseInt(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1));
            SolicitudFirmaElectronica printer = new SolicitudReintegroCajaFirmaElectronica();
            printer.setDetail(getDetail());
            printer.setDocName(getDocumentName());
            printer.setDocument(getDocument());
            printer.setField(getField());
            printer.setFileExtension("pdf");
            printer.setHeader(getHeader());
            printer.setIdField(nFolioReintegroCaja);
            printer.setReportPath(getReportPath());
            printer.setUsuario(u);
            FirmaElectronicaManager.generaArchivoFirma(conn, printer, getFolderName(), false);
            CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
            Map<?, ?> datos = Util.readValuesCasoDato(c.getCasoDato());
            cbl.avanzaCaso(c, u.getLogin(), "", new String[] { "VO_BO_REINTEGROCAJA_FIEL" }, new String[] { "vo_bo_fiel" }, datos, null);
            printer.notificaOperacionPendiente(conn, SolicitudFirmaElectronica.VO_BO);
            FirmaElectronicaManager.avanzaEstatusSICOP(conn, printer, SolicitudFirmaElectronica.VO_BO_SICOP);
            FirmaElectronicaManager.actualizaMetodoAutorizacion(conn, printer.getHeader(), printer.getField(), String.valueOf(printer.getIdField()), true);
            conn.commit();
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn(e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    public String cancelaSolicitud(Caso c, String nFolioReinegroCaja, String cFechaAplicacion) throws Exception {
        String mensaje = "";
        String folio = "";
        AccountingEngine ae = new AccountingEngine();
        Connection conn = null;
        String validaMes = "";
        try {
            conn = getConnection();
            //Cancelacion contable de Reintegro Caja
            log.debug("Inicia cacelacion contable" + new Timestamp(System.currentTimeMillis()) + " Para  Folio:" + nFolioReinegroCaja);
            ae.cancelAccountingApplication(conn, "REINTEGROCAJA", nFolioReinegroCaja, "tReintegroCajaEncabezado", "tReintegroCajaDetalle", "nFolioReintegroCaja");
            log.debug("Termina Cancelacion contable " + new Timestamp(System.currentTimeMillis()) + " Para  Folio:" + nFolioReinegroCaja);
            conn.commit();
        } catch (Exception exc) {
            mensaje = exc.getMessage();
            try {
                conn.rollback();
            } catch (Exception ex) {
                log.warn("En Rollback", ex);
            }
            throw exc;
        } finally {
            CloseObject.closeObject(conn);
        }
        return mensaje;
    }
}
