package com.axtel.cfdi.ComplementoCombustible.vales;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import java.util.Base64;

public class Emision {

    private BigDecimal totalEmision;

    private BigDecimal ivaEmision;

    private BigDecimal subTotalEmision;

    private BigDecimal totalFiscal;

    private BigDecimal ivaFiscal;

    private BigDecimal subTotalFiscal;

    private BigDecimal totalGlobal;

    private BigDecimal ivaGlobal;

    private BigDecimal subTotalGlobal;

    private Conceptos conceptos;

    @XmlAttribute(name = "totalEmision")
    public String getTotalEmision() {
        return formatBigDecimal(totalEmision);
    }

    public void setTotalEmision(String totalEmision) {
        this.totalEmision = parseBigDecimal(totalEmision);
    }

    @XmlAttribute(name = "ivaEmision")
    public String getIvaEmision() {
        return formatBigDecimal(ivaEmision);
    }

    public void setIvaEmision(String ivaEmision) {
        this.ivaEmision = parseBigDecimal(ivaEmision);
    }

    @XmlAttribute(name = "subTotalEmision")
    public String getSubTotalEmision() {
        return formatBigDecimal(subTotalEmision);
    }

    public void setSubTotalEmision(String subTotalEmision) {
        this.subTotalEmision = parseBigDecimal(subTotalEmision);
    }

    @XmlAttribute(name = "totalFiscal")
    public String getTotalFiscal() {
        return formatBigDecimal(totalFiscal);
    }

    public void setTotalFiscal(String totalFiscal) {
        this.totalFiscal = parseBigDecimal(totalFiscal);
    }

    @XmlAttribute(name = "ivaFiscal")
    public String getIvaFiscal() {
        return formatBigDecimal(ivaFiscal);
    }

    public void setIvaFiscal(String ivaFiscal) {
        this.ivaFiscal = parseBigDecimal(ivaFiscal);
    }

    @XmlAttribute(name = "subTotalFiscal")
    public String getSubTotalFiscal() {
        return formatBigDecimal(subTotalFiscal);
    }

    public void setSubTotalFiscal(String subTotalFiscal) {
        this.subTotalFiscal = parseBigDecimal(subTotalFiscal);
    }

    @XmlAttribute(name = "totalGlobal")
    public String getTotalGlobal() {
        return formatBigDecimal(totalGlobal);
    }

    public void setTotalGlobal(String totalGlobal) {
        this.totalGlobal = parseBigDecimal(totalGlobal);
    }

    @XmlAttribute(name = "ivaGlobal")
    public String getIvaGlobal() {
        return formatBigDecimal(ivaGlobal);
    }

    public void setIvaGlobal(String ivaGlobal) {
        this.ivaGlobal = parseBigDecimal(ivaGlobal);
    }

    @XmlAttribute(name = "subTotalGlobal")
    public String getSubTotalGlobal() {
        return formatBigDecimal(subTotalGlobal);
    }

    public void setSubTotalGlobal(String subTotalGlobal) {
        this.subTotalGlobal = parseBigDecimal(subTotalGlobal);
    }

    @XmlElement(name = "conceptos")
    public Conceptos getConceptos() {
        return conceptos;
    }

    public void setConceptos(Conceptos conceptos) {
        this.conceptos = conceptos;
    }

    private String formatBigDecimal(BigDecimal value) {
        if (value == null) {
            return null;
        }
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###.00");
        return decimalFormat.format(value);
    }

    public static BigDecimal parseBigDecimal(String value) {
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
        return "Emision [totalEmision=" + totalEmision + ", ivaEmision=" + ivaEmision + ", subTotalEmision=" + subTotalEmision + ", totalFiscal=" + totalFiscal + ", ivaFiscal=" + ivaFiscal + ", subTotalFiscal=" + subTotalFiscal + ", totalGlobal=" + totalGlobal + ", ivaGlobal=" + ivaGlobal + ", subTotalGlobal=" + subTotalGlobal + "]";
    }
}
