package com.syc.contable;

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
import com.syc.reportes.ReporteConciliacion11225BusinessLogic;
import com.syc.reportes.servlet.ReportePolizasServlet;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

@WebServlet(name = "Conciliacion11225", urlPatterns = { "/reportes/conciliacion11225" })
public class Conciliacion11225 extends HttpServlet implements GestionInterface {

    private static final long serialVersionUID = 6723127616859732249L;

    private static final Logger log = LoggerFactory.getLogger(ReportePolizasServlet.class);

    private static String jniName = "jdbc/gestion";

    private static Map<String, String> plantillas = null;

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        ServletOutputStream out = res.getOutputStream();
        HttpSession session = req.getSession(false);
        try {
            if (session == null) {
                log.error("Acceso sin sesion");
                throw new ServletException("Acceso sin sesion. Por favor reingrese al sistema.");
            }
            Usuario u = (Usuario) session.getAttribute(ATT_USER);
            if (u == null) {
                log.error("Acceso sin usuario");
                throw new ServletException("Acceso sin usuario. Por favor reingrese al sistema.");
            }
            ReporteConciliacion11225BusinessLogic rc11225 = new ReporteConciliacion11225BusinessLogic(jniName);
            try {
                rc11225.generaReporteConciliacion(req, res, plantillas);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new ServletException(e);
            }
        } catch (Exception e) {
            out.println("Ocurrio el siguiente error al generar el reporte:<br/>");
            out.println(e.toString() + "<br/>");
            out.println("Por favor notifique al administrador.<br/>");
            out.flush();
            out.close();
        }
    }

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        synchronized (this) {
            if (plantillas == null) {
                plantillas = new HashMap<String, String>();
                plantillas.put("PCONCILIACION11225", getServletContext().getRealPath("Reportes" + File.separator + "FormatoConciliacion11225.xls"));
            }
        }
    }
}
