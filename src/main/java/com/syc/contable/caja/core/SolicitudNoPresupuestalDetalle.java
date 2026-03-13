package com.syc.contable.caja.core;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.poi.ss.usermodel.Row;

import com.syc.gestion.core.Usuario;


public class SolicitudNoPresupuestalDetalle {

	private String		almacen;
	private String		cuentaBancaria;
	private String		cuentaBeneficiario;
	private int			docRenglon;
	private String		EP;
	private String		evento;
	private int			foliocaja;
	private BigDecimal	importe;
	private BigDecimal	importeNegativo;
	private String		objetoGasto;
	private String		rfc;

	public String getAlmacen() {
		return almacen;
	}

	public String getCuentaBancaria() {
		return cuentaBancaria;
	}

	public String getCuentaBeneficiario() {
		return cuentaBeneficiario;
	}

	public int getDocRenglon() {
		return docRenglon;
	}

	public String getEP() {
		return EP;
	}

	public String getEvento() {
		return evento;
	}

	public int getFoliocaja() {
		return foliocaja;
	}

	public BigDecimal getImporte() {
		return importe;
	}

	public BigDecimal getImporteNegativo() {
		return importeNegativo;
	}

	public String getObjetoGasto() {
		return objetoGasto;
	}

	public String getRfc() {
		return rfc;
	}

	public void setAlmacen(String almacen) {
		this.almacen = almacen;
	}

	public void setCuentaBancaria(String cuentaBancaria) {
		this.cuentaBancaria = cuentaBancaria;
	}

	public void setCuentaBeneficiario(String cuentaBeneficiario) {
		this.cuentaBeneficiario = cuentaBeneficiario;
	}

	public void setDocRenglon(int docRenglon) {
		this.docRenglon = docRenglon;
	}

	public void setEP(String eP) {
		EP = eP;
	}

	public void setEvento(String evento) {
		this.evento = evento;
	}

	public void setFoliocaja(int foliocaja) {
		this.foliocaja = foliocaja;
	}

	public void setImporte(BigDecimal importe) {
		this.importe = importe;
	}

	public void setImporteNegativo(BigDecimal importeNegativo) {
		this.importeNegativo = importeNegativo;
	}

	public void setObjetoGasto(String objetoGasto) {
		this.objetoGasto = objetoGasto;
	}

	public void setRfc(String rfc) {
		this.rfc = rfc;
	}

	public static SolicitudNoPresupuestalDetalle instanceFromExcel( Row fila, Usuario u ) {

		SolicitudNoPresupuestalDetalle snpd = new SolicitudNoPresupuestalDetalle();
		snpd.setDocRenglon( ( int ) ( fila.getCell( 1 ).getNumericCellValue() ) );
		snpd.setEvento( fila.getCell( 2 ).getStringCellValue() );
		snpd.setImporte( new BigDecimal( fila.getCell( 3 ).getNumericCellValue() ).setScale( 2, RoundingMode.HALF_UP ) );
		snpd.setImporteNegativo( new BigDecimal( -1.0 ).multiply( snpd.getImporte() ) );
		snpd.setCuentaBancaria( fila.getCell( 4 ).getStringCellValue() );
		if ("5_30_1".equals(fila.getCell( 2 ).getStringCellValue()))
			snpd.setRfc( "" );
		else 
			snpd.setRfc( fila.getCell( 5 ).getStringCellValue() );
		snpd.setCuentaBeneficiario( fila.getCell( 6 ).getStringCellValue() );
		return snpd;

	}

	@Override
	public String toString() {
		return "SolicitudNoPresupuestalDetalle [almacen=" + almacen + ", cuentaBancaria=" + cuentaBancaria + ", cuentaBeneficiario=" + cuentaBeneficiario + ", docRenglon=" + docRenglon + ", EP=" + EP + ", evento=" + evento + ", foliocaja=" + foliocaja + ", importe=" + importe + ", importeNegativo=" + importeNegativo + ", objetoGasto=" + objetoGasto + ", rfc=" + rfc + "]";
	}

}
