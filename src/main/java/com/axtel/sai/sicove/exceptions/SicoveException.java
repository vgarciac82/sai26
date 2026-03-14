package com.axtel.sai.sicove.exceptions;

import java.util.Base64;

public class SicoveException extends Exception {

    private static final long serialVersionUID = 2843948880022083689L;

    public SicoveException() {
        super();
    }

    public SicoveException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public SicoveException(String message, Throwable cause) {
        super(message, cause);
    }

    public SicoveException(String message) {
        super(message);
    }

    public SicoveException(Throwable cause) {
        super(cause);
    }
}
