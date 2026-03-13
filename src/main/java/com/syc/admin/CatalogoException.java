package com.syc.admin;

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
