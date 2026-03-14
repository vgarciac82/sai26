package com.syc.gestion.core;

import java.sql.Timestamp;
import java.util.Base64;

public class Mensaje {

    private int id_msg;

    private String msg_para_login;

    private String msg_de_login;

    private Timestamp msg_fecha;

    private String msg_asunto;

    private String msg_body;

    private int msg_status;

    private String usr_para_nombre;

    private String usr_de_nombre;

    public int getIdMsg() {
        return id_msg;
    }

    public void setIdMsg(int id_msg) {
        this.id_msg = id_msg;
    }

    public String getAsunto() {
        return msg_asunto;
    }

    public void setAsunto(String msg_asunto) {
        this.msg_asunto = msg_asunto;
    }

    public String getBody() {
        return msg_body;
    }

    public void setBody(String msg_body) {
        this.msg_body = msg_body;
    }

    public Timestamp getFecha() {
        return msg_fecha;
    }

    public void setFecha(Timestamp msg_fecha) {
        this.msg_fecha = msg_fecha;
    }

    public int getStatus() {
        return msg_status;
    }

    public void setStatus(int msg_status) {
        this.msg_status = msg_status;
    }

    public String getParaLogin() {
        return msg_para_login;
    }

    public void setParaLogin(String msg_para_login) {
        this.msg_para_login = msg_para_login;
    }

    public String getDeLogin() {
        return msg_de_login;
    }

    public void setDeLogin(String msg_de_login) {
        this.msg_de_login = msg_de_login;
    }

    public String getParaNombre() {
        return usr_para_nombre;
    }

    public void setParaNombre(String usr_para_nombre) {
        this.usr_para_nombre = usr_para_nombre;
    }

    public String getDeNombre() {
        return usr_de_nombre;
    }

    public void setDeNombre(String usr_de_nombre) {
        this.usr_de_nombre = usr_de_nombre;
    }
}
