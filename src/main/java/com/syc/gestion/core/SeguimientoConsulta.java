package com.syc.gestion.core;

import java.io.Serializable;


public class SeguimientoConsulta implements Serializable{

	private final static long serialVersionUID = 1;

	private int id_tc = -1;
	private int b_id_oper = -1;
	private int b_id_caso = -1;
	private String b_c_folio_ini;
	private String b_c_folio_end;
	private String b_c_fecha_ini;
	private String b_c_fecha_end;
	private String b_co_fecha_ini;
	private String b_co_fecha_end;
	private String b_co_responsable_ejec;
	private String b_co_responsable_sigte;
	private String tc_descripcion;
	private String estado;
	private String referencia;
	private String asunto;
	
	
	public int getIdTC() {

		return id_tc;
	}

	public void setIdTC(int id_tc) {

		this.id_tc = id_tc;
	}

	public int getIdOper() {

		return b_id_oper;
	}

	public void setIdOper(int b_id_oper) {

		this.b_id_oper = b_id_oper;
	}

	public int getIdCaso() {

		return b_id_caso;
	}

	public void setIdCaso(int id_caso) {

		this.b_id_caso = id_caso;
	}

	public String getFolioIni() {

		return b_c_folio_ini;
	}

	public void setFolioIni(String b_c_folio) {

		this.b_c_folio_ini = b_c_folio;
	}

	public String getFolioEnd() {

		return b_c_folio_end;
	}

	public void setFolioEnd(String b_c_folio) {

		this.b_c_folio_end = b_c_folio;
	}

	public String getFechaCreacionIni() {

		return b_c_fecha_ini;
	}

	public void setFechaCreacionIni(String b_c_fecha_ini) {

		this.b_c_fecha_ini = b_c_fecha_ini;
	}

	public String getFechaCreacionEnd() {

		return b_c_fecha_end;
	}

	public void setFechaCreacionEnd(String b_c_fecha_ini) {

		this.b_c_fecha_end = b_c_fecha_ini;
	}

	public String getFechaEnvioIni() {

		return b_co_fecha_ini;
	}

	public void setFechaEnvioIni(String b_co_fecha_ini) {

		this.b_co_fecha_ini = b_co_fecha_ini;
	}

	public String getFechaEnvioEnd() {

		return b_co_fecha_end;
	}

	public void setFechaEnvioEnd(String b_co_fecha_ini) {

		this.b_co_fecha_end = b_co_fecha_ini;
	}

	public String getResponsableEjec() {

		return b_co_responsable_ejec;
	}

	public void setResponsableEjec(String b_co_responsable_ejec) {

		this.b_co_responsable_ejec = b_co_responsable_ejec;
	}

	public String getResponsableSigte() {

		return b_co_responsable_sigte;
	}


	public void setResponsableSigte(String b_co_responsable_sigte) {

		this.b_co_responsable_sigte = b_co_responsable_sigte;
	}

	public String getDescripcion() {

		return tc_descripcion;
	}

	public void setDescripcion(String tc_descripcion) {

		this.tc_descripcion = tc_descripcion;
	}

	public String getEstado() {

		return estado;
	}

	public void setEstado(String estado) {

		this.estado = estado;
	}
	
	public String getReferencia() {

		return referencia;
	}

	public void setReferencia(String referencia) {

		this.referencia = referencia;
	}

	public String getAsunto() {

		return asunto;
	}

	public void setAsunto(String asunto) {

		this.asunto = asunto;
	}	
	
	
}
