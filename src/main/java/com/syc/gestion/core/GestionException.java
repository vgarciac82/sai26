package com.syc.gestion.core;

import java.util.Base64;

public class GestionException extends Exception {

    public static final long serialVersionUID = 1L;

    public GestionException() {
        super();
    }

    public GestionException(String msg) {
        super(msg);
    }

    public GestionException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public GestionException(Throwable cause) {
        super(cause);
    }
}
