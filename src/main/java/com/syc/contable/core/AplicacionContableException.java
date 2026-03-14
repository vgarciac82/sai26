package com.syc.contable.core;

import java.util.Base64;

public class AplicacionContableException extends Exception {

    private static final long serialVersionUID = -2980783174282719937L;

    public AplicacionContableException(String message) {
        super(message);
    }

    public AplicacionContableException(Throwable cause) {
        super(cause);
    }

    public AplicacionContableException(String message, Throwable cause) {
        super(message, cause);
    }
}
