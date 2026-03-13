 package com.axtel.sai.sicove.services.impl;


import java.sql.Connection;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.axtel.contratos.entities.FuelProvisioningRequest;
import com.axtel.sai.sicove.entities.RequestAuthChain;
import com.axtel.sai.sicove.exceptions.SicoveException;
import com.axtel.sai.sicove.repositories.FuelingNotificatorRepository;
import com.axtel.sai.sicove.services.FuelAccountNotificatorService;
import com.syc.cfdi.db.CloseObject;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.core.AlarmaManager;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.core.UsuarioManager;
import com.syc.gestion.util.Util;
import com.syc.obrapublica.core.ConfiguraAplicativoManager;


public class MailFuelAccountNotificatorService extends DataSourceManager implements FuelAccountNotificatorService {

	private static final Logger				log	= LogManager.getLogger( MailFuelAccountNotificatorService.class );
	private String							processName;
	private FuelingNotificatorRepository	fuelingNotificatorRepository;

	public MailFuelAccountNotificatorService( String jniName, String processName, FuelingNotificatorRepository fuelingNotificatorRepository ) {
		super.init( jniName );
		this.processName = processName;
		this.fuelingNotificatorRepository = fuelingNotificatorRepository;
	}

	@Override
	public boolean sendPendingAuthNotification( FuelProvisioningRequest request ) throws SicoveException {
		Connection conn = null;
		boolean success = false;
		try {
			RequestAuthChain requestAuthChain = getNotificationChain( request );
			String notificationBody = generateNotificationBody( request, requestAuthChain );
			conn = getConnection();
			String authCC = ConfiguraAplicativoManager.getSystemSetting( conn, "FUEL_ACCOUNT_AUTH_CC" );
			AlarmaManager.procesaAlarmaCNF( conn, null, null, null, "Solicitud de combustible en cuenta " + requestAuthChain.getAccountNumber() + " pendiente de autorizar.", 
					requestAuthChain.getAuthorizerMail() + (authCC != null?( ";"+ authCC) :"" ) , 
					notificationBody );
			success = true;
		} catch ( Exception e ) {
			log.error( e.toString(), e );
		}finally {
			CloseObject.closeObject( conn );
		}

		return success;

	}

	private RequestAuthChain getNotificationChain( FuelProvisioningRequest request ) throws SicoveException {
		Connection conn = null;
		try {
			conn = getConnection();
			RequestAuthChain requestAuthChain = fuelingNotificatorRepository.getRequestAuthChain( conn, request.getFuelProvisioningRequestId() );
			Usuario u = new Usuario( request.getUserRequest() );
			u = UsuarioManager.select( conn, u );
			requestAuthChain.setInitiatingUser( u );
			return requestAuthChain;
		} catch ( Exception e ) {
			log.error( "Problemas obteniendo cadenas de autorizacion " + e, e );
			throw new SicoveException( e );
		} finally {
			CloseObject.closeObject( conn );
		}

	}

