package com.syc.altaproveedor;


public class DatosCtaBancaria {

	String			cBanco;
	String			cNameBanco;
	String			cPlaza;
	int				nDigitoVerificador;
	String			cSucursal;
	String			cCuentaBancaria;
	String			cClabeInterbancaria;
	int				nBCBEnviadoSICOP;
	int				nEstatusCta;
	String			cEstatusCta;
	String			cUsuarioModifico;
	String			cMotivoEliminaCta;
	private String	folio;
	private String	rfc;

	public String getcBanco() {
		return cBanco;
	}

	public void setcBanco( String cBanco ) {
		this.cBanco = cBanco;
	}

	public String getcNameBanco() {
		return cNameBanco;
	}

	public void setcNameBanco( String cNameBanco ) {
		this.cNameBanco = cNameBanco;
	}

	public String getcPlaza() {
		return cPlaza;
	}

	public void setcPlaza( String cPlaza ) {
		this.cPlaza = cPlaza;
	}

	public int getnDigitoVerificador() {
		return nDigitoVerificador;
	}

	public void setnDigitoVerificador( int nDigitoVerificador ) {
		this.nDigitoVerificador = nDigitoVerificador;
	}

	public String getcSucursal() {
		return cSucursal;
	}

	public void setcSucursal( String cSucursal ) {
		this.cSucursal = cSucursal;
	}

	public String getcCuentaBancaria() {
		return cCuentaBancaria;
	}

	public void setcCuentaBancaria( String cCuentaBancaria ) {
		this.cCuentaBancaria = cCuentaBancaria;
	}

	public String getcClabeInterbancaria() {
		return cClabeInterbancaria;
	}

	public void setcClabeInterbancaria( String cClabeInterbancaria ) {
		this.cClabeInterbancaria = cClabeInterbancaria;
	}

	public int getnBCBEnviadoSICOP() {
		return nBCBEnviadoSICOP;
	}

	public void setnBCBEnviadoSICOP( int nBCBEnviadoSICOP ) {
		this.nBCBEnviadoSICOP = nBCBEnviadoSICOP;
	}

	public int getnEstatusCta() {
		return nEstatusCta;
	}

	public void setnEstatusCta( int nEstatusCta ) {
		this.nEstatusCta = nEstatusCta;
	}

	public String getcUsuarioModifico() {
		return cUsuarioModifico;
	}

	public void setcUsuarioModifico( String cUsuarioModifico ) {
		this.cUsuarioModifico = cUsuarioModifico;
	}

	public String getcEstatusCta() {
		return cEstatusCta;
	}

	public void setcEstatusCta( String cEstatusCta ) {
		this.cEstatusCta = cEstatusCta;
	}

	public String getcMotivoEliminaCta() {
		return cMotivoEliminaCta;
	}

	public void setcMotivoEliminaCta( String cMotivoEliminaCta ) {
		this.cMotivoEliminaCta = cMotivoEliminaCta;
	}

	public String getFolio() {
		return folio;
	}

	public void setFolio( String folio ) {
		this.folio = folio;
	}

	public String getRfc() {
		return rfc;
	}

	public void setRfc( String rfc ) {
		this.rfc = rfc;
	}

}
