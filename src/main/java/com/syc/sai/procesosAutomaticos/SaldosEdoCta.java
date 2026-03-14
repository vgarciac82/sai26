package com.syc.sai.procesosAutomaticos;

import java.util.Base64;

public class SaldosEdoCta {

    private double saldoAlCorte;

    private double saldoAnterior;

    public SaldosEdoCta(double saldoAnterior, double saldoAlCorte) {
        super();
        this.saldoAnterior = saldoAnterior;
        this.saldoAlCorte = saldoAlCorte;
    }

    public double getSaldoAlCorte() {
        return saldoAlCorte;
    }

    public double getSaldoAnterior() {
        return saldoAnterior;
    }

    public void setSaldoAlCorte(double saldoAlCorte) {
        this.saldoAlCorte = saldoAlCorte;
    }

    public void setSaldoAnterior(double saldoAnterior) {
        this.saldoAnterior = saldoAnterior;
    }

    @Override
    public String toString() {
        return "SaldosEdoCta [saldoAnterior=" + saldoAnterior + ", saldoAlCorte=" + saldoAlCorte + "]";
    }
}
