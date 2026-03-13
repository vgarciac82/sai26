package com.axtel.egresos.entities;

public class PaymentDiference {
    private String tipoDiferencia;
    private double diferencia;

    public PaymentDiference(String tipoDiferencia, double diferencia) {
        this.tipoDiferencia = tipoDiferencia;
        this.diferencia = diferencia;
    }

    public String getTipoDiferencia() {
        return tipoDiferencia;
    }

    public double getDiferencia() {
        return diferencia;
    }

    @Override
    public String toString() {
        return tipoDiferencia + ": Diferencia de " + diferencia;
    }
}
