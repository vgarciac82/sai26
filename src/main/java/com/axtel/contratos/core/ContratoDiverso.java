package com.axtel.contratos.core;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.syc.obrapublica.EjercicioFiscalManager;
import java.util.Base64;

/**
 * Contarto Diverso.
 *
 * @author vicente.garcia
 */
public class ContratoDiverso {

    private boolean abierto = false;

    private boolean aplicaImpuestoCedular = false;

    private String aprobacionPLU = new String("");

    private boolean camInst = false;

    private String caNoCompromiso;

    private String centroContable;

    private String codContratoCNET;

    private String codExpedienteCNET;

    private String conceptoContrato;

    private boolean convEjercicioAnt = false;

    private boolean descentralizado = false;

    private String ejercicioFiscal;

    private boolean eliminaRetencion6IVA;

    private Date fAdjudicacion;

    private Date fContratoFin;

    private Date fContratoIni;

    private Date fContratoPropuestas;

    private Date fContratoSolicitud;

    private Date fDocumento;

    private Date fFirmaContrato;

    private String folioCompromisoSICOP;

    private Date fVigenciaIVA;

    private int idCaso;

    private String idContrato;

    private String idDistritoRiego = "0";

    private String idGEstatal = "0";

    private String idGRegional = "0";

    private int idPrecio = 1;

    private char idSistemaOrigen;

    private int idTipoAdjudicacion;

    private String idTipoContratoDiverso = "3";

    private String idTipoDocumento = "2";

    private String idTipoFondo = "FF";

    private String idTipoLimiteDlls = "00";

    private String idTipoMoneda = "0";

    private String idTipoMontoDesembolso;

    private String idUnidadAdministrativa;

    private String idUsuarioResponsable;

    private BigDecimal mContratoME = new BigDecimal(0.0);

    private BigDecimal mContratoMN;

    private BigDecimal mImporteBruto;

    private BigDecimal mImporteContrato;

    private BigDecimal mImporteHonorarios = new BigDecimal(0.0);

    private BigDecimal mImporteIVA;

    private BigDecimal mImporteTotal;

    private BigDecimal mImporteViaticos = new BigDecimal(0.0);

    private String noOficioRenunciaAnticipo;

    private String noProcedimientoCNET;

    private String origenRM;

    private BigDecimal otrosImpuestos = new BigDecimal(0.0);

    private boolean pasivo = false;

    private String plazo = "0";

    private boolean plurianual = false;

    private double porcImpuestoCedular;

    private double porcIVAAplicable;

    private boolean radicado = false;

    private boolean renunciaAnticipo = false;

    private boolean requiereAnticipo = false;

    private String rfc;

    private boolean saldoAnticipo = false;

    private boolean tieneAnticipo;

    private boolean aplica15D;

    private List<ContratoEP> detalleEP;

    private List<ContratoDiversoRetencion> retenciones;

    private ContratoDiversoAnticipo anticipo;

    /**
     * @return the aprobacionPLU
     */
    public String getAprobacionPLU() {
        return aprobacionPLU;
    }

    /**
     * @return the caNoCompromiso
     */
    public String getCaNoCompromiso() {
        return caNoCompromiso;
    }

    /**
     * @return the centroContable
     */
    public String getCentroContable() {
        return centroContable;
    }

    public String getCodContratoCNET() {
        return codContratoCNET;
    }

    public void setCodContratoCNET(String codContratoCNET) {
        this.codContratoCNET = codContratoCNET;
    }

    /**
     * @return the codExpedienteCNET
     */
    public String getCodExpedienteCNET() {
        return codExpedienteCNET;
    }

    /**
     * @return the conceptoContrato
     */
    public String getConceptoContrato() {
        return conceptoContrato;
    }

    /**
     * @return the ejercicioFiscal
     */
    public String getEjercicioFiscal() {
        return ejercicioFiscal;
    }

    /**
     * @return the fAdjudicacion
     */
    public Date getfAdjudicacion() {
        return fAdjudicacion;
    }

