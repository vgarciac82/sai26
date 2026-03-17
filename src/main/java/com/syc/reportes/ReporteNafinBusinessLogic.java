package com.syc.reportes;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.util.Util;
import com.syc.reportes.core.ReporteNafinManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;
import java.nio.file.Paths;

public class ReporteNafinBusinessLogic extends DataSourceManager {

    private static final Logger log = LoggerFactory.getLogger(ReporteNafinBusinessLogic.class);

    public ReporteNafinBusinessLogic(String jniName) {
        super.init(jniName);
    }

    public void generaReporte(HttpServletRequest req, HttpServletResponse resp, Map<String, String> plantilla) throws Exception {
        Connection conn = null;
        ArrayList<String> file = null;
        try {
            conn = getConnection();
            String fechaInicio = req.getParameter("fecha_inicio");
            String fechaFin = req.getParameter("fecha_fin");
            String tipoReporte = req.getParameter("reporteTipo");
            String tipoExtraccion = req.getParameter("tipoExtraccion");
            SimpleDateFormat fecha = new SimpleDateFormat("yyyyMMddhhmm");
            String sufijo = fecha.format(new Date(System.currentTimeMillis()));
            String tipo = "pagos";
            String archivo = null;
            if (tipoReporte.equals("Proveedores")) {
                file = ReporteNafinManager.ReporteProveedorManager(conn, fechaInicio, fechaFin, tipoReporte, plantilla);
                tipo = "prov";
            } else if (tipoReporte.equals("Pagos")) {
                if ("txt".equals(tipoExtraccion)) {
                    file = ReporteNafinManager.ReportePagosManager(conn, fechaInicio, fechaFin, tipoReporte, plantilla);
                } else {
                    archivo = ReporteNafinManager.ReportePagosExcelManager(conn, fechaInicio, fechaFin, tipoReporte, plantilla);
                }
            }
            if ("Excel".equals(tipoExtraccion)) {
                File f = new File(archivo);
                resp.setContentType("application/vnd.ms-excel");
                resp.addHeader("Content-Disposition", "inline; filename=\"" + f.getName() + "\"; ");
                ServletOutputStream out = resp.getOutputStream();
                Util.doDownload(out, archivo, archivo, "");
                out.flush();
                out.close();
            } else {
                String archivoNafin = System.getProperty("java.io.tmpdir") + File.separatorChar + "ReporteNafin_" + tipo + "_" + sufijo.trim() + ".txt";
                log.info("Object: {}", "-------Se genera el archivo " + archivoNafin + " ----------");
                BufferedWriter salida = new BufferedWriter(new FileWriter(archivoNafin));
                StringBuffer archivoBuffer = new StringBuffer();
                for (int i = 0; i < file.size(); i++) {
                    archivoBuffer.append(file.get(i));
                }
                String outTextNafin = archivoBuffer.toString();
                salida.write(outTextNafin);
                salida.flush();
                salida.close();
                log.info("Se envia el archivo al doDownload");
                Util.doDownload(resp, archivoNafin, Util.getFileName(archivoNafin), "text/plain");
            }
            conn.commit();
        } catch (Exception e) {
            if (conn != null) {
                conn.rollback();
            }
            log.error(e.getMessage(), e);
            throw e;
        } finally {
            CloseObject.closeObject(conn, false);
        }
    }

    public void finalizaProveedores(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            //Actualizar tproveedornafin en S
            ReporteNafinManager.FinalizaProveedor(conn);
            conn.commit();
        } catch (Exception e) {
            if (conn != null) {
                conn.rollback();
            }
            log.error(e.getMessage(), e);
            throw e;
        } finally {
            CloseObject.closeObject(conn, false);
        }
    }

    public void finalizaPagos(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            String fechaInicio = req.getParameter("fecha_inicio");
            String fechaFin = req.getParameter("fecha_fin");
            ReporteNafinManager.FinalizaPagos(conn, fechaInicio, fechaFin);
            conn.commit();
        } catch (Exception e) {
            if (conn != null) {
                conn.rollback();
            }
            log.error(e.getMessage(), e);
            throw e;
        } finally {
            CloseObject.closeObject(conn, false);
        }
    }
}
