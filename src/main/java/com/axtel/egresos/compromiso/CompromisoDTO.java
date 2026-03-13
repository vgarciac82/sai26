package com.axtel.egresos.compromiso;


public class CompromisoDTO {

	private String	cidproceso;
	private String	ccompromisosicop;
	private String	tipoMov;
	private String	ur;
	private String	ep;
	private String	cidcontrato;
	private String	cdescripcion;
	private String	origen;
	private double	saldo;
	private double	mimporte;
	private double	disponible;

	public String getCidproceso() {
		return cidproceso;
	}

	public void setCidproceso( String cidproceso ) {
		this.cidproceso = cidproceso;
	}

	public String getCcompromisosicop() {
		return ccompromisosicop;
	}

	public void setCcompromisosicop( String ccompromisosicop ) {
		this.ccompromisosicop = ccompromisosicop;
	}

	public String getTipoMov() {
		return tipoMov;
	}

	public void setTipoMov( String tipoMov ) {
		this.tipoMov = tipoMov;
	}

	public String getUr() {
		return ur;
	}

	public void setUr( String ur ) {
		this.ur = ur;
	}

	public String getEp() {
		return ep;
	}

	public void setEp( String ep ) {
		this.ep = ep;
	}

	public String getCidcontrato() {
		return cidcontrato;
	}

	public void setCidcontrato( String cidcontrato ) {
		this.cidcontrato = cidcontrato;
	}

	public String getCdescripcion() {
		return cdescripcion;
	}

	public void setCdescripcion( String cdescripcion ) {
		this.cdescripcion = cdescripcion;
	}

	public String getOrigen() {
		return origen;
	}

	public void setOrigen( String origen ) {
		this.origen = origen;
	}

	public double getSaldo() {
		return saldo;
	}

	public void setSaldo( double saldo ) {
		this.saldo = saldo;
	}

	public double getMimporte() {
		return mimporte;
	}

	public void setMimporte( double mimporte ) {
		this.mimporte = mimporte;
	}

	public double getDisponible() {
		return disponible;
	}

	public void setDisponible( double disponible ) {
		this.disponible = disponible;
	}

	@Override
	public String toString() {
		return "CompromisoDTO [cidproceso=" + cidproceso + ", ccompromisosicop=" + ccompromisosicop + ", tipoMov=" + tipoMov + ", ur=" + ur + ", ep=" + ep + ", cidcontrato=" + cidcontrato + ", cdescripcion=" + cdescripcion + ", origen=" + origen + ", saldo=" + saldo + ", mimporte=" + mimporte + ", disponible=" + disponible + "]";
	}

}
