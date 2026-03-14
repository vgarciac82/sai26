package com.axtel.egresos.compromiso;

import java.math.BigDecimal;
import java.util.Base64;

public class CalendarioDetalle {

    private int mes;

    private BigDecimal base;

    private BigDecimal captura;

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public BigDecimal getBase() {
        return base;
    }

    public void setBase(BigDecimal base) {
        this.base = base;
    }

    public BigDecimal getCaptura() {
        return captura;
    }

    public void setCaptura(BigDecimal captura) {
        this.captura = captura;
    }
}