    /**
     * @return the fContratoFin
     */
    public Date getfContratoFin() {
        return fContratoFin;
    }

    /**
     * @return the fContratoIni
     */
    public Date getfContratoIni() {
        return fContratoIni;
    }

    /**
     * @return the fContratoPropuestas
     */
    public Date getfContratoPropuestas() {
        return fContratoPropuestas;
    }

    /**
     * @return the fContratoSolicitud
     */
    public Date getfContratoSolicitud() {
        return fContratoSolicitud;
    }

    /**
     * @return the fDocumento
     */
    public Date getfDocumento() {
        return fDocumento;
    }

    /**
     * @return the fFirmaContrato
     */
    public Date getfFirmaContrato() {
        return fFirmaContrato;
    }

    /**
     * @return the folioCompromisoSICOP
     */
    public String getFolioCompromisoSICOP() {
        return folioCompromisoSICOP;
    }

    /**
     * @return the fVigenciaIVA
     */
    public Date getfVigenciaIVA() {
        return fVigenciaIVA;
    }

    /**
     * @return the idCaso
     */
    public int getIdCaso() {
        return idCaso;
    }

    /**
     * @return the idContrato
     */
    public String getIdContrato() {
        return idContrato;
    }

    /**
     * @return the idDistritoRiego
     */
    public String getIdDistritoRiego() {
        return idDistritoRiego;
    }

    /**
     * @return the idGEstatal
     */
    public String getIdGEstatal() {
        return idGEstatal;
    }

    /**
     * @return the idGRegional
     */
    public String getIdGRegional() {
        return idGRegional;
    }

    /**
     * @return the idPrecio
     */
    public int getIdPrecio() {
        return idPrecio;
    }

    /**
     * @return the idSistemaOrigen
     */
    public char getIdSistemaOrigen() {
        return idSistemaOrigen;
    }

    /**
     * @return the idTipoAdjudicacion
     */
    public int getIdTipoAdjudicacion() {
        return idTipoAdjudicacion;
    }

    /**
     * @return the idTipoContratoDiverso
     */
    public String getIdTipoContratoDiverso() {
        return idTipoContratoDiverso;
    }

    /**
     * @return the idTipoDocumento
     */
    public String getIdTipoDocumento() {
        return idTipoDocumento;
    }

    /**
     * @return the idTipoFondo
     */
    public String getIdTipoFondo() {
        return idTipoFondo;
    }

    /**
     * @return the idTipoLimiteDlls
     */
    public String getIdTipoLimiteDlls() {
        return idTipoLimiteDlls;
    }

    /**
     * @return the idTipoMoneda
     */
    public String getIdTipoMoneda() {
        return idTipoMoneda;
    }

    /**
     * @return the idTipoMontoDesembolso
     */
    public String getIdTipoMontoDesembolso() {
        return idTipoMontoDesembolso;
    }

    /**
     * @return the idUnidadAdministrativa
     */
    public String getIdUnidadAdministrativa() {
        return idUnidadAdministrativa;
    }

    /**
     * @return the idUsuarioResponsable
     */
    public String getIdUsuarioResponsable() {
        return idUsuarioResponsable;
    }

    /**
     * @return the mContratoME
     */
    public BigDecimal getmContratoME() {
        return mContratoME;
    }

    /**
     * @return the mContratoMN
     */
    public BigDecimal getmContratoMN() {
        return mContratoMN;
    }

    /**
     * @return the mImporteBruto
     */
    public BigDecimal getmImporteBruto() {
        return mImporteBruto;
    }

    /**
     * @return the mImporteContrato
     */
    public BigDecimal getmImporteContrato() {
        return mImporteContrato;
    }

    /**
     * @return the mImporteHonorarios
     */
    public BigDecimal getmImporteHonorarios() {
        return mImporteHonorarios;
    }

    /**
     * @return the mImporteIVA
     */
    public BigDecimal getmImporteIVA() {
        return mImporteIVA;
    }

