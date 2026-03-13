package com.syc.egresos.core;

import java.math.BigDecimal;
import java.util.Date;

public class RelacionGastosEncabezado {

	private String		aEjercicioFiscal;
	private String		ALM;
	private String		c_origen;
	private String		caNoAP;
	private String		caNoContrarrecibo;
	private String		capitulo;
	private String		cBoletoReservacion;
	private String		cCentroContable;
	private String		cConcepto;
	private String		cDescripcionPoliza;
	private char		cDocumentoHaplicado;
	private String		cEjercicio;
	private String		cEsFirmaElectronica;
	private String		cEvento;
	private String		cIdDistritoRiego;
	private String		cIdEntidadContable;
	private char		cIdEstadoRelacion;
	private String		cIdGEstatal;
	private String		cIdGRegional;
	private String		cIdRelacion;
	private String		cIdRFC;
	private char		cIdTipoDocumento;
	private String		cIdTipoFondo;
	private String		cIdTipoLimiteDlls;
	private char		cIdTipoMontoDesembolso;
	private char		cIdTipoRelacion;
	private String		cIdUnidadAdministrativa;
	private String		cIdUsuarioAprobacion;
	private String		cIdUsuarioCaptura;
	private String		cIdUsuarioImpresion;
	private String		cIdUsuarioRechazo;
	private String		cIdUsuarioRevision;
	private String		cInformeComision;
	private String		cMes;
	private String		cnombre;
	private String		cOficioDiferenciaCambiaria;
	private char		cPagoReferenciado;
	private char		cRadicado;
	private String		cRamo;
	private String		cReferenciaBancaria;
	private String		cReferenciaPRODDER;
	private String		cSubPrograma;
	private String		CTAB;
	private String		cTipoPoliza;
	private String		cUnidadResponsable;
	private String		cUnidadResponsableContable;
	private Date		fAplicacion;
	private Date		fCancelacion;
	private Date		fProgramadaPago;
	private Date		fRecepcion;
	private Date		fRevision;
	private String		ID_DESTINO_GASTO;
	private String		ID_TIPO_CONCEPTO;
	private String		ID_TIPO_MOVIMIENTO;
	private String		lAplicaImpuestoCedular;
	private char		lContrarreciboImpreso;
	private char		lSuficienciaAnualValidada;
	private char		lSuficienciaMensualValidada;
	private BigDecimal	mImporteBruto;
	private BigDecimal	mImporteMasIva;
	private BigDecimal	mImporteNeto;
	private BigDecimal	mImporteRetencion;
	private BigDecimal	mMontoBoleto;
	private int			nAcompanantes;
	private int			nContieneFacturas;
	private int			nEnviadoSICOP;
	private int			nEsAlimentacionBrigadistas;
	private int			nEsCertificadoTransito;
	private int			nEsComisionExtranjero;
	private int			nEsComisionNacional;
	private int			nFolioCaja;
	private int			nFolioCargaMasiva;
	private int			nFolioPoliza;
	private int			nFolioPolizaCancelacion;
	private int			nFolioRELACIONGASTOS;
	private int			nIdComision;
	private int			nIdConcepto;
	private String		nidprograma;
	private int			nNumEmpleadoAut;
	private int			nNumEmpleadoElab;
	private int			nNumEmpleadoVoBo;
	private String		nPorcImpuestoCedular;
	private String		nTipoCambio;
	private int			polManual;
	private int			reclasificada;
	private String		RFC;
	private String		sFirmanteAut;
	private String		sFirmanteEla;
	private String		sFirmanteVoBo;
	private String		sPuestoAut;
	private String		sPuestoEla;
	private String		sPuestoVoBo;
	private String		TIPO_OPERACION;
	private String		U_LOGIN;
	private int folioApartado;
	private int 		nIdComisionReloj;

	public String getaEjercicioFiscal() {
		return aEjercicioFiscal;
	}

	public String getALM() {
		return ALM;
	}

	public String getC_origen() {
		return c_origen;
	}

	public String getCaNoAP() {
		return caNoAP;
	}

	public String getCaNoContrarrecibo() {
		return caNoContrarrecibo;
	}

	public String getCapitulo() {
		return capitulo;
	}

	public String getcBoletoReservacion() {
		return cBoletoReservacion;
	}

