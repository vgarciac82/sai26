package com.axtel.cfdi;

import java.util.Base64;

public class Serie {

    private boolean exclusivaNomina;

    private boolean exclusivaPagos;

    private int idSerie;

    private String serie;

    public int getIdSerie() {
        return idSerie;
    }

    public String getSerie() {
        return serie;
    }

    public boolean isExclusivaNomina() {
        return exclusivaNomina;
    }

    public boolean isExclusivaPagos() {
        return exclusivaPagos;
    }

    public void setExclusivaNomina(boolean exclusivaNomina) {
        this.exclusivaNomina = exclusivaNomina;
    }

    public void setExclusivaPagos(boolean exclusivaPagos) {
        this.exclusivaPagos = exclusivaPagos;
    }

    public void setIdSerie(int idSerie) {
        this.idSerie = idSerie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    @Override
    public String toString() {
        return "Serie [idSerie=" + idSerie + ", serie=" + serie + ", exclusivaNomina=" + exclusivaNomina + ", exclusivaPagos=" + exclusivaPagos + "]";
    }
}
