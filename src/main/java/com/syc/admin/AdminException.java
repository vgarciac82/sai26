package com.syc.admin;

public class AdminException extends Exception {

	public static final long serialVersionUID = 1L;

	public AdminException() {
		super();
	}

	public AdminException(String msg) {
		super(msg);
	}

	public AdminException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public AdminException(Throwable cause) {
		super(cause);
	}
}
