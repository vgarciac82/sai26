package com.syc.admin.servlet;

public class ReportsException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ReportsException(String msg) {
		super(msg);
	}

	public ReportsException(Throwable throwable) {
		super(throwable);
	}

	public ReportsException(String msg, Throwable throwable) {
		super(msg, throwable);
	}
}
