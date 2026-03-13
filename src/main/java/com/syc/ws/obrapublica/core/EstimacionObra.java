package com.syc.ws.obrapublica.core;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import com.syc.gestion.util.Util;

public class EstimacionObra {
	private String		cveContrato;
	private String		eFiscalPago;
	private Date		fEntregaVentanilla;
	private String		folioSAI;
	private Date		fPeriodoEstimacionFin;
	private Date		fPeriodoEstimacionIni;
	private int			mesEstimado;
	private BigDecimal	montoEstimacion;
	private BigDecimal	montoEstimacionAmortizado;
	private BigDecimal	montoEstimacionIva;
	private BigDecimal	montoEstimacionMasIva;
	private BigDecimal	montoEstimacionRetencion;
	private BigDecimal	montoFisicoEjecutado;
	private BigDecimal	montoFisicoProgramado;
	private int			noEstimacion;
	private BigDecimal	porceAvanceFisicoEjecutado;
	private BigDecimal	porceAvanceFisicoEstimado;
	private BigDecimal	porceAvanceFisicoProgramado;
	private boolean		esCapitalizable;
	private boolean		ultimaEstimacion;

	/**
	 * @return the cveContrato
	 */
	public String getCveContrato() {
		return cveContrato;
	}

	/**
	 * @param cveContrato
	 *            the cveContrato to set
	 */
	public void setCveContrato(String cveContrato) {
		this.cveContrato = cveContrato;
	}

	/**
	 * @return the eFiscalPago
	 */
	public String geteFiscalPago() {
		return eFiscalPago;
	}

	/**
	 * @param eFiscalPago
	 *            the eFiscalPago to set
	 */
	public void seteFiscalPago(String eFiscalPago) {
		this.eFiscalPago = eFiscalPago;
	}

	/**
	 * @return the fEntregaVentanilla
	 */
	public Date getfEntregaVentanilla() {
		return fEntregaVentanilla;
	}

	/**
	 * @param fEntregaVentanilla
	 *            the fEntregaVentanilla to set
	 */
	public void setfEntregaVentanilla(Date fEntregaVentanilla) {
		this.fEntregaVentanilla = fEntregaVentanilla;
	}

	/**
	 * @return the folioSAI
	 */
	public String getFolioSAI() {
		return folioSAI;
	}

	/**
	 * @param folioSAI
	 *            the folioSAI to set
	 */
	public void setFolioSAI(String folioSAI) {
		this.folioSAI = folioSAI;
	}

	/**
	 * @return the fPeriodoEstimacionFin
	 */
	public Date getfPeriodoEstimacionFin() {
		return fPeriodoEstimacionFin;
	}

	/**
	 * @param fPeriodoEstimacionFin
	 *            the fPeriodoEstimacionFin to set
	 */
	public void setfPeriodoEstimacionFin(Date fPeriodoEstimacionFin) {
		this.fPeriodoEstimacionFin = fPeriodoEstimacionFin;
	}

	/**
	 * @return the fPeriodoEstimacionIni
	 */
	public Date getfPeriodoEstimacionIni() {
		return fPeriodoEstimacionIni;
	}

	/**
	 * @param fPeriodoEstimacionIni
	 *            the fPeriodoEstimacionIni to set
	 */
	public void setfPeriodoEstimacionIni(Date fPeriodoEstimacionIni) {
		this.fPeriodoEstimacionIni = fPeriodoEstimacionIni;
	}

	/**
	 * @return the mesEstimado
	 */
	public int getMesEstimado() {
		return mesEstimado;
	}

	/**
	 * @param mesEstimado
	 *            the mesEstimado to set
	 */
	public void setMesEstimado(int mesEstimado) {
		this.mesEstimado = mesEstimado;
	}

	/**
	 * @return the montoEstimacion
	 */
	public BigDecimal getMontoEstimacion() {
		return montoEstimacion;
	}

