package com.axtel.cfdi.ComplementoCombustible.custom;

import java.math.BigDecimal;
import java.util.Base64;

public class EdrConceptoDispersion {

    private String idDocumento;

    private BigDecimal importePagado;

    private BigDecimal importeSaldoAnterior;

    private BigDecimal importeSaldoInsolutoPendientePago;

    public String getIdDocumento() {
        return idDocumento;
    }

    public BigDecimal getImportePagado() {
        return importePagado;
    }

    public BigDecimal getImporteSaldoAnterior() {
        return importeSaldoAnterior;
    }

    public BigDecimal getImporteSaldoInsolutoPendientePago() {
        return importeSaldoInsolutoPendientePago;
    }

    public void setIdDocumento(String idDocumento) {
        this.idDocumento = idDocumento;
    }

    public void setImportePagado(BigDecimal importePagado) {
        this.importePagado = importePagado;
    }

    public void setImporteSaldoAnterior(BigDecimal importeSaldoAnterior) {
        this.importeSaldoAnterior = importeSaldoAnterior;
    }

    public void setImporteSaldoInsolutoPendientePago(BigDecimal importeSaldoInsolutoPendientePago) {
        this.importeSaldoInsolutoPendientePago = importeSaldoInsolutoPendientePago;
    }

    @Override
    public String toString() {
        return "EdrConceptoDispersion [idDocumento=" + idDocumento + ", importeSaldoAnterior=" + importeSaldoAnterior + ", importePagado=" + importePagado + ", importeSaldoInsolutoPendientePago=" + importeSaldoInsolutoPendientePago + "]";
    }
}
