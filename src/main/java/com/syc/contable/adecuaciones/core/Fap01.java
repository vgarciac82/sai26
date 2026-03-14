package com.syc.contable.adecuaciones.core;

import java.util.Arrays;
import java.util.Base64;

public class Fap01 {

    private String AE;

    private String AI;

    private String cartera;

    private String ef;

    private String ejercicio;

    private String ep;

    private String f;

    private String ff;

    private String gf;

    private double[] montos = new double[13];

    private String movimiento;

    private int nfolioconsolidacion;

    private String OG;

    private String PG;

    private String PP;

    private String ramo;

    private String sf;

    private String tg;

    private String unidadResponsable;

    public String getAE() {
        return AE;
    }

    public String getAI() {
        return AI;
    }

    public String getCartera() {
        return cartera;
    }

    public String getEf() {
        return ef;
    }

    public String getEjercicio() {
        return ejercicio;
    }

    public String getEp() {
        return ep;
    }

    public String getF() {
        return f;
    }

    public String getFf() {
        return ff;
    }

    public String getGf() {
        return gf;
    }

    public double[] getMontos() {
        return montos;
    }

    public String getMovimiento() {
        return movimiento;
    }

    public int getNfolioconsolidacion() {
        return nfolioconsolidacion;
    }

    public String getOG() {
        return OG;
    }

    public String getPG() {
        return PG;
    }

    public String getPP() {
        return PP;
    }

    public String getRamo() {
        return ramo;
    }

    public String getSf() {
        return sf;
    }

    public String getTg() {
        return tg;
    }

    public String getUnidadResponsable() {
        return unidadResponsable;
    }

    public void setAE(String aE) {
        AE = aE;
    }

    public void setAI(String aI) {
        AI = aI;
    }

    public void setCartera(String cartera) {
        this.cartera = cartera;
    }

    public void setEf(String ef) {
        this.ef = ef;
    }

    public void setEjercicio(String ejercicio) {
        this.ejercicio = ejercicio;
    }

    public void setEp(String ep) {
        this.ep = ep;
    }

    public void setF(String f) {
        this.f = f;
    }

    public void setFf(String ff) {
        this.ff = ff;
    }

    public void setGf(String gf) {
        this.gf = gf;
    }

    public void setMontos(double[] montos) {
        this.montos = montos;
    }

    public void setMovimiento(String movimiento) {
        this.movimiento = movimiento;
    }

    public void setNfolioconsolidacion(int nfolioconsolidacion) {
        this.nfolioconsolidacion = nfolioconsolidacion;
    }

    public void setOG(String oG) {
        OG = oG;
    }

    public void setPG(String pG) {
        PG = pG;
    }

    public void setPP(String pP) {
        PP = pP;
    }

    public void setRamo(String ramo) {
        this.ramo = ramo;
    }

    public void setSf(String sf) {
        this.sf = sf;
    }

    public void setTg(String tg) {
        this.tg = tg;
    }

    public void setUnidadResponsable(String unidadResponsable) {
        this.unidadResponsable = unidadResponsable;
    }

    @Override
    public String toString() {
        return "Fap01 [nfolioconsolidacion=" + nfolioconsolidacion + ", ep=" + ep + ", movimiento=" + movimiento + ", montos=" + Arrays.toString(montos) + ", ejercicio=" + ejercicio + ", ramo=" + ramo + ", unidadResponsable=" + unidadResponsable + ", gf=" + gf + ", f=" + f + ", sf=" + sf + ", PG=" + PG + ", AI=" + AI + ", PP=" + PP + ", OG=" + OG + ", tg=" + tg + ", ff=" + ff + ", ef=" + ef + ", cartera=" + cartera + ", AE=" + AE + "]";
    }
}
