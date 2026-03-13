package com.syc.sai.contabilidad;

public abstract class CondicionCierreMes extends CondicionContable {

	String mensaje;

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}
}