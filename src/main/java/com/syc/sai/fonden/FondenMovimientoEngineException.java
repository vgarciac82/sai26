package com.syc.sai.fonden;

import java.util.Base64;

public class FondenMovimientoEngineException extends Exception {

    private static final long serialVersionUID = 8264113602595941002L;

    public FondenMovimientoEngineException(String message) {
        super(message);
    }

    public FondenMovimientoEngineException(Throwable cause) {
        super(cause);
    }

    public FondenMovimientoEngineException(String message, Throwable cause) {
        super(message, cause);
    }
}
