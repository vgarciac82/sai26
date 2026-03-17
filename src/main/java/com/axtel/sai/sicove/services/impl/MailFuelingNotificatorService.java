package com.axtel.sai.sicove.services.impl;

import java.sql.Connection;
import org.apache.commons.lang.StringUtils;
import com.axtel.sai.sicove.entities.RequestAuthChain;
import com.axtel.sai.sicove.entities.VehicleFuelRequest;
import com.axtel.sai.sicove.exceptions.SicoveException;
import com.axtel.sai.sicove.repositories.FuelingNotificatorRepository;
import com.axtel.sai.sicove.services.FuelingNotificatorService;
import com.syc.cfdi.db.CloseObject;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.core.AlarmaManager;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.core.UsuarioManager;
import com.syc.gestion.util.Util;
import com.syc.obrapublica.core.ConfiguraAplicativoManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class MailFuelingNotificatorService extends DataSourceManager implements FuelingNotificatorService {

    private static final Logger log = LoggerFactory.getLogger(MailFuelingNotificatorService.class);

    private String processName;

    private FuelingNotificatorRepository fuelingNotificatorRepository;

    public MailFuelingNotificatorService(String jniName, String processName, FuelingNotificatorRepository fuelingNotificatorRepository) {
        super.init(jniName);
        this.processName = processName;
        this.fuelingNotificatorRepository = fuelingNotificatorRepository;
    }

    @Override
    public boolean sendPendingAuthNotification(VehicleFuelRequest request) throws SicoveException {
        Connection conn = null;
        boolean success = false;
        try {
            RequestAuthChain requestAuthChain = getNotificationChain(request);
            String notificationBody = generateNotificationBody(request, requestAuthChain);
            conn = getConnection();
            AlarmaManager.procesaAlarmaCNF(conn, null, null, null, "Solicitud de combustible en tarjeta " + request.getWalletNumber() + " pendiente de autorizar.", requestAuthChain.getAuthorizerMail(), notificationBody);
            success = true;
        } catch (Exception e) {
            log.error(e.toString(), e);
        } finally {
            CloseObject.closeObject(conn);
        }
        return success;
    }

    private RequestAuthChain getNotificationChain(VehicleFuelRequest request) throws SicoveException {
        Connection conn = null;
        try {
            conn = getConnection();
            RequestAuthChain requestAuthChain = fuelingNotificatorRepository.getRequestAuthChain(conn, request.getFuelingRequestId());
            Usuario u = new Usuario(request.getUserRequest());
            u = UsuarioManager.select(conn, u);
            requestAuthChain.setInitiatingUser(u);
            return requestAuthChain;
        } catch (Exception e) {
            log.error("Problemas obteniendo cadenas de autorizacion " + e, e);
            throw new SicoveException(e);
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    @Override
    public String generateAuthNotificationBody(VehicleFuelRequest request, RequestAuthChain requestAuthChain) throws SicoveException {
        Connection conn = null;
        try {
            conn = getConnection();
            String applicantName = requestAuthChain.getApplicantName();
            String applicantPosition = requestAuthChain.getApplicantPosition();
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
            mailBody += "	<b> C." + applicantName + "</b>";
            mailBody += "	<br>";
            mailBody += "	<b>" + applicantPosition + "</b>";
            mailBody += "	<br />";
            mailBody += "	<p>";
            mailBody += "		Se hace de su conocimiento que fue autorizada la siguiente solicitud de combustible:";
            mailBody += "	</p>";
            mailBody += "	<table>";
            mailBody += "		<thead>";
            mailBody += "			<tr>";
            mailBody += "				<th>Tarjeta</th>";
            mailBody += "				<th>Motivo</th>";
            mailBody += "				<th>Monto Solicitado</th>";
            mailBody += "				<th>Monto Autorizado</th>";
            mailBody += "			</tr>";
            mailBody += "		</thead>";
            mailBody += "		<tbody>";
            mailBody += "<tr>";
            mailBody += "\n<td>" + request.getWalletNumber() + "</td>";
            mailBody += "\n<td>" + request.getJustification().getJustification() + "</td>";
            mailBody += "\n<td>" + request.getFuelingAmount() + "</td>";
            mailBody += "\n<td>" + request.getAuthorizedAmount() + "</td>";
            mailBody += "</tr>";
            mailBody += "		</tbody>";
            mailBody += "	</table>";
            mailBody += "	<br />";
            mailBody += "	<p>";
            mailBody += "Recuerde que es su obligación el registro de la comprobación de esta asignación, por lo que deberá guardar los comprobantes de carga (tickets) para adjuntarlos como evidencia.";
            mailBody += "	</p>";
            mailBody += "	<br />";
            mailBody += "	<p>";
            mailBody += "		Notificaciones Automaticas<br />Sistema de Administracion Integral<br />" + Util.getToday();
            mailBody += "	</p>";
            mailBody += "	</form>";
            mailBody += "</body>";
            mailBody += "</html>";
            return mailBody;
        } catch (Exception e) {
            throw new SicoveException(e);
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    @Override
    public String generateNotificationBody(VehicleFuelRequest request, RequestAuthChain requestAuthChain) throws SicoveException {
        Connection conn = null;
        try {
            conn = getConnection();
            String urlAutorizacion = ConfiguraAplicativoManager.getSystemSetting(conn, "URL_SAI") + "/sicove/AutFueling";
            String authorizerName = requestAuthChain.getAuthorizerName();
            String authorizerPosition = requestAuthChain.getAuthorizerPosition();
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
            mailBody += "	<b> C." + authorizerName + "</b>";
            mailBody += "	<br>";
            mailBody += "	<b>" + authorizerPosition + "</b>";
            mailBody += "	<br />";
            mailBody += "	<p>";
            mailBody += "		Se solicita de su autorización de la siguiente solicitud de combustible:";
            mailBody += "	</p>";
            mailBody += "	<table>";
            mailBody += "		<thead>";
            mailBody += "			<tr>";
            mailBody += "				<th>Solicitante</th>";
            mailBody += "				<th>Tarjeta</th>";
            mailBody += "				<th>Motivo</th>";
            mailBody += "				<th>Monto Solicitado</th>";
            mailBody += "			</tr>";
            mailBody += "		</thead>";
            mailBody += "		<tbody>";
            mailBody += "<tr>";
            mailBody += "\n<td>" + requestAuthChain.getApplicantName() + " - " + requestAuthChain.getApplicantPosition() + "</td>";
            mailBody += "\n<td>" + request.getWalletNumber() + "</td>";
            mailBody += "\n<td>" + request.getJustification().getJustification() + "</td>";
            mailBody += "\n<td>" + request.getFuelingAmount() + "</td>";
            mailBody += "</tr>";
            mailBody += "		</tbody>";
            mailBody += "	</table>";
            mailBody += "	<br />";
            mailBody += "	<br />";
            mailBody += "\n<b>Para autorizar o rechazar esta solicitud por favor de click <a href=\"" + urlAutorizacion + generateLinkToken(requestAuthChain.getAuthorizerEmployeeNumber(), processName, request.getFuelingRequestId()) + "\" > aquí </a>.</b>";
            mailBody += "	<br />";
            mailBody += "	<br />";
            mailBody += "	<p>";
            mailBody += "		Notificaciones Automaticas<br />Sistema de Administracion Integral<br />" + Util.getToday();
            mailBody += "	</p>";
            mailBody += "	</form>";
            mailBody += "</body>";
            mailBody += "</html>";
            return mailBody;
        } catch (Exception e) {
            throw new SicoveException(e);
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    private String generateLinkToken(int employeeNumber, String processName, int id) {
        StringBuffer parametrosReales = null;
        parametrosReales = new StringBuffer("?");
        parametrosReales.append("u=").append(StringUtils.reverse(String.valueOf(employeeNumber)));
        parametrosReales.append("&");
        parametrosReales.append("d=").append(String.valueOf(processName));
        parametrosReales.append("&");
        parametrosReales.append("f=").append(StringUtils.reverse(String.valueOf(id)));
        log.debug("Object: " + String.valueOf("Cadena generada: " + parametrosReales));
        return parametrosReales.toString();
    }

    @Override
    public boolean sendAuthNotification(VehicleFuelRequest fuelRequest) throws SicoveException {
        Connection conn = null;
        boolean success = false;
        try {
            RequestAuthChain requestAuthChain = getNotificationChain(fuelRequest);
            String notificationBody = generateAuthNotificationBody(fuelRequest, requestAuthChain);
            conn = getConnection();
            String to = requestAuthChain.getApplicantMail() + (requestAuthChain.getInitiatingUser() != null ? ";" + requestAuthChain.getInitiatingUser().getU_email() : "");
            AlarmaManager.procesaAlarmaCNF(conn, null, null, null, "Solicitud de combustible en tarjeta " + fuelRequest.getWalletNumber() + " autorizada", to, notificationBody);
            success = true;
        } catch (Exception e) {
            log.error(e.toString(), e);
        } finally {
            CloseObject.closeObject(conn);
        }
        return success;
    }

    @Override
    public boolean notifyRejection(VehicleFuelRequest fuelRequest) {
        Connection conn = null;
        boolean success = false;
        try {
            RequestAuthChain requestAuthChain = getNotificationChain(fuelRequest);
            String notificationBody = generateRejectNotificationBody(fuelRequest, requestAuthChain);
            conn = getConnection();
            String to = requestAuthChain.getApplicantMail() + (requestAuthChain.getInitiatingUser() != null ? ";" + requestAuthChain.getInitiatingUser().getU_email() : "");
            AlarmaManager.procesaAlarmaCNF(conn, null, null, null, "Solicitud de combustible en tarjeta " + fuelRequest.getWalletNumber() + " rechazada", to, notificationBody);
            success = true;
        } catch (Exception e) {
            log.error(e.toString(), e);
        } finally {
            CloseObject.closeObject(conn);
        }
        return success;
    }

    @Override
    public String generateRejectNotificationBody(VehicleFuelRequest request, RequestAuthChain requestAuthChain) throws SicoveException {
        Connection conn = null;
        try {
            conn = getConnection();
            String applicantName = requestAuthChain.getApplicantName();
            String applicantPosition = requestAuthChain.getApplicantPosition();
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
            mailBody += "	<b> C." + applicantName + "</b>";
            mailBody += "	<br>";
            mailBody += "	<b>" + applicantPosition + "</b>";
            mailBody += "	<br />";
            mailBody += "	<p>";
            mailBody += "		Se hace de su conocimiento que fue <b>Rechazada</b> la siguiente solicitud de combustible:";
            mailBody += "	</p>";
            mailBody += "	<table>";
            mailBody += "		<thead>";
            mailBody += "			<tr>";
            mailBody += "				<th>Tarjeta</th>";
            mailBody += "				<th>Motivo</th>";
            mailBody += "				<th>Monto Solicitado</th>";
            mailBody += "				<th>Monto Autorizado</th>";
            mailBody += "			</tr>";
            mailBody += "		</thead>";
            mailBody += "		<tbody>";
            mailBody += "<tr>";
            mailBody += "\n<td>" + request.getWalletNumber() + "</td>";
            mailBody += "\n<td>" + request.getJustification().getJustification() + "</td>";
            mailBody += "\n<td>" + request.getFuelingAmount() + "</td>";
            mailBody += "\n<td>" + request.getAuthorizedAmount() + "</td>";
            mailBody += "</tr>";
            mailBody += "		</tbody>";
            mailBody += "	</table>";
            mailBody += "	<br />";
            mailBody += "	<p>";
            mailBody += "El motivo de rechazo es:<br><br><b>";
            mailBody += StringUtils.trimToEmpty(request.getRejectJustification()).replace("\n", "<br>");
            mailBody += "	</b><br></p>";
            mailBody += "	<br />";
            mailBody += "	<p>";
            mailBody += "		Notificaciones Automaticas<br />Sistema de Administracion Integral<br />" + Util.getToday();
            mailBody += "	</p>";
            mailBody += "	</form>";
            mailBody += "</body>";
            mailBody += "</html>";
            return mailBody;
        } catch (Exception e) {
            throw new SicoveException(e);
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    @Override
    public boolean sendValidateVerifNotification(VehicleFuelRequest fuelRequest) {
        Connection conn = null;
        boolean success = false;
        try {
            RequestAuthChain requestAuthChain = getNotificationChain(fuelRequest);
            String notificationBody = generateValidationBody(fuelRequest, requestAuthChain);
            conn = getConnection();
            AlarmaManager.procesaAlarmaCNF(conn, null, null, null, "Comprobacion en tarjeta " + fuelRequest.getWalletNumber() + " del usuario " + requestAuthChain.getApplicantName() + " pendiente de autorizar.", requestAuthChain.getAuthorizerMail(), notificationBody);
            success = true;
        } catch (Exception e) {
            log.error(e.toString(), e);
        } finally {
            CloseObject.closeObject(conn);
        }
        return success;
    }

    @Override
    public String generateValidationBody(VehicleFuelRequest request, RequestAuthChain requestAuthChain) throws SicoveException {
        Connection conn = null;
        try {
            conn = getConnection();
            String urlAutorizacion = ConfiguraAplicativoManager.getSystemSetting(conn, "URL_SAI") + "/sicove/ValidateFueling";
            String authorizerName = requestAuthChain.getAuthorizerName();
            String authorizerPosition = requestAuthChain.getAuthorizerPosition();
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
            mailBody += "	<b> C." + authorizerName + "</b>";
            mailBody += "	<br>";
            mailBody += "	<b>" + authorizerPosition + "</b>";
            mailBody += "	<br />";
            mailBody += "	<p>";
            mailBody += "		Se solicita de su validacion de la comprobacion de la siguiente solicitud de combustible:";
            mailBody += "	</p>";
            mailBody += "	<table>";
            mailBody += "		<thead>";
            mailBody += "			<tr>";
            mailBody += "				<th>Solicitante</th>";
            mailBody += "				<th>Tarjeta</th>";
            mailBody += "				<th>Motivo</th>";
            mailBody += "				<th>Monto Solicitado</th>";
            mailBody += "			</tr>";
            mailBody += "		</thead>";
            mailBody += "		<tbody>";
            mailBody += "<tr>";
            mailBody += "\n<td>" + requestAuthChain.getApplicantName() + " - " + requestAuthChain.getApplicantPosition() + "</td>";
            mailBody += "\n<td>" + request.getWalletNumber() + "</td>";
            mailBody += "\n<td>" + request.getJustification().getJustification() + "</td>";
            mailBody += "\n<td>" + request.getFuelingAmount() + "</td>";
            mailBody += "</tr>";
            mailBody += "		</tbody>";
            mailBody += "	</table>";
            mailBody += "	<br />";
            mailBody += "	<br />";
            mailBody += "\n<b>Para autorizar o rechazar esta comprobacion por favor de click <a href=\"" + urlAutorizacion + generateLinkToken(requestAuthChain.getAuthorizerEmployeeNumber(), processName, request.getFuelingRequestId()) + "\" > aquí </a>.</b>";
            mailBody += "	<br />";
            mailBody += "	<br />";
            mailBody += "	<p>";
            mailBody += "		Notificaciones Automaticas<br />Sistema de Administracion Integral<br />" + Util.getToday();
            mailBody += "	</p>";
            mailBody += "	</form>";
            mailBody += "</body>";
            mailBody += "</html>";
            return mailBody;
        } catch (Exception e) {
            throw new SicoveException(e);
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    @Override
    public boolean sendCaptureVerifNotification(VehicleFuelRequest fuelRequest) {
        Connection conn = null;
        boolean success = false;
        try {
            RequestAuthChain requestAuthChain = getNotificationChain(fuelRequest);
            String notificationBody = generateCaptureVerificationBody(fuelRequest, requestAuthChain);
            log.trace("Object: {}", "Cuerpo del correo: \n\n" + notificationBody + "\n\n");
            conn = getConnection();
            AlarmaManager.procesaAlarmaCNF(conn, null, null, null, "Comprobacion en tarjeta " + fuelRequest.getWalletNumber() + " NO aceptada", requestAuthChain.getAuthorizerMail(), notificationBody);
            success = true;
        } catch (Exception e) {
            log.error(e.toString(), e);
        } finally {
            CloseObject.closeObject(conn);
        }
        return success;
    }

    private String generateCaptureVerificationBody(VehicleFuelRequest fuelRequest, RequestAuthChain requestAuthChain) throws SicoveException {
        Connection conn = null;
        try {
            conn = getConnection();
            String urlAutorizacion = ConfiguraAplicativoManager.getSystemSetting(conn, "URL_SAI") + "/sicove/CaptureFueling";
            String applicantName = requestAuthChain.getApplicantName();
            String applicantPosition = requestAuthChain.getApplicantPosition();
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
            mailBody += "	<b> C." + applicantName + "</b>";
            mailBody += "	<br>";
            mailBody += "	<b>" + applicantPosition + "</b>";
            mailBody += "	<br />";
            mailBody += "	<p>";
            mailBody += "		Se hace de su conocimiento que se encontraron detalles en la comprobacion enviada de la tarjeta <b>";
            mailBody += fuelRequest.getWalletNumber();
            mailBody += "</b>, por lo que es necesario realizar la correcion de estos y enviar nuevamente a validacion.";
            mailBody += "	</p>";
            mailBody += "	<br />";
            mailBody += "\n<b>Para revisar el detalle de las observaciones por favor de click <a href=\"" + urlAutorizacion + generateLinkToken(requestAuthChain.getAuthorizerEmployeeNumber(), processName, fuelRequest.getFuelingRequestId()) + "\" > aquí </a>.</b>";
            mailBody += "	<br />";
            mailBody += "	<br />";
            mailBody += "	<p>";
            mailBody += "		Notificaciones Automaticas<br />Sistema de Administracion Integral<br />" + Util.getToday();
            mailBody += "	</p>";
            mailBody += "	</form>";
            mailBody += "</body>";
            mailBody += "</html>";
            return mailBody;
        } catch (Exception e) {
            throw new SicoveException(e);
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    @Override
    public boolean sendAprovedVerifNotification(VehicleFuelRequest fuelRequest) {
        Connection conn = null;
        boolean success = false;
        try {
            RequestAuthChain requestAuthChain = getNotificationChain(fuelRequest);
            String notificationBody = generateAprovedVerifBody(fuelRequest, requestAuthChain);
            log.trace("Object: {}", "Cuerpo del correo: \n\n" + notificationBody + "\n\n");
            conn = getConnection();
            AlarmaManager.procesaAlarmaCNF(conn, null, null, null, "Comprobacion en tarjeta " + fuelRequest.getWalletNumber() + " ACEPTADA", requestAuthChain.getAuthorizerMail(), notificationBody);
            success = true;
        } catch (Exception e) {
            log.error(e.toString(), e);
        } finally {
            CloseObject.closeObject(conn);
        }
        return success;
    }

    private String generateAprovedVerifBody(VehicleFuelRequest fuelRequest, RequestAuthChain requestAuthChain) throws SicoveException {
        Connection conn = null;
        try {
            conn = getConnection();
            String applicantName = requestAuthChain.getApplicantName();
            String applicantPosition = requestAuthChain.getApplicantPosition();
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
            mailBody += "	<b> C." + applicantName + "</b>";
            mailBody += "	<br>";
            mailBody += "	<b>" + applicantPosition + "</b>";
            mailBody += "	<br />";
            mailBody += "	<p>";
            mailBody += "		Se hace de su conocimiento que fue <b>APROBADA</b> la comprobacion enviada de la tarjeta <b>";
            mailBody += fuelRequest.getWalletNumber();
            mailBody += "</b>";
            mailBody += "	</p>";
            mailBody += "	<br />";
            mailBody += "	<br />";
            mailBody += "	<p>";
            mailBody += "		Notificaciones Automaticas<br />Sistema de Administracion Integral<br />" + Util.getToday();
            mailBody += "	</p>";
            mailBody += "	</form>";
            mailBody += "</body>";
            mailBody += "</html>";
            return mailBody;
        } catch (Exception e) {
            throw new SicoveException(e);
        } finally {
            CloseObject.closeObject(conn);
        }
    }
}
