package com.syc.fortimax.core;

import java.util.Base64;

public class ExpedientNode {

    private String path;

    private String tituloAplicacion;

    private int idGabinete;

    private int idCarpetaPadre;

    private int idDocumento;

    private String fortimax;

    private String nombreDocumento;

    public String getNombreDocumento() {
        return nombreDocumento;
    }

    public void setNombreDocumento(String nombreDocumento) {
        this.nombreDocumento = nombreDocumento;
    }

    public String getFortimax() {
        return fortimax;
    }

    public void setFortimax(String fortimax) {
        this.fortimax = fortimax;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTituloAplicacion() {
        return tituloAplicacion;
    }

    public void setTituloAplicacion(String tituloAplicacion) {
        this.tituloAplicacion = tituloAplicacion;
    }

    public int getIdGabinete() {
        return idGabinete;
    }

    public void setIdGabinete(int idGabinete) {
        this.idGabinete = idGabinete;
    }

    public int getIdCarpetaPadre() {
        return idCarpetaPadre;
    }

    public void setIdCarpetaPadre(int idCarpetaPadre) {
        this.idCarpetaPadre = idCarpetaPadre;
    }

    public int getIdDocumento() {
        return idDocumento;
    }

    public void setIdDocumento(int idDocumento) {
        this.idDocumento = idDocumento;
    }
}
