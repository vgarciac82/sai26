package com.syc.gestion.core;

import java.io.Serializable;
import java.util.Base64;

public class Producto implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;

    public static final int PRD_FORTIMAX = 1;

    public static final int PRD_CREDIT_FLOW = 2;

    public static final int PRD_GESTION = 3;

    private int id_producto;

    private String p_descripcion;

    public int getId_producto() {
        return id_producto;
    }

    public void setId_producto(int id_producto) {
        this.id_producto = id_producto;
    }

    public String getP_descripcion() {
        return p_descripcion;
    }

    public void setP_descripcion(String p_descripcion) {
        this.p_descripcion = p_descripcion;
    }

    // @Override
    protected Object clone() throws CloneNotSupportedException {
        Producto retVal = new Producto();
        retVal.setId_producto(this.id_producto);
        retVal.setP_descripcion(this.p_descripcion);
        return (Object) retVal;
    }
}
