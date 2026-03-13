package com.syc.ejercido.pagado.core;


import java.math.BigDecimal;


public class EgresoCalendarioRG extends EgresoCalendario {
	private BigDecimal montoRetencion;

	public BigDecimal getMontoRetencion() {
		return montoRetencion;
	}

	public void setMontoRetencion( BigDecimal montoRetencion ) {
		this.montoRetencion = montoRetencion;
	}
	
}
