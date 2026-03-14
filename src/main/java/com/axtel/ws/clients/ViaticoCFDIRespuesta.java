package com.axtel.ws.clients;

import java.util.Base64;

public class ViaticoCFDIRespuesta {

    private int periodo;

    private String message;

    private boolean result;

    public int getPeriodo() {
        return periodo;
    }

    public void setPeriodo(int periodo) {
        this.periodo = periodo;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }
}
