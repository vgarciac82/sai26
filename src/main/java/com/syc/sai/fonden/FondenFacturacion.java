package com.syc.sai.fonden;

import java.util.Base64;

/**
 * TfondenFacturacion entity. @author MyEclipse Persistence Tools
 */
public class FondenFacturacion implements java.io.Serializable {

    // Fields
    private Integer cidFonden;

    private Integer nidFondenMovimiento;

    private Integer nidFondenFacturacion;

    private String cnumero;

    private Long ncantidad;

    private Double nimporteFactura;

    private Double nTipoCambio;

    private Integer nIdTipoPago;

    private Integer nIdTipoFactura;

    private String cDescripcionFactura;

    // Constructors
    public Double getnTipoCambio() {
        return nTipoCambio;
    }

    public void setnTipoCambio(Double nTipoCambio) {
        this.nTipoCambio = nTipoCambio;
    }

    public Integer getnIdTipoPago() {
        return nIdTipoPago;
    }

    public void setnIdTipoPago(Integer nIdTipoPago) {
        this.nIdTipoPago = nIdTipoPago;
    }

    public Integer getnIdTipoFactura() {
        return nIdTipoFactura;
    }

    public void setnIdTipoFactura(Integer nIdTipoFactura) {
        this.nIdTipoFactura = nIdTipoFactura;
    }

    public String getcDescripcionFactura() {
        return cDescripcionFactura;
    }

    public void setcDescripcionFactura(String cDescripcionFactura) {
        this.cDescripcionFactura = cDescripcionFactura;
    }

    /**
     * default constructor
     */
    public FondenFacturacion() {
    }

    public String getCnumero() {
        return this.cnumero;
    }

    public void setCnumero(String cnumero) {
        this.cnumero = cnumero;
    }

    public Long getNcantidad() {
        return this.ncantidad;
    }

    public void setNcantidad(Long ncantidad) {
        this.ncantidad = ncantidad;
    }

    public Double getNimporteFactura() {
        return this.nimporteFactura;
    }

    public void setNimporteFactura(Double nimporteFactura) {
        this.nimporteFactura = nimporteFactura;
    }

    public Integer getCidFonden() {
        return cidFonden;
    }

    public void setCidFonden(Integer cidFonden) {
        this.cidFonden = cidFonden;
    }

    public Integer getNidFondenMovimiento() {
        return nidFondenMovimiento;
    }

    public void setNidFondenMovimiento(Integer nidFondenMovimiento) {
        this.nidFondenMovimiento = nidFondenMovimiento;
    }

    public Integer getNidFondenFacturacion() {
        return nidFondenFacturacion;
    }

    public void setNidFondenFacturacion(Integer nidFondenFacturacion) {
        this.nidFondenFacturacion = nidFondenFacturacion;
    }
}
