package com.syc.contratosplurianuales;

import java.io.File;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.Map;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SolicitudContratoPlurianualBusinessLogic extends DataSourceManager {

    private static final Logger log = LoggerFactory.getLogger(SolicitudContratoPlurianualBusinessLogic.class);

    public SolicitudContratoPlurianualBusinessLogic(String jniName) {
        super.init(jniName);
    }

    public int generaExcelSolicitud(HttpServletRequest req, HttpServletResponse resp, Map<String, String> plantillas) throws Exception {
        Connection conn = null;
        String file = null;
        int iRegistros = 0;
        try {
            conn = getConnection();
            String generaExcel = req.getParameter("generaExcel");
            if ("1".equals(generaExcel)) {
                file = SolicitudContratoPlurianualManager.generaExcelSolicitud(conn, req, resp, plantillas);
            } else if ("2".equals(generaExcel)) {
                file = SolicitudContratoPlurianualManager.generaExcelSolicitudModificado(conn, req, resp, plantillas);
            }
            File f = new File(file);
            conn.commit();
            resp.setContentType("application/vnd.ms-excel");
            resp.addHeader("Content-Disposition", "inline; filename=\"" + f.getName() + "\"; ");
            ServletOutputStream out = resp.getOutputStream();
            Util.doDownload(out, file, file, "");
            out.flush();
            out.close();
        } catch (Exception e) {
            try {
                if (conn != null)
                    conn.rollback();
            } catch (Exception e2) {
                log.warn("Error en rollback: " + e2);
            }
            log.error(e.getMessage(), e);
            throw e;
        } finally {
            CloseObject.closeObject(conn, false);
            if (file != null) {
                File f = new File(file);
                if (!f.delete())
                    f.deleteOnExit();
            }
        }
        return iRegistros;
    }

    public void exportaReportes(String reportPath, String fileName, String folio, int cEsModificado, int nFolioContratoPlurianual, int esOriginal, OutputStream out) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            SolicitudContratoPlurianualManager.exportaReportes(conn, reportPath, fileName, folio, cEsModificado, nFolioContratoPlurianual, esOriginal, out);
        } finally {
            CloseObject.closeObject(conn);
        }
    }
}
