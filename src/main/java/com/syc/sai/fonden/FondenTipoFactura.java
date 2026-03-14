package com.syc.sai.fonden;

import java.util.HashSet;
import java.util.Set;
import java.util.Base64;

/**
 * TfondenTipoFactura entity. @author MyEclipse Persistence Tools
 */
public class FondenTipoFactura implements java.io.Serializable {

    // Fields
    private Integer nidTipoFactura;

    private String cdescripcion;

    // Constructors
    /**
     * default constructor
     */
    public FondenTipoFactura() {
    }

    /**
     * minimal constructor
     */
    public FondenTipoFactura(Integer nidTipoFactura) {
        this.nidTipoFactura = nidTipoFactura;
    }

    /**
     * full constructor
     */
    public FondenTipoFactura(Integer nidTipoFactura, String cdescripcion, Set tfondenFacturacions) {
        this.nidTipoFactura = nidTipoFactura;
        this.cdescripcion = cdescripcion;
    }

    // Property accessors
    public Integer getNidTipoFactura() {
        return this.nidTipoFactura;
    }

    public void setNidTipoFactura(Integer nidTipoFactura) {
        this.nidTipoFactura = nidTipoFactura;
    }

    public String getCdescripcion() {
        return this.cdescripcion;
    }

    public void setCdescripcion(String cdescripcion) {
        this.cdescripcion = cdescripcion;
    }
}
