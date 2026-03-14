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
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import java.util.Base64;
import java.nio.file.Paths;

public class ReporteAntAmortManager {

    public static String generaReporteAntAmort(Connection conn, Map<String, String> plantillas, String centroContable, String fechaFin, String unidad) throws Exception {
        CallableStatement cs = null;
        CallableStatement cs1 = null;
        CallableStatement cs2 = null;
        PreparedStatement ps = null, ps1 = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        ResultSet rs3 = null;
        ResultSet rs4 = null, rs5 = null;
        ;
        String fileName = "";
        String mensaje = "";
        String d_unidad = "";
        String query = "{call sp_l_reporteAntAmort( ?, ?, ? )}";
        String query2 = "SELECT cDescripcion FROM tCatalogoCentroContable (NOLOCK) WHERE cCentroContable = ?";
        String query3 = "{call sp_reporteAntAmortAntig (?, ?)}";
        String query4 = "{call sp_reporteAntAmortPorProv(?, ?)}";
        String query5 = "SELECT UPPER(D_DESCRIPCION) AS d_unidad FROM tCatUnidadEjecutora  (NOLOCK) WHERE cUnidadEjecutora = ?";
        try {
            cs = conn.prepareCall(query);
            cs1 = conn.prepareCall(query3);
            cs2 = conn.prepareCall(query4);
            cs.setInt(1, 1);
            cs.setString(2, centroContable);
            cs.setString(3, fechaFin);
            rs = cs.executeQuery();
            cs1.setString(1, centroContable);
            cs1.setString(2, fechaFin);
            rs2 = cs1.executeQuery();
            cs2.setString(1, centroContable);
            cs2.setString(2, fechaFin);
            rs3 = cs2.executeQuery();
            ps = conn.prepareStatement(query2);
            ps.setString(1, centroContable);
            rs4 = ps.executeQuery();
            if (rs4.next()) {
                mensaje = rs4.getString("cDescripcion");
            }
            ps1 = conn.prepareStatement(query5);
            ps1.setString(1, unidad);
            rs5 = ps1.executeQuery();
            if (rs5.next()) {
                d_unidad = rs5.getString("d_unidad");
            }
            fileName = generaReporteAntAmort(rs, rs2, rs3, plantillas.get("ANTAMORT"), mensaje, fechaFin, d_unidad);
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(rs2, false);
            CloseObject.closeObject(rs3, false);
            CloseObject.closeObject(rs4, false);
            CloseObject.closeObject(ps, false);
            CloseObject.closeObject(cs, false);
            CloseObject.closeObject(cs1, false);
            CloseObject.closeObject(cs2, false);
        }
    }

