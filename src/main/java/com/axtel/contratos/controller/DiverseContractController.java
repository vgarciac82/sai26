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

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.syc.contable.ContratoDiversoBusinessLogic;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;

@WebServlet( urlPatterns = { "/contracts/diverse", "/contracts/diverse/updateRESICO" } )
public class DiverseContractController extends HttpServlet {

	private ContratoDiversoBusinessLogic	diverseContractService;

	private String							jniName;

	private Logger							log					= LogManager.getLogger( DiverseContractController.class );

	private static final long				serialVersionUID	= -8143094690699400730L;

	@Override
	protected void doGet( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException {

	}

	@Override
	protected void doPost( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException {
	}

	@Override
	protected void doPut( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException {

		try {

			HttpSession session = req.getSession( false );
			if ( session == null )
				throw new Exception( "Session terminada. Ingrese nuevamente al sistema" );

			Usuario u = ( Usuario ) session.getAttribute( GestionInterface.ATT_USER );
			if ( u == null )
				throw new Exception( "Session terminada. Ingrese nuevamente al sistema" );

			Map<String, String> result = new HashMap<>();
			String contractId = req.getParameter( "contractId" );
			diverseContractService.changeToRESICO( u.getLogin(), contractId );
			result.put( "success", "true" );
			Util.sendJSONResponse( resp, contractId );
		} catch ( Exception e ) {
			log.error( e,e );
			Util.sendJSONError( resp, e );
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

		diverseContractService = new ContratoDiversoBusinessLogic( jniName );
	}

}
