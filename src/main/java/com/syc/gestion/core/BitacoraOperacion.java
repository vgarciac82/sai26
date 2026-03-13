package com.syc.gestion.core;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class BitacoraOperacion implements Serializable{

	private final static long serialVersionUID = 1;
	// Valores iniciales invalidos
	private int idBitacora  = -1;
	private int idCaso      = -1;
	private int idTipoCaso  = -1;
	private int idOperacion = -1;
	private int idGabinete  = -1;
	private int oprStack    = -2; 

	private int secuencialAnterior  = -1;
	private int secuencialOperacion = -1;	// Corresponde a CG_OPERACION.ID_OPER
	private int secuencialSiguiente = -1;

	private int status;

	private Timestamp fechaInicio;
	private Timestamp fechaCompromiso;
	private Timestamp fechaTermino;
	
	private String folio;
	private String remitenteId;
	private String remitenteArea;
	private String responsableId;
	private String responsableArea;
	private String turnadoId;
	private String turnadoArea;
	private String tituloAplicacion;
	
	private String instruccion;
	
	private boolean terminada;
	private boolean leida;
	
	private Operacion operacion = null;

	public boolean isLeida() {
		return leida;
	}

	public void setLeida(boolean leida) {
		this.leida = leida;
	}

	public int getIdBitacora() {
		return idBitacora;
	}

	public void setIdBitacora(int idBitacora) {
		this.idBitacora = idBitacora;
	}

	public int getIdCaso() {
		return idCaso;
	}

	public void setIdCaso(int idCaso) {
		this.idCaso = idCaso;
	}

	public int getIdOperacion() {
		return idOperacion;
	}

	public void setIdOperacion(int idOperacion) {
		this.idOperacion = idOperacion;
	}

	// Corresponde a CG_OPERACION.ID_CASO_OPER
	public int getSecuencialOperacion() {
		return secuencialOperacion;
	}

	// Corresponde a CG_OPERACION.ID_CASO_OPER
	public void setSecuencialOperacion(int secuencialOperacion) {
		this.secuencialOperacion = secuencialOperacion;
	}

	public int getSecuencialSiguiente() {
		return secuencialSiguiente;
	}

	public void setSecuencialSiguiente(int secuencialSiguiente) {
		this.secuencialSiguiente = secuencialSiguiente;
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

	public Timestamp getFechaTermino() {
		return fechaTermino;
	}

	public String getFormattedFechaTermino(String format) {
		String retVal = null;
		try {
			retVal = (new SimpleDateFormat(format)).format(fechaTermino);
		} catch (Exception e) {
			//ignore
		}
		return retVal;
	}

	public void setFechaTermino(Timestamp fechaTermino) {
		this.fechaTermino = fechaTermino;
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

	public String getTurnadoId() {
		return turnadoId;
	}

	public void setTurnadoId(String turnadoId) {
		this.turnadoId = turnadoId;
	}

	public String getTurnadoArea() {
		return turnadoArea;
	}

	public void setTurnadoArea(String turnadoArea) {
		this.turnadoArea = turnadoArea;
	}

	public String getInstruccion() {
		return instruccion;
	}

	public void setInstruccion(String instruccion) {
		this.instruccion = instruccion;
	}

	public boolean isTerminada() {
		return terminada;
	}

	public void setTerminada(boolean terminada) {
		this.terminada = terminada;
	}

	public int getSecuencialAnterior() {
		return secuencialAnterior;
	}

	public void setSecuencialAnterior(int secuencialAnterior) {
		this.secuencialAnterior = secuencialAnterior;
	}

	public Operacion getOperacion() {
		return operacion;
	}

	public void setOperacion(Operacion operacion) {
		this.operacion = operacion;
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

	public int getIdTipoCaso() {
		return idTipoCaso;
	}

	public void setIdTipoCaso(int idTipoCaso) {
		this.idTipoCaso = idTipoCaso;
	}

	public String getFolio() {
		return folio;
	}

	public void setFolio(String folio) {
		this.folio = folio;
	}

	public int getOprStack() {
	
		return oprStack;
	}
	
	public void setOprStack(int oprStack) {
	
		this.oprStack = oprStack;
	}
}