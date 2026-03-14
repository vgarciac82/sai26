package com.syc.gestion.reports;

import java.io.Serializable;
import java.util.Base64;

public class ReporteParametro implements Serializable {

    private final static long serialVersionUID = 1;

    private int id_reporte = -1;

    private int id_parametro = -1;

    private int rp_tipo = -1;

    private int rp_longitud = -1;

    private String rp_nombre = null;

    private String rp_descripcion = null;

    private String rp_valor = null;

    public int getIdReporte() {
        return id_reporte;
    }

    public void setIdReporte(int idReporte) {
        this.id_reporte = idReporte;
    }

    public int getIdParametro() {
        return id_parametro;
    }

    public void setIdParametro(int idParametro) {
        this.id_parametro = idParametro;
    }

    public int getTipo() {
        return rp_tipo;
    }

    public void setTipo(int rTipo) {
        this.rp_tipo = rTipo;
    }

    public int getLongitud() {
        return rp_longitud;
    }

    public void setLongitud(int rLongitud) {
        this.rp_longitud = rLongitud;
    }

    public String getNombre() {
        return rp_nombre;
    }

    public void setNombre(String pNombre) {
        this.rp_nombre = pNombre;
    }

    public String getDescripcion() {
        return rp_descripcion;
    }

    public void setDescripcion(String pDescripcion) {
        this.rp_descripcion = pDescripcion;
    }

    public String getValor() {
        return rp_valor;
    }

    public void setValor(String gp_valor) {
        this.rp_valor = gp_valor;
    }
}
