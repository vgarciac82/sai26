package com.axtel.presupuesto.catalogo;


public class ProgramaPresupuestario {

	private String	programaPresuestario;
	private String	nombreProgramaPresuestario;

	/**
	 * @return the programaPresuestario
	 */
	public String getProgramaPresuestario() {
		return programaPresuestario;
	}

	/**
	 * @param programaPresuestario
	 *            the programaPresuestario to set
	 */
	public void setProgramaPresuestario( String programaPresuestario ) {
		this.programaPresuestario = programaPresuestario;
	}

	/**
	 * @return the nombreProgramaPresuestario
	 */
	public String getNombreProgramaPresuestario() {
		return nombreProgramaPresuestario;
	}

	/**
	 * @param nombreProgramaPresuestario
	 *            the nombreProgramaPresuestario to set
	 */
	public void setNombreProgramaPresuestario( String nombreProgramaPresuestario ) {
		this.nombreProgramaPresuestario = nombreProgramaPresuestario;
	}

	@Override
	public String toString() {
		return "ProgramaPresupuestario [programaPresuestario=" + programaPresuestario + ", nombreProgramaPresuestario=" + nombreProgramaPresuestario + "]";
	}

	/**
	 * @param programaPresuestario
	 * @param nombreProgramaPresuestario
	 */
	public ProgramaPresupuestario( String programaPresuestario, String nombreProgramaPresuestario ) {
		super();
		this.programaPresuestario = programaPresuestario;
		this.nombreProgramaPresuestario = nombreProgramaPresuestario;
	}

}
