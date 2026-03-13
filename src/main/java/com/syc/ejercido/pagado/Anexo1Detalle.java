package com.syc.ejercido.pagado;

public class Anexo1Detalle {
	private int		nFolioAnexo;
	private int		nDocRenglon;
	private String	Ep;
	private String 	cMes;
	private String	aEjercicioFiscal;
	private String	cCentroContable;
	private double	mImporte;
	private double	mImporteNegativo;
	private String	cEvento;
	private String	OBGT;
	
	public int getnFolioAnexo() {
		return nFolioAnexo;
	}
	public void setnFolioAnexo(int nFolioAnexo) {
		this.nFolioAnexo = nFolioAnexo;
	}

	public int getnDocRenglon() {
		return nDocRenglon;
	}
	public void setnDocRenglon(int nDocRenglon) {
		this.nDocRenglon = nDocRenglon;
	}
	
	public String getEp() {
		return Ep;
	}
	public void setEp(String ep) {
		Ep = ep;
	}
	public String getcMes() {
		return cMes;
	}
	public void setcMes(String cmes) {
		cMes = cmes;
	}
	
	public String getaEjercicioFiscal() {
		return aEjercicioFiscal;
	}
	public void setaEjercicioFiscal(String aEjercicioFiscal) {
		this.aEjercicioFiscal = aEjercicioFiscal;
	}
	
	public String getcCentroContable() {
		return cCentroContable;
	}
	public void setcCentroContable(String cCentroContable) {
		this.cCentroContable = cCentroContable;
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
	
	public String getcEvento() {
		return cEvento;
	}
	public void setcEvento(String cEvento) {
		this.cEvento = cEvento;
	}
	
	public String getOBGT() {
		return OBGT;
	}
	public void setOBGT(String OBGT) {
		this.OBGT = OBGT;
	}
}