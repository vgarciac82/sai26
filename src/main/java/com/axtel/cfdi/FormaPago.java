package com.axtel.cfdi;

import java.util.Base64;

public class FormaPago {

    private String formaPago;

    private String descripcion;

    public FormaPago() {
    }

    public FormaPago(String formaPago) {
        super();
        this.formaPago = formaPago;
    }

    public String getFormaPago() {
        return formaPago;
    }

    public void setFormaPago(String formaPago) {
        this.formaPago = formaPago;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
