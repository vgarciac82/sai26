package com.syc.admin.servlet;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
/**
 * Clase para Rportes en los que los datos para llenar los reportes se mandan al reporte
 * desde fuera esta clase rescibe una matriz de strings en donde la primer fila contiene el nombre de la columnas
 * y apartir de la segunda columna vienen los datos
 * @author Fernando Escamilla
 *
 */
public class DataSourceJasper implements JRDataSource{
	
	private String[][] data;
	private int ind;
/**
 * Trae el valor del campo
 */
public Object getFieldValue(JRField arg0) throws JRException {
	String[] nombres = this.data[0];
	//System.out.println("ind en get Field "+ind + " buscar "+arg0.getName() + " mobres " + nombres.length);
	if(data.length>ind){
		String[] datos = this.data[ind];
		for (int i = 0; i < nombres.length; i++) {
			//System.out.println("nombre "+ nombres[i]);
			if(arg0.getName().equals(nombres[i])){
				return datos[i];
			}
		}
	}
	return null;
	
}
/**
 * Dice si hay una fila siguiente
 */
public boolean next() throws JRException {
	if(data == null) return false;
	boolean next = ind<data.length;
	//System.out.println("hay siguiente? "+next);
	if(next){
		ind++;
	}
		return next;
	}

public DataSourceJasper() {
	ind=0;
	data = new String[0][0];
}
/**
 * Recibe los datos para llenar un reporte en form ade matriz de string en el cual
 * la primera fila debe llevar el nombre de las columnas del reporte
 * @param data
 */
public DataSourceJasper(String[][] data){
	this();
	this.data = data;
}
}
