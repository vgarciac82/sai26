package com.syc.gestion.core;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class TipoCaso  implements Serializable {

	private int id_tc;
	private String tc_descripcion;
	private String tc_gaveta_asociada;
	private int tc_tiempo_limite;
	private String tc_alarma;
	private String tc_who_can_init;
	private String tc_interface;

	private Map tipo_caso_variable;
	private List documentos;

	public TipoCaso() {

		tipo_caso_variable = new Hashtable();
	}

	public int getIdTC() {

		return id_tc;
	}

	public void setIdTC(int id_tc) {

		this.id_tc = id_tc;
	}

	public String getDescripcion() {

		return tc_descripcion;
	}

	public void setDescripcion(String tc_descripcion) {

		this.tc_descripcion = tc_descripcion;
	}

	public String getGavetaAsociada() {

		return tc_gaveta_asociada;
	}

	public void setGavetaAsociada(String tc_gaveta_asociada) {

		this.tc_gaveta_asociada = tc_gaveta_asociada;
	}

	public int getTiempoLimite() {

		return tc_tiempo_limite;
	}

	public void setTiempoLimite(int tc_tiempo_limite) {

		this.tc_tiempo_limite = tc_tiempo_limite;
	}

	public String getAlarma() {

		return tc_alarma;
	}

	public void setAlarma(String tc_alarma) {

		this.tc_alarma = tc_alarma;
	}

	public String getWhoCanInit() {

		return tc_who_can_init;
	}

	public void setWhoCanInit(String tc_who_can_init) {

		this.tc_who_can_init = tc_who_can_init;
	}

	public Map getCasoVariable() {

		return tipo_caso_variable;
	}

	public TipoCasoVariable getTipoCasoVariable(String name) {

		return (TipoCasoVariable) tipo_caso_variable.get(name);
	}

	public void setTipoCasoVariable(Map tipo_caso_variable) {

		this.tipo_caso_variable = tipo_caso_variable;
	}

	public void setTipoCasoVariable(String name, TipoCasoVariable tcv) {

		tipo_caso_variable.put(name, tcv);
	}

	public List getDocumentos() {

		return documentos;
	}

	public void setDocumentos(List documentos) {

		this.documentos = documentos;
	}

	public String getInterface() {

		return tc_interface;
	}

	public boolean tieneInterface() {

		return (tc_interface != null);
	}

	public void setInterface(String tc_interface) {

		this.tc_interface = tc_interface;
	}
}
