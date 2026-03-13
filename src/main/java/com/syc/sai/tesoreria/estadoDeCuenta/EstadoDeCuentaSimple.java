package com.syc.sai.tesoreria.estadoDeCuenta;

public class EstadoDeCuentaSimple {

	private String	fechaEmision;
	private double	intereses;
	private String	numeroDeCuenta;
	private double	saldo;

	public EstadoDeCuentaSimple(String numeroDeCuenta, String fechaEmision, double saldo, double intereses) {
		super();
		this.numeroDeCuenta = numeroDeCuenta;
		this.fechaEmision = fechaEmision;
		this.saldo = saldo;
		this.intereses = intereses;
	}

	@Override
	public String toString() {
		return "EstadoDeCuentaSimple [fechaEmision=" + fechaEmision + ", intereses=" + intereses + ", numeroDeCuenta=" + numeroDeCuenta + ", saldo=" + saldo + "]";
	}

	public String getFechaEmision() {
		return fechaEmision;
	}

	public double getIntereses() {
		return intereses;
	}

	public String getNumeroDeCuenta() {
		return numeroDeCuenta;
	}

	public double getSaldo() {
		return saldo;
	}

	public void setFechaEmision(String fechaEmision) {
		this.fechaEmision = fechaEmision;
	}

	public void setIntereses(double intereses) {
		this.intereses = intereses;
	}

	public void setNumeroDeCuenta(String numeroDeCuenta) {
		this.numeroDeCuenta = numeroDeCuenta;
	}

	public void setSaldo(double saldo) {
		this.saldo = saldo;
	}
}
