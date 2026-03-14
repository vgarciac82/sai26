package com.axtel.cfdi;

import java.math.BigDecimal;
import java.util.Date;
import com.axtel.cfdi.core.DomicilioFiscal;
import java.util.Base64;

public class CFDIEncabezado {

    private String cadenaOriginal;

    private String cadenaOriginalComplemento;

    private String certificado;

    private int cfdiId;

    private String condicionesDePago;

    private BigDecimal descuento = new BigDecimal(0.00);

    private final DomicilioFiscal dfe = new DomicilioFiscal("Periférico Poniente", "5360", "San Juan de Ocotán", "Zapopan", "Jalisco", 45019);

    private String donativoAutorizacion;

    private Date donativoFechaAutorizacion;

    private Emisor emisor;

    private int estatusId;

    private Date fecha;

    private String folio;

    private FormaPago formaPago;

    private CodigoPostal lugarExpedicion;

    private MetodoPago metodoPago;

    private Moneda moneda;

    private String noCertificado;

    private String noCertificadoSAT;

    private String numCtaPago;

    private Periodicidad periodicidad;

    private Receptor receptor;

    private RegimenFiscal regimenFiscal;

    private String rfcProvCertif;

    private String sello;

    private String selloSAT;

    private Serie serie;

    private BigDecimal subTotal;

    private BigDecimal tipoCambio = new BigDecimal(1.00);

    private TipoDeComprobante tipoDeComprobante;

    private TipoRelacion tipoRelacion;

    private BigDecimal total;

    private UsoCFDI usoCFDI;

    private String uuid;

    private String version;

    public CFDIEncabezado() {
        RegimenFiscal regimenEmisor = new RegimenFiscal();
        regimenEmisor.setRegimenFiscal("603");
        this.regimenFiscal = regimenEmisor;
        this.emisor = new Emisor("CNF010405EG1", "Comisión Nacional Forestal", "603", "45019");
        this.emisor.setDomicilio(dfe);
    }

    public String getCadenaOriginal() {
        return cadenaOriginal;
    }

    public String getCadenaOriginalComplemento() {
        return cadenaOriginalComplemento;
    }

    public String getCertificado() {
        return certificado;
    }

    public int getCfdiId() {
        return cfdiId;
    }

    public String getCondicionesDePago() {
        return condicionesDePago;
    }

    public BigDecimal getDescuento() {
        return descuento;
    }

    public DomicilioFiscal getDfe() {
        return dfe;
    }

    public String getDonativoAutorizacion() {
        return donativoAutorizacion;
    }

    public Date getDonativoFechaAutorizacion() {
        return donativoFechaAutorizacion;
    }

    public Emisor getEmisor() {
        return emisor;
    }

    public int getEstatusId() {
        return estatusId;
    }

    public Date getFecha() {
        return fecha;
    }

    public String getFolio() {
        return folio;
    }

    public FormaPago getFormaPago() {
        return formaPago;
    }

    public CodigoPostal getLugarExpedicion() {
        return lugarExpedicion;
    }

    public MetodoPago getMetodoPago() {
        return metodoPago;
    }

    public Moneda getMoneda() {
        return moneda;
    }

    public String getNoCertificado() {
        return noCertificado;
    }

    public String getNoCertificadoSAT() {
        return noCertificadoSAT;
    }

    public String getNumCtaPago() {
        return numCtaPago;
    }

    public Periodicidad getPeriodicidad() {
        return periodicidad;
    }

    public Receptor getReceptor() {
        return receptor;
    }

    public RegimenFiscal getRegimenFiscal() {
        return regimenFiscal;
    }

    public String getRfcProvCertif() {
        return rfcProvCertif;
    }

    public String getSello() {
        return sello;
    }

    public String getSelloSAT() {
        return selloSAT;
    }

    public Serie getSerie() {
        return serie;
    }

    public BigDecimal getSubTotal() {
        return subTotal;
    }

    public BigDecimal getTipoCambio() {
        return tipoCambio;
    }

    public TipoDeComprobante getTipoDeComprobante() {
        return tipoDeComprobante;
    }

