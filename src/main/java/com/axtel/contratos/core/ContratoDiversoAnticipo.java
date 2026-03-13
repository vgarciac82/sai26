package com.axtel.contratos.core;


import java.math.BigDecimal;
import java.util.Date;


public class ContratoDiversoAnticipo {

	private String		ejercicio;
	private String		idContrato;
	private String		tipoContrato;
	private String		centroContable;
	private int			idTipoAnticipoDiverso	= 1;
	private BigDecimal	importeAnticipo			= new BigDecimal( 0.0 );
	private BigDecimal	importeAnticipoIVA		= new BigDecimal( 0.0 );
	private BigDecimal	totalAnticipo			= new BigDecimal( 0.0 );
	private int			porcAmortizacion		= 0;
	private Date		fAnticipo				= new Date();
	private double		porcAsignacion			= 0;
	private BigDecimal	importeAmortizado		= new BigDecimal( 0.0 );

	/**
	 * @return the ejercicio
	 */
	public String getEjercicio() {
		return ejercicio;
	}

	/**
	 * @param ejercicio
	 *            the ejercicio to set
	 */
	public void setEjercicio( String ejercicio ) {
		this.ejercicio = ejercicio;
	}

	/**
	 * @return the idContrato
	 */
	public String getIdContrato() {
		return idContrato;
	}

	/**
	 * @param idContrato
	 *            the idContrato to set
	 */
	public void setIdContrato( String idContrato ) {
		this.idContrato = idContrato;
	}

	/**
	 * @return the tipoContrato
	 */
	public String getTipoContrato() {
		return tipoContrato;
	}

	/**
	 * @param tipoContrato
	 *            the tipoContrato to set
	 */
	public void setTipoContrato( String tipoContrato ) {
		this.tipoContrato = tipoContrato;
	}

	/**
	 * @return the centroContable
	 */
	public String getCentroContable() {
		return centroContable;
	}

	/**
	 * @param centroContable
	 *            the centroContable to set
	 */
	public void setCentroContable( String centroContable ) {
		this.centroContable = centroContable;
	}

	/**
	 * @return the idTipoAnticipoDiverso
	 */
	public int getIdTipoAnticipoDiverso() {
		return idTipoAnticipoDiverso;
	}

	/**
	 * @param idTipoAnticipoDiverso
	 *            the idTipoAnticipoDiverso to set
	 */
	public void setIdTipoAnticipoDiverso( int idTipoAnticipoDiverso ) {
		this.idTipoAnticipoDiverso = idTipoAnticipoDiverso;
	}

	/**
	 * @return the importeAnticipo
	 */
	public BigDecimal getImporteAnticipo() {
		return importeAnticipo;
	}

	/**
	 * @param importeAnticipo
	 *            the importeAnticipo to set
	 */
	public void setImporteAnticipo( BigDecimal importeAnticipo ) {
		this.importeAnticipo = importeAnticipo;
	}

	/**
	 * @return the importeAnticipoIVA
	 */
	public BigDecimal getImporteAnticipoIVA() {
		return importeAnticipoIVA;
	}

	/**
	 * @param importeAnticipoIVA
	 *            the importeAnticipoIVA to set
	 */
	public void setImporteAnticipoIVA( BigDecimal importeAnticipoIVA ) {
		this.importeAnticipoIVA = importeAnticipoIVA;
	}

	/**
	 * @return the totalAnticipo
	 */
	public BigDecimal getTotalAnticipo() {
		return totalAnticipo;
	}

	/**
	 * @param totalAnticipo
	 *            the totalAnticipo to set
	 */
	public void setTotalAnticipo( BigDecimal totalAnticipo ) {
		this.totalAnticipo = totalAnticipo;
	}

	/**
	 * @return the porcAmortizacion
	 */
	public int getPorcAmortizacion() {
		return porcAmortizacion;
	}

	/**
	 * @param porcAmortizacion
	 *            the porcAmortizacion to set
	 */
	public void setPorcAmortizacion( int porcAmortizacion ) {
		this.porcAmortizacion = porcAmortizacion;
	}

	/**
	 * @return the fAnticipo
	 */
	public Date getfAnticipo() {
		return fAnticipo;
	}

	/**
	 * @param fAnticipo
	 *            the fAnticipo to set
	 */
	public void setfAnticipo( Date fAnticipo ) {
		this.fAnticipo = fAnticipo;
	}

	/**
	 * @return the porcAsignacion
	 */
	public double getPorcAsignacion() {
		return porcAsignacion;
	}

	/**
	 * @param porcAsignacion
	 *            the porcAsignacion to set
	 */
	public void setPorcAsignacion( double porcAsignacion ) {
		this.porcAsignacion = porcAsignacion;
	}

	/**
	 * @return the importeAmortizado
	 */
	public BigDecimal getImporteAmortizado() {
		return importeAmortizado;
	}

	/**
	 * @param importeAmortizado
	 *            the importeAmortizado to set
	 */
	public void setImporteAmortizado( BigDecimal importeAmortizado ) {
		this.importeAmortizado = importeAmortizado;
	}

}
