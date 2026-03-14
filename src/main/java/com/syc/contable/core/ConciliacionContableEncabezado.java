package com.syc.contable.core;

import java.util.Base64;

public class ConciliacionContableEncabezado {

    private String centroContable;

    private String cuentaBancaria;

    private int folioConciliacionContable;

    private String folioSAI;

    private int idGabinete;

    public ConciliacionContableEncabezado(String centroContable, String cuentaBancaria, int folioConciliacionContable, String folioSAI, int idGabinete) {
        super();
        this.centroContable = centroContable;
        this.cuentaBancaria = cuentaBancaria;
        this.folioConciliacionContable = folioConciliacionContable;
        this.folioSAI = folioSAI;
        this.idGabinete = idGabinete;
    }

    public String getCentroContable() {
        return centroContable;
    }

    public String getCuentaBancaria() {
        return cuentaBancaria;
    }

    public int getfolioConciliacionContable() {
        return folioConciliacionContable;
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

    public void setCuentaBancaria(String cuentaBancaria) {
        this.cuentaBancaria = cuentaBancaria;
    }

    public void setfolioConciliacionContable(int folioConciliacionContable) {
        this.folioConciliacionContable = folioConciliacionContable;
    }

    public void setFolioSAI(String folioSAI) {
        this.folioSAI = folioSAI;
    }

    public void setIdGabinete(int idGabinete) {
        this.idGabinete = idGabinete;
    }

    @Override
    public String toString() {
        return "ConciliacionBancoFirmadaEncabezado [centroContable=" + centroContable + ", cuentaBancaria=" + cuentaBancaria + ", folioConciliacionContable=" + folioConciliacionContable + ", folioSAI=" + folioSAI + ", idGabinete=" + idGabinete + "]";
    }
}
