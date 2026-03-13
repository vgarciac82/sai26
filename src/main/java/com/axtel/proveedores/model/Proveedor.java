package com.axtel.proveedores.model;


import java.io.Serializable;
import java.util.Objects;


public class Proveedor implements Serializable {

	private static final long	serialVersionUID	= -2610938912999472490L;

	public static final int		REGIMEN_FISCAL_RESICO	= 626;


	private String				rfc;

	private String				calle;

	private String				codigoPostal;

	private String				colonia;

	private String				curp;

	private String				correo;

	private String				giro;

	private String				cIdEntidadFederativa;

	private String				unidadEjecutora;

	private String				municipio;

	private String				numeroExterno;

	private String				numeroInterno;

	private String				numeroRegistro;

	private String				numeroREPSE;

	private String				razonSocial;

	private String				representanteLegal;

	private String				url;

	private int					habilitado;

	private int					nIdPyme;

	private String				esResico;

	private int					idRegimenFiscal;
	
	private int					nPersonaFisica;
	
	private int					nPersonaMoral;

	public String getRfc() {
		return this.rfc;
	}

	public void setRfc( String rfc ) {
		this.rfc = rfc;
	}

	public String getCalle() {
		return this.calle;
	}

	public void setCalle( String calle ) {
		this.calle = calle;
	}

	public String getCodigoPostal() {
		return this.codigoPostal;
	}

	public void setCodigoPostal( String codigoPostal ) {
		this.codigoPostal = codigoPostal;
	}

	public String getColonia() {
		return this.colonia;
	}

	public void setColonia( String colonia ) {
		this.colonia = colonia;
	}

	public String getCurp() {
		return this.curp;
	}

	public void setCurp( String curp ) {
		this.curp = curp;
	}

	public String getCorreo() {
		return this.correo;
	}

	public void setCorreo( String correo ) {
		this.correo = correo;
	}

	public String getGiro() {
		return this.giro;
	}

	public void setGiro( String giro ) {
		this.giro = giro;
	}

	public String getCIdEntidadFederativa() {
		return this.cIdEntidadFederativa;
	}

	public void setCIdEntidadFederativa( String cIdEntidadFederativa ) {
		this.cIdEntidadFederativa = cIdEntidadFederativa;
	}

	public String getUnidadEjecutora() {
		return this.unidadEjecutora;
	}

	public void setUnidadEjecutora( String unidadEjecutora ) {
		this.unidadEjecutora = unidadEjecutora;
	}

	public String getMunicipio() {
		return this.municipio;
	}

	public void setMunicipio( String municipio ) {
		this.municipio = municipio;
	}

	public String getNumeroExterno() {
		return this.numeroExterno;
	}

	public void setNumeroExterno( String numeroExterno ) {
		this.numeroExterno = numeroExterno;
	}

	public String getNumeroInterno() {
		return this.numeroInterno;
	}

	public void setNumeroInterno( String numeroInterno ) {
		this.numeroInterno = numeroInterno;
	}

	public String getNumeroRegistro() {
		return this.numeroRegistro;
	}

	public void setNumeroRegistro( String numeroRegistro ) {
		this.numeroRegistro = numeroRegistro;
	}

	public String getNumeroREPSE() {
		return this.numeroREPSE;
	}

	public void setNumeroREPSE( String numeroREPSE ) {
		this.numeroREPSE = numeroREPSE;
	}

	public String getRazonSocial() {
		return this.razonSocial;
	}

	public void setRazonSocial( String razonSocial ) {
		this.razonSocial = razonSocial;
	}

	public String getRepresentanteLegal() {
		return this.representanteLegal;
	}

