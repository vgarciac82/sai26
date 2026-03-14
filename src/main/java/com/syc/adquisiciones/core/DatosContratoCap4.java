package com.syc.adquisiciones.core;

import java.util.ArrayList;
import java.util.Base64;

public class DatosContratoCap4 {

    private String cIdcontratoDefinitivo;

    private String cIdUnidadEjecutora;

    private int cActividadEconomica;

    private int nEsPlurianual;

    private int nEsAbierto;

    private int nEsDescentralizado;

    private int nIdAmpliacion;

    private int nIdCategoria;

    private int nIdFundamentoLeg;

    private String cDescripcion;

    private String cNoContCNET;

    private String cNoProcedimientoCNET;

    private String nCondContratoCNET;

    private String nCondExpedienteCNET;

    private String cOficioDG;

    private String cFolioMASCP;

    private String cEjercicio;

    private String cIdRFC;

    private String cCuentaDisponible;

    private double totalPlurianual;

    private ArrayList<FechasContratacion> arrayFechas;

    private ArrayList<PartidasContratoCap4> arrayPartidas;

    private String descripPoliza;

    private int nComprometeMax;

    public String getcIdcontratoDefinitivo() {
        return cIdcontratoDefinitivo;
    }

    public void setcIdcontratoDefinitivo(String cIdcontratoDefinitivo) {
        this.cIdcontratoDefinitivo = cIdcontratoDefinitivo;
    }

    public String getcIdUnidadEjecutora() {
        return cIdUnidadEjecutora;
    }

    public void setcIdUnidadEjecutora(String cIdUnidadEjecutora) {
        this.cIdUnidadEjecutora = cIdUnidadEjecutora;
    }

    public int getcActividadEconomica() {
        return cActividadEconomica;
    }

    public void setcActividadEconomica(int cActividadEconomica) {
        this.cActividadEconomica = cActividadEconomica;
    }

    public int getnEsPlurianual() {
        return nEsPlurianual;
    }

    public void setnEsPlurianual(int nEsPlurianual) {
        this.nEsPlurianual = nEsPlurianual;
    }

    public int getnEsAbierto() {
        return nEsAbierto;
    }

    public void setnEsAbierto(int nEsAbierto) {
        this.nEsAbierto = nEsAbierto;
    }

    public int getnEsDescentralizado() {
        return nEsDescentralizado;
    }

    public void setnEsDescentralizado(int nEsDescentralizado) {
        this.nEsDescentralizado = nEsDescentralizado;
    }

    public int getnIdAmpliacion() {
        return nIdAmpliacion;
    }

    public void setnIdAmpliacion(int nIdAmpliacion) {
        this.nIdAmpliacion = nIdAmpliacion;
    }

    public int getnIdCategoria() {
        return nIdCategoria;
    }

    public void setnIdCategoria(int nIdCategoria) {
        this.nIdCategoria = nIdCategoria;
    }

    public int getnIdFundamentoLeg() {
        return nIdFundamentoLeg;
    }

    public void setnIdFundamentoLeg(int nIdFundamentoLeg) {
        this.nIdFundamentoLeg = nIdFundamentoLeg;
    }

    public String getcDescripcion() {
        return cDescripcion;
    }

    public void setcDescripcion(String cDescripcion) {
        this.cDescripcion = cDescripcion;
    }

    public String getcNoContCNET() {
        return cNoContCNET;
    }

    public void setcNoContCNET(String cNoContCNET) {
        this.cNoContCNET = cNoContCNET;
    }

    public String getcNoProcedimientoCNET() {
        return cNoProcedimientoCNET;
    }

    public void setcNoProcedimientoCNET(String cNoProcedimientoCNET) {
        this.cNoProcedimientoCNET = cNoProcedimientoCNET;
    }

    public String getnCondContratoCNET() {
        return nCondContratoCNET;
    }

    public void setnCondContratoCNET(String nCondContratoCNET) {
        this.nCondContratoCNET = nCondContratoCNET;
    }

    public String getnCondExpedienteCNET() {
        return nCondExpedienteCNET;
    }

    public void setnCondExpedienteCNET(String nCondExpedienteCNET) {
        this.nCondExpedienteCNET = nCondExpedienteCNET;
    }

    public String getcOficioDG() {
        return cOficioDG;
    }

    public void setcOficioDG(String cOficioDG) {
        this.cOficioDG = cOficioDG;
    }

    public String getcFolioMASCP() {
        return cFolioMASCP;
    }

    public void setcFolioMASCP(String cFolioMASCP) {
        this.cFolioMASCP = cFolioMASCP;
    }

    public String getcEjercicio() {
        return cEjercicio;
    }

    public void setcEjercicio(String cEjercicio) {
        this.cEjercicio = cEjercicio;
    }

    public String getcIdRFC() {
        return cIdRFC;
    }

    public void setcIdRFC(String cIdRFC) {
        this.cIdRFC = cIdRFC;
    }

    public String getcCuentaDisponible() {
        return cCuentaDisponible;
    }

    public void setcCuentaDisponible(String cCuentaDisponible) {
        this.cCuentaDisponible = cCuentaDisponible;
    }

    public double getTotalPlurianual() {
        return totalPlurianual;
    }

    public void setTotalPlurianual(double totalPlurianual) {
        this.totalPlurianual = totalPlurianual;
    }

    public ArrayList<FechasContratacion> getArrayFechas() {
        return arrayFechas;
    }

    public void setArrayFechas(ArrayList<FechasContratacion> arrayFechas) {
        this.arrayFechas = arrayFechas;
    }

    public ArrayList<PartidasContratoCap4> getArrayPartidas() {
        return arrayPartidas;
    }

    public void setArrayPartidas(ArrayList<PartidasContratoCap4> arrayPartidas) {
        this.arrayPartidas = arrayPartidas;
    }

    public String getDescripPoliza() {
        return descripPoliza;
    }

    public void setDescripPoliza(String descripPoliza) {
        this.descripPoliza = descripPoliza;
    }

    public int getnComprometeMax() {
        return nComprometeMax;
    }

    public void setnComprometeMax(int nComprometeMax) {
        this.nComprometeMax = nComprometeMax;
    }
}
