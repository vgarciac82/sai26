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
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import java.util.Base64;
import java.nio.file.Paths;

@SuppressWarnings("unused")
public class Reporte005AManager {

    public static String Reporte005Manager(Connection conn, String trimestre, String anio, Map<String, String> plantillas) throws Exception {
        CallableStatement cs = null;
        ResultSet rs = null;
        int mes = Integer.parseInt(trimestre);
        int EjercicioFiscal = Integer.parseInt(anio);
        String query = "{CALL dbo.sp_Formato_005A ( ? )}";
        String fileName = "";
        try {
            cs = conn.prepareCall(query);
            cs.setInt(1, mes);
            rs = cs.executeQuery();
            if (EjercicioFiscal >= 2020)
                fileName = generaReporte(rs, plantillas.get("Formato005A_2020"), mes, EjercicioFiscal);
            else
                fileName = generaReporte(rs, plantillas.get("Formato005A"), mes, EjercicioFiscal);
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(cs, false);
        }
    }

    private static String generaReporte(ResultSet rs, String plantillaPath, int mesIni, int EjercicioFiscal) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "Formato_005A" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        ResultSetMetaData rsMetadata = rs.getMetaData();
        int renglonInicio = 5;
        int UC = 7;
        int UE = 10;
        int UI = 13;
        int UP = 16;
        int UA = 21;
        int total_columnas = -1;
        if (EjercicioFiscal >= 2020)
            total_columnas = 17;
        else
            total_columnas = 18;
        String Encabezado = null;
        String MesTrabajo = null;
        if (mesIni == 3) {
            Encabezado = "Enero - Marzo de " + EjercicioFiscal;
            MesTrabajo = "Enero - Marzo";
        } else if (mesIni == 6) {
            Encabezado = "Abril - Junio de " + EjercicioFiscal;
            MesTrabajo = "Abril - Junio";
        } else if (mesIni == 9) {
            Encabezado = "Julio - Septiembre de " + EjercicioFiscal;
            MesTrabajo = "Julio - Septiembre";
        } else if (mesIni == 12) {
            Encabezado = "Octubre - Diciembre de " + EjercicioFiscal;
            MesTrabajo = "Octubre - Diciembre";
        }
        Row rwEnc1 = (sheet0.getRow(1) == null ? sheet0.createRow(1) : sheet0.getRow(1));
        Cell cell1 = (rwEnc1.getCell(1) == null ? rwEnc1.createCell(1) : rwEnc1.getCell(1));
        cell1.setCellValue(Encabezado);
        CellStyle estiloTabla = workbook.createCellStyle();
        estiloTabla.setBorderRight(BorderStyle.THIN);
        estiloTabla.setBorderLeft(BorderStyle.THIN);
        estiloTabla.setBorderTop(BorderStyle.THIN);
        estiloTabla.setBorderBottom(BorderStyle.THIN);
        CellStyle estilodecimal = workbook.createCellStyle();
        HSSFDataFormat d = (HSSFDataFormat) workbook.createDataFormat();
        estilodecimal.setBorderRight(BorderStyle.THIN);
        estilodecimal.setBorderLeft(BorderStyle.THIN);
        estilodecimal.setBorderTop(BorderStyle.THIN);
        estilodecimal.setBorderBottom(BorderStyle.THIN);
        estilodecimal.setDataFormat(d.getFormat("#,##0.00"));
        CellStyle estiloporcentaje = workbook.createCellStyle();
        HSSFDataFormat p = (HSSFDataFormat) workbook.createDataFormat();
        estiloporcentaje.setBorderRight(BorderStyle.THIN);
        estiloporcentaje.setBorderLeft(BorderStyle.THIN);
        estiloporcentaje.setBorderTop(BorderStyle.THIN);
        estiloporcentaje.setBorderBottom(BorderStyle.THIN);
        estiloporcentaje.setDataFormat(p.getFormat("##0.00%"));
        while (rs.next()) {
            String Unidad = rs.getString("Unidad_Normativa").substring(0, 1);
            if ("C".equals(Unidad)) {
                int rows = UC;
                sheet0.shiftRows(rows, UA + 5, 1);
                Row rw = (sheet0.getRow(UC) == null ? sheet0.createRow(UC) : sheet0.getRow(UC));
                for (int i = 0; i < total_columnas; i++) {
                    int tipo = rsMetadata.getColumnType(i + 1);
                    if (EjercicioFiscal >= 2020) {
                        if (tipo == 3 && (i == 6 || i == 8 || i == 10 || i == 12 || i == 14 || i == 16)) {
                            Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloporcentaje);
                        } else if (tipo == 3) {
                            Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estilodecimal);
                        } else
                            Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
                    } else {
                        if (tipo == 3 && (i == 7 || i == 9 || i == 11 || i == 13 || i == 15 || i == 17)) {
                            Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloporcentaje);
                        } else if (tipo == 3) {
                            Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estilodecimal);
                        } else
                            Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
                    }
                }
                UC++;
                UE++;
                UI++;
                UP++;
                UA++;
            } else if ("E".equals(Unidad)) {
                int rows = UE;
                sheet0.shiftRows(rows, UA + 5, 1);
                Row rw = (sheet0.getRow(UE) == null ? sheet0.createRow(UE) : sheet0.getRow(UE));
                for (int i = 0; i < total_columnas; i++) {
                    int tipo = rsMetadata.getColumnType(i + 1);
                    if (EjercicioFiscal >= 2020) {
                        if (tipo == 3 && (i == 6 || i == 8 || i == 10 || i == 12 || i == 14 || i == 16)) {
                            Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloporcentaje);
                        } else if (tipo == 3) {
                            Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estilodecimal);
                        } else
                            Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
                    } else {
                        if (tipo == 3 && (i == 7 || i == 9 || i == 11 || i == 13 || i == 15 || i == 17)) {
                            Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloporcentaje);
                        } else if (tipo == 3) {
                            Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estilodecimal);
                        } else
                            Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
                    }
                }
                UE++;
                UI++;
                UP++;
                UA++;
            } else if ("I".equals(Unidad)) {
                int rows = UI;
                sheet0.shiftRows(rows, UA + 5, 1);
                Row rw = (sheet0.getRow(UI) == null ? sheet0.createRow(UI) : sheet0.getRow(UI));
                for (int i = 0; i < total_columnas; i++) {
                    int tipo = rsMetadata.getColumnType(i + 1);
                    if (EjercicioFiscal >= 2020) {
                        if (tipo == 3 && (i == 6 || i == 8 || i == 10 || i == 12 || i == 14 || i == 16)) {
                            Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloporcentaje);
                        } else if (tipo == 3) {
                            Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estilodecimal);
                        } else
                            Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
                    } else {
                        if (tipo == 3 && (i == 7 || i == 9 || i == 11 || i == 13 || i == 15 || i == 17)) {
                            Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloporcentaje);
                        } else if (tipo == 3) {
                            Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estilodecimal);
                        } else
                            Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
                    }
                }
                UI++;
                UP++;
                UA++;
            } else if ("P".equals(Unidad)) {
                int rows = UP;
                sheet0.shiftRows(rows, UA + 5, 1);
                Row rw = (sheet0.getRow(UP) == null ? sheet0.createRow(UP) : sheet0.getRow(UP));
                for (int i = 0; i < total_columnas; i++) {
                    int tipo = rsMetadata.getColumnType(i + 1);
                    if (EjercicioFiscal >= 2020) {
                        if (tipo == 3 && (i == 6 || i == 8 || i == 10 || i == 12 || i == 14 || i == 16)) {
                            Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloporcentaje);
                        } else if (tipo == 3) {
                            Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estilodecimal);
                        } else
                            Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
                    } else {
                        if (tipo == 3 && (i == 7 || i == 9 || i == 11 || i == 13 || i == 15 || i == 17)) {
                            Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloporcentaje);
                        } else if (tipo == 3) {
                            Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estilodecimal);
                        } else
                            Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
                    }
                }
                UP++;
                UA++;
            } else if ("A".equals(Unidad)) {
                int rows = UA;
                sheet0.shiftRows(rows, UA + 5, 1);
                Row rw = (sheet0.getRow(UA) == null ? sheet0.createRow(UA) : sheet0.getRow(UA));
                for (int i = 0; i < total_columnas; i++) {
                    int tipo = rsMetadata.getColumnType(i + 1);
                    if (EjercicioFiscal >= 2020) {
                        if (tipo == 3 && (i == 6 || i == 8 || i == 10 || i == 12 || i == 14 || i == 16)) {
                            Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloporcentaje);
                        } else if (tipo == 3) {
                            Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estilodecimal);
                        } else
                            Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
                    } else {
                        if (tipo == 3 && (i == 7 || i == 9 || i == 11 || i == 13 || i == 15 || i == 17)) {
                            Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloporcentaje);
                        } else if (tipo == 3) {
                            Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estilodecimal);
                        } else
                            Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
                    }
                }
                UA++;
            }
        }
        sheet0 = Util.EvaluaFormula(workbook, sheet0, 6, UA + 5, 3, total_columnas);
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
