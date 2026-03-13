package com.syc.sai.contabilidad.polizamanual;

/**
 * TgrupoEvento entity. @author MyEclipse Persistence Tools
 */

public class GrupoEvento implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer nidGrupoEvento;
	private String cnombreGrupo;



	public Integer getNidGrupoEvento() {
		return this.nidGrupoEvento;
	}

	public void setNidGrupoEvento(Integer nidGrupoEvento) {
		this.nidGrupoEvento = nidGrupoEvento;
	}

	public String getCnombreGrupo() {
		return this.cnombreGrupo;
	}

	public void setCnombreGrupo(String cnombreGrupo) {
		this.cnombreGrupo = cnombreGrupo;
	}

}