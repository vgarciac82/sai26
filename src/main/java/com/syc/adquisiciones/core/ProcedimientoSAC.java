package com.syc.adquisiciones.core;

import java.util.Base64;

public class ProcedimientoSAC {

    private int nIdProcedimientoSAC;

    private String cIdProcedimientoSAC;

    private String fSolicitud;

    private String cNamePlantilla;

    private String cFechaInicio;

    private String cFechaFin;

    private int nTipoReporte;

    public int getnIdProcedimientoSAC() {
        return nIdProcedimientoSAC;
    }

    public void setnIdProcedimientoSAC(int nIdProcedimientoSAC) {
        this.nIdProcedimientoSAC = nIdProcedimientoSAC;
    }

    public String getcIdProcedimientoSAC() {
        return cIdProcedimientoSAC;
    }

    public void setcIdProcedimientoSAC(String cIdProcedimientoSAC) {
        this.cIdProcedimientoSAC = cIdProcedimientoSAC;
    }

    public String getfSolicitud() {
        return fSolicitud;
    }

    public void setfSolicitud(String fSolicitud) {
        this.fSolicitud = fSolicitud;
    }

    public String getcNamePlantilla() {
        return cNamePlantilla;
    }

    public void setcNamePlantilla(String cNamePlantilla) {
        this.cNamePlantilla = cNamePlantilla;
    }

    public String getcFechaInicio() {
        return cFechaInicio;
    }

    public void setcFechaInicio(String cFechaInicio) {
        this.cFechaInicio = cFechaInicio;
    }

    public String getcFechaFin() {
        return cFechaFin;
    }

    public void setcFechaFin(String cFechaFin) {
        this.cFechaFin = cFechaFin;
    }

    public int getnTipoReporte() {
        return nTipoReporte;
    }

    public void setnTipoReporte(int nTipoReporte) {
        this.nTipoReporte = nTipoReporte;
    }
}
