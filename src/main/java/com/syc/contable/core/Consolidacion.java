package com.syc.contable.core;

import java.util.Base64;

public class Consolidacion {

    private int nFolioCONSOLIDACION;

    private String fCreacion;

    private String nAutorizacionMAP;

    private String fMAP;

    private String cMotivoRechazo;

    public int getnFolioCONSOLIDACION() {
        return nFolioCONSOLIDACION;
    }

    public void setnFolioCONSOLIDACION(int nFolioCONSOLIDACION) {
        this.nFolioCONSOLIDACION = nFolioCONSOLIDACION;
    }

    public String getfCreacion() {
        return fCreacion;
    }

    public void setfCreacion(String fCreacion) {
        this.fCreacion = fCreacion;
    }

    public String getnAutorizacionMAP() {
        return nAutorizacionMAP;
    }

    public void setnAutorizacionMAP(String nAutorizacionMAP) {
        this.nAutorizacionMAP = nAutorizacionMAP;
    }

    public String getfMAP() {
        return fMAP;
    }

    public void setfMAP(String fMAP) {
        this.fMAP = fMAP;
    }

    public String getcMotivoRechazo() {
        return cMotivoRechazo;
    }

    public void setcMotivoRechazo(String cMotivoRechazo) {
        this.cMotivoRechazo = cMotivoRechazo;
    }
}
