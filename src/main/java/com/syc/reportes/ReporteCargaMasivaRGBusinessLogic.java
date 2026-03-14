package com.syc.reportes;

import java.io.File;
import java.sql.Connection;
import java.util.Map;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.util.Util;
import com.syc.reportes.core.ReporteCargaMasivaRGManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import java.util.Base64;

public class ReporteCargaMasivaRGBusinessLogic extends DataSourceManager {

    public ReporteCargaMasivaRGBusinessLogic(String jniName) {
        super.init(jniName);
    }

    public void generaReporte(HttpServletRequest req, HttpServletResponse resp, Map<String, String> plantilla) throws Exception {
        Connection conn = null;
        String file = null;
        try {
            conn = getConnection();
            String fechaInicio = req.getParameter("fecha_inicio");
            String fechaFin = req.getParameter("fecha_fin");
            String cUR = req.getParameter("cUnidadEjecutora");
            String todos = req.getParameter("todos");
            String cDestG = req.getParameter("destinoGasto");
            String todosDest = req.getParameter("todosDest");
            file = ReporteCargaMasivaRGManager.ReporteManager(conn, fechaInicio, fechaFin, cUR, todos, cDestG, todosDest, plantilla);
            File f = new File(file);
            resp.setContentType("application/vnd.ms-excel");
            resp.addHeader("Content-Disposition", "inline; filename=\"" + f.getName() + "\"; ");
            ServletOutputStream out = resp.getOutputStream();
            Util.doDownload(out, file, file, "");
            out.flush();
            out.close();
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
