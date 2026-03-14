package com.syc.egresos.core;

import java.math.BigDecimal;
import java.util.Base64;

public class Amortizacion {

    private int folioPago;

    private BigDecimal montoAmortizacion;

    private BigDecimal montoAnticipo;

    private BigDecimal porcentajeAmortizacion;

    private String tipoPago;

    public int getFolioPago() {
        return folioPago;
    }

    public BigDecimal getMontoAmortizacion() {
        return montoAmortizacion;
    }

    public BigDecimal getMontoAnticipo() {
        return montoAnticipo;
    }

    public BigDecimal getPorcentajeAmortizacion() {
        return porcentajeAmortizacion;
    }

    public String getTipoPago() {
        return tipoPago;
    }

    public void setFolioPago(int folioPago) {
        this.folioPago = folioPago;
    }

    public void setMontoAmortizacion(BigDecimal montoAmortizacion) {
        this.montoAmortizacion = montoAmortizacion;
    }

    public void setMontoAnticipo(BigDecimal montoAnticipo) {
        this.montoAnticipo = montoAnticipo;
    }

    public void setPorcentajeAmortizacion(BigDecimal porcentajeAmortizacion) {
        this.porcentajeAmortizacion = porcentajeAmortizacion;
    }

    public void setTipoPago(String tipoPago) {
        this.tipoPago = tipoPago;
    }
    //200911001931-
}
