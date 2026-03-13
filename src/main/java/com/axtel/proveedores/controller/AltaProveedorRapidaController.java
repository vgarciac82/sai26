package com.axtel.proveedores.controller;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.jenkov.prizetags.tree.itf.ITree;
import com.syc.altaproveedor.AltaProveedorBusinessLogic;
import com.syc.altaproveedor.DatosProveedor;
import com.syc.cuentasbancarias.DocBancarioBusinessLogic;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;


@WebServlet( urlPatterns = { "/altaProveedorRapida", "/altaProveedorRapida/avanzaAutorizacion", "/altaProveedorRapida/autorizaProveedor", "/altaProveedorRapida/rechazaProveedor", "/altaProveedor/eliminaCuenta" } )
@MultipartConfig
public class AltaProveedorRapidaController extends HttpServlet {

	/**
	 * 
	 */
	private static final long			serialVersionUID	= -3010861657439379922L;
	private static final Logger			log					= Logger.getLogger( AltaProveedorRapidaController.class );
	private String						jniName;
	private AltaProveedorBusinessLogic	apbl;
	private DocBancarioBusinessLogic	dbbl;
	private CasoBusinessLogic			cbl					= null;

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

		apbl = new AltaProveedorBusinessLogic( jniName );
		cbl = new CasoBusinessLogic( jniName );
		dbbl = new DocBancarioBusinessLogic( jniName );
	}

	@Override
	protected void doPost( HttpServletRequest request, HttpServletResponse response ) throws IOException {

		log.info( "Saving/Updating Alta Proveedor Rapida." );
		request.setCharacterEncoding( "UTF-8" );
		try {

			String action = request.getRequestURI().substring( request.getRequestURI().lastIndexOf( "/" ) + 1 );

			HttpSession session = request.getSession( false );
			if ( session == null )
				throw new RuntimeException( "Sin session. Ingrese nuevamente al sistema" );

			Usuario user = ( Usuario ) session.getAttribute( GestionInterface.ATT_USER );
			if ( user == null )
				throw new RuntimeException( "Sin session. Ingrese nuevamente al sistema" );

			if ( action.equalsIgnoreCase( "rechazaProveedor" ) ) {
				String observaciones = request.getParameter( "observacionesRechazo" );
				String folio = request.getParameter( "folio" );
				apbl.rechazaProveedor( user, folio, observaciones );
				Map<String, String> resultado = new HashMap<>();
				resultado.put( "success", "true" );
				resultado.put( "put", "folio" );
			} else if ( action.equalsIgnoreCase( "autorizaProveedor" ) ) {

				String folio = request.getParameter( "folio" );
				apbl.autorizaAltaRapidaProveedor( user, folio );
				Map<String, String> resultado = new HashMap<>();
				resultado.put( "success", "true" );
				resultado.put( "put", "folio" );
				
			} else if ( action.equalsIgnoreCase( "avanzaAutorizacion" ) ) {

				String folio = request.getParameter( "folio" );

				apbl.avanzaOperacionAltaRapida( user, folio, "AUTORIZA_ALTA_PROVEEDOR_CORTA", "autoriza_proveedor_corta" );
				Map<String, String> resultado = new HashMap<>();
				resultado.put( "success", "true" );
				resultado.put( "put", "folio" );
			} else {
				DatosProveedor datosProveedor = instanceFromRequest( request );
				apbl.guardaInfoProveedorCorta( datosProveedor, user );

				Util.sendJSON( response, datosProveedor );
			}
		} catch ( Exception ex ) {
			log.error( ex, ex );
			Util.sendJSONError( response, ex );
		}
	}

	@Override
	protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws IOException {

		log.info( "Getting Alta Proveedor Rapida." );

		try {

			HttpSession session = request.getSession( false );
			if ( session == null )
				throw new RuntimeException( "Sin session. Ingrese nuevamente al sistema" );

			Usuario user = ( Usuario ) session.getAttribute( GestionInterface.ATT_USER );
			if ( user == null )
				throw new RuntimeException( "Sin session. Ingrese nuevamente al sistema" );

			String folio = request.getParameter( "folio" );

			Util.sendJSON( response, apbl.getAltaProveedorMap( folio ) );

		} catch ( Exception ex ) {
			log.error( ex, ex );
			Util.sendJSONError( response, ex );
		}
	}

	private DatosProveedor instanceFromRequest( HttpServletRequest request ) {
		DatosProveedor proveedor = new DatosProveedor();
		proveedor.setcFolio( request.getParameter( "folio" ) );

		proveedor.setnTipoPersona( Integer.parseInt( request.getParameter( "tipoPersona" ) ) );
		proveedor.setIdRegimenFiscal( Integer.parseInt( request.getParameter( "regimenFiscal" ) ) );
		proveedor.setnPyme( Integer.parseInt( request.getParameter( "pyme" ) ) );

		proveedor.setRfc1( request.getParameter( "rfc1" ) );
		proveedor.setRfc2( request.getParameter( "rfc2" ) );
		proveedor.setRfc3( request.getParameter( "rfc3" ) );
		proveedor.setRfc( request.getParameter( "rfc1" ) + "-" + request.getParameter( "rfc2" ) + "-" + request.getParameter( "rfc3" ) );

		proveedor.setcRazonSocial( request.getParameter( "razonSocial" ) );
		proveedor.setcCurp( request.getParameter( "curp" ) );

		proveedor.setcNombre( request.getParameter( "nombre" ) );
		proveedor.setcApellidoMat( request.getParameter( "apellidoMaterno" ) );
		proveedor.setcApellidoPat( request.getParameter( "apellidoPaterno" ) );
		proveedor.setcGiro( request.getParameter( "giro" ) );

		proveedor.setnEntidadFederativa( Integer.parseInt( request.getParameter( "estado" ) ) );
		proveedor.setnMunicipio( Integer.parseInt( request.getParameter( "municipio" ) ) );
		proveedor.setcCalle( request.getParameter( "calle" ) );
		proveedor.setcNumeroExt( request.getParameter( "noExterior" ) );
		proveedor.setcNumeroInt( request.getParameter( "noInterior" ) );
		proveedor.setcColonia( request.getParameter( "colonia" ) );
		proveedor.setcCodigoPost( request.getParameter( "codigoPostal" ) );

		proveedor.setcEmail( request.getParameter( "correo" ) );
		proveedor.setnTipoTelefono( Integer.parseInt( request.getParameter( "tipoTelefono" ) ) );
		proveedor.setcTelefono( request.getParameter( "telefono" ) );

		proveedor.setcTipoPB( "PROVEEDOR" );
		proveedor.setcPais( "México" );
		proveedor.setEsAltaRapida( "S" );

		return proveedor;

	}

	@Override
	protected void doDelete( HttpServletRequest request, HttpServletResponse response ) throws IOException {
		String action = request.getRequestURI().substring( request.getRequestURI().lastIndexOf( "/" ) + 1 );
		HttpSession session = null;
		Usuario user = null;
		try {

			session = request.getSession( false );
			if ( session == null )
				throw new RuntimeException( "Sin session. Ingrese nuevamente al sistema" );

			user = ( Usuario ) session.getAttribute( GestionInterface.ATT_USER );
			if ( user == null )
				throw new RuntimeException( "Sin session. Ingrese nuevamente al sistema" );

			if ( "eliminaCuenta".equals( action ) ) {
				log.info( "Deleting document from expedient." );

				Caso c = ( Caso ) session.getAttribute( GestionInterface.ATT_CASE );
				if ( c == null )
					throw new RuntimeException( "No hay session. Ingrese nuevamente al sistema" );

				String clabeEliminar = request.getParameter( "clabeEliminar" );
				String folio = request.getParameter( "folio" );

				log.info( "Eliminando Cuenta: " + clabeEliminar );

				dbbl.eliminaCtaBancaria( folio, clabeEliminar );

				ITree tree = cbl.getArbolCaso( c );

				session.setAttribute( GestionInterface.ATT_CASE, c );
				session.setAttribute( "tree.model", tree );

				Map<String, String> result = new HashMap<>();
				result.put( "success", "true" );
				result.put( "message", "El archivo se elimino correctamente." );
				Util.sendJSON( response, result );
			} else if ( "altaProveedorRapida".equals( action ) ) {
				String rfc = request.getParameter( "rfc" );
				String folio = request.getParameter( "folio" );

				apbl.deleteAltaProveedor( user, rfc, folio );
			}
		} catch ( Exception ex ) {
			log.error( ex, ex );
			Util.sendJSONError( response, ex );
		}

	}
}
