package com.axtel.egresos.compromiso;


import java.sql.Connection;
import java.time.LocalDate;

import com.axtel.contratos.core.ConvenioColaboracion;
import com.syc.obrapublica.EjercicioFiscalManager;


public class PrecompromisoFinancieroEncabezado {

	private String		centroContable;
	private String		descripcionPoliza;
	private boolean		documentoAaplicado;
	private int			ejercicioFiscal;
	private LocalDate	fCancelacion;
	private LocalDate	fechaAplicacion;
	private LocalDate	fechaCarga;
	private int			folioPoliza;
	private int			folioPolizaCancelacion;
	private int			folioPrecomFinanciero;
	private String		login;
	private boolean		radicado	= false;
	private String		ramo;
	private String		tipoPoliza;
	private String		unidadResponsable;
	private String		unidadResponsableContable;

	/**
	 * @return the centroContable
	 */
	public String getCentroContable() {
		return centroContable;
	}

	/**
	 * @return the descripcionPoliza
	 */
	public String getDescripcionPoliza() {
		return descripcionPoliza;
	}

	/**
	 * @return the ejercicioFiscal
	 */
	public int getEjercicioFiscal() {
		return ejercicioFiscal;
	}

	/**
	 * @return the fCancelacion
	 */
	public LocalDate getfCancelacion() {
		return fCancelacion;
	}

	/**
	 * @return the fechaAplicacion
	 */
	public LocalDate getFechaAplicacion() {
		return fechaAplicacion;
	}

	/**
	 * @return the fechaCarga
	 */
	public LocalDate getFechaCarga() {
		return fechaCarga;
	}

	/**
	 * @return the folioPoliza
	 */
	public int getFolioPoliza() {
		return folioPoliza;
	}

	/**
	 * @return the folioPolizaCancelacion
	 */
	public int getFolioPolizaCancelacion() {
		return folioPolizaCancelacion;
	}

	/**
	 * @return the folioPrecomFinanciero
	 */
	public int getFolioPrecomFinanciero() {
		return folioPrecomFinanciero;
	}

	/**
	 * @return the login
	 */
	public String getLogin() {
		return login;
	}

	/**
	 * @return the ramo
	 */
	public String getRamo() {
		return ramo;
	}

	/**
	 * @return the tipoPoliza
	 */
	public String getTipoPoliza() {
		return tipoPoliza;
	}

	/**
	 * @return the unidadResponsable
	 */
	public String getUnidadResponsable() {
		return unidadResponsable;
	}

	/**
	 * @return the unidadResponsableContable
	 */
	public String getUnidadResponsableContable() {
		return unidadResponsableContable;
	}

	/**
	 * @return the documentoAaplicado
	 */
	public boolean isDocumentoAaplicado() {
		return documentoAaplicado;
	}

	/**
	 * @return the radicado
	 */
	public boolean isRadicado() {
		return radicado;
	}

	/**
	 * @param centroContable
	 *            the centroContable to set
	 */
	public void setCentroContable( String centroContable ) {
		this.centroContable = centroContable;
	}

	/**
	 * @param descripcionPoliza
	 *            the descripcionPoliza to set
	 */
	public void setDescripcionPoliza( String descripcionPoliza ) {
		this.descripcionPoliza = descripcionPoliza;
	}

	/**
	 * @param documentoAaplicado
	 *            the documentoAaplicado to set
	 */
	public void setDocumentoAaplicado( boolean documentoAaplicado ) {
		this.documentoAaplicado = documentoAaplicado;
	}

	/**
	 * @param ejercicioFiscal
	 *            the ejercicioFiscal to set
	 */
	public void setEjercicioFiscal( int ejercicioFiscal ) {
		this.ejercicioFiscal = ejercicioFiscal;
	}

	/**
	 * @param fCancelacion
	 *            the fCancelacion to set
	 */
	public void setfCancelacion( LocalDate fCancelacion ) {
		this.fCancelacion = fCancelacion;
	}

	/**
	 * @param fechaAplicacion
	 *            the fechaAplicacion to set
	 */
	public void setFechaAplicacion( LocalDate fechaAplicacion ) {
		this.fechaAplicacion = fechaAplicacion;
	}

