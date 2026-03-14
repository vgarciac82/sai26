package com.syc.ejercido.pagado;

import java.sql.ResultSet;
import java.util.List;
import java.util.Base64;

public class AvisoPago {

    private String caNoContrarecibo;

    private List<String> destinatarios;

    private String fechaPago;

    private String folioPago;

    private String mensaje;

    private double montoPago;

    private String tipoPago;

    public String getCaNoContrarecibo() {
        return caNoContrarecibo;
    }

    public List<String> getDestinatarios() {
        return destinatarios;
    }

    public String getFechaPago() {
        return fechaPago;
    }

    public String getFolioPago() {
        return folioPago;
    }

    public String getMensaje() {
        return mensaje;
    }

    public double getMontoPago() {
        return montoPago;
    }

    public String getTipoPago() {
        return tipoPago;
    }

    public void setCaNoContrarecibo(String caNoContrarecibo) {
        this.caNoContrarecibo = caNoContrarecibo;
    }

    public void setDestinatarios(List<String> destinatarios) {
        this.destinatarios = destinatarios;
    }

    public void setFechaPago(String fechaPago) {
        this.fechaPago = fechaPago;
    }

    public void setFolioPago(String folioPago) {
        this.folioPago = folioPago;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public void setMontoPago(double montoPago) {
        this.montoPago = montoPago;
    }

    public void setTipoPago(String tipoPago) {
        this.tipoPago = tipoPago;
    }

    public static AvisoPago instance(ResultSet rs) throws Exception {
        AvisoPago retVal = new AvisoPago();
        retVal.setCaNoContrarecibo(rs.getString("caNoContrarrecibo"));
        retVal.setDestinatarios(null);
        retVal.setFechaPago(rs.getString("fPagoSAI"));
        retVal.setFolioPago(rs.getString("nFolioPAGO"));
        retVal.setMensaje(null);
        retVal.setMontoPago(-1.0d);
        retVal.setTipoPago(rs.getString("cTipoPago"));
        return retVal;
    }
}
