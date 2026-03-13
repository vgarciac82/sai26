package com.syc.cfdi.utils;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.input.BOMInputStream;
import org.w3c.dom.Document;

import com.google.common.io.ByteStreams;
import com.syc.cfdi.core.Factura;
import com.syc.cfdi.core.Traslados;
import com.syc.cfdi.v3332.Comprobante.Comprobante;
import com.syc.gestion.servlet.GestionInterface;

import mx.grupocorasa.sat.cfdi.v3.CFDv33;
import mx.grupocorasa.sat.cfdi.v4.CFDv40;
import mx.grupocorasa.sat.exceptions.UnsupportedVersionException;


public class FacturaUtils {

	public static Factura cargaCFDI( com.syc.cfdi.v3332.Comprobante.Comprobante comprobante ) throws Exception {

		Factura factura = new Factura();
		factura.setUUID( comprobante.getUUID() );
		factura.setRfcEmisor( comprobante.getRFCEmisor() );
		factura.setNombreEmisor( comprobante.getNombreEmisor() );
		factura.setRfcReceptor( comprobante.getRFCReceptor() );
		factura.setNombreReceptor( comprobante.getNombreReceptor() );
		factura.setTotal( comprobante.getTotal() );
		factura.setSubTotal( comprobante.getSubTotal() );
		factura.setImpuestos( FacturaUtils.listaImpuestos( comprobante ) );
		factura.setRetenciones( FacturaUtils.listaRetenciones( comprobante ) );

		return factura;
	}

	public static Factura cargaCFDI( File f ) throws Exception {
		Comprobante comprobante = null;
		InputStream in = new FileInputStream( f );
		// LAOP - Detect and exclude a UTF-8 BOM
		InputStream inBOM = new BOMInputStream( in );
		Factura factura = null;
		try ( ByteArrayOutputStream baos = new ByteArrayOutputStream() ) {
			ByteStreams.copy( inBOM, baos );
			byte[] data = baos.toByteArray();
			switch ( getVersion( data ) ) {
				case "3.3":
					try ( ByteArrayInputStream bais = new ByteArrayInputStream( data ) ) {
						comprobante = new Comprobante( CFDv33.newComprobante( bais ) );
						factura = cargaCFDI( comprobante );
					}
				break;
				case "4.0":
					try ( ByteArrayInputStream bais = new ByteArrayInputStream( data ) ) {
						comprobante = new Comprobante( CFDv40.newComprobante( bais ) );
						factura = cargaCFDI( comprobante );
					}
				break;
				default:
					throw new UnsupportedVersionException( "La versión " + getVersion( data ) + " no es soportada en esta librería" );
			}
		}
		return factura;
	}

	public static synchronized String generaNombreArchivoTemporal( String directorioTemporal, String extension ) {
		String idRandom = String.valueOf( Math.round( ( 1 + Math.random() ) * 10000 ) );
		String idArchivoFinal = GestionInterface.PREFIX_TEMP.substring( 0, GestionInterface.PREFIX_TEMP.length() - idRandom.length() ) + idRandom;
		String nombreDestino = directorioTemporal + "CARGA_FACTURAS_ZIP_" + System.currentTimeMillis() + "_" + idArchivoFinal + "." + extension;
		return nombreDestino;
	}

	public static synchronized String generaNombreArchivoTemporal( String directorioTemporal, String nombreArchivo, String extension ) {
		nombreArchivo = obtenNombreArchivo( nombreArchivo, false );
		String idRandom = String.valueOf( Math.round( ( 1 + Math.random() ) * 10000 ) );
		String idArchivoFinal = GestionInterface.PREFIX_TEMP.substring( 0, GestionInterface.PREFIX_TEMP.length() - idRandom.length() ) + idRandom;
		String nombreDestino = directorioTemporal + nombreArchivo + "_" + System.currentTimeMillis() + "_" + idArchivoFinal + "." + extension;
		return nombreDestino;
	}

	public static synchronized String generaNombreZip( String directorioTemporal, String extension ) {
		String idRandom = String.valueOf( Math.round( ( 1 + Math.random() ) * 10000 ) );
		String idArchivoFinal = GestionInterface.PREFIX_TEMP.substring( 0, GestionInterface.PREFIX_TEMP.length() - idRandom.length() ) + idRandom;
		String nombreDestino = directorioTemporal + "CARGA_FACTURAS_ZIP_" + System.currentTimeMillis() + "_" + idArchivoFinal + "." + extension;
		return nombreDestino;
	}

	public static String getUUID( Comprobante comprobante ) {
		return comprobante.getUUID();
	}

	public static String getVersion( byte[] data ) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse( new ByteArrayInputStream( data ) );
		XPathFactory xfactory = XPathFactory.newInstance();
		XPath xpath = xfactory.newXPath();
		String v = xpath.evaluate( "/Comprobante/@version", doc );
		if ( v.equals( "" ) ) {
			return xpath.evaluate( "/Comprobante/@Version", doc );
		} else {
			return v;
		}
	}

	public static Traslados listaImpuestos( com.syc.cfdi.v3332.Comprobante.Comprobante comprobante ) {
		Traslados traslados = new Traslados();
		if ( comprobante.isCfd40() ) {
			traslados.setVer40( true );
			traslados.setVer33( false );
			if ( comprobante.getComprobante40().getImpuestos() != null )
				traslados.setTraslados40( comprobante.getComprobante40().getImpuestos().getTraslados() );

		} else {
			traslados.setVer40( false );
			traslados.setVer33( true ); 
			if ( comprobante.getComprobante33().getImpuestos() != null )
				traslados.setTraslados33( comprobante.getComprobante33().getImpuestos().getTraslados() );
		}
		return traslados;
	}

	public static Map<String, BigDecimal> listaRetenciones( Comprobante comprobante ) {
		return comprobante.listaRetenciones();

	}

	public static String obtenExtensionArchivoZip( String nombreArchivo ) {

		if ( nombreArchivo.indexOf( "." ) > 0 ) {
			return nombreArchivo.substring( nombreArchivo.lastIndexOf( "." ) + 1 );
		} else
			return "";
	}

	public static String obtenNombreArchivo( String nombreArchivo, boolean incluyeExtension ) {
		String nombre = "";

		if ( nombreArchivo.contains( String.valueOf( File.separatorChar ) ) ) {
			String[] componentes = nombreArchivo.split( "\\\\" );
			nombre = componentes[componentes.length - 1];
		} else
			nombre = nombreArchivo;

		if ( !incluyeExtension )
			nombre = nombre.lastIndexOf( "." ) > 0 ? nombre.substring( 0, nombre.lastIndexOf( "." ) ) : nombre;
		return nombre;
	}

	public static String obtenNombreArchivoZip( String nombreArchivo, boolean incluyeExtension ) {
		String nombre = "";

		if ( nombreArchivo.contains( "/" ) ) {
			String[] componentes = nombreArchivo.split( "/" );
			nombre = componentes[componentes.length - 1];
		} else
			nombre = nombreArchivo;

		if ( !incluyeExtension )
			nombre = nombre.lastIndexOf( "." ) > 0 ? nombre.substring( 0, nombre.lastIndexOf( "." ) ) : nombre;
		return nombre;

	}

	public static String readVersion( File fxml ) throws Exception {

		FileInputStream in = new FileInputStream( fxml );
		String version = "";

		try ( ByteArrayOutputStream baos = new ByteArrayOutputStream() ) {
			ByteStreams.copy( in, baos );
			byte[] data = baos.toByteArray();
			version = getVersion( data );
			in.close();
			in = null;
		}
		return version;
	}
}
