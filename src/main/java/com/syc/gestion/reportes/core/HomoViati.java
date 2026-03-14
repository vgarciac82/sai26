package com.syc.gestion.reportes.core;

import java.io.Serializable;
import java.util.Base64;

public class HomoViati implements Serializable {

    private final static long serialVersionUID = 1;

    private String no_oficio;

    private String fecha;

    private String area;

    public String getNo_oficio() {
        return no_oficio;
    }

    public void setNo_oficio(String noOficio) {
        no_oficio = noOficio;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }
}
