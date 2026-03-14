package com.axtel.sisecop.entities;

import java.io.Serializable;
import java.util.Base64;

public class ProyectoServicioTDR implements Serializable {

    private static final long serialVersionUID = -8107977818614116813L;

    private String tdrArchivo;

    private int tdrId;

    private String tdrRuta;

    public String getTdrArchivo() {
        return tdrArchivo;
    }

    public int getTdrId() {
        return tdrId;
    }

    public String getTdrRuta() {
        return tdrRuta;
    }

    public void setTdrArchivo(String tdrArchivo) {
        this.tdrArchivo = tdrArchivo;
    }

    public void setTdrId(int tdrId) {
        this.tdrId = tdrId;
    }

    public void setTdrRuta(String tdrRuta) {
        this.tdrRuta = tdrRuta;
    }

    @Override
    public String toString() {
        return "ProyectoServicioTDR [tdrId=" + tdrId + ", tdrArchivo=" + tdrArchivo + ", tdrRuta=" + tdrRuta + "]";
    }
}
