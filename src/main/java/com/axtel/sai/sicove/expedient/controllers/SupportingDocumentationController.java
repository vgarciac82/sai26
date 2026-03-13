package com.axtel.sai.sicove.expedient.controllers;


import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.axtel.sai.sicove.entities.FuelAsignationVerification;
import com.axtel.sai.sicove.entities.WalletFuelRequestVerificationDetail;
import com.axtel.sai.sicove.exceptions.SicoveException;
import com.axtel.sai.sicove.expedient.repositories.ExpedientRepository;
import com.axtel.sai.sicove.expedient.repositories.impl.JDBCExpedientRepostory;
import com.axtel.sai.sicove.repositories.impl.JDBCFuelAsignationVerificationRepository;
import com.axtel.sai.sicove.services.FuelAsignationVerificationService;
import com.axtel.sai.sicove.services.impl.JDBCFuelAsignationVerificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;


@WebServlet( urlPatterns = { "/SICOVE/fuelingExpedient", "/SICOVE/fuelingExpedient/updateVerificationDetail" } )
@MultipartConfig
public class SupportingDocumentationController extends HttpServlet {

	private static final long					serialVersionUID	= 1L;
	private static final Logger					log					= Logger.getLogger( SupportingDocumentationController.class );
	private String								jniName;
	private ExpedientRepository					expedientRepository;
	private FuelAsignationVerificationService	verificationService;
	private ObjectMapper						mapper				= new ObjectMapper();

	@Override
	public void init( ServletConfig config ) throws ServletException {
		super.init( config );
		try {
			InitialContext ic = new InitialContext();
			jniName = ( String ) ic.lookup( "java:comp/env/dataSourceRefName" );

			if ( jniName == null ) {
				jniName = "jdbc/gestion";
				log.info( "Environment Entry \"dataSourceRefName\" nula usando default \"" + jniName + "\"" );
			} else
				log.info( "dataSourceRefName=" + jniName );
		} catch ( NamingException exc ) {
			jniName = "jdbc/gestion";
			log.info( "Environment Entry \"dataSourceRefName\" no definida usando default \"" + jniName + "\"" );
		}

		expedientRepository = new JDBCExpedientRepostory();
		verificationService = new JDBCFuelAsignationVerificationService( jniName, new JDBCFuelAsignationVerificationRepository() );
		( ( JDBCFuelAsignationVerificationService ) verificationService ).setExpedientRepository( expedientRepository );

	}

	@Override
	protected void doPost( HttpServletRequest request, HttpServletResponse response ) throws IOException {

		log.info( "Adding document to expedient." );
		Usuario user = null;

		try {
			log.trace( "Testing if login is present: [" + request.getParameter( "login" ) + "]" );
			String login = request.getParameter( "login" );
			log.debug( "Recibed login: " + login );

			if ( StringUtils.isEmpty( login ) ) {
				log.trace( "Testing for session and user" );
				HttpSession session = request.getSession( false );
				if ( session == null ) {
					response.sendRedirect( "../../index.jsp" );
					return;
				}

				user = ( Usuario ) session.getAttribute( GestionInterface.ATT_USER );
				if ( user == null ) {
					response.sendRedirect( "../../index.jsp" );
					return;
				}

				login = user.getLogin();
			}

			int idVerification = Integer.parseInt( request.getParameter( "idVerification" ) );

			FuelAsignationVerification asignationVerification = verificationService.readFuelingVerification( idVerification );

			WalletFuelRequestVerificationDetail verificationDetail = new WalletFuelRequestVerificationDetail();
			verificationDetail.setIdVerification( idVerification );
			verificationDetail.setTicketDate( Util.stringToDate( request.getParameter( "ticketDate" ), "yyyy-MM-dd" ) );
			verificationDetail.setTicketAmount( new BigDecimal( request.getParameter( "ticketAmount" ) ) );
			verificationDetail.setTicketNumber( request.getParameter( "ticketNumber" ) );

			BigDecimal pendingAmount = asignationVerification.getValidationAmount().subtract( verificationDetail.getTicketAmount() );

			if ( pendingAmount.compareTo( Util.ZERO ) < 0 )
				throw new SicoveException( "El monto registrado dejaria rojo el saldo." );

			int idProcess = Integer.parseInt( request.getParameter( "idProcess" ) );
			String folderName = request.getParameter( "folderName" );

			Part filePart = request.getPart( "documentTicket" );
			String fileName = filePart.getSubmittedFileName();
			File file = File.createTempFile( "upload_", "_" + fileName );
			FileUtils.copyInputStreamToFile( filePart.getInputStream(), file );

			verificationService.addVerificationDetail( idProcess, folderName, file, verificationDetail, login );

			Map<String, String> result = new HashMap<>();
			result.put( "success", "true" );
			result.put( "message", "El archivo se adjuntó correctamente." );

			Util.sendJSON( response, result );

		} catch ( Exception ex ) {
			log.error( ex, ex );
			Util.sendJSONError( response, ex );
		}
	}

	@Override
	protected void doPut( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
		try {
			HttpSession session = request.getSession( false );
			if ( session == null )
				throw new SicoveException( "Sesion expirada." );

			Usuario user = ( Usuario ) session.getAttribute( GestionInterface.ATT_USER );
			if ( user == null )
				throw new SicoveException( "Sesion expirada." );

			String action = request.getRequestURI().substring( request.getRequestURI().lastIndexOf( "/" ) + 1 );

			log.trace( "Executing action: " + action );

			if ( "updateVerificationDetail".equals( action ) ) {

				WalletFuelRequestVerificationDetail detail = mapper.readValue( request.getInputStream(), WalletFuelRequestVerificationDetail.class );
				WalletFuelRequestVerificationDetail verificationDetail = verificationService.readFuelingVerificationDetail( detail.getIdDetail() );

				log.debug( "Before updating: " + verificationDetail );

				verificationDetail.setAcepted( detail.isAcepted() );
				verificationDetail.setTicketObservations( detail.getTicketObservations() );

				verificationDetail = verificationService.updateFuelingVerificationDetail( verificationDetail );
				log.info( "Updated Verification Detail: " + verificationDetail );

				Util.sendJSON( response, verificationDetail );
			}

		} catch ( Exception ex ) {
			log.error( ex, ex );
			Util.sendJSONError( response, ex );
		}
	}

	@Override
	protected void doDelete( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
		try {

			int idDetail = 0;

			if ( request.getParameter( "idDetail" ) != null )
				idDetail = Integer.parseInt( request.getParameter( "idDetail" ) );
			else {
				idDetail = mapper.readValue( request.getInputStream(), WalletFuelRequestVerificationDetail.class ).getIdDetail();
			}

			WalletFuelRequestVerificationDetail detail = verificationService.readFuelingVerificationDetail( idDetail );

			log.info( "Deleting verfication detail: " + detail );
			verificationService.deleteFuelingVerificationDetail( detail );
			log.info( "Deleted row: " + detail );

			Map<String, String> result = new HashMap<>();
			result.put( "success", "true" );
			result.put( "message", "El registro se elimino correctamente." );

			Util.sendJSON( response, result );

		} catch ( Exception ex ) {
			log.error( ex, ex );
			Util.sendJSONError( response, ex );
		}
	}

}
