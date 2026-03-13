package com.syc.adquisiciones.core;

public class DatosContratoAmpDetalle {
	int nLineaConsolidado;
	int Cantidad;
	double mMontoConIVA;
	public int getnLineaConsolidado() {
		return nLineaConsolidado;
	}
	public void setnLineaConsolidado(int nLineaConsolidado) {
		this.nLineaConsolidado = nLineaConsolidado;
	}
	public int getCantidad() {
		return Cantidad;
	}
	public void setCantidad(int cantidad) {
		Cantidad = cantidad;
	}
	public double getmMontoConIVA() {
		return mMontoConIVA;
	}
	public void setmMontoConIVA(double mMontoConIVA) {
		this.mMontoConIVA = mMontoConIVA;
	}
}
