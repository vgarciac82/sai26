package com.syc.gestion.reportes.servlet;

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
import com.syc.gestion.reportes.ReporteSIPOT_LGTA70BussinesLogic;
import com.syc.gestion.servlet.GestionInterface;
import jakarta.servlet.annotation.WebServlet;

@WebServlet(name = "ReporteSIPOT_LGTA70Servlet", urlPatterns = { "/reportes/ReporteSIPOT_LGTA70" })
public class ReporteSIPOT_LGTA70Servlet extends HttpServlet implements GestionInterface {

    private static final long serialVersionUID = -6810376574978594871L;

    private static String jniName = "jdbc/gestion";

    private static final Logger log = Logger.getLogger(ReporteSIPOT_LGTA70Servlet.class);

    private static Map<String, String> plantillas = null;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null)
            throw new ServletException("Su session a caducado");
        Usuario u = (Usuario) session.getAttribute(ATT_USER);
        if (u == null)
            throw new ServletException("Su session a caducado");
        ReporteSIPOT_LGTA70BussinesLogic objReporte = new ReporteSIPOT_LGTA70BussinesLogic(jniName);
        try {
            objReporte.generaPlantillaExcel(req, resp, plantillas);
        } catch (Exception e) {
            log.error(e, e);
            //throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

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
                plantillas.put("F43A", getServletContext().getRealPath("Reportes" + File.separator + "Formato_Ingresos_Recibidos_43-XLIII-A.xls"));
                plantillas.put("F43A_v2", getServletContext().getRealPath("Reportes" + File.separator + "Formato_Ingresos_Recibidos_43-XLIII-A_v2.xls"));
                plantillas.put("F43B", getServletContext().getRealPath("Reportes" + File.separator + "Formato_Fraccion_43-XLIII-B.xls"));
                plantillas.put("F43B_v2", getServletContext().getRealPath("Reportes" + File.separator + "Formato_Fraccion_43-XLIII-B_v2.xls"));
                plantillas.put("F31", getServletContext().getRealPath("Reportes" + File.separator + "Formato_Informe_Ingreso_31.xls"));
                plantillas.put("F31_v2", getServletContext().getRealPath("Reportes" + File.separator + "Formato_Informe_Ingreso_31_v2.xls"));
                plantillas.put("F31_v3", getServletContext().getRealPath("Reportes" + File.separator + "Formato_Informe_Ingreso_31_v3.xls"));
            }
        }
    }
}
