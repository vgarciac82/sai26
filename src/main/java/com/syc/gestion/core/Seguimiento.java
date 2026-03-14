package com.syc.gestion.core;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Base64;

public class Seguimiento implements Serializable {

    private final static long serialVersionUID = 1;

    private int id_tc = -1;

    private int b_id_caso = -1;

    private int idOperacion = -1;

    private int b_id_caso_operacion = 1;

    private int secuencialOperacion = -1;

    private int secuencialAnterior = -1;

    private int b_id_caso_oper = -1;

    private int b_co_id_caso_oper_sigte = -1;

    private String b_c_folio;

    private String tc_gaveta_asociada;

    private int b_c_id_gabinete = -1;

    private Timestamp b_c_fecha_ini;

    private Timestamp b_co_fecha_ini;

    private String fechaRecepcion;

    private Timestamp fechaLimite;

    private String fechaOtorgada;

    private String tc_descripcion;

    private String o_nombre;

    private String b_co_responsable_ejec;

    private String b_co_operacion_sigte;

    private String b_co_responsable_sigte;

    private String responsableOperacion;

    private String b_co_observacion;

    private String estado;

    private String estadoAsunto;

    private String respuestaAsunto;

    private String respuestaParcial;

    private String rechazoResPuesta;

    private String referencia;

    private String asunto;

    private String remitente;

    private String remitenteCargo;

    private String registro;

    private String registroCargo;

    private String responsable;

    private String idResponsable;

    private String responsableCargo;

    private String idRemitente;

    //RDMB: Se agrega para el seguimiento
    private String tipo_instruccion;

    public int getIdTC() {
        return id_tc;
    }

    public void setIdTC(int id_tc) {
        this.id_tc = id_tc;
    }

    public int getIdCaso() {
        return b_id_caso;
    }

    public void setIdCaso(int id_caso) {
        this.b_id_caso = id_caso;
    }

    public int getIdCasoOperacion() {
        return b_id_caso_operacion;
    }

    public void setIdCasoOperacion(int id_caso_operacion) {
        this.b_id_caso_operacion = id_caso_operacion;
    }

    public int getIdOperacion() {
        return idOperacion;
    }

    public void setIdOperacion(int idOperacion) {
        this.idOperacion = idOperacion;
    }

    public int getSecuencialOperacion() {
        return secuencialOperacion;
    }

    public void setSecuencialOperacion(int secuencialOperacion) {
        this.secuencialOperacion = secuencialOperacion;
    }

    public int getSecuencialAnterior() {
        return secuencialAnterior;
    }

    public void setSecuencialAnterior(int secuencialAnterior) {
        this.secuencialAnterior = secuencialAnterior;
    }

    public int getPadre() {
        return b_id_caso_oper;
    }

    public void setPadre(int b_id_caso_oper) {
        this.b_id_caso_oper = b_id_caso_oper;
    }

    public int getHijo() {
        return b_co_id_caso_oper_sigte;
    }

    public void setHijo(int b_co_id_caso_oper_sigte) {
        this.b_co_id_caso_oper_sigte = b_co_id_caso_oper_sigte;
    }

    public String getFolio() {
        return b_c_folio;
    }

    public void setFolio(String b_c_folio) {
        this.b_c_folio = b_c_folio;
    }

    public String getGavetaAsociada() {
        return tc_gaveta_asociada;
    }

    public void setGavetaAsociada(String tc_gaveta_asociada) {
        this.tc_gaveta_asociada = tc_gaveta_asociada;
    }

    public int getIdGabinete() {
        return b_c_id_gabinete;
    }

    public void setIdGabinete(int b_c_id_gabinete) {
        this.b_c_id_gabinete = b_c_id_gabinete;
    }

    public Timestamp getFechaCreacion() {
        return b_c_fecha_ini;
    }

    public void setFechaCreacion(Timestamp b_c_fecha_ini) {
        this.b_c_fecha_ini = b_c_fecha_ini;
    }

    public Timestamp getFechaEnvio() {
        return b_co_fecha_ini;
    }

    public void setFechaEnvio(Timestamp b_co_fecha_ini) {
        this.b_co_fecha_ini = b_co_fecha_ini;
    }

    public String getFechaRecepcion() {
        return fechaRecepcion;
    }

    public void setFechaRecepcion(String fechaRecepcion) {
        this.fechaRecepcion = fechaRecepcion;
    }

