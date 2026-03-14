package com.syc.contable.core;

import java.io.Serializable;
import java.util.Base64;

public class Auxiliares implements Serializable {

    /**
     */
    private static final long serialVersionUID = 1L;

    /*
	private String NfolioPoliza;
	private String CcentroContable;
	private String CtipoDocumento;
	private String CdescripcionMovPol;
	private String CfolioDocumentoMovimiento;
	private String Fmovimiento;
	private String CtipoPoliza;
	private String Mmovimiento;
	private String Macumulado;
*/
    private String CtipoMovimiento;

    private String Ncuenta;

    private String Dcuenta;

    private String FauxIni;

    private String FauxFin;

    private String AuxTipo;

    private String AuxNum;

    private String AuxCxP;

    private String AuxFecha;

    private String AuxConcepto;

    private String AuxReferencia;

    private String AuxCargos;

    private String AuxAbonos;

    private String AuxSaldo;

    public String getFauxIni() {
        return FauxIni;
    }

    public void setFauxIni(String fauxIni) {
        FauxIni = fauxIni;
    }

    public String getFauxFin() {
        return FauxIni;
    }

    public void setFauxFin(String fauxFin) {
        FauxFin = fauxFin;
    }

    public String getAuxTipo() {
        return AuxTipo;
    }

    public void setAuxTipo(String auxTipo) {
        AuxTipo = auxTipo;
    }

    public String getAuxNum() {
        return AuxNum;
    }

    public void setAuxNum(String auxNum) {
        AuxNum = auxNum;
    }

    public String getAuxCxP() {
        return AuxCxP;
    }

    public void setAuxCxP(String auxCxP) {
        AuxCxP = auxCxP;
    }

    public String getAuxFecha() {
        return AuxFecha;
    }

    public void setAuxFecha(String auxFecha) {
        AuxFecha = auxFecha;
    }

    public String getAuxConcepto() {
        return AuxConcepto;
    }

    public void setAuxConcepto(String auxConcepto) {
        AuxConcepto = auxConcepto;
    }

    public String getAuxReferencia() {
        return AuxReferencia;
    }

    public void setAuxReferencia(String auxReferencia) {
        AuxReferencia = auxReferencia;
    }

    public String getAuxCargos() {
        return AuxCargos;
    }

    public void setAuxCargos(String auxCargos) {
        AuxCargos = auxCargos;
    }

    public String getAuxAbonos() {
        return AuxAbonos;
    }

    public void setAuxAbonos(String auxAbonos) {
        AuxAbonos = auxAbonos;
    }

    public String getAuxSaldo() {
        return AuxSaldo;
    }

    public void setAuxSaldo(String auxSaldo) {
        AuxSaldo = auxSaldo;
    }

    public String getNcuenta() {
        return Ncuenta;
    }

    public void setNcuenta(String ncuenta) {
        Ncuenta = ncuenta;
    }

    public String getDcuenta() {
        return Dcuenta;
    }

    public void setDcuenta(String dcuenta) {
        Dcuenta = dcuenta;
    }

    /*	
	public String getNfolioPoliza() {
		return NfolioPoliza;
	}
	public void setNfolioPoliza(String nfolioPoliza) {
		NfolioPoliza = nfolioPoliza;
	}
	public String getCcentroContable() {
		return CcentroContable;
	}
	public void setCcentroContable(String ccentroContable) {
		CcentroContable = ccentroContable;
	}
	public String getCtipoDocumento() {
		return CtipoDocumento;
	}
	public void setCtipoDocumento(String ctipoDocumento) {
		CtipoDocumento = ctipoDocumento;
	}
	public String getCdescripcionMovPol() {
		return CdescripcionMovPol;
	}
	public void setCdescripcionMovPol(String cdescripcionMovPol) {
		CdescripcionMovPol = cdescripcionMovPol;
	}
	public String getCfolioDocumentoMovimiento() {
		return CfolioDocumentoMovimiento;
	}
	public void setCfolioDocumentoMovimiento(String cfolioDocumentoMovimiento) {
		CfolioDocumentoMovimiento = cfolioDocumentoMovimiento;
	}
	public String getFmovimiento() {
		return Fmovimiento;
	}
	public void setFmovimiento(String fmovimiento) {
		Fmovimiento = fmovimiento;
	}
	public String getCtipoPoliza() {
		return CtipoPoliza;
	}
	public void setCtipoPoliza(String ctipoPoliza) {
		CtipoPoliza = ctipoPoliza;
	}
*/
    public String getCtipoMovimiento() {
        return CtipoMovimiento;
    }

    public void setCtipoMovimiento(String ctipoMovimiento) {
        CtipoMovimiento = ctipoMovimiento;
    }
    /*	
	public String getMmovimiento() {
		return Mmovimiento;
	}
	public void setMmovimiento(String mmovimiento) {
		Mmovimiento = mmovimiento;
	}
	public String getMacumulado() {
		return Macumulado;
	}
	public void setMacumulado(String macumulado) {
		Macumulado = macumulado;
	}
*/
}
