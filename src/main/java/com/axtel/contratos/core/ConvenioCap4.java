package com.axtel.contratos.core;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * @author hfariasr
 *
 */
public class ConvenioCap4 {
	private int nIdContModCap4;
	private String cIdContratoDefinitivo;
	private String cIdUnidadEjecutora;
	private String cEjercicio;
	private int nConsecutivoModificacion;
	private double mTotalAnterior;
	private double mTotalNuevo;
	private double mMontoConvenioSinIVA;
	private double mMontoConvenioConIVA;
	private double mMontoConvenioIVA;
	private int nEstatus;
	private String fCapturaMod;
	private int nTipoModificacion;
	private String cFolio;
	private int nConsecutivoCDIV;
	private String cFolioPre;
	private int nConsecutivoPrecom;
	private String cNoConvenio;
	private String cObjetoConvenio;
	private int lEsTotalPluri;
	private int lEsConvEjercicioAnt;
	private int nIdCategoria;
	private int nIdFundamentoLegal;
	private String cIdRFC;
	private String cNumProcedCNET;
	private String nCodExpedienteCNET;
	private String nCodContratoCNET;
	private String cUsuarioCaptura;
	private String fFormalizacion;
	private String fInicio;
	private String fFin;
	private String cEntidadContable;
	private ArrayList<PartidasConvenioCap4>partidas;
	private String cEP;
	private String jndiName;
	private String prefixPath;
	
	public int getnIdContModCap4() {
		return nIdContModCap4;
	}
	
	public void setnIdContModCap4( int nIdContModCap4 ) {
		this.nIdContModCap4 = nIdContModCap4;
	}
	
	public String getcIdContratoDefinitivo() {
		return cIdContratoDefinitivo;
	}
	
	public void setcIdContratoDefinitivo( String cIdContratoDefinitivo ) {
		this.cIdContratoDefinitivo = cIdContratoDefinitivo;
	}
	
	public String getcIdUnidadEjecutora() {
		return cIdUnidadEjecutora;
	}
	
	public void setcIdUnidadEjecutora( String cIdUnidadEjecutora ) {
		this.cIdUnidadEjecutora = cIdUnidadEjecutora;
	}
	
	public String getcEjercicio() {
		return cEjercicio;
	}
	
	public void setcEjercicio( String cEjercicio ) {
		this.cEjercicio = cEjercicio;
	}
	
	public int getnConsecutivoModificacion() {
		return nConsecutivoModificacion;
	}
	
	public void setnConsecutivoModificacion( int nConsecutivoModificacion ) {
		this.nConsecutivoModificacion = nConsecutivoModificacion;
	}
	
	public double getmTotalAnterior() {
		return mTotalAnterior;
	}
	
	public void setmTotalAnterior( double mTotalAnterior ) {
		this.mTotalAnterior = mTotalAnterior;
	}
	
	public double getmTotalNuevo() {
		return mTotalNuevo;
	}
	
	public void setmTotalNuevo( double mTotalNuevo ) {
		this.mTotalNuevo = mTotalNuevo;
	}
	
	public double getmMontoConvenioSinIVA() {
		return mMontoConvenioSinIVA;
	}
	
	public void setmMontoConvenioSinIVA( double mMontoConvenioSinIVA ) {
		this.mMontoConvenioSinIVA = mMontoConvenioSinIVA;
	}
	
	public double getmMontoConvenioConIVA() {
		return mMontoConvenioConIVA;
	}
	
	public void setmMontoConvenioConIVA( double mMontoConvenioConIVA ) {
		this.mMontoConvenioConIVA = mMontoConvenioConIVA;
	}
	
	public double getmMontoConvenioIVA() {
		return mMontoConvenioIVA;
	}
	
	public void setmMontoConvenioIVA( double mMontoConvenioIVA ) {
		this.mMontoConvenioIVA = mMontoConvenioIVA;
	}
	
	public int getnEstatus() {
		return nEstatus;
	}
	
	public void setnEstatus( int nEstatus ) {
		this.nEstatus = nEstatus;
	}
	
	public String getfCapturaMod() {
		return fCapturaMod;
	}
	
	public void setfCapturaMod( String fCapturaMod ) {
		this.fCapturaMod = fCapturaMod;
	}
	
	public int getnTipoModificacion() {
		return nTipoModificacion;
	}
	
	public void setnTipoModificacion( int nTipoModificacion ) {
		this.nTipoModificacion = nTipoModificacion;
	}
	
	public String getcFolio() {
		return cFolio;
	}
	
	public void setcFolio( String cFolio ) {
		this.cFolio = cFolio;
	}
	
