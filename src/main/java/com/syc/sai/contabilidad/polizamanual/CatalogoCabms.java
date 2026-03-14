package com.syc.sai.contabilidad.polizamanual;

import java.util.Base64;

/**
 * TcatalogoCabms entity. @author MyEclipse Persistence Tools
 */
public class CatalogoCabms implements java.io.Serializable {

    // Fields
    private String cdescripcion;

    private String ncuenta;

    private String nidUnidadMedida;

    private String ccabms;

    private Integer ccucop;

    private String cpartida;

    // Constructors
    /**
     * default constructor
     */
    public CatalogoCabms() {
    }

    /**
     * minimal constructor
     */
    public CatalogoCabms(String ncuenta, String nidUnidadMedida) {
        this.ncuenta = ncuenta;
        this.nidUnidadMedida = nidUnidadMedida;
    }

    // Property accessors
    public String getCdescripcion() {
        return this.cdescripcion;
    }

    public void setCdescripcion(String cdescripcion) {
        this.cdescripcion = cdescripcion;
    }

    public String getNcuenta() {
        return this.ncuenta;
    }

    public void setNcuenta(String ncuenta) {
        this.ncuenta = ncuenta;
    }

    public String getNidUnidadMedida() {
        return this.nidUnidadMedida;
    }

    public void setNidUnidadMedida(String nidUnidadMedida) {
        this.nidUnidadMedida = nidUnidadMedida;
    }

    public String getCcabms() {
        return ccabms;
    }

    public void setCcabms(String ccabms) {
        this.ccabms = ccabms;
    }

    public Integer getCcucop() {
        return ccucop;
    }

    public void setCcucop(Integer ccucop) {
        this.ccucop = ccucop;
    }

    public String getCpartida() {
        return cpartida;
    }

    public void setCpartida(String cpartida) {
        this.cpartida = cpartida;
    }
}
