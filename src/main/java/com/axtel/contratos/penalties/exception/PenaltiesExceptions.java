package com.axtel.contratos.penalties.exception;

import java.util.Base64;

public class PenaltiesExceptions extends Exception {

    private static final long serialVersionUID = -4348116258525307190L;

    public PenaltiesExceptions() {
        super();
    }

    public PenaltiesExceptions(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public PenaltiesExceptions(String message, Throwable cause) {
        super(message, cause);
    }

    public PenaltiesExceptions(String message) {
        super(message);
    }

    public PenaltiesExceptions(Throwable cause) {
        super(cause);
    }
}
