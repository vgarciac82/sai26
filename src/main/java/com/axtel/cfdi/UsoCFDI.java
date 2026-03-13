package com.axtel.cfdi;


public class UsoCFDI {

	private String	usoCFDI;
	private String	descripcion;

	public String getUsoCFDI() {
		return usoCFDI;
	}

	public void setUsoCFDI( String usoCFDI ) {
		this.usoCFDI = usoCFDI;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion( String descripcion ) {
		this.descripcion = descripcion;
	}

	@Override
	public String toString() {
		return "UsoCFDI [usoCFDI=" + usoCFDI + ", descripcion=" + descripcion + "]";
	}

}
