package com.syc.gestion.reports;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Map;
import org.jdom.Element;
import java.util.Base64;

public class Reporte implements Serializable {

    private final static long serialVersionUID = 1;

    private int id;

    private String r_nombre;

    private String r_path;

    private String r_descripcion;

    private Map rptParameters = new Hashtable();

    public int getIdReporte() {
        return id;
    }

    public void setIdReporte(int idReporte) {
        this.id = idReporte;
    }

    public String getNombre() {
        return r_nombre;
    }

    public void setNombre(String g_nombre) {
        this.r_nombre = g_nombre;
    }

    public String getRuta() {
        return r_path;
    }

    public void setRuta(String pRuta) {
        this.r_path = pRuta;
    }

    public String getDescripcion() {
        return r_descripcion;
    }

    public void setDescripcion(String g_descripcion) {
        this.r_descripcion = g_descripcion;
    }

    public Map getParametros() {
        return rptParameters;
    }

    public ReporteParametro getParametro(String name) {
        return (ReporteParametro) rptParameters.get(name);
    }

    public void setParametros(Map params) {
        this.rptParameters = params;
    }

    public void setParametro(String name, ReporteParametro prop) {
        rptParameters.put(name, prop);
    }

    public Element toXML() {
        Element elGrupo = new Element("reporte");
        elGrupo.setAttribute("id", Integer.toString(id));
        elGrupo.setAttribute("nombre", r_nombre);
        elGrupo.setAttribute("path", r_path);
        Element elDesc = new Element("descripcion").addContent(r_descripcion);
        elGrupo.addContent(elDesc);
        return elGrupo;
    }
}
