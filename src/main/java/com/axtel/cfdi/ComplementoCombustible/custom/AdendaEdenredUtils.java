package com.axtel.cfdi.ComplementoCombustible.custom;


import java.io.File;
import java.math.BigDecimal;
import java.util.Iterator;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.*;

import org.w3c.dom.*;


public class AdendaEdenredUtils {

	public static EdrConceptoDispersion buscarConceptoDispersion( String xmlPath, String uuid ) throws Exception {
		if ( xmlPath == null || uuid == null )
			return null;

		File xmlFile = new File( xmlPath );
		if ( !xmlFile.exists() )
			return null;

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware( true );
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse( xmlFile );

		XPathFactory xPathFactory = XPathFactory.newInstance();
		XPath xpath = xPathFactory.newXPath();
		xpath.setNamespaceContext( new NamespaceContext() {

			public String getNamespaceURI( String prefix ) {
				return "edr".equals( prefix ) ? "http://www.edenred.com.mx/cfdi/3/" : null;
			}

			public String getPrefix( String uri ) {
				return null;
			}

			public Iterator<String> getPrefixes( String uri ) {
				return null;
			}
		} );

		String expr = String.format( "//edr:conceptoDispersion[@IdDocumento=\"%s\"]", uuid );
		Node node = ( Node ) xpath.evaluate( expr, doc, XPathConstants.NODE );
		if ( node == null )
			return null;

		NamedNodeMap attrs = node.getAttributes();
		EdrConceptoDispersion concepto = new EdrConceptoDispersion();
		concepto.setIdDocumento( uuid );
		concepto.setImporteSaldoAnterior( toBigDecimal( attrs.getNamedItem( "ImporteSaldoAnterior" ) ) );
		concepto.setImportePagado( toBigDecimal( attrs.getNamedItem( "ImportePagado" ) ) );
		concepto.setImporteSaldoInsolutoPendientePago( toBigDecimal( attrs.getNamedItem( "ImporteSaldoInsolutoPendientePago" ) ) );

		return concepto;
	}

	private static BigDecimal toBigDecimal( Node n ) {
		try {
			return n != null ? new BigDecimal( n.getNodeValue() ) : BigDecimal.ZERO;
		} catch ( NumberFormatException e ) {
			return BigDecimal.ZERO;
		}
	}
}
