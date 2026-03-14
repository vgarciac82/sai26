package com.syc.sai.contabilidad.polizamanual;

import java.util.HashSet;
import java.util.Set;
import java.util.Base64;

/**
 * Tcuentas entity. @author MyEclipse Persistence Tools
 */
public class Cuentas implements java.io.Serializable {

    // Fields
    private String ncuenta;

    private String cSubcuenta;

    private String dcuenta;

    private String tipoCuenta;

    private String ncuentaPadre;

    private String tipoBalance;

    private String verificaSaldo;

    private String naturalezaCuenta;

    private Short nivelCuenta;

    private String aplicacionCuenta;

    private String ncuentaLike;

    private Integer nordenBalanza;

    private Integer nnivelBalanza;

    private String cbloqueaAbonos;

    private String cbloqueaCargos;

    private String cnivelBloqueo;

    private String cuentaBloqueada;

    private String partida;

    public String getPartida() {
        return partida;
    }

    public void setPartida(String partida) {
        this.partida = partida;
    }

    public String getNcuenta() {
        return this.ncuenta;
    }

    public void setNcuenta(String ncuenta) {
        this.ncuenta = ncuenta;
    }

    public String getDcuenta() {
        return this.dcuenta;
    }

    public void setDcuenta(String dcuenta) {
        this.dcuenta = dcuenta;
    }

    public String getTipoCuenta() {
        return this.tipoCuenta;
    }

    public void setTipoCuenta(String tipoCuenta) {
        this.tipoCuenta = tipoCuenta;
    }

    public String getNcuentaPadre() {
        return this.ncuentaPadre;
    }

    public void setNcuentaPadre(String ncuentaPadre) {
        this.ncuentaPadre = ncuentaPadre;
    }

    public String getTipoBalance() {
        return this.tipoBalance;
    }

    public void setTipoBalance(String tipoBalance) {
        this.tipoBalance = tipoBalance;
    }

    public String getVerificaSaldo() {
        return this.verificaSaldo;
    }

    public void setVerificaSaldo(String verificaSaldo) {
        this.verificaSaldo = verificaSaldo;
    }

    public String getNaturalezaCuenta() {
        return this.naturalezaCuenta;
    }

    public void setNaturalezaCuenta(String naturalezaCuenta) {
        this.naturalezaCuenta = naturalezaCuenta;
    }

    public Short getNivelCuenta() {
        return this.nivelCuenta;
    }

    public void setNivelCuenta(Short nivelCuenta) {
        this.nivelCuenta = nivelCuenta;
    }

    public String getAplicacionCuenta() {
        return this.aplicacionCuenta;
    }

    public void setAplicacionCuenta(String aplicacionCuenta) {
        this.aplicacionCuenta = aplicacionCuenta;
    }

    public String getNcuentaLike() {
        return this.ncuentaLike;
    }

    public void setNcuentaLike(String ncuentaLike) {
        this.ncuentaLike = ncuentaLike;
    }

    public Integer getNordenBalanza() {
        return this.nordenBalanza;
    }

    public void setNordenBalanza(Integer nordenBalanza) {
        this.nordenBalanza = nordenBalanza;
    }

    public Integer getNnivelBalanza() {
        return this.nnivelBalanza;
    }

    public void setNnivelBalanza(Integer nnivelBalanza) {
        this.nnivelBalanza = nnivelBalanza;
    }

    public String getCbloqueaAbonos() {
        return this.cbloqueaAbonos;
    }

    public void setCbloqueaAbonos(String cbloqueaAbonos) {
        this.cbloqueaAbonos = cbloqueaAbonos;
    }

    public String getCbloqueaCargos() {
        return this.cbloqueaCargos;
    }

    public void setCbloqueaCargos(String cbloqueaCargos) {
        this.cbloqueaCargos = cbloqueaCargos;
    }

    public String getCnivelBloqueo() {
        return this.cnivelBloqueo;
    }

    public void setCnivelBloqueo(String cnivelBloqueo) {
        this.cnivelBloqueo = cnivelBloqueo;
    }

    public String getCuentaBloqueada() {
        return this.cuentaBloqueada;
    }

    public void setCuentaBloqueada(String cuentaBloqueada) {
        this.cuentaBloqueada = cuentaBloqueada;
    }

    public String getcSubcuenta() {
        return cSubcuenta;
    }

    public void setcSubcuenta(String cSubcuenta) {
        this.cSubcuenta = cSubcuenta;
    }
}
