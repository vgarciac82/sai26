package com.syc.reportes.core;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Map;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.obrapublica.ConfiguraAplicativoBusinessLogic;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import java.util.Base64;
import java.nio.file.Paths;

public class ReporteSipotManager {

    private static ConfiguraAplicativoBusinessLogic cabl = new ConfiguraAplicativoBusinessLogic(GestionInterface.ATT_CONEXION);

    private static String tipoFuente = cabl.getSystemSetting("FUENTE_INSTITUCIONAL");

    public static String flujoObjetoGastoManager(Connection conn, int mesFin, Map<String, String> plantillas, String fechaFin) throws Exception {
        CallableStatement cs = null;
        ResultSet rs = null;
        String query = "{call sp_flujoObgt( ? )}";
        String fileName = "";
        try {
            cs = conn.prepareCall(query);
            cs.setInt(1, mesFin);
            rs = cs.executeQuery();
            fileName = generaflujoObgt(rs, plantillas.get("FLUJOOBGT"), mesFin, fechaFin);
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(cs, false);
        }
    }

    private static String generaflujoObgt(ResultSet rs, String plantillaPath, int mesFin, String fechaFin) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "FLUJO_OBGT" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        int mes;
        String dia, anio, meses;
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        int cnt1 = 0;
        int renglonInicio = 9;
        double original = 0, ampliacion = 0, mod = 0, dev = 0, pag = 0, sub = 0;
        mes = Integer.parseInt(fechaFin.substring(3, 5));
        dia = fechaFin.substring(0, 2);
        anio = fechaFin.substring(6, 10);
        meses = Util.NOMBRE_MESES_MX[mes - 1].toLowerCase();
        meses = Util.firstUpper(meses);
        String periodo = "Del 01 de Enero al " + dia + " de " + meses + " " + anio;
        Row rwEnc2 = (sheet0.getRow(4) == null ? sheet0.createRow(4) : sheet0.getRow(4));
        Cell cell2 = (rwEnc2.getCell(0) == null ? rwEnc2.createCell(0) : rwEnc2.getCell(0));
        cell2.setCellValue(periodo);
        ResultSetMetaData rsMetadata = rs.getMetaData();
        CellStyle estiloMoney = Util.generaEstilo(workbook, 7, false, true, false, false, true);
        estiloMoney.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        estiloMoney.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        CellStyle estilo2 = Util.generaEstilo(workbook, 7, false, false, false, true, false);
        estilo2.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        estilo2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        CellStyle esTitulo = Util.generaEstilo(workbook, 7, true, true, false, false, true);
        esTitulo.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        esTitulo.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        CellStyle esTitulo2 = Util.generaEstilo(workbook, 7, true, true, false, true, true);
        esTitulo2.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        esTitulo2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        while (rs.next()) {
            Row rw = (sheet0.getRow(renglonInicio + cnt1) == null ? sheet0.createRow(renglonInicio + cnt1) : sheet0.getRow(renglonInicio + cnt1));
            if ("1".equals(rs.getString(9))) {
                for (int h = 1; h <= 8; h++) {
                    Util.createExcelCellRep(h, rw, rs, rsMetadata.getColumnName(h), rsMetadata.getColumnType(h), esTitulo);
                    if (h == 1)
                        h++;
                }
                cnt1++;
            } else if ("0".equals(rs.getString(9))) {
                for (int h = 1; h <= 8; h++) {
                    Util.createExcelCellRep(h, rw, rs, rsMetadata.getColumnName(h), rsMetadata.getColumnType(h), estiloMoney);
                    if (h == 1)
                        h++;
                }
                original = original + rs.getDouble(3);
                ampliacion = ampliacion + rs.getDouble(4);
                mod = mod + rs.getDouble(5);
                dev = dev + rs.getDouble(6);
                pag = pag + rs.getDouble(7);
                sub = sub + rs.getDouble(8);
                cnt1++;
            }
        }
        Row rwEnc3 = (sheet0.getRow(renglonInicio + cnt1) == null ? sheet0.createRow(renglonInicio + cnt1) : sheet0.getRow(renglonInicio + cnt1));
        for (int k = 0; k <= 8; k++) {
            Cell cell0 = (rwEnc3.getCell(k) == null ? rwEnc3.createCell(k) : rwEnc3.getCell(k));
            cell0.setCellStyle(esTitulo2);
            switch(k) {
                case 1:
                    cell0.setCellValue("Total del Gasto");
                    break;
                case 3:
                    cell0.setCellValue(original);
                    break;
                case 4:
                    cell0.setCellValue(ampliacion);
                    break;
                case 5:
                    cell0.setCellValue(mod);
                    break;
                case 6:
                    cell0.setCellValue(dev);
                    break;
                case 7:
                    cell0.setCellValue(pag);
                    break;
                case 8:
                    cell0.setCellValue(sub);
                    break;
            }
        }
        Cell cell10 = (rwEnc3.getCell(0) == null ? rwEnc3.createCell(0) : rwEnc3.getCell(0));
        cell10.setCellStyle(estilo2);
        Row rwEnc4 = (sheet0.getRow(renglonInicio + cnt1 + 1) == null ? sheet0.createRow(renglonInicio + cnt1 + 1) : sheet0.getRow(renglonInicio + cnt1 + 1));
        Cell cell9 = (rwEnc4.getCell(1) == null ? rwEnc4.createCell(1) : rwEnc4.getCell(1));
        cell9.setCellStyle(estiloMoney);
        cell9.setCellValue("Fuente: Sistema de registro de la Comisión Nacional Forestal.");
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

    public static String flujoClasifAdmManager(Connection conn, int mesFin, Map<String, String> plantillas, String fechaFin, String reporte) throws Exception {
        CallableStatement cs = null;
        ResultSet rs = null;
        String query = "{call sp_flujoCA( ? )}";
        String fileName = "";
        try {
            cs = conn.prepareCall(query);
            cs.setInt(1, mesFin);
            rs = cs.executeQuery();
            if ("FLUJOCAA".equals(reporte))
                fileName = generaflujoClasifAdmManager(conn, rs, plantillas.get("FLUJOCAA"), mesFin, fechaFin);
            else
                fileName = generaflujoClasifAdmArmManager(conn, rs, plantillas.get("FLUJOCA"), mesFin, fechaFin);
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(cs, false);
        }
    }

