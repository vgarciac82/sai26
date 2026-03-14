package com.axtel.cfdi.stamp.repository;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import com.axtel.db.DBUtils;
import com.syc.cfdi.db.CloseObject;
import com.syc.cfdi.util.CFDIUtils;
import mx.grupocorasa.sat.cfd._40.Comprobante;
import mx.grupocorasa.sat.cfd._40.Comprobante.CfdiRelacionados;
import mx.grupocorasa.sat.cfd._40.Comprobante.CfdiRelacionados.CfdiRelacionado;
import mx.grupocorasa.sat.cfd._40.Comprobante.Complemento;
import mx.grupocorasa.sat.cfd._40.Comprobante.Conceptos;
import mx.grupocorasa.sat.cfd._40.Comprobante.Conceptos.Concepto;
import mx.grupocorasa.sat.cfd._40.Comprobante.Emisor;
import mx.grupocorasa.sat.cfd._40.Comprobante.Receptor;
import mx.grupocorasa.sat.cfd._40.ObjectFactory;
import mx.grupocorasa.sat.cfdi.v4.CFDv40;
import mx.grupocorasa.sat.common.Pagos20.Pagos;
import mx.grupocorasa.sat.common.Pagos20.Pagos.Pago;
import mx.grupocorasa.sat.common.Pagos20.Pagos.Pago.DoctoRelacionado;
import mx.grupocorasa.sat.common.Pagos20.Pagos.Totales;
import mx.grupocorasa.sat.common.catalogos.CClaveUnidad;
import mx.grupocorasa.sat.common.catalogos.CExportacion;
import mx.grupocorasa.sat.common.catalogos.CFormaPago;
import mx.grupocorasa.sat.common.catalogos.CMetodoPago;
import mx.grupocorasa.sat.common.catalogos.CMoneda;
import mx.grupocorasa.sat.common.catalogos.CObjetoImp;
import mx.grupocorasa.sat.common.catalogos.CRegimenFiscal;
import mx.grupocorasa.sat.common.catalogos.CTipoDeComprobante;
import mx.grupocorasa.sat.common.catalogos.CTipoRelacion;
import mx.grupocorasa.sat.common.catalogos.CUsoCFDI;
import mx.grupocorasa.sat.common.catalogos.Pagos.CTipoCadenaPago;
import mx.grupocorasa.sat.common.donat11.Donatarias;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StampInvoiceRepository {

    private static final ObjectFactory of = new ObjectFactory();

    private static final Logger log = LoggerFactory.getLogger(StampInvoiceRepository.class);

    private static Collection<? extends DoctoRelacionado> getDoctosRelacionados(Connection conn, int idPago) throws SQLException {
        List<DoctoRelacionado> doctosRelacionados = new ArrayList<>();
        StringBuilder query = new StringBuilder("");
        query.append("SELECT  * ");
        query.append("  FROM  cfdi_pago_detalle drpd ");
        query.append(" WHERE  drpd.pago_id = ?");
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, idPago);
            rs = ps.executeQuery();
            DBUtils.showResultSetInfo(rs);
            while (rs.next()) {
                DoctoRelacionado doctoRelacionado = new DoctoRelacionado();
                doctoRelacionado.setIdDocumento(rs.getString("id_documento"));
                doctoRelacionado.setSerie(rs.getString("serie"));
                doctoRelacionado.setFolio(rs.getString("folio"));
                doctoRelacionado.setMonedaDR(CMoneda.fromValue(rs.getString("moneda")));
                // doctoRelacionado.setEquivalenciaDR(rs.getBigDecimal("equivalenciadr"));
                doctoRelacionado.setEquivalenciaDR(new BigDecimal(1));
                doctoRelacionado.setNumParcialidad(BigInteger.valueOf(rs.getInt("numero_parcialidad")));
                doctoRelacionado.setImpSaldoAnt(rs.getBigDecimal("importe_saldo_anterior").setScale(2, RoundingMode.HALF_UP));
                doctoRelacionado.setImpPagado(rs.getBigDecimal("importe_pagado").setScale(2, RoundingMode.HALF_UP));
                doctoRelacionado.setImpSaldoInsoluto(rs.getBigDecimal("importe_saldo_insoluto").setScale(2, RoundingMode.HALF_UP));
                doctoRelacionado.setObjetoImpDR(CObjetoImp.fromValue(rs.getString("objeto_imp") == null ? "01" : rs.getString("objeto_imp")));
                // doctoRelacionado.setImpuestosDR(rs.getBigDecimal(""));
                // doctoRelacionado.setObjetoImpDR(rs.getString(""));
                doctosRelacionados.add(doctoRelacionado);
            }
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
        return doctosRelacionados;
    }

    public static CFDv40 instanceFromDB(Connection conn, Integer invoiceID) throws Exception {
        String querySelCFDIS = "SELECT * FROM vw_CFDI_Encabezado WITH(NOLOCK) WHERE nFolioPago = ?";
        List<String> schemas = new ArrayList<>();
        ResultSet rsSelCFDIS = null;
        PreparedStatement psSelCFDIS = null;
        try {
            psSelCFDIS = conn.prepareStatement(querySelCFDIS);
            psSelCFDIS.setInt(1, invoiceID);
            rsSelCFDIS = psSelCFDIS.executeQuery();
            rsSelCFDIS.next();
            Comprobante comprobante = StampInvoiceRepository.leeComprobanteDB(rsSelCFDIS);
            Emisor emisor = StampInvoiceRepository.leeEmisorDB(rsSelCFDIS);
            Receptor receptor = StampInvoiceRepository.leeReceptorDB(rsSelCFDIS);
            CfdiRelacionados cfdiRelacionados = StampInvoiceRepository.leeCFIDRelacionadosDB(conn, invoiceID);
            Conceptos conceptos = StampInvoiceRepository.leeConceptosDB(conn, invoiceID, "P".equalsIgnoreCase(comprobante.getTipoDeComprobante().value()));
            comprobante.setEmisor(emisor);
            comprobante.setReceptor(receptor);
            comprobante.setConceptos(conceptos);
            if (cfdiRelacionados != null)
                comprobante.getCfdiRelacionados().add(cfdiRelacionados);
            comprobante.setSello("");
            comprobante.setCertificado("");
            comprobante.setNoCertificado("");
            CFDv40 cfd = null;
            Donatarias donatarias = StampInvoiceRepository.leeComplementoDonataria(conn, invoiceID);
            if (donatarias != null) {
                Complemento complemento = new Complemento();
                complemento.getAny().add(donatarias);
                comprobante.setComplemento(complemento);
                schemas.add("mx.grupocorasa.sat.common.donat11");
            }
            Pagos pagos = StampInvoiceRepository.leePagos(conn, invoiceID);
            if (pagos != null) {
                Complemento complemento = null;
                if (comprobante.getComplemento() == null)
                    complemento = new Complemento();
                else
                    complemento = comprobante.getComplemento();
                complemento.getAny().add(pagos);
                comprobante.setComplemento(complemento);
                schemas.add("mx.grupocorasa.sat.common.Pagos20");
            }
            if (schemas.size() > 0)
                cfd = new CFDv40(comprobante, schemas.toArray(new String[schemas.size()]));
            else
                cfd = new CFDv40(comprobante);
            return cfd;
        } finally {
            CloseObject.closeObject(psSelCFDIS, rsSelCFDIS);
        }
    }

    private static CfdiRelacionados leeCFIDRelacionadosDB(Connection conn, Integer invoiceID) throws Exception {
        StringBuilder queryCFDIRel = new StringBuilder();
        queryCFDIRel.append("SELECT r.cfdi_relacionado_id, ");
        queryCFDIRel.append("       e.cfdi_id, ");
        queryCFDIRel.append("       e.serie, ");
        queryCFDIRel.append("       e.folio, ");
        queryCFDIRel.append("       e.UUID AS cUUIDOrigen, ");
        queryCFDIRel.append("       r.tiporelacion AS cTipoRelacion, ");
        queryCFDIRel.append("       t.descripcion AS TipoRelacionDescripcion ");
        queryCFDIRel.append("FROM   cfdi_relacionado r ");
        queryCFDIRel.append("       JOIN cfdi_encabezado e ");
        queryCFDIRel.append("         ON r.cfdi_relacionado_id_ref = e.cfdi_id ");
        queryCFDIRel.append("       JOIN c_tiporelacion t ");
        queryCFDIRel.append("         ON r.tiporelacion = t.tiporelacion ");
        queryCFDIRel.append("WHERE  r.cfdi_id = " + invoiceID);
        CfdiRelacionados cfdiRelacionados = null;
        Statement stmt = null;
        ResultSet rs = null;
        log.trace("Object: {}", "Executing query to fetch CFDI relationships: " + queryCFDIRel.toString());
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(queryCFDIRel.toString());
            while (rs.next()) {
                if (cfdiRelacionados == null) {
                    log.trace("Initializing CfdiRelacionados object");
                    cfdiRelacionados = of.createComprobanteCfdiRelacionados();
                    String tipoRelacion = rs.getString("cTipoRelacion");
                    log.trace("Object: {}", "Setting TipoRelacion to CfdiRelacionados: " + tipoRelacion);
                    cfdiRelacionados.setTipoRelacion(CTipoRelacion.fromValue(tipoRelacion));
                }
                String uuidOrigen = rs.getString("cUUIDOrigen");
                log.trace("Object: {}", "Adding CfdiRelacionado with UUID: " + uuidOrigen);
                CfdiRelacionado cfdiRelacionado = of.createComprobanteCfdiRelacionadosCfdiRelacionado();
                cfdiRelacionado.setUUID(uuidOrigen);
                cfdiRelacionados.getCfdiRelacionado().add(cfdiRelacionado);
            }
        } catch (Exception e) {
            log.error("Error fetching CFDI relationships for invoice ID: " + invoiceID, e);
            throw e;
        } finally {
            log.trace("Closing ResultSet and Statement objects");
            CloseObject.closeObject(rs, stmt);
        }
        if (cfdiRelacionados == null) {
            log.trace("Object: {}", "No relationships found for invoice ID: " + invoiceID);
        } else {
            log.trace("Object: {}", "Fetched relationships for invoice ID: " + invoiceID);
        }
        return cfdiRelacionados;
    }

    private static Donatarias leeComplementoDonataria(Connection conn, Integer invoiceID) throws Exception {
        StringBuilder query = new StringBuilder();
        query.append("SELECT no_autorizacion,");
        query.append("       leyenda,");
        query.append("       fecha_autorizacion,");
        query.append("       version");
        query.append("  FROM donataria CFD");
        query.append(" WHERE cfdi_id = " + invoiceID);
        Statement stmnt = null;
        ResultSet rs = null;
        Donatarias donatarias = null;
        try {
            stmnt = conn.createStatement();
            rs = stmnt.executeQuery(query.toString());
            if (rs.next()) {
                donatarias = new Donatarias();
                donatarias.setVersion(rs.getString("version"));
                donatarias.setFechaAutorizacion(rs.getDate("fecha_autorizacion").toLocalDate());
                donatarias.setLeyenda(rs.getString("leyenda"));
                donatarias.setNoAutorizacion(rs.getString("no_autorizacion"));
            }
            return donatarias;
        } finally {
            CloseObject.closeObject(rs, stmnt);
        }
    }

    private static Comprobante leeComprobanteDB(ResultSet rsSelCFDIS) throws Exception {
        Comprobante comprobante = of.createComprobante();
        comprobante.setVersion(rsSelCFDIS.getString("cVersion"));
        comprobante.setSerie(rsSelCFDIS.getString("cSerie"));
        comprobante.setFolio(rsSelCFDIS.getString("cFolio"));
        comprobante.setFecha(rsSelCFDIS.getTimestamp("dFechaEmision").toLocalDateTime());
        if ("P".equalsIgnoreCase(rsSelCFDIS.getString("cTipoComprobante"))) {
            comprobante.setSubTotal(new BigDecimal(0).setScale(0, RoundingMode.HALF_UP));
            comprobante.setTotal(new BigDecimal(0).setScale(0, RoundingMode.HALF_UP));
        } else {
            comprobante.setSubTotal(CFDIUtils.genBigDecimalData(rsSelCFDIS.getBigDecimal("mSubTotal")));
            comprobante.setTotal(CFDIUtils.genBigDecimalData(rsSelCFDIS.getBigDecimal("mTotal")));
        }
        if (rsSelCFDIS.getBigDecimal("mDescuento") != null)
            comprobante.setDescuento(CFDIUtils.genBigDecimalData(rsSelCFDIS.getBigDecimal("mDescuento")));
        comprobante.setMoneda(CMoneda.fromValue(rsSelCFDIS.getString("cMoneda")));
        comprobante.setTipoDeComprobante(CTipoDeComprobante.fromValue(rsSelCFDIS.getString("cTipoComprobante")));
        System.out.println("Version Produccion 1 Forma Pago vs Metodo Pago: " + rsSelCFDIS.getString("cformapago") + " VS " + rsSelCFDIS.getString("cmetodopago"));
        if (StringUtils.isNotBlank(rsSelCFDIS.getString("cformapago")))
            comprobante.setFormaPago(CFormaPago.fromValue(rsSelCFDIS.getString("cformapago")));
        if (StringUtils.isNotBlank(rsSelCFDIS.getString("cmetodopago")))
            comprobante.setMetodoPago(CMetodoPago.fromValue(rsSelCFDIS.getString("cmetodopago")));
        comprobante.setLugarExpedicion(rsSelCFDIS.getString("cLugarExpedicion"));
        comprobante.setExportacion(CExportacion.fromValue(rsSelCFDIS.getString("cExportacion")));
        return comprobante;
    }

    private static Conceptos leeConceptosDB(Connection conn, Integer invoiceID, boolean esComprobantePago) throws Exception {
        StringBuilder query = new StringBuilder();
        query.append("SELECT dc.cfdi_id, ");
        query.append("       dps.claveprodserv AS cClaveProdServ, ");
        query.append("       dc.cantidad       cCantidad, ");
        query.append("       dc.claveunidad    AS cClaveUnidad, ");
        query.append("       dc.descripcion    AS cDescripcion, ");
        query.append("       dc.valorunitario  AS mValorUnitario, ");
        query.append("       dc.importe        AS mImporte, ");
        query.append("       dc.descuento      AS mDescuento ");
        query.append("FROM   cfdi_encabezado encabezado ");
        query.append("       INNER JOIN cfdi_detalle dc ");
        query.append("               ON encabezado.cfdi_id = dc.cfdi_id ");
        query.append("       INNER JOIN c_claveprodserv dps ");
        query.append("               ON dc.claveprodserv = dps.claveprodserv ");
        query.append("WHERE  dc.cfdi_id = " + invoiceID);
        Statement stmnt = null;
        ResultSet rs = null;
        Conceptos conceptos = null;
        try {
            stmnt = conn.createStatement();
            rs = stmnt.executeQuery(query.toString());
            while (rs.next()) {
                Concepto concepto = of.createComprobanteConceptosConcepto();
                concepto.setClaveProdServ(rs.getString("cClaveProdServ"));
                concepto.setCantidad(rs.getBigDecimal("cCantidad").setScale(0, RoundingMode.HALF_UP));
                concepto.setClaveUnidad(CClaveUnidad.fromValue(rs.getString("cClaveUnidad")));
                concepto.setDescripcion(rs.getString("cDescripcion"));
                if (esComprobantePago) {
                    concepto.setValorUnitario(new BigDecimal(0).setScale(0));
                    concepto.setImporte(new BigDecimal(0).setScale(0));
                } else {
                    concepto.setValorUnitario(CFDIUtils.genBigDecimalData(rs.getBigDecimal("mValorUnitario")));
                    concepto.setImporte(CFDIUtils.genBigDecimalData(rs.getBigDecimal("mImporte")));
                }
                if (rs.getBigDecimal("mDescuento") != null)
                    concepto.setDescuento(CFDIUtils.genBigDecimalData(rs.getBigDecimal("mDescuento")));
                CObjetoImp objImp = CObjetoImp.fromValue("01");
                concepto.setObjetoImp(objImp);
                if (conceptos == null) {
                    conceptos = of.createComprobanteConceptos();
                }
                conceptos.getConcepto().add(concepto);
            }
        } finally {
            CloseObject.closeObject(stmnt, rs);
        }
        return conceptos;
    }

    private static Emisor leeEmisorDB(ResultSet rsSelCFDIS) throws Exception {
        Emisor emisor = of.createComprobanteEmisor();
        emisor.setRfc(rsSelCFDIS.getString("cRFCEmisor"));
        emisor.setNombre(rsSelCFDIS.getString("cNombreEmisor").toUpperCase());
        emisor.setRegimenFiscal(CRegimenFiscal.fromValue(rsSelCFDIS.getString("cRegimenFiscalEmisor")));
        return emisor;
    }

    private static Pagos leePagos(Connection conn, Integer invoiceID) throws SQLException {
        Pagos pagos = null;
        StringBuilder query = new StringBuilder();
        query.append("SELECT * ");
        query.append("  FROM cfdi_pago ");
        query.append(" WHERE cfdi_id  = ?");
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, invoiceID);
            rs = ps.executeQuery();
            while (rs.next()) {
                if (pagos == null)
                    pagos = new Pagos();
                pagos.setVersion("2.0");
                Pago pago = new Pago();
                pago.setFechaPago(rs.getTimestamp("fecha_pago").toLocalDateTime());
                pago.setFormaDePagoP(CFormaPago.fromValue(rs.getString("forma_pago")));
                pago.setMonedaP(CMoneda.fromValue(rs.getString("moneda")));
                if ("MXN".equalsIgnoreCase(rs.getString("moneda")))
                    pago.setTipoCambioP(new BigDecimal(1).setScale(0));
                else
                    pago.setTipoCambioP(rs.getBigDecimal("tipo_cambio").setScale(2, RoundingMode.HALF_UP));
                pago.setMonto(rs.getBigDecimal("monto").setScale(2, RoundingMode.HALF_UP));
                pago.setNumOperacion(rs.getString("numero_operacion"));
                pago.setRfcEmisorCtaOrd(StringUtils.trimToNull(rs.getString("rfc_emisor_cta_ordenante")));
                pago.setNomBancoOrdExt(StringUtils.trimToNull(rs.getString("nombre_banco_ordenante")));
                pago.setCtaOrdenante(StringUtils.trimToNull(rs.getString("cuenta_ordenante")));
                pago.setRfcEmisorCtaBen(StringUtils.trimToNull(rs.getString("rfc_emisor_cta_beneficiario")));
                pago.setCtaBeneficiario(StringUtils.trimToNull(rs.getString("cuenta_beneficiario")));
                if (StringUtils.isNotBlank(rs.getString("tipo_cadena_pago")))
                    pago.setTipoCadPago(CTipoCadenaPago.fromValue(rs.getString("cadena_original_pago")));
                pago.setCadPago(StringUtils.trimToNull(rs.getString("cadena_original_pago")));
                if (StringUtils.isNotBlank(rs.getString("certificado_pago")))
                    pago.setCertPago(rs.getString("certificado_pago").getBytes());
                if (StringUtils.isNotBlank(rs.getString("sello_pago")))
                    pago.setSelloPago(rs.getString("sello_pago").getBytes());
                pago.getDoctoRelacionado().addAll(getDoctosRelacionados(conn, rs.getInt("dinadmin_recepcion_pago_id")));
                // pago.setImpuestosP(rs.getString(""));
                pagos.getPago().add(pago);
            }
            if (pagos != null)
                pagos.setTotales(readTotales(conn, invoiceID));
            return pagos;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }

    private static Receptor leeReceptorDB(ResultSet rsSelCFDIS) throws Exception {
        Receptor receptor = of.createComprobanteReceptor();
        receptor.setRfc(rsSelCFDIS.getString("cRFCReceptor"));
        receptor.setNombre(rsSelCFDIS.getString("cNombreReceptor"));
        receptor.setUsoCFDI(CUsoCFDI.fromValue(rsSelCFDIS.getString("cUsoCFDI")));
        receptor.setDomicilioFiscalReceptor(rsSelCFDIS.getString("cDomicilioFiscalReceptor"));
        if (!StringUtils.isBlank(rsSelCFDIS.getString("regimenFiscalReceptor")))
            receptor.setRegimenFiscalReceptor(CRegimenFiscal.fromValue(rsSelCFDIS.getString("regimenFiscalReceptor")));
        return receptor;
    }

    private static Totales readTotales(Connection conn, Integer invoiceID) throws SQLException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT Sum(monto) AS montoPago ");
        query.append("  FROM   cfdi_pago ");
        query.append(" WHERE  cfdi_id = ?");
        PreparedStatement ps = null;
        ResultSet rs = null;
        Totales totales = null;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, invoiceID);
            rs = ps.executeQuery();
            if (rs.next()) {
                totales = new Totales();
                totales.setMontoTotalPagos(rs.getBigDecimal("montoPago").setScale(2, RoundingMode.HALF_UP));
            }
            return totales;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }
}
