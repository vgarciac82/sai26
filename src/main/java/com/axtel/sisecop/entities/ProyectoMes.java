package com.axtel.sisecop.entities;


import java.io.Serializable;

import com.syc.gestion.util.Util;


public class ProyectoMes implements Serializable {

	private static final long	serialVersionUID	= 1L;

	private int					mesId;

	private String				mesNombre;

	public ProyectoMes( ) {
		super();
	}

	public ProyectoMes( int mesID ) {
		this.mesId = mesID;
		this.mesNombre = Util.NOMBRE_MESES_MX[mesID - 1];
	}

	public int getMesId() {
		return mesId;
	}

	public String getMesNombre() {
		return mesNombre;
	}

	public void setMesId( int mesId ) {
		this.mesId = mesId;
	}

	public void setMesNombre( String mesNombre ) {
		this.mesNombre = mesNombre;
	}

	@Override
	public String toString() {
		return "ProyectoMes [mesId=" + mesId + ", mesNombre=" + mesNombre + "]";
	}

}
