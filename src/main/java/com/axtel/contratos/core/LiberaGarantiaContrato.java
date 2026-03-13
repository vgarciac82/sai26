package com.axtel.contratos.core;

import java.io.InputStream;

/**
 * @author hfariasr
 *
 */
public class LiberaGarantiaContrato {
	private String cIdContratoDefinitivo;
	private String cMotivoLiberacion;
	private String cOficioSolicitud;
	private String fFechaSolicitud;
	private String cOficioLiberacion;
	private String fFechaLiberacion;
	private int lChequeEntregadoProveedor;
	private String cUsuarioCaptura;
	private String cUsuarioActualiza;
	private String cNombreArchivo;
	private String cExtencion;
	private InputStream archivoStream;
	private String cNombreArchivoDestino;
	
	public String getcIdContratoDefinitivo() {
		return cIdContratoDefinitivo;
	}
	
	public void setcIdContratoDefinitivo( String cIdContratoDefinitivo ) {
		this.cIdContratoDefinitivo = cIdContratoDefinitivo;
	}
	
	public String getcMotivoLiberacion() {
		return cMotivoLiberacion;
	}
	
	public void setcMotivoLiberacion( String cMotivoLiberacion ) {
		this.cMotivoLiberacion = cMotivoLiberacion;
	}
	
	public String getcOficioSolicitud() {
		return cOficioSolicitud;
	}
	
	public void setcOficioSolicitud( String cOficioSolicitud ) {
		this.cOficioSolicitud = cOficioSolicitud;
	}
	
	public String getfFechaSolicitud() {
		return fFechaSolicitud;
	}
	
	public void setfFechaSolicitud( String fFechaSolicitud ) {
		this.fFechaSolicitud = fFechaSolicitud;
	}
	
	public String getcOficioLiberacion() {
		return cOficioLiberacion;
	}
	
	public void setcOficioLiberacion( String cOficioLiberacion ) {
		this.cOficioLiberacion = cOficioLiberacion;
	}
	
	public String getfFechaLiberacion() {
		return fFechaLiberacion;
	}
	
	public void setfFechaLiberacion( String fFechaLiberacion ) {
		this.fFechaLiberacion = fFechaLiberacion;
	}
	
	public int getlChequeEntregadoProveedor() {
		return lChequeEntregadoProveedor;
	}
	
	public void setlChequeEntregadoProveedor( int lChequeEntregadoProveedor ) {
		this.lChequeEntregadoProveedor = lChequeEntregadoProveedor;
	}

	public String getcUsuarioCaptura() {
		return cUsuarioCaptura;
	}
	
	public void setcUsuarioCaptura( String cUsuarioCaptura ) {
		this.cUsuarioCaptura = cUsuarioCaptura;
	}
	
	public String getcUsuarioActualiza() {
		return cUsuarioActualiza;
	}
	
	public void setcUsuarioActualiza( String cUsuarioActualiza ) {
		this.cUsuarioActualiza = cUsuarioActualiza;
	}
	
	public String getcNombreArchivo() {
		return cNombreArchivo;
	}
	
	public void setcNombreArchivo( String cNombreArchivo ) {
		this.cNombreArchivo = cNombreArchivo;
	}
	
	public String getcExtencion() {
		return cExtencion;
	}
	
	public void setcExtencion( String cExtencion ) {
		this.cExtencion = cExtencion;
	}
	
	public InputStream getArchivoStream() {
		return archivoStream;
	}
	
	public void setArchivoStream( InputStream archivoStream ) {
		this.archivoStream = archivoStream;
	}
	
	public String getcNombreArchivoDestino() {
		return cNombreArchivoDestino;
	}
	
	public void setcNombreArchivoDestino( String cNombreArchivoDestino ) {
		this.cNombreArchivoDestino = cNombreArchivoDestino;
	}
	
}
