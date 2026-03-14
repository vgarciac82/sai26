package com.syc.reportes.servlet;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.reportes.PolizaCierreBusinessLogic;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

@WebServlet(name = "PolizaCierreServlet", urlPatterns = { "/reportes/PolizaCierre" })
public class PolizaCierreServlet extends HttpServlet implements GestionInterface {

    private static final long serialVersionUID = 6723127616859732249L;

    private static final Logger log = LoggerFactory.getLogger(PolizaCierreServlet.class);

    private static String jniName = "jdbc/gestion";

    private static Map<String, String> plantillas = null;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null)
            throw new ServletException("Su session a caducado");
        Usuario u = (Usuario) session.getAttribute(ATT_USER);
        if (u == null)
            throw new ServletException("Su session a caducado");
        PolizaCierreBusinessLogic rrs = new PolizaCierreBusinessLogic(jniName);
        try {
            rrs.cosultaTemporal(req, resp, plantillas);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServletException(e);
        }
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null)
            throw new ServletException("Su session a caducado");
        Usuario u = (Usuario) session.getAttribute(ATT_USER);
        if (u == null)
            throw new ServletException("Su session a caducado");
        String msg = "";
        PolizaCierreBusinessLogic rrs = new PolizaCierreBusinessLogic(jniName);
        try {
            rrs.guardaVersion(req, resp);
            msg = "Se creo exitosamente la poliza de cierre.";
            log.info("Poliza creada.");
            session.setAttribute("RESULT", msg);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            msg = "Notifique al Administrador. Ocurrio el siguiente error: " + e;
            session.setAttribute("RESULT", msg);
            throw new ServletException(e);
        }
        resp.sendRedirect("../Generador/PolizaCierre.jsp");
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        synchronized (this) {
            if (plantillas == null) {
                plantillas = new HashMap<String, String>();
                plantillas.put("CIERRE", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_PolizaCierre.xls"));
            }
        }
    }
}
