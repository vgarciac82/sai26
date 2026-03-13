package com.axtel.egresos.viaticos;

import java.math.BigDecimal;

public class Viaticos {
	private int idAgenda;
	private BigDecimal cuotaPorDia;
	private String moneda;
	private BigDecimal tipoCambio;
	private int tieneHomologacion;
	private int nivelHomologar;
	private String plazaHomologar;
	private String justificacion;
	private int tienePaquete;
	private int idPaquete;
	
	public BigDecimal getCuotaPorDia() {
		return cuotaPorDia;
	}
	
	public void setCuotaPorDia( BigDecimal cuotaPorDia ) {
		this.cuotaPorDia = cuotaPorDia;
	}
	
	public String getPlazaHomologar() {
		return plazaHomologar;
	}
	
	public int getNivelHomologar() {
		return nivelHomologar;
	}
	
	public void setNivelHomologar( int nivelHomologar ) {
		this.nivelHomologar = nivelHomologar;
	}

	public void setPlazaHomologar( String plazaHomologar ) {
		this.plazaHomologar = plazaHomologar;
	}
	
	public String getJustificacion() {
		return justificacion;
	}
	
	public void setJustificacion( String justificacion ) {
		this.justificacion = justificacion;
	}
	
	public int getIdPaquete() {
		return idPaquete;
	}
	
	public void setIdPaquete( int idPaquete ) {
		this.idPaquete = idPaquete;
	}
	
	public String getMoneda() {
		return moneda;
	}

	
	public void setMoneda( String moneda ) {
		this.moneda = moneda;
	}

	
	public BigDecimal getTipoCambio() {
		return tipoCambio;
	}

	
	public void setTipoCambio( BigDecimal tipoCambio ) {
		this.tipoCambio = tipoCambio;
	}

	
	public int getidAgenda() {
		return idAgenda;
	}

	public void setidAgenda( int idAgenda ) {
		this.idAgenda = idAgenda;
	}
	
	public int getTieneHomologacion() {
		return tieneHomologacion;
	}

	public void setTieneHomologacion( int tieneHomologacion ) {
		this.tieneHomologacion = tieneHomologacion;
	}

	public int getTienePaquete() {
		return tienePaquete;
	}

	public void setTienePaquete( int tienePaquete ) {
		this.tienePaquete = tienePaquete;
	}


	@Override
	public String toString() {
		return "Viaticos [idAgenda=" + idAgenda + ", cuotaPorDia=" + cuotaPorDia + ", moneda=" + moneda + ", tipoCambio=" + tipoCambio + ", tieneHomologacion=" + tieneHomologacion + ", nivelHomologar=" + nivelHomologar + ", plazaHomologar=" + plazaHomologar + ", justificacion=" + justificacion + ", tienePaquete=" + tienePaquete + ", idPaquete=" + idPaquete + "]";
	}
	
	
}
