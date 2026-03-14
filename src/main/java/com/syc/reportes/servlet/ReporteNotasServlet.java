package com.syc.reportes.servlet;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.reportes.ReporteNotasBusinessLogic;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

@WebServlet(name = "ReporteNotasServlet", urlPatterns = { "/reportes/ReporteNotas" })
public class ReporteNotasServlet extends HttpServlet implements GestionInterface {

    private static final long serialVersionUID = 6723127616859732249L;

    private static final Logger log = LoggerFactory.getLogger(ReporteNotasServlet.class);

    private static String jniName = "jdbc/gestion";

    private static Map<String, String> plantillas = null;

    // private static Boolean tipo_plantilla = false;
    // @SuppressWarnings("unused")
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        log.debug("Validando sesion");
        if (session == null)
            throw new ServletException("Su session a caducado");
        log.debug("sesion valida");
        log.debug("Validando usuario");
        Usuario u = (Usuario) session.getAttribute(ATT_USER);
        if (u == null)
            throw new ServletException("Su session a caducado");
        log.debug("Usuario valido");
        String general = req.getParameter("reporte");
        log.debug("Object: {}", "General: " + general);
        String tipoReporte = req.getParameter("tipoReporte");
        log.debug("Object: {}", "tipoReporte: " + tipoReporte);
        ReporteNotasBusinessLogic rrs = new ReporteNotasBusinessLogic(jniName);
        log.debug("ReporteNotasBusinessLogic creado ");
        try {
            if ("excel".equals(tipoReporte)) {
                if ("detalle".equals(general)) {
                    rrs.generaReporteDetalle(req, resp, plantillas);
                } else if ("resumen".equals(general)) {
                    rrs.generaReporteResumen(req, resp, plantillas);
                }
            } else {
                if ("detalle".equals(general)) {
                    rrs.generaReporteDetalle(req, resp, plantillas);
                } else if ("resumen".equals(general)) {
                    log.info("Se generan notas a los estados financieros");
                    rrs.generaReporteResumenWord(req, resp, plantillas);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            try {
                resp.setContentType("text/html");
                ServletOutputStream out = resp.getOutputStream();
                out.println("<h1>Error</h1>");
                out.println("<div><label>Ocurrio el siguiente error al procesar la solicitud:</label></div>");
                out.println("<div><textarea cols=\"80\" rows=\"10\">" + e.toString() + "</textarea></div>");
                out.println("<div><label>Intente nuevamente, si el problema persiste notifique a mesa de ayuda.</label></div>");
                out.flush();
                out.close();
            } catch (Exception e2) {
                throw new ServletException(e);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        synchronized (this) {
            if (plantillas == null) {
                plantillas = new HashMap<String, String>();
                plantillas.put("REPNOTAS", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_ReporteNotas.xls"));
                plantillas.put("REPNOTASWORD", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_notas_edo_fin.docx"));
            }
        }
    }
}
