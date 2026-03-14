package com.syc.contable.core;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import com.syc.fortimax.core.Carpeta;
import com.syc.fortimax.core.Documento;
import com.syc.fortimax.core.Volumen;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.core.AlarmaManager;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoManager;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.obrapublica.ConfiguraAplicativoBusinessLogic;
import com.syc.obrapublica.core.ConfiguraAplicativoManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import com.syc.sai.firmaElectronica.core.FirmaElectronicaManager;
import com.syc.sai.firmaElectronica.exceptions.FirmaElectronicaException;
import com.syc.sai.firmaElectronica.exceptions.NotEmptyDocumentException;
import com.syc.sai.firmaElectronica.interfaces.SolicitudFirmaElectronica;
import com.syc.solicitudviaticos.core.ComisionSinViaticosManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SolicitudTramiteFirmaElectronica extends SolicitudFirmaElectronica {

    private static final StringBuilder correo;

    private static final Logger log = LoggerFactory.getLogger(SolicitudTramiteFirmaElectronica.class);

    static {
        correo = new StringBuilder();
        correo.append("<html>");
        correo.append("\t<head>");
        correo.append("\t\t<meta charset=\"UTF-8\">");
        correo.append("\t\t<style type=\"text/css\">");
        correo.append("\t\tbody{");
        correo.append("\t\t\tfont-family: verdana, arial, sans-serif;");
        correo.append("\t\t\tfont-size: 12px;");
        correo.append("\t\t}");
        correo.append("\t\t</style>");
        correo.append("\t</head>");
        correo.append("\t<body>");
        correo.append("\t\t<b>%s</b>");
        correo.append("\t\t<br/>");
        correo.append("\t\t<br/> ");
        correo.append("\t\t<p>");
        correo.append("\t\t\tSe hace de su conocimiento que la solicitud con folio: <b>%s</b> fue rechazada por el siguiente motivo:");
        correo.append("\t\t</p>");
        correo.append("\t\t<p>");
        correo.append("\t\t\t<i>%s</i>");
        correo.append("\t\t</p>");
        correo.append("\t\t<p>");
        correo.append("\t\t\tSe sugiere tome las medidas pertinentes.");
        correo.append("\t\t</p>");
        correo.append("\t\t<p>");
        correo.append("\t\t\tNotificaciones Automaticas");
        correo.append("\t\t\t<br />");
        correo.append("\t\t\tSistema de Administracion Integral");
        correo.append("\t\t\t<br />");
        correo.append("\t\t\t%s");
        correo.append("\t\t</p>");
        correo.append("\t</body>");
        correo.append("</html>");
    }

    public SolicitudTramiteFirmaElectronica() {
        super();
        setFolder("Solicitud Firmada");
        setDocument("COMSINVIATICOS");
        setHeader("tComisionesSinComprobacionEnc");
        setDetail("tComisionesSinComprobacionDet");
        setField("nFolioComision");
    }

    @Override
    public String generaArchivoFirma(Connection conn, Volumen vol, Carpeta folder, boolean isSignedCopy) throws FirmaElectronicaException {
        String filename = null;
        String reportName = FirmaElectronicaManager.REPORTES_SOL_PAGO.get((isSignedCopy ? "ACUSE_" : "") + getDocument() + "_FIEL");
        Integer whereFolio = new Integer(getIdField());
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("whereFolio", whereFolio);
        params.put("SUBREPORT_DIR", getReportPath() + File.separatorChar);
        try {
            Documento d = getOrCreateDocument(conn, vol, folder, getDocName(), getFileExtension(), getUsuario().getLogin());
            filename = d.getFullPathFilesNames()[0];
            runReport(conn, getReportPath(), reportName, "", filename, params);
        } catch (NotEmptyDocumentException nede) {
            log.warn("Object: {}", "El documento no esta vacio. Se ignora" + nede);
        } catch (Exception e) {
            throw new FirmaElectronicaException(e);
        }
        return filename;
    }

    @Override
    public String getAutLegend(Connection conn) throws Exception {
        try {
            if (tieneDelegatorioAut(conn)) {
                String voLegend = AUT_LEGEND_PREFIX + " Firma " + getTipoSuplenciaAut() + " de " + getNombreEmpleadoSuplidoAut() + " con fundamento en el oficio: " + getFolioOficioAut() + " de fecha: " + getFechaOficioAut();
                log.info("Object: {}", voLegend);
                return voLegend;
            } else
                return AUT_LEGEND_PREFIX;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getAutLegendWithName(Connection conn, String nombre, String puesto) {
        try {
            String autLegend = AUT_LEGEND_PREFIX.concat(" Firmado por: ").concat(nombre).concat(" | ").concat(puesto);
            if (tieneDelegatorioAut(conn)) {
                autLegend = AUT_LEGEND_PREFIX + ". Firma " + getTipoSuplenciaAut() + " de " + getNombreEmpleadoSuplidoAut() + " con fundamento en el oficio: " + getFolioOficioAut() + " de fecha: " + getFechaOficioAut();
                log.info("Object: {}", autLegend);
            }
            return autLegend;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getConcepto(Connection conn) throws Exception {
        StringBuilder query = new StringBuilder();
        query.append("SELECT	cInformeComision ");
        query.append("  FROM	tComisionesSinComprobacionEnc WITH(NOLOCK) ");
        query.append(" WHERE	nFolioComision=?");
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, getIdField());
            rs = ps.executeQuery();
            if (rs.next())
                return rs.getString(1);
            else
                throw new Exception("No fue posible encontrar el cConcepto para el folio " + getIdField() + " en el documento" + getDocument());
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    @Override
    public String getCuerpoCorreoAutoriza(Connection conn) throws Exception {
        ConfiguraAplicativoBusinessLogic cabl = new ConfiguraAplicativoBusinessLogic(GestionInterface.ATT_CONEXION);
        String urlAutorizacion = cabl.getSystemSetting("URL_SAI") + "/egresos/AutPago";
        String nombreCompleto = getAutNombre(conn);
        String puesto = getAutPuesto(conn);
        String folio = getFolioSAI(conn);
        String concepto = getConcepto(conn);
        String importe = getImporteStr(conn);
        int numeroEmpleadoAut = getNumEmpleadoAutSuplencia(conn);
        int numeroEmpleado = numeroEmpleadoAut > 0 ? numeroEmpleadoAut : getAutNumEmpleado(conn);
        String mailBody = "<html>";
        mailBody += "\n\t<head>";
        mailBody += "\n\t<meta charset=\"UTF-8\">";
        mailBody += "\n\t<style type=\"text/css\">";
        mailBody += "\n\tbody {";
        mailBody += "\n\t\t	font-family: verdana, arial, sans-serif;";
        mailBody += "\n\t\t	font-size: 12px;";
        mailBody += "\n\t}";
        mailBody += "\n\ttable {";
        mailBody += "\n\t\tfont-size: 12px;";
        mailBody += "\n\t\tcolor: #333333;";
        mailBody += "\n\t\tborder-width: 1px;";
        mailBody += "\n\t\tborder-color: #666666;";
        mailBody += "\n\t\tborder-collapse: collapse;";
        mailBody += "\n\t}";
        mailBody += "\n\ttable th {";
        mailBody += "\n\t\tborder-width: 1px;";
        mailBody += "\n\t\tpadding: 8px;";
        mailBody += "\n\t\tborder-style: solid;";
        mailBody += "\n\t\tborder-color: #666666;";
        mailBody += "\n\t\tbackground-color: #dedede;";
        mailBody += "\n\t}";
        mailBody += "\n\ttable td {";
        mailBody += "\n\t\tborder-width: 1px;";
        mailBody += "\n\t\tpadding: 8px;";
        mailBody += "\n\t\tborder-style: solid;";
        mailBody += "\n\t\tborder-color: #666666;";
        mailBody += "\n\t\tbackground-color: #ffffff;";
        mailBody += "\n\t}";
        mailBody += "\n\t</style>";
        mailBody += "</head>";
        mailBody += "\n\t<body>";
        mailBody += "\n\t\t<form id=\"Form\" name=\"FormViaticos\" >";
        mailBody += "	<b> C." + nombreCompleto + "</b>";
        mailBody += "	<br>";
        mailBody += "	<b>" + puesto + "</b>";
        mailBody += "	<br />";
        mailBody += "	<p>";
        mailBody += "		Se solicita de su autorización de la siguiente comisi&oacute;n que no requirio viaticos:";
        mailBody += "	</p>";
        mailBody += "	<table>";
        mailBody += "		<thead>";
        mailBody += "			<tr>";
        mailBody += "				<th>Folio de Solicitud</th>";
        mailBody += "				<th>Concepto</th>";
        mailBody += "				<th>Monto en Vuelos</th>";
        mailBody += "			</tr>";
        mailBody += "		</thead>";
        mailBody += "		<tbody>";
        mailBody += "<tr>";
        mailBody += "\n<td>" + folio + "</td>";
        mailBody += "\n<td>" + concepto + "</td>";
        mailBody += "\n<td>" + importe + "</td>";
        mailBody += "</tr>";
        mailBody += "		</tbody>";
        mailBody += "	</table>";
        mailBody += "	<br />";
        mailBody += "	<br />";
        mailBody += "\n<b>Para autorizar o rechazar esta solicitud por favor de click <a href=\"" + urlAutorizacion + generaAccessoAutToken(numeroEmpleado, getDocument(), getIdField()) + "\" > aquí </a>.</b>";
        mailBody += "	<br />";
        mailBody += "	<br />";
        mailBody += "	<p>";
        mailBody += "		Notificaciones Automaticas<br />Sistema de Administracion Integral<br />" + Util.getToday();
        mailBody += "	</p>";
        mailBody += "	</form>";
        mailBody += "</body>";
        mailBody += "</html>";
        return mailBody;
    }

    @Override
    public String getCuerpoCorreoVistoBueno(Connection conn) throws Exception {
        String urlAutorizacion = ConfiguraAplicativoManager.getSystemSetting(conn, "URL_SAI") + "/egresos/VoBoPago";
        String nombreCompleto = getVoBoNombre(conn);
        String puesto = getVoBoPuesto(conn);
        String folio = getFolioSAI(conn);
        String concepto = getConcepto(conn);
        String importe = getImporteStr(conn);
        int numeroEmpleadoVoBo = getNumEmpleadoVistoBueno(conn);
        int numeroEmpleado = numeroEmpleadoVoBo > 0 ? numeroEmpleadoVoBo : getVoBoNumEmpleado(conn);
        String mailBody = "<html>";
        mailBody += "\n\t<head>";
        mailBody += "\n\t<meta charset=\"UTF-8\">";
        mailBody += "\n\t<style type=\"text/css\">";
        mailBody += "\n\tbody {";
        mailBody += "\n\t\t	font-family: verdana, arial, sans-serif;";
        mailBody += "\n\t\t	font-size: 12px;";
        mailBody += "\n\t}";
        mailBody += "\n\ttable {";
        mailBody += "\n\t\tfont-size: 12px;";
        mailBody += "\n\t\tcolor: #333333;";
        mailBody += "\n\t\tborder-width: 1px;";
        mailBody += "\n\t\tborder-color: #666666;";
        mailBody += "\n\t\tborder-collapse: collapse;";
        mailBody += "\n\t}";
        mailBody += "\n\ttable th {";
        mailBody += "\n\t\tborder-width: 1px;";
        mailBody += "\n\t\tpadding: 8px;";
        mailBody += "\n\t\tborder-style: solid;";
        mailBody += "\n\t\tborder-color: #666666;";
        mailBody += "\n\t\tbackground-color: #dedede;";
        mailBody += "\n\t}";
        mailBody += "\n\ttable td {";
        mailBody += "\n\t\tborder-width: 1px;";
        mailBody += "\n\t\tpadding: 8px;";
        mailBody += "\n\t\tborder-style: solid;";
        mailBody += "\n\t\tborder-color: #666666;";
        mailBody += "\n\t\tbackground-color: #ffffff;";
        mailBody += "\n\t}";
        mailBody += "\n\t</style>";
        mailBody += "</head>";
        mailBody += "\n\t<body>";
        mailBody += "\n\t\t<form id=\"Form\" name=\"FormViaticos\" >";
        mailBody += "	<b> C." + nombreCompleto + "</b>";
        mailBody += "	<br>";
        mailBody += "	<b>" + puesto + "</b>";
        mailBody += "	<br />";
        mailBody += "	<p>";
        mailBody += "		Se solicita de su Visto Bueno de la siguiente la siguiente comisi&oacute;n que no requirio viaticos:";
        mailBody += "	</p>";
        mailBody += "	<table>";
        mailBody += "		<thead>";
        mailBody += "			<tr>";
        mailBody += "				<th>Folio de Solicitud</th>";
        mailBody += "				<th>Concepto</th>";
        mailBody += "				<th>Monto</th>";
        mailBody += "			</tr>";
        mailBody += "		</thead>";
        mailBody += "		<tbody>";
        mailBody += "<tr>";
        mailBody += "\n<td>" + folio + "</td>";
        mailBody += "\n<td>" + concepto + "</td>";
        mailBody += "\n<td>" + importe + "</td>";
        mailBody += "</tr>";
        mailBody += "		</tbody>";
        mailBody += "	</table>";
        mailBody += "	<br />";
        mailBody += "	<br />";
        mailBody += "\n<b>Para dar su visto bueno o rechazar esta solicitud por favor de click <a href=\"" + urlAutorizacion + generaAccessoAutToken(numeroEmpleado, getDocument(), getIdField()) + "\" > aquí </a>.</b>";
        mailBody += "	<br />";
        mailBody += "	<br />";
        mailBody += "	<p>";
        mailBody += "		Notificaciones Automaticas<br />Sistema de Administracion Integral<br />" + Util.getToday();
        mailBody += "	</p>";
        mailBody += "	</form>";
        mailBody += "</body>";
        mailBody += "</html>";
        return mailBody;
    }

    @Override
    public String getImporteStr(Connection conn) throws Exception {
        return ComisionSinViaticosManager.getMontoSolicitudStr(conn, getIdField());
    }

    @Override
    public String getVoBoLegend(Connection conn) throws Exception {
        if (tieneDelegatorioVoBO(conn)) {
            String voLegend = VO_BO_LEGEND_PREFIX + " Firma " + getTipoSuplencia() + " de " + getNombreEmpleadoSuplido() + " con fundamento en el oficio: " + getFolioOficioVoBo() + " de fecha: " + getFechaOficioVoBo();
            log.info("Object: {}", voLegend);
            return voLegend;
        } else
            return VO_BO_LEGEND_PREFIX;
    }

    @Override
    public String getVoBoLegendWithName(Connection conn, String nombre, String puesto) throws Exception {
        String voLegend = VO_BO_LEGEND_PREFIX.concat(" Firmado por: ").concat(nombre).concat(" | ").concat(puesto);
        if (tieneDelegatorioVoBO(conn)) {
            voLegend = VO_BO_LEGEND_PREFIX + " Firma " + getTipoSuplencia() + " de " + getNombreEmpleadoSuplido() + " con fundamento en el oficio: " + getFolioOficioVoBo() + " de fecha: " + getFechaOficioVoBo();
            log.info("Object: {}", voLegend);
        }
        return voLegend;
    }

    @Override
    public void notificaOperacionMasivaPendiente(Connection conn, String operacion) throws Exception {
        throw new Exception("No implementado");
    }

    @Override
    public String notificaOperacionPendiente(Connection conn, String tipoAutorizacion) throws Exception {
        if (VO_BO.equals(tipoAutorizacion)) {
            AlarmaManager.procesaAlarmaCNF(conn, "", null, null, "Solicitud de visto bueno para comision sin viaticos.", getCorreoVistoBueno(conn), getCuerpoCorreoVistoBueno(conn));
            FirmaElectronicaManager.avanzaEstatusSICOP(conn, this, -2);
        }
        if (AUTORIZA.equals(tipoAutorizacion)) {
            AlarmaManager.procesaAlarmaCNF(conn, "", null, null, "Solicitud de autorizacion para comision sin viaticos.", getCorreoAutoriza(conn), getCuerpoCorreoAutoriza(conn));
        }
        return "success";
    }

    @Override
    public void onCancelaTramite(Connection conn, String reason) throws Exception {
        log.info("Object: {}", "Procesando cancelacion de tramite. Causa: " + reason);
        StringBuilder query = new StringBuilder();
        query.append(" SELECT	TOP 1 nombreCapturista,  ");
        query.append(" 		folioTramite, ");
        query.append(" 		mailSubject, ");
        query.append(" 		correoCapturista ");
        query.append("   FROM	( ");
        query.append(" 		SELECT	usuario.U_NOMBRE AS nombreCapturista,  ");
        query.append(" 				comision.nFolioComision AS folioTramite, ");
        query.append(" 				'Comision sin comprobacion rechazada' AS mailSubject, ");
        query.append(" 				usuario.U_EMAIL AS correoCapturista ");
        query.append(" 		  FROM	tComisionesSinComprobacionEnc comision WITH(NOLOCK) ");
        query.append(" 				INNER JOIN  ");
        query.append(" 				cg_usuario usuario WITH(nolock) ");
        query.append(" 				ON ");
        query.append(" 				comision.nNumEmpleadoElab = usuario.cNumeroEmpleado ");
        query.append(" 		 WHERE	( comision.nNumEmpleadoElab IS NOT NULL ) ");
        query.append(" 		UNION ");
        query.append(" 		SELECT	usuario.U_NOMBRE AS nombreCapturista,  ");
        query.append(" 				comision.nFolioComision AS folioTramite, ");
        query.append(" 				'Comision sin comprobacion rechazada' AS mailSubject, ");
        query.append(" 				usuario.U_EMAIL AS correoCapturista ");
        query.append(" 		  FROM	tComisionesSinComprobacionEnc comision WITH(NOLOCK) ");
        query.append(" 				INNER JOIN  ");
        query.append(" 				cg_usuario usuario WITH(nolock) ");
        query.append(" 				ON ");
        query.append(" 				comision.cIdUsuarioCaptura = usuario.U_LOGIN ");
        query.append(" 		 WHERE	( comision.nNumEmpleadoElab IS NULL OR comision.nNumEmpleadoElab < 0 ) ");
        query.append(" ) AS elabora ");
        query.append(" WHERE elabora.folioTramite = ? ");
        log.debug("Object: {}", "Se ejecutara: " + query);
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            FirmaElectronicaManager.registraBitacoraCancelacion(conn, reason, this);
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, getIdField());
            rs = ps.executeQuery();
            if (rs.next()) {
                String nombreCapturista = rs.getString("nombreCapturista");
                String folioTramite = rs.getString("folioTramite");
                String mailSubject = rs.getString("mailSubject");
                String correoCapturista = rs.getString("correoCapturista");
                String cuerpoCorreo = String.format(SolicitudTramiteFirmaElectronica.correo.toString(), nombreCapturista, folioTramite, reason, Util.getToday());
                log.debug("Object: {}", "Se encontro usuario elabora: \n" + nombreCapturista + "\n" + correoCapturista);
                log.trace("Object: {}", "Se enviara correo: " + cuerpoCorreo);
                AlarmaManager.procesaAlarmaCNF(conn, "", null, null, mailSubject, correoCapturista, cuerpoCorreo);
            }
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    @Override
    public void onFinishAut(Connection conn) throws Exception {
        log.info("Object: {}", "Terminando la autorizacion del tramite: " + this.getIdField());
        CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
        Caso c = CasoManager.findByFolioLike(conn, getDocument(), String.valueOf(getIdField()));
        Map<String, String> datos = Util.readValuesCasoDato(c.getCasoDato());
        cbl.avanzaCaso(conn, c, getUsuario().getLogin(), "", new String[] { "CONSULTA_COMSINVIATICOS" }, new String[] { "consulta_comsinviaticos" }, datos, null);
    }

    @Override
    public void onFinishVoBo(Connection conn) throws Exception {
        CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
        Caso c = CasoManager.findByFolioLike(conn, getDocument(), String.valueOf(getIdField()));
        Map<String, String> datos = Util.readValuesCasoDato(c.getCasoDato());
        cbl.avanzaCaso(conn, c, getUsuario().getLogin(), "", new String[] { "FIRMA_AUT_FIEL" }, new String[] { "aut_comsinviaticos_fiel" }, datos, null);
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

    @Override
    public String notificaPrefirmante(Connection conn) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }
}
