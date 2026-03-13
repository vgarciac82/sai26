package com.syc.admin;

/**
 * Tablero de control. Alerta Subejercicio
 */
public class TableroAlertaSubejercicio {

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
	 * @param unidadNormativa
	 *            Unidad Normativa
	 * @param totalCalendario
	 *            Total de registros en calendario
	 * @param totalCapturado
	 *            Total de registros capturados.
	 */
	public TableroAlertaSubejercicio(String unidadNormativa, int totalCalendario, int totalCapturado) {
		super();
		this.unidadNormativa = unidadNormativa;
		this.totalCalendario = totalCalendario;
		this.totalCapturado = totalCapturado;
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
