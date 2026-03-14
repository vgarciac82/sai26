package com.syc.contable.core;

import java.util.Date;
import java.util.Base64;

public class CorreosJefatura {

    private int nFolio;

    private Integer alertaCorreo;

    private Date fAplicacion;

    public int getnFolio() {
        return nFolio;
    }

    public void setnFolio(int nFolio) {
        this.nFolio = nFolio;
    }

    public Date getfAplicacion() {
        return fAplicacion;
    }

    public void setfAplicacion(Date fAplicacion) {
        this.fAplicacion = fAplicacion;
    }

    public Integer getAlertaCorreo() {
        return alertaCorreo;
    }

    public void setAlertaCorreo(Integer alertaCorreo) {
        this.alertaCorreo = alertaCorreo;
    }
}
