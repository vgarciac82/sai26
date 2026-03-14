package com.axtel.cfdi;

import java.math.BigDecimal;
import java.util.Base64;

public class CFDIDetalle {

    private int cfdiDetalleId;

    private int cfdiId;

    private ClaveProdServ claveProdServ;

    private String noIdentificacion;

    private BigDecimal cantidad;

    private ClaveUnidad claveUnidad;

    private String unidad;

    private String descripcion;

    private BigDecimal valorUnitario;

    private BigDecimal importe;

    private BigDecimal descuento;

    private ObjetoImp objetoImp;

    public int getCfdiDetalleId() {
        return cfdiDetalleId;
    }

    public void setCfdiDetalleId(int cfdiDetalleId) {
        this.cfdiDetalleId = cfdiDetalleId;
    }

    public int getCfdiId() {
        return cfdiId;
    }

    public void setCfdiId(int cfdiId) {
        this.cfdiId = cfdiId;
    }

    public ClaveProdServ getClaveProdServ() {
        return claveProdServ;
    }

    public void setClaveProdServ(ClaveProdServ claveProdServ) {
        this.claveProdServ = claveProdServ;
    }

    public String getNoIdentificacion() {
        return noIdentificacion;
    }

    public void setNoIdentificacion(String noIdentificacion) {
        this.noIdentificacion = noIdentificacion;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }

    public ClaveUnidad getClaveUnidad() {
        return claveUnidad;
    }

    public void setClaveUnidad(ClaveUnidad claveUnidad) {
        this.claveUnidad = claveUnidad;
    }

    public String getUnidad() {
        return unidad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(BigDecimal valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    public BigDecimal getImporte() {
        return importe;
    }

    public void setImporte(BigDecimal importe) {
        this.importe = importe;
    }

    public BigDecimal getDescuento() {
        return descuento;
    }

    public void setDescuento(BigDecimal descuento) {
        this.descuento = descuento;
    }

    public ObjetoImp getObjetoImp() {
        return objetoImp;
    }

    public void setObjetoImp(ObjetoImp objetoImp) {
        this.objetoImp = objetoImp;
    }

    @Override
    public String toString() {
        return "CFDIDetalle [cfdiDetalleId=" + cfdiDetalleId + ", cfdiId=" + cfdiId + ", claveProdServ=" + claveProdServ + ", noIdentificacion=" + noIdentificacion + ", cantidad=" + cantidad + ", claveUnidad=" + claveUnidad + ", unidad=" + unidad + ", descripcion=" + descripcion + ", valorUnitario=" + valorUnitario + ", importe=" + importe + ", descuento=" + descuento + ", objetoImp=" + objetoImp + "]";
    }
}
