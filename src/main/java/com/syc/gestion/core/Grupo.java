package com.syc.gestion.core;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.jdom.Element;
import java.util.Base64;

public class Grupo implements Serializable {

    private static final long serialVersionUID = -1571699656344931063L;

    private String g_nombre;

    private String g_descripcion;

    private Map grpProperties = new Hashtable();

    public Grupo() {
        super();
    }

    public Grupo(String g_nombre) {
        super();
        this.g_nombre = g_nombre;
    }

    public String getNombre() {
        return g_nombre;
    }

    public void setNombre(String g_nombre) {
        this.g_nombre = g_nombre;
    }

    public String getDescripcion() {
        return g_descripcion;
    }

    public void setDescripcion(String g_descripcion) {
        this.g_descripcion = g_descripcion;
    }

    public Map getPropiedades() {
        return grpProperties;
    }

    public GrupoPropiedades getPropiedad(String name) {
        return (GrupoPropiedades) grpProperties.get(name);
    }

    public void setPropiedades(Map properties) {
        this.grpProperties = properties;
    }

    public void setPropiedad(String name, GrupoPropiedades prop) {
        grpProperties.put(name, prop);
    }

    public Element toXML() {
        Element elGrupo = new Element("grupo");
        elGrupo.setAttribute("nombre", g_nombre);
        Element elDesc = new Element("descripcion").addContent(g_descripcion);
        elGrupo.addContent(elDesc);
        return elGrupo;
    }

    public Element toXML(List opers) {
        Element elGrupo = toXML();
        Element elOpers = new Element("opers");
        for (Iterator iter = opers.iterator(); iter.hasNext(); ) elOpers.addContent(((Operacion) iter.next()).toXML());
        elGrupo.addContent(elOpers);
        return elGrupo;
    }
}
