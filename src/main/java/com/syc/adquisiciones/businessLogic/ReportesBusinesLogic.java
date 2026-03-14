package com.syc.adquisiciones.businessLogic;

import java.io.File;
import java.sql.Connection;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import com.syc.adquisiciones.manager.ReportesManager;
import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReportesBusinesLogic extends DataSourceManager {

    public static Logger log = LoggerFactory.getLogger(ReportesBusinesLogic.class);

    Connection conn = null;

    public ReportesBusinesLogic(String jniName) {
        super.init(jniName);
    }

    public void conciliacionesGRM(HttpServletResponse resp, String plantillaPath) throws Exception {
        Connection conn = null;
        String file = null;
        ReportesManager repManager = new ReportesManager();
        String file_name = null;
        try {
            conn = getConnection();
            file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "Apartado_Precom_Compromiso";
            file = repManager.generaApartadoPrecomCompromiso(conn, plantillaPath, file_name);
            resp.setContentType("application/vnd.ms-excel");
            resp.addHeader("Content-Disposition", "inline; filename=\"" + file + "\"; ");
            ServletOutputStream out = resp.getOutputStream();
            Util.doDownload(out, file, file, "");
            conn.commit();
            out.flush();
        } catch (Exception e) {
            // TODO: handle exception
            conn.rollback();
            log.error("Object: {}", e.getMessage());
            throw (e);
        } finally {
            if (conn != null) {
                conn.close();
            }
            if (file != null) {
                File f = new File(file);
                if (!f.delete())
                    f.deleteOnExit();
            }
            repManager = null;
            conn = null;
            file = null;
            file_name = null;
        }
    }
}
