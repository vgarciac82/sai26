package com.syc.contable.core;

import java.sql.ResultSet;
import java.util.Base64;

public class ComprobacionLaudos {

    private String esDevengado;

    private String esLiquidacion;

    private String esRetencionSicop;

    private int folioCaja;

    private int folioRelacionGastos;

    private double importeNeto;

    private double importeCaja;

    private double importeRetencion;

    /**
     * @return the esDevengado
     */
    public String getEsDevengado() {
        return esDevengado;
    }

    /**
     * @return the esLiquidacion
     */
    public String getEsLiquidacion() {
        return esLiquidacion;
    }

    /**
     * @return the esRetencionSicop
     */
    public String getEsRetencionSicop() {
        return esRetencionSicop;
    }

    /**
     * @return the folioCaja
     */
    public int getFolioCaja() {
        return folioCaja;
    }

    /**
     * @return the folioRelacionGastos
     */
    public int getFolioRelacionGastos() {
        return folioRelacionGastos;
    }

    /**
     * @return the importeNeto
     */
    public double getImporteNeto() {
        return importeNeto;
    }

    /**
     * @return the importeRetencion
     */
    public double getImporteRetencion() {
        return importeRetencion;
    }

    /**
     * @param esDevengado
     *            the esDevengado to set
     */
    public void setEsDevengado(String esDevengado) {
        this.esDevengado = esDevengado;
    }

    /**
     * @param esLiquidacion
     *            the esLiquidacion to set
     */
    public void setEsLiquidacion(String esLiquidacion) {
        this.esLiquidacion = esLiquidacion;
    }

    /**
     * @param esRetencionSicop
     *            the esRetencionSicop to set
     */
    public void setEsRetencionSicop(String esRetencionSicop) {
        this.esRetencionSicop = esRetencionSicop;
    }

    /**
     * @param folioCaja
     *            the folioCaja to set
     */
    public void setFolioCaja(int folioCaja) {
        this.folioCaja = folioCaja;
    }

    /**
     * @param folioRelacionGastos
     *            the folioRelacionGastos to set
     */
    public void setFolioRelacionGastos(int folioRelacionGastos) {
        this.folioRelacionGastos = folioRelacionGastos;
    }

    /**
     * @param importeNeto
     *            the importeNeto to set
     */
    public void setImporteNeto(double importeNeto) {
        this.importeNeto = importeNeto;
    }

    /**
     * @param importeRetencion
     *            the importeRetencion to set
     */
    public void setImporteRetencion(double importeRetencion) {
        this.importeRetencion = importeRetencion;
    }

    public static ComprobacionLaudos instance(ResultSet rs) throws Exception {
        ComprobacionLaudos comprobacion = new ComprobacionLaudos();
        comprobacion.setFolioRelacionGastos(rs.getInt("nFolioRELACIONGASTOS"));
        comprobacion.setFolioCaja(rs.getInt("nFolioCaja"));
        comprobacion.setImporteRetencion(rs.getDouble("mImporteRet"));
        comprobacion.setImporteNeto(rs.getDouble("mImporteNeto"));
        comprobacion.setEsRetencionSicop(rs.getString("cRetSICOP"));
        comprobacion.setEsDevengado(rs.getString("cEsDevengado"));
        comprobacion.setEsLiquidacion(rs.getString("cEsLiquidacion"));
        return comprobacion;
    }

    /**
     * @return the importeCaja
     */
    public double getImporteCaja() {
        return importeCaja;
    }

    /**
     * @param importeCaja the importeCaja to set
     */
    public void setImporteCaja(double importeCaja) {
        this.importeCaja = importeCaja;
    }
}
