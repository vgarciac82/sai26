package com.syc.admin.servlet;

import java.util.Base64;

public class CatEmpleado {

    private int id_empleado = -1;

    private String ce_nombre_completo = null;

    private String ce_ap_paterno = null;

    private String ce_ap_materno = null;

    private String ce_os_responsable = null;

    private String salutacion = null;

    private String tipo_rem_des = null;

    public int getId_empleado() {
        return id_empleado;
    }

    public void setId_empleado(int id_empleado) {
        this.id_empleado = id_empleado;
    }

    public String getCe_nombre_completo() {
        return ce_nombre_completo;
    }

    public void setCe_nombre_completo(String ce_nombre_completo) {
        this.ce_nombre_completo = ce_nombre_completo;
    }

    public String getCe_ap_paterno() {
        return ce_ap_paterno;
    }

    public void setCe_ap_paterno(String ce_ap_paterno) {
        this.ce_ap_paterno = ce_ap_paterno;
    }

    public String getCe_ap_materno() {
        return ce_ap_materno;
    }

    public void setCe_ap_materno(String ce_ap_materno) {
        this.ce_ap_materno = ce_ap_materno;
    }

    public String getCe_os_responsable() {
        return ce_os_responsable;
    }

    public void setCe_os_responsable(String ce_os_responsable) {
        this.ce_os_responsable = ce_os_responsable;
    }

    public String getSalutacion() {
        return salutacion;
    }

    public void setSalutacion(String salutacion) {
        this.salutacion = salutacion;
    }

    public String getTipo_rem_des() {
        return tipo_rem_des;
    }

    public void setTipo_rem_des(String tipo_rem_des) {
        this.tipo_rem_des = tipo_rem_des;
    }
}
