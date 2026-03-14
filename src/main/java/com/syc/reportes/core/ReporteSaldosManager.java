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
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jfree.util.Log;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import java.util.Base64;

public class ReporteSaldosManager {

    public static String generaReporteSaldosManager(Connection conn, Integer mIni, Integer mFin, String nCuenta, String nSubCuenta, String cCC, Map<String, String> plantillas) throws Exception {
        if ("".equals(nSubCuenta)) {
            nSubCuenta = "%";
        }
        String Centro = "";
        if (cCC.equals("0")) {
            Centro = "%";
        } else {
            Centro = cCC;
        }
        Statement pstmt = null;
        ResultSet rs = null;
        String sql_ini = "";
        String sql_cargos = "";
        String sql_abonos = "";
        String fileName = "";
        Integer count = mIni;
        if (mIni == 1) {
            sql_ini = "SUM(mSaldo0)";
        } else {
            sql_ini = "SUM(mSaldo" + Integer.toString(mIni - 1) + ")";
        }
        while (count <= mFin) {
            if (sql_cargos == "") {
                sql_cargos = "";
            } else {
                sql_cargos = sql_cargos + " + ";
            }
            sql_cargos = sql_cargos + "SUM(mDeber" + Integer.toString(count) + ")";
            if (sql_abonos == "") {
                sql_abonos = "";
            } else {
                sql_abonos = sql_abonos + " + ";
            }
            sql_abonos = sql_abonos + "SUM(mHaber" + Integer.toString(count) + ")";
            count += 1;
        }
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT DISTINCT s.nCuenta ");
        sql.append(", s.cSubCuenta ");
        sql.append(", s.cCentroContable ");
        sql.append(", CAST( " + sql_ini + " AS money) AS SaldoInicial ");
        sql.append(", CAST( " + sql_cargos + " AS money) AS Cargos ");
        sql.append(", CAST( " + sql_abonos + " AS money) AS Abonos ");
        sql.append(", CAST( " + sql_ini + " + (CASE WHEN s.naturalezaDeLaCuenta = 'D' THEN " + " ( " + sql_cargos + " ) - ( " + sql_abonos + " ) ");
        sql.append(" ELSE " + " ( " + sql_abonos + " ) - ( " + sql_cargos + " )");
        sql.append(" END ) AS money) AS SaldoFinal ");
        sql.append(" FROM dbo.tSaldosVista s (NOLOCK) INNER JOIN tcuentas c  (NOLOCK) ON s.nCuenta = c.nCuenta ");
        sql.append(" WHERE s.nCuenta LIKE '" + nCuenta + "' ");
        sql.append(" AND s.cSubCuenta LIKE '" + nSubCuenta + "' ");
        sql.append(" AND s.cCentroContable LIKE '" + Centro + "' ");
        sql.append(" GROUP BY s.nCuenta, s.cSubCuenta, s.cCentroContable, s.naturalezaDeLaCuenta");
        Log.debug("Object: {}", sql.toString());
        try {
            pstmt = conn.createStatement();
            rs = pstmt.executeQuery(sql.toString());
            fileName = generaReporteSaldos(conn, rs, mIni, mFin, nCuenta, nSubCuenta, plantillas.get("REPCONSALDOS"));
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(pstmt, false);
        }
    }

