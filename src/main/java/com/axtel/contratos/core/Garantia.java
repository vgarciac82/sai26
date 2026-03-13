package com.axtel.contratos.core;


/**
 * @author hfariasr
 *
 */
public class Garantia {
	private int nTipoGarantia;
	private String cAseguradora;
	private int lFianza;
	private int lCheque;
	private String fFechaExpedicion;
	private String cNumeroChequeFianza;
	private float mMontoGarantia;
	private int nIdConsecutivoEndosoGarantia;
	
	public int getnTipoGarantia() {
		return nTipoGarantia;
	}
	
	public void setnTipoGarantia( int nTipoGarantia ) {
		this.nTipoGarantia = nTipoGarantia;
	}
	
	public String getcAseguradora() {
		return cAseguradora;
	}
	
	public void setcAseguradora( String cAseguradora ) {
		this.cAseguradora = cAseguradora;
	}
	
	public int getlFianza() {
		return lFianza;
	}
	
	public void setlFianza( int lFianza ) {
		this.lFianza = lFianza;
	}
	
	public int getlCheque() {
		return lCheque;
	}
	
	public void setlCheque( int lCheque ) {
		this.lCheque = lCheque;
	}
	
	public String getfFechaExpedicion() {
		return fFechaExpedicion;
	}
	
	public void setfFechaExpedicion( String fFechaExpedicion ) {
		this.fFechaExpedicion = fFechaExpedicion;
	}
	
	public String getcNumeroChequeFianza() {
		return cNumeroChequeFianza;
	}
	
	public void setcNumeroChequeFianza( String cNumeroChequeFianza ) {
		this.cNumeroChequeFianza = cNumeroChequeFianza;
	}
	
	public float getmMontoGarantia() {
		return mMontoGarantia;
	}
	
	public void setmMontoGarantia( float mMontoGarantia ) {
		this.mMontoGarantia = mMontoGarantia;
	}

	public int getnIdConsecutivoEndosoGarantia() {
		return nIdConsecutivoEndosoGarantia;
	}

	public void setnIdConsecutivoEndosoGarantia( int nIdConsecutivoEndosoGarantia ) {
		this.nIdConsecutivoEndosoGarantia = nIdConsecutivoEndosoGarantia;
	}
	
}
