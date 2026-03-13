package com.syc.contable.core;

import java.util.List;

public class ConciliacionBancoFirmada {
	private List<ConciliacionBancoFirmadaDetalle>	detalle;
	private ConciliacionBancoFirmadaEncabezado		encabezado;

	public ConciliacionBancoFirmada(ConciliacionBancoFirmadaEncabezado encabezado, List<ConciliacionBancoFirmadaDetalle> detalle) {
		super();
		this.encabezado = encabezado;
		this.detalle = detalle;
	}

	public List<ConciliacionBancoFirmadaDetalle> getDetalle() {
		return detalle;
	}

	public ConciliacionBancoFirmadaEncabezado getEncabezado() {
		return encabezado;
	}

	public void setDetalle(List<ConciliacionBancoFirmadaDetalle> detalle) {
		this.detalle = detalle;
	}

	public void setEncabezado(ConciliacionBancoFirmadaEncabezado encabezado) {
		this.encabezado = encabezado;
	}

}
