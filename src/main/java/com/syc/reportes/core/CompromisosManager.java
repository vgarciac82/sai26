package com.syc.reportes.core;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
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

public class CompromisosManager {

    public static String generaReporteCompromisosManager(Connection conn, Integer tipo, Map<String, String> plantillas) throws Exception {
        Statement pstmt = null;
        ResultSet rs = null;
        String query = "SELECT * FROM vCompromisosSai ORDER BY caNoCompromiso";
        System.out.print(query);
        String fileName = "";
        try {
            pstmt = conn.createStatement();
            rs = pstmt.executeQuery(query);
            fileName = generaReporteCompromisos(rs, plantillas.get("COMPROMISOSSAI"));
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(pstmt, false);
        }
    }

    private static String generaReporteCompromisos(ResultSet rs, String plantillaPath) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "ReporteCompromisos" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        int renglonInicio = 3;
        ResultSetMetaData rsMetadata = rs.getMetaData();
        String DATE_FORMAT = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        // today
        Calendar c1 = Calendar.getInstance();
        String periodo = sdf.format(c1.getTime());
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 9);
        font.setFontName("Arial");
        CellStyle estiloTabla = workbook.createCellStyle();
        estiloTabla.setBorderRight(BorderStyle.HAIR);
        estiloTabla.setBorderLeft(BorderStyle.HAIR);
        estiloTabla.setBorderTop(BorderStyle.HAIR);
        estiloTabla.setFont(font);
        estiloTabla.setBorderBottom(BorderStyle.HAIR);
        DataFormat df = workbook.createDataFormat();
        CellStyle estiloFecha = workbook.createCellStyle();
        estiloFecha.setBorderRight(BorderStyle.HAIR);
        estiloFecha.setBorderLeft(BorderStyle.HAIR);
        estiloFecha.setBorderTop(BorderStyle.HAIR);
        estiloFecha.setBorderBottom(BorderStyle.HAIR);
        estiloFecha.setFont(font);
        estiloFecha.setDataFormat(df.getFormat("dd/mm/yyyy"));
        CellStyle estilodato = workbook.createCellStyle();
        estilodato.setFont(font);
        while (rs.next()) {
            Row rw = (sheet0.getRow(renglonInicio) == null ? sheet0.createRow(renglonInicio) : sheet0.getRow(renglonInicio));
            Util.createExcelCellRep(0, rw, rs, rsMetadata.getColumnName(1), rsMetadata.getColumnType(1), estiloTabla);
            Util.createExcelCellRep(1, rw, rs, rsMetadata.getColumnName(2), rsMetadata.getColumnType(2), estiloFecha);
            Util.createExcelCellRep(2, rw, rs, rsMetadata.getColumnName(3), rsMetadata.getColumnType(3), estiloTabla);
            Util.createExcelCellRep(3, rw, rs, rsMetadata.getColumnName(4), rsMetadata.getColumnType(4), estiloTabla);
            Util.createExcelCellRep(4, rw, rs, rsMetadata.getColumnName(5), rsMetadata.getColumnType(5), estiloTabla);
            Util.createExcelCellRep(5, rw, rs, rsMetadata.getColumnName(6), rsMetadata.getColumnType(6), estiloTabla);
            renglonInicio++;
        }
        File fsalida = new File(file_name);
        FileOutputStream fos = new FileOutputStream(fsalida);
        BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
        workbook.write(bos.toPath());
        /* Cierra Flujos */
        workbook.close();
        bos.flush();
        bos.close();
        fos.close();
        return file_name;
    }

    public static String generaReporteCompromisosSICOPManager(Connection conn, Integer tipo, Map<String, String> plantillas) throws Exception {
        Statement pstmt = null;
        ResultSet rs = null;
        String query = "SELECT * FROM vCompromisosSicop ORDER BY caNoCompromiso";
        System.out.print(query);
        String fileName = "";
        try {
            pstmt = conn.createStatement();
            rs = pstmt.executeQuery(query);
            fileName = generaReporteCompromisoSICOP(rs, plantillas.get("COMPROMISOSSICOP"));
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(pstmt, false);
        }
    }

    private static String generaReporteCompromisoSICOP(ResultSet rs, String plantillaPath) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "ReporteCompromisosSicop" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        int renglonInicio = 3;
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
        DataFormat df = workbook.createDataFormat();
        CellStyle estiloFecha = workbook.createCellStyle();
        estiloFecha.setBorderRight(BorderStyle.HAIR);
        estiloFecha.setBorderLeft(BorderStyle.HAIR);
        estiloFecha.setBorderTop(BorderStyle.HAIR);
        estiloFecha.setBorderBottom(BorderStyle.HAIR);
        estiloFecha.setFont(font);
        estiloFecha.setDataFormat(df.getFormat("dd/mm/yyyy"));
        CellStyle estilodato = workbook.createCellStyle();
        estilodato.setFont(font);
        while (rs.next()) {
            Row rw = (sheet0.getRow(renglonInicio) == null ? sheet0.createRow(renglonInicio) : sheet0.getRow(renglonInicio));
            Util.createExcelCellRep(0, rw, rs, rsMetadata.getColumnName(1), rsMetadata.getColumnType(1), estiloTabla);
            Util.createExcelCellRep(1, rw, rs, rsMetadata.getColumnName(2), rsMetadata.getColumnType(2), estiloFecha);
            Util.createExcelCellRep(2, rw, rs, rsMetadata.getColumnName(3), rsMetadata.getColumnType(3), estiloTabla);
            Util.createExcelCellRep(3, rw, rs, rsMetadata.getColumnName(4), rsMetadata.getColumnType(4), estiloTabla);
            Util.createExcelCellRep(4, rw, rs, rsMetadata.getColumnName(5), rsMetadata.getColumnType(5), estiloTabla);
            Util.createExcelCellRep(5, rw, rs, rsMetadata.getColumnName(6), rsMetadata.getColumnType(6), estiloTabla);
            Util.createExcelCellRep(6, rw, rs, rsMetadata.getColumnName(7), rsMetadata.getColumnType(7), estiloTabla);
            Util.createExcelCellRep(7, rw, rs, rsMetadata.getColumnName(8), rsMetadata.getColumnType(8), estiloTabla);
            renglonInicio++;
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

    public static String generaReporteCompromisosSICOPDuplicadosManager(Connection conn, Integer tipo, Map<String, String> plantillas) throws Exception {
        Statement pstmt = null;
        ResultSet rs = null;
        String query = "SELECT * FROM dbo.vCompromisosDuplicadosSICOP ORDER BY caNoCompromiso";
        System.out.print(query);
        String fileName = "";
        try {
            pstmt = conn.createStatement();
            rs = pstmt.executeQuery(query);
            fileName = generaReporteCompromisoSICOP(rs, plantillas.get("COMPROMISOSSICOP"));
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(pstmt, false);
        }
    }

    public static String getFileName(Connection conn, String cxp) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder query = new StringBuilder();
        query.append("SELECT SUBSTRING(CONVERT(VARCHAR(4), YEAR(GETDATE())),3,2) ");
        query.append(" + CONVERT(VARCHAR(2), MONTH(GETDATE())) ");
        query.append(" + CONVERT(VARCHAR(2),DAY(GETDATE())) ");
        query.append(" + '_ R16 P_' + ISNULL(cIdProceso,CONVERT(VARCHAR(20),'[PROCESO]')) ");
        query.append(" + ' C_' + ISNULL(cCompromisoSicop,'[FOLIO]') ");
        query.append(" + ' COMPROMISO SIN CONTRATO' AS nombreArchivo ");
        query.append(" FROM vListaCompromisos V WITH (NOLOCK) ");
        query.append(" LEFT JOIN COMPROMISOS_SICOP CS WITH (NOLOCK) ON V.caNoCompromiso = CS.caNoCompromiso ");
        query.append(" WHERE V.caNoCompromiso = ? ");
        System.out.print(query);
        String fileName = "";
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, cxp);
            rs = ps.executeQuery();
            while (rs.next()) {
                fileName = rs.getString("nombreArchivo");
            }
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
    }
}
