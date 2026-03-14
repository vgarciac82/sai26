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
import java.text.DecimalFormat;
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

public class ConciliacionGastoDevengadoManager {

    public static String GastoDevengadoManager(Connection conn, String fechaInicio, Map<String, String> plantillas) throws Exception {
        CallableStatement cs = null, cs2 = null;
        ResultSet rs = null, rs2 = null;
        int mes = Integer.parseInt(fechaInicio.substring(3, 5));
        int anio = Integer.parseInt(fechaInicio.substring(6, 10));
        String query = "{call sp_conciliacionGastoDevengado( ?, ? )}";
        String query2 = "{call sp_conciliacionGastoDevengado_C( ?, ? )}";
        String fileName = "";
        try {
            cs = conn.prepareCall(query);
            cs.setInt(1, mes);
            cs.setInt(2, anio);
            rs = cs.executeQuery();
            cs2 = conn.prepareCall(query2);
            cs2.setInt(1, mes);
            cs2.setInt(2, anio);
            rs2 = cs2.executeQuery();
            fileName = generaReporte(conn, rs, rs2, plantillas.get("ConciliaGastoDevengado"), mes, anio);
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(rs2, false);
            CloseObject.closeObject(cs, false);
            CloseObject.closeObject(cs2, false);
        }
    }

