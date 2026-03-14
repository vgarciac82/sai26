package com.syc.sai.contabilidad;

import java.util.Date;
import java.util.Base64;

public class MesContable {

    private int nMes;

    private String cCentroContable;

    private int aEjercicioFiscal;

    private String mesAbierto;

    private Date fCierre;

    private String cUnidadResponsable;

    private String usuarioCerro;

    public int getnMes() {
        return nMes;
    }

    public void setnMes(int nMes) {
        this.nMes = nMes;
    }

    public String getcCentroContable() {
        return cCentroContable;
    }

    public void setcCentroContable(String cCentroContable) {
        this.cCentroContable = cCentroContable;
    }

    public int getaEjercicioFiscal() {
        return aEjercicioFiscal;
    }

    public void setaEjercicioFiscal(int aEjercicioFiscal) {
        this.aEjercicioFiscal = aEjercicioFiscal;
    }

    public String getMesAbierto() {
        return mesAbierto;
    }

    public void setMesAbierto(String mesAbierto) {
        this.mesAbierto = mesAbierto;
    }

    public Date getfCierre() {
        return fCierre;
    }

    public void setfCierre(Date fCierre) {
        this.fCierre = fCierre;
    }

    public String getcUnidadResponsable() {
        return cUnidadResponsable;
    }

    public void setcUnidadResponsable(String cUnidadResponsable) {
        this.cUnidadResponsable = cUnidadResponsable;
    }

    public String getUsuarioCerro() {
        return usuarioCerro;
    }

    public void setUsuarioCerro(String usuarioCerro) {
        this.usuarioCerro = usuarioCerro;
    }
}
