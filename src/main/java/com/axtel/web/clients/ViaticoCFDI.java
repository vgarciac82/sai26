package com.axtel.web.clients;

import java.math.BigDecimal;
import java.util.Base64;

public class ViaticoCFDI {

    int idEmpleado;

    String fechaPago = null;

    String contraRecibo = null;

    String folioSAI = null;

    String tipoDocumento = null;

    BigDecimal A200 = null;

    BigDecimal I200 = null;

    BigDecimal E200 = null;

    BigDecimal E201 = null;

    BigDecimal E202 = null;

    int subproceso = 1;

    public int getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(int idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public String getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(String fechaPago) {
        this.fechaPago = fechaPago;
    }

    public String getContraRecibo() {
        return contraRecibo;
    }

    public void setContraRecibo(String contraRecibo) {
        this.contraRecibo = contraRecibo;
    }

    public String getFolioSAI() {
        return folioSAI;
    }

    public void setFolioSAI(String folioSAI) {
        this.folioSAI = folioSAI;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public BigDecimal getA200() {
        return A200;
    }

    public void setA200(BigDecimal a200) {
        A200 = a200;
    }

    public BigDecimal getI200() {
        return I200;
    }

    public void setI200(BigDecimal i200) {
        I200 = i200;
    }

    public BigDecimal getE200() {
        return E200;
    }

    public void setE200(BigDecimal e200) {
        E200 = e200;
    }

    public BigDecimal getE201() {
        return E201;
    }

    public void setE201(BigDecimal e201) {
        E201 = e201;
    }

    public BigDecimal getE202() {
        return E202;
    }

    public void setE202(BigDecimal e202) {
        E202 = e202;
    }

    public int getSubproceso() {
        return subproceso;
    }

    public void setSubproceso(int subproceso) {
        this.subproceso = subproceso;
    }
}
