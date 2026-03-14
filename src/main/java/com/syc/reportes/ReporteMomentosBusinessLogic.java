package com.syc.reportes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.util.Util;
import com.syc.obrapublica.ConfiguraAplicativoBusinessLogic;
import com.syc.reportes.core.ReporteMomentosManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import net.sf.jasperreports.engine.JasperRunManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class ReporteMomentosBusinessLogic extends DataSourceManager {

    ConfiguraAplicativoBusinessLogic cabl;

    private static final Logger log = LoggerFactory.getLogger(ReporteMomentosBusinessLogic.class);

    public ReporteMomentosBusinessLogic(String jniName) {
        super.init(jniName);
        cabl = new ConfiguraAplicativoBusinessLogic(jniName);
    }

    public void generaReporteMasivo(HttpServletRequest req, HttpServletResponse resp, Map<String, String> plantilla) throws Exception {
        Connection conn = null;
        String file = null;
        try {
            conn = getConnection();
            String fechaInicio = req.getParameter("fecha_inicio");
            String fechaFin = req.getParameter("fecha_fin");
            file = ReporteMomentosManager.ReporteDMomentosManager(conn, fechaInicio, fechaFin, plantilla);
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

    public void generaReporteMasivoPorSolicitud(HttpServletRequest req, HttpServletResponse resp, String tipoReporte, String tipoReporte2, String ruta) throws Exception {
        Connection conn = null;
        ZipOutputStream zos = null;
        boolean first = true;
        InputStream in = null;
        ServletOutputStream out = null;
        try {
            out = resp.getOutputStream();
            String pathEntrada = cabl.getSystemSetting("RUTA_LISTADO_EXPORTAR");
            conn = getConnection();
            Map<String, Object> parms = new LinkedHashMap<String, Object>();
            int tipoSol = 8;
            BufferedReader reader;
            try {
                reader = new BufferedReader(new FileReader(pathEntrada));
                String line = reader.readLine();
                parms.put("tipoSol", tipoSol);
                parms.put("SUBREPORT_DIR", ruta + File.separator);
                while (line != null) {
                    String cxp = StringUtils.trimToEmpty(line);
                    if (!StringUtils.isEmpty(cxp)) {
                        in = new FileInputStream(tipoReporte2);
                        if (first) {
                            zos = new ZipOutputStream(out);
                            first = false;
                        }
                        parms.put("cxp", cxp);
                        String pdfName = cxp + ".pdf";
                        ZipEntry ze = new ZipEntry(pdfName);
                        zos.putNextEntry(ze);
                        JasperRunManager.runReportToPdfStream(in, zos, parms, conn);
                    }
                    line = reader.readLine();
                    if (in != null)
                        try {
                            in.close();
                        } catch (Exception e) {
                            log.warn("Object: {}", "Problemas cerrando reporte. " + e);
                        }
                }
                if (zos != null) {
                    zos.flush();
                    zos.closeEntry();
                    zos.close();
                    out.flush();
                    out.close();
                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } finally {
            CloseObject.closeObject(conn, false);
        }
    }

    public void generaReportePorSolicitud(HttpServletRequest req, HttpServletResponse resp, String tipoReporte, String tipoReporte2, String tipoReporte3, String tipoReporte4, String ruta) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            Map<String, Object> parms = new LinkedHashMap<String, Object>();
            int tipoSol = Integer.parseInt(req.getParameter("tipo_solicitud"));
            String cxp = req.getParameter("cxp");
            if (cxp != "") {
                parms.put("tipoSol", tipoSol);
                parms.put("cxp", cxp);
                parms.put("SUBREPORT_DIR", ruta + File.separator);
                if (tipoSol == 2 || tipoSol == 3 || tipoSol == 4 || tipoSol == 8) {
                    ReporteMomentosManager.ReporteMomentosPorSolicitud(conn, resp, tipoReporte4, ruta, parms);
                } else {
                    ReporteMomentosManager.ReporteMomentosPorSolicitud(conn, resp, tipoReporte2, ruta, parms);
                }
            } else {
                int numeroSol = Integer.parseInt(req.getParameter("no_solicitud"));
                parms.put("tipoSol", tipoSol);
                parms.put("numeroSol", numeroSol);
                parms.put("SUBREPORT_DIR", ruta + File.separator);
                if (tipoSol == 2 || tipoSol == 3 || tipoSol == 4 || tipoSol == 8) {
                    ReporteMomentosManager.ReporteMomentosPorSolicitud(conn, resp, tipoReporte3, ruta, parms);
                } else {
                    ReporteMomentosManager.ReporteMomentosPorSolicitud(conn, resp, tipoReporte, ruta, parms);
                }
            }
        } finally {
            CloseObject.closeObject(conn, false);
        }
    }
}
