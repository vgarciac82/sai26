package com.syc.contable.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import com.syc.contable.caja.core.CajaManager;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public final class SolicitudCajaFirmaElectronica extends SolicitudFirmaElectronica {

    private static final Logger log = LoggerFactory.getLogger(SolicitudCajaFirmaElectronica.class);

    public SolicitudCajaFirmaElectronica() {
        super();
        setFolder("Documentacion Comprobatoria");
    }

    @Override
    public String generaArchivoFirma(Connection conn, Volumen vol, Carpeta folder, boolean isSignedCopy) throws FirmaElectronicaException {
        String filename = null;
        String reportName = FirmaElectronicaManager.REPORTES_SOL_PAGO.get((isSignedCopy ? "ACUSE_" : "") + getDocument() + "_FIEL");
        String whereFolio = "and ce.nfoliocaja=" + getIdField();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("whereFolio", whereFolio);
        params.put("SUBREPORT_DIR", getReportPath());
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
    public String getAutLegend(Connection conn) {
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
    public String getConcepto(Connection conn) throws Exception {
        String query = "SELECT	cdescripcionpoliza  FROM tcajaencabezado WITH(NOLOCK) WHERE nFolioCaja = ? ";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, getIdField());
            rs = ps.executeQuery();
            if (rs.next())
                return rs.getString(1);
            else
                throw new Exception("No fue posible encontrar el cConcepto para el folio " + getIdField() + " en el documento " + getDocument());
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
        String folio = getFolioCaja(conn);
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
        mailBody += "		Se solicita de su autorización de la siguiente Solicitud No Presupuestal:";
        mailBody += "	</p>";
        mailBody += "	<table>";
        mailBody += "		<thead>";
        mailBody += "			<tr>";
        mailBody += "				<th>Folio de Caja</th>";
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
        // ConfiguraAplicativoBusinessLogic cabl = new
        // ConfiguraAplicativoBusinessLogic(GestionInterface.ATT_CONEXION);
        String urlAutorizacion = ConfiguraAplicativoManager.getSystemSetting(conn, "URL_SAI") + "/egresos/VoBoPago";
        String nombreCompleto = getVoBoNombre(conn);
        String puesto = getVoBoPuesto(conn);
        String folio = getFolioCaja(conn);
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
        mailBody += "		Se solicita de su Visto Bueno de la siguiente Solicitud No Presupuestal:";
        mailBody += "	</p>";
        mailBody += "	<table>";
        mailBody += "		<thead>";
        mailBody += "			<tr>";
        mailBody += "				<th>Folio de Caja</th>";
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

    private String getFolioCaja(Connection conn) throws Exception {
        return CajaManager.getFolioSAI(conn, getIdField());
    }

    @Override
    public String getImporteStr(Connection conn) throws Exception {
        return CajaManager.getMontoCajaStr(conn, getIdField());
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
    public void notificaOperacionMasivaPendiente(Connection conn, String operacion) throws Exception {
        throw new Exception("No implementado");
    }

    @Override
    public String notificaOperacionPendiente(Connection conn, String tipoAutorizacion) throws Exception {
        if (VO_BO.equals(tipoAutorizacion)) {
            AlarmaManager.procesaAlarmaCNF(conn, "", null, null, "Solicitud de visto bueno para Solicitud No Presupuestal", getCorreoVistoBueno(conn), getCuerpoCorreoVistoBueno(conn));
            FirmaElectronicaManager.avanzaEstatusSICOP(conn, this, -2);
        }
        if (AUTORIZA.equals(tipoAutorizacion))
            AlarmaManager.procesaAlarmaCNF(conn, "", null, null, "Solicitud de autorizacion para Solicitud No Presupuestal", getCorreoAutoriza(conn), getCuerpoCorreoAutoriza(conn));
        return "success";
    }

    @Override
    public void onCancelaTramite(Connection conn, String reason) throws Exception {
        String query = "{call sp_informacionCajaFiel(?)} ";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            FirmaElectronicaManager.registraBitacoraCancelacion(conn, reason, this);
            ps = conn.prepareCall(query);
            ps.setInt(1, getIdField());
            rs = ps.executeQuery();
            if (rs.next()) {
                CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
                Caso c = CasoManager.findByFolioLike(conn, getDocument(), String.valueOf(getIdField()));
                Map<String, String> datos = Util.readValuesCasoDato(c.getCasoDato());
                cbl.avanzaCaso(conn, c, getUsuario().getLogin(), "", new String[] { "CONSULTA_CAJA" }, new String[] { "consulta_caja" }, datos, null);
                String nombreCapturista = rs.getString("nombreCapturista");
                String folioTramite = rs.getString("folioTramite");
                String mailSubject = rs.getString("mailSubject");
                String correoCapturista = rs.getString("correoCapturista");
                String cuerpoCorreo = String.format("<html>" + "\t<head>" + "\t\t<meta charset=\"UTF-8\">" + "\t\t<style type=\"text/css\">" + "\t\tbody{" + "\t\t\tfont-family: verdana, arial, sans-serif;" + "\t\t\tfont-size: 12px;" + "\t\t}" + "\t\t</style>" + "\t</head>" + "\t<body>" + "\t\t<b>%s</b>" + "\t\t<br/>" + "\t\t<br/> " + "\t\t<p>" + "\t\t\tSe hace de su conocimiento que la solicitud con folio: <b>%s</b> fue rechazada por el siguiente motivo:" + "\t\t</p>" + "\t\t<p>" + "\t\t\t<i>%s</i>" + "\t\t</p>" + "\t\t<p>" + "\t\t\tSe sugiere tome las medidas pertinentes." + "\t\t</p>" + "\t\t<p>" + "\t\t\tNotificaciones Automaticas" + "\t\t\t<br />" + "\t\t\tSistema de Administracion Integral" + "\t\t\t<br />" + "\t\t\t%s" + "\t\t</p>" + "\t</body>" + "</html>", nombreCapturista, folioTramite, reason, Util.getToday());
                AlarmaManager.procesaAlarmaCNF(conn, "", null, null, mailSubject, correoCapturista, cuerpoCorreo);
                actualizarDocHaplicado(conn, getIdField());
            }
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    private void actualizarDocHaplicado(Connection conn, int idField) throws Exception {
        PreparedStatement pst = null;
        try {
            pst = conn.prepareStatement("UPDATE tcajaencabezado SET cdocumentohaplicado = 'C' WHERE nFoliocaja = ?");
            pst.setInt(1, idField);
            pst.executeUpdate();
        } finally {
            CloseObject.closeObject(pst);
        }
    }

    @Override
    public void onFinishAut(Connection conn) throws Exception {
        // AccountingEngine ae = new AccountingEngine();
        // ae.setValidaInsuficienciaDeSaldo(true);
        // ae.makeAccountingApplication(conn,getDocument(),String.valueOf(
        // getIdField() ),getHeader(),getDetail(),getField());
        //
        CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
        Caso c = CasoManager.findByFolioLike(conn, getDocument(), String.valueOf(getIdField()));
        Map<String, String> datos = Util.readValuesCasoDato(c.getCasoDato());
        cbl.avanzaCaso(conn, c, getUsuario().getLogin(), "", new String[] { "AUTORIZA_CAJA" }, new String[] { "autoriza_caja" }, datos, null);
    }

    @Override
    public void onFinishVoBo(Connection conn) throws Exception {
        CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
        Caso c = CasoManager.findByFolioLike(conn, getDocument(), String.valueOf(getIdField()));
        Map<String, String> datos = Util.readValuesCasoDato(c.getCasoDato());
        cbl.avanzaCaso(conn, c, getUsuario().getLogin(), "", new String[] { "AUT_CAJA_FIEL" }, new String[] { "aut_fiel" }, datos, null);
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
            log.info("Object: {}", voLegend);
        }
        return voLegend;
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
    public String notificaPrefirmante(Connection conn) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }
}
