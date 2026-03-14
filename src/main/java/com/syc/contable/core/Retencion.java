package com.syc.contable.core;

import java.io.Serializable;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class Retencion implements Serializable {

    private static final long serialVersionUID = 1540226551394911009L;

    private static final Logger log = LoggerFactory.getLogger(Retencion.class);

    private List<RetencionDetalle> detalle;

    private RetencionEncabezado encabezado;

    private int nFolioRetencion;

    public Retencion() {
        log.trace("Retencion vacia creada");
    }

    public Retencion(int nFolioRetencion, RetencionEncabezado encabezado, List<RetencionDetalle> detalle) {
        super();
        this.nFolioRetencion = nFolioRetencion;
        this.encabezado = encabezado;
        this.detalle = detalle;
        log.trace("Retencion creada con parmetros.");
    }

    public List<RetencionDetalle> getDetalle() {
        log.trace("Devolviendo detalle");
        return detalle;
    }

    public RetencionEncabezado getEncabezado() {
        log.trace("Devolviendo encebezado");
        return encabezado;
    }

    public int getnFolioRetencion() {
        return nFolioRetencion;
    }

    public void setDetalle(List<RetencionDetalle> detalle) {
        log.trace("Estableciendo detalle");
        this.detalle = detalle;
    }

    public void setEncabezado(RetencionEncabezado encabezado) {
        log.trace("Estableciendo encabezado de la Retencion.");
        this.encabezado = encabezado;
        log.trace("Encabezado establecido");
        log.trace("Object: {}", this.encabezado);
    }

    public void setnFolioRetencion(int nFolioRetencion) {
        this.nFolioRetencion = nFolioRetencion;
    }

    @Override
    public String toString() {
        return "Retencion [detalle=" + detalle + ", encabezado=" + encabezado + ", nFolioRetencion=" + nFolioRetencion + "]";
    }
}
