package com.axtel.egresos.compromiso;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import com.axtel.contratos.core.ConvenioColaboracion;
import com.axtel.contratos.core.ConvenioColaboracionDetalle;
import java.util.Base64;

public class PrecompromisoFinancieroDetalle {

    private String centroContable;

    private String cEvento;

    private String EP;

    private int folioPrecomFinanciero;

    private BigDecimal importe;

    private BigDecimal importeNegativo;

    private int mes;

    private int renglon;

    private String unidadResponsable;

    /**
     * @return the centroContable
     */
    public String getCentroContable() {
        return centroContable;
    }

    /**
     * @return the cEvento
     */
    public String getcEvento() {
        return cEvento;
    }

    /**
     * @return the eP
     */
    public String getEP() {
        return EP;
    }

    /**
     * @return the folioPrecomFinanciero
     */
    public int getFolioPrecomFinanciero() {
        return folioPrecomFinanciero;
    }

    /**
     * @return the importe
     */
    public BigDecimal getImporte() {
        return importe;
    }

    /**
     * @return the importeNegativo
     */
    public BigDecimal getImporteNegativo() {
        return importeNegativo;
    }

    /**
     * @return the mes
     */
    public int getMes() {
        return mes;
    }

    /**
     * @return the renglon
     */
    public int getRenglon() {
        return renglon;
    }

    /**
     * @return the unidadResponsable
     */
    public String getUnidadResponsable() {
        return unidadResponsable;
    }

    /**
     * @param centroContable
     *            the centroContable to set
     */
    public void setCentroContable(String centroContable) {
        this.centroContable = centroContable;
    }

    /**
     * @param cEvento
     *            the cEvento to set
     */
    public void setcEvento(String cEvento) {
        this.cEvento = cEvento;
    }

    /**
     * @param eP
     *            the eP to set
     */
    public void setEP(String eP) {
        EP = eP;
    }

    /**
     * @param folioPrecomFinanciero
     *            the folioPrecomFinanciero to set
     */
    public void setFolioPrecomFinanciero(int folioPrecomFinanciero) {
        this.folioPrecomFinanciero = folioPrecomFinanciero;
    }

    /**
     * @param importe
     *            the importe to set
     */
    public void setImporte(BigDecimal importe) {
        this.importe = importe;
    }

    /**
     * @param importeNegativo
     *            the importeNegativo to set
     */
    public void setImporteNegativo(BigDecimal importeNegativo) {
        this.importeNegativo = importeNegativo;
    }

    /**
     * @param mes
     *            the mes to set
     */
    public void setMes(int mes) {
        this.mes = mes;
    }

    /**
     * @param renglon
     *            the renglon to set
     */
    public void setRenglon(int renglon) {
        this.renglon = renglon;
    }

    /**
     * @param unidadResponsable
     *            the unidadResponsable to set
     */
    public void setUnidadResponsable(String unidadResponsable) {
        this.unidadResponsable = unidadResponsable;
    }

    @Override
    public String toString() {
        return "PrecompromisoFinancieroDetalle [folioPrecomFinanciero=" + folioPrecomFinanciero + ", renglon=" + renglon + ", EP=" + EP + ", cEvento=" + cEvento + ", importe=" + importe + ", importeNegativo=" + importeNegativo + ", mes=" + mes + ", centroContable=" + centroContable + ", unidadResponsable=" + unidadResponsable + "]";
    }

    public static List<PrecompromisoFinancieroDetalle> instanceFrom(Connection conn, ConvenioColaboracion convenio, int folioCompromiso) {
        List<PrecompromisoFinancieroDetalle> detalle = new ArrayList<>();
        int nRenglon = 1;
        for (ConvenioColaboracionDetalle renglonConvenio : convenio.getDetalle()) {
            PrecompromisoFinancieroDetalle precomDetalle = new PrecompromisoFinancieroDetalle();
            precomDetalle.setCentroContable(convenio.getEncabezado().getCentroContable());
            precomDetalle.setcEvento("CMP005");
            precomDetalle.setEP(renglonConvenio.getEp());
            precomDetalle.setFolioPrecomFinanciero(folioCompromiso);
            precomDetalle.setImporte(renglonConvenio.getImporte());
            precomDetalle.setImporteNegativo(renglonConvenio.getImporte().multiply(new BigDecimal(-1.0)));
            precomDetalle.setRenglon(nRenglon++);
            precomDetalle.setMes(renglonConvenio.getMes());
            precomDetalle.setUnidadResponsable(convenio.getEncabezado().getUnidadEjecutora());
            detalle.add(precomDetalle);
        }
        return detalle;
    }
}
