package com.syc.contable.core;

import java.util.ArrayList;
import java.util.Base64;

public class Reintegro {

    private String nFolioReintegro;

    private String fSolicitud;

    private String cTipoReintegro;

    private String fAplicacion;

    private String cRamo;

    private String cUnidadResponsable;

    private String cDocumentoHaplicado;

    private int nFolioPoliza;

    private String cTipoPoliza;

    private String cRevisado;

    private String aEjercicioFiscal;

    private Consolidacion nFolioConsolidacion;

    private String observaciones;

    private String concepto;

    private String formaPago;

    private String clvRastreo;

    private String fichaDeposito;

    private String clvBanco;

    private String lineaCaptura;

    private double importeLineaCaptura;

    private String motivoRechazo;

    private String cuentaBancaria;

    private ArrayList<ReintegroDetalle> arrReintegroDetalle;

    public String getnFolioReintegro() {
        return nFolioReintegro;
    }

    public void setnFolioReintegro(String nFolioReintegro) {
        this.nFolioReintegro = nFolioReintegro;
    }

    public String getfSolicitud() {
        return fSolicitud;
    }

    public void setfSolicitud(String fSolicitud) {
        this.fSolicitud = fSolicitud;
    }

    public String getcTipoReintegro() {
        return cTipoReintegro;
    }

    public void setcTipoReintegro(String cTipoReintegro) {
        this.cTipoReintegro = cTipoReintegro;
    }

    public String getfAplicacion() {
        return fAplicacion;
    }

    public void setfAplicacion(String fAplicacion) {
        this.fAplicacion = fAplicacion;
    }

    public String getcRamo() {
        return cRamo;
    }

    public void setcRamo(String cRamo) {
        this.cRamo = cRamo;
    }

    public String getcUnidadResponsable() {
        return cUnidadResponsable;
    }

    public void setcUnidadResponsable(String cUnidadResponsable) {
        this.cUnidadResponsable = cUnidadResponsable;
    }

    public String getcDocumentoHaplicado() {
        return cDocumentoHaplicado;
    }

    public void setcDocumentoHaplicado(String cDocumentoHaplicado) {
        this.cDocumentoHaplicado = cDocumentoHaplicado;
    }

    public int getnFolioPoliza() {
        return nFolioPoliza;
    }

    public void setnFolioPoliza(int nFolioPoliza) {
        this.nFolioPoliza = nFolioPoliza;
    }

    public String getcTipoPoliza() {
        return cTipoPoliza;
    }

    public void setcTipoPoliza(String cTipoPoliza) {
        this.cTipoPoliza = cTipoPoliza;
    }

    public String getcRevisado() {
        return cRevisado;
    }

    public void setcRevisado(String cRevisado) {
        this.cRevisado = cRevisado;
    }

    public String getaEjercicioFiscal() {
        return aEjercicioFiscal;
    }

    public void setaEjercicioFiscal(String aEjercicioFiscal) {
        this.aEjercicioFiscal = aEjercicioFiscal;
    }

    public Consolidacion getnFolioConsolidacion() {
        return nFolioConsolidacion;
    }

    public void setnFolioConsolidacion(Consolidacion nFolioConsolidacion) {
        this.nFolioConsolidacion = nFolioConsolidacion;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public String getFormaPago() {
        return formaPago;
    }

    public void setFormaPago(String formaPago) {
        this.formaPago = formaPago;
    }

    public String getClvRastreo() {
        return clvRastreo;
    }

    public void setClvRastreo(String clvRastreo) {
        this.clvRastreo = clvRastreo;
    }

    public String getFichaDeposito() {
        return fichaDeposito;
    }

    public void setFichaDeposito(String fichaDeposito) {
        this.fichaDeposito = fichaDeposito;
    }

    public String getClvBanco() {
        return clvBanco;
    }

    public void setClvBanco(String clvBanco) {
        this.clvBanco = clvBanco;
    }

    public String getLineaCaptura() {
        return lineaCaptura;
    }

    public void setLineaCaptura(String lineaCaptura) {
        this.lineaCaptura = lineaCaptura;
    }

    public double getImporteLineaCaptura() {
        return importeLineaCaptura;
    }

    public void setImporteLineaCaptura(double importeLineaCaptura) {
        this.importeLineaCaptura = importeLineaCaptura;
    }

    public String getMotivoRechazo() {
        return motivoRechazo;
    }

    public void setMotivoRechazo(String motivoRechazo) {
        this.motivoRechazo = motivoRechazo;
    }

    public String getCuentaBancaria() {
        return cuentaBancaria;
    }

    public void setCuentaBancaria(String cuentaBancaria) {
        this.cuentaBancaria = cuentaBancaria;
    }

    public ArrayList<ReintegroDetalle> getArrReintegroDetalle() {
        return arrReintegroDetalle;
    }

    public void setArrReintegroDetalle(ArrayList<ReintegroDetalle> arrReintegroDetalle) {
        this.arrReintegroDetalle = arrReintegroDetalle;
    }
}
