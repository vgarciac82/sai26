package com.axtel.cfdi;

import com.axtel.cfdi.core.DomicilioFiscal;
import java.util.Base64;

public class Emisor {

    private DomicilioFiscal domicilio;

    private String domicilioFiscal;

    private int emisorID;

    private String nombre;

    private String regimenFiscal;

    private String rfc;

    public Emisor() {
    }

    public Emisor(String rfc, String nombre, String regimenFiscal, String domicilioFiscal) {
        this.rfc = rfc;
        this.nombre = nombre;
        this.regimenFiscal = regimenFiscal;
        this.domicilioFiscal = domicilioFiscal;
    }

    public DomicilioFiscal getDomicilio() {
        return domicilio;
    }

    public String getDomicilioFiscal() {
        return domicilioFiscal;
    }

    public int getEmisorID() {
        return emisorID;
    }

    public String getNombre() {
        return nombre;
    }

    public String getRegimenFiscal() {
        return regimenFiscal;
    }

    public String getRfc() {
        return rfc;
    }

    public void setDomicilio(DomicilioFiscal domicilio) {
        this.domicilio = domicilio;
    }

    public void setDomicilioFiscal(String domicilioFiscal) {
        this.domicilioFiscal = domicilioFiscal;
    }

    public void setEmisorID(int emisorID) {
        this.emisorID = emisorID;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setRegimenFiscal(String regimenFiscal) {
        this.regimenFiscal = regimenFiscal;
    }

    public void setRfc(String rfc) {
        this.rfc = rfc;
    }

    @Override
    public String toString() {
        return "Emisor [domicilio=" + domicilio + ", domicilioFiscal=" + domicilioFiscal + ", emisorID=" + emisorID + ", nombre=" + nombre + ", regimenFiscal=" + regimenFiscal + ", rfc=" + rfc + "]";
    }
}
