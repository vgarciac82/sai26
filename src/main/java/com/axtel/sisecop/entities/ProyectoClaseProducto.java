package com.axtel.sisecop.entities;


import java.io.Serializable;


public class ProyectoClaseProducto implements Serializable {

	@Override
	public String toString() {
		return "ProyectoClaseProducto [claseId=" + claseId + ", claseNombre=" + claseNombre + "]";
	}

	private static final long	serialVersionUID	= 1L;

	private int					claseId;

	private String				claseNombre;

	public int getClaseId() {
		return claseId;
	}

	public void setClaseId( int claseId ) {
		this.claseId = claseId;
	}

	public String getClaseNombre() {
		return claseNombre;
	}

	public void setClaseNombre( String claseNombre ) {
		this.claseNombre = claseNombre;
	}

}
