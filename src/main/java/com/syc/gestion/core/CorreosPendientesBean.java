package com.syc.gestion.core;

import java.io.Serializable;
import java.util.Base64;

public class CorreosPendientesBean implements Serializable {

    private static final long serialVersionUID = -6972993172037799887L;

    private int id;

    private String destinatario;

    private String mensaje;

    private String subject;

    private String error;

    public CorreosPendientesBean(String destinatario, String mensaje, String subject, String error) {
        super();
        this.id = -1;
        this.destinatario = destinatario;
        this.mensaje = mensaje;
        this.subject = subject;
        this.error = error;
    }

    public CorreosPendientesBean() {
        super();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