	public int getnConsecutivoCDIV() {
		return nConsecutivoCDIV;
	}
	
	public void setnConsecutivoCDIV( int nConsecutivoCDIV ) {
		this.nConsecutivoCDIV = nConsecutivoCDIV;
	}
	
	public String getcFolioPre() {
		return cFolioPre;
	}
	
	public void setcFolioPre( String cFolioPre ) {
		this.cFolioPre = cFolioPre;
	}
	
	public int getnConsecutivoPrecom() {
		return nConsecutivoPrecom;
	}
	
	public void setnConsecutivoPrecom( int nConsecutivoPrecom ) {
		this.nConsecutivoPrecom = nConsecutivoPrecom;
	}
	
	public String getcNoConvenio() {
		return cNoConvenio;
	}
	
	public void setcNoConvenio( String cNoConvenio ) {
		this.cNoConvenio = cNoConvenio;
	}
	
	public String getcObjetoConvenio() {
		return cObjetoConvenio;
	}
	
	public void setcObjetoConvenio( String cObjetoConvenio ) {
		this.cObjetoConvenio = cObjetoConvenio;
	}
	
	public int getlEsTotalPluri() {
		return lEsTotalPluri;
	}
	
	public void setlEsTotalPluri( int lEsTotalPluri ) {
		this.lEsTotalPluri = lEsTotalPluri;
	}
	
	public int getlEsConvEjercicioAnt() {
		return lEsConvEjercicioAnt;
	}
	
	public void setlEsConvEjercicioAnt( int lEsConvEjercicioAnt ) {
		this.lEsConvEjercicioAnt = lEsConvEjercicioAnt;
	}
	
	public int getnIdCategoria() {
		return nIdCategoria;
	}
	
	public void setnIdCategoria( int nIdCategoria ) {
		this.nIdCategoria = nIdCategoria;
	}
	
	public int getnIdFundamentoLegal() {
		return nIdFundamentoLegal;
	}
	
	public void setnIdFundamentoLegal( int nIdFundamentoLegal ) {
		this.nIdFundamentoLegal = nIdFundamentoLegal;
	}
	
	public String getcIdRFC() {
		return cIdRFC;
	}
	
	public void setcIdRFC( String cIdRFC ) {
		this.cIdRFC = cIdRFC;
	}
	
	public String getcNumProcedCNET() {
		return cNumProcedCNET;
	}
	
	public void setcNumProcedCNET( String cNumProcedCNET ) {
		this.cNumProcedCNET = cNumProcedCNET;
	}	
	
	public String getnCodExpedienteCNET() {
		return nCodExpedienteCNET;
	}

	
	public void setnCodExpedienteCNET( String nCodExpedienteCNET ) {
		this.nCodExpedienteCNET = nCodExpedienteCNET;
	}

	
	public String getnCodContratoCNET() {
		return nCodContratoCNET;
	}

	
	public void setnCodContratoCNET( String nCodContratoCNET ) {
		this.nCodContratoCNET = nCodContratoCNET;
	}

	public String getcUsuarioCaptura() {
		return cUsuarioCaptura;
	}
	
	public void setcUsuarioCaptura( String cUsuarioCaptura ) {
		this.cUsuarioCaptura = cUsuarioCaptura;
	}
	
	public String getfFormalizacion() {
		return fFormalizacion;
	}
	
	public void setfFormalizacion( String fFormalizacion ) {
		this.fFormalizacion = fFormalizacion;
	}
	
	public String getfInicio() {
		return fInicio;
	}
	
	public void setfInicio( String fInicio ) {
		this.fInicio = fInicio;
	}
	
	public String getfFin() {
		return fFin;
	}
	
	public void setfFin( String fFin ) {
		this.fFin = fFin;
	}
	
	public String getcEntidadContable() {
		return cEntidadContable;
	}
	
	public void setcEntidadContable( String cEntidadContable ) {
		this.cEntidadContable = cEntidadContable;
	}
	
	public ArrayList<PartidasConvenioCap4> getPartidas() {
		return partidas;
	}
	
	public void setPartidas( ArrayList<PartidasConvenioCap4> partidas ) {
		this.partidas = partidas;
	}
	
	public String getcEP() {
		return cEP;
	}
	
	public void setcEP( String cEP ) {
		this.cEP = cEP;
	}
	
	public String getJndiName() {
		return jndiName;
	}
	
	public void setJndiName( String jndiName ) {
		this.jndiName = jndiName;
	}
	
	public String getPrefixPath() {
		return prefixPath;
	}
	
	public void setPrefixPath( String prefixPath ) {
		this.prefixPath = prefixPath;
	}
	
	
}
