package com.syc.egresos.core;

import java.math.BigDecimal;
import java.util.Base64;

public class AmortizacionDetalle {

    private String EP;

    private int folioPago;

    private BigDecimal importeAmortizacion;

    private BigDecimal importeBruto;

    private BigDecimal importeImpuestos;

    private int mesAmortiza;

    private BigDecimal porcentajeAmortizacion;

    private BigDecimal porcentajeIVA;

    private String tipoPago;

    public BigDecimal getPorcentajeIVA() {
        return porcentajeIVA;
    }

    public void setPorcentajeIVA(BigDecimal porcentajeIVA) {
        this.porcentajeIVA = porcentajeIVA;
    }

    public BigDecimal getImporteBruto() {
        return importeBruto;
    }

    public void setImporteBruto(BigDecimal importeBruto) {
        this.importeBruto = importeBruto;
    }

    public BigDecimal getImporteImpuestos() {
        return importeImpuestos;
    }

    public void setImporteImpuestos(BigDecimal importeImpuestos) {
        this.importeImpuestos = importeImpuestos;
    }

    public String getEP() {
        return EP;
    }

    public int getFolioPago() {
        return folioPago;
    }

    public BigDecimal getImporteAmortizacion() {
        return importeAmortizacion;
    }

    public int getMesAmortiza() {
        return mesAmortiza;
    }

    public BigDecimal getPorcentajeAmortizacion() {
        return porcentajeAmortizacion;
    }

    public String getTipoPago() {
        return tipoPago;
    }

    public void setEP(String eP) {
        EP = eP;
    }

    public void setFolioPago(int folioPago) {
        this.folioPago = folioPago;
    }

    public void setImporteAmortizacion(BigDecimal importeAmortizacion) {
        this.importeAmortizacion = importeAmortizacion;
    }

    public void setMesAmortiza(int mesAmortiza) {
        this.mesAmortiza = mesAmortiza;
    }

    public void setPorcentajeAmortizacion(BigDecimal porcentajeAmortizacion) {
        this.porcentajeAmortizacion = porcentajeAmortizacion;
    }

    public void setTipoPago(String tipoPago) {
        this.tipoPago = tipoPago;
    }

    @Override
    public String toString() {
        return "AmortizacionDetalle [EP=" + EP + ", folioPago=" + folioPago + ", importeAmortizacion=" + importeAmortizacion + ", importeBruto=" + importeBruto + ", importeImpuestos=" + importeImpuestos + ", mesAmortiza=" + mesAmortiza + ", porcentajeAmortizacion=" + porcentajeAmortizacion + ", porcentajeIVA=" + porcentajeIVA + ", tipoPago=" + tipoPago + "]";
    }
}
