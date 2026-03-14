package com.axtel.contratos;

import java.util.ArrayList;
import java.util.Base64;

/**
 * @author hfariasr
 */
public class Procedimiento {

    private String cEjercicio;

    private String cIdTipoProcedimiento;

    private String cIdUnidadEjecutora;

    private int nIdConsecutivo;

    private String cIdTipoConsolidado;

    private int nIdConsecutivoConsolidado;

    private int nIdCategoria;

    private String cDescripcion;

    private int nIdEstado;

    private String cIdProcedimiento;

    private String cIdConsolidado;

    private int nIdFundamentoLeg;

    private int nEsPlurianual;

    private String cIdRFC;

    private String cNoProcedCNET;

    private String cUsuarioCrea;

    private String cIdEntidadContable;

    private int nEsContratoAbierto;

    private int nPorcentajeIVA;

    private double mTotalMinimoHonorarios;

    private double mTotalMinimoGastosTraslado;

    private double mTotalMaximoHonorarios;

    private double mTotalMaximoGastosTraslado;

    private double mTotalPlurianualHonorarios;

    private double mTotalPlurianualGastosTraslado;

    private double mTotalPlurianual;

    private int nComprometeMaximo;

    private int nIdconsecutivoAdj;

    private int nConsecutivoContrato;

    private String cIdTipoContrato;

    private String cIdContrato;

    private String cIdContratoDefinitivo;

    private ArrayList<ProcedimientoFecha> fechasProcedimiento;

    public String getcEjercicio() {
        return cEjercicio;
    }

    public void setcEjercicio(String cEjercicio) {
        this.cEjercicio = cEjercicio;
    }

    public String getcIdTipoProcedimiento() {
        return cIdTipoProcedimiento;
    }

    public void setcIdTipoProcedimiento(String cIdTipoProcedimiento) {
        this.cIdTipoProcedimiento = cIdTipoProcedimiento;
    }

    public String getcIdUnidadEjecutora() {
        return cIdUnidadEjecutora;
    }

    public void setcIdUnidadEjecutora(String cIdUnidadEjecutora) {
        this.cIdUnidadEjecutora = cIdUnidadEjecutora;
    }

    public int getnIdConsecutivo() {
        return nIdConsecutivo;
    }

    public void setnIdConsecutivo(int nIdConsecutivo) {
        this.nIdConsecutivo = nIdConsecutivo;
    }

    public String getcIdTipoConsolidado() {
        return cIdTipoConsolidado;
    }

    public void setcIdTipoConsolidado(String cIdTipoConsolidado) {
        this.cIdTipoConsolidado = cIdTipoConsolidado;
    }

    public int getnIdConsecutivoConsolidado() {
        return nIdConsecutivoConsolidado;
    }

    public void setnIdConsecutivoConsolidado(int nIdConsecutivoConsolidado) {
        this.nIdConsecutivoConsolidado = nIdConsecutivoConsolidado;
    }

    public int getnIdCategoria() {
        return nIdCategoria;
    }

    public void setnIdCategoria(int nIdCategoria) {
        this.nIdCategoria = nIdCategoria;
    }

    public String getcDescripcion() {
        return cDescripcion;
    }

    public void setcDescripcion(String cDescripcion) {
        this.cDescripcion = cDescripcion;
    }

    public int getnIdEstado() {
        return nIdEstado;
    }

    public void setnIdEstado(int nIdEstado) {
        this.nIdEstado = nIdEstado;
    }

    public String getcIdProcedimiento() {
        return cIdProcedimiento;
    }

    public void setcIdProcedimiento(String cIdProcedimiento) {
        this.cIdProcedimiento = cIdProcedimiento;
    }

    public String getcIdConsolidado() {
        return cIdConsolidado;
    }

    public void setcIdConsolidado(String cIdConsolidado) {
        this.cIdConsolidado = cIdConsolidado;
    }

    public int getnIdFundamentoLeg() {
        return nIdFundamentoLeg;
    }

    public void setnIdFundamentoLeg(int nIdFundamentoLeg) {
        this.nIdFundamentoLeg = nIdFundamentoLeg;
    }

    public int getnEsPlurianual() {
        return nEsPlurianual;
    }

    public void setnEsPlurianual(int nEsPlurianual) {
        this.nEsPlurianual = nEsPlurianual;
    }

    public String getcIdRFC() {
        return cIdRFC;
    }

