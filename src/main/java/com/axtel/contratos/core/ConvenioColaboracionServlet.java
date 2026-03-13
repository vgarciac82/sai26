package com.axtel.contratos.core;


import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.naming.InitialContext;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.axtel.web.exceptions.SessionExpiredException;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;


/**
 * Servicio para operaciones con convenios de colaboracion.
 * 
 * @author vicente.garcia
 *
 */
public class ConvenioColaboracionServlet extends HttpServlet implements GestionInterface {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 5496072673639795589L;
	private static final Logger	log					= LogManager.getLogger( ConvenioColaboracionServlet.class );
	private String				jniName;

	@Override
	protected void doPost( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException {
		
		ConvenioColaboracion cc = null;
		HttpSession session = req.getSession( false );
		String msg = "";
		Caso c = null;
		Usuario u = null;
		boolean success = false;
		try {
			if ( session == null ) {
				throw new SessionExpiredException( "Ha terminado se session. Ingrese nuevamente al sistema." );
			} else {
				c = ( Caso ) session.getAttribute( ATT_CASE );
				u = ( Usuario ) session.getAttribute( ATT_USER );

				if ( c == null || u == null ) {
					throw new ServletException( "Ha terminado se session. Ingrese nuevamente al sistema." );
				}
			}

			int folio = Integer.parseInt( c.getFolio().substring( c.getFolio().lastIndexOf( '-' ) + 1 ) );

			cc = ConvenioColaboracionBussinessLogic.instanceFromRequest( req, folio, u );
			ConvenioColaboracionBussinessLogic ccbl = new ConvenioColaboracionBussinessLogic( jniName );
			int insertados = ccbl.insertaContratoColaboracion( cc );

			log.info( "Se insertaron " + insertados + " registros exitosamente" );

			msg = "Se inserto correctamente el convenio de colaboracion";
			success = true;

		} catch ( SessionExpiredException e ) {
			log.error( e, e );
			session = req.getSession( true );
			msg = e.getMessage();
		} catch ( Exception e ) {
			log.error( e, e );
			msg = e.toString();
		}

		Map<String, Object> result = new HashMap<String, Object>();
		result.put( "success", success );
		result.put( "message", msg );

		try {

			JSONObject resultJSON = Util.toJson( result );

			resp.setContentType( "application/json;charset=UTF-8" );
			resp.setCharacterEncoding( "UTF-8" );

			PrintWriter out = resp.getWriter();
			out.println( resultJSON.toString() );
			out.flush();
			out.close();
		} catch ( Exception e ) {
			throw new ServletException( e );
		}

	}

	@Override
	public void init( ServletConfig config ) throws ServletException {
		super.init( config );

		try {
			InitialContext ic = new InitialContext();
			jniName = ( String ) ic.lookup( "java:comp/env/dataSourceRefName" );
		} catch ( Exception exc ) {
			jniName = "jdbc/gestion";
			log.info( "Environment Entry \"dataSourceRefName\" no definida usando default \"" + jniName + "\"" );
		}
		log.debug( "Environment Entry \"dataSourceRefName\" \"" + jniName + "\"" );
	}

}
