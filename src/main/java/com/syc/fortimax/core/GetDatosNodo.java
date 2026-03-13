package com.syc.fortimax.core;

public class GetDatosNodo {
	
	private String nodo = null;
	private String titulo_aplicacion = null;
	private int gabinete;
	private int carpeta;
	private int documento;
	
	public GetDatosNodo(String nodo){
		this.nodo = nodo;
	}
	
	public boolean isDocumento(){
		boolean retVal = false;
		separaDatosGabinete();
		String s = nodo.substring(titulo_aplicacion.length());
		if(s.indexOf("D")!=-1)
			return true;
		
		return retVal;
	}

	public boolean isCarpeta(){
		boolean retVal = false;
		separaDatosGabinete();
		String s = nodo.substring(titulo_aplicacion.length());
		if(s.indexOf("C")!=-1)
			return true;
		
		return retVal;
	}
	
	public boolean isGabinete(){
		boolean retVal = false;
		String s = nodo.substring(titulo_aplicacion.length());
		if(s.indexOf("G")!=-1)
			return true;
		
		return retVal;
	}

	public void separaDatosDocumento(){
		documento = new Integer(nodo.substring(nodo.lastIndexOf("D")+1)).intValue();
		separaDatosCarpeta();
	}
	
	public void separaDatosCarpeta(){
		String s = nodo.substring(nodo.lastIndexOf("C")+1);
		if(s.indexOf("D")!=-1){
			s = s.substring(0,s.lastIndexOf("D"));
		}
		carpeta = new Integer(s).intValue();
		
		separaDatosGabinete();
	}
	
	public void separaDatosGabinete(){
		String s = nodo.substring(nodo.lastIndexOf("G")+1);
		if(s.indexOf("C")!=-1){
			s = s.substring(0,s.indexOf("C"));
		}
		gabinete = new Integer(s).intValue();
		
		titulo_aplicacion = nodo.substring(0,nodo.lastIndexOf("G")-1);
	}
	
	public int getIdDocumento(){
		return documento;
	}
	
	public int getIdCarpeta(){
		return carpeta;
	}
	
	public int getGabinete(){
		return gabinete;
	}
	
	public String getGaveta(){
		return titulo_aplicacion;
	}	
}
