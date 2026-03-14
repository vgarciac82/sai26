package com.syc.adquisiciones.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Base64;

public class DatosProcedimiento {

    int nConsecutivoProcedimiento;

    String cTipoProcedimiento;

    String cUnidadEjecutora;

    int nCategoriaProcedimiento;

    int nFundamentoLegal;

    int nCategoriaProcedimientoNuevo;

    int nFundamentoLegalNuevo;

    String cIdProcedimiento;

    String cIdRFC;

    String cIdContratoDef;

    double mMontoNetoContrato;

    ArrayList<List<String>> fechas;

    public int getnConsecutivoProcedimiento() {
        return nConsecutivoProcedimiento;
    }

    public void setnConsecutivoProcedimiento(int nConsecutivoProcedimiento) {
        this.nConsecutivoProcedimiento = nConsecutivoProcedimiento;
    }

    public String getcTipoProcedimiento() {
        return cTipoProcedimiento;
    }

    public void setcTipoProcedimiento(String cTipoProcedimiento) {
        this.cTipoProcedimiento = cTipoProcedimiento;
    }

    public String getcUnidadEjecutora() {
        return cUnidadEjecutora;
    }

    public void setcUnidadEjecutora(String cUnidadEjecutora) {
        this.cUnidadEjecutora = cUnidadEjecutora;
    }

    public int getnCategoriaProcedimiento() {
        return nCategoriaProcedimiento;
    }

    public void setnCategoriaProcedimiento(int nCategoriaProcedimiento) {
        this.nCategoriaProcedimiento = nCategoriaProcedimiento;
    }

    public int getnFundamentoLegal() {
        return nFundamentoLegal;
    }

    public void setnFundamentoLegal(int nFundamentoLegal) {
        this.nFundamentoLegal = nFundamentoLegal;
    }

    public String getcIdProcedimiento() {
        return cIdProcedimiento;
    }

    public void setcIdProcedimiento(String cIdProcedimiento) {
        this.cIdProcedimiento = cIdProcedimiento;
    }

    public ArrayList<List<String>> getFechas() {
        return fechas;
    }

    public void setFechas(ArrayList<List<String>> fechas) {
        this.fechas = fechas;
    }

    public int getnCategoriaProcedimientoNuevo() {
        return nCategoriaProcedimientoNuevo;
    }

    public void setnCategoriaProcedimientoNuevo(int nCategoriaProcedimientoNuevo) {
        this.nCategoriaProcedimientoNuevo = nCategoriaProcedimientoNuevo;
    }

    public int getnFundamentoLegalNuevo() {
        return nFundamentoLegalNuevo;
    }

    public void setnFundamentoLegalNuevo(int nFundamentoLegalNuevo) {
        this.nFundamentoLegalNuevo = nFundamentoLegalNuevo;
    }

    public String getcIdRFC() {
        return cIdRFC;
    }

    public void setcIdRFC(String cIdRFC) {
        this.cIdRFC = cIdRFC;
    }

    public String getcIdContratoDef() {
        return cIdContratoDef;
    }

    public void setcIdContratoDef(String cIdContratoDef) {
        this.cIdContratoDef = cIdContratoDef;
    }

    public double getmMontoNetoContrato() {
        return mMontoNetoContrato;
    }

    public void setmMontoNetoContrato(double mMontoNetoContrato) {
        this.mMontoNetoContrato = mMontoNetoContrato;
    }
}
