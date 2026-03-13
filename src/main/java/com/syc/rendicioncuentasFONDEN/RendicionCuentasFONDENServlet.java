package com.syc.rendicioncuentasFONDEN;


import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONObject;

import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.obrapublica.ConfiguraAplicativoBusinessLogic;
import com.syc.ws.fonden.PolizaAutomatica;
import com.syc.ws.inventario.GenericConnectionWS;


public class RendicionCuentasFONDENServlet extends HttpServlet implements GestionInterface {

	private static final long			serialVersionUID	= 6723127616859732249L;
	private static final Logger			log					= Logger.getLogger( RendicionCuentasFONDENServlet.class );
	private static String				jniName				= "jdbc/gestion";
	private static Map<String, String>	plantillas			= null;

	@Override
	protected void doGet( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException {

		HttpSession session = req.getSession( false );
		if ( session == null )
			throw new ServletException( "Su session a caducado" );

		Usuario u = ( Usuario ) session.getAttribute( ATT_USER );
		if ( u == null )
			throw new ServletException( "Su session a caducado" );

		RendicionCuentasFONDENBusinessLogic rrs = new RendicionCuentasFONDENBusinessLogic( jniName );

		try {
			rrs.generaCedula( req, resp, plantillas ); // vista previa
		} catch ( Exception e ) {
			log.error( e, e );
			throw new ServletException( e );
		}

	}

	protected void doPost( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException {
		HttpSession session = req.getSession( false );
		String msg = "";
		int nFolioDocPoliza = 0;

		try {

			if ( session == null ) {
				session = req.getSession( true );
				throw new ServletException( "Su session a caducado" );
			}

			Usuario u = ( Usuario ) session.getAttribute( ATT_USER );
			if ( u == null )
				throw new ServletException( "Su session a caducado" );

			String fAplicacion = req.getParameter( "fecha_aplicacion" );

			if ( StringUtils.isEmpty( fAplicacion ) )
				throw new Exception( "No se recibio la fecha de aplicacion." );

			RendicionCuentasFONDENBusinessLogic docEnc = new RendicionCuentasFONDENBusinessLogic( GestionInterface.ATT_CONEXION );
			ConfiguraAplicativoBusinessLogic cabl = new ConfiguraAplicativoBusinessLogic( jniName );

			String unidadEjecutora = cabl.getSystemSetting( "UE_POLIZA_AUTOMATICA" );
			String centroContable = cabl.getSystemSetting( "CC_POLIZA_AUTOMATICA" );
			String login = cabl.getSystemSetting( "LOGIN_POLIZA_AUTOMATICA" );
			String nombreUsuario = cabl.getSystemSetting( "USUARIO_POLIZA_AUTOMATICA" );
			String url = cabl.getSystemSetting( "URL_WS_POLIZA_AUTOMATICA" );
			PolizaAutomatica poliza = docEnc.generaPolizaFONDEN( fAplicacion, unidadEjecutora, centroContable, login, nombreUsuario );

			JSONObject inputJson = Util.toJson( poliza );
			// Se llama al web service para crear la poliza en SAI

			GenericConnectionWS gcws = new GenericConnectionWS();
			String respStr = gcws.connectionWebService( url, "POST", "application/json;charset=UTF-8", inputJson );
			RespuestaPolizaWS respuesta = new ObjectMapper().readValue( respStr, RespuestaPolizaWS.class );
			
			if( respuesta.getIdEstatus() > 0 ) {
				nFolioDocPoliza = respuesta.getFolioDocPoliza();
				msg = "Se genero exitosamente la poliza con folio: " + nFolioDocPoliza;
			}else {
				msg = "Ocurrio el siguiente error al generar la poliza: " + respuesta.getEstatus();
			}
			session.setAttribute( "RESULT", msg );
			session.setAttribute( "success", "true" );

		} catch ( Exception e ) {
			log.error( e, e );
			msg = "Ocurrio el siguiente error: " + e.getMessage();
			session.setAttribute( "success", "false" );
			session.setAttribute( "RESULT", msg );
		}

		resp.sendRedirect( "../Generador/GeneraRendicionCuentasFONDEN.jsp" );
		return;

	}

	@Override
	public void init( ServletConfig config ) throws ServletException {
		super.init( config );
		synchronized ( this ) {
			if ( plantillas == null ) {
				plantillas = new HashMap<String, String>();

				plantillas.put( "VISTAPREVIA", getServletContext().getRealPath( "Reportes" + File.separator + "Plantilla_VistaPreviaRendicionCuentasFONDEN.xls" ) );
			}
		}
	}
}
