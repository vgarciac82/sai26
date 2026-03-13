package com.syc.contable.core;

public class RetencionEncabezado {
	private int		nFolioRetencion;
	private int		nIdCaso;
	private String	cUnidadResponsable;
	private java.sql.Date	fCarga;
	private java.sql.Date	fAplicacion;				
	private String	cRamo;
	private int		nOrigenPPTO;
	private String	aEjercicioFiscal;
	private String	cCentroContable;
	private String	cIdRFC;
	private String	CTAB;
	private String	NOMBRE;
	private String	cConcepto;
	private String	nPorcIVA;
	private double	mImporteRetencion;
	private String  tipoPago;
	private String	caNoContrarrecibo;
	private String	cIdUsuarioCaptura;
	private String	cDocumentoHaplicado;
	private int		nFolioPoliza;
	private String	cTipoPoliza;
	private int		nEnviadoSICOP;
	private String	nCompromisoSICOP;
	private String	cUnidadResponsableContable;
	private int		nFolioPolizaCancelacion;
	private String	fCancelacion;
	private String  esIP;
	private String 	cPasivo_C;
	private int 	nFolioPago;
	private int 	nFolioSICOP;
	
	
	public int getnFolioPago() {
		return nFolioPago;
	}




	
	public void setnFolioPago( int nFolioPago ) {
		this.nFolioPago = nFolioPago;
	}




	public String getcPasivo_C() {
		return cPasivo_C;
	}



	
	public void setcPasivo_C( String cPasivo_C ) {
		this.cPasivo_C = cPasivo_C;
	}



	public int getnFolioRetencion() {
		return nFolioRetencion;
	}


	
	public void setnFolioRetencion( int nFolioRetencion ) {
		this.nFolioRetencion = nFolioRetencion;
	}


	
	public int getnIdCaso() {
		return nIdCaso;
	}


	
	public void setnIdCaso( int nIdCaso ) {
		this.nIdCaso = nIdCaso;
	}


	public String getTipoPago() {
		return tipoPago;
	}



	
	public void setTipoPago( String tipoPago ) {
		this.tipoPago = tipoPago;
	}

