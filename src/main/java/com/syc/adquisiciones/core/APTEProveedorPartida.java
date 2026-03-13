package com.syc.adquisiciones.core;

public class APTEProveedorPartida {
	
	private Integer idLinea;
	private Double precio = 0.0;
	private Double tipoCambio = 1.0;
	private String tipoMoneda = "";
	private Integer cantidad = 0;
	
	public Integer getIdLinea() {
		return idLinea;
	}
	
	public void setIdLinea(Integer idLinea) {
		this.idLinea = idLinea;
	}
	
	public Double getPrecio() {
		return precio;
	}
	
	public void setPrecio(Double precio) {
		this.precio = precio;
	}
	
	public Double getTipoCambio() {
		return tipoCambio;
	}
	
	public void setTipoCambio(Double tipoCambio) {
		this.tipoCambio = tipoCambio;
	}
	
	public String getTipoMoneda() {
		return tipoMoneda;
	}
	
	public void setTipoMoneda(String tipoMoneda) {
		this.tipoMoneda = tipoMoneda;
	}
	
	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
	}
	
	public Integer getCantidad() {
		return cantidad;
	}
		
}
