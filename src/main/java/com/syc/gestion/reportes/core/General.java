package com.syc.gestion.reportes.core;

import java.io.Serializable;


public class General implements Serializable{
	private final static long serialVersionUID = 1;

	private String area;                  // remitente_area
	private String folio; 
	private String referencia; 
	private String remitente_nombre;
	private String remitente_id;          // REMITENTE_ID
	private String idRemitenteArea; 
	private String areaRemitente;         // area_rem, DESC_AREA_REM 
	private String fechaAtencion;
	private String cerrado;               // Estatus
	private String destinatario;          // Turnado A
	
	private String area_estructura;       // AREA_ESTRUCTURA
	private String fecha_registro;        // FECHAREGISTRO
	private String fecha_limite;          // FECHALIMITE
	private String responsable_area;      // RESPONSABLE_AREA
	private String responsable_area_desc; // DESC_AREA_RESP
	private String responsable_id;        // RESPONSABLE_ID
	private String responsable_nombre;    // NOMBRE_RESP
	private String tipo_asunto;           // TIPOASUNTO
	private String asunto;                // Descripcion del Asunto
	private String estatus;               // ESTATUS
	private String prioridad;             // PRIORIDAD
	private String user01;                // USER01
	private String tipo_instruccion;      // TIPOINSTRUCCION
	private String fecha_hoy;             // FECHA ACTUAL

	//Esteban Badillo: Fecha: 10/Sep/2009
	//Descripcion: Se agregan dos nuevos atributos a la clase para el manejo de la fecha
	//de envio de los documentos, como resultado de la modificacion al Reporte General
	//en el que se solicita la inclusion de dos nuevos campos.
	private String fecha_envio;           // FECHA DE ENVIO DEL DOCUMENTO.
	//private String fecha_limite_atencion;        // FECHA DE ATENCION DEL DOCUMENTO.
	
	/*
	REMITENTE_AREA
	DESC_AREA_REM
	AREA_ESTRUCTURA
	FOLIO
	REFERENCIA
	FECHAREGISTRO
	FECHALIMITE
	RESPONSABLE_AREA
	DESC_AREA_RESP
	RESPONSABLE_ID
	NOMBRE_RESP
	REMITENTE_ID
	REMITENTE
	TIPOASUNTO
	ESTATUS
	PRIORIDAD
	USER01
	TIPOINSTRUCCION
	fecha_hoy
	*/
	
	public String getArea() {
	
		return area;
	}
	
	
	
//=====================================
	public void setArea(String area) {
	
		this.area = area;
	}
	
	public String getFolio() {
	
		return folio;
	}
	
	public void setFolio(String folio) {
	
		this.folio = folio;
	}
	
	public String getReferencia() {
	
		return referencia;
	}
	
	public void setReferencia(String referencia) {
	
		this.referencia = referencia;
	}
	
	public String getNombreRemitente() {
	
		return remitente_nombre;
	}
	
	public void setNombreRemitente(String nombreRemitente) {
	
		this.remitente_nombre = nombreRemitente;
	}
	
	public String getIdRemitenteArea() {
	
		return idRemitenteArea;
	}
	
	public void setIdRemitenteArea(String idRemitenteArea) {
	
		this.idRemitenteArea = idRemitenteArea;
	}
	
	public String getAreaRemitente() {
	
		return areaRemitente;
	}
	
	public void setAreaRemitente(String areaRemitente) {
	
		this.areaRemitente = areaRemitente;
	}
	
	public String getFechaAtencion() {
	
		return fechaAtencion;
	}
	
	public void setFechaAtencion(String fechaAtencion) {
	
		this.fechaAtencion = fechaAtencion;
	}
	
	public String getCerrado() {
	
		return cerrado;
	}
	
	public void setCerrado(String cerrado) {
	
		this.cerrado = cerrado;
	}
	
	public String getDestinatario() {
	
		return destinatario;
	}
	
	public void setDestinatario(String destinatario) {
	
		this.destinatario = destinatario;
	}
	
	public String getRemitenteId() {
		
		return remitente_id;
	}

	
	public void setRemitenteId(String remitente_id) {
	
		this.remitente_id = remitente_id;
	}

	
	public String getAreaEstructura() {
	
		return area_estructura;
	}

	
	public void setAreaEstructura(String area_estructura) {
	
		this.area_estructura = area_estructura;
	}

	
	public String getFechaRegistro() {
	
		return fecha_registro;
	}

	
	public void setFechaRegistro(String fecha_registro) {
	
		this.fecha_registro = fecha_registro;
	}

	
	public String getFechaLimite() {
	
		return fecha_limite;
	}

	
	public void setFechaLimite(String fecha_limite) {
	
		this.fecha_limite = fecha_limite;
	}

	
	public String getResponsableArea() {
	
		return responsable_area;
	}

	
	public void setResponsableArea(String responsable_area) {
	
		this.responsable_area = responsable_area;
	}

	
	public String getResponsableAreaDesc() {
	
		return responsable_area_desc;
	}

	
	public void setResponsableAreaDesc(String responsable_area_desc) {
	
		this.responsable_area_desc = responsable_area_desc;
	}

	
	public String getResponsableId() {
	
		return responsable_id;
	}

	
	public void setResponsableId(String responsable_id) {
	
		this.responsable_id = responsable_id;
	}

	
	public String getResponsableNombre() {
	
		return responsable_nombre;
	}

	
	public void setResponsableNombre(String responsable_nombre) {
	
		this.responsable_nombre = responsable_nombre;
	}

	
	public String getTipoAsunto() {
	
		return tipo_asunto;
	}

	
	public void setTipoAsunto(String tipo_asunto) {
	
		this.asunto = tipo_asunto;
	}

	public String getAsunto() {
		
		return asunto;
	}

	
	public void setAsunto(String asunto) {
	
		this.asunto = asunto;
	}
	
	
	public String getEstatus() {
	
		return estatus;
	}

	
	public void setEstatus(String estatus) {
	
		this.estatus = estatus;
	}

	
	public String getUser01() {
	
		return user01;
	}

	
	public void setUser01(String user01) {
	
		this.user01 = user01;
	}

	
	public String getTipoInstruccion() {
	
		return tipo_instruccion;
	}

	
	public void setTipoInstruccion(String tipo_instruccion) {
	
		this.tipo_instruccion = tipo_instruccion;
	}

	
	public String getFechaActual() {
	
		return fecha_hoy;
	}

	
	public void setFechaActual(String fecha_hoy) {
	
		this.fecha_hoy = fecha_hoy;
	}
	
	
	public String getPrioridad() {
	
		return prioridad;
	}
	
	public void setPrioridad(String prioridad) {
	
		this.prioridad = prioridad;
	}
	
	/*
	 * Esteban Badillo. Fecha: 10/Sep/2009
	 * Descripcion:
	 *    Se agrega el metodo Get y Set para la propiedad fecha_envio para las modificaciones
	 *    solicitadas al Reporte General.
	 * */
	public void setFechaEnvio(String fecha_envio)
	{
		this.fecha_envio = fecha_envio;
	}
	
	public String getFechaEnvio()
	{
		return this.fecha_envio;
	}
}
