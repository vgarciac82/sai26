package com.axtel.egresos.viaticos;

import java.util.Base64;

public class TransporteOficial extends Transporte {

    private String numEconomico;

    private int tieneVales;

    public int getTieneVales() {
        return tieneVales;
    }

    public void setTieneVales(int tieneVales) {
        this.tieneVales = tieneVales;
    }

    public String getNumEconomico() {
        return numEconomico;
    }

    public void setNumEconomico(String numEconomico) {
        this.numEconomico = numEconomico;
    }

    @Override
    public String toString() {
        return "TrasporteOficial [numEconomico=" + numEconomico + ", tieneVales=" + tieneVales + "]";
    }
}
