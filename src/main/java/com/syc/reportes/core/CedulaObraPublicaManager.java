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
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import java.util.Base64;
import java.nio.file.Paths;

public class CedulaObraPublicaManager {

    public static String generaReporteCedulaObraPublica(Connection conn, Map<String, String> plantillas) throws Exception {
        PreparedStatement pstm_rst = null;
        PreparedStatement ps = null;
        ResultSet rst = null;
        ResultSet rst2 = null;
        try {
            pstm_rst = conn.prepareStatement("select * from vCedulaObraPublica WITH (NOLOCK)");
            rst = pstm_rst.executeQuery();
            String fileName = generaReporteCedulaObraPublica(rst, plantillas.get("CedulaObraPublica"));
            return fileName;
        } finally {
            CloseObject.closeObject(rst, false);
            CloseObject.closeObject(pstm_rst, false);
            CloseObject.closeObject(rst2, false);
            CloseObject.closeObject(ps, false);
        }
    }

    public static String generaReporteCedulaObraPublica(ResultSet rs, String plantillaPath) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "CedulaObraPublica" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) ((Math.random() * 100) / 3)) + ".xls";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        int cnt = 0;
        int renglonInicio = 7;
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
        String fecha = "del 1o. de enero al " + dia + " de " + mes + " de " + anio;
        Row rwfecha = (sheet0.getRow(4) == null ? sheet0.createRow(4) : sheet0.getRow(4));
        Cell celda = (rwfecha.getCell(1) == null ? rwfecha.createCell(1) : rwfecha.getCell(1));
        celda.setCellValue(fecha);
        DataFormat df = workbook.createDataFormat();
        CellStyle estiloMoneda = workbook.createCellStyle();
        estiloMoneda.setBorderRight(BorderStyle.THIN);
        estiloMoneda.setBorderLeft(BorderStyle.THIN);
        estiloMoneda.setBorderTop(BorderStyle.THIN);
        estiloMoneda.setBorderBottom(BorderStyle.THIN);
        estiloMoneda.setDataFormat(df.getFormat("#,###,##0.00"));
        CellStyle estiloTabla = workbook.createCellStyle();
        estiloTabla.setBorderRight(BorderStyle.THIN);
        estiloTabla.setBorderLeft(BorderStyle.THIN);
        estiloTabla.setBorderTop(BorderStyle.THIN);
        estiloTabla.setBorderBottom(BorderStyle.THIN);
        String nombreColumna;
        while (rs.next()) {
            Row rw = (sheet0.getRow(renglonInicio + cnt) == null ? sheet0.createRow(renglonInicio + cnt) : sheet0.getRow(renglonInicio + cnt));
            for (int i = 0; i < rsMetadata.getColumnCount(); i++) {
                nombreColumna = rsMetadata.getColumnName(i + 1);
                if (nombreColumna.equals("MontoOli") || nombreColumna.equals("MontoAnticipo") || nombreColumna.equals("MontoAmortizacion") || nombreColumna.equals("MontoSaldoAnticipo")) {
                    Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloMoneda);
                } else
                    Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
            }
            cnt++;
        }
        File fsalida = new File(file_name);
        FileOutputStream fos = new FileOutputStream(fsalida);
        BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
        workbook.write(bos);
        /* Cierra Flujos */
        bos.flush();
        bos.close();
        fos.close();
        return file_name;
    }
}
