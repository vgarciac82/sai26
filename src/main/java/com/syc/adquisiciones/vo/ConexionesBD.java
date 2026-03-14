package com.syc.adquisiciones.vo;

import java.util.Base64;

public class ConexionesBD {

    private String servidor;

    private String puerto;

    private String nombreBD;

    private String usuarioBD;

    private String passBD;

    private String ejercicioFiscal;

    private String cActivo;

    public String getPuerto() {
        return puerto;
    }

    public void setPuerto(String puerto) {
        this.puerto = puerto;
    }

    public String getServidor() {
        return servidor;
    }

    public void setServidor(String servidor) {
        this.servidor = servidor;
    }

    public String getNombreBD() {
        return nombreBD;
    }

    public void setNombreBD(String nombreBD) {
        this.nombreBD = nombreBD;
    }

    public String getUsuarioBD() {
        return usuarioBD;
    }

    public void setUsuarioBD(String usuarioBD) {
        this.usuarioBD = usuarioBD;
    }

    public String getPassBD() {
        return passBD;
    }

    public void setPassBD(String passBD) {
        this.passBD = passBD;
    }

    public String getEjercicioFiscal() {
        return ejercicioFiscal;
    }

    public void setEjercicioFiscal(String ejercicioFiscal) {
        this.ejercicioFiscal = ejercicioFiscal;
    }

    public String getcActivo() {
        return cActivo;
    }

    public void setcActivo(String cActivo) {
        this.cActivo = cActivo;
    }
}
