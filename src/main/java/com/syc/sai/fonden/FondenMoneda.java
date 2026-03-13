package com.syc.sai.fonden;

import java.util.HashSet;
import java.util.Set;

/**
 * TfondenMoneda entity. @author MyEclipse Persistence Tools
 */

public class FondenMoneda implements java.io.Serializable {

	// Fields

	private String cidMoneda;
	private String cdescripcion;
	private Double ntipoCambio;


	// Constructors

	/** default constructor */
	public FondenMoneda() {
	}

	/** minimal constructor */
	public FondenMoneda(String cidMoneda) {
		this.cidMoneda = cidMoneda;
	}

	/** full constructor */
	public FondenMoneda(String cidMoneda, String cdescripcion,
			Double ntipoCambio, Set tfondenMovimientos) {
		this.cidMoneda = cidMoneda;
		this.cdescripcion = cdescripcion;
		this.ntipoCambio = ntipoCambio;
		
	}

	// Property accessors

	public String getCidMoneda() {
		return this.cidMoneda;
	}

	public void setCidMoneda(String cidMoneda) {
		this.cidMoneda = cidMoneda;
	}

	public String getCdescripcion() {
		return this.cdescripcion;
	}

	public void setCdescripcion(String cdescripcion) {
		this.cdescripcion = cdescripcion;
	}

	public Double getNtipoCambio() {
		return this.ntipoCambio;
	}

	public void setNtipoCambio(Double ntipoCambio) {
		this.ntipoCambio = ntipoCambio;
	}

}