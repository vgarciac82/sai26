package com.axtel.egresos.core;


import java.time.LocalDateTime;


public class CargaMasivaRG {

	public static final int	CARGA_EXITOSA		= 1;
	public static final int	CARGA_APLICADA		= 2;
	public static final int	CARGA_ERROR			= 3;
	public static final int	CARGA_WAIT_VO_BO	= 4;
	public static final int	CARGA_WAIT_AUT		= 5;
	public static final int	CARGA_AUT			= 6;

	private int				folioCargaMasiva;
	private LocalDateTime	fechaCarga			= LocalDateTime.now();
	private int				estatusCarga;

	public CargaMasivaRG( int folioCargaMasiva, int estatusCarga ) {
		super();
		this.folioCargaMasiva = folioCargaMasiva;
		this.estatusCarga = estatusCarga;
	}

	public int getFolioCargaMasiva() {
		return folioCargaMasiva;
	}

	public void setFolioCargaMasiva( int folioCargaMasiva ) {
		this.folioCargaMasiva = folioCargaMasiva;
	}

	public LocalDateTime getFechaCarga() {
		return fechaCarga;
	}

	public void setFechaCarga( LocalDateTime fechaCarga ) {
		this.fechaCarga = fechaCarga;
	}

	public int getEstatusCarga() {
		return estatusCarga;
	}

	public void setEstatusCarga( int estatusCarga ) {
		this.estatusCarga = estatusCarga;
	}

	@Override
	public String toString() {
		return "CargaMasivaRG [folioCargaMasiva=" + folioCargaMasiva + ", fechaCarga=" + fechaCarga + ", estatusCarga=" + estatusCarga + "]";
	}

}
