package com.axtel.sisecop.entities;


import java.io.Serializable;


public class ProyectoMunicipio implements Serializable {

	private static final long	serialVersionUID	= -9143088505887916293L;
	private int					municipioId;
	private int					entidadFederativaId;
	private String				municipioNombre;

	public int getMunicipioId() {
		return municipioId;
	}

	public void setMunicipioId( int municipioId ) {
		this.municipioId = municipioId;
	}

	public int getEntidadFederativaId() {
		return entidadFederativaId;
	}

	public void setEntidadFederativaId( int entidadFederativaId ) {
		this.entidadFederativaId = entidadFederativaId;
	}

	public String getMunicipioNombre() {
		return municipioNombre;
	}

	public void setMunicipioNombre( String municipioNombre ) {
		this.municipioNombre = municipioNombre;
	}

	@Override
	public String toString() {
		return "ProyectoMunicipio [municipioId=" + municipioId + ", entidadFederativaId=" + entidadFederativaId + ", municipioNombre=" + municipioNombre + "]";
	}

}
