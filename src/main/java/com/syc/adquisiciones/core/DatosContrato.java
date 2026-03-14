package com.syc.adquisiciones.core;

import java.util.ArrayList;
import java.util.Base64;

public class DatosContrato {

    String cTipoContrato;

    String cUE;

    String cIdContratoDef;

    int nEstatus;

    String cUsuarioCreacion;

    String cUsuarioCancela;

    String cEjercicio;

    int nIdConsecutivo;

    ArrayList<DatosContratoAmp> ampliacion;

    public String getcTipoContrato() {
        return cTipoContrato;
    }

    public void setcTipoContrato(String cTipoContrato) {
        this.cTipoContrato = cTipoContrato;
    }

    public String getcUE() {
        return cUE;
    }

    public void setcUE(String cUE) {
        this.cUE = cUE;
    }

    public String getcIdContratoDef() {
        return cIdContratoDef;
    }

    public void setcIdContratoDef(String cIdContratoDef) {
        this.cIdContratoDef = cIdContratoDef;
    }

    public int getnEstatus() {
        return nEstatus;
    }

    public void setnEstatus(int nEstatus) {
        this.nEstatus = nEstatus;
    }

    public String getcUsuarioCreacion() {
        return cUsuarioCreacion;
    }

    public void setcUsuarioCreacion(String cUsuarioCreacion) {
        this.cUsuarioCreacion = cUsuarioCreacion;
    }

    public String getcUsuarioCancela() {
        return cUsuarioCancela;
    }

    public void setcUsuarioCancela(String cUsuarioCancela) {
        this.cUsuarioCancela = cUsuarioCancela;
    }

    public ArrayList<DatosContratoAmp> getAmpliacion() {
        return ampliacion;
    }

    public void setAmpliacion(ArrayList<DatosContratoAmp> ampliacion) {
        this.ampliacion = ampliacion;
    }

    public String getcEjercicio() {
        return cEjercicio;
    }

    public void setcEjercicio(String cEjercicio) {
        this.cEjercicio = cEjercicio;
    }

    public int getnIdConsecutivo() {
        return nIdConsecutivo;
    }

    public void setnIdConsecutivo(int nIdConsecutivo) {
        this.nIdConsecutivo = nIdConsecutivo;
    }
}
