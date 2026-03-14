package com.syc.contable.core;

import java.math.BigDecimal;
import java.util.Base64;

public class EgresoImpuestos {

    private String tipoPago;

    private int folioPago;

    private String idContrato;

    private String recepcion;

    private BigDecimal montoSinIVA = new BigDecimal(0.00d);

    private BigDecimal montoIVA = new BigDecimal(0.00d);

    private BigDecimal montoOtrosImpuestos = new BigDecimal(0.00d);

    private BigDecimal totalMasIVA = new BigDecimal(0.00d);

    private BigDecimal total = new BigDecimal(0.00d);

    private BigDecimal porcentajeIVA = new BigDecimal(0.00d);

    private BigDecimal porcentajeOtrosImpuestos = new BigDecimal(0.00d);

    public String getTipoPago() {
        return tipoPago;
    }

    public void setTipoPago(String tipoPago) {
        this.tipoPago = tipoPago;
    }

    public int getFolioPago() {
        return folioPago;
    }

    public void setFolioPago(int folioPago) {
        this.folioPago = folioPago;
    }

    public String getIdContrato() {
        return idContrato;
    }

    public void setIdContrato(String idContrato) {
        this.idContrato = idContrato;
    }

    public String getRecepcion() {
        return recepcion;
    }

    public void setRecepcion(String recepcion) {
        this.recepcion = recepcion;
    }

    public BigDecimal getMontoSinIVA() {
        return montoSinIVA;
    }

    public void setMontoSinIVA(BigDecimal montoSinIVA) {
        this.montoSinIVA = montoSinIVA;
    }

    public BigDecimal getMontoIVA() {
        return montoIVA;
    }

    public void setMontoIVA(BigDecimal montoIVA) {
        this.montoIVA = montoIVA;
    }

    public BigDecimal getMontoOtrosImpuestos() {
        return montoOtrosImpuestos;
    }

    public void setMontoOtrosImpuestos(BigDecimal montoOtrosImpuestos) {
        this.montoOtrosImpuestos = montoOtrosImpuestos;
    }

    public BigDecimal getTotalMasIVA() {
        return totalMasIVA;
    }

    public void setTotalMasIVA(BigDecimal totalMasIVA) {
        this.totalMasIVA = totalMasIVA;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getPorcentajeIVA() {
        return porcentajeIVA;
    }

    public void setPorcentajeIVA(BigDecimal porcentajeIVA) {
        this.porcentajeIVA = porcentajeIVA;
    }

    public BigDecimal getPorcentajeOtrosImpuestos() {
        return porcentajeOtrosImpuestos;
    }

    public void setPorcentajeOtrosImpuestos(BigDecimal porcentajeOtrosImpuestos) {
        this.porcentajeOtrosImpuestos = porcentajeOtrosImpuestos;
    }
}
