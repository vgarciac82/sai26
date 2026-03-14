package com.syc.obrapublica;

import java.util.Base64;

public class AdvertenciaCancelacionCorreo {

    private String message;

    private String to;

    public AdvertenciaCancelacionCorreo(String message, String to) {
        super();
        this.message = message;
        this.to = to;
    }

    public String getMessage() {
        return message;
    }

    public String getTo() {
        return to;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
