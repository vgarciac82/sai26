package com.axtel.cfdi.stamp.controller;


import java.io.File;
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

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.axtel.cfdi.stamp.core.InvoiceRespond;
import com.axtel.cfdi.stamp.service.StampCFDIService;
import com.axtel.web.utils.ControllerUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


@WebServlet( "/CFDIManagment/stamp" )
public class CFDIStampController extends HttpServlet {

	private static final long	serialVersionUID	= 1L;
	private static final Logger	log					= LogManager.getLogger( CFDIStampController.class );
	private final Gson			gson				= new GsonBuilder().create();
	private StampCFDIService	stampService;
	private String				jniName;
	private String				REPORT_DIR;

	@Override
	protected void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
		try {
			String json = ControllerUtils.readJsonFromRequest( request );

			@SuppressWarnings( "unchecked" )
			List<String> cfdiIds = ( List<String> ) gson.fromJson( json, List.class );

			log.info( "Timbrando CFDIs con IDs: " + cfdiIds );

			List<InvoiceRespond> result = stampService.stamp( cfdiIds );
			response.setContentType( "application/json" );
			response.setCharacterEncoding( "UTF-8" );
			response.getWriter().write( gson.toJson( result ) );

		} catch ( Exception e ) {
			log.error( "Error al timbrar los CFDIs: ", e );
			response.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
			response.getWriter().write( "{\"error\": \"Error al timbrar los CFDIs. Por favor intente nuevamente.\"}" );
		}
	}

	@Override
	protected void doDelete( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
		try {
			String json = ControllerUtils.readJsonFromRequest( request );
			List<Integer> cfdiIds = gson.fromJson( json, List.class );

			// Log para los IDs recibidos
			log.info( "Cancelando CFDIs con IDs: " + cfdiIds );

			// Aquí agregar la lógica de cancelación de CFDIs
			// Por ejemplo: cfdiService.cancelCFDIs(cfdiIds);

			response.setContentType( "application/json" );
			response.setCharacterEncoding( "UTF-8" );
			response.getWriter().write( "{\"message\":\"CFDIs cancelados correctamente.\"}" );
		} catch ( Exception e ) {
			log.error( "Error al cancelar los CFDIs: ", e );
			response.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
			response.getWriter().write( "{\"error\": \"Error al cancelar los CFDIs. Por favor intente nuevamente.\"}" );
		}
	}

	@Override
	public void init( ServletConfig config ) throws ServletException {
		super.init( config );
		REPORT_DIR = getServletContext().getRealPath( "Reportes" + File.separator );
		try {
			InitialContext ic = new InitialContext();
			jniName = ( String ) ic.lookup( "java:comp/env/dataSourceRefName" );

			if ( jniName == null ) {
				jniName = "jdbc/gestion";
				log.info( "Environment Entry \"dataSourceRefName\" nula, usando default \"" + jniName + "\"" );
			} else {
				log.info( "dataSourceRefName=" + jniName );
			}
		} catch ( NamingException exc ) {
			jniName = "jdbc/gestion";
			log.info( "Environment Entry \"dataSourceRefName\" no definida, usando default \"" + jniName + "\"" );
		}
		stampService = new StampCFDIService( jniName, new File( REPORT_DIR ) );
	}

}
