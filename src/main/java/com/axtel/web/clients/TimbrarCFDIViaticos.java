package com.axtel.web.clients;

import java.util.Base64;

public class TimbrarCFDIViaticos {

    String action = "timbrarCFDI";

    int periodo;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public int getPeriodo() {
        return periodo;
    }

    public void setPeriodo(int periodo) {
        this.periodo = periodo;
    }
}
