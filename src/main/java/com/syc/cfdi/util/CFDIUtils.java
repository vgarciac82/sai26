package com.syc.cfdi.util;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang.StringUtils;

import com.syc.fortimax.core.Documento;
import com.syc.fortimax.core.ExportLogDetallado;
import com.syc.info.cfdi.export.config.DBConfigurator;

import mx.grupocorasa.sat.cfd._40.Comprobante;
import mx.grupocorasa.sat.common.TimbreFiscalDigital11.TimbreFiscalDigital;


public class CFDIUtils {

	public static final String[]	ASSOCIATION_TYPES		= { ", S.A. DE C.V.", ", S. DE R. L. DE C.V.", "S.A DE C.V", "S.A. DE C.V.", "SA DE CV", "S. A. P. DE C.V.", };
	private static final String		dbPropertiesFilePath	= "/procesos/config/dbConfig.properties";

	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern( "yyyy-MM-dd'T'HH:mm:ss" );

	public static int addToZip( ZipOutputStream zos, Documento doc ) throws Exception {
		return addToZip( zos, doc, null );
	}

	public static int addToZip( ZipOutputStream zos, Documento doc, String nombreDocumento ) throws Exception {

		byte[] buffer = new byte [1024 * 1024];
		int agregados = 0;

		if ( doc.getPaginasDocumento() != null && doc.getPaginasDocumento().length > 0 ) {

			String nombreEntrada;
			String extencion = CFDIUtils.getFileExtencion( doc.getNombreDocumento() );

			if ( nombreDocumento != null )
				nombreEntrada = nombreDocumento + ( StringUtils.isBlank( extencion ) ? "" : "." + extencion );
			else
				nombreEntrada = doc.getNombreDocumento();

			String file = doc.getPaginaDocumento( 0 ).getFullPathFileName();

			File f = new File( file );

			if ( !f.exists() ) {
				throw new Exception( "El documento " + doc.toFortimax() + " no puede leer la pagina: " + f.getAbsolutePath() );
			} else {

				if ( nombreEntrada.indexOf( '.' ) < 0 )
					nombreEntrada = nombreEntrada + "." + doc.getExtension();
			}

			ZipEntry ze = new ZipEntry( nombreEntrada );
			zos.putNextEntry( ze );

			FileInputStream in = new FileInputStream( file );

			int len;
			while ( ( len = in.read( buffer ) ) > 0 ) {
				zos.write( buffer, 0, len );
			}

			in.close();
		} else
			throw new Exception( " El documento " + ( doc.toFortimax() ) + " Esta vacio " );

		return agregados;

	}

	public static int addToZip( ZipOutputStream zos, Documento[] agregar ) throws Exception {

		byte[] buffer = new byte [1024 * 512];
		int agregados = 0;

		for ( Documento doc : agregar ) {

			if ( doc.getPaginasDocumento() != null && doc.getPaginasDocumento().length > 0 ) {

				String nombreEntrada = doc.getNombreDocumento();

				String file = doc.getPaginaDocumento( 0 ).getFullPathFileName();

				File f = new File( file );

				if ( !f.exists() ) {
					file = "/archivoImagen.jpg";
					nombreEntrada = doc.getNombreDocumento() + ".jpg";
				} else {

					if ( nombreEntrada.indexOf( '.' ) < 0 )
						nombreEntrada = doc.getNombreDocumento() + "." + doc.getExtension();
				}
				ZipEntry ze = new ZipEntry( nombreEntrada );
				zos.putNextEntry( ze );

				FileInputStream in = new FileInputStream( file );

				int len;
				while ( ( len = in.read( buffer ) ) > 0 ) {
					zos.write( buffer, 0, len );
				}

				in.close();
				agregados++;
			} else
				continue;
		}

		return agregados;

	}

