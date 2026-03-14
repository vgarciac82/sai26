package com.axtel.contratos.core;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.axtel.contratos.ActionsFIEL;
import com.axtel.contratos.ProcesoEnteraSatisfaccionBusinessLogic;
import com.axtel.contratos.entities.DatEnteraSatisfaccion;
import com.syc.cfdi.db.CloseObject;
import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.sai.firmaElectronica.FirmaElectronicaBusinessLogic;
import com.syc.sai.firmaElectronica.interfaces.SolicitudFirmaElectronica;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImplementsActionsFIEL_ENSA extends DataSourceManager implements ActionsFIEL {

    private static final Logger log = LoggerFactory.getLogger(ImplementsActionsFIEL_ENSA.class);

    @Override
    public List<String> sign(SolicitudFirmaElectronica sfe, String cerFileName, String keyFileName) throws Exception {
        FirmaElectronicaBusinessLogic febl = new FirmaElectronicaBusinessLogic(GestionInterface.ATT_CONEXION);
        List<String> logProc = null;
        String[] folios = null;
        Connection conn = null;
        try {
            folios = sfe.getFolios().split(",");
            for (String folio : folios) {
                conn = null;
                try {
                    conn = getConnection(GestionInterface.ATT_CONEXION);
                    sfe.setFolios(folio);
                    if (logProc == null)
                        logProc = new ArrayList<String>();
                    febl.signENSA(conn, (ProcesoEnteraSatisfaccionBusinessLogic) sfe, cerFileName, keyFileName);
                    //Guardar bitacora
                    ((ProcesoEnteraSatisfaccionBusinessLogic) sfe).registrarBitacora(conn, SolicitudFirmaElectronica.FIRMA_ENSA);
                    //Notificar
                    ((ProcesoEnteraSatisfaccionBusinessLogic) sfe).getDatEnteraSatisfaccion().setnIdEstatus(SolicitudFirmaElectronica.ESTATUS_ENSA_CONSULTA);
                    if (((ProcesoEnteraSatisfaccionBusinessLogic) sfe).getDatEnteraSatisfaccion().getnServPrestEnteraSatisfaccion() == SolicitudFirmaElectronica.SERVICIO_NO_PRESTADO_ENSA) {
                        ((ProcesoEnteraSatisfaccionBusinessLogic) sfe).getDatEnteraSatisfaccion().setnIdEstatus(SolicitudFirmaElectronica.ESTATUS_ENSA_TESTIGO1);
                    }
                    ((ProcesoEnteraSatisfaccionBusinessLogic) sfe).sendEmailProcess(conn, false);
                    //Actualizar estatus
                    ProcesoEnteraSatisfaccionManager.updateStateEnteraSatisfaccion(conn, ((ProcesoEnteraSatisfaccionBusinessLogic) sfe).getDatEnteraSatisfaccion());
                    //Avanzar caso
                    avanzandoCaso(((ProcesoEnteraSatisfaccionBusinessLogic) sfe).getDatEnteraSatisfaccion().getcFolio(), ((ProcesoEnteraSatisfaccionBusinessLogic) sfe).getUsuario().getLogin(), ((ProcesoEnteraSatisfaccionBusinessLogic) sfe).getDatEnteraSatisfaccion().getOper(), ((ProcesoEnteraSatisfaccionBusinessLogic) sfe).getDatEnteraSatisfaccion().getResp());
                    logProc.add(" Documento: " + ((ProcesoEnteraSatisfaccionBusinessLogic) sfe).getDocument() + " con Folio: " + ((ProcesoEnteraSatisfaccionBusinessLogic) sfe).getDatEnteraSatisfaccion().getcFolio() + " firmado exitosamente. Avanzado a siguiente estatus");
                    conn.commit();
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    logProc.add(" Error en documento: " + ((ProcesoEnteraSatisfaccionBusinessLogic) sfe).getDocument() + " con Folio: " + ((ProcesoEnteraSatisfaccionBusinessLogic) sfe).getDatEnteraSatisfaccion().getcFolio() + " Causa: " + e);
                    if (conn != null)
                        try {
                            conn.rollback();
                        } catch (Exception e2) {
                            log.warn(e2.getMessage(), e2);
                        }
                    throw e;
                } finally {
                    CloseObject.closeObject(conn);
                }
            }
        } finally {
            febl = null;
            folios = null;
            conn = null;
        }
        return logProc;
    }

    private void avanzandoCaso(String cFolio, String cLogin, String[] oper, String[] resp) throws GestionException {
        Caso c = null;
        CasoBusinessLogic casoTx = null;
        try {
            casoTx = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
            c = casoTx.getCaso(cFolio);
            casoTx.avanzaCaso(c, cLogin, "", resp, oper, com.syc.gestion.util.Util.readValuesCasoDato(c.getCasoDato()), null);
        } finally {
            c = null;
            casoTx = null;
        }
    }

    @Override
    public List<String> reject(Usuario u, Map<String, String> objMap) throws Exception {
        List<String> logProc = null;
        Connection conn = null;
        String[] folios = null;
        SolicitudFirmaElectronica sfe = null;
        DatEnteraSatisfaccion dat = null;
        try {
            conn = getConnection(GestionInterface.ATT_CONEXION);
            if (logProc == null)
                logProc = new ArrayList<String>();
            folios = objMap.get("folios").split(",");
            sfe = new ProcesoEnteraSatisfaccionBusinessLogic();
            sfe.setFolios(objMap.get("folios"));
            sfe.setFileExtension("pdf");
            sfe.setUsuario(u);
            sfe.setDocument(objMap.get("cTipoPago"));
            dat = ProcesoEnteraSatisfaccionManager.read(conn, Integer.parseInt(folios[0]));
            dat.setcObservacionesTramite(objMap.get("motivoRechazo"));
            ((ProcesoEnteraSatisfaccionBusinessLogic) sfe).setDatEnteraSatisfaccion(dat);
            //Notificar
            ((ProcesoEnteraSatisfaccionBusinessLogic) sfe).getDatEnteraSatisfaccion().setnIdEstatus(SolicitudFirmaElectronica.ESTATUS_ENSA_CAPTURA);
            ((ProcesoEnteraSatisfaccionBusinessLogic) sfe).sendEmailProcess(conn, true);
            //Actualizar estatus
            ProcesoEnteraSatisfaccionManager.updateStateEnteraSatisfaccion(conn, ((ProcesoEnteraSatisfaccionBusinessLogic) sfe).getDatEnteraSatisfaccion());
            ProcesoEnteraSatisfaccionManager.updateObservationEnteraSatisfaccion(conn, ((ProcesoEnteraSatisfaccionBusinessLogic) sfe).getDatEnteraSatisfaccion());
            //Avanzar caso
            avanzandoCaso(((ProcesoEnteraSatisfaccionBusinessLogic) sfe).getDatEnteraSatisfaccion().getcFolio(), ((ProcesoEnteraSatisfaccionBusinessLogic) sfe).getUsuario().getLogin(), ((ProcesoEnteraSatisfaccionBusinessLogic) sfe).getDatEnteraSatisfaccion().getOper(), ((ProcesoEnteraSatisfaccionBusinessLogic) sfe).getDatEnteraSatisfaccion().getResp());
            logProc.add(" Documento: " + sfe.getDocument() + " con Folio: " + dat.getcFolio() + " devuelto a captura.");
            conn.commit();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            logProc.add(" Error en documento: " + ((ProcesoEnteraSatisfaccionBusinessLogic) sfe).getDocument() + " con Folio: " + ((ProcesoEnteraSatisfaccionBusinessLogic) sfe).getDatEnteraSatisfaccion().getcFolio() + " Causa: " + e);
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn(e2.getMessage(), e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
            folios = null;
            sfe = null;
            dat = null;
        }
        return logProc;
    }

    @Override
    public List<String> aceptProcess(Usuario u, Map<String, String> objMap) throws Exception {
        List<String> logProc = null;
        Connection conn = null;
        String[] folios = null;
        SolicitudFirmaElectronica sfe = null;
        DatEnteraSatisfaccion dat = null;
        try {
            conn = getConnection(GestionInterface.ATT_CONEXION);
            if (logProc == null)
                logProc = new ArrayList<String>();
            folios = objMap.get("folios").split(",");
            sfe = new ProcesoEnteraSatisfaccionBusinessLogic();
            sfe.setFolios(objMap.get("folios"));
            sfe.setUsuario(u);
            sfe.setDocument(objMap.get("cTipoPago"));
            sfe.setDocName("Anexo1A");
            dat = ProcesoEnteraSatisfaccionManager.read(conn, Integer.parseInt(folios[0]));
            ((ProcesoEnteraSatisfaccionBusinessLogic) sfe).setDatEnteraSatisfaccion(dat);
            //Notificar
            if (dat.getnIdEstatus() == SolicitudFirmaElectronica.ESTATUS_ENSA_TESTIGO1) {
                ((ProcesoEnteraSatisfaccionBusinessLogic) sfe).getDatEnteraSatisfaccion().setnIdEstatus(SolicitudFirmaElectronica.ESTATUS_ENSA_TESTIGO2);
            } else {
                ((ProcesoEnteraSatisfaccionBusinessLogic) sfe).getDatEnteraSatisfaccion().setnIdEstatus(SolicitudFirmaElectronica.ESTATUS_ENSA_CONSULTA);
            }
            ((ProcesoEnteraSatisfaccionBusinessLogic) sfe).sendEmailProcess(conn, false);
            //Actualizar estatus
            ProcesoEnteraSatisfaccionManager.updateStateEnteraSatisfaccion(conn, ((ProcesoEnteraSatisfaccionBusinessLogic) sfe).getDatEnteraSatisfaccion());
            //Avanzar caso
            avanzandoCaso(((ProcesoEnteraSatisfaccionBusinessLogic) sfe).getDatEnteraSatisfaccion().getcFolio(), ((ProcesoEnteraSatisfaccionBusinessLogic) sfe).getUsuario().getLogin(), ((ProcesoEnteraSatisfaccionBusinessLogic) sfe).getDatEnteraSatisfaccion().getOper(), ((ProcesoEnteraSatisfaccionBusinessLogic) sfe).getDatEnteraSatisfaccion().getResp());
            logProc.add(" Documento: " + sfe.getDocument() + " con Folio: " + dat.getcFolio() + " fue aceptado. Avanzo al siguiente estatus");
            conn.commit();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            logProc.add(" Error en documento: " + ((ProcesoEnteraSatisfaccionBusinessLogic) sfe).getDocument() + " con Folio: " + ((ProcesoEnteraSatisfaccionBusinessLogic) sfe).getDatEnteraSatisfaccion().getcFolio() + " Causa: " + e);
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn(e2.getMessage(), e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn);
            folios = null;
            sfe = null;
            dat = null;
        }
        return logProc;
    }
}
