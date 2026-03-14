package com.syc.gestion.core;

import java.io.Serializable;
import java.util.Base64;

public class SeguimientoTurnoCaso implements Serializable {

    private final static long serialVersionUID = 1;

    private int org_id_caso;

    private int org_id_caso_oper;

    private int org_id_tc;

    private int org_id_oper;

    private String org_co_responsable;

    //identificar del que recibe la tarea
    private int des_id_caso;

    private int des_id_caso_oper;

    private int des_id_tc;

    private int des_id_oper;

    private String des_co_responsable;

    private String des_co_instruccion;

    public int getOrgIdCaso() {
        return org_id_caso;
    }

    public void setOrgIdCaso(int org_id_caso) {
        this.org_id_caso = org_id_caso;
    }

    public int getOrgIdCasoOper() {
        return org_id_caso_oper;
    }

    public void setOrgIdCasoOper(int org_id_caso_oper) {
        this.org_id_caso_oper = org_id_caso_oper;
    }

    public int getOrgIdTc() {
        return org_id_tc;
    }

    public void setOrgIdTc(int org_id_tc) {
        this.org_id_tc = org_id_tc;
    }

    public int getOrgIdOper() {
        return org_id_oper;
    }

    public void setOrgIdOper(int org_id_oper) {
        this.org_id_oper = org_id_oper;
    }

    public String getOrgCoResponsable() {
        return org_co_responsable;
    }

    public void setOrgCoResponsable(String org_co_responsable) {
        this.org_co_responsable = org_co_responsable;
    }

    public int getDesIdCaso() {
        return des_id_caso;
    }

    public void setDesIdCaso(int des_id_caso) {
        this.des_id_caso = des_id_caso;
    }

    public int getDesIdCasoOper() {
        return des_id_caso_oper;
    }

    public void setDesIdCasoOper(int des_id_caso_oper) {
        this.des_id_caso_oper = des_id_caso_oper;
    }

    public int getDesIdTc() {
        return des_id_tc;
    }

    public void setDesIdTc(int des_id_tc) {
        this.des_id_tc = des_id_tc;
    }

    public int getDesIdOper() {
        return des_id_oper;
    }

    public void setDesIdOper(int des_id_oper) {
        this.des_id_oper = des_id_oper;
    }

    public String getDesCoResponsable() {
        return des_co_responsable;
    }

    public void setDesCoResponsable(String des_co_responsable) {
        this.des_co_responsable = des_co_responsable;
    }

    public String getDesCoInstruccion() {
        return des_co_instruccion;
    }

    public void setDesCoInstruccion(String des_co_instruccion) {
        this.des_co_instruccion = des_co_instruccion;
    }
}
