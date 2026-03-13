package com.syc.registroingresos;

public class RegistrosIngresosDetalle {

	private int nDocRenglon;
	private String cEvento;
	private String EP;
	private double mImporte;
	private double mImporteNegativo;
	private int nMes;
	private String cCentroContable;
	private double mSaldo;
	private String CTAB;
	private double mImporteRendimientos;
	private double mImporteRendimientosGM;
	
	public int getnDocRenglon() {
		return nDocRenglon;
	}
	public void setnDocRenglon(int nDocRenglon) {
		this.nDocRenglon = nDocRenglon;
	}
	
	public String getcEvento() {
		return cEvento;
	}
	public void setcEvento(String cEvento) {
		this.cEvento = cEvento;
	}
	
	public String getEP() {
		return EP;
	}
	public void setEP(String EP) {
		this.EP = EP;
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
	
	public int getnMes() {
		return nMes;
	}
	public void setnMes(int nMes) {
		this.nMes = nMes;
	}
	
	public String getcCentroContable() {
		return cCentroContable;
	}
	public void setcCentroContable(String cCentroContable) {
		this.cCentroContable = cCentroContable;
	}
	
	public double getmSaldo() {
		return mSaldo;
	}
	public void setmSaldo(double mSaldo) {
		this.mSaldo = mSaldo;
	}
	
	public String getCTAB() {
		return CTAB;
	}
	public void setCTAB(String CTAB) {
		this.CTAB = CTAB;
	}
	
	public double getmImporteRendimientos() {
		return mImporteRendimientos;
	}	
	public void setmImporteRendimientos(double mImporteRendimientos) {
		this.mImporteRendimientos = mImporteRendimientos;
	}
	
	public double getmImporteRendimientosGM() {
		return mImporteRendimientosGM;
	}
	public void setmImporteRendimientosGM(double mImporteRendimientosGM) {
		this.mImporteRendimientosGM = mImporteRendimientosGM;
	}
}
