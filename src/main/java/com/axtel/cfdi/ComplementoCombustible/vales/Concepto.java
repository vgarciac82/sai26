package com.axtel.cfdi.ComplementoCombustible.vales;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlValue;
import java.util.Base64;

@XmlRootElement(name = "concepto", namespace = "http://www.edenred.com.mx/cfdi/3/")
public class Concepto {

    private int cantidad;

    private String unidad;

    private String descripcion;

    private BigDecimal valorUnitario;

    private BigDecimal importe;

    private BigDecimal tasaIva;

    private BigDecimal importeIva;

    private String additionalContent;

    @XmlValue
    public String getAdditionalContent() {
        return additionalContent;
    }

    public void setAdditionalContent(String additionalContent) {
        this.additionalContent = additionalContent;
    }

    @XmlAttribute(name = "cantidad")
    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    @XmlAttribute(name = "unidad")
    public String getUnidad() {
        return unidad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

    @XmlAttribute(name = "descripcion")
    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @XmlAttribute(name = "valorUnitario")
    public String getValorUnitario() {
        return formatBigDecimal(valorUnitario);
    }

    public void setValorUnitario(String valorUnitario) {
        this.valorUnitario = parseBigDecimal(valorUnitario);
    }

    @XmlAttribute(name = "importe")
    public String getImporte() {
        return formatBigDecimal(importe);
    }

    public void setImporte(String importe) {
        this.importe = parseBigDecimal(importe);
    }

    @XmlAttribute(name = "tasaIva")
    public String getTasaIva() {
        return formatBigDecimal(tasaIva);
    }

    public void setTasaIva(String tasaIva) {
        this.tasaIva = parseBigDecimal(tasaIva);
    }

    @XmlAttribute(name = "importeIva")
    public String getImporteIva() {
        return formatBigDecimal(importeIva);
    }

    public void setImporteIva(String importeIva) {
        this.importeIva = parseBigDecimal(importeIva);
    }

    private String formatBigDecimal(BigDecimal value) {
        if (value == null) {
            return null;
        }
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###.00");
        return decimalFormat.format(value);
    }

    private BigDecimal parseBigDecimal(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###.00");
        try {
            return new BigDecimal(decimalFormat.parse(value).doubleValue());
        } catch (ParseException e) {
            // Manejo de errores según tu lógica de
            e.printStackTrace();
            // aplicación
            return null;
        }
    }

    @Override
    public String toString() {
        return "Concepto [cantidad=" + cantidad + ", unidad=" + unidad + ", descripcion=" + descripcion + ", valorUnitario=" + valorUnitario + ", importe=" + importe + ", tasaIva=" + tasaIva + ", importeIva=" + importeIva + "]\nAditional Content:" + additionalContent;
    }
}