    private static String generaflujoClasifAdmManager(Connection conn, ResultSet rs, String plantillaPath, int mesFin, String fechaFin) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "FlujoClasAdm" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        int mes = 0;
        String anio = "";
        String dia, meses;
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        mes = Integer.parseInt(fechaFin.substring(3, 5));
        anio = fechaFin.substring(6, 10);
        dia = fechaFin.substring(0, 2);
        meses = Util.NOMBRE_MESES_MX[mes - 1].toLowerCase();
        meses = Util.firstUpper(meses);
        String periodo = "Del 01 de Enero al " + dia + " de " + meses + " " + anio;
        Row rwEnc2 = (sheet0.getRow(4) == null ? sheet0.createRow(4) : sheet0.getRow(4));
        Cell cell2 = (rwEnc2.getCell(0) == null ? rwEnc2.createCell(0) : rwEnc2.getCell(0));
        cell2.setCellValue(periodo);
        ResultSetMetaData rsMetadata = rs.getMetaData();
        int renglonInicio = 9;
        int cnt1 = 0;
        CellStyle estiloMoney = Util.generaEstilo(workbook, 7, false, true, true, true, true);
        while (rs.next()) {
            Row rw = (sheet0.getRow(renglonInicio + cnt1) == null ? sheet0.createRow(renglonInicio + cnt1) : sheet0.getRow(renglonInicio + cnt1));
            for (int i = 1; i <= 6; i++) {
                Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i), rsMetadata.getColumnType(i), estiloMoney);
            }
            cnt1++;
        }
        sheet0 = Util.EvaluaFormula(workbook, sheet0, 10, 11, 1, 7);
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

    private static String generaflujoClasifAdmArmManager(Connection conn, ResultSet rs, String plantillaPath, int mesFin, String fechaFin) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "FlujoClasAdmArm" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        int mes = 0;
        String anio = "";
        String dia, meses;
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        mes = Integer.parseInt(fechaFin.substring(3, 5));
        anio = fechaFin.substring(6, 10);
        dia = fechaFin.substring(0, 2);
        meses = Util.NOMBRE_MESES_MX[mes - 1].toLowerCase();
        meses = Util.firstUpper(meses);
        String periodo = "Del 01 de Enero al " + dia + " de " + meses + " " + anio;
        Row rwEnc2 = (sheet0.getRow(4) == null ? sheet0.createRow(4) : sheet0.getRow(4));
        Cell cell2 = (rwEnc2.getCell(0) == null ? rwEnc2.createCell(0) : rwEnc2.getCell(0));
        cell2.setCellValue(periodo);
        ResultSetMetaData rsMetadata = rs.getMetaData();
        int renglonInicio = 7;
        int cnt1 = 0;
        //generaEstilo de la celda
        CellStyle estiloMoney = Util.generaEstilo(workbook, 8, false, true, true, true, true);
        while (rs.next()) {
            Row rw = (sheet0.getRow(renglonInicio + cnt1) == null ? sheet0.createRow(renglonInicio + cnt1) : sheet0.getRow(renglonInicio + cnt1));
            for (int i = 1; i <= 6; i++) {
                if (i == 1) {
                    Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i), rsMetadata.getColumnType(i), estiloMoney);
                    i++;
                } else {
                    Util.createExcelCellRep(i - 1, rw, rs, rsMetadata.getColumnName(i), rsMetadata.getColumnType(i), estiloMoney);
                }
            }
            cnt1++;
        }
        sheet0 = Util.EvaluaFormula(workbook, sheet0, 10, 11, 1, 7);
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

    public static String flujoEconManager(Connection conn, int mesFin, Map<String, String> plantillas, String fechaFin) throws Exception {
        CallableStatement cs = null;
        ResultSet rs = null;
        String query = "{call sp_flujoEcon( ? )}";
        String fileName = "";
        try {
            cs = conn.prepareCall(query);
            cs.setInt(1, mesFin);
            rs = cs.executeQuery();
            fileName = generaflujoEconManager(rs, plantillas.get("Flujoecon"), mesFin, fechaFin);
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(cs, false);
        }
    }

    private static String generaflujoEconManager(ResultSet rs, String plantillaPath, int mesFin, String fechaFin) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "FLUJO_ECON" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        int mes = 0;
        String anio = "";
        String dia, meses;
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        int cnt1 = 0;
        mes = Integer.parseInt(fechaFin.substring(3, 5));
        anio = fechaFin.substring(6, 10);
        dia = fechaFin.substring(0, 2);
        meses = Util.NOMBRE_MESES_MX[mes - 1].toLowerCase();
        meses = Util.firstUpper(meses);
        String periodo = "Del 01 de Enero al " + dia + " de " + meses + " " + anio;
        Row rwEnc2 = (sheet0.getRow(3) == null ? sheet0.createRow(3) : sheet0.getRow(3));
        Cell cell2 = (rwEnc2.getCell(0) == null ? rwEnc2.createCell(0) : rwEnc2.getCell(0));
        cell2.setCellValue(periodo);
        ResultSetMetaData rsMetadata = rs.getMetaData();
        int renglonInicio = 8;
        //generaEstilo de la celda
        CellStyle estiloMoney = Util.generaEstilo(workbook, 7, false, true, true, false, true);
        estiloMoney.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        estiloMoney.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        while (rs.next()) {
            Row rw = (sheet0.getRow(renglonInicio + cnt1) == null ? sheet0.createRow(renglonInicio + cnt1) : sheet0.getRow(renglonInicio + cnt1));
            for (int i = 1; i <= 6; i++) {
                Util.createExcelCellRep(i + 2, rw, rs, rsMetadata.getColumnName(i), rsMetadata.getColumnType(i), estiloMoney);
            }
            cnt1++;
        }
        sheet0 = Util.EvaluaFormula(workbook, sheet0, 13, 14, 3, 9);
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

    public static String flujoFuncManager(Connection conn, int mesFin, Map<String, String> plantillas, String fechaFin) throws Exception {
        CallableStatement cs = null;
        ResultSet rs = null;
        String query = "{call sp_FlujoFunc( ? )}";
        String fileName = "";
        try {
            cs = conn.prepareCall(query);
            cs.setInt(1, mesFin);
            rs = cs.executeQuery();
            fileName = generaflujoFuncManager(rs, plantillas.get("FLUJOFUNC"), mesFin, fechaFin);
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(cs, false);
        }
    }

    private static String generaflujoFuncManager(ResultSet rs, String plantillaPath, int mesFin, String fechaFin) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "Flujo_funcion" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        int mes = 0;
        String dia, meses, anio;
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        int cnt1 = 0;
        mes = Integer.parseInt(fechaFin.substring(3, 5));
        anio = fechaFin.substring(6, 10);
        dia = fechaFin.substring(0, 2);
        meses = Util.NOMBRE_MESES_MX[mes - 1].toLowerCase();
        meses = Util.firstUpper(meses);
        String periodo = "Del 01 de Enero al " + dia + " de " + meses + " " + anio;
        Row rwEnc2 = (sheet0.getRow(4) == null ? sheet0.createRow(4) : sheet0.getRow(4));
        Cell cell2 = (rwEnc2.getCell(1) == null ? rwEnc2.createCell(1) : rwEnc2.getCell(1));
        cell2.setCellValue(periodo);
        ResultSetMetaData rsMetadata = rs.getMetaData();
        int renglonInicio = 10;
        //generaEstilo (Workbook wk, int tamLetra, boolean negritas, boolean derecho, boolean izquierdo, boolean abajo, boolean formatoNum)
        CellStyle estiloMoney = Util.generaEstilo(workbook, 7, false, true, true, false, true);
        estiloMoney.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        estiloMoney.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        CellStyle estiloMonedaTitulo = Util.generaEstilo(workbook, 7, true, true, true, false, true);
        estiloMonedaTitulo.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        estiloMonedaTitulo.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        while (rs.next()) {
            Row rw = (sheet0.getRow(renglonInicio + cnt1) == null ? sheet0.createRow(renglonInicio + cnt1) : sheet0.getRow(renglonInicio + cnt1));
            if (// si es titulo lo pone en negritas
            "1".equals(rs.getString(8))) {
                for (int i = 1; i <= 7; i++) {
                    if (i == 1) {
                        Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i), rsMetadata.getColumnType(i), estiloMonedaTitulo);
                    } else {
                        Util.createExcelCellRep(i + 1, rw, rs, rsMetadata.getColumnName(i), rsMetadata.getColumnType(i), estiloMonedaTitulo);
                    }
                }
                cnt1++;
            } else if ("0".equals(rs.getString(8))) {
                for (int i = 1; i <= 7; i++) {
                    if (i == 1) {
                        Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i), rsMetadata.getColumnType(i), estiloMoney);
                    } else {
                        Util.createExcelCellRep(i + 1, rw, rs, rsMetadata.getColumnName(i), rsMetadata.getColumnType(i), estiloMoney);
                    }
                }
                cnt1++;
            }
        }
        sheet0 = Util.EvaluaFormula(workbook, sheet0, 43, 44, 3, 9);
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

    public static String flujoProgramaManager(Connection conn, int mesFin, Map<String, String> plantillas, String fechaFin) throws Exception {
        CallableStatement cs = null;
        ResultSet rs = null;
        String query = "{call sp_flujoPrograma( ? )}";
        String fileName = "";
        try {
            cs = conn.prepareCall(query);
            cs.setInt(1, mesFin);
            rs = cs.executeQuery();
            fileName = generaflujoProgramaManager(rs, plantillas.get("FLUJOPROG"), mesFin, fechaFin);
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(cs, false);
        }
    }

    private static String generaflujoProgramaManager(ResultSet rs, String plantillaPath, int mesFin, String fechaFin) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "FLUJO_PROG" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        int mes = 0;
        String dia, anio, meses;
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        int cnt1 = 0;
        mes = Integer.parseInt(fechaFin.substring(3, 5));
        anio = fechaFin.substring(6, 10);
        dia = fechaFin.substring(0, 2);
        meses = Util.NOMBRE_MESES_MX[mes - 1].toLowerCase();
        meses = Util.firstUpper(meses);
        String periodo = "Del 01 de Enero al " + dia + " de " + meses + " " + anio;
        Row rwEnc2 = (sheet0.getRow(3) == null ? sheet0.createRow(3) : sheet0.getRow(3));
        Cell cell2 = (rwEnc2.getCell(0) == null ? rwEnc2.createCell(0) : rwEnc2.getCell(0));
        cell2.setCellValue(periodo);
        ResultSetMetaData rsMetadata = rs.getMetaData();
        int renglonInicio = 8;
        //generaEstilo (Workbook wk, int tamLetra, boolean negritas, boolean derecho, boolean izquierdo, boolean abajo, boolean formatoNum)
        CellStyle estiloMoney = Util.generaEstilo(workbook, 7, false, true, false, false, true);
        CellStyle esTitulo = Util.generaEstilo(workbook, 7, true, true, false, false, true);
        CellStyle esTitulo2 = Util.generaEstilo(workbook, 7, true, true, false, true, true);
        while (rs.next()) {
            Row rw = (sheet0.getRow(renglonInicio + cnt1) == null ? sheet0.createRow(renglonInicio + cnt1) : sheet0.getRow(renglonInicio + cnt1));
            if ("1".equals(rs.getString(8))) {
                for (int h = 1; h <= 7; h++) {
                    if (h == 1)
                        Util.createExcelCellRep(h + 1, rw, rs, rsMetadata.getColumnName(h), rsMetadata.getColumnType(h), esTitulo);
                    else
                        Util.createExcelCellRep(h + 2, rw, rs, rsMetadata.getColumnName(h), rsMetadata.getColumnType(h), esTitulo);
                }
                cnt1++;
                Cell cell3 = (rw.getCell(3) == null ? rw.createCell(3) : rw.getCell(3));
                cell3.setCellStyle(esTitulo);
            } else if ("0".equals(rs.getString(8))) {
                for (int h = 1; h <= 7; h++) {
                    Util.createExcelCellRep(h + 2, rw, rs, rsMetadata.getColumnName(h), rsMetadata.getColumnType(h), estiloMoney);
                }
                cnt1++;
            } else if ("3".equals(rs.getString(8))) {
                for (int h = 1; h <= 7; h++) {
                    if (h == 1)
                        Util.createExcelCellRep(h - 1, rw, rs, rsMetadata.getColumnName(h), rsMetadata.getColumnType(h), esTitulo);
                    else
                        Util.createExcelCellRep(h + 2, rw, rs, rsMetadata.getColumnName(h), rsMetadata.getColumnType(h), esTitulo);
                }
                cnt1++;
                Cell cell3 = (rw.getCell(3) == null ? rw.createCell(3) : rw.getCell(3));
                cell3.setCellStyle(esTitulo);
            } else if ("2".equals(rs.getString(8))) {
                for (int h = 1; h <= 7; h++) {
                    if (h == 1)
                        Util.createExcelCellRep(h, rw, rs, rsMetadata.getColumnName(h), rsMetadata.getColumnType(h), esTitulo2);
                    else
                        Util.createExcelCellRep(h + 2, rw, rs, rsMetadata.getColumnName(h), rsMetadata.getColumnType(h), esTitulo2);
                }
                cnt1++;
                Cell cell3 = (rw.getCell(3) == null ? rw.createCell(3) : rw.getCell(3));
                cell3.setCellStyle(esTitulo2);
            }
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

    public static String flujoObgtCEManager(Connection conn, int mesFin, Map<String, String> plantillas, String fechaFin) throws Exception {
        CallableStatement cs = null;
        ResultSet rs = null;
        String query = "{call sp_FlujoObgtCE( ? )}";
        String fileName = "";
        try {
            cs = conn.prepareCall(query);
            cs.setInt(1, mesFin);
            rs = cs.executeQuery();
            fileName = generaflujoObgtCEManager(rs, plantillas.get("FLUJOBGTCE"), mesFin, fechaFin);
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(cs, false);
        }
    }

    private static String generaflujoObgtCEManager(ResultSet rs, String plantillaPath, int mesFin, String fechaFin) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "Flujo_ObgtCE" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        int mes = 0;
        String dia, anio, meses;
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        int cnt1 = 0;
        mes = Integer.parseInt(fechaFin.substring(3, 5));
        anio = fechaFin.substring(6, 10);
        dia = fechaFin.substring(0, 2);
        meses = Util.NOMBRE_MESES_MX[mes - 1].toLowerCase();
        meses = Util.firstUpper(meses);
        String periodo = "Del 01 de Enero al " + dia + " de " + meses + " " + anio;
        Row rwEnc2 = (sheet0.getRow(4) == null ? sheet0.createRow(4) : sheet0.getRow(4));
        Cell cell2 = (rwEnc2.getCell(0) == null ? rwEnc2.createCell(0) : rwEnc2.getCell(0));
        cell2.setCellValue(periodo);
        ResultSetMetaData rsMetadata = rs.getMetaData();
        int renglonInicio = 9;
        //generaEstilo (Workbook wk, int tamLetra, boolean negritas, boolean derecho, boolean izquierdo, boolean abajo, boolean formatoNum)
        CellStyle estiloMoney = Util.generaEstilo(workbook, 7, false, true, true, false, true);
        CellStyle estiloMoney2 = Util.generaEstilo(workbook, 7, false, true, true, true, true);
        CellStyle estiloMoneyNeg = Util.generaEstilo(workbook, 7, true, true, true, false, true);
        CellStyle esTitulo = Util.generaEstilo(workbook, 7, true, true, true, false, true);
        CellStyle esTitulo2 = Util.generaEstilo(workbook, 7, false, false, false, false, true);
        CellStyle esTitulo3 = Util.generaEstilo(workbook, 7, false, false, false, true, true);
        while (rs.next()) {
            Row rw = (sheet0.getRow(renglonInicio + cnt1) == null ? sheet0.createRow(renglonInicio + cnt1) : sheet0.getRow(renglonInicio + cnt1));
            if ("2".equals(rs.getString(9))) {
                Util.createExcelCellRep(0, rw, rs, rsMetadata.getColumnName(1), rsMetadata.getColumnType(1), esTitulo2);
                Util.createExcelCellRep(4, rw, rs, rsMetadata.getColumnName(3), rsMetadata.getColumnType(3), estiloMoney);
                for (int i = 5; i <= 8; i++) {
                    Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i), rsMetadata.getColumnType(i), estiloMoney);
                }
                cnt1++;
            } else if ("3".equals(rs.getString(9))) {
                Util.createExcelCellRep(0, rw, rs, rsMetadata.getColumnName(1), rsMetadata.getColumnType(1), esTitulo);
                Util.createExcelCellRep(4, rw, rs, rsMetadata.getColumnName(3), rsMetadata.getColumnType(3), estiloMoneyNeg);
                for (int i = 5; i <= 8; i++) {
                    Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i), rsMetadata.getColumnType(i), esTitulo);
                }
                cnt1++;
            } else if ("1".equals(rs.getString(9))) {
                Util.createExcelCellRep(1, rw, rs, rsMetadata.getColumnName(2), rsMetadata.getColumnType(2), esTitulo2);
                Util.createExcelCellRep(2, rw, rs, rsMetadata.getColumnName(1), rsMetadata.getColumnType(1), esTitulo2);
                Util.createExcelCellRep(4, rw, rs, rsMetadata.getColumnName(3), rsMetadata.getColumnType(3), estiloMoney);
                for (int i = 5; i <= 8; i++) {
                    Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i), rsMetadata.getColumnType(i), estiloMoney);
                }
                cnt1++;
            } else {
                if (rs.isAfterLast()) {
                    Util.createExcelCellRep(2, rw, rs, rsMetadata.getColumnName(2), rsMetadata.getColumnType(2), esTitulo3);
                    Util.createExcelCellRep(3, rw, rs, rsMetadata.getColumnName(1), rsMetadata.getColumnType(1), esTitulo3);
                    Util.createExcelCellRep(4, rw, rs, rsMetadata.getColumnName(3), rsMetadata.getColumnType(3), estiloMoney2);
                    for (int i = 5; i <= 8; i++) {
                        Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i), rsMetadata.getColumnType(i), estiloMoney2);
                    }
                    Cell cell8 = (rw.getCell(0) == null ? rw.createCell(0) : rw.getCell(0));
                    cell8.setCellStyle(esTitulo3);
                    Cell cell9 = (rw.getCell(1) == null ? rw.createCell(1) : rw.getCell(1));
                    cell9.setCellStyle(esTitulo3);
                } else {
                    Util.createExcelCellRep(2, rw, rs, rsMetadata.getColumnName(2), rsMetadata.getColumnType(2), esTitulo2);
                    Util.createExcelCellRep(3, rw, rs, rsMetadata.getColumnName(1), rsMetadata.getColumnType(1), esTitulo2);
                    Util.createExcelCellRep(4, rw, rs, rsMetadata.getColumnName(3), rsMetadata.getColumnType(3), estiloMoney);
                    for (int i = 5; i <= 8; i++) {
                        Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i), rsMetadata.getColumnType(i), estiloMoney);
                    }
                }
                cnt1++;
            }
        }
        Row rw2 = (sheet0.getRow(renglonInicio + cnt1) == null ? sheet0.createRow(renglonInicio + cnt1) : sheet0.getRow(renglonInicio + cnt1));
        Cell cell8 = (rw2.getCell(0) == null ? rw2.createCell(0) : rw2.getCell(0));
        cell8.setCellValue("Fuente: Sistema de registro de la Comisión Nacional Forestal.");
        cell8.setCellStyle(esTitulo2);
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

    public static String flujoCFPEAManager(Connection conn, int mesFin, Map<String, String> plantillas, String fechaFin) throws Exception {
        CallableStatement cs = null;
        ResultSet rs = null;
        String query = "{call sp_flujoCFPEArmonizado( ? )}";
        String fileName = "";
        try {
            cs = conn.prepareCall(query);
            cs.setInt(1, mesFin);
            rs = cs.executeQuery();
            fileName = generaflujoCFPEAManager(rs, plantillas.get("FLUJOCFPEA"), mesFin, fechaFin);
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(cs, false);
        }
    }

    private static String generaflujoCFPEAManager(ResultSet rs, String plantillaPath, int mesFin, String fechaFin) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "FLUJO_CFPEA" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        int mes = 0;
        String dia, anio, meses;
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        int cnt1 = 0;
        mes = Integer.parseInt(fechaFin.substring(3, 5));
        anio = fechaFin.substring(6, 10);
        dia = fechaFin.substring(0, 2);
        meses = Util.NOMBRE_MESES_MX[mes - 1].toLowerCase();
        meses = Util.firstUpper(meses);
        String periodo = "Del 01 de Enero al " + dia + " de " + meses + " " + anio;
        Row rwEnc2 = (sheet0.getRow(3) == null ? sheet0.createRow(3) : sheet0.getRow(3));
        Cell cell2 = (rwEnc2.getCell(0) == null ? rwEnc2.createCell(0) : rwEnc2.getCell(0));
        cell2.setCellValue(periodo);
        ResultSetMetaData rsMetadata = rs.getMetaData();
        int renglonInicio = 8;
        DataFormat mon = workbook.createDataFormat();
        //generaEstilo (Workbook wk, int tamLetra, boolean negritas, boolean derecho, boolean izquierdo, boolean abajo, boolean formatoNum)
        CellStyle estiloMoney = Util.generaEstilo(workbook, 7, false, true, true, false, true);
        CellStyle estiloPorcentaje = Util.generaEstilo3(workbook, 7, false, true, true, false, true);
        CellStyle estiloPorcentaje2 = Util.generaEstilo3(workbook, 7, false, true, true, true, true);
        CellStyle esTitulo = Util.generaEstilo(workbook, 7, false, true, true, false, false);
        CellStyle esTitulo2 = Util.generaEstilo(workbook, 7, false, true, true, true, false);
        while (rs.next()) {
            Row rw = (sheet0.getRow(renglonInicio + cnt1) == null ? sheet0.createRow(renglonInicio + cnt1) : sheet0.getRow(renglonInicio + cnt1));
            for (int j = 1; j < 4; j++) {
                if (!"0".equals(rs.getString(j))) {
                    if (rs.isAfterLast()) {
                        Util.createExcelCellRep(j - 1, rw, rs, rsMetadata.getColumnName(j), rsMetadata.getColumnType(j), esTitulo2);
                    } else {
                        Util.createExcelCellRep(j - 1, rw, rs, rsMetadata.getColumnName(j), rsMetadata.getColumnType(j), esTitulo);
                    }
                } else {
                    Cell cell6 = (rw.getCell(j) == null ? rw.createCell(j) : rw.getCell(j));
                    cell6.setCellStyle(esTitulo);
                }
            }
            if (!"000".equals(rs.getString(4))) {
                if (rs.isAfterLast()) {
                    Util.createExcelCellRep(3, rw, rs, rsMetadata.getColumnName(4), rsMetadata.getColumnType(4), esTitulo2);
                } else {
                    Util.createExcelCellRep(3, rw, rs, rsMetadata.getColumnName(4), rsMetadata.getColumnType(4), esTitulo);
                }
            } else {
                Cell cell7 = (rw.getCell(4) == null ? rw.createCell(4) : rw.getCell(4));
                cell7.setCellStyle(esTitulo);
            }
            if ("Porce".equalsIgnoreCase(rs.getString(5).substring(0, 5))) {
                if (rs.isAfterLast()) {
                    for (int h = 5; h <= 18; h++) {
                        if (h <= 10)
                            Util.createExcelCellRep(h - 1, rw, rs, rsMetadata.getColumnName(h), rsMetadata.getColumnType(h), estiloPorcentaje2);
                        else if (h > 10 && h < 17)
                            Util.createExcelCellRep(h, rw, rs, rsMetadata.getColumnName(h), rsMetadata.getColumnType(h), estiloPorcentaje2);
                        else if (h == 18)
                            Util.createExcelCellRep(h, rw, rs, rsMetadata.getColumnName(h - 1), rsMetadata.getColumnType(h - 1), estiloPorcentaje2);
                    }
                    cnt1++;
                    Cell cell3 = (rw.getCell(10) == null ? rw.createCell(10) : rw.getCell(10));
                    cell3.setCellStyle(estiloPorcentaje2);
                    Cell cell4 = (rw.getCell(17) == null ? rw.createCell(17) : rw.getCell(17));
                    cell4.setCellStyle(estiloPorcentaje2);
                    Row rw2 = (sheet0.getRow(renglonInicio + cnt1) == null ? sheet0.createRow(renglonInicio + cnt1) : sheet0.getRow(renglonInicio + cnt1));
                    Cell cell5 = (rw2.getCell(0) == null ? rw2.createCell(0) : rw2.getCell(0));
                    cell5.setCellValue("Fuente: Sistema de registro de la Comisión Nacional Forestal.");
                } else {
                    for (int h = 5; h <= 18; h++) {
                        if (h <= 10)
                            Util.createExcelCellRep(h - 1, rw, rs, rsMetadata.getColumnName(h), rsMetadata.getColumnType(h), estiloPorcentaje);
                        else if (h > 10 && h < 17)
                            Util.createExcelCellRep(h, rw, rs, rsMetadata.getColumnName(h), rsMetadata.getColumnType(h), estiloPorcentaje);
                        else if (h == 18)
                            Util.createExcelCellRep(h, rw, rs, rsMetadata.getColumnName(h - 1), rsMetadata.getColumnType(h - 1), estiloPorcentaje);
                    }
                    cnt1++;
                }
            } else {
                Util.createExcelCellRep(4, rw, rs, rsMetadata.getColumnName(5), rsMetadata.getColumnType(5), estiloMoney);
                if ("0".equals(rs.getString(18))) {
                    for (int h = 5; h <= 18; h++) {
                        if (h <= 10)
                            Util.createExcelCellRep(h - 1, rw, rs, rsMetadata.getColumnName(h), rsMetadata.getColumnType(h), estiloMoney);
                        else if (h > 10 && h < 16)
                            Util.createExcelCellRep(h, rw, rs, rsMetadata.getColumnName(h), rsMetadata.getColumnType(h), estiloMoney);
                        else if (h == 16)
                            Util.createExcelCellRep(h, rw, rs, rsMetadata.getColumnName(h), rsMetadata.getColumnType(h), estiloPorcentaje);
                        else if (h == 18)
                            Util.createExcelCellRep(h, rw, rs, rsMetadata.getColumnName(h - 1), rsMetadata.getColumnType(h - 1), estiloPorcentaje);
                    }
                } else {
                    for (int k = 6; k <= 18; k++) {
                        Cell cell4 = (rw.getCell(k) == null ? rw.createCell(k) : rw.getCell(k));
                        cell4.setCellStyle(estiloMoney);
                    }
                }
                cnt1++;
            }
            if ("Porcentaje Pag/Modif".equals(rs.getString(5))) {
                if (!rs.isAfterLast()) {
                    for (int i = 1; i <= 18; i++) {
                        Row rw2 = (sheet0.getRow(renglonInicio + cnt1) == null ? sheet0.createRow(renglonInicio + cnt1) : sheet0.getRow(renglonInicio + cnt1));
                        Cell cell5 = (rw2.getCell(i) == null ? rw2.createCell(i) : rw2.getCell(i));
                        cell5.setCellStyle(estiloPorcentaje);
                    }
                    cnt1++;
                }
            }
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

    public static String avancePPI(Connection conn, int mesFin, Map<String, String> plantillas, String fechaFin) throws Exception {
        CallableStatement cs = null;
        ResultSet rs = null;
        String query = "{call sp_avanceFinanciero( ? )}";
        String fileName = "";
        int anio = Integer.parseInt(fechaFin.split("/")[2]);
        try {
            cs = conn.prepareCall(query);
            cs.setInt(1, mesFin);
            rs = cs.executeQuery();
            if (anio <= 2018)
                fileName = generaAvancePPI(rs, plantillas.get("AvanceFin2"), mesFin, fechaFin);
            else
                fileName = generaAvancePPI(rs, plantillas.get("AvanceFinancieroProg"), mesFin, fechaFin);
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(cs, false);
        }
    }

    private static String generaAvancePPI(ResultSet rs, String plantillaPath, int mesFin, String fechaFin) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "Avance_PPI" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        int mes = 0;
        String meses, anio, dia;
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        int cnt1 = 0;
        mes = Integer.parseInt(fechaFin.substring(3, 5));
        anio = fechaFin.substring(6, 10);
        dia = fechaFin.substring(0, 2);
        meses = Util.NOMBRE_MESES_MX[mes - 1].toLowerCase();
        meses = Util.firstUpper(meses);
        String periodo = "Al " + dia + " de " + meses + " " + anio;
        Row rwEnc2 = (sheet0.getRow(3) == null ? sheet0.createRow(3) : sheet0.getRow(3));
        Cell cell2 = (rwEnc2.getCell(1) == null ? rwEnc2.createCell(1) : rwEnc2.getCell(1));
        cell2.setCellValue(periodo);
        ResultSetMetaData rsMetadata = rs.getMetaData();
        int renglonInicio = 8;
        DataFormat mon = workbook.createDataFormat();
        //generaEstilo (Workbook wk, int tamLetra, boolean negritas, boolean derecho, boolean izquierdo, boolean abajo, boolean formatoNum)
        CellStyle estiloMoney = Util.generaEstilo(workbook, 10, false, true, true, true, true);
        estiloMoney.setAlignment(HorizontalAlignment.CENTER);
        estiloMoney.setVerticalAlignment(VerticalAlignment.CENTER);
        CellStyle esTitulo = Util.generaEstilo(workbook, 10, false, true, true, true, false);
        esTitulo.setAlignment(HorizontalAlignment.CENTER);
        esTitulo.setVerticalAlignment(VerticalAlignment.CENTER);
        esTitulo.setWrapText(true);
        esTitulo.setShrinkToFit(true);
        CellStyle esPorcentaje = Util.generaEstilo(workbook, 10, false, true, true, true, false);
        esPorcentaje.setDataFormat(mon.getFormat("#0.0%"));
        esPorcentaje.setAlignment(HorizontalAlignment.CENTER);
        esPorcentaje.setVerticalAlignment(VerticalAlignment.CENTER);
        esPorcentaje.setWrapText(true);
        esPorcentaje.setShrinkToFit(true);
        double formula = 0;
        while (rs.next()) {
            Row rw = (sheet0.getRow(renglonInicio + cnt1) == null ? sheet0.createRow(renglonInicio + cnt1) : sheet0.getRow(renglonInicio + cnt1));
            if (rs.getDouble(10) == 0)
                formula = 0;
            else
                formula = rs.getDouble(11) / rs.getDouble(9);
            Util.createExcelCellRep(1, rw, rs, rsMetadata.getColumnName(1), rsMetadata.getColumnType(1), esTitulo);
            for (int i = 7; i <= 11; i++) {
                if (i == 7)
                    Util.createExcelCellRep(i - 5, rw, rs, rsMetadata.getColumnName(i), rsMetadata.getColumnType(i), esTitulo);
                else
                    Util.createExcelCellRep(i - 5, rw, rs, rsMetadata.getColumnName(i), rsMetadata.getColumnType(i), estiloMoney);
            }
            Cell cell = (rw.getCell(7) == null ? rw.createCell(7) : rw.getCell(7));
            cell.setCellStyle(esPorcentaje);
            cell.setCellValue(formula);
            cnt1++;
        }
        Row rwEnc3 = (sheet0.getRow(renglonInicio + cnt1) == null ? sheet0.createRow(renglonInicio + cnt1) : sheet0.getRow(renglonInicio + cnt1));
        Cell cell3 = (rwEnc3.getCell(1) == null ? rwEnc3.createCell(1) : rwEnc3.getCell(1));
        cell3.setCellValue("Fuente: Sistema de registro de la Comisión Nacional Forestal.");
        //sheet0 = Util.EvaluaFormula(workbook, sheet0, renglonInicio,renglonInicio + cnt1, 7,8);
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

    public static String flujoCFPEManager(Connection conn, int mesFin, Map<String, String> plantillas, String fechaFin) throws Exception {
        CallableStatement cs = null;
        ResultSet rs = null;
        String query = "{call sp_flujoCFPE( ? )}";
        String fileName = "";
        try {
            cs = conn.prepareCall(query);
            cs.setInt(1, mesFin);
            rs = cs.executeQuery();
            fileName = generaflujoCFPEManager(rs, plantillas.get("FLUJOCFPE"), mesFin, fechaFin);
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(cs, false);
        }
    }

    private static String generaflujoCFPEManager(ResultSet rs, String plantillaPath, int mesFin, String fechaFin) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "FLUJO_CFPE" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        int mes = 0;
        String dia, anio, meses;
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        int cnt1 = 0;
        mes = Integer.parseInt(fechaFin.substring(3, 5));
        anio = fechaFin.substring(6, 10);
        dia = fechaFin.substring(0, 2);
        meses = Util.NOMBRE_MESES_MX[mes - 1].toLowerCase();
        meses = Util.firstUpper(meses);
        String periodo = "Del 01 de Enero al " + dia + " de " + meses + " " + anio;
        Row rwEnc2 = (sheet0.getRow(4) == null ? sheet0.createRow(4) : sheet0.getRow(4));
        Cell cell2 = (rwEnc2.getCell(0) == null ? rwEnc2.createCell(0) : rwEnc2.getCell(0));
        cell2.setCellValue(periodo);
        ResultSetMetaData rsMetadata = rs.getMetaData();
        int renglonInicio = 9;
        DataFormat mon = workbook.createDataFormat();
        //generaEstilo (Workbook wk, int tamLetra, boolean negritas, boolean derecho, boolean izquierdo, boolean abajo, boolean formatoNum)
        CellStyle estiloMoney = Util.generaEstilo1(workbook, 7, false, true, true, false, true);
        CellStyle estiloPorcentaje = Util.generaEstilo3(workbook, 7, false, true, true, false, true);
        CellStyle estiloPorcentaje2 = Util.generaEstilo3(workbook, 7, false, true, true, true, true);
        CellStyle esTitulo = Util.generaEstilo1(workbook, 7, false, true, true, false, false);
        CellStyle esTitulo2 = Util.generaEstilo1(workbook, 7, false, true, true, true, false);
        while (rs.next()) {
            Row rw = (sheet0.getRow(renglonInicio + cnt1) == null ? sheet0.createRow(renglonInicio + cnt1) : sheet0.getRow(renglonInicio + cnt1));
            for (int i = 1; i <= 2; i++) {
                if (!"0".equals(rs.getString(i))) {
                    Util.createExcelCellRep(i - 1, rw, rs, rsMetadata.getColumnName(i), rsMetadata.getColumnType(i), esTitulo);
                } else {
                    Cell cell6 = (rw.getCell(i) == null ? rw.createCell(i) : rw.getCell(i));
                    cell6.setCellStyle(esTitulo);
                }
            }
            if (!"00".equals(rs.getString(3))) {
                Util.createExcelCellRep(2, rw, rs, rsMetadata.getColumnName(3), rsMetadata.getColumnType(3), esTitulo);
            } else {
                Cell cell6 = (rw.getCell(3) == null ? rw.createCell(3) : rw.getCell(3));
                cell6.setCellStyle(esTitulo);
            }
            if (!"000".equals(rs.getString(4))) {
                Util.createExcelCellRep(3, rw, rs, rsMetadata.getColumnName(4), rsMetadata.getColumnType(4), esTitulo);
            } else {
                Cell cell6 = (rw.getCell(4) == null ? rw.createCell(4) : rw.getCell(4));
                cell6.setCellStyle(esTitulo);
            }
            if (!"0000".equals(rs.getString(5))) {
                Util.createExcelCellRep(4, rw, rs, rsMetadata.getColumnName(5), rsMetadata.getColumnType(5), esTitulo);
            } else {
                Cell cell6 = (rw.getCell(5) == null ? rw.createCell(5) : rw.getCell(5));
                cell6.setCellStyle(esTitulo);
            }
            if ("Porce".equalsIgnoreCase(rs.getString(7).substring(0, 5))) {
                if (rs.isAfterLast()) {
                    if (!"000".equals(rs.getString(6))) {
                        Util.createExcelCellRep(5, rw, rs, rsMetadata.getColumnName(6), rsMetadata.getColumnType(6), esTitulo2);
                    }
                    for (int j = 7; j <= 19; j++) {
                        if (j <= 12)
                            Util.createExcelCellRep(j - 1, rw, rs, rsMetadata.getColumnName(j), rsMetadata.getColumnType(j), estiloMoney);
                        else if (j > 12 && j <= 18)
                            Util.createExcelCellRep(j, rw, rs, rsMetadata.getColumnName(j), rsMetadata.getColumnType(j), estiloMoney);
                        else if (j > 18)
                            Util.createExcelCellRep(j + 1, rw, rs, rsMetadata.getColumnName(j), rsMetadata.getColumnType(j), estiloMoney);
                    }
                    cnt1++;
                    Cell cell3 = (rw.getCell(12) == null ? rw.createCell(12) : rw.getCell(12));
                    cell3.setCellStyle(estiloPorcentaje2);
                    Cell cell4 = (rw.getCell(19) == null ? rw.createCell(19) : rw.getCell(19));
                    cell4.setCellStyle(estiloPorcentaje2);
                    for (int i = 0; i <= 5; i++) {
                        Cell cell5 = (rw.getCell(i) == null ? rw.createCell(i) : rw.getCell(i));
                        cell5.setCellStyle(esTitulo2);
                    }
                    Row rw2 = (sheet0.getRow(renglonInicio + cnt1) == null ? sheet0.createRow(renglonInicio + cnt1) : sheet0.getRow(renglonInicio + cnt1));
                    Cell cell5 = (rw2.getCell(0) == null ? rw2.createCell(0) : rw2.getCell(0));
                    cell5.setCellValue("Fuente: Sistema de registro de la Comisión Nacional Forestal.");
                } else {
                    if (!"000".equals(rs.getString(6))) {
                        Util.createExcelCellRep(5, rw, rs, rsMetadata.getColumnName(6), rsMetadata.getColumnType(6), esTitulo);
                    }
                    for (int j = 7; j <= 19; j++) {
                        if (j <= 12)
                            Util.createExcelCellRep(j - 1, rw, rs, rsMetadata.getColumnName(j), rsMetadata.getColumnType(j), estiloMoney);
                        else if (j > 12 && j <= 18)
                            Util.createExcelCellRep(j, rw, rs, rsMetadata.getColumnName(j), rsMetadata.getColumnType(j), estiloMoney);
                        else if (j > 18)
                            Util.createExcelCellRep(j + 1, rw, rs, rsMetadata.getColumnName(j), rsMetadata.getColumnType(j), estiloMoney);
                    }
                    cnt1++;
                }
            } else {
                if (!"000".equals(rs.getString(6))) {
                    Util.createExcelCellRep(5, rw, rs, rsMetadata.getColumnName(6), rsMetadata.getColumnType(6), esTitulo);
                }
                for (int j = 7; j <= 19; j++) {
                    if (j <= 12)
                        Util.createExcelCellRep(j - 1, rw, rs, rsMetadata.getColumnName(j), rsMetadata.getColumnType(j), estiloMoney);
                    else if (j > 12 && j < 18)
                        Util.createExcelCellRep(j, rw, rs, rsMetadata.getColumnName(j), rsMetadata.getColumnType(j), estiloMoney);
                    else if (j == 18)
                        Util.createExcelCellRep(j, rw, rs, rsMetadata.getColumnName(j), rsMetadata.getColumnType(j), estiloMoney);
                    else if (j > 18)
                        Util.createExcelCellRep(j + 1, rw, rs, rsMetadata.getColumnName(j), rsMetadata.getColumnType(j), estiloMoney);
                }
                cnt1++;
            }
            if ("Porcentaje Pag/Modif".equals(rs.getString(7))) {
                if (!rs.isAfterLast()) {
                    for (int i = 1; i <= 20; i++) {
                        Row rw2 = (sheet0.getRow(renglonInicio + cnt1) == null ? sheet0.createRow(renglonInicio + cnt1) : sheet0.getRow(renglonInicio + cnt1));
                        Cell cell5 = (rw2.getCell(i) == null ? rw2.createCell(i) : rw2.getCell(i));
                        cell5.setCellStyle(estiloPorcentaje);
                    }
                    cnt1++;
                }
            }
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

    public static String flujoEFEManager(Connection conn, int mesFin, Map<String, String> plantillas, String fechaFin) throws Exception {
        CallableStatement cs = null;
        ResultSet rs = null;
        String query = "{call sp_flujoEfectivoEgresos( ? )}";
        String fileName = "";
        try {
            cs = conn.prepareCall(query);
            cs.setInt(1, mesFin);
            rs = cs.executeQuery();
            fileName = generaflujoEFEManager(rs, plantillas.get("FLUJOEFE"), mesFin, fechaFin);
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(cs, false);
        }
    }

    private static String generaflujoEFEManager(ResultSet rs, String plantillaPath, int mesFin, String fechaFin) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "Flujo_EFE" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        int mes = 0;
        String dia, anio, meses;
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        int cnt1 = 0;
        mes = Integer.parseInt(fechaFin.substring(3, 5));
        anio = fechaFin.substring(6, 10);
        dia = fechaFin.substring(0, 2);
        meses = Util.NOMBRE_MESES_MX[mes - 1].toLowerCase();
        meses = Util.firstUpper(meses);
        String periodo = "Del 01 de Enero al " + dia + " de " + meses + " " + anio;
        Row rwEnc2 = (sheet0.getRow(5) == null ? sheet0.createRow(5) : sheet0.getRow(5));
        Cell cell2 = (rwEnc2.getCell(1) == null ? rwEnc2.createCell(1) : rwEnc2.getCell(1));
        cell2.setCellValue(periodo);
        ResultSetMetaData rsMetadata = rs.getMetaData();
        int renglonInicio = 8;
        //generaEstilo (Workbook wk, int tamLetra, boolean negritas, boolean derecho, boolean izquierdo, boolean abajo, boolean formatoNum)
        CellStyle estiloMoney = Util.generaEstilo(workbook, 7, false, true, false, false, true);
        estiloMoney.setFillBackgroundColor(IndexedColors.WHITE.getIndex());
        CellStyle esTitulo = Util.generaEstilo(workbook, 7, false, true, false, false, true);
        esTitulo.setFillBackgroundColor(IndexedColors.WHITE.getIndex());
        while (rs.next()) {
            Row rw = (sheet0.getRow(renglonInicio + cnt1) == null ? sheet0.createRow(renglonInicio + cnt1) : sheet0.getRow(renglonInicio + cnt1));
            if ("0".equals(rs.getString(6))) {
                for (int h = 2; h <= 4; h++) {
                    Util.createExcelCellRep(h + 3, rw, rs, rsMetadata.getColumnName(h), rsMetadata.getColumnType(h), estiloMoney);
                }
                cnt1++;
            } else {
                for (int h = 2; h <= 4; h++) {
                    Util.createExcelCellRep(h + 3, rw, rs, rsMetadata.getColumnName(h), rsMetadata.getColumnType(h), esTitulo);
                }
                cnt1++;
            }
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

    public static String flujoIFEManager(Connection conn, int mesFin, Map<String, String> plantillas, String fechaFin) throws Exception {
        CallableStatement cs = null;
        ResultSet rs = null;
        String query = "{call sp_flujoEfectivoIngresos( ? )}";
        String fileName = "";
        try {
            cs = conn.prepareCall(query);
            cs.setInt(1, mesFin);
            rs = cs.executeQuery();
            fileName = generaflujoIFEManager(rs, plantillas.get("FLUJOIFE"), mesFin, fechaFin);
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(cs, false);
        }
    }

    private static String generaflujoIFEManager(ResultSet rs, String plantillaPath, int mesFin, String fechaFin) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "Flujo_IFE" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        int mes = 0;
        String dia, anio, meses;
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        mes = Integer.parseInt(fechaFin.substring(3, 5));
        anio = fechaFin.substring(6, 10);
        dia = fechaFin.substring(0, 2);
        meses = Util.NOMBRE_MESES_MX[mes - 1].toLowerCase();
        meses = Util.firstUpper(meses);
        String periodo = "Del 01 de Enero al " + dia + " de " + meses + " " + anio;
        Row rwEnc2 = (sheet0.getRow(5) == null ? sheet0.createRow(5) : sheet0.getRow(5));
        Cell cell2 = (rwEnc2.getCell(0) == null ? rwEnc2.createCell(0) : rwEnc2.getCell(0));
        cell2.setCellValue(periodo);
        ResultSetMetaData rsMetadata = rs.getMetaData();
        //generaEstilo (Workbook wk, int tamLetra, boolean negritas, boolean derecho, boolean izquierdo, boolean abajo, boolean formatoNum)
        CellStyle estiloMoney = Util.generaEstilo(workbook, 7, false, true, false, false, true);
        int cnt1 = 9;
        while (rs.next()) {
            Row rw = (sheet0.getRow(cnt1) == null ? sheet0.createRow(cnt1) : sheet0.getRow(cnt1));
            for (int h = 2; h <= 4; h++) {
                Util.createExcelCellRep(h + 3, rw, rs, rsMetadata.getColumnName(h), rsMetadata.getColumnType(h), estiloMoney);
            }
            switch(rs.getString(1)) {
                case "02":
                    cnt1 = cnt1 + 10;
                    break;
                case "03":
                    cnt1++;
                    break;
                case "04":
                    cnt1 = cnt1 + 9;
                    break;
                case "10":
                    cnt1++;
                    break;
                case "11":
                    cnt1 = cnt1 + 3;
                    break;
                case "12":
                    cnt1++;
                    break;
                case "13":
                    cnt1++;
                    break;
                case "14":
                    cnt1++;
                    break;
            }
        }
        Util.EvaluaFormula(workbook, sheet0, 8, 40, 5, 8);
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

    public static String analiticoManager(Connection conn, int mesFin, Map<String, String> plantillas, String fechaFin) throws Exception {
        CallableStatement cs = null;
        ResultSet rs = null;
        String query = "{call sp_reporteAnaliticoIng( ? )}";
        String fileName = "";
        try {
            cs = conn.prepareCall(query);
            cs.setInt(1, mesFin);
            rs = cs.executeQuery();
            fileName = generaAnaliticoManager(rs, plantillas.get("ANALITING"), mesFin, fechaFin);
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(cs, false);
        }
    }

    private static String generaAnaliticoManager(ResultSet rs, String plantillaPath, int mesFin, String fechaFin) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "analiticoIng" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        int mes = 0;
        String dia, anio, meses;
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        mes = Integer.parseInt(fechaFin.substring(3, 5));
        anio = fechaFin.substring(6, 10);
        dia = fechaFin.substring(0, 2);
        meses = Util.NOMBRE_MESES_MX[mes - 1].toLowerCase();
        meses = Util.firstUpper(meses);
        String periodo = "Del 01 de Enero al " + dia + " de " + meses + " " + anio;
        Row rwEnc2 = (sheet0.getRow(3) == null ? sheet0.createRow(3) : sheet0.getRow(3));
        Cell cell2 = (rwEnc2.getCell(1) == null ? rwEnc2.createCell(1) : rwEnc2.getCell(1));
        cell2.setCellValue(periodo);
        ResultSetMetaData rsMetadata = rs.getMetaData();
        //generaEstilo (Workbook wk, int tamLetra, boolean negritas, boolean derecho, boolean izquierdo, boolean abajo, boolean formatoNum)
        CellStyle estiloMoney = Util.generaEstilo(workbook, 7, false, true, false, false, true);
        int cnt1 = 16;
        int cnt2 = 33;
        while (rs.next()) {
            Row rw = (sheet0.getRow(cnt1) == null ? sheet0.createRow(cnt1) : sheet0.getRow(cnt1));
            Row rw2 = (sheet0.getRow(cnt2) == null ? sheet0.createRow(cnt2) : sheet0.getRow(cnt2));
            for (int h = 2; h <= 7; h++) {
                Util.createExcelCellRep(h, rw, rs, rsMetadata.getColumnName(h), rsMetadata.getColumnType(h), estiloMoney);
                Util.createExcelCellRep(h, rw2, rs, rsMetadata.getColumnName(h), rsMetadata.getColumnType(h), estiloMoney);
            }
            switch(rs.getString(1)) {
                case "1":
                    cnt1 = cnt1 - 2;
                    cnt2 = cnt2 + 4;
                    break;
                case "4":
                    cnt1 = cnt1 - 2;
                    break;
            }
        }
        Util.EvaluaFormula(workbook, sheet0, 18, 43, 2, 8);
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

    public static String analiticoManager2(Connection conn, int mesFin, Map<String, String> plantillas, String fechaFin) throws Exception {
        CallableStatement cs = null;
        ResultSet rs = null;
        String query = "{call sp_reporteAnaliticoIng( ? )}";
        String fileName = "";
        try {
            cs = conn.prepareCall(query);
            cs.setInt(1, mesFin);
            rs = cs.executeQuery();
            fileName = generaAnaliticoManager2(rs, plantillas.get("ANALITING2"), mesFin, fechaFin);
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(cs, false);
        }
    }

    private static String generaAnaliticoManager2(ResultSet rs, String plantillaPath, int mesFin, String fechaFin) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "analiticoIng" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        int mes = 0;
        String dia, anio, meses;
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        mes = Integer.parseInt(fechaFin.substring(3, 5));
        anio = fechaFin.substring(6, 10);
        dia = fechaFin.substring(0, 2);
        meses = Util.NOMBRE_MESES_MX[mes - 1].toLowerCase();
        meses = Util.firstUpper(meses);
        String periodo = "Del 01 de Enero al " + dia + " de " + meses + " " + anio;
        Row rwEnc2 = (sheet0.getRow(3) == null ? sheet0.createRow(3) : sheet0.getRow(3));
        Cell cell2 = (rwEnc2.getCell(1) == null ? rwEnc2.createCell(1) : rwEnc2.getCell(1));
        cell2.setCellValue(periodo);
        ResultSetMetaData rsMetadata = rs.getMetaData();
        //generaEstilo (Workbook wk, int tamLetra, boolean negritas, boolean derecho, boolean izquierdo, boolean abajo, boolean formatoNum)
        CellStyle estiloMoney = Util.generaEstilo(workbook, 7, false, true, false, false, true);
        int cnt1 = 20;
        int cnt2 = 41;
        while (rs.next()) {
            Row rw = (sheet0.getRow(cnt1) == null ? sheet0.createRow(cnt1) : sheet0.getRow(cnt1));
            Row rw2 = (sheet0.getRow(cnt2) == null ? sheet0.createRow(cnt2) : sheet0.getRow(cnt2));
            for (int h = 2; h <= 7; h++) {
                Util.createExcelCellRep(h, rw, rs, rsMetadata.getColumnName(h), rsMetadata.getColumnType(h), estiloMoney);
                Util.createExcelCellRep(h, rw2, rs, rsMetadata.getColumnName(h), rsMetadata.getColumnType(h), estiloMoney);
            }
            switch(rs.getString(1)) {
                case "1":
                    cnt1 = cnt1 - 2;
                    cnt2 = cnt2 + 4;
                    break;
                case "4":
                    cnt1 = cnt1 - 2;
                    break;
            }
        }
        Util.EvaluaFormula(workbook, sheet0, 22, 50, 2, 8);
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
}
