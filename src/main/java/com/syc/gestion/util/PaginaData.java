package com.syc.gestion.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class PaginaData implements Serializable{

	private final static long serialVersionUID = 1;

	private int numero_registros;
	//private int total_paginas;
	private int numero_pagina;
	private int tamano_pagina;
	private String param_consulta;
	private String buscar;
	private String[][] data;
	private String target;
	
	
	List lista = new ArrayList();
	
	public String getParamConsulta() {
	
		return param_consulta;
	}

	
	
	public String getTarget() {
	
		return target;
	}


	
	public void setTarget(String target) {
	
		this.target = target;
	}


	public void setParamConsulta(String param_consulta) {
	
		this.param_consulta = param_consulta;
	}

	
	public int getNumeroPagina() {
	
		return numero_pagina;
	}
	
	public void setNumeroPagina(int numero_pagina) {
	
		this.numero_pagina = numero_pagina;
	}
	
	public int getNumeroRegistros() {
	
		return numero_registros;
	}
	
	
	public void setNumeroRegistros(int numero_registros) {
	
		this.numero_registros = numero_registros;
	}
	
	public int getTotalPaginas() {
		
		return numero_registros / tamano_pagina + ((numero_registros%tamano_pagina>0) ? 0 : -1);
	}
	
	
	public int getTamanoPaginas() {
		
		return tamano_pagina;
	}
	
	
	public void setTamanoPaginas(int tamano_pagina) {
	
		this.tamano_pagina = tamano_pagina;
	}
	

	public List getLista() {
	
		return lista;
	}
	
		public void setLista(List lst) {
	
		this.lista = lst;
	}

	public String[][] getData() {
		
		return data;
	}
	
	public void setData(String[][] data) {
	
		this.data = data;
	}
	
	public String getBuscarFiltro() {
	
		return buscar;
	}

	public void setBuscarFiltro(String filtro) {
	
		this.buscar = filtro;
	}
	
}
