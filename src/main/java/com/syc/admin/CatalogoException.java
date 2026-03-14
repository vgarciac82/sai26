package com.syc.admin;

import java.util.Base64;

public class CatalogoException extends Exception {

    public static final long serialVersionUID = 1L;

    public CatalogoException() {
        super();
    }

    public CatalogoException(String msg) {
        super(msg);
    }

    public CatalogoException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public CatalogoException(Throwable cause) {
        super(cause);
    }
}
