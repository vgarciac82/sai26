package com.axtel.egresos.compromiso;


import java.sql.Connection;
import java.util.Date;

import com.axtel.contratos.core.ConvenioColaboracion;
import com.syc.contable.core.CompromisoManager;
import com.syc.gestion.util.Util;
import com.syc.obrapublica.EjercicioFiscalManager;


public class CompromisoEncabezado {

	private int		aEjercicioFiscal;
	private String	caNoCompromiso;
	private String	cCentroContable;
	private String	cDescripcionPoliza;
	private String	cDocumentoHaplicado;
	private String	cIdContrato;
	private String	cRadicado	= "N";
	private String	cRevisado;
	private String	cTipoContrato;
	private String	cTipoPoliza;
	private String	cUnidadResponsable;
	private String	cUnidadResponsableContable;
	private Date	fAplicacion;
	private Date	fCancelacion;
	private Date	fCarga;
	private int		nEnviadoSICOP;
	private int		nFolioCompromiso;
	private int		nFolioPoliza;
	private int		nFolioPolizaCancelacion;
	private int		nMes;
	private String	usuario;
	private String	esCalendario;

	private Ramo ramo;// = new Ramo();
	
	/**
	 * @return the aEjercicioFiscal
	 */
	public int getaEjercicioFiscal() {
		return aEjercicioFiscal;
	}

	/**
	 * @return the caNoCompromiso
	 */
	public String getCaNoCompromiso() {
		return caNoCompromiso;
	}

	/**
	 * @return the cCentroContable
	 */
	public String getcCentroContable() {
		return cCentroContable;
	}

	/**
	 * @return the cDescripcionPoliza
	 */
	public String getcDescripcionPoliza() {
		return cDescripcionPoliza;
	}

	/**
	 * @return the cDocumentoHaplicado
	 */
	public String getcDocumentoHaplicado() {
		return cDocumentoHaplicado;
	}

	/**
	 * @return the cIdContrato
	 */
	public String getcIdContrato() {
		return cIdContrato;
	}

	/**
	 * @return the cRadicado
	 */
	public String getcRadicado() {
		return cRadicado;
	}


	/**
	 * @return the cRevisado
	 */
	public String getcRevisado() {
		return cRevisado;
	}

	/**
	 * @return the cTipoContrato
	 */
	public String getcTipoContrato() {
		return cTipoContrato;
	}

	/**
	 * @return the cTipoPoliza
	 */
	public String getcTipoPoliza() {
		return cTipoPoliza;
	}

	/**
	 * @return the cUnidadResponsable
	 */
	public String getcUnidadResponsable() {
		return cUnidadResponsable;
	}

	/**
	 * @return the cUnidadResponsableContable
	 */
	public String getcUnidadResponsableContable() {
		return cUnidadResponsableContable;
	}

	/**
	 * @return the fAplicacion
	 */
	public Date getfAplicacion() {
		return fAplicacion;
	}

	/**
	 * @return the fCancelacion
	 */
	public Date getfCancelacion() {
		return fCancelacion;
	}

	/**
	 * @return the fCarga
	 */
	public Date getfCarga() {
		return fCarga;
	}

	/**
	 * @return the nEnviadoSICOP
	 */
	public int getnEnviadoSICOP() {
		return nEnviadoSICOP;
	}

	/**
	 * @return the nFolioCompromiso
	 */
	public int getnFolioCompromiso() {
		return nFolioCompromiso;
	}

	/**
	 * @return the nFolioPoliza
	 */
	public int getnFolioPoliza() {
		return nFolioPoliza;
	}

	/**
	 * @return the nFolioPolizaCancelacion
	 */
	public int getnFolioPolizaCancelacion() {
		return nFolioPolizaCancelacion;
	}

	/**
	 * @return the nMes
	 */
	public int getnMes() {
		return nMes;
	}

	/**
	 * @return the usuario
	 */
	public String getUsuario() {
		return usuario;
	}

	/**
	 * @param aEjercicioFiscal
	 *            the aEjercicioFiscal to set
	 */
	public void setaEjercicioFiscal( int aEjercicioFiscal ) {
		this.aEjercicioFiscal = aEjercicioFiscal;
	}

	/**
	 * @param caNoCompromiso
	 *            the caNoCompromiso to set
	 */
	public void setCaNoCompromiso( String caNoCompromiso ) {
		this.caNoCompromiso = caNoCompromiso;
	}

	/**
	 * @param cCentroContable
	 *            the cCentroContable to set
	 */
	public void setcCentroContable( String cCentroContable ) {
		this.cCentroContable = cCentroContable;
	}

	/**
	 * @param cDescripcionPoliza
	 *            the cDescripcionPoliza to set
	 */
	public void setcDescripcionPoliza( String cDescripcionPoliza ) {
		this.cDescripcionPoliza = cDescripcionPoliza;
	}

	/**
	 * @param cDocumentoHaplicado
	 *            the cDocumentoHaplicado to set
	 */
	public void setcDocumentoHaplicado( String cDocumentoHaplicado ) {
		this.cDocumentoHaplicado = cDocumentoHaplicado;
	}

	/**
	 * @param cIdContrato
	 *            the cIdContrato to set
	 */
	public void setcIdContrato( String cIdContrato ) {
		this.cIdContrato = cIdContrato;
	}

	/**
	 * @param cRadicado
	 *            the cRadicado to set
	 */
	public void setcRadicado( String cRadicado ) {
		this.cRadicado = cRadicado;
	}

	/**
	 * @param cRevisado
	 *            the cRevisado to set
	 */
	public void setcRevisado( String cRevisado ) {
		this.cRevisado = cRevisado;
	}

