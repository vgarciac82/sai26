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
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
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

public class ConciliacionesBienesManager {

    public static String generaConciliacionBM(HttpServletRequest req, Connection conn, String tipo, String general, Map<String, String> plantillas, String fecha, String unidad) throws Exception {
        String fileName = "";
        CallableStatement cs = null, cs2 = null;
        ResultSet rs = null, rs2 = null;
        String query = "";
        String query2 = "";
        int mes = Integer.parseInt(fecha.substring(3, 5));
        String anio = fecha.substring(6, 10);
        int tipoC = Integer.parseInt(tipo);
        double saldo = consultaSaldos(conn, tipo, mes);
        double saldoGRMO = consultaSaldosGRMO(conn, tipo, mes);
        if ("1".equals(tipo)) {
            query = "{call sp_ConciliacionBienesMuebles( ?, ?, ? )}";
            query2 = "{call sp_ConciliacionBienesMueblesPartida( ?, ? )}";
        } else if ("2".equals(tipo)) {
            query = "{call sp_ConciliacionDepreciacionBM( ?, ?, ? )}";
            query2 = "{call sp_ConciliacionDepreciacionBMPartida( ?, ? )}";
        }
        cs = conn.prepareCall(query);
        cs.setInt(1, mes);
        cs.setInt(2, tipoC);
        cs.setString(3, anio);
        rs = cs.executeQuery();
        cs2 = conn.prepareCall(query2);
        cs2.setInt(1, mes);
        cs2.setInt(2, tipoC);
        rs2 = cs2.executeQuery();
        /*En espera de que scob mande la informacion acumulada por partida para hacer los calculos y sacar el saldo de la GRMO, provicional se enviara fijo*/
        fileName = generaReporteBM(req, rs, rs2, plantillas.get("CONCILIABM"), fecha, tipo, saldo, saldoGRMO, unidad);
        return fileName;
    }

    public static String generaConciliacionBC(HttpServletRequest req, Connection conn, String tipo, String general, Map<String, String> plantillas, String fecha, String unidad) throws Exception {
        String fileName = "";
        CallableStatement cs = null, cs2 = null;
        ResultSet rs = null, rs2 = null;
        String query = "";
        String query2 = "";
        int mes = Integer.parseInt(fecha.substring(3, 5));
        String anio = fecha.substring(6, 10);
        int tipoC = Integer.parseInt(tipo);
        double saldo = consultaSaldos(conn, tipo, mes);
        double saldoGRMO = consultaSaldosGRMO(conn, tipo, mes);
        query = "{call sp_ConciliacionBienesConsumibles( ?, ?, ? )}";
        query2 = "{call sp_ConciliacionBienesConsumiblesPartida( ?, ? )}";
        cs = conn.prepareCall(query);
        cs.setInt(1, mes);
        cs.setInt(2, tipoC);
        cs.setString(3, anio);
        rs = cs.executeQuery();
        cs2 = conn.prepareCall(query2);
        cs2.setInt(1, mes);
        cs2.setInt(2, tipoC);
        rs2 = cs2.executeQuery();
        /*En espera de que scob mande la informacion acumulada por partida para hacer los calculos y sacar el saldo de la GRMO, provicional se enviara fijo*/
        fileName = generaReporteBC(req, rs, rs2, plantillas.get("CONCILIABC"), fecha, tipo, saldo, saldoGRMO, unidad);
        return fileName;
    }

    public static String generaConciliacionBI(HttpServletRequest req, Connection conn, String tipo, String general, Map<String, String> plantillas, String fecha, String unidad) throws Exception {
        String fileName = "";
        CallableStatement cs = null, cs2 = null;
        ResultSet rs = null, rs2 = null;
        String query = "";
        String query2 = "";
        int mes = Integer.parseInt(fecha.substring(3, 5));
        String anio = fecha.substring(6, 10);
        int tipoC = Integer.parseInt(tipo);
        double saldo = consultaSaldos(conn, tipo, mes);
        double saldoGRMO = consultaSaldosGRMO(conn, tipo, mes);
        if ("7".equals(tipo)) {
            query = "{call sp_ConciliacionBienesInmuebles( ?, ?, ? )}";
            query2 = "{call sp_ConciliacionBienesInmueblesPartida( ?, ? )}";
        } else if ("6".equals(tipo)) {
            query = "{call sp_ConciliacionDepreciacionBI( ?, ?, ? )}";
            query2 = "{call sp_ConciliacionDepreciacionBIPartida( ?, ? )}";
        }
        cs = conn.prepareCall(query);
        cs.setInt(1, mes);
        cs.setInt(2, tipoC);
        cs.setString(3, anio);
        rs = cs.executeQuery();
        cs2 = conn.prepareCall(query2);
        cs2.setInt(1, mes);
        cs2.setInt(2, tipoC);
        rs2 = cs2.executeQuery();
        fileName = generaReporteBI(req, rs, rs2, plantillas.get("CONCILIABI"), fecha, tipo, saldo, saldoGRMO, unidad);
        return fileName;
    }

    public static double consultaSaldos(Connection conn, String tipo, int mes) throws Exception {
        double saldo = 0.00;
        ResultSet rs = null;
        PreparedStatement ps = null;
        String cuenta = "";
        if ("1".equals(tipo)) {
            cuenta = "nCuenta LIKE '124%00001%' OR nCuenta LIKE '125%00001%'";
        } else if ("2".equals(tipo)) {
            cuenta = "nCuenta LIKE '126%00001%' AND cSubCuenta NOT LIKE '%58301%'";
        } else if ("3".equals(tipo)) {
            cuenta = "nCuenta LIKE '124%00002%'";
        } else if ("5".equals(tipo)) {
            cuenta = "nCuenta LIKE '1151%'";
        } else if ("7".equals(tipo)) {
            cuenta = "nCuenta in ('12311-00000-00000-00001','12313-00000-00000-00000','12332-00001-00000-00001'" + " ,'12332-00003-00000-00000','12332-00004-00000-00000'/*,'12362-00001-00000-00001'*/,'12369-00001-00000-00001') " + " AND cSubCuenta IN ('58101','58301','62201','62202','62905')";
        } else if ("6".equals(tipo)) {
            cuenta = "nCuenta LIKE '126%00001%' AND cSubCuenta LIKE '%58301%'";
        }
        String query2 = "SELECT ISNULL(CASE WHEN " + mes + " = 1 THEN SUM(mSaldo1) " + "	WHEN " + mes + " = 2 THEN SUM(mSaldo2) " + "	WHEN " + mes + " = 3 THEN SUM(mSaldo3) " + "	WHEN " + mes + " = 4 THEN SUM(mSaldo4) " + "	WHEN " + mes + " = 5 THEN SUM(mSaldo5) " + "	WHEN " + mes + " = 6 THEN SUM(mSaldo6) " + "	WHEN " + mes + " = 7 THEN SUM(mSaldo7) " + "	WHEN " + mes + " = 8 THEN SUM(mSaldo8) " + "	WHEN " + mes + " = 9 THEN SUM(mSaldo9) " + "	WHEN " + mes + " = 10 THEN SUM(mSaldo10) " + "	WHEN " + mes + " = 11 THEN SUM(mSaldo11) " + "	WHEN " + mes + " = 12 THEN SUM(mSaldo12) " + "	END, 0) AS saldoFin " + "	FROM TSALDOS " + " WHERE " + cuenta;
        try {
            ps = conn.prepareStatement(query2);
            rs = ps.executeQuery();
            if (rs.next()) {
                saldo = rs.getDouble("saldoFin");
            }
            return saldo;
        } finally {
            CloseObject.closeObject(rs, false);
        }
    }

    public static double consultaSaldosGRMO(Connection conn, String tipo, int mes) throws Exception {
        double sIni = 0.00;
        double cargo = 0.00;
        double abono = 0.00;
        double sFin = 0.00;
        //BigDecimal sFin;
        ResultSet rs = null, rs2 = null, rs3 = null;
        PreparedStatement ps = null, ps2 = null, ps3 = null;
        String cuenta = "";
        String cargoAlmacen = "";
        String abonoAlmacen = "";
        if ("1".equals(tipo)) {
            cuenta = "nCuenta LIKE '124%00001%' OR nCuenta LIKE '125%00001%'";
        } else if ("2".equals(tipo)) {
            cuenta = "nCuenta LIKE '126%00001%' AND cSubCuenta NOT LIKE '%58301%'";
        } else if ("5".equals(tipo)) {
            cuenta = "nCuenta LIKE '1151%'";
        } else if ("7".equals(tipo)) {
            cuenta = "nCuenta in ('12311-00000-00000-00001','12313-00000-00000-00000','12332-00001-00000-00001'" + " ,'12332-00003-00000-00000','12332-00004-00000-00000'/*,'12362-00001-00000-00001'*/,'12369-00001-00000-00001') " + " AND cSubCuenta IN ('58101','58301','62201','62202','62905')";
        } else if ("6".equals(tipo)) {
            cuenta = "nCuenta LIKE '126%00001%' AND cSubCuenta LIKE '%58301%'";
        }
        String saldoInicial = "SELECT SUM(mSaldo0) AS saldoInicial FROM tSaldos WHERE " + cuenta;
        if ("1".equals(tipo) || "5".equals(tipo) || "7".equals(tipo)) {
            cargoAlmacen = "SELECT ROUND(SUM(mImporte),2) AS cargo" + "	FROM tSistemaControlInventarios " + "	WHERE idTipoConciliacion = " + tipo + "		AND nMes = " + mes + "		AND (cTipoPoliza LIKE 'entrada%' OR cTipoPoliza LIKE 'alta%') ";
            abonoAlmacen = "SELECT ROUND(SUM(mImporte),2) AS abono" + "	FROM tSistemaControlInventarios " + "	WHERE idTipoConciliacion = " + tipo + "		AND nMes = " + mes + "		AND (cTipoPoliza LIKE 'salida%' OR cTipoPoliza LIKE 'baja%') ";
        } else if ("2".equals(tipo) || "6".equals(tipo)) {
            cargoAlmacen = "SELECT ROUND(SUM(mImporte),2) AS cargo" + "	FROM tSistemaControlInventarios " + "	WHERE idTipoConciliacion = " + tipo + "		AND nMes = " + mes + " AND (SUBSTRING(cTipoPoliza,1,6) = 'salida' OR SUBSTRING(cTipoPoliza,14,5) = 'bajas' OR SUBSTRING(cTipoPoliza,14,6) IN ('actual','anteri'))";
            abonoAlmacen = "SELECT ROUND(SUM(mImporte),2) AS abono" + "	FROM tSistemaControlInventarios " + "	WHERE idTipoConciliacion = " + tipo + "		AND nMes = " + mes + " AND (SUBSTRING(cTipoPoliza,14,7) = 'mensual' OR SUBSTRING(cTipoPoliza,1,6) = 'entrad' OR SUBSTRING(cTipoPoliza,14,5) = 'altas')";
        }
        try {
            ps = conn.prepareStatement(saldoInicial);
            ps2 = conn.prepareStatement(cargoAlmacen);
            ps3 = conn.prepareStatement(abonoAlmacen);
            rs = ps.executeQuery();
            if (rs.next()) {
                sIni = rs.getDouble("saldoInicial");
            }
            rs2 = ps2.executeQuery();
            if (rs2.next()) {
                cargo = rs2.getDouble("cargo");
            }
            rs3 = ps3.executeQuery();
            if (rs3.next()) {
                abono = rs3.getDouble("abono");
            }
            /*Calulo des saldo de acuardo al naturaleza de la cuenta*/
            if ("1".equals(tipo) || "5".equals(tipo) || "7".equals(tipo)) {
                //DEUDORA
                sFin = sIni + cargo - abono;
                //sFin = new BigDecimal(sIni + cargo - abono);
            } else if ("2".equals(tipo) || "6".equals(tipo)) {
                //ACREEDORA
                sFin = sIni - cargo + abono;
                //sFin = new BigDecimal(sIni - cargo + abono);
            }
            return sFin;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(rs2, false);
            CloseObject.closeObject(rs3, false);
        }
    }

