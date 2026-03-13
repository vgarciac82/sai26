package com.syc.sai.fonden;

import java.sql.Date;

/**
 * Tfonden entity. @author MyEclipse Persistence Tools
 */

public class Fonden implements java.io.Serializable {

	// Fields

	private Integer cidFonden;
	private String cusuarioCreador;
	private Date dfechaCaptura;
	private String cdescripcion;
	private Double nimporteAnual;

	// Constructors

	/** default constructor */
	public Fonden() {
	}

	/** minimal constructor */
	public Fonden(Integer cidFonden) {
		this.cidFonden = cidFonden;
	}

	/** full constructor */
	public Fonden(Integer cidFonden, String cusuarioCreador,
			Date dfechaCaptura, String cdescripcion, Double nimporteAnual) {
		this.cidFonden = cidFonden;
		this.cusuarioCreador = cusuarioCreador;
		this.dfechaCaptura = dfechaCaptura;
		this.cdescripcion = cdescripcion;
		this.nimporteAnual = nimporteAnual;
	}

	// Property accessors

	public Integer getCidFonden() {
		return this.cidFonden;
	}

	public void setCidFonden(Integer cidFonden) {
		this.cidFonden = cidFonden;
	}

	public String getCusuarioCreador() {
		return this.cusuarioCreador;
	}

	public void setCusuarioCreador(String cusuarioCreador) {
		this.cusuarioCreador = cusuarioCreador;
	}

	public Date getDfechaCaptura() {
		return this.dfechaCaptura;
	}

	public void setDfechaCaptura(Date dfechaCaptura) {
		this.dfechaCaptura = dfechaCaptura;
	}

	public String getCdescripcion() {
		return this.cdescripcion;
	}

	public void setCdescripcion(String cdescripcion) {
		this.cdescripcion = cdescripcion;
	}

	public Double getNimporteAnual() {
		return this.nimporteAnual;
	}

	public void setNimporteAnual(Double nimporteAnual) {
		this.nimporteAnual = nimporteAnual;
	}

}