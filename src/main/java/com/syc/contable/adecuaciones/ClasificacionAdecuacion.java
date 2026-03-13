package com.syc.contable.adecuaciones;

/**
 * Bean que almacena el nivel y el tipo de una adecuacion.
 * 
 * @author Vicente Garcia Carrillo
 * 
 */
public class ClasificacionAdecuacion {

	private int		nivel;
	private String	tipoAdecuacion;

	public int getNivel() {
		return nivel;
	}

	public void setNivel(int nivel) {
		this.nivel = nivel;
	}

	public String getTipoAdecuacion() {
		return tipoAdecuacion;
	}

	public void setTipoAdecuacion(String tipoAdecuacion) {
		this.tipoAdecuacion = tipoAdecuacion;
	}

	public ClasificacionAdecuacion(int nivel, String tipoAdecuacion) {
		super();
		this.nivel = nivel;
		this.tipoAdecuacion = tipoAdecuacion;
	}

}
