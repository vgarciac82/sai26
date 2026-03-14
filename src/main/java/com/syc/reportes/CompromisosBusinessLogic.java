package com.syc.reportes;

import java.io.File;
import java.sql.Connection;
import java.util.Map;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.util.Util;
import com.syc.reportes.core.CompromisosManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import java.util.Base64;

public class CompromisosBusinessLogic extends DataSourceManager {

    public CompromisosBusinessLogic(String jniName) {
        super.init(jniName);
    }

    public void generaReporteCompromisos(HttpServletRequest req, HttpServletResponse resp, Map<String, String> plantilla) throws Exception {
        Connection conn = null;
        String file = null;
        try {
            conn = getConnection();
            Integer tipo = Integer.parseInt(req.getParameter("cTipo"));
            if (tipo == 1)
                file = CompromisosManager.generaReporteCompromisosManager(conn, tipo, plantilla);
            else if (tipo == 2)
                file = CompromisosManager.generaReporteCompromisosSICOPManager(conn, tipo, plantilla);
            else if (tipo == 3)
                file = CompromisosManager.generaReporteCompromisosSICOPDuplicadosManager(conn, tipo, plantilla);
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
