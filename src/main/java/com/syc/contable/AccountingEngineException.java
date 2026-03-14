package com.syc.contable;

import java.util.Base64;

public class AccountingEngineException extends Exception {

    private static final long serialVersionUID = 8264113602595941002L;

    public AccountingEngineException(String message) {
        super(message);
    }

    public AccountingEngineException(Throwable cause) {
        super(cause);
    }

    public AccountingEngineException(String message, Throwable cause) {
        super(message, cause);
    }
}
