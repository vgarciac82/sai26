
package com.axtel.contabilidad.reintegrosCaja.core;

import java.util.List;
import org.apache.log4j.Logger;
import com.axtel.contabilidad.reintegrosCaja.core.ReintegrosCaja;

/**
 * @author Ana
 *
 */
public class ReintegrosCaja {

	private static final long	serialVersionUID	= 1540226551394911009L;
	private static final Logger			log	= Logger.getLogger(ReintegrosCaja.class);
	private List<ReintegrosCajaDetalle>	detalle;
	private ReintegrosCajaEncabezado	encabezado;
	private int							nFolioReintegrocaja;

	public ReintegrosCaja() {
		log.trace("Creada reintegros caja vacia");
	}

	public ReintegrosCaja(int nFolioReintegrocaja, ReintegrosCajaEncabezado encabezado, List<ReintegrosCajaDetalle> detalle) {
		super();
		this.nFolioReintegrocaja = nFolioReintegrocaja;
		this.encabezado = encabezado;
		this.detalle = detalle;
		log.trace("Reintegro caja creado con parmetros.");
	}

	public List<ReintegrosCajaDetalle> getDetalle() {
		log.trace("Devolviendo detalle");
		return detalle;
	}

	public ReintegrosCajaEncabezado getEncabezado() {
		log.trace("Devolviendo encebezado");
		return encabezado;
	}

	public int getnFolioReintegrocaja() {
		return nFolioReintegrocaja;
	}

	public void setDetalle(List<ReintegrosCajaDetalle> detalle) {
		log.trace("Estableciendo detalle");
		this.detalle = detalle;
	}

	public void setEncabezado(ReintegrosCajaEncabezado encabezado) {
		log.trace("Estableciendo encabezado del reintegro de caja.");
		this.encabezado = encabezado;
		log.trace("Encabezado establecido");
		log.trace(this.encabezado);
	}

	public void setnFolioReintegrocaja(int nFolioReintegrocaja) {
		this.nFolioReintegrocaja = nFolioReintegrocaja;
	}

	@Override
	public String toString() {
		return "Rectificacion [detalle=" + detalle + ", encabezado=" + encabezado + ", nFolioRectificacion=" + nFolioReintegrocaja + "]";
	}

}
