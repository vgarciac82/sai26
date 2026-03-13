package com.syc.contable.core;


import java.util.List;

import com.axtel.egresos.compromiso.PrecompromisoFinancieroDetalle;
import com.axtel.egresos.compromiso.PrecompromisoFinancieroEncabezado;


public class PrecompromisoFinanciero {

	private List<PrecompromisoFinancieroDetalle>	detalle;
	private PrecompromisoFinancieroEncabezado		encabezado;

	/**
	 * @return the detalle
	 */
	public List<PrecompromisoFinancieroDetalle> getDetalle() {
		return detalle;
	}

	/**
	 * @return the encabezado
	 */
	public PrecompromisoFinancieroEncabezado getEncabezado() {
		return encabezado;
	}

	/**
	 * @param detalle
	 *            the detalle to set
	 */
	public void setDetalle( List<PrecompromisoFinancieroDetalle> detalle ) {
		this.detalle = detalle;
	}

	/**
	 * @param encabezado
	 *            the encabezado to set
	 */
	public void setEncabezado( PrecompromisoFinancieroEncabezado encabezado ) {
		this.encabezado = encabezado;
	}

	@Override
	public String toString() {
		return "PrecompromisoFinanciero [encabezado=" + encabezado + ", detalle=" + detalle + "]";
	}

}
