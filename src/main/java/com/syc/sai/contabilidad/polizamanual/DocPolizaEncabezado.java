package com.syc.sai.contabilidad.polizamanual;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * TdocPolizaEncabezado entity. @author MyEclipse Persistence Tools
 */

public class DocPolizaEncabezado implements java.io.Serializable {

	// Fields	
	private String fcarga;
	private String faplicacion;
	private String cramo;
	private String cunidadResponsable;
	private String cdocumentoHaplicado;
	private Integer nfolioPoliza;
	private Short nmes;
	private String crevisado;
	private String cunidadResponsableContable;
	private Integer nfolioPolizaCancelacion;
	private String fcancelacion;
	private String cdescripcionPoliza;
	private String cconcepto;
	private String cidUsuarioCaptura;
	private String cidUsuarioRevision;
	private String cidUsuarioAprobacion;
	private String cidOrigen;
	private Double mtotalCargos;
	private Double mtotalAbonos;
	private String ctipoDocumento;
	private String ccomentarios;
	private Integer ncambio;
	private BigDecimal nidCasoOrigen;
	private String periodo13;
	private String adefas;
	private Integer ntipoAjuste;
	
	private String aejercicioFiscal;
	private String ccentroContable;
	private Integer nfolioDocPoliza;
	private String ctipoPoliza;
	private Integer nFormatoPoliza;



	public String getFcarga() {
		return this.fcarga;
	}

	public void setFcarga(String fcarga) {
		this.fcarga = fcarga;
	}

	public String getFaplicacion() {
		return this.faplicacion;
	}

	public void setFaplicacion(String faplicacion) {
		this.faplicacion = faplicacion;
	}

	public String getCramo() {
		return this.cramo;
	}

	public void setCramo(String cramo) {
		this.cramo = cramo;
	}

	public String getCunidadResponsable() {
		return this.cunidadResponsable;
	}

	public void setCunidadResponsable(String cunidadResponsable) {
		this.cunidadResponsable = cunidadResponsable;
	}

	public String getCdocumentoHaplicado() {
		return this.cdocumentoHaplicado;
	}

	public void setCdocumentoHaplicado(String cdocumentoHaplicado) {
		this.cdocumentoHaplicado = cdocumentoHaplicado;
	}

	public Integer getNfolioPoliza() {
		return this.nfolioPoliza;
	}

	public void setNfolioPoliza(Integer nfolioPoliza) {
		this.nfolioPoliza = nfolioPoliza;
	}

	public Short getNmes() {
		return this.nmes;
	}

	public void setNmes(Short nmes) {
		this.nmes = nmes;
	}

	public String getCrevisado() {
		return this.crevisado;
	}

	public void setCrevisado(String crevisado) {
		this.crevisado = crevisado;
	}

	public String getCunidadResponsableContable() {
		return this.cunidadResponsableContable;
	}

	public void setCunidadResponsableContable(String cunidadResponsableContable) {
		this.cunidadResponsableContable = cunidadResponsableContable;
	}

	public Integer getNfolioPolizaCancelacion() {
		return this.nfolioPolizaCancelacion;
	}

	public void setNfolioPolizaCancelacion(Integer nfolioPolizaCancelacion) {
		this.nfolioPolizaCancelacion = nfolioPolizaCancelacion;
	}

	public String getFcancelacion() {
		return this.fcancelacion;
	}

	public void setFcancelacion(String fcancelacion) {
		this.fcancelacion = fcancelacion;
	}

	public String getCdescripcionPoliza() {
		return this.cdescripcionPoliza;
	}

	public void setCdescripcionPoliza(String cdescripcionPoliza) {
		this.cdescripcionPoliza = cdescripcionPoliza;
	}

	public String getCconcepto() {
		return this.cconcepto;
	}

	public void setCconcepto(String cconcepto) {
		this.cconcepto = cconcepto;
	}

	public String getCidUsuarioCaptura() {
		return this.cidUsuarioCaptura;
	}

	public void setCidUsuarioCaptura(String cidUsuarioCaptura) {
		this.cidUsuarioCaptura = cidUsuarioCaptura;
	}

	public String getCidUsuarioRevision() {
		return this.cidUsuarioRevision;
	}

	public void setCidUsuarioRevision(String cidUsuarioRevision) {
		this.cidUsuarioRevision = cidUsuarioRevision;
	}

	public String getCidUsuarioAprobacion() {
		return this.cidUsuarioAprobacion;
	}

	public void setCidUsuarioAprobacion(String cidUsuarioAprobacion) {
		this.cidUsuarioAprobacion = cidUsuarioAprobacion;
	}

	public String getCidOrigen() {
		return this.cidOrigen;
	}

	public void setCidOrigen(String cidOrigen) {
		this.cidOrigen = cidOrigen;
	}

	public Double getMtotalCargos() {
		return this.mtotalCargos;
	}

	public void setMtotalCargos(Double mtotalCargos) {
		this.mtotalCargos = mtotalCargos;
	}

	public Double getMtotalAbonos() {
		return this.mtotalAbonos;
	}

	public void setMtotalAbonos(Double mtotalAbonos) {
		this.mtotalAbonos = mtotalAbonos;
	}

	public String getCtipoDocumento() {
		return this.ctipoDocumento;
	}

	public void setCtipoDocumento(String ctipoDocumento) {
		this.ctipoDocumento = ctipoDocumento;
	}

	public String getCcomentarios() {
		return this.ccomentarios;
	}

	public void setCcomentarios(String ccomentarios) {
		this.ccomentarios = ccomentarios;
	}

	public Integer getNcambio() {
		return this.ncambio;
	}

	public void setNcambio(Integer ncambio) {
		this.ncambio = ncambio;
	}

	public BigDecimal getNidCasoOrigen() {
		return this.nidCasoOrigen;
	}

	public void setNidCasoOrigen(BigDecimal nidCasoOrigen) {
		this.nidCasoOrigen = nidCasoOrigen;
	}

	public String getPeriodo13() {
		return this.periodo13;
	}

	public void setPeriodo13(String periodo13) {
		this.periodo13 = periodo13;
	}

	public String getAdefas() {
		return this.adefas;
	}

	public void setAdefas(String adefas) {
		this.adefas = adefas;
	}

	public Integer getNtipoAjuste() {
		return this.ntipoAjuste;
	}

	public void setNtipoAjuste(Integer ntipoAjuste) {
		this.ntipoAjuste = ntipoAjuste;
	}

	public String getAejercicioFiscal() {
		return aejercicioFiscal;
	}

	public void setAejercicioFiscal(String aejercicioFiscal) {
		this.aejercicioFiscal = aejercicioFiscal;
	}

	public String getCcentroContable() {
		return ccentroContable;
	}

	public void setCcentroContable(String ccentroContable) {
		this.ccentroContable = ccentroContable;
	}

	public Integer getNfolioDocPoliza() {
		return nfolioDocPoliza;
	}

	public void setNfolioDocPoliza(Integer nfolioDocPoliza) {
		this.nfolioDocPoliza = nfolioDocPoliza;
	}

	public String getCtipoPoliza() {
		return ctipoPoliza;
	}

	public void setCtipoPoliza(String ctipoPoliza) {
		this.ctipoPoliza = ctipoPoliza;
	}

	public Integer getnFormatoPoliza() {
		return nFormatoPoliza;
	}

	public void setnFormatoPoliza(Integer nFormatoPoliza) {
		this.nFormatoPoliza = nFormatoPoliza;
	}

}