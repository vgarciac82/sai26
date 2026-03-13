package com.syc.utils.zip;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.apache.log4j.Logger;

import com.syc.cfdi.ComponentesFactura;
import com.syc.cfdi.MassPaymentInvoiceComponents;
import com.syc.cfdi.utils.FacturaUtils;


public class ZipManager {

	private static final int	BUFFER_SIZE	= 2048;
	private static final Logger	log			= Logger.getLogger( ZipManager.class );

	public static void extractFile( ZipInputStream zipIn, String filePath ) throws IOException {
		log.trace( "Extrayendo archivo a disco. Escribiendo a flujo de salida" );
		BufferedOutputStream bos = new BufferedOutputStream( new FileOutputStream( filePath ) );
		byte[] bytesIn = new byte [BUFFER_SIZE];
		int read = 0;
		while ( ( read = zipIn.read( bytesIn ) ) != -1 ) {
			bos.write( bytesIn, 0, read );
		}
		log.trace( "Archivo extraido exitosamente. Cerrando flujos" );
		bos.flush();
		bos.close();
		bos = null;
		log.trace( "flujos cerrados" );
	}

	public static int extraeArchivos( String rutaArchivo, String directorioTemporal, List<File> extractedFiles ) throws IOException {

		long start = System.currentTimeMillis();
		log.trace( "Iniciando extraccion de contenido del archivo [" + rutaArchivo + "]" );

		if ( extractedFiles != null )
			log.debug( "Los resultados se guardaran en lista." );

		int archivosExtraidos = 0;

		File destDir = new File( directorioTemporal );
		if ( !destDir.exists() ) {
			destDir.mkdir();
		}

		log.trace( "Abriendo archivo [" + rutaArchivo + "]  para su extraccion." );
		ZipInputStream zipIn = new ZipInputStream( new FileInputStream( rutaArchivo ) );
		ZipEntry entry = zipIn.getNextEntry();

		log.trace( "Iterando contenido del archivo." );
		while ( entry != null ) {

			String filePath = directorioTemporal + ( directorioTemporal.endsWith( String.valueOf( File.separatorChar ) ) ? "" : File.separatorChar ) + entry.getName();

			log.trace( "Procesando archivo [" + filePath + "] dentro del archivo ZIP" );

			if ( !entry.isDirectory() ) {
				log.trace( "Se trata de un archivo, se extraera" );
				extractFile( zipIn, filePath );
				archivosExtraidos++;
				if ( extractedFiles != null )
					extractedFiles.add( new File( filePath ) );
			} else {
				log.trace( "Se trata de un directorio, se creara" );
				File dir = new File( filePath );
				dir.mkdir();
			}

			zipIn.closeEntry();
			entry = zipIn.getNextEntry();

		}

		zipIn.close();
		zipIn = null;
		long stop = System.currentTimeMillis();
		log.trace( "Terminada extraccion de contenido del archivo [" + rutaArchivo + "] Se extrageron [" + archivosExtraidos + "] archivos en [" + ( ( stop - start ) / 1000 ) + "] s." );
		return archivosExtraidos;
	}

	public static Map<String, ComponentesFactura> extraeArchivosFactura( InputStream is, String directorioTemporal ) throws IOException {
		long start = System.currentTimeMillis();

		int archivosExtraidos = 0;

		if ( ! ( directorioTemporal.endsWith( "/" ) || directorioTemporal.endsWith( "\\" ) ) )
			directorioTemporal = directorioTemporal + File.separatorChar;
		log.trace( "Directorio temporal de extraccion[ " + directorioTemporal + "]" );
		File destDir = new File( directorioTemporal );
		if ( !destDir.exists() ) {
			destDir.mkdir();
		}
		Map<String, ComponentesFactura> facturas = new HashMap<String, ComponentesFactura>();
		ZipInputStream zipIn = new ZipInputStream( is );
		ZipEntry entry = zipIn.getNextEntry();

		log.trace( "Iterando contenido del archivo." );

		while ( entry != null ) {

			String nombreElemento = FacturaUtils.obtenNombreArchivoZip( entry.getName(), false );
			String extension = FacturaUtils.obtenExtensionArchivoZip( entry.getName() );

			String filePath = FacturaUtils.generaNombreArchivoTemporal( directorioTemporal, nombreElemento, extension );

			log.trace( "Procesando archivo [" + nombreElemento + "." + extension + "] dentro del archivo ZIP" );

			if ( !entry.isDirectory() ) {
				log.trace( "Se trata de un archivo, se extraera" );
				File f = new File( filePath );
				extractFile( zipIn, filePath );

				if ( facturas.get( nombreElemento ) == null )
					facturas.put( nombreElemento, new ComponentesFactura() );

				ComponentesFactura cf = facturas.get( nombreElemento );
				if ( "pdf".equalsIgnoreCase( extension ) ) {
					cf.setPdfPathFile( f.getAbsolutePath() );
				} else
					cf.setXmlPathFile( f.getAbsolutePath() );

				archivosExtraidos++;
			} else {
				log.trace( "Se trata de un directorio, se creara" );
				File dir = new File( filePath );
				dir.mkdir();
			}

			zipIn.closeEntry();
			entry = zipIn.getNextEntry();

		}

		zipIn.close();
		is.close();
		long stop = System.currentTimeMillis();
		log.trace( "Terminada extraccion de contenido del archivo. Se extrageron [" + archivosExtraidos + "] archivos en [" + ( ( stop - start ) / 1000 ) + "] s." );
		return facturas;
	}

