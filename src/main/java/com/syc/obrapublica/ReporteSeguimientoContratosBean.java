package com.syc.obrapublica;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReporteSeguimientoContratosBean {

	String				idArea;
	List<String[]>		info;
	Map<String, Double>	totales;
	String				UR;

	public String getIdArea() {
		return idArea;
	}

	public void setIdArea(String idArea) {
		this.idArea = idArea;
	}

	public List<String[]> getInfo() {
		return info;
	}

	public Map<String, Double> getTotales() {
		return totales;
	}

	public String getUR() {
		return UR;
	}

	public void setInfo(List<String[]> info) {
		this.info = info;
	}

	public void setInfo(String[] info) {

		if (this.info == null)
			setInfo(new ArrayList<String[]>());

		getInfo().add(info);

	}

	public void setTotales(Map<String, Double> totales) {
		this.totales = totales;
	}

	public void setUR(String uR) {
		UR = uR;
	}

}
