package com.syc.contable;

import java.util.ArrayList;
import java.util.List;
import java.util.Base64;

public class AccountingEvent {

    private String evento;

    private String cuenta;

    private String tipoCuenta;

    private String componente;

    private List<String> detalle;

    public AccountingEvent(String evento, String cuenta, String tipoCuenta, String componente) {
        this(evento, cuenta, tipoCuenta, componente, new ArrayList<String>());
    }

    public AccountingEvent(String evento, String cuenta, String tipoCuenta, String componente, List<String> detalle) {
        this.evento = evento;
        this.cuenta = cuenta;
        this.tipoCuenta = tipoCuenta;
        this.componente = componente;
        this.detalle = detalle;
    }

    public String getEvento() {
        return evento;
    }

    public void setEvento(String evento) {
        this.evento = evento;
    }

    public String getCuenta() {
        return cuenta;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    public String getComponente() {
        return componente;
    }

    public void setComponente(String componente) {
        this.componente = componente;
    }

    public List<String> getDetalle() {
        return detalle;
    }

    public boolean esCargo() {
        return "C".equalsIgnoreCase(tipoCuenta);
    }

    public void setTipoCuenta(String tipoCuenta) {
        this.tipoCuenta = tipoCuenta;
    }

    public String getTipoCuenta() {
        return tipoCuenta;
    }
}