    /**
     * @return the mImporteTotal
     */
    public BigDecimal getmImporteTotal() {
        return mImporteTotal;
    }

    /**
     * @return the mImporteViaticos
     */
    public BigDecimal getmImporteViaticos() {
        return mImporteViaticos;
    }

    /**
     * @return the noOficioRenunciaAnticipo
     */
    public String getNoOficioRenunciaAnticipo() {
        return noOficioRenunciaAnticipo;
    }

    /**
     * @return the noProcedimientoCNET
     */
    public String getNoProcedimientoCNET() {
        return noProcedimientoCNET;
    }

    /**
     * @return the origenRM
     */
    public String getOrigenRM() {
        return origenRM;
    }

    /**
     * @return the otrosImpuestos
     */
    public BigDecimal getOtrosImpuestos() {
        return otrosImpuestos;
    }

    /**
     * @return the plazo
     */
    public String getPlazo() {
        return plazo;
    }

    /**
     * @return the porcImpuestoCedular
     */
    public double getPorcImpuestoCedular() {
        return porcImpuestoCedular;
    }

    /**
     * @return the porcIVAAplicable
     */
    public double getPorcIVAAplicable() {
        return porcIVAAplicable;
    }

    /**
     * @return the rfc
     */
    public String getRfc() {
        return rfc;
    }

    /**
     * @return the abierto
     */
    public boolean isAbierto() {
        return abierto;
    }

    /**
     * @return the aplicaImpuestoCedular
     */
    public boolean isAplicaImpuestoCedular() {
        return aplicaImpuestoCedular;
    }

    /**
     * @return the camInst
     */
    public boolean isCamInst() {
        return camInst;
    }

    /**
     * @return the convEjercicioAnt
     */
    public boolean isConvEjercicioAnt() {
        return convEjercicioAnt;
    }

    /**
     * @return the descentralizado
     */
    public boolean isDescentralizado() {
        return descentralizado;
    }

    /**
     * @return the eliminaRetencion6IVA
     */
    public boolean isEliminaRetencion6IVA() {
        return eliminaRetencion6IVA;
    }

    /**
     * @return the pasivo
     */
    public boolean isPasivo() {
        return pasivo;
    }

    /**
     * @return the plurianual
     */
    public boolean isPlurianual() {
        return plurianual;
    }

    /**
     * @return the radicado
     */
    public boolean isRadicado() {
        return radicado;
    }

    /**
     * @return the renunciaAnticipo
     */
    public boolean isRenunciaAnticipo() {
        return renunciaAnticipo;
    }

    /**
     * @return the requiereAnticipo
     */
    public boolean isRequiereAnticipo() {
        return requiereAnticipo;
    }

    /**
     * @return the saldoAnticipo
     */
    public boolean isSaldoAnticipo() {
        return saldoAnticipo;
    }

    /**
     * @return the tieneAnticipo
     */
    public boolean isTieneAnticipo() {
        return tieneAnticipo;
    }

    /**
     * @param abierto
     *            the abierto to set
     */
    public void setAbierto(boolean abierto) {
        this.abierto = abierto;
    }

    /**
     * @param aplicaImpuestoCedular
     *            the aplicaImpuestoCedular to set
     */
    public void setAplicaImpuestoCedular(boolean aplicaImpuestoCedular) {
        this.aplicaImpuestoCedular = aplicaImpuestoCedular;
    }

    /**
     * @param aprobacionPLU
     *            the aprobacionPLU to set
     */
    public void setAprobacionPLU(String aprobacionPLU) {
        this.aprobacionPLU = aprobacionPLU;
    }

    /**
     * @param camInst
     *            the camInst to set
     */
    public void setCamInst(boolean camInst) {
        this.camInst = camInst;
    }

    /**
     * @param caNoCompromiso
     *            the caNoCompromiso to set
     */
    public void setCaNoCompromiso(String caNoCompromiso) {
        this.caNoCompromiso = caNoCompromiso;
    }