	public String getcCentroContable() {
		return cCentroContable;
	}

	public String getcConcepto() {
		return cConcepto;
	}

	public String getcDescripcionPoliza() {
		return cDescripcionPoliza;
	}

	public char getcDocumentoHaplicado() {
		return cDocumentoHaplicado;
	}

	public String getcEjercicio() {
		return cEjercicio;
	}

	public String getcEsFirmaElectronica() {
		return cEsFirmaElectronica;
	}

	public String getcEvento() {
		return cEvento;
	}

	public String getcIdDistritoRiego() {
		return cIdDistritoRiego;
	}

	public String getcIdEntidadContable() {
		return cIdEntidadContable;
	}

	public char getcIdEstadoRelacion() {
		return cIdEstadoRelacion;
	}

	public String getcIdGEstatal() {
		return cIdGEstatal;
	}

	public String getcIdGRegional() {
		return cIdGRegional;
	}

	public String getcIdRelacion() {
		return cIdRelacion;
	}

	public String getcIdRFC() {
		return cIdRFC;
	}

	public char getcIdTipoDocumento() {
		return cIdTipoDocumento;
	}

	public String getcIdTipoFondo() {
		return cIdTipoFondo;
	}

	public String getcIdTipoLimiteDlls() {
		return cIdTipoLimiteDlls;
	}

	public char getcIdTipoMontoDesembolso() {
		return cIdTipoMontoDesembolso;
	}

	public char getcIdTipoRelacion() {
		return cIdTipoRelacion;
	}

	public String getcIdUnidadAdministrativa() {
		return cIdUnidadAdministrativa;
	}

	public String getcIdUsuarioAprobacion() {
		return cIdUsuarioAprobacion;
	}

	public String getcIdUsuarioCaptura() {
		return cIdUsuarioCaptura;
	}

	public String getcIdUsuarioImpresion() {
		return cIdUsuarioImpresion;
	}

	public String getcIdUsuarioRechazo() {
		return cIdUsuarioRechazo;
	}

	public String getcIdUsuarioRevision() {
		return cIdUsuarioRevision;
	}

	public String getcInformeComision() {
		return cInformeComision;
	}

	public String getcMes() {
		return cMes;
	}

	public String getCnombre() {
		return cnombre;
	}

	public String getcOficioDiferenciaCambiaria() {
		return cOficioDiferenciaCambiaria;
	}

	public char getcPagoReferenciado() {
		return cPagoReferenciado;
	}

	public char getcRadicado() {
		return cRadicado;
	}

	public String getcRamo() {
		return cRamo;
	}

	public String getcReferenciaBancaria() {
		return cReferenciaBancaria;
	}

	public String getcReferenciaPRODDER() {
		return cReferenciaPRODDER;
	}

	public String getcSubPrograma() {
		return cSubPrograma;
	}

	public String getCTAB() {
		return CTAB;
	}

	public String getcTipoPoliza() {
		return cTipoPoliza;
	}

	public String getcUnidadResponsable() {
		return cUnidadResponsable;
	}

	public String getcUnidadResponsableContable() {
		return cUnidadResponsableContable;
	}

	public Date getfAplicacion() {
		return fAplicacion;
	}

	public Date getfCancelacion() {
		return fCancelacion;
	}

	public Date getfProgramadaPago() {
		return fProgramadaPago;
	}

	public Date getfRecepcion() {
		return fRecepcion;
	}

	public Date getfRevision() {
		return fRevision;
	}

	public String getID_DESTINO_GASTO() {
		return ID_DESTINO_GASTO;
	}

	public String getID_TIPO_CONCEPTO() {
		return ID_TIPO_CONCEPTO;
	}

	public String getID_TIPO_MOVIMIENTO() {
		return ID_TIPO_MOVIMIENTO;
	}

	public String getlAplicaImpuestoCedular() {
		return lAplicaImpuestoCedular;
	}

	public char getlContrarreciboImpreso() {
		return lContrarreciboImpreso;
	}

	public char getlSuficienciaAnualValidada() {
		return lSuficienciaAnualValidada;
	}

	public char getlSuficienciaMensualValidada() {
		return lSuficienciaMensualValidada;
	}

	public BigDecimal getmImporteBruto() {
		return mImporteBruto;
	}

	public BigDecimal getmImporteMasIva() {
		return mImporteMasIva;
	}

