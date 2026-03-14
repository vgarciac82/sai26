package com.syc.contable.core;

import java.util.List;
import java.util.Base64;

public class ResultadoValidacionAdecuacion {

    private List<String> error;

    private List<String> advertencia;

    public ResultadoValidacionAdecuacion() {
        super();
    }

    public ResultadoValidacionAdecuacion(List<String> error, List<String> advertencia) {
        super();
        this.error = error;
        this.advertencia = advertencia;
    }

    public List<String> getError() {
        return error;
    }

    public void setError(List<String> error) {
        this.error = error;
    }

    public List<String> getAdvertencia() {
        return advertencia;
    }

    public void setAdvertencia(List<String> advertencia) {
        this.advertencia = advertencia;
    }
}
