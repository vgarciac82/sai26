package com.syc.reportes.core;

import java.io.FileInputStream;
import java.sql.Connection;
import java.util.Map;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JasperRunManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class MatrizConversionManager {

    public static final Logger log = LoggerFactory.getLogger(MatrizConversionManager.class);

    public static void cosulta(Connection conn, HttpServletResponse resp, String ruta, Map<String, Object> parms, String reportPath) {
        FileInputStream in = null;
        ServletOutputStream out = null;
        try {
            out = resp.getOutputStream();
            in = new FileInputStream(reportPath);
            JasperRunManager.runReportToPdfStream(in, out, parms, conn);
            resp.setContentType("application/pdf");
            out.flush();
            out.close();
        } catch (Exception exc) {
            exc.printStackTrace();
        } finally {
            try {
                if (in != null)
                    in.close();
                if (out != null)
                    out.close();
            } catch (Exception exc) {
                exc.printStackTrace();
            }
            in = null;
            out = null;
        }
    }
}
