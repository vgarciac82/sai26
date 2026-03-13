package com.syc.reportes.servlet;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.reportes.ReportesGreenMexBusinessLogic;
import jakarta.servlet.annotation.WebServlet;

@WebServlet(name = "ReportesGreenMexServlet", urlPatterns = { "/reportes/ReportesGreenMex" })
public class ReportesGreenMexServlet extends HttpServlet implements GestionInterface {

    private static final long serialVersionUID = 6723127616859732249L;

    private static final Logger log = Logger.getLogger(ReportesGreenMexServlet.class);

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
        ReportesGreenMexBusinessLogic rrs = new ReportesGreenMexBusinessLogic(jniName);
        String Nombre_Reporte = req.getParameter("Nombre_Reporte");
        try {
            if ("FINANCIEROGREENMEX".equals(Nombre_Reporte)) {
                rrs.generaReporte(req, resp, plantillas);
            }
        } catch (Exception e) {
            log.error(e, e);
            msg = e.toString();
        }
        session.setAttribute("msg", msg);
        //resp.sendRedirect("../Generador/ReporteIngresos.jsp");
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try {
            InitialContext ic = new InitialContext();
            jniName = (String) ic.lookup("java:comp/env/dataSourceRefName");
            if (jniName == null) {
                jniName = "jdbc/gestion";
                log.info("Environment Entry \"dataSourceRefName\" nula usando default \"" + jniName + "\"");
            } else
                log.info("dataSourceRefName=" + jniName);
        } catch (NamingException exc) {
            jniName = "jdbc/gestion";
            log.info("Environment Entry \"dataSourceRefName\" no definida usando default \"" + jniName + "\"");
        }
        synchronized (this) {
            if (plantillas == null) {
                plantillas = new HashMap<String, String>();
                plantillas.put("FINANCIEROGREENMEX", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_ReporteFinancieroGreenMex.xls"));
            }
        }
    }
}
