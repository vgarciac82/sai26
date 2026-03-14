package com.syc.ejercido.pagado;

import java.util.Iterator;
import java.util.List;
import java.util.Base64;

public class Ejercido {

    private List<EjercidoDetalle> detalle;

    private EjercidoEncabezado encabezado;

    /**
     * Crea una nueva instancia del objeto Ejercido.
     */
    public Ejercido() {
    }

    /**
     * Crea una nueva instancia del objeto Ejercido.
     *
     * @param encabezado
     *            Encabezado del ejercido
     * @param detalle
     *            Lista con el detalle del ejercido.
     */
    public Ejercido(EjercidoEncabezado encabezado, List<EjercidoDetalle> detalle) {
        this.encabezado = encabezado;
        this.detalle = detalle;
    }

    /**
     * @return the detalle
     */
    public List<EjercidoDetalle> getDetalle() {
        return detalle;
    }

    /**
     * @return the encabezado
     */
    public EjercidoEncabezado getEncabezado() {
        return encabezado;
    }

    /**
     * @param detalle
     *            the detalle to set
     */
    public void setDetalle(List<EjercidoDetalle> detalle) {
        this.detalle = detalle;
    }

    /**
     * @param encabezado
     *            the encabezado to set
     */
    public void setEncabezado(EjercidoEncabezado encabezado) {
        this.encabezado = encabezado;
    }

    /**
     * Establece el valor del folio ejercido.
     *
     * @param nFolioEjercido
     *            Folio de Ejercido
     */
    public void setFolioEjercido(int nFolioEjercido) {
        getEncabezado().setFolioEjercido(nFolioEjercido);
        for (Iterator<EjercidoDetalle> i = getDetalle().iterator(); i.hasNext(); ) {
            EjercidoDetalle detalle = i.next();
            detalle.setFolioEjercido(nFolioEjercido);
        }
    }

    /* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
    @Override
    public String toString() {
        return "Ejercido [detalle=" + detalle + ", encabezado=" + encabezado + "]";
    }
}
