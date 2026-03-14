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
import java.util.Map;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;
import java.nio.file.Paths;

public class ReporteNominaManager {

    private static final Logger log = LoggerFactory.getLogger(ReporteNominaManager.class);

    public static String generaReporteNominaManager(Connection conn, String fechaFin, Map<String, String> plantillas) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder str = new StringBuilder();
        str.append("select distinct enc.fAplicacion, enc.cIdUsuarioCaptura, enc.cIdRelacion, clc.cConcepto, clc.mImporteNeto ");
        str.append(", compe.nFolioSICOP folioCompromisoSicop");
        str.append(", enc.caNoCompromiso, clcDet.caNoContrarrecibo, clc.caNoContrarreciboCLC");
        str.append(", pag.nFolioSICOP, pag.nFolioSIAFF, pag.NumeroProceso, pag.FechaPagoSicop");
        str.append(" from tNOMINACLCEncabezado clc (nolock)");
        str.append(" inner join tNOMINACLCDetalle clcDet (nolock)");
        str.append(" on clc.nFolioNOMINACLC = clcDet.nFolioNOMINACLC");
        str.append(" inner join tNOMINAEncabezado enc (nolock)");
        str.append(" on enc.caNoContrarrecibo = clcDet.caNoContrarrecibo");
        str.append(" left join tPagadoEncabezado pag (nolock)");
        str.append(" on pag.caNoContrarrecibo = clc.caNoContrarreciboCLC");
        str.append(" left join tCompromisoNominaEncabezado compe (nolock)");
        str.append(" on enc.caNoCompromiso = compe.caNoCompromiso");
        str.append(" where enc.cDocumentoHaplicado = 'S'");
        str.append(" and clc.Activo = 1");
        str.append(" and enc.fAplicacion <= '" + fechaFin + "'");
        String fileName = "";
        try {
            String query = str.toString();
            ps = conn.prepareStatement(query);
            log.debug("Object: {}", query.toString());
            rs = ps.executeQuery();
            fileName = generaReporteNomina(rs, plantillas.get("REPNOMINA"), fechaFin, fechaFin);
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
    }

