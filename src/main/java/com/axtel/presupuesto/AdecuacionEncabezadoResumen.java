package com.axtel.presupuesto;

import java.util.Base64;

public class AdecuacionEncabezadoResumen {

    private String afectaMetas;

    private String apiKey = "#d$FZLS6Zm*yKI*LA0T*Jh5po#j4t73V0FN6s*$NxJ@V";

    private String fAplicacion;

    private int folio;

    private String justificacion;

    private String respuesta;

    /**
     */
    public AdecuacionEncabezadoResumen() {
        super();
    }

    /**
     * @return the afectaMetas
     */
    public String getAfectaMetas() {
        return afectaMetas;
    }

    public String getApiKey() {
        return apiKey;
    }

    /**
     * @return the fAplicacion
     */
    public String getfAplicacion() {
        return fAplicacion;
    }

    /**
     * @return the folio
     */
    public int getFolio() {
        return folio;
    }

    /**
     * @return the justificacion
     */
    public String getJustificacion() {
        return justificacion;
    }

    /**
     * @return the respuesta
     */
    public String getRespuesta() {
        return respuesta;
    }

    /**
     * @param afectaMetas
     *            the afectaMetas to set
     */
    public void setAfectaMetas(String afectaMetas) {
        this.afectaMetas = afectaMetas;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    /**
     * @param fAplicacion
     *            the fAplicacion to set
     */
    public void setfAplicacion(String fAplicacion) {
        this.fAplicacion = fAplicacion;
    }

    /**
     * @param folio
     *            the folio to set
     */
    public void setFolio(int folio) {
        this.folio = folio;
    }

    /**
     * @param justificacion
     *            the justificacion to set
     */
    public void setJustificacion(String justificacion) {
        this.justificacion = justificacion;
    }

    /**
     * @param respuesta
     *            the respuesta to set
     */
    public void setRespuesta(String respuesta) {
        this.respuesta = respuesta;
    }

    @Override
    public String toString() {
        return "AdecuacionEncabezadoResumen [afectaMetas=" + afectaMetas + ", fAplicacion=" + fAplicacion + ", folio=" + folio + ", justificacion=" + justificacion + ", respuesta=" + respuesta + "]";
    }
}
