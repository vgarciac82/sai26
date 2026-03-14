package com.axtel.cfdi.stamp.core;

import java.util.Base64;

public class InvoiceRespond {

    private String CFDI_xml;

    private String tfd;

    private String mensaje = "";

    private String estatus = "vig";

    public String getCFDI_xml() {
        return CFDI_xml;
    }

    public void setCFDI_xml(String cFDI_xml) {
        CFDI_xml = cFDI_xml;
    }

    public String getTfd() {
        return tfd;
    }

    public void setTfd(String tfd) {
        this.tfd = tfd;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getEstatus() {
        return estatus;
    }

    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }
}
