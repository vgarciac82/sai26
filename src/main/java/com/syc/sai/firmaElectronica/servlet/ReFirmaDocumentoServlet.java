/**
 * 
 */
package com.syc.sai.firmaElectronica.servlet;


import java.io.File;
import java.io.IOException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.sai.firmaElectronica.FirmaElectronicaBusinessLogic;
import com.syc.sai.firmaElectronica.interfaces.SolicitudFirmaElectronica;


/**
 * Servlet que recibe solicitud para firmar nuevamente un tramite.
 * 
 * @author Ana
 * 
 *
 */
public class ReFirmaDocumentoServlet extends HttpServlet implements GestionInterface {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	/**
	 * Log del sistema
	 */
	private static final Logger	log					= LogManager.getLogger( ReFirmaDocumentoServlet.class );
	private String jniName;
	private static String TEMP_DIR;

	@Override
	protected void doPost( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException {
		try {
			String document = req.getParameter( "DOCUMENT" );
			String folio = req.getParameter( "FOLIO" ) ;
			String reportPath = getServletContext().getRealPath( "Reportes" + File.separator );
						
			HttpSession session = req.getSession(false);
			if (session == null) {
				log.warn("No hay sesion");				
				return;
			}
			
			Usuario u = (Usuario) session.getAttribute(ATT_USER);
			
			FirmaElectronicaBusinessLogic febl = new FirmaElectronicaBusinessLogic( jniName );
			
			SolicitudFirmaElectronica sfe = febl.instanceFromWeb( document );
			sfe.setFolios( folio );
			sfe.setReportPath( reportPath );
			
			sfe.setRefirma(true);
			sfe.setUsuario( u );
			
			String result = febl.reFirmaDocumento(sfe);
			
		} catch ( Exception e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

		try {
			InitialContext ic = new InitialContext();
			TEMP_DIR = ( String ) ic.lookup( "java:comp/env/TemporaryDirectory" );
			if ( TEMP_DIR == null ) {
				TEMP_DIR = "/temp/firmaElectronica/";
				log.info( "Environment Entry \"TEMP_DIR\" nula usando default \"" + TEMP_DIR + "\"" );
			} else
				log.info( "dataSourceRefName=" + TEMP_DIR );
		} catch ( NamingException exc ) {
			TEMP_DIR = "/temp/firmaElectronica/";
			log.info( "Ocurrio un error que evito que se cargara la entrada \"TEMP_DIR\"" + exc );
			log.info( "Environment Entry \"dataSourceRefName\" no definida usando default \"" + TEMP_DIR + "\"" );
		}

		try {
			File f = new File( TEMP_DIR );
			if ( !f.exists() )
				if ( !f.mkdirs() )
					throw new Exception( "No se puede crear el directorio temporal " + TEMP_DIR );
		} catch ( Exception e ) {
			log.error( "No fue posible crear automaticamente el directorio temporal: " + TEMP_DIR + " Solicite su creacion manual" );
		}
	}

}
