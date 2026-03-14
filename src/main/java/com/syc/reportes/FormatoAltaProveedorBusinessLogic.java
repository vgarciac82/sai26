package com.syc.reportes;

import java.io.File;
import java.sql.Connection;
import java.util.Map;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.util.Util;
import com.syc.reportes.core.FormatoAltaProveedorManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;

public class FormatoAltaProveedorBusinessLogic extends DataSourceManager {

    public FormatoAltaProveedorBusinessLogic(String jniName) {
        super.init(jniName);
    }

    public void generaReporteFormatoAltaProveedor(HttpServletRequest req, HttpServletResponse resp, Map<String, String> plantillas, String folio) throws Exception {
        generaReporteFormatoAltaProveedor(req, resp, plantillas, folio, false);
    }

    public void generaReporteFormatoAltaProveedor(HttpServletRequest req, HttpServletResponse resp, Map<String, String> plantillas, String folio, boolean altaRapida) throws Exception {
        Connection conn = null;
        String file = null;
        try {
            conn = getConnection();
            file = FormatoAltaProveedorManager.generaReporteFormatoAltaProveedor(conn, plantillas, folio, altaRapida);
            resp.setContentType("application/vnd.ms-excel");
            resp.addHeader("Content-Disposition", "inline; filename=\"FormatoAltaProveedor_" + folio + ".xls\"; ");
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
