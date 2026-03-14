package com.syc.gestion.documental;

import java.io.Serializable;
import java.util.Base64;

public class CatLDistribucion implements Serializable {

    private final static long serialVersionUID = 1;

    private int id_ldistribucion;

    private String ld_nombre;

    public int getId_ldistribucion() {
        return id_ldistribucion;
    }

    public void setId_ldistribucion(int id_ldistribucion) {
        this.id_ldistribucion = id_ldistribucion;
    }

    public String getLd_nombre() {
        return ld_nombre;
    }

    public void setLd_nombre(String ld_nombre) {
        this.ld_nombre = ld_nombre;
    }
}
