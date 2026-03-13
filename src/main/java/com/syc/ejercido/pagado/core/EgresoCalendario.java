package com.syc.ejercido.pagado.core;


import java.math.BigDecimal;


public class EgresoCalendario {

	private String		ep;
	private int			folioPago;
	private String		idTipoConcepto;
	private String		idTipoMovimiento;
	private BigDecimal	importeBruto	= new BigDecimal( 0.00d );
	private int			mesPresupuesto;
	private String		tipoPago;

	
	public String getEp() {
		return ep;
	}

	
	public int getFolioPago() {
		return folioPago;
	}

	
	public String getIdTipoConcepto() {
		return idTipoConcepto;
	}

	
	public String getIdTipoMovimiento() {
		return idTipoMovimiento;
	}

	public BigDecimal getImporteBruto() {
		return importeBruto;
	}

	public int getMesPresupuesto() {
		return mesPresupuesto;
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

	public void setIdTipoConcepto( String idTipoConcepto ) {
		this.idTipoConcepto = idTipoConcepto;
	}

	public void setIdTipoMovimiento( String idTipoMovimiento ) {
		this.idTipoMovimiento = idTipoMovimiento;
	}

	public void setImporteBruto( BigDecimal importeBruto ) {
		this.importeBruto = importeBruto;
	}

	public void setMesPresupuesto( int mesPresupuesto ) {
		this.mesPresupuesto = mesPresupuesto;
	}

	public void setTipoPago( String tipoPago ) {
		this.tipoPago = tipoPago;
	}

}