    /**
     * @param centroContable
     *            the centroContable to set
     */
    public void setCentroContable(String centroContable) {
        this.centroContable = centroContable;
    }

    /**
     * @param codExpedienteCNET
     *            the codExpedienteCNET to set
     */
    public void setCodExpedienteCNET(String codExpedienteCNET) {
        this.codExpedienteCNET = codExpedienteCNET;
    }

    /**
     * @param conceptoContrato
     *            the conceptoContrato to set
     */
    public void setConceptoContrato(String conceptoContrato) {
        this.conceptoContrato = conceptoContrato;
    }

    /**
     * @param convEjercicioAnt
     *            the convEjercicioAnt to set
     */
    public void setConvEjercicioAnt(boolean convEjercicioAnt) {
        this.convEjercicioAnt = convEjercicioAnt;
    }

    /**
     * @param descentralizado
     *            the descentralizado to set
     */
    public void setDescentralizado(boolean descentralizado) {
        this.descentralizado = descentralizado;
    }

    /**
     * @param ejercicioFiscal
     *            the ejercicioFiscal to set
     */
    public void setEjercicioFiscal(String ejercicioFiscal) {
        this.ejercicioFiscal = ejercicioFiscal;
    }

    /**
     * @param eliminaRetencion6IVA
     *            the eliminaRetencion6IVA to set
     */
    public void setEliminaRetencion6IVA(boolean eliminaRetencion6IVA) {
        this.eliminaRetencion6IVA = eliminaRetencion6IVA;
    }

    /**
     * @param fAdjudicacion
     *            the fAdjudicacion to set
     */
    public void setfAdjudicacion(Date fAdjudicacion) {
        this.fAdjudicacion = fAdjudicacion;
    }

    /**
     * @param fContratoFin
     *            the fContratoFin to set
     */
    public void setfContratoFin(Date fContratoFin) {
        this.fContratoFin = fContratoFin;
    }

    /**
     * @param fContratoIni
     *            the fContratoIni to set
     */
    public void setfContratoIni(Date fContratoIni) {
        this.fContratoIni = fContratoIni;
    }

    /**
     * @param fContratoPropuestas
     *            the fContratoPropuestas to set
     */
    public void setfContratoPropuestas(Date fContratoPropuestas) {
        this.fContratoPropuestas = fContratoPropuestas;
    }

    /**
     * @param fContratoSolicitud
     *            the fContratoSolicitud to set
     */
    public void setfContratoSolicitud(Date fContratoSolicitud) {
        this.fContratoSolicitud = fContratoSolicitud;
    }

    /**
     * @param fDocumento
     *            the fDocumento to set
     */
    public void setfDocumento(Date fDocumento) {
        this.fDocumento = fDocumento;
    }

    /**
     * @param fFirmaContrato
     *            the fFirmaContrato to set
     */
    public void setfFirmaContrato(Date fFirmaContrato) {
        this.fFirmaContrato = fFirmaContrato;
    }

    /**
     * @param folioCompromisoSICOP
     *            the folioCompromisoSICOP to set
     */
    public void setFolioCompromisoSICOP(String folioCompromisoSICOP) {
        this.folioCompromisoSICOP = folioCompromisoSICOP;
    }

    /**
     * @param fVigenciaIVA
     *            the fVigenciaIVA to set
     */
    public void setfVigenciaIVA(Date fVigenciaIVA) {
        this.fVigenciaIVA = fVigenciaIVA;
    }

    /**
     * @param idCaso
     *            the idCaso to set
     */
    public void setIdCaso(int idCaso) {
        this.idCaso = idCaso;
    }

    /**
     * @param idContrato
     *            the idContrato to set
     */
    public void setIdContrato(String idContrato) {
        this.idContrato = idContrato;
    }

    /**
     * @param idDistritoRiego
     *            the idDistritoRiego to set
     */
    public void setIdDistritoRiego(String idDistritoRiego) {
        this.idDistritoRiego = idDistritoRiego;
    }

