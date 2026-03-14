package com.syc.reportes.core;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Map;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
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

@SuppressWarnings("unused")
public class ReportePlurianualesManager {

    public static String ReportePlurianualManager(Connection conn, String trimestre, String anio, Map<String, String> plantillas) throws Exception {
        CallableStatement cs = null;
        ResultSet rs = null;
        int mes = Integer.parseInt(trimestre);
        int a = Integer.parseInt(anio);
        String query = "{CALL dbo.sp_Plurianuales_Ejercido ( ?, ? )}";
        String fileName = "";
        try {
            cs = conn.prepareCall(query);
            cs.setInt(1, a);
            cs.setInt(2, mes);
            rs = cs.executeQuery();
            fileName = generaReporte(rs, plantillas.get("ContratosPlurianuales"), mes, a);
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(cs, false);
        }
    }

    private static String generaReporte(ResultSet rs, /*ResultSet rs2,*/
    String plantillaPath, int mesIni, int anio) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "ReportePlurianuales" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        int renglonInicio = 38;
        String Encabezado = null;
        String MesTrabajo = null;
        if (mesIni == 3) {
            Encabezado = "Enero - Marzo de " + anio;
            MesTrabajo = "Enero - Marzo";
        } else if (mesIni == 6) {
            Encabezado = "Abril - Junio de " + anio;
            MesTrabajo = "Abril - Junio";
        } else if (mesIni == 9) {
            Encabezado = "Julio - Septiembre de " + anio;
            MesTrabajo = "Julio - Septiembre";
        } else if (mesIni == 12) {
            Encabezado = "Octubre - Diciembre de " + anio;
            MesTrabajo = "Octubre - Diciembre";
        }
        Row rwEnc1 = (sheet0.getRow(4) == null ? sheet0.createRow(4) : sheet0.getRow(4));
        Cell cell1 = (rwEnc1.getCell(0) == null ? rwEnc1.createCell(0) : rwEnc1.getCell(0));
        cell1.setCellValue(Encabezado);
        Row rwEnc2 = (sheet0.getRow(17) == null ? sheet0.createRow(17) : sheet0.getRow(17));
        Cell cell2 = (rwEnc2.getCell(4) == null ? rwEnc2.createCell(4) : rwEnc2.getCell(4));
        cell2.setCellValue(MesTrabajo);
        sheet0 = Util.resultSetToExcel(rs, sheet0, renglonInicio, 5, 1, false);
        sheet0 = Util.EvaluaFormula(workbook, sheet0, 20, 59, 1, 7);
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
