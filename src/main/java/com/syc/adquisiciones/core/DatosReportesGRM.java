package com.syc.adquisiciones.core;

import java.util.Base64;

public class DatosReportesGRM {

    String cNamePlantilla;

    String cFechaInicio;

    String cFechaFin;

    String cEjercicioAnterior;

    String cEjercicioActual;

    String cNameDB;

    String cMes;

    int nTipoReporte;

    public String getcNamePlantilla() {
        return cNamePlantilla;
    }

    public void setcNamePlantilla(String cNamePlantilla) {
        this.cNamePlantilla = cNamePlantilla;
    }

    public String getcFechaInicio() {
        return cFechaInicio;
    }

    public void setcFechaInicio(String cFechaInicio) {
        this.cFechaInicio = cFechaInicio;
    }

    public String getcFechaFin() {
        return cFechaFin;
    }

    public void setcFechaFin(String cFechaFin) {
        this.cFechaFin = cFechaFin;
    }

    public String getcEjercicioAnterior() {
        return cEjercicioAnterior;
    }

    public void setcEjercicioAnterior(String cEjercicioAnterior) {
        this.cEjercicioAnterior = cEjercicioAnterior;
    }

    public String getcEjercicioActual() {
        return cEjercicioActual;
    }

    public void setcEjercicioActual(String cEjercicioActual) {
        this.cEjercicioActual = cEjercicioActual;
    }

    public String getcNameDB() {
        return cNameDB;
    }

    public void setcNameDB(String cNameDB) {
        this.cNameDB = cNameDB;
    }

    public String getcMes() {
        return cMes;
    }

    public void setcMes(String cMes) {
        this.cMes = cMes;
    }

    public int getnTipoReporte() {
        return nTipoReporte;
    }

    public void setnTipoReporte(int nTipoReporte) {
        this.nTipoReporte = nTipoReporte;
    }
}
