package com.axtel.presupuesto;


import java.util.List;

import com.syc.contable.adecuaciones.UsuarioSiplan;


public class AdecuacionResumen {

	/**
	 * 
	 */
	public AdecuacionResumen( ) {
		super();
	}

	private List<AdecuacionDetalleResumen>	detalle;
	private AdecuacionEncabezadoResumen		encabezado;
	private List<UsuarioSiplan>				enlacesSai;

	public List<UsuarioSiplan> getEnlacesSai() {
		return enlacesSai;
	}

	public void setEnlacesSai( List<UsuarioSiplan> enlacesSai ) {
		this.enlacesSai = enlacesSai;
	}

	/**
	 * @param encabezado
	 * @param detalle
	 */
	public AdecuacionResumen( AdecuacionEncabezadoResumen encabezado, List<AdecuacionDetalleResumen> detalle ) {
		super();
		this.encabezado = encabezado;
		this.detalle = detalle;
	}

	/**
	 * @return the detalle
	 */
	public List<AdecuacionDetalleResumen> getDetalle() {
		return detalle;
	}

	/**
	 * @return the encabezado
	 */
	public AdecuacionEncabezadoResumen getEncabezado() {
		return encabezado;
	}

	/**
	 * @param detalle
	 *            the detalle to set
	 */
	public void setDetalle( List<AdecuacionDetalleResumen> detalle ) {
		this.detalle = detalle;
	}

	/**
	 * @param encabezado
	 *            the encabezado to set
	 */
	public void setEncabezado( AdecuacionEncabezadoResumen encabezado ) {
		this.encabezado = encabezado;
	}

	@Override
	public String toString() {
		return "AdecuacionResumen [detalle=" + detalle + ", encabezado=" + encabezado + ", enlacesSai=" + enlacesSai + "]";
	}

}