    /**
     * @param idGEstatal
     *            the idGEstatal to set
     */
    public void setIdGEstatal(String idGEstatal) {
        this.idGEstatal = idGEstatal;
    }

    /**
     * @param idGRegional
     *            the idGRegional to set
     */
    public void setIdGRegional(String idGRegional) {
        this.idGRegional = idGRegional;
    }

    /**
     * @param idPrecio
     *            the idPrecio to set
     */
    public void setIdPrecio(int idPrecio) {
        this.idPrecio = idPrecio;
    }

    /**
     * @param idSistemaOrigen
     *            the idSistemaOrigen to set
     */
    public void setIdSistemaOrigen(char idSistemaOrigen) {
        this.idSistemaOrigen = idSistemaOrigen;
    }

    /**
     * @param idTipoAdjudicacion
     *            the idTipoAdjudicacion to set
     */
    public void setIdTipoAdjudicacion(int idTipoAdjudicacion) {
        this.idTipoAdjudicacion = idTipoAdjudicacion;
    }

    /**
     * @param idTipoContratoDiverso
     *            the idTipoContratoDiverso to set
     */
    public void setIdTipoContratoDiverso(String idTipoContratoDiverso) {
        this.idTipoContratoDiverso = idTipoContratoDiverso;
    }

    /**
     * @param idTipoDocumento
     *            the idTipoDocumento to set
     */
    public void setIdTipoDocumento(String idTipoDocumento) {
        this.idTipoDocumento = idTipoDocumento;
    }

    /**
     * @param idTipoFondo
     *            the idTipoFondo to set
     */
    public void setIdTipoFondo(String idTipoFondo) {
        this.idTipoFondo = idTipoFondo;
    }

    /**
     * @param idTipoLimiteDlls
     *            the idTipoLimiteDlls to set
     */
    public void setIdTipoLimiteDlls(String idTipoLimiteDlls) {
        this.idTipoLimiteDlls = idTipoLimiteDlls;
    }

    /**
     * @param idTipoMoneda
     *            the idTipoMoneda to set
     */
    public void setIdTipoMoneda(String idTipoMoneda) {
        this.idTipoMoneda = idTipoMoneda;
    }

    /**
     * @param idTipoMontoDesembolso
     *            the idTipoMontoDesembolso to set
     */
    public void setIdTipoMontoDesembolso(String idTipoMontoDesembolso) {
        this.idTipoMontoDesembolso = idTipoMontoDesembolso;
    }

    /**
     * @param idUnidadAdministrativa
     *            the idUnidadAdministrativa to set
     */
    public void setIdUnidadAdministrativa(String idUnidadAdministrativa) {
        this.idUnidadAdministrativa = idUnidadAdministrativa;
    }

    /**
     * @param idUsuarioResponsable
     *            the idUsuarioResponsable to set
     */
    public void setIdUsuarioResponsable(String idUsuarioResponsable) {
        this.idUsuarioResponsable = idUsuarioResponsable;
    }

    /**
     * @param mContratoME
     *            the mContratoME to set
     */
    public void setmContratoME(BigDecimal mContratoME) {
        this.mContratoME = mContratoME;
    }

    /**
     * @param mContratoMN
     *            the mContratoMN to set
     */
    public void setmContratoMN(BigDecimal mContratoMN) {
        this.mContratoMN = mContratoMN;
    }

    /**
     * @param mImporteBruto
     *            the mImporteBruto to set
     */
    public void setmImporteBruto(BigDecimal mImporteBruto) {
        this.mImporteBruto = mImporteBruto;
    }

    /**
     * @param mImporteContrato
     *            the mImporteContrato to set
     */
    public void setmImporteContrato(BigDecimal mImporteContrato) {
        this.mImporteContrato = mImporteContrato;
    }

    /**
     * @param mImporteHonorarios
     *            the mImporteHonorarios to set
     */
    public void setmImporteHonorarios(BigDecimal mImporteHonorarios) {
        this.mImporteHonorarios = mImporteHonorarios;
    }

