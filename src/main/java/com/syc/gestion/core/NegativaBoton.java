package com.syc.gestion.core;

import java.io.Serializable;
import java.io.Serializable;
import java.util.Base64;

public class NegativaBoton implements Serializable {

    private static final long serialVersionUID = -7740203864511747270L;

    private String r_nombre = null;

    private String modulo = null;

    private String pestana = null;

    private String boton = null;

    public NegativaBoton() {
        super();
    }

    public String getR_Nombre() {
        return r_nombre;
    }

    public void setId(String r_nombre) {
        this.r_nombre = r_nombre;
    }

    public String getModulo() {
        return modulo;
    }

    public void setModulo(String modulo) {
        this.modulo = modulo;
    }

    public String getPestana() {
        return pestana;
    }

    public void setPestana(String pestana) {
        this.pestana = pestana;
    }

    public String getBoton() {
        return boton;
    }

    public void setBoton(String boton) {
        this.boton = boton;
    }
}
