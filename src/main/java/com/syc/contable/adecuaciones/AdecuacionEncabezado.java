package com.syc.contable.adecuaciones;

import java.io.Serializable;
import java.sql.Date;
import java.util.Base64;

/**
 * Bean del encabezado de una adecuacion.
 *
 * @author Vicente Garcia.
 */
public class AdecuacionEncabezado implements Serializable {

    private static final long serialVersionUID = 2630922757922707887L;

    /**
     */
    private int alertaCorreo;

    /**
     * Descripcion de la poliza
     */
    private String cDescripcionPoliza;

    /**
     * Documento Aplicado S, N
     */
    private String cDocumentoHaplicado;

    private String centroContable;

    /**
     * Clave de la afectacion
     */
    // ???
    private int claveAfectacion;

    /**
     * Mes de la Adecuacion
     */
    private int cMes;

    /**
     * Motivo de Cancelacion
     */
    private String cMotivoCancelacion;

    /**
     * Revisado
     */
    private String cRevisado;

    /**
     */
    private String cSRInterna;

    /**
     */
    private String cSuperAdecuacion;

    /**
     * Tipo de Adecuación
     */
    private String cTipoAdecuacion;

    /**
     * Tipo de Poliza
     */
    private String cTipoPoliza;

    /**
     * Unidad Contable
     */
    private String cUnidadResponsableContable;

    /**
     * Ejercicio fiscal en que se afectara.
     */
    // base
    private int ejercicioFiscal;

    // char
    /**
     * Fecha de Aplicacion
     */
    private Date fAplicacion;

    /**
     * Fecha de Cancelacion
     */
    private Date fCancelacion;

    /**
     * Fecha de Captura de la adecuacion
     */
    private Date fCarga;

    /**
     */
    private Date FMAP;

    /**
     */
    private Date FSICOP;

    /**
     */
    private int id_caso;

    /**
     * Justificacion de la adecuacion.
     */
    private String justificacion;

    /**
     */
    private String justificacionA;

    /**
     */
    private String justificacionN;

    /**
     */
    private String justificacionR;

    /**
     * Monto total de la adecuacion.
     */
    // ???
    private double montoTotal;

    /**
     * Consecutivo SICOP
     */
    private int nConsecutivoSicop;

    /**
     * Folio de la adecuacion
     */
    private int nFolioAdecuacion;

    /**
     * Folio de la Consolidacion
     */
    private int nFolioConsolidacion;

    /**
     */
    private int nFolioFIAF;

    /**
     * Folio de la Poliza
     */
    private int nFolioPoliza;

    /**
     * Folio de Cancelacion
     */
    private int nFolioPolizaCancelacion;

    /**
     * Folio MAP
     */
    private String nFolioTramiteMAP;

    /**
     * Folio SICOP
     */
    private String nFolioTramiteSicop;

    /**
     * Nivel de la adecuacion
     */
    private int nNivel;

    /**
     * Ramo
     */
    private String ramo;

    /**
     * ID de Usuario que solicita la adecuacion
     */
    private String uLogin;

    /**
     * Unidad Ejecutora del usaurio.
     */
    private String unidadEjecutora;

    public AdecuacionEncabezado() {
        super();
    }

    /**
     * Construye una nueva instancia del objeto.
     *
     * @param uLogin
     *            Login del usuario solicitante.
     * @param ejercicioFiscal
     *            Ejercicio fiscal en el que se afectara.
     * @param unidadEjecutora
     *            Unidad ejecutora del usuario.
     * @param claveAfectacion
     *            Clave de la adecuacion
     * @param ramo
     *            Ramo
     * @param montoTotal
     *            Monto total de la adecuacion.
     */
    public AdecuacionEncabezado(String uLogin, int ejercicioFiscal, String unidadEjecutora, int claveAfectacion, String ramo, Double montoTotal) {
        super();
        this.uLogin = uLogin;
        this.ejercicioFiscal = ejercicioFiscal;
        this.unidadEjecutora = unidadEjecutora;
        this.claveAfectacion = claveAfectacion;
        this.ramo = ramo;
        this.montoTotal = montoTotal;
    }

