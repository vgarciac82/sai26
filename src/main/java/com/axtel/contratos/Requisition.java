package com.axtel.contratos;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Base64;

/**
 * @author hfariasr
 */
public class Requisition {

    private int consecutivoApartado;

    private boolean contieneAnexos;

    private String cuentaDisp;

    private String descripcion;

    private String ejercicio;

    private String facturarA;

    private Date fechaAnulacion;

    private Date fechaAprobacion;

    private Date fechaCreacion;

    private Date fechaRequerida;

    private Date fechaSolicitud;

    private String folioApartado;

    private int idAlcance;

    private String idAlmacen;

    private String idAlmacenEntrega;

    private int idCategoria;

    private int idConsecutivo;

    private String idEntidadContable;

    private int idEstado;

    private int idEstadoPrecomprometido;

    private String idFuenteFinanciamiento;

    private int idPeriodo;

    private int idPlazo;

    private String idSolicitud;

    private String idSubPartida;

    private int idTipoGarantia;

    private String idTipoSolicitud;

    private String idUnidadEjecutora;

    private String idUsuarioAnulacion;

    private String idUsuarioAprobacion;

    private String idUsuarioCreacion;

    private BigDecimal importePoliza;

    private String notas;

    private String observaciones;

    private String plurianualidad;

    private BigDecimal porcentajeGarantia;

    private String tipoGarantia;

    private String tipoConsolidado;

    private String cIdConsolidado;

    private String cNamePlantilla;

    private int nTipoReporte;

    private String applyQuestionnaire;

    private String cAplica15D;

    /**
     * @return the consecutivoApartado
     */
    public int getConsecutivoApartado() {
        return consecutivoApartado;
    }

    /**
     * @param consecutivoApartado
     *            the consecutivoApartado to set
     */
    public void setConsecutivoApartado(int consecutivoApartado) {
        this.consecutivoApartado = consecutivoApartado;
    }

    /**
     * @return the contieneAnexos
     */
    public boolean isContieneAnexos() {
        return contieneAnexos;
    }

    /**
     * @param contieneAnexos
     *            the contieneAnexos to set
     */
    public void setContieneAnexos(boolean contieneAnexos) {
        this.contieneAnexos = contieneAnexos;
    }

    /**
     * @return the cuentaDisp
     */
    public String getCuentaDisp() {
        return cuentaDisp;
    }

    /**
     * @param cuentaDisp
     *            the cuentaDisp to set
     */
    public void setCuentaDisp(String cuentaDisp) {
        this.cuentaDisp = cuentaDisp;
    }

    /**
     * @return the descripcion
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * @param descripcion
     *            the descripcion to set
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * @return the ejercicio
     */
    public String getEjercicio() {
        return ejercicio;
    }

    /**
     * @param ejercicio
     *            the ejercicio to set
     */
    public void setEjercicio(String ejercicio) {
        this.ejercicio = ejercicio;
    }

    /**
     * @return the facturarA
     */
    public String getFacturarA() {
        return facturarA;
    }

    /**
     * @param facturarA
     *            the facturarA to set
     */
    public void setFacturarA(String facturarA) {
        this.facturarA = facturarA;
    }

    /**
     * @return the fechaAnulacion
     */
    public Date getFechaAnulacion() {
        return fechaAnulacion;
    }

    /**
     * @param fechaAnulacion
     *            the fechaAnulacion to set
     */
    public void setFechaAnulacion(Date fechaAnulacion) {
        this.fechaAnulacion = fechaAnulacion;
    }

    /**
     * @return the fechaAprobacion
     */
    public Date getFechaAprobacion() {
        return fechaAprobacion;
    }

    /**
     * @param fechaAprobacion
     *            the fechaAprobacion to set
     */
    public void setFechaAprobacion(Date fechaAprobacion) {
        this.fechaAprobacion = fechaAprobacion;
    }

    /**
     * @return the fechaCreacion
     */
    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    /**
     * @param fechaCreacion
     *            the fechaCreacion to set
     */
    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    /**
     * @return the fechaRequerida
     */
    public Date getFechaRequerida() {
        return fechaRequerida;
    }