    public void setcIdRFC(String cIdRFC) {
        this.cIdRFC = cIdRFC;
    }

    public String getcNoProcedCNET() {
        return cNoProcedCNET;
    }

    public void setcNoProcedCNET(String cNoProcedCNET) {
        this.cNoProcedCNET = cNoProcedCNET;
    }

    public String getcUsuarioCrea() {
        return cUsuarioCrea;
    }

    public void setcUsuarioCrea(String cUsuarioCrea) {
        this.cUsuarioCrea = cUsuarioCrea;
    }

    public String getcIdEntidadContable() {
        return cIdEntidadContable;
    }

    public void setcIdEntidadContable(String cIdEntidadContable) {
        this.cIdEntidadContable = cIdEntidadContable;
    }

    public ArrayList<ProcedimientoFecha> getFechasProcedimiento() {
        return fechasProcedimiento;
    }

    public void setFechasProcedimiento(ArrayList<ProcedimientoFecha> fechasProcedimiento) {
        this.fechasProcedimiento = fechasProcedimiento;
    }

    public int getnEsContratoAbierto() {
        return nEsContratoAbierto;
    }

    public void setnEsContratoAbierto(int nEsContratoAbierto) {
        this.nEsContratoAbierto = nEsContratoAbierto;
    }

    public int getnPorcentajeIVA() {
        return nPorcentajeIVA;
    }

    public void setnPorcentajeIVA(int nPorcentajeIVA) {
        this.nPorcentajeIVA = nPorcentajeIVA;
    }

    public double getmTotalPlurianual() {
        return mTotalPlurianual;
    }

    public void setmTotalPlurianual(double mTotalPlurianual) {
        this.mTotalPlurianual = mTotalPlurianual;
    }

    public double getmTotalMinimoHonorarios() {
        return mTotalMinimoHonorarios;
    }

    public void setmTotalMinimoHonorarios(double mTotalMinimoHonorarios) {
        this.mTotalMinimoHonorarios = mTotalMinimoHonorarios;
    }

    public double getmTotalMinimoGastosTraslado() {
        return mTotalMinimoGastosTraslado;
    }

    public void setmTotalMinimoGastosTraslado(double mTotalMinimoGastosTraslado) {
        this.mTotalMinimoGastosTraslado = mTotalMinimoGastosTraslado;
    }

    public double getmTotalMaximoHonorarios() {
        return mTotalMaximoHonorarios;
    }

    public void setmTotalMaximoHonorarios(double mTotalMaximoHonorarios) {
        this.mTotalMaximoHonorarios = mTotalMaximoHonorarios;
    }

    public double getmTotalMaximoGastosTraslado() {
        return mTotalMaximoGastosTraslado;
    }

    public void setmTotalMaximoGastosTraslado(double mTotalMaximoGastosTraslado) {
        this.mTotalMaximoGastosTraslado = mTotalMaximoGastosTraslado;
    }

    public double getmTotalPlurianualHonorarios() {
        return mTotalPlurianualHonorarios;
    }

    public void setmTotalPlurianualHonorarios(double mTotalPlurianualHonorarios) {
        this.mTotalPlurianualHonorarios = mTotalPlurianualHonorarios;
    }

    public double getmTotalPlurianualGastosTraslado() {
        return mTotalPlurianualGastosTraslado;
    }

    public void setmTotalPlurianualGastosTraslado(double mTotalPlurianualGastosTraslado) {
        this.mTotalPlurianualGastosTraslado = mTotalPlurianualGastosTraslado;
    }

    public int getnComprometeMaximo() {
        return nComprometeMaximo;
    }

    public void setnComprometeMaximo(int nComprometeMaximo) {
        this.nComprometeMaximo = nComprometeMaximo;
    }

    public int getnIdconsecutivoAdj() {
        return nIdconsecutivoAdj;
    }

    public void setnIdconsecutivoAdj(int nIdconsecutivoAdj) {
        this.nIdconsecutivoAdj = nIdconsecutivoAdj;
    }

    public int getnConsecutivoContrato() {
        return nConsecutivoContrato;
    }

    public void setnConsecutivoContrato(int nConsecutivoContrato) {
        this.nConsecutivoContrato = nConsecutivoContrato;
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

    public String getcIdTipoContrato() {
        return cIdTipoContrato;
    }

    public void setcIdTipoContrato(String cIdTipoContrato) {
        this.cIdTipoContrato = cIdTipoContrato;
    }
}
