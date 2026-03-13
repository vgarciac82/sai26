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
import com.syc.reportes.RegeneraModificadoBusinessLogic;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "RegeneraModificadoServlet", urlPatterns = { "/reportes/RegeneraModificado" })
public class RegeneraModificadoServlet extends HttpServlet implements GestionInterface {

    private static final long serialVersionUID = 6723127616859732249L;

    private static final Logger log = LoggerFactory.getLogger(RegeneraModificadoServlet.class);

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
        String msg = "";
        RegeneraModificadoBusinessLogic rrs = new RegeneraModificadoBusinessLogic(jniName);
        try {
            msg = rrs.RegeneraModificado(req, resp);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            msg = e.toString();
        }
        session.setAttribute("msg", msg);
        resp.sendRedirect("../Generador/SubirArchivoReintRect.jsp");
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        synchronized (this) {
            if (plantillas == null) {
                plantillas = new HashMap<String, String>();
                plantillas.put("MASIVO", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_EstadoDelEjercicio.xls"));
            }
        }
    }
}
