package com.syc.gestion.reportes;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Map;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import net.sf.jasperreports.engine.JasperRunManager;
import java.util.Base64;
import java.nio.file.Paths;

@SuppressWarnings("unused")
public class reporteSIIWebDisFinancierasManager implements Serializable {

    private final static long serialVersionUID = 1;

    public void execute(Connection conn, HttpServletRequest req, HttpServletResponse resp, String reportName, Map<String, Object> parms) {
        InputStream in = null;
        ServletOutputStream out = null;
        try {
            out = resp.getOutputStream();
            in = new FileInputStream(reportName);
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

    public static File DisponibilidadesFinancierasCSV(Connection conn, String tipo, String nFolio) throws Exception {
        CallableStatement cs = null;
        ResultSet rs = null;
        String query = null;
        File f = null;
        int mes = 0;
        int anio = 0;
        query = "{call sp_l_DisponibleFinanciero( ?, ? )}";
        String fileName = "";
        try {
            if (tipo.equals("210") || tipo.equals("221") || tipo.equals("222")) {
                cs = conn.prepareCall(query);
                cs.setString(1, nFolio);
                cs.setString(2, tipo);
            }
            rs = cs.executeQuery();
            String nombreArchivoDescarga = System.getProperty("java.io.tmpdir") + File.separatorChar + "CSV_" + tipo + "." + "csv";
            f = new File(nombreArchivoDescarga);
            Util.CSVFromResultSetSinEncabezado(f, rs, false);
            return f;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(cs, false);
        }
    }

    public static String FlujoEfectivoExcel(Connection conn, int mesIni, int ejercicioFiscal, String tipo, Map<String, String> plantillas) throws Exception {
        CallableStatement cs = null, cs2 = null, cs3 = null;
        ResultSet rs = null, rs2 = null, rs3 = null;
        String query = null, query2 = null, query3 = null;
        int anio = 0;
        int mes = 0;
        anio = ejercicioFiscal;
        mes = mesIni;
        if (tipo.equals("Org161"))
            query = "{call sp_FlujoEfectivo_Org161( ?, ? )}";
        else if (tipo.equals("Rad162"))
            query = "{call sp_FlujoEfectivo_Rad162( ?, ?)}";
        else if (tipo.equals("Mod163"))
            query = "{call sp_FlujoEfectivo_Mod163( ?, ?)}";
        else if (tipo.equals("Sol164"))
            query = "{call sp_FlujoEfectivo_Sol164( ?, ?)}";
        else if (tipo.equals("Org111"))
            query = "{call sp_FlujoEfectivo_Org111( ?, ?)}";
        else if (tipo.equals("Mod1111"))
            query = "{call sp_FlujoEfectivo_Mod1111( ?, ?)}";
        else if (tipo.equals("Com318"))
            query = "{call sp_GastoProgramable_Com318( ?, ?)}";
        else if (tipo.equals("Pag316"))
            query = "{call sp_GastoProgramable_Pag316( ?, ?)}";
        else if (tipo.equals("Dev319"))
            query = "{call sp_GastoProgramable_Dev319( ?, ?)}";
        else if (tipo.equals("explicaciones")) {
            query = "{call sp_Explicacion_145( ?, ?)}";
            query2 = "{call sp_Explicacion_146( ?, ?)}";
            query3 = "{call sp_Explicacion_147( ?, ?)}";
        } else if (tipo.equals("Com3110"))
            query = "{call sp_GastoProgramable_Com3110( ? )}";
        else if (tipo.equals("Ant114"))
            query = "{call sp_FlujoEfectivo_Ant114( ?, ? )}";
        String fileName = "";
        try {
            if (tipo.equals("Com3110")) {
                cs = conn.prepareCall(query);
                cs.setInt(1, anio);
            } else if (tipo.equals("explicaciones")) {
                cs = conn.prepareCall(query);
                cs.setInt(1, anio);
                cs.setInt(2, mes);
                cs2 = conn.prepareCall(query2);
                cs2.setInt(1, anio);
                cs2.setInt(2, mes);
                cs3 = conn.prepareCall(query3);
                cs3.setInt(1, anio);
                cs3.setInt(2, mes);
            } else {
                cs = conn.prepareCall(query);
                cs.setInt(1, anio);
                cs.setInt(2, mes);
            }
            if (tipo.equals("explicaciones")) {
                rs = cs.executeQuery();
                rs2 = cs2.executeQuery();
                rs3 = cs3.executeQuery();
            } else
                rs = cs.executeQuery();
            if (tipo.equals("Org161") || tipo.equals("Mod163")) {
                if (anio >= 2020) {
                    fileName = generaReporteExcel(rs, tipo, plantillas.get("161y163_2020"), mes, anio);
                } else
                    fileName = generaReporteExcel(rs, tipo, plantillas.get("161y163"), mes, anio);
            } else if (tipo.equals("Rad162")) {
                if (anio >= 2020) {
                    fileName = generaReporteExcel(rs, tipo, plantillas.get("162_2020"), mes, anio);
                } else
                    fileName = generaReporteExcel(rs, tipo, plantillas.get("162"), mes, anio);
            } else if (tipo.equals("Sol164")) {
                if (anio >= 2020) {
                    fileName = generaReporteExcel(rs, tipo, plantillas.get("164_2020"), mes, anio);
                } else
                    fileName = generaReporteExcel(rs, tipo, plantillas.get("164"), mes, anio);
            } else if (tipo.equals("Org111") || tipo.equals("Mod1111")) {
                if (anio == 2018) {
                    fileName = generaReporteExcel(rs, tipo, plantillas.get("111y1111_2018"), mes, anio);
                } else if (anio >= 2020) {
                    fileName = generaReporteExcel(rs, tipo, plantillas.get("111y1111_2020"), mes, anio);
                } else {
                    fileName = generaReporteExcel(rs, tipo, plantillas.get("111y1111"), mes, anio);
                }
            } else if (tipo.equals("Com318")) {
                fileName = generaReporteExcel(rs, tipo, plantillas.get("318"), mes, anio);
            } else if (tipo.equals("Pag316") || tipo.equals("Dev319")) {
                fileName = generaReporteExcel(rs, tipo, plantillas.get("316y319"), mes, anio);
            } else if (tipo.equals("explicaciones")) {
                //|| tipo.equals("146") || tipo.equals("147")){
                fileName = generaReporteExcelExplicaciones(rs, rs2, rs3, tipo, plantillas.get("145y146y147"), anio, mes);
            } else if (tipo.equals("Com3110")) {
                fileName = generaReporteExcel(rs, tipo, plantillas.get("3110"), mes, anio);
            } else if (tipo.equals("Ant114")) {
                fileName = generaReporteExcel114(rs, tipo, plantillas.get("114"), mes, anio);
            }
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(cs, false);
        }
    }

    private static String generaReporteExcel(ResultSet rs, String tipoFormato, String plantillaPath, int mes, int anio) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "ReporteFlujoEfectivo" + "_" + tipoFormato + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        int renglonInicio = 31;
        int renglonInicio14 = 11;
        int columnInicio14 = 0;
        int renglonFin = 150;
        int columnInicio = 22;
        int columnFin = 36;
        int columnaLetra = 15;
        //int mes = Integer.parseInt(fecha.substring(3, 5));
        //String anio = fecha.substring(6, 10);
        String Encabezado2 = null;
        String Encabezado = "EJERCICIO " + anio;
        String momento = tipoFormato.substring(0, 3);
        String MesTrabajo = Util.NOMBRE_MESES_MX[mes - 1] + " 1/";
        if (momento.equals("Pag")) {
            Encabezado2 = "Formato 316 'Análisis Programático del Gasto del SPNF, Pagado'";
        }
        if (momento.equals("Dev")) {
            Encabezado2 = "Formato 319 'Análisis Programático Funcional del Gasto del SPNF, Devengado'";
        }
        if (tipoFormato.equals("Com318") || tipoFormato.equals("Pag316") || tipoFormato.equals("Dev319") || tipoFormato.equals("Com3110")) {
            if (tipoFormato.equals("Com318") || tipoFormato.equals("Com3110")) {
                Row rwEnc3 = (sheet0.getRow(4) == null ? sheet0.createRow(4) : sheet0.getRow(4));
                Cell cell3 = (rwEnc3.getCell(0) == null ? rwEnc3.createCell(0) : rwEnc3.getCell(0));
                cell3.setCellValue(Encabezado);
                sheet0 = Util.resultSetToExcel(rs, sheet0, 7, 0, false);
            } else if (tipoFormato.equals("Pag316") || tipoFormato.equals("Dev319")) {
                Row rwEnc3 = (sheet0.getRow(4) == null ? sheet0.createRow(4) : sheet0.getRow(4));
                Cell cell3 = (rwEnc3.getCell(0) == null ? rwEnc3.createCell(0) : rwEnc3.getCell(0));
                cell3.setCellValue(Encabezado);
                Row rwEnc4 = (sheet0.getRow(2) == null ? sheet0.createRow(2) : sheet0.getRow(2));
                Cell cell4 = (rwEnc4.getCell(0) == null ? rwEnc4.createCell(0) : rwEnc4.getCell(0));
                cell4.setCellValue(Encabezado2);
                sheet0 = Util.resultSetToExcel(rs, sheet0, 7, 0, false);
            }
        } else {
            while (rs.next()) {
                if (tipoFormato.equals("Org111") || tipoFormato.equals("Mod1111")) {
                    Row rwEnc2 = (sheet0.getRow(1) == null ? sheet0.createRow(1) : sheet0.getRow(1));
                    Cell cell2 = (rwEnc2.getCell(15) == null ? rwEnc2.createCell(15) : rwEnc2.getCell(15));
                    cell2.setCellValue(Encabezado);
                    String cuenta = rs.getString("cuenta");
                    int renglon = Util.buscaPrimerCoincidencia(sheet0, 4, 0, cuenta);
                    sheet0 = Util.resultSetToExcelRow(rs, sheet0, renglon, 15, 1, false);
                }
                Row rwEnc1 = (sheet0.getRow(1) == null ? sheet0.createRow(1) : sheet0.getRow(1));
                Cell cell1 = (rwEnc1.getCell(7) == null ? rwEnc1.createCell(7) : rwEnc1.getCell(7));
                cell1.setCellValue(Encabezado);
                if (!tipoFormato.equals("Org111") && !tipoFormato.equals("Mod1111")) {
                    String cuenta = rs.getString("cuenta");
                    int renglon = Util.buscaPrimerCoincidencia(sheet0, renglonInicio, columnaLetra, cuenta);
                    sheet0 = Util.resultSetToExcelRow(rs, sheet0, renglon, 22, 1, false);
                }
            }
        }
        if (tipoFormato.equals("Org111") || tipoFormato.equals("Mod1111")) {
            sheet0 = Util.EvaluaFormula(workbook, sheet0, 4 - 1, 366, 15, 29);
        } else
            sheet0 = Util.EvaluaFormula(workbook, sheet0, renglonInicio - 1, renglonFin, columnInicio, columnFin);
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

    public static String DispFinExcel(Connection conn, String nFolio, String tipo, Map<String, String> plantillas) throws Exception {
        CallableStatement cs = null;
        ResultSet rs = null;
        String query = null;
        if (tipo.equals("Obs112")) {
            query = "{call sp_l_genera112 ( ? )}";
        } else if (tipo.equals("Efe1112")) {
            query = "{call sp_l_generaOnce12 ( ? )} ";
        } else {
            query = "{call sp_l_DisponibleFinanciero(?,?)}";
        }
        String fileName = "";
        try {
            if (tipo.equals("Obs112") || tipo.equals("Efe1112")) {
                cs = conn.prepareCall(query);
                cs.setString(1, nFolio);
            } else {
                cs = conn.prepareCall(query);
                cs.setString(1, nFolio);
                cs.setString(2, tipo);
            }
            rs = cs.executeQuery();
            if (tipo.equals("210")) {
                fileName = generaReporteExcelDF(rs, tipo, plantillas.get("210"), nFolio);
            } else if (tipo.equals("221")) {
                fileName = generaReporteExcelDF(rs, tipo, plantillas.get("221"), nFolio);
            } else if (tipo.equals("222")) {
                fileName = generaReporteExcelDF(rs, tipo, plantillas.get("222"), nFolio);
            } else if (tipo.equals("Obs112")) {
                fileName = generaReporteExcel112(rs, tipo, plantillas.get("112"), nFolio);
            } else if (tipo.equals("Efe1112")) {
                fileName = generaReporteExcelOnce12(rs, tipo, plantillas.get("Once12"), nFolio);
            }
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(cs, false);
        }
    }

    public static String DispFinExcelSelect(Connection conn, String nFolio, String tipo, Map<String, String> plantillas) throws Exception {
        CallableStatement cs = null;
        ResultSet rs = null;
        String query = null;
        if (tipo.equals("Obs112")) {
            query = "{call sp_l_genera112Select(?)}";
        } else if (tipo.equals("Efe1112")) {
            query = "{call sp_l_generaOnce12Select(?)}";
        }
        String fileName = "";
        try {
            if (tipo.equals("Obs112") || tipo.equals("Efe1112")) {
                cs = conn.prepareCall(query);
                cs.setString(1, nFolio);
            }
            rs = cs.executeQuery();
            if (tipo.equals("Obs112")) {
                fileName = generaReporteExcel112(rs, tipo, plantillas.get("112"), nFolio);
            } else if (tipo.equals("Efe1112")) {
                fileName = generaReporteExcelOnce12(rs, tipo, plantillas.get("Once12"), nFolio);
            }
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(cs, false);
        }
    }

    private static String generaReporteExcel112(ResultSet rs, String tipoFormato, String plantillaPath, String nFolio) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = "Reporte112" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        ResultSetMetaData rsMetadata = rs.getMetaData();
        DataFormat df = workbook.createDataFormat();
        CellStyle estiloMoneda = workbook.createCellStyle();
        estiloMoneda.setBorderRight(BorderStyle.THIN);
        estiloMoneda.setBorderLeft(BorderStyle.THIN);
        estiloMoneda.setBorderTop(BorderStyle.DOTTED);
        estiloMoneda.setBorderBottom(BorderStyle.DOTTED);
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 9);
        font.setFontName("Calibri");
        estiloMoneda.setFont(font);
        estiloMoneda.setDataFormat(df.getFormat("#,###,##0.00"));
        while (rs.next()) {
            Row rw = (sheet0.getRow(rs.getInt("nRenglon") - 1) == null ? sheet0.createRow(rs.getInt("nRenglon") - 1) : sheet0.getRow(rs.getInt("nRenglon") - 1));
            Util.createExcelCellRep(16, rw, rs, rsMetadata.getColumnName(6), rsMetadata.getColumnType(6), estiloMoneda);
            Util.createExcelCellRep(17, rw, rs, rsMetadata.getColumnName(7), rsMetadata.getColumnType(7), estiloMoneda);
            Util.createExcelCellRep(18, rw, rs, rsMetadata.getColumnName(8), rsMetadata.getColumnType(8), estiloMoneda);
            Util.createExcelCellRep(19, rw, rs, rsMetadata.getColumnName(9), rsMetadata.getColumnType(9), estiloMoneda);
            Util.createExcelCellRep(20, rw, rs, rsMetadata.getColumnName(10), rsMetadata.getColumnType(10), estiloMoneda);
            Util.createExcelCellRep(21, rw, rs, rsMetadata.getColumnName(11), rsMetadata.getColumnType(11), estiloMoneda);
            Util.createExcelCellRep(22, rw, rs, rsMetadata.getColumnName(12), rsMetadata.getColumnType(12), estiloMoneda);
            Util.createExcelCellRep(23, rw, rs, rsMetadata.getColumnName(13), rsMetadata.getColumnType(13), estiloMoneda);
            Util.createExcelCellRep(24, rw, rs, rsMetadata.getColumnName(14), rsMetadata.getColumnType(14), estiloMoneda);
            Util.createExcelCellRep(25, rw, rs, rsMetadata.getColumnName(15), rsMetadata.getColumnType(15), estiloMoneda);
            Util.createExcelCellRep(26, rw, rs, rsMetadata.getColumnName(16), rsMetadata.getColumnType(16), estiloMoneda);
            Util.createExcelCellRep(27, rw, rs, rsMetadata.getColumnName(17), rsMetadata.getColumnType(17), estiloMoneda);
        }
        sheet0 = Util.EvaluaFormula(workbook, sheet0, 4, 370, 10, 30);
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

    private static String generaReporteExcelOnce12(ResultSet rs, String tipoFormato, String plantillaPath, String nFolio) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = "Reporte1112" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        ResultSetMetaData rsMetadata = rs.getMetaData();
        DataFormat df = workbook.createDataFormat();
        CellStyle estiloMoneda = workbook.createCellStyle();
        estiloMoneda.setBorderRight(BorderStyle.THIN);
        estiloMoneda.setBorderLeft(BorderStyle.THIN);
        estiloMoneda.setBorderTop(BorderStyle.DOTTED);
        estiloMoneda.setBorderBottom(BorderStyle.DOTTED);
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 9);
        font.setFontName("Calibri");
        estiloMoneda.setFont(font);
        estiloMoneda.setDataFormat(df.getFormat("#,###,##0.00"));
        while (rs.next()) {
            Row rw = (sheet0.getRow(rs.getInt("nRenglon") - 1) == null ? sheet0.createRow(rs.getInt("nRenglon") - 1) : sheet0.getRow(rs.getInt("nRenglon") - 1));
            Util.createExcelCellRep(16, rw, rs, rsMetadata.getColumnName(6), rsMetadata.getColumnType(6), estiloMoneda);
            Util.createExcelCellRep(17, rw, rs, rsMetadata.getColumnName(7), rsMetadata.getColumnType(7), estiloMoneda);
            Util.createExcelCellRep(18, rw, rs, rsMetadata.getColumnName(8), rsMetadata.getColumnType(8), estiloMoneda);
            Util.createExcelCellRep(19, rw, rs, rsMetadata.getColumnName(9), rsMetadata.getColumnType(9), estiloMoneda);
            Util.createExcelCellRep(20, rw, rs, rsMetadata.getColumnName(10), rsMetadata.getColumnType(10), estiloMoneda);
            Util.createExcelCellRep(21, rw, rs, rsMetadata.getColumnName(11), rsMetadata.getColumnType(11), estiloMoneda);
            Util.createExcelCellRep(22, rw, rs, rsMetadata.getColumnName(12), rsMetadata.getColumnType(12), estiloMoneda);
            Util.createExcelCellRep(23, rw, rs, rsMetadata.getColumnName(13), rsMetadata.getColumnType(13), estiloMoneda);
            Util.createExcelCellRep(24, rw, rs, rsMetadata.getColumnName(14), rsMetadata.getColumnType(14), estiloMoneda);
            Util.createExcelCellRep(25, rw, rs, rsMetadata.getColumnName(15), rsMetadata.getColumnType(15), estiloMoneda);
            Util.createExcelCellRep(26, rw, rs, rsMetadata.getColumnName(16), rsMetadata.getColumnType(16), estiloMoneda);
            Util.createExcelCellRep(27, rw, rs, rsMetadata.getColumnName(17), rsMetadata.getColumnType(17), estiloMoneda);
        }
        sheet0 = Util.EvaluaFormula(workbook, sheet0, 4, 290, 10, 30);
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

