package com.syc.pasivoscontingentes;

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
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Map;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import com.syc.gestion.util.Util;
import com.syc.reportes.core.ReporteAcreedoresDeudoresManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;
import java.nio.file.Paths;

public class PasivosContingentesManager {

    private static final Logger log = LoggerFactory.getLogger(ReporteAcreedoresDeudoresManager.class);

    public static String PasivoContingenteManager(Connection conn, String mes, String anio, Map<String, String> plantillas) throws Exception {
        log.info("Object: {}", String.format("Iniciando cedulas de pasivos contigentes patrimoniales"));
        CallableStatement cs = null, cs2 = null;
        ResultSet rs = null, rs2 = null;
        String query = "{call sp_cedulaPasivosContingentes( ? )}";
        String query2 = "{call sp_PasivosContengentesMensual( ? )}";
        String fileName = "";
        String fecha = null;
        int mesS = Integer.parseInt(mes);
        int ejFiscal = Integer.parseInt(anio);
        int ultimoDiaMes = obtenerUltimoDiaMes(ejFiscal, mesS);
        fecha = ultimoDiaMes + "/" + mes + "/" + ejFiscal;
        try {
            cs = conn.prepareCall(query);
            cs.setString(1, fecha);
            log.debug("Object: {}", String.format("Ejecutando[" + query + "]%S", fecha));
            rs = cs.executeQuery();
            cs2 = conn.prepareCall(query2);
            cs2.setString(1, fecha);
            log.debug("Object: {}", String.format("Ejecutando[" + query2 + "]%S", fecha));
            rs2 = cs2.executeQuery();
            fileName = generaReporteMasivo(rs, rs2, plantillas.get("CEDULA"), mesS, anio);
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(cs, false);
            CloseObject.closeObject(rs2, false);
            CloseObject.closeObject(cs2, false);
        }
    }

    public static int obtenerUltimoDiaMes(int anio, int mes) {
        Calendar calendario = Calendar.getInstance();
        calendario.set(anio, mes - 1, 1);
        return calendario.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    private static String generaReporteMasivo(ResultSet rs, ResultSet rs2, String plantillaPath, int mes, String anio) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "CedulaPasivosContingentes" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        Sheet sheet1 = workbook.getSheetAt(1);
        int cnt = 0;
        int cnt2 = 0;
        ResultSetMetaData rsMetadata = rs.getMetaData();
        ResultSetMetaData rsMetadata2 = rs2.getMetaData();
        int renglonInicio = 7;
        int renglonInicio2 = 5;
        String periodo = "";
        double indemnizacion = 0.00;
        double actualizacionmensual = 0.00;
        int diasMes = 0;
        periodo = "Mes: " + Util.NOMBRE_MESES_MX[mes - 1];
        Row rwEnc2 = (sheet0.getRow(4) == null ? sheet0.createRow(4) : sheet0.getRow(4));
        Cell cell2 = (rwEnc2.getCell(0) == null ? rwEnc2.createCell(0) : rwEnc2.getCell(0));
        cell2.setCellValue(periodo);
        CellStyle estiloTabla = workbook.createCellStyle();
        estiloTabla.setBorderRight(BorderStyle.HAIR);
        estiloTabla.setBorderLeft(BorderStyle.HAIR);
        estiloTabla.setBorderTop(BorderStyle.HAIR);
        estiloTabla.setBorderBottom(BorderStyle.HAIR);
        while (rs.next()) {
            Row rw = (sheet0.getRow(renglonInicio + cnt) == null ? sheet0.createRow(renglonInicio + cnt) : sheet0.getRow(renglonInicio + cnt));
            for (int i = 0; i < rsMetadata.getColumnCount(); i++) {
                Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
            }
            cnt++;
        }
        while (rs2.next()) {
            Row rw = (sheet1.getRow(renglonInicio2 + cnt2) == null ? sheet1.createRow(renglonInicio2 + cnt2) : sheet1.getRow(renglonInicio2 + cnt2));
            for (int i = 0; i < rsMetadata2.getColumnCount(); i++) {
                Util.createExcelCellRep(i, rw, rs2, rsMetadata2.getColumnName(i + 1), rsMetadata2.getColumnType(i + 1), estiloTabla);
            }
            cnt2++;
            indemnizacion += rs2.getDouble("nMontoEstimado");
            actualizacionmensual += rs2.getDouble("actualizacionMensual");
            diasMes = rs2.getInt("diasAcumulados");
        }
        Row rwEnc3 = (sheet1.getRow(cnt2 + 5) == null ? sheet1.createRow(cnt2 + 5) : sheet1.getRow(cnt2 + 5));
        Cell cell3 = (rwEnc3.getCell(3) == null ? rwEnc3.createCell(3) : rwEnc3.getCell(3));
        cell3.setCellValue(indemnizacion);
        Row rwEnc4 = (sheet1.getRow(cnt2 + 5) == null ? sheet1.createRow(cnt2 + 5) : sheet1.getRow(cnt2 + 5));
        Cell cell4 = (rwEnc4.getCell(5) == null ? rwEnc4.createCell(5) : rwEnc4.getCell(5));
        cell4.setCellValue(actualizacionmensual);
        Row rwEnc6 = (sheet1.getRow(cnt2 + 5) == null ? sheet1.createRow(cnt2 + 5) : sheet1.getRow(cnt2 + 5));
        Cell cell6 = (rwEnc6.getCell(0) == null ? rwEnc6.createCell(0) : rwEnc6.getCell(0));
        cell6.setCellValue("Totales");
        Row rwEnc7 = (sheet1.getRow(4) == null ? sheet1.createRow(4) : sheet1.getRow(4));
        Cell cell7 = (rwEnc7.getCell(5) == null ? rwEnc7.createCell(5) : rwEnc7.getCell(5));
        cell7.setCellValue("ACTUALIZACIONES DE " + Util.NOMBRE_MESES_MX[mes - 1] + " " + anio);
        Row rwEnc8 = (sheet1.getRow(1) == null ? sheet1.createRow(1) : sheet1.getRow(1));
        Cell cell8 = (rwEnc8.getCell(0) == null ? rwEnc8.createCell(0) : rwEnc8.getCell(0));
        cell8.setCellValue("PASIVOS CONTINGENTES DE DEMANDAS JUDICIALES DEL 1 AL " + diasMes + " DE " + Util.NOMBRE_MESES_MX[mes - 1] + " " + anio);
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

    public static int ValidaExistencia(Connection conn, int nEjercicioFiscal) throws Exception {
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        int nExiste = 0;
        String query = "SELECT COUNT(idPasivoContingente) AS total FROM dbo.tpasivosContingentesAnt WHERE nEjercicioFiscal = ?";
        try {
            pstmnt = conn.prepareStatement(query);
            pstmnt.setInt(1, nEjercicioFiscal);
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                nExiste = rs.getInt(1);
            }
        } finally {
            CloseObject.closeObject(pstmnt, false);
            CloseObject.closeObject(rs, false);
        }
        return nExiste;
    }

