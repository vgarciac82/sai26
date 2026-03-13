package com.syc.contable.core;

import java.io.Serializable;
import java.util.List;

import org.apache.log4j.Logger;

public class Rectificacion implements Serializable {

	private static final long	serialVersionUID	= 1540226551394911009L;
	private static final Logger			log	= Logger.getLogger(Rectificacion.class);
	private List<RectificacionDetalle>	detalle;
	private RectificacionEncabezado		encabezado;
	private int							nFolioRectificacion;

	public Rectificacion() {
		log.trace("Creada rectificacion vacia");
	}

	public Rectificacion(int nFolioRectificacion, RectificacionEncabezado encabezado, List<RectificacionDetalle> detalle) {
		super();
		this.nFolioRectificacion = nFolioRectificacion;
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
		return nFolioRectificacion;
	}

	public void setDetalle(List<RectificacionDetalle> detalle) {
		log.trace("Estableciendo detalle");
		this.detalle = detalle;
		log.trace("Detalle establecido");
		log.trace(this.detalle);
	}

	public void setEncabezado(RectificacionEncabezado encabezado) {
		log.trace("Estableciendo encabezado de la rectificacion.");
		this.encabezado = encabezado;
		log.trace("Encabezado establecido");
		log.trace(this.encabezado);
	}

	public void setnFolioRectificacion(int nFolioRectificacion) {
		this.nFolioRectificacion = nFolioRectificacion;
	}

	@Override
	public String toString() {
		return "Rectificacion [detalle=" + detalle + ", encabezado=" + encabezado + ", nFolioRectificacion=" + nFolioRectificacion + "]";
	}

	
}
