package com.syc.adquisiciones.core;

import java.util.ArrayList;


public class PedidoModHoja {
	
	private Integer totales = 0;
	private ArrayList<PedidoModLinea> original = new ArrayList<PedidoModLinea>();
	private ArrayList<PedidoModLinea> modificado = new ArrayList<PedidoModLinea>();
	
	public void setOriginal(ArrayList<PedidoModLinea> original) {
		this.original = original;
	}
	
	public ArrayList<PedidoModLinea> getOriginal() {
		return original;
	}
	
	public void setModificado(ArrayList<PedidoModLinea> modificado) {
		this.modificado = modificado;
	}
	
	public ArrayList<PedidoModLinea> getModificado() {
		return modificado;
	}
	
	public Integer getTotales() {
		return totales;
	}
	
	public void setTotales(Integer totales) {
		this.totales = totales;
	}
	
	public static ArrayList<PedidoModHoja> fixDetails(ArrayList<PedidoModLinea> original, ArrayList<PedidoModLinea> modificacion){
		ArrayList<PedidoModLinea> tempOriginal = new ArrayList<PedidoModLinea>();
		tempOriginal.addAll(original);
		ArrayList<PedidoModLinea> tempModificado = new ArrayList<PedidoModLinea>();
		tempModificado.addAll(modificacion);
		
		ArrayList<PedidoModHoja> details = new ArrayList<PedidoModHoja>();
		
		Integer detailsSize = tempModificado.size() / 8;
		for (int i = 0; i < detailsSize; i++){
			PedidoModHoja detail = new PedidoModHoja();
			for (int j = 0; j < 8; j++){
				detail.getOriginal().add(tempOriginal.get(0));
				detail.getModificado().add(tempModificado.get(0));
				tempOriginal.remove(0);
				tempModificado.remove(0);
			}
			details.add(detail);
		}
		
		if (tempModificado.size() > 0){
			PedidoModHoja detail = new PedidoModHoja();
			Integer size = tempModificado.size();
			for (int j = 0; j < size; j++){
				detail.getOriginal().add(tempOriginal.get(j));
				detail.getModificado().add(tempModificado.get(j));
			}
			details.add(detail);
		}
		
		PedidoModHoja last = details.get(details.size() - 1);
		if (last.getModificado().size() > 4) {
			PedidoModHoja detail = new PedidoModHoja();
			detail.setTotales(1);
			details.add(detail);
		}
		else {
			last.setTotales(1);
		}
		
		return details;
	}
	
}