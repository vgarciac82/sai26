package com.syc.gestion.core;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class BitacoraCaso implements Serializable{
	private final static long serialVersionUID = 1;

	/*
	+ "(id_caso, folio, tipo_caso, status, "
	   + " fecha_inicio, fecha_compromiso, responsable_id, responsable_area, "
	   + " remitente_id, remitente_area, fecha_ultima_operacion, cerrado) " 
	*/

	private int idCaso;
	private String folio;
	private int tipoCaso;
	private int status;
	private int idGabinete;
	private Timestamp fechaInicio;
	private Timestamp fechaCompromiso;
	private Timestamp fechaUltimaOperacion;
	private String remitenteId;
	private String remitenteArea;
	private String responsableId;
	private String responsableArea;
	private String tituloAplicacion;
	
	private boolean cerrado;

	public int getIdCaso() {
		return idCaso;
	}

	public void setIdCaso(int idCaso) {
		this.idCaso = idCaso;
	}

	public String getFolio() {
		return folio;
	}

	public void setFolio(String folio) {
		this.folio = folio;
	}

	public int getTipoCaso() {
		return tipoCaso;
	}

	public void setTipoCaso(int tipoCaso) {
		this.tipoCaso = tipoCaso;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Timestamp getFechaInicio() {
		return fechaInicio;
	}

	public String getFormattedFechaInicio(String format) {
		String retVal = null;
		try {
			retVal = (new SimpleDateFormat(format)).format(fechaInicio);
		} catch (Exception e) {
			//ignore
		}
		return retVal;
	}

	public void setFechaInicio(Timestamp fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public Timestamp getFechaCompromiso() {
		return fechaCompromiso;
	}

	public String getFormattedFechaCompromiso(String format) {
		String retVal = null;
		try {
			retVal = (new SimpleDateFormat(format)).format(fechaCompromiso);
		} catch (Exception e) {
			//ignore
		}
		return retVal;
	}

	public void setFechaCompromiso(Timestamp fechaCompromiso) {
		this.fechaCompromiso = fechaCompromiso;
	}

	public Timestamp getFechaUltimaOperacion() {
		return fechaUltimaOperacion;
	}

	public String getFormattedFechaUltimaOperacion(String format) {
		String retVal = null;
		try {
			retVal = (new SimpleDateFormat(format)).format(fechaUltimaOperacion);
		} catch (Exception e) {
			//ignore
		}
		return retVal;
	}

	public void setFechaUltimaOperacion(Timestamp fechaUltimaOperacion) {
		this.fechaUltimaOperacion = fechaUltimaOperacion;
	}

	public String getRemitenteId() {
		return remitenteId;
	}

	public void setRemitenteId(String remitenteId) {
		this.remitenteId = remitenteId;
	}

	public String getRemitenteArea() {
		return remitenteArea;
	}

	public void setRemitenteArea(String remitenteArea) {
		this.remitenteArea = remitenteArea;
	}

	public String getResponsableId() {
		return responsableId;
	}

	public void setResponsableId(String responsableId) {
		this.responsableId = responsableId;
	}

	public String getResponsableArea() {
		return responsableArea;
	}

	public void setResponsableArea(String responsableArea) {
		this.responsableArea = responsableArea;
	}

	public boolean isCerrado() {
		return cerrado;
	}

	public void setCerrado(boolean cerrado) {
		this.cerrado = cerrado;
	}

	public int getIdGabinete() {
		return idGabinete;
	}

	public void setIdGabinete(int idGabinete) {
		this.idGabinete = idGabinete;
	}

	public String getTituloAplicacion() {
		return tituloAplicacion;
	}

	public void setTituloAplicacion(String tituloAplicacion) {
		this.tituloAplicacion = tituloAplicacion;
	}

}
