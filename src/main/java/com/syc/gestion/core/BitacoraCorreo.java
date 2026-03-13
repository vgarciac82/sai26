package com.syc.gestion.core;


public class BitacoraCorreo {

	private int					id;
	private String				destinatarios;
	private String				mensaje;
	private String				subject;
	private String				estatus;
	private java.sql.Timestamp	fechaEnvio;

	// Getters y Setters
	public int getId() {
		return id;
	}

	public void setId( int id ) {
		this.id = id;
	}

	public String getDestinatarios() {
		return destinatarios;
	}

	public void setDestinatarios( String destinatarios ) {
		this.destinatarios = destinatarios;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje( String mensaje ) {
		this.mensaje = mensaje;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject( String subject ) {
		this.subject = subject;
	}

	public String getEstatus() {
		return estatus;
	}

	public void setEstatus( String estatus ) {
		this.estatus = estatus;
	}

	public java.sql.Timestamp getFechaEnvio() {
		return fechaEnvio;
	}

	public void setFechaEnvio( java.sql.Timestamp fechaEnvio ) {
		this.fechaEnvio = fechaEnvio;
	}
}
