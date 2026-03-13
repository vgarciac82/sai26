package com.syc.calificacion;

public class Calificacion {
	private String cve_unica;
	private String nombre_alumno;
	private String calificacion;
	
	public Calificacion(String cve_unica,String nombre_alumno,String calificacion){
		this.cve_unica = cve_unica;
		this.nombre_alumno = nombre_alumno;
		this.calificacion = calificacion;
	}
	
	public String getCveUnica(){
		return cve_unica;
	}
	
	public String getNombreAlumno(){
		return nombre_alumno;
	}
	
	public String getCalificacion(){
		return calificacion;
	}
}
