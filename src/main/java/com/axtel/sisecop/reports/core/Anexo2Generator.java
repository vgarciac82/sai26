package com.axtel.sisecop.reports.core;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.LogManager;
import org.json.JSONException;
import com.axtel.sisecop.entities.ProyectoServicio;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JsonDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Anexo2Generator {

    private File reportPath;

    private final Map<String, Object> params;

    private static final Logger log = LogManager.getLogger(Anexo2Generator.class);

    private final ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

    public Anexo2Generator(File reportPath) {
        this.reportPath = reportPath;
        params = new HashMap<>();
        params.put("REPORT_DIR", reportPath.getAbsolutePath() + File.separatorChar);
        log.info("Anexo2Generator - REPORT_DIR: " + params.get("REPORT_DIR"));
        log.info("Anexo2Generator - Subreport path: " + params.get("REPORT_DIR") + "SubreportTerritory.jasper");
    }

    public String generateReport(ProyectoServicio proyectoServicio) throws JSONException, JRException, IOException {
        log.debug("generateReport - Start");
        String jsonString = ow.writeValueAsString(proyectoServicio);
        return generateReport(jsonString);
    }

    public String generateReport(String jsonString) throws JSONException, JRException, IOException {
        log.trace("generateReport - Parameters received: jsonString=" + jsonString);
        InputStream reportStream = getClass().getResourceAsStream(reportPath.getAbsolutePath() + File.separatorChar + "sisepot.jasper");
        if (reportStream == null) {
            reportStream = new FileInputStream(reportPath.getAbsolutePath() + File.separator + "sisepot.jasper");
            log.warn("generateReport - Using FileInputStream to load report: " + reportPath.getAbsolutePath() + File.separator + "sisepot.jasper");
        }
        log.trace("generateReport - Getting input stream from byte array with UTF-8 charset");
        InputStream jsonInputStream = new ByteArrayInputStream(jsonString.getBytes(StandardCharsets.UTF_8));
        JsonDataSource dataSource = new JsonDataSource(jsonInputStream);
        log.trace("generateReport - Filling report with parameters: " + params);
        JasperPrint jasperPrint = JasperFillManager.fillReport(reportStream, params, dataSource);
        log.debug("generateReport - Report filled successfully");
        File tempFile = File.createTempFile("ProyectoServicioReport", ".pdf", new File(System.getProperty("java.io.tmpdir")));
        JasperExportManager.exportReportToPdfFile(jasperPrint, tempFile.getAbsolutePath());
        log.info("generateReport - Report written to temporary file: " + tempFile.getAbsolutePath());
        log.debug("generateReport - End of operation");
        return tempFile.getAbsolutePath();
    }
}
