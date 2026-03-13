package com.syc.gestion.util;

public class Condicion {

	private String	nombreCampo;
	private String	operador;
	private String	valor;

	public Condicion(String nombreCampo, String operador, String valor) {
		super();
		this.nombreCampo = nombreCampo;
		this.operador = operador;
		this.valor = valor;
	}

	public String getNombreCampo() {
		return nombreCampo;
	}

	public void setNombreCampo(String nombreCampo) {
		this.nombreCampo = nombreCampo;
	}

	public String getOperador() {
		return operador;
	}

	public void setOperador(String operador) {
		this.operador = operador;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

	@Override
	public String toString() {
		return "Condicion [nombreCampo=" + nombreCampo + ", operador=" + operador + ", valor=" + valor + "]";
	}

}
