package com.syc.reportes;

import java.io.File;
import java.sql.Connection;
import java.util.Map;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.util.Util;
import com.syc.reportes.core.ReporteEstadoEjercicioManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReporteEstadoEjercicioBusinessLogic extends DataSourceManager {

    Logger log = LoggerFactory.getLogger(ReporteEstadoEjercicioBusinessLogic.class);

    public ReporteEstadoEjercicioBusinessLogic(String jniName) {
        super.init(jniName);
    }

    public void generaReporte(HttpServletRequest req, HttpServletResponse resp, Map<String, String> plantilla) throws Exception {
        Connection conn = null;
        String file = null;
        try {
            conn = getConnection();
            String mes = req.getParameter("mes");
            String ejercicioFiscal = req.getParameter("ejercicioFiscal");
            int mesIni = Integer.parseInt(mes);
            int anio = Integer.parseInt(ejercicioFiscal);
            file = ReporteEstadoEjercicioManager.ReporteEstadoEjercicioManager(conn, mesIni, anio, plantilla);
            conn.commit();
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

    public String limpiarEdoEjercicio(HttpServletRequest req, HttpServletResponse resp) {
        Connection conn = null;
        String msg = "Los registros se eliminaron correctamente.";
        try {
            conn = getConnection();
            String mes = req.getParameter("mes");
            String ejercicioFiscal = req.getParameter("ejercicioFiscal");
            int mesIni = Integer.parseInt(mes);
            int anio = Integer.parseInt(ejercicioFiscal);
            ReporteEstadoEjercicioManager.limpiaEdoEjercicio(conn, mesIni, anio);
            conn.commit();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            msg = e.toString();
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    log.warn("Problemas realizando rollback " + e2, e2);
                }
        } finally {
            CloseObject.closeObject(conn);
        }
        return msg;
    }
}
