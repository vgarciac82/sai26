package com.syc.egresos.core;

import java.math.BigDecimal;
import java.util.Base64;

public class EgresoRetencion {

    private String componente;

    private String contrarecibo;

    private int folioPago;

    private String idContrato;

    private int idTipoRetencion;

    private BigDecimal importeBruto = new BigDecimal(0.00d);

    private BigDecimal importeRetencion = new BigDecimal(0.00d);

    private boolean obligatoria;

    private String partida;

    private BigDecimal porcRetencion = new BigDecimal(0.00d);

    private boolean requeridaPersonaFisica;

    private boolean requeridaPersonaMoral;

    private String tipoPago;

    private int tipoPersona;

    private String tipoRetencion;

    private String cTipoDocumento;

    public String getComponente() {
        return componente;
    }

    public String getContrarecibo() {
        return contrarecibo;
    }

    public int getFolioPago() {
        return folioPago;
    }

    public String getIdContrato() {
        return idContrato;
    }

    public int getIdTipoRetencion() {
        return idTipoRetencion;
    }

    public BigDecimal getImporteBruto() {
        return importeBruto;
    }

    public BigDecimal getImporteRetencion() {
        return importeRetencion;
    }

    public String getPartida() {
        return partida;
    }

    public BigDecimal getPorcRetencion() {
        return porcRetencion;
    }

    public String getTipoPago() {
        return tipoPago;
    }

    public int getTipoPersona() {
        return tipoPersona;
    }

    public String getTipoRetencion() {
        return tipoRetencion;
    }

    public boolean isObligatoria() {
        return obligatoria;
    }

    public boolean isRequeridaPersonaFisica() {
        return requeridaPersonaFisica;
    }

    public boolean isRequeridaPersonaMoral() {
        return requeridaPersonaMoral;
    }

    public void setComponente(String componente) {
        this.componente = componente;
    }

    public void setContrarecibo(String contrarecibo) {
        this.contrarecibo = contrarecibo;
    }

    public void setFolioPago(int folioPago) {
        this.folioPago = folioPago;
    }

    public void setIdContrato(String idContrato) {
        this.idContrato = idContrato;
    }

    public void setIdTipoRetencion(int idTipoRetencion) {
        this.idTipoRetencion = idTipoRetencion;
    }

    public void setImporteBruto(BigDecimal importeBruto) {
        this.importeBruto = importeBruto;
    }

    public void setImporteRetencion(BigDecimal importeRetencion) {
        this.importeRetencion = importeRetencion;
    }

    public void setObligatoria(boolean obligatoria) {
        this.obligatoria = obligatoria;
    }

    public void setPartida(String partida) {
        this.partida = partida;
    }

    public void setPorcRetencion(BigDecimal porcRetencion) {
        this.porcRetencion = porcRetencion;
    }

    public void setRequeridaPersonaFisica(boolean requeridaPersonaFisica) {
        this.requeridaPersonaFisica = requeridaPersonaFisica;
    }

    public void setRequeridaPersonaMoral(boolean requeridaPersonaMoral) {
        this.requeridaPersonaMoral = requeridaPersonaMoral;
    }

    public void setTipoPago(String tipoPago) {
        this.tipoPago = tipoPago;
    }

    public void setTipoPersona(int tipoPersona) {
        this.tipoPersona = tipoPersona;
    }

    public void setTipoRetencion(String tipoRetencion) {
        this.tipoRetencion = tipoRetencion;
    }

    @Override
    public String toString() {
        return "EgresoRetencion [componente=" + componente + ", contrarecibo=" + contrarecibo + ", folioPago=" + folioPago + ", idContrato=" + idContrato + ", idTipoRetencion=" + idTipoRetencion + ", importeBruto=" + importeBruto + ", importeRetencion=" + importeRetencion + ", obligatoria=" + obligatoria + ", partida=" + partida + ", porcRetencion=" + porcRetencion + ", requeridaPersonaFisica=" + requeridaPersonaFisica + ", requeridaPersonaMoral=" + requeridaPersonaMoral + ", tipoPago=" + tipoPago + ", tipoPersona=" + tipoPersona + ", tipoRetencion=" + tipoRetencion + "]";
    }

    public String getcTipoDocumento() {
        return cTipoDocumento;
    }

    public void setcTipoDocumento(String cTipoDocumento) {
        this.cTipoDocumento = cTipoDocumento;
    }
}