    public Timestamp getFechaLimite() {
        return fechaLimite;
    }

    public void setFechaLimite(Timestamp fechaLimite) {
        this.fechaLimite = fechaLimite;
    }

    public String getFechaOtorgada() {
        return fechaOtorgada;
    }

    public void setFechaOtorgada(String fechaOtorgada) {
        this.fechaOtorgada = fechaOtorgada;
    }

    public String getDescripcion() {
        return tc_descripcion;
    }

    public void setDescripcion(String tc_descripcion) {
        this.tc_descripcion = tc_descripcion;
    }

    public String getOperacionEjec() {
        return o_nombre;
    }

    public void setOperacionEjec(String o_nombre) {
        this.o_nombre = o_nombre;
    }

    public String getResponsableEjec() {
        return b_co_responsable_ejec;
    }

    public void setResponsableEjec(String b_co_responsable_ejec) {
        this.b_co_responsable_ejec = b_co_responsable_ejec;
    }

    public void setResponsableOperacion(String responsableOperacion) {
        this.responsableOperacion = responsableOperacion;
    }

    public String getResponsableOperacion() {
        return responsableOperacion;
    }

    public String getOperacionSigte() {
        return b_co_operacion_sigte;
    }

    public void setOperacionSigte(String b_co_operacion_sigte) {
        this.b_co_operacion_sigte = b_co_operacion_sigte;
    }

    public String getResponsableSigte() {
        return b_co_responsable_sigte;
    }

    public void setResponsableSigte(String b_co_responsable_sigte) {
        this.b_co_responsable_sigte = b_co_responsable_sigte;
    }

    public String getObservacion() {
        return b_co_observacion == null ? "" : b_co_observacion;
    }

    public void setObservacion(String b_co_observacion) {
        this.b_co_observacion = b_co_observacion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getEstadoAsunto() {
        return estadoAsunto;
    }

    public void setEstadoAsunto(String estadoAsunto) {
        this.estadoAsunto = estadoAsunto;
    }

    public String getRespuestaAsunto() {
        return respuestaAsunto;
    }

    public void setRespuestaAsunto(String respuestaAsunto) {
        this.respuestaAsunto = respuestaAsunto;
    }

    public String getRespuestaParcial() {
        return respuestaParcial;
    }

    public void setRespuestaParcial(String respuestaParcial) {
        this.respuestaParcial = respuestaParcial;
    }

    public String getRechazoRespuesta() {
        return rechazoResPuesta;
    }

    public void setRechazoRespuesta(String rechazoResPuesta) {
        this.rechazoResPuesta = rechazoResPuesta;
    }

    // Adicionado para CNA
    private String respuesta;

    public String getRespuesta() {
        return respuesta == null ? "" : respuesta;
    }

    public void setRespuesta(String respuesta) {
        this.respuesta = respuesta;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getAsunto() {
        return asunto;
    }

    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }

    public String getRemitente() {
        return remitente;
    }

    public void setRemitente(String remitente) {
        this.remitente = remitente;
    }

    public String getRemitenteCargo() {
        return remitenteCargo;
    }

    public void setRemitenteCargo(String remitenteCargo) {
        this.remitenteCargo = remitenteCargo;
    }

    public String getRegistro() {
        return registro;
    }

    public void setRegistro(String registro) {
        this.registro = registro;
    }

    public String getRegistroCargo() {
        return registroCargo;
    }

    public void setRegistroCargo(String registroCargo) {
        this.registroCargo = registroCargo;
    }

    public String getIdResponsable() {
        return idResponsable;
    }

    public void setIdResponsable(String idResponsable) {
        this.idResponsable = idResponsable;
    }

    public String getResponsable() {
        return responsable;
    }

    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }

    public String getResponsableCargo() {
        return responsableCargo;
    }

    public void setResponsableCargo(String responsableCargo) {
        this.responsableCargo = responsableCargo;
    }

    public String getIdRemitente() {
        return idRemitente;
    }

    public void setIdRemitente(String idRemitente) {
        this.idRemitente = idRemitente;
    }

    //RDMB: Se agrega geter y seter para tipo_instruccion
    public String getTipoInstruccion() {
        return tipo_instruccion;
    }

    public void setTipoInstruccion(String idInstruccion) {
        this.tipo_instruccion = idInstruccion;
    }
    // Fin adicionado para CNA
}
