package com.axtel.contratos.core;
/**
 * Contrato EP. 
 * 
 * @author vicente.garcia
 *
 */
public class ContratoEP {

	private String	ejercicio;
	private String	idContrato;
	private String	tipoContrato;
	private String	ep;
	private String	centroContable;

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
	 * @return the ep
	 */
	public String getEp() {
		return ep;
	}

	/**
	 * @param ep
	 *            the ep to set
	 */
	public void setEp( String ep ) {
		this.ep = ep;
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

}
