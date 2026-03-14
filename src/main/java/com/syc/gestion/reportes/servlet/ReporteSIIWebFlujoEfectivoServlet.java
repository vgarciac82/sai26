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
import com.syc.gestion.reportes.reporteSIIWebFlujoEfectivoBussinesLogic;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

@SuppressWarnings("unused")
@WebServlet(name = "ReporteSIIWebFlujoEfectivoServlet", urlPatterns = { "/reportes/ReporteSIIWebFlujoEfectivo" })
public class ReporteSIIWebFlujoEfectivoServlet extends HttpServlet implements GestionInterface {

    private static final long serialVersionUID = -6810376574978594871L;

    String jniName;

    String jniName2;

    String jniName3;

    private static final Logger log = LoggerFactory.getLogger(ReporteSIIWebFlujoEfectivoServlet.class);

    private static Map<String, String> plantillas = null;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        String tipoFormato = req.getParameter("csv");
        reporteSIIWebFlujoEfectivoBussinesLogic objReporte = new reporteSIIWebFlujoEfectivoBussinesLogic(jniName);
        File fOut = null;
        ServletOutputStream out = null;
        String mes = req.getParameter("mes");
        int mesIni = Integer.parseInt(mes);
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
                plantillas.put("161y163", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_Formatos_161-163.xls"));
                plantillas.put("162", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_Formato_162.xls"));
                plantillas.put("164", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_Formato_164.xls"));
                plantillas.put("111y1111", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_Formatos_111-1111.xls"));
                plantillas.put("318", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_Formato_318.xls"));
                plantillas.put("316y319", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_Formato_316-319.xls"));
                plantillas.put("145y146y147", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_Formato_145-146-147.xls"));
                plantillas.put("3110", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_Formato_3110.xls"));
                plantillas.put("210", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_Formato_210.xls"));
                plantillas.put("221", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_Formato_221.xls"));
                plantillas.put("222", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_Formato_222.xls"));
                plantillas.put("112", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_Formato_112.xls"));
                plantillas.put("Once12", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_Formato_1112.xls"));
                plantillas.put("111y1111_2018", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_Formatos_111-1111_2018.xls"));
                plantillas.put("114", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_Formato_114.xls"));
                plantillas.put("111y1111_2020", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_Formatos_111-1111_2020.xls"));
                plantillas.put("161y163_2020", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_Formatos_161-163_2020.xls"));
                plantillas.put("162_2020", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_Formato_162_2020.xls"));
                plantillas.put("164_2020", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_Formato_164_2020.xls"));
            }
        }
    }
}
