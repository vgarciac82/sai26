package com.axtel.cfdi.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import com.axtel.cfdi.CFDI;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JsonDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class InvoicePDFService {

    private static final Logger log = LoggerFactory.getLogger(InvoicePDFService.class);

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private File reportPath;

    private final Map<String, Object> params;

    public InvoicePDFService(File reportPath) {
        this.reportPath = reportPath;
        params = new HashMap<>();
        params.put("REPORT_DIR", reportPath.getAbsolutePath() + File.separatorChar);
        log.info("Object: {}", "Anexo2Generator - REPORT_DIR: " + params.get("REPORT_DIR"));
        log.info("Object: {}", "Anexo2Generator - Subreport path: " + params.get("REPORT_DIR") + "InvoiceDetail.jasper");
    }

    public File generateInvoicePDF(CFDI cfdi, String outputPath) throws Exception {
        log.info("Object: {}", "Inicio de generación de PDF para el CFDI con ID: " + cfdi.getEncabezado().getCfdiId());
        String cfdiJson = gson.toJson(cfdi);
        log.debug("Object: " + String.valueOf("Representación JSON del CFDI: " + cfdiJson));
        File fOut = generateReport(cfdiJson, outputPath);
        log.info("Object: {}", "PDF generado correctamente en la ruta: " + outputPath);
        return fOut;
    }

    private File generateReport(String jsonString, String outputPath) throws JSONException, JRException, IOException {
        log.trace("Object: {}", "generateReport - Parameters received: jsonString=" + jsonString);
        InputStream reportStream = getClass().getResourceAsStream(reportPath.getAbsolutePath() + File.separatorChar + "sisepot.jasper");
        if (reportStream == null) {
            reportStream = new FileInputStream(reportPath.getAbsolutePath() + File.separator + "InvoiceCNF.jasper");
            log.warn("Object: {}", "generateReport - Using FileInputStream to load report: " + reportPath.getAbsolutePath() + File.separator + "sisepot.jasper");
        }
        log.trace("generateReport - Getting input stream from byte array with UTF-8 charset");
        InputStream jsonInputStream = new ByteArrayInputStream(jsonString.getBytes(StandardCharsets.UTF_8));
        JsonDataSource dataSource = new JsonDataSource(jsonInputStream);
        log.trace("Object: {}", "generateReport - Filling report with parameters: " + params);
        JasperPrint jasperPrint = JasperFillManager.fillReport(reportStream, params, dataSource);
        log.debug("generateReport - Report filled successfully");
        File tempFile = new File(outputPath);
        JasperExportManager.exportReportToPdfFile(jasperPrint, tempFile.getAbsolutePath());
        log.info("Object: {}", "generateReport - Report written to temporary file: " + tempFile.getAbsolutePath());
        log.debug("generateReport - End of operation");
        return tempFile;
    }
}