    /**
     * @param mImporteIVA
     *            the mImporteIVA to set
     */
    public void setmImporteIVA(BigDecimal mImporteIVA) {
        this.mImporteIVA = mImporteIVA;
    }

    /**
     * @param mImporteTotal
     *            the mImporteTotal to set
     */
    public void setmImporteTotal(BigDecimal mImporteTotal) {
        this.mImporteTotal = mImporteTotal;
    }

    /**
     * @param mImporteViaticos
     *            the mImporteViaticos to set
     */
    public void setmImporteViaticos(BigDecimal mImporteViaticos) {
        this.mImporteViaticos = mImporteViaticos;
    }

    /**
     * @param noOficioRenunciaAnticipo
     *            the noOficioRenunciaAnticipo to set
     */
    public void setNoOficioRenunciaAnticipo(String noOficioRenunciaAnticipo) {
        this.noOficioRenunciaAnticipo = noOficioRenunciaAnticipo;
    }

    /**
     * @param noProcedimientoCNET
     *            the noProcedimientoCNET to set
     */
    public void setNoProcedimientoCNET(String noProcedimientoCNET) {
        this.noProcedimientoCNET = noProcedimientoCNET;
    }

    /**
     * @param origenRM
     *            the origenRM to set
     */
    public void setOrigenRM(String origenRM) {
        this.origenRM = origenRM;
    }

    /**
     * @param otrosImpuestos
     *            the otrosImpuestos to set
     */
    public void setOtrosImpuestos(BigDecimal otrosImpuestos) {
        this.otrosImpuestos = otrosImpuestos;
    }

    /**
     * @param pasivo
     *            the pasivo to set
     */
    public void setPasivo(boolean pasivo) {
        this.pasivo = pasivo;
    }

    /**
     * @param plazo
     *            the plazo to set
     */
    public void setPlazo(String plazo) {
        this.plazo = plazo;
    }

    /**
     * @param plurianual
     *            the plurianual to set
     */
    public void setPlurianual(boolean plurianual) {
        this.plurianual = plurianual;
    }

    /**
     * @param porcImpuestoCedular
     *            the porcImpuestoCedular to set
     */
    public void setPorcImpuestoCedular(double porcImpuestoCedular) {
        this.porcImpuestoCedular = porcImpuestoCedular;
    }

    /**
     * @param porcIVAAplicable
     *            the porcIVAAplicable to set
     */
    public void setPorcIVAAplicable(double porcIVAAplicable) {
        this.porcIVAAplicable = porcIVAAplicable;
    }

    /**
     * @param radicado
     *            the radicado to set
     */
    public void setRadicado(boolean radicado) {
        this.radicado = radicado;
    }

    /**
     * @param renunciaAnticipo
     *            the renunciaAnticipo to set
     */
    public void setRenunciaAnticipo(boolean renunciaAnticipo) {
        this.renunciaAnticipo = renunciaAnticipo;
    }

    /**
     * @param requiereAnticipo
     *            the requiereAnticipo to set
     */
    public void setRequiereAnticipo(boolean requiereAnticipo) {
        this.requiereAnticipo = requiereAnticipo;
    }

    /**
     * @param rfc
     *            the rfc to set
     */
    public void setRfc(String rfc) {
        this.rfc = rfc;
    }

    /**
     * @param saldoAnticipo
     *            the saldoAnticipo to set
     */
    public void setSaldoAnticipo(boolean saldoAnticipo) {
        this.saldoAnticipo = saldoAnticipo;
    }

    /**
     * @param tieneAnticipo
     *            the tieneAnticipo to set
     */
    public void setTieneAnticipo(boolean tieneAnticipo) {
        this.tieneAnticipo = tieneAnticipo;
    }