	public BigDecimal getmImporteNeto() {
		return mImporteNeto;
	}

	public BigDecimal getmMontoBoleto() {
		return mMontoBoleto;
	}

	public int getnAcompanantes() {
		return nAcompanantes;
	}

	public int getnContieneFacturas() {
		return nContieneFacturas;
	}

	public int getnEnviadoSICOP() {
		return nEnviadoSICOP;
	}

	public int getnEsAlimentacionBrigadistas() {
		return nEsAlimentacionBrigadistas;
	}

	public int getnEsCertificadoTransito() {
		return nEsCertificadoTransito;
	}

	public int getnEsComisionExtranjero() {
		return nEsComisionExtranjero;
	}

	public int getnEsComisionNacional() {
		return nEsComisionNacional;
	}

	public int getnFolioCaja() {
		return nFolioCaja;
	}

	public int getnFolioCargaMasiva() {
		return nFolioCargaMasiva;
	}

	public int getnFolioPoliza() {
		return nFolioPoliza;
	}

	public int getnFolioPolizaCancelacion() {
		return nFolioPolizaCancelacion;
	}

	public int getnFolioRELACIONGASTOS() {
		return nFolioRELACIONGASTOS;
	}

	public int getnIdComision() {
		return nIdComision;
	}

	public int getnIdConcepto() {
		return nIdConcepto;
	}

	public String getNidprograma() {
		return nidprograma;
	}

	public int getnNumEmpleadoAut() {
		return nNumEmpleadoAut;
	}

	public int getnNumEmpleadoElab() {
		return nNumEmpleadoElab;
	}

	public int getnNumEmpleadoVoBo() {
		return nNumEmpleadoVoBo;
	}

	public String getnPorcImpuestoCedular() {
		return nPorcImpuestoCedular;
	}

	public String getnTipoCambio() {
		return nTipoCambio;
	}

	public int getPolManual() {
		return polManual;
	}

	public int getReclasificada() {
		return reclasificada;
	}

	public String getRFC() {
		return RFC;
	}

	public String getsFirmanteAut() {
		return sFirmanteAut;
	}

	public String getsFirmanteEla() {
		return sFirmanteEla;
	}

	public String getsFirmanteVoBo() {
		return sFirmanteVoBo;
	}

	public String getsPuestoAut() {
		return sPuestoAut;
	}

	public String getsPuestoEla() {
		return sPuestoEla;
	}

	public String getsPuestoVoBo() {
		return sPuestoVoBo;
	}

	public String getTIPO_OPERACION() {
		return TIPO_OPERACION;
	}

	public String getU_LOGIN() {
		return U_LOGIN;
	}

	public void setaEjercicioFiscal(String aEjercicioFiscal) {
		this.aEjercicioFiscal = aEjercicioFiscal;
	}

	public void setALM(String aLM) {
		ALM = aLM;
	}

	public void setC_origen(String c_origen) {
		this.c_origen = c_origen;
	}

	public void setCaNoAP(String caNoAP) {
		this.caNoAP = caNoAP;
	}

	public void setCaNoContrarrecibo(String caNoContrarrecibo) {
		this.caNoContrarrecibo = caNoContrarrecibo;
	}

	public void setCapitulo(String capitulo) {
		this.capitulo = capitulo;
	}

	public void setcBoletoReservacion(String cBoletoReservacion) {
		this.cBoletoReservacion = cBoletoReservacion;
	}

	public void setcCentroContable(String cCentroContable) {
		this.cCentroContable = cCentroContable;
	}

	public void setcConcepto(String cConcepto) {
		this.cConcepto = cConcepto;
	}

	public void setcDescripcionPoliza(String cDescripcionPoliza) {
		this.cDescripcionPoliza = cDescripcionPoliza;
	}

	public void setcDocumentoHaplicado(char cDocumentoHaplicado) {
		this.cDocumentoHaplicado = cDocumentoHaplicado;
	}

	public void setcEjercicio(String cEjercicio) {
		this.cEjercicio = cEjercicio;
	}

	public void setcEsFirmaElectronica(String cEsFirmaElectronica) {
		this.cEsFirmaElectronica = cEsFirmaElectronica;
	}

	public void setcEvento(String cEvento) {
		this.cEvento = cEvento;
	}

