package com.syc.fortimax.core;

import java.io.File;
import java.util.Base64;

public class Volumen {

    private String unidad = null;

    private String ruta_base = null;

    private String ruta_directorio = null;

    private String volumen = null;

    private int estado_unidad = 1;

    private String tipo_dispositivo = null;

    private String capacidad = "1";

    private String tipo_volumen = "0";

    public Volumen(String volumen) {
        this.volumen = volumen;
    }

    public Volumen(String unidad, String ruta_base, String ruta_directorio, String volumen) {
        this(volumen);
        this.unidad = unidad;
        this.ruta_base = ruta_base;
        setRutaDirectorio(ruta_directorio);
    }

    public Volumen(String unidad, String ruta_base, String ruta_directorio, String volumen, int estado_unidad, String tipo_dispositivo, String capacidad, String tipo_volumen) {
        this(unidad, ruta_base, ruta_directorio, volumen);
        this.estado_unidad = estado_unidad;
        this.tipo_dispositivo = tipo_dispositivo;
        this.capacidad = capacidad;
        this.tipo_volumen = tipo_volumen;
    }

    public String getUnidad() {
        return unidad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

    public String getRutaBase() {
        return ruta_base;
    }

    public void setRutaBase(String ruta_base) {
        this.ruta_base = ruta_base;
    }

    public String getRutaDirectorio() {
        return ruta_directorio;
    }

    public void setRutaDirectorio(String ruta_directorio) {
        // Se valida que no se incluya el volumen en la ruta directorio
        File f = new File(ruta_directorio);
        this.ruta_directorio = (f.getName().equals(getVolumen())) ? f.getParent() + File.separator : ruta_directorio;
    }

    public String getVolumen() {
        return volumen;
    }

    public void setVolumen(String volumen) {
        this.volumen = volumen;
    }

    public int getEstadoUnidad() {
        return estado_unidad;
    }

    public void setEstadoUnidad(int estado_unidad) {
        this.estado_unidad = estado_unidad;
    }

    public String getTipoDispositivo() {
        return tipo_dispositivo;
    }

    public void setTipoDispositivo(String tipo_dispositivo) {
        this.tipo_dispositivo = tipo_dispositivo;
    }

    public String getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(String capacidad) {
        this.capacidad = capacidad;
    }

    public String getTipoVolumen() {
        return tipo_volumen;
    }

    public void setTipoVolumen(String tipo_volumen) {
        this.tipo_volumen = tipo_volumen;
    }
}
