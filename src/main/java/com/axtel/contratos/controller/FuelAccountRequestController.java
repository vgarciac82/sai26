package com.axtel.contratos.controller;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.axtel.contratos.entities.FuelProvisioningRequest;
import com.axtel.contratos.repositories.JDBCFuelProvisioningRequestRepository;
import com.axtel.contratos.services.FuelProvisioningRequestService;
import com.axtel.contratos.services.implementation.JDBCFuelProvisioningRequestService;
import com.axtel.sai.sicove.SICOVE;
import com.axtel.sai.sicove.repositories.impl.JDBCFuelAccountNotificatorRepository;
import com.axtel.sai.sicove.services.FuelAccountNotificatorService;
import com.axtel.sai.sicove.services.impl.MailFuelAccountNotificatorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.syc.gestion.util.Util;


public class FuelAccountRequestController extends HttpServlet {

	private static final long				serialVersionUID	= -1540413424487280843L;
	private FuelProvisioningRequestService	fuelProvisioningRequestService;
	private FuelAccountNotificatorService	fuelAccountNotificatorService;
	private String							jniName				= "";
	private static final Logger				log					= LogManager.getLogger( FuelAccountRequestController.class );
	private ObjectMapper					mapper				= new ObjectMapper();

	@Override
	protected void doPut( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException {
		String action = req.getRequestURI().substring( req.getRequestURI().lastIndexOf( "/" ) + 1 );
		log.info( "Action: " + action );
		try {
			FuelProvisioningRequest fuelProvisioningRequest = mapper.readValue( req.getInputStream(), FuelProvisioningRequest.class );

			if ( "auth".equals( action ) ) {
				fuelProvisioningRequestService.update( fuelProvisioningRequest );
				fuelProvisioningRequest = fuelProvisioningRequestService.readFuelProvisioning( fuelProvisioningRequest.getFuelProvisioningRequestId() );
				fuelAccountNotificatorService.sendAuthNotification( fuelProvisioningRequest );
			} else if ( "sendRequest".equals( action ) ) {
				fuelProvisioningRequestService.update( fuelProvisioningRequest );
				fuelProvisioningRequest = fuelProvisioningRequestService.readFuelProvisioning( fuelProvisioningRequest.getFuelProvisioningRequestId() );
				fuelAccountNotificatorService.sendPendingAuthNotification( fuelProvisioningRequest );
			} else {

				log.info( fuelProvisioningRequest );

				FuelProvisioningRequest fuelProvisioningOrg = fuelProvisioningRequestService.readFuelProvisioning( fuelProvisioningRequest.getFuelProvisioningRequestId() );
				fuelProvisioningOrg.setAutorizedAmount( fuelProvisioningRequest.getAutorizedAmount() );
				fuelProvisioningOrg.setRequestStatus( fuelProvisioningRequest.getRequestStatus() );
				fuelProvisioningOrg.setFuelContractAccountId( fuelProvisioningRequest.getFuelContractAccountId() );
				fuelProvisioningOrg.setRequestJustification( fuelProvisioningRequest.getRequestJustification() );
				fuelProvisioningOrg.setRejectJustification( fuelProvisioningRequest.getRejectJustification() );
				fuelProvisioningRequest = fuelProvisioningRequestService.update( fuelProvisioningRequest );
				log.info( fuelProvisioningRequest );
				Util.sendJSON( resp, fuelProvisioningRequest );

			}
		} catch ( Exception e ) {
			log.error( e, e );
			Util.sendJSONError( resp, e );
		}
	}

	@Override
	protected void doPost( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException {
		FuelProvisioningRequest fuelProvisioningRequest = mapper.readValue( req.getInputStream(), FuelProvisioningRequest.class );
		log.info( fuelProvisioningRequest );
		try {
			fuelProvisioningRequest = fuelProvisioningRequestService.insert( fuelProvisioningRequest );
			log.info( fuelProvisioningRequest );
			Util.sendJSON( resp, fuelProvisioningRequest );
		} catch ( Exception e ) {
			log.error( e, e );
			Util.sendJSONError( resp, e );
		}
	}

	@Override
	protected void doGet( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException {

		String idStr = req.getParameter( SICOVE.PARAM_REQUEST_FOLIO );

		log.info( "Querying Fuelling Request into Account with folio: " + idStr );

		try {
			if ( StringUtils.isBlank( idStr ) )
				throw new RuntimeException( "No se recibio folio para busqueda" );

			FuelProvisioningRequest fuelProvisioningRequest = fuelProvisioningRequestService.readFuelProvisioning( Integer.valueOf( idStr ) );
			log.debug( "Found: " + fuelProvisioningRequest );
			Util.sendJSON( resp, fuelProvisioningRequest );
		} catch ( Exception e ) {
			log.error( e, e );
			Util.sendJSONError( resp, e );
		}
	}

	@Override
	protected void doDelete( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException {
		String action = req.getRequestURI().substring( req.getRequestURI().lastIndexOf( "/" ) + 1 );
		if ( "rejectRequest".equals( action ) ) {
			try {
				FuelProvisioningRequest fuelProvisioningRequest = mapper.readValue( req.getInputStream(), FuelProvisioningRequest.class );
				fuelProvisioningRequestService.update( fuelProvisioningRequest );
				fuelProvisioningRequest = fuelProvisioningRequestService.readFuelProvisioning( fuelProvisioningRequest.getFuelProvisioningRequestId() );
				fuelAccountNotificatorService.notifyRejection( fuelProvisioningRequest );
				Map<String, String> result = new HashMap<>();
				result.put( "success", String.valueOf( true ) );
				result.put( "message", "Solicitud rechazada exitosamente." );
			} catch ( Exception e ) {
				log.error( e, e );
				Util.sendJSONError( resp, e );
			}
		} else {
			String idStr = req.getParameter( SICOVE.PARAM_REQUEST_FOLIO );

			log.info( "Deleting Fuelling Request with folio: " + idStr );

			try {
				if ( StringUtils.isBlank( idStr ) )
					throw new RuntimeException( "No se recibio folio para descartar" );

				boolean deleted = fuelProvisioningRequestService.deleteFuelProvisioning( Integer.valueOf( idStr ) );
				Map<String, String> result = new HashMap<>();
				result.put( "success", String.valueOf( deleted ) );
				result.put( "message", "Solicitud " + idStr + " eliminada exitosamente." );

				Util.sendJSON( resp, result );

			} catch ( Exception e ) {
				log.error( e, e );
				Util.sendJSONError( resp, e );
			}
		}
	}

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

		fuelProvisioningRequestService = new JDBCFuelProvisioningRequestService( new JDBCFuelProvisioningRequestRepository(), jniName );
		fuelAccountNotificatorService = new MailFuelAccountNotificatorService( jniName, "REQFUELACCOUNT", new JDBCFuelAccountNotificatorRepository() );
	}

}
