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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.StringTokenizer;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import java.util.Base64;
import java.nio.file.Paths;

public class Formato12Manager {

    public static String generaReporteFormato12(Connection conn, Map<String, String> plantillas) throws Exception {
        PreparedStatement pstm_rst = null;
        PreparedStatement ps = null;
        ResultSet rst = null;
        ResultSet rst2 = null;
        try {
            pstm_rst = conn.prepareStatement("select * from vformato12 WITH (NOLOCK)");
            rst = pstm_rst.executeQuery();
            String query = "SELECT SUM (CASE " + "						    WHEN (compOB.cCveOLI <>'-1' " + "						                  AND compOB.cCveOLI <>'' " + "						                     AND compOB.cTipoAdjudica='08') THEN compOB.nmontoconiva " + "						              ELSE 0 " + "						           END) AS TOTAL_Obra_I3P, " + "						          SUM (CASE " + "					                  WHEN ((cCveOLI ='-1' " + "							                         OR cCveOLI ='') " + "						                         AND cTipoAdjudica='08') THEN compOB.nmontoconiva " + "							                    ELSE 0 " + "							                END) AS TOTAL_Servicios_I3P, " + "							               SUM (CASE " + "							                        WHEN (compOB.cCveOLI <>'-1' " + "							                              AND compOB.cCveOLI <>'' " + "							                              AND compOB.cTipoAdjudica='23') THEN compOB.nmontoconiva " + "							                        ELSE 0 " + "							                    END) AS TOTAL_Obra_AD, " + "							                   SUM (CASE " + "							                            WHEN ((cCveOLI ='-1' " + "							                                   OR cCveOLI ='') " + "							                                  AND cTipoAdjudica='23') THEN compOB.nmontoconiva " + "							                            ELSE 0 " + "							                        END) AS TOTAL_Servicios_AD, " + "							                         SUM (compOB.nmontoconiva) as monto_total " + "							FROM tobrapublicacompromisoencabezado compOB WITH (NOLOCK) " + "							WHERE cDocumentoHaplicado='S'";
            ps = conn.prepareStatement(query);
            rst2 = ps.executeQuery();
            String fileName = generaReporteFormato12(rst, rst2, plantillas.get("Fmto12"));
            return fileName;
        } finally {
            CloseObject.closeObject(rst, false);
            CloseObject.closeObject(pstm_rst, false);
            CloseObject.closeObject(rst2, false);
            CloseObject.closeObject(ps, false);
        }
    }

