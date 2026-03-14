package com.syc.sai.contabilidad.polizamanual;

import java.math.BigDecimal;
import java.util.Base64;

/**
 * TdocPolizaDetalle entity. @author MyEclipse Persistence Tools
 */
public class DocPolizaDetalle implements java.io.Serializable {

    // Fields
    private DocPolizaEncabezado tdocPolizaEncabezado;

    private String ncuenta;

    private String nsubCuenta;

    private String cevento;

    private Double mimporte;

    private String cconcepto;

    private BigDecimal nidCasoOrigen;

    private String periodo13;

    private String adefas;

    private Integer ntipoAjuste;

    private String parcial;

    private String ccabms;

    private Integer ccucop;

    private String cpartida;

    private String aejercicioFiscal;

    private String ccentroContable;

    private Integer nfolioDocPoliza;

    private String ctipoPoliza;

    private Integer ndocRenglon;

    // Constructors
    /**
     * default constructor
     */
    public DocPolizaDetalle() {
    }

    /**
     * minimal constructor
     */
    public DocPolizaDetalle(DocPolizaEncabezado tdocPolizaEncabezado, String periodo13, String adefas, Integer ntipoAjuste, String parcial, String ccabms, Integer ccucop, String cpartida) {
        this.tdocPolizaEncabezado = tdocPolizaEncabezado;
        this.periodo13 = periodo13;
        this.adefas = adefas;
        this.ntipoAjuste = ntipoAjuste;
        this.parcial = parcial;
        this.ccabms = ccabms;
        this.ccucop = ccucop;
        this.cpartida = cpartida;
    }

    public DocPolizaEncabezado getTdocPolizaEncabezado() {
        return this.tdocPolizaEncabezado;
    }

    public void setTdocPolizaEncabezado(DocPolizaEncabezado tdocPolizaEncabezado) {
        this.tdocPolizaEncabezado = tdocPolizaEncabezado;
    }

    public String getNcuenta() {
        return this.ncuenta;
    }

    public void setNcuenta(String ncuenta) {
        this.ncuenta = ncuenta;
    }

    public String getNsubCuenta() {
        return this.nsubCuenta;
    }

    public void setNsubCuenta(String nsubCuenta) {
        this.nsubCuenta = nsubCuenta;
    }

    public String getCevento() {
        return this.cevento;
    }

    public void setCevento(String cevento) {
        this.cevento = cevento;
    }

    public Double getMimporte() {
        return this.mimporte;
    }

    public void setMimporte(Double mimporte) {
        this.mimporte = mimporte;
    }

    public String getCconcepto() {
        return this.cconcepto;
    }

    public void setCconcepto(String cconcepto) {
        this.cconcepto = cconcepto;
    }

    public BigDecimal getNidCasoOrigen() {
        return this.nidCasoOrigen;
    }

    public void setNidCasoOrigen(BigDecimal nidCasoOrigen) {
        this.nidCasoOrigen = nidCasoOrigen;
    }

    public String getPeriodo13() {
        return this.periodo13;
    }

    public void setPeriodo13(String periodo13) {
        this.periodo13 = periodo13;
    }

    public String getAdefas() {
        return this.adefas;
    }

    public void setAdefas(String adefas) {
        this.adefas = adefas;
    }

    public Integer getNtipoAjuste() {
        return this.ntipoAjuste;
    }

    public void setNtipoAjuste(Integer ntipoAjuste) {
        this.ntipoAjuste = ntipoAjuste;
    }

    public String getParcial() {
        return this.parcial;
    }

    public void setParcial(String parcial) {
        this.parcial = parcial;
    }

    public String getCcabms() {
        return this.ccabms;
    }

    public void setCcabms(String ccabms) {
        this.ccabms = ccabms;
    }

    public Integer getCcucop() {
        return this.ccucop;
    }

    public void setCcucop(Integer ccucop) {
        this.ccucop = ccucop;
    }

    public String getCpartida() {
        return this.cpartida;
    }

    public void setCpartida(String cpartida) {
        this.cpartida = cpartida;
    }

    public String getAejercicioFiscal() {
        return aejercicioFiscal;
    }

    public void setAejercicioFiscal(String aejercicioFiscal) {
        this.aejercicioFiscal = aejercicioFiscal;
    }

    public String getCcentroContable() {
        return ccentroContable;
    }

    public void setCcentroContable(String ccentroContable) {
        this.ccentroContable = ccentroContable;
    }

    public Integer getNfolioDocPoliza() {
        return nfolioDocPoliza;
    }

    public void setNfolioDocPoliza(Integer nfolioDocPoliza) {
        this.nfolioDocPoliza = nfolioDocPoliza;
    }

    public String getCtipoPoliza() {
        return ctipoPoliza;
    }

    public void setCtipoPoliza(String ctipoPoliza) {
        this.ctipoPoliza = ctipoPoliza;
    }

    public Integer getNdocRenglon() {
        return ndocRenglon;
    }

    public void setNdocRenglon(Integer ndocRenglon) {
        this.ndocRenglon = ndocRenglon;
    }
}
