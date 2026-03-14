package com.axtel.sisecop.entities;

import java.io.Serializable;
import java.util.Base64;

public class ProyectoServicioTerritorio implements Serializable {

    private static final long serialVersionUID = -1032571951204809296L;

    private ProyectoEntidadFederativa entidadFederativa;

    private ProyectoMunicipio municipio;

    private int territorioId;

    public ProyectoEntidadFederativa getEntidadFederativa() {
        return entidadFederativa;
    }

    public ProyectoMunicipio getMunicipio() {
        return municipio;
    }

    public int getTerritorioId() {
        return territorioId;
    }

    public void setEntidadFederativa(ProyectoEntidadFederativa entidadFederativa) {
        this.entidadFederativa = entidadFederativa;
    }

    public void setMunicipio(ProyectoMunicipio municipio) {
        this.municipio = municipio;
    }

    public void setTerritorioId(int territorioId) {
        this.territorioId = territorioId;
    }

    @Override
    public String toString() {
        return "ProyectoServicioTerritorio [territorioId=" + territorioId + ", entidadFederativa=" + entidadFederativa + ", municipio=" + municipio + "]";
    }
}
