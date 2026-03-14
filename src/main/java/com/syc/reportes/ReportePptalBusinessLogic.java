package com.syc.reportes;

import java.io.File;
import java.sql.Connection;
import java.util.Map;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.reportes.reportesBussinesObject;
import com.syc.gestion.util.Util;
import com.syc.reportes.core.ReportePptalManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class ReportePptalBusinessLogic extends DataSourceManager {

    private static Logger log = LoggerFactory.getLogger(reportesBussinesObject.class);

    public ReportePptalBusinessLogic(String jniName) {
        super.init(jniName);
    }

    public void generaReportesPresupuestales(HttpServletRequest req, HttpServletResponse resp, Map<String, String> plantilla) throws Exception {
        Connection conn = null;
        String file = null;
        try {
            //Obtener el tipo de reporte para enviar la plantilla y mandar a su manager
            conn = getConnection();
            String reporte = req.getParameter("TIPO_REPORTE");
            if ("APARTADO".equals(reporte)) {
                file = ReportePptalManager.reporteApartadoManager(conn, plantilla);
            } else if ("PRECOMP".equals(reporte)) {
                file = ReportePptalManager.reportePrecompManager(conn, plantilla);
            } else if ("COMPROMETIDO".equals(reporte)) {
                file = ReportePptalManager.reporteCompManager(conn, plantilla);
            } else if ("DEVENGADO".equals(reporte)) {
                file = ReportePptalManager.reporteDevManager(conn, plantilla);
            } else if ("ACUMULADO".equals(reporte)) {
                file = ReportePptalManager.reporteAccManager(conn, plantilla);
            } else if ("PPTO_DEV".equals(reporte)) {
                file = ReportePptalManager.reportePptoDevengado(conn, plantilla);
            }
            File f = new File(file);
            resp.setContentType("application/octet-stream");
            resp.addHeader("Content-Disposition", "inline; filename=\"" + f.getName() + "\"; ");
            log.debug("======== Fin ejecución del formato en Excel==========");
            ServletOutputStream out = resp.getOutputStream();
            Util.doDownload(out, file, file, "");
            out.flush();
            out.close();
            f = null;
        } catch (Exception e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Object: {}", "Problemas en rollback: " + e2);
                }
            throw e;
        } finally {
            CloseObject.closeObject(conn, false);
            if (file != null) {
                File f = new File(file);
                if (!f.delete())
                    f.deleteOnExit();
            }
        }
    }
}
