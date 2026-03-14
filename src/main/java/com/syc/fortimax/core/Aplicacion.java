package com.syc.fortimax.core;

import java.util.Hashtable;
import java.util.Map;
import java.util.Base64;

public class Aplicacion {

    private String tbl_aplicacion;

    private String titulo_aplicacion;

    private String descripcion;

    private Map camposDescripcion = new Hashtable();

    public String getTableAplicacion() {
        return tbl_aplicacion;
    }

    public void setTableAplicacion(String tbl_aplicacion) {
        this.tbl_aplicacion = tbl_aplicacion;
    }

    public String getTituloAplicacion() {
        return titulo_aplicacion;
    }

    public void setTituloAplicacion(String titulo_aplicacion) {
        this.titulo_aplicacion = titulo_aplicacion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Map getCamposDescripcion() {
        return camposDescripcion;
    }

    public Descripcion getCamposDescripcion(String name) {
        return (Descripcion) camposDescripcion.get(name);
    }

    public void setCamposDescripcion(Map descFields) {
        this.camposDescripcion = descFields;
    }
}
