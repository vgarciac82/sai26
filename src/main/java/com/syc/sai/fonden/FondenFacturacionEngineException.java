package com.syc.sai.fonden;

import java.util.Base64;

public class FondenFacturacionEngineException extends Exception {

    private static final long serialVersionUID = 8264113602595941002L;

    public FondenFacturacionEngineException(String message) {
        super(message);
    }

    public FondenFacturacionEngineException(Throwable cause) {
        super(cause);
    }

    public FondenFacturacionEngineException(String message, Throwable cause) {
        super(message, cause);
    }
}