    public TipoRelacion getTipoRelacion() {
        return tipoRelacion;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public UsoCFDI getUsoCFDI() {
        return usoCFDI;
    }

    public String getUuid() {
        return uuid;
    }

    public String getVersion() {
        return version;
    }

    public void setCadenaOriginal(String cadenaOriginal) {
        this.cadenaOriginal = cadenaOriginal;
    }

    public void setCadenaOriginalComplemento(String cadenaOriginalComplemento) {
        this.cadenaOriginalComplemento = cadenaOriginalComplemento;
    }

    public void setCertificado(String certificado) {
        this.certificado = certificado;
    }

    public void setCfdiId(int cfdiId) {
        this.cfdiId = cfdiId;
    }

    public void setCondicionesDePago(String condicionesDePago) {
        this.condicionesDePago = condicionesDePago;
    }

    public void setDescuento(BigDecimal descuento) {
        this.descuento = descuento;
    }

    public void setDonativoAutorizacion(String donativoAutorizacion) {
        this.donativoAutorizacion = donativoAutorizacion;
    }

    public void setDonativoFechaAutorizacion(Date donativoFechaAutorizacion) {
        this.donativoFechaAutorizacion = donativoFechaAutorizacion;
    }

    public void setEmisor(Emisor emisor) {
        this.emisor = emisor;
    }

    public void setEstatusId(int estatusId) {
        this.estatusId = estatusId;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public void setFormaPago(FormaPago formaPago) {
        this.formaPago = formaPago;
    }

    public void setLugarExpedicion(CodigoPostal lugarExpedicion) {
        this.lugarExpedicion = lugarExpedicion;
    }

    public void setMetodoPago(MetodoPago metodoPago) {
        this.metodoPago = metodoPago;
    }

    public void setMoneda(Moneda moneda) {
        this.moneda = moneda;
    }

    public void setNoCertificado(String noCertificado) {
        this.noCertificado = noCertificado;
    }

    public void setNoCertificadoSAT(String noCertificadoSAT) {
        this.noCertificadoSAT = noCertificadoSAT;
    }

    public void setNumCtaPago(String numCtaPago) {
        this.numCtaPago = numCtaPago;
    }

    public void setPeriodicidad(Periodicidad periodicidad) {
        this.periodicidad = periodicidad;
    }

    public void setReceptor(Receptor receptor) {
        this.receptor = receptor;
    }

    public void setRegimenFiscal(RegimenFiscal regimenFiscal) {
        this.regimenFiscal = regimenFiscal;
    }

    public void setRfcProvCertif(String rfcProvCertif) {
        this.rfcProvCertif = rfcProvCertif;
    }

    public void setSello(String sello) {
        this.sello = sello;
    }

    public void setSelloSAT(String selloSAT) {
        this.selloSAT = selloSAT;
    }

    public void setSerie(Serie serie) {
        this.serie = serie;
    }

    public void setSubTotal(BigDecimal subTotal) {
        this.subTotal = subTotal;
    }

    public void setTipoCambio(BigDecimal tipoCambio) {
        this.tipoCambio = tipoCambio;
    }

    public void setTipoDeComprobante(TipoDeComprobante tipoDeComprobante) {
        this.tipoDeComprobante = tipoDeComprobante;
    }

    public void setTipoRelacion(TipoRelacion tipoRelacion) {
        this.tipoRelacion = tipoRelacion;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public void setUsoCFDI(UsoCFDI usoCFDI) {
        this.usoCFDI = usoCFDI;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "CFDIEncabezado [cadenaOriginal=" + cadenaOriginal + ", cadenaOriginalComplemento=" + cadenaOriginalComplemento + ", certificado=" + certificado + ", cfdiId=" + cfdiId + ", condicionesDePago=" + condicionesDePago + ", descuento=" + descuento + ", dfe=" + dfe + ", donativoAutorizacion=" + donativoAutorizacion + ", donativoFechaAutorizacion=" + donativoFechaAutorizacion + ", emisor=" + emisor + ", estatusId=" + estatusId + ", fecha=" + fecha + ", folio=" + folio + ", formaPago=" + formaPago + ", lugarExpedicion=" + lugarExpedicion + ", metodoPago=" + metodoPago + ", moneda=" + moneda + ", noCertificado=" + noCertificado + ", noCertificadoSAT=" + noCertificadoSAT + ", numCtaPago=" + numCtaPago + ", periodicidad=" + periodicidad + ", receptor=" + receptor + ", regimenFiscal=" + regimenFiscal + ", rfcProvCertif=" + rfcProvCertif + ", sello=" + sello + ", selloSAT=" + selloSAT + ", serie=" + serie + ", subTotal=" + subTotal + ", tipoCambio=" + tipoCambio + ", tipoDeComprobante=" + tipoDeComprobante + ", tipoRelacion=" + tipoRelacion + ", total=" + total + ", usoCFDI=" + usoCFDI + ", uuid=" + uuid + ", version=" + version + "]";
    }
}
