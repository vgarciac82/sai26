package com.syc.rendicioncuentasFONDEN;

import java.io.File;
import java.sql.Connection;
import java.util.List;
import java.util.Map;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import com.syc.ws.fonden.PolizaAutomatica;
import com.syc.ws.fonden.PolizaAutomaticaDetalle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class RendicionCuentasFONDENBusinessLogic extends DataSourceManager {

    private static final Logger log = LoggerFactory.getLogger(RendicionCuentasFONDENBusinessLogic.class);

    public RendicionCuentasFONDENBusinessLogic(String jniName) {
        super.init(jniName);
    }

    public void generaCedula(HttpServletRequest req, HttpServletResponse resp, Map<String, String> plantilla) throws Exception {
        Connection conn = null;
        String file = null;
        try {
            conn = getConnection();
            String fecha = req.getParameter("fecha_aplicacion");
            file = RendicionCuentasFONDENManager.rendicionCuentasFONDENManager(conn, fecha, plantilla);
            resp.setContentType("application/vnd.ms-excel");
            resp.addHeader("Content-Disposition", "inline; filename=\"" + file + "\"; ");
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

    public PolizaAutomatica generaPolizaFONDEN(String fAplicacion, String unidadEjecutora, String centroContable, String login, String nombreUsuario) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            log.info("Object: {}", String.format("Generando poliza automatica. Fecha de aplicacion [%s] Unidad Ejecutora [%s] Centro Contable [%s] Login [%s] Nombre Usuario [%s]", fAplicacion, unidadEjecutora, centroContable, login, nombreUsuario));
            PolizaAutomatica enc = RendicionCuentasFONDENManager.generaEncabezadoPolizaFONDEN(conn, fAplicacion, unidadEjecutora, centroContable, login, nombreUsuario);
            List<PolizaAutomaticaDetalle> detalle = RendicionCuentasFONDENManager.generaDetallePolizaFONDEN(conn, fAplicacion, unidadEjecutora, centroContable, login, nombreUsuario);
            enc.setDetalle(detalle);
            log.debug("Object: {}", "Poliza Generada: " + enc);
            return enc;
        } finally {
            CloseObject.closeObject(conn);
        }
    }
}
