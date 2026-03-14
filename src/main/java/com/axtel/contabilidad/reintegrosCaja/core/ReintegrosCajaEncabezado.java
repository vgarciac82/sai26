package com.axtel.contabilidad.reintegrosCaja.core;

import java.util.Base64;

/**
 * @author Ana
 */
public class ReintegrosCajaEncabezado {

    private int nFolioReintegrocaja;

    private String fCreacion;

    private String fAplicacion;

    private int cMes;

    private String cTipoPoliza;

    private int nFolioPoliza;

    private String cDescripcionPoliza;

    private String U_LOGIN;

    private String cDocumentohAplicado;

    private String cMotivoRechazo;

    private String cUnidadResponsableContable;

    private int nFolioPolizaCancelacion;

    private String fCancelacion;

    private String aEjercicioFiscal;

    private String cRamo;

    private String cUnidadEjecutora;

    private String cIdUsuarioCaptura;

    private int ID_CASO;

    private double mMontoSolicitud;

    private String cEsFirmaElectronica;

    private int nNumEmpleadoVoBo;

    private int nNumEmpleadoAut;

    private int nNumEmpleadoElab;

    private int nEnviadoSICOP;

    public void setnFolioReintegrocaja(int nFolioReintegrocaja) {
        this.nFolioReintegrocaja = nFolioReintegrocaja;
    }

    public void setfCreacion(String fCreacion) {
        this.fCreacion = fCreacion;
    }

    public void setfAplicacion(String fAplicacion) {
        this.fAplicacion = fAplicacion;
    }

    public void setcMes(int cMes) {
        this.cMes = cMes;
    }

    public void setcTipoPoliza(String cTipoPoliza) {
        this.cTipoPoliza = cTipoPoliza;
    }

    public void setnFolioPoliza(int nFolioPoliza) {
        this.nFolioPoliza = nFolioPoliza;
    }

    public void setcDescripcionPoliza(String cDescripcionPoliza) {
        this.cDescripcionPoliza = cDescripcionPoliza;
    }

    public void setU_LOGIN(String u_LOGIN) {
        U_LOGIN = u_LOGIN;
    }

    public void setcDocumentohAplicado(String cDocumentohAplicado) {
        this.cDocumentohAplicado = cDocumentohAplicado;
    }

    public void setcMotivoRechazo(String cMotivoRechazo) {
        this.cMotivoRechazo = cMotivoRechazo;
    }

    public void setcUnidadResponsableContable(String cUnidadResponsableContable) {
        this.cUnidadResponsableContable = cUnidadResponsableContable;
    }

    public void setnFolioPolizaCancelacion(int nFolioPolizaCancelacion) {
        this.nFolioPolizaCancelacion = nFolioPolizaCancelacion;
    }

    public void setfCancelacion(String fCancelacion) {
        this.fCancelacion = fCancelacion;
    }

    public void setaEjercicioFiscal(String aEjercicioFiscal) {
        this.aEjercicioFiscal = aEjercicioFiscal;
    }

    public void setcRamo(String cRamo) {
        this.cRamo = cRamo;
    }

    public void setcUnidadEjecutora(String cUnidadEjecutora) {
        this.cUnidadEjecutora = cUnidadEjecutora;
    }

    public void setcIdUsuarioCaptura(String cIdUsuarioCaptura) {
        this.cIdUsuarioCaptura = cIdUsuarioCaptura;
    }

    public void setID_CASO(int iD_CASO) {
        ID_CASO = iD_CASO;
    }

    public void setmMontoSolicitud(double mMontoSolicitud) {
        this.mMontoSolicitud = mMontoSolicitud;
    }

    public void setcEsFirmaElectronica(String cEsFirmaElectronica) {
        this.cEsFirmaElectronica = cEsFirmaElectronica;
    }

    public void setnNumEmpleadoVoBo(int nNumEmpleadoVoBo) {
        this.nNumEmpleadoVoBo = nNumEmpleadoVoBo;
    }

    public void setnNumEmpleadoAut(int nNumEmpleadoAut) {
        this.nNumEmpleadoAut = nNumEmpleadoAut;
    }

    public void setnNumEmpleadoElab(int nNumEmpleadoElab) {
        this.nNumEmpleadoElab = nNumEmpleadoElab;
    }

    public void setnEnviadoSICOP(int nEnviadoSICOP) {
        this.nEnviadoSICOP = nEnviadoSICOP;
    }

    public int getnFolioReintegrocaja() {
        return nFolioReintegrocaja;
    }

    public String getfCreacion() {
        return fCreacion;
    }

    public String getfAplicacion() {
        return fAplicacion;
    }

    public int getcMes() {
        return cMes;
    }

    public String getcTipoPoliza() {
        return cTipoPoliza;
    }

    public int getnFolioPoliza() {
        return nFolioPoliza;
    }

    public String getcDescripcionPoliza() {
        return cDescripcionPoliza;
    }

    public String getU_LOGIN() {
        return U_LOGIN;
    }

    public String getcDocumentohAplicado() {
        return cDocumentohAplicado;
    }

    public String getcMotivoRechazo() {
        return cMotivoRechazo;
    }

    public String getcUnidadResponsableContable() {
        return cUnidadResponsableContable;
    }

    public int getnFolioPolizaCancelacion() {
        return nFolioPolizaCancelacion;
    }

    public String getfCancelacion() {
        return fCancelacion;
    }

    public String getaEjercicioFiscal() {
        return aEjercicioFiscal;
    }

    public String getcRamo() {
        return cRamo;
    }

    public String getcUnidadEjecutora() {
        return cUnidadEjecutora;
    }

    public String getcIdUsuarioCaptura() {
        return cIdUsuarioCaptura;
    }

    public int getID_CASO() {
        return ID_CASO;
    }

    public double getmMontoSolicitud() {
        return mMontoSolicitud;
    }

    public String getcEsFirmaElectronica() {
        return cEsFirmaElectronica;
    }

    public int getnNumEmpleadoVoBo() {
        return nNumEmpleadoVoBo;
    }

    public int getnNumEmpleadoAut() {
        return nNumEmpleadoAut;
    }

    public int getnNumEmpleadoElab() {
        return nNumEmpleadoElab;
    }

    public int getnEnviadoSICOP() {
        return nEnviadoSICOP;
    }
}
