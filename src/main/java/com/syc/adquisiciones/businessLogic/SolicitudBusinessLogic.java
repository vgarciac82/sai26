package com.syc.adquisiciones.businessLogic;

import java.io.DataInputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.json.JSONObject;
import com.axtel.contratos.PrecomprometidoStatus;
import com.axtel.contratos.QuestionnaireBussinessLogic;
import com.axtel.contratos.Requisition;
import com.axtel.contratos.RequisitionStatus;
import com.axtel.contratos.RequisitionType;
import com.axtel.contratos.core.ContractQuestionnaire;
import com.axtel.contratos.core.QuestionnaireManager;
import com.axtel.contratos.core.RequisitionManager;
import com.syc.adquisiciones.core.DatosRequisicion;
import com.syc.adquisiciones.manager.SolicitudManager;
import com.syc.adquisiciones.util.Util;
import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.core.CFSequenceManager;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoDatoManager;
import com.syc.gestion.core.CasoManager;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SolicitudBusinessLogic extends DataSourceManager {

    private static Logger log = LoggerFactory.getLogger(SolicitudBusinessLogic.class);

    public JSONObject apartaRT(HttpServletRequest req, DatosRequisicion datosRequi, HttpSession session, Usuario usuario) throws Exception {
        Connection conn = null;
        JSONObject jsonObj = null;
        Caso c = null;
        SolicitudManager manager = null;
        String folioCaso = null;
        int folio = 0;
        CasoBusinessLogic cbl = null;
        String cEjercicio = null;
        String DATE_FORMAT = null;
        SimpleDateFormat sdf = null;
        // today
        Calendar c1 = null;
        String today = null;
        int seqFolio;
        CFSequenceManager sequence = null;
        String seqValue = null;
        try {
            conn = DataSourceManager.getConnection(datosRequi.getJndiName());
            cbl = new CasoBusinessLogic(datosRequi.getJndiName());
            jsonObj = new JSONObject();
            manager = new SolicitudManager();
            //Se obtiene el caso si ya existe
            if (session == null) {
                log.warn("La sesion es nula.");
                throw new ServletException("La sesion es nula.");
            }
            c = manager.getCaso(session, conn);
            cEjercicio = (String) session.getAttribute(GestionInterface.ATT_ReqEjercicio);
            DATE_FORMAT = "dd/MM/yyyy";
            sdf = new SimpleDateFormat(DATE_FORMAT);
            // today
            c1 = Calendar.getInstance();
            today = sdf.format(c1.getTime());
            if (c == null) {
                log.debug("fallo. Creando caso");
                c = manager.iniciaCaso(datosRequi.getJndiName(), datosRequi.getTipoCaso(), datosRequi.getFolioGenerator(), usuario);
                //casoOrigen = CASO_CREADO;
                if (c == null) {
                    jsonObj.put("Folio1", -1);
                    jsonObj.put("Folio2", "");
                    jsonObj.put("msg", "No se pudo crear el caso");
                    log.debug("No se pudo crear el caso");
                }
            }
            if (c != null) {
                folioCaso = c.getFolio();
                log.debug("Object: {}", "Caso obtenido: " + folioCaso);
                int indice = folioCaso.lastIndexOf('-') + 1;
                folio = Integer.parseInt(folioCaso.substring(indice));
                //Datos que serán usados en el callback del ajax
                jsonObj.put("Folio1", String.valueOf(folio));
                jsonObj.put("Folio2", folioCaso);
                //Argumentos para llenar la tabla de CG_CASO_DATO y que se muestren en el inbox
                Map<String, String> datos = new HashMap<String, String>();
                datos.put("FOLIO", folioCaso);
                datos.put("FECHA_DOCUMENTO", today);
                datos.put("EJERCICIO_FISCAL", cEjercicio);
                datos.put("OPERADOR", usuario.getNombre());
                datos.put("CONCEPTO_MOV", datosRequi.getCONCEPTO_MOV());
                datos.put("MONEDA", "MXP");
                datos.put("APLICADO_CONT", "false");
                //Actualiza el caso en BD con Map<> datos
                CasoDatoManager.update(conn, c.getIdTC(), c.getIdCaso(), datos);
                //Caso sc solo tiene el id caso para hacer un select de toda su info
                //y actualizar asi los valores de Caso c
                Caso sc = new Caso();
                sc.setIdCaso(c.getIdCaso());
                c = CasoManager.select(conn, sc);
                manager.avanzaCaso(req, c, usuario, datosRequi.getPrefixPath(), datosRequi.getResponsable(), datosRequi.getNombre(), datosRequi.getJndiName());
                session.setAttribute(GestionInterface.ATT_CASE, c);
                if (c.getIdGabinete() == -1) {
                    c.setIdGabinete(cbl.creaExpediente(usuario.getLogin(), c));
                    cbl.recibeDocumentoGestion(c, new DataInputStream(req.getInputStream()));
                }
                //crea canocontrarecibo
                sequence = CFSequenceManager.getInstance();
                seqFolio = sequence.nextVal("AP-" + usuario.getPropiedad("CCENTROCONTABLE").getValor());
                seqValue = "" + (100000 + seqFolio);
                seqValue = usuario.getPropiedad("CCENTROCONTABLE").getValor() + "AP" + datosRequi.getcEjercicio() + seqValue;
                datosRequi.setnFolioApartado(folio);
                datosRequi.setCaNoPreCompromiso(seqValue);
                //Crea encabezado y detalle
                manager.creaEncabDetApartado(conn, datosRequi, usuario);
            }
            jsonObj.put("msg", "Datos Guardados.\nFavor de generar la integraci\u00f3n de requisiciones para el layout de apartado");
            conn.commit();
        } catch (Exception e) {
            try {
                jsonObj.put("Folio1", -1);
                jsonObj.put("Folio2", "");
                jsonObj.put("msg", (null == e.getMessage() ? "Error" : e.getMessage()));
                if (conn != null && !conn.isClosed()) {
                    conn.rollback();
                }
                log.error(e.getMessage(), e);
            } catch (SQLException e2) {
                log.error("Error occurred", "Error en el rollback " + e2);
            }
        } finally {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
            conn = null;
            cEjercicio = null;
            DATE_FORMAT = null;
            sdf = null;
            c1 = null;
            today = null;
            sequence = null;
        }
        return jsonObj;
    }

    public String devuelveApartado(HttpServletRequest req, DatosRequisicion datosRequi, HttpSession session, Usuario usuario) throws Exception {
        Connection conn = null;
        SolicitudManager manager = null;
        Requisition requisition = null;
        Caso c = null;
        Caso sc = null;
        QuestionnaireBussinessLogic contractBL = null;
        String msg = null;
        ContractQuestionnaire questionnaire = null;
        boolean isDebuger = true;
        try {
            conn = getConnection();
            manager = new SolicitudManager();
            requisition = RequisitionManager.readRequisition(conn, datosRequi.getcIdSolicitud());
            //Ejecutar las validaciones
            validacionesDevolverApartado(conn, manager, requisition);
            if (requisition.getIdEstado() == RequisitionStatus.REQUESTED) {
                //Si es estatus es 2
                //Se cambia el estatus de la requi y el EstadoPrecomprometido a 1
                RequisitionManager.setRequisitionStatus(conn, requisition, RequisitionStatus.CAPTURED, PrecomprometidoStatus.NO_REQUESTED);
                //Retrocede el caso
                c = manager.getCaso(session, conn);
                if (c == null) {
                    log.debug("fallo. Creando caso");
                    c = manager.iniciaCaso(datosRequi.getJndiName(), datosRequi.getTipoCaso(), datosRequi.getFolioGenerator(), usuario);
                    //casoOrigen = CASO_CREADO;
                    if (c == null) {
                        throw new Exception("No se pudo crear el caso");
                    }
                }
                sc = new Caso();
                sc.setIdCaso(c.getIdCaso());
                c = CasoManager.select(conn, sc);
                manager.avanzaCaso(req, c, usuario, datosRequi.getPrefixPath(), datosRequi.getResponsable(), datosRequi.getNombre(), datosRequi.getJndiName());
                session.setAttribute(GestionInterface.ATT_CASE, c);
                //sp_deleteEyDApartado solamente pone la req en estados 1. MF
                manager.resetLineasApartado(conn, datosRequi.getcIdSolicitud());
                manager.deleteDetalleApartado(conn, datosRequi.getcIdSolicitud());
                manager.deleteEncabezadoApartado(conn, datosRequi.getcIdSolicitud());
                //Bitácora
                Util.bitacoraMovimientos(datosRequi.getcIdSolicitud(), "DEVUELVE_REQUISICION a Captura", usuario.getLogin(), conn);
            } else if (requisition.getIdEstado() == RequisitionStatus.REQUESTED || isDebuger) {
                //Si es estatus es 3
                //Cancela el apartado contablemente
                contractBL = new QuestionnaireBussinessLogic();
                contractBL.setContractRequisition(requisition);
                contractBL.setUsuario(usuario);
                contractBL.canceledSecluded(conn);
                questionnaire = new ContractQuestionnaire();
                questionnaire.setIdRequest(requisition.getIdSolicitud());
                QuestionnaireManager.findCabinet(conn, questionnaire);
                QuestionnaireManager.deleteQuestionnaireFIEL(conn, requisition.getIdSolicitud());
                QuestionnaireManager.deleteQuestionnaire(conn, requisition.getIdSolicitud());
                QuestionnaireManager.deleteIMXPAgina(conn, questionnaire.getCabinetId());
                RequisitionManager.setRequisitionStatus(conn, requisition, RequisitionStatus.CAPTURED, PrecomprometidoStatus.NO_REQUESTED);
                manager.resetLineasApartado(conn, datosRequi.getcIdSolicitud());
                manager.resetConsecutivoApartado(conn, datosRequi.getcIdSolicitud());
            } else {
                throw new Exception("Estatus no valido para aplicar la devolución del apartado.");
            }
            msg = "Requisici\u00f3n devuelta correctamente.";
            conn.commit();
            return msg;
        } catch (Exception e) {
            try {
                if (conn != null)
                    conn.rollback();
                log.error(e.getMessage(), e);
            } catch (SQLException e2) {
                // TODO: handle exception
                log.error(e2.getMessage(), e2);
            }
            throw (e);
        } finally {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
            conn = null;
            manager = null;
            requisition = null;
            sc = null;
            contractBL = null;
            questionnaire = null;
        }
    }

    private void validacionesDevolverApartado(Connection conn, SolicitudManager manager, Requisition requisition) throws Exception {
        if (requisition.getIdEstado() == RequisitionStatus.CAPTURED) {
            throw new Exception("No se puede devolver porque el estatus de la requisición es CAPTURA.");
        } else {
            if (RequisitionType.DE_TIENDA_DIGITAL.equalsIgnoreCase(requisition.getIdTipoSolicitud())) {
                //Validar si es una requi de tienda digital
                if (requisition.getIdEstado() == RequisitionStatus.APPROVED) {
                    throw new Exception("No se puede devolver por cuestión de que el apartado se sube a SICOP.");
                }
                //validar que no este integrada la requi
                if (manager.requiIntegrada(conn, requisition.getIdSolicitud())) {
                    throw new Exception("No se puede devolver porque ya está integrada la requisici\\u00f3n");
                }
            } else if (RequisitionType.DE_MODIFICACION.equalsIgnoreCase(requisition.getIdTipoSolicitud())) {
                //Validar si es una requi de modificacion
                //Validar si existe convenio capturado
                if (manager.requiIsConvenio(conn, requisition.getIdSolicitud())) {
                    throw new Exception("No se puede devolver la requicision porque esta asociada a un contrato modificatorio");
                }
            } else {
                //Validar que no este en un consolidado
                if (manager.requiIsConsolidado(conn, requisition.getIdSolicitud())) {
                    throw new Exception("No se puede devolver el apartado porque ya está en un Consolidado");
                }
            }
        }
    }
}
