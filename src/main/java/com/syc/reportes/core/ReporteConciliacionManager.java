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
import java.util.Calendar;
import java.util.Map;
//import javax.mail.Session;
//import jakarta.servlet.http.HttpSession;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import com.syc.gestion.reportes.ConciliacionFirma;
//import com.syc.gestion.core.Usuario;
//import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import java.util.Base64;
import java.nio.file.Paths;

public class ReporteConciliacionManager {

    public static String generaReporteConciliacionManager(Connection conn, Integer idConciliacion, Integer nMes, String cBan, Map<String, String> plantillas) throws Exception {
        PreparedStatement cs = null;
        ResultSet rs = null;
        String query = "SELECT * FROM fn_l_noConciliados( " + idConciliacion + ", " + nMes + ", '" + cBan + "') ORDER BY cOrigen, cTipo, ABS(mMonto)";
        System.out.print(query);
        String fileName = "";
        try {
            cs = conn.prepareStatement(query);
            rs = cs.executeQuery();
            fileName = generaReporteConciliacion(conn, rs, idConciliacion, nMes, cBan, plantillas.get("REPCONCILIACION"));
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(cs, false);
        }
    }

    private static String generaReporteConciliacion(Connection conn, ResultSet rs, Integer idConciliacion, Integer nMes, String cBan, String plantillaPath) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "ReporteConciliacion" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        int cnt_conta_a = 13;
        int cnt_conta_c = 19;
        int cnt_banco_a = 25;
        int cnt_banco_c = 31;
        int suma_conta_a = 10;
        int suma_conta_c = 16;
        int suma_banco_a = 22;
        int suma_banco_c = 28;
        double suma_ca = 0;
        double suma_cc = 0;
        double suma_ba = 0;
        double suma_bc = 0;
        double saldo_banco = 0;
        double saldo_ch = 0;
        double saldo_libros = 0;
        ResultSetMetaData rsMetadata = rs.getMetaData();
        String mes = nMes.toString();
        String cuenta = cBan;
        String DATE_FORMAT = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        // today
        Calendar c1 = Calendar.getInstance();
        String periodo = sdf.format(c1.getTime());
        Row rwEnc1 = (sheet0.getRow(5) == null ? sheet0.createRow(5) : sheet0.getRow(5));
        Cell cell1 = (rwEnc1.getCell(2) == null ? rwEnc1.createCell(2) : rwEnc1.getCell(2));
        cell1.setCellValue(cuenta);
        Row rwEnc2 = (sheet0.getRow(6) == null ? sheet0.createRow(6) : sheet0.getRow(6));
        Cell cell2 = (rwEnc2.getCell(2) == null ? rwEnc2.createCell(2) : rwEnc2.getCell(2));
        cell2.setCellValue(mes);
        Row rwEnc3 = (sheet0.getRow(7) == null ? sheet0.createRow(7) : sheet0.getRow(7));
        Cell cell3 = (rwEnc3.getCell(2) == null ? rwEnc3.createCell(2) : rwEnc3.getCell(2));
        cell3.setCellValue(periodo);
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
        while (rs.next()) {
            if (rs.getString("cOrigen").equals("Contabilidad")) {
                if (rs.getString("cTipo").equals("A")) {
                    Row rw = (sheet0.getRow(cnt_conta_a) == null ? sheet0.createRow(cnt_conta_a) : sheet0.getRow(cnt_conta_a));
                    Util.createExcelCellRep(1, rw, rs, rsMetadata.getColumnName(5), rsMetadata.getColumnType(5), estiloTabla);
                    Util.createExcelCellRep(2, rw, rs, rsMetadata.getColumnName(6), rsMetadata.getColumnType(6), estiloTabla);
                    Util.createExcelCellRep(3, rw, rs, rsMetadata.getColumnName(7), rsMetadata.getColumnType(7), estiloTabla);
                    Util.createExcelCellRep(4, rw, rs, rsMetadata.getColumnName(8), rsMetadata.getColumnType(8), estiloTabla);
                    Util.createExcelCellRep(5, rw, rs, rsMetadata.getColumnName(10), rsMetadata.getColumnType(10), estiloMoneda);
                    sheet0.shiftRows(cnt_conta_a + 1, cnt_banco_c + 13, 1, true, true);
                    suma_ca = suma_ca + rs.getDouble("mMonto");
                    cnt_conta_a++;
                    cnt_conta_c++;
                    cnt_banco_a++;
                    cnt_banco_c++;
                    suma_conta_c++;
                    suma_banco_a++;
                    suma_banco_c++;
                }
                if (rs.getString("cTipo").equals("C")) {
                    Row rw = (sheet0.getRow(cnt_conta_c) == null ? sheet0.createRow(cnt_conta_c) : sheet0.getRow(cnt_conta_c));
                    Util.createExcelCellRep(1, rw, rs, rsMetadata.getColumnName(5), rsMetadata.getColumnType(5), estiloTabla);
                    Util.createExcelCellRep(2, rw, rs, rsMetadata.getColumnName(6), rsMetadata.getColumnType(6), estiloTabla);
                    Util.createExcelCellRep(3, rw, rs, rsMetadata.getColumnName(7), rsMetadata.getColumnType(7), estiloTabla);
                    Util.createExcelCellRep(4, rw, rs, rsMetadata.getColumnName(8), rsMetadata.getColumnType(8), estiloTabla);
                    Util.createExcelCellRep(5, rw, rs, rsMetadata.getColumnName(10), rsMetadata.getColumnType(10), estiloMoneda);
                    sheet0.shiftRows(cnt_conta_c + 1, cnt_banco_c + 13, 1, true, true);
                    suma_cc = suma_cc + rs.getDouble("mMonto");
                    cnt_conta_c++;
                    cnt_banco_a++;
                    cnt_banco_c++;
                    suma_banco_a++;
                    suma_banco_c++;
                }
            } else {
                if (rs.getString("cTipo").equals("C")) {
                    Row rw = (sheet0.getRow(cnt_banco_a) == null ? sheet0.createRow(cnt_banco_a) : sheet0.getRow(cnt_banco_a));
                    Util.createExcelCellRep(1, rw, rs, rsMetadata.getColumnName(5), rsMetadata.getColumnType(5), estiloTabla);
                    Util.createExcelCellRep(2, rw, rs, rsMetadata.getColumnName(6), rsMetadata.getColumnType(6), estiloTabla);
                    Util.createExcelCellRep(3, rw, rs, rsMetadata.getColumnName(7), rsMetadata.getColumnType(7), estiloTabla);
                    Util.createExcelCellRep(4, rw, rs, rsMetadata.getColumnName(8), rsMetadata.getColumnType(8), estiloTabla);
                    Util.createExcelCellRep(5, rw, rs, rsMetadata.getColumnName(10), rsMetadata.getColumnType(10), estiloMoneda);
                    sheet0.shiftRows(cnt_banco_a + 1, cnt_banco_c + 13, 1, true, true);
                    suma_ba = suma_ba + rs.getDouble("mMonto");
                    cnt_banco_a++;
                    cnt_banco_c++;
                    suma_banco_c++;
                }
                if (rs.getString("cTipo").equals("A")) {
                    Row rw = (sheet0.getRow(cnt_banco_c) == null ? sheet0.createRow(cnt_banco_c) : sheet0.getRow(cnt_banco_c));
                    Util.createExcelCellRep(1, rw, rs, rsMetadata.getColumnName(5), rsMetadata.getColumnType(5), estiloTabla);
                    Util.createExcelCellRep(2, rw, rs, rsMetadata.getColumnName(6), rsMetadata.getColumnType(6), estiloTabla);
                    Util.createExcelCellRep(3, rw, rs, rsMetadata.getColumnName(7), rsMetadata.getColumnType(7), estiloTabla);
                    Util.createExcelCellRep(4, rw, rs, rsMetadata.getColumnName(8), rsMetadata.getColumnType(8), estiloTabla);
                    Util.createExcelCellRep(5, rw, rs, rsMetadata.getColumnName(10), rsMetadata.getColumnType(10), estiloMoneda);
                    sheet0.shiftRows(cnt_banco_c + 1, cnt_banco_c + 13, 1, true, true);
                    suma_bc = suma_bc + rs.getDouble("mMonto");
                    cnt_banco_c++;
                }
            }
        }
        Row rwCA = (sheet0.getRow(suma_conta_a) == null ? sheet0.createRow(suma_conta_a) : sheet0.getRow(suma_conta_a));
        Cell cellCA = (rwCA.getCell(7) == null ? rwCA.createCell(7) : rwCA.getCell(7));
        cellCA.setCellValue(suma_ca);
        Row rwCC = (sheet0.getRow(suma_conta_c) == null ? sheet0.createRow(suma_conta_c) : sheet0.getRow(suma_conta_c));
        Cell cellCC = (rwCC.getCell(7) == null ? rwCC.createCell(7) : rwCC.getCell(7));
        cellCC.setCellValue(suma_cc);
        Row rwBA = (sheet0.getRow(suma_banco_a) == null ? sheet0.createRow(suma_banco_a) : sheet0.getRow(suma_banco_a));
        Cell cellBA = (rwBA.getCell(7) == null ? rwBA.createCell(7) : rwBA.getCell(7));
        cellBA.setCellValue(suma_ba);
        Row rwBC = (sheet0.getRow(suma_banco_c) == null ? sheet0.createRow(suma_banco_c) : sheet0.getRow(suma_banco_c));
        Cell cellBC = (rwBC.getCell(7) == null ? rwBC.createCell(7) : rwBC.getCell(7));
        cellBC.setCellValue(suma_bc);
        String querySaldos = "SELECT ISNULL((SELECT TOP (1) mSaldo FROM dbo.tEdoCtaDetalle (NOLOCK) WHERE nEdoCta = (SELECT nEdoCta FROM dbo.tConciliacion (NOLOCK) WHERE nConciliacion = " + idConciliacion + ") ORDER BY n0Movimiento DESC),(SELECT mSaldoFinal FROM dbo.tConciliacion (NOLOCK) WHERE nConciliacion = " + idConciliacion + " )) AS BANCO, ISNULL((SELECT mFinal FROM dbo.tAuxiliarEncabezado (NOLOCK) WHERE nAuxiliar = (SELECT nAuxiliar FROM dbo.tConciliacion (NOLOCK) WHERE nConciliacion = " + idConciliacion + ")),(SELECT ISNULL(mSaldoLibros,0) FROM dbo.tConciliacion (NOLOCK) WHERE nConciliacion = " + idConciliacion + " )) AS LIBRO";
        System.out.print(querySaldos);
        PreparedStatement cSaldo = null;
        ResultSet rSaldo = null;
        try {
            cSaldo = conn.prepareStatement(querySaldos);
            rSaldo = cSaldo.executeQuery();
            while (rSaldo.next()) {
                Row rwSaldoBanco = (sheet0.getRow(8) == null ? sheet0.createRow(8) : sheet0.getRow(8));
                Cell cellBanco = (rwSaldoBanco.getCell(7) == null ? rwSaldoBanco.createCell(7) : rwSaldoBanco.getCell(7));
                cellBanco.setCellValue(Double.parseDouble(rSaldo.getString("BANCO")));
                Row rwSaldoLibro = (sheet0.getRow(cnt_banco_c + 4) == null ? sheet0.createRow(cnt_banco_c + 4) : sheet0.getRow(cnt_banco_c + 4));
                Cell cellLibro = (rwSaldoLibro.getCell(7) == null ? rwSaldoLibro.createCell(7) : rwSaldoLibro.getCell(7));
                cellLibro.setCellValue(Double.parseDouble(rSaldo.getString("LIBRO")));
                saldo_libros = Double.parseDouble(rSaldo.getString("LIBRO"));
                saldo_banco = Double.parseDouble(rSaldo.getString("BANCO"));
                saldo_ch = saldo_banco - suma_ca + suma_cc - suma_ba + suma_bc;
                Row rwSaldoCh = (sheet0.getRow(cnt_banco_c + 3) == null ? sheet0.createRow(cnt_banco_c + 3) : sheet0.getRow(cnt_banco_c + 3));
                Cell cellCh = (rwSaldoCh.getCell(7) == null ? rwSaldoCh.createCell(7) : rwSaldoCh.getCell(7));
                cellCh.setCellValue(saldo_ch);
                Row rwDIF = (sheet0.getRow(cnt_banco_c + 5) == null ? sheet0.createRow(cnt_banco_c + 5) : sheet0.getRow(cnt_banco_c + 5));
                Cell cellDIF = (rwDIF.getCell(7) == null ? rwDIF.createCell(7) : rwDIF.getCell(7));
                cellDIF.setCellValue(saldo_libros - saldo_ch);
            }
        } finally {
            CloseObject.closeObject(rSaldo, false);
            CloseObject.closeObject(cSaldo, false);
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

    public static String generaReporteConEdoCtaManager(Connection conn, Integer idConciliacion, Integer nMes, String cBan, Map<String, String> plantillas) throws Exception {
        PreparedStatement cs = null;
        ResultSet rs = null;
        String query = "SELECT nCuenta,fOperacion,fFecha,cReferencia,cDescRef,cCodigo,cSucursal, mCargos, mAbonos, mSaldo,n0Movimiento,cDescripcion FROM dbo.tEdoCtaDetalle (NOLOCK) WHERE nEdoCta = (SELECT nEdoCta FROM dbo.tConciliacion (NOLOCK) WHERE nConciliacion = " + idConciliacion + ")";
        System.out.print(query);
        String fileName = "";
        try {
            cs = conn.prepareStatement(query);
            rs = cs.executeQuery();
            fileName = generaReporteConEdoCta(rs, idConciliacion, nMes, cBan, plantillas.get("REPCONEDOCTA"));
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(cs, false);
        }
    }

    public static void insertaConciliacionFirmaElectronica(Connection conn, ConciliacionFirma conciliacion) throws Exception {
        PreparedStatement pst = null;
        ResultSet rs = null;
        String query = "INSERT INTO tConciliacionFirmaElectronica (nConciliacion, cEstatus, cRutaArchivo, cRutaAcuse) VALUES ( ?, 'E', ?, ? )";
        try {
            pst = conn.prepareStatement(query);
            pst.setInt(1, conciliacion.getIdConciliacion());
            pst.setString(2, conciliacion.getRutaReporteImpreso());
            pst.setString(3, conciliacion.getRutaAcuseImpreso());
            pst.execute();
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(pst, false);
        }
    }

    private static String generaReporteConEdoCta(ResultSet rs, Integer idConciliacion, Integer nMes, String cBan, String plantillaPath) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "ReporteConciliacion" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
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
        String mes = nMes.toString();
        String cuenta = cBan;
        String DATE_FORMAT = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        // today
        Calendar c1 = Calendar.getInstance();
        String periodo = sdf.format(c1.getTime());
        Row rwEnc1 = (sheet0.getRow(5) == null ? sheet0.createRow(5) : sheet0.getRow(5));
        Cell cell1 = (rwEnc1.getCell(3) == null ? rwEnc1.createCell(3) : rwEnc1.getCell(3));
        cell1.setCellValue(cuenta);
        Row rwEnc2 = (sheet0.getRow(6) == null ? sheet0.createRow(6) : sheet0.getRow(6));
        Cell cell2 = (rwEnc2.getCell(3) == null ? rwEnc2.createCell(3) : rwEnc2.getCell(3));
        cell2.setCellValue(mes);
        Row rwEnc3 = (sheet0.getRow(7) == null ? sheet0.createRow(7) : sheet0.getRow(7));
        Cell cell3 = (rwEnc3.getCell(3) == null ? rwEnc3.createCell(3) : rwEnc3.getCell(3));
        cell3.setCellValue(periodo);
        CellStyle estiloTabla = workbook.createCellStyle();
        estiloTabla.setBorderRight(BorderStyle.HAIR);
        estiloTabla.setBorderLeft(BorderStyle.HAIR);
        estiloTabla.setBorderTop(BorderStyle.HAIR);
        estiloTabla.setBorderBottom(BorderStyle.HAIR);
        while (rs.next()) {
            Row rw = (sheet0.getRow(renglonInicio + cnt) == null ? sheet0.createRow(renglonInicio + cnt) : sheet0.getRow(renglonInicio + cnt));
            for (int i = 0; i < rsMetadata.getColumnCount(); i++) {
                Util.createExcelCellRep(i + 1, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
            }
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

    public static String generaReporteConAuxiliarManager(Connection conn, Integer idConciliacion, Integer nMes, String cBan, Map<String, String> plantillas) throws Exception {
        PreparedStatement cs = null;
        ResultSet rs = null;
        String query = "SELECT cTipo, nFolioPoliza, cCxP, fFecha, cCuenta, cSubCuenta, cCheque, cCocepto, mCargos, mAbonos FROM dbo.tAuxiliarDetalle (NOLOCK) WHERE nAuxiliar = (SELECT nAuxiliar FROM dbo.tConciliacion (NOLOCK) WHERE nConciliacion = " + idConciliacion + ")";
        System.out.print(query);
        String fileName = "";
        try {
            cs = conn.prepareStatement(query);
            rs = cs.executeQuery();
            fileName = generaReporteConAuxiliar(rs, idConciliacion, nMes, cBan, plantillas.get("REPCONAUXILIAR"));
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(cs, false);
        }
    }

    private static String generaReporteConAuxiliar(ResultSet rs, Integer idConciliacion, Integer nMes, String cBan, String plantillaPath) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "ReporteConciliacion" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
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
        String mes = nMes.toString();
        String cuenta = cBan;
        String DATE_FORMAT = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        // today
        Calendar c1 = Calendar.getInstance();
        String periodo = sdf.format(c1.getTime());
        Row rwEnc1 = (sheet0.getRow(5) == null ? sheet0.createRow(5) : sheet0.getRow(5));
        Cell cell1 = (rwEnc1.getCell(3) == null ? rwEnc1.createCell(3) : rwEnc1.getCell(3));
        cell1.setCellValue(cuenta);
        Row rwEnc2 = (sheet0.getRow(6) == null ? sheet0.createRow(6) : sheet0.getRow(6));
        Cell cell2 = (rwEnc2.getCell(3) == null ? rwEnc2.createCell(3) : rwEnc2.getCell(3));
        cell2.setCellValue(mes);
        Row rwEnc3 = (sheet0.getRow(7) == null ? sheet0.createRow(7) : sheet0.getRow(7));
        Cell cell3 = (rwEnc3.getCell(3) == null ? rwEnc3.createCell(3) : rwEnc3.getCell(3));
        cell3.setCellValue(periodo);
        CellStyle estiloTabla = workbook.createCellStyle();
        estiloTabla.setBorderRight(BorderStyle.HAIR);
        estiloTabla.setBorderLeft(BorderStyle.HAIR);
        estiloTabla.setBorderTop(BorderStyle.HAIR);
        estiloTabla.setBorderBottom(BorderStyle.HAIR);
        while (rs.next()) {
            Row rw = (sheet0.getRow(renglonInicio + cnt) == null ? sheet0.createRow(renglonInicio + cnt) : sheet0.getRow(renglonInicio + cnt));
            for (int i = 0; i < rsMetadata.getColumnCount(); i++) {
                Util.createExcelCellRep(i + 1, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
            }
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

    public static void actualizarEstatus(Connection conn, String estatus, String folio) throws Exception {
        PreparedStatement pst = null;
        String query = "UPDATE tConciliacion SET cEsFirmaElectronica = ? WHERE nConciliacion = ?";
        try {
            pst = conn.prepareStatement(query);
            pst.setString(1, estatus);
            pst.setString(2, folio);
            pst.executeUpdate();
        } finally {
            CloseObject.closeObject(pst, false);
        }
    }
}
