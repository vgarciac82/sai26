package com.axtel.egresos.viaticos;

import java.math.BigDecimal;
import java.util.Base64;

public class Comision {

    private int idComision;

    private Empleado empleado;

    private int idEstatus;

    private String CTAB;

    private String usuarioCaptura;

    private String UnidadResponsable;

    private BigDecimal totalDias;

    private BigDecimal totalAgenda;

    private BigDecimal totalTransporte;

    private String nombreComision;

    private String cuentaBancariaCNF;

    private String RFC;

    private BigDecimal pasaje;

    private BigDecimal taxi;

    private BigDecimal peaje;

    private BigDecimal hotel;

    private BigDecimal consumos;

    private BigDecimal otros;

    private BigDecimal pasajeLocal;

    private BigDecimal taxiLocal;

    private BigDecimal gasLocal;

    private BigDecimal peajeLocal;

    private BigDecimal maritimoLocal;

    private BigDecimal aereoLocal;

    private String evento;

    private int folioReemplazo;

    private int idNombre;

    public BigDecimal getTaxi() {
        return taxi;
    }

    public void setTaxi(BigDecimal taxi) {
        this.taxi = taxi;
    }

    public BigDecimal getPeaje() {
        return peaje;
    }

    public void setPeaje(BigDecimal peaje) {
        this.peaje = peaje;
    }

    public BigDecimal getHotel() {
        return hotel;
    }

    public void setHotel(BigDecimal hotel) {
        this.hotel = hotel;
    }

    public BigDecimal getConsumos() {
        return consumos;
    }

    public void setConsumos(BigDecimal consumos) {
        this.consumos = consumos;
    }

    public BigDecimal getOtros() {
        return otros;
    }

    public void setOtros(BigDecimal otros) {
        this.otros = otros;
    }

    public BigDecimal getPasajeLocal() {
        return pasajeLocal;
    }

    public void setPasajeLocal(BigDecimal pasajeLocal) {
        this.pasajeLocal = pasajeLocal;
    }

    public BigDecimal getTaxiLocal() {
        return taxiLocal;
    }

    public void setTaxiLocal(BigDecimal taxiLocal) {
        this.taxiLocal = taxiLocal;
    }

    public BigDecimal getGasLocal() {
        return gasLocal;
    }

    public void setGasLocal(BigDecimal gasLocal) {
        this.gasLocal = gasLocal;
    }

    public int getIdComision() {
        return idComision;
    }

    public void setIdComision(int idComision) {
        this.idComision = idComision;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

    public int getIdEstatus() {
        return idEstatus;
    }

    public void setIdEstatus(int idEstatus) {
        this.idEstatus = idEstatus;
    }

    public String getCTAB() {
        return CTAB;
    }

    public void setCTAB(String cTAB) {
        CTAB = cTAB;
    }

    public String getUsuarioCaptura() {
        return usuarioCaptura;
    }

    public void setUsuarioCaptura(String usuarioCaptura) {
        this.usuarioCaptura = usuarioCaptura;
    }

    public String getUnidadResponsable() {
        return UnidadResponsable;
    }

    public void setUnidadResponsable(String unidadResponsable) {
        UnidadResponsable = unidadResponsable;
    }

    public BigDecimal getTotalDias() {
        return totalDias;
    }

    public void setTotalDias(BigDecimal totalDias) {
        this.totalDias = totalDias;
    }

    public BigDecimal getTotalAgenda() {
        return totalAgenda;
    }

    public void setTotalAgenda(BigDecimal totalAgenda) {
        this.totalAgenda = totalAgenda;
    }

    public BigDecimal getTotalTransporte() {
        return totalTransporte;
    }

    public void setTotalTransporte(BigDecimal totalTransporte) {
        this.totalTransporte = totalTransporte;
    }

    public String getNombreComision() {
        return nombreComision;
    }

    public void setNombreComision(String nombreComision) {
        this.nombreComision = nombreComision;
    }

    public String getCuentaBancariaCNF() {
        return cuentaBancariaCNF;
    }

    public void setCuentaBancariaCNF(String cuentaBancariaCNF) {
        this.cuentaBancariaCNF = cuentaBancariaCNF;
    }

    public String getRFC() {
        return RFC;
    }

    public void setRFC(String rFC) {
        RFC = rFC;
    }

    public BigDecimal getPasaje() {
        return pasaje;
    }

    public void setPasaje(BigDecimal pasaje) {
        this.pasaje = pasaje;
    }

    public String getEvento() {
        return evento;
    }

    public void setEvento(String evento) {
        this.evento = evento;
    }

    public int getFolioReemplazo() {
        return folioReemplazo;
    }

    public void setFolioReemplazo(int folioReemplazo) {
        this.folioReemplazo = folioReemplazo;
    }

    public int getIdNombre() {
        return idNombre;
    }

    public void setIdNombre(int idNombre) {
        this.idNombre = idNombre;
    }

    public BigDecimal getPeajeLocal() {
        return peajeLocal;
    }

    public void setPeajeLocal(BigDecimal peajeLocal) {
        this.peajeLocal = peajeLocal;
    }

    public BigDecimal getMaritimoLocal() {
        return maritimoLocal;
    }

    public void setMaritimoLocal(BigDecimal maritimoLocal) {
        this.maritimoLocal = maritimoLocal;
    }

    public BigDecimal getAereoLocal() {
        return aereoLocal;
    }

    public void setAereoLocal(BigDecimal aereoLocal) {
        this.aereoLocal = aereoLocal;
    }
}
