package com.syc.adquisiciones.core;

import java.util.ArrayList;

public class DatosContratoAmp {
	String cIdUERequi;
	int nEstatus;
	int nConsecutivoAmpliacion;
	String cUsuarioCreacion;
	String cUsuarioCancela;
	int nIVA;
	double mMontoConIVA;
	int nCantidad;
	ArrayList<DatosContratoAmpDetalle>detalleAmpliaciones;
	public String getcIdUERequi() {
		return cIdUERequi;
	}
	public void setcIdUERequi(String cIdUERequi) {
		this.cIdUERequi = cIdUERequi;
	}
	public int getnEstatus() {
		return nEstatus;
	}
	public void setnEstatus(int nEstatus) {
		this.nEstatus = nEstatus;
	}
	public int getnConsecutivoAmpliacion() {
		return nConsecutivoAmpliacion;
	}
	public void setnConsecutivoAmpliacion(int nConsecutivoAmpliacion) {
		this.nConsecutivoAmpliacion = nConsecutivoAmpliacion;
	}
	public String getcUsuarioCreacion() {
		return cUsuarioCreacion;
	}
	public void setcUsuarioCreacion(String cUsuarioCreacion) {
		this.cUsuarioCreacion = cUsuarioCreacion;
	}
	public String getcUsuarioCancela() {
		return cUsuarioCancela;
	}
	public void setcUsuarioCancela(String cUsuarioCancela) {
		this.cUsuarioCancela = cUsuarioCancela;
	}
	public int getnIVA() {
		return nIVA;
	}
	public void setnIVA(int nIVA) {
		this.nIVA = nIVA;
	}
	public double getmMontoConIVA() {
		return mMontoConIVA;
	}
	public void setmMontoConIVA(double mMontoConIVA) {
		this.mMontoConIVA = mMontoConIVA;
	}
	public int getnCantidad() {
		return nCantidad;
	}
	public void setnCantidad(int nCantidad) {
		this.nCantidad = nCantidad;
	}
	public ArrayList<DatosContratoAmpDetalle> getDetalleAmpliaciones() {
		return detalleAmpliaciones;
	}
	public void setDetalleAmpliaciones(ArrayList<DatosContratoAmpDetalle> detalleAmpliaciones) {
		this.detalleAmpliaciones = detalleAmpliaciones;
	}
	
}
