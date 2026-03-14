package com.axtel.ws.clients;

import java.util.Base64;

public class RespuestaTimbradoCFDI {

    String message = "";

    boolean result = false;

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