	/**
	 * @param montoEstimacion
	 *            the montoEstimacion to set
	 */
	public void setMontoEstimacion(BigDecimal montoEstimacion) {
		this.montoEstimacion = montoEstimacion;
	}

	/**
	 * @return the montoEstimacionAmortizado
	 */
	public BigDecimal getMontoEstimacionAmortizado() {
		return montoEstimacionAmortizado;
	}

	/**
	 * @param montoEstimacionAmortizado
	 *            the montoEstimacionAmortizado to set
	 */
	public void setMontoEstimacionAmortizado(BigDecimal montoEstimacionAmortizado) {
		this.montoEstimacionAmortizado = montoEstimacionAmortizado;
	}

	/**
	 * @return the montoEstimacionIva
	 */
	public BigDecimal getMontoEstimacionIva() {
		return montoEstimacionIva;
	}

	/**
	 * @param montoEstimacionIva
	 *            the montoEstimacionIva to set
	 */
	public void setMontoEstimacionIva(BigDecimal montoEstimacionIva) {
		this.montoEstimacionIva = montoEstimacionIva;
	}

	/**
	 * @return the montoEstimacionMasIva
	 */
	public BigDecimal getMontoEstimacionMasIva() {
		return montoEstimacionMasIva;
	}

	/**
	 * @param montoEstimacionMasIva
	 *            the montoEstimacionMasIva to set
	 */
	public void setMontoEstimacionMasIva(BigDecimal montoEstimacionMasIva) {
		this.montoEstimacionMasIva = montoEstimacionMasIva;
	}

	/**
	 * @return the montoEstimacionRetencion
	 */
	public BigDecimal getMontoEstimacionRetencion() {
		return montoEstimacionRetencion;
	}

	/**
	 * @param montoEstimacionRetencion
	 *            the montoEstimacionRetencion to set
	 */
	public void setMontoEstimacionRetencion(BigDecimal montoEstimacionRetencion) {
		this.montoEstimacionRetencion = montoEstimacionRetencion;
	}

	/**
	 * @return the montoFisicoEjecutado
	 */
	public BigDecimal getMontoFisicoEjecutado() {
		return montoFisicoEjecutado;
	}

	/**
	 * @param montoFisicoEjecutado
	 *            the montoFisicoEjecutado to set
	 */
	public void setMontoFisicoEjecutado(BigDecimal montoFisicoEjecutado) {
		this.montoFisicoEjecutado = montoFisicoEjecutado;
	}

	/**
	 * @return the montoFisicoProgramado
	 */
	public BigDecimal getMontoFisicoProgramado() {
		return montoFisicoProgramado;
	}

	/**
	 * @param montoFisicoProgramado
	 *            the montoFisicoProgramado to set
	 */
	public void setMontoFisicoProgramado(BigDecimal montoFisicoProgramado) {
		this.montoFisicoProgramado = montoFisicoProgramado;
	}

	/**
	 * @return the noEstimacion
	 */
	public int getNoEstimacion() {
		return noEstimacion;
	}

	/**
	 * @param noEstimacion
	 *            the noEstimacion to set
	 */
	public void setNoEstimacion(int noEstimacion) {
		this.noEstimacion = noEstimacion;
	}

	/**
	 * @return the porceAvanceFisicoEjecutado
	 */
	public BigDecimal getPorceAvanceFisicoEjecutado() {
		return porceAvanceFisicoEjecutado;
	}

	/**
	 * @param porceAvanceFisicoEjecutado
	 *            the porceAvanceFisicoEjecutado to set
	 */
	public void setPorceAvanceFisicoEjecutado(BigDecimal porceAvanceFisicoEjecutado) {
		this.porceAvanceFisicoEjecutado = porceAvanceFisicoEjecutado;
	}

	/**
	 * @return the porceAvanceFisicoEstimado
	 */
	public BigDecimal getPorceAvanceFisicoEstimado() {
		return porceAvanceFisicoEstimado;
	}

