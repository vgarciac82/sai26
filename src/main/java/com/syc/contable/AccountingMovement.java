package com.syc.contable;

public class AccountingMovement {

	private String cuenta;
	private String subCuenta;
	private double movimiento;
	private boolean cargo;
	private boolean naturaleza;
	private boolean verificaSaldo;
	private String centroContable;
	private String ejercicioFiscal;
	private int folioPoliza;
	private int docRenglon;
	private String descCuenta;
	private String parcial;
	private boolean bPresupuesto;
	private String unidadResponsable;
	
	public AccountingMovement(String cuenta, double movimiento, boolean cargo, boolean naturaleza, boolean varificaSaldo, String centroContable, String ejercicioFiscal,
	        int docRenglon, String descCuenta, boolean bPresupuesto, String unidadResponsable) {
		this(cuenta, null, movimiento, cargo, naturaleza, varificaSaldo, centroContable, ejercicioFiscal, docRenglon, descCuenta, bPresupuesto, unidadResponsable);
	}

	public AccountingMovement(String cuenta, String subCuenta, double movimiento, boolean cargo, boolean naturaleza, boolean verificaSaldo, String centroContable,
	        String ejercicioFiscal, int docRenglon, String descCuenta, boolean bPresupuesto, String unidadResponsable ) {
		this.cuenta = cuenta;
		this.subCuenta = subCuenta;
		this.movimiento = movimiento;
		this.cargo = cargo;
		this.naturaleza = naturaleza;
		this.verificaSaldo = verificaSaldo;
		this.centroContable = centroContable;
		this.ejercicioFiscal = ejercicioFiscal;
		this.docRenglon = docRenglon;
		this.descCuenta = descCuenta;
		this.bPresupuesto = bPresupuesto;
		this.unidadResponsable = unidadResponsable;
	}

	public String getCuenta() {
		return cuenta;
	}

	public void setCuenta(String cuenta) {
		this.cuenta = cuenta;
	}

	public String getSubCuenta() {
		return subCuenta;
	}

	public void setSubCuenta(String subCuenta) {
		this.subCuenta = subCuenta;
	}

	public double getMovimiento() {
		return movimiento;
	}

	public void setMovimiento(double movimiento) {
		this.movimiento = movimiento;
	}

	public boolean isCargo() {
		return cargo;
	}

	public String getTipoMovimiento() {
		return isCargo() ? "C" : "A";
	}

	public void setCargo(boolean esCargo) {
		this.cargo = esCargo;
	}

	public boolean esDeudora() {
		return naturaleza;
	}

	public String getNaturaleza() {
		return esDeudora() ? "D" : "A";
	}

	public void setNaturaleza(boolean naturaleza) {
		this.naturaleza = naturaleza;
	}

	public boolean seVarificaSaldo() {
		return verificaSaldo;
	}

	public String getVarificaSaldo() {
		return verificaSaldo ? "S" : "N";
	}

	public void setVerificaSaldo(boolean verificaSaldo) {
		this.verificaSaldo = verificaSaldo;
	}

	public String getCentroContable() {
		return centroContable;
	}

	public void setCentroContable(String centroContable) {
		this.centroContable = centroContable;
	}

	public void setEjercicioFiscal(String ejercicioFiscal) {
		this.ejercicioFiscal = ejercicioFiscal;
	}

	public String getEjercicioFiscal() {
		return ejercicioFiscal;
	}

	public void setFolioPoliza(int folioPoliza) {
		this.folioPoliza = folioPoliza;
	}

	public int getFolioPoliza() {
		return folioPoliza;
	}

	public boolean isVerificaSaldo() {
		return verificaSaldo;
	}

	public int getDocRenglon() {
		return docRenglon;
	}

	public String getDescCuenta() {
		return descCuenta;
	}

	public void setDescCuenta(String descCuenta) {
		this.descCuenta = descCuenta;
	}

	public String getPK() {
		return centroContable + cuenta + (subCuenta == null ? "" : subCuenta);
	}

	public boolean getbPresupuesto() {
		return bPresupuesto;
	}

	public void setbPresupuesto(boolean bPresupuesto) {
		this.bPresupuesto = bPresupuesto;
	}

	@Override
	public String toString() {
		return "[ejercicioFiscal = " + ejercicioFiscal + ", centroContable=" + centroContable + ", cuenta=" + cuenta + ", subCuenta=" + subCuenta + ", movimiento=" + movimiento
		        + ", tipoMovimiento=" + getTipoMovimiento() + "(" + cargo + ")" + ", naturaleza=" + getNaturaleza() + "(" + naturaleza + ")" + ", verificaSaldo="
		        + getVarificaSaldo() + "(" + verificaSaldo + ")" + ", folioPoliza=" + folioPoliza + ", docRenglon=" + docRenglon + ", descCuenta=" + descCuenta + ", Unidad Responsable=" + unidadResponsable + "]";
	}

	public String getParcial() {
		return parcial;
	}

	public void setParcial(String parcial) {
		this.parcial = parcial;
	}

	
	public String getUnidadResponsable() {
		return unidadResponsable;
	}

	
	public void setUnidadResponsable( String unidadResponsable ) {
		this.unidadResponsable = unidadResponsable;
	}

	
}