    /**
     * @param fechaRequerida
     *            the fechaRequerida to set
     */
    public void setFechaRequerida(Date fechaRequerida) {
        this.fechaRequerida = fechaRequerida;
    }

    /**
     * @return the fechaSolicitud
     */
    public Date getFechaSolicitud() {
        return fechaSolicitud;
    }

    /**
     * @param fechaSolicitud
     *            the fechaSolicitud to set
     */
    public void setFechaSolicitud(Date fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    /**
     * @return the folioApartado
     */
    public String getFolioApartado() {
        return folioApartado;
    }

    /**
     * @param folioApartado
     *            the folioApartado to set
     */
    public void setFolioApartado(String folioApartado) {
        this.folioApartado = folioApartado;
    }

    /**
     * @return the idAlcance
     */
    public int getIdAlcance() {
        return idAlcance;
    }

    /**
     * @param idAlcance
     *            the idAlcance to set
     */
    public void setIdAlcance(int idAlcance) {
        this.idAlcance = idAlcance;
    }

    /**
     * @return the idAlmacen
     */
    public String getIdAlmacen() {
        return idAlmacen;
    }

    /**
     * @param idAlmacen
     *            the idAlmacen to set
     */
    public void setIdAlmacen(String idAlmacen) {
        this.idAlmacen = idAlmacen;
    }

    /**
     * @return the idAlmacenEntrega
     */
    public String getIdAlmacenEntrega() {
        return idAlmacenEntrega;
    }

    /**
     * @param idAlmacenEntrega
     *            the idAlmacenEntrega to set
     */
    public void setIdAlmacenEntrega(String idAlmacenEntrega) {
        this.idAlmacenEntrega = idAlmacenEntrega;
    }

    /**
     * @return the idCategoria
     */
    public int getIdCategoria() {
        return idCategoria;
    }

    /**
     * @param idCategoria
     *            the idCategoria to set
     */
    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    /**
     * @return the idConsecutivo
     */
    public int getIdConsecutivo() {
        return idConsecutivo;
    }

    /**
     * @param idConsecutivo
     *            the idConsecutivo to set
     */
    public void setIdConsecutivo(int idConsecutivo) {
        this.idConsecutivo = idConsecutivo;
    }

    /**
     * @return the idEntidadContable
     */
    public String getIdEntidadContable() {
        return idEntidadContable;
    }

    /**
     * @param idEntidadContable
     *            the idEntidadContable to set
     */
    public void setIdEntidadContable(String idEntidadContable) {
        this.idEntidadContable = idEntidadContable;
    }

    /**
     * @return the idEstado
     */
    public int getIdEstado() {
        return idEstado;
    }

    /**
     * @param idEstado
     *            the idEstado to set
     */
    public void setIdEstado(int idEstado) {
        this.idEstado = idEstado;
    }

    /**
     * @return the idEstadoPrecomprometido
     */
    public int getIdEstadoPrecomprometido() {
        return idEstadoPrecomprometido;
    }

    /**
     * @param idEstadoPrecomprometido
     *            the idEstadoPrecomprometido to set
     */
    public void setIdEstadoPrecomprometido(int idEstadoPrecomprometido) {
        this.idEstadoPrecomprometido = idEstadoPrecomprometido;
    }

    /**
     * @return the idFuenteFinanciamiento
     */
    public String getIdFuenteFinanciamiento() {
        return idFuenteFinanciamiento;
    }

    /**
     * @param idFuenteFinanciamiento
     *            the idFuenteFinanciamiento to set
     */
    public void setIdFuenteFinanciamiento(String idFuenteFinanciamiento) {
        this.idFuenteFinanciamiento = idFuenteFinanciamiento;
    }

    /**
     * @return the idPeriodo
     */
    public int getIdPeriodo() {
        return idPeriodo;
    }

    /**
     * @param idPeriodo
     *            the idPeriodo to set
     */
    public void setIdPeriodo(int idPeriodo) {
        this.idPeriodo = idPeriodo;
    }

    /**
     * @return the idPlazo
     */
    public int getIdPlazo() {
        return idPlazo;
    }

    /**
     * @param idPlazo
     *            the idPlazo to set
     */
    public void setIdPlazo(int idPlazo) {
        this.idPlazo = idPlazo;
    }

    /**
     * @return the idSolicitud
     */
    public String getIdSolicitud() {
        return idSolicitud;
    }

    /**
     * @param idSolicitud
     *            the idSolicitud to set
     */
    public void setIdSolicitud(String idSolicitud) {
        this.idSolicitud = idSolicitud;
    }

    /**
     * @return the idSubPartida
     */
    public String getIdSubPartida() {
        return idSubPartida;
    }

    /**
     * @param idSubPartida
     *            the idSubPartida to set
     */
    public void setIdSubPartida(String idSubPartida) {
        this.idSubPartida = idSubPartida;
    }

    /**
     * @return the idTipoGarantia
     */
    public int getIdTipoGarantia() {
        return idTipoGarantia;
    }

    /**
     * @param idTipoGarantia
     *            the idTipoGarantia to set
     */
    public void setIdTipoGarantia(int idTipoGarantia) {
        this.idTipoGarantia = idTipoGarantia;
    }

    /**
     * @return the idTipoSolicitud
     */
    public String getIdTipoSolicitud() {
        return idTipoSolicitud;
    }

    /**
     * @param idTipoSolicitud
     *            the idTipoSolicitud to set
     */
    public void setIdTipoSolicitud(String idTipoSolicitud) {
        this.idTipoSolicitud = idTipoSolicitud;
    }

    /**
     * @return the idUnidadEjecutora
     */
    public String getIdUnidadEjecutora() {
        return idUnidadEjecutora;
    }

    /**
     * @param idUnidadEjecutora
     *            the idUnidadEjecutora to set
     */
    public void setIdUnidadEjecutora(String idUnidadEjecutora) {
        this.idUnidadEjecutora = idUnidadEjecutora;
    }

    /**
     * @return the idUsuarioAnulacion
     */
    public String getIdUsuarioAnulacion() {
        return idUsuarioAnulacion;
    }

    /**
     * @param idUsuarioAnulacion
     *            the idUsuarioAnulacion to set
     */
    public void setIdUsuarioAnulacion(String idUsuarioAnulacion) {
        this.idUsuarioAnulacion = idUsuarioAnulacion;
    }

    /**
     * @return the idUsuarioAprobacion
     */
    public String getIdUsuarioAprobacion() {
        return idUsuarioAprobacion;
    }

    /**
     * @param idUsuarioAprobacion
     *            the idUsuarioAprobacion to set
     */
    public void setIdUsuarioAprobacion(String idUsuarioAprobacion) {
        this.idUsuarioAprobacion = idUsuarioAprobacion;
    }

    /**
     * @return the idUsuarioCreacion
     */
    public String getIdUsuarioCreacion() {
        return idUsuarioCreacion;
    }

    /**
     * @param idUsuarioCreacion
     *            the idUsuarioCreacion to set
     */
    public void setIdUsuarioCreacion(String idUsuarioCreacion) {
        this.idUsuarioCreacion = idUsuarioCreacion;
    }

    /**
     * @return the importePoliza
     */
    public BigDecimal getImportePoliza() {
        return importePoliza;
    }

    /**
     * @param importePoliza
     *            the importePoliza to set
     */
    public void setImportePoliza(BigDecimal importePoliza) {
        this.importePoliza = importePoliza;
    }

    /**
     * @return the notas
     */
    public String getNotas() {
        return notas;
    }

    /**
     * @param notas
     *            the notas to set
     */
    public void setNotas(String notas) {
        this.notas = notas;
    }

    /**
     * @return the observaciones
     */
    public String getObservaciones() {
        return observaciones;
    }

    /**
     * @param observaciones
     *            the observaciones to set
     */
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    /**
     * @return the plurianualidad
     */
    public String getPlurianualidad() {
        return plurianualidad;
    }

    /**
     * @param plurianualidad
     *            the plurianualidad to set
     */
    public void setPlurianualidad(String plurianualidad) {
        this.plurianualidad = plurianualidad;
    }

    /**
     * @return the porcentajeGarantia
     */
    public BigDecimal getPorcentajeGarantia() {
        return porcentajeGarantia;
    }

    /**
     * @param porcentajeGarantia
     *            the porcentajeGarantia to set
     */
    public void setPorcentajeGarantia(BigDecimal porcentajeGarantia) {
        this.porcentajeGarantia = porcentajeGarantia;
    }

    /**
     * @return the tipoGarantia
     */
    public String getTipoGarantia() {
        return tipoGarantia;
    }

    /**
     * @param tipoGarantia
     *            the tipoGarantia to set
     */
    public void setTipoGarantia(String tipoGarantia) {
        this.tipoGarantia = tipoGarantia;
    }

    public String getTipoConsolidado() {
        return tipoConsolidado;
    }

    public void setTipoConsolidado(String tipoConsolidado) {
        this.tipoConsolidado = tipoConsolidado;
    }

    public String getcIdConsolidado() {
        return cIdConsolidado;
    }

    public void setcIdConsolidado(String cIdConsolidado) {
        this.cIdConsolidado = cIdConsolidado;
    }

    public String getcNamePlantilla() {
        return cNamePlantilla;
    }

    public void setcNamePlantilla(String cNamePlantilla) {
        this.cNamePlantilla = cNamePlantilla;
    }

    public int getnTipoReporte() {
        return nTipoReporte;
    }

    public void setnTipoReporte(int nTipoReporte) {
        this.nTipoReporte = nTipoReporte;
    }

    public String getApplyQuestionnaire() {
        return applyQuestionnaire;
    }

    public void setApplyQuestionnaire(String applyQuestionnaire) {
        this.applyQuestionnaire = applyQuestionnaire;
    }

    public String getcAplica15D() {
        return cAplica15D;
    }

    public void setcAplica15D(String cAplica15D) {
        this.cAplica15D = cAplica15D;
    }

    @Override
    public String toString() {
        return "Requisition [consecutivoApartado=" + consecutivoApartado + ", contieneAnexos=" + contieneAnexos + ", cuentaDisp=" + cuentaDisp + ", descripcion=" + descripcion + ", ejercicio=" + ejercicio + ", facturarA=" + facturarA + ", fechaAnulacion=" + fechaAnulacion + ", fechaAprobacion=" + fechaAprobacion + ", fechaCreacion=" + fechaCreacion + ", fechaRequerida=" + fechaRequerida + ", fechaSolicitud=" + fechaSolicitud + ", folioApartado=" + folioApartado + ", idAlcance=" + idAlcance + ", idAlmacen=" + idAlmacen + ", idAlmacenEntrega=" + idAlmacenEntrega + ", idCategoria=" + idCategoria + ", idConsecutivo=" + idConsecutivo + ", idEntidadContable=" + idEntidadContable + ", idEstado=" + idEstado + ", idEstadoPrecomprometido=" + idEstadoPrecomprometido + ", idFuenteFinanciamiento=" + idFuenteFinanciamiento + ", idPeriodo=" + idPeriodo + ", idPlazo=" + idPlazo + ", idSolicitud=" + idSolicitud + ", idSubPartida=" + idSubPartida + ", idTipoGarantia=" + idTipoGarantia + ", idTipoSolicitud=" + idTipoSolicitud + ", idUnidadEjecutora=" + idUnidadEjecutora + ", idUsuarioAnulacion=" + idUsuarioAnulacion + ", idUsuarioAprobacion=" + idUsuarioAprobacion + ", idUsuarioCreacion=" + idUsuarioCreacion + ", importePoliza=" + importePoliza + ", notas=" + notas + ", observaciones=" + observaciones + ", plurianualidad=" + plurianualidad + ", porcentajeGarantia=" + porcentajeGarantia + ", tipoGarantia=" + tipoGarantia + ", tipoConsolidado=" + tipoConsolidado + ", cIdConsolidado=" + cIdConsolidado + ", cNamePlantilla=" + cNamePlantilla + ", nTipoReporte=" + nTipoReporte + ", applyQuestionnaire=" + applyQuestionnaire + ", cAplica15D=" + cAplica15D + "]";
    }
}
