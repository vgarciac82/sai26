package com.axtel.cfdi;


public class Periodicidad {

	private String	periodicidad;
	private String	descripcion;

	public String getPeriodicidad() {
		return periodicidad;
	}

	public void setPeriodicidad( String periodicidad ) {
		this.periodicidad = periodicidad;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion( String descripcion ) {
		this.descripcion = descripcion;
	}

	@Override
	public String toString() {
		return "Periodicidad [periodicidad=" + periodicidad + ", descripcion=" + descripcion + "]";
	}

}
