package com.syc.reportes;

import java.io.File;
import java.sql.Connection;
import java.util.Map;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.core.UnidadEjecutora;
import com.syc.gestion.util.Util;
import com.syc.reportes.core.ConciliacionesBienesManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import com.syc.sai.core.*;

public class ConciliacionesBienesBusinessLogic extends DataSourceManager {

    public ConciliacionesBienesBusinessLogic(String jniName) {
        super.init(jniName);
    }

    public void conciliacion(HttpServletRequest req, HttpServletResponse resp, Map<String, String> plantilla) throws Exception {
        String file = null;
        Connection conn = null;
        try {
            conn = getConnection();
            String tipo = req.getParameter("nReporte");
            String general = req.getParameter("TIPO_REPORTE");
            String fecha = req.getParameter("fecha_fin");
            UnidadEjecutora ue = UnidadEjecutoraManager.selectUnidadEjecutora(conn, "A02");
            String unidad = ue.getDescripcion();
            System.out.println("Tipo: " + tipo + " Nombre: " + general + " Fecha: " + fecha);
            if ("1".equals(tipo) || "2".equals(tipo) || "3".equals(tipo) || "4".equals(tipo)) {
                file = ConciliacionesBienesManager.generaConciliacionBM(req, conn, tipo, general, plantilla, fecha, unidad);
            } else if ("5".equals(tipo)) {
                file = ConciliacionesBienesManager.generaConciliacionBC(req, conn, tipo, general, plantilla, fecha, unidad);
            } else if ("6".equals(tipo) || "7".equals(tipo)) {
                file = ConciliacionesBienesManager.generaConciliacionBI(req, conn, tipo, general, plantilla, fecha, unidad);
            }
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
