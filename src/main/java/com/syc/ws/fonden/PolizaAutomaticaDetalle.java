package com.syc.ws.fonden;

import java.math.BigDecimal;
import java.util.Base64;

public class PolizaAutomaticaDetalle {

    private String cuentaContable;

    private String evento;

    private BigDecimal importeMovimiento;

    private String partida;

    private String subcuenta;

    public PolizaAutomaticaDetalle() {
    }

    public PolizaAutomaticaDetalle(String cuentaContable, String evento, BigDecimal importeMovimiento, String subcuenta) {
        super();
        this.cuentaContable = cuentaContable;
        this.evento = evento;
        this.importeMovimiento = importeMovimiento;
        this.subcuenta = subcuenta;
    }

    public String getCuentaContable() {
        return cuentaContable;
    }

    public String getEvento() {
        return evento;
    }

    public BigDecimal getImporteMovimiento() {
        return importeMovimiento;
    }

    public String getPartida() {
        return this.partida;
    }

    public String getSubcuenta() {
        return subcuenta;
    }

    public void setCuentaContable(String cuentaContable) {
        this.cuentaContable = cuentaContable;
    }

    public void setEvento(String evento) {
        this.evento = evento;
    }

    public void setImporteMovimiento(BigDecimal importeMovimiento) {
        this.importeMovimiento = importeMovimiento;
    }

    public void setPartida(String partida) {
        this.partida = partida;
    }

    public void setSubcuenta(String subcuenta) {
        this.subcuenta = subcuenta;
    }

    @Override
    public String toString() {
        return "PolizaAutomaticaDetalle [cuentaContable=" + cuentaContable + ", evento=" + evento + ", importeMovimiento=" + importeMovimiento + ", partida=" + partida + ", subcuenta=" + subcuenta + "]";
    }
}