	public void setRepresentanteLegal( String representanteLegal ) {
		this.representanteLegal = representanteLegal;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl( String url ) {
		this.url = url;
	}

	public int getHabilitado() {
		return this.habilitado;
	}

	public void setHabilitado( int habilitado ) {
		this.habilitado = habilitado;
	}

	public int getNIdPyme() {
		return this.nIdPyme;
	}

	public void setNIdPyme( int nIdPyme ) {
		this.nIdPyme = nIdPyme;
	}


	public String getEsResico() {
		return esResico;
	}

	public void setEsResico( String esResico ) {
		this.esResico = esResico;
	}

	public int getIdRegimenFiscal() {
		return idRegimenFiscal;
	}

	public void setIdRegimenFiscal( int idRegimenFiscal ) {
		this.idRegimenFiscal = idRegimenFiscal;
	}
	
	public int getnPersonaFisica() {
		return nPersonaFisica;
	}
	
	public void setnPersonaFisica( int nPersonaFisica ) {
		this.nPersonaFisica = nPersonaFisica;
	}
	
	public int getnPersonaMoral() {
		return nPersonaMoral;
	}
	
	public void setnPersonaMoral( int nPersonaMoral ) {
		this.nPersonaMoral = nPersonaMoral;
	}

	@Override
	public String toString() {
		return "Proveedor [rfc=" + rfc + ", calle=" + calle + ", codigoPostal=" + codigoPostal + ", colonia=" + colonia + ", curp=" + curp + ", correo=" + correo + ", giro=" + giro + ", cIdEntidadFederativa=" + cIdEntidadFederativa + ", unidadEjecutora=" + unidadEjecutora + ", municipio=" + municipio + ", numeroExterno=" + numeroExterno + ", numeroInterno=" + numeroInterno + ", numeroRegistro=" + numeroRegistro + ", numeroREPSE=" + numeroREPSE + ", razonSocial=" + razonSocial + ", representanteLegal=" + representanteLegal + ", url=" + url + ", habilitado=" + habilitado + ", nIdPyme=" + nIdPyme + ", esResico=" + esResico + ", idRegimenFiscal=" + idRegimenFiscal+ ", nPersonaFisica=" + nPersonaFisica+ ", nPersonaMoral=" + nPersonaMoral + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash( cIdEntidadFederativa, calle, codigoPostal, colonia, correo, curp, esResico, giro, habilitado, idRegimenFiscal, municipio, nIdPyme, numeroExterno, numeroInterno, numeroREPSE, numeroRegistro, razonSocial, representanteLegal, rfc, unidadEjecutora, url, nPersonaFisica, nPersonaMoral );
	}

	@Override
	public boolean equals( Object obj ) {
		if ( this == obj )
			return true;
		if ( obj == null )
			return false;
		if ( getClass() != obj.getClass() )
			return false;
		Proveedor other = ( Proveedor ) obj;
		return Objects.equals( cIdEntidadFederativa, other.cIdEntidadFederativa ) && Objects.equals( calle, other.calle ) && Objects.equals( codigoPostal, other.codigoPostal ) && Objects.equals( colonia, other.colonia ) && Objects.equals( correo, other.correo ) && Objects.equals( curp, other.curp ) && Objects.equals( esResico, other.esResico ) && Objects.equals( giro, other.giro ) && habilitado == other.habilitado && idRegimenFiscal == other.idRegimenFiscal && Objects.equals( municipio, other.municipio ) && nIdPyme == other.nIdPyme && Objects.equals( numeroExterno, other.numeroExterno ) && Objects.equals( numeroInterno, other.numeroInterno ) && Objects.equals( numeroREPSE, other.numeroREPSE ) && Objects.equals( numeroRegistro, other.numeroRegistro ) && Objects.equals( razonSocial, other.razonSocial ) && Objects.equals( representanteLegal, other.representanteLegal ) && Objects.equals( rfc, other.rfc ) && Objects.equals( unidadEjecutora, other.unidadEjecutora ) && Objects.equals( url, other.url )&& Objects.equals( nPersonaFisica, other.nPersonaFisica )&& Objects.equals( nPersonaMoral, other.nPersonaMoral );
	}
	
	public static String esResico( int idRegimenFiscal ) {
		return REGIMEN_FISCAL_RESICO == idRegimenFiscal ? "S" : "N";
 
	}

}
