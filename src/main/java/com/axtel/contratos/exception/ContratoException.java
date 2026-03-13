/**
 * 
 */
package com.axtel.contratos.exception;


/**
 * @author vicente.garcia
 *
 */
public class ContratoException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7958822490186646601L;

	/**
	 * 
	 */
	public ContratoException( ) {
		super();
	}

	/**
	 * @param message
	 */
	public ContratoException( String message ) {
		super( message );
	}

	/**
	 * @param cause
	 */
	public ContratoException( Throwable cause ) {
		super( cause );
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ContratoException( String message, Throwable cause ) {
		super( message, cause );
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public ContratoException( String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace ) {
		super( message, cause, enableSuppression, writableStackTrace );
	}

}
