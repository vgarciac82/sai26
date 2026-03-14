package com.syc.sai.contabilidad;

import java.sql.Connection;
import com.syc.contable.AccountingEngineException;
import java.util.Base64;

public abstract class CondicionCuentaContable extends CondicionContable {

    private String grupo;

    private String mensaje;

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    public String getGrupo() {
        return grupo;
    }

    public abstract void preEjecucion(Connection con) throws AccountingEngineException;

    public abstract boolean CumpleCondicion(Connection conn, CuentaContable cuenta) throws AccountingEngineException;

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getMensaje() {
        return mensaje;
    }
}
