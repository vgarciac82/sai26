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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.syc.gestion.reportes.reportesBussinesObject;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.obrapublica.ConfiguraAplicativoBusinessLogic;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;
import java.nio.file.Paths;

public class ReportePptalManager {

    private static Logger log = LoggerFactory.getLogger(reportesBussinesObject.class);

    private static ConfiguraAplicativoBusinessLogic cabl = new ConfiguraAplicativoBusinessLogic(GestionInterface.ATT_CONEXION);

    private static String tipoFuente = cabl.getSystemSetting("FUENTE_INSTITUCIONAL");

    public static String reporteApartadoManager(Connection conn, Map<String, String> plantillas) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "select nfolioApartado, faplicacion, cunidadResponsable, cidsolicitud, ep, mimporte, cmes, ccentrocontable from v_SaldoApartado";
        String fileName = "";
        try {
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            log.debug("======== Fin ejecución vistas para obtener reporte==========");
            fileName = generaApartadoExcel(rs, plantillas.get("APARTADO"));
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
    }

    public static String reportePrecompManager(Connection conn, Map<String, String> plantillas) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "select faplicacion, cunidadresponsable, cIdContrato, caNoCompromiso, ep, cmes, importe from v_SaldoPrecompromisos";
        String fileName = "";
        try {
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            log.debug("======== Fin ejecución vistas para obtener reporte==========");
            fileName = generaPrecomprometidoExcel(rs, plantillas.get("PRECOMP"));
            log.debug("======== Fin ejecución formato en Excel==========");
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
    }

    public static String reporteCompManager(Connection conn, Map<String, String> plantillas) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder query = new StringBuilder();
        query.append("Select cidcontrato, ctipocontrato, ep, rfc, Nombre, mAnual, mCompromisoEnero, mCompromisoFebrero, mCompromisoMarzo, mCompromisoAbril ");
        query.append(" , mCompromisoMayo, mCompromisoJunio, mCompromisoJulio, mCompromisoAgosto, mCompromisoSeptiembre, mCompromisoOctubre");
        query.append(" , mCompromisoNoviembre, mCompromisoDiciembre from v_SaldoCompromisos");
        String fileName = "";
        try {
            String sql = query.toString();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            log.debug("======== Fin ejecución vistas para obtener reporte==========");
            fileName = generaComprometidoExcel(rs, plantillas.get("COMPROMETIDO"));
            log.debug("======== Fin ejecución formato en Excel==========");
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
    }

    public static String reporteDevManager(Connection conn, Map<String, String> plantillas) throws Exception {
        CallableStatement cs = null;
        ResultSet rs = null;
        String query = "{call sp_saldoDevengado(  )}";
        String fileName = "";
        try {
            cs = conn.prepareCall(query);
            rs = cs.executeQuery();
            log.debug("======== Fin ejecución Store sp_saldoDevengado==========");
            fileName = generaDevengadoExcel(rs, plantillas.get("DEVENGADO"));
            log.debug("======== Fin ejecución formato en Excel==========");
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(cs, false);
        }
    }

    public static String reporteAccManager(Connection conn, Map<String, String> plantillas) throws Exception {
        PreparedStatement cs = null, ps = null;
        ResultSet rs = null, rs2 = null;
        StringBuilder query = new StringBuilder();
        query.append("Select tipo, cTipoPago, cxp, rfc, nombre, descPoliza, faplicacion, ur,estatus,  ep, SUBSTRING(ep,1,4) anio, SUBSTRING(ep,6,2) ramo, SUBSTRING(ep,9,3) unidad, SUBSTRING(ep,13,1) gf, SUBSTRING(ep,15,1) func");
        query.append(" , SUBSTRING(ep,17,2) sf, SUBSTRING(ep,20,2) pg, SUBSTRING(ep,23,3) ai, SUBSTRING(ep,27,4) pp, SUBSTRING(ep,32,5) obgt, SUBSTRING(ep,38,1) tg, SUBSTRING(ep,40,1) ff, SUBSTRING(ep,42,2) ent, SUBSTRING(ep,45,11) cart, SUBSTRING(ep,57,3) un, SUBSTRING(ep,61,3) ue");
        query.append(" , ene,feb, Marzo, abr, may, jun,jul,ago,sep, oct, nov, Dic ");
        query.append("  from v_PresupuestoAcumulado (nolock) ");
        String fileName = "";
        try {
            //se ejecuta el Store del saldo devengado para que salga actualizado el devengado en la vista
            ps = conn.prepareStatement("exec sp_saldoDevengado ");
            rs2 = ps.executeQuery();
            //Se ejecuta la vista del presupuesto acumulado
            String sql = query.toString();
            cs = conn.prepareStatement(sql);
            rs = cs.executeQuery();
            log.debug("======== Fin ejecución Store y vistas para el reporte==========");
            fileName = generaAcumuladoExcel(rs, plantillas.get("ACUMULADO"));
            log.debug("======== Fin ejecución formato en Excel==========");
            return fileName;
        } finally {
            CloseObject.closeObject(ps, false);
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(rs2, false);
            CloseObject.closeObject(cs, false);
        }
    }