	public static List<ExportLogDetallado> addToZip( ZipOutputStream zos, Documento[] agregar, String cxp ) throws Exception {
		byte[] buffer = new byte [524288];
		ArrayList<ExportLogDetallado> result = new ArrayList<ExportLogDetallado>();
		Documento[] arrdocumento = agregar;
		int n = arrdocumento.length;
		int n2 = 0;
		while ( n2 < n ) {
			Documento doc = arrdocumento[n2];
			ExportLogDetallado logDetallado = new ExportLogDetallado();
			logDetallado.setCuentaPorPagar( cxp );
			try {
				String nombreEntrada = doc.getNombreDocumento();
				logDetallado.setPathDocumento( nombreEntrada );
				logDetallado.setCarpeta( CFDIUtils.getDocPath( nombreEntrada ) );
				logDetallado.setDocumento( CFDIUtils.getFileName( nombreEntrada ) );
				if ( doc.getPaginasDocumento() != null && doc.getPaginasDocumento().length > 0 ) {
					int len;
					logDetallado.setCumple( true );
					String file = doc.getPaginaDocumento( 0 ).getFullPathFileName();
					File f = new File( file );
					if ( !f.exists() ) {
						file = "/archivoImagen.jpg";
						nombreEntrada = String.valueOf( doc.getNombreDocumento() ) + ".jpg";
						logDetallado.setLog( "No se encontro el archivo en disco." );
						logDetallado.setCumple( false );
					} else {
						if ( nombreEntrada.indexOf( 46 ) < 0 ) {
							nombreEntrada = String.valueOf( doc.getNombreDocumento() ) + "." + doc.getExtension();
						}
						logDetallado.setDocumento( CFDIUtils.getFileName( nombreEntrada ) );
						if ( StringUtils.isEmpty( ( String ) CFDIUtils.getFileExtencion( nombreEntrada ) ) ) {
							logDetallado.setLog( "No se encontro extencion para el documento." );
							logDetallado.setCumple( false );
						}
					}
					ZipEntry ze = new ZipEntry( nombreEntrada );
					zos.putNextEntry( ze );
					FileInputStream in = new FileInputStream( file );
					while ( ( len = in.read( buffer ) ) > 0 ) {
						zos.write( buffer, 0, len );
					}
					in.close();
					logDetallado.setLog( String.valueOf( logDetallado.getLog() ) + ";" + "Archivo Procesado" );
					logDetallado.setCumple( logDetallado.isCumple() );
				} else {
					logDetallado.setLog( "El documento esta vacio." );
					logDetallado.setCumple( false );
				}
			} catch ( Exception e ) {
				logDetallado.setLog( "ERROR procesando documento:" + e );
				logDetallado.setCumple( false );
			}
			result.add( logDetallado );
			++n2;
		}
		return result;
	}

	public static BigDecimal genBigDecimalData( BigDecimal value ) throws Exception {

		DecimalFormat df2 = new DecimalFormat( ".00" );

		BigDecimal retVal = new BigDecimal( df2.format( value.doubleValue() ) );
		retVal.setScale( 2, BigDecimal.ROUND_DOWN );

		return retVal;

	}

	public static String generaCadenaComplemento( TimbreFiscalDigital tfd11 ) {

		StringBuilder sb = new StringBuilder( "||" );

		sb.append( tfd11.getVersion() ).append( "|" );
		sb.append( tfd11.getUUID() ).append( "|" );
		sb.append( tfd11.getFechaTimbrado().format( formatter ) ).append( "|" );
		sb.append( tfd11.getRfcProvCertif() ).append( "|" );
		sb.append( tfd11.getSelloCFD() ).append( "|" );
		sb.append( tfd11.getNoCertificadoSAT() );
		sb.append( "||" );

		return sb.toString();
	}

	public static Connection getConnection( DBConfigurator dbConfigurator ) throws Exception {
		return getConnection( dbConfigurator, false );
	}

	public static Connection getConnection( DBConfigurator dbConfigurator, boolean autoCommit ) throws Exception {

		Class.forName( dbConfigurator.getDriverClassName() );
		Connection conn = DriverManager.getConnection( dbConfigurator.getUrl(), dbConfigurator.getUserName(), dbConfigurator.getPassword() );
		conn.setAutoCommit( autoCommit );

		return conn;

	}

	public static String getDocPath( String nameFile ) {
		String path = "";
		if ( ! ( StringUtils.isEmpty( ( String ) nameFile ) || nameFile.indexOf( "\\" ) <= 0 && nameFile.indexOf( "/" ) <= 0 ) ) {
			String separador = nameFile.indexOf( "\\" ) > 0 ? "\\\\" : "/";
			String[] componentes = nameFile.split( separador );
			int i = 0;
			while ( i < componentes.length - 1 ) {
				path = String.valueOf( path ) + "/" + componentes[i];
				++i;
			}
		}
		return path;
	}

