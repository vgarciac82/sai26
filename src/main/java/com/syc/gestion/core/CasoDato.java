package com.syc.gestion.core;

import java.io.Serializable;

public class CasoDato implements Serializable {

	private static final long	serialVersionUID	= -2028253904822116169L;
	private String				cd_valor;
	private int					id_caso;
	private int					id_cd;
	private int					id_tc;

	private TipoCasoVariable	tcv;

	public int getIdCaso() {
		return id_caso;
	}

	public int getIdCD() {
		return id_cd;
	}

	public int getIdTC() {
		return id_tc;
	}

	public TipoCasoVariable getTipoCasoVariable() {
		return tcv;
	}

	public String getValor() {
		return cd_valor;
	}

	public void setIdCaso(int id_caso) {
		this.id_caso = id_caso;
	}

	public void setIdCD(int id_cd) {
		this.id_cd = id_cd;
	}

	public void setIdTC(int id_tc) {
		this.id_tc = id_tc;
	}

	public void setTipoCasoVariable(TipoCasoVariable tcv) {
		this.tcv = tcv;
	}

	public void setValor(String cd_valor) {
		this.cd_valor = cd_valor;
	}

	@Override
	public String toString() {
		return "CasoDato [cd_valor=" + cd_valor + ", id_cd=" + id_cd + "]";
	}
	
	
}
