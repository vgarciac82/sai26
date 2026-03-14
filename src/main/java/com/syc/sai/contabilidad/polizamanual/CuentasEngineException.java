package com.syc.sai.contabilidad.polizamanual;

import java.util.Base64;

public class CuentasEngineException extends Exception {

    public CuentasEngineException(String message) {
        super(message);
    }

    public CuentasEngineException(Throwable cause) {
        super(cause);
    }

    public CuentasEngineException(String message, Throwable cause) {
        super(message, cause);
    }
}
