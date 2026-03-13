package com.syc.fortimax.exceptions;


public class FortimaxException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2207621505102817630L;

	public FortimaxException( ) {
	}

	public FortimaxException( String message ) {
		super( message );
	}

	public FortimaxException( Throwable cause ) {
		super( cause );
	}

	public FortimaxException( String message, Throwable cause ) {
		super( message, cause );
	}

	public FortimaxException( String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace ) {
		super( message, cause, enableSuppression, writableStackTrace );
	}

}
