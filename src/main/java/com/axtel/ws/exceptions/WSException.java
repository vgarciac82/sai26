package com.axtel.ws.exceptions;


public class WSException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4808455725234561079L;

	/**
	 * 
	 */
	public WSException( ) {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public WSException( String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace ) {
		super( message, cause, enableSuppression, writableStackTrace );
	}

	/**
	 * @param message
	 * @param cause
	 */
	public WSException( String message, Throwable cause ) {
		super( message, cause );
	}

	/**
	 * @param message
	 */
	public WSException( String message ) {
		super( message );
	}

	/**
	 * @param cause
	 */
	public WSException( Throwable cause ) {
		super( cause );
	}

}
