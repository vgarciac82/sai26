package com.syc.gestion.servlet;


import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

import com.axtel.contratos.ActionsFIEL;
import com.axtel.contratos.QuestionnaireBussinessLogic;
import com.axtel.contratos.core.ImplementsActionsFIEL_ENSA;
import com.syc.cfdi.utils.FacturaUtils;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.reportes.ConciliacionFirma;
import com.syc.gestion.reportes.EstadosFinancierosFirma;
import com.syc.gestion.util.Util;
import com.syc.obrapublica.ConfiguraAplicativoBusinessLogic;
import com.syc.obrapublica.FirmaAutorizacionObraInterface;
import com.syc.obrapublica.businessLogic.FirmaAutEstObraBusinessLogic;
import com.syc.sai.firmaElectronica.FirmaElectronicaBusinessLogic;
import com.syc.sai.firmaElectronica.core.FIELChecker;
import com.syc.sai.firmaElectronica.core.RecepcionMaterialFIEL;
import com.syc.sai.firmaElectronica.interfaces.SolicitudFirmaElectronica;


public class FirmaReporteServlet extends HttpServlet implements GestionInterface {

	private static final long					serialVersionUID	= 240044205695553539L;
	private static String						TEMP_DIR			= "";
	private static String						jniName				= "jdbc/gestion";
	private static final Logger					log					= Logger.getLogger( FirmaReporteServlet.class );
	private static Map<String, String>			IMPLEMENTATIONS		= new HashMap<String, String>();
	private ConfiguraAplicativoBusinessLogic	settings			= null;

