package com.axtel.web.exceptions;

/**
 * Excepcion lanzada al terminar la session e intentar acceder a algun modulo
 * del sistema
 * 
 * @author vicente.garcia
 *
 */
public class SessionExpiredException extends Exception {

	private static final long serialVersionUID = 2421032716499972498L;

	public SessionExpiredException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public SessionExpiredException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public SessionExpiredException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public SessionExpiredException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public SessionExpiredException(Throwable cause) {
		super(cause);
	}

}
