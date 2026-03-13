package com.syc.contable.core;


public class ReintegroBoletos {
	private int		nfolioReintegro;
	private int		nFolioPagoDiverso;
	private String	RFC;
	private String	cNombre;
	private String	cReferencia;
	private double	mTotal;
	private String	cPartida;
	
	public int getNfolioReintegro() {
		return nfolioReintegro;
	}
	public void setNfolioReintegro( int nfolioReintegro ) {
		this.nfolioReintegro = nfolioReintegro;
	}
	public String getRFC() {
		return RFC;
	}
	public void setRFC( String rFC ) {
		RFC = rFC;
	}
	public int getnFolioPagoDiverso() {
		return nFolioPagoDiverso;
	}
	public void setnFolioPagoDiverso( int nFolioPagoDiverso ) {
		this.nFolioPagoDiverso = nFolioPagoDiverso;
	}
	public String getcReferencia() {
		return cReferencia;
	}
	public void setcReferencia( String cReferencia ) {
		this.cReferencia = cReferencia;
	}
	public String getcNombre() {
		return cNombre;
	}
	public void setcNombre( String cNombre ) {
		this.cNombre = cNombre;
	}
	public double getmTotal() {
		return mTotal;
	}
	public void setmTotal( double mTotal ) {
		this.mTotal = mTotal;
	}
	public String getcPartida() {
		return cPartida;
	}
	public void setcPartida( String cPartida ) {
		this.cPartida = cPartida;
	}
}
