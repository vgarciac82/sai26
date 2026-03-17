package com.syc.reportes.core;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.Map;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import java.util.Base64;
import java.nio.file.Paths;

public class ReporteSIIWEBManager {

    public static String generaReporteSIIWEBManager(Connection conn, Map<String, String> plantillas) throws Exception {
        Statement pstmt = null;
        ResultSet rs = null;
        String query = "SELECT * FROM tSIIWEBCuentasVista";
        System.out.print(query);
        String fileName = "";
        try {
            pstmt = conn.createStatement();
            rs = pstmt.executeQuery(query);
            fileName = generaReporteCtasSIIWEB(rs, plantillas.get("REPCTASIIWEB"));
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(pstmt, false);
        }
    }

    private static String generaReporteCtasSIIWEB(ResultSet rs, String plantillaPath) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "ReporteMovimientos" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        int renglonInicio = 7;
        ResultSetMetaData rsMetadata = rs.getMetaData();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 9);
        font.setFontName("Arial");
        CellStyle estiloTabla = workbook.createCellStyle();
        estiloTabla.setBorderRight(BorderStyle.HAIR);
        estiloTabla.setBorderLeft(BorderStyle.HAIR);
        estiloTabla.setBorderTop(BorderStyle.HAIR);
        estiloTabla.setFont(font);
        estiloTabla.setBorderBottom(BorderStyle.HAIR);
        while (rs.next()) {
            Row rw = (sheet0.getRow(renglonInicio) == null ? sheet0.createRow(renglonInicio) : sheet0.getRow(renglonInicio));
            Util.createExcelCellRep(1, rw, rs, rsMetadata.getColumnName(1), rsMetadata.getColumnType(1), estiloTabla);
            Util.createExcelCellRep(2, rw, rs, rsMetadata.getColumnName(2), rsMetadata.getColumnType(2), estiloTabla);
            Util.createExcelCellRep(3, rw, rs, rsMetadata.getColumnName(3), rsMetadata.getColumnType(3), estiloTabla);
            Util.createExcelCellRep(4, rw, rs, rsMetadata.getColumnName(4), rsMetadata.getColumnType(4), estiloTabla);
            Util.createExcelCellRep(5, rw, rs, rsMetadata.getColumnName(5), rsMetadata.getColumnType(5), estiloTabla);
            Util.createExcelCellRep(6, rw, rs, rsMetadata.getColumnName(6), rsMetadata.getColumnType(6), estiloTabla);
            Util.createExcelCellRep(7, rw, rs, rsMetadata.getColumnName(7), rsMetadata.getColumnType(7), estiloTabla);
            Util.createExcelCellRep(8, rw, rs, rsMetadata.getColumnName(8), rsMetadata.getColumnType(8), estiloTabla);
            Util.createExcelCellRep(9, rw, rs, rsMetadata.getColumnName(9), rsMetadata.getColumnType(9), estiloTabla);
            Util.createExcelCellRep(10, rw, rs, rsMetadata.getColumnName(10), rsMetadata.getColumnType(10), estiloTabla);
            Util.createExcelCellRep(11, rw, rs, rsMetadata.getColumnName(11), rsMetadata.getColumnType(11), estiloTabla);
            Util.createExcelCellRep(12, rw, rs, rsMetadata.getColumnName(12), rsMetadata.getColumnType(12), estiloTabla);
            Util.createExcelCellRep(13, rw, rs, rsMetadata.getColumnName(13), rsMetadata.getColumnType(13), estiloTabla);
            Util.createExcelCellRep(14, rw, rs, rsMetadata.getColumnName(14), rsMetadata.getColumnType(14), estiloTabla);
            Util.createExcelCellRep(15, rw, rs, rsMetadata.getColumnName(15), rsMetadata.getColumnType(15), estiloTabla);
            Util.createExcelCellRep(16, rw, rs, rsMetadata.getColumnName(16), rsMetadata.getColumnType(16), estiloTabla);
            Util.createExcelCellRep(17, rw, rs, rsMetadata.getColumnName(17), rsMetadata.getColumnType(17), estiloTabla);
            Util.createExcelCellRep(18, rw, rs, rsMetadata.getColumnName(18), rsMetadata.getColumnType(18), estiloTabla);
            Util.createExcelCellRep(19, rw, rs, rsMetadata.getColumnName(19), rsMetadata.getColumnType(19), estiloTabla);
            renglonInicio++;
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
}
