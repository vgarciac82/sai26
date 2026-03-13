package com.syc.adquisiciones.core;

public class Respuesta {
	private boolean resp;
	private String msg;
	private String msg2;
	private int code;
	private boolean newCtas;
	private boolean oldCtas;
	public boolean isResp() {
		return resp;
	}
	public void setResp(boolean resp) {
		this.resp = resp;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public boolean isNewCtas() {
		return newCtas;
	}
	public void setNewCtas(boolean newCtas) {
		this.newCtas = newCtas;
	}
	public boolean isOldCtas() {
		return oldCtas;
	}
	public void setOldCtas(boolean oldCtas) {
		this.oldCtas = oldCtas;
	}
	public String getMsg2() {
		return msg2;
	}
	public void setMsg2(String msg2) {
		this.msg2 = msg2;
	}
	
}
