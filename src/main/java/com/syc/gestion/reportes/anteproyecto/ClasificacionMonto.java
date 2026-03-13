package com.syc.gestion.reportes.anteproyecto;

import java.util.Map;

/**
 * Relacion cuenta/monto en el reporte de clasificacion economica.
 * 
 * @author Vicente Garcia Carrillo
 * 
 */
public class ClasificacionMonto {

	private int					tipoGasto;
	private String				descCapitulo;
	private Map<String, Double>	cuentaMonto;

	/**
	 * 
	 */
	public ClasificacionMonto() {
		super();
	}

	/**
	 * @param tipoGasto
	 * @param descCapitulo
	 * @param cuentaMonto
	 */
	public ClasificacionMonto(int tipoGasto, String descCapitulo, Map<String, Double> cuentaMonto) {
		super();
		this.tipoGasto = tipoGasto;
		this.descCapitulo = descCapitulo;
		this.cuentaMonto = cuentaMonto;
	}

	/**
	 * @return the tipoGasto
	 */
	public int getTipoGasto() {
		return tipoGasto;
	}

	/**
	 * @param tipoGasto
	 *            the tipoGasto to set
	 */
	public void setTipoGasto(int tipoGasto) {
		this.tipoGasto = tipoGasto;
	}

	/**
	 * @return the descCapitulo
	 */
	public String getDescCapitulo() {
		return descCapitulo;
	}

	/**
	 * @param descCapitulo
	 *            the descCapitulo to set
	 */
	public void setDescCapitulo(String descCapitulo) {
		this.descCapitulo = descCapitulo;
	}

	/**
	 * @return the cuentaMonto
	 */
	public Map<String, Double> getCuentaMonto() {
		return cuentaMonto;
	}

	/**
	 * @param cuentaMonto
	 *            the cuentaMonto to set
	 */
	public void setCuentaMonto(Map<String, Double> cuentaMonto) {
		this.cuentaMonto = cuentaMonto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ClasificacionMonto [tipoGasto=" + tipoGasto + ", descCapitulo=" + descCapitulo + ", cuentaMonto=" + cuentaMonto + "]";
	}

}