package com.syc.contable;

public class DocPolizaDetalle {
	private String ADEFAS;
	private String aEjercicioFiscal;
	private String cCentroContable;
	private String cConcepto;
	private String cEvento;
	private String cTipoPoliza;
	private double mImporte;
	private String nCuenta;
	private int nDocRenglon;
	private long nFolioDocPoliza;
	private String nSubCuenta;
	private int nTipoAjuste;
	private String parcial;
	private String periodo13;
	private String referencia;

	public String getADEFAS() {
		return ADEFAS;
	}

	public String getaEjercicioFiscal() {
		return aEjercicioFiscal;
	}

	public String getcCentroContable() {
		return cCentroContable;
	}

	public String getcConcepto() {
		return cConcepto;
	}

	public String getcEvento() {
		return cEvento;
	}

	public String getcTipoPoliza() {
		return cTipoPoliza;
	}

	public double getmImporte() {
		return mImporte;
	}

	public String getnCuenta() {
		return nCuenta;
	}

	public int getnDocRenglon() {
		return nDocRenglon;
	}

	public long getnFolioDocPoliza() {
		return nFolioDocPoliza;
	}

	public String getnSubCuenta() {
		return nSubCuenta;
	}

	public int getnTipoAjuste() {
		return nTipoAjuste;
	}

	public String getParcial() {
		return parcial;
	}

	public String getPeriodo13() {
		return periodo13;
	}

	public String getReferencia() {
		return referencia;
	}

	public void setADEFAS(String aDEFAS) {
		ADEFAS = aDEFAS;
	}

	public void setaEjercicioFiscal(String aEjercicioFiscal) {
		this.aEjercicioFiscal = aEjercicioFiscal;
	}

	public void setcCentroContable(String cCentroContable) {
		this.cCentroContable = cCentroContable;
	}

	public void setcConcepto(String cConcepto) {
		this.cConcepto = cConcepto;
	}

	public void setcEvento(String cEvento) {
		this.cEvento = cEvento;
	}

	public void setcTipoPoliza(String cTipoPoliza) {
		this.cTipoPoliza = cTipoPoliza;
	}

	public void setmImporte(double mImporte) {
		this.mImporte = mImporte;
	}

	public void setnCuenta(String nCuenta) {
		this.nCuenta = nCuenta;
	}

	public void setnDocRenglon(int nDocRenglon) {
		this.nDocRenglon = nDocRenglon;
	}

	public void setnFolioDocPoliza(long nFolioDocPoliza) {
		this.nFolioDocPoliza = nFolioDocPoliza;
	}

	public void setnSubCuenta(String nSubCuenta) {
		this.nSubCuenta = nSubCuenta;
	}

	public void setnTipoAjuste(int nTipoAjuste) {
		this.nTipoAjuste = nTipoAjuste;
	}

	public void setParcial(String parcial) {
		this.parcial = parcial;
	}

	public void setPeriodo13(String periodo13) {
		this.periodo13 = periodo13;
	}

	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}

	@Override
	public String toString() {
		return "DocPolizaDetalle [ADEFAS=" + ADEFAS + ", aEjercicioFiscal="
				+ aEjercicioFiscal + ", cCentroContable=" + cCentroContable
				+ ", cConcepto=" + cConcepto + ", cEvento=" + cEvento
				+ ", cTipoPoliza=" + cTipoPoliza + ", mImporte=" + mImporte
				+ ", nCuenta=" + nCuenta + ", nDocRenglon=" + nDocRenglon
				+ ", nFolioDocPoliza=" + nFolioDocPoliza + ", nSubCuenta="
				+ nSubCuenta + ", nTipoAjuste=" + nTipoAjuste + ", parcial="
				+ parcial + ", periodo13=" + periodo13 + ", referencia="
				+ referencia + "]";
	}

}
