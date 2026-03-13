package com.axtel.cfdi.descargaMasiva;


import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;

import org.w3c.dom.Document;

import com.axtel.cfdi.descargaMasiva.request.RequestBase;


public class VerifyRequest extends RequestBase {

	private Request requestOrigen = null;

	private Request getRequestOrigen() {
		return requestOrigen;
	}

	private void setRequestOrigen( Request requestOrigen ) {
		this.requestOrigen = requestOrigen;
	}

	/**
	 * Constructor of VerifyRequest class
	 *
	 * @param url
	 * @param SOAPAction
	 */
	public VerifyRequest( String url, String SOAPAction ) {
		super( url, SOAPAction );
	}

	@Override
	protected String getResult( String xmlResponse ) {
		Document doc = convertStringToXMLDocument( xmlResponse );
		if ( doc != null ) {

			String codigoEstadoSolicitud = doc.getElementsByTagName( "VerificaSolicitudDescargaResult" ).item( 0 ).getAttributes().getNamedItem( "CodigoEstadoSolicitud" ).getTextContent();
			String estadoSolicitud = "";
			if ( doc.getElementsByTagName( "VerificaSolicitudDescargaResult" ).item( 0 ).getAttributes().getNamedItem( "EstadoSolicitud" ) != null )
				estadoSolicitud = doc.getElementsByTagName( "VerificaSolicitudDescargaResult" ).item( 0 ).getAttributes().getNamedItem( "EstadoSolicitud" ).getTextContent();
			String numeroCFDIs = "0";
			if ( doc.getElementsByTagName( "VerificaSolicitudDescargaResult" ).item( 0 ).getAttributes().getNamedItem( "NumeroCFDIs" ) != null )
				numeroCFDIs = doc.getElementsByTagName( "VerificaSolicitudDescargaResult" ).item( 0 ).getAttributes().getNamedItem( "NumeroCFDIs" ).getTextContent();
			
			String idPaquetes = "";
			if( doc.getElementsByTagName( "IdsPaquetes" )!= null && doc.getElementsByTagName( "IdsPaquetes" ).getLength() > 0 )
				idPaquetes = doc.getElementsByTagName( "IdsPaquetes" ).item( 0 ).getTextContent();
			
			
			getRequestOrigen().setIdPaquetes( idPaquetes );
			getRequestOrigen().setStatusRequest( estadoSolicitud );
			getRequestOrigen().setCodigoStatus(codigoEstadoSolicitud);
			getRequestOrigen().setTotalCFDI(numeroCFDIs);

		}

		return getRequestOrigen().getIdPaquetes();
	}

