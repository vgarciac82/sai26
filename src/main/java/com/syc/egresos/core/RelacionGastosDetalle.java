package com.syc.egresos.core;

import java.math.BigDecimal;

public class RelacionGastosDetalle {
	private char		ADEFAS		= 'N';
	private String		aEjercicioFiscal;
	private String		ALM;
	private String		altaAlmacen;
	private String		cCentroContable;
	private char		cDocumentoHaplicado;
	private String		cEjercicio;
	private String		cEvento;
	private String		cIdCuentaContable;
	private String		cIdEntidadContable;
	private String		cIdRelacion;
	private String		cMes;
	private String		cPasivo;
	private String		CTAB;
	private String		cTipoPoliza;
	private String		EP;
	private String		FFM;
	private String		ID_TIPO_CONCEPTO;
	private String		ID_TIPO_MOVIMIENTO;
	private String		m23IVA;
	private String		m2Millar;
	private String		m5Millar;
	private String		mAmortizacionAnticipo;
	private String		mBruto;
	private String		mCedular;
	private BigDecimal	mCNIC;
	private BigDecimal	mComprometido;
	private String		mDevolucion;
	private String		mFletes;
	private BigDecimal	mIMDT;
	private String		mImporte;
	private String		mImporteAmortiza;
	private BigDecimal	mImporteBruto;
	private BigDecimal	mImporteFlete23;
	private String		mImporteFlete4;
	private BigDecimal	mImporteHospedaje = new BigDecimal( 0.00d);
	private BigDecimal	mImporteISRLaudos;
	private BigDecimal	mImporteIva;
	private BigDecimal	mImporteIvaArrenda;
	private BigDecimal	mImporteIvaHonorarios;
	private BigDecimal	mImporteIvaProv;
	private BigDecimal	mImporteMasIva;
	private BigDecimal	mImporteNegativo;
	private BigDecimal	mImporteNeto;
	private BigDecimal	mImporteObra;
	private String		mISRArrenda;
	private String		mISRHonorarios;
	private BigDecimal	mISROtros = new BigDecimal(0.0d);
	private String		mIVA;
	private String		mNeto;
	private String		mObra5;
	private String		mPenalizacion;
	private String		mRetencion;
	private String		mRetImpuestoCedular;
	private String		mSancion;
	private BigDecimal	mTesofe;
	private String		nCapitulo;
	private int			nDocRenglon;
	private int			nFolioPoliza;
	private int			nFolioRELACIONGASTOS;
	private int			nMes;
	private int			nPoliza;
	private String		OBGT;
	private char		Periodo13	= 'N';
	private BigDecimal 	mISRResico;

	private String		RFC;

	public char getADEFAS() {
		return ADEFAS;
	}

	public String getaEjercicioFiscal() {
		return aEjercicioFiscal;
	}

	public String getALM() {
		return ALM;
	}

	public String getAltaAlmacen() {
		return altaAlmacen;
	}

	public String getcCentroContable() {
		return cCentroContable;
	}

	public char getcDocumentoHaplicado() {
		return cDocumentoHaplicado;
	}

	public String getcEjercicio() {
		return cEjercicio;
	}

	public String getcEvento() {
		return cEvento;
	}

	public String getcIdCuentaContable() {
		return cIdCuentaContable;
	}

	public String getcIdEntidadContable() {
		return cIdEntidadContable;
	}

	public String getcIdRelacion() {
		return cIdRelacion;
	}

	public String getcMes() {
		return cMes;
	}

	public String getcPasivo() {
		return cPasivo;
	}

	public String getCTAB() {
		return CTAB;
	}

	public String getcTipoPoliza() {
		return cTipoPoliza;
	}

	public String getEP() {
		return EP;
	}

	public String getFFM() {
		return FFM;
	}

	public String getID_TIPO_CONCEPTO() {
		return ID_TIPO_CONCEPTO;
	}

	public String getID_TIPO_MOVIMIENTO() {
		return ID_TIPO_MOVIMIENTO;
	}

	public String getM23IVA() {
		return m23IVA;
	}

	public String getM2Millar() {
		return m2Millar;
	}

	public String getM5Millar() {
		return m5Millar;
	}

	public String getmAmortizacionAnticipo() {
		return mAmortizacionAnticipo;
	}

	public String getmBruto() {
		return mBruto;
	}

	public String getmCedular() {
		return mCedular;
	}

	public BigDecimal getmCNIC() {
		return mCNIC;
	}

	public BigDecimal getmComprometido() {
		return mComprometido;
	}

	public String getmDevolucion() {
		return mDevolucion;
	}

	public String getmFletes() {
		return mFletes;
	}

	public BigDecimal getmIMDT() {
		return mIMDT;
	}

	public String getmImporte() {
		return mImporte;
	}

	public String getmImporteAmortiza() {
		return mImporteAmortiza;
	}

	public BigDecimal getmImporteBruto() {
		return mImporteBruto;
	}

	public BigDecimal getmImporteFlete23() {
		return mImporteFlete23;
	}