	@Override
	public String generateAuthNotificationBody( FuelProvisioningRequest request, RequestAuthChain requestAuthChain ) throws SicoveException {
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
			mailBody += "		Se hace de su conocimiento que fue autorizada la siguiente solicitud de abastecimiento de combustible en su cuenta:";
			mailBody += "	</p>";

			mailBody += "	<table>";
			mailBody += "		<thead>";
			mailBody += "			<tr>";
			mailBody += "				<th>Cuenta</th>";
			mailBody += "				<th>Motivo</th>";
			mailBody += "				<th>Monto Solicitado</th>";
			mailBody += "				<th>Monto Autorizado</th>";
			mailBody += "			</tr>";
			mailBody += "		</thead>";
			mailBody += "		<tbody>";

			mailBody += "<tr>";
			mailBody += "\n<td>" + requestAuthChain.getAccountNumber() + "</td>";
			mailBody += "\n<td>" + request.getRequestJustification() + "</td>";
			mailBody += "\n<td>" + request.getRequestAmount() + "</td>";
			mailBody += "\n<td>" + request.getAutorizedAmount() + "</td>";
			mailBody += "</tr>";

			mailBody += "		</tbody>";
			mailBody += "	</table>";
			mailBody += "	<br />";
			mailBody += "	<p>";
			mailBody += "		Notificaciones Automaticas<br />Sistema de Administracion Integral<br />" + Util.getToday();
			mailBody += "	</p>";
			mailBody += "	</form>";
			mailBody += "</body>";
			mailBody += "</html>";

			return mailBody;

		} catch ( Exception e ) {
			throw new SicoveException( e );
		} finally {
			CloseObject.closeObject( conn );
		}
	}

	@Override
	public String generateNotificationBody( FuelProvisioningRequest request, RequestAuthChain requestAuthChain ) throws SicoveException {
		Connection conn = null;
		try {

			conn = getConnection();

			String urlAutorizacion = ConfiguraAplicativoManager.getSystemSetting( conn, "URL_SAI" ) + "/sicove/AutAccountFueling";

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
			mailBody += "		Se solicita de su autorización de la siguiente solicitud de abastecimiento en cuenta:";
			mailBody += "	</p>";

			mailBody += "	<table>";
			mailBody += "		<thead>";
			mailBody += "			<tr>";
			mailBody += "				<th>Solicitante</th>";
			mailBody += "				<th>Cuenta</th>";
			mailBody += "				<th>Motivo</th>";
			mailBody += "				<th>Monto Solicitado</th>";
			mailBody += "			</tr>";
			mailBody += "		</thead>";
			mailBody += "		<tbody>";

			mailBody += "<tr>";
			mailBody += "\n<td>" + requestAuthChain.getApplicantName() + " - " + requestAuthChain.getApplicantPosition() + "</td>";
			mailBody += "\n<td>" + requestAuthChain.getAccountNumber() + "</td>";
			mailBody += "\n<td>" + request.getRequestJustification() + "</td>";
			mailBody += "\n<td>" + request.getRequestAmount() + "</td>";
			mailBody += "</tr>";

			mailBody += "		</tbody>";
			mailBody += "	</table>";
			mailBody += "	<br />";
			mailBody += "	<br />";
			mailBody += "\n<b>Para autorizar o rechazar esta solicitud por favor de click <a href=\"" + urlAutorizacion + generateLinkToken( requestAuthChain.getAuthorizerEmployeeNumber(), processName, request.getFuelProvisioningRequestId() ) + "\" > aquí </a>.</b>";
			mailBody += "	<br />";
			mailBody += "	<br />";
			mailBody += "	<p>";
			mailBody += "		Notificaciones Automaticas<br />Sistema de Administracion Integral<br />" + Util.getToday();
			mailBody += "	</p>";
			mailBody += "	</form>";
			mailBody += "</body>";
			mailBody += "</html>";

			return mailBody;

		} catch ( Exception e ) {
			throw new SicoveException( e );
		} finally {
			CloseObject.closeObject( conn );
		}
	}

	private String generateLinkToken( int employeeNumber, String processName, int id ) {

		StringBuffer parametrosReales = null;
		parametrosReales = new StringBuffer( "?" );
		parametrosReales.append( "u=" ).append( StringUtils.reverse( String.valueOf( employeeNumber ) ) );
		parametrosReales.append( "&" );
		parametrosReales.append( "d=" ).append( String.valueOf( processName ) );
		parametrosReales.append( "&" );
		parametrosReales.append( "f=" ).append( StringUtils.reverse( String.valueOf( id ) ) );
		log.debug( "Cadena generada: " + parametrosReales );

		return parametrosReales.toString();

	}

	@Override
	public boolean sendAuthNotification( FuelProvisioningRequest fuelRequest ) throws SicoveException {
		Connection conn = null;
		boolean success = false;
		try {
			RequestAuthChain requestAuthChain = getNotificationChain( fuelRequest );
			String notificationBody = generateAuthNotificationBody( fuelRequest, requestAuthChain );
			conn = getConnection();
			String to = requestAuthChain.getApplicantMail() + ( requestAuthChain.getInitiatingUser() != null ? ";" + requestAuthChain.getInitiatingUser().getU_email() : "" );
			AlarmaManager.procesaAlarmaCNF( conn,null, null, null,  "Solicitud de combustible en cuenta " + requestAuthChain.getAccountNumber() + " autorizada", to, notificationBody );
			success = true;
		} catch ( Exception e ) {
			log.error( e.toString(), e );
		}finally {
			CloseObject.closeObject( conn );
		}

		return success;
	}

	@Override
	public boolean notifyRejection( FuelProvisioningRequest fuelRequest ) {
		
		log.trace( "[notifyRejection]Initializing reject notification" );
		Connection conn = null;
		boolean success = false;
		log.trace( "[notifyRejection]Objects connection and success declarated" );
		try {
			log.trace( "[notifyRejection] Gettin notification chain" );
			RequestAuthChain requestAuthChain = getNotificationChain( fuelRequest );
			log.trace( "[notifyRejection] notification chain obtained \n[" +  requestAuthChain + "]\n");
			
			log.trace( "[notifyRejection] Gettin notification body" );
			String notificationBody = generateRejectNotificationBody( fuelRequest, requestAuthChain );
			log.trace( "[notifyRejection] notification body obtained \n[" +  notificationBody + "]\n");
			
			log.trace( "[notifyRejection] Gettin connection" );
			conn = getConnection();
			log.trace( "[notifyRejection] connection obtained \n[" +  conn + "]\n");
			
			log.trace( "[notifyRejection] Gettin TO list" );
			String to = requestAuthChain.getApplicantMail() + ( requestAuthChain.getInitiatingUser() != null ? ";" + requestAuthChain.getInitiatingUser().getU_email() : "" );
			log.trace( "[notifyRejection] notification list \n[" +  to + "]\n");
			
			log.trace( "[notifyRejection] Gettin notification chain" );
			AlarmaManager.procesaAlarmaCNF( conn, null, null, null, "Solicitud de combustible en tarjeta " + requestAuthChain.getAccountNumber() + " rechazada", to, notificationBody );
			log.trace( "[notifyRejection] notification chain obtained \n[" +  requestAuthChain + "]\n");
			
			success = true;
		} catch ( Exception e ) {
			log.error( e.toString(), e );
		}finally {
			CloseObject.closeObject( conn );
		}

		return success;
	}

	@Override
	public String generateRejectNotificationBody( FuelProvisioningRequest request, RequestAuthChain requestAuthChain ) throws SicoveException {
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
			mailBody += "		Se hace de su conocimiento que fue <b>Rechazada</b> la siguiente solicitud de combustible en cuenta:";
			mailBody += "	</p>";

			mailBody += "	<table>";
			mailBody += "		<thead>";
			mailBody += "			<tr>";
			mailBody += "				<th>Cuenta</th>";
			mailBody += "				<th>Motivo</th>";
			mailBody += "				<th>Monto Solicitado</th>";
			mailBody += "				<th>Monto Autorizado</th>";
			mailBody += "			</tr>";
			mailBody += "		</thead>";
			mailBody += "		<tbody>";

			mailBody += "<tr>";
			mailBody += "\n<td>" + requestAuthChain.getAccountNumber() + "</td>";
			mailBody += "\n<td>" + request.getRequestJustification() + "</td>";
			mailBody += "\n<td>" + request.getRequestAmount() + "</td>";
			mailBody += "\n<td>" + request.getAutorizedAmount() + "</td>";
			mailBody += "</tr>";

			mailBody += "		</tbody>";
			mailBody += "	</table>";
			mailBody += "	<br />";
			mailBody += "	<p>";
			mailBody += "El motivo de rechazo es:<br><br><b>";
			mailBody += StringUtils.trimToEmpty( request.getRejectJustification() ).replace( "\n", "<br>" );
			mailBody += "	</b><br></p>";
			mailBody += "	<br />";
			mailBody += "	<p>";
			mailBody += "		Notificaciones Automaticas<br />Sistema de Administracion Integral<br />" + Util.getToday();
			mailBody += "	</p>";
			mailBody += "	</form>";
			mailBody += "</body>";
			mailBody += "</html>";

			return mailBody;

		} catch ( Exception e ) {
			throw new SicoveException( e );
		} finally {
			CloseObject.closeObject( conn );
		}
	}
}
