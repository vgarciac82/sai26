package com.syc.reportes;

import java.io.File;
import java.sql.Connection;
import java.util.Map;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.util.Util;
import com.syc.reportes.core.ReporteAuxiliarCRIManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReporteAuxiliarCRIBusinessLogic extends DataSourceManager {

    public ReporteAuxiliarCRIBusinessLogic(String jniName) {
        super.init(jniName);
    }

    Logger log = LoggerFactory.getLogger(ReporteAuxiliarCRIBusinessLogic.class);

    public void generaAuxiliarCRI(HttpServletRequest request, HttpServletResponse resp, Map<String, String> plantillas) throws Exception {
        Connection conn = null;
        String file = null;
        try {
            conn = getConnection();
            String buscaCtaMayor = request.getParameter("ctaMayorCRI");
            String buscaSubCtaMayor = request.getParameter("bsubCtaMayorCRI");
            String cCentroContable = request.getParameter("bcCentroContable");
            String fAuxIni = request.getParameter("bfAuxIniCRI");
            String fAuxFin = request.getParameter("bfAuxFinCRI");
            String conEP = request.getParameter("bconEP");
            file = ReporteAuxiliarCRIManager.ReporteAuxilairCRIManager(conn, buscaCtaMayor, buscaSubCtaMayor, cCentroContable, fAuxIni, fAuxFin, conEP, plantillas);
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
}