    public static boolean LimpiaPasivos(Connection conn, int nEjercicioFiscal) throws Exception {
        PreparedStatement pstmnt = null;
        boolean retval;
        String query = "DELETE dbo.tpasivosContingentesAnt WHERE nEjercicioFiscal = ?";
        try {
            pstmnt = conn.prepareStatement(query);
            pstmnt.setInt(1, nEjercicioFiscal);
            retval = pstmnt.execute();
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        return retval;
    }

    public static void PasivosEjerciciosAnteriores(Connection conn, int anio) throws Exception {
        log.info("Object: {}", String.format("Actualizando saldos para el siguiente ejercicio fiscal"));
        PreparedStatement ps = null;
        String query = "INSERT INTO tpasivosContingentesAnt " + " SELECT idPasivoContingente " + " 	, (SELECT aEjercicioFiscal FROM dbo.tEjercicioFiscal WHERE cActivo = 1) AS nEjercicioFiscal " + " 	, ISNULL(SUM(nEnero),0) + ISNULL(SUM(nFebrero),0) + ISNULL(SUM(nMarzo),0) + ISNULL(SUM(nAbril),0) + ISNULL(SUM(nMayo),0) + ISNULL(SUM(nJunio),0) + ISNULL(SUM(nJulio),0) + ISNULL(SUM(nAgosto),0) + ISNULL(SUM(nSeptiembre),0) + ISNULL(SUM(nOctubre),0) + ISNULL(SUM(nNoviembre),0) + ISNULL(SUM(nDiciembre),0) AS nInteresesAnuales " + " FROM dbo.tpasivosContingentesAct " + " GROUP BY idPasivoContingente";
        try {
            ps = conn.prepareStatement(query);
            ps.execute();
        } finally {
            CloseObject.closeObject(ps, false);
        }
    }

    public static void bajaPasivo(Connection conn, String RFC, String PC, String fAplicacion) throws Exception {
        PreparedStatement pstmnt = null;
        String query = "UPDATE tpasivosContingentes SET Estatus = 1, cFechaBaja = ? WHERE cRFC = ? AND cSubcuenta = ?";
        try {
            pstmnt = conn.prepareStatement(query);
            pstmnt.setString(1, fAplicacion);
            pstmnt.setString(2, RFC);
            pstmnt.setString(3, PC);
            pstmnt.execute();
        } finally {
            CloseObject.closeObject(pstmnt);
        }
    }

    public static String PasivosContingentesLaboral(Connection conn, String mes, String anio, Map<String, String> plantillas) throws Exception {
        log.info("Object: {}", String.format("Iniciando cedulas de pasivos contigentes patrimoniales"));
        CallableStatement cs = null;
        ResultSet rs = null;
        String query = "{call sp_cedulaPasivosContingentesLaborales( ? )}";
        String fileName = "";
        String fecha = null;
        int mesS = Integer.parseInt(mes);
        int ejFiscal = Integer.parseInt(anio);
        int ultimoDiaMes = obtenerUltimoDiaMes(ejFiscal, mesS);
        fecha = ultimoDiaMes + "/" + mes + "/" + ejFiscal;
        try {
            cs = conn.prepareCall(query);
            cs.setString(1, fecha);
            log.debug("Object: {}", String.format("Ejecutando[" + query + "]%S", fecha));
            rs = cs.executeQuery();
            fileName = generaReporteLaboral(rs, plantillas.get("CEDULALABORAL"), mesS, anio);
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(cs, false);
        }
    }

    private static String generaReporteLaboral(ResultSet rs, String plantillaPath, int mes, String anio) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "CedulaPasivosContingentesLaboral" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        int cnt = 0;
        ResultSetMetaData rsMetadata = rs.getMetaData();
        int renglonInicio = 7;
        String periodo = "";
        periodo = "Mes: " + Util.NOMBRE_MESES_MX[mes - 1];
        Row rwEnc2 = (sheet0.getRow(4) == null ? sheet0.createRow(4) : sheet0.getRow(4));
        Cell cell2 = (rwEnc2.getCell(0) == null ? rwEnc2.createCell(0) : rwEnc2.getCell(0));
        cell2.setCellValue(periodo);
        CellStyle estiloTabla = workbook.createCellStyle();
        estiloTabla.setBorderRight(BorderStyle.HAIR);
        estiloTabla.setBorderLeft(BorderStyle.HAIR);
        estiloTabla.setBorderTop(BorderStyle.HAIR);
        estiloTabla.setBorderBottom(BorderStyle.HAIR);
        while (rs.next()) {
            Row rw = (sheet0.getRow(renglonInicio + cnt) == null ? sheet0.createRow(renglonInicio + cnt) : sheet0.getRow(renglonInicio + cnt));
            for (int i = 0; i < rsMetadata.getColumnCount(); i++) {
                Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
            }
            cnt++;
        }
        Row rwEnc3 = (sheet0.getRow(6) == null ? sheet0.createRow(6) : sheet0.getRow(6));
        Cell cell3 = (rwEnc3.getCell(9) == null ? rwEnc3.createCell(9) : rwEnc3.getCell(9));
        cell3.setCellValue("Modificacion del Monto del Pasivo Contingente " + Util.NOMBRE_MESES_MX[mes - 1] + "-" + anio);
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

    public static String PasivosContingentesAntiguedad(Connection conn, String mes, String anio, Map<String, String> plantillas, String unidad) throws Exception {
        log.info("Object: {}", String.format("Iniciando cedulas de pasivos contigentes patrimoniales"));
        CallableStatement cs = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        PreparedStatement ps = null;
        String query = "{call sp_cedulaPasivosContingentesAntiguedad( ? )}";
        String query2 = "select D_DESCRIPCION from tCatUnidadResponsable WHERE cUnidadResponsable = ?";
        String fileName = "";
        String fecha = null;
        int mesS = Integer.parseInt(mes);
        int ejFiscal = Integer.parseInt(anio);
        String unidadResponsable = "";
        int ultimoDiaMes = obtenerUltimoDiaMes(ejFiscal, mesS);
        fecha = ultimoDiaMes + "/" + mes + "/" + ejFiscal;
        try {
            cs = conn.prepareCall(query);
            cs.setString(1, fecha);
            log.debug("Object: {}", String.format("Ejecutando[" + query + "]%S", fecha));
            rs = cs.executeQuery();
            ps = conn.prepareStatement(query2);
            ps.setString(1, unidad);
            rs2 = ps.executeQuery();
            if (rs2.next()) {
                unidadResponsable = rs2.getString(1);
            }
            fileName = generaReporteAntiguedad(rs, plantillas.get("CEDULAANTIGUEDAD"), mesS, anio, unidadResponsable);
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(cs, false);
        }
    }

    private static String generaReporteAntiguedad(ResultSet rs, String plantillaPath, int mes, String anio, String unidadResponsable) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "CedulaPasivosContingentesAntiguedad" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        int cnt = 0;
        ResultSetMetaData rsMetadata = rs.getMetaData();
        int renglonInicio = 8;
        String periodo = "";
        periodo = "MES DE: " + Util.NOMBRE_MESES_MX[mes - 1];
        Row rwEnc = (sheet0.getRow(1) == null ? sheet0.createRow(1) : sheet0.getRow(1));
        Cell cell = (rwEnc.getCell(0) == null ? rwEnc.createCell(0) : rwEnc.getCell(0));
        cell.setCellValue(unidadResponsable);
        Row rwEnc2 = (sheet0.getRow(3) == null ? sheet0.createRow(3) : sheet0.getRow(3));
        Cell cell2 = (rwEnc2.getCell(0) == null ? rwEnc2.createCell(0) : rwEnc2.getCell(0));
        cell2.setCellValue(periodo);
        CellStyle estiloTabla = workbook.createCellStyle();
        estiloTabla.setBorderRight(BorderStyle.HAIR);
        estiloTabla.setBorderLeft(BorderStyle.HAIR);
        estiloTabla.setBorderTop(BorderStyle.HAIR);
        estiloTabla.setBorderBottom(BorderStyle.HAIR);
        while (rs.next()) {
            Row rw = (sheet0.getRow(renglonInicio + cnt) == null ? sheet0.createRow(renglonInicio + cnt) : sheet0.getRow(renglonInicio + cnt));
            for (int i = 0; i < rsMetadata.getColumnCount(); i++) {
                Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
            }
            cnt++;
        }
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

    public static String PasivosContingentesTrimestral(Connection conn, String mes, String anio, Map<String, String> plantillas, String unidad) throws Exception {
        log.info("Object: {}", String.format("Iniciando cedulas de pasivos contigentes patrimoniales"));
        CallableStatement cs = null;
        ResultSet rs = null, rs2 = null, rs3 = null, rs4 = null;
        PreparedStatement ps = null, ps2 = null, ps3 = null;
        String query = "{CALL sp_cedulaPasivosContingentesTrimestral( ? )}";
        String query2 = "SELECT D_DESCRIPCION FROM tCatUnidadResponsable WHERE cUnidadResponsable = ?";
        String query3 = "SELECT cAccionesT, cNotaT FROM tpasivosContingentesNotaT WHERE cTrimestre = ?";
        String query4 = "SELECT nTipoPasivo, cAntiguedad, nSubTotal FROM tPasivosTrimestralHistorico WHERE nTrimestre = ? ";
        String fileName = "";
        String fecha = null;
        int mesS = Integer.parseInt(mes);
        int ejFiscal = Integer.parseInt(anio);
        String unidadResponsable = "";
        String cAccionesT = "";
        String cNotaT = "";
        int ultimoDiaMes = obtenerUltimoDiaMes(ejFiscal, mesS);
        fecha = ultimoDiaMes + "/" + mes + "/" + ejFiscal;
        try {
            cs = conn.prepareCall(query);
            cs.setString(1, fecha);
            log.debug("Object: {}", String.format("Ejecutando[" + query + "]%S", fecha));
            rs = cs.executeQuery();
            ps = conn.prepareStatement(query2);
            ps.setString(1, unidad);
            log.debug("Object: {}", String.format("Ejecutando[" + query2 + "]%S", unidad));
            rs2 = ps.executeQuery();
            ps2 = conn.prepareStatement(query3);
            ps2.setInt(1, mesS);
            log.debug("Object: {}", String.format("Ejecutando[" + query3 + "]%S", mes));
            rs3 = ps2.executeQuery();
            ps3 = conn.prepareStatement(query4);
            ps3.setInt(1, mesS);
            log.debug("Object: {}", String.format("Ejecutando[" + query4 + "]%S", mes));
            rs4 = ps3.executeQuery();
            if (rs2.next()) {
                unidadResponsable = rs2.getString(1);
            }
            if (rs3.next()) {
                cAccionesT = rs3.getString(1);
                cNotaT = rs3.getString(2);
            }
            fileName = generaReporteTrimestral(rs, rs4, plantillas.get("CEDULATRIMESTRAL"), mesS, anio, unidadResponsable, cAccionesT, cNotaT, ejFiscal);
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(rs2, false);
            CloseObject.closeObject(rs3, false);
            CloseObject.closeObject(cs, false);
            CloseObject.closeObject(ps, false);
            CloseObject.closeObject(ps2, false);
        }
    }

    private static String generaReporteTrimestral(ResultSet rs, ResultSet rs4, String plantillaPath, int mes, String anio, String unidadResponsable, String cAccionesT, String cNotaT, int ejFiscal) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "CedulaPasivosContingentes" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        ResultSetMetaData rsMetadata = rs.getMetaData();
        ResultSetMetaData rs4Metadata = rs4.getMetaData();
        int cnt = 0;
        int cntP = 0;
        int cntA = 0;
        int cntAP = 0;
        int renglonInicio = 9;
        int renglonInicioP = 15;
        int renglon = 9;
        int renglonP = 15;
        String periodo = "";
        periodo = "MES DE: " + Util.NOMBRE_MESES_MX[mes - 1];
        Row rwEnc = (sheet0.getRow(1) == null ? sheet0.createRow(1) : sheet0.getRow(1));
        Cell cell = (rwEnc.getCell(0) == null ? rwEnc.createCell(0) : rwEnc.getCell(0));
        cell.setCellValue(unidadResponsable);
        Row rwEnc2 = (sheet0.getRow(3) == null ? sheet0.createRow(3) : sheet0.getRow(3));
        Cell cell2 = (rwEnc2.getCell(0) == null ? rwEnc2.createCell(0) : rwEnc2.getCell(0));
        cell2.setCellValue(periodo);
        while (rs4.next()) {
            int tipoPasivo = rs4.getInt(1);
            int columna = 2;
            int columnaP = 2;
            if (tipoPasivo == 0) {
                for (int i = 1; i < rs4Metadata.getColumnCount(); i++) {
                    int tipoDato = rs4Metadata.getColumnType(i + 1);
                    if (tipoDato == 3) {
                        double valorD = rs4.getDouble(rs4Metadata.getColumnName(i + 1));
                        Row rwEnc3 = (sheet0.getRow(renglon + cntA) == null ? sheet0.createRow(renglon + cntA) : sheet0.getRow(renglon + cntA));
                        Cell cell3 = (rwEnc3.getCell(columna) == null ? rwEnc3.createCell(columna) : rwEnc3.getCell(columna));
                        cell3.setCellValue(valorD);
                        columna++;
                    } else {
                        String valorS = rs4.getString(rs4Metadata.getColumnName(i + 1));
                        Row rwEnc3 = (sheet0.getRow(renglon + cntA) == null ? sheet0.createRow(renglon + cntA) : sheet0.getRow(renglon + cntA));
                        Cell cell3 = (rwEnc3.getCell(columna) == null ? rwEnc3.createCell(columna) : rwEnc3.getCell(columna));
                        cell3.setCellValue(valorS);
                        columna++;
                    }
                }
                cntA++;
            } else if (tipoPasivo == 1) {
                for (int i = 1; i < rs4Metadata.getColumnCount(); i++) {
                    int tipoDato = rs4Metadata.getColumnType(i + 1);
                    if (tipoDato == 3) {
                        double valorD = rs4.getDouble(rs4Metadata.getColumnName(i + 1));
                        Row rwEnc3 = (sheet0.getRow(renglonP + cntAP) == null ? sheet0.createRow(renglonP + cntAP) : sheet0.getRow(renglonP + cntAP));
                        Cell cell3 = (rwEnc3.getCell(columnaP) == null ? rwEnc3.createCell(columnaP) : rwEnc3.getCell(columnaP));
                        cell3.setCellValue(valorD);
                        columnaP++;
                    } else {
                        String valorS = rs4.getString(rs4Metadata.getColumnName(i + 1));
                        Row rwEnc3 = (sheet0.getRow(renglonP + cntAP) == null ? sheet0.createRow(renglonP + cntAP) : sheet0.getRow(renglonP + cntAP));
                        Cell cell3 = (rwEnc3.getCell(columnaP) == null ? rwEnc3.createCell(columnaP) : rwEnc3.getCell(columnaP));
                        cell3.setCellValue(valorS);
                        columnaP++;
                    }
                }
                cntAP++;
            }
        }
        while (rs.next()) {
            int tipo = rs.getInt(1);
            int columnaInicio = 4;
            int columnaInicioP = 4;
            if (tipo == 0) {
                for (int i = 1; i < rsMetadata.getColumnCount(); i++) {
                    int tipoDato = rsMetadata.getColumnType(i + 1);
                    if (tipoDato == 3) {
                        double valorD = rs.getDouble(rsMetadata.getColumnName(i + 1));
                        Row rwEnc3 = (sheet0.getRow(renglonInicio + cnt) == null ? sheet0.createRow(renglonInicio + cnt) : sheet0.getRow(renglonInicio + cnt));
                        Cell cell3 = (rwEnc3.getCell(columnaInicio) == null ? rwEnc3.createCell(columnaInicio) : rwEnc3.getCell(columnaInicio));
                        cell3.setCellValue(valorD);
                        columnaInicio++;
                    } else {
                        String valorS = rs.getString(rsMetadata.getColumnName(i + 1));
                        Row rwEnc3 = (sheet0.getRow(renglonInicio + cnt) == null ? sheet0.createRow(renglonInicio + cnt) : sheet0.getRow(renglonInicio + cnt));
                        Cell cell3 = (rwEnc3.getCell(columnaInicio) == null ? rwEnc3.createCell(columnaInicio) : rwEnc3.getCell(columnaInicio));
                        cell3.setCellValue(valorS);
                        columnaInicio++;
                    }
                }
                cnt++;
            } else if (tipo == 1) {
                for (int i = 1; i < rsMetadata.getColumnCount(); i++) {
                    int tipoDato = rsMetadata.getColumnType(i + 1);
                    if (tipoDato == 3) {
                        double valorD = rs.getDouble(rsMetadata.getColumnName(i + 1));
                        Row rwEnc3 = (sheet0.getRow(renglonInicioP + cntP) == null ? sheet0.createRow(renglonInicioP + cntP) : sheet0.getRow(renglonInicioP + cntP));
                        Cell cell3 = (rwEnc3.getCell(columnaInicioP) == null ? rwEnc3.createCell(columnaInicioP) : rwEnc3.getCell(columnaInicioP));
                        cell3.setCellValue(valorD);
                        columnaInicioP++;
                    } else {
                        String valorS = rs.getString(rsMetadata.getColumnName(i + 1));
                        Row rwEnc3 = (sheet0.getRow(renglonInicioP + cntP) == null ? sheet0.createRow(renglonInicioP + cntP) : sheet0.getRow(renglonInicioP + cntP));
                        Cell cell3 = (rwEnc3.getCell(columnaInicioP) == null ? rwEnc3.createCell(columnaInicioP) : rwEnc3.getCell(columnaInicioP));
                        cell3.setCellValue(valorS);
                        columnaInicioP++;
                    }
                }
                cntP++;
            }
        }
        Row rwEnc4 = (sheet0.getRow(23) == null ? sheet0.createRow(23) : sheet0.getRow(23));
        Cell cell4 = (rwEnc4.getCell(2) == null ? rwEnc4.createCell(2) : rwEnc4.getCell(2));
        cell4.setCellValue(cAccionesT);
        Row rwEnc5 = (sheet0.getRow(30) == null ? sheet0.createRow(30) : sheet0.getRow(30));
        Cell cell5 = (rwEnc5.getCell(2) == null ? rwEnc5.createCell(2) : rwEnc5.getCell(2));
        cell5.setCellValue(cNotaT);
        Row rwEnc6 = (sheet0.getRow(6) == null ? sheet0.createRow(6) : sheet0.getRow(6));
        Cell cell6 = (rwEnc6.getCell(4) == null ? rwEnc6.createCell(4) : rwEnc6.getCell(4));
        cell6.setCellValue(ejFiscal);
        Row rwEnc7 = (sheet0.getRow(6) == null ? sheet0.createRow(6) : sheet0.getRow(6));
        Cell cell7 = (rwEnc7.getCell(2) == null ? rwEnc7.createCell(2) : rwEnc7.getCell(2));
        cell7.setCellValue(ejFiscal - 1);
        sheet0 = Util.EvaluaFormula(workbook, sheet0, 13, 14, 3, 10);
        sheet0 = Util.EvaluaFormula(workbook, sheet0, 19, 21, 3, 10);
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

    public static String obtenSubcuentaPC(Connection conn) throws SQLException {
        String subcuenta = "";
        ResultSet rs = null;
        PreparedStatement pstm = null;
        try {
            pstm = conn.prepareStatement("SELECT 'PC' + RIGHT('00000' + CAST(MAX(CAST(SUBSTRING(cSubcuenta, 3, LEN(cSubcuenta)) AS INT)) + 1 AS VARCHAR(20)), 5) AS cSubcuenta FROM tpasivosContingentes WITH (NOLOCK)");
            rs = pstm.executeQuery();
            if (rs.next()) {
                subcuenta = rs.getString(1);
            }
        } finally {
            if (pstm != null)
                pstm.close();
            pstm = null;
            if (rs != null)
                rs.close();
            rs = null;
        }
        return subcuenta;
    }

    public static double validarSaldo(Connection conn, String RFC, String PC, int mes, String tipoPasivo) throws Exception {
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        double nExiste = 0;
        String query = "";
        if ("L".equals(tipoPasivo)) {
            query = "SELECT CASE ? " + "	WHEN 1 THEN SUM(mSaldo1) " + "	WHEN 2 THEN SUM(mSaldo2) " + "	WHEN 3 THEN SUM(mSaldo3) " + "	WHEN 4 THEN SUM(mSaldo4) " + "	WHEN 5 THEN SUM(mSaldo5) " + "	WHEN 6 THEN SUM(mSaldo6) " + "	WHEN 7 THEN SUM(mSaldo7) " + "	WHEN 8 THEN SUM(mSaldo8) " + "	WHEN 9 THEN SUM(mSaldo9) " + "	WHEN 10 THEN SUM(mSaldo10) " + "	WHEN 11 THEN SUM(mSaldo11) " + "	WHEN 12 THEN SUM(mSaldo12) " + "	END AS saldo " + " FROM tSaldos " + " WHERE nCuenta like '71101%' " + "	AND cSubCuenta = ? ";
        } else if ("P".equals(tipoPasivo)) {
            query = "SELECT CASE ? " + "	WHEN 1 THEN SUM(mSaldo1) " + "	WHEN 2 THEN SUM(mSaldo2) " + "	WHEN 3 THEN SUM(mSaldo3) " + "	WHEN 4 THEN SUM(mSaldo4) " + "	WHEN 5 THEN SUM(mSaldo5) " + "	WHEN 6 THEN SUM(mSaldo6) " + "	WHEN 7 THEN SUM(mSaldo7) " + "	WHEN 8 THEN SUM(mSaldo8) " + "	WHEN 9 THEN SUM(mSaldo9) " + "	WHEN 10 THEN SUM(mSaldo10) " + "	WHEN 11 THEN SUM(mSaldo11) " + "	WHEN 12 THEN SUM(mSaldo12) " + "	END AS saldo " + " FROM tSaldos " + " WHERE nCuenta like '71102%' " + "	AND cSubCuenta = ? ";
        }
        try {
            pstmnt = conn.prepareStatement(query);
            pstmnt.setInt(1, mes);
            pstmnt.setString(2, PC);
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                nExiste = rs.getInt(1);
            }
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
            if (rs != null)
                rs.close();
            rs = null;
            /*CloseObject.closeObject(pstmnt, false);
			CloseObject.closeObject(rs, false);*/
        }
        return nExiste;
    }