    private static String generaReporteBM(HttpServletRequest req, ResultSet rs, ResultSet rs2, String plantillaPath, String fecha, String tipo, double saldo, double saldoGRMO, String unidad) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "ConciliacionDeBienesMuebles" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        ResultSetMetaData rsMetadata = rs.getMetaData();
        ResultSetMetaData rs2Metadata = rs2.getMetaData();
        Sheet sheet0 = workbook.getSheetAt(0);
        Sheet sheet1 = workbook.getSheetAt(1);
        String encabezado = "";
        int mesFin = 0;
        String anio = "";
        String periodo = "";
        String firmaGRF1 = req.getParameter("nombre1");
        String firmaGRF2 = req.getParameter("nombre2");
        String firmaGRF3 = req.getParameter("nombre3");
        String firmaGRMO1 = req.getParameter("nombre4");
        String firmaGRMO2 = req.getParameter("nombre5");
        String firmaGRMO3 = req.getParameter("nombre6");
        /*Suma de subtotales*/
        double suma_binv = 0.00;
        double suma_bconta = 0.00;
        double suma_ainv = 0.00;
        double suma_aconta = 0.00;
        /*contados para las sumas de los subtotales*/
        int s_cont_binv = 12;
        int s_cont_bconta = 18;
        int s_cont_ainv = 24;
        int s_cont_aconta = 30;
        /*contador para los renglones en cada seccion*/
        int cont_binv = 15;
        int cont_bconta = 21;
        int cont_ainv = 27;
        int cont_aconta = 33;
        /*contador para los calculos del saldo y diferencia*/
        int cont_saldoCalculado = 36;
        int cont_diferencia = 38;
        mesFin = Integer.parseInt(fecha.substring(3, 5));
        anio = fecha.substring(6, 10);
        periodo = Util.NOMBRE_MESES_MX[mesFin - 1] + " " + anio;
        if ("1".equals(tipo)) {
            encabezado = "BIENES MUEBLES E INTANGIBLES";
        } else if ("2".equals(tipo)) {
            encabezado = "DEPRECIACION DE BIENES MUEBLES E INTANGIBLES";
        } else if ("3".equals(tipo)) {
            encabezado = "RE EXPRESION DE BIENES MUEBLES E INTANGIBLES";
        } else if ("4".equals(tipo)) {
            encabezado = "RE EXPRESION DE DEPRECIACION DE BIENES MUEBLES E INTANGIBLES";
        }
        CellStyle estiloTabla = workbook.createCellStyle();
        estiloTabla.setBorderRight(BorderStyle.HAIR);
        estiloTabla.setBorderLeft(BorderStyle.HAIR);
        estiloTabla.setBorderTop(BorderStyle.HAIR);
        estiloTabla.setBorderBottom(BorderStyle.HAIR);
        CellStyle estiloFirma = workbook.createCellStyle();
        estiloFirma.setBorderBottom(BorderStyle.THIN);
        DataFormat df = workbook.createDataFormat();
        CellStyle estiloMoneda = workbook.createCellStyle();
        estiloMoneda.setBorderRight(BorderStyle.HAIR);
        estiloMoneda.setBorderLeft(BorderStyle.HAIR);
        estiloMoneda.setBorderTop(BorderStyle.HAIR);
        estiloMoneda.setBorderBottom(BorderStyle.HAIR);
        estiloMoneda.setDataFormat(df.getFormat("#,###,##0.00"));
        DataFormat df2 = workbook.createDataFormat();
        CellStyle estiloMoneda2 = workbook.createCellStyle();
        estiloMoneda2.setDataFormat(df2.getFormat("#,###,##0.00"));
        Row rwEnc1 = (sheet0.getRow(4) == null ? sheet0.createRow(4) : sheet0.getRow(4));
        Cell cell1 = (rwEnc1.getCell(1) == null ? rwEnc1.createCell(1) : rwEnc1.getCell(1));
        cell1.setCellValue("CONCILIACIÓN DE INVENTARIO DE ACTIVOS DE " + encabezado);
        Row rwEnc11 = (sheet1.getRow(4) == null ? sheet1.createRow(4) : sheet1.getRow(4));
        Cell cell11 = (rwEnc11.getCell(1) == null ? rwEnc11.createCell(1) : rwEnc11.getCell(1));
        cell11.setCellValue("CONCILIACIÓN DE INVENTARIO DE ACTIVOS DE " + encabezado);
        Row rwEnc2 = (sheet0.getRow(8) == null ? sheet0.createRow(8) : sheet0.getRow(8));
        Cell cell2 = (rwEnc2.getCell(2) == null ? rwEnc2.createCell(2) : rwEnc2.getCell(2));
        cell2.setCellValue(periodo);
        Row rwEnc42 = (sheet1.getRow(8) == null ? sheet1.createRow(8) : sheet1.getRow(8));
        Cell cell42 = (rwEnc42.getCell(1) == null ? rwEnc42.createCell(1) : rwEnc42.getCell(1));
        cell42.setCellValue("PERIODO: " + periodo);
        Row rwEnc3 = (sheet0.getRow(37) == null ? sheet0.createRow(37) : sheet0.getRow(37));
        Cell cell3 = (rwEnc3.getCell(6) == null ? rwEnc3.createCell(6) : rwEnc3.getCell(6));
        cell3.setCellStyle(estiloMoneda2);
        cell3.setCellValue(saldo);
        Row rwEnc43 = (sheet1.getRow(37) == null ? sheet1.createRow(37) : sheet1.getRow(37));
        Cell cell43 = (rwEnc43.getCell(4) == null ? rwEnc43.createCell(4) : rwEnc43.getCell(4));
        cell43.setCellStyle(estiloMoneda2);
        cell43.setCellValue(saldo);
        if (!"".equals(firmaGRF1)) {
            Row rwEnc18 = (sheet0.getRow(45) == null ? sheet0.createRow(45) : sheet0.getRow(45));
            Cell cell18 = (rwEnc18.getCell(1) == null ? rwEnc18.createCell(1) : rwEnc18.getCell(1));
            cell18.setCellValue(firmaGRF1);
            Row rwEnc19 = (sheet0.getRow(45) == null ? sheet0.createRow(45) : sheet0.getRow(45));
            for (int i = 0; i < 4; i++) {
                Cell cell19 = rwEnc19.createCell(3 + i);
                cell19.setCellStyle(estiloFirma);
            }
            Row rwEnc30 = (sheet1.getRow(45) == null ? sheet1.createRow(45) : sheet1.getRow(45));
            Cell cell30 = (rwEnc30.getCell(1) == null ? rwEnc30.createCell(1) : rwEnc30.getCell(1));
            cell30.setCellValue(firmaGRF1);
            Row rwEnc31 = (sheet1.getRow(46) == null ? sheet1.createRow(46) : sheet1.getRow(46));
            for (int j = 0; j < 4; j++) {
                Cell cell31 = rwEnc31.createCell(3 + j);
                cell31.setCellStyle(estiloFirma);
            }
        }
        if (!"".equals(firmaGRF2)) {
            Row rwEnc20 = (sheet0.getRow(47) == null ? sheet0.createRow(47) : sheet0.getRow(47));
            Cell cell20 = (rwEnc20.getCell(1) == null ? rwEnc20.createCell(1) : rwEnc20.getCell(1));
            cell20.setCellValue(firmaGRF2);
            Row rwEnc21 = (sheet0.getRow(47) == null ? sheet0.createRow(47) : sheet0.getRow(47));
            for (int i = 0; i < 4; i++) {
                Cell cell21 = rwEnc21.getCell(3 + i);
                cell21.setCellStyle(estiloFirma);
            }
            Row rwEnc32 = (sheet1.getRow(47) == null ? sheet1.createRow(47) : sheet1.getRow(47));
            Cell cell32 = (rwEnc32.getCell(1) == null ? rwEnc32.createCell(1) : rwEnc32.getCell(1));
            cell32.setCellValue(firmaGRF2);
            Row rwEnc33 = (sheet1.getRow(47) == null ? sheet1.createRow(47) : sheet1.getRow(47));
            for (int i = 0; i < 4; i++) {
                Cell cell33 = rwEnc33.createCell(3 + i);
                cell33.setCellStyle(estiloFirma);
            }
        }
        if (!"".equals(firmaGRF3)) {
            Row rwEnc22 = (sheet0.getRow(49) == null ? sheet0.createRow(49) : sheet0.getRow(49));
            Cell cell22 = (rwEnc22.getCell(1) == null ? rwEnc22.createCell(1) : rwEnc22.getCell(1));
            cell22.setCellValue(firmaGRF3);
            Row rwEnc23 = (sheet0.getRow(49) == null ? sheet0.createRow(49) : sheet0.getRow(49));
            for (int i = 0; i < 4; i++) {
                Cell cell23 = rwEnc23.getCell(3 + i);
                cell23.setCellStyle(estiloFirma);
            }
            Row rwEnc34 = (sheet1.getRow(49) == null ? sheet1.createRow(49) : sheet1.getRow(49));
            Cell cell34 = (rwEnc34.getCell(1) == null ? rwEnc34.createCell(1) : rwEnc34.getCell(1));
            cell34.setCellValue(firmaGRF3);
            Row rwEnc35 = (sheet1.getRow(49) == null ? sheet1.createRow(49) : sheet1.getRow(49));
            for (int i = 0; i < 4; i++) {
                Cell cell35 = rwEnc35.createCell(3 + i);
                cell35.setCellStyle(estiloFirma);
            }
        }
        if (!"".equals(firmaGRMO1)) {
            Row rwEnc24 = (sheet0.getRow(53) == null ? sheet0.createRow(53) : sheet0.getRow(53));
            Cell cell24 = (rwEnc24.getCell(1) == null ? rwEnc24.createCell(1) : rwEnc24.getCell(1));
            cell24.setCellValue(firmaGRMO1);
            Row rwEnc25 = (sheet0.getRow(53) == null ? sheet0.createRow(53) : sheet0.getRow(53));
            for (int i = 0; i < 4; i++) {
                Cell cell25 = rwEnc25.getCell(3 + i);
                cell25.setCellStyle(estiloFirma);
            }
            Row rwEnc36 = (sheet1.getRow(53) == null ? sheet1.createRow(53) : sheet1.getRow(53));
            Cell cell36 = (rwEnc36.getCell(1) == null ? rwEnc36.createCell(1) : rwEnc36.getCell(1));
            cell36.setCellValue(firmaGRMO1);
            Row rwEnc37 = (sheet1.getRow(53) == null ? sheet1.createRow(53) : sheet1.getRow(53));
            for (int i = 0; i < 4; i++) {
                Cell cell37 = rwEnc37.createCell(3 + i);
                cell37.setCellStyle(estiloFirma);
            }
        }
        if (!"".equals(firmaGRMO2)) {
            Row rwEnc26 = (sheet0.getRow(55) == null ? sheet0.getRow(55) : sheet0.getRow(55));
            Cell cell26 = (rwEnc26.getCell(1) == null ? rwEnc26.createCell(1) : rwEnc26.getCell(1));
            cell26.setCellValue(firmaGRMO2);
            Row rwEnc27 = (sheet0.getRow(55) == null ? sheet0.getRow(55) : sheet0.getRow(55));
            for (int i = 0; i < 4; i++) {
                Cell cell27 = rwEnc27.createCell(3 + i);
                cell27.setCellStyle(estiloFirma);
            }
            Row rwEnc38 = (sheet1.getRow(55) == null ? sheet1.createRow(55) : sheet1.getRow(55));
            Cell cell38 = (rwEnc38.getCell(1) == null ? rwEnc38.createCell(1) : rwEnc38.getCell(1));
            cell38.setCellValue(firmaGRMO2);
            Row rwEnc39 = (sheet1.getRow(55) == null ? sheet1.createRow(55) : sheet1.getRow(55));
            for (int i = 0; i < 4; i++) {
                Cell cell39 = rwEnc39.createCell(3 + i);
                cell39.setCellStyle(estiloFirma);
            }
        }
        if (!"".equals(firmaGRMO3)) {
            Row rwEnc28 = (sheet0.getRow(57) == null ? sheet0.createRow(57) : sheet0.getRow(57));
            Cell cell28 = (rwEnc28.getCell(1) == null ? rwEnc28.createCell(1) : rwEnc28.getCell(1));
            cell28.setCellValue(firmaGRMO3);
            Row rwEnc29 = (sheet0.getRow(57) == null ? sheet0.createRow(57) : sheet0.getRow(57));
            for (int i = 0; i < 4; i++) {
                Cell cell29 = rwEnc29.createCell(3 + i);
                cell29.setCellStyle(estiloFirma);
            }
            Row rwEnc40 = (sheet1.getRow(57) == null ? sheet1.createRow(57) : sheet1.getRow(57));
            Cell cell40 = (rwEnc40.getCell(1) == null ? rwEnc40.createCell(1) : rwEnc40.getCell(1));
            cell40.setCellValue(firmaGRMO3);
            Row rwEnc41 = (sheet1.getRow(57) == null ? sheet1.createRow(57) : sheet1.getRow(57));
            for (int i = 0; i < 4; i++) {
                Cell cell41 = rwEnc41.createCell(3 + i);
                cell41.setCellStyle(estiloFirma);
            }
        }
        while (rs.next()) {
            String seccion = rs.getString(1);
            if ("baja_inventario".equals(seccion)) {
                int rows = cont_binv;
                sheet0.shiftRows(rows, cont_aconta + 30, 1);
                Row rw = (sheet0.getRow(cont_binv) == null ? sheet0.createRow(cont_binv) : sheet0.getRow(cont_binv));
                /*recorre toda la fila del rs para guardar en el excel*/
                for (int i = 1; i < 5; i++) {
                    if (i < 4) {
                        Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
                    } else
                        Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloMoneda);
                }
                /*suma del subtotal*/
                suma_binv = suma_binv + rs.getDouble(5);
                /*se incrementa el renglon que se inserto en la seccion*/
                cont_binv++;
                cont_bconta++;
                cont_ainv++;
                cont_aconta++;
                s_cont_bconta++;
                s_cont_ainv++;
                s_cont_aconta++;
                cont_saldoCalculado++;
                cont_diferencia++;
            } else if ("baja_contabilidad".equals(seccion) || "salida_contable".equals(seccion)) {
                int rows = cont_bconta;
                sheet0.shiftRows(rows, cont_aconta + 30, 1);
                Row rw = (sheet0.getRow(cont_bconta) == null ? sheet0.createRow(cont_bconta) : sheet0.getRow(cont_bconta));
                /*recorre toda la fila del rs para guardar en el excel*/
                for (int i = 1; i < 5; i++) {
                    if (i < 4) {
                        Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
                    } else
                        Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloMoneda);
                }
                /*suma del subtotal*/
                suma_bconta = suma_bconta + rs.getDouble(5);
                /*se incrementa el renglon que se inserto en la seccion*/
                cont_bconta++;
                cont_ainv++;
                cont_aconta++;
                s_cont_ainv++;
                s_cont_aconta++;
                cont_saldoCalculado++;
                cont_diferencia++;
            } else if ("alta_inventario".equals(seccion) || "mensual_inventario".equals(seccion)) {
                int rows = cont_ainv;
                sheet0.shiftRows(rows, cont_aconta + 30, 1);
                Row rw = (sheet0.getRow(cont_ainv) == null ? sheet0.createRow(cont_ainv) : sheet0.getRow(cont_ainv));
                /*recorre toda la fila del rs para guardar en el excel*/
                for (int i = 1; i < 5; i++) {
                    Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
                }
                /*suma del subtotal*/
                suma_ainv = suma_ainv + rs.getDouble(5);
                /*se incrementa el renglon que se inserto en la seccion*/
                cont_ainv++;
                cont_aconta++;
                s_cont_aconta++;
                cont_saldoCalculado++;
                cont_diferencia++;
            } else if ("alta_contabilidad".equals(seccion) || "mensual_contable".equals(seccion)) {
                int rows = cont_aconta;
                sheet0.shiftRows(rows, cont_aconta + 30, 1);
                Row rw = (sheet0.getRow(cont_aconta) == null ? sheet0.createRow(cont_aconta) : sheet0.getRow(cont_aconta));
                /*recorre toda la fila del rs para guardar en el excel*/
                for (int i = 1; i < 5; i++) {
                    Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
                }
                /*suma del subtotal*/
                suma_aconta = suma_aconta + rs.getDouble(5);
                /*se incrementa el renglon que se inserto en la seccion*/
                cont_aconta++;
                cont_saldoCalculado++;
                cont_diferencia++;
            }
        }
        Row rwEnc4 = (sheet0.getRow(s_cont_binv) == null ? sheet0.createRow(s_cont_binv) : sheet0.getRow(s_cont_binv));
        Cell cell4 = (rwEnc4.getCell(6) == null ? rwEnc4.createCell(6) : rwEnc4.getCell(6));
        cell4.setCellStyle(estiloMoneda2);
        cell4.setCellValue(suma_binv);
        Row rwEnc5 = (sheet0.getRow(s_cont_bconta) == null ? sheet0.createRow(s_cont_bconta) : sheet0.getRow(s_cont_bconta));
        Cell cell5 = (rwEnc5.getCell(6) == null ? rwEnc5.createCell(6) : rwEnc5.getCell(6));
        cell5.setCellStyle(estiloMoneda2);
        cell5.setCellValue(suma_bconta);
        Row rwEnc6 = (sheet0.getRow(s_cont_ainv) == null ? sheet0.createRow(s_cont_ainv) : sheet0.getRow(s_cont_ainv));
        Cell cell6 = (rwEnc6.getCell(6) == null ? rwEnc6.createCell(6) : rwEnc6.getCell(6));
        cell6.setCellStyle(estiloMoneda2);
        cell6.setCellValue(suma_ainv);
        Row rwEnc7 = (sheet0.getRow(s_cont_aconta) == null ? sheet0.createRow(s_cont_aconta) : sheet0.getRow(s_cont_aconta));
        Cell cell7 = (rwEnc7.getCell(6) == null ? rwEnc7.createCell(6) : rwEnc7.getCell(6));
        cell7.setCellStyle(estiloMoneda2);
        cell7.setCellValue(suma_aconta);
        Row rwEnc8 = (sheet0.getRow(10) == null ? sheet0.createRow(10) : sheet0.getRow(10));
        Cell cell8 = (rwEnc8.getCell(6) == null ? rwEnc8.createCell(6) : rwEnc8.getCell(6));
        cell8.setCellStyle(estiloMoneda2);
        cell8.setCellValue(saldoGRMO);
        double saldoCalculado = saldoGRMO + suma_binv + suma_bconta - suma_ainv - suma_aconta;
        double diferencia = saldoCalculado - saldo;
        Row rwEnc9 = (sheet0.getRow(cont_saldoCalculado) == null ? sheet0.createRow(cont_saldoCalculado) : sheet0.getRow(cont_saldoCalculado));
        Cell cell9 = (rwEnc9.getCell(6) == null ? rwEnc9.createCell(6) : rwEnc9.getCell(6));
        cell9.setCellStyle(estiloMoneda2);
        cell9.setCellValue(saldoCalculado);
        Row rwEnc10 = (sheet0.getRow(cont_diferencia) == null ? sheet0.createRow(cont_diferencia) : sheet0.getRow(cont_diferencia));
        Cell cell10 = (rwEnc10.getCell(6) == null ? rwEnc10.createCell(6) : rwEnc10.getCell(6));
        cell10.setCellStyle(estiloMoneda2);
        cell10.setCellValue(diferencia);
        /*Suma de subtotales*/
        suma_aconta = 0.00;
        suma_bconta = 0.00;
        suma_binv = 0.00;
        suma_ainv = 0.00;
        /*contados para las sumas de los subtotales*/
        s_cont_binv = 12;
        s_cont_bconta = 18;
        s_cont_ainv = 24;
        s_cont_aconta = 30;
        /*contador para los renglones en cada seccion*/
        cont_binv = 15;
        cont_bconta = 21;
        cont_ainv = 27;
        cont_aconta = 33;
        /*contador para los calculos del saldo y diferencia*/
        cont_saldoCalculado = 36;
        cont_diferencia = 38;
        while (rs2.next()) {
            String seccion = rs2.getString(1);
            if ("A_inventario".equals(seccion) || "B_inventario".equals(seccion)) {
                int rows = cont_binv;
                sheet1.shiftRows(rows, cont_aconta + 30, 1);
                Row rw = (sheet1.getRow(cont_binv) == null ? sheet1.createRow(cont_binv) : sheet1.getRow(cont_binv));
                /*recorre toda la fila del rs2 para guardar en el excel*/
                for (int i = 1; i < 3; i++) {
                    if (i <= 2) {
                        Util.createExcelCellRep(i, rw, rs2, rs2Metadata.getColumnName(i + 1), rs2Metadata.getColumnType(i + 1), estiloTabla);
                    } else
                        Util.createExcelCellRep(i, rw, rs2, rs2Metadata.getColumnName(i + 1), rs2Metadata.getColumnType(i + 1), estiloMoneda);
                }
                /*suma del subtotal*/
                suma_binv = suma_binv + rs2.getDouble(3);
                /*se incrementa el renglon que se inserto en la seccion*/
                cont_binv++;
                cont_bconta++;
                cont_ainv++;
                cont_aconta++;
                s_cont_bconta++;
                s_cont_ainv++;
                s_cont_aconta++;
                cont_saldoCalculado++;
                cont_diferencia++;
            } else if ("A_contabilidad".equals(seccion) || "B_contabilidad".equals(seccion)) {
                int rows = cont_bconta;
                sheet1.shiftRows(rows, cont_aconta + 30, 1);
                Row rw = (sheet1.getRow(cont_bconta) == null ? sheet1.createRow(cont_bconta) : sheet1.getRow(cont_bconta));
                /*recorre toda la fila del rs2 para guardar en el excel*/
                for (int i = 1; i < 3; i++) {
                    if (i < 2) {
                        Util.createExcelCellRep(i, rw, rs2, rs2Metadata.getColumnName(i + 1), rs2Metadata.getColumnType(i + 1), estiloTabla);
                    } else
                        Util.createExcelCellRep(i, rw, rs2, rs2Metadata.getColumnName(i + 1), rs2Metadata.getColumnType(i + 1), estiloMoneda);
                }
                /*suma del subtotal*/
                suma_bconta = suma_bconta + rs2.getDouble(3);
                /*se incrementa el renglon que se inserto en la seccion*/
                cont_bconta++;
                cont_ainv++;
                cont_aconta++;
                s_cont_ainv++;
                s_cont_aconta++;
                cont_saldoCalculado++;
                cont_diferencia++;
            } else if ("C_inventario".equals(seccion) || "D_inventario".equals(seccion)) {
                int rows = cont_ainv;
                sheet1.shiftRows(rows, cont_aconta + 30, 1);
                Row rw = (sheet1.getRow(cont_ainv) == null ? sheet1.createRow(cont_ainv) : sheet1.getRow(cont_ainv));
                /*recorre toda la fila del rs2 para guardar en el excel*/
                for (int i = 1; i < 3; i++) {
                    if (i < 2) {
                        Util.createExcelCellRep(i, rw, rs2, rs2Metadata.getColumnName(i + 1), rs2Metadata.getColumnType(i + 1), estiloTabla);
                    } else
                        Util.createExcelCellRep(i, rw, rs2, rs2Metadata.getColumnName(i + 1), rs2Metadata.getColumnType(i + 1), estiloMoneda);
                }
                /*suma del subtotal*/
                suma_ainv = suma_ainv + rs2.getDouble(3);
                /*se incrementa el renglon que se inserto en la seccion*/
                cont_ainv++;
                cont_aconta++;
                s_cont_aconta++;
                cont_saldoCalculado++;
                cont_diferencia++;
            } else if ("C_contabilidad".equals(seccion) || "D_contabilidad".equals(seccion)) {
                int rows = cont_aconta;
                sheet1.shiftRows(rows, cont_aconta + 30, 1);
                Row rw = (sheet1.getRow(cont_aconta) == null ? sheet1.createRow(cont_aconta) : sheet1.getRow(cont_aconta));
                /*recorre toda la fila del rs2 para guardar en el excel*/
                for (int i = 1; i < 3; i++) {
                    if (i < 2) {
                        Util.createExcelCellRep(i, rw, rs2, rs2Metadata.getColumnName(i + 1), rs2Metadata.getColumnType(i + 1), estiloTabla);
                    } else
                        Util.createExcelCellRep(i, rw, rs2, rs2Metadata.getColumnName(i + 1), rs2Metadata.getColumnType(i + 1), estiloMoneda);
                }
                /*suma del subtotal*/
                suma_aconta = suma_aconta + rs2.getDouble(3);
                /*se incrementa el renglon que se inserto en la seccion*/
                cont_aconta++;
                cont_saldoCalculado++;
                cont_diferencia++;
            }
        }
        Row rwEnc12 = (sheet1.getRow(s_cont_binv) == null ? sheet1.createRow(s_cont_binv) : sheet1.getRow(s_cont_binv));
        Cell cell12 = (rwEnc12.getCell(4) == null ? rwEnc12.createCell(4) : rwEnc12.getCell(4));
        cell12.setCellStyle(estiloMoneda2);
        cell12.setCellValue(suma_binv);
        Row rwEnc13 = (sheet1.getRow(s_cont_bconta) == null ? sheet1.createRow(s_cont_bconta) : sheet1.getRow(s_cont_bconta));
        Cell cell13 = (rwEnc13.getCell(4) == null ? rwEnc13.createCell(4) : rwEnc13.getCell(4));
        cell13.setCellStyle(estiloMoneda2);
        cell13.setCellValue(suma_bconta);
        Row rwEnc14 = (sheet1.getRow(s_cont_ainv) == null ? sheet1.createRow(s_cont_ainv) : sheet1.getRow(s_cont_ainv));
        Cell cell14 = (rwEnc14.getCell(4) == null ? rwEnc14.createCell(4) : rwEnc14.getCell(4));
        cell14.setCellStyle(estiloMoneda2);
        cell14.setCellValue(suma_ainv);
        Row rwEnc15 = (sheet1.getRow(s_cont_aconta) == null ? sheet1.createRow(s_cont_aconta) : sheet1.getRow(s_cont_aconta));
        Cell cell15 = (rwEnc15.getCell(4) == null ? rwEnc15.createCell(4) : rwEnc15.getCell(4));
        cell15.setCellStyle(estiloMoneda2);
        cell15.setCellValue(suma_aconta);
        Row rwEnc44 = (sheet1.getRow(10) == null ? sheet1.createRow(10) : sheet1.getRow(10));
        Cell cell44 = (rwEnc44.getCell(4) == null ? rwEnc44.createCell(4) : rwEnc44.getCell(4));
        cell44.setCellStyle(estiloMoneda2);
        cell44.setCellValue(saldoGRMO);
        saldoCalculado = saldoGRMO + suma_binv - suma_bconta - suma_ainv + suma_aconta;
        diferencia = saldoCalculado - saldo;
        Row rwEnc16 = (sheet1.getRow(cont_saldoCalculado) == null ? sheet1.createRow(cont_saldoCalculado) : sheet1.getRow(cont_saldoCalculado));
        Cell cell16 = (rwEnc16.getCell(4) == null ? rwEnc16.createCell(4) : rwEnc16.getCell(4));
        cell16.setCellStyle(estiloMoneda2);
        cell16.setCellValue(saldoCalculado);
        Row rwEnc17 = (sheet1.getRow(cont_diferencia) == null ? sheet1.createRow(cont_diferencia) : sheet1.getRow(cont_diferencia));
        Cell cell17 = (rwEnc17.getCell(4) == null ? rwEnc17.createCell(4) : rwEnc17.getCell(4));
        cell17.setCellStyle(estiloMoneda2);
        cell17.setCellValue(diferencia);
        Row rwEnc18 = (sheet0.getRow(2) == null ? sheet0.createRow(2) : sheet0.getRow(2));
        Cell cell18 = (rwEnc18.getCell(1) == null ? rwEnc18.createCell(1) : rwEnc18.getCell(1));
        cell18.setCellValue(unidad);
        Row rwEnc19 = (sheet1.getRow(2) == null ? sheet1.createRow(2) : sheet1.getRow(2));
        Cell cell19 = (rwEnc19.getCell(1) == null ? rwEnc19.createCell(1) : rwEnc19.getCell(1));
        cell19.setCellValue(unidad);
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

    private static String generaReporteBC(HttpServletRequest req, ResultSet rs, ResultSet rs2, String plantillaPath, String fecha, String tipo, double saldo, double saldoGRMO, String unidad) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "ConciliacionDeBienesConsumibles" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        ResultSetMetaData rsMetadata = rs.getMetaData();
        ResultSetMetaData rs2Metadata = rs2.getMetaData();
        Sheet sheet0 = workbook.getSheetAt(0);
        Sheet sheet1 = workbook.getSheetAt(1);
        int mesFin = 0;
        String anio = "";
        String periodo = "";
        String firmaGRF1 = req.getParameter("nombre1");
        String firmaGRF2 = req.getParameter("nombre2");
        String firmaGRF3 = req.getParameter("nombre3");
        String firmaGRMO1 = req.getParameter("nombre4");
        String firmaGRMO2 = req.getParameter("nombre5");
        String firmaGRMO3 = req.getParameter("nombre6");
        /*Suma de subtotales*/
        double suma_aconta = 0.00;
        double suma_sconta = 0.00;
        double suma_sinv = 0.00;
        double suma_ainv = 0.00;
        /*contados para las sumas de los subtotales*/
        int s_cont_aconta = 12;
        int s_cont_sconta = 18;
        int s_cont_sinv = 24;
        int s_cont_ainv = 30;
        /*contador para los renglones en cada seccion*/
        int cont_aconta = 15;
        int cont_sconta = 21;
        int cont_sinv = 27;
        int cont_ainv = 33;
        /*contador para los calculos del saldo y diferencia*/
        int cont_saldoCalculado = 36;
        int cont_diferencia = 38;
        mesFin = Integer.parseInt(fecha.substring(3, 5));
        anio = fecha.substring(6, 10);
        periodo = Util.NOMBRE_MESES_MX[mesFin - 1] + " " + anio;
        CellStyle estiloTabla = workbook.createCellStyle();
        estiloTabla.setBorderRight(BorderStyle.HAIR);
        estiloTabla.setBorderLeft(BorderStyle.HAIR);
        estiloTabla.setBorderTop(BorderStyle.HAIR);
        estiloTabla.setBorderBottom(BorderStyle.HAIR);
        CellStyle estiloFirma = workbook.createCellStyle();
        estiloFirma.setBorderBottom(BorderStyle.THIN);
        DataFormat df = workbook.createDataFormat();
        CellStyle estiloMoneda = workbook.createCellStyle();
        estiloMoneda.setBorderRight(BorderStyle.HAIR);
        estiloMoneda.setBorderLeft(BorderStyle.HAIR);
        estiloMoneda.setBorderTop(BorderStyle.HAIR);
        estiloMoneda.setBorderBottom(BorderStyle.HAIR);
        estiloMoneda.setDataFormat(df.getFormat("#,###,##0.00"));
        DataFormat df2 = workbook.createDataFormat();
        CellStyle estiloMoneda2 = workbook.createCellStyle();
        estiloMoneda2.setDataFormat(df2.getFormat("#,###,##0.00"));
        Row rwEnc2 = (sheet0.getRow(8) == null ? sheet0.createRow(8) : sheet0.getRow(8));
        Cell cell2 = (rwEnc2.getCell(2) == null ? rwEnc2.createCell(2) : rwEnc2.getCell(2));
        cell2.setCellValue(periodo);
        Row rwEnc = (sheet1.getRow(8) == null ? sheet1.createRow(8) : sheet1.getRow(8));
        Cell cell = (rwEnc.getCell(1) == null ? rwEnc.createCell(1) : rwEnc.getCell(1));
        cell.setCellValue("PERIODO: " + periodo);
        Row rwEnc3 = (sheet0.getRow(10) == null ? sheet0.createRow(10) : sheet0.getRow(10));
        Cell cell3 = (rwEnc3.getCell(6) == null ? rwEnc3.createCell(6) : rwEnc3.getCell(6));
        cell3.setCellStyle(estiloMoneda2);
        cell3.setCellValue(saldo);
        Row rwEnc1 = (sheet1.getRow(10) == null ? sheet1.createRow(10) : sheet1.getRow(10));
        Cell cell1 = (rwEnc1.getCell(4) == null ? rwEnc1.createCell(4) : rwEnc1.getCell(4));
        cell1.setCellValue(saldo);
        Row rwEnc8 = (sheet0.getRow(37) == null ? sheet0.createRow(37) : sheet0.getRow(37));
        Cell cell8 = (rwEnc8.getCell(6) == null ? rwEnc8.createCell(6) : rwEnc8.getCell(6));
        cell8.setCellStyle(estiloMoneda2);
        cell8.setCellValue(saldoGRMO);
        Row rwEnc11 = (sheet1.getRow(37) == null ? sheet1.createRow(37) : sheet1.getRow(37));
        Cell cell11 = (rwEnc11.getCell(4) == null ? rwEnc11.createCell(4) : rwEnc11.getCell(4));
        cell11.setCellStyle(estiloMoneda2);
        cell11.setCellValue(saldoGRMO);
        if (!"".equals(firmaGRF1)) {
            Row rwEnc18 = (sheet0.getRow(45) == null ? sheet0.createRow(45) : sheet0.getRow(45));
            Cell cell18 = (rwEnc18.getCell(1) == null ? rwEnc18.createCell(1) : rwEnc18.getCell(1));
            cell18.setCellValue(firmaGRF1);
            Row rwEnc19 = (sheet0.getRow(45) == null ? sheet0.createRow(45) : sheet0.getRow(45));
            Cell cell19 = (rwEnc19.getCell(3) == null ? rwEnc19.createCell(3) : rwEnc19.getCell(3));
            cell19.setCellStyle(estiloFirma);
            Row rwEnc30 = (sheet1.getRow(45) == null ? sheet1.createRow(45) : sheet1.getRow(45));
            Cell cell30 = (rwEnc30.getCell(1) == null ? rwEnc30.createCell(1) : rwEnc30.getCell(1));
            cell30.setCellValue(firmaGRF1);
            Row rwEnc31 = (sheet1.getRow(45) == null ? sheet1.createRow(45) : sheet1.getRow(45));
            Cell cell31 = (rwEnc31.getCell(2) == null ? rwEnc31.createCell(2) : rwEnc31.getCell(2));
            cell31.setCellStyle(estiloFirma);
        }
        if (!"".equals(firmaGRF2)) {
            Row rwEnc20 = (sheet0.getRow(47) == null ? sheet0.createRow(47) : sheet0.getRow(47));
            Cell cell20 = (rwEnc20.getCell(1) == null ? rwEnc20.createCell(1) : rwEnc20.getCell(1));
            cell20.setCellValue(firmaGRF2);
            Row rwEnc21 = (sheet0.getRow(47) == null ? sheet0.createRow(47) : sheet0.getRow(47));
            Cell cell21 = (rwEnc21.getCell(3) == null ? rwEnc21.createCell(3) : rwEnc21.getCell(3));
            cell21.setCellStyle(estiloFirma);
            Row rwEnc32 = (sheet1.getRow(47) == null ? sheet1.createRow(47) : sheet1.getRow(47));
            Cell cell32 = (rwEnc32.getCell(1) == null ? rwEnc32.createCell(1) : rwEnc32.getCell(1));
            cell32.setCellValue(firmaGRF2);
            Row rwEnc33 = (sheet1.getRow(47) == null ? sheet1.createRow(47) : sheet1.getRow(47));
            Cell cell33 = (rwEnc33.getCell(2) == null ? rwEnc33.createCell(2) : rwEnc33.getCell(2));
            cell33.setCellStyle(estiloFirma);
        }
        if (!"".equals(firmaGRF3)) {
            Row rwEnc22 = (sheet0.getRow(49) == null ? sheet0.createRow(49) : sheet0.getRow(49));
            Cell cell22 = (rwEnc22.getCell(1) == null ? rwEnc22.createCell(1) : rwEnc22.getCell(1));
            cell22.setCellValue(firmaGRF3);
            Row rwEnc23 = (sheet0.getRow(49) == null ? sheet0.createRow(49) : sheet0.getRow(49));
            Cell cell23 = (rwEnc23.getCell(3) == null ? rwEnc23.createCell(3) : rwEnc23.getCell(3));
            cell23.setCellStyle(estiloFirma);
            Row rwEnc34 = (sheet1.getRow(49) == null ? sheet1.createRow(49) : sheet1.getRow(49));
            Cell cell34 = (rwEnc34.getCell(1) == null ? rwEnc34.createCell(1) : rwEnc34.getCell(1));
            cell34.setCellValue(firmaGRF3);
            Row rwEnc35 = (sheet1.getRow(49) == null ? sheet1.createRow(49) : sheet1.getRow(49));
            Cell cell35 = (rwEnc35.getCell(2) == null ? rwEnc35.createCell(2) : rwEnc35.getCell(2));
            cell35.setCellStyle(estiloFirma);
        }
        if (!"".equals(firmaGRMO1)) {
            Row rwEnc24 = (sheet0.getRow(53) == null ? sheet0.createRow(53) : sheet0.getRow(53));
            Cell cell24 = (rwEnc24.getCell(1) == null ? rwEnc24.createCell(1) : rwEnc24.getCell(1));
            cell24.setCellValue(firmaGRMO1);
            Row rwEnc25 = (sheet0.getRow(53) == null ? sheet0.createRow(53) : sheet0.getRow(53));
            Cell cell25 = (rwEnc25.getCell(3) == null ? rwEnc25.createCell(3) : rwEnc25.getCell(3));
            cell25.setCellStyle(estiloFirma);
            Row rwEnc36 = (sheet1.getRow(53) == null ? sheet1.createRow(53) : sheet1.getRow(53));
            Cell cell36 = (rwEnc36.getCell(1) == null ? rwEnc36.createCell(1) : rwEnc36.getCell(1));
            cell36.setCellValue(firmaGRMO1);
            Row rwEnc37 = (sheet1.getRow(53) == null ? sheet1.createRow(53) : sheet1.getRow(53));
            Cell cell37 = (rwEnc37.getCell(2) == null ? rwEnc37.createCell(2) : rwEnc37.getCell(2));
            cell37.setCellStyle(estiloFirma);
        }
        if (!"".equals(firmaGRMO2)) {
            Row rwEnc26 = (sheet0.getRow(55) == null ? sheet0.createRow(55) : sheet0.getRow(55));
            Cell cell26 = (rwEnc26.getCell(1) == null ? rwEnc26.createCell(1) : rwEnc26.getCell(1));
            cell26.setCellValue(firmaGRMO2);
            Row rwEnc27 = (sheet0.getRow(55) == null ? sheet0.createRow(55) : sheet0.getRow(55));
            Cell cell27 = (rwEnc27.getCell(3) == null ? rwEnc27.createCell(3) : rwEnc27.getCell(3));
            cell27.setCellStyle(estiloFirma);
            Row rwEnc38 = (sheet1.getRow(55) == null ? sheet1.createRow(55) : sheet1.getRow(55));
            Cell cell38 = (rwEnc38.getCell(1) == null ? rwEnc38.createCell(1) : rwEnc38.getCell(1));
            cell38.setCellValue(firmaGRMO2);
            Row rwEnc39 = (sheet1.getRow(55) == null ? sheet1.createRow(55) : sheet1.getRow(55));
            Cell cell39 = (rwEnc39.getCell(2) == null ? rwEnc39.createCell(2) : rwEnc39.getCell(2));
            cell39.setCellStyle(estiloFirma);
        }
        if (!"".equals(firmaGRMO3)) {
            Row rwEnc28 = (sheet0.getRow(57) == null ? sheet0.createRow(57) : sheet0.getRow(57));
            Cell cell28 = (rwEnc28.getCell(1) == null ? rwEnc28.createCell(1) : rwEnc28.getCell(1));
            cell28.setCellValue(firmaGRMO3);
            Row rwEnc29 = (sheet0.getRow(57) == null ? sheet0.createRow(57) : sheet0.getRow(57));
            Cell cell29 = (rwEnc29.getCell(3) == null ? rwEnc29.createCell(3) : rwEnc29.getCell(3));
            cell29.setCellStyle(estiloFirma);
            Row rwEnc40 = (sheet1.getRow(57) == null ? sheet1.createRow(57) : sheet1.getRow(57));
            Cell cell40 = (rwEnc40.getCell(1) == null ? rwEnc40.createCell(1) : rwEnc40.getCell(1));
            cell40.setCellValue(firmaGRMO3);
            Row rwEnc41 = (sheet1.getRow(57) == null ? sheet1.createRow(57) : sheet1.getRow(57));
            Cell cell41 = (rwEnc41.getCell(2) == null ? rwEnc41.createCell(2) : rwEnc41.getCell(2));
            cell41.setCellStyle(estiloFirma);
        }
        while (rs.next()) {
            String seccion = rs.getString(1);
            if ("alta_contabilidad".equals(seccion)) {
                int rows = cont_aconta;
                sheet0.shiftRows(rows, cont_ainv + 30, 1);
                Row rw = (sheet0.getRow(cont_aconta) == null ? sheet0.createRow(cont_aconta) : sheet0.getRow(cont_aconta));
                /*recorre toda la fila del rs para guardar en el excel*/
                for (int i = 1; i < 5; i++) {
                    if (i < 4) {
                        Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
                    } else
                        Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloMoneda);
                }
                /*suma del subtotal*/
                suma_aconta = suma_aconta + rs.getDouble(5);
                /*se incrementa el renglon que se inserto en la seccion*/
                cont_aconta++;
                cont_sconta++;
                cont_sinv++;
                cont_ainv++;
                s_cont_sconta++;
                s_cont_sinv++;
                s_cont_ainv++;
                cont_saldoCalculado++;
                cont_diferencia++;
            } else if ("salida_contabilidad".equals(seccion)) {
                int rows = cont_sconta;
                sheet0.shiftRows(rows, cont_ainv + 30, 1);
                Row rw = (sheet0.getRow(cont_sconta) == null ? sheet0.createRow(cont_sconta) : sheet0.getRow(cont_sconta));
                /*recorre toda la fila del rs para guardar en el excel*/
                for (int i = 1; i < 5; i++) {
                    if (i < 4) {
                        Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
                    } else
                        Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloMoneda);
                }
                /*suma del subtotal*/
                suma_sconta = suma_sconta + rs.getDouble(5);
                /*se incrementa el renglon que se inserto en la seccion*/
                cont_sconta++;
                cont_sinv++;
                cont_ainv++;
                s_cont_sinv++;
                s_cont_ainv++;
                cont_saldoCalculado++;
                cont_diferencia++;
            } else if ("salida_inventario".equals(seccion)) {
                int rows = cont_sinv;
                sheet0.shiftRows(rows, cont_ainv + 30, 1);
                Row rw = (sheet0.getRow(cont_sinv) == null ? sheet0.createRow(cont_sinv) : sheet0.getRow(cont_sinv));
                /*recorre toda la fila del rs para guardar en el excel*/
                for (int i = 1; i < 5; i++) {
                    if (i < 4) {
                        Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
                    } else
                        Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloMoneda);
                }
                /*suma del subtotal*/
                suma_sinv = suma_sinv + rs.getDouble(5);
                /*se incrementa el renglon que se inserto en la seccion*/
                cont_sinv++;
                cont_ainv++;
                s_cont_ainv++;
                cont_saldoCalculado++;
                cont_diferencia++;
            } else if ("alta_inventario".equals(seccion)) {
                int rows = cont_ainv;
                sheet0.shiftRows(rows, cont_ainv + 30, 1);
                Row rw = (sheet0.getRow(cont_ainv) == null ? sheet0.createRow(cont_ainv) : sheet0.getRow(cont_ainv));
                /*recorre toda la fila del rs para guardar en el excel*/
                for (int i = 1; i < 5; i++) {
                    if (i < 4) {
                        Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
                    } else
                        Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloMoneda);
                }
                /*suma del subtotal*/
                suma_ainv = suma_ainv + rs.getDouble(5);
                /*se incrementa el renglon que se inserto en la seccion*/
                cont_ainv++;
                cont_saldoCalculado++;
                cont_diferencia++;
            }
        }
        Row rwEnc4 = (sheet0.getRow(s_cont_aconta) == null ? sheet0.createRow(s_cont_aconta) : sheet0.getRow(s_cont_aconta));
        Cell cell4 = (rwEnc4.getCell(6) == null ? rwEnc4.createCell(6) : rwEnc4.getCell(6));
        cell4.setCellStyle(estiloMoneda2);
        cell4.setCellValue(suma_aconta);
        Row rwEnc5 = (sheet0.getRow(s_cont_sconta) == null ? sheet0.createRow(s_cont_sconta) : sheet0.getRow(s_cont_sconta));
        Cell cell5 = (rwEnc5.getCell(6) == null ? rwEnc5.createCell(6) : rwEnc5.getCell(6));
        cell5.setCellStyle(estiloMoneda2);
        cell5.setCellValue(suma_sconta);
        Row rwEnc6 = (sheet0.getRow(s_cont_sinv) == null ? sheet0.createRow(s_cont_sinv) : sheet0.getRow(s_cont_sinv));
        Cell cell6 = (rwEnc6.getCell(6) == null ? rwEnc6.createCell(6) : rwEnc6.getCell(6));
        cell6.setCellStyle(estiloMoneda2);
        cell6.setCellValue(suma_sinv);
        Row rwEnc7 = (sheet0.getRow(s_cont_ainv) == null ? sheet0.createRow(s_cont_ainv) : sheet0.getRow(s_cont_ainv));
        Cell cell7 = (rwEnc7.getCell(6) == null ? rwEnc7.createCell(6) : rwEnc7.getCell(6));
        cell7.setCellStyle(estiloMoneda2);
        cell7.setCellValue(suma_ainv);
        double saldoCalculado = saldo - suma_aconta + suma_sconta - suma_sinv + suma_ainv;
        double diferencia = saldoCalculado - saldoGRMO;
        Row rwEnc9 = (sheet0.getRow(cont_saldoCalculado) == null ? sheet0.createRow(cont_saldoCalculado) : sheet0.getRow(cont_saldoCalculado));
        Cell cell9 = (rwEnc9.getCell(6) == null ? rwEnc9.createCell(6) : rwEnc9.getCell(6));
        cell9.setCellStyle(estiloMoneda2);
        cell9.setCellValue(saldoCalculado);
        Row rwEnc10 = (sheet0.getRow(cont_diferencia) == null ? sheet0.createRow(cont_diferencia) : sheet0.getRow(cont_diferencia));
        Cell cell10 = (rwEnc10.getCell(6) == null ? rwEnc10.createCell(6) : rwEnc10.getCell(6));
        cell10.setCellStyle(estiloMoneda2);
        cell10.setCellValue(diferencia);
        /*Suma de subtotales*/
        suma_aconta = 0.00;
        suma_sconta = 0.00;
        suma_sinv = 0.00;
        suma_ainv = 0.00;
        /*contados para las sumas de los subtotales*/
        s_cont_aconta = 12;
        s_cont_sconta = 18;
        s_cont_sinv = 24;
        s_cont_ainv = 30;
        /*contador para los renglones en cada seccion*/
        cont_aconta = 15;
        cont_sconta = 21;
        cont_sinv = 27;
        cont_ainv = 33;
        /*contador para los calculos del saldo y diferencia*/
        cont_saldoCalculado = 36;
        cont_diferencia = 38;
        while (rs2.next()) {
            String seccion = rs2.getString(1);
            if ("C_contabilidad".equals(seccion)) {
                int rows = cont_aconta;
                sheet1.shiftRows(rows, cont_ainv + 30, 1);
                Row rw = (sheet1.getRow(cont_aconta) == null ? sheet1.createRow(cont_aconta) : sheet1.getRow(cont_aconta));
                /*recorre toda la fila del rs2 para guardar en el excel*/
                for (int i = 1; i < 3; i++) {
                    if (i <= 2) {
                        Util.createExcelCellRep(i, rw, rs2, rs2Metadata.getColumnName(i + 1), rs2Metadata.getColumnType(i + 1), estiloTabla);
                    } else
                        Util.createExcelCellRep(i, rw, rs2, rs2Metadata.getColumnName(i + 1), rs2Metadata.getColumnType(i + 1), estiloMoneda);
                }
                /*suma del subtotal*/
                suma_aconta = suma_aconta + rs2.getDouble(3);
                /*se incrementa el renglon que se inserto en la seccion*/
                cont_aconta++;
                cont_sconta++;
                cont_sinv++;
                cont_ainv++;
                s_cont_sconta++;
                s_cont_sinv++;
                s_cont_ainv++;
                cont_saldoCalculado++;
                cont_diferencia++;
            } else if ("A_contabilidad".equals(seccion)) {
                int rows = cont_sconta;
                sheet1.shiftRows(rows, cont_ainv + 30, 1);
                Row rw = (sheet1.getRow(cont_sconta) == null ? sheet1.createRow(cont_sconta) : sheet1.getRow(cont_sconta));
                /*recorre toda la fila del rs2 para guardar en el excel*/
                for (int i = 1; i < 3; i++) {
                    if (i < 2) {
                        Util.createExcelCellRep(i, rw, rs2, rs2Metadata.getColumnName(i + 1), rs2Metadata.getColumnType(i + 1), estiloTabla);
                    } else
                        Util.createExcelCellRep(i, rw, rs2, rs2Metadata.getColumnName(i + 1), rs2Metadata.getColumnType(i + 1), estiloMoneda);
                }
                /*suma del subtotal*/
                suma_sconta = suma_sconta + rs2.getDouble(3);
                /*se incrementa el renglon que se inserto en la seccion*/
                cont_sconta++;
                cont_sinv++;
                cont_ainv++;
                s_cont_sinv++;
                s_cont_ainv++;
                cont_saldoCalculado++;
                cont_diferencia++;
            } else if ("A_inventario".equals(seccion)) {
                int rows = cont_sinv;
                sheet1.shiftRows(rows, cont_ainv + 30, 1);
                Row rw = (sheet1.getRow(cont_sinv) == null ? sheet1.createRow(cont_sinv) : sheet1.getRow(cont_sinv));
                /*recorre toda la fila del rs2 para guardar en el excel*/
                for (int i = 1; i < 3; i++) {
                    if (i < 2) {
                        Util.createExcelCellRep(i, rw, rs2, rs2Metadata.getColumnName(i + 1), rs2Metadata.getColumnType(i + 1), estiloTabla);
                    } else
                        Util.createExcelCellRep(i, rw, rs2, rs2Metadata.getColumnName(i + 1), rs2Metadata.getColumnType(i + 1), estiloMoneda);
                }
                /*suma del subtotal*/
                suma_sinv = suma_sinv + rs2.getDouble(3);
                /*se incrementa el renglon que se inserto en la seccion*/
                cont_sinv++;
                cont_ainv++;
                s_cont_ainv++;
                cont_saldoCalculado++;
                cont_diferencia++;
            } else if ("C_inventario".equals(seccion)) {
                int rows = cont_ainv;
                sheet1.shiftRows(rows, cont_ainv + 30, 1);
                Row rw = (sheet1.getRow(cont_ainv) == null ? sheet1.createRow(cont_ainv) : sheet1.getRow(cont_ainv));
                /*recorre toda la fila del rs2 para guardar en el excel*/
                for (int i = 1; i < 3; i++) {
                    if (i < 2) {
                        Util.createExcelCellRep(i, rw, rs2, rs2Metadata.getColumnName(i + 1), rs2Metadata.getColumnType(i + 1), estiloTabla);
                    } else
                        Util.createExcelCellRep(i, rw, rs2, rs2Metadata.getColumnName(i + 1), rs2Metadata.getColumnType(i + 1), estiloMoneda);
                }
                /*suma del subtotal*/
                suma_ainv = suma_ainv + rs2.getDouble(3);
                /*se incrementa el renglon que se inserto en la seccion*/
                cont_ainv++;
                cont_saldoCalculado++;
                cont_diferencia++;
            }
        }
        Row rwEnc12 = (sheet1.getRow(s_cont_aconta) == null ? sheet1.createRow(s_cont_aconta) : sheet1.getRow(s_cont_aconta));
        Cell cell12 = (rwEnc12.getCell(4) == null ? rwEnc12.createCell(4) : rwEnc12.getCell(4));
        cell12.setCellStyle(estiloMoneda2);
        cell12.setCellValue(suma_aconta);
        Row rwEnc13 = (sheet1.getRow(s_cont_sconta) == null ? sheet1.createRow(s_cont_sconta) : sheet1.getRow(s_cont_sconta));
        Cell cell13 = (rwEnc13.getCell(4) == null ? rwEnc13.createCell(4) : rwEnc13.getCell(4));
        cell13.setCellStyle(estiloMoneda2);
        cell13.setCellValue(suma_sconta);
        Row rwEnc14 = (sheet1.getRow(s_cont_sinv) == null ? sheet1.createRow(s_cont_sinv) : sheet1.getRow(s_cont_sinv));
        Cell cell14 = (rwEnc14.getCell(4) == null ? rwEnc14.createCell(4) : rwEnc14.getCell(4));
        cell14.setCellStyle(estiloMoneda2);
        cell14.setCellValue(suma_sinv);
        Row rwEnc15 = (sheet1.getRow(s_cont_ainv) == null ? sheet1.createRow(s_cont_ainv) : sheet1.getRow(s_cont_ainv));
        Cell cell15 = (rwEnc15.getCell(4) == null ? rwEnc15.createCell(4) : rwEnc15.getCell(4));
        cell15.setCellStyle(estiloMoneda2);
        cell15.setCellValue(suma_ainv);
        saldoCalculado = saldo - suma_aconta + suma_sconta - suma_sinv + suma_ainv;
        diferencia = saldoCalculado - saldoGRMO;
        Row rwEnc16 = (sheet1.getRow(cont_saldoCalculado) == null ? sheet1.createRow(cont_saldoCalculado) : sheet1.getRow(cont_saldoCalculado));
        Cell cell16 = (rwEnc16.getCell(4) == null ? rwEnc16.createCell(4) : rwEnc16.getCell(4));
        cell16.setCellStyle(estiloMoneda2);
        cell16.setCellValue(saldoCalculado);
        Row rwEnc17 = (sheet1.getRow(cont_diferencia) == null ? sheet1.createRow(cont_diferencia) : sheet1.getRow(cont_diferencia));
        Cell cell17 = (rwEnc17.getCell(4) == null ? rwEnc17.createCell(4) : rwEnc17.getCell(4));
        cell17.setCellStyle(estiloMoneda2);
        cell17.setCellValue(diferencia);
        Row rwEnc18 = (sheet0.getRow(2) == null ? sheet0.createRow(2) : sheet0.getRow(2));
        Cell cell18 = (rwEnc18.getCell(1) == null ? rwEnc18.createCell(1) : rwEnc18.getCell(1));
        cell18.setCellValue(unidad);
        Row rwEnc19 = (sheet1.getRow(2) == null ? sheet1.createRow(2) : sheet1.getRow(2));
        Cell cell19 = (rwEnc19.getCell(1) == null ? rwEnc19.createCell(1) : rwEnc19.getCell(1));
        cell19.setCellValue(unidad);
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

    private static String generaReporteBI(HttpServletRequest req, ResultSet rs, ResultSet rs2, String plantillaPath, String fecha, String tipo, double saldo, double saldoGRMO, String unidad) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "ConciliacionDeBienesInmuebles" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        ResultSetMetaData rsMetadata = rs.getMetaData();
        ResultSetMetaData rs2Metadata = rs2.getMetaData();
        Sheet sheet0 = workbook.getSheetAt(0);
        Sheet sheet1 = workbook.getSheetAt(1);
        int mesFin = 0;
        String anio = "";
        String periodo = "";
        String firmaGRF1 = req.getParameter("nombre1");
        String firmaGRF2 = req.getParameter("nombre2");
        String firmaGRF3 = req.getParameter("nombre3");
        String firmaGRMO1 = req.getParameter("nombre4");
        String firmaGRMO2 = req.getParameter("nombre5");
        String firmaGRMO3 = req.getParameter("nombre6");
        /*Suma de subtotales*/
        double suma_inv = 0.00;
        double suma_conta = 0.00;
        /*contados para las sumas de los subtotales*/
        int s_cont_conta = 18;
        int s_cont_inv = 22;
        /*contador para los renglones en cada seccion*/
        int cont_conta = 17;
        int cont_inv = 21;
        /*contador para los calculos del saldo y diferencia*/
        int cont_saldoTotal = 23;
        mesFin = Integer.parseInt(fecha.substring(3, 5));
        anio = fecha.substring(6, 10);
        periodo = Util.NOMBRE_MESES_MX[mesFin - 1] + " " + anio;
        CellStyle estiloTabla = workbook.createCellStyle();
        estiloTabla.setBorderRight(BorderStyle.HAIR);
        estiloTabla.setBorderLeft(BorderStyle.HAIR);
        estiloTabla.setBorderTop(BorderStyle.HAIR);
        estiloTabla.setBorderBottom(BorderStyle.HAIR);
        CellStyle estiloFirma = workbook.createCellStyle();
        estiloFirma.setBorderBottom(BorderStyle.THIN);
        DataFormat df = workbook.createDataFormat();
        CellStyle estiloMoneda = workbook.createCellStyle();
        estiloMoneda.setBorderRight(BorderStyle.HAIR);
        estiloMoneda.setBorderLeft(BorderStyle.HAIR);
        estiloMoneda.setBorderTop(BorderStyle.HAIR);
        estiloMoneda.setBorderBottom(BorderStyle.HAIR);
        estiloMoneda.setDataFormat(df.getFormat("#,###,##0.00"));
        DataFormat df2 = workbook.createDataFormat();
        CellStyle estiloMoneda2 = workbook.createCellStyle();
        estiloMoneda2.setDataFormat(df2.getFormat("#,###,##0.00"));
        Row rwEnc2 = (sheet0.getRow(8) == null ? sheet0.createRow(8) : sheet0.getRow(8));
        Cell cell2 = (rwEnc2.getCell(1) == null ? rwEnc2.createCell(1) : rwEnc2.getCell(1));
        cell2.setCellValue("PERIODO: " + periodo);
        Row rwEnc = (sheet1.getRow(8) == null ? sheet1.createRow(8) : sheet1.getRow(8));
        Cell cell = (rwEnc.getCell(1) == null ? rwEnc.createCell(1) : rwEnc.getCell(1));
        cell.setCellValue("PERIODO: " + periodo);
        Row rwEnc3 = (sheet0.getRow(11) == null ? sheet0.createRow(11) : sheet0.getRow(11));
        Cell cell3 = (rwEnc3.getCell(6) == null ? rwEnc3.createCell(6) : rwEnc3.getCell(6));
        cell3.setCellStyle(estiloMoneda2);
        cell3.setCellValue(saldo);
        Row rwEnc1 = (sheet1.getRow(11) == null ? sheet1.createRow(11) : sheet1.getRow(11));
        Cell cell1 = (rwEnc1.getCell(4) == null ? rwEnc1.createCell(4) : rwEnc1.getCell(4));
        cell1.setCellValue(saldo);
        Row rwEnc8 = (sheet0.getRow(10) == null ? sheet0.createRow(10) : sheet0.getRow(10));
        Cell cell8 = (rwEnc8.getCell(6) == null ? rwEnc8.createCell(6) : rwEnc8.getCell(6));
        cell8.setCellStyle(estiloMoneda2);
        cell8.setCellValue(saldoGRMO);
        Row rwEnc11 = (sheet1.getRow(10) == null ? sheet1.createRow(10) : sheet1.getRow(10));
        Cell cell11 = (rwEnc11.getCell(4) == null ? rwEnc11.createCell(4) : rwEnc11.getCell(4));
        cell11.setCellStyle(estiloMoneda2);
        cell11.setCellValue(saldoGRMO);
        if (!"".equals(firmaGRF1)) {
            Row rwEnc18 = (sheet0.getRow(30) == null ? sheet0.createRow(30) : sheet0.getRow(30));
            Cell cell18 = (rwEnc18.getCell(1) == null ? rwEnc18.createCell(1) : rwEnc18.getCell(1));
            cell18.setCellValue(firmaGRF1);
            Row rwEnc19 = (sheet0.getRow(30) == null ? sheet0.createRow(30) : sheet0.getRow(30));
            Cell cell19 = (rwEnc19.getCell(3) == null ? rwEnc19.createCell(3) : rwEnc19.getCell(3));
            cell19.setCellStyle(estiloFirma);
            Row rwEnc30 = (sheet1.getRow(30) == null ? sheet1.createRow(30) : sheet1.getRow(30));
            Cell cell30 = (rwEnc30.getCell(1) == null ? rwEnc30.createCell(1) : rwEnc30.getCell(1));
            cell30.setCellValue(firmaGRF1);
            Row rwEnc31 = (sheet1.getRow(30) == null ? sheet1.createRow(30) : sheet1.getRow(30));
            Cell cell31 = (rwEnc31.getCell(2) == null ? rwEnc31.createCell(2) : rwEnc31.getCell(2));
            cell31.setCellStyle(estiloFirma);
        }
        if (!"".equals(firmaGRF2)) {
            Row rwEnc20 = (sheet0.getRow(32) == null ? sheet0.createRow(32) : sheet0.getRow(32));
            Cell cell20 = (rwEnc20.getCell(1) == null ? rwEnc20.createCell(1) : rwEnc20.getCell(1));
            cell20.setCellValue(firmaGRF2);
            Row rwEnc21 = (sheet0.getRow(32) == null ? sheet0.createRow(32) : sheet0.getRow(32));
            Cell cell21 = (rwEnc21.getCell(3) == null ? rwEnc21.createCell(3) : rwEnc21.getCell(3));
            cell21.setCellStyle(estiloFirma);
            Row rwEnc32 = (sheet1.getRow(32) == null ? sheet1.createRow(32) : sheet1.getRow(32));
            Cell cell32 = (rwEnc32.getCell(1) == null ? rwEnc32.createCell(1) : rwEnc32.getCell(1));
            cell32.setCellValue(firmaGRF2);
            Row rwEnc33 = (sheet1.getRow(32) == null ? sheet1.createRow(32) : sheet1.getRow(32));
            Cell cell33 = (rwEnc33.getCell(2) == null ? rwEnc33.createCell(2) : rwEnc33.getCell(2));
            cell33.setCellStyle(estiloFirma);
        }
        if (!"".equals(firmaGRF3)) {
            Row rwEnc22 = (sheet0.getRow(34) == null ? sheet0.createRow(34) : sheet0.getRow(34));
            Cell cell22 = (rwEnc22.getCell(1) == null ? rwEnc22.createCell(1) : rwEnc22.getCell(1));
            cell22.setCellValue(firmaGRF3);
            Row rwEnc23 = (sheet0.getRow(34) == null ? sheet0.createRow(34) : sheet0.getRow(34));
            Cell cell23 = (rwEnc23.getCell(3) == null ? rwEnc23.createCell(3) : rwEnc23.getCell(3));
            cell23.setCellStyle(estiloFirma);
            Row rwEnc34 = (sheet1.getRow(34) == null ? sheet1.createRow(34) : sheet1.getRow(34));
            Cell cell34 = (rwEnc34.getCell(1) == null ? rwEnc34.createCell(1) : rwEnc34.getCell(1));
            cell34.setCellValue(firmaGRF3);
            Row rwEnc35 = (sheet1.getRow(34) == null ? sheet1.createRow(34) : sheet1.getRow(34));
            Cell cell35 = (rwEnc35.getCell(2) == null ? rwEnc35.createCell(2) : rwEnc35.getCell(2));
            cell35.setCellStyle(estiloFirma);
        }
        if (!"".equals(firmaGRMO1)) {
            Row rwEnc24 = (sheet0.getRow(38) == null ? sheet0.createRow(38) : sheet0.getRow(38));
            Cell cell24 = (rwEnc24.getCell(1) == null ? rwEnc24.createCell(1) : rwEnc24.getCell(1));
            cell24.setCellValue(firmaGRMO1);
            Row rwEnc25 = (sheet0.getRow(38) == null ? sheet0.createRow(38) : sheet0.getRow(38));
            Cell cell25 = (rwEnc25.getCell(3) == null ? rwEnc25.createCell(3) : rwEnc25.getCell(3));
            cell25.setCellStyle(estiloFirma);
            Row rwEnc36 = (sheet1.getRow(38) == null ? sheet1.createRow(38) : sheet1.getRow(38));
            Cell cell36 = (rwEnc36.getCell(1) == null ? rwEnc36.createCell(1) : rwEnc36.getCell(1));
            cell36.setCellValue(firmaGRMO1);
            Row rwEnc37 = (sheet1.getRow(38) == null ? sheet1.createRow(38) : sheet1.getRow(38));
            Cell cell37 = (rwEnc37.getCell(2) == null ? rwEnc37.createCell(2) : rwEnc37.getCell(2));
            cell37.setCellStyle(estiloFirma);
        }
        if (!"".equals(firmaGRMO2)) {
            Row rwEnc26 = (sheet0.getRow(40) == null ? sheet0.createRow(40) : sheet0.getRow(40));
            Cell cell26 = (rwEnc26.getCell(1) == null ? rwEnc26.createCell(1) : rwEnc26.getCell(1));
            cell26.setCellValue(firmaGRMO2);
            Row rwEnc27 = (sheet0.getRow(40) == null ? sheet0.createRow(40) : sheet0.getRow(40));
            Cell cell27 = (rwEnc27.getCell(3) == null ? rwEnc27.createCell(3) : rwEnc27.getCell(3));
            cell27.setCellStyle(estiloFirma);
            Row rwEnc38 = (sheet1.getRow(40) == null ? sheet1.createRow(40) : sheet1.getRow(40));
            Cell cell38 = (rwEnc38.getCell(1) == null ? rwEnc38.createCell(1) : rwEnc38.getCell(1));
            cell38.setCellValue(firmaGRMO2);
            Row rwEnc39 = (sheet1.getRow(40) == null ? sheet1.createRow(40) : sheet1.getRow(40));
            Cell cell39 = (rwEnc39.getCell(2) == null ? rwEnc39.createCell(2) : rwEnc39.getCell(2));
            cell39.setCellStyle(estiloFirma);
        }
        if (!"".equals(firmaGRMO3)) {
            Row rwEnc28 = (sheet0.getRow(42) == null ? sheet0.createRow(42) : sheet0.getRow(42));
            Cell cell28 = (rwEnc28.getCell(1) == null ? rwEnc28.createCell(1) : rwEnc28.getCell(1));
            cell28.setCellValue(firmaGRMO3);
            Row rwEnc29 = (sheet0.getRow(42) == null ? sheet0.createRow(42) : sheet0.getRow(42));
            Cell cell29 = (rwEnc29.getCell(3) == null ? rwEnc29.createCell(3) : rwEnc29.getCell(3));
            cell29.setCellStyle(estiloFirma);
            Row rwEnc40 = (sheet1.getRow(42) == null ? sheet1.createRow(42) : sheet1.getRow(42));
            Cell cell40 = (rwEnc40.getCell(1) == null ? rwEnc40.createCell(1) : rwEnc40.getCell(1));
            cell40.setCellValue(firmaGRMO3);
            Row rwEnc41 = (sheet1.getRow(42) == null ? sheet1.createRow(42) : sheet1.getRow(42));
            Cell cell41 = (rwEnc41.getCell(2) == null ? rwEnc41.createCell(2) : rwEnc41.getCell(2));
            cell41.setCellStyle(estiloFirma);
        }
        while (rs.next()) {
            String seccion = rs.getString(1);
            if ("mensual_inventario".equals(seccion) || "alta_inventario".equals(seccion)) {
                int rows = cont_inv;
                sheet0.shiftRows(rows, cont_inv + 30, 1);
                Row rw = (sheet0.getRow(cont_inv) == null ? sheet0.createRow(cont_inv) : sheet0.getRow(cont_inv));
                /*recorre toda la fila del rs para guardar en el excel*/
                for (int i = 1; i < 5; i++) {
                    if (i < 4) {
                        Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
                    } else
                        Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloMoneda);
                }
                /*suma del subtotal*/
                suma_inv = suma_inv + rs.getDouble(5);
                /*se incrementa el renglon que se inserto en la seccion*/
                cont_inv++;
                s_cont_inv++;
                cont_saldoTotal++;
            } else if ("mensual_contabilidad".equals(seccion) || "alta_contabilidad".equals(seccion)) {
                int rows = cont_conta;
                sheet0.shiftRows(rows, cont_inv + 30, 1);
                Row rw = (sheet0.getRow(cont_conta) == null ? sheet0.createRow(cont_conta) : sheet0.getRow(cont_conta));
                /*recorre toda la fila del rs para guardar en el excel*/
                for (int i = 1; i < 5; i++) {
                    if (i < 4) {
                        Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
                    } else
                        Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloMoneda);
                }
                /*suma del subtotal*/
                suma_conta = suma_conta + rs.getDouble(5);
                /*se incrementa el renglon que se inserto en la seccion*/
                cont_conta++;
                cont_inv++;
                s_cont_conta++;
                s_cont_inv++;
                cont_saldoTotal++;
            }
        }
        Row rwEnc4 = (sheet0.getRow(s_cont_conta) == null ? sheet0.createRow(s_cont_conta) : sheet0.getRow(s_cont_conta));
        Cell cell4 = (rwEnc4.getCell(4) == null ? rwEnc4.createCell(4) : rwEnc4.getCell(4));
        cell4.setCellStyle(estiloMoneda);
        cell4.setCellValue(suma_conta);
        Row rwEnc6 = (sheet0.getRow(s_cont_inv) == null ? sheet0.createRow(s_cont_inv) : sheet0.getRow(s_cont_inv));
        Cell cell6 = (rwEnc6.getCell(4) == null ? rwEnc6.createCell(4) : rwEnc6.getCell(4));
        cell6.setCellStyle(estiloMoneda);
        cell6.setCellValue(suma_inv);
        double saldoCalculado = suma_conta - suma_inv;
        double diferencia = saldoGRMO - saldo;
        Row rwEnc9 = (sheet0.getRow(cont_saldoTotal) == null ? sheet0.createRow(cont_saldoTotal) : sheet0.getRow(cont_saldoTotal));
        Cell cell9 = (rwEnc9.getCell(4) == null ? rwEnc9.createCell(4) : rwEnc9.getCell(4));
        cell9.setCellStyle(estiloMoneda);
        cell9.setCellValue(saldoCalculado);
        Row rwEnc10 = (sheet0.getRow(12) == null ? sheet0.createRow(12) : sheet0.getRow(12));
        Cell cell10 = (rwEnc10.getCell(6) == null ? rwEnc10.createCell(6) : rwEnc10.getCell(6));
        cell10.setCellStyle(estiloMoneda2);
        cell10.setCellValue(diferencia);
        /*Suma de subtotales*/
        suma_inv = 0.00;
        suma_conta = 0.00;
        /*contados para las sumas de los subtotales*/
        s_cont_conta = 18;
        s_cont_inv = 22;
        /*contador para los renglones en cada seccion*/
        cont_conta = 17;
        cont_inv = 21;
        /*contador para los calculos del saldo y diferencia*/
        cont_saldoTotal = 23;
        while (rs2.next()) {
            String seccion = rs2.getString(1);
            if ("A_contabilidad".equals(seccion) || "C_contabilidad".equals(seccion)) {
                int rows = cont_conta;
                sheet1.shiftRows(rows, cont_inv + 30, 1);
                Row rw = (sheet1.getRow(cont_conta) == null ? sheet1.createRow(cont_conta) : sheet1.getRow(cont_conta));
                /*recorre toda la fila del rs2 para guardar en el excel*/
                for (int i = 1; i < 3; i++) {
                    if (i <= 2) {
                        Util.createExcelCellRep(i, rw, rs2, rs2Metadata.getColumnName(i + 1), rs2Metadata.getColumnType(i + 1), estiloTabla);
                    } else
                        Util.createExcelCellRep(i, rw, rs2, rs2Metadata.getColumnName(i + 1), rs2Metadata.getColumnType(i + 1), estiloMoneda);
                }
                /*suma del subtotal*/
                suma_conta = suma_conta + rs2.getDouble(3);
                /*se incrementa el renglon que se inserto en la seccion*/
                cont_conta++;
                cont_inv++;
                s_cont_conta++;
                s_cont_inv++;
                cont_saldoTotal++;
            } else if ("A_inventario".equals(seccion)) {
                int rows = cont_conta;
                sheet1.shiftRows(rows, cont_inv + 30, 1);
                Row rw = (sheet1.getRow(cont_conta) == null ? sheet1.createRow(cont_conta) : sheet1.getRow(cont_conta));
                /*recorre toda la fila del rs2 para guardar en el excel*/
                for (int i = 1; i < 3; i++) {
                    if (i < 2) {
                        Util.createExcelCellRep(i, rw, rs2, rs2Metadata.getColumnName(i + 1), rs2Metadata.getColumnType(i + 1), estiloTabla);
                    } else
                        Util.createExcelCellRep(i, rw, rs2, rs2Metadata.getColumnName(i + 1), rs2Metadata.getColumnType(i + 1), estiloMoneda);
                }
                /*suma del subtotal*/
                suma_conta = suma_conta + rs2.getDouble(3);
                /*se incrementa el renglon que se inserto en la seccion*/
                cont_conta++;
                cont_inv++;
                s_cont_inv++;
                cont_saldoTotal++;
            }
        }
        Row rwEnc12 = (sheet1.getRow(s_cont_conta) == null ? sheet1.createRow(s_cont_conta) : sheet1.getRow(s_cont_conta));
        Cell cell12 = (rwEnc12.getCell(2) == null ? rwEnc12.createCell(2) : rwEnc12.getCell(2));
        cell12.setCellStyle(estiloMoneda);
        cell12.setCellValue(suma_conta);
        Row rwEnc14 = (sheet1.getRow(s_cont_inv) == null ? sheet1.createRow(s_cont_inv) : sheet1.getRow(s_cont_inv));
        Cell cell14 = (rwEnc14.getCell(2) == null ? rwEnc14.createCell(2) : rwEnc14.getCell(2));
        cell14.setCellStyle(estiloMoneda);
        cell14.setCellValue(suma_inv);
        saldoCalculado = suma_conta - suma_inv;
        diferencia = saldoGRMO - saldo;
        Row rwEnc16 = (sheet1.getRow(cont_saldoTotal) == null ? sheet1.createRow(cont_saldoTotal) : sheet1.getRow(cont_saldoTotal));
        Cell cell16 = (rwEnc16.getCell(2) == null ? rwEnc16.createCell(2) : rwEnc16.getCell(2));
        cell16.setCellStyle(estiloMoneda);
        cell16.setCellValue(saldoCalculado);
        Row rwEnc17 = (sheet1.getRow(12) == null ? sheet1.createRow(12) : sheet1.getRow(12));
        Cell cell17 = (rwEnc17.getCell(4) == null ? rwEnc17.createCell(4) : rwEnc17.getCell(4));
        cell17.setCellStyle(estiloMoneda2);
        cell17.setCellValue(diferencia);
        Row rwEnc18 = (sheet0.getRow(2) == null ? sheet0.createRow(2) : sheet0.getRow(2));
        Cell cell18 = (rwEnc18.getCell(1) == null ? rwEnc18.createCell(1) : rwEnc18.getCell(1));
        cell18.setCellValue(unidad);
        Row rwEnc19 = (sheet1.getRow(2) == null ? sheet1.createRow(2) : sheet1.getRow(2));
        Cell cell19 = (rwEnc19.getCell(1) == null ? rwEnc19.createCell(1) : rwEnc19.getCell(1));
        cell19.setCellValue(unidad);
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
