package com.axtel.gestion.exception;


public class DocumentTreeException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public DocumentTreeException () {
		super ();
	}
	
	public DocumentTreeException (String msg) {
		super (msg);
	}
	
	public DocumentTreeException (Throwable cause) {
		super(cause);		
	}
	
	public DocumentTreeException ( String msg, Throwable cause) {
		super (msg, cause);		
	}
	
	public DocumentTreeException ( String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace ) {
		super( message, cause, enableSuppression, writableStackTrace );
	}
	

}
