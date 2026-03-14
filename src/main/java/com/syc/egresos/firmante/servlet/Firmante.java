package com.syc.egresos.firmante.servlet;

import java.util.Base64;

public class Firmante {

    public static final String ELABORA = "Elabora";

    private String nombreEmpleado;

    private int numeroEmpleado;

    private String puestoEmpleado;

    private String correoEmpleado;

    private String areaEmpleado;

    private String tipoAutorizador;

    public Firmante() {
    }

    public Firmante(String nombreEmpleado, int numeroEmpleado, String puestoEmpleado) {
        super();
        this.nombreEmpleado = nombreEmpleado;
        this.numeroEmpleado = numeroEmpleado;
        this.puestoEmpleado = puestoEmpleado;
    }

    public Firmante(int employeeNumber) {
        this.numeroEmpleado = employeeNumber;
    }

    public String getNombreEmpleado() {
        return nombreEmpleado;
    }

    public void setNombreEmpleado(String nombreEmpleado) {
        this.nombreEmpleado = nombreEmpleado;
    }

    public int getNumeroEmpleado() {
        return numeroEmpleado;
    }

    public void setNumeroEmpleado(int numeroEmpleado) {
        this.numeroEmpleado = numeroEmpleado;
    }

    public String getPuestoEmpleado() {
        return puestoEmpleado;
    }

    public void setPuestoEmpleado(String puestoEmpleado) {
        this.puestoEmpleado = puestoEmpleado;
    }

    public String getTipoAutorizador() {
        return tipoAutorizador;
    }

    public void setTipoAutorizador(String tipoAutorizador) {
        this.tipoAutorizador = tipoAutorizador;
    }

    /**
     * @return the correoEmpleado
     */
    public String getCorreoEmpleado() {
        return correoEmpleado;
    }

    /**
     * @param correoEmpleado
     *            the correoEmpleado to set
     */
    public void setCorreoEmpleado(String correoEmpleado) {
        this.correoEmpleado = correoEmpleado;
    }

    /**
     * @return the areaEmpleado
     */
    public String getAreaEmpleado() {
        return areaEmpleado;
    }

    /**
     * @param areaEmpleado
     *            the areaEmpleado to set
     */
    public void setAreaEmpleado(String areaEmpleado) {
        this.areaEmpleado = areaEmpleado;
    }

    @Override
    public String toString() {
        return "Firmante [nombreEmpleado=" + nombreEmpleado + ", numeroEmpleado=" + numeroEmpleado + ", puestoEmpleado=" + puestoEmpleado + ", correoEmpleado=" + correoEmpleado + ", areaEmpleado=" + areaEmpleado + ", tipoAutorizador=" + tipoAutorizador + "]";
    }
}
