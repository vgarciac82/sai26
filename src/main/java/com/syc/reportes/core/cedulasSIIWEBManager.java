package com.syc.reportes.core;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
//import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Map;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import java.util.Base64;
import java.nio.file.Paths;

public class cedulasSIIWEBManager {

    public static String generacedulasSIIWEB(Connection conn, String folio, String mes, String cedula, Map<String, String> Plantilla) throws Exception {
        CallableStatement cs = null;
        ResultSet rs = null;
        String fileName = "";
        String query = "";
        if (("6").equals(cedula)) {
            query = "{call sp_l_generaCedulaE06Detalle(?,?)}";
        } else {
            query = "{call sp_l_generaCedulaE02Detalle(?)}";
        }
        try {
            if (("6").equals(cedula)) {
                cs = conn.prepareCall(query);
                cs.setString(1, folio);
                cs.setString(2, mes);
            } else {
                cs = conn.prepareCall(query);
                cs.setString(1, folio);
            }
            rs = cs.executeQuery();
            if (("6").equals(cedula)) {
                fileName = generaCedulaE06(conn, rs, Plantilla.get("CEDE06"), mes, folio);
            } else {
                fileName = generaCedulaE02(conn, rs, Plantilla.get("CEDE02"), mes, folio);
            }
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(cs, false);
        }
    }

