package com.axtel.cfdi.stamp.core;

import java.util.Base64;

public class InvoiceRequest {

    private String codigoPostal;

    private int regimenFiscal;

    private int folioCFDI;

    private String formaDePago;

    public void setFormaDePago(String formaDePago) {
        this.formaDePago = formaDePago;
    }

    public String getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    public int getRegimenFiscal() {
        return regimenFiscal;
    }

    public void setRegimenFiscal(int regimenFiscal) {
        this.regimenFiscal = regimenFiscal;
    }

    public int getFolioCFDI() {
        return folioCFDI;
    }

    public void setFolioCFDI(int folioCFDI) {
        this.folioCFDI = folioCFDI;
    }

    @Override
    public String toString() {
        return "CFDIRequest [codigoPostal=" + codigoPostal + ", regimenFiscal=" + regimenFiscal + ", folioCFDI=" + folioCFDI + "]";
    }

    public String getFormaDePago() {
        return formaDePago;
    }
}
