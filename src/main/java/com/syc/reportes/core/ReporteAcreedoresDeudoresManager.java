package com.syc.reportes.core;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import com.syc.admin.servlet.ReportsException;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.obrapublica.ConfiguraAplicativoBusinessLogic;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;
import java.nio.file.Paths;

public class ReporteAcreedoresDeudoresManager {

    private static ConfiguraAplicativoBusinessLogic cabl = new ConfiguraAplicativoBusinessLogic(GestionInterface.ATT_CONEXION);

    private static String tipoFuente = cabl.getSystemSetting("FUENTE_INSTITUCIONAL");

    private static final Logger log = LoggerFactory.getLogger(ReporteAcreedoresDeudoresManager.class);

    public static void ReporteAcreedoresDeudoresManager(Connection conn, HttpServletResponse resp, String tipoReporte, String ruta, Map<String, Object> parms) {
        FileInputStream in = null;
        ServletOutputStream out = null;
        try {
            out = resp.getOutputStream();
            in = new FileInputStream(tipoReporte);
            JasperRunManager.runReportToPdfStream(in, out, parms, conn);
            resp.setContentType("application/pdf");
            out.flush();
            out.close();
        } catch (Exception exc) {
            exc.printStackTrace();
        } finally {
            try {
                if (in != null)
                    in.close();
                if (out != null)
                    out.close();
            } catch (Exception exc) {
                exc.printStackTrace();
            }
            in = null;
            out = null;
        }
    }

    public static String ReporteAcreedoresManager(Connection conn, String fechaInicio, String fechaFin, String centroContable, String tipoCedula, Map<String, String> plantillas) throws Exception {
        CallableStatement cs = null;
        PreparedStatement ps = null;
        ResultSet rs = null, rs2 = null;
        String query = "{call sp_a_Acreedores( ?, ?, ? )}";
        String query2 = "SELECT cDescripcion FROM tCatalogoCentroContable (NOLOCK) WHERE cCentroContable = ?";
        String fileName = "";
        String mensaje = "";
        try {
            cs = conn.prepareCall(query);
            cs.setString(1, fechaInicio);
            cs.setString(2, fechaFin);
            cs.setString(3, centroContable);
            rs = cs.executeQuery();
            ps = conn.prepareStatement(query2);
            ps.setString(1, centroContable);
            rs2 = ps.executeQuery();
            if (rs2.next()) {
                mensaje = rs2.getString("cDescripcion");
            }
            fileName = generaReporteAcreedores(rs, plantillas.get("ACREEDORES"), fechaInicio, fechaFin, centroContable, mensaje);
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(cs, false);
            CloseObject.closeObject(rs2, false);
            CloseObject.closeObject(ps, false);
        }
    }

