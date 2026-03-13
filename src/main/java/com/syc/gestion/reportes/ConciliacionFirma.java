package com.syc.gestion.reportes;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import com.axtel.reports.exceptions.ReportException;
import com.lowagie.text.Rectangle;
import com.syc.fortimax.core.Carpeta;
import com.syc.fortimax.core.DocumentoManager;
import com.syc.fortimax.core.Volumen;
import com.syc.fortimax.exceptions.FortimaxException;
import com.syc.gestion.core.AlarmaManager;
import com.syc.gestion.util.Util;
import com.syc.obrapublica.core.ConfiguraAplicativoManager;
import com.syc.reportes.core.ReporteAvanceFinancieroManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import com.syc.sai.firmaElectronica.exceptions.FirmaElectronicaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConciliacionFirma extends FirmaElectronicaReporte {

    private static final Logger log = LoggerFactory.getLogger(ConciliacionFirma.class);

    public static final List<Integer> REPORTE_HORIZONTAL = new ArrayList<Integer>();

    public static final Map<Integer, Integer[]> ZONAS_ACUSE = new HashMap<Integer, Integer[]>(4);

    public static final Map<Integer, Integer[]> ZONAS_ACUSE_H = new HashMap<Integer, Integer[]>(4);

    public static final Map<Integer, Rectangle> ZONAS_FIRMA = new HashMap<Integer, Rectangle>(4);

    public static final Map<Integer, Rectangle> ZONAS_FIRMA_H = new HashMap<Integer, Rectangle>(4);

    static {
        REPORTE_HORIZONTAL.add(1);
        REPORTE_HORIZONTAL.add(7);
        ZONAS_FIRMA.put(1, new Rectangle(40, 55, 220, 140));
        ZONAS_FIRMA.put(2, new Rectangle(40, 55, 220, 140));
        ZONAS_FIRMA.put(3, new Rectangle(225, 55, 385, 140));
        ZONAS_FIRMA.put(4, new Rectangle(400, 55, 560, 140));
        ZONAS_FIRMA_H.put(1, new Rectangle(40, 50, 250, 140));
        ZONAS_FIRMA_H.put(2, new Rectangle(40, 50, 250, 140));
        ZONAS_FIRMA_H.put(3, new Rectangle(288, 50, 498, 140));
        ZONAS_FIRMA_H.put(4, new Rectangle(516, 50, 726, 140));
        ZONAS_ACUSE.put(1, new Integer[] { 33, 165, 580, 220 });
        ZONAS_ACUSE.put(2, new Integer[] { 33, 165, 580, 220 });
        ZONAS_ACUSE.put(3, new Integer[] { 33, 105, 580, 160 });
        ZONAS_ACUSE.put(4, new Integer[] { 33, 45, 580, 100 });
        ZONAS_ACUSE_H.put(1, new Integer[] { 43, 63, 740, 117 });
        ZONAS_ACUSE_H.put(2, new Integer[] { 43, 63, 740, 117 });
        ZONAS_ACUSE_H.put(3, new Integer[] { 43, 36, 740, 90 });
        ZONAS_ACUSE_H.put(4, new Integer[] { 43, 8, 740, 62 });
    }

    private String cargoFirmante;

    private String descripcionFirmante;

    private int idTipoFirmante;

    private String leyendaFirma;

    private String nombreFirmante;

    private int numeroEmpleadoFirmante;

    private Map<String, Object> parameters;

    private String reportFilePath;

    private String reportName;

    private String rutaAcuseImpreso;

    private String unidadFirmante;

    private int idConciliacion;

    private String claveBanco;

    public ConciliacionFirma() {
    }

    public ConciliacionFirma(String reportFilePath, String reportName, Map<String, Object> parameters) {
        super();
        this.reportFilePath = reportFilePath;
        this.parameters = parameters;
        this.reportName = reportName;
    }

    public void cargaIdTipoFirmante(Connection conn) throws Exception {
        StringBuilder query = new StringBuilder();
        query.append(" SELECT	nIDTipoFirmante  ");
        query.append("   FROM	vFirmanteReporteConciliacion  ");
        query.append("  WHERE	nOrden = ? ");
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cargaOrdenActual(conn);
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, getOrden());
            rs = ps.executeQuery();
            if (rs.next()) {
                setIdTipoFirmante(rs.getInt("nIDTipoFirmante"));
            }
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public void cargaInformacion(Connection conn) throws Exception {
        StringBuilder query = new StringBuilder();
        query.append(" SELECT	cNumeroEmpleado, nIDTipoFirmante, cDescTipoFirmante, cLeyendaFirma, nombreFirmante, ");
        query.append(" 		Cargo, UNIDAD, RFC, estatus, nMes, nCban, cRutaAcuse, cRutaArchivo  ");
        query.append("   FROM	vFirmanteReporteConciliacion  ");
        query.append("  WHERE	nConciliacion = ?  ");
        query.append("    AND	nOrden = ? ");
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cargaOrdenActual(conn);
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, getIdConciliacion());
            ps.setInt(2, getOrden());
            rs = ps.executeQuery();
            if (rs.next()) {
                setNumeroEmpleadoFirmante(rs.getInt("cNumeroEmpleado"));
                setIdTipoFirmante(rs.getInt("nIDTipoFirmante"));
                setDescripcionFirmante(rs.getString("cDescTipoFirmante"));
                setLeyendaFirma(rs.getString("cLeyendaFirma"));
                setNombreFirmante(rs.getString("nombreFirmante"));
                setCargoFirmante(rs.getString("Cargo"));
                setUnidadFirmante(rs.getString("UNIDAD"));
                setRfcFirma(rs.getString("RFC"));
                setMes(rs.getInt("nMes"));
                setClaveBanco(rs.getString("nCban"));
                setRutaReporteImpreso(rs.getString("cRutaArchivo"));
                setRutaAcuseImpreso(rs.getString("cRutaAcuse"));
            }
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public final void cargaOrdenActual(Connection conn) throws Exception {
        StringBuilder query = new StringBuilder();
        query.append("SELECT	nConciliacion, ");
        query.append("      	MIN(nOrden) AS  nOrden ");
        query.append("  FROM	tFirmanteConciliacion firmante ");
        query.append(" WHERE	nConciliacion = ?  ");
        query.append("   AND	cEstatus = 'E' GROUP BY nConciliacion");
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, getIdConciliacion());
            rs = ps.executeQuery();
            while (rs.next()) {
                setOrden(rs.getInt(2));
            }
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public String generaAccessoAutToken(int numeroEmpleado, int idConciliacion, int idOrden) {
        StringBuffer parametrosReales = null;
        /*
		 * Concatena los parametros. El separador sera el caracter | (pipe)
		 */
        parametrosReales = new StringBuffer("?");
        parametrosReales.append("u=").append(StringUtils.reverse(String.valueOf(numeroEmpleado)));
        parametrosReales.append("&");
        parametrosReales.append("d=").append("CONCILIABANCOS");
        parametrosReales.append("&");
        parametrosReales.append("o=").append(String.valueOf(idOrden));
        parametrosReales.append("&");
        parametrosReales.append("f=").append(StringUtils.reverse(String.valueOf(idConciliacion)));
        log.debug("Cadena generada: " + parametrosReales);
        return parametrosReales.toString();
    }

    public String generaAcuse(Connection conn, Volumen volumen, Object object, boolean b) throws Exception {
        String rptAcuse = StringUtils.replace(reportName, "_fiel", "_acuse");
        String nombreArchivo = DocumentoManager.getNextFilename(null, "Reporte_");
        File outputPath = new File(volumen.getUnidad() + volumen.getRutaBase() + volumen.getRutaDirectorio() + volumen.getVolumen() + File.separatorChar + "Acuse_" + nombreArchivo + ".pdf");
        runReport(conn, getReportFilePath(), rptAcuse, null, outputPath.getAbsolutePath(), getParameters());
        setRutaAcuseImpreso(outputPath.getAbsolutePath());
        return outputPath.getAbsolutePath();
    }

    @Override
    public String generaArchivoFirma(Connection conn, Volumen vol, Carpeta folder, boolean isSignedCopy) throws FirmaElectronicaException {
        try {
            String nombreArchivo = DocumentoManager.getNextFilename(null, "Reporte_");
            File outputPath = new File(vol.getUnidad() + vol.getRutaBase() + vol.getRutaDirectorio() + vol.getVolumen() + File.separatorChar + nombreArchivo + ".pdf");
            runReport(conn, getReportFilePath(), getReportName(), null, outputPath.getAbsolutePath(), getParameters());
            return outputPath.getAbsolutePath();
        } catch (FortimaxException | ReportException e) {
            throw new FirmaElectronicaException(e);
        }
    }

    @Override
    public String getAutLegend(Connection conn) throws Exception {
        throw new Exception("getAutLegend No implementado");
    }

    public String getCargoFirmante() {
        return cargoFirmante;
    }

    @Override
    public String getCorreoAutoriza(Connection conn) throws Exception {
        StringBuilder query = new StringBuilder();
        query.append("SELECT	U_EMAIL  ");
        query.append("   FROM	CG_USUARIO WITH(NOLOCK)  ");
        query.append("  WHERE	cNumeroEmpleado =  ?  ");
        query.append("    AND	U_ESTATUS = 'A'");
        PreparedStatement psMail = null;
        ResultSet rsMail = null;
        try {
            int numeroEmpleado = getNumeroEmpleadoFirmante();
            psMail = conn.prepareStatement(query.toString());
            psMail.setInt(1, numeroEmpleado);
            rsMail = psMail.executeQuery();
            if (rsMail.next()) {
                if (StringUtils.isBlank(rsMail.getString("U_EMAIL")))
                    throw new Exception("El empleado con Numero: " + numeroEmpleado + " no tiene correo asignado. Notifique al administrador");
                return rsMail.getString("U_EMAIL");
            } else
                throw new Exception("No se encontro empleado con numero: " + numeroEmpleado);
        } finally {
            CloseObject.closeObject(psMail);
            CloseObject.closeObject(rsMail);
        }
    }

    private String getCorreoElabora(Connection conn) throws Exception {
        StringBuilder queryMail = new StringBuilder();
        queryMail.append("SELECT	U_EMAIL ");
        queryMail.append("  FROM	CG_USUARIO WITH(NOLOCK) ");
        queryMail.append(" WHERE	cNumeroEmpleado =  ? ");
        queryMail.append("   AND	U_ESTATUS = 'A'");
        PreparedStatement psMail = null;
        ResultSet rsMail = null;
        try {
            int numeroEmpleadoElabora = getNumEmpleadoElabora(conn);
            psMail = conn.prepareStatement(queryMail.toString());
            psMail.setInt(1, numeroEmpleadoElabora);
            rsMail = psMail.executeQuery();
            if (rsMail.next()) {
                if (StringUtils.isBlank(rsMail.getString("U_EMAIL")))
                    throw new Exception("El empleado con Numero: " + numeroEmpleadoElabora + " no tiene correo asignado. Notifique al administrador");
                return rsMail.getString("U_EMAIL");
            } else
                throw new Exception("No se encontro empleado con numero: " + numeroEmpleadoElabora);
        } finally {
            CloseObject.closeObject(psMail);
            CloseObject.closeObject(rsMail);
        }
    }

    @Override
    public String getCuerpoCorreoAutoriza(Connection conn) throws Exception {
        String urlAutorizacion = ConfiguraAplicativoManager.getSystemSetting(conn, "URL_SAI") + "/reportes/FirmaReporte";
        String nombreCompleto = getNombreFirmante();
        String puesto = getCargoFirmante();
        int folio = getIdConciliacion();
        int numeroEmpleado = getNumeroEmpleadoFirmante();
        StringBuilder mailBody = new StringBuilder();
        mailBody.append("<html>");
        mailBody.append("\n\t<head>");
        mailBody.append("\n\t<meta charset=\"UTF-8\">");
        mailBody.append("\n\t<style type=\"text/css\">");
        mailBody.append("\n\tbody {");
        mailBody.append("\n\t\t	font-family: verdana, arial, sans-serif;");
        mailBody.append("\n\t\t	font-size: 12px;");
        mailBody.append("\n\t}");
        mailBody.append("\n\ttable {");
        mailBody.append("\n\t\tfont-size: 12px;");
        mailBody.append("\n\t\tcolor: #333333;");
        mailBody.append("\n\t\tborder-width: 1px;");
        mailBody.append("\n\t\tborder-color: #666666;");
        mailBody.append("\n\t\tborder-collapse: collapse;");
        mailBody.append("\n\t}");
        mailBody.append("\n\ttable th {");
        mailBody.append("\n\t\tborder-width: 1px;");
        mailBody.append("\n\t\tpadding: 8px;");
        mailBody.append("\n\t\tborder-style: solid;");
        mailBody.append("\n\t\tborder-color: #666666;");
        mailBody.append("\n\t\tbackground-color: #dedede;");
        mailBody.append("\n\t}");
        mailBody.append("\n\ttable td {");
        mailBody.append("\n\t\tborder-width: 1px;");
        mailBody.append("\n\t\tpadding: 8px;");
        mailBody.append("\n\t\tborder-style: solid;");
        mailBody.append("\n\t\tborder-color: #666666;");
        mailBody.append("\n\t\tbackground-color: #ffffff;");
        mailBody.append("\n\t}");
        mailBody.append("\n\t</style>");
        mailBody.append("</head>");
        mailBody.append("\n\t<body>");
        mailBody.append("\n\t\t<form id=\"Form\" name=\"FormConciliacion\" >");
        mailBody.append("	<b> C.").append(nombreCompleto).append("</b>");
        mailBody.append("	<br>");
        mailBody.append("	<b>").append(puesto).append("</b>");
        mailBody.append("	<br />");
        mailBody.append("	<p>");
        mailBody.append("		Se solicita de su Firma de ").append(getLeyendaFirmante(conn, getOrden())).append(" de la siguiente conciliación:");
        mailBody.append("	</p>");
        mailBody.append("	<table>");
        mailBody.append("		<thead>");
        mailBody.append("			<tr>");
        mailBody.append("				<th>ID</th>");
        mailBody.append("				<th>Clave Banco</th>");
        mailBody.append("				<th>Mes</th>");
        mailBody.append("			</tr>");
        mailBody.append("		</thead>");
        mailBody.append("		<tbody>");
        mailBody.append("<tr>");
        mailBody.append("\n<td>" + folio + "</td>");
        mailBody.append("\n<td>" + getClaveBanco() + "</td>");
        mailBody.append("\n<td>" + Util.nombreDeMes(getMes()) + "</td>");
        mailBody.append("</tr>");
        mailBody.append("		</tbody>");
        mailBody.append("	</table>");
        mailBody.append("	<br />");
        mailBody.append("	<br />");
        mailBody.append("\n<b>Para firmar o rechazar esta solicitud por favor de click <a href=\"" + urlAutorizacion + generaAccessoAutToken(numeroEmpleado, getIdConciliacion(), getOrden()) + "\" > aquí </a>.</b>");
        mailBody.append("	<br />");
        mailBody.append("	<br />");
        mailBody.append("	<p>");
        mailBody.append("		Notificaciones Automaticas<br />Sistema de Administracion Integral<br />" + Util.getToday());
        mailBody.append("	</p>");
        mailBody.append("	</form>");
        mailBody.append("</body>");
        mailBody.append("</html>");
        return mailBody.toString();
    }

    public String getCuerpoCorreoElabora(Connection conn) throws Exception {
        String urlAutorizacion = ConfiguraAplicativoManager.getSystemSetting(conn, "URL_SAI") + "/reportes/FirmaReporte";
        String nombreCompleto = getElaboraNombre(conn);
        String puesto = getElaboraPuesto(conn);
        int folio = getIdConciliacion();
        int numeroEmpleado = getNumEmpleadoElabora(conn);
        StringBuilder mailBody = new StringBuilder();
        mailBody.append("<html>");
        mailBody.append("\n\t<head>");
        mailBody.append("\n\t<meta charset=\"UTF-8\">");
        mailBody.append("\n\t<style type=\"text/css\">");
        mailBody.append("\n\tbody {");
        mailBody.append("\n\t\t	font-family: verdana, arial, sans-serif;");
        mailBody.append("\n\t\t	font-size: 12px;");
        mailBody.append("\n\t}");
        mailBody.append("\n\ttable {");
        mailBody.append("\n\t\tfont-size: 12px;");
        mailBody.append("\n\t\tcolor: #333333;");
        mailBody.append("\n\t\tborder-width: 1px;");
        mailBody.append("\n\t\tborder-color: #666666;");
        mailBody.append("\n\t\tborder-collapse: collapse;");
        mailBody.append("\n\t}");
        mailBody.append("\n\ttable th {");
        mailBody.append("\n\t\tborder-width: 1px;");
        mailBody.append("\n\t\tpadding: 8px;");
        mailBody.append("\n\t\tborder-style: solid;");
        mailBody.append("\n\t\tborder-color: #666666;");
        mailBody.append("\n\t\tbackground-color: #dedede;");
        mailBody.append("\n\t}");
        mailBody.append("\n\ttable td {");
        mailBody.append("\n\t\tborder-width: 1px;");
        mailBody.append("\n\t\tpadding: 8px;");
        mailBody.append("\n\t\tborder-style: solid;");
        mailBody.append("\n\t\tborder-color: #666666;");
        mailBody.append("\n\t\tbackground-color: #ffffff;");
        mailBody.append("\n\t}");
        mailBody.append("\n\t</style>");
        mailBody.append("</head>");
        mailBody.append("\n\t<body>");
        mailBody.append("\n\t\t<form id=\"Form\" name=\"FormConciliacion\" >");
        mailBody.append("	<b> C.").append(nombreCompleto).append("</b>");
        mailBody.append("	<br>");
        mailBody.append("	<b>").append(puesto).append("</b>");
        mailBody.append("	<br />");
        mailBody.append("	<p>");
        mailBody.append("		Se solicita de su Firma de ").append(getLeyendaFirmante(conn, getOrden())).append(" de la siguiente Conciliación Bancaria");
        mailBody.append("	</p>");
        mailBody.append("	<table>");
        mailBody.append("		<thead>");
        mailBody.append("			<tr>");
        mailBody.append("				<th>ID</th>");
        mailBody.append("				<th>Clabe interbancaria</th>");
        mailBody.append("				<th>Mes</th>");
        mailBody.append("			</tr>");
        mailBody.append("		</thead>");
        mailBody.append("		<tbody>");
        mailBody.append("<tr>");
        mailBody.append("\n<td>" + folio + "</td>");
        mailBody.append("\n<td>" + getClaveBanco() + "</td>");
        mailBody.append("\n<td>" + Util.nombreDeMes(getMes()) + "</td>");
        mailBody.append("</tr>");
        mailBody.append("		</tbody>");
        mailBody.append("	</table>");
        mailBody.append("	<br />");
        mailBody.append("	<br />");
        mailBody.append("\n<b>Para firmar o rechazar esta solicitud por favor de click <a href=\"" + urlAutorizacion + generaAccessoAutToken(numeroEmpleado, getIdConciliacion(), 1) + "\" > aquí </a>.</b>");
        mailBody.append("	<br />");
        mailBody.append("	<br />");
        mailBody.append("	<p>");
        mailBody.append("		Notificaciones Automaticas<br />Sistema de Administracion Integral<br />" + Util.getToday());
        mailBody.append("	</p>");
        mailBody.append("	</form>");
        mailBody.append("</body>");
        mailBody.append("</html>");
        return mailBody.toString();
    }

    public String getCuerpoCorreoRechazo(Connection conn) throws Exception {
        String nombreCompleto = getElaboraNombre(conn);
        String puesto = getElaboraPuesto(conn);
        int folio = getIdConciliacion();
        StringBuilder mailBody = new StringBuilder();
        mailBody.append("<html>");
        mailBody.append("\n\t<head>");
        mailBody.append("\n\t<meta charset=\"UTF-8\">");
        mailBody.append("\n\t<style type=\"text/css\">");
        mailBody.append("\n\tbody {");
        mailBody.append("\n\t\t	font-family: verdana, arial, sans-serif;");
        mailBody.append("\n\t\t	font-size: 12px;");
        mailBody.append("\n\t}");
        mailBody.append("\n\ttable {");
        mailBody.append("\n\t\tfont-size: 12px;");
        mailBody.append("\n\t\tcolor: #333333;");
        mailBody.append("\n\t\tborder-width: 1px;");
        mailBody.append("\n\t\tborder-color: #666666;");
        mailBody.append("\n\t\tborder-collapse: collapse;");
        mailBody.append("\n\t}");
        mailBody.append("\n\ttable th {");
        mailBody.append("\n\t\tborder-width: 1px;");
        mailBody.append("\n\t\tpadding: 8px;");
        mailBody.append("\n\t\tborder-style: solid;");
        mailBody.append("\n\t\tborder-color: #666666;");
        mailBody.append("\n\t\tbackground-color: #dedede;");
        mailBody.append("\n\t}");
        mailBody.append("\n\ttable td {");
        mailBody.append("\n\t\tborder-width: 1px;");
        mailBody.append("\n\t\tpadding: 8px;");
        mailBody.append("\n\t\tborder-style: solid;");
        mailBody.append("\n\t\tborder-color: #666666;");
        mailBody.append("\n\t\tbackground-color: #ffffff;");
        mailBody.append("\n\t}");
        mailBody.append("\n\t</style>");
        mailBody.append("</head>");
        mailBody.append("\n\t<body>");
        mailBody.append("\n\t\t<form id=\"Form\" name=\"FormConciliacion\" >");
        mailBody.append("	<b> C.").append(nombreCompleto).append("</b>");
        mailBody.append("	<br>");
        mailBody.append("	<b>").append(puesto).append("</b>");
        mailBody.append("	<br />");
        mailBody.append("	<p>");
        mailBody.append("		Se notifica que la siguiente Conciliación Bancaria fue <b>Rechazado</b>:");
        mailBody.append("	</p>");
        mailBody.append("	<table>");
        mailBody.append("		<thead>");
        mailBody.append("			<tr>");
        mailBody.append("				<th>ID</th>");
        mailBody.append("				<th>Clabe interbancaria</th>");
        mailBody.append("				<th>Mes</th>");
        mailBody.append("			</tr>");
        mailBody.append("		</thead>");
        mailBody.append("		<tbody>");
        mailBody.append("<tr>");
        mailBody.append("\n<td>" + folio + "</td>");
        mailBody.append("\n<td>" + getClaveBanco() + "</td>");
        mailBody.append("\n<td>" + Util.nombreDeMes(getMes()) + "</td>");
        mailBody.append("</tr>");
        mailBody.append("		</tbody>");
        mailBody.append("	</table>");
        mailBody.append("	<br />");
        mailBody.append("	<p>");
        mailBody.append("		Debido a:<br>");
        mailBody.append("	</p>");
        mailBody.append("	<pre>");
        mailBody.append(getMotivoRechazo());
        mailBody.append("	</pre>");
        mailBody.append("	<br />");
        mailBody.append("	<br />");
        mailBody.append("\n<b>Para los fines que considere convenientes.</b>");
        mailBody.append("	<br />");
        mailBody.append("	<br />");
        mailBody.append("	<p>");
        mailBody.append("		Notificaciones Automaticas<br />Sistema de Administracion Integral<br />" + Util.getToday());
        mailBody.append("	</p>");
        mailBody.append("	</form>");
        mailBody.append("</body>");
        mailBody.append("</html>");
        return mailBody.toString();
    }

    public String getCuerpoCorreoTermina(Connection conn) throws Exception {
        String nombreCompleto = getElaboraNombre(conn);
        String puesto = getElaboraPuesto(conn);
        int folio = getIdConciliacion();
        StringBuilder mailBody = new StringBuilder();
        mailBody.append("<html>");
        mailBody.append("\n\t<head>");
        mailBody.append("\n\t<meta charset=\"UTF-8\">");
        mailBody.append("\n\t<style type=\"text/css\">");
        mailBody.append("\n\tbody {");
        mailBody.append("\n\t\t	font-family: verdana, arial, sans-serif;");
        mailBody.append("\n\t\t	font-size: 12px;");
        mailBody.append("\n\t}");
        mailBody.append("\n\ttable {");
        mailBody.append("\n\t\tfont-size: 12px;");
        mailBody.append("\n\t\tcolor: #333333;");
        mailBody.append("\n\t\tborder-width: 1px;");
        mailBody.append("\n\t\tborder-color: #666666;");
        mailBody.append("\n\t\tborder-collapse: collapse;");
        mailBody.append("\n\t}");
        mailBody.append("\n\ttable th {");
        mailBody.append("\n\t\tborder-width: 1px;");
        mailBody.append("\n\t\tpadding: 8px;");
        mailBody.append("\n\t\tborder-style: solid;");
        mailBody.append("\n\t\tborder-color: #666666;");
        mailBody.append("\n\t\tbackground-color: #dedede;");
        mailBody.append("\n\t}");
        mailBody.append("\n\ttable td {");
        mailBody.append("\n\t\tborder-width: 1px;");
        mailBody.append("\n\t\tpadding: 8px;");
        mailBody.append("\n\t\tborder-style: solid;");
        mailBody.append("\n\t\tborder-color: #666666;");
        mailBody.append("\n\t\tbackground-color: #ffffff;");
        mailBody.append("\n\t}");
        mailBody.append("\n\t</style>");
        mailBody.append("</head>");
        mailBody.append("\n\t<body>");
        mailBody.append("\n\t\t<form id=\"Form\" name=\"FormConciliacion\" >");
        mailBody.append("	<b> C.").append(nombreCompleto).append("</b>");
        mailBody.append("	<br>");
        mailBody.append("	<b>").append(puesto).append("</b>");
        mailBody.append("	<br />");
        mailBody.append("	<p>");
        mailBody.append("		Se notifica que el flujo de autorizacion de la conciliación bancaria a terminado exitosamente:");
        mailBody.append("	</p>");
        mailBody.append("	<table>");
        mailBody.append("		<thead>");
        mailBody.append("			<tr>");
        mailBody.append("				<th>ID</th>");
        mailBody.append("				<th>Estado Financiero</th>");
        mailBody.append("				<th>Mes</th>");
        mailBody.append("			</tr>");
        mailBody.append("		</thead>");
        mailBody.append("		<tbody>");
        mailBody.append("<tr>");
        mailBody.append("\n<td>" + folio + "</td>");
        mailBody.append("\n<td>" + getClaveBanco() + "</td>");
        mailBody.append("\n<td>" + Util.nombreDeMes(getMes()) + "</td>");
        mailBody.append("</tr>");
        mailBody.append("		</tbody>");
        mailBody.append("	</table>");
        mailBody.append("	<br />");
        mailBody.append("	<br />");
        mailBody.append("	<br />");
        mailBody.append("	<p>");
        mailBody.append("		Notificaciones Automaticas<br />Sistema de Administracion Integral<br />" + Util.getToday());
        mailBody.append("	</p>");
        mailBody.append("	</form>");
        mailBody.append("</body>");
        mailBody.append("</html>");
        return mailBody.toString();
    }

    @Override
    public String getCuerpoCorreoVistoBueno(Connection conn) throws Exception {
        throw new Exception("getCuerpoCorreoVistoBueno No implementado");
    }

    public String getDescripcionFirmante() {
        return descripcionFirmante;
    }

    private String getElaboraNombre(Connection conn) throws Exception {
        StringBuilder queryNombre = new StringBuilder();
        queryNombre.append("SELECT	nombreFirmante ");
        queryNombre.append("  FROM	vFirmanteReporteConciliacion ");
        queryNombre.append(" WHERE	nConciliacion = ? ");
        queryNombre.append("   AND	nOrden = 1");
        PreparedStatement psNombre = null;
        ResultSet rsNombre = null;
        try {
            psNombre = conn.prepareStatement(queryNombre.toString());
            psNombre.setInt(1, getIdConciliacion());
            rsNombre = psNombre.executeQuery();
            if (rsNombre.next()) {
                return rsNombre.getString("nombreFirmante");
            } else
                throw new Exception("No se encontro nombre de empleado en la conciliación: " + getIdConciliacion());
        } finally {
            CloseObject.closeObject(psNombre);
            CloseObject.closeObject(rsNombre);
        }
    }

    private String getElaboraPuesto(Connection conn) throws Exception {
        StringBuilder queryNombre = new StringBuilder();
        queryNombre.append("SELECT	Cargo ");
        queryNombre.append("  FROM	vFirmanteReporteConciliacion ");
        queryNombre.append(" WHERE	nConciliacion = ? ");
        queryNombre.append("   AND	nOrden = 1");
        PreparedStatement psNombre = null;
        ResultSet rsNombre = null;
        try {
            psNombre = conn.prepareStatement(queryNombre.toString());
            psNombre.setInt(1, getIdConciliacion());
            rsNombre = psNombre.executeQuery();
            if (rsNombre.next()) {
                return rsNombre.getString("Cargo");
            } else
                throw new Exception("No se encontro cargo de empleado en la conciliación bancaria: " + getIdConciliacion());
        } finally {
            CloseObject.closeObject(psNombre);
            CloseObject.closeObject(rsNombre);
        }
    }

    public int getIdTipoFirmante() {
        return idTipoFirmante;
    }

    @Override
    public String getImporteStr(Connection conn) throws Exception {
        throw new Exception("getImporteStr No implementado");
    }

    public String getLeyendaFirma() {
        return leyendaFirma;
    }

    public String getLeyendaFirmante(Connection conn, int nOrden) throws Exception {
        StringBuilder query = new StringBuilder();
        query.append("SELECT	cDescTipoFirmante ");
        query.append("  FROM	vFirmanteReporteConciliacion WITH(NOLOCK) ");
        query.append(" WHERE	nConciliacion = ? ");
        query.append("   AND	nOrden = ? ");
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, getIdConciliacion());
            ps.setInt(2, nOrden);
            rs = ps.executeQuery();
            if (rs.next())
                return rs.getString(1);
            else
                throw new Exception("No fue posible encontrar el cConcepto para la conciación " + getIdConciliacion() + " en el orden " + nOrden);
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public String getNombreFirmante() {
        return nombreFirmante;
    }

    private int getNumEmpleadoElabora(Connection conn) throws Exception {
        StringBuilder query = new StringBuilder();
        query.append("SELECT	cNumeroEmpleado ");
        query.append("  FROM	tFirmanteConciliacion WITH(NOLOCK) ");
        query.append(" WHERE	nConciliacion = ? ");
        query.append("   AND	nOrden = 1");
        PreparedStatement ps = null;
        ResultSet rs = null;
        int numeroEmpleado = -1;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, getIdConciliacion());
            rs = ps.executeQuery();
            if (rs.next())
                numeroEmpleado = rs.getInt(1);
            return numeroEmpleado;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public int getNumeroEmpleadoFirmante() {
        return numeroEmpleadoFirmante;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public String getReportFilePath() {
        return reportFilePath;
    }

    public String getReportName() {
        return reportName;
    }

    public String getRutaAcuseImpreso() {
        return this.rutaAcuseImpreso;
    }

    public String getRutaReporteImpreso(Connection conn) throws Exception {
        StringBuilder query = new StringBuilder();
        query.append("SELECT	cRutaArchivo ");
        query.append("  FROM	tConciliacionFirmaElectronica WITH(NOLOCK) ");
        query.append(" WHERE	nConciliacion = ? and cEstatus = 'E'");
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, getIdConciliacion());
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString(1);
            } else
                throw new Exception("NO se encontro archivo para firmar con el folio: " + getIdConciliacion());
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    public String getUnidadFirmante() {
        return unidadFirmante;
    }

    @Override
    public String getVoBoLegend(Connection conn) throws Exception {
        throw new Exception("getVoBoLegend No implementado");
    }

    public void notificaCancelacion(Connection conn) throws Exception {
        String subject = "Conciliación bancaria con Folio: " + getIdConciliacion() + " rechazado";
        String correoElabora = getCorreoElabora(conn);
        AlarmaManager.procesaAlarmaCNF(conn, "", null, null, subject, correoElabora, getCuerpoCorreoRechazo(conn));
    }

    @Override
    public void notificaOperacionMasivaPendiente(Connection conn, String operacion) throws Exception {
        throw new Exception("No implementado");
    }

    @Override
    public String notificaOperacionPendiente(Connection conn, String tipoAutorizacionS) throws Exception {
        boolean firmanteReporteSiguiente = firmanteReporteSiguiente(conn, this);
        if (firmanteReporteSiguiente) {
            int tipoAutorizacion = Integer.parseInt(tipoAutorizacionS);
            if (ELABORA_REPORTE == tipoAutorizacion || ELABORA_REVISA_REPORTE == tipoAutorizacion) {
                String subject = "Solicitud de Firma de Elaboracion de Conciliación Bancaria";
                String correoElabora = getCorreoElabora(conn);
                AlarmaManager.procesaAlarmaCNF(conn, "", null, null, subject, correoElabora, getCuerpoCorreoElabora(conn));
            } else {
                AlarmaManager.procesaAlarmaCNF(conn, "", null, null, "Solicitud de " + getLeyendaFirmante(conn, getOrden()) + " para la Conciliación bancaria", getCorreoAutoriza(conn), getCuerpoCorreoAutoriza(conn));
            }
        } else {
            String subject = "Conciliación bancaria con Folio: " + getIdConciliacion() + " firmada completamente";
            String correoElabora = getCorreoElabora(conn);
            AlarmaManager.procesaAlarmaCNF(conn, "", null, null, subject, correoElabora, getCuerpoCorreoTermina(conn));
        }
        return "success";
    }

    public static boolean firmanteReporteSiguiente(Connection conn, ConciliacionFirma cfirma) throws SQLException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT	COUNT(*) ");
        query.append("  FROM	tFirmanteConciliacion FIRMANTE WITH(NOLOCK) ");
        query.append(" WHERE	nConciliacion = ? ");
        query.append("   AND	cEstatus='E'");
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, cfirma.getIdConciliacion());
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            } else
                return false;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    @Override
    public void onCancelaTramite(Connection conn, String reason) throws Exception {
        throw new Exception("onCancelaTramite No implementado");
    }

    @Override
    public void onFinishAut(Connection conn) throws Exception {
        throw new Exception("onFinishAut No implementado");
    }

    @Override
    public void onFinishVoBo(Connection conn) throws Exception {
    }

    public int registraConciliacion(Connection conn) throws Exception {
        int id = ReporteAvanceFinancieroManager.insertaConciliacionFIEL(conn, this);
        return id;
    }

    private void setCargoFirmante(String cargoFirmante) {
        this.cargoFirmante = cargoFirmante;
    }

    private void setDescripcionFirmante(String descripcionFirmante) {
        this.descripcionFirmante = descripcionFirmante;
    }

    private void setIdTipoFirmante(int idTipoFirmante) {
        this.idTipoFirmante = idTipoFirmante;
    }

    private void setLeyendaFirma(String leyendaFirma) {
        this.leyendaFirma = leyendaFirma;
    }

    private void setNombreFirmante(String nombreFirmante) {
        this.nombreFirmante = nombreFirmante;
    }

    private void setNumeroEmpleadoFirmante(int numeroEmpleadoFirmante) {
        this.numeroEmpleadoFirmante = numeroEmpleadoFirmante;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    public void setReportFilePath(String reportFilePath) {
        this.reportFilePath = reportFilePath;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public void setRutaAcuseImpreso(String rutaAcuseImpreso) {
        this.rutaAcuseImpreso = rutaAcuseImpreso;
    }

    private void setUnidadFirmante(String unidadFirmante) {
        this.unidadFirmante = unidadFirmante;
    }

    @Override
    public void onGeneraArchivosMasivo(Connection conn) {
        // TODO Auto-generated method stub
    }

    @Override
    public String generaArchivoInformeComision(Connection conn, Volumen vol, Carpeta folder, boolean isSignedCopy) throws FirmaElectronicaException {
        // TODO Auto-generated method stub
        return null;
    }

    public String getVoBoLegendWithName(Connection conn, String nombre, String puesto) throws Exception {
        String voLegend = VO_BO_LEGEND_PREFIX.concat(" Firmado por: ").concat(nombre).concat(" | ").concat(puesto);
        if (tieneDelegatorioVoBO(conn)) {
            voLegend = VO_BO_LEGEND_PREFIX + " Firma " + getTipoSuplencia() + " de " + getNombreEmpleadoSuplido() + " con fundamento en el oficio: " + getFolioOficioVoBo() + " de fecha: " + getFechaOficioVoBo();
            log.info(voLegend);
        }
        return voLegend;
    }

    @Override
    public String getAutLegendWithName(Connection conn, String nombre, String puesto) {
        try {
            String autLegend = AUT_LEGEND_PREFIX.concat(" Firmado por: ").concat(nombre).concat(" | ").concat(puesto);
            if (tieneDelegatorioAut(conn)) {
                autLegend = AUT_LEGEND_PREFIX + ". Firma " + getTipoSuplenciaAut() + " de " + getNombreEmpleadoSuplidoAut() + " con fundamento en el oficio: " + getFolioOficioAut() + " de fecha: " + getFechaOficioAut();
                log.info(autLegend);
            }
            return autLegend;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public int getIdConciliacion() {
        return idConciliacion;
    }

    public void setIdConciliacion(int idConciliacion) {
        this.idConciliacion = idConciliacion;
    }

    public String getClaveBanco() {
        return claveBanco;
    }

    public void setClaveBanco(String claveBanco) {
        this.claveBanco = claveBanco;
    }

    @Override
    public boolean registraFirmantes(Connection conn) throws Exception {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public String notificaPrefirmante(Connection conn) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }
}