	public void setcIdDistritoRiego(String cIdDistritoRiego) {
		this.cIdDistritoRiego = cIdDistritoRiego;
	}

	public void setcIdEntidadContable(String cIdEntidadContable) {
		this.cIdEntidadContable = cIdEntidadContable;
	}

	public void setcIdEstadoRelacion(char cIdEstadoRelacion) {
		this.cIdEstadoRelacion = cIdEstadoRelacion;
	}

	public void setcIdGEstatal(String cIdGEstatal) {
		this.cIdGEstatal = cIdGEstatal;
	}

	public void setcIdGRegional(String cIdGRegional) {
		this.cIdGRegional = cIdGRegional;
	}

	public void setcIdRelacion(String cIdRelacion) {
		this.cIdRelacion = cIdRelacion;
	}

	public void setcIdRFC(String cIdRFC) {
		this.cIdRFC = cIdRFC;
	}

	public void setcIdTipoDocumento(char cIdTipoDocumento) {
		this.cIdTipoDocumento = cIdTipoDocumento;
	}

	public void setcIdTipoFondo(String cIdTipoFondo) {
		this.cIdTipoFondo = cIdTipoFondo;
	}

	public void setcIdTipoLimiteDlls(String cIdTipoLimiteDlls) {
		this.cIdTipoLimiteDlls = cIdTipoLimiteDlls;
	}

	public void setcIdTipoMontoDesembolso(char cIdTipoMontoDesembolso) {
		this.cIdTipoMontoDesembolso = cIdTipoMontoDesembolso;
	}

	public void setcIdTipoRelacion(char cIdTipoRelacion) {
		this.cIdTipoRelacion = cIdTipoRelacion;
	}

	public void setcIdUnidadAdministrativa(String cIdUnidadAdministrativa) {
		this.cIdUnidadAdministrativa = cIdUnidadAdministrativa;
	}

	public void setcIdUsuarioAprobacion(String cIdUsuarioAprobacion) {
		this.cIdUsuarioAprobacion = cIdUsuarioAprobacion;
	}

	public void setcIdUsuarioCaptura(String cIdUsuarioCaptura) {
		this.cIdUsuarioCaptura = cIdUsuarioCaptura;
	}

	public void setcIdUsuarioImpresion(String cIdUsuarioImpresion) {
		this.cIdUsuarioImpresion = cIdUsuarioImpresion;
	}

	public void setcIdUsuarioRechazo(String cIdUsuarioRechazo) {
		this.cIdUsuarioRechazo = cIdUsuarioRechazo;
	}

	public void setcIdUsuarioRevision(String cIdUsuarioRevision) {
		this.cIdUsuarioRevision = cIdUsuarioRevision;
	}

	public void setcInformeComision(String cInformeComision) {
		this.cInformeComision = cInformeComision;
	}

	public void setcMes(String cMes) {
		this.cMes = cMes;
	}

	public void setCnombre(String cnombre) {
		this.cnombre = cnombre;
	}

	public void setcOficioDiferenciaCambiaria(String cOficioDiferenciaCambiaria) {
		this.cOficioDiferenciaCambiaria = cOficioDiferenciaCambiaria;
	}

	public void setcPagoReferenciado(char cPagoReferenciado) {
		this.cPagoReferenciado = cPagoReferenciado;
	}

	public void setcRadicado(char cRadicado) {
		this.cRadicado = cRadicado;
	}

	public void setcRamo(String cRamo) {
		this.cRamo = cRamo;
	}

	public void setcReferenciaBancaria(String cReferenciaBancaria) {
		this.cReferenciaBancaria = cReferenciaBancaria;
	}

	public void setcReferenciaPRODDER(String cReferenciaPRODDER) {
		this.cReferenciaPRODDER = cReferenciaPRODDER;
	}

	public void setcSubPrograma(String cSubPrograma) {
		this.cSubPrograma = cSubPrograma;
	}

	public void setCTAB(String cTAB) {
		CTAB = cTAB;
	}

	public void setcTipoPoliza(String cTipoPoliza) {
		this.cTipoPoliza = cTipoPoliza;
	}

	public void setcUnidadResponsable(String cUnidadResponsable) {
		this.cUnidadResponsable = cUnidadResponsable;
	}

