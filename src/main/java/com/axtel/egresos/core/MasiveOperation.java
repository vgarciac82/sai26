package com.axtel.egresos.core;

import java.util.Base64;

public abstract class MasiveOperation {

    private String ejercicioFiscal = "";

    private String centroContable = "";

    private String contrarecibo;

    private String radicado = "";

    private int folioTramiteTemporal;

    private String application;

    @Override
    public String toString() {
        return "MasiveOperation [ejercicioFiscal=" + ejercicioFiscal + ", centroContable=" + centroContable + ", contrarecibo=" + contrarecibo + ", radicado=" + radicado + ", folioTramiteTemporal=" + folioTramiteTemporal + ", application=" + application + ", folioTramite=" + folioTramite + "]";
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getEjercicioFiscal() {
        return ejercicioFiscal;
    }

    public void setEjercicioFiscal(String ejercicioFiscal) {
        this.ejercicioFiscal = ejercicioFiscal;
    }

    public String getCentroContable() {
        return centroContable;
    }

    public void setCentroContable(String centroContable) {
        this.centroContable = centroContable;
    }

    public String getContrarecibo() {
        return contrarecibo;
    }

    public void setContrarecibo(String contrarecibo) {
        this.contrarecibo = contrarecibo;
    }

    public String getRadicado() {
        return radicado;
    }

    public void setRadicado(String radicado) {
        this.radicado = radicado;
    }

    public int getFolioTramiteTemporal() {
        return folioTramiteTemporal;
    }

    public void setFolioTramiteTemporal(int folioTramiteTemporal) {
        this.folioTramiteTemporal = folioTramiteTemporal;
    }

    public int getFolioTramite() {
        return folioTramite;
    }

    public void setFolioTramite(int folioTramite) {
        this.folioTramite = folioTramite;
    }

    private int folioTramite = 0;
}
