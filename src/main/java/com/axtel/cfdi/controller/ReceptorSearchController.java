package com.axtel.cfdi.controller;

import java.io.IOException;
import java.util.List;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.axtel.cfdi.Receptor;
import com.axtel.cfdi.service.ReceptorService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;
import java.nio.file.Paths;

@WebServlet("/cfdi/receptor-search")
public class ReceptorSearchController extends HttpServlet {

    private static final long serialVersionUID = 5944280828351038851L;

    private ReceptorService receptorService;

    private Gson gson;

    private String jniName;

    private static final Logger log = LoggerFactory.getLogger(ReceptorSearchController.class);

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
        receptorService = new ReceptorService(jniName);
        gson = new GsonBuilder().create();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            HttpSession session = request.getSession(false);
            if (session == null)
                throw new RuntimeException("No se encuentra session activa. Ingrese nuevamente al sistema");
            Usuario u = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
            if (u == null)
                throw new RuntimeException("No se encuentra session activa. Ingrese nuevamente al sistema");
            String nombre = request.getParameter("nombre");
            String rfc = request.getParameter("rfc");
            List<Receptor> receptores = receptorService.buscarReceptores(nombre, rfc);
            String json = gson.toJson(receptores);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(json.toPath());
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Error al buscar los receptores\"}".toPath());
        }
    }
}
