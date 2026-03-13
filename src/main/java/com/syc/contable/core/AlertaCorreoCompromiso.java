package com.syc.contable.core;

public class AlertaCorreoCompromiso {
	private String	message;
	private String	to;

	public AlertaCorreoCompromiso(String message, String to) {
		super();
		this.message = message;
		this.to = to;
	}

	public String getMessage() {
		return message;
	}

	public String getTo() {
		return to;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setTo(String to) {
		this.to = to;
	}
}
