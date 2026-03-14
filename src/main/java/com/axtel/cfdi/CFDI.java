package com.axtel.cfdi;

import java.util.List;
import com.axtel.cfdi.stamp.core.VirtualFile;
import java.util.Base64;

public class CFDI {

    public static final int AUTORIZADO = 2;

    public static final int CANCELADO = 4;

    public static final int CAPTURA = 0;

    public static final int PENDIENTE_AUTORIZACION = 1;

    public static final int RECHAZADO = 5;

    public static final int TIMBRADO = 3;

    private List<CFDIDetalle> detalles;

    private CFDIEncabezado encabezado;

    private VirtualFile filePDF;

    private VirtualFile fileXML;

    private String xmlInvoice;

    public CFDI() {
    }

    public CFDI(CFDIEncabezado encabezado, List<CFDIDetalle> detalles) {
        this.encabezado = encabezado;
        this.detalles = detalles;
    }

    public List<CFDIDetalle> getDetalles() {
        return detalles;
    }

    public CFDIEncabezado getEncabezado() {
        return encabezado;
    }

    public VirtualFile getFileXML() {
        return fileXML;
    }

    public String getXmlInvoice() {
        return xmlInvoice;
    }

    public void setDetalles(List<CFDIDetalle> detalles) {
        this.detalles = detalles;
    }

    public void setEncabezado(CFDIEncabezado encabezado) {
        this.encabezado = encabezado;
    }

    public void setFileXML(VirtualFile fileXML) {
        this.fileXML = fileXML;
    }

    public void setXmlInvoice(String xmlInvoice) {
        this.xmlInvoice = xmlInvoice;
    }

    @Override
    public String toString() {
        return "CFDI [encabezado=" + encabezado + ", detalles=" + detalles + "]";
    }

    public VirtualFile getFilePDF() {
        return filePDF;
    }

    public void setFilePDF(VirtualFile filePDF) {
        this.filePDF = filePDF;
    }
}
