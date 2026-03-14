package com.syc.reportes.core;

import java.util.Base64;

public class LibroBalancePagina {

    private String nombreConciliacion;

    private String nombreMesConciliacion;

    private String rutaConciliacion;

    public String getNombreConciliacion() {
        return nombreConciliacion;
    }

    public void setNombreConciliacion(String nombreConciliacion) {
        this.nombreConciliacion = nombreConciliacion;
    }

    public String getNombreMesConciliacion() {
        return nombreMesConciliacion;
    }

    public void setNombreMesConciliacion(String nombreMesConciliacion) {
        this.nombreMesConciliacion = nombreMesConciliacion;
    }

    public String getRutaConciliacion() {
        return rutaConciliacion;
    }

    public void setRutaConciliacion(String rutaConciliacion) {
        this.rutaConciliacion = rutaConciliacion;
    }
}