    private static String generaReporteSaldos(Connection conn, ResultSet rs, Integer mIni, Integer mFin, String nCuenta, String nSubCuenta, String plantillaPath) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "ReporteConsultaSaldos" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xlsx";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsFile = new FileInputStream(cFileExcelPlantilla);
        XSSFWorkbook workbook = new XSSFWorkbook(fsFile);
        fsFile.close();
        XSSFSheet sheet0 = workbook.getSheetAt(0);
        int renglonInicio = 9;
        double si = 0;
        double ca = 0;
        double ab = 0;
        double sf = 0;
        ResultSetMetaData rsMetadata = rs.getMetaData();
        String DATE_FORMAT = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        // today
        Calendar c1 = Calendar.getInstance();
        String periodo = sdf.format(c1.getTime());
        XSSFRow rwEnc0 = (sheet0.getRow(3) == null ? sheet0.createRow(3) : sheet0.getRow(3));
        XSSFCell cell0 = (rwEnc0.getCell(1) == null ? rwEnc0.createCell(1) : rwEnc0.getCell(1));
        cell0.setCellValue(periodo);
        XSSFRow rwEnc1 = (sheet0.getRow(6) == null ? sheet0.createRow(6) : sheet0.getRow(6));
        XSSFCell cell1 = (rwEnc1.getCell(1) == null ? rwEnc1.createCell(1) : rwEnc1.getCell(1));
        cell1.setCellValue(nCuenta);
        XSSFRow rwEnc2 = (sheet0.getRow(6) == null ? sheet0.createRow(6) : sheet0.getRow(6));
        XSSFCell cell2 = (rwEnc2.getCell(2) == null ? rwEnc2.createCell(2) : rwEnc2.getCell(2));
        cell2.setCellValue(nSubCuenta);
        XSSFRow rwEnc3 = (sheet0.getRow(6) == null ? sheet0.createRow(6) : sheet0.getRow(6));
        XSSFCell cell3 = (rwEnc3.getCell(5) == null ? rwEnc3.createCell(5) : rwEnc3.getCell(5));
        cell3.setCellValue(mIni.toString());
        XSSFRow rwEnc4 = (sheet0.getRow(6) == null ? sheet0.createRow(6) : sheet0.getRow(6));
        XSSFCell cell4 = (rwEnc4.getCell(6) == null ? rwEnc4.createCell(6) : rwEnc4.getCell(6));
        cell4.setCellValue(mFin.toString());
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 9);
        font.setFontName("Arial");
        CellStyle estiloTabla = workbook.createCellStyle();
        estiloTabla = Util.generaEstilo2(workbook, 9, false, true, true, true, true, false);
        CellStyle estiloMoneda = workbook.createCellStyle();
        estiloMoneda = Util.generaEstilo2(workbook, 9, false, true, true, true, true, true);
        CellStyle estiloMonedaT = workbook.createCellStyle();
        estiloMonedaT = Util.generaEstilo2(workbook, 9, true, true, true, true, true, true);
        while (rs.next()) {
            XSSFRow rw = (sheet0.getRow(renglonInicio) == null ? sheet0.createRow(renglonInicio) : sheet0.getRow(renglonInicio));
            Util.createExcelCellRep(1, rw, rs, rsMetadata.getColumnName(1), rsMetadata.getColumnType(1), estiloTabla);
            Util.createExcelCellRep(2, rw, rs, rsMetadata.getColumnName(2), rsMetadata.getColumnType(2), estiloTabla);
            Util.createExcelCellRep(3, rw, rs, rsMetadata.getColumnName(3), rsMetadata.getColumnType(3), estiloTabla);
            Util.createExcelCellRep(4, rw, rs, rsMetadata.getColumnName(4), rsMetadata.getColumnType(4), estiloMoneda);
            Util.createExcelCellRep(5, rw, rs, rsMetadata.getColumnName(5), rsMetadata.getColumnType(5), estiloMoneda);
            Util.createExcelCellRep(6, rw, rs, rsMetadata.getColumnName(6), rsMetadata.getColumnType(6), estiloMoneda);
            Util.createExcelCellRep(7, rw, rs, rsMetadata.getColumnName(7), rsMetadata.getColumnType(7), estiloMoneda);
            renglonInicio++;
            si += rs.getDouble("SaldoInicial");
            ca += rs.getDouble("Cargos");
            ab += rs.getDouble("Abonos");
            sf += rs.getDouble("SaldoFinal");
        }
        XSSFRow rwSALDOS = (sheet0.getRow(renglonInicio) == null ? sheet0.createRow(renglonInicio) : sheet0.getRow(renglonInicio));
        XSSFCell cellSI = (rwSALDOS.getCell(4) == null ? rwSALDOS.createCell(4) : rwSALDOS.getCell(4));
        cellSI.setCellValue(si);
        cellSI.setCellStyle(estiloMonedaT);
        XSSFCell cellCA = (rwSALDOS.getCell(5) == null ? rwSALDOS.createCell(5) : rwSALDOS.getCell(5));
        cellCA.setCellValue(ca);
        cellCA.setCellStyle(estiloMonedaT);
        XSSFCell cellAB = (rwSALDOS.getCell(6) == null ? rwSALDOS.createCell(6) : rwSALDOS.getCell(6));
        cellAB.setCellValue(ab);
        cellAB.setCellStyle(estiloMonedaT);
        XSSFCell cellSF = (rwSALDOS.getCell(7) == null ? rwSALDOS.createCell(7) : rwSALDOS.getCell(7));
        cellSF.setCellValue(sf);
        cellSF.setCellStyle(estiloMonedaT);
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

    public static String generaReporteMovimientosManager(Connection conn, Integer mIni, Integer mFin, String nCuenta, String nSubCuenta, String nCC, String SI, String C, String A, String SF, Map<String, String> plantillas) throws Exception {
        Statement pstmt = null;
        ResultSet rs = null;
        String fileName = "";
        String query = "SELECT * FROM fn_l_ConsultaMovimientos(" + mIni + "," + mFin + ",'" + nCuenta + "','" + nSubCuenta + "','" + nCC + "')";
        Log.debug("Object: {}", query);
        try {
            pstmt = conn.createStatement();
            rs = pstmt.executeQuery(query);
            fileName = generaReporteMovimientos(rs, mIni, mFin, nCuenta, nSubCuenta, nCC, SI, C, A, SF, plantillas.get("REPCONMOVIMIENTOS"));
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(pstmt, false);
        }
    }

    private static String generaReporteMovimientos(ResultSet rs, Integer mIni, Integer mFin, String nCuenta, String nSubCuenta, String nCC, String SI, String C, String A, String SF, String plantillaPath) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "ReporteMovimientos" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xlsx";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        XSSFWorkbook workbook = new XSSFWorkbook(fsArchivo);
        fsArchivo.close();
        XSSFSheet sheet0 = workbook.getSheetAt(0);
        int renglonInicio = 10;
        ResultSetMetaData rsMetadata = rs.getMetaData();
        String DATE_FORMAT = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        // today
        Calendar c1 = Calendar.getInstance();
        String periodo = sdf.format(c1.getTime());
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 9);
        font.setFontName("Arial");
        CellStyle estilodato = workbook.createCellStyle();
        estilodato = Util.generaEstilo2(workbook, 9, false, false, false, false, false, false);
        CellStyle estiloTabla = workbook.createCellStyle();
        estiloTabla = Util.generaEstilo2(workbook, 9, false, true, true, true, true, false);
        CellStyle estiloMoneda = workbook.createCellStyle();
        estiloMoneda = Util.generaEstilo2(workbook, 9, false, true, true, true, true, true);
        CellStyle estiloMoneda2 = workbook.createCellStyle();
        estiloMoneda2 = Util.generaEstilo2(workbook, 9, false, false, false, false, false, true);
        XSSFRow rwEnc0 = (sheet0.getRow(5) == null ? sheet0.createRow(5) : sheet0.getRow(5));
        XSSFCell cell0 = (rwEnc0.getCell(2) == null ? rwEnc0.createCell(2) : rwEnc0.getCell(2));
        cell0.setCellStyle(estilodato);
        cell0.setCellValue(nCuenta);
        XSSFRow rwEnc1 = (sheet0.getRow(6) == null ? sheet0.createRow(6) : sheet0.getRow(6));
        XSSFCell cell1 = (rwEnc1.getCell(2) == null ? rwEnc1.createCell(2) : rwEnc1.getCell(2));
        cell1.setCellStyle(estilodato);
        cell1.setCellValue(nSubCuenta);
        XSSFRow rwEnc2 = (sheet0.getRow(7) == null ? sheet0.createRow(7) : sheet0.getRow(7));
        XSSFCell cell2 = (rwEnc2.getCell(2) == null ? rwEnc2.createCell(2) : rwEnc2.getCell(2));
        cell2.setCellStyle(estilodato);
        cell2.setCellValue(nCC);
        XSSFRow rwEnc3 = (sheet0.getRow(5) == null ? sheet0.createRow(5) : sheet0.getRow(5));
        XSSFCell cell3 = (rwEnc3.getCell(9) == null ? rwEnc3.createCell(9) : rwEnc3.getCell(9));
        cell3.setCellStyle(estilodato);
        cell3.setCellValue(mIni.toString());
        XSSFRow rwEnc4 = (sheet0.getRow(6) == null ? sheet0.createRow(6) : sheet0.getRow(6));
        XSSFCell cell4 = (rwEnc4.getCell(9) == null ? rwEnc4.createCell(9) : rwEnc4.getCell(9));
        cell4.setCellStyle(estilodato);
        cell4.setCellValue(mFin.toString());
        XSSFRow rwEnc5 = (sheet0.getRow(3) == null ? sheet0.createRow(3) : sheet0.getRow(3));
        XSSFCell cell5 = (rwEnc5.getCell(1) == null ? rwEnc5.createCell(1) : rwEnc5.getCell(1));
        cell5.setCellStyle(estilodato);
        cell5.setCellValue(periodo);
        XSSFRow rwEnc6 = (sheet0.getRow(7) == null ? sheet0.createRow(7) : sheet0.getRow(7));
        XSSFCell cell6 = (rwEnc6.getCell(4) == null ? rwEnc6.createCell(4) : rwEnc6.getCell(4));
        cell6.setCellStyle(estiloMoneda2);
        cell6.setCellValue(SI);
        XSSFRow rwEnc7 = (sheet0.getRow(7) == null ? sheet0.createRow(7) : sheet0.getRow(7));
        XSSFCell cell7 = (rwEnc7.getCell(5) == null ? rwEnc7.createCell(5) : rwEnc7.getCell(5));
        cell7.setCellStyle(estiloMoneda2);
        cell7.setCellValue(C);
        XSSFRow rwEnc8 = (sheet0.getRow(7) == null ? sheet0.createRow(7) : sheet0.getRow(7));
        XSSFCell cell8 = (rwEnc8.getCell(6) == null ? rwEnc8.createCell(6) : rwEnc8.getCell(6));
        cell8.setCellStyle(estiloMoneda2);
        cell8.setCellValue(A);
        XSSFRow rwEnc9 = (sheet0.getRow(7) == null ? sheet0.createRow(7) : sheet0.getRow(7));
        XSSFCell cell9 = (rwEnc9.getCell(7) == null ? rwEnc9.createCell(7) : rwEnc9.getCell(7));
        cell9.setCellStyle(estiloMoneda2);
        cell9.setCellValue(SF);
        while (rs.next()) {
            XSSFRow rw = (sheet0.getRow(renglonInicio) == null ? sheet0.createRow(renglonInicio) : sheet0.getRow(renglonInicio));
            Util.createExcelCellRep(1, rw, rs, rsMetadata.getColumnName(1), rsMetadata.getColumnType(1), estiloTabla);
            Util.createExcelCellRep(2, rw, rs, rsMetadata.getColumnName(2), rsMetadata.getColumnType(2), estiloTabla);
            Util.createExcelCellRep(3, rw, rs, rsMetadata.getColumnName(3), rsMetadata.getColumnType(3), estiloTabla);
            Util.createExcelCellRep(4, rw, rs, rsMetadata.getColumnName(4), rsMetadata.getColumnType(4), estiloTabla);
            Util.createExcelCellRep(5, rw, rs, rsMetadata.getColumnName(5), rsMetadata.getColumnType(5), estiloTabla);
            Util.createExcelCellRep(6, rw, rs, rsMetadata.getColumnName(6), rsMetadata.getColumnType(6), estiloMoneda);
            Util.createExcelCellRep(7, rw, rs, rsMetadata.getColumnName(7), rsMetadata.getColumnType(7), estiloMoneda);
            Util.createExcelCellRep(8, rw, rs, rsMetadata.getColumnName(8), rsMetadata.getColumnType(8), estiloTabla);
            Util.createExcelCellRep(9, rw, rs, rsMetadata.getColumnName(9), rsMetadata.getColumnType(9), estiloTabla);
            renglonInicio++;
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

    public static String generaReportePolizaAuxManager(Connection conn, Integer nFolio, String cTipo, Integer nDocto, String cDocto, Integer estatus, Map<String, String> plantillas) throws Exception {
        Statement pstmt_enc = null;
        Statement pstmt_det = null;
        Statement pstmt_cheque = null;
        CallableStatement cs = null;
        ResultSet rs_enc = null;
        ResultSet rs_det = null;
        ResultSet rs_pie = null;
        ResultSet rs_cheque = null;
        StringBuilder queryEnc = new StringBuilder();
        queryEnc.append("SELECT cTipoPoliza as pTipo ");
        queryEnc.append(", nFolioPoliza as pFolio ");
        queryEnc.append(", cTipoDocumento as pDocumento ");
        queryEnc.append(", nFolioDocumento as pNumero ");
        queryEnc.append(", nMes as pMes ");
        queryEnc.append(", fCreacion as pCreacion ");
        queryEnc.append(", fAplicacion as pAplicacion ");
        queryEnc.append(", cCentroContable as pCC ");
        queryEnc.append(", CONVERT(varchar, CAST(mTotalCargo AS money), 1) as pCargos ");
        queryEnc.append(", CONVERT(varchar, CAST(mTotalAbono AS money), 1) as pAbonos ");
        queryEnc.append(", cDescripcionPoliza as pDescripcion ");
        queryEnc.append(" FROM dbo.tPoliza WITH (NOLOCK) ");
        queryEnc.append(" WHERE nFolioPoliza = " + nFolio);
        queryEnc.append(" AND cTipoPoliza = '" + cTipo + "' ");
        queryEnc.append(" AND cTipoDocumento = '" + cDocto + "' ");
        queryEnc.append(" AND nFolioDocumento = " + nDocto);
        String query_det = "SELECT * FROM fn_l_DetallePoliza(" + nFolio + ",'" + cTipo + "'," + nDocto + ",'" + cDocto + "')";
        String query_cheque = "";
        String fileName = "";
        try {
            pstmt_enc = conn.createStatement();
            pstmt_det = conn.createStatement();
            pstmt_cheque = conn.createStatement();
            rs_enc = pstmt_enc.executeQuery(queryEnc.toString());
            rs_det = pstmt_det.executeQuery(query_det);
            cs = conn.prepareCall("{call sp_l_DatosReferencia(?,?,?,?,?)}");
            cs.setInt(1, nFolio);
            cs.setString(2, cTipo);
            cs.setInt(3, nDocto);
            cs.setString(4, cDocto);
            cs.setInt(5, estatus);
            rs_pie = cs.executeQuery();
            Integer cheque = 0;
            if (cDocto == "CHEQUE") {
                query_cheque = "SELECT dbo.fn_l_obtenCheque (" + nDocto + " , '" + cDocto + "' ) AS rCheque";
                rs_cheque = pstmt_cheque.executeQuery(query_cheque);
                cheque = rs_cheque.getInt("rCheque");
            } else if (cDocto == "CAJA") {
                query_cheque = "SELECT dbo.fn_l_obtenCheque (" + nDocto + " , '" + cDocto + "' ) AS rCheque";
                rs_cheque = pstmt_cheque.executeQuery(query_cheque);
                cheque = rs_cheque.getInt("rCheque");
            } else if (cDocto == "RELACIONGASTOS") {
                query_cheque = "SELECT dbo.fn_l_obtenCheque (" + nDocto + " , '" + cDocto + "' ) AS rCheque";
                rs_cheque = pstmt_cheque.executeQuery(query_cheque);
                cheque = rs_cheque.getInt("rCheque");
            } else {
                cheque = 0;
            }
            fileName = generaReporteConAuxiliar(rs_enc, rs_det, rs_pie, nFolio, cTipo, nDocto, cDocto, cheque, plantillas.get("REPCONPOLIZAAUX"));
            return fileName;
        } finally {
            CloseObject.closeObject(rs_enc, false);
            CloseObject.closeObject(rs_det, false);
            CloseObject.closeObject(rs_pie, false);
            CloseObject.closeObject(rs_cheque, false);
            CloseObject.closeObject(pstmt_enc, false);
            CloseObject.closeObject(pstmt_det, false);
            CloseObject.closeObject(pstmt_cheque, false);
            CloseObject.closeObject(cs, false);
        }
    }

    private static String generaReporteConAuxiliar(ResultSet rs_enc, ResultSet rs_det, ResultSet rs_pie, Integer nFolio, String cTipo, Integer nDocto, String cDocto, Integer cheque, String plantillaPath) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "ReporteAuxPoliza" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        int cnt = 0;
        int renglonInicio = 11;
        ResultSetMetaData rsMetadata = rs_det.getMetaData();
        String DATE_FORMAT = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        // today
        Calendar c1 = Calendar.getInstance();
        String periodo = sdf.format(c1.getTime());
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 9);
        font.setFontName("Arial");
        CellStyle estiloTabla = workbook.createCellStyle();
        estiloTabla = Util.generaEstilo2(workbook, 9, false, true, true, true, true, false);
        CellStyle estiloMoneda = workbook.createCellStyle();
        estiloMoneda = Util.generaEstilo2(workbook, 9, false, true, true, true, true, true);
        CellStyle estilodato = workbook.createCellStyle();
        estilodato.setAlignment(HorizontalAlignment.CENTER);
        estilodato.setFont(font);
        CellStyle estilodescripcion = workbook.createCellStyle();
        estilodescripcion.setAlignment(HorizontalAlignment.CENTER);
        estilodescripcion.setVerticalAlignment(VerticalAlignment.CENTER);
        estilodescripcion.setFont(font);
        Row rwEnc0 = (sheet0.getRow(3) == null ? sheet0.createRow(3) : sheet0.getRow(3));
        Cell cell0 = (rwEnc0.getCell(1) == null ? rwEnc0.createCell(1) : rwEnc0.getCell(1));
        cell0.setCellValue(periodo);
        while (rs_enc.next()) {
            Row rwTipoPoliza = (sheet0.getRow(5) == null ? sheet0.createRow(5) : sheet0.getRow(5));
            Cell cellTP = (rwTipoPoliza.getCell(2) == null ? rwTipoPoliza.createCell(2) : rwTipoPoliza.getCell(2));
            cellTP.setCellStyle(estilodato);
            cellTP.setCellValue(rs_enc.getString("pTipo"));
            Row rwFolioPoliza = (sheet0.getRow(6) == null ? sheet0.createRow(6) : sheet0.getRow(6));
            Cell cellFP = (rwFolioPoliza.getCell(2) == null ? rwFolioPoliza.createCell(2) : rwFolioPoliza.getCell(2));
            cellFP.setCellStyle(estilodato);
            cellFP.setCellValue(rs_enc.getInt("pFolio"));
            Row rwDocumento = (sheet0.getRow(7) == null ? sheet0.createRow(7) : sheet0.getRow(7));
            Cell cellDoc = (rwDocumento.getCell(2) == null ? rwDocumento.createCell(2) : rwDocumento.getCell(2));
            cellDoc.setCellStyle(estilodato);
            cellDoc.setCellValue(rs_enc.getString("pDocumento"));
            Row rwDocNum = (sheet0.getRow(8) == null ? sheet0.createRow(8) : sheet0.getRow(8));
            Cell cellDocN = (rwDocNum.getCell(2) == null ? rwDocNum.createCell(2) : rwDocNum.getCell(2));
            cellDocN.setCellStyle(estilodato);
            cellDocN.setCellValue(rs_enc.getInt("pNumero"));
            Row rwFC = (sheet0.getRow(5) == null ? sheet0.createRow(5) : sheet0.getRow(5));
            Cell cellFC = (rwFC.getCell(4) == null ? rwFC.createCell(4) : rwFC.getCell(4));
            cellFC.setCellStyle(estilodato);
            cellFC.setCellValue(rs_enc.getString("pCreacion"));
            Row rwFA = (sheet0.getRow(6) == null ? sheet0.createRow(6) : sheet0.getRow(6));
            Cell cellFA = (rwFA.getCell(4) == null ? rwFA.createCell(4) : rwFA.getCell(4));
            cellFA.setCellStyle(estilodato);
            cellFA.setCellValue(rs_enc.getString("pAplicacion"));
            Row rwCC = (sheet0.getRow(7) == null ? sheet0.createRow(7) : sheet0.getRow(7));
            Cell cellCC = (rwCC.getCell(4) == null ? rwCC.createCell(4) : rwCC.getCell(4));
            cellCC.setCellStyle(estilodato);
            cellCC.setCellValue(rs_enc.getString("pCC"));
            Row rwMes = (sheet0.getRow(8) == null ? sheet0.createRow(8) : sheet0.getRow(8));
            Cell cellMes = (rwMes.getCell(4) == null ? rwMes.createCell(4) : rwMes.getCell(4));
            cellMes.setCellStyle(estilodato);
            cellMes.setCellValue(rs_enc.getString("pMes"));
            Row rwCargos = (sheet0.getRow(12) == null ? sheet0.createRow(12) : sheet0.getRow(12));
            Cell cellCargos = (rwCargos.getCell(3) == null ? rwCargos.createCell(3) : rwCargos.getCell(3));
            cellCargos.setCellStyle(estiloMoneda);
            cellCargos.setCellValue(rs_enc.getString("pCargos"));
            Row rwAbonos = (sheet0.getRow(12) == null ? sheet0.createRow(12) : sheet0.getRow(12));
            Cell cellAbonos = (rwAbonos.getCell(4) == null ? rwAbonos.createCell(4) : rwAbonos.getCell(4));
            cellAbonos.setCellStyle(estiloMoneda);
            cellAbonos.setCellValue(rs_enc.getString("pAbonos"));
            Row rwDET = (sheet0.getRow(15) == null ? sheet0.createRow(15) : sheet0.getRow(15));
            Cell cellDET = (rwDET.getCell(1) == null ? rwDET.createCell(1) : rwDET.getCell(1));
            cellDET.setCellStyle(estilodescripcion);
            cellDET.setCellValue(rs_enc.getString("pDescripcion"));
        }
        while (rs_pie.next()) {
            Row rwRDoc = (sheet0.getRow(20) == null ? sheet0.createRow(20) : sheet0.getRow(20));
            Cell cellRDoc = (rwRDoc.getCell(2) == null ? rwRDoc.createCell(2) : rwRDoc.getCell(2));
            cellRDoc.setCellStyle(estilodato);
            cellRDoc.setCellValue(rs_pie.getString("rTD"));
            Row rwRDocN = (sheet0.getRow(21) == null ? sheet0.createRow(21) : sheet0.getRow(21));
            Cell cellRDocN = (rwRDocN.getCell(2) == null ? rwRDocN.createCell(2) : rwRDocN.getCell(2));
            cellRDocN.setCellStyle(estilodato);
            cellRDocN.setCellValue(rs_pie.getInt("rND"));
            Row rwRPol = (sheet0.getRow(22) == null ? sheet0.createRow(22) : sheet0.getRow(22));
            Cell cellRPol = (rwRPol.getCell(2) == null ? rwRPol.createCell(2) : rwRPol.getCell(2));
            cellRPol.setCellStyle(estilodato);
            cellRPol.setCellValue(rs_pie.getString("rTP"));
            Row rwRPolN = (sheet0.getRow(23) == null ? sheet0.createRow(23) : sheet0.getRow(23));
            Cell cellRPolN = (rwRPolN.getCell(2) == null ? rwRPolN.createCell(2) : rwRPolN.getCell(2));
            cellRPolN.setCellStyle(estilodato);
            cellRPolN.setCellValue(rs_pie.getString("rFP"));
            Row rwRCxP = (sheet0.getRow(24) == null ? sheet0.createRow(24) : sheet0.getRow(24));
            Cell cellRCxP = (rwRCxP.getCell(2) == null ? rwRCxP.createCell(2) : rwRCxP.getCell(2));
            cellRCxP.setCellStyle(estilodato);
            cellRCxP.setCellValue(rs_pie.getString("rRef"));
            Row rwRED = (sheet0.getRow(20) == null ? sheet0.createRow(20) : sheet0.getRow(20));
            Cell cellRED = (rwRED.getCell(4) == null ? rwRED.createCell(4) : rwRED.getCell(4));
            cellRED.setCellStyle(estilodato);
            cellRED.setCellValue(rs_pie.getString("rEst"));
            Row rwRPC = (sheet0.getRow(21) == null ? sheet0.createRow(21) : sheet0.getRow(21));
            Cell cellRPC = (rwRPC.getCell(4) == null ? rwRPC.createCell(4) : rwRPC.getCell(4));
            cellRPC.setCellStyle(estilodato);
            cellRPC.setCellValue(rs_pie.getString("rPC"));
            Row rwRFC = (sheet0.getRow(22) == null ? sheet0.createRow(22) : sheet0.getRow(22));
            Cell cellRFC = (rwRFC.getCell(4) == null ? rwRFC.createCell(4) : rwRFC.getCell(4));
            cellRFC.setCellStyle(estilodato);
            cellRFC.setCellValue(rs_pie.getString("rFC"));
            Row rwCheque = (sheet0.getRow(23) == null ? sheet0.createRow(23) : sheet0.getRow(23));
            Cell cellCheque = (rwCheque.getCell(4) == null ? rwCheque.createCell(4) : rwCheque.getCell(4));
            cellCheque.setCellStyle(estilodato);
            cellCheque.setCellValue(cheque);
        }
        while (rs_det.next()) {
            Row rw = (sheet0.getRow(renglonInicio + cnt) == null ? sheet0.createRow(renglonInicio + cnt) : sheet0.getRow(renglonInicio + cnt));
            Util.createExcelCellRep(1, rw, rs_det, rsMetadata.getColumnName(1), rsMetadata.getColumnType(1), estiloTabla);
            Util.createExcelCellRep(2, rw, rs_det, rsMetadata.getColumnName(2), rsMetadata.getColumnType(2), estiloTabla);
            Util.createExcelCellRep(3, rw, rs_det, rsMetadata.getColumnName(3), rsMetadata.getColumnType(3), estiloMoneda);
            Util.createExcelCellRep(4, rw, rs_det, rsMetadata.getColumnName(4), rsMetadata.getColumnType(4), estiloMoneda);
            sheet0.shiftRows(renglonInicio + cnt + 1, renglonInicio + cnt + 13, 1, true, true);
            cnt++;
        }
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
