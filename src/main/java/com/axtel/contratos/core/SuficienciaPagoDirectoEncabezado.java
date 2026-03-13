package com.axtel.contratos.core;


import java.math.BigDecimal;
import java.util.Date;


public class SuficienciaPagoDirectoEncabezado {

	public static final String	CONCTRACT_TYPE	= "PD";

	private int					folioSuficienciaPagoDirecto;
	private Date				fechaCarga		= new Date();
	private String				idContrato;
	private String				tipoContrato;
	private Date				fechaAplicacion;
	private Date				fechaCancelacion;
	private int					centroContable;
	private String				ramo;
	private String				unidadResponsable;
	private String				documentoAplicado;
	private String				tipoPoliza;
	private Integer				folioPoliza;
	private Integer				folioPolizaCancelacion;
	private int					idStatus;
	private int					ejercicioFiscal;
	private String				unidadResponsableContable;
	private String				rfc;
	private String				justificacion;
	private String				loginCaptura;
	private BigDecimal			importe;
	private BigDecimal			importeIVA;
	private BigDecimal			total;
	private int					porcentajeIVA;
	private String				razonSocial;

	public int getFolioSuficienciaPagoDirecto() {
		return folioSuficienciaPagoDirecto;
	}

	public void setFolioSuficienciaPagoDirecto( int folioSuficienciaPagoDirecto ) {
		this.folioSuficienciaPagoDirecto = folioSuficienciaPagoDirecto;
	}

	public Date getFechaCarga() {
		return fechaCarga;
	}

	public void setFechaCarga( Date fechaCarga ) {
		this.fechaCarga = fechaCarga;
	}

	public String getIdContrato() {
		return idContrato;
	}

	public void setIdContrato( String idContrato ) {
		this.idContrato = idContrato;
	}

	public String getTipoContrato() {
		return tipoContrato;
	}

	public void setTipoContrato( String tipoContrato ) {
		this.tipoContrato = tipoContrato;
	}

	public Date getFechaAplicacion() {
		return fechaAplicacion;
	}

	public void setFechaAplicacion( Date fechaAplicacion ) {
		this.fechaAplicacion = fechaAplicacion;
	}

	public Date getFechaCancelacion() {
		return fechaCancelacion;
	}

	public void setFechaCancelacion( Date fechaCancelacion ) {
		this.fechaCancelacion = fechaCancelacion;
	}

	public int getCentroContable() {
		return centroContable;
	}

	public void setCentroContable( int centroContable ) {
		this.centroContable = centroContable;
	}

	public String getRamo() {
		return ramo;
	}

	public void setRamo( String ramo ) {
		this.ramo = ramo;
	}

	public String getUnidadResponsable() {
		return unidadResponsable;
	}

	public void setUnidadResponsable( String unidadResponsable ) {
		this.unidadResponsable = unidadResponsable;
	}

	public String getDocumentoAplicado() {
		return documentoAplicado;
	}

	public void setDocumentoAplicado( String documentoAplicado ) {
		this.documentoAplicado = documentoAplicado;
	}

	public String getTipoPoliza() {
		return tipoPoliza;
	}

	public void setTipoPoliza( String tipoPoliza ) {
		this.tipoPoliza = tipoPoliza;
	}

	public Integer getFolioPoliza() {
		return folioPoliza;
	}

	public void setFolioPoliza( Integer folioPoliza ) {
		this.folioPoliza = folioPoliza;
	}

	public Integer getFolioPolizaCancelacion() {
		return folioPolizaCancelacion;
	}

	public void setFolioPolizaCancelacion( Integer folioPolizaCancelacion ) {
		this.folioPolizaCancelacion = folioPolizaCancelacion;
	}

	public int getIdStatus() {
		return idStatus;
	}

	public void setIdStatus( int idStatus ) {
		this.idStatus = idStatus;
	}

	public int getEjercicioFiscal() {
		return ejercicioFiscal;
	}

	public void setEjercicioFiscal( int ejercicioFiscal ) {
		this.ejercicioFiscal = ejercicioFiscal;
	}

	public String getUnidadResponsableContable() {
		return unidadResponsableContable;
	}

	public void setUnidadResponsableContable( String unidadResponsableContable ) {
		this.unidadResponsableContable = unidadResponsableContable;
	}

	public String getRfc() {
		return rfc;
	}

	public void setRfc( String rfc ) {
		this.rfc = rfc;
	}

	public String getJustificacion() {
		return justificacion;
	}

	public void setJustificacion( String justificacion ) {
		this.justificacion = justificacion;
	}

	public String getLoginCaptura() {
		return loginCaptura;
	}

	public void setLoginCaptura( String loginCaptura ) {
		this.loginCaptura = loginCaptura;
	}

	public BigDecimal getImporte() {
		return importe;
	}

	public void setImporte( BigDecimal importe ) {
		this.importe = importe;
	}

	public BigDecimal getImporteIVA() {
		return importeIVA;
	}

	public void setImporteIVA( BigDecimal importeIVA ) {
		this.importeIVA = importeIVA;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal( BigDecimal total ) {
		this.total = total;
	}

	public int getPorcentajeIVA() {
		return porcentajeIVA;
	}

	public void setPorcentajeIVA( int porcentajeIVA ) {
		this.porcentajeIVA = porcentajeIVA;
	}

	public void setRazonSocial( String razonSocial ) {
		this.razonSocial = razonSocial;
	}

	public String getRazonSocial() {
		return this.razonSocial;
	}
}
