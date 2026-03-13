package com.syc.cfdi.servlet;


import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
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
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.axtel.egresos.viaticos.ViaticosBusinessLogic;
import com.jenkov.prizetags.tree.itf.ITree;
import com.syc.cfdi.FacturaBusinessLogic;
import com.syc.cfdi.utils.FacturaUtils;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.obrapublica.ConfiguraAplicativoBusinessLogic;
import com.syc.obrapublica.EjercicioFiscalBusinessLogic;


public class UploadFacturasZipServletRG extends HttpServlet implements GestionInterface {

	private static final long	serialVersionUID				= -795967476374791070L;
	private static String		jniName							= "jdbc/gestion";
	private static final Logger	log								= Logger.getLogger( UploadFacturasZipServletRG.class );
	private static String		TEMP_DIR						= "";
	private static boolean		validaContraSAT					= false;
	private static boolean		notificaFacturasInvalidasSAT	= false;
	private static boolean		notificaFacturasEFA				= false;
	private static boolean		permiteFacturasVersionAnterior	= false;

	private static final String	COMPROBACION					= "COMPROBACION";
	private static final String	ALIMENTACION					= "ALIMENTACION";
	private static final String	FACTURA_PROVEEDOR				= "FACTURA_PROVEEDOR";
	private static final String	OFICIO_PROVEEDOR				= "OFICIO_PROVEEDOR";

