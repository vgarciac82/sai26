package com.axtel.egresos.viaticos;

import java.math.BigDecimal;
import java.util.Base64;

public class TransporteAereo extends Transporte {

    private String cNumeroBoleto;

    private BigDecimal mImporteBoleto;

    private String partida;

    private String ruta;

    private String RFCVuelo;

    private String NombreVuelo;

    private int folioPago;

    private String tipoPago;

    public BigDecimal getmImporteBoleto() {
        return mImporteBoleto;
    }

    public void setmImporteBoleto(BigDecimal mImporteBoleto) {
        this.mImporteBoleto = mImporteBoleto;
    }

    public String getcNumeroBoleto() {
        return cNumeroBoleto;
    }

    public void setcNumeroBoleto(String cNumeroBoleto) {
        this.cNumeroBoleto = cNumeroBoleto;
    }

    public String getPartida() {
        return partida;
    }

    public void setPartida(String partida) {
        this.partida = partida;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public String getRFCVuelo() {
        return RFCVuelo;
    }

    public void setRFCVuelo(String rFCVuelo) {
        RFCVuelo = rFCVuelo;
    }

    public String getNombreVuelo() {
        return NombreVuelo;
    }

    public void setNombreVuelo(String nombreVuelo) {
        NombreVuelo = nombreVuelo;
    }

    public int getFolioPago() {
        return folioPago;
    }

    public void setFolioPago(int folioPago) {
        this.folioPago = folioPago;
    }

    public String getTipoPago() {
        return tipoPago;
    }

    public void setTipoPago(String tipoPago) {
        this.tipoPago = tipoPago;
    }

    @Override
    public String toString() {
        return "TransporteAereo [cNumeroBoleto=" + cNumeroBoleto + ", mImporteBoleto=" + mImporteBoleto + ", partida=" + partida + ", ruta=" + ruta + ", RFCVuelo=" + RFCVuelo + ", NombreVuelo=" + NombreVuelo + ", folioPago=" + folioPago + ", tipoPago=" + tipoPago + "]";
    }
}