    @Override
    public String toString() {
        return "ContratoDiverso [ejercicioFiscal=" + ejercicioFiscal + ", centroContable=" + centroContable + ", idContrato=" + idContrato + ", idTipoDocumento=" + idTipoDocumento + ", idTipoMontoDesembolso=" + idTipoMontoDesembolso + ", rfc=" + rfc + ", idTipoContratoDiverso=" + idTipoContratoDiverso + ", plazo=" + plazo + ", idUnidadAdministrativa=" + idUnidadAdministrativa + ", idGRegional=" + idGRegional + ", idGEstatal=" + idGEstatal + ", idDistritoRiego=" + idDistritoRiego + ", idTipoFondo=" + idTipoFondo + ", conceptoContrato=" + conceptoContrato + ", fDocumento=" + fDocumento + ", idTipoAdjudicacion=" + idTipoAdjudicacion + ", fAdjudicacion=" + fAdjudicacion + ", fContratoIni=" + fContratoIni + ", fContratoFin=" + fContratoFin + ", idTipoMoneda=" + idTipoMoneda + ", fVigenciaIVA=" + fVigenciaIVA + ", porcIVAAplicable=" + porcIVAAplicable + ", mImporteContrato=" + mImporteContrato + ", mImporteHonorarios=" + mImporteHonorarios + ", mImporteViaticos=" + mImporteViaticos + ", mImporteBruto=" + mImporteBruto + ", mImporteIVA=" + mImporteIVA + ", mImporteTotal=" + mImporteTotal + ", mContratoMN=" + mContratoMN + ", mContratoME=" + mContratoME + ", requiereAnticipo=" + requiereAnticipo + ", renunciaAnticipo=" + renunciaAnticipo + ", noOficioRenunciaAnticipo=" + noOficioRenunciaAnticipo + ", idUsuarioResponsable=" + idUsuarioResponsable + ", saldoAnticipo=" + saldoAnticipo + ", fFirmaContrato=" + fFirmaContrato + ", porcImpuestoCedular=" + porcImpuestoCedular + ", aplicaImpuestoCedular=" + aplicaImpuestoCedular + ", idSistemaOrigen=" + idSistemaOrigen + ", idTipoLimiteDlls=" + idTipoLimiteDlls + ", caNoCompromiso=" + caNoCompromiso + ", folioCompromisoSICOP=" + folioCompromisoSICOP + ", idCaso=" + idCaso + ", plurianual=" + plurianual + ", camInst=" + camInst + ", origenRM=" + origenRM + ", fContratoSolicitud=" + fContratoSolicitud + ", fContratoPropuestas=" + fContratoPropuestas + ", pasivo=" + pasivo + ", abierto=" + abierto + ", idPrecio=" + idPrecio + ", otrosImpuestos=" + otrosImpuestos + ", descentralizado=" + descentralizado + ", radicado=" + radicado + ", convEjercicioAnt=" + convEjercicioAnt + ", noProcedimientoCNET=" + noProcedimientoCNET + ", codContratoCNET=" + codContratoCNET + ", aprobacionPLU=" + aprobacionPLU + ", codExpedienteCNET=" + codExpedienteCNET + ", tieneAnticipo=" + tieneAnticipo + ", eliminaRetencion6IVA=" + eliminaRetencion6IVA + "]";
    }

    public void setDetalleEP(List<ContratoEP> detalleEP) {
        this.detalleEP = detalleEP;
    }

    public List<ContratoEP> getDetalleEP() {
        return this.detalleEP;
    }

    public void setAnticipo(ContratoDiversoAnticipo anticipo) {
        this.anticipo = anticipo;
    }

    public ContratoDiversoAnticipo getAnticipo() {
        return this.anticipo;
    }

    public void setRetenciones(List<ContratoDiversoRetencion> retenciones) {
        this.retenciones = retenciones;
    }

    public List<ContratoDiversoRetencion> getRetenciones() {
        return this.retenciones;
    }

