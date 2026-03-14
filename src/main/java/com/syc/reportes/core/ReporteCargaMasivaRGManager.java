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
import java.util.Map;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import com.syc.gestion.util.Util;
import java.util.Base64;

public class ReporteCargaMasivaRGManager {

    public static String ReporteManager(Connection conn, String fechaInicio, String fechaFin, String cUR, String todos, String cDestG, String todosDest, Map<String, String> plantillas) throws Exception {
        PreparedStatement pstmnt, pstmnt2 = null;
        ResultSet rs, rs2 = null;
        String query = "";
        String destinoGasto = "";
        query = "SELECT  fAplicacion," + " cUnidadResponsable," + " caNoContrarrecibo," + " mImporteMasIva," + " cIdRFC," + " cnombre," + " cIdRelacion," + " cConcepto," + " ID_DESTINO_GASTO," + " DESTINO_GASTO," + " cIdUsuarioCaptura " + "FROM vRelacionGastosCargaMasiva WITH (NOLOCK) WHERE";
        if (todos.equals("NO"))
            query += " cUnidadResponsable = '" + cUR + "' AND ";
        if (todosDest.equals("NO")) {
            query += " ID_DESTINO_GASTO = '" + cDestG + "' AND ";
            pstmnt2 = conn.prepareStatement("SELECT TOP 1 ID_DESTINO_GASTO + ' - ' + DESTINO_GASTO AS DESTINO FROM CAT_DESTINO_GASTO WITH (NOLOCK) WHERE ID_DESTINO_GASTO = '" + cDestG + "'");
            rs = pstmnt2.executeQuery();
            if (rs.next()) {
                destinoGasto = rs.getString("DESTINO");
            }
        }
        query += " fAplicacion >= '" + fechaInicio + "' AND fAplicacion <= '" + fechaFin + "' ORDER BY cUnidadResponsable, fAplicacion,caNoContrarrecibo";
        String fileName = "";
        pstmnt = conn.prepareStatement(query);
        rs = pstmnt.executeQuery();
        try {
            fileName = generaReporte(rs, plantillas.get("REPORTECARGAMASIVARG"), fechaInicio, fechaFin, destinoGasto);
            return fileName;
        } finally {
            if (pstmnt != null) {
                pstmnt.close();
            }
            if (pstmnt2 != null) {
                pstmnt2.close();
            }
            if (rs != null) {
                rs.close();
            }
            if (rs2 != null) {
                rs2.close();
            }
            pstmnt = null;
            pstmnt2 = null;
            rs = null;
            rs2 = null;
        }
    }

    private static String generaReporte(ResultSet rs, String plantillaPath, String fechaInicio, String fechaFin, String destinoGasto) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "ReporteCargaMasivaRG" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        int cnt = 0;
        ResultSetMetaData rsMetadata = rs.getMetaData();
        int renglonInicio = 7;
        String periodo = "DEL " + fechaInicio + " AL " + fechaFin;
        Row rwEnc2 = (sheet0.getRow(3) == null ? sheet0.createRow(3) : sheet0.getRow(3));
        Cell cell2 = (rwEnc2.getCell(0) == null ? rwEnc2.createCell(0) : rwEnc2.getCell(0));
        cell2.setCellValue(destinoGasto);
        rwEnc2 = (sheet0.getRow(4) == null ? sheet0.createRow(4) : sheet0.getRow(4));
        cell2 = (rwEnc2.getCell(0) == null ? rwEnc2.createCell(0) : rwEnc2.getCell(0));
        cell2.setCellValue(periodo);
        CellStyle estiloTabla = workbook.createCellStyle();
        estiloTabla.setBorderRight(BorderStyle.HAIR);
        estiloTabla.setBorderLeft(BorderStyle.HAIR);
        estiloTabla.setBorderTop(BorderStyle.HAIR);
        estiloTabla.setBorderBottom(BorderStyle.HAIR);
        while (rs.next()) {
            Row rw = (sheet0.getRow(renglonInicio + cnt) == null ? sheet0.createRow(renglonInicio + cnt) : sheet0.getRow(renglonInicio + cnt));
            for (int i = 0; i < rsMetadata.getColumnCount(); i++) {
                Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
            }
            cnt++;
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
}
