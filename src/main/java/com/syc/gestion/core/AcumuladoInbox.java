package com.syc.gestion.core;

import java.io.Serializable;


public class AcumuladoInbox implements Serializable{
	private final static long serialVersionUID = 1;
	private int total_entrada;
	private int total_porenviar;
	private int total_turnados;
	private int total_respuestas;
	private int total_prorrogas;
	
	public int getTotalEntrada() {
	
		return total_entrada;
	}
	
	public void setTotalEntrada(int total_entrada) {
	
		this.total_entrada = total_entrada;
	}
	
	public int getTotalPorEnviar() {
	
		return total_porenviar;
	}
	
	public void setTotalPorEnviar(int total_porenviar) {
	
		this.total_porenviar = total_porenviar;
	}
	
	public int getTotalTurnados() {
	
		return total_turnados;
	}
	
	public void setTotalTurnados(int total_turnados) {
	
		this.total_turnados = total_turnados;
	}
	
	public int getTotalRespuestas() {
	
		return total_respuestas;
	}
	
	public void setTotalRespuestas(int total_respuestas) {
	
		this.total_respuestas = total_respuestas;
	}
	
	public int getTotalProrrogas() {
	
		return total_prorrogas;
	}
	
	public void setTotalProrrogas(int total_prorrogas) {
	
		this.total_prorrogas = total_prorrogas;
	}

}
