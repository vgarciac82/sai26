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
import com.syc.reportes.ReporteNominaBusinessLogic;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "ReporteNominaServlet", urlPatterns = { "/reportes/ReporteNomina" })
public class ReporteNominaServlet extends HttpServlet implements GestionInterface {

    private static final long serialVersionUID = 6723127616859732249L;

    private static final Logger log = LoggerFactory.getLogger(ReporteNominaServlet.class);

    private static String jniName = "jdbc/gestion";

    private static Map<String, String> plantillas = null;

    //private static Boolean 				tipo_plantilla 		= false;
    //	@SuppressWarnings("unused")
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null)
            throw new ServletException("Su session a caducado");
        Usuario u = (Usuario) session.getAttribute(ATT_USER);
        if (u == null)
            throw new ServletException("Su session a caducado");
        String tipoReporte = req.getParameter("TIPO_REPORTE");
        ReporteNominaBusinessLogic rrs = new ReporteNominaBusinessLogic(jniName);
        try {
            if ("SCOMP".equals(tipoReporte)) {
                rrs.generaReporteSaldosCompromisoMil(req, resp, plantillas);
            } else {
                rrs.generaReporteNomina(req, resp, plantillas);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServletException(e);
        }
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        synchronized (this) {
            if (plantillas == null) {
                plantillas = new HashMap<String, String>();
                plantillas.put("REPNOMINA", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_ResumenNomina.xlsx"));
                plantillas.put("SALDOCOMPROMISOMIL", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_SaldosCompromisoMil.xlsx"));
            }
        }
    }
}
