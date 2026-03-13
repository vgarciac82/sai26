package com.syc.adquisiciones.core;

public class APTEProveedorDoc {
	
	private String id = "";
	private String documento = "";
	private String referencia = "";
	private Boolean presento = false;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getDocumento() {
		return documento;
	}
	
	public void setDocumento(String documento) {
		this.documento = documento;
	}
	
	public String getReferencia() {
		return referencia;
	}
	
	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}
	
	public Boolean getPresento() {
		return presento;
	}
	
	public void setPresento(Boolean presento) {
		this.presento = presento;
	}
	
	public String getSi() {
		if (this.presento) return "X";
		return "";
	}
	
	public String getNo() {
		if (!this.presento) return "X";
		return "";
	}
	
}
