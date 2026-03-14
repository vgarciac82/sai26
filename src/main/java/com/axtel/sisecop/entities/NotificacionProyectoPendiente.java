package com.axtel.sisecop.entities;

import java.util.Base64;

public class NotificacionProyectoPendiente {

    private String funcionario;

    private String cargo;

    private String folio;

    private String titulo;

    private String fecha;

    private String motivoRechazo;

    public String getFuncionario() {
        return funcionario;
    }

    public String getMotivoRechazo() {
        return motivoRechazo;
    }

    public void setFuncionario(String funcionario) {
        this.funcionario = funcionario;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    @Override
    public String toString() {
        return "NotificacionProyectoPendiente [funcionario=" + funcionario + ", cargo=" + cargo + ", folio=" + folio + ", titulo=" + titulo + ", fecha=" + fecha + "]";
    }

    public void setMotivoRechazo(String motivoRechazo) {
        this.motivoRechazo = motivoRechazo;
    }
}
