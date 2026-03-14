package com.axtel.sisecop.entities;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.Base64;

public class ProyectoServicio implements Serializable {

    private static final long serialVersionUID = -2855369393931719628L;

    private List<ProyectoServicioActividad> actividades;

    private ProyectoConfidencialidad confidencialidad;

    private ProyectoEstatus estatus;

    private int idProcess;

    private String loginUsuario;

    private String managmentUnit;

    private String observaciones;

    private List<ProyectoServicioPago> pagos;

    private List<ProyectoProducto> productos;

    private ProyectoExcepcion proyectoExcepcion;

    private ProyectoTipo proyectoTipo;

    private String responsibleUnit;

    private List<ProjectBudgetItem> servicioClaves;

    private boolean servicioCompleto;

    private String servicioCoordinacion;

    private Timestamp servicioCreacion;

    private int servicioDuracion;

    private int servicioDuracionDias;

    private int servicioFolioAnio;

    private int servicioFolioNum;

    private String servicioFolioPre;

    private String servicioGerencia;

    private int servicioId;

    private boolean servicioListo;

    private Timestamp servicioModificacion;

    private String servicioObjetivos;

    private List<ProyectoServicioAdicional> serviciosAdicionales;

    private List<ProyectoServicioTDR> serviciosTDR;

    private String servicioTitulo;

    private String servicioVinculacion;

    private String gerenciaNombre = "";

    private String coordinacionNombre = "";

    private List<ProyectoServicioTerritorio> territorios;

    public String getGerenciaNombre() {
        return gerenciaNombre;
    }

    public void setGerenciaNombre(String gerenciaNombre) {
        this.gerenciaNombre = gerenciaNombre;
    }

    public String getCoordinacionNombre() {
        return coordinacionNombre;
    }

    public void setCoordinacionNombre(String coordinacionNombre) {
        this.coordinacionNombre = coordinacionNombre;
    }

    public List<ProyectoServicioActividad> getActividades() {
        return actividades;
    }

    public ProyectoConfidencialidad getConfidencialidad() {
        return confidencialidad;
    }

    public ProyectoEstatus getEstatus() {
        return estatus;
    }

    public int getIdProcess() {
        return idProcess;
    }

    public String getLoginUsuario() {
        return loginUsuario;
    }

