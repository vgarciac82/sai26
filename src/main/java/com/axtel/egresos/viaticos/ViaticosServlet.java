package com.axtel.egresos.viaticos;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.fileupload2.core.FileItem;
import org.apache.commons.lang.StringUtils;
import com.syc.cfdi.utils.FacturaUtils;
import com.syc.egresos.DetallePago;
import com.syc.egresos.PagoCalendarioBussinessLogic;
import com.syc.egresos.ResponseJSON;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.servlet.ResponseSender;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

@WebServlet(name = "ViaticosServlet", urlPatterns = { "/viaticos/guardarAgenda", "/viaticos/eliminarAgenda", "/viaticos/guardarTransporte", "/viaticos/eliminarTransporte", "/viaticos/guardarTramite", "/viaticos/resumenViaticos", "/viaticos/avanzaFirmante", "/viaticos/generaSolicitud", "/viaticos/iniciaCasoRG", "/viaticos/actualizarAgenda", "/viaticos/agregaBoletoAvion", "/viaticos/eliminarBoletoAvion", "/viaticos/borrarTodo", "/viaticos/autorizarViaticos", "/viaticos/rechazarViaticos", "/viaticos/rechazarAgenda", "/viaticos/actualizaNombreComision", "/viaticos/adjuntaJustificacion", "/viaticos/adjuntaBoletos", "/viaticos/actualizarJustificacion", "/viaticos/actualizarOtraJustif", "/viaticos/actualizarJustTickets", "/viaticos/actualizarJustGasolina", "/viaticos/agregaRetenciones", "/viaticos/finalizaTramite", "/viaticos/guardaCalendario", "/viaticos/actualizarTransporte", "/viaticos/enviarFirmarAgenda", "/viaticos/enviarFirmar", "/viaticos/cancelaComision", "/viaticos/validaPartidasViatico", "/viaticos/agregaDetalle" })
public class ViaticosServlet extends HttpServlet implements GestionInterface {

    private static final long serialVersionUID = 6125237145437183223L;

    private static final String GUARDAR_AGENDA = "guardarAgenda";

    private static final String ELIMINAR_AGENDA = "eliminarAgenda";

    private static final String GUARDAR_TRANSPORTE = "guardarTransporte";

    private static final String ELIMINAR_TRANSPORTE = "eliminarTransporte";

    private static final String AUTORIZACIONES_COMISION = "guardarTramite";

    private static final String GENERA_TRAMITE = "generaSolicitud";

    private static final String AVANZA_FIRMA = "avanzaFirmante";

    private static final String AUTORIZAR_VIATICOS = "autorizarViaticos";

    private static final String RECHAZAR_VIATICOS = "rechazarViaticos";

    private static final String RECHAZAR_AGENDA = "rechazarAgenda";

    private static final String AGREGA_BOLETOS = "agregaBoletoAvion";

    private static final String ELIMINA_BOLETO = "eliminarBoletoAvion";

    private static final String INICIA_RG = "iniciaCasoRG";

    private static final String GUARDA_CALENDARIO = "guardaCalendario";

    private static final String ACTUALIZAR_AGENDA = "actualizarAgenda";

    private static final String ACTUALIZAR_TRANSPORTE = "actualizarTransporte";

    private static final String BORRAR_DATOS = "borrarTodo";

    private static final String ENVIAR_FIRMAR = "enviarFirmarAgenda";

    private static final String EDITAR_ANTES_AUTORIZAR = "enviarFirmar";

    private static final String AGREGA_RETENCIONES = "agregaRetenciones";

    private static final String ACTUALIZA_NOMBRE = "actualizaNombreComision";

    private static final String FINALIZA_COMISION = "finalizaTramite";

    private static final String CANCELA_COMISION = "cancelaComision";

    private static final String INSERTA_JUSTICACION = "adjuntaJustificacion";

    private static final String ADJUNTA_PASES = "adjuntaBoletos";

    private static final String ACTUALIZAR_JUST_BOLETOS = "actualizarJustificacion";

    private static final String ACTUALIZAR_JUST_TICKET = "actualizarJustTickets";

