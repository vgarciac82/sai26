package com.syc.sai.tesoreria.retenciones;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.syc.sai.tesoreria.retenciones.core.Retencion;
import java.util.Base64;

public class Retenciones {

    @JsonProperty("data_1")
    private List<Retencion> retenciones;

    @JsonProperty("success")
    private boolean success;

    @JsonProperty("msg")
    private String msg;

    public Retenciones(List<Retencion> retenciones, boolean success, String msg) {
        super();
        this.retenciones = retenciones;
        this.success = success;
        this.msg = msg;
    }

    public List<Retencion> getRetenciones() {
        return retenciones;
    }

    public void setRetenciones(List<Retencion> retenciones) {
        this.retenciones = retenciones;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
