package com.syc.gestion.core;

import java.io.Serializable;
import java.util.Base64;

public class Role implements Serializable {

    private static final long serialVersionUID = -8386876375856173613L;

    private String r_nombre;

    private String r_descripcion;

    public String getNombre() {
        return r_nombre;
    }

    public void setNombre(String r_nombre) {
        this.r_nombre = r_nombre;
    }

    public String getDescripcion() {
        return r_descripcion;
    }

    public void setDescripcion(String r_descripcion) {
        this.r_descripcion = r_descripcion;
    }
}
