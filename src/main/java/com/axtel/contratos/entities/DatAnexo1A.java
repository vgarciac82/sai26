package com.axtel.contratos.entities;


public class DatAnexo1A {
	private int nDiaformalizacion;
	private String cMesFormalizacion;
	private String cAnioFormalizacion;
	private String cDeclaraccion;
	private String cDescripcionServicio;
	private String cInmueble;
	private String cEjercicioPago;
	
	public int getnDiaformalizacion() {
		return nDiaformalizacion;
	}
	
	public void setnDiaformalizacion( int nDiaformalizacion ) {
		this.nDiaformalizacion = nDiaformalizacion;
	}
	
	public String getcMesFormalizacion() {
		return cMesFormalizacion;
	}
	
	public void setcMesFormalizacion( String cMesFormalizacion ) {
		this.cMesFormalizacion = cMesFormalizacion;
	}
	
	public String getcAnioFormalizacion() {
		return cAnioFormalizacion;
	}
	
	public void setcAnioFormalizacion( String cAnioFormalizacion ) {
		this.cAnioFormalizacion = cAnioFormalizacion;
	}
	
	public String getcDeclaraccion() {
		return cDeclaraccion;
	}
	
	public void setcDeclaraccion( String cDeclaraccion ) {
		this.cDeclaraccion = cDeclaraccion;
	}
	
	public String getcDescripcionServicio() {
		return cDescripcionServicio;
	}
	
	public void setcDescripcionServicio( String cDescripcionServicio ) {
		this.cDescripcionServicio = cDescripcionServicio;
	}
	
	public String getcInmueble() {
		return cInmueble;
	}
	
	public void setcInmueble( String cInmueble ) {
		this.cInmueble = cInmueble;
	}
	
	public String getcEjercicioPago() {
		return cEjercicioPago;
	}
	
	public void setcEjercicioPago( String cEjercicioPago ) {
		this.cEjercicioPago = cEjercicioPago;
	}

	@Override
	public String toString() {
		return "DatAnexo1A [nDiaformalizacion=" + nDiaformalizacion + ", cMesFormalizacion=" + cMesFormalizacion + ", cAnioFormalizacion=" + cAnioFormalizacion + ", cDeclaraccion=" + cDeclaraccion + ", cDescripcionServicio=" + cDescripcionServicio + ", cInmueble=" + cInmueble + ", cEjercicioPago=" + cEjercicioPago + "]";
	}
	
}
