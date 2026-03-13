package com.syc.cfdi.core;


import java.math.BigDecimal;
import java.util.Map;



public class Factura {

	private Traslados			impuestos;
	private String					nombreEmisor;
	private String					nombreReceptor;
	private Map<String, BigDecimal>	retenciones;
	private BigDecimal				totalRetenciones;
	private String					rfcEmisor;
	private String					rfcReceptor;
	private BigDecimal				subTotal;
	private BigDecimal				total;
	private String					UUID;

	public Traslados getImpuestos() {
		return impuestos;
	}

	public String getNombreEmisor() {
		return nombreEmisor;
	}

	public String getNombreReceptor() {
		return nombreReceptor;
	}

	public Map<String, BigDecimal> getRetenciones() {
		return retenciones;
	}

	public String getRfcEmisor() {
		return rfcEmisor;
	}

	public String getRfcReceptor() {
		return rfcReceptor;
	}

	public BigDecimal getSubTotal() {
		return subTotal;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public String getUUID() {
		return UUID;
	}

	public void setImpuestos( Traslados impuestos ) {
		this.impuestos = impuestos;
	}

	public void setNombreEmisor( String nombreEmisor ) {
		this.nombreEmisor = nombreEmisor;
	}

	public void setNombreReceptor( String nombreReceptor ) {
		this.nombreReceptor = nombreReceptor;
	}

	public void setRetenciones( Map<String, BigDecimal> retenciones ) {
		this.retenciones = retenciones;
	}

	public void setRfcEmisor( String rfcEmisor ) {
		this.rfcEmisor = rfcEmisor;
	}

	public void setRfcReceptor( String rfcReceptor ) {
		this.rfcReceptor = rfcReceptor;
	}

	public void setSubTotal( BigDecimal subTotal ) {
		this.subTotal = subTotal;
	}

	public void setTotal( BigDecimal total ) {
		this.total = total;
	}

	public void setUUID( String uUID ) {
		UUID = uUID;
	}

	@Override
	public String toString() {
		return "Factura [impuestos=" + impuestos + ", nombreEmisor=" + nombreEmisor + ", nombreReceptor=" + nombreReceptor + ", retenciones=" + retenciones + ", rfcEmisor=" + rfcEmisor + ", rfcReceptor=" + rfcReceptor + ", subTotal=" + subTotal + ", total=" + total + ", UUID=" + UUID + "]";
	}

}
