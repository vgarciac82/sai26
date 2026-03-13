package com.axtel.services;


/**
 * Respuesta de error al ejecutar el servicio web.
 * 
 * @author vicente.garcia
 *
 */
public class ErrorResponse {

	/**
	 * Construye una nueva instancia del error
	 */
	public ErrorResponse( ) {

	}

	/**
	 * Mensaje de error
	 */
	private String messageError;

	/**
	 * @return the messageError
	 */
	public String getMessageError() {
		return messageError;
	}

	/**
	 * @param messageError
	 *            the messageError to set
	 */
	public void setMessageError( String messageError ) {
		this.messageError = messageError;
	}

	@Override
	public String toString() {
		return "ErrorResponse [messageError=" + messageError + "]";
	}

	public ErrorResponse( String messageError ) {
		super();
		this.messageError = messageError;
	}

}
