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
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import net.sf.jasperreports.engine.JasperRunManager;
import java.util.Base64;
import java.nio.file.Paths;

public class ReporteLibrosManager {

    public static String ReporteLibroMayorManager(Connection conn, String fechaInicio, String fechaFin, Map<String, String> plantillas) throws Exception {
        CallableStatement cs = null;
        ResultSet rs = null;
        String query = "{call sp_a_reporteMomentos( ?, ? )}";
        String fileName = "";
        try {
            cs = conn.prepareCall(query);
            cs.setString(1, fechaInicio);
            cs.setString(2, fechaFin);
            rs = cs.executeQuery();
            fileName = generaReporteMasivo(rs, plantillas.get("MASIVO"), fechaInicio, fechaFin);
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(cs, false);
        }
    }

    private static String generaReporteMasivo(ResultSet rs, String plantillaPath, String fechaInicio, String fechaFin) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = "ReporteMomentos" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        int cnt = 0;
        ResultSetMetaData rsMetadata = rs.getMetaData();
        int renglonInicio = 8;
        int mesIni = 0;
        int mesFin = 0;
        String nom_mesIni = "";
        String nom_mesFin = "";
        String anio = "";
        String periodo = "";
        Sheet sheet = workbook.getSheetAt(0);
        Sheet metas = workbook.getSheetAt(1);
        mesIni = Integer.parseInt(fechaInicio.substring(3, 5));
        mesFin = Integer.parseInt(fechaFin.substring(3, 5));
        anio = fechaInicio.substring(6, 10);
        if (mesIni == mesFin)
            periodo = Util.NOMBRE_MESES_MX[mesIni - 1] + " " + anio;
        else if (mesIni != mesFin)
            periodo = "DE " + Util.NOMBRE_MESES_MX[mesIni - 1] + " A " + Util.NOMBRE_MESES_MX[mesFin - 1] + " " + anio;
        Row rwEnc2 = (sheet0.getRow(5) == null ? sheet0.createRow(5) : sheet0.getRow(5));
        Cell cell2 = (rwEnc2.getCell(1) == null ? rwEnc2.createCell(1) : rwEnc2.getCell(1));
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

    public static void ReporteLibros(Connection conn, HttpServletResponse resp, String tipoReporte, String ruta, Map<String, Object> parms) {
        FileInputStream in = null;
        ServletOutputStream out = null;
        try {
            out = resp.getOutputStream();
            in = new FileInputStream(tipoReporte);
            JasperRunManager.runReportToPdfStream(in, out, parms, conn);
            resp.setContentType("application/pdf");
            out.flush();
            out.close();
        } catch (Exception exc) {
            exc.printStackTrace();
        } finally {
            try {
                if (in != null)
                    in.close();
                if (out != null)
                    out.close();
            } catch (Exception exc) {
                exc.printStackTrace();
            }
            in = null;
            out = null;
        }
    }
}