    public static String PasivosContingentesAntiguedadExpediente(Connection conn, String mes, String anio, Map<String, String> plantillas, String unidad) throws Exception {
        log.info("Object: {}", String.format("Iniciando cedulas de pasivos contigentes patrimoniales"));
        CallableStatement cs = null, cs2 = null;
        ResultSet rs = null, rs2 = null;
        String query = "{call sp_cedulaPasivosContingentesAntiguedadExpediente( ? )}";
        String query2 = "{call sp_cedulaPasivosContingentesExpediente( ? )}";
        String fileName = "";
        String fecha = null;
        int mesS = Integer.parseInt(mes);
        int ejFiscal = Integer.parseInt(anio);
        int ultimoDiaMes = obtenerUltimoDiaMes(ejFiscal, mesS);
        fecha = ultimoDiaMes + "/" + mes + "/" + ejFiscal;
        try {
            cs = conn.prepareCall(query);
            cs.setString(1, fecha);
            log.debug("Object: {}", String.format("Ejecutando[" + query + "]%S", fecha));
            rs = cs.executeQuery();
            cs2 = conn.prepareCall(query2);
            cs2.setString(1, fecha);
            log.debug("Object: {}", String.format("Ejecutando[" + query2 + "]%S", fecha));
            rs2 = cs2.executeQuery();
            fileName = generaReporteAntiguedadExpediente(rs, rs2, plantillas.get("CEDULAANTIGUEDADEXPEDIENTE"), mesS, anio);
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(cs, false);
        }
    }

