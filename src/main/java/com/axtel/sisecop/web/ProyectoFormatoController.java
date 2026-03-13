package com.axtel.sisecop.web;

import java.io.File;
import java.io.IOException;
import javax.naming.InitialContext;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.axtel.sisecop.entities.ProyectoServicio;
import com.axtel.sisecop.reports.core.Anexo2Generator;
import com.axtel.sisecop.services.ProyectoServicioService;
import com.syc.gestion.util.Util;
import net.sf.jasperreports.engine.JRException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet("/SISECOP/anexo2/download")
public class ProyectoFormatoController extends HttpServlet {

    private static final long serialVersionUID = 338085628802187809L;

    private static final Logger log = LoggerFactory.getLogger(ProyectoFormatoController.class);

    private String reportPath;

    private String jniName;

    private ProyectoServicioService proyectoService = null;

    private Anexo2Generator anexo2Generator;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try {
            InitialContext ic = new InitialContext();
            jniName = (String) ic.lookup("java:comp/env/dataSourceRefName");
            if (jniName == null) {
                jniName = "jdbc/gestion";
                log.info("Environment Entry \"dataSourceRefName\" nula usando default \"" + jniName + "\"");
            } else {
                log.info("dataSourceRefName=" + jniName);
            }
            reportPath = getServletContext().getRealPath("Reportes");
            anexo2Generator = new Anexo2Generator(new File(reportPath));
            proyectoService = new ProyectoServicioService(jniName);
        } catch (Exception exc) {
            jniName = "jdbc/gestion";
            log.info("Environment Entry \"dataSourceRefName\" no definida usando default \"" + jniName + "\"");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
        try {
            int id = Integer.parseInt(req.getParameter("id"));
            ProyectoServicio project = proyectoService.findById(id);
            generatePDF(response, project);
        } catch (Exception e) {
            log.error("Error getting project for PDF: " + e.toString(), e);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request");
        }
    }

    private void generatePDF(HttpServletResponse response, ProyectoServicio project) throws Exception {
        try {
            String anexo2File = anexo2Generator.generateReport(project);
            Util.doDownload(response, anexo2File, "Anexo2-" + project.getServicioFolioNum() + ".pdf", "application/pdf");
            log.info("generatePDF - PDF downloaded successfully: " + anexo2File);
        } catch (JRException e) {
            log.error("Error generating PDF: " + e.toString(), e);
        } catch (IOException e) {
            log.error("IO error while writing PDF: " + e.toString(), e);
        }
    }
}
