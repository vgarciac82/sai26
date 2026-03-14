package com.syc.gestion.core;

import java.io.Serializable;
import java.util.Base64;

public class OperacionSiguiente implements Serializable {

    private static final long serialVersionUID = 6665831672920007324L;

    private int id_tc;

    private int id_oper;

    private int id_oper_sigte;

    private String os_responsable;

    private String os_operacion;

    public int getIdTC() {
        return id_tc;
    }

    public void setIdTC(int id_tc) {
        this.id_tc = id_tc;
    }

    public int getIdOperacion() {
        return id_oper;
    }

    public void setIdOperacion(int id_oper) {
        this.id_oper = id_oper;
    }

    public int getIdOperacionSigte() {
        return id_oper_sigte;
    }

    public void setIdOperacionSigte(int id_oper_sigte) {
        this.id_oper_sigte = id_oper_sigte;
    }

    public String getResponsable() {
        return os_responsable;
    }

    public void setResponsable(String os_responsable) {
        this.os_responsable = os_responsable;
    }

    public String getOperacion() {
        return os_operacion;
    }

    public void setOperacion(String os_operacion) {
        this.os_operacion = os_operacion;
    }
}
