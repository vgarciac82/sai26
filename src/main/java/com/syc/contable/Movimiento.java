package com.syc.contable;

import java.util.Date;
import java.util.Base64;

public class Movimiento {

    private String ADEFAS;

    private String aejerciciofiscal;

    private String ccancelamovimiento;

    private String ccentrocontable;

    private String cdescripcionmovpol;

    private long cfoliodocumentomovimiento;

    private String cmoneda;

    private String cramo;

    private String ctipodocumento;

    private String ctipomovimiento;

    private String ctipopoliza;

    private String cunidadresponsable;

    private String dconceptomovimiento;

    private Date fmovimiento;

    private Date foperacionmovimiento;

    private double mmovimiento;

    private long nConsecutivoMovimiento;

    private String ncuenta;

    private int ndocrenglon;

    private long nfoliopoliza;

    private String nsubcuenta;

    private int nTipoAjuste;

    private String parcial;

    private String periodo13;

    public String getADEFAS() {
        return ADEFAS;
    }

    public String getAejerciciofiscal() {
        return aejerciciofiscal;
    }

    public String getCcancelamovimiento() {
        return ccancelamovimiento;
    }

    public String getCcentrocontable() {
        return ccentrocontable;
    }

    public String getCdescripcionmovpol() {
        return cdescripcionmovpol;
    }

    public long getCfoliodocumentomovimiento() {
        return cfoliodocumentomovimiento;
    }

    public String getCmoneda() {
        return cmoneda;
    }

    public String getCramo() {
        return cramo;
    }

    public String getCtipodocumento() {
        return ctipodocumento;
    }

    public String getCtipomovimiento() {
        return ctipomovimiento;
    }

    public String getCtipopoliza() {
        return ctipopoliza;
    }

    public String getCunidadresponsable() {
        return cunidadresponsable;
    }

    public String getDconceptomovimiento() {
        return dconceptomovimiento;
    }

    public Date getFmovimiento() {
        return fmovimiento;
    }

    public Date getFoperacionmovimiento() {
        return foperacionmovimiento;
    }

    public double getMmovimiento() {
        return mmovimiento;
    }

    public long getnConsecutivoMovimiento() {
        return nConsecutivoMovimiento;
    }

    public String getNcuenta() {
        return ncuenta;
    }

    public int getNdocrenglon() {
        return ndocrenglon;
    }

    public long getNfoliopoliza() {
        return nfoliopoliza;
    }

    public String getNsubcuenta() {
        return nsubcuenta;
    }

    public int getnTipoAjuste() {
        return nTipoAjuste;
    }

    public String getParcial() {
        return parcial;
    }

    public String getPeriodo13() {
        return periodo13;
    }

    public void setADEFAS(String aDEFAS) {
        ADEFAS = aDEFAS;
    }

    public void setAejerciciofiscal(String aejerciciofiscal) {
        this.aejerciciofiscal = aejerciciofiscal;
    }

    public void setCcancelamovimiento(String ccancelamovimiento) {
        this.ccancelamovimiento = ccancelamovimiento;
    }

    public void setCcentrocontable(String ccentrocontable) {
        this.ccentrocontable = ccentrocontable;
    }

    public void setCdescripcionmovpol(String cdescripcionmovpol) {
        this.cdescripcionmovpol = cdescripcionmovpol;
    }

    public void setCfoliodocumentomovimiento(long cfoliodocumentomovimiento) {
        this.cfoliodocumentomovimiento = cfoliodocumentomovimiento;
    }

    public void setCmoneda(String cmoneda) {
        this.cmoneda = cmoneda;
    }

    public void setCramo(String cramo) {
        this.cramo = cramo;
    }

    public void setCtipodocumento(String ctipodocumento) {
        this.ctipodocumento = ctipodocumento;
    }

    public void setCtipomovimiento(String ctipomovimiento) {
        this.ctipomovimiento = ctipomovimiento;
    }

    public void setCtipopoliza(String ctipopoliza) {
        this.ctipopoliza = ctipopoliza;
    }

    public void setCunidadresponsable(String cunidadresponsable) {
        this.cunidadresponsable = cunidadresponsable;
    }

    public void setDconceptomovimiento(String dconceptomovimiento) {
        this.dconceptomovimiento = dconceptomovimiento;
    }

    public void setFmovimiento(Date fmovimiento) {
        this.fmovimiento = fmovimiento;
    }

    public void setFoperacionmovimiento(Date foperacionmovimiento) {
        this.foperacionmovimiento = foperacionmovimiento;
    }

    public void setMmovimiento(double mmovimiento) {
        this.mmovimiento = mmovimiento;
    }

    public void setnConsecutivoMovimiento(long nConsecutivoMovimiento) {
        this.nConsecutivoMovimiento = nConsecutivoMovimiento;
    }

    public void setNcuenta(String ncuenta) {
        this.ncuenta = ncuenta;
    }

    public void setNdocrenglon(int ndocrenglon) {
        this.ndocrenglon = ndocrenglon;
    }

    public void setNfoliopoliza(long nfoliopoliza) {
        this.nfoliopoliza = nfoliopoliza;
    }

    public void setNsubcuenta(String nsubcuenta) {
        this.nsubcuenta = nsubcuenta;
    }

    public void setnTipoAjuste(int nTipoAjuste) {
        this.nTipoAjuste = nTipoAjuste;
    }

    public void setParcial(String parcial) {
        this.parcial = parcial;
    }

    public void setPeriodo13(String periodo13) {
        this.periodo13 = periodo13;
    }

    @Override
    public String toString() {
        return "Movimiento [ADEFAS=" + ADEFAS + ", aejerciciofiscal=" + aejerciciofiscal + ", ccancelamovimiento=" + ccancelamovimiento + ", ccentrocontable=" + ccentrocontable + ", cdescripcionmovpol=" + cdescripcionmovpol + ", cfoliodocumentomovimiento=" + cfoliodocumentomovimiento + ", cmoneda=" + cmoneda + ", cramo=" + cramo + ", ctipodocumento=" + ctipodocumento + ", ctipomovimiento=" + ctipomovimiento + ", ctipopoliza=" + ctipopoliza + ", cunidadresponsable=" + cunidadresponsable + ", dconceptomovimiento=" + dconceptomovimiento + ", fmovimiento=" + fmovimiento + ", foperacionmovimiento=" + foperacionmovimiento + ", mmovimiento=" + mmovimiento + ", nConsecutivoMovimiento=" + nConsecutivoMovimiento + ", ncuenta=" + ncuenta + ", ndocrenglon=" + ndocrenglon + ", nfoliopoliza=" + nfoliopoliza + ", nsubcuenta=" + nsubcuenta + ", nTipoAjuste=" + nTipoAjuste + ", parcial=" + parcial + ", periodo13=" + periodo13 + "]";
    }
}
