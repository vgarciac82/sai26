package com.syc.gestion.core;

import java.io.Serializable;
import java.util.Vector;
import org.jdom.Element;
import java.util.Base64;

public class Operacion implements Serializable {

    public static final long serialVersionUID = 1L;

    private int id_tc;

    private int id_oper;

    private int o_numero;

    private String o_nombre;

    private String o_responsable;

    private String o_descripcion;

    private String o_plantilla;

    private int o_tiempo_limite;

    private String o_alarma;

    private String o_post_display;

    private String o_post_submit;

    private String o_on_load;

    private String o_on_submit;

    private String o_folder_docto;

    private String o_on_cancel;

    public String getOnCancel() {
        return o_on_cancel;
    }

    public void setOnCancel(String oOnCancel) {
        o_on_cancel = oOnCancel;
    }

    private Vector operacion_sgte;

    public int getIdTC() {
        return id_tc;
    }

    public void setIdTC(int id_tc) {
        this.id_tc = id_tc;
    }

    public int getIdOperacion() {
        return id_oper;
    }

    public void setIdOperacion(int id_oper) {
        this.id_oper = id_oper;
    }

    public int getNumero() {
        return o_numero;
    }

    public void setNumero(int o_numero) {
        this.o_numero = o_numero;
    }

    public String getNombre() {
        return o_nombre;
    }

    public void setNombre(String o_nombre) {
        this.o_nombre = o_nombre;
    }

    public String getResponsable() {
        return o_responsable;
    }

    public void setResponsable(String o_responsable) {
        this.o_responsable = o_responsable;
    }

    public String getDescripcion() {
        return o_descripcion;
    }

    public void setDescripcion(String o_descripcion) {
        this.o_descripcion = o_descripcion;
    }

    public String getPlantilla() {
        return o_plantilla;
    }

    public void setPlantilla(String o_plantilla) {
        this.o_plantilla = o_plantilla;
    }

    public int getTiempoLimite() {
        return o_tiempo_limite;
    }

    public void setTiempoLimite(int o_tiempo_limite) {
        this.o_tiempo_limite = o_tiempo_limite;
    }

    public String getAlarma() {
        return o_alarma;
    }

    public void setAlarma(String o_alarma) {
        this.o_alarma = o_alarma;
    }

    public String getPostDisplay() {
        return o_post_display;
    }

    public void setPostDisplay(String o_post_display) {
        this.o_post_display = o_post_display;
    }

    public String getPostSubmit() {
        return o_post_submit;
    }

    public void setPostSubmit(String o_post_submit) {
        this.o_post_submit = o_post_submit;
    }

    public String getOnLoad() {
        return o_on_load;
    }

    public void setOnLoad(String o_on_load) {
        this.o_on_load = o_on_load;
    }

    public String getOnSubmit() {
        return o_on_submit;
    }

    public void setOnSubmit(String o_on_submit) {
        this.o_on_submit = o_on_submit;
    }

    public Vector getOperacionSgte() {
        if (operacion_sgte == null)
            operacion_sgte = new Vector();
        return operacion_sgte;
    }

    public OperacionSiguiente getOperacionSgte(int index) {
        if (operacion_sgte == null)
            operacion_sgte = new Vector();
        return (OperacionSiguiente) operacion_sgte.get(index);
    }

    public void setOperacionSgte(OperacionSiguiente os) {
        if (operacion_sgte == null)
            operacion_sgte = new Vector();
        operacion_sgte.add(os);
    }

    public void setOperacionSgte(Vector operacion_sgte) {
        this.operacion_sgte = operacion_sgte;
    }

    public String getFolderDocto() {
        return o_folder_docto;
    }

    public void setFolderDocto(String o_folder_docto) {
        this.o_folder_docto = o_folder_docto;
    }

    public Element toXML() {
        Element elOper = new Element("oper");
        elOper.setAttribute("nombre", getNombre());
        elOper.setAttribute("resp", getResponsable());
        Element elDesc = new Element("descripcion").addContent(getDescripcion());
        elOper.addContent(elDesc);
        Element elOpersSgtes = new Element("opersSgtes");
        for (int i = 0; i < getOperacionSgte().size(); i++) {
            Element elOperSgte = new Element("operSgte");
            elOperSgte.setAttribute("resp", getOperacionSgte(i).getResponsable());
            elOperSgte.setAttribute("oper", getOperacionSgte(i).getOperacion());
            elOpersSgtes.addContent(elOperSgte);
        }
        elOper.addContent(elOpersSgtes);
        return elOper;
    }
}
