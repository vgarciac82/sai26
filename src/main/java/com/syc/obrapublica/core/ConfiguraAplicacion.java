package com.syc.obrapublica.core;

import java.io.Serializable;

public class ConfiguraAplicacion  implements Serializable {
	
	private final static long serialVersionUID = 1;
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	private String cRamo;
	private String cUnidadResponsable;
	private String cTituloAplicacion;
	private String cActivaFecAPL;
	//private ConfiguraDocumentos objDoctosConf;
	public String getcRamo() {
		return cRamo;
	}
	public void setcRamo(String cRamo) {
		this.cRamo = cRamo;
	}
	public String getcUnidadResponsable() {
		return cUnidadResponsable;
	}
	public void setcUnidadResponsable(String cUnidadResponsable) {
		this.cUnidadResponsable = cUnidadResponsable;
	}
	public String getcTituloAplicacion() {
		return cTituloAplicacion;
	}
	public void setcTituloAplicacion(String cTituloAplicacion) {
		this.cTituloAplicacion = cTituloAplicacion;
	}
	public String getcActivaFecAPL() {
		return cActivaFecAPL;
	}
	public void setcActivaFecAPL(String cActivaFecAPL) {
		this.cActivaFecAPL = cActivaFecAPL;
	}
	/*public ConfiguraDocumentos getObjDoctosConf() {
		return objDoctosConf;
	}
	public void setObjDoctosConf(ConfiguraDocumentos objDoctosConf) {
		this.objDoctosConf = objDoctosConf;
	}*/
	
	
}
