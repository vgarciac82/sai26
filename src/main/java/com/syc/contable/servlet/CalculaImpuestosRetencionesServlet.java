package com.syc.contable.servlet;


import java.io.IOException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.syc.contable.CalculaImpuestosRetencionesBusinessLogic;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.sai.contabilidad.servlet.ResponseSender;


public class CalculaImpuestosRetencionesServlet extends HttpServlet implements GestionInterface {

	private static final long	serialVersionUID	= 3113670099079119431L;
	private static final Logger	log					= Logger.getLogger( CalculaImpuestosRetencionesServlet.class );
	private static String		jniName;

	@Override
	protected void doPost( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException {
		final long t0 = System.currentTimeMillis();
		log.trace( "doPost(): inicio" );

		HttpSession session = req.getSession( false );
		if ( session == null ) {
			log.debug( "Sesión inválida: session == null" );
			ResponseSender.sendClientSimpleMessage( resp, false, "Su sesion a caducado. Por favor reingrese al sistema." );
			log.trace( "doPost(): fin (sesión nula) en " + ( System.currentTimeMillis() - t0 ) + " ms" );
			return;
		}

		Usuario u = ( Usuario ) session.getAttribute( ATT_USER );
		if ( u == null ) {
			log.debug( "Sesión inválida: usuario == null" );
			ResponseSender.sendClientSimpleMessage( resp, false, "Su sesion a caducado. Por favor reingrese al sistema." );
			log.trace( "doPost(): fin (usuario nulo) en " + ( System.currentTimeMillis() - t0 ) + " ms" );
			return;
		}

		Caso c = ( Caso ) session.getAttribute( ATT_CASE );
		if ( c == null ) {
			log.debug( "Sesión inválida: caso == null" );
			ResponseSender.sendClientSimpleMessage( resp, false, "Su sesion a caducado. Por favor reingrese al sistema." );
			log.trace( "doPost(): fin (caso nulo) en " + ( System.currentTimeMillis() - t0 ) + " ms" );
			return;
		}

		try {
			log.trace( "Validación de sesión OK. Preparando datos para recálculo." );
			String folio = c.getFolio();
			String tipoPago = c.getTipoCaso().getGavetaAsociada();
			int nFolioPago = Integer.parseInt( folio.substring( 9 ) );

			log.debug( "Parámetros de recálculo: tipoPago=" + tipoPago + ", nFolioPago=" + nFolioPago + ", folioCompleto=" + folio );

			CalculaImpuestosRetencionesBusinessLogic cirBL = new CalculaImpuestosRetencionesBusinessLogic( jniName );
			log.trace( "Invocando recalculaMontoImpuestos()" );
			int afectados = cirBL.recalculaMontoImpuestos( tipoPago, nFolioPago );

			log.info( "Recalculo de impuestos/retenciones completado. Registros afectados=" + afectados );
			ResponseSender.sendClientSimpleMessage( resp, true, String.valueOf( afectados ) );

			log.trace( "doPost(): fin OK en " + ( System.currentTimeMillis() - t0 ) + " ms" );
			return;

		} catch ( Exception e ) {
			log.error( "Error en doPost(): " + e, e );
			ResponseSender.sendClientSimpleMessage( resp, false, e.toString() );
			log.trace( "doPost(): fin con error en " + ( System.currentTimeMillis() - t0 ) + " ms" );
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

	}

}
