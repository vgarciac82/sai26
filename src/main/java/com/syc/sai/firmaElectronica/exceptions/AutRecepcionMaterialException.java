package com.syc.sai.firmaElectronica.exceptions;

import java.util.Base64;

/**
 * Excepcion ocurrida mientras se realiza el proceso de firma electronica de una
 * recepcion de material.
 *
 * @author vicente.garcia
 */
public class AutRecepcionMaterialException extends Exception {

    /**
     */
    private static final long serialVersionUID = 5318726548254632522L;

    /**
     */
    public AutRecepcionMaterialException() {
        super();
    }

    /**
     * @param message
     * @param cause
     * @param enableSuppression
     * @param writableStackTrace
     */
    public AutRecepcionMaterialException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    /**
     * @param message
     * @param cause
     */
    public AutRecepcionMaterialException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     */
    public AutRecepcionMaterialException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public AutRecepcionMaterialException(Throwable cause) {
        super(cause);
    }
}
