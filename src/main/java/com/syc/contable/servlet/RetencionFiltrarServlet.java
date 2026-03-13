package com.syc.contable.servlet;


import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.syc.contable.RetencionBusinessLogic;
import com.syc.contable.core.Retencion;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;


public class RetencionFiltrarServlet extends HttpServlet {

	private static final long	serialVersionUID	= 1L;
	private static Logger		log					= Logger.getLogger( RetencionFiltrarServlet.class );

	public void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {

		HttpSession session = request.getSession( false );
		boolean exito = false;
		if ( session == null ) {
			response.sendRedirect( "../index.jsp" );
			return;
		}

		Usuario usuario = ( Usuario ) session.getAttribute( GestionInterface.ATT_USER );
		if ( usuario == null ) {
			response.sendRedirect( "../index.jsp" );
			return;
		}

		String esIP = ( request.getParameter( "cEsIP" ) );
		Retencion clcFiltrada = null;
		RetencionBusinessLogic recPresBL = new RetencionBusinessLogic( GestionInterface.ATT_CONEXION );

		try {
			
			String CXP = ( request.getParameter( "CXP" ) == null ) ? " " : request.getParameter( "CXP" ).trim();
			clcFiltrada = recPresBL.filtrar( CXP, esIP );

			if ( clcFiltrada != null ) {
				session.setAttribute( "RETENCION", clcFiltrada );
				exito = true;
			}
			
			session.setAttribute( "exito", exito );

		} catch ( Exception e ) {
			log.error( e, e );
			session.setAttribute( "exito", false );
			session.setAttribute( "ERR_MSG", e.toString() );
		}
		
		response.sendRedirect( "../plantillasCasos/retencion.jsp" );
		
	}

}