    private static String generaReporteAntiguedadExpediente(ResultSet rs, ResultSet rs2, String plantillaPath, int mes, String anio) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "CedulaPasivosContingentesAntiguedad" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        Sheet sheet1 = workbook.getSheetAt(1);
        int cnt = 0;
        ResultSetMetaData rsMetadata = rs.getMetaData();
        ResultSetMetaData rs2Metadata = rs2.getMetaData();
        int renglonInicio = 8;
        int renglon = 8;
        int renglonP = 14;
        String periodo = "";
        int cntA = 0;
        int cntAP = 0;
        periodo = "MES DE: " + Util.NOMBRE_MESES_MX[mes - 1];
        Row rwEnc2 = (sheet0.getRow(2) == null ? sheet0.createRow(2) : sheet0.getRow(2));
        Cell cell2 = (rwEnc2.getCell(0) == null ? rwEnc2.createCell(0) : rwEnc2.getCell(0));
        cell2.setCellValue(periodo);
        CellStyle estiloTabla = workbook.createCellStyle();
        estiloTabla.setBorderRight(BorderStyle.HAIR);
        estiloTabla.setBorderLeft(BorderStyle.HAIR);
        estiloTabla.setBorderTop(BorderStyle.HAIR);
        estiloTabla.setBorderBottom(BorderStyle.HAIR);
        while (rs.next()) {
            Row rw = (sheet0.getRow(renglonInicio + cnt) == null ? sheet0.createRow(renglonInicio + cnt) : sheet0.getRow(renglonInicio + cnt));
            for (int i = 0; i < rsMetadata.getColumnCount(); i++) {
                Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
            }
            cnt++;
        }
        Row rwEnc3 = (sheet1.getRow(3) == null ? sheet1.createRow(3) : sheet1.getRow(3));
        Cell cell3 = (rwEnc3.getCell(2) == null ? rwEnc3.createCell(2) : rwEnc3.getCell(2));
        cell3.setCellValue(periodo);
        while (rs2.next()) {
            int tipoPasivo = rs2.getInt(1);
            int columna = 2;
            int columnaP = 2;
            if (tipoPasivo == 0) {
                for (int i = 1; i < rs2Metadata.getColumnCount(); i++) {
                    int tipoDato = rs2Metadata.getColumnType(i + 1);
                    if (tipoDato == 3) {
                        double valorD = rs2.getDouble(rs2Metadata.getColumnName(i + 1));
                        Row rwEnc4 = (sheet1.getRow(renglon + cntA) == null ? sheet1.createRow(renglon + cntA) : sheet1.getRow(renglon + cntA));
                        Cell cell4 = (rwEnc4.getCell(columna) == null ? rwEnc4.createCell(columna) : rwEnc4.getCell(columna));
                        cell4.setCellValue(valorD);
                        columna++;
                    } else {
                        String valorS = rs2.getString(rs2Metadata.getColumnName(i + 1));
                        Row rwEnc4 = (sheet1.getRow(renglon + cntA) == null ? sheet1.createRow(renglon + cntA) : sheet1.getRow(renglon + cntA));
                        Cell cell4 = (rwEnc4.getCell(columna) == null ? rwEnc4.createCell(columna) : rwEnc4.getCell(columna));
                        cell4.setCellValue(valorS);
                        columna++;
                    }
                }
                cntA++;
            } else if (tipoPasivo == 1) {
                for (int i = 1; i < rs2Metadata.getColumnCount(); i++) {
                    int tipoDato = rs2Metadata.getColumnType(i + 1);
                    if (tipoDato == 3) {
                        double valorD = rs2.getDouble(rs2Metadata.getColumnName(i + 1));
                        Row rwEnc4 = (sheet1.getRow(renglonP + cntAP) == null ? sheet1.createRow(renglonP + cntAP) : sheet1.getRow(renglonP + cntAP));
                        Cell cell4 = (rwEnc4.getCell(columnaP) == null ? rwEnc4.createCell(columnaP) : rwEnc4.getCell(columnaP));
                        cell4.setCellValue(valorD);
                        columnaP++;
                    } else {
                        String valorS = rs2.getString(rs2Metadata.getColumnName(i + 1));
                        Row rwEnc4 = (sheet1.getRow(renglonP + cntAP) == null ? sheet1.createRow(renglonP + cntAP) : sheet1.getRow(renglonP + cntAP));
                        Cell cell4 = (rwEnc4.getCell(columnaP) == null ? rwEnc4.createCell(columnaP) : rwEnc4.getCell(columnaP));
                        cell4.setCellValue(valorS);
                        columnaP++;
                    }
                }
                cntAP++;
            }
        }
        sheet0 = Util.EvaluaFormula(workbook, sheet1, 3, 18, 3, 8);
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

