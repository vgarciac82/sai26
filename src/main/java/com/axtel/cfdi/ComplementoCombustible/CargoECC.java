package com.axtel.cfdi.ComplementoCombustible;

import java.math.BigDecimal;
import java.util.Base64;

public class CargoECC extends ConceptoAdenda {

    private String descripcionCargo;

    private BigDecimal descuento = new BigDecimal(0.0);

    public CargoECC() {
        super();
    }

    public CargoECC(BigDecimal importe, BigDecimal impuestos, BigDecimal descuento) {
        super(importe, impuestos);
        this.descuento = descuento;
    }

    public CargoECC(BigDecimal importe, BigDecimal impuestos, BigDecimal descuento, String descripcionCargo) {
        super(importe, impuestos);
        this.descripcionCargo = descripcionCargo;
        this.descuento = descuento;
    }

    public String getDescripcionCargo() {
        return descripcionCargo;
    }

    public void setDescripcionCargo(String descripcionCargo) {
        this.descripcionCargo = descripcionCargo;
    }

    public BigDecimal getDescuento() {
        return descuento;
    }

    public void setDescuento(BigDecimal descuento) {
        this.descuento = descuento;
    }
}
