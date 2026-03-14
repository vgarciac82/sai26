package com.axtel.web.clients;

import java.util.Base64;

public class InvoiceDTO {

    String action = "generarCFDI";

    ViaticoCFDI viaticoCFDI;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public ViaticoCFDI getViaticoCFDI() {
        return viaticoCFDI;
    }

    public void setViaticoCFDI(ViaticoCFDI viaticoCFDI) {
        this.viaticoCFDI = viaticoCFDI;
    }
}