	public void setcUnidadResponsableContable(String cUnidadResponsableContable) {
		this.cUnidadResponsableContable = cUnidadResponsableContable;
	}

	public void setfAplicacion(Date fAplicacion) {
		this.fAplicacion = fAplicacion;
	}

	public void setfCancelacion(Date fCancelacion) {
		this.fCancelacion = fCancelacion;
	}

	public void setfProgramadaPago(Date fProgramadaPago) {
		this.fProgramadaPago = fProgramadaPago;
	}

	public void setfRecepcion(Date fRecepcion) {
		this.fRecepcion = fRecepcion;
	}

	public void setfRevision(Date fRevision) {
		this.fRevision = fRevision;
	}

	public void setID_DESTINO_GASTO(String iD_DESTINO_GASTO) {
		ID_DESTINO_GASTO = iD_DESTINO_GASTO;
	}

	public void setID_TIPO_CONCEPTO(String iD_TIPO_CONCEPTO) {
		ID_TIPO_CONCEPTO = iD_TIPO_CONCEPTO;
	}

	public void setID_TIPO_MOVIMIENTO(String iD_TIPO_MOVIMIENTO) {
		ID_TIPO_MOVIMIENTO = iD_TIPO_MOVIMIENTO;
	}

	public void setlAplicaImpuestoCedular(String lAplicaImpuestoCedular) {
		this.lAplicaImpuestoCedular = lAplicaImpuestoCedular;
	}

	public void setlContrarreciboImpreso(char lContrarreciboImpreso) {
		this.lContrarreciboImpreso = lContrarreciboImpreso;
	}

	public void setlSuficienciaAnualValidada(char lSuficienciaAnualValidada) {
		this.lSuficienciaAnualValidada = lSuficienciaAnualValidada;
	}

	public void setlSuficienciaMensualValidada(char lSuficienciaMensualValidada) {
		this.lSuficienciaMensualValidada = lSuficienciaMensualValidada;
	}

	public void setmImporteBruto(BigDecimal mImporteBruto) {
		this.mImporteBruto = mImporteBruto;
	}

	public void setmImporteMasIva(BigDecimal mImporteMasIva) {
		this.mImporteMasIva = mImporteMasIva;
	}

	public void setmImporteNeto(BigDecimal mImporteNeto) {
		this.mImporteNeto = mImporteNeto;
	}

	public void setmMontoBoleto(BigDecimal mMontoBoleto) {
		this.mMontoBoleto = mMontoBoleto;
	}

	public void setnAcompanantes(int nAcompanantes) {
		this.nAcompanantes = nAcompanantes;
	}

	public void setnContieneFacturas(int nContieneFacturas) {
		this.nContieneFacturas = nContieneFacturas;
	}

	public void setnEnviadoSICOP(int nEnviadoSICOP) {
		this.nEnviadoSICOP = nEnviadoSICOP;
	}

	public void setnEsAlimentacionBrigadistas(int nEsAlimentacionBrigadistas) {
		this.nEsAlimentacionBrigadistas = nEsAlimentacionBrigadistas;
	}

	public void setnEsCertificadoTransito(int nEsCertificadoTransito) {
		this.nEsCertificadoTransito = nEsCertificadoTransito;
	}

	public void setnEsComisionExtranjero(int nEsComisionExtranjero) {
		this.nEsComisionExtranjero = nEsComisionExtranjero;
	}

	public void setnEsComisionNacional(int nEsComisionNacional) {
		this.nEsComisionNacional = nEsComisionNacional;
	}

	public void setnFolioCaja(int nFolioCaja) {
		this.nFolioCaja = nFolioCaja;
	}

	public void setnFolioCargaMasiva(int nFolioCargaMasiva) {
		this.nFolioCargaMasiva = nFolioCargaMasiva;
	}

	public void setnFolioPoliza(int nFolioPoliza) {
		this.nFolioPoliza = nFolioPoliza;
	}

	public void setnFolioPolizaCancelacion(int nFolioPolizaCancelacion) {
		this.nFolioPolizaCancelacion = nFolioPolizaCancelacion;
	}

	public void setnFolioRELACIONGASTOS(int nFolioRELACIONGASTOS) {
		this.nFolioRELACIONGASTOS = nFolioRELACIONGASTOS;
	}

