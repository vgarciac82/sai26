package com.syc.sai.fonden;

import java.util.Base64;

/**
 * TfondenMovimiento entity. @author MyEclipse Persistence Tools
 */
public class FondenMovimiento implements java.io.Serializable {

    // Fields
    private Integer cidFonden;

    private Integer nidFondenMovimiento;

    private String cconcepto;

    private Double nprecioUnitario;

    private Integer ncantidad;

    private Double nimporte;

    private String crfc;

    private String cnumPedido;

    private Double nnetoPedido;

    private Double nprecio;

    private Double ntotal;

    private String cproveedor;

    private String activo;

    private Integer idGabinete;

    private String cFolio;

    private String cCentroContable;

    private String cUnidadResponsable;

    private Integer nNumCaso;

    private Integer nCantidadTotal;

    private Double nTechoDef;

    private Double nTipoCambio;

    private String cIdMoneda;

    private Integer nIdFondenMovEstatus;

    // Constructors
    public Integer getnIdFondenMovEstatus() {
        return nIdFondenMovEstatus;
    }

    public void setnIdFondenMovEstatus(Integer nIdFondenMovEstatus) {
        this.nIdFondenMovEstatus = nIdFondenMovEstatus;
    }

    public String getcIdMoneda() {
        return cIdMoneda;
    }

    public void setcIdMoneda(String cIdMoneda) {
        this.cIdMoneda = cIdMoneda;
    }

    public Double getnTipoCambio() {
        return nTipoCambio;
    }

    public void setnTipoCambio(Double nTipoCambio) {
        this.nTipoCambio = nTipoCambio;
    }

    public Double getnTechoDef() {
        return nTechoDef;
    }

    public void setnTechoDef(Double nTechoDef) {
        this.nTechoDef = nTechoDef;
    }

    public Integer getnCantidadTotal() {
        return nCantidadTotal;
    }

    public void setnCantidadTotal(Integer nCantidadTotal) {
        this.nCantidadTotal = nCantidadTotal;
    }

    public Integer getnNumCaso() {
        return nNumCaso;
    }

    public void setnNumCaso(Integer nNumCaso) {
        this.nNumCaso = nNumCaso;
    }

    public String getcCentroContable() {
        return cCentroContable;
    }

    public void setcCentroContable(String cCentroContable) {
        this.cCentroContable = cCentroContable;
    }

    public String getcUnidadResponsable() {
        return cUnidadResponsable;
    }

    public void setcUnidadResponsable(String cUnidadResponsable) {
        this.cUnidadResponsable = cUnidadResponsable;
    }

    public String getcFolio() {
        return cFolio;
    }

    public void setcFolio(String cFolio) {
        this.cFolio = cFolio;
    }

    /**
     * default constructor
     */
    public FondenMovimiento() {
    }

    public String getCconcepto() {
        return this.cconcepto;
    }

    public void setCconcepto(String cconcepto) {
        this.cconcepto = cconcepto;
    }

    public Double getNprecioUnitario() {
        return this.nprecioUnitario;
    }

    public void setNprecioUnitario(Double nprecioUnitario) {
        this.nprecioUnitario = nprecioUnitario;
    }

    public Integer getNcantidad() {
        return this.ncantidad;
    }

    public void setNcantidad(Integer ncantidad) {
        this.ncantidad = ncantidad;
    }

    public Double getNimporte() {
        return this.nimporte;
    }

    public void setNimporte(Double nimporte) {
        this.nimporte = nimporte;
    }

    public String getCrfc() {
        return this.crfc;
    }

    public void setCrfc(String crfc) {
        this.crfc = crfc;
    }

    public String getCnumPedido() {
        return this.cnumPedido;
    }

    public void setCnumPedido(String cnumPedido) {
        this.cnumPedido = cnumPedido;
    }

    public Double getNnetoPedido() {
        return this.nnetoPedido;
    }

    public void setNnetoPedido(Double nnetoPedido) {
        this.nnetoPedido = nnetoPedido;
    }

    public Double getNprecio() {
        return this.nprecio;
    }

    public void setNprecio(Double nprecio) {
        this.nprecio = nprecio;
    }

    public Double getNtotal() {
        return this.ntotal;
    }

    public void setNtotal(Double ntotal) {
        this.ntotal = ntotal;
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

    public String getCproveedor() {
        return cproveedor;
    }

    public void setCproveedor(String cproveedor) {
        this.cproveedor = cproveedor;
    }

    public String getActivo() {
        return activo;
    }

    public void setActivo(String activo) {
        this.activo = activo;
    }

    public Integer getIdGabinete() {
        return idGabinete;
    }

    public void setIdGabinete(Integer idGabinete) {
        this.idGabinete = idGabinete;
    }
}
