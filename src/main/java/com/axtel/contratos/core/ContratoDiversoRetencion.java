package com.axtel.contratos.core;


public class ContratoDiversoRetencion {

	private String	ejercicio;
	private String	centroContable;
	private String	idContrato;
	private int		idTipoRetencion;

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
	 * @return the idTipoRetencion
	 */
	public int getIdTipoRetencion() {
		return idTipoRetencion;
	}

	/**
	 * @param idTipoRetencion
	 *            the idTipoRetencion to set
	 */
	public void setIdTipoRetencion( int idTipoRetencion ) {
		this.idTipoRetencion = idTipoRetencion;
	}

	@Override
	public String toString() {
		return "ContratoDiversoRetencion [ejercicio=" + ejercicio + ", centroContable=" + centroContable + ", idContrato=" + idContrato + ", idTipoRetencion=" + idTipoRetencion + "]";
	}

}