	public void setnIdComision(int nIdComision) {
		this.nIdComision = nIdComision;
	}

	public void setnIdConcepto(int nIdConcepto) {
		this.nIdConcepto = nIdConcepto;
	}

	public void setNidprograma(String nidprograma) {
		this.nidprograma = nidprograma;
	}

	public void setnNumEmpleadoAut(int nNumEmpleadoAut) {
		this.nNumEmpleadoAut = nNumEmpleadoAut;
	}

	public void setnNumEmpleadoElab(int nNumEmpleadoElab) {
		this.nNumEmpleadoElab = nNumEmpleadoElab;
	}

	public void setnNumEmpleadoVoBo(int nNumEmpleadoVoBo) {
		this.nNumEmpleadoVoBo = nNumEmpleadoVoBo;
	}

	public void setnPorcImpuestoCedular(String nPorcImpuestoCedular) {
		this.nPorcImpuestoCedular = nPorcImpuestoCedular;
	}

	public void setnTipoCambio(String nTipoCambio) {
		this.nTipoCambio = nTipoCambio;
	}

	public void setPolManual(int polManual) {
		this.polManual = polManual;
	}

	public void setReclasificada(int reclasificada) {
		this.reclasificada = reclasificada;
	}

	public void setRFC(String rFC) {
		RFC = rFC;
	}

	public void setsFirmanteAut(String sFirmanteAut) {
		this.sFirmanteAut = sFirmanteAut;
	}

	public void setsFirmanteEla(String sFirmanteEla) {
		this.sFirmanteEla = sFirmanteEla;
	}

	public void setsFirmanteVoBo(String sFirmanteVoBo) {
		this.sFirmanteVoBo = sFirmanteVoBo;
	}

	public void setsPuestoAut(String sPuestoAut) {
		this.sPuestoAut = sPuestoAut;
	}

	public void setsPuestoEla(String sPuestoEla) {
		this.sPuestoEla = sPuestoEla;
	}

	public void setsPuestoVoBo(String sPuestoVoBo) {
		this.sPuestoVoBo = sPuestoVoBo;
	}

	public void setTIPO_OPERACION(String tIPO_OPERACION) {
		TIPO_OPERACION = tIPO_OPERACION;
	}

	public void setU_LOGIN(String u_LOGIN) {
		U_LOGIN = u_LOGIN;
	}

	public int getFolioApartado() {
		return this.folioApartado;
	}

	public void setFolioApartado( int folioApartado ) {
		this.folioApartado = folioApartado;
	}

