package com.axtel.egresos.exceptions;

import java.util.Base64;

public class RelacionRegimenRetencionException extends RuntimeException {

    /**
     */
    private static final long serialVersionUID = -2627172494903027219L;

    /**
     */
    public RelacionRegimenRetencionException() {
        super();
    }

    /**
     * @param message
     * @param cause
     * @param enableSuppression
     * @param writableStackTrace
     */
    public RelacionRegimenRetencionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    /**
     * @param message
     * @param cause
     */
    public RelacionRegimenRetencionException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     */
    public RelacionRegimenRetencionException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public RelacionRegimenRetencionException(Throwable cause) {
        super(cause);
    }
}
