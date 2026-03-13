package com.syc.contable.core;

import java.util.List;

public class ConciliacionContable {
	private List<ConciliacionContableDetalle>	detalle;
	private ConciliacionContableEncabezado		encabezado;

	public ConciliacionContable(ConciliacionContableEncabezado encabezado, List<ConciliacionContableDetalle> detalle) {
		super();
		this.encabezado = encabezado;
		this.detalle = detalle;
	}

	public List<ConciliacionContableDetalle> getDetalle() {
		return detalle;
	}

	public ConciliacionContableEncabezado getEncabezado() {
		return encabezado;
	}

	public void setDetalle(List<ConciliacionContableDetalle> detalle) {
		this.detalle = detalle;
	}

	public void setEncabezado(ConciliacionContableEncabezado encabezado) {
		this.encabezado = encabezado;
	}

}
