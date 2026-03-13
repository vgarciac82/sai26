package com.syc.fortimax.core;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

public class Pagina {

	private String titulo_aplicacion;
	private int id_gabinete;
	private int id_carpeta_padre;
	private int id_documento;
	private int numero_pagina;
	private String volumen;
	private String tipo_volumen;
	private String nom_archivo_vol;
	private String nom_archivo_org;
	private String tipo_pagina; // A=Archivo (D), I=ImaxFile
	private String anotaciones;
	private String estado_pagina; // V=Vigente (D), T=Transitorio, H=Historico
	private double tamano_bytes;

	private String unidad_disco;
	private String ruta_base;
	private String ruta_directorio;
	//GAF 2009-02-20
	private Date fechaCreacion;
	private Date horaCreacion;
	//Campos de IMX_PAGINA_INDEX
	private Date fechaModificacion;
	private char procesado;

	public Pagina() {
		id_gabinete = -1;
		id_carpeta_padre = -1;
		id_documento = -1;
		numero_pagina = -1;
		tipo_pagina = new String("A");
		estado_pagina = new String("V");
		tamano_bytes = Double.parseDouble("0");
	}

	public String getTituloAplicacion() {
		return titulo_aplicacion;
	}

	public void setTituloAplicacion(String titulo_aplicacion) {
		this.titulo_aplicacion = titulo_aplicacion;
	}

	public int getIdGabinete() {
		return id_gabinete;
	}

	public void setIdGabinete(int id_gabinete) {
		this.id_gabinete = id_gabinete;
	}

	public int getIdCarpetaPadre() {
		return id_carpeta_padre;
	}

	public void setIdCarpetaPadre(int id_carpeta_padre) {
		this.id_carpeta_padre = id_carpeta_padre;
	}

	public int getIdDocumento() {
		return id_documento;
	}

	public void setIdDocumento(int id_documento) {
		this.id_documento = id_documento;
	}

	public int getNumeroPagina() {
		return numero_pagina;
	}

	public void setNumeroPagina(int numero_pagina) {
		this.numero_pagina = numero_pagina;
	}

	public String getVolumen() {
		return volumen;
	}

	public void setVolumen(String volumen) {
		this.volumen = volumen;
	}

	public String getTipoVolumen() {
		return tipo_volumen;
	}

	public void setTipoVolumen(String tipo_volumen) {
		this.tipo_volumen = tipo_volumen;
	}

	public String getNomArchivoVol() {
		return nom_archivo_vol;
	}

	public void setNomArchivoVol(String nom_archivo_vol) {
		this.nom_archivo_vol = nom_archivo_vol;
	}

	public String getNomArchivoOrg() {
		return nom_archivo_org;
	}

	public void setNomArchivoOrg(String nom_archivo_org) {
		this.nom_archivo_org = nom_archivo_org;
	}

	public String getTipoPagina() {
		return tipo_pagina;
	}

	public void setTipoPagina(String tipo_pagina) {
		this.tipo_pagina = tipo_pagina;
	}

	public String getAnotaciones() {
		return anotaciones;
	}

	public void setAnotaciones(String anotaciones) {
		this.anotaciones = anotaciones;
	}

	public String getEstadoPagina() {
		return estado_pagina;
	}

	public void setEstadoPagina(String estado_pagina) {
		this.estado_pagina = estado_pagina;
	}

	public double getTamanoBytes() {
		return tamano_bytes;
	}

	public void setTamanoBytes(double tamano_bytes) {
		this.tamano_bytes = tamano_bytes;
	}

	public String getUnidadDisco() {
		return unidad_disco;
	}

	public void setUnidadDisco(String unidad_disco) {
		this.unidad_disco = unidad_disco;
	}

	public String getRutaBase() {
		return ruta_base;
	}

	public void setRutaBase(String ruta_base) {
		this.ruta_base = ruta_base;
	}

	public String getRutaDirectorio() {
		return ruta_directorio;
	}

	public void setRutaDirectorio(String ruta_directorio) {
		this.ruta_directorio = ruta_directorio;
	}

	public String getPageExtension() {
		return nom_archivo_org.substring(nom_archivo_org.lastIndexOf("."));
	}
	public Date getFechaCreacion() {
		return fechaCreacion;
	}

	public void setFechaCreacion(Date fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	public Date getHoraCreacion() {
		return horaCreacion;
	}

	public void setHoraCreacion(Date horaCreacion) {
		this.horaCreacion = horaCreacion;
	}

	public Date getFechaModificacion() {
		return fechaModificacion;
	}

	public void setFechaModificacion(Date fechaModificacion) {
		this.fechaModificacion = fechaModificacion;
	}

	public char getProcesado() {
		return procesado;
	}

	public void setProcesado(char procesado) {
		this.procesado = procesado;
	}

	public String getFullPathFileName() {
		return    StringUtils.trimToEmpty( getUnidadDisco() ) 
				+ StringUtils.trimToEmpty( getRutaBase() ) 
				+ StringUtils.trimToEmpty( getRutaDirectorio() ) 
				+ StringUtils.trimToEmpty( getNomArchivoVol() );
	}

}
