package com.syc.gestion.core;


import java.io.File;
import java.sql.Connection;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.syc.gestion.BitacoraCorreosBusinessLogic;
import com.syc.gestion.CorreosPendientesBusinessLogic;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.obrapublica.ConfiguraAplicativoBusinessLogic;
import com.syc.obrapublica.core.ConfiguraAplicativoManager;


public class AlarmaManager {

	private static Logger log = Logger.getLogger( AlarmaManager.class );

	public static void procesaAlarmaAttachmentCNF( Connection conn, String prefixPath, CasoOperacion casoOperacion, Caso c, String subject, String to, String body, File solicitud, boolean isProcessAutGRM ) throws Exception {
		procesaAlarmaAttachmentCNF( conn, prefixPath, casoOperacion, c, subject, to, null, null, body, solicitud, isProcessAutGRM );
	}

	public static void procesaAlarmaAttachmentCNF( Connection conn, String prefixPath, CasoOperacion casoOperacion, Caso c, String subject, String to, String cc, String bcc, String body, File solicitud, boolean isProcessAutGRM ) throws Exception {

		boolean correoEnviado = false;
		String asuntoCorreo = subject == null ? "" : subject;
		String errorMsg = "";
		Session session = null;

		ConfiguraAplicativoBusinessLogic cabl = new ConfiguraAplicativoBusinessLogic( GestionInterface.ATT_CONEXION );

		String host = cabl.getSystemSetting( "MAIL;HOST" );
		String protocol = cabl.getSystemSetting( "MAIL;PROTOCOL" );
		String port = cabl.getSystemSetting( "MAIL;PORT" );
		String localhost = cabl.getSystemSetting( "MAIL;LOCALHOST" );
		String from = cabl.getSystemSetting( "MAIL;FROM" );

		boolean esAmbienteDesarrollo = "TRUE".equalsIgnoreCase( cabl.getSystemSetting( "AMBIENTE_DESARROLLO" ) );
		String correosDesarrollo = cabl.getSystemSetting( "CORREO_ALERTAS_DESARROLLO" );

		log.debug( "Cofiguracion de alerta: PROTOCOL[" + protocol + "] HOST[" + host + "] PORT[" + port + "] LOCALHOST[" + localhost + "] FROM[" + from + "]" );

		try {

			if ( esAmbienteDesarrollo ) {
				to = correosDesarrollo;
				cc="";
				bcc="";
			}

			Properties props = new Properties();
			props.put( "mail.transport.protocol", protocol );
			props.put( "mail.smtp.host", host );
			props.put( "mail.smtp.port", port );
			props.put( "mail.smtp.localhost", localhost );
			props.put( "mail.smtp.from", from );
			props.put( "mail.smtp.allow8bitmime", "true" );
			props.put( "mail.debug", "true" );

			session = Session.getInstance( props );
			session.setDebug( true );

			MimeMessage msg = new MimeMessage( session );
			msg.setSubject( asuntoCorreo );

			MimeBodyPart textPart = new MimeBodyPart();
			textPart.setContent( body, "text/html" );

			Multipart mp = new MimeMultipart();
			mp.addBodyPart( textPart );

			msg.setContent( mp );

			String[] destinatarios = to.split( ";" );
			for ( String destinatario : destinatarios ) {
				destinatario.replaceAll( ";", "" );
				msg.addRecipient( Message.RecipientType.TO, new InternetAddress( destinatario ) );
			}

			if ( !StringUtils.isBlank( cc ) ) {
				String[] copies = cc.split( ";" );
				for ( String copy : copies ) {
					copy = copy.replaceAll( ";", "" );
					msg.addRecipient( Message.RecipientType.CC, new InternetAddress( copy ) );
				}
			}

			if ( !StringUtils.isBlank( bcc ) ) {
				String[] copies = bcc.split( ";" );
				for ( String copy : copies ) {
					copy = copy.replaceAll( ";", "" );
					msg.addRecipient( Message.RecipientType.BCC, new InternetAddress( copy ) );
				}
			}

			DataSource source = new FileDataSource( solicitud );
			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setDataHandler( new DataHandler( source ) );
			messageBodyPart.setFileName( solicitud.getName() );
			mp.addBodyPart( messageBodyPart );

			msg.saveChanges();

			Transport.send( msg );

			log.info( "EMAIL TERMINO" );

			/*
			 * Si llega a esta linea es por que no genero alguna excepcion al
			 * enviar el correo.
			 */
			correoEnviado = true;

		} catch ( NoSuchProviderException exc ) {
			log.error( exc, exc );
			exc.printStackTrace( System.out );
			errorMsg = exc.toString();
			throw new GestionException( exc.getMessage() );
		} catch ( MessagingException exc ) {
			log.error( exc, exc );
			exc.printStackTrace( System.out );
			errorMsg = exc.toString();
			throw new GestionException( exc.getMessage() );
		} catch ( Throwable t ) {
			log.error( t, t );
			t.printStackTrace( System.out );
			errorMsg = t.toString();
			throw new GestionException( t.getMessage() );
		} finally {

			session = null;
			/*
			 * Si la bandera correoEnviado es false, ocurrio un error en el
			 * envio, por lo que se debe guardar en DB el correo para su
			 * posterior reenvio.
			 */
			if ( !correoEnviado )
				try {
					CorreosPendientesBean cpb = new CorreosPendientesBean( to, body, asuntoCorreo, errorMsg.length() >= 1024 ? errorMsg.substring( 0, 1023 ) : errorMsg );
					CorreosPendientesBusinessLogic cpbl = new CorreosPendientesBusinessLogic();
					cpbl.insertaAlertaPendiente( cpb );
				} catch ( Exception e ) {
					log.error( "ATENCION!!!\nNo fue posible guardar el correo. Causa: " + e.toString(), e );
				}
		}

	}

