package com.axtel.sisecop.entities;


import java.io.Serializable;


public class ProyectoTipo implements Serializable {

	private static final long	serialVersionUID	= -4298044940509593209L;

	private int					tipoProyectoId;

	private String				tipoProyectoNombre;

	public int getTipoProyectoId() {
		return tipoProyectoId;
	}

	public void setTipoProyectoId( int tipoProyectoId ) {
		this.tipoProyectoId = tipoProyectoId;
	}

	public String getTipoProyectoNombre() {
		return tipoProyectoNombre;
	}

	public void setTipoProyectoNombre( String tipoProyectoNombre ) {
		this.tipoProyectoNombre = tipoProyectoNombre;
	}

	@Override
	public String toString() {
		return "ProyectoTipo [tipoProyectoId=" + tipoProyectoId + ", tipoProyectoNombre=" + tipoProyectoNombre + "]";
	}

}
