package com.axtel.presupuesto;


public class RespuestaAfectaMetas {

	private String	acciones;
	private String	afectaMetas;
	private int		folioAdecuacion;

	/**
	 * @return the acciones
	 */
	public String getAcciones() {
		return acciones;
	}

	/**
	 * @return the afectaMetas
	 */
	public String getAfectaMetas() {
		return afectaMetas;
	}

	/**
	 * @return the folioAdecuacion
	 */
	public int getFolioAdecuacion() {
		return folioAdecuacion;
	}

	/**
	 * @param acciones
	 *            the acciones to set
	 */
	public void setAcciones( String acciones ) {
		this.acciones = acciones;
	}

	/**
	 * @param afectaMetas
	 *            the afectaMetas to set
	 */
	public void setAfectaMetas( String afectaMetas ) {
		this.afectaMetas = afectaMetas;
	}

	/**
	 * @param folioAdecuacion
	 *            the folioAdecuacion to set
	 */
	public void setFolioAdecuacion( int folioAdecuacion ) {
		this.folioAdecuacion = folioAdecuacion;
	}

	@Override
	public String toString() {
		return "RespuestaAfectaMetas [afectaMetas=" + afectaMetas + ", acciones=" + acciones + ", folioAdecuacion=" + folioAdecuacion + "]";
	}

}
