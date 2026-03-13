package com.axtel.cfdi.core;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.LogManager;
import com.axtel.cfdi.CFDI;
import com.axtel.cfdi.CFDIEncabezado;
import com.syc.cfdi.util.CFDIUtils;
import mx.grupocorasa.sat.cfd._40.Comprobante;
import mx.grupocorasa.sat.cfdi.v4.CFDv40;
import mx.grupocorasa.sat.common.TimbreFiscalDigital11.TimbreFiscalDigital;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CFDIEncabezadoManager {

    private static final Logger log = LogManager.getLogger(CFDIEncabezadoManager.class);

    public static void actualizarCFDIEncabezado(Connection conn, CFDIEncabezado encabezado) throws SQLException {
        log.info("Inicio de actualización del CFDI encabezado con ID: " + encabezado.getCfdiId());
        StringBuilder queryUpdate = new StringBuilder();
        queryUpdate.append("UPDATE CFDI_Encabezado SET ");
        queryUpdate.append("Version = ?, ");
        queryUpdate.append("Serie = ?, ");
        queryUpdate.append("Folio = ?, ");
        queryUpdate.append("Fecha = ?, ");
        queryUpdate.append("Sello = ?, ");
        queryUpdate.append("FormaPago = ?, ");
        queryUpdate.append("NoCertificado = ?, ");
        queryUpdate.append("Certificado = ?, ");
        queryUpdate.append("CondicionesDePago = ?, ");
        queryUpdate.append("Moneda = ?, ");
        queryUpdate.append("TipoDeComprobante = ?, ");
        queryUpdate.append("MetodoPago = ?, ");
        queryUpdate.append("LugarExpedicion = ?, ");
        queryUpdate.append("RegimenFiscal = ?, ");
        queryUpdate.append("DonativoAutorizacion = ?, ");
        queryUpdate.append("DonativoFechaAutorizacion = ?, ");
        queryUpdate.append("TipoRelacion = ?, ");
        queryUpdate.append("UsoCFDI = ?, ");
        queryUpdate.append("ReceptorID = ?, ");
        queryUpdate.append("Periodicidad = ?, ");
        queryUpdate.append("EstatusID = ?, ");
        queryUpdate.append("CadenaOriginal = ?, ");
        queryUpdate.append("UUID = ?, ");
        queryUpdate.append("selloSAT = ?, ");
        queryUpdate.append("CadenaOriginalComplemento = ?, ");
        queryUpdate.append("NoCertificadoSAT = ?, ");
        queryUpdate.append("num_cta_pago = ?, ");
        queryUpdate.append("rfc_prov_certif = ?, ");
        queryUpdate.append("tipoCambio = ? ");
        queryUpdate.append("WHERE CFDI_ID = ?");
        int i = 1;
        try (PreparedStatement ps = conn.prepareStatement(queryUpdate.toString())) {
            log.debug("Preparando parámetros para el encabezado del CFDI.");
            ps.setString(i++, encabezado.getVersion());
            ps.setInt(i++, encabezado.getSerie().getIdSerie());
            ps.setString(i++, encabezado.getFolio());
            ps.setTimestamp(i++, new java.sql.Timestamp(encabezado.getFecha().getTime()));
            ps.setString(i++, encabezado.getSello());
            ps.setString(i++, encabezado.getFormaPago().getFormaPago());
            ps.setString(i++, encabezado.getNoCertificado());
            ps.setString(i++, encabezado.getCertificado());
            ps.setString(i++, encabezado.getCondicionesDePago());
            ps.setString(i++, encabezado.getMoneda().getMoneda());
            ps.setString(i++, encabezado.getTipoDeComprobante().getTipoDeComprobante());
            ps.setString(i++, encabezado.getMetodoPago().getMetodoPago());
            ps.setString(i++, encabezado.getLugarExpedicion().getCodigoPostal());
            ps.setString(i++, encabezado.getRegimenFiscal().getRegimenFiscal());
            ps.setString(i++, encabezado.getDonativoAutorizacion());
            ps.setDate(i++, encabezado.getDonativoFechaAutorizacion() == null ? null : new java.sql.Date(encabezado.getDonativoFechaAutorizacion().getTime()));
            if (encabezado.getTipoRelacion() != null) {
                ps.setString(i++, encabezado.getTipoRelacion().getTipoRelacion());
            } else {
                log.debug("El campo TipoRelacion es nulo.");
                ps.setNull(i++, Types.VARCHAR);
            }
            ps.setString(i++, encabezado.getUsoCFDI().getUsoCFDI());
            ps.setInt(i++, encabezado.getReceptor().getReceptorID());
            if (encabezado.getPeriodicidad() == null) {
                log.debug("El campo Periodicidad es nulo.");
                ps.setNull(i++, Types.VARCHAR);
            } else {
                ps.setString(i++, encabezado.getPeriodicidad().getPeriodicidad());
            }
            ps.setInt(i++, encabezado.getEstatusId());
            ps.setString(i++, encabezado.getCadenaOriginal());
            ps.setString(i++, encabezado.getUuid());
            ps.setString(i++, encabezado.getSelloSAT());
            ps.setString(i++, encabezado.getCadenaOriginalComplemento());
            ps.setString(i++, encabezado.getNoCertificadoSAT());
            ps.setString(i++, encabezado.getNumCtaPago());
            ps.setString(i++, encabezado.getRfcProvCertif());
            ps.setBigDecimal(i++, encabezado.getTipoCambio());
            ps.setInt(i++, encabezado.getCfdiId());
            log.debug("Ejecutando actualización del CFDI encabezado con ID: " + encabezado.getCfdiId());
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                log.error("No se pudo actualizar el encabezado del CFDI con ID: " + encabezado.getCfdiId());
                throw new SQLException("Actualización fallida, no se pudo actualizar el CFDI Encabezado.");
            }
            log.info("CFDI encabezado actualizado correctamente con ID: " + encabezado.getCfdiId());
        } catch (SQLException e) {
            log.error("Error al actualizar el encabezado del CFDI con ID: " + encabezado.getCfdiId() + ". Detalle: " + e.getMessage(), e);
            throw e;
        }
    }

    public static void cambiarEstatusCFDI(Connection conn, int cfdiId, int nuevoEstatus) throws SQLException {
        String query = "UPDATE CFDI_Encabezado SET EstatusID = ? WHERE CFDI_ID = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, nuevoEstatus);
            ps.setInt(2, cfdiId);
            log.trace("Cambiando estatus del CFDI ID " + cfdiId + " a " + nuevoEstatus);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("No se encontró el CFDI con ID " + cfdiId);
            }
        }
    }

    public static CFDIEncabezado guardarCFDIEncabezado(Connection conn, CFDIEncabezado encabezado) throws SQLException {
        StringBuilder queryInsert = new StringBuilder();
        queryInsert.append("INSERT INTO CFDI_Encabezado (");
        queryInsert.append("Version, Serie, Folio, Fecha, Sello, FormaPago, NoCertificado, ");
        queryInsert.append("Certificado, CondicionesDePago, SubTotal, Moneda, Total, ");
        queryInsert.append("TipoDeComprobante, MetodoPago, LugarExpedicion, RegimenFiscal, ");
        queryInsert.append("DonativoAutorizacion, DonativoFechaAutorizacion, TipoRelacion, UsoCFDI, ");
        queryInsert.append("ReceptorID, Periodicidad, EstatusID,num_cta_pago,tipoCambio) ");
        queryInsert.append("VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?)");
        try (PreparedStatement ps = conn.prepareStatement(queryInsert.toString(), PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, encabezado.getVersion());
            ps.setInt(2, encabezado.getSerie().getIdSerie());
            ps.setString(3, encabezado.getFolio());
            ps.setDate(4, new java.sql.Date(encabezado.getFecha().getTime()));
            ps.setString(5, encabezado.getSello());
            ps.setString(6, encabezado.getFormaPago().getFormaPago());
            ps.setString(7, encabezado.getNoCertificado());
            ps.setString(8, encabezado.getCertificado());
            ps.setString(9, encabezado.getCondicionesDePago());
            ps.setBigDecimal(10, encabezado.getSubTotal());
            ps.setString(11, encabezado.getMoneda().getMoneda());
            ps.setBigDecimal(12, encabezado.getTotal());
            ps.setString(13, encabezado.getTipoDeComprobante().getTipoDeComprobante());
            ps.setString(14, encabezado.getMetodoPago().getMetodoPago());
            ps.setString(15, encabezado.getLugarExpedicion().getCodigoPostal());
            ps.setString(16, encabezado.getEmisor().getRegimenFiscal());
            ps.setString(17, encabezado.getDonativoAutorizacion());
            if (encabezado.getDonativoFechaAutorizacion() == null) {
                ps.setNull(18, Types.DATE);
            } else {
                ps.setDate(18, new java.sql.Date(encabezado.getDonativoFechaAutorizacion().getTime()));
            }
            if (encabezado.getTipoRelacion() == null) {
                ps.setNull(19, Types.VARCHAR);
            } else {
                ps.setString(19, encabezado.getTipoRelacion().getTipoRelacion());
            }
            ps.setString(20, encabezado.getUsoCFDI().getUsoCFDI());
            ps.setInt(21, encabezado.getReceptor().getReceptorID());
            if (encabezado.getPeriodicidad() == null) {
                ps.setNull(22, Types.VARCHAR);
            } else {
                ps.setString(22, encabezado.getPeriodicidad().getPeriodicidad());
            }
            ps.setInt(23, encabezado.getEstatusId());
            ps.setString(24, encabezado.getNumCtaPago());
            ps.setBigDecimal(25, encabezado.getTipoCambio());
            log.trace("Query: " + queryInsert);
            log.trace("Parámetros: " + encabezado.toString());
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating CFDI encabezado failed, no rows affected.");
            }
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    encabezado.setCfdiId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating CFDI encabezado failed, no ID obtained.");
                }
            }
            return encabezado;
        }
    }

    public static CFDIEncabezado obtenerCFDIEncabezado(Connection conn, int cfdiId) throws SQLException {
        String querySelect = "SELECT * FROM CFDI_Encabezado WHERE CFDI_ID = ?";
        try (PreparedStatement ps = conn.prepareStatement(querySelect)) {
            ps.setInt(1, cfdiId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    CFDIEncabezado encabezado = new CFDIEncabezado();
                    encabezado.setCfdiId(rs.getInt("CFDI_ID"));
                    encabezado.setVersion(rs.getString("Version"));
                    encabezado.setSerie(SerieManager.obtenerSerie(conn, rs.getInt("Serie")));
                    encabezado.setFolio(rs.getString("Folio"));
                    encabezado.setFecha(rs.getTimestamp("Fecha"));
                    encabezado.setSello(rs.getString("Sello"));
                    encabezado.setFormaPago(FormaPagoManager.obtenerFormaPago(conn, rs.getString("FormaPago")));
                    encabezado.setNoCertificado(rs.getString("NoCertificado"));
                    encabezado.setCertificado(rs.getString("Certificado"));
                    encabezado.setCondicionesDePago(rs.getString("CondicionesDePago"));
                    encabezado.setSubTotal(rs.getBigDecimal("SubTotal"));
                    encabezado.setMoneda(MonedaManager.obtenerTipoMoneda(conn, rs.getString("Moneda")));
                    encabezado.setTotal(rs.getBigDecimal("Total"));
                    encabezado.setTipoDeComprobante(TipoDeComprobanteManager.obtenerTipoComprobante(conn, rs.getString("TipoDeComprobante")));
                    encabezado.setMetodoPago(MetodoPagoManager.obtener(conn, rs.getString("MetodoPago")));
                    encabezado.setLugarExpedicion(CodigoPostalManager.obtenerCodigoPostal(conn, rs.getString("LugarExpedicion")));
                    encabezado.setRegimenFiscal(RegimenFiscalManager.obtenerRegimenFiscal(conn, rs.getString("RegimenFiscal")));
                    encabezado.setDonativoAutorizacion(rs.getString("DonativoAutorizacion"));
                    encabezado.setDonativoFechaAutorizacion(rs.getDate("DonativoFechaAutorizacion"));
                    if (rs.getBigDecimal("Descuento") != null)
                        encabezado.setDescuento(rs.getBigDecimal("Descuento"));
                    if (rs.getBigDecimal("Descuento") != null)
                        encabezado.setDescuento(rs.getBigDecimal("Descuento"));
                    if (rs.getString("TipoRelacion") != null)
                        encabezado.setTipoRelacion(TipoRelacionManager.obtener(conn, rs.getString("TipoRelacion")));
                    encabezado.setUsoCFDI(UsoCFDIManager.obtenerUsoCfdi(conn, rs.getString("UsoCFDI")));
                    encabezado.setReceptor(ReceptorManager.obtenerCliente(conn, rs.getInt("ReceptorID")));
                    if (rs.getString("Periodicidad") != null)
                        encabezado.setPeriodicidad(PeriodicidadManager.obtenerPeriodicidad(conn, rs.getString("Periodicidad")));
                    encabezado.setEstatusId(rs.getInt("EstatusID"));
                    encabezado.setCadenaOriginal(rs.getString("CadenaOriginal"));
                    encabezado.setUuid(rs.getString("UUID"));
                    encabezado.setSelloSAT(rs.getString("selloSAT"));
                    encabezado.setCadenaOriginalComplemento(rs.getString("CadenaOriginalComplemento"));
                    encabezado.setNoCertificadoSAT(rs.getString("NoCertificadoSAT"));
                    encabezado.setNumCtaPago(rs.getString("num_cta_pago"));
                    return encabezado;
                } else {
                    throw new SQLException("No CFDI encabezado found with ID: " + cfdiId);
                }
            }
        }
    }

    public static String saveStampedInvoice(Connection conn, CFDI invoice) throws Exception {
        byte[] byteCfdi = null;
        String uuid = "";
        InputStream fxml = null;
        byteCfdi = invoice.getXmlInvoice().getBytes();
        fxml = new ByteArrayInputStream(byteCfdi);
        CFDv40 cfd4 = new CFDv40(fxml);
        TimbreFiscalDigital tfd11 = CFDIUtils.getTFD((Comprobante) cfd4.getComprobanteDocument());
        invoice.getEncabezado().setSello(cfd4.getSelloString());
        invoice.getEncabezado().setCadenaOriginal(cfd4.getCadenaOriginal());
        invoice.getEncabezado().setUuid(tfd11.getUUID());
        invoice.getEncabezado().setSelloSAT(tfd11.getSelloSAT());
        invoice.getEncabezado().setCadenaOriginalComplemento(CFDIUtils.generaCadenaComplemento(tfd11));
        invoice.getEncabezado().setNoCertificadoSAT(tfd11.getNoCertificadoSAT());
        invoice.getEncabezado().setEstatusId(com.axtel.cfdi.CFDI.TIMBRADO);
        invoice.getEncabezado().setCertificado(cfd4.getCertificadoString());
        invoice.getEncabezado().setRfcProvCertif(tfd11.getRfcProvCertif());
        actualizarCFDIEncabezado(conn, invoice.getEncabezado());
        return uuid;
    }

    public void eliminarCFDIEncabezado(Connection conn, int cfdiId) throws SQLException {
        String queryDelete = "DELETE FROM CFDI_Encabezado WHERE CFDI_ID = ?";
        try (PreparedStatement ps = conn.prepareStatement(queryDelete)) {
            ps.setInt(1, cfdiId);
            ps.executeUpdate();
        }
    }

    public List<CFDIEncabezado> obtenerTodosCFDIEncabezados(Connection conn) throws SQLException {
        String querySelectAll = "SELECT * FROM CFDI_Encabezado";
        List<CFDIEncabezado> encabezados = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(querySelectAll);
            ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                CFDIEncabezado encabezado = new CFDIEncabezado();
                encabezado.setCfdiId(rs.getInt("CFDI_ID"));
                encabezado.setVersion(rs.getString("Version"));
                encabezado.setSerie(SerieManager.obtenerSerie(conn, rs.getInt("Serie")));
                encabezado.setFolio(rs.getString("Folio"));
                encabezado.setFecha(rs.getDate("Fecha"));
                encabezado.setSello(rs.getString("Sello"));
                encabezado.setFormaPago(FormaPagoManager.obtenerFormaPago(conn, rs.getString("FormaPago")));
                encabezado.setNoCertificado(rs.getString("NoCertificado"));
                encabezado.setCertificado(rs.getString("Certificado"));
                encabezado.setCondicionesDePago(rs.getString("CondicionesDePago"));
                encabezado.setSubTotal(rs.getBigDecimal("SubTotal"));
                encabezado.setMoneda(MonedaManager.obtenerTipoMoneda(conn, rs.getString("Moneda")));
                encabezado.setTotal(rs.getBigDecimal("Total"));
                encabezado.setTipoDeComprobante(TipoDeComprobanteManager.obtenerTipoComprobante(conn, rs.getString("TipoDeComprobante")));
                encabezado.setMetodoPago(MetodoPagoManager.obtener(conn, rs.getString("MetodoPago")));
                encabezado.setLugarExpedicion(CodigoPostalManager.obtenerCodigoPostal(conn, rs.getString("LugarExpedicion")));
                encabezado.setRegimenFiscal(RegimenFiscalManager.obtenerRegimenFiscal(conn, rs.getString("RegimenFiscal")));
                encabezado.setDonativoAutorizacion(rs.getString("DonativoAutorizacion"));
                encabezado.setDonativoFechaAutorizacion(rs.getDate("DonativoFechaAutorizacion"));
                encabezado.setTipoRelacion(TipoRelacionManager.obtener(conn, rs.getString("TipoRelacion")));
                encabezado.setUsoCFDI(UsoCFDIManager.obtenerUsoCfdi(conn, rs.getString("UsoCFDI")));
                encabezado.setReceptor(ReceptorManager.obtenerCliente(conn, rs.getString("RFC_CLiente")));
                encabezado.setPeriodicidad(PeriodicidadManager.obtenerPeriodicidad(conn, rs.getString("Periodicidad")));
                encabezado.setCadenaOriginal(rs.getString("CadenaOriginal"));
                encabezado.setUuid(rs.getString("UUID"));
                encabezado.setSelloSAT(rs.getString("selloSAT"));
                encabezado.setCadenaOriginalComplemento(rs.getString("CadenaOriginalComplemento"));
                encabezado.setNoCertificadoSAT(rs.getString("NoCertificadoSAT"));
                encabezado.setNumCtaPago(rs.getString("num_cta_pago"));
                encabezados.add(encabezado);
            }
        }
        return encabezados;
    }

    public static void actualizaMonto(Connection conn, int cfdiId) {
        StringBuilder query = new StringBuilder("UPDATE CFDI_Encabezado SET SubTotal = ?, Total = ? WHERE CFDI_ID = ?");
        PreparedStatement ps = null;
        try {
            /*
			 * ps = conn.prepareStatement( query.toString() ); BigDecimal
			 * importerBruto = getImporteBruto(conn, cfdiId); BigDecimal
			 * importerNeto = getImporteNeto(conn, cfdiId);
			 * 
			 * ps.setBigDecimal( 1, importerBruto ); ps.setBigDecimal( 2,
			 * importerNeto ); ps.setInt( 3, cfdiId );
			 * 
			 * int afectados = ps.executeUpdate();
			 */
        } catch (Exception e) {
            throw new RuntimeException("Error actualizando montos del CFDI FOlio: " + cfdiId + ". Causa: " + e, e);
        }
    }

    private static BigDecimal getImporteBruto(Connection conn, int cfdiId) {
        StringBuilder query = new StringBuilder("UPDATE CFDI_Encabezado SET SubTotal = ?, Total = ? WHERE CFDI_ID = ?");
        PreparedStatement ps = null;
        try {
            /*
			 * ps = conn.prepareStatement( query.toString() ); BigDecimal
			 * importerBruto = getImporteBruto(conn, cfdiId); BigDecimal
			 * importerNeto =getImporteNeto(conn, cfdiId);
			 * 
			 * ps.setBigDecimal( 1, importerBruto ); ps.setBigDecimal( 2,
			 * importerNeto ); ps.setInt( 3, cfdiId );
			 * 
			 * int afectados = ps.executeUpdate();
			 */
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Error actualizando montos del CFDI FOlio: " + cfdiId + ". Causa: " + e, e);
        }
    }
}
