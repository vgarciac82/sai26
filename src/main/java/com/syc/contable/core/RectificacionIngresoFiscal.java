package com.syc.contable.core;

import java.io.Serializable;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RectificacionIngresoFiscal implements Serializable {

    private static final long serialVersionUID = 1540226551394911009L;

    private static final Logger log = LoggerFactory.getLogger(RectificacionIngresoFiscal.class);

    private List<RectificacionIngresoFiscalDetalle> detalle;

    private RectificacionIngresoFiscalEncabezado encabezado;

    private int nFolioRectificaIngreso;

    public RectificacionIngresoFiscal() {
        log.trace("Creada rectificacion vacia");
    }

    public RectificacionIngresoFiscal(int nFolioRectificaIngreso, RectificacionIngresoFiscalEncabezado encabezado, List<RectificacionIngresoFiscalDetalle> detalle) {
        super();
        this.nFolioRectificaIngreso = nFolioRectificaIngreso;
        this.encabezado = encabezado;
        this.detalle = detalle;
        log.trace("Rectificacion creada con parmetros.");
    }

    public List<RectificacionIngresoFiscalDetalle> getDetalle() {
        log.trace("Devolviendo detalle");
        return detalle;
    }

    public RectificacionIngresoFiscalEncabezado getEncabezado() {
        log.trace("Devolviendo encabezado");
        return encabezado;
    }

    public int getnFolioRectificacion() {
        return nFolioRectificaIngreso;
    }

    public void setDetalle(List<RectificacionIngresoFiscalDetalle> detalle) {
        log.trace("Estableciendo detalle");
        this.detalle = detalle;
    }

    public void setEncabezado(RectificacionIngresoFiscalEncabezado encabezado) {
        log.trace("Estableciendo encabezado de la rectificacion.");
        this.encabezado = encabezado;
        log.trace("Encabezado establecido");
        log.trace("Object: {}", this.encabezado);
    }

    public void setnFolioRectificacion(int nFolioRectificaIngreso) {
        this.nFolioRectificaIngreso = nFolioRectificaIngreso;
    }

    @Override
    public String toString() {
        return "Rectificacion [detalle=" + detalle + ", encabezado=" + encabezado + ", nFolioRectificaIngreso = " + nFolioRectificaIngreso + "]";
    }
}
