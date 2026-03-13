package com.axtel.sisecop.dto;


import java.io.Serializable;


public class TerritoryDTO implements Serializable {

	private static final long	serialVersionUID	= 3495907327418937160L;
	int							municipalityId;
	int							servicioId;
	int							stateId;

	public int getMunicipalityId() {
		return municipalityId;
	}

	public int getServicioId() {
		return servicioId;
	}

	public int getStateId() {
		return stateId;
	}

	public void setMunicipalityId( int municipalityId ) {
		this.municipalityId = municipalityId;
	}

	public void setServicioId( int servicioId ) {
		this.servicioId = servicioId;
	}

	public void setStateId( int stateId ) {
		this.stateId = stateId;
	}

	@Override
	public String toString() {
		return "TerritoryDTO [stateId=" + stateId + ", municipalityId=" + municipalityId + ", servicioId=" + servicioId + "]";
	}
}
