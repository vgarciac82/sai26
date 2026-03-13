package com.axtel.notifications;


public class CorreoNotificacion {

	private String	cuerpoCorreo;

	private String	destinatarios;

	/**
	 * 
	 */
	public CorreoNotificacion( ) {
		super();
	}

	/**
	 * @param cuerpoCorreo
	 * @param destinatarios
	 */
	public CorreoNotificacion( String cuerpoCorreo, String destinatarios ) {
		super();
		this.cuerpoCorreo = cuerpoCorreo;
		this.destinatarios = destinatarios;
	}

	/**
	 * @return the cuerpoCorreo
	 */
	public String getCuerpoCorreo() {
		return cuerpoCorreo;
	}

	/**
	 * @return the destinatarios
	 */
	public String getDestinatarios() {
		return destinatarios;
	}

	/**
	 * @param cuerpoCorreo
	 *            the cuerpoCorreo to set
	 */
	public void setCuerpoCorreo( String cuerpoCorreo ) {
		this.cuerpoCorreo = cuerpoCorreo;
	}

	/**
	 * @param destinatarios
	 *            the destinatarios to set
	 */
	public void setDestinatarios( String destinatarios ) {
		this.destinatarios = destinatarios;
	}

	@Override
	public String toString() {
		return "CorreoNotificacion [cuerpoCorreo=" + cuerpoCorreo + ", destinatarios=" + destinatarios + "]";
	}

}
