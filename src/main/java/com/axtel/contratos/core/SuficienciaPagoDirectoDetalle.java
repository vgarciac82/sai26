package com.axtel.contratos.core;


import java.math.BigDecimal;


public class SuficienciaPagoDirectoDetalle {

	private int			folioSuficienciaPagoDirecto;
	private int			docRenglon;
	private String		ep;
	private BigDecimal	importe;
	private BigDecimal	importeNegativo;
	private int			centroContable;
	private String		ur;

	public int getFolioSuficienciaPagoDirecto() {
		return folioSuficienciaPagoDirecto;
	}

	public void setFolioSuficienciaPagoDirecto( int folioSuficienciaPagoDirecto ) {
		this.folioSuficienciaPagoDirecto = folioSuficienciaPagoDirecto;
	}

	public int getDocRenglon() {
		return docRenglon;
	}

	public void setDocRenglon( int docRenglon ) {
		this.docRenglon = docRenglon;
	}

	public String getEp() {
		return ep;
	}

	public void setEp( String ep ) {
		this.ep = ep;
	}

	public BigDecimal getImporte() {
		return importe;
	}

	public void setImporte( BigDecimal importe ) {
		this.importe = importe;
	}

	public BigDecimal getImporteNegativo() {
		return importeNegativo;
	}

	public void setImporteNegativo( BigDecimal importeNegativo ) {
		this.importeNegativo = importeNegativo;
	}

	public int getCentroContable() {
		return centroContable;
	}

	public void setCentroContable( int centroContable ) {
		this.centroContable = centroContable;
	}

	public String getUr() {
		return ur;
	}

	public void setUr( String ur ) {
		this.ur = ur;
	}
}
