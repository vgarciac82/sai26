package com.syc.egresos;


import java.math.BigDecimal;


public class DetallePago {

	private String		ep;
	private int			folioPago;
	private BigDecimal	importeBruto;
	private String		tipoPago;
	private String		idTipoConcepto;
	private String		idTipoMovimiento;
	private BigDecimal	importeRetencion;

	public String getIdTipoConcepto() {
		return idTipoConcepto;
	}

	public void setIdTipoConcepto( String idTipoConcepto ) {
		this.idTipoConcepto = idTipoConcepto;
	}

	public String getIdTipoMovimiento() {
		return idTipoMovimiento;
	}

	public void setIdTipoMovimiento( String idTipoMovimiento ) {
		this.idTipoMovimiento = idTipoMovimiento;
	}

	public String getEp() {
		return ep;
	}

	public int getFolioPago() {
		return folioPago;
	}

	public BigDecimal getImporteBruto() {
		return importeBruto;
	}

	public String getTipoPago() {
		return tipoPago;
	}

	public void setEp( String ep ) {
		this.ep = ep;
	}

	public void setFolioPago( int folioPago ) {
		this.folioPago = folioPago;
	}

	public void setImporteBruto( BigDecimal importeBruto ) {
		this.importeBruto = importeBruto;
	}

	public void setTipoPago( String tipoPago ) {
		this.tipoPago = tipoPago;
	}

	@Override
	public String toString() {
		return "DetallePago [ep=" + ep + ", folioPago=" + folioPago + ", tipoPago=" + tipoPago + ", importeBruto=" + importeBruto + "]";
	}

	public BigDecimal getImporteRetencion() {
		return importeRetencion;
	}

	public void setImporteRetencion( BigDecimal importeRetencion ) {
		this.importeRetencion = importeRetencion;
	}

}
