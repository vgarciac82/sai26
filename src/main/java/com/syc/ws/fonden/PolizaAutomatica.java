package com.syc.ws.fonden;

import java.math.BigDecimal;
import java.util.List;
import java.util.Base64;

public class PolizaAutomatica {

    public static final int ID_ESTATUS_ERROR = -1;

    public static final String ESTATUS_ERROR = "ERROR";

    public static final int ID_ESTATUS_OK = 1;

    public static final String ESTATUS_OK = "SUCCESS";

    private String centroContable;

    private String descripcion;

    private List<PolizaAutomaticaDetalle> detalle;

    private int mesCierre;

    private String tipoPoliza;

    private BigDecimal totalPoliza;

    private String unidadEjecutora;

    public PolizaAutomatica() {
    }

    public PolizaAutomatica(String centroContable, String descripcion, List<PolizaAutomaticaDetalle> detalle, int mesCierre, String tipoPoliza, BigDecimal totalPoliza, String unidadEjecutora) {
        super();
        this.centroContable = centroContable;
        this.descripcion = descripcion;
        this.detalle = detalle;
        this.mesCierre = mesCierre;
        this.tipoPoliza = tipoPoliza;
        this.totalPoliza = totalPoliza;
        this.unidadEjecutora = unidadEjecutora;
    }

    public String getCentroContable() {
        return centroContable;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public List<PolizaAutomaticaDetalle> getDetalle() {
        return detalle;
    }

    public int getMesCierre() {
        return mesCierre;
    }

    public String getTipoPoliza() {
        return tipoPoliza;
    }

    public BigDecimal getTotalPoliza() {
        return totalPoliza;
    }

    public String getUnidadEjecutora() {
        return unidadEjecutora;
    }

    public void setCentroContable(String centroContable) {
        this.centroContable = centroContable;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setDetalle(List<PolizaAutomaticaDetalle> detalle) {
        this.detalle = detalle;
    }

    public void setMesCierre(int mesCierre) {
        this.mesCierre = mesCierre;
    }

    public void setTipoPoliza(String tipoPoliza) {
        this.tipoPoliza = tipoPoliza;
    }

    public void setTotalPoliza(BigDecimal totalPoliza) {
        this.totalPoliza = totalPoliza;
    }

    public void setUnidadEjecutora(String unidadEjecutora) {
        this.unidadEjecutora = unidadEjecutora;
    }

    @Override
    public String toString() {
        return "PolizaAutomatica [centroContable=" + centroContable + ", descripcion=" + descripcion + ", detalle=" + detalle + ", mesCierre=" + mesCierre + ", tipoPoliza=" + tipoPoliza + ", totalPoliza=" + totalPoliza + ", unidadEjecutora=" + unidadEjecutora + "]";
    }
}
