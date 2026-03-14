package com.syc.contable.core;

import java.util.Base64;

public class RetencionDetalle {

    private int nFolioRetencion;

    private int nDocRenglon;

    private String cMes;

    private String cEvento;

    private String EP;

    private double mImporte;

    private double m2Millar;

    private double mObra5;

    private double mImporteFlete4;

    private double mISRHonorarios;

    private double mISRArrenda;

    private double mRetImpuestoCedular;

    private double mImporteIvaArrenda;

    private double mImporteIvaHonorarios;

    private double mOtrosImpuestos;

    private double mImporteISRLaudos;

    private double mISROtros;

    private double mImporteIva6;

    private String cUnidadResponsable;

    private String cPasivo;

    private double mPasivoDiferido;

    private String rfc;

    private String obgt;

    private String cEjercicio;

    private int cCentroContable;

    public String getcEjercicio() {
        return cEjercicio;
    }

    public void setcEjercicio(String cEjercicio) {
        this.cEjercicio = cEjercicio;
    }

    public int getcCentroContable() {
        return cCentroContable;
    }

    public void setcCentroContable(int cCentroContable) {
        this.cCentroContable = cCentroContable;
    }

    public String getRfc() {
        return rfc;
    }

    public void setRfc(String rfc) {
        this.rfc = rfc;
    }

    public String getObgt() {
        return obgt;
    }

    public void setObgt(String obgt) {
        this.obgt = obgt;
    }

    public String getcPasivo() {
        return cPasivo;
    }

    public void setcPasivo(String cPasivo) {
        this.cPasivo = cPasivo;
    }

    public double getmPasivoDiferido() {
        return mPasivoDiferido;
    }

    public void setmPasivoDiferido(double mPasivoDiferido) {
        this.mPasivoDiferido = mPasivoDiferido;
    }

    public int getnFolioRetencion() {
        return nFolioRetencion;
    }

    public void setnFolioRetencion(int nFolioRetencion) {
        this.nFolioRetencion = nFolioRetencion;
    }

    public int getnDocRenglon() {
        return nDocRenglon;
    }

    public void setnDocRenglon(int nDocRenglon) {
        this.nDocRenglon = nDocRenglon;
    }

    public String getcMes() {
        return cMes;
    }

    public void setcMes(String cMes) {
        this.cMes = cMes;
    }

    public String getcEvento() {
        return cEvento;
    }

    public void setcEvento(String cEvento) {
        this.cEvento = cEvento;
    }

    public String getEP() {
        return EP;
    }

    public void setEP(String eP) {
        EP = eP;
    }

    public double getmImporte() {
        return mImporte;
    }

    public void setmImporte(double mImporte) {
        this.mImporte = mImporte;
    }

    public double getM2Millar() {
        return m2Millar;
    }

    public void setM2Millar(double m2Millar) {
        this.m2Millar = m2Millar;
    }

    public double getmObra5() {
        return mObra5;
    }

    public void setmObra5(double mObra5) {
        this.mObra5 = mObra5;
    }

    public double getmImporteFlete4() {
        return mImporteFlete4;
    }

    public void setmImporteFlete4(double mImporteFlete4) {
        this.mImporteFlete4 = mImporteFlete4;
    }

    public double getmISRHonorarios() {
        return mISRHonorarios;
    }

    public void setmISRHonorarios(double mISRHonorarios) {
        this.mISRHonorarios = mISRHonorarios;
    }

    public double getmISRArrenda() {
        return mISRArrenda;
    }

    public void setmISRArrenda(double mISRArrenda) {
        this.mISRArrenda = mISRArrenda;
    }

    public double getmRetImpuestoCedular() {
        return mRetImpuestoCedular;
    }

    public void setmRetImpuestoCedular(double mRetImpuestoCedular) {
        this.mRetImpuestoCedular = mRetImpuestoCedular;
    }

    public double getmImporteIvaArrenda() {
        return mImporteIvaArrenda;
    }

    public void setmImporteIvaArrenda(double mImporteIvaArrenda) {
        this.mImporteIvaArrenda = mImporteIvaArrenda;
    }

    public double getmImporteIvaHonorarios() {
        return mImporteIvaHonorarios;
    }

    public void setmImporteIvaHonorarios(double mImporteIvaHonorarios) {
        this.mImporteIvaHonorarios = mImporteIvaHonorarios;
    }

    public double getmOtrosImpuestos() {
        return mOtrosImpuestos;
    }

    public void setmOtrosImpuestos(double mOtrosImpuestos) {
        this.mOtrosImpuestos = mOtrosImpuestos;
    }

    public double getmImporteISRLaudos() {
        return mImporteISRLaudos;
    }

    public void setmImporteISRLaudos(double mImporteISRLaudos) {
        this.mImporteISRLaudos = mImporteISRLaudos;
    }

    public double getmISROtros() {
        return mISROtros;
    }

    public void setmISROtros(double mISROtros) {
        this.mISROtros = mISROtros;
    }

    public double getmImporteIva6() {
        return mImporteIva6;
    }

    public void setmImporteIva6(double mImporteIva6) {
        this.mImporteIva6 = mImporteIva6;
    }

    public String getcUnidadResponsable() {
        return cUnidadResponsable;
    }

    public void setcUnidadResponsable(String cUnidadResponsable) {
        this.cUnidadResponsable = cUnidadResponsable;
    }

    @Override
    public String toString() {
        return "RectificacionDetalle [nFolioRetencion=" + nFolioRetencion + ", nDocRenglon=" + nDocRenglon + ", cMes=" + cMes + ", cEvento=" + cEvento + ", EP=" + EP + ", mImporte=" + mImporte + ", m2Millar=" + m2Millar + ", mObra5=" + mObra5 + ", mImporteFlete4=" + mImporteFlete4 + ", mISRHonorarios=" + mISRHonorarios + ", mISRArrenda=" + mISRArrenda + ", mRetImpuestoCedular=" + mRetImpuestoCedular + ", mImporteIvaArrenda=" + mImporteIvaArrenda + ", mImporteIvaHonorarios=" + mImporteIvaHonorarios + ", mOtrosImpuestos=" + mOtrosImpuestos + ", mImporteISRLaudos=" + mImporteISRLaudos + ", mISROtros=" + mISROtros + ", mImporteIva6=" + mImporteIva6 + ", cUnidadResponsable=" + cUnidadResponsable + "]";
    }
}
