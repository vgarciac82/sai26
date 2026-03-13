package com.syc.fortimax.core;

public class OrgCarpeta {

	private String titulo_aplicacion;
	private int id_gabinete;
	private int id_carpeta_hija;
	private int id_carpeta_padre;
	private String nombre_hija;

	public int getIdCarpetaHija() {
		return id_carpeta_hija;
	}

	public void setIdCarpetaHija(int id_carpeta_hija) {
		this.id_carpeta_hija = id_carpeta_hija;
	}

	public int getIdCarpetaPadre() {
		return id_carpeta_padre;
	}

	public void setIdCarpetaPadre(int id_carpeta_padre) {
		this.id_carpeta_padre = id_carpeta_padre;
	}

	public int getIdGabinete() {
		return id_gabinete;
	}

	public void setIdGabinete(int id_gabinete) {
		this.id_gabinete = id_gabinete;
	}

	public String getNombreHija() {
		return nombre_hija;
	}

	public void setNombreHija(String nombre_hija) {
		this.nombre_hija = nombre_hija;
	}

	public String getTituloAplicacion() {
		return titulo_aplicacion;
	}

	public void setTituloAplicacion(String titulo_aplicacion) {
		this.titulo_aplicacion = titulo_aplicacion;
	}
}
