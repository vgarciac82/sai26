package com.syc.reportes.servlet;


import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.log4j.Logger;

import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.reportes.CargaArchivoBusinessLogic;


public class CargaArchivoTaxiServlet extends HttpServlet {

	private static final long	serialVersionUID	= 1L;
	private static Logger		log					= Logger.getLogger( CargaArchivoTaxiServlet.class );

	private void cargaArchivo( HttpServletRequest request, HttpServletResponse response, HttpSession session ) throws Exception {

		List<?> fileItems = null;
		Iterator<?> iter = null;
		DataInputStream archivoCargaStream = null;
		File nombreDestino = null;
		
		try {
			fileItems = Util.parseRequest( request, System.getProperty( "java.io.tmpdir" ), -1 );
			iter = fileItems.iterator();

			boolean archivoRecibido = false;
			while ( iter.hasNext() ) {

				FileItem item = ( FileItem ) iter.next();

				if ( !item.isFormField() ) {
					archivoCargaStream = new DataInputStream( item.getInputStream() );
					String nombreArchivo = item.getName();

					log.info( "Copiando archivo :" + nombreArchivo );
					nombreDestino = File.createTempFile( "Carga_taxis", ".xls", new File( System.getProperty( "java.io.tmpdir" ) ) );

					Util.copiaArchivo( archivoCargaStream, nombreDestino.getAbsolutePath() );
					archivoRecibido = true;
					break;
				}
			}
			if ( !archivoRecibido )
				throw new Exception( "No se recibio archivo." );

			log.info( "Copiando archivo :" + nombreDestino );

			CargaArchivoBusinessLogic cmpBL = new CargaArchivoBusinessLogic( GestionInterface.ATT_CONEXION );
			List<String> errores = cmpBL.procesaLayoutTaxis( nombreDestino );

			if ( errores == null )
				session.setAttribute( "msg", "Archivo cargado exitosamente." );
			else
				session.setAttribute( "msg", "Atencion. El archivo no se cargo completo.\n" + Util.join( errores, '\n' ) );
			
		} catch ( Exception e ) {
			log.error( e, e );
			session.setAttribute( "msg", "Ocurrio el siguiente error: " + e.getMessage() + " intente nuevamente." );
		}
	}

	public void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
		HttpSession session = null;

		try {
			session = request.getSession( false );

			if ( session == null ) {
				response.sendRedirect( "index.jsp" );
				return;
			}

			Usuario usuario = ( Usuario ) session.getAttribute( GestionInterface.ATT_USER );
			if ( usuario == null ) {
				response.sendRedirect( "index.jsp" );
				return;
			}

			cargaArchivo( request, response, session );

			response.sendRedirect( "../Generador/CargaArchivoTaxis.jsp" );

		} catch ( Exception e ) {
			log.error( e, e );
			session.setAttribute( GestionInterface.ATT_MSG, e.toString() );
			try {
				response.sendRedirect( "../Generador/CargaArchivoTaxis.jsp?error=SI" );
			} catch ( IOException e1 ) {
				log.error( e1, e1 );
			}

		}
	}

}