	@Override
	protected void doPost( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException {

		HttpSession session = req.getSession( false );
		String msgRetorno = "";
		Usuario u = null;
		Caso c = null;
		String tipoPago = "";
		boolean cargaDesdeViaticos = false;

		if ( session == null ) {
			msgRetorno = "Su sesion a terminado. Por favor ingrese nuevamente al sistema.";
		} else {

			u = ( Usuario ) session.getAttribute( ATT_USER );
			c = ( Caso ) session.getAttribute( ATT_CASE );

			if ( c == null || u == null ) {
				msgRetorno = "Su sesion a terminado. Por favor ingrese nuevamente al sistema.";
			}

			if ( c.getTipoCaso().getIdTC() == 73 ) {
				try {
					ViaticosBusinessLogic vbl = new ViaticosBusinessLogic( jniName );
					c = vbl.getRelacionGastosAsociada( Util.folio( c ) );
					cargaDesdeViaticos = true;	
				} catch ( Exception e ) {
					log.error( e, e );
					msgRetorno = "Ocurrio el siguiente error al cargar el archivo: <br>" + e.getMessage();
				}
			}
		}

		if ( "".equals( msgRetorno ) ) {
			ConfiguraAplicativoBusinessLogic cabl = new ConfiguraAplicativoBusinessLogic( jniName );

			validaContraSAT = "S".equalsIgnoreCase( cabl.getSystemSetting( "ACTIVA_VALIDACION_SAT" ) );
			notificaFacturasEFA = "S".equalsIgnoreCase( cabl.getSystemSetting( "NOTIFICA_ERROR_VALIDACION_EFA" ) );
			notificaFacturasInvalidasSAT = "S".equalsIgnoreCase( cabl.getSystemSetting( "NOTIFICA_ERROR_VALIDACION_SAT" ) );
			permiteFacturasVersionAnterior = "S".equalsIgnoreCase( cabl.getSystemSetting( "PERMITE_VERSION_MENOR" ) );

			CasoBusinessLogic cbl = new CasoBusinessLogic( jniName );

			List<?> fileItems = null;
			Iterator<?> iter = null;
			DataInputStream archivoCargaStream = null;
			String nombreDestino = "";

			try {

				if ( c.getIdGabinete() == -1 ) {
					EjercicioFiscalBusinessLogic efbl = new EjercicioFiscalBusinessLogic( jniName );
					c.getCasoDato( "FOLIO" ).setValor( c.getFolio() );
					c.getCasoDato( "FECHA_DOCUMENTO" ).setValor( Util.getToday() );
					c.getCasoDato( "EJERCICIO_FISCAL" ).setValor( efbl.getEjercicioFiscalActivo().getaEjercicioFiscal() );
					c.getCasoDato( "OPERADOR" ).setValor( c.getCasoOperacion( 0 ).getResponsable() );
					c.setIdGabinete( cbl.creaExpediente( u.getLogin(), c ) );
				}

				fileItems = Util.parseRequest( req, UploadFacturasZipServletRG.TEMP_DIR, -1 );
				iter = fileItems.iterator();
				String nombreArchivo = "";
				Map<String, String> datosCarga = new HashMap<String, String>();

				while ( iter.hasNext() ) {

					FileItem item = ( FileItem ) iter.next();

					if ( item.isFormField() ) {
						datosCarga.put( item.getFieldName(), item.getString() );
						item.delete();
						continue;
					}

					archivoCargaStream = new DataInputStream( item.getInputStream() );
					nombreArchivo = item.getName();

					if ( StringUtils.isBlank( nombreArchivo ) ) {
						item.delete();
						continue;
					}

					String extension = Util.getFileExtencion( nombreArchivo );

					nombreDestino = FacturaUtils.generaNombreZip( UploadFacturasZipServletRG.TEMP_DIR, extension );

					log.info( "Copiando archivo :" + nombreArchivo );
					Util.copiaArchivo( archivoCargaStream, nombreDestino );
					datosCarga.put( item.getFieldName(), nombreDestino );

					item.delete();
				}

				String tipoCarga = datosCarga.get( "TipoCarga" );
				String tipoComprobacion = datosCarga.get( "tipoComprobacion" );
				boolean viaticos = "TRUE".equalsIgnoreCase( datosCarga.get( "viaticos" ) ); 

				FacturaBusinessLogic fbl = new FacturaBusinessLogic( jniName, validaContraSAT, u );
				fbl.setNotificaErroresSAT( notificaFacturasInvalidasSAT );
				fbl.setNotificaErroresEFA( notificaFacturasEFA );
				fbl.setPermiteVersionAnterior( permiteFacturasVersionAnterior );
				fbl.setViaticos(viaticos);
				
				if ( UploadFacturasZipServletRG.OFICIO_PROVEEDOR.equalsIgnoreCase( tipoCarga ) ) {
					fbl.insertaOficio( c, u, datosCarga );
				} else if ( UploadFacturasZipServletRG.FACTURA_PROVEEDOR.equalsIgnoreCase( tipoCarga ) ) {
					fbl.cargaFacturasPagoProveedor( c, u, datosCarga );
				} else if ( UploadFacturasZipServletRG.COMPROBACION.equalsIgnoreCase( tipoCarga ) ) {
					fbl.cargaInformacionPagoRG( c, u, datosCarga );
				} else if ( UploadFacturasZipServletRG.ALIMENTACION.equalsIgnoreCase( tipoCarga ) ) {
					fbl.cargaInformacionAlimentacion( c, u, datosCarga );
				} else if ( "extranjero".equals( tipoComprobacion ) ) {
					String noOficio = datosCarga.get( "noOficio" );
					String monto = datosCarga.get( "monto" );
					insertaDocumentoExtranjero( c, u, nombreDestino, noOficio, Double.parseDouble( monto ) );

				}

				ITree tree = cbl.getArbolCaso( c );

				
				if ( !cargaDesdeViaticos ) {
					session.setAttribute( ATT_CASE, c );
					session.setAttribute( "tree.model", tree );
				}	
					
				msgRetorno = "Archivo cargado exitosamente";

			} catch ( Exception e ) {
				log.error( e, e );
				msgRetorno = "Ocurrio el siguiente error al cargar el archivo: <br>" + e.getMessage();
			} finally {
				if ( archivoCargaStream != null )
					try {
						archivoCargaStream.close();
					} catch ( Exception e ) {
						log.error( "Error cerrando flujo DataInputStream" + e );
					}
				archivoCargaStream = null;

				if ( !"".equals( nombreDestino ) ) {
					File toDelete = new File( nombreDestino );
					if ( !toDelete.delete() )
						toDelete.deleteOnExit();
				}
			}
		} else {
			resp.sendRedirect( "Generador/UploadFacturas.jsp?tipo_pago=" + tipoPago + "&msgError=" + msgRetorno );
		}

		session.setAttribute( "RESULT", msgRetorno );
		resp.sendRedirect( "Generador/UploadFacturas.jsp?tipo_pago=" + tipoPago );

	}

	private void insertaDocumentoExtranjero( Caso c, Usuario u, String rutaArchivo, String cFactura, double montoDocto ) throws Exception {
		FacturaBusinessLogic fbl = new FacturaBusinessLogic( jniName, validaContraSAT, u );
		fbl.setNotificaErroresSAT( notificaFacturasInvalidasSAT );
		fbl.setNotificaErroresEFA( notificaFacturasEFA );
		fbl.setPermiteVersionAnterior( permiteFacturasVersionAnterior );
		fbl.insertaDoctosExtranjero( c, u, rutaArchivo, cFactura, montoDocto );
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
				TEMP_DIR = "../upload/Facturas/";
				log.info( "Environment Entry \"TEMP_DIR\" nula usando default \"" + TEMP_DIR + "\"" );
			} else
				log.info( "dataSourceRefName=" + TEMP_DIR );
		} catch ( NamingException exc ) {
			TEMP_DIR = "../upload/Facturas/";
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
