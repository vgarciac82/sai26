
package com.axtel.contabilidad.reintegrosCaja.core;

/**
 * @author Ana
 *
 */
public class ReintegrosCajaDetalle {
	private int		nFolioReintegroCaja;
	private int		nDocRenglon;
	private String	cEvento;
	private String	cEventoDestino;
	private double	mImporte;
	private double	mImporteNegativo;
	private String	ALM;
	private String	CTAB;
	private String	OBGT;
	private String	RFC;
	private String	EP;
	private String	nCuentaBeneficiario;
	private String	FFM;
	private String	cCentroContable;
	private String	cUnidadResponsable;
	private String	dNombre;
	private String	ctaBeneficiario;
	private String	dFFM;
	private String	nomCTAB;

	public void setnFolioReintegroCaja( int nFolioReintegroCaja ) {
		this.nFolioReintegroCaja = nFolioReintegroCaja;
	}
	
	public void setnDocRenglon( int nDocRenglon ) {
		this.nDocRenglon = nDocRenglon;
	}
	
	public void setcEvento( String cEvento ) {
		this.cEvento = cEvento;
	}
	
	public void setcEventoDestino( String cEventoDestino ) {
		this.cEventoDestino = cEventoDestino;
	}
	
	public void setmImporte( double mImporte ) {
		this.mImporte = mImporte;
	}
	
	public void setmImporteNegativo( double mImporteNegativo ) {
		this.mImporteNegativo = mImporteNegativo;
	}
	
	public void setALM( String aLM ) {
		ALM = aLM;
	}
	
	public void setCTAB( String cTAB ) {
		CTAB = cTAB;
	}
	
	public void setOBGT( String oBGT ) {
		OBGT = oBGT;
	}
	
	public void setRFC( String rFC ) {
		RFC = rFC;
	}
	
	public void setEP( String eP ) {
		EP = eP;
	}
	
	public void setnCuentaBeneficiario( String nCuentaBeneficiario ) {
		this.nCuentaBeneficiario = nCuentaBeneficiario;
	}
	
	public void setFFM( String fFM ) {
		FFM = fFM;
	}
	
	public void setcCentroContable( String cCentroContable ) {
		this.cCentroContable = cCentroContable;
	}
	
	public void setcUnidadResponsable( String cUnidadResponsable ) {
		this.cUnidadResponsable = cUnidadResponsable;
	}
		
	public void setdNombre( String dNombre ) {
		this.dNombre = dNombre;
	}
	
	public void setctaBeneficiario( String ctaBeneficiario ) {
		this.ctaBeneficiario = ctaBeneficiario;
	}
	
	public void setdFFM( String dFFM ) {
		this.dFFM = dFFM;
	}
	
	public void setnomCTAB( String nomCTAB ) {
		this.nomCTAB = nomCTAB;
	}
	

	public int getnFolioReintegroCaja() {
		return nFolioReintegroCaja;
	}
	
	public int getnDocRenglon() {
		return nDocRenglon;
	}
	
	public String getcEvento() {
		return cEvento;
	}
	
	public String getcEventoDestino() {
		return cEventoDestino;
	}
	
	public double getmImporte() {
		return mImporte;
	}
	
	public double getmImporteNegativo() {
		return mImporteNegativo;
	}
	
	public String getALM() {
		return ALM;
	}
	
	public String getCTAB() {
		return CTAB;
	}
	
	public String getOBGT() {
		return OBGT;
	}
	
	public String getRFC() {
		return RFC;
	}
	
	public String getEP() {
		return EP;
	}
	
	public String getnCuentaBeneficiario() {
		return nCuentaBeneficiario;
	}
	
	public String getFFM() {
		return FFM;
	}
	
	public String getcCentroContable() {
		return cCentroContable;
	}
	
	public String getcUnidadResponsable() {
		return cUnidadResponsable;
	}
	
	public String getdNombre() {
		return dNombre;
	}
	
	public String getctaBeneficiario() {
		return ctaBeneficiario;
	}
	
	public String getdFFM() {
		return dFFM;
	}
	
	public String getnomCTAB() {
		return nomCTAB;
	}
	
}
