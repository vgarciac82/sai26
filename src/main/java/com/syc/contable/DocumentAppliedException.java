package com.syc.contable;


public class DocumentAppliedException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1557900888919573459L;

	public DocumentAppliedException( String message ) {
		super( message );
	}

	public DocumentAppliedException( Throwable cause ) {
		super( cause );
	}

	public DocumentAppliedException( String message, Throwable cause ) {
		super( message, cause );
	}
}
