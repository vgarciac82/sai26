package com.axtel.contratos.core;


import java.math.BigDecimal;


public class SuficienciaPagoDirectoRetencion {

	private int			folioSuficienciaPagoDirecto;
	private int			idTipoRetencion;
	private BigDecimal	importeRetencion;

	public int getFolioSuficienciaPagoDirecto() {
		return folioSuficienciaPagoDirecto;
	}

	public void setFolioSuficienciaPagoDirecto( int folioSuficienciaPagoDirecto ) {
		this.folioSuficienciaPagoDirecto = folioSuficienciaPagoDirecto;
	}

	public int getIdTipoRetencion() {
		return idTipoRetencion;
	}

	public void setIdTipoRetencion( int idTipoRetencion ) {
		this.idTipoRetencion = idTipoRetencion;
	}

	public BigDecimal getImporteRetencion() {
		return importeRetencion;
	}

	public void setImporteRetencion( BigDecimal importeRetencion ) {
		this.importeRetencion = importeRetencion;
	}
}
