package com.axtel.sisecop.entities;

import java.io.Serializable;
import java.util.Base64;

public class ProyectoEstatus implements Serializable {

    private static final long serialVersionUID = 1L;

    private int estatusId;

    private String estatusNombre;

    public ProyectoEstatus() {
        super();
        this.estatusId = 7;
    }

    public ProyectoEstatus(int estatusId) {
        super();
        this.estatusId = estatusId;
    }

    public int getEstatusId() {
        return estatusId;
    }

    public void setEstatusId(int estatusId) {
        this.estatusId = estatusId;
    }

    public String getEstatusNombre() {
        return estatusNombre;
    }

    public void setEstatusNombre(String estatusNombre) {
        this.estatusNombre = estatusNombre;
    }

    @Override
    public String toString() {
        return "ProyectoEstatus [estatusId=" + estatusId + ", estatusNombre=" + estatusNombre + "]";
    }
}
