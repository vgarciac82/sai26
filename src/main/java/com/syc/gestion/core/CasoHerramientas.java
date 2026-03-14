package com.syc.gestion.core;

import java.util.Base64;

public class CasoHerramientas {

    private String guardar;

    private String enviar;

    private String cerrar;

    private String descartar;

    public CasoHerramientas() {
        super();
        setGuardar("S");
        setEnviar("S");
        setCerrar("S");
        setDescartar("S");
    }

    /**
     * @return the guardar
     */
    public String getGuardar() {
        return guardar;
    }

    /**
     * @param guardar
     *            the guardar to set
     */
    public void setGuardar(String guardar) {
        this.guardar = guardar;
    }

    /**
     * @return the enviar
     */
    public String getEnviar() {
        return enviar;
    }

    /**
     * @param enviar
     *            the enviar to set
     */
    public void setEnviar(String enviar) {
        this.enviar = enviar;
    }

    /**
     * @return the cerrar
     */
    public String getCerrar() {
        return cerrar;
    }

    /**
     * @param cerrar
     *            the cerrar to set
     */
    public void setCerrar(String cerrar) {
        this.cerrar = cerrar;
    }

    /**
     * @return the descartar
     */
    public String getDescartar() {
        return descartar;
    }

    /**
     * @param descartar
     *            the descartar to set
     */
    public void setDescartar(String descartar) {
        this.descartar = descartar;
    }
}
