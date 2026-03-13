package com.syc.contable.core;

import java.util.List;

public class ManualContable {
	private List<ManualContableDetalle>	detalle;
	private ManualContableEncabezado		encabezado;

	public ManualContable(ManualContableEncabezado encabezado, List<ManualContableDetalle> detalle) {
		super();
		this.encabezado = encabezado;
		this.detalle = detalle;
	}

	public List<ManualContableDetalle> getDetalle() {
		return detalle;
	}

	public ManualContableEncabezado getEncabezado() {
		return encabezado;
	}

	public void setDetalle(List<ManualContableDetalle> detalle) {
		this.detalle = detalle;
	}

	public void setEncabezado(ManualContableEncabezado encabezado) {
		this.encabezado = encabezado;
	}

}
