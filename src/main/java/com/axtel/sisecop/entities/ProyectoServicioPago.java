package com.axtel.sisecop.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Base64;

public class ProyectoServicioPago implements Serializable {

    private static final long serialVersionUID = 5371864063552135926L;

    private ProyectoMes mesPago;

    private int servicioPagoAnio;

    private BigDecimal servicioPagoCantidad;

    private int servicioPagoId;

    public ProyectoMes getMesPago() {
        return mesPago;
    }

    public int getServicioPagoAnio() {
        return servicioPagoAnio;
    }

    public BigDecimal getServicioPagoCantidad() {
        return servicioPagoCantidad;
    }

    public int getServicioPagoId() {
        return servicioPagoId;
    }

    public void setMesPago(ProyectoMes mesPago) {
        this.mesPago = mesPago;
    }

    public void setServicioPagoAnio(int servicioPagoAnio) {
        this.servicioPagoAnio = servicioPagoAnio;
    }

    public void setServicioPagoCantidad(BigDecimal servicioPagoCantidad) {
        this.servicioPagoCantidad = servicioPagoCantidad;
    }

    public void setServicioPagoId(int servicioPagoId) {
        this.servicioPagoId = servicioPagoId;
    }

    @Override
    public String toString() {
        return "ProyectoServicioPago [servicioPagoId=" + servicioPagoId + ", servicioPagoAnio=" + servicioPagoAnio + ", servicioPagoCantidad=" + servicioPagoCantidad + ", mesPago=" + mesPago + "]";
    }
}
