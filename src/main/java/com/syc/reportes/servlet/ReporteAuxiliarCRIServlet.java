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
import org.apache.log4j.Logger;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.reportes.ReporteAuxiliarCRIBusinessLogic;
import jakarta.servlet.annotation.WebServlet;

@WebServlet(name = "ReporteAuxiliarCRIServlet", urlPatterns = { "/reportes/ReporteAuxiliarCRI" })
public class ReporteAuxiliarCRIServlet extends HttpServlet implements GestionInterface {

    private static final long serialVersionUID = 6723127616859732249L;

    private static final Logger log = Logger.getLogger(ReporteAuxiliarCRIServlet.class);

    private static String jniName = "jdbc/gestion";

    private static Map<String, String> plantillas = null;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
        ReporteAuxiliarCRIBusinessLogic rrs = new ReporteAuxiliarCRIBusinessLogic(jniName);
        String msg = "";
        try {
            rrs.generaAuxiliarCRI(request, resp, plantillas);
        } catch (Exception e) {
            log.error(e, e);
            msg = e.toString();
        }
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
                plantillas.put("AUXILIARCRI", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_AuxiliarCRI.xls"));
            }
        }
    }
}
