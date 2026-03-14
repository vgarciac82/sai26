package com.syc.admin.servlet;

import java.util.Base64;

public class CatRemArea {

    private String cra_id_area = null;

    private String cra_descripcion = null;

    private String tipo = null;

    private int orden = -1;

    public String getCra_id_area() {
        return cra_id_area;
    }

    public void setCra_id_area(String cra_id_area) {
        this.cra_id_area = cra_id_area;
    }

    public String getCra_descripcion() {
        return cra_descripcion;
    }

    public void setCra_descripcion(String cra_descripcion) {
        this.cra_descripcion = cra_descripcion;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getOrden() {
        return orden;
    }

    public void setOrden(int orden) {
        this.orden = orden;
    }
}
