package com.syc.gestion.reportes;

import java.sql.Connection;
import com.syc.egresos.firmante.servlet.Firmante;
import com.syc.sai.firmaElectronica.interfaces.SolicitudFirmaElectronica;
import java.util.Base64;

public abstract class FirmaElectronicaReporte extends SolicitudFirmaElectronica {

    public static final String ACUSE = "A";

    public static final int AUTORIZA_REPORTE = 4;

    public static final int ELABORA_REPORTE = 1;

    public static final int ELABORA_REVISA_REPORTE = 2;

    public static final String ESPERA_FIRMA = "E";

    public static final String FIRMADO = "FIRMADO";

    public static final String REPORTE = "R";

    public static final Object REPORTE_CANCELADO = "CANCELADO";

    public static final int REVISA_REPORTE = 3;

    private Firmante[] firmantesReporte;

    private int idTipoReporte;

    private int mes;

    private String motivoRechazo;

    private int orden;

    private String ordenes;

    private String pathReporte;

    private String rutaReporteImpreso;

    public Firmante[] getFirmantesReporte() {
        return firmantesReporte;
    }

    public int getIdTipoReporte() {
        return idTipoReporte;
    }

    public int getMes() {
        return this.mes;
    }

    public String getMotivoRechazo() {
        return motivoRechazo;
    }

    public int getOrden() {
        return orden;
    }

    public String getOrdenes() {
        return this.ordenes;
    }

    public String getPathReporte() {
        return pathReporte;
    }

    public String getRutaReporteImpreso() {
        return this.rutaReporteImpreso;
    }

    public abstract boolean registraFirmantes(Connection conn) throws Exception;

    public void setFirmantesReporte(Firmante[] firmantesReporte) {
        this.firmantesReporte = firmantesReporte;
    }

    public void setIdTipoReporte(int idTipoReporte) {
        this.idTipoReporte = idTipoReporte;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public void setMotivoRechazo(String motivoRechazo) {
        this.motivoRechazo = motivoRechazo;
    }

    public void setOrden(int orden) {
        this.orden = orden;
    }

    public void setOrdenes(String ordenes) {
        this.ordenes = ordenes;
    }

    public void setPathReporte(String pathReporte) {
        this.pathReporte = pathReporte;
    }

    public void setRutaReporteImpreso(String rutaReporteImpreso) {
        this.rutaReporteImpreso = rutaReporteImpreso;
    }
}
