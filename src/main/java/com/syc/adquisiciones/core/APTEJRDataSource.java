package com.syc.adquisiciones.core;

import java.util.ArrayList;


import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

public class APTEJRDataSource implements JRDataSource {
	
	private Integer cont = 0;
	
	private ArrayList<APTEProveedor> proveedores = new ArrayList<APTEProveedor>();
	private ArrayList<APTEProveedorTotales> partidas = new ArrayList<APTEProveedorTotales>();
	
	public ArrayList<APTEProveedor> getProveedores() {
		return proveedores;
	}
	
	public void setProveedores(ArrayList<APTEProveedor> proveedores) {
		this.proveedores = proveedores;
	}
	
	public ArrayList<APTEProveedorTotales> getPartidas() {
		return partidas;
	}
	
	public void setPartidas(ArrayList<APTEProveedorTotales> partidas) {
		this.partidas = partidas;
	}
	
	public Object getFieldValue(JRField arg0) throws JRException {
		if (arg0.getName().equals("proveedores")){
			return this.proveedores;
		}
		else if (arg0.getName().equals("partidas")){
			return this.partidas;
		}
		return null;
	}

	public boolean next() throws JRException {
		
		if (this.cont < 1){
			this.cont++;
			return true;
		}
		return false;
	}
	
}