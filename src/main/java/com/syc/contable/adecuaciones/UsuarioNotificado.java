package com.syc.contable.adecuaciones;

import java.util.ArrayList;
import java.util.List;
import java.util.Base64;

public class UsuarioNotificado extends UsuarioSiplan {

    private int folio;

    private String loginUsuario;

    private List<String> nOrden = new ArrayList<String>();

    private String puesto;

    private String unidad;

    private String usuario;

    /**
     * @return the folio
     */
    public int getFolio() {
        return folio;
    }

    /**
     * @return the loginUsuario
     */
    public String getLoginUsuario() {
        return loginUsuario;
    }

    public List<String> getnOrden() {
        return nOrden;
    }

    /**
     * @return the puesto
     */
    public String getPuesto() {
        return puesto;
    }

    /**
     * @return the unidad
     */
    public String getUnidad() {
        return unidad;
    }

    public String getUsuario() {
        return usuario;
    }

    /**
     * @param folio
     *            the folio to set
     */
    public void setFolio(int folio) {
        this.folio = folio;
    }

    /**
     * @param loginUsuario
     *            the loginUsuario to set
     */
    public void setLoginUsuario(String loginUsuario) {
        this.loginUsuario = loginUsuario;
    }

    public void setnOrden(List<String> nOrden) {
        this.nOrden = nOrden;
    }

    /**
     * @param puesto
     *            the puesto to set
     */
    public void setPuesto(String puesto) {
        this.puesto = puesto;
    }

    /**
     * @param unidad
     *            the unidad to set
     */
    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
}
