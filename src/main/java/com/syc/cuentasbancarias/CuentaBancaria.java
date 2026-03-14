package com.syc.cuentasbancarias;

import java.util.Base64;

public class CuentaBancaria {

    private String banco;

    private String clabe;

    private String cuenta;

    private String cuentaBancaria;

    private String digVerificador;

    private String folio;

    private String idBanco;

    private String plaza;

    private String RFC;

    private int statusCuenta;

    private int statusSICOP;

    private String sucursal;

    public CuentaBancaria(String clabeInterbancaria, String RFC) {
        if (clabeInterbancaria.length() != 18) {
            throw new IllegalArgumentException("La CLABE Interbancaria debe tener 18 caracteres.");
        }
        this.clabe = clabeInterbancaria;
        this.idBanco = clabeInterbancaria.substring(0, 3);
        this.plaza = clabeInterbancaria.substring(3, 6);
        this.cuentaBancaria = clabeInterbancaria.substring(6, 17);
        this.digVerificador = clabeInterbancaria.substring(17, 18);
        this.RFC = RFC;
    }

    public CuentaBancaria(String cuenta, String RFC, String banco, String plaza, String idBanco, String cuentaBancaria, String digVerificador, int statusCuenta, String sucursal, int statusSICOP, String folio) {
        this.cuenta = cuenta;
        this.RFC = RFC;
        this.banco = banco;
        this.plaza = plaza;
        this.idBanco = idBanco;
        this.cuentaBancaria = cuentaBancaria;
        this.digVerificador = digVerificador;
        this.statusCuenta = statusCuenta;
        this.sucursal = sucursal;
        this.statusSICOP = statusSICOP;
        this.folio = folio;
    }

    public String getBanco() {
        return banco;
    }

    public String getClabe() {
        return clabe;
    }

    public String getCuenta() {
        return cuenta;
    }

    public String getCuentaBancaria() {
        return cuentaBancaria;
    }

    public String getDigVerificador() {
        return digVerificador;
    }

    public String getFolio() {
        return folio;
    }

    public String getIdBanco() {
        return idBanco;
    }

    public String getPlaza() {
        return plaza;
    }

    public String getRFC() {
        return RFC;
    }

    public int getStatusCuenta() {
        return statusCuenta;
    }

    public int getStatusSICOP() {
        return statusSICOP;
    }

    public String getSucursal() {
        return sucursal;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public void setClabe(String clabe) {
        this.clabe = clabe;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    public void setCuentaBancaria(String cuentaBancaria) {
        this.cuentaBancaria = cuentaBancaria;
    }

    public void setDigVerificador(String digVerificador) {
        this.digVerificador = digVerificador;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public void setIdBanco(String idBanco) {
        this.idBanco = idBanco;
    }

    public void setPlaza(String plaza) {
        this.plaza = plaza;
    }

    public void setRFC(String RFC) {
        this.RFC = RFC;
    }

    public void setStatusCuenta(int statusCuenta) {
        this.statusCuenta = statusCuenta;
    }

    public void setStatusSICOP(int statusSICOP) {
        this.statusSICOP = statusSICOP;
    }

    public void setSucursal(String sucursal) {
        this.sucursal = sucursal;
    }

    @Override
    public String toString() {
        return "CuentaBancaria [cuenta=" + cuenta + ", RFC=" + RFC + ", banco=" + banco + ", plaza=" + plaza + ", idBanco=" + idBanco + ", cuentaBancaria=" + cuentaBancaria + ", digVerificador=" + digVerificador + ", sucursal=" + sucursal + ", folio=" + folio + ", statusCuenta=" + statusCuenta + ", statusSICOP=" + statusSICOP + "]";
    }
}
