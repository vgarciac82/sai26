package com.syc.contable.anteproyecto;

/**
 * Tablero de control. Representacion de una relacion UE-UN-Total
 * Calendario-Total Capturado
 * 
 * @author Vicente Garcia Carrillo
 * 
 */
public class TableroControlBean {

	/**
	 * Unidad Ejecutora
	 */
	private String	unidadEjecutora;
	/**
	 * Unidad Normativa
	 */
	private String	unidadNormativa;
	/**
	 * Total de registros en calendario para la convinacion UE/UN
	 */
	private int		totalCalendario;
	/**
	 * Total de registros capturados
	 */
	private int		totalCapturado;

	/**
	 * Construye una nueva instancia de este objeto
	 * 
	 * @param unidadEjecutora
	 *            Unidad Ejecutora
	 * @param unidadNormativa
	 *            Unidad Normativa
	 * @param totalCalendario
	 *            Total de registros en calendario
	 * @param totalCapturado
	 *            Total de registros capturados.
	 */
	public TableroControlBean(String unidadEjecutora, String unidadNormativa, int totalCalendario, int totalCapturado) {
		super();
		this.unidadEjecutora = unidadEjecutora;
		this.unidadNormativa = unidadNormativa;
		this.totalCalendario = totalCalendario;
		this.totalCapturado = totalCapturado;
	}

	/**
	 * @return the unidadEjecutora
	 */
	public String getUnidadEjecutora() {
		return unidadEjecutora;
	}

	/**
	 * @param unidadEjecutora
	 *            the unidadEjecutora to set
	 */
	public void setUnidadEjecutora(String unidadEjecutora) {
		this.unidadEjecutora = unidadEjecutora;
	}

	/**
	 * @return the unidadNormativa
	 */
	public String getUnidadNormativa() {
		return unidadNormativa;
	}

	/**
	 * @param unidadNormativa
	 *            the unidadNormativa to set
	 */
	public void setUnidadNormativa(String unidadNormativa) {
		this.unidadNormativa = unidadNormativa;
	}

	/**
	 * @return the totalCalendario
	 */
	public int getTotalCalendario() {
		return totalCalendario;
	}

	/**
	 * @param totalCalendario
	 *            the totalCalendario to set
	 */
	public void setTotalCalendario(int totalCalendario) {
		this.totalCalendario = totalCalendario;
	}

	/**
	 * @return the totalCapturado
	 */
	public int getTotalCapturado() {
		return totalCapturado;
	}

	/**
	 * @param totalCapturado
	 *            the totalCapturado to set
	 */
	public void setTotalCapturado(int totalCapturado) {
		this.totalCapturado = totalCapturado;
	}

}
