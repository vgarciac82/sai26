package com.syc.gestion.core;

import java.io.Serializable;
import java.util.Base64;

public class UsuarioPropiedades implements Serializable {

    private static final long serialVersionUID = 4164913516772951797L;

    private String u_login;

    private String up_nombre;

    private String up_valor;

    public String getLogin() {
        return u_login;
    }

    public void setLogin(String u_login) {
        this.u_login = u_login;
    }

    public String getNombre() {
        return up_nombre;
    }

    public void setNombre(String up_nombre) {
        this.up_nombre = up_nombre;
    }

    public String getValor() {
        return up_valor;
    }

    public void setValor(String up_valor) {
        this.up_valor = up_valor;
    }
}
