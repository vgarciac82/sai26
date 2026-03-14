package com.syc.gestion.servlet.export;

import java.io.File;
import java.io.IOException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.syc.fortimax.core.ExpedientExporterBusinessLogic;
import com.syc.gestion.util.Util;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "ExpedientExportServlet", urlPatterns = { "/export/expedienteCasos" })
public class ExpedientExportServlet extends HttpServlet {

    private static final long serialVersionUID = 6518385836826324575L;

    private static Logger log = LoggerFactory.getLogger(ExpedientExportServlet.class);

    private String jniName = "";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String[] export = req.getParameterValues("export");
        ExpedientExporterBusinessLogic eebl = new ExpedientExporterBusinessLogic(jniName);
        try {
            String fileResult = eebl.exportExpedients(export);
            File f = new File(fileResult);
            Util.doDownload(resp, fileResult, f.getName(), "");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ServletOutputStream out = resp.getOutputStream();
            out.println("<br/>");
            out.println("<h1>No fue posible generar el archivo de descarga</h1>");
            out.println("<br/>");
            out.println("Notifique el siguiente error al administrador: " + e.toString());
            out.flush();
            out.close();
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
                log.info("Object: {}", "Environment Entry \"dataSourceRefName\" nula usando default \"" + jniName + "\"");
            } else
                log.info("Object: {}", "dataSourceRefName=" + jniName);
        } catch (NamingException exc) {
            jniName = "jdbc/gestion";
            log.info("Object: {}", "Environment Entry \"dataSourceRefName\" no definida usando default \"" + jniName + "\"");
        }
    }
}
