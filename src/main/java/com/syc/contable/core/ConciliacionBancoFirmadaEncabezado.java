package com.syc.contable.core;

public class ConciliacionBancoFirmadaEncabezado {
	private String	centroContable;
	private String	cuentaBancaria;

	private int		folioConciliacionFirmada;

	private String	folioSAI;
	private int		idGabinete;

	@Override
	public String toString() {
		return "ConciliacionBancoFirmadaEncabezado [centroContable=" + centroContable + ", cuentaBancaria=" + cuentaBancaria + ", folioConciliacionFirmada=" + folioConciliacionFirmada + ", folioSAI=" + folioSAI + ", idGabinete=" + idGabinete + "]";
	}

	public ConciliacionBancoFirmadaEncabezado(String centroContable, String cuentaBancaria, int folioConciliacionFirmada, String folioSAI, int idGabinete) {
		super();
		this.centroContable = centroContable;
		this.cuentaBancaria = cuentaBancaria;
		this.folioConciliacionFirmada = folioConciliacionFirmada;
		this.folioSAI = folioSAI;
		this.idGabinete = idGabinete;
	}

	public String getCentroContable() {
		return centroContable;
	}

	public String getCuentaBancaria() {
		return cuentaBancaria;
	}

	public int getFolioConciliacionFirmada() {
		return folioConciliacionFirmada;
	}

	public String getFolioSAI() {
		return folioSAI;
	}

	public int getIdGabinete() {
		return idGabinete;
	}

	public void setCentroContable(String centroContable) {
		this.centroContable = centroContable;
	}

	public void setCuentaBancaria(String cuentaBancaria) {
		this.cuentaBancaria = cuentaBancaria;
	}

	public void setFolioConciliacionFirmada(int folioConciliacionFirmada) {
		this.folioConciliacionFirmada = folioConciliacionFirmada;
	}

	public void setFolioSAI(String folioSAI) {
		this.folioSAI = folioSAI;
	}

	public void setIdGabinete(int idGabinete) {
		this.idGabinete = idGabinete;
	}

}
