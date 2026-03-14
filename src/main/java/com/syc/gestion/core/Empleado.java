package com.syc.gestion.core;

import java.io.Serializable;
import java.util.Base64;

public class Empleado implements Serializable {

    private static final long serialVersionUID = -7740203864511747270L;

    private String id = null;

    private String apellidoPaterno = null;

    private String apellidoMaterno = null;

    private String nombre = null;

    private String salutacion = null;

    private String claveUsuario = null;

    private String cargo = null;

    private String claveArea = null;

    public Empleado() {
        super();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getApellidoPaterno() {
        return apellidoPaterno;
    }

    public void setApellidoPaterno(String apellidoPaterno) {
        this.apellidoPaterno = apellidoPaterno;
    }

    public String getApellidoMaterno() {
        return apellidoMaterno;
    }

    public void setApellidoMaterno(String apellidoMaterno) {
        this.apellidoMaterno = apellidoMaterno;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNombreCompleto() {
        String nombreCompleto = ((apellidoPaterno == null) ? "" : apellidoPaterno + " ") + ((apellidoMaterno == null) ? "" : apellidoMaterno + ", ") + ((nombre == null) ? "" : nombre);
        return ("".equals(nombreCompleto) ? null : nombreCompleto);
    }

    public String getSalutacion() {
        return salutacion;
    }

    public void setSalutacion(String salutacion) {
        this.salutacion = salutacion;
    }

    public String getClaveUsuario() {
        return claveUsuario;
    }

    public void setClaveUsuario(String claveUsuario) {
        this.claveUsuario = claveUsuario;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getClaveArea() {
        return claveArea;
    }

    public void setClaveArea(String claveArea) {
        this.claveArea = claveArea;
    }

    public String toString() {
        return "com.syc.gestion.core.Empleado {\n" + "\t\tid=[" + this.id + "],\n" + "\t\tapellidoPaterno=[" + this.apellidoPaterno + "],\n" + "\t\tapellidoMaterno=[" + this.apellidoMaterno + "],\n" + "\t\tnombre=[" + this.nombre + "],\n" + "\t\tsalutacion=[" + this.salutacion + "],\n" + "\t\tclaveUsuario=[" + this.claveUsuario + "],\n" + "\t\tcargo=[" + this.cargo + "],\n" + "\t\tclaveArea=[" + this.claveArea + "],\n" + "}";
    }

    public void setArea(EmpleadoArea ea) {
        this.area = ea;
    }

    private EmpleadoArea area;

    public EmpleadoArea getArea() {
        return this.area;
    }
}
