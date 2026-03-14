package com.syc.reportes;

import java.io.File;
import java.sql.Connection;
import java.util.Map;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.util.Util;
import com.syc.reportes.core.ReporteAdecuacionManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class ReporteAdecuacionBusinessLogic extends DataSourceManager {

    private static Logger log = LoggerFactory.getLogger(ReporteAdecuacionBusinessLogic.class);

    public ReporteAdecuacionBusinessLogic(String jniName) {
        super.init(jniName);
    }

    public void generaReporteAdecuaciones(HttpServletRequest req, HttpServletResponse resp, Map<String, String> plantilla) throws Exception {
        Connection conn = null;
        String file = null;
        try {
            conn = getConnection();
            String fechaInicio = req.getParameter("fecha_inicio");
            String fechaFin = req.getParameter("fecha_fin");
            String ep = req.getParameter("ep");
            String cUR = req.getParameter("cUnidadResponsable");
            if ("*".equals(cUR)) {
                cUR = "%";
            }
            if (!"".equals(StringUtils.trimToEmpty(ep)))
                ep = ep.trim();
            file = ReporteAdecuacionManager.generaReporteAdecuacionesManager(conn, fechaInicio, fechaFin, ep, cUR, plantilla);
            //Descargar archivo
            File f = new File(file);
            resp.setContentType("application/octet-stream");
            resp.addHeader("Content-Disposition", "inline; filename=\"" + f.getName() + "\"; ");
            log.debug("======== Formato de Adecuaciones generado =========");
            ServletOutputStream out = resp.getOutputStream();
            Util.doDownload(out, file, file, "");
            out.flush();
            out.close();
            f.delete();
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
