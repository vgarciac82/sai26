package com.syc.egresos.core;


import java.math.BigDecimal;


public class PenaConvencional {

	private int			folioPago;
	private String		idContrato;
	private BigDecimal	importeSancionBruto	= new BigDecimal( 0.0d );
	private BigDecimal	importeSancionIVA	= new BigDecimal( 0.0d );
	private BigDecimal	importeSancionNeto	= new BigDecimal( 0.0d );
	private String		tipoPago;
    

	
	public String getComponente() {
		return "importePenalizacion";
	}

	
	public int getFolioPago() {
		return folioPago;
	}

	
	public String getIdContrato() {
		return idContrato;
	}

	
	public BigDecimal getImporteSancionBruto() {
		return importeSancionBruto;
	}

	public BigDecimal getImporteSancionIVA() {
		return importeSancionIVA;
	}

	public BigDecimal getImporteSancionNeto() {
		return importeSancionNeto;
	}

	public String getTipoPago() {
		return tipoPago;
	}

	public void setFolioPago( int folioPago ) {
		this.folioPago = folioPago;
	}

	public void setIdContrato( String idContrato ) {
		this.idContrato = idContrato;
	}

	public void setImporteSancionBruto( BigDecimal importeSancionBruto ) {
		this.importeSancionBruto = importeSancionBruto;
	}

	public void setImporteSancionIVA( BigDecimal importeSancionIVA ) {
		this.importeSancionIVA = importeSancionIVA;
	}

	public void setImporteSancionNeto( BigDecimal importeSancionNeto ) {
		this.importeSancionNeto = importeSancionNeto;
	}

	public void setTipoPago( String tipoPago ) {
		this.tipoPago = tipoPago;
	}

}
