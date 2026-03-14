package com.syc.gestion.documental;

import java.io.Serializable;
import java.util.Base64;

public class CatTipoDocumento implements Serializable {

    private final static long serialVersionUID = 1;

    private int td_id;

    private String td_descripcion;

    public int getTd_id() {
        return td_id;
    }

    public void setTd_id(int td_id) {
        this.td_id = td_id;
    }

    public String getTd_descripcion() {
        return td_descripcion;
    }

    public void setTd_descripcion(String td_descripcion) {
        this.td_descripcion = td_descripcion;
    }
}
