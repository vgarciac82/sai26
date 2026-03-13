package com.syc.cfdi.core;


import java.util.List;
import java.util.Map;

import com.syc.cfdi.ComponentesFactura;


public class ExtraccionFacturas {

	private Map<String, ComponentesFactura>	facturas;
	private List<String>					errores;

	public void setFacturas( Map<String, ComponentesFactura> facturas ) {
		this.facturas = facturas;
	}

	public void setErrores( List<String> errores ) {
		this.errores = errores;
	}

	public Map<String, ComponentesFactura> getFacturas() {
		return facturas;
	}

	public List<String> getErrores() {
		return errores;
	}

}