	public String getcUnidadResponsable() {
		return cUnidadResponsable;
	}


	
	public void setcUnidadResponsable( String cUnidadResponsable ) {
		this.cUnidadResponsable = cUnidadResponsable;
	}


	
	public java.sql.Date getfCarga() {
		return fCarga;
	}


	
	public void setfCarga( java.sql.Date fCarga ) {
		this.fCarga = fCarga;
	}


	
	public java.sql.Date getfAplicacion() {
		return fAplicacion;
	}


	
	public void setfAplicacion( java.sql.Date fAplicacion ) {
		this.fAplicacion = fAplicacion;
	}


	
	public String getcRamo() {
		return cRamo;
	}


	
	public void setcRamo( String cRamo ) {
		this.cRamo = cRamo;
	}


	
	public int getnOrigenPPTO() {
		return nOrigenPPTO;
	}


	
	public void setnOrigenPPTO( int nOrigenPPTO ) {
		this.nOrigenPPTO = nOrigenPPTO;
	}


	
	public String getaEjercicioFiscal() {
		return aEjercicioFiscal;
	}


	
	public void setaEjercicioFiscal( String aEjercicioFiscal ) {
		this.aEjercicioFiscal = aEjercicioFiscal;
	}


	
	public String getcCentroContable() {
		return cCentroContable;
	}


	
	public void setcCentroContable( String cCentroContable ) {
		this.cCentroContable = cCentroContable;
	}


	
	public String getcIdRFC() {
		return cIdRFC;
	}


	
	public void setcIdRFC( String cIdRFC ) {
		this.cIdRFC = cIdRFC;
	}


	
	public String getCTAB() {
		return CTAB;
	}


	
	public void setCTAB( String cTAB ) {
		CTAB = cTAB;
	}


	
	public String getNOMBRE() {
		return NOMBRE;
	}


	
	public void setNOMBRE( String nOMBRE ) {
		NOMBRE = nOMBRE;
	}


	
	public String getcConcepto() {
		return cConcepto;
	}


	
	public void setcConcepto( String cConcepto ) {
		this.cConcepto = cConcepto;
	}


	
	public String getnPorcIVA() {
		return nPorcIVA;
	}


	
	public void setnPorcIVA( String nPorcIVA ) {
		this.nPorcIVA = nPorcIVA;
	}


	
	public double getmImporteRetencion() {
		return mImporteRetencion;
	}


	
	public void setmImporteRetencion( double mImporteRetencion ) {
		this.mImporteRetencion = mImporteRetencion;
	}


	
	public String getCaNoContrarrecibo() {
		return caNoContrarrecibo;
	}


	
	public void setCaNoContrarrecibo( String caNoContrarrecibo ) {
		this.caNoContrarrecibo = caNoContrarrecibo;
	}


	
	public String getcIdUsuarioCaptura() {
		return cIdUsuarioCaptura;
	}


	
	public void setcIdUsuarioCaptura( String cIdUsuarioCaptura ) {
		this.cIdUsuarioCaptura = cIdUsuarioCaptura;
	}


	
	public String getcDocumentoHaplicado() {
		return cDocumentoHaplicado;
	}


	
	public void setcDocumentoHaplicado( String cDocumentoHaplicado ) {
		this.cDocumentoHaplicado = cDocumentoHaplicado;
	}


	
	public int getnFolioPoliza() {
		return nFolioPoliza;
	}


	
	public void setnFolioPoliza( int nFolioPoliza ) {
		this.nFolioPoliza = nFolioPoliza;
	}


	
	public String getcTipoPoliza() {
		return cTipoPoliza;
	}


	
	public void setcTipoPoliza( String cTipoPoliza ) {
		this.cTipoPoliza = cTipoPoliza;
	}


	
	public int getnEnviadoSICOP() {
		return nEnviadoSICOP;
	}


	
	public void setnEnviadoSICOP( int nEnviadoSICOP ) {
		this.nEnviadoSICOP = nEnviadoSICOP;
	}


	
	public String getnCompromisoSICOP() {
		return nCompromisoSICOP;
	}


	
	public void setnCompromisoSICOP( String nCompromisoSICOP ) {
		this.nCompromisoSICOP = nCompromisoSICOP;
	}


	
	public String getcUnidadResponsableContable() {
		return cUnidadResponsableContable;
	}


	
	public void setcUnidadResponsableContable( String cUnidadResponsableContable ) {
		this.cUnidadResponsableContable = cUnidadResponsableContable;
	}


	
	public int getnFolioPolizaCancelacion() {
		return nFolioPolizaCancelacion;
	}


	
	public void setnFolioPolizaCancelacion( int nFolioPolizaCancelacion ) {
		this.nFolioPolizaCancelacion = nFolioPolizaCancelacion;
	}


	
	public String getfCancelacion() {
		return fCancelacion;
	}


	
	public void setfCancelacion( String fCancelacion ) {
		this.fCancelacion = fCancelacion;
	}


	@Override
	public String toString() {
		return "RetencionEncabezado [nFolioRetencion=" + nFolioRetencion + ", nIdCaso=" + nIdCaso + ", cUnidadResponsable=" + cUnidadResponsable + ", fCarga=" + fCarga + ", fAplicacion=" + fAplicacion + ", cRamo=" + cRamo
			+ ", nOrigenPPTO=" + nOrigenPPTO + ", aEjercicioFiscal=" + aEjercicioFiscal + ", cCentroContable=" + cCentroContable + ", cIdRFC=" + cIdRFC + ", CTAB=" + CTAB + ", NOMBRE=" + NOMBRE + ", cConcepto=" + cConcepto + ", mImporteRetencion=" + mImporteRetencion + ", caNoContrarrecibo="
			+ caNoContrarrecibo + ", cIdUsuarioCaptura=" + cIdUsuarioCaptura + ", cDocumentoHaplicado=" + cDocumentoHaplicado + ", nFolioPoliza=" + nFolioPoliza + ", cTipoPoliza=" + cTipoPoliza + ", nEnviadoSICOP=" + nEnviadoSICOP + ", nCompromisoSICOP=" + nCompromisoSICOP + ", cUnidadResponsableContable=" + cUnidadResponsableContable + ", nFolioPolizaCancelacion="
			+ nFolioPolizaCancelacion + ", fCancelacion=" + fCancelacion +  ", nFolioPago=" + nFolioPago +  "]";
	}



	public String getEsIP() {
		return esIP;
	}



	public void setEsIP( String esIP ) {
		this.esIP = esIP;
	}





	public int getnFolioSICOP() {
		return nFolioSICOP;
	}





	public void setnFolioSICOP( int nFolioSICOP ) {
		this.nFolioSICOP = nFolioSICOP;
	}

	

}