	/**
	 * Generate XML to send through SAT's web service
	 *
	 * @param certificate
	 * @param privateKey
	 * @param idRequest
	 * @param rfcSolicitante
	 * @throws NoSuchAlgorithmException
	 * @throws SignatureException
	 * @throws InvalidKeyException
	 * @throws CertificateEncodingException
	 */
	private void generate() throws NoSuchAlgorithmException, SignatureException, InvalidKeyException, CertificateEncodingException {

		StringBuilder canonicalTimestamp = new StringBuilder();
		canonicalTimestamp.append( "<des:VerificaSolicitudDescarga xmlns:des=\"http://DescargaMasivaTerceros.sat.gob.mx\">" );
		canonicalTimestamp.append( "<des:solicitud IdSolicitud=\"" );
		canonicalTimestamp.append( getRequestOrigen().getIdRequest() );
		canonicalTimestamp.append( "\" RfcSolicitante=\"" );
		canonicalTimestamp.append( getRequestOrigen().getRfcConsulta() );
		canonicalTimestamp.append( "\">" );
		canonicalTimestamp.append( "</des:solicitud>" );
		canonicalTimestamp.append( "</des:VerificaSolicitudDescarga>" );

		StringBuilder digest = new StringBuilder( createDigest( canonicalTimestamp.toString() ) );

		StringBuilder canonicalSignedInfo = new StringBuilder();
		canonicalSignedInfo.append( "<SignedInfo xmlns=\"http://www.w3.org/2000/09/xmldsig#\">" );
		canonicalSignedInfo.append( "<CanonicalizationMethod Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"></CanonicalizationMethod>" );
		canonicalSignedInfo.append( "<SignatureMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#rsa-sha1\"></SignatureMethod>" );
		canonicalSignedInfo.append( "<Reference URI=\"#_0\">" );
		canonicalSignedInfo.append( "<Transforms>" );
		canonicalSignedInfo.append( "<Transform Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"></Transform>" );
		canonicalSignedInfo.append( "</Transforms>" );
		canonicalSignedInfo.append( "<DigestMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#sha1\"></DigestMethod>" );
		canonicalSignedInfo.append( "<DigestValue>" );
		canonicalSignedInfo.append( digest );
		canonicalSignedInfo.append( "</DigestValue>" );
		canonicalSignedInfo.append( "</Reference>" );
		canonicalSignedInfo.append( "</SignedInfo>" );

		String signature = sign( canonicalSignedInfo.toString(), getRequestOrigen().getFirma().getKey() );

		StringBuilder xmlSB = new StringBuilder();
		xmlSB.append( "<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:u=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\" xmlns:des=\"http://DescargaMasivaTerceros.sat.gob.mx\" xmlns:xd=\"http://www.w3.org/2000/09/xmldsig#\">" );
		xmlSB.append( "<s:Header/>" );
		xmlSB.append( "<s:Body>" );
		xmlSB.append( "<des:VerificaSolicitudDescarga>" );
		xmlSB.append( "<des:solicitud IdSolicitud=\"" );
		xmlSB.append( getRequestOrigen().getIdRequest() );
		xmlSB.append( "\" RfcSolicitante=\"" );
		xmlSB.append( getRequestOrigen().getRfcConsulta() );
		xmlSB.append( "\">" );
		xmlSB.append( "<Signature xmlns=\"http://www.w3.org/2000/09/xmldsig#\">" );
		xmlSB.append( "<SignedInfo>" );
		xmlSB.append( "<CanonicalizationMethod Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"/>" );
		xmlSB.append( "<SignatureMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#rsa-sha1\"/>" );
		xmlSB.append( "<Reference URI=\"#_0\">" );
		xmlSB.append( "<Transforms>" );
		xmlSB.append( "<Transform Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"/></Transforms>" );
		xmlSB.append( "<DigestMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#sha1\"/>" );
		xmlSB.append( "<DigestValue>" );
		xmlSB.append( digest );
		xmlSB.append( "</DigestValue>" );
		xmlSB.append( "</Reference>" );
		xmlSB.append( "</SignedInfo>" );
		xmlSB.append( "<SignatureValue>" );
		xmlSB.append( signature );
		xmlSB.append( "</SignatureValue>" );
		xmlSB.append( "<KeyInfo>" );
		xmlSB.append( "<X509Data>" );
		xmlSB.append( "<X509IssuerSerial>" );
		xmlSB.append( "<X509IssuerName>" );
		xmlSB.append( getRequestOrigen().getFirma().getCert().getIssuerX500Principal() );
		xmlSB.append( "</X509IssuerName>" );
		xmlSB.append( "<X509SerialNumber>" );
		xmlSB.append( getRequestOrigen().getFirma().getCert().getSerialNumber() );
		xmlSB.append( "</X509SerialNumber>" );
		xmlSB.append( "</X509IssuerSerial>" );
		xmlSB.append( "<X509Certificate>" );
		xmlSB.append( getRequestOrigen().getFirma().getKeyString() );
		xmlSB.append( "</X509Certificate>" );
		xmlSB.append( "</X509Data>" );
		xmlSB.append( "</KeyInfo>" );
		xmlSB.append( "</Signature>" );
		xmlSB.append( "</des:solicitud>" );
		xmlSB.append( "</des:VerificaSolicitudDescarga>" );
		xmlSB.append( "</s:Body>" );
		xmlSB.append( "</s:Envelope>" );
		this.setXml( xmlSB.toString() );
	}

	public void generate( Request request ) throws InvalidKeyException, CertificateEncodingException, NoSuchAlgorithmException, SignatureException {
		setRequestOrigen( request );
		generate();
	}
}
