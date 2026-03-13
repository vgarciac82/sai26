package com.syc.contable.core;

public class ReintegroDetalle {
	private int		nDocRenglon;
	private int		noCLC;
	private int		secCLC;
	private int		codigoSaf;
	private String	EP;
	private int		cop;
	private double	mImporteCLC;
	private String	mImporteCLCFormat;
	private String	nCompromiso;
	private String	beneficiario;
	private String	suficiencia;
	private int		solOli;
	private String	tipoCon;
	private String	tipoDeCon;
	private int		mes;
	private int		mvto;
	private double	isr;
	private double	iva;
	private double	millar;
	private double	ivaDes;
	private Evento	cEvento;
	private String	nPartida;
	private String	cxp;
	private String	clcSicop;
	private String	tpag;
	private String	nres;
	private String	ncom;
	private String	obgt;
	private String	nFolioDependencia;
	private String cProgramaGeneral;
	private String cPasivo;
	private String rfc;

	public String getnFolioDependencia() {
		return nFolioDependencia;
	}

	public String getObgt() {
		return obgt;
	}

	public void setObgt(String obgt) {
		this.obgt = obgt;
	}

	public int getnDocRenglon() {
		return nDocRenglon;
	}

	public void setnDocRenglon(int nDocRenglon) {
		this.nDocRenglon = nDocRenglon;
	}

	public int getNoCLC() {
		return noCLC;
	}

	public void setNoCLC(int noCLC) {
		this.noCLC = noCLC;
	}

	public int getSecCLC() {
		return secCLC;
	}

	public void setSecCLC(int secCLC) {
		this.secCLC = secCLC;
	}

	public int getCodigoSaf() {
		return codigoSaf;
	}

	public void setCodigoSaf(int codigoSaf) {
		this.codigoSaf = codigoSaf;
	}

	public String getEP() {
		return EP;
	}

	public void setEP(String eP) {
		EP = eP;
	}

	public int getCop() {
		return cop;
	}

	public void setCop(int cop) {
		this.cop = cop;
	}

	public double getmImporteCLC() {
		return mImporteCLC;
	}

	public void setmImporteCLC(double mImporteCLC) {
		this.mImporteCLC = mImporteCLC;
	}

	public String getmImporteCLCFormat() {
		return mImporteCLCFormat;
	}

	public void setmImporteCLCFormat(String mImporteCLCFormat) {
		this.mImporteCLCFormat = mImporteCLCFormat;
	}

	public String getnCompromiso() {
		return nCompromiso;
	}

	public void setnCompromiso(String nCompromiso) {
		this.nCompromiso = nCompromiso;
	}

	public String getBeneficiario() {
		return beneficiario;
	}

	public void setBeneficiario(String beneficiario) {
		this.beneficiario = beneficiario;
	}

	public String getSuficiencia() {
		return suficiencia;
	}

	public void setSuficiencia(String suficiencia) {
		this.suficiencia = suficiencia;
	}

	public int getSolOli() {
		return solOli;
	}

	public void setSolOli(int solOli) {
		this.solOli = solOli;
	}

	public String getTipoCon() {
		return tipoCon;
	}

	public void setTipoCon(String tipoCon) {
		this.tipoCon = tipoCon;
	}

	public String getTipoDeCon() {
		return tipoDeCon;
	}

	public void setTipoDeCon(String tipoDeCon) {
		this.tipoDeCon = tipoDeCon;
	}

	public int getMes() {
		return mes;
	}

	public void setMes(int mes) {
		this.mes = mes;
	}

	public int getMvto() {
		return mvto;
	}

	public void setMvto(int mvto) {
		this.mvto = mvto;
	}

	public double getIsr() {
		return isr;
	}

	public void setIsr(double isr) {
		this.isr = isr;
	}

	public double getIva() {
		return iva;
	}

	public void setIva(double iva) {
		this.iva = iva;
	}

	public double getMillar() {
		return millar;
	}

	public void setMillar(double millar) {
		this.millar = millar;
	}

	public double getIvaDes() {
		return ivaDes;
	}

	public void setIvaDes(double ivaDes) {
		this.ivaDes = ivaDes;
	}

	public Evento getcEvento() {
		return cEvento;
	}

	public void setcEvento(Evento cEvento) {
		this.cEvento = cEvento;
	}

	public String getnPartida() {
		return nPartida;
	}

	public void setnPartida(String nPartida) {
		this.nPartida = nPartida;
	}

	public String getCxp() {
		return cxp;
	}

	public void setCxp(String cxp) {
		this.cxp = cxp;
	}

	public String getClcSicop() {
		return clcSicop;
	}

	public void setClcSicop(String clcSicop) {
		this.clcSicop = clcSicop;
	}

	public String getTpag() {
		return tpag;
	}

	public void setTpag(String tpag) {
		this.tpag = tpag;
	}

	public String getNres() {
		return nres;
	}
	public String getrfc() {
		return rfc;
	}

	public void setNres(String nres) {
		this.nres = nres;
	}

	public String getNcom() {
		return ncom;
	}

	public void setNcom(String ncom) {
		this.ncom = ncom;
	}

	public void setnFolioDependencia(String nFolioDependencia) {
		this.nFolioDependencia = nFolioDependencia;
	}
	
	public String getcProgramaGeneral() {
		return cProgramaGeneral;
	}

	public void setcProgramaGeneral(String cProgramaGeneral) {
		this.cProgramaGeneral = cProgramaGeneral;
	}

	public String getcPasivo() {
		return cPasivo;
	}

	public void setcPasivo(String cPasivo) {
		this.cPasivo = cPasivo;
	}
	public void setrfc(String rfc) {
		this.rfc = rfc;
	}
}
