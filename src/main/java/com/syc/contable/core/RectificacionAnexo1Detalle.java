package com.syc.contable.core;

public class RectificacionAnexo1Detalle {
	private int		folio;
	private int		renglon;
	private String	evento;
	private String	ep;
	private String	epRectifica;
	private double	importe;
	private double	importeneg;
	private int		mes;
	private String	centro;
	private String	capitulo;
	private String	movimiento;
	private String	concepto;
	private double	importeRemanente	= 0d;
	private String  caNoContrarrecibo;

	public String getEpRectifica() {
		return epRectifica;
	}

	public void setEpRectifica(String epRectifica) {
		this.epRectifica = epRectifica;
	}

	public double getImporteRemanente() {
		return importeRemanente;
	}

	public void setImporteRemanente(double importeRemanente) {
		this.importeRemanente = importeRemanente;
	}

	public int getFolio() {
		return folio;
	}

	public void setFolio(int folio) {
		this.folio = folio;
	}

	public int getRenglon() {
		return renglon;
	}

	public void setRenglon(int renglon) {
		this.renglon = renglon;
	}

	public String getEvento() {
		return evento;
	}

	public void setEvento(String evento) {
		this.evento = evento;
	}

	public String getEp() {
		return ep;
	}

	public void setEp(String ep) {
		this.ep = ep;
	}

	public double getImporte() {
		return importe;
	}

	public void setImporte(double importe) {
		this.importe = importe;
	}

	public double getImporteneg() {
		return importeneg;
	}

	public void setImporteneg(double importeneg) {
		this.importeneg = importeneg;
	}

	public int getMes() {
		return mes;
	}

	public void setMes(int mes) {
		this.mes = mes;
	}

	public String getCentro() {
		return centro;
	}

	public void setCentro(String centro) {
		this.centro = centro;
	}

	public String getCapitulo() {
		return capitulo;
	}

	public void setCapitulo(String capitulo) {
		this.capitulo = capitulo;
	}

	public String getMovimiento() {
		return movimiento;
	}

	public void setMovimiento(String movimiento) {
		this.movimiento = movimiento;
	}

	public String getConcepto() {
		return concepto;
	}

	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}
	
	public String getcaNoContrarrecibo() {
		return caNoContrarrecibo;
	}

	public void setcaNoContrarrecibo(String caNoContrarrecibo) {
		this.caNoContrarrecibo = caNoContrarrecibo;
	}

	@Override
	public String toString() {
		return "RectificaAnexo1Detalle [folio=" + folio + ", renglon=" + renglon + ", evento=" + evento + ", ep=" + ep + ", epRectifica=" + epRectifica + ", importe=" + importe + ", importeneg=" + importeneg + ", mes=" + mes + ", centro=" + centro + ", capitulo=" + capitulo + ", movimiento="
			+ movimiento + ", concepto=" + concepto + ", importeRemanente=" + importeRemanente + ", caNoContrarrecibo=" + caNoContrarrecibo + "]";
	}

}
