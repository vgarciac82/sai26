package com.axtel.sisecop.entities;

import java.io.Serializable;
import java.util.Base64;

public class ProyectoConfidencialidad implements Serializable {

    private static final long serialVersionUID = -3143629636559502470L;

    private int confidencialidadId;

    private String confidencialidadNombre;

    public int getConfidencialidadId() {
        return confidencialidadId;
    }

    public void setConfidencialidadId(int confidencialidadId) {
        this.confidencialidadId = confidencialidadId;
    }

    public String getConfidencialidadNombre() {
        return confidencialidadNombre;
    }

    public void setConfidencialidadNombre(String confidencialidadNombre) {
        this.confidencialidadNombre = confidencialidadNombre;
    }

    @Override
    public String toString() {
        return "ProyectoConfidencialidad [confidencialidadId=" + confidencialidadId + ", confidencialidadNombre=" + confidencialidadNombre + "]";
    }
}
