package com.axtel.cfdi.ComplementoCombustible;

import java.math.BigDecimal;
import java.util.Base64;

/**
 * Super clase de los conceptos en una Adenda de Estado de Cuenta de
 * combustible.
 *
 * @author vicente.garcia
 */
public class ConceptoAdenda {

    /**
     * Importe del concepto
     */
    private BigDecimal importe = new BigDecimal(0.00);

    /**
     * Impuestos del concepto.
     */
    private BigDecimal traslado = new BigDecimal(0.00);

    /**
     * Impuestos Totales.
     */
    private BigDecimal total = new BigDecimal(0.00);

    /**
     * Construye un nuevo objeto con los valores en cero
     */
    public ConceptoAdenda() {
        super();
    }

    /**
     * @param importe
     * @param traslado
     */
    public ConceptoAdenda(BigDecimal importe, BigDecimal traslado) {
        super();
        this.importe = importe;
        this.traslado = traslado;
        setTotal(this.importe.add(this.traslado));
    }

    /**
     * @return the importe
     */
    public BigDecimal getImporte() {
        return importe;
    }

    /**
     * @return the traslado
     */
    public BigDecimal getTraslado() {
        return traslado;
    }

    /**
     * @param importe the importe to set
     */
    public void setImporte(BigDecimal importe) {
        this.importe = importe;
    }

    /**
     * @param traslado the traslado to set
     */
    public void setTraslado(BigDecimal traslado) {
        this.traslado = traslado;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "ConceptoAdenda [importe=" + importe + ", traslado=" + traslado + ", total=" + total + "]";
    }
}
