package com.syc.egresos.core.impl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import com.axtel.egresos.entities.EgresoExcedeUMA;
import com.syc.egresos.core.Amortizacion;
import com.syc.egresos.core.EgresoEncabezado;

public class EgresoCOMSINVIATICOSEncabezado extends EgresoEncabezado {

    @Override
    public int actualizaMontosRetencion(Connection conn) throws Exception {
        return 0;
    }

    @Override
    public int actualizaRetencion(Connection conn, int idTipoRetencion, BigDecimal valorRetencion) throws Exception {
        return 0;
    }

    @Override
    public int avanzaEstatus(Connection conn) throws Exception {
        return 0;
    }

    @Override
    public EgresoEncabezado cargaEncabezado(HttpServletRequest req) throws Exception {
        return null;
    }

    @Override
    public EgresoEncabezado cargaEncabezado(int folioEgreso) throws Exception {
        EgresoEncabezado encabezado = new EgresoCOMSINVIATICOSEncabezado();
        encabezado.setTipoPago("COMSINVIATICOS");
        encabezado.setFolioPago(folioEgreso);
        return encabezado;
    }

    @Override
    public int delete(Connection conn) throws Exception {
        return 0;
    }

    @Override
    public int eliminaRetencion(Connection conn, int idTipoRetencion) throws Exception {
        return 0;
    }

    @Override
    public int generaRetenciones(Connection conn) throws Exception {
        return 0;
    }

    @Override
    public String getNombreAnexo() {
        return null;
    }

    @Override
    public String getNombreSolicitudPago() {
        return null;
    }

    @Override
    public String getPrefijoCR(Connection conn) throws Exception {
        return null;
    }

    @Override
    public void rechazaPago(Connection conn, String motivoRechazo) throws Exception {
    }

    @Override
    public Map<String, String> resumenConcepto(Connection conn) throws Exception {
        return null;
    }

    @Override
    public Map<String, String> resumenPago(Connection conn) throws Exception {
        return null;
    }

    @Override
    public List<Map<String, String>> resumenRetenciones(Connection conn) throws Exception {
        return null;
    }

    @Override
    public int save(Connection conn) throws Exception {
        return 0;
    }

    @Override
    public Amortizacion getAmortizacion(Connection conn) throws Exception {
        return null;
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
