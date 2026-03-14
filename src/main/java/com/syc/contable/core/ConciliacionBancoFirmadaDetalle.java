package com.syc.contable.core;

import java.util.Date;
import com.syc.sai.procesosAutomaticos.SaldosEdoCta;
import java.util.Base64;

/**
 * @author Developer
 */
public class ConciliacionBancoFirmadaDetalle {

    private boolean edoCta;

    private Date fechaCarga;

    private int folioConciliacionFirmada;

    private int mes;

    private SaldosEdoCta saldo;

    private String usuarioCarga;

    public ConciliacionBancoFirmadaDetalle() {
        super();
    }

    public ConciliacionBancoFirmadaDetalle(int folioConciliacionFirmada, int mes, String usuarioCarga, boolean esEdoCta) {
        super();
        this.folioConciliacionFirmada = folioConciliacionFirmada;
        this.mes = mes;
        this.usuarioCarga = usuarioCarga;
        this.edoCta = esEdoCta;
    }

    public Date getFechaCarga() {
        return fechaCarga;
    }

    public int getFolioConciliacionFirmada() {
        return folioConciliacionFirmada;
    }

    public int getMes() {
        return mes;
    }

    public SaldosEdoCta getSaldo() {
        return saldo;
    }

    public String getUsuarioCarga() {
        return usuarioCarga;
    }

    public boolean isEdoCta() {
        return edoCta;
    }

    public void setEdoCta(boolean edoCta) {
        this.edoCta = edoCta;
    }

    public void setFechaCarga(Date fechaCarga) {
        this.fechaCarga = fechaCarga;
    }

    public void setFolioConciliacionFirmada(int folioConciliacionFirmada) {
        this.folioConciliacionFirmada = folioConciliacionFirmada;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public void setSaldo(SaldosEdoCta saldo) {
        this.saldo = saldo;
    }

    public void setUsuarioCarga(String usuarioCarga) {
        this.usuarioCarga = usuarioCarga;
    }

    @Override
    public String toString() {
        return "ConciliacionBancoFirmadaDetalle [edoCta=" + edoCta + ", fechaCarga=" + fechaCarga + ", folioConciliacionFirmada=" + folioConciliacionFirmada + ", mes=" + mes + ", saldo=" + saldo + ", usuarioCarga=" + usuarioCarga + "]";
    }
}
