package com.syc.contable.core;

import java.util.Base64;

public class AdecuacionDetalle {

    private int nDocRenglon;

    private String EP;

    private Evento cEvento;

    private double mImporte;

    public int getnDocRenglon() {
        return nDocRenglon;
    }

    public void setnDocRenglon(int nDocRenglon) {
        this.nDocRenglon = nDocRenglon;
    }

    public String getEP() {
        return EP;
    }

    public void setEP(String eP) {
        EP = eP;
    }

    public Evento getcEvento() {
        return cEvento;
    }

    public void setcEvento(Evento cEvento) {
        this.cEvento = cEvento;
    }

    public double getmImporte() {
        return mImporte;
    }

    public void setmImporte(double mImporte) {
        this.mImporte = mImporte;
    }
}