	/**
	 * Regresa la extension, de existir, de un archivo.
	 * 
	 * @param fileName
	 *            Nombre del archivo.
	 * @return Extension del archivo.
	 */
	public static String getFileExtencion( String fileName ) {
		String ext = "";
		int indexPunto = fileName.lastIndexOf( "." );
		if ( indexPunto > 0 )
			ext = fileName.substring( indexPunto + 1 );
		return ext;
	}

	public static String getFileName( String nameFile ) {
		if ( nameFile.indexOf( "\\" ) > 0 || nameFile.indexOf( "/" ) > 0 ) {
			String separador = nameFile.indexOf( "\\" ) > 0 ? "\\\\" : "/";
			String[] componentes = nameFile.split( separador );
			String file = componentes[componentes.length - 1];
			return file;
		} else
			return nameFile;
	}

	/**
	 * Regresa el nombre del archivo sin extencion.
	 * 
	 * @param fileName
	 *            Nombre del archivo.
	 * @return Extension del archivo.
	 */
	public static String getFileWithoutExtencion( String fileName ) {
		String sinExt = "";
		int indexPunto = fileName.lastIndexOf( "." );
		if ( indexPunto > 0 )
			sinExt = fileName.substring( 0, indexPunto );
		return sinExt;
	}

	public static Connection getStandAloneConnection() throws Exception {
		DBConfigurator dbConfigurator = DBConfigurator.instance( dbPropertiesFilePath );
		return getConnection( dbConfigurator, false );
	}

	public static TimbreFiscalDigital getTFD( Comprobante comp ) {
		List<Object> complementos = comp.getComplemento().getAny();
		TimbreFiscalDigital tfd = null;

		if ( complementos != null )
			for ( Iterator<?> k = complementos.iterator(); k.hasNext() && tfd == null; ) {
				Object o = k.next();
				if ( o instanceof TimbreFiscalDigital ) {
					tfd = ( ( mx.grupocorasa.sat.common.TimbreFiscalDigital11.TimbreFiscalDigital ) o );
					break;
				}
			}

		return tfd;
	}

	public static String getTodayDir() {
		String dateFormat = "yyyyMMddHH";
		SimpleDateFormat sdf = new SimpleDateFormat( dateFormat );
		return sdf.format( new Date() );
	}

	public static String getTodayFile() {
		String dateFormat = "yyyy.MM.dd.HH.mm";
		SimpleDateFormat sdf = new SimpleDateFormat( dateFormat );
		return sdf.format( new Date() );
	}

	public static String getUUID( Comprobante comp ) {
		TimbreFiscalDigital tfd = getTFD( comp );
		if ( tfd != null )
			return tfd.getUUID();
		else
			return "";
	}

	public static Properties loadFileProperties( String path ) throws Exception {
		Properties prop = new Properties();
		InputStream input = null;

		try {

			input = new FileInputStream( path );
			prop.load( input );
			return prop;
		} finally {
			if ( input != null ) {
				try {
					input.close();
				} catch ( IOException e ) {
					e.printStackTrace();
				}
			}
		}
	}

	public static String makeJSName( String str ) {

		StringBuffer jsName = new StringBuffer();
		boolean makeCapital = false;

		for ( int i = 0; i < str.length(); i++ ) {

			if ( "_".equals( str.substring( i, i + 1 ) ) ) {
				makeCapital = true;
				continue;
			}

			jsName.append( makeCapital ? str.substring( i, i + 1 ).toUpperCase() : str.substring( i, i + 1 ).toLowerCase() );
			makeCapital = false;
		}

		return jsName.toString();
	}

	public static String plainName( String nombre ) {
		for ( String associationType : ASSOCIATION_TYPES )
			nombre = nombre.toUpperCase().replace( associationType, "" );
		return nombre.trim().replaceAll( "Á", "A" ).replaceAll( "É", "E" ).replaceAll( "Í", "I" ).replaceAll( "Ó", "O" ).replaceAll( "Ú", "U" );
	}

	public static String toDate( long millis ) {
		SimpleDateFormat sdf = new SimpleDateFormat( "dd/MM/yyyy HH:mm:ss" );
		return sdf.format( new Date( millis ) );
	}

}
