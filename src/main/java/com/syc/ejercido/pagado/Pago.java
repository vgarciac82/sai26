package com.syc.ejercido.pagado;

import java.math.BigDecimal;

public class Pago {
	String		estatusEjercido;
	String		estatusPagado;
	int			folioPagado;
	BigDecimal	importeNeto;
	String		integracion;
	String		tipoPago;

	public String getEstatusEjercido() {
		return estatusEjercido;
	}

	public String getEstatusPagado() {
		return estatusPagado;
	}

	public int getFolioPagado() {
		return folioPagado;
	}

	public BigDecimal getImporteNeto() {
		return importeNeto;
	}

	public String getIntegracion() {
		return integracion;
	}

	public String getTipoPago() {
		return tipoPago;
	}

	public void setEstatusEjercido(String estatusEjercido) {
		this.estatusEjercido = estatusEjercido;
	}

	public void setEstatusPagado(String estatusPagado) {
		this.estatusPagado = estatusPagado;
	}

	public void setFolioPagado(int folioPagado) {
		this.folioPagado = folioPagado;
	}

	public void setImporteNeto(BigDecimal importeNeto) {
		this.importeNeto = importeNeto;
	}

	public void setIntegracion(String integracion) {
		this.integracion = integracion;
	}

	public void setTipoPago(String tipoPago) {
		this.tipoPago = tipoPago;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Pago [estatusEjercido=" + estatusEjercido + ", estatusPagado=" + estatusPagado + ", folioPagado=" + folioPagado + ", importeNeto=" + importeNeto + ", integracion=" + integracion + ", tipoPago=" + tipoPago + "]";
	}

}