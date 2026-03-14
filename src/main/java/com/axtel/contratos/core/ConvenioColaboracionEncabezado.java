package com.axtel.contratos.core;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Base64;

/**
 * POJO Convenio de colaboracion.
 *
 * @author vicente.garcia
 */
public class ConvenioColaboracionEncabezado {

    private String conceptoConvenio;

    private String idTipoAdjudicacion;

    private String idUnidadAdministrativa;

    private Date fechaCaptura = new Date();

    private Date fechaFinConvenio;

    private Date fechaConvenioInicio;

    private Date fechaFirmaConvenio;

    private int folioConvenioColaboracion;

    private String idContrato;

    private String esPlurianual = "N";

    private String loginCaptura;

    private BigDecimal mImporteBruto;

    private BigDecimal mImporteConvenio;

    private BigDecimal mImporteIVA;

    private BigDecimal mImporteTotal;

    private int porcIvaAplicable;

    private String rfc;

    private String unidadEjecutora;

    private String setCentroContable;

    @Override
    public String toString() {
        return "ConvenioColaboracion [conceptoConvenio=" + conceptoConvenio + ", idTipoAdjudicacion=" + idTipoAdjudicacion + ", idUnidadAdministrativa=" + idUnidadAdministrativa + ", fechaCaptura=" + fechaCaptura + ", fechaFinConvenio=" + fechaFinConvenio + ", fechaConvenioInicio=" + fechaConvenioInicio + ", fechaFirmaConvenio=" + fechaFirmaConvenio + ", folioConvenioColaboracion=" + folioConvenioColaboracion + ", idContrato=" + idContrato + ", esPlurianual=" + esPlurianual + ", loginCaptura=" + loginCaptura + ", mImporteBruto=" + mImporteBruto + ", mImporteConvenio=" + mImporteConvenio + ", mImporteIVA=" + mImporteIVA + ", mImporteTotal=" + mImporteTotal + ", porcIvaAplicable=" + porcIvaAplicable + ", rfc=" + rfc + ", unidadEjecutora=" + unidadEjecutora + "]";
    }

    /**
     * @return the conceptoConvenio
     */
    public String getConceptoConvenio() {
        return conceptoConvenio;
    }

    /**
     * @param conceptoConvenio
     *            the conceptoConvenio to set
     */
    public void setConceptoConvenio(String conceptoConvenio) {
        this.conceptoConvenio = conceptoConvenio;
    }

    /**
     * @return the idTipoAdjudicacion
     */
    public String getIdTipoAdjudicacion() {
        return idTipoAdjudicacion;
    }

    /**
     * @param idTipoAdjudicacion
     *            the idTipoAdjudicacion to set
     */
    public void setIdTipoAdjudicacion(String idTipoAdjudicacion) {
        this.idTipoAdjudicacion = idTipoAdjudicacion;
    }

    /**
     * @return the idUnidadAdministrativa
     */
    public String getIdUnidadAdministrativa() {
        return idUnidadAdministrativa;
    }

    /**
     * @param idUnidadAdministrativa
     *            the idUnidadAdministrativa to set
     */
    public void setIdUnidadAdministrativa(String idUnidadAdministrativa) {
        this.idUnidadAdministrativa = idUnidadAdministrativa;
    }

    /**
     * @return the fechaCaptura
     */
    public Date getFechaCaptura() {
        return fechaCaptura;
    }

    /**
     * @param fechaCaptura
     *            the fechaCaptura to set
     */
    public void setFechaCaptura(Date fechaCaptura) {
        this.fechaCaptura = fechaCaptura;
    }

    /**
     * @return the fechaFinConvenio
     */
    public Date getFechaFinConvenio() {
        return fechaFinConvenio;
    }

    /**
     * @param fechaFinConvenio
     *            the fechaFinConvenio to set
     */
    public void setFechaFinConvenio(Date fechaFinConvenio) {
        this.fechaFinConvenio = fechaFinConvenio;
    }

    /**
     * @return the fechaConvenioInicio
     */
    public Date getFechaConvenioInicio() {
        return fechaConvenioInicio;
    }

    /**
     * @param fechaConvenioInicio
     *            the fechaConvenioInicio to set
     */
    public void setFechaConvenioInicio(Date fechaConvenioInicio) {
        this.fechaConvenioInicio = fechaConvenioInicio;
    }

