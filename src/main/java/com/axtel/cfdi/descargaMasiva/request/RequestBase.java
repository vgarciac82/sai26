package com.axtel.cfdi.descargaMasiva.request;


import java.io.IOException;
import java.io.StringReader;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Base64;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;


public abstract class RequestBase {

	private String				xml;
	private final String		url;
	private final String		SOAPAction;
	private static final Logger	log	= Logger.getLogger( RequestBase.class );

	/**
	 * Constructor of RequestBase class
	 *
	 * @param url
	 * @param SOAPAction
	 */
	protected RequestBase( String url, String SOAPAction ) {
		log.info( "Creado Request a: \nURL:" + url + "\nAction:" + SOAPAction );
		this.xml = null;
		this.url = url;
		this.SOAPAction = SOAPAction;
	}

	protected void setXml( String xml ) {
		this.xml = xml;
	}

	/**
	 * Get result of a previously obtained XML
	 *
	 * @param xmlResponse
	 * @return
	 */
	protected abstract String getResult( String xmlResponse ) throws Exception;

	/**
	 * Create digest SHA1 from a String and returning a Base64 String
	 *
	 * @param sourceData
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	protected String createDigest( String sourceData ) throws NoSuchAlgorithmException {
		log.debug( "Creando Digest para la entrada " + sourceData );
		MessageDigest digest = MessageDigest.getInstance( "SHA-1" );
		digest.reset();
		digest.update( sourceData.getBytes() );

		return Base64.getEncoder().encodeToString( digest.digest() );
	}

	/**
	 * Sign SHA1 with private key and a String and returning a Base64 String
	 *
	 * @param sourceData
	 * @param privateKey
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 * @throws SignatureException
	 */
	protected String sign( String sourceData, PrivateKey privateKey ) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
		Signature sig = Signature.getInstance( "SHA1WithRSA" );
		sig.initSign( privateKey );
		sig.update( sourceData.getBytes() );

		return Base64.getEncoder().encodeToString( sig.sign() );
	}

	/**
	 * Create HttpURLConnection to send previously created XML
	 *
	 * @return
	 * @throws IOException
	 */
	public String send( String authorization ) throws Exception {
		String xmlResult = SOAPCall.callSoapWebService(this.url,this.SOAPAction,this.xml, authorization);
		return getResult( xmlResult );
	}

	/**
	 * Convert a String to XMl (Document Object)
	 *
	 * @param xmlString
	 * @return
	 */
	protected static Document convertStringToXMLDocument( String xmlString ) {
		// Parser that produces DOM object trees from XML content
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		// API to obtain DOM Document instance
		DocumentBuilder builder;
		try {
			// Create DocumentBuilder with default configuration
			builder = factory.newDocumentBuilder();

			// Parse the content to Document object
			return builder.parse( new InputSource( new StringReader( xmlString ) ) );
		} catch ( Exception e ) {
			e.printStackTrace();
		}

		return null;
	}
}
