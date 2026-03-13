package com.axtel.cfdi.exceptions;

public class FileManagmentException extends Exception {

	private static final long serialVersionUID = -6601692684555040268L;

	public FileManagmentException() {
		super();
	}

	public FileManagmentException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public FileManagmentException(String message, Throwable cause) {
		super(message, cause);
	}

	public FileManagmentException(String message) {
		super(message);
	}

	public FileManagmentException(Throwable cause) {
		super(cause);
	}

}
