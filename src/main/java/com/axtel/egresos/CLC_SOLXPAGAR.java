package com.axtel.egresos;

import java.math.BigDecimal;
import java.util.Base64;

public class CLC_SOLXPAGAR {

    private int idEvento;

    private String evento;

    private String idRamoMl;

    private String idUnidadMl;

    private String cani;

    private String cgfu;

    private String cfun;

    private String csfu;

    private String cprg;

    private String cain;

    private String cppt;

    private String ccap;

    private String ccon;

    private String cparg;

    private String cpar;

    private String ctga;

    private String cfin;

    private String ccau;

    private String ccop;

    private String cgeo;

    private String cpla;

    private String cppi;

    private String ofin;

    private String aux1;

    private String aux2;

    private String aux3;

    private BigDecimal importe148;

    private String mes149;

    private String ncom15;

    private String cben16;

    private String nres17;

    private String noif18;

    private BigDecimal remaIsr192;

    private BigDecimal remIva193;

    private BigDecimal rem5mil194;

    private BigDecimal impNetneg200;

    private BigDecimal remaContrib202;

    private String ppag177;

    private String tnom178;

    private String tconc49;

    private String concMov50;

    private String spag176;

    private String proceso;

    private String idUnidadCr;

    private BigDecimal rem2mil315;

    private BigDecimal remOtret316;

    private BigDecimal remPena317;

    // --- Getters y Setters ---
    public int getIdEvento() {
        return idEvento;
    }

    public void setIdEvento(int idEvento) {
        this.idEvento = idEvento;
    }

    public String getEvento() {
        return evento;
    }

    public void setEvento(String evento) {
        this.evento = evento;
    }

    public String getIdRamoMl() {
        return idRamoMl;
    }

    public void setIdRamoMl(String idRamoMl) {
        this.idRamoMl = idRamoMl;
    }

    public String getIdUnidadMl() {
        return idUnidadMl;
    }

    public void setIdUnidadMl(String idUnidadMl) {
        this.idUnidadMl = idUnidadMl;
    }

    public String getCani() {
        return cani;
    }

    public void setCani(String cani) {
        this.cani = cani;
    }

    public String getCgfu() {
        return cgfu;
    }

    public void setCgfu(String cgfu) {
        this.cgfu = cgfu;
    }

    public String getCfun() {
        return cfun;
    }

    public void setCfun(String cfun) {
        this.cfun = cfun;
    }

    public String getCsfu() {
        return csfu;
    }

    public void setCsfu(String csfu) {
        this.csfu = csfu;
    }

    public String getCprg() {
        return cprg;
    }

    public void setCprg(String cprg) {
        this.cprg = cprg;
    }

    public String getCain() {
        return cain;
    }

    public void setCain(String cain) {
        this.cain = cain;
    }

    public String getCppt() {
        return cppt;
    }

    public void setCppt(String cppt) {
        this.cppt = cppt;
    }

    public String getCcap() {
        return ccap;
    }

    public void setCcap(String ccap) {
        this.ccap = ccap;
    }

    public String getCcon() {
        return ccon;
    }

    public void setCcon(String ccon) {
        this.ccon = ccon;
    }

    public String getCparg() {
        return cparg;
    }

    public void setCparg(String cparg) {
        this.cparg = cparg;
    }

    public String getCpar() {
        return cpar;
    }

    public void setCpar(String cpar) {
        this.cpar = cpar;
    }

    public String getCtga() {
        return ctga;
    }

    public void setCtga(String ctga) {
        this.ctga = ctga;
    }

    public String getCfin() {
        return cfin;
    }

    public void setCfin(String cfin) {
        this.cfin = cfin;
    }

    public String getCcau() {
        return ccau;
    }

    public void setCcau(String ccau) {
        this.ccau = ccau;
    }

    public String getCcop() {
        return ccop;
    }

    public void setCcop(String ccop) {
        this.ccop = ccop;
    }

    public String getCgeo() {
        return cgeo;
    }

    public void setCgeo(String cgeo) {
        this.cgeo = cgeo;
    }

    public String getCpla() {
        return cpla;
    }

