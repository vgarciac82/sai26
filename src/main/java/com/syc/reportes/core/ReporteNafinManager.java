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
import java.util.ArrayList;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.syc.gestion.util.Util;
import com.syc.reportes.ReportePagosBeneficiariosBusinessLogic;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReporteNafinManager {

    private static final Logger log = LoggerFactory.getLogger(ReportePagosBeneficiariosBusinessLogic.class);

    public static ArrayList<String> ReportePagosManager(Connection conn, String fechaInicio, String fechaFin, String tipoReporte, Map<String, String> plantillas) throws Exception {
        ResultSet rs = null;
        String fini = "", ffin = "";
        ArrayList<String> arrListaComp = new ArrayList<String>();
        if (!"".equals(StringUtils.trimToEmpty(fechaInicio)) || !"".equals(StringUtils.trimToEmpty(fechaFin))) {
            fini = fechaInicio;
            ffin = fechaFin;
        } else {
            throw new Exception("Favor de seleccionar las fechas");
        }
        try {
            //Obtiene el ultimo folio del cf_sequence que se envio
            int consecutivo = buscarSiguienteConsecutivo(conn);
            //Poner las fechas de aplicacion correctas en tpagado
            actualizaFechasPagado(conn);
            //Realiza la consulta para generación de archivos
            rs = generaConsulta(conn, fini, ffin, consecutivo);
            //Actualizar la tabla de tpagosNafin
            guardarPagos(conn, fini, ffin);
            //Genera archivo en txt
            ResultSetMetaData rsmd = rs.getMetaData();
            while (rs.next()) {
                String token = new String();
                StringBuffer detalle = new StringBuffer();
                for (int i = 1; i < rsmd.getColumnCount() - 1; i++) {
                    detalle.append(token).append(rs.getString(i).trim().replaceAll("[\r\n]", ""));
                    token = "|";
                }
                token = "";
                detalle.append("\r\n");
                arrListaComp.add(detalle.toString());
            }
            return arrListaComp;
        } finally {
            CloseObject.closeObject(rs, false);
        }
    }

    public static void actualizaFechasPagado(Connection conn) throws Exception {
        PreparedStatement pst = null;
        String query = "UPDATE P SET FAPLICACION = CONVERT(DATE, FMOVIMIENTO,102) FROM dbo.tMovimiento M (NOLOCK) INNER JOIN dbo.tPagadoEncabezado P (NOLOCK)" + "ON M.cFolioDocumentoMovimiento = P.nFolioPagado WHERE cTipoDocumento = 'PAGADO' AND MONTH(fAplicacion) <> MONTH(fMovimiento)";
        try {
            pst = conn.prepareStatement(query);
            int num = pst.executeUpdate();
            log.debug("Object: {}", "Se actualizaron " + num + " registros.");
        } finally {
            CloseObject.closeObject(pst);
        }
    }

    public static String ReportePagosExcelManager(Connection conn, String fechaInicio, String fechaFin, String tipoReporte, Map<String, String> plantillas) throws Exception {
        ResultSet rs = null;
        String fini = "", ffin = "";
        String archivo = null;
        if (!"".equals(StringUtils.trimToEmpty(fechaInicio)) || !"".equals(StringUtils.trimToEmpty(fechaFin))) {
            fini = fechaInicio;
            ffin = fechaFin;
        } else {
            throw new Exception("Favor de seleccionar las fechas");
        }
        try {
            //Obtiene el ultimo folio del cf_sequence que se envio
            int consecutivo = buscarSiguienteConsecutivo(conn);
            //Poner las fechas de aplicacion correctas en tpagado
            actualizaFechasPagado(conn);
            //Realiza la consulta para generación de archivos
            rs = generaConsulta(conn, fini, ffin, consecutivo);
            //Actualizar la tabla de tpagosNafin
            guardarPagos(conn, fini, ffin);
            archivo = generaReporteExcel(rs, plantillas.get("PAGOSNAFIN"), fechaInicio, fechaFin);
            return archivo;
        } finally {
            CloseObject.closeObject(rs, false);
        }
    }

    private static ResultSet generaConsulta(Connection conn, String fini, String ffin, int consecutivo) throws Exception {
        CallableStatement ps = null;
        ResultSet rs = null;
        log.info("----------------Preparando la consulta para enviarla ------------------");
        ps = conn.prepareCall("{call sp_ReportePagosNafin( ?, ?, ? )}");
        ps.setString(1, fini);
        ps.setString(2, ffin);
        ps.setInt(3, consecutivo);
        rs = ps.executeQuery();
        return rs;
    }

    private static int buscarSiguienteConsecutivo(Connection conn) throws Exception {
        int consecutivo = 0;
        PreparedStatement pst = null;
        ResultSet rs = null;
        String query = "SELECT seq_value FROM CF_SEQUENCE WITH (NOLOCK) WHERE seq_name = 'FACTURAS_NAFIN'";
        try {
            pst = conn.prepareStatement(query);
            rs = pst.executeQuery();
            log.debug("Object: {}", "Consulta el consecutivo para el id del documento: " + query);
            if (rs.next()) {
                consecutivo = rs.getInt("seq_value");
            }
        } finally {
            CloseObject.closeObject(pst);
            CloseObject.closeObject(rs);
        }
        return consecutivo;
    }

    private static void guardarPagos(Connection conn, String fini, String ffin) throws Exception {
        PreparedStatement pst = null;
        StringBuilder query = new StringBuilder();
        query.append(" DECLARE @fechaini date = ? ");
        query.append(" DECLARE @fechaFin date = ? ");
        query.append(" INSERT INTO tpagosNafin ");
        query.append(" SELECT pago.caNoContrarrecibo, 'N' ");
        query.append("	   FROM tPagoDirectoEncabezado pago (NOLOCK) ");
        query.append("	   INNER JOIN tPagoFactura fact (NOLOCK) ");
        query.append("	   on pago.nFolioPagoDirecto = fact.nFolioPago AND fact.cTipoPago = 'PAGODIRECTO' ");
        query.append("	   INNER JOIN tBeneficiario BEN (NOLOCK) ");
        query.append("	   ON pago.cIdRFC = BEN.dRFC ");
        query.append("	   INNER JOIN tpagadoEncabezado pagado (NOLOCK) ");
        query.append("	   ON pago.caNoContrarrecibo = pagado.caNoContrarrecibo ");
        query.append("	   WHERE pago.cDocumentoHaplicado = 'S' and  pagado.cDocumentoHaplicado = 'S'  ");
        query.append("	   AND pagado.fAplicacion between @fechaini AND @fechaFin ");
        query.append("	   AND cEsNotaCredito = 'N' AND pago.nFolioPagoDirecto not in (select nFolioPagoDirecto from tPagoDirectoDetalle (NOLOCK)  where substring(EP,32,5) = '31301' OR substring(EP,32,5) = '39202' OR SUBSTRING(EP,32,1) = 4 )");
        query.append("	   AND pago.caNoContrarrecibo not in (select caNoContrarrecibo from tpagosNafin (NOLOCK)) ");
        query.append("	   and fact.mimporteconiva > 0 ");
        query.append("	   UNION  ");
        query.append("	   SELECT pago.caNoContrarrecibo, 'N' ");
        query.append("	   FROM tPAGODIVERSOEncabezado pago (NOLOCK) ");
        query.append("	   INNER JOIN tPagoFactura fact (NOLOCK) ");
        query.append("	   on pago.nFolioPAGODIVERSO = fact.nFolioPago AND fact.cTipoPago = 'PAGODIVERSO' ");
        query.append("	   INNER JOIN tBeneficiario BEN (NOLOCK) ");
        query.append("	   ON pago.RFC = BEN.dRFC ");
        query.append("	   INNER JOIN tpagadoEncabezado pagado (NOLOCK) ");
        query.append("	   ON pago.caNoContrarrecibo = pagado.caNoContrarrecibo ");
        query.append("	   WHERE pago.cDocumentoHaplicado = 'S' and  pagado.cDocumentoHaplicado = 'S' ");
        query.append("	   AND convert(date,pagado.fAplicacion,113) between @fechaini and @fechaFin ");
        query.append("	   AND cEsNotaCredito = 'N' AND pago.nFolioPagoDiverso not in (select nFolioPagoDiverso from tPagoDiversoDetalle (NOLOCK) where substring(EP,32,5) = '31301' OR substring(EP,32,5) = '39202' OR SUBSTRING(EP,32,1) = 4 )  ");
        query.append("	   AND pago.caNoContrarrecibo not in (select caNoContrarrecibo from tpagosNafin (NOLOCK)) ");
        query.append("	   and fact.mimporteconiva > 0 ");
        query.append("	   UNION  ");
        query.append("	   SELECT pago.caNoContrarrecibo, 'N' ");
        query.append("	   FROM tPAGOOBRAEncabezado pago (NOLOCK) ");
        query.append("	   INNER JOIN tBeneficiario BEN (NOLOCK) ");
        query.append("	   ON pago.RFC = BEN.dRFC ");
        query.append("	   INNER JOIN tpagadoEncabezado pagado (NOLOCK) ");
        query.append("	   ON pago.caNoContrarrecibo = pagado.caNoContrarrecibo ");
        query.append("	   WHERE pago.cDocumentoHaplicado = 'S' and  pagado.cDocumentoHaplicado = 'S' ");
        query.append("	   AND convert(date,pagado.fAplicacion,113) between @fechaini and @fechaFin ");
        query.append("	   AND pago.caNoContrarrecibo not in (select caNoContrarrecibo from tpagosNafin (NOLOCK)) ");
        try {
            pst = conn.prepareStatement(query.toString());
            pst.setString(1, fini);
            pst.setString(2, ffin);
            pst.execute();
        } finally {
            CloseObject.closeObject(pst);
        }
    }

    public static ArrayList<String> ReporteProveedorManager(Connection conn, String fechaInicio, String fechaFin, String tipoReporte, Map<String, String> plantillas) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String fini = "", ffin = "";
        ArrayList<String> arrListaComp = new ArrayList<String>();
        if (!"".equals(StringUtils.trimToEmpty(fechaInicio)) || !"".equals(StringUtils.trimToEmpty(fechaFin))) {
            fini = fechaInicio;
            ffin = fechaFin;
        } else {
            throw new Exception("Favor de seleccionar las fechas");
        }
        StringBuilder query = new StringBuilder();
        query.append(" DECLARE @fechaini date = ? ");
        query.append(" DECLARE @fechaFin date = ? ");
        query.append(" SELECT DISTINCT dRFC NUMERO, dRFC ");
        query.append(", CASE WHEN cIdTipoPersonaRFC = 2 then dApellidoPaterno else '' end  dApellidoPaterno ");
        query.append(", CASE WHEN cIdTipoPersonaRFC = 2 then dApellidoMaterno else '' end  dApellidoMaterno ");
        query.append(", CASE WHEN cIdTipoPersonaRFC = 2 then dnombre else '' end  Nombre ");
        query.append(", CASE WHEN cIdTipoPersonaRFC = 1 then dNombre else '' end  RazonSocial ");
        query.append(", dCalleFiscal ");
        query.append(", dColoniaFiscal ");
        query.append(", (select EDO_NOMBRE from CAT_ESTADOS (NOLOCK) where ID_ESTADO = ben.cEstadoFiscal) Estado ");
        query.append(", (select UPPER(cPais) from tCatalogoPaises (NOLOCK) where nIdPais =( select ID_PAIS from CAT_ESTADOS (NOLOCK)  where ID_ESTADO = ben.cEstadoFiscal)) Pais ");
        query.append(", (select mpo_nombre from CAT_MUNICIPIO (NOLOCK) where ID_MUNICIPIO =  cMunicipioFiscal) ");
        query.append(", ben.dCodigoPostalFiscal ");
        query.append(", ISNULL(ben.dTelefonoActual, '') dTelefonoActual ");
        query.append(", ISNULL(ben.dTelefonoFiscal, '') dTelefonoFiscal ");
        query.append(", ben.dEMailFiscal ");
        query.append(", coalesce(dapaternoApoderado, dapellidoPaterno) apellidoPaternoApoderado ");
        query.append(", coalesce(damaternoApoderado, dapellidoMaterno) apellidoMaternoApoderado ");
        query.append(", CASE WHEN cIdTipoPersonaRFC = 1 then ISNULL(dnombreApoderado,'') else ISNULL(dNombre, '') end  RazonSocial ");
        query.append(", ISNULL(ben.dTelefonoFiscal,'') dTelefonoFiscal ");
        query.append(", '' fax, ben.dEMailActual, 1 id, 'S' id2, 'N' id3 ");
        query.append(" FROM tBeneficiario ben(NOLOCK) ");
        query.append(" INNER JOIN tproveedorNafin PROV (NOLOCK) ");
        query.append(" ON BEN.dRFC = PROV.cIdRFC ");
        query.append(" WHERE  cIdTipoPersonaRFC IN (1,2) AND PROV.cenviadoNafin = 'N' ");
        try {
            guardarProveedores(conn, fini, ffin);
            log.info("----------------Preparando la consulta para enviarla ------------------");
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, fini);
            ps.setString(2, ffin);
            rs = ps.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            log.info("Object: {}", query.toString());
            while (rs.next()) {
                String token = new String();
                StringBuffer detalle = new StringBuffer();
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    detalle.append(token).append(rs.getString(i).trim().replaceAll("[\r\n]{2,}", " "));
                    token = "|";
                }
                token = "";
                detalle.append("\r\n");
                arrListaComp.add(detalle.toString());
            }
            return arrListaComp;
        } finally {
            CloseObject.closeObject(ps, false);
            CloseObject.closeObject(rs, false);
        }
    }

    private static void guardarProveedores(Connection conn, String fini, String ffin) throws Exception {
        PreparedStatement pst = null;
        StringBuilder query = new StringBuilder();
        query.append(" DECLARE @fechaini date = ? ");
        query.append(" DECLARE @fechaFin date = ? ");
        query.append(" INSERT INTO tproveedorNafin ");
        query.append(" SELECT DISTINCT dRFC , 'N' ");
        query.append(" FROM tBeneficiario ben(NOLOCK) ");
        query.append(" INNER JOIN tPAGODIVERSOEncabezado (nolock) ");
        query.append(" ON dRFC = tPAGODIVERSOEncabezado.RFC ");
        query.append(" WHERE cDocumentoHaplicado = 's' AND convert(date,fAplicacion,113) between @fechaini AND @fechaFin ");
        query.append(" AND dRFC not in (SELECT cIdRFC FROM tproveedorNafin (nolock) ) ");
        query.append(" UNION ");
        query.append(" SELECT DISTINCT dRFC , 'N' ");
        query.append(" FROM tBeneficiario ben(NOLOCK) ");
        query.append(" INNER JOIN TPAGODIRECTOENCABEZADO (nolock) ");
        query.append(" ON ben.dRFC = TPAGODIRECTOENCABEZADO.cIdRFC ");
        query.append(" WHERE cDocumentoHaplicado = 's' AND convert(date,fAplicacion,113) between @fechaini AND @fechaFin ");
        query.append(" AND dRFC not in (SELECT cIdRFC FROM tproveedorNafin (nolock)) ");
        query.append(" UNION  ");
        query.append(" SELECT DISTINCT dRFC, 'N' ");
        query.append(" FROM tBeneficiario ben(NOLOCK) ");
        query.append(" INNER JOIN TPAGOOBRAENCABEZADO (nolock) ");
        query.append(" ON ben.dRFC = tPAGOOBRAEncabezado.RFC ");
        query.append(" WHERE cDocumentoHaplicado = 's' AND convert(date,fAplicacion,113) between @fechaini AND @fechaFin ");
        query.append(" AND dRFC not in (SELECT cIdRFC FROM tproveedorNafin (nolock))");
        try {
            log.debug("Object: {}", query.toString());
            pst = conn.prepareStatement(query.toString());
            pst.setString(1, fini);
            pst.setString(2, ffin);
            pst.execute();
        } finally {
            CloseObject.closeObject(pst);
        }
    }

    public static void FinalizaProveedor(Connection conn) throws Exception {
        PreparedStatement pst = null;
        String query = "UPDATE tproveedornafin SET cEnviadoNafin = 'S' WHERE cEnviadoNafin = 'N'";
        try {
            pst = conn.prepareStatement(query);
            pst.executeUpdate();
        } finally {
            CloseObject.closeObject(pst);
        }
    }

    public static void FinalizaPagos(Connection conn, String fechaIni, String fechaFin) throws Exception {
        CallableStatement pst = null;
        try {
            pst = conn.prepareCall("{call sp_contarPagos(?, ?)}");
            pst.setString(1, fechaIni);
            pst.setString(2, fechaFin);
            pst.executeUpdate();
        } finally {
            CloseObject.closeObject(pst);
        }
    }

    public static void actualizaCFSequence(Connection conn, int noPagos) throws Exception {
        PreparedStatement pst = null;
        String query = "UPDATE  CF_SEQUENCE WITH (ROWLOCK) SET seq_value = seq_value + " + noPagos + " WHERE seq_name = 'FACTURAS_NAFIN'";
        try {
            pst = conn.prepareStatement(query);
            pst.executeUpdate();
        } finally {
            CloseObject.closeObject(pst);
        }
    }

    private static String generaReporteExcel(ResultSet rs, String plantillaPath, String fechaInicio, String fechaFin) throws Exception {
        File cFileExcelPlantilla = new File(plantillaPath);
        String file_name = System.getProperty("java.io.tmpdir") + File.separatorChar + "ReporteNafin" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xlsx";
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new XSSFWorkbook(fsArchivo);
        fsArchivo.close();
        Sheet sheet0 = workbook.getSheetAt(0);
        int cnt = 0;
        ResultSetMetaData rsMetadata = rs.getMetaData();
        int[] dataTypes = new int[rsMetadata.getColumnCount()];
        String[] columNames = new String[rsMetadata.getColumnCount()];
        for (int i = 0; i < rsMetadata.getColumnCount(); i++) {
            dataTypes[i] = rsMetadata.getColumnType(i + 1);
            columNames[i] = rsMetadata.getColumnName(i + 1);
        }
        int renglonInicio = 2;
        CellStyle estiloTabla = workbook.createCellStyle();
        estiloTabla.setBorderRight(BorderStyle.HAIR);
        estiloTabla.setBorderLeft(BorderStyle.HAIR);
        estiloTabla.setBorderTop(BorderStyle.HAIR);
        estiloTabla.setBorderBottom(BorderStyle.HAIR);
        while (rs.next()) {
            Row rw = (sheet0.getRow(renglonInicio + cnt) == null ? sheet0.createRow(renglonInicio + cnt) : sheet0.getRow(renglonInicio + cnt));
            for (int i = 0; i < rsMetadata.getColumnCount(); i++) {
                Util.createExcelCellRep(i, rw, rs, columNames[i], dataTypes[i], estiloTabla);
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
}
