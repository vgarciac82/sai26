package com.syc.sai.firmaElectronica.exceptions;

import java.util.Base64;

public class FirmaElectronicaException extends Exception {

    /**
     */
    private static final long serialVersionUID = 5169770878805650019L;

    public FirmaElectronicaException() {
    }

    public FirmaElectronicaException(String message) {
        super(message);
    }

    public FirmaElectronicaException(Throwable cause) {
        super(cause);
    }

    public FirmaElectronicaException(String message, Throwable cause) {
        super(message, cause);
    }

    public FirmaElectronicaException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
