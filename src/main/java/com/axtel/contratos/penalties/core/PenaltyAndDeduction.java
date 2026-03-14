package com.axtel.contratos.penalties.core;

import java.util.ArrayList;
import java.util.Base64;

public class PenaltyAndDeduction {

    private int nIdPenaltyDeduction;

    private String cFolio;

    private String cIdContratoDefinitivo;

    private int nIdEstate;

    private String fCaptureDate;

    private String cCaptureUsser;

    private int lPenalty;

    private int lDeduction;

    private int lExist;

    private String cTipoContrato;

    private String cDocumentHAplicado;

    private String cValidatingUser;

    private String cObservations;

    private int nIdOper;

    private String cConcepto;

    private String cOficio;

    private int nPeriodo;

    private String cNumContratoCNET;

    private String cProveedor;

    private String cEmailCaptureUsser;

    private ArrayList<PenaltyItems> penaltyItems;

    private ArrayList<DeductionItems> deductionItems;

    public int getnIdPenaltyDeduction() {
        return nIdPenaltyDeduction;
    }

    public void setnIdPenaltyDeduction(int nIdPenaltyDeduction) {
        this.nIdPenaltyDeduction = nIdPenaltyDeduction;
    }

    public String getcFolio() {
        return cFolio;
    }

    public void setcFolio(String cFolio) {
        this.cFolio = cFolio;
    }

    public String getcIdContratoDefinitivo() {
        return cIdContratoDefinitivo;
    }

    public void setcIdContratoDefinitivo(String cIdContratoDefinitivo) {
        this.cIdContratoDefinitivo = cIdContratoDefinitivo;
    }

    public int getnIdEstate() {
        return nIdEstate;
    }

    public void setnIdEstate(int nIdEstate) {
        this.nIdEstate = nIdEstate;
    }

    public String getfCaptureDate() {
        return fCaptureDate;
    }

    public void setfCaptureDate(String fCaptureDate) {
        this.fCaptureDate = fCaptureDate;
    }

    public String getcCaptureUsser() {
        return cCaptureUsser;
    }

    public void setcCaptureUsser(String cCaptureUsser) {
        this.cCaptureUsser = cCaptureUsser;
    }

    public int getlPenalty() {
        return lPenalty;
    }

    public void setlPenalty(int lPenalty) {
        this.lPenalty = lPenalty;
    }

    public int getlDeduction() {
        return lDeduction;
    }

    public void setlDeduction(int lDeduction) {
        this.lDeduction = lDeduction;
    }

    public int getlExist() {
        return lExist;
    }

    public void setlExist(int lExist) {
        this.lExist = lExist;
    }

    public String getcTipoContrato() {
        return cTipoContrato;
    }

    public void setcTipoContrato(String cTipoContrato) {
        this.cTipoContrato = cTipoContrato;
    }

    public String getcDocumentHAplicado() {
        return cDocumentHAplicado;
    }

    public void setcDocumentHAplicado(String cDocumentHAplicado) {
        this.cDocumentHAplicado = cDocumentHAplicado;
    }

    public String getcValidatingUser() {
        return cValidatingUser;
    }

    public void setcValidatingUser(String cValidatingUser) {
        this.cValidatingUser = cValidatingUser;
    }

    public String getcObservations() {
        return cObservations;
    }

    public void setcObservations(String cObservations) {
        this.cObservations = cObservations;
    }

    public int getnIdOper() {
        return nIdOper;
    }

    public void setnIdOper(int nIdOper) {
        this.nIdOper = nIdOper;
    }

    public String getcConcepto() {
        return cConcepto;
    }

    public void setcConcepto(String cConcepto) {
        this.cConcepto = cConcepto;
    }

    public String getcOficio() {
        return cOficio;
    }

    public void setcOficio(String cOficio) {
        this.cOficio = cOficio;
    }

    public int getnPeriodo() {
        return nPeriodo;
    }

    public void setnPeriodo(int nPeriodo) {
        this.nPeriodo = nPeriodo;
    }

    public String getcNumContratoCNET() {
        return cNumContratoCNET;
    }

    public void setcNumContratoCNET(String cNumContratoCNET) {
        this.cNumContratoCNET = cNumContratoCNET;
    }

    public String getcProveedor() {
        return cProveedor;
    }

    public void setcProveedor(String cProveedor) {
        this.cProveedor = cProveedor;
    }

    public String getcEmailCaptureUsser() {
        return cEmailCaptureUsser;
    }

    public void setcEmailCaptureUsser(String cEmailCaptureUsser) {
        this.cEmailCaptureUsser = cEmailCaptureUsser;
    }

    public ArrayList<PenaltyItems> getPenaltyItems() {
        return penaltyItems;
    }

    public void setPenaltyItems(ArrayList<PenaltyItems> penaltyItems) {
        this.penaltyItems = penaltyItems;
    }

    public ArrayList<DeductionItems> getDeductionItems() {
        return deductionItems;
    }

    public void setDeductionItems(ArrayList<DeductionItems> deductionItems) {
        this.deductionItems = deductionItems;
    }

    @Override
    public String toString() {
        return "PenaltyAndDeduction [nIdPenaltyDeduction=" + nIdPenaltyDeduction + ", cFolio=" + cFolio + ", cIdContratoDefinitivo=" + cIdContratoDefinitivo + ", nIdEstate=" + nIdEstate + ", fCaptureDate=" + fCaptureDate + ", cCaptureUsser=" + cCaptureUsser + ", lPenalty=" + lPenalty + ", lDeduction=" + lDeduction + ", lExist=" + lExist + ", cTipoContrato=" + cTipoContrato + ", cDocumentHAplicado=" + cDocumentHAplicado + ", cValidatingUser=" + cValidatingUser + ", cObservations=" + cObservations + ", nIdOper=" + nIdOper + ", cConcepto=" + cConcepto + ", cOficio=" + cOficio + ", nPeriodo=" + nPeriodo + ", cNumContratoCNET=" + cNumContratoCNET + ", cProveedor=" + cProveedor + ", cEmailCaptureUsser=" + cEmailCaptureUsser + "]";
    }
}
