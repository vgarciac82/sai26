/**
 */
package com.syc.egresos.core.impl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import com.axtel.egresos.entities.EgresoExcedeUMA;
import com.syc.cfdi.db.CloseObject;
import com.syc.contable.core.PagoObrasManager;
import com.syc.egresos.core.Amortizacion;
import com.syc.egresos.core.EgresoEncabezado;
import com.syc.ejercido.pagado.core.EgresosManager;

/**
 * @author iccvi
 */
public class EgresoPAGOOBRAEncabezado extends EgresoEncabezado {

    private Date fechaPeriodoDesde;

    private Date fechaPeriodoHasta;

    private String folioContratoObra;

    private int folioPagoObra;

    private int idEstadoEstimacion;

    private String numConvenio;

    @Override
    public int actualizaMontosRetencion(Connection conn) throws Exception {
        throw new Exception("actualizaMontosRetencion  No implementado");
    }

    @Override
    public int actualizaRetencion(Connection conn, int idTipoRetencion, BigDecimal valorRetencion) throws Exception {
        throw new Exception("actualizaRetencion  No implementado");
    }

    @Override
    public int avanzaEstatus(Connection conn) throws Exception {
        throw new Exception("avanzaEstatus  No implementado");
    }

    @Override
    public EgresoEncabezado cargaEncabezado(HttpServletRequest req) throws Exception {
        throw new Exception(" cargaEncabezado( HttpServletRequest req )  No implementado");
    }

    @Override
    public EgresoEncabezado cargaEncabezado(int folioEgreso) throws Exception {
        super.init(getJniName());
        EgresoPAGOOBRAEncabezado epoe = null;
        Connection conn = null;
        try {
            conn = getConnection();
            epoe = PagoObrasManager.cargaEncabezado(conn, folioEgreso);
            return epoe;
        } finally {
            CloseObject.closeObject(conn);
        }
    }

    @Override
    public int delete(Connection conn) throws Exception {
        throw new Exception("delete  No implementado");
    }

    @Override
    public int eliminaRetencion(Connection conn, int idTipoRetencion) throws Exception {
        throw new Exception("eliminaRetencion  No implementado");
    }

    @Override
    public int generaRetenciones(Connection conn) throws Exception {
        throw new Exception("generaRetenciones  No implementado");
    }

    public Date getFechaPeriodoDesde() {
        return fechaPeriodoDesde;
    }

    public Date getFechaPeriodoHasta() {
        return fechaPeriodoHasta;
    }

    public String getFolioContratoObra() {
        return folioContratoObra;
    }

    public int getFolioPagoObra() {
        return folioPagoObra;
    }

    public int getIdEstadoEstimacion() {
        return idEstadoEstimacion;
    }

    @Override
    public String getNombreAnexo() {
        throw new RuntimeException("getNombreAnexo  No implementado");
    }

    @Override
    public String getNombreSolicitudPago() {
        throw new RuntimeException("getNombreSolicitudPago  No implementado");
    }

    public String getNumConvenio() {
        return numConvenio;
    }

    @Override
    public String getPrefijoCR(Connection conn) throws Exception {
        throw new Exception("getPrefijoCR  No implementado");
    }

    @Override
    public void rechazaPago(Connection conn, String motivoRechazo) throws Exception {
        throw new Exception("rechazaPago  No implementado");
    }

    @Override
    public Map<String, String> resumenConcepto(Connection conn) throws Exception {
        throw new Exception("resumenConcepto  No implementado");
    }

    @Override
    public Map<String, String> resumenPago(Connection conn) throws Exception {
        throw new Exception("resumenPago  No implementado");
    }

    @Override
    public List<Map<String, String>> resumenRetenciones(Connection conn) throws Exception {
        throw new Exception("resumenRetenciones  No implementado");
    }

    @Override
    public int save(Connection conn) throws Exception {
        throw new Exception("save  No implementado");
    }

    public void setFechaPeriodoDesde(Date fechaPeriodoDesde) {
        this.fechaPeriodoDesde = fechaPeriodoDesde;
    }

    public void setFechaPeriodoHasta(Date fechaPeriodoHasta) {
        this.fechaPeriodoHasta = fechaPeriodoHasta;
    }

    public void setFolioContratoObra(String folioContratoObra) {
        this.folioContratoObra = folioContratoObra;
    }

    public void setFolioPagoObra(int folioPagoObra) {
        this.folioPagoObra = folioPagoObra;
    }

    public void setIdEstadoEstimacion(int idEstadoEstimacion) {
        this.idEstadoEstimacion = idEstadoEstimacion;
    }

    public void setNumConvenio(String numConvenio) {
        this.numConvenio = numConvenio;
    }

    @Override
    public Amortizacion getAmortizacion(Connection conn) throws Exception {
        return EgresosManager.getAmortizacion(conn, this);
    }

    @Override
    public boolean retencionEliminable(Connection conn, int idRetencion) throws Exception {
        throw new RuntimeException("retencionEliminable No Implementado.");
    }

    @Override
    public List<EgresoExcedeUMA> validaTopeUMASUnidad(Connection conn, String rfc2) {
        return new ArrayList<EgresoExcedeUMA>();
    }
}
