package com.syc.registroingresos;

import java.util.Base64;

public class RegistrosIngresosEncabezado {

    private int nFolioRegistroIngreso;

    private String nTipoIngreso;

    // se refiere al tipo de ingreso : Ingresos Propios, Ingresos Fiscales.
    private String cTipoIngreso;

    //fecha
    private String fCaptura;

    //fecha
    private String fAplicacion;

    private String cRamo;

    private String cUnidadResponsable;

    private String cDocumentoHaplicado;

    private int nFolioPoliza;

    private String cTipoPoliza;

    private String cRevisado;

    private String aEjercicioFiscal;

    private String cConcepto;

    private String cUnidadResponsableContable;

    private int nFolioPolizaCancelacion;

    //fecha
    private String fCancelacion;

    private String cDescripcionPoliza;

    private String nFolioSicop;

    private double mImporte;

    private String cCentroContable;

    private int nApartado;

    private String cPrograma;

    private String cOrigen;

    // Folio apartado para Recurso Fiscal
    private int nFolioApartado;

    private String caNoContrarrecibo;

    public int getnFolioRegistroIngreso() {
        return nFolioRegistroIngreso;
    }

    public void setnFolioRegistroIngreso(int nFolioRegistroIngreso) {
        this.nFolioRegistroIngreso = nFolioRegistroIngreso;
    }

    public int getnFolioApartado() {
        return nFolioApartado;
    }

    public void setnFolioApartado(int nFolioApartado) {
        this.nFolioApartado = nFolioApartado;
    }

    public String getcaNoContrarrecibo() {
        return caNoContrarrecibo;
    }

    public void setcaNoContrarrecibo(String caNoContrarrecibo) {
        this.caNoContrarrecibo = caNoContrarrecibo;
    }

    public String getnTipoIngreso() {
        return nTipoIngreso;
    }

    public void setnTipoIngreso(String nTipoIngreso) {
        this.nTipoIngreso = nTipoIngreso;
    }

    public String getcTipoIngreso() {
        return cTipoIngreso;
    }

    public void setcTipoIngreso(String cTipoIngreso) {
        this.cTipoIngreso = cTipoIngreso;
    }

    public String getfCaptura() {
        return fCaptura;
    }

    public void setfCaptura(String fCaptura) {
        this.fCaptura = fCaptura;
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

    public String getcConcepto() {
        return cConcepto;
    }

    public void setcConcepto(String cConcepto) {
        this.cConcepto = cConcepto;
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

    public String getfCancelacion() {
        return fCancelacion;
    }

    public void setfCancelacion(String fCancelacion) {
        this.fCancelacion = fCancelacion;
    }

    public String getcDescripcionPoliza() {
        return cDescripcionPoliza;
    }

    public void setcDescripcionPoliza(String cDescripcionPoliza) {
        this.cDescripcionPoliza = cDescripcionPoliza;
    }

    public String getnFolioSicop() {
        return nFolioSicop;
    }

    public void setnFolioSicop(String nFolioSicop) {
        this.nFolioSicop = nFolioSicop;
    }

    public double getmImporte() {
        return mImporte;
    }

    public void setmImporte(double mImporte) {
        this.mImporte = mImporte;
    }

    public String getcCentroContable() {
        return cCentroContable;
    }

    public void setcCentroContable(String cCentroContable) {
        this.cCentroContable = cCentroContable;
    }

    public int getnApartado() {
        return nApartado;
    }

    public void setnApartado(int nApartado) {
        this.nApartado = nApartado;
    }

    public String getcPrograma() {
        return cPrograma;
    }

    public void setcPrograma(String cPrograma) {
        this.cPrograma = cPrograma;
    }

    public String getcOrigen() {
        return cOrigen;
    }

    public void setcOrigen(String cOrigen) {
        this.cOrigen = cOrigen;
    }
}
