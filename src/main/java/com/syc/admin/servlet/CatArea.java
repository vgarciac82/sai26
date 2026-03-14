package com.syc.admin.servlet;

import java.util.Base64;

public class CatArea {

    private String id_area;

    private String d_descripcion;

    private String tipo_area;

    private String prefijo_folio;

    public String getId_area() {
        return id_area;
    }

    public void setId_area(String id_area) {
        this.id_area = id_area;
    }

    public String getD_descripcion() {
        return d_descripcion;
    }

    public void setD_descripcion(String d_descripcion) {
        this.d_descripcion = d_descripcion;
    }

    public String getTipo_area() {
        return tipo_area;
    }

    public void setTipo_area(String tipo_area) {
        this.tipo_area = tipo_area;
    }

    public String getPrefijo_folio() {
        return prefijo_folio;
    }

    public void setPrefijo_folio(String prefijo_folio) {
        this.prefijo_folio = prefijo_folio;
    }
}
