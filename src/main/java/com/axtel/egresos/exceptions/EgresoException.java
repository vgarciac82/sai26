package com.axtel.egresos.exceptions;

import java.util.Base64;

public class EgresoException extends Exception {

    private static final long serialVersionUID = 8408163812685413751L;

    public EgresoException() {
    }

    public EgresoException(String message) {
        super(message);
    }

    public EgresoException(Throwable cause) {
        super(cause);
    }

    public EgresoException(String message, Throwable cause) {
        super(message, cause);
    }

    public EgresoException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
