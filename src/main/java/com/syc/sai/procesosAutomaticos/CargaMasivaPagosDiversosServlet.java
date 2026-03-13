package com.syc.sai.procesosAutomaticos;


import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.log4j.Logger;

import com.axtel.contratos.core.ConvenioColaboracion;
import com.axtel.contratos.core.ConvenioColaboracionBussinessLogic;
import com.syc.contable.PagosDiversosBussinessLogic;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;


public class CargaMasivaPagosDiversosServlet extends HttpServlet implements GestionInterface {

	private static String		jniName				= "jdbc/gestion";
	private static final Logger	log					= Logger.getLogger( CargaMasivaPagosDiversosServlet.class );
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -651372170523768237L;
	private static final String	TEMP_DIR			= System.getProperty( "java.io.tmpdir" ) + File.separatorChar + "cargaMasivo" + File.separatorChar;
	private String reportDir;

	@Override
	protected void doPost( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException {

		log.info( "Iniciando proceso de carga masiva de solicitudes" );
		List<?> fileItems = null;
		Iterator<?> iter = null;
		InputStream archivoCargaIS = null;
		DataInputStream archivoCargaStream = null;
		String nombreDestino = "";
		String nombreArchivo = "";

		HttpSession session = req.getSession( false );

		try {

			if ( session == null ) {
				resp.sendRedirect( "index.jsp" );
				return;
			}

			Usuario u = ( Usuario ) session.getAttribute( ATT_USER );

			if ( u == null ) {
				resp.sendRedirect( "index.jsp" );
				return;
			}

			fileItems = Util.parseRequest( req, TEMP_DIR, -1 );
			iter = fileItems.iterator();

			while ( iter.hasNext() ) {
				FileItem item = ( FileItem ) iter.next();

				String extension = Util.getFileExtencion( item.getName() );

				if ( !"xlsx".equalsIgnoreCase( extension ) )
					throw new Exception( "No se puede procesar archivos [" + extension + "].\nCorrija e intente de nuevo" );

				
				archivoCargaIS = item.getInputStream();
				archivoCargaStream = new DataInputStream( archivoCargaIS );
				nombreArchivo = item.getName();
				nombreDestino = TEMP_DIR + "CARGA_ARCHIVO_" + System.currentTimeMillis() + "." + extension;

				log.info( "Copiando archivo :" + nombreArchivo );
				Util.copiaArchivo( archivoCargaStream, nombreDestino );

				item.delete();

				log.debug( "Procesando archivo:" + nombreArchivo );

			}

			String accion = req.getRequestURI().substring( req.getRequestURI().lastIndexOf( "/" ) + 1 );

			if ( "CargaMasivaCompromisosDiversos".equals( accion ) ) {
				ConvenioColaboracionBussinessLogic ccbl = new ConvenioColaboracionBussinessLogic();
				List<ConvenioColaboracion> convenios = ccbl.generaConveniosPagoBeneficiarioMasivo( new File( nombreDestino ), u );

				session.setAttribute( "msg", ConvenioColaboracion.toSummaryString( convenios ).toString() );
				resp.sendRedirect( "CargaLayouts/CargarPagosDiversosMasivo.jsp?showResult=true" );
			} else {
				PagosDiversosBussinessLogic pdbl = new PagosDiversosBussinessLogic();

				int insertados = pdbl.procesaPagosMasivo( new File( nombreDestino ), u,reportDir  );
				log.trace( "Terminado el llamado:\n" + insertados );
				session.setAttribute( "msg", "Se insertaron " + insertados + " pagos diversos. Por favor realizar el proceso de firma." );
				resp.sendRedirect( "CargaLayouts/CargarPagosDiversosMasivo.jsp?showResult=true" );
			}
		} catch ( Exception e ) {

			session.setAttribute( "msg", "Ocurrio el siguiente error: \n" + e.getMessage() + "\n " );
			resp.sendRedirect( "CargaLayouts/CargarPagosDiversosMasivo.jsp?showResult=true" );

		} finally {
			if ( archivoCargaStream != null )
				try {
					archivoCargaStream.close();
				} catch ( Exception e ) {
					log.error( "Error cerrando flujo DataInputStream" + e );
				}
			if ( archivoCargaIS != null )
				try {
					archivoCargaIS.close();
				} catch ( Exception e ) {
					log.error( "Error cerrando flujo InputStream" + e );
				}
			archivoCargaIS = null;
			archivoCargaStream = null;

			if ( !"".equals( nombreDestino ) ) {
				File toDelete = new File( nombreDestino );
				if ( !toDelete.delete() )
					toDelete.deleteOnExit();
			}
		}

	}

	@Override
	public void init( ServletConfig config ) throws ServletException {
		super.init( config );
		try {
			reportDir = getServletContext().getRealPath("Reportes" + File.separator);
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
			File f = new File( TEMP_DIR );

			if ( !f.exists() )
				f.mkdir();
		} catch ( Exception e ) {
			log.error( "No se logro crear el directorio temporal: " + TEMP_DIR + " Causa:" + e );
		}
	}
}
