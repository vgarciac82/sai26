package com.syc.egresos.firmante.servlet;


import java.util.Date;


public class FirmanteSuplente extends Firmante {

	private Date	fechaOficio;
	private String	folioOficio;
	private String	motivoSuplencia;
	private String	tipoSuplencia;

	public FirmanteSuplente( ) {
		super();
	}

	public FirmanteSuplente( Firmante firmante ) {
		super( firmante.getNombreEmpleado(), firmante.getNumeroEmpleado(), firmante.getPuestoEmpleado() );
	}

	public Date getFechaOficio() {
		return fechaOficio;
	}

	public String getFolioOficio() {
		return folioOficio;
	}

	public String getMotivoSuplencia() {
		return motivoSuplencia;
	}

	public String getTipoSuplencia() {
		return tipoSuplencia;
	}

	public void setFechaOficio( Date fechaOficio ) {
		this.fechaOficio = fechaOficio;
	}

	public void setFolioOficio( String folioOficio ) {
		this.folioOficio = folioOficio;
	}

	public void setMotivoSuplencia( String motivoSuplencia ) {
		this.motivoSuplencia = motivoSuplencia;
	}

	public void setTipoSuplencia( String tipoSuplencia ) {
		this.tipoSuplencia = tipoSuplencia;
	}

	@Override
	public String toString() {
		return "FirmanteSuplente [fechaOficio=" + fechaOficio + ", folioOficio=" + folioOficio + ", tipoSuplencia=" + tipoSuplencia + ", getNombreEmpleado()=" + getNombreEmpleado() + ", getNumeroEmpleado()=" + getNumeroEmpleado() + ", getPuestoEmpleado()=" + getPuestoEmpleado() + ", getTipoAutorizador()=" + getTipoAutorizador() + "]";
	}

}
