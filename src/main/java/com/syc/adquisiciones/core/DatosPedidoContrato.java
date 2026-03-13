package com.syc.adquisiciones.core;

public class DatosPedidoContrato {
	private String cNamePlantilla;
	private String cFechaInicio;
	private String cFechaFin;
	private String cNameMes;
	private String cWhere;
	private String cUnidadEjecutora;
	private int nTipoReporte;
	private int nTipoIngreso;
	private String jniName;
	
	public String getcNamePlantilla() {
		return cNamePlantilla;
	}
	
	public void setcNamePlantilla( String cNamePlantilla ) {
		this.cNamePlantilla = cNamePlantilla;
	}
	
	public String getcFechaInicio() {
		return cFechaInicio;
	}
	
	public void setcFechaInicio( String cFechaInicio ) {
		this.cFechaInicio = cFechaInicio;
	}
	
	public String getcFechaFin() {
		return cFechaFin;
	}
	
	public void setcFechaFin( String cFechaFin ) {
		this.cFechaFin = cFechaFin;
	}
	
	public String getcNameMes() {
		return cNameMes;
	}
	
	public void setcNameMes( String cNameMes ) {
		this.cNameMes = cNameMes;
	}
	
	public String getcWhere() {
		return cWhere;
	}
	
	public void setcWhere( String cWhere ) {
		this.cWhere = cWhere;
	}
	
	public String getcUnidadEjecutora() {
		return cUnidadEjecutora;
	}
	
	public void setcUnidadEjecutora( String cUnidadEjecutora ) {
		this.cUnidadEjecutora = cUnidadEjecutora;
	}
	
	public int getnTipoReporte() {
		return nTipoReporte;
	}
	
	public void setnTipoReporte( int nTipoReporte ) {
		this.nTipoReporte = nTipoReporte;
	}
	
	public int getnTipoIngreso() {
		return nTipoIngreso;
	}
	
	public void setnTipoIngreso( int nTipoIngreso ) {
		this.nTipoIngreso = nTipoIngreso;
	}
	
	public String getJniName() {
		return jniName;
	}
	
	public void setJniName( String jniName ) {
		this.jniName = jniName;
	}
		
}