    public static String PasivosContingentesAntiguedadExpedienteConsolidado(Connection conn, String mes, String anio, Map<String, String> plantillas, String unidad) throws Exception {
        log.info("Object: {}", String.format("Iniciando cedulas de pasivos contigentes patrimoniales"));
        CallableStatement cs = null, cs2 = null;
        ResultSet rs = null, rs2 = null;
        String query = "{call sp_cedulaPasivosContingentesAntiguedadExpedienteConsolidado( ? )}";
        String query2 = "{call sp_cedulaPasivosContingentesExpedienteConsolidado( ? )}";
        String fileName = "";
        String fecha = null;
        int mesS = Integer.parseInt(mes);
        int ejFiscal = Integer.parseInt(anio);
        int ultimoDiaMes = obtenerUltimoDiaMes(ejFiscal, mesS);
        fecha = ultimoDiaMes + "/" + mes + "/" + ejFiscal;
        try {
            cs = conn.prepareCall(query);
            cs.setString(1, fecha);
            log.debug("Object: {}", String.format("Ejecutando[" + query + "]%S", fecha));
            rs = cs.executeQuery();
            cs2 = conn.prepareCall(query2);
            cs2.setString(1, fecha);
            log.debug("Object: {}", String.format("Ejecutando[" + query2 + "]%S", fecha));
            rs2 = cs2.executeQuery();
            fileName = generaReporteAntiguedadExpedienteConsolidado(rs, rs2, plantillas.get("CEDULAANTIGUEDADEXPEDIENTECONSOLIDADO"), mesS, anio);
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(cs, false);
        }
    }

