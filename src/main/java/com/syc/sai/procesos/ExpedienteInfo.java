package com.syc.sai.procesos;

import java.util.Base64;

public class ExpedienteInfo {

    String tituloAplicacion;

    int idGabinete;

    public String getTituloAplicacion() {
        return tituloAplicacion;
    }

    public void setTituloAplicacion(String tituloAplicacion) {
        this.tituloAplicacion = tituloAplicacion;
    }

    public int getIdGabinete() {
        return idGabinete;
    }

    public void setIdGabinete(int idGabinete) {
        this.idGabinete = idGabinete;
    }

    @Override
    public String toString() {
        return "ExpedienteInfo [tituloAplicacion=" + tituloAplicacion + ", idGabinete=" + idGabinete + "]";
    }
}
