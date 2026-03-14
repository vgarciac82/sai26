package com.axtel.contratos.core;

import java.util.ArrayList;
import java.util.Base64;

public class Contrato {

    private String cEjercicio;

    private String cIdtipoContrato;

    private String cIdUnidadEjecutora;

    private int nIdConsecutivo;

    private String cIdtipoProcedimiento;

    private int nIdConsecutivoProcedimiento;

    private String cConceptoContrato;

    private int nIdEstado;

    private String cIdTipoCambio;

    private double mTipoCambio;

    private String cIdContratoDefinitivo;

    private String cIdContrato;

    private String fFallo;

    private String fInicio;

    private String fFin;

    private String cIdEntidadContable;

    private String cIdProcedimiento;

    private int nIVA;

    private String cIdUsuarioCreacion;

    private String fFormalizacion;

    private String cFOLIO;

    private int nConsecutivoCDIV;

    private String C_FOLIO_PRE;

    private int nConsecutivoPRECOMP;

    private int esDescentralizado;

    private String fPropuestas;

    private String fSolicitud;

    private String fEntrega;

    private String cNoContratoCNET;

    private String fCreacion;

    private String cNumCuentaDisp;

    private String cMotivoCancelacion;

    private String cOficioDG;

    private String cFolioMASCP;

    private String nCodExpedienteCNET;

    private String nCodContratoCNET;

    private int lJustificaTipoProced;

    private String cComentarioJustificaTipoProced;

    private int lArchivoContCargado;

    private int nIdconsecutivoAdj;

    private int nCategoriaProcedimiento;

    private boolean nEsContratacionPSP;

    private double mTotalGarantia;

    private double mGarantiaAnticipo;

    private double mGarantiaCumplimiento;

    private String cMecanismosVigilancia;

    private DatosContratoPSP datPSP;

    private ProcedimientoAdjudicacion procedAdj;

    private int ITieneAnticipo;

    private String jndiName;

    private String prefixPath;

    private int lExcentaGarantia;

    private String cIdRFC;

    private ArrayList<OtrosImpuestos> otrosImp;

    private String cEvento;

    private String cIdRequi;

    public String getcEjercicio() {
        return cEjercicio;
    }

    public void setcEjercicio(String cEjercicio) {
        this.cEjercicio = cEjercicio;
    }

    public String getcIdtipoContrato() {
        return cIdtipoContrato;
    }