    /**
     * Construye una nueva instancia del objeto.
     *
     * @param uLogin
     *            Login del usuario solicitante.
     * @param ejercicioFiscal
     *            Ejercicio fiscal en el que se afectara.
     * @param unidadEjecutora
     *            Unidad ejecutora del usuario.
     * @param claveAfectacion
     *            Clave de la adecuacion
     * @param ramo
     *            Ramo
     * @param montoTotal
     *            Monto total de la adecuacion.
     * @param justificacion
     *            Justificacion para realizar la adecuacion.
     */
    public AdecuacionEncabezado(String uLogin, int ejercicioFiscal, String unidadEjecutora, int claveAfectacion, String ramo, double montoTotal, String justificacion) {
        super();
        this.uLogin = uLogin;
        this.ejercicioFiscal = ejercicioFiscal;
        this.unidadEjecutora = unidadEjecutora;
        this.claveAfectacion = claveAfectacion;
        this.ramo = ramo;
        this.montoTotal = montoTotal;
        this.justificacion = justificacion;
    }

    public int getAlertaCorreo() {
        return alertaCorreo;
    }

    public String getcDescripcionPoliza() {
        return cDescripcionPoliza;
    }

    public String getcDocumentoHaplicado() {
        return cDocumentoHaplicado;
    }

    public String getCentroContable() {
        return centroContable;
    }

    /**
     * @return the claveAfectacion
     */
    public int getClaveAfectacion() {
        return claveAfectacion;
    }

    public int getcMes() {
        return cMes;
    }

    public String getcMotivoCancelacion() {
        return cMotivoCancelacion;
    }

    public String getcRevisado() {
        return cRevisado;
    }

    public String getcSRInterna() {
        return cSRInterna;
    }

    public String getcSuperAdecuacion() {
        return cSuperAdecuacion;
    }

    public String getcTipoAdecuacion() {
        return cTipoAdecuacion;
    }

    public String getcTipoPoliza() {
        return cTipoPoliza;
    }

    public String getcUnidadResponsableContable() {
        return cUnidadResponsableContable;
    }

    /**
     * @return the ejercicioFiscal
     */
    public int getEjercicioFiscal() {
        return ejercicioFiscal;
    }

    public Date getfAplicacion() {
        return fAplicacion;
    }

    public Date getfCancelacion() {
        return fCancelacion;
    }

    public Date getfCarga() {
        return fCarga;
    }

    public Date getFMAP() {
        return FMAP;
    }

    public Date getFSICOP() {
        return FSICOP;
    }

    public int getId_caso() {
        return id_caso;
    }

    /**
     * @return the justificacion
     */
    public String getJustificacion() {
        return justificacion;
    }

    public String getJustificacionA() {
        return justificacionA;
    }

    public String getJustificacionN() {
        return justificacionN;
    }

    public String getJustificacionR() {
        return justificacionR;
    }

    /**
     * @return the montoTotal
     */
    public double getMontoTotal() {
        return montoTotal;
    }

    public int getnConsecutivoSicop() {
        return nConsecutivoSicop;
    }

    public int getnFolioAdecuacion() {
        return nFolioAdecuacion;
    }

    public int getnFolioConsolidacion() {
        return nFolioConsolidacion;
    }

    public int getnFolioFIAF() {
        return nFolioFIAF;
    }

    public int getnFolioPoliza() {
        return nFolioPoliza;
    }

    public int getnFolioPolizaCancelacion() {
        return nFolioPolizaCancelacion;
    }

    public String getnFolioTramiteMAP() {
        return nFolioTramiteMAP;
    }

    public String getnFolioTramiteSicop() {
        return nFolioTramiteSicop;
    }

    public int getnNivel() {
        return nNivel;
    }

    /**
     * @return the ramo
     */
    public String getRamo() {
        return ramo;
    }

    /**
     * @return the uLogin
     */
    public String getuLogin() {
        return uLogin;
    }

    /**
     * @return the unidadEjecutora
     */
    public String getUnidadEjecutora() {
        return unidadEjecutora;
    }

    public void setAlertaCorreo(int alertaCorreo) {
        this.alertaCorreo = alertaCorreo;
    }

    public void setcDescripcionPoliza(String cDescripcionPoliza) {
        this.cDescripcionPoliza = cDescripcionPoliza;
    }

    public void setcDocumentoHaplicado(String cDocumentoHaplicado) {
        this.cDocumentoHaplicado = cDocumentoHaplicado;
    }

    public void setCentroContable(String centroContable) {
        this.centroContable = centroContable;
    }

    /**
     * @param claveAfectacion
     *            the claveAfectacion to set
     */
    public void setClaveAfectacion(int claveAfectacion) {
        this.claveAfectacion = claveAfectacion;
    }

    public void setcMes(int cMes) {
        this.cMes = cMes;
    }

