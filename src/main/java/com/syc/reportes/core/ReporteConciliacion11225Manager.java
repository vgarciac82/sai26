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
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.obrapublica.ConfiguraAplicativoBusinessLogic;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import java.util.Base64;
import java.nio.file.Paths;

public class ReporteConciliacion11225Manager {

    private static ConfiguraAplicativoBusinessLogic cabl = new ConfiguraAplicativoBusinessLogic(GestionInterface.ATT_CONEXION);

    private static String tipoFuente = cabl.getSystemSetting("FUENTE_INSTITUCIONAL");

    public static String generaReporteConciliacion(Connection conn, String mes, String saldo, Map<String, String> Plantilla) throws Exception {
        CallableStatement cs = null, cs2 = null;
        ResultSet rs = null, rs2 = null;
        CallableStatement cs_SIAFF = null;
        ResultSet rs_SIAFF = null;
        String fileName = "";
        String query = "{call sp_Conciliacion11225( ? )}";
        String query2 = "{call sp_l_GeneraAuxiliarMayor ('11225-00000-00000-00000', '%','00', ?, ?, 0 )}";
        String querySIAFF = "{call sp_l_Conciliacion11225SIAFF( ? )}";
        try {
            cs = conn.prepareCall(query);
            cs.setString(1, mes);
            rs = cs.executeQuery();
            cs2 = conn.prepareCall(query2);
            cs2.setString(1, mes);
            cs2.setString(2, mes);
            rs2 = cs2.executeQuery();
            cs_SIAFF = conn.prepareCall(querySIAFF);
            cs_SIAFF.setString(1, mes);
            rs_SIAFF = cs_SIAFF.executeQuery();
            fileName = generaReporte11225(conn, rs, rs2, rs_SIAFF, Plantilla.get("PCONCILIACION11225"), mes, saldo);
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(cs, false);
            CloseObject.closeObject(rs2, false);
            CloseObject.closeObject(cs2, false);
            CloseObject.closeObject(rs_SIAFF, false);
            CloseObject.closeObject(cs_SIAFF, false);
        }
    }

