package com.syc.sai.procesosAutomaticos;

import com.syc.sai.procesosAutomaticos.interfaces.MailSenderInterface;

/**
 * Envia notificaciones al momento de autorizar/cancelar un pago.
 * 
 * @author Propietario
 * 
 */
public class NotificacionPagos extends MailSenderInterface {

	private String	jniName	= "jdbc/gestion";

	/**
	 * @param ipHost
	 * @param port
	 * @param protocol
	 * @param from
	 * @param to
	 * @param message
	 */
	public NotificacionPagos(String jniName, String ipHost, int port, String protocol, String from, String to, String message) {
		super(ipHost, port, protocol, from, to, message);
		this.jniName = jniName;
	}

	/**
	 * @param ipHost
	 * @param port
	 * @param protocol
	 */
	public NotificacionPagos(String jniName, String ipHost, int port, String protocol) {
		super(ipHost, port, protocol);
		this.jniName = jniName;
	}

	/**
	 * 
	 */
	public NotificacionPagos() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.syc.sai.procesosAutomaticos.interfaces.MailSenderInterface#sendMail()
	 */
	@Override
	public boolean sendMail() {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.syc.sai.procesosAutomaticos.interfaces.MailSenderInterface#sendMail
	 * (java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public boolean sendMail(String from, String to, String message) {
		// TODO Auto-generated method stub
		return false;
	}

}
