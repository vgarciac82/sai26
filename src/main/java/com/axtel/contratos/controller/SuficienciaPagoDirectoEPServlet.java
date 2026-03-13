package com.axtel.contratos.controller;


import java.io.IOException;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.axtel.contratos.core.SuficienciaPagoDirectoEP;
import com.axtel.contratos.services.SuficienciaPagoDirectoEPBusinessLogic;
import com.fasterxml.jackson.databind.ObjectMapper;


@WebServlet( "/api/suficiencia/ep" )
public class SuficienciaPagoDirectoEPServlet extends HttpServlet {

	private static final long						serialVersionUID	= -1939377243758378129L;
	private static final Logger						log					= Logger.getLogger( SuficienciaPagoDirectoEPServlet.class );
	private final ObjectMapper						mapper				= new ObjectMapper();
	private SuficienciaPagoDirectoEPBusinessLogic	logic;

	@Override
	public void init( ServletConfig config ) throws ServletException {
		super.init( config );
		String jniName;
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
		logic = new SuficienciaPagoDirectoEPBusinessLogic( jniName );
	}

	@Override
	protected void doPost( HttpServletRequest request, HttpServletResponse response ) throws IOException {
		try {
			SuficienciaPagoDirectoEP bean = mapper.readValue( request.getReader(), SuficienciaPagoDirectoEP.class );
			SuficienciaPagoDirectoEP resultado = logic.insert( bean );

			response.setContentType( "application/json" );
			response.setCharacterEncoding( "UTF-8" );
			mapper.writeValue( response.getWriter(), resultado );

		} catch ( Exception ex ) {
			log.error( "Error al insertar EP", ex );
			enviarError( response, "Error al insertar EP. " + ex.toString() );
		}
	}

	@Override
	protected void doPut( HttpServletRequest request, HttpServletResponse response ) throws IOException {
		try {
			SuficienciaPagoDirectoEP bean = mapper.readValue( request.getReader(), SuficienciaPagoDirectoEP.class );
			SuficienciaPagoDirectoEP resultado = logic.update( bean );

			response.setContentType( "application/json" );
			response.setCharacterEncoding( "UTF-8" );
			mapper.writeValue( response.getWriter(), resultado );

		} catch ( Exception ex ) {
			log.error( "Error al actualizar EP", ex );
			enviarError( response, "Error al actualizar EP." );
		}
	}

	@Override
	protected void doDelete( HttpServletRequest request, HttpServletResponse response ) throws IOException {
		try {
			int folio = Integer.parseInt( request.getParameter( "folio" ) );
			String ep = request.getParameter( "ep" );
			logic.deleteByFolio( folio, ep );

			response.setContentType( "application/json" );
			response.setCharacterEncoding( "UTF-8" );
			response.getWriter().write( "{\"mensaje\":\"Eliminados correctamente\"}" );

		} catch ( Exception ex ) {
			log.error( "Error al eliminar EPs", ex );
			enviarError( response, "Error al eliminar EPs." );
		}
	}

	@Override
	protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws IOException {
		try {
			int folio = Integer.parseInt( request.getParameter( "folio" ) );
			List<SuficienciaPagoDirectoEP> lista = logic.findByFolio( folio );

			response.setContentType( "application/json" );
			response.setCharacterEncoding( "UTF-8" );
			mapper.writeValue( response.getWriter(), lista );

		} catch ( Exception ex ) {
			log.error( "Error al buscar EPs", ex );
			enviarError( response, "Error al buscar EPs." );
		}
	}

	private void enviarError( HttpServletResponse response, String mensaje ) throws IOException {
		response.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
		response.setContentType( "application/json" );
		response.setCharacterEncoding( "UTF-8" );
		response.getWriter().write( "{\"error\": \"" + mensaje + "\"}" );
	}
}
