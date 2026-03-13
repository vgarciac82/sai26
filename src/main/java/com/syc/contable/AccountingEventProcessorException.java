package com.syc.contable;

public class AccountingEventProcessorException extends Exception {

	private static final long serialVersionUID = 4464049807957434528L;

	public AccountingEventProcessorException(String message) {
		super(message);
	}

	public AccountingEventProcessorException(Throwable message) {
		super(message);
	}

	public AccountingEventProcessorException(String message, Throwable cause) {
		super(message, cause);
	}
}
