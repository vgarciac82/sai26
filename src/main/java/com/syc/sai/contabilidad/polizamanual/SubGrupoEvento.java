package com.syc.sai.contabilidad.polizamanual;

/**
 * TsubGrupoEvento entity. @author MyEclipse Persistence Tools
 */

public class SubGrupoEvento implements java.io.Serializable {

	// Fields

	private GrupoEvento tgrupoEvento;
	private String cnombreSubGrupo;
	private Integer nidSubGrupoEvento;
	private Integer nidGrupoEvento;

	// Constructors

	public GrupoEvento getTgrupoEvento() {
		return this.tgrupoEvento;
	}

	public void setTgrupoEvento(GrupoEvento tgrupoEvento) {
		this.tgrupoEvento = tgrupoEvento;
	}

	public String getCnombreSubGrupo() {
		return this.cnombreSubGrupo;
	}

	public void setCnombreSubGrupo(String cnombreSubGrupo) {
		this.cnombreSubGrupo = cnombreSubGrupo;
	}

	public Integer getNidSubGrupoEvento() {
		return nidSubGrupoEvento;
	}

	public void setNidSubGrupoEvento(Integer nidSubGrupoEvento) {
		this.nidSubGrupoEvento = nidSubGrupoEvento;
	}

	public Integer getNidGrupoEvento() {
		return nidGrupoEvento;
	}

	public void setNidGrupoEvento(Integer nidGrupoEvento) {
		this.nidGrupoEvento = nidGrupoEvento;
	}

}