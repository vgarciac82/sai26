package com.syc.contable.core;

import java.util.Date;
import java.util.Base64;

public class FIAFEncabezado {

    int nFolioFIAF;

    Date fCreacion;

    String nAutorizacionMAP;

    Date fMAP;

    String cMotivoRechazo;

    String cUnidadResponsableContable;

    int nFolioPolizaCancelacion;

    Date fCancelacion;

    String cDescripcionPoliza;

    String cJustificacionA;

    String cJustificacionR;

    String cJustificacionNormativa;

    String nFolioTramiteSicop;

    Date fSicop;

    String cMotivoRechazoSicop;

    int nConsecutivoSicop;

    String cDocumentoHaplicado;

    int nFolioPoliza;

    String cTipoPoliza;

    String cRamo;

    Date fAplicacion;

    String aEjercicioFiscal;

    String U_LOGIN;

    String cUnidadResponsable;

    String cCentroContable;

    public int getnFolioFIAF() {
        return nFolioFIAF;
    }

    public void setnFolioFIAF(int nFolioFIAF) {
        this.nFolioFIAF = nFolioFIAF;
    }

    public Date getfCreacion() {
        return fCreacion;
    }

    public void setfCreacion(Date fCreacion) {
        this.fCreacion = fCreacion;
    }

    public String getnAutorizacionMAP() {
        return nAutorizacionMAP;
    }

    public void setnAutorizacionMAP(String nAutorizacionMAP) {
        this.nAutorizacionMAP = nAutorizacionMAP;
    }

    public Date getfMAP() {
        return fMAP;
    }

    public void setfMAP(Date fMAP) {
        this.fMAP = fMAP;
    }

    public String getcMotivoRechazo() {
        return cMotivoRechazo;
    }

    public void setcMotivoRechazo(String cMotivoRechazo) {
        this.cMotivoRechazo = cMotivoRechazo;
    }

    public String getcUnidadResponsableContable() {
        return cUnidadResponsableContable;
    }

    public void setcUnidadResponsableContable(String cUnidadResponsableContable) {
        this.cUnidadResponsableContable = cUnidadResponsableContable;
    }

    public int getnFolioPolizaCancelacion() {
        return nFolioPolizaCancelacion;
    }

    public void setnFolioPolizaCancelacion(int nFolioPolizaCancelacion) {
        this.nFolioPolizaCancelacion = nFolioPolizaCancelacion;
    }

    public Date getfCancelacion() {
        return fCancelacion;
    }

    public void setfCancelacion(Date fCancelacion) {
        this.fCancelacion = fCancelacion;
    }

    public String getcDescripcionPoliza() {
        return cDescripcionPoliza;
    }

    public void setcDescripcionPoliza(String cDescripcionPoliza) {
        this.cDescripcionPoliza = cDescripcionPoliza;
    }

    public String getcJustificacionA() {
        return cJustificacionA;
    }

    public void setcJustificacionA(String cJustificacionA) {
        this.cJustificacionA = cJustificacionA;
    }

    public String getcJustificacionR() {
        return cJustificacionR;
    }

    public void setcJustificacionR(String cJustificacionR) {
        this.cJustificacionR = cJustificacionR;
    }

    public String getcJustificacionNormativa() {
        return cJustificacionNormativa;
    }

    public void setcJustificacionNormativa(String cJustificacionNormativa) {
        this.cJustificacionNormativa = cJustificacionNormativa;
    }

    public String getnFolioTramiteSicop() {
        return nFolioTramiteSicop;
    }

    public void setnFolioTramiteSicop(String nFolioTramiteSicop) {
        this.nFolioTramiteSicop = nFolioTramiteSicop;
    }

    public Date getfSicop() {
        return fSicop;
    }

    public void setfSicop(Date fSicop) {
        this.fSicop = fSicop;
    }

    public String getcMotivoRechazoSicop() {
        return cMotivoRechazoSicop;
    }

    public void setcMotivoRechazoSicop(String cMotivoRechazoSicop) {
        this.cMotivoRechazoSicop = cMotivoRechazoSicop;
    }

    public int getnConsecutivoSicop() {
        return nConsecutivoSicop;
    }

    public void setnConsecutivoSicop(int nConsecutivoSicop) {
        this.nConsecutivoSicop = nConsecutivoSicop;
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

    public String getcRamo() {
        return cRamo;
    }

    public void setcRamo(String cRamo) {
        this.cRamo = cRamo;
    }

    public Date getfAplicacion() {
        return fAplicacion;
    }

    public void setfAplicacion(Date fAplicacion) {
        this.fAplicacion = fAplicacion;
    }

    public String getaEjercicioFiscal() {
        return aEjercicioFiscal;
    }

    public void setaEjercicioFiscal(String aEjercicioFiscal) {
        this.aEjercicioFiscal = aEjercicioFiscal;
    }

    public String getU_LOGIN() {
        return U_LOGIN;
    }

    public void setU_LOGIN(String uLOGIN) {
        U_LOGIN = uLOGIN;
    }

    public String getcUnidadResponsable() {
        return cUnidadResponsable;
    }

    public void setcUnidadResponsable(String cUnidadResponsable) {
        this.cUnidadResponsable = cUnidadResponsable;
    }

    public String getcCentroContable() {
        return cCentroContable;
    }

    public void setcCentroContable(String cCentroContable) {
        this.cCentroContable = cCentroContable;
    }
}
