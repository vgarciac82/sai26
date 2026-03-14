package com.syc.gestion.core;

import java.io.Serializable;
import java.util.Base64;

public class Cobertura implements Serializable {

    private final static long serialVersionUID = 1;

    private int idCobertura;

    private String coDescripcion;

    private String coWhereClause;

    private String coFromClause;

    private int idTabla;

    private int idProducto;

    public int getIdCobertura() {
        return idCobertura;
    }

    public void setIdCobertura(int idCobertura) {
        this.idCobertura = idCobertura;
    }

    public String getCoDescripcion() {
        return coDescripcion;
    }

    public void setCoDescripcion(String coDescripcion) {
        this.coDescripcion = coDescripcion;
    }

    public String getCoWhereClause() {
        return coWhereClause;
    }

    public void setCoWhereClause(String coWhereClause) {
        this.coWhereClause = coWhereClause;
    }

    public String getCoFromClause() {
        return coFromClause;
    }

    public void setCoFromClause(String coFromClause) {
        this.coFromClause = coFromClause;
    }

    public int getIdTabla() {
        return idTabla;
    }

    public void setIdTabla(int idTabla) {
        this.idTabla = idTabla;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }
}
