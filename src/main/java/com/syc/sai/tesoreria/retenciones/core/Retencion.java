package com.syc.sai.tesoreria.retenciones.core;

import org.codehaus.jackson.annotate.JsonProperty;

public class Retencion {

	@JsonProperty("idRetencion")
	int		idRetencion;

	@JsonProperty("descRetencion")
	String	descRetencion;

	@JsonProperty("porcentaje")
	float	porcentaje;

	public int getIdRetencion() {
		return idRetencion;
	}

	public void setIdRetencion(int idRetencion) {
		this.idRetencion = idRetencion;
	}

	public String getDescRetencion() {
		return descRetencion;
	}

	public void setDescRetencion(String descRetencion) {
		this.descRetencion = descRetencion;
	}

	public float getPorcentaje() {
		return porcentaje;
	}

	public void setPorcentaje(float porcentaje) {
		this.porcentaje = porcentaje;
	}

}
