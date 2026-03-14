package com.syc.fortimax.core;

import java.sql.Timestamp;
import java.util.Base64;

public class Carpeta {

    @Override
    public String toString() {
        return "Carpeta [titulo_aplicacion=" + titulo_aplicacion + ", id_gabinete=" + id_gabinete + ", id_carpeta=" + id_carpeta + ", nombre_carpeta=" + nombre_carpeta + ", nombre_usuario=" + nombre_usuario + ", bandera_raiz=" + bandera_raiz + ", fh_creacion=" + fh_creacion + ", fh_modificacion=" + fh_modificacion + ", numero_accesos=" + numero_accesos + ", numero_carpetas=" + numero_carpetas + ", numero_documentos=" + numero_documentos + ", descripcion=" + descripcion + ", password=" + password + "]";
    }

    private String titulo_aplicacion;

    private int id_gabinete;

    private int id_carpeta;

    private String nombre_carpeta;

    private String nombre_usuario;

    private String bandera_raiz;

    private Timestamp fh_creacion;

    private Timestamp fh_modificacion;

    private int numero_accesos;

    private int numero_carpetas;

    private int numero_documentos;

    private String descripcion;

    private String password;

    public String getBanderaRaiz() {
        return bandera_raiz;
    }

    public void setBanderaRaiz(String bandera_raiz) {
        this.bandera_raiz = bandera_raiz;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Timestamp getFechaCreacion() {
        return fh_creacion;
    }

    public void setFechaCreacion(Timestamp fh_creacion) {
        this.fh_creacion = fh_creacion;
    }

    public Timestamp getFechaModificacion() {
        return fh_modificacion;
    }

    public void setFechaModificacion(Timestamp fh_modificacion) {
        this.fh_modificacion = fh_modificacion;
    }

    public int getIdCarpeta() {
        return id_carpeta;
    }

    public void setIdCarpeta(int id_carpeta) {
        this.id_carpeta = id_carpeta;
    }

    public int getIdGabinete() {
        return id_gabinete;
    }

    public void setIdGabinete(int id_gabinete) {
        this.id_gabinete = id_gabinete;
    }

    public String getNombreCarpeta() {
        return nombre_carpeta;
    }

    public void setNombreCarpeta(String nombre_carpeta) {
        this.nombre_carpeta = nombre_carpeta;
    }

    public String getNombreUsuario() {
        return nombre_usuario;
    }

    public void setNombreUsuario(String nombre_usuario) {
        this.nombre_usuario = nombre_usuario;
    }

    public int getNumeroAccesos() {
        return numero_accesos;
    }

    public void setNumeroAccesos(int numero_accesos) {
        this.numero_accesos = numero_accesos;
    }

    public int getNumeroCarpetas() {
        return numero_carpetas;
    }

    public void setNumeroCarpetas(int numero_carpetas) {
        this.numero_carpetas = numero_carpetas;
    }

    public int getNumeroDocumentos() {
        return numero_documentos;
    }

    public void setNumeroDocumentos(int numero_documentos) {
        this.numero_documentos = numero_documentos;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTituloAplicacion() {
        return titulo_aplicacion;
    }

    public void setTituloAplicacion(String titulo_aplicacion) {
        this.titulo_aplicacion = titulo_aplicacion;
    }
}
