package com.axtel.proveedores.exception;

import java.util.Base64;

public class ProveedorException extends Exception {

    /**
     */
    private static final long serialVersionUID = -1483418378280478424L;

    /**
     */
    public ProveedorException() {
        super();
    }

    /**
     * @param message
     * @param cause
     * @param enableSuppression
     * @param writableStackTrace
     */
    public ProveedorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    /**
     * @param message
     * @param cause
     */
    public ProveedorException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     */
    public ProveedorException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public ProveedorException(Throwable cause) {
        super(cause);
    }
}
