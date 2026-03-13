package com.syc.sai.tesoreria.retenciones;

import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

import com.syc.sai.tesoreria.retenciones.core.Retencion;

public class Retenciones {

	@JsonProperty("data_1")
	private List<Retencion>	retenciones;
	@JsonProperty("success")
	private boolean			success;
	@JsonProperty("msg")
	private String			msg;

	public Retenciones(List<Retencion> retenciones, boolean success, String msg) {
		super();
		this.retenciones = retenciones;
		this.success = success;
		this.msg = msg;
	}

	public List<Retencion> getRetenciones() {
		return retenciones;
	}

	public void setRetenciones(List<Retencion> retenciones) {
		this.retenciones = retenciones;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

}
