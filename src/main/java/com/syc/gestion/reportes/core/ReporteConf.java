package com.syc.gestion.reportes.core;

import java.io.Serializable;

public class ReporteConf implements Serializable{

	private final static long serialVersionUID = 1;

	private int id;
	private String nombre;
	private String plantilla_formulario;
	private String plantilla_resultado;
	private String descripcion;
	
	public int getId() {
	
		return id;
	}
	
	public void setId(int id) {
	
		this.id = id;
	}
	
	public String getNombre() {
	
		return nombre;
	}
	
	public void setNombre(String nombre) {
	
		this.nombre = nombre;
	}
	
	public String getPlantillaFormulario() {
	
		return plantilla_formulario;
	}
	
	public void setPlantillaFormulario(String plantilla_formulario) {
	
		this.plantilla_formulario = plantilla_formulario;
	}
	
	public String getPlantillaResultado() {
	
		return plantilla_resultado;
	}
	
	public void setPlantillaResultado(String plantilla_resultado) {
	
		this.plantilla_resultado = plantilla_resultado;
	}
	
	public String getDescripcion() {
	
		return descripcion;
	}
	
	public void setDescripcion(String descripcion) {
	
		this.descripcion = descripcion;
	}
	

	
}
