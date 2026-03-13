package com.syc.js.core;

public class QueryException extends Exception {

	public static final long serialVersionUID = 1L;

	public QueryException() {
		super();
	}

	public QueryException(String msg) {
		super(msg);
	}

	public QueryException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public QueryException(Throwable cause) {
		super(cause);
	}
}
