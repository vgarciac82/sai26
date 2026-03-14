package com.syc.gestion.reportes.servlet;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.syc.gestion.reportes.reporteSIIWebDisFinancierasBussinesLogic;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unused")
@WebServlet(name = "ReporteSIIWebDisFinancierasServlet", urlPatterns = { "/reportes/ReporteSIIWebDisFinancieras" })
public class ReporteSIIWebDisFinancierasServlet extends HttpServlet implements GestionInterface {

    private static final long serialVersionUID = -6810376574978594871L;

    String jniName;

    String jniName2;

    String jniName3;

    private static final Logger log = LoggerFactory.getLogger(ReporteSIIWebDisFinancierasServlet.class);

    private static Map<String, String> plantillas = null;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        String tipoFormato = req.getParameter("csv");
        reporteSIIWebDisFinancierasBussinesLogic objReporte = new reporteSIIWebDisFinancierasBussinesLogic(jniName);
        File fOut = null;
        ServletOutputStream out = null;
        String tipo = req.getParameter("reporte");
        String mensaje = "";
        try {
            if ("csv".equals(tipoFormato)) {
                fOut = objReporte.generaCSV(req, resp, plantillas);
                if (fOut != null) {
                    ServletContext context = getServletConfig().getServletContext();
                    String mimetype = context.getMimeType(fOut.getName());
                    resp.setContentType((mimetype != null) ? mimetype : "application/octet-stream");
                    resp.setContentLength((int) fOut.length());
                    resp.addHeader("Content-Disposition", "inline; filename=\"" + fOut.getName() + "\";");
                    out = resp.getOutputStream();
                    Util.doDownload(out, fOut.getAbsolutePath(), fOut.getName(), mimetype);
                    if (!fOut.delete())
                        fOut.deleteOnExit();
                }
            } else {
                objReporte.generaPlantillaExcel(req, resp, plantillas);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServletException(e);
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
                log.info("Object: {}", "Environment Entry \"dataSourceRefName\" nula usando default \"" + jniName + "\"");
            } else
                log.info("Object: {}", "dataSourceRefName=" + jniName);
        } catch (NamingException exc) {
            jniName = "jdbc/gestion";
            log.info("Object: {}", "Environment Entry \"dataSourceRefName\" no definida usando default \"" + jniName + "\"");
        }
        synchronized (this) {
            if (plantillas == null) {
                plantillas = new HashMap<String, String>();
                plantillas.put("210", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_Formato_210.xls"));
                plantillas.put("221", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_Formato_221.xls"));
                plantillas.put("222", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_Formato_222.xls"));
            }
        }
    }
}
