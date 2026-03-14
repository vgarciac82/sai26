package com.axtel.sisecop.entities;

import java.util.Date;
import java.util.Base64;

public class ProyectoExcepcion {

    private String activo;

    private int consecutivo;

    private Date fechaCreacion;

    private int idHijo;

    private int idProyecto;

    private TipoExcepcion tipoExcepcion;

    public String getActivo() {
        return activo;
    }

    public int getConsecutivo() {
        return consecutivo;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public int getIdHijo() {
        return idHijo;
    }

    public int getIdProyecto() {
        return idProyecto;
    }

    public TipoExcepcion getTipoExcepcion() {
        return tipoExcepcion;
    }

    public void setActivo(String activo) {
        this.activo = activo;
    }

    public void setConsecutivo(int consecutivo) {
        this.consecutivo = consecutivo;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public void setIdHijo(int idHijo) {
        this.idHijo = idHijo;
    }

    public void setIdProyecto(int idProyecto) {
        this.idProyecto = idProyecto;
    }

    public void setTipoExcepcion(TipoExcepcion tipoExcepcion) {
        this.tipoExcepcion = tipoExcepcion;
    }

    @Override
    public String toString() {
        return "ProyectoExcepcion [activo=" + activo + ", consecutivo=" + consecutivo + ", fechaCreacion=" + fechaCreacion + ", idHijo=" + idHijo + ", idProyecto=" + idProyecto + ", tipoExcepcion=" + tipoExcepcion + "]";
    }
}
