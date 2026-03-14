package com.syc.sai.contratos.core;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Base64;

public class ContratoFederalizadoBean {

    private String centroContable;

    private int ejercicioFiscal;

    private String idContrato;

    public String getCentroContable() {
        return centroContable;
    }

    public void setCentroContable(String centroContable) {
        this.centroContable = centroContable;
    }

    public int getEjercicioFiscal() {
        return ejercicioFiscal;
    }

    public void setEjercicioFiscal(int ejercicioFiscal) {
        this.ejercicioFiscal = ejercicioFiscal;
    }

    public String getIdContrato() {
        return idContrato;
    }

    public void setIdContrato(String idContrato) {
        this.idContrato = idContrato;
    }

    public static ContratoFederalizadoBean instanceFromRequest(HttpServletRequest req) {
        ContratoFederalizadoBean cfb = new ContratoFederalizadoBean();
        cfb.setCentroContable(req.getParameter("cIdEntidadContable"));
        cfb.setEjercicioFiscal(Integer.parseInt(req.getParameter("cEjercicio")));
        cfb.setIdContrato(req.getParameter("cIdContrato"));
        return cfb;
    }

    @Override
    public String toString() {
        return "ContratoFederalizadoBean [centroContable=" + centroContable + ", ejercicioFiscal=" + ejercicioFiscal + ", idContrato=" + idContrato + "]";
    }
}
