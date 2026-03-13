package com.syc.sai.ejercido.pagado;

import java.util.List;

public class Pagado {

	private List<PagadoDetalle>	detalle;
	private PagadoEncabezado	encabezado;

	public Pagado(PagadoEncabezado encabezado, List<PagadoDetalle> detalle) {
		this.encabezado = encabezado;
		this.detalle = detalle;
	}

	/**
	 * @return the detalle
	 */
	public List<PagadoDetalle> getDetalle() {
		return detalle;
	}

	/**
	 * @return the encabezado
	 */
	public PagadoEncabezado getEncabezado() {
		return encabezado;
	}

	/**
	 * @param detalle
	 *            the detalle to set
	 */
	public void setDetalle(List<PagadoDetalle> detalle) {
		this.detalle = detalle;
	}

	/**
	 * @param encabezado
	 *            the encabezado to set
	 */
	public void setEncabezado(PagadoEncabezado encabezado) {
		this.encabezado = encabezado;
	}

	public void setFolioPagado(int nFolioPagado) {
		this.encabezado.setFolioPagado(nFolioPagado);
		for (int i = 0; i < this.detalle.size(); i++)
			this.detalle.get(i).setFolioPagado(nFolioPagado);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Pagado [detalle=" + detalle + ", encabezado=" + encabezado + "]";
	}

}