    private static String generaReporteAcreedores(ResultSet rs, String plantillaPath, String fechaInicio, String fechaFin, String centroContable, String mensaje) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "ReporteAcreedores" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        ResultSetMetaData rsMetadata = rs.getMetaData();
        int mesFin = 0;
        String anio = "";
        String periodo = "";
        String estado = "";
        int cta2112 = 9;
        int cta2113 = 12;
        int cta2115 = 15;
        int cta2119 = 18;
        int cta2199 = 21;
        mesFin = Integer.parseInt(fechaFin.substring(3, 5));
        anio = fechaInicio.substring(6, 10);
        periodo = "MES DE: " + Util.NOMBRE_MESES_MX[mesFin - 1] + " " + anio;
        Row rwEnc1 = (sheet0.getRow(3) == null ? sheet0.createRow(3) : sheet0.getRow(3));
        Cell cell1 = (rwEnc1.getCell(0) == null ? rwEnc1.createCell(0) : rwEnc1.getCell(0));
        cell1.setCellValue(periodo);
        estado = "GERENCIA ESTATAL DE: " + mensaje;
        Row rwEnc2 = (sheet0.getRow(1) == null ? sheet0.createRow(1) : sheet0.getRow(1));
        Cell cell2 = (rwEnc2.getCell(0) == null ? rwEnc2.createCell(0) : rwEnc2.getCell(0));
        cell2.setCellValue(estado);
        CellStyle estiloTabla = workbook.createCellStyle();
        estiloTabla.setBorderRight(BorderStyle.HAIR);
        estiloTabla.setBorderLeft(BorderStyle.HAIR);
        estiloTabla.setBorderTop(BorderStyle.HAIR);
        estiloTabla.setBorderBottom(BorderStyle.HAIR);
        while (rs.next()) {
            String cuenta = rs.getString(1).substring(0, 4);
            if ("2112".equals(cuenta)) {
                // Se insertan filas cada que se encuentre la cuenta 2112
                int rows = cta2112;
                sheet0.shiftRows(rows, cta2199 + 11, 1);
                Row rw = (sheet0.getRow(cta2112) == null ? sheet0.getRow(cta2112) : sheet0.getRow(cta2112));
                for (int i = 0; i < 14; i++) {
                    Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
                }
                cta2112++;
                cta2113++;
                cta2115++;
                cta2119++;
                cta2199++;
            } else if ("2113".equals(cuenta)) {
                // Se insertan filas cada que se encuentre la cuenta 2113
                int rows = cta2113;
                sheet0.shiftRows(rows, cta2199 + 11, 1);
                Row rw = (sheet0.getRow(cta2113) == null ? sheet0.getRow(cta2113) : sheet0.getRow(cta2113));
                for (int i = 0; i < 14; i++) {
                    Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
                }
                cta2113++;
                cta2115++;
                cta2119++;
                cta2199++;
            } else if ("2115".equals(cuenta)) {
                // Se insertan filas cada que se encuentre la cuenta 2115
                int rows = cta2115;
                sheet0.shiftRows(rows, cta2199 + 11, 1);
                Row rw = (sheet0.getRow(cta2115) == null ? sheet0.getRow(cta2115) : sheet0.getRow(cta2115));
                for (int i = 0; i < 14; i++) {
                    Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
                }
                cta2115++;
                cta2119++;
                cta2199++;
            } else if ("2119".equals(cuenta)) {
                // Se insertan filas cada que se encuentre la cuenta 2119
                int rows = cta2119;
                sheet0.shiftRows(rows, cta2199 + 11, 1);
                Row rw = (sheet0.getRow(cta2119) == null ? sheet0.getRow(cta2119) : sheet0.getRow(cta2119));
                for (int i = 0; i < 14; i++) {
                    Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
                }
                cta2119++;
                cta2199++;
            } else if ("2199".equals(cuenta)) {
                // Se insertan filas cada que se encuentre la cuenta 2199
                int rows = cta2119;
                sheet0.shiftRows(rows, cta2199 + 11, 1);
                Row rw = (sheet0.getRow(cta2199) == null ? sheet0.getRow(cta2199) : sheet0.getRow(cta2199));
                for (int i = 0; i < 14; i++) {
                    Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
                }
                cta2199++;
            }
        }
        sheet0 = Util.EvaluaFormula(workbook, sheet0, 11, (cta2199 + 4), 4, 10);
        File fsalida = new File(file_name);
        FileOutputStream fos = new FileOutputStream(fsalida);
        BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
        workbook.write(bos);
        /* Cierra Flujos */
        bos.flush();
        bos.close();
        fos.close();
        return file_name;
    }

    public static String ReporteDeudoresManager(Connection conn, String fechaInicio, String fechaFin, String centroContable, String tipoCedula, Map<String, String> plantillas) throws Exception {
        log.info("Object: {}", String.format("Iniciando reporte Deudores con los parametros[%S][%S][%S]", fechaInicio, fechaFin, centroContable));
        long startQueries = System.currentTimeMillis();
        CallableStatement cs = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        String query = "{call sp_a_Deudores( ?, ?, ? )}";
        String query2 = "SELECT cDescripcion FROM tCatalogoCentroContable (NOLOCK) WHERE cCentroContable = ?";
        log.trace("Object: {}", "Call[" + query + "]");
        log.trace("Object: {}", "Select[" + query2 + "]");
        String fileName = "";
        String mensaje = "";
        try {
            cs = conn.prepareCall(query);
            cs.setString(1, fechaInicio);
            cs.setString(2, fechaFin);
            cs.setString(3, centroContable);
            log.debug("Object: " + String.valueOf(String.format("Ejecutando[" + query + "]%S, %S, %S", fechaInicio, fechaFin, centroContable)));
            rs = cs.executeQuery();
            ps = conn.prepareStatement(query2);
            ps.setString(1, centroContable);
            log.debug("Object: " + String.valueOf(String.format("Ejecutando[%S] %S", query2, centroContable)));
            rs2 = ps.executeQuery();
            if (rs2.next()) {
                mensaje = rs2.getString("cDescripcion");
            }
            long stopQueries = System.currentTimeMillis();
            log.info("Object: {}", String.format("Ejecucion de consultas terminado en [%2d] segundos", (stopQueries - startQueries) / 1000));
            fileName = generaReporteDeudores(rs, plantillas.get("DEUDORES"), fechaInicio, fechaFin, centroContable, mensaje);
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(cs, false);
            CloseObject.closeObject(ps, false);
            CloseObject.closeObject(rs2, false);
        }
    }

    private static String generaReporteDeudores(ResultSet rs, String plantillaPath, String fechaInicio, String fechaFin, String centroContable, String mensaje) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "ReporteDeudores" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        int cnt = 0;
        ResultSetMetaData rsMetadata = rs.getMetaData();
        int renglonInicio = 9;
        int mesFin = 0;
        String anio = "";
        String periodo = "";
        String estado = "";
        mesFin = Integer.parseInt(fechaFin.substring(3, 5));
        anio = fechaInicio.substring(6, 10);
        periodo = "MES DE: " + Util.NOMBRE_MESES_MX[mesFin - 1] + " " + anio;
        Row rwEnc1 = (sheet0.getRow(3) == null ? sheet0.createRow(3) : sheet0.getRow(3));
        Cell cell1 = (rwEnc1.getCell(0) == null ? rwEnc1.createCell(0) : rwEnc1.getCell(0));
        cell1.setCellValue(periodo);
        estado = "GERENCIA ESTATAL DE: " + mensaje;
        Row rwEnc2 = (sheet0.getRow(1) == null ? sheet0.createRow(1) : sheet0.getRow(1));
        Cell cell2 = (rwEnc2.getCell(0) == null ? rwEnc2.createCell(0) : rwEnc2.getCell(0));
        cell2.setCellValue(estado);
        CellStyle estiloTabla = workbook.createCellStyle();
        estiloTabla.setBorderRight(BorderStyle.HAIR);
        estiloTabla.setBorderLeft(BorderStyle.HAIR);
        estiloTabla.setBorderTop(BorderStyle.HAIR);
        estiloTabla.setBorderBottom(BorderStyle.HAIR);
        while (rs.next()) {
            String cuenta = rs.getString(1).substring(0, 5);
            if ("11231".equals(cuenta)) {
                int rows = renglonInicio + cnt;
                sheet0.shiftRows(rows, sheet0.getLastRowNum(), 1);
                Row rw = (sheet0.getRow(renglonInicio + cnt) == null ? sheet0.getRow(renglonInicio + cnt) : sheet0.getRow(renglonInicio + cnt));
                for (int i = 0; i < 14; i++) {
                    Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
                }
            } else if ("11232".equals(cuenta)) {
                int rows = renglonInicio + cnt + 3;
                sheet0.shiftRows(rows, sheet0.getLastRowNum(), 1);
                Row rw = (sheet0.getRow(renglonInicio + cnt + 3) == null ? sheet0.getRow(renglonInicio + cnt + 3) : sheet0.getRow(renglonInicio + cnt + 3));
                for (int i = 0; i < 14; i++) {
                    Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
                }
            }
            cnt++;
        }
        sheet0 = Util.EvaluaFormula(workbook, sheet0, 11, (renglonInicio + cnt + 6), 4, 10);
        File fsalida = new File(file_name);
        FileOutputStream fos = new FileOutputStream(fsalida);
        BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
        workbook.write(bos);
        /* Cierra Flujos */
        bos.flush();
        bos.close();
        fos.close();
        return file_name;
    }

    public static void ReporteAnalitica(Connection conn, HttpServletResponse resp, String tipoReporte, String ruta, Map<String, Object> parms) {
        FileInputStream in = null;
        ServletOutputStream out = null;
        try {
            out = resp.getOutputStream();
            in = new FileInputStream(tipoReporte);
            resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            byte[] bytes = null;
            JasperPrint jasperPrint = JasperFillManager.fillReport(tipoReporte, parms, conn);
            bytes = convertJasperPrintToExcel(jasperPrint);
            resp.setContentLength(bytes.length);
            out.write(bytes, 0, bytes.length);
            out.flush();
            out.close();
        } catch (Exception exc) {
            exc.printStackTrace();
        } finally {
            try {
                if (in != null)
                    in.close();
                if (out != null)
                    out.close();
            } catch (Exception exc) {
                exc.printStackTrace();
            }
            in = null;
            out = null;
        }
    }

    private static byte[] convertJasperPrintToExcel(JasperPrint jasperPrint) throws JRException {
        try {
            if (jasperPrint == null)
                throw new NullPointerException();
            ByteArrayOutputStream outExcel = new ByteArrayOutputStream();
            JRXlsxExporter exporter = new JRXlsxExporter();
            exporter.setParameter(JRXlsExporterParameter.JASPER_PRINT, jasperPrint);
            exporter.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, outExcel);
            exporter.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
            //exporter.setParameter(JRXlsExporterParameter.OUTPUT_FILE_NAME,"report.xls");
            //exporter.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.TRUE);
            //exporter.setParameter(JRXlsExporterParameter.IS_AUTO_DETECT_CELL_TYPE, Boolean.TRUE);
            //exporter.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
            exporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
            exporter.exportReport();
            return outExcel.toByteArray();
        } catch (Exception ex) {
            throw new ReportsException(ex);
        }
    }

    public static String ReporteDeudoresConComision(Connection conn, String fechaInicio, String fechaFin, String centroContable, String tipoCedula, Map<String, String> plantillas) throws Exception {
        log.info("Object: {}", String.format("Iniciando reporte Deudores con los parametros[%S][%S][%S]", fechaInicio, fechaFin, centroContable));
        long startQueries = System.currentTimeMillis();
        CallableStatement cs = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        String query = "{call sp_a_DeudoresComision( ?, ?, ? )}";
        String query2 = "SELECT cDescripcion FROM tCatalogoCentroContable (NOLOCK) WHERE cCentroContable = ?";
        log.trace("Object: {}", "Call[" + query + "]");
        log.trace("Object: {}", "Select[" + query2 + "]");
        String fileName = "";
        String mensaje = "";
        try {
            cs = conn.prepareCall(query);
            cs.setString(1, fechaInicio);
            cs.setString(2, fechaFin);
            cs.setString(3, centroContable);
            log.debug("Object: " + String.valueOf(String.format("Ejecutando[" + query + "]%S, %S, %S", fechaInicio, fechaFin, centroContable)));
            rs = cs.executeQuery();
            ps = conn.prepareStatement(query2);
            ps.setString(1, centroContable);
            log.debug("Object: " + String.valueOf(String.format("Ejecutando[%S] %S", query2, centroContable)));
            rs2 = ps.executeQuery();
            if (rs2.next()) {
                mensaje = rs2.getString("cDescripcion");
            }
            long stopQueries = System.currentTimeMillis();
            log.info("Object: {}", String.format("Ejecucion de consultas terminado en [%2d] segundos", (stopQueries - startQueries) / 1000));
            fileName = generaReporteDeudoresComision(rs, plantillas.get("DEUDORES_COMISION"), fechaInicio, fechaFin, centroContable, mensaje);
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(cs, false);
            CloseObject.closeObject(ps, false);
            CloseObject.closeObject(rs2, false);
        }
    }

    private static String generaReporteDeudoresComision(ResultSet rs, String plantillaPath, String fechaInicio, String fechaFin, String centroContable, String mensaje) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "ReporteDeudoresComision" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        int cnt = 0;
        ResultSetMetaData rsMetadata = rs.getMetaData();
        int renglonInicio = 8;
        int mesFin = 0;
        String anio = "";
        String periodo = "";
        String estado = "";
        mesFin = Integer.parseInt(fechaFin.substring(3, 5));
        anio = fechaInicio.substring(6, 10);
        periodo = "MES DE: " + Util.NOMBRE_MESES_MX[mesFin - 1] + " " + anio;
        Row rwEnc1 = (sheet0.getRow(3) == null ? sheet0.createRow(3) : sheet0.getRow(3));
        Cell cell1 = (rwEnc1.getCell(0) == null ? rwEnc1.createCell(0) : rwEnc1.getCell(0));
        cell1.setCellValue(periodo);
        estado = "GERENCIA ESTATAL DE: " + mensaje;
        Row rwEnc2 = (sheet0.getRow(1) == null ? sheet0.createRow(1) : sheet0.getRow(1));
        Cell cell2 = (rwEnc2.getCell(0) == null ? rwEnc2.createCell(0) : rwEnc2.getCell(0));
        cell2.setCellValue(estado);
        CellStyle estiloTabla = workbook.createCellStyle();
        estiloTabla.setBorderRight(BorderStyle.HAIR);
        estiloTabla.setBorderLeft(BorderStyle.HAIR);
        estiloTabla.setBorderTop(BorderStyle.HAIR);
        estiloTabla.setBorderBottom(BorderStyle.HAIR);
        while (rs.next()) {
            int rows = renglonInicio + cnt;
            sheet0.shiftRows(rows, sheet0.getLastRowNum(), 1);
            Row rw = (sheet0.getRow(renglonInicio + cnt) == null ? sheet0.createRow(renglonInicio + cnt) : sheet0.getRow(renglonInicio + cnt));
            for (int i = 0; i < 13; i++) {
                Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
            }
            cnt++;
        }
        File fsalida = new File(file_name);
        FileOutputStream fos = new FileOutputStream(fsalida);
        BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
        workbook.write(bos);
        workbook.close();
        /* Cierra Flujos */
        bos.flush();
        bos.close();
        fos.close();
        return file_name;
    }

    public static void limpiaCedula(Connection conn, int mesFin, int anio, int tipoReporte) throws Exception {
        CallableStatement cs = null;
        ResultSet rs = null;
        String query = "{CALL dbo.sp_BorraCedulaAD ( ?, ?, ? ) }";
        try {
            cs = conn.prepareCall(query);
            cs.setInt(1, tipoReporte);
            cs.setInt(2, mesFin);
            cs.setInt(3, anio);
            cs.executeQuery();
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(cs, false);
        }
    }

    public static String ReporteDevengadoDA(Connection conn, Map<String, String> plantillas) throws Exception {
        String fileName = "";
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "select ctipopago, tipopoliza, cc, nfoliopago,cxp, rfc, cnombre, nfoliopoliza" + ", descPoliza, faplicacion, ur, mimporteNeto, ncuenta from v_devengadoNoEjercido";
        try {
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            fileName = generaReporteDevengado(rs, plantillas.get("DEVENGADO"));
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
    }

    private static String generaReporteDevengado(ResultSet rs, String plantillaPath) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "Devengado" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        Date objDate = new Date();
        String dateFormat = "dd-MMM-yyyy";
        SimpleDateFormat formatFecha = new SimpleDateFormat(dateFormat);
        Row rwEnc2 = (sheet0.getRow(3) == null ? sheet0.createRow(3) : sheet0.getRow(3));
        Cell cell2 = (rwEnc2.getCell(0) == null ? rwEnc2.createCell(0) : rwEnc2.getCell(0));
        cell2.setCellValue(formatFecha.format(objDate));
        ResultSetMetaData rsMetadata = rs.getMetaData();
        int renglonInicio = 5;
        int cnt1 = 0;
        Font fon = workbook.createFont();
        fon.setFontHeightInPoints((short) 8);
        fon.setFontName(tipoFuente);
        DataFormat mon = workbook.createDataFormat();
        CellStyle estiloMoney = workbook.createCellStyle();
        estiloMoney.setBorderRight(BorderStyle.THIN);
        estiloMoney.setBorderLeft(BorderStyle.THIN);
        estiloMoney.setBorderBottom(BorderStyle.THIN);
        estiloMoney.setDataFormat(mon.getFormat("#,###,###0.00"));
        estiloMoney.setFont(fon);
        CellStyle estiloGeneral = workbook.createCellStyle();
        estiloGeneral.setBorderRight(BorderStyle.THIN);
        estiloGeneral.setBorderLeft(BorderStyle.THIN);
        estiloGeneral.setBorderBottom(BorderStyle.THIN);
        estiloGeneral.setFont(fon);
        while (rs.next()) {
            Row rw = (sheet0.getRow(renglonInicio + cnt1) == null ? sheet0.createRow(renglonInicio + cnt1) : sheet0.getRow(renglonInicio + cnt1));
            Util.createExcelCellRep(0, rw, rs, rsMetadata.getColumnName(1), rsMetadata.getColumnType(1), estiloGeneral);
            Util.createExcelCellRep(1, rw, rs, rsMetadata.getColumnName(2), rsMetadata.getColumnType(2), estiloGeneral);
            Util.createExcelCellRep(2, rw, rs, rsMetadata.getColumnName(3), rsMetadata.getColumnType(3), estiloGeneral);
            Util.createExcelCellRep(3, rw, rs, rsMetadata.getColumnName(4), rsMetadata.getColumnType(4), estiloGeneral);
            Util.createExcelCellRep(4, rw, rs, rsMetadata.getColumnName(5), rsMetadata.getColumnType(5), estiloGeneral);
            Util.createExcelCellRep(5, rw, rs, rsMetadata.getColumnName(6), rsMetadata.getColumnType(6), estiloGeneral);
            Util.createExcelCellRep(6, rw, rs, rsMetadata.getColumnName(7), rsMetadata.getColumnType(7), estiloGeneral);
            Util.createExcelCellRep(7, rw, rs, rsMetadata.getColumnName(8), rsMetadata.getColumnType(8), estiloGeneral);
            Util.createExcelCellRep(8, rw, rs, rsMetadata.getColumnName(9), rsMetadata.getColumnType(9), estiloGeneral);
            Util.createExcelCellRep(9, rw, rs, rsMetadata.getColumnName(10), rsMetadata.getColumnType(10), estiloGeneral);
            Util.createExcelCellRep(10, rw, rs, rsMetadata.getColumnName(11), rsMetadata.getColumnType(11), estiloGeneral);
            Util.createExcelCellRep(11, rw, rs, rsMetadata.getColumnName(12), rsMetadata.getColumnType(12), estiloMoney);
            Util.createExcelCellRep(12, rw, rs, rsMetadata.getColumnName(13), rsMetadata.getColumnType(13), estiloGeneral);
            cnt1++;
        }
        File fsalida = new File(file_name);
        FileOutputStream fos = new FileOutputStream(fsalida);
        BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
        workbook.write(bos);
        workbook.close();
        /* Cierra Flujos */
        bos.flush();
        bos.close();
        fos.close();
        return file_name;
    }

    public static String ReporteAcreedoresConUnidad(Connection conn, String fechaInicio, String fechaFin, String centroContable, String tipoCedula, Map<String, String> plantillas) throws Exception {
        log.info("Object: {}", String.format("Iniciando reporte Acreedores con los parametros[%S][%S][%S]", fechaInicio, fechaFin, centroContable));
        long startQueries = System.currentTimeMillis();
        CallableStatement cs = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        String query = "{call sp_a_AcreedoresUnidad( ?, ?, ? )}";
        String query2 = "SELECT cDescripcion FROM tCatalogoCentroContable (NOLOCK) WHERE cCentroContable = ?";
        log.trace("Object: {}", "Call[" + query + "]");
        log.trace("Object: {}", "Select[" + query2 + "]");
        String fileName = "";
        String mensaje = "";
        try {
            cs = conn.prepareCall(query);
            cs.setString(1, fechaInicio);
            cs.setString(2, fechaFin);
            cs.setString(3, centroContable);
            log.debug("Object: " + String.valueOf(String.format("Ejecutando[" + query + "]%S, %S, %S", fechaInicio, fechaFin, centroContable)));
            rs = cs.executeQuery();
            ps = conn.prepareStatement(query2);
            ps.setString(1, centroContable);
            log.debug("Object: " + String.valueOf(String.format("Ejecutando[%S] %S", query2, centroContable)));
            rs2 = ps.executeQuery();
            if (rs2.next()) {
                mensaje = rs2.getString("cDescripcion");
            }
            long stopQueries = System.currentTimeMillis();
            log.info("Object: {}", String.format("Ejecucion de consultas terminado en [%2d] segundos", (stopQueries - startQueries) / 1000));
            fileName = generaReporteAcreedoresUnidad(rs, plantillas.get("ACREEDORES_UNIDAD"), fechaInicio, fechaFin, centroContable, mensaje);
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(cs, false);
            CloseObject.closeObject(ps, false);
            CloseObject.closeObject(rs2, false);
        }
    }

    private static String generaReporteAcreedoresUnidad(ResultSet rs, String plantillaPath, String fechaInicio, String fechaFin, String centroContable, String mensaje) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "ReporteAcreedoresUnidad" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        ResultSetMetaData rsMetadata = rs.getMetaData();
        int mesFin = 0;
        String anio = "";
        String periodo = "";
        String estado = "";
        int cta2112 = 9;
        int cta2113 = 12;
        int cta2115 = 15;
        int cta2119 = 18;
        int cta2199 = 21;
        mesFin = Integer.parseInt(fechaFin.substring(3, 5));
        anio = fechaInicio.substring(6, 10);
        periodo = "MES DE: " + Util.NOMBRE_MESES_MX[mesFin - 1] + " " + anio;
        Row rwEnc1 = (sheet0.getRow(3) == null ? sheet0.createRow(3) : sheet0.getRow(3));
        Cell cell1 = (rwEnc1.getCell(0) == null ? rwEnc1.createCell(0) : rwEnc1.getCell(0));
        cell1.setCellValue(periodo);
        estado = "GERENCIA ESTATAL DE: " + mensaje;
        Row rwEnc2 = (sheet0.getRow(1) == null ? sheet0.createRow(1) : sheet0.getRow(1));
        Cell cell2 = (rwEnc2.getCell(0) == null ? rwEnc2.createCell(0) : rwEnc2.getCell(0));
        cell2.setCellValue(estado);
        CellStyle estiloTabla = workbook.createCellStyle();
        estiloTabla.setBorderRight(BorderStyle.HAIR);
        estiloTabla.setBorderLeft(BorderStyle.HAIR);
        estiloTabla.setBorderTop(BorderStyle.HAIR);
        estiloTabla.setBorderBottom(BorderStyle.HAIR);
        while (rs.next()) {
            String cuenta = rs.getString(1).substring(0, 4);
            if ("2112".equals(cuenta)) {
                // Se insertan filas cada que se encuentre la cuenta 2112
                int rows = cta2112;
                sheet0.shiftRows(rows, cta2199 + 11, 1);
                Row rw = (sheet0.getRow(cta2112) == null ? sheet0.getRow(cta2112) : sheet0.getRow(cta2112));
                for (int i = 0; i < 14; i++) {
                    Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
                }
                cta2112++;
                cta2113++;
                cta2115++;
                cta2119++;
                cta2199++;
            } else if ("2113".equals(cuenta)) {
                // Se insertan filas cada que se encuentre la cuenta 2113
                int rows = cta2113;
                sheet0.shiftRows(rows, cta2199 + 11, 1);
                Row rw = (sheet0.getRow(cta2113) == null ? sheet0.getRow(cta2113) : sheet0.getRow(cta2113));
                for (int i = 0; i < 14; i++) {
                    Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
                }
                cta2113++;
                cta2115++;
                cta2119++;
                cta2199++;
            } else if ("2115".equals(cuenta)) {
                // Se insertan filas cada que se encuentre la cuenta 2115
                int rows = cta2115;
                sheet0.shiftRows(rows, cta2199 + 11, 1);
                Row rw = (sheet0.getRow(cta2115) == null ? sheet0.getRow(cta2115) : sheet0.getRow(cta2115));
                for (int i = 0; i < 14; i++) {
                    Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
                }
                cta2115++;
                cta2119++;
                cta2199++;
            } else if ("2119".equals(cuenta)) {
                // Se insertan filas cada que se encuentre la cuenta 2119
                int rows = cta2119;
                sheet0.shiftRows(rows, cta2199 + 11, 1);
                Row rw = (sheet0.getRow(cta2119) == null ? sheet0.getRow(cta2119) : sheet0.getRow(cta2119));
                for (int i = 0; i < 14; i++) {
                    Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
                }
                cta2119++;
                cta2199++;
            } else if ("2199".equals(cuenta)) {
                // Se insertan filas cada que se encuentre la cuenta 2199
                int rows = cta2199;
                sheet0.shiftRows(rows, cta2199 + 11, 1);
                Row rw = (sheet0.getRow(cta2199) == null ? sheet0.getRow(cta2199) : sheet0.getRow(cta2199));
                for (int i = 0; i < 14; i++) {
                    Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
                }
                cta2199++;
            }
        }
        sheet0 = Util.EvaluaFormula(workbook, sheet0, 11, (cta2199 + 4), 4, 10);
        File fsalida = new File(file_name);
        FileOutputStream fos = new FileOutputStream(fsalida);
        BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
        workbook.write(bos);
        /* Cierra Flujos */
        bos.flush();
        bos.close();
        fos.close();
        return file_name;
    }
}
