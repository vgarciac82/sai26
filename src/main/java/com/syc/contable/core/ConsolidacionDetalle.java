package com.syc.contable.core;

import java.util.Base64;

public class ConsolidacionDetalle {

    private Adecuacion nFolioAdecuacion;

    private Consolidacion nFolioCONSOLIDACION;

    public Adecuacion getnFolioAdecuacion() {
        return nFolioAdecuacion;
    }

    public void setnFolioAdecuacion(Adecuacion nFolioAdecuacion) {
        this.nFolioAdecuacion = nFolioAdecuacion;
    }

    public Consolidacion getnFolioCONSOLIDACION() {
        return nFolioCONSOLIDACION;
    }

    public void setnFolioCONSOLIDACION(Consolidacion nFolioCONSOLIDACION) {
        this.nFolioCONSOLIDACION = nFolioCONSOLIDACION;
    }
}
