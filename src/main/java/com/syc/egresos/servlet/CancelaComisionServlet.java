package com.syc.egresos.servlet;


import java.io.IOException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.syc.egresos.core.ComisionSinViaticosBussinessLogic;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.sai.contabilidad.servlet.ResponseSender;


public class CancelaComisionServlet extends HttpServlet implements GestionInterface {

	private static final long	serialVersionUID	= -3620769987066164309L;
	private String				jniName;
	private static final Logger	log					= Logger.getLogger( CancelaComisionServlet.class );

	@Override
	protected void doPost( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException {

		String msg = "";
		log.info( "Cancelando solicitudes de comision sin viaticos" );

		HttpSession session = req.getSession( false );
		String responseType = "";
		boolean success = false;

		try {

			if ( session == null )
				throw new Exception( "Su sesion ha terminado. Ingrese nuevamente al sistema." );

			Usuario u = ( Usuario ) session.getAttribute( ATT_USER );
			if ( u == null )
				msg = "Su sesion ha terminado. Ingrese nuevamente al sistema.";

			String cancelReason = StringUtils.trimToEmpty( req.getParameter( "reason" ) );

			String[] cancel = req.getParameterValues( "chk_comision" );
			responseType = StringUtils.isBlank( req.getParameter( "responseType" ) ) ? "REDIRECT" : req.getParameter( "responseType" );

			if ( cancel == null || cancel.length == 0 )
				msg = "No se recibieron folios a cancelar.";

			if ( StringUtils.isEmpty( msg ) ) {
				ComisionSinViaticosBussinessLogic csvbl = new ComisionSinViaticosBussinessLogic( jniName );
				csvbl.cancelaSolicitudFirmaElectronica( cancel[0], cancelReason, u );
				
			}
			
			success = true;
			
		} catch ( Exception e ) {
			log.error( e, e );
			msg += e.toString();
		}

		if ( "REDIRECT".equalsIgnoreCase( responseType ) ) {
			if ( session != null )
				session.setAttribute( "RESULT", msg );
			else {
				resp.sendRedirect( "index.jsp" );
				return;
			}

			resp.sendRedirect( "Generador/CancelaSolicitudesNoPresupuestales.jsp" );
		} else if ( "JSON".equalsIgnoreCase( responseType ) ) {
			ResponseSender.sendClientSimpleMessage( resp, success, msg );
		}
	}

	@Override
	public void init( ServletConfig config ) throws ServletException {
		// TODO Auto-generated method stub
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
