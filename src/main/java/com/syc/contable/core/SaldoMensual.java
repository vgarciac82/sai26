package com.syc.contable.core;

import java.math.BigDecimal;
import java.util.Base64;

public class SaldoMensual {

    private String cuenta;

    private String ep;

    private String idTipoConcepto;

    private String idTipoMovimiento;

    private int mes;

    private BigDecimal montoSaldo;

    public SaldoMensual(String cuenta, BigDecimal montoSaldo, int mes) {
        super();
        this.cuenta = cuenta;
        this.montoSaldo = montoSaldo;
        this.mes = mes;
    }

    public SaldoMensual(String ep, int mes, BigDecimal montoSaldo) {
        super();
        this.ep = ep;
        this.mes = mes;
        this.montoSaldo = montoSaldo;
    }

    public SaldoMensual(String ep, int mesPresupuesto, BigDecimal montoRetencion, String idTipoConcepto, String idTipoMovimiento) {
        this(ep, mesPresupuesto, montoRetencion);
        this.setIdTipoConcepto(idTipoConcepto);
        this.setIdTipoMovimiento(idTipoMovimiento);
    }

    public String getCuenta() {
        return cuenta;
    }

    public String getEp() {
        return ep;
    }

    public String getIdTipoConcepto() {
        return idTipoConcepto;
    }

    public String getIdTipoMovimiento() {
        return idTipoMovimiento;
    }

    public int getMes() {
        return mes;
    }

    public BigDecimal getMontoSaldo() {
        return montoSaldo;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    public void setEp(String ep) {
        this.ep = ep;
    }

    public void setIdTipoConcepto(String idTipoConcepto) {
        this.idTipoConcepto = idTipoConcepto;
    }

    public void setIdTipoMovimiento(String idTipoMovimiento) {
        this.idTipoMovimiento = idTipoMovimiento;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public void setMontoSaldo(BigDecimal montoSaldo) {
        this.montoSaldo = montoSaldo;
    }

    @Override
    public String toString() {
        return "SaldoMensual [cuenta=" + cuenta + ", ep=" + ep + ", idTipoConcepto=" + idTipoConcepto + ", idTipoMovimiento=" + idTipoMovimiento + ", mes=" + mes + ", montoSaldo=" + montoSaldo + "]";
    }
}