	/**
	 * @param porceAvanceFisicoEstimado
	 *            the porceAvanceFisicoEstimado to set
	 */
	public void setPorceAvanceFisicoEstimado(BigDecimal porceAvanceFisicoEstimado) {
		this.porceAvanceFisicoEstimado = porceAvanceFisicoEstimado;
	}

	/**
	 * @return the porceAvanceFisicoProgramado
	 */
	public BigDecimal getPorceAvanceFisicoProgramado() {
		return porceAvanceFisicoProgramado;
	}

	/**
	 * @param porceAvanceFisicoProgramado
	 *            the porceAvanceFisicoProgramado to set
	 */
	public void setPorceAvanceFisicoProgramado(BigDecimal porceAvanceFisicoProgramado) {
		this.porceAvanceFisicoProgramado = porceAvanceFisicoProgramado;
	}

	/**
	 * @return the esCapitalizable
	 */
	public boolean isEsCapitalizable() {
		return esCapitalizable;
	}

	/**
	 * @param esCapitalizable
	 *            the esCapitalizable to set
	 */
	public void setEsCapitalizable(boolean esCapitalizable) {
		this.esCapitalizable = esCapitalizable;
	}

	/**
	 * @return the ultimaEstimacion
	 */
	public boolean isUltimaEstimacion() {
		return ultimaEstimacion;
	}

	/**
	 * @param ultimaEstimacion
	 *            the ultimaEstimacion to set
	 */
	public void setUltimaEstimacion(boolean ultimaEstimacion) {
		this.ultimaEstimacion = ultimaEstimacion;
	}
	
	/**
	 * @return the ultimaEstimacion
	 */
	public boolean getUltimaEstimacion() {
		return ultimaEstimacion;
	}
	
	/**
	 * @return the esCapitalizable
	 */
	public boolean getEsCapitalizable() {
		return esCapitalizable;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "EstimacionObra [cveContrato=" + cveContrato + ", eFiscalPago=" + eFiscalPago + ", fEntregaVentanilla=" + fEntregaVentanilla + ", folioSAI=" + folioSAI + ", fPeriodoEstimacionFin=" + fPeriodoEstimacionFin + ", fPeriodoEstimacionIni=" + fPeriodoEstimacionIni + ", mesEstimado="
			+ mesEstimado + ", montoEstimacion=" + montoEstimacion + ", montoEstimacionAmortizado=" + montoEstimacionAmortizado + ", montoEstimacionIva=" + montoEstimacionIva + ", montoEstimacionMasIva=" + montoEstimacionMasIva + ", montoEstimacionRetencion=" + montoEstimacionRetencion
			+ ", montoFisicoEjecutado=" + montoFisicoEjecutado + ", montoFisicoProgramado=" + montoFisicoProgramado + ", noEstimacion=" + noEstimacion + ", porceAvanceFisicoEjecutado=" + porceAvanceFisicoEjecutado + ", porceAvanceFisicoEstimado=" + porceAvanceFisicoEstimado
			+ ", porceAvanceFisicoProgramado=" + porceAvanceFisicoProgramado + ", esCapitalizable=" + esCapitalizable + ", ultimaEstimacion=" + ultimaEstimacion + "]";
	}

