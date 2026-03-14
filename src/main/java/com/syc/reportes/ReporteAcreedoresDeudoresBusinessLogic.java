package com.syc.reportes;

import java.io.File;
import java.sql.Connection;
import java.util.LinkedHashMap;
import java.util.Map;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.util.Util;
import com.syc.reportes.core.ReporteAcreedoresDeudoresManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReporteAcreedoresDeudoresBusinessLogic extends DataSourceManager {

    public ReporteAcreedoresDeudoresBusinessLogic(String jniName) {
        super.init(jniName);
    }

    Logger log = LoggerFactory.getLogger(ReporteAcreedoresDeudoresBusinessLogic.class);

    //PDF
    public void generaCedulas(HttpServletRequest req, HttpServletResponse resp, String tipoReporte, String ruta) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            String fechaInicio = req.getParameter("fecha_inicio");
            String fechaFinal = req.getParameter("fecha_fin");
            String centroContable = req.getParameter("cCentroContable");
            Map<String, Object> parms = new LinkedHashMap<String, Object>();
            parms.put("fechaInicio", fechaInicio);
            parms.put("fechaFinal", fechaFinal);
            parms.put("centroContable", centroContable);
            ReporteAcreedoresDeudoresManager.ReporteAcreedoresDeudoresManager(conn, resp, tipoReporte, ruta, parms);
        } finally {
            CloseObject.closeObject(conn, false);
        }
    }

    public void generaCedulasA(HttpServletRequest req, HttpServletResponse resp, Map<String, String> plantillas, String conUnidadEjecutora) throws Exception {
        Connection conn = null;
        String file = null;
        try {
            conn = getConnection();
            String fechaInicio = req.getParameter("fecha_inicio");
            String fechaFin = req.getParameter("fecha_fin");
            String centroContable = req.getParameter("cCentroContable");
            String tipoCedula = req.getParameter("TIPO_REPORTE");
            if ("1".equals(conUnidadEjecutora))
                file = ReporteAcreedoresDeudoresManager.ReporteAcreedoresConUnidad(conn, fechaInicio, fechaFin, centroContable, tipoCedula, plantillas);
            else
                file = ReporteAcreedoresDeudoresManager.ReporteAcreedoresManager(conn, fechaInicio, fechaFin, centroContable, tipoCedula, plantillas);
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

    public void generaCedulasD(HttpServletRequest req, HttpServletResponse resp, Map<String, String> plantillas, String conFechaComision) throws Exception {
        Connection conn = null;
        String file = null;
        try {
            conn = getConnection();
            String fechaInicio = req.getParameter("fecha_inicio");
            String fechaFin = req.getParameter("fecha_fin");
            String centroContable = req.getParameter("cCentroContable");
            String tipoCedula = req.getParameter("TIPO_REPORTE");
            if ("1".equals(conFechaComision))
                file = ReporteAcreedoresDeudoresManager.ReporteDeudoresConComision(conn, fechaInicio, fechaFin, centroContable, tipoCedula, plantillas);
            else {
                file = ReporteAcreedoresDeudoresManager.ReporteDeudoresManager(conn, fechaInicio, fechaFin, centroContable, tipoCedula, plantillas);
                conn.commit();
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

    public void generaAnalitica(HttpServletRequest req, HttpServletResponse resp, String tipoReporte, String ruta, String fechaIni, String fechaFin, String centroContable) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            Map<String, Object> parms = new LinkedHashMap<String, Object>();
            parms.put("fIni", fechaIni);
            parms.put("fFin", fechaFin);
            parms.put("cc", centroContable);
            parms.put("SUBREPORT_DIR", ruta);
            ReporteAcreedoresDeudoresManager.ReporteAnalitica(conn, resp, tipoReporte, ruta, parms);
        } finally {
            CloseObject.closeObject(conn, false);
        }
    }

    public String limpiarCedula(HttpServletRequest req, HttpServletResponse resp, int tipoReporte, String fechaFin) {
        Connection conn = null;
        String msg = "Los registros se eliminaron correctamente.";
        try {
            conn = getConnection();
            int mesFin = Integer.parseInt(fechaFin.substring(3, 5));
            int anio = Integer.parseInt(fechaFin.substring(6, 10));
            ReporteAcreedoresDeudoresManager.limpiaCedula(conn, mesFin, anio, tipoReporte);
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

    public void generaReporteDev(HttpServletRequest req, HttpServletResponse resp, Map<String, String> plantillas) throws Exception {
        Connection conn = null;
        String file = null;
        try {
            conn = getConnection();
            file = ReporteAcreedoresDeudoresManager.ReporteDevengadoDA(conn, plantillas);
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