    public void setcMotivoCancelacion(String cMotivoCancelacion) {
        this.cMotivoCancelacion = cMotivoCancelacion;
    }

    public void setcRevisado(String cRevisado) {
        this.cRevisado = cRevisado;
    }

    public void setcSRInterna(String cSRInterna) {
        this.cSRInterna = cSRInterna;
    }

    public void setcSuperAdecuacion(String cSuperAdecuacion) {
        this.cSuperAdecuacion = cSuperAdecuacion;
    }

    public void setcTipoAdecuacion(String cTipoAdecuacion) {
        this.cTipoAdecuacion = cTipoAdecuacion;
    }

    public void setcTipoPoliza(String cTipoPoliza) {
        this.cTipoPoliza = cTipoPoliza;
    }

    public void setcUnidadResponsableContable(String cUnidadResponsableContable) {
        this.cUnidadResponsableContable = cUnidadResponsableContable;
    }

    /**
     * @param ejercicioFiscal
     *            the ejercicioFiscal to set
     */
    public void setEjercicioFiscal(int ejercicioFiscal) {
        this.ejercicioFiscal = ejercicioFiscal;
    }

    public void setfAplicacion(Date fAplicacion) {
        this.fAplicacion = fAplicacion;
    }

    public void setfCancelacion(Date fCancelacion) {
        this.fCancelacion = fCancelacion;
    }

    public void setfCarga(Date fCarga) {
        this.fCarga = fCarga;
    }

    public void setFMAP(Date fMAP) {
        FMAP = fMAP;
    }

    public void setFSICOP(Date fSICOP) {
        FSICOP = fSICOP;
    }

    public void setId_caso(int id_caso) {
        this.id_caso = id_caso;
    }

    /**
     * @param justificacion
     *            the justificacion to set
     */
    public void setJustificacion(String justificacion) {
        this.justificacion = justificacion;
    }

    public void setJustificacionA(String justificacionA) {
        this.justificacionA = justificacionA;
    }

    public void setJustificacionN(String justificacionN) {
        this.justificacionN = justificacionN;
    }

    public void setJustificacionR(String justificacionR) {
        this.justificacionR = justificacionR;
    }

    /**
     * @param total_reducciones
     *            the montoTotal to set
     */
    public void setMontoTotal(double total_reducciones) {
        this.montoTotal = total_reducciones;
    }

    public void setnConsecutivoSicop(int nConsecutivoSicop) {
        this.nConsecutivoSicop = nConsecutivoSicop;
    }

    public void setnFolioAdecuacion(int nFolioAdecuacion) {
        this.nFolioAdecuacion = nFolioAdecuacion;
    }

    public void setnFolioConsolidacion(int nFolioConsolidacion) {
        this.nFolioConsolidacion = nFolioConsolidacion;
    }

    public void setnFolioFIAF(int nFolioFIAF) {
        this.nFolioFIAF = nFolioFIAF;
    }

    public void setnFolioPoliza(int nFolioPoliza) {
        this.nFolioPoliza = nFolioPoliza;
    }

    public void setnFolioPolizaCancelacion(int nFolioPolizaCancelacion) {
        this.nFolioPolizaCancelacion = nFolioPolizaCancelacion;
    }

    public void setnFolioTramiteMAP(String nFolioTramiteMAP) {
        this.nFolioTramiteMAP = nFolioTramiteMAP;
    }

    public void setnFolioTramiteSicop(String nFolioTramiteSicop) {
        this.nFolioTramiteSicop = nFolioTramiteSicop;
    }

    public void setnNivel(int nNivel) {
        this.nNivel = nNivel;
    }

    /**
     * @param ramo
     *            the ramo to set
     */
    public void setRamo(String ramo) {
        this.ramo = ramo;
    }

    /**
     * @param uLogin
     *            the uLogin to set
     */
    public void setuLogin(String uLogin) {
        this.uLogin = uLogin;
    }

    /**
     * @param unidadEjecutora
     *            the unidadEjecutora to set
     */
    public void setUnidadEjecutora(String unidadEjecutora) {
        this.unidadEjecutora = unidadEjecutora;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
    @Override
    public String toString() {
        return "AdecuacionEncabezado [uLogin=" + uLogin + ", ejercicioFiscal=" + ejercicioFiscal + ", unidadEjecutora=" + unidadEjecutora + ", claveAfectacion=" + claveAfectacion + ", ramo=" + ramo + ", montoTotal=" + montoTotal + ", justificacion=" + justificacion + "]";
    }
}
