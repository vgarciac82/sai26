package com.syc.sai.contabilidad;

import java.util.Base64;

public abstract class CondicionCierreMes extends CondicionContable {

    String mensaje;

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