	/**
	 * @param cTipoContrato
	 *            the cTipoContrato to set
	 */
	public void setcTipoContrato( String cTipoContrato ) {
		this.cTipoContrato = cTipoContrato;
	}

	/**
	 * @param cTipoPoliza
	 *            the cTipoPoliza to set
	 */
	public void setcTipoPoliza( String cTipoPoliza ) {
		this.cTipoPoliza = cTipoPoliza;
	}

	/**
	 * @param cUnidadResponsable
	 *            the cUnidadResponsable to set
	 */
	public void setcUnidadResponsable( String cUnidadResponsable ) {
		this.cUnidadResponsable = cUnidadResponsable;
	}

	/**
	 * @param cUnidadResponsableContable
	 *            the cUnidadResponsableContable to set
	 */
	public void setcUnidadResponsableContable( String cUnidadResponsableContable ) {
		this.cUnidadResponsableContable = cUnidadResponsableContable;
	}

	/**
	 * @param fAplicacion
	 *            the fAplicacion to set
	 */
	public void setfAplicacion( Date fAplicacion ) {
		this.fAplicacion = fAplicacion;
	}

	/**
	 * @param fCancelacion
	 *            the fCancelacion to set
	 */
	public void setfCancelacion( Date fCancelacion ) {
		this.fCancelacion = fCancelacion;
	}

	/**
	 * @param fCarga
	 *            the fCarga to set
	 */
	public void setfCarga( Date fCarga ) {
		this.fCarga = fCarga;
	}

	/**
	 * @param nEnviadoSICOP
	 *            the nEnviadoSICOP to set
	 */
	public void setnEnviadoSICOP( int nEnviadoSICOP ) {
		this.nEnviadoSICOP = nEnviadoSICOP;
	}

	/**
	 * @param nFolioCompromiso
	 *            the nFolioCompromiso to set
	 */
	public void setnFolioCompromiso( int nFolioCompromiso ) {
		this.nFolioCompromiso = nFolioCompromiso;
	}

	/**
	 * @param nFolioPoliza
	 *            the nFolioPoliza to set
	 */
	public void setnFolioPoliza( int nFolioPoliza ) {
		this.nFolioPoliza = nFolioPoliza;
	}

	/**
	 * @param nFolioPolizaCancelacion
	 *            the nFolioPolizaCancelacion to set
	 */
	public void setnFolioPolizaCancelacion( int nFolioPolizaCancelacion ) {
		this.nFolioPolizaCancelacion = nFolioPolizaCancelacion;
	}

	/**
	 * @param nMes
	 *            the nMes to set
	 */
	public void setnMes( int nMes ) {
		this.nMes = nMes;
	}

	/**
	 * @param usuario
	 *            the usuario to set
	 */
	public void setUsuario( String usuario ) {
		this.usuario = usuario;
	}

	@Override
	public String toString() {
		return "CompromisoEncabezado [aEjercicioFiscal=" + aEjercicioFiscal + ", caNoCompromiso=" + caNoCompromiso + ", cCentroContable=" + cCentroContable + ", cDescripcionPoliza=" + cDescripcionPoliza + ", cDocumentoHaplicado=" + cDocumentoHaplicado + ", cIdContrato=" + cIdContrato + ", cRadicado=" + cRadicado + ", cRamo=" + ramo + ", cRevisado=" + cRevisado + ", cTipoContrato=" + cTipoContrato + ", cTipoPoliza=" + cTipoPoliza + ", cUnidadResponsable=" + cUnidadResponsable + ", cUnidadResponsableContable=" + cUnidadResponsableContable + ", fAplicacion=" + fAplicacion + ", fCancelacion=" + fCancelacion + ", fCarga=" + fCarga + ", nEnviadoSICOP=" + nEnviadoSICOP + ", nFolioCompromiso=" + nFolioCompromiso + ", nFolioPoliza=" + nFolioPoliza + ", nFolioPolizaCancelacion=" + nFolioPolizaCancelacion + ", nMes=" + nMes + ", usuario=" + usuario + "]";
	}

	public static CompromisoEncabezado instanceFrom( Connection conn, ConvenioColaboracion convenio ) throws NumberFormatException, Exception {

		CompromisoEncabezado encabezado = new CompromisoEncabezado();
		encabezado.setaEjercicioFiscal( Integer.parseInt( EjercicioFiscalManager.getEjercicioFiscalActivo( conn ).getaEjercicioFiscal() ) );
		encabezado.setCaNoCompromiso( CompromisoManager.generateCaNoCompromiso( convenio.getEncabezado().getCentroContable(), String.valueOf( encabezado.getaEjercicioFiscal() ) ) );
		encabezado.setcCentroContable( convenio.getEncabezado().getCentroContable() );
		encabezado.setcDescripcionPoliza( convenio.getEncabezado().getConceptoConvenio() );
		encabezado.setcIdContrato( convenio.getEncabezado().getIdContrato() );
		encabezado.setcTipoContrato( "DI" );
		encabezado.setcTipoPoliza( "CO" );
		encabezado.setcUnidadResponsable( convenio.getEncabezado().getUnidadEjecutora() );
		encabezado.setcUnidadResponsableContable( "RHQ" );
		encabezado.setfAplicacion( new Date() );
		encabezado.setfCarga( new Date() );
		encabezado.setnEnviadoSICOP( 0 );
		encabezado.setnMes( Util.getCurrentMonth(conn) );
		encabezado.setUsuario( convenio.getEncabezado().getLoginCaptura() );
		encabezado.setEsCalendario( "N" );
		return encabezado;
	}

	public String getEsCalendario() {
		return esCalendario;
	}

	public void setEsCalendario( String esCalendario ) {
		this.esCalendario = esCalendario;
	}

}
