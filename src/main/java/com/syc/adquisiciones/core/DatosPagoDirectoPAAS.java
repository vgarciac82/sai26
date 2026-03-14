package com.syc.adquisiciones.core;

import java.util.Base64;

public class DatosPagoDirectoPAAS {

    private int nCapitulo;

    private String cPartida;

    private String cAlmacenEntrega;

    private String fFechaInicio;

    private String fFechaFin;

    private int nTipoAdjudicacion;

    private int nFundamentoLegal;

    private int nFolioPago;

    private String cEjercicio;

    private String cCabm;

    private String cIdUnidadEjecutora;

    private int nCantidadTotal;

    private double mPUpromedio;

    private int nIdIVA;

    private int nValorIVA;

    // Para eliminar lineas
    private int nLinea;

    public int getnCapitulo() {
        return nCapitulo;
    }

    public void setnCapitulo(int nCapitulo) {
        this.nCapitulo = nCapitulo;
    }

    public String getcPartida() {
        return cPartida;
    }

    public void setcPartida(String cPartida) {
        this.cPartida = cPartida;
    }

    public String getcAlmacenEntrega() {
        return cAlmacenEntrega;
    }

    public void setcAlmacenEntrega(String cAlmacenEntrega) {
        this.cAlmacenEntrega = cAlmacenEntrega;
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

    public int getnTipoAdjudicacion() {
        return nTipoAdjudicacion;
    }

    public void setnTipoAdjudicacion(int nTipoAdjudicacion) {
        this.nTipoAdjudicacion = nTipoAdjudicacion;
    }

    public int getnFundamentoLegal() {
        return nFundamentoLegal;
    }

    public void setnFundamentoLegal(int nFundamentoLegal) {
        this.nFundamentoLegal = nFundamentoLegal;
    }

    public int getnFolioPago() {
        return nFolioPago;
    }

    public void setnFolioPago(int nFolioPago) {
        this.nFolioPago = nFolioPago;
    }

    public String getcEjercicio() {
        return cEjercicio;
    }

    public void setcEjercicio(String cEjercicio) {
        this.cEjercicio = cEjercicio;
    }

    public String getcIdUnidadEjecutora() {
        return cIdUnidadEjecutora;
    }

    public void setcIdUnidadEjecutora(String cIdUnidadEjecutora) {
        this.cIdUnidadEjecutora = cIdUnidadEjecutora;
    }

    public String getcCabm() {
        return cCabm;
    }

    public void setcCabm(String cCabm) {
        this.cCabm = cCabm;
    }

    public int getnCantidadTotal() {
        return nCantidadTotal;
    }

    public void setnCantidadTotal(int nCantidadTotal) {
        this.nCantidadTotal = nCantidadTotal;
    }

    public double getmPUpromedio() {
        return mPUpromedio;
    }

    public void setmPUpromedio(double mPUpromedio) {
        this.mPUpromedio = mPUpromedio;
    }

    public int getnIdIVA() {
        return nIdIVA;
    }

    public void setnIdIVA(int nIdIVA) {
        this.nIdIVA = nIdIVA;
    }

    public int getnValorIVA() {
        return nValorIVA;
    }

    public void setnValorIVA(int nValorIVA) {
        this.nValorIVA = nValorIVA;
    }

    public int getnLinea() {
        return nLinea;
    }

    public void setnLinea(int nLinea) {
        this.nLinea = nLinea;
    }
}
