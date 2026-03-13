package com.axtel.sisecop.dto;


import java.io.Serializable;


public class ProyectoDTO implements Serializable {

	private static final long	serialVersionUID	= 1L;

	private int					servicioId;
	private String				servicioTitulo;
	private String				servicioObjetivos;
	private String				servicioFolio;

	public ProyectoDTO( ) {
	}

	public ProyectoDTO( int servicioId, String servicioTitulo, String servicioObjetivos, String servicioFolio ) {
		this.servicioId = servicioId;
		this.servicioTitulo = servicioTitulo;
		this.servicioObjetivos = servicioObjetivos;
		this.servicioFolio = servicioFolio;
	}

	public int getServicioId() {
		return servicioId;
	}

	public void setServicioId( int servicioId ) {
		this.servicioId = servicioId;
	}

	public String getServicioTitulo() {
		return servicioTitulo;
	}

	public void setServicioTitulo( String servicioTitulo ) {
		this.servicioTitulo = servicioTitulo;
	}

	public String getServicioObjetivos() {
		return servicioObjetivos;
	}

	public void setServicioObjetivos( String servicioObjetivos ) {
		this.servicioObjetivos = servicioObjetivos;
	}

	public String getServicioFolio() {
		return servicioFolio;
	}

	public void setServicioFolio( String servicioFolio ) {
		this.servicioFolio = servicioFolio;
	}

	@Override
	public String toString() {
		return "ProyectoDTO{" + "servicioId=" + servicioId + ", servicioTitulo='" + servicioTitulo + '\'' + ", servicioObjetivos='" + servicioObjetivos + '\'' + ", servicioFolio='" + servicioFolio + '\'' + '}';
	}
}
