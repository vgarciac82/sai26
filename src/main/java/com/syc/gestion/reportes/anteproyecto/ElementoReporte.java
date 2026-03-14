package com.syc.gestion.reportes.anteproyecto;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Base64;

/**
 * Elemento del reporte. Se corresponde con una linea en el excel de reporte de
 * clasificacon economica.
 *
 * @author Vicente Garcia Carrillo
 */
public class ElementoReporte {

    /**
     * Tipo de Gasto. 1: Corriente 2:Inversion
     */
    private int tipoGasto;

    /**
     * Agrupamiento de capitulo con sus montos por momento presupuestal
     */
    private Map<String, ClasificacionMonto> montos;

    /**
     * Crea una nueva instancia del objeto
     *
     * @param tipoGasto
     *            Numero que indica el tipo de gasto
     * @param montos
     *            Agrupamiento de capitulos por momento presupuestal.
     */
    public ElementoReporte(int tipoGasto, Map<String, ClasificacionMonto> montos) {
        super();
        this.tipoGasto = tipoGasto;
        this.montos = montos;
    }

    /**
     * Construye una nueva instancia del objeto con valores por defecto.
     */
    public ElementoReporte() {
        tipoGasto = -1;
        montos = new LinkedHashMap<String, ClasificacionMonto>();
    }

    /**
     * @return the tipoGasto
     */
    public int getTipoGasto() {
        return tipoGasto;
    }

    /**
     * @param tipoGasto
     *            the tipoGasto to set
     */
    public void setTipoGasto(int tipoGasto) {
        this.tipoGasto = tipoGasto;
    }

    /**
     * @return the montos
     */
    public Map<String, ClasificacionMonto> getMontos() {
        return montos;
    }

    /**
     * @param montos
     *            the montos to set
     */
    public void setMontos(Map<String, ClasificacionMonto> montos) {
        this.montos = montos;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
    @Override
    public String toString() {
        return "ElementoReporte [tipoGasto=" + tipoGasto + ", montos=" + montos + "]";
    }
}
