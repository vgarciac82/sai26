/**
 */
package com.syc.contable.caja.core;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Propietario
 */
public class SolicitudNoPresupuestal {

    private static final Logger log = LoggerFactory.getLogger(SolicitudNoPresupuestal.class);

    private List<SolicitudNoPresupuestalDetalle> detalle = null;

    private SolicitudNoPresupuestalEncabezado encabezado = null;

    /**
     */
    public SolicitudNoPresupuestal() {
        log.trace("Nueva instancia creada");
        encabezado = new SolicitudNoPresupuestalEncabezado();
        detalle = new ArrayList<SolicitudNoPresupuestalDetalle>();
    }

    public SolicitudNoPresupuestal(SolicitudNoPresupuestalEncabezado encabezado, List<SolicitudNoPresupuestalDetalle> detalle) {
        super();
        this.encabezado = encabezado;
        this.detalle = detalle;
        log.trace("Instancia creada con los objetos: Encabezado[" + encabezado + "] Detalle[" + detalle + "]");
    }

    public List<SolicitudNoPresupuestalDetalle> getDetalle() {
        return detalle;
    }

    public SolicitudNoPresupuestalDetalle getDetalle(int index) {
        return detalle.get(index);
    }

    public SolicitudNoPresupuestalEncabezado getEncabezado() {
        return encabezado;
    }

    public void setDetalle(List<SolicitudNoPresupuestalDetalle> detalle) {
        this.detalle = detalle;
    }

    public void setDetalle(SolicitudNoPresupuestalDetalle detalle) {
        this.detalle.add(detalle);
    }

    public void setEncabezado(SolicitudNoPresupuestalEncabezado encabezado) {
        this.encabezado = encabezado;
    }

    @Override
    public String toString() {
        return "SolicitudNoPresupuestal [detalle=" + detalle + ", encabezado=" + encabezado + "]";
    }

    public void setFolioSNP(int nFolioCaja) {
        getEncabezado().setFolioCaja(nFolioCaja);
        if (getDetalle() != null)
            for (int i = 0; i < getDetalle().size(); i++) getDetalle().get(i).setFoliocaja(nFolioCaja);
    }
}
