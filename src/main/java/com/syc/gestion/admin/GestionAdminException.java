package com.syc.gestion.admin;

import java.util.Base64;

public class GestionAdminException extends Exception {

    public static final long serialVersionUID = 1L;

    public GestionAdminException() {
        super();
    }

    public GestionAdminException(String msg) {
        super(msg);
    }

    public GestionAdminException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public GestionAdminException(Throwable cause) {
        super(cause);
    }
}
