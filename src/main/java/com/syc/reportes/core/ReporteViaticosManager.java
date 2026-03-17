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
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import java.util.Base64;
import java.nio.file.Paths;

public class ReporteViaticosManager {

    public static String generaReporteViaticosManager(Connection conn, int mesIni, int mesFin, Map<String, String> plantillas) throws Exception {
        CallableStatement cs = null;
        ResultSet rs = null;
        String query = "{call sp_reporteViaticos( ? , ?)}";
        String fileName = "";
        try {
            cs = conn.prepareCall(query);
            cs.setInt(1, mesIni);
            cs.setInt(2, mesFin);
            rs = cs.executeQuery();
            fileName = generaReporteViaticos(rs, plantillas.get("REPORTEVIAT"), mesIni, mesFin);
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(cs, false);
        }
    }

    private static String generaReporteViaticos(ResultSet rs, String plantillaPath, int mesIni, int mesFin) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "REPORTE" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        int cnt1 = 0;
        ResultSetMetaData rsMetadata = rs.getMetaData();
        int renglonInicio = 7;
        CellStyle estiloTabla = workbook.createCellStyle();
        estiloTabla.setBorderRight(BorderStyle.HAIR);
        estiloTabla.setBorderLeft(BorderStyle.HAIR);
        estiloTabla.setBorderTop(BorderStyle.HAIR);
        estiloTabla.setBorderBottom(BorderStyle.HAIR);
        DataFormat df = workbook.createDataFormat();
        CellStyle estiloFecha = workbook.createCellStyle();
        estiloFecha.setBorderRight(BorderStyle.HAIR);
        estiloFecha.setBorderLeft(BorderStyle.HAIR);
        estiloFecha.setBorderTop(BorderStyle.HAIR);
        estiloFecha.setBorderBottom(BorderStyle.HAIR);
        estiloFecha.setDataFormat(df.getFormat("dd/MM/yyyy"));
        while (rs.next()) {
            Row rw = (sheet0.getRow(renglonInicio + cnt1) == null ? sheet0.createRow(renglonInicio + cnt1) : sheet0.getRow(renglonInicio + cnt1));
            for (int i = 0; i < rsMetadata.getColumnCount(); i++) {
                if (rsMetadata.getColumnName(i + 1).equals("dSalida") || rsMetadata.getColumnName(i + 1).equals("dRegreso") || rsMetadata.getColumnName(i + 1).equals("dFechaInforme") || rsMetadata.getColumnName(i + 1).equals("dFechaValidacion") || rsMetadata.getColumnName(i + 1).equals("dFechaActualizacion")) {
                    Util.createExcelDateCell(i, (HSSFRow) rw, rs.getString(rsMetadata.getColumnName(i + 1)), estiloFecha);
                } else {
                    Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
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

    public static String generaReporteGastosManager(Connection conn, int mesIni, int mesFin, Map<String, String> plantillas) throws Exception {
        CallableStatement cs = null;
        ResultSet rs = null;
        String query = "{call sp_reporteGastosRep( ? ,?)}";
        String fileName = "";
        try {
            cs = conn.prepareCall(query);
            cs.setInt(1, mesIni);
            cs.setInt(2, mesFin);
            rs = cs.executeQuery();
            fileName = generaReporteViaticos(rs, plantillas.get("REPORTEGASTOS"), mesIni, mesFin);
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(cs, false);
        }
    }

    public static String generaReporteViaticosyGastosManager(Connection conn, int mesIni, int mesFin, Map<String, String> plantillas, int anio) throws Exception {
        CallableStatement cs = null, cs2 = null;
        ResultSet rs = null, rs2 = null;
        String query = "{call sp_reporteViaticosyGastos( ? ,?, ?)}";
        String query2 = "{call sp_reporteViaticosyGastosXPartida( ? ,?)}";
        String fileName = "";
        try {
            cs = conn.prepareCall(query);
            cs.setInt(1, mesIni);
            cs.setInt(2, mesFin);
            cs.setInt(3, anio);
            cs2 = conn.prepareCall(query2);
            cs2.setInt(1, mesIni);
            cs2.setInt(2, mesFin);
            rs = cs.executeQuery();
            rs2 = cs2.executeQuery();
            fileName = generaReporteViaticos2(rs, rs2, plantillas.get("REPORTEVIATICOSYGASTOS"), mesIni, mesFin);
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(cs, false);
            CloseObject.closeObject(rs2, false);
            CloseObject.closeObject(cs2, false);
        }
    }

    private static String generaReporteViaticos2(ResultSet rs, ResultSet rs2, String plantillaPath, int mesIni, int mesFin) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "REPORTE" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        Sheet sheet1 = workbook.getSheetAt(1);
        int cnt1 = 0;
        int cnt2 = 0;
        ResultSetMetaData rsMetadata = rs.getMetaData();
        ResultSetMetaData rs2Metadata = rs2.getMetaData();
        int renglonInicio = 7;
        int renglonInicio2 = 1;
        CellStyle estiloTabla = workbook.createCellStyle();
        estiloTabla.setBorderRight(BorderStyle.HAIR);
        estiloTabla.setBorderLeft(BorderStyle.HAIR);
        estiloTabla.setBorderTop(BorderStyle.HAIR);
        estiloTabla.setBorderBottom(BorderStyle.HAIR);
        DataFormat df = workbook.createDataFormat();
        CellStyle estiloFecha = workbook.createCellStyle();
        estiloFecha.setBorderRight(BorderStyle.HAIR);
        estiloFecha.setBorderLeft(BorderStyle.HAIR);
        estiloFecha.setBorderTop(BorderStyle.HAIR);
        estiloFecha.setBorderBottom(BorderStyle.HAIR);
        estiloFecha.setDataFormat(df.getFormat("dd/MM/yyyy"));
        while (rs.next()) {
            Row rw = (sheet0.getRow(renglonInicio + cnt1) == null ? sheet0.createRow(renglonInicio + cnt1) : sheet0.getRow(renglonInicio + cnt1));
            for (int i = 0; i < rsMetadata.getColumnCount(); i++) {
                if (rsMetadata.getColumnName(i + 1).equals("dSalida") || rsMetadata.getColumnName(i + 1).equals("dRegreso") || rsMetadata.getColumnName(i + 1).equals("dFechaInforme") || rsMetadata.getColumnName(i + 1).equals("dFechaValidacion") || rsMetadata.getColumnName(i + 1).equals("dFechaActualizacion")) {
                    Util.createExcelDateCell(i, (HSSFRow) rw, rs.getString(rsMetadata.getColumnName(i + 1)), estiloFecha);
                } else {
                    Util.createExcelCell(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1));
                }
            }
            cnt1++;
        }
        while (rs2.next()) {
            Row rw2 = (sheet1.getRow(renglonInicio2 + cnt2) == null ? sheet1.createRow(renglonInicio2 + cnt2) : sheet1.getRow(renglonInicio2 + cnt2));
            for (int i = 0; i < rs2Metadata.getColumnCount(); i++) {
                Util.createExcelCell(i, rw2, rs2, rs2Metadata.getColumnName(i + 1), rs2Metadata.getColumnType(i + 1));
            }
            cnt2++;
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

    public static String generaReporteViaticosyGastosManagerV2(Connection conn, int mesIni, int mesFin, Map<String, String> plantillas, int anio) throws Exception {
        CallableStatement cs = null, cs2 = null;
        ResultSet rs = null, rs2 = null;
        String query = "{call sp_reporteViaticosyGastos_V2( ? ,?, ?)}";
        String query2 = "{call sp_reporteViaticosyGastosXPartida_V2( ? ,?)}";
        String fileName = "";
        try {
            cs = conn.prepareCall(query);
            cs.setInt(1, mesIni);
            cs.setInt(2, mesFin);
            cs.setInt(3, anio);
            cs2 = conn.prepareCall(query2);
            cs2.setInt(1, mesIni);
            cs2.setInt(2, mesFin);
            rs = cs.executeQuery();
            rs2 = cs2.executeQuery();
            fileName = generaReporteViaticos2_V2(rs, rs2, plantillas.get("REPORTEVIATICOSYGASTOSV2"), mesIni, mesFin);
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(cs, false);
            CloseObject.closeObject(rs2, false);
            CloseObject.closeObject(cs2, false);
        }
    }

    private static String generaReporteViaticos2_V2(ResultSet rs, ResultSet rs2, String plantillaPath, int mesIni, int mesFin) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "REPORTE" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        Sheet sheet1 = workbook.getSheetAt(1);
        int cnt1 = 0;
        int cnt2 = 0;
        ResultSetMetaData rsMetadata = rs.getMetaData();
        ResultSetMetaData rs2Metadata = rs2.getMetaData();
        int renglonInicio = 7;
        int renglonInicio2 = 1;
        CellStyle estiloTabla = workbook.createCellStyle();
        estiloTabla.setBorderRight(BorderStyle.HAIR);
        estiloTabla.setBorderLeft(BorderStyle.HAIR);
        estiloTabla.setBorderTop(BorderStyle.HAIR);
        estiloTabla.setBorderBottom(BorderStyle.HAIR);
        DataFormat df = workbook.createDataFormat();
        CellStyle estiloFecha = workbook.createCellStyle();
        estiloFecha.setBorderRight(BorderStyle.HAIR);
        estiloFecha.setBorderLeft(BorderStyle.HAIR);
        estiloFecha.setBorderTop(BorderStyle.HAIR);
        estiloFecha.setBorderBottom(BorderStyle.HAIR);
        estiloFecha.setDataFormat(df.getFormat("dd/MM/yyyy"));
        while (rs.next()) {
            Row rw = (sheet0.getRow(renglonInicio + cnt1) == null ? sheet0.createRow(renglonInicio + cnt1) : sheet0.getRow(renglonInicio + cnt1));
            for (int i = 0; i < rsMetadata.getColumnCount(); i++) {
                if (rsMetadata.getColumnName(i + 1).equals("dSalida") || rsMetadata.getColumnName(i + 1).equals("dRegreso") || rsMetadata.getColumnName(i + 1).equals("dFechaInforme") || rsMetadata.getColumnName(i + 1).equals("dFechaValidacion") || rsMetadata.getColumnName(i + 1).equals("dFechaActualizacion")) {
                    Util.createExcelDateCell(i, (HSSFRow) rw, rs.getString(rsMetadata.getColumnName(i + 1)), estiloFecha);
                } else {
                    Util.createExcelCell(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1));
                }
            }
            cnt1++;
        }
        while (rs2.next()) {
            Row rw2 = (sheet1.getRow(renglonInicio2 + cnt2) == null ? sheet1.createRow(renglonInicio2 + cnt2) : sheet1.getRow(renglonInicio2 + cnt2));
            for (int i = 0; i < rs2Metadata.getColumnCount(); i++) {
                Util.createExcelCell(i, rw2, rs2, rs2Metadata.getColumnName(i + 1), rs2Metadata.getColumnType(i + 1));
            }
            cnt2++;
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

    public static String generaDeudoresViaticosManager(Connection conn, String folio, Map<String, String> plantillas) throws Exception {
        PreparedStatement pst = null;
        ResultSet rs = null;
        StringBuilder query = new StringBuilder();
        query.append("SELECT   cRFC, empleado.nombreCompleto, nidComisionModulo, empleado.cUR, empleado.cUejecutora ");
        query.append("	, FINICIO, FFIN ");
        query.append("	, SUM(anticipo) anticipo, SUM(devolucion ) devolucion ");
        query.append("	, CASE 	WHEN(ffin >= GETDATE()) THEN SUM(IMPORTE) ");
        query.append("		   WHEN  DAY(getdate() - FFIN  )  < 30 THEN SUM(IMPORTE)  ELSE 0 END de1a30  ");
        query.append("	, CASE WHEN  (ffin < GETDATE()) AND DAY(getdate() - FFIN ) between 31 and 90 THEN SUM(IMPORTE)  ELSE 0 END de31a90 ");
        query.append("	, CASE WHEN  (ffin < GETDATE()) AND DAY(getdate() - FFIN ) between 91 and 180 THEN SUM(IMPORTE)  ELSE 0 END de91a180 ");
        query.append("	, CASE WHEN  (ffin < GETDATE()) AND DAY(getdate() - FFIN ) between 181 and 365 THEN SUM(IMPORTE)  ELSE 0 END de181a365 ");
        query.append("	, CASE WHEN  (ffin < GETDATE()) AND DAY(getdate() - FFIN ) > 365 THEN SUM(IMPORTE)  ELSE 0 END mayor365 ");
        query.append("	, (SELECT TOP 1 cnombre FROM tFirmantesViaticos WITH (NOLOCK) WHERE nFolio = nidComisionModulo ORDER BY nRenglon DESC) cNombreAutorizador ");
        query.append(" FROM ( ");
        query.append("		SELECT CASE WHEN tipo = 'A'  ");
        query.append("			THEN sum(Anticipo) ELSE SUM(-Devolucion) END IMPORTE ");
        query.append("			, nidComisionModulo ");
        query.append("			, SUM(anticipo) anticipo ");
        query.append("			, SUM(devolucion) devolucion ");
        query.append("		FROM v_RelacionComision  ");
        query.append("		WHERE nidComisionModulo like '" + folio + "' AND estatus = 'Aplicado' ");
        query.append("		GROUP BY tipo, nidComisionModulo ");
        query.append(") AS DEUDORES ");
        query.append(" INNER JOIN tComision Comision WITH (NOLOCK) ");
        query.append(" ON Comision.nIdComision = nidComisionModulo ");
        query.append(" INNER JOIN v_Empleados empleado WITH (NOLOCK) ");
        query.append(" ON empleado.nEmpleado = Comision.nIdEmpleado ");
        query.append(" INNER JOIN v_AgendaDiasAcumulados dias WITH (NOLOCK) ");
        query.append(" ON dias.nIdComision = Comision.nIdComision ");
        query.append(" GROUP BY nidComisionModulo, cRFC, empleado.nombreCompleto, empleado.cUR ");
        query.append(" 	, empleado.cUejecutora, FINICIO, FFIN ");
        query.append(" HAVING SUM(Anticipo) > 0");
        String fileName = "";
        try {
            pst = conn.prepareStatement(query.toString());
            rs = pst.executeQuery();
            fileName = generaReporteDeudoresViaticos(rs, plantillas.get("REPORTEDEUDORES"));
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(pst, false);
        }
    }

    private static String generaReporteDeudoresViaticos(ResultSet rs, String plantillaPath) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "REPORTE_Deudores" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        int cnt1 = 0;
        ResultSetMetaData rsMetadata = rs.getMetaData();
        int renglonInicio = 7;
        CellStyle estiloMoney = Util.generaEstilo2(workbook, 9, false, true, true, true, true, true);
        CellStyle estiloTabla = Util.generaEstilo2(workbook, 9, false, true, true, true, true, false);
        while (rs.next()) {
            Row rw = (sheet0.getRow(renglonInicio + cnt1) == null ? sheet0.createRow(renglonInicio + cnt1) : sheet0.getRow(renglonInicio + cnt1));
            for (int i = 0; i < rsMetadata.getColumnCount(); i++) {
                if (i > 6) {
                    Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloMoney);
                } else {
                    Util.createExcelCellRep(i, rw, rs.getString(rsMetadata.getColumnName(i + 1)), estiloTabla);
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

    public static String generaDiasAcumuladosManager(Connection conn, String UR, Map<String, String> plantillas) throws Exception {
        CallableStatement cll = null;
        PreparedStatement pst = null;
        ResultSet rs = null, rs2 = null;
        revisionDatosViaticos(conn);
        StringBuilder query = new StringBuilder();
        query.append("SELECT nidempleado, nidcomision, crfc, nombrecompleto, cur, cargo, dias_comision ");
        query.append(" , mPasaje, mTaxi, mPeaje, mHotel, mConsumos, mOtros, mTotal ");
        query.append(" , mPasajeLocal, mTaxiLocal, mGasolinaLocal, mPeajeLocal, mMaritimoLocal , maereolocal, mtotallocal ");
        query.append(" , cNombreAutorizador, ");
        query.append(" ISNULL(( SELECT ISNULL(SUM(MIMPORTEBOLETO),0) FROM tInfoBoleto WITH (NOLOCK) WHERE nFolioRelacionGastos IN (");
        query.append(" SELECT nFolioTramite FROM tRelacionComprobacionComisiones COMP WITH (NOLOCK)  ");
        query.append(" INNER JOIN tRELACIONGASTOSEncabezado RG WITH (NOLOCK) ON RG.nFolioRELACIONGASTOS = COMP.nFolioTramite ");
        query.append(" WHERE cTipoTramite = 'RELACIONGASTOS' AND nidComisionModulo =detalle.nidcomision AND RG.cDocumentoHaplicado = 'S'  )  ),0) + ");
        query.append(" ISNULL((SELECT ISNULL(SUM(MIMPORTEBOLETO),0) FROM tComisionesSinComprobacionDet WITH (NOLOCK) ");
        query.append(" WHERE tComisionesSinComprobacionDet.nFolioComision in  ");
        query.append(" (SELECT nFolioTramite FROM tRelacionComprobacionComisiones WITH (NOLOCK) WHERE cTipoTramite = 'COMSINVIATICOS' AND nidComisionModulo =detalle.nidcomision  )) ,0) importeBoleto ");
        query.append(" FROM v_AgendaDiasAcumuladosDetalle detalle");
        query.append(" WHERE cUejecutora LIKE '" + UR + "'");
        query.append(" ORDER BY cUejecutora ");
        String fileName = "";
        try {
            pst = conn.prepareStatement(query.toString());
            rs = pst.executeQuery();
            cll = conn.prepareCall("{call sp_reporteDiasAcumulados ( ? )}");
            cll.setString(1, UR);
            rs2 = cll.executeQuery();
            fileName = generaReporteDiasACC(rs, rs2, plantillas.get("REPORTEDIASACUM"));
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(pst, false);
        }
    }

    private static String generaReporteDiasACC(ResultSet rs, ResultSet rs2, String plantillaPath) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "REPORTE_DIAS" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        Sheet sheet1 = workbook.getSheetAt(1);
        int cnt1 = 0;
        ResultSetMetaData rsMetadata = rs.getMetaData();
        ResultSetMetaData rsMetadata2 = rs2.getMetaData();
        int renglonInicio = 7;
        CellStyle estiloTabla = Util.generaEstilo2(workbook, 10, false, true, true, true, true, false);
        while (rs.next()) {
            Row rw = (sheet0.getRow(renglonInicio + cnt1) == null ? sheet0.createRow(renglonInicio + cnt1) : sheet0.getRow(renglonInicio + cnt1));
            for (int i = 0; i < rsMetadata.getColumnCount(); i++) {
                Util.createExcelCellRep(i, rw, rs.getString(rsMetadata.getColumnName(i + 1)), estiloTabla);
            }
            cnt1++;
        }
        cnt1 = 0;
        while (rs2.next()) {
            Row rw = (sheet1.getRow(renglonInicio + cnt1) == null ? sheet1.createRow(renglonInicio + cnt1) : sheet1.getRow(renglonInicio + cnt1));
            for (int i = 0; i < rsMetadata2.getColumnCount(); i++) {
                Util.createExcelCellRep(i, rw, rs2.getString(rsMetadata2.getColumnName(i + 1)), estiloTabla);
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

    public static String generaSaldoVencimientoManager(Connection conn, String UR, Map<String, String> plantillas) throws Exception {
        PreparedStatement pst = null;
        ResultSet rs = null;
        StringBuilder query = new StringBuilder();
        query.append("SELECT CRFC, nombreCompleto, nidcomisionModulo, cNombreComision, cUR, cuEjecutora, cDocHaplicado, fInicio, fFin, fechaVencimiento, diasComision ");
        query.append(" , anticipo , devolucion, comprobacion, reintegro, saldo, maxFaplicacion, diasRetraso ");
        query.append("	, (SELECT TOP 1 cnombre FROM tFirmantesViaticos WITH (NOLOCK) WHERE nFolio = nidComisionModulo ORDER BY nRenglon DESC) cNombreAutorizador ");
        query.append(" FROM v_saldosViaticos ");
        query.append("	WHERE cUejecutora LIKE '" + UR + "'");
        String fileName = "";
        try {
            pst = conn.prepareStatement(query.toString());
            rs = pst.executeQuery();
            fileName = generaReporteSaldoViaticos(rs, plantillas.get("REPORTESALDOVENC"));
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(pst, false);
        }
    }

    private static String generaReporteSaldoViaticos(ResultSet rs, String plantillaPath) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "REPORTE_SALDOS" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        int cnt1 = 0;
        ResultSetMetaData rsMetadata = rs.getMetaData();
        int renglonInicio = 6;
        CellStyle estiloTabla = Util.generaEstilo2(workbook, 9, false, true, true, true, true, false);
        CellStyle estiloMoney = Util.generaEstilo2(workbook, 9, false, true, true, true, true, true);
        while (rs.next()) {
            Row rw = (sheet0.getRow(renglonInicio + cnt1) == null ? sheet0.createRow(renglonInicio + cnt1) : sheet0.getRow(renglonInicio + cnt1));
            for (int i = 0; i < rsMetadata.getColumnCount(); i++) {
                if (i > 10 && i < 16) {
                    Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloMoney);
                } else {
                    Util.createExcelCellRep(i, rw, rs.getString(rsMetadata.getColumnName(i + 1)), estiloTabla);
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

    public static String generaGastosViaticosManager(Connection conn, String UR, Map<String, String> plantillas) throws Exception {
        PreparedStatement pst = null;
        ResultSet rs = null;
        StringBuilder query = new StringBuilder();
        query.append(" SELECT nIdComision, cRFC, nombreCompleto, cNombreComision, cUejecutora, FINICIO, ffin, DiasViaticos ");
        query.append(" , nFolioRelacion, cTipoPago, cEvento, mPasaje, mTaxi, mPeaje, mHotel, mConsumos, mOtros, mTotal ");
        query.append(" , mPasajeLocal, mTaxiLocal, mGasolinaLocal, mPeajeLocal, mMaritimoLocal, mAereoLocal,  totalLocal  ");
        query.append("	, (SELECT TOP 1 cnombre FROM tFirmantesViaticos WITH (NOLOCK) WHERE nFolio = nidComision ORDER BY nRenglon DESC) cNombreAutorizador ");
        query.append(" , CASE WHEN cTipoPago = 'RELACIONGASTOS' THEN ( SELECT ISNULL(SUM(MIMPORTEBOLETO),0) FROM tInfoBoleto WITH (NOLOCK) WHERE nFolioRelacionGastos = nFolioRelacion ) ");
        query.append(" WHEN cTipoPago = 'COMSINVIATICOS'THEN (SELECT ISNULL(SUM(MIMPORTEBOLETO),0) FROM tComisionesSinComprobacionDet WITH (NOLOCK) WHERE tComisionesSinComprobacionDet.nFolioComision = NFOLIORELACION) ");
        query.append(" ELSE 0	END importeBoleto ");
        query.append(" FROM v_gastosViaticos ");
        query.append("	WHERE cUejecutora LIKE '" + UR + "' AND cDocHaplicado in ( 'S', 'F') ");
        String fileName = "";
        try {
            pst = conn.prepareStatement(query.toString());
            rs = pst.executeQuery();
            fileName = generaReporteGastosViaticos(rs, plantillas.get("REPORTEGASTORUBRO"));
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(pst, false);
        }
    }

    private static String generaReporteGastosViaticos(ResultSet rs, String plantillaPath) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "REPORTE_GASTOS" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        int cnt1 = 0;
        ResultSetMetaData rsMetadata = rs.getMetaData();
        int renglonInicio = 6;
        CellStyle estiloTabla = Util.generaEstilo2(workbook, 9, false, true, true, true, true, false);
        CellStyle estiloMoney = Util.generaEstilo2(workbook, 9, false, true, true, true, true, true);
        CellStyle estiloFecha = Util.generaEstilo2(workbook, 9, false, true, true, true, true, false);
        String comisionAnterior = "";
        boolean brincar = false;
        while (rs.next()) {
            Row rw = (sheet0.getRow(renglonInicio + cnt1) == null ? sheet0.createRow(renglonInicio + cnt1) : sheet0.getRow(renglonInicio + cnt1));
            for (int i = 0; i < rsMetadata.getColumnCount(); i++) {
                String nombreColumna = rsMetadata.getColumnName(i + 1);
                if (nombreColumna.equals("nIdComision")) {
                    String comisionActual = rs.getString(i + 1);
                    if (!comisionActual.equals(comisionAnterior)) {
                        comisionAnterior = comisionActual;
                        brincar = true;
                    }
                    Util.createExcelCellRep(i, rw, comisionActual, estiloTabla);
                } else if (nombreColumna.equals("DiasViaticos")) {
                    if (brincar) {
                        Util.createExcelCellRep(i, rw, rs.getString(nombreColumna), estiloTabla);
                        brincar = false;
                    } else {
                        // Celda vacía si es la misma comision
                        Util.createExcelCellRep(i, rw, "", estiloTabla);
                    }
                } else if (nombreColumna.equals("FINICIO") || nombreColumna.equals("FFIN")) {
                    Util.createExcelCellRep(i, rw, rs.getString(nombreColumna), estiloFecha);
                } else if (i > 9) {
                    Util.createExcelCellRep(i, rw, rs, nombreColumna, rsMetadata.getColumnType(i + 1), estiloMoney);
                } else {
                    Util.createExcelCellRep(i, rw, rs.getString(nombreColumna), estiloTabla);
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

    public static String generaBoletosXURManager(Connection conn, String UR, String fechaIni, String fechaFin, Map<String, String> plantillas) throws Exception {
        PreparedStatement pst = null;
        ResultSet rs = null;
        StringBuilder query = new StringBuilder();
        query.append(" SELECT * FROM v_BoletosRelacionados ");
        query.append("	WHERE cUejecutora LIKE '" + UR + "' AND faplicacion BETWEEN ? AND ?");
        String fileName = "";
        try {
            pst = conn.prepareStatement(query.toString());
            pst.setString(1, fechaIni);
            pst.setString(2, fechaFin);
            rs = pst.executeQuery();
            fileName = generaReporteBoletosViaticos(rs, plantillas.get("REPORTEBOLETOS"));
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(pst, false);
        }
    }

    private static String generaReporteBoletosViaticos(ResultSet rs, String plantillaPath) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "REPORTE_VIATICOS" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        int cnt1 = 0;
        ResultSetMetaData rsMetadata = rs.getMetaData();
        int renglonInicio = 6;
        CellStyle estiloTabla = Util.generaEstilo2(workbook, 9, false, true, true, true, true, false);
        CellStyle estiloMoney = Util.generaEstilo2(workbook, 9, false, true, true, true, true, true);
        while (rs.next()) {
            Row rw = (sheet0.getRow(renglonInicio + cnt1) == null ? sheet0.createRow(renglonInicio + cnt1) : sheet0.getRow(renglonInicio + cnt1));
            for (int i = 0; i < rsMetadata.getColumnCount(); i++) {
                if (rsMetadata.getColumnName(i + 1).equals("mImporteBoleto")) {
                    Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloMoney);
                } else
                    Util.createExcelCellRep(i, rw, rs.getString(rsMetadata.getColumnName(i + 1)), estiloTabla);
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

    public static String consultarAgendasPendientes(Connection conn, Map<String, String> plantillas) throws Exception {
        PreparedStatement pst = null;
        ResultSet rs = null;
        StringBuilder query = new StringBuilder();
        query.append(" SELECT nIdComision, nIdEmpleado, nombreCompleto, fInicio, fFin, Estatus, nOrigen, cUnidadResponsable, cDocHaplicado  ");
        query.append("	, (SELECT TOP 1 cnombre FROM tFirmantesViaticos WITH (NOLOCK) WHERE nFolio = nidComision ORDER BY nRenglon DESC) cNombreAutorizador ");
        query.append(" FROM v_inboxViaticos  WHERE cDocHaplicado is null or cDocHaplicado = 'S' ");
        try {
            pst = conn.prepareStatement(query.toString());
            rs = pst.executeQuery();
            String fileName = generaReporteBoletosViaticos(rs, plantillas.get("REPORTEAGENDASPENDIENTES"));
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(pst, false);
        }
    }

    public static void revisionDatosViaticos(Connection conn) throws Exception {
        CallableStatement cs = null;
        try {
            cs = conn.prepareCall("{call sp_validacionDatosViaticos }");
            cs.execute();
        } finally {
            CloseObject.closeObject(cs, false);
        }
    }
}