	/**
	 * Extrae las facturas del archivo Zip para
	 * 
	 * @param rutaArchivo
	 * @param directorioTemporal
	 * @return
	 * @throws IOException
	 */
	public static Map<String, ComponentesFactura> extraeArchivosFactura( String rutaArchivo, String directorioTemporal ) throws IOException {
		log.trace( "Iniciando extraccion de contenido del archivo [" + rutaArchivo + "]" );
		log.trace( "Abriendo archivo [" + rutaArchivo + "]  para su extraccion." );
		return extraeArchivosFactura( new FileInputStream( rutaArchivo ), directorioTemporal );

	}

	
	public static List<File> extraeArchivosMemoria( String rutaArchivo, String directorioTemporal ) throws IOException {

		List<File> contenido = new ArrayList<File>();
		long start = System.currentTimeMillis();
		log.trace( "Iniciando extraccion de contenido del archivo [" + rutaArchivo + "]" );
		int archivosExtraidos = 0;

		File destDir = new File( directorioTemporal );
		if ( !destDir.exists() ) {
			destDir.mkdir();
		}

		log.trace( "Abriendo archivo [" + rutaArchivo + "]  para su extraccion." );
		ZipInputStream zipIn = new ZipInputStream( new FileInputStream( rutaArchivo ) );
		ZipEntry entry = zipIn.getNextEntry();

		log.trace( "Iterando contenido del archivo." );
		while ( entry != null ) {

			String filePath = directorioTemporal + ( directorioTemporal.endsWith( String.valueOf( File.separatorChar ) ) ? "" : File.separatorChar ) + entry.getName();

			log.trace( "Procesando archivo [" + filePath + "] dentro del archivo ZIP" );

			if ( !entry.isDirectory() ) {
				log.trace( "Se trata de un archivo, se extraera" );
				extractFile( zipIn, filePath );
				archivosExtraidos++;
				contenido.add( new File( filePath ) );
			} else {
				log.trace( "Se trata de un directorio, se creara" );
				File dir = new File( filePath );
				dir.mkdir();
			}

			zipIn.closeEntry();
			entry = zipIn.getNextEntry();
		}

		zipIn.close();
		zipIn = null;
		long stop = System.currentTimeMillis();
		log.trace( "Terminada extraccion de contenido del archivo [" + rutaArchivo + "] Se extrageron [" + archivosExtraidos + "] archivos en [" + ( ( stop - start ) / 1000 ) + "] s." );
		return contenido;
	}

	public static List<String> listContents( File archivoZip, boolean ignoraCarpetas ) throws ZipException, IOException {

		List<String> contents = new ArrayList<String>();
		ZipFile zFile = null;

		try {

			log.trace( "Iniciando lectura de contenido del archivo [" + archivoZip.getAbsolutePath() + "]" );
			long start = System.currentTimeMillis();

			zFile = ZipManager.openZipFile( archivoZip );
			Enumeration<? extends ZipEntry> entries = zFile.entries();

			while ( entries.hasMoreElements() ) {
				ZipEntry entry = entries.nextElement();
				if ( entry.isDirectory() && ignoraCarpetas )
					continue;
				else
					contents.add( entry.getName().toLowerCase() );
			}

			long finish = System.currentTimeMillis();
			log.trace( "Terminada lectura de contenido del archivo [" + archivoZip.getAbsolutePath() + "] en [" + ( ( finish - start ) / 1000 ) + " s.]" );
			return contents;
		} finally {
			if ( zFile != null )
				try {
					zFile.close();
				} catch ( Exception e ) {
					log.warn( "Error cerrando archivo ZIP: " + e, e );
				} finally {
					zFile = null;
				}
		}
	}

	public static List<String> listContents( String rutaArchivoZip, boolean ignoraCarpetas ) throws Exception {

		return listContents( new File( rutaArchivoZip ), ignoraCarpetas );
	}

	private static ZipFile openZipFile( File archivoZip ) throws ZipException, IOException {
		long start = System.currentTimeMillis();
		ZipFile zipFile = null;

		log.trace( "Iniciando apertura de archivo [" + archivoZip.getAbsolutePath() + "]" );

		log.trace( "Validando archivo" );
		if ( !archivoZip.exists() )
			throw new ZipException( "No existe el archivo [" + archivoZip + "]" );

		log.debug( "Abriendo archivo [" + archivoZip.getAbsolutePath() + "]" );
		zipFile = new ZipFile( archivoZip );

		long finish = System.currentTimeMillis();
		log.trace( "Terminando apertura de archivo en " + ( ( finish - start ) / 1000 ) + " s." );
		return zipFile;
	}

	/**
	 * Devuelve un archivo zip abierto. Valida que el archivo exista
	 * 
	 * @param zipFileMap
	 *            Ruta absoluta al archivo Zip
	 * @return ZipFile
	 * @throws ZipException
	 * @throws IOException
	 */
	public static ZipFile openZipFile( String zipFileMap ) throws ZipException, IOException {

		return openZipFile( new File( zipFileMap ) );
	}

}
