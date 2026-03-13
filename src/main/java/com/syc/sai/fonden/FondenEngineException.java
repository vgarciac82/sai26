package com.syc.sai.fonden;

public class FondenEngineException extends Exception {

	private static final long serialVersionUID = 8264113602595941002L;

	public FondenEngineException(String message) {
		super(message);
	}

	public FondenEngineException(Throwable cause) {
		super(cause);
	}

	public FondenEngineException(String message, Throwable cause) {
		super(message, cause);
	}
}