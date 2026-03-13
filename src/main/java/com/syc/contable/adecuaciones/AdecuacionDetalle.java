package com.syc.contable.adecuaciones;

import java.io.Serializable;
import java.util.List;

public class AdecuacionDetalle implements Serializable {

	private static final long	serialVersionUID	= -1499227133082953192L;
	private String				claveInterna;
	private String				claveSIAFF;
	private List<Double>		montos;
	private int					secuencia;
	private String				tipo;
	private String				cEvento;
	private String				EP;
	private int					nFolioAdecuacion;
	private String				proyecto;

	public String getProyecto() {
		return proyecto;
	}

	public void setProyecto(String proyecto) {
		this.proyecto = proyecto;
	}

	public int getnFolioAdecuacion() {
		return nFolioAdecuacion;
	}

	public void setnFolioAdecuacion(int nFolioAdecuacion) {
		this.nFolioAdecuacion = nFolioAdecuacion;
	}

	public String getEP() {
		return EP;
	}

	public void setEP(String eP) {
		EP = eP;
		if (getEP() != null && getEP().length() == 63) {
			setClaveSIAFF(getEP().substring(0, 55));
			setClaveInterna(getEP().substring(56));
		}

	}

	public String getcEvento() {
		return cEvento;
	}

	public void setcEvento(String cEvento) {
		this.cEvento = cEvento;
	}

	/**
	 * @param claveInterna
	 * @param claveSIAFF
	 * @param montos
	 * @param secuencia
	 * @param tipo
	 */
	public AdecuacionDetalle(String claveInterna, String claveSIAFF, List<Double> montos, int secuencia, String tipo) {
		super();
		this.claveInterna = claveInterna;
		this.claveSIAFF = claveSIAFF;
		this.montos = montos;
		this.secuencia = secuencia;
		this.tipo = tipo;
	}

	/**
	 * 
	 */
	public AdecuacionDetalle() {
		super();
	}

	/**
	 * @return the claveInterna
	 */
	public String getClaveInterna() {
		return claveInterna;
	}

	/**
	 * @return the claveSIAFF
	 */
	public String getClaveSIAFF() {
		return claveSIAFF;
	}

	/**
	 * @return the montos
	 */
	public List<Double> getMontos() {
		return montos;
	}

	/**
	 * @return the secuencia
	 */
	public int getSecuencia() {
		return secuencia;
	}

	/**
	 * @return the tipo
	 */
	public String getTipo() {
		return tipo;
	}

	/**
	 * @param claveInterna
	 *            the claveInterna to set
	 */
	public void setClaveInterna(String claveInterna) {
		this.claveInterna = claveInterna;
	}

	/**
	 * @param claveSIAFF
	 *            the claveSIAFF to set
	 */
	public void setClaveSIAFF(String claveSIAFF) {
		this.claveSIAFF = claveSIAFF;
	}

	/**
	 * @param montos
	 *            the montos to set
	 */
	public void setMontos(List<Double> montos) {
		this.montos = montos;
	}

	/**
	 * @param secuencia
	 *            the secuencia to set
	 */
	public void setSecuencia(int secuencia) {
		this.secuencia = secuencia;
	}

	/**
	 * @param tipo
	 *            the tipo to set
	 */
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AdecuacionDetalle [claveInterna=" + claveInterna + ", claveSIAFF=" + claveSIAFF + ", montos=" + montos + ", secuencia=" + secuencia + ", tipo=" + tipo + "]";
	}

}
