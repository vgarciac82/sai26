package com.syc.cfdi.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.syc.cfdi.FacturaBusinessLogic;
import com.syc.gestion.core.Usuario;


@WebServlet(name = "FacturaValidacionesPreAutorizaRG", urlPatterns = {"/cfdi/validacionesPreAutorizaRelacionGastos"})
public class FacturaValidacionesPreAutorizaRGServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(FacturaValidacionesPreAutorizaRGServlet.class);

    private String jniName = null;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        try {
            InitialContext ic = new InitialContext();
            jniName = (String) ic.lookup("java:comp/env/dataSourceRefName");

            if (jniName == null) {
                jniName = "jdbc/gestion";
                log.info("Environment Entry \"dataSourceRefName\" nula usando default \"" + jniName + "\"");
            } else {
                log.info("dataSourceRefName=" + jniName);
            }
        } catch (NamingException exc) {
            jniName = "jdbc/gestion";
            log.info("Environment Entry \"dataSourceRefName\" no definida usando default \"" + jniName + "\"");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.info("Peticion GET a /validacionesPreAutorizaRelacionGastos");

        resp.setContentType("application/json; charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);
        if (session == null) {
            log.warn("Peticion sin sesion o sesion invalida");
            writeJsonError(resp, "Sesion invalida");
            return;
        }

        Usuario u = (Usuario) session.getAttribute("usuario");
        if (u == null) {
            log.warn("Peticion sin usuario en sesion");
            writeJsonError(resp, "Usuario no autenticado");
            return;
        }

        String folioParam = req.getParameter("folio");
        if (folioParam == null) {
            folioParam = req.getParameter("folioRelacionGastos");
        }

        int folio = 0;
        try {
            if (folioParam != null && !folioParam.trim().isEmpty()) {
                folio = Integer.parseInt(folioParam.trim());
            } else {
                log.info("No se recibio folio; se devolvera arreglo vacio");
            }
        } catch (NumberFormatException nfe) {
            log.warn("Parametro folio invalido: " + folioParam, nfe);
            writeJsonError(resp, "Parametro folio invalido");
            return;
        }

        FacturaBusinessLogic fbl = null;
        try {
            fbl = new FacturaBusinessLogic(jniName,false, u);
            log.info("Invocando validacionesPreAutorizaRelacionGastos para folio=" + folio + " usuario=" + u.getLogin());

            List<String> resultados = fbl.validacionesPreAutorizaRelacionGastos(folio);

            String json = toJsonArray(resultados);
            PrintWriter out = resp.getWriter();
            out.write(json);
            out.flush();

            log.info("Respuesta enviada OK para folio=" + folio + " (cantidad=" + (resultados == null ? 0 : resultados.size()) + ")");

        } catch (Exception e) {
            log.error("Error procesando validacionesPreAutorizaRelacionGastos", e);
            writeJsonError(resp, "Ocurrio un error procesando la solicitud");
        }
    }

    private void writeJsonError(HttpServletResponse resp, String msg) throws IOException {
        String safe = sanitize(msg);
        String json = "{\"error\":\"" + safe + "\"}";
        PrintWriter out = resp.getWriter();
        out.write(json);
        out.flush();
    }

    private String toJsonArray(List<String> list) {
        if (list == null || list.isEmpty()) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        boolean first = true;
        for (String s : list) {
            if (!first) {
                sb.append(',');
            }
            sb.append('"').append(sanitize(s)).append('"');
            first = false;
        }
        sb.append(']');
        return sb.toString();
    }

    private String sanitize(String in) {
        if (in == null) {
            return "";
        }
        String out = in.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", " ").replace("\r", " ").replace("\t", " ");
        if (out.length() > 1000) {
            out = out.substring(0, 1000) + "...";
        }
        return out;
    }

}