    public static String generaReporte11225(Connection conn, ResultSet rs, ResultSet rs2, ResultSet rs_SIAFF, String plantillaPath, String mes, String saldo) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "ReporteConciliacion11225" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        Sheet sheet1 = workbook.getSheetAt(1);
        Sheet sheet2 = workbook.getSheetAt(2);
        int nMes = Integer.parseInt(mes) - 1;
        int cnt_c = 19;
        int cnt2 = 10;
        int cnt_a = 13;
        int cntf = 43;
        int cntSIAFF = 5;
        double cargos = 0, abonos = 0, saldoFinal = 0, saldoInicial = 0, saldoAux = 0, saldoSIAFF = 0;
        ResultSetMetaData rsMetadata = rs.getMetaData();
        ResultSetMetaData rs2Metadata = rs2.getMetaData();
        ResultSetMetaData rsSIAFFMetadata = rs_SIAFF.getMetaData();
        Font font = workbook.createFont();
        font.setFontName(tipoFuente);
        font.setFontHeightInPoints((short) 10);
        DataFormat df = workbook.createDataFormat();
        CellStyle estiloMoneda = workbook.createCellStyle();
        estiloMoneda.setBorderRight(BorderStyle.HAIR);
        estiloMoneda.setBorderLeft(BorderStyle.HAIR);
        estiloMoneda.setBorderTop(BorderStyle.HAIR);
        estiloMoneda.setBorderBottom(BorderStyle.HAIR);
        estiloMoneda.setDataFormat(df.getFormat("#,###,##0.00"));
        estiloMoneda.setFont(font);
        Row rwEnc1 = (sheet0.getRow(8) == null ? sheet0.createRow(8) : sheet0.getRow(8));
        Cell cell1 = (rwEnc1.getCell(9) == null ? rwEnc1.createCell(9) : rwEnc1.getCell(9));
        cell1.setCellValue(Double.parseDouble(saldo));
        cell1.setCellStyle(estiloMoneda);
        CellStyle estiloTabla = workbook.createCellStyle();
        estiloTabla.setBorderRight(BorderStyle.HAIR);
        estiloTabla.setBorderLeft(BorderStyle.HAIR);
        estiloTabla.setBorderTop(BorderStyle.HAIR);
        estiloTabla.setBorderBottom(BorderStyle.HAIR);
        estiloTabla.setFont(font);
        while (rs.next()) {
            if (rs.getString("MOV").equals("A")) {
                Row rw = (sheet0.getRow(cnt_a) == null ? sheet0.createRow(cnt_a) : sheet0.getRow(cnt_a));
                Util.createExcelCellRep(1, rw, rs, rsMetadata.getColumnName(1), rsMetadata.getColumnType(1), estiloTabla);
                Util.createExcelCellRep(2, rw, rs, rsMetadata.getColumnName(2), rsMetadata.getColumnType(2), estiloTabla);
                Util.createExcelCellRep(3, rw, rs, rsMetadata.getColumnName(3), rsMetadata.getColumnType(3), estiloTabla);
                Util.createExcelCellRep(4, rw, rs, rsMetadata.getColumnName(4), rsMetadata.getColumnType(4), estiloTabla);
                Util.createExcelCellRep(5, rw, rs, rsMetadata.getColumnName(5), rsMetadata.getColumnType(5), estiloTabla);
                Util.createExcelCellRep(6, rw, rs, rsMetadata.getColumnName(6), rsMetadata.getColumnType(6), estiloTabla);
                Util.createExcelCellRep(7, rw, rs, rsMetadata.getColumnName(7), rsMetadata.getColumnType(7), estiloTabla);
                Util.createExcelCellRep(8, rw, rs, rsMetadata.getColumnName(8), rsMetadata.getColumnType(8), estiloMoneda);
                sheet0.shiftRows(cnt_a + 1, cntf + 1, 1, true, true);
                cnt_a++;
                cnt_c++;
                cntf++;
                abonos += rs.getDouble("IMPORTE");
            } else {
                Row rw = (sheet0.getRow(cnt_c) == null ? sheet0.createRow(cnt_c) : sheet0.getRow(cnt_c));
                Util.createExcelCellRep(1, rw, rs, rsMetadata.getColumnName(1), rsMetadata.getColumnType(1), estiloTabla);
                Util.createExcelCellRep(2, rw, rs, rsMetadata.getColumnName(2), rsMetadata.getColumnType(2), estiloTabla);
                Util.createExcelCellRep(3, rw, rs, rsMetadata.getColumnName(3), rsMetadata.getColumnType(3), estiloTabla);
                Util.createExcelCellRep(4, rw, rs, rsMetadata.getColumnName(4), rsMetadata.getColumnType(4), estiloTabla);
                Util.createExcelCellRep(5, rw, rs, rsMetadata.getColumnName(5), rsMetadata.getColumnType(5), estiloTabla);
                Util.createExcelCellRep(6, rw, rs, rsMetadata.getColumnName(6), rsMetadata.getColumnType(6), estiloTabla);
                Util.createExcelCellRep(7, rw, rs, rsMetadata.getColumnName(7), rsMetadata.getColumnType(7), estiloTabla);
                Util.createExcelCellRep(8, rw, rs, rsMetadata.getColumnName(8), rsMetadata.getColumnType(8), estiloMoneda);
                sheet0.shiftRows(cnt_c + 1, cntf + 1, 1, true, true);
                cnt_c++;
                cntf++;
                cargos += rs.getDouble("IMPORTE");
            }
        }
        saldoFinal = Double.parseDouble(saldo) + cargos - abonos;
        Row rwEnc2 = (sheet0.getRow(12) == null ? sheet0.createRow(12) : sheet0.getRow(12));
        Cell cell2 = (rwEnc2.getCell(9) == null ? rwEnc2.createCell(9) : rwEnc2.getCell(9));
        cell2.setCellValue(abonos);
        cell2.setCellStyle(estiloMoneda);
        Row rwEnc3 = (sheet0.getRow(cnt_a + 3) == null ? sheet0.createRow(cnt_a + 3) : sheet0.getRow(cnt_a + 3));
        Cell cell3 = (rwEnc3.getCell(9) == null ? rwEnc3.createCell(9) : rwEnc3.getCell(9));
        cell3.setCellValue(cargos);
        cell3.setCellStyle(estiloMoneda);
        Row rwEnc4 = (sheet0.getRow(cnt_c + 14) == null ? sheet0.createRow(cnt_c + 15) : sheet0.getRow(cnt_c + 15));
        Cell cell4 = (rwEnc4.getCell(9) == null ? rwEnc4.createCell(9) : rwEnc4.getCell(9));
        cell4.setCellValue(saldoFinal);
        cell4.setCellStyle(estiloMoneda);
        saldoAux = saldoFinal;
        cargos = 0;
        abonos = 0;
        saldoFinal = 0;
        saldoInicial = 0;
        while (rs2.next()) {
            Row rw = (sheet1.getRow(cnt2) == null ? sheet1.createRow(cnt2) : sheet1.getRow(cnt2));
            Util.createExcelCellRep(1, rw, rs2, rs2Metadata.getColumnName(1), rs2Metadata.getColumnType(1), estiloTabla);
            Util.createExcelCellRep(2, rw, rs2, rs2Metadata.getColumnName(2), rs2Metadata.getColumnType(2), estiloTabla);
            Util.createExcelCellRep(3, rw, rs2, rs2Metadata.getColumnName(3), rs2Metadata.getColumnType(3), estiloTabla);
            Util.createExcelCellRep(4, rw, rs2, rs2Metadata.getColumnName(4), rs2Metadata.getColumnType(4), estiloTabla);
            Util.createExcelCellRep(5, rw, rs2, rs2Metadata.getColumnName(5), rs2Metadata.getColumnType(5), estiloTabla);
            Util.createExcelCellRep(6, rw, rs2, rs2Metadata.getColumnName(6), rs2Metadata.getColumnType(6), estiloTabla);
            Util.createExcelCellRep(7, rw, rs2, rs2Metadata.getColumnName(9), rs2Metadata.getColumnType(9), estiloTabla);
            Util.createExcelCellRep(8, rw, rs2, rs2Metadata.getColumnName(10), rs2Metadata.getColumnType(10), estiloTabla);
            Util.createExcelCellRep(9, rw, rs2, rs2Metadata.getColumnName(11), rs2Metadata.getColumnType(11), estiloTabla);
            Util.createExcelCellRep(10, rw, rs2, rs2Metadata.getColumnName(12), rs2Metadata.getColumnType(12), estiloTabla);
            Util.createExcelCellRep(11, rw, rs2, rs2Metadata.getColumnName(13), rs2Metadata.getColumnType(13), estiloMoneda);
            Util.createExcelCellRep(12, rw, rs2, rs2Metadata.getColumnName(14), rs2Metadata.getColumnType(14), estiloMoneda);
            cargos += rs2.getDouble("mCargo");
            abonos += rs2.getDouble("mAbono");
            cnt2++;
        }
        PreparedStatement pstm_si = null;
        ResultSet nSI = null;
        pstm_si = conn.prepareStatement("SELECT SUM(msaldo" + Integer.toString(nMes) + ") as SI FROM tSaldos (NOLOCK) WHERE nCuenta LIKE '11225%'");
        nSI = pstm_si.executeQuery();
        if (nSI.next()) {
            saldoInicial = nSI.getDouble("SI");
            saldoFinal = saldoInicial + cargos - abonos;
        }
        Row rwEnc8 = (sheet1.getRow(4) == null ? sheet1.createRow(4) : sheet1.getRow(4));
        Cell cell8 = (rwEnc8.getCell(12) == null ? rwEnc8.createCell(12) : rwEnc8.getCell(12));
        cell8.setCellValue(saldoInicial);
        cell8.setCellStyle(estiloMoneda);
        Row rwEnc5 = (sheet1.getRow(5) == null ? sheet1.createRow(5) : sheet1.getRow(5));
        Cell cell5 = (rwEnc5.getCell(12) == null ? rwEnc5.createCell(12) : rwEnc5.getCell(12));
        cell5.setCellValue(cargos);
        cell5.setCellStyle(estiloMoneda);
        Row rwEnc6 = (sheet1.getRow(6) == null ? sheet1.createRow(6) : sheet1.getRow(6));
        Cell cell6 = (rwEnc6.getCell(12) == null ? rwEnc6.createCell(12) : rwEnc6.getCell(12));
        cell6.setCellValue(abonos);
        cell6.setCellStyle(estiloMoneda);
        Row rwEnc7 = (sheet1.getRow(7) == null ? sheet1.createRow(7) : sheet1.getRow(7));
        Cell cell7 = (rwEnc7.getCell(12) == null ? rwEnc7.createCell(12) : rwEnc7.getCell(12));
        cell7.setCellValue(saldoFinal);
        cell7.setCellStyle(estiloMoneda);
        Row rwEnc9 = (sheet1.getRow(5) == null ? sheet1.createRow(5) : sheet1.getRow(5));
        Cell cell9 = (rwEnc9.getCell(3) == null ? rwEnc9.createCell(3) : rwEnc9.getCell(3));
        cell9.setCellValue(mes);
        Row rwEnc10 = (sheet0.getRow(cnt_c + 16) == null ? sheet0.createRow(cnt_c + 16) : sheet0.getRow(cnt_c + 16));
        Cell cell10 = (rwEnc10.getCell(9) == null ? rwEnc10.createCell(9) : rwEnc10.getCell(9));
        cell10.setCellValue(saldoFinal);
        cell10.setCellStyle(estiloMoneda);
        Row rwEnc11 = (sheet0.getRow(cnt_c + 17) == null ? sheet0.createRow(cnt_c + 17) : sheet0.getRow(cnt_c + 17));
        Cell cell11 = (rwEnc11.getCell(9) == null ? rwEnc11.createCell(9) : rwEnc11.getCell(9));
        cell11.setCellValue(saldoFinal - saldoAux);
        cell11.setCellStyle(estiloMoneda);
        while (rs_SIAFF.next()) {
            Row rw = (sheet2.getRow(cntSIAFF) == null ? sheet2.createRow(cntSIAFF) : sheet2.getRow(cntSIAFF));
            Util.createExcelCellRep(1, rw, rs_SIAFF, rsSIAFFMetadata.getColumnName(1), rsSIAFFMetadata.getColumnType(1), estiloTabla);
            Util.createExcelCellRep(2, rw, rs_SIAFF, rsSIAFFMetadata.getColumnName(2), rsSIAFFMetadata.getColumnType(2), estiloTabla);
            Util.createExcelCellRep(3, rw, rs_SIAFF, rsSIAFFMetadata.getColumnName(3), rsSIAFFMetadata.getColumnType(3), estiloTabla);
            Util.createExcelCellRep(4, rw, rs_SIAFF, rsSIAFFMetadata.getColumnName(4), rsSIAFFMetadata.getColumnType(4), estiloTabla);
            Util.createExcelCellRep(5, rw, rs_SIAFF, rsSIAFFMetadata.getColumnName(5), rsSIAFFMetadata.getColumnType(5), estiloTabla);
            Util.createExcelCellRep(6, rw, rs_SIAFF, rsSIAFFMetadata.getColumnName(6), rsSIAFFMetadata.getColumnType(6), estiloTabla);
            Util.createExcelCellRep(7, rw, rs_SIAFF, rsSIAFFMetadata.getColumnName(7), rsSIAFFMetadata.getColumnType(7), estiloTabla);
            Util.createExcelCellRep(8, rw, rs_SIAFF, rsSIAFFMetadata.getColumnName(8), rsSIAFFMetadata.getColumnType(8), estiloMoneda);
            saldoSIAFF += rs_SIAFF.getDouble("IMPORTE");
            cntSIAFF++;
        }
        Row rwSIAFF = (sheet2.getRow(2) == null ? sheet2.createRow(2) : sheet2.getRow(2));
        Cell cellSIAFF = (rwSIAFF.getCell(9) == null ? rwSIAFF.createCell(9) : rwSIAFF.getCell(9));
        cellSIAFF.setCellValue(saldoSIAFF);
        cellSIAFF.setCellStyle(estiloMoneda);
        CloseObject.closeObject(nSI);
        CloseObject.closeObject(pstm_si);
        File fsalida = new File(file_name);
        FileOutputStream fos = new FileOutputStream(fsalida);
        BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
        workbook.write(bos.toPath());
        workbook.close();
        /* CierraFlujos */
        bos.flush();
        bos.close();
        fos.close();
        return file_name;
    }
}
