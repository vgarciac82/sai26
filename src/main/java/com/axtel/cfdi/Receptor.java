package com.axtel.cfdi;


public class Receptor {

	private int		receptorID;
	private String	rfc;
	private String	nombre;
	private String	regimenFiscal;
	private String	domicilioFiscal;
	private String	email;

	public Receptor( ) {
	}

	public Receptor( String rfc, String nombre, String regimenFiscal, String domicilioFiscal, String email ) {
		this.rfc = rfc;
		this.nombre = nombre;
		this.regimenFiscal = regimenFiscal;
		this.domicilioFiscal = domicilioFiscal;
		this.email = email;
	}

	public int getReceptorID() {
		return receptorID;
	}

	public void setReceptorID( int receptorID ) {
		this.receptorID = receptorID;
	}

	public String getRfc() {
		return rfc;
	}

	public void setRfc( String rfc ) {
		this.rfc = rfc;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre( String nombre ) {
		this.nombre = nombre;
	}

	public String getRegimenFiscal() {
		return regimenFiscal;
	}

	public void setRegimenFiscal( String regimenFiscal ) {
		this.regimenFiscal = regimenFiscal;
	}

	public String getDomicilioFiscal() {
		return domicilioFiscal;
	}

	public void setDomicilioFiscal( String domicilioFiscal ) {
		this.domicilioFiscal = domicilioFiscal;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail( String email ) {
		this.email = email;
	}
}
