package com.axtel.sisecop.dto;

import java.util.Base64;

public class ProyectoExcepcionDTO {

    private int idProyectoOriginal;

    private int idTipoExcepcion;

    private String login;

    private String tituloProyecto;

    public int getIdProyectoOriginal() {
        return idProyectoOriginal;
    }

    public int getIdTipoExcepcion() {
        return idTipoExcepcion;
    }

    public String getLogin() {
        return login;
    }

    public String getTituloProyecto() {
        return tituloProyecto;
    }

    public void setIdProyectoOriginal(int idProyectoOriginal) {
        this.idProyectoOriginal = idProyectoOriginal;
    }

    public void setIdTipoExcepcion(int idTipoExcepcion) {
        this.idTipoExcepcion = idTipoExcepcion;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setTituloProyecto(String tituloProyecto) {
        this.tituloProyecto = tituloProyecto;
    }

    @Override
    public String toString() {
        return "ProyectoExcepcionDTO [idProyectoOriginal=" + idProyectoOriginal + ", idTipoExcepcion=" + idTipoExcepcion + ", tituloProyecto=" + tituloProyecto + ", login=" + login + "]";
    }
}
