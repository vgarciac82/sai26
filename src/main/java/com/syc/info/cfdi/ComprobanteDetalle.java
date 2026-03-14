package com.syc.info.cfdi;

import java.math.BigDecimal;
import java.util.Base64;

public class ComprobanteDetalle {

    private int idFacturaDet;

    private int idFactura;

    private BigDecimal cantidad;

    private String unidad;

    private String numIdentif;

    private String descripcion;

    private BigDecimal valorUnitario;

    private BigDecimal mImporte;

    /**
     * @return the idFacturaDet
     */
    public int getIdFacturaDet() {
        return idFacturaDet;
    }

    /**
     * @param idFacturaDet
     *            the idFacturaDet to set
     */
    public void setIdFacturaDet(int idFacturaDet) {
        this.idFacturaDet = idFacturaDet;
    }

    /**
     * @return the idFactura
     */
    public int getIdFactura() {
        return idFactura;
    }

    /**
     * @param idFactura
     *            the idFactura to set
     */
    public void setIdFactura(int idFactura) {
        this.idFactura = idFactura;
    }

    /**
     * @return the cantidad
     */
    public BigDecimal getCantidad() {
        return cantidad;
    }

    /**
     * @param cantidad
     *            the cantidad to set
     */
    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }

    /**
     * @return the unidad
     */
    public String getUnidad() {
        return unidad;
    }

    /**
     * @param unidad
     *            the unidad to set
     */
    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

    /**
     * @return the numIdentif
     */
    public String getNumIdentif() {
        return numIdentif;
    }

    /**
     * @param numIdentif
     *            the numIdentif to set
     */
    public void setNumIdentif(String numIdentif) {
        this.numIdentif = numIdentif;
    }

    /**
     * @return the descripcion
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * @param descripcion
     *            the descripcion to set
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * @return the valorUnitario
     */
    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    /**
     * @param valorUnitario
     *            the valorUnitario to set
     */
    public void setValorUnitario(BigDecimal valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    /**
     * @return the mImporte
     */
    public BigDecimal getmImporte() {
        return mImporte;
    }

    /**
     * @param mImporte
     *            the mImporte to set
     */
    public void setmImporte(BigDecimal mImporte) {
        this.mImporte = mImporte;
    }
}