	public String getmImporteFlete4() {
		return mImporteFlete4;
	}

	public BigDecimal getmImporteHospedaje() {
		return mImporteHospedaje;
	}

	public BigDecimal getmImporteISRLaudos() {
		return mImporteISRLaudos;
	}

	public BigDecimal getmImporteIva() {
		return mImporteIva;
	}

	public BigDecimal getmImporteIvaArrenda() {
		return mImporteIvaArrenda;
	}

	public BigDecimal getmImporteIvaHonorarios() {
		return mImporteIvaHonorarios;
	}

	public BigDecimal getmImporteIvaProv() {
		return mImporteIvaProv;
	}

	public BigDecimal getmImporteMasIva() {
		return mImporteMasIva;
	}

	public BigDecimal getmImporteNegativo() {
		
		if( mImporteNegativo == null && mImporteMasIva != null )
			return mImporteMasIva.multiply( new BigDecimal(-1.0d));
		
		return mImporteNegativo;
	}

	public BigDecimal getmImporteNeto() {
		return mImporteNeto;
	}

	public BigDecimal getmImporteObra() {
		return mImporteObra;
	}

	public String getmISRArrenda() {
		return mISRArrenda;
	}

	public String getmISRHonorarios() {
		return mISRHonorarios;
	}

	public BigDecimal getmISROtros() {
		return mISROtros;
	}

	public String getmIVA() {
		return mIVA;
	}

	public String getmNeto() {
		return mNeto;
	}

	public String getmObra5() {
		return mObra5;
	}

	public String getmPenalizacion() {
		return mPenalizacion;
	}

	public String getmRetencion() {
		return mRetencion;
	}

	public String getmRetImpuestoCedular() {
		return mRetImpuestoCedular;
	}

	public String getmSancion() {
		return mSancion;
	}

	public BigDecimal getmTesofe() {
		return mTesofe;
	}

	public String getnCapitulo() {
		return nCapitulo;
	}

	public int getnDocRenglon() {
		return nDocRenglon;
	}

	public int getnFolioPoliza() {
		return nFolioPoliza;
	}

	public int getnFolioRELACIONGASTOS() {
		return nFolioRELACIONGASTOS;
	}

	public int getnMes() {
		return nMes;
	}

	public int getnPoliza() {
		return nPoliza;
	}

	public String getOBGT() {
		return OBGT;
	}

	public char getPeriodo13() {
		return Periodo13;
	}

	public String getRFC() {
		return RFC;
	}

	public void setADEFAS(char aDEFAS) {
		ADEFAS = aDEFAS;
	}

	public void setaEjercicioFiscal(String aEjercicioFiscal) {
		this.aEjercicioFiscal = aEjercicioFiscal;
	}

	public void setALM(String aLM) {
		ALM = aLM;
	}

	public void setAltaAlmacen(String altaAlmacen) {
		this.altaAlmacen = altaAlmacen;
	}

	public void setcCentroContable(String cCentroContable) {
		this.cCentroContable = cCentroContable;
	}

	public void setcDocumentoHaplicado(char cDocumentoHaplicado) {
		this.cDocumentoHaplicado = cDocumentoHaplicado;
	}

	public void setcEjercicio(String cEjercicio) {
		this.cEjercicio = cEjercicio;
	}

	public void setcEvento(String cEvento) {
		this.cEvento = cEvento;
	}

	public void setcIdCuentaContable(String cIdCuentaContable) {
		this.cIdCuentaContable = cIdCuentaContable;
	}

	public void setcIdEntidadContable(String cIdEntidadContable) {
		this.cIdEntidadContable = cIdEntidadContable;
	}

	public void setcIdRelacion(String cIdRelacion) {
		this.cIdRelacion = cIdRelacion;
	}

	public void setcMes(String cMes) {
		this.cMes = cMes;
	}

	public void setcPasivo(String cPasivo) {
		this.cPasivo = cPasivo;
	}

	public void setCTAB(String cTAB) {
		CTAB = cTAB;
	}

	public void setcTipoPoliza(String cTipoPoliza) {
		this.cTipoPoliza = cTipoPoliza;
	}

	public void setEP(String eP) {
		EP = eP;
	}

	public void setFFM(String fFM) {
		FFM = fFM;
	}

	public void setID_TIPO_CONCEPTO(String iD_TIPO_CONCEPTO) {
		ID_TIPO_CONCEPTO = iD_TIPO_CONCEPTO;
	}

	public void setID_TIPO_MOVIMIENTO(String iD_TIPO_MOVIMIENTO) {
		ID_TIPO_MOVIMIENTO = iD_TIPO_MOVIMIENTO;
	}

	public void setM23IVA(String m23iva) {
		m23IVA = m23iva;
	}

	public void setM2Millar(String m2Millar) {
		this.m2Millar = m2Millar;
	}

	public void setM5Millar(String m5Millar) {
		this.m5Millar = m5Millar;
	}

	public void setmAmortizacionAnticipo(String mAmortizacionAnticipo) {
		this.mAmortizacionAnticipo = mAmortizacionAnticipo;
	}

