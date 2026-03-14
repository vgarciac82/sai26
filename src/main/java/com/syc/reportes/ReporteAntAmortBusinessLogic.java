package com.syc.reportes;

import java.io.File;
import java.sql.Connection;
import java.util.Map;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.util.Util;
import com.syc.reportes.core.ReporteAntAmortManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import java.util.Base64;

public class ReporteAntAmortBusinessLogic extends DataSourceManager {

    public ReporteAntAmortBusinessLogic(String jniName) {
        super.init(jniName);
    }

    public void generaReporteAntAmort(HttpServletRequest req, HttpServletResponse resp, Map<String, String> plantillas) throws Exception {
        Connection conn = null;
        String file = null;
        try {
            conn = getConnection();
            String centroContable = req.getParameter("cCentroContable");
            String fechaFin = req.getParameter("fecha_inicio");
            String unidad = req.getParameter("cUnidadResponsable");
            file = ReporteAntAmortManager.generaReporteAntAmort(conn, plantillas, centroContable, fechaFin, unidad);
            File f = new File(file);
            resp.setContentType("application/vnd.ms-excel");
            resp.addHeader("Content-Disposition", "inline; filename=\"" + f.getName() + "\"; ");
            ServletOutputStream out = resp.getOutputStream();
            Util.doDownload(out, file, file, "");
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
    }
}
