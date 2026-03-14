package com.syc.reportes;

import java.io.File;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.reportes.reportes;
import com.syc.gestion.reportes.reportesBussinesObject;
import com.syc.gestion.util.Util;
import com.syc.reportes.core.ReporteSipotManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReporteSipotBusinessLogic extends DataSourceManager {

    private static Logger log = LoggerFactory.getLogger(reportesBussinesObject.class);

    double ori1 = 0;

    double ori4 = 0;

    double ori0 = 0;

    double mod1 = 0;

    double mod4 = 0;

    double mod0 = 0;

    double amp1 = 0;

    double amp4 = 0;

    double amp0 = 0;

    double dev1 = 0;

    double dev4 = 0;

    double dev0 = 0;

    double pag1 = 0;

    double pag4 = 0;

    double pag0 = 0;

    double dif1 = 0;

    double dif4 = 0;

    double dif0 = 0;

    public ReporteSipotBusinessLogic(String jniName) {
        super.init(jniName);
    }

    public void generaReporteSipotVarios(HttpServletRequest req, HttpServletResponse resp, Map<String, String> plantilla) throws Exception {
        Connection conn = null;
        String file = null;
        try {
            conn = getConnection();
            String reporte = req.getParameter("TIPO_REPORTE");
            String fechaFin = req.getParameter("fecha_fin");
            int mesFin = Integer.parseInt(fechaFin.split("/")[1]);
            if ("FLUJOOBGT".equals(reporte)) {
                file = ReporteSipotManager.flujoObjetoGastoManager(conn, mesFin, plantilla, fechaFin);
            } else if ("FLUJOCA".equals(reporte)) {
                file = ReporteSipotManager.flujoClasifAdmManager(conn, mesFin, plantilla, fechaFin, reporte);
            } else if ("FLUJOECON".equalsIgnoreCase(reporte)) {
                file = ReporteSipotManager.flujoEconManager(conn, mesFin, plantilla, fechaFin);
            } else if ("FLUJOFUNC".equals(reporte)) {
                file = ReporteSipotManager.flujoFuncManager(conn, mesFin, plantilla, fechaFin);
            } else if ("FLUJOPROG".equals(reporte)) {
                file = ReporteSipotManager.flujoProgramaManager(conn, mesFin, plantilla, fechaFin);
            } else if ("FLUJOOBGTCE".equals(reporte)) {
                file = ReporteSipotManager.flujoObgtCEManager(conn, mesFin, plantilla, fechaFin);
            } else if ("FLUJOCFPE".equals(reporte)) {
                file = ReporteSipotManager.flujoCFPEManager(conn, mesFin, plantilla, fechaFin);
            } else if ("FLUJOEFE".equals(reporte)) {
                file = ReporteSipotManager.flujoEFEManager(conn, mesFin, plantilla, fechaFin);
            } else if ("FLUJOIFE".equals(reporte)) {
                file = ReporteSipotManager.flujoIFEManager(conn, mesFin, plantilla, fechaFin);
            } else if ("ANALITING".equals(reporte)) {
                file = ReporteSipotManager.analiticoManager(conn, mesFin, plantilla, fechaFin);
            } else if ("FLUJOCAA".equals(reporte)) {
                file = ReporteSipotManager.flujoClasifAdmManager(conn, mesFin, plantilla, fechaFin, reporte);
            } else if ("FLUJOCFPEA".equals(reporte)) {
                file = ReporteSipotManager.flujoCFPEAManager(conn, mesFin, plantilla, fechaFin);
            } else if ("avanceFinancieroProg".equalsIgnoreCase(reporte)) {
                file = ReporteSipotManager.avancePPI(conn, mesFin, plantilla, fechaFin);
            } else if ("ANALITING2".equals(reporte)) {
                file = ReporteSipotManager.analiticoManager2(conn, mesFin, plantilla, fechaFin);
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

    public void reporteSipot(HttpServletRequest req, HttpServletResponse resp, String strReport, String ruta) throws Exception {
        Connection conn = null;
        try {
            Integer numRep = Integer.parseInt(req.getParameter("nReporte"), 10);
            //String moneda = req.getParameter("Moneda");
            //String nivel = req.getParameter("Formato");
            //(req.getParameter("chk_firmas") == null ? "3" : req.getParameter("chk_firmas"));
            String numfirmas = "4";
            String fecha = req.getParameter("fecha_fin");
            int anioFin = Integer.parseInt(fecha.split("/")[2]);
            String nombreRep = "";
            if (anioFin <= 2018) {
                nombreRep = "\\logotipo-usuario.gif";
            } else {
                nombreRep = "\\logotipo-usuario.png";
            }
            String centroContable = req.getParameter("cCentroContable");
            String descripcionContable = req.getParameter("cdescripcion");
            String nombre2 = new String(req.getParameter("nombre1").getBytes("ISO-8859-1"), "UTF-8");
            String puesto2 = new String(req.getParameter("cargo1").getBytes("ISO-8859-1"), "UTF-8");
            String nombre3 = new String(req.getParameter("nombre2").getBytes("ISO-8859-1"), "UTF-8");
            String puesto3 = new String(req.getParameter("cargo2").getBytes("ISO-8859-1"), "UTF-8");
            String nombre4 = new String(req.getParameter("nombre3").getBytes("ISO-8859-1"), "UTF-8");
            String puesto4 = new String(req.getParameter("cargo3").getBytes("ISO-8859-1"), "UTF-8");
            String nombre1 = "";
            String puesto1 = "";
            conn = getConnection();
            Map<String, Object> parms = new LinkedHashMap<String, Object>();
            reportes objReporte = new reportes();
            parms.put("mes", Integer.parseInt(fecha.substring(3, 5), 10));
            parms.put("anio", Integer.parseInt(fecha.substring(6), 10));
            //parms.put("miles", Integer.parseInt(moneda,10));
            parms.put("centroContable", centroContable);
            parms.put("nombreCentro", descripcionContable);
            if (numRep == 10 || numRep == 14) {
                reporteAnaliticoIng(conn, Integer.parseInt(fecha.substring(3, 5), 10));
                parms.put("ori1", ori1);
                parms.put("ori4", ori4);
                parms.put("ori0", ori0);
                parms.put("amp0", amp0);
                parms.put("amp1", amp1);
                parms.put("amp4", amp4);
                parms.put("mod1", mod1);
                parms.put("mod4", mod4);
                parms.put("mod0", mod0);
                parms.put("dev0", dev0);
                parms.put("dev1", dev1);
                parms.put("dev4", dev4);
                parms.put("pag1", pag1);
                parms.put("pag4", pag4);
                parms.put("pag0", pag0);
                parms.put("dif0", dif0);
                parms.put("dif1", dif1);
                parms.put("dif4", dif4);
            }
            parms.put("nfirmas", Integer.parseInt(numfirmas, 10));
            parms.put("nombre1", nombre1);
            parms.put("puesto1", puesto1);
            parms.put("nombre2", nombre2);
            parms.put("puesto2", puesto2);
            parms.put("nombre3", nombre3);
            parms.put("puesto3", puesto3);
            parms.put("nombre4", nombre4);
            parms.put("puesto4", puesto4);
            System.out.println(ruta);
            parms.put("SUBREPORT_DIR", ruta);
            parms.put("NOMBRE_REP", nombreRep);
            Util.generaFormatoJasper(conn, resp, ruta, parms, strReport);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.error(exc.getMessage(), exc);
            }
            conn = null;
        }
    }

    private void reporteAnaliticoIng(Connection conn, int mes) throws Exception {
        CallableStatement cll = null;
        ResultSet rs = null;
        String query = "{call sp_reporteAnaliticoIng_det ( ? ) }";
        String descripcion = "";
        double importe = 0;
        try {
            cll = conn.prepareCall(query);
            cll.setInt(1, mes);
            rs = cll.executeQuery();
            while (rs.next()) {
                descripcion = rs.getString(1);
                importe = rs.getDouble(2);
                if ("1ori".equals(descripcion))
                    ori1 = importe;
                if ("4ori".equals(descripcion))
                    ori4 = importe;
                if ("0ori".equals(descripcion))
                    ori0 = importe;
                if ("1amp".equals(descripcion))
                    amp1 = importe;
                if ("4amp".equals(descripcion))
                    amp4 = importe;
                if ("0amp".equals(descripcion))
                    amp0 = importe;
                if ("1mod".equals(descripcion))
                    mod1 = importe;
                if ("4mod".equals(descripcion))
                    mod4 = importe;
                if ("0mod".equals(descripcion))
                    mod0 = importe;
                if ("1dev".equals(descripcion))
                    dev1 = importe;
                if ("4dev".equals(descripcion))
                    dev4 = importe;
                if ("0dev".equals(descripcion))
                    dev0 = importe;
                if ("1pag".equals(descripcion))
                    pag1 = importe;
                if ("4pag".equals(descripcion))
                    pag4 = importe;
                if ("0pag".equals(descripcion))
                    pag0 = importe;
                if ("1dif".equals(descripcion))
                    dif1 = importe;
                if ("4dif".equals(descripcion))
                    dif4 = importe;
                if ("0dif".equals(descripcion))
                    dif0 = importe;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            CloseObject.closeObject(cll);
            CloseObject.closeObject(rs);
        }
    }
}
