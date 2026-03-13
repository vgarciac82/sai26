package com.axtel.contratos.core;


public class DatosContratoPSP {
	private String cIdcontratoDefinitivo;
	private String cAreaRequirente;
	private String cAreaResponsable;
	private int nCentroTrabajo;
	private double mMontoMensual;
	private String cDenominacionProyecto;
	private boolean lEsMaestro;
	
	public String getcIdcontratoDefinitivo() {
		return cIdcontratoDefinitivo;
	}
	
	public void setcIdcontratoDefinitivo( String cIdcontratoDefinitivo ) {
		this.cIdcontratoDefinitivo = cIdcontratoDefinitivo;
	}
	
	public String getcAreaRequirente() {
		return cAreaRequirente;
	}
	
	public void setcAreaRequirente( String cAreaRequirente ) {
		this.cAreaRequirente = cAreaRequirente;
	}
	
	public String getcAreaResponsable() {
		return cAreaResponsable;
	}
	
	public void setcAreaResponsable( String cAreaResponsable ) {
		this.cAreaResponsable = cAreaResponsable;
	}
	
	public int getnCentroTrabajo() {
		return nCentroTrabajo;
	}
	
	public void setnCentroTrabajo( int nCentroTrabajo ) {
		this.nCentroTrabajo = nCentroTrabajo;
	}
	
	public boolean islEsMaestro() {
		return lEsMaestro;
	}
	
	public void setlEsMaestro( boolean lEsMaestro ) {
		this.lEsMaestro = lEsMaestro;
	}

	
	public double getmMontoMensual() {
		return mMontoMensual;
	}

	
	public void setmMontoMensual( double mMontoMensual ) {
		this.mMontoMensual = mMontoMensual;
	}

	
	public String getcDenominacionProyecto() {
		return cDenominacionProyecto;
	}

	
	public void setcDenominacionProyecto( String cDenominacionProyecto ) {
		this.cDenominacionProyecto = cDenominacionProyecto;
	}
	
}
