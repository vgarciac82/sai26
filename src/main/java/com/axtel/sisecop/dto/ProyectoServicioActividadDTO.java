package com.axtel.sisecop.dto;

import java.io.Serializable;
import com.axtel.sisecop.entities.ProyectoMes;
import java.util.Base64;

public class ProyectoServicioActividadDTO implements Serializable {

    private static final long serialVersionUID = -3786325513922165869L;

    private int servicioactividadId;

    private int idService;

    private int servicioactividadAnio;

    private String servicioactividadDescripcion;

    private ProyectoMes sisecopMes;

    public int getServicioactividadId() {
        return servicioactividadId;
    }

    public void setServicioactividadId(int servicioactividadId) {
        this.servicioactividadId = servicioactividadId;
    }

    public int getIdService() {
        return idService;
    }

    public void setIdService(int idService) {
        this.idService = idService;
    }

    public int getServicioactividadAnio() {
        return servicioactividadAnio;
    }

    public void setServicioactividadAnio(int servicioactividadAnio) {
        this.servicioactividadAnio = servicioactividadAnio;
    }

    public String getServicioactividadDescripcion() {
        return servicioactividadDescripcion;
    }

    public void setServicioactividadDescripcion(String servicioactividadDescripcion) {
        this.servicioactividadDescripcion = servicioactividadDescripcion;
    }

    public ProyectoMes getSisecopMes() {
        return sisecopMes;
    }

    public void setSisecopMes(ProyectoMes sisecopMes) {
        this.sisecopMes = sisecopMes;
    }

    @Override
    public String toString() {
        return "ProyectoServicioActividadDTO [servicioactividadId=" + servicioactividadId + ", idService=" + idService + ", servicioactividadAnio=" + servicioactividadAnio + ", servicioactividadDescripcion=" + servicioactividadDescripcion + ", sisecopMes=" + sisecopMes + "]";
    }
}
