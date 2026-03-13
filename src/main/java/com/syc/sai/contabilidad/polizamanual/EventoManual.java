package com.syc.sai.contabilidad.polizamanual;

/**
 * TeventoManual entity. @author MyEclipse Persistence Tools
 */

public class EventoManual implements java.io.Serializable {

	// Fields	
	private String cevento;
	private Integer ndocRenglon;
	private Integer aejercicioFiscal;
	private Integer cidGrupoEvento;
	private Integer cidSubGrupoEvento;
	private String cidEventoManual;
	private Integer cpartida;
	private String ncuenta;

	public String getCevento() {
		return this.cevento;
	}

	public void setCevento(String cevento) {
		this.cevento = cevento;
	}

	public Integer getNdocRenglon() {
		return this.ndocRenglon;
	}

	public void setNdocRenglon(Integer ndocRenglon) {
		this.ndocRenglon = ndocRenglon;
	}

	public Integer getAejercicioFiscal() {
		return this.aejercicioFiscal;
	}

	public void setAejercicioFiscal(Integer aejercicioFiscal) {
		this.aejercicioFiscal = aejercicioFiscal;
	}

	public Integer getCidGrupoEvento() {
		return cidGrupoEvento;
	}

	public void setCidGrupoEvento(Integer cidGrupoEvento) {
		this.cidGrupoEvento = cidGrupoEvento;
	}

	public Integer getCidSubGrupoEvento() {
		return cidSubGrupoEvento;
	}

	public void setCidSubGrupoEvento(Integer cidSubGrupoEvento) {
		this.cidSubGrupoEvento = cidSubGrupoEvento;
	}

	public String getCidEventoManual() {
		return cidEventoManual;
	}

	public void setCidEventoManual(String cidEventoManual) {
		this.cidEventoManual = cidEventoManual;
	}

	public Integer getCpartida() {
		return cpartida;
	}

	public void setCpartida(Integer cpartida) {
		this.cpartida = cpartida;
	}

	public String getNcuenta() {
		return ncuenta;
	}

	public void setNcuenta(String ncuenta) {
		this.ncuenta = ncuenta;
	}

}