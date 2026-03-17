package com.axtel.cfdi.descargaMasiva;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.sql.Connection;
import java.util.Base64;
import org.w3c.dom.Document;
import com.axtel.cfdi.descargaMasiva.request.RequestBase;
import com.syc.gestion.util.Util;
import java.nio.file.Paths;

public class Download extends RequestBase {

    private Connection conn = null;

    private Request requestOrigen = null;

    /**
     * Constructor of Download class
     *
     * @param url
     * @param SOAPAction
     */
    public Download(String url, String SOAPAction) {
        super(url, SOAPAction);
    }

    /**
     * Generate XML to send through SAT's web service
     *
     * @param certificate
     * @param privateKey
     * @param rfcSolicitante
     * @param idPackage
     * @throws NoSuchAlgorithmException
     * @throws SignatureException
     * @throws InvalidKeyException
     * @throws CertificateEncodingException
     */
    public void generate(Request request) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException, CertificateEncodingException {
        StringBuilder canonicalTimestamp = new StringBuilder();
        canonicalTimestamp.append("<des:PeticionDescargaMasivaTercerosEntrada xmlns:des=\"http://DescargaMasivaTerceros.sat.gob.mx\">");
        canonicalTimestamp.append("<des:peticionDescarga IdPaquete=\"");
        canonicalTimestamp.append(request.getIdPaquetes());
        canonicalTimestamp.append("\" RfcSolicitante=\"");
        canonicalTimestamp.append(request.getRfcConsulta());
        canonicalTimestamp.append("\"></des:peticionDescarga>");
        canonicalTimestamp.append("</des:PeticionDescargaMasivaTercerosEntrada>");
        StringBuilder digest = new StringBuilder(createDigest(canonicalTimestamp.toString()));
        StringBuilder canonicalSignedInfo = new StringBuilder();
        canonicalSignedInfo.append("<SignedInfo xmlns=\"http://www.w3.org/2000/09/xmldsig#\">");
        canonicalSignedInfo.append("<CanonicalizationMethod Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"></CanonicalizationMethod>");
        canonicalSignedInfo.append("<SignatureMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#rsa-sha1\"></SignatureMethod>");
        canonicalSignedInfo.append("<Reference URI=\"#_0\">");
        canonicalSignedInfo.append("<Transforms>");
        canonicalSignedInfo.append("<Transform Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"></Transform>");
        canonicalSignedInfo.append("</Transforms>");
        canonicalSignedInfo.append("<DigestMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#sha1\"></DigestMethod>");
        canonicalSignedInfo.append("<DigestValue>");
        canonicalSignedInfo.append(digest);
        canonicalSignedInfo.append("</DigestValue>");
        canonicalSignedInfo.append("</Reference>");
        canonicalSignedInfo.append("</SignedInfo>");
        StringBuilder signature = new StringBuilder(sign(canonicalSignedInfo.toString(), request.getFirma().getKey()));
        StringBuilder xmlStr = new StringBuilder();
        xmlStr.append("<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:u=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\" xmlns:des=\"http://DescargaMasivaTerceros.sat.gob.mx\" xmlns:xd=\"http://www.w3.org/2000/09/xmldsig#\">");
        xmlStr.append("<s:Header/>");
        xmlStr.append("<s:Body>");
        xmlStr.append("<des:PeticionDescargaMasivaTercerosEntrada>");
        xmlStr.append("<des:peticionDescarga IdPaquete=\"");
        xmlStr.append(request.getIdPaquetes());
        xmlStr.append("\" RfcSolicitante=\"");
        xmlStr.append(request.getRfcConsulta());
        xmlStr.append("\">");
        xmlStr.append("<Signature xmlns=\"http://www.w3.org/2000/09/xmldsig#\">");
        xmlStr.append("<SignedInfo>");
        xmlStr.append("<CanonicalizationMethod Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"/>");
        xmlStr.append("<SignatureMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#rsa-sha1\"/>");
        xmlStr.append("<Reference URI=\"#_0\">");
        xmlStr.append("<Transforms>");
        xmlStr.append("<Transform Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"/>");
        xmlStr.append("</Transforms>");
        xmlStr.append("<DigestMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#sha1\"/>");
        xmlStr.append("<DigestValue>");
        xmlStr.append(digest);
        xmlStr.append("</DigestValue>");
        xmlStr.append("</Reference>");
        xmlStr.append("</SignedInfo>");
        xmlStr.append("<SignatureValue>");
        xmlStr.append(signature);
        xmlStr.append("</SignatureValue>");
        xmlStr.append("<KeyInfo>");
        xmlStr.append("<X509Data>");
        xmlStr.append("<X509IssuerSerial>");
        xmlStr.append("<X509IssuerName>");
        xmlStr.append(request.getFirma().getCert().getIssuerX500Principal());
        xmlStr.append("</X509IssuerName>");
        xmlStr.append("<X509SerialNumber>");
        xmlStr.append(request.getFirma().getCert().getSerialNumber());
        xmlStr.append("</X509SerialNumber>");
        xmlStr.append("</X509IssuerSerial>");
        xmlStr.append("<X509Certificate>");
        xmlStr.append(request.getFirma().getKeyString());
        xmlStr.append("</X509Certificate>");
        xmlStr.append("</X509Data>");
        xmlStr.append("</KeyInfo>");
        xmlStr.append("</Signature>");
        xmlStr.append("</des:peticionDescarga>");
        xmlStr.append("</des:PeticionDescargaMasivaTercerosEntrada>");
        xmlStr.append("</s:Body>");
        xmlStr.append("</s:Envelope>");
        this.setXml(xmlStr.toString());
    }

    public Connection getConn() {
        return conn;
    }

    public Request getRequestOrigen() {
        return requestOrigen;
    }

    @Override
    protected String getResult(String xmlResponse) throws Exception {
        Document doc = convertStringToXMLDocument(xmlResponse);
        if (doc != null) {
            File f = Util.generateVolFile(conn, "CFDI_", ".zip");
            byte[] fileBytes = Base64.getDecoder().decode(doc.getElementsByTagName("Paquete").item(0).getTextContent().getBytes());
            OutputStream os = new FileOutputStream(f);
            os.write(fileBytes);
            os.flush();
            os.close();
            getRequestOrigen().setRutaDescarga(f);
            return f.getAbsolutePath();
        } else
            throw new Exception("La respuesta del servicio del SAT fue nula o no fue posible convertir en XML ");
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }

    public void setRequestOrigen(Request requestOrigen) {
        this.requestOrigen = requestOrigen;
    }
}
