package com.syc.admin.servlet;

public class AdminCatalogoException extends Exception {

	public static final long serialVersionUID = 1L;

	public AdminCatalogoException() {
		super();
	}

	public AdminCatalogoException(String msg) {
		super(msg);
	}

	public AdminCatalogoException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public AdminCatalogoException(Throwable cause) {
		super(cause);
	}
}
