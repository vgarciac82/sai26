package com.syc.contable.core;

import java.util.Base64;

public class ManualContableEncabezado {

    private String centroContable;

    private int folioManualContable;

    private String folioSAI;

    private int idGabinete;

    public ManualContableEncabezado(String centroContable, String cuentaBancaria, int folioManualContable, String folioSAI, int idGabinete) {
        super();
        this.centroContable = centroContable;
        this.folioManualContable = folioManualContable;
        this.folioSAI = folioSAI;
        this.idGabinete = idGabinete;
    }

    public String getCentroContable() {
        return centroContable;
    }

    public int getfolioManualContable() {
        return folioManualContable;
    }

    public String getFolioSAI() {
        return folioSAI;
    }

    public int getIdGabinete() {
        return idGabinete;
    }

    public void setCentroContable(String centroContable) {
        this.centroContable = centroContable;
    }

    public void setfolioManualContable(int folioManualContable) {
        this.folioManualContable = folioManualContable;
    }

    public void setFolioSAI(String folioSAI) {
        this.folioSAI = folioSAI;
    }

    public void setIdGabinete(int idGabinete) {
        this.idGabinete = idGabinete;
    }

    @Override
    public String toString() {
        return "ConciliacionBancoFirmadaEncabezado [centroContable=" + centroContable + ", folioConciliacionContable=" + folioManualContable + ", folioSAI=" + folioSAI + ", idGabinete=" + idGabinete + "]";
    }
}