    private static final String ACTUALIZAR_OTRA_JUST = "actualizarOtraJustif";

    private static final String ACTUALIZAR_JUST_GAS = "actualizarJustGasolina";

    private static final String VALIDAR_PARTIDAS = "validaPartidasViatico";

    public static final Logger log = LoggerFactory.getLogger(ViaticosServlet.class);

    private String jniName = "";

    private String reportPath = "";

    private static String TEMP_DIR = "";

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null) {
            ResponseSender.sendClientSimpleMessage(resp, false, "Su sesion a caducado. Ingrese nuevamente al sistema ");
            return;
        }
        Usuario u = (Usuario) session.getAttribute(ATT_USER);
        if (u == null) {
            ResponseSender.sendClientSimpleMessage(resp, false, "Su sesion a caducado. Ingrese nuevamente al sistema ");
            return;
        }
        String accion = req.getRequestURI().substring(req.getRequestURI().lastIndexOf("/") + 1);
        Agenda agenda = null;
        ViaticosBusinessLogic vbl = new ViaticosBusinessLogic(jniName);
        Viaticos viatico;
        Comision comision;
        TransporteOficial transporte;
        TransporteAereo avion;
        boolean insertados = false;
        try {
            if (GUARDAR_AGENDA.equals(accion)) {
                int folioAgenda = vbl.lastId("Agenda");
                if ("EDITAR".equals(req.getParameter("tipoOperacion"))) {
                    //Nuevo registro
                    agenda = vbl.cargaInstanciaAgenda(req, folioAgenda);
                    comision = vbl.cargaInstanciaComisionEdicion(req, u);
                    viatico = vbl.cargaInstanciaViaticos(req, folioAgenda);
                    insertados = vbl.actualizarAgenda(agenda, viatico, comision, u.getLogin());
                } else if ("EDICION_SIN_FIRMAS".equals(req.getParameter("tipoOperacion"))) {
                    comision = vbl.cargaInstanciaEdicionSinFirmas(req, u);
                    agenda = vbl.cargaInstanciaAgenda(req, folioAgenda);
                    viatico = vbl.cargaInstanciaViaticos(req, folioAgenda);
                    insertados = vbl.actualizarAgenda(agenda, viatico, comision, u.getLogin());
                } else {
                    //Crear nueva Agenda
                    comision = vbl.cargaInstanciaComision(req, u);
                    agenda = vbl.cargaInstanciaAgenda(req, folioAgenda);
                    viatico = vbl.cargaInstanciaViaticos(req, folioAgenda);
                    int tieneExcepcionMesesAnteriores = Integer.parseInt(req.getParameter("tieneExepcionMA"));
                    insertados = vbl.guardarAgenda(agenda, viatico, comision, tieneExcepcionMesesAnteriores);
                }
                ResponseJSON responseJSON = new ResponseJSON(true, null, Arrays.asList((new String[] { String.valueOf(insertados) })));
                sendJSONResponse(resp, responseJSON);
            } else if (ELIMINAR_AGENDA.equals(accion)) {
                int eliminados = 0;
                int folioAgenda = Integer.parseInt(req.getParameter("folioA"));
                String tipoOperacion = (req.getParameter("tipoOperacion") != null ? req.getParameter("tipoOperacion") : req.getParameter("operacion"));
                int idEmpleado = 0;
                if ("EDITAR".equals(tipoOperacion) || "EDICION_SIN_FIRMAS".equals(tipoOperacion)) {
                    idEmpleado = vbl.consultaEmpleado(folioAgenda);
                    agenda = vbl.cargaInstanciaAgendaBorrar(req, folioAgenda, idEmpleado);
                }
                eliminados = vbl.eliminarAgenda(folioAgenda, u.getLogin(), tipoOperacion, agenda, idEmpleado);
                ResponseJSON responseJSON = new ResponseJSON(true, null, Arrays.asList((new String[] { String.valueOf(eliminados) })));
                sendJSONResponse(resp, responseJSON);
            } else if (GUARDAR_TRANSPORTE.equals(accion)) {
                transporte = vbl.cargaInstanciaTransporte(req);
                insertados = vbl.guardarTransporte(transporte);
                if ("EDITAR".equals(req.getParameter("tipoOperacion")) || "EDICION_SIN_FIRMAS".equals(req.getParameter("operacion"))) {
                    vbl.actualizarComision(transporte.getIdComision());
                }
                ResponseJSON responseJSON = new ResponseJSON(true, null, Arrays.asList((new String[] { String.valueOf(insertados) })));
                sendJSONResponse(resp, responseJSON);
            } else if (ELIMINAR_TRANSPORTE.equals(accion)) {
                int eliminados = 0;
                int folioAgenda = Integer.parseInt(req.getParameter("folioA"));
                int folioComision = Integer.parseInt(req.getParameter("idComision"));
                eliminados = vbl.eliminarTransporte(folioAgenda, folioComision);
                ResponseJSON responseJSON = new ResponseJSON(true, null, Arrays.asList((new String[] { String.valueOf(eliminados) })));
                sendJSONResponse(resp, responseJSON);
            } else if (AUTORIZACIONES_COMISION.equals(accion)) {
                String mensaje = "";
                int folioComision = Integer.parseInt(req.getParameter("idComision"));
                comision = vbl.cargaInstanciaComision(req, u);
                //agenda = vbl.consultaAgendas(folioComision);
                int tieneBoleto = Integer.parseInt(req.getParameter("hasTicket"));
                String justificaBoletos = req.getParameter("jBoleto");
                mensaje = vbl.guardarComision(folioComision, comision, tieneBoleto, justificaBoletos);
                ResponseJSON responseJSON = new ResponseJSON(true, null, Arrays.asList((new String[] { String.valueOf(mensaje) })));
                sendJSONResponse(resp, responseJSON);
            } else if (AVANZA_FIRMA.equals(accion)) {
                String actualizado = "";
                int folioComision = Integer.parseInt(req.getParameter("idComision"));
                int idEstatus = Integer.parseInt(req.getParameter("nIdEstatus"));
                comision = vbl.cargaInstanciaComision(req, u);
                boolean existe = vbl.existenMasFirmantes(folioComision);
                if (existe) {
                    actualizado = vbl.actualizaEstatus(folioComision, Integer.parseInt(u.getNumeroEmpleado()));
                } else {
                    //Agenda agendas = vbl.consultaAgendas(folioComision);
                    actualizado = vbl.finalizaTramite(folioComision, idEstatus, Integer.parseInt(u.getNumeroEmpleado()), u.getLogin());
                }
                ResponseJSON responseJSON = new ResponseJSON(true, null, Arrays.asList((new String[] { String.valueOf(actualizado) })));
                sendJSONResponse(resp, responseJSON);
            } else if (AGREGA_BOLETOS.equals(accion)) {
                String mensaje = "";
                avion = vbl.cargaInstanciaTransporteAereo(req);
                int actualizados = vbl.agregaBoleto(avion);
                if (actualizados > 0)
                    mensaje = "Se agregó correctamente el boleto de avión";
                else
                    mensaje = "Ocurrio un error, No se pudo agregar el boleto verifique con el administrador";
                ResponseJSON responseJSON = new ResponseJSON(true, null, Arrays.asList((new String[] { String.valueOf(mensaje) })));
                sendJSONResponse(resp, responseJSON);
            } else if (ELIMINA_BOLETO.equals(accion)) {
                String mensaje = "";
                avion = vbl.cargaInstanciaTransporteAereo(req);
                int actualizados = vbl.eliminaBoletoAvion(avion);
                if (actualizados > 0)
                    mensaje = "Se elimino correctamente el boleto de avión";
                else
                    mensaje = "Ocurrio un error, No se pudo eliminar el boleto verifique con el administrador";
                ResponseJSON responseJSON = new ResponseJSON(true, null, Arrays.asList((new String[] { String.valueOf(mensaje) })));
                sendJSONResponse(resp, responseJSON);
            } else if (INICIA_RG.equals(accion)) {
                comision = vbl.consultaComision(req, Integer.parseInt(req.getParameter("idComision")));
                agenda = vbl.consultaAgenda(req, comision.getIdComision());
                int tipoSolicitud = Integer.parseInt(req.getParameter("nidTipo"));
                String idEvento = req.getParameter("cEvento");
                comision.setEvento(idEvento);
                comision.setTotalAgenda(new BigDecimal((req.getParameter("mTotal1") == null ? "0" : req.getParameter("mTotal1"))));
                comision.setTotalTransporte(new BigDecimal(req.getParameter("mTotalLocal") == null ? "0" : req.getParameter("mTotalLocal")));
                comision.setFolioReemplazo(Integer.parseInt((req.getParameter("folioReemplazo") == null || req.getParameter("folioReemplazo") == "") ? "0" : req.getParameter("folioReemplazo")));
                Caso casoRG = vbl.generaCasoRG(comision, agenda, tipoSolicitud, u, idEvento);
                session.setAttribute(GestionInterface.ATT_CASE, casoRG);
                ResponseJSON responseJSON = new ResponseJSON(true, null, Arrays.asList((new String[] { String.valueOf(Util.folio(casoRG)) })));
                sendJSONResponse(resp, responseJSON);
            } else if (GUARDA_CALENDARIO.equals(accion)) {
                String mensaje = "";
                String tipoConcepto = req.getParameter("cEvento");
                String evento = vbl.consultaEvento();
                if (evento.equals(tipoConcepto)) {
                    tipoConcepto = "DDV";
                } else {
                    tipoConcepto = "PN";
                }
                DetallePago renglon = new DetallePago();
                renglon.setEp(req.getParameter("ep"));
                renglon.setTipoPago(req.getParameter("cTipoPago"));
                renglon.setFolioPago(Integer.parseInt(req.getParameter("nFolioPago")));
                renglon.setImporteBruto(new BigDecimal((req.getParameter("montoEjercer") == null ? "0" : req.getParameter("montoEjercer"))));
                renglon.setIdTipoConcepto(tipoConcepto);
                renglon.setIdTipoMovimiento("000");
                renglon.setImporteRetencion(new BigDecimal((req.getParameter("montoRetencion") == null ? "0" : req.getParameter("montoRetencion"))));
                PagoCalendarioBussinessLogic pcbl = new PagoCalendarioBussinessLogic(jniName);
                int insertado = pcbl.insertaCalendarioPagoDisponible(renglon);
                if (insertado > 0) {
                    mensaje = "Se agregó el calendario.";
                }
                ResponseJSON responseJSON = new ResponseJSON(true, null, Arrays.asList((new String[] { String.valueOf(mensaje) })));
                sendJSONResponse(resp, responseJSON);
            } else if (GENERA_TRAMITE.equals(accion)) {
                comision = vbl.consultaComision(req, Integer.parseInt(req.getParameter("idComision")));
                agenda = vbl.consultaAgenda(req, comision.getIdComision());
                Caso casoGenerado = vbl.generaTramite(comision, agenda, req, reportPath, u);
                session.setAttribute(GestionInterface.ATT_CASE, casoGenerado);
                ResponseJSON responseJSON = new ResponseJSON(true, null, Arrays.asList((new String[] { String.valueOf(Util.folio(casoGenerado)) })));
                sendJSONResponse(resp, responseJSON);
            } else if (ACTUALIZAR_AGENDA.equals(accion)) {
                String mensaje = "";
                String tipoOperacion = req.getParameter("tipoOperacion");
                int folioAgenda = 0;
                //Actualiza la agenda existente
                if (tipoOperacion.equals("EDICION_SIN_FIRMAS")) {
                    folioAgenda = Integer.parseInt(req.getParameter("idAgenda"));
                } else {
                    folioAgenda = Integer.parseInt(req.getParameter("nidAgenda"));
                }
                comision = vbl.cargaInstanciaComisionEdicion(req, u);
                agenda = vbl.cargaInstanciaAgenda(req, folioAgenda);
                viatico = vbl.cargaInstanciaViaticos(req, folioAgenda);
                vbl.editarAgenda(comision, agenda, viatico, u.getLogin());
                ResponseJSON responseJSON = new ResponseJSON(true, null, Arrays.asList((new String[] { String.valueOf(mensaje) })));
                sendJSONResponse(resp, responseJSON);
            } else if (ACTUALIZAR_TRANSPORTE.equals(accion)) {
                String mensaje = "";
                transporte = vbl.cargaInstanciaTransporte(req);
                int folioComision = Integer.parseInt(req.getParameter("id"));
                vbl.actualizarTransporte(transporte, folioComision);
                ResponseJSON responseJSON = new ResponseJSON(true, null, Arrays.asList((new String[] { String.valueOf(mensaje) })));
                sendJSONResponse(resp, responseJSON);
            } else if (ACTUALIZA_NOMBRE.equals(accion)) {
                String mensaje = "";
                int folioComision = Integer.parseInt(req.getParameter("idComision"));
                String nuevoNombre = req.getParameter("nombreComision");
                int idNombre = Integer.parseInt(req.getParameter("idNombre"));
                vbl.editarNombreAgenda(folioComision, nuevoNombre, idNombre);
                ResponseJSON responseJSON = new ResponseJSON(true, null, Arrays.asList((new String[] { String.valueOf(mensaje) })));
                sendJSONResponse(resp, responseJSON);
            } else if (ENVIAR_FIRMAR.equals(accion)) {
                String mensaje = "";
                int idcomision = Integer.parseInt(req.getParameter("id"));
                int noEmpleado = Integer.parseInt(req.getParameter("noEmpleadoComision"));
                String tipoOper = req.getParameter("tipoOperacion");
                agenda = vbl.consultaAgenda(req, idcomision);
                vbl.editarFirmarAgenda(idcomision, noEmpleado, tipoOper, agenda.getIdPais());
                ResponseJSON responseJSON = new ResponseJSON(true, null, Arrays.asList((new String[] { String.valueOf(mensaje) })));
                sendJSONResponse(resp, responseJSON);
            } else if (BORRAR_DATOS.equals(accion)) {
                String mensaje = "";
                int idcomision = Integer.parseInt(req.getParameter("id"));
                int folioPago = Integer.parseInt(req.getParameter("folioPago"));
                vbl.eliminarSolicitud(idcomision, folioPago);
                ResponseJSON responseJSON = new ResponseJSON(true, null, Arrays.asList((new String[] { String.valueOf(mensaje) })));
                sendJSONResponse(resp, responseJSON);
            } else if (AGREGA_RETENCIONES.equals(accion)) {
                String mensaje = "";
                vbl.agregarRetenciones(req);
                mensaje = "Se agregaron las retenciones correctamente";
                ResponseJSON responseJSON = new ResponseJSON(true, null, Arrays.asList((new String[] { String.valueOf(mensaje) })));
                sendJSONResponse(resp, responseJSON);
            } else if (EDITAR_ANTES_AUTORIZAR.equals(accion)) {
                String mensaje = "";
                int idcomision = Integer.parseInt(req.getParameter("id"));
                int noEmpleado = Integer.parseInt(req.getParameter("noEmpleadoComision"));
                agenda = vbl.consultaAgenda(req, idcomision);
                vbl.editarSinFirmar(idcomision, noEmpleado, agenda.getIdPais(), 0, "");
                ResponseJSON responseJSON = new ResponseJSON(true, null, Arrays.asList((new String[] { String.valueOf(mensaje) })));
                sendJSONResponse(resp, responseJSON);
            } else if (RECHAZAR_AGENDA.equals(accion)) {
                String actualizado = "";
                int folio = Integer.parseInt(req.getParameter("idComision"));
                String motivo = req.getParameter("cMotivoRechazo");
                actualizado = vbl.rechazaComisionConMotivo(folio, motivo, u.getLogin());
                ResponseJSON responseJSON = new ResponseJSON(true, null, Arrays.asList((new String[] { String.valueOf(actualizado) })));
                sendJSONResponse(resp, responseJSON);
            } else if (FINALIZA_COMISION.equals(accion)) {
                int folio = Integer.parseInt(req.getParameter("idComision"));
                int numEmpleado = Integer.parseInt(req.getParameter("nEmpleado"));
                vbl.finalizaComision(folio, numEmpleado, u.getLogin());
                ResponseJSON responseJSON = new ResponseJSON(true, null, Arrays.asList((new String[] { String.valueOf("") })));
                sendJSONResponse(resp, responseJSON);
            } else if (CANCELA_COMISION.equals(accion)) {
                String cancelado = "";
                int folio = Integer.parseInt(req.getParameter("idComision"));
                cancelado = vbl.cancelaComision(folio, u.getLogin());
                ResponseJSON responseJSON = new ResponseJSON(true, null, Arrays.asList((new String[] { String.valueOf(cancelado) })));
                sendJSONResponse(resp, responseJSON);
            } else if (VALIDAR_PARTIDAS.equals(accion)) {
                String mensaje = "";
                int folio = Integer.parseInt(req.getParameter("folio"));
                vbl.validaPartidasComprobacion(folio);
                ResponseJSON responseJSON = new ResponseJSON(true, null, Arrays.asList((new String[] { String.valueOf(mensaje) })));
                sendJSONResponse(resp, responseJSON);
            } else if (INSERTA_JUSTICACION.equals(accion)) {
                List<?> fileItems = null;
                Iterator<?> iter = null;
                DataInputStream archivoCargaStream = null;
                String nombreDestino = "";
                Map<String, String> datosCarga = new HashMap<String, String>();
                try {
                    fileItems = Util.parseRequest(req, ViaticosServlet.TEMP_DIR, -1);
                    iter = fileItems.iterator();
                    String nombreArchivo = "";
                    while (iter.hasNext()) {
                        FileItem item = (FileItem) iter.next();
                        if (item.isFormField()) {
                            datosCarga.put(item.getFieldName(), item.getString());
                            item.delete();
                            continue;
                        }
                        archivoCargaStream = new DataInputStream(item.getInputStream());
                        nombreArchivo = item.getName();
                        if (StringUtils.isBlank(nombreArchivo)) {
                            item.delete();
                            continue;
                        }
                        String extension = Util.getFileExtencion(nombreArchivo);
                        nombreDestino = FacturaUtils.generaNombreArchivoTemporal(ViaticosServlet.TEMP_DIR, "JustificaTickets", extension);
                        log.info("Object: {}", "Copiando archivo :" + nombreArchivo);
                        Util.copiaArchivo(archivoCargaStream, nombreDestino);
                        datosCarga.put(item.getFieldName(), nombreDestino);
                        item.delete();
                    }
                    vbl.cargaJustificacion(u, datosCarga);
                    Map<String, String> result = new HashMap<>();
                    result.put("success", "true");
                    result.put("message", "El archivo se adjuntó correctamente.");
                    Util.sendJSON(resp, result);
                } finally {
                    if (archivoCargaStream != null)
                        try {
                            archivoCargaStream.close();
                        } catch (Exception e) {
                            log.error("Error occurred", "Error cerrando flujo DataInputStream" + e);
                        }
                    archivoCargaStream = null;
                    if (!"".equals(nombreDestino)) {
                        File toDelete = new File(nombreDestino);
                        if (!toDelete.delete())
                            toDelete.deleteOnExit();
                    }
                }
            } else if (ACTUALIZAR_JUST_BOLETOS.equals(accion)) {
                String respuesta = "";
                String folio = req.getParameter("folio");
                String mensaje = req.getParameter("msjBoletos");
                vbl.actualizarJustificacionBoletos(folio, mensaje);
                ResponseJSON responseJSON = new ResponseJSON(true, null, Arrays.asList((new String[] { String.valueOf(respuesta) })));
                sendJSONResponse(resp, responseJSON);
            } else if (ACTUALIZAR_JUST_TICKET.equals(accion)) {
                String respuesta = "";
                String folio = req.getParameter("folio");
                String mensaje = req.getParameter("msjJustificacion");
                vbl.actualizarJustificacionTicket(folio, mensaje);
                ResponseJSON responseJSON = new ResponseJSON(true, null, Arrays.asList((new String[] { String.valueOf(respuesta) })));
                sendJSONResponse(resp, responseJSON);
            } else if (ACTUALIZAR_OTRA_JUST.equals(accion)) {
                String respuesta = "";
                String folio = req.getParameter("folio");
                String mensaje = req.getParameter("cOtraJustificacion");
                vbl.actualizarOtraJustificacion(folio, mensaje);
                ResponseJSON responseJSON = new ResponseJSON(true, null, Arrays.asList((new String[] { String.valueOf(respuesta) })));
                sendJSONResponse(resp, responseJSON);
            } else if (ACTUALIZAR_JUST_GAS.equals(accion)) {
                String respuesta = "";
                vbl.actualizarJustificacionGasolina(req);
                ResponseJSON responseJSON = new ResponseJSON(true, null, Arrays.asList((new String[] { String.valueOf(respuesta) })));
                sendJSONResponse(resp, responseJSON);
            } else if (ADJUNTA_PASES.equals(accion)) {
                List<?> fileItems = null;
                Iterator<?> iter = null;
                DataInputStream archivoCargaStream = null;
                String nombreDestino = "";
                Map<String, String> datosCarga = new HashMap<String, String>();
                try {
                    fileItems = Util.parseRequest(req, ViaticosServlet.TEMP_DIR, -1);
                    iter = fileItems.iterator();
                    String nombreArchivo = "";
                    while (iter.hasNext()) {
                        FileItem item = (FileItem) iter.next();
                        if (item.isFormField()) {
                            datosCarga.put(item.getFieldName(), item.getString());
                            item.delete();
                            continue;
                        }
                        archivoCargaStream = new DataInputStream(item.getInputStream());
                        nombreArchivo = item.getName();
                        if (StringUtils.isBlank(nombreArchivo)) {
                            item.delete();
                            continue;
                        }
                        String extension = Util.getFileExtencion(nombreArchivo);
                        nombreDestino = FacturaUtils.generaNombreArchivoTemporal(ViaticosServlet.TEMP_DIR, "JustificaBoletos", extension);
                        log.info("Object: {}", "Copiando archivo :" + nombreArchivo);
                        Util.copiaArchivo(archivoCargaStream, nombreDestino);
                        datosCarga.put(item.getFieldName(), nombreDestino);
                        item.delete();
                    }
                    vbl.cargaPasesAbordar(u, datosCarga);
                    Map<String, String> result = new HashMap<>();
                    result.put("success", "true");
                    result.put("message", "El archivo se adjuntó correctamente.");
                    Util.sendJSON(resp, result);
                } finally {
                    if (archivoCargaStream != null)
                        try {
                            archivoCargaStream.close();
                        } catch (Exception e) {
                            log.error("Error occurred", "Error cerrando flujo DataInputStream" + e);
                        }
                    archivoCargaStream = null;
                    if (!"".equals(nombreDestino)) {
                        File toDelete = new File(nombreDestino);
                        if (!toDelete.delete())
                            toDelete.deleteOnExit();
                    }
                }
            }
        } catch (Exception e) {
            log.error("Ocurrio el siguiente error: " + e, e);
            List<String> errores = new ArrayList<String>();
            errores.add(e.toString());
            ResponseJSON responseJSON = new ResponseJSON(false, errores, null);
            try {
                sendJSONResponse(resp, responseJSON);
            } catch (Exception e2) {
                throw new ServletException(e2);
            }
        }
    }

    private void sendJSONResponse(HttpServletResponse resp, ResponseJSON responseJSON) throws Exception {
        PrintWriter out = null;
        try {
            out = resp.getWriter();
            resp.getWriter();
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            out.print(responseJSON.toJSON());
            out.flush();
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                if (out != null)
                    out.close();
            } catch (Exception e2) {
                log.warn("Object: {}", "Problemas cerrando flujo: " + e2);
            }
        }
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try {
            InitialContext ic = new InitialContext();
            jniName = (String) ic.lookup("java:comp/env/dataSourceRefName");
            reportPath = getServletContext().getRealPath("Reportes" + File.separator);
            log.info("Object: {}", "Se genero REPORT PATH " + reportPath);
            if (jniName == null) {
                jniName = "jdbc/gestion";
                log.info("Object: {}", "Environment Entry \"dataSourceRefName\" nula usando default \"" + jniName + "\"");
            } else
                log.info("Object: {}", "dataSourceRefName=" + jniName);
        } catch (NamingException exc) {
            jniName = "jdbc/gestion";
            log.info("Object: {}", "Environment Entry \"dataSourceRefName\" no definida usando default \"" + jniName + "\"");
        }
        try {
            InitialContext ic = new InitialContext();
            TEMP_DIR = (String) ic.lookup("java:comp/env/TemporaryDirectory");
            if (TEMP_DIR == null) {
                TEMP_DIR = "../upload/";
                log.info("Object: {}", "Environment Entry \"TEMP_DIR\" nula usando default \"" + TEMP_DIR + "\"");
            } else
                log.info("Object: {}", "dataSourceRefName=" + TEMP_DIR);
        } catch (NamingException exc) {
            TEMP_DIR = "../upload/";
            log.info("Error occurred", "Ocurrio un error que evito que se cargara la entrada \"TEMP_DIR\"" + exc);
            log.info("Object: {}", "Environment Entry \"dataSourceRefName\" no definida usando default \"" + TEMP_DIR + "\"");
        }
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null) {
            ResponseSender.sendClientSimpleMessage(resp, false, "Su sesion a caducado. Ingrese nuevamente al sistema ");
            return;
        }
        String accion = req.getRequestURI().substring(req.getRequestURI().lastIndexOf("/") + 1);
        ViaticosBusinessLogic vbl = new ViaticosBusinessLogic(jniName);
        int numEmpleado = Integer.parseInt(req.getParameter("nEmpleadoUsuario"));
        String login = req.getParameter("u_Login");
        try {
            if (AUTORIZAR_VIATICOS.equals(accion)) {
                String actualizado = "";
                String[] folios = req.getParameter("nFolios").split(",");
                int idEstatus = 0;
                for (String s : folios) {
                    int folio = Integer.parseInt(s);
                    idEstatus = vbl.estatusSiguiente(folio, numEmpleado);
                    boolean existe = vbl.existenMasFirmantes(folio);
                    idEstatus++;
                    if (existe) {
                        // Enviar correo al siguiente autorizador y actualiza estatus
                        actualizado = vbl.actualizaEstatusMasivo(folio, numEmpleado, idEstatus);
                    } else {
                        //						Agenda agenda = vbl.consultaAgendas(folio);
                        actualizado = vbl.finalizaTramite(folio, idEstatus, numEmpleado, login);
                    }
                }
                ResponseJSON responseJSON = new ResponseJSON(true, null, Arrays.asList((new String[] { String.valueOf(actualizado) })));
                sendJSONResponse(resp, responseJSON);
            } else if (RECHAZAR_VIATICOS.equals(accion)) {
                String actualizado = "";
                String[] folios = req.getParameter("nFolios").split(",");
                for (String s : folios) {
                    int folio = Integer.parseInt(s);
                    actualizado = vbl.rechazaComision(folio, login);
                }
                ResponseJSON responseJSON = new ResponseJSON(true, null, Arrays.asList((new String[] { String.valueOf(actualizado) })));
                sendJSONResponse(resp, responseJSON);
            }
        } catch (Exception e) {
            log.error("Ocurrio el siguiente error: " + e, e);
            List<String> errores = new ArrayList<String>();
            errores.add(e.toString());
            ResponseJSON responseJSON = new ResponseJSON(false, errores, null);
            try {
                sendJSONResponse(resp, responseJSON);
            } catch (Exception e2) {
                throw new ServletException(e2);
            }
        }
    }
}
