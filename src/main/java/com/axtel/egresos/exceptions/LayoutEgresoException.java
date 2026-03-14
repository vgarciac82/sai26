package com.axtel.egresos.exceptions;

import java.util.List;
import com.syc.gestion.util.Util;
import java.util.Base64;

public class LayoutEgresoException extends Exception {

    /**
     */
    private static final long serialVersionUID = 6833431499371340087L;

    public LayoutEgresoException() {
    }

    public LayoutEgresoException(String message) {
        super(message);
    }

    public LayoutEgresoException(Throwable cause) {
        super(cause);
    }

    public LayoutEgresoException(String message, Throwable cause) {
        super(message, cause);
    }

    public LayoutEgresoException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public LayoutEgresoException(List<String> errores) {
        this(Util.join(errores, '\n'));
    }
}
