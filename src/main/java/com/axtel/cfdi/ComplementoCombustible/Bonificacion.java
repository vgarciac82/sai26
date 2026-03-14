package com.axtel.cfdi.ComplementoCombustible;

import java.math.BigDecimal;
import java.util.Base64;

/**
 * @author vicente.garcia
 */
public class Bonificacion extends CargoECC {

    /**
     */
    public Bonificacion() {
        super();
    }

    /**
     * @param importe
     * @param impuestos
     */
    public Bonificacion(BigDecimal importe, BigDecimal impuestos) {
        super(importe, impuestos, new BigDecimal(0.0));
        setTotal(getImporte().add(getTraslado()));
    }

    private BigDecimal total;

    /**
     * @return the total
     */
    public BigDecimal getTotal() {
        return total;
    }

    /**
     * @param total
     *            the total to set
     */
    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    /**
     * @param importe
     * @param impuestos
     * @param descripcionCargo
     */
    public Bonificacion(BigDecimal importe, BigDecimal impuestos, String descripcionCargo) {
        this(importe, impuestos);
        setDescripcionCargo(descripcionCargo);
    }
}