	public static EstimacionObra instanceFromRequest(HttpServletRequest request) throws Exception {
		EstimacionObra estimacion = new EstimacionObra();
		estimacion.setCveContrato(request.getParameter("cCveContrato"));
		estimacion.seteFiscalPago(request.getParameter("eFiscalPago"));
		estimacion.setfEntregaVentanilla(Util.stringToDate(request.getParameter("fEntregaVentanilla"), "dd/MM/yyyy"));
		estimacion.setFolioSAI(request.getParameter("FolioSAI"));
		estimacion.setfPeriodoEstimacionFin(Util.stringToDate(request.getParameter("fperiodoEstimacionFin"), "dd/MM/yyyy"));
		estimacion.setfPeriodoEstimacionIni(Util.stringToDate(request.getParameter("fperiodoEstimacionIni"), "dd/MM/yyyy"));
		estimacion.setMesEstimado(Integer.parseInt(request.getParameter("mesEstimado")));
		estimacion.setMontoEstimacion((new BigDecimal(request.getParameter("mMontoEstimacion") == null || "".equals(request.getParameter("mMontoEstimacion")) ? "0.00" : request.getParameter("mMontoEstimacion"))).setScale(2, RoundingMode.HALF_UP));
		estimacion.setMontoEstimacionAmortizado((new BigDecimal(request.getParameter("mMontoEstimacionAmortizado") == null || "".equals(request.getParameter("mMontoEstimacionAmortizado")) ? "0.00" : request.getParameter("mMontoEstimacionAmortizado"))).setScale(2, RoundingMode.HALF_UP));
		estimacion.setMontoEstimacionIva((new BigDecimal(request.getParameter("mMontoEstimacionIva") == null || "".equals(request.getParameter("mMontoEstimacionIva")) ? "0.00" : request.getParameter("mMontoEstimacionIva"))).setScale(2, RoundingMode.HALF_UP));
		estimacion.setMontoEstimacionMasIva((new BigDecimal(request.getParameter("mMontoEstimacionMasIva") == null || "".equals(request.getParameter("mMontoEstimacionMasIva"))? "0.00" : request.getParameter("mMontoEstimacionMasIva"))).setScale(2, RoundingMode.HALF_UP));
		estimacion.setMontoEstimacionRetencion((new BigDecimal(request.getParameter("mMontoEstimacionRetencion") == null || "".equals(request.getParameter("mMontoEstimacionRetencion")) ? "0.00" : request.getParameter("mMontoEstimacionRetencion"))).setScale(2, RoundingMode.HALF_UP));
		estimacion.setMontoFisicoEjecutado((new BigDecimal(request.getParameter("mmontoFisicoEjecutado") == null || "".equals(request.getParameter("mmontoFisicoEjecutado")) ? "0.00" : request.getParameter("mmontoFisicoEjecutado"))).setScale(2, RoundingMode.HALF_UP));
		estimacion.setMontoFisicoProgramado((new BigDecimal(request.getParameter("mmontoFisicoProgramado") == null || "".equals(request.getParameter("mmontoFisicoProgramado")) ? "0.00" : request.getParameter("mmontoFisicoProgramado"))).setScale(2, RoundingMode.HALF_UP));
		estimacion.setNoEstimacion(Integer.parseInt(request.getParameter("noEstimacion")));
		estimacion.setPorceAvanceFisicoEjecutado((new BigDecimal(request.getParameter("nPorceAvanceFisicoEjecutado") == null || "".equals(request.getParameter("nPorceAvanceFisicoEjecutado"))? "0.00" : request.getParameter("nPorceAvanceFisicoEjecutado"))).setScale(2, RoundingMode.HALF_UP));
		estimacion.setPorceAvanceFisicoEstimado((new BigDecimal(request.getParameter("nPorceAvanceFisicoEstimado") == null || "".equals(request.getParameter("nPorceAvanceFisicoEstimado")) ? "0.00" : request.getParameter("nPorceAvanceFisicoEstimado"))).setScale(2, RoundingMode.HALF_UP));
		estimacion.setPorceAvanceFisicoProgramado((new BigDecimal(request.getParameter("nPorceAvanceFisicoProgramado") == null || "".equals(request.getParameter("nPorceAvanceFisicoProgramado")) ? "0.00" : request.getParameter("nPorceAvanceFisicoProgramado"))).setScale(2, RoundingMode.HALF_UP));
		estimacion.setEsCapitalizable("true".equalsIgnoreCase(request.getParameter("esCapitalizable")));
		estimacion.setUltimaEstimacion("true".equalsIgnoreCase(request.getParameter("ultimaEstimacion")));
		return estimacion;
	}

}