    public static String generaCedulaE06(Connection conn, ResultSet rs, String plantillaPath, String mes, String folio) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "ReporteCedulaE06" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        int cnt = 8;
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
        Row rwEnc1 = (sheet0.getRow(5) == null ? sheet0.createRow(5) : sheet0.getRow(5));
        Cell cell1 = (rwEnc1.getCell(1) == null ? rwEnc1.createCell(1) : rwEnc1.getCell(1));
        cell1.setCellValue(mes);
        while (rs.next()) {
            Row rw = (sheet0.getRow(cnt) == null ? sheet0.createRow(cnt) : sheet0.getRow(cnt));
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
            Util.createExcelCellRep(15, rw, rs, rsMetadata.getColumnName(15), rsMetadata.getColumnType(15), estiloMoneda);
            Util.createExcelCellRep(16, rw, rs, rsMetadata.getColumnName(16), rsMetadata.getColumnType(16), estiloMoneda);
            Util.createExcelCellRep(17, rw, rs, rsMetadata.getColumnName(17), rsMetadata.getColumnType(17), estiloMoneda);
            Util.createExcelCellRep(18, rw, rs, rsMetadata.getColumnName(18), rsMetadata.getColumnType(18), estiloMoneda);
            Util.createExcelCellRep(19, rw, rs, rsMetadata.getColumnName(19), rsMetadata.getColumnType(19), estiloTabla);
            Util.createExcelCellRep(20, rw, rs, rsMetadata.getColumnName(20), rsMetadata.getColumnType(20), estiloTabla);
            Util.createExcelCellRep(21, rw, rs, rsMetadata.getColumnName(21), rsMetadata.getColumnType(21), estiloTabla);
            Util.createExcelCellRep(22, rw, rs, rsMetadata.getColumnName(22), rsMetadata.getColumnType(22), estiloTabla);
            cnt++;
        }
        //CallableStatement cs2 = null;
        PreparedStatement pstm = null;
        ResultSet rs2 = null;
        //String query = "{call sp_l_generaCedulaE06Encabezado(?)}";
        try {
            pstm = conn.prepareStatement("SELECT mChequesAnt, mCheques, mCirculante, mSaldoBanco FROM dbo.tSIIWEBeCeroSeisEncabezado (NOLOCK) WHERE nFolioeCs = " + folio + ";");
            rs2 = pstm.executeQuery();
            if (rs2.next()) {
                Row rwEnc2 = (sheet0.getRow(1) == null ? sheet0.createRow(1) : sheet0.getRow(1));
                Cell cell2 = (rwEnc2.getCell(10) == null ? rwEnc2.createCell(10) : rwEnc2.getCell(10));
                cell2.setCellValue(rs2.getDouble("mChequesAnt"));
                cell2.setCellStyle(estiloMoneda);
                Row rwEnc3 = (sheet0.getRow(2) == null ? sheet0.createRow(2) : sheet0.getRow(2));
                Cell cell3 = (rwEnc3.getCell(10) == null ? rwEnc3.createCell(10) : rwEnc3.getCell(10));
                cell3.setCellValue(rs2.getDouble("mCheques"));
                cell3.setCellStyle(estiloMoneda);
                Row rwEnc4 = (sheet0.getRow(3) == null ? sheet0.createRow(3) : sheet0.getRow(3));
                Cell cell4 = (rwEnc4.getCell(10) == null ? rwEnc4.createCell(10) : rwEnc4.getCell(10));
                cell4.setCellValue(rs2.getDouble("mCirculante"));
                cell4.setCellStyle(estiloMoneda);
                Row rwEnc5 = (sheet0.getRow(4) == null ? sheet0.createRow(4) : sheet0.getRow(4));
                Cell cell5 = (rwEnc5.getCell(10) == null ? rwEnc5.createCell(10) : rwEnc5.getCell(10));
                cell5.setCellValue(rs2.getDouble("mSaldoBanco"));
                cell5.setCellStyle(estiloMoneda);
            } else {
                workbook.close();
                throw new Exception("No se encontro información...");
            }
        } finally {
            CloseObject.closeObject(rs2, false);
            CloseObject.closeObject(pstm, false);
        }
        File fsalida = new File(file_name);
        FileOutputStream fos = new FileOutputStream(fsalida);
        BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
        workbook.write(bos.toPath());
        /* CierraFlujos */
        workbook.close();
        bos.flush();
        bos.close();
        fos.close();
        return file_name;
    }

    public static String generaCedulaE02(Connection conn, ResultSet rs, String plantillaPath, String mes, String folio) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "ReporteCedulaE02" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        int cnt = 10;
        ResultSetMetaData rsMetadata = rs.getMetaData();
        DataFormat df = workbook.createDataFormat();
        CellStyle estiloMoneda = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 9);
        font.setFontName("Arial");
        estiloMoneda.setFont(font);
        estiloMoneda.setDataFormat(df.getFormat("#,###,##0.00"));
        Row rwEnc1 = (sheet0.getRow(4) == null ? sheet0.createRow(4) : sheet0.getRow(4));
        Cell cell1 = (rwEnc1.getCell(2) == null ? rwEnc1.createCell(2) : rwEnc1.getCell(2));
        cell1.setCellValue(mes);
        while (rs.next()) {
            Row rw = (sheet0.getRow(cnt) == null ? sheet0.createRow(cnt) : sheet0.getRow(cnt));
            if (rs.getInt("cConcepto") == 1) {
                Util.createExcelCellRep(2, rw, rs, rsMetadata.getColumnName(4), rsMetadata.getColumnType(4), estiloMoneda);
                Util.createExcelCellRep(3, rw, rs, rsMetadata.getColumnName(5), rsMetadata.getColumnType(5), estiloMoneda);
                Util.createExcelCellRep(4, rw, rs, rsMetadata.getColumnName(6), rsMetadata.getColumnType(6), estiloMoneda);
                Util.createExcelCellRep(5, rw, rs, rsMetadata.getColumnName(7), rsMetadata.getColumnType(7), estiloMoneda);
                Util.createExcelCellRep(6, rw, rs, rsMetadata.getColumnName(8), rsMetadata.getColumnType(8), estiloMoneda);
                Util.createExcelCellRep(7, rw, rs, rsMetadata.getColumnName(9), rsMetadata.getColumnType(9), estiloMoneda);
                Util.createExcelCellRep(8, rw, rs, rsMetadata.getColumnName(10), rsMetadata.getColumnType(10), estiloMoneda);
                Util.createExcelCellRep(9, rw, rs, rsMetadata.getColumnName(11), rsMetadata.getColumnType(11), estiloMoneda);
                Util.createExcelCellRep(10, rw, rs, rsMetadata.getColumnName(12), rsMetadata.getColumnType(12), estiloMoneda);
                Util.createExcelCellRep(11, rw, rs, rsMetadata.getColumnName(13), rsMetadata.getColumnType(13), estiloMoneda);
                Util.createExcelCellRep(12, rw, rs, rsMetadata.getColumnName(14), rsMetadata.getColumnType(14), estiloMoneda);
                Util.createExcelCellRep(13, rw, rs, rsMetadata.getColumnName(15), rsMetadata.getColumnType(15), estiloMoneda);
                Util.createExcelCellRep(14, rw, rs, rsMetadata.getColumnName(16), rsMetadata.getColumnType(16), estiloMoneda);
                Util.createExcelCellRep(15, rw, rs, rsMetadata.getColumnName(17), rsMetadata.getColumnType(17), estiloMoneda);
                Util.createExcelCellRep(16, rw, rs, rsMetadata.getColumnName(18), rsMetadata.getColumnType(18), estiloMoneda);
                Util.createExcelCellRep(17, rw, rs, rsMetadata.getColumnName(19), rsMetadata.getColumnType(19), estiloMoneda);
                Util.createExcelCellRep(18, rw, rs, rsMetadata.getColumnName(20), rsMetadata.getColumnType(20), estiloMoneda);
                Util.createExcelCellRep(19, rw, rs, rsMetadata.getColumnName(21), rsMetadata.getColumnType(21), estiloMoneda);
                Util.createExcelCellRep(20, rw, rs, rsMetadata.getColumnName(22), rsMetadata.getColumnType(22), estiloMoneda);
                Util.createExcelCellRep(21, rw, rs, rsMetadata.getColumnName(23), rsMetadata.getColumnType(23), estiloMoneda);
                Util.createExcelCellRep(22, rw, rs, rsMetadata.getColumnName(24), rsMetadata.getColumnType(24), estiloMoneda);
                Util.createExcelCellRep(23, rw, rs, rsMetadata.getColumnName(25), rsMetadata.getColumnType(25), estiloMoneda);
                Util.createExcelCellRep(24, rw, rs, rsMetadata.getColumnName(26), rsMetadata.getColumnType(26), estiloMoneda);
                Util.createExcelCellRep(25, rw, rs, rsMetadata.getColumnName(27), rsMetadata.getColumnType(27), estiloMoneda);
                Util.createExcelCellRep(26, rw, rs, rsMetadata.getColumnName(28), rsMetadata.getColumnType(28), estiloMoneda);
                cnt++;
            } else if (rs.getInt("cConcepto") == 2) {
                Util.createExcelCellRep(2, rw, rs, rsMetadata.getColumnName(4), rsMetadata.getColumnType(4), estiloMoneda);
                Util.createExcelCellRep(3, rw, rs, rsMetadata.getColumnName(5), rsMetadata.getColumnType(5), estiloMoneda);
                Util.createExcelCellRep(4, rw, rs, rsMetadata.getColumnName(6), rsMetadata.getColumnType(6), estiloMoneda);
                Util.createExcelCellRep(5, rw, rs, rsMetadata.getColumnName(7), rsMetadata.getColumnType(7), estiloMoneda);
                Util.createExcelCellRep(6, rw, rs, rsMetadata.getColumnName(8), rsMetadata.getColumnType(8), estiloMoneda);
                Util.createExcelCellRep(7, rw, rs, rsMetadata.getColumnName(9), rsMetadata.getColumnType(9), estiloMoneda);
                Util.createExcelCellRep(8, rw, rs, rsMetadata.getColumnName(10), rsMetadata.getColumnType(10), estiloMoneda);
                Util.createExcelCellRep(9, rw, rs, rsMetadata.getColumnName(11), rsMetadata.getColumnType(11), estiloMoneda);
                Util.createExcelCellRep(10, rw, rs, rsMetadata.getColumnName(12), rsMetadata.getColumnType(12), estiloMoneda);
                Util.createExcelCellRep(11, rw, rs, rsMetadata.getColumnName(13), rsMetadata.getColumnType(13), estiloMoneda);
                Util.createExcelCellRep(12, rw, rs, rsMetadata.getColumnName(14), rsMetadata.getColumnType(14), estiloMoneda);
                Util.createExcelCellRep(13, rw, rs, rsMetadata.getColumnName(15), rsMetadata.getColumnType(15), estiloMoneda);
                Util.createExcelCellRep(14, rw, rs, rsMetadata.getColumnName(16), rsMetadata.getColumnType(16), estiloMoneda);
                Util.createExcelCellRep(15, rw, rs, rsMetadata.getColumnName(17), rsMetadata.getColumnType(17), estiloMoneda);
                Util.createExcelCellRep(16, rw, rs, rsMetadata.getColumnName(18), rsMetadata.getColumnType(18), estiloMoneda);
                Util.createExcelCellRep(17, rw, rs, rsMetadata.getColumnName(19), rsMetadata.getColumnType(19), estiloMoneda);
                Util.createExcelCellRep(18, rw, rs, rsMetadata.getColumnName(20), rsMetadata.getColumnType(20), estiloMoneda);
                Util.createExcelCellRep(19, rw, rs, rsMetadata.getColumnName(21), rsMetadata.getColumnType(21), estiloMoneda);
                Util.createExcelCellRep(20, rw, rs, rsMetadata.getColumnName(22), rsMetadata.getColumnType(22), estiloMoneda);
                Util.createExcelCellRep(21, rw, rs, rsMetadata.getColumnName(23), rsMetadata.getColumnType(23), estiloMoneda);
                Util.createExcelCellRep(22, rw, rs, rsMetadata.getColumnName(24), rsMetadata.getColumnType(24), estiloMoneda);
                Util.createExcelCellRep(23, rw, rs, rsMetadata.getColumnName(25), rsMetadata.getColumnType(25), estiloMoneda);
                Util.createExcelCellRep(24, rw, rs, rsMetadata.getColumnName(26), rsMetadata.getColumnType(26), estiloMoneda);
                Util.createExcelCellRep(25, rw, rs, rsMetadata.getColumnName(27), rsMetadata.getColumnType(27), estiloMoneda);
                Util.createExcelCellRep(26, rw, rs, rsMetadata.getColumnName(28), rsMetadata.getColumnType(28), estiloMoneda);
                cnt++;
            } else if (rs.getInt("cConcepto") == 3) {
                Util.createExcelCellRep(2, rw, rs, rsMetadata.getColumnName(4), rsMetadata.getColumnType(4), estiloMoneda);
                Util.createExcelCellRep(3, rw, rs, rsMetadata.getColumnName(5), rsMetadata.getColumnType(5), estiloMoneda);
                Util.createExcelCellRep(4, rw, rs, rsMetadata.getColumnName(6), rsMetadata.getColumnType(6), estiloMoneda);
                Util.createExcelCellRep(5, rw, rs, rsMetadata.getColumnName(7), rsMetadata.getColumnType(7), estiloMoneda);
                Util.createExcelCellRep(6, rw, rs, rsMetadata.getColumnName(8), rsMetadata.getColumnType(8), estiloMoneda);
                Util.createExcelCellRep(7, rw, rs, rsMetadata.getColumnName(9), rsMetadata.getColumnType(9), estiloMoneda);
                Util.createExcelCellRep(8, rw, rs, rsMetadata.getColumnName(10), rsMetadata.getColumnType(10), estiloMoneda);
                Util.createExcelCellRep(9, rw, rs, rsMetadata.getColumnName(11), rsMetadata.getColumnType(11), estiloMoneda);
                Util.createExcelCellRep(10, rw, rs, rsMetadata.getColumnName(12), rsMetadata.getColumnType(12), estiloMoneda);
                Util.createExcelCellRep(11, rw, rs, rsMetadata.getColumnName(13), rsMetadata.getColumnType(13), estiloMoneda);
                Util.createExcelCellRep(12, rw, rs, rsMetadata.getColumnName(14), rsMetadata.getColumnType(14), estiloMoneda);
                Util.createExcelCellRep(13, rw, rs, rsMetadata.getColumnName(15), rsMetadata.getColumnType(15), estiloMoneda);
                Util.createExcelCellRep(14, rw, rs, rsMetadata.getColumnName(16), rsMetadata.getColumnType(16), estiloMoneda);
                Util.createExcelCellRep(15, rw, rs, rsMetadata.getColumnName(17), rsMetadata.getColumnType(17), estiloMoneda);
                Util.createExcelCellRep(16, rw, rs, rsMetadata.getColumnName(18), rsMetadata.getColumnType(18), estiloMoneda);
                Util.createExcelCellRep(17, rw, rs, rsMetadata.getColumnName(19), rsMetadata.getColumnType(19), estiloMoneda);
                Util.createExcelCellRep(18, rw, rs, rsMetadata.getColumnName(20), rsMetadata.getColumnType(20), estiloMoneda);
                Util.createExcelCellRep(19, rw, rs, rsMetadata.getColumnName(21), rsMetadata.getColumnType(21), estiloMoneda);
                Util.createExcelCellRep(20, rw, rs, rsMetadata.getColumnName(22), rsMetadata.getColumnType(22), estiloMoneda);
                Util.createExcelCellRep(21, rw, rs, rsMetadata.getColumnName(23), rsMetadata.getColumnType(23), estiloMoneda);
                Util.createExcelCellRep(22, rw, rs, rsMetadata.getColumnName(24), rsMetadata.getColumnType(24), estiloMoneda);
                Util.createExcelCellRep(23, rw, rs, rsMetadata.getColumnName(25), rsMetadata.getColumnType(25), estiloMoneda);
                Util.createExcelCellRep(24, rw, rs, rsMetadata.getColumnName(26), rsMetadata.getColumnType(26), estiloMoneda);
                Util.createExcelCellRep(25, rw, rs, rsMetadata.getColumnName(27), rsMetadata.getColumnType(27), estiloMoneda);
                Util.createExcelCellRep(26, rw, rs, rsMetadata.getColumnName(28), rsMetadata.getColumnType(28), estiloMoneda);
                Util.createExcelCellRep(27, rw, rs, rsMetadata.getColumnName(29), rsMetadata.getColumnType(29), estiloMoneda);
                cnt = cnt + 2;
            } else if (rs.getInt("cConcepto") == 4) {
                Util.createExcelCellRep(2, rw, rs, rsMetadata.getColumnName(4), rsMetadata.getColumnType(4), estiloMoneda);
                Util.createExcelCellRep(3, rw, rs, rsMetadata.getColumnName(5), rsMetadata.getColumnType(5), estiloMoneda);
                Util.createExcelCellRep(4, rw, rs, rsMetadata.getColumnName(6), rsMetadata.getColumnType(6), estiloMoneda);
                Util.createExcelCellRep(5, rw, rs, rsMetadata.getColumnName(7), rsMetadata.getColumnType(7), estiloMoneda);
                Util.createExcelCellRep(6, rw, rs, rsMetadata.getColumnName(8), rsMetadata.getColumnType(8), estiloMoneda);
                Util.createExcelCellRep(7, rw, rs, rsMetadata.getColumnName(9), rsMetadata.getColumnType(9), estiloMoneda);
                Util.createExcelCellRep(8, rw, rs, rsMetadata.getColumnName(10), rsMetadata.getColumnType(10), estiloMoneda);
                Util.createExcelCellRep(9, rw, rs, rsMetadata.getColumnName(11), rsMetadata.getColumnType(11), estiloMoneda);
                Util.createExcelCellRep(10, rw, rs, rsMetadata.getColumnName(12), rsMetadata.getColumnType(12), estiloMoneda);
                Util.createExcelCellRep(11, rw, rs, rsMetadata.getColumnName(13), rsMetadata.getColumnType(13), estiloMoneda);
                Util.createExcelCellRep(12, rw, rs, rsMetadata.getColumnName(14), rsMetadata.getColumnType(14), estiloMoneda);
                Util.createExcelCellRep(13, rw, rs, rsMetadata.getColumnName(15), rsMetadata.getColumnType(15), estiloMoneda);
                Util.createExcelCellRep(14, rw, rs, rsMetadata.getColumnName(16), rsMetadata.getColumnType(16), estiloMoneda);
                Util.createExcelCellRep(15, rw, rs, rsMetadata.getColumnName(17), rsMetadata.getColumnType(17), estiloMoneda);
                Util.createExcelCellRep(16, rw, rs, rsMetadata.getColumnName(18), rsMetadata.getColumnType(18), estiloMoneda);
                Util.createExcelCellRep(17, rw, rs, rsMetadata.getColumnName(19), rsMetadata.getColumnType(19), estiloMoneda);
                Util.createExcelCellRep(18, rw, rs, rsMetadata.getColumnName(20), rsMetadata.getColumnType(20), estiloMoneda);
                Util.createExcelCellRep(19, rw, rs, rsMetadata.getColumnName(21), rsMetadata.getColumnType(21), estiloMoneda);
                Util.createExcelCellRep(20, rw, rs, rsMetadata.getColumnName(22), rsMetadata.getColumnType(22), estiloMoneda);
                Util.createExcelCellRep(21, rw, rs, rsMetadata.getColumnName(23), rsMetadata.getColumnType(23), estiloMoneda);
                Util.createExcelCellRep(22, rw, rs, rsMetadata.getColumnName(24), rsMetadata.getColumnType(24), estiloMoneda);
                Util.createExcelCellRep(23, rw, rs, rsMetadata.getColumnName(25), rsMetadata.getColumnType(25), estiloMoneda);
                Util.createExcelCellRep(24, rw, rs, rsMetadata.getColumnName(26), rsMetadata.getColumnType(26), estiloMoneda);
                Util.createExcelCellRep(25, rw, rs, rsMetadata.getColumnName(27), rsMetadata.getColumnType(27), estiloMoneda);
                Util.createExcelCellRep(26, rw, rs, rsMetadata.getColumnName(28), rsMetadata.getColumnType(28), estiloMoneda);
                Util.createExcelCellRep(27, rw, rs, rsMetadata.getColumnName(29), rsMetadata.getColumnType(29), estiloMoneda);
                cnt = cnt + 4;
            } else if (rs.getInt("cConcepto") == 5) {
                Util.createExcelCellRep(2, rw, rs, rsMetadata.getColumnName(4), rsMetadata.getColumnType(4), estiloMoneda);
                Util.createExcelCellRep(3, rw, rs, rsMetadata.getColumnName(5), rsMetadata.getColumnType(5), estiloMoneda);
                Util.createExcelCellRep(4, rw, rs, rsMetadata.getColumnName(6), rsMetadata.getColumnType(6), estiloMoneda);
                Util.createExcelCellRep(5, rw, rs, rsMetadata.getColumnName(7), rsMetadata.getColumnType(7), estiloMoneda);
                Util.createExcelCellRep(6, rw, rs, rsMetadata.getColumnName(8), rsMetadata.getColumnType(8), estiloMoneda);
                Util.createExcelCellRep(7, rw, rs, rsMetadata.getColumnName(9), rsMetadata.getColumnType(9), estiloMoneda);
                Util.createExcelCellRep(8, rw, rs, rsMetadata.getColumnName(10), rsMetadata.getColumnType(10), estiloMoneda);
                Util.createExcelCellRep(9, rw, rs, rsMetadata.getColumnName(11), rsMetadata.getColumnType(11), estiloMoneda);
                Util.createExcelCellRep(10, rw, rs, rsMetadata.getColumnName(12), rsMetadata.getColumnType(12), estiloMoneda);
                Util.createExcelCellRep(11, rw, rs, rsMetadata.getColumnName(13), rsMetadata.getColumnType(13), estiloMoneda);
                Util.createExcelCellRep(12, rw, rs, rsMetadata.getColumnName(14), rsMetadata.getColumnType(14), estiloMoneda);
                Util.createExcelCellRep(13, rw, rs, rsMetadata.getColumnName(15), rsMetadata.getColumnType(15), estiloMoneda);
                Util.createExcelCellRep(14, rw, rs, rsMetadata.getColumnName(16), rsMetadata.getColumnType(16), estiloMoneda);
                Util.createExcelCellRep(15, rw, rs, rsMetadata.getColumnName(17), rsMetadata.getColumnType(17), estiloMoneda);
                Util.createExcelCellRep(16, rw, rs, rsMetadata.getColumnName(18), rsMetadata.getColumnType(18), estiloMoneda);
                Util.createExcelCellRep(17, rw, rs, rsMetadata.getColumnName(19), rsMetadata.getColumnType(19), estiloMoneda);
                Util.createExcelCellRep(18, rw, rs, rsMetadata.getColumnName(20), rsMetadata.getColumnType(20), estiloMoneda);
                Util.createExcelCellRep(19, rw, rs, rsMetadata.getColumnName(21), rsMetadata.getColumnType(21), estiloMoneda);
                Util.createExcelCellRep(20, rw, rs, rsMetadata.getColumnName(22), rsMetadata.getColumnType(22), estiloMoneda);
                Util.createExcelCellRep(21, rw, rs, rsMetadata.getColumnName(23), rsMetadata.getColumnType(23), estiloMoneda);
                Util.createExcelCellRep(22, rw, rs, rsMetadata.getColumnName(24), rsMetadata.getColumnType(24), estiloMoneda);
                Util.createExcelCellRep(23, rw, rs, rsMetadata.getColumnName(25), rsMetadata.getColumnType(25), estiloMoneda);
                Util.createExcelCellRep(24, rw, rs, rsMetadata.getColumnName(26), rsMetadata.getColumnType(26), estiloMoneda);
                Util.createExcelCellRep(25, rw, rs, rsMetadata.getColumnName(27), rsMetadata.getColumnType(27), estiloMoneda);
                Util.createExcelCellRep(26, rw, rs, rsMetadata.getColumnName(28), rsMetadata.getColumnType(28), estiloMoneda);
                Util.createExcelCellRep(27, rw, rs, rsMetadata.getColumnName(29), rsMetadata.getColumnType(29), estiloMoneda);
                cnt = cnt + 4;
            } else if (rs.getInt("cConcepto") == 6) {
                Util.createExcelCellRep(2, rw, rs, rsMetadata.getColumnName(4), rsMetadata.getColumnType(4), estiloMoneda);
                Util.createExcelCellRep(4, rw, rs, rsMetadata.getColumnName(6), rsMetadata.getColumnType(6), estiloMoneda);
                Util.createExcelCellRep(6, rw, rs, rsMetadata.getColumnName(8), rsMetadata.getColumnType(8), estiloMoneda);
                Util.createExcelCellRep(8, rw, rs, rsMetadata.getColumnName(10), rsMetadata.getColumnType(10), estiloMoneda);
                Util.createExcelCellRep(10, rw, rs, rsMetadata.getColumnName(12), rsMetadata.getColumnType(12), estiloMoneda);
                Util.createExcelCellRep(12, rw, rs, rsMetadata.getColumnName(14), rsMetadata.getColumnType(14), estiloMoneda);
                Util.createExcelCellRep(14, rw, rs, rsMetadata.getColumnName(16), rsMetadata.getColumnType(16), estiloMoneda);
                Util.createExcelCellRep(16, rw, rs, rsMetadata.getColumnName(18), rsMetadata.getColumnType(18), estiloMoneda);
                Util.createExcelCellRep(18, rw, rs, rsMetadata.getColumnName(20), rsMetadata.getColumnType(20), estiloMoneda);
                Util.createExcelCellRep(20, rw, rs, rsMetadata.getColumnName(22), rsMetadata.getColumnType(22), estiloMoneda);
                Util.createExcelCellRep(22, rw, rs, rsMetadata.getColumnName(24), rsMetadata.getColumnType(24), estiloMoneda);
                Util.createExcelCellRep(24, rw, rs, rsMetadata.getColumnName(26), rsMetadata.getColumnType(26), estiloMoneda);
                Util.createExcelCellRep(26, rw, rs, rsMetadata.getColumnName(28), rsMetadata.getColumnType(28), estiloMoneda);
                cnt++;
            } else if (rs.getInt("cConcepto") == 7) {
                Util.createExcelCellRep(2, rw, rs, rsMetadata.getColumnName(4), rsMetadata.getColumnType(4), estiloMoneda);
                Util.createExcelCellRep(4, rw, rs, rsMetadata.getColumnName(6), rsMetadata.getColumnType(6), estiloMoneda);
                Util.createExcelCellRep(6, rw, rs, rsMetadata.getColumnName(8), rsMetadata.getColumnType(8), estiloMoneda);
                Util.createExcelCellRep(8, rw, rs, rsMetadata.getColumnName(10), rsMetadata.getColumnType(10), estiloMoneda);
                Util.createExcelCellRep(10, rw, rs, rsMetadata.getColumnName(12), rsMetadata.getColumnType(12), estiloMoneda);
                Util.createExcelCellRep(12, rw, rs, rsMetadata.getColumnName(14), rsMetadata.getColumnType(14), estiloMoneda);
                Util.createExcelCellRep(14, rw, rs, rsMetadata.getColumnName(16), rsMetadata.getColumnType(16), estiloMoneda);
                Util.createExcelCellRep(16, rw, rs, rsMetadata.getColumnName(18), rsMetadata.getColumnType(18), estiloMoneda);
                Util.createExcelCellRep(18, rw, rs, rsMetadata.getColumnName(20), rsMetadata.getColumnType(20), estiloMoneda);
                Util.createExcelCellRep(20, rw, rs, rsMetadata.getColumnName(22), rsMetadata.getColumnType(22), estiloMoneda);
                Util.createExcelCellRep(22, rw, rs, rsMetadata.getColumnName(24), rsMetadata.getColumnType(24), estiloMoneda);
                Util.createExcelCellRep(24, rw, rs, rsMetadata.getColumnName(26), rsMetadata.getColumnType(26), estiloMoneda);
                Util.createExcelCellRep(26, rw, rs, rsMetadata.getColumnName(28), rsMetadata.getColumnType(28), estiloMoneda);
                cnt = cnt + 2;
            } else if (rs.getInt("cConcepto") == 8) {
                Util.createExcelCellRep(2, rw, rs, rsMetadata.getColumnName(4), rsMetadata.getColumnType(4), estiloMoneda);
                Util.createExcelCellRep(4, rw, rs, rsMetadata.getColumnName(6), rsMetadata.getColumnType(6), estiloMoneda);
                Util.createExcelCellRep(6, rw, rs, rsMetadata.getColumnName(8), rsMetadata.getColumnType(8), estiloMoneda);
                Util.createExcelCellRep(8, rw, rs, rsMetadata.getColumnName(10), rsMetadata.getColumnType(10), estiloMoneda);
                Util.createExcelCellRep(10, rw, rs, rsMetadata.getColumnName(12), rsMetadata.getColumnType(12), estiloMoneda);
                Util.createExcelCellRep(12, rw, rs, rsMetadata.getColumnName(14), rsMetadata.getColumnType(14), estiloMoneda);
                Util.createExcelCellRep(14, rw, rs, rsMetadata.getColumnName(16), rsMetadata.getColumnType(16), estiloMoneda);
                Util.createExcelCellRep(16, rw, rs, rsMetadata.getColumnName(18), rsMetadata.getColumnType(18), estiloMoneda);
                Util.createExcelCellRep(18, rw, rs, rsMetadata.getColumnName(20), rsMetadata.getColumnType(20), estiloMoneda);
                Util.createExcelCellRep(20, rw, rs, rsMetadata.getColumnName(22), rsMetadata.getColumnType(22), estiloMoneda);
                Util.createExcelCellRep(22, rw, rs, rsMetadata.getColumnName(24), rsMetadata.getColumnType(24), estiloMoneda);
                Util.createExcelCellRep(24, rw, rs, rsMetadata.getColumnName(26), rsMetadata.getColumnType(26), estiloMoneda);
                Util.createExcelCellRep(26, rw, rs, rsMetadata.getColumnName(28), rsMetadata.getColumnType(28), estiloMoneda);
                cnt++;
            } else if (rs.getInt("cConcepto") == 9) {
                Util.createExcelCellRep(2, rw, rs, rsMetadata.getColumnName(4), rsMetadata.getColumnType(4), estiloMoneda);
                Util.createExcelCellRep(4, rw, rs, rsMetadata.getColumnName(6), rsMetadata.getColumnType(6), estiloMoneda);
                Util.createExcelCellRep(6, rw, rs, rsMetadata.getColumnName(8), rsMetadata.getColumnType(8), estiloMoneda);
                Util.createExcelCellRep(8, rw, rs, rsMetadata.getColumnName(10), rsMetadata.getColumnType(10), estiloMoneda);
                Util.createExcelCellRep(10, rw, rs, rsMetadata.getColumnName(12), rsMetadata.getColumnType(12), estiloMoneda);
                Util.createExcelCellRep(12, rw, rs, rsMetadata.getColumnName(14), rsMetadata.getColumnType(14), estiloMoneda);
                Util.createExcelCellRep(14, rw, rs, rsMetadata.getColumnName(16), rsMetadata.getColumnType(16), estiloMoneda);
                Util.createExcelCellRep(16, rw, rs, rsMetadata.getColumnName(18), rsMetadata.getColumnType(18), estiloMoneda);
                Util.createExcelCellRep(18, rw, rs, rsMetadata.getColumnName(20), rsMetadata.getColumnType(20), estiloMoneda);
                Util.createExcelCellRep(20, rw, rs, rsMetadata.getColumnName(22), rsMetadata.getColumnType(22), estiloMoneda);
                Util.createExcelCellRep(22, rw, rs, rsMetadata.getColumnName(24), rsMetadata.getColumnType(24), estiloMoneda);
                Util.createExcelCellRep(24, rw, rs, rsMetadata.getColumnName(26), rsMetadata.getColumnType(26), estiloMoneda);
                Util.createExcelCellRep(26, rw, rs, rsMetadata.getColumnName(28), rsMetadata.getColumnType(28), estiloMoneda);
                cnt = cnt + 4;
            } else if (rs.getInt("cConcepto") == 10) {
                Util.createExcelCellRep(2, rw, rs, rsMetadata.getColumnName(4), rsMetadata.getColumnType(4), estiloMoneda);
                cnt++;
            } else if (rs.getInt("cConcepto") == 11) {
                Util.createExcelCellRep(2, rw, rs, rsMetadata.getColumnName(4), rsMetadata.getColumnType(4), estiloMoneda);
                Util.createExcelCellRep(4, rw, rs, rsMetadata.getColumnName(6), rsMetadata.getColumnType(6), estiloMoneda);
                Util.createExcelCellRep(6, rw, rs, rsMetadata.getColumnName(8), rsMetadata.getColumnType(8), estiloMoneda);
                Util.createExcelCellRep(8, rw, rs, rsMetadata.getColumnName(10), rsMetadata.getColumnType(10), estiloMoneda);
                Util.createExcelCellRep(10, rw, rs, rsMetadata.getColumnName(12), rsMetadata.getColumnType(12), estiloMoneda);
                Util.createExcelCellRep(12, rw, rs, rsMetadata.getColumnName(14), rsMetadata.getColumnType(14), estiloMoneda);
                Util.createExcelCellRep(14, rw, rs, rsMetadata.getColumnName(16), rsMetadata.getColumnType(16), estiloMoneda);
                Util.createExcelCellRep(16, rw, rs, rsMetadata.getColumnName(18), rsMetadata.getColumnType(18), estiloMoneda);
                Util.createExcelCellRep(18, rw, rs, rsMetadata.getColumnName(20), rsMetadata.getColumnType(20), estiloMoneda);
                Util.createExcelCellRep(20, rw, rs, rsMetadata.getColumnName(22), rsMetadata.getColumnType(22), estiloMoneda);
                Util.createExcelCellRep(22, rw, rs, rsMetadata.getColumnName(24), rsMetadata.getColumnType(24), estiloMoneda);
                Util.createExcelCellRep(24, rw, rs, rsMetadata.getColumnName(26), rsMetadata.getColumnType(26), estiloMoneda);
                cnt++;
            } else if (rs.getInt("cConcepto") == 12) {
                Util.createExcelCellRep(2, rw, rs, rsMetadata.getColumnName(4), rsMetadata.getColumnType(4), estiloMoneda);
                Util.createExcelCellRep(4, rw, rs, rsMetadata.getColumnName(6), rsMetadata.getColumnType(6), estiloMoneda);
                Util.createExcelCellRep(6, rw, rs, rsMetadata.getColumnName(8), rsMetadata.getColumnType(8), estiloMoneda);
                Util.createExcelCellRep(8, rw, rs, rsMetadata.getColumnName(10), rsMetadata.getColumnType(10), estiloMoneda);
                Util.createExcelCellRep(10, rw, rs, rsMetadata.getColumnName(12), rsMetadata.getColumnType(12), estiloMoneda);
                Util.createExcelCellRep(12, rw, rs, rsMetadata.getColumnName(14), rsMetadata.getColumnType(14), estiloMoneda);
                Util.createExcelCellRep(14, rw, rs, rsMetadata.getColumnName(16), rsMetadata.getColumnType(16), estiloMoneda);
                Util.createExcelCellRep(16, rw, rs, rsMetadata.getColumnName(18), rsMetadata.getColumnType(18), estiloMoneda);
                Util.createExcelCellRep(18, rw, rs, rsMetadata.getColumnName(20), rsMetadata.getColumnType(20), estiloMoneda);
                Util.createExcelCellRep(20, rw, rs, rsMetadata.getColumnName(22), rsMetadata.getColumnType(22), estiloMoneda);
                Util.createExcelCellRep(22, rw, rs, rsMetadata.getColumnName(24), rsMetadata.getColumnType(24), estiloMoneda);
                Util.createExcelCellRep(24, rw, rs, rsMetadata.getColumnName(26), rsMetadata.getColumnType(26), estiloMoneda);
                cnt = 10;
            }
        }
        sheet0 = Util.EvaluaFormula(workbook, sheet0, 7, 40, 1, 40);
        File fsalida = new File(file_name);
        FileOutputStream fos = new FileOutputStream(fsalida);
        BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
        workbook.write(bos.toPath());
        /* CierraFlujos */
        bos.flush();
        bos.close();
        fos.close();
        return file_name;
    }
}
