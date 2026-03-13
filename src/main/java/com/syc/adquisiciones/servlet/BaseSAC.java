package com.syc.adquisiciones.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.syc.adquisiciones.businessLogic.BaseSACBusinessLogic;
import com.syc.adquisiciones.core.DatosProcedSAC;
import com.syc.adquisiciones.core.Respuesta;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "BaseSAC", urlPatterns = { "/BaseSAC" })
public class BaseSAC extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static Logger log = LoggerFactory.getLogger(BaseSAC.class);

    private static String jndiName = null;

    private String folioGenerator = null;

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
        switch(tipoProceso) {
            case //Nuevo Procedimiento
            1:
                nuevoProcedimientoSAC(request, response, usuario);
                break;
            case //Procedimiento
            2:
                procedimientoSAC(request, response, usuario);
                break;
            case //Nuevo contrato
            3:
                break;
            default:
                log.warn("Proceso desconocido");
                break;
        }
    }

    protected void procedimientoSAC(HttpServletRequest request, HttpServletResponse response, Usuario usuario) throws UnsupportedEncodingException {
        PrintWriter out = null;
        BaseSACBusinessLogic business = null;
        DatosProcedSAC datos = null;
        JSONObject jsonObj = null;
        JSONArray arrayObj = null;
        String mensaje = "";
        boolean respuesta = false;
        int tipoOperacion = 0;
        try {
            business = new BaseSACBusinessLogic(jndiName);
            arrayObj = new JSONArray();
            out = response.getWriter();
            tipoOperacion = (request.getParameter("tipoOperacion") == null || "".equalsIgnoreCase(request.getParameter("tipoOperacion")) ? -1 : Integer.parseInt(request.getParameter("tipoOperacion")));
            switch(tipoOperacion) {
                case //consulta de datos
                1:
                    datos = llenaDatosProcedSAC(request, usuario);
                    jsonObj = business.queryProcedimiento(datos);
                    respuesta = true;
                    mensaje = "Consulta de datos";
                    break;
                case //Actualiza datos
                2:
                    datos = llenaDatosProcedSAC(request, usuario);
                    business.updateProcedSAC(datos);
                    respuesta = true;
                    mensaje = "Actualizaci\u00f3n de datos.";
                    jsonObj = new JSONObject();
                    break;
                case //Consulta Procesos de Contrataci\u00f3n
                3:
                    jsonObj = business.consultaProcesoContratacion((request.getParameter("nTipoProcedimiento") == null ? 0 : Integer.parseInt(request.getParameter("nTipoProcedimiento"))));
                    respuesta = true;
                    break;
                case //Se declara desierto el procedimiento
                4:
                    datos = llenaDatosProcedSAC(request, usuario);
                    business.desiertoProcedSAC(datos);
                    mensaje = "Est\u00f3 procedimiento fue declarado desierto.";
                    respuesta = true;
                    jsonObj = new JSONObject();
                    break;
                case //Se devuelve el proceso
                5:
                    datos = llenaDatosProcedSAC(request, usuario);
                    business.procedSACDevuelto(datos);
                    mensaje = "Est\u00f3 procedimiento fue devuelto.";
                    respuesta = true;
                    jsonObj = new JSONObject();
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
            datos = null;
            try {
                jsonObj.put("RESPUESTA", respuesta);
                jsonObj.put("MENSAJE", mensaje);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String destino = new String(arrayObj.put(jsonObj).toString().getBytes("UTF-8"), "ISO-8859-1");
            out.println(destino);
            out.flush();
        }
    }

    protected void nuevoProcedimientoSAC(HttpServletRequest request, HttpServletResponse response, Usuario usuario) throws UnsupportedEncodingException {
        int tipoOperacion = 0;
        PrintWriter out = null;
        BaseSACBusinessLogic business = null;
        DatosProcedSAC datos = null;
        JSONObject jsonObj = null;
        JSONArray arrayObj = null;
        String mensaje = "";
        boolean respuesta = false;
        Respuesta resp = null;
        try {
            tipoOperacion = (request.getParameter("tipoOperacion") == null || "".equalsIgnoreCase(request.getParameter("tipoOperacion")) ? -1 : Integer.parseInt(request.getParameter("tipoOperacion")));
            business = new BaseSACBusinessLogic(jndiName);
            arrayObj = new JSONArray();
            out = response.getWriter();
            switch(tipoOperacion) {
                case //obtiene datos de los selects Nuevo Procedimiento
                1:
                    jsonObj = business.consultaSelectsProcedimiento((request.getParameter("cAreaRequirente") == null || "null".equalsIgnoreCase(request.getParameter("cAreaRequirente")) || "".equalsIgnoreCase(request.getParameter("cAreaRequirente")) ? "A01" : request.getParameter("cAreaRequirente")), 0);
                    respuesta = true;
                    break;
                case //obtiene datos de los selects Nuevo Procedimiento
                2:
                    jsonObj = business.consultaAreasResponsables((request.getParameter("cAreaRequirente") == null ? "" : request.getParameter("cAreaRequirente")));
                    respuesta = true;
                    break;
                case 3:
                    datos = llenaDatosProcedSAC(request, usuario);
                    resp = business.addNewProcedimiento(datos);
                    respuesta = resp.isResp();
                    mensaje = resp.getMsg();
                    jsonObj = new JSONObject();
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
            datos = null;
            resp = null;
            try {
                jsonObj.put("RESPUESTA", respuesta);
                jsonObj.put("MENSAJE", mensaje);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String destino = new String(arrayObj.put(jsonObj).toString().getBytes("UTF-8"), "ISO-8859-1");
            out.println(destino);
        }
    }

    private DatosProcedSAC llenaDatosProcedSAC(HttpServletRequest request, Usuario usuario) throws Exception {
        DatosProcedSAC datos = new DatosProcedSAC();
        datos.setcAreaRequirente((request.getParameter("cAreaRequirente") == null ? "" : request.getParameter("cAreaRequirente")));
        datos.setcAreaResponsable(request.getParameter("cAreaResponsable") == null ? "" : request.getParameter("cAreaResponsable"));
        datos.setcAreaTecnica(request.getParameter("cAreaTecnica") == null ? "" : new String((request.getParameter("cAreaTecnica")).getBytes("ISO-8859-1"), "UTF-8"));
        datos.setcDenominacionProced(request.getParameter("cDenominacionProced") == null ? "" : new String((request.getParameter("cDenominacionProced")).getBytes("ISO-8859-1"), "UTF-8"));
        datos.setcFechaApertProposiciones(request.getParameter("cFechaApertProposiciones") == null ? "" : request.getParameter("cFechaApertProposiciones"));
        datos.setcFechaAutConvocatoria(request.getParameter("cFechaAutConvocatoria") == null ? "" : request.getParameter("cFechaAutConvocatoria"));
        datos.setcFechaEvaluacionTecnica(request.getParameter("cFechaEvaluacionTecnica") == null ? "" : request.getParameter("cFechaEvaluacionTecnica"));
        datos.setcFechaExpediente(request.getParameter("cFechaExpediente") == null ? "" : request.getParameter("cFechaExpediente"));
        datos.setcFechaFallo(request.getParameter("cFechaFallo") == null ? "" : request.getParameter("cFechaFallo"));
        datos.setcFechaAtencion(request.getParameter("cFechaAtencion") == null ? "" : request.getParameter("cFechaAtencion"));
        datos.setcFechaGeneracionContrato(request.getParameter("cFechaGeneracionContrato") == null ? "" : request.getParameter("cFechaGeneracionContrato"));
        datos.setcFechaJuntaAclara(request.getParameter("cFechaJuntaAclara") == null ? "" : request.getParameter("cFechaJuntaAclara"));
        datos.setcFechaPublicacionConvocatoria(request.getParameter("cFechaPublicacionConvocatoria") == null ? "" : request.getParameter("cFechaPublicacionConvocatoria"));
        datos.setcFechaSolicitud(request.getParameter("cFechaSolicitud") == null ? "" : request.getParameter("cFechaSolicitud"));
        datos.setcLogin(usuario.getLogin());
        datos.setcNumeroProcedimiento(request.getParameter("cNumeroProcedimiento") == null ? "" : new String((request.getParameter("cNumeroProcedimiento")).getBytes("ISO-8859-1"), "UTF-8"));
        datos.setcOficioSolicitud(request.getParameter("cOficioSolicitud") == null ? "" : new String((request.getParameter("cOficioSolicitud")).getBytes("ISO-8859-1"), "UTF-8"));
        datos.setcProcedimientoContratacionTurnado(request.getParameter("cProcedContTurnado") == null ? "" : new String((request.getParameter("cProcedContTurnado")).getBytes("ISO-8859-1"), "UTF-8"));
        datos.setcProyectoConvocatoria(request.getParameter("cProyectoConvocatoria") == null ? "" : request.getParameter("cProyectoConvocatoria"));
        datos.setnMateriaProcedimiento(request.getParameter("nMateriaProcedimiento") == null || "".equalsIgnoreCase(request.getParameter("nMateriaProcedimiento")) ? -1 : Integer.parseInt(request.getParameter("nMateriaProcedimiento")));
        datos.setnProveedorDadoAlta(request.getParameter("nProveedorDadoAlta") == null || "".equalsIgnoreCase(request.getParameter("nProveedorDadoAlta")) ? -1 : Integer.parseInt(request.getParameter("nProveedorDadoAlta")));
        datos.setnTipoProcedimiento(request.getParameter("nTipoProcedimiento") == null || "".equalsIgnoreCase(request.getParameter("nTipoProcedimiento")) || "null".equalsIgnoreCase(request.getParameter("nTipoProcedimiento")) ? -1 : Integer.parseInt(request.getParameter("nTipoProcedimiento")));
        datos.setnProcesoContratacion(request.getParameter("nProcesoContratacion") == null || "".equalsIgnoreCase(request.getParameter("nProcesoContratacion")) || "null".equalsIgnoreCase(request.getParameter("nProcesoContratacion")) ? 0 : Integer.parseInt(request.getParameter("nProcesoContratacion")));
        datos.setCadTablaParticipantes(request.getParameter("cadTablaParticipantes") == null ? "" : new String((request.getParameter("cadTablaParticipantes")).getBytes("ISO-8859-1"), "UTF-8"));
        datos.setnIdProcedimientoSAC(request.getParameter("nIdProcedimientoSAC") == null || "".equalsIgnoreCase(request.getParameter("nIdProcedimientoSAC")) ? -1 : Integer.parseInt(request.getParameter("nIdProcedimientoSAC")));
        datos.setcObservaciones(request.getParameter("cObservaciones") == null ? "" : new String((request.getParameter("cObservaciones")).getBytes("ISO-8859-1"), "UTF-8"));
        return datos;
    }

    public void init(ServletConfig config) throws ServletException {
        //Crea la conexi\u00f3n a BD
        super.init(config);
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
