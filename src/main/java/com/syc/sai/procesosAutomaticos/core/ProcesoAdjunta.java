/**
 * 
 */
package com.syc.sai.procesosAutomaticos.core;

import java.util.Date;

/**
 * @author Vicente Garcia Carrillo
 * 
 */
public class ProcesoAdjunta {

	private Date	fechaProceso;
	private int		idEstatus;
	private String	idProceso;
	private String	resultadoProceso;
	private String	uLogin;
	private String	unidadEjecutora;

	public ProcesoAdjunta(Date fechaProceso, int idEstatus, String idProceso, String resultadoProceso, String uLogin, String unidadEjecutora) {
		super();
		this.fechaProceso = fechaProceso;
		this.idEstatus = idEstatus;
		this.idProceso = idProceso;
		this.resultadoProceso = resultadoProceso;
		this.uLogin = uLogin;
		this.unidadEjecutora = unidadEjecutora;
	}

	public Date getFechaProceso() {
		return fechaProceso;
	}

	public int getIdEstatus() {
		return idEstatus;
	}

	public String getIdProceso() {
		return idProceso;
	}

	public String getResultadoProceso() {
		return resultadoProceso;
	}

	public String getuLogin() {
		return uLogin;
	}

	public String getUnidadEjecutora() {
		return unidadEjecutora;
	}

	public void setFechaProceso(Date fechaProceso) {
		this.fechaProceso = fechaProceso;
	}

	public void setIdEstatus(int idEstatus) {
		this.idEstatus = idEstatus;
	}

	public void setIdProceso(String idProceso) {
		this.idProceso = idProceso;
	}

	public void setResultadoProceso(String resultadoProceso) {
		this.resultadoProceso = resultadoProceso;
	}

	public void setuLogin(String uLogin) {
		this.uLogin = uLogin;
	}

	public void setUnidadEjecutora(String unidadEjecutora) {
		this.unidadEjecutora = unidadEjecutora;
	}

	@Override
	public String toString() {
		return "ProcesoAdjunta [fechaProceso=" + fechaProceso + ", idEstatus=" + idEstatus + ", idProceso=" + idProceso + ", resultadoProceso=" + resultadoProceso + ", uLogin=" + uLogin + ", unidadEjecutora=" + unidadEjecutora + "]";
	}

}
