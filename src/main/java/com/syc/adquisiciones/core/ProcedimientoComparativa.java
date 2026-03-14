package com.syc.adquisiciones.core;

import java.io.Serializable;
import java.util.Base64;

public class ProcedimientoComparativa implements Serializable {

    private static final long serialVersionUID = -7740203864511747270L;

    private String cEjercicio = null;

    private String cIdTipoProcedimiento = null;

    private String cIdUnidadEjecutora = null;

    private int nIdConsecutivo = 0;

    private String cIdRFC = null;

    private String cIdTipoConsolidado = null;

    private int nIdConsecutivoConsolidado = 0;

    private int nIdLineaConsolidado = 0;

    private String montoMinimoTexto = null;

    private float montoMinimo = 0;

    private float montoMax = 0;

    private float iva = 0;

    private String cIdProcedimiento = null;

    private String cIdConsolidado = null;

    private String cDescripcion = null;

    private int cGanador = 0;

    private String cEvaluacionTecnica = null;

    private int cEvaluacion = 0;

    private int ganadorSug = 0;

    private String Observaciones = null;

    private String razonSocial = null;

    private int cantidad = 0;

    private String firmante = null;

    private String categoria = null;

    private String fecha = null;

    private float tipoCambio = 0;

    private float montoMaxBruto = 0;

    private int numeroRfc = 0;

    public ProcedimientoComparativa() {
        super();
    }

    public String getEjercicio() {
        return cEjercicio;
    }

    public void setEjercicio(String Ejercicio) {
        this.cEjercicio = Ejercicio;
    }

    public String getTipoProcedimiento() {
        return cIdTipoProcedimiento;
    }

    public void setTipoProcedimiento(String TipoProcedimiento) {
        this.cIdTipoProcedimiento = TipoProcedimiento;
    }

    public String getUnidadEjecutora() {
        return cIdUnidadEjecutora;
    }

    public void setUnidadEjecutora(String UnidadEjecutora) {
        this.cIdUnidadEjecutora = UnidadEjecutora;
    }

    public int getConsecutivo() {
        return nIdConsecutivo;
    }

    public void setConsecutivo(int Consecutivo) {
        this.nIdConsecutivo = Consecutivo;
    }

    public String getIdRFC() {
        return cIdRFC;
    }

    public void setIdRFC(String IdRFC) {
        this.cIdRFC = IdRFC;
    }

    public String getTipoConsolidado() {
        return cIdTipoConsolidado;
    }

    public void setTipoConsolidado(String TipoConsolidado) {
        this.cIdTipoConsolidado = TipoConsolidado;
    }

    public int getConsecutivoConsolidado() {
        return nIdConsecutivoConsolidado;
    }

    public void setConsecutivoConsolidado(int ConsecutivoConsolidado) {
        this.nIdConsecutivoConsolidado = ConsecutivoConsolidado;
    }

    public int getLineaConsolidado() {
        return nIdLineaConsolidado;
    }

    public void setLineaConsolidado(int LineaConsolidado) {
        this.nIdLineaConsolidado = LineaConsolidado;
    }

    public float getmontoMinimo() {
        return montoMinimo;
    }

    public void setmontoMinimo(float montoMin) {
        this.montoMinimo = montoMin;
    }

    public String getmontoMinimoTexto() {
        return montoMinimoTexto;
    }

    public void setmontoMinimoTexto(String montoMinTexto) {
        this.montoMinimoTexto = montoMinTexto;
    }

    public float getmontoMaximo() {
        return montoMax;
    }

    public void setmontoMaximo(float montoMaximo) {
        this.montoMax = montoMaximo;
    }

    public float getmIva() {
        return iva;
    }

    public void setmIva(float mIva) {
        this.iva = mIva;
    }

    public String getProcedimiento() {
        return cIdProcedimiento;
    }

    public void setProcedimiento(String Procedimiento) {
        this.cIdProcedimiento = Procedimiento;
    }

    public String getConsolidado() {
        return cIdConsolidado;
    }

    public void setConsolidado(String Consolidado) {
        this.cIdConsolidado = Consolidado;
    }

    public String getDescripcion() {
        return cDescripcion;
    }

    public void setDescripcion(String Descripcion) {
        this.cDescripcion = Descripcion;
    }

    public int getGanador() {
        return cGanador;
    }

    public void setGanador(int Ganador) {
        this.cGanador = Ganador;
    }

    public String getEvaluacionTecnica() {
        return cEvaluacionTecnica;
    }

    public void setEvaluacionTecnica(String EvaluacionTecnica) {
        this.cEvaluacionTecnica = EvaluacionTecnica;
    }

    public int getEvaluacion() {
        return cEvaluacion;
    }

    public void setEvaluacion(int Evaluacion) {
        this.cEvaluacion = Evaluacion;
    }

    public int getcganadorSug() {
        return ganadorSug;
    }

    public void setcganadorSug(int cganadorSug) {
        this.ganadorSug = cganadorSug;
    }

    public String getcObservaciones() {
        return Observaciones;
    }

    public void setcObservaciones(String cObservaciones) {
        this.Observaciones = cObservaciones;
    }

    public String getcrazonSocial() {
        return razonSocial;
    }

    public void setcrazonSocial(String crazonSocial) {
        this.razonSocial = crazonSocial;
    }

    public int getNumRfc() {
        return numeroRfc;
    }

    public void setNumRfc(int NumRfc) {
        this.numeroRfc = NumRfc;
    }

    public String getcFirmante() {
        return firmante;
    }

    public void setcFirmante(String cFirmante) {
        this.firmante = cFirmante;
    }

    public String getcCategoria() {
        return categoria;
    }

    public void setcCategoria(String cCategoria) {
        this.categoria = cCategoria;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String cFecha) {
        this.fecha = cFecha;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int Cantidad) {
        this.cantidad = Cantidad;
    }

    public void setcTipoCambio(float cTipoCambio) {
        this.tipoCambio = cTipoCambio;
    }

    public float getcTipoCambio() {
        return tipoCambio;
    }

    public float getmontoMaximoBruto() {
        return montoMaxBruto;
    }

    public void setmontoMaximoBruto(float montoMaximoBruto) {
        this.montoMaxBruto = montoMaximoBruto;
    }
}