    public void setcIdtipoContrato(String cIdtipoContrato) {
        this.cIdtipoContrato = cIdtipoContrato;
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

    public String getcIdtipoProcedimiento() {
        return cIdtipoProcedimiento;
    }

    public void setcIdtipoProcedimiento(String cIdtipoProcedimiento) {
        this.cIdtipoProcedimiento = cIdtipoProcedimiento;
    }

    public int getnIdConsecutivoProcedimiento() {
        return nIdConsecutivoProcedimiento;
    }

    public void setnIdConsecutivoProcedimiento(int nIdConsecutivoProcedimiento) {
        this.nIdConsecutivoProcedimiento = nIdConsecutivoProcedimiento;
    }

    public String getcConceptoContrato() {
        return cConceptoContrato;
    }

    public void setcConceptoContrato(String cConceptoContrato) {
        this.cConceptoContrato = cConceptoContrato;
    }

    public int getnIdEstado() {
        return nIdEstado;
    }

    public void setnIdEstado(int nIdEstado) {
        this.nIdEstado = nIdEstado;
    }

    public String getcIdTipoCambio() {
        return cIdTipoCambio;
    }

    public void setcIdTipoCambio(String cIdTipoCambio) {
        this.cIdTipoCambio = cIdTipoCambio;
    }

    public double getmTipoCambio() {
        return mTipoCambio;
    }

    public void setmTipoCambio(double mTipoCambio) {
        this.mTipoCambio = mTipoCambio;
    }

    public String getcIdContratoDefinitivo() {
        return cIdContratoDefinitivo;
    }

    public void setcIdContratoDefinitivo(String cIdContratoDefinitivo) {
        this.cIdContratoDefinitivo = cIdContratoDefinitivo;
    }

    public String getfFallo() {
        return fFallo;
    }

    public void setfFallo(String fFallo) {
        this.fFallo = fFallo;
    }

    public String getfInicio() {
        return fInicio;
    }

    public void setfInicio(String fInicio) {
        this.fInicio = fInicio;
    }

    public String getfFin() {
        return fFin;
    }

    public void setfFin(String fFin) {
        this.fFin = fFin;
    }

    public String getcIdEntidadContable() {
        return cIdEntidadContable;
    }

    public void setcIdEntidadContable(String cIdEntidadContable) {
        this.cIdEntidadContable = cIdEntidadContable;
    }

    public String getcIdProcedimiento() {
        return cIdProcedimiento;
    }

    public void setcIdProcedimiento(String cIdProcedimiento) {
        this.cIdProcedimiento = cIdProcedimiento;
    }

    public int getnIVA() {
        return nIVA;
    }

    public void setnIVA(int nIVA) {
        this.nIVA = nIVA;
    }

    public String getcIdUsuarioCreacion() {
        return cIdUsuarioCreacion;
    }

    public void setcIdUsuarioCreacion(String cIdUsuarioCreacion) {
        this.cIdUsuarioCreacion = cIdUsuarioCreacion;
    }

    public String getfFormalizacion() {
        return fFormalizacion;
    }

    public void setfFormalizacion(String fFormalizacion) {
        this.fFormalizacion = fFormalizacion;
    }

    public String getcFOLIO() {
        return cFOLIO;
    }

    public void setcFOLIO(String cFOLIO) {
        this.cFOLIO = cFOLIO;
    }

    public int getnConsecutivoCDIV() {
        return nConsecutivoCDIV;
    }

    public void setnConsecutivoCDIV(int nConsecutivoCDIV) {
        this.nConsecutivoCDIV = nConsecutivoCDIV;
    }

    public String getC_FOLIO_PRE() {
        return C_FOLIO_PRE;
    }

    public void setC_FOLIO_PRE(String c_FOLIO_PRE) {
        C_FOLIO_PRE = c_FOLIO_PRE;
    }

    public int getnConsecutivoPRECOMP() {
        return nConsecutivoPRECOMP;
    }

    public void setnConsecutivoPRECOMP(int nConsecutivoPRECOMP) {
        this.nConsecutivoPRECOMP = nConsecutivoPRECOMP;
    }

    public int getEsDescentralizado() {
        return esDescentralizado;
    }

    public void setEsDescentralizado(int esDescentralizado) {
        this.esDescentralizado = esDescentralizado;
    }

    public String getfPropuestas() {
        return fPropuestas;
    }

    public void setfPropuestas(String fPropuestas) {
        this.fPropuestas = fPropuestas;
    }

    public String getfSolicitud() {
        return fSolicitud;
    }

    public void setfSolicitud(String fSolicitud) {
        this.fSolicitud = fSolicitud;
    }

    public String getfEntrega() {
        return fEntrega;
    }

    public void setfEntrega(String fEntrega) {
        this.fEntrega = fEntrega;
    }

    public String getcNoContratoCNET() {
        return cNoContratoCNET;
    }

    public void setcNoContratoCNET(String cNoContratoCNET) {
        this.cNoContratoCNET = cNoContratoCNET;
    }

    public String getfCreacion() {
        return fCreacion;
    }

    public void setfCreacion(String fCreacion) {
        this.fCreacion = fCreacion;
    }

    public String getcNumCuentaDisp() {
        return cNumCuentaDisp;
    }

    public void setcNumCuentaDisp(String cNumCuentaDisp) {
        this.cNumCuentaDisp = cNumCuentaDisp;
    }

    public String getcMotivoCancelacion() {
        return cMotivoCancelacion;
    }

    public void setcMotivoCancelacion(String cMotivoCancelacion) {
        this.cMotivoCancelacion = cMotivoCancelacion;
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

    public String getnCodExpedienteCNET() {
        return nCodExpedienteCNET;
    }

    public void setnCodExpedienteCNET(String nCodExpedienteCNET) {
        this.nCodExpedienteCNET = nCodExpedienteCNET;
    }

    public String getnCodContratoCNET() {
        return nCodContratoCNET;
    }

    public void setnCodContratoCNET(String nCodContratoCNET) {
        this.nCodContratoCNET = nCodContratoCNET;
    }

    public int getlJustificaTipoProced() {
        return lJustificaTipoProced;
    }

    public void setlJustificaTipoProced(int lJustificaTipoProced) {
        this.lJustificaTipoProced = lJustificaTipoProced;
    }

    public String getcComentarioJustificaTipoProced() {
        return cComentarioJustificaTipoProced;
    }

    public void setcComentarioJustificaTipoProced(String cComentarioJustificaTipoProced) {
        this.cComentarioJustificaTipoProced = cComentarioJustificaTipoProced;
    }

    public int getlArchivoContCargado() {
        return lArchivoContCargado;
    }

    public void setlArchivoContCargado(int lArchivoContCargado) {
        this.lArchivoContCargado = lArchivoContCargado;
    }

    public int getnIdconsecutivoAdj() {
        return nIdconsecutivoAdj;
    }

    public void setnIdconsecutivoAdj(int nIdconsecutivoAdj) {
        this.nIdconsecutivoAdj = nIdconsecutivoAdj;
    }

    public int getnCategoriaProcedimiento() {
        return nCategoriaProcedimiento;
    }

    public void setnCategoriaProcedimiento(int nCategoriaProcedimiento) {
        this.nCategoriaProcedimiento = nCategoriaProcedimiento;
    }

    public boolean isnEsContratacionPSP() {
        return nEsContratacionPSP;
    }

    public void setnEsContratacionPSP(boolean nEsContratacionPSP) {
        this.nEsContratacionPSP = nEsContratacionPSP;
    }

    public double getmTotalGarantia() {
        return mTotalGarantia;
    }

    public void setmTotalGarantia(double mTotalGarantia) {
        this.mTotalGarantia = mTotalGarantia;
    }

    public double getmGarantiaAnticipo() {
        return mGarantiaAnticipo;
    }

    public void setmGarantiaAnticipo(double mGarantiaAnticipo) {
        this.mGarantiaAnticipo = mGarantiaAnticipo;
    }

    public double getmGarantiaCumplimiento() {
        return mGarantiaCumplimiento;
    }

    public void setmGarantiaCumplimiento(double mGarantiaCumplimiento) {
        this.mGarantiaCumplimiento = mGarantiaCumplimiento;
    }

    public String getcMecanismosVigilancia() {
        return cMecanismosVigilancia;
    }

    public void setcMecanismosVigilancia(String cMecanismosVigilancia) {
        this.cMecanismosVigilancia = cMecanismosVigilancia;
    }

    public DatosContratoPSP getDatPSP() {
        return datPSP;
    }

    public void setDatPSP(DatosContratoPSP datPSP) {
        this.datPSP = datPSP;
    }

    public ProcedimientoAdjudicacion getProcedAdj() {
        return procedAdj;
    }

    public void setProcedAdj(ProcedimientoAdjudicacion procedAdj) {
        this.procedAdj = procedAdj;
    }

    public int getITieneAnticipo() {
        return ITieneAnticipo;
    }

    public void setITieneAnticipo(int iTieneAnticipo) {
        ITieneAnticipo = iTieneAnticipo;
    }

    public String getJndiName() {
        return jndiName;
    }

    public void setJndiName(String jndiName) {
        this.jndiName = jndiName;
    }

    public String getPrefixPath() {
        return prefixPath;
    }

    public void setPrefixPath(String prefixPath) {
        this.prefixPath = prefixPath;
    }

    public int getlExcentaGarantia() {
        return lExcentaGarantia;
    }

    public void setlExcentaGarantia(int lExcentaGarantia) {
        this.lExcentaGarantia = lExcentaGarantia;
    }

    public ArrayList<OtrosImpuestos> getOtrosImp() {
        return otrosImp;
    }

    public void setOtrosImp(ArrayList<OtrosImpuestos> otrosImp) {
        this.otrosImp = otrosImp;
    }

    public String getcIdRFC() {
        return cIdRFC;
    }

    public void setcIdRFC(String cIdRFC) {
        this.cIdRFC = cIdRFC;
    }

    public String getcIdContrato() {
        return cIdContrato;
    }

    public void setcIdContrato(String cIdContrato) {
        this.cIdContrato = cIdContrato;
    }

    public String getcEvento() {
        return cEvento;
    }

    public void setcEvento(String cEvento) {
        this.cEvento = cEvento;
    }

    public String getcIdRequi() {
        return cIdRequi;
    }

    public void setcIdRequi(String cIdRequi) {
        this.cIdRequi = cIdRequi;
    }
}