    public String getManagmentUnit() {
        return managmentUnit;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public List<ProyectoServicioPago> getPagos() {
        return pagos;
    }

    public List<ProyectoProducto> getProductos() {
        return productos;
    }

    public ProyectoExcepcion getProyectoExcepcion() {
        return proyectoExcepcion;
    }

    public ProyectoTipo getProyectoTipo() {
        return proyectoTipo;
    }

    public String getResponsibleUnit() {
        return responsibleUnit;
    }

    public List<ProjectBudgetItem> getServicioClaves() {
        return servicioClaves;
    }

    public String getServicioCoordinacion() {
        return servicioCoordinacion;
    }

    public Timestamp getServicioCreacion() {
        return servicioCreacion;
    }

    public int getServicioDuracion() {
        return servicioDuracion;
    }

    public int getServicioDuracionDias() {
        return servicioDuracionDias;
    }

    public int getServicioFolioAnio() {
        return servicioFolioAnio;
    }

    public int getServicioFolioNum() {
        return servicioFolioNum;
    }

    public String getServicioFolioPre() {
        return servicioFolioPre;
    }

    public String getServicioGerencia() {
        return servicioGerencia;
    }

    public int getServicioId() {
        return servicioId;
    }

    public Timestamp getServicioModificacion() {
        return servicioModificacion;
    }

    public String getServicioObjetivos() {
        return servicioObjetivos;
    }

    public List<ProyectoServicioAdicional> getServiciosAdicionales() {
        return serviciosAdicionales;
    }

    public List<ProyectoServicioTDR> getServiciosTDR() {
        return serviciosTDR;
    }

    public String getServicioTitulo() {
        return servicioTitulo;
    }

    public String getServicioVinculacion() {
        return servicioVinculacion;
    }

    public List<ProyectoServicioTerritorio> getTerritorios() {
        return territorios;
    }

    public boolean isServicioCompleto() {
        return servicioCompleto;
    }

    public boolean isServicioListo() {
        return servicioListo;
    }

    public void setActividades(List<ProyectoServicioActividad> actividades) {
        this.actividades = actividades;
    }

    public void setConfidencialidad(ProyectoConfidencialidad confidencialidad) {
        this.confidencialidad = confidencialidad;
    }

    public void setEstatus(ProyectoEstatus estatus) {
        this.estatus = estatus;
    }

    public void setIdProcess(int idProcess) {
        this.idProcess = idProcess;
    }

    public void setLoginUsuario(String loginUsuario) {
        this.loginUsuario = loginUsuario;
    }

    public void setManagmentUnit(String managmentUnit) {
        this.managmentUnit = managmentUnit;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public void setPagos(List<ProyectoServicioPago> pagos) {
        this.pagos = pagos;
    }

    public void setProductos(List<ProyectoProducto> productos) {
        this.productos = productos;
    }

    public void setProyectoExcepcion(ProyectoExcepcion proyectoExcepcion) {
        this.proyectoExcepcion = proyectoExcepcion;
    }

    public void setProyectoTipo(ProyectoTipo proyectoTipo) {
        this.proyectoTipo = proyectoTipo;
    }

    public void setResponsibleUnit(String responsibleUnit) {
        this.responsibleUnit = responsibleUnit;
    }

    public void setServicioClaves(List<ProjectBudgetItem> servicioClaves) {
        this.servicioClaves = servicioClaves;
    }

    public void setServicioCompleto(boolean servicioCompleto) {
        this.servicioCompleto = servicioCompleto;
    }

    public void setServicioCoordinacion(String servicioCoordinacion) {
        this.servicioCoordinacion = servicioCoordinacion;
    }

    public void setServicioCreacion(Timestamp servicioCreacion) {
        this.servicioCreacion = servicioCreacion;
    }

    public void setServicioDuracion(int servicioDuracion) {
        this.servicioDuracion = servicioDuracion;
    }

    public void setServicioDuracionDias(int servicioDuracionDias) {
        this.servicioDuracionDias = servicioDuracionDias;
    }

    public void setServicioFolioAnio(int servicioFolioAnio) {
        this.servicioFolioAnio = servicioFolioAnio;
    }

    public void setServicioFolioNum(int servicioFolioNum) {
        this.servicioFolioNum = servicioFolioNum;
    }

    public void setServicioFolioPre(String servicioFolioPre) {
        this.servicioFolioPre = servicioFolioPre;
    }

    public void setServicioGerencia(String servicioGerencia) {
        this.servicioGerencia = servicioGerencia;
    }

    public void setServicioId(int servicioId) {
        this.servicioId = servicioId;
    }

    public void setServicioListo(boolean servicioListo) {
        this.servicioListo = servicioListo;
    }

    public void setServicioModificacion(Timestamp servicioModificacion) {
        this.servicioModificacion = servicioModificacion;
    }

    public void setServicioObjetivos(String servicioObjetivos) {
        this.servicioObjetivos = servicioObjetivos;
    }

    public void setServiciosAdicionales(List<ProyectoServicioAdicional> serviciosAdicionales) {
        this.serviciosAdicionales = serviciosAdicionales;
    }

    public void setServiciosTDR(List<ProyectoServicioTDR> serviciosTDR) {
        this.serviciosTDR = serviciosTDR;
    }

    public void setServicioTitulo(String servicioTitulo) {
        this.servicioTitulo = servicioTitulo;
    }

    public void setServicioVinculacion(String servicioVinculacion) {
        this.servicioVinculacion = servicioVinculacion;
    }

    public void setTerritorios(List<ProyectoServicioTerritorio> territorios) {
        this.territorios = territorios;
    }

    @Override
    public String toString() {
        return "ProyectoServicio [actividades=" + actividades + ", confidencialidad=" + confidencialidad + ", estatus=" + estatus + ", idProcess=" + idProcess + ", loginUsuario=" + loginUsuario + ", managmentUnit=" + managmentUnit + ", observaciones=" + observaciones + ", pagos=" + pagos + ", productos=" + productos + ", proyectoExcepcion=" + proyectoExcepcion + ", proyectoTipo=" + proyectoTipo + ", responsibleUnit=" + responsibleUnit + ", servicioClaves=" + servicioClaves + ", servicioCompleto=" + servicioCompleto + ", servicioCoordinacion=" + servicioCoordinacion + ", servicioCreacion=" + servicioCreacion + ", servicioDuracion=" + servicioDuracion + ", servicioDuracionDias=" + servicioDuracionDias + ", servicioFolioAnio=" + servicioFolioAnio + ", servicioFolioNum=" + servicioFolioNum + ", servicioFolioPre=" + servicioFolioPre + ", servicioGerencia=" + servicioGerencia + ", servicioId=" + servicioId + ", servicioListo=" + servicioListo + ", servicioModificacion=" + servicioModificacion + ", servicioObjetivos=" + servicioObjetivos + ", serviciosAdicionales=" + serviciosAdicionales + ", serviciosTDR=" + serviciosTDR + ", servicioTitulo=" + servicioTitulo + ", servicioVinculacion=" + servicioVinculacion + ", gerenciaNombre=" + gerenciaNombre + ", coordinacionNombre=" + coordinacionNombre + ", territorios=" + territorios + "]";
    }
}
