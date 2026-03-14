package com.syc.reportes;

import java.io.File;
import java.sql.Connection;
import java.util.LinkedHashMap;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.reportes.core.MatrizConversionManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import java.util.Base64;

public class MatrizConversionBusinessLogic extends DataSourceManager {

    public MatrizConversionBusinessLogic(String jniName) {
        super.init(jniName);
    }

    public void cosulta(HttpServletRequest req, HttpServletResponse resp, String ruta, String formato) throws Exception {
        Connection conn = null;
        try {
            String reportPath = "";
            conn = getConnection();
            Map<String, Object> parms = new LinkedHashMap<String, Object>();
            parms.put("SUBREPORT_DIR", ruta + File.separator);
            parms.put("tipoDevEg", "1");
            /*parms.put("tipoDevIn","3");
				parms.put("tipoRecIn","4");
				parms.put("tipoDevRecIn","5");*/
            parms.put("tipoDevRecIn", "6");
            reportPath = ruta + File.separator + formato;
            MatrizConversionManager.cosulta(conn, resp, ruta, parms, reportPath);
        } finally {
            CloseObject.closeObject(conn, false);
        }
    }
}
