package com.syc.contable;

import java.util.Base64;

public class PolizaException extends Exception {

    private static final long serialVersionUID = -1129912658779574368L;

    public PolizaException() {
        super();
    }

    public PolizaException(String message, Throwable cause) {
        super(message, cause);
    }

    public PolizaException(String message) {
        super(message);
    }

    public PolizaException(Throwable cause) {
        super(cause);
    }
}
