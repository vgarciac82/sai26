package com.axtel.contratos.core;


import java.math.BigDecimal;


public class ConvenioColaboracionDetalle {

	private String		ep;
	private int			folioConvenioColaboracion;
	private BigDecimal	importe;
	private int			mes;

	/**
	 * @return the ep
	 */
	public String getEp() {
		return ep;
	}

	/**
	 * @return the folioConvenioColaboracion
	 */
	public int getFolioConvenioColaboracion() {
		return folioConvenioColaboracion;
	}

	/**
	 * @return the importe
	 */
	public BigDecimal getImporte() {
		return importe;
	}

	/**
	 * @return the mes
	 */
	public int getMes() {
		return mes;
	}

	/**
	 * @param ep
	 *            the ep to set
	 */
	public void setEp( String ep ) {
		this.ep = ep;
	}

	/**
	 * @param folioConvenioColaboracion
	 *            the folioConvenioColaboracion to set
	 */
	public void setFolioConvenioColaboracion( int folioConvenioColaboracion ) {
		this.folioConvenioColaboracion = folioConvenioColaboracion;
	}

	/**
	 * @param importe
	 *            the importe to set
	 */
	public void setImporte( BigDecimal importe ) {
		this.importe = importe;
	}

	/**
	 * @param mes
	 *            the mes to set
	 */
	public void setMes( int mes ) {
		this.mes = mes;
	}

	@Override
	public String toString() {
		return "ConvenioColaboracionDetalle [folioConvenioColaboracion=" + folioConvenioColaboracion + ", ep=" + ep + ", importe=" + importe + ", mes=" + mes + "]";
	}

}
