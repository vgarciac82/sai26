package com.syc.contable.adecuaciones.Exception;

public class IncompletRowException extends Exception {

	private static final long	serialVersionUID	= -1214028493238126448L;

	public IncompletRowException() {
		super();
	}

	public IncompletRowException(String message, Throwable cause) {
		super(message, cause);
	}

	public IncompletRowException(String message) {
		super(message);
	}

	public IncompletRowException(Throwable cause) {
		super(cause);
	}

}
