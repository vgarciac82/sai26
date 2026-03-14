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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import com.syc.cfdi.db.CloseObject;
import com.syc.gestion.util.Util;
import java.util.Base64;
import java.nio.file.Paths;

public class ReportePagosFFMManager {

    public static String ReporteManager(Connection conn, String fechaInicio, String fechaFin, String cUR, String todos, Map<String, String> plantillas) throws Exception {
        PreparedStatement pstmntQueryReporteFFM = null, pstmntUR = null;
        ResultSet rsQueryReporteFFM = null, rsUR = null;
        String query = "", filtroUR = "";
        query = "SELECT  Cuenta , " + "		 Programa , " + "		 Subprograma , " + "		 cEjercicio , " + "		 importeTotal , " + "		 CONVERT(VARCHAR(10),fAplicacion ,103) fAplicacion , " + "		 Documento , " + "		 Poliza , " + "		 UR , " + "		 EP , " + "		 mImporteNeto " + "FROM vReporteFFM WITH (NOLOCK)";
        try {
            if (todos.equals("SI"))
                query += " WHERE fAplicacion >= '" + fechaInicio + "' AND fAplicacion <= '" + fechaFin + "'";
            else {
                query += " WHERE UR = '" + cUR + "' AND fAplicacion >= '" + fechaInicio + "' AND fAplicacion <= '" + fechaFin + "'";
                pstmntUR = conn.prepareStatement("SELECT cUnidadResponsable + ' - ' + D_DESCRIPCION D_DESCRIPCION FROM tCatUnidadResponsable WITH(NOLOCK) WHERE cUnidadResponsable = ?");
                pstmntUR.setString(1, cUR);
                rsUR = pstmntUR.executeQuery();
                if (rsUR.next())
                    filtroUR = rsUR.getString("D_DESCRIPCION");
            }
            query += " ORDER BY fAplicacion";
            String fileName = "";
            pstmntQueryReporteFFM = conn.prepareStatement(query);
            rsQueryReporteFFM = pstmntQueryReporteFFM.executeQuery();
            fileName = generaReporte(rsQueryReporteFFM, plantillas.get("PAGOSFFM"), fechaInicio, fechaFin, filtroUR);
            return fileName;
        } finally {
            CloseObject.closeObject(pstmntQueryReporteFFM, false);
            CloseObject.closeObject(rsQueryReporteFFM, false);
            CloseObject.closeObject(pstmntUR, false);
            CloseObject.closeObject(rsUR, false);
        }
    }

    private static String generaReporte(ResultSet rsQueryReporteFFM, String plantillaPath, String fechaInicio, String fechaFin, String filtroUR) throws Exception {
        SimpleDateFormat fecha = new SimpleDateFormat("yyyyMMddhhmm");
        String sufijo = fecha.format(new Date(System.currentTimeMillis()));
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "ReportePagosFFM" + "_" + sufijo + ".xls";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        int cnt = 0;
        ResultSetMetaData rsMetadata = rsQueryReporteFFM.getMetaData();
        int renglonInicio = 7;
        String periodo = "DETALLE DE RECURSOS DEPOSITADOS DEL " + fechaInicio + " AL " + fechaFin;
        Row rwEnc2 = (sheet0.getRow(3) == null ? sheet0.createRow(3) : sheet0.getRow(3));
        Cell cell2 = (rwEnc2.getCell(0) == null ? rwEnc2.createCell(0) : rwEnc2.getCell(0));
        cell2.setCellValue(periodo);
        rwEnc2 = (sheet0.getRow(4) == null ? sheet0.createRow(4) : sheet0.getRow(4));
        cell2 = (rwEnc2.getCell(0) == null ? rwEnc2.createCell(0) : rwEnc2.getCell(0));
        cell2.setCellValue(filtroUR);
        CellStyle estiloTabla = workbook.createCellStyle();
        estiloTabla.setBorderRight(BorderStyle.HAIR);
        estiloTabla.setBorderLeft(BorderStyle.HAIR);
        estiloTabla.setBorderTop(BorderStyle.HAIR);
        estiloTabla.setBorderBottom(BorderStyle.HAIR);
        while (rsQueryReporteFFM.next()) {
            Row rw = (sheet0.getRow(renglonInicio + cnt) == null ? sheet0.createRow(renglonInicio + cnt) : sheet0.getRow(renglonInicio + cnt));
            for (int i = 0; i < rsMetadata.getColumnCount(); i++) {
                Util.createExcelCellRep(i, rw, rsQueryReporteFFM, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
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
