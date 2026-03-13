package com.syc.adquisiciones.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.axtel.contratos.ContractStatus;
import com.axtel.contratos.core.Contrato;
import com.axtel.contratos.core.ConvenioCap4;
import com.axtel.contratos.core.DatosContratoPSP;
import com.axtel.contratos.core.Garantia;
import com.axtel.contratos.core.GarantiaContrato;
import com.axtel.contratos.core.LiberaGarantiaContrato;
import com.axtel.contratos.core.OtrosImpuestos;
import com.axtel.contratos.core.PartidasConvenioCap4;
import com.axtel.contratos.core.ProcedimientoAdjudicacion;
import com.syc.adquisiciones.ContratoArt25;
import com.syc.adquisiciones.DatosEP_TMP;
import com.syc.adquisiciones.businessLogic.ContratacionFormalizadaBusinessLogic;
import com.syc.adquisiciones.core.ContratoModificado;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "ContratacionFormalizadaServlet", urlPatterns = { "/ContratacionFormalizadaServlet" })
public class ContratacionFormalizadaServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static Logger log = LoggerFactory.getLogger(ContratacionFormalizadaServlet.class);

    private static String jndiName = null;

    private static String prefixPath = null;

    private String folioGenerator = null;

    private static String tempDir = "";

    public void destroy() {
        // Just puts "destroy" string in log
        super.destroy();
        // Put your code here
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Usuario usuario = null;
        if (session == null) {
            log.warn("No hay sesion");
            response.sendRedirect("../index.jsp");
            return;
        }
        usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
        int tipoProceso = (request.getParameter("tipoProceso") == null || "".equalsIgnoreCase(request.getParameter("tipoProceso")) ? -1 : Integer.parseInt(request.getParameter("tipoProceso")));
        log.debug("Proceso: " + tipoProceso);
        prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
        switch(tipoProceso) {
            case //Contratos
            1:
                contratos(request, response, usuario, session);
                break;
            case //Contratos remanentes
            2:
                contratosRemanentes(request, response, usuario);
                break;
            case //Convenios de contratos cap 4000
            3:
                conveniosContratosCap4(request, response, usuario, session);
                break;
            case //Contratos Art 25
            4:
                contratosArt25(request, response, usuario, session);
                break;
            case //Garantias
            5:
                garantias(request, response, usuario, session);
                break;
            case //convenios
            6:
                conveniosContratos(request, response, usuario, session);
                break;
            default:
                log.warn("Proceso desconocido");
                break;
        }
    }

    protected void garantias(HttpServletRequest request, HttpServletResponse response, Usuario usuario, HttpSession session) throws UnsupportedEncodingException {
        int tipoOperacion = 0;
        PrintWriter out = null;
        ContratacionFormalizadaBusinessLogic business = null;
        JSONObject jsonObj = null;
        JSONArray arrayObj = null;
        boolean respuesta = false;
        String mensaje = null;
        String cIdContratoDef = null;
        GarantiaContrato garantia = null;
        List<?> fileItems = null;
        Iterator<?> iter = null;
        LiberaGarantiaContrato liberaGarantia = null;
        try {
            tipoOperacion = (request.getParameter("tipoOperacion") == null || "".equalsIgnoreCase(request.getParameter("tipoOperacion")) ? -1 : Integer.parseInt(request.getParameter("tipoOperacion")));
            cIdContratoDef = (request.getParameter("cIdContratoDefinitivo") == null || "".equalsIgnoreCase(request.getParameter("cIdContratoDefinitivo")) ? "" : request.getParameter("cIdContratoDefinitivo"));
            business = new ContratacionFormalizadaBusinessLogic(jndiName);
            arrayObj = new JSONArray();
            out = response.getWriter();
            switch(tipoOperacion) {
                case //pestaña consulta
                1:
                    jsonObj = business.queryGarantias();
                    respuesta = true;
                    mensaje = "Consulta de datos";
                    break;
                case //Consulta pestaña captura
                2:
                    jsonObj = business.datosGarantias(cIdContratoDef);
                    respuesta = true;
                    mensaje = "Consulta de datos";
                    break;
                case //Guardar
                3:
                    fileItems = Util.parseRequest(request, ContratacionFormalizadaServlet.tempDir, -1);
                    iter = fileItems.iterator();
                    garantia = fillGarantiaContrato(request, usuario, iter);
                    if (garantia.getArchivoStream() != null && !"zip".equalsIgnoreCase(garantia.getcExtencion())) {
                        throw new Exception("No se puede procesar archivos [" + garantia.getcExtencion() + "] Corrija e intente de nuevo");
                    }
                    garantia.setcNombreArchivoDestino(ContratacionFormalizadaServlet.tempDir + "garantia" + System.currentTimeMillis() + "." + garantia.getcExtencion());
                    if (garantia.getnTipoProcesoGarantia() == GestionInterface.ENDOSO) {
                        garantia.setcNombreArchivoDestino(ContratacionFormalizadaServlet.tempDir + "endoso" + System.currentTimeMillis() + "." + garantia.getcExtencion());
                    }
                    business.saveGarantiaContrato(garantia);
                    jsonObj = business.datosGarantias(garantia.getcIdContratoDefinitivo());
                    respuesta = true;
                    mensaje = "Datos Guardados.";
                    break;
                case //pestaña Libera garantas
                4:
                    jsonObj = business.datosLiberaGarantia(cIdContratoDef);
                    respuesta = true;
                    mensaje = "Consulta de datos";
                    break;
                case 5:
                    fileItems = Util.parseRequest(request, ContratacionFormalizadaServlet.tempDir, -1);
                    iter = fileItems.iterator();
                    liberaGarantia = fillLiberaGarantiaContrato(request, usuario, iter);
                    if (liberaGarantia.getArchivoStream() != null && !"zip".equalsIgnoreCase(liberaGarantia.getcExtencion())) {
                        throw new Exception("No se puede procesar archivos [" + liberaGarantia.getcExtencion() + "] Corrija e intente de nuevo");
                    }
                    liberaGarantia.setcNombreArchivoDestino(ContratacionFormalizadaServlet.tempDir + "liberaGarantia" + System.currentTimeMillis() + "." + liberaGarantia.getcExtencion());
                    business.saveLiberaGarantiaContrato(liberaGarantia);
                    jsonObj = business.datosLiberaGarantia(liberaGarantia.getcIdContratoDefinitivo());
                    respuesta = true;
                    mensaje = "Datos Guardados.";
                    break;
                default:
                    log.warn("Operación desconocida");
                    break;
            }
        } catch (Exception e) {
            log.error(e);
            jsonObj = new JSONObject();
            respuesta = false;
            mensaje = e.getMessage();
        } finally {
            try {
                jsonObj.put("RESPUESTA", respuesta);
                jsonObj.put("MENSAJE", (mensaje == null ? "error" : mensaje));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String destino = new String(arrayObj.put(jsonObj).toString().getBytes("UTF-8"), "ISO-8859-1");
            out.println(destino);
            if (out != null) {
                out.flush();
                out.close();
            }
            if (garantia != null && !garantia.getListGarantia().isEmpty()) {
                garantia.getListGarantia().clear();
            }
            out = null;
            jsonObj = null;
            arrayObj = null;
            business = null;
            cIdContratoDef = null;
            garantia = null;
            fileItems = null;
            iter = null;
            liberaGarantia = null;
        }
    }

    protected void contratosArt25(HttpServletRequest request, HttpServletResponse response, Usuario usuario, HttpSession session) throws UnsupportedEncodingException {
        int tipoOperacion = 0;
        PrintWriter out = null;
        ContratacionFormalizadaBusinessLogic business = null;
        JSONObject jsonObj = null;
        JSONArray arrayObj = null;
        boolean respuesta = false;
        String mensaje = null;
        String cIdContratoDef = null;
        DatosEP_TMP datEP = null;
        ContratoArt25 cont = null;
        int nConsecutivoAmp = 0;
        try {
            tipoOperacion = (request.getParameter("tipoOperacion") == null || "".equalsIgnoreCase(request.getParameter("tipoOperacion")) ? -1 : Integer.parseInt(request.getParameter("tipoOperacion")));
            cIdContratoDef = (request.getParameter("cIdContratoDefinitivo") == null || "".equalsIgnoreCase(request.getParameter("cIdContratoDefinitivo")) ? "" : request.getParameter("cIdContratoDefinitivo"));
            nConsecutivoAmp = (request.getParameter("nConsecutivoAmpliacion") == null || "".equalsIgnoreCase(request.getParameter("nConsecutivoAmpliacion")) ? 0 : Integer.parseInt(request.getParameter("nConsecutivoAmpliacion")));
            business = new ContratacionFormalizadaBusinessLogic(jndiName);
            arrayObj = new JSONArray();
            out = response.getWriter();
            datEP = fillContEP(request);
            cont = new ContratoArt25();
            cont.setcIdContratoDef(cIdContratoDef);
            cont.setnIdConsecutivoAmpliacion(nConsecutivoAmp);
            cont.setcPrefixPath(prefixPath);
            cont.setJndiName(jndiName);
            switch(tipoOperacion) {
                case //Pestaña consulta
                1:
                    jsonObj = business.queryDataContArt25();
                    respuesta = true;
                    break;
                case //Consulta de datos migrados.
                2:
                    jsonObj = business.queryItemsContArt25(cIdContratoDef);
                    respuesta = true;
                    break;
                case //Add EP
                3:
                    jsonObj = new JSONObject();
                    business.addEp_tmp(datEP);
                    respuesta = true;
                    mensaje = "Clave presupuestal agregada.";
                    break;
                case // Pestaña presupuesto, Aprobar contrato
                4:
                    business.apruebaContratoArt25(cont, usuario);
                    jsonObj = business.queryItemsContArt25(cont.getcIdContratoDef());
                    respuesta = true;
                    mensaje = "Contrato aprobado.";
                    break;
                case //Pestaña presupuesto,  devolver contrato
                5:
                    business.devuelveContratoArt25(cont, usuario.getLogin());
                    jsonObj = business.queryItemsContArt25(cont.getcIdContratoDef());
                    respuesta = true;
                    mensaje = "El contrato se devuelve de forma correcta.";
                    break;
                case // Pestaña precompromiso, precomprometer contrato
                6:
                    business.generaPrecompromisoContArt25(cont, request, usuario);
                    jsonObj = business.queryItemsContArt25(cont.getcIdContratoDef());
                    respuesta = true;
                    mensaje = "Precompromiso generado correctamente";
                    break;
                case //Pestaña precompromiso,  devolver precompromiso contrato
                7:
                    business.cancelaPrecompromisoContArt25(cont, request, usuario);
                    jsonObj = business.queryItemsContArt25(cont.getcIdContratoDef());
                    respuesta = true;
                    mensaje = "Precompromiso cancelado correctamente";
                    break;
                case //Pestaña precompromiso,  autorizar contrato
                8:
                    business.autorizaContArt25(cont, request, usuario);
                    jsonObj = business.queryItemsContArt25(cont.getcIdContratoDef());
                    respuesta = true;
                    mensaje = "Contrato autorizado correctamente";
                    break;
                case //Nuevas EPS
                9:
                    jsonObj = new JSONObject();
                    business.addNewEps(datEP);
                    respuesta = true;
                    mensaje = "Clave presupuestal agregada.";
                    break;
                case //Aprueba ampliacion
                10:
                    jsonObj = new JSONObject();
                    business.aprovedExtensionContractArt25(cont, usuario);
                    respuesta = true;
                    mensaje = "Ampliación Aprobada.";
                    break;
                case //Aprueba ampliacion
                11:
                    jsonObj = new JSONObject();
                    business.deleteExtensionContractArt25(cont, usuario);
                    respuesta = true;
                    mensaje = "Ampliación devuelta.";
                    break;
                case //Aprueba precompromiso ampliacion
                12:
                    jsonObj = new JSONObject();
                    business.generaPrecompromisoAmpContArt25(cont, request, usuario);
                    respuesta = true;
                    mensaje = "Ampliación precomprometida.";
                    break;
                case //Cancela precompromiso ampliacion
                13:
                    jsonObj = new JSONObject();
                    business.cancelaPrecompromisoAmpContArt25(cont, request, usuario);
                    respuesta = true;
                    mensaje = "Precompromiso cancelado.";
                    break;
                case //Autoriza precompromiso ampliacion
                14:
                    jsonObj = new JSONObject();
                    business.autorizaAmpContArt25(cont, request, usuario);
                    respuesta = true;
                    mensaje = "Compromiso autorizado.";
                    break;
                default:
                    jsonObj = new JSONObject();
                    mensaje = "Proceso desconocido.";
                    log.warn("Proceso desconocido");
                    break;
            }
        } catch (Exception e) {
            log.error(e);
            jsonObj = new JSONObject();
            respuesta = false;
            mensaje = e.getMessage();
        } finally {
            try {
                jsonObj.put("RESPUESTA", respuesta);
                jsonObj.put("MENSAJE", (mensaje == null ? "error" : mensaje));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String destino = new String(arrayObj.put(jsonObj).toString().getBytes("UTF-8"), "ISO-8859-1");
            out.println(destino);
            if (out != null) {
                out.flush();
                out.close();
            }
            out = null;
            jsonObj = null;
            arrayObj = null;
            business = null;
            cIdContratoDef = null;
            datEP = null;
            cont = null;
        }
    }

    protected void conveniosContratos(HttpServletRequest request, HttpServletResponse response, Usuario usuario, HttpSession session) throws UnsupportedEncodingException {
        int tipoOperacion = 0;
        PrintWriter out = null;
        ContratacionFormalizadaBusinessLogic business = null;
        JSONObject jsonObj = null;
        JSONArray arrayObj = null;
        boolean respuesta = false;
        String mensaje = null;
        ContratoModificado conv = null;
        try {
            tipoOperacion = (request.getParameter("tipoOperacion") == null || "".equalsIgnoreCase(request.getParameter("tipoOperacion")) ? -1 : Integer.parseInt(request.getParameter("tipoOperacion")));
            business = new ContratacionFormalizadaBusinessLogic(jndiName);
            arrayObj = new JSONArray();
            jsonObj = new JSONObject();
            out = response.getWriter();
            conv = fillObjectConvenio(request, usuario);
            switch(tipoOperacion) {
                case //Precomprometer
                1:
                    business.generaPrecompromisoConvenio(conv, request, usuario);
                    mensaje = "Precomprometido aplicado.";
                    respuesta = true;
                    break;
                case //Cancelar Precompromiso
                2:
                    business.cancelaPrecompromisoConvenio(conv, request, usuario);
                    mensaje = "Precompromiso cancelado.";
                    respuesta = true;
                    break;
                case 3:
                    business.autorizaConvenio(conv, request, usuario);
                    mensaje = "Convenio autorizado.";
                    respuesta = true;
                    break;
                default:
                    log.warn("Proceso desconocido");
                    break;
            }
        } catch (Exception e) {
            log.error(e);
            jsonObj = new JSONObject();
            respuesta = false;
            mensaje = e.getMessage();
        } finally {
            try {
                jsonObj.put("RESPUESTA", respuesta);
                jsonObj.put("MENSAJE", mensaje);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String destino = new String(arrayObj.put(jsonObj).toString().getBytes("UTF-8"), "ISO-8859-1");
            out.println(destino);
            if (out != null) {
                out.flush();
                out.close();
            }
            out = null;
            jsonObj = null;
            arrayObj = null;
            business = null;
            conv = null;
        }
    }

    protected void conveniosContratosCap4(HttpServletRequest request, HttpServletResponse response, Usuario usuario, HttpSession session) throws UnsupportedEncodingException {
        int tipoOperacion = 0;
        PrintWriter out = null;
        ContratacionFormalizadaBusinessLogic business = null;
        JSONObject jsonObj = null;
        JSONArray arrayObj = null;
        ConvenioCap4 conv = null;
        boolean respuesta = false;
        String mensaje = null;
        ArrayList<PartidasConvenioCap4> listaItems = null;
        PartidasConvenioCap4 item = null;
        try {
            tipoOperacion = (request.getParameter("tipoOperacion") == null || "".equalsIgnoreCase(request.getParameter("tipoOperacion")) ? -1 : Integer.parseInt(request.getParameter("tipoOperacion")));
            business = new ContratacionFormalizadaBusinessLogic(jndiName);
            arrayObj = new JSONArray();
            //jsonObj=new JSONObject();
            out = response.getWriter();
            switch(tipoOperacion) {
                case //Consulta Datos nuevo convenio
                1:
                    jsonObj = business.queryDataNewConvCap4();
                    break;
                case //Genera nuevo convenio cap4
                2:
                    conv = fillObjectConvenioCap4(request, usuario);
                    business.addNewConvenioCap4(conv);
                    jsonObj = new JSONObject();
                    respuesta = true;
                    mensaje = "Convenio Generado";
                    jsonObj.put("ContractConvCap4Definitivo", conv.getcIdContratoDefinitivo());
                    jsonObj.put("ContractConvCap4Consecutivo", conv.getnConsecutivoModificacion());
                    jsonObj.put("nIdContModCap4", conv.getnIdContModCap4());
                    session.setAttribute(GestionInterface.ATT_ContractConvCap4Definitivo, conv.getcIdContratoDefinitivo());
                    session.setAttribute(GestionInterface.ATT_ContractConvCap4Consecutivo, conv.getnConsecutivoModificacion() + "");
                    session.setAttribute(GestionInterface.ATT_nIdContModCap4, conv.getnIdContModCap4() + "");
                    break;
                case //Consulta Datos  convenio
                3:
                    jsonObj = business.queryDataConvCap4();
                    break;
                case //caratula Datos  convenio
                4:
                    conv = fillObjectConvenioCap4(request, usuario);
                    jsonObj = business.queryDataCaratulaConvCap4(conv);
                    session.setAttribute(GestionInterface.ATT_ContractConvCap4Definitivo, conv.getcIdContratoDefinitivo());
                    session.setAttribute(GestionInterface.ATT_ContractConvCap4Consecutivo, conv.getnConsecutivoModificacion() + "");
                    session.setAttribute(GestionInterface.ATT_nIdContModCap4, conv.getnIdContModCap4() + "");
                    respuesta = true;
                    break;
                case //elimina  convenio
                5:
                    conv = fillObjectConvenioCap4(request, usuario);
                    business.deleteConvenioCap4(conv);
                    mensaje = "Convenio Eliminado";
                    jsonObj = new JSONObject();
                    respuesta = true;
                    break;
                case 6:
                    conv = fillObjectConvenioCap4(request, usuario);
                    listaItems = new ArrayList<PartidasConvenioCap4>();
                    item = fillObjectItemConvenioCap4(request);
                    listaItems.add(item);
                    conv.setPartidas(listaItems);
                    business.updateItemsConvenioCap4(conv);
                    jsonObj = business.queryDataCaratulaConvCap4(conv);
                    mensaje = "Partida actualizada";
                    respuesta = true;
                    break;
                case 7:
                    conv = fillObjectConvenioCap4(request, usuario);
                    business.apruebaConvenioCap4(conv, usuario);
                    jsonObj = business.queryDataCaratulaConvCap4(conv);
                    mensaje = "Convenio aprobado, favor de generar el precompromiso.";
                    respuesta = true;
                    break;
                case //Devuelve presupuesto
                8:
                    conv = fillObjectConvenioCap4(request, usuario);
                    business.devuelvePresupConvenioCap4(conv);
                    jsonObj = business.queryDataCaratulaConvCap4(conv);
                    mensaje = "Convenio devuelto al estatus de captura.";
                    respuesta = true;
                    break;
                case //Precomprometer
                9:
                    conv = fillObjectConvenioCap4(request, usuario);
                    business.generaPrecompromisoConvCap4(conv, request, usuario, prefixPath, jndiName);
                    jsonObj = business.queryDataCaratulaConvCap4(conv);
                    mensaje = "Convenio precomprometido.";
                    respuesta = true;
                    break;
                case //devuelve el Precompromiso
                10:
                    conv = fillObjectConvenioCap4(request, usuario);
                    business.devuelvePrecomConvenioCap4(request, prefixPath, conv, usuario);
                    jsonObj = business.queryDataCaratulaConvCap4(conv);
                    mensaje = "Precompromiso Cancelado Correctamente.";
                    respuesta = true;
                    break;
                case //delete EP
                11:
                    conv = fillObjectConvenioCap4(request, usuario);
                    business.deleteEPConvenioCap4(conv);
                    mensaje = "EP Eliminada.";
                    jsonObj = new JSONObject();
                    respuesta = true;
                    break;
                case //aprueba el convenio
                12:
                    conv = fillObjectConvenioCap4(request, usuario);
                    business.generaCompromisoConvCap4(conv, request, usuario, prefixPath, jndiName);
                    jsonObj = business.queryDataCaratulaConvCap4(conv);
                    mensaje = "Convenio aprobado.";
                    respuesta = true;
                    break;
                default:
                    log.warn("Proceso desconocido");
                    break;
            }
        } catch (Exception e) {
            log.error(e);
            jsonObj = new JSONObject();
            respuesta = false;
            mensaje = e.getMessage();
        } finally {
            try {
                jsonObj.put("RESPUESTA", respuesta);
                jsonObj.put("MENSAJE", mensaje);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String destino = new String(arrayObj.put(jsonObj).toString().getBytes("UTF-8"), "ISO-8859-1");
            out.println(destino);
            if (out != null) {
                out.flush();
                out.close();
            }
            if (listaItems != null) {
                listaItems.clear();
            }
            listaItems = null;
            item = null;
            conv = null;
            out = null;
            jsonObj = null;
            arrayObj = null;
            business = null;
        }
    }

    protected ConvenioCap4 fillObjectConvenioCap4(HttpServletRequest request, Usuario usuario) throws Exception {
        ConvenioCap4 conv = null;
        conv = new ConvenioCap4();
        conv.setcIdContratoDefinitivo((request.getParameter("cIdContratoDefinitivo") == null || "".equalsIgnoreCase(request.getParameter("cIdContratoDefinitivo")) ? "" : request.getParameter("cIdContratoDefinitivo")));
        conv.setcUsuarioCaptura(usuario.getLogin());
        conv.setlEsTotalPluri(request.getParameter("lEsTotalPluri") == null || "".equalsIgnoreCase(request.getParameter("lEsTotalPluri")) ? 0 : Integer.parseInt(request.getParameter("lEsTotalPluri")));
        conv.setnTipoModificacion(request.getParameter("nTipoModificacion") == null || "".equalsIgnoreCase(request.getParameter("nTipoModificacion")) ? 0 : Integer.parseInt(request.getParameter("nTipoModificacion")));
        conv.setnConsecutivoModificacion(request.getParameter("nConsecutivoMod") == null || "".equalsIgnoreCase(request.getParameter("nConsecutivoMod")) ? 0 : Integer.parseInt(request.getParameter("nConsecutivoMod")));
        conv.setnIdContModCap4(request.getParameter("nIdcontratoMod") == null || "".equalsIgnoreCase(request.getParameter("nIdcontratoMod")) ? 0 : Integer.parseInt(request.getParameter("nIdcontratoMod")));
        conv.setcObjetoConvenio((request.getParameter("cObjetoConvenio") == null || "".equalsIgnoreCase(request.getParameter("cObjetoConvenio")) ? "" : request.getParameter("cObjetoConvenio")));
        conv.setcNoConvenio((request.getParameter("cNoConvenio") == null || "".equalsIgnoreCase(request.getParameter("cNoConvenio")) ? "" : request.getParameter("cNoConvenio")));
        conv.setfFormalizacion((request.getParameter("fFormalizacion") == null || "".equalsIgnoreCase(request.getParameter("fFormalizacion")) ? "" : request.getParameter("fFormalizacion")));
        conv.setfInicio((request.getParameter("fInicio") == null || "".equalsIgnoreCase(request.getParameter("fInicio")) ? "" : request.getParameter("fInicio")));
        conv.setfFin((request.getParameter("fFin") == null || "".equalsIgnoreCase(request.getParameter("fFin")) ? "" : request.getParameter("fFin")));
        conv.setcEP((request.getParameter("cEP") == null || "".equalsIgnoreCase(request.getParameter("cEP")) ? "" : request.getParameter("cEP")));
        conv.setJndiName(jndiName);
        conv.setPrefixPath(prefixPath);
        return conv;
    }

    protected PartidasConvenioCap4 fillObjectItemConvenioCap4(HttpServletRequest request) throws Exception {
        PartidasConvenioCap4 item = null;
        item = new PartidasConvenioCap4();
        item.setnIdLineaConsolidado(request.getParameter("nIdLineaConsolidado") == null || "".equalsIgnoreCase(request.getParameter("nIdLineaConsolidado")) ? 0 : Integer.parseInt(request.getParameter("nIdLineaConsolidado")));
        item.setmMontoNeto(request.getParameter("mMontoNeto") == null || "".equalsIgnoreCase(request.getParameter("mMontoNeto")) ? 0.0 : Double.parseDouble(request.getParameter("mMontoNeto")));
        item.setnCantidad(request.getParameter("nCantidad") == null || "".equalsIgnoreCase(request.getParameter("nCantidad")) ? 0 : Integer.parseInt(request.getParameter("nCantidad")));
        return item;
    }

    protected void contratosRemanentes(HttpServletRequest request, HttpServletResponse response, Usuario usuario) throws UnsupportedEncodingException {
        int tipoOperacion = 0;
        PrintWriter out = null;
        ContratacionFormalizadaBusinessLogic business = null;
        JSONObject jsonObj = null;
        JSONArray arrayObj = null;
        String mensaje = "";
        boolean respuesta = false;
        Contrato cont = null;
        try {
            tipoOperacion = (request.getParameter("tipoOperacion") == null || "".equalsIgnoreCase(request.getParameter("tipoOperacion")) ? -1 : Integer.parseInt(request.getParameter("tipoOperacion")));
            business = new ContratacionFormalizadaBusinessLogic(jndiName);
            arrayObj = new JSONArray();
            jsonObj = new JSONObject();
            out = response.getWriter();
            cont = new Contrato();
            switch(tipoOperacion) {
                case //Aprobar contrato
                1:
                    cont = fillContrato(request, response);
                    business.apruebaContratoRemanente(cont, usuario);
                    respuesta = true;
                    mensaje = "Contrato remanente aprobado, favor de solicitar al \u00e1rea responsable de generar el compromiso.";
                    break;
                default:
                    log.warn("Proceso desconocido");
                    break;
            }
        } catch (Exception e) {
            jsonObj = new JSONObject();
            arrayObj = new JSONArray();
            mensaje = e.getMessage().toString();
            respuesta = false;
            log.error(e.getMessage());
            e.printStackTrace();
        } finally {
            business = null;
            cont = null;
            try {
                jsonObj.put("RESPUESTA", respuesta);
                jsonObj.put("MENSAJE", mensaje);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String destino = new String(arrayObj.put(jsonObj).toString().getBytes("UTF-8"), "ISO-8859-1");
            out.println(destino);
            if (out != null) {
                out.flush();
                out.close();
            }
            out = null;
            jsonObj = null;
            arrayObj = null;
        }
    }

    protected void contratos(HttpServletRequest request, HttpServletResponse response, Usuario usuario, HttpSession session) throws UnsupportedEncodingException {
        int tipoOperacion = 0;
        PrintWriter out = null;
        ContratacionFormalizadaBusinessLogic business = null;
        JSONObject jsonObj = null;
        JSONArray arrayObj = null;
        String mensaje = "";
        boolean respuesta = false;
        Contrato cont = null;
        try {
            tipoOperacion = (request.getParameter("tipoOperacion") == null || "".equalsIgnoreCase(request.getParameter("tipoOperacion")) ? -1 : Integer.parseInt(request.getParameter("tipoOperacion")));
            business = new ContratacionFormalizadaBusinessLogic(jndiName);
            arrayObj = new JSONArray();
            out = response.getWriter();
            cont = new Contrato();
            switch(tipoOperacion) {
                case //obtiene datos de los selects
                1:
                    jsonObj = business.consultaSelectsPSP((request.getParameter("cContratoDefinitivo") == null ? "" : request.getParameter("cContratoDefinitivo")));
                    respuesta = true;
                    break;
                case //obtiene datos de los selects Nuevo Procedimiento
                2:
                    jsonObj = business.consultaAreasResponsables((request.getParameter("cAreaRequirente") == null ? "" : request.getParameter("cAreaRequirente")));
                    respuesta = true;
                    break;
                case //Guarda caratula
                3:
                    cont = fillContrato(request, response);
                    mensaje = business.guardaCaratulaContrato(cont, usuario);
                    respuesta = true;
                    jsonObj = new JSONObject();
                    break;
                case //Guarda datos PSP
                4:
                    cont = fillContrato(request, response);
                    business.guardaDatosPSP(cont, usuario);
                    mensaje = "Datos guardados de PSP";
                    respuesta = true;
                    jsonObj = new JSONObject();
                    break;
                case //Aprueba Contrato Art 25 LAASSP
                5:
                    cont = fillContrato(request, response);
                    business.apruebaContratoArt25LAASSP(cont, usuario);
                    mensaje = "Contrato aprobado.";
                    respuesta = true;
                    jsonObj = new JSONObject();
                    session.setAttribute(GestionInterface.ATT_EstadoContrato, ContractStatus.APPROVED);
                    break;
                default:
                    log.warn("Proceso desconocido");
                    break;
            }
        } catch (Exception e) {
            jsonObj = new JSONObject();
            arrayObj = new JSONArray();
            mensaje = e.getMessage().toString();
            respuesta = false;
            log.error(e.getMessage());
            e.printStackTrace();
        } finally {
            business = null;
            if (null != cont.getOtrosImp()) {
                cont.getOtrosImp().clear();
            }
            cont = null;
            try {
                jsonObj.put("RESPUESTA", respuesta);
                jsonObj.put("MENSAJE", mensaje);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String destino = new String(arrayObj.put(jsonObj).toString().getBytes("UTF-8"), "ISO-8859-1");
            out.println(destino);
            if (out != null) {
                out.flush();
                out.close();
            }
            out = null;
            jsonObj = null;
            arrayObj = null;
        }
    }

    private GarantiaContrato fillGarantiaContrato(HttpServletRequest request, Usuario usuario, Iterator<?> iter) throws Exception {
        GarantiaContrato dat = new GarantiaContrato();
        Garantia garantiaA = new Garantia();
        garantiaA.setnTipoGarantia(GestionInterface.GARANTIA_ANTICIPO);
        Garantia garantiaC = new Garantia();
        garantiaC.setnTipoGarantia(GestionInterface.GARANTIA_CUMPLIMIENTO);
        Garantia garantiaV = new Garantia();
        garantiaV.setnTipoGarantia(GestionInterface.GARANTIA_VICIOS_OCULTOS);
        ArrayList<Garantia> listGarantia = new ArrayList<Garantia>();
        dat.setcUsuarioActualiza(usuario.getLogin());
        dat.setcUsuarioCaptura(usuario.getLogin());
        while (iter.hasNext()) {
            FileItem item = (FileItem) iter.next();
            if (item.isFormField()) {
                if ("cAseguradoraGA".equals(item.getFieldName())) {
                    garantiaA.setcAseguradora((StringUtils.isBlank(item.getString()) ? "" : item.getString()));
                }
                if ("fFechaExpedicionGA".equals(item.getFieldName())) {
                    garantiaA.setfFechaExpedicion((StringUtils.isBlank(item.getString()) ? "" : item.getString()));
                }
                if ("lChequeGA".equals(item.getFieldName())) {
                    garantiaA.setlCheque((StringUtils.isBlank(item.getString()) ? 0 : Integer.parseInt(item.getString())));
                }
                if ("lFianzaGA".equals(item.getFieldName())) {
                    garantiaA.setlFianza((StringUtils.isBlank(item.getString()) ? 0 : Integer.parseInt(item.getString())));
                }
                if ("nNumeroChequeFianzaGA".equals(item.getFieldName())) {
                    garantiaA.setcNumeroChequeFianza((StringUtils.isBlank(item.getString()) ? "" : item.getString()));
                }
                if ("mGarantiaAnticipo".equals(item.getFieldName())) {
                    garantiaA.setmMontoGarantia((StringUtils.isBlank(item.getString()) ? 0 : Float.parseFloat(item.getString())));
                }
                if ("nIdConsecutivoEndosoGA".equals(item.getFieldName())) {
                    garantiaA.setnIdConsecutivoEndosoGarantia((StringUtils.isBlank(item.getString()) ? 2 : Integer.parseInt(item.getString())));
                }
                if ("cAseguradoraGC".equals(item.getFieldName())) {
                    garantiaC.setcAseguradora((StringUtils.isBlank(item.getString()) ? "" : item.getString()));
                }
                if ("fFechaExpedicionGC".equals(item.getFieldName())) {
                    garantiaC.setfFechaExpedicion((StringUtils.isBlank(item.getString()) ? "" : item.getString()));
                }
                if ("lChequeGC".equals(item.getFieldName())) {
                    garantiaC.setlCheque((StringUtils.isBlank(item.getString()) ? 0 : Integer.parseInt(item.getString())));
                }
                if ("lFianzaGC".equals(item.getFieldName())) {
                    garantiaC.setlFianza((StringUtils.isBlank(item.getString()) ? 0 : Integer.parseInt(item.getString())));
                }
                if ("nNumeroChequeFianzaGC".equals(item.getFieldName())) {
                    garantiaC.setcNumeroChequeFianza((StringUtils.isBlank(item.getString()) ? "" : item.getString()));
                }
                if ("mGarantiaCumplimiento".equals(item.getFieldName())) {
                    garantiaC.setmMontoGarantia((StringUtils.isBlank(item.getString()) ? 0 : Float.parseFloat(item.getString())));
                }
                if ("nIdConsecutivoEndosoGC".equals(item.getFieldName())) {
                    garantiaC.setnIdConsecutivoEndosoGarantia((StringUtils.isBlank(item.getString()) ? 1 : Integer.parseInt(item.getString())));
                }
                if ("cAseguradoraGV".equals(item.getFieldName())) {
                    garantiaV.setcAseguradora((StringUtils.isBlank(item.getString()) ? "" : item.getString()));
                }
                if ("fFechaExpedicionGV".equals(item.getFieldName())) {
                    garantiaV.setfFechaExpedicion((StringUtils.isBlank(item.getString()) ? "" : item.getString()));
                }
                if ("lChequeGV".equals(item.getFieldName())) {
                    garantiaV.setlCheque((StringUtils.isBlank(item.getString()) ? 0 : Integer.parseInt(item.getString())));
                }
                if ("lFianzaGV".equals(item.getFieldName())) {
                    garantiaV.setlFianza((StringUtils.isBlank(item.getString()) ? 0 : Integer.parseInt(item.getString())));
                }
                if ("nNumeroChequeFianzaGV".equals(item.getFieldName())) {
                    garantiaV.setcNumeroChequeFianza((StringUtils.isBlank(item.getString()) ? "" : item.getString()));
                }
                if ("mGarantiaViciosOcultos".equals(item.getFieldName())) {
                    garantiaV.setmMontoGarantia((StringUtils.isBlank(item.getString()) ? 0 : Float.parseFloat(item.getString())));
                }
                if ("cIdContratoDefinitivo".equals(item.getFieldName())) {
                    dat.setcIdContratoDefinitivo((StringUtils.isBlank(item.getString()) ? "" : item.getString()));
                }
                if ("nTipoProcesoGarantia".equals(item.getFieldName())) {
                    dat.setnTipoProcesoGarantia((StringUtils.isBlank(item.getString()) ? 1 : Integer.parseInt(item.getString())));
                }
                if ("nIdConsecutivoEndosoGV".equals(item.getFieldName())) {
                    garantiaV.setnIdConsecutivoEndosoGarantia((StringUtils.isBlank(item.getString()) ? 3 : Integer.parseInt(item.getString())));
                }
                item.delete();
                continue;
            } else {
                dat.setArchivoStream(item.getInputStream());
                dat.setcNombreArchivo(item.getName());
                dat.setcExtencion(Util.getFileExtencion(item.getName()));
            }
        }
        listGarantia.add(garantiaA);
        listGarantia.add(garantiaC);
        listGarantia.add(garantiaV);
        dat.setListGarantia(listGarantia);
        return dat;
    }

    private LiberaGarantiaContrato fillLiberaGarantiaContrato(HttpServletRequest request, Usuario usuario, Iterator<?> iter) throws Exception {
        LiberaGarantiaContrato dat = new LiberaGarantiaContrato();
        dat.setcUsuarioActualiza(usuario.getLogin());
        dat.setcUsuarioCaptura(usuario.getLogin());
        while (iter.hasNext()) {
            FileItem item = (FileItem) iter.next();
            if (item.isFormField()) {
                if ("cMotivoLiberacion".equals(item.getFieldName())) {
                    dat.setcMotivoLiberacion((StringUtils.isBlank(item.getString()) ? "" : item.getString()));
                }
                if ("cIdContratoDefinitivo".equals(item.getFieldName())) {
                    dat.setcIdContratoDefinitivo((StringUtils.isBlank(item.getString()) ? "" : item.getString()));
                }
                if ("cOficioSolicitud".equals(item.getFieldName())) {
                    dat.setcOficioSolicitud((StringUtils.isBlank(item.getString()) ? "" : item.getString()));
                }
                if ("fFechaSolicitud".equals(item.getFieldName())) {
                    dat.setfFechaSolicitud((StringUtils.isBlank(item.getString()) ? "" : item.getString()));
                }
                if ("cOficioLiberacion".equals(item.getFieldName())) {
                    dat.setcOficioLiberacion((StringUtils.isBlank(item.getString()) ? "" : item.getString()));
                }
                if ("fFechaLiberacion".equals(item.getFieldName())) {
                    dat.setfFechaLiberacion((StringUtils.isBlank(item.getString()) ? "" : item.getString()));
                }
                if ("lchequeEntregadoProveedor".equals(item.getFieldName())) {
                    dat.setlChequeEntregadoProveedor((StringUtils.isBlank(item.getString()) ? 0 : Integer.parseInt(item.getString())));
                }
                item.delete();
                continue;
            } else {
                dat.setArchivoStream(item.getInputStream());
                dat.setcNombreArchivo(item.getName());
                dat.setcExtencion(Util.getFileExtencion(item.getName()));
            }
        }
        return dat;
    }

    private DatosEP_TMP fillContEP(HttpServletRequest request) throws Exception {
        DatosEP_TMP dat = new DatosEP_TMP();
        dat.setcClaveInterna((request.getParameter("cClaveInterna") == null ? "" : request.getParameter("cClaveInterna")));
        dat.setcEjercicio((request.getParameter("cEjercicio") == null ? "" : request.getParameter("cEjercicio")));
        dat.setcIdClaveEgresos((request.getParameter("cIdClaveEgresos") == null ? "" : request.getParameter("cIdClaveEgresos")));
        dat.setcIdContrato((request.getParameter("cIdContrato") == null ? "" : request.getParameter("cIdContrato")));
        dat.setcIdContratoDefinitivo((request.getParameter("cIdContratoDefinitivo") == null ? "" : request.getParameter("cIdContratoDefinitivo")));
        dat.setcIdEntidadContable((request.getParameter("cIdEntidadContable") == null ? "" : request.getParameter("cIdEntidadContable")));
        dat.setcIdTipocontrato((request.getParameter("cIdTipocontrato") == null ? "" : request.getParameter("cIdTipocontrato")));
        dat.setcIdUnidadEjecutora((request.getParameter("cIdUnidadEjecutora") == null ? "" : request.getParameter("cIdUnidadEjecutora")));
        return dat;
    }

    private Contrato fillContrato(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Contrato cont = new Contrato();
        DatosContratoPSP datPSP = new DatosContratoPSP();
        ProcedimientoAdjudicacion procedAdj = new ProcedimientoAdjudicacion();
        OtrosImpuestos otrosImp = new OtrosImpuestos();
        ArrayList<OtrosImpuestos> listOtrosImp = new ArrayList<OtrosImpuestos>();
        try {
            otrosImp.setnId(1);
            otrosImp.setcImpuesto(request.getParameter("cDescripcionOtroImpuesto1"));
            otrosImp.setmMonto((request.getParameter("cMontoOtroImpuesto1") == null || "".equalsIgnoreCase(request.getParameter("cMontoOtroImpuesto1"))) ? 0 : Double.parseDouble(request.getParameter("cMontoOtroImpuesto1")));
            listOtrosImp.add(otrosImp);
            otrosImp = new OtrosImpuestos();
            otrosImp.setnId(2);
            otrosImp.setcImpuesto(request.getParameter("cDescripcionOtroImpuesto2"));
            otrosImp.setmMonto((request.getParameter("cMontoOtroImpuesto2") == null || "".equalsIgnoreCase(request.getParameter("cMontoOtroImpuesto2"))) ? 0 : Double.parseDouble(request.getParameter("cMontoOtroImpuesto2")));
            listOtrosImp.add(otrosImp);
            otrosImp = new OtrosImpuestos();
            otrosImp.setnId(3);
            otrosImp.setcImpuesto(request.getParameter("cDescripcionOtroImpuesto3"));
            otrosImp.setmMonto((request.getParameter("cMontoOtroImpuesto3") == null || "".equalsIgnoreCase(request.getParameter("cMontoOtroImpuesto3"))) ? 0 : Double.parseDouble(request.getParameter("cMontoOtroImpuesto3")));
            listOtrosImp.add(otrosImp);
            cont.setcIdContratoDefinitivo((request.getParameter("cContratoDefinitivo") == null ? "" : request.getParameter("cContratoDefinitivo")));
            cont.setcIdProcedimiento((request.getParameter("cIdProcedimiento") == null ? "" : request.getParameter("cIdProcedimiento")));
            cont.setnIdconsecutivoAdj(request.getParameter("nIdConsecutivoAdj") == null || "".equalsIgnoreCase(request.getParameter("nIdConsecutivoAdj")) ? -1 : Integer.parseInt(request.getParameter("nIdConsecutivoAdj")));
            cont.setnEsContratacionPSP((request.getParameter("nEsContratacionPSP") == null || "".equalsIgnoreCase(request.getParameter("nEsContratacionPSP")) || "0".equalsIgnoreCase(request.getParameter("nEsContratacionPSP")) ? false : true));
            cont.setPrefixPath(getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator);
            cont.setJndiName(jndiName);
            cont.setEsDescentralizado((request.getParameter("lEsDescentralizado") == null || "0".equalsIgnoreCase(request.getParameter("lEsDescentralizado"))) ? 0 : Integer.parseInt(request.getParameter("lEsDescentralizado")));
            cont.setcConceptoContrato(request.getParameter("cConceptoContrato") == null ? "" : new String((request.getParameter("cConceptoContrato")).getBytes("ISO-8859-1"), "UTF-8"));
            cont.setfInicio(request.getParameter("fFechaIninio"));
            cont.setfFin(request.getParameter("fFechaFin"));
            cont.setfFormalizacion(request.getParameter("fFechaFormalizacion"));
            cont.setfSolicitud(request.getParameter("fFechaSolicitud"));
            cont.setfPropuestas(request.getParameter("fFechaPropuestas"));
            cont.setfEntrega(request.getParameter("fFechaEntrega"));
            cont.setcNoContratoCNET(request.getParameter("cContratoCNET"));
            cont.setnCodExpedienteCNET(request.getParameter("nCodExpedienteCNET"));
            cont.setnCodContratoCNET(request.getParameter("nCodContratoCNET"));
            cont.setcOficioDG(request.getParameter("cOficioDG"));
            cont.setcFolioMASCP(request.getParameter("cFolioMASCP"));
            cont.setcComentarioJustificaTipoProced(request.getParameter("cDescripJustTipoProced") == null ? "" : new String((request.getParameter("cDescripJustTipoProced")).getBytes("ISO-8859-1"), "UTF-8"));
            cont.setlJustificaTipoProced((request.getParameter("lJustificaTipoProced") == null || "0".equalsIgnoreCase(request.getParameter("lJustificaTipoProced"))) ? 0 : Integer.parseInt(request.getParameter("lJustificaTipoProced")));
            cont.setITieneAnticipo((request.getParameter("llevaAnticipo") == null || "0".equalsIgnoreCase(request.getParameter("llevaAnticipo"))) ? 0 : Integer.parseInt(request.getParameter("llevaAnticipo")));
            cont.setcEjercicio(request.getParameter("cEjercicio"));
            cont.setnCategoriaProcedimiento(request.getParameter("nIdCategoriaProced") == null || "".equalsIgnoreCase(request.getParameter("nIdCategoriaProced")) ? -1 : Integer.parseInt(request.getParameter("nIdCategoriaProced")));
            cont.setnIdEstado(request.getParameter("nIdEstado") == null || "".equalsIgnoreCase(request.getParameter("nIdEstado")) ? 6 : Integer.parseInt(request.getParameter("nIdEstado")));
            cont.setcIdUnidadEjecutora(request.getParameter("cUnidadEjecutora"));
            cont.setmTotalGarantia((request.getParameter("mMontoTotalGarantias") == null || "".equalsIgnoreCase(request.getParameter("mMontoTotalGarantias"))) ? 0 : Double.parseDouble(request.getParameter("mMontoTotalGarantias")));
            cont.setmGarantiaAnticipo((request.getParameter("mMontoGarantiaAnticipo") == null || "".equalsIgnoreCase(request.getParameter("mMontoGarantiaAnticipo"))) ? 0 : Double.parseDouble(request.getParameter("mMontoGarantiaAnticipo")));
            cont.setmGarantiaCumplimiento((request.getParameter("mMontoGarantiaCumplimiento") == null || "".equalsIgnoreCase(request.getParameter("mMontoGarantiaCumplimiento"))) ? 0 : Double.parseDouble(request.getParameter("mMontoGarantiaCumplimiento")));
            cont.setcMecanismosVigilancia(request.getParameter("cMecanismosVigilancia") == null ? "" : new String((request.getParameter("cMecanismosVigilancia")).getBytes("ISO-8859-1"), "UTF-8"));
            cont.setcIdtipoContrato(request.getParameter("cIdTipoContrato"));
            cont.setcIdtipoProcedimiento(request.getParameter("cIdTipoProcedimiento"));
            cont.setnIdConsecutivo(request.getParameter("nIdConsecutivoCont") == null || "".equalsIgnoreCase(request.getParameter("nIdConsecutivoCont")) ? -1 : Integer.parseInt(request.getParameter("nIdConsecutivoCont")));
            cont.setnIdConsecutivoProcedimiento(request.getParameter("nIdConsecutivoProcedimiento") == null || "".equalsIgnoreCase(request.getParameter("nIdConsecutivoProcedimiento")) ? -1 : Integer.parseInt(request.getParameter("nIdConsecutivoProcedimiento")));
            cont.setlExcentaGarantia(request.getParameter("lExcentaGarantia") == null || "".equalsIgnoreCase(request.getParameter("lExcentaGarantia")) ? 1 : Integer.parseInt(request.getParameter("lExcentaGarantia")));
            cont.setcIdRFC(request.getParameter("rfc") == null ? "" : new String((request.getParameter("rfc")).getBytes("ISO-8859-1"), "UTF-8"));
            datPSP.setcAreaRequirente((request.getParameter("cAreaRequirente") == null ? "" : request.getParameter("cAreaRequirente")));
            datPSP.setcAreaResponsable(request.getParameter("cAreaResponsable") == null ? "" : request.getParameter("cAreaResponsable"));
            datPSP.setcIdcontratoDefinitivo((request.getParameter("cContratoDefinitivo") == null ? "" : request.getParameter("cContratoDefinitivo")));
            datPSP.setlEsMaestro((request.getParameter("nElPSPEsMaestro") == null || "".equalsIgnoreCase(request.getParameter("nElPSPEsMaestro")) || "0".equalsIgnoreCase(request.getParameter("nElPSPEsMaestro")) ? false : true));
            datPSP.setnCentroTrabajo(request.getParameter("cCentroTrabajo") == null || "".equalsIgnoreCase(request.getParameter("cCentroTrabajo")) ? -1 : Integer.parseInt(request.getParameter("cCentroTrabajo")));
            datPSP.setmMontoMensual((request.getParameter("mMontoMensual") == null || "".equalsIgnoreCase(request.getParameter("mMontoMensual")) ? 0.00 : Double.parseDouble(request.getParameter("mMontoMensual"))));
            datPSP.setcDenominacionProyecto(request.getParameter("cDenominacionProyecto") == null ? "" : new String((request.getParameter("cDenominacionProyecto")).getBytes("ISO-8859-1"), "UTF-8"));
            procedAdj.setcIdProcedimiento((request.getParameter("cIdProcedimiento") == null ? "" : request.getParameter("cIdProcedimiento")));
            procedAdj.setcIdRFC(request.getParameter("rfc") == null ? "" : new String((request.getParameter("rfc")).getBytes("ISO-8859-1"), "UTF-8"));
            procedAdj.setnIdconsecutivoAdj(request.getParameter("nIdConsecutivoAdj") == null || "".equalsIgnoreCase(request.getParameter("nIdConsecutivoAdj")) ? -1 : Integer.parseInt(request.getParameter("nIdConsecutivoAdj")));
            procedAdj.setnIdfundamentoLeg(request.getParameter("nIdFundamentoLeg") == null || "".equalsIgnoreCase(request.getParameter("nIdFundamentoLeg")) ? -1 : Integer.parseInt(request.getParameter("nIdFundamentoLeg")));
            procedAdj.setnIdConsecutivo(request.getParameter("nIdConsecutivoProcedimiento") == null || "".equalsIgnoreCase(request.getParameter("nIdConsecutivoProcedimiento")) ? -1 : Integer.parseInt(request.getParameter("nIdConsecutivoProcedimiento")));
            procedAdj.setcIdTipoProcedimiento(request.getParameter("cIdTipoProcedimiento"));
            cont.setDatPSP(datPSP);
            cont.setProcedAdj(procedAdj);
            cont.setOtrosImp(listOtrosImp);
            return cont;
        } finally {
            otrosImp = null;
            listOtrosImp = null;
            datPSP = null;
            procedAdj = null;
        }
    }

    protected ContratoModificado fillObjectConvenio(HttpServletRequest request, Usuario usuario) throws Exception {
        ContratoModificado conv = null;
        conv = new ContratoModificado();
        conv.setcIdContratoDefinitivo((request.getParameter("cIdContratoDefinitivo") == null || "".equalsIgnoreCase(request.getParameter("cIdContratoDefinitivo")) ? "" : request.getParameter("cIdContratoDefinitivo")));
        conv.setnConsecutivoModificacion(request.getParameter("nConsecutivoMod") == null || "".equalsIgnoreCase(request.getParameter("nConsecutivoMod")) ? 0 : Integer.parseInt(request.getParameter("nConsecutivoMod")));
        conv.setnEsDescentralizado(request.getParameter("nTipoPago") == null || "".equalsIgnoreCase(request.getParameter("nTipoPago")) ? 0 : Integer.parseInt(request.getParameter("nTipoPago")));
        conv.setcNoConvenio((request.getParameter("cNoConvenio") == null || "".equalsIgnoreCase(request.getParameter("cNoConvenio")) ? "" : request.getParameter("cNoConvenio")));
        conv.setJndiName(jndiName);
        conv.setPrefixPath(prefixPath);
        return conv;
    }

    public void init(ServletConfig config) throws ServletException {
        //Crea la conexión a BD
        super.init(config);
        tempDir = config.getInitParameter("tempDir");
        if (tempDir == null) {
            //tempDir = config.getServletContext().getRealPath("/") +  "upload" + File.separator;
            tempDir = config.getServletContext().getRealPath("/") + ".." + File.separator + "upload" + File.separator;
            File fDir = new File(tempDir);
            if (!fDir.exists())
                if (!fDir.mkdirs())
                    throw new ServletException("No se pudo crear el directorio " + tempDir);
        }
        try {
            InitialContext ic = new InitialContext();
            jndiName = (String) ic.lookup("java:comp/env/dataSourceRefName");
            if (jndiName == null) {
                jndiName = "jdbc/gestion";
                log.info("Environment Entry \"dataSourceRefName\" nula usando default \"" + jndiName + "\"");
            } else
                log.info("dataSourceRefName=" + jndiName);
        } catch (NamingException exc) {
            jndiName = "jdbc/gestion";
            log.info("Environment Entry \"dataSourceRefName\" no definida usando default \"" + jndiName + "\"");
        }
        // Put your code here
        try {
            InitialContext ic = new InitialContext();
            folioGenerator = (String) ic.lookup("java:comp/env/folioGeneratorInterface");
            if (folioGenerator == null) {
                folioGenerator = "com.syc.gestion.custom.DefaultFolioGenerator";
                log.info("Environment Entry \"folioGeneratorInterface\" nula usando default \"" + folioGenerator + "\"");
            } else
                log.info("folioGeneratorInterface=" + folioGenerator);
        } catch (NamingException exc) {
            folioGenerator = "com.syc.gestion.custom.DefaultFolioGenerator";
            log.info("Environment Entry \"folioGeneratorInterface\" no definida usando default \"" + folioGenerator + "\"");
        }
    }
}
