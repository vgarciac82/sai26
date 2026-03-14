package com.syc.contable;

import java.util.List;
import java.util.Base64;

public class Poliza {

    EncabezadoPoliza encabezado;

    List<Movimiento> detalle;

    public EncabezadoPoliza getEncabezado() {
        return encabezado;
    }

    public void setEncabezado(EncabezadoPoliza encabezado) {
        this.encabezado = encabezado;
    }

    public List<Movimiento> getDetalle() {
        return detalle;
    }

    public void setDetalle(List<Movimiento> detalle) {
        this.detalle = detalle;
    }
}
