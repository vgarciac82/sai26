package com.axtel.cfdi.descargaMasiva;

import java.io.File;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.util.Date;
import org.w3c.dom.Document;
import com.axtel.cfdi.descargaMasiva.request.RequestBase;
import com.syc.gestion.util.Util;
import com.syc.sai.firmaElectronica.core.FirmaElectronica;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class Request extends RequestBase {

    private static final String ACEPTADA = "5000";

    private static final Logger log = LoggerFactory.getLogger(Request.class);

    private String codigoStatus;

    private Date fFin;

    private Date fInicio;

    private FirmaElectronica firma;

    private String idPaquetes;

    private String idRequest;

    private String rfcConsulta = "";

    private String rfcEmisor = "";

    private String rfcReceptor = "";

    private File rutaDescarga = null;

    private File rutaProcesado = null;

    private String statusRequest;

    private String totalCFDI = "0";

    private String typeRequest = "CFDI";

    /**
     * Constructor of Request class
     *
     * @param url
     * @param SOAPAction
     */
    public Request(String url, String SOAPAction, Date fFin, Date fInicio, FirmaElectronica firma, String rfcConsulta, String rfcEmisor, String rfcReceptor) {
        super(url, SOAPAction);
        this.fFin = fFin;
        this.fInicio = fInicio;
        this.firma = firma;
        this.rfcConsulta = rfcConsulta;
        this.rfcEmisor = rfcEmisor;
        this.rfcReceptor = rfcReceptor;
        log.trace("Object: {}", "Request creado.\nURL:" + url + "\nAction:" + SOAPAction);
    }

    /**
     * Generate XML to send through SAT's web service
     *
     * @param certificate
     * @param privateKey
     * @param rfcEmisor
     * @param rfcReceptor
     * @param rfcSolicitante
     * @param fechaInicial
     * @param fechaFinal
     * @throws NoSuchAlgorithmException
     * @throws SignatureException
     * @throws InvalidKeyException
     * @throws CertificateEncodingException
     */
    public void generate() throws NoSuchAlgorithmException, SignatureException, InvalidKeyException, CertificateEncodingException {
        String fechaInicial = Util.dateToString(getfInicio(), "yyyy-MM-dd") + "T00:00:00";
        String fechaFinal = Util.dateToString(getfFin(), "yyyy-MM-dd") + "T23:59:59";
        log.trace("Object: {}", "Se enviara busqueda al SAT con los siguientes parametros:\nFecha Inicio:" + fechaInicial + "\nFecha Final:" + fechaFinal + "\nRFC:" + rfcReceptor);
        StringBuilder canonicalTimestamp = new StringBuilder();
        canonicalTimestamp.append("<des:SolicitaDescarga xmlns:des=\"http://DescargaMasivaTerceros.sat.gob.mx\">");
        canonicalTimestamp.append("<des:solicitud RfcEmisor=\"");
        canonicalTimestamp.append(getRfcEmisor());
        canonicalTimestamp.append("\" RfcReceptor=\"");
        canonicalTimestamp.append(getRfcReceptor());
        canonicalTimestamp.append("\" RfcSolicitante=\"");
        canonicalTimestamp.append(getRfcConsulta());
        canonicalTimestamp.append("\" FechaInicial=\"");
        canonicalTimestamp.append(fechaInicial);
        canonicalTimestamp.append("\" FechaFinal=\"");
        canonicalTimestamp.append(fechaFinal);
        canonicalTimestamp.append("\" TipoSolicitud=\"");
        canonicalTimestamp.append(getTypeRequest());
        canonicalTimestamp.append("\">");
        canonicalTimestamp.append("</des:solicitud>");
        canonicalTimestamp.append("</des:SolicitaDescarga>");
        log.info("Object: {}", "Request Generado: " + canonicalTimestamp.toString());
        String digest = createDigest(canonicalTimestamp.toString());
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
        String signature = sign(canonicalSignedInfo.toString(), getFirma().getKey());
        StringBuilder xmlSolicitud = new StringBuilder();
        xmlSolicitud.append("<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:u=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\" xmlns:des=\"http://DescargaMasivaTerceros.sat.gob.mx\" xmlns:xd=\"http://www.w3.org/2000/09/xmldsig#\">");
        xmlSolicitud.append("<s:Header/>");
        xmlSolicitud.append("<s:Body>");
        xmlSolicitud.append("<des:SolicitaDescarga>");
        xmlSolicitud.append("<des:solicitud RfcEmisor=\"");
        xmlSolicitud.append(getRfcEmisor());
        xmlSolicitud.append("\" RfcReceptor =\"");
        xmlSolicitud.append(getRfcReceptor());
        xmlSolicitud.append("\" RfcSolicitante=\"");
        xmlSolicitud.append(getRfcConsulta());
        xmlSolicitud.append("\" FechaInicial=\"");
        xmlSolicitud.append(fechaInicial);
        xmlSolicitud.append("\" FechaFinal =\"");
        xmlSolicitud.append(fechaFinal);
        xmlSolicitud.append("\" TipoSolicitud=\"");
        xmlSolicitud.append(getTypeRequest());
        xmlSolicitud.append("\">");
        xmlSolicitud.append("<Signature xmlns=\"http://www.w3.org/2000/09/xmldsig#\">");
        xmlSolicitud.append("<SignedInfo>");
        xmlSolicitud.append("<CanonicalizationMethod Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"/>");
        xmlSolicitud.append("<SignatureMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#rsa-sha1\"/>");
        xmlSolicitud.append("<Reference URI=\"#_0\">");
        xmlSolicitud.append("<Transforms>");
        xmlSolicitud.append("<Transform Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"/>");
        xmlSolicitud.append("</Transforms>");
        xmlSolicitud.append("<DigestMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#sha1\"/>");
        xmlSolicitud.append("<DigestValue>");
        xmlSolicitud.append(digest);
        xmlSolicitud.append("</DigestValue>");
        xmlSolicitud.append("</Reference>");
        xmlSolicitud.append("</SignedInfo>");
        xmlSolicitud.append("<SignatureValue>");
        xmlSolicitud.append(signature);
        xmlSolicitud.append("</SignatureValue>");
        xmlSolicitud.append("<KeyInfo>");
        xmlSolicitud.append("<X509Data>");
        xmlSolicitud.append("<X509IssuerSerial>");
        xmlSolicitud.append("<X509IssuerName>");
        xmlSolicitud.append(getFirma().getCert().getIssuerX500Principal());
        xmlSolicitud.append("</X509IssuerName>");
        xmlSolicitud.append("<X509SerialNumber>");
        xmlSolicitud.append(getFirma().getCert().getSerialNumber());
        xmlSolicitud.append("</X509SerialNumber>");
        xmlSolicitud.append("</X509IssuerSerial>");
        xmlSolicitud.append("<X509Certificate>");
        xmlSolicitud.append(getFirma().getKeyString());
        xmlSolicitud.append("</X509Certificate>");
        xmlSolicitud.append("</X509Data>");
        xmlSolicitud.append("</KeyInfo>");
        xmlSolicitud.append("</Signature>");
        xmlSolicitud.append("</des:solicitud>");
        xmlSolicitud.append("</des:SolicitaDescarga>");
        xmlSolicitud.append("</s:Body>");
        xmlSolicitud.append("</s:Envelope>");
        log.trace("Object: {}", "XML enviado:\n" + xmlSolicitud);
        this.setXml(xmlSolicitud.toString());
    }

    public String getCodigoStatus() {
        return codigoStatus;
    }

    public Date getfFin() {
        return fFin;
    }

    public Date getfInicio() {
        return fInicio;
    }

    public FirmaElectronica getFirma() {
        return firma;
    }

    public String getIdPaquetes() {
        return idPaquetes;
    }

    public String getIdRequest() {
        return idRequest;
    }

    @Override
    protected String getResult(String xmlResponse) throws Exception {
        log.debug("Object: " + String.valueOf("Respuesta recibida: " + xmlResponse));
        Document doc = convertStringToXMLDocument(xmlResponse);
        if (doc != null) {
            String codStatus = doc.getElementsByTagName("SolicitaDescargaResult").item(0).getAttributes().getNamedItem("CodEstatus").getTextContent();
            if (codStatus.equals(ACEPTADA)) {
                setStatusRequest(codStatus);
                String numSolicitud = doc.getElementsByTagName("SolicitaDescargaResult").item(0).getAttributes().getNamedItem("IdSolicitud").getTextContent();
                return numSolicitud;
            } else {
                String msg = doc.getElementsByTagName("SolicitaDescargaResult").item(0).getAttributes().getNamedItem("Mensaje").getTextContent();
                throw new Exception("El SAT rechazo la solicitud con el siguiente mensaje:[" + codStatus + " " + msg + "]");
            }
        } else {
            throw new Exception("No se recibio respuesta del SAT");
        }
    }

    public String getRfcConsulta() {
        return rfcConsulta;
    }

    public String getRfcEmisor() {
        return rfcEmisor;
    }

    public String getRfcReceptor() {
        return rfcReceptor;
    }

    public File getRutaDescarga() {
        return rutaDescarga;
    }

    public File getRutaProcesado() {
        return rutaProcesado;
    }

    public String getStatusRequest() {
        return statusRequest;
    }

    public String getTotalCFDI() {
        return totalCFDI;
    }

    public String getTypeRequest() {
        return typeRequest;
    }

    public void setCodigoStatus(String codigoStatus) {
        this.codigoStatus = codigoStatus;
    }

    public void setfFin(Date fFin) {
        this.fFin = fFin;
    }

    public void setfInicio(Date fInicio) {
        this.fInicio = fInicio;
    }

    public void setFirma(FirmaElectronica firma) {
        this.firma = firma;
    }

    public void setIdPaquetes(String idPaquetes) {
        this.idPaquetes = idPaquetes;
    }

    public void setIdRequest(String idRequest) {
        this.idRequest = idRequest;
    }

    public void setRfcConsulta(String rfcConsulta) {
        this.rfcConsulta = rfcConsulta;
    }

    public void setRfcEmisor(String rfcEmisor) {
        this.rfcEmisor = rfcEmisor;
    }

    public void setRfcReceptor(String rfcReceptor) {
        this.rfcReceptor = rfcReceptor;
    }

    public void setRutaDescarga(File rutaDescarga) {
        this.rutaDescarga = rutaDescarga;
    }

    public void setRutaProcesado(File rutaProcesado) {
        this.rutaProcesado = rutaProcesado;
    }

    public void setStatusRequest(String statusRequest) {
        this.statusRequest = statusRequest;
    }

    public void setTotalCFDI(String totalCFDI) {
        this.totalCFDI = totalCFDI;
    }

    public void setTypeRequest(String typeRequest) {
        this.typeRequest = typeRequest;
    }

    @Override
    public String toString() {
        return "Request [fFin=" + fFin + ", fInicio=" + fInicio + ", rfcConsulta=" + rfcConsulta + ", rfcEmisor=" + rfcEmisor + ", rfcReceptor=" + rfcReceptor + ", typeRequest=" + typeRequest + "]";
    }
}
