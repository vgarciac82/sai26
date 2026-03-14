package com.syc.contable.caja.core;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.GregorianCalendar;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.obrapublica.EjercicioFiscalBusinessLogic;
import java.util.Base64;

public class SolicitudNoPresupuestalEncabezado {

    private String centroContable;

    private String comprobado;

    private String descripcionPoliza;

    private String documentoHAplicado;

    private String ejercicioFiscal;

    private String fechaAplicacion;

    private String fechaCancelacion;

    private String fechaCreacion;

    private String firmanteAut;

    private String firmanteVoBo;

    private int folioCaja;

    private int folioComprobacion;

    private int folioPoliza;

    private String folioPolizaCancelacion;

    private int idCaso;

    private int idGrupoEvento;

    private String maternoBenCheque;

    private int mes;

    private BigDecimal montoSolicitud;

    private String motivoRechazo;

    private String nombreBenCheque;

    private String paternoBenCheque;

    private String puestoAut;

    private String puestoVoBo;

    private String ramo;

    private String tipoPoliza;

    private String uLogin;

    private String unidadEjecutora;

    private String unidadResponsableContable;

    public String getCentroContable() {
        return centroContable;
    }

    public String getComprobado() {
        return comprobado;
    }

    public String getDescripcionPoliza() {
        return descripcionPoliza;
    }

    public String getDocumentoHAplicado() {
        return documentoHAplicado;
    }

    public String getEjercicioFiscal() {
        return ejercicioFiscal;
    }

    public String getFechaAplicacion() {
        return fechaAplicacion;
    }

    public String getFechaCancelacion() {
        return fechaCancelacion;
    }

    public String getFechaCreacion() {
        return fechaCreacion;
    }

    public String getFirmanteAut() {
        return firmanteAut;
    }

    public String getFirmanteVoBo() {
        return firmanteVoBo;
    }

    public int getFolioCaja() {
        return folioCaja;
    }

    public int getFolioComprobacion() {
        return folioComprobacion;
    }

    public int getFolioPoliza() {
        return folioPoliza;
    }

    public String getFolioPolizaCancelacion() {
        return folioPolizaCancelacion;
    }

    public int getIdCaso() {
        return idCaso;
    }

    public int getIdGrupoEvento() {
        return idGrupoEvento;
    }

    public String getMaternoBenCheque() {
        return maternoBenCheque;
    }

    public int getMes() {
        return mes;
    }

    public BigDecimal getMontoSolicitud() {
        return montoSolicitud;
    }

    public String getMotivoRechazo() {
        return motivoRechazo;
    }

    public String getNombreBenCheque() {
        return nombreBenCheque;
    }

    public String getPaternoBenCheque() {
        return paternoBenCheque;
    }

    public String getPuestoAut() {
        return puestoAut;
    }

    public String getPuestoVoBo() {
        return puestoVoBo;
    }

    public String getRamo() {
        return ramo;
    }

    public String getTipoPoliza() {
        return tipoPoliza;
    }

    public String getuLogin() {
        return uLogin;
    }

    public String getUnidadEjecutora() {
        return unidadEjecutora;
    }

    public String getUnidadResponsableContable() {
        return unidadResponsableContable;
    }

    public void setCentroContable(String centroContable) {
        this.centroContable = centroContable;
    }

    public void setComprobado(String comprobado) {
        this.comprobado = comprobado;
    }

    public void setDescripcionPoliza(String descripcionPoliza) {
        this.descripcionPoliza = descripcionPoliza;
    }

    public void setDocumentoHAplicado(String documentoHAplicado) {
        this.documentoHAplicado = documentoHAplicado;
    }

    public void setEjercicioFiscal(String ejercicioFiscal) {
        this.ejercicioFiscal = ejercicioFiscal;
    }

    public void setFechaAplicacion(String fechaAplicacion) {
        this.fechaAplicacion = fechaAplicacion;
    }

    public void setFechaCancelacion(String fechaCancelacion) {
        this.fechaCancelacion = fechaCancelacion;
    }

