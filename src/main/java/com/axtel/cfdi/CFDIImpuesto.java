package com.axtel.cfdi;


import java.math.BigDecimal;


public class CFDIImpuesto {

	private int			cfdiImpuestoId;
	private int			cfdiDetalleId;
	private String		tipo;			// 'R' para Retenciones, 'T' para
										// Traslados
	private Impuesto	impuesto;
	private TipoFactor	tipoFactor;
	private BigDecimal	tasaOCuota;
	private BigDecimal	importe;

	public int getCfdiImpuestoId() {
		return cfdiImpuestoId;
	}

	public void setCfdiImpuestoId( int cfdiImpuestoId ) {
		this.cfdiImpuestoId = cfdiImpuestoId;
	}

	public int getCfdiDetalleId() {
		return cfdiDetalleId;
	}

	public void setCfdiDetalleId( int cfdiDetalleId ) {
		this.cfdiDetalleId = cfdiDetalleId;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo( String tipo ) {
		this.tipo = tipo;
	}

	public Impuesto getImpuesto() {
		return impuesto;
	}

	public void setImpuesto( Impuesto impuesto ) {
		this.impuesto = impuesto;
	}

	public TipoFactor getTipoFactor() {
		return tipoFactor;
	}

	public void setTipoFactor( TipoFactor tipoFactor ) {
		this.tipoFactor = tipoFactor;
	}

	public BigDecimal getTasaOCuota() {
		return tasaOCuota;
	}

	public void setTasaOCuota( BigDecimal tasaOCuota ) {
		this.tasaOCuota = tasaOCuota;
	}

	public BigDecimal getImporte() {
		return importe;
	}

	public void setImporte( BigDecimal importe ) {
		this.importe = importe;
	}

}
