package com.syc.sai.contabilidad.polizamanual;

import java.util.Base64;

public class EventoManualEngineException extends Exception {

    public EventoManualEngineException(String message) {
        super(message);
    }

    public EventoManualEngineException(Throwable cause) {
        super(cause);
    }

    public EventoManualEngineException(String message, Throwable cause) {
        super(message, cause);
    }
}
