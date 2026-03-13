package com.syc.contable;

import java.util.List;

public class Poliza {

	EncabezadoPoliza encabezado;
	List<Movimiento> detalle;

	public EncabezadoPoliza getEncabezado() {
		return encabezado;
	}

	public void setEncabezado(EncabezadoPoliza encabezado) {
		this.encabezado = encabezado;
	}

	public List<Movimiento> getDetalle() {
		return detalle;
	}

	public void setDetalle(List<Movimiento> detalle) {
		this.detalle = detalle;
	}

}