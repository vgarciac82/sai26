package com.syc.reportes;

import java.io.File;
import java.sql.Connection;
//import java.sql.SQLException;
//import java.util.LinkedHashMap;
import java.util.Map;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.syc.crud.dsmngr.DataSourceManager;
//import com.syc.gestion.reportes.reportes;
import com.syc.gestion.util.Util;
import com.syc.reportes.core.ReporteViaticosManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;

public class ReporteViaticosBusinessLogic extends DataSourceManager {

    public ReporteViaticosBusinessLogic(String jniName) {
        super.init(jniName);
    }

    public void generaReporte(HttpServletRequest req, HttpServletResponse resp, Map<String, String> plantilla) throws Exception {
        Connection conn = null;
        String file = null;
        try {
            conn = getConnection();
            String Fecha = req.getParameter("fecha_fin");
            String FechaIni = req.getParameter("fecha_ini");
            int mesFin = Integer.parseInt(Fecha.split("/")[1]);
            int mesIni = Integer.parseInt(FechaIni.split("/")[1]);
            file = ReporteViaticosManager.generaReporteViaticosManager(conn, mesIni, mesFin, plantilla);
            File f = new File(file);
            resp.setContentType("application/vnd.ms-excel");
            resp.addHeader("Content-Disposition", "inline; filename=\"" + f.getName() + "\"; ");
            ServletOutputStream out = resp.getOutputStream();
            Util.doDownload(out, file, file, "");
            out.flush();
            out.close();
            conn.commit();
        } finally {
            CloseObject.closeObject(conn, false);
            if (file != null) {
                File f = new File(file);
                if (!f.delete())
                    f.deleteOnExit();
            }
        }
    }

    public void generaReporteGastos(HttpServletRequest req, HttpServletResponse resp, Map<String, String> plantilla) throws Exception {
        Connection conn = null;
        String file = null;
        try {
            conn = getConnection();
            String Fecha = req.getParameter("fecha_ini");
            String FechaFin = req.getParameter("fecha_fin");
            int mesIni = Integer.parseInt(Fecha.split("/")[1]);
            int mesFin = Integer.parseInt(FechaFin.split("/")[1]);
            file = ReporteViaticosManager.generaReporteGastosManager(conn, mesIni, mesFin, plantilla);
            resp.setContentType("application/vnd.ms-excel");
            resp.addHeader("Content-Disposition", "inline; filename=\"" + file + "\"; ");
            ServletOutputStream out = resp.getOutputStream();
            Util.doDownload(out, file, file, "");
            out.flush();
            out.close();
            conn.commit();
        } finally {
            CloseObject.closeObject(conn, false);
            if (file != null) {
                File f = new File(file);
                if (!f.delete())
                    f.deleteOnExit();
            }
        }
    }

    public void generaReporteViatocosyGastos(HttpServletRequest req, HttpServletResponse resp, Map<String, String> plantilla) throws Exception {
        Connection conn = null;
        String file = null;
        try {
            conn = getConnection();
            String Fecha = req.getParameter("fecha_ini");
            String FechaFin = req.getParameter("fecha_fin");
            int mesIni = Integer.parseInt(Fecha.split("/")[1]);
            int mesFin = Integer.parseInt(FechaFin.split("/")[1]);
            int anio = Integer.parseInt(FechaFin.split("/")[2]);
            if (anio >= 2024) {
                file = ReporteViaticosManager.generaReporteViaticosyGastosManagerV2(conn, mesIni, mesFin, plantilla, anio);
            } else if (anio <= 2023 && mesFin <= 3) {
                file = ReporteViaticosManager.generaReporteViaticosyGastosManager(conn, mesIni, mesFin, plantilla, anio);
            } else if (anio >= 2023 && mesFin > 3) {
                file = ReporteViaticosManager.generaReporteViaticosyGastosManagerV2(conn, mesIni, mesFin, plantilla, anio);
            }
            File f = new File(file);
            resp.setContentType("application/vnd.ms-excel");
            resp.addHeader("Content-Disposition", "inline; filename=\"" + f.getName() + "\"; ");
            ServletOutputStream out = resp.getOutputStream();
            Util.doDownload(out, file, file, "");
            out.flush();
            out.close();
            conn.commit();
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

    public void generaReporteModuloViaticos(HttpServletRequest req, HttpServletResponse resp, Map<String, String> plantilla) throws Exception {
        Connection conn = null;
        String file = null;
        try {
            conn = getConnection();
            String UR = req.getParameter("unidadEjecutora");
            String folio = req.getParameter("nFolio");
            //int mesFin = Integer.parseInt(Fecha.split("/")[1]);
            //int mesIni = Integer.parseInt(FechaIni.split("/")[1]);
            String general = req.getParameter("TIPO_REPORTE");
            String FechaIni = req.getParameter("fInicio");
            String FechaFin = req.getParameter("fFin");
            if ("Deudores".equals(general)) {
                file = ReporteViaticosManager.generaDeudoresViaticosManager(conn, folio, plantilla);
            } else if ("MontoXComision".equals(general)) {
                file = ReporteViaticosManager.generaGastosViaticosManager(conn, UR, plantilla);
            } else if ("DiasXUR".equals(general)) {
                file = ReporteViaticosManager.generaDiasAcumuladosManager(conn, UR, plantilla);
            } else if ("SaldoVencimiento".equals(general)) {
                file = ReporteViaticosManager.generaSaldoVencimientoManager(conn, UR, plantilla);
            } else if ("BoletosXUR".equals(general)) {
                file = ReporteViaticosManager.generaBoletosXURManager(conn, UR, FechaIni, FechaFin, plantilla);
            } else if ("AgendasP".equals(general)) {
                file = ReporteViaticosManager.consultarAgendasPendientes(conn, plantilla);
            }
            File f = new File(file);
            resp.setContentType("application/vnd.ms-excel");
            resp.addHeader("Content-Disposition", "inline; filename=\"" + f.getName() + "\"; ");
            ServletOutputStream out = resp.getOutputStream();
            Util.doDownload(out, file, file, "");
            out.flush();
            out.close();
            conn.commit();
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
