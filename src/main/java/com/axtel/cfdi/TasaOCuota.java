package com.axtel.cfdi;


import java.math.BigDecimal;


public class TasaOCuota {

	private int			tasaOCuotaID;
	private String		rangoOFijo;
	private BigDecimal	valorMinimo;
	private BigDecimal	valorMaximo;
	private String		impuesto;
	private String		factor;
	private boolean		traslado;
	private boolean		retencion;

	public int getTasaOCuotaID() {
		return tasaOCuotaID;
	}

	public void setTasaOCuotaID( int tasaOCuotaID ) {
		this.tasaOCuotaID = tasaOCuotaID;
	}

	public String getRangoOFijo() {
		return rangoOFijo;
	}

	public void setRangoOFijo( String rangoOFijo ) {
		this.rangoOFijo = rangoOFijo;
	}

	public BigDecimal getValorMinimo() {
		return valorMinimo;
	}

	public void setValorMinimo( BigDecimal valorMinimo ) {
		this.valorMinimo = valorMinimo;
	}

	public BigDecimal getValorMaximo() {
		return valorMaximo;
	}

	public void setValorMaximo( BigDecimal valorMaximo ) {
		this.valorMaximo = valorMaximo;
	}

	public String getImpuesto() {
		return impuesto;
	}

	public void setImpuesto( String impuesto ) {
		this.impuesto = impuesto;
	}

	public String getFactor() {
		return factor;
	}

	public void setFactor( String factor ) {
		this.factor = factor;
	}

	public boolean isTraslado() {
		return traslado;
	}

	public void setTraslado( boolean traslado ) {
		this.traslado = traslado;
	}

	public boolean isRetencion() {
		return retencion;
	}

	public void setRetencion( boolean retencion ) {
		this.retencion = retencion;
	}
}
