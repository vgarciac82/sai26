package com.axtel.egresos.viaticos;

import java.math.BigDecimal;
import java.util.Base64;

public class Transporte {

    private int idComision;

    private int idTransporte;

    private int idTipo;

    private String origen;

    private BigDecimal monto;

    private long km;

    public int getIdTipo() {
        return idTipo;
    }

    public void setIdTipo(int idTipo) {
        this.idTipo = idTipo;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public long getKm() {
        return km;
    }

    public void setKm(long km) {
        this.km = km;
    }

    public int getIdTransporte() {
        return idTransporte;
    }

    public void setIdTransporte(int idTransporte) {
        this.idTransporte = idTransporte;
    }

    @Override
    public String toString() {
        return "TransporteLocal [idTransporte=" + idTransporte + ", idTipo=" + idTipo + ", origen=" + origen + ", monto=" + monto + ", km=" + km + ", idComision=" + idComision + "]";
    }

    public int getIdComision() {
        return idComision;
    }

    public void setIdComision(int idComision) {
        this.idComision = idComision;
    }
}
