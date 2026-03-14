package com.syc.sai.fonden;

import java.util.HashSet;
import java.util.Set;
import java.util.Base64;

/**
 * TfondenTipoPago entity. @author MyEclipse Persistence Tools
 */
public class FondenTipoPago implements java.io.Serializable {

    // Fields
    private Integer nidTipoPago;

    private String cdescripcion;

    // Constructors
    /**
     * default constructor
     */
    public FondenTipoPago() {
    }

    /**
     * minimal constructor
     */
    public FondenTipoPago(Integer nidTipoPago) {
        this.nidTipoPago = nidTipoPago;
    }

    /**
     * full constructor
     */
    public FondenTipoPago(Integer nidTipoPago, String cdescripcion, Set tfondenFacturacions) {
        this.nidTipoPago = nidTipoPago;
        this.cdescripcion = cdescripcion;
    }

    // Property accessors
    public Integer getNidTipoPago() {
        return this.nidTipoPago;
    }

    public void setNidTipoPago(Integer nidTipoPago) {
        this.nidTipoPago = nidTipoPago;
    }

    public String getCdescripcion() {
        return this.cdescripcion;
    }

    public void setCdescripcion(String cdescripcion) {
        this.cdescripcion = cdescripcion;
    }
}