    @SuppressWarnings("null")
    private static String generaReporte(Connection conn, ResultSet rs, ResultSet rs2, String plantillaPath, int mes, int anio) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "ConciliacionGTOvsDEV" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        ResultSetMetaData rsMetadata = rs.getMetaData();
        ResultSetMetaData rs2Metadata = rs2.getMetaData();
        String periodo = "";
        String cuenta = "";
        double cargo113 = 0;
        double abono113 = 0;
        double saldo113 = 0;
        double cargo115 = 0;
        double abono115 = 0;
        double saldo115 = 0;
        double cargo1213 = 0;
        double abono1213 = 0;
        double saldo1213 = 0;
        double cargo123 = 0;
        double cargo124 = 0;
        double cargo1125 = 0;
        double cargo523 = 0;
        double saldo8251 = 0;
        double saldoreint = 0;
        double devNeto = 0;
        double saldoDev = 0;
        double saldo5 = 0;
        double saldo551 = 0;
        double saldo559 = 0;
        double saldoGasto = 0;
        double totalGasto = 0;
        double dif = 0;
        int cta113 = 11;
        int cta115 = 16;
        int cta1213 = 21;
        int cta123 = 26;
        int cta124 = 31;
        //int cta125 = 36;
        int cta1271 = 41;
        int cta1125 = 46;
        int cta523 = 51;
        periodo = Util.NOMBRE_MESES_MX[mes - 1] + " " + anio;
        Row rwEnc2 = (sheet0.getRow(5) == null ? sheet0.createRow(5) : sheet0.getRow(5));
        Cell cell2 = (rwEnc2.getCell(1) == null ? rwEnc2.createCell(1) : rwEnc2.getCell(1));
        cell2.setCellValue(periodo);
        CellStyle estiloTabla = workbook.createCellStyle();
        estiloTabla.setBorderRight(BorderStyle.HAIR);
        estiloTabla.setBorderLeft(BorderStyle.HAIR);
        estiloTabla.setBorderTop(BorderStyle.HAIR);
        estiloTabla.setBorderBottom(BorderStyle.HAIR);
        DataFormat df = workbook.createDataFormat();
        CellStyle estiloMoneda = workbook.createCellStyle();
        estiloMoneda.setBorderRight(BorderStyle.HAIR);
        estiloMoneda.setBorderLeft(BorderStyle.HAIR);
        estiloMoneda.setBorderTop(BorderStyle.HAIR);
        estiloMoneda.setBorderBottom(BorderStyle.HAIR);
        estiloMoneda.setDataFormat(df.getFormat("#,###,##0.00"));
        DecimalFormat formateador = new DecimalFormat("#,###,###.00");
        while (rs.next()) {
            cuenta = rs.getString(1);
            if ("113".equals(cuenta)) {
                Row rw = (sheet0.getRow(cta113) == null ? sheet0.getRow(cta113) : sheet0.getRow(cta113));
                Util.createExcelCellRep(1, rw, rs, rsMetadata.getColumnName(2), rsMetadata.getColumnType(2), estiloTabla);
                Util.createExcelCellRep(2, rw, rs, rsMetadata.getColumnName(3), rsMetadata.getColumnType(3), estiloTabla);
                Util.createExcelCellRep(3, rw, rs, rsMetadata.getColumnName(4), rsMetadata.getColumnType(4), estiloTabla);
                Util.createExcelCellRep(4, rw, rs, rsMetadata.getColumnName(5), rsMetadata.getColumnType(5), estiloTabla);
                Util.createExcelCellRep(5, rw, rs, rsMetadata.getColumnName(6), rsMetadata.getColumnType(6), estiloTabla);
                Util.createExcelCellRep(6, rw, rs, rsMetadata.getColumnName(7), rsMetadata.getColumnType(7), estiloTabla);
                Util.createExcelCellRep(7, rw, rs, rsMetadata.getColumnName(8), rsMetadata.getColumnType(8), estiloMoneda);
                sheet0.shiftRows(cta113 + 1, cta523 + 1, 1, true, true);
                cargo113 += rs.getDouble("cargo");
                abono113 += rs.getDouble("abono");
                cta113++;
                cta115++;
                cta1213++;
                cta123++;
                cta124++;
                //cta125 ++;
                cta1271++;
                cta1125++;
                cta523++;
            } else if ("115".equals(cuenta)) {
                Row rw = (sheet0.getRow(cta115) == null ? sheet0.getRow(cta115) : sheet0.getRow(cta115));
                Util.createExcelCellRep(1, rw, rs, rsMetadata.getColumnName(2), rsMetadata.getColumnType(2), estiloTabla);
                Util.createExcelCellRep(2, rw, rs, rsMetadata.getColumnName(3), rsMetadata.getColumnType(3), estiloTabla);
                Util.createExcelCellRep(3, rw, rs, rsMetadata.getColumnName(4), rsMetadata.getColumnType(4), estiloTabla);
                Util.createExcelCellRep(4, rw, rs, rsMetadata.getColumnName(5), rsMetadata.getColumnType(5), estiloTabla);
                Util.createExcelCellRep(5, rw, rs, rsMetadata.getColumnName(6), rsMetadata.getColumnType(6), estiloTabla);
                Util.createExcelCellRep(6, rw, rs, rsMetadata.getColumnName(7), rsMetadata.getColumnType(7), estiloTabla);
                Util.createExcelCellRep(7, rw, rs, rsMetadata.getColumnName(8), rsMetadata.getColumnType(8), estiloMoneda);
                sheet0.shiftRows(cta115 + 1, cta523 + 1, 1, true, true);
                cargo115 += rs.getDouble("cargo");
                abono115 += rs.getDouble("abono");
                cta115++;
                cta1213++;
                cta123++;
                cta124++;
                //cta125 ++;
                cta1271++;
                cta1125++;
                cta523++;
            } else if ("1213".equals(cuenta)) {
                Row rw = (sheet0.getRow(cta1213) == null ? sheet0.getRow(cta1213) : sheet0.getRow(cta1213));
                Util.createExcelCellRep(1, rw, rs, rsMetadata.getColumnName(2), rsMetadata.getColumnType(2), estiloTabla);
                Util.createExcelCellRep(2, rw, rs, rsMetadata.getColumnName(3), rsMetadata.getColumnType(3), estiloTabla);
                Util.createExcelCellRep(3, rw, rs, rsMetadata.getColumnName(4), rsMetadata.getColumnType(4), estiloTabla);
                Util.createExcelCellRep(4, rw, rs, rsMetadata.getColumnName(5), rsMetadata.getColumnType(5), estiloTabla);
                Util.createExcelCellRep(5, rw, rs, rsMetadata.getColumnName(6), rsMetadata.getColumnType(6), estiloTabla);
                Util.createExcelCellRep(6, rw, rs, rsMetadata.getColumnName(7), rsMetadata.getColumnType(7), estiloTabla);
                Util.createExcelCellRep(7, rw, rs, rsMetadata.getColumnName(8), rsMetadata.getColumnType(8), estiloMoneda);
                sheet0.shiftRows(cta1213 + 1, cta523 + 1, 1, true, true);
                cargo1213 += rs.getDouble("cargo");
                abono1213 += rs.getDouble("abono");
                cta1213++;
                cta123++;
                cta124++;
                //cta125 ++;
                cta1271++;
                cta1125++;
                cta523++;
            }
        }
        while (rs2.next()) {
            cuenta = rs2.getString(1);
            if ("123".equals(cuenta)) {
                Row rw = (sheet0.getRow(cta123) == null ? sheet0.getRow(cta123) : sheet0.getRow(cta123));
                Util.createExcelCellRep(1, rw, rs2, rs2Metadata.getColumnName(2), rs2Metadata.getColumnType(2), estiloTabla);
                Util.createExcelCellRep(2, rw, rs2, rs2Metadata.getColumnName(3), rs2Metadata.getColumnType(3), estiloTabla);
                Util.createExcelCellRep(3, rw, rs2, rs2Metadata.getColumnName(4), rs2Metadata.getColumnType(4), estiloTabla);
                Util.createExcelCellRep(4, rw, rs2, rs2Metadata.getColumnName(5), rs2Metadata.getColumnType(5), estiloTabla);
                Util.createExcelCellRep(5, rw, rs2, rs2Metadata.getColumnName(6), rs2Metadata.getColumnType(6), estiloTabla);
                Util.createExcelCellRep(6, rw, rs2, rs2Metadata.getColumnName(7), rs2Metadata.getColumnType(7), estiloTabla);
                sheet0.shiftRows(cta123 + 1, cta523 + 1, 1, true, true);
                cargo123 += rs2.getDouble("cargo");
                cta123++;
                cta124++;
                //cta125 ++;
                cta1271++;
                cta1125++;
                cta523++;
            } else if ("124".equals(cuenta)) {
                Row rw = (sheet0.getRow(cta124) == null ? sheet0.getRow(cta124) : sheet0.getRow(cta124));
                Util.createExcelCellRep(1, rw, rs2, rs2Metadata.getColumnName(2), rs2Metadata.getColumnType(2), estiloTabla);
                Util.createExcelCellRep(2, rw, rs2, rs2Metadata.getColumnName(3), rs2Metadata.getColumnType(3), estiloTabla);
                Util.createExcelCellRep(3, rw, rs2, rs2Metadata.getColumnName(4), rs2Metadata.getColumnType(4), estiloTabla);
                Util.createExcelCellRep(4, rw, rs2, rs2Metadata.getColumnName(5), rs2Metadata.getColumnType(5), estiloTabla);
                Util.createExcelCellRep(5, rw, rs2, rs2Metadata.getColumnName(6), rs2Metadata.getColumnType(6), estiloTabla);
                Util.createExcelCellRep(6, rw, rs2, rs2Metadata.getColumnName(7), rs2Metadata.getColumnType(7), estiloTabla);
                sheet0.shiftRows(cta124 + 1, cta523 + 1, 1, true, true);
                cargo124 += rs2.getDouble("cargo");
                cta124++;
                //cta125 ++;
                cta1271++;
                cta1125++;
                cta523++;
            } else if ("1125".equals(cuenta)) {
                Row rw = (sheet0.getRow(cta1125) == null ? sheet0.getRow(cta1125) : sheet0.getRow(cta1125));
                Util.createExcelCellRep(1, rw, rs2, rs2Metadata.getColumnName(2), rs2Metadata.getColumnType(2), estiloTabla);
                Util.createExcelCellRep(2, rw, rs2, rs2Metadata.getColumnName(3), rs2Metadata.getColumnType(3), estiloTabla);
                Util.createExcelCellRep(3, rw, rs2, rs2Metadata.getColumnName(4), rs2Metadata.getColumnType(4), estiloTabla);
                Util.createExcelCellRep(4, rw, rs2, rs2Metadata.getColumnName(5), rs2Metadata.getColumnType(5), estiloTabla);
                Util.createExcelCellRep(5, rw, rs2, rs2Metadata.getColumnName(6), rs2Metadata.getColumnType(6), estiloTabla);
                Util.createExcelCellRep(6, rw, rs2, rs2Metadata.getColumnName(7), rs2Metadata.getColumnType(7), estiloTabla);
                sheet0.shiftRows(cta1125 + 1, cta523 + 1, 1, true, true);
                cargo1125 += rs2.getDouble("cargo");
                cta1125++;
                cta523++;
            } else if ("523".equals(cuenta)) {
                Row rw = (sheet0.getRow(cta523) == null ? sheet0.getRow(cta523) : sheet0.getRow(cta523));
                Util.createExcelCellRep(1, rw, rs2, rs2Metadata.getColumnName(2), rs2Metadata.getColumnType(2), estiloTabla);
                Util.createExcelCellRep(2, rw, rs2, rs2Metadata.getColumnName(3), rs2Metadata.getColumnType(3), estiloTabla);
                Util.createExcelCellRep(3, rw, rs2, rs2Metadata.getColumnName(4), rs2Metadata.getColumnType(4), estiloTabla);
                Util.createExcelCellRep(4, rw, rs2, rs2Metadata.getColumnName(5), rs2Metadata.getColumnType(5), estiloTabla);
                Util.createExcelCellRep(5, rw, rs2, rs2Metadata.getColumnName(6), rs2Metadata.getColumnType(6), estiloTabla);
                Util.createExcelCellRep(6, rw, rs2, rs2Metadata.getColumnName(7), rs2Metadata.getColumnType(7), estiloTabla);
                sheet0.shiftRows(cta523 + 1, cta523 + 1, 1, true, true);
                cargo523 += rs2.getDouble("cargo");
                cta523++;
            }
        }
        PreparedStatement pstm_8251 = null, pstm_reint = null, pstm_551 = null, pstm_5 = null, pstm_559 = null;
        ResultSet nSI = null, nSIR = null, n551 = null, n559 = null, n5 = null;
        pstm_8251 = conn.prepareStatement("SELECT CASE cTipoMovimiento WHEN 'C' THEN SUM(mMovimiento) ELSE 0 END AS cargo " + " FROM dbo.tMovimiento WITH (NOLOCK) " + " WHERE SUBSTRING(nCuenta,1,4) IN('8251') " + " AND MONTH(fMovimiento) BETWEEN 1 AND " + mes + " AND YEAR(fMovimiento) = " + anio + " AND cTipoMovimiento = 'C' " + " GROUP BY SUBSTRING(nCuenta,1,4), cTipoMovimiento");
        /*Se excluyen los reintegros en negativo ya que solo se afectan contablemente*/
        pstm_reint = conn.prepareStatement("SELECT SUM(cargo) AS cargo " + " FROM ( " + " SELECT '8251RP' AS cuenta " + " , CASE SUBSTRING(nCuenta,1,4) WHEN '8251' THEN 'egresos_presupuestarios' END AS concepto " + " , SUM(mMovimiento) AS cargo " + " , 0 AS abono " + " FROM dbo.tMovimiento WITH (NOLOCK) " + " JOIN vReintegrosAutorizados AS r ON cFolioDocumentoMovimiento = nFolioReintegroaut AND cTipoDocumento = tipoDocumento	" + " WHERE SUBSTRING(nCuenta,1,4) IN('8251') " + " AND MONTH(fMovimiento) BETWEEN 1 AND " + mes + " AND YEAR(fMovimiento) =  " + anio + " AND cTipoDocumento LIKE 'REINTEGRO%' " + " AND SUBSTRING(r.cEvento,1,2) NOT IN ('RN') " + " GROUP BY SUBSTRING(nCuenta,1,4), cTipoMovimiento " + " UNION " + " SELECT SUBSTRING(nCuenta,1,4)+'RP' AS cuenta " + " , CASE SUBSTRING(nCuenta,1,4) WHEN '8251' THEN 'egresos_presupuestarios' END AS concepto " + " , (CASE cTipoMovimiento WHEN 'C' THEN SUM(mMovimiento) ELSE 0 END) AS cargo " + " , (CASE cTipoMovimiento WHEN 'A' THEN SUM(mMovimiento) ELSE 0 END) AS abono " + " FROM tMovimiento as m WITH (NOLOCK) " + " WHERE SUBSTRING(nCuenta,1,4) IN ('8251') " + " AND MONTH( fMovimiento) BETWEEN 1 AND " + mes + " AND YEAR(fMovimiento) = " + anio + " AND m.cTipoDocumento = 'DOCPOLIZA' " + " AND cFolioDocumentoMovimiento = 1088 " + " GROUP BY SUBSTRING(nCuenta,1,4), cTipoMovimiento) TBL");
        pstm_551 = conn.prepareStatement("SELECT SUM(mSaldo" + mes + ") AS saldo FROM dbo.tSaldos WITH (NOLOCK) WHERE nCuenta LIKE '551%'");
        pstm_5 = conn.prepareStatement("SELECT SUM(cargo) - SUM(abono) AS saldo " + " FROM (SELECT CASE cTipoMovimiento WHEN 'C' THEN SUM(mMovimiento) ELSE 0 END AS cargo " + " , CASE cTipoMovimiento WHEN 'A' THEN SUM(mMovimiento) ELSE 0 END AS abono " + " FROM dbo.tMovimiento WITH (NOLOCK) " + " WHERE nCuenta LIKE '5%' " + " AND MONTH(fMovimiento) BETWEEN 1 AND " + mes + " AND YEAR(fMovimiento) = " + anio + " GROUP BY cTipoMovimiento " + " ) tbl");
        if (anio == 2015) {
            pstm_559 = conn.prepareStatement("SELECT SUM(mSaldo" + mes + ") AS abono FROM dbo.tSaldos WITH (NOLOCK) WHERE nCuenta LIKE '43991-00008%'");
        } else
            pstm_559 = conn.prepareStatement("SELECT SUM(mSaldo" + mes + ") AS abono FROM dbo.tSaldos WITH (NOLOCK) WHERE nCuenta LIKE '43992-00002%'");
        nSI = pstm_8251.executeQuery();
        nSIR = pstm_reint.executeQuery();
        n551 = pstm_551.executeQuery();
        n559 = pstm_559.executeQuery();
        n5 = pstm_5.executeQuery();
        if (nSI.next()) {
            saldo8251 = nSI.getDouble("cargo");
        }
        if (nSIR.next()) {
            saldoreint = nSIR.getDouble("cargo");
        }
        if (n551.next()) {
            saldo551 = n551.getDouble("saldo");
        }
        if (n559.next()) {
            saldo559 = n559.getDouble("abono");
        }
        if (n5.next()) {
            saldo5 = n5.getDouble("saldo");
        }
        formateador.format(cargo113);
        formateador.format(abono113);
        saldo113 = cargo113 - abono113;
        formateador.format(saldo113);
        formateador.format(cargo115);
        formateador.format(abono115);
        saldo115 = cargo115 - abono115;
        formateador.format(saldo115);
        formateador.format(cargo1213);
        formateador.format(abono1213);
        saldo1213 = cargo1213 - abono1213;
        formateador.format(saldo1213);
        formateador.format(cargo123);
        formateador.format(cargo124);
        formateador.format(cargo1125);
        formateador.format(cargo523);
        formateador.format(saldo8251);
        formateador.format(saldoreint);
        devNeto = saldo8251 - saldoreint - saldoreint;
        formateador.format(devNeto);
        /*ARLA --Se suman los abonos de las cuentas 113, 115 y 1213*/
        saldoDev = abono113 + abono115 + abono1213;
        formateador.format(saldoDev);
        formateador.format(saldo5);
        formateador.format(saldo551);
        formateador.format(saldo559);
        /*ARLA --Al saldo de la cuenta 5% se resta el saldo de las cuentas 551, 559 y saldoDev*/
        saldoGasto = saldo5 - saldoDev - saldo551 - saldo559;
        formateador.format(saldoGasto);
        /*ARLA --La suma de todas los criterios*/
        totalGasto = saldoDev + saldo551 + saldo559 + saldoGasto;
        formateador.format(totalGasto);
        /*ARLA --Diferencia saldo de la cuenta 5% menos total del gasto*/
        dif = saldo5 - totalGasto;
        formateador.format(dif);
        Row rwEnc1 = (sheet0.getRow(9) == null ? sheet0.createRow(9) : sheet0.getRow(9));
        Cell cell1 = (rwEnc1.getCell(8) == null ? rwEnc1.createCell(8) : rwEnc1.getCell(8));
        cell1.setCellValue(saldo113);
        Row rwEnc3 = (sheet0.getRow(cta113 + 3) == null ? sheet0.createRow(cta113 + 3) : sheet0.getRow(cta113 + 3));
        Cell cell3 = (rwEnc3.getCell(8) == null ? rwEnc3.createCell(8) : rwEnc3.getCell(8));
        cell3.setCellValue(saldo115);
        Row rwEnc4 = (sheet0.getRow(cta115 + 3) == null ? sheet0.createRow(cta115 + 3) : sheet0.getRow(cta115 + 3));
        Cell cell4 = (rwEnc4.getCell(8) == null ? rwEnc4.createCell(8) : rwEnc4.getCell(8));
        cell4.setCellValue(saldo1213);
        Row rwEnc5 = (sheet0.getRow(cta1213 + 3) == null ? sheet0.createRow(cta1213 + 3) : sheet0.getRow(cta1213 + 3));
        Cell cell5 = (rwEnc5.getCell(8) == null ? rwEnc5.createCell(8) : rwEnc5.getCell(8));
        cell5.setCellValue(cargo123);
        Row rwEnc6 = (sheet0.getRow(cta123 + 3) == null ? sheet0.createRow(cta123 + 3) : sheet0.getRow(cta123 + 3));
        Cell cell6 = (rwEnc6.getCell(8) == null ? rwEnc6.createCell(8) : rwEnc6.getCell(8));
        cell6.setCellValue(cargo124);
        Row rwEnc7 = (sheet0.getRow(cta1271 + 2) == null ? sheet0.createRow(cta1271 + 2) : sheet0.getRow(cta1271 + 2));
        Cell cell7 = (rwEnc7.getCell(8) == null ? rwEnc7.createCell(8) : rwEnc7.getCell(8));
        cell7.setCellValue(cargo1125);
        Row rwEnc21 = (sheet0.getRow(cta1125 + 3) == null ? sheet0.createRow(cta1125 + 3) : sheet0.getRow(cta1125 + 3));
        Cell cell21 = (rwEnc21.getCell(8) == null ? rwEnc21.createCell(8) : rwEnc21.getCell(8));
        cell21.setCellValue(cargo523);
        Row rwEnc8 = (sheet0.getRow(7) == null ? sheet0.createRow(7) : sheet0.getRow(7));
        Cell cell8 = (rwEnc8.getCell(8) == null ? rwEnc8.createCell(8) : rwEnc8.getCell(8));
        cell8.setCellValue(devNeto);
        Row rwEnc9 = (sheet0.getRow(cta523 + 3) == null ? sheet0.createRow(cta523 + 3) : sheet0.getRow(cta523 + 3));
        Cell cell9 = (rwEnc9.getCell(8) == null ? rwEnc9.createCell(8) : rwEnc9.getCell(8));
        cell9.setCellValue(saldoDev);
        Row rwEnc10 = (sheet0.getRow(cta523 + 3) == null ? sheet0.createRow(cta523 + 3) : sheet0.getRow(cta523 + 3));
        Cell cell10 = (rwEnc10.getCell(5) == null ? rwEnc10.createCell(5) : rwEnc10.getCell(5));
        cell10.setCellValue("TOTALES ACTIVO NO PRESUPUESTALES");
        Row rwEnc11 = (sheet0.getRow(cta523 + 4) == null ? sheet0.createRow(cta523 + 4) : sheet0.getRow(cta523 + 4));
        Cell cell11 = (rwEnc11.getCell(8) == null ? rwEnc11.createCell(8) : rwEnc11.getCell(8));
        cell11.setCellValue(saldo551);
        Row rwEnc12 = (sheet0.getRow(cta523 + 4) == null ? sheet0.createRow(cta523 + 4) : sheet0.getRow(cta523 + 4));
        Cell cell12 = (rwEnc12.getCell(5) == null ? rwEnc12.createCell(5) : rwEnc12.getCell(5));
        cell12.setCellValue("551 DEPRECIACIONES");
        Row rwEnc13 = (sheet0.getRow(cta523 + 5) == null ? sheet0.createRow(cta523 + 5) : sheet0.getRow(cta523 + 5));
        Cell cell13 = (rwEnc13.getCell(8) == null ? rwEnc13.createCell(8) : rwEnc13.getCell(8));
        cell13.setCellValue(saldo559);
        Row rwEnc14 = (sheet0.getRow(cta523 + 5) == null ? sheet0.createRow(cta523 + 5) : sheet0.getRow(cta523 + 5));
        Cell cell14 = (rwEnc14.getCell(5) == null ? rwEnc14.createCell(5) : rwEnc14.getCell(5));
        cell14.setCellValue("559 RENDICION DE CUENTAS FFM");
        Row rwEnc15 = (sheet0.getRow(cta523 + 6) == null ? sheet0.createRow(cta523 + 6) : sheet0.getRow(cta523 + 6));
        Cell cell15 = (rwEnc15.getCell(8) == null ? rwEnc15.createCell(8) : rwEnc15.getCell(8));
        cell15.setCellValue(saldoGasto);
        Row rwEnc16 = (sheet0.getRow(cta523 + 6) == null ? sheet0.createRow(cta523 + 6) : sheet0.getRow(cta523 + 6));
        Cell cell16 = (rwEnc16.getCell(5) == null ? rwEnc16.createCell(5) : rwEnc16.getCell(5));
        cell16.setCellValue("GASTO PRESUPUESTAL");
        Row rwEnc19 = (sheet0.getRow(cta523 + 7) == null ? sheet0.createRow(cta523 + 7) : sheet0.getRow(cta523 + 7));
        Cell cell19 = (rwEnc19.getCell(8) == null ? rwEnc19.createCell(8) : rwEnc19.getCell(8));
        cell19.setCellValue(totalGasto);
        Row rwEnc20 = (sheet0.getRow(cta523 + 7) == null ? sheet0.createRow(cta523 + 7) : sheet0.getRow(cta523 + 7));
        Cell cell20 = (rwEnc20.getCell(5) == null ? rwEnc20.createCell(5) : rwEnc20.getCell(5));
        cell20.setCellValue("(=) TOTAL GASTO");
        Row rwEnc17 = (sheet0.getRow(cta523 + 8) == null ? sheet0.createRow(cta523 + 8) : sheet0.getRow(cta523 + 8));
        Cell cell17 = (rwEnc17.getCell(8) == null ? rwEnc17.createCell(8) : rwEnc17.getCell(8));
        cell17.setCellValue(dif);
        Row rwEnc18 = (sheet0.getRow(cta523 + 8) == null ? sheet0.createRow(cta523 + 8) : sheet0.getRow(cta523 + 8));
        Cell cell18 = (rwEnc18.getCell(5) == null ? rwEnc18.createCell(5) : rwEnc18.getCell(5));
        cell18.setCellValue("DIF");
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
