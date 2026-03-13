package com.syc.contable.core;
import java.io.Serializable;

public class Balanza implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String NordenBalanza;
	private String Csangria;
	private String Ncuenta;
	private String Dcuenta;
	private String SaldoInicial;
	private String SaldoInicialAcreedor;
	private String SaldoInicialDeudor;
	private String MovimientosAcumuladosDebe;
	private String MovimientosAcumuladosHaber;
	private String SaldoMesAnterior;
	private String MovimientosMesDebe;
	private String MovimientosMesHaber;
	private String SaldoFinal;
	private String SaldoFinalAcreedor;
	private String SaldoFinalDeudor;
	private String AplicacionCuentar;

	public String getNordenBalanza() {
		return NordenBalanza;
	}
	public void setNordenBalanza(String nordenBalanza) {
		NordenBalanza = nordenBalanza;
	}
	public String getCsangria() {
		return Csangria;
	}
	public void setCsangria(String csangria) {
		Csangria = csangria;
	}
	public String getNcuenta() {
		return Ncuenta;
	}
	public void setNcuenta(String ncuenta) {
		Ncuenta = ncuenta;
	}
	public String getDcuenta() {
		return Dcuenta;
	}
	public void setDcuenta(String dcuenta) {
		Dcuenta = dcuenta;
	}
	public String getSaldoInicial() {
		return SaldoInicial;
	}
	public void setSaldoInicial(String saldoInicial) {
		SaldoInicial = saldoInicial;
	}
	public String getSaldoInicialAcreedor() {
		return SaldoInicialAcreedor;
	}
	public void setSaldoInicialAcreedor(String saldoInicialAcreedor) {
		SaldoInicialAcreedor = saldoInicialAcreedor;
	}
	public String getSaldoInicialDeudor() {
		return SaldoInicialDeudor;
	}
	public void setSaldoInicialDeudor(String saldoInicialDeudor) {
		SaldoInicialDeudor = saldoInicialDeudor;
	}
	public String getMovimientosAcumuladosDebe() {
		return MovimientosAcumuladosDebe;
	}
	public void setMovimientosAcumuladosDebe(String movimientosAcumuladosDebe) {
		MovimientosAcumuladosDebe = movimientosAcumuladosDebe;
	}
	public String getMovimientosAcumuladosHaber() {
		return MovimientosAcumuladosHaber;
	}
	public void setMovimientosAcumuladosHaber(String movimientosAcumuladosHaber) {
		MovimientosAcumuladosHaber = movimientosAcumuladosHaber;
	}
	public String getSaldoMesAnterior() {
		return SaldoMesAnterior;
	}
	public void setSaldoMesAnterior(String saldoMesAnterior) {
		SaldoMesAnterior = saldoMesAnterior;
	}
	public String getMovimientosMesDebe() {
		return MovimientosMesDebe;
	}
	public void setMovimientosMesDebe(String movimientosMesDebe) {
		MovimientosMesDebe = movimientosMesDebe;
	}
	public String getMovimientosMesHaber() {
		return MovimientosMesHaber;
	}
	public void setMovimientosMesHaber(String movimientosMesHaber) {
		MovimientosMesHaber = movimientosMesHaber;
	}
	public String getSaldoFinal() {
		return SaldoFinal;
	}
	public void setSaldoFinal(String saldoFinal) {
		SaldoFinal = saldoFinal;
	}
	public String getSaldoFinalAcreedor() {
		return SaldoFinalAcreedor;
	}
	public void setSaldoFinalAcreedor(String saldoFinalAcreedor) {
		SaldoFinalAcreedor = saldoFinalAcreedor;
	}
	public String getSaldoFinalDeudor() {
		return SaldoFinalDeudor;
	}
	public void setSaldoFinalDeudor(String saldoFinalDeudor) {
		SaldoFinalDeudor = saldoFinalDeudor;
	}
	public String getAplicacionCuentar() {
		return AplicacionCuentar;
	}
	public void setAplicacionCuentar(String aplicacionCuentar) {
		AplicacionCuentar = aplicacionCuentar;
	}


}
