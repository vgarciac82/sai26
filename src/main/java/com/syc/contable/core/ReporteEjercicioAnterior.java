package com.syc.contable.core;

public class ReporteEjercicioAnterior 
{
	public ReporteEjercicioAnterior()
	{
		
	}

	public ReporteEjercicioAnterior(String cFuenteFinanciamiento, String cprograma_presupuestario, String saldoEjercido, String saldoModificado, String saldoOriginal, String ejercicio, String unidadEjecutora)
	{
		this.cFuenteFinanciamiento=cFuenteFinanciamiento; 
		this.cprograma_presupuestario=cprograma_presupuestario;
		this.saldoOriginal=saldoOriginal;
		this.saldoModificado=saldoModificado;
		this.saldoEjercido=saldoEjercido;
		this.ejercicio=ejercicio;
		this.unidadEjecutora=unidadEjecutora;
	}
	
	private String cFuenteFinanciamiento;
	private String cprograma_presupuestario;
	private String saldoEjercido;
	private String saldoModificado;
	private String saldoOriginal;
	private String ejercicio;
	private String unidadEjecutora;
	
	

	public String getCprograma_presupuestario() {
		return cprograma_presupuestario;
	}

	public void setCprograma_presupuestario(String cprograma_presupuestario) {
		this.cprograma_presupuestario = cprograma_presupuestario;
	}

	public String getEjercicio() {
		return ejercicio;
	}

	public void setEjercicio(String ejercicio) {
		this.ejercicio = ejercicio;
	}

	public String getUnidadEjecutora() {
		return unidadEjecutora;
	}

	public void setUnidadEjecutora(String unidadEjecutora) {
		this.unidadEjecutora = unidadEjecutora;
	}

	public String getcFuenteFinanciamiento() {
		return cFuenteFinanciamiento;
	}

	public void setcFuenteFinanciamiento(String cFuenteFinanciamiento) {
		this.cFuenteFinanciamiento = cFuenteFinanciamiento;
	}

	public String getsaldoEjercido() {
		return saldoEjercido;
	}

	public void setsaldoEjercido(String saldoEjercido) {
		this.saldoEjercido = saldoEjercido;
	}

	public String getsaldoModificado() {
		return saldoModificado;
	}

	public void setsaldoModificado(String saldoModificado) {
		this.saldoModificado = saldoModificado;
	}

	public String getSaldoOriginal() {
		return saldoOriginal;
	}

	public void setSaldoOriginal(String saldoOriginal) {
		this.saldoOriginal = saldoOriginal;
	}
	
	

}