    private static String generaAcumuladoExcel(ResultSet rs, String plantillaPath) throws Exception {
        //Generación del reporte Acumulado en Excel
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "Acumulado" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xlsx";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        XSSFWorkbook workbook = new XSSFWorkbook(fsArchivo);
        fsArchivo.close();
        XSSFSheet sheet0 = workbook.getSheetAt(0);
        //Se genera la fecha en la fila 3
        Date objDate = new Date();
        String dateFormat = "dd-MMM-yyyy";
        SimpleDateFormat formatFecha = new SimpleDateFormat(dateFormat);
        XSSFRow rwEnc2 = (sheet0.getRow(3) == null ? (XSSFRow) sheet0.createRow(3) : (XSSFRow) sheet0.getRow(3));
        Cell cell2 = (rwEnc2.getCell(0) == null ? rwEnc2.createCell(0) : rwEnc2.getCell(0));
        cell2.setCellValue(formatFecha.format(objDate));
        ResultSetMetaData rsMetadata = rs.getMetaData();
        int renglonInicio = 5;
        int cnt1 = 0;
        Font fon = workbook.createFont();
        fon.setFontHeightInPoints((short) 8);
        fon.setFontName(tipoFuente);
        DataFormat df = workbook.createDataFormat();
        XSSFCellStyle estiloMoney = (XSSFCellStyle) Util.generaEstilo(workbook, 8, false, true, true, true, true);
        XSSFCellStyle estiloGeneral = (XSSFCellStyle) Util.generaEstilo(workbook, 8, false, true, true, true, false);
        XSSFCellStyle estiloFecha = (XSSFCellStyle) Util.generaEstilo(workbook, 8, false, true, true, true, false);
        estiloFecha.setDataFormat(df.getFormat("dd/mm/yyyy"));
        //Recorre el resultset para pintar los resultados en excel
        while (rs.next()) {
            XSSFRow rw = (sheet0.getRow(renglonInicio + cnt1) == null ? (XSSFRow) sheet0.createRow(renglonInicio + cnt1) : (XSSFRow) sheet0.getRow(renglonInicio + cnt1));
            for (int i = 0; i < 38; i++) {
                if (i <= 25) {
                    if (i == 6) {
                        Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloFecha);
                    } else {
                        Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloGeneral);
                    }
                } else if (i > 25) {
                    Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloMoney);
                }
            }
            cnt1++;
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

    private static String generaApartadoExcel(ResultSet rs, String plantillaPath) throws Exception {
        //Genera el archivo del apartado en Excel
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "Apartado" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xlsx";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        XSSFWorkbook workbook = new XSSFWorkbook(fsArchivo);
        fsArchivo.close();
        XSSFSheet sheet0 = workbook.getSheetAt(0);
        //Pone la fecha en el 3 renglon
        Date objDate = new Date();
        String dateFormat = "dd-MMM-yyyy";
        SimpleDateFormat formatFecha = new SimpleDateFormat(dateFormat);
        XSSFRow rwEnc2 = (sheet0.getRow(3) == null ? sheet0.createRow(3) : sheet0.getRow(3));
        XSSFCell cell2 = (rwEnc2.getCell(0) == null ? rwEnc2.createCell(0) : rwEnc2.getCell(0));
        cell2.setCellValue(formatFecha.format(objDate));
        ResultSetMetaData rsMetadata = rs.getMetaData();
        int renglonInicio = 5;
        int cnt1 = 0;
        //Define el estilo de las celdas a usar
        Font fon = workbook.createFont();
        fon.setFontName(tipoFuente);
        DataFormat df = workbook.createDataFormat();
        XSSFCellStyle estiloGeneral = (XSSFCellStyle) Util.generaEstilo(workbook, 10, false, true, true, true, false);
        XSSFCellStyle estiloMoney = (XSSFCellStyle) Util.generaEstilo(workbook, 10, false, true, true, true, true);
        XSSFCellStyle estiloFecha = (XSSFCellStyle) Util.generaEstilo(workbook, 10, false, true, true, true, false);
        estiloFecha.setDataFormat(df.getFormat("dd/mm/yyyy"));
        estiloMoney.setFont(fon);
        estiloGeneral.setFont(fon);
        //Recorre el resultset para pintar los resultados en excel
        while (rs.next()) {
            Row rw = (sheet0.getRow(renglonInicio + cnt1) == null ? sheet0.createRow(renglonInicio + cnt1) : sheet0.getRow(renglonInicio + cnt1));
            Util.createExcelCellRep(0, rw, rs, rsMetadata.getColumnName(1), rsMetadata.getColumnType(1), estiloGeneral);
            Util.createExcelCellRep(1, rw, rs, rsMetadata.getColumnName(2), rsMetadata.getColumnType(2), estiloFecha);
            Util.createExcelCellRep(2, rw, rs, rsMetadata.getColumnName(3), rsMetadata.getColumnType(3), estiloMoney);
            Util.createExcelCellRep(3, rw, rs, rsMetadata.getColumnName(4), rsMetadata.getColumnType(4), estiloMoney);
            Util.createExcelCellRep(4, rw, rs, rsMetadata.getColumnName(5), rsMetadata.getColumnType(5), estiloMoney);
            Util.createExcelCellRep(5, rw, rs, rsMetadata.getColumnName(6), rsMetadata.getColumnType(6), estiloMoney);
            Util.createExcelCellRep(6, rw, rs, rsMetadata.getColumnName(7), rsMetadata.getColumnType(7), estiloGeneral);
            Util.createExcelCellRep(7, rw, rs, rsMetadata.getColumnName(8), rsMetadata.getColumnType(8), estiloMoney);
            cnt1++;
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

    private static String generaPrecomprometidoExcel(ResultSet rs, String plantillaPath) throws Exception {
        //Genera el archivo del precompromiso en Excel
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "Precomprometido" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xlsx";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        XSSFWorkbook workbook = new XSSFWorkbook(fsArchivo);
        fsArchivo.close();
        XSSFSheet sheet0 = workbook.getSheetAt(0);
        //Pone la fecha en el renglon 3
        Date objDate = new Date();
        String dateFormat = "dd-MMM-yyyy";
        SimpleDateFormat formatFecha = new SimpleDateFormat(dateFormat);
        XSSFRow rwEnc2 = (sheet0.getRow(3) == null ? sheet0.createRow(3) : sheet0.getRow(3));
        XSSFCell cell2 = (rwEnc2.getCell(0) == null ? rwEnc2.createCell(0) : rwEnc2.getCell(0));
        cell2.setCellValue(formatFecha.format(objDate));
        ResultSetMetaData rsMetadata = rs.getMetaData();
        int renglonInicio = 5;
        int cnt1 = 0;
        //Define los estilos de celda a usar
        DataFormat df = workbook.createDataFormat();
        XSSFCellStyle estiloGeneral = (XSSFCellStyle) Util.generaEstilo(workbook, 9, false, true, true, true, false);
        XSSFCellStyle estiloMoney = (XSSFCellStyle) Util.generaEstilo(workbook, 9, false, true, true, true, true);
        XSSFCellStyle estiloFecha = (XSSFCellStyle) Util.generaEstilo(workbook, 9, false, true, true, true, false);
        estiloFecha.setDataFormat(df.getFormat("dd/mm/yyyy"));
        //Pinta el resultset en el excel
        while (rs.next()) {
            XSSFRow rw = (sheet0.getRow(renglonInicio + cnt1) == null ? sheet0.createRow(renglonInicio + cnt1) : sheet0.getRow(renglonInicio + cnt1));
            Util.createExcelCellRep(0, rw, rs, rsMetadata.getColumnName(1), rsMetadata.getColumnType(1), estiloFecha);
            for (int i = 1; i < 7; i++) {
                Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloGeneral);
            }
            Util.createExcelCellRep(6, rw, rs, rsMetadata.getColumnName(7), rsMetadata.getColumnType(7), estiloMoney);
            cnt1++;
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

    private static String generaComprometidoExcel(ResultSet rs, String plantillaPath) throws Exception {
        //Genera el archivo del compromiso en Excel
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "Comprometido" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xlsx";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        XSSFWorkbook workbook = new XSSFWorkbook(fsArchivo);
        fsArchivo.close();
        XSSFSheet sheet0 = workbook.getSheetAt(0);
        //Pone la fecha en el 3 renglon
        Date objDate = new Date();
        String dateFormat = "dd-MMM-yyyy";
        SimpleDateFormat formatFecha = new SimpleDateFormat(dateFormat);
        XSSFRow rwEnc2 = (sheet0.getRow(3) == null ? sheet0.createRow(3) : sheet0.getRow(3));
        XSSFCell cell2 = (rwEnc2.getCell(0) == null ? rwEnc2.createCell(0) : rwEnc2.getCell(0));
        cell2.setCellValue(formatFecha.format(objDate));
        ResultSetMetaData rsMetadata = rs.getMetaData();
        int renglonInicio = 5;
        int cnt1 = 0;
        //Define los estilos de las celdas a utilizar
        XSSFCellStyle estiloGeneral = (XSSFCellStyle) Util.generaEstilo(workbook, 9, false, true, true, true, false);
        XSSFCellStyle estiloMoney = (XSSFCellStyle) Util.generaEstilo(workbook, 9, false, true, true, true, true);
        //Pinta el resultSet en el Excel
        while (rs.next()) {
            XSSFRow rw = (sheet0.getRow(renglonInicio + cnt1) == null ? sheet0.createRow(renglonInicio + cnt1) : sheet0.getRow(renglonInicio + cnt1));
            for (int i = 0; i <= 4; i++) {
                Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloGeneral);
            }
            for (int i = 5; i <= 17; i++) {
                Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloMoney);
            }
            cnt1++;
        }
        //Actualiza las formulas del excel con los datos generados
        sheet0 = (XSSFSheet) Util.EvaluaFormula(workbook, sheet0, 10, 11, 1, 7);
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

    private static String generaDevengadoExcel(ResultSet rs, String plantillaPath) throws Exception {
        //Genera el archivo del devengado en Excel
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "Devengado" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xlsx";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        XSSFWorkbook workbook = new XSSFWorkbook(fsArchivo);
        fsArchivo.close();
        //Pone la fecha en el renglon 3
        XSSFSheet sheet0 = workbook.getSheetAt(0);
        Date objDate = new Date();
        String dateFormat = "dd-MMM-yyyy";
        SimpleDateFormat formatFecha = new SimpleDateFormat(dateFormat);
        XSSFRow rwEnc2 = (sheet0.getRow(3) == null ? sheet0.createRow(3) : sheet0.getRow(3));
        XSSFCell cell2 = (rwEnc2.getCell(0) == null ? rwEnc2.createCell(0) : rwEnc2.getCell(0));
        cell2.setCellValue(formatFecha.format(objDate));
        ResultSetMetaData rsMetadata = rs.getMetaData();
        int renglonInicio = 5;
        int cnt1 = 0;
        //Define los estilos de las celdas a utilizar
        DataFormat mon = workbook.createDataFormat();
        XSSFCellStyle estiloGeneral = (XSSFCellStyle) Util.generaEstilo(workbook, 9, false, true, true, true, false);
        XSSFCellStyle estiloMoney = (XSSFCellStyle) Util.generaEstilo(workbook, 9, false, true, true, true, true);
        XSSFCellStyle estiloFecha = (XSSFCellStyle) Util.generaEstilo(workbook, 9, false, true, true, true, false);
        estiloFecha.setDataFormat(mon.getFormat("dd/mm/yyyy"));
        //Pinta el resultSet en el Excel
        while (rs.next()) {
            XSSFRow rw = (sheet0.getRow(renglonInicio + cnt1) == null ? sheet0.createRow(renglonInicio + cnt1) : sheet0.getRow(renglonInicio + cnt1));
            for (int i = 0; i <= 11; i++) {
                if (i == 6)
                    Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloFecha);
                else if (i == 10)
                    Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloMoney);
                else
                    Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloGeneral);
            }
            cnt1++;
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

    public static String reportePptoDevengado(Connection conn, Map<String, String> plantillas) throws Exception {
        PreparedStatement cs = null, cs2 = null, cs3 = null;
        ResultSet rs = null, rs2 = null, rs3 = null;
        String fileName = "";
        try {
            //se ejecuta el Store del saldo devengado para que salga actualizado el devengado en la vista
            cs2 = conn.prepareStatement("exec sp_saldoDevengado ");
            rs2 = cs2.executeQuery();
            //Se ejecuta la vista del presupuesto acumulado
            cs = conn.prepareStatement("exec sp_saldoDevengadoAcumulado ");
            rs = cs.executeQuery();
            //Se ejecuta la vista del presupuesto acumulado
            cs3 = conn.prepareStatement("exec sp_saldoDevengadoCalendario ");
            rs3 = cs3.executeQuery();
            log.debug("======== Fin ejecución Store y vistas para el reporte==========");
            fileName = reportePptoDevengadoExcel(rs, rs3, plantillas.get("PPTO_DEV"));
            log.debug("======== Fin ejecución formato en Excel==========");
            return fileName;
        } finally {
            CloseObject.closeObject(cs, false);
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(cs2, false);
            CloseObject.closeObject(rs2, false);
            CloseObject.closeObject(cs3, false);
            CloseObject.closeObject(rs3, false);
        }
    }

    private static String reportePptoDevengadoExcel(ResultSet rs, ResultSet rs3, String plantillaPath) throws Exception {
        //Generación del reporte Acumulado en Excel
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "Presupuesto" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xlsx";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        XSSFWorkbook workbook = new XSSFWorkbook(fsArchivo);
        fsArchivo.close();
        XSSFSheet sheet0 = workbook.getSheetAt(0);
        XSSFSheet sheet1 = workbook.getSheetAt(1);
        //Se genera la fecha en la fila 3
        Date objDate = new Date();
        String dateFormat = "dd-MMM-yyyy";
        SimpleDateFormat formatFecha = new SimpleDateFormat(dateFormat);
        XSSFRow rwEnc2 = (sheet0.getRow(3) == null ? (XSSFRow) sheet0.createRow(3) : (XSSFRow) sheet0.getRow(3));
        Cell cell2 = (rwEnc2.getCell(0) == null ? rwEnc2.createCell(0) : rwEnc2.getCell(0));
        cell2.setCellValue(formatFecha.format(objDate));
        ResultSetMetaData rsMetadata = rs.getMetaData();
        ResultSetMetaData rsMetadata2 = rs3.getMetaData();
        int renglonInicio = 5;
        int cnt1 = 0;
        Font fon = workbook.createFont();
        fon.setFontHeightInPoints((short) 8);
        fon.setFontName(tipoFuente);
        DataFormat df = workbook.createDataFormat();
        XSSFCellStyle estiloMoney = (XSSFCellStyle) Util.generaEstilo(workbook, 8, false, true, true, true, true);
        XSSFCellStyle estiloGeneral = (XSSFCellStyle) Util.generaEstilo(workbook, 8, false, true, true, true, false);
        XSSFCellStyle estiloFecha = (XSSFCellStyle) Util.generaEstilo(workbook, 8, false, true, true, true, false);
        estiloFecha.setDataFormat(df.getFormat("dd/mm/yyyy"));
        //Recorre el resultset para pintar los resultados en excel
        while (rs.next()) {
            XSSFRow rw = (sheet0.getRow(renglonInicio + cnt1) == null ? (XSSFRow) sheet0.createRow(renglonInicio + cnt1) : (XSSFRow) sheet0.getRow(renglonInicio + cnt1));
            for (int i = 0; i < rsMetadata.getColumnCount(); i++) {
                if (i >= 1) {
                    Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloMoney);
                } else {
                    Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloGeneral);
                }
            }
            cnt1++;
        }
        cnt1 = 0;
        while (rs3.next()) {
            XSSFRow rw = (sheet1.getRow(renglonInicio + cnt1) == null ? (XSSFRow) sheet1.createRow(renglonInicio + cnt1) : (XSSFRow) sheet1.getRow(renglonInicio + cnt1));
            for (int i = 0; i < rsMetadata2.getColumnCount(); i++) {
                if (i >= 2) {
                    Util.createExcelCellRep(i, rw, rs3, rsMetadata2.getColumnName(i + 1), rsMetadata2.getColumnType(i + 1), estiloMoney);
                } else {
                    Util.createExcelCellRep(i, rw, rs3, rsMetadata2.getColumnName(i + 1), rsMetadata2.getColumnType(i + 1), estiloGeneral);
                }
            }
            cnt1++;
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
