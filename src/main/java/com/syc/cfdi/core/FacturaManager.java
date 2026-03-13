package com.syc.cfdi.core;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipException;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.lang.StringUtils;
import com.axtel.cfdi.ComplementoCombustible.Bonificacion;
import com.axtel.cfdi.ComplementoCombustible.custom.AdendaECC;
import com.axtel.cfdi.ComplementoCombustible.vales.AddendaEfectivale;
import com.google.common.io.Files;
import com.syc.cfdi.ComponentesFactura;
import com.syc.cfdi.FacturaBusinessLogic;
import com.syc.cfdi.FacturaBusinessLogic.DocumentoSAI;
import com.syc.cfdi.MassPaymentInvoiceComponents;
import com.syc.cfdi.core.exception.ArchivoNoAdmitido;
import com.syc.cfdi.security.TripleDesEncryption;
import com.syc.cfdi.utils.FacturaUtils;
import com.syc.cfdi.v3332.Comprobante.Comprobante;
import com.syc.cfdi.v3332.Comprobante.Concepto;
import com.syc.cfdi.v3332.Comprobante.Conceptos;
import com.syc.fortimax.core.Carpeta;
import com.syc.fortimax.core.CarpetaManager;
import com.syc.fortimax.core.Documento;
import com.syc.fortimax.core.DocumentoManager;
import com.syc.fortimax.core.OrgCarpeta;
import com.syc.fortimax.core.OrgCarpetaManager;
import com.syc.fortimax.core.Volumen;
import com.syc.fortimax.core.VolumenManager;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoManager;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.obrapublica.EjercicioFiscalBusinessLogic;
import com.syc.obrapublica.EjercicioFiscalManager;
import com.syc.obrapublica.core.ConfiguraAplicativoManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import com.syc.sai.interfaces.CFDIInterface;
import com.syc.utils.zip.ZipManager;
import com.syc.ws.validacionSAT.Acuse;
import com.syc.ws.validacionSAT.impl.FacturaSATValidacion;
import mx.grupocorasa.sat.cfd._40.Comprobante.CfdiRelacionados;
import mx.grupocorasa.sat.cfd._40.Comprobante.CfdiRelacionados.CfdiRelacionado;
import mx.grupocorasa.sat.cfdi.v3.CFDv32;
import mx.grupocorasa.sat.cfdi.v3.CFDv33;
import mx.grupocorasa.sat.cfdi.v4.CFDv40;
import mx.grupocorasa.sat.common.CfdCommon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FacturaManager {

    private static final String CONTRATO_FORMA_DE_PAGO = "99";

    private static final String CONTRATO_TIPO_CFDI = "I";

    private static final Logger log = LoggerFactory.getLogger(FacturaManager.class);

    private static final String METODO_DE_PAGO = "PPD";

    private static final String POR_DEFINIR = "99";

    private static final String PPD = "PPD";

    private static final String SAT_PROCESO_CANCELACION = "en proceso";

    private static final String SAT_VIGENTE_STR = "vigente";

    private static final String[] EXCLUDE_PREFIX = { "informe_", "reporte_" };

    public static Comprobante cargaComprobante(File fxml) throws Exception {
        String version = FacturaUtils.readVersion(fxml);
        CfdCommon cfdi = null;
        Comprobante comprobante = null;
        String text = new String(Files.toByteArray(fxml), Charsets.UTF_8);
        text = Util.removeStringBOMChar(text);
        InputStream in = new ByteArrayInputStream(text.getBytes());
        log.debug(text);
        // LAOP - Detect and exclude a UTF-8 BOM
        InputStream inBOM = new BOMInputStream(in);
        if ("3.3".equals(version)) {
            cfdi = new CFDv33(inBOM, "mx.grupocorasa.sat.common.EstadoDeCuentaCombustible12");
            mx.grupocorasa.sat.cfd._33.Comprobante comp = (mx.grupocorasa.sat.cfd._33.Comprobante) cfdi.getComprobanteDocument();
            comprobante = new Comprobante((mx.grupocorasa.sat.cfd._33.Comprobante) comp);
        } else if ("3.2".equals(version)) {
            cfdi = new CFDv32(inBOM, "mx.grupocorasa.sat.common.EstadoDeCuentaCombustible12");
            mx.grupocorasa.sat.cfd._32.Comprobante comp = (mx.grupocorasa.sat.cfd._32.Comprobante) cfdi.getComprobanteDocument();
            comprobante = new Comprobante((mx.grupocorasa.sat.cfd._32.Comprobante) comp);
        } else {
            cfdi = new CFDv40(inBOM, "mx.grupocorasa.sat.common.EstadoDeCuentaCombustible12");
            mx.grupocorasa.sat.cfd._40.Comprobante comp = (mx.grupocorasa.sat.cfd._40.Comprobante) cfdi.getComprobanteDocument();
            comprobante = new Comprobante((mx.grupocorasa.sat.cfd._40.Comprobante) comp);
        }
        if (inBOM != null)
            try {
                inBOM.close();
            } catch (Exception e) {
                log.warn("Problemas cerrando flujo hacia el XML del CFDI " + e.toString());
            }
        if (in != null)
            try {
                in.close();
            } catch (Exception e) {
                log.warn("Problemas cerrando flujo hacia el XML del CFDI " + e.toString());
            }
        in = null;
        inBOM = null;
        return comprobante;
    }

    public static String[] cargaInformacionLlaves(Connection conn) throws Exception {
        String[] llaves = null;
        String query = "SELECT cCertFile , cKeyFile , cKey FROM tCFDIConfig WITH(NOLOCK)";
        Statement stmnt = null;
        ResultSet rs = null;
        try {
            stmnt = conn.createStatement();
            rs = stmnt.executeQuery(query);
            if (rs.next()) {
                llaves = new String[3];
                llaves[0] = rs.getString("cKeyFile");
                llaves[1] = rs.getString("cCertFile");
                llaves[2] = rs.getString("cKey");
            }
            return llaves;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(stmnt);
        }
    }

    private static int eliminaDocumentoFactura(Connection conn, String tituloAplicacion, String folioGestion, String login) throws Exception {
        String queryBuscarGabinete = "SELECT id_gabinete FROM imx" + tituloAplicacion + " WITH(NOLOCK) WHERE folio = ?";
        String queryBuscarCarpeta = "SELECT id_carpeta FROM imx_carpeta WITH(NOLOCK) WHERE titulo_aplicacion = ? AND id_gabinete = ? AND nombre_carpeta IN (?, ?)";
        String queryBuscaDocumentos = "SELECT id_documento FROM imx_documento WITH(NOLOCK) WHERE titulo_aplicacion = ? and id_gabinete = ? AND id_carpeta_padre = ?";
        String queryBorraPaginas = "DELETE FROM imx_pagina WHERE titulo_aplicacion = ? AND id_gabinete = ? AND id_carpeta_padre = ? and id_documento = ?";
        String queryBorraDocumentos = "DELETE FROM imx_documento WHERE titulo_aplicacion = ? AND id_gabinete = ? AND id_carpeta_padre = ? and id_documento = ?";
        PreparedStatement psBuscarGabinete = null;
        PreparedStatement psBuscarCarpeta = null;
        PreparedStatement psBuscarDocumentos = null;
        PreparedStatement psBorraPaginas = null;
        PreparedStatement psBorraDocumentos = null;
        ResultSet rsBuscarGabinete = null;
        ResultSet rsBuscarCarpeta = null;
        ResultSet rsBorraPaginas = null;
        ResultSet rsBorraDocumentos = null;
        int borrados = 0;
        try {
            psBuscarGabinete = conn.prepareStatement(queryBuscarGabinete);
            psBuscarCarpeta = conn.prepareStatement(queryBuscarCarpeta);
            psBuscarDocumentos = conn.prepareStatement(queryBuscaDocumentos);
            psBorraPaginas = conn.prepareStatement(queryBorraPaginas);
            psBorraDocumentos = conn.prepareStatement(queryBorraDocumentos);
            log.trace("Ejecutando query [ " + queryBuscarGabinete + "] [" + folioGestion + "]");
            psBuscarGabinete.setString(1, folioGestion);
            rsBuscarGabinete = psBuscarGabinete.executeQuery();
            if (rsBuscarGabinete.next()) {
                int gabinete = rsBuscarGabinete.getInt(1);
                log.info("Se eliminaran los documentos factura para el pago [" + tituloAplicacion + "] folio [" + folioGestion + "] Gabinete [" + gabinete + "]");
                log.trace("Buscando la carpeta CFDI");
                log.trace("Ejecutando [" + queryBuscarCarpeta + "][" + tituloAplicacion + "]" + "][" + gabinete + "][CFDI][Oficios]");
                psBuscarCarpeta.setString(1, tituloAplicacion);
                psBuscarCarpeta.setInt(2, gabinete);
                psBuscarCarpeta.setString(3, "CFDI");
                psBuscarCarpeta.setString(4, "Oficios");
                rsBuscarCarpeta = psBuscarCarpeta.executeQuery();
                while (rsBuscarCarpeta.next()) {
                    int idCarpetaPadre = rsBuscarCarpeta.getInt(1);
                    log.info("Se eliminaran todos los documentos en la carpeta [" + idCarpetaPadre + "] del pago [" + tituloAplicacion + "] Folio[" + folioGestion + "] Gabinete[" + gabinete + "]");
                    log.trace("Buscando documentos de la carpeta [" + idCarpetaPadre + "]");
                    log.trace("Ejecutando [" + queryBuscaDocumentos + "][" + tituloAplicacion + "][" + gabinete + "][" + idCarpetaPadre + "]");
                    psBuscarDocumentos.setString(1, tituloAplicacion);
                    psBuscarDocumentos.setInt(2, gabinete);
                    psBuscarDocumentos.setInt(3, idCarpetaPadre);
                    rsBorraDocumentos = psBuscarDocumentos.executeQuery();
                    while (rsBorraDocumentos.next()) {
                        int idDocumento = rsBorraDocumentos.getInt(1);
                        log.debug("Eliminando paginas de [" + tituloAplicacion + "_G" + gabinete + "C" + idCarpetaPadre + "D" + idDocumento + "]");
                        log.trace("Ejecutando [" + queryBorraPaginas + "][" + tituloAplicacion + "][" + gabinete + "][" + idCarpetaPadre + "][" + idDocumento + "]");
                        psBorraPaginas.setString(1, tituloAplicacion);
                        psBorraPaginas.setInt(2, gabinete);
                        psBorraPaginas.setInt(3, idCarpetaPadre);
                        psBorraPaginas.setInt(4, idDocumento);
                        int paginasBorradas = psBorraPaginas.executeUpdate();
                        log.info("Se borraron " + paginasBorradas);
                        log.debug("Eliminando documento [" + tituloAplicacion + "_G" + gabinete + "C" + idCarpetaPadre + "D" + idDocumento + "]");
                        log.trace("Ejecutando [" + queryBorraDocumentos + "][" + tituloAplicacion + "][" + gabinete + "][" + idCarpetaPadre + "][" + idDocumento + "]");
                        psBorraDocumentos.setString(1, tituloAplicacion);
                        psBorraDocumentos.setInt(2, gabinete);
                        psBorraDocumentos.setInt(3, idCarpetaPadre);
                        psBorraDocumentos.setInt(4, idDocumento);
                        int documentosBorrdos = psBorraDocumentos.executeUpdate();
                        log.info("Se borraron " + documentosBorrdos + " documentos ");
                        borrados++;
                    }
                }
            }
            return borrados;
        } finally {
            CloseObject.closeObject(rsBuscarGabinete, false);
            CloseObject.closeObject(rsBuscarCarpeta, false);
            CloseObject.closeObject(rsBorraPaginas, false);
            CloseObject.closeObject(rsBorraDocumentos, false);
            CloseObject.closeObject(psBuscarGabinete, false);
            CloseObject.closeObject(psBuscarCarpeta, false);
            CloseObject.closeObject(psBuscarDocumentos, false);
            CloseObject.closeObject(psBorraPaginas, false);
            CloseObject.closeObject(psBorraDocumentos, false);
        }
    }

    public static int eliminaFacturas(Connection conn, String tituloAplicacion, String nFolioPago) throws Exception {
        log.info("Eliminando facturas para el tipo de pago [" + tituloAplicacion + "] folio de pago [" + nFolioPago + "]");
        String queryDeleteFacturas = "DELETE FROM tPagoFactura WHERE cTipoPago = ? AND nFolioPago = ?";
        int eliminados = 0;
        PreparedStatement psFacturas = null;
        try {
            psFacturas = conn.prepareStatement(queryDeleteFacturas);
            psFacturas.setString(1, tituloAplicacion);
            psFacturas.setInt(2, Integer.parseInt(nFolioPago, 10));
            eliminados = eliminaRetencionFacturas(conn, tituloAplicacion, Integer.parseInt(nFolioPago, 10));
            eliminados += eliminaImpuestosFacturas(conn, tituloAplicacion, Integer.parseInt(nFolioPago, 10));
            eliminados += psFacturas.executeUpdate();
            log.info("Se eliminaron " + eliminados + " facturas");
            log.debug("Eliminando documentos");
            return eliminados;
        } finally {
            CloseObject.closeObject(psFacturas, false);
        }
    }

    public static int eliminaFacturas(Connection conn, String tituloAplicacion, String nFolioPago, String folioGestion, String login) throws Exception {
        log.info("Eliminando facturas para el tipo de pago [" + tituloAplicacion + "] folio de pago [" + nFolioPago + "] folio gestion [" + folioGestion + "] por el usuario [" + login + "]");
        String queryDeleteFacturas = "DELETE FROM tPagoFactura WHERE cTipoPago = ? AND nFolioPago = ?";
        int eliminados = 0;
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(queryDeleteFacturas);
            ps.setString(1, tituloAplicacion);
            ps.setInt(2, Integer.parseInt(nFolioPago, 10));
            eliminados = ps.executeUpdate();
            log.info("Se eliminaron " + eliminados + " facturas");
            log.debug("Eliminando documentos");
            eliminados += FacturaManager.eliminaDocumentoFactura(conn, tituloAplicacion, folioGestion, login);
            return eliminados;
        } finally {
            CloseObject.closeObject(ps, false);
        }
    }

    public static int eliminaImpuestosFacturas(Connection conn, String tituloAplicacion, int nFolioPago) throws Exception {
        log.info("Eliminando impuestos de las facturas para el tipo de pago [" + tituloAplicacion + "] folio de pago [" + nFolioPago + "]");
        String queryDeleteImpuestos = "DELETE FROM tPagoFacturaImpuestos WHERE cTipoPago = ? AND nFolioPago = ?";
        int eliminados = 0;
        PreparedStatement psImpuestos = null;
        try {
            psImpuestos = conn.prepareStatement(queryDeleteImpuestos);
            psImpuestos.setString(1, tituloAplicacion);
            psImpuestos.setInt(2, nFolioPago);
            eliminados += psImpuestos.executeUpdate();
            log.info("Se eliminaron " + eliminados + " facturas");
            return eliminados;
        } finally {
            CloseObject.closeObject(psImpuestos, false);
        }
    }

    public static int eliminaRetencionFacturas(Connection conn, String tituloAplicacion, int nFolioPago) throws Exception {
        log.info("Eliminando retenciones para el tipo de pago [" + tituloAplicacion + "] folio de pago [" + nFolioPago + "]");
        String queryDeleteRetenciones = "DELETE FROM tPagoFacturaRetencion WHERE cTipoPago = ? AND nFolioPago = ?";
        int eliminados = 0;
        PreparedStatement psRetenciones = null;
        try {
            psRetenciones = conn.prepareStatement(queryDeleteRetenciones);
            psRetenciones.setString(1, tituloAplicacion);
            psRetenciones.setInt(2, nFolioPago);
            eliminados = psRetenciones.executeUpdate();
            log.info("Se eliminaron " + eliminados + " retenciones en facturas");
            return eliminados;
        } finally {
            CloseObject.closeObject(psRetenciones, false);
        }
    }

    public static boolean existeCFDIPadreContrato(Connection conn, String idContrato, String UUID) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean existe = false;
        String query = "select COUNT(*) AS existe from tContratoFactura with(Nolock) where cIDContrato='" + idContrato + "' and cFactura = '" + UUID + "'";
        try {
            log.trace("Query[" + query + "]");
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next()) {
                existe = (rs.getInt(1) > 0);
            }
            return existe;
        } finally {
            ps = null;
            rs = null;
        }
    }

    public static boolean existeFirma(Connection conn) throws SQLException {
        String query = "SELECT COUNT(*) AS existe FROM tCFDIConfig WITH(NOLOCK)";
        Statement stmnt = null;
        ResultSet rs = null;
        try {
            stmnt = conn.createStatement();
            rs = stmnt.executeQuery(query);
            if (rs.next()) {
                return rs.getInt("existe") > 0;
            } else
                return false;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(stmnt);
        }
    }

    public static int facturasCapturadas(Connection con, String gavetaAsociada, int nFolioPago) throws Exception {
        String query = "SELECT Count(*) AS esPagoConfactura " + "FROM   tpagofactura  WITH(NOLOCK) " + "WHERE  crfcfactura <> 'OFICIOCTOFED' " + "       AND ctipopago = ? " + "       AND nfoliopago = ? ";
        PreparedStatement ps = null;
        ResultSet rs = null;
        int nFacturas = 0;
        try {
            ps = con.prepareStatement(query);
            ps.setString(1, gavetaAsociada);
            ps.setInt(2, nFolioPago);
            rs = ps.executeQuery();
            if (rs.next())
                nFacturas = rs.getInt(1);
            return nFacturas;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
    }

    public static String generaRutaDefinitiva(Connection conn) throws Exception {
        Volumen vol = VolumenManager.getVolumen(conn);
        String filename = DocumentoManager.getNextFilename(null, "GES") + ".tif";
        return vol.getUnidad() + vol.getRutaBase() + vol.getRutaDirectorio() + vol.getVolumen() + File.separatorChar + filename;
    }

    public static DocumentoSAI getDocumentoOrigen(Connection conn, String uuidOrigen) throws Exception {
        String query = "SELECT cTipoPago, nFolioPago FROM tPagoFactura WITH( nolock) WHERE cfactura = ?";
        PreparedStatement psDoctoOrigen = null;
        ResultSet rsDoctoOrigen = null;
        DocumentoSAI doctoSAI = null;
        try {
            psDoctoOrigen = conn.prepareStatement(query);
            psDoctoOrigen.setString(1, uuidOrigen);
            rsDoctoOrigen = psDoctoOrigen.executeQuery();
            if (rsDoctoOrigen.next()) {
                doctoSAI = (new FacturaBusinessLogic()).new DocumentoSAI();
                doctoSAI.setFolioDocumento(rsDoctoOrigen.getInt("nFolioPago"));
                doctoSAI.setTipoDocumento(rsDoctoOrigen.getString("cTipoPago"));
                doctoSAI.setUUID(uuidOrigen);
                int idCaso = CasoManager.findIdCasoByFolio(conn, doctoSAI.getTipoDocumento(), doctoSAI.getFolioDocumento());
                if (idCaso > 0)
                    doctoSAI.setIdCaso(idCaso);
                else
                    throw new Exception("No se encontro tramite para adjuntar el comprobante de pago. Notifique al administrador la siguiente informacion: \n" + String.format("UUID Origen[%s]\n Tipo Pago[%s]\n Folio[%d] ", uuidOrigen, doctoSAI.getTipoDocumento(), doctoSAI.getFolioDocumento()));
            } else
                throw new Exception("No se encontro tramite que haya utilizado la factura [" + uuidOrigen + "] en el sistema o la factura no es de pago parcial (Metodo de pago PPD)");
            return doctoSAI;
        } finally {
            CloseObject.closeObject(rsDoctoOrigen, false);
            CloseObject.closeObject(psDoctoOrigen, false);
        }
    }

    private static List<String> getFormaPagoValido(Connection conn, boolean esNotaDeCredito) throws Exception {
        String settingName = esNotaDeCredito ? "NC_FORMA_PAGO_PERMITIDO" : "CFDI_FORMA_PAGO_PERMITIDO";
        String settingVal = StringUtils.trimToEmpty(ConfiguraAplicativoManager.getSystemSetting(conn, settingName));
        return new ArrayList<>(Arrays.asList(settingVal.split(",")));
    }

    private static List<String> getFormaPagoValidoRG(Connection conn) throws Exception {
        String settingVal = StringUtils.trimToEmpty(ConfiguraAplicativoManager.getSystemSetting(conn, "RG_FORMA_PAGO_PERMITIDO"));
        return new ArrayList<>(Arrays.asList(settingVal.split(",")));
    }

    private static int getToleranceDays(Connection conn) throws Exception {
        String configSetting = ConfiguraAplicativoManager.getSystemSetting(conn, "PAYMENT_TOLERANCE_DAYS");
        return StringUtils.isBlank(configSetting) ? -1 : Integer.parseInt(configSetting);
    }

    private static List<String> getUsoCfdiValido(Connection conn, boolean esNotaDeCredito) throws Exception {
        String settingName = esNotaDeCredito ? "USO_NC_PERMITIDO" : "USO_CFDI_PERMITIDO";
        String settingVal = StringUtils.trimToEmpty(ConfiguraAplicativoManager.getSystemSetting(conn, settingName));
        return new ArrayList<>(Arrays.asList(settingVal.split(",")));
    }

    public static String getUUIDContrato(Connection conn, String idContrato) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String uuidContrato = null;
        String query = "select *from tContratoFactura with(Nolock) where cIDContrato='" + idContrato + "'";
        try {
            log.trace("Query[" + query + "]");
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next()) {
                uuidContrato = rs.getString("cFactura");
            }
        } finally {
            ps = null;
            rs = null;
        }
        return uuidContrato;
    }

    public static void guardaFirma(Connection conn, String nombreArchivoCert, String nombreArchivoKey, String password) throws Exception {
        String rutaDefinitivaKey = FacturaManager.generaRutaDefinitiva(conn);
        Util.copiaArchivo(nombreArchivoKey, rutaDefinitivaKey);
        String keyFileEnc = TripleDesEncryption.encryptAndMask(rutaDefinitivaKey);
        String rutaDefinitivaCert = FacturaManager.generaRutaDefinitiva(conn);
        if (rutaDefinitivaKey.equalsIgnoreCase(rutaDefinitivaCert))
            throw new Exception("Hubo una colicion de nombres entre rutas definitivas Reintente");
        Util.copiaArchivo(nombreArchivoCert, rutaDefinitivaCert);
        String certFileEnc = TripleDesEncryption.encryptAndMask(rutaDefinitivaCert);
        String passwordEnc = TripleDesEncryption.encryptAndMask(password);
        FacturaManager.insertaInformacionCert(conn, keyFileEnc, certFileEnc, passwordEnc);
    }

    /**
     * Inserta la informacion de la adenda de vales de combustible.
     *
     * @param conn
     *            COnexion abierta la DB
     * @param tipoPago
     *            Tipo de pago
     * @param nFolioPago
     *            Folio del pago.
     * @param uuid
     *            Identificador del CFDI
     * @param addenda
     *            Adenda con la informacion de los vales.
     * @return Registros insertados.
     * @throws SQLException
     */
    private static int insertaAdendaValesCombustible(Connection conn, String tipoPago, int nFolioPago, String uuid, AddendaEfectivale addenda) throws SQLException {
        PreparedStatement ps = null;
        StringBuilder query = new StringBuilder();
        query.append("INSERT INTO tPagoFacturaValesCombustible");
        query.append("           (cTipoPago");
        query.append("           ,nFolioPago");
        query.append("           ,UUID");
        query.append("           ,mImporteSubtotal");
        query.append("           ,mImporteImpuestos");
        query.append("           ,mImporteTotal");
        query.append("           ,totalEmision");
        query.append("           ,ivaEmision");
        query.append("           ,subTotalEmision");
        query.append("           ,totalFiscal");
        query.append("           ,ivaFiscal");
        query.append("           ,subTotalFiscal)");
        query.append("     VALUES");
        query.append("           (?");
        query.append("           ,?");
        query.append("           ,?");
        query.append("           ,?");
        query.append("           ,?");
        query.append("           ,?");
        query.append("           ,?");
        query.append("           ,?");
        query.append("           ,?");
        query.append("           ,?");
        query.append("           ,?");
        query.append("           ,?)");
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, tipoPago);
            ps.setInt(2, nFolioPago);
            ps.setString(3, uuid);
            ps.setBigDecimal(4, addenda.getDispersion().getValorUnitario());
            ps.setBigDecimal(5, addenda.getDispersion().getImporte().subtract(addenda.getDispersion().getValorUnitario()));
            ps.setBigDecimal(6, addenda.getDispersion().getImporte());
            ps.setBigDecimal(7, addenda.getDispersion().getImporte());
            ps.setBigDecimal(8, addenda.getDispersion().getImporte().subtract(addenda.getDispersion().getValorUnitario()));
            ps.setBigDecimal(9, addenda.getDispersion().getValorUnitario());
            ps.setBigDecimal(10, addenda.getDispersion().getImporte());
            ps.setBigDecimal(11, addenda.getDispersion().getImporte().subtract(addenda.getDispersion().getValorUnitario()));
            ps.setBigDecimal(12, addenda.getDispersion().getValorUnitario());
            int afectados = ps.executeUpdate();
            return afectados;
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static double insertaAlimentacionBrigadistas(Connection conn, String tipoPago, int nFolioPago, String noOficio, double montoOficio) throws Exception {
        String query = "INSERT INTO tPagoFactura " + "        ( cTipoPago , " + "          nFolioPago , " + "          cfactura , " + "          mImporteBruto , " + "          cRFCFactura , " + "          mImporteConIVA , " + "          mImporteIVA " + "        ) " + "VALUES  ( ? ,  " + "          ? ,  " + "          ? ,  " + "          ? ,  " + "          ? ,  " + "          ? ,  " + "          ?   " + "        ) ";
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query);
            String rfcOrigen = "OFICIOALIMENTACION";
            ps.setString(1, tipoPago);
            ps.setInt(2, nFolioPago);
            ps.setString(3, noOficio);
            ps.setDouble(4, montoOficio);
            ps.setString(5, rfcOrigen);
            ps.setDouble(6, montoOficio);
            ps.setDouble(7, 0d);
            return ps.executeUpdate();
        } finally {
            CloseObject.closeObject(ps, false);
        }
    }

    public static int insertaArchivosFactura(Connection conn, Caso c, Usuario u, Map<String, ? extends ComponentesFactura> facturas, boolean esNotaCredito) throws Exception {
        int insertados = 0;
        if (c.getIdGabinete() <= 0)
            throw new Exception("No se ha guardado el tramite. Debe guardar el tramite primero para anexar facturas.");
        Carpeta cfdi = FacturaManager.obtenCarpetaDestino(conn, c, (esNotaCredito ? "NC" : "CFDI"), u.getLogin());
        for (Iterator<String> i = facturas.keySet().iterator(); i.hasNext(); ) {
            String facturaNombre = i.next();
            log.debug("Insertando factura [" + facturaNombre + "] ");
            ComponentesFactura cf = facturas.get(facturaNombre);
            insertados += FacturaManager.insertaArchivosFactura(conn, facturaNombre, cf, cfdi, c, u.getLogin());
        }
        return insertados;
    }

    public static int insertaArchivosFactura(Connection conn, String nombreFactura, ComponentesFactura cf, Carpeta c, Caso caso, String uLogin) throws Exception {
        log.trace("Iniciando la insercion en expediente de la factura");
        int total = 0;
        long start = System.currentTimeMillis();
        log.trace("Insertando archivo PDF");
        for (int i = 0; i < 2; i++) {
            File origen;
            if (i == 0)
                origen = new File(cf.getPdfPathFile());
            else
                origen = new File(cf.getXmlPathFile());
            log.trace("Insertando el archivo: " + origen.getName());
            String tmpFile = origen.getName();
            int pos = tmpFile.lastIndexOf('.') + 1;
            String ext = pos != -1 ? tmpFile.substring(pos) : "";
            DocumentoManager.insertaDocumento(conn, c.getTituloAplicacion(), c.getIdGabinete(), c.getIdCarpeta(), nombreFactura + "." + ext, ext, uLogin, origen.getAbsolutePath());
            log.trace("Archivo insertado exitosamente");
            total++;
        }
        long stop = System.currentTimeMillis();
        log.trace("Finalizado insercion en expediene de la factura en [" + ((stop - start) / 1000) + "] s.");
        return total;
    }

    public static int insertaArchivosFactura(Connection conn, String nombreFactura, ComponentesFactura cf, Carpeta c, String uLogin) throws Exception {
        return insertaArchivosFactura(conn, nombreFactura, cf, c, null, uLogin);
    }

    public static int insertaArchivosREP(Connection conn, String nombreFactura, ComponentesFactura cf, Carpeta c, Caso caso, String uLogin) throws Exception {
        log.trace("Iniciando la insercion en expediente de la factura");
        int total = 0;
        long start = System.currentTimeMillis();
        log.trace("Insertando archivo PDF");
        for (int i = 0; i < 2; i++) {
            File origen;
            if (i == 0)
                origen = new File(cf.getPdfPathFile());
            else
                origen = new File(cf.getXmlPathFile());
            log.trace("Insertando el archivo: " + origen.getName());
            String tmpFile = origen.getName();
            int pos = tmpFile.lastIndexOf('.') + 1;
            String ext = pos != -1 ? tmpFile.substring(pos) : "";
            Documento d = DocumentoManager.getDocumento(conn, c.getTituloAplicacion(), c.getIdGabinete(), c.getIdCarpeta(), nombreFactura + "." + ext);
            if (d == null) {
                DocumentoManager.insertaDocumento(conn, c.getTituloAplicacion(), c.getIdGabinete(), c.getIdCarpeta(), nombreFactura + "." + ext, ext, uLogin, origen.getAbsolutePath());
                log.trace("Archivo insertado exitosamente");
                total++;
            } else
                log.info("El documento: --[" + d + "]-- YA EXISTIA COMO RECIBO. No se adjunta nuevamente-");
        }
        long stop = System.currentTimeMillis();
        log.trace("Finalizado insercion en expediene de la factura en [" + ((stop - start) / 1000) + "] s.");
        return total;
    }

    /**
     * inserta la informacion referente a la bonificacion en la factura.
     *
     * @param conn
     *            Conexion activa a la base de datos.
     * @param tipoPago
     *            Tipo de pago
     * @param nFolioPago
     *            FOlio de pago
     * @param uuid
     *            Identificador de la factura
     * @param bonificacion
     *            Bonificacion.
     * @return Registros insertados.
     * @throws SQLException
     */
    private static int insertaBonificacion(Connection conn, String tipoPago, int nFolioPago, String uuid, Bonificacion bonificacion) throws SQLException {
        PreparedStatement ps = null;
        StringBuilder query = new StringBuilder();
        query.append("INSERT INTO tPagoFacturaBonificacion ");
        query.append("           (cTipoPago ");
        query.append("           ,nFolioPago ");
        query.append("           ,UUID ");
        query.append("           ,mImporte ");
        query.append("           ,mImporteImpuestos ");
        query.append("           ,mImporteTotal) ");
        query.append("     VALUES ");
        query.append("           (? ");
        query.append("           ,? ");
        query.append("           ,? ");
        query.append("           ,? ");
        query.append("           ,? ");
        query.append("           ,? )");
        log.trace("Query: \n" + query);
        try {
            log.debug("Insertando bonificacion: " + bonificacion);
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, tipoPago);
            ps.setInt(2, nFolioPago);
            ps.setString(3, uuid);
            ps.setBigDecimal(4, bonificacion.getImporte());
            ps.setBigDecimal(5, bonificacion.getTraslado());
            ps.setBigDecimal(6, bonificacion.getImporte().add(bonificacion.getTraslado()));
            int afectados = ps.executeUpdate();
            return afectados;
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static int insertaConceptoImpuestos(Connection conn, Concepto concepto) throws SQLException {
        StringBuilder query = new StringBuilder();
        query.append("INSERT INTO tCFDIConceptoImpuestos ");
        query.append("           (nIDConcepto ");
        query.append("           ,cImpuesto ");
        query.append("           ,cTipoFactor ");
        query.append("           ,nTasaOCuota ");
        query.append("           ,mBase ");
        query.append("           ,mImporte) ");
        query.append("     VALUES ");
        query.append("           (?, ");
        query.append("            ?, ");
        query.append("            ?, ");
        query.append("            ?, ");
        query.append("            ?, ");
        query.append("            ? ");
        query.append("		       )");
        PreparedStatement ps = null;
        int inserts = 0;
        try {
            ps = conn.prepareStatement(query.toString());
            if (concepto.getTraslados() != null)
                for (Traslado traslado : concepto.getTraslados().getTraslados()) {
                    log.trace("Ejecutando: \n" + query + "\n" + traslado);
                    int i = 1;
                    ps.setLong(i++, concepto.getIdConcepto());
                    ps.setString(i++, traslado.getImpuesto());
                    ps.setString(i++, traslado.getTipoFactor());
                    if (traslado.getTasaOCuota() == null)
                        ps.setBigDecimal(i++, new BigDecimal(0.00));
                    else
                        ps.setBigDecimal(i++, traslado.getTasaOCuota());
                    ps.setBigDecimal(i++, traslado.getBase());
                    if (traslado.getImporte() == null)
                        ps.setBigDecimal(i++, new BigDecimal(0.00));
                    else
                        ps.setBigDecimal(i++, traslado.getImporte());
                    inserts = ps.executeUpdate();
                    ps.clearParameters();
                }
            return inserts;
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static int insertaDocAlimentacionBrigadistas(Connection conn, String rutaArchivo, Carpeta c, Caso caso, String uLogin) throws Exception {
        log.trace("Iniciando la insercion de Doctos Extranjero");
        int total = 0;
        long start = System.currentTimeMillis();
        log.trace("Insertando archivo");
        File origen = new File(rutaArchivo);
        log.trace("Insertando el archivo: " + origen.getName());
        String tmpFile = origen.getName();
        int pos = tmpFile.lastIndexOf('.') + 1;
        String ext = pos != -1 ? tmpFile.substring(pos) : "";
        DocumentoManager.insertaDocumento(conn, c.getTituloAplicacion(), c.getIdGabinete(), c.getIdCarpeta(), origen.getName(), ext, uLogin, origen.getAbsolutePath());
        log.trace("Archivo insertado exitosamente");
        total++;
        long stop = System.currentTimeMillis();
        log.trace("Finalizado insercion en expediene de la factura en [" + ((stop - start) / 1000) + "] s.");
        return total;
    }

    public static int insertaDoctoAutNoComprobable(Connection conn, String rutaArcAutNoComprobable, Carpeta oficioAutNoComprobable, Caso c, String login) throws Exception {
        log.trace("Iniciando la insercion de Doctos Sin Factura");
        int total = 0;
        long start = System.currentTimeMillis();
        log.trace("Insertando archivo");
        File origen = new File(rutaArcAutNoComprobable);
        log.trace("Insertando el archivo: " + origen.getName());
        String tmpFile = origen.getName();
        int pos = tmpFile.lastIndexOf('.') + 1;
        String ext = pos != -1 ? tmpFile.substring(pos) : "";
        DocumentoManager.insertaDocumento(conn, oficioAutNoComprobable.getTituloAplicacion(), oficioAutNoComprobable.getIdGabinete(), oficioAutNoComprobable.getIdCarpeta(), origen.getName(), ext, login, origen.getAbsolutePath());
        log.trace("Archivo insertado exitosamente");
        total++;
        long stop = System.currentTimeMillis();
        log.trace("Finalizado insercion en expediene de la factura en [" + ((stop - start) / 1000) + "] s.");
        return total;
    }

    public static int insertaDoctoExtranjero(Connection conn, String rutaArchivo, Carpeta c, Caso caso, String uLogin) throws Exception {
        log.trace("Iniciando la insercion de Doctos Extranjero");
        int total = 0;
        long start = System.currentTimeMillis();
        log.trace("Insertando archivo");
        File origen = new File(rutaArchivo);
        log.trace("Insertando el archivo: " + origen.getName());
        String tmpFile = origen.getName();
        int pos = tmpFile.lastIndexOf('.') + 1;
        String ext = pos != -1 ? tmpFile.substring(pos) : "";
        DocumentoManager.insertaDocumento(conn, c.getTituloAplicacion(), c.getIdGabinete(), c.getIdCarpeta(), origen.getName(), ext, uLogin, origen.getAbsolutePath());
        log.trace("Archivo insertado exitosamente");
        total++;
        long stop = System.currentTimeMillis();
        log.trace("Finalizado insercion en expediene de la factura en [" + ((stop - start) / 1000) + "] s.");
        return total;
    }

    public static void insertaGastosNoComprobables(Connection conn, int nFolio, double monto) throws Exception {
        log.info("Insertando informcion de gastos no comprobables para la RG[" + nFolio + "] Monto[" + monto + "]");
        long start = System.currentTimeMillis();
        log.trace("Inicio de insertando informcion de gastos no comprobables");
        String queryDelete = "DELETE FROM tRelacionGastosNoComprobable WHERE nFolioRelacionGastos = ?";
        String queryInsert = "INSERT INTO tRelacionGastosNoComprobable( nFolioRelacionGastos , mMontoNoComprobable ) VALUES  ( ?,? )";
        log.trace("Query Delete[ " + queryDelete + "]");
        log.trace("Query Insert[ " + queryInsert + "]");
        PreparedStatement psDelete = null;
        PreparedStatement psInsert = null;
        try {
            psDelete = conn.prepareStatement(queryDelete);
            psInsert = conn.prepareStatement(queryInsert);
            psDelete.setInt(1, nFolio);
            int borrados = psDelete.executeUpdate();
            log.debug("Se borraron " + borrados + " registros preexistentes");
            psInsert.setInt(1, nFolio);
            psInsert.setDouble(2, monto);
            int insertados = psInsert.executeUpdate();
            log.debug("Se insertaron " + insertados + " registros.");
            long stop = System.currentTimeMillis();
            log.trace("Finalizado insercion de gastos no comprobables en [" + ((stop - start) / 1000) + "] s.");
        } finally {
            CloseObject.closeObject(psDelete, false);
            CloseObject.closeObject(psInsert, false);
        }
    }

    /**
     * Inserta los impuestos (de existir) de la factura.
     *
     * @param conn
     *            Conexion activa a la base de datos.
     * @param tipoPago
     *            Tipo de pago
     * @param nFolioPago
     *            Folio del pago
     * @param factura
     *            Factura a procesar
     * @return numero de registros insertados;
     * @throws Exception
     */
    public static int insertaImpuestosFactura(Connection conn, String tipoPago, int nFolioPago, Factura factura) throws Exception {
        int afectados = 0;
        PreparedStatement psInsertaImpuestos = null;
        String qInsertaImpuestos = "INSERT INTO tPagoFacturaImpuestos(cTipoPago, nFolioPago, UUID, cNombreImpuesto, mImporteImpuesto,nTazaImpuesto, cTipoFactor, mImporteBase ) VALUES( ?, ?, ?, ?, ?, ?, ?, ?)";
        Traslados traslados = null;
        try {
            psInsertaImpuestos = conn.prepareStatement(qInsertaImpuestos);
            if (factura.getImpuestos() != null) {
                traslados = factura.getImpuestos();
                String tipoFactor = null;
                String nombreImpuesto = null;
                BigDecimal importeImpuesto = null;
                BigDecimal tasa = null;
                BigDecimal base = null;
                for (Iterator<?> itImpuestos = traslados.getTraslado().iterator(); itImpuestos.hasNext(); ) {
                    if (traslados.isVer40()) {
                        mx.grupocorasa.sat.cfd._40.Comprobante.Impuestos.Traslados.Traslado t = (mx.grupocorasa.sat.cfd._40.Comprobante.Impuestos.Traslados.Traslado) itImpuestos.next();
                        nombreImpuesto = t.getImpuesto().value();
                        importeImpuesto = t.getImporte() == null ? new BigDecimal(0.00) : t.getImporte();
                        tasa = t.getTasaOCuota() == null ? new BigDecimal(0.00) : t.getTasaOCuota();
                        tipoFactor = t.getTipoFactor() != null ? t.getTipoFactor().value() : "NC";
                        base = t.getBase();
                    } else {
                        mx.grupocorasa.sat.cfd._33.Comprobante.Impuestos.Traslados.Traslado t = (mx.grupocorasa.sat.cfd._33.Comprobante.Impuestos.Traslados.Traslado) itImpuestos.next();
                        nombreImpuesto = t.getImpuesto().value();
                        importeImpuesto = t.getImporte() == null ? new BigDecimal(0.00) : t.getImporte();
                        tasa = t.getTasaOCuota() == null ? new BigDecimal(0.00) : t.getTasaOCuota();
                        tipoFactor = t.getTipoFactor().value();
                        base = new BigDecimal(0.0);
                    }
                    psInsertaImpuestos.setString(1, tipoPago);
                    psInsertaImpuestos.setInt(2, nFolioPago);
                    psInsertaImpuestos.setString(3, factura.getUUID());
                    psInsertaImpuestos.setString(4, nombreImpuesto);
                    psInsertaImpuestos.setBigDecimal(5, importeImpuesto);
                    psInsertaImpuestos.setBigDecimal(6, tasa);
                    psInsertaImpuestos.setString(7, tipoFactor);
                    psInsertaImpuestos.setBigDecimal(8, base);
                    log.trace(String.format("Ejecutando %s [%s, %d, %s, %s, %.2f, %.2f, %s, %.2f]", qInsertaImpuestos, tipoPago, nFolioPago, factura.getUUID(), nombreImpuesto, importeImpuesto.floatValue(), tasa.floatValue(), tipoFactor, base.floatValue()));
                    afectados = psInsertaImpuestos.executeUpdate();
                    psInsertaImpuestos.clearParameters();
                }
            } else {
                String nombreImpuesto = "002";
                BigDecimal importeImpuesto = new BigDecimal("0.00");
                BigDecimal tasa = new BigDecimal("0.00");
                psInsertaImpuestos.setString(1, tipoPago);
                psInsertaImpuestos.setInt(2, nFolioPago);
                psInsertaImpuestos.setString(3, factura.getUUID());
                psInsertaImpuestos.setString(4, nombreImpuesto);
                psInsertaImpuestos.setBigDecimal(5, importeImpuesto);
                psInsertaImpuestos.setBigDecimal(6, tasa);
                log.trace(String.format("Ejecutando %s [%s, %d, %s, %s, %.2f, %.2f]", qInsertaImpuestos, tipoPago, nFolioPago, factura.getUUID(), nombreImpuesto, importeImpuesto.floatValue(), tasa.floatValue()));
                afectados = psInsertaImpuestos.executeUpdate();
                psInsertaImpuestos.clearParameters();
            }
            log.debug("Se insertaron " + afectados + " retenciones en factura.");
            return afectados;
        } finally {
            CloseObject.closeObject(psInsertaImpuestos, false);
        }
    }

    public static int insertaInformacionCert(Connection conn, String rutaKey, String rutaCert, String key) throws Exception {
        PreparedStatement ps = null;
        String query = "INSERT INTO tCFDIConfig( cCertFile, cKeyFile, cKey )VALUES(?,?,?)";
        int insertados = 0;
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, rutaCert);
            ps.setString(2, rutaKey);
            ps.setString(3, key);
            insertados = ps.executeUpdate();
            return insertados;
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static double insertaInformacionContratoFacturas(Connection conn, String tipoContrato, String idContrato, int ejercicioFiscal, Map<String, ? extends ComponentesFactura> facturas, boolean esNotaCredito) throws Exception {
        double totalInsertado = 0.0d;
        StringBuilder query = new StringBuilder();
        query.append("INSERT INTO tContratoFactura");
        query.append("        ( cTipoContrato ,");
        query.append("          cIDContrato ,");
        query.append("          cFactura ,");
        query.append("          mImporteSinImpuestos ,");
        query.append("          mImporteConImpuestos ,");
        query.append("          mimporteIVA ,");
        query.append("          cRFCFactura ,");
        query.append("          cTipoComprobante ,");
        query.append("          cMetodoPago,");
        query.append("          aEjercicioFiscal,");
        query.append("          cEsNotaCredito");
        query.append("        ) ");
        query.append("VALUES  ( ? ,");
        query.append("          ? ,");
        query.append("          ? ,");
        query.append("          ? ,");
        query.append("          ? ,");
        query.append("          ? ,");
        query.append("          ? ,");
        query.append("          ? ,");
        query.append("          ? ,");
        query.append("          ? ,");
        query.append("          ?");
        query.append("        )");
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query.toString());
            for (Iterator<String> i = facturas.keySet().iterator(); i.hasNext(); ) {
                String factura = i.next();
                ComponentesFactura cf = facturas.get(factura);
                Comprobante comprobante = cf.getComprobante();
                log.trace("Factura [" + factura + "]");
                double montoFacturaSinIVA = comprobante.getSubTotal().doubleValue();
                double montoTotal = comprobante.getTotal().doubleValue();
                double montoIVA = 0.0d;
                double montoTotalImpuestos = 0.0d;
                double montoTotalImpuestosCalculado = 0.0d;
                double factor = esNotaCredito ? -1.0d : 1.0d;
                String noFactura = comprobante.getUUID();
                String rfcOrigen = comprobante.getRFCEmisor();
                montoIVA = comprobante.calculaImpuestosTrasladados();
                if (comprobante.getTotalImpuestosTrasladados() != null) {
                    montoTotalImpuestos = comprobante.getTotalImpuestosTrasladados().doubleValue();
                    log.trace("La factura cuenta con el elemento <TotalImpuestosTrasladados> [" + montoTotal + "]");
                } else {
                    montoTotalImpuestos = montoTotal - montoFacturaSinIVA;
                    log.trace("La factura no cuenta con el elemento <TotalImpuestosTrasladados> Se calcula. Monto Total[" + montoTotal + " Monto Subtotal[" + montoFacturaSinIVA + "] Impuestos[" + montoTotalImpuestos + "]");
                }
                log.trace("UUID [" + noFactura + "]");
                montoTotalImpuestosCalculado = montoTotal - montoFacturaSinIVA - montoIVA;
                if (montoTotalImpuestosCalculado < 0) {
                    montoTotalImpuestosCalculado = 0;
                }
                ps.setString(1, tipoContrato);
                ps.setString(2, idContrato);
                ps.setString(3, noFactura);
                ps.setDouble(4, factor * montoFacturaSinIVA);
                ps.setDouble(5, factor * montoTotal);
                ps.setDouble(6, factor * montoIVA);
                ps.setString(7, rfcOrigen);
                ps.setString(8, comprobante.getTipoComprobante());
                ps.setString(9, comprobante.getMetodoPago());
                ps.setInt(10, ejercicioFiscal);
                ps.setString(11, esNotaCredito ? "S" : "N");
                ps.executeUpdate();
            }
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return totalInsertado;
    }

    public static double insertaInformacionDoctoExtranjero(Connection conn, String tipoPago, int nFolioPago, String noOficio, double montoOficio) throws Exception {
        StringBuilder query = new StringBuilder();
        query.append("INSERT INTO tPagoFactura ");
        query.append("		(cTipoPago, nFolioPago, cfactura, mImporteBruto, cRFCFactura, mImporteConIVA, mImporteIVA ) ");
        query.append("VALUES  (?, ?, ?, ?, ?, ?, ?) ");
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query.toString());
            String rfcOrigen = "EXTRANJERO";
            ps.setString(1, tipoPago);
            ps.setInt(2, nFolioPago);
            ps.setString(3, noOficio);
            ps.setDouble(4, montoOficio);
            ps.setString(5, rfcOrigen);
            ps.setDouble(6, montoOficio);
            ps.setDouble(7, 0d);
            return ps.executeUpdate();
        } finally {
            CloseObject.closeObject(ps, false);
        }
    }

    /**
     * Inserta la informacion del estado de cuenta de viaticos.
     *
     * @param conn
     *            COnexion abierta la DB
     * @param tipoPago
     *            Tipo de pago
     * @param nFolioPago
     *            Folio del pago.
     * @param uuid
     *            Identificador del CFDI
     * @param estadoDeCuentaCombustible
     *            Estado de cuenta.
     * @return Registros insertados.
     * @throws SQLException
     */
    private static int insertaInformacionECC(Connection conn, String tipoPago, int nFolioPago, String uuid, mx.grupocorasa.sat.common.EstadoDeCuentaCombustible12.EstadoDeCuentaCombustible estadoDeCuentaCombustible) throws SQLException {
        PreparedStatement ps = null;
        StringBuilder query = new StringBuilder();
        query.append("INSERT INTO tPagoFacturaECC ");
        query.append("           (cTipoPago ");
        query.append("           ,nFolioPago ");
        query.append("           ,UUID ");
        query.append("           ,mImporteSubtotal ");
        query.append("           ,mImporteImpuestos ");
        query.append("           ,mImporteTotal) ");
        query.append("     VALUES ");
        query.append("           (? ");
        query.append("           ,? ");
        query.append("           ,? ");
        query.append("           ,? ");
        query.append("           ,? ");
        query.append("           ,?) ");
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, tipoPago);
            ps.setInt(2, nFolioPago);
            ps.setString(3, uuid);
            ps.setBigDecimal(4, estadoDeCuentaCombustible.getSubTotal());
            ps.setBigDecimal(5, estadoDeCuentaCombustible.getTotal().subtract(estadoDeCuentaCombustible.getSubTotal()));
            ps.setBigDecimal(6, estadoDeCuentaCombustible.getTotal());
            int afectados = ps.executeUpdate();
            return afectados;
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static void insertaInformacionFacturas(Connection conn, String gavetaAsociada, int nFolioPago, Map<String, ? extends ComponentesFactura> facturas) throws Exception {
        insertaInformacionFacturas(conn, gavetaAsociada, nFolioPago, facturas, false);
    }

    private static boolean existsRelationUUID(Connection conn, String uuidNC, String parentUUID) throws SQLException {
        ResultSet rs = null;
        boolean existe = false;
        StringBuilder querySearchRelaedInfo = new StringBuilder("SELECT COUNT(*) as total FROM tPagoNotaCredito WHERE cUUID = ? AND cUUIDRelacionado = ?");
        PreparedStatement psSearchInfo = null;
        try {
            psSearchInfo = conn.prepareStatement(querySearchRelaedInfo.toString());
            psSearchInfo.setString(1, uuidNC);
            psSearchInfo.setString(2, parentUUID);
            rs = psSearchInfo.executeQuery();
            if (rs.next())
                existe = rs.getInt(1) > 0;
            return existe;
        } finally {
            CloseObject.closeObject(rs);
        }
    }

    public static void insertaNotaCredito(Connection conn, String tipoPago, int folioPago, Comprobante nc) throws SQLException {
        StringBuilder queryInsertNCInfo = new StringBuilder("INSERT INTO tPagoNotaCredito (cTipoPago ,nFolioPago ,cUUID ,cUUIDRelacionado ,mimporteBruto, mimporteconiva ,mimporteiva ,cRFCFactura ,mOtrosImpuestos ,mImporteDescuento ,dFechaNC ,dFechaTimbrado)\r\n" + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        PreparedStatement psInsertInfo = null;
        try {
            psInsertInfo = conn.prepareStatement(queryInsertNCInfo.toString());
            List<CfdiRelacionados> uuidRelatedList = nc.getComprobante40().getCfdiRelacionados();
            String uuidNC = nc.getUUID();
            if (uuidRelatedList != null && uuidRelatedList.size() > 0)
                for (CfdiRelacionados relatedInvoices : uuidRelatedList) {
                    if ("01".equals(relatedInvoices.getTipoRelacion().value())) {
                        List<CfdiRelacionado> relatedInvoiceList = relatedInvoices.getCfdiRelacionado();
                        for (CfdiRelacionado invoice : relatedInvoiceList) {
                            if (existsRelationUUID(conn, nc.getUUID(), invoice.getUUID()))
                                continue;
                            psInsertInfo.setString(1, tipoPago);
                            psInsertInfo.setInt(2, folioPago);
                            psInsertInfo.setString(3, uuidNC);
                            psInsertInfo.setString(4, invoice.getUUID());
                            psInsertInfo.setBigDecimal(5, nc.getSubTotal());
                            psInsertInfo.setBigDecimal(6, nc.getTotal());
                            psInsertInfo.setBigDecimal(7, nc.getTotal().subtract(nc.getSubTotal()));
                            psInsertInfo.setString(8, nc.getRFCEmisor());
                            psInsertInfo.setBigDecimal(9, Util.ZERO);
                            psInsertInfo.setBigDecimal(10, Util.ZERO);
                            psInsertInfo.setDate(11, new Date(nc.getFechaExpedicionMillis()));
                            psInsertInfo.setDate(12, new Date(nc.getFechaTimbradoMillis()));
                            int insertados = psInsertInfo.executeUpdate();
                            log.info("Se inserto " + insertados + " registro con la relacion: " + tipoPago + ", " + folioPago + " " + "[" + uuidNC + "][" + invoice.getUUID() + "]");
                        }
                    }
                }
        } finally {
            CloseObject.closeObject(psInsertInfo);
        }
    }

    /**
     * Inserta la informacion leida delos CFDI en la base de datos y las asigna
     * a un pago.
     *
     * @param conn
     *            Conexion activa a base de datos.
     * @param tipoPago
     *            Tipo de pago
     * @param nFolioPago
     *            Folio del pago
     * @param facturas
     *            Facturas a guardar
     * @param esNotaCredito
     *            Indica si es una nota de credito.
     * @return Total insertado.
     * @throws Exception
     */
    public static double insertaInformacionFacturas(Connection conn, String tipoPago, int nFolioPago, Map<String, ? extends ComponentesFactura> facturas, boolean esNotaCredito) throws Exception {
        double totalInsertado = 0.0d;
        StringBuilder query = new StringBuilder();
        query.append("INSERT INTO tPagoFactura ");
        query.append("        ( cTipoPago , ");
        query.append("          nFolioPago , ");
        query.append("          cfactura , ");
        query.append("          mImporteBruto , ");
        query.append("          cRFCFactura , ");
        query.append("          mImporteConIVA , ");
        query.append("          mImporteIVA,");
        query.append("          mOtrosImpuestos,");
        query.append("          cEsNotaCredito,");
        query.append("          cMetodoPago, ");
        query.append("          cRazonSocial, ");
        query.append("          cRegimenFiscal, ");
        query.append("          dFechaFactura, ");
        query.append("          dFechaTimbrado, ");
        query.append("          serie, ");
        query.append("          folio, ");
        query.append("          cCodigoPostalEmisor, ");
        query.append("          mImporteDescuento");
        query.append("        ) ");
        query.append("VALUES  ( ? ,  ");
        query.append("          ? ,  ");
        query.append("          ? ,  ");
        query.append("          ? ,  ");
        query.append("          ? ,  ");
        query.append("          ? ,  ");
        query.append("          ? ,  ");
        query.append("          ? ,   ");
        query.append("          ? ,   ");
        query.append("          ? ,");
        query.append("          ? ,");
        query.append("          ? ,");
        query.append("          ? ,");
        query.append("          ? ,");
        query.append("          ? ,");
        query.append("          ? ,");
        query.append("          ? ,");
        query.append("          ?    ");
        query.append("        ) ");
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query.toString());
            for (Iterator<String> i = facturas.keySet().iterator(); i.hasNext(); ) {
                String factura = i.next();
                ComponentesFactura cf = facturas.get(factura);
                Comprobante comprobante = cf.getComprobante();
                log.trace("Factura [" + factura + "]");
                BigDecimal montoFacturaSinIVA = new BigDecimal(0.0);
                BigDecimal montoTotal = new BigDecimal(0.0);
                BigDecimal montoIVA = new BigDecimal(0.0);
                BigDecimal montoTotalImpuestos = new BigDecimal(0.0);
                BigDecimal montoTotalImpuestosCalculado = new BigDecimal(0.0);
                BigDecimal montoDescuento = new BigDecimal(0.0);
                if (comprobante.isComplementoCombustible()) {
                    AdendaECC adendaECC = comprobante.getAdendaECC();
                    montoFacturaSinIVA = adendaECC.getSubTotal().subtract(comprobante.getBonificacion().getImporte());
                    montoTotal = adendaECC.getTotal().subtract(comprobante.getBonificacion().getTotal()).subtract(adendaECC.getDescuento());
                    montoIVA = adendaECC.getTraslados().subtract(comprobante.getBonificacion().getTraslado());
                    montoTotalImpuestosCalculado = montoTotal.subtract(montoFacturaSinIVA).subtract(montoIVA);
                    if (montoTotalImpuestosCalculado.compareTo(Util.ZERO) < 0) {
                        montoTotalImpuestosCalculado = new BigDecimal(0.0);
                    }
                } else if (comprobante.isFacturaVales()) {
                    AddendaEfectivale adenda = comprobante.getAdenda();
                    montoFacturaSinIVA = adenda.getDispersion().getValorUnitario().add(comprobante.getSubTotal());
                    montoTotal = adenda.getDispersion().getImporte().add(comprobante.getTotal());
                    montoIVA = (adenda.getDispersion().getImporte().subtract(adenda.getDispersion().getValorUnitario())).add(comprobante.getTotalImpuestosTrasladados());
                    montoTotalImpuestosCalculado = montoIVA;
                } else {
                    montoFacturaSinIVA = comprobante.getSubTotal();
                    montoTotal = comprobante.getTotal();
                    montoIVA = new BigDecimal(comprobante.calculaImpuestosTrasladados());
                    if (comprobante.getTotalImpuestosTrasladados() != null) {
                        montoTotalImpuestos = comprobante.getTotalImpuestosTrasladados();
                        log.trace("La factura cuenta con el elemento <TotalImpuestosTrasladados> [" + montoTotal + "]");
                    } else {
                        montoTotalImpuestos = montoTotal.subtract(montoFacturaSinIVA);
                        log.trace("La factura no cuenta con el elemento <TotalImpuestosTrasladados> Se calcula. Monto Total[" + montoTotal + " Monto Subtotal[" + montoFacturaSinIVA + "] Impuestos[" + montoTotalImpuestos + "]");
                    }
                    montoDescuento = (comprobante.getDescuento() == null ? new BigDecimal(0.0) : comprobante.getDescuento());
                }
                String noFactura = comprobante.getUUID();
                String rfcOrigen = comprobante.getRFCEmisor();
                log.trace("UUID [" + noFactura + "]");
                ps.setString(1, tipoPago);
                ps.setInt(2, nFolioPago);
                ps.setString(3, noFactura);
                ps.setBigDecimal(4, montoFacturaSinIVA.multiply(new BigDecimal(esNotaCredito ? -1.0d : 1.0d)));
                ps.setString(5, rfcOrigen);
                ps.setBigDecimal(6, montoTotal.multiply(new BigDecimal(esNotaCredito ? -1.0d : 1.0d)));
                ps.setBigDecimal(7, montoIVA.multiply(new BigDecimal(esNotaCredito ? -1.0d : 1.0d)).setScale(2, RoundingMode.HALF_UP));
                ps.setBigDecimal(8, montoTotalImpuestosCalculado.multiply(new BigDecimal(esNotaCredito ? -1.0d : 1.0d)));
                ps.setString(9, (esNotaCredito ? "S" : "N"));
                /*
				 * VGC20181004 Se agrega para validacion: Se lee el metodo de
				 * pago
				 */
                ps.setString(10, comprobante.getMetodoPago());
                /*
				 * VGC20220127 Se agregan campos para RESICO
				 */
                ps.setString(11, comprobante.getNombreEmisor());
                ps.setString(12, comprobante.getRegimenEmisor());
                /*
				 * VGC20220218 Se agregan campos para reporte NAFIN
				 */
                ps.setTimestamp(13, new Timestamp(comprobante.getFechaExpedicionMillis()));
                ps.setTimestamp(14, new Timestamp(comprobante.getFechaTimbradoMillis()));
                /*
				 * VGC20250114 Se agrega serie y folio
				 */
                ps.setString(15, comprobante.getFolioSerie());
                ps.setString(16, comprobante.getFolio());
                ps.setString(17, comprobante.getCodigoPostalEmisor());
                ps.setBigDecimal(18, montoDescuento.multiply(new BigDecimal(esNotaCredito ? -1.0d : 1.0d)));
                int logCnt = 1;
                log.trace("Ejecutando: [" + query.toString() + "]" + "\n[" + logCnt++ + "][" + tipoPago + "]" + "\n[" + logCnt++ + "][" + nFolioPago + "]" + "\n[" + logCnt++ + "][" + noFactura + "]" + "\n[" + logCnt++ + "][" + montoFacturaSinIVA.multiply(new BigDecimal(esNotaCredito ? -1.0d : 1.0d)) + "]" + "\n[" + logCnt++ + "][" + rfcOrigen + "]" + "\n[" + logCnt++ + "][" + montoTotal.multiply(new BigDecimal(esNotaCredito ? -1.0d : 1.0d)) + "]" + "\n[" + logCnt++ + "][" + montoIVA.multiply(new BigDecimal(esNotaCredito ? -1.0d : 1.0d)).setScale(2, RoundingMode.HALF_UP) + "]" + "\n[" + logCnt++ + "][" + montoTotalImpuestosCalculado.multiply(new BigDecimal(esNotaCredito ? -1.0d : 1.0d)) + "]" + "\n[" + logCnt++ + "][" + (esNotaCredito ? "S" : "N") + "]" + "\n[" + logCnt++ + "][" + comprobante.getMetodoPago() + "]" + "\n[" + logCnt++ + "][" + comprobante.getNombreEmisor() + "]" + "\n[" + logCnt++ + "][" + comprobante.getRegimenEmisor() + "]" + "\n[" + logCnt++ + "][" + new Timestamp(comprobante.getFechaExpedicionMillis()) + "]" + "\n[" + logCnt++ + "][" + new Timestamp(comprobante.getFechaTimbradoMillis()) + "]" + "\n[" + logCnt++ + "][" + comprobante.getFolioSerie() + "]" + "\n[" + logCnt++ + "][" + comprobante.getFolio() + "]" + "\n[" + logCnt++ + "][" + comprobante.getCodigoPostalEmisor() + "]" + "\n[" + logCnt++ + "][" + montoDescuento.multiply(new BigDecimal(esNotaCredito ? -1.0d : 1.0d)) + "]");
                ps.executeUpdate();
                /*
				 * VGC20160815 Se agrega para validacion: Se leen las
				 * retenciones y los impuestos para guardarlos
				 */
                Factura facturaRetenImp = FacturaUtils.cargaCFDI(comprobante);
                FacturaManager.insertaRetencionesFactura(conn, tipoPago, nFolioPago, facturaRetenImp);
                FacturaManager.insertaImpuestosFactura(conn, tipoPago, nFolioPago, facturaRetenImp);
                FacturaManager.insertConceptoFactura(conn, comprobante.getUUID(), comprobante.getConceptos());
                if (esNotaCredito)
                    insertaNotaCredito(conn, tipoPago, nFolioPago, comprobante);
                if (comprobante.isComplementoCombustible()) {
                    FacturaManager.insertaInformacionECC(conn, tipoPago, nFolioPago, noFactura, comprobante.getEstadoDeCuentaCombustible());
                    FacturaManager.insertaBonificacion(conn, tipoPago, nFolioPago, noFactura, comprobante.getBonificacion());
                } else /*
				 * VGC20231115 Se agrega lectura de adenda de vales de
				 * combustible.
				 */
                if (comprobante.isFacturaVales()) {
                    AddendaEfectivale addenda = comprobante.getAdenda();
                    FacturaManager.insertaAdendaValesCombustible(conn, tipoPago, nFolioPago, noFactura, addenda);
                }
            }
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return totalInsertado;
    }

    public static double insertaInformacionOficio(Connection conn, String tipoPago, int nFolioPago, String noOficio, double montoOficio, double montoImpuestos) throws Exception {
        String query = "INSERT INTO tPagoFactura " + "        ( cTipoPago , " + "          nFolioPago , " + "          cfactura , " + "          mImporteBruto , " + "          cRFCFactura , " + "          mImporteConIVA , " + "          mImporteIVA " + "        ) " + "VALUES  ( ? ,  " + "          ? ,  " + "          ? ,  " + "          ? ,  " + "          ? ,  " + "          ? ,  " + "          ?   " + "        ) ";
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query);
            String rfcOrigen = "OFICIOPAGO";
            ps.setString(1, tipoPago);
            ps.setInt(2, nFolioPago);
            ps.setString(3, noOficio);
            ps.setDouble(4, montoOficio);
            ps.setString(5, rfcOrigen);
            ps.setDouble(6, montoOficio + montoImpuestos);
            ps.setDouble(7, montoImpuestos);
            return ps.executeUpdate();
        } finally {
            CloseObject.closeObject(ps, false);
        }
    }

    public static double insertaInformacionOficioCtoFederalizado(Connection conn, String tipoPago, int nFolioPago, String noOficio, double montoOficio) throws Exception {
        StringBuilder query = new StringBuilder();
        query.append("INSERT INTO tPagoFactura( ");
        query.append("      	  cTipoPago, ");
        query.append("      	  nFolioPago, ");
        query.append("      	  cfactura, ");
        query.append("      	  mImporteBruto, ");
        query.append("      	  cRFCFactura, ");
        query.append("      	  mImporteConIVA, ");
        query.append("      	  mImporteIVA ");
        query.append("      	) ");
        query.append("VALUES  (    ? ,  ");
        query.append("      	    ? ,  ");
        query.append("      	    ? ,  ");
        query.append("      	    ? ,  ");
        query.append("      	    ? ,  ");
        query.append("      	    ? ,  ");
        query.append("      	    ?   ");
        query.append("      	)");
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query.toString());
            String rfcOrigen = "OFICIOCTOFED";
            ps.setString(1, tipoPago);
            ps.setInt(2, nFolioPago);
            ps.setString(3, noOficio);
            ps.setDouble(4, montoOficio);
            ps.setString(5, rfcOrigen);
            ps.setDouble(6, montoOficio);
            ps.setDouble(7, 0d);
            return ps.executeUpdate();
        } finally {
            CloseObject.closeObject(ps, false);
        }
    }

    public static double insertaInformacionOficioTransito(Connection conn, String tipoPago, int nFolioPago, String noOficio, double montoOficio) throws Exception {
        String query = "INSERT INTO tPagoFactura " + "        ( cTipoPago , " + "          nFolioPago , " + "          cfactura , " + "          mImporteBruto , " + "          cRFCFactura , " + "          mImporteConIVA , " + "          mImporteIVA " + "        ) " + "VALUES  ( ? ,  " + "          ? ,  " + "          ? ,  " + "          ? ,  " + "          ? ,  " + "          ? ,  " + "          ?   " + "        ) ";
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query);
            String rfcOrigen = "OFICIOTRANSITO";
            ps.setString(1, tipoPago);
            ps.setInt(2, nFolioPago);
            ps.setString(3, noOficio);
            ps.setDouble(4, montoOficio);
            ps.setString(5, rfcOrigen);
            ps.setDouble(6, montoOficio);
            ps.setDouble(7, 0d);
            return ps.executeUpdate();
        } finally {
            CloseObject.closeObject(ps, false);
        }
    }

    public static double insertaInformacionReciboDePago33(Connection conn, String UUID_REP, mx.grupocorasa.sat.common.Pagos10.Pagos.Pago.DoctoRelacionado docto, String tipoPago, int folioPago) throws Exception {
        double totalInsertado = 0.0d;
        StringBuilder query = new StringBuilder();
        query.append("INSERT INTO tReciboElectronico");
        query.append("        ( UUID_REP ,");
        query.append("          cFolio ,");
        query.append("          cSerie ,");
        query.append("          UUID_CFDI ,");
        query.append("          cParcialidad ,");
        query.append("          mMontoTotal ,");
        query.append("          mImpSaldoAnt ,");
        query.append("          mImpPagado ,");
        query.append("          mImpSaldoInsoluto,");
        query.append("          cTipoPago,");
        query.append("          nFolioPago");
        query.append("        )");
        // UUID_REP
        query.append("VALUES  ( ?,");
        // cFolio
        query.append("          ?,");
        // cSerie
        query.append("          ?,");
        // UUID_CFDI
        query.append("          ?,");
        // cParcialidad
        query.append("          ?,");
        // mMontoTotal
        query.append("          ?,");
        // mImpSaldoAnt
        query.append("          ?,");
        // mImpPagado
        query.append("          ?,");
        // mImpSaldoInsoluto
        query.append("          ?,");
        // cTipoPago
        query.append("          ?,");
        // nFolioPago
        query.append("          ?");
        query.append("        )");
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, UUID_REP);
            ps.setString(2, docto.getFolio());
            ps.setString(3, docto.getSerie());
            ps.setString(4, docto.getIdDocumento());
            long numeroParcialidad = docto.getNumParcialidad() == null ? 1l : docto.getNumParcialidad().longValue();
            ps.setLong(5, numeroParcialidad);
            ps.setBigDecimal(6, docto.getImpPagado());
            ps.setBigDecimal(7, docto.getImpSaldoAnt());
            ps.setBigDecimal(8, docto.getImpPagado());
            ps.setBigDecimal(9, docto.getImpSaldoInsoluto());
            ps.setString(10, tipoPago);
            ps.setInt(11, folioPago);
            ps.executeUpdate();
        } finally {
            CloseObject.closeObject(ps, false);
        }
        return totalInsertado;
    }

    public static double insertaInformacionReciboDePago40(Connection conn, String UUID_REP, mx.grupocorasa.sat.common.Pagos20.Pagos.Pago.DoctoRelacionado docto, String tipoPago, int folioPago) throws Exception {
        log.trace("Inicio insertaInformacionReciboDePago40 - UUID_REP=" + UUID_REP + ", tipoPago=" + tipoPago + ", folioPago=" + folioPago);
        double totalInsertado = 0.0d;
        StringBuilder query = new StringBuilder();
        query.append("INSERT INTO tReciboElectronico");
        query.append("        ( UUID_REP ,");
        query.append("          cFolio ,");
        query.append("          cSerie ,");
        query.append("          UUID_CFDI ,");
        query.append("          cParcialidad ,");
        query.append("          mMontoTotal ,");
        query.append("          mImpSaldoAnt ,");
        query.append("          mImpPagado ,");
        query.append("          mImpSaldoInsoluto,");
        query.append("          cTipoPago,");
        query.append("          nFolioPago");
        query.append("        )");
        // UUID_REP
        query.append("VALUES  ( ?,");
        // cFolio
        query.append("          ?,");
        // cSerie
        query.append("          ?,");
        // UUID_CFDI
        query.append("          ?,");
        // cParcialidad
        query.append("          ?,");
        // mMontoTotal
        query.append("          ?,");
        // mImpSaldoAnt
        query.append("          ?,");
        // mImpPagado
        query.append("          ?,");
        // mImpSaldoInsoluto
        query.append("          ?,");
        // cTipoPago
        query.append("          ?,");
        // nFolioPago
        query.append("          ?");
        query.append("        )");
        log.trace("SQL de insertaInformacionReciboDePago40 construido");
        PreparedStatement ps = null;
        try {
            log.trace("Preparando PreparedStatement para insertaInformacionReciboDePago40");
            ps = conn.prepareStatement(query.toString());
            long numeroParcialidad = docto.getNumParcialidad() == null ? 1L : docto.getNumParcialidad().longValue();
            log.debug("SQL a ejecutar en insertaInformacionReciboDePago40: " + query.toString());
            log.debug("Parámetros SQL insertaInformacionReciboDePago40: " + "[1=" + UUID_REP + ", 2=" + docto.getFolio() + ", 3=" + docto.getSerie() + ", 4=" + docto.getIdDocumento() + ", 5=" + numeroParcialidad + ", 6=" + docto.getImpPagado() + ", 7=" + docto.getImpSaldoAnt() + ", 8=" + docto.getImpPagado() + ", 9=" + docto.getImpSaldoInsoluto() + ", 10=" + tipoPago + ", 11=" + folioPago + "]");
            ps.setString(1, UUID_REP);
            ps.setString(2, docto.getFolio());
            ps.setString(3, docto.getSerie());
            ps.setString(4, docto.getIdDocumento());
            ps.setLong(5, numeroParcialidad);
            ps.setBigDecimal(6, docto.getImpPagado());
            ps.setBigDecimal(7, docto.getImpSaldoAnt());
            ps.setBigDecimal(8, docto.getImpPagado());
            ps.setBigDecimal(9, docto.getImpSaldoInsoluto());
            ps.setString(10, tipoPago);
            ps.setInt(11, folioPago);
            int rows = ps.executeUpdate();
            log.info("insertaInformacionReciboDePago40 - registros afectados en tReciboElectronico: " + rows);
        } finally {
            log.debug("Cerrando PreparedStatement en insertaInformacionReciboDePago40");
            CloseObject.closeObject(ps, false);
            log.debug("PreparedStatement cerrado en insertaInformacionReciboDePago40");
        }
        log.trace("Fin insertaInformacionReciboDePago40");
        return totalInsertado;
    }

    public static int insertaOficioDePago(Connection conn, String rutaArchivo, Carpeta c, Caso caso, String uLogin) throws Exception {
        log.trace("Iniciando la insercion de oficio de pago");
        int total = 0;
        long start = System.currentTimeMillis();
        log.trace("Insertando archivo");
        File origen = new File(rutaArchivo);
        log.trace("Insertando el archivo: " + origen.getName());
        String tmpFile = origen.getName();
        int pos = tmpFile.lastIndexOf('.') + 1;
        String ext = pos != -1 ? tmpFile.substring(pos) : "";
        DocumentoManager.insertaDocumento(conn, c.getTituloAplicacion(), c.getIdGabinete(), c.getIdCarpeta(), "Oficio de Pago", ext, uLogin, origen.getAbsolutePath());
        log.trace("Archivo insertado exitosamente");
        total++;
        long stop = System.currentTimeMillis();
        log.trace("Finalizado insercion en expediene de la factura en [" + ((stop - start) / 1000) + "] s.");
        return total;
    }

    public static int insertaOficioDeTransito(Connection conn, String rutaArchivo, Carpeta c, Caso caso, String uLogin) throws Exception {
        log.trace("Iniciando la insercion de oficio");
        int total = 0;
        long start = System.currentTimeMillis();
        log.trace("Insertando archivo");
        File origen = new File(rutaArchivo);
        log.trace("Insertando el archivo: " + origen.getName());
        String tmpFile = origen.getName();
        int pos = tmpFile.lastIndexOf('.') + 1;
        String ext = pos != -1 ? tmpFile.substring(pos) : "";
        DocumentoManager.insertaDocumento(conn, c.getTituloAplicacion(), c.getIdGabinete(), c.getIdCarpeta(), origen.getName(), ext, uLogin, origen.getAbsolutePath());
        log.trace("Archivo insertado exitosamente");
        total++;
        long stop = System.currentTimeMillis();
        log.trace("Finalizado insercion en expediene de la factura en [" + ((stop - start) / 1000) + "] s.");
        return total;
    }

    public static void insertaRelacionNCContratoFacturas(Connection conn, String tipoContrato, String idContrato, int ejercicioFiscal, Map<String, ? extends ComponentesFactura> facturas, boolean esNotaCredito) throws Exception {
        StringBuilder queryInsert = new StringBuilder("INSERT INTO tContratoFacturaRelacionado(UUID_PADRE, UUID_HIJO, TIPO_RELACION)VALUES( ?,?,?)");
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(queryInsert.toString());
            for (Iterator<String> i = facturas.keySet().iterator(); i.hasNext(); ) {
                String factura = i.next();
                log.trace("Obteniendo CFDI relacionados: " + factura);
                ComponentesFactura cf = facturas.get(factura);
                Comprobante comprobante = cf.getComprobante();
                String uuidHijo = comprobante.getUUID();
                List<CfdiRelacionados> cfdisRel = comprobante.getUUIDRelacionado();
                int insertados = 0;
                if (cfdisRel != null && cfdisRel.size() > 0)
                    for (CfdiRelacionados o : cfdisRel) {
                        String tipoRelacion = o.getTipoRelacion().value();
                        List<CfdiRelacionado> cfdiRelacionadoLst = o.getCfdiRelacionado();
                        for (CfdiRelacionado cfdiRelacionado : cfdiRelacionadoLst) {
                            String uuidPadre = cfdiRelacionado.getUUID();
                            log.trace("Tipo Relacion: " + tipoRelacion + " UUID Padre: " + uuidPadre + " UUID Factura: " + uuidHijo);
                            if (!existeCFDIPadreContrato(conn, idContrato, uuidPadre))
                                throw new RuntimeException("En el archivo: " + factura + " no se encontro referencia a la factura global del contrato: " + idContrato + " Posiblemente esta cargando el archivo incorrecto.");
                            ps.setString(1, uuidPadre);
                            ps.setString(2, uuidHijo);
                            ps.setString(3, tipoRelacion);
                            insertados = ps.executeUpdate();
                        }
                    }
                if (insertados == 0)
                    throw new RuntimeException("En el archivo: " + factura + " no se encontraron CFDIs relacionados. Posiblemente esta cargando el archivo incorrecto.");
            }
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    /**
     * Inserta las retenciones (de existir) de la factura.
     *
     * @param conn
     *            Conexion activa a la base de datos.
     * @param tipoPago
     *            Tipo de pago
     * @param nFolioPago
     *            Folio del pago
     * @param factura
     *            Factura a procesar
     * @return numero de registros insertados;
     * @throws Exception
     */
    public static int insertaRetencionesFactura(Connection conn, String tipoPago, int nFolioPago, Factura factura) throws Exception {
        int afectados = 0;
        if (factura != null && factura.getRetenciones() != null)
            afectados = insertaRetencionesFactura(conn, tipoPago, nFolioPago, factura.getUUID(), factura.getRetenciones());
        log.debug("Se insertaron " + afectados + " retenciones en factura.");
        return afectados;
    }

    /**
     * Inserta las retenciones (de existir) de la factura.
     *
     * @param conn
     *            Conexion activa a la base de datos.
     * @param tipoPago
     *            Tipo de pago
     * @param nFolioPago
     *            Folio del pago
     * @param map
     * @param string
     * @param factura
     *            Factura a procesar
     * @return numero de registros insertados;
     * @throws Exception
     */
    private static int insertaRetencionesFactura(Connection conn, String tipoPago, int nFolioPago, String uuid, Map<String, BigDecimal> retenciones) throws Exception {
        int afectados = 0;
        PreparedStatement psInsertaRetenciones = null;
        String qInsertaRetenciones = "INSERT INTO tPagoFacturaRetencion( cTipoPago, nFolioPago, UUID, cNombreRetencion, mImporteRetencion)  " + "VALUES( ?, ?, ?, ?, ?)";
        try {
            psInsertaRetenciones = conn.prepareStatement(qInsertaRetenciones);
            for (Iterator<String> i = retenciones.keySet().iterator(); i.hasNext(); ) {
                String nombreRetencion = i.next();
                BigDecimal importeRetencion = retenciones.get(nombreRetencion);
                psInsertaRetenciones.setString(1, tipoPago);
                psInsertaRetenciones.setInt(2, nFolioPago);
                psInsertaRetenciones.setString(3, uuid);
                psInsertaRetenciones.setString(4, nombreRetencion);
                psInsertaRetenciones.setBigDecimal(5, importeRetencion);
                log.trace(String.format("Ejecutando %s,[%s, %d, %s, %s, %.2f]", qInsertaRetenciones, tipoPago, nFolioPago, uuid, nombreRetencion, importeRetencion));
                afectados += psInsertaRetenciones.executeUpdate();
                psInsertaRetenciones.clearParameters();
            }
            log.debug("Se insertaron " + afectados + " retenciones en factura.");
            return afectados;
        } finally {
            CloseObject.closeObject(psInsertaRetenciones, false);
        }
    }

    public static int insertConceptoFactura(Connection conn, String UUID, Conceptos conceptos) throws SQLException {
        StringBuilder query = new StringBuilder();
        query.append("INSERT INTO tCFDIConceptos ");
        query.append("           (cUUID ");
        query.append("           ,cClaveProdServ ");
        query.append("           ,nCantidad ");
        query.append("           ,cClaveUnidad ");
        query.append("           ,cDescripcion ");
        query.append("           ,mValorUnitario ");
        query.append("           ,mImporte) ");
        query.append("     VALUES ");
        query.append("           (?, ");
        query.append("           ?, ");
        query.append("           ?, ");
        query.append("           ?, ");
        query.append("           ?, ");
        query.append("           ?, ");
        query.append("           ?) ");
        PreparedStatement ps = null;
        int inserts = 0;
        try {
            ps = conn.prepareStatement(query.toString(), Statement.RETURN_GENERATED_KEYS);
            for (Concepto concepto : conceptos.getConceptos()) {
                log.trace("Ejecutando: " + query + "\n" + concepto);
                int i = 1;
                ps.setString(i++, UUID);
                ps.setString(i++, concepto.getClaveProdServ());
                ps.setBigDecimal(i++, concepto.getCantidad());
                ps.setString(i++, concepto.getClaveUnidad());
                ps.setString(i++, concepto.getDescripcion());
                ps.setBigDecimal(i++, concepto.getValorUnitario());
                ps.setBigDecimal(i++, concepto.getImporte());
                inserts += ps.executeUpdate();
                ResultSet generatedKeys = ps.getGeneratedKeys();
                generatedKeys.next();
                long idConcepto = generatedKeys.getLong(1);
                concepto.setIdConcepto(idConcepto);
                inserts += FacturaManager.insertaConceptoImpuestos(conn, concepto);
                ps.clearParameters();
            }
            return inserts;
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public static Carpeta obtenCarpetaDestino(Connection conn, Caso c, String nombreCarpeta, String uLogin) throws Exception {
        Carpeta cfdiCarpeta = obtenCarpetaDestino(conn, c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), nombreCarpeta, uLogin);
        return cfdiCarpeta;
    }

    public static Carpeta obtenCarpetaDestino(Connection conn, String tituloAplicacion, int idGabinete, String nombreCarpeta, String uLogin) throws Exception {
        log.trace("Inicia busqueda de carpeta [" + nombreCarpeta + "]");
        long start = System.currentTimeMillis();
        Carpeta cfdiCarpeta = CarpetaManager.getCarpetaByName(conn, tituloAplicacion, idGabinete, nombreCarpeta);
        if (cfdiCarpeta == null) {
            log.trace("No existe la carpeta [" + nombreCarpeta + "] se creara.");
            Carpeta modelo = new Carpeta();
            modelo.setTituloAplicacion(tituloAplicacion);
            modelo.setIdGabinete(idGabinete);
            modelo.setIdCarpeta(CarpetaManager.getNextIdCarpeta(conn, tituloAplicacion, idGabinete));
            modelo.setNombreCarpeta(nombreCarpeta);
            modelo.setNombreUsuario(uLogin);
            modelo.setBanderaRaiz("N");
            modelo.setDescripcion("Carpeta que contiene las facturas que ampara el pago");
            modelo.setPassword("-1");
            cfdiCarpeta = CarpetaManager.insertaCarpeta(conn, modelo);
            OrgCarpeta oc = new OrgCarpeta();
            oc.setIdCarpetaHija(cfdiCarpeta.getIdCarpeta());
            oc.setIdCarpetaPadre(0);
            oc.setIdGabinete(idGabinete);
            oc.setNombreHija(cfdiCarpeta.getNombreCarpeta());
            oc.setTituloAplicacion(cfdiCarpeta.getTituloAplicacion());
            OrgCarpetaManager.insert(conn, oc);
            log.trace("Carpeta [" + nombreCarpeta + "] creada con exito.");
        }
        long stop = System.currentTimeMillis();
        log.trace("Finaliza busqueda de carpeta en [" + ((stop - start) / 1000) + "] s.");
        return cfdiCarpeta;
    }

    private static boolean mustExcludeFile(String file) {
        for (String prefix : FacturaManager.EXCLUDE_PREFIX) {
            if (file.toLowerCase().startsWith(prefix.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public static Map<String, MassPaymentInvoiceComponents> organizeInvoicesMassiveSupplierPayment(List<String> files) {
        log.trace("Iniciando organizacion de facturas.");
        long start = System.currentTimeMillis();
        Map<String, MassPaymentInvoiceComponents> content = new HashMap<String, MassPaymentInvoiceComponents>();
        log.trace("Iterando lista de facturas.");
        for (Iterator<String> i = files.iterator(); i.hasNext(); ) {
            String fullFileName = i.next().toLowerCase();
            String fileName = FacturaUtils.obtenNombreArchivoZip(fullFileName, false).toLowerCase();
            log.trace("File[" + fullFileName + "]");
            log.trace("File Name[" + fileName + "]");
            if (mustExcludeFile(fileName))
                continue;
            log.trace("FileName[" + fileName + "]");
            String extencion = FacturaUtils.obtenExtensionArchivoZip(fullFileName).toLowerCase();
            log.trace("Extension[" + extencion + "]");
            log.trace("Creando componentes de factura");
            MassPaymentInvoiceComponents invoice;
            if (!content.containsKey(fileName.toLowerCase())) {
                content.put(fileName.toLowerCase(), new MassPaymentInvoiceComponents());
                log.trace("No existe el elemento [" + fileName + "] en el mapa. Se inserta.");
            }
            invoice = content.get(fileName.toLowerCase());
            if ("PDF".equalsIgnoreCase(extencion)) {
                invoice.setPdfFile(fileName + "." + extencion);
                log.trace("Se inserto el PDF [" + fileName + "." + extencion + "]");
            } else if ("XML".equalsIgnoreCase(extencion)) {
                invoice.setXmlFile(fileName + "." + extencion);
                log.trace("Se inserto el XML [" + fileName + "." + extencion + "]");
            }
        }
        long stop = System.currentTimeMillis();
        log.trace("Terminado organizacion de facturas en [" + ((stop - start) / 1000) + "s. ] ");
        return content;
    }

    public static Map<String, ComponentesFactura> organizaFacturas(List<String> files) throws ArchivoNoAdmitido {
        log.trace("Iniciando organizacion de facturas.");
        long start = System.currentTimeMillis();
        Map<String, ComponentesFactura> contenido = new HashMap<String, ComponentesFactura>();
        log.trace("Iterando lista de facturas.");
        for (Iterator<String> i = files.iterator(); i.hasNext(); ) {
            String nombreArchivoCompleto = i.next();
            log.trace("Archivo[" + nombreArchivoCompleto + "]");
            String nombreArchivo = FacturaUtils.obtenNombreArchivoZip(nombreArchivoCompleto, false);
            log.trace("Nombre Archivo[" + nombreArchivo + "]");
            String extencion = FacturaUtils.obtenExtensionArchivoZip(nombreArchivoCompleto);
            log.trace("Extension[" + extencion + "]");
            if ("".equals(extencion) || (!"PDF".equalsIgnoreCase(extencion) && !("XML".equalsIgnoreCase(extencion))))
                throw new ArchivoNoAdmitido("Se encontro el archivo no permitido [" + nombreArchivoCompleto + "]");
            log.trace("Creando componentes de factura");
            ComponentesFactura factura;
            if (!contenido.containsKey(nombreArchivo)) {
                contenido.put(nombreArchivo, new ComponentesFactura());
                log.trace("No existe el elemento [" + nombreArchivo + "] en el mapa. Se inserta.");
            }
            factura = contenido.get(nombreArchivo);
            if ("PDF".equalsIgnoreCase(extencion)) {
                factura.setPdfFile(nombreArchivo + "." + extencion);
                log.trace("Se inserto el PDF [" + nombreArchivo + "." + extencion + "]");
            } else if ("XML".equalsIgnoreCase(extencion)) {
                factura.setXmlFile(nombreArchivo + "." + extencion);
                log.trace("Se inserto el XML [" + nombreArchivo + "." + extencion + "]");
            }
        }
        long stop = System.currentTimeMillis();
        log.trace("Terminado organizacion de facturas en [" + ((stop - start) / 1000) + "s. ] ");
        return contenido;
    }

    private static int timeToPay(Connection conn) throws SQLException {
        LocalDate lastDayOfMonth = Util.lastDayOfMonth();
        StringBuilder query = new StringBuilder();
        query.append("SELECT	DATEDIFF( day, GETDATE(), ? )   ");
        query.append("		- ");
        query.append("		( ");
        query.append("			SELECT dbo.fn_diasInhabiles(GETDATE(), ? )  ");
        query.append("		) ");
        query.append("		AS dias_entre_fechas  ");
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setDate(1, java.sql.Date.valueOf(lastDayOfMonth));
            ps.setDate(2, java.sql.Date.valueOf(lastDayOfMonth));
            rs = ps.executeQuery();
            if (rs.next()) {
                int timeToPay = rs.getInt(1);
                log.info("Still having " + timeToPay + " to pay completly ");
                return timeToPay;
            } else
                throw new RuntimeException("no es posible determinar los dias restantes para el pago");
        } finally {
            CloseObject.closeObject(ps);
            CloseObject.closeObject(rs);
        }
    }

    public static List<String> validaArchivoFacturas(String rutaArchivoZip) {
        List<String> errores = new ArrayList<String>();
        try {
            List<String> files = ZipManager.listContents(rutaArchivoZip, true);
            Map<String, ComponentesFactura> facturas;
            facturas = organizaFacturas(files);
            List<String> erroresFacturasIncompletas = FacturaManager.validaFacturasCompletas(facturas);
            if (erroresFacturasIncompletas.size() > 0)
                errores.addAll(erroresFacturasIncompletas);
            List<String> erroresFacturasRepetidas = FacturaManager.validaFacturasRepetidasEnArchivo(files);
            if (erroresFacturasRepetidas.size() > 0)
                errores.addAll(erroresFacturasRepetidas);
        } catch (ArchivoNoAdmitido e) {
            log.error(e.getMessage(), e);
            errores.add(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            errores.add("Ocurrio el siguiente error validando facturas: " + e);
        }
        return errores;
    }

    public static List<String> validaBeneficiarios(Map<String, ? extends ComponentesFactura> facturas, String beneficiario) {
        List<String> errores = new ArrayList<>();
        for (Iterator<String> i = facturas.keySet().iterator(); i.hasNext(); ) {
            String archivo = i.next();
            ComponentesFactura cf = facturas.get(archivo);
            try {
                log.trace("Validando el beneficiario en [" + archivo + "]");
                String rfcFactura = StringUtils.trimToEmpty(cf.getComprobante().getRFCReceptor());
                String rfcEsperado = StringUtils.trimToEmpty(beneficiario);
                if (!rfcEsperado.equalsIgnoreCase(rfcFactura)) {
                    String mensajeError = "El beneficiario [" + rfcFactura + "] NO coincide con el RFC esperado [" + rfcEsperado + "]";
                    errores.add(mensajeError);
                    cf.getErrorLog().add(mensajeError);
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                String mensajeError = "Ocurrió un error mientras se validaba el beneficiario en la factura [" + archivo + "]: " + e;
                errores.add(mensajeError);
                cf.getErrorLog().add(mensajeError);
            }
        }
        return errores;
    }

    /*
	 * Segun la normatividad vigente el tipo de comprobante debe ser Ingreso
	 * ("I") la forma de pago Por Definir "99" y el metodo de pago En
	 * parcialidades o Diferido "PPD"
	 */
    public static List<String> validaCFDIContrato(Connection conn, Map<String, ComponentesFactura> facturas) throws Exception {
        List<String> errores = new ArrayList<String>();
        for (Iterator<String> i = facturas.keySet().iterator(); i.hasNext(); ) {
            String factura = i.next();
            log.trace("Validando factura: " + factura);
            ComponentesFactura cf = facturas.get(factura);
            Comprobante comprobante = cf.getComprobante();
            String noFactura = comprobante.getUUID();
            String tipoComprobante = comprobante.getTipoComprobante();
            if (!FacturaManager.CONTRATO_TIPO_CFDI.equalsIgnoreCase(tipoComprobante))
                errores.add("La factura " + noFactura + " no es del tipo esperado: " + FacturaManager.CONTRATO_TIPO_CFDI + " es de tipo " + tipoComprobante);
            String formaPago = comprobante.getFormaPago();
            if (!FacturaManager.CONTRATO_FORMA_DE_PAGO.equalsIgnoreCase(formaPago))
                errores.add("La factura " + noFactura + " no contiene la forma de pago esperada: " + FacturaManager.CONTRATO_FORMA_DE_PAGO + " contiene: " + formaPago);
            String metodoDePago = comprobante.getMetodoPago();
            if (!FacturaManager.METODO_DE_PAGO.equalsIgnoreCase(metodoDePago))
                errores.add("La factura " + noFactura + " no contiene el metodo de pago esperado: " + FacturaManager.METODO_DE_PAGO + " contiene: " + metodoDePago);
        }
        return errores;
    }

    public static List<String> validaCFDIConvenio(Connection conn, Map<String, ComponentesFactura> facturas, String idContrato) throws Exception {
        List<String> errores = new ArrayList<String>();
        List<String> uuid_tipo = null;
        String uuidContrato = null;
        for (Iterator<String> i = facturas.keySet().iterator(); i.hasNext(); ) {
            String factura = i.next();
            log.trace("Validando factura convenio: " + factura);
            ComponentesFactura cf = facturas.get(factura);
            Comprobante comprobante = cf.getComprobante();
            // Obtener uuid relacionado y tipo de relacion
            uuid_tipo = comprobante.getUUIDRelacionado_TipoRelacion();
            if (uuid_tipo == null || uuid_tipo.size() == 0) {
                errores.add("Error al leer el componente de facturaras relacionadas");
                break;
            }
            // obtener el uuid de la factura global del contrato
            uuidContrato = getUUIDContrato(conn, idContrato);
            // validar uuid relacionado exista en tContratoFactura 53108308478
            if (!uuidContrato.equalsIgnoreCase(uuid_tipo.get(0))) {
                errores.add("El UUID de la factura global de contrato " + uuidContrato + " es difernente al uuid relacionado de la factura global de convenio " + uuid_tipo.get(0));
            }
            // validar que el tipo de relación sea 02
            if (!"02".equalsIgnoreCase(uuid_tipo.get(1))) {
                errores.add("El tipo es " + uuid_tipo.get(1) + " y no es el que se requiere para esté tipo de factura");
            }
        }
        return errores;
    }

    public static List<String> validaEjercicioFactura(Connection conn, Map<String, ? extends ComponentesFactura> facturas) {
        try {
            int ejercicio = Integer.parseInt(EjercicioFiscalManager.getEjercicioFiscalActivo(conn).getaEjercicioFiscal());
            return validaEjercicioFactura(facturas, ejercicio);
        } catch (Exception e) {
            log.error("Error validando EF: " + e.toString());
            throw new RuntimeException("Error validando EF: " + e.toString(), e);
        }
    }

    public static List<String> validaEjercicioFactura(Map<String, ? extends ComponentesFactura> facturas) {
        EjercicioFiscalBusinessLogic efbl = new EjercicioFiscalBusinessLogic(GestionInterface.ATT_CONEXION);
        int ejercicio = Integer.parseInt(efbl.getEjercicioFiscalActivo().getaEjercicioFiscal());
        return validaEjercicioFactura(facturas, ejercicio);
    }

    private static List<String> validaEjercicioFactura(Map<String, ? extends ComponentesFactura> facturas, int ejercicio) {
        List<String> errores = new ArrayList<>();
        for (Iterator<String> i = facturas.keySet().iterator(); i.hasNext(); ) {
            String archivo = i.next();
            ComponentesFactura cf = facturas.get(archivo);
            try {
                log.trace("Validando el ejercicio de factura en [" + archivo + "] Ejercicio esperado [" + ejercicio + "]");
                long fechaFactura = cf.getComprobante().getFechaExpedicionMillis();
                Calendar c = Util.toDate(fechaFactura);
                int yEmision = c.get(Calendar.YEAR);
                if (yEmision != ejercicio) {
                    String mensajeError = "El archivo [" + archivo + "] no corresponde al EF actual. EF:" + ejercicio + " Año en factura: " + yEmision;
                    errores.add(mensajeError);
                    cf.getErrorLog().add(mensajeError);
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                String mensajeError = "Ocurrió un error mientras se validaba el ejercicio en la factura [" + archivo + "]: " + e;
                errores.add(mensajeError);
                cf.getErrorLog().add(mensajeError);
            }
        }
        return errores;
    }

    public static List<String> validaEmisor(Map<String, ? extends ComponentesFactura> facturas, String beneficiario) {
        List<String> errores = new ArrayList<>();
        for (Iterator<String> i = facturas.keySet().iterator(); i.hasNext(); ) {
            String archivo = i.next();
            ComponentesFactura cf = facturas.get(archivo);
            try {
                log.trace("Validando el beneficiario en [" + archivo + "]");
                String rfcFactura = StringUtils.trimToEmpty(cf.getComprobante().getRFCEmisor());
                String rfcEsperado = StringUtils.trimToEmpty(beneficiario);
                if (!rfcEsperado.equalsIgnoreCase(rfcFactura)) {
                    String mensajeError = "El emisor de la factura [" + rfcFactura + "] NO coincide con el RFC esperado [" + rfcEsperado + "]";
                    errores.add(mensajeError);
                    cf.getErrorLog().add(mensajeError);
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                String mensajeError = "Ocurrió un error mientras se validaba el emisor en la factura [" + archivo + "]: " + e;
                errores.add(mensajeError);
                cf.getErrorLog().add(mensajeError);
            }
        }
        return errores;
    }

    public static Map<String, MassPaymentInvoiceComponents> validatesCompleteInvoices(Map<String, MassPaymentInvoiceComponents> invoices) {
        log.trace("Iniciando validacion de facturas completas. ");
        long start = System.currentTimeMillis();
        for (Iterator<String> i = invoices.keySet().iterator(); i.hasNext(); ) {
            String invoiceName = i.next();
            MassPaymentInvoiceComponents mpic = invoices.get(invoiceName);
            if (!mpic.componentesCompletos())
                mpic.getErrorLog().add("No se encuentra completa la factura: " + invoiceName + "; debe existir el archivo XML y PDF de la factura.");
        }
        long stop = System.currentTimeMillis();
        log.trace("Terminado validacion de facturas completas en [" + ((stop - start) / 1000) + "s. ] ");
        return invoices;
    }

    public static List<String> validaFacturasCompletas(Map<String, ? extends ComponentesFactura> facturas) {
        log.trace("Iniciando validacion de facturas completas. ");
        long start = System.currentTimeMillis();
        List<String> facturasIncompletas = new ArrayList<String>();
        for (Iterator<String> i = facturas.keySet().iterator(); i.hasNext(); ) {
            String nombreFactura = i.next();
            ComponentesFactura cf = facturas.get(nombreFactura);
            // if ( !soloXML ) {
            if (!cf.componentesCompletos())
                facturasIncompletas.add("No se encuentra completa la factura: " + nombreFactura + "; debe existir el archivo XML y PDF de la factura.");
            // }else {
            // if( StringUtils.isEmpty(cf.getXmlFile()) ) {
            // facturasIncompletas.add( "Solo se adminten XML sin embargo no se
            // adjunto la
            // factura: " + nombreFactura + "; debe existir el archivo XML." );
            // }
            // }
        }
        long stop = System.currentTimeMillis();
        log.trace("Terminado validacion de facturas completas en [" + ((stop - start) / 1000) + "s. ] ");
        return facturasIncompletas;
    }

    public static List<String> validaFacturasRepetidas(Connection conn, Map<String, ? extends ComponentesFactura> facturas) {
        String query = "SELECT ctipopago, nfoliopago, crfcfactura, cfactura FROM dbo.tpagofactura WITH(NOLOCK) WHERE Rtrim(Ltrim(cfactura)) = ?";
        List<String> errores = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            log.trace("Query [" + query + "]");
            for (Iterator<String> i = facturas.keySet().iterator(); i.hasNext(); ) {
                String factura = i.next();
                ComponentesFactura cf = facturas.get(factura);
                try {
                    log.trace("Validando factura: " + factura);
                    Comprobante comprobante = cf.getComprobante();
                    String noFactura = comprobante.getUUID();
                    ps.setString(1, noFactura);
                    log.trace("Ejecutando consulta para: ['" + comprobante.getRFCEmisor() + "', '" + noFactura + "']");
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            String mensajeError = "La factura: " + rs.getString("cfactura") + " ya existe en el pago [" + rs.getString("ctipopago") + "] con el folio [" + rs.getInt("nfoliopago") + "]";
                            errores.add(mensajeError);
                            cf.getErrorLog().add(mensajeError);
                        }
                    }
                } catch (Exception e) {
                    log.error("Error validando factura [" + factura + "]: " + e, e);
                    String mensajeError = "Ocurrió un error mientras se validaba la factura [" + factura + "]: " + e;
                    errores.add(mensajeError);
                    cf.getErrorLog().add(mensajeError);
                }
            }
        } catch (Exception e) {
            log.error("Error preparando la consulta SQL: " + e, e);
            throw new RuntimeException("Error preparando la consulta SQL: " + e.toString(), e);
        }
        return errores;
    }

    public static List<String> validaFacturasRepetidasContratos(Connection conn, Map<String, ? extends ComponentesFactura> facturas) throws Exception {
        String query = " SELECT cTipoContrato, " + "        cIDContrato,  " + "        cRFCFactura," + "        cFactura" + "  FROM  tContratoFactura  WITH(NOLOCK) " + " WHERE  Rtrim(Ltrim(cfactura)) = ? ";
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<String> errores = new ArrayList<String>();
        try {
            log.trace("Query[" + query + "]");
            ps = conn.prepareStatement(query);
            for (Iterator<String> i = facturas.keySet().iterator(); i.hasNext(); ) {
                String factura = i.next();
                log.trace("Validando factura: " + factura);
                ComponentesFactura cf = facturas.get(factura);
                Comprobante comprobante = cf.getComprobante();
                String noFactura = comprobante.getUUID();
                ps.setString(1, noFactura);
                log.trace("Ejecutando consulta para:['" + comprobante.getRFCEmisor() + "', '" + noFactura + "']");
                rs = ps.executeQuery();
                if (rs.next()) {
                    errores.add("La factura: " + rs.getString("cFactura") + " ya existe en el contrato[" + rs.getString("cIDContrato") + "]");
                }
            }
            return errores;
        } finally {
            CloseObject.closeObject(rs, false);
            CloseObject.closeObject(ps, false);
        }
    }

    public static List<String> validaFacturasRepetidasEjerciciosAnteriores(Connection conn, Map<String, ? extends ComponentesFactura> facturas) {
        String query = "SELECT ctipopago, nfoliopago, crfcfactura, cfactura, aEjercicioFiscal " + "FROM v_pagofactura WITH(NOLOCK) " + "WHERE aEjercicioFiscal NOT IN (SELECT aEjercicioFiscal FROM tejerciciofiscal WITH(NOLOCK) WHERE cactivo = 1) " + "AND Rtrim(Ltrim(cfactura)) = ?";
        List<String> errores = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            log.trace("Query [" + query + "]");
            for (Iterator<String> i = facturas.keySet().iterator(); i.hasNext(); ) {
                String factura = i.next();
                ComponentesFactura cf = facturas.get(factura);
                try {
                    log.trace("Validando factura: " + factura);
                    Comprobante comprobante = cf.getComprobante();
                    String noFactura = comprobante.getUUID();
                    ps.setString(1, noFactura);
                    log.trace("Ejecutando consulta para: ['" + comprobante.getRFCEmisor() + "', '" + noFactura + "']");
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            String mensajeError = "La factura: " + rs.getString("cfactura") + " ya existe en el pago [" + rs.getString("ctipopago") + "] con el folio [" + rs.getInt("nfoliopago") + "] en el ejercicio fiscal [" + rs.getString("aEjercicioFiscal") + "]";
                            errores.add(mensajeError);
                            cf.getErrorLog().add(mensajeError);
                        }
                    }
                } catch (Exception e) {
                    log.error("Error validando factura [" + factura + "]: " + e, e);
                    String mensajeError = "Ocurrió un error mientras se validaba la factura [" + factura + "]: " + e;
                    errores.add(mensajeError);
                    cf.getErrorLog().add(mensajeError);
                }
            }
        } catch (Exception e) {
            log.error("Error preparando la consulta SQL: " + e, e);
            throw new RuntimeException("Error preparando la consulta SQL: " + e.toString(), e);
        }
        return errores;
    }

    public static List<String> validaFacturasRepetidasEnArchivo(List<String> archivos) {
        log.trace("Iniciando validacion de facturas repetidas en archivo. ");
        long start = System.currentTimeMillis();
        Map<String, Integer> contador = new HashMap<String, Integer>();
        List<String> repetidos = new ArrayList<String>();
        for (Iterator<String> i = archivos.iterator(); i.hasNext(); ) {
            String fileName = FacturaUtils.obtenNombreArchivoZip(i.next(), true);
            if (contador.get(fileName) == null) {
                contador.put(fileName, 0);
            } else {
                contador.put(fileName, contador.get(fileName) + 1);
            }
        }
        for (Iterator<String> j = contador.keySet().iterator(); j.hasNext(); ) {
            String fileName = j.next();
            if (contador.get(fileName) >= 1)
                repetidos.add("El archivo: " + fileName + " se encuentra en " + (contador.get(fileName).intValue() + 1) + " ocacion(es) en el archivo.");
        }
        long stop = System.currentTimeMillis();
        log.trace("Terminado validacion de facturas repetidas en archivo en [" + ((stop - start) / 1000) + "s. ] ");
        return repetidos;
    }

    public static List<String> validaFormaPago(Connection conn, Map<String, ? extends ComponentesFactura> facturas, boolean esNotaCredito) {
        List<String> errores = new ArrayList<>();
        List<String> formaPagoValida = null;
        try {
            formaPagoValida = FacturaManager.getFormaPagoValido(conn, esNotaCredito);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("Error validando forma de pago: " + e.toString(), e);
        }
        for (Iterator<String> i = facturas.keySet().iterator(); i.hasNext(); ) {
            String archivo = i.next();
            ComponentesFactura cf = facturas.get(archivo);
            try {
                log.trace("Validando la forma de pago en [" + archivo + "]");
                String formaDePago = cf.getComprobante().getFormaDePago();
                if (!formaPagoValida.contains(formaDePago)) {
                    String mensajeError = "La factura [" + archivo + "] NO contiene forma de pago válida. Contiene: " + formaDePago;
                    errores.add(mensajeError);
                    cf.getErrorLog().add(mensajeError);
                } else if (!esNotaCredito && (POR_DEFINIR.equals(formaDePago) && !PPD.equalsIgnoreCase(cf.getComprobante().getMetodoPago()))) {
                    String mensajeError = "La factura [" + archivo + "] contiene forma de pago Por definir y no es PPD";
                    errores.add(mensajeError);
                    cf.getErrorLog().add(mensajeError);
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                String mensajeError = "Ocurrió un error mientras se validaba la forma de pago en la factura [" + archivo + "]: " + e;
                errores.add(mensajeError);
                cf.getErrorLog().add(mensajeError);
            }
        }
        return errores;
    }

    public static List<String> validaFormaPagoRG(Connection conn, Map<String, ? extends ComponentesFactura> facturas, boolean esNotaCredito) {
        List<String> errores = new ArrayList<>();
        List<String> formaPagoValida = null;
        try {
            formaPagoValida = FacturaManager.getFormaPagoValidoRG(conn);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("Error validando forma de pago: " + e.toString(), e);
        }
        for (Iterator<String> i = facturas.keySet().iterator(); i.hasNext(); ) {
            String archivo = i.next();
            ComponentesFactura cf = facturas.get(archivo);
            try {
                log.trace("Validando la forma de pago en [" + archivo + "]");
                String formaDePago = cf.getComprobante().getFormaDePago();
                if (!formaPagoValida.contains(formaDePago)) {
                    String mensajeError = "La factura [" + archivo + "] NO contiene forma de pago válida. Contiene: " + formaDePago;
                    errores.add(mensajeError);
                    cf.getErrorLog().add(mensajeError);
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                String mensajeError = "Ocurrió un error mientras se validaba la forma de pago en la factura [" + archivo + "]: " + e;
                errores.add(mensajeError);
                cf.getErrorLog().add(mensajeError);
            }
        }
        return errores;
    }

    public static List<String> validaMontosContratoFactura(Map<String, ? extends ComponentesFactura> facturas, BigDecimal montoTotalContrato, BigDecimal montoIVAContrato) throws Exception {
        List<String> errores = new ArrayList<String>();
        BigDecimal montoTotalFactura = new BigDecimal(0.00d).setScale(2, RoundingMode.HALF_UP);
        BigDecimal montoIVAFactura = new BigDecimal(0.00d).setScale(2, RoundingMode.HALF_UP);
        for (Iterator<String> i = facturas.keySet().iterator(); i.hasNext(); ) {
            String factura = i.next();
            ComponentesFactura cf = facturas.get(factura);
            Comprobante comprobante = cf.getComprobante();
            montoTotalFactura = montoTotalFactura.add(comprobante.getTotal());
            montoIVAFactura = montoIVAFactura.add(new BigDecimal(comprobante.calculaImpuestosTrasladados()).setScale(2, RoundingMode.HALF_UP));
        }
        if (montoTotalContrato.compareTo(montoTotalFactura) <= 0) {
            errores.add("El monto total del contrato= " + montoTotalContrato + " no es igual al monto total en facturas= " + montoTotalFactura);
        }
        if (montoIVAFactura.compareTo(Util.ZERO) > 0 || montoIVAContrato.compareTo(Util.ZERO) > 0) {
            if (!(montoIVAFactura.compareTo(Util.ZERO) > 0 && montoIVAContrato.compareTo(Util.ZERO) > 0)) {
                errores.add("El monto IVA del contrato= " + montoIVAContrato + " no es igual al monto IVA en facturas= " + montoIVAFactura);
            }
        }
        return errores;
    }

    public static List<String> validaRegimenReceptor(Map<String, ? extends ComponentesFactura> facturas, String regimenFiscalCliente) {
        List<String> errores = new ArrayList<>();
        for (Iterator<String> i = facturas.keySet().iterator(); i.hasNext(); ) {
            String archivo = i.next();
            ComponentesFactura cf = facturas.get(archivo);
            try {
                log.trace("Validando el régimen fiscal del receptor en [" + archivo + "]");
                String regimenReceptor = StringUtils.trimToEmpty(cf.getComprobante().getRegimenReceptor());
                String regimenEsperado = StringUtils.trimToEmpty(regimenFiscalCliente);
                if (!StringUtils.isBlank(regimenReceptor) && !regimenReceptor.equalsIgnoreCase(regimenEsperado)) {
                    String mensajeError = "El régimen del receptor [" + regimenReceptor + "] NO coincide con el régimen esperado [" + regimenEsperado + "]";
                    errores.add(mensajeError);
                    cf.getErrorLog().add(mensajeError);
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                String mensajeError = "Ocurrió un error mientras se validaba el régimen fiscal del receptor en la factura [" + archivo + "]: " + e;
                errores.add(mensajeError);
                cf.getErrorLog().add(mensajeError);
            }
        }
        return errores;
    }

    /**
     * Se encarga de validar cada una de las facturas contra el SAT para saber
     * si estan vigentes. Para la validacion mediante el WS del SAT se debe
     * enviar el RFC del emisor, el RFC del receptor, el UUID de la factura y el
     * monto total. Si todos los datos son correctos, la unica respuesta del WS
     * es "VIGENTE" (hasta el momento). Si algun dato es erroneo mandara la
     * leyenda "No encontrado" y si esta cancelado el CFDI enviara la leyenda
     * "CANCELADO".
     *
     * Si esta activada la notificacion de intento de usar CFDI invalido, se
     * mostrara el mensaje al usuario.
     *
     * @param facturas
     *            Facturas a validar.
     * @return Lista con los errores de validacion, vacio si todas las facturas
     *         son validas ante el SAT
     */
    public static List<String> validaSAT(Map<String, ? extends ComponentesFactura> facturas, boolean notificaErrores) {
        List<String> errores = new ArrayList<>();
        FacturaSATValidacion fsv;
        try {
            fsv = new FacturaSATValidacion();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            errores.add("Está habilitada la validación contra SAT, sin embargo, no se pudo iniciar esta operación: " + e);
            return errores;
        }
        for (Iterator<String> i = facturas.keySet().iterator(); i.hasNext(); ) {
            String factura = i.next();
            ComponentesFactura cf = facturas.get(factura);
            try {
                log.trace("Factura [" + factura + "]");
                Comprobante comprobante = cf.getComprobante();
                Factura f;
                Acuse acuse;
                try {
                    f = FacturaUtils.cargaCFDI(comprobante);
                } catch (Exception e) {
                    log.error("Error instanciando factura [" + factura + "]: " + e, e);
                    String mensajeError = "Ocurrió un error mientras se instanciaba la factura [" + factura + "] para su validación en SAT: " + e;
                    errores.add(mensajeError);
                    cf.getErrorLog().add(mensajeError);
                    continue;
                }
                fsv.setRfcEmisor(f.getRfcEmisor().replace("&", "&amp;"));
                fsv.setRfcReceptor(f.getRfcReceptor());
                fsv.setTotalFactura(f.getTotal());
                fsv.setUuid(f.getUUID());
                try {
                    acuse = fsv.validaCFDI();
                    log.trace("Acuse SAT de la factura [" + factura + "] Estatus [" + (acuse != null ? acuse.getCodigoEstatus() : "") + "] Estado [" + acuse.getEstado() + "]");
                } catch (Exception e) {
                    log.error("No fue posible validar la factura [" + factura + "] ante el SAT debido al error: " + e, e);
                    String mensajeError = "No fue posible validar la factura [" + factura + "] ante el SAT debido al error: " + e;
                    errores.add(mensajeError);
                    cf.getErrorLog().add(mensajeError);
                    continue;
                }
                if (!FacturaManager.SAT_VIGENTE_STR.equalsIgnoreCase(acuse.getEstado())) {
                    String mensajeError = "La factura: " + f.getUUID() + " del emisor: " + f.getNombreEmisor() + " no es válida ante el SAT." + (notificaErrores ? " Se notificará a la Gerencia de Recursos Financieros de este hecho." : "") + " Respuesta SAT. Estatus [" + acuse.getCodigoEstatus() + "] Estado CFDI [" + acuse.getEstado() + "]. Favor de revisar con el proveedor la validación del certificado del emisor o actualización en los registros del SAT e intentarlo de nuevo.";
                    log.info(mensajeError);
                    errores.add(mensajeError);
                    cf.getErrorLog().add(mensajeError);
                } else if (FacturaManager.SAT_PROCESO_CANCELACION.equalsIgnoreCase(acuse.getEstatusCancelacion())) {
                    String mensajeError = "La factura: " + f.getUUID() + " del emisor: " + f.getNombreEmisor() + " está en proceso de cancelación en el SAT." + (notificaErrores ? " Se notificará a la Gerencia de Recursos Financieros de este hecho." : "") + " Respuesta SAT. Estatus [" + acuse.getCodigoEstatus() + "] Estado CFDI [" + acuse.getEstado() + "] Estatus Cancelación [" + acuse.getEstatusCancelacion() + "]. Favor de revisar con el proveedor la validación del certificado del emisor o actualización en los registros del SAT e intentarlo de nuevo.";
                    log.info(mensajeError);
                    errores.add(mensajeError);
                    cf.getErrorLog().add(mensajeError);
                }
            } catch (Exception e) {
                log.error("Error inesperado en la validación de la factura [" + factura + "]: " + e, e);
                String mensajeError = "Ocurrió un error inesperado mientras se validaba la factura [" + factura + "]: " + e;
                errores.add(mensajeError);
                cf.getErrorLog().add(mensajeError);
            }
        }
        return errores;
    }

    public static Map<String, MassPaymentInvoiceComponents> validateInvoiceFiles(File invoicesFile) throws ZipException, IOException {
        List<String> files = ZipManager.listContents(invoicesFile, true);
        List<String> errorsRepeatedInvoices = FacturaManager.validaFacturasRepetidasEnArchivo(files);
        if (errorsRepeatedInvoices.size() > 0)
            throw new RuntimeException("Se encontraron facturas repetidas en el archivo: \n" + String.join("\n", errorsRepeatedInvoices));
        Map<String, MassPaymentInvoiceComponents> invoices = organizeInvoicesMassiveSupplierPayment(files);
        invoices = FacturaManager.validatesCompleteInvoices(invoices);
        return invoices;
    }

    public static List<String> validaTipoFactura(Map<String, ? extends ComponentesFactura> facturas, boolean esNotaCredito) {
        List<String> errores = new ArrayList<>();
        for (Iterator<String> i = facturas.keySet().iterator(); i.hasNext(); ) {
            String archivo = i.next();
            ComponentesFactura cf = facturas.get(archivo);
            try {
                log.trace("Validando el tipo de factura en [" + archivo + "]");
                String tipoFactura = cf.getComprobante().getTipoComprobante();
                if (esNotaCredito && !CFDIInterface.EGRESO.equalsIgnoreCase(tipoFactura)) {
                    String mensajeError = "La nota de crédito [" + archivo + "] NO es de tipo Egreso. Se debe procesar como factura";
                    errores.add(mensajeError);
                    cf.getErrorLog().add(mensajeError);
                } else if (!esNotaCredito && !CFDIInterface.INGRESO.equalsIgnoreCase(tipoFactura)) {
                    String mensajeError = "La factura [" + archivo + "] NO es de tipo ingreso. Se debe procesar como nota de crédito";
                    errores.add(mensajeError);
                    cf.getErrorLog().add(mensajeError);
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                String mensajeError = "Ocurrió un error mientras se validaba el tipo de comprobante en la factura [" + archivo + "]: " + e;
                errores.add(mensajeError);
                cf.getErrorLog().add(mensajeError);
            }
        }
        return errores;
    }

    /**/
    public static List<String> validaTipoPagoRetencion(Connection conn, Map<String, ? extends ComponentesFactura> facturas, String tipoModulo) {
        List<String> errores = new ArrayList<>();
        try {
            int eFiscal = Integer.parseInt(EjercicioFiscalManager.getEjercicioFiscalActivo(conn).getaEjercicioFiscal());
            int year = LocalDate.now().getYear();
            int tolerance = getToleranceDays(conn);
            if (tolerance > 0) {
                for (Iterator<String> i = facturas.keySet().iterator(); i.hasNext(); ) {
                    String archivo = i.next();
                    ComponentesFactura cf = facturas.get(archivo);
                    try {
                        log.trace("Validando el tipo de pago y retenciones en [" + archivo + "]");
                        String formaPagoFactura = cf.getComprobante().getMetodoPago();
                        if ("PUE".equalsIgnoreCase(formaPagoFactura)) {
                            Comprobante comprobante = cf.getComprobante();
                            int monthCFDI = comprobante.getFechaExpedicion().getMonthValue();
                            int currentMonth = ((year == eFiscal) ? LocalDate.now().getMonthValue() : 12);
                            // VGC-03022026 Condicion modificada. Para cualquier
                            // tipo de pago, si es PUE se debe pagar en el mismo
                            // mes. Casos RG se validan en su modulo para
                            // determinar el evento y saber si se aceptan o no.
                            if (monthCFDI != currentMonth && !("RELACIONGASTOS".equalsIgnoreCase(tipoModulo) || "RGOC".equals(tipoModulo))) {
                                String mensajeError = "La factura [" + archivo + "] es tipo PUE y corresponde a un mes diferente al actual. Mes Factura [" + monthCFDI + "] Mes Actual [" + currentMonth + "]";
                                errores.add(mensajeError);
                                cf.getErrorLog().add(mensajeError);
                            }
                            BigDecimal totalRetenciones = comprobante.getTotalRetenciones();
                            if (totalRetenciones.compareTo(Util.ZERO) > 0) {
                                if (!"RELACIONGASTOS".equalsIgnoreCase(tipoModulo) || !"RGOC".equals(tipoModulo)) {
                                    if (timeToPay(conn) < tolerance) {
                                        String mensajeError = "La factura [" + archivo + "] es tipo PUE y contiene retenciones que deben pagarse en este mes, sin embargo, no se encuentra en tiempo.";
                                        errores.add(mensajeError);
                                        cf.getErrorLog().add(mensajeError);
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        log.error("Error validando el tipo de pago y retenciones en la factura [" + archivo + "]: " + e, e);
                        String mensajeError = "Ocurrió un error mientras se validaba el tipo de pago y retenciones en la factura [" + archivo + "]: " + e;
                        errores.add(mensajeError);
                        cf.getErrorLog().add(mensajeError);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error obteniendo la tolerancia de días para validación: " + e, e);
            throw new RuntimeException("Error obteniendo la tolerancia de días para validación: " + e.toString(), e);
        }
        return errores;
    }

    public static List<String> validaUsoFactura(Connection conn, Map<String, ? extends ComponentesFactura> facturas, boolean esNotaCredito) {
        List<String> errores = new ArrayList<String>();
        List<String> usosValidos = null;
        try {
            usosValidos = FacturaManager.getUsoCfdiValido(conn, esNotaCredito);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("Error validando uso de factura: " + e.toString(), e);
        }
        for (Iterator<String> i = facturas.keySet().iterator(); i.hasNext(); ) {
            String archivo = i.next();
            ComponentesFactura cf = facturas.get(archivo);
            try {
                log.trace("Validando el tipo de factura en  [" + archivo + "]");
                String usoCFDI = cf.getComprobante().getUsoCFDI();
                if (!usosValidos.contains(usoCFDI)) {
                    errores.add("La factura [" + archivo + "] NO contiene el uso de CFDI Valido.  Contiene: " + usoCFDI);
                    cf.getErrorLog().add("La factura [" + archivo + "] NO contiene el uso de CFDI Valido.  Contiene: " + usoCFDI);
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                errores.add("Ocurrio el siguiente error mientras se validaba el tipo de comprobante en las facturas: " + e);
                cf.getErrorLog().add("Ocurrio el siguiente error mientras se validaba el tipo de comprobante en las facturas: " + e);
            }
        }
        return errores;
    }

    public static List<String> validaVersionFactura(Connection conn, String tipoPago, Map<String, ? extends ComponentesFactura> facturas) {
        List<String> errores = new ArrayList<>();
        for (Iterator<String> i = facturas.keySet().iterator(); i.hasNext(); ) {
            String archivo = i.next();
            ComponentesFactura cf = facturas.get(archivo);
            try {
                log.trace("Validando la versión en [" + archivo + "]");
                if (!(cf.getComprobante().isCfd33() || cf.getComprobante().isCfd40())) {
                    String mensajeError = "La versión de la factura no es la esperada: 3.3 o 4.0. Se requiere solicitar cambio de factura.";
                    errores.add(mensajeError);
                    cf.getErrorLog().add(mensajeError);
                }
            } catch (Exception e) {
                log.error("Error validando la versión de la factura [" + archivo + "]: " + e, e);
                String mensajeError = "Ocurrió un error mientras se validaba la versión de la factura [" + archivo + "]: " + e;
                errores.add(mensajeError);
                cf.getErrorLog().add(mensajeError);
            }
        }
        return errores;
    }

    /**
     * Valida que para los casos que no son: "Comprobacion de Caja Chica, Gastos
     * Devengados, Comprobacion de Viaticos y Gastos" entonces: <br>
     * La factura es PUE debe ser pagada en el mismo mes.
     *
     * Devuelve entonces las facturas que son de un mes diferente a la fecha de
     * aplicacion de aquellos pagos que no estan en la excepcion.
     *
     * @param conn
     * @param nFolioRelacionGastos
     * @return Concatenacion de RFC + ";" + UUID + ";" + MES PAGO + ";" + MES
     *         FACTURA
     * @throws SQLException
     */
    public static List<String> validacionesPreAutorizaRelacionGastos(Connection conn, int nFolioRelacionGastos) throws SQLException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT rg.nfoliorelaciongastos, ");
        query.append("       rg.id_destino_gasto, ");
        query.append("       rg.faplicacion, ");
        query.append("       Month(rg.faplicacion) AS mesPago, ");
        query.append("       cfdi.cfactura, ");
        query.append("       cfdi.crfcfactura, ");
        query.append("       cfdi.cmetodopago, ");
        query.append("       cfdi.dfechafactura, ");
        query.append("       Month(dfechafactura)  AS mesFactura ");
        query.append("FROM   trelaciongastosencabezado rg WITH(nolock) ");
        query.append("       INNER JOIN tpagofactura cfdi WITH(nolock) ");
        query.append("               ON cfdi.ctipopago = 'RELACIONGASTOS' ");
        query.append("                  AND cfdi.nfoliopago = rg.nfoliorelaciongastos ");
        query.append("WHERE  rg.id_destino_gasto NOT IN ( 'CCRE', 'CERE', 'GCRE', 'RCRE' ) ");
        query.append("       AND rg.nfoliorelaciongastos = ? ");
        query.append("       AND cfdi.cmetodopago = 'PUE' ");
        query.append("       AND Month(rg.faplicacion) <> Month(dfechafactura)  ");
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<String> result = new ArrayList<String>();
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, nFolioRelacionGastos);
            rs = ps.executeQuery();
            while (rs.next()) {
                result.add(rs.getString("crfcfactura") + ";" + rs.getString("cfactura") + ";" + rs.getString("mesPago") + ";" + rs.getString("mesFactura"));
            }
            return result;
        } finally {
            CloseObject.closeObject(rs, ps);
        }
    }
}
