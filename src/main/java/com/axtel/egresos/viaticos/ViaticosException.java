package com.axtel.egresos.viaticos;


public class ViaticosException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ViaticosException( ) {
		super();
	}

	public ViaticosException( String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace ) {
		super( message, cause, enableSuppression, writableStackTrace );
	}

	public ViaticosException( String message, Throwable cause ) {
		super( message, cause );
	}

	public ViaticosException( String message ) {
		super( message );
	}

	public ViaticosException( Throwable cause ) {
		super( cause );
	}

}
