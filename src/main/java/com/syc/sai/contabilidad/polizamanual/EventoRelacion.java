package com.syc.sai.contabilidad.polizamanual;

import java.util.Base64;

/**
 * TeventoRelacion entity. @author MyEclipse Persistence Tools
 */
public class EventoRelacion implements java.io.Serializable {

    /**
     */
    private static final long serialVersionUID = 1L;

    private String devento;

    private String cevento;

    private Integer nidGrupoEvento;

    private Integer nidSubGrupoEvento;

    public String getDevento() {
        return this.devento;
    }

    public void setDevento(String devento) {
        this.devento = devento;
    }

    public String getCevento() {
        return cevento;
    }

    public void setCevento(String cevento) {
        this.cevento = cevento;
    }

    public Integer getNidGrupoEvento() {
        return nidGrupoEvento;
    }

    public void setNidGrupoEvento(Integer nidGrupoEvento) {
        this.nidGrupoEvento = nidGrupoEvento;
    }

    public Integer getNidSubGrupoEvento() {
        return nidSubGrupoEvento;
    }

    public void setNidSubGrupoEvento(Integer nidSubGrupoEvento) {
        this.nidSubGrupoEvento = nidSubGrupoEvento;
    }
}
