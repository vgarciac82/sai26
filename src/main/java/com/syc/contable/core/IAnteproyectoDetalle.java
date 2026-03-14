package com.syc.contable.core;

import java.util.Base64;

public class IAnteproyectoDetalle {

    private int nConsecutivo;

    private String aEjercicioFiscal;

    private String cUnidadResponsable;

    private String cClaveSiaff;

    private String cClaveInterna;

    private double mCalculado;

    private double mOptimo;

    private double mIreductible;

    private int nPorcentajeReduccion;

    private int nPorcentajeIncremento;

    private String cMotivoRechazo;

    public int getnConsecutivo() {
        return nConsecutivo;
    }

    public void setnConsecutivo(int nConsecutivo) {
        this.nConsecutivo = nConsecutivo;
    }

    public String getaEjercicioFiscal() {
        return aEjercicioFiscal;
    }

    public void setaEjercicioFiscal(String aEjercicioFiscal) {
        this.aEjercicioFiscal = aEjercicioFiscal;
    }

    public String getcUnidadResponsable() {
        return cUnidadResponsable;
    }

    public void setcUnidadResponsable(String cUnidadResponsable) {
        this.cUnidadResponsable = cUnidadResponsable;
    }

    public String getcClaveSiaff() {
        return cClaveSiaff;
    }

    public void setcClaveSiaff(String cClaveSiaff) {
        this.cClaveSiaff = cClaveSiaff;
    }

    public String getcClaveInterna() {
        return cClaveInterna;
    }

    public void setcClaveInterna(String cClaveInterna) {
        this.cClaveInterna = cClaveInterna;
    }

    public double getmCalculado() {
        return mCalculado;
    }

    public void setmCalculado(double mCalculado) {
        this.mCalculado = mCalculado;
    }

    public double getmOptimo() {
        return mOptimo;
    }

    public void setmOptimo(double mOptimo) {
        this.mOptimo = mOptimo;
    }

    public double getmIreductible() {
        return mIreductible;
    }

    public void setmIreductible(double mIreductible) {
        this.mIreductible = mIreductible;
    }

    public int getnPorcentajeReduccion() {
        return nPorcentajeReduccion;
    }

    public void setnPorcentajeReduccion(int nPorcentajeReduccion) {
        this.nPorcentajeReduccion = nPorcentajeReduccion;
    }

    public int getnPorcentajeIncremento() {
        return nPorcentajeIncremento;
    }

    public void setnPorcentajeIncremento(int nPorcentajeIncremento) {
        this.nPorcentajeIncremento = nPorcentajeIncremento;
    }

    public String getcMotivoRechazo() {
        return cMotivoRechazo;
    }

    public void setcMotivoRechazo(String cMotivoRechazo) {
        this.cMotivoRechazo = cMotivoRechazo;
    }
}
