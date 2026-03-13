package com.syc.adquisiciones.core;

import java.util.ArrayList;
import java.util.HashMap;


public class APTEProveedor {
	
	private String rfc = "";
	private String proveedor = "";
	private ArrayList<APTEProveedorDoc> docs = new ArrayList<APTEProveedorDoc>();
	private HashMap<String, APTEProveedorDoc> docMap = new HashMap<String, APTEProveedorDoc>();
	
	public String getRfc() {
		return rfc;
	}
	
	public void setRfc(String rfc) {
		if (rfc != null) rfc = rfc.trim();
		this.rfc = rfc;
	}
	
	public void setProveedor(String proveedor) {
		this.proveedor = proveedor;
	}
	
	public String getProveedor() {
		return proveedor;
	}

	public void setDocs(ArrayList<APTEProveedorDoc> docs) {
		this.docs = docs;
	}

	public ArrayList<APTEProveedorDoc> getDocs() {
		return docs;
	}
	
	public HashMap<String, APTEProveedorDoc> getDocMap() {
		return docMap;
	}

	public void setDocMap(HashMap<String, APTEProveedorDoc> docMap) {
		this.docMap = docMap;
	}
	
	public void addDocPresentado(String docId, String desc, Boolean presento){
		APTEProveedorDoc doc = this.docMap.get(docId);
		if (doc != null){
			doc.setDocumento(desc);
			doc.setPresento(presento);
		}
	}

	public static void llenarDocs(ArrayList<ArrayList<String>> catalogoDocs, ArrayList<APTEProveedor> proveedores){
		for (APTEProveedor prov : proveedores){
			for (ArrayList<String> catdoc : catalogoDocs){
				APTEProveedorDoc doc = new APTEProveedorDoc();
				doc.setId(catdoc.get(0));
				doc.setDocumento(catdoc.get(1));
				doc.setReferencia(catdoc.get(2));
				prov.getDocs().add(doc);
				prov.getDocMap().put(doc.getId(), doc);
			}
		}
	}
	
}
