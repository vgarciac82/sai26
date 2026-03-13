package com.axtel.cfdi.descargaMasiva;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.UUID;
import org.w3c.dom.Document;
import com.axtel.cfdi.descargaMasiva.request.RequestBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Authentication extends RequestBase {

    private static final Logger log = LoggerFactory.getLogger(Authentication.class);

    /**
     * Constructor of Authentication class
     *
     * @param url
     * @param SOAPAction
     */
    public Authentication(String url, String SOAPAction) {
        super(url, SOAPAction);
        log.info("Autenticacion creada:\nURL:" + url + "\nAction:" + SOAPAction);
    }

    @Override
    protected String getResult(String xmlResponse) throws Exception {
        log.info("Parseando respuesta: " + xmlResponse);
        Document doc = convertStringToXMLDocument(xmlResponse);
        log.trace("Respuesta parceada: " + doc);
        if (doc != null) {
            if (doc.getElementsByTagName("AutenticaResult") != null && doc.getElementsByTagName("AutenticaResult").getLength() > 0) {
                String token = doc.getElementsByTagName("AutenticaResult").item(0).getTextContent();
                log.info("Token de autentiacion generado por SAT: " + token);
                return token;
            } else
                throw new Exception("Problemas autenticando con FIEL.\nRespuesta SAT: \n" + xmlResponse);
        } else
            throw new Exception("No se logro realizar la autenticacion ante el SAT");
    }

    public void generate(X509Certificate certificate, PrivateKey privateKey, int seconds) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException, CertificateEncodingException {
        log.trace("Generando solicitud de acceso SAT");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Calendar calendarNow = Calendar.getInstance();
        String created = simpleDateFormat.format(calendarNow.getTime());
        // Add 300 seconds which equals 5 minutes
        calendarNow.add(Calendar.SECOND, seconds);
        String expires = simpleDateFormat.format(calendarNow.getTime());
        String uuid = "uuid-" + UUID.randomUUID().toString() + "-1";
        log.trace("Solicitud de acceso SAT \nFecha creacion[" + created + "]\nFecha de Expiracion[" + expires + "]\nUUID[" + uuid + "]");
        String canonicalTimestamp = "<u:Timestamp xmlns:u=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\" u:Id=\"_0\">" + "<u:Created>" + created + "</u:Created>" + "<u:Expires>" + expires + "</u:Expires>" + "</u:Timestamp>";
        String digest = createDigest(canonicalTimestamp);
        String canonicalSignedInfo = "<SignedInfo xmlns=\"http://www.w3.org/2000/09/xmldsig#\">" + "<CanonicalizationMethod Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"></CanonicalizationMethod>" + "<SignatureMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#rsa-sha1\"></SignatureMethod>" + "<Reference URI=\"#_0\">" + "<Transforms>" + "<Transform Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"></Transform>" + "</Transforms>" + "<DigestMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#sha1\"></DigestMethod>" + "<DigestValue>" + digest + "</DigestValue>" + "</Reference>" + "</SignedInfo>";
        String signature = sign(canonicalSignedInfo, privateKey);
        String solicitudXML = "<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:u=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\">" + "<s:Header>" + "<o:Security s:mustUnderstand=\"1\" xmlns:o=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\">" + "<u:Timestamp u:Id=\"_0\">" + "<u:Created>" + created + "</u:Created>" + "<u:Expires>" + expires + "</u:Expires>" + "</u:Timestamp>" + "<o:BinarySecurityToken u:Id=\"" + uuid + "\" ValueType=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-x509-token-profile-1.0#X509v3\" EncodingType=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-soap-message-security-1.0#Base64Binary\">" + Base64.getEncoder().encodeToString(certificate.getEncoded()) + "</o:BinarySecurityToken>" + "<Signature xmlns=\"http://www.w3.org/2000/09/xmldsig#\">" + "<SignedInfo>" + "<CanonicalizationMethod Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"/>" + "<SignatureMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#rsa-sha1\"/>" + "<Reference URI=\"#_0\">" + "<Transforms>" + "<Transform Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"/>" + "</Transforms>" + "<DigestMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#sha1\"/>" + "<DigestValue>" + digest + "</DigestValue>" + "</Reference>" + "</SignedInfo>" + "<SignatureValue>" + signature + "</SignatureValue>" + "<KeyInfo>" + "<o:SecurityTokenReference>" + "<o:Reference ValueType=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-x509-token-profile-1.0#X509v3\" URI=\"#" + uuid + "\"/>" + "</o:SecurityTokenReference>" + "</KeyInfo>" + "</Signature>" + "</o:Security>" + "</s:Header>" + "<s:Body>" + "<Autentica xmlns=\"http://DescargaMasivaTerceros.gob.mx\"/>" + "</s:Body>" + "</s:Envelope>";
        log.debug("Solicitud generada:\n" + solicitudXML);
        this.setXml(solicitudXML);
    }
}
