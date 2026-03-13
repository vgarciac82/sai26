package com.syc.contable;

import java.util.List;

public class DocPoliza {

	private List<DocPolizaDetalle> detalle;
	private DocPolizaEncabezado encabezado;

	public DocPoliza() {
	}

	public DocPoliza(DocPolizaEncabezado encabezado,
			List<DocPolizaDetalle> detalle) {
		super();
		this.encabezado = encabezado;
		this.detalle = detalle;
	}

	public List<DocPolizaDetalle> getDetalle() {
		return detalle;
	}

	public DocPolizaEncabezado getEncabezado() {
		return encabezado;
	}

	public void setDetalle(List<DocPolizaDetalle> detalle) {
		this.detalle = detalle;
	}

	public void setEncabezado(DocPolizaEncabezado encabezado) {
		this.encabezado = encabezado;
	}

	@Override
	public String toString() {
		return "DocPoliza [encabezado=" + encabezado + ", detalle=" + detalle
				+ "]";
	}

}
