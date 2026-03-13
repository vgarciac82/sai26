package com.axtel.egresos.core;


import java.util.List;

import com.syc.egresos.core.impl.EgresoPAGODIVERSODetalle;
import com.syc.egresos.core.impl.EgresoPAGODIVERSOEncabezado;


public class EgresoPAGODIVERSO {

	public static final int					ID_TC	= 6;
	private List<EgresoPAGODIVERSODetalle>	detalle;
	private EgresoPAGODIVERSOEncabezado		encabezado;

	public EgresoPAGODIVERSO( EgresoPAGODIVERSOEncabezado encabezado, List<EgresoPAGODIVERSODetalle> detalle ) {
		this.encabezado = encabezado;
		this.detalle = detalle;
	}

	/**
	 * @return the detalle
	 */
	public List<EgresoPAGODIVERSODetalle> getDetalle() {
		return detalle;
	}

	/**
	 * @return the encabezado
	 */
	public EgresoPAGODIVERSOEncabezado getEncabezado() {
		return encabezado;
	}

	/**
	 * @param detalle
	 *            the detalle to set
	 */
	public void setDetalle( List<EgresoPAGODIVERSODetalle> detalle ) {
		this.detalle = detalle;
	}

	/**
	 * @param encabezado
	 *            the encabezado to set
	 */
	public void setEncabezado( EgresoPAGODIVERSOEncabezado encabezado ) {
		this.encabezado = encabezado;
	}

	@Override
	public String toString() {
		return "EgresoPAGODIVERSO [encabezado=" + encabezado + ", detalle=" + detalle + "]";
	}

}
