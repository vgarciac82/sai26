package com.syc.sai.firmaElectronica.exceptions;

import java.util.Base64;

public class NotEmptyDocumentException extends Exception {

    private static final long serialVersionUID = 7631594281462221812L;

    public NotEmptyDocumentException() {
        super();
    }

    public NotEmptyDocumentException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotEmptyDocumentException(String message) {
        super(message);
    }

    public NotEmptyDocumentException(Throwable cause) {
        super(cause);
    }
}
