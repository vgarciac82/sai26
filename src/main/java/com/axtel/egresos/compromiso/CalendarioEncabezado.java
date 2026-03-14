package com.axtel.egresos.compromiso;

import java.util.List;
import java.util.Base64;

public class CalendarioEncabezado {

    private String folio;

    private String ep;

    private String tipo;

    private List<CalendarioDetalle> datos;

    // Getters y setters
    public String getEp() {
        return ep;
    }

    public void setEp(String ep) {
        this.ep = ep;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public List<CalendarioDetalle> getDatos() {
        return datos;
    }

    public void setDatos(List<CalendarioDetalle> datos) {
        this.datos = datos;
    }

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }
}
