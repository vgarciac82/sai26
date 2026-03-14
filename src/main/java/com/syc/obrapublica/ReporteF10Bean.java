package com.syc.obrapublica;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Base64;

public class ReporteF10Bean {

    String CC;

    List<String[]> info;

    double[][] resumen;

    Map<String, Double> totales;

    String UR;

    public String getCC() {
        return CC;
    }

    public List<String[]> getInfo() {
        return info;
    }

    public double[][] getResumen() {
        return resumen;
    }

    public Map<String, Double> getTotales() {
        return totales;
    }

    public String getUR() {
        return UR;
    }

    public void setCC(String cC) {
        CC = cC;
    }

    public void setInfo(List<String[]> info) {
        this.info = info;
    }

    public void setInfo(String[] info) {
        if (this.info == null)
            setInfo(new ArrayList<String[]>());
        getInfo().add(info);
    }

    public void setResumen(double[][] resumen) {
        this.resumen = resumen;
    }

    public void setTotales(Map<String, Double> totales) {
        this.totales = totales;
    }

    public void setUR(String uR) {
        UR = uR;
    }
}