	@Override
	public String toString() {
		return "RelacionGastosEncabezado [aEjercicioFiscal=" + aEjercicioFiscal + ", ALM=" + ALM + ", c_origen=" + c_origen + ", caNoAP=" + caNoAP + ", caNoContrarrecibo=" + caNoContrarrecibo + ", capitulo=" + capitulo + ", cBoletoReservacion=" + cBoletoReservacion + ", cCentroContable=" + cCentroContable + ", cConcepto=" + cConcepto + ", cDescripcionPoliza=" + cDescripcionPoliza + ", cDocumentoHaplicado=" + cDocumentoHaplicado + ", cEjercicio=" + cEjercicio + ", cEsFirmaElectronica=" + cEsFirmaElectronica + ", cEvento=" + cEvento + ", cIdDistritoRiego=" + cIdDistritoRiego + ", cIdEntidadContable=" + cIdEntidadContable + ", cIdEstadoRelacion=" + cIdEstadoRelacion + ", cIdGEstatal=" + cIdGEstatal + ", cIdGRegional=" + cIdGRegional + ", cIdRelacion=" + cIdRelacion + ", cIdRFC=" + cIdRFC + ", cIdTipoDocumento=" + cIdTipoDocumento + ", cIdTipoFondo=" + cIdTipoFondo + ", cIdTipoLimiteDlls=" + cIdTipoLimiteDlls + ", cIdTipoMontoDesembolso=" + cIdTipoMontoDesembolso + ", cIdTipoRelacion=" + cIdTipoRelacion + ", cIdUnidadAdministrativa=" + cIdUnidadAdministrativa + ", cIdUsuarioAprobacion=" + cIdUsuarioAprobacion + ", cIdUsuarioCaptura=" + cIdUsuarioCaptura + ", cIdUsuarioImpresion=" + cIdUsuarioImpresion + ", cIdUsuarioRechazo=" + cIdUsuarioRechazo + ", cIdUsuarioRevision=" + cIdUsuarioRevision + ", cInformeComision=" + cInformeComision + ", cMes=" + cMes + ", cnombre=" + cnombre + ", cOficioDiferenciaCambiaria=" + cOficioDiferenciaCambiaria + ", cPagoReferenciado=" + cPagoReferenciado + ", cRadicado=" + cRadicado + ", cRamo=" + cRamo + ", cReferenciaBancaria=" + cReferenciaBancaria + ", cReferenciaPRODDER=" + cReferenciaPRODDER + ", cSubPrograma=" + cSubPrograma + ", CTAB=" + CTAB + ", cTipoPoliza=" + cTipoPoliza + ", cUnidadResponsable=" + cUnidadResponsable + ", cUnidadResponsableContable=" + cUnidadResponsableContable + ", fAplicacion=" + fAplicacion + ", fCancelacion=" + fCancelacion + ", fProgramadaPago=" + fProgramadaPago + ", fRecepcion=" + fRecepcion + ", fRevision=" + fRevision + ", ID_DESTINO_GASTO=" + ID_DESTINO_GASTO + ", ID_TIPO_CONCEPTO=" + ID_TIPO_CONCEPTO + ", ID_TIPO_MOVIMIENTO=" + ID_TIPO_MOVIMIENTO + ", lAplicaImpuestoCedular=" + lAplicaImpuestoCedular + ", lContrarreciboImpreso=" + lContrarreciboImpreso + ", lSuficienciaAnualValidada=" + lSuficienciaAnualValidada + ", lSuficienciaMensualValidada=" + lSuficienciaMensualValidada + ", mImporteBruto=" + mImporteBruto + ", mImporteMasIva=" + mImporteMasIva + ", mImporteNeto=" + mImporteNeto + ", mMontoBoleto=" + mMontoBoleto + ", nAcompanantes=" + nAcompanantes + ", nContieneFacturas=" + nContieneFacturas + ", nEnviadoSICOP=" + nEnviadoSICOP + ", nEsAlimentacionBrigadistas=" + nEsAlimentacionBrigadistas + ", nEsCertificadoTransito=" + nEsCertificadoTransito + ", nEsComisionExtranjero=" + nEsComisionExtranjero + ", nEsComisionNacional=" + nEsComisionNacional + ", nFolioCaja=" + nFolioCaja + ", nFolioCargaMasiva=" + nFolioCargaMasiva + ", nFolioPoliza=" + nFolioPoliza + ", nFolioPolizaCancelacion=" + nFolioPolizaCancelacion + ", nFolioRELACIONGASTOS=" + nFolioRELACIONGASTOS + ", nIdComision=" + nIdComision + ", nIdConcepto=" + nIdConcepto + ", nidprograma=" + nidprograma + ", nNumEmpleadoAut=" + nNumEmpleadoAut + ", nNumEmpleadoElab=" + nNumEmpleadoElab + ", nNumEmpleadoVoBo=" + nNumEmpleadoVoBo + ", nPorcImpuestoCedular=" + nPorcImpuestoCedular + ", nTipoCambio=" + nTipoCambio + ", polManual=" + polManual + ", reclasificada=" + reclasificada + ", RFC=" + RFC + ", sFirmanteAut=" + sFirmanteAut + ", sFirmanteEla=" + sFirmanteEla + ", sFirmanteVoBo=" + sFirmanteVoBo + ", sPuestoAut=" + sPuestoAut + ", sPuestoEla=" + sPuestoEla + ", sPuestoVoBo=" + sPuestoVoBo + ", TIPO_OPERACION=" + TIPO_OPERACION + ", U_LOGIN=" + U_LOGIN + ", folioApartado=" + folioApartado + "]";
	}

	public BigDecimal getmImporteRetencion() {
		return mImporteRetencion;
	}

	public void setmImporteRetencion( BigDecimal mImporteRetencion ) {
		this.mImporteRetencion = mImporteRetencion;
	}

	public int getnIdComisionReloj() {
		return nIdComisionReloj;
	}

	public void setnIdComisionReloj( int nIdComisionReloj ) {
		this.nIdComisionReloj = nIdComisionReloj;
	}
	
	
}
