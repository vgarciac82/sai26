package com.syc.contable.anteproyecto;

public class PosicionArchivoSICOP {
	
	private String momento_sicop;
	private String cuentas_sai;
	private int col_inicio;
	private int posiciones;
	
	public PosicionArchivoSICOP(String momento_sicop, String cuentas_sai, int col_inicio, int posiciones) {
		super();
		this.momento_sicop = momento_sicop;
		this.cuentas_sai = cuentas_sai;
		this.col_inicio = col_inicio;
		this.posiciones = posiciones;
	}
	public String getmomento_sicop() {
		return momento_sicop;
	}
	public void setmomento_sicop(String momento_sicop) {
		this.momento_sicop = momento_sicop;
	}
	public String getcuentas_sai() {
		return cuentas_sai;
	}
	public void setcuentas_sai(String cuentas_sai) {
		this.cuentas_sai = cuentas_sai;
	}
	public int getcol_inicio() {
		return col_inicio;
	}
	public void setcol_inicio(int col_inicio) {
		this.col_inicio = col_inicio;
	}
	public int getPosiciones() {
		return posiciones;
	}
	public void setPosiciones(int posiciones) {
		this.posiciones = posiciones;
	}
	
	
}
