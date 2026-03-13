package com.axtel.presupuesto;


import java.math.BigDecimal;


public class AdecuacionDetalleResumen {

	private String		entidadFederativa;
	private int			folio;
	private int			mes;
	private BigDecimal	montoMovimiento;
	private String		programa;
	private String		tipoMovimiento;
	private String		unidadEjecutora;
	private String		ordenIndicador;
	
	public String getOrdenIndicador() {
		return ordenIndicador;
	}

	
	public void setOrdenIndicador( String ordenIndicador ) {
		this.ordenIndicador = ordenIndicador;
	}

	/**
	 * 
	 */
	public AdecuacionDetalleResumen( ) {
		super();
	}

	/**
	 * @return the entidadFederativa
	 */
	public String getEntidadFederativa() {
		return entidadFederativa;
	}

	/**
	 * @return the folio
	 */
	public int getFolio() {
		return folio;
	}

	public int getMes() {
		return mes;
	}

	/**
	 * @return the montoMovimiento
	 */
	public BigDecimal getMontoMovimiento() {
		return montoMovimiento;
	}

	/**
	 * @return the programa
	 */
	public String getPrograma() {
		return programa;
	}

	/**
	 * @return the tipoMovimiento
	 */
	public String getTipoMovimiento() {
		return tipoMovimiento;
	}

	/**
	 * @return the unidadEjecutora
	 */
	public String getUnidadEjecutora() {
		return unidadEjecutora;
	}

	/**
	 * @param entidadFederativa
	 *            the entidadFederativa to set
	 */
	public void setEntidadFederativa( String entidadFederativa ) {
		this.entidadFederativa = entidadFederativa;
	}

	/**
	 * @param folio
	 *            the folio to set
	 */
	public void setFolio( int folio ) {
		this.folio = folio;
	}

	public void setMes( int mes ) {
		this.mes = mes;
	}

	/**
	 * @param montoMovimiento
	 *            the montoMovimiento to set
	 */
	public void setMontoMovimiento( BigDecimal montoMovimiento ) {
		this.montoMovimiento = montoMovimiento;
	}

	/**
	 * @param programa
	 *            the programa to set
	 */
	public void setPrograma( String programa ) {
		this.programa = programa;
	}

	/**
	 * @param tipoMovimiento
	 *            the tipoMovimiento to set
	 */
	public void setTipoMovimiento( String tipoMovimiento ) {
		this.tipoMovimiento = tipoMovimiento;
	}

	/**
	 * @param unidadEjecutora
	 *            the unidadEjecutora to set
	 */
	public void setUnidadEjecutora( String unidadEjecutora ) {
		this.unidadEjecutora = unidadEjecutora;
	}

	@Override
	public String toString() {
		return "AdecuacionDetalleResumen [entidadFederativa=" + entidadFederativa + ", folio=" + folio + ", montoMovimiento=" + montoMovimiento + ", programa=" + programa + ", tipoMovimiento=" + tipoMovimiento + ", unidadEjecutora=" + unidadEjecutora + "]";
	}

}