    /**
     * @return the fechaFirmaConvenio
     */
    public Date getFechaFirmaConvenio() {
        return fechaFirmaConvenio;
    }

    /**
     * @param fechaFirmaConvenio
     *            the fechaFirmaConvenio to set
     */
    public void setFechaFirmaConvenio(Date fechaFirmaConvenio) {
        this.fechaFirmaConvenio = fechaFirmaConvenio;
    }

    /**
     * @return the folioConvenioColaboracion
     */
    public int getFolioConvenioColaboracion() {
        return folioConvenioColaboracion;
    }

    /**
     * @param folioConvenioColaboracion
     *            the folioConvenioColaboracion to set
     */
    public void setFolioConvenioColaboracion(int folioConvenioColaboracion) {
        this.folioConvenioColaboracion = folioConvenioColaboracion;
    }

    /**
     * @return the idContrato
     */
    public String getIdContrato() {
        return idContrato;
    }

    /**
     * @param idContrato
     *            the idContrato to set
     */
    public void setIdContrato(String idContrato) {
        this.idContrato = idContrato;
    }

    /**
     * @return the esPlurianual
     */
    public String getEsPlurianual() {
        return esPlurianual;
    }

    /**
     * @param esPlurianual
     *            the esPlurianual to set
     */
    public void setEsPlurianual(String esPlurianual) {
        this.esPlurianual = esPlurianual;
    }

    /**
     * @return the loginCaptura
     */
    public String getLoginCaptura() {
        return loginCaptura;
    }

    /**
     * @param loginCaptura
     *            the loginCaptura to set
     */
    public void setLoginCaptura(String loginCaptura) {
        this.loginCaptura = loginCaptura;
    }

    /**
     * @return the mImporteBruto
     */
    public BigDecimal getmImporteBruto() {
        return mImporteBruto;
    }

    /**
     * @param mImporteBruto
     *            the mImporteBruto to set
     */
    public void setmImporteBruto(BigDecimal mImporteBruto) {
        this.mImporteBruto = mImporteBruto;
    }

    /**
     * @return the mImporteConvenio
     */
    public BigDecimal getmImporteConvenio() {
        return mImporteConvenio;
    }

    /**
     * @param mImporteConvenio
     *            the mImporteConvenio to set
     */
    public void setmImporteConvenio(BigDecimal mImporteConvenio) {
        this.mImporteConvenio = mImporteConvenio;
    }

    /**
     * @return the mImporteIVA
     */
    public BigDecimal getmImporteIVA() {
        return mImporteIVA;
    }

    /**
     * @param mImporteIVA
     *            the mImporteIVA to set
     */
    public void setmImporteIVA(BigDecimal mImporteIVA) {
        this.mImporteIVA = mImporteIVA;
    }

    /**
     * @return the mImporteTotal
     */
    public BigDecimal getmImporteTotal() {
        return mImporteTotal;
    }

    /**
     * @param mImporteTotal
     *            the mImporteTotal to set
     */
    public void setmImporteTotal(BigDecimal mImporteTotal) {
        this.mImporteTotal = mImporteTotal;
    }

    /**
     * @return the porcIvaAplicable
     */
    public int getPorcIvaAplicable() {
        return porcIvaAplicable;
    }

    /**
     * @param porcIvaAplicable
     *            the porcIvaAplicable to set
     */
    public void setPorcIvaAplicable(int porcIvaAplicable) {
        this.porcIvaAplicable = porcIvaAplicable;
    }

    /**
     * @return the rfc
     */
    public String getRfc() {
        return rfc;
    }

    /**
     * @param rfc
     *            the rfc to set
     */
    public void setRfc(String rfc) {
        this.rfc = rfc;
    }

    /**
     * @return the unidadEjecutora
     */
    public String getUnidadEjecutora() {
        return unidadEjecutora;
    }

    /**
     * @param unidadEjecutora
     *            the unidadEjecutora to set
     */
    public void setUnidadEjecutora(String unidadEjecutora) {
        this.unidadEjecutora = unidadEjecutora;
    }

    public void setCentroContable(String centroContable) {
        this.setCentroContable = centroContable;
    }

    public String getCentroContable() {
        return this.setCentroContable;
    }
}
