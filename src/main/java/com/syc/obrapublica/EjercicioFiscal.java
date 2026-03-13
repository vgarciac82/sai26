package com.syc.obrapublica;

public class EjercicioFiscal {

	private String	aEjercicioFiscal;
	private String	cActivo;

	public EjercicioFiscal(String aEjercicioFiscal, String cActivo) {
		super();
		this.aEjercicioFiscal = aEjercicioFiscal;
		this.cActivo = cActivo;
	}

	public String getaEjercicioFiscal() {
		return aEjercicioFiscal;
	}

	public String getcActivo() {
		return cActivo;
	}

	public void setaEjercicioFiscal(String aEjercicioFiscal) {
		this.aEjercicioFiscal = aEjercicioFiscal;
	}

	public void setcActivo(String cActivo) {
		this.cActivo = cActivo;
	}

}
