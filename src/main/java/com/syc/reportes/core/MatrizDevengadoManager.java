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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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

public class MatrizDevengadoManager {

    public static String generaReporteMatrizDevengado(Connection conn, Map<String, String> plantillas, String tipo) throws Exception {
        PreparedStatement pstm_version = null;
        CallableStatement cs = null, cs2 = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        ResultSet nVersion = null;
        String fileName = "";
        String version = "";
        String query = "", query2 = "";
        if ("Devengado".equals(tipo))
            query = "{call sp_l_MatrizGasto( )}";
        else if ("Pagado".equals(tipo))
            query = "{call sp_a_MatrizGastoPagado( )}";
        else if ("Ingreso".equals(tipo))
            query = "{call sp_a_MatrizGastoIngreso( )}";
        try {
            cs = conn.prepareCall(query);
            rs = cs.executeQuery();
            if ("Devengado".equals(tipo)) {
                query2 = "{call sp_a_ConciliacionMatrizGastoDevengado( )}";
            } else if ("Pagado".equals(tipo)) {
                query2 = "{call sp_a_ConciliacionMatrizGastoPagado( )}";
            } else if ("Ingreso".equals(tipo)) {
                query2 = "{call sp_a_ConciliacionMatrizGastoIngreso( )}";
            }
            cs2 = conn.prepareCall(query2);
            rs2 = cs2.executeQuery();
            pstm_version = conn.prepareStatement("SELECT cVersion FROM tMatricesConac WHERE nActivo = 1");
            nVersion = pstm_version.executeQuery();
            if (nVersion.next()) {
                version = nVersion.getString("cVersion");
            }
            fileName = generaReporteMatriz(rs, rs2, version, plantillas.get("MATRIZ"), tipo);
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(rs2, false);
            CloseObject.closeObject(cs, false);
            CloseObject.closeObject(nVersion, false);
            CloseObject.closeObject(cs2, false);
            CloseObject.closeObject(pstm_version, false);
        }
    }

    private static String generaReporteMatriz(ResultSet rs, ResultSet rs1, String version, String plantillaPath, String tipo) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "Matriz" + tipo + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        Sheet sheet1 = workbook.getSheetAt(1);
        int cnt = 0;
        ResultSetMetaData rsMetadata = rs.getMetaData();
        ResultSetMetaData rs1Metadata = rs1.getMetaData();
        int renglonInicio = 11;
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date();
        String fecha = dateFormat.format(date);
        String encabezadoV = "";
        String encabezadoM = "";
        String encabezadoD = "";
        if ("Devengado".equals(tipo)) {
            encabezadoV = "Validación - A.1 Matriz " + tipo + " de Gastos";
            encabezadoM = "A.1 Matriz " + tipo + " de Gastos";
            encabezadoD = "Diferencias - A.1 Matriz " + tipo + " de Gastos";
        } else if ("Pagado".equals(tipo)) {
            encabezadoV = "Validación - A.2 Matriz " + tipo + " de Gastos";
            encabezadoM = "A.2 Matriz " + tipo + " de Gastos";
            encabezadoD = "Diferencias - A.2 Matriz " + tipo + " de Gastos";
        } else if ("Ingreso".equals(tipo)) {
            encabezadoV = "Validación - B.3 Matriz de Ingreso Devengado y Recaudado Simultaneos";
            encabezadoM = "B.3 Matriz de Ingreso Devengado y Recaudado Simultaneos";
            encabezadoD = "Diferencias - B.3 Matriz de Ingreso Devengado y Recaudado Simultáneos";
        }
        Row rwfecha = (sheet0.getRow(6) == null ? sheet0.createRow(6) : sheet0.getRow(6));
        Cell celda = (rwfecha.getCell(2) == null ? rwfecha.createCell(2) : rwfecha.getCell(2));
        celda.setCellValue(fecha);
        Row rwVersion = (sheet0.getRow(5) == null ? sheet0.createRow(5) : sheet0.getRow(5));
        Cell celda1 = (rwVersion.getCell(2) == null ? rwVersion.createCell(2) : rwVersion.getCell(2));
        celda1.setCellValue(version);
        Row rwTipo = (sheet0.getRow(3) == null ? sheet0.createRow(3) : sheet0.getRow(3));
        Cell celda2 = (rwTipo.getCell(1) == null ? rwTipo.createCell(1) : rwTipo.getCell(1));
        celda2.setCellValue(encabezadoV);
        Row rwTipo1 = (sheet0.getRow(8) == null ? sheet0.createRow(8) : sheet0.getRow(8));
        Cell celda3 = (rwTipo1.getCell(1) == null ? rwTipo1.createCell(1) : rwTipo1.getCell(1));
        celda3.setCellValue(encabezadoM);
        Row rwTipo2 = (sheet1.getRow(2) == null ? sheet1.createRow(2) : sheet1.getRow(2));
        Cell celda4 = (rwTipo2.getCell(1) == null ? rwTipo2.createCell(1) : rwTipo2.getCell(1));
        celda4.setCellValue(encabezadoD);
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
        while (rs.next()) {
            int rows = renglonInicio + cnt;
            sheet0.shiftRows(rows, sheet0.getLastRowNum(), 1);
            Row rw = (sheet0.getRow(renglonInicio + cnt) == null ? sheet0.createRow(renglonInicio + cnt) : sheet0.getRow(renglonInicio + cnt));
            for (int i = 0; i < rsMetadata.getColumnCount(); i++) {
                if (rsMetadata.getColumnName(i + 1).equals("nOrden")) {
                    // No se imprime
                } else {
                    Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
                }
            }
            cnt++;
        }
        renglonInicio = 6;
        cnt = 0;
        while (rs1.next()) {
            int rows1 = renglonInicio + cnt;
            sheet1.shiftRows(rows1, sheet1.getLastRowNum(), 1);
            Row rw1 = (sheet1.getRow(renglonInicio + cnt) == null ? sheet1.createRow(renglonInicio + cnt) : sheet1.getRow(renglonInicio + cnt));
            for (int i = 0; i < rs1Metadata.getColumnCount(); i++) {
                if (rs1Metadata.getColumnName(i + 1).equals("mMovimiento")) {
                    Util.createExcelCellRep(i + 1, rw1, rs1, rs1Metadata.getColumnName(i + 1), rs1Metadata.getColumnType(i + 1), estiloMoneda);
                } else {
                    Util.createExcelCellRep(i + 1, rw1, rs1, rs1Metadata.getColumnName(i + 1), rs1Metadata.getColumnType(i + 1), estiloTabla);
                }
            }
            cnt++;
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
}
