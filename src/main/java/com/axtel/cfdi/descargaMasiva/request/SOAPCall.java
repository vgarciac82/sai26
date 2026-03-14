package com.axtel.cfdi.descargaMasiva.request;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.dom.DOMSource;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SOAPCall {

    private static final Logger log = LoggerFactory.getLogger(SOAPCall.class);

    public static void main(String[] args) throws Exception {
        StringBuilder xmlStr = new StringBuilder();
        xmlStr.append("<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:u=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\" xmlns:des=\"http://DescargaMasivaTerceros.sat.gob.mx\" xmlns:xd=\"http://www.w3.org/2000/09/xmldsig#\"><s:Header/><s:Body><des:SolicitaDescarga><des:solicitud RfcEmisor=\"\" RfcReceptor =\"GACV820307QL8\" RfcSolicitante=\"GACV820307QL8\" FechaInicial=\"2020-10-20T00:00:00\" FechaFinal =\"2020-10-21T23:59:59\" TipoSolicitud=\"CFDI\"><Signature xmlns=\"http://www.w3.org/2000/09/xmldsig#\"><SignedInfo><CanonicalizationMethod Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"/><SignatureMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#rsa-sha1\"/><Reference URI=\"#_0\"><Transforms><Transform Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"/></Transforms><DigestMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#sha1\"/><DigestValue>fIA+GfEV719VDNsXpbcacH/dGkg=</DigestValue></Reference></SignedInfo><SignatureValue>OGXueRFXCP2WQYvlOVhnO+OMgzsiznqj+HGfPSgsP1fF809BdyYUIt9+eExvB720M7p2R3bPgH2995lAPnwf0sykSOhl5V20iMkHvyQFB18MlWtcQ8EKnsDkKGJQNlQjJ9xdltha12xYJ649RaFdEgH7OzvoJJbNs4m/vs6f2uIQ3mZYtV34VjDak4ZjhsOkVgT+XroNlZ36w73L1pKRK6m5PhPxQJLrOY8S+n7XXUSmTt1XSI7fOR14wrZyoKIgRL5pEYC2nlMBcOwb0iiZ+LJzpcMCgrzxqJ//n9Gk/mebxU4YeqKVniu8LoF2cYS5GG+egvvsJm79tWuB9tE5TA==</SignatureValue><KeyInfo><X509Data><X509IssuerSerial><X509IssuerName>OID.1.2.840.113549.1.9.2=Responsable: Administración Central de Servicios Tributarios al Contribuyente, OID.2.5.4.45=SAT970701NN3, L=Cuauhtémoc, ST=Distrito Federal, C=MX, OID.2.5.4.17=06300, STREET=\"Av. Hidalgo 77, Col. Guerrero\", EMAILADDRESS=acods@sat.gob.mx, OU=Administración de Seguridad de la Información, O=Servicio de Administración Tributaria, CN=A.C. del Servicio de Administración Tributaria</X509IssuerName><X509SerialNumber>275106190557734483187066766810933709147060187952</X509SerialNumber></X509IssuerSerial><X509Certificate>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</X509Certificate></X509Data></KeyInfo></Signature></des:solicitud></des:SolicitaDescarga></s:Body></s:Envelope>");
        String soapEndpointUrl = "https://cfdidescargamasivasolicitud.clouda.sat.gob.mx/SolicitaDescargaService.svc";
        String soapAction = "http://DescargaMasivaTerceros.sat.gob.mx/ISolicitaDescargaService/SolicitaDescarga";
        callSoapWebService(soapEndpointUrl, soapAction, xmlStr.toString(), "");
    }

    private static DOMSource stringToXML(String xmlStr) throws Exception {
        DOMSource domSource = null;
        InputStream is = new ByteArrayInputStream(xmlStr.toString().getBytes(StandardCharsets.UTF_8.toString()));
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        dbFactory.setNamespaceAware(true);
        DocumentBuilder builder = dbFactory.newDocumentBuilder();
        Document document = builder.parse(is);
        domSource = new DOMSource(document);
        return domSource;
    }

    private static void createSoapEnvelope(SOAPMessage soapMessage, String xml) throws Exception {
        SOAPPart soapPart = soapMessage.getSOAPPart();
        DOMSource domSource = stringToXML(xml);
        soapPart.setContent(domSource);
        soapMessage.saveChanges();
    }

    public static String callSoapWebService(String soapEndpointUrl, String soapAction, String xml, String token) throws Exception {
        try {
            // Create SOAP Connection
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();
            // Send SOAP Message to SOAP Server
            SOAPMessage soapResponse = soapConnection.call(createSOAPRequest(soapAction, xml, token), soapEndpointUrl);
            // Print the SOAP Response
            log.info("Response SOAP Message:");
            soapResponse.writeTo(System.out);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            soapResponse.writeTo(baos);
            String result = new String(baos.toByteArray(), "UTF-8");
            log.info("Object: {}", "Request SOAP Message:\nR E S U L T: \n" + result);
            soapConnection.close();
            return result;
        } catch (Exception e) {
            System.err.println("\nError occurred while sending SOAP Request to Server!\nMake sure you have the correct endpoint URL and SOAPAction!\n");
            e.printStackTrace();
            throw e;
        }
    }

    private static SOAPMessage createSOAPRequest(String soapAction, String xml, String token) throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        createSoapEnvelope(soapMessage, xml);
        MimeHeaders headers = soapMessage.getMimeHeaders();
        headers.addHeader("SOAPAction", soapAction);
        if (!StringUtils.isBlank(token))
            headers.addHeader("Authorization", token);
        // soapMessage.saveChanges();
        /* Print the request message, just for debugging purposes */
        log.info("Request SOAP Message:");
        soapMessage.writeTo(System.out);
        return soapMessage;
    }
}
