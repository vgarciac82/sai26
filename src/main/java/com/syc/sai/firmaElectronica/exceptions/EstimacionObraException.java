package com.syc.sai.firmaElectronica.exceptions;

import java.util.Base64;

public class EstimacionObraException extends Exception {

    private static final long serialVersionUID = 5318726548254632522L;

    public EstimacionObraException() {
        super();
    }

    public EstimacionObraException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public EstimacionObraException(String message, Throwable cause) {
        super(message, cause);
    }

    public EstimacionObraException(String message) {
        super(message);
    }

    public EstimacionObraException(Throwable cause) {
        super(cause);
    }
}