    private static String generaReporteAntiguedadExpedienteConsolidado(ResultSet rs, ResultSet rs2, String plantillaPath, int mes, String anio) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "CedulaPasivosContingentesAntiguedad" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        Sheet sheet1 = workbook.getSheetAt(1);
        int cnt = 0;
        ResultSetMetaData rsMetadata = rs.getMetaData();
        ResultSetMetaData rs2Metadata = rs2.getMetaData();
        int renglonInicio = 8;
        int renglon = 8;
        int renglonP = 14;
        String periodo = "";
        int cntA = 0;
        int cntAP = 0;
        periodo = "MES DE: " + Util.NOMBRE_MESES_MX[mes - 1];
        Row rwEnc2 = (sheet0.getRow(2) == null ? sheet0.createRow(2) : sheet0.getRow(2));
        Cell cell2 = (rwEnc2.getCell(0) == null ? rwEnc2.createCell(0) : rwEnc2.getCell(0));
        cell2.setCellValue(periodo);
        CellStyle estiloTabla = workbook.createCellStyle();
        estiloTabla.setBorderRight(BorderStyle.HAIR);
        estiloTabla.setBorderLeft(BorderStyle.HAIR);
        estiloTabla.setBorderTop(BorderStyle.HAIR);
        estiloTabla.setBorderBottom(BorderStyle.HAIR);
        while (rs.next()) {
            Row rw = (sheet0.getRow(renglonInicio + cnt) == null ? sheet0.createRow(renglonInicio + cnt) : sheet0.getRow(renglonInicio + cnt));
            for (int i = 0; i < rsMetadata.getColumnCount(); i++) {
                Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
            }
            cnt++;
        }
        Row rwEnc3 = (sheet1.getRow(3) == null ? sheet1.createRow(3) : sheet1.getRow(3));
        Cell cell3 = (rwEnc3.getCell(2) == null ? rwEnc3.createCell(2) : rwEnc3.getCell(2));
        cell3.setCellValue(periodo);
        while (rs2.next()) {
            int tipoPasivo = rs2.getInt(1);
            int columna = 2;
            int columnaP = 2;
            if (tipoPasivo == 0) {
                for (int i = 1; i < rs2Metadata.getColumnCount(); i++) {
                    int tipoDato = rs2Metadata.getColumnType(i + 1);
                    if (tipoDato == 3) {
                        double valorD = rs2.getDouble(rs2Metadata.getColumnName(i + 1));
                        Row rwEnc4 = (sheet1.getRow(renglon + cntA) == null ? sheet1.createRow(renglon + cntA) : sheet1.getRow(renglon + cntA));
                        Cell cell4 = (rwEnc4.getCell(columna) == null ? rwEnc4.createCell(columna) : rwEnc4.getCell(columna));
                        cell4.setCellValue(valorD);
                        columna++;
                    } else {
                        String valorS = rs2.getString(rs2Metadata.getColumnName(i + 1));
                        Row rwEnc4 = (sheet1.getRow(renglon + cntA) == null ? sheet1.createRow(renglon + cntA) : sheet1.getRow(renglon + cntA));
                        Cell cell4 = (rwEnc4.getCell(columna) == null ? rwEnc4.createCell(columna) : rwEnc4.getCell(columna));
                        cell4.setCellValue(valorS);
                        columna++;
                    }
                }
                cntA++;
            } else if (tipoPasivo == 1) {
                for (int i = 1; i < rs2Metadata.getColumnCount(); i++) {
                    int tipoDato = rs2Metadata.getColumnType(i + 1);
                    if (tipoDato == 3) {
                        double valorD = rs2.getDouble(rs2Metadata.getColumnName(i + 1));
                        Row rwEnc4 = (sheet1.getRow(renglonP + cntAP) == null ? sheet1.createRow(renglonP + cntAP) : sheet1.getRow(renglonP + cntAP));
                        Cell cell4 = (rwEnc4.getCell(columnaP) == null ? rwEnc4.createCell(columnaP) : rwEnc4.getCell(columnaP));
                        cell4.setCellValue(valorD);
                        columnaP++;
                    } else {
                        String valorS = rs2.getString(rs2Metadata.getColumnName(i + 1));
                        Row rwEnc4 = (sheet1.getRow(renglonP + cntAP) == null ? sheet1.createRow(renglonP + cntAP) : sheet1.getRow(renglonP + cntAP));
                        Cell cell4 = (rwEnc4.getCell(columnaP) == null ? rwEnc4.createCell(columnaP) : rwEnc4.getCell(columnaP));
                        cell4.setCellValue(valorS);
                        columnaP++;
                    }
                }
                cntAP++;
            }
        }
        sheet0 = Util.EvaluaFormula(workbook, sheet1, 3, 18, 3, 8);
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

