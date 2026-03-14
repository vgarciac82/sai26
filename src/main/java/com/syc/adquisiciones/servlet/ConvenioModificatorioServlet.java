package com.syc.adquisiciones.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
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
import com.syc.adquisiciones.businessLogic.ContratoModificadoBusiness;
import com.syc.adquisiciones.core.ContratoModificado;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "ConvenioModificatorioServlet", urlPatterns = { "/servlet/ConvenioModificatorioServlet" })
public class ConvenioModificatorioServlet extends HttpServlet implements GestionInterface {

    private static final long serialVersionUID = -7613020770370927331L;

    private static String jndiName = null;

    private static Logger log = LoggerFactory.getLogger(ConvenioModificatorioServlet.class);

    public void init(ServletConfig config) throws ServletException {
        //Crea la conexión a BD
        super.init(config);
        try {
            InitialContext ic = new InitialContext();
            jndiName = (String) ic.lookup("java:comp/env/dataSourceRefName");
            if (jndiName == null) {
                jndiName = "jdbc/gestion";
                log.info("Object: {}", "Environment Entry \"dataSourceRefName\" nula usando default \"" + jndiName + "\"");
            } else
                log.info("Object: {}", "dataSourceRefName=" + jndiName);
        } catch (NamingException exc) {
            jndiName = "jdbc/gestion";
            log.info("Object: {}", "Environment Entry \"dataSourceRefName\" no definida usando default \"" + jndiName + "\"");
        }
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null) {
            log.warn("No se logro crear la sesion");
            throw new ServletException("No se logro crear la sesion");
        }
        Usuario usuario = (Usuario) session.getAttribute(ATT_USER);
        if (usuario == null) {
            log.warn("No hay Usuario en sesion");
            session.invalidate();
            resp.sendRedirect("../index.jsp");
            return;
        }
        int pestana = -1;
        String destino = null;
        JSONArray arrayObj = null;
        JSONObject jsonObj = null;
        PrintWriter out = null;
        try {
            arrayObj = new JSONArray();
            pestana = (null == req.getParameter("pestana") || "".equalsIgnoreCase(req.getParameter("pestana")) ? -1 : Integer.parseInt(req.getParameter("pestana")));
            switch(pestana) {
                case //Nuevo
                0:
                    break;
                case //Caratula
                1:
                    jsonObj = caratula(req, resp, usuario);
                    break;
                case //Presupuesto
                2:
                    break;
                case //precompromiso
                3:
                    break;
                case //Compromiso
                4:
                    jsonObj = compromiso(req, resp, usuario);
                    break;
                default:
                    jsonObj = new JSONObject();
                    log.warn("Pestaña no identificada.");
                    jsonObj.put("MSG", "Pestaña no identificada.");
                    break;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            jsonObj = new JSONObject();
            try {
                jsonObj.put("MSG", (null == e.getMessage() || "null".equalsIgnoreCase(e.getMessage()) ? "Error: " + e.toString() : "Error : " + e.getMessage().toString()));
            } catch (JSONException e1) {
                log.error(e1.getMessage(), e1);
                throw new ServletException(e1);
            }
        } finally {
            out = resp.getWriter();
            destino = new String(arrayObj.put(jsonObj).toString().getBytes("UTF-8"), "ISO-8859-1");
            out.println(destino);
            out.flush();
            if (out != null) {
                out.close();
            }
            usuario = null;
            session = null;
            arrayObj = null;
            jsonObj = null;
            destino = null;
            out = null;
        }
    }

    private JSONObject compromiso(HttpServletRequest req, HttpServletResponse resp, Usuario usuario) throws Exception, JSONException {
        JSONObject jsonObj = null;
        int operacion = -1;
        ContratoModificadoBusiness business = null;
        ContratoModificado contMod = null;
        StringBuilder msg = null;
        JSONArray arrayObj = null;
        try {
            jsonObj = new JSONObject();
            msg = new StringBuilder();
            business = new ContratoModificadoBusiness();
            operacion = (null == req.getParameter("operacion") || "".equalsIgnoreCase(req.getParameter("operacion")) ? -1 : Integer.parseInt(req.getParameter("operacion")));
            contMod = fillObject(req);
            switch(operacion) {
                case //Consulta Info
                0:
                    //Consulta info
                    arrayObj = business.queryContratoModificado(jndiName, contMod);
                    jsonObj.put("datosGuardados", arrayObj);
                    msg.append("Consulta de datos");
                    break;
                case //Aprueba reducción de compromiso
                1:
                    msg.append(business.apruebaConvRed(jndiName, contMod, usuario));
                    jsonObj.put("datosGuardados", business.queryContratoModificado(jndiName, contMod));
                    break;
                case //Devuelve reducción de compromiso
                2:
                    msg.append(business.devulveConvRed(jndiName, contMod, usuario));
                    jsonObj.put("datosGuardados", business.queryContratoModificado(jndiName, contMod));
                    break;
                case //Autoriza reducción de compromiso
                3:
                    msg.append(business.autorizaConvRed(jndiName, contMod, usuario));
                    jsonObj.put("datosGuardados", business.queryContratoModificado(jndiName, contMod));
                    break;
                default:
                    log.warn("Operación no valida.");
                    msg.append("Operación no valida.");
                    break;
            }
            jsonObj.put("MSG", msg.toString());
            return jsonObj;
        } finally {
            business = null;
            contMod = null;
            arrayObj = null;
        }
    }

