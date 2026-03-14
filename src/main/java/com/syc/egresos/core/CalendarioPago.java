package com.syc.egresos.core;

import java.math.BigDecimal;
import com.syc.egresos.DetallePago;
import java.util.Base64;

public class CalendarioPago extends DetallePago {

    private BigDecimal importeBrutoMes;

    private int mes;

    public BigDecimal getImporteBrutoMes() {
        return importeBrutoMes;
    }

    public int getMes() {
        return mes;
    }

    public void setImporteBrutoMes(BigDecimal importeBrutoMes) {
        this.importeBrutoMes = importeBrutoMes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    @Override
    public String toString() {
        return "CalendarioPago [importeBrutoMes=" + importeBrutoMes + ", mes=" + mes + ", EP=" + getEp() + ",  Folio Pago=" + getFolioPago() + ", Tipo Pago =" + getTipoPago() + ", Importe Bruto=" + getImporteBruto() + "]";
    }
}
