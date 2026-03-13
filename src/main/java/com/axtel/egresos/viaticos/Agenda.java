package com.axtel.egresos.viaticos;

import java.util.Date;


public class Agenda {
	private int idComision;
	private int idAgenda;
	private Date fechaInicio;
	private Date fechaFin;
	private int idTipo;
	private int idPais;
	private int idEstado;
	private int idMunicipio;
	private String localidad;
	private String motivoComision;
	private String actividades;
	private String pais;
	private String entidad;
	private String municipio;
	private int idEmpleado;
	private String nombreComision;

	public Date getFechaInicio() {
		return fechaInicio;
	}
	
	public void setFechaInicio( Date fechaInicio ) {
		this.fechaInicio = fechaInicio;
	}
	
	public Date getFechaFin() {
		return fechaFin;
	}
	
	public void setFechaFin( Date fechaFin ) {
		this.fechaFin = fechaFin;
	}
	
	public int getIdTipo() {
		return idTipo;
	}
	
	public void setIdTipo( int idTipo ) {
		this.idTipo = idTipo;
	}
	
	public int getIdPais() {
		return idPais;
	}
	
	public void setIdPais( int idPais ) {
		this.idPais = idPais;
	}
	
	public int getIdEstado() {
		return idEstado;
	}
	
	public void setIdEstado( int idEstado ) {
		this.idEstado = idEstado;
	}
	
	public int getIdMunicipio() {
		return idMunicipio;
	}
	
	public void setIdMunicipio( int idMunicipio ) {
		this.idMunicipio = idMunicipio;
	}
	
	public String getLocalidad() {
		return localidad;
	}
	
	public void setLocalidad( String localidad ) {
		this.localidad = localidad;
	}
	
	public String getMotivoComision() {
		return motivoComision;
	}
	
	public void setMotivoComision( String motivoComision ) {
		this.motivoComision = motivoComision;
	}
	
	public String getActividades() {
		return actividades;
	}
	public void setActividades( String actividades ) {
		this.actividades = actividades;
	}

	public int getIdAgenda() {
		return idAgenda;
	}

	public void setIdAgenda( int idAgenda ) {
		this.idAgenda = idAgenda;
	}

	public int getIdComision() {
		return idComision;
	}

	public void setIdComision( int idComision ) {
		this.idComision = idComision;
	}

	@Override
	public String toString() {
		return "Agenda [idComision=" + idComision + ", idAgenda=" + idAgenda + ", fechaInicio=" + fechaInicio + ", fechaFin=" + fechaFin + ", idTipo=" + idTipo + ", idPais=" + idPais + ", idEstado=" + idEstado + ", idMunicipio=" + idMunicipio + ", localidad=" + localidad + ", motivoComision=" + motivoComision + ", actividades=" + actividades + ", pais=" + pais + ", entidad=" + entidad + ", municipio=" + municipio + ", idEmpleado=" + idEmpleado + ", nombreComision=" + nombreComision + "]";
	}

	public int getIdEmpleado() {
		return idEmpleado;
	}

	public void setIdEmpleado( int idEmpleado ) {
		this.idEmpleado = idEmpleado;
	}

	public String getMunicipio() {
		return municipio;
	}

	public void setMunicipio( String municipio ) {
		this.municipio = municipio;
	}

	public String getPais() {
		return pais;
	}

	public void setPais( String pais ) {
		this.pais = pais;
	}

	public String getEntidad() {
		return entidad;
	}

	public void setEntidad( String entidad ) {
		this.entidad = entidad;
	}

	public String getNombreComision() {
		return nombreComision;
	}

	public void setNombreComision( String nombreComision ) {
		this.nombreComision = nombreComision;
	}	
}
