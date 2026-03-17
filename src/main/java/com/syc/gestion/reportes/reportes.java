package com.syc.gestion.reportes;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Map;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import com.syc.egresos.firmante.servlet.Firmante;
import com.syc.fortimax.core.Volumen;
import com.syc.fortimax.core.VolumenManager;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.obrapublica.ConfiguraAplicativoBusinessLogic;
import com.syc.reportes.core.ReporteConciliacionManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import net.sf.jasperreports.engine.JasperRunManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;
import java.nio.file.Paths;

public class reportes implements Serializable {

    private static final Logger log = LoggerFactory.getLogger(reportes.class);

    private final static long serialVersionUID = 1;

    private static ConfiguraAplicativoBusinessLogic cabl = new ConfiguraAplicativoBusinessLogic(GestionInterface.ATT_CONEXION);

    private static String tipoFuente = cabl.getSystemSetting("FUENTE_INSTITUCIONAL");

    public void execute(Connection conn, HttpServletRequest req, HttpServletResponse resp, String reportName, Map parms) {
        InputStream in = null;
        ServletOutputStream out = null;
        try {
            out = resp.getOutputStream();
            in = new FileInputStream(reportName);
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

    public void execute(Connection conn, OutputStream out, String reportName, Map<String, Object> parms) throws Exception {
        InputStream in = null;
        try {
            in = new FileInputStream(reportName);
            JasperRunManager.runReportToPdfStream(in, out, parms, conn);
            out.flush();
            out.close();
        } finally {
            try {
                if (in != null)
                    in.close();
            } catch (Exception exc) {
                exc.printStackTrace();
            }
            in = null;
            out = null;
        }
    }

    public static String EstadosFinancierosExcel(Connection conn, String fecha, String centroContable, String tipoCedula, String moneda, String nivel, String comentarios, String caracteristicas, Map<String, String> plantillas, String desagregado, String cEsConac, String esMesAA) throws Exception {
        CallableStatement cs = null;
        CallableStatement cs2 = null;
        CallableStatement cs3 = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        ResultSet rs3 = null;
        String query = null;
        String query2 = null;
        int mes = Integer.parseInt(fecha.substring(3, 5));
        int anio = Integer.parseInt(fecha.substring(6, 10));
        String fileName = "";
        try {
            if ("BalanzaMayor.jasper".equals(tipoCedula)) {
                // 1
                query = "{call sp_u_reportebalanza( ?, ?, ?, ? )}";
                cs = conn.prepareCall(query);
                cs.setInt(1, mes);
                cs.setInt(2, anio);
                cs.setString(3, moneda);
                cs.setString(4, centroContable);
                rs = cs.executeQuery();
            } else if ("EdoSituacionFinanciera.jasper".equals(tipoCedula)) {
                // 2
                query = "{call sp_u_estadosituacionfinanciera1_excel( ?, ?, ?, ?, ? ,?, ?, ?)}";
                query2 = "{call sp_u_estadosituacionfinanciera2_excel( ?, ?, ?, ?, ? , ?, ?, ?)}";
                cs = conn.prepareCall(query);
                cs.setInt(1, mes);
                cs.setInt(2, anio);
                cs.setString(3, nivel);
                cs.setString(4, moneda);
                cs.setInt(5, 1);
                cs.setString(6, centroContable);
                cs.setString(7, cEsConac);
                cs.setString(8, esMesAA);
                rs = cs.executeQuery();
                cs2 = conn.prepareCall(query2);
                cs2.setInt(1, mes);
                cs2.setInt(2, anio);
                cs2.setString(3, nivel);
                cs2.setString(4, moneda);
                cs2.setInt(5, 1);
                cs2.setString(6, centroContable);
                cs2.setString(7, cEsConac);
                cs2.setString(8, esMesAA);
                rs2 = cs2.executeQuery();
            } else if ("EdoActividades.jasper".equals(tipoCedula)) {
                // 3
                query = "{call sp_u_estadoactividades_excel( ?, ?, ?, ?, ?, ?, ?)}";
                cs = conn.prepareCall(query);
                cs.setInt(1, mes);
                cs.setInt(2, anio);
                cs.setString(3, nivel);
                cs.setString(4, moneda);
                cs.setString(5, centroContable);
                cs.setString(6, cEsConac);
                cs.setString(7, esMesAA);
                rs = cs.executeQuery();
            } else if ("EdoVariacionHacienda.jasper".equals(tipoCedula)) {
                // 4
                query = "{call sp_estadovariacion( ?, ?, ?, ?, ? )}";
                cs = conn.prepareCall(query);
                cs.setInt(1, mes);
                cs.setInt(2, anio);
                cs.setString(3, moneda);
                cs.setString(4, centroContable);
                cs.setString(5, cEsConac);
                rs = cs.executeQuery();
            } else if ("EdoAnaliticoActivo.jasper".equals(tipoCedula)) {
                // 5
                query = "{call sp_u_estadoanaliticoactivo_excel( ?, ?, ?, ?, ?, ?)}";
                cs = conn.prepareCall(query);
                cs.setInt(1, mes);
                cs.setInt(2, anio);
                cs.setString(3, nivel);
                cs.setString(4, moneda);
                cs.setString(5, centroContable);
                cs.setString(6, cEsConac);
                rs = cs.executeQuery();
            } else if ("EdoAnaliticoPasivo.jasper".equals(tipoCedula)) {
                // 6
                query = "{call sp_u_estadoanaliticopasivo_excel( ?, ?, ?, ?)}";
                cs = conn.prepareCall(query);
                cs.setInt(1, mes);
                cs.setInt(2, anio);
                cs.setString(3, moneda);
                cs.setString(4, centroContable);
                //cs.setString( 5, cEsConac );
                rs = cs.executeQuery();
            } else if ("EstadoActivoNoCirculante.jasper".equals(tipoCedula)) {
                // 7
                query = "{call sp_u_activonocirculante_excel( ?, ?, ? ,? )}";
                cs = conn.prepareCall(query);
                cs.setInt(1, mes);
                cs.setInt(2, anio);
                cs.setString(3, moneda);
                cs.setString(4, centroContable);
                rs = cs.executeQuery();
            } else if ("EdoFlujoEfectivo.jasper".equals(tipoCedula)) {
                // 8
                query = "{call sp_u_estadoflujoefectivo2( ?, ?, ?, ?, ?, ? )}";
                cs = conn.prepareCall(query);
                cs.setInt(1, mes);
                cs.setInt(2, anio);
                cs.setString(3, moneda);
                cs.setString(4, centroContable);
                cs.setString(5, cEsConac);
                cs.setString(6, esMesAA);
                rs = cs.executeQuery();
            } else if ("EdoCambiosSituacionFinanciera.jasper".equals(tipoCedula)) {
                // 9
                query = "{call sp_u_estadosituacionfinanciera1_excel( ?, ?, ?, ?, ? , ?, ?, ?)}";
                cs = conn.prepareCall(query);
                cs.setInt(1, mes);
                cs.setInt(2, anio);
                cs.setString(3, nivel);
                cs.setString(4, moneda);
                cs.setInt(5, 0);
                cs.setString(6, centroContable);
                cs.setString(7, cEsConac);
                cs.setString(8, esMesAA);
                rs = cs.executeQuery();
            } else if ("InfPasivosContingentes.jasper".equals(tipoCedula)) {
                // 10
                query = "{call sp_a_pasivosContingentes( ?, ?, ?, ? )}";
                cs = conn.prepareCall(query);
                cs.setInt(1, mes);
                cs.setInt(2, anio);
                cs.setString(3, moneda);
                cs.setString(4, centroContable);
                rs = cs.executeQuery();
            } else if ("ConciliacionCONAC.jasper".equals(tipoCedula)) {
                // 11
                query = "{call sp_a_conciliacion_ING_excel( ?, ?, ? )}";
                cs = conn.prepareCall(query);
                cs.setInt(1, mes);
                cs.setInt(2, anio);
                cs.setString(3, moneda);
                rs = cs.executeQuery();
            } else if ("ConciliacionCONACEg.jasper".equals(tipoCedula)) {
                // 12
                if ("desagregada".equals(desagregado)) {
                    query = "{call sp_a_conciliacion_EG_Desagregacion( ?, ?, ? )}";
                } else {
                    query = "{call sp_a_conciliacion_EG_excel( ?, ?, ? )}";
                }
                cs = conn.prepareCall(query);
                cs.setInt(1, mes);
                cs.setInt(2, anio);
                cs.setString(3, moneda);
                rs = cs.executeQuery();
            } else if ("EdoAnaliticoPasivoCONAC.jasper".equals(tipoCedula)) {
                // 13
                query = "{call sp_u_estadoanaliticopasivoConac_excel( ?, ?, ?, ? )}";
                cs = conn.prepareCall(query);
                cs.setInt(1, mes);
                cs.setInt(2, anio);
                cs.setString(3, moneda);
                cs.setString(4, centroContable);
                rs = cs.executeQuery();
            }
            if ("EdoSituacionFinanciera.jasper".equals(tipoCedula) || "EdoCambiosSituacionFinanciera.jasper".equals(tipoCedula) || "EdoActividades.jasper".equals(tipoCedula) || "EdoAnaliticoPasivoCONAC.jasper".equals(tipoCedula) || "ConciliacionCONACEg.jasper".equals(tipoCedula)) {
                fileName = generaReporteSFExcel(rs, rs2, rs3, plantillas.get("excel"), tipoCedula, fecha, desagregado, moneda);
            } else if ("ConciliacionCONAC.jasper".equals(tipoCedula)) {
                fileName = generaReporteSFExcel(rs, rs2, rs3, plantillas.get("excelIng"), tipoCedula, fecha, desagregado, moneda);
            } else
                fileName = generaReporteExcel(rs, rs2, rs3, plantillas.get("excel"), tipoCedula, fecha, desagregado, moneda);
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(rs2, false);
            CloseObject.closeObject(rs3, false);
            CloseObject.closeObject(cs, false);
            CloseObject.closeObject(cs2, false);
            CloseObject.closeObject(cs3, false);
        }
    }

    private static String generaReporteSFExcel(ResultSet rs, ResultSet rs2, ResultSet rs3, String plantillaPath, String tipoCedula, String fecha, String desagregado, String miles) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "EstadosFinancieros" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        int mesFin = 0;
        String anio = "";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        String estadoFinanciero = tipoCedula.substring(0, tipoCedula.lastIndexOf("."));
        mesFin = Integer.parseInt(fecha.substring(3, 5));
        anio = fecha.substring(6, 10);
        int renglonIni = 7;
        int cnt1 = 0;
        String moneda = "";
        String periodo = "Del 1 de Enero al " + Util.ultimoDiaMes(mesFin) + " de " + Util.nombreDeMes(mesFin) + " de " + anio;
        if ("1".equals(miles)) {
            moneda = "(Cifras en miles pesos)";
        } else
            moneda = "(Cifras en pesos)";
        Row rwEnc2 = (sheet0.getRow(2) == null ? sheet0.createRow(2) : sheet0.getRow(2));
        Cell cell2 = (rwEnc2.getCell(0) == null ? rwEnc2.createCell(0) : rwEnc2.getCell(0));
        cell2.setCellValue(periodo);
        Row rwEnc13 = (sheet0.getRow(3) == null ? sheet0.createRow(3) : sheet0.getRow(3));
        Cell cell13 = (rwEnc13.getCell(0) == null ? rwEnc13.createCell(0) : rwEnc13.getCell(0));
        cell13.setCellValue(moneda);
        Row rwEnc3 = (sheet0.getRow(6) == null ? sheet0.createRow(6) : sheet0.getRow(6));
        Cell cell3 = (rwEnc3.getCell(0) == null ? rwEnc3.createCell(0) : rwEnc3.getCell(0));
        cell3.setCellValue("Cuenta");
        Cell cell4 = (rwEnc3.getCell(1) == null ? rwEnc3.createCell(1) : rwEnc3.getCell(1));
        cell4.setCellValue("Descripción");
        if ("EdoCambiosSituacionFinanciera".equals(estadoFinanciero)) {
            Cell cell5 = (rwEnc3.getCell(2) == null ? rwEnc3.createCell(2) : rwEnc3.getCell(2));
            cell5.setCellValue("Origen");
            Cell cell6 = (rwEnc3.getCell(3) == null ? rwEnc3.createCell(3) : rwEnc3.getCell(3));
            cell6.setCellValue("Aplicación");
        } else if ("ConciliacionCONAC".equals(estadoFinanciero) || "ConciliacionCONACEg".equals(estadoFinanciero)) {
            Cell cell5 = (rwEnc3.getCell(2) == null ? rwEnc3.createCell(2) : rwEnc3.getCell(2));
            cell5.setCellValue("");
            Cell cell6 = (rwEnc3.getCell(3) == null ? rwEnc3.createCell(3) : rwEnc3.getCell(3));
            cell6.setCellValue("Total");
        } else {
            Cell cell12 = (rwEnc3.getCell(2) == null ? rwEnc3.createCell(2) : rwEnc3.getCell(2));
            cell12.setCellValue("Actual");
            Cell cell11 = (rwEnc3.getCell(3) == null ? rwEnc3.createCell(3) : rwEnc3.getCell(3));
            cell11.setCellValue("Anterior");
        }
        ResultSetMetaData rsMetadata = rs.getMetaData();
        CellStyle estiloMoney = Util.generaEstilo(workbook, 8, false, false, false, false, true);
        CellStyle esTitulo = Util.generaEstilo(workbook, 8, true, false, false, false, false);
        while (rs.next()) {
            Row rw = (sheet0.getRow(renglonIni + cnt1) == null ? sheet0.createRow(renglonIni + cnt1) : sheet0.getRow(renglonIni + cnt1));
            if ("3".equals(rs.getString(5)))
                cnt1++;
            if ("1".equals(rs.getString(5)) || "2".equals(rs.getString(5))) {
                if ("2".equals(rs.getString(5)))
                    cnt1++;
                Util.createExcelCellRep(0, rw, rs, rsMetadata.getColumnName(1), rsMetadata.getColumnType(1), esTitulo);
                Util.createExcelCellRep(1, rw, rs, rsMetadata.getColumnName(2), rsMetadata.getColumnType(2), esTitulo);
                if (!"0.0000".equals(rs.getString(3)) || !"0.0000".equals(rs.getString(4))) {
                    Util.createExcelCellRep(2, rw, rs, rsMetadata.getColumnName(3), rsMetadata.getColumnType(3), esTitulo);
                    Util.createExcelCellRep(3, rw, rs, rsMetadata.getColumnName(4), rsMetadata.getColumnType(4), esTitulo);
                }
                cnt1++;
            } else if ("0".equals(rs.getString(5)) || "3".equals(rs.getString(5))) {
                for (int i = 1; i < 5; i++) {
                    Util.createExcelCellRep(i - 1, rw, rs, rsMetadata.getColumnName(i), rsMetadata.getColumnType(i), estiloMoney);
                }
                cnt1++;
            }
        }
        if ("EdoSituacionFinanciera".equals(estadoFinanciero)) {
            Cell cell7 = (rwEnc3.getCell(5) == null ? rwEnc3.createCell(5) : rwEnc3.getCell(5));
            cell7.setCellValue("Cuenta");
            Cell cell8 = (rwEnc3.getCell(6) == null ? rwEnc3.createCell(6) : rwEnc3.getCell(6));
            cell8.setCellValue("Descripción");
            Cell cell9 = (rwEnc3.getCell(7) == null ? rwEnc3.createCell(7) : rwEnc3.getCell(7));
            cell9.setCellValue("Actual");
            Cell cell10 = (rwEnc3.getCell(8) == null ? rwEnc3.createCell(8) : rwEnc3.getCell(8));
            cell10.setCellValue("Anterior");
            ResultSetMetaData rs2Metadata = rs2.getMetaData();
            cnt1 = 0;
            while (rs2.next()) {
                Row rw = (sheet0.getRow(renglonIni + cnt1) == null ? sheet0.createRow(renglonIni + cnt1) : sheet0.getRow(renglonIni + cnt1));
                if ("1".equals(rs2.getString(5)) || "2".equals(rs2.getString(5))) {
                    if ("2".equals(rs2.getString(5)))
                        cnt1++;
                    Util.createExcelCellRep(5, rw, rs2, rs2Metadata.getColumnName(1), rs2Metadata.getColumnType(1), esTitulo);
                    Util.createExcelCellRep(6, rw, rs2, rs2Metadata.getColumnName(2), rs2Metadata.getColumnType(2), esTitulo);
                    if (!"0.0000".equals(rs2.getString(3)) || !"0.0000".equals(rs2.getString(4)) || "330".equals(rs2.getString(1))) {
                        Util.createExcelCellRep(7, rw, rs2, rs2Metadata.getColumnName(3), rs2Metadata.getColumnType(3), esTitulo);
                        Util.createExcelCellRep(8, rw, rs2, rs2Metadata.getColumnName(4), rs2Metadata.getColumnType(4), esTitulo);
                    }
                    cnt1++;
                } else if ("0".equals(rs2.getString(5))) {
                    for (int i = 1; i < 5; i++) {
                        Util.createExcelCellRep(i + 4, rw, rs2, rs2Metadata.getColumnName(i), rs2Metadata.getColumnType(i), estiloMoney);
                    }
                    cnt1++;
                }
            }
        }
        switch(estadoFinanciero) {
            case "EdoActividades":
                estadoFinanciero = "Estado de Actividades";
                break;
            case "EdoSituacionFinanciera":
                estadoFinanciero = "Estado de Situación Financiera";
                break;
            case "EdoCambiosSituacionFinanciera":
                estadoFinanciero = "Estado de Cambios en la Situación Financiera";
                break;
            case "ConciliacionCONACEg":
                estadoFinanciero = "Conciliación entre los Egresos Presupuestarios y los Gastos Contables";
                break;
            case "EdoAnaliticoPasivoCONAC":
                estadoFinanciero = "Estado Analítico de la Deuda y Otros Pasivos";
                break;
            case "ConciliacionCONAC":
                estadoFinanciero = "Conciliación entre los Ingresos Presupuestarios y Contables";
                break;
        }
        Row rwEnc1 = (sheet0.getRow(1) == null ? sheet0.createRow(1) : sheet0.getRow(1));
        Cell cell1 = (rwEnc1.getCell(0) == null ? rwEnc1.createCell(0) : rwEnc1.getCell(0));
        cell1.setCellValue(estadoFinanciero);
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

    private static String generaReporteExcel(ResultSet rs, ResultSet rs2, ResultSet rs3, String plantillaPath, String tipoCedula, String fecha, String desagregado, String miles) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "EstadosFinancieros" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        int mesFin = 0;
        String anio = "", moneda = "";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        String estadoFinanciero = tipoCedula.substring(0, tipoCedula.lastIndexOf("."));
        mesFin = Integer.parseInt(fecha.substring(3, 5));
        anio = fecha.substring(6, 10);
        int renglonIni = 7;
        int cnt1 = 0;
        int inicia = 0;
        String periodo = "Del 1 de Enero al " + Util.ultimoDiaMes(mesFin) + " de " + Util.nombreDeMes(mesFin) + " de " + anio;
        if ("1".equals(miles)) {
            moneda = "(Cifras en miles pesos)";
        } else
            moneda = "(Cifras en pesos)";
        //Util.resultSetToExcel(rs, sheet0, renglonIni, true);
        ResultSetMetaData rsMetadata = rs.getMetaData();
        CellStyle estiloMoney = Util.generaEstilo(workbook, 8, false, false, false, false, true);
        CellStyle estiloTabla = Util.generaEstilo(workbook, 8, false, false, false, false, true);
        CellStyle estiloTitulo = Util.generaEstilo(workbook, 8, true, false, false, false, true);
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        font.setBold(true);
        CellStyle estilo = workbook.createCellStyle();
        estilo.setFont(font);
        estilo.setAlignment(HorizontalAlignment.CENTER);
        if (estadoFinanciero.equalsIgnoreCase("EdoVariacionHacienda") || estadoFinanciero.equalsIgnoreCase("EdoFlujoEfectivo")) {
            inicia = 1;
        } else {
            inicia = 0;
        }
        while (rs.next()) {
            Row rw = (sheet0.getRow(renglonIni + cnt1) == null ? sheet0.createRow(renglonIni + cnt1) : sheet0.getRow(renglonIni + cnt1));
            if (estadoFinanciero.equalsIgnoreCase("EdoVariacionHacienda")) {
                for (int i = inicia; i < rsMetadata.getColumnCount() - 1; i++) {
                    int val = rs.getInt(rsMetadata.getColumnName(8));
                    String nombre = rs.getString(rsMetadata.getColumnName(2));
                    if (nombre.equals("") || nombre == null)
                        break;
                    if (val == 1) {
                        Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTitulo);
                    } else {
                        Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloMoney);
                    }
                }
            } else if (estadoFinanciero.equalsIgnoreCase("EdoFlujoEfectivo")) {
                for (int i = inicia; i < rsMetadata.getColumnCount() - 2; i++) {
                    int val = rs.getInt(rsMetadata.getColumnName(5));
                    String nombre = rs.getString(rsMetadata.getColumnName(2));
                    if (nombre.equals("") || nombre == null)
                        break;
                    if (val == 1) {
                        Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTitulo);
                    } else {
                        Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloMoney);
                    }
                }
            } else {
                for (int i = inicia; i < rsMetadata.getColumnCount(); i++) {
                    if (i > 2) {
                        Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloMoney);
                    } else {
                        Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
                    }
                }
            }
            cnt1++;
        }
        switch(estadoFinanciero) {
            case "EdoVariacionHacienda":
                estadoFinanciero = "Estado de Variación en la Hacienda Pública";
                for (int i = 1; i <= 6; i++) {
                    Row rwEnc4 = (sheet0.getRow(renglonIni - 1) == null ? sheet0.createRow(renglonIni - 1) : sheet0.getRow(renglonIni - 1));
                    Cell cell4 = (rwEnc4.getCell(i) == null ? rwEnc4.createCell(i) : rwEnc4.getCell(i));
                    cell4.setCellStyle(estilo);
                    switch(i) {
                        case 1:
                            cell4.setCellValue("Hacienda Pública/Patrimonio Contribuido Neto de 2023");
                            break;
                        case 2:
                            cell4.setCellValue("Hacienda Pública/Patrimonio Contribuido");
                            break;
                        case 3:
                            cell4.setCellValue("Hacienda Pública/Patrimonio Generado de Ejercicios Anteriores");
                            break;
                        case 4:
                            cell4.setCellValue("Hacienda Pública/Patrimonio Generado del Ejercicio");
                            break;
                        case 5:
                            cell4.setCellValue("Exceso o Insuficiencia en la Actualización de la Hacienda Pública/Patrimonio");
                            break;
                        case 6:
                            cell4.setCellValue("Total");
                            break;
                    }
                }
                break;
            case "EdoAnaliticoActivo":
                estadoFinanciero = "Estado Analítico del Activo";
                for (int i = 0; i <= 6; i++) {
                    Row rwEnc4 = (sheet0.getRow(renglonIni - 1) == null ? sheet0.createRow(renglonIni - 1) : sheet0.getRow(renglonIni - 1));
                    Cell cell4 = (rwEnc4.getCell(i) == null ? rwEnc4.createCell(i) : rwEnc4.getCell(i));
                    cell4.setCellStyle(estilo);
                    switch(i) {
                        case 0:
                            cell4.setCellValue("Cuenta");
                            break;
                        case 1:
                            cell4.setCellValue("Descripción");
                            break;
                        case 2:
                            cell4.setCellValue("Saldo Inicial");
                            break;
                        case 3:
                            cell4.setCellValue("Cargos");
                            break;
                        case 4:
                            cell4.setCellValue("Abonos");
                            break;
                        case 5:
                            cell4.setCellValue("Saldo Final");
                            break;
                        case 6:
                            cell4.setCellValue("Flujo Periodo");
                            break;
                    }
                }
                break;
            case "EdoAnaliticoPasivo":
                estadoFinanciero = "Estado Analítico de la Deuda y Otros Pasivos";
                for (int i = 0; i <= 2; i++) {
                    Row rwEnc4 = (sheet0.getRow(renglonIni - 1) == null ? sheet0.createRow(renglonIni - 1) : sheet0.getRow(renglonIni - 1));
                    Cell cell4 = (rwEnc4.getCell(i) == null ? rwEnc4.createCell(i) : rwEnc4.getCell(i));
                    cell4.setCellStyle(estilo);
                    switch(i) {
                        case 0:
                            cell4.setCellValue("Cuenta");
                            break;
                        case 1:
                            cell4.setCellValue("Saldo inicial periodo");
                            break;
                        case 2:
                            cell4.setCellValue("Saldo Final del periodo");
                            break;
                    }
                }
                break;
            case "EstadoActivoNoCirculante":
                estadoFinanciero = "Estado Analítico del Activo no Circulante";
                for (int i = 0; i <= 11; i++) {
                    Row rwEnc4 = (sheet0.getRow(renglonIni - 1) == null ? sheet0.createRow(renglonIni - 1) : sheet0.getRow(renglonIni - 1));
                    Cell cell4 = (rwEnc4.getCell(i) == null ? rwEnc4.createCell(i) : rwEnc4.getCell(i));
                    cell4.setCellStyle(estilo);
                    switch(i) {
                        case 0:
                            cell4.setCellValue("Cuenta");
                            break;
                        case 1:
                            cell4.setCellValue("Descripcion");
                            break;
                        case 2:
                            cell4.setCellValue("Saldo inicial");
                            break;
                        case 3:
                            cell4.setCellValue("Cargos");
                            break;
                        case 4:
                            cell4.setCellValue("Abonos");
                            break;
                        case 5:
                            cell4.setCellValue("Flujo del Periodo");
                            break;
                        case 6:
                            cell4.setCellValue("Cargos");
                            break;
                        case 7:
                            cell4.setCellValue("Abonos");
                            break;
                        case 8:
                            cell4.setCellValue("Flujo del Periodo");
                            break;
                        case 9:
                            cell4.setCellValue("Cargos");
                            break;
                        case 10:
                            cell4.setCellValue("Abonos");
                            break;
                        case 11:
                            cell4.setCellValue("Saldo Final");
                            break;
                    }
                }
                break;
            case "EdoFlujoEfectivo":
                estadoFinanciero = "Estado de Flujos de Efectivo";
                for (int i = 1; i <= 2; i++) {
                    Row rwEnc4 = (sheet0.getRow(renglonIni - 1) == null ? sheet0.createRow(renglonIni - 1) : sheet0.getRow(renglonIni - 1));
                    Cell cell4 = (rwEnc4.getCell(i) == null ? rwEnc4.createCell(i) : rwEnc4.getCell(i));
                    cell4.setCellStyle(estilo);
                    switch(i) {
                        case 1:
                            cell4.setCellValue("Descripción");
                            break;
                        case 2:
                            cell4.setCellValue("Año actual");
                            break;
                        case 3:
                            cell4.setCellValue("Año anterior");
                            break;
                    }
                }
                break;
            case "InfPasivosContingentes":
                estadoFinanciero = "Informe sobre Pasivos Contingentes";
                for (int i = 0; i <= 2; i++) {
                    Row rwEnc4 = (sheet0.getRow(renglonIni - 1) == null ? sheet0.createRow(renglonIni - 1) : sheet0.getRow(renglonIni - 1));
                    Cell cell4 = (rwEnc4.getCell(i) == null ? rwEnc4.createCell(i) : rwEnc4.getCell(i));
                    cell4.setCellStyle(estilo);
                    switch(i) {
                        case 0:
                            cell4.setCellValue("Cuenta");
                            break;
                        case 1:
                            cell4.setCellValue("Descripción");
                            break;
                        case 2:
                            cell4.setCellValue("Saldo Final");
                            break;
                    }
                }
                break;
        }
        Row rwEnc1 = (sheet0.getRow(1) == null ? sheet0.createRow(1) : sheet0.getRow(1));
        Cell cell1 = (rwEnc1.getCell(0) == null ? rwEnc1.createCell(0) : rwEnc1.getCell(0));
        cell1.setCellValue(estadoFinanciero);
        Row rwEnc2 = (sheet0.getRow(2) == null ? sheet0.createRow(2) : sheet0.getRow(2));
        Cell cell2 = (rwEnc2.getCell(0) == null ? rwEnc2.createCell(0) : rwEnc2.getCell(0));
        cell2.setCellValue(periodo);
        Row rwEnc13 = (sheet0.getRow(3) == null ? sheet0.createRow(3) : sheet0.getRow(3));
        Cell cell13 = (rwEnc13.getCell(0) == null ? rwEnc13.createCell(0) : rwEnc13.getCell(0));
        cell13.setCellValue(moneda);
        File fsalida = new File(file_name);
        FileOutputStream fos = new FileOutputStream(fsalida);
        BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
        workbook.write(bos);
        workbook.close();
        /* Cierra Flujos */
        bos.flush();
        bos.close();
        fos.flush();
        fos.close();
        return file_name;
    }

    public void executeFIEL(Connection conn, String reportPath, String reportName, Map<String, Object> parms, String[] firmantes, int idReporte) throws Exception {
        int numfirmas = (Integer) parms.get("nfirmas");
        Firmante[] firmantesReporte = instanciaFirmantes(conn, firmantes, numfirmas);
        Volumen volumen = VolumenManager.getVolumen(conn);
        FirmaElectronicaReporte fer = new EstadosFinancierosFirma(reportPath, reportName, parms);
        fer.setIdTipoReporte(idReporte);
        String pathReporte = fer.generaArchivoFirma(conn, volumen, null, false);
        log.debug("Object: " + String.valueOf("Generado reporte para firma electronica en: " + pathReporte));
        String pathAcuse = ((EstadosFinancierosFirma) fer).generaAcuse(conn, volumen, null, false);
        log.debug("Object: " + String.valueOf("Generado reporte para acuse en: " + pathAcuse));
        fer.setPathReporte(pathReporte);
        fer.setOrden(1);
        fer.setFirmantesReporte(firmantesReporte);
        fer.setMes((Integer) parms.get("mes"));
        ((EstadosFinancierosFirma) fer).setRutaAcuseImpreso(pathAcuse);
        ((EstadosFinancierosFirma) fer).setIdTipoMoneda((Integer) parms.get("miles"));
        int iNivel = parms.get("nivel") == null ? 3 : (Integer) parms.get("nivel");
        ((EstadosFinancierosFirma) fer).setIdNivel(iNivel);
        ((EstadosFinancierosFirma) fer).setRutaReporteImpreso(pathReporte);
        ((EstadosFinancierosFirma) fer).setNumeroFirmas(numfirmas);
        int id = ((EstadosFinancierosFirma) fer).registraReporte(conn);
        ((EstadosFinancierosFirma) fer).setIdEstadoFinanciero(id);
        fer.setIdField(id);
        fer.registraFirmantes(conn);
        fer.notificaOperacionPendiente(conn, firmantesReporte[0].getTipoAutorizador());
    }

    private Firmante[] instanciaFirmantes(Connection conn, String[] firmantes, int numfirmas) {
        Firmante[] firmante = new Firmante[numfirmas - 1];
        for (int i = 1; i < numfirmas; i++) {
            Firmante f = new Firmante();
            f.setNumeroEmpleado(Integer.parseInt(firmantes[i]));
            if (i == 1)
                f.setTipoAutorizador(numfirmas == 3 ? "2" : "1");
            else if (i == 2)
                f.setTipoAutorizador(numfirmas == 3 ? "4" : "3");
            else if (i == 3)
                f.setTipoAutorizador("4");
            firmante[i - 1] = f;
        }
        return firmante;
    }

    public void executeConciliaFIEL(Connection conn, String reportPath, String reportName, Map<String, Object> parms) throws Exception {
        String idConciliacion = parms.get("nConciliacion").toString();
        Firmante[] firmantesReporte = instanciaFirmantesConciliacion(conn, idConciliacion);
        Volumen volumen = VolumenManager.getVolumen(conn);
        ConciliacionFirma cf = new ConciliacionFirma(reportPath, reportName, parms);
        cf.setIdTipoReporte(5);
        cf.setIdConciliacion(Integer.parseInt(idConciliacion));
        String pathReporte = cf.generaArchivoFirma(conn, volumen, null, false);
        log.debug("Object: " + String.valueOf("Generado reporte para firma electronica en: " + pathReporte));
        String pathAcuse = ((ConciliacionFirma) cf).generaAcuse(conn, volumen, null, false);
        log.debug("Object: " + String.valueOf("Generado reporte para acuse en: " + pathAcuse));
        cf.setPathReporte(pathReporte);
        cf.setOrden(1);
        cf.setFirmantesReporte(firmantesReporte);
        cf.cargaInformacion(conn);
        ((ConciliacionFirma) cf).setRutaAcuseImpreso(pathAcuse);
        ((ConciliacionFirma) cf).setRutaReporteImpreso(pathReporte);
        //insertar en tConciliacionFirmaElectronica
        ReporteConciliacionManager.insertaConciliacionFirmaElectronica(conn, cf);
        cf.notificaOperacionPendiente(conn, firmantesReporte[0].getTipoAutorizador());
    }

    private Firmante[] instanciaFirmantesConciliacion(Connection conn, String idConciliacion) throws Exception {
        Firmante[] firmante = new Firmante[3];
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            pst = conn.prepareStatement("SELECT cNumeroEmpleado, nOrden FROM tFirmanteConciliacion WITH (NOLOCK) WHERE nConciliacion = ? ORDER BY nOrden");
            pst.setString(1, idConciliacion);
            rs = pst.executeQuery();
            int i = 0;
            while (rs.next()) {
                Firmante f = new Firmante();
                f.setNumeroEmpleado(rs.getInt("cNumeroEmpleado"));
                f.setTipoAutorizador(rs.getString("nOrden"));
                firmante[i] = f;
                i++;
            }
        } finally {
            CloseObject.closeObject(pst);
            CloseObject.closeObject(rs);
        }
        return firmante;
    }
}