	@Override
	protected void doPost( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException {

		HttpSession session = req.getSession( false );
		String msgRetorno = "";
		Usuario u = null;
		String tipoAutorizacion = "";
		String urlRespuesta = "../Generador/ReportesFirma.jsp";
		String tipoPago = null;

		int orden = -1;
		String foliosReporte = null;

		Map<String, String> objMap = new HashMap<String, String>();
		int daysTolerance = ( settings.getSystemSetting( "DAYS_TOLERANCE" ) == null ? 30 : Integer.parseInt( settings.getSystemSetting( "DAYS_TOLERANCE" ) ) );
		if ( session == null ) {
			msgRetorno = "Su sesion a terminado. Por favor ingrese nuevamente al sistema.";
		} else {

			u = ( Usuario ) session.getAttribute( ATT_USER );

			if ( u == null ) {
				msgRetorno = "Su sesion a terminado. Por favor ingrese nuevamente al sistema.";
			}
		}

		if ( "".equals( msgRetorno ) ) {

			String reportPath = getServletContext().getRealPath( "Reportes" + File.separator );
			List<?> fileItems = null;
			Iterator<?> iter = null;
			String cerFileName = "";
			String keyFileName = "";
			DataInputStream archivoCargaStreamCer = null;
			DataInputStream archivoCargaStreamKey = null;
			FirmaAutorizacionObraInterface interfaceObra = null;
			ActionsFIEL actions=null;
			try {

				fileItems = Util.parseRequest( req, TEMP_DIR, -1 );
				iter = fileItems.iterator();

				while ( iter.hasNext() ) {
					FileItem item = ( FileItem ) iter.next();
					if ( item.isFormField() ) {
						log.trace( item.getFieldName() + " = " + item.getString() );
						objMap.put( item.getFieldName(), item.getString() );
						item.delete();
						continue;
					} else {

						if ( "cerFile".equals( item.getFieldName() ) ) {
							archivoCargaStreamCer = new DataInputStream( item.getInputStream() );
							cerFileName = FacturaUtils.generaNombreArchivoTemporal( TEMP_DIR, item.getName(), "cer" );
							log.info( "Copiando archivo :" + cerFileName );
							Util.copiaArchivo( archivoCargaStreamCer, cerFileName );
						} else if ( "keyFile".equals( item.getFieldName() ) ) {
							archivoCargaStreamKey = new DataInputStream( item.getInputStream() );
							keyFileName = FacturaUtils.generaNombreArchivoTemporal( TEMP_DIR, item.getName(), "key" );
							log.info( "Copiando archivo :" + keyFileName );
							Util.copiaArchivo( archivoCargaStreamKey, keyFileName );
							if ( archivoCargaStreamKey != null )
								try {
									archivoCargaStreamKey.close();
								} catch ( Exception e ) {
									log.error( "Error cerrando flujo DataInputStream" + e );
								}
						}
					}

					item.delete();

				}

				if ( objMap.containsKey( "urlRetorno" ) )
					urlRespuesta = objMap.get( "urlRetorno" );

				try {

					long daysUntilExpiry = FIELChecker.isCertificateExpiringSoon( cerFileName );
					
					if ( daysUntilExpiry <= daysTolerance ) {
						session.setAttribute( "EXPIRING_SOON", true );
						if ( daysUntilExpiry <= 0 )
							session.setAttribute( "EXPIRING_MSG", "Su firma electronica caducó desde hace: " + daysUntilExpiry + " dias. Por favor inicie la renovacion lo antes posible." );
						else
							session.setAttribute( "EXPIRING_MSG", "Su firma electronica caducará en menos de " + daysUntilExpiry + " dias. Por favor inicie la renovacion lo antes posible." );
					}

				} catch ( Exception e ) {
					log.warn( "No fue posible determinar la vigencia de la  FIEL " + e, e );
				}

				tipoPago = objMap.get( "tipoPagoSeleccionado" );

				FirmaElectronicaBusinessLogic febl = new FirmaElectronicaBusinessLogic( jniName );

				SolicitudFirmaElectronica fer = null;
				if ( IMPLEMENTATIONS.get( tipoPago ) != null ) {
					ClassLoader cl = getClass().getClassLoader();
					Class<?> clase = cl.loadClass( IMPLEMENTATIONS.get( tipoPago ) );
					fer = ( SolicitudFirmaElectronica ) clase.newInstance();
				} else if ( "CONCILIABANCOS".equals( tipoPago )) {
					fer = new ConciliacionFirma();
				} else {
					fer = new EstadosFinancierosFirma();
					if ( objMap.get( "orden" ) != null ) {
						orden = Integer.parseInt( objMap.get( "orden" ) );
						( ( EstadosFinancierosFirma ) fer ).setOrden( orden );
					} else if ( objMap.get( "ordenes" ) != null ) {
						( ( EstadosFinancierosFirma ) fer ).setOrdenes( ( String ) objMap.get( "ordenes" ) );
					} 
				}
				
				foliosReporte = objMap.get( "nFolios" );
				fer.setFolios( foliosReporte );
				fer.setFileExtension( "pdf" );
				fer.setPasswordLlave( objMap.get( "passwordLlave" ) );
				fer.setReportPath( reportPath );
				fer.setRfcFirma( objMap.get( "rfcFirma" ) );
				fer.setTipoAutorizacion( objMap.get( "tipoAutorizacion" ) );
				fer.setUsuario( u );

				//if ( !"CONTRATODIVERSO".equals( tipoPago ) && !"APARTADO".equals( tipoPago ) && !"OBRAPUBLICA".equals( tipoPago ) && !"CONCILIABANCOS".equals( tipoPago ) )
					//	throw new Exception( "No se recibio orden de firma." );
				//}

				fer.setDocument( tipoPago );
				List<String> logFirma = null;
				String logConciliacion ="";
				if ( "CONTRATODIVERSO".equals( tipoPago ) )
					logFirma = febl.firmaRecepcionMaterial( ( RecepcionMaterialFIEL ) fer, cerFileName, keyFileName );
				else if ( "APARTADO".equals( tipoPago ) )
					logFirma = febl.firmaCuestionario( ( QuestionnaireBussinessLogic ) fer, cerFileName, keyFileName );
				else if ( "OBRAPUBLICA".equals( tipoPago ) ) {
					interfaceObra = new FirmaAutEstObraBusinessLogic();
					logFirma = interfaceObra.autoriza( fer, cerFileName, keyFileName );
				} else if ( "CONCILIABANCOS".equals( tipoPago ) ) {
					 logConciliacion =  febl.firmaConciliacion( ( ConciliacionFirma ) fer, cerFileName, keyFileName );
				} else if ( "ENTERASATISFACCION".equals( tipoPago ) ) {
					actions=new ImplementsActionsFIEL_ENSA();
					logFirma=actions.sign( fer, cerFileName, keyFileName );
				}else
					logFirma = febl.firmaReporte( ( EstadosFinancierosFirma ) fer, cerFileName, keyFileName );
				if ( "CONCILIABANCOS".equals( tipoPago ) )
					msgRetorno = logConciliacion;
				else
					msgRetorno = Util.listToHTMLTable( logFirma );

			} catch ( Exception e ) {

				log.error( e, e );
				List<String> logException = new ArrayList<String>();
				logException.add( "Ocurrio el siguiente error al cargar el archivo: <br>" + e.getMessage() );
				msgRetorno = Util.listToHTMLTable( logException );

			} finally {
				if ( archivoCargaStreamCer != null )
					try {
						archivoCargaStreamCer.close();
					} catch ( Exception e ) {
						log.error( "Error cerrando flujo DataInputStream" + e );
					}
				archivoCargaStreamCer = null;

				if ( !"".equals( cerFileName ) ) {
					File toDeleteCer = new File( cerFileName );
					if ( !toDeleteCer.delete() )
						toDeleteCer.deleteOnExit();
				}

				if ( archivoCargaStreamKey != null )
					try {
						archivoCargaStreamKey.close();
					} catch ( Exception e ) {
						log.error( "Error cerrando flujo DataInputStream" + e );
					}

				archivoCargaStreamKey = null;

				if ( !"".equals( keyFileName ) ) {
					File toDeleteKey = new File( keyFileName );
					if ( !toDeleteKey.delete() )
						toDeleteKey.deleteOnExit();
				}
				interfaceObra = null;
				actions=null;
			}
		} else {
			resp.sendRedirect( urlRespuesta + "?TYPE=" + tipoAutorizacion + "&msgError=" + msgRetorno + "&a=" + orden );
		}

		session.setAttribute( "RESULT", msgRetorno );
		resp.sendRedirect( urlRespuesta + "?TYPE=" + tipoAutorizacion + "&a=" + orden + "&d=" + objMap.get( "tipoPagoSeleccionado" ) + "&f=" + objMap.get( "nFolios" ) + "&o=" + orden );

	}

	@Override
	public void init( ServletConfig config ) throws ServletException {
		super.init( config );
		try {
			IMPLEMENTATIONS.put( "CONTRATODIVERSO", "com.syc.sai.firmaElectronica.core.RecepcionMaterialFIEL" );
			IMPLEMENTATIONS.put( "OBRAPUBLICA", "com.syc.obrapublica.core.EstimacionObraFIEL" );
			IMPLEMENTATIONS.put( "APARTADO", "com.axtel.contratos.QuestionnaireBussinessLogic" );
			IMPLEMENTATIONS.put( "ENTERASATISFACCION", "com.axtel.contratos.ProcesoEnteraSatisfaccionBusinessLogic" );
		} catch ( Exception e ) {
			log.error( e, e );
		}

		try {
			InitialContext ic = new InitialContext();
			jniName = ( String ) ic.lookup( "java:comp/env/dataSourceRefName" );

			if ( jniName == null ) {
				jniName = "jdbc/gestion";
				log.info( "Environment Entry \"dataSourceRefName\" nula usando default \"" + jniName + "\"" );
			} else
				log.info( "dataSourceRefName=" + jniName );
			settings = new ConfiguraAplicativoBusinessLogic( jniName );
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
