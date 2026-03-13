package com.syc.adquisiciones.core;


import java.math.BigDecimal;


public class RecepcionMaterial {

	private String		idRecepMat;
	private int			idConsecutivoRecepM;
	private String		ejercicio;
	private String		idPedContDef;
	private Integer		cantidad;
	private BigDecimal	montoConIVA;
	private BigDecimal	montoSinIVA;
	private BigDecimal	montoIVA;
	private long		idEstadoRecepMat;
	private String		unidadEjecutora;
	private String		idAlmacen;
	private Boolean		esServicio;
	private BigDecimal	montoOtrosImp;
	private boolean		factAmort;
	private String		observaciones;
	private Boolean		amortizaEjercAnt;
	private BigDecimal	descuentoConIVA;
	private BigDecimal	descuentoSinIVA;
	private BigDecimal	descuentoIVA;
	private int			idEntraAlmacen;
	
	public String getIdRecepMat() {
		return idRecepMat;
	}
	
	public void setIdRecepMat( String idRecepMat ) {
		this.idRecepMat = idRecepMat;
	}
	
	public int getIdConsecutivoRecepM() {
		return idConsecutivoRecepM;
	}
	
	public void setIdConsecutivoRecepM( int idConsecutivoRecepM ) {
		this.idConsecutivoRecepM = idConsecutivoRecepM;
	}
	
	public String getEjercicio() {
		return ejercicio;
	}
	
	public void setEjercicio( String ejercicio ) {
		this.ejercicio = ejercicio;
	}
	
	public String getIdPedContDef() {
		return idPedContDef;
	}
	
	public void setIdPedContDef( String idPedContDef ) {
		this.idPedContDef = idPedContDef;
	}
	
	public Integer getCantidad() {
		return cantidad;
	}
	
	public void setCantidad( Integer cantidad ) {
		this.cantidad = cantidad;
	}
	
	public BigDecimal getMontoConIVA() {
		return montoConIVA;
	}
	
	public void setMontoConIVA( BigDecimal montoConIVA ) {
		this.montoConIVA = montoConIVA;
	}
	
	public BigDecimal getMontoSinIVA() {
		return montoSinIVA;
	}
	
	public void setMontoSinIVA( BigDecimal montoSinIVA ) {
		this.montoSinIVA = montoSinIVA;
	}
	
	public BigDecimal getMontoIVA() {
		return montoIVA;
	}
	
	public void setMontoIVA( BigDecimal montoIVA ) {
		this.montoIVA = montoIVA;
	}
	
	public long getIdEstadoRecepMat() {
		return idEstadoRecepMat;
	}
	
	public void setIdEstadoRecepMat( long idEstadoRecepMat ) {
		this.idEstadoRecepMat = idEstadoRecepMat;
	}
	
	public String getUnidadEjecutora() {
		return unidadEjecutora;
	}
	
	public void setUnidadEjecutora( String unidadEjecutora ) {
		this.unidadEjecutora = unidadEjecutora;
	}
	
	public String getIdAlmacen() {
		return idAlmacen;
	}
	
	public void setIdAlmacen( String idAlmacen ) {
		this.idAlmacen = idAlmacen;
	}
	
	public Boolean getEsServicio() {
		return esServicio;
	}
	
	public void setEsServicio( Boolean esServicio ) {
		this.esServicio = esServicio;
	}
	
	public BigDecimal getMontoOtrosImp() {
		return montoOtrosImp;
	}
	
	public void setMontoOtrosImp( BigDecimal montoOtrosImp ) {
		this.montoOtrosImp = montoOtrosImp;
	}
	
	public boolean isFactAmort() {
		return factAmort;
	}
	
	public void setFactAmort( boolean factAmort ) {
		this.factAmort = factAmort;
	}
	
	public String getObservaciones() {
		return observaciones;
	}
	
	public void setObservaciones( String observaciones ) {
		this.observaciones = observaciones;
	}
	
	public Boolean getAmortizaEjercAnt() {
		return amortizaEjercAnt;
	}
	
	public void setAmortizaEjercAnt( Boolean amortizaEjercAnt ) {
		this.amortizaEjercAnt = amortizaEjercAnt;
	}
	
	public BigDecimal getDescuentoConIVA() {
		return descuentoConIVA;
	}
	
	public void setDescuentoConIVA( BigDecimal descuentoConIVA ) {
		this.descuentoConIVA = descuentoConIVA;
	}
	
	public BigDecimal getDescuentoSinIVA() {
		return descuentoSinIVA;
	}
	
	public void setDescuentoSinIVA( BigDecimal descuentoSinIVA ) {
		this.descuentoSinIVA = descuentoSinIVA;
	}
	
	public BigDecimal getDescuentoIVA() {
		return descuentoIVA;
	}
	
	public void setDescuentoIVA( BigDecimal descuentoIVA ) {
		this.descuentoIVA = descuentoIVA;
	}
	
	public int getIdEntraAlmacen() {
		return idEntraAlmacen;
	}
	
	public void setIdEntraAlmacen( int idEntraAlmacen ) {
		this.idEntraAlmacen = idEntraAlmacen;
	}
	
	
}
