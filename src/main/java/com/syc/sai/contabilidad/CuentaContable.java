package com.syc.sai.contabilidad;

public class CuentaContable {
	/**
	 * El el centro contable considerado LOCAL, los restantes centros contables
	 * se consideran foraneos.
	 */
	public static String CENTRO_CONTABLE_LOCAL = "10";
	public static String ABONO = "A";
	public static String CARGO = "C";
	
	private String AplicacionCuenta;
	private String cBloqueaAbonos;
	private String cBloqueaCargos;
	private int cNivelBloqueo;
	private String cSubcuenta;
	private String dCuenta;
	private String NaturalezaCuenta;
	private String nCuenta;
	private String nCuentaLike;
	private String nCuentaPadre;
	private int NivelCuenta;
	private int nNivelBalanza;
	private int nOrdenBalanza;
	private String TipoBalance;
	private String tipoCuenta;
	private String VerificaSaldo;
	private boolean visible=true;
	private boolean tieneMovimientos=false;
	private boolean tieneHijos=false;
	

	public String getAplicacionCuenta() {
		return AplicacionCuenta;
	}

	public String getcBloqueaAbonos() {
		return cBloqueaAbonos;
	}

	public String getcBloqueaCargos() {
		return cBloqueaCargos;
	}

	public int getcNivelBloqueo() {
		return cNivelBloqueo;
	}

	public String getcSubcuenta() {
		return cSubcuenta;
	}

	public String getdCuenta() {
		return dCuenta;
	}

	public String getNaturalezaCuenta() {
		return NaturalezaCuenta;
	}

	public String getnCuenta() {
		return nCuenta;
	}

	public String getnCuentaLike() {
		return nCuentaLike;
	}

	public String getnCuentaPadre() {
		return nCuentaPadre;
	}

	public int getNivelCuenta() {
		return NivelCuenta;
	}

	public int getnNivelBalanza() {
		return nNivelBalanza;
	}

	public int getnOrdenBalanza() {
		return nOrdenBalanza;
	}

	public String getTipoBalance() {
		return TipoBalance;
	}

	public String getTipoCuenta() {
		return tipoCuenta;
	}

	public String getVerificaSaldo() {
		return VerificaSaldo;
	}

	public void setAplicacionCuenta(String aplicacionCuenta) {
		AplicacionCuenta = aplicacionCuenta;
	}

	public void setcBloqueaAbonos(String cBloqueaAbonos) {
		this.cBloqueaAbonos = cBloqueaAbonos;
	}

	public void setcBloqueaCargos(String cBloqueaCargos) {
		this.cBloqueaCargos = cBloqueaCargos;
	}

	public void setcNivelBloqueo(int cNivelBloqueo) {
		this.cNivelBloqueo = cNivelBloqueo;
	}

	public void setcSubcuenta(String cSubcuenta) {
		this.cSubcuenta = cSubcuenta;
	}

	public void setdCuenta(String dCuenta) {
		this.dCuenta = dCuenta;
	}

	public void setNaturalezaCuenta(String naturalezaCuenta) {
		NaturalezaCuenta = naturalezaCuenta;
	}

	public void setnCuenta(String nCuenta) {
		this.nCuenta = nCuenta;
	}

	public void setnCuentaLike(String nCuentaLike) {
		this.nCuentaLike = nCuentaLike;
	}

	public void setnCuentaPadre(String nCuentaPadre) {
		this.nCuentaPadre = nCuentaPadre;
	}

	public void setNivelCuenta(int nivelCuenta) {
		NivelCuenta = nivelCuenta;
	}

	public void setnNivelBalanza(int nNivelBalanza) {
		this.nNivelBalanza = nNivelBalanza;
	}

	public void setnOrdenBalanza(int nOrdenBalanza) {
		this.nOrdenBalanza = nOrdenBalanza;
	}

	public void setTipoBalance(String tipoBalance) {
		TipoBalance = tipoBalance;
	}

	public void setTipoCuenta(String tipoCuenta) {
		this.tipoCuenta = tipoCuenta;
	}

	public void setVerificaSaldo(String verificaSaldo) {
		VerificaSaldo = verificaSaldo;
	}
	public  void setVisibility(boolean visible)
	{
		this.visible=visible;
	}
	public  boolean getVisibility()
	{
		return visible;
	}
	public  void settieneMovimientos(boolean tieneMovimientos)
	{
		this.tieneMovimientos=tieneMovimientos;
	}
	public  boolean gettieneMovimientos()
	{
		return tieneMovimientos;
	}
	public  void setcontieneHijos(boolean tieneHijos)
	{
		this.tieneHijos=tieneHijos;
	}
	public  boolean getcontieneHijos()
	{
		return tieneHijos;
	}
	

}