    public void setFechaCreacion(String fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public void setFirmanteAut(String firmanteAut) {
        this.firmanteAut = firmanteAut;
    }

    public void setFirmanteVoBo(String firmanteVoBo) {
        this.firmanteVoBo = firmanteVoBo;
    }

    public void setFolioCaja(int folioCaja) {
        this.folioCaja = folioCaja;
    }

    public void setFolioComprobacion(int folioComprobacion) {
        this.folioComprobacion = folioComprobacion;
    }

    public void setFolioPoliza(int folioPoliza) {
        this.folioPoliza = folioPoliza;
    }

    public void setFolioPolizaCancelacion(String folioPolizaCancelacion) {
        this.folioPolizaCancelacion = folioPolizaCancelacion;
    }

    public void setIdCaso(int idCaso) {
        this.idCaso = idCaso;
    }

    public void setIdGrupoEvento(int idGrupoEvento) {
        this.idGrupoEvento = idGrupoEvento;
    }

    public void setMaternoBenCheque(String maternoBenCheque) {
        this.maternoBenCheque = maternoBenCheque;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public void setMontoSolicitud(BigDecimal montoSolicitud) {
        this.montoSolicitud = montoSolicitud;
    }

    public void setMotivoRechazo(String motivoRechazo) {
        this.motivoRechazo = motivoRechazo;
    }

    public void setNombreBenCheque(String nombreBenCheque) {
        this.nombreBenCheque = nombreBenCheque;
    }

    public void setPaternoBenCheque(String paternoBenCheque) {
        this.paternoBenCheque = paternoBenCheque;
    }

    public void setPuestoAut(String puestoAut) {
        this.puestoAut = puestoAut;
    }

    public void setPuestoVoBo(String puestoVoBo) {
        this.puestoVoBo = puestoVoBo;
    }

    public void setRamo(String ramo) {
        this.ramo = ramo;
    }

    public void setTipoPoliza(String tipoPoliza) {
        this.tipoPoliza = tipoPoliza;
    }

    public void setuLogin(String uLogin) {
        this.uLogin = uLogin;
    }

    public void setUnidadEjecutora(String unidadEjecutora) {
        this.unidadEjecutora = unidadEjecutora;
    }

    public void setUnidadResponsableContable(String unidadResponsableContable) {
        this.unidadResponsableContable = unidadResponsableContable;
    }

    @Override
    public String toString() {
        return "SolicitudNoPresupuestalEncabezado [folioCaja=" + folioCaja + ", fechaCreacion=" + fechaCreacion + ", fechaAplicacion=" + fechaAplicacion + ", mes=" + mes + ", tipoPoliza=" + tipoPoliza + ", folioPoliza=" + folioPoliza + ", descripcionPoliza=" + descripcionPoliza + ", uLogin=" + uLogin + ", documentoHAplicado=" + documentoHAplicado + ", motivoRechazo=" + motivoRechazo + ", unidadResponsableContable=" + unidadResponsableContable + ", folioPolizaCancelacion=" + folioPolizaCancelacion + ", fechaCancelacion=" + fechaCancelacion + ", ejercicioFiscal=" + ejercicioFiscal + ", ramo=" + ramo + ", unidadEjecutora=" + unidadEjecutora + ", firmanteVoBo=" + firmanteVoBo + ", puestoVoBo=" + puestoVoBo + ", firmanteAut=" + firmanteAut + ", puestoAut=" + puestoAut + ", idCaso=" + idCaso + ", idGrupoEvento=" + idGrupoEvento + ", montoSolicitud=" + montoSolicitud + ", folioComprobacion=" + folioComprobacion + ", nombreBenCheque=" + nombreBenCheque + ", paternoBenCheque=" + paternoBenCheque + ", maternoBenCheque=" + maternoBenCheque + ", comprobado=" + comprobado + "]";
    }

    public static SolicitudNoPresupuestalEncabezado instaceFromExcel(Row fila, Usuario u) throws Exception {
        EjercicioFiscalBusinessLogic efbl = new EjercicioFiscalBusinessLogic(GestionInterface.ATT_CONEXION);
        SolicitudNoPresupuestalEncabezado snp = new SolicitudNoPresupuestalEncabezado();
        Calendar c = new GregorianCalendar();
        String ef = efbl.getEjercicioFiscalActivo().getaEjercicioFiscal();
        snp.setCentroContable(u.getPropiedad("CCENTROCONTABLE").getValor());
        snp.setComprobado("0");
        snp.setDescripcionPoliza(fila.getCell(2).getStringCellValue());
        snp.setDocumentoHAplicado(null);
        snp.setEjercicioFiscal(ef);
        snp.setFechaAplicacion(Util.getToday());
        snp.setFechaCancelacion(null);
        snp.setFechaCreacion(Util.getToday());
        snp.setFirmanteVoBo(fila.getCell(3).getStringCellValue());
        snp.setPuestoVoBo(fila.getCell(4).getStringCellValue());
        snp.setFirmanteAut(fila.getCell(5).getStringCellValue());
        snp.setPuestoAut(fila.getCell(6).getStringCellValue());
        snp.setFolioComprobacion(0);
        snp.setTipoPoliza(fila.getCell(1).getStringCellValue());
        snp.setMes(c.get(Calendar.MONTH) + 1);
        snp.setRamo("16");
        snp.setMontoSolicitud((new BigDecimal(fila.getCell(7).getNumericCellValue())).setScale(2, RoundingMode.HALF_UP));
        if (fila.getCell(8) != null)
            snp.setNombreBenCheque(StringUtils.trimToNull(fila.getCell(8).getStringCellValue()));
        if (fila.getCell(9) != null)
            snp.setPaternoBenCheque(StringUtils.trimToNull(fila.getCell(9).getStringCellValue()));
        if (fila.getCell(10) != null)
            snp.setMaternoBenCheque(StringUtils.trimToNull(fila.getCell(10).getStringCellValue()));
        snp.setuLogin(u.getLogin());
        snp.setUnidadEjecutora(u.getU_UR());
        snp.setUnidadResponsableContable("RHQ");
        return snp;
    }
}
