package com.syc.reportes.core;

import java.io.FileInputStream;
import java.sql.Connection;
import java.util.Map;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JasperRunManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReimpresionFormatosManager {

    private static final Logger log = LoggerFactory.getLogger(ReportesGreenMexManager.class);

    public static void ReimpresionPagos(Connection conn, HttpServletResponse resp, String tipoReporte, String ruta, Map<String, Object> parms) {
        FileInputStream in = null;
        ServletOutputStream out = null;
        try {
            out = resp.getOutputStream();
            in = new FileInputStream(tipoReporte);
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
