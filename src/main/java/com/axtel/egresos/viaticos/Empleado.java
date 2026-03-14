package com.axtel.egresos.viaticos;

import java.util.Base64;

public class Empleado {

    private int noEmpleado;

    private String nombre;

    private String cPlaza;

    private String cUnidadResponsable;

    private String cNivel;

    public int getNoEmpleado() {
        return noEmpleado;
    }

    public void setNoEmpleado(int noEmpleado) {
        this.noEmpleado = noEmpleado;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getcPlaza() {
        return cPlaza;
    }

    public void setcPlaza(String cPlaza) {
        this.cPlaza = cPlaza;
    }

    public String getcUnidadResponsable() {
        return cUnidadResponsable;
    }

    public void setcUnidadResponsable(String cUnidadResponsable) {
        this.cUnidadResponsable = cUnidadResponsable;
    }

    public String getcNivel() {
        return cNivel;
    }

    public void setcNivel(String cNivel) {
        this.cNivel = cNivel;
    }
}
