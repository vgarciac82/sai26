package com.axtel.sisecop.web;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.axtel.sisecop.dto.ProyectoExcepcionDTO;
import com.axtel.sisecop.entities.ProyectoServicio;
import com.axtel.sisecop.services.ProyectoExcepcionRepository;
import com.axtel.sisecop.services.ProyectoExcepcionService;
import com.axtel.web.utils.WebUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.syc.gestion.util.Util;


@WebServlet( urlPatterns = { "/SISECOP/excepciones/consulta", "/SISECOP/excepciones" } )
public class ProyectoServicioExcepcionController extends HttpServlet {

	private static final long			serialVersionUID			= 6867954132414915056L;
	private static final Logger			log							= Logger.getLogger( ProyectoServicioExcepcionController.class );
	private String						jniName;
	private ProyectoExcepcionService	proyectoExcepcionService	= null;
	private final ObjectMapper			mapper						= new ObjectMapper();

	@Override
	public void init( ServletConfig config ) throws ServletException {
		super.init( config );

		jniName = WebUtils.findJNIName( config );
		proyectoExcepcionService = new ProyectoExcepcionService( jniName );
		proyectoExcepcionService.setProyectoExcepcionRepository( new ProyectoExcepcionRepository() );

	}

	@Override
	protected void doGet( HttpServletRequest req, HttpServletResponse response ) throws ServletException, IOException {

		try {

			int idProyecto = Integer.parseInt( req.getParameter( "id_proyecto" ) );
			int idTipoExcepcion = Integer.parseInt( req.getParameter( "tipo_excepcion" ) );

			log.info( "Looking for last version to project with id: " + req.getParameter( "id" ) + " AND exception type id: " + idTipoExcepcion );

			int consecutivo = proyectoExcepcionService.getMaxConsecutivo( idProyecto, idTipoExcepcion );

			log.debug( "version founded: " + consecutivo );

			Map<String, Integer> result = new HashMap<String, Integer>();
			result.put( "version", consecutivo );

			Util.sendJSONResponse( response, result );
		} catch ( Exception e ) {
			log.error( "Error getting project: " + e.toString(), e );
			Util.sendJSONError( response, e );
		}
	}

	@Override
	protected void doPost( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException {

		ProyectoExcepcionDTO proyectoExcepcionDTO = mapper.readValue( req.getInputStream(), ProyectoExcepcionDTO.class );
		ProyectoServicio project = proyectoExcepcionService.creaCopiaInicial( proyectoExcepcionDTO );

		log.debug( "Project founded: " + project );

		Util.sendJSONResponse( resp, project );
		log.info( "Creando clon de proyecto: " + proyectoExcepcionDTO );
	}

}
