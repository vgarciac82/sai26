package com.syc.gestion.core;

import java.io.Serializable;
import java.util.Base64;

public class EmpleadoArea implements Serializable {

    private final static long serialVersionUID = 1;

    private String id;

    private String descripcion;

    private String prefijoFolio;

    private int tipoArea = -1;

    private String areaPadre;

    private boolean bandejaEntradaCompartida;

    private boolean bandejaSalidaCompartida;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getPrefijoFolio() {
        return prefijoFolio;
    }

    public void setPrefijoFolio(String folio) {
        this.prefijoFolio = folio;
    }

    public int getTipoArea() {
        return tipoArea;
    }

    public void setTipoArea(int tipoArea) {
        this.tipoArea = tipoArea;
    }

    public EmpleadoArea() {
        super();
    }

    public String getAreaPadre() {
        return areaPadre;
    }

    public void setAreaPadre(String areaPadre) {
        this.areaPadre = areaPadre;
    }

    public boolean isBandejaEntradaCompartida() {
        return bandejaEntradaCompartida;
    }

    public void setBandejaEntradaCompartida(boolean bandejaEntradaCompartida) {
        this.bandejaEntradaCompartida = bandejaEntradaCompartida;
    }

    public boolean isBandejaSalidaCompartida() {
        return bandejaSalidaCompartida;
    }

    public void setBandejaSalidaCompartida(boolean bandejaSalidaCompartida) {
        this.bandejaSalidaCompartida = bandejaSalidaCompartida;
    }

    public String toString() {
        return "com.syc.gestion.core.EmpleadoArea {\n" + "\t\tid=[" + this.id + "],\n" + "\t\tdescripcion=[" + this.descripcion + "],\n" + "\t\tprefijoFolio=[" + this.prefijoFolio + "],\n" + "\t\ttipoArea=[" + this.tipoArea + "],\n" + "\t\tareaPadre=[" + this.areaPadre + "],\n" + "\t\tbandejaEntradaCompartida=[" + this.bandejaEntradaCompartida + "],\n" + "\t\tbandejaSalidaCompartida=[" + this.bandejaSalidaCompartida + "],\n" + "}";
    }
}
