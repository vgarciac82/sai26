package com.syc.reportes.core;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.DecimalFormat;
import java.util.Map;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import java.util.Base64;
import java.nio.file.Paths;

public class ReportePolizasDetManager {

    public static String generaReportePolizasDetManager(Connection conn, String fechaInicio, String fechaFin, String cContable, String tPoliza, String UR, Map<String, String> plantillas) throws Exception {
        CallableStatement cs = null;
        ResultSet rs = null;
        String query = "{call sp_l_repPolizasDet( ?, ?, ?, ?, ? )}";
        String fileName = "";
        try {
            if ("*".equals(UR)) {
                UR = "%";
            }
            cs = conn.prepareCall(query);
            cs.setString(1, fechaInicio);
            cs.setString(2, fechaFin);
            cs.setString(3, cContable);
            cs.setString(4, tPoliza);
            cs.setString(5, UR);
            rs = cs.executeQuery();
            fileName = generaReportePolizasDet(rs, plantillas.get("REPPOLIZASDET"), fechaInicio, fechaFin);
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(cs, false);
        }
    }

    private static String generaReportePolizasDet(ResultSet rs, String plantillaPath, String fechaInicio, String fechaFin) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "ReportePolizasDet" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xlsx";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        XSSFWorkbook workbook = new XSSFWorkbook(fsArchivo);
        fsArchivo.close();
        XSSFSheet sheet0 = workbook.getSheetAt(0);
        int cnt = 0;
        ResultSetMetaData rsMetadata = rs.getMetaData();
        int renglonInicio = 8;
        int mesIni = 0;
        int mesFin = 0;
        String anio = "";
        String periodo = "";
        mesIni = Integer.parseInt(fechaInicio.substring(3, 5));
        mesFin = Integer.parseInt(fechaFin.substring(3, 5));
        anio = fechaInicio.substring(0, 4);
        if (mesIni == mesFin)
            periodo = Util.NOMBRE_MESES_MX[mesIni - 1] + " " + anio;
        else if (mesIni != mesFin)
            periodo = "DE " + Util.NOMBRE_MESES_MX[mesIni - 1] + " A " + Util.NOMBRE_MESES_MX[mesFin - 1] + " " + anio;
        Row rwEnc2 = (sheet0.getRow(5) == null ? sheet0.createRow(5) : sheet0.getRow(5));
        Cell cell2 = (rwEnc2.getCell(1) == null ? rwEnc2.createCell(1) : rwEnc2.getCell(1));
        cell2.setCellValue(periodo);
        CellStyle estiloTabla = Util.generaEstilo2(workbook, 9, false, true, true, true, true, false);
        while (rs.next()) {
            XSSFRow rw = (sheet0.getRow(renglonInicio + cnt) == null ? sheet0.createRow(renglonInicio + cnt) : sheet0.getRow(renglonInicio + cnt));
            for (int i = 0; i < rsMetadata.getColumnCount(); i++) {
                Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
            }
            cnt++;
        }
        File fsalida = new File(file_name);
        FileOutputStream fos = new FileOutputStream(fsalida);
        BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
        workbook.write(bos.toPath());
        /* Cierra Flujos */
        bos.flush();
        bos.close();
        fos.close();
        return file_name;
    }

    public static String generaReporteCOMSOCManager(Connection conn, int mesFin, int anio, Map<String, String> plantillas) throws Exception {
        CallableStatement cs = null, cs2 = null, cs3 = null;
        ResultSet rs = null, rs2 = null, rs3 = null;
        String query = "{call sp_COMSOC( ?,? )}";
        String query2 = "{call sp_COMSOC( ?,? )}";
        String query3 = "{call sp_COMSOC( ?,? )}";
        String fileName = "";
        try {
            cs = conn.prepareCall(query);
            cs.setInt(1, mesFin);
            cs.setString(2, "33605");
            rs = cs.executeQuery();
            cs2 = conn.prepareCall(query2);
            cs2.setInt(1, mesFin);
            cs2.setString(2, "36101");
            rs2 = cs2.executeQuery();
            cs3 = conn.prepareCall(query3);
            cs3.setInt(1, mesFin);
            cs3.setString(2, "36201");
            rs3 = cs3.executeQuery();
            fileName = generaReporteCOMSOC(rs, rs2, rs3, plantillas.get("COMSOC"), mesFin, anio);
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(cs, false);
            CloseObject.closeObject(rs2, false);
            CloseObject.closeObject(cs2, false);
            CloseObject.closeObject(rs3, false);
            CloseObject.closeObject(cs3, false);
        }
    }

    private static String generaReporteCOMSOC(ResultSet rs, ResultSet rs2, ResultSet rs3, String plantillaPath, int mesFin, int anio) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "ReporteCOMSOC" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        Sheet sheet1 = workbook.getSheetAt(1);
        Sheet sheet2 = workbook.getSheetAt(2);
        int cnt1 = 0;
        int cnt2 = 0;
        int cnt3 = 0;
        float total = 0;
        float total2 = 0;
        float total3 = 0;
        ResultSetMetaData rsMetadata = rs.getMetaData();
        int renglonInicio = 15;
        String periodo = "";
        periodo = Util.NOMBRE_MESES_MX[mesFin - 1] + " " + anio;
        Row rwEnc2 = (sheet0.getRow(2) == null ? sheet0.createRow(2) : sheet0.getRow(2));
        Cell cell2 = (rwEnc2.getCell(3) == null ? rwEnc2.createCell(3) : rwEnc2.getCell(3));
        cell2.setCellValue(periodo);
        Row rwEnc3 = (sheet1.getRow(2) == null ? sheet1.createRow(2) : sheet1.getRow(2));
        Cell cell3 = (rwEnc3.getCell(3) == null ? rwEnc3.createCell(3) : rwEnc3.getCell(3));
        cell3.setCellValue(periodo);
        Row rwEnc4 = (sheet2.getRow(2) == null ? sheet2.createRow(2) : sheet2.getRow(2));
        Cell cell4 = (rwEnc4.getCell(3) == null ? rwEnc4.createCell(3) : rwEnc4.getCell(3));
        cell4.setCellValue(periodo);
        CellStyle estiloTabla = Util.generaEstilo2(workbook, 9, false, true, true, true, true, false);
        while (rs.next()) {
            Row rw = (sheet0.getRow(renglonInicio + cnt1) == null ? sheet0.createRow(renglonInicio + cnt1) : sheet0.getRow(renglonInicio + cnt1));
            for (int i = 0; i < rsMetadata.getColumnCount(); i++) {
                Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
            }
            total = total + (rs.getFloat(10));
            cnt1++;
        }
        while (rs2.next()) {
            Row rw = (sheet1.getRow(renglonInicio + cnt2) == null ? sheet1.createRow(renglonInicio + cnt2) : sheet1.getRow(renglonInicio + cnt2));
            for (int i = 0; i < rsMetadata.getColumnCount(); i++) {
                Util.createExcelCellRep(i, rw, rs2, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
            }
            total2 = total2 + (rs2.getFloat(10));
            cnt2++;
        }
        while (rs3.next()) {
            Row rw = (sheet2.getRow(renglonInicio + cnt3) == null ? sheet2.createRow(renglonInicio + cnt3) : sheet2.getRow(renglonInicio + cnt3));
            for (int i = 0; i < rsMetadata.getColumnCount(); i++) {
                Util.createExcelCellRep(i, rw, rs3, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
            }
            total3 = total3 + (rs3.getFloat(10));
            cnt3++;
        }
        Row rwEnc5 = sheet0.getRow(renglonInicio + cnt1) == null ? sheet0.createRow(renglonInicio + cnt1) : sheet0.getRow(renglonInicio + cnt1);
        Cell cell5 = (rwEnc5.getCell(5) == null ? rwEnc5.createCell(5) : rwEnc5.getCell(5));
        cell5.setCellValue("TOTAL: ");
        Row rwEnc6 = sheet0.getRow(renglonInicio + cnt1) == null ? sheet0.createRow(renglonInicio + cnt1) : sheet0.getRow(renglonInicio + cnt1);
        Cell cell6 = (rwEnc6.getCell(9) == null ? rwEnc6.createCell(9) : rwEnc6.getCell(9));
        cell6.setCellValue(total);
        Row rwEnc7 = sheet1.getRow(renglonInicio + cnt2) == null ? sheet1.createRow(renglonInicio + cnt2) : sheet1.getRow(renglonInicio + cnt2);
        Cell cell7 = (rwEnc7.getCell(5) == null ? rwEnc7.createCell(5) : rwEnc7.getCell(5));
        cell7.setCellValue("TOTAL: ");
        Row rwEnc8 = sheet1.getRow(renglonInicio + cnt2) == null ? sheet1.createRow(renglonInicio + cnt2) : sheet1.getRow(renglonInicio + cnt2);
        Cell cell8 = (rwEnc8.getCell(9) == null ? rwEnc8.createCell(9) : rwEnc8.getCell(9));
        cell8.setCellValue(total2);
        Row rwEnc9 = sheet2.getRow(renglonInicio + cnt3) == null ? sheet2.createRow(renglonInicio + cnt3) : sheet2.getRow(renglonInicio + cnt3);
        Cell cell9 = (rwEnc9.getCell(5) == null ? rwEnc9.createCell(5) : rwEnc9.getCell(5));
        cell9.setCellValue("TOTAL: ");
        Row rwEnc10 = sheet2.getRow(renglonInicio + cnt3) == null ? sheet2.createRow(renglonInicio + cnt3) : sheet2.getRow(renglonInicio + cnt3);
        Cell cell10 = (rwEnc10.getCell(9) == null ? rwEnc10.createCell(9) : rwEnc10.getCell(9));
        cell10.setCellValue(total3);
        Row rwEnc11 = sheet0.getRow(renglonInicio + cnt1 + 5) == null ? sheet0.createRow(renglonInicio + cnt1 + 5) : sheet0.getRow(renglonInicio + cnt1 + 5);
        Cell cell11 = (rwEnc11.getCell(4) == null ? rwEnc11.createCell(4) : rwEnc11.getCell(4));
        cell11.setCellValue("Responsable de la unidad de Comunicación Social ");
        Row rwEnc12 = sheet0.getRow(renglonInicio + cnt1 + 6) == null ? sheet0.createRow(renglonInicio + cnt1 + 6) : sheet0.getRow(renglonInicio + cnt1 + 6);
        Cell cell12 = (rwEnc12.getCell(4) == null ? rwEnc12.createCell(4) : rwEnc12.getCell(4));
        cell12.setCellValue("de Autorizar el Gasto de la partida 33605.");
        Row rwEnc13 = sheet0.getRow(renglonInicio + cnt1 + 10) == null ? sheet0.createRow(renglonInicio + cnt1 + 10) : sheet0.getRow(renglonInicio + cnt1 + 10);
        Cell cell13 = (rwEnc13.getCell(4) == null ? rwEnc13.createCell(4) : rwEnc13.getCell(4));
        cell13.setCellValue("______________________________________________");
        Row rwEnc14 = sheet1.getRow(renglonInicio + cnt2 + 5) == null ? sheet1.createRow(renglonInicio + cnt2 + 5) : sheet1.getRow(renglonInicio + cnt2 + 5);
        Cell cell14 = (rwEnc14.getCell(4) == null ? rwEnc14.createCell(4) : rwEnc14.getCell(4));
        cell14.setCellValue("Responsable de la unidad de Comunicación Social ");
        Row rwEnc15 = sheet1.getRow(renglonInicio + cnt2 + 6) == null ? sheet1.createRow(renglonInicio + cnt2 + 6) : sheet1.getRow(renglonInicio + cnt2 + 6);
        Cell cell15 = (rwEnc15.getCell(4) == null ? rwEnc15.createCell(4) : rwEnc15.getCell(4));
        cell15.setCellValue("de Autorizar el Gasto de la partida 36101.");
        Row rwEnc16 = sheet1.getRow(renglonInicio + cnt2 + 10) == null ? sheet1.createRow(renglonInicio + cnt2 + 10) : sheet1.getRow(renglonInicio + cnt2 + 10);
        Cell cell16 = (rwEnc16.getCell(4) == null ? rwEnc16.createCell(4) : rwEnc16.getCell(4));
        cell16.setCellValue("______________________________________________");
        Row rwEnc17 = sheet2.getRow(renglonInicio + cnt3 + 5) == null ? sheet2.createRow(renglonInicio + cnt3 + 5) : sheet2.getRow(renglonInicio + cnt3 + 5);
        Cell cell17 = (rwEnc17.getCell(4) == null ? rwEnc17.createCell(4) : rwEnc17.getCell(4));
        cell17.setCellValue("Responsable de la unidad de Comunicación Social ");
        Row rwEnc18 = sheet2.getRow(renglonInicio + cnt3 + 6) == null ? sheet2.createRow(renglonInicio + cnt3 + 6) : sheet2.getRow(renglonInicio + cnt3 + 6);
        Cell cell18 = (rwEnc18.getCell(4) == null ? rwEnc18.createCell(4) : rwEnc18.getCell(4));
        cell18.setCellValue("de Autorizar el Gasto de la partida 36201.");
        Row rwEnc19 = sheet2.getRow(renglonInicio + cnt3 + 10) == null ? sheet2.createRow(renglonInicio + cnt3 + 10) : sheet2.getRow(renglonInicio + cnt3 + 10);
        Cell cell19 = (rwEnc19.getCell(4) == null ? rwEnc19.createCell(4) : rwEnc19.getCell(4));
        cell19.setCellValue("______________________________________________");
        File fsalida = new File(file_name);
        FileOutputStream fos = new FileOutputStream(fsalida);
        BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
        workbook.write(bos.toPath());
        workbook.close();
        /* Cierra Flujos */
        bos.flush();
        bos.close();
        fos.close();
        return file_name;
    }

    public static String generaReporteConciliacionManager(Connection conn, int mesFin, Map<String, String> plantillas) throws Exception {
        CallableStatement cs = null, cs1 = null, cs2 = null;
        ResultSet rs = null, rs1 = null, rs2 = null;
        String query = "{call sp_conciliaMOD( ? )}";
        String queryIP = "{call sp_conciliaMOD_IP( ? )}";
        String queryAC = "{call sp_ConciliaMod_Acumulada( ? )}";
        String fileName = "";
        try {
            cs = conn.prepareCall(query);
            cs.setInt(1, mesFin);
            rs = cs.executeQuery();
            cs1 = conn.prepareCall(queryIP);
            cs1.setInt(1, mesFin);
            rs1 = cs1.executeQuery();
            cs2 = conn.prepareCall(queryAC);
            cs2.setInt(1, mesFin);
            rs2 = cs2.executeQuery();
            fileName = generaReporteCONCILIAMOD(rs, plantillas.get("CONCILIAMOD"), mesFin, rs1, rs2);
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(cs, false);
            CloseObject.closeObject(rs1, false);
            CloseObject.closeObject(cs1, false);
            CloseObject.closeObject(rs2, false);
            CloseObject.closeObject(cs2, false);
        }
    }

    private static String generaReporteCONCILIAMOD(ResultSet rs, String plantillaPath, int mesFin, ResultSet rs1, ResultSet rs2) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "CONCILIACION_MODIFICADO" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        int cnt1 = 0;
        ResultSetMetaData rsMetadata = rs.getMetaData();
        int renglonInicio = 4;
        CellStyle estiloTabla = Util.generaEstilo2(workbook, 9, false, true, true, true, true, false);
        while (rs.next()) {
            Row rw = (sheet0.getRow(renglonInicio + cnt1) == null ? sheet0.createRow(renglonInicio + cnt1) : sheet0.getRow(renglonInicio + cnt1));
            for (int i = 0; i < rsMetadata.getColumnCount(); i++) {
                Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
            }
            cnt1++;
        }
        Sheet sheet1 = workbook.getSheetAt(1);
        cnt1 = 0;
        ResultSetMetaData rsMetadata1 = rs1.getMetaData();
        renglonInicio = 4;
        while (rs1.next()) {
            Row rw1 = (sheet1.getRow(renglonInicio + cnt1) == null ? sheet1.createRow(renglonInicio + cnt1) : sheet1.getRow(renglonInicio + cnt1));
            for (int i = 0; i < rsMetadata1.getColumnCount(); i++) {
                Util.createExcelCellRep(i, rw1, rs1, rsMetadata1.getColumnName(i + 1), rsMetadata1.getColumnType(i + 1), estiloTabla);
            }
            cnt1++;
        }
        Sheet sheet2 = workbook.getSheetAt(2);
        cnt1 = 0;
        ResultSetMetaData rsMetadata2 = rs2.getMetaData();
        renglonInicio = 4;
        while (rs2.next()) {
            Row rw2 = (sheet2.getRow(renglonInicio + cnt1) == null ? sheet2.createRow(renglonInicio + cnt1) : sheet2.getRow(renglonInicio + cnt1));
            for (int i = 0; i < rsMetadata2.getColumnCount(); i++) {
                Util.createExcelCellRep(i, rw2, rs2, rsMetadata2.getColumnName(i + 1), rsMetadata2.getColumnType(i + 1), estiloTabla);
            }
            cnt1++;
        }
        File fsalida = new File(file_name);
        FileOutputStream fos = new FileOutputStream(fsalida);
        BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
        workbook.write(bos.toPath());
        workbook.close();
        /* Cierra Flujos */
        bos.flush();
        bos.close();
        fos.close();
        return file_name;
    }

    public static String generaReporteConciliaRadPagManager(Connection conn, int mesFin, Map<String, String> plantillas) throws Exception {
        CallableStatement cs = null;
        CallableStatement cs1 = null;
        ResultSet rs = null;
        ResultSet rs1 = null;
        String query = "{call sp_conciliaRadPagado( ? )}";
        String query1 = "{call sp_ConciliaRadPagadoDet( ? )}";
        String fileName = "";
        try {
            cs = conn.prepareCall(query);
            cs.setInt(1, mesFin);
            cs1 = conn.prepareCall(query1);
            cs1.setInt(1, mesFin);
            rs = cs.executeQuery();
            rs1 = cs1.executeQuery();
            fileName = generaReporteRadPagado(rs, plantillas.get("CONCILIARADPAGADO"), mesFin, rs1);
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(cs, false);
            CloseObject.closeObject(rs1, false);
            CloseObject.closeObject(cs1, false);
        }
    }

    private static String generaReporteRadPagado(ResultSet rs, String plantillaPath, int mesFin, ResultSet rs1) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "CONCILIACION_PAGADO" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        int cnt1 = 0;
        ResultSetMetaData rsMetadata = rs.getMetaData();
        int renglonInicio = 4;
        CellStyle estiloTabla = Util.generaEstilo2(workbook, 9, false, true, true, true, true, false);
        while (rs.next()) {
            Row rw = (sheet0.getRow(renglonInicio + cnt1) == null ? sheet0.createRow(renglonInicio + cnt1) : sheet0.getRow(renglonInicio + cnt1));
            for (int i = 0; i < rsMetadata.getColumnCount(); i++) {
                Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
            }
            cnt1++;
        }
        Sheet sheet1 = workbook.getSheetAt(1);
        cnt1 = 0;
        ResultSetMetaData rsMetadata1 = rs1.getMetaData();
        renglonInicio = 3;
        while (rs1.next()) {
            Row rw1 = (sheet1.getRow(renglonInicio + cnt1) == null ? sheet1.createRow(renglonInicio + cnt1) : sheet1.getRow(renglonInicio + cnt1));
            for (int i = 0; i < rsMetadata1.getColumnCount(); i++) {
                Util.createExcelCellRep(i, rw1, rs1, rsMetadata1.getColumnName(i + 1), rsMetadata1.getColumnType(i + 1), estiloTabla);
            }
            cnt1++;
        }
        File fsalida = new File(file_name);
        FileOutputStream fos = new FileOutputStream(fsalida);
        BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
        workbook.write(bos.toPath());
        workbook.close();
        /* Cierra Flujos */
        bos.flush();
        bos.close();
        fos.close();
        return file_name;
    }

    public static String generaReporteConciliaRadIngManager(Connection conn, int mesFin, Map<String, String> plantillas) throws Exception {
        CallableStatement cs = null;
        ResultSet rs = null;
        String query = "{call sp_conciliaRadIngreso( ? )}";
        String fileName = "";
        try {
            cs = conn.prepareCall(query);
            cs.setInt(1, mesFin);
            rs = cs.executeQuery();
            fileName = generaReporteRadIngreso(rs, plantillas.get("CONCILIARADINGRESO"), mesFin);
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(cs, false);
        }
    }

    private static String generaReporteRadIngreso(ResultSet rs, String plantillaPath, int mesFin) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "CONCILIACION_INGRESO" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        int cnt1 = 0;
        ResultSetMetaData rsMetadata = rs.getMetaData();
        int renglonInicio = 4;
        CellStyle estiloTabla = Util.generaEstilo2(workbook, 9, false, true, true, true, true, false);
        while (rs.next()) {
            Row rw = (sheet0.getRow(renglonInicio + cnt1) == null ? sheet0.createRow(renglonInicio + cnt1) : sheet0.getRow(renglonInicio + cnt1));
            for (int i = 0; i < rsMetadata.getColumnCount(); i++) {
                Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
            }
            cnt1++;
        }
        File fsalida = new File(file_name);
        FileOutputStream fos = new FileOutputStream(fsalida);
        BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
        workbook.write(bos.toPath());
        workbook.close();
        /* Cierra Flujos */
        bos.flush();
        bos.close();
        fos.close();
        return file_name;
    }

    public static String conciliaEjercidoManager(Connection conn, int mesFin, Map<String, String> plantillas) throws Exception {
        CallableStatement cs = null;
        ResultSet rs = null;
        CallableStatement cs2 = null;
        ResultSet rs2 = null;
        String query = "{call sp_ConciliaEjercidoEP( ? )}";
        String query2 = "{call sp_l_RepEjercido( ? )}";
        String fileName = "";
        try {
            cs = conn.prepareCall(query);
            cs.setInt(1, mesFin);
            rs = cs.executeQuery();
            cs2 = conn.prepareCall(query2);
            cs2.setInt(1, mesFin);
            rs2 = cs2.executeQuery();
            fileName = generaConciliaEjercido(rs, rs2, plantillas.get("CONCILIAEJERCIDO"), mesFin);
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(cs, false);
            CloseObject.closeObject(rs2, false);
            CloseObject.closeObject(cs2, false);
        }
    }

    private static String generaConciliaEjercido(ResultSet rs, ResultSet rs2, String plantillaPath, int mesFin) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "CONCILIACION_EJERCIDO" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        Sheet sheet1 = workbook.getSheetAt(1);
        int cnt1 = 0;
        int cnt2 = 0;
        ResultSetMetaData rsMetadata = rs.getMetaData();
        ResultSetMetaData rsMetadata2 = rs2.getMetaData();
        int renglonInicio = 5;
        CellStyle estiloTabla = Util.generaEstilo2(workbook, 9, false, true, true, true, true, false);
        while (rs.next()) {
            Row rw = (sheet0.getRow(renglonInicio + cnt1) == null ? sheet0.createRow(renglonInicio + cnt1) : sheet0.getRow(renglonInicio + cnt1));
            for (int i = 0; i < rsMetadata.getColumnCount(); i++) {
                Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
            }
            cnt1++;
        }
        while (rs2.next()) {
            Row rw2 = (sheet1.getRow(renglonInicio + cnt2) == null ? sheet1.createRow(renglonInicio + cnt2) : sheet1.getRow(renglonInicio + cnt2));
            for (int i = 0; i < rsMetadata2.getColumnCount(); i++) {
                Util.createExcelCellRep(i, rw2, rs2, rsMetadata2.getColumnName(i + 1), rsMetadata2.getColumnType(i + 1), estiloTabla);
            }
            cnt2++;
        }
        File fsalida = new File(file_name);
        FileOutputStream fos = new FileOutputStream(fsalida);
        BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
        workbook.write(bos.toPath());
        workbook.close();
        /* Cierra Flujos */
        bos.flush();
        bos.close();
        fos.close();
        return file_name;
    }

    public static String conciliaDevengadoManager(Connection conn, int mesFin, Map<String, String> plantillas) throws Exception {
        CallableStatement cs = null;
        ResultSet rs = null;
        CallableStatement cs2 = null;
        ResultSet rs2 = null;
        String query = "{call sp_ConciliaDevengadoEP( ? )}";
        String query2 = "{call sp_ConciliaDevengadoCXP( ? )}";
        String fileName = "";
        try {
            cs = conn.prepareCall(query);
            cs.setInt(1, mesFin);
            rs = cs.executeQuery();
            cs2 = conn.prepareCall(query2);
            cs2.setInt(1, mesFin);
            rs2 = cs2.executeQuery();
            fileName = generaConciliaDevengado(rs, rs2, plantillas.get("CONCILIADEVENGADO"), mesFin);
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(cs, false);
            CloseObject.closeObject(rs2, false);
            CloseObject.closeObject(cs2, false);
        }
    }

    private static String generaConciliaDevengado(ResultSet rs, ResultSet rs2, String plantillaPath, int mesFin) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "CONCILIACION_DEVENGADO" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        Sheet sheet1 = workbook.getSheetAt(1);
        int cnt1 = 0;
        int cnt2 = 0;
        ResultSetMetaData rsMetadata = rs.getMetaData();
        ResultSetMetaData rsMetadata2 = rs2.getMetaData();
        int renglonInicio = 5;
        CellStyle estiloTabla = Util.generaEstilo2(workbook, 9, false, true, true, true, true, false);
        while (rs.next()) {
            Row rw = (sheet0.getRow(renglonInicio + cnt1) == null ? sheet0.createRow(renglonInicio + cnt1) : sheet0.getRow(renglonInicio + cnt1));
            for (int i = 0; i < rsMetadata.getColumnCount(); i++) {
                Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
            }
            cnt1++;
        }
        while (rs2.next()) {
            Row rw2 = (sheet1.getRow(renglonInicio + cnt2) == null ? sheet1.createRow(renglonInicio + cnt2) : sheet1.getRow(renglonInicio + cnt2));
            for (int i = 0; i < rsMetadata2.getColumnCount(); i++) {
                Util.createExcelCellRep(i, rw2, rs2, rsMetadata2.getColumnName(i + 1), rsMetadata2.getColumnType(i + 1), estiloTabla);
            }
            cnt2++;
        }
        File fsalida = new File(file_name);
        FileOutputStream fos = new FileOutputStream(fsalida);
        BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
        workbook.write(bos.toPath());
        workbook.close();
        /* Cierra Flujos */
        bos.flush();
        bos.close();
        fos.close();
        return file_name;
    }

    public static String conciliaCompromisoManager(Connection conn, int mesFin, Map<String, String> plantillas) throws Exception {
        CallableStatement cs = null;
        ResultSet rs = null;
        CallableStatement cs2 = null;
        ResultSet rs2 = null;
        String query = "{call sp_ConciliaComprometidoEP( ? )}";
        String query2 = "{call sp_ConciliaComprometidoCXP( ? )}";
        String fileName = "";
        try {
            cs = conn.prepareCall(query);
            cs.setInt(1, mesFin);
            rs = cs.executeQuery();
            cs2 = conn.prepareCall(query2);
            cs2.setInt(1, mesFin);
            rs2 = cs2.executeQuery();
            fileName = generaConciliaCompromiso(rs, rs2, plantillas.get("CONCILIACOMPROMISO"), mesFin);
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(cs, false);
            CloseObject.closeObject(rs2, false);
            CloseObject.closeObject(cs2, false);
        }
    }

    private static String generaConciliaCompromiso(ResultSet rs, ResultSet rs2, String plantillaPath, int mesFin) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "CONCILIACION_COMPROMETIDO" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        Sheet sheet1 = workbook.getSheetAt(1);
        int cnt1 = 0;
        int cnt2 = 0;
        ResultSetMetaData rsMetadata = rs.getMetaData();
        ResultSetMetaData rsMetadata2 = rs2.getMetaData();
        int renglonInicio = 5;
        CellStyle estiloTabla = Util.generaEstilo2(workbook, 9, false, true, true, true, true, false);
        while (rs.next()) {
            Row rw = (sheet0.getRow(renglonInicio + cnt1) == null ? sheet0.createRow(renglonInicio + cnt1) : sheet0.getRow(renglonInicio + cnt1));
            for (int i = 0; i < rsMetadata.getColumnCount(); i++) {
                Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
            }
            cnt1++;
        }
        while (rs2.next()) {
            Row rw2 = (sheet1.getRow(renglonInicio + cnt2) == null ? sheet1.createRow(renglonInicio + cnt2) : sheet1.getRow(renglonInicio + cnt2));
            for (int i = 0; i < rsMetadata2.getColumnCount(); i++) {
                Util.createExcelCellRep(i, rw2, rs2, rsMetadata2.getColumnName(i + 1), rsMetadata2.getColumnType(i + 1), estiloTabla);
            }
            cnt2++;
        }
        File fsalida = new File(file_name);
        FileOutputStream fos = new FileOutputStream(fsalida);
        BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
        workbook.write(bos.toPath());
        workbook.close();
        /* Cierra Flujos */
        bos.flush();
        bos.close();
        fos.close();
        return file_name;
    }

    public static String generaReporteConciliaIngGtoManager(Connection conn, int mesFin, Map<String, String> plantillas) throws Exception {
        CallableStatement cs = null;
        ResultSet rs = null;
        CallableStatement cs2 = null;
        ResultSet rs2 = null;
        String query = "{call sp_ConciliaIngresoGasto( ? )}";
        String query2 = "{call sp_ConciliaIngresoGastoDet( ? )}";
        String fileName = "";
        try {
            cs = conn.prepareCall(query);
            cs.setInt(1, mesFin);
            rs = cs.executeQuery();
            cs2 = conn.prepareCall(query2);
            cs2.setInt(1, mesFin);
            rs2 = cs2.executeQuery();
            fileName = generaReporteIngresoGto(rs, rs2, plantillas.get("CONCILIAINGRESOGASTO"), mesFin);
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(cs, false);
            CloseObject.closeObject(rs2, false);
            CloseObject.closeObject(cs2, false);
        }
    }

    private static String generaReporteIngresoGto(ResultSet rs, ResultSet rs2, String plantillaPath, int mesFin) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "CONCILIACION_INGRESO_GASTO" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        Sheet sheet1 = workbook.getSheetAt(1);
        int cnt1 = 0;
        int cnt2 = 0;
        ResultSetMetaData rsMetadata = rs.getMetaData();
        ResultSetMetaData rsMetadata2 = rs2.getMetaData();
        int renglonInicio = 4;
        CellStyle estiloTabla = Util.generaEstilo2(workbook, 9, false, true, true, true, true, false);
        while (rs.next()) {
            Row rw = (sheet0.getRow(renglonInicio + cnt1) == null ? sheet0.createRow(renglonInicio + cnt1) : sheet0.getRow(renglonInicio + cnt1));
            for (int i = 0; i < rsMetadata.getColumnCount(); i++) {
                Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
            }
            cnt1++;
        }
        while (rs2.next()) {
            Row rw2 = (sheet1.getRow(renglonInicio + cnt2) == null ? sheet1.createRow(renglonInicio + cnt2) : sheet1.getRow(renglonInicio + cnt2));
            for (int i = 0; i < rsMetadata2.getColumnCount(); i++) {
                Util.createExcelCellRep(i, rw2, rs2, rsMetadata2.getColumnName(i + 1), rsMetadata2.getColumnType(i + 1), estiloTabla);
            }
            cnt2++;
        }
        File fsalida = new File(file_name);
        FileOutputStream fos = new FileOutputStream(fsalida);
        BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
        workbook.write(bos.toPath());
        workbook.close();
        /* Cierra Flujos */
        bos.flush();
        bos.close();
        fos.close();
        return file_name;
    }

    public static String conciliaMomentosManager(Connection conn, int mesFin, Map<String, String> plantillas) throws Exception {
        CallableStatement cs = null;
        ResultSet rs = null;
        String query = "{call sp_ConciliaMomentosPpto( ? )}";
        String fileName = "";
        try {
            cs = conn.prepareCall(query);
            cs.setInt(1, mesFin);
            rs = cs.executeQuery();
            fileName = generaReporteConciliaMomentos(rs, plantillas.get("CONCILIAMOMENTOS"), mesFin);
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(cs, false);
        }
    }

    private static String generaReporteConciliaMomentos(ResultSet rs, String plantillaPath, int mesFin) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "CONCILIACION_MOMENTOS" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        int cnt1 = 0;
        ResultSetMetaData rsMetadata = rs.getMetaData();
        int renglonInicio = 5;
        CellStyle estiloTabla = Util.generaEstilo2(workbook, 9, false, true, true, true, true, false);
        while (rs.next()) {
            Row rw = (sheet0.getRow(renglonInicio + cnt1) == null ? sheet0.createRow(renglonInicio + cnt1) : sheet0.getRow(renglonInicio + cnt1));
            for (int i = 0; i < rsMetadata.getColumnCount(); i++) {
                Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
            }
            cnt1++;
        }
        File fsalida = new File(file_name);
        FileOutputStream fos = new FileOutputStream(fsalida);
        BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
        workbook.write(bos.toPath());
        workbook.close();
        /* Cierra Flujos */
        bos.flush();
        bos.close();
        fos.close();
        return file_name;
    }

    public static String conciliaIngresosManager(Connection conn, int mesFin, String fechaFin, Map<String, String> plantillas) throws Exception {
        CallableStatement cs = null;
        ResultSet rs = null;
        String query = "{call sp_ConciliaIngresoContaPptal( ? )}";
        String fileName = "";
        try {
            cs = conn.prepareCall(query);
            cs.setInt(1, mesFin);
            rs = cs.executeQuery();
            fileName = generaReporteExcelIngresos(conn, rs, plantillas.get("ConciliaIngreso"), mesFin, fechaFin);
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(cs, false);
        }
    }

    private static String generaReporteExcelIngresos(Connection conn, ResultSet rs, String plantillaPath, int mesFin, String fechaFin) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "IngresoPresupConta" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        int mes = 0;
        String anio = "";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        ResultSetMetaData rsMetadata = rs.getMetaData();
        Sheet sheet0 = workbook.getSheetAt(0);
        mes = Integer.parseInt(fechaFin.substring(3, 5));
        anio = fechaFin.substring(6, 10);
        String tipo = "";
        String periodo = "MES DE: " + Util.NOMBRE_MESES_MX[mes - 1] + " " + anio;
        Row rwEnc2 = (sheet0.getRow(3) == null ? sheet0.createRow(3) : sheet0.getRow(3));
        Cell cell2 = (rwEnc2.getCell(0) == null ? rwEnc2.createCell(0) : rwEnc2.getCell(0));
        cell2.setCellValue(periodo);
        double amort = 0, cAlmacen = 0, cFFM = 0;
        double sOtrosIngresos = 0, devNoPag = 0, ajenasNoPag = 0;
        double devCanc = 0, saldoICNP = 0, ajenasPagAnt = 0;
        double bienesMuebles = 0, op = 0, salidas = 0;
        double reintegros = 0, obrasAA = 0;
        double ffm = 0;
        double alm = 0;
        double antic = 0;
        double amAnt = 0;
        double saldo3113 = 0;
        double saldo31132 = 0;
        double saldoIPNC = 0;
        double totalIngresosContables = 0;
        double bienesInmuebles = 0;
        double saldo8151 = 0;
        double saldoIngreso = 0;
        int corte1 = 10;
        int corte2 = 13;
        int corte3 = 16;
        int corte4 = 19;
        int corte5 = 23;
        int corte6 = 26;
        int corte7 = 32;
        int corte8 = 36;
        int corte9 = 39;
        int corte10 = 42;
        int corte11 = 47;
        int corte12 = 50;
        int corte13 = 53;
        int corte14 = 56;
        int corte15 = 59;
        int corte16 = 62;
        int corte17 = 65;
        int corte18 = 68;
        CellStyle estiloTabla = Util.generaEstilo2(workbook, 9, false, true, true, true, true, false);
        CellStyle estiloMoneda = Util.generaEstilo2(workbook, 9, false, true, true, true, true, true);
        CellStyle estiloFecha = Util.generaEstilo2(workbook, 9, false, true, true, true, true, false);
        DataFormat df = workbook.createDataFormat();
        estiloFecha.setDataFormat(df.getFormat("dd/mm/yyyy"));
        DecimalFormat formateador = new DecimalFormat("#,###,###.00");
        while (rs.next()) {
            tipo = rs.getString(1);
            if ("Amortización".equals(tipo)) {
                Row rw = (sheet0.getRow(corte1) == null ? sheet0.createRow(corte1) : sheet0.getRow(corte1));
                Util.createExcelCellRep(0, rw, rs, rsMetadata.getColumnName(2), rsMetadata.getColumnType(2), estiloFecha);
                Util.createExcelCellRep(1, rw, rs, rsMetadata.getColumnName(3), rsMetadata.getColumnType(3), estiloTabla);
                Util.createExcelCellRep(2, rw, rs, rsMetadata.getColumnName(4), rsMetadata.getColumnType(4), estiloTabla);
                Util.createExcelCellRep(3, rw, rs, rsMetadata.getColumnName(5), rsMetadata.getColumnType(5), estiloTabla);
                Util.createExcelCellRep(4, rw, rs, rsMetadata.getColumnName(6), rsMetadata.getColumnType(6), estiloTabla);
                Util.createExcelCellRep(5, rw, rs, rsMetadata.getColumnName(7), rsMetadata.getColumnType(7), estiloTabla);
                Util.createExcelCellRep(6, rw, rs, rsMetadata.getColumnName(8), rsMetadata.getColumnType(8), estiloMoneda);
                sheet0.shiftRows(corte1 + 1, corte18 + 1, 1, true, true);
                amort += rs.getDouble("Importe");
                corte1++;
                corte2++;
                corte3++;
                corte4++;
                corte5++;
                corte6++;
                corte7++;
                corte8++;
                corte9++;
                corte10++;
                corte11++;
                corte12++;
                corte13++;
                corte14++;
                corte15++;
                corte16++;
                corte17++;
                corte18++;
            } else if ("IngresosNoPAlmacen".equals(tipo)) {
                Row rw = (sheet0.getRow(corte2) == null ? sheet0.createRow(corte2) : sheet0.getRow(corte2));
                Util.createExcelCellRep(0, rw, rs, rsMetadata.getColumnName(2), rsMetadata.getColumnType(2), estiloFecha);
                Util.createExcelCellRep(1, rw, rs, rsMetadata.getColumnName(3), rsMetadata.getColumnType(3), estiloTabla);
                Util.createExcelCellRep(2, rw, rs, rsMetadata.getColumnName(4), rsMetadata.getColumnType(4), estiloTabla);
                Util.createExcelCellRep(3, rw, rs, rsMetadata.getColumnName(5), rsMetadata.getColumnType(5), estiloTabla);
                Util.createExcelCellRep(4, rw, rs, rsMetadata.getColumnName(6), rsMetadata.getColumnType(6), estiloTabla);
                Util.createExcelCellRep(5, rw, rs, rsMetadata.getColumnName(7), rsMetadata.getColumnType(7), estiloTabla);
                Util.createExcelCellRep(6, rw, rs, rsMetadata.getColumnName(8), rsMetadata.getColumnType(8), estiloMoneda);
                sheet0.shiftRows(corte2 + 1, corte18 + 1, 1, true, true);
                cAlmacen += rs.getDouble("importe");
                corte2++;
                corte3++;
                corte4++;
                corte5++;
                corte6++;
                corte7++;
                corte8++;
                corte9++;
                corte10++;
                corte11++;
                corte12++;
                corte13++;
                corte14++;
                corte15++;
                corte16++;
                corte17++;
                corte18++;
            } else if ("ObrasAA".equals(tipo)) {
                Row rw = (sheet0.getRow(corte3) == null ? sheet0.createRow(corte3) : sheet0.getRow(corte3));
                Util.createExcelCellRep(0, rw, rs, rsMetadata.getColumnName(2), rsMetadata.getColumnType(2), estiloFecha);
                Util.createExcelCellRep(1, rw, rs, rsMetadata.getColumnName(3), rsMetadata.getColumnType(3), estiloTabla);
                Util.createExcelCellRep(2, rw, rs, rsMetadata.getColumnName(4), rsMetadata.getColumnType(4), estiloTabla);
                Util.createExcelCellRep(3, rw, rs, rsMetadata.getColumnName(5), rsMetadata.getColumnType(5), estiloTabla);
                Util.createExcelCellRep(4, rw, rs, rsMetadata.getColumnName(6), rsMetadata.getColumnType(6), estiloTabla);
                Util.createExcelCellRep(5, rw, rs, rsMetadata.getColumnName(7), rsMetadata.getColumnType(7), estiloTabla);
                Util.createExcelCellRep(6, rw, rs, rsMetadata.getColumnName(8), rsMetadata.getColumnType(8), estiloMoneda);
                sheet0.shiftRows(corte3 + 1, corte18 + 1, 1, true, true);
                obrasAA += rs.getDouble("importe");
                corte3++;
                corte4++;
                corte5++;
                corte6++;
                corte7++;
                corte8++;
                corte9++;
                corte10++;
                corte11++;
                corte12++;
                corte13++;
                corte14++;
                corte15++;
                corte16++;
                corte17++;
                corte18++;
            } else if ("IngresosNoPFFM".equals(tipo)) {
                Row rw = (sheet0.getRow(corte4) == null ? sheet0.createRow(corte4) : sheet0.getRow(corte4));
                Util.createExcelCellRep(0, rw, rs, rsMetadata.getColumnName(2), rsMetadata.getColumnType(2), estiloFecha);
                Util.createExcelCellRep(1, rw, rs, rsMetadata.getColumnName(3), rsMetadata.getColumnType(3), estiloTabla);
                Util.createExcelCellRep(2, rw, rs, rsMetadata.getColumnName(4), rsMetadata.getColumnType(4), estiloTabla);
                Util.createExcelCellRep(3, rw, rs, rsMetadata.getColumnName(5), rsMetadata.getColumnType(5), estiloTabla);
                Util.createExcelCellRep(4, rw, rs, rsMetadata.getColumnName(6), rsMetadata.getColumnType(6), estiloTabla);
                Util.createExcelCellRep(5, rw, rs, rsMetadata.getColumnName(7), rsMetadata.getColumnType(7), estiloTabla);
                Util.createExcelCellRep(6, rw, rs, rsMetadata.getColumnName(8), rsMetadata.getColumnType(8), estiloMoneda);
                sheet0.shiftRows(corte4 + 1, corte18 + 1, 1, true, true);
                cFFM += rs.getDouble("importe");
                corte4++;
                corte5++;
                corte6++;
                corte7++;
                corte8++;
                corte9++;
                corte10++;
                corte11++;
                corte12++;
                corte13++;
                corte14++;
                corte15++;
                corte16++;
                corte17++;
                corte18++;
            } else if ("DevengadosNoPagados".equals(tipo)) {
                Row rw = (sheet0.getRow(corte5) == null ? sheet0.createRow(corte5) : sheet0.getRow(corte5));
                Util.createExcelCellRep(0, rw, rs, rsMetadata.getColumnName(2), rsMetadata.getColumnType(2), estiloFecha);
                Util.createExcelCellRep(1, rw, rs, rsMetadata.getColumnName(3), rsMetadata.getColumnType(3), estiloTabla);
                Util.createExcelCellRep(2, rw, rs, rsMetadata.getColumnName(4), rsMetadata.getColumnType(4), estiloTabla);
                Util.createExcelCellRep(3, rw, rs, rsMetadata.getColumnName(5), rsMetadata.getColumnType(5), estiloTabla);
                Util.createExcelCellRep(4, rw, rs, rsMetadata.getColumnName(6), rsMetadata.getColumnType(6), estiloTabla);
                Util.createExcelCellRep(5, rw, rs, rsMetadata.getColumnName(7), rsMetadata.getColumnType(7), estiloTabla);
                Util.createExcelCellRep(6, rw, rs, rsMetadata.getColumnName(8), rsMetadata.getColumnType(8), estiloMoneda);
                sheet0.shiftRows(corte5 + 1, corte18 + 1, 1, true, true);
                devNoPag += rs.getDouble("importe");
                corte5++;
                corte6++;
                corte7++;
                corte8++;
                corte9++;
                corte10++;
                corte11++;
                corte12++;
                corte13++;
                corte14++;
                corte15++;
                corte16++;
                corte17++;
                corte18++;
            } else if ("DevengadosCanc".equals(tipo)) {
                Row rw = (sheet0.getRow(corte6) == null ? sheet0.createRow(corte6) : sheet0.getRow(corte6));
                Util.createExcelCellRep(0, rw, rs, rsMetadata.getColumnName(2), rsMetadata.getColumnType(2), estiloFecha);
                Util.createExcelCellRep(1, rw, rs, rsMetadata.getColumnName(3), rsMetadata.getColumnType(3), estiloTabla);
                Util.createExcelCellRep(2, rw, rs, rsMetadata.getColumnName(4), rsMetadata.getColumnType(4), estiloTabla);
                Util.createExcelCellRep(3, rw, rs, rsMetadata.getColumnName(5), rsMetadata.getColumnType(5), estiloTabla);
                Util.createExcelCellRep(4, rw, rs, rsMetadata.getColumnName(6), rsMetadata.getColumnType(6), estiloTabla);
                Util.createExcelCellRep(5, rw, rs, rsMetadata.getColumnName(7), rsMetadata.getColumnType(7), estiloTabla);
                Util.createExcelCellRep(6, rw, rs, rsMetadata.getColumnName(8), rsMetadata.getColumnType(8), estiloMoneda);
                sheet0.shiftRows(corte6 + 1, corte18 + 1, 1, true, true);
                devCanc += rs.getDouble("importe");
                corte6++;
                corte7++;
                corte8++;
                corte9++;
                corte10++;
                corte11++;
                corte12++;
                corte13++;
                corte14++;
                corte15++;
                corte16++;
                corte17++;
                corte18++;
            } else if ("AjenasNoPag".equals(tipo)) {
                Row rw = (sheet0.getRow(corte7) == null ? sheet0.createRow(corte7) : sheet0.getRow(corte7));
                Util.createExcelCellRep(0, rw, rs, rsMetadata.getColumnName(2), rsMetadata.getColumnType(2), estiloFecha);
                Util.createExcelCellRep(1, rw, rs, rsMetadata.getColumnName(3), rsMetadata.getColumnType(3), estiloTabla);
                Util.createExcelCellRep(2, rw, rs, rsMetadata.getColumnName(4), rsMetadata.getColumnType(4), estiloTabla);
                Util.createExcelCellRep(3, rw, rs, rsMetadata.getColumnName(5), rsMetadata.getColumnType(5), estiloTabla);
                Util.createExcelCellRep(4, rw, rs, rsMetadata.getColumnName(6), rsMetadata.getColumnType(6), estiloTabla);
                Util.createExcelCellRep(5, rw, rs, rsMetadata.getColumnName(7), rsMetadata.getColumnType(7), estiloTabla);
                Util.createExcelCellRep(6, rw, rs, rsMetadata.getColumnName(8), rsMetadata.getColumnType(8), estiloMoneda);
                sheet0.shiftRows(corte7 + 1, corte18 + 1, 1, true, true);
                ajenasNoPag += rs.getDouble("importe");
                corte7++;
                corte8++;
                corte9++;
                corte10++;
                corte11++;
                corte12++;
                corte13++;
                corte14++;
                corte15++;
                corte16++;
                corte17++;
                corte18++;
            } else if ("BienesMuebles".equals(tipo)) {
                Row rw = (sheet0.getRow(corte8) == null ? sheet0.createRow(corte8) : sheet0.getRow(corte8));
                Util.createExcelCellRep(0, rw, rs, rsMetadata.getColumnName(2), rsMetadata.getColumnType(2), estiloFecha);
                Util.createExcelCellRep(1, rw, rs, rsMetadata.getColumnName(3), rsMetadata.getColumnType(3), estiloTabla);
                Util.createExcelCellRep(2, rw, rs, rsMetadata.getColumnName(4), rsMetadata.getColumnType(4), estiloTabla);
                Util.createExcelCellRep(3, rw, rs, rsMetadata.getColumnName(5), rsMetadata.getColumnType(5), estiloTabla);
                Util.createExcelCellRep(4, rw, rs, rsMetadata.getColumnName(6), rsMetadata.getColumnType(6), estiloTabla);
                Util.createExcelCellRep(5, rw, rs, rsMetadata.getColumnName(7), rsMetadata.getColumnType(7), estiloTabla);
                Util.createExcelCellRep(6, rw, rs, rsMetadata.getColumnName(8), rsMetadata.getColumnType(8), estiloMoneda);
                sheet0.shiftRows(corte8 + 1, corte18 + 1, 1, true, true);
                bienesMuebles += rs.getDouble("importe");
                corte8++;
                corte9++;
                corte10++;
                corte11++;
                corte12++;
                corte13++;
                corte14++;
                corte15++;
                corte16++;
                corte17++;
                corte18++;
            } else if ("ObraP".equals(tipo)) {
                Row rw = (sheet0.getRow(corte9) == null ? sheet0.createRow(corte9) : sheet0.getRow(corte9));
                Util.createExcelCellRep(0, rw, rs, rsMetadata.getColumnName(2), rsMetadata.getColumnType(2), estiloFecha);
                Util.createExcelCellRep(1, rw, rs, rsMetadata.getColumnName(3), rsMetadata.getColumnType(3), estiloTabla);
                Util.createExcelCellRep(2, rw, rs, rsMetadata.getColumnName(4), rsMetadata.getColumnType(4), estiloTabla);
                Util.createExcelCellRep(3, rw, rs, rsMetadata.getColumnName(5), rsMetadata.getColumnType(5), estiloTabla);
                Util.createExcelCellRep(4, rw, rs, rsMetadata.getColumnName(6), rsMetadata.getColumnType(6), estiloTabla);
                Util.createExcelCellRep(5, rw, rs, rsMetadata.getColumnName(7), rsMetadata.getColumnType(7), estiloTabla);
                Util.createExcelCellRep(6, rw, rs, rsMetadata.getColumnName(8), rsMetadata.getColumnType(8), estiloMoneda);
                sheet0.shiftRows(corte9 + 1, corte18 + 1, 1, true, true);
                op += rs.getDouble("importe");
                corte9++;
                corte10++;
                corte11++;
                corte12++;
                corte13++;
                corte14++;
                corte15++;
                corte16++;
                corte17++;
                corte18++;
            } else if ("1233".equals(tipo)) {
                Row rw = (sheet0.getRow(corte10) == null ? sheet0.createRow(corte10) : sheet0.getRow(corte10));
                Util.createExcelCellRep(0, rw, rs, rsMetadata.getColumnName(2), rsMetadata.getColumnType(2), estiloFecha);
                Util.createExcelCellRep(1, rw, rs, rsMetadata.getColumnName(3), rsMetadata.getColumnType(3), estiloTabla);
                Util.createExcelCellRep(2, rw, rs, rsMetadata.getColumnName(4), rsMetadata.getColumnType(4), estiloTabla);
                Util.createExcelCellRep(3, rw, rs, rsMetadata.getColumnName(5), rsMetadata.getColumnType(5), estiloTabla);
                Util.createExcelCellRep(4, rw, rs, rsMetadata.getColumnName(6), rsMetadata.getColumnType(6), estiloTabla);
                Util.createExcelCellRep(5, rw, rs, rsMetadata.getColumnName(7), rsMetadata.getColumnType(7), estiloTabla);
                Util.createExcelCellRep(6, rw, rs, rsMetadata.getColumnName(8), rsMetadata.getColumnType(8), estiloMoneda);
                sheet0.shiftRows(corte10 + 1, corte18 + 1, 1, true, true);
                bienesInmuebles += rs.getDouble("importe");
                corte10++;
                corte11++;
                corte12++;
                corte13++;
                corte14++;
                corte15++;
                corte16++;
                corte17++;
                corte18++;
            } else if ("Anticipos".equals(tipo)) {
                Row rw = (sheet0.getRow(corte11) == null ? sheet0.createRow(corte11) : sheet0.getRow(corte11));
                Util.createExcelCellRep(0, rw, rs, rsMetadata.getColumnName(2), rsMetadata.getColumnType(2), estiloFecha);
                Util.createExcelCellRep(1, rw, rs, rsMetadata.getColumnName(3), rsMetadata.getColumnType(3), estiloTabla);
                Util.createExcelCellRep(2, rw, rs, rsMetadata.getColumnName(4), rsMetadata.getColumnType(4), estiloTabla);
                Util.createExcelCellRep(3, rw, rs, rsMetadata.getColumnName(5), rsMetadata.getColumnType(5), estiloTabla);
                Util.createExcelCellRep(4, rw, rs, rsMetadata.getColumnName(6), rsMetadata.getColumnType(6), estiloTabla);
                Util.createExcelCellRep(5, rw, rs, rsMetadata.getColumnName(7), rsMetadata.getColumnType(7), estiloTabla);
                Util.createExcelCellRep(6, rw, rs, rsMetadata.getColumnName(8), rsMetadata.getColumnType(8), estiloMoneda);
                sheet0.shiftRows(corte11 + 1, corte18 + 1, 1, true, true);
                antic += rs.getDouble("importe");
                corte11++;
                corte12++;
                corte13++;
                corte14++;
                corte15++;
                corte16++;
                corte17++;
                corte18++;
            } else if ("AmortAnt".equals(tipo)) {
                Row rw = (sheet0.getRow(corte12) == null ? sheet0.createRow(corte12) : sheet0.getRow(corte12));
                Util.createExcelCellRep(0, rw, rs, rsMetadata.getColumnName(2), rsMetadata.getColumnType(2), estiloFecha);
                Util.createExcelCellRep(1, rw, rs, rsMetadata.getColumnName(3), rsMetadata.getColumnType(3), estiloTabla);
                Util.createExcelCellRep(2, rw, rs, rsMetadata.getColumnName(4), rsMetadata.getColumnType(4), estiloTabla);
                Util.createExcelCellRep(3, rw, rs, rsMetadata.getColumnName(5), rsMetadata.getColumnType(5), estiloTabla);
                Util.createExcelCellRep(4, rw, rs, rsMetadata.getColumnName(6), rsMetadata.getColumnType(6), estiloTabla);
                Util.createExcelCellRep(5, rw, rs, rsMetadata.getColumnName(7), rsMetadata.getColumnType(7), estiloTabla);
                Util.createExcelCellRep(6, rw, rs, rsMetadata.getColumnName(8), rsMetadata.getColumnType(8), estiloMoneda);
                sheet0.shiftRows(corte12 + 1, corte18 + 1, 1, true, true);
                amAnt += rs.getDouble("importe");
                corte12++;
                corte13++;
                corte14++;
                corte15++;
                corte16++;
                corte17++;
                corte18++;
            } else if ("Almacenes".equals(tipo)) {
                Row rw = (sheet0.getRow(corte13) == null ? sheet0.createRow(corte13) : sheet0.getRow(corte13));
                Util.createExcelCellRep(0, rw, rs, rsMetadata.getColumnName(2), rsMetadata.getColumnType(2), estiloFecha);
                Util.createExcelCellRep(1, rw, rs, rsMetadata.getColumnName(3), rsMetadata.getColumnType(3), estiloTabla);
                Util.createExcelCellRep(2, rw, rs, rsMetadata.getColumnName(4), rsMetadata.getColumnType(4), estiloTabla);
                Util.createExcelCellRep(3, rw, rs, rsMetadata.getColumnName(5), rsMetadata.getColumnType(5), estiloTabla);
                Util.createExcelCellRep(4, rw, rs, rsMetadata.getColumnName(6), rsMetadata.getColumnType(6), estiloTabla);
                Util.createExcelCellRep(5, rw, rs, rsMetadata.getColumnName(7), rsMetadata.getColumnType(7), estiloTabla);
                Util.createExcelCellRep(6, rw, rs, rsMetadata.getColumnName(8), rsMetadata.getColumnType(8), estiloMoneda);
                sheet0.shiftRows(corte13 + 1, corte18 + 1, 1, true, true);
                alm += rs.getDouble("importe");
                corte13++;
                corte14++;
                corte15++;
                corte16++;
                corte17++;
                corte18++;
            } else if ("SalidasAlmacen".equals(tipo)) {
                Row rw = (sheet0.getRow(corte14) == null ? sheet0.createRow(corte14) : sheet0.getRow(corte14));
                Util.createExcelCellRep(0, rw, rs, rsMetadata.getColumnName(2), rsMetadata.getColumnType(2), estiloFecha);
                Util.createExcelCellRep(1, rw, rs, rsMetadata.getColumnName(3), rsMetadata.getColumnType(3), estiloTabla);
                Util.createExcelCellRep(2, rw, rs, rsMetadata.getColumnName(4), rsMetadata.getColumnType(4), estiloTabla);
                Util.createExcelCellRep(3, rw, rs, rsMetadata.getColumnName(5), rsMetadata.getColumnType(5), estiloTabla);
                Util.createExcelCellRep(4, rw, rs, rsMetadata.getColumnName(6), rsMetadata.getColumnType(6), estiloTabla);
                Util.createExcelCellRep(5, rw, rs, rsMetadata.getColumnName(7), rsMetadata.getColumnType(7), estiloTabla);
                Util.createExcelCellRep(6, rw, rs, rsMetadata.getColumnName(8), rsMetadata.getColumnType(8), estiloMoneda);
                sheet0.shiftRows(corte14 + 1, corte18 + 1, 1, true, true);
                salidas += rs.getDouble("importe");
                corte14++;
                corte15++;
                corte16++;
                corte17++;
                corte18++;
            } else if ("FFM".equals(tipo)) {
                Row rw = (sheet0.getRow(corte15) == null ? sheet0.createRow(corte15) : sheet0.getRow(corte15));
                Util.createExcelCellRep(0, rw, rs, rsMetadata.getColumnName(2), rsMetadata.getColumnType(2), estiloFecha);
                Util.createExcelCellRep(1, rw, rs, rsMetadata.getColumnName(3), rsMetadata.getColumnType(3), estiloTabla);
                Util.createExcelCellRep(2, rw, rs, rsMetadata.getColumnName(4), rsMetadata.getColumnType(4), estiloTabla);
                Util.createExcelCellRep(3, rw, rs, rsMetadata.getColumnName(5), rsMetadata.getColumnType(5), estiloTabla);
                Util.createExcelCellRep(4, rw, rs, rsMetadata.getColumnName(6), rsMetadata.getColumnType(6), estiloTabla);
                Util.createExcelCellRep(5, rw, rs, rsMetadata.getColumnName(7), rsMetadata.getColumnType(7), estiloTabla);
                Util.createExcelCellRep(6, rw, rs, rsMetadata.getColumnName(8), rsMetadata.getColumnType(8), estiloMoneda);
                sheet0.shiftRows(corte15 + 1, corte18 + 1, 1, true, true);
                ffm += rs.getDouble("importe");
                corte15++;
                corte16++;
                corte17++;
                corte18++;
            } else if ("ReintegrosFFM".equals(tipo)) {
                Row rw = (sheet0.getRow(corte16) == null ? sheet0.createRow(corte16) : sheet0.getRow(corte16));
                Util.createExcelCellRep(0, rw, rs, rsMetadata.getColumnName(2), rsMetadata.getColumnType(2), estiloFecha);
                Util.createExcelCellRep(1, rw, rs, rsMetadata.getColumnName(3), rsMetadata.getColumnType(3), estiloTabla);
                Util.createExcelCellRep(2, rw, rs, rsMetadata.getColumnName(4), rsMetadata.getColumnType(4), estiloTabla);
                Util.createExcelCellRep(3, rw, rs, rsMetadata.getColumnName(5), rsMetadata.getColumnType(5), estiloTabla);
                Util.createExcelCellRep(4, rw, rs, rsMetadata.getColumnName(6), rsMetadata.getColumnType(6), estiloTabla);
                Util.createExcelCellRep(5, rw, rs, rsMetadata.getColumnName(7), rsMetadata.getColumnType(7), estiloTabla);
                Util.createExcelCellRep(6, rw, rs, rsMetadata.getColumnName(8), rsMetadata.getColumnType(8), estiloMoneda);
                sheet0.shiftRows(corte16 + 1, corte18 + 1, 1, true, true);
                reintegros += rs.getDouble("importe");
                corte16++;
                corte17++;
                corte18++;
            } else if ("AjenasPagAnt".equals(tipo)) {
                Row rw = (sheet0.getRow(corte17) == null ? sheet0.createRow(corte17) : sheet0.getRow(corte17));
                Util.createExcelCellRep(0, rw, rs, rsMetadata.getColumnName(2), rsMetadata.getColumnType(2), estiloFecha);
                Util.createExcelCellRep(1, rw, rs, rsMetadata.getColumnName(3), rsMetadata.getColumnType(3), estiloTabla);
                Util.createExcelCellRep(2, rw, rs, rsMetadata.getColumnName(4), rsMetadata.getColumnType(4), estiloTabla);
                Util.createExcelCellRep(3, rw, rs, rsMetadata.getColumnName(5), rsMetadata.getColumnType(5), estiloTabla);
                Util.createExcelCellRep(4, rw, rs, rsMetadata.getColumnName(6), rsMetadata.getColumnType(6), estiloTabla);
                Util.createExcelCellRep(5, rw, rs, rsMetadata.getColumnName(7), rsMetadata.getColumnType(7), estiloTabla);
                Util.createExcelCellRep(6, rw, rs, rsMetadata.getColumnName(8), rsMetadata.getColumnType(8), estiloMoneda);
                sheet0.shiftRows(corte17 + 1, corte18 + 1, 1, true, true);
                ajenasPagAnt += rs.getDouble("importe");
                corte17++;
                corte18++;
            }
        }
        //, pstm_reint = null, pstm_551 = null, pstm_559 = null;
        PreparedStatement pstm_8151 = null, pstm_4 = null;
        //, nSIR = null, n551 = null, n559 = null ;
        ResultSet nSI = null, n4 = null;
        pstm_8151 = conn.prepareStatement("SELECT CASE cTipoMovimiento WHEN 'A' THEN SUM(mMovimiento) ELSE SUM(-mMovimiento) END AS cargo " + " FROM dbo.tMovimiento WITH (NOLOCK) " + " WHERE SUBSTRING(nCuenta,1,4) IN('8151') " + " AND MONTH(fMovimiento) BETWEEN 1 AND " + mes + " AND YEAR(fMovimiento) = " + anio + " GROUP BY SUBSTRING(nCuenta,1,4), cTipoMovimiento");
        nSI = pstm_8151.executeQuery();
        while (nSI.next()) {
            saldo8151 = saldo8151 + nSI.getDouble("cargo");
        }
        pstm_4 = conn.prepareStatement("SELECT CASE cTipoMovimiento WHEN 'A' THEN SUM(mMovimiento) ELSE SUM(-mMovimiento) END AS importe " + " FROM dbo.tMovimiento WITH (NOLOCK) " + " WHERE SUBSTRING(nCuenta,1,1) IN('4') " + " AND MONTH(fMovimiento) BETWEEN 1 AND " + mes + " AND YEAR(fMovimiento) = " + anio + " GROUP BY SUBSTRING(nCuenta,1,4), cTipoMovimiento");
        n4 = pstm_4.executeQuery();
        while (n4.next()) {
            saldoIngreso = saldoIngreso + n4.getDouble("importe");
        }
        formateador.format(amort);
        formateador.format(cAlmacen);
        formateador.format(cFFM);
        formateador.format(obrasAA);
        sOtrosIngresos = amort + cAlmacen + cFFM + obrasAA;
        formateador.format(sOtrosIngresos);
        formateador.format(devNoPag);
        formateador.format(ajenasNoPag);
        formateador.format(devCanc);
        saldoICNP = devNoPag + ajenasNoPag + devCanc;
        formateador.format(saldoICNP);
        formateador.format(saldo8151);
        formateador.format(bienesMuebles);
        formateador.format(bienesInmuebles);
        formateador.format(op);
        formateador.format(antic);
        formateador.format(amAnt);
        formateador.format(alm);
        formateador.format(ffm);
        formateador.format(reintegros);
        formateador.format(salidas);
        saldo3113 = bienesMuebles + op + bienesInmuebles;
        formateador.format(ajenasPagAnt);
        formateador.format(saldo3113);
        saldo31132 = antic + amAnt + alm + ffm + reintegros + salidas;
        saldoIPNC = saldo3113 + saldo31132;
        formateador.format(saldo31132);
        formateador.format(saldoIPNC);
        totalIngresosContables = sOtrosIngresos + saldoICNP;
        formateador.format(totalIngresosContables);
        Row rwEnc3 = (sheet0.getRow(4) == null ? sheet0.createRow(4) : sheet0.getRow(4));
        Cell cell3 = (rwEnc3.getCell(8) == null ? rwEnc3.createCell(8) : rwEnc3.getCell(8));
        cell3.setCellValue(saldo8151);
        Row rwEnc30 = (sheet0.getRow(6) == null ? sheet0.createRow(6) : sheet0.getRow(6));
        Cell cell30 = (rwEnc30.getCell(8) == null ? rwEnc30.createCell(8) : rwEnc30.getCell(8));
        cell30.setCellValue(totalIngresosContables);
        Row rwEnc1 = (sheet0.getRow(9) == null ? sheet0.createRow(9) : sheet0.getRow(9));
        Cell cell1 = (rwEnc1.getCell(7) == null ? rwEnc1.createCell(7) : rwEnc1.getCell(7));
        cell1.setCellValue(amort);
        Row rwEnc31 = (sheet0.getRow(corte1 + 2) == null ? sheet0.createRow(corte1 + 2) : sheet0.getRow(corte1 + 2));
        Cell cell31 = (rwEnc31.getCell(7) == null ? rwEnc31.createCell(7) : rwEnc31.getCell(7));
        cell31.setCellValue(cAlmacen);
        Row rwEnc4 = (sheet0.getRow(corte2 + 2) == null ? sheet0.createRow(corte2 + 2) : sheet0.getRow(corte2 + 2));
        Cell cell4 = (rwEnc4.getCell(7) == null ? rwEnc4.createCell(7) : rwEnc4.getCell(7));
        cell4.setCellValue(obrasAA);
        Row rwEnc8 = (sheet0.getRow(8) == null ? sheet0.createRow(8) : sheet0.getRow(8));
        Cell cell8 = (rwEnc8.getCell(7) == null ? rwEnc8.createCell(7) : rwEnc8.getCell(7));
        cell8.setCellValue(sOtrosIngresos);
        Row rwEnc25 = (sheet0.getRow(corte3 + 2) == null ? sheet0.createRow(corte3 + 2) : sheet0.getRow(corte3 + 2));
        Cell cell25 = (rwEnc25.getCell(7) == null ? rwEnc25.createCell(7) : rwEnc25.getCell(7));
        cell25.setCellValue(cFFM);
        Row rwEnc10 = (sheet0.getRow(corte4 + 2) == null ? sheet0.createRow(corte4 + 2) : sheet0.getRow(corte4 + 2));
        Cell cell10 = (rwEnc10.getCell(7) == null ? rwEnc10.createCell(7) : rwEnc10.getCell(7));
        cell10.setCellValue(saldoICNP);
        Row rwEnc5 = (sheet0.getRow(corte4 + 3) == null ? sheet0.createRow(corte4 + 3) : sheet0.getRow(corte4 + 3));
        Cell cell5 = (rwEnc5.getCell(7) == null ? rwEnc5.createCell(7) : rwEnc5.getCell(7));
        cell5.setCellValue(devNoPag);
        Row rwEnc6 = (sheet0.getRow(corte5 + 2) == null ? sheet0.createRow(corte5 + 2) : sheet0.getRow(corte5 + 2));
        Cell cell6 = (rwEnc6.getCell(7) == null ? rwEnc6.createCell(7) : rwEnc6.getCell(7));
        cell6.setCellValue(devCanc);
        Row rwEnc7 = (sheet0.getRow(corte6 + 2) == null ? sheet0.createRow(corte6 + 2) : sheet0.getRow(corte6 + 2));
        Cell cell7 = (rwEnc7.getCell(7) == null ? rwEnc7.createCell(7) : rwEnc7.getCell(7));
        cell7.setCellValue(ajenasNoPag);
        Row rwEnc21 = (sheet0.getRow(corte7 + 3) == null ? sheet0.createRow(corte7 + 3) : sheet0.getRow(corte7 + 3));
        Cell cell21 = (rwEnc21.getCell(7) == null ? rwEnc21.createCell(7) : rwEnc21.getCell(7));
        cell21.setCellValue(bienesMuebles);
        Row rwEnc12 = (sheet0.getRow(corte7 - 1) == null ? sheet0.createRow(corte7 - 1) : sheet0.getRow(corte7 - 1));
        Cell cell12 = (rwEnc12.getCell(8) == null ? rwEnc12.createCell(8) : rwEnc12.getCell(8));
        cell12.setCellValue(saldoIPNC);
        Row rwEnc19 = (sheet0.getRow(corte7 + 2) == null ? sheet0.createRow(corte7 + 2) : sheet0.getRow(corte7 + 2));
        Cell cell19 = (rwEnc19.getCell(7) == null ? rwEnc19.createCell(7) : rwEnc19.getCell(7));
        cell19.setCellValue(saldo3113);
        Row rwEnc9 = (sheet0.getRow(corte8 + 2) == null ? sheet0.createRow(corte8 + 2) : sheet0.getRow(corte8 + 2));
        Cell cell9 = (rwEnc9.getCell(7) == null ? rwEnc9.createCell(7) : rwEnc9.getCell(7));
        cell9.setCellValue(op);
        Row rwEnc34 = (sheet0.getRow(corte9 + 2) == null ? sheet0.createRow(corte9 + 2) : sheet0.getRow(corte9 + 2));
        Cell cell34 = (rwEnc34.getCell(7) == null ? rwEnc34.createCell(7) : rwEnc34.getCell(7));
        cell34.setCellValue(bienesInmuebles);
        Row rwEnc13 = (sheet0.getRow(corte10 + 4) == null ? sheet0.createRow(corte10 + 4) : sheet0.getRow(corte10 + 4));
        Cell cell13 = (rwEnc13.getCell(7) == null ? rwEnc13.createCell(7) : rwEnc13.getCell(7));
        cell13.setCellValue(antic);
        Row rwEnc22 = (sheet0.getRow(corte10 + 3) == null ? sheet0.createRow(corte10 + 3) : sheet0.getRow(corte10 + 3));
        Cell cell22 = (rwEnc22.getCell(7) == null ? rwEnc22.createCell(7) : rwEnc22.getCell(7));
        cell22.setCellValue(saldo31132);
        Row rwEnc14 = (sheet0.getRow(corte11 + 2) == null ? sheet0.createRow(corte11 + 2) : sheet0.getRow(corte11 + 2));
        Cell cell14 = (rwEnc14.getCell(7) == null ? rwEnc14.createCell(7) : rwEnc14.getCell(7));
        cell14.setCellValue(amAnt);
        Row rwEnc15 = (sheet0.getRow(corte12 + 2) == null ? sheet0.createRow(corte12 + 2) : sheet0.getRow(corte12 + 2));
        Cell cell15 = (rwEnc15.getCell(7) == null ? rwEnc15.createCell(7) : rwEnc15.getCell(7));
        cell15.setCellValue(alm);
        Row rwEnc16 = (sheet0.getRow(corte13 + 2) == null ? sheet0.createRow(corte13 + 2) : sheet0.getRow(corte13 + 2));
        Cell cell16 = (rwEnc16.getCell(7) == null ? rwEnc16.createCell(7) : rwEnc16.getCell(7));
        cell16.setCellValue(salidas);
        Row rwEnc17 = (sheet0.getRow(corte14 + 2) == null ? sheet0.createRow(corte14 + 2) : sheet0.getRow(corte14 + 2));
        Cell cell17 = (rwEnc17.getCell(7) == null ? rwEnc17.createCell(7) : rwEnc17.getCell(7));
        cell17.setCellValue(ffm);
        Row rwEnc18 = (sheet0.getRow(corte15 + 2) == null ? sheet0.createRow(corte15 + 2) : sheet0.getRow(corte15 + 2));
        Cell cell18 = (rwEnc18.getCell(7) == null ? rwEnc18.createCell(7) : rwEnc18.getCell(7));
        cell18.setCellValue(reintegros);
        Row rwEnc20 = (sheet0.getRow(corte16 + 2) == null ? sheet0.createRow(corte16 + 2) : sheet0.getRow(corte16 + 2));
        Cell cell20 = (rwEnc20.getCell(7) == null ? rwEnc20.createCell(7) : rwEnc20.getCell(7));
        cell20.setCellValue(ajenasPagAnt);
        Row rwEnc11 = (sheet0.getRow(corte18 - 1) == null ? sheet0.createRow(corte18 - 1) : sheet0.getRow(corte18 - 1));
        Cell cell11 = (rwEnc11.getCell(8) == null ? rwEnc11.createCell(8) : rwEnc11.getCell(8));
        cell11.setCellValue(saldoIngreso);
        Row rwEnc33 = (sheet0.getRow(corte18) == null ? sheet0.createRow(corte18) : sheet0.getRow(corte18));
        Cell cell33 = (rwEnc33.getCell(8) == null ? rwEnc33.createCell(8) : rwEnc33.getCell(8));
        cell33.setCellValue(saldo8151 + totalIngresosContables - saldoIPNC);
        File fsalida = new File(file_name);
        FileOutputStream fos = new FileOutputStream(fsalida);
        BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
        workbook.write(bos.toPath());
        workbook.close();
        /* Cierra Flujos */
        bos.flush();
        bos.close();
        fos.close();
        return file_name;
    }

    public static String reporteFFM(Connection conn, int mesFin, Map<String, String> plantillas) throws Exception {
        CallableStatement cs = null;
        ResultSet rs = null;
        String query = "{call sp_ReporteFFM( ? )}";
        String fileName = "";
        try {
            cs = conn.prepareCall(query);
            cs.setInt(1, mesFin);
            rs = cs.executeQuery();
            fileName = generaReporteFFM(rs, plantillas.get("PAGOSFFM"), mesFin);
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(cs, false);
        }
    }

    private static String generaReporteFFM(ResultSet rs, String plantillaPath, int mesFin) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "CONCILIACION_MOMENTOS" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        int cnt1 = 0;
        ResultSetMetaData rsMetadata = rs.getMetaData();
        int renglonInicio = 7;
        CellStyle estiloTabla = Util.generaEstilo2(workbook, 8, false, true, true, true, true, false);
        CellStyle estiloFecha = Util.generaEstilo2(workbook, 8, false, true, true, true, true, false);
        DataFormat df = workbook.createDataFormat();
        estiloFecha.setDataFormat(df.getFormat("dd/mm/yyyy"));
        CellStyle estiloMoney = Util.generaEstilo2(workbook, 8, false, true, true, true, true, true);
        while (rs.next()) {
            Row rw = (sheet0.getRow(renglonInicio + cnt1) == null ? sheet0.createRow(renglonInicio + cnt1) : sheet0.getRow(renglonInicio + cnt1));
            for (int i = 0; i <= 10; i++) {
                if (i == 3 || i == 9)
                    Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloMoney);
                else if (i == 4)
                    Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloFecha);
                else
                    Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
            }
            cnt1++;
        }
        File fsalida = new File(file_name);
        FileOutputStream fos = new FileOutputStream(fsalida);
        BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
        workbook.write(bos.toPath());
        workbook.close();
        /* Cierra Flujos */
        bos.flush();
        bos.close();
        fos.close();
        return file_name;
    }

    public static String generaCedulaPatrimonioManager(Connection conn, int mesFin, Map<String, String> plantillas) throws Exception {
        CallableStatement cs = null;
        ResultSet rs = null;
        CallableStatement cs2 = null;
        ResultSet rs2 = null;
        CallableStatement cs3 = null;
        ResultSet rs3 = null;
        String query = "{call sp_CedPatrimonio( ? )}";
        String query2 = "{call sp_CedPatrimonioDet( ? )}";
        String query3 = "{call sp_CedPatrimonioComprob( ? )}";
        String fileName = "";
        try {
            cs = conn.prepareCall(query);
            cs.setInt(1, mesFin);
            rs = cs.executeQuery();
            cs2 = conn.prepareCall(query2);
            cs2.setInt(1, mesFin);
            rs2 = cs2.executeQuery();
            cs3 = conn.prepareCall(query3);
            cs3.setInt(1, mesFin);
            rs3 = cs3.executeQuery();
            fileName = generaCedulaPatrimonio(rs, rs2, rs3, plantillas.get("PATRIMONIO"), mesFin);
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(cs, false);
            CloseObject.closeObject(rs2, false);
            CloseObject.closeObject(cs2, false);
            CloseObject.closeObject(rs3, false);
            CloseObject.closeObject(cs3, false);
        }
    }

    private static String generaCedulaPatrimonio(ResultSet rs, ResultSet rs2, ResultSet rs3, String plantillaPath, int mesFin) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "CEDULA_PATRIMONIO" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        Sheet sheet1 = workbook.getSheetAt(1);
        int cnt1 = 0;
        ResultSetMetaData rsMetadata = rs.getMetaData();
        ResultSetMetaData rsMetadata2 = rs2.getMetaData();
        ResultSetMetaData rsMetadata3 = rs3.getMetaData();
        int renglonInicio = 6;
        CellStyle estiloTabla = Util.generaEstilo2(workbook, 8, false, true, true, true, true, false);
        CellStyle Moneda = Util.generaEstilo2(workbook, 8, false, true, true, true, true, true);
        while (rs.next()) {
            Row rw = (sheet0.getRow(renglonInicio + cnt1) == null ? sheet0.createRow(renglonInicio + cnt1) : sheet0.getRow(renglonInicio + cnt1));
            for (int i = 0; i < rsMetadata.getColumnCount(); i++) {
                Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
            }
            cnt1++;
        }
        cnt1 = 0;
        while (rs2.next()) {
            Row rw2 = (sheet1.getRow(renglonInicio + cnt1) == null ? sheet1.createRow(renglonInicio + cnt1) : sheet1.getRow(renglonInicio + cnt1));
            for (int i = 0; i < rsMetadata2.getColumnCount(); i++) {
                Util.createExcelCellRep(i, rw2, rs2, rsMetadata2.getColumnName(i + 1), rsMetadata2.getColumnType(i + 1), estiloTabla);
            }
            cnt1++;
        }
        cnt1 = 0;
        while (rs3.next()) {
            Row rw3 = (sheet1.getRow(renglonInicio + cnt1) == null ? sheet1.createRow(renglonInicio + cnt1) : sheet1.getRow(renglonInicio + cnt1));
            Util.createExcelCellRep(8, rw3, rs3, rsMetadata3.getColumnName(1), rsMetadata3.getColumnType(1), estiloTabla);
            Util.createExcelCellRep(9, rw3, rs3, rsMetadata3.getColumnName(2), rsMetadata3.getColumnType(2), Moneda);
            Util.createExcelCellRep(10, rw3, rs3, rsMetadata3.getColumnName(3), rsMetadata3.getColumnType(3), Moneda);
            cnt1++;
        }
        File fsalida = new File(file_name);
        FileOutputStream fos = new FileOutputStream(fsalida);
        BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
        workbook.write(bos.toPath());
        workbook.close();
        /* Cierra Flujos */
        bos.flush();
        bos.close();
        fos.close();
        return file_name;
    }

    public static String conciliaDevengadoIngManager(Connection conn, int mesFin, Map<String, String> plantillas) throws Exception {
        CallableStatement cs = null;
        ResultSet rs = null;
        String query = "{call sp_ConciliaDevengadoEPIng( ? )}";
        String fileName = "";
        try {
            cs = conn.prepareCall(query);
            cs.setInt(1, mesFin);
            rs = cs.executeQuery();
            fileName = generaConciliaDevengadoIng(rs, /*rs2,*/
            plantillas.get("CONCILIADEVENGADOING"), mesFin);
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(cs, false);
        }
    }

    private static String generaConciliaDevengadoIng(ResultSet rs, /*ResultSet rs2,*/
    String plantillaPath, int mesFin) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "CONCILIACION_DEVENGADO_ING" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        int cnt1 = 0;
        ResultSetMetaData rsMetadata = rs.getMetaData();
        int renglonInicio = 5;
        CellStyle estiloTabla = Util.generaEstilo2(workbook, 8, false, true, true, true, true, false);
        while (rs.next()) {
            Row rw = (sheet0.getRow(renglonInicio + cnt1) == null ? sheet0.createRow(renglonInicio + cnt1) : sheet0.getRow(renglonInicio + cnt1));
            for (int i = 0; i < rsMetadata.getColumnCount(); i++) {
                Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
            }
            cnt1++;
        }
        File fsalida = new File(file_name);
        FileOutputStream fos = new FileOutputStream(fsalida);
        BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
        workbook.write(bos.toPath());
        workbook.close();
        /* Cierra Flujos */
        bos.flush();
        bos.close();
        fos.close();
        return file_name;
    }

    public static String conciliaOrgDispModEjeIngManager(Connection conn, int mesFin, Map<String, String> plantillas) throws Exception {
        CallableStatement cs = null;
        ResultSet rs = null;
        String query = "{call sp_ConciliaOrgDisModEjeEPIng( ? )}";
        String fileName = "";
        try {
            cs = conn.prepareCall(query);
            cs.setInt(1, mesFin);
            rs = cs.executeQuery();
            fileName = generaConciliaOrgDispModEjeIng(rs, /*rs2,*/
            plantillas.get("CONCILIAORGDISPMODEJEING"), mesFin);
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(cs, false);
        }
    }

    private static String generaConciliaOrgDispModEjeIng(ResultSet rs, /*ResultSet rs2,*/
    String plantillaPath, int mesFin) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "CONCILIACION_ORGDISPMODEJE_ING" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        int cnt1 = 0;
        ResultSetMetaData rsMetadata = rs.getMetaData();
        int renglonInicio = 5;
        CellStyle estiloTabla = Util.generaEstilo2(workbook, 8, false, true, true, true, true, false);
        while (rs.next()) {
            Row rw = (sheet0.getRow(renglonInicio + cnt1) == null ? sheet0.createRow(renglonInicio + cnt1) : sheet0.getRow(renglonInicio + cnt1));
            for (int i = 0; i < rsMetadata.getColumnCount(); i++) {
                Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
            }
            cnt1++;
        }
        File fsalida = new File(file_name);
        FileOutputStream fos = new FileOutputStream(fsalida);
        BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
        workbook.write(bos.toPath());
        workbook.close();
        /* Cierra Flujos */
        bos.flush();
        bos.close();
        fos.close();
        return file_name;
    }

    public static String conciliaRadicadoPagado(Connection conn, int mesFin, Map<String, String> plantillas) throws Exception {
        CallableStatement cs = null;
        ResultSet rs = null;
        String query = "{call sp_ConciliaRadicadoPagado( ? )}";
        String fileName = "";
        try {
            cs = conn.prepareCall(query);
            cs.setInt(1, mesFin);
            rs = cs.executeQuery();
            fileName = generaConciliaRadicadoPagado(rs, /*rs2,*/
            plantillas.get("ConciliaRadicadoPagado"), mesFin);
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(cs, false);
        }
    }

    private static String generaConciliaRadicadoPagado(ResultSet rs, /*ResultSet rs2,*/
    String plantillaPath, int mesFin) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "CONCILIACION_RAD_PAG" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        int cnt1 = 0;
        ResultSetMetaData rsMetadata = rs.getMetaData();
        int renglonInicio = 5;
        CellStyle estiloTabla = Util.generaEstilo2(workbook, 8, false, true, true, true, true, false);
        while (rs.next()) {
            Row rw = (sheet0.getRow(renglonInicio + cnt1) == null ? sheet0.createRow(renglonInicio + cnt1) : sheet0.getRow(renglonInicio + cnt1));
            for (int i = 0; i < rsMetadata.getColumnCount(); i++) {
                Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
            }
            cnt1++;
        }
        File fsalida = new File(file_name);
        FileOutputStream fos = new FileOutputStream(fsalida);
        BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
        workbook.write(bos.toPath());
        workbook.close();
        /* Cierra Flujos */
        bos.flush();
        bos.close();
        fos.close();
        return file_name;
    }

    public static String generaReporteIPManager(Connection conn, int mesFin, Map<String, String> plantillas) throws Exception {
        CallableStatement cs = null;
        ResultSet rs = null;
        String query = "{call sp_ReporteIngresosPropios( ? )}";
        String fileName = "";
        try {
            cs = conn.prepareCall(query);
            cs.setInt(1, mesFin);
            rs = cs.executeQuery();
            fileName = generadorReporteIPManager(rs, plantillas.get("ReporteIP"), mesFin);
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(cs, false);
        }
    }

    private static String generadorReporteIPManager(ResultSet rs, String plantillaPath, int mesFin) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "INGRESOS_PROPIOS" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        int cnt1 = 0;
        ResultSetMetaData rsMetadata = rs.getMetaData();
        int renglonInicio = 6;
        CellStyle estiloTabla = Util.generaEstilo2(workbook, 8, false, true, true, true, true, false);
        CellStyle Moneda = Util.generaEstilo2(workbook, 8, false, true, true, true, true, true);
        while (rs.next()) {
            Row rw = (sheet0.getRow(renglonInicio + cnt1) == null ? sheet0.createRow(renglonInicio + cnt1) : sheet0.getRow(renglonInicio + cnt1));
            Util.createExcelCellRep(1, rw, rs, rsMetadata.getColumnName(1), rsMetadata.getColumnType(1), estiloTabla);
            Util.createExcelCellRep(2, rw, rs, rsMetadata.getColumnName(2), rsMetadata.getColumnType(2), estiloTabla);
            Util.createExcelCellRep(3, rw, rs, rsMetadata.getColumnName(3), rsMetadata.getColumnType(3), Moneda);
            cnt1++;
        }
        File fsalida = new File(file_name);
        FileOutputStream fos = new FileOutputStream(fsalida);
        BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
        workbook.write(bos.toPath());
        workbook.close();
        /* Cierra Flujos */
        bos.flush();
        bos.close();
        fos.close();
        return file_name;
    }

    public static void actualizaSaldosManager(Connection conn, Integer mes) throws Exception {
        CallableStatement cs = null;
        String query = "{call sp_actualizarSaldos( ? )}";
        try {
            cs = conn.prepareCall(query);
            cs.setInt(1, mes);
            cs.execute();
        } finally {
            CloseObject.closeObject(cs, false);
        }
    }

    public static String generaReporteCapacitacionManager(Connection conn, int mes, int anio, Map<String, String> plantillas) throws Exception {
        CallableStatement cs = null, cs2 = null;
        ResultSet rs = null, rs2 = null;
        String query = "{call sp_Capacitacion33401( ?, ? )}";
        String query2 = "{call sp_Capacitacion33401Detalle( ? )}";
        String fileName = "";
        try {
            cs = conn.prepareCall(query);
            cs.setInt(1, mes);
            cs.setInt(2, anio);
            rs = cs.executeQuery();
            cs2 = conn.prepareCall(query2);
            cs2.setInt(1, mes);
            rs2 = cs2.executeQuery();
            fileName = generaReporteCapacitacion(rs, rs2, plantillas.get("CAPACITACION"), mes, anio);
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(cs, false);
        }
    }

    private static String generaReporteCapacitacion(ResultSet rs, ResultSet rs2, String plantillaPath, int mes, int anio) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "ReporteCapacitacion33401" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        Sheet sheet1 = workbook.getSheetAt(1);
        int cnt1 = 9;
        int cnt2 = 9;
        float org = 0;
        float mod = 0;
        float pag = 0;
        DecimalFormat df = new DecimalFormat("#,###.##");
        ResultSetMetaData rsMetadata = rs.getMetaData();
        ResultSetMetaData rs2Metadata = rs2.getMetaData();
        String periodo = "";
        periodo = Util.NOMBRE_MESES_MX[mes - 1] + " " + anio;
        Row rwEnc2 = (sheet0.getRow(2) == null ? sheet0.createRow(2) : sheet0.getRow(2));
        Cell cell2 = (rwEnc2.getCell(11) == null ? rwEnc2.createCell(11) : rwEnc2.getCell(11));
        cell2.setCellValue(periodo);
        CellStyle estiloMoneda = Util.generaEstilo2(workbook, 9, false, true, true, true, true, true);
        while (rs.next()) {
            String ff = rs.getString(12);
            if ("4".equals(ff)) {
                org = org + (rs.getFloat(15));
                mod = mod + (rs.getFloat(16));
                pag = pag + (rs.getFloat(29));
            }
            int rows = cnt1;
            sheet0.shiftRows(rows, cnt1 + 1, 1);
            Row rw = (sheet0.getRow(cnt1) == null ? sheet0.createRow(cnt1) : sheet0.getRow(cnt1));
            for (int i = 0; i < rsMetadata.getColumnCount(); i++) {
                Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloMoneda);
            }
            cnt1++;
        }
        String LeyendaORG = "El presupuesto anual original incluye $ " + df.format(org) + " pesos de ingresos diversos que se programaron recibir y a la fecha no han sido recibidos.";
        Row rwEnc3 = (sheet0.getRow(cnt1 + 3) == null ? sheet0.createRow(cnt1 + 3) : sheet0.getRow(cnt1 + 3));
        Cell cell3 = (rwEnc3.getCell(0) == null ? rwEnc3.createCell(0) : rwEnc3.getCell(0));
        cell3.setCellValue(LeyendaORG);
        String LeyendaMOD = "El presupuesto anual modificado incluye $ " + df.format(mod) + " pesos de ingresos diversos que se programaron recibir y a la fecha no han sido recibidos.";
        Row rwEnc4 = (sheet0.getRow(cnt1 + 4) == null ? sheet0.createRow(cnt1 + 4) : sheet0.getRow(cnt1 + 4));
        Cell cell4 = (rwEnc4.getCell(0) == null ? rwEnc4.createCell(0) : rwEnc4.getCell(0));
        cell4.setCellValue(LeyendaMOD);
        String LeyendaPAG = "El presupuesto ejercido incluye $ " + df.format(pag) + " pesos de ingresos diversos recibidos y ejercidos en la partida 33401.";
        Row rwEnc5 = (sheet0.getRow(cnt1 + 5) == null ? sheet0.createRow(cnt1 + 5) : sheet0.getRow(cnt1 + 5));
        Cell cell5 = (rwEnc5.getCell(0) == null ? rwEnc5.createCell(0) : rwEnc5.getCell(0));
        cell5.setCellValue(LeyendaPAG);
        while (rs2.next()) {
            int rows2 = cnt2;
            sheet1.shiftRows(rows2, cnt2 + 1, 1);
            Row rw = (sheet1.getRow(cnt2) == null ? sheet1.createRow(cnt2) : sheet1.getRow(cnt2));
            for (int i = 0; i < rs2Metadata.getColumnCount(); i++) {
                Util.createExcelCellRep(i, rw, rs2, rs2Metadata.getColumnName(i + 1), rs2Metadata.getColumnType(i + 1), estiloMoneda);
            }
            cnt2++;
        }
        sheet0 = Util.EvaluaFormula(workbook, sheet0, 9, (cnt1 + 2), 14, 30);
        File fsalida = new File(file_name);
        FileOutputStream fos = new FileOutputStream(fsalida);
        BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
        workbook.write(bos.toPath());
        workbook.close();
        /* Cierra Flujos */
        bos.flush();
        bos.close();
        fos.close();
        return file_name;
    }
}
