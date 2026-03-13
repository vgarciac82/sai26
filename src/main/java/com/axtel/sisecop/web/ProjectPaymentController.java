package com.axtel.sisecop.web;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.axtel.sisecop.dto.ProjectPaymentDTO;
import com.axtel.sisecop.entities.ProyectoServicioPago;
import com.axtel.sisecop.services.ProjectPaymentService;
import com.axtel.web.utils.WebUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.syc.gestion.util.Util;


@WebServlet( "/SISECOP/payment" )
public class ProjectPaymentController extends HttpServlet {

	private static final long			serialVersionUID	= 7214789876912436509L;
	private static final ObjectMapper	objectMapper		= new ObjectMapper();
	private static final Logger			log					= Logger.getLogger( ProjectPaymentController.class );
	private String						jniName;
	private ProjectPaymentService		paymentService		= null;

	@Override
	public void init( ServletConfig config ) throws ServletException {
		super.init( config );

		jniName = WebUtils.findJNIName( config );
		paymentService = new ProjectPaymentService( jniName );
	}

	@Override
	protected void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {

		ProjectPaymentDTO paymentDTO = readPayment( request );
		log.trace( "JSON recibido correctamente y mapeado a objeto Payment. " + paymentDTO );

		try {

			log.debug( "Saving payment: " + paymentDTO );
			ProyectoServicioPago projectPayment = paymentService.createProjectPayment( paymentDTO );

			log.info( "Payment saved: " + projectPayment );
			Util.sendJSONResponse( response, projectPayment );

		} catch ( Exception e ) {
			log.error( "Error saving porject: " + e.toString(), e );
			Util.sendJSONError( response, e );
		}

	}

	@Override
	protected void doDelete( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {

		try {

			int idPayment = Integer.parseInt( request.getParameter( "id" ) );
			log.info( "Trying to delete " + idPayment + " payment" );
			Map<String, String> result = new HashMap<>();

			paymentService.deleteProjectPayment( idPayment );

			log.info( "Payment  " + idPayment + " was deleted" );
			result.put( "deleted", "true" );
			result.put( "success", "true" );
			result.put( "rowsAfected", "1" );

			Util.sendJSONResponse( response, result );

		} catch ( Exception e ) {
			log.error( "Error saving porject: " + e.toString(), e );
			Util.sendJSONError( response, e );
		}

	}

	private ProjectPaymentDTO readPayment( HttpServletRequest request ) throws IOException {
		final StringBuilder jsonRequest = new StringBuilder();
		try ( BufferedReader reader = new BufferedReader( new InputStreamReader( request.getInputStream() ) ) ) {
			String line;
			while ( ( line = reader.readLine() ) != null ) {
				jsonRequest.append( line );
			}
		}

		String jsonString = jsonRequest.toString();
		log.trace( "Recibed: " + jsonString );
		ProjectPaymentDTO paymentDTO = objectMapper.readValue( jsonString, ProjectPaymentDTO.class );
		return paymentDTO;
	}
}
