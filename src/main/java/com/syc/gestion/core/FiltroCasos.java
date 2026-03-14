package com.syc.gestion.core;

import java.util.Base64;

public class FiltroCasos {

    private String tipoAsunto = null;

    private String remitenteInternoLogin = null;

    private String remitenteInternoNombre = null;

    private String remitenteInternoPuesto = null;

    private String remitenteInternoArea = null;

    private String remitenteExternoId = null;

    private String remitenteExternoNombre = null;

    private String remitenteExternoProcedencia = null;

    private String remitenteExternoCargo = null;

    private String remitenteExternoEstado = null;

    private String remitenteExternoMunicipio = null;

    private String remitenteExternoLocalidad = null;

    private String fechaInicial = null;

    private String fechaFinal = null;

    private String palabrasClave = null;

    public FiltroCasos() {
        super();
    }

    public String getTipoAsunto() {
        return tipoAsunto;
    }

    public void setTipoAsunto(String tipoAsunto) {
        this.tipoAsunto = tipoAsunto.trim();
    }

    public String getRemitenteInternoLogin() {
        return remitenteInternoLogin;
    }

    public void setRemitenteInternoLogin(String remitenteInternoLogin) {
        this.remitenteInternoLogin = remitenteInternoLogin.trim();
    }

    public String getRemitenteInternoNombre() {
        return remitenteInternoNombre;
    }

    public void setRemitenteInternoNombre(String remitenteInternoNombre) {
        this.remitenteInternoNombre = remitenteInternoNombre.trim();
    }

    public String getRemitenteInternoPuesto() {
        return remitenteInternoPuesto;
    }

    public void setRemitenteInternoPuesto(String remitenteInternoPuesto) {
        this.remitenteInternoPuesto = remitenteInternoPuesto.trim();
    }

    public String getRemitenteInternoArea() {
        return remitenteInternoArea;
    }

    public void setRemitenteInternoArea(String remitenteInternoArea) {
        this.remitenteInternoArea = remitenteInternoArea.trim();
    }

    public String getRemitenteExternoId() {
        return remitenteExternoId;
    }

    public void setRemitenteExternoId(String remitenteExternoId) {
        this.remitenteExternoId = remitenteExternoId.trim();
    }

    public String getRemitenteExternoNombre() {
        return remitenteExternoNombre;
    }

    public void setRemitenteExternoNombre(String remitenteExternoNombre) {
        this.remitenteExternoNombre = remitenteExternoNombre.trim();
    }

    public String getRemitenteExternoProcedencia() {
        return remitenteExternoProcedencia;
    }

    public void setRemitenteExternoProcedencia(String remitenteExternoProcedencia) {
        this.remitenteExternoProcedencia = remitenteExternoProcedencia.trim();
    }

    public String getRemitenteExternoCargo() {
        return remitenteExternoCargo;
    }

    public void setRemitenteExternoCargo(String remitenteExternoCargo) {
        this.remitenteExternoCargo = remitenteExternoCargo.trim();
    }

    public String getRemitenteExternoEstado() {
        return remitenteExternoEstado;
    }

    public void setRemitenteExternoEstado(String remitenteExternoEstado) {
        this.remitenteExternoEstado = remitenteExternoEstado.trim();
    }

    public String getRemitenteExternoMunicipio() {
        return remitenteExternoMunicipio;
    }

    public void setRemitenteExternoMunicipio(String remitenteExternoMunicipio) {
        this.remitenteExternoMunicipio = remitenteExternoMunicipio.trim();
    }

    public String getRemitenteExternoLocalidad() {
        return remitenteExternoLocalidad;
    }

    public void setRemitenteExternoLocalidad(String remitenteExternoLocalidad) {
        this.remitenteExternoLocalidad = remitenteExternoLocalidad.trim();
    }

    public String getFechaInicial() {
        return fechaInicial;
    }

    public void setFechaInicial(String fechaInicial) {
        this.fechaInicial = fechaInicial.trim();
    }

    public String getFechaFinal() {
        return fechaFinal;
    }

    public void setFechaFinal(String fechaFinal) {
        this.fechaFinal = fechaFinal.trim();
    }

    public String getPalabrasClave() {
        return palabrasClave;
    }

    public void setPalabrasClave(String palabrasClave) {
        this.palabrasClave = palabrasClave.trim();
    }
}
