package com.axtel.sisecop.entities;


import java.io.Serializable;


public class ProyectoEntidadFederativa implements Serializable {

	private static final long	serialVersionUID	= -2317858676075982004L;
	private int					id;
	private String				nombre;

	public int getId() {
		return id;
	}

	public void setId( int id ) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre( String nombre ) {
		this.nombre = nombre;
	}

	@Override
	public String toString() {
		return "ProyectoEntidadFederativa [id=" + id + ", nombre=" + nombre + "]";
	}

}
