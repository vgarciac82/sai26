package com.syc.fortimax.core;

import java.util.Base64;

public class TipoDocumento {

    private String titulo_aplicacion;

    private int id_tipo_docto;

    private int prioridad;

    private String nombre_tipo_docto;

    private String descripcion;

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getIdTipoDocto() {
        return id_tipo_docto;
    }

    public void setIdTipoDocto(int id_tipo_docto) {
        this.id_tipo_docto = id_tipo_docto;
    }

    public String getNombreTipoDocto() {
        return nombre_tipo_docto;
    }

    public void setNombreTipoDocto(String nombre_tipo_docto) {
        this.nombre_tipo_docto = nombre_tipo_docto;
    }

    public int getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(int prioridad) {
        this.prioridad = prioridad;
    }

    public String getTituloAplicacion() {
        return titulo_aplicacion;
    }

    public void setTituloAplicacion(String titulo_aplicacion) {
        this.titulo_aplicacion = titulo_aplicacion;
    }
}
