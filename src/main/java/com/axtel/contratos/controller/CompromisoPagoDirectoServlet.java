package com.axtel.contratos.controller;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.axtel.contratos.services.CompromisoPagoDirectoEncabezadoBusinessLogic;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;


@WebServlet( name = "CompromisoPagoDirectoServlet", urlPatterns = { "/api/compromiso/pagodirecto" } )
public class CompromisoPagoDirectoServlet extends HttpServlet {

	private static final long								serialVersionUID	= -6391438417416210266L;
	private static final Logger								log					= Logger.getLogger( CompromisoPagoDirectoServlet.class );
	private final ObjectMapper								mapper				= new ObjectMapper();
	private CompromisoPagoDirectoEncabezadoBusinessLogic	bl					= null;
	private static String									jniName				= "jdbc/gestion";

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
		bl = new CompromisoPagoDirectoEncabezadoBusinessLogic( jniName );
	}

	@Override
	protected void doPost( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException {

		Map<String, Object> out = new HashMap<>();
		int status = HttpServletResponse.SC_OK;

		try {
			HttpSession session = req.getSession( false );
			if ( session == null )
				throw new RuntimeException( "Sesion terminada. Ingrese nuevamente al sistema." );

			Usuario user = ( Usuario ) session.getAttribute( GestionInterface.ATT_USER );

			if ( user == null )
				throw new RuntimeException( "Sesion terminada. Ingrese nuevamente al sistema." );

			Caso c = ( Caso ) session.getAttribute( GestionInterface.ATT_CASE );
			if ( c == null )
				throw new RuntimeException( "Sesion terminada. Ingrese nuevamente al sistema." );

			String suficiencia = req.getParameter( "folio" );

			if ( StringUtils.trimToEmpty( suficiencia ) == null )
				throw new RuntimeException( "No se recibio la suficiencia." );

			bl.insertaPagoDesdeSuficiencia( c, user, suficiencia );

			resp.setStatus( HttpServletResponse.SC_OK );
			out.put( "status", "OK" );
			out.put( "message", "Trámite finalizado correctamente." );

			mapper.writeValue( resp.getWriter(), out );
		} catch ( Exception e ) {
			status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
			out.put( "status", "FAIL" );
			out.put( "message", "Error procesando solicitud: " + e.toString() );

		}

		resp.setStatus( status );
		mapper.writeValue( resp.getWriter(), out );
	}

}
