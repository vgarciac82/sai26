package com.syc.adquisiciones.core;


import java.util.Date;


/**
 * Recepcion de material para autorizacion por base de datos.
 * 
 * @author vicente.garcia
 *
 */
public class DatosRecepcionFIEL extends DatosRecepcion {

	/**
	 * Numero de contrato en COMPRANET
	 */
	private String	contratoCNET;
	/**
	 * Fecha de la atenta nota.
	 */
	private Date	fechaNota	= new Date();
	/**
	 * Folio de la atenta nota.
	 */
	private String	folioNota;
	/**
	 * ID del estatus actual de la RM
	 */
	private int		idEstatusRM;

	/**
	 * ID de atenta nota.
	 */
	private int		idNota;
	
	private int requiereAtentaNota;
	/**
	 * Usuario que captura la atenta nota de la RM
	 */
	private String	idUsuarioCaptura;

	/**
	 * Motivo de autorizacion. Texto que se integrara a la atenta nota.
	 */
	private String	motivoAutorizacion;

	/**
	 * Numero del empleado que firmara la solicitud.
	 */
	private int		numeroEmpleado;

	private int		nIdEntraAlmacen;
	private int		nIdEstatusAtentaNotaFirmada;
	/**
	 * Numero del empleado que da el visto bueno de la atenta nota virtual
	 */
	private int		numeroEmpleadoVoBo;
	
	public DatosRecepcionFIEL( ) {

	}

	public String getContratoCNET() {
		return this.contratoCNET;
	}

	/**
	 * @return the fechaNota
	 */
	public Date getFechaNota() {
		return fechaNota;
	}

	/**
	 * @return the folioNota
	 */
	public String getFolioNota() {
		return folioNota;
	}

	public int getIdEstatusRM() {
		return idEstatusRM;
	}

	/**
	 * @return the idNota
	 */
	public int getIdNota() {
		return idNota;
	}

	public String getIdUsuarioCaptura() {
		return idUsuarioCaptura;
	}

	/**
	 * @return the motivoAutorizacion
	 */
	public String getMotivoAutorizacion() {
		return motivoAutorizacion;
	}

	/**
	 * @return the numeroEmpleado
	 */
	public int getNumeroEmpleado() {
		return numeroEmpleado;
	}

	public void setContratoCNET( String contratoCNET ) {
		this.contratoCNET = contratoCNET;
	}

	/**
	 * @param fechaNota
	 *            the fechaNota to set
	 */
	public void setFechaNota( Date fechaNota ) {
		this.fechaNota = fechaNota;
	}

	/**
	 * @param folioNota
	 *            the folioNota to set
	 */
	public void setFolioNota( String folioNota ) {
		this.folioNota = folioNota;
	}

	public void setIdEstatusRM( int idEstatusRM ) {
		this.idEstatusRM = idEstatusRM;
	}

	/**
	 * @param idNota
	 *            the idNota to set
	 */
	public void setIdNota( int idNota ) {
		this.idNota = idNota;
	}

	public void setIdUsuarioCaptura( String idUsuarioCaptura ) {
		this.idUsuarioCaptura = idUsuarioCaptura;
	}

	/**
	 * @param motivoAutorizacion
	 *            the motivoAutorizacion to set
	 */
	public void setMotivoAutorizacion( String motivoAutorizacion ) {
		this.motivoAutorizacion = motivoAutorizacion;
	}

	/**
	 * @param numeroEmpleado
	 *            the numeroEmpleado to set
	 */
	public void setNumeroEmpleado( int numeroEmpleado ) {
		this.numeroEmpleado = numeroEmpleado;
	}
	
	public int getnIdEntraAlmacen() {
		return nIdEntraAlmacen;
	}
	
	public void setnIdEntraAlmacen( int nIdEntraAlmacen ) {
		this.nIdEntraAlmacen = nIdEntraAlmacen;
	}
	
	public int getnIdEstatusAtentaNotaFirmada() {
		return nIdEstatusAtentaNotaFirmada;
	}
	
	public void setnIdEstatusAtentaNotaFirmada( int nIdEstatusAtentaNotaFirmada ) {
		this.nIdEstatusAtentaNotaFirmada = nIdEstatusAtentaNotaFirmada;
	}
	
	public int getNumeroEmpleadoVoBo() {
		return numeroEmpleadoVoBo;
	}
	
	public void setNumeroEmpleadoVoBo( int numeroEmpleadoVoBo ) {
		this.numeroEmpleadoVoBo = numeroEmpleadoVoBo;
	}
	
	public int getRequiereAtentaNota() {
		return requiereAtentaNota;
	}

	public void setRequiereAtentaNota( int requiereAtentaNota ) {
		this.requiereAtentaNota = requiereAtentaNota;
	}

	@Override
	public String toString() {
		return "DatosRecepcionFIEL [contratoCNET=" + contratoCNET + ", fechaNota=" + fechaNota + ", folioNota=" + folioNota + ", idEstatusRM=" + idEstatusRM + ", idNota=" + idNota 
				+ ", idUsuarioCaptura=" + idUsuarioCaptura + ", motivoAutorizacion=" + motivoAutorizacion + ", numeroEmpleado=" + numeroEmpleado + ", nIdEntraAlmacen=" + nIdEntraAlmacen 
				+ ", nIdEstatusAtentaNotaFirmada=" + nIdEstatusAtentaNotaFirmada + ", numeroEmpleadoVoBo=" + numeroEmpleadoVoBo + "]";
	}

}
