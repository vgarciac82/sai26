package com.axtel.sisecop.dto;


import java.io.Serializable;
import java.math.BigDecimal;

import com.axtel.sisecop.entities.ProyectoMes;


public class ProjectPaymentDTO implements Serializable {

	private static final long	serialVersionUID	= 1L;

	private int					idService;
	private ProyectoMes			mesPago;
	private int					servicioPagoAnio;
	private BigDecimal			servicioPagoCantidad;
	private int					servicioPagoId;

	public int getIdService() {
		return idService;
	}

	public ProyectoMes getMesPago() {
		return mesPago;
	}

	public int getServicioPagoAnio() {
		return servicioPagoAnio;
	}

	public BigDecimal getServicioPagoCantidad() {
		return servicioPagoCantidad;
	}

	public int getServicioPagoId() {
		return servicioPagoId;
	}

	public void setIdService( int idService ) {
		this.idService = idService;
	}

	public void setMesPago( ProyectoMes mesPago ) {
		this.mesPago = mesPago;
	}

	public void setServicioPagoAnio( int servicioPagoAnio ) {
		this.servicioPagoAnio = servicioPagoAnio;
	}

	public void setServicioPagoCantidad( BigDecimal servicioPagoCantidad ) {
		this.servicioPagoCantidad = servicioPagoCantidad;
	}

	public void setServicioPagoId( int servicioPagoId ) {
		this.servicioPagoId = servicioPagoId;
	}

	@Override
	public String toString() {
		return "ProjectPaymentDTO [servicioPagoId=" + servicioPagoId + ", idService=" + idService + ", servicioPagoAnio=" + servicioPagoAnio + ", servicioPagoCantidad=" + servicioPagoCantidad + ", mesPago=" + mesPago + "]";
	}
}
