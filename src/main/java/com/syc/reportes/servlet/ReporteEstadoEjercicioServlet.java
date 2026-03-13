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
import org.apache.commons.lang.StringUtils;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.reportes.ReporteEstadoEjercicioBusinessLogic;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "ReporteEstadoEjercicioServlet", urlPatterns = { "/reportes/ReporteEstadoEjercicio" })
public class ReporteEstadoEjercicioServlet extends HttpServlet implements GestionInterface {

    private static final long serialVersionUID = 6723127616859732249L;

    private static final Logger log = LoggerFactory.getLogger(ReporteEstadoEjercicioServlet.class);

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
        String tipoReporte = "Plantilla_EstadoDelEjercicio";
        String limp = req.getParameter("limpiar");
        String msg = "";
        if (StringUtils.isEmpty(tipoReporte))
            throw new ServletException("No se recibio el parametro TIPO_REPORTE");
        ReporteEstadoEjercicioBusinessLogic rrs = new ReporteEstadoEjercicioBusinessLogic(jniName);
        try {
            if ("limpiar".equals(limp)) {
                msg = rrs.limpiarEdoEjercicio(req, resp);
            } else
                rrs.generaReporte(req, resp, plantillas);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            msg = e.toString();
        }
        session.setAttribute("msg", msg);
        resp.sendRedirect("../Generador/ReporteEstadoEjercicio.jsp");
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
