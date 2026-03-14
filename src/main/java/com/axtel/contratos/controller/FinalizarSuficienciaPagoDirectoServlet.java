package com.axtel.contratos.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.axtel.contratos.services.SuficienciaPagoDirectoEncabezadoBusinessLogic;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "FinalizarSuficienciaPagoDirectoServlet", urlPatterns = { "/api/suficiencia/finalizar" })
public class FinalizarSuficienciaPagoDirectoServlet extends HttpServlet {

    private static final long serialVersionUID = -2553000753851280558L;

    private static final Logger log = LoggerFactory.getLogger(FinalizarSuficienciaPagoDirectoServlet.class);

    private final ObjectMapper mapper = new ObjectMapper();

    private SuficienciaPagoDirectoEncabezadoBusinessLogic bl = null;

    private static String jniName = "jdbc/ContratosDS";

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try {
            InitialContext ic = new InitialContext();
            jniName = (String) ic.lookup("java:comp/env/dataSourceRefName");
            if (jniName == null) {
                jniName = "jdbc/gestion";
                log.info("Object: {}", "Environment Entry \"dataSourceRefName\" nula usando default \"" + jniName + "\"");
            } else
                log.info("Object: {}", "dataSourceRefName=" + jniName);
        } catch (NamingException exc) {
            jniName = "jdbc/gestion";
            log.info("Object: {}", "Environment Entry \"dataSourceRefName\" no definida usando default \"" + jniName + "\"");
        }
        bl = new SuficienciaPagoDirectoEncabezadoBusinessLogic(jniName);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json; charset=UTF-8");
        Integer folio = null;
        String folioParam = req.getParameter("folio");
        if (folioParam != null && !folioParam.trim().isEmpty())
            folio = Integer.valueOf(folioParam.trim());
        else
            throw new RuntimeException("Folio con valor no valido.");
        if (folio == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            mapper.writeValue(resp.getWriter(), error("Parámetro 'folio' es requerido."));
            return;
        }
        log.info("Object: {}", "[API] /api/suficiencia/finalizar POST folio=" + folio);
        try {
            boolean ok = bl.finalizarContratoDirecto(folio);
            Map<String, Object> out = new HashMap<>();
            out.put("ok", ok);
            out.put("folio", folio);
            if (ok) {
                resp.setStatus(HttpServletResponse.SC_OK);
                out.put("status", "OK");
                out.put("message", "Trámite finalizado correctamente.");
            } else {
                throw new RuntimeException("No se inserto informacion complementaria.");
            }
            mapper.writeValue(resp.getWriter(), out);
        } catch (Exception ex) {
            log.error("[API] Error al finalizar folio=" + folio + " : " + ex.getMessage(), ex);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            mapper.writeValue(resp.getWriter(), error("Error occurred", "Error al finalizar el trámite: " + ex.getMessage()));
        }
    }

    private Map<String, Object> error(String msg) {
        Map<String, Object> m = new HashMap<>();
        m.put("error", msg);
        return m;
    }
}
