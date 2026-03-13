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

import com.axtel.contratos.core.SuficienciaPagoDirectoDetalle;
import com.axtel.contratos.services.SuficienciaPagoDirectoDetalleBusinessLogic;
import com.fasterxml.jackson.databind.ObjectMapper;


@WebServlet( "/api/suficiencia/detalle" )
public class SuficienciaPagoDirectoDetalleServlet extends HttpServlet {

	private static final long							serialVersionUID	= 4076717367748812824L;
	private static final Logger							log					= Logger.getLogger( SuficienciaPagoDirectoDetalleServlet.class );
	private final ObjectMapper							mapper				= new ObjectMapper();
	private SuficienciaPagoDirectoDetalleBusinessLogic	logic;

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
		logic = new SuficienciaPagoDirectoDetalleBusinessLogic( jniName );
	}

	@Override
	protected void doPost( HttpServletRequest request, HttpServletResponse response ) throws IOException {
		try {
			SuficienciaPagoDirectoDetalle bean = mapper.readValue( request.getReader(), SuficienciaPagoDirectoDetalle.class );
			SuficienciaPagoDirectoDetalle resultado = logic.insert( bean );

			response.setContentType( "application/json" );
			response.setCharacterEncoding( "UTF-8" );
			mapper.writeValue( response.getWriter(), resultado );

		} catch ( Exception ex ) {
			log.error( "Error al insertar Detalle", ex );
			enviarError( response, "Error al insertar Detalle." );
		}
	}

	@Override
	protected void doPut( HttpServletRequest request, HttpServletResponse response ) throws IOException {
		try {
			SuficienciaPagoDirectoDetalle bean = mapper.readValue( request.getReader(), SuficienciaPagoDirectoDetalle.class );
			SuficienciaPagoDirectoDetalle resultado = logic.update( bean );

			response.setContentType( "application/json" );
			response.setCharacterEncoding( "UTF-8" );
			mapper.writeValue( response.getWriter(), resultado );

		} catch ( Exception ex ) {
			log.error( "Error al actualizar Detalle", ex );
			enviarError( response, "Error al actualizar Detalle." );
		}
	}

	@Override
	protected void doDelete( HttpServletRequest request, HttpServletResponse response ) throws IOException {
		try {
			int folio = Integer.parseInt( request.getParameter( "folio" ) );
			logic.deleteByFolio( folio );

			response.setContentType( "application/json" );
			response.setCharacterEncoding( "UTF-8" );
			response.getWriter().write( "{\"mensaje\":\"Eliminados correctamente\"}" );

		} catch ( Exception ex ) {
			log.error( "Error al eliminar Detalles", ex );
			enviarError( response, "Error al eliminar Detalles." );
		}
	}

	@Override
	protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws IOException {
		try {
			int folio = Integer.parseInt( request.getParameter( "folio" ) );
			List<SuficienciaPagoDirectoDetalle> lista = logic.findByFolio( folio );

			response.setContentType( "application/json" );
			response.setCharacterEncoding( "UTF-8" );
			mapper.writeValue( response.getWriter(), lista );

		} catch ( Exception ex ) {
			log.error( "Error al buscar Detalles", ex );
			enviarError( response, "Error al buscar Detalles." );
		}
	}

	private void enviarError( HttpServletResponse response, String mensaje ) throws IOException {
		response.setStatus( HttpServletResponse.SC_OK );
		response.setContentType( "application/json" );
		response.setCharacterEncoding( "UTF-8" );
		response.getWriter().write( "{\"error\": \"" + mensaje + "\"}" );
	}
}