    public static String generaReporteFormato12(ResultSet rs, ResultSet rst2, String plantillaPath) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "Formato12" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        int cnt = 0;
        int renglonInicio = 10;
        ResultSetMetaData rsMetadata = rs.getMetaData();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date();
        String datef = dateFormat.format(date);
        StringTokenizer tokens = new StringTokenizer(datef, "-");
        String dia = tokens.nextToken();
        int nMes = Integer.parseInt(tokens.nextToken());
        String mes;
        String anio = tokens.nextToken();
        switch(nMes) {
            case 1:
                mes = "enero";
                break;
            case 2:
                mes = "febrero";
                break;
            case 3:
                mes = "marzo";
                break;
            case 4:
                mes = "abril";
                break;
            case 5:
                mes = "mayo";
                break;
            case 6:
                mes = "junio";
                break;
            case 7:
                mes = "julio";
                break;
            case 8:
                mes = "agosto";
                break;
            case 9:
                mes = "septiembre";
                break;
            case 10:
                mes = "octubre";
                break;
            case 11:
                mes = "noviembre";
                break;
            case 12:
                mes = "diciembre";
                break;
            default:
                mes = nMes + "";
                break;
        }
        String fecha = "Período: 1o. de enero al " + dia + " de " + mes + " de " + anio;
        Row rwfecha = (sheet0.getRow(2) == null ? sheet0.createRow(2) : sheet0.getRow(2));
        Cell celda = (rwfecha.getCell(0) == null ? rwfecha.createCell(0) : rwfecha.getCell(0));
        celda.setCellValue(fecha);
        DataFormat df = workbook.createDataFormat();
        CellStyle estiloMoneda = workbook.createCellStyle();
        estiloMoneda.setBorderRight(BorderStyle.THIN);
        estiloMoneda.setBorderLeft(BorderStyle.THIN);
        estiloMoneda.setBorderTop(BorderStyle.THIN);
        estiloMoneda.setBorderBottom(BorderStyle.THIN);
        estiloMoneda.setDataFormat(df.getFormat("#,###,##0.00"));
        CellStyle estiloTotal = workbook.createCellStyle();
        estiloTotal.setBorderRight(BorderStyle.THIN);
        estiloTotal.setBorderLeft(BorderStyle.THIN);
        estiloTotal.setBorderTop(BorderStyle.THIN);
        estiloTotal.setBorderBottom(BorderStyle.THIN);
        estiloTotal.setDataFormat(df.getFormat("#,###,##0.00"));
        estiloTotal.setAlignment(HorizontalAlignment.CENTER);
        CellStyle estiloTabla = workbook.createCellStyle();
        estiloTabla.setBorderRight(BorderStyle.THIN);
        estiloTabla.setBorderLeft(BorderStyle.THIN);
        estiloTabla.setBorderTop(BorderStyle.THIN);
        estiloTabla.setBorderBottom(BorderStyle.THIN);
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 6);
        CellStyle estiloX = workbook.createCellStyle();
        estiloX.setBorderRight(BorderStyle.THIN);
        estiloX.setBorderLeft(BorderStyle.THIN);
        estiloX.setBorderTop(BorderStyle.THIN);
        estiloX.setBorderBottom(BorderStyle.THIN);
        estiloX.setAlignment(HorizontalAlignment.CENTER);
        //double suma=0;
        int lp = 0, i3p = 0, ad = 0;
        double monto_total = 0;
        if (rst2.next()) {
            double tOI3P = rst2.getDouble("TOTAL_Obra_I3P");
            double tSI3P = rst2.getDouble("TOTAL_Servicios_I3P");
            double tOAD = rst2.getDouble("TOTAL_Obra_AD");
            double tSAD = rst2.getDouble("TOTAL_Servicios_AD");
            monto_total = rst2.getDouble("monto_total");
            Row ri3p = (sheet0.getRow(17) == null ? sheet0.createRow(17) : sheet0.getRow(17));
            Cell celdari3p = (ri3p.getCell(4) == null ? ri3p.createCell(4) : ri3p.getCell(4));
            celdari3p.setCellStyle(estiloTotal);
            celdari3p.setCellValue(tOI3P);
            Row rsi3p = (sheet0.getRow(17) == null ? sheet0.createRow(17) : sheet0.getRow(17));
            Cell celdarsi3p = (rsi3p.getCell(6) == null ? rsi3p.createCell(6) : rsi3p.getCell(6));
            celdarsi3p.setCellStyle(estiloTotal);
            celdarsi3p.setCellValue(tSI3P);
            Row road = (sheet0.getRow(18) == null ? sheet0.createRow(18) : sheet0.getRow(18));
            Cell celdaroad = (road.getCell(4) == null ? road.createCell(4) : road.getCell(4));
            celdaroad.setCellStyle(estiloTotal);
            celdaroad.setCellValue(tOAD);
            Row rsad = (sheet0.getRow(18) == null ? sheet0.createRow(18) : sheet0.getRow(18));
            Cell celdarsad = (rsad.getCell(6) == null ? rsad.createCell(6) : rsad.getCell(6));
            celdarsad.setCellStyle(estiloTotal);
            celdarsad.setCellValue(tSAD);
        }
        while (rs.next()) {
            int rows = renglonInicio + cnt;
            sheet0.shiftRows(rows, sheet0.getLastRowNum(), 1);
            Row rw = (sheet0.getRow(renglonInicio + cnt) == null ? sheet0.createRow(renglonInicio + cnt) : sheet0.getRow(renglonInicio + cnt));
            for (int i = 0; i < rsMetadata.getColumnCount(); i++) {
                if (rsMetadata.getColumnName(i + 1).equals("nmontoconiva")) {
                    //suma=suma+((double)((int)(Double.parseDouble(rs.getString("nmontoconiva"))*100)))/100;
                    Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloMoneda);
                } else if (rsMetadata.getColumnName(i + 1).equals("LP")) {
                    if (rs.getString("LP").equalsIgnoreCase("X"))
                        lp++;
                    Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloX);
                } else if (rsMetadata.getColumnName(i + 1).equals("I3P")) {
                    if (rs.getString("I3P").equalsIgnoreCase("X"))
                        i3p++;
                    Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloX);
                } else if (rsMetadata.getColumnName(i + 1).equals("AD")) {
                    if (rs.getString("AD").equalsIgnoreCase("X"))
                        ad++;
                    Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloX);
                } else {
                    Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
                }
            }
            cnt++;
        }
        Row slp = (sheet0.getRow(cnt + 10) == null ? sheet0.createRow(cnt + 10) : sheet0.getRow(cnt + 10));
        Cell celdaslp = (slp.getCell(1) == null ? slp.createCell(1) : slp.getCell(1));
        celdaslp.setCellStyle(estiloX);
        celdaslp.setCellValue(lp);
        Row si3p = (sheet0.getRow(cnt + 10) == null ? sheet0.createRow(cnt + 10) : sheet0.getRow(cnt + 10));
        Cell celdasi3p = (si3p.getCell(2) == null ? si3p.createCell(2) : si3p.getCell(2));
        celdasi3p.setCellStyle(estiloX);
        celdasi3p.setCellValue(i3p);
        Row sad = (sheet0.getRow(cnt + 10) == null ? sheet0.createRow(cnt + 10) : sheet0.getRow(cnt + 10));
        Cell celdasad = (sad.getCell(3) == null ? sad.createCell(3) : sad.getCell(3));
        celdasad.setCellStyle(estiloX);
        celdasad.setCellValue(ad);
        Row ssuma = (sheet0.getRow(cnt + 10) == null ? sheet0.createRow(cnt + 10) : sheet0.getRow(cnt + 10));
        Cell celdassuma = (ssuma.getCell(8) == null ? ssuma.createCell(8) : ssuma.getCell(8));
        celdassuma.setCellStyle(estiloMoneda);
        celdassuma.setCellValue(monto_total);
        Row ssumatipo = (sheet0.getRow(cnt + 11) == null ? sheet0.createRow(cnt + 11) : sheet0.getRow(cnt + 11));
        Cell celdassumatipo = (ssumatipo.getCell(1) == null ? ssumatipo.createCell(1) : ssumatipo.getCell(1));
        celdassumatipo.setCellValue(lp + i3p + ad);
        File fsalida = new File(file_name);
        FileOutputStream fos = new FileOutputStream(fsalida);
        BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
        workbook.write(bos.toPath());
        /* Cierra Flujos */
        bos.flush();
        bos.close();
        fos.close();
        return file_name;
    }
}