	public static void procesaAlarmaCNF( Connection conn, String prefixPath, CasoOperacion casoOperacion, Caso c, String subject, String to, String body ) throws Exception {
		procesaAlarmaCNF( conn, prefixPath, casoOperacion, c, subject, to, null, null, body, false );
	}

	public static void procesaAlarmaCNF( Connection conn, String prefixPath, CasoOperacion casoOperacion, Caso c, String subject, String to, String cc, String bcc, String body ) throws Exception {
		procesaAlarmaCNF( conn, prefixPath, casoOperacion, c, subject, to, cc, bcc, body, false );
	}

	public static void procesaAlarmaCNF( Connection conn, String prefixPath, CasoOperacion casoOperacion, Caso c, String subject, String to, String cc, String bcc, String body, boolean isProcessAutGRM ) throws Exception {
		BitacoraCorreosBusinessLogic bcbl = new BitacoraCorreosBusinessLogic( GestionInterface.ATT_CONEXION );

		BitacoraCorreo bitacoraCorreo = new BitacoraCorreo();
		bitacoraCorreo.setDestinatarios( to );
		bitacoraCorreo.setEstatus( "SUCCESS" );
		bitacoraCorreo.setMensaje( body );
		bitacoraCorreo.setSubject( subject );

		String host = ConfiguraAplicativoManager.getSystemSetting( conn, "MAIL;HOST" );
		String protocol = ConfiguraAplicativoManager.getSystemSetting( conn, "MAIL;PROTOCOL" );
		String port = ConfiguraAplicativoManager.getSystemSetting( conn, "MAIL;PORT" );
		String localhost = ConfiguraAplicativoManager.getSystemSetting( conn, "MAIL;LOCALHOST" );
		String from = ConfiguraAplicativoManager.getSystemSetting( conn, "MAIL;FROM" );
		int timeOut = Integer.parseInt( ConfiguraAplicativoManager.getSystemSetting( conn, "MAIL;TIMEOUT" ) );

		final String user = ConfiguraAplicativoManager.getSystemSetting( conn, "MAIL;USER" );
		final String password = ConfiguraAplicativoManager.getSystemSetting( conn, "MAIL;PASS" );

		boolean esAmbienteDesarrollo = "TRUE".equalsIgnoreCase( ConfiguraAplicativoManager.getSystemSetting( conn, "AMBIENTE_DESARROLLO" ) );
		String correosDesarrollo = ConfiguraAplicativoManager.getSystemSetting( conn, "CORREO_ALERTAS_DESARROLLO" );
		if ( esAmbienteDesarrollo ) {
			to = correosDesarrollo;
			cc="";
			bcc="";
		}
		log.debug( "Cofiguracion de alerta: PROTOCOL[" + protocol + "] HOST[" + host + "] PORT[" + port + "] LOCALHOST[" + localhost + "] FROM[" + from + "]" );

		Session session;
		String errorMsg = "";
		boolean correoEnviado = false;
		try {

			Properties props = new Properties();
			props.put( "mail.transport.protocol", protocol );
			props.put( "mail.smtp.host", host );
			props.put( "mail.smtp.port", port );
			props.put( "mail.smtp.localhost", localhost );
			props.put( "mail.smtp.from", from );
			props.put( "mail.smtp.allow8bitmime", "true" );
			props.put( "mail.smtp.auth", "true" ); // Enabling SMTP
													// Authentication
			props.put( "mail.debug", "true" );
			props.put( "mail.smtp.timeout", timeOut );

			Authenticator auth = new Authenticator() {

				// override the getPasswordAuthentication method
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication( user, password );
				}
			};

			session = Session.getDefaultInstance( props, auth );
			session.setDebug( true );

			MimeMessage msg = new MimeMessage( session );

			if ( StringUtils.isEmpty( subject ) )
				subject = c.getTipoCaso().getDescripcion() + " " + c.getFolio();

			msg.setSubject( subject );

			MimeBodyPart textPart = new MimeBodyPart();
			textPart.setContent( body, "text/html" );

			Multipart mp = new MimeMultipart();
			mp.addBodyPart( textPart );

			msg.setContent( mp );

			String[] destinatarios = to.split( ";" );
			for ( String destinatario : destinatarios ) {
				destinatario = destinatario.replaceAll( ";", "" );
				msg.addRecipient( Message.RecipientType.TO, new InternetAddress( destinatario ) );
			}

			if ( !StringUtils.isBlank( cc ) ) {
				String[] copies = cc.split( ";" );
				for ( String copy : copies ) {
					copy = copy.replaceAll( ";", "" );
					msg.addRecipient( Message.RecipientType.CC, new InternetAddress( copy ) );
				}
			}

			if ( !StringUtils.isBlank( bcc ) ) {
				String[] copies = bcc.split( ";" );
				for ( String copy : copies ) {
					copy = copy.replaceAll( ";", "" );
					msg.addRecipient( Message.RecipientType.BCC, new InternetAddress( copy ) );
				}
			}

			msg.saveChanges();

			Transport.send( msg );

			log.info( "EMAIL TERMINO" );

			/*
			 * Si llega a esta linea es por que no genero alguna excepcion al
			 * enviar el correo. Actualizamos el detalla de la bitacora
			 */
			correoEnviado = true;

		} catch ( NoSuchProviderException exc ) {
			log.error( exc, exc );
			exc.printStackTrace( System.out );
			errorMsg = exc.toString();
			throw new GestionException( exc.getMessage() );
		} catch ( MessagingException exc ) {
			log.error( exc, exc );
			exc.printStackTrace( System.out );
			errorMsg = exc.toString();
			throw new GestionException( exc.getMessage() );
		} catch ( Throwable t ) {
			log.error( t, t );
			t.printStackTrace( System.out );
			errorMsg = t.toString();
			throw new GestionException( t.getMessage() );
		} finally {

			session = null;
			/*
			 * Si la bandera correoEnviado es false, ocurrio un error en el
			 * envio, por lo que se debe guardar en DB el correo para su
			 * posterior reenvio.
			 */
			if ( !correoEnviado )
				try {
					bitacoraCorreo.setEstatus( "Error" );

					CorreosPendientesBean cpb = new CorreosPendientesBean( to, body, subject, errorMsg.length() >= 1024 ? errorMsg.substring( 0, 1023 ) : errorMsg );
					CorreosPendientesBusinessLogic cpbl = new CorreosPendientesBusinessLogic();
					cpbl.insertaAlertaPendiente( cpb, isProcessAutGRM );
				} catch ( Exception e ) {
					log.error( "ATENCION!!!\nNo fue posible guardar el correo. Causa: " + e.toString(), e );
				}

			try {
				bcbl.insertaBitacoraCorreo( bitacoraCorreo );
			} catch ( Exception e ) {
				log.warn( "Error ingresando bitacora: " + e );
			}
		}
	}

}
