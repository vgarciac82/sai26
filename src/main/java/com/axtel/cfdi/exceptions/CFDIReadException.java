package com.axtel.cfdi.exceptions;

import java.util.Base64;

/**
 * Excepcion ocurrida mientas se realiza la lectura del CFDI 3.0
 *
 * @author vicente.garcia
 */
public class CFDIReadException extends Exception {

    /**
     */
    private static final long serialVersionUID = 7315339923407253556L;

    /**
     */
    public CFDIReadException() {
    }

    /**
     * @param message
     */
    public CFDIReadException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public CFDIReadException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public CFDIReadException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     * @param cause
     * @param enableSuppression
     * @param writableStackTrace
     */
    public CFDIReadException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
