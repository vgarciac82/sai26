package com.syc.obrapublica.businessLogic;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import com.syc.cfdi.db.CloseObject;
import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.obrapublica.FirmaAutorizacionObraInterface;
import com.syc.obrapublica.core.DatosEstimacionObra;
import com.syc.obrapublica.core.EstimacionObraFIEL;
import com.syc.obrapublica.core.manager.FirmaAutorizacionObraManager;
import com.syc.sai.firmaElectronica.FirmaElectronicaBusinessLogic;
import com.syc.sai.firmaElectronica.interfaces.SolicitudFirmaElectronica;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class FirmaAutEstObraBusinessLogic extends DataSourceManager implements FirmaAutorizacionObraInterface {

    private static final Logger log = LoggerFactory.getLogger(FirmaAutEstObraBusinessLogic.class);

    @Override
    public void enviaAutorizacion(Usuario usuario, HttpServletRequest request, String REPORT_PATH) throws Exception {
        DatosEstimacionObra estimacion = null;
        SolicitudFirmaElectronica sfe = null;
        String[] arrayFolio = null;
        try {
            estimacion = fillObject(request);
            estimacion.setcLogin(usuario.getLogin());
            estimacion.setIdEstatusEstimacion(SolicitudFirmaElectronica.ESTATUS_ESTIMACION_FIRMA);
            arrayFolio = estimacion.getcFolioObra().split("-");
            estimacion.setnFolioObra(arrayFolio[2]);
            sfe = new EstimacionObraFIEL();
            sfe.setDocName("Atenta Nota ".concat((estimacion.getnEstimacion() == 0) ? "Anticipo 0" : "Estimacion " + String.valueOf(estimacion.getnEstimacion())));
            sfe.setFileExtension("pdf");
            sfe.setReportPath(REPORT_PATH);
            sfe.setUsuario(usuario);
            sfe.setDocument(SolicitudFirmaElectronica.TITULO_APLICACION_OBRA);
            sfe.setIdField(Integer.parseInt(arrayFolio[2]));
            ((EstimacionObraFIEL) sfe).setEstimacionObra(estimacion);
            ((EstimacionObraFIEL) sfe).solicitaAutorizacionEstimacion();
        } finally {
            estimacion = null;
            arrayFolio = null;
            sfe = null;
        }
    }

    @Override
    public List<String> autoriza(SolicitudFirmaElectronica sfe, String cerFileName, String keyFileName) throws Exception {
        FirmaElectronicaBusinessLogic febl = new FirmaElectronicaBusinessLogic(GestionInterface.ATT_CONEXION);
        List<String> logProc = null;
        String[] folios = null;
        File signedFile = null;
        Connection conn = null;
        DatosEstimacionObra estimacion = null;
        int nNumEmpleadoSig = 0;
        try {
            folios = sfe.getFolios().split(",");
            for (String folio : folios) {
                conn = null;
                signedFile = null;
                try {
                    conn = getConnection();
                    sfe.setFolios(folio);
                    if (logProc == null)
                        logProc = new ArrayList<String>();
                    estimacion = FirmaAutorizacionObraManager.read(conn, Integer.parseInt(folio));
                    ((EstimacionObraFIEL) sfe).setEstimacionObra(estimacion);
                    sfe.setIdField(Integer.parseInt(estimacion.getnFolioObra()));
                    if (estimacion.getIdEstatusEstimacion() == SolicitudFirmaElectronica.ESTATUS_ESTIMACION_FIRMA) {
                        estimacion.setNumFirmante(1);
                        estimacion.setIdEstatusEstimacion(SolicitudFirmaElectronica.ESTATUS_ESTIMACION_FIRMA2);
                        nNumEmpleadoSig = estimacion.getNumeroEmpleadoJefe();
                        ((EstimacionObraFIEL) sfe).registrarBitacora(conn, SolicitudFirmaElectronica.AUT_EST);
                    } else if (estimacion.getIdEstatusEstimacion() == SolicitudFirmaElectronica.ESTATUS_ESTIMACION_FIRMA2) {
                        estimacion.setNumFirmante(2);
                        nNumEmpleadoSig = estimacion.getNumeroEmpleadoSubgerente();
                        estimacion.setIdEstatusEstimacion(SolicitudFirmaElectronica.ESTATUS_ESTIMACION_FIRMA3);
                        ((EstimacionObraFIEL) sfe).registrarBitacora(conn, SolicitudFirmaElectronica.AUT_EST_JEFE);
                    } else if (estimacion.getIdEstatusEstimacion() == SolicitudFirmaElectronica.ESTATUS_ESTIMACION_FIRMA3) {
                        estimacion.setNumFirmante(3);
                        estimacion.setIdEstatusEstimacion(SolicitudFirmaElectronica.ESTATUS_ESTIMACION_AUT);
                        ((EstimacionObraFIEL) sfe).registrarBitacora(conn, SolicitudFirmaElectronica.AUT_EST_SUBGERENTE);
                    }
                    signedFile = febl.firmaEstimacionObra(conn, (EstimacionObraFIEL) sfe, cerFileName, keyFileName);
                    FirmaAutorizacionObraManager.avanzaEstatus(conn, ((EstimacionObraFIEL) sfe).getEstimacionObra());
                    logProc.add(" Documento: " + ((EstimacionObraFIEL) sfe).getDocument() + " con Folio: " + folio + " firmado exitosamente. Avanzado a siguiente estatus");
                    if (((EstimacionObraFIEL) sfe).getEstimacionObra().getIdEstatusEstimacion() == SolicitudFirmaElectronica.ESTATUS_ESTIMACION_AUT) {
                        ((EstimacionObraFIEL) sfe).notificaAutorizacion(conn, signedFile, "Atenta_Nota_" + (((EstimacionObraFIEL) sfe).getEstimacionObra().getnEstimacion() == 0 ? "Anticipo" : String.valueOf(((EstimacionObraFIEL) sfe).getEstimacionObra().getnEstimacion())) + "_Firmada.pdf");
                    } else {
                        estimacion.setNumeroEmpleado(nNumEmpleadoSig);
                        ((EstimacionObraFIEL) sfe).notificaOperacionPendiente(conn, SolicitudFirmaElectronica.AUT_EST);
                    }
                    conn.commit();
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    logProc.add(" Error en documento: " + ((EstimacionObraFIEL) sfe).getDocument() + " con Folio: " + folio + " Causa: " + e);
                    if (conn != null)
                        try {
                            conn.rollback();
                        } catch (Exception e2) {
                            log.warn(e2.getMessage(), e2);
                        }
                    throw e;
                } finally {
                    CloseObject.closeObject(conn);
                    signedFile = null;
                }
            }
        } finally {
            febl = null;
            folios = null;
            conn = null;
        }
        return logProc;
    }

    @Override
    public List<String> rechaza(Usuario usuario, Map<String, String> objMap) throws Exception {
        List<String> logProc = null;
        SolicitudFirmaElectronica sfe = null;
        DatosEstimacionObra estimacion = null;
        String[] folios = null;
        Connection conn = null;
        try {
            folios = objMap.get("folios").split(",");
            sfe = new EstimacionObraFIEL();
            sfe.setFolios(objMap.get("folios"));
            sfe.setFileExtension("pdf");
            sfe.setPasswordLlave(objMap.get("passwordLlave"));
            sfe.setUsuario(usuario);
            sfe.setDocument(objMap.get("cTipoPago"));
            ((EstimacionObraFIEL) sfe).setMotivoRechazo(objMap.get("motivoRechazo"));
            for (String folio : folios) {
                conn = null;
                try {
                    conn = getConnection();
                    if (logProc == null)
                        logProc = new ArrayList<String>();
                    estimacion = FirmaAutorizacionObraManager.read(conn, Integer.parseInt(folio));
                    ((EstimacionObraFIEL) sfe).setEstimacionObra(estimacion);
                    sfe.setIdField(Integer.parseInt(estimacion.getnFolioObra()));
                    ((EstimacionObraFIEL) sfe).rechazaEstimacion(conn);
                    logProc.add(" Documento: " + sfe.getDocument() + " con Folio: " + folio + " cancelado exitosamente.");
                    estimacion = null;
                    conn.commit();
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    logProc.add(" Error en documento: " + sfe.getDocument() + " con Folio: " + folio + " Causa: " + e);
                    if (conn != null)
                        try {
                            conn.rollback();
                        } catch (Exception e2) {
                            log.warn(e2.getMessage(), e2);
                        }
                } finally {
                    CloseObject.closeObject(conn);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            folios = null;
            estimacion = null;
        }
        return logProc;
    }

    private DatosEstimacionObra fillObject(HttpServletRequest req) throws UnsupportedEncodingException {
        DatosEstimacionObra est = new DatosEstimacionObra();
        est.setFolioNota(new String(req.getParameter("cFolioNota").getBytes("ISO-8859-1"), "UTF-8"));
        est.setMotivoAutorizacion(new String(req.getParameter("dMotivoNota").getBytes("ISO-8859-1"), "UTF-8"));
        est.setNumeroEmpleado(Integer.parseInt(req.getParameter("cNumeroEmpleado")));
        est.setNumeroEmpleadoJefe(Integer.parseInt(req.getParameter("cNumeroEmpleadoJefe")));
        est.setNumeroEmpleadoSubgerente(Integer.parseInt(req.getParameter("cNumeroEmpleadoSubgerente")));
        est.setContratoCNET(new String(req.getParameter("cIDContrato").getBytes("ISO-8859-1"), "UTF-8"));
        est.setcFolioObra(new String(req.getParameter("folioSAI").getBytes("ISO-8859-1"), "UTF-8"));
        //
        est.setnEstimacion(req.getParameter("nEstimacionAut") == null || "".equalsIgnoreCase(req.getParameter("nEstimacionAut")) ? -1 : Integer.parseInt(req.getParameter("nEstimacionAut")));
        return est;
    }
}
