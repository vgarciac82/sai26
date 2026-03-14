package com.syc.gestion.core;

import java.util.Base64;

public class UsuarioGrupo {

    private String u_login;

    private String g_nombre;

    public String getLogin() {
        return u_login;
    }

    public void setLogin(String u_login) {
        this.u_login = u_login;
    }

    public String getNombre() {
        return g_nombre;
    }

    public void setNombre(String g_nombre) {
        this.g_nombre = g_nombre;
    }
}
