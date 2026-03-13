package com.syc.contable.core;

public class RectificacionIngresoFiscalDetalle {	
	
	private int		nFolioRectificaIngreso;
	private int		nDocRenglon;
	private String	EP;
	private String	cEvento;	
	private double	mImporte;
	private double	mImporteNegativo;
	private int		cMes;
	private String	cCentroContable;
	private String	CTAB;
	private String	OBGT;
	private String	caNoContrarrecibo;
	
	
	public int getnFolioRectificaIngreso() {
		return nFolioRectificaIngreso;
	}

	public void setnFolioRectificaIngreso(int nFolioRectificaIngreso) {
		this.nFolioRectificaIngreso = nFolioRectificaIngreso;
	}
	
	public int getnDocRenglon() {
		return nDocRenglon;
	}

	public void setnDocRenglon(int nDocRenglon) {
		this.nDocRenglon = nDocRenglon;
	}
	
	public String getEP() {
		return EP;
	}

	public void setEP(String EP) {
		this.EP = EP;
	}	

	public String getcEvento() {
		return cEvento;
	}

	public void setcEvento(String cEvento) {
		this.cEvento = cEvento;
	}

	public double getmImporte() {
		return mImporte;
	}

	public void setmImporte(double mImporte) {
		this.mImporte = mImporte;
	}

	public double getmImporteNegativo() {
		return mImporteNegativo;
	}

	public void setmImporteNegativo(double mImporteNegativo) {
		this.mImporteNegativo = mImporteNegativo;
	}

	public int getcMes() {
		return cMes;
	}

	public void setcMes(int cMes) {
		this.cMes = cMes;
	}

	public String getcCentroContable() {
		return cCentroContable;
	}

	public void setcCentroContable(String cCentroContable) {
		this.cCentroContable = cCentroContable;
	}

	public String getCTAB() {
		return CTAB;
	}

	public void setCTAB(String CTAB) {
		this.CTAB = CTAB;
	}

	public String getOBGT() {
		return OBGT;
	}

	public void setOBGT(String OBGT) {
		this.OBGT = OBGT;
	}
	
	
	public String getcaNoContrarrecibo() {
		return caNoContrarrecibo;
	}

	public void setcaNoContrarrecibo(String caNoContrarrecibo) {
		this.caNoContrarrecibo = caNoContrarrecibo;
	}

	@Override
	public String toString() {
		return "RectificacionIngresoFiscalDetalle [ nFolioRectificaIngreso = " + nFolioRectificaIngreso + ", nDocRenglon = " + nDocRenglon + ", EP = " + EP + ", cEvento = " + cEvento + ", mImporte = " + mImporte + ", mImporteNegativo = " + mImporteNegativo 
			+ ", cMes = " + cMes + ", cCentroContable = " + cCentroContable + ", CTAB = " + CTAB + ", OBGT = " + OBGT + ", caNoContrarrecibo=" + caNoContrarrecibo + "]";
	}
	

}
