package com.syc.fortimax.core;

import java.io.Serializable;

public class ListaImaxfile implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String consecutivo = null;
	private String valor = null;

	public ListaImaxfile(String consecutivo, String valor) {
		this.consecutivo = consecutivo;
		this.valor = valor;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

	public String getConsecutivo() {
		return consecutivo;
	}

	public void setConsecutivo(String consecutivo) {
		this.consecutivo = consecutivo;
	}
}