    private JSONObject caratula(HttpServletRequest req, HttpServletResponse resp, Usuario usuario) throws Exception {
        JSONObject jsonObj = null;
        int operacion = -1;
        ContratoModificadoBusiness business = null;
        ContratoModificado contMod = null;
        String msg = null;
        JSONArray arrayObj = null;
        try {
            jsonObj = new JSONObject();
            business = new ContratoModificadoBusiness();
            operacion = (null == req.getParameter("operacion") || "".equalsIgnoreCase(req.getParameter("operacion")) ? -1 : Integer.parseInt(req.getParameter("operacion")));
            contMod = fillObject(req);
            int nidLineaConsolidado = (null == req.getParameter("nIdlineaConsolidado") || "".equalsIgnoreCase(req.getParameter("nIdlineaConsolidado")) ? -1 : Integer.parseInt(req.getParameter("nIdlineaConsolidado")));
            switch(operacion) {
                case //Agrega una partida de contrato
                0:
                    msg = business.addPartidaDeContrato(jndiName, contMod, nidLineaConsolidado, usuario);
                    break;
                case //Agrega todas las partidas de contrato
                1:
                    msg = business.addAllPartidas(jndiName, contMod, usuario);
                    break;
                case //Elimina PArtida
                2:
                    msg = business.deletePartida(jndiName, contMod, nidLineaConsolidado, usuario);
                    break;
                case //Guardar
                3:
                    msg = business.savePartidas(jndiName, contMod, usuario);
                    break;
                case //Elimina todas las Partidas
                4:
                    msg = business.deleteAllPartidas(jndiName, contMod, usuario);
                    break;
                default:
                    log.warn("Operación no valida.");
                    msg = "Operación no valida.";
                    break;
            }
            //Consulta info
            arrayObj = business.queryContratoModificado(jndiName, contMod);
            jsonObj.put("datosGuardados", arrayObj);
            jsonObj.put("MSG", msg);
            return jsonObj;
        } finally {
            business = null;
            contMod = null;
            arrayObj = null;
        }
    }

    private ContratoModificado fillObject(HttpServletRequest req) throws Exception {
        ContratoModificado contMod = new ContratoModificado();
        contMod.setcIdContrato(req.getParameter("cIdContrato"));
        contMod.setcIdContratoDefinitivo(req.getParameter("cIdContratoDefinitivo"));
        contMod.setnTipoMod((null == req.getParameter("nTipoMod") || "".equalsIgnoreCase(req.getParameter("nTipoMod"))) ? -1 : Integer.parseInt(req.getParameter("nTipoMod")));
        contMod.setnConsecutivoModificacion((null == req.getParameter("nConsecutivoMod") || "".equalsIgnoreCase(req.getParameter("nConsecutivoMod"))) ? -1 : Integer.parseInt(req.getParameter("nConsecutivoMod")));
        contMod.setnEstado((null == req.getParameter("nIdEstado") || "".equalsIgnoreCase(req.getParameter("nIdEstado"))) ? -1 : Integer.parseInt(req.getParameter("nIdEstado")));
        contMod.setCadTabla(req.getParameter("arregloDatos"));
        contMod.setnContratoAbierto((null == req.getParameter("lContratoAbierto") || "".equalsIgnoreCase(req.getParameter("lContratoAbierto"))) ? -1 : Integer.parseInt(req.getParameter("lContratoAbierto")));
        contMod.setnConvenioEjercicioAnt((null == req.getParameter("isConvEjercicioAnt") || "".equalsIgnoreCase(req.getParameter("isConvEjercicioAnt"))) ? -1 : Integer.parseInt(req.getParameter("isConvEjercicioAnt")));
        contMod.setnTotalPlurianual((null == req.getParameter("bEsXTotalPlu") || "".equalsIgnoreCase(req.getParameter("bEsXTotalPlu"))) ? -1 : Integer.parseInt(req.getParameter("bEsXTotalPlu")));
        contMod.setPrefixPath(getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator);
        return contMod;
    }
}
