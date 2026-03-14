package com.syc.adquisiciones.core;

import java.util.Base64;

public class DatosRecepcion {

    private String cIdPedContDef;

    private String cIdRecepAnticipo;

    private String cIdRecepcionMat;

    private boolean factorAmortizacion;

    private boolean recepMat;

    public String getcIdPedContDef() {
        return cIdPedContDef;
    }

    public String getcIdRecepAnticipo() {
        return cIdRecepAnticipo;
    }

    public String getcIdRecepcionMat() {
        return cIdRecepcionMat;
    }

    public boolean isFactorAmortizacion() {
        return factorAmortizacion;
    }

    public boolean isRecepMat() {
        return recepMat;
    }

    public void setcIdPedContDef(String cIdPedContDef) {
        this.cIdPedContDef = cIdPedContDef;
    }

    public void setcIdRecepAnticipo(String cIdRecepAnticipo) {
        this.cIdRecepAnticipo = cIdRecepAnticipo;
    }

    public void setcIdRecepcionMat(String cIdRecepcionMat) {
        this.cIdRecepcionMat = cIdRecepcionMat;
    }

    public void setFactorAmortizacion(boolean factorAmortizacion) {
        this.factorAmortizacion = factorAmortizacion;
    }

    public void setRecepMat(boolean recepMat) {
        this.recepMat = recepMat;
    }
}
