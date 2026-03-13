package com.syc.contable.core;

import java.io.Serializable;
import java.util.List;

import org.apache.log4j.Logger;

public class RectificacionAnexo1 implements Serializable {

	private static final long	serialVersionUID	= 1540226551394911009L;
	private static final Logger			log	= Logger.getLogger(RectificacionAnexo1.class);
	private List<RectificacionDetalle>	detalle;
	private RectificacionEncabezado		encabezado;
	private int							nFolioRectificaAnexo1;

	public RectificacionAnexo1() {
		log.trace("Creada rectificacion vacia");
	}

	public RectificacionAnexo1(int nFolioRectificaAnexo1, RectificacionEncabezado encabezado, List<RectificacionDetalle> detalle) {
		super();
		this.nFolioRectificaAnexo1 = nFolioRectificaAnexo1;
		this.encabezado = encabezado;
		this.detalle = detalle;
		log.trace("Rectificacion creada con parmetros.");
	}

	public List<RectificacionDetalle> getDetalle() {
		log.trace("Devolviendo detalle");
		return detalle;
	}

	public RectificacionEncabezado getEncabezado() {
		log.trace("Devolviendo encebezado");
		return encabezado;
	}

	public int getnFolioRectificacion() {
		return nFolioRectificaAnexo1;
	}

	public void setDetalle(List<RectificacionDetalle> detalle) {
		log.trace("Estableciendo detalle");
		this.detalle = detalle;
	}

	public void setEncabezado(RectificacionEncabezado encabezado) {
		log.trace("Estableciendo encabezado de la rectificacion.");
		this.encabezado = encabezado;
		log.trace("Encabezado establecido");
		log.trace(this.encabezado);
	}

	public void setnFolioRectificacion(int nFolioRectificaAnexo1) {
		this.nFolioRectificaAnexo1 = nFolioRectificaAnexo1;
	}

	@Override
	public String toString() {
		return "Rectificacion [detalle=" + detalle + ", encabezado=" + encabezado + ", nFolioRectificaAnexo1=" + nFolioRectificaAnexo1 + "]";
	}

	
}
