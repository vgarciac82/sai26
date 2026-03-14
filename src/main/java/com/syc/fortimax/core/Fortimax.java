package com.syc.fortimax.core;

import java.util.regex.Pattern;
import java.util.Base64;

public class Fortimax {

    private int idCarp = -1;

    private int idDoc = -1;

    private int idGab = -1;

    private String titApp = new String();

    /**
     * @param idCarp
     * @param idDoc
     * @param idGab
     * @param titApp
     */
    public Fortimax(String titApp, int idGab, int idCarp, int idDoc) {
        this.idCarp = idCarp;
        this.idDoc = idDoc;
        this.idGab = idGab;
        this.titApp = titApp;
    }

    public static boolean esNodoValido(String cadena) {
        String regex = "^[A-Za-z0-9]+_G\\d+C\\d+D\\d+$";
        return Pattern.matches(regex, cadena);
    }

    public Fortimax(String nodeId) {
        int maxLen = nodeId.length() - 1;
        int lastPos = maxLen;
        if (hasNotNumbers(nodeId)) {
            titApp = nodeId;
            return;
        }
        for (int i = maxLen; i >= 0; i--) {
            if ('D' == nodeId.charAt(i)) {
                lastPos = i;
                idDoc = Integer.parseInt(nodeId.substring(i + 1));
            } else if ('C' == nodeId.charAt(i)) {
                if (maxLen == lastPos)
                    idCarp = Integer.parseInt(nodeId.substring(i + 1));
                else
                    idCarp = Integer.parseInt(nodeId.substring(i + 1, lastPos));
                lastPos = i;
            } else if ('G' == nodeId.charAt(i)) {
                idGab = Integer.parseInt(nodeId.substring(i + 1, lastPos));
                lastPos = i - 1;
                break;
            }
        }
        titApp = nodeId.substring(0, lastPos);
    }

    /**
     * @return the idCarp
     */
    public int getIdCarp() {
        return idCarp;
    }

    public int getIdCarpeta() {
        return idCarp;
    }

    /**
     * @return the idDoc
     */
    public int getIdDoc() {
        return idDoc;
    }

    public int getIdDocumento() {
        return idDoc;
    }

    /**
     * @return the idGab
     */
    public int getIdGab() {
        return idGab;
    }

    public int getIdGabinete() {
        return idGab;
    }

    /**
     * @return the titApp
     */
    public String getTitApp() {
        return titApp;
    }

    public String getTituloAplicacion() {
        return titApp;
    }

    private boolean hasNotNumbers(String s) {
        for (int i = 0; i < s.length(); i++) if (Character.isDigit(s.charAt(i)))
            return false;
        return true;
    }

    public boolean isDocumento() {
        return idDoc != -1;
    }

    /**
     * @param idCarp
     *            the idCarp to set
     */
    public void setIdCarp(int idCarp) {
        this.idCarp = idCarp;
    }

    /**
     * @param idDoc
     *            the idDoc to set
     */
    public void setIdDoc(int idDoc) {
        this.idDoc = idDoc;
    }

    /**
     * @param idGab
     *            the idGab to set
     */
    public void setIdGab(int idGab) {
        this.idGab = idGab;
    }

    /**
     * @param titApp
     *            the titApp to set
     */
    public void setTitApp(String titApp) {
        this.titApp = titApp;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
    @Override
    public String toString() {
        return titApp + "_G" + idGab + "C" + idCarp + "D" + idDoc;
    }
}
