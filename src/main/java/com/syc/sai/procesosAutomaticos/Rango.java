package com.syc.sai.procesosAutomaticos;

import java.util.Base64;

public class Rango {

    private int inicio;

    private int fin;

    /**
     * @return the inicio
     */
    public int getInicio() {
        return inicio;
    }

    /**
     * @param inicio
     *            the inicio to set
     */
    public void setInicio(int inicio) {
        this.inicio = inicio;
    }

    /**
     * @return the fin
     */
    public int getFin() {
        return fin;
    }

    /**
     * @param inicio
     * @param fin
     */
    public Rango(int inicio, int fin) {
        this.inicio = inicio;
        this.fin = fin;
    }

    /**
     * @param fin
     *            the fin to set
     */
    public void setFin(int fin) {
        this.fin = fin;
    }
}