    private static String generaReporteNomina(ResultSet rs, String plantillaPath, String fechaInicio, String fechaFin) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "ReporteNomina" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xlsx";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new XSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        int cnt = 0;
        ResultSetMetaData rsMetadata = rs.getMetaData();
        int renglonInicio = 2;
        int mesIni = 0;
        int mesFin = 0;
        String anio = "";
        String periodo = "";
        mesIni = Integer.parseInt(fechaInicio.substring(5, 7));
        mesFin = Integer.parseInt(fechaFin.substring(5, 7));
        anio = fechaInicio.substring(0, 4);
        if (mesIni == mesFin)
            periodo = Util.NOMBRE_MESES_MX[mesIni - 1] + " " + anio;
        else if (mesIni != mesFin)
            periodo = "DE " + Util.NOMBRE_MESES_MX[mesIni - 1] + " A " + Util.NOMBRE_MESES_MX[mesFin - 1] + " " + anio;
        Row rwEnc2 = (sheet0.getRow(0) == null ? sheet0.createRow(0) : sheet0.getRow(0));
        Cell cell2 = (rwEnc2.getCell(1) == null ? rwEnc2.createCell(1) : rwEnc2.getCell(1));
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
        workbook.close();
        /* Cierra Flujos */
        bos.flush();
        bos.close();
        fos.close();
        return file_name;
    }

    public static String generaReporteSaldosCompromisoMil(Connection conn, String cxp, String ep, Map<String, String> plantillas) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder str = new StringBuilder();
        if (!"".equals(cxp)) {
            str = new StringBuilder();
            str.append("SELECT caNoCompromiso ");
            str.append(" , EP ");
            str.append(" , CASE WHEN cMes = 1 THEN Remanente ELSE 0 END AS ENE ");
            str.append(" , CASE WHEN cMes = 2 THEN Remanente ELSE 0 END AS FEB ");
            str.append(" , CASE WHEN cMes = 3 THEN Remanente ELSE 0 END AS MAR ");
            str.append(" , CASE WHEN cMes = 4 THEN Remanente ELSE 0 END AS ABR ");
            str.append(" , CASE WHEN cMes = 5 THEN Remanente ELSE 0 END AS MAY ");
            str.append(" , CASE WHEN cMes = 6 THEN Remanente ELSE 0 END AS JUN ");
            str.append(" , CASE WHEN cMes = 7 THEN Remanente ELSE 0 END AS JUL ");
            str.append(" , CASE WHEN cMes = 8 THEN Remanente ELSE 0 END AS AGO ");
            str.append(" , CASE WHEN cMes = 9 THEN Remanente ELSE 0 END AS SEP ");
            str.append(" , CASE WHEN cMes = 10 THEN Remanente ELSE 0 END AS OCT ");
            str.append(" , CASE WHEN cMes = 11 THEN Remanente ELSE 0 END AS NOM ");
            str.append(" , CASE WHEN cMes = 12 THEN Remanente ELSE 0 END AS DIC ");
            str.append(" FROM vRemanenteCompromisoCapituloMil WITH (NOLOCK) ");
            str.append(" WHERE caNoCompromiso LIKE '" + cxp + "'");
            str.append(" AND Remanente > 0 ");
        }
        if (!"".equals(ep)) {
            str = new StringBuilder();
            str.append("SELECT caNoCompromiso ");
            str.append(" , EP ");
            str.append(" , CASE WHEN cMes = 1 THEN Remanente ELSE 0 END AS ENE ");
            str.append(" , CASE WHEN cMes = 2 THEN Remanente ELSE 0 END AS FEB ");
            str.append(" , CASE WHEN cMes = 3 THEN Remanente ELSE 0 END AS MAR ");
            str.append(" , CASE WHEN cMes = 4 THEN Remanente ELSE 0 END AS ABR ");
            str.append(" , CASE WHEN cMes = 5 THEN Remanente ELSE 0 END AS MAY ");
            str.append(" , CASE WHEN cMes = 6 THEN Remanente ELSE 0 END AS JUN ");
            str.append(" , CASE WHEN cMes = 7 THEN Remanente ELSE 0 END AS JUL ");
            str.append(" , CASE WHEN cMes = 8 THEN Remanente ELSE 0 END AS AGO ");
            str.append(" , CASE WHEN cMes = 9 THEN Remanente ELSE 0 END AS SEP ");
            str.append(" , CASE WHEN cMes = 10 THEN Remanente ELSE 0 END AS OCT ");
            str.append(" , CASE WHEN cMes = 11 THEN Remanente ELSE 0 END AS NOM ");
            str.append(" , CASE WHEN cMes = 12 THEN Remanente ELSE 0 END AS DIC ");
            str.append(" FROM vRemanenteCompromisoCapituloMil WITH (NOLOCK) ");
            str.append(" WHERE EP LIKE '" + ep + "'");
            str.append(" AND Remanente > 0 ");
        }
        String fileName = "";
        try {
            String query = str.toString();
            ps = conn.prepareStatement(query);
            log.debug("Object: {}", query.toString());
            rs = ps.executeQuery();
            fileName = generaReporteSCOMPMIL(rs, plantillas.get("SALDOCOMPROMISOMIL"));
            return fileName;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
    }

    private static String generaReporteSCOMPMIL(ResultSet rs, String plantillaPath) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "ReporteSaldosCompromisoMil" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xlsx";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new XSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        int cnt = 0;
        ResultSetMetaData rsMetadata = rs.getMetaData();
        int renglonInicio = 1;
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
        workbook.close();
        /* Cierra Flujos */
        bos.flush();
        bos.close();
        fos.close();
        return file_name;
    }
}
