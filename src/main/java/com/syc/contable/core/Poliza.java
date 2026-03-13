package com.syc.contable.core;
import java.io.Serializable;

public class Poliza {

	private final static long serialVersionUID = 1;
	private String nFolioPoliza ;
	private String fCreacion ;
	private String cDescripcionPoliza ;
	private String mTotalCargo ;
	private String mTotalAbono ;
	private String nMes ;
	private String nCuenta ;
	private String fAplicacion ;
	private String nPolizaAutomatica ;
	private String cCentroContable ;
	private String cDescripcionCentroContable ;
	private String aEjercicioFiscal ;
	private String cTipoPoliza ;
	private String cDescripcionTipoPoliza ;
	private String cTipoDocumento ;
	private String nFolioDocumento;
	private String DocHAplicado ;
	private String cUsuarioAutorizo;
	private String IdOper;
	//VARIABLES PARA CONSULTA DE CARGA DE POLIZAS POR EXCEL
	private String cDescripcionCuenta;
	private String cAuxiliar;
	private String cDescripcionMov;
	private String cParcial;
	private String cGrupo;
	private String cSubGrupo;
	private String cEvento;
	private String cPartida;
	private String cNumEvento;
	private String mCargo;
	private String mAbono;
	
	
	
	
	public String getmCargo() {
		return mCargo;
	}
	public void setmCargo(String mCargo) {
		this.mCargo = mCargo;
	}
	public String getmAbono() {
		return mAbono;
	}
	public void setmAbono(String mAbono) {
		this.mAbono = mAbono;
	}

	public String getnCuenta() {
		return nCuenta;
	}
	public void setnCuenta(String nCuenta) {
		this.nCuenta = nCuenta;
	}
	public String getcCentroContable() {
		return cCentroContable;
	}
	public void setcCentroContable(String cCentroContable) {
		this.cCentroContable = cCentroContable;
	}
	public String getnFolioDocumento() {
		return nFolioDocumento;
	}
	public void setnFolioDocumento(String nFolioDocumento) {
		this.nFolioDocumento = nFolioDocumento;
	}
	public String getcDescripcionCuenta() {
		return cDescripcionCuenta;
	}
	public void setcDescripcionCuenta(String cDescripcionCuenta) {
		this.cDescripcionCuenta = cDescripcionCuenta;
	}
	public String getcAuxiliar() {
		return cAuxiliar;
	}
	public void setcAuxiliar(String cAuxiliar) {
		this.cAuxiliar = cAuxiliar;
	}
	public String getcDescripcionMov() {
		return cDescripcionMov;
	}
	public void setcDescripcionMov(String cDescripcionMov) {
		this.cDescripcionMov = cDescripcionMov;
	}
	public String getcParcial() {
		return cParcial;
	}
	public void setcParcial(String cParcial) {
		this.cParcial = cParcial;
	}
	public String getcGrupo() {
		return cGrupo;
	}
	public void setcGrupo(String cGrupo) {
		this.cGrupo = cGrupo;
	}
	public String getcSubGrupo() {
		return cSubGrupo;
	}
	public void setcSubGrupo(String cSubGrupo) {
		this.cSubGrupo = cSubGrupo;
	}
	public String getcEvento() {
		return cEvento;
	}
	public void setcEvento(String cEvento) {
		this.cEvento = cEvento;
	}
	public String getcPartida() {
		return cPartida;
	}
	public void setcPartida(String cPartida) {
		this.cPartida = cPartida;
	}
	public String getcNumEvento() {
		return cNumEvento;
	}
	public void setcNumEvento(String cNumEvento) {
		this.cNumEvento = cNumEvento;
	}
	
	public String getIdOper() {
		return IdOper;
	}
	public void setIdOper(String idOper) {
		IdOper = idOper;
	}
	public String getNfolioPoliza() {
		return nFolioPoliza;
	}
	public void setNfolioPoliza(String nfoliopoliza) {
		nFolioPoliza = nfoliopoliza;
	}
	public String getFcreacion() {
		return fCreacion;
	}
	public void setFcreacion(String fcreacion) {
		fCreacion = fcreacion;
	}
	public String getCdescripcionPoliza() {
		return cDescripcionPoliza;
	}
	public void setCdescripcionPoliza(String cdescripcionpoliza) {
		cDescripcionPoliza = cdescripcionpoliza;
	}
	public String getMtotalCargo() {
		return mTotalCargo;
	}
	public void setMtotalCargo(String mtotalcargo) {
		mTotalCargo = mtotalcargo;
	}
	public String getMtotalAbono() {
		return mTotalAbono;
	}
	public void setMtotalAbono(String mtotalabono) {
		mTotalAbono = mtotalabono;
	}
	public String getNmes() {
		return nMes;
	}
	public void setNmes(String nmes) {
		nMes = nmes;
	}
	public String getNcuentae() {
		return nCuenta;
	}
	public void setNcuenta(String ncuenta) {
		nCuenta = ncuenta;
	}
	public String getFaplicacion() {
		return fAplicacion;
	}
	public void setFaplicacion(String faplicacion) {
		fAplicacion = faplicacion;
	}
	public String getNpolizaAutomatica() {
		return nPolizaAutomatica;
	}
	public void setNpolizaAutomatica(String npolizaautomatica) {
		nPolizaAutomatica = npolizaautomatica;
	}
	public String getCcentroContable() {
		return cCentroContable;
	}
	public void setCcentroContable(String ccentrocontable) {
		cCentroContable = ccentrocontable;
	}
	public String getCdescripcionCentroContable() {
		return cDescripcionCentroContable;
	}
	public void setCdescripcionCentroContable(String cdescripcioncentrocontable) {
		cDescripcionCentroContable = cdescripcioncentrocontable;
	}
	public String getAejercicioFiscal() {
		return aEjercicioFiscal;
	}
	public void setAejercicioFiscal(String aejerciciofiscal) {
		aEjercicioFiscal = aejerciciofiscal;
	}
	public String getCtipoPoliza() {
		return cTipoPoliza;
	}
	public void setCtipoPoliza(String ctipopoliza) {
		cTipoPoliza = ctipopoliza;
	}
	public String getCdescripcionTipoPoliza() {
		return cDescripcionTipoPoliza;
	}
	public void setCdescripcionTipoPoliza(String cdescripciontipopoliza) {
		cDescripcionTipoPoliza = cdescripciontipopoliza;
	}
	public String getCtipoDocumento() {
		return cTipoDocumento;
	}
	public void setCtipoDocumento(String ctipodocumento) {
		cTipoDocumento = ctipodocumento;
	}
	public String getNfolioDocumento() {
		return nFolioDocumento;
	}
	public void setNfolioDocumento(String nfoliodocumento) {
		nFolioDocumento = nfoliodocumento;
	}
	public String getDocHAplicado() {
		return DocHAplicado;
	}
	public void setDocHAplicado(String dochaplicado) {
		DocHAplicado = dochaplicado;
	}
	public String getCusuarioAutorizo() {
		return cUsuarioAutorizo;
	}
	public void setCusuarioAutorizo(String cusuarioautorizo) {
		cUsuarioAutorizo = cusuarioautorizo;
	}
}
