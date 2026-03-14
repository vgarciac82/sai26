package com.syc.gestion.reportes.core;

import java.io.Serializable;
import java.util.Base64;

public class Acumulado implements Serializable {

    private final static long serialVersionUID = 1;

    private String id;

    private String descripcion;

    private int vencidos_numero;

    private int vencidos_porcentaje;

    private int novencidos_numero;

    private int novencidos_porcentaje;

    private int pendientes;

    private int concluidos;

    private int concluidos_porcentaje;

    private int total_turnos;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String desc) {
        this.descripcion = desc;
    }

    public int getVencidosNumero() {
        return vencidos_numero;
    }

    public void setVencidosNumero(int vencidos_numero) {
        this.vencidos_numero = vencidos_numero;
    }

    public int getVencidosPorcentaje() {
        return vencidos_porcentaje;
    }

    public void setVencidosPorcentaje(int vencidos_porcentaje) {
        this.vencidos_porcentaje = vencidos_porcentaje;
    }

    public int getNovencidosNumero() {
        return novencidos_numero;
    }

    public void setNovencidosNumero(int novencidos_numero) {
        this.novencidos_numero = novencidos_numero;
    }

    public int getNovencidosPorcentaje() {
        return novencidos_porcentaje;
    }

    public void setNovencidosPorcentaje(int novencidos_porcentaje) {
        this.novencidos_porcentaje = novencidos_porcentaje;
    }

    public int getTotalTurnos() {
        return total_turnos;
    }

    public void setTotalTurnos(int total_turnos) {
        this.total_turnos = total_turnos;
    }

    public int getConcluidos() {
        return concluidos;
    }

    public void setConcluidos(int concluidos) {
        this.concluidos = concluidos;
    }

    public int getConcluidosPorcentaje() {
        return concluidos_porcentaje;
    }

    public void setConcluidosPorcentaje(int concluidos_porcentaje) {
        this.concluidos_porcentaje = concluidos_porcentaje;
    }

    public int getPendientes() {
        return pendientes;
    }

    public void setPendientes(int pendientes) {
        this.pendientes = pendientes;
    }
}