    public static ContratoDiverso generaContratoDiverso(Connection conn, ConvenioColaboracion cc) throws Exception {
        ContratoDiverso contrato = new ContratoDiverso();
        String ejercicioFiscal = EjercicioFiscalManager.getEjercicioFiscalActivo(conn).getaEjercicioFiscal();
        contrato.setEjercicioFiscal(ejercicioFiscal);
        contrato.setCentroContable(cc.getEncabezado().getCentroContable());
        contrato.setIdContrato(cc.getEncabezado().getIdContrato());
        contrato.setRfc(cc.getEncabezado().getRfc());
        contrato.setIdTipoContratoDiverso(ConvenioColaboracion.ID_TIPO_CONVENIO);
        contrato.setIdUnidadAdministrativa(cc.getEncabezado().getIdUnidadAdministrativa());
        contrato.setConceptoContrato(cc.getEncabezado().getConceptoConvenio());
        contrato.setfDocumento(cc.getEncabezado().getFechaCaptura());
        contrato.setIdTipoAdjudicacion(0);
        contrato.setfAdjudicacion(cc.getEncabezado().getFechaFirmaConvenio());
        contrato.setfContratoIni(cc.getEncabezado().getFechaConvenioInicio());
        contrato.setfContratoFin(cc.getEncabezado().getFechaFinConvenio());
        contrato.setPorcIVAAplicable(cc.getEncabezado().getPorcIvaAplicable() / 100.0);
        contrato.setmImporteContrato(cc.getEncabezado().getmImporteTotal());
        contrato.setmImporteBruto(cc.getEncabezado().getmImporteBruto());
        contrato.setmImporteIVA(cc.getEncabezado().getmImporteIVA());
        contrato.setmImporteTotal(cc.getEncabezado().getmImporteTotal());
        contrato.setmContratoMN(cc.getEncabezado().getmImporteTotal());
        contrato.setIdUsuarioResponsable(cc.getEncabezado().getLoginCaptura());
        contrato.setfFirmaContrato(cc.getEncabezado().getFechaFirmaConvenio());
        contrato.setAplicaImpuestoCedular(false);
        contrato.setPlurianual(false);
        contrato.setOrigenRM("CONVENIO");
        contrato.setPasivo(false);
        contrato.setAbierto(false);
        contrato.setEliminaRetencion6IVA(false);
        return contrato;
    }

    public static List<ContratoEP> generaDetalleContratoDiverso(Connection conn, ConvenioColaboracion cc) throws Exception {
        List<ContratoEP> detalle = new ArrayList<ContratoEP>();
        String ejercicioFiscal = EjercicioFiscalManager.getEjercicioFiscalActivo(conn).getaEjercicioFiscal();
        for (ConvenioColaboracionDetalle renglon : cc.getDetalle()) {
            ContratoEP contratoEP = new ContratoEP();
            contratoEP.setCentroContable(cc.getEncabezado().getCentroContable());
            contratoEP.setEjercicio(ejercicioFiscal);
            contratoEP.setEp(renglon.getEp());
            contratoEP.setIdContrato(cc.getEncabezado().getIdContrato());
            contratoEP.setTipoContrato(ConvenioColaboracion.TIPO_CONTRATO);
            detalle.add(contratoEP);
        }
        return detalle;
    }

    public static ContratoDiversoAnticipo generaDetalleAnticipo(Connection conn, ConvenioColaboracion cc) throws Exception {
        String ejercicioFiscal = EjercicioFiscalManager.getEjercicioFiscalActivo(conn).getaEjercicioFiscal();
        ContratoDiversoAnticipo anticipo = new ContratoDiversoAnticipo();
        anticipo.setCentroContable(cc.getEncabezado().getCentroContable());
        anticipo.setEjercicio(ejercicioFiscal);
        anticipo.setIdContrato(cc.getEncabezado().getIdContrato());
        anticipo.setTipoContrato(ConvenioColaboracion.TIPO_CONTRATO);
        return anticipo;
    }

    public boolean isAplica15D() {
        return aplica15D;
    }

    public void setAplica15D(boolean aplica15d) {
        aplica15D = aplica15d;
    }
}