    public void setCpla(String cpla) {
        this.cpla = cpla;
    }

    public String getCppi() {
        return cppi;
    }

    public void setCppi(String cppi) {
        this.cppi = cppi;
    }

    public String getOfin() {
        return ofin;
    }

    public void setOfin(String ofin) {
        this.ofin = ofin;
    }

    public String getAux1() {
        return aux1;
    }

    public void setAux1(String aux1) {
        this.aux1 = aux1;
    }

    public String getAux2() {
        return aux2;
    }

    public void setAux2(String aux2) {
        this.aux2 = aux2;
    }

    public String getAux3() {
        return aux3;
    }

    public void setAux3(String aux3) {
        this.aux3 = aux3;
    }

    public BigDecimal getImporte148() {
        return importe148;
    }

    public void setImporte148(BigDecimal importe148) {
        this.importe148 = importe148;
    }

    public String getMes149() {
        return mes149;
    }

    public void setMes149(String mes149) {
        this.mes149 = mes149;
    }

    public String getNcom15() {
        return ncom15;
    }

    public void setNcom15(String ncom15) {
        this.ncom15 = ncom15;
    }

    public String getCben16() {
        return cben16;
    }

    public void setCben16(String cben16) {
        this.cben16 = cben16;
    }

    public String getNres17() {
        return nres17;
    }

    public void setNres17(String nres17) {
        this.nres17 = nres17;
    }

    public String getNoif18() {
        return noif18;
    }

    public void setNoif18(String noif18) {
        this.noif18 = noif18;
    }

    public BigDecimal getRemaIsr192() {
        return remaIsr192;
    }

    public void setRemaIsr192(BigDecimal remaIsr192) {
        this.remaIsr192 = remaIsr192;
    }

    public BigDecimal getRemIva193() {
        return remIva193;
    }

    public void setRemIva193(BigDecimal remIva193) {
        this.remIva193 = remIva193;
    }

    public BigDecimal getRem5mil194() {
        return rem5mil194;
    }

    public void setRem5mil194(BigDecimal rem5mil194) {
        this.rem5mil194 = rem5mil194;
    }

    public BigDecimal getImpNetneg200() {
        return impNetneg200;
    }

    public void setImpNetneg200(BigDecimal impNetneg200) {
        this.impNetneg200 = impNetneg200;
    }

    public BigDecimal getRemaContrib202() {
        return remaContrib202;
    }

    public void setRemaContrib202(BigDecimal remaContrib202) {
        this.remaContrib202 = remaContrib202;
    }

    public String getPpag177() {
        return ppag177;
    }

    public void setPpag177(String ppag177) {
        this.ppag177 = ppag177;
    }

    public String getTnom178() {
        return tnom178;
    }

    public void setTnom178(String tnom178) {
        this.tnom178 = tnom178;
    }

    public String getTconc49() {
        return tconc49;
    }

    public void setTconc49(String tconc49) {
        this.tconc49 = tconc49;
    }

    public String getConcMov50() {
        return concMov50;
    }

    public void setConcMov50(String concMov50) {
        this.concMov50 = concMov50;
    }

    public String getSpag176() {
        return spag176;
    }

    public void setSpag176(String spag176) {
        this.spag176 = spag176;
    }

    public String getProceso() {
        return proceso;
    }

    public void setProceso(String proceso) {
        this.proceso = proceso;
    }

    public String getIdUnidadCr() {
        return idUnidadCr;
    }

    public void setIdUnidadCr(String idUnidadCr) {
        this.idUnidadCr = idUnidadCr;
    }

    public BigDecimal getRem2mil315() {
        return rem2mil315;
    }

    public void setRem2mil315(BigDecimal rem2mil315) {
        this.rem2mil315 = rem2mil315;
    }

    public BigDecimal getRemOtret316() {
        return remOtret316;
    }

    public void setRemOtret316(BigDecimal remOtret316) {
        this.remOtret316 = remOtret316;
    }

    public BigDecimal getRemPena317() {
        return remPena317;
    }

    public void setRemPena317(BigDecimal remPena317) {
        this.remPena317 = remPena317;
    }
}
