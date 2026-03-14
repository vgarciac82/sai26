package com.syc.adquisiciones.core;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Base64;

public class APTEProveedorTotales {

    private String rfc = "";

    private String proveedor = "";

    private ArrayList<APTEProveedorPartida> partidas = new ArrayList<APTEProveedorPartida>();

    private Double subtotal = 0.0;

    private Double total = 0.0;

    private String tipoMoneda = "M.N.";

    public String getRfc() {
        return rfc;
    }

    public void setRfc(String rfc) {
        if (rfc != null)
            rfc = rfc.trim();
        this.rfc = rfc;
    }

    public void setProveedor(String proveedor) {
        this.proveedor = proveedor;
    }

    public String getProveedor() {
        return proveedor;
    }

    public ArrayList<APTEProveedorPartida> getPartidas() {
        return partidas;
    }

    public void setPartidas(ArrayList<APTEProveedorPartida> partidas) {
        this.partidas = partidas;
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public void calcTotals(Double iva, HashMap<Integer, Integer> cantidades) {
        this.subtotal = 0.0;
        if (this.partidas != null) {
            for (APTEProveedorPartida partida : this.partidas) {
                Integer cantidad = cantidades.get(partida.getIdLinea());
                if (cantidad != null) {
                    Double subtot = partida.getPrecio() * cantidad;
                    this.subtotal += subtot;
                }
            }
        }
        this.total = this.subtotal;
        if (iva != null)
            this.total *= iva;
    }

    public String getPartidasS() {
        if (this.partidas != null) {
            String value = "";
            for (APTEProveedorPartida partida : this.partidas) {
                value += partida.getIdLinea() + ",";
            }
            if (value.length() > 0)
                return value.substring(0, value.length() - 1);
            return value;
        }
        return "";
    }

    public String getSubtotalS() {
        if (this.subtotal != null) {
            String num = format(this.subtotal, 2);
            return "$ " + num + " " + this.tipoMoneda;
        }
        return "";
    }

    public String getTotalS() {
        if (this.total != null) {
            String num = format(this.total, 2);
            return "$ " + num + " " + this.tipoMoneda;
        }
        return "";
    }

    public void setTipoMoneda(String tipoMoneda) {
        this.tipoMoneda = tipoMoneda;
    }

    public String getTipoMoneda() {
        return tipoMoneda;
    }

    private String format(Double n, Integer d) {
        if (n == null)
            return "";
        NumberFormat nf = NumberFormat.getInstance(new Locale("en"));
        nf.setMaximumFractionDigits(d);
        nf.setMinimumFractionDigits(d);
        return nf.format(n);
    }
}