    public static String esPasivoContingente(Connection conn, int folio) throws Exception {
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        String esPasivo = null;
        String query = "SELECT DISTINCT CASE WHEN cPasivo IS NULL THEN 'NO' ELSE 'SI' END AS esPasivoContingente\r\n" + " FROM tDocPolizaDetalle DET\r\n" + " LEFT JOIN tpasivosContingentes PC ON DET.cPasivo = cSubcuenta\r\n" + " WHERE nFolioDocPoliza = ?";
        try {
            pstmnt = conn.prepareStatement(query);
            pstmnt.setInt(1, folio);
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                esPasivo = rs.getString(1);
            }
            return esPasivo;
        } finally {
            CloseObject.closeObject(pstmnt);
            CloseObject.closeObject(rs);
        }
    }

    public static void cancelaBajaPasivo(Connection conn, int folio) throws Exception {
        PreparedStatement pstmnt = null, pstmnt2 = null;
        ResultSet rs = null;
        String PC = null;
        String query = "SELECT cPasivo FROM tDocPolizaDetalle WHERE nFolioDocPoliza = ?";
        String queryUpdate = "UPDATE tpasivosContingentes SET Estatus = NULL, cFechaBaja = NULL WHERE cSubcuenta = ?";
        try {
            pstmnt = conn.prepareStatement(query);
            pstmnt.setInt(1, folio);
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                PC = rs.getString(1);
            }
            pstmnt2 = conn.prepareStatement(queryUpdate);
            pstmnt2.setString(1, PC);
            pstmnt2.execute();
        } finally {
            CloseObject.closeObject(pstmnt);
            CloseObject.closeObject(pstmnt2);
            CloseObject.closeObject(rs);
        }
    }
}
