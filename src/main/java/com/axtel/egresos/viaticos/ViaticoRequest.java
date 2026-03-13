package com.axtel.egresos.viaticos;

public class ViaticoRequest {
    private int idEmpleado;
    private String fechaInicio;
    private String fechaFin;
    private String concepto;

    public ViaticoRequest() {
    }

    public ViaticoRequest(int idEmpleado, String fechaInicio, String fechaFin, String concepto) {
        this.idEmpleado = idEmpleado;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.concepto = concepto;
    }

    public ViaticoRequest(int idEmpleado, String fechaInicio, String fechaFin) {
        this(idEmpleado, fechaInicio, fechaFin, null);
    }
    
    public int getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(int idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(String fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }
}