	/**
	 * @param fechaCarga
	 *            the fechaCarga to set
	 */
	public void setFechaCarga( LocalDate fechaCarga ) {
		this.fechaCarga = fechaCarga;
	}

	/**
	 * @param folioPoliza
	 *            the folioPoliza to set
	 */
	public void setFolioPoliza( int folioPoliza ) {
		this.folioPoliza = folioPoliza;
	}

	/**
	 * @param folioPolizaCancelacion
	 *            the folioPolizaCancelacion to set
	 */
	public void setFolioPolizaCancelacion( int folioPolizaCancelacion ) {
		this.folioPolizaCancelacion = folioPolizaCancelacion;
	}

	/**
	 * @param folioPrecomFinanciero
	 *            the folioPrecomFinanciero to set
	 */
	public void setFolioPrecomFinanciero( int folioPrecomFinanciero ) {
		this.folioPrecomFinanciero = folioPrecomFinanciero;
	}

	/**
	 * @param login
	 *            the login to set
	 */
	public void setLogin( String login ) {
		this.login = login;
	}

	/**
	 * @param radicado
	 *            the radicado to set
	 */
	public void setRadicado( boolean radicado ) {
		this.radicado = radicado;
	}

	/**
	 * @param ramo
	 *            the ramo to set
	 */
	public void setRamo( String ramo ) {
		this.ramo = ramo;
	}

	/**
	 * @param tipoPoliza
	 *            the tipoPoliza to set
	 */
	public void setTipoPoliza( String tipoPoliza ) {
		this.tipoPoliza = tipoPoliza;
	}

	/**
	 * @param unidadResponsable
	 *            the unidadResponsable to set
	 */
	public void setUnidadResponsable( String unidadResponsable ) {
		this.unidadResponsable = unidadResponsable;
	}

	/**
	 * @param unidadResponsableContable
	 *            the unidadResponsableContable to set
	 */
	public void setUnidadResponsableContable( String unidadResponsableContable ) {
		this.unidadResponsableContable = unidadResponsableContable;
	}

	@Override
	public String toString() {
		return "PrecompromisoFinancieroEncabezado [folioPrecomFinanciero=" + folioPrecomFinanciero + ", fechaCarga=" + fechaCarga + ", fechaAplicacion=" + fechaAplicacion + ", centroContable=" + centroContable + ", ramo=" + ramo + ", unidadResponsable=" + unidadResponsable + ", documentoAaplicado=" + documentoAaplicado + ", folioPoliza=" + folioPoliza + ", tipoPoliza=" + tipoPoliza + ", ejercicioFiscal=" + ejercicioFiscal + ", unidadResponsableContable=" + unidadResponsableContable + ", folioPolizaCancelacion=" + folioPolizaCancelacion + ", fCancelacion=" + fCancelacion + ", descripcionPoliza=" + descripcionPoliza + ", login=" + login + ", radicado=" + radicado + "]";
	}

	public static PrecompromisoFinancieroEncabezado instanceFrom( Connection conn, ConvenioColaboracion convenio, int folioCompromiso ) throws NumberFormatException, Exception {
		PrecompromisoFinancieroEncabezado encabezado = new PrecompromisoFinancieroEncabezado();
		encabezado.setCentroContable( convenio.getEncabezado().getCentroContable() );
		encabezado.setDescripcionPoliza("Precompromiso del convenio con folio: " + convenio.getEncabezado().getFolioConvenioColaboracion() );
		encabezado.setEjercicioFiscal( Integer.parseInt( EjercicioFiscalManager.getEjercicioFiscalActivo( conn ).getaEjercicioFiscal() ) );
		encabezado.setFechaAplicacion(LocalDate.now());
		encabezado.setFechaCarga(LocalDate.now());
		encabezado.setFolioPrecomFinanciero(folioCompromiso);
		encabezado.setLogin(convenio.getEncabezado().getLoginCaptura());
		encabezado.setRadicado(false);
		encabezado.setRamo("16");
		encabezado.setTipoPoliza("PR");
		encabezado.setUnidadResponsable(convenio.getEncabezado().getIdUnidadAdministrativa());
		encabezado.setUnidadResponsableContable( "RHQ" );
		return encabezado;
	}
	

}
