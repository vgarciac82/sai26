package com.syc.contable.core;

import java.util.Base64;

public class URInaccesibleException extends Exception {

    private static final long serialVersionUID = -5543115649060208868L;

    public URInaccesibleException() {
        super();
    }

    public URInaccesibleException(String message) {
        super(message);
    }

    public URInaccesibleException(Throwable cause) {
        super(cause);
    }

    public URInaccesibleException(String message, Throwable cause) {
        super(message, cause);
    }
}