	public void setmBruto(String mBruto) {
		this.mBruto = mBruto;
	}

	public void setmCedular(String mCedular) {
		this.mCedular = mCedular;
	}

	public void setmCNIC(BigDecimal mCNIC) {
		this.mCNIC = mCNIC;
	}

	public void setmComprometido(BigDecimal mComprometido) {
		this.mComprometido = mComprometido;
	}

	public void setmDevolucion(String mDevolucion) {
		this.mDevolucion = mDevolucion;
	}

	public void setmFletes(String mFletes) {
		this.mFletes = mFletes;
	}

	public void setmIMDT(BigDecimal mIMDT) {
		this.mIMDT = mIMDT;
	}

	public void setmImporte(String mImporte) {
		this.mImporte = mImporte;
	}

	public void setmImporteAmortiza(String mImporteAmortiza) {
		this.mImporteAmortiza = mImporteAmortiza;
	}

	public void setmImporteBruto(BigDecimal mImporteBruto) {
		this.mImporteBruto = mImporteBruto;
	}

	public void setmImporteFlete23(BigDecimal mImporteFlete23) {
		this.mImporteFlete23 = mImporteFlete23;
	}

	public void setmImporteFlete4(String mImporteFlete4) {
		this.mImporteFlete4 = mImporteFlete4;
	}

	public void setmImporteHospedaje(BigDecimal mImporteHospedaje) {
		this.mImporteHospedaje = mImporteHospedaje;
	}

	public void setmImporteISRLaudos(BigDecimal mImporteISRLaudos) {
		this.mImporteISRLaudos = mImporteISRLaudos;
	}

	public void setmImporteIva(BigDecimal mImporteIva) {
		this.mImporteIva = mImporteIva;
	}

	public void setmImporteIvaArrenda(BigDecimal mImporteIvaArrenda) {
		this.mImporteIvaArrenda = mImporteIvaArrenda;
	}

	public void setmImporteIvaHonorarios(BigDecimal mImporteIvaHonorarios) {
		this.mImporteIvaHonorarios = mImporteIvaHonorarios;
	}

	public void setmImporteIvaProv(BigDecimal mImporteIvaProv) {
		this.mImporteIvaProv = mImporteIvaProv;
	}

	public void setmImporteMasIva(BigDecimal mImporteMasIva) {
		this.mImporteMasIva = mImporteMasIva;
	}

	public void setmImporteNegativo(BigDecimal mImporteNegativo) {
		this.mImporteNegativo = mImporteNegativo;
	}

	public void setmImporteNeto(BigDecimal mImporteNeto) {
		this.mImporteNeto = mImporteNeto;
	}

	public void setmImporteObra(BigDecimal mImporteObra) {
		this.mImporteObra = mImporteObra;
	}

	public void setmISRArrenda(String mISRArrenda) {
		this.mISRArrenda = mISRArrenda;
	}

	public void setmISRHonorarios(String mISRHonorarios) {
		this.mISRHonorarios = mISRHonorarios;
	}

	public void setmISROtros(BigDecimal mISROtros) {
		this.mISROtros = mISROtros;
	}

	public void setmIVA(String mIVA) {
		this.mIVA = mIVA;
	}

	public void setmNeto(String mNeto) {
		this.mNeto = mNeto;
	}

	public void setmObra5(String mObra5) {
		this.mObra5 = mObra5;
	}

	public void setmPenalizacion(String mPenalizacion) {
		this.mPenalizacion = mPenalizacion;
	}

	public void setmRetencion(String mRetencion) {
		this.mRetencion = mRetencion;
	}

	public void setmRetImpuestoCedular(String mRetImpuestoCedular) {
		this.mRetImpuestoCedular = mRetImpuestoCedular;
	}

	public void setmSancion(String mSancion) {
		this.mSancion = mSancion;
	}

	public void setmTesofe(BigDecimal mTesofe) {
		this.mTesofe = mTesofe;
	}

	public void setnCapitulo(String nCapitulo) {
		this.nCapitulo = nCapitulo;
	}

	public void setnDocRenglon(int nDocRenglon) {
		this.nDocRenglon = nDocRenglon;
	}

	public void setnFolioPoliza(int nFolioPoliza) {
		this.nFolioPoliza = nFolioPoliza;
	}

	public void setnFolioRELACIONGASTOS(int nFolioRELACIONGASTOS) {
		this.nFolioRELACIONGASTOS = nFolioRELACIONGASTOS;
	}

	public void setnMes(int nMes) {
		this.nMes = nMes;
	}

	public void setnPoliza(int nPoliza) {
		this.nPoliza = nPoliza;
	}

	public void setOBGT(String oBGT) {
		OBGT = oBGT;
	}

	public void setPeriodo13(char periodo13) {
		Periodo13 = periodo13;
	}

	public void setRFC(String rFC) {
		RFC = rFC;
	}

	public BigDecimal getmISRResico() {
		return mISRResico;
	}

	public void setmISRResico( BigDecimal mISRResico ) {
		this.mISRResico = mISRResico;
	}
}
