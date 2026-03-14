package com.axtel.cfdi;

import java.util.Base64;

public class ClaveProdServ {

    private String claveProdServ;

    private String descripcion;

    public String getClaveProdServ() {
        return claveProdServ;
    }

    public void setClaveProdServ(String claveProdServ) {
        this.claveProdServ = claveProdServ;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return "ClaveProdServ [claveProdServ=" + claveProdServ + ", descripcion=" + descripcion + "]";
    }
}
