package com.syc.contable.core;

import java.util.ArrayList;
import java.util.Base64;

public class Adecuacion {

    private int nFolioAdecuacion;

    private String fCarga;

    private String cTipoAdecuacion;

    private String fAplicacion;

    private String cRamo;

    private String cUnidadResponsable;

    private String cDocumentoHaplicado;

    private int nFolioPoliza;

    private String cTipoPoliza;

    private int nMes;

    private String cRevisado;

    private String aEjercicioFiscal;

    private Consolidacion nFolioConsolidacion;

    private ArrayList<AdecuacionDetalle> arrAdecuacionDetalle;

    private String mMontoAdecuacion;

    private String cJustificacion;

    public String getcJustificacion() {
        return cJustificacion;
    }

    public void setcJustificacion(String cJustificacion) {
        this.cJustificacion = cJustificacion;
    }

    public String getmMontoAdecuacion() {
        return mMontoAdecuacion;
    }

    public void setmMontoAdecuacion(String mMontoAdecuacion) {
        this.mMontoAdecuacion = mMontoAdecuacion;
    }

    public ArrayList<AdecuacionDetalle> getArrAdecuacionDetalle() {
        return arrAdecuacionDetalle;
    }

    public void setArrAdecuacionDetalle(ArrayList<AdecuacionDetalle> arrAdecuacionDetalle) {
        this.arrAdecuacionDetalle = arrAdecuacionDetalle;
    }

    public int getnFolioAdecuacion() {
        return nFolioAdecuacion;
    }

    public void setnFolioAdecuacion(int nFolioAdecuacion) {
        this.nFolioAdecuacion = nFolioAdecuacion;
    }

    public String getfCarga() {
        return fCarga;
    }

    public void setfCarga(String fCarga) {
        this.fCarga = fCarga;
    }

    public String getcTipoAdecuacion() {
        return cTipoAdecuacion;
    }

    public void setcTipoAdecuacion(String cTipoAdecuacion) {
        this.cTipoAdecuacion = cTipoAdecuacion;
    }

    public String getfAplicacion() {
        return fAplicacion;
    }

    public void setfAplicacion(String fAplicacion) {
        this.fAplicacion = fAplicacion;
    }

    public String getcRamo() {
        return cRamo;
    }

    public void setcRamo(String cRamo) {
        this.cRamo = cRamo;
    }

    public String getcUnidadResponsable() {
        return cUnidadResponsable;
    }

    public void setcUnidadResponsable(String cUnidadResponsable) {
        this.cUnidadResponsable = cUnidadResponsable;
    }

    public String getcDocumentoHaplicado() {
        return cDocumentoHaplicado;
    }

    public void setcDocumentoHaplicado(String cDocumentoHaplicado) {
        this.cDocumentoHaplicado = cDocumentoHaplicado;
    }

    public int getnFolioPoliza() {
        return nFolioPoliza;
    }

    public void setnFolioPoliza(int nFolioPoliza) {
        this.nFolioPoliza = nFolioPoliza;
    }

    public String getcTipoPoliza() {
        return cTipoPoliza;
    }

    public void setcTipoPoliza(String cTipoPoliza) {
        this.cTipoPoliza = cTipoPoliza;
    }

    public int getnMes() {
        return nMes;
    }

    public void setnMes(int nMes) {
        this.nMes = nMes;
    }

    public String getcRevisado() {
        return cRevisado;
    }

    public void setcRevisado(String cRevisado) {
        this.cRevisado = cRevisado;
    }

    public String getaEjercicioFiscal() {
        return aEjercicioFiscal;
    }

    public void setaEjercicioFiscal(String aEjercicioFiscal) {
        this.aEjercicioFiscal = aEjercicioFiscal;
    }

    public Consolidacion getnFolioConsolidacion() {
        return nFolioConsolidacion;
    }

    public void setnFolioConsolidacion(Consolidacion nFolioConsolidacion) {
        this.nFolioConsolidacion = nFolioConsolidacion;
    }
}
