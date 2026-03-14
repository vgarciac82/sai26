package com.syc.reportes;

import java.io.File;
import java.sql.Connection;
import java.util.Map;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.util.Util;
import com.syc.reportes.core.ReporteConacManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import java.util.Base64;

public class ReporteConacBusinessLogic extends DataSourceManager {

    public ReporteConacBusinessLogic(String jniName) {
        super.init(jniName);
    }

    public void generaReporte(HttpServletRequest req, HttpServletResponse resp, Map<String, String> plantilla) throws Exception {
        Connection conn = null;
        String file = null;
        try {
            conn = getConnection();
            String Fecha = req.getParameter("fecha_fin");
            int mesFin = Integer.parseInt(Fecha.split("/")[1]);
            int anioFin = Integer.parseInt(Fecha.split("/")[0]);
            file = ReporteConacManager.generaReporteConacManager(conn, mesFin, anioFin, plantilla);
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

    public String generaLibroBalance(String centroContable, int mes, boolean acumulado) throws Exception {
        Connection conn = null;
        try {
            int mesInicio = 1;
            if (!acumulado)
                mesInicio = mes;
            conn = getConnection();
            String filePath = ReporteConacManager.generLibroBalanceFIEL(conn, centroContable, mesInicio, mes);
            return filePath;
        } finally {
            CloseObject.closeObject(conn);
        }
    }
}
