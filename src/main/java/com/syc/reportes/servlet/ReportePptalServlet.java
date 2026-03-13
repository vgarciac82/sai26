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
import org.apache.log4j.Logger;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.reportes.ReportePptalBusinessLogic;
import jakarta.servlet.annotation.WebServlet;

@WebServlet(name = "ReportePptalServlet", urlPatterns = { "/reportes/ReportePresupuestal" })
public class ReportePptalServlet extends HttpServlet implements GestionInterface {

    private static final long serialVersionUID = 6723127616859732249L;

    private static final Logger log = Logger.getLogger(ReportePptalServlet.class);

    private static String jniName = "jdbc/gestion";

    private static Map<String, String> plantillas = null;

    // private static Boolean tipo_plantilla = false;
    // @SuppressWarnings("unused")
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null)
            throw new ServletException("Su session a caducado");
        Usuario u = (Usuario) session.getAttribute(ATT_USER);
        if (u == null)
            throw new ServletException("Su session a caducado");
        ReportePptalBusinessLogic rrs = new ReportePptalBusinessLogic(jniName);
        try {
            //Reporte presupuestal para obtener los momentos presupuestales
            rrs.generaReportesPresupuestales(req, resp, plantillas);
        } catch (Exception e) {
            log.error(e, e);
            throw new ServletException(e);
        }
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        synchronized (this) {
            if (plantillas == null) {
                plantillas = new HashMap<String, String>();
                plantillas.put("APARTADO", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_apartado.xlsx"));
                plantillas.put("COMPROMETIDO", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_Comprometido.xlsx"));
                plantillas.put("PRECOMP", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_precomp.xlsx"));
                plantillas.put("DEVENGADO", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_Devengado.xlsx"));
                plantillas.put("ACUMULADO", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_Acumulado.xlsx"));
                plantillas.put("PPTO_DEV", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_DevengadoCalendario.xlsx"));
            }
        }
    }
}
