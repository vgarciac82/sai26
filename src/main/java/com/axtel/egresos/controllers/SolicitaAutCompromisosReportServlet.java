package com.axtel.egresos.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.LogManager;
import com.syc.contable.CompromisoBussinessLogic;
import com.syc.gestion.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "CompromisosExcelServlet", urlPatterns = { "/compromisos/excel" })
public class SolicitaAutCompromisosReportServlet extends HttpServlet {

    private static final Logger log = LogManager.getLogger(SolicitaAutCompromisosReportServlet.class);

    private static final long serialVersionUID = -6914266220036308467L;

    private CompromisoBussinessLogic logic;

    private String jniName;

    private static File TEMPLATE_REPORT = null;

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
            if (TEMPLATE_REPORT == null) {
                log.info("Loading Authorization Format Template");
                URL resource = getClass().getClassLoader().getResource("ExcelTemplates/Solicitud_Aut_Compromisos.xlsx");
                if (resource == null) {
                    throw new IllegalArgumentException("file Solicitud_Aut_Compromisos.xlsx not found!");
                } else {
                    TEMPLATE_REPORT = new File(resource.toURI());
                }
            }
        } catch (NamingException | URISyntaxException exc) {
            jniName = "jdbc/gestion";
            log.info("Environment Entry \"dataSourceRefName\" no definida usando default \"" + jniName + "\"");
        }
        logic = new CompromisoBussinessLogic(jniName);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        File tempFile = Util.copyFile(TEMPLATE_REPORT, Util.createTempFile("compromisos_", ".xlsx"));
        try {
            logic.generaListadoCompromisoAut(tempFile);
            resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            resp.setHeader("Content-Disposition", "attachment; filename=solicitud_compromisos_" + Util.getTodayFile() + ".xlsx");
            try (FileInputStream fis = new FileInputStream(tempFile);
                OutputStream out = resp.getOutputStream()) {
                byte[] buffer = new byte[4096];
                int len;
                while ((len = fis.read(buffer)) > 0) {
                    out.write(buffer, 0, len);
                }
            }
        } catch (Exception e) {
            log("Error generando Excel", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error generando Excel");
        } finally {
            tempFile.delete();
        }
    }
}