    private static String generaReporteAntAmort(ResultSet rs, ResultSet rs1, ResultSet rs2, String plantillaPath, String mensaje, String fechaFin, String d_unidad) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "ReporteAntAmort" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        Sheet sheet1 = workbook.getSheetAt(1);
        Sheet sheet2 = workbook.getSheetAt(2);
        int cnt = 0;
        ResultSetMetaData rsMetadata = rs.getMetaData();
        ResultSetMetaData rs1Metadata = rs1.getMetaData();
        ResultSetMetaData rs2Metadata = rs2.getMetaData();
        int renglonInicio = 8;
        //DateFormat dateFormat = new SimpleDateFormat("dd-mm-yyyy");
        String fecha = fechaFin;
        Row rwfecha = (sheet0.getRow(5) == null ? sheet0.createRow(5) : sheet0.getRow(5));
        Cell celda = (rwfecha.getCell(1) == null ? rwfecha.createCell(1) : rwfecha.getCell(1));
        celda.setCellValue(fecha);
        Row rwfecha1 = (sheet1.getRow(5) == null ? sheet1.createRow(5) : sheet1.getRow(5));
        Cell celda1 = (rwfecha1.getCell(1) == null ? rwfecha1.createCell(1) : rwfecha1.getCell(1));
        celda1.setCellValue(fecha);
        Row rwfecha2 = (sheet2.getRow(5) == null ? sheet2.createRow(5) : sheet2.getRow(5));
        Cell celda2 = (rwfecha2.getCell(1) == null ? rwfecha2.createCell(1) : rwfecha2.getCell(1));
        celda2.setCellValue(fecha);
        String estado = mensaje;
        Row rwEnc2 = (sheet0.getRow(2) == null ? sheet0.createRow(2) : sheet0.getRow(2));
        Cell cell2 = (rwEnc2.getCell(0) == null ? rwEnc2.createCell(0) : rwEnc2.getCell(0));
        cell2.setCellValue(estado);
        Row rwEnc3 = (sheet1.getRow(2) == null ? sheet1.createRow(2) : sheet1.getRow(2));
        Cell cell3 = (rwEnc3.getCell(0) == null ? rwEnc3.createCell(0) : rwEnc3.getCell(0));
        cell3.setCellValue(estado);
        Row rwEnc4 = (sheet2.getRow(2) == null ? sheet2.createRow(2) : sheet2.getRow(2));
        Cell cell4 = (rwEnc4.getCell(0) == null ? rwEnc4.createCell(0) : rwEnc4.getCell(0));
        cell4.setCellValue(estado);
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
        CellStyle estiloFecha = workbook.createCellStyle();
        estiloFecha.setBorderRight(BorderStyle.HAIR);
        estiloFecha.setBorderLeft(BorderStyle.HAIR);
        estiloFecha.setBorderTop(BorderStyle.HAIR);
        estiloFecha.setBorderBottom(BorderStyle.HAIR);
        estiloFecha.setDataFormat(df.getFormat("dd/mm/yyyy"));
        while (rs.next()) {
            int rows = renglonInicio + cnt;
            sheet0.shiftRows(rows, sheet0.getLastRowNum(), 1);
            Row rw = (sheet0.getRow(renglonInicio + cnt) == null ? sheet0.createRow(renglonInicio + cnt) : sheet0.getRow(renglonInicio + cnt));
            for (int h = 0; h < rsMetadata.getColumnCount(); h++) {
                if (rsMetadata.getColumnName(h + 1).equals("ANTICIPO") || rsMetadata.getColumnName(h + 1).equals("AMORTIZACION")) {
                    Util.createExcelCellRep(h, rw, rs, rsMetadata.getColumnName(h + 1), rsMetadata.getColumnType(h + 1), estiloMoneda);
                } else if (rsMetadata.getColumnName(h + 1).equals("FECHA")) {
                    Util.createExcelCellRep(h, rw, rs, rsMetadata.getColumnName(h + 1), rsMetadata.getColumnType(h + 1), estiloFecha);
                } else {
                    Util.createExcelCellRep(h, rw, rs, rsMetadata.getColumnName(h + 1), rsMetadata.getColumnType(h + 1), estiloTabla);
                }
            }
            cnt++;
        }
        cnt = 0;
        while (rs1.next()) {
            int rows1 = renglonInicio + cnt;
            sheet1.shiftRows(rows1, sheet1.getLastRowNum(), 1);
            Row rw1 = (sheet1.getRow(renglonInicio + cnt) == null ? sheet1.createRow(renglonInicio + cnt) : sheet1.getRow(renglonInicio + cnt));
            for (int i = 0; i < rs1Metadata.getColumnCount(); i++) {
                if (rs1Metadata.getColumnName(i + 1).equals("Saldo") || rs1Metadata.getColumnName(i + 1).equals("DE91A180") || rs1Metadata.getColumnName(i + 1).equals("DE181A365") || rs1Metadata.getColumnName(i + 1).equals("MAYOR365")) {
                    Util.createExcelCellRep(i, rw1, rs1, rs1Metadata.getColumnName(i + 1), rs1Metadata.getColumnType(i + 1), estiloMoneda);
                } else if (rs1Metadata.getColumnName(i + 1).equals("Fecha")) {
                    Util.createExcelCellRep(i, rw1, rs1, rs1Metadata.getColumnName(i + 1), rs1Metadata.getColumnType(i + 1), estiloFecha);
                } else {
                    Util.createExcelCellRep(i, rw1, rs1, rs1Metadata.getColumnName(i + 1), rs1Metadata.getColumnType(i + 1), estiloTabla);
                }
            }
            cnt++;
        }
        cnt = 0;
        while (rs2.next()) {
            int rows2 = renglonInicio + cnt;
            sheet2.shiftRows(rows2, sheet2.getLastRowNum(), 1);
            Row rw2 = (sheet2.getRow(renglonInicio + cnt) == null ? sheet2.createRow(renglonInicio + cnt) : sheet2.getRow(renglonInicio + cnt));
            for (int i = 0; i < rs2Metadata.getColumnCount(); i++) {
                if (rs2Metadata.getColumnName(i + 1).equals("Saldo")) {
                    Util.createExcelCellRep(i, rw2, rs2, rs2Metadata.getColumnName(i + 1), rs2Metadata.getColumnType(i + 1), estiloMoneda);
                } else if (rs2Metadata.getColumnName(i + 1).equals("Fecha")) {
                    Util.createExcelCellRep(i, rw2, rs2, rs2Metadata.getColumnName(i + 1), rs2Metadata.getColumnType(i + 1), estiloFecha);
                } else {
                    Util.createExcelCellRep(i, rw2, rs2, rs2Metadata.getColumnName(i + 1), rs2Metadata.getColumnType(i + 1), estiloTabla);
                }
            }
            cnt++;
        }
        Row rwEnc = (sheet0.getRow(3) == null ? sheet0.createRow(3) : sheet0.getRow(3));
        Cell cell = (rwEnc.getCell(0) == null ? rwEnc.createCell(0) : rwEnc.getCell(0));
        cell.setCellValue(d_unidad);
        int j = 0;
        j = sheet0.getLastRowNum();
        sheet0 = Util.EvaluaFormula(workbook, sheet0, j - 8, j - 5, 13, 16);
        j = sheet1.getLastRowNum();
        sheet1 = Util.EvaluaFormula(workbook, sheet1, j - 8, j - 6, 3, 4);
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
