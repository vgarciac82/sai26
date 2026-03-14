package com.syc.gestion.documental;

import java.io.Serializable;
import java.util.Base64;

public class CatDetInstruccion implements Serializable {

    private final static long serialVersionUID = 1;

    private int id_det_instruccion;

    private String di_descripcion;

    public int getId_det_instruccion() {
        return id_det_instruccion;
    }

    public void setId_det_instruccion(int id_det_instruccion) {
        this.id_det_instruccion = id_det_instruccion;
    }

    public String getDi_descripcion() {
        return di_descripcion;
    }

    public void setDi_descripcion(String di_descripcion) {
        this.di_descripcion = di_descripcion;
    }
}
