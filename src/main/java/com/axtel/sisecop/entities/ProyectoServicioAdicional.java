package com.axtel.sisecop.entities;

import java.io.Serializable;
import java.util.Base64;

public class ProyectoServicioAdicional implements Serializable {

    private static final long serialVersionUID = 7684801080851906020L;

    private String servicioAdicionalArchivo;

    private int servicioAdicionalId;

    private String servicioAdicionalRuta;

    public String getServicioAdicionalArchivo() {
        return servicioAdicionalArchivo;
    }

    public int getServicioAdicionalId() {
        return servicioAdicionalId;
    }

    public String getServicioAdicionalRuta() {
        return servicioAdicionalRuta;
    }

    public void setServicioAdicionalArchivo(String servicioAdicionalArchivo) {
        this.servicioAdicionalArchivo = servicioAdicionalArchivo;
    }

    public void setServicioAdicionalId(int servicioAdicionalId) {
        this.servicioAdicionalId = servicioAdicionalId;
    }

    public void setServicioAdicionalRuta(String servicioAdicionalRuta) {
        this.servicioAdicionalRuta = servicioAdicionalRuta;
    }

    @Override
    public String toString() {
        return "ProyectoServicioAdicional [servicioAdicionalId=" + servicioAdicionalId + ", servicioAdicionalArchivo=" + servicioAdicionalArchivo + ", servicioAdicionalRuta=" + servicioAdicionalRuta + "]";
    }
}
