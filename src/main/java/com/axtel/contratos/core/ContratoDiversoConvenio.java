package com.axtel.contratos.core;

import java.util.Base64;

/**
 * @author hfariasr
 */
public class ContratoDiversoConvenio {

    private String cEjercicio;

    private String cIdEntidadContable;

    private String cIdContrato;

    private int nConsecutivoModificacion;

    private double mConvenio;

    private double mIVAConvenio;

    private double mTotal;

    private double mGlobalContrato;

    private String fInicio;

    private String fTermino;

    private String fFirmaContrato;

    private String cIdModificacion;

    private String cOrigenRM;

    private String fAdjudicacion;

    private int nDocumentoAbierto;

    public String getcEjercicio() {
        return cEjercicio;
    }

    public void setcEjercicio(String cEjercicio) {
        this.cEjercicio = cEjercicio;
    }

    public String getcIdEntidadContable() {
        return cIdEntidadContable;
    }

    public void setcIdEntidadContable(String cIdEntidadContable) {
        this.cIdEntidadContable = cIdEntidadContable;
    }

    public String getcIdContrato() {
        return cIdContrato;
    }

    public void setcIdContrato(String cIdContrato) {
        this.cIdContrato = cIdContrato;
    }

    public int getnConsecutivoModificacion() {
        return nConsecutivoModificacion;
    }

    public void setnConsecutivoModificacion(int nConsecutivoModificacion) {
        this.nConsecutivoModificacion = nConsecutivoModificacion;
    }

    public double getmConvenio() {
        return mConvenio;
    }

    public void setmConvenio(double mConvenio) {
        this.mConvenio = mConvenio;
    }

    public double getmIVAConvenio() {
        return mIVAConvenio;
    }

    public void setmIVAConvenio(double mIVAConvenio) {
        this.mIVAConvenio = mIVAConvenio;
    }

    public double getmTotal() {
        return mTotal;
    }

    public void setmTotal(double mTotal) {
        this.mTotal = mTotal;
    }

    public double getmGlobalContrato() {
        return mGlobalContrato;
    }

    public void setmGlobalContrato(double mGlobalContrato) {
        this.mGlobalContrato = mGlobalContrato;
    }

    public String getfInicio() {
        return fInicio;
    }

    public void setfInicio(String fInicio) {
        this.fInicio = fInicio;
    }

    public String getfTermino() {
        return fTermino;
    }

    public void setfTermino(String fTermino) {
        this.fTermino = fTermino;
    }

    public String getfFirmaContrato() {
        return fFirmaContrato;
    }

    public void setfFirmaContrato(String fFirmaContrato) {
        this.fFirmaContrato = fFirmaContrato;
    }

    public String getcIdModificacion() {
        return cIdModificacion;
    }

    public void setcIdModificacion(String cIdModificacion) {
        this.cIdModificacion = cIdModificacion;
    }

    public String getcOrigenRM() {
        return cOrigenRM;
    }

    public void setcOrigenRM(String cOrigenRM) {
        this.cOrigenRM = cOrigenRM;
    }

    public String getfAdjudicacion() {
        return fAdjudicacion;
    }

    public void setfAdjudicacion(String fAdjudicacion) {
        this.fAdjudicacion = fAdjudicacion;
    }

    public int getnDocumentoAbierto() {
        return nDocumentoAbierto;
    }

    public void setnDocumentoAbierto(int nDocumentoAbierto) {
        this.nDocumentoAbierto = nDocumentoAbierto;
    }
}
