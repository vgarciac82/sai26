package com.syc.gestion.core;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Base64;

public class Bitacora {

    private int id_bitacora;

    private int b_id_caso;

    private int b_id_caso_oper;

    private int b_c_id_gabinete;

    private String b_c_folio;

    private Timestamp b_c_fecha_ini;

    private int b_c_tiempo_limite;

    private int b_c_status;

    private int b_id_tc;

    private int b_id_oper;

    private Timestamp b_co_fecha_ini;

    private int b_co_tiempo_limite;

    private String b_co_responsable_ejec;

    private int b_co_id_caso_sigte;

    private String b_co_responsable_sigte;

    private String b_co_operacion_sigte;

    private String b_co_observacion;

    private int b_co_status;

    private String b_nombre_equipo;

    private String b_ip_equipo;

    public String getB_nombre_equipo() {
        return b_nombre_equipo;
    }

    public void setB_nombre_equipo(String b_nombre_equipo) {
        this.b_nombre_equipo = b_nombre_equipo;
    }

    public String getB_ip_equipo() {
        return b_ip_equipo;
    }

    public void setB_ip_equipo(String b_ip_equipo) {
        this.b_ip_equipo = b_ip_equipo;
    }

    public int getIdBitacora() {
        return id_bitacora;
    }

    public void setIdBitacora(int id_bitacora) {
        this.id_bitacora = id_bitacora;
    }

    public int getIdCaso() {
        return b_id_caso;
    }

    public void setIdCaso(int b_id_caso) {
        this.b_id_caso = b_id_caso;
    }

    public int getIdCasoOper() {
        return b_id_caso_oper;
    }

    public void setIdCasoOper(int b_id_caso_oper) {
        this.b_id_caso_oper = b_id_caso_oper;
    }

    public int getIdGabinete() {
        return b_c_id_gabinete;
    }

    public void setIdGabinete(int b_c_id_gabinete) {
        this.b_c_id_gabinete = b_c_id_gabinete;
    }

    public String getFolio() {
        return b_c_folio;
    }

    public void setFolio(String b_c_folio) {
        this.b_c_folio = b_c_folio;
    }

    public Timestamp getFechaInicioCaso() {
        return b_c_fecha_ini;
    }

    public String getFormatFechaInicioCaso(String format) {
        return (new SimpleDateFormat(format)).format(b_c_fecha_ini);
    }

    public void setFechaInicioCaso(Timestamp b_c_fecha_ini) {
        this.b_c_fecha_ini = b_c_fecha_ini;
    }

    public int getTiempoLimiteCaso() {
        return b_c_tiempo_limite;
    }

    public void setTiempoLimiteCaso(int b_c_tiempo_limite) {
        this.b_c_tiempo_limite = b_c_tiempo_limite;
    }

    public int getCasoStatus() {
        return b_c_status;
    }

    public void setCasoStatus(int b_c_status) {
        this.b_c_status = b_c_status;
    }

    public int getIdTC() {
        return b_id_tc;
    }

    public void setIdTC(int b_id_tc) {
        this.b_id_tc = b_id_tc;
    }

    public int getIdOperacion() {
        return b_id_oper;
    }

    public void setIdOperacion(int b_id_oper) {
        this.b_id_oper = b_id_oper;
    }

    public Timestamp getFechaInicioCasoOper() {
        return b_co_fecha_ini;
    }

    public String getFormatFechaInicioCasoOper(String format) {
        return (new SimpleDateFormat(format)).format(b_co_fecha_ini);
    }

    public void setFechaInicioCasoOper(Timestamp b_co_fecha_ini) {
        this.b_co_fecha_ini = b_co_fecha_ini;
    }

    public int getTiempoLimiteCasoOper() {
        return b_co_tiempo_limite;
    }

    public void setTiempoLimiteCasoOper(int b_co_tiempo_limite) {
        this.b_co_tiempo_limite = b_co_tiempo_limite;
    }

    public String getResponsableEjec() {
        return b_co_responsable_ejec;
    }

    public void setResponsableEjec(String b_co_responsable_ejec) {
        this.b_co_responsable_ejec = b_co_responsable_ejec;
    }

    public int getIdCasoOperSigte() {
        return b_co_id_caso_sigte;
    }

    public void setIdCasoOperSigte(int b_co_id_caso_sigte) {
        this.b_co_id_caso_sigte = b_co_id_caso_sigte;
    }

    public String getResponsableSigte() {
        return b_co_responsable_sigte;
    }

    public void setResponsableSigte(String b_co_responsable_sigte) {
        this.b_co_responsable_sigte = b_co_responsable_sigte;
    }

    public String getOperacionSigte() {
        return b_co_operacion_sigte;
    }

    public void setOperacionSigte(String b_co_operacion_sigte) {
        this.b_co_operacion_sigte = b_co_operacion_sigte;
    }

    public String getObservacion() {
        return b_co_observacion;
    }

    public void setObservacion(String b_co_observacion) {
        this.b_co_observacion = b_co_observacion;
    }

    public int getCasoOperacionStatus() {
        return b_co_status;
    }

    public void setCasoOperacionStatus(int b_co_status) {
        this.b_co_status = b_co_status;
    }
}
