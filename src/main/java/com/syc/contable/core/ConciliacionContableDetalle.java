package com.syc.contable.core;

import java.util.Base64;

public class ConciliacionContableDetalle {

    private int folioConciliacionContable;

    private int idConciliacion;

    private int mes;

    private String usuarioCarga;

    public ConciliacionContableDetalle() {
        super();
    }

    public ConciliacionContableDetalle(int folioConciliacionContable, int mes, String usuarioCarga, int idConciliacion) {
        super();
        this.folioConciliacionContable = folioConciliacionContable;
        this.mes = mes;
        this.usuarioCarga = usuarioCarga;
        this.idConciliacion = idConciliacion;
    }

    public int getFolioConciliacionContable() {
        return folioConciliacionContable;
    }

    public int getIdConciliacion() {
        return idConciliacion;
    }

    public int getMes() {
        return mes;
    }

    public String getUsuarioCarga() {
        return usuarioCarga;
    }

    public void setFolioConciliacionContable(int folioConciliacionContable) {
        this.folioConciliacionContable = folioConciliacionContable;
    }

    public void setIdConciliacion(int idConciliacion) {
        this.idConciliacion = idConciliacion;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public void setUsuarioCarga(String usuarioCarga) {
        this.usuarioCarga = usuarioCarga;
    }

    @Override
    public String toString() {
        return "ConciliacionContableDetalle [folioConciliacionContable=" + folioConciliacionContable + ", mes=" + mes + ", usuarioCarga=" + usuarioCarga + ", idConciliacion=" + idConciliacion + "]";
    }
}
