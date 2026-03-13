package com.syc.sai.contabilidad.polizamanual.servlet;


import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.syc.sai.contabilidad.polizamanual.CatalogoCabms;
import com.syc.sai.contabilidad.polizamanual.CatalogoPartida;	
import com.syc.sai.contabilidad.polizamanual.Cuentas;
import com.syc.sai.contabilidad.polizamanual.controller.CatalogoCabmsBusinessLogic;
import com.syc.sai.contabilidad.polizamanual.controller.CatalogoPartidaBusinessLogic;
import com.syc.sai.contabilidad.polizamanual.controller.CuentasBusinessLogic;
import com.syc.sai.contabilidad.polizamanual.controller.DocPolizaEncabezadoBusinessLogic;
import com.syc.sai.contabilidad.servlet.ResponseSender;

import common.Logger;


public class PolizaManualServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 964438170851267336L;
	private static final Logger	log					= Logger.getLogger( PolizaManualServlet.class );
	private static String		jniName				= "jdbc/gestion";

	/**
	 * Constructor of the object.
	 */
	public PolizaManualServlet( ) {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	private void saveOrUpdateDocPolizaEncabezado( HttpServletRequest req, HttpServletResponse resp ) throws Exception {
		DocPolizaEncabezadoBusinessLogic bl = new DocPolizaEncabezadoBusinessLogic();

		bl.saveOrUpdateDocPolizaEncabezado( req );

		String arr = "";
		arr = "[" + arr + "]";
		log.debug( arr );
		resp.setContentType( "text/html" );
		PrintWriter o = resp.getWriter();
		o.print( arr );
		o.flush();
		o.close();
	}

	private void autocompleteCabms( HttpServletRequest req, HttpServletResponse resp ) throws Exception {
		String nGrupo = ( String ) req.getSession().getAttribute( "nGrupo" );
		String nSubGrupo = ( String ) req.getSession().getAttribute( "nSubGrupo" );
		String nEvento = ( String ) req.getSession().getAttribute( "nEvento" );

		String arr = "";
		String token = "";
		String json = "{\"value\":\"%s\",\"label\":\"%s\",\"descripcion\":\"%s\"}";

		if ( nGrupo != null ) {
			if ( nSubGrupo != null ) {
				if ( nEvento != null ) {
					CatalogoCabmsBusinessLogic bl = new CatalogoCabmsBusinessLogic();
					List<CatalogoCabms> l = bl.autocompleteCabms( req );
					for ( int i = 0; i < l.size(); i++ ) {
						arr += token + String.format( json, l.get( i ).getCcabms(), l.get( i ).getCcabms() + " (" + l.get( i ).getCdescripcion() + ")", l.get( i ).getCdescripcion() );
						token = ",";
					}
					arr = "[" + arr + "]";
				} else {
					arr += token + String.format( json, "0", "Debe seleccionar un Evento", "Debe seleccionar un Evento" );
				}
			} else {
				arr += token + String.format( json, "0", "Debe seleccionar un SubGrupo", "Debe seleccionar un SubGrupo" );
			}
		} else {
			arr += token + String.format( json, "0", "Debe seleccionar un grupo", "Debe seleccionar un grupo" );
		}
		log.debug( arr );
		resp.setContentType( "text/html" );
		PrintWriter o = resp.getWriter();
		o.print( arr );
		o.flush();
		o.close();
	}

	private void getCuentasByPartida( HttpServletRequest req, HttpServletResponse resp ) throws Exception {
		CuentasBusinessLogic bl = new CuentasBusinessLogic();

		Hashtable<String, List<Cuentas>> h = bl.getCuentasByPartida( req );

		String arr = "";
		String token = "";
		String json = "{\"value\":\"%s\",\"label\":\"%s\",\"descripcion\":\"%s\",\"cEvento\":\"%s\"}";

		boolean listAbonoEmpty = false;
		boolean listCargoEmpty = false;

		String cIdGrupoEvento = req.getParameter( "cIdGrupoEvento" );
		String cIdSubGrupoEvento = req.getParameter( "cIdSubGrupoEvento" );
		String cIdeventoManual = req.getParameter( "cIdeventoManual" );
		String cPartida = req.getParameter( "cPartida" );

		String parametros = " cIdGrupoEvento=" + cIdGrupoEvento + " AND " + "cIdSubGrupoEvento=" + cIdSubGrupoEvento + " AND " + "cIdeventoManual=" + cIdeventoManual;
		String resultadoPartida = bl.checkEvent( parametros );

		if ( ( resultadoPartida.equals( "0" ) ) && ( cPartida != "" ) ) {
			arr += token + String.format( json, "error", "PARA ESTE EVENTO NO SE CAPTURA LA PARTIDA", "PARA ESTE EVENTO NO SE CAPTURA LA PARTIDA", "" );

		} else {

			List<Cuentas> l = h.get( "ABONO" );
			listAbonoEmpty = l.isEmpty();

			for ( int i = 0; i < l.size(); i++ ) {
				arr += token + String.format( json, l.get( i ).getNcuenta(), l.get( i ).getNcuenta() + "(" + l.get( i ).getDcuenta() + ")", l.get( i ).getDcuenta(), "CARGO" );
				token = ",";
			}

			l.clear();
			l = h.get( "CARGO" );
			for ( int i = 0; i < l.size(); i++ ) {
				arr += token + String.format( json, l.get( i ).getNcuenta(), l.get( i ).getNcuenta() + "(" + l.get( i ).getDcuenta() + ")", l.get( i ).getDcuenta(), "ABONO" );
				token = ",";
			}

			listCargoEmpty = l.isEmpty();

			if ( listCargoEmpty && listAbonoEmpty ) {
				arr += token + String.format( json, "error", "Combinacion de evento - partida invalida", "Combinacion de evento - partida invalida", "" );
			}
		} /// fin del else
		arr = "[" + arr + "]";
		arr = new String( arr.getBytes( "ISO-8859-1" ), "UTF-8" );
		log.debug( arr );
		resp.setContentType( "text/html" );
		PrintWriter o = resp.getWriter();
		o.print( arr );
		o.flush();
		o.close();
	}

	private void autocompleteCatalogoPartida( HttpServletRequest req, HttpServletResponse resp ) throws Exception {
		CatalogoPartidaBusinessLogic bl = new CatalogoPartidaBusinessLogic();

		List<CatalogoPartida> l = bl.autocompleteCatalogoPartida( req );

		String arr = "";
		String token = "";
		String json = "{\"value\":\"%s\",\"label\":\"%s\",\"descripcion\":\"%s\"}";
		for ( int i = 0; i < l.size(); i++ ) {
			arr += token + String.format( json, l.get( i ).getCpartida(), l.get( i ).getCpartida() + "(" + l.get( i ).getDpartida() + ")", l.get( i ).getDpartida() );
			token = ",";
		}
		arr = "[" + arr + "]";
		log.debug( arr );
		resp.setContentType( "text/html" );
		PrintWriter o = resp.getWriter();
		o.print( arr );
		o.flush();
		o.close();
	}

	public void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {

		HttpSession session = request.getSession( false );
		if ( session == null )
			throw new ServletException( "Su session a caducado" );

		String tipoReporte = request.getParameter( "TIPO_REPORTE" );
		if ( "PolizaManual.jasper".equals( tipoReporte ) ) {
			String reportPath = getServletContext().getRealPath( "Reportes" + File.separator + tipoReporte );
			String ruta = getServletContext().getRealPath( "Reportes" + File.separator );
			String nFolioPoliza = request.getParameter( "nFolioPoliza" );
			String cCentroContable = request.getParameter( "cCentroContable" );
			int poliza = Integer.parseInt( nFolioPoliza );

			DocPolizaEncabezadoBusinessLogic bl = new DocPolizaEncabezadoBusinessLogic( jniName );

			try {
				bl.imprimePoliza( request, response, reportPath, reportPath, ruta, poliza, cCentroContable );

			} catch ( Exception e ) {
				log.error( e, e );
				throw new ServletException( e );
			}
		} else {
			doPost( request, response );
		}
	}

	/**
	 * The doPost method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to
	 * post.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doPost( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException {

		HttpSession session = req.getSession( false );
		if ( session == null ) {
			ResponseSender.sendError( resp, "Sin session. Por favor reingrese al sistema." );
			return;
		}

		String accion = req.getParameter( "accion" );
		if ( "".equals( accion ) || null == accion ) {
			try {
				accion = req.getRequestURI().substring( req.getRequestURI().lastIndexOf( "/" ) + 1 );
			} catch ( Exception e ) {
				log.warn( "Error obteniendo substring " + e );
			}

			if ( "".equals( accion ) || null == accion ) {
				ResponseSender.sendError( resp, "No se recibio el parametro \"accion\" reporte al administrador" );
				return;
			}
		}

		if ( "saveOrUpdateDocPolizaEncabezado".equals( accion ) ) {

			try {
				saveOrUpdateDocPolizaEncabezado( req, resp );
			} catch ( Exception e ) {
				log.error( e, e );
			}

		} else if ( "autocompleteCabms".equals( accion ) ) {
			try {
				autocompleteCabms( req, resp );
			} catch ( Exception e ) {
				log.error( e, e );
			}

		} else if ( "getCuentasByPartida".equals( accion ) ) {

			try {
				getCuentasByPartida( req, resp );
			} catch ( Exception e ) {
				log.error( e, e );
			}
		} else if ( "autocompleteCatalogoPartida".equals( accion ) ) {
			try {
				autocompleteCatalogoPartida( req, resp );
			} catch ( Exception e ) {
				log.error( e, e );
			}
		}
	}

}
