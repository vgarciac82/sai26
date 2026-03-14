package com.syc.auditoria.core;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Base64;

public class Auditoria implements Serializable {

    private final static long serialVersionUID = 1;

    public static final int CREATED = 1;

    public static final int EXECUTED = 2;

    public static final int MSG_SENDED = 4;

    private int id_auditoria;

    private String nombre_aplicacion;

    private String nombre_modulo;

    private String accion;

    private String valor_origen;

    private String valor_destino;

    private String usuario;

    private String nombre_usuario;

    private Timestamp fecha_operacion;

    private String area_usuario;

    private String area_padre;

    private String id_area;

    private String query;

    public String getQuery() {
        return query;
    }

    public String getNombre_usuario() {
        return nombre_usuario;
    }

    public String getArea_usuario() {
        return area_usuario;
    }

    public String getArea_padre() {
        return area_padre;
    }

    public String getId_area() {
        return id_area;
    }

    public void setNombre_usuario(String nombreusuario) {
        this.nombre_usuario = nombreusuario;
    }

    public void setArea_usuario(String areausuario) {
        this.area_usuario = areausuario;
    }

    public void setArea_padre(String areapadre) {
        this.area_padre = areapadre;
    }

    public void setId_area(String idarea) {
        this.id_area = idarea;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public int getId_auditoria() {
        return id_auditoria;
    }

    public void setId_auditoria(int id_auditoria) {
        this.id_auditoria = id_auditoria;
    }

    public String getNombre_aplicacion() {
        return nombre_aplicacion;
    }

    public void setNombre_aplicacion(String nombre_aplicacion) {
        this.nombre_aplicacion = nombre_aplicacion;
    }

    public String getNombre_modulo() {
        return nombre_modulo;
    }

    public void setNombre_modulo(String nombre_modulo) {
        this.nombre_modulo = nombre_modulo;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public String getValor_origen() {
        return valor_origen;
    }

    public void setValor_origen(String valor) {
        this.valor_origen = valor;
    }

    public String getValor_destino() {
        return valor_destino;
    }

    public void setValor_destino(String valor) {
        this.valor_destino = valor;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public Timestamp getFecha_operacion() {
        return fecha_operacion;
    }

    public void setFecha_operacion(Timestamp fecha_operacion) {
        this.fecha_operacion = fecha_operacion;
    }
}