    private static String generaReporteExcelDF(ResultSet rs, String tipoFormato, String plantillaPath, String nFolio) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "ReporteDispFin" + "_" + tipoFormato + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        int cnt = 6;
        ResultSetMetaData rsMetadata = rs.getMetaData();
        DataFormat df = workbook.createDataFormat();
        CellStyle estiloMoneda = workbook.createCellStyle();
        estiloMoneda.setBorderRight(BorderStyle.HAIR);
        estiloMoneda.setBorderLeft(BorderStyle.HAIR);
        estiloMoneda.setBorderTop(BorderStyle.HAIR);
        estiloMoneda.setBorderBottom(BorderStyle.HAIR);
        estiloMoneda.setDataFormat(df.getFormat("#,###,##0.00"));
        CellStyle estiloTabla = workbook.createCellStyle();
        estiloTabla.setBorderRight(BorderStyle.HAIR);
        estiloTabla.setBorderLeft(BorderStyle.HAIR);
        estiloTabla.setBorderTop(BorderStyle.HAIR);
        estiloTabla.setBorderBottom(BorderStyle.HAIR);
        estiloTabla.setAlignment(HorizontalAlignment.CENTER);
        if (tipoFormato.equals("210")) {
            while (rs.next()) {
                Row rw = (sheet0.getRow(cnt) == null ? sheet0.createRow(cnt) : sheet0.getRow(cnt));
                Util.createExcelCellRep(0, rw, rs, rsMetadata.getColumnName(1), rsMetadata.getColumnType(1), estiloTabla);
                Util.createExcelCellRep(1, rw, rs, rsMetadata.getColumnName(2), rsMetadata.getColumnType(2), estiloTabla);
                Util.createExcelCellRep(2, rw, rs, rsMetadata.getColumnName(3), rsMetadata.getColumnType(3), estiloTabla);
                Util.createExcelCellRep(3, rw, rs, rsMetadata.getColumnName(4), rsMetadata.getColumnType(4), estiloTabla);
                Util.createExcelCellRep(4, rw, rs, rsMetadata.getColumnName(5), rsMetadata.getColumnType(5), estiloTabla);
                Util.createExcelCellRep(5, rw, rs, rsMetadata.getColumnName(6), rsMetadata.getColumnType(6), estiloTabla);
                Util.createExcelCellRep(6, rw, rs, rsMetadata.getColumnName(7), rsMetadata.getColumnType(7), estiloTabla);
                Util.createExcelCellRep(7, rw, rs, rsMetadata.getColumnName(8), rsMetadata.getColumnType(8), estiloTabla);
                Util.createExcelCellRep(8, rw, rs, rsMetadata.getColumnName(9), rsMetadata.getColumnType(9), estiloTabla);
                Util.createExcelCellRep(9, rw, rs, rsMetadata.getColumnName(10), rsMetadata.getColumnType(10), estiloTabla);
                Util.createExcelCellRep(10, rw, rs, rsMetadata.getColumnName(11), rsMetadata.getColumnType(11), estiloTabla);
                Util.createExcelCellRep(11, rw, rs, rsMetadata.getColumnName(12), rsMetadata.getColumnType(12), estiloTabla);
                Util.createExcelCellRep(12, rw, rs, rsMetadata.getColumnName(13), rsMetadata.getColumnType(13), estiloTabla);
                Util.createExcelCellRep(13, rw, rs, rsMetadata.getColumnName(14), rsMetadata.getColumnType(14), estiloTabla);
                cnt++;
            }
        } else if (tipoFormato.equals("221")) {
            while (rs.next()) {
                Row rw = (sheet0.getRow(cnt) == null ? sheet0.createRow(cnt) : sheet0.getRow(cnt));
                Util.createExcelCellRep(0, rw, rs, rsMetadata.getColumnName(1), rsMetadata.getColumnType(1), estiloTabla);
                Util.createExcelCellRep(1, rw, rs, rsMetadata.getColumnName(2), rsMetadata.getColumnType(2), estiloTabla);
                Util.createExcelCellRep(2, rw, rs, rsMetadata.getColumnName(3), rsMetadata.getColumnType(3), estiloTabla);
                Util.createExcelCellRep(3, rw, rs, rsMetadata.getColumnName(4), rsMetadata.getColumnType(4), estiloTabla);
                Util.createExcelCellRep(4, rw, rs, rsMetadata.getColumnName(5), rsMetadata.getColumnType(5), estiloTabla);
                Util.createExcelCellRep(5, rw, rs, rsMetadata.getColumnName(6), rsMetadata.getColumnType(6), estiloMoneda);
                Util.createExcelCellRep(6, rw, rs, rsMetadata.getColumnName(7), rsMetadata.getColumnType(7), estiloMoneda);
                cnt++;
            }
        } else if (tipoFormato.equals("222")) {
            while (rs.next()) {
                Row rw = (sheet0.getRow(cnt) == null ? sheet0.createRow(cnt) : sheet0.getRow(cnt));
                Util.createExcelCellRep(0, rw, rs, rsMetadata.getColumnName(1), rsMetadata.getColumnType(1), estiloTabla);
                Util.createExcelCellRep(1, rw, rs, rsMetadata.getColumnName(2), rsMetadata.getColumnType(2), estiloTabla);
                Util.createExcelCellRep(2, rw, rs, rsMetadata.getColumnName(3), rsMetadata.getColumnType(3), estiloTabla);
                Util.createExcelCellRep(3, rw, rs, rsMetadata.getColumnName(4), rsMetadata.getColumnType(4), estiloTabla);
                Util.createExcelCellRep(4, rw, rs, rsMetadata.getColumnName(5), rsMetadata.getColumnType(5), estiloMoneda);
                Util.createExcelCellRep(5, rw, rs, rsMetadata.getColumnName(6), rsMetadata.getColumnType(6), estiloMoneda);
                cnt++;
            }
        }
        File fsalida = new File(file_name);
        FileOutputStream fos = new FileOutputStream(fsalida);
        BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
        workbook.write(bos);
        /* Cierra Flujos */
        workbook.close();
        bos.flush();
        bos.close();
        fos.close();
        return file_name;
    }

    private static String generaReporteExcelExplicaciones(ResultSet rs, ResultSet rs2, ResultSet rs3, String tipoFormato, String plantillaPath, int anio, int mes) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "ReporteExplicaciones" + "_" + tipoFormato + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        Sheet sheet1 = workbook.getSheetAt(1);
        Sheet sheet2 = workbook.getSheetAt(2);
        int renglonInicio = 31;
        int renglonInicio14 = 11;
        int columnInicio14 = 0;
        int renglonFin = 150;
        int columnInicio = 22;
        int columnFin = 36;
        int columnaLetra = 15;
        String cuenta = null;
        String MesTrabajo = Util.NOMBRE_MESES_MX[mes - 1] + " 1/";
        Row rwEnc1 = (sheet0.getRow(5) == null ? sheet0.createRow(5) : sheet0.getRow(5));
        Cell cell1 = (rwEnc1.getCell(1) == null ? rwEnc1.createCell(1) : rwEnc1.getCell(1));
        cell1.setCellValue("AÑO: " + anio);
        Row rwEnc2 = (sheet0.getRow(5) == null ? sheet0.createRow(5) : sheet0.getRow(5));
        Cell cell2 = (rwEnc2.getCell(6) == null ? rwEnc2.createCell(6) : rwEnc2.getCell(6));
        cell2.setCellValue("PERIODO: " + MesTrabajo);
        Row rwEnc3 = (sheet1.getRow(5) == null ? sheet1.createRow(5) : sheet1.getRow(5));
        Cell cell3 = (rwEnc3.getCell(1) == null ? rwEnc3.createCell(1) : rwEnc3.getCell(1));
        cell3.setCellValue("AÑO: " + anio);
        Row rwEnc4 = (sheet1.getRow(5) == null ? sheet1.createRow(5) : sheet1.getRow(5));
        Cell cell4 = (rwEnc4.getCell(6) == null ? rwEnc4.createCell(6) : rwEnc4.getCell(6));
        cell4.setCellValue("PERIODO: " + MesTrabajo);
        Row rwEnc5 = (sheet2.getRow(5) == null ? sheet2.createRow(5) : sheet2.getRow(5));
        Cell cell5 = (rwEnc5.getCell(1) == null ? rwEnc5.createCell(1) : rwEnc5.getCell(1));
        cell5.setCellValue("AÑO: " + anio);
        Row rwEnc6 = (sheet2.getRow(5) == null ? sheet2.createRow(5) : sheet2.getRow(5));
        Cell cell6 = (rwEnc6.getCell(6) == null ? rwEnc6.createCell(6) : rwEnc6.getCell(6));
        cell6.setCellValue("PERIODO: " + MesTrabajo);
        while (rs.next()) {
            cuenta = rs.getString("cuenta");
            int renglon = Util.buscaPrimerCoincidencia(sheet0, renglonInicio14, columnInicio14, cuenta);
            sheet0 = Util.resultSetToExcelRow(rs, sheet0, renglon, 9, 1, false);
        }
        while (rs2.next()) {
            cuenta = rs2.getString("cuenta");
            int renglon = Util.buscaPrimerCoincidencia(sheet1, renglonInicio14, columnInicio14, cuenta);
            sheet1 = Util.resultSetToExcelRow(rs2, sheet1, renglon, 9, 1, false);
        }
        while (rs3.next()) {
            cuenta = rs3.getString("cuenta");
            int renglon = Util.buscaPrimerCoincidencia(sheet2, renglonInicio14, columnInicio14, cuenta);
            sheet2 = Util.resultSetToExcelRow(rs3, sheet2, renglon, 9, 1, false);
        }
        sheet0 = Util.EvaluaFormula(workbook, sheet0, renglonInicio14 - 4, 45, 9, 14);
        sheet1 = Util.EvaluaFormula(workbook, sheet1, renglonInicio14 - 4, 45, 9, 14);
        sheet2 = Util.EvaluaFormula(workbook, sheet2, renglonInicio14 - 4, 45, 9, 14);
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

    private static String generaReporteExcel114(ResultSet rs, String tipoFormato, String plantillaPath, int mes, int anio) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "Formato114" + "_" + tipoFormato + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        ResultSetMetaData rsMetadata = rs.getMetaData();
        Sheet sheet0 = workbook.getSheetAt(0);
        int renglonInicio = 3;
        int cnt = 0;
        CellStyle estiloTabla = workbook.createCellStyle();
        estiloTabla.setBorderRight(BorderStyle.HAIR);
        estiloTabla.setBorderLeft(BorderStyle.HAIR);
        estiloTabla.setBorderTop(BorderStyle.HAIR);
        estiloTabla.setBorderBottom(BorderStyle.HAIR);
        /**/
        while (rs.next()) {
            int rows = renglonInicio + cnt;
            sheet0.shiftRows(rows, sheet0.getLastRowNum(), 1);
            Row rw = (sheet0.getRow(renglonInicio + cnt) == null ? sheet0.getRow(renglonInicio + cnt) : sheet0.getRow(renglonInicio + cnt));
            for (int i = 0; i < rsMetadata.getColumnCount(); i++) {
                Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
            }
            cnt++;
        }
        sheet0 = Util.EvaluaFormula(workbook, sheet0, (renglonInicio + cnt + 1), (renglonInicio + cnt + 3), 2, 18);
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
