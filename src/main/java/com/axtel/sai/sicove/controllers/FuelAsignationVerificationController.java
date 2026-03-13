package com.axtel.sai.sicove.controllers;


import java.io.IOException;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.axtel.sai.sicove.entities.FuelAsignationVerification;
import com.axtel.sai.sicove.entities.WalletFuelRequestVerificationDetail;
import com.axtel.sai.sicove.repositories.impl.JDBCFuelAsignationVerificationRepository;
import com.axtel.sai.sicove.services.impl.JDBCFuelAsignationVerificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.syc.gestion.util.Util;


public class FuelAsignationVerificationController extends HttpServlet {

	private static final long						serialVersionUID	= 1037498415129331546L;
	private String									jniName;
	private JDBCFuelAsignationVerificationService	fuelAsignationVerificationService;
	private ObjectMapper							mapper				= new ObjectMapper();
	private static final Logger						log					= LogManager.getLogger( FuelAsignationVerificationController.class );

	@Override
	protected void doGet( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException {

		String action = req.getRequestURI().substring( req.getRequestURI().lastIndexOf( "/" ) + 1 );
		log.info( "Action: " + action );

		try {

			FuelAsignationVerification fuelAsignationVerification = null;

			if ( "getVerificationList".equals( action ) ) {
				int idVerification = Integer.parseInt( req.getParameter( "idVerification" ) );
				log.info( "Looking for verification list with assignation folio " + idVerification );
				List<WalletFuelRequestVerificationDetail> verificationDetailList = fuelAsignationVerificationService.readFuelingVerificationDetailList( idVerification );
				Util.sendJSON( resp, verificationDetailList );
				return;
			} else if ( "getByAsignationId".equals( action ) ) {
				int id = Integer.parseInt( req.getParameter( "fuelingRequestId" ) );
				log.info( "Looking for verification with assignation folio " + id );
				int idVerification = fuelAsignationVerificationService.readFuelingVerificationIdByAsignation( id );
				if ( idVerification > 0 )
					fuelAsignationVerification = fuelAsignationVerificationService.readFuelingVerification( idVerification );
				else
					fuelAsignationVerification = new FuelAsignationVerification();
			} else {

				int id = Integer.parseInt( req.getParameter( "idVerification" ) );
				log.info( "Looking for verification with id " + id );

				fuelAsignationVerification = fuelAsignationVerificationService.readFuelingVerification( id );

				log.info( "Found: " + fuelAsignationVerification );

			}
			Util.sendJSON( resp, fuelAsignationVerification );
		} catch ( Exception e ) {
			log.error( e, e );
			Util.sendJSONError( resp, e );
		}

	}

	@Override
	protected void doPost( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException {
		FuelAsignationVerification fuelAsignationVerification = mapper.readValue( req.getInputStream(), FuelAsignationVerification.class );
		log.info( "Saving: " + fuelAsignationVerification );

		try {
			fuelAsignationVerification = fuelAsignationVerificationService.createFuelingVerification( fuelAsignationVerification );
			log.info( fuelAsignationVerification );
			Util.sendJSON( resp, fuelAsignationVerification );
		} catch ( Exception e ) {
			log.error( e, e );
			Util.sendJSONError( resp, e );
		}
	}

	@Override
	protected void doPut( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException {
		FuelAsignationVerification fuelAsignationVerification = mapper.readValue( req.getInputStream(), FuelAsignationVerification.class );
		log.info( "Updating: " + fuelAsignationVerification );

		try {
			
			FuelAsignationVerification originalAsignation = fuelAsignationVerificationService.readFuelingVerification( fuelAsignationVerification.getIdVerification() );
			originalAsignation.setCurrentVehicleKilometers( fuelAsignationVerification.getCurrentVehicleKilometers() );
			originalAsignation.setCurrentWalletBalance( fuelAsignationVerification.getCurrentWalletBalance() );
			originalAsignation.setValidationAmount( fuelAsignationVerification.getValidationAmount() );
			originalAsignation.setInitialVehicleKilometers( fuelAsignationVerification.getInitialVehicleKilometers() );
			
			fuelAsignationVerification = fuelAsignationVerificationService.updateFuelingVerification( originalAsignation );
			log.info( fuelAsignationVerification );
			Util.sendJSON( resp, fuelAsignationVerification );
		} catch ( Exception e ) {
			log.error( e, e );
			Util.sendJSONError( resp, e );
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

		fuelAsignationVerificationService = new JDBCFuelAsignationVerificationService( jniName, new JDBCFuelAsignationVerificationRepository() );
	}

}
