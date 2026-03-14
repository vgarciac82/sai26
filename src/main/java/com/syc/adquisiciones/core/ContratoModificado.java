package com.syc.adquisiciones.core;

import java.util.Base64;

public class ContratoModificado {

    String cEjercicio;

    String cIdContrato;

    String cIdContratoDefinitivo;

    int nConsecutivoModificacion;

    double mTotalAnterior;

    double mTotalNuevo;

    double mTotalModificacion;

    int nEstado;

    int nTipoMod;

    int nTotalPlurianual;

    int nConvenioEjercicioAnt;

    int nContratoAbierto;

    int nEsDescentralizado;

    String cadTabla;

    String cUnidadEjecutoraLinea;

    int nFolioCompromiso;

    String cFolioCompromiso;

    int nFolioPreCompromiso;

    String cFolioPreCompromiso;

    String cCentroContable;

    String prefixPath;

    String cCanoCompromiso;

    String cNoConvenio;

    String cObjetoConv;

    String fFechaInicio;

    String fFechaFin;

    String fFechaFormalizacion;

    String jndiName;

    String cEntidadContable;

    String cIdRFC;

    public String getcEjercicio() {
        return cEjercicio;
    }

    public void setcEjercicio(String cEjercicio) {
        this.cEjercicio = cEjercicio;
    }

    public String getcIdContrato() {
        return cIdContrato;
    }

    public void setcIdContrato(String cIdContrato) {
        this.cIdContrato = cIdContrato;
    }

    public String getcIdContratoDefinitivo() {
        return cIdContratoDefinitivo;
    }

    public void setcIdContratoDefinitivo(String cIdContratoDefinitivo) {
        this.cIdContratoDefinitivo = cIdContratoDefinitivo;
    }

    public int getnConsecutivoModificacion() {
        return nConsecutivoModificacion;
    }

    public void setnConsecutivoModificacion(int nConsecutivoModificacion) {
        this.nConsecutivoModificacion = nConsecutivoModificacion;
    }

    public double getmTotalAnterior() {
        return mTotalAnterior;
    }

    public void setmTotalAnterior(double mTotalAnterior) {
        this.mTotalAnterior = mTotalAnterior;
    }

    public double getmTotalNuevo() {
        return mTotalNuevo;
    }

    public void setmTotalNuevo(double mTotalNuevo) {
        this.mTotalNuevo = mTotalNuevo;
    }

    public double getmTotalModificacion() {
        return mTotalModificacion;
    }

    public void setmTotalModificacion(double mTotalModificacion) {
        this.mTotalModificacion = mTotalModificacion;
    }

    public int getnEstado() {
        return nEstado;
    }

    public void setnEstado(int nEstado) {
        this.nEstado = nEstado;
    }

    public int getnTipoMod() {
        return nTipoMod;
    }

    public void setnTipoMod(int nTipoMod) {
        this.nTipoMod = nTipoMod;
    }

    public String getCadTabla() {
        return cadTabla;
    }

    public void setCadTabla(String cadTabla) {
        this.cadTabla = cadTabla;
    }

    public int getnTotalPlurianual() {
        return nTotalPlurianual;
    }

    public void setnTotalPlurianual(int nTotalPlurianual) {
        this.nTotalPlurianual = nTotalPlurianual;
    }

    public int getnConvenioEjercicioAnt() {
        return nConvenioEjercicioAnt;
    }

    public void setnConvenioEjercicioAnt(int nConvenioEjercicioAnt) {
        this.nConvenioEjercicioAnt = nConvenioEjercicioAnt;
    }

    public int getnContratoAbierto() {
        return nContratoAbierto;
    }

    public void setnContratoAbierto(int nContratoAbierto) {
        this.nContratoAbierto = nContratoAbierto;
    }

    public String getcUnidadEjecutoraLinea() {
        return cUnidadEjecutoraLinea;
    }

    public void setcUnidadEjecutoraLinea(String cUnidadEjecutoraLinea) {
        this.cUnidadEjecutoraLinea = cUnidadEjecutoraLinea;
    }

    public int getnFolioCompromiso() {
        return nFolioCompromiso;
    }

    public void setnFolioCompromiso(int nFolioCompromiso) {
        this.nFolioCompromiso = nFolioCompromiso;
    }

    public String getcCentroContable() {
        return cCentroContable;
    }

    public void setcCentroContable(String cCentroContable) {
        this.cCentroContable = cCentroContable;
    }

    public String getPrefixPath() {
        return prefixPath;
    }

    public void setPrefixPath(String prefixPath) {
        this.prefixPath = prefixPath;
    }

    public String getcCanoCompromiso() {
        return cCanoCompromiso;
    }

    public void setcCanoCompromiso(String cCanoCompromiso) {
        this.cCanoCompromiso = cCanoCompromiso;
    }

    public String getcNoConvenio() {
        return cNoConvenio;
    }

    public void setcNoConvenio(String cNoConvenio) {
        this.cNoConvenio = cNoConvenio;
    }

    public String getcObjetoConv() {
        return cObjetoConv;
    }

    public void setcObjetoConv(String cObjetoConv) {
        this.cObjetoConv = cObjetoConv;
    }

    public String getfFechaInicio() {
        return fFechaInicio;
    }

    public void setfFechaInicio(String fFechaInicio) {
        this.fFechaInicio = fFechaInicio;
    }

    public String getfFechaFin() {
        return fFechaFin;
    }

    public void setfFechaFin(String fFechaFin) {
        this.fFechaFin = fFechaFin;
    }

    public String getfFechaFormalizacion() {
        return fFechaFormalizacion;
    }

    public void setfFechaFormalizacion(String fFechaFormalizacion) {
        this.fFechaFormalizacion = fFechaFormalizacion;
    }

    public int getnEsDescentralizado() {
        return nEsDescentralizado;
    }

    public void setnEsDescentralizado(int nEsDescentralizado) {
        this.nEsDescentralizado = nEsDescentralizado;
    }

    public String getJndiName() {
        return jndiName;
    }

    public void setJndiName(String jndiName) {
        this.jndiName = jndiName;
    }

    public int getnFolioPreCompromiso() {
        return nFolioPreCompromiso;
    }

    public void setnFolioPreCompromiso(int nFolioPreCompromiso) {
        this.nFolioPreCompromiso = nFolioPreCompromiso;
    }

    public String getcFolioPreCompromiso() {
        return cFolioPreCompromiso;
    }

    public void setcFolioPreCompromiso(String cFolioPreCompromiso) {
        this.cFolioPreCompromiso = cFolioPreCompromiso;
    }

    public String getcEntidadContable() {
        return cEntidadContable;
    }

    public void setcEntidadContable(String cEntidadContable) {
        this.cEntidadContable = cEntidadContable;
    }

    public String getcIdRFC() {
        return cIdRFC;
    }

    public void setcIdRFC(String cIdRFC) {
        this.cIdRFC = cIdRFC;
    }

    public String getcFolioCompromiso() {
        return cFolioCompromiso;
    }

    public void setcFolioCompromiso(String cFolioCompromiso) {
        this.cFolioCompromiso = cFolioCompromiso;
    }
}
