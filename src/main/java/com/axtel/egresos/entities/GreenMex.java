package com.axtel.egresos.entities;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Base64;

public class GreenMex {

    private int nFolioCaja;

    private int nFolioComprobacion;

    private int nDocRenglon;

    private Date fFechaComprobacion;

    private BigDecimal mMontoComprobacion;

    private String cdocumenteohaplicado;

    public int getnFolioCaja() {
        return nFolioCaja;
    }

    public void setnFolioCaja(int nFolioCaja) {
        this.nFolioCaja = nFolioCaja;
    }

    public int getnFolioComprobacion() {
        return nFolioComprobacion;
    }

    public void setnFolioComprobacion(int nFolioComprobacion) {
        this.nFolioComprobacion = nFolioComprobacion;
    }

    public int getnDocRenglon() {
        return nDocRenglon;
    }

    public void setnDocRenglon(int nDocRenglon) {
        this.nDocRenglon = nDocRenglon;
    }

    public Date getfFechaComprobacion() {
        return fFechaComprobacion;
    }

    public void setfFechaComprobacion(Date fFechaComprobacion) {
        this.fFechaComprobacion = fFechaComprobacion;
    }

    public BigDecimal getmMontoComprobacion() {
        return mMontoComprobacion;
    }

    public void setmMontoComprobacion(BigDecimal mMontoComprobacion) {
        this.mMontoComprobacion = mMontoComprobacion;
    }

    public String getCdocumenteohaplicado() {
        return cdocumenteohaplicado;
    }

    public void setCdocumenteohaplicado(String cdocumenteohaplicado) {
        this.cdocumenteohaplicado = cdocumenteohaplicado;
    }
}
