package com.axtel.contratos.controller;


import java.io.IOException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.axtel.contratos.entities.FuelContract;
import com.axtel.contratos.services.FuelContractService;
import com.axtel.contratos.services.implementation.FuelContractServiceImplementation;
import com.fasterxml.jackson.databind.ObjectMapper;


public class FuelContractController extends HttpServlet {

	private static final long	serialVersionUID	= 1036345358034762358L;
	private static final Logger	log					= LogManager.getLogger( FuelContractController.class );
	private String				jniName;
	private FuelContractService	fuelContractService;
	private ObjectMapper mapper = new ObjectMapper();
 

	@Override
	protected void doPost( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException {
		log.trace( "Starting contract save operation " );
		FuelContract fuelContract = mapper.readValue( req.getInputStream(), FuelContract.class );

		try {
			log.trace( "Reading Contract object from request.");
			fuelContract = fuelContractService.createContract( fuelContract );
			log.debug( "Contract object obtined: " + fuelContract);
			
			log.info( "Successfull Saved Contract: " + fuelContract );

			String fuelContractJson = mapper.writeValueAsString( fuelContract );
			log.trace( "Response created: " + fuelContractJson );
			
			resp.setContentType( "application/json" );
			resp.setCharacterEncoding( "UTF-8" );
			log.debug( "Writting response: " + fuelContractJson );
			resp.getWriter().write( fuelContractJson );
		} catch ( Exception e ) {
			log.error( e, e );
			String errorJson = mapper.writeValueAsString( e.getMessage() );
			resp.setContentType( "application/json" );
			resp.setCharacterEncoding( "UTF-8" );
			resp.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
			resp.getWriter().write( errorJson );
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

			fuelContractService = new FuelContractServiceImplementation( jniName );
		} catch ( NamingException exc ) {
			jniName = "jdbc/gestion";
			log.info( "Environment Entry \"dataSourceRefName\" no definida usando default \"" + jniName + "\"" );
		}
	}
}
