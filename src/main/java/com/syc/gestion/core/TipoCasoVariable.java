package com.syc.gestion.core;

import java.io.Serializable;

import com.syc.gestion.util.Util;

public class TipoCasoVariable implements Serializable {

	private static final long serialVersionUID = -1992977288501634095L;

	private int id_tc;
	private int id_tcv;
	private String tcv_nombre;
	private String tcv_descripcion;
	private String tcv_etiqueta;
	private int tcv_tipo;
	private int tcv_longitud;
	private int tcv_indice;
	private String tcv_en_gaveta;

	public int getIdTC() {
		return id_tc;
	}

	public void setIdTC(int id_tc) {
		this.id_tc = id_tc;
	}

	public int getIdTCV() {
		return id_tcv;
	}

	public void setIdTCV(int id_tcv) {
		this.id_tcv = id_tcv;
	}

	public String getNombre() {
		return (tcv_nombre != null) ? tcv_nombre.toUpperCase() : tcv_nombre;
	}

	public String getNombreJS() {
		return Util.makeJSName(tcv_nombre);
	}

	public void setNombre(String tcv_nombre) {
		this.tcv_nombre = (tcv_nombre != null) ? tcv_nombre.toUpperCase() : tcv_nombre;
	}

	public String getDescripcion() {
		return tcv_descripcion;
	}

	public void setDescripcion(String tcv_descripcion) {
		this.tcv_descripcion = tcv_descripcion;
	}

	public String getEtiqueta() {
		return tcv_etiqueta;
	}

	public void setEtiqueta(String tcv_etiqueta) {
		this.tcv_etiqueta = tcv_etiqueta;
	}

	public int getTipo() {
		return tcv_tipo;
	}

	public void setTipo(int tcv_tipo) {
		this.tcv_tipo = tcv_tipo;
	}

	public int getLongitud() {
		return tcv_longitud;
	}

	public void setLongitud(int tcv_longitud) {
		this.tcv_longitud = tcv_longitud;
	}

	public int getIndice() {
		return tcv_indice;
	}

	public void setIndice(int tcv_indice) {
		this.tcv_indice = tcv_indice;
	}

	public String getEnGaveta() {
		return (tcv_en_gaveta != null) ? tcv_en_gaveta.toUpperCase() : tcv_en_gaveta;
	}

	public boolean isEnGaveta() {
		return "S".equals(getEnGaveta());
	}

	public void setEnGaveta(String tcv_en_gaveta) {
		this.tcv_en_gaveta = (tcv_en_gaveta != null) ? tcv_en_gaveta.toUpperCase() : tcv_en_gaveta;
	}
}
