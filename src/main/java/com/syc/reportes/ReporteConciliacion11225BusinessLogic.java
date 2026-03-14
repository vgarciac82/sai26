/**
 */
package com.syc.reportes;

import java.io.File;
import java.sql.Connection;
import java.util.Map;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.util.Util;
import com.syc.reportes.core.ReporteConciliacion11225Manager;
import com.syc.sai.contabilidad.utils.db.CloseObject;

public class ReporteConciliacion11225BusinessLogic extends DataSourceManager {

    public ReporteConciliacion11225BusinessLogic(String jniName) {
        super.init(jniName);
    }

    public String generaReporteConciliacion(HttpServletRequest req, HttpServletResponse resp, Map<String, String> plantilla) throws Exception {
        Connection conn = null;
        String file = null;
        try {
            conn = getConnection();
            String cMes = req.getParameter("cMes");
            String saldo = req.getParameter("saldo");
            file = ReporteConciliacion11225Manager.generaReporteConciliacion(conn, cMes, saldo, plantilla);
            File f = new File(file);
            resp.setContentType("application/vnd.ms-excel");
            resp.addHeader("Content-Disposition", "inline; filename=\"" + f.getName() + "\"; ");
            ServletOutputStream out = resp.getOutputStream();
            Util.doDownload(out, file, file, ".xls");
            out.flush();
            out.close();
            f = null;
        } finally {
            CloseObject.closeObject(conn, false);
            if (file != null) {
                File f = new File(file);
                if (!f.delete())
                    f.deleteOnExit();
            }
        }
        return file;
    }
}
