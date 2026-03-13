package com.axtel.sisecop.web;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.axtel.sisecop.dto.ProyectoServicioActividadDTO;
import com.axtel.sisecop.entities.ProyectoServicioActividad;
import com.axtel.sisecop.services.ProjectActivityService;
import com.axtel.web.utils.WebUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.syc.gestion.util.Util;


@WebServlet( "/SISECOP/activity" )
public class ProjectActivityController extends HttpServlet {

	private static final long			serialVersionUID	= 7214789876912436509L;
	private static final ObjectMapper	objectMapper		= new ObjectMapper();
	private static final Logger			log					= Logger.getLogger( ProjectActivityController.class );
	private String						jniName;
	private ProjectActivityService		activityService		= null;

	@Override
	public void init( ServletConfig config ) throws ServletException {
		super.init( config );
		jniName = WebUtils.findJNIName( config );
		activityService = new ProjectActivityService( jniName );
	}

	@Override
	protected void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {

		ProyectoServicioActividadDTO activity = readActivity( request );

		log.trace( "JSON recibido correctamente y mapeado a objeto ProyectoServicioActividadDTO. " + activity );

		try {

			log.debug( "Saving activity: " + activity );
			ProyectoServicioActividad activitySaved = activityService.createActivity( activity );

			log.info( "Activity saved: " + activitySaved );
			Util.sendJSONResponse( response, activitySaved );

		} catch ( Exception e ) {
			log.error( "Error saving porject: " + e.toString(), e );
			Util.sendJSONError( response, e );
		}

	}

	@Override
	protected void doDelete( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {

		try {

			int idActivity = Integer.parseInt( request.getParameter( "id" ) );
			log.info( "Trying to delete " + idActivity + " budget item" );
			Map<String, String> result = new HashMap<>();

			activityService.deleteActivity( idActivity );

			log.info( "Activity " + idActivity + " was deleted" );
			result.put( "deleted", "true" );
			result.put( "success", "true" );
			result.put( "rowsAfected", "1" );

			Util.sendJSONResponse( response, result );

		} catch ( Exception e ) {
			log.error( "Error saving porject: " + e.toString(), e );
			Util.sendJSONError( response, e );
		}

	}

	private ProyectoServicioActividadDTO readActivity( HttpServletRequest request ) throws IOException {
		final StringBuilder jsonRequest = new StringBuilder();
		try ( BufferedReader reader = new BufferedReader( new InputStreamReader( request.getInputStream(), StandardCharsets.UTF_8 ) ) ) {
			String line;
			while ( ( line = reader.readLine() ) != null ) {
				jsonRequest.append( line );
			}
		}

		String jsonString = jsonRequest.toString();
		log.trace( "Recibed: " + jsonString );
		ProyectoServicioActividadDTO activity = objectMapper.readValue( jsonString, ProyectoServicioActividadDTO.class );
		return activity;
	}
}
