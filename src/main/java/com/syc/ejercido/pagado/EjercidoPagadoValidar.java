package com.syc.ejercido.pagado;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import com.axtel.egresos.core.CargaMasivaRG;
import com.axtel.egresos.core.MasiveOperation;
import com.axtel.egresos.core.RGMasiva;
import com.syc.contable.AccountingEngine;
import com.syc.contable.AdecuacionBusinessLogic;
import com.syc.contable.RelacionGastosBussinessLogic;
import com.syc.contable.core.RelacionGastosManager;
import com.syc.contable.core.SolicitudPagoFirmaElectronica;
import com.syc.dsmngr.DataSourceManager;
import com.syc.egresos.firmante.FirmanteBussinessLogic;
import com.syc.egresos.firmante.servlet.Firmante;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.CasoOperacionBusinessLogic;
import com.syc.gestion.core.CFSequenceManager;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoOperacion;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.custom.FolioGeneratorInterface;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.obrapublica.ConfiguraAplicativoBusinessLogic;
import com.syc.obrapublica.EjercicioFiscalManager;
import com.syc.obrapublica.core.ConfiguraAplicativoManager;
import com.syc.sai.contabilidad.polizamanual.model.GeneradorPolizaManualBusinessLogic;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import com.syc.sai.firmaElectronica.FirmaElectronicaBusinessLogic;
import com.syc.sai.firmaElectronica.core.FirmaElectronicaManager;
import com.syc.sai.firmaElectronica.interfaces.SolicitudFirmaElectronica;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class EjercidoPagadoValidar extends DataSourceManager {

    private static Logger log = LoggerFactory.getLogger(EjercidoPagadoValidar.class);

    /* FAV20171019 Se guarda en base el prefijo de CxP */
    private final ConfiguraAplicativoBusinessLogic cabl = new ConfiguraAplicativoBusinessLogic(GestionInterface.ATT_CONEXION);

    private final String cxpPrefijo = cabl.getSystemSetting("CXP_PREFIJO") == null ? "CP" : cabl.getSystemSetting("CXP_PREFIJO");

    private String reportPath = null;

    private Usuario usuario = null;

    private int actualizaTipoCargaAlim(Connection conn, RGMasiva rg) throws Exception {
        PreparedStatement psUpTipoCarga = null;
        StringBuilder query = new StringBuilder();
        query.append("UPDATE tRELACIONGASTOSEncabezado ");
        query.append("   SET nEsAlimentacionBrigadistas = 1 ");
        query.append(" WHERE nFolioRELACIONGASTOS = ?  ");
        try {
            psUpTipoCarga = conn.prepareStatement(query.toString());
            psUpTipoCarga.setInt(1, rg.getFolioTramite());
            return psUpTipoCarga.executeUpdate();
        } finally {
            CloseObject.closeObject(psUpTipoCarga);
        }
    }

    private void actualizarTipoJuegosDeportivos(Connection conn, RGMasiva rg) throws Exception {
        PreparedStatement pst = null;
        try {
            pst = conn.prepareStatement("UPDATE tRELACIONGASTOSEncabezado SET nEsAlimentacionBrigadistas = 0, nEsBoxLunch = 1 where nfoliorelaciongastos = ? ");
            pst.setInt(1, rg.getFolioTramite());
            pst.executeUpdate();
        } finally {
            CloseObject.closeObject(pst);
        }
    }

    private int actualizaTipoCargaCert(Connection conn, RGMasiva rg) throws Exception {
        PreparedStatement psUpTipoCarga = null;
        StringBuilder query = new StringBuilder();
        query.append("UPDATE tRELACIONGASTOSEncabezado ");
        query.append("   SET nEsCertificadoTransito = 1 ");
        query.append(" WHERE nFolioRELACIONGASTOS = ?  ");
        try {
            psUpTipoCarga = conn.prepareStatement(query.toString());
            psUpTipoCarga.setInt(1, rg.getFolioTramite());
            return psUpTipoCarga.executeUpdate();
        } finally {
            CloseObject.closeObject(psUpTipoCarga);
        }
    }

    public String aplicacionISRLaudos(String strFolios, String strfPago, String usuario, String ctab) throws SQLException {
        String valor = "Insertado";
        String query = "";
        PreparedStatement pstmtnSelect = null, pstmntInsertEj = null, pstmnSelectDet = null, pstmntInsertDet = null, pstmntInsertPag = null, pstmntInsertDetPag = null, pstmUpEjercido = null, pstmSeqEjercido = null, pstmUpPagado = null;
        ResultSet rs = null, rs2 = null, rs3 = null, rs4 = null;
        Connection conn = null;
        int nFolioPago = 0;
        String strFolioEjercido = "0";
        String strFolioPagado = "0";
        try {
            conn = getConnection();
            pstmtnSelect = conn.prepareStatement("SELECT * FROM tOperAjenasEncabezado WITH (NOLOCK) WHERE nFolioOperAjenas IN ( ? )");
            pstmtnSelect.setString(1, strFolios);
            rs = pstmtnSelect.executeQuery();
            if (rs.next()) {
                // OBTENER SEQUENCE DE EJERCIDO
                pstmUpEjercido = conn.prepareStatement("UPDATE CF_SEQUENCE  WITH (ROWLOCK) SET seq_value = seq_value + 1  WHERE seq_name = 'EJERCIDO' ");
                pstmUpEjercido.executeUpdate();
                pstmSeqEjercido = conn.prepareStatement("SELECT seq_value FROM CF_SEQUENCE WITH(NOLOCK) WHERE seq_name = 'EJERCIDO' ");
                rs3 = pstmSeqEjercido.executeQuery();
                if (rs3.next()) {
                    strFolioEjercido = rs3.getString("seq_value");
                }
                // OBTENER SEQUENCE DE PAGADO
                pstmUpPagado = conn.prepareStatement("UPDATE CF_SEQUENCE  WITH (ROWLOCK) SET seq_value = seq_value + 1 WHERE seq_name = 'PAGADO' ");
                pstmUpPagado.executeUpdate();
                pstmSeqEjercido = conn.prepareStatement("SELECT seq_value FROM CF_SEQUENCE WITH(NOLOCK) WHERE seq_name = 'PAGADO' ");
                rs4 = pstmSeqEjercido.executeQuery();
                if (rs4.next()) {
                    strFolioPagado = rs4.getString("seq_value");
                }
                String recibo = rs.getString("caNoContrarrecibo");
                String desPoliza = rs.getString("cDescripcionPoliza");
                String uniResp = rs.getString("cUnidadResponsableContable");
                String cRamo = rs.getString("cRamo");
                String ur = rs.getString("cUnidadResponsable");
                nFolioPago = Integer.parseInt(rs.getString("nFolioOperAjenas"), 10);
                int aEjercicioFiscal = Integer.parseInt(rs.getString("aEjercicioFiscal"), 10);
                // Guarda En Cabecera Ejercido
                query = "INSERT INTO tEjercidoEncabezado (cTipoPago,nFolioPAGO,caNoContrarrecibo,nFolioPoliza,cTipoPoliza,U_LOGIN,nFolioPolizaCancelacion,cDescripcionPoliza,cUnidadResponsableContable,fAplicacion,cRamo,cIdUsuarioCaptura,FechaAplicacionSicop,FechaPagoSicop,nFolioEjercido,SolicitudPago,nFolioSICOP,NumeroProceso,nFolioSIAFF) " + "VALUES ('AJENAS'," + nFolioPago + ",'" + recibo + "',NULL,'DI','" + usuario + "',NULL,'" + desPoliza + "','" + uniResp + "','" + strfPago + "','" + cRamo + "','" + usuario + "','" + strfPago + "','" + strfPago + "'," + strFolioEjercido + ",-1,-1,-1,-1 )";
                System.out.println(query);
                pstmntInsertEj = conn.prepareStatement(query);
                int intr = pstmntInsertEj.executeUpdate();
                // Guarda En Cabecera Pagado
                query = "INSERT INTO tPagadoEncabezado (cTipoPago,nFolioPAGO,caNoContrarrecibo,nFolioPoliza,cTipoPoliza,U_LOGIN,nFolioPolizaCancelacion,cDescripcionPoliza,cUnidadResponsableContable,fAplicacion,cRamo,cIdUsuarioCaptura,FechaAplicacionSicop,FechaPagoSicop,SolicitudPago,NumeroProceso,nFolioSIAFF,nFolioSICOP,FechaPagado,nFolioPagado) " + "VALUES ('AJENAS'," + nFolioPago + ",'" + recibo + "',NULL,'EG','" + usuario + "',NULL,'" + desPoliza + "','" + uniResp + "','" + strfPago + "','" + cRamo + "','" + usuario + "','" + strfPago + "','" + strfPago + "',-1,-1,-1,-1,'" + strfPago + "'," + strFolioPagado + " )";
                System.out.println(query);
                pstmntInsertPag = conn.prepareStatement(query);
                int intrPag = pstmntInsertPag.executeUpdate();
                if (intr > 0 && intrPag > 0) {
                    // Buscar detalles con el nFolio de Encabezado
                    pstmnSelectDet = conn.prepareStatement("SELECT * FROM tOperAjenasDetalle WITH (NOLOCK) WHERE nFolioOperAjenas = ? ");
                    pstmnSelectDet.setInt(1, nFolioPago);
                    rs2 = pstmnSelectDet.executeQuery();
                    while (rs2.next()) {
                        int nDocRenglon = Integer.parseInt(rs2.getString("nDocRenglon"), 10);
                        int cMes = Integer.parseInt(rs2.getString("cMes"), 10);
                        String cEjercicio = rs2.getString("aEjercicioFiscal");
                        String EP = rs2.getString("EP");
                        String cOBGT = EP.substring(31, 36);
                        String cIdCuentaContable = " ";
                        String cIdEntidadContable = rs2.getString("cCentroContable");
                        String cIdRelacion = " ";
                        String idTipoMovimiento = " ";
                        String idTipoConcepto = " ";
                        String cEvento = "P_AJENA_LAUDOS";
                        String cCentroContable = rs2.getString("cCentroContable");
                        String Rfc = rs2.getString("RFC");
                        String alm = " ";
                        String nCapitulo = EP.substring(31, 32) + "000";
                        ;
                        String altaAlmacen = " ";
                        // Guarda Detalle Ejercido
                        query = "INSERT INTO tEjercidoDetalle (cTipoPago,nFolioPAGO,nDocRenglon,cMes,cEjercicio,cIdEntidadContable,cIdRelacion,EP,cIdCuentaContable,mComprometido,nPoliza,ID_TIPO_MOVIMIENTO,ID_TIPO_CONCEPTO,cEvento,nMes,cCentroContable,RFC, mImporteNeto,ALM,mImporteBruto,mImporteMasIva,mImporteIva,nCapitulo,mSancion,mDevolucion,mImporteAmortiza,mRetencion,mPenalizacion,m2Millar,m23IVA,mISRHonorarios,mObra5,mImporteFlete4,mISRArrenda,mRetImpuestoCedular,mImporteIvaArrenda,mImporteIvaHonorarios ,mImporteFlete23,mImporteIvaProv ,mImporteObra,mCNIC,mTesofe,altaAlmacen,aEjercicioFiscal,mIMDT,mImporte,nFolioEjercido,mImporteISRLaudos, cUnidadResponsable)" + " VALUES('AJENAS'," + nFolioPago + "," + nDocRenglon + "," + cMes + ",'" + cEjercicio + "','" + cIdEntidadContable + "','" + cIdRelacion + "','" + EP + "','" + cIdCuentaContable + "',0.00,0,'" + idTipoMovimiento + "','" + idTipoConcepto + "','EJERCIDO','" + cMes + "','" + cCentroContable + "','" + Rfc + "'," + rs2.getString("mTotal") + ",'" + alm + "'," + rs2.getString("mTotal") + "," + rs2.getString("mTotal") + ",0.00,'" + nCapitulo + "',0.00,0.00,0.00," + rs2.getString("mTotal") + ",0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,'" + altaAlmacen + "','" + aEjercicioFiscal + "',0.00," + rs2.getString("mTotal") + "," + strFolioEjercido + "," + rs2.getString("mImporteISRLaudos") + ",'" + ur + "' )";
                        log.trace("Object: {}", query.toString());
                        pstmntInsertDet = conn.prepareStatement(query);
                        int intDet = pstmntInsertDet.executeUpdate();
                        log.trace("Object: {}", "Se insertaron " + intDet + " registros");
                        // Guarda Detalle Pagado
                        query = "INSERT INTO tPagadoDetalle (cTipoPago,nFolioPAGO,nDocRenglon,cMes,cEjercicio,cIdEntidadContable,cIdRelacion,EP,cIdCuentaContable,mComprometido,nPoliza,ID_TIPO_MOVIMIENTO,ID_TIPO_CONCEPTO,cEvento,nMes,cCentroContable,RFC, mImporteNeto,ALM,mImporteBruto,mImporteMasIva,mImporteIva,nCapitulo,mSancion,mDevolucion,mImporteAmortiza,mRetencion,mPenalizacion,m2Millar,m23IVA,mISRHonorarios,mObra5,mImporteFlete4,mISRArrenda,mRetImpuestoCedular,mImporteIvaArrenda,mImporteIvaHonorarios ,mImporteFlete23,mImporteIvaProv ,mImporteObra,mCNIC,mTesofe,altaAlmacen,aEjercicioFiscal,mIMDT,mImporte,nFolioPagado,OBGT,ctab,mImporteISRLaudos, cUnidadResponsable)" + " VALUES('AJENAS'," + nFolioPago + "," + nDocRenglon + "," + cMes + ",'" + cEjercicio + "','" + cIdEntidadContable + "','" + cIdRelacion + "','" + EP + "','" + cIdCuentaContable + "',0.00,0,'" + idTipoMovimiento + "','" + idTipoConcepto + "','" + cEvento + "','" + cMes + "','" + cCentroContable + "','" + Rfc + "'," + rs2.getString("mTotal") + ",'" + alm + "'," + rs2.getString("mTotal") + "," + rs2.getString("mTotal") + ",0.00,'" + nCapitulo + "',0.00,0.00,0.00," + rs2.getString("mTotal") + ",0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,'" + altaAlmacen + "','" + aEjercicioFiscal + "',0.00," + rs2.getString("mTotal") + "," + strFolioPagado + ",'" + cOBGT + "','" + ctab + "'," + rs2.getString("mImporteISRLaudos") + ",'" + ur + "')";
                        log.trace("Object: {}", query.toString());
                        pstmntInsertDetPag = conn.prepareStatement(query);
                        int intDetPag = pstmntInsertDetPag.executeUpdate();
                        log.trace("Object: {}", "Se insertaron: " + intDetPag + " Registros");
                        conn.commit();
                    }
                } else {
                    System.out.println("No Guarda Encabezado");
                }
            }
            try {
                AccountingEngine accEng = new AccountingEngine();
                accEng.setValidaInsuficienciaDeSaldo(true);
                // Datos Para Aplicar Ejercido
                String cTablaPadre = "tEjercidoEncabezado";
                String cTablaHija = "tEjercidoDetalle";
                String cFolio = "nFolioEjercido";
                String cTipoDocumento = "EJERCIDO";
                // Datos Para Aplicar Pagado
                String cTablaPadrePag = "tPagadoEncabezado";
                String cTablaHijaPag = "tPagadoDetalle";
                String cFolioPag = "nFolioPagado";
                String cTipoDocumentoPag = "PAGADO";
                accEng.makeAccountingApplication(conn, cTipoDocumento, strFolioEjercido, cTablaPadre, cTablaHija, cFolio);
                accEng.makeAccountingApplication(conn, cTipoDocumentoPag, strFolioPagado, cTablaPadrePag, cTablaHijaPag, cFolioPag);
                conn.commit();
                valor = "Insertado";
            } catch (Exception ex) {
                log.warn(ex.getMessage(), ex);
                log.debug("Object: " + String.valueOf(ex.getLocalizedMessage()));
                try {
                    conn.rollback();
                } catch (Exception e) {
                    log.warn("Error en rollback", e);
                }
                valor = "no_ingresado" + ex.toString();
            }
        } catch (Exception e) {
            log.debug("Error occurred", "Error: " + e.toString());
            try {
                conn.rollback();
            } catch (Exception ee) {
                log.warn("Error: cerrando rollback ", ee);
            }
            valor = "no_ingresado";
        } finally {
            try {
                CloseObject.closeObject(rs);
                CloseObject.closeObject(rs2);
                CloseObject.closeObject(rs3);
                CloseObject.closeObject(pstmtnSelect);
                CloseObject.closeObject(pstmntInsertEj);
                CloseObject.closeObject(pstmUpEjercido);
                CloseObject.closeObject(pstmSeqEjercido);
            } catch (Exception e) {
                log.warn("Error: cerrando statement", e);
            }
            try {
                CloseObject.closeObject(conn);
            } catch (Exception e) {
                log.warn("Error: cerrando conexion", e);
            }
            pstmtnSelect = null;
            pstmntInsertEj = null;
            pstmnSelectDet = null;
            pstmntInsertDet = null;
            pstmntInsertPag = null;
            pstmntInsertDetPag = null;
            rs = null;
            rs2 = null;
            conn = null;
        }
        return valor;
    }

    public String aplicacionPagosDiversosRG(String strFolios, String strfPagado, String usuario, String ctab, boolean generaPoliza, String tipoPago) throws SQLException {
        PreparedStatement pstmtnSelect = null, pstmntInsertEj = null, pstmnSelectDet = null, pstmntInsertDet = null, pstmntInsertPag = null, pstmntInsertDetPag = null, pstmUpEjercido = null, pstmSeqEjercido = null, pstmUpPagado = null, pstmSicop = null, pstmUpIngPag = null, pstmtnUpEnviadoSICOP = null;
        ResultSet rs = null, rs2 = null, rs3 = null;
        Connection conn = null;
        int nFolioPago = 0;
        int folioPoliza = 0;
        String valor = "";
        String strFolioEjercido = "0";
        String strFolioPagado = "0";
        String strCLC = null;
        String fechaAplicadoSicop = null;
        String fechaPagoSicop = null;
        String solicitudPago = null;
        String numProceso = null;
        String folioSiaff = null;
        String esIngreosPropios = "";
        String esRadicado = "";
        boolean aplicarPagado = false;
        String strTabla = "";
        String strTablaDet = "";
        String strCampos = "";
        String strCamposDet = "";
        // String nomCamposDetValidar = "";
        String nomCXP = "";
        String strNomDetFolio = "";
        String strLetratipoPago = "";
        String documento = "";
        String fn_integracion = "";
        try {
            conn = getConnection();
            boolean esSAIAlterno = "true".equals(ConfiguraAplicativoManager.getSystemSetting(conn, "SAI_AMBIENTAL"));
            // TODO: Definimos los nombres de las tablas y los campos para cada
            // tipo de Pago Diverso RG con OC, Pago Relacion Gastos y Pago Obra
            if ("1".equals(tipoPago)) {
                documento = "PAGODIVERSO";
                strTabla = "t" + documento + "Encabezado";
                strTablaDet = "t" + documento + "Detalle";
                /*
				 * VGC21051222 Se agrega la UE que se requiere para la Pol Man
				 * VGC20170814 Se agraga columna para FONDEN en SAI Ambiental
				 */
                strCampos = "nFolio" + documento + " AS nFolio,mImporteNeto as impNeto,caNoContrarrecibo,nFolioPoliza,cTipoPoliza,U_LOGIN,isnull(nFolioPolizaCancelacion,'0') AS nFolioPolizaCancelacion,isnull(fCancelacion,NULL) AS fCancelacion,cDescripcionPoliza,cUnidadResponsableContable,fAplicacion,cRamo,aEjercicioFiscal,CTAB, cUnidadResponsable" + (esSAIAlterno ? ", cProyectoFonden" : "");
                strCamposDet = "nDocRenglon,cMes,cEjercicio, EP,cIdCuentaContable,mComprometido,isnull(nPoliza,0) AS nPoliza,ID_TIPO_MOVIMIENTO,ID_TIPO_CONCEPTO,cEvento,cCentroContable,RFC,mImporteNeto,ALM,mImporteBruto,mImporteMasIva,mImporteIva,nCapitulo,mSancion,mDevolucion,mImporteAmortiza,mRetencion,mPenalizacion,m2Millar,m23IVA,mISRHonorarios,mObra5,mImporteFlete4,mISRArrenda,mRetImpuestoCedular,mImporte,mImporteIvaArrenda,mImporteIvaHonorarios,mImporteFlete23,mImporteIvaProv,mImporteObra,isnull(mCNIC,0.00) AS mCNIC,isnull(mIMDT,0.00) AS mIMDT,isnull(mTesofe,0.00) AS mTesofe,altaAlmacen,cCentroContable AS cIdEntidadContable, cIdRelacion, cUnidadResponsable";
                nomCXP = "caNoContrarrecibo";
                strNomDetFolio = "nFolio" + documento;
                strLetratipoPago = documento;
                fn_integracion = "fn_IntegracionPDIV";
            } else if ("2".equals(tipoPago)) {
                documento = "PAGOOBRA";
                strTabla = "t" + documento + "Encabezado";
                strTablaDet = "t" + documento + "Detalle";
                strCampos = "nFolio" + documento + " AS nFolio,mImporteNeto as impNeto,caNoContrarrecibo,nFolioPoliza,cTipoPoliza,U_LOGIN,isnull(nFolioPolizaCancelacion,'0') AS nFolioPolizaCancelacion,isnull(fCancelacion,NULL) AS fCancelacion,cDescripcionPoliza,cUnidadResponsableContable,fAplicacion,cRamo,aEjercicioFiscal,CTAB, cUnidadResponsable";
                strCamposDet = "nDocRenglon,cMes,cEjercicio, EP,cIdCuentaContable,mComprometido,isnull(nPoliza,0) AS nPoliza,ID_TIPO_MOVIMIENTO,ID_TIPO_CONCEPTO,cEvento,cCentroContable,RFC,mImporteNeto,ALM,mImporteBruto,mImporteMasIva,mImporteIva,nCapitulo,mSancion,mDevolucion,mImporteAmortiza,mRetencion,mPenalizacion,m2Millar,m23IVA,mISRHonorarios,mObra5,mImporteFlete4,mISRArrenda,mRetImpuestoCedular,mImporte,mImporteIvaArrenda,mImporteIvaHonorarios,mImporteFlete23,mImporteIvaProv,mImporteObra,isnull(mCNIC,0.00) AS mCNIC,isnull(mIMDT,0.00) AS mIMDT,isnull(mTesofe,0.00) AS mTesofe,altaAlmacen,cCentroContable AS cIdEntidadContable, cIdRelacion, cUnidadResponsable";
                nomCXP = "caNoContrarrecibo";
                strNomDetFolio = "nFolio" + documento;
                strLetratipoPago = documento;
            } else if ("3".equals(tipoPago)) {
                documento = "RELACIONGASTOS";
                strTabla = "t" + documento + "Encabezado";
                strTablaDet = "t" + documento + "Detalle";
                strCampos = "nFolio" + documento + " AS nFolio,mImporteNeto as impNeto,caNoContrarrecibo,nFolioPoliza,cTipoPoliza,U_LOGIN,isnull(nFolioPolizaCancelacion,'0') AS nFolioPolizaCancelacion,isnull(fCancelacion,NULL) AS fCancelacion,cDescripcionPoliza,cUnidadResponsableContable,fAplicacion,cRamo,aEjercicioFiscal,CTAB, cUnidadResponsable";
                strCamposDet = "nDocRenglon,cMes,cEjercicio, EP,cIdCuentaContable,mComprometido,isnull(nPoliza,0) AS nPoliza,ID_TIPO_MOVIMIENTO,ID_TIPO_CONCEPTO,cEvento,cCentroContable,RFC,mImporteNeto,ALM,mImporteBruto,mImporteMasIva,mImporteIva,nCapitulo,mSancion,mDevolucion,mImporteAmortiza,mRetencion,mPenalizacion,m2Millar,m23IVA,mISRHonorarios,mObra5,mImporteFlete4,mISRArrenda,mRetImpuestoCedular,mImporte,mImporteIvaArrenda,mImporteIvaHonorarios,mImporteFlete23,mImporteIvaProv,mImporteObra,isnull(mCNIC,0.00) AS mCNIC,isnull(mIMDT,0.00) AS mIMDT,isnull(mTesofe,0.00) AS mTesofe,altaAlmacen,cCentroContable AS cIdEntidadContable, cIdRelacion, cUnidadResponsable";
                nomCXP = "caNoContrarrecibo";
                strNomDetFolio = "nFolio" + documento;
                strLetratipoPago = documento;
            } else if ("4".equals(tipoPago)) {
                documento = "PAGOPENASCONV";
                strTabla = "t" + documento + "Encabezado";
                strTablaDet = "t" + documento + "Detalle";
                strCampos = "nFolio" + documento + " AS nFolio, caNoContrarrecibo, nFolioPoliza,cTipoPoliza, U_LOGIN, isnull(nFolioPolizaCancelacion,'0') AS nFolioPolizaCancelacion, isnull(fCancelacion,NULL) AS fCancelacion, cDescripcionPoliza, cUnidadResponsableContable,fAplicacion,cRamo,aEjercicioFiscal, cUnidadResponsable";
                strCamposDet = " nDocRenglon,cMes,cEjercicio, EP, '00' cIdCuentaContable, 0.00 mComprometido, 0 AS nPoliza,'' ID_TIPO_MOVIMIENTO, '' ID_TIPO_CONCEPTO, 'P_PENAS_IP' AS cEvento, cCentroContable, RFC, mImporteMasIva AS mImporteNeto,'' ALM, 0.00 mImporteBruto, mImporteMasIva, 0.00 mImporteIva,'' nCapitulo, 0.00 mSancion, 0.00 mDevolucion, 0.00 mImporteAmortiza, 0.00 mRetencion, mImporteMasIva AS mPenalizacion, 0.00 m2Millar, 0.00 m23IVA, 0.00 mISRHonorarios, 0.00 mObra5, 0.00 mImporteFlete4, 0.00 mISRArrenda, 0.00 mRetImpuestoCedular, 0.00 mImporte, 0.00 mImporteIvaArrenda, 0.00 mImporteIvaHonorarios, 0.00 mImporteFlete23, 0.00 mImporteIvaProv, 0.00 mImporteObra, 0.00 mCNIC, 0.00 mIMDT, 0.00 mTesofe, '' altaAlmacen, cCentroContable AS cIdEntidadContable, '' cIdRelacion, 'F01' cUnidadResponsable";
                nomCXP = "caNoContrarrecibo";
                strNomDetFolio = "nFolio" + documento;
                strLetratipoPago = documento;
            }
            // boolean appCont = true, appContPag = true;
            AccountingEngine accEng = new AccountingEngine();
            accEng.setValidaInsuficienciaDeSaldo(true);
            java.util.Date utilDate = new java.util.Date();
            long lnMilisegundos = utilDate.getTime();
            java.sql.Date sqlDate = new java.sql.Date(lnMilisegundos);
            String[] fp = String.valueOf(sqlDate).split("-");
            String FechaPagado = fp[0] + "-" + fp[1] + "-" + fp[2];
            int ejercicioFiscal = EjercicioFiscalManager.getEjercicioFiscal(conn).getEjercicio();
            int anioAplicacion = Util.getYearFromDate(strfPagado);
            String fechaAplicacion = (ejercicioFiscal == anioAplicacion ? strfPagado : "31/12/" + ejercicioFiscal);
            String cTipoPoliza = "EG";
            String FechaPagadoApl = strfPagado;
            // Buscar Informacion Cabecera
            if ("3".equals(tipoPago)) {
                pstmtnSelect = conn.prepareStatement("SELECT " + strCampos + ", cRadicado FROM " + strTabla + " WITH(NOLOCK) WHERE nFolio" + documento + " IN ( " + strFolios + " )");
            } else if ("4".equals(tipoPago)) {
                pstmtnSelect = conn.prepareStatement("SELECT " + strCampos + ", 'N' AS cRadicado FROM " + strTabla + " WITH(NOLOCK) WHERE nFolio" + documento + " IN ( " + strFolios + " )");
            } else {
                pstmtnSelect = conn.prepareStatement("SELECT " + strCampos + ",cIngresosPropios,cRadicado FROM " + strTabla + " WITH(NOLOCK) WHERE nFolio" + documento + " IN ( " + strFolios + " )");
            }
            /*
			 * ARLA Se actualiza enviado SICOP a 1 Layout generado y cargado en
			 * SICOP RG con OC
			 */
            if ("1".equals(tipoPago)) {
                pstmtnUpEnviadoSICOP = conn.prepareStatement("UPDATE " + strTabla + " SET nEnviadoSICOP = 1 WHERE nFolio" + documento + " IN ( " + strFolios + " )");
                pstmtnUpEnviadoSICOP.executeUpdate();
            }
            rs = pstmtnSelect.executeQuery();
            while (rs.next()) {
                String recibo = rs.getString("caNoContrarrecibo");
                String tipoPoliza = rs.getString("cTipoPoliza");
                String fCancelacion = rs.getString("fCancelacion");
                String desPoliza = rs.getString("cDescripcionPoliza");
                String uniResp = rs.getString("cUnidadResponsableContable");
                String cRamo = rs.getString("cRamo");
                /*
				 * VGC21051222 Se agrega la UE que se requiere para la Pol Man
				 */
                String cUE = rs.getString("cUnidadResponsable");
                String numFonden = "";
                if (esSAIAlterno)
                    numFonden = rs.getString("cProyectoFonden");
                nFolioPago = Integer.parseInt(rs.getString("nFolio"), 10);
                int nFolPolCancelacion = Integer.parseInt(rs.getString("nFolioPolizaCancelacion"), 10);
                int intaEjercicioFiscal = Integer.parseInt(rs.getString("aEjercicioFiscal"), 10);
                if ("3".equals(tipoPago)) {
                    esIngreosPropios = "N";
                } else if ("4".equals(tipoPago)) {
                    esIngreosPropios = "S";
                } else {
                    esIngreosPropios = rs.getString("cIngresosPropios");
                }
                esRadicado = rs.getString("cRadicado");
                if (fCancelacion != null) {
                    fCancelacion = "'" + fCancelacion + "'";
                } else {
                    fCancelacion = null;
                }
                if ("S".equals(esIngreosPropios) || "S".equals(esRadicado)) {
                    strCLC = "-1";
                    solicitudPago = "-1";
                    numProceso = "-1";
                    folioSiaff = "-1";
                    fechaAplicadoSicop = FechaPagadoApl;
                    fechaPagoSicop = FechaPagadoApl;
                    aplicarPagado = true;
                } else {
                    /**
                     * Selecciona Informacion de SICOP y SIAFF **
                     */
                    pstmSicop = conn.prepareStatement(" SELECT SICOP.NCTR_47, SICOP.NCLC_43, SICOP.FECHA_APL, SICOP.FECHA_PAGO_106, SICOP.SPAG_176, SICOP.PROC_CLAVE, SICOP.FOLIO_SIAFF_112 " + " FROM CLC_SICOP SICOP WITH(NOLOCK), CLC_SIAFF_ENC SIAFF WITH(NOLOCK) " + " WHERE SIAFF.ESTATUS_CLC='Pagada' AND SICOP.FOLIO_SIAFF_112=SIAFF.FOLIO_CLC AND NCTR_47 = (SELECT dbo." + fn_integracion + "(?)) " + " GROUP BY SICOP.NCTR_47, SICOP.NCLC_43, SICOP.FECHA_APL, SICOP.FECHA_PAGO_106, SICOP.SPAG_176, SICOP.PROC_CLAVE, SICOP.FOLIO_SIAFF_112,NCLC_43 ");
                    pstmSicop.setString(1, recibo);
                    rs3 = pstmSicop.executeQuery();
                    if (rs3.next()) {
                        strCLC = rs3.getString("NCLC_43");
                        solicitudPago = rs3.getString("SPAG_176");
                        numProceso = rs3.getString("PROC_CLAVE");
                        folioSiaff = rs3.getString("FOLIO_SIAFF_112");
                        String[] fAS = rs3.getString("FECHA_APL").split("/");
                        fechaAplicadoSicop = fAS[2] + "-" + fAS[1] + "-" + fAS[0];
                        String[] fPS = rs3.getString("FECHA_PAGO_106").split("/");
                        fechaPagoSicop = fPS[2] + "-" + fPS[1] + "-" + fPS[0];
                        aplicarPagado = true;
                    }
                }
                if (aplicarPagado) {
                    // OBTENER SEQUENCE DE EJERCIDO
                    pstmUpEjercido = conn.prepareStatement("UPDATE CF_SEQUENCE  WITH (ROWLOCK) SET seq_value = seq_value + 1  WHERE seq_name = 'EJERCIDO' ");
                    pstmUpEjercido.executeUpdate();
                    pstmSeqEjercido = conn.prepareStatement("SELECT seq_value FROM CF_SEQUENCE WITH(NOLOCK) WHERE seq_name = 'EJERCIDO' ");
                    rs2 = pstmSeqEjercido.executeQuery();
                    if (rs2.next()) {
                        strFolioEjercido = rs2.getString("seq_value");
                    }
                    // OBTENER SEQUENCE DE PAGADO
                    pstmUpPagado = conn.prepareStatement("UPDATE CF_SEQUENCE  WITH (ROWLOCK) SET seq_value = seq_value + 1 WHERE seq_name = 'PAGADO' ");
                    pstmUpPagado.executeUpdate();
                    pstmSeqEjercido = conn.prepareStatement("SELECT seq_value FROM CF_SEQUENCE WITH(NOLOCK) WHERE seq_name = 'PAGADO' ");
                    rs2 = pstmSeqEjercido.executeQuery();
                    if (rs2.next()) {
                        strFolioPagado = rs2.getString("seq_value");
                    }
                    // Guarda En Cabecera Ejercido
                    String encE = "INSERT INTO tEjercidoEncabezado (cTipoPago,nFolioPAGO,caNoContrarrecibo,nFolioPoliza,cTipoPoliza,U_LOGIN,nFolioPolizaCancelacion,fCancelacion,cDescripcionPoliza,cUnidadResponsableContable,nFolioSICOP,fAplicacion,cRamo,cIdUsuarioCaptura,FechaAplicacionSicop,FechaPagoSicop,SolicitudPago,NumeroProceso,nFolioSIAFF,nFolioEjercido" + (esSAIAlterno ? ",cProyectoFonden" : "") + ") " + "VALUES ('" + strLetratipoPago + "'," + nFolioPago + ",'" + recibo + "'," + folioPoliza + ",'" + tipoPoliza + "','" + usuario + "'," + nFolPolCancelacion + "," + fCancelacion + ",'" + desPoliza + "','" + uniResp + "'," + strCLC + ",'" + (ejercicioFiscal != anioAplicacion ? fechaAplicacion : fechaAplicadoSicop) + "','" + cRamo + "','" + usuario + "','" + fechaAplicadoSicop + "','" + fechaPagoSicop + "'," + solicitudPago + "," + numProceso + "," + folioSiaff + "," + strFolioEjercido + (esSAIAlterno ? ",'" + numFonden + "'" : "") + " )";
                    pstmntInsertEj = conn.prepareStatement(encE);
                    int intr = pstmntInsertEj.executeUpdate();
                    if (ejercicioFiscal != anioAplicacion) {
                        cTipoPoliza = "DI";
                    }
                    // Guarda En Cabecera Pagado
                    String encP = "INSERT INTO tPagadoEncabezado (cTipoPago,nFolioPAGO,caNoContrarrecibo,nFolioPoliza,cTipoPoliza,U_LOGIN,nFolioPolizaCancelacion,fCancelacion,cDescripcionPoliza,cUnidadResponsableContable,nFolioSICOP,fAplicacion,cRamo,cIdUsuarioCaptura,FechaAplicacionSicop,FechaPagoSicop,SolicitudPago,NumeroProceso,nFolioSIAFF,FechaPagado,nFolioPagado" + (esSAIAlterno ? ", cProyectoFonden" : "") + ") " + "VALUES ('" + strLetratipoPago + "'," + nFolioPago + ",'" + recibo + "'," + folioPoliza + ",'" + cTipoPoliza + "','" + usuario + "'," + nFolPolCancelacion + "," + fCancelacion + ",'" + desPoliza + "','" + uniResp + "'," + strCLC + ",'" + (ejercicioFiscal != anioAplicacion ? fechaAplicacion : fechaAplicadoSicop) + "','" + cRamo + "','" + usuario + "','" + fechaAplicadoSicop + "','" + fechaPagoSicop + "'," + solicitudPago + "," + numProceso + "," + folioSiaff + ",'" + FechaPagado + "'," + strFolioPagado + (esSAIAlterno ? ", '" + numFonden + "'" : "") + " )";
                    pstmntInsertPag = conn.prepareStatement(encP);
                    int intrPag = pstmntInsertPag.executeUpdate();
                    if (intr > 0 && intrPag > 0) {
                        // Buscar detalles con el nFolio de Encabezado
                        String sqlDet = "SELECT " + strCamposDet + " FROM " + strTablaDet + " WITH(NOLOCK) WHERE cEvento != 'ANTICIPO' AND cEvento != 'ANTICIPO_DIV' AND " + strNomDetFolio + " = ? ";
                        pstmnSelectDet = conn.prepareStatement("SELECT " + strCamposDet + " FROM " + strTablaDet + " WITH(NOLOCK) WHERE cEvento != 'ANTICIPO' AND cEvento != 'ANTICIPO_DIV' AND " + strNomDetFolio + " = ? ");
                        pstmnSelectDet.setInt(1, nFolioPago);
                        rs2 = pstmnSelectDet.executeQuery();
                        int nDocRenglon = 0;
                        while (rs2.next()) {
                            nDocRenglon = Integer.parseInt(rs2.getString("nDocRenglon"), 10);
                            int cMes = Integer.parseInt(rs2.getString("cMes"), 10);
                            String strcEjercicio = rs2.getString("cEjercicio");
                            String EP = rs2.getString("EP");
                            String cOBGT = EP.substring(31, 36);
                            String cIdCuentaContable = (strTablaDet.equals("tOperAjenasDetalle")) ? " " : rs2.getString("cIdCuentaContable");
                            String cIdEntidadContable = rs2.getString("cIdEntidadContable");
                            String cIdRelacion = (strTablaDet.equals("tOperAjenasDetalle")) ? " " : rs2.getString("cIdRelacion");
                            String idTipoMovimiento = (strTablaDet.equals("tOperAjenasDetalle")) ? " " : rs2.getString("ID_TIPO_MOVIMIENTO");
                            String idTipoConcepto = (strTablaDet.equals("tOperAjenasDetalle")) ? " " : rs2.getString("ID_TIPO_CONCEPTO");
                            double mComprometido = (strTablaDet.equals("tOperAjenasDetalle")) ? 0.00 : rs2.getDouble("mComprometido");
                            double mImporteBruto = (strTablaDet.equals("tOperAjenasDetalle")) ? 0.00 : rs2.getDouble("mImporteBruto");
                            double mImporteIva = (strTablaDet.equals("tOperAjenasDetalle")) ? 0.00 : rs2.getDouble("mImporteIva");
                            double mImporteMasIva = (strTablaDet.equals("tOperAjenasDetalle")) ? 0.00 : rs2.getDouble("mImporteMasIva");
                            double mSancion = (strTablaDet.equals("tOperAjenasDetalle")) ? 0.00 : rs2.getDouble("mSancion");
                            double mDevolucion = (strTablaDet.equals("tOperAjenasDetalle")) ? 0.00 : rs2.getDouble("mDevolucion");
                            double mImporteAmortiza = (strTablaDet.equals("tOperAjenasDetalle")) ? 0.00 : rs2.getDouble("mImporteAmortiza");
                            double mRetencion = (strTablaDet.equals("tOperAjenasDetalle")) ? 0.00 : rs2.getDouble("mRetencion");
                            double mPenalizacion = (strTablaDet.equals("tOperAjenasDetalle")) ? 0.00 : rs2.getDouble("mPenalizacion");
                            double m2Millar = (strTablaDet.equals("tOperAjenasDetalle")) ? 0.00 : rs2.getDouble("m2Millar");
                            double m23IVA = (strTablaDet.equals("tOperAjenasDetalle")) ? 0.00 : rs2.getDouble("m23IVA");
                            double mImporteIvaHonorarios = (strTablaDet.equals("tOperAjenasDetalle")) ? 0.00 : rs2.getDouble("mImporteIvaHonorarios");
                            double mImporteIvaProv = (strTablaDet.equals("tOperAjenasDetalle")) ? 0.00 : rs2.getDouble("mImporteIvaProv");
                            double mImporteObra = (strTablaDet.equals("tOperAjenasDetalle")) ? 0.00 : rs2.getDouble("mImporteObra");
                            double mImporteIvaArrenda = (strTablaDet.equals("tOperAjenasDetalle")) ? 0.00 : rs2.getDouble("mImporteIvaArrenda");
                            int nPoliza = (strTablaDet.equals("tOperAjenasDetalle")) ? 0 : rs2.getInt("nPoliza");
                            String[] cEventoCa = rs2.getString("cEvento").split("_");
                            String cEvento = "P";
                            if (ejercicioFiscal != anioAplicacion) {
                                cEvento = cEvento + "_FA";
                            } else if ("S".equals(esRadicado) && "2".equals(tipoPago)) {
                                cEvento = cEvento + "R";
                            }
                            for (int i = 1; i < cEventoCa.length; i++) {
                                cEvento += "_";
                                cEvento += cEventoCa[i];
                            }
                            String cCentroContable = rs2.getString("cCentroContable");
                            String Rfc = rs2.getString("RFC");
                            String alm = (strTablaDet.equals("tOperAjenasDetalle")) ? " " : rs2.getString("ALM");
                            String nCapitulo = (strTablaDet.equals("tOperAjenasDetalle")) ? " " : rs2.getString("nCapitulo");
                            String altaAlmacen = "0";
                            String cEventoPagado = (strTablaDet.equals("tOperAjenasDetalle")) ? "P_AJENA" : cEvento;
                            if ("4".equals(tipoPago)) {
                                mPenalizacion = rs2.getDouble("mPenalizacion");
                            }
                            if (!strTablaDet.equals("tNOMINADetalle") && !strTablaDet.equals("tOperAjenasDetalle")) {
                                altaAlmacen = rs2.getString("altaAlmacen");
                            }
                            // Guarda Detalle Ejercido
                            String sqlinser = "INSERT INTO tEjercidoDetalle (cTipoPago,nFolioPAGO,nDocRenglon,cMes,cEjercicio,cIdEntidadContable,cIdRelacion,EP,cIdCuentaContable,mComprometido,nPoliza,ID_TIPO_MOVIMIENTO," + "              ID_TIPO_CONCEPTO,cEvento,nMes,cCentroContable,RFC, mImporteNeto,ALM,mImporteBruto,mImporteMasIva,mImporteIva,nCapitulo,mSancion,mDevolucion,mImporteAmortiza,mRetencion,mPenalizacion,m2Millar,m23IVA,mISRHonorarios,mObra5,mImporteFlete4,mISRArrenda,mRetImpuestoCedular,mImporteIvaArrenda,mImporteIvaHonorarios ,mImporteFlete23,mImporteIvaProv ,mImporteObra,mCNIC,mTesofe,altaAlmacen,aEjercicioFiscal,mIMDT,mImporte,nFolioEjercido,cUnidadResponsable)" + " VALUES('" + strLetratipoPago + "'," + nFolioPago + "," + nDocRenglon + "," + cMes + ",'" + strcEjercicio + "','" + cIdEntidadContable + "','" + cIdRelacion + "','" + EP + "','" + cIdCuentaContable + "'," + mComprometido + "," + nPoliza + ",'" + idTipoMovimiento + "','" + idTipoConcepto + "','EJERCIDO','" + cMes + "','" + cCentroContable + "','" + Rfc + "'," + rs2.getString("mImporteNeto") + ",'" + alm + "'," + mImporteBruto + "," + mImporteMasIva + "," + mImporteIva + ",'" + nCapitulo + "'," + mSancion + "," + mDevolucion + "," + mImporteAmortiza + " ," + mRetencion + "," + mPenalizacion + "," + m2Millar + "," + m23IVA + "," + rs2.getString("mISRHonorarios") + "," + rs2.getString("mObra5") + "       ," + rs2.getString("mImporteFlete4") + "," + rs2.getString("mISRArrenda") + "," + rs2.getString("mRetImpuestoCedular") + "," + mImporteIvaArrenda + "," + mImporteIvaHonorarios + "	," + rs2.getString("mImporteFlete23") + "," + mImporteIvaProv + "," + mImporteObra + "," + rs2.getString("mCNIC") + "," + rs2.getString("mTesofe") + ",'" + altaAlmacen + "','" + intaEjercicioFiscal + "'," + rs2.getString("mIMDT") + "," + rs2.getString("mImporteNeto") + "," + strFolioEjercido + ", '" + rs2.getString("cUnidadResponsable") + "' )";
                            pstmntInsertDet = conn.prepareStatement(sqlinser);
                            int intDet = pstmntInsertDet.executeUpdate();
                            log.trace("Object: {}", "Se insertaron en tEjercidoDetalle : " + intDet + " registros");
                            // Guarda Detalle Pagado
                            String sqlinsetP = "INSERT INTO tPagadoDetalle (cTipoPago,nFolioPAGO,nDocRenglon,cMes,cEjercicio,cIdEntidadContable,cIdRelacion,EP,cIdCuentaContable,mComprometido,nPoliza,ID_TIPO_MOVIMIENTO,ID_TIPO_CONCEPTO,cEvento,nMes,cCentroContable,RFC, mImporteNeto,ALM,mImporteBruto,mImporteMasIva,mImporteIva,nCapitulo,mSancion,mDevolucion,mImporteAmortiza,mRetencion,mPenalizacion,m2Millar,m23IVA,mISRHonorarios,mObra5    ,mImporteFlete4,mISRArrenda,mRetImpuestoCedular,mImporteIvaArrenda,mImporteIvaHonorarios ,mImporteFlete23,mImporteIvaProv ,mImporteObra,mCNIC,mTesofe,altaAlmacen,aEjercicioFiscal,mIMDT,mImporte,nFolioPagado,OBGT,CTAB, cUnidadResponsable)" + " VALUES('" + strLetratipoPago + "'," + nFolioPago + "," + nDocRenglon + "," + cMes + ",'" + strcEjercicio + "','" + cIdEntidadContable + "','" + cIdRelacion + "','" + EP + "','" + cIdCuentaContable + "'," + mComprometido + "," + nPoliza + ",'" + idTipoMovimiento + "','" + idTipoConcepto + "','" + cEventoPagado + "','" + cMes + "','" + cCentroContable + "','" + Rfc + "'," + rs2.getString("mImporteNeto") + ",'" + alm + "'," + mImporteBruto + "," + mImporteMasIva + "," + mImporteIva + ",'" + nCapitulo + "'," + mSancion + "," + mDevolucion + "," + mImporteAmortiza + " ," + mRetencion + "," + mPenalizacion + "," + m2Millar + "," + m23IVA + "," + rs2.getString("mISRHonorarios") + "," + rs2.getString("mObra5") + "       ," + rs2.getString("mImporteFlete4") + "," + rs2.getString("mISRArrenda") + "," + rs2.getString("mRetImpuestoCedular") + "," + mImporteIvaArrenda + "," + mImporteIvaHonorarios + "	," + rs2.getString("mImporteFlete23") + "," + mImporteIvaProv + "," + mImporteObra + "," + rs2.getString("mCNIC") + "," + rs2.getString("mTesofe") + ",'" + altaAlmacen + "','" + intaEjercicioFiscal + "'," + rs2.getString("mIMDT") + "," + rs2.getString("mImporteNeto") + "," + strFolioPagado + ",'" + cOBGT + "','" + ctab + "', '" + rs2.getString("cUnidadResponsable") + "' )";
                            pstmntInsertDetPag = conn.prepareStatement(sqlinsetP);
                            int intDetPag = pstmntInsertDetPag.executeUpdate();
                            log.trace("Object: {}", "Se insertaron en tPagadoDetalle : " + intDetPag + " registros");
                        }
                        // APLICACION CONTABLE
                        accEng.makeAccountingApplication(conn, "EJERCIDO", strFolioEjercido, "tEjercidoEncabezado", "tEjercidoDetalle", "nFolioEjercido");
                        // conn.commit();
                        boolean pagado = false;
                        accEng.makeAccountingApplication(conn, "PAGADO", strFolioPagado, "tPagadoEncabezado", "tPagadoDetalle", "nFolioPagado");
                        conn.commit();
                        pagado = true;
                        /*
						 * VGC20151222 Se genera una poliza que cancela la
						 * afectacion de cuentas contables.
						 */
                        if (generaPoliza && pagado) {
                            GeneradorPolizaManualBusinessLogic gpmbl = new GeneradorPolizaManualBusinessLogic(GestionInterface.ATT_CONEXION);
                            gpmbl.generaPolizaCancelaPago(strFolioPagado, cUE);
                        }
                        // Actualizar tIngresoPago cDocHaplicado
                        if (pagado && "S".equals(esRadicado)) {
                            pstmUpIngPag = conn.prepareStatement("UPDATE tIngresoPagoEncabezado SET fAplicacion ='" + FechaPagadoApl + "', cDocumentoHaplicado = 'S' WHERE cTipoSolicitud = '" + strLetratipoPago + "' AND  nFolioSolicitud = " + nFolioPago);
                            pstmUpIngPag.executeUpdate();
                        }
                        valor = "Insertado";
                    } else {
                        log.debug("Object: " + String.valueOf("No Guarda Encabezado [" + recibo + "]"));
                    }
                } else {
                    valor = "No existe en Archivos de SICOP";
                }
            }
        } catch (Exception ex) {
            log.warn(ex.getMessage(), ex);
            log.debug("Object: " + String.valueOf(ex.getLocalizedMessage()));
            try {
                conn.rollback();
            } catch (Exception ee) {
                log.warn("Error: cerrando rollback ", ee);
            }
            valor = ex.toString();
        } finally {
            try {
                CloseObject.closeObject(rs);
                CloseObject.closeObject(rs2);
                CloseObject.closeObject(rs3);
                CloseObject.closeObject(pstmtnSelect);
                CloseObject.closeObject(pstmntInsertEj);
                CloseObject.closeObject(pstmUpEjercido);
                CloseObject.closeObject(pstmSeqEjercido);
                CloseObject.closeObject(pstmUpPagado);
                CloseObject.closeObject(pstmnSelectDet);
                CloseObject.closeObject(pstmntInsertDet);
                CloseObject.closeObject(pstmntInsertPag);
                CloseObject.closeObject(pstmntInsertDetPag);
                CloseObject.closeObject(pstmSicop);
                CloseObject.closeObject(conn);
            } catch (Exception e) {
                log.warn("Error: cerrando statement", e);
            }
        }
        return valor;
    }

    public String aplicacionPagosFuera(String strFolio, String strCXP, String strTipoPago, String fPago, String fEjer, String strUsuario, String strfolioSICOP, String strsolPago, String strnumProceso, String strfolioSIAFF, String CTABLaudo) throws Exception {
        Connection conn = null;
        PreparedStatement pstmtnSelect = null, pstmntInsertEj = null, pstmnSelectDet = null, pstmntInsertDet = null, pstmntInsertPag = null, pstmntInsertDetPag = null, upd_fAplicacion = null, pstmUpEjercido = null, pstmSeqEjercido = null, pstmUpPagado = null, pstmRadicado = null, pstmBuscaEjercido = null, pstmBuscaPagado = null, pstmSAIAlterno = null, psRFCCuotas = null, psUpdateRFCCuotas = null;
        ResultSet rs = null, rs2 = null, rsRadicado = null, rsbe = null, rsbp = null, rsSAIAlterno = null, rsRFCCuotas;
        String valor = "";
        String queryUpdateFechaAplicacion = "";
        String strFolioEjercido = null;
        String strFolioPagado = null;
        String strLetratipoPago = "";
        String FechaPagado = fPago;
        String FechaEjercido = fEjer;
        String strTabla = "";
        String strTablaDet = "";
        String strCampos = "";
        String strCamposDet = "";
        String nomCamposDetValidar = "";
        String nomCXP = "";
        String strNomDetFolio = "";
        String usuario = strUsuario;
        String strRadicado = "";
        int ejercidoInsertado = 0;
        int pagadoInsertado = 0;
        String ejercidoAplicado = "";
        String pagadoAplicado = "";
        int folioPoliza = 0;
        int nFolioPago = 0;
        try {
            conn = getConnection();
            int ejercicioFiscal = EjercicioFiscalManager.getEjercicioFiscal(conn).getEjercicio();
            int anioAplicacion = Util.getYearFromDate(FechaPagado);
            String fechaAplicacion = (ejercicioFiscal == anioAplicacion ? fPago : "31/12/" + ejercicioFiscal);
            String cTipoPoliza = "EG";
            boolean appCont = true, appContPag = true;
            AccountingEngine accEng = new AccountingEngine();
            accEng.setValidaInsuficienciaDeSaldo(true);
            // ARLA 20072021 Busca si existe el ejercido y esta aplicado
            pstmBuscaEjercido = conn.prepareStatement("SELECT COUNT(*) AS ejercidoInsertado, cDocumentoHaplicado, nFolioEjercido FROM tEjercidoEncabezado WHERE caNoContrarrecibo = ? GROUP BY cDocumentoHaplicado, nFolioEjercido");
            pstmBuscaEjercido.setString(1, strCXP);
            rsbe = pstmBuscaEjercido.executeQuery();
            if (rsbe.next()) {
                ejercidoInsertado = rsbe.getInt("ejercidoInsertado");
                ejercidoAplicado = rsbe.getString("cDocumentoHaplicado");
                strFolioEjercido = rsbe.getString("nFolioEjercido");
            }
            // ARLA 20072021 Busca si existe el pagado y esta aplicado
            pstmBuscaPagado = conn.prepareStatement("SELECT COUNT(*) AS pagadoInsertado, cDocumentoHaplicado, nFolioPagado FROM tPagadoEncabezado WHERE caNoContrarrecibo = ? GROUP BY cDocumentoHaplicado, nFolioPagado");
            pstmBuscaPagado.setString(1, strCXP);
            rsbp = pstmBuscaPagado.executeQuery();
            if (rsbp.next()) {
                pagadoInsertado = rsbp.getInt("pagadoInsertado");
                pagadoAplicado = rsbp.getString("cDocumentoHaplicado");
                strFolioPagado = rsbp.getString("nFolioPagado");
            }
            // APLICACION DE LA INTEGRACION
            if (strTipoPago.equals("INTEGRACION")) {
                queryUpdateFechaAplicacion = "UPDATE tconsolidacionrelaciongastosEncabezado SET fAplicacion = ? WHERE nFolioConsolidacion = ?";
                upd_fAplicacion = conn.prepareStatement(queryUpdateFechaAplicacion);
                upd_fAplicacion.setString(1, fechaAplicacion);
                upd_fAplicacion.setString(2, strFolio);
                upd_fAplicacion.executeUpdate();
                appContPag = accEng.makeAccountingApplication(conn, "CONSOLIDACIONRG", strFolio, "tconsolidacionrelaciongastosEncabezado", "tconsolidacionrelaciongastosdetalle", "nFolioConsolidacion");
                conn.commit();
                valor = "ingresado";
            } else {
                if (strTipoPago.equals("PAGODIVERSO")) {
                    strTabla = "tPAGODIVERSOEncabezado";
                    strTablaDet = "tPAGODIVERSODetalle";
                    strCampos = "nFolioPAGODIVERSO AS nFolio,mImporteNeto as impNeto,caNoContrarrecibo,nFolioPoliza,cTipoPoliza,U_LOGIN,isnull(nFolioPolizaCancelacion,'0') AS nFolioPolizaCancelacion,isnull(fCancelacion,NULL) AS fCancelacion,cDescripcionPoliza,cUnidadResponsableContable,fAplicacion,cRamo,aEjercicioFiscal,CASE WHEN cEsRelacionGastos='N' THEN CTAB ELSE dbo.fn_cuentaIntegradora(dbo.fn_IntegracionPDIV(caNoContrarrecibo)) END CTAB";
                    strCamposDet = "nDocRenglon,cMes,cEjercicio, EP,cIdCuentaContable,mComprometido,isnull(nPoliza,0) AS nPoliza,ID_TIPO_MOVIMIENTO,ID_TIPO_CONCEPTO,cEvento,cCentroContable,RFC,mImporteNeto,ALM,mImporteBruto,mImporteMasIva,mImporteIva,nCapitulo,mSancion,mDevolucion,mImporteAmortiza,mRetencion,mPenalizacion,m2Millar,m23IVA,mISRHonorarios,mObra5,mImporteFlete4,mISRArrenda,mRetImpuestoCedular,mImporte,mImporteIvaArrenda,mImporteIvaHonorarios,mImporteFlete23,mImporteIvaProv,mImporteObra,isnull(mCNIC,0.00) AS mCNIC,isnull(mIMDT,0.00) AS mIMDT,isnull(mTesofe,0.00) AS mTesofe,altaAlmacen,cCentroContable AS cIdEntidadContable, cIdRelacion,mImporteISRLaudos,mISROtros, dbo.fnMontoPasivoDiferido( '" + strTipoPago + "',nFolioPAGODIVERSO, nDocRenglon ) AS montoPasivoDiferdio, mImporteIva6";
                    nomCamposDetValidar = "cEvento, EP, SUM(CONVERT(money,mImporteNeto)) AS mImporteNeto,cCentroContable,RFC,cEjercicio, cIdRelacion,nCapitulo";
                    nomCXP = "caNoContrarrecibo";
                    strNomDetFolio = "nFolioPAGODIVERSO";
                    strLetratipoPago = "PAGODIVERSO";
                } else if (strTipoPago.equals("PAGOOBRA")) {
                    strTabla = "tPAGOOBRAEncabezado";
                    strTablaDet = "tPAGOOBRADetalle";
                    strCampos = "nFolioPAGOOBRA AS nFolio,mImporteNeto as impNeto,caNoContrarrecibo,cDocumentoHaplicado,nFolioPoliza,cTipoPoliza,U_LOGIN,isnull(nFolioPolizaCancelacion,'0') AS nFolioPolizaCancelacion,isnull(fCancelacion,NULL) AS fCancelacion,cDescripcionPoliza,cUnidadResponsableContable,fAplicacion,cRamo,aEjercicioFiscal,CTAB";
                    strCamposDet = "nDocRenglon,cMes,cEjercicio, EP,cIdCuentaContable,mComprometido,isnull(nPoliza,0) AS nPoliza,ID_TIPO_MOVIMIENTO,ID_TIPO_CONCEPTO,cEvento,cCentroContable,RFC,mImporteNeto,ALM,mImporteBruto,mImporteMasIva,mImporteIva,nCapitulo,mSancion,mDevolucion,mImporteAmortiza,mRetencion,mPenalizacion,m2Millar,m23IVA,mISRHonorarios,mObra5,mImporteFlete4,mISRArrenda,mRetImpuestoCedular,mImporte,mImporteIvaArrenda,mImporteIvaHonorarios,mImporteFlete23,mImporteIvaProv,mImporteObra,isnull(mCNIC,0.00) AS mCNIC,isnull(mIMDT,0.00) AS mIMDT,isnull(mTesofe,0.00) AS mTesofe,altaAlmacen,cCentroContable AS cIdEntidadContable, cIdRelacion,mImporteISRLaudos,mISROtros, dbo.fnMontoPasivoDiferido( '" + strTipoPago + "',nFolioPAGOOBRA, nDocRenglon) AS montoPasivoDiferdio, mImporteIva6, cUnidadResponsable";
                    nomCamposDetValidar = "cEvento, EP, SUM(CONVERT(money,mImporteNeto)) AS mImporteNeto,cCentroContable,RFC,cEjercicio, cIdRelacion,nCapitulo";
                    nomCXP = "caNoContrarrecibo";
                    strNomDetFolio = "nFolioPAGOOBRA";
                    strLetratipoPago = "PAGOOBRA";
                } else if (strTipoPago.equals("FEDERALIZADO")) {
                    strTabla = "tPAGOFEDERALIZADOEncabezado";
                    strTablaDet = "tPAGOFEDERALIZADODetalle";
                    strCampos = "nFolioPAGOFEDERALIZADO AS nFolio,mImporteNeto as impNeto,caNoContrarrecibo,cDocumentoHaplicado,nFolioPoliza,cTipoPoliza,U_LOGIN,isnull(nFolioPolizaCancelacion,'0') AS nFolioPolizaCancelacion,isnull(fCancelacion,NULL) AS fCancelacion,cDescripcionPoliza,cUnidadResponsableContable,fAplicacion,cRamo,aEjercicioFiscal,CTAB";
                    strCamposDet = "nDocRenglon,cMes,cEjercicio, EP,cIdCuentaContable,mComprometido,isnull(nPoliza,0) AS nPoliza,ID_TIPO_MOVIMIENTO,ID_TIPO_CONCEPTO,cEvento,cCentroContable,RFC,mImporteNeto,ALM,mImporteBruto,mImporteMasIva,mImporteIva,nCapitulo,mSancion,mDevolucion,mImporteAmortiza,mRetencion,mPenalizacion,m2Millar,m23IVA,mISRHonorarios,mObra5,mImporteFlete4,mISRArrenda,mRetImpuestoCedular,mImporte,mImporteIvaArrenda,mImporteIvaHonorarios,mImporteFlete23,mImporteIvaProv,mImporteObra,isnull(mCNIC,0.00) AS mCNIC,isnull(mIMDT,0.00) AS mIMDT,isnull(mTesofe,0.00) AS mTesofe,altaAlmacen,cCentroContable AS cIdEntidadContable, cIdRelacion,mImporteISRLaudos,mISROtros, dbo.fnMontoPasivoDiferido( 'FEDERALIZADO',nFolioPAGOFEDERALIZADO, nDocRenglon) AS montoPasivoDiferdio, mImporteIva6, cUnidadResponsable";
                    nomCamposDetValidar = "cEvento, EP, SUM(CONVERT(money,mImporteNeto)) AS mImporteNeto,cCentroContable,RFC,cEjercicio, cIdRelacion,nCapitulo";
                    nomCXP = "caNoContrarrecibo";
                    strNomDetFolio = "nFolioPAGOFEDERALIZADO";
                    strLetratipoPago = "FEDERALIZADO";
                } else if (strTipoPago.equals("RELACIONGASTOS")) {
                    strTabla = "tRELACIONGASTOSEncabezado";
                    strTablaDet = "tRELACIONGASTOSDetalle";
                    strCampos = "nFolioRELACIONGASTOS AS nFolio,mImporteNeto as impNeto,caNoContrarrecibo,nFolioPoliza,cTipoPoliza,U_LOGIN,isnull(nFolioPolizaCancelacion,'0') AS nFolioPolizaCancelacion,isnull(fCancelacion,NULL) AS fCancelacion,cDescripcionPoliza,cUnidadResponsableContable,fAplicacion,cRamo,aEjercicioFiscal,(SELECT TOP 1 sCUENTA_BANCARIA FROM dbo.tLayoutsCreadosRelacionGastosHeader WHERE sAuxiliarComodin=(SELECT dbo.fn_IntegracionRG('" + strCXP + "')))CTAB";
                    strCamposDet = "nDocRenglon,cMes,cEjercicio, EP,cIdCuentaContable,mComprometido,isnull(nPoliza,0) AS nPoliza,ID_TIPO_MOVIMIENTO,ID_TIPO_CONCEPTO,cEvento,cCentroContable,RFC,mImporteNeto,ALM,mImporteBruto,mImporteMasIva,mImporteIva,nCapitulo,mSancion,mDevolucion,mImporteAmortiza,mRetencion,mPenalizacion,m2Millar,m23IVA,mISRHonorarios,mObra5,mImporteFlete4,mISRArrenda,mRetImpuestoCedular,mImporte,mImporteIvaArrenda,mImporteIvaHonorarios,mImporteFlete23,mImporteIvaProv,mImporteObra,isnull(mCNIC,0.00) AS mCNIC,isnull(mIMDT,0.00) AS mIMDT,isnull(mTesofe,0.00) AS mTesofe,altaAlmacen,cCentroContable AS cIdEntidadContable, cIdRelacion,mImporteISRLaudos,mISROtros, dbo.fnMontoPasivoDiferido( '" + strTipoPago + "',nFolioRELACIONGASTOS, nDocRenglon) AS montoPasivoDiferdio, mImporteIva6, cPasivo, cUnidadResponsable,mimporteISRResico ";
                    nomCamposDetValidar = "cEvento, EP, SUM(CONVERT(money,mImporteNeto)) AS mImporteNeto,cCentroContable,RFC,cEjercicio, cIdRelacion,nCapitulo";
                    nomCXP = "caNoContrarrecibo";
                    strNomDetFolio = "nFolioRELACIONGASTOS";
                    strLetratipoPago = "RELACIONGASTOS";
                } else if (strTipoPago.equals("PAGODIRECTO")) {
                    strTabla = "tPagoDirectoEncabezado";
                    strTablaDet = "tPagoDirectoDetalle";
                    strCampos = "nFolioPagoDirecto AS nFolio, mImporteNeto as impNeto,caNoContrarrecibo,nFolioPoliza,cTipoPoliza,U_LOGIN,isnull(nFolioPolizaCancelacion,'0') AS nFolioPolizaCancelacion,isnull(fCancelacion,NULL) AS fCancelacion,cDescripcionPoliza,cUnidadResponsableContable,fAplicacion,cRamo,aEjercicioFiscal,CTAB";
                    strCamposDet = "nDocRenglon,cMes,cEjercicio, EP,cIdCuentaContable,mComprometido,isnull(nPoliza,0) AS nPoliza,ID_TIPO_MOVIMIENTO,ID_TIPO_CONCEPTO,cEvento,cCentroContable,RFC,mImporteNeto,ALM,mImporteBruto,mImporteMasIva,mImporteIva,nCapitulo,mSancion,mDevolucion,mImporteAmortiza,mRetencion,mPenalizacion,m2Millar,m23IVA,mISRHonorarios,mObra5,mImporteFlete4,mISRArrenda,mRetImpuestoCedular,mImporte,mImporteIvaArrenda,mImporteIvaHonorarios,mImporteFlete23,mImporteIvaProv,mImporteObra,isnull(mCNIC,0.00) AS mCNIC,isnull(mIMDT,0.00) AS mIMDT,isnull(mTesofe,0.00) AS mTesofe,altaAlmacen,cCentroContable AS cIdEntidadContable,cIdDocumento AS cIdRelacion,mImporteISRLaudos,mISROtros, dbo.fnMontoPasivoDiferido( '" + strTipoPago + "',nFolioPagoDirecto, nDocRenglon) AS montoPasivoDiferdio, mImporteIva6, mimporteISRResico, cUnidadResponsable";
                    nomCamposDetValidar = "cEvento, EP, cCentroContable,SUM(CONVERT(money,mImporteNeto)) AS mImporteNeto,RFC,cEjercicio,cIdDocumento AS cIdRelacion,nCapitulo";
                    nomCXP = "caNoContrarrecibo";
                    strNomDetFolio = "nFolioPagoDirecto";
                    strLetratipoPago = "PAGODIRECTO";
                } else if (strTipoPago.equals("AJENAS")) {
                    strTabla = "tOperAjenasEncabezado";
                    strTablaDet = "tOperAjenasDetalle";
                    // INGRESOS PROPIOS
                    strCampos = "nFolioOperAjenas AS nFolio,mImportes as impNeto,caNoContrarrecibo,cDocumentoHaplicado,nFolioPoliza,cTipoPoliza,U_LOGIN,isnull(nFolioPolizaCancelacion,'0') AS nFolioPolizaCancelacion,isnull(fCancelacion,NULL) AS fCancelacion,cDescripcionPoliza,cUnidadResponsableContable,fAplicacion,cRamo,aEjercicioFiscal,(SELECT GP_VALOR FROM CG_GRUPO_PROPIEDADES WITH (NOLOCK) WHERE GP_NOMBRE = 'CUENTA INGRESOS PROPIOS') AS CTAB";
                    strCamposDet = "nDocRenglon,cMes,aEjercicioFiscal AS cEjercicio,Ep AS EP,cEvento,cCentroContable,RFC,mTotal AS mImporteNeto,mISRHonorarios,mObra5,mImporteFlete4,mISRArrenda,mRetImpuestoCedular,mImporteFlete23,isnull(mCNIC,0.00) AS mCNIC,isnull(mIMDT,0.00) AS mIMDT,isnull(mTesofe,0.00) AS mTesofe,cCentroContable AS cIdEntidadContable,mImporteIvaArrenda,mImporteIvaHonorarios,mImporteFlete23,mRetImpuestoCedular,mImporteISRLaudos,mISROtros, dbo.fnMontoPasivoDiferido( '" + strTipoPago + "',nFolioOperAjenas, nDocRenglon) AS montoPasivoDiferdio, mImporteIva6, mimporteISRResico, cUnidadResponsable";
                    nomCamposDetValidar = "cEvento, Ep AS EP, SUM(CONVERT(money,mTotal)) AS mImporteNeto,cCentroContable,RFC,aEjercicioFiscal AS cEjercicio";
                    nomCXP = "caNoContrarrecibo";
                    strNomDetFolio = "nFolioOperAjenas";
                    strLetratipoPago = "AJENAS";
                } else if (strTipoPago.equals("PAGOPENASCONV")) {
                    strTabla = "tPagoPenasConvEncabezado";
                    strTablaDet = "tPagoPenasConvDetalle";
                    // INGRESOS PROPIOS
                    strCampos = "nFolioPagoPenasConv AS nFolio,(SELECT SUM(MPENALIZACION) FROM tPagoPenasConvDetalle WHERE nFolioPagoPenasConv = tPagoPenasConvEncabezado.nFolioPagoPenasConv ) as impNeto,caNoContrarrecibo,cDocumentoHaplicado,nFolioPoliza,cTipoPoliza,U_LOGIN,isnull(nFolioPolizaCancelacion,'0') AS nFolioPolizaCancelacion,isnull(fCancelacion,NULL) AS fCancelacion,cDescripcionPoliza,cUnidadResponsableContable,fAplicacion,cRamo,aEjercicioFiscal,(SELECT GP_VALOR FROM CG_GRUPO_PROPIEDADES WITH (NOLOCK) WHERE GP_NOMBRE = 'CUENTA INGRESOS PROPIOS') AS CTAB";
                    strCamposDet = "nDocRenglon,cMes,cEjercicio AS cEjercicio,Ep AS EP,cEvento,cCentroContable,RFC,mPenalizacion AS mImporteNeto, mPenalizacion AS mImporteMasIva, 0 mISRHonorarios,0 mObra5,0 mImporteFlete4,0 mISRArrenda,0 mRetImpuestoCedular,0 mImporteFlete23,0 AS mCNIC,0 AS mIMDT,0 AS mTesofe,cCentroContable AS cIdEntidadContable, 0 mImporteIvaArrenda,0 mImporteIvaHonorarios,0 mImporteFlete23,0 mRetImpuestoCedular,0 mImporteISRLaudos,0  mISROtros, dbo.fnMontoPasivoDiferido( '\" + strTipoPago + \"',nFolioPagoPenasConv, nDocRenglon) AS montoPasivoDiferdio, 0 mImporteIva6,0  mimporteISRResico, cUnidadResponsable , mPenalizacion";
                    nomCamposDetValidar = "cEvento, Ep AS EP, SUM(CONVERT(money,mPenalizacion)) AS mImporteNeto,cCentroContable,RFC,cEjercicio AS cEjercicio";
                    nomCXP = "caNoContrarrecibo";
                    strNomDetFolio = "nFolioPagoPenasConv";
                    strLetratipoPago = "PAGOPENASCONV";
                }
                // Buscar Informacion Cabecera
                pstmtnSelect = conn.prepareStatement("SELECT " + strCampos + " FROM " + strTabla + " WITH(NOLOCK) WHERE " + nomCXP + "= ? ");
                pstmtnSelect.setString(1, strCXP);
                rs = pstmtnSelect.executeQuery();
                if (rs.next()) {
                    if (ejercidoInsertado == 0) {
                        // OBTENER SEQUENCE DE EJERCIDO
                        pstmUpEjercido = conn.prepareStatement("UPDATE CF_SEQUENCE  WITH (ROWLOCK) SET seq_value = seq_value + 1  WHERE seq_name = 'EJERCIDO' ");
                        pstmUpEjercido.executeUpdate();
                        pstmSeqEjercido = conn.prepareStatement("SELECT seq_value FROM CF_SEQUENCE WITH(NOLOCK) WHERE seq_name = 'EJERCIDO' ");
                        rs2 = pstmSeqEjercido.executeQuery();
                        if (rs2.next()) {
                            strFolioEjercido = rs2.getString("seq_value");
                        }
                    }
                    if (pagadoInsertado == 0) {
                        // OBTENER SEQUENCE DE PAGADO
                        pstmUpPagado = conn.prepareStatement("UPDATE CF_SEQUENCE  WITH (ROWLOCK) SET seq_value = seq_value + 1 WHERE seq_name = 'PAGADO' ");
                        pstmUpPagado.executeUpdate();
                        pstmSeqEjercido = conn.prepareStatement("SELECT seq_value FROM CF_SEQUENCE WITH(NOLOCK) WHERE seq_name = 'PAGADO' ");
                        rs2 = pstmSeqEjercido.executeQuery();
                        if (rs2.next()) {
                            strFolioPagado = rs2.getString("seq_value");
                        }
                    }
                    // rs.getString("caNoContrarrecibo");
                    String recibo = strCXP;
                    String tipoPoliza = rs.getString("cTipoPoliza");
                    String fCancelacion = rs.getString("fCancelacion");
                    String desPoliza = rs.getString("cDescripcionPoliza");
                    String uniResp = rs.getString("cUnidadResponsableContable");
                    String cRamo = rs.getString("cRamo");
                    String strCTAB = rs.getString("CTAB");
                    nFolioPago = Integer.parseInt(rs.getString("nFolio"), 10);
                    int nFolPolCancelacion = Integer.parseInt(rs.getString("nFolioPolizaCancelacion"), 10);
                    int intaEjercicioFiscal = Integer.parseInt(rs.getString("aEjercicioFiscal"), 10);
                    int intr = 0;
                    int intrPag = 0;
                    int intDet = 0;
                    int intDetPag = 0;
                    if (fCancelacion != null) {
                        fCancelacion = "'" + fCancelacion + "'";
                    } else {
                        fCancelacion = null;
                    }
                    if (ejercidoInsertado == 0) {
                        // Guarda En Cabecera Ejercido
                        String encE = "INSERT INTO tEjercidoEncabezado (cTipoPago,nFolioPAGO,caNoContrarrecibo,nFolioPoliza,cTipoPoliza," + "U_LOGIN,nFolioPolizaCancelacion,fCancelacion,cDescripcionPoliza,cUnidadResponsableContable," + "nFolioSICOP,fAplicacion,cRamo,cIdUsuarioCaptura,FechaAplicacionSicop," + "FechaPagoSicop,SolicitudPago,NumeroProceso,nFolioSIAFF,nFolioEjercido) " + "        VALUES ('" + strLetratipoPago + "'," + nFolioPago + ",'" + recibo + "'," + folioPoliza + ",'" + tipoPoliza + "','" + usuario + "'," + nFolPolCancelacion + "," + fCancelacion + ",'" + desPoliza + "','" + uniResp + "'," + strfolioSICOP + ",'" + fechaAplicacion + "','" + cRamo + "','" + usuario + "','" + FechaEjercido + "','" + FechaEjercido + "'," + strsolPago + "," + strnumProceso + "," + strfolioSIAFF + "," + strFolioEjercido + " )";
                        pstmntInsertEj = conn.prepareStatement(encE);
                        intr = pstmntInsertEj.executeUpdate();
                    } else {
                        intr = 1;
                    }
                    if (anioAplicacion != intaEjercicioFiscal) {
                        cTipoPoliza = "DI";
                    }
                    if (pagadoInsertado == 0) {
                        // Guarda En Cabecera Pagado
                        String encP = "INSERT INTO tPagadoEncabezado (cTipoPago,nFolioPAGO,caNoContrarrecibo,nFolioPoliza,cTipoPoliza," + "U_LOGIN,nFolioPolizaCancelacion,fCancelacion,cDescripcionPoliza,cUnidadResponsableContable," + "nFolioSICOP,fAplicacion,cRamo,cIdUsuarioCaptura,FechaAplicacionSicop," + "FechaPagoSicop,SolicitudPago,NumeroProceso,nFolioSIAFF,FechaPagado,nFolioPagado) " + "VALUES ('" + strLetratipoPago + "'," + nFolioPago + ",'" + recibo + "'," + folioPoliza + ",'" + cTipoPoliza + "','" + usuario + "'," + nFolPolCancelacion + "," + fCancelacion + ",'" + desPoliza + "','" + uniResp + "'," + strfolioSICOP + ",'" + fechaAplicacion + "','" + cRamo + "','" + usuario + "','" + FechaPagado + "','" + FechaPagado + "'," + strsolPago + "," + strnumProceso + "," + strfolioSIAFF + ",'" + FechaPagado + "'," + strFolioPagado + " )";
                        pstmntInsertPag = conn.prepareStatement(encP);
                        intrPag = pstmntInsertPag.executeUpdate();
                    } else {
                        intrPag = 1;
                    }
                    if (intr > 0 && intrPag > 0) {
                        // Buscar detalles con el nFolio de Encabezado
                        // String sqlDet = "SELECT " + strCamposDet + " FROM " +
                        // strTablaDet + " WITH(NOLOCK) WHERE cEvento !=
                        // 'ANTICIPO' AND cEvento != 'ANTICIPO_DIV' AND " +
                        // strNomDetFolio + " = ? ";
                        pstmnSelectDet = conn.prepareStatement("SELECT " + strCamposDet + " FROM " + strTablaDet + " WITH(NOLOCK) WHERE cEvento != 'ANTICIPO' AND cEvento != 'ANTICIPO_DIV' AND " + strNomDetFolio + " = ? " + "order by ndocRenglon");
                        pstmnSelectDet.setInt(1, nFolioPago);
                        rs2 = pstmnSelectDet.executeQuery();
                        int nDocRenglon = 0;
                        int renglonPasivoDiFerido = 0;
                        String cEsPagoCuotas = "";
                        String cRFCCuotas = "";
                        String RFC_PC = "";
                        String PC = "";
                        if (strTablaDet.equals("tRELACIONGASTOSDetalle")) {
                            StringBuilder queryesRFCCuotas = new StringBuilder("SELECT cEsPagoCuotas, cRFCCuotas, PC.cRFC, cSubcuenta AS PC ");
                            queryesRFCCuotas.append("FROM tRELACIONGASTOSDetalle DET WITH (NOLOCK) ");
                            queryesRFCCuotas.append("LEFT JOIN tComprobacionLaudos COMP WITH (NOLOCK) ON DET.nFolioRELACIONGASTOS = COMP.nFolioRELACIONGASTOS ");
                            queryesRFCCuotas.append("LEFT JOIN tpasivosContingentes PC WITH (NOLOCK) ON DET.cPasivo = cSubcuenta ");
                            queryesRFCCuotas.append("WHERE DET.nFolioRELACIONGASTOS = ?");
                            psRFCCuotas = conn.prepareStatement(queryesRFCCuotas.toString());
                            psRFCCuotas.setInt(1, nFolioPago);
                            rsRFCCuotas = psRFCCuotas.executeQuery();
                            if (rsRFCCuotas.next()) {
                                cEsPagoCuotas = rsRFCCuotas.getString("cEsPagoCuotas");
                                cRFCCuotas = rsRFCCuotas.getString("cRFCCuotas");
                                RFC_PC = rsRFCCuotas.getString("cRFC");
                                PC = rsRFCCuotas.getString("PC");
                            }
                        }
                        while (rs2.next()) {
                            if (strTablaDet.equals("tPAGOFEDERALIZADODetalle") || strTablaDet.equals("tRELACIONGASTOSDetalle")) {
                                nDocRenglon = nDocRenglon + 1 + renglonPasivoDiFerido;
                            } else {
                                nDocRenglon = Integer.parseInt(rs2.getString("nDocRenglon"), 10) + renglonPasivoDiFerido;
                            }
                            int cMes = Integer.parseInt(rs2.getString("cMes"), 10);
                            String strcEjercicio = rs2.getString("cEjercicio");
                            String EP = rs2.getString("EP");
                            // String esIngreosPropios = ( EP.substring( 39, 40
                            // ).equals( "4" ) ? "S" : "N" );
                            String cOBGT = EP.substring(31, 36);
                            String cIdCuentaContable = (strTablaDet.equals("tOperAjenasDetalle") || strTablaDet.equals("tPagoPenasConvDetalle")) ? " " : rs2.getString("cIdCuentaContable");
                            String cIdEntidadContable = rs2.getString("cIdEntidadContable");
                            String cIdRelacion = (strTablaDet.equals("tOperAjenasDetalle") || strTablaDet.equals("tPagoPenasConvDetalle")) ? " " : rs2.getString("cIdRelacion");
                            String idTipoMovimiento = (strTablaDet.equals("tOperAjenasDetalle") || strTablaDet.equals("tPagoPenasConvDetalle")) ? " " : rs2.getString("ID_TIPO_MOVIMIENTO");
                            String idTipoConcepto = (strTablaDet.equals("tOperAjenasDetalle") || strTablaDet.equals("tPagoPenasConvDetalle")) ? " " : rs2.getString("ID_TIPO_CONCEPTO");
                            double mComprometido = (strTablaDet.equals("tOperAjenasDetalle") || strTablaDet.equals("tPagoPenasConvDetalle")) ? 0.00 : rs2.getDouble("mComprometido");
                            double mImporteBruto = (strTablaDet.equals("tOperAjenasDetalle") || strTablaDet.equals("tPagoPenasConvDetalle")) ? 0.00 : rs2.getDouble("mImporteBruto");
                            double mImporteIva = (strTablaDet.equals("tOperAjenasDetalle") || strTablaDet.equals("tPagoPenasConvDetalle")) ? 0.00 : rs2.getDouble("mImporteIva");
                            double mImporteMasIva = (strTablaDet.equals("tOperAjenasDetalle")) ? 0.00 : rs2.getDouble("mImporteMasIva");
                            double mSancion = (strTablaDet.equals("tOperAjenasDetalle") || strTablaDet.equals("tPagoPenasConvDetalle")) ? 0.00 : rs2.getDouble("mSancion");
                            double mDevolucion = (strTablaDet.equals("tOperAjenasDetalle") || strTablaDet.equals("tPagoPenasConvDetalle")) ? 0.00 : rs2.getDouble("mDevolucion");
                            double mImporteAmortiza = (strTablaDet.equals("tOperAjenasDetalle") || strTablaDet.equals("tPagoPenasConvDetalle")) ? 0.00 : rs2.getDouble("mImporteAmortiza");
                            double mRetencion = (strTablaDet.equals("tOperAjenasDetalle") || strTablaDet.equals("tPagoPenasConvDetalle")) ? 0.00 : rs2.getDouble("mRetencion");
                            double mPenalizacion = strTablaDet.equals("tOperAjenasDetalle") ? 0.00 : rs2.getDouble("mPenalizacion");
                            double m2Millar = (strTablaDet.equals("tOperAjenasDetalle") || strTablaDet.equals("tPagoPenasConvDetalle")) ? 0.00 : rs2.getDouble("m2Millar");
                            double m23IVA = (strTablaDet.equals("tOperAjenasDetalle") || strTablaDet.equals("tPagoPenasConvDetalle")) ? 0.00 : rs2.getDouble("m23IVA");
                            double mImporteIvaProv = (strTablaDet.equals("tOperAjenasDetalle") || strTablaDet.equals("tPagoPenasConvDetalle")) ? 0.00 : rs2.getDouble("mImporteIvaProv");
                            double mImporteObra = (strTablaDet.equals("tOperAjenasDetalle") || strTablaDet.equals("tPagoPenasConvDetalle")) ? 0.00 : rs2.getDouble("mImporteObra");
                            int nPoliza = (strTablaDet.equals("tOperAjenasDetalle") || strTablaDet.equals("tPagoPenasConvDetalle")) ? 0 : rs2.getInt("nPoliza");
                            String[] cEventoCa = rs2.getString("cEvento").split("_");
                            String cEvento = "P";
                            String cPasivo = "";
                            String ur = rs2.getString("cUnidadResponsable");
                            if ("RELACIONGASTOS".equals(strTipoPago)) {
                                cPasivo = rs2.getString("cPasivo");
                            }
                            for (int i = 1; i < cEventoCa.length; i++) {
                                cEvento += "_";
                                cEvento += cEventoCa[i];
                            }
                            // cEvento = "E_"+cEventoCa[1];
                            String cCentroContable = rs2.getString("cCentroContable");
                            String Rfc = rs2.getString("RFC");
                            String alm = (strTablaDet.equals("tOperAjenasDetalle") || strTablaDet.equals("tPagoPenasConvDetalle")) ? " " : rs2.getString("ALM");
                            String nCapitulo = (strTablaDet.equals("tOperAjenasDetalle") || strTablaDet.equals("tPagoPenasConvDetalle")) ? " " : rs2.getString("nCapitulo");
                            String altaAlmacen = "0";
                            String cEventoPagado = (strTablaDet.equals("tOperAjenasDetalle")) ? "P_AJENA_IP" : cEvento;
                            if (strTipoPago.equals("AJENAS")) {
                                pstmRadicado = conn.prepareStatement("SELECT cRadicado FROM tOperAjenasEncabezado WITH(NOLOCK) WHERE caNoContrarrecibo = ?");
                                pstmRadicado.setString(1, strCXP);
                                rsRadicado = pstmRadicado.executeQuery();
                                pstmSAIAlterno = conn.prepareStatement("SELECT GP_VALOR FROM CG_GRUPO_PROPIEDADES WHERE GP_NOMBRE = 'SAI_AMBIENTAL'");
                                rsSAIAlterno = pstmSAIAlterno.executeQuery();
                                String strSAIAlterno = null;
                                if (rsSAIAlterno.next())
                                    strSAIAlterno = rsSAIAlterno.getString("GP_VALOR");
                                if (strSAIAlterno.equals("true")) {
                                    cEventoPagado = cEvento;
                                } else {
                                    cEventoPagado = (strTablaDet.equals("tOperAjenasDetalle")) ? "P_AJENA_IP" : cEvento;
                                }
                                if (rsRadicado.next())
                                    strRadicado = rsRadicado.getString("cRadicado");
                                if (strRadicado.equals("S"))
                                    cEventoPagado = "P_AJENA_RAD";
                                if (anioAplicacion != intaEjercicioFiscal && !strSAIAlterno.equals("true")) {
                                    cEventoPagado = "P_FA_AJENA_IP";
                                }
                            }
                            if (!strTablaDet.equals("tNOMINADetalle") && !strTablaDet.equals("tOperAjenasDetalle") && !strTablaDet.equals("tPagoPenasConvDetalle")) {
                                altaAlmacen = rs2.getString("altaAlmacen");
                            }
                            if (ejercidoInsertado == 0) {
                                // Guarda Detalle Ejercido
                                pstmntInsertDet = conn.prepareStatement("INSERT INTO tEjercidoDetalle (cTipoPago,nFolioPAGO,nDocRenglon,cMes,cEjercicio,cIdEntidadContable,cIdRelacion,EP,cIdCuentaContable,mComprometido,nPoliza,ID_TIPO_MOVIMIENTO,ID_TIPO_CONCEPTO,cEvento,nMes,cCentroContable,RFC, mImporteNeto,ALM,mImporteBruto,mImporteMasIva,mImporteIva,nCapitulo,mSancion,mDevolucion,mImporteAmortiza,mRetencion,mPenalizacion,m2Millar,m23IVA,mISRHonorarios,mObra5,mImporteFlete4,mISRArrenda,mRetImpuestoCedular,mImporteIvaArrenda,mImporteIvaHonorarios ,mImporteFlete23,mImporteIvaProv ,mImporteObra,mCNIC,mTesofe,altaAlmacen,aEjercicioFiscal,mIMDT,mImporte,nFolioEjercido,mImporteISRLaudos,mISROtros, mImporteIva6, mimporteISRResico, cUnidadResponsable)" + " VALUES('" + strLetratipoPago + "'," + nFolioPago + "," + nDocRenglon + "," + cMes + ",'" + strcEjercicio + "','" + cIdEntidadContable + "','" + cIdRelacion + "','" + EP + "','" + cIdCuentaContable + "'," + mComprometido + "," + nPoliza + ",'" + idTipoMovimiento + "','" + idTipoConcepto + "','EJERCIDO','" + cMes + "','" + cCentroContable + "','" + Rfc + "'," + rs2.getString("mImporteNeto") + ",'" + alm + "'," + mImporteBruto + "," + mImporteMasIva + "," + mImporteIva + ",'" + nCapitulo + "'," + mSancion + "," + mDevolucion + "," + mImporteAmortiza + " ," + mRetencion + "," + mPenalizacion + "," + m2Millar + "," + m23IVA + "," + rs2.getString("mISRHonorarios") + "," + rs2.getString("mObra5") + "       ," + rs2.getString("mImporteFlete4") + "," + rs2.getString("mISRArrenda") + "," + rs2.getString("mRetImpuestoCedular") + "," + rs2.getString("mImporteIvaArrenda") + "," + rs2.getString("mImporteIvaHonorarios") + "	," + rs2.getString("mImporteFlete23") + "," + mImporteIvaProv + "," + mImporteObra + "," + rs2.getString("mCNIC") + "," + rs2.getString("mTesofe") + ",'" + altaAlmacen + "','" + intaEjercicioFiscal + "'," + rs2.getString("mIMDT") + "," + rs2.getString("mImporteNeto") + "," + strFolioEjercido + "," + rs2.getString("mImporteISRLaudos") + "," + rs2.getString("mISROtros") + "," + rs2.getString("mImporteIva6") + "," + rs2.getString("mimporteISRResico") + ",'" + ur + "' )");
                                intDet = pstmntInsertDet.executeUpdate();
                                log.debug("Object: " + String.valueOf("Se insertaron en tEjercidoDetalle: " + intDet));
                            }
                            if (pagadoInsertado == 0) {
                                // Guarda Detalle Pagado
                                BigDecimal montoPasivoDiferdio = rs2.getBigDecimal("montoPasivoDiferdio");
                                String sqlinsetP = "INSERT INTO tPagadoDetalle (cTipoPago,nFolioPAGO,nDocRenglon,cMes,cEjercicio,cIdEntidadContable,cIdRelacion,EP,cIdCuentaContable,mComprometido,nPoliza,ID_TIPO_MOVIMIENTO,ID_TIPO_CONCEPTO,cEvento,nMes,cCentroContable,RFC, mImporteNeto,ALM,mImporteBruto,mImporteMasIva,mImporteIva,nCapitulo,mSancion,mDevolucion,mImporteAmortiza,mRetencion,mPenalizacion,m2Millar,m23IVA,mISRHonorarios,mObra5    ,mImporteFlete4,mISRArrenda,mRetImpuestoCedular,mImporteIvaArrenda,mImporteIvaHonorarios ,mImporteFlete23,mImporteIvaProv ,mImporteObra,mCNIC,mTesofe,altaAlmacen,aEjercicioFiscal,mIMDT,mImporte,nFolioPagado,OBGT,ctab,mImporteISRLaudos,mISROtros, mPasivoDiferido, mImporteIva6, cPasivo, mimporteISRResico, cUnidadResponsable)" + "VALUES('" + strLetratipoPago + "'," + nFolioPago + "," + nDocRenglon + "," + cMes + ",'" + strcEjercicio + "','" + cIdEntidadContable + "','" + cIdRelacion + "','" + EP + "','" + cIdCuentaContable + "'," + mComprometido + "," + nPoliza + ",'" + idTipoMovimiento + "','" + idTipoConcepto + "','" + cEventoPagado + "','" + cMes + "','" + cCentroContable + "','" + Rfc + "'," + rs2.getString("mImporteNeto") + ",'" + alm + "'," + mImporteBruto + "," + mImporteMasIva + "," + mImporteIva + ",'" + nCapitulo + "'," + mSancion + "," + mDevolucion + "," + mImporteAmortiza + " ," + mRetencion + "," + mPenalizacion + "," + m2Millar + "," + m23IVA + "," + rs2.getString("mISRHonorarios") + "," + rs2.getString("mObra5") + "       ," + rs2.getString("mImporteFlete4") + "," + rs2.getString("mISRArrenda") + "," + rs2.getString("mRetImpuestoCedular") + "," + rs2.getString("mImporteIvaArrenda") + "," + rs2.getString("mImporteIvaHonorarios") + "	," + rs2.getString("mImporteFlete23") + "," + mImporteIvaProv + "," + mImporteObra + "," + rs2.getString("mCNIC") + "," + rs2.getString("mTesofe") + ",'" + altaAlmacen + "','" + intaEjercicioFiscal + "'," + rs2.getString("mIMDT") + "," + rs2.getString("mImporteNeto") + "," + strFolioPagado + ",'" + cOBGT + "','" + strCTAB + "'," + rs2.getString("mImporteISRLaudos") + "," + rs2.getString("mISROtros") + ",0 ," + rs2.getString("mImporteIva6") + ", '" + cPasivo + "'," + rs2.getString("mimporteISRResico") + ", '" + ur + "' )";
                                if (montoPasivoDiferdio.compareTo(new BigDecimal(0.0f)) > 0) {
                                    nDocRenglon++;
                                    sqlinsetP = sqlinsetP + ";" + "INSERT INTO tPagadoDetalle (cTipoPago,nFolioPAGO,nDocRenglon,cMes,cEjercicio,cIdEntidadContable,cIdRelacion,EP,cIdCuentaContable,mComprometido,nPoliza,ID_TIPO_MOVIMIENTO,ID_TIPO_CONCEPTO,cEvento,nMes,cCentroContable,RFC, mImporteNeto,ALM,mImporteBruto,mImporteMasIva,mImporteIva,nCapitulo,mSancion,mDevolucion,mImporteAmortiza,mRetencion,mPenalizacion,m2Millar,m23IVA,mISRHonorarios,mObra5    ,mImporteFlete4,mISRArrenda,mRetImpuestoCedular,mImporteIvaArrenda,mImporteIvaHonorarios ,mImporteFlete23,mImporteIvaProv ,mImporteObra,mCNIC,mTesofe,altaAlmacen,aEjercicioFiscal,mIMDT,mImporte,nFolioPagado,OBGT,ctab,mImporteISRLaudos,mISROtros, mPasivoDiferido, mImporteIva6, mimporteISRResico, cUnidadResponsable)" + "VALUES('" + strLetratipoPago + "'," + nFolioPago + "," + nDocRenglon + "," + cMes + ",'" + strcEjercicio + "','" + cIdEntidadContable + "','" + cIdRelacion + "','" + EP + "','" + cIdCuentaContable + "'," + mComprometido + "," + nPoliza + ",'" + idTipoMovimiento + "','" + idTipoConcepto + "','" + cEventoPagado + "','" + cMes + "','" + cCentroContable + "','TESOFE'," + "0,'" + alm + "',0,0,0,'" + nCapitulo + "',0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,'" + altaAlmacen + "','" + intaEjercicioFiscal + "',0,0," + strFolioPagado + ",'" + cOBGT + "','" + strCTAB + "',0,0," + montoPasivoDiferdio + ",0, 0, '" + ur + "' )";
                                    renglonPasivoDiFerido++;
                                }
                                if (!"".equals(cPasivo) && "P_DDNORE01".equals(cEventoPagado)) {
                                    nDocRenglon = nDocRenglon + 1;
                                    sqlinsetP = sqlinsetP + ";" + "INSERT INTO tPagadoDetalle (cTipoPago,nFolioPAGO,nDocRenglon,cMes,cEjercicio,cIdEntidadContable,cIdRelacion,EP,cIdCuentaContable,mComprometido,nPoliza,ID_TIPO_MOVIMIENTO,ID_TIPO_CONCEPTO,cEvento,nMes,cCentroContable,RFC, mImporteNeto,ALM,mImporteBruto,mImporteMasIva,mImporteIva,nCapitulo,mSancion,mDevolucion,mImporteAmortiza,mRetencion,mPenalizacion,m2Millar,m23IVA,mISRHonorarios,mObra5    ,mImporteFlete4,mISRArrenda,mRetImpuestoCedular,mImporteIvaArrenda,mImporteIvaHonorarios ,mImporteFlete23,mImporteIvaProv ,mImporteObra,mTesofe,altaAlmacen,aEjercicioFiscal,mIMDT,mImporte,nFolioPagado,OBGT,ctab,mImporteISRLaudos,mISROtros, mPasivoDiferido, mImporteIva6, mimporteISRResico, cUnidadResponsable,mCNIC) VALUES('" + strLetratipoPago + "'," + nFolioPago + "," + nDocRenglon + "," + cMes + ",'" + strcEjercicio + "','" + cIdEntidadContable + "','" + cIdRelacion + "','" + EP + "','" + cIdCuentaContable + "',0.00,0.00,'','','" + cEventoPagado + "','" + cMes + "','" + cCentroContable + "','LAUD000000000'," + "0,'',0,0,0,'" + nCapitulo + "',0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,'','" + intaEjercicioFiscal + "',0,0," + strFolioPagado + ",'" + cOBGT + "','" + CTABLaudo + "',0,0,0,0, 0, '" + ur + "'," + rs2.getString("mImporteNeto") + " )";
                                }
                                if (!"".equals(cPasivo) && "S".equals(cEsPagoCuotas)) {
                                    nDocRenglon = nDocRenglon + 1;
                                    sqlinsetP = sqlinsetP + ";" + "INSERT INTO tPagadoDetalle (cTipoPago,nFolioPAGO,nDocRenglon,cMes,cEjercicio,cIdEntidadContable,cIdRelacion,EP,cIdCuentaContable,mComprometido,nPoliza,ID_TIPO_MOVIMIENTO,ID_TIPO_CONCEPTO,cEvento,nMes,cCentroContable,RFC, mImporteNeto,ALM,mImporteBruto,mImporteMasIva,mImporteIva,nCapitulo,mSancion,mDevolucion,mImporteAmortiza,mRetencion,mPenalizacion,m2Millar,m23IVA,mISRHonorarios,mObra5    ,mImporteFlete4,mISRArrenda,mRetImpuestoCedular,mImporteIvaArrenda,mImporteIvaHonorarios ,mImporteFlete23,mImporteIvaProv ,mImporteObra,mTesofe,altaAlmacen,aEjercicioFiscal,mIMDT,mImporte,nFolioPagado,OBGT,ctab,mImporteISRLaudos,mISROtros, mPasivoDiferido, mImporteIva6, mimporteISRResico, cUnidadResponsable, mCNIC, cPasivo) VALUES('" + strLetratipoPago + "'," + nFolioPago + "," + nDocRenglon + "," + cMes + ",'" + strcEjercicio + "','" + cIdEntidadContable + "','" + cIdRelacion + "','" + EP + "','" + cIdCuentaContable + "',0.00,0.00,'','','" + cEventoPagado + "','" + cMes + "','" + cCentroContable + "','" + RFC_PC + "'," + "0,''," + mImporteBruto + ",0,0,'" + nCapitulo + "',0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,'','" + intaEjercicioFiscal + "',0,0," + strFolioPagado + ",'" + cOBGT + "','" + CTABLaudo + "',0,0,0,0, 0, '" + ur + "',0,'" + PC + "' )";
                                }
                                pstmntInsertDetPag = conn.prepareStatement(sqlinsetP);
                                intDetPag = pstmntInsertDetPag.executeUpdate();
                                log.debug("Object: " + String.valueOf("Se insertaron en tPagadoDetalle: " + intDetPag));
                            }
                        }
                        if ("S".equals(cEsPagoCuotas)) {
                            psUpdateRFCCuotas = conn.prepareStatement("UPDATE tPagadoDetalle SET mImporteBruto = 0, CTAB = ? WHERE nFolioPAGO = ? AND nFolioPagado = ? AND RFC = ?");
                            psUpdateRFCCuotas.setString(1, CTABLaudo);
                            psUpdateRFCCuotas.setInt(2, nFolioPago);
                            psUpdateRFCCuotas.setString(3, strFolioPagado);
                            psUpdateRFCCuotas.setString(4, cRFCCuotas);
                            psUpdateRFCCuotas.executeUpdate();
                        }
                    } else {
                        log.debug("No Guarda Encabezado");
                    }
                    // APLICACION CONTABLE
                    if (("").equals(ejercidoAplicado) || ejercidoAplicado == null)
                        accEng.makeAccountingApplication(conn, "EJERCIDO", strFolioEjercido, "tEjercidoEncabezado", "tEjercidoDetalle", "nFolioEjercido");
                    if (("").equals(pagadoAplicado) || pagadoAplicado == null || intDetPag > 0)
                        accEng.makeAccountingApplication(conn, "PAGADO", strFolioPagado, "tPagadoEncabezado", "tPagadoDetalle", "nFolioPagado");
                    conn.commit();
                    valor = "ingresado";
                }
            }
        } catch (Exception e) {
            valor = e.toString();
            log.warn(e.getMessage(), e);
            log.debug("Object: " + String.valueOf(e.getLocalizedMessage()));
            try {
                conn.rollback();
            } catch (Exception ee) {
                log.warn("Error: cerrando rollback ", ee);
            }
        } finally {
            try {
                if (!valor.equals("ingresado")) {
                    conn.rollback();
                }
                CloseObject.closeObject(rs);
                CloseObject.closeObject(rs2);
                CloseObject.closeObject(rsRadicado);
                CloseObject.closeObject(rsbe);
                CloseObject.closeObject(rsbp);
                CloseObject.closeObject(pstmtnSelect);
                CloseObject.closeObject(pstmntInsertEj);
                CloseObject.closeObject(pstmnSelectDet);
                CloseObject.closeObject(pstmntInsertDet);
                CloseObject.closeObject(pstmntInsertPag);
                CloseObject.closeObject(pstmntInsertDetPag);
                CloseObject.closeObject(pstmRadicado);
                CloseObject.closeObject(upd_fAplicacion);
                CloseObject.closeObject(pstmUpEjercido);
                CloseObject.closeObject(pstmSeqEjercido);
                CloseObject.closeObject(pstmUpPagado);
                CloseObject.closeObject(pstmBuscaEjercido);
                CloseObject.closeObject(pstmBuscaPagado);
                CloseObject.closeObject(conn);
            } catch (Exception e) {
                log.warn("Error: cerrando statement", e);
            }
        }
        return valor;
    }

    public String aplicar(String strCampos, String strCamposDet, String strTabla, String strTablaDet, String strCXP, String strCLC, String nomCXP, String tipoPago, String strNomDetFolio, String strLetratipoP, String fPago, String usuario, String strFolioEjercido, String strFolioPagado, String strNomCamposDetValidar, String strStatusSiaff) throws SQLException {
        String valor = null;
        PreparedStatement pstmtnSelect = null, pstmntInsertEj = null, pstmnSelectDet = null, pstmntInsertDet = null, pstmntInsertPag = null, pstmntInsertDetPag = null, pstmtnSicop = null, pstmntEncCXP = null, pstmntDetCXP = null, pstmntDetSICOP = null, pstmntSelectDet = null, pstmCent = null;
        ResultSet rs = null, rs2 = null, rs3 = null, rs4 = null, rs5 = null, rs6 = null, rs7 = null, rs8 = null, rs9 = null, rs10 = null, rs11 = null, rs12 = null, rs13 = null;
        Connection conn = null;
        valor = "ingresado";
        String idDet = null;
        String aEjercicioFiscal = "";
        String epDetSICOP = "";
        double imporDetSICOP = 0;
        // Ajuste
        String cCentroContable = "";
        // Ajuste
        String cEvento = "";
        // Ajuste
        String RFC = "";
        // Ajuste
        int nMes = 0;
        // Ajuste
        int cEjercicio = 0;
        String idRelacion = "";
        String camposDetValidar = "";
        double numeMenor;
        double numeMayor;
        double numeMenorDet;
        double numeMayorDet;
        double centavos = 0.00;
        try {
            // Se Validan los Detalles Para identificar el que tenga diferencia
            // de 0.01
            conn = getConnection();
            pstmntEncCXP = conn.prepareStatement("SELECT " + strCampos + " FROM " + strTabla + " WITH(NOLOCK) WHERE " + nomCXP + " = ?");
            pstmntEncCXP.setString(1, strCXP);
            rs8 = pstmntEncCXP.executeQuery();
            if (rs8.next()) {
                idDet = rs8.getString("nFolio");
                // Ajuste
                aEjercicioFiscal = rs8.getString("aEjercicioFiscal");
                if (aEjercicioFiscal.equals("2012")) {
                    numeMenor = Double.parseDouble(rs8.getString("impNeto")) - 1.00d;
                    numeMayor = Double.parseDouble(rs8.getString("impNeto")) + 1.00d;
                } else {
                    pstmCent = conn.prepareStatement("SELECT TOP 1 diferenciaCentavos FROM tEjercidoPagadoCentavo WITH(NOLOCK) WHERE Activo = 1");
                    rs13 = pstmCent.executeQuery();
                    if (rs13.next()) {
                        centavos = rs13.getDouble("diferenciaCentavos");
                    }
                    numeMenor = Double.parseDouble(rs8.getString("impNeto")) - centavos;
                    numeMayor = Double.parseDouble(rs8.getString("impNeto")) + centavos;
                }
            }
            if (strTablaDet.equals("tOperAjenasDetalle")) {
                camposDetValidar = "SELECT " + strNomCamposDetValidar + " FROM " + strTablaDet + " WITH(NOLOCK) WHERE cEvento != 'ANTICIPO' AND cEvento != 'ANTICIPO_DIV' AND " + strNomDetFolio + " = ? GROUP BY SUBSTRING(EP, 0,56) , cCentroContable,RFC,aEjercicioFiscal,cEvento,cMes";
            } else {
                idRelacion = (strTablaDet.equals("tPagoDirectoDetalle")) ? "cIdDocumento" : "cIdRelacion";
                camposDetValidar = "SELECT " + strNomCamposDetValidar + " FROM " + strTablaDet + " WITH(NOLOCK) WHERE cEvento != 'ANTICIPO' AND cEvento != 'ANTICIPO_DIV' AND " + strNomDetFolio + " = ? GROUP BY SUBSTRING(EP, 0,56) , cCentroContable,RFC,cEjercicio,cIdCuentaContable," + idRelacion + ",nCapitulo,cEvento,cMes";
            }
            pstmntDetCXP = conn.prepareStatement(camposDetValidar);
            pstmntDetCXP.setString(1, idDet);
            rs10 = pstmntDetCXP.executeQuery();
            while (rs10.next()) {
                // CXP
                epDetSICOP = rs10.getString("EP");
                imporDetSICOP = rs10.getDouble("mImporteNeto");
                // Para ingresar en tEjercidoDetalle (Ajuste)
                cCentroContable = rs10.getString("cCentroContable");
                cEvento = rs10.getString("cEvento");
                RFC = rs10.getString("RFC");
                cEjercicio = Integer.parseInt(rs10.getString("cEjercicio"), 10);
                nMes = rs10.getInt("cMes");
                if (aEjercicioFiscal.equals("2012")) {
                    numeMenorDet = rs10.getDouble("mImporteNeto") - 1.00d;
                    numeMayorDet = rs10.getDouble("mImporteNeto") + 1.00d;
                } else {
                    numeMenorDet = rs10.getDouble("mImporteNeto") - centavos;
                    numeMayorDet = rs10.getDouble("mImporteNeto") + centavos;
                }
                NumberFormat formatter = new DecimalFormat("###.##");
                double numeMenorForDet = Double.parseDouble(formatter.format(numeMenorDet));
                double numeMayorForDet = Double.parseDouble(formatter.format(numeMayorDet));
                // Detalles de SICOP
                int renglonn = 1;
                // Validacion con un centavo mas o menos.
                String sql = (// +'.'+SUBSTRING(CCAU_162,8,3)+'.'+"
                // +'.'+SUBSTRING(CCAU_162,8,3)+'.'+"
                // ,CCAU_162,CCOP_163"
                // ,CCAU_162,CCOP_163"
                "SELECT CANI_150+'.'+ID_RAMO_CR+'.'+ID_UNIDAD_CR+'.'+CGFU_151+'.'+CFUN_152+'.'+" + " CASE LEN(CSFU_153) WHEN 1 THEN '0'+CSFU_153 END+'.'+" + " CASE LEN(CPRG_154) WHEN 1 THEN '0'+CPRG_154 END +'.'+" + " CASE LEN(CAIN_155) WHEN 1 THEN '00'+CAIN_155 WHEN 2 THEN '0'+CAIN_155 END+'.'+CPPT_156+'.'+COBG_183+'.'+CTGA_160+'.'+CFIN_161+'.'+" + " CASE LEN(CGEO_164) WHEN 1 THEN '0'+CGEO_164 WHEN 2 THEN CGEO_164 END+'.'+" + " CASE CPPI_166 WHEN '0' THEN '00000000000' ELSE CPPI_166 END " + " AS EP," + " SUM(CONVERT(money,IMPORTE_148)) AS IMPORTE_148 " + " FROM CLC_SICOP WITH(NOLOCK) " + " WHERE NCLC_43 = '" + strCLC + "' AND " + " CPPT_156 = SUBSTRING('" + epDetSICOP + "',27,4) AND COBG_183 = SUBSTRING('" + epDetSICOP + "',32,5) AND " + " CASE LEN(CGEO_164) WHEN 1 THEN '0'+CGEO_164 WHEN 2 THEN CGEO_164 END = SUBSTRING('" + epDetSICOP + "',42,2) AND " + " CGFU_151 = SUBSTRING('" + epDetSICOP + "',13,1) AND " + " CASE LEN(CSFU_153) WHEN 1 THEN '0'+CSFU_153 END = SUBSTRING('" + epDetSICOP + "',17,2) AND " + " CASE LEN(CPRG_154) WHEN 1 THEN '0'+CPRG_154 END = SUBSTRING('" + epDetSICOP + "',20,2) AND " + " CFUN_152 = SUBSTRING('" + epDetSICOP + "',15,1) AND " + " CASE CPPI_166 WHEN '0' THEN '00000000000' ELSE CPPI_166 END = SUBSTRING('" + epDetSICOP + "',45,11) AND " + " (" + " SELECT CANI_150+'.'+ID_RAMO_CR+'.'+ID_UNIDAD_CR+'.'+CGFU_151+'.'+CFUN_152+'.'+" + " CASE LEN(CSFU_153) WHEN 1 THEN '0'+CSFU_153 END+'.'+" + " CASE LEN(CPRG_154) WHEN 1 THEN '0'+CPRG_154 END +'.'+" + " CASE LEN(CAIN_155) WHEN 1 THEN '00'+CAIN_155 WHEN 2 THEN '0'+CAIN_155 END+'.'+" + " CPPT_156+'.'+COBG_183+'.'+CTGA_160+'.'+CFIN_161+'.'+" + " CASE LEN(CGEO_164) WHEN 1 THEN '0'+CGEO_164 WHEN 2 THEN CGEO_164 END+'.'+" + " CASE CPPI_166 WHEN '0' THEN '00000000000' ELSE CPPI_166 END " + " AS EP" + " FROM CLC_SICOP WITH(NOLOCK) " + " WHERE NCLC_43 = '" + strCLC + "' AND " + " CPPT_156 = SUBSTRING('" + epDetSICOP + "',27,4) AND COBG_183 = SUBSTRING('" + epDetSICOP + "',32,5) AND " + " CASE LEN(CGEO_164) WHEN 1 THEN '0'+CGEO_164 WHEN 2 THEN CGEO_164 END = SUBSTRING('" + epDetSICOP + "',42,2) AND " + " CGFU_151 = SUBSTRING('" + epDetSICOP + "',13,1) AND " + " CASE LEN(CSFU_153) WHEN 1 THEN '0'+CSFU_153 END = SUBSTRING('" + epDetSICOP + "',17,2) AND " + " CASE LEN(CPRG_154) WHEN 1 THEN '0'+CPRG_154 END = SUBSTRING('" + epDetSICOP + "',20,2) AND " + " CFUN_152 = SUBSTRING('" + epDetSICOP + "',15,1) AND " + " CASE CPPI_166 WHEN '0' THEN '00000000000' ELSE CPPI_166 END = SUBSTRING('" + epDetSICOP + "',45,11) " + " GROUP BY CANI_150,ID_RAMO_CR,ID_UNIDAD_CR,CGFU_151,CFUN_152,CSFU_153,CPRG_154,CAIN_155,CPPT_156,COBG_183,CTGA_160,CFIN_161,CGEO_164,CPPI_166" + " HAVING(SUM(CONVERT(money,IMPORTE_148))) BETWEEN " + numeMenorForDet + " AND " + numeMayorForDet + ") = '" + epDetSICOP + "'" + " GROUP BY CANI_150,ID_RAMO_CR,ID_UNIDAD_CR,CGFU_151,CFUN_152,CSFU_153,CPRG_154,CAIN_155,CPPT_156,COBG_183,CTGA_160,CFIN_161,CGEO_164,CPPI_166" + " HAVING(SUM(CONVERT(money,IMPORTE_148))) BETWEEN " + numeMenorForDet + " AND " + numeMayorForDet);
                pstmntDetSICOP = conn.prepareStatement(sql);
                rs11 = pstmntDetSICOP.executeQuery();
                if (rs11.next()) {
                    // Si no Existe lo agrega para hacer el Ajuste
                    if (imporDetSICOP != rs11.getDouble("IMPORTE_148")) {
                        pstmntSelectDet = conn.prepareStatement("SELECT * FROM tEjercidoDetalle WITH(NOLOCK) WHERE EP = ? AND nFolioEjercido = ? ");
                        pstmntSelectDet.setString(1, epDetSICOP);
                        pstmntSelectDet.setInt(2, Integer.parseInt(strFolioEjercido, 10));
                        rs12 = pstmntSelectDet.executeQuery();
                    }
                } else {
                    renglonn = renglonn + 1;
                }
            }
            // Aplicacion Contable
            String fPagado = null;
            if (fPago.length() > 1) {
                String[] fechPagado = fPago.split("/");
                fPagado = "'" + fechPagado[2] + "-" + fechPagado[1] + "-" + fechPagado[0] + "'";
                // movFecha
            }
            System.out.println("fPagado: " + fPagado);
            String fechaAplicadoSicop = "";
            String fechaPagoSicop = "";
            String solicitudPago = "";
            String numProceso = "";
            String folioSiaff = "";
            String DATE_FORMAT = "yyyy-MM-dd";
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            // today
            Calendar c1 = Calendar.getInstance();
            String FechaPagado = sdf.format(c1.getTime());
            System.out.println("Fecha_today: " + FechaPagado);
            // Buscar Informacion SICOP
            pstmtnSicop = conn.prepareStatement("SELECT FECHA_APL,FECHA_PAGO_106,SPAG_176,PROC_CLAVE,FOLIO_SIAFF_112 FROM CLC_SICOP WITH(NOLOCK) WHERE NCLC_43 = ? GROUP BY FECHA_APL,FECHA_PAGO_106,SPAG_176,PROC_CLAVE,FOLIO_SIAFF_112");
            pstmtnSicop.setString(1, strCLC);
            rs7 = pstmtnSicop.executeQuery();
            if (rs7.next()) {
                String[] fAS = rs7.getString("FECHA_APL").split("/");
                fechaAplicadoSicop = fAS[2] + "-" + fAS[1] + "-" + fAS[0];
                String[] fPS = rs7.getString("FECHA_PAGO_106").split("/");
                fechaPagoSicop = fPS[2] + "-" + fPS[1] + "-" + fPS[0];
                solicitudPago = rs7.getString("SPAG_176");
                numProceso = rs7.getString("PROC_CLAVE");
                folioSiaff = rs7.getString("FOLIO_SIAFF_112");
            }
            // Buscar Informacion Cabecera
            pstmtnSelect = conn.prepareStatement("SELECT " + strCampos + " FROM " + strTabla + " WITH(NOLOCK) WHERE " + nomCXP + "= ? ");
            pstmtnSelect.setString(1, strCXP);
            rs = pstmtnSelect.executeQuery();
            if (rs.next()) {
                // String fAplicacion = rs.getString( "fAplicacion" );
                String recibo = (strTabla.equals("tNOMINACLCEncabezado")) ? rs.getString("caNoContrarreciboCLC") : rs.getString("caNoContrarrecibo");
                String tipoPoliza = rs.getString("cTipoPoliza");
                String fCancelacion = rs.getString("fCancelacion");
                String desPoliza = rs.getString("cDescripcionPoliza");
                String uniResp = rs.getString("cUnidadResponsableContable");
                String cRamo = rs.getString("cRamo");
                int nFolioPago = Integer.parseInt(rs.getString("nFolio"), 10);
                int nFolPolCancelacion = Integer.parseInt(rs.getString("nFolioPolizaCancelacion"), 10);
                int intaEjercicioFiscal = Integer.parseInt(rs.getString("aEjercicioFiscal"), 10);
                if (fCancelacion != null) {
                    fCancelacion = "'" + fCancelacion + "'";
                } else {
                    fCancelacion = null;
                }
                // Guarda En Cabecera Ejercido
                // Para Guardar 0 y Generar el Nuevo Folio.
                int folioPoliza = 0;
                // String encE = "INSERT INTO tEjercidoEncabezado
                // (cTipoPago,nFolioPAGO,caNoContrarrecibo,nFolioPoliza,cTipoPoliza,U_LOGIN,nFolioPolizaCancelacion,fCancelacion,cDescripcionPoliza,cUnidadResponsableContable,nFolioSICOP,fAplicacion,cRamo,cIdUsuarioCaptura,FechaAplicacionSicop,FechaPagoSicop,SolicitudPago,NumeroProceso,nFolioSIAFF,nFolioEjercido)
                // " + "VALUES ('" + strLetratipoP + "'," + nFolioPago + ",'" +
                // recibo + "'," + folioPoliza + ",'" + tipoPoliza + "','" +
                // usuario + "'," + nFolPolCancelacion + "," + fCancelacion +
                // ",'" + desPoliza + "','" + uniResp + "'," + strCLC + ",'" +
                // fechaAplicadoSicop + "','" + cRamo + "','" + usuario + "','"
                // + fechaAplicadoSicop + "','" + fechaPagoSicop + "'," +
                // solicitudPago + "," + numProceso + "," + folioSiaff + "," +
                // strFolioEjercido + " )";
                pstmntInsertEj = conn.prepareStatement("INSERT INTO tEjercidoEncabezado (cTipoPago,nFolioPAGO,caNoContrarrecibo,nFolioPoliza,cTipoPoliza,U_LOGIN,nFolioPolizaCancelacion,fCancelacion,cDescripcionPoliza,cUnidadResponsableContable,nFolioSICOP,fAplicacion,cRamo,cIdUsuarioCaptura,FechaAplicacionSicop,FechaPagoSicop,SolicitudPago,NumeroProceso,nFolioSIAFF,nFolioEjercido) " + "VALUES ('" + strLetratipoP + "'," + nFolioPago + ",'" + recibo + "'," + folioPoliza + ",'DI','" + usuario + "'," + nFolPolCancelacion + "," + fCancelacion + ",'" + desPoliza + "','" + uniResp + "'," + strCLC + ",'" + fechaAplicadoSicop + "','" + cRamo + "','" + usuario + "','" + fechaAplicadoSicop + "','" + fechaPagoSicop + "'," + solicitudPago + "," + numProceso + "," + folioSiaff + "," + strFolioEjercido + " )");
                int intr = pstmntInsertEj.executeUpdate();
                // Guarda En Cabecera Pagado
                String encP = "INSERT INTO tPagadoEncabezado (cTipoPago,nFolioPAGO,caNoContrarrecibo,nFolioPoliza,cTipoPoliza,U_LOGIN,nFolioPolizaCancelacion,fCancelacion,cDescripcionPoliza,cUnidadResponsableContable,nFolioSICOP,fAplicacion,cRamo,cIdUsuarioCaptura,FechaAplicacionSicop,FechaPagoSicop,SolicitudPago,NumeroProceso,nFolioSIAFF,FechaPagado,nFolioPagado) " + "VALUES ('" + strLetratipoP + "'," + nFolioPago + ",'" + recibo + "'," + folioPoliza + ",'" + tipoPoliza + "','" + usuario + "'," + nFolPolCancelacion + "," + fCancelacion + ",'" + desPoliza + "','" + uniResp + "'," + strCLC + "," + fPagado + ",'" + cRamo + "','" + usuario + "','" + fechaAplicadoSicop + "','" + fechaPagoSicop + "'," + solicitudPago + "," + numProceso + "," + folioSiaff + ",'" + FechaPagado + "'," + strFolioPagado + " )";
                pstmntInsertPag = conn.prepareStatement(encP);
                int intrPag = pstmntInsertPag.executeUpdate();
                if (intr > 0 && intrPag > 0) {
                    // Buscar detalles con el nFolio de Encabezado
                    String sqlDet = "SELECT " + strCamposDet + " FROM " + strTablaDet + " WITH(NOLOCK) WHERE cEvento != 'ANTICIPO' AND cEvento != 'ANTICIPO_DIV' AND " + strNomDetFolio + " = ? ";
                    pstmnSelectDet = conn.prepareStatement(sqlDet);
                    pstmnSelectDet.setInt(1, nFolioPago);
                    rs3 = pstmnSelectDet.executeQuery();
                    int nDocRenglon = 0;
                    while (rs3.next()) {
                        if (strTablaDet.equals("tPAGOFEDERALIZADODetalle")) {
                            nDocRenglon = nDocRenglon + 1;
                        } else {
                            nDocRenglon = Integer.parseInt(rs3.getString("nDocRenglon"), 10);
                        }
                        int cMes = Integer.parseInt(rs3.getString("cMes"), 10);
                        String strcEjercicio = rs3.getString("cEjercicio");
                        String EP = rs3.getString("EP");
                        String cOBGT = EP.substring(31, 36);
                        String cIdCuentaContable = (strTablaDet.equals("tOperAjenasDetalle")) ? " " : rs3.getString("cIdCuentaContable");
                        String cIdEntidadContable = rs3.getString("cIdEntidadContable");
                        String cIdRelacion = (strTablaDet.equals("tOperAjenasDetalle")) ? " " : rs3.getString("cIdRelacion");
                        String idTipoMovimiento = (strTablaDet.equals("tOperAjenasDetalle")) ? " " : rs3.getString("ID_TIPO_MOVIMIENTO");
                        String idTipoConcepto = (strTablaDet.equals("tOperAjenasDetalle")) ? " " : rs3.getString("ID_TIPO_CONCEPTO");
                        double mComprometido = (strTablaDet.equals("tOperAjenasDetalle")) ? 0.00 : rs3.getDouble("mComprometido");
                        double mImporteBruto = (strTablaDet.equals("tOperAjenasDetalle")) ? 0.00 : rs3.getDouble("mImporteBruto");
                        double mImporteIva = (strTablaDet.equals("tOperAjenasDetalle")) ? 0.00 : rs3.getDouble("mImporteIva");
                        double mImporteMasIva = (strTablaDet.equals("tOperAjenasDetalle")) ? 0.00 : rs3.getDouble("mImporteMasIva");
                        double mSancion = (strTablaDet.equals("tOperAjenasDetalle")) ? 0.00 : rs3.getDouble("mSancion");
                        double mDevolucion = (strTablaDet.equals("tOperAjenasDetalle")) ? 0.00 : rs3.getDouble("mDevolucion");
                        double mImporteAmortiza = (strTablaDet.equals("tOperAjenasDetalle")) ? 0.00 : rs3.getDouble("mImporteAmortiza");
                        double mRetencion = (strTablaDet.equals("tOperAjenasDetalle")) ? 0.00 : rs3.getDouble("mRetencion");
                        double mPenalizacion = (strTablaDet.equals("tOperAjenasDetalle")) ? 0.00 : rs3.getDouble("mPenalizacion");
                        double m2Millar = (strTablaDet.equals("tOperAjenasDetalle")) ? 0.00 : rs3.getDouble("m2Millar");
                        double m23IVA = (strTablaDet.equals("tOperAjenasDetalle")) ? 0.00 : rs3.getDouble("m23IVA");
                        double mImporteIvaHonorarios = (strTablaDet.equals("tOperAjenasDetalle")) ? 0.00 : rs3.getDouble("mImporteIvaHonorarios");
                        double mImporteIvaProv = (strTablaDet.equals("tOperAjenasDetalle")) ? 0.00 : rs3.getDouble("mImporteIvaProv");
                        double mImporteObra = (strTablaDet.equals("tOperAjenasDetalle")) ? 0.00 : rs3.getDouble("mImporteObra");
                        double mImporteIvaArrenda = (strTablaDet.equals("tOperAjenasDetalle")) ? 0.00 : rs3.getDouble("mImporteIvaArrenda");
                        int nPoliza = (strTablaDet.equals("tOperAjenasDetalle")) ? 0 : rs3.getInt("nPoliza");
                        String[] cEventoCa = rs3.getString("cEvento").split("_");
                        cEvento = "P";
                        for (int i = 1; i < cEventoCa.length; i++) {
                            cEvento += "_";
                            cEvento += cEventoCa[i];
                        }
                        cCentroContable = rs3.getString("cCentroContable");
                        String Rfc = rs3.getString("RFC");
                        String alm = (strTablaDet.equals("tOperAjenasDetalle")) ? " " : rs3.getString("ALM");
                        String nCapitulo = (strTablaDet.equals("tOperAjenasDetalle")) ? " " : rs3.getString("nCapitulo");
                        String altaAlmacen = "0";
                        String cEventoPagado = (strTablaDet.equals("tOperAjenasDetalle")) ? "P_AJENA" : cEvento;
                        if (!strTablaDet.equals("tNOMINADetalle") && !strTablaDet.equals("tOperAjenasDetalle")) {
                            altaAlmacen = rs3.getString("altaAlmacen");
                        }
                        // Guarda Detalle Ejercido
                        // ,"+rs.getString("mIMDT")+")");
                        pstmntInsertDet = conn.prepareStatement("INSERT INTO tEjercidoDetalle (cTipoPago,nFolioPAGO,nDocRenglon,cMes,cEjercicio,cIdEntidadContable,cIdRelacion,EP,cIdCuentaContable,mComprometido,nPoliza,ID_TIPO_MOVIMIENTO,ID_TIPO_CONCEPTO,cEvento,nMes,cCentroContable,RFC, mImporteNeto,ALM,mImporteBruto,mImporteMasIva,mImporteIva,nCapitulo,mSancion,mDevolucion,mImporteAmortiza,mRetencion,mPenalizacion,m2Millar,m23IVA,mISRHonorarios,mObra5,mImporteFlete4,mISRArrenda,mRetImpuestoCedular,mImporteIvaArrenda,mImporteIvaHonorarios ,mImporteFlete23,mImporteIvaProv ,mImporteObra,mCNIC,mTesofe,altaAlmacen,aEjercicioFiscal,mIMDT,mImporte,nFolioEjercido)" + " VALUES('" + strLetratipoP + "'," + nFolioPago + "," + nDocRenglon + "," + cMes + ",'" + strcEjercicio + "','" + cIdEntidadContable + "','" + cIdRelacion + "','" + EP + "','" + cIdCuentaContable + "'," + mComprometido + "," + nPoliza + ",'" + idTipoMovimiento + "','" + idTipoConcepto + "','EJERCIDO','" + cMes + "','" + cCentroContable + "','" + Rfc + "'," + rs3.getString("mImporteNeto") + ",'" + alm + "'," + mImporteBruto + "," + mImporteMasIva + "," + mImporteIva + ",'" + nCapitulo + "'," + mSancion + "," + mDevolucion + "," + mImporteAmortiza + " ," + mRetencion + "," + mPenalizacion + "," + m2Millar + "," + m23IVA + "," + rs3.getString("mISRHonorarios") + "," + rs3.getString("mObra5") + "       ," + rs3.getString("mImporteFlete4") + "," + rs3.getString("mISRArrenda") + "," + rs3.getString("mRetImpuestoCedular") + "," + mImporteIvaArrenda + "," + mImporteIvaHonorarios + "	," + rs3.getString("mImporteFlete23") + "," + mImporteIvaProv + "," + mImporteObra + "," + rs3.getString("mCNIC") + "," + rs3.getString("mTesofe") + ",'" + altaAlmacen + "','" + intaEjercicioFiscal + "'," + rs3.getString("mIMDT") + "," + rs3.getString("mImporteNeto") + "," + strFolioEjercido + " )");
                        int intDet = pstmntInsertDet.executeUpdate();
                        // Guarda Detalle Pagado
                        // ,"+rs.getString("mIMDT")+")");
                        pstmntInsertDetPag = conn.prepareStatement("INSERT INTO tPagadoDetalle (cTipoPago,nFolioPAGO,nDocRenglon,cMes,cEjercicio,cIdEntidadContable,cIdRelacion,EP,cIdCuentaContable,mComprometido,nPoliza,ID_TIPO_MOVIMIENTO,ID_TIPO_CONCEPTO,cEvento,nMes,cCentroContable,RFC, mImporteNeto,ALM,mImporteBruto,mImporteMasIva,mImporteIva,nCapitulo,mSancion,mDevolucion,mImporteAmortiza,mRetencion,mPenalizacion,m2Millar,m23IVA,mISRHonorarios,mObra5    ,mImporteFlete4,mISRArrenda,mRetImpuestoCedular,mImporteIvaArrenda,mImporteIvaHonorarios ,mImporteFlete23,mImporteIvaProv ,mImporteObra,mCNIC,mTesofe,altaAlmacen,aEjercicioFiscal,mIMDT,mImporte,nFolioPagado,OBGT,ctab)" + " VALUES('" + strLetratipoP + "'," + nFolioPago + "," + nDocRenglon + "," + cMes + ",'" + strcEjercicio + "','" + cIdEntidadContable + "','" + cIdRelacion + "','" + EP + "','" + cIdCuentaContable + "'," + mComprometido + "," + nPoliza + ",'" + idTipoMovimiento + "','" + idTipoConcepto + "','" + cEventoPagado + "','" + cMes + "','" + cCentroContable + "','" + Rfc + "'," + rs3.getString("mImporteNeto") + ",'" + alm + "'," + mImporteBruto + "," + mImporteMasIva + "," + mImporteIva + ",'" + nCapitulo + "'," + mSancion + "," + mDevolucion + "," + mImporteAmortiza + " ," + mRetencion + "," + mPenalizacion + "," + m2Millar + "," + m23IVA + "," + rs3.getString("mISRHonorarios") + "," + rs3.getString("mObra5") + "       ," + rs3.getString("mImporteFlete4") + "," + rs3.getString("mISRArrenda") + "," + rs3.getString("mRetImpuestoCedular") + "," + mImporteIvaArrenda + "," + mImporteIvaHonorarios + "	," + rs3.getString("mImporteFlete23") + "," + mImporteIvaProv + "," + mImporteObra + "," + rs3.getString("mCNIC") + "," + rs3.getString("mTesofe") + ",'" + altaAlmacen + "','" + intaEjercicioFiscal + "'," + rs3.getString("mIMDT") + "," + rs3.getString("mImporteNeto") + "," + strFolioPagado + ",'" + cOBGT + "','00000000000000' )");
                        int intDetPag = pstmntInsertDetPag.executeUpdate();
                    }
                } else {
                    log.debug("No Guarda Encabezado");
                }
                // aplicacion contable
                boolean appCont = false, appContPag = false;
                AccountingEngine accEng = new AccountingEngine();
                accEng.setValidaInsuficienciaDeSaldo(true);
                // Datos Para Aplicar Ejercido
                String cTablaPadre = "tEjercidoEncabezado";
                String cTablaHija = "tEjercidoDetalle";
                String cFolio = "nFolioEjercido";
                String cTipoDocumento = "EJERCIDO";
                // Datos Para Aplicar Pagado
                String cTablaPadrePag = "tPagadoEncabezado";
                String cTablaHijaPag = "tPagadoDetalle";
                String cFolioPag = "nFolioPagado";
                String cTipoDocumentoPag = "PAGADO";
                appCont = accEng.makeAccountingApplication(conn, cTipoDocumento, strFolioEjercido, cTablaPadre, cTablaHija, cFolio);
                if (!aEjercicioFiscal.equals("2012")) {
                    valor = "Ejercido";
                    conn.commit();
                }
                if (strStatusSiaff.trim().equals("Pagada") && fPago.length() > 1) {
                    appContPag = accEng.makeAccountingApplication(conn, cTipoDocumentoPag, strFolioPagado, cTablaPadrePag, cTablaHijaPag, cFolioPag);
                    if (!aEjercicioFiscal.equals("2012")) {
                        valor = "Pagado";
                        conn.commit();
                    }
                } else {
                    // Ejerce y No Paga Cuando es Autorizador Ramo, Envio Banco,
                    // Programada, Rechazo en Banco
                    appContPag = true;
                }
            }
        } catch (Exception e) {
            log.error("Error occurred", "Error: " + e);
            valor = e.toString();
            String[] valorArray = valor.split("\n");
            valor = valorArray[0];
            try {
                conn.rollback();
            } catch (Exception ee) {
                log.warn("Error: cerrando rollback ", ee);
            }
        } finally {
            try {
                CloseObject.closeObject(pstmtnSelect);
                CloseObject.closeObject(pstmntInsertEj);
                CloseObject.closeObject(pstmnSelectDet);
                CloseObject.closeObject(pstmntInsertDet);
                CloseObject.closeObject(pstmntInsertPag);
                CloseObject.closeObject(pstmntInsertDetPag);
                CloseObject.closeObject(pstmtnSicop);
                CloseObject.closeObject(pstmntEncCXP);
                CloseObject.closeObject(pstmntDetCXP);
                CloseObject.closeObject(pstmntDetSICOP);
                CloseObject.closeObject(pstmntSelectDet);
                CloseObject.closeObject(pstmCent);
                CloseObject.closeObject(rs);
                CloseObject.closeObject(rs2);
                CloseObject.closeObject(rs3);
                CloseObject.closeObject(rs4);
                CloseObject.closeObject(rs5);
                CloseObject.closeObject(rs6);
                CloseObject.closeObject(rs7);
                CloseObject.closeObject(rs8);
                CloseObject.closeObject(rs9);
                CloseObject.closeObject(rs10);
                CloseObject.closeObject(rs11);
                CloseObject.closeObject(rs12);
                CloseObject.closeObject(rs13);
            } catch (Exception e) {
                log.warn("Error: cerrando statement", e);
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e) {
                log.warn("Error: cerrando conexion", e);
            }
            conn = null;
        }
        return valor;
    }

    public String aplicarContable(String tipoAplicacion, String nFolioPago) throws SQLException {
        String valor = null;
        Connection conn = null;
        valor = "correcto";
        String cTablaPadre = "";
        String cTablaHija = "";
        String cFolio = "";
        String cTipoDocumento = "";
        try {
            conn = getConnection();
            AccountingEngine accEng = new AccountingEngine();
            // Verificar estaba en
            accEng.setValidaInsuficienciaDeSaldo(true);
            // false
            // Datos Para Aplicar Ejercido
            if (tipoAplicacion.equals("EJERCIDO")) {
                cTablaPadre = "tEjercidoEncabezado";
                cTablaHija = "tEjercidoDetalle";
                cFolio = "nFolioEjercido";
                cTipoDocumento = "EJERCIDO";
            } else {
                // Datos Para Aplicar Pagado
                cTablaPadre = "tPagadoEncabezado";
                cTablaHija = "tPagadoDetalle";
                cFolio = "nFolioPagado";
                cTipoDocumento = "PAGADO";
            }
            accEng.makeAccountingApplication(conn, cTipoDocumento, nFolioPago, cTablaPadre, cTablaHija, cFolio);
            conn.commit();
        } catch (Exception ex) {
            log.warn(ex.getMessage(), ex);
            log.debug("Object: " + String.valueOf(ex.getLocalizedMessage()));
            valor = ex.toString();
            String[] valorArray = valor.split("\n");
            valor = valorArray[0];
            try {
                conn.rollback();
            } catch (Exception ee) {
                log.warn("Error: cerrando rollback ", ee);
            }
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e) {
                log.warn("Error: cerrando conexion", e);
            }
            conn = null;
        }
        return valor;
    }

    public String aplicarContablementeVarios(String nFolios, String tipoDoc, String fPago) throws SQLException {
        String valor = null;
        Connection conn = null;
        PreparedStatement pstmn = null;
        valor = "correcto";
        String cTablaPadre = "";
        String cTablaHija = "";
        String cFolio = "";
        String cTipoDocumento = "";
        String nFolioPago = "";
        try {
            conn = getConnection();
            AccountingEngine accEng = new AccountingEngine();
            accEng.setValidaInsuficienciaDeSaldo(true);
            if (tipoDoc.equals("EJERCIDO")) {
                cTablaPadre = "tEjercidoEncabezado";
                cTablaHija = "tEjercidoDetalle";
                cFolio = "nFolioEjercido";
                cTipoDocumento = "EJERCIDO";
            } else {
                cTablaPadre = "tPagadoEncabezado";
                cTablaHija = "tPagadoDetalle";
                cFolio = "nFolioPagado";
                cTipoDocumento = "PAGADO";
                String[] nF = nFolios.split("/");
                String[] fechPagado = fPago.split("/");
                String fPagado = fechPagado[2] + "-" + fechPagado[1] + "-" + fechPagado[0];
                for (int i = 0; i < nF.length; i++) {
                    nFolioPago = nF[i];
                    pstmn = conn.prepareStatement("UPDATE tPagadoEncabezado SET fAplicacion = ? WHERE nFolioPagado = ? ");
                    pstmn.setString(1, fPagado);
                    pstmn.setString(2, nFolioPago);
                    pstmn.executeUpdate();
                    accEng.makeAccountingApplication(conn, cTipoDocumento, nFolioPago, cTablaPadre, cTablaHija, cFolio);
                }
            }
            conn.commit();
        } catch (Exception ex) {
            log.debug("Object: " + String.valueOf(ex.getLocalizedMessage()));
            valor = ex.toString();
            String[] valorArray = valor.split("\n");
            valor = valorArray[0];
            try {
                conn.rollback();
            } catch (Exception ee) {
                log.warn("Error: cerrando rollback ", ee);
            }
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e) {
                log.warn("Error: cerrando conexion", e);
            }
            conn = null;
        }
        return valor;
    }

    public String aplicarCXPIntegracion(String strCaNoContrarrecibo, String strSicop, String strFolios, String strTipoDocumentos, String fPago, String usuario, String strFolioEjercido, String strFolioPagado, String strStatusSiaff) throws SQLException {
        String valor = null;
        PreparedStatement pstmtnSelect = null, pstmntInsertEj = null, pstmnSelectDet = null, pstmntInsertDet = null, pstmntInsertPag = null, pstmntInsertDetPag = null, pstmtnSicop = null, pstmntDetCXP = null, pstmntDetSICOP = null, pstmntSelectDet = null;
        ResultSet rs = null, rs2 = null, rs3 = null, rs4 = null, rs5 = null, rs6 = null, rs7 = null, rs8 = null, rs9 = null, rs10 = null;
        Connection conn = null;
        valor = "ingresado";
        // Ajuste
        String cCentroContable = "";
        // Ajuste
        String cEvento = "";
        String strTabla = "";
        String strTablaDet = "";
        String strCampos = "";
        String strCamposDet = "";
        String nomCamposDetValidar = "";
        String nomCXP = "";
        String strNomDetFolio = "";
        String strLetratipoP = "";
        try {
            conn = getConnection();
            String[] nF = strFolios.split("/");
            String[] nTD = strTipoDocumentos.split("/");
            String[] nCa = strCaNoContrarrecibo.split("/");
            String[] nFolioEjercido = strFolioEjercido.split("/");
            String[] nFolioPagado = strFolioPagado.split("/");
            for (int e = 0; e < nF.length; e++) {
                String doc = nTD[e];
                String caNoCon = nCa[e];
                String nFolioE = nFolioEjercido[e];
                String nFolioP = nFolioPagado[e];
                if (doc.equals("p_directo")) {
                    strTabla = "tPagoDirectoEncabezado";
                    strTablaDet = "tPagoDirectoDetalle";
                    strCampos = "nFolioPagoDirecto AS nFolio, mImporteNeto as impNeto,caNoContrarrecibo,nFolioPoliza,cTipoPoliza,U_LOGIN,isnull(nFolioPolizaCancelacion,'0') AS nFolioPolizaCancelacion,isnull(fCancelacion,NULL) AS fCancelacion,cDescripcionPoliza,cUnidadResponsableContable,fAplicacion,cRamo,aEjercicioFiscal";
                    strCamposDet = "nDocRenglon,cMes,cEjercicio, EP,cIdCuentaContable,mComprometido,isnull(nPoliza,0) AS nPoliza,ID_TIPO_MOVIMIENTO,ID_TIPO_CONCEPTO,cEvento,cCentroContable,RFC,mImporteNeto,ALM,mImporteBruto,mImporteMasIva,mImporteIva,nCapitulo,mSancion,mDevolucion,mImporteAmortiza,mRetencion,mPenalizacion,m2Millar,m23IVA,mISRHonorarios,mObra5,mImporteFlete4,mISRArrenda,mRetImpuestoCedular,mImporte,mImporteIvaArrenda,mImporteIvaHonorarios,mImporteFlete23,mImporteIvaProv,mImporteObra,isnull(mCNIC,0.00) AS mCNIC,isnull(mIMDT,0.00) AS mIMDT,isnull(mTesofe,0.00) AS mTesofe,altaAlmacen,cCentroContable AS cIdEntidadContable,cIdDocumento AS cIdRelacion";
                    nomCamposDetValidar = "cEvento, EP, cCentroContable,SUM(CONVERT(money,mImporteNeto)) AS mImporteNeto,RFC,cEjercicio,cIdDocumento AS cIdRelacion,nCapitulo";
                    nomCXP = "caNoContrarrecibo";
                    strNomDetFolio = "nFolioPagoDirecto";
                    strLetratipoP = "PAGODIRECTO";
                } else if (doc.equals("r_gastos")) {
                    strTabla = "tRELACIONGASTOSEncabezado";
                    strTablaDet = "tRELACIONGASTOSDetalle";
                    strCampos = "nFolioRELACIONGASTOS AS nFolio,mImporteNeto as impNeto,caNoContrarrecibo,nFolioPoliza,cTipoPoliza,U_LOGIN,isnull(nFolioPolizaCancelacion,'0') AS nFolioPolizaCancelacion,isnull(fCancelacion,NULL) AS fCancelacion,cDescripcionPoliza,cUnidadResponsableContable,fAplicacion,cRamo,aEjercicioFiscal";
                    strCamposDet = "nDocRenglon,cMes,cEjercicio, EP,cIdCuentaContable,mComprometido,isnull(nPoliza,0) AS nPoliza,ID_TIPO_MOVIMIENTO,ID_TIPO_CONCEPTO,cEvento,cCentroContable,RFC,mImporteNeto,ALM,mImporteBruto,mImporteMasIva,mImporteIva,nCapitulo,mSancion,mDevolucion,mImporteAmortiza,mRetencion,mPenalizacion,m2Millar,m23IVA,mISRHonorarios,mObra5,mImporteFlete4,mISRArrenda,mRetImpuestoCedular,mImporte,mImporteIvaArrenda,mImporteIvaHonorarios,mImporteFlete23,mImporteIvaProv,mImporteObra,isnull(mCNIC,0.00) AS mCNIC,isnull(mIMDT,0.00) AS mIMDT,isnull(mTesofe,0.00) AS mTesofe,altaAlmacen,cCentroContable AS cIdEntidadContable, cIdRelacion";
                    nomCamposDetValidar = "cEvento, EP, SUM(CONVERT(money,mImporteNeto)) AS mImporteNeto,cCentroContable,RFC,cEjercicio, cIdRelacion,nCapitulo";
                    nomCXP = "caNoContrarrecibo";
                    strNomDetFolio = "nFolioRELACIONGASTOS";
                    strLetratipoP = "RELACIONGASTOS";
                } else if (doc.equals("p_diverso")) {
                    strTabla = "tPAGODIVERSOEncabezado";
                    strTablaDet = "tPAGODIVERSODetalle";
                    strCampos = "nFolioPAGODIVERSO AS nFolio,mImporteNeto as impNeto,caNoContrarrecibo,nFolioPoliza,cTipoPoliza,U_LOGIN,isnull(nFolioPolizaCancelacion,'0') AS nFolioPolizaCancelacion,isnull(fCancelacion,NULL) AS fCancelacion,cDescripcionPoliza,cUnidadResponsableContable,fAplicacion,cRamo,aEjercicioFiscal";
                    strCamposDet = "nDocRenglon,cMes,cEjercicio, EP,cIdCuentaContable,mComprometido,isnull(nPoliza,0) AS nPoliza,ID_TIPO_MOVIMIENTO,ID_TIPO_CONCEPTO,cEvento,cCentroContable,RFC,mImporteNeto,ALM,mImporteBruto,mImporteMasIva,mImporteIva,nCapitulo,mSancion,mDevolucion,mImporteAmortiza,mRetencion,mPenalizacion,m2Millar,m23IVA,mISRHonorarios,mObra5,mImporteFlete4,mISRArrenda,mRetImpuestoCedular,mImporte,mImporteIvaArrenda,mImporteIvaHonorarios,mImporteFlete23,mImporteIvaProv,mImporteObra,isnull(mCNIC,0.00) AS mCNIC,isnull(mIMDT,0.00) AS mIMDT,isnull(mTesofe,0.00) AS mTesofe,altaAlmacen,cCentroContable AS cIdEntidadContable, cIdRelacion";
                    nomCamposDetValidar = "cEvento, EP, SUM(CONVERT(money,mImporteNeto)) AS mImporteNeto,cCentroContable,RFC,cEjercicio, cIdRelacion,nCapitulo";
                    nomCXP = "caNoContrarrecibo";
                    strNomDetFolio = "nFolioPAGODIVERSO";
                    strLetratipoP = "PAGODIVERSO";
                } else if (doc.equals("p_obra")) {
                    strTabla = "tPAGOOBRAEncabezado";
                    strTablaDet = "tPAGOOBRADetalle";
                    strCampos = "nFolioPAGOOBRA AS nFolio,mImporteNeto as impNeto,caNoContrarrecibo,cDocumentoHaplicado,nFolioPoliza,cTipoPoliza,U_LOGIN,isnull(nFolioPolizaCancelacion,'0') AS nFolioPolizaCancelacion,isnull(fCancelacion,NULL) AS fCancelacion,cDescripcionPoliza,cUnidadResponsableContable,fAplicacion,cRamo,aEjercicioFiscal";
                    strCamposDet = "nDocRenglon,cMes,cEjercicio, EP,cIdCuentaContable,mComprometido,isnull(nPoliza,0) AS nPoliza,ID_TIPO_MOVIMIENTO,ID_TIPO_CONCEPTO,cEvento,cCentroContable,RFC,mImporteNeto,ALM,mImporteBruto,mImporteMasIva,mImporteIva,nCapitulo,mSancion,mDevolucion,mImporteAmortiza,mRetencion,mPenalizacion,m2Millar,m23IVA,mISRHonorarios,mObra5,mImporteFlete4,mISRArrenda,mRetImpuestoCedular,mImporte,mImporteIvaArrenda,mImporteIvaHonorarios,mImporteFlete23,mImporteIvaProv,mImporteObra,isnull(mCNIC,0.00) AS mCNIC,isnull(mIMDT,0.00) AS mIMDT,isnull(mTesofe,0.00) AS mTesofe,altaAlmacen,cCentroContable AS cIdEntidadContable, cIdRelacion";
                    nomCamposDetValidar = "cEvento, EP, SUM(CONVERT(money,mImporteNeto)) AS mImporteNeto,cCentroContable,RFC,cEjercicio, cIdRelacion,nCapitulo";
                    nomCXP = "caNoContrarrecibo";
                    strNomDetFolio = "nFolioPAGOOBRA";
                    strLetratipoP = "PAGOOBRA";
                } else if (doc.equals("nomina")) {
                    strTabla = "tNOMINACLCEncabezado";
                    strTablaDet = "tNOMINACLCDetalle";
                    strCampos = "nFolioNOMINACLC AS nFolio,mImporteNeto as impNeto,caNoContrarreciboCLC, '' cDocumentoHaplicado, 0 nFolioPoliza, 'EG' cTipoPoliza, cIdUsuarioCaptura AS U_LOGIN, 0 AS nFolioPolizaCancelacion, null AS fCancelacion, '' cDescripcionPoliza,cUnidadResponsableContable,fAplicacion,cRamo,aEjercicioFiscal";
                    strCamposDet = "nDocRenglonCLC AS nDocRenglon,cMes,cEjercicio, EP,cIdCuentaContable,mImporteNeto as mComprometido,0 AS nPoliza,ID_TIPO_MOVIMIENTO,ID_TIPO_CONCEPTO,cEvento,cCentroContable,RFC,mImporteNeto,ALM,mImporteBruto,mImporteMasIva, 0 mImporteIva,'1000' as nCapitulo,0 as mSancion,0 as mDevolucion,0 as mImporteAmortiza,0 as mRetencion,0 as mPenalizacion,0 as m2Millar,0 as m23IVA,0 as mISRHonorarios,0 as mObra5,0 as mImporteFlete4, 0 as mISRArrenda,0 as mRetImpuestoCedular,mImporteNeto as mImporte, 0 as mImporteIvaArrenda,0 as mImporteIvaHonorarios,0 as mImporteFlete23,0 as mImporteIvaProv,0 as mImporteObra,0 AS mCNIC,0 AS mIMDT,0 AS mTesofe,cCentroContable AS cIdEntidadContable, cIdRelacion, 0 altaAlmacen ";
                    nomCamposDetValidar = "cEvento, EP, SUM(CONVERT(money,mImporteNeto)) AS mImporteNeto,cCentroContable,RFC,cEjercicio, cIdRelacion,nCapitulo,cMes";
                    nomCXP = "caNoContrarreciboCLC";
                    strNomDetFolio = "nFolioNOMINACLC";
                    strLetratipoP = "NOMINA";
                } else if (doc.equals("federalizado")) {
                    strTabla = "tPAGOFEDERALIZADOEncabezado";
                    strTablaDet = "tPAGOFEDERALIZADODetalle";
                    strCampos = "nFolioPAGOFEDERALIZADO AS nFolio,mImporteNeto as impNeto,caNoContrarrecibo,cDocumentoHaplicado,nFolioPoliza,cTipoPoliza,U_LOGIN,isnull(nFolioPolizaCancelacion,'0') AS nFolioPolizaCancelacion,isnull(fCancelacion,NULL) AS fCancelacion,cDescripcionPoliza,cUnidadResponsableContable,fAplicacion,cRamo,aEjercicioFiscal";
                    strCamposDet = "nDocRenglon,cMes,cEjercicio, EP,cIdCuentaContable,mComprometido,isnull(nPoliza,0) AS nPoliza,ID_TIPO_MOVIMIENTO,ID_TIPO_CONCEPTO,cEvento,cCentroContable,RFC,mImporteNeto,ALM,mImporteBruto,mImporteMasIva,mImporteIva,nCapitulo,mSancion,mDevolucion,mImporteAmortiza,mRetencion,mPenalizacion,m2Millar,m23IVA,mISRHonorarios,mObra5,mImporteFlete4,mISRArrenda,mRetImpuestoCedular,mImporte,mImporteIvaArrenda,mImporteIvaHonorarios,mImporteFlete23,mImporteIvaProv,mImporteObra,isnull(mCNIC,0.00) AS mCNIC,isnull(mIMDT,0.00) AS mIMDT,isnull(mTesofe,0.00) AS mTesofe,altaAlmacen,cCentroContable AS cIdEntidadContable, cIdRelacion";
                    nomCamposDetValidar = "cEvento, EP, SUM(CONVERT(money,mImporteNeto)) AS mImporteNeto,cCentroContable,RFC,cEjercicio, cIdRelacion,nCapitulo";
                    nomCXP = "caNoContrarrecibo";
                    strNomDetFolio = "nFolioPAGOFEDERALIZADO";
                    strLetratipoP = "FEDERALIZADO";
                } else if (doc.equals("o_ajenas")) {
                    strTabla = "tOperAjenasEncabezado";
                    strTablaDet = "tOperAjenasDetalle";
                    strCampos = "nFolioOperAjenas AS nFolio,mImportes as impNeto,caNoContrarrecibo,cDocumentoHaplicado,nFolioPoliza,cTipoPoliza,U_LOGIN,isnull(nFolioPolizaCancelacion,'0') AS nFolioPolizaCancelacion,isnull(fCancelacion,NULL) AS fCancelacion,cDescripcionPoliza,cUnidadResponsableContable,fAplicacion,cRamo,aEjercicioFiscal";
                    strCamposDet = "nDocRenglon,cMes,aEjercicioFiscal AS cEjercicio,Ep AS EP,cEvento,cCentroContable,RFC,mTotal AS mImporteNeto,mISRHonorarios,mObra5,mImporteFlete4,mISRArrenda,mRetImpuestoCedular,mImporteFlete23,isnull(mCNIC,0.00) AS mCNIC,isnull(mIMDT,0.00) AS mIMDT,isnull(mTesofe,0.00) AS mTesofe,cCentroContable AS cIdEntidadContable";
                    nomCamposDetValidar = "cEvento, Ep AS EP, SUM(CONVERT(money,mTotal)) AS mImporteNeto,cCentroContable,RFC,aEjercicioFiscal AS cEjercicio";
                    nomCXP = "caNoContrarrecibo";
                    strNomDetFolio = "nFolioOperAjenas";
                    strLetratipoP = "AJENAS";
                }
                String fPagado = null;
                if (fPago.length() > 1) {
                    String[] fechPagado = fPago.split("/");
                    fPagado = "'" + fechPagado[2] + "-" + fechPagado[1] + "-" + fechPagado[0] + "'";
                }
                String fechaAplicadoSicop = "";
                String fechaPagoSicop = "";
                String solicitudPago = "";
                String numProceso = "";
                String folioSiaff = "";
                String DATE_FORMAT = "yyyy-MM-dd";
                SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
                // today
                Calendar c1 = Calendar.getInstance();
                String FechaPagado = sdf.format(c1.getTime());
                System.out.println("Fecha_today: " + FechaPagado);
                // Buscar Informacion SICOP
                pstmtnSicop = conn.prepareStatement("SELECT FECHA_APL,FECHA_PAGO_106,SPAG_176,PROC_CLAVE,FOLIO_SIAFF_112 FROM CLC_SICOP WITH(NOLOCK) WHERE NCLC_43 = ? GROUP BY FECHA_APL,FECHA_PAGO_106,SPAG_176,PROC_CLAVE,FOLIO_SIAFF_112");
                pstmtnSicop.setString(1, strSicop);
                rs7 = pstmtnSicop.executeQuery();
                if (rs7.next()) {
                    String[] fAS = rs7.getString("FECHA_APL").split("/");
                    fechaAplicadoSicop = fAS[2] + "-" + fAS[1] + "-" + fAS[0];
                    String[] fPS = rs7.getString("FECHA_PAGO_106").split("/");
                    fechaPagoSicop = fPS[2] + "-" + fPS[1] + "-" + fPS[0];
                    solicitudPago = rs7.getString("SPAG_176");
                    numProceso = rs7.getString("PROC_CLAVE");
                    folioSiaff = rs7.getString("FOLIO_SIAFF_112");
                }
                // Buscar Informacion Cabecera
                pstmtnSelect = conn.prepareStatement("SELECT " + strCampos + " FROM " + strTabla + " WITH(NOLOCK) WHERE " + nomCXP + "= ? ");
                pstmtnSelect.setString(1, caNoCon);
                rs = pstmtnSelect.executeQuery();
                if (rs.next()) {
                    String recibo = (strTabla.equals("tNOMINACLCEncabezado")) ? rs.getString("caNoContrarreciboCLC") : rs.getString("caNoContrarrecibo");
                    String tipoPoliza = rs.getString("cTipoPoliza");
                    String fCancelacion = rs.getString("fCancelacion");
                    String desPoliza = rs.getString("cDescripcionPoliza");
                    String uniResp = rs.getString("cUnidadResponsableContable");
                    String cRamo = rs.getString("cRamo");
                    int nFolioPago = Integer.parseInt(rs.getString("nFolio"), 10);
                    int folioPoliza = Integer.parseInt(rs.getString("nFolioPoliza"), 10);
                    int nFolPolCancelacion = Integer.parseInt(rs.getString("nFolioPolizaCancelacion"), 10);
                    int intaEjercicioFiscal = Integer.parseInt(rs.getString("aEjercicioFiscal"), 10);
                    if (fCancelacion != null) {
                        fCancelacion = "'" + fCancelacion + "'";
                    } else {
                        fCancelacion = null;
                    }
                    // Guarda En Cabecera Ejercido
                    // Para Guardar 0 y Generar el Nuevo Folio.
                    folioPoliza = 0;
                    pstmntInsertEj = conn.prepareStatement("INSERT INTO tEjercidoEncabezado (cTipoPago,nFolioPAGO,caNoContrarrecibo,nFolioPoliza,cTipoPoliza,U_LOGIN,nFolioPolizaCancelacion,fCancelacion,cDescripcionPoliza,cUnidadResponsableContable,nFolioSICOP,fAplicacion,cRamo,cIdUsuarioCaptura,FechaAplicacionSicop,FechaPagoSicop,SolicitudPago,NumeroProceso,nFolioSIAFF,nFolioEjercido) " + "VALUES ('" + strLetratipoP + "'," + nFolioPago + ",'" + recibo + "'," + folioPoliza + ",'" + tipoPoliza + "','" + usuario + "'," + nFolPolCancelacion + "," + fCancelacion + ",'" + desPoliza + "','" + uniResp + "'," + strSicop + ",'" + fechaAplicadoSicop + "','" + cRamo + "','" + usuario + "','" + fechaAplicadoSicop + "','" + fechaPagoSicop + "'," + solicitudPago + "," + numProceso + "," + folioSiaff + "," + nFolioE + " )");
                    int intr = pstmntInsertEj.executeUpdate();
                    // Guarda En Cabecera Pagado
                    pstmntInsertPag = conn.prepareStatement("INSERT INTO tPagadoEncabezado (cTipoPago,nFolioPAGO,caNoContrarrecibo,nFolioPoliza,cTipoPoliza,U_LOGIN,nFolioPolizaCancelacion,fCancelacion,cDescripcionPoliza,cUnidadResponsableContable,nFolioSICOP,fAplicacion,cRamo,cIdUsuarioCaptura,FechaAplicacionSicop,FechaPagoSicop,SolicitudPago,NumeroProceso,nFolioSIAFF,FechaPagado,nFolioPagado) " + "VALUES ('" + strLetratipoP + "'," + nFolioPago + ",'" + recibo + "'," + folioPoliza + ",'" + tipoPoliza + "','" + usuario + "'," + nFolPolCancelacion + "," + fCancelacion + ",'" + desPoliza + "','" + uniResp + "'," + strSicop + "," + fPagado + ",'" + cRamo + "','" + usuario + "','" + fechaAplicadoSicop + "','" + fechaPagoSicop + "'," + solicitudPago + "," + numProceso + "," + folioSiaff + ",'" + FechaPagado + "'," + nFolioP + " )");
                    int intrPag = pstmntInsertPag.executeUpdate();
                    if (intr > 0 && intrPag > 0) {
                        // Buscar detalles con el nFolio de Encabezado
                        pstmnSelectDet = conn.prepareStatement("SELECT " + strCamposDet + " FROM " + strTablaDet + " WITH(NOLOCK) WHERE cEvento != 'ANTICIPO' AND cEvento != 'ANTICIPO_DIV' AND " + strNomDetFolio + " = ? ");
                        pstmnSelectDet.setInt(1, nFolioPago);
                        rs3 = pstmnSelectDet.executeQuery();
                        int nDocRenglon = 0;
                        while (rs3.next()) {
                            if (strTablaDet.equals("tPAGOFEDERALIZADODetalle")) {
                                nDocRenglon = nDocRenglon + 1;
                            } else {
                                nDocRenglon = Integer.parseInt(rs3.getString("nDocRenglon"), 10);
                            }
                            int cMes = Integer.parseInt(rs3.getString("cMes"), 10);
                            String strcEjercicio = rs3.getString("cEjercicio");
                            String EP = rs3.getString("EP");
                            String cOBGT = EP.substring(31, 36);
                            String cIdCuentaContable = (strTablaDet.equals("tOperAjenasDetalle")) ? " " : rs3.getString("cIdCuentaContable");
                            String cIdEntidadContable = rs3.getString("cIdEntidadContable");
                            String cIdRelacion = (strTablaDet.equals("tOperAjenasDetalle")) ? " " : rs3.getString("cIdRelacion");
                            String idTipoMovimiento = (strTablaDet.equals("tOperAjenasDetalle")) ? " " : rs3.getString("ID_TIPO_MOVIMIENTO");
                            String idTipoConcepto = (strTablaDet.equals("tOperAjenasDetalle")) ? " " : rs3.getString("ID_TIPO_CONCEPTO");
                            double mComprometido = (strTablaDet.equals("tOperAjenasDetalle")) ? 0.00 : rs3.getDouble("mComprometido");
                            double mImporteBruto = (strTablaDet.equals("tOperAjenasDetalle")) ? 0.00 : rs3.getDouble("mImporteBruto");
                            double mImporteIva = (strTablaDet.equals("tOperAjenasDetalle")) ? 0.00 : rs3.getDouble("mImporteIva");
                            double mImporteMasIva = (strTablaDet.equals("tOperAjenasDetalle")) ? 0.00 : rs3.getDouble("mImporteMasIva");
                            double mSancion = (strTablaDet.equals("tOperAjenasDetalle")) ? 0.00 : rs3.getDouble("mSancion");
                            double mDevolucion = (strTablaDet.equals("tOperAjenasDetalle")) ? 0.00 : rs3.getDouble("mDevolucion");
                            double mImporteAmortiza = (strTablaDet.equals("tOperAjenasDetalle")) ? 0.00 : rs3.getDouble("mImporteAmortiza");
                            double mRetencion = (strTablaDet.equals("tOperAjenasDetalle")) ? 0.00 : rs3.getDouble("mRetencion");
                            double mPenalizacion = (strTablaDet.equals("tOperAjenasDetalle")) ? 0.00 : rs3.getDouble("mPenalizacion");
                            double m2Millar = (strTablaDet.equals("tOperAjenasDetalle")) ? 0.00 : rs3.getDouble("m2Millar");
                            double m23IVA = (strTablaDet.equals("tOperAjenasDetalle")) ? 0.00 : rs3.getDouble("m23IVA");
                            double mImporteIvaHonorarios = (strTablaDet.equals("tOperAjenasDetalle")) ? 0.00 : rs3.getDouble("mImporteIvaHonorarios");
                            double mImporteIvaProv = (strTablaDet.equals("tOperAjenasDetalle")) ? 0.00 : rs3.getDouble("mImporteIvaProv");
                            double mImporteObra = (strTablaDet.equals("tOperAjenasDetalle")) ? 0.00 : rs3.getDouble("mImporteObra");
                            double mImporteIvaArrenda = (strTablaDet.equals("tOperAjenasDetalle")) ? 0.00 : rs3.getDouble("mImporteIvaArrenda");
                            int nPoliza = (strTablaDet.equals("tOperAjenasDetalle")) ? 0 : rs3.getInt("nPoliza");
                            String[] cEventoCa = rs3.getString("cEvento").split("_");
                            cEvento = "P";
                            for (int i = 1; i < cEventoCa.length; i++) {
                                cEvento += "_";
                                cEvento += cEventoCa[i];
                            }
                            cCentroContable = rs3.getString("cCentroContable");
                            String Rfc = rs3.getString("RFC");
                            String alm = (strTablaDet.equals("tOperAjenasDetalle")) ? " " : rs3.getString("ALM");
                            String nCapitulo = (strTablaDet.equals("tOperAjenasDetalle")) ? " " : rs3.getString("nCapitulo");
                            String altaAlmacen = "0";
                            String cEventoPagado = (strTablaDet.equals("tOperAjenasDetalle")) ? "P_AJENA" : cEvento;
                            if (!strTablaDet.equals("tNOMINADetalle") && !strTablaDet.equals("tOperAjenasDetalle")) {
                                altaAlmacen = rs3.getString("altaAlmacen");
                            }
                            // Guarda Detalle Ejercido
                            // ,"+rs.getString("mIMDT")+")");
                            pstmntInsertDet = conn.prepareStatement("INSERT INTO tEjercidoDetalle (cTipoPago,nFolioPAGO,nDocRenglon,cMes,cEjercicio,cIdEntidadContable,cIdRelacion,EP,cIdCuentaContable,mComprometido,nPoliza,ID_TIPO_MOVIMIENTO,ID_TIPO_CONCEPTO,cEvento,nMes,cCentroContable,RFC, mImporteNeto,ALM,mImporteBruto,mImporteMasIva,mImporteIva,nCapitulo,mSancion,mDevolucion,mImporteAmortiza,mRetencion,mPenalizacion,m2Millar,m23IVA,mISRHonorarios,mObra5,mImporteFlete4,mISRArrenda,mRetImpuestoCedular,mImporteIvaArrenda,mImporteIvaHonorarios ,mImporteFlete23,mImporteIvaProv ,mImporteObra,mCNIC,mTesofe,altaAlmacen,aEjercicioFiscal,mIMDT,mImporte,nFolioEjercido)" + " VALUES('" + strLetratipoP + "'," + nFolioPago + "," + nDocRenglon + "," + cMes + ",'" + strcEjercicio + "','" + cIdEntidadContable + "','" + cIdRelacion + "','" + EP + "','" + cIdCuentaContable + "'," + mComprometido + "," + nPoliza + ",'" + idTipoMovimiento + "','" + idTipoConcepto + "','EJERCIDO','" + cMes + "','" + cCentroContable + "','" + Rfc + "'," + rs3.getString("mImporteNeto") + ",'" + alm + "'," + mImporteBruto + "," + mImporteMasIva + "," + mImporteIva + ",'" + nCapitulo + "'," + mSancion + "," + mDevolucion + "," + mImporteAmortiza + " ," + mRetencion + "," + mPenalizacion + "," + m2Millar + "," + m23IVA + "," + rs3.getString("mISRHonorarios") + "," + rs3.getString("mObra5") + "       ," + rs3.getString("mImporteFlete4") + "," + rs3.getString("mISRArrenda") + "," + rs3.getString("mRetImpuestoCedular") + "," + mImporteIvaArrenda + "," + mImporteIvaHonorarios + "	," + rs3.getString("mImporteFlete23") + "," + mImporteIvaProv + "," + mImporteObra + "," + rs3.getString("mCNIC") + "," + rs3.getString("mTesofe") + ",'" + altaAlmacen + "','" + intaEjercicioFiscal + "'," + rs3.getString("mIMDT") + "," + rs3.getString("mImporteNeto") + "," + nFolioE + " )");
                            int intDet = pstmntInsertDet.executeUpdate();
                            log.trace("Object: {}", "Se insertaron: " + intDet + " registros");
                            // ,"+rs.getString("mIMDT")+")");
                            pstmntInsertDetPag = conn.prepareStatement("INSERT INTO tPagadoDetalle (cTipoPago,nFolioPAGO,nDocRenglon,cMes,cEjercicio,cIdEntidadContable,cIdRelacion,EP,cIdCuentaContable,mComprometido,nPoliza,ID_TIPO_MOVIMIENTO,ID_TIPO_CONCEPTO,cEvento,nMes,cCentroContable,RFC, mImporteNeto,ALM,mImporteBruto,mImporteMasIva,mImporteIva,nCapitulo,mSancion,mDevolucion,mImporteAmortiza,mRetencion,mPenalizacion,m2Millar,m23IVA,mISRHonorarios,mObra5    ,mImporteFlete4,mISRArrenda,mRetImpuestoCedular,mImporteIvaArrenda,mImporteIvaHonorarios ,mImporteFlete23,mImporteIvaProv ,mImporteObra,mCNIC,mTesofe,altaAlmacen,aEjercicioFiscal,mIMDT,mImporte,nFolioPagado,OBGT)" + " VALUES('" + strLetratipoP + "'," + nFolioPago + "," + nDocRenglon + "," + cMes + ",'" + strcEjercicio + "','" + cIdEntidadContable + "','" + cIdRelacion + "','" + EP + "','" + cIdCuentaContable + "'," + mComprometido + "," + nPoliza + ",'" + idTipoMovimiento + "','" + idTipoConcepto + "','" + cEventoPagado + "','" + cMes + "','" + cCentroContable + "','" + Rfc + "'," + rs3.getString("mImporteNeto") + ",'" + alm + "'," + mImporteBruto + "," + mImporteMasIva + "," + mImporteIva + ",'" + nCapitulo + "'," + mSancion + "," + mDevolucion + "," + mImporteAmortiza + " ," + mRetencion + "," + mPenalizacion + "," + m2Millar + "," + m23IVA + "," + rs3.getString("mISRHonorarios") + "," + rs3.getString("mObra5") + "       ," + rs3.getString("mImporteFlete4") + "," + rs3.getString("mISRArrenda") + "," + rs3.getString("mRetImpuestoCedular") + "," + mImporteIvaArrenda + "," + mImporteIvaHonorarios + "	," + rs3.getString("mImporteFlete23") + "," + mImporteIvaProv + "," + mImporteObra + "," + rs3.getString("mCNIC") + "," + rs3.getString("mTesofe") + ",'" + altaAlmacen + "','" + intaEjercicioFiscal + "'," + rs3.getString("mIMDT") + "," + rs3.getString("mImporteNeto") + "," + nFolioP + ",'" + cOBGT + "' )");
                            int intDetPag = pstmntInsertDetPag.executeUpdate();
                            log.trace("Object: {}", "Se insertaron: " + intDetPag + " registros");
                        }
                    } else {
                        log.debug("No Guarda Encabezado");
                    }
                }
                AccountingEngine accEng = new AccountingEngine();
                accEng.setValidaInsuficienciaDeSaldo(true);
                // Datos Para Aplicar Ejercido
                String cTablaPadre = "tEjercidoEncabezado";
                String cTablaHija = "tEjercidoDetalle";
                String cFolio = "nFolioEjercido";
                String cTipoDocumento = "EJERCIDO";
                // Datos Para Aplicar Pagado
                String cTablaPadrePag = "tPagadoEncabezado";
                String cTablaHijaPag = "tPagadoDetalle";
                String cFolioPag = "nFolioPagado";
                String cTipoDocumentoPag = "PAGADO";
                accEng.makeAccountingApplication(conn, cTipoDocumento, nFolioE, cTablaPadre, cTablaHija, cFolio);
                conn.commit();
                if (strStatusSiaff.trim().equals("Pagada") && fPago.length() > 1) {
                    accEng.makeAccountingApplication(conn, cTipoDocumentoPag, nFolioP, cTablaPadrePag, cTablaHijaPag, cFolioPag);
                    conn.commit();
                }
            }
        } catch (Exception e) {
            log.error("Error occurred", "Error: " + e);
            log.debug("Object: " + String.valueOf(e.getLocalizedMessage()));
            valor = e.toString();
            String[] valorArray = valor.split("\n");
            valor = valorArray[0];
            valor = "no_ingresado";
            try {
                conn.rollback();
            } catch (Exception ee) {
                log.warn("Error: cerrando rollback ", ee);
            }
        } finally {
            try {
                CloseObject.closeObject(pstmtnSelect);
                CloseObject.closeObject(pstmntInsertEj);
                CloseObject.closeObject(pstmnSelectDet);
                CloseObject.closeObject(pstmntInsertDet);
                CloseObject.closeObject(pstmntInsertPag);
                CloseObject.closeObject(pstmntInsertDetPag);
                CloseObject.closeObject(pstmtnSicop);
                CloseObject.closeObject(pstmntDetCXP);
                CloseObject.closeObject(pstmntDetSICOP);
                CloseObject.closeObject(pstmntSelectDet);
                CloseObject.closeObject(rs);
                CloseObject.closeObject(rs2);
                CloseObject.closeObject(rs3);
                CloseObject.closeObject(rs4);
                CloseObject.closeObject(rs5);
                CloseObject.closeObject(rs6);
                CloseObject.closeObject(rs7);
                CloseObject.closeObject(rs8);
                CloseObject.closeObject(rs9);
                CloseObject.closeObject(rs10);
            } catch (Exception e) {
                log.warn("Error: cerrando statement", e);
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e) {
                log.warn("Error: cerrando conexion", e);
            }
            conn = null;
        }
        return valor;
    }

    public String aplicarDiferentes(String strTabla, String idCXPDiferentes, String strCLC, String fPago, String usuario, String strFolioEjercido, String strFolioPagado, String strStatusSiaff) throws SQLException {
        String valor = null;
        PreparedStatement pstmtnSelect = null, pstmntInsertEj = null, pstmnSelectDet = null, pstmntInsertDet = null, pstmntInsertPag = null, pstmntInsertDetPag = null, pstmtnSicop = null;
        ResultSet rs = null, rs2 = null, rs3 = null, rs4 = null, rs5 = null, rs6 = null, rs7 = null;
        Connection conn = null;
        valor = "ingresado";
        int nFolioPago = 0;
        int ii = 0;
        try {
            conn = getConnection();
            String fPagado = null;
            if (!fPago.equals(" ")) {
                String[] fechPagado = fPago.split("/");
                fPagado = "'" + fechPagado[2] + "-" + fechPagado[1] + "-" + fechPagado[0] + "'";
            }
            String fechaAplicadoSicop = "";
            String fechaPagoSicop = "";
            String solicitudPago = "";
            String numProceso = "";
            String folioSiaff = "";
            java.util.Date utilDate = new java.util.Date();
            long lnMilisegundos = utilDate.getTime();
            java.sql.Date sqlDate = new java.sql.Date(lnMilisegundos);
            String[] fp = String.valueOf(sqlDate).split("-");
            String FechaPagado = fp[2] + "-" + fp[1] + "-" + fp[0];
            // Buscar Informacion SICOP
            pstmtnSicop = conn.prepareStatement("SELECT FECHA_APL,FECHA_PAGO_106,SPAG_176,PROC_CLAVE,FOLIO_SIAFF_112 FROM CLC_SICOP WITH(NOLOCK) WHERE NCLC_43 = ? GROUP BY FECHA_APL,FECHA_PAGO_106,SPAG_176,PROC_CLAVE,FOLIO_SIAFF_112");
            pstmtnSicop.setString(1, strCLC);
            rs7 = pstmtnSicop.executeQuery();
            if (rs7.next()) {
                String[] fAS = rs7.getString("FECHA_APL").split("/");
                fechaAplicadoSicop = fAS[2] + "-" + fAS[1] + "-" + fAS[0];
                String[] fPS = rs7.getString("FECHA_PAGO_106").split("/");
                fechaPagoSicop = fPS[2] + "-" + fPS[1] + "-" + fPS[0];
                solicitudPago = rs7.getString("SPAG_176");
                numProceso = rs7.getString("PROC_CLAVE");
                folioSiaff = rs7.getString("FOLIO_SIAFF_112");
            }
            // Buscar Informacion Cabecera
            String[] cxpDif = idCXPDiferentes.split("/");
            int f2 = 0;
            for (int i = 1; i < cxpDif.length; i++) {
                pstmtnSelect = conn.prepareStatement("SELECT nFolioRELACIONGASTOS AS nFolio,caNoContrarrecibo,nFolioPoliza,cTipoPoliza,U_LOGIN,isnull(nFolioPolizaCancelacion,'0') AS nFolioPolizaCancelacion,isnull(fCancelacion,NULL) AS fCancelacion,cDescripcionPoliza,cUnidadResponsableContable,fAplicacion,cRamo,aEjercicioFiscal FROM " + strTabla + " WITH(NOLOCK) WHERE caNoContrarrecibo = ? ");
                pstmtnSelect.setString(1, cxpDif[i]);
                rs = pstmtnSelect.executeQuery();
                String[] strFolioEjercido2 = strFolioEjercido.split("/");
                String[] strFolioPagado2 = strFolioPagado.split("/");
                if (rs.next()) {
                    String recibo = rs.getString("caNoContrarrecibo");
                    String tipoPoliza = rs.getString("cTipoPoliza");
                    String fCancelacion = rs.getString("fCancelacion");
                    String desPoliza = rs.getString("cDescripcionPoliza");
                    String uniResp = rs.getString("cUnidadResponsableContable");
                    String cRamo = rs.getString("cRamo");
                    nFolioPago = Integer.parseInt(rs.getString("nFolio"), 10);
                    int folioPoliza = Integer.parseInt(rs.getString("nFolioPoliza"), 10);
                    int nFolPolCancelacion = Integer.parseInt(rs.getString("nFolioPolizaCancelacion"), 10);
                    int aEjercicioFiscal = Integer.parseInt(rs.getString("aEjercicioFiscal"), 10);
                    if (fCancelacion != null) {
                        fCancelacion = "'" + fCancelacion + "'";
                    } else {
                        fCancelacion = null;
                    }
                    // Guarda En Cabecera Ejercido
                    // Para Guardar 0 y Generar el Nuevo Folio.
                    folioPoliza = 0;
                    pstmntInsertEj = conn.prepareStatement("INSERT INTO tEjercidoEncabezado (cTipoPago,nFolioPAGO,caNoContrarrecibo,nFolioPoliza,cTipoPoliza,U_LOGIN,nFolioPolizaCancelacion,fCancelacion,cDescripcionPoliza,cUnidadResponsableContable,nFolioSICOP,fAplicacion,cRamo,cIdUsuarioCaptura,FechaAplicacionSicop,FechaPagoSicop,SolicitudPago,NumeroProceso,nFolioSIAFF,nFolioEjercido) " + "VALUES ('RELACIONGASTOS'," + nFolioPago + ",'" + recibo + "'," + folioPoliza + ",'" + tipoPoliza + "','" + usuario + "'," + nFolPolCancelacion + "," + fCancelacion + ",'" + desPoliza + "','" + uniResp + "'," + strCLC + ",'" + fechaAplicadoSicop + "','" + cRamo + "','" + usuario + "','" + fechaAplicadoSicop + "','" + fechaPagoSicop + "'," + solicitudPago + "," + numProceso + "," + folioSiaff + "," + strFolioEjercido2[f2] + " )");
                    int intr = pstmntInsertEj.executeUpdate();
                    // Guarda En Cabecera Pagado
                    pstmntInsertPag = conn.prepareStatement("INSERT INTO tPagadoEncabezado (cTipoPago,nFolioPAGO,caNoContrarrecibo,nFolioPoliza,cTipoPoliza,U_LOGIN,nFolioPolizaCancelacion,fCancelacion,cDescripcionPoliza,cUnidadResponsableContable,nFolioSICOP,fAplicacion,cRamo,cIdUsuarioCaptura,FechaAplicacionSicop,FechaPagoSicop,SolicitudPago,NumeroProceso,nFolioSIAFF,FechaPagado,nFolioPagado) " + "VALUES ('RELACIONGASTOS'," + nFolioPago + ",'" + recibo + "'," + folioPoliza + ",'" + tipoPoliza + "','" + usuario + "'," + nFolPolCancelacion + "," + fCancelacion + ",'" + desPoliza + "','" + uniResp + "'," + strCLC + "," + fPagado + ",'" + cRamo + "','" + usuario + "','" + fechaAplicadoSicop + "','" + fechaPagoSicop + "'," + solicitudPago + "," + numProceso + "," + folioSiaff + ",'" + FechaPagado + "'," + strFolioPagado2[f2] + " )");
                    int intrPag = pstmntInsertPag.executeUpdate();
                    if (intr > 0 && intrPag > 0) {
                        // Buscar detalles con el nFolio de Encabezado
                        pstmnSelectDet = conn.prepareStatement("SELECT nDocRenglon,cMes,cEjercicio, EP,cIdCuentaContable,mComprometido,isnull(nPoliza,0) AS nPoliza,ID_TIPO_MOVIMIENTO,ID_TIPO_CONCEPTO,cEvento,cCentroContable,RFC,mImporteNeto,ALM,mImporteBruto,mImporteMasIva,mImporteIva,nCapitulo,mSancion,mDevolucion,mImporteAmortiza,mRetencion,mPenalizacion,m2Millar,m23IVA,mISRHonorarios,mObra5,mImporteFlete4,mISRArrenda,mRetImpuestoCedular,mImporte,mImporteIvaArrenda,mImporteIvaHonorarios,mImporteFlete23,mImporteIvaProv,mImporteObra,isnull(mCNIC,0.00) AS mCNIC,isnull(mIMDT,0.00) AS mIMDT,isnull(mTesofe,0.00) AS mTesofe,isnull(altaAlmacen,0) AS altaAlmacen,cCentroContable AS cIdEntidadContable, cIdRelacion FROM tRELACIONGASTOSDetalle WITH(NOLOCK) WHERE cEvento != 'ANTICIPO' AND nFolioRELACIONGASTOS = ? ");
                        pstmnSelectDet.setInt(1, nFolioPago);
                        rs3 = pstmnSelectDet.executeQuery();
                        while (rs3.next()) {
                            int nDocRenglon = Integer.parseInt(rs3.getString("nDocRenglon"), 10);
                            int cMes = Integer.parseInt(rs3.getString("cMes"), 10);
                            String cEjercicio = rs3.getString("cEjercicio");
                            String EP = rs3.getString("EP");
                            String cOBGT = EP.substring(31, 36);
                            String cIdCuentaContable = rs3.getString("cIdCuentaContable");
                            String cIdEntidadContable = rs3.getString("cIdEntidadContable");
                            String cIdRelacion = rs3.getString("cIdRelacion");
                            String idTipoMovimiento = rs3.getString("ID_TIPO_MOVIMIENTO");
                            String idTipoConcepto = rs3.getString("ID_TIPO_CONCEPTO");
                            String[] cEventoCa = rs3.getString("cEvento").split("_");
                            String cEvento = "P_" + cEventoCa[1];
                            String cCentroContable = rs3.getString("cCentroContable");
                            String Rfc = rs3.getString("RFC");
                            String alm = rs3.getString("ALM");
                            String nCapitulo = rs3.getString("nCapitulo");
                            // String altaAlmacen = "0";
                            String altaAlmacen = rs3.getString("altaAlmacen");
                            // Guarda Detalle Ejercido
                            // ,"+rs.getString("mIMDT")+")");
                            pstmntInsertDet = conn.prepareStatement("INSERT INTO tEjercidoDetalle (cTipoPago,nFolioPAGO,nDocRenglon,cMes,cEjercicio,cIdEntidadContable,cIdRelacion,EP,cIdCuentaContable,mComprometido,nPoliza,ID_TIPO_MOVIMIENTO,ID_TIPO_CONCEPTO,cEvento,nMes,cCentroContable,RFC, mImporteNeto,ALM,mImporteBruto,mImporteMasIva,mImporteIva,nCapitulo,mSancion,mDevolucion,mImporteAmortiza,mRetencion,mPenalizacion,m2Millar,m23IVA,mISRHonorarios,mObra5,mImporteFlete4,mISRArrenda,mRetImpuestoCedular,mImporteIvaArrenda,mImporteIvaHonorarios ,mImporteFlete23,mImporteIvaProv ,mImporteObra,mCNIC,mTesofe,altaAlmacen,aEjercicioFiscal,mIMDT,mImporte,nFolioEjercido)" + " VALUES('RELACIONGASTOS'," + nFolioPago + "," + nDocRenglon + "," + cMes + ",'" + cEjercicio + "','" + cIdEntidadContable + "','" + cIdRelacion + "','" + EP + "','" + cIdCuentaContable + "'," + rs3.getString("mComprometido") + "," + rs3.getString("nPoliza") + ",'" + idTipoMovimiento + "','" + idTipoConcepto + "','EJERCIDO','" + cMes + "','" + cCentroContable + "','" + Rfc + "'," + rs3.getString("mImporteNeto") + ",'" + alm + "'," + rs3.getString("mImporteBruto") + "," + rs3.getString("mImporteMasIva") + "," + rs3.getString("mImporteIva") + ",'" + nCapitulo + "'," + rs3.getString("mSancion") + "," + rs3.getString("mDevolucion") + "," + rs3.getString("mImporteAmortiza") + " ," + rs3.getString("mRetencion") + "," + rs3.getString("mPenalizacion") + "," + rs3.getString("m2Millar") + "," + rs3.getString("m23IVA") + "," + rs3.getString("mISRHonorarios") + "," + rs3.getString("mObra5") + "       ," + rs3.getString("mImporteFlete4") + "," + rs3.getString("mISRArrenda") + "," + rs3.getString("mRetImpuestoCedular") + "," + rs3.getString("mImporteIvaArrenda") + "," + rs3.getString("mImporteIvaHonorarios") + "	," + rs3.getString("mImporteFlete23") + "," + rs3.getString("mImporteIvaProv") + "," + rs3.getString("mImporteObra") + "," + rs3.getString("mCNIC") + "," + rs3.getString("mTesofe") + ",'" + altaAlmacen + "','" + aEjercicioFiscal + "'," + rs3.getString("mIMDT") + "," + rs3.getString("mImporteNeto") + "," + strFolioEjercido2[f2] + " )");
                            pstmntInsertDet.executeUpdate();
                            // Guarda Detalle Pagado
                            // ,"+rs.getString("mIMDT")+")");
                            pstmntInsertDetPag = conn.prepareStatement("INSERT INTO tPagadoDetalle (cTipoPago,nFolioPAGO,nDocRenglon,cMes,cEjercicio,cIdEntidadContable,cIdRelacion,EP,cIdCuentaContable,mComprometido,nPoliza,ID_TIPO_MOVIMIENTO,ID_TIPO_CONCEPTO,cEvento,nMes,cCentroContable,RFC, mImporteNeto,ALM,mImporteBruto,mImporteMasIva,mImporteIva,nCapitulo,mSancion,mDevolucion,mImporteAmortiza,mRetencion,mPenalizacion,m2Millar,m23IVA,mISRHonorarios,mObra5    ,mImporteFlete4,mISRArrenda,mRetImpuestoCedular,mImporteIvaArrenda,mImporteIvaHonorarios ,mImporteFlete23,mImporteIvaProv ,mImporteObra,mCNIC,mTesofe,altaAlmacen,aEjercicioFiscal,mIMDT,mImporte,nFolioPagado,OBGT)" + " VALUES('RELACIONGASTOS'," + nFolioPago + "," + nDocRenglon + "," + cMes + ",'" + cEjercicio + "','" + cIdEntidadContable + "','" + cIdRelacion + "','" + EP + "','" + cIdCuentaContable + "'," + rs3.getString("mComprometido") + "," + rs3.getString("nPoliza") + ",'" + idTipoMovimiento + "','" + idTipoConcepto + "','" + cEvento + "','" + cMes + "','" + cCentroContable + "','" + Rfc + "'," + rs3.getString("mImporteNeto") + ",'" + alm + "'," + rs3.getString("mImporteBruto") + "," + rs3.getString("mImporteMasIva") + "," + rs3.getString("mImporteIva") + ",'" + nCapitulo + "'," + rs3.getString("mSancion") + "," + rs3.getString("mDevolucion") + "," + rs3.getString("mImporteAmortiza") + " ," + rs3.getString("mRetencion") + "," + rs3.getString("mPenalizacion") + "," + rs3.getString("m2Millar") + "," + rs3.getString("m23IVA") + "," + rs3.getString("mISRHonorarios") + "," + rs3.getString("mObra5") + "       ," + rs3.getString("mImporteFlete4") + "," + rs3.getString("mISRArrenda") + "," + rs3.getString("mRetImpuestoCedular") + "," + rs3.getString("mImporteIvaArrenda") + "," + rs3.getString("mImporteIvaHonorarios") + "	," + rs3.getString("mImporteFlete23") + "," + rs3.getString("mImporteIvaProv") + "," + rs3.getString("mImporteObra") + "," + rs3.getString("mCNIC") + "," + rs3.getString("mTesofe") + ",'" + altaAlmacen + "','" + aEjercicioFiscal + "'," + rs3.getString("mIMDT") + "," + rs3.getString("mImporteNeto") + "," + strFolioPagado2[f2] + ",'" + cOBGT + "' )");
                            pstmntInsertDetPag.executeUpdate();
                        }
                        f2 = f2 + 1;
                    } else {
                        // System.out.println("No Guarda Encabezado");
                    }
                }
                // aplicacion contable
                boolean appCont = true, appContPag = true;
                AccountingEngine accEng = new AccountingEngine();
                accEng.setValidaInsuficienciaDeSaldo(true);
                // Datos Para Aplicar Ejercido
                String cTablaPadre = "tEjercidoEncabezado";
                String cTablaHija = "tEjercidoDetalle";
                String cFolio = "nFolioEjercido";
                String cTipoDocumento = "EJERCIDO";
                // Datos Para Aplicar Pagado
                String cTablaPadrePag = "tPagadoEncabezado";
                String cTablaHijaPag = "tPagadoDetalle";
                String cFolioPag = "nFolioPagado";
                String cTipoDocumentoPag = "PAGADO";
                appCont = accEng.makeAccountingApplication(conn, cTipoDocumento, "" + strFolioEjercido2[ii], cTablaPadre, cTablaHija, cFolio);
                conn.commit();
                if (strStatusSiaff.trim().equals("Pagada") && fPago.length() > 1) {
                    appContPag = accEng.makeAccountingApplication(conn, cTipoDocumentoPag, "" + strFolioPagado2[ii], cTablaPadrePag, cTablaHijaPag, cFolioPag);
                    conn.commit();
                }
                ii = ii + 1;
            }
        } catch (Exception e) {
            log.error("Error occurred", "Error: " + e);
            // log.debug(message); para desarrollo.
            try {
                conn.rollback();
            } catch (Exception ee) {
                log.warn("Error: cerrando rollback ", ee);
            }
            valor = "no_ingresado";
        } finally {
            try {
                CloseObject.closeObject(rs);
                CloseObject.closeObject(rs2);
                CloseObject.closeObject(rs3);
                CloseObject.closeObject(rs4);
                CloseObject.closeObject(rs5);
                CloseObject.closeObject(rs6);
                CloseObject.closeObject(rs7);
                CloseObject.closeObject(pstmtnSelect);
                CloseObject.closeObject(pstmntInsertEj);
                CloseObject.closeObject(pstmnSelectDet);
                CloseObject.closeObject(pstmntInsertDet);
                CloseObject.closeObject(pstmntInsertPag);
                CloseObject.closeObject(pstmntInsertDetPag);
                CloseObject.closeObject(pstmtnSicop);
                CloseObject.closeObject(conn);
            } catch (Exception e) {
                log.warn("Error: cerrando statement y/o conexion", e);
            }
        }
        return valor;
    }

    public String aplicarIntegracion(String strTabla, String idCXPIntegracion, String strCLC, String fPago, String usuario, String strFolioEjercido, String strFolioPagado, String strNomCamposDetValidar, String strStatusSiaff) throws SQLException {
        String valor = null;
        PreparedStatement pstmntHeader = null, pstmtnSelect = null, pstmntInsertEj = null, pstmnSelectDet = null, pstmntInsertDet = null, pstmntInsertPag = null, pstmntInsertDetPag = null, pstmtnSicop = null;
        ResultSet rs = null, rs2 = null, rs3 = null, rs4 = null, rs5 = null, rs6 = null, rs7 = null;
        Connection conn = null;
        valor = "ingresado";
        try {
            conn = getConnection();
            String fPagado = null;
            if (fPago.length() > 1) {
                String[] fechPagado = fPago.split("/");
                fPagado = "'" + fechPagado[2] + "-" + fechPagado[1] + "-" + fechPagado[0] + "'";
            }
            String[] fE = strFolioEjercido.split("/");
            String[] fP = strFolioPagado.split("/");
            String fechaAplicadoSicop = "";
            String fechaPagoSicop = "";
            String solicitudPago = "";
            String numProceso = "";
            String folioSiaff = "";
            String sNoContrarrecibo = "";
            java.util.Date utilDate = new java.util.Date();
            long lnMilisegundos = utilDate.getTime();
            java.sql.Date sqlDate = new java.sql.Date(lnMilisegundos);
            String[] fp = String.valueOf(sqlDate).split("-");
            String FechaPagado = fp[2] + "-" + fp[1] + "-" + fp[0];
            int nFolioPago = 0;
            // Buscar Informacion SICOP
            pstmtnSicop = conn.prepareStatement("SELECT FECHA_APL,FECHA_PAGO_106,SPAG_176,PROC_CLAVE,FOLIO_SIAFF_112 FROM CLC_SICOP WITH(NOLOCK) WHERE NCLC_43 = ? GROUP BY FECHA_APL,FECHA_PAGO_106,SPAG_176,PROC_CLAVE,FOLIO_SIAFF_112");
            pstmtnSicop.setString(1, strCLC);
            rs7 = pstmtnSicop.executeQuery();
            if (rs7.next()) {
                String[] fAS = rs7.getString("FECHA_APL").split("/");
                fechaAplicadoSicop = fAS[2] + "-" + fAS[1] + "-" + fAS[0];
                String[] fPS = rs7.getString("FECHA_PAGO_106").split("/");
                fechaPagoSicop = fPS[2] + "-" + fPS[1] + "-" + fPS[0];
                solicitudPago = rs7.getString("SPAG_176");
                numProceso = rs7.getString("PROC_CLAVE");
                folioSiaff = rs7.getString("FOLIO_SIAFF_112");
            }
            // Traer Cuentas por Pagar de tLayoutsCreadosRelacionGastosHeader
            pstmntHeader = conn.prepareStatement("SELECT l.sNoContrarrecibo AS sNoContrarrecibo, r.nFolioRELACIONGASTOS AS idFolio, r.mImporteNeto as impNeto,r.aEjercicioFiscal FROM tLayoutsCreadosRelacionGastosHeader l WITH(NOLOCK) INNER JOIN tRELACIONGASTOSEncabezado r WITH(NOLOCK) ON l.sNoContrarrecibo = r.caNoContrarrecibo WHERE sAuxiliarComodin = ? ");
            pstmntHeader.setString(1, idCXPIntegracion);
            rs = pstmntHeader.executeQuery();
            int i = 0;
            int ii = 0;
            while (rs.next()) {
                sNoContrarrecibo = rs.getString("sNoContrarrecibo");
                // Buscar Informacion Cabecera
                pstmtnSelect = conn.prepareStatement("SELECT nFolioRELACIONGASTOS AS nFolio,caNoContrarrecibo,nFolioPoliza,cTipoPoliza,U_LOGIN,isnull(nFolioPolizaCancelacion,'0') AS nFolioPolizaCancelacion,isnull(fCancelacion,NULL) AS fCancelacion,cDescripcionPoliza,cUnidadResponsableContable,fAplicacion,cRamo,aEjercicioFiscal FROM tRELACIONGASTOSEncabezado WITH(NOLOCK) WHERE caNoContrarrecibo = ? ");
                pstmtnSelect.setString(1, sNoContrarrecibo);
                rs2 = pstmtnSelect.executeQuery();
                if (rs2.next()) {
                    String recibo = rs2.getString("caNoContrarrecibo");
                    String tipoPoliza = rs2.getString("cTipoPoliza");
                    String fCancelacion = rs2.getString("fCancelacion");
                    String desPoliza = rs2.getString("cDescripcionPoliza");
                    String uniResp = rs2.getString("cUnidadResponsableContable");
                    String cRamo = rs2.getString("cRamo");
                    nFolioPago = Integer.parseInt(rs2.getString("nFolio"), 10);
                    int folioPoliza = Integer.parseInt(rs2.getString("nFolioPoliza"), 10);
                    int nFolPolCancelacion = Integer.parseInt(rs2.getString("nFolioPolizaCancelacion"), 10);
                    int aEjercicioFiscal = Integer.parseInt(rs2.getString("aEjercicioFiscal"), 10);
                    if (fCancelacion != null) {
                        fCancelacion = "'" + fCancelacion + "'";
                    } else {
                        fCancelacion = null;
                    }
                    // Guarda En Cabecera Ejercido
                    // Para Guardar 0 y Generar el Nuevo Folio.
                    folioPoliza = 0;
                    pstmntInsertEj = conn.prepareStatement("INSERT INTO tEjercidoEncabezado (cTipoPago,nFolioPAGO,caNoContrarrecibo,nFolioPoliza,cTipoPoliza,U_LOGIN,nFolioPolizaCancelacion,fCancelacion,cDescripcionPoliza,cUnidadResponsableContable,nFolioSICOP,fAplicacion,cRamo,cIdUsuarioCaptura,FechaAplicacionSicop,FechaPagoSicop,SolicitudPago,NumeroProceso,nFolioSIAFF,nFolioEjercido) " + "VALUES ('RELACIONGASTOS'," + nFolioPago + ",'" + recibo + "'," + folioPoliza + ",'DI','" + usuario + "'," + nFolPolCancelacion + "," + fCancelacion + ",'" + desPoliza + "','" + uniResp + "'," + strCLC + ",'" + fechaAplicadoSicop + "','" + cRamo + "','" + usuario + "','" + fechaAplicadoSicop + "','" + fechaPagoSicop + "'," + solicitudPago + "," + numProceso + "," + folioSiaff + "," + fE[i] + " )");
                    int intr = pstmntInsertEj.executeUpdate();
                    // Guarda En Cabecera Pagado
                    pstmntInsertPag = conn.prepareStatement("INSERT INTO tPagadoEncabezado (cTipoPago,nFolioPAGO,caNoContrarrecibo,nFolioPoliza,cTipoPoliza,U_LOGIN,nFolioPolizaCancelacion,fCancelacion,cDescripcionPoliza,cUnidadResponsableContable,nFolioSICOP,fAplicacion,cRamo,cIdUsuarioCaptura,FechaAplicacionSicop,FechaPagoSicop,SolicitudPago,NumeroProceso,nFolioSIAFF,FechaPagado,nFolioPagado) " + "VALUES ('RELACIONGASTOS'," + nFolioPago + ",'" + recibo + "'," + folioPoliza + ",'" + tipoPoliza + "','" + usuario + "'," + nFolPolCancelacion + "," + fCancelacion + ",'" + desPoliza + "','" + uniResp + "'," + strCLC + "," + fPagado + ",'" + cRamo + "','" + usuario + "','" + fechaAplicadoSicop + "','" + fechaPagoSicop + "'," + solicitudPago + "," + numProceso + "," + folioSiaff + ",'" + FechaPagado + "'," + fP[ii] + " )");
                    int intrPag = pstmntInsertPag.executeUpdate();
                    if (intr > 0 && intrPag > 0) {
                        // Buscar detalles con el nFolio de Encabezado
                        pstmnSelectDet = conn.prepareStatement("SELECT nDocRenglon,cMes,cEjercicio, EP,cIdCuentaContable,mComprometido,isnull(nPoliza,0) AS nPoliza,ID_TIPO_MOVIMIENTO,ID_TIPO_CONCEPTO,cEvento,cCentroContable,RFC,mImporteNeto,ALM,mImporteBruto,mImporteMasIva,mImporteIva,nCapitulo,mSancion,mDevolucion,mImporteAmortiza,mRetencion,mPenalizacion,m2Millar,m23IVA,mISRHonorarios,mObra5,mImporteFlete4,mISRArrenda,mRetImpuestoCedular,mImporte,mImporteIvaArrenda,mImporteIvaHonorarios,mImporteFlete23,mImporteIvaProv,mImporteObra,isnull(mCNIC,0.00) AS mCNIC,isnull(mIMDT,0.00) AS mIMDT,isnull(mTesofe,0.00) AS mTesofe,isnull(altaAlmacen,0) AS altaAlmacen,cCentroContable AS cIdEntidadContable, cIdRelacion FROM tRELACIONGASTOSDetalle WITH(NOLOCK) WHERE cEvento != 'ANTICIPO' AND nFolioRELACIONGASTOS = ? ");
                        pstmnSelectDet.setInt(1, nFolioPago);
                        rs3 = pstmnSelectDet.executeQuery();
                        while (rs3.next()) {
                            int nDocRenglon = Integer.parseInt(rs3.getString("nDocRenglon"), 10);
                            int cMes = Integer.parseInt(rs3.getString("cMes"), 10);
                            String cEjercicio = rs3.getString("cEjercicio");
                            String EP = rs3.getString("EP");
                            String cOBGT = EP.substring(31, 36);
                            String cIdCuentaContable = rs3.getString("cIdCuentaContable");
                            String cIdEntidadContable = rs3.getString("cIdEntidadContable");
                            String cIdRelacion = rs3.getString("cIdRelacion");
                            String idTipoMovimiento = rs3.getString("ID_TIPO_MOVIMIENTO");
                            String idTipoConcepto = rs3.getString("ID_TIPO_CONCEPTO");
                            String[] cEventoCa = rs3.getString("cEvento").split("_");
                            String cEvento = "P_" + cEventoCa[1];
                            String cCentroContable = rs3.getString("cCentroContable");
                            String Rfc = rs3.getString("RFC");
                            String alm = rs3.getString("ALM");
                            String nCapitulo = rs3.getString("nCapitulo");
                            String altaAlmacen = rs3.getString("altaAlmacen");
                            // Guarda Detalle Ejercido
                            // ,"+rs.getString("mIMDT")+")");
                            pstmntInsertDet = conn.prepareStatement("INSERT INTO tEjercidoDetalle (cTipoPago,nFolioPAGO,nDocRenglon,cMes,cEjercicio,cIdEntidadContable,cIdRelacion,EP,cIdCuentaContable,mComprometido,nPoliza,ID_TIPO_MOVIMIENTO,ID_TIPO_CONCEPTO,cEvento,nMes,cCentroContable,RFC, mImporteNeto,ALM,mImporteBruto,mImporteMasIva,mImporteIva,nCapitulo,mSancion,mDevolucion,mImporteAmortiza,mRetencion,mPenalizacion,m2Millar,m23IVA,mISRHonorarios,mObra5,mImporteFlete4,mISRArrenda,mRetImpuestoCedular,mImporteIvaArrenda,mImporteIvaHonorarios ,mImporteFlete23,mImporteIvaProv ,mImporteObra,mCNIC,mTesofe,altaAlmacen,aEjercicioFiscal,mIMDT,mImporte,nFolioEjercido)" + " VALUES('RELACIONGASTOS'," + nFolioPago + "," + nDocRenglon + "," + cMes + ",'" + cEjercicio + "','" + cIdEntidadContable + "','" + cIdRelacion + "','" + EP + "','" + cIdCuentaContable + "'," + rs3.getString("mComprometido") + "," + rs3.getString("nPoliza") + ",'" + idTipoMovimiento + "','" + idTipoConcepto + "','EJERCIDO','" + cMes + "','" + cCentroContable + "','" + Rfc + "'," + rs3.getString("mImporteNeto") + ",'" + alm + "'," + rs3.getString("mImporteBruto") + "," + rs3.getString("mImporteMasIva") + "," + rs3.getString("mImporteIva") + ",'" + nCapitulo + "'," + rs3.getString("mSancion") + "," + rs3.getString("mDevolucion") + "," + rs3.getString("mImporteAmortiza") + " ," + rs3.getString("mRetencion") + "," + rs3.getString("mPenalizacion") + "," + rs3.getString("m2Millar") + "," + rs3.getString("m23IVA") + "," + rs3.getString("mISRHonorarios") + "," + rs3.getString("mObra5") + "       ," + rs3.getString("mImporteFlete4") + "," + rs3.getString("mISRArrenda") + "," + rs3.getString("mRetImpuestoCedular") + "," + rs3.getString("mImporteIvaArrenda") + "," + rs3.getString("mImporteIvaHonorarios") + "	," + rs3.getString("mImporteFlete23") + "," + rs3.getString("mImporteIvaProv") + "," + rs3.getString("mImporteObra") + "," + rs3.getString("mCNIC") + "," + rs3.getString("mTesofe") + ",'" + altaAlmacen + "','" + aEjercicioFiscal + "'," + rs3.getString("mIMDT") + "," + rs3.getString("mImporteNeto") + "," + fE[i] + " )");
                            int intDet = pstmntInsertDet.executeUpdate();
                            log.trace("Object: {}", "Se insertaron " + intDet + " registros");
                            // Guarda Detalle Pagado
                            // ,"+rs.getString("mIMDT")+")");
                            pstmntInsertDetPag = conn.prepareStatement("INSERT INTO tPagadoDetalle (cTipoPago,nFolioPAGO,nDocRenglon,cMes,cEjercicio,cIdEntidadContable,cIdRelacion,EP,cIdCuentaContable,mComprometido,nPoliza,ID_TIPO_MOVIMIENTO,ID_TIPO_CONCEPTO,cEvento,nMes,cCentroContable,RFC, mImporteNeto,ALM,mImporteBruto,mImporteMasIva,mImporteIva,nCapitulo,mSancion,mDevolucion,mImporteAmortiza,mRetencion,mPenalizacion,m2Millar,m23IVA,mISRHonorarios,mObra5    ,mImporteFlete4,mISRArrenda,mRetImpuestoCedular,mImporteIvaArrenda,mImporteIvaHonorarios ,mImporteFlete23,mImporteIvaProv ,mImporteObra,mCNIC,mTesofe,altaAlmacen,aEjercicioFiscal,mIMDT,mImporte,nFolioPagado,OBGT,CTAB)" + " VALUES('RELACIONGASTOS'," + nFolioPago + "," + nDocRenglon + "," + cMes + ",'" + cEjercicio + "','" + cIdEntidadContable + "','" + cIdRelacion + "','" + EP + "','" + cIdCuentaContable + "'," + rs3.getString("mComprometido") + "," + rs3.getString("nPoliza") + ",'" + idTipoMovimiento + "','" + idTipoConcepto + "','" + cEvento + "','" + cMes + "','" + cCentroContable + "','" + Rfc + "'," + rs3.getString("mImporteNeto") + ",'" + alm + "'," + rs3.getString("mImporteBruto") + "," + rs3.getString("mImporteMasIva") + "," + rs3.getString("mImporteIva") + ",'" + nCapitulo + "'," + rs3.getString("mSancion") + "," + rs3.getString("mDevolucion") + "," + rs3.getString("mImporteAmortiza") + " ," + rs3.getString("mRetencion") + "," + rs3.getString("mPenalizacion") + "," + rs3.getString("m2Millar") + "," + rs3.getString("m23IVA") + "," + rs3.getString("mISRHonorarios") + "," + rs3.getString("mObra5") + "       ," + rs3.getString("mImporteFlete4") + "," + rs3.getString("mISRArrenda") + "," + rs3.getString("mRetImpuestoCedular") + "," + rs3.getString("mImporteIvaArrenda") + "," + rs3.getString("mImporteIvaHonorarios") + "	," + rs3.getString("mImporteFlete23") + "," + rs3.getString("mImporteIvaProv") + "," + rs3.getString("mImporteObra") + "," + rs3.getString("mCNIC") + "," + rs3.getString("mTesofe") + ",'" + altaAlmacen + "','" + aEjercicioFiscal + "'," + rs3.getString("mIMDT") + "," + rs3.getString("mImporteNeto") + "," + fP[ii] + ",'" + cOBGT + "','000000000' )");
                            pstmntInsertDetPag.executeUpdate();
                        }
                        // try {
                        AccountingEngine accEng = new AccountingEngine();
                        accEng.setValidaInsuficienciaDeSaldo(true);
                        // Datos Para Aplicar Ejercido
                        String cTablaPadre = "tEjercidoEncabezado";
                        String cTablaHija = "tEjercidoDetalle";
                        String cFolio = "nFolioEjercido";
                        String cTipoDocumento = "EJERCIDO";
                        // Datos Para Aplicar Pagado
                        String cTablaPadrePag = "tPagadoEncabezado";
                        String cTablaHijaPag = "tPagadoDetalle";
                        String cFolioPag = "nFolioPagado";
                        String cTipoDocumentoPag = "PAGADO";
                        strFolioEjercido = fE[i];
                        strFolioPagado = fP[ii];
                        accEng.makeAccountingApplication(conn, cTipoDocumento, strFolioEjercido, cTablaPadre, cTablaHija, cFolio);
                        conn.commit();
                        if (strStatusSiaff.trim().equals("Pagada") && fPago.length() > 1) {
                            accEng.makeAccountingApplication(conn, cTipoDocumentoPag, strFolioPagado, cTablaPadrePag, cTablaHijaPag, cFolioPag);
                            conn.commit();
                        }
                    }
                }
                i = i + 1;
                ii = i + 1;
            }
        } catch (Exception e) {
            log.error("Error occurred", "Error: " + e);
            valor = "no_ingresado";
            try {
                conn.rollback();
            } catch (Exception ee) {
                log.warn("Error en rollback", ee);
            }
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(rs2);
            CloseObject.closeObject(rs3);
            CloseObject.closeObject(rs4);
            CloseObject.closeObject(rs5);
            CloseObject.closeObject(rs6);
            CloseObject.closeObject(rs7);
            CloseObject.closeObject(pstmtnSelect);
            CloseObject.closeObject(pstmntInsertEj);
            CloseObject.closeObject(pstmnSelectDet);
            CloseObject.closeObject(pstmntInsertDet);
            CloseObject.closeObject(pstmntInsertPag);
            CloseObject.closeObject(pstmntInsertDetPag);
            CloseObject.closeObject(pstmtnSicop);
            CloseObject.closeObject(conn);
        }
        return valor;
    }

    @SuppressWarnings("resource")
    public String aplicarManualmenteRGProveedorIP(String idCXP, String tablaEncabezado, String tablaDetalle, String fPago, String usuario, String strFolioEjercido, String strFolioPagado, String strCuenta, String strAccion) throws SQLException {
        String valor = "";
        PreparedStatement pstmtnSelect = null, pstmntInsertEj = null, pstmnSelectDet = null, pstmntInsertDet = null, pstmntInsertPag = null, pstmntInsertDetPag = null, updateFechaAplicacion = null, pstUpdateFolioSicop = null;
        ResultSet rs = null, rs3 = null, rs8 = null;
        Connection conn = null;
        int nFolioPago = 0;
        String nFolioSicop = null;
        String solicitudPago = null;
        String numProceso = null;
        String folioSiaff = null;
        try {
            conn = getConnection();
            String FechaPagado = fPago;
            pstmtnSelect = conn.prepareStatement("SELECT nFolioRELACIONGASTOS AS nFolio,caNoContrarrecibo,nFolioPoliza,cTipoPoliza,U_LOGIN,isnull(nFolioPolizaCancelacion,'0') AS nFolioPolizaCancelacion,isnull(fCancelacion,NULL) AS fCancelacion,cDescripcionPoliza,cUnidadResponsableContable,fAplicacion,cRamo,aEjercicioFiscal, cUnidadResponsable FROM " + tablaEncabezado + " WITH(NOLOCK) WHERE caNoContrarrecibo = ? ");
            pstmtnSelect.setString(1, idCXP);
            rs = pstmtnSelect.executeQuery();
            if (rs.next()) {
                String recibo = rs.getString("caNoContrarrecibo");
                String desPoliza = rs.getString("cDescripcionPoliza");
                String uniResp = rs.getString("cUnidadResponsableContable");
                String cRamo = rs.getString("cRamo");
                String ur = rs.getString("cUnidadResponsable");
                nFolioPago = Integer.parseInt(rs.getString("nFolio"), 10);
                // porque
                int folioPoliza = Integer.parseInt(rs.getString("nFolioPoliza"), 10);
                // pago??
                int nFolPolCancelacion = Integer.parseInt(rs.getString("nFolioPolizaCancelacion"), 10);
                int aEjercicioFiscal = Integer.parseInt(rs.getString("aEjercicioFiscal"), 10);
                // Guarda En Cabecera Ejercido
                // Para Guardar 0 y Generar el Nuevo Folio.
                folioPoliza = 0;
                // Accion 1 para insertar registros de ejercido y pagado
                if (strAccion.equals("1")) {
                    pstUpdateFolioSicop = conn.prepareStatement("SELECT TOP 1 SICOP.NCTR_47, SICOP.NCLC_43 nFolioSicop, SICOP.SPAG_176 SolicitudPago, SICOP.PROC_CLAVE NumeroProceso, SICOP.FOLIO_SIAFF_112 nFolioSIAFF " + "FROM CLC_SICOP SICOP WITH(NOLOCK), CLC_SIAFF_ENC SIAFF WITH(NOLOCK) " + "WHERE SICOP.FOLIO_SIAFF_112=SIAFF.FOLIO_CLC AND NCTR_47 = dbo.fn_IntegracionRG( ? ) " + "GROUP BY SICOP.NCTR_47, SICOP.NCLC_43, SICOP.SPAG_176, SICOP.PROC_CLAVE, SICOP.FOLIO_SIAFF_112");
                    pstUpdateFolioSicop.setString(1, idCXP);
                    rs8 = pstUpdateFolioSicop.executeQuery();
                    if (rs8.next()) {
                        nFolioSicop = rs8.getString("nFolioSicop");
                        solicitudPago = rs8.getString("SolicitudPago");
                        numProceso = rs8.getString("NumeroProceso");
                        folioSiaff = rs8.getString("nFolioSIAFF");
                    } else {
                        // SI NO SE ENCUENTYRA EN ARCHHIVOS DE SICOP VALIDA SI
                        // ES RG DE RADICADO PONE EL -1 EN LOS FOLIOS
                        pstUpdateFolioSicop = conn.prepareStatement("SELECT  enc.caNoContrarrecibo NCTR_47, '-1' nFolioSicop, '-1' SolicitudPago, '-1' NumeroProceso, '-1' nFolioSIAFF " + "FROM tRELACIONGASTOSEncabezado enc WITH ( NOLOCK ) " + "WHERE caNoContrarrecibo = ? " + "AND cDocumentoHaplicado = 'S' " + "AND cRadicado = 'S'");
                        pstUpdateFolioSicop.setString(1, idCXP);
                        rs8 = pstUpdateFolioSicop.executeQuery();
                        if (rs8.next()) {
                            nFolioSicop = rs8.getString("nFolioSicop");
                            solicitudPago = rs8.getString("SolicitudPago");
                            numProceso = rs8.getString("NumeroProceso");
                            folioSiaff = rs8.getString("nFolioSIAFF");
                        }
                    }
                    pstmntInsertEj = conn.prepareStatement("INSERT INTO tEjercidoEncabezado (cTipoPago,nFolioPAGO,caNoContrarrecibo,nFolioPoliza,cTipoPoliza,U_LOGIN,nFolioPolizaCancelacion,cDescripcionPoliza,cUnidadResponsableContable,fAplicacion,cRamo,cIdUsuarioCaptura,FechaAplicacionSicop,FechaPagoSicop,nFolioEjercido,SolicitudPago,nFolioSICOP,NumeroProceso,nFolioSIAFF) " + "VALUES ('RELACIONGASTOS'," + nFolioPago + ",'" + recibo + "'," + folioPoliza + ",'DI','" + usuario + "'," + folioPoliza + ",'" + desPoliza + "','" + uniResp + "','" + FechaPagado + "','" + cRamo + "','" + usuario + "','" + FechaPagado + "','" + FechaPagado + "'," + strFolioEjercido + "," + solicitudPago + "," + nFolioSicop + "," + numProceso + "," + folioSiaff + ")");
                    int intr = pstmntInsertEj.executeUpdate();
                    // Guarda En Cabecera Pagado
                    pstmntInsertPag = conn.prepareStatement("INSERT INTO tPagadoEncabezado (cTipoPago,nFolioPAGO,caNoContrarrecibo,nFolioPoliza,cTipoPoliza,U_LOGIN,nFolioPolizaCancelacion,cDescripcionPoliza,cUnidadResponsableContable,fAplicacion,cRamo,cIdUsuarioCaptura,FechaAplicacionSicop,FechaPagoSicop,SolicitudPago,NumeroProceso,nFolioSIAFF,nFolioSICOP,FechaPagado,nFolioPagado) " + "VALUES ('RELACIONGASTOS'," + nFolioPago + ",'" + recibo + "'," + folioPoliza + ",'EG','" + usuario + "'," + folioPoliza + ",'" + desPoliza + "','" + uniResp + "','" + FechaPagado + "','" + cRamo + "','" + usuario + "','" + FechaPagado + "','" + FechaPagado + "'," + solicitudPago + "," + numProceso + "," + folioSiaff + "," + nFolioSicop + ",'" + FechaPagado + "'," + strFolioPagado + " )");
                    int intrPag = pstmntInsertPag.executeUpdate();
                    if (intr > 0 && intrPag > 0) {
                        // Buscar detalles con el nFolio de Encabezado
                        pstmnSelectDet = conn.prepareStatement("SELECT nDocRenglon,cMes,cEjercicio, EP,cIdCuentaContable,mComprometido,isnull(nPoliza,0) AS nPoliza,ID_TIPO_MOVIMIENTO,ID_TIPO_CONCEPTO,cEvento,cCentroContable,RFC,mImporteNeto,ALM,mImporteBruto,mImporteMasIva,mImporteIva,nCapitulo,mSancion,mDevolucion,mImporteAmortiza,mRetencion,mPenalizacion,m2Millar,m23IVA,mISRHonorarios,mObra5,mImporteFlete4,mISRArrenda,mRetImpuestoCedular,mImporte,mImporteIvaArrenda,mImporteIvaHonorarios,mImporteFlete23,mImporteIvaProv,mImporteObra,isnull(mCNIC,0.00) AS mCNIC,isnull(mIMDT,0.00) AS mIMDT,isnull(mTesofe,0.00) AS mTesofe,isnull(altaAlmacen,0) AS altaAlmacen,cCentroContable AS cIdEntidadContable, cIdRelacion, (SELECT TOP 1 sCUENTA_BANCARIA FROM tLayoutsCreadosRelacionGastosHeader WITH(NOLOCK) WHERE sNoContrarrecibo = '" + recibo + "' ORDER BY Id DESC)ctab FROM tRELACIONGASTOSDetalle WITH(NOLOCK) WHERE cEvento != 'ANTICIPO' AND nFolioRELACIONGASTOS = ? ");
                        pstmnSelectDet.setInt(1, nFolioPago);
                        rs3 = pstmnSelectDet.executeQuery();
                        while (rs3.next()) {
                            int nDocRenglon = Integer.parseInt(rs3.getString("nDocRenglon"), 10);
                            int cMes = Integer.parseInt(rs3.getString("cMes"), 10);
                            String cEjercicio = rs3.getString("cEjercicio");
                            String EP = rs3.getString("EP");
                            String cOBGT = EP.substring(31, 36);
                            String cIdCuentaContable = rs3.getString("cIdCuentaContable");
                            String cIdEntidadContable = rs3.getString("cIdEntidadContable");
                            String cIdRelacion = rs3.getString("cIdRelacion");
                            String idTipoMovimiento = rs3.getString("ID_TIPO_MOVIMIENTO");
                            String idTipoConcepto = rs3.getString("ID_TIPO_CONCEPTO");
                            String[] cEventoCa = rs3.getString("cEvento").split("_");
                            String cEvento = "P_" + cEventoCa[1];
                            String cCentroContable = rs3.getString("cCentroContable");
                            String Rfc = rs3.getString("RFC");
                            String alm = rs3.getString("ALM");
                            String nCapitulo = rs3.getString("nCapitulo");
                            String altaAlmacen = rs3.getString("altaAlmacen");
                            // Guarda Detalle Ejercido
                            pstmntInsertDet = conn.prepareStatement("INSERT INTO tEjercidoDetalle (cTipoPago,nFolioPAGO,nDocRenglon,cMes,cEjercicio,cIdEntidadContable,cIdRelacion,EP,cIdCuentaContable,mComprometido,nPoliza,ID_TIPO_MOVIMIENTO,ID_TIPO_CONCEPTO,cEvento,nMes,cCentroContable,RFC, mImporteNeto,ALM,mImporteBruto,mImporteMasIva,mImporteIva,nCapitulo,mSancion,mDevolucion,mImporteAmortiza,mRetencion,mPenalizacion,m2Millar,m23IVA,mISRHonorarios,mObra5,mImporteFlete4,mISRArrenda,mRetImpuestoCedular,mImporteIvaArrenda,mImporteIvaHonorarios ,mImporteFlete23,mImporteIvaProv ,mImporteObra,mCNIC,mTesofe,altaAlmacen,aEjercicioFiscal,mIMDT,mImporte,nFolioEjercido, cUnidadResponsable)" + " VALUES('RELACIONGASTOS'," + nFolioPago + "," + nDocRenglon + "," + cMes + ",'" + cEjercicio + "','" + cIdEntidadContable + "','" + cIdRelacion + "','" + EP + "','" + cIdCuentaContable + "'," + rs3.getString("mComprometido") + "," + rs3.getString("nPoliza") + ",'" + idTipoMovimiento + "','" + idTipoConcepto + "','EJERCIDO','" + cMes + "','" + cCentroContable + "','" + Rfc + "'," + rs3.getString("mImporteNeto") + ",'" + alm + "'," + rs3.getString("mImporteBruto") + "," + rs3.getString("mImporteMasIva") + "," + rs3.getString("mImporteIva") + ",'" + nCapitulo + "'," + rs3.getString("mSancion") + "," + rs3.getString("mDevolucion") + "," + rs3.getString("mImporteAmortiza") + " ," + rs3.getString("mRetencion") + "," + rs3.getString("mPenalizacion") + "," + rs3.getString("m2Millar") + "," + rs3.getString("m23IVA") + "," + rs3.getString("mISRHonorarios") + "," + rs3.getString("mObra5") + "       ," + rs3.getString("mImporteFlete4") + "," + rs3.getString("mISRArrenda") + "," + rs3.getString("mRetImpuestoCedular") + "," + rs3.getString("mImporteIvaArrenda") + "," + rs3.getString("mImporteIvaHonorarios") + "	," + rs3.getString("mImporteFlete23") + "," + rs3.getString("mImporteIvaProv") + "," + rs3.getString("mImporteObra") + "," + rs3.getString("mCNIC") + "," + rs3.getString("mTesofe") + ",'" + altaAlmacen + "','" + aEjercicioFiscal + "'," + rs3.getString("mIMDT") + "," + rs3.getString("mImporteNeto") + "," + strFolioEjercido + ", " + ur + " )");
                            pstmntInsertDet.executeUpdate();
                            // Guarda Detalle Pagado
                            pstmntInsertDetPag = conn.prepareStatement("INSERT INTO tPagadoDetalle (cTipoPago,nFolioPAGO,nDocRenglon,cMes,cEjercicio,cIdEntidadContable,cIdRelacion,EP,cIdCuentaContable,mComprometido,nPoliza,ID_TIPO_MOVIMIENTO,ID_TIPO_CONCEPTO,cEvento,nMes,cCentroContable,RFC, mImporteNeto,ALM,mImporteBruto,mImporteMasIva,mImporteIva,nCapitulo,mSancion,mDevolucion,mImporteAmortiza,mRetencion,mPenalizacion,m2Millar,m23IVA,mISRHonorarios,mObra5    ,mImporteFlete4,mISRArrenda,mRetImpuestoCedular,mImporteIvaArrenda,mImporteIvaHonorarios ,mImporteFlete23,mImporteIvaProv ,mImporteObra,mCNIC,mTesofe,altaAlmacen,aEjercicioFiscal,mIMDT,mImporte,nFolioPagado,OBGT,ctab, cUnidadResponsable)" + " VALUES('RELACIONGASTOS'," + nFolioPago + "," + nDocRenglon + "," + cMes + ",'" + cEjercicio + "','" + cIdEntidadContable + "','" + cIdRelacion + "','" + EP + "','" + cIdCuentaContable + "'," + rs3.getString("mComprometido") + "," + rs3.getString("nPoliza") + ",'" + idTipoMovimiento + "','" + idTipoConcepto + "','" + cEvento + "','" + cMes + "','" + cCentroContable + "','" + Rfc + "'," + rs3.getString("mImporteNeto") + ",'" + alm + "'," + rs3.getString("mImporteBruto") + "," + rs3.getString("mImporteMasIva") + "," + rs3.getString("mImporteIva") + ",'" + nCapitulo + "'," + rs3.getString("mSancion") + "," + rs3.getString("mDevolucion") + "," + rs3.getString("mImporteAmortiza") + " ," + rs3.getString("mRetencion") + "," + rs3.getString("mPenalizacion") + "," + rs3.getString("m2Millar") + "," + rs3.getString("m23IVA") + "," + rs3.getString("mISRHonorarios") + "," + rs3.getString("mObra5") + "       ," + rs3.getString("mImporteFlete4") + "," + rs3.getString("mISRArrenda") + "," + rs3.getString("mRetImpuestoCedular") + "," + rs3.getString("mImporteIvaArrenda") + "," + rs3.getString("mImporteIvaHonorarios") + "	," + rs3.getString("mImporteFlete23") + "," + rs3.getString("mImporteIvaProv") + "," + rs3.getString("mImporteObra") + "," + rs3.getString("mCNIC") + "," + rs3.getString("mTesofe") + ",'" + altaAlmacen + "','" + aEjercicioFiscal + "'," + rs3.getString("mIMDT") + "," + rs3.getString("mImporteNeto") + "," + strFolioPagado + ",'" + cOBGT + "','" + strCuenta + "," + ur + "')");
                            pstmntInsertDetPag.executeUpdate();
                        }
                    }
                }
            }
            try {
                AccountingEngine accEng = new AccountingEngine();
                accEng.setValidaInsuficienciaDeSaldo(true);
                // Datos Para Aplicar Ejercido
                String cTablaPadre = "tEjercidoEncabezado";
                String cTablaHija = "tEjercidoDetalle";
                String cFolio = "nFolioEjercido";
                String cTipoDocumento = "EJERCIDO";
                // Datos Para Aplicar Pagado
                String cTablaPadrePag = "tPagadoEncabezado";
                String cTablaHijaPag = "tPagadoDetalle";
                String cFolioPag = "nFolioPagado";
                String cTipoDocumentoPag = "PAGADO";
                if (strAccion.equals("1") || strAccion.equals("3")) {
                    updateFechaAplicacion = conn.prepareStatement("UPDATE tEjercidoEncabezado SET fAplicacion = ?  WHERE nFolioEjercido = ?");
                    updateFechaAplicacion.setString(1, FechaPagado);
                    updateFechaAplicacion.setString(2, strFolioEjercido);
                    updateFechaAplicacion.executeUpdate();
                    accEng.makeAccountingApplication(conn, cTipoDocumento, strFolioEjercido, cTablaPadre, cTablaHija, cFolio);
                    conn.commit();
                }
                updateFechaAplicacion = conn.prepareStatement("UPDATE tPagadoEncabezado SET fAplicacion = ?  WHERE nFolioPagado = ?");
                updateFechaAplicacion.setString(1, FechaPagado);
                updateFechaAplicacion.setString(2, strFolioPagado);
                updateFechaAplicacion.executeUpdate();
                accEng.makeAccountingApplication(conn, cTipoDocumentoPag, strFolioPagado, cTablaPadrePag, cTablaHijaPag, cFolioPag);
                conn.commit();
                valor = "ingresado";
            } catch (Exception ex) {
                valor = ex.toString();
                log.warn(ex.getMessage(), ex);
                log.debug("Object: " + String.valueOf(ex.getLocalizedMessage()));
                try {
                    conn.rollback();
                } catch (Exception e) {
                    log.warn("Error en rollback", e);
                }
            }
        } catch (Exception e) {
            log.error("Error occurred", "Error: " + e);
            try {
                conn.rollback();
            } catch (Exception ee) {
                log.warn("Error: cerrando rollback ", ee);
            }
            valor = "no_ingresado";
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(rs3);
            CloseObject.closeObject(rs8);
            CloseObject.closeObject(pstmtnSelect);
            CloseObject.closeObject(pstmntInsertEj);
            CloseObject.closeObject(pstmnSelectDet);
            CloseObject.closeObject(pstmntInsertDet);
            CloseObject.closeObject(pstmntInsertPag);
            CloseObject.closeObject(pstmntInsertDetPag);
            CloseObject.closeObject(updateFechaAplicacion);
            CloseObject.closeObject(pstUpdateFolioSicop);
            CloseObject.closeObject(conn);
        }
        return valor;
    }

    @SuppressWarnings("resource")
    public String aplicarManualmenteRGProveedorIP_Nuevo(String idCXP, String fPago, String usuario, String strCuenta, boolean generaPoliza) throws SQLException {
        String valor = "";
        PreparedStatement pstmtnSelect = null, pstmntInsertEj = null, pstmnSelectDet = null, pstmntInsertDet = null, pstmntInsertPag = null, pstmntInsertDetPag = null, updateFechaAplicacion = null, pstUpdateFolioSicop = null, pstmUpEjercido = null, pstmUpPagado = null, pstmSeqEjercido = null, pstmExisteEncabezado = null;
        ResultSet rs = null, rs3 = null, rs4 = null, rs8 = null, rsExisteEncabezado = null;
        Connection conn = null;
        int nFolioPago = 0;
        String nFolioSicop = null;
        String solicitudPago = null;
        String numProceso = null;
        String folioSiaff = null;
        String strFolioEjercido = null;
        String strFolioPagado = null;
        try {
            conn = getConnection();
            String FechaPagado = fPago;
            int ejercicioFiscal = EjercicioFiscalManager.getEjercicioFiscal(conn).getEjercicio();
            int anioAplicacion = Util.getYearFromDate(FechaPagado);
            String fechaAplicacion = (ejercicioFiscal == anioAplicacion ? fPago : "31/12/" + ejercicioFiscal);
            String cTipoPoliza = "EG";
            /* VGC21051222 Se agrega la UE que se requiere para la Pol Man */
            pstmtnSelect = conn.prepareStatement("SELECT nFolioRELACIONGASTOS AS nFolio,caNoContrarrecibo,nFolioPoliza,cTipoPoliza,U_LOGIN,isnull(nFolioPolizaCancelacion,'0') AS nFolioPolizaCancelacion,isnull(fCancelacion,NULL) AS fCancelacion,cDescripcionPoliza,cUnidadResponsableContable,fAplicacion,cRamo,aEjercicioFiscal, cUnidadResponsable, ID_DESTINO_GASTO FROM tRELACIONGASTOSEncabezado WITH(NOLOCK) WHERE nFolioRELACIONGASTOS IN (" + idCXP + ") ");
            rs = pstmtnSelect.executeQuery();
            while (rs.next()) {
                String recibo = rs.getString("caNoContrarrecibo");
                String desPoliza = rs.getString("cDescripcionPoliza");
                String uniResp = rs.getString("cUnidadResponsableContable");
                String cRamo = rs.getString("cRamo");
                String cUE = rs.getString("cUnidadResponsable");
                String ID_DESTINO_GASTO = rs.getString("ID_DESTINO_GASTO");
                nFolioPago = Integer.parseInt(rs.getString("nFolio"), 10);
                int folioPoliza = Integer.parseInt(rs.getString("nFolioPoliza"), 10);
                int aEjercicioFiscal = Integer.parseInt(rs.getString("aEjercicioFiscal"), 10);
                // Guarda En Cabecera Ejercido
                // Para Guardar 0 y Generar el Nuevo Folio.
                folioPoliza = 0;
                pstUpdateFolioSicop = conn.prepareStatement("SELECT TOP 1 SICOP.NCTR_47, SICOP.NCLC_43 nFolioSicop, SICOP.SPAG_176 SolicitudPago, SICOP.PROC_CLAVE NumeroProceso, SICOP.FOLIO_SIAFF_112 nFolioSIAFF " + "FROM CLC_SICOP SICOP WITH(NOLOCK), CLC_SIAFF_ENC SIAFF WITH(NOLOCK) " + "WHERE SICOP.FOLIO_SIAFF_112=SIAFF.FOLIO_CLC AND NCTR_47 = dbo.fn_IntegracionRG( ? ) " + "GROUP BY SICOP.NCTR_47, SICOP.NCLC_43, SICOP.SPAG_176, SICOP.PROC_CLAVE, SICOP.FOLIO_SIAFF_112");
                pstUpdateFolioSicop.setString(1, recibo);
                rs8 = pstUpdateFolioSicop.executeQuery();
                if (rs8.next()) {
                    nFolioSicop = rs8.getString("nFolioSicop");
                    solicitudPago = rs8.getString("SolicitudPago");
                    numProceso = rs8.getString("NumeroProceso");
                    folioSiaff = rs8.getString("nFolioSIAFF");
                } else {
                    // SI NO SE ENCUENTRA EN ARCHIVOS DE SICOP VALIDA SI ES RG
                    // DE RADICADO PONE EL -1 EN LOS FOLIOS
                    pstUpdateFolioSicop = conn.prepareStatement("SELECT  enc.caNoContrarrecibo NCTR_47, '-1' nFolioSicop, '-1' SolicitudPago, '-1' NumeroProceso, '-1' nFolioSIAFF " + "FROM tRELACIONGASTOSEncabezado enc WITH ( NOLOCK ) " + "WHERE caNoContrarrecibo = ? " + "AND cDocumentoHaplicado = 'S' " + "AND cRadicado = 'S'");
                    pstUpdateFolioSicop.setString(1, recibo);
                    rs8 = pstUpdateFolioSicop.executeQuery();
                    if (rs8.next()) {
                        nFolioSicop = rs8.getString("nFolioSicop");
                        solicitudPago = rs8.getString("SolicitudPago");
                        numProceso = rs8.getString("NumeroProceso");
                        folioSiaff = rs8.getString("nFolioSIAFF");
                    }
                }
                pstmExisteEncabezado = conn.prepareStatement("SELECT 'existe' existe, ISNULL(cDocumentoHaplicado,'N')aplicado, nFolioejercido FROM dbo.tEjercidoEncabezado WHERE caNoContrarrecibo=?");
                pstmExisteEncabezado.setString(1, recibo);
                rsExisteEncabezado = pstmExisteEncabezado.executeQuery();
                int intr = 0;
                int intrPag = 0;
                String ejercidoApl = "N";
                String pagadoApl = "N";
                if (rsExisteEncabezado.next()) {
                    ejercidoApl = rsExisteEncabezado.getString("aplicado");
                    intr = 1;
                    strFolioEjercido = rsExisteEncabezado.getString("nFolioejercido");
                } else {
                    // OBTENER SEQUENCE DE EJERCIDO
                    pstmUpEjercido = conn.prepareStatement("UPDATE CF_SEQUENCE  WITH (ROWLOCK) SET seq_value = seq_value + 1  WHERE seq_name = 'EJERCIDO' ");
                    pstmUpEjercido.executeUpdate();
                    pstmSeqEjercido = conn.prepareStatement("SELECT seq_value FROM CF_SEQUENCE WITH(NOLOCK) WHERE seq_name = 'EJERCIDO' ");
                    rs3 = pstmSeqEjercido.executeQuery();
                    if (rs3.next()) {
                        strFolioEjercido = rs3.getString("seq_value");
                    }
                    pstmntInsertEj = conn.prepareStatement("INSERT INTO tEjercidoEncabezado (cTipoPago,nFolioPAGO,caNoContrarrecibo,nFolioPoliza,cTipoPoliza,U_LOGIN," + "                                 nFolioPolizaCancelacion,cDescripcionPoliza,cUnidadResponsableContable," + "                                 fAplicacion,cRamo,cIdUsuarioCaptura,FechaAplicacionSicop,FechaPagoSicop,nFolioEjercido,SolicitudPago,nFolioSICOP,NumeroProceso,nFolioSIAFF) " + "VALUES ('RELACIONGASTOS'," + nFolioPago + ",'" + recibo + "'," + folioPoliza + ",'DI','" + usuario + "'" + "        ," + folioPoliza + ",'" + desPoliza + "','" + uniResp + "','" + fechaAplicacion + "','" + cRamo + "','" + usuario + "','" + FechaPagado + "','" + FechaPagado + "'," + strFolioEjercido + "," + solicitudPago + "," + nFolioSicop + "," + numProceso + "," + folioSiaff + ")");
                    intr = pstmntInsertEj.executeUpdate();
                }
                pstmExisteEncabezado = conn.prepareStatement("SELECT 'existe' existe, ISNULL(cDocumentoHaplicado,'N')aplicado, nFolioPagado FROM dbo.tPagadoEncabezado WHERE caNoContrarrecibo=?");
                pstmExisteEncabezado.setString(1, recibo);
                rsExisteEncabezado = pstmExisteEncabezado.executeQuery();
                if (rsExisteEncabezado.next()) {
                    pagadoApl = rsExisteEncabezado.getString("aplicado");
                    intrPag = 1;
                    strFolioPagado = rsExisteEncabezado.getString("nFolioPagado");
                } else {
                    // OBTENER SEQUENCE DE PAGADO
                    pstmUpPagado = conn.prepareStatement("UPDATE CF_SEQUENCE WITH (ROWLOCK)  SET seq_value = seq_value + 1 WHERE seq_name = 'PAGADO' ");
                    pstmUpPagado.executeUpdate();
                    pstmSeqEjercido = conn.prepareStatement("SELECT seq_value FROM CF_SEQUENCE WITH(NOLOCK) WHERE seq_name = 'PAGADO' ");
                    rs4 = pstmSeqEjercido.executeQuery();
                    if (rs4.next()) {
                        strFolioPagado = rs4.getString("seq_value");
                    }
                    if (ejercicioFiscal != anioAplicacion) {
                        String esComprobacion = EjercidoPagadoValidarVarios.esComprobacion(conn, recibo);
                        if ("NO".equalsIgnoreCase(esComprobacion))
                            cTipoPoliza = "DI";
                    }
                    pstmntInsertPag = conn.prepareStatement("INSERT INTO tPagadoEncabezado (cTipoPago,nFolioPAGO,caNoContrarrecibo,nFolioPoliza,cTipoPoliza,U_LOGIN,nFolioPolizaCancelacion,cDescripcionPoliza,cUnidadResponsableContable,fAplicacion,cRamo,cIdUsuarioCaptura,FechaAplicacionSicop,FechaPagoSicop,SolicitudPago,NumeroProceso,nFolioSIAFF,nFolioSICOP,FechaPagado,nFolioPagado) " + "VALUES ('RELACIONGASTOS'," + nFolioPago + ",'" + recibo + "'," + folioPoliza + ",'" + cTipoPoliza + "','" + usuario + "'," + folioPoliza + ",'" + desPoliza + "','" + uniResp + "','" + fechaAplicacion + "','" + cRamo + "','" + usuario + "','" + FechaPagado + "','" + FechaPagado + "'," + solicitudPago + "," + numProceso + "," + folioSiaff + "," + nFolioSicop + ",'" + FechaPagado + "'," + strFolioPagado + " )");
                    intrPag = pstmntInsertPag.executeUpdate();
                }
                if (intr > 0 && intrPag > 0) {
                    if ("NORE".equals(ID_DESTINO_GASTO) || "CQRE".equals(ID_DESTINO_GASTO) || "CLRE".equals(ID_DESTINO_GASTO) || "GLRE".equals(ID_DESTINO_GASTO)) {
                        pstmnSelectDet = conn.prepareStatement("SELECT nDocRenglon,cMes,cEjercicio, EP,cIdCuentaContable,mComprometido,isnull(nPoliza,0) AS nPoliza,ID_TIPO_MOVIMIENTO,ID_TIPO_CONCEPTO,cEvento,cCentroContable,RFC,mImporteNeto,ALM,mImporteBruto,mImporteMasIva,mImporteIva,nCapitulo,mSancion,mDevolucion,mImporteAmortiza,mRetencion,mPenalizacion,m2Millar,m23IVA,mISRHonorarios,mObra5,mImporteFlete4,mISRArrenda,mRetImpuestoCedular,mImporte,mImporteIvaArrenda,mImporteIvaHonorarios,mImporteFlete23,mImporteIvaProv,mImporteObra,isnull(mCNIC,0.00) AS mCNIC,isnull(mIMDT,0.00) AS mIMDT,isnull(mTesofe,0.00) AS mTesofe,isnull(altaAlmacen,0) AS altaAlmacen,cCentroContable AS cIdEntidadContable, cIdRelacion, (SELECT TOP 1 sCUENTA_BANCARIA FROM tLayoutsCreadosRelacionGastosHeader WITH(NOLOCK) WHERE sNoContrarrecibo = '" + recibo + "' ORDER BY Id DESC)ctab, cPasivo FROM v_AplicarEjercidoPagadoDetalle WITH(NOLOCK) WHERE cEvento != 'ANTICIPO' AND nFolio = ? AND cTipoPago = 'RELACIONGASTOS' ");
                        pstmnSelectDet.setInt(1, nFolioPago);
                        rs3 = pstmnSelectDet.executeQuery();
                    } else {
                        pstmnSelectDet = conn.prepareStatement("SELECT nDocRenglon,cMes,cEjercicio, EP,cIdCuentaContable,mComprometido,isnull(nPoliza,0) AS nPoliza,ID_TIPO_MOVIMIENTO,ID_TIPO_CONCEPTO,cEvento,cCentroContable,RFC,mImporteNeto,ALM,mImporteBruto,mImporteMasIva,mImporteIva,nCapitulo,mSancion,mDevolucion,mImporteAmortiza,mRetencion,mPenalizacion,m2Millar,m23IVA,mISRHonorarios,mObra5,mImporteFlete4,mISRArrenda,mRetImpuestoCedular,mImporte,mImporteIvaArrenda,mImporteIvaHonorarios,mImporteFlete23,mImporteIvaProv,mImporteObra,isnull(mCNIC,0.00) AS mCNIC,isnull(mIMDT,0.00) AS mIMDT,isnull(mTesofe,0.00) AS mTesofe,isnull(altaAlmacen,0) AS altaAlmacen,cCentroContable AS cIdEntidadContable, cIdRelacion, (SELECT TOP 1 sCUENTA_BANCARIA FROM tLayoutsCreadosRelacionGastosHeader WITH(NOLOCK) WHERE sNoContrarrecibo = '" + recibo + "' ORDER BY Id DESC)ctab, cPasivo FROM tRELACIONGASTOSDetalle WITH(NOLOCK) WHERE cEvento != 'ANTICIPO' AND nFolioRELACIONGASTOS = ? ");
                        pstmnSelectDet.setInt(1, nFolioPago);
                        rs3 = pstmnSelectDet.executeQuery();
                    }
                    while (rs3.next()) {
                        int nDocRenglon = Integer.parseInt(rs3.getString("nDocRenglon"), 10);
                        int cMes = Integer.parseInt(rs3.getString("cMes"), 10);
                        String cEjercicio = rs3.getString("cEjercicio");
                        String EP = rs3.getString("EP");
                        String cOBGT = EP.substring(31, 36);
                        String cIdCuentaContable = rs3.getString("cIdCuentaContable");
                        String cIdEntidadContable = rs3.getString("cIdEntidadContable");
                        String cIdRelacion = rs3.getString("cIdRelacion");
                        String idTipoMovimiento = rs3.getString("ID_TIPO_MOVIMIENTO");
                        String idTipoConcepto = rs3.getString("ID_TIPO_CONCEPTO");
                        String[] cEventoCa = rs3.getString("cEvento").split("_");
                        String cEvento = "P_";
                        if (ejercicioFiscal != anioAplicacion)
                            cEvento = cEvento + "FA_";
                        cEvento = cEvento + cEventoCa[1];
                        String cCentroContable = rs3.getString("cCentroContable");
                        String Rfc = rs3.getString("RFC");
                        String alm = rs3.getString("ALM");
                        String nCapitulo = rs3.getString("nCapitulo");
                        String cPasivo = rs3.getString("cPasivo");
                        String altaAlmacen = rs3.getString("altaAlmacen");
                        // Guarda Detalle Ejercido
                        pstmntInsertDet = conn.prepareStatement("INSERT INTO tEjercidoDetalle (cTipoPago,nFolioPAGO,nDocRenglon,cMes,cEjercicio,cIdEntidadContable,cIdRelacion,EP,cIdCuentaContable,mComprometido,nPoliza,ID_TIPO_MOVIMIENTO,ID_TIPO_CONCEPTO,cEvento,nMes,cCentroContable,RFC, mImporteNeto,ALM,mImporteBruto,mImporteMasIva,mImporteIva,nCapitulo,mSancion,mDevolucion,mImporteAmortiza,mRetencion,mPenalizacion,m2Millar,m23IVA,mISRHonorarios,mObra5,mImporteFlete4,mISRArrenda,mRetImpuestoCedular,mImporteIvaArrenda,mImporteIvaHonorarios ,mImporteFlete23,mImporteIvaProv ,mImporteObra,mCNIC,mTesofe,altaAlmacen,aEjercicioFiscal,mIMDT,mImporte,nFolioEjercido, cUnidadResponsable)" + " VALUES('RELACIONGASTOS'," + nFolioPago + "," + nDocRenglon + "," + cMes + ",'" + cEjercicio + "','" + cIdEntidadContable + "','" + cIdRelacion + "','" + EP + "','" + cIdCuentaContable + "'," + rs3.getString("mComprometido") + "," + rs3.getString("nPoliza") + ",'" + idTipoMovimiento + "','" + idTipoConcepto + "','EJERCIDO','" + cMes + "','" + cCentroContable + "','" + Rfc + "'," + rs3.getString("mImporteNeto") + ",'" + alm + "'," + rs3.getString("mImporteBruto") + "," + rs3.getString("mImporteMasIva") + "," + rs3.getString("mImporteIva") + ",'" + nCapitulo + "'," + rs3.getString("mSancion") + "," + rs3.getString("mDevolucion") + "," + rs3.getString("mImporteAmortiza") + " ," + rs3.getString("mRetencion") + "," + rs3.getString("mPenalizacion") + "," + rs3.getString("m2Millar") + "," + rs3.getString("m23IVA") + "," + rs3.getString("mISRHonorarios") + "," + rs3.getString("mObra5") + "," + rs3.getString("mImporteFlete4") + "," + rs3.getString("mISRArrenda") + "," + rs3.getString("mRetImpuestoCedular") + "," + rs3.getString("mImporteIvaArrenda") + "," + rs3.getString("mImporteIvaHonorarios") + "	," + rs3.getString("mImporteFlete23") + "," + rs3.getString("mImporteIvaProv") + "," + rs3.getString("mImporteObra") + "," + rs3.getString("mCNIC") + "," + rs3.getString("mTesofe") + ",'" + altaAlmacen + "','" + aEjercicioFiscal + "'," + rs3.getString("mIMDT") + "," + rs3.getString("mImporteNeto") + "," + strFolioEjercido + ",'" + cUE + "' )");
                        pstmntInsertDet.executeUpdate();
                        // Guarda Detalle Pagado
                        pstmntInsertDetPag = conn.prepareStatement("INSERT INTO tPagadoDetalle (cTipoPago,nFolioPAGO,nDocRenglon,cMes,cEjercicio,cIdEntidadContable,cIdRelacion,EP,cIdCuentaContable,mComprometido,nPoliza,ID_TIPO_MOVIMIENTO,ID_TIPO_CONCEPTO,cEvento,nMes,cCentroContable,RFC, mImporteNeto,ALM,mImporteBruto,mImporteMasIva,mImporteIva,nCapitulo,mSancion,mDevolucion,mImporteAmortiza,mRetencion,mPenalizacion,m2Millar,m23IVA,mISRHonorarios,mObra5    ,mImporteFlete4,mISRArrenda,mRetImpuestoCedular,mImporteIvaArrenda,mImporteIvaHonorarios ,mImporteFlete23,mImporteIvaProv ,mImporteObra,mCNIC,mTesofe,altaAlmacen,aEjercicioFiscal,mIMDT,mImporte,nFolioPagado,OBGT,ctab,cPasivo, cUnidadResponsable)" + " VALUES('RELACIONGASTOS'," + nFolioPago + "," + nDocRenglon + "," + cMes + ",'" + cEjercicio + "','" + cIdEntidadContable + "','" + cIdRelacion + "','" + EP + "','" + cIdCuentaContable + "'," + rs3.getString("mComprometido") + "," + rs3.getString("nPoliza") + ",'" + idTipoMovimiento + "','" + idTipoConcepto + "','" + cEvento + "','" + cMes + "','" + cCentroContable + "','" + Rfc + "'," + rs3.getString("mImporteNeto") + ",'" + alm + "'," + rs3.getString("mImporteBruto") + "," + rs3.getString("mImporteMasIva") + "," + rs3.getString("mImporteIva") + ",'" + nCapitulo + "'," + rs3.getString("mSancion") + "," + rs3.getString("mDevolucion") + "," + rs3.getString("mImporteAmortiza") + " ," + rs3.getString("mRetencion") + "," + rs3.getString("mPenalizacion") + "," + rs3.getString("m2Millar") + "," + rs3.getString("m23IVA") + "," + rs3.getString("mISRHonorarios") + "," + rs3.getString("mObra5") + "       ," + rs3.getString("mImporteFlete4") + "," + rs3.getString("mISRArrenda") + "," + rs3.getString("mRetImpuestoCedular") + "," + rs3.getString("mImporteIvaArrenda") + "," + rs3.getString("mImporteIvaHonorarios") + "	," + rs3.getString("mImporteFlete23") + "," + rs3.getString("mImporteIvaProv") + "," + rs3.getString("mImporteObra") + "," + rs3.getString("mCNIC") + "," + rs3.getString("mTesofe") + ",'" + altaAlmacen + "','" + aEjercicioFiscal + "'," + rs3.getString("mIMDT") + "," + rs3.getString("mImporteNeto") + "," + strFolioPagado + ",'" + cOBGT + "','" + strCuenta + "','" + cPasivo + "','" + cUE + "')");
                        int intDetPag = pstmntInsertDetPag.executeUpdate();
                        log.trace("Object: {}", "Se insertaron " + intDetPag + " registros");
                    }
                    try {
                        AccountingEngine accEng = new AccountingEngine();
                        // Verificar
                        accEng.setValidaInsuficienciaDeSaldo(true);
                        // estaba
                        // en
                        // false
                        // Datos Para Aplicar Ejercido
                        String cTablaPadre = "tEjercidoEncabezado";
                        String cTablaHija = "tEjercidoDetalle";
                        String cFolio = "nFolioEjercido";
                        String cTipoDocumento = "EJERCIDO";
                        // Datos Para Aplicar Pagado
                        String cTablaPadrePag = "tPagadoEncabezado";
                        String cTablaHijaPag = "tPagadoDetalle";
                        String cFolioPag = "nFolioPagado";
                        String cTipoDocumentoPag = "PAGADO";
                        if ("N".equals(ejercidoApl)) {
                            updateFechaAplicacion = conn.prepareStatement("UPDATE tEjercidoEncabezado SET fAplicacion = ?  WHERE nFolioEjercido = ?");
                            updateFechaAplicacion.setString(1, fechaAplicacion);
                            updateFechaAplicacion.setString(2, strFolioEjercido);
                            updateFechaAplicacion.executeUpdate();
                            accEng.makeAccountingApplication(conn, cTipoDocumento, strFolioEjercido, cTablaPadre, cTablaHija, cFolio);
                        }
                        boolean pagado = false;
                        if ("N".equals(pagadoApl)) {
                            updateFechaAplicacion = conn.prepareStatement("UPDATE tPagadoEncabezado SET fAplicacion = ?  WHERE nFolioPagado = ?");
                            updateFechaAplicacion.setString(1, fechaAplicacion);
                            updateFechaAplicacion.setString(2, strFolioPagado);
                            updateFechaAplicacion.executeUpdate();
                            accEng.makeAccountingApplication(conn, cTipoDocumentoPag, strFolioPagado, cTablaPadrePag, cTablaHijaPag, cFolioPag);
                            pagado = true;
                        }
                        conn.commit();
                        /*
						 * VGC20151222 Se genera una poliza que cancela la
						 * afectacion de cuentas contables.
						 */
                        if (generaPoliza && pagado) {
                            GeneradorPolizaManualBusinessLogic gpmbl = new GeneradorPolizaManualBusinessLogic(GestionInterface.ATT_CONEXION);
                            gpmbl.generaPolizaCancelaPago(strFolioPagado, cUE);
                        }
                        valor = "ingresado";
                    } catch (Exception ex) {
                        valor = ex.toString();
                        log.warn(ex.getMessage(), ex);
                        log.debug("Object: " + String.valueOf(ex.getLocalizedMessage()));
                        try {
                            conn.rollback();
                        } catch (Exception e) {
                            log.warn("Error en rollback", e);
                        }
                    }
                } else {
                    System.out.println("No Guardo Encabezado");
                }
            }
        } catch (Exception e) {
            log.error("Error occurred", "Error: " + e);
            // Rollback a la transaccion
            try {
                conn.rollback();
            } catch (Exception ee) {
                log.warn("Error: cerrando rollback ", ee);
            }
            valor = e.toString();
        } finally {
            try {
                CloseObject.closeObject(rs);
                CloseObject.closeObject(rs3);
                CloseObject.closeObject(rs4);
                CloseObject.closeObject(rs8);
                CloseObject.closeObject(rsExisteEncabezado);
                CloseObject.closeObject(pstmtnSelect);
                CloseObject.closeObject(pstmntInsertEj);
                CloseObject.closeObject(pstmnSelectDet);
                CloseObject.closeObject(pstmntInsertDet);
                CloseObject.closeObject(pstmUpPagado);
                CloseObject.closeObject(pstmSeqEjercido);
                CloseObject.closeObject(pstmExisteEncabezado);
                CloseObject.closeObject(pstmntInsertPag);
                CloseObject.closeObject(pstmntInsertDetPag);
                CloseObject.closeObject(updateFechaAplicacion);
                CloseObject.closeObject(pstUpdateFolioSicop);
                CloseObject.closeObject(pstmUpEjercido);
                CloseObject.closeObject(conn);
            } catch (Exception e) {
                log.warn("Error: cerrando statement", e);
            }
        }
        return valor;
    }

    public String aplicarMasivoRelacionGastos(int folioGeneral, String strTabla, FolioGeneratorInterface fg, Usuario u, boolean esFirmaElectronica, List<Firmante> firmantes) throws SQLException {
        AccountingEngine accEng = new AccountingEngine();
        String valor = "incorrecto";
        Connection conn = null;
        FirmanteBussinessLogic fbl = new FirmanteBussinessLogic(GestionInterface.ATT_CONEXION);
        RelacionGastosBussinessLogic rgbl = new RelacionGastosBussinessLogic(GestionInterface.ATT_CONEXION, null);
        SolicitudFirmaElectronica solicitudPagoPrinter = new SolicitudPagoFirmaElectronica();
        List<MasiveOperation> listRG = null;
        boolean terminado = false;
        try {
            if (esFirmaElectronica && firmantes == null)
                throw new Exception("Los firmantes son requeridos cuando se autorizara el tramite por FIEL");
            solicitudPagoPrinter.setDetail("tRelacionGastosDetalle");
            solicitudPagoPrinter.setDocument("RELACIONGASTOS");
            solicitudPagoPrinter.setField("nFolioRelacionGastos");
            solicitudPagoPrinter.setFileExtension("pdf");
            solicitudPagoPrinter.setHeader("tRELACIONGASTOSEncabezado");
            solicitudPagoPrinter.setReportPath(getReportPath());
            solicitudPagoPrinter.setUsuario(getUsuario());
            solicitudPagoPrinter.setCargaMasiva(true);
            solicitudPagoPrinter.setMasiveHeader("tRELACIONGASTOSEncabezado_temp");
            solicitudPagoPrinter.setMasiveDetail("tRELACIONGASTOSDetalle_temp");
            solicitudPagoPrinter.setMasiveField("folioTempGral");
            solicitudPagoPrinter.setMasiveID(folioGeneral);
            listRG = rgbl.readRGMasivaTemp(folioGeneral);
            generaCxPRG(listRG);
            conn = getConnection();
            String listadoCxP = "";
            for (MasiveOperation mo : listRG) {
                RGMasiva rg = (RGMasiva) mo;
                if (StringUtils.isBlank(rg.getContrarecibo()))
                    throw new Exception("No fue posible crear el contrarecibo.");
                listadoCxP = listadoCxP + rg.getContrarecibo() + " - ";
                int folioRG = generaCasoRelGastos(fg, u);
                if (folioRG <= 0)
                    throw new Exception("No fue posible crear el tramite para la RG.");
                /* Traer sequence de Apartado para nFolioApartado */
                int folApartado = generaFolioApartado(conn);
                if (folApartado <= 0)
                    throw new Exception("No fue posible crear el tramite para el apartado de la RG.");
                /* Folios para su Aplicacion nFolioApar */
                rg.setFolioApartado(folApartado);
                rg.setFolioTramite(folioRG);
                /*
				 * Guardar Apartado Encabezado de tRELACIONGASTOSEncabezado_temp
				 */
                int rgApartadoInsertado = insertaRelacionRGApartado(conn, rg);
                if (rgApartadoInsertado == 0)
                    throw new Exception("No fue posible insertar la relacion RG-Apartado");
                int nApartadoDetalle = 0;
                if ("S".equalsIgnoreCase(rg.getRadicado())) {
                    nApartadoDetalle = insertaApartadoDetalleRadicado(conn, rg);
                } else {
                    nApartadoDetalle = insertaApartadoDetalle(conn, rg);
                }
                if (nApartadoDetalle == 0)
                    throw new Exception("No fue posible insertar detalle del apartado.");
                /* Aplicacion contable del apartado */
                accEng.makeAccountingApplication(conn, "PAGOAPARTADO", String.valueOf(rg.getFolioApartado()), "tPAGOAPARTADOEncabezado", "tPAGOAPARTADODetalle", "nFolioPagoApartado");
                /* Guardar tRELACIONGASTOSEncabezado */
                int rgInsertada = insertaRGEncabezado(conn, rg, esFirmaElectronica, firmantes);
                if (rgInsertada <= 0)
                    throw new Exception("No fue posible insertar encabezado RG.");
                if (esFirmaElectronica) {
                    int firmantesInsertados = 0;
                    firmantesInsertados = fbl.saveFirmantes(conn, firmantes, "RELACIONGASTOS", rg.getFolioTramite(), esFirmaElectronica);
                    if (firmantesInsertados <= 0)
                        throw new Exception("No fue posible insertar firmantes");
                }
                /* Guardar tRELACIONGASTOSDetalle */
                int detalleInsertado = insertaRGDetalle(conn, rg);
                if (detalleInsertado <= 0)
                    throw new Exception("No fue posible insertar detalle de RG");
                boolean esRGLaudos = RelacionGastosManager.esRGLaudos(conn, folioRG);
                double importeISRLaudos = 0.00;
                if (esRGLaudos) {
                    RelacionGastosManager.insertaInformacionLaudos(conn, folioRG);
                    importeISRLaudos = RelacionGastosManager.leeMontoLaudos(conn, folioRG);
                }
                rg.setImporteISRLaudos(importeISRLaudos);
                /* Guardar en tContrarrecibo */
                int cxpInsertados = insertaContrarecibo(conn, rg);
                if (cxpInsertados <= 0)
                    throw new Exception("No fue posible insertar contrarecibo");
                /* Guardar en tDocumentacionComprobatoriaDet */
                int dcInsertado = insertaDoComp(conn, rg);
                if (dcInsertado <= 0)
                    throw new Exception("No fue posible insertar contrarecibo");
                /*
				 * Actualizar el tipo de carga seleccionado: 2=Alimentacion a
				 * Brigadistas, 3=Certificado de Transito.
				 */
                if (rg.getTipoCarga() == 2) {
                    actualizaTipoCargaAlim(conn, rg);
                } else if (rg.getTipoCarga() == 3) {
                    actualizaTipoCargaCert(conn, rg);
                } else if (rg.getTipoCarga() == 7) {
                    actualizarTipoJuegosDeportivos(conn, rg);
                }
                accEng.makeAccountingApplication(conn, "RELACIONGASTOS", String.valueOf(rg.getFolioTramite()), "tRELACIONGASTOSEncabezado", "tRELACIONGASTOSDetalle", "nFolioRELACIONGASTOS");
                if (esFirmaElectronica) {
                    solicitudPagoPrinter.setIdField(rg.getFolioTramite());
                    FirmaElectronicaManager.avanzaEstatusSICOP(conn, solicitudPagoPrinter, SolicitudFirmaElectronica.ARCHIVOS_MASIVO_PENDIENTES);
                    FirmaElectronicaManager.actualizaMetodoAutorizacion(conn, solicitudPagoPrinter.getHeader(), solicitudPagoPrinter.getField(), String.valueOf(rg.getFolioTramite()), esFirmaElectronica);
                }
            }
            if (esFirmaElectronica) {
                FirmaElectronicaManager.avanzaEstatusSICOPMasivo(conn, solicitudPagoPrinter, SolicitudFirmaElectronica.ARCHIVOS_MASIVO_PENDIENTES);
            }
            CargaMasivaRG cargaMasivaRG = new CargaMasivaRG(folioGeneral, CargaMasivaRG.CARGA_APLICADA);
            RelacionGastosManager.updateCargaMasivaRG(conn, cargaMasivaRG);
            valor = "correcto:" + listadoCxP;
            terminado = true;
            if (esFirmaElectronica) {
                FirmaElectronicaManager.avanzaEstatusSICOPMasivo(conn, solicitudPagoPrinter, SolicitudFirmaElectronica.ARCHIVOS_MASIVO_PENDIENTES);
            }
            conn.commit();
            FirmaElectronicaBusinessLogic febl = new FirmaElectronicaBusinessLogic(GestionInterface.ATT_CONEXION);
            if (terminado) {
                try {
                    febl.generaArchivosFirmaMasiva(folioGeneral, solicitudPagoPrinter, listRG, "Solicitud de Pago Firmada");
                } catch (Exception e) {
                    log.warn(e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            log.error("Error aplicando captura masiva de RG " + e, e);
            if (conn != null)
                try {
                    conn.rollback();
                } catch (Exception ee) {
                    log.warn("Error: cerrando rollback ", ee);
                }
            valor = e.toString();
        } finally {
            CloseObject.closeObject(conn);
        }
        return valor;
    }

    public boolean existe(String strTabla, String strIdCab, String strNomCampoId) throws SQLException {
        boolean existeCXP = false;
        PreparedStatement pstmn = null;
        ResultSet rs = null;
        Connection conn = null;
        try {
            conn = getConnection();
            pstmn = conn.prepareStatement("SELECT * FROM " + strTabla + " WITH(NOLOCK) WHERE " + strNomCampoId + " = ? ");
            pstmn.setString(1, strIdCab);
            rs = pstmn.executeQuery();
            if (rs.next()) {
                existeCXP = true;
            }
        } catch (Exception e) {
            log.error("Error occurred", "Error: " + e);
            existeCXP = true;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(pstmn);
            CloseObject.closeObject(conn);
        }
        return existeCXP;
    }

    public boolean existeIntegracion(String strTabla, String strIdCab, String strNomCampoId) throws SQLException {
        boolean existeCXP = false;
        PreparedStatement pstmn = null;
        ResultSet rs = null;
        Connection conn = null;
        try {
            conn = getConnection();
            pstmn = conn.prepareStatement("SELECT l.sNoContrarrecibo, l.nFolio,l.sUnidadResponsable,e.caNoContrarrecibo,l.fAplicacion,l.sRamo FROM " + strTabla + " WITH(NOLOCK) WHERE " + strNomCampoId + " = ? ");
            pstmn.setString(1, strIdCab);
            rs = pstmn.executeQuery();
            if (rs.next()) {
                existeCXP = true;
            }
        } catch (Exception e) {
            log.error("Error occurred", "Error: " + e);
            existeCXP = true;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(pstmn);
            CloseObject.closeObject(conn);
        }
        return existeCXP;
    }

    public String existeIntegracionPagado(String strTabla, String strIdCab, String strNomCampoId) throws SQLException {
        String existeCXP = "noExiste";
        PreparedStatement pstmn = null, pstmnp = null;
        ResultSet rs = null, rsp = null;
        Connection conn = null;
        try {
            conn = getConnection();
            pstmn = conn.prepareStatement("SELECT l.sNoContrarrecibo, l.nFolio,l.sUnidadResponsable,e.caNoContrarrecibo,l.fAplicacion,l.sRamo FROM " + strTabla + " WHERE " + strNomCampoId + " = ? AND cDocumentoHaplicado = 'S' ");
            pstmn.setString(1, strIdCab);
            rs = pstmn.executeQuery();
            if (rs.next()) {
                existeCXP = "Ejercido";
            }
            pstmnp = conn.prepareStatement("SELECT l.sNoContrarrecibo, l.nFolio,l.sUnidadResponsable,e.caNoContrarrecibo,l.fAplicacion,l.sRamo FROM tLayoutsCreadosRelacionGastosHeader as l WITH(NOLOCK) INNER JOIN tPagadoEncabezado as e WITH(NOLOCK) ON l.sNoContrarrecibo = e.caNoContrarrecibo WHERE " + strNomCampoId + " = ? AND cDocumentoHaplicado = 'S' ");
            pstmnp.setString(1, strIdCab);
            rsp = pstmnp.executeQuery();
            if (rsp.next()) {
                existeCXP = "Pagado";
            }
        } catch (Exception e) {
            log.error("Error occurred", "Error: " + e);
            existeCXP = "Error";
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(rsp);
            CloseObject.closeObject(pstmn);
            CloseObject.closeObject(pstmnp);
            CloseObject.closeObject(conn);
        }
        return existeCXP;
    }

    public String existePagado(String strTabla, String strIdCab, String strNomCampoId) throws SQLException {
        String existeCXP = "noExiste";
        PreparedStatement pstmn = null, pstmnP = null;
        ResultSet rs = null, rsP = null;
        Connection conn = null;
        try {
            conn = getConnection();
            pstmn = conn.prepareStatement("SELECT * FROM " + strTabla + " WITH(NOLOCK) WHERE " + strNomCampoId + " = ? AND cDocumentoHaplicado = 'S' ");
            pstmn.setString(1, strIdCab);
            rs = pstmn.executeQuery();
            if (rs.next()) {
                existeCXP = "Ejercido";
            }
            pstmnP = conn.prepareStatement("SELECT * FROM tPagadoEncabezado WITH(NOLOCK) WHERE " + strNomCampoId + " = ? AND cDocumentoHaplicado = 'S' ");
            pstmnP.setString(1, strIdCab);
            rsP = pstmnP.executeQuery();
            if (rsP.next()) {
                existeCXP = "Pagado";
            }
        } catch (Exception e) {
            log.error("Error occurred", "Error: " + e);
            existeCXP = "Error";
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(rsP);
            CloseObject.closeObject(pstmn);
            CloseObject.closeObject(pstmnP);
            CloseObject.closeObject(conn);
        }
        return existeCXP;
    }

    private Caso generaCaso(Usuario u, int idTCaso, FolioGeneratorInterface fg, String opResponsable, String concepto) throws GestionException, SQLException {
        CasoBusinessLogic casoTx = new CasoBusinessLogic("jdbc/gestion");
        Caso c = casoTx.IniciaCaso(u, idTCaso, fg);
        AdecuacionBusinessLogic adbl = new AdecuacionBusinessLogic("jdbc/gestion");
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String fecha = sdf.format(date);
        // Variables del caso
        c.getCasoDato("FOLIO").setValor(c.getFolio());
        c.getCasoDato("OPERADOR").setValor(u.getLogin());
        c.getCasoDato("FECHA_DOCUMENTO").setValor(fecha);
        c.getCasoDato("EJERCICIO_FISCAL").setValor(adbl.obtenEjercicioFiscal());
        c.getCasoDato("CONCEPTO_MOV").setValor(concepto);
        c.getCasoDato("MONEDA").setValor("MXP");
        Map<String, String> m = new HashMap<String, String>();
        m.put("FOLIO", c.getFolio());
        m.put("OPERADOR", u.getLogin());
        m.put("FECHA_DOCUMENTO", fecha);
        m.put("EJERCICIO_FISCAL", adbl.obtenEjercicioFiscal());
        m.put("CONCEPTO_MOV", "Relacion Gastos");
        m.put("MONEDA", "MXP");
        c.setIdGabinete(casoTx.creaExpediente(u.getLogin(), c));
        // Guarda las variables de caso.
        c = casoTx.actualizaCasoDato(c, m);
        // Cambia el usuario al grupo ventanilla para que aparezca en el inbox.
        CasoOperacion co = ((CasoOperacion) c.getCasoOperacion().get(0));
        // Ingresa numero id oper
        co.setIdOperacion(3);
        CasoOperacionBusinessLogic cobl = new CasoOperacionBusinessLogic("jdbc/gestion");
        cobl.updateCasoOperacion(co);
        cobl.updateCasoResponsable(co, opResponsable);
        return c;
    }

    public int generaCasoApartado(FolioGeneratorInterface fg, Usuario u) throws Exception {
        Caso cRelGas = null;
        cRelGas = generaCaso(u, 11, fg, "CONSULTA_RELACIONGASTOS", "Consulta Relacion de Gastos");
        int idCasoReintegro = new Integer(cRelGas.getFolio().substring(cRelGas.getFolio().lastIndexOf('-') + 1)).intValue();
        return idCasoReintegro;
    }

    public int generaCasoRelGastos(FolioGeneratorInterface fg, Usuario u) throws Exception {
        Caso cRelGas = null;
        cRelGas = generaCaso(u, 11, fg, "CONSULTA_RELACIONGASTOS", "Consulta Relacion de Gastos");
        int idCasoReintegro = new Integer(cRelGas.getFolio().substring(cRelGas.getFolio().lastIndexOf('-') + 1)).intValue();
        return idCasoReintegro;
    }

    private void generaCxPRG(List<MasiveOperation> listRG) throws Exception {
        CFSequenceManager sequence = CFSequenceManager.getInstance();
        for (MasiveOperation rg : listRG) {
            int folioRe = sequence.nextVal("CR-" + rg.getCentroContable());
            String seqValue = "000000" + folioRe;
            seqValue = seqValue.substring(seqValue.length() - 6);
            seqValue = "1" + seqValue.substring(seqValue.length() - 5);
            seqValue = rg.getCentroContable() + cxpPrefijo + rg.getEjercicioFiscal() + seqValue;
            rg.setContrarecibo(seqValue);
        }
    }

    private int generaFolioApartado(Connection conn) throws Exception {
        CFSequenceManager sequence = CFSequenceManager.getInstance();
        return sequence.nextVal(conn, "APARTADO");
    }

    public String getReportPath() {
        return reportPath;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    private int insertaApartadoDetalle(Connection conn, RGMasiva rg) throws Exception {
        PreparedStatement psInsD = null;
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ");
        sb.append("      tPagoApartadoDetalle (");
        sb.append("      	nFolioPagoApartado");
        sb.append("      	,nDocRenglon");
        sb.append("      	,cMes");
        sb.append("      	,cEvento");
        sb.append("      	,cEjercicio");
        sb.append("      	,cCentroContable");
        sb.append("      	,EP");
        sb.append("      	,mImporte ");
        sb.append("      	,cUnidadResponsable ");
        sb.append("      	)	");
        sb.append("SELECT	? as folioApartado");
        sb.append("      	, nDocRenglon");
        sb.append("      	, nMes");
        sb.append("      	, 'APARTADO'");
        sb.append("      	, cEjercicio");
        sb.append("      	, cCentroContable");
        sb.append("      	, EP");
        sb.append("      	, mImporteBruto ");
        sb.append("      	, cUnidadResponsable ");
        sb.append("  FROM	tRELACIONGASTOSDetalle_temp WITH (NOLOCK)  ");
        sb.append(" WHERE	nFolioRELACIONGASTOS = ?");
        int afectados = 0;
        try {
            psInsD = conn.prepareStatement(sb.toString());
            psInsD.setInt(1, rg.getFolioApartado());
            psInsD.setInt(2, rg.getFolioTramiteTemporal());
            afectados += psInsD.executeUpdate();
            return afectados;
        } finally {
            CloseObject.closeObject(psInsD);
        }
    }

    private int insertaApartadoDetalleRadicado(Connection conn, RGMasiva rg) throws Exception {
        PreparedStatement psInsD = null;
        PreparedStatement psInsIngrPagEnc = null;
        // PreparedStatement psInsIngrPagDet = null;
        PreparedStatement psUpdIngrPagEnc = null;
        StringBuilder sbInsertApartado = new StringBuilder();
        sbInsertApartado.append("INSERT INTO ");
        sbInsertApartado.append("     tPagoApartadoDetalle");
        sbInsertApartado.append("     (");
        sbInsertApartado.append("     	nFolioPagoApartado, ");
        sbInsertApartado.append("     	nDocRenglon, ");
        sbInsertApartado.append("     	cMes, ");
        sbInsertApartado.append("     	cEvento, ");
        sbInsertApartado.append("     	cEjercicio, ");
        sbInsertApartado.append("     	cCentroContable, ");
        sbInsertApartado.append("     	EP, ");
        sbInsertApartado.append("     	mImporte ");
        sbInsertApartado.append("     ) ");
        sbInsertApartado.append("SELECT	? as folioApartado, ");
        sbInsertApartado.append("      	nDocRenglon, ");
        sbInsertApartado.append("      	nMes, ");
        sbInsertApartado.append("      	'R_APARTADO', ");
        sbInsertApartado.append("      	cEjercicio, ");
        sbInsertApartado.append("      	cCentroContable, ");
        sbInsertApartado.append("      	EP, ");
        sbInsertApartado.append("      	mImporteBruto ");
        sbInsertApartado.append("  FROM	tRELACIONGASTOSDetalle_temp WITH (NOLOCK)  ");
        sbInsertApartado.append(" WHERE	nFolioRELACIONGASTOS = ?");
        StringBuilder sbInsertIngPE = new StringBuilder();
        sbInsertIngPE.append("INSERT INTO ");
        sbInsertIngPE.append("      tingresopagoencabezado( ");
        sbInsertIngPE.append("      	ctiposolicitud,  ");
        sbInsertIngPE.append("      	nfoliosolicitud,");
        sbInsertIngPE.append("      	fsolicitud,");
        sbInsertIngPE.append("      	cdocumentohaplicado,");
        sbInsertIngPE.append("      	ccentrocontable,");
        sbInsertIngPE.append("      	mtotal,");
        sbInsertIngPE.append("      	active,");
        sbInsertIngPE.append("      	cradicado,");
        sbInsertIngPE.append("      	cejercicio)");
        sbInsertIngPE.append("SELECT	'RELACIONGASTOS',");
        sbInsertIngPE.append("      	nFolioRELACIONGASTOS,");
        sbInsertIngPE.append("      	Getdate(),");
        sbInsertIngPE.append("      	'D',");
        sbInsertIngPE.append("      	cCentroContable,");
        sbInsertIngPE.append("      	mImporteMasIva,");
        sbInsertIngPE.append("      	'1',");
        sbInsertIngPE.append("      	'S',");
        sbInsertIngPE.append("      	aEjercicioFiscal");
        sbInsertIngPE.append("  FROM	tRELACIONGASTOSEncabezado_temp WITH (NOLOCK) ");
        sbInsertIngPE.append(" WHERE	nFolioRELACIONGASTOS= ? ");
        StringBuilder sbInsIngPD = new StringBuilder();
        sbInsIngPD.append("INSERT INTO ");
        sbInsIngPD.append("      tIngresoPagoDetalle ");
        sbInsIngPD.append("      (	[nFolioIngresoPago], ");
        sbInsIngPD.append("      	[nFolioRegistroIngreso], ");
        sbInsIngPD.append("      	[EP], ");
        sbInsIngPD.append("      	[nMes], ");
        sbInsIngPD.append("      	[mImporte]) ");
        sbInsIngPD.append("SELECT	ingEnc.nFolioIngresoPago, ");
        sbInsIngPD.append("      	reltemp.nFolioSolicitud, ");
        sbInsIngPD.append("      	reltemp.EP, ");
        sbInsIngPD.append("      	reltemp.nMes, ");
        sbInsIngPD.append("      	reltemp.mImporteMasIva ");
        sbInsIngPD.append("  FROM	tRELACIONGASTOSDetalle_temp reltemp WITH (NOLOCK) ");
        sbInsIngPD.append("      	INNER JOIN  ");
        sbInsIngPD.append("      	tIngresoPagoEncabezado ingEnc WITH (NOLOCK) ");
        sbInsIngPD.append("      	ON reltemp.nFolioRELACIONGASTOS = ingEnc.nFolioSolicitud ");
        sbInsIngPD.append(" WHERE	cTipoSolicitud = 'RELACIONGASTOS' ");
        sbInsIngPD.append("   AND	reltemp.nFolioRELACIONGASTOS = ? ");
        StringBuilder sbUpdIngrPagEnc = new StringBuilder();
        sbUpdIngrPagEnc.append("UPDATE	tIngresoPagoEncabezado ");
        sbUpdIngrPagEnc.append("   SET	nFolioSolicitud = ( ");
        sbUpdIngrPagEnc.append("     		SELECT nFolioPago  ");
        sbUpdIngrPagEnc.append("     		  FROM   tPagoApartadoEncabezado WITH (nolock)  ");
        sbUpdIngrPagEnc.append("     		 WHERE  caNoContrarrecibo = ?");
        sbUpdIngrPagEnc.append("     	)  ");
        sbUpdIngrPagEnc.append(" WHERE	nFolioSolicitud = ? ");
        int afectados = 0;
        try {
            /* Guardar Apartado detalle de tRELACIONGASTOSDetalle_temp */
            psInsD = conn.prepareStatement(sbInsertApartado.toString());
            psInsIngrPagEnc = conn.prepareStatement(sbInsertIngPE.toString());
            psUpdIngrPagEnc = conn.prepareStatement(sbUpdIngrPagEnc.toString());
            psInsD.setInt(1, rg.getFolioApartado());
            psInsD.setInt(2, rg.getFolioTramiteTemporal());
            afectados += psInsD.executeUpdate();
            psInsIngrPagEnc.setInt(1, rg.getFolioTramiteTemporal());
            afectados += psInsIngrPagEnc.executeUpdate();
            psUpdIngrPagEnc.setString(1, rg.getContrarecibo());
            psUpdIngrPagEnc.setInt(2, rg.getFolioTramiteTemporal());
            afectados += psUpdIngrPagEnc.executeUpdate();
            return afectados;
        } finally {
            CloseObject.closeObject(psInsD);
            CloseObject.closeObject(psInsIngrPagEnc);
        }
    }

    private int insertaContrarecibo(Connection conn, RGMasiva rg) throws Exception {
        StringBuilder sbInsCxP = new StringBuilder();
        sbInsCxP.append("INSERT INTO tContrarrecibo (");
        sbInsCxP.append("                            caNoContrarrecibo, ");
        sbInsCxP.append("                            cIdTipoOperacion, ");
        sbInsCxP.append("                            caNoAP, ");
        sbInsCxP.append("                            nTipoCambio, ");
        sbInsCxP.append("                            mImporteBruto, ");
        sbInsCxP.append("                            mImporteSancion, ");
        sbInsCxP.append("                            mImporteDevolucion, ");
        sbInsCxP.append("                            mAmortizacionAnticipo, ");
        sbInsCxP.append("                            mImporteIVA, ");
        sbInsCxP.append("                            mImporteRetencion, ");
        sbInsCxP.append("                            mImportePenalizacion, ");
        sbInsCxP.append("                            mImporteNeto, ");
        sbInsCxP.append("                            fProgramadaPago, ");
        sbInsCxP.append("                            cIdEntidadContable, ");
        sbInsCxP.append("                            aEjercicioFiscal, ");
        sbInsCxP.append("                            cIdDocumento, ");
        sbInsCxP.append("                            cIdTipoDocumento, ");
        sbInsCxP.append("                            cIdSubtipoDocumento, ");
        sbInsCxP.append("                            cIdRFC) ");
        sbInsCxP.append("SELECT	?, ");
        sbInsCxP.append("      	TIPO_OPERACION, ");
        sbInsCxP.append("      	0, ");
        sbInsCxP.append("      	0.0000, ");
        sbInsCxP.append("      	mImporteBruto, ");
        sbInsCxP.append("      	0.00, ");
        sbInsCxP.append("      	0.00,");
        sbInsCxP.append("      	0.00, ");
        sbInsCxP.append("      	0.00, ");
        sbInsCxP.append("      	?, ");
        sbInsCxP.append("      	0.00, ");
        sbInsCxP.append("      	mImporteNeto, ");
        sbInsCxP.append("      	fProgramadaPago, ");
        sbInsCxP.append("      	cIdEntidadContable, ");
        sbInsCxP.append("      	aEjercicioFiscal, ");
        sbInsCxP.append("      	UPPER(cIdRelacion), ");
        sbInsCxP.append("      	cIdTipoDocumento, ");
        sbInsCxP.append("      	0, ");
        sbInsCxP.append("      	RFC ");
        sbInsCxP.append("  FROM	tRELACIONGASTOSEncabezado_temp ");
        sbInsCxP.append(" WHERE	nFolioRELACIONGASTOS = ?");
        PreparedStatement psInsCon = null;
        try {
            psInsCon = conn.prepareStatement(sbInsCxP.toString());
            psInsCon.setString(1, rg.getContrarecibo());
            psInsCon.setDouble(2, rg.getImporteISRLaudos());
            psInsCon.setInt(3, rg.getFolioTramiteTemporal());
            return psInsCon.executeUpdate();
        } finally {
            CloseObject.closeObject(psInsCon);
        }
    }

    private int insertaDoComp(Connection conn, RGMasiva rg) throws Exception {
        StringBuilder sbIns = new StringBuilder();
        sbIns.append("INSERT INTO dbo.tDocumentacionComprobatoriaDet   ");
        sbIns.append("SELECT	 cEjercicio ");
        sbIns.append("      	,10 ");
        sbIns.append("      	,caNoContrarrecibo ");
        sbIns.append("      	,1 ");
        sbIns.append("      	,16 ");
        sbIns.append("      	,cIdRelacion ");
        sbIns.append("      	,fAplicacion ");
        sbIns.append("      	,fAplicacion ");
        sbIns.append("      	,CBEN dcd_cben ");
        sbIns.append("      	,cIdTipoPersonaRFC dcd_tben ");
        sbIns.append("      	,'85' ");
        sbIns.append("      	,'05' ");
        sbIns.append("      	,0.00 ");
        sbIns.append("      	,mImporteBruto ");
        sbIns.append("      	,0.00 ");
        sbIns.append("      	,0.00 ");
        sbIns.append("      	,0.00 ");
        sbIns.append("      	,0.00 ");
        sbIns.append("      	,0.00 ");
        sbIns.append("      	,0.00 ");
        sbIns.append("      	,0.00 ");
        sbIns.append("      	,0.00 ");
        sbIns.append("      	,0 ");
        sbIns.append("      	,cConcepto ");
        sbIns.append("      	,fAplicacion   ");
        sbIns.append("  FROM	tRELACIONGASTOSEncabezado e WITH (NOLOCK)  ");
        sbIns.append("      	INNER JOIN  ");
        sbIns.append("      	tBeneficiario b  WITH (NOLOCK) ");
        sbIns.append("      	ON  ");
        sbIns.append("      	dRFC=RFC  ");
        sbIns.append("  WHERE	nFolioRELACIONGASTOS = ? ");
        PreparedStatement psInsDocComp = null;
        try {
            psInsDocComp = conn.prepareStatement(sbIns.toString());
            psInsDocComp.setInt(1, rg.getFolioTramite());
            return psInsDocComp.executeUpdate();
        } finally {
            CloseObject.closeObject(psInsDocComp);
        }
    }

    private int insertaRelacionRGApartado(Connection conn, RGMasiva rg) throws Exception {
        PreparedStatement psInsA = null;
        StringBuilder sbInsert = new StringBuilder();
        sbInsert.append("INSERT INTO ");
        sbInsert.append("      tPagoApartadoEncabezado(  ");
        sbInsert.append("       	nFolioPagoApartado, ");
        sbInsert.append("       	cTipoPago,");
        sbInsert.append("       	nFolioPago,");
        sbInsert.append("       	fAplicacion,");
        sbInsert.append("       	cRamo,");
        sbInsert.append("       	caNoContrarrecibo,");
        sbInsert.append("       	aEjercicioFiscal,");
        sbInsert.append("       	cTipoPoliza,");
        sbInsert.append("       	cUnidadResponsableContable,");
        sbInsert.append("       	cDescripcionPoliza,");
        sbInsert.append("       	cUnidadResponsable");
        sbInsert.append("       	)");
        sbInsert.append("SELECT	? as folioApartado, ");
        sbInsert.append("       	'RELACIONGASTOS' as cTipoPago,");
        sbInsert.append("       	? as nFolioPago,");
        sbInsert.append("       	fAplicacion,");
        sbInsert.append("       	cRamo,");
        sbInsert.append("       	?,");
        sbInsert.append("       	aEjercicioFiscal,");
        sbInsert.append("       	'PR',");
        sbInsert.append("       	cUnidadResponsableContable,");
        sbInsert.append("       	cDescripcionPoliza, ");
        sbInsert.append("       	cUnidadResponsable ");
        sbInsert.append("  FROM	tRELACIONGASTOSEncabezado_temp ");
        sbInsert.append(" WHERE	nFolioRELACIONGASTOS = ?");
        try {
            psInsA = conn.prepareStatement(sbInsert.toString());
            psInsA.setInt(1, rg.getFolioApartado());
            psInsA.setInt(2, rg.getFolioTramite());
            psInsA.setString(3, rg.getContrarecibo());
            psInsA.setInt(4, rg.getFolioTramiteTemporal());
            return psInsA.executeUpdate();
        } finally {
            CloseObject.closeObject(psInsA);
        }
    }

    private int insertaRGDetalle(Connection conn, RGMasiva rg) throws Exception {
        StringBuilder sbInsertaDet = new StringBuilder();
        sbInsertaDet.append("INSERT INTO tRELACIONGASTOSDetalle (nFolioRELACIONGASTOS, ");
        sbInsertaDet.append(" nDocRenglon, ");
        sbInsertaDet.append(" nMes, ");
        sbInsertaDet.append(" cEjercicio, ");
        sbInsertaDet.append(" cIdCuentaContable, ");
        sbInsertaDet.append(" cIdEntidadContable, ");
        sbInsertaDet.append(" cIdRelacion, ");
        sbInsertaDet.append(" EP, ");
        sbInsertaDet.append(" mComprometido, ");
        sbInsertaDet.append(" nPoliza, ");
        sbInsertaDet.append(" ID_TIPO_MOVIMIENTO, ");
        sbInsertaDet.append(" ID_TIPO_CONCEPTO, ");
        sbInsertaDet.append(" cEvento, ");
        sbInsertaDet.append(" aEjercicioFiscal, ");
        sbInsertaDet.append(" cCentroContable, ");
        sbInsertaDet.append(" cMes, ");
        sbInsertaDet.append(" RFC, ");
        sbInsertaDet.append(" mImporteNeto, ");
        sbInsertaDet.append(" mImporteBruto, ");
        sbInsertaDet.append(" mImporteMasIva, ");
        sbInsertaDet.append(" nCapitulo, ");
        sbInsertaDet.append(" mSancion, ");
        sbInsertaDet.append(" mDevolucion, ");
        sbInsertaDet.append(" mImporteAmortiza, ");
        sbInsertaDet.append(" mRetencion, ");
        sbInsertaDet.append(" mPenalizacion, ");
        sbInsertaDet.append(" m2Millar, ");
        sbInsertaDet.append(" m23IVA, ");
        sbInsertaDet.append(" mISRHonorarios, ");
        sbInsertaDet.append(" mObra5, ");
        sbInsertaDet.append(" mImporteFlete4, ");
        sbInsertaDet.append(" mISRArrenda, ");
        sbInsertaDet.append(" mRetImpuestoCedular, ");
        sbInsertaDet.append(" mBruto, ");
        sbInsertaDet.append(" mAmortizacionAnticipo, ");
        sbInsertaDet.append(" mIVA, ");
        sbInsertaDet.append(" mNeto, ");
        sbInsertaDet.append(" m5Millar, ");
        sbInsertaDet.append(" mFletes, ");
        sbInsertaDet.append(" mCedular, ");
        sbInsertaDet.append(" mImporte, ");
        sbInsertaDet.append(" mImporteIvaArrenda, ");
        sbInsertaDet.append(" mImporteIvaHonorarios, ");
        sbInsertaDet.append(" mImporteFlete23, ");
        sbInsertaDet.append(" mImporteIvaProv, ");
        sbInsertaDet.append(" mImporteObra, ");
        sbInsertaDet.append(" mCNIC, ");
        sbInsertaDet.append(" mIMDT, ");
        sbInsertaDet.append(" mTesofe, ");
        sbInsertaDet.append(" altaAlmacen, ");
        sbInsertaDet.append(" Periodo13, ");
        sbInsertaDet.append(" ADEFAS, ");
        sbInsertaDet.append(" OBGT, ");
        sbInsertaDet.append(" mImporteISRLaudos, ");
        sbInsertaDet.append(" mISROtros )");
        sbInsertaDet.append(" SELECT ? as nFolioRELACIONGASTOS, ");
        sbInsertaDet.append(" nDocRenglon, ");
        sbInsertaDet.append(" nMes, ");
        sbInsertaDet.append(" cEjercicio, ");
        sbInsertaDet.append(" cIdCuentaContable, ");
        sbInsertaDet.append(" cIdEntidadContable, ");
        sbInsertaDet.append(" cIdRelacion, ");
        sbInsertaDet.append(" EP, ");
        sbInsertaDet.append(" mComprometido, ");
        sbInsertaDet.append(" nPoliza, ");
        sbInsertaDet.append(" ID_TIPO_MOVIMIENTO, ");
        sbInsertaDet.append(" ID_TIPO_CONCEPTO, ");
        sbInsertaDet.append(" cEvento, ");
        sbInsertaDet.append(" aEjercicioFiscal, ");
        sbInsertaDet.append(" cCentroContable, ");
        sbInsertaDet.append(" cMes, ");
        sbInsertaDet.append(" RFC, ");
        sbInsertaDet.append(" mImporteNeto, ");
        sbInsertaDet.append(" mImporteBruto, ");
        sbInsertaDet.append(" mImporteMasIva, ");
        sbInsertaDet.append(" nCapitulo, ");
        sbInsertaDet.append(" mSancion, ");
        sbInsertaDet.append(" mDevolucion, ");
        sbInsertaDet.append(" mImporteAmortiza, ");
        sbInsertaDet.append(" mRetencion, ");
        sbInsertaDet.append(" mPenalizacion, ");
        sbInsertaDet.append(" m2Millar, ");
        sbInsertaDet.append(" m23IVA, ");
        sbInsertaDet.append(" mISRHonorarios, ");
        sbInsertaDet.append(" mObra5, ");
        sbInsertaDet.append(" mImporteFlete4, ");
        sbInsertaDet.append(" mISRArrenda, ");
        sbInsertaDet.append(" mRetImpuestoCedular, ");
        sbInsertaDet.append(" mBruto, ");
        sbInsertaDet.append(" mAmortizacionAnticipo, ");
        sbInsertaDet.append(" mIVA, ");
        sbInsertaDet.append(" mNeto, ");
        sbInsertaDet.append(" m5Millar, ");
        sbInsertaDet.append(" mFletes, ");
        sbInsertaDet.append(" mCedular, ");
        sbInsertaDet.append(" mImporte, ");
        sbInsertaDet.append(" mImporteIvaArrenda, ");
        sbInsertaDet.append(" mImporteIvaHonorarios, ");
        sbInsertaDet.append(" mImporteFlete23, ");
        sbInsertaDet.append(" mImporteIvaProv, ");
        sbInsertaDet.append(" mImporteObra, ");
        sbInsertaDet.append(" mCNIC, ");
        sbInsertaDet.append(" mIMDT, ");
        sbInsertaDet.append(" mTesofe, ");
        sbInsertaDet.append(" altaAlmacen, ");
        sbInsertaDet.append(" Periodo13, ");
        sbInsertaDet.append(" ADEFAS, ");
        sbInsertaDet.append(" OBGT, ");
        sbInsertaDet.append(" mISRLaudos as mImporteISRLaudos, ");
        sbInsertaDet.append(" mISROtros ");
        sbInsertaDet.append("  FROM	tRELACIONGASTOSDetalle_temp ");
        sbInsertaDet.append(" WHERE	nFolioRELACIONGASTOS = ?");
        PreparedStatement psInsDet = null;
        try {
            psInsDet = conn.prepareStatement(sbInsertaDet.toString());
            psInsDet.setInt(1, rg.getFolioTramite());
            psInsDet.setInt(2, rg.getFolioTramiteTemporal());
            return psInsDet.executeUpdate();
        } finally {
            CloseObject.closeObject(psInsDet);
        }
    }

    private int insertaRGEncabezado(Connection conn, RGMasiva rg, boolean esFirmaElectronica, List<Firmante> firmantes) throws Exception {
        PreparedStatement psIns = null;
        StringBuilder sbInsertaEnc = new StringBuilder();
        sbInsertaEnc.append("INSERT INTO tRELACIONGASTOSEncabezado(nFolioRELACIONGASTOS,  ");
        sbInsertaEnc.append("                                      fAplicacion,  ");
        sbInsertaEnc.append("                                      cRamo,  ");
        sbInsertaEnc.append("                                      cUnidadResponsable,  ");
        sbInsertaEnc.append("                                      cEjercicio,   ");
        sbInsertaEnc.append("                                      cIdEntidadContable,   ");
        sbInsertaEnc.append("                                      cIdRelacion,    ");
        sbInsertaEnc.append("                                      cIdTipoDocumento, ");
        sbInsertaEnc.append("                                      cIdTipoRelacion,  ");
        sbInsertaEnc.append("                                      cIdTipoMontoDesembolso,  ");
        sbInsertaEnc.append("                                      cIdRFC,  ");
        sbInsertaEnc.append("                                      fRecepcion,  ");
        sbInsertaEnc.append("                                      fRevision,  ");
        sbInsertaEnc.append("                                      fProgramadaPago,  ");
        sbInsertaEnc.append("                                      caNoContrarrecibo,   ");
        sbInsertaEnc.append("                                      cConcepto,  ");
        sbInsertaEnc.append("                                      mImporteNeto,  ");
        sbInsertaEnc.append("                                      cIdUnidadAdministrativa,  ");
        sbInsertaEnc.append("                                      cIdGRegional,  ");
        sbInsertaEnc.append("                                      cIdGEstatal,  ");
        sbInsertaEnc.append("                                      cIdDistritoRiego,  ");
        sbInsertaEnc.append("                                      cIdTipoFondo,   ");
        sbInsertaEnc.append("                                      caNoAP,  ");
        sbInsertaEnc.append("                                      cIdEstadoRelacion,  ");
        sbInsertaEnc.append("                                      lContrarreciboImpreso,  ");
        sbInsertaEnc.append("                                      nIdConcepto,  ");
        sbInsertaEnc.append("                                      cIdTipoLimiteDlls,  ");
        sbInsertaEnc.append("                                      cIdUsuarioCaptura,  ");
        sbInsertaEnc.append("                                      cIdUsuarioImpresion,   ");
        sbInsertaEnc.append("                                      cIdUsuarioRevision,  ");
        sbInsertaEnc.append("                                      cIdUsuarioAprobacion,  ");
        sbInsertaEnc.append("                                      cIdUsuarioRechazo,  ");
        sbInsertaEnc.append("                                      lSuficienciaAnualValidada,  ");
        sbInsertaEnc.append("                                      lSuficienciaMensualValidada,  ");
        sbInsertaEnc.append("                                      ID_DESTINO_GASTO,  ");
        sbInsertaEnc.append("                                      ID_TIPO_MOVIMIENTO,  ");
        sbInsertaEnc.append("                                      ID_TIPO_CONCEPTO,  ");
        sbInsertaEnc.append("                                      cnombre,  ");
        sbInsertaEnc.append("                                      TIPO_OPERACION,  ");
        sbInsertaEnc.append("                                      cEvento,  ");
        sbInsertaEnc.append("                                      aEjercicioFiscal,  ");
        sbInsertaEnc.append("                                      cCentroContable,  ");
        sbInsertaEnc.append("                                      cMes,  ");
        sbInsertaEnc.append("                                      RFC,  ");
        sbInsertaEnc.append("                                      mImporteBruto,  ");
        sbInsertaEnc.append("                                      mImporteMasIva,  ");
        sbInsertaEnc.append("                                      cTipoPoliza,  ");
        sbInsertaEnc.append("                                      nEnviadoSICOP,  ");
        sbInsertaEnc.append("                                      cUnidadResponsableContable,  ");
        sbInsertaEnc.append("                                      cDescripcionPoliza,  ");
        sbInsertaEnc.append("                                      cRadicado,  ");
        sbInsertaEnc.append("                                      nFolioCargaMasiva,  ");
        sbInsertaEnc.append("                                      CTAB,  ");
        sbInsertaEnc.append("                                      cInformeComision,  ");
        sbInsertaEnc.append("                                      nIdComision, ");
        sbInsertaEnc.append("                                      nFolioCaja  ");
        sbInsertaEnc.append("                                      ) ");
        sbInsertaEnc.append("SELECT	? as nFolioRELACIONGASTOS,  ");
        sbInsertaEnc.append("         fAplicacion,    ");
        sbInsertaEnc.append("         cRamo,   ");
        sbInsertaEnc.append("         cUnidadResponsable,   ");
        sbInsertaEnc.append("         cEjercicio,   ");
        sbInsertaEnc.append("         cIdEntidadContable,   ");
        sbInsertaEnc.append("         cIdRelacion,  ");
        sbInsertaEnc.append("         cIdTipoDocumento,   ");
        sbInsertaEnc.append("         cIdTipoRelacion,   ");
        sbInsertaEnc.append("         cIdTipoMontoDesembolso,   ");
        sbInsertaEnc.append("         cIdRFC,   ");
        sbInsertaEnc.append("         fRecepcion,   ");
        sbInsertaEnc.append("         fRevision,   ");
        sbInsertaEnc.append("         fProgramadaPago,   ");
        sbInsertaEnc.append("         ?,   ");
        sbInsertaEnc.append("         cConcepto,       ");
        sbInsertaEnc.append("         mImporteNeto,  ");
        sbInsertaEnc.append("         cIdUnidadAdministrativa,   ");
        sbInsertaEnc.append("         cIdGRegional,   ");
        sbInsertaEnc.append("         cIdGEstatal,   ");
        sbInsertaEnc.append("         cIdDistritoRiego,   ");
        sbInsertaEnc.append("         cIdTipoFondo,  ");
        sbInsertaEnc.append("         caNoAP,  ");
        sbInsertaEnc.append("         cIdEstadoRelacion,  ");
        sbInsertaEnc.append("         lContrarreciboImpreso,  ");
        sbInsertaEnc.append("         nIdConcepto,  ");
        sbInsertaEnc.append("         cIdTipoLimiteDlls,  ");
        sbInsertaEnc.append("         cIdUsuarioCaptura,  ");
        sbInsertaEnc.append("         cIdUsuarioImpresion,  ");
        sbInsertaEnc.append("         cIdUsuarioRevision,  ");
        sbInsertaEnc.append("         cIdUsuarioAprobacion,  ");
        sbInsertaEnc.append("         cIdUsuarioRechazo,  ");
        sbInsertaEnc.append("         lSuficienciaAnualValidada,  ");
        sbInsertaEnc.append("         lSuficienciaMensualValidada,  ");
        sbInsertaEnc.append("         ID_DESTINO_GASTO,  ");
        sbInsertaEnc.append("         ID_TIPO_MOVIMIENTO,  ");
        sbInsertaEnc.append("         ID_TIPO_CONCEPTO,  ");
        sbInsertaEnc.append("         cnombre,  ");
        sbInsertaEnc.append("         TIPO_OPERACION,  ");
        sbInsertaEnc.append("         cEvento,  ");
        sbInsertaEnc.append("         aEjercicioFiscal,  ");
        sbInsertaEnc.append("         cCentroContable,  ");
        sbInsertaEnc.append("         cMes,  ");
        sbInsertaEnc.append("         RFC,  ");
        sbInsertaEnc.append("         mImporteBruto,  ");
        sbInsertaEnc.append("         mImporteMasIva,  ");
        sbInsertaEnc.append("         cTipoPoliza,  ");
        sbInsertaEnc.append("         nEnviadoSICOP,  ");
        sbInsertaEnc.append("         cUnidadResponsableContable,  ");
        sbInsertaEnc.append("         cDescripcionPoliza,  ");
        sbInsertaEnc.append("         ISNULL(cRadicado, 'N'),  ");
        sbInsertaEnc.append("         folioTempGral,  ");
        sbInsertaEnc.append("         CTAB,  ");
        sbInsertaEnc.append("         cInformeComision,  ");
        sbInsertaEnc.append("         nIdComision,  ");
        sbInsertaEnc.append("         nFolioCaja ");
        sbInsertaEnc.append("  FROM	tRELACIONGASTOSEncabezado_temp ");
        sbInsertaEnc.append(" WHERE	nFolioRELACIONGASTOS = ?");
        try {
            psIns = conn.prepareStatement(sbInsertaEnc.toString());
            psIns.setInt(1, rg.getFolioTramite());
            psIns.setString(2, rg.getContrarecibo());
            psIns.setInt(3, rg.getFolioTramiteTemporal());
            return psIns.executeUpdate();
        } finally {
            CloseObject.closeObject(psIns);
        }
    }

    public void setReportPath(String reportPath) {
        this.reportPath = reportPath;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public JSONObject validar(String campos, String campoDetCXP, String nomTabla, String nomTablaDet, String idCXP, String idCLC, String nomIdEnc, String nomIdDet, String strFolioEjercido, String strFolioPagado) throws SQLException {
        String valorEnc = "";
        String valorDet = "";
        PreparedStatement pstmntEncCXP = null, pstmntEncSICOP = null, pstmntDetCXP = null, pstmntDetSICOP = null, pstmntSelect = null, pstmntInsert = null, pstmntMax = null, pstmntInsertP = null, pstmntInsertE = null, pstmntInsertPE = null, pstmCent = null;
        ResultSet rs = null, rs1 = null, rs2 = null, rs3 = null, rs4 = null, rs5 = null, rs6 = null;
        Connection conn = null;
        JSONObject json = new JSONObject();
        String idDet = null;
        String epDetSICOP = null;
        // Ajuste
        int nDocRenglonMax = 0;
        // Ajuste
        String cCentroContable = "";
        // Ajuste
        String cEvento = "";
        // Ajuste
        String RFC = "";
        // Ajuste
        int nMes = 0;
        // Ajuste
        int cEjercicio;
        String aEjercicioFiscal = "";
        // String cIdCuentaContable = null;
        double imporDetSICOP = 0;
        double numeMenor;
        double numeMayor;
        double numeMenorDet;
        double numeMayorDet;
        double centavos = 0.00;
        String diferencia = "";
        String camposDet = "";
        try {
            conn = getConnection();
            // Encabezado de CXP
            pstmntEncCXP = conn.prepareStatement("SELECT " + campos + " FROM " + nomTabla + " WITH(NOLOCK) WHERE " + nomIdEnc + " = ?");
            pstmntEncCXP.setString(1, idCXP);
            rs = pstmntEncCXP.executeQuery();
            if (rs.next()) {
                idDet = rs.getString("idFolio");
                // Ajuste
                aEjercicioFiscal = rs.getString("aEjercicioFiscal");
                if (aEjercicioFiscal.equals("2012")) {
                    numeMenor = Double.parseDouble(rs.getString("impNeto")) - 1.00d;
                    numeMayor = Double.parseDouble(rs.getString("impNeto")) + 1.00d;
                } else {
                    pstmCent = conn.prepareStatement("SELECT TOP 1 diferenciaCentavos FROM tEjercidoPagadoCentavo WITH(NOLOCK) WHERE Activo = 1");
                    rs6 = pstmCent.executeQuery();
                    if (rs6.next()) {
                        centavos = rs6.getDouble("diferenciaCentavos");
                    }
                    numeMenor = Double.parseDouble(rs.getString("impNeto")) - centavos;
                    numeMayor = Double.parseDouble(rs.getString("impNeto")) + centavos;
                }
                NumberFormat formatter = new DecimalFormat("###.##");
                double numeMenorFor = Double.parseDouble(formatter.format(numeMenor));
                double numeMayorFor = Double.parseDouble(formatter.format(numeMayor));
                // Encabezado de SICOP conforme a valores obtenidos de
                // encabezado CXP para validar si son iguales.
                String sql = "SELECT NCLC_43 FROM CLC_SICOP WITH(NOLOCK) WHERE NCLC_43 = '" + idCLC + "' AND (SELECT SUM(convert(money,IMP_NETO_107)) as totalSicop FROM CLC_SICOP WITH(NOLOCK) WHERE NCLC_43 = '" + idCLC + "') BETWEEN  " + numeMenorFor + " AND " + numeMayorFor + " GROUP BY NCLC_43";
                pstmntEncSICOP = conn.prepareStatement(sql);
                rs1 = pstmntEncSICOP.executeQuery();
                if (rs1.next()) {
                    valorEnc = "true";
                    if (nomTabla.equals("tNOMINAEncabezado")) {
                        if (valorDet == "iguales") {
                            valorEnc = "nomina";
                            json.put("validacionCorrecta", "validacionCorrecta");
                        }
                    }
                } else {
                    valorEnc = "false";
                    json.put("encaFalso", "encaFalso");
                }
            }
            if (valorEnc == "true") {
                // Detalles de CXP
                pstmntMax = conn.prepareStatement("SELECT MAX(nDocRenglon) AS nDocRenglon FROM " + nomTablaDet + " WITH(NOLOCK) WHERE " + nomIdDet + " = ? ");
                pstmntMax.setString(1, idDet);
                rs5 = pstmntMax.executeQuery();
                while (rs5.next()) {
                    nDocRenglonMax = rs5.getInt("nDocRenglon");
                }
                if (nomTablaDet.equals("tOperAjenasDetalle")) {
                    camposDet = "SELECT " + campoDetCXP + " FROM " + nomTablaDet + " WITH(NOLOCK) WHERE cEvento != 'ANTICIPO' AND cEvento != 'ANTICIPO_DIV' AND " + nomIdDet + " = ? GROUP BY Ep, cCentroContable, aEjercicioFiscal ";
                } else {
                    String idRelacion = (nomTablaDet.equals("tPagoDirectoDetalle")) ? "cIdDocumento" : "cIdRelacion";
                    camposDet = "SELECT " + campoDetCXP + ", SUM(CONVERT(money,mImporteNeto)) AS mImporteNeto FROM " + nomTablaDet + " WITH(NOLOCK) WHERE cEvento != 'ANTICIPO' AND cEvento != 'ANTICIPO_DIV' AND " + nomIdDet + " = ? GROUP BY SUBSTRING(EP, 0,56) , cCentroContable,RFC,cEjercicio,cIdCuentaContable," + idRelacion + ",nCapitulo";
                }
                pstmntDetCXP = conn.prepareStatement(camposDet);
                pstmntDetCXP.setString(1, idDet);
                rs2 = pstmntDetCXP.executeQuery();
                valorDet = "iguales";
                while (rs2.next()) {
                    // CXP
                    epDetSICOP = rs2.getString("EP");
                    imporDetSICOP = rs2.getDouble("mImporteNeto");
                    // Para ingresar en tEjercidoDetalle (Ajuste)
                    cCentroContable = rs2.getString("cCentroContable");
                    // cEvento = rs2.getString("cEvento");
                    cEvento = "0";
                    RFC = (nomTablaDet.equals("tOperAjenasDetalle")) ? "" : rs2.getString("RFC");
                    cEjercicio = Integer.parseInt(rs2.getString("cEjercicio"), 10);
                    // String tipoMovimiento = "0";
                    // String tipoConcepto = "0";
                    if (aEjercicioFiscal.equals("2012")) {
                        numeMenorDet = rs2.getDouble("mImporteNeto") - 1.00d;
                        numeMayorDet = rs2.getDouble("mImporteNeto") + 1.00d;
                    } else {
                        numeMenorDet = rs2.getDouble("mImporteNeto") - centavos;
                        numeMayorDet = rs2.getDouble("mImporteNeto") + centavos;
                    }
                    NumberFormat formatter = new DecimalFormat("###.##");
                    double numeMenorForDet = Double.parseDouble(formatter.format(numeMenorDet));
                    double numeMayorForDet = Double.parseDouble(formatter.format(numeMayorDet));
                    // Detalles de SICOP
                    int renglonn = 1;
                    // Validacion con un centavo mas o menos.
                    String sql = (// +'.'+SUBSTRING(CCAU_162,8,3)+'.'+"
                    // END
                    // //
                    // +'.'+SUBSTRING(CCAU_162,8,3)+'.'+"
                    // ,CCAU_162,CCOP_163"
                    // ,CCAU_162,CCOP_163"
                    "SELECT CANI_150+'.'+ID_RAMO_CR+'.'+ID_UNIDAD_CR+'.'+CGFU_151+'.'+CFUN_152+'.'+" + " CASE LEN(CSFU_153) WHEN 1 THEN '0'+CSFU_153 END+'.'+" + " CASE LEN(CPRG_154) WHEN 1 THEN '0'+CPRG_154 END +'.'+" + " CASE LEN(CAIN_155) WHEN 1 THEN '00'+CAIN_155 WHEN 2 THEN '0'+CAIN_155 END+'.'+CPPT_156+'.'+COBG_183+'.'+CTGA_160+'.'+CFIN_161+'.'+" + " CASE LEN(CGEO_164) WHEN 1 THEN '0'+CGEO_164 WHEN 2 THEN CGEO_164 END+'.'+" + " CASE CPPI_166 WHEN '0' THEN '00000000000' ELSE CPPI_166 END " + " AS EP," + " SUM(CONVERT(money,IMPORTE_148)) AS IMPORTE_148 " + " FROM CLC_SICOP WITH(NOLOCK) " + " WHERE NCLC_43 = '" + idCLC + "' AND " + " CPPT_156 = SUBSTRING('" + epDetSICOP + "',27,4) AND COBG_183 = SUBSTRING('" + epDetSICOP + "',32,5) AND " + " CASE LEN(CGEO_164) WHEN 1 THEN '0'+CGEO_164 WHEN 2 THEN CGEO_164 END = SUBSTRING('" + epDetSICOP + "',42,2) AND " + " CGFU_151 = SUBSTRING('" + epDetSICOP + "',13,1) AND " + " CASE LEN(CSFU_153) WHEN 1 THEN '0'+CSFU_153 END = SUBSTRING('" + epDetSICOP + "',17,2) AND " + " CASE LEN(CPRG_154) WHEN 1 THEN '0'+CPRG_154 END = SUBSTRING('" + epDetSICOP + "',20,2) AND " + " CFUN_152 = SUBSTRING('" + epDetSICOP + "',15,1) AND " + " CASE CPPI_166 WHEN '0' THEN '00000000000' ELSE CPPI_166 END = SUBSTRING('" + epDetSICOP + "',45,11) AND " + " (" + " SELECT CANI_150+'.'+ID_RAMO_CR+'.'+ID_UNIDAD_CR+'.'+CGFU_151+'.'+CFUN_152+'.'+" + " CASE LEN(CSFU_153) WHEN 1 THEN '0'+CSFU_153 END+'.'+" + " CASE LEN(CPRG_154) WHEN 1 THEN '0'+CPRG_154 END +'.'+" + " CASE LEN(CAIN_155) WHEN 1 THEN '00'+CAIN_155 WHEN 2 THEN '0'+CAIN_155 END+'.'+" + " CPPT_156+'.'+COBG_183+'.'+CTGA_160+'.'+CFIN_161+'.'+" + " CASE LEN(CGEO_164) WHEN 1 THEN '0'+CGEO_164 WHEN 2 THEN CGEO_164 END+'.'+" + " CASE CPPI_166 WHEN '0' THEN '00000000000' ELSE CPPI_166 " + " END AS EP" + " FROM CLC_SICOP WITH(NOLOCK) " + " WHERE NCLC_43 = '" + idCLC + "' AND " + " CPPT_156 = SUBSTRING('" + epDetSICOP + "',27,4) AND COBG_183 = SUBSTRING('" + epDetSICOP + "',32,5) AND " + " CASE LEN(CGEO_164) WHEN 1 THEN '0'+CGEO_164 WHEN 2 THEN CGEO_164 END = SUBSTRING('" + epDetSICOP + "',42,2) AND " + " CGFU_151 = SUBSTRING('" + epDetSICOP + "',13,1) AND " + " CASE LEN(CSFU_153) WHEN 1 THEN '0'+CSFU_153 END = SUBSTRING('" + epDetSICOP + "',17,2) AND " + " CASE LEN(CPRG_154) WHEN 1 THEN '0'+CPRG_154 END = SUBSTRING('" + epDetSICOP + "',20,2) AND " + " CFUN_152 = SUBSTRING('" + epDetSICOP + "',15,1) AND " + " CASE CPPI_166 WHEN '0' THEN '00000000000' ELSE CPPI_166 END = SUBSTRING('" + epDetSICOP + "',45,11) " + " GROUP BY CANI_150,ID_RAMO_CR,ID_UNIDAD_CR,CGFU_151,CFUN_152,CSFU_153,CPRG_154,CAIN_155,CPPT_156,COBG_183,CTGA_160,CFIN_161,CGEO_164,CPPI_166" + " HAVING(SUM(CONVERT(money,IMPORTE_148))) BETWEEN " + numeMenorForDet + " AND " + numeMayorForDet + ") = '" + epDetSICOP + "'" + " GROUP BY CANI_150,ID_RAMO_CR,ID_UNIDAD_CR,CGFU_151,CFUN_152,CSFU_153,CPRG_154,CAIN_155,CPPT_156,COBG_183,CTGA_160,CFIN_161,CGEO_164,CPPI_166" + " HAVING(SUM(CONVERT(money,IMPORTE_148))) BETWEEN " + numeMenorForDet + " AND " + numeMayorForDet);
                    pstmntDetSICOP = conn.prepareStatement(sql);
                    System.out.println("sqlDet:" + sql);
                    rs3 = pstmntDetSICOP.executeQuery();
                    if (rs3.next()) {
                        // Si no Existe lo agrega para hacer el Ajuste
                        if (imporDetSICOP != rs3.getDouble("IMPORTE_148")) {
                            java.util.Date utilDate = new java.util.Date();
                            long lnMilisegundos = utilDate.getTime();
                            java.sql.Date sqlDate = new java.sql.Date(lnMilisegundos);
                            String[] mes = String.valueOf(sqlDate).split("-");
                            nMes = Integer.parseInt(mes[1], 10);
                            valorDet = "errorCentavos";
                            json.put("diferenciaCentavo", "diferenciaCentavo");
                            pstmntSelect = conn.prepareStatement("SELECT * FROM tEjercidoDetalle WITH(NOLOCK) WHERE EP = ? AND nFolioPago = ? ");
                            pstmntSelect.setString(1, epDetSICOP);
                            pstmntSelect.setInt(2, Integer.parseInt(idDet, 10));
                            rs4 = pstmntSelect.executeQuery();
                        }
                    } else {
                        renglonn = renglonn + 1;
                        valorDet = "errorDetalles";
                        diferencia += " EP: " + epDetSICOP + "   IMPORTE: " + imporDetSICOP + " \n";
                        json.put("errorDetalles", diferencia);
                    }
                }
                if (valorDet == "iguales") {
                    json.put("validacionCorrecta", "validacionCorrecta");
                }
            }
        } catch (Exception e) {
            log.error("Error occurred", "Error: " + e);
            try {
                conn.rollback();
            } catch (Exception ee) {
                log.warn("Error: cerrando rollback ", ee);
            }
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(rs1);
            CloseObject.closeObject(rs2);
            CloseObject.closeObject(rs3);
            CloseObject.closeObject(rs6);
            CloseObject.closeObject(pstmntEncCXP);
            CloseObject.closeObject(pstmntEncSICOP);
            CloseObject.closeObject(pstmntDetSICOP);
            CloseObject.closeObject(pstmntDetCXP);
            CloseObject.closeObject(pstmntInsert);
            CloseObject.closeObject(pstmntInsertP);
            CloseObject.closeObject(pstmntInsertPE);
            CloseObject.closeObject(pstmntInsertE);
            CloseObject.closeObject(pstmCent);
            CloseObject.closeObject(conn);
        }
        return json;
    }

    public JSONObject validarCXPIntegracion(String strCaNoContrarrecibo, String strSicop, String strFolios, String strTipoDocumentos, String strFolioIntegracion) throws SQLException {
        PreparedStatement pstmntS = null, pstmnt = null, pstmntInsert = null, pstmntSicop = null, pstmCent = null;
        ResultSet rs = null, rs1 = null, rs2 = null, rs3 = null;
        Connection conn = null;
        JSONObject json = new JSONObject();
        String diferencia = "";
        String tablaDet = "";
        String camposDet = "";
        String nFolio = "";
        String doc = "";
        String campoFolio = "";
        String valorDet = "iguales";
        String EpSicop = "";
        String importeSicop = "";
        String aEjercicioFiscal = "";
        double centavos = 0.00;
        try {
            conn = getConnection();
            String[] nF = strFolios.split("/");
            String[] nTD = strTipoDocumentos.split("/");
            String[] nCa = strCaNoContrarrecibo.split("/");
            for (int e = 0; e < nF.length; e++) {
                doc = nTD[e];
                if (doc.equals("p_directo")) {
                    camposDet = " nFolioPagoDirecto AS nFolio, EP AS EP, mImporteNeto AS importe, cEjercicio ";
                    tablaDet = " tPagoDirectoDetalle ";
                    campoFolio = "nFolioPagoDirecto";
                    nFolio = nF[e];
                } else if (doc.equals("r_gastos")) {
                    camposDet = " nFolioRELACIONGASTOS AS nFolio, EP AS EP, mImporteNeto AS importe, aEjercicioFiscal AS cEjercicio ";
                    tablaDet = " tRELACIONGASTOSDetalle ";
                    campoFolio = "nFolioRELACIONGASTOS";
                    nFolio = nF[e];
                } else if (doc.equals("p_diverso")) {
                    camposDet = " nFolioPAGODIVERSO AS nFolio, EP AS EP, mImporteNeto AS importe, aEjercicioFiscal AS cEjercicio ";
                    tablaDet = " tPAGODIVERSODetalle ";
                    campoFolio = "nFolioPAGODIVERSO";
                    nFolio = nF[e];
                } else if (doc.equals("p_obra")) {
                    camposDet = " nFolioPAGOOBRA AS nFolio, EP AS EP, mImporteNeto AS importe, aEjercicioFiscal AS cEjercicio ";
                    tablaDet = " tPAGOOBRADetalle ";
                    campoFolio = "nFolioPagoDirecto";
                    nFolio = nF[e];
                } else if (doc.equals("nomina")) {
                    camposDet = " nFolioNOMINACLC AS nFolio, EP AS EP, mImporteNeto AS importe, aEjercicioFiscal AS cEjercicio ";
                    tablaDet = " tNOMINACLCDetalle ";
                    campoFolio = "nFolioNOMINACLC";
                    nFolio = nF[e];
                } else if (doc.equals("federalizado")) {
                    camposDet = " nFolioPAGOFEDERALIZADO AS nFolio, EP AS EP, mImporteNeto AS importe, aEjercicioFiscal AS cEjercicio ";
                    tablaDet = " tPAGOFEDERALIZADODetalle ";
                    campoFolio = "nFolioPAGOFEDERALIZADO";
                    nFolio = nF[e];
                } else if (doc.equals("o_ajenas")) {
                    tablaDet = "tOperAjenasDetalle";
                    camposDet = "nFolioOperAjenas AS nFolio, EP AS EP, mTotal AS importe, aEjercicioFiscal AS cEjercicio";
                    campoFolio = "nFolioOperAjenas";
                    nFolio = nF[e];
                }
                // Trae Informacion de la Tabla Detalle CXP
                pstmnt = conn.prepareStatement("SELECT " + camposDet + " FROM " + tablaDet + " WITH(NOLOCK) WHERE " + campoFolio + " = ? ");
                pstmnt.setInt(1, Integer.parseInt(nF[e], 10));
                rs = pstmnt.executeQuery();
                while (rs.next()) {
                    aEjercicioFiscal = rs.getString("cEjercicio");
                    String EP = rs.getString("EP");
                    String importe = rs.getString("importe");
                    // Guarda Informacion En Tabla tEjercidoPagadoIntegracion
                    // Para Comparar y agrupar por EP
                    pstmntInsert = conn.prepareStatement("INSERT INTO tEjercidoPagadoIntegracion (nFolioEjercidoPagadoIntegracion,cTipoDocumento,caNoContrarrecibo,nFolio,EP,mImporteNeto) " + "	VALUES(" + strFolioIntegracion + ",'" + nTD[e] + "','" + nCa[e] + "','" + nF[e] + "','" + EP + "'," + importe + " )");
                    pstmntInsert.executeUpdate();
                }
                conn.commit();
            }
            // Muestra Informacion SICOP
            String sicop = " SELECT CANI_150+'.'+ID_RAMO_CR+'.'+ID_UNIDAD_CR+'.'+CGFU_151+'.'+CFUN_152+'.'+CASE LEN(CSFU_153) WHEN 1 THEN '0'+CSFU_153 END+'.'+ " + "CASE LEN(CPRG_154) WHEN 1 THEN '0'+CPRG_154 END +'.'+CASE LEN(CAIN_155) WHEN 1 THEN '00'+CAIN_155 WHEN 2 THEN '0'+CAIN_155 END+'.'+ " + "CPPT_156+'.'+COBG_183+'.'+CTGA_160+'.'+CFIN_161+'.'+CASE LEN(CGEO_164) WHEN 1 THEN '0'+CGEO_164 WHEN 2 THEN CGEO_164 END+'.'+ " + "CASE CPPI_166 WHEN '0' THEN '00000000000' ELSE CPPI_166 END +'.'+ SUBSTRING(CCAU_162,8,3)+'.'+CASE LEN(CCOP_163) WHEN 1 THEN 'B0'+" + "CCOP_163 WHEN 2 THEN 'B'+CCOP_163 WHEN 3 THEN CCOP_163 END AS EP, " + "SUM(CONVERT(money,IMPORTE_148)) AS IMPORTE_148 " + " FROM CLC_SICOP WITH(NOLOCK) " + " WHERE NCLC_43 = '" + strSicop + "' " + " GROUP BY CANI_150, ID_RAMO_CR, ID_UNIDAD_CR, CGFU_151, CFUN_152,	CSFU_153, CPRG_154, CAIN_155, CPPT_156, COBG_183, CTGA_160, " + " CFIN_161, CGEO_164, CPPI_166, CCAU_162, CCOP_163";
            pstmntSicop = conn.prepareStatement(sicop);
            rs1 = pstmntSicop.executeQuery();
            while (rs1.next()) {
                EpSicop = rs1.getString("EP");
                double numeMenorDet;
                double numeMayorDet;
                if (aEjercicioFiscal.equals("2012")) {
                    numeMenorDet = rs1.getDouble("IMPORTE_148") - 1.00d;
                    numeMayorDet = rs1.getDouble("IMPORTE_148") + 1.00d;
                } else {
                    pstmCent = conn.prepareStatement("SELECT TOP 1 diferenciaCentavos FROM tEjercidoPagadoCentavo WITH(NOLOCK) WHERE Activo = 1");
                    rs3 = pstmCent.executeQuery();
                    if (rs3.next()) {
                        centavos = rs3.getDouble("diferenciaCentavos");
                    }
                    numeMenorDet = rs1.getDouble("IMPORTE_148") - centavos;
                    numeMayorDet = rs1.getDouble("IMPORTE_148") + centavos;
                }
                NumberFormat formatterr = new DecimalFormat("###.##");
                double numeMenorForDet = Double.parseDouble(formatterr.format(numeMenorDet));
                double numeMayorForDet = Double.parseDouble(formatterr.format(numeMayorDet));
                // Compara Informacion De la Tabla tEjercidoPagadoIntegracion
                // Con las de Sicop
                pstmntS = conn.prepareStatement(" SELECT EP, SUM(mImporteNeto) as importeEP " + " FROM tEjercidoPagadoIntegracion WITH(NOLOCK) " + " WHERE nFolioEjercidoPagadoIntegracion = " + strFolioIntegracion + " AND EP = '" + EpSicop + "' " + " AND (select convert(money,(SUM(mImporteNeto))) " + " from tEjercidoPagadoIntegracion WITH(NOLOCK) " + " where nFolioEjercidoPagadoIntegracion = " + strFolioIntegracion + " AND EP = '" + EpSicop + "') BETWEEN CONVERT(money," + numeMenorForDet + ") AND CONVERT(money," + numeMayorForDet + ") " + " GROUP BY EP");
                rs2 = pstmntS.executeQuery();
                if (!rs2.next()) {
                    valorDet = "errorDetalles";
                    diferencia += " EP: " + EpSicop + "   IMPORTE: " + importeSicop + " \n";
                    json.put("errorDetalles", diferencia);
                }
            }
            if (valorDet.equals("iguales")) {
                json.put("validacionCorrecta", "validacionCorrecta");
            }
        } catch (Exception e) {
            log.error("Error occurred", "Error: " + e);
            conn.rollback();
            try {
                conn.rollback();
            } catch (Exception ee) {
                log.warn("Error: cerrando rollback ", ee);
            }
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(rs1);
            CloseObject.closeObject(rs2);
            CloseObject.closeObject(rs3);
            CloseObject.closeObject(pstmnt);
            CloseObject.closeObject(pstmntInsert);
            CloseObject.closeObject(pstmntSicop);
            CloseObject.closeObject(pstmntS);
            CloseObject.closeObject(pstmCent);
            CloseObject.closeObject(conn);
        }
        return json;
    }

    public JSONObject validarIntegracion(String nomTabla, String idCXPIntegracion, String idCLC, String tipoCxp, String strFolioEjercido, String strFolioPagado) throws SQLException {
        String valorEnc = "";
        String valorDet = "";
        PreparedStatement pstmntHeader = null, pstmntEncSICOP = null, pstmnDif = null, pstmntDetCXP = null, pstmntDetSICOP = null, pstmntSelect = null, pstmntInsert = null, pstmntMax = null, pstmntInsertP = null, pstmntInsertE = null, pstmntInsertPE = null, pstmntEje = null, pstmntEjeD = null, pstmCent = null;
        ResultSet rs = null, rs1 = null, rs2 = null, rs3 = null, rs4 = null, rs5 = null, rs6 = null, rs7 = null, rs8 = null;
        Connection conn = null;
        JSONObject json = new JSONObject();
        // String idDet = null;
        String epDetSICOP = null;
        // Ajuste
        int nDocRenglonMax = 0;
        // Ajuste
        String cCentroContable = "";
        // Ajuste
        String cEvento = "";
        // Ajuste
        String RFC = "";
        // Ajuste
        int nMes = 0;
        // Ajuste
        int cEjercicio;
        String aEjercicioFiscal = "";
        // String cIdCuentaContable = null;
        double importIntegracion = 0;
        double imporDetSICOP = 0;
        String diferencia = "";
        String nFolios = "";
        // String nFoliosEnc = "";
        double numeMenor;
        double numeMayor;
        double numeMenorDet;
        double numeMayorDet;
        double centavos = 0.00;
        try {
            conn = getConnection();
            if (tipoCxp.equals("integracion")) {
                // Traer Cuentas por Pagar de
                // tLayoutsCreadosRelacionGastosHeader
                pstmntHeader = conn.prepareStatement("SELECT l.sNoContrarrecibo, r.nFolioRELACIONGASTOS AS idFolio, r.mImporteNeto as impNeto,r.aEjercicioFiscal FROM tLayoutsCreadosRelacionGastosHeader l WITH(NOLOCK) INNER JOIN tRELACIONGASTOSEncabezado r WITH(NOLOCK) ON l.sNoContrarrecibo = r.caNoContrarrecibo WHERE sAuxiliarComodin = ? ");
                pstmntHeader.setString(1, idCXPIntegracion);
                rs = pstmntHeader.executeQuery();
                while (rs.next()) {
                    importIntegracion = importIntegracion + rs.getDouble("impNeto");
                    nFolios += rs.getString("idFolio");
                    nFolios += ",";
                    // Ajuste
                    aEjercicioFiscal = rs.getString("aEjercicioFiscal");
                }
            } else {
                // Para Relacion de Gastos Diferentes
                String[] nFe = idCXPIntegracion.split("/");
                for (int e = 1; e < nFe.length; e++) {
                    pstmnDif = conn.prepareStatement("SELECT nFolioRELACIONGASTOS AS idFolio, mImporteNeto AS impNeto,aEjercicioFiscal FROM tRELACIONGASTOSEncabezado WITH(NOLOCK) WHERE caNoContrarrecibo = ? ");
                    pstmnDif.setString(1, nFe[e]);
                    rs = pstmnDif.executeQuery();
                    while (rs.next()) {
                        importIntegracion = importIntegracion + rs.getDouble("impNeto");
                        nFolios += rs.getString("idFolio");
                        nFolios += ",";
                        // Ajuste
                        aEjercicioFiscal = rs.getString("aEjercicioFiscal");
                    }
                }
                // Buscar Relaciones de gastos ejercidas para validar con las
                // nuevas para ejercer
                pstmntEje = conn.prepareStatement("SELECT nFolioPago FROM tEjercidoEncabezado WITH(NOLOCK) WHERE nFolioSicop = ?");
                pstmntEje.setString(1, idCLC);
                rs6 = pstmntEje.executeQuery();
                while (rs6.next()) {
                    pstmntEjeD = conn.prepareStatement("SELECT SUM(mImporteNeto) AS importeNetoEjercido FROM tEjercidoDetalle WITH(NOLOCK) WHERE nFolioPago = ? AND cTipoPago = 'RELACIONGASTOS' ");
                    pstmntEjeD.setString(1, rs6.getString("nFolioPago"));
                    rs7 = pstmntEjeD.executeQuery();
                    if (rs7.next()) {
                        importIntegracion = importIntegracion + rs7.getDouble("importeNetoEjercido");
                    }
                    nFolios += rs6.getString("nFolioPago");
                    nFolios += ",";
                }
            }
            if (aEjercicioFiscal.equals("2012")) {
                numeMenor = importIntegracion - 1.00d;
                numeMayor = importIntegracion + 1.00d;
            } else {
                pstmCent = conn.prepareStatement("SELECT TOP 1 diferenciaCentavos FROM tEjercidoPagadoCentavo WITH(NOLOCK) WHERE Activo = 1");
                rs8 = pstmCent.executeQuery();
                if (rs8.next()) {
                    centavos = rs8.getDouble("diferenciaCentavos");
                }
                numeMenor = importIntegracion - centavos;
                numeMayor = importIntegracion + centavos;
            }
            NumberFormat formatter = new DecimalFormat("###.##");
            double numeMenorFor = Double.parseDouble(formatter.format(numeMenor));
            double numeMayorFor = Double.parseDouble(formatter.format(numeMayor));
            // Encabezado de SICOP conforme a valores obtenidos de Header CXP
            // para validar si son iguales.
            String sql = "SELECT NCLC_43 FROM CLC_SICOP WITH(NOLOCK) WHERE NCLC_43 = '" + idCLC + "' AND (SELECT SUM(convert(money,IMP_NETO_107)) as totalSicop FROM CLC_SICOP WITH(NOLOCK) WHERE NCLC_43 = '" + idCLC + "') BETWEEN  " + numeMenorFor + " AND " + numeMayorFor + " GROUP BY NCLC_43";
            pstmntEncSICOP = conn.prepareStatement(sql);
            rs1 = pstmntEncSICOP.executeQuery();
            if (rs1.next()) {
                valorEnc = "true";
                // System.out.println("true");
            } else {
                valorEnc = "false";
                json.put("encaFalso", "encaFalso");
                // System.out.println("encabezado false");
            }
            if (valorEnc == "true") {
                int valorr = nFolios.length() - 1;
                String sFolioQueryy = nFolios.substring(0, valorr);
                String[] nF = nFolios.split("/");
                // Detalles de CXP
                if (tipoCxp.equals("integracion")) {
                    for (int i = 0; i < nF.length; i++) {
                        pstmntDetCXP = conn.prepareStatement("SELECT EP, SUM(CONVERT(money,mImporteNeto)) AS mImporteNeto,cCentroContable,cEjercicio FROM tRELACIONGASTOSDetalle WITH(NOLOCK) WHERE cEvento != 'ANTICIPO' AND cEvento != 'ANTICIPO_DIV' AND nFolioRELACIONGASTOS IN (" + sFolioQueryy + ")  GROUP BY EP, cCentroContable, cEjercicio, cIdCuentaContable");
                        rs2 = pstmntDetCXP.executeQuery();
                        valorDet = "iguales";
                        while (rs2.next()) {
                            // CXP
                            epDetSICOP = rs2.getString("EP");
                            imporDetSICOP = rs2.getDouble("mImporteNeto");
                            // Para ingresar en tEjercidoDetalle (Ajuste)
                            cCentroContable = rs2.getString("cCentroContable");
                            cEvento = "P_";
                            RFC = "0";
                            cEjercicio = Integer.parseInt(rs2.getString("cEjercicio"), 10);
                            if (aEjercicioFiscal.equals("2012")) {
                                numeMenorDet = rs2.getDouble("mImporteNeto") - 1.00d;
                                numeMayorDet = rs2.getDouble("mImporteNeto") + 1.00d;
                            } else {
                                numeMenorDet = rs2.getDouble("mImporteNeto") - centavos;
                                numeMayorDet = rs2.getDouble("mImporteNeto") + centavos;
                            }
                            NumberFormat formatterr = new DecimalFormat("###.##");
                            double numeMenorForDet = Double.parseDouble(formatterr.format(numeMenorDet));
                            double numeMayorForDet = Double.parseDouble(formatterr.format(numeMayorDet));
                            // Detalles de SICOP
                            int renglonn = 1;
                            String sqll = (// +'.'+SUBSTRING(CCAU_162,8,3)+'.'+"
                            // END
                            // +'.'+SUBSTRING(CCAU_162,8,3)+'.'+"
                            // ,CCAU_162,CCOP_163
                            // //
                            // "
                            "SELECT CANI_150+'.'+ID_RAMO_CR+'.'+ID_UNIDAD_CR+'.'+CGFU_151+'.'+CFUN_152+'.'+" + " CASE LEN(CSFU_153) WHEN 1 THEN '0'+CSFU_153 END+'.'+" + " CASE LEN(CPRG_154) WHEN 1 THEN '0'+CPRG_154 END +'.'+" + " CASE LEN(CAIN_155) WHEN 1 THEN '00'+CAIN_155 WHEN 2 THEN '0'+CAIN_155 END+'.'+CPPT_156+'.'+COBG_183+'.'+CTGA_160+'.'+CFIN_161+'.'+" + " CASE LEN(CGEO_164) WHEN 1 THEN '0'+CGEO_164 WHEN 2 THEN CGEO_164 END+'.'+" + " CASE CPPI_166 WHEN '0' THEN '00000000000' ELSE CPPI_166 END AS EP," + " SUM(CONVERT(money,IMPORTE_148)) AS IMPORTE_148 " + " FROM CLC_SICOP WITH(NOLOCK) " + " WHERE NCLC_43 = '" + idCLC + "' AND " + " CPPT_156 = SUBSTRING('" + epDetSICOP + "',27,4) AND COBG_183 = SUBSTRING('" + epDetSICOP + "',32,5) AND " + " CASE LEN(CGEO_164) WHEN 1 THEN '0'+CGEO_164 WHEN 2 THEN CGEO_164 END = SUBSTRING('" + epDetSICOP + "',42,2) AND " + " CGFU_151 = SUBSTRING('" + epDetSICOP + "',13,1) AND " + " CASE LEN(CSFU_153) WHEN 1 THEN '0'+CSFU_153 END = SUBSTRING('" + epDetSICOP + "',17,2) AND " + " CASE LEN(CPRG_154) WHEN 1 THEN '0'+CPRG_154 END = SUBSTRING('" + epDetSICOP + "',20,2) " + " AND " + " CFUN_152 = SUBSTRING('" + epDetSICOP + "',15,1) AND " + " CASE CPPI_166 WHEN '0' THEN '00000000000' ELSE CPPI_166 END = SUBSTRING('" + epDetSICOP + "',45,11) AND " + " (" + " SELECT CANI_150+'.'+ID_RAMO_CR+'.'+ID_UNIDAD_CR+'.'+CGFU_151+'.'+CFUN_152+'.'+" + " CASE LEN(CSFU_153) WHEN 1 THEN '0'+CSFU_153 END+'.'+" + " CASE LEN(CPRG_154) WHEN 1 THEN '0'+CPRG_154 END +'.'+" + " CASE LEN(CAIN_155) WHEN 1 THEN '00'+CAIN_155 WHEN 2 THEN '0'+CAIN_155 END+'.'+" + " CPPT_156+'.'+COBG_183+'.'+CTGA_160+'.'+CFIN_161+'.'+" + " CASE LEN(CGEO_164) WHEN 1 THEN '0'+CGEO_164 WHEN 2 THEN CGEO_164 END+'.'+" + " CASE CPPI_166 WHEN '0' THEN '00000000000' ELSE CPPI_166 END AS EP " + " FROM CLC_SICOP WITH(NOLOCK) " + " WHERE NCLC_43 = '" + idCLC + "' AND " + " CPPT_156 = SUBSTRING('" + epDetSICOP + "',27,4) AND COBG_183 = SUBSTRING('" + epDetSICOP + "',32,5) AND " + " CASE LEN(CGEO_164) WHEN 1 THEN '0'+CGEO_164 WHEN 2 THEN CGEO_164 END = SUBSTRING('" + epDetSICOP + "',42,2) AND " + " CGFU_151 = SUBSTRING('" + epDetSICOP + "',13,1) AND " + " CASE LEN(CSFU_153) WHEN 1 THEN '0'+CSFU_153 END = SUBSTRING('" + epDetSICOP + "',17,2) AND " + " CASE LEN(CPRG_154) WHEN 1 THEN '0'+CPRG_154 END = SUBSTRING('" + epDetSICOP + "',20,2) " + " AND " + " CFUN_152 = SUBSTRING('" + epDetSICOP + "',15,1) AND " + " CASE CPPI_166 WHEN '0' THEN '00000000000' ELSE CPPI_166 END = SUBSTRING('" + epDetSICOP + "',45,11) " + " GROUP BY CANI_150,ID_RAMO_CR,ID_UNIDAD_CR,CGFU_151,CFUN_152,CSFU_153,CPRG_154,CAIN_155,CPPT_156,COBG_183,CTGA_160,CFIN_161,CGEO_164,CPPI_166" + " HAVING(SUM(CONVERT(money,IMPORTE_148))) BETWEEN " + numeMenorForDet + // ,CCAU_162,CCOP_163
                            " AND " + // //
                            numeMayorForDet + // "
                            ") = '" + epDetSICOP.substring(0, 55) + "'" + " GROUP BY CANI_150,ID_RAMO_CR,ID_UNIDAD_CR,CGFU_151,CFUN_152,CSFU_153,CPRG_154,CAIN_155,CPPT_156,COBG_183,CTGA_160,CFIN_161,CGEO_164,CPPI_166" + " HAVING(SUM(CONVERT(money,IMPORTE_148))) BETWEEN " + numeMenorForDet + " AND " + numeMayorForDet);
                            pstmntDetSICOP = conn.prepareStatement(sqll);
                            rs3 = pstmntDetSICOP.executeQuery();
                            if (rs3.next()) {
                                // Si no Existe lo agrega para hacer el Ajuste
                                if (imporDetSICOP != rs3.getDouble("IMPORTE_148")) {
                                    java.util.Date utilDate = new java.util.Date();
                                    long lnMilisegundos = utilDate.getTime();
                                    java.sql.Date sqlDate = new java.sql.Date(lnMilisegundos);
                                    String[] mes = String.valueOf(sqlDate).split("-");
                                    nMes = Integer.parseInt(mes[1], 10);
                                    valorDet = "errorCentavos";
                                    json.put("diferenciaCentavo", "diferenciaCentavo");
                                    pstmntSelect = conn.prepareStatement("SELECT * FROM tEjercidoDetalle WITH(NOLOCK) WHERE EP = ? AND nFolioPago = ? ");
                                    pstmntSelect.setString(1, epDetSICOP);
                                    pstmntSelect.setInt(2, Integer.parseInt(nF[i], 10));
                                    rs4 = pstmntSelect.executeQuery();
                                }
                            } else {
                                renglonn = renglonn + 1;
                                valorDet = "errorDetalles";
                                diferencia += " EP: " + epDetSICOP + "   IMPORTE: " + imporDetSICOP + " \n";
                                json.put("errorDetalles", diferencia);
                            }
                        }
                    }
                } else {
                    // Para Relacion de Gastos Diferentes
                    int valor = nFolios.length() - 1;
                    String sFolioQuery = nFolios.substring(0, valor);
                    for (int i = 0; i < nF.length; i++) {
                        pstmntMax = conn.prepareStatement("SELECT COUNT(nDocRenglon) AS nDocRenglon FROM tRELACIONGASTOSDetalle WITH(NOLOCK) WHERE nFolioRELACIONGASTOS IN (" + sFolioQuery + ") ");
                        rs5 = pstmntMax.executeQuery();
                        while (rs5.next()) {
                            nDocRenglonMax = rs5.getInt("nDocRenglon") + 1;
                        }
                        pstmntDetCXP = conn.prepareStatement("SELECT EP, SUM(CONVERT(money,mImporteNeto)) AS mImporteNeto,cCentroContable,cEjercicio, nCapitulo FROM tRELACIONGASTOSDetalle WITH(NOLOCK) WHERE cEvento != 'ANTICIPO' AND nFolioRELACIONGASTOS IN (" + sFolioQuery + ") GROUP BY EP , cCentroContable,cEjercicio,nCapitulo");
                        rs2 = pstmntDetCXP.executeQuery();
                        valorDet = "iguales";
                        while (rs2.next()) {
                            // CXP
                            epDetSICOP = rs2.getString("EP");
                            imporDetSICOP = rs2.getDouble("mImporteNeto");
                            // Para ingresar en tEjercidoDetalle (Ajuste)
                            cCentroContable = rs2.getString("cCentroContable");
                            cEvento = "";
                            RFC = "";
                            cEjercicio = Integer.parseInt(rs2.getString("cEjercicio"), 10);
                            if (aEjercicioFiscal.equals("2012")) {
                                numeMenorDet = rs2.getDouble("mImporteNeto") - 1.00d;
                                numeMayorDet = rs2.getDouble("mImporteNeto") + 1.00d;
                            } else {
                                numeMenorDet = rs2.getDouble("mImporteNeto") - centavos;
                                numeMayorDet = rs2.getDouble("mImporteNeto") + centavos;
                            }
                            NumberFormat formatterr = new DecimalFormat("###.##");
                            double numeMenorForDet = Double.parseDouble(formatterr.format(numeMenorDet));
                            double numeMayorForDet = Double.parseDouble(formatterr.format(numeMayorDet));
                            // Detalles de SICOP
                            int renglonn = 1;
                            String sqll = ("SELECT CANI_150+'.'+ID_RAMO_CR+'.'+ID_UNIDAD_CR+'.'+CGFU_151+'.'+CFUN_152+'.'+" + " CASE LEN(CSFU_153) WHEN 1 THEN '0'+CSFU_153 END+'.'+" + " CASE LEN(CPRG_154) WHEN 1 THEN '0'+CPRG_154 END +'.'+" + " CASE LEN(CAIN_155) WHEN 1 THEN '00'+CAIN_155 WHEN 2 THEN '0'+CAIN_155 END+'.'+CPPT_156+'.'+COBG_183+'.'+CTGA_160+'.'+CFIN_161+'.'+" + " CASE LEN(CGEO_164) WHEN 1 THEN '0'+CGEO_164 WHEN 2 THEN CGEO_164 END+'.'+" + " CASE CPPI_166 WHEN '0' THEN '00000000000' ELSE CPPI_166 END +'.'+SUBSTRING(CCAU_162,8,3)+'.'+" + " CASE LEN(CCOP_163) WHEN 1 THEN 'B0'+CCOP_163 WHEN 2 THEN 'B'+CCOP_163 WHEN 3 THEN CCOP_163 END AS EP," + " SUM(CONVERT(money,IMPORTE_148)) AS IMPORTE_148 " + " FROM CLC_SICOP WITH(NOLOCK) " + " WHERE NCLC_43 = '" + idCLC + "' AND " + " CPPT_156 = SUBSTRING('" + epDetSICOP + "',27,4) AND COBG_183 = SUBSTRING('" + epDetSICOP + "',32,5) AND " + " CASE LEN(CCOP_163) WHEN 1 THEN 'B0'+CCOP_163 WHEN 2 THEN 'B'+CCOP_163 WHEN 3 THEN CCOP_163 END = SUBSTRING('" + epDetSICOP + "',61,3) AND" + " CASE LEN(CGEO_164) WHEN 1 THEN '0'+CGEO_164 WHEN 2 THEN CGEO_164 END = SUBSTRING('" + epDetSICOP + "',42,2) AND " + " CGFU_151 = SUBSTRING('" + epDetSICOP + "',13,1) AND " + " CASE LEN(CSFU_153) WHEN 1 THEN '0'+CSFU_153 END = SUBSTRING('" + epDetSICOP + "',17,2) AND " + " CASE LEN(CPRG_154) WHEN 1 THEN '0'+CPRG_154 END = SUBSTRING('" + epDetSICOP + "',20,2) AND " + " SUBSTRING(CCAU_162,8,3) = SUBSTRING('" + epDetSICOP + "',57,3) AND " + " CFUN_152 = SUBSTRING('" + epDetSICOP + "',15,1) AND " + " CASE CPPI_166 WHEN '0' THEN '00000000000' ELSE CPPI_166 END = SUBSTRING('" + epDetSICOP + "',45,11) AND " + " (" + " SELECT CANI_150+'.'+ID_RAMO_CR+'.'+ID_UNIDAD_CR+'.'+CGFU_151+'.'+CFUN_152+'.'+" + " CASE LEN(CSFU_153) WHEN 1 THEN '0'+CSFU_153 END+'.'+" + " CASE LEN(CPRG_154) WHEN 1 THEN '0'+CPRG_154 END +'.'+" + " CASE LEN(CAIN_155) WHEN 1 THEN '00'+CAIN_155 WHEN 2 THEN '0'+CAIN_155 END+'.'+" + " CPPT_156+'.'+COBG_183+'.'+CTGA_160+'.'+CFIN_161+'.'+" + " CASE LEN(CGEO_164) WHEN 1 THEN '0'+CGEO_164 WHEN 2 THEN CGEO_164 END+'.'+" + " CASE CPPI_166 WHEN '0' THEN '00000000000' ELSE CPPI_166 END +'.'+SUBSTRING(CCAU_162,8,3)+'.'+" + " CASE LEN(CCOP_163) WHEN 1 THEN 'B0'+CCOP_163 WHEN 2 THEN 'B'+CCOP_163 WHEN 3 THEN CCOP_163 END AS EP" + " FROM CLC_SICOP WITH(NOLOCK) " + " WHERE NCLC_43 = '" + idCLC + "' AND " + " CPPT_156 = SUBSTRING('" + epDetSICOP + "',27,4) AND COBG_183 = SUBSTRING('" + epDetSICOP + "',32,5) AND " + " CASE LEN(CCOP_163) WHEN 1 THEN 'B0'+CCOP_163 WHEN 2 THEN 'B'+CCOP_163 WHEN 3 THEN CCOP_163 END = SUBSTRING('" + epDetSICOP + "',61,3) AND " + " CASE LEN(CGEO_164) WHEN 1 THEN '0'+CGEO_164 WHEN 2 THEN CGEO_164 END = SUBSTRING('" + epDetSICOP + "',42,2) AND " + " CGFU_151 = SUBSTRING('" + epDetSICOP + "',13,1) AND " + " CASE LEN(CSFU_153) WHEN 1 THEN '0'+CSFU_153 END = SUBSTRING('" + epDetSICOP + "',17,2) AND " + " CASE LEN(CPRG_154) WHEN 1 THEN '0'+CPRG_154 END = SUBSTRING('" + epDetSICOP + "',20,2) AND " + " SUBSTRING(CCAU_162,8,3) = SUBSTRING('" + epDetSICOP + "',57,3) AND " + " CFUN_152 = SUBSTRING('" + epDetSICOP + "',15,1) AND " + " CASE CPPI_166 WHEN '0' THEN '00000000000' ELSE CPPI_166 END = SUBSTRING('" + epDetSICOP + "',45,11) " + " GROUP BY CANI_150,ID_RAMO_CR,ID_UNIDAD_CR,CGFU_151,CFUN_152,CSFU_153,CPRG_154,CAIN_155,CPPT_156,COBG_183,CTGA_160,CFIN_161,CGEO_164,CPPI_166,CCAU_162,CCOP_163 " + " HAVING(SUM(CONVERT(money,IMPORTE_148))) BETWEEN " + numeMenorForDet + " AND " + numeMayorForDet + ") = '" + epDetSICOP + "'" + " GROUP BY CANI_150,ID_RAMO_CR,ID_UNIDAD_CR,CGFU_151,CFUN_152,CSFU_153,CPRG_154,CAIN_155,CPPT_156,COBG_183,CTGA_160,CFIN_161,CGEO_164,CPPI_166,CCAU_162,CCOP_163 " + " HAVING(SUM(CONVERT(money,IMPORTE_148))) BETWEEN " + numeMenorForDet + " AND " + numeMayorForDet);
                            pstmntDetSICOP = conn.prepareStatement(sqll);
                            rs3 = pstmntDetSICOP.executeQuery();
                            if (rs3.next()) {
                                // Si no Existe lo agrega para hacer el Ajuste
                                if (imporDetSICOP != rs3.getDouble("IMPORTE_148")) {
                                    java.util.Date utilDate = new java.util.Date();
                                    long lnMilisegundos = utilDate.getTime();
                                    java.sql.Date sqlDate = new java.sql.Date(lnMilisegundos);
                                    String[] mes = String.valueOf(sqlDate).split("-");
                                    nMes = Integer.parseInt(mes[1], 10);
                                    valorDet = "errorCentavos";
                                    json.put("diferenciaCentavo", "diferenciaCentavo");
                                    pstmntSelect = conn.prepareStatement("SELECT * FROM tEjercidoDetalle WITH(NOLOCK) WHERE EP = ? AND nFolioPago = ? ");
                                    pstmntSelect.setString(1, epDetSICOP);
                                    pstmntSelect.setInt(2, Integer.parseInt(nF[i], 10));
                                    rs4 = pstmntSelect.executeQuery();
                                }
                            } else {
                                renglonn = renglonn + 1;
                                valorDet = "errorDetalles";
                                diferencia += " EP: " + epDetSICOP + "   IMPORTE: " + imporDetSICOP + " \n";
                                json.put("errorDetalles", diferencia);
                            }
                        }
                    }
                }
                if (valorDet == "iguales") {
                    json.put("validacionCorrecta", "validacionCorrecta");
                }
            }
            conn.commit();
        } catch (Exception e) {
            log.error("Error occurred", "Error: " + e);
            try {
                conn.rollback();
            } catch (Exception ee) {
                log.warn("Error: cerrando rollback ", ee);
            }
        } finally {
            try {
                CloseObject.closeObject(rs);
                CloseObject.closeObject(rs1);
                CloseObject.closeObject(rs2);
                CloseObject.closeObject(rs3);
                CloseObject.closeObject(rs4);
                CloseObject.closeObject(rs5);
                CloseObject.closeObject(rs7);
                CloseObject.closeObject(rs8);
                CloseObject.closeObject(pstmntHeader);
                CloseObject.closeObject(pstmntDetSICOP);
                CloseObject.closeObject(pstmntDetCXP);
                CloseObject.closeObject(pstmntInsert);
                CloseObject.closeObject(pstmntInsertP);
                CloseObject.closeObject(pstmntInsertE);
                CloseObject.closeObject(pstmntInsertPE);
                CloseObject.closeObject(pstmntEjeD);
                CloseObject.closeObject(pstmCent);
                CloseObject.closeObject(pstmntEncSICOP);
                CloseObject.closeObject(pstmntEje);
                CloseObject.closeObject(conn);
            } catch (Exception e) {
                log.warn("Error: cerrando statement");
            }
        }
        return json;
    }

    public String aplicacionPagosFueraNomina(String strFolio, String strCXP, String strTipoPago, String fPago, String fEjer, String strUsuario, String strCtaBancaria, String strfolioSICOP, String strsolPago, String strnumProceso, String strfolioSIAFF) throws Exception {
        Connection conn = null;
        PreparedStatement pstmtnSelect = null, pstmntInsertEj = null, pstmnSelectDet = null, pstmntInsertDet = null, pstmntInsertPag = null, pstmntInsertDetPag = null, upd_fAplicacion = null, pstmUpEjercido = null, pstmSeqEjercido = null, pstmUpPagado = null, pstmRadicado = null, pstmBuscaEjercido = null, pstmBuscaPagado = null;
        ;
        ResultSet rs = null, rs2 = null, rsRadicado = null, rsbe = null, rsbp = null;
        ;
        String valor = "";
        String queryUpdateFechaAplicacion = "";
        String strFolioEjercido = null;
        String strFolioPagado = null;
        String strLetratipoPago = "";
        String FechaPagado = fPago;
        String FechaEjercido = fEjer;
        String strTabla = "";
        String strTablaDet = "";
        String strCampos = "";
        String strCamposDet = "";
        String nomCamposDetValidar = "";
        String nomCXP = "";
        String strNomDetFolio = "";
        String usuario = strUsuario;
        String strRadicado = "";
        int ejercidoInsertado = 0;
        int pagadoInsertado = 0;
        String ejercidoAplicado = "";
        String pagadoAplicado = "";
        int folioPoliza = 0;
        int nFolioPago = 0;
        try {
            conn = getConnection();
            int ejercicioFiscal = EjercicioFiscalManager.getEjercicioFiscal(conn).getEjercicio();
            int anioAplicacion = Util.getYearFromDate(FechaPagado);
            String fechaAplicacion = (ejercicioFiscal == anioAplicacion ? fPago : "31/12/" + ejercicioFiscal);
            String cTipoPoliza = "EG";
            boolean appCont = true, appContPag = true;
            AccountingEngine accEng = new AccountingEngine();
            accEng.setValidaInsuficienciaDeSaldo(true);
            // ARLA 20072021 Busca si existe el ejercido y esta aplicado
            pstmBuscaEjercido = conn.prepareStatement("SELECT COUNT(*) AS ejercidoInsertado, cDocumentoHaplicado, nFolioEjercido FROM tEjercidoEncabezado WHERE caNoContrarrecibo = ? GROUP BY cDocumentoHaplicado, nFolioEjercido");
            pstmBuscaEjercido.setString(1, strCXP);
            rsbe = pstmBuscaEjercido.executeQuery();
            if (rsbe.next()) {
                ejercidoInsertado = rsbe.getInt("ejercidoInsertado");
                ejercidoAplicado = rsbe.getString("cDocumentoHaplicado");
                strFolioEjercido = rsbe.getString("nFolioEjercido");
            }
            // ARLA 20072021 Busca si existe el pagado y esta aplicado
            pstmBuscaPagado = conn.prepareStatement("SELECT COUNT(*) AS pagadoInsertado, cDocumentoHaplicado, nFolioPagado FROM tPagadoEncabezado WHERE caNoContrarrecibo = ? GROUP BY cDocumentoHaplicado, nFolioPagado");
            pstmBuscaPagado.setString(1, strCXP);
            rsbp = pstmBuscaPagado.executeQuery();
            if (rsbp.next()) {
                pagadoInsertado = rsbp.getInt("pagadoInsertado");
                pagadoAplicado = rsbp.getString("cDocumentoHaplicado");
                strFolioPagado = rsbp.getString("nFolioPagado");
            }
            // APLICACION DEL EJERCIDO / PAGADO
            if (strTipoPago.equals("RELACIONGASTOS")) {
                strTabla = "tRELACIONGASTOSEncabezado";
                strTablaDet = "tRELACIONGASTOSDetalle";
                strCampos = "nFolioRELACIONGASTOS AS nFolio,mImporteNeto as impNeto,caNoContrarrecibo,nFolioPoliza,cTipoPoliza,U_LOGIN,isnull(nFolioPolizaCancelacion,'0') AS nFolioPolizaCancelacion,isnull(fCancelacion,NULL) AS fCancelacion,cDescripcionPoliza,cUnidadResponsableContable,fAplicacion,cRamo,aEjercicioFiscal,(SELECT TOP 1 sCUENTA_BANCARIA FROM dbo.tLayoutsCreadosRelacionGastosHeader WHERE sAuxiliarComodin=(SELECT dbo.fn_IntegracionRG('" + strCXP + "')))CTAB";
                strCamposDet = "nDocRenglon,cMes,cEjercicio, EP,cIdCuentaContable,mComprometido,isnull(nPoliza,0) AS nPoliza,ID_TIPO_MOVIMIENTO,ID_TIPO_CONCEPTO,cEvento,cCentroContable,RFC,mImporteNeto,ALM,mImporteBruto,mImporteMasIva,mImporteIva,nCapitulo,mSancion,mDevolucion,mImporteAmortiza,mRetencion,mPenalizacion,m2Millar,m23IVA,mISRHonorarios,mObra5,mImporteFlete4,mISRArrenda,mRetImpuestoCedular,mImporte,mImporteIvaArrenda,mImporteIvaHonorarios,mImporteFlete23,mImporteIvaProv,mImporteObra,isnull(mCNIC,0.00) AS mCNIC,isnull(mIMDT,0.00) AS mIMDT,isnull(mTesofe,0.00) AS mTesofe,altaAlmacen,cCentroContable AS cIdEntidadContable, cIdRelacion,mImporteISRLaudos,mISROtros, dbo.fnMontoPasivoDiferido( '" + strTipoPago + "',nFolioRELACIONGASTOS, nDocRenglon) AS montoPasivoDiferdio, mImporteIva6, cPasivo, cUnidadResponsable,mimporteISRResico ";
                nomCamposDetValidar = "cEvento, EP, SUM(CONVERT(money,mImporteNeto)) AS mImporteNeto,cCentroContable,RFC,cEjercicio, cIdRelacion,nCapitulo";
                nomCXP = "caNoContrarrecibo";
                strNomDetFolio = "nFolioRELACIONGASTOS";
                strLetratipoPago = "RELACIONGASTOS";
            }
            // Buscar Informacion Cabecera
            pstmtnSelect = conn.prepareStatement("SELECT " + strCampos + " FROM " + strTabla + " WITH(NOLOCK) WHERE " + nomCXP + "= ? ");
            pstmtnSelect.setString(1, strCXP);
            rs = pstmtnSelect.executeQuery();
            if (rs.next()) {
                if (ejercidoInsertado == 0) {
                    // OBTENER SEQUENCE DE EJERCIDO
                    pstmUpEjercido = conn.prepareStatement("UPDATE CF_SEQUENCE  WITH (ROWLOCK) SET seq_value = seq_value + 1  WHERE seq_name = 'EJERCIDO' ");
                    pstmUpEjercido.executeUpdate();
                    pstmSeqEjercido = conn.prepareStatement("SELECT seq_value FROM CF_SEQUENCE WITH(NOLOCK) WHERE seq_name = 'EJERCIDO' ");
                    rs2 = pstmSeqEjercido.executeQuery();
                    if (rs2.next()) {
                        strFolioEjercido = rs2.getString("seq_value");
                    }
                }
                if (pagadoInsertado == 0) {
                    // OBTENER SEQUENCE DE PAGADO
                    pstmUpPagado = conn.prepareStatement("UPDATE CF_SEQUENCE  WITH (ROWLOCK) SET seq_value = seq_value + 1 WHERE seq_name = 'PAGADO' ");
                    pstmUpPagado.executeUpdate();
                    pstmSeqEjercido = conn.prepareStatement("SELECT seq_value FROM CF_SEQUENCE WITH(NOLOCK) WHERE seq_name = 'PAGADO' ");
                    rs2 = pstmSeqEjercido.executeQuery();
                    if (rs2.next()) {
                        strFolioPagado = rs2.getString("seq_value");
                    }
                }
                // rs.getString("caNoContrarrecibo");
                String recibo = strCXP;
                String tipoPoliza = rs.getString("cTipoPoliza");
                String fCancelacion = rs.getString("fCancelacion");
                String desPoliza = rs.getString("cDescripcionPoliza");
                String uniResp = rs.getString("cUnidadResponsableContable");
                String cRamo = rs.getString("cRamo");
                String strCTAB = strCtaBancaria;
                nFolioPago = Integer.parseInt(rs.getString("nFolio"), 10);
                int nFolPolCancelacion = Integer.parseInt(rs.getString("nFolioPolizaCancelacion"), 10);
                int intaEjercicioFiscal = Integer.parseInt(rs.getString("aEjercicioFiscal"), 10);
                int intr = 0;
                int intrPag = 0;
                int intDet = 0;
                int intDetPag = 0;
                if (fCancelacion != null) {
                    fCancelacion = "'" + fCancelacion + "'";
                } else {
                    fCancelacion = null;
                }
                if (ejercidoInsertado == 0) {
                    // Guarda En Cabecera Ejercido
                    String encE = "INSERT INTO tEjercidoEncabezado (cTipoPago,nFolioPAGO,caNoContrarrecibo,nFolioPoliza,cTipoPoliza," + "U_LOGIN,nFolioPolizaCancelacion,fCancelacion,cDescripcionPoliza,cUnidadResponsableContable," + "nFolioSICOP,fAplicacion,cRamo,cIdUsuarioCaptura,FechaAplicacionSicop," + "FechaPagoSicop,SolicitudPago,NumeroProceso,nFolioSIAFF,nFolioEjercido) " + "        VALUES ('" + strLetratipoPago + "'," + nFolioPago + ",'" + recibo + "'," + folioPoliza + ",'" + tipoPoliza + "','" + usuario + "'," + nFolPolCancelacion + "," + fCancelacion + ",'" + desPoliza + "','" + uniResp + "'," + strfolioSICOP + ",'" + fechaAplicacion + "','" + cRamo + "','" + usuario + "','" + FechaEjercido + "','" + FechaEjercido + "'," + strsolPago + "," + strnumProceso + "," + strfolioSIAFF + "," + strFolioEjercido + " )";
                    pstmntInsertEj = conn.prepareStatement(encE);
                    intr = pstmntInsertEj.executeUpdate();
                } else {
                    intr = 1;
                }
                if (anioAplicacion != intaEjercicioFiscal) {
                    cTipoPoliza = "DI";
                }
                if (pagadoInsertado == 0) {
                    // Guarda En Cabecera Pagado
                    String encP = "INSERT INTO tPagadoEncabezado (cTipoPago,nFolioPAGO,caNoContrarrecibo,nFolioPoliza,cTipoPoliza," + "U_LOGIN,nFolioPolizaCancelacion,fCancelacion,cDescripcionPoliza,cUnidadResponsableContable," + "nFolioSICOP,fAplicacion,cRamo,cIdUsuarioCaptura,FechaAplicacionSicop," + "FechaPagoSicop,SolicitudPago,NumeroProceso,nFolioSIAFF,FechaPagado,nFolioPagado) " + "VALUES ('" + strLetratipoPago + "'," + nFolioPago + ",'" + recibo + "'," + folioPoliza + ",'" + cTipoPoliza + "','" + usuario + "'," + nFolPolCancelacion + "," + fCancelacion + ",'" + desPoliza + "','" + uniResp + "'," + strfolioSICOP + ",'" + fechaAplicacion + "','" + cRamo + "','" + usuario + "','" + FechaPagado + "','" + FechaPagado + "'," + strsolPago + "," + strnumProceso + "," + strfolioSIAFF + ",'" + FechaPagado + "'," + strFolioPagado + " )";
                    pstmntInsertPag = conn.prepareStatement(encP);
                    intrPag = pstmntInsertPag.executeUpdate();
                } else {
                    intrPag = 1;
                }
                if (intr > 0 && intrPag > 0) {
                    // Buscar detalles con el nFolio de Encabezado
                    // String sqlDet = "SELECT " + strCamposDet + " FROM " +
                    // strTablaDet + " WITH(NOLOCK) WHERE cEvento !=
                    // 'ANTICIPO' AND cEvento != 'ANTICIPO_DIV' AND " +
                    // strNomDetFolio + " = ? ";
                    pstmnSelectDet = conn.prepareStatement("SELECT " + strCamposDet + " FROM " + strTablaDet + " WITH(NOLOCK) WHERE cEvento != 'ANTICIPO' AND cEvento != 'ANTICIPO_DIV' AND " + strNomDetFolio + " = ? " + "order by ndocRenglon");
                    pstmnSelectDet.setInt(1, nFolioPago);
                    rs2 = pstmnSelectDet.executeQuery();
                    int nDocRenglon = 0;
                    int renglonPasivoDiFerido = 0;
                    while (rs2.next()) {
                        if (strTablaDet.equals("tPAGOFEDERALIZADODetalle")) {
                            nDocRenglon = nDocRenglon + 1 + renglonPasivoDiFerido;
                        } else {
                            nDocRenglon = Integer.parseInt(rs2.getString("nDocRenglon"), 10) + renglonPasivoDiFerido;
                        }
                        int cMes = Integer.parseInt(rs2.getString("cMes"), 10);
                        String strcEjercicio = rs2.getString("cEjercicio");
                        String EP = rs2.getString("EP");
                        // String esIngreosPropios = ( EP.substring( 39, 40
                        // ).equals( "4" ) ? "S" : "N" );
                        String cOBGT = EP.substring(31, 36);
                        String cIdCuentaContable = (strTablaDet.equals("tOperAjenasDetalle") || strTablaDet.equals("tPagoPenasConvDetalle")) ? " " : rs2.getString("cIdCuentaContable");
                        String cIdEntidadContable = rs2.getString("cIdEntidadContable");
                        String cIdRelacion = (strTablaDet.equals("tOperAjenasDetalle") || strTablaDet.equals("tPagoPenasConvDetalle")) ? " " : rs2.getString("cIdRelacion");
                        String idTipoMovimiento = (strTablaDet.equals("tOperAjenasDetalle") || strTablaDet.equals("tPagoPenasConvDetalle")) ? " " : rs2.getString("ID_TIPO_MOVIMIENTO");
                        String idTipoConcepto = (strTablaDet.equals("tOperAjenasDetalle") || strTablaDet.equals("tPagoPenasConvDetalle")) ? " " : rs2.getString("ID_TIPO_CONCEPTO");
                        double mComprometido = (strTablaDet.equals("tOperAjenasDetalle") || strTablaDet.equals("tPagoPenasConvDetalle")) ? 0.00 : rs2.getDouble("mComprometido");
                        double mImporteBruto = (strTablaDet.equals("tOperAjenasDetalle") || strTablaDet.equals("tPagoPenasConvDetalle")) ? 0.00 : rs2.getDouble("mImporteBruto");
                        double mImporteIva = (strTablaDet.equals("tOperAjenasDetalle") || strTablaDet.equals("tPagoPenasConvDetalle")) ? 0.00 : rs2.getDouble("mImporteIva");
                        double mImporteMasIva = (strTablaDet.equals("tOperAjenasDetalle")) ? 0.00 : rs2.getDouble("mImporteMasIva");
                        double mSancion = (strTablaDet.equals("tOperAjenasDetalle") || strTablaDet.equals("tPagoPenasConvDetalle")) ? 0.00 : rs2.getDouble("mSancion");
                        double mDevolucion = (strTablaDet.equals("tOperAjenasDetalle") || strTablaDet.equals("tPagoPenasConvDetalle")) ? 0.00 : rs2.getDouble("mDevolucion");
                        double mImporteAmortiza = (strTablaDet.equals("tOperAjenasDetalle") || strTablaDet.equals("tPagoPenasConvDetalle")) ? 0.00 : rs2.getDouble("mImporteAmortiza");
                        double mRetencion = (strTablaDet.equals("tOperAjenasDetalle") || strTablaDet.equals("tPagoPenasConvDetalle")) ? 0.00 : rs2.getDouble("mRetencion");
                        double mPenalizacion = strTablaDet.equals("tOperAjenasDetalle") ? 0.00 : rs2.getDouble("mPenalizacion");
                        double m2Millar = (strTablaDet.equals("tOperAjenasDetalle") || strTablaDet.equals("tPagoPenasConvDetalle")) ? 0.00 : rs2.getDouble("m2Millar");
                        double m23IVA = (strTablaDet.equals("tOperAjenasDetalle") || strTablaDet.equals("tPagoPenasConvDetalle")) ? 0.00 : rs2.getDouble("m23IVA");
                        double mImporteIvaProv = (strTablaDet.equals("tOperAjenasDetalle") || strTablaDet.equals("tPagoPenasConvDetalle")) ? 0.00 : rs2.getDouble("mImporteIvaProv");
                        double mImporteObra = (strTablaDet.equals("tOperAjenasDetalle") || strTablaDet.equals("tPagoPenasConvDetalle")) ? 0.00 : rs2.getDouble("mImporteObra");
                        int nPoliza = (strTablaDet.equals("tOperAjenasDetalle") || strTablaDet.equals("tPagoPenasConvDetalle")) ? 0 : rs2.getInt("nPoliza");
                        String[] cEventoCa = rs2.getString("cEvento").split("_");
                        String cEvento = "P";
                        String cPasivo = "";
                        String ur = rs2.getString("cUnidadResponsable");
                        if ("RELACIONGASTOS".equals(strTipoPago))
                            cPasivo = rs2.getString("cPasivo");
                        for (int i = 1; i < cEventoCa.length; i++) {
                            cEvento += "_";
                            cEvento += cEventoCa[i];
                        }
                        // cEvento = "E_"+cEventoCa[1];
                        String cCentroContable = rs2.getString("cCentroContable");
                        String Rfc = rs2.getString("RFC");
                        String alm = (strTablaDet.equals("tOperAjenasDetalle") || strTablaDet.equals("tPagoPenasConvDetalle")) ? " " : rs2.getString("ALM");
                        String nCapitulo = (strTablaDet.equals("tOperAjenasDetalle") || strTablaDet.equals("tPagoPenasConvDetalle")) ? " " : rs2.getString("nCapitulo");
                        String altaAlmacen = "0";
                        String cEventoPagado = (strTablaDet.equals("tOperAjenasDetalle")) ? "P_AJENA_IP" : cEvento;
                        if (strTipoPago.equals("AJENAS")) {
                            pstmRadicado = conn.prepareStatement("SELECT cRadicado FROM tOperAjenasEncabezado WITH(NOLOCK) WHERE caNoContrarrecibo = ?");
                            pstmRadicado.setString(1, strCXP);
                            rsRadicado = pstmRadicado.executeQuery();
                            if (rsRadicado.next())
                                strRadicado = rsRadicado.getString("cRadicado");
                            if (strRadicado.equals("S"))
                                cEventoPagado = "P_AJENA_RAD";
                            if (anioAplicacion != intaEjercicioFiscal) {
                                cEventoPagado = "P_FA_AJENA_IP";
                            }
                        }
                        if (!strTablaDet.equals("tNOMINADetalle") && !strTablaDet.equals("tOperAjenasDetalle") && !strTablaDet.equals("tPagoPenasConvDetalle")) {
                            altaAlmacen = rs2.getString("altaAlmacen");
                        }
                        if (ejercidoInsertado == 0) {
                            // Guarda Detalle Ejercido
                            pstmntInsertDet = conn.prepareStatement("INSERT INTO tEjercidoDetalle (cTipoPago,nFolioPAGO,nDocRenglon,cMes,cEjercicio,cIdEntidadContable,cIdRelacion,EP,cIdCuentaContable,mComprometido,nPoliza,ID_TIPO_MOVIMIENTO,ID_TIPO_CONCEPTO,cEvento,nMes,cCentroContable,RFC, mImporteNeto,ALM,mImporteBruto,mImporteMasIva,mImporteIva,nCapitulo,mSancion,mDevolucion,mImporteAmortiza,mRetencion,mPenalizacion,m2Millar,m23IVA,mISRHonorarios,mObra5,mImporteFlete4,mISRArrenda,mRetImpuestoCedular,mImporteIvaArrenda,mImporteIvaHonorarios ,mImporteFlete23,mImporteIvaProv ,mImporteObra,mCNIC,mTesofe,altaAlmacen,aEjercicioFiscal,mIMDT,mImporte,nFolioEjercido,mImporteISRLaudos,mISROtros, mImporteIva6, mimporteISRResico, cUnidadResponsable)" + " VALUES('" + strLetratipoPago + "'," + nFolioPago + "," + nDocRenglon + "," + cMes + ",'" + strcEjercicio + "','" + cIdEntidadContable + "','" + cIdRelacion + "','" + EP + "','" + cIdCuentaContable + "'," + mComprometido + "," + nPoliza + ",'" + idTipoMovimiento + "','" + idTipoConcepto + "','EJERCIDO','" + cMes + "','" + cCentroContable + "','" + Rfc + "'," + rs2.getString("mImporteNeto") + ",'" + alm + "'," + mImporteBruto + "," + mImporteMasIva + "," + mImporteIva + ",'" + nCapitulo + "'," + mSancion + "," + mDevolucion + "," + mImporteAmortiza + " ," + mRetencion + "," + mPenalizacion + "," + m2Millar + "," + m23IVA + "," + rs2.getString("mISRHonorarios") + "," + rs2.getString("mObra5") + "       ," + rs2.getString("mImporteFlete4") + "," + rs2.getString("mISRArrenda") + "," + rs2.getString("mRetImpuestoCedular") + "," + rs2.getString("mImporteIvaArrenda") + "," + rs2.getString("mImporteIvaHonorarios") + "	," + rs2.getString("mImporteFlete23") + "," + mImporteIvaProv + "," + mImporteObra + "," + rs2.getString("mCNIC") + "," + rs2.getString("mTesofe") + ",'" + altaAlmacen + "','" + intaEjercicioFiscal + "'," + rs2.getString("mIMDT") + "," + rs2.getString("mImporteNeto") + "," + strFolioEjercido + "," + rs2.getString("mImporteISRLaudos") + "," + rs2.getString("mISROtros") + "," + rs2.getString("mImporteIva6") + "," + rs2.getString("mimporteISRResico") + ",'" + ur + "' )");
                            intDet = pstmntInsertDet.executeUpdate();
                            log.debug("Object: " + String.valueOf("Se insertaron en tEjercidoDetalle: " + intDet));
                        }
                        if (pagadoInsertado == 0) {
                            // Guarda Detalle Pagado
                            BigDecimal montoPasivoDiferdio = rs2.getBigDecimal("montoPasivoDiferdio");
                            String sqlinsetP = "INSERT INTO tPagadoDetalle (cTipoPago,nFolioPAGO,nDocRenglon,cMes,cEjercicio,cIdEntidadContable,cIdRelacion,EP,cIdCuentaContable,mComprometido,nPoliza,ID_TIPO_MOVIMIENTO,ID_TIPO_CONCEPTO,cEvento,nMes,cCentroContable,RFC, mImporteNeto,ALM,mImporteBruto,mImporteMasIva,mImporteIva,nCapitulo,mSancion,mDevolucion,mImporteAmortiza,mRetencion,mPenalizacion,m2Millar,m23IVA,mISRHonorarios,mObra5    ,mImporteFlete4,mISRArrenda,mRetImpuestoCedular,mImporteIvaArrenda,mImporteIvaHonorarios ,mImporteFlete23,mImporteIvaProv ,mImporteObra,mCNIC,mTesofe,altaAlmacen,aEjercicioFiscal,mIMDT,mImporte,nFolioPagado,OBGT,ctab,mImporteISRLaudos,mISROtros, mPasivoDiferido, mImporteIva6, cPasivo, mimporteISRResico, cUnidadResponsable)" + "VALUES('" + strLetratipoPago + "'," + nFolioPago + "," + nDocRenglon + "," + cMes + ",'" + strcEjercicio + "','" + cIdEntidadContable + "','" + cIdRelacion + "','" + EP + "','" + cIdCuentaContable + "'," + mComprometido + "," + nPoliza + ",'" + idTipoMovimiento + "','" + idTipoConcepto + "','" + cEventoPagado + "','" + cMes + "','" + cCentroContable + "','" + Rfc + "'," + rs2.getString("mImporteNeto") + ",'" + alm + "'," + mImporteBruto + "," + mImporteMasIva + "," + mImporteIva + ",'" + nCapitulo + "'," + mSancion + "," + mDevolucion + "," + mImporteAmortiza + " ," + mRetencion + "," + mPenalizacion + "," + m2Millar + "," + m23IVA + "," + rs2.getString("mISRHonorarios") + "," + rs2.getString("mObra5") + "       ," + rs2.getString("mImporteFlete4") + "," + rs2.getString("mISRArrenda") + "," + rs2.getString("mRetImpuestoCedular") + "," + rs2.getString("mImporteIvaArrenda") + "," + rs2.getString("mImporteIvaHonorarios") + "	," + rs2.getString("mImporteFlete23") + "," + mImporteIvaProv + "," + mImporteObra + "," + rs2.getString("mCNIC") + "," + rs2.getString("mTesofe") + ",'" + altaAlmacen + "','" + intaEjercicioFiscal + "'," + rs2.getString("mIMDT") + "," + rs2.getString("mImporteNeto") + "," + strFolioPagado + ",'" + cOBGT + "','" + strCTAB + "'," + rs2.getString("mImporteISRLaudos") + "," + rs2.getString("mISROtros") + ",0 ," + rs2.getString("mImporteIva6") + ", '" + cPasivo + "'," + rs2.getString("mimporteISRResico") + ", '" + ur + "' )";
                            if (montoPasivoDiferdio.compareTo(new BigDecimal(0.0f)) > 0) {
                                nDocRenglon++;
                                sqlinsetP = sqlinsetP + ";" + "INSERT INTO tPagadoDetalle (cTipoPago,nFolioPAGO,nDocRenglon,cMes,cEjercicio,cIdEntidadContable,cIdRelacion,EP,cIdCuentaContable,mComprometido,nPoliza,ID_TIPO_MOVIMIENTO,ID_TIPO_CONCEPTO,cEvento,nMes,cCentroContable,RFC, mImporteNeto,ALM,mImporteBruto,mImporteMasIva,mImporteIva,nCapitulo,mSancion,mDevolucion,mImporteAmortiza,mRetencion,mPenalizacion,m2Millar,m23IVA,mISRHonorarios,mObra5    ,mImporteFlete4,mISRArrenda,mRetImpuestoCedular,mImporteIvaArrenda,mImporteIvaHonorarios ,mImporteFlete23,mImporteIvaProv ,mImporteObra,mCNIC,mTesofe,altaAlmacen,aEjercicioFiscal,mIMDT,mImporte,nFolioPagado,OBGT,ctab,mImporteISRLaudos,mISROtros, mPasivoDiferido, mImporteIva6, mimporteISRResico, cUnidadResponsable)" + "VALUES('" + strLetratipoPago + "'," + nFolioPago + "," + nDocRenglon + "," + cMes + ",'" + strcEjercicio + "','" + cIdEntidadContable + "','" + cIdRelacion + "','" + EP + "','" + cIdCuentaContable + "'," + mComprometido + "," + nPoliza + ",'" + idTipoMovimiento + "','" + idTipoConcepto + "','" + cEventoPagado + "','" + cMes + "','" + cCentroContable + "','TESOFE'," + "0,'" + alm + "',0,0,0,'" + nCapitulo + "',0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,'" + altaAlmacen + "','" + intaEjercicioFiscal + "',0,0," + strFolioPagado + ",'" + cOBGT + "','" + strCTAB + "',0,0," + montoPasivoDiferdio + ",0, 0, '" + ur + "' )";
                                renglonPasivoDiFerido++;
                            }
                            pstmntInsertDetPag = conn.prepareStatement(sqlinsetP);
                            intDetPag = pstmntInsertDetPag.executeUpdate();
                            log.debug("Object: " + String.valueOf("Se insertaron en tPagadoDetalle: " + intDetPag));
                        }
                    }
                } else {
                    log.debug("No Guarda Encabezado");
                }
                // APLICACION CONTABLE
                if (("").equals(ejercidoAplicado) || ejercidoAplicado == null)
                    accEng.makeAccountingApplication(conn, "EJERCIDO", strFolioEjercido, "tEjercidoEncabezado", "tEjercidoDetalle", "nFolioEjercido");
                if (("").equals(pagadoAplicado) || pagadoAplicado == null || intDetPag > 0)
                    accEng.makeAccountingApplication(conn, "PAGADO", strFolioPagado, "tPagadoEncabezado", "tPagadoDetalle", "nFolioPagado");
                conn.commit();
                valor = "ingresado";
            }
        } catch (Exception e) {
            valor = e.toString();
            log.warn(e.getMessage(), e);
            log.debug("Object: " + String.valueOf(e.getLocalizedMessage()));
            try {
                conn.rollback();
            } catch (Exception ee) {
                log.warn("Error: cerrando rollback ", ee);
            }
        } finally {
            try {
                if (!valor.equals("ingresado")) {
                    conn.rollback();
                }
                CloseObject.closeObject(rs);
                CloseObject.closeObject(rs2);
                CloseObject.closeObject(rsRadicado);
                CloseObject.closeObject(rsbe);
                CloseObject.closeObject(rsbp);
                CloseObject.closeObject(pstmtnSelect);
                CloseObject.closeObject(pstmntInsertEj);
                CloseObject.closeObject(pstmnSelectDet);
                CloseObject.closeObject(pstmntInsertDet);
                CloseObject.closeObject(pstmntInsertPag);
                CloseObject.closeObject(pstmntInsertDetPag);
                CloseObject.closeObject(pstmRadicado);
                CloseObject.closeObject(upd_fAplicacion);
                CloseObject.closeObject(pstmUpEjercido);
                CloseObject.closeObject(pstmSeqEjercido);
                CloseObject.closeObject(pstmUpPagado);
                CloseObject.closeObject(pstmBuscaEjercido);
                CloseObject.closeObject(pstmBuscaPagado);
                CloseObject.closeObject(conn);
            } catch (Exception e) {
                log.warn("Error: cerrando statement", e);
            }
        }
        return valor;
    }

    public String aplicacionPagosPorReintegro(String strFolio, String strCXP, String strTipoPago, String fPago, String fEjer, String strUsuario, String strfolioSICOP, String strsolPago, String strnumProceso, String strfolioSIAFF, String ctaBancarias) throws Exception {
        Connection conn = null;
        PreparedStatement pstmtnSelect = null, pstmntInsertEj = null, pstmnSelectDet = null, pstmntInsertDet = null, pstmntInsertPag = null, pstmntInsertDetPag = null, upd_fAplicacion = null, pstmUpEjercido = null, pstmSeqEjercido = null, pstmUpPagado = null, pstmRadicado = null, pstmBuscaEjercido = null, pstmBuscaPagado = null, pstmEnviadoSICOP = null, psRFCCuotas = null, psUpdateRFCCuotas = null;
        ;
        ResultSet rs = null, rs2 = null, rsRadicado = null, rsbe = null, rsbp = null, rsRFCCuotas = null;
        ;
        String valor = "";
        String queryUpdateFechaAplicacion = "";
        String strFolioEjercido = null;
        String strFolioPagado = null;
        String strLetratipoPago = "";
        String FechaPagado = fPago;
        String FechaEjercido = fEjer;
        String strTabla = "";
        String strTablaDet = "";
        String strCampos = "";
        String strCamposDet = "";
        String nomCamposDetValidar = "";
        String nomCXP = "";
        String strNomDetFolio = "";
        String usuario = strUsuario;
        String strRadicado = "";
        int ejercidoInsertado = 0;
        int pagadoInsertado = 0;
        String ejercidoAplicado = "";
        String pagadoAplicado = "";
        int folioPoliza = 0;
        int nFolioPago = 0;
        try {
            conn = getConnection();
            int ejercicioFiscal = EjercicioFiscalManager.getEjercicioFiscal(conn).getEjercicio();
            int anioAplicacion = Util.getYearFromDate(FechaPagado);
            String fechaAplicacion = (ejercicioFiscal == anioAplicacion ? fPago : "31/12/" + ejercicioFiscal);
            String cTipoPoliza = "EG";
            boolean appCont = true, appContPag = true;
            AccountingEngine accEng = new AccountingEngine();
            accEng.setValidaInsuficienciaDeSaldo(true);
            // ARLA 20072021 Busca si existe el ejercido y esta aplicado
            pstmBuscaEjercido = conn.prepareStatement("SELECT COUNT(*) AS ejercidoInsertado, cDocumentoHaplicado, nFolioEjercido FROM tEjercidoEncabezado WHERE caNoContrarrecibo = ? GROUP BY cDocumentoHaplicado, nFolioEjercido");
            pstmBuscaEjercido.setString(1, strCXP);
            rsbe = pstmBuscaEjercido.executeQuery();
            if (rsbe.next()) {
                ejercidoInsertado = rsbe.getInt("ejercidoInsertado");
                ejercidoAplicado = rsbe.getString("cDocumentoHaplicado");
                strFolioEjercido = rsbe.getString("nFolioEjercido");
            }
            // ARLA 20072021 Busca si existe el pagado y esta aplicado
            pstmBuscaPagado = conn.prepareStatement("SELECT COUNT(*) AS pagadoInsertado, cDocumentoHaplicado, nFolioPagado FROM tPagadoEncabezado WHERE caNoContrarrecibo = ? GROUP BY cDocumentoHaplicado, nFolioPagado");
            pstmBuscaPagado.setString(1, strCXP);
            rsbp = pstmBuscaPagado.executeQuery();
            if (rsbp.next()) {
                pagadoInsertado = rsbp.getInt("pagadoInsertado");
                pagadoAplicado = rsbp.getString("cDocumentoHaplicado");
                strFolioPagado = rsbp.getString("nFolioPagado");
            }
            // APLICACION DE LA INTEGRACION
            if (strTipoPago.equals("INTEGRACION")) {
                queryUpdateFechaAplicacion = "UPDATE tconsolidacionrelaciongastosEncabezado SET fAplicacion = ? WHERE nFolioConsolidacion = ?";
                upd_fAplicacion = conn.prepareStatement(queryUpdateFechaAplicacion);
                upd_fAplicacion.setString(1, fechaAplicacion);
                upd_fAplicacion.setString(2, strFolio);
                upd_fAplicacion.executeUpdate();
                appContPag = accEng.makeAccountingApplication(conn, "CONSOLIDACIONRG", strFolio, "tconsolidacionrelaciongastosEncabezado", "tconsolidacionrelaciongastosdetalle", "nFolioConsolidacion");
                conn.commit();
                valor = "ingresado";
            } else {
                if (strTipoPago.equals("PAGODIVERSO")) {
                    strTabla = "tPAGODIVERSOEncabezado";
                    strTablaDet = "tPAGODIVERSODetalle";
                    strCampos = "nFolioPAGODIVERSO AS nFolio,mImporteNeto as impNeto,caNoContrarrecibo,nFolioPoliza,cTipoPoliza,U_LOGIN,isnull(nFolioPolizaCancelacion,'0') AS nFolioPolizaCancelacion,isnull(fCancelacion,NULL) AS fCancelacion,cDescripcionPoliza,cUnidadResponsableContable,fAplicacion,cRamo,aEjercicioFiscal,CASE WHEN cEsRelacionGastos='N' THEN CTAB ELSE dbo.fn_cuentaIntegradora(dbo.fn_IntegracionPDIV(caNoContrarrecibo)) END CTAB";
                    strCamposDet = "nDocRenglon,cMes,cEjercicio, EP,cIdCuentaContable,mComprometido,isnull(nPoliza,0) AS nPoliza,ID_TIPO_MOVIMIENTO,ID_TIPO_CONCEPTO,cEvento,cCentroContable,RFC,mImporteNeto,ALM,mImporteBruto,mImporteMasIva,mImporteIva,nCapitulo,mSancion,mDevolucion,mImporteAmortiza,mRetencion,mPenalizacion,m2Millar,m23IVA,mISRHonorarios,mObra5,mImporteFlete4,mISRArrenda,mRetImpuestoCedular,mImporte,mImporteIvaArrenda,mImporteIvaHonorarios,mImporteFlete23,mImporteIvaProv,mImporteObra,isnull(mCNIC,0.00) AS mCNIC,isnull(mIMDT,0.00) AS mIMDT,isnull(mTesofe,0.00) AS mTesofe,altaAlmacen,cCentroContable AS cIdEntidadContable, cIdRelacion,mImporteISRLaudos,mISROtros, dbo.fnMontoPasivoDiferido( '" + strTipoPago + "',nFolioPAGODIVERSO, nDocRenglon ) AS montoPasivoDiferdio, mImporteIva6, mimporteISRResico, cUnidadResponsable";
                    nomCamposDetValidar = "cEvento, EP, SUM(CONVERT(money,mImporteNeto)) AS mImporteNeto,cCentroContable,RFC,cEjercicio, cIdRelacion,nCapitulo";
                    nomCXP = "caNoContrarrecibo";
                    strNomDetFolio = "nFolioPAGODIVERSO";
                    strLetratipoPago = "PAGODIVERSO";
                } else if (strTipoPago.equals("PAGOOBRA")) {
                    strTabla = "tPAGOOBRAEncabezado";
                    strTablaDet = "tPAGOOBRADetalle";
                    strCampos = "nFolioPAGOOBRA AS nFolio,mImporteNeto as impNeto,caNoContrarrecibo,cDocumentoHaplicado,nFolioPoliza,cTipoPoliza,U_LOGIN,isnull(nFolioPolizaCancelacion,'0') AS nFolioPolizaCancelacion,isnull(fCancelacion,NULL) AS fCancelacion,cDescripcionPoliza,cUnidadResponsableContable,fAplicacion,cRamo,aEjercicioFiscal,CTAB";
                    strCamposDet = "nDocRenglon,cMes,cEjercicio, EP,cIdCuentaContable,mComprometido,isnull(nPoliza,0) AS nPoliza,ID_TIPO_MOVIMIENTO,ID_TIPO_CONCEPTO,cEvento,cCentroContable,RFC,mImporteNeto,ALM,mImporteBruto,mImporteMasIva,mImporteIva,nCapitulo,mSancion,mDevolucion,mImporteAmortiza,mRetencion,mPenalizacion,m2Millar,m23IVA,mISRHonorarios,mObra5,mImporteFlete4,mISRArrenda,mRetImpuestoCedular,mImporte,mImporteIvaArrenda,mImporteIvaHonorarios,mImporteFlete23,mImporteIvaProv,mImporteObra,isnull(mCNIC,0.00) AS mCNIC,isnull(mIMDT,0.00) AS mIMDT,isnull(mTesofe,0.00) AS mTesofe,altaAlmacen,cCentroContable AS cIdEntidadContable, cIdRelacion,mImporteISRLaudos,mISROtros, dbo.fnMontoPasivoDiferido( '" + strTipoPago + "',nFolioPAGOOBRA, nDocRenglon) AS montoPasivoDiferdio, mImporteIva6, mimporteISRResico, cUnidadResponsable";
                    nomCamposDetValidar = "cEvento, EP, SUM(CONVERT(money,mImporteNeto)) AS mImporteNeto,cCentroContable,RFC,cEjercicio, cIdRelacion,nCapitulo";
                    nomCXP = "caNoContrarrecibo";
                    strNomDetFolio = "nFolioPAGOOBRA";
                    strLetratipoPago = "PAGOOBRA";
                } else if (strTipoPago.equals("FEDERALIZADO")) {
                    strTabla = "tPAGOFEDERALIZADOEncabezado";
                    strTablaDet = "tPAGOFEDERALIZADODetalle";
                    strCampos = "nFolioPAGOFEDERALIZADO AS nFolio,mImporteNeto as impNeto,caNoContrarrecibo,cDocumentoHaplicado,nFolioPoliza,cTipoPoliza,U_LOGIN,isnull(nFolioPolizaCancelacion,'0') AS nFolioPolizaCancelacion,isnull(fCancelacion,NULL) AS fCancelacion,cDescripcionPoliza,cUnidadResponsableContable,fAplicacion,cRamo,aEjercicioFiscal,CTAB";
                    strCamposDet = "nDocRenglon,cMes,cEjercicio, EP,cIdCuentaContable,mComprometido,isnull(nPoliza,0) AS nPoliza,ID_TIPO_MOVIMIENTO,ID_TIPO_CONCEPTO,cEvento,cCentroContable,RFC,mImporteNeto,ALM,mImporteBruto,mImporteMasIva,mImporteIva,nCapitulo,mSancion,mDevolucion,mImporteAmortiza,mRetencion,mPenalizacion,m2Millar,m23IVA,mISRHonorarios,mObra5,mImporteFlete4,mISRArrenda,mRetImpuestoCedular,mImporte,mImporteIvaArrenda,mImporteIvaHonorarios,mImporteFlete23,mImporteIvaProv,mImporteObra,isnull(mCNIC,0.00) AS mCNIC,isnull(mIMDT,0.00) AS mIMDT,isnull(mTesofe,0.00) AS mTesofe,altaAlmacen,cCentroContable AS cIdEntidadContable, cIdRelacion,mImporteISRLaudos,mISROtros, dbo.fnMontoPasivoDiferido( 'FEDERALIZADO',nFolioPAGOFEDERALIZADO, nDocRenglon) AS montoPasivoDiferdio, mImporteIva6, mimporteISRResico, cUnidadResponsable";
                    nomCamposDetValidar = "cEvento, EP, SUM(CONVERT(money,mImporteNeto)) AS mImporteNeto,cCentroContable,RFC,cEjercicio, cIdRelacion,nCapitulo";
                    nomCXP = "caNoContrarrecibo";
                    strNomDetFolio = "nFolioPAGOFEDERALIZADO";
                    strLetratipoPago = "FEDERALIZADO";
                } else if (strTipoPago.equals("RELACIONGASTOS")) {
                    strTabla = "tRELACIONGASTOSEncabezado";
                    strTablaDet = "tRELACIONGASTOSDetalle";
                    strCampos = "nFolioRELACIONGASTOS AS nFolio,mImporteNeto as impNeto,caNoContrarrecibo,nFolioPoliza,cTipoPoliza,U_LOGIN,isnull(nFolioPolizaCancelacion,'0') AS nFolioPolizaCancelacion,isnull(fCancelacion,NULL) AS fCancelacion,cDescripcionPoliza,cUnidadResponsableContable,fAplicacion,cRamo,aEjercicioFiscal,(SELECT TOP 1 sCUENTA_BANCARIA FROM dbo.tLayoutsCreadosRelacionGastosHeader WHERE sAuxiliarComodin=(SELECT dbo.fn_IntegracionRG('" + strCXP + "')))CTAB";
                    strCamposDet = "nDocRenglon,cMes,cEjercicio, EP,cIdCuentaContable,mComprometido,isnull(nPoliza,0) AS nPoliza,ID_TIPO_MOVIMIENTO,ID_TIPO_CONCEPTO,cEvento,cCentroContable,RFC,mImporteNeto,ALM,mImporteBruto,mImporteMasIva,mImporteIva,nCapitulo,mSancion,mDevolucion,mImporteAmortiza,mRetencion,mPenalizacion,m2Millar,m23IVA,mISRHonorarios,mObra5,mImporteFlete4,mISRArrenda,mRetImpuestoCedular,mImporte,mImporteIvaArrenda,mImporteIvaHonorarios,mImporteFlete23,mImporteIvaProv,mImporteObra,isnull(mCNIC,0.00) AS mCNIC,isnull(mIMDT,0.00) AS mIMDT,isnull(mTesofe,0.00) AS mTesofe,altaAlmacen,cCentroContable AS cIdEntidadContable, cIdRelacion,mImporteISRLaudos,mISROtros, dbo.fnMontoPasivoDiferido( '" + strTipoPago + "',nFolioRELACIONGASTOS, nDocRenglon) AS montoPasivoDiferdio, mImporteIva6, cPasivo, cUnidadResponsable,mimporteISRResico ";
                    nomCamposDetValidar = "cEvento, EP, SUM(CONVERT(money,mImporteNeto)) AS mImporteNeto,cCentroContable,RFC,cEjercicio, cIdRelacion,nCapitulo";
                    nomCXP = "caNoContrarrecibo";
                    strNomDetFolio = "nFolioRELACIONGASTOS";
                    strLetratipoPago = "RELACIONGASTOS";
                } else if (strTipoPago.equals("PAGODIRECTO")) {
                    strTabla = "tPagoDirectoEncabezado";
                    strTablaDet = "tPagoDirectoDetalle";
                    strCampos = "nFolioPagoDirecto AS nFolio, mImporteNeto as impNeto,caNoContrarrecibo,nFolioPoliza,cTipoPoliza,U_LOGIN,isnull(nFolioPolizaCancelacion,'0') AS nFolioPolizaCancelacion,isnull(fCancelacion,NULL) AS fCancelacion,cDescripcionPoliza,cUnidadResponsableContable,fAplicacion,cRamo,aEjercicioFiscal,CTAB";
                    strCamposDet = "nDocRenglon,cMes,cEjercicio, EP,cIdCuentaContable,mComprometido,isnull(nPoliza,0) AS nPoliza,ID_TIPO_MOVIMIENTO,ID_TIPO_CONCEPTO,cEvento,cCentroContable,RFC,mImporteNeto,ALM,mImporteBruto,mImporteMasIva,mImporteIva,nCapitulo,mSancion,mDevolucion,mImporteAmortiza,mRetencion,mPenalizacion,m2Millar,m23IVA,mISRHonorarios,mObra5,mImporteFlete4,mISRArrenda,mRetImpuestoCedular,mImporte,mImporteIvaArrenda,mImporteIvaHonorarios,mImporteFlete23,mImporteIvaProv,mImporteObra,isnull(mCNIC,0.00) AS mCNIC,isnull(mIMDT,0.00) AS mIMDT,isnull(mTesofe,0.00) AS mTesofe,altaAlmacen,cCentroContable AS cIdEntidadContable,cIdDocumento AS cIdRelacion,mImporteISRLaudos,mISROtros, dbo.fnMontoPasivoDiferido( '" + strTipoPago + "',nFolioPagoDirecto, nDocRenglon) AS montoPasivoDiferdio, mImporteIva6, mimporteISRResico, cUnidadResponsable";
                    nomCamposDetValidar = "cEvento, EP, cCentroContable,SUM(CONVERT(money,mImporteNeto)) AS mImporteNeto,RFC,cEjercicio,cIdDocumento AS cIdRelacion,nCapitulo";
                    nomCXP = "caNoContrarrecibo";
                    strNomDetFolio = "nFolioPagoDirecto";
                    strLetratipoPago = "PAGODIRECTO";
                } else if (strTipoPago.equals("AJENAS")) {
                    strTabla = "tOperAjenasEncabezado";
                    strTablaDet = "tOperAjenasDetalle";
                    // INGRESOS PROPIOS
                    strCampos = "nFolioOperAjenas AS nFolio,mImportes as impNeto,caNoContrarrecibo,cDocumentoHaplicado,nFolioPoliza,cTipoPoliza,U_LOGIN,isnull(nFolioPolizaCancelacion,'0') AS nFolioPolizaCancelacion,isnull(fCancelacion,NULL) AS fCancelacion,cDescripcionPoliza,cUnidadResponsableContable,fAplicacion,cRamo,aEjercicioFiscal,(SELECT GP_VALOR FROM CG_GRUPO_PROPIEDADES WITH (NOLOCK) WHERE GP_NOMBRE = 'CUENTA INGRESOS PROPIOS') AS CTAB";
                    strCamposDet = "nDocRenglon,cMes,aEjercicioFiscal AS cEjercicio,Ep AS EP,cEvento,cCentroContable,RFC,mTotal AS mImporteNeto,mISRHonorarios,mObra5,mImporteFlete4,mISRArrenda,mRetImpuestoCedular,mImporteFlete23,isnull(mCNIC,0.00) AS mCNIC,isnull(mIMDT,0.00) AS mIMDT,isnull(mTesofe,0.00) AS mTesofe,cCentroContable AS cIdEntidadContable,mImporteIvaArrenda,mImporteIvaHonorarios,mImporteFlete23,mRetImpuestoCedular,mImporteISRLaudos,mISROtros, dbo.fnMontoPasivoDiferido( '" + strTipoPago + "',nFolioOperAjenas, nDocRenglon) AS montoPasivoDiferdio, mImporteIva6, mimporteISRResico, cUnidadResponsable";
                    nomCamposDetValidar = "cEvento, Ep AS EP, SUM(CONVERT(money,mTotal)) AS mImporteNeto,cCentroContable,RFC,aEjercicioFiscal AS cEjercicio";
                    nomCXP = "caNoContrarrecibo";
                    strNomDetFolio = "nFolioOperAjenas";
                    strLetratipoPago = "AJENAS";
                } else if (strTipoPago.equals("PAGOPENASCONV")) {
                    strTabla = "tPagoPenasConvEncabezado";
                    strTablaDet = "tPagoPenasConvDetalle";
                    // INGRESOS PROPIOS
                    strCampos = "nFolioPagoPenasConv AS nFolio,(SELECT SUM(MPENALIZACION) FROM tPagoPenasConvDetalle WHERE nFolioPagoPenasConv = tPagoPenasConvEncabezado.nFolioPagoPenasConv ) as impNeto,caNoContrarrecibo,cDocumentoHaplicado,nFolioPoliza,cTipoPoliza,U_LOGIN,isnull(nFolioPolizaCancelacion,'0') AS nFolioPolizaCancelacion,isnull(fCancelacion,NULL) AS fCancelacion,cDescripcionPoliza,cUnidadResponsableContable,fAplicacion,cRamo,aEjercicioFiscal,(SELECT GP_VALOR FROM CG_GRUPO_PROPIEDADES WITH (NOLOCK) WHERE GP_NOMBRE = 'CUENTA INGRESOS PROPIOS') AS CTAB";
                    strCamposDet = "nDocRenglon,cMes,cEjercicio AS cEjercicio,Ep AS EP,cEvento,cCentroContable,RFC,mPenalizacion AS mImporteNeto, mPenalizacion AS mImporteMasIva, 0 mISRHonorarios,0 mObra5,0 mImporteFlete4,0 mISRArrenda,0 mRetImpuestoCedular,0 mImporteFlete23,0 AS mCNIC,0 AS mIMDT,0 AS mTesofe,cCentroContable AS cIdEntidadContable, 0 mImporteIvaArrenda,0 mImporteIvaHonorarios,0 mImporteFlete23,0 mRetImpuestoCedular,0 mImporteISRLaudos,0  mISROtros, dbo.fnMontoPasivoDiferido( '\" + strTipoPago + \"',nFolioPagoPenasConv, nDocRenglon) AS montoPasivoDiferdio, 0 mImporteIva6,0  mimporteISRResico, cUnidadResponsable , mPenalizacion";
                    nomCamposDetValidar = "cEvento, Ep AS EP, SUM(CONVERT(money,mPenalizacion)) AS mImporteNeto,cCentroContable,RFC,cEjercicio AS cEjercicio";
                    nomCXP = "caNoContrarrecibo";
                    strNomDetFolio = "nFolioPagoPenasConv";
                    strLetratipoPago = "PAGOPENASCONV";
                }
                // Buscar Informacion Cabecera
                pstmtnSelect = conn.prepareStatement("SELECT " + strCampos + " FROM " + strTabla + " WITH(NOLOCK) WHERE " + nomCXP + "= ? ");
                pstmtnSelect.setString(1, strCXP);
                rs = pstmtnSelect.executeQuery();
                if (rs.next()) {
                    if (ejercidoInsertado == 0) {
                        // OBTENER SEQUENCE DE EJERCIDO
                        pstmUpEjercido = conn.prepareStatement("UPDATE CF_SEQUENCE  WITH (ROWLOCK) SET seq_value = seq_value + 1  WHERE seq_name = 'EJERCIDO' ");
                        pstmUpEjercido.executeUpdate();
                        pstmSeqEjercido = conn.prepareStatement("SELECT seq_value FROM CF_SEQUENCE WITH(NOLOCK) WHERE seq_name = 'EJERCIDO' ");
                        rs2 = pstmSeqEjercido.executeQuery();
                        if (rs2.next()) {
                            strFolioEjercido = rs2.getString("seq_value");
                        }
                    }
                    if (pagadoInsertado == 0) {
                        // OBTENER SEQUENCE DE PAGADO
                        pstmUpPagado = conn.prepareStatement("UPDATE CF_SEQUENCE  WITH (ROWLOCK) SET seq_value = seq_value + 1 WHERE seq_name = 'PAGADO' ");
                        pstmUpPagado.executeUpdate();
                        pstmSeqEjercido = conn.prepareStatement("SELECT seq_value FROM CF_SEQUENCE WITH(NOLOCK) WHERE seq_name = 'PAGADO' ");
                        rs2 = pstmSeqEjercido.executeQuery();
                        if (rs2.next()) {
                            strFolioPagado = rs2.getString("seq_value");
                        }
                    }
                    // rs.getString("caNoContrarrecibo");
                    String recibo = strCXP;
                    String tipoPoliza = rs.getString("cTipoPoliza");
                    String fCancelacion = rs.getString("fCancelacion");
                    String desPoliza = rs.getString("cDescripcionPoliza");
                    String uniResp = rs.getString("cUnidadResponsableContable");
                    String cRamo = rs.getString("cRamo");
                    String strCTAB = rs.getString("CTAB");
                    if (strTipoPago.equals("RELACIONGASTOS")) {
                        strCTAB = ctaBancarias;
                    }
                    nFolioPago = Integer.parseInt(rs.getString("nFolio"), 10);
                    int nFolPolCancelacion = Integer.parseInt(rs.getString("nFolioPolizaCancelacion"), 10);
                    int intaEjercicioFiscal = Integer.parseInt(rs.getString("aEjercicioFiscal"), 10);
                    int intr = 0;
                    int intrPag = 0;
                    int intDet = 0;
                    int intDetPag = 0;
                    if (fCancelacion != null) {
                        fCancelacion = "'" + fCancelacion + "'";
                    } else {
                        fCancelacion = null;
                    }
                    if (ejercidoInsertado == 0) {
                        // Guarda En Cabecera Ejercido
                        String encE = "INSERT INTO tEjercidoEncabezado (cTipoPago,nFolioPAGO,caNoContrarrecibo,nFolioPoliza,cTipoPoliza," + "U_LOGIN,nFolioPolizaCancelacion,fCancelacion,cDescripcionPoliza,cUnidadResponsableContable," + "nFolioSICOP,fAplicacion,cRamo,cIdUsuarioCaptura,FechaAplicacionSicop," + "FechaPagoSicop,SolicitudPago,NumeroProceso,nFolioSIAFF,nFolioEjercido) " + "        VALUES ('" + strLetratipoPago + "'," + nFolioPago + ",'" + recibo + "'," + folioPoliza + ",'" + tipoPoliza + "','" + usuario + "'," + nFolPolCancelacion + "," + fCancelacion + ",'" + desPoliza + "','" + uniResp + "'," + strfolioSICOP + ",'" + fechaAplicacion + "','" + cRamo + "','" + usuario + "','" + FechaEjercido + "','" + FechaEjercido + "'," + strsolPago + "," + strnumProceso + "," + strfolioSIAFF + "," + strFolioEjercido + " )";
                        pstmntInsertEj = conn.prepareStatement(encE);
                        intr = pstmntInsertEj.executeUpdate();
                    } else {
                        intr = 1;
                    }
                    if (anioAplicacion != intaEjercicioFiscal) {
                        cTipoPoliza = "DI";
                    }
                    if (pagadoInsertado == 0) {
                        // Guarda En Cabecera Pagado
                        String encP = "INSERT INTO tPagadoEncabezado (cTipoPago,nFolioPAGO,caNoContrarrecibo,nFolioPoliza,cTipoPoliza," + "U_LOGIN,nFolioPolizaCancelacion,fCancelacion,cDescripcionPoliza,cUnidadResponsableContable," + "nFolioSICOP,fAplicacion,cRamo,cIdUsuarioCaptura,FechaAplicacionSicop," + "FechaPagoSicop,SolicitudPago,NumeroProceso,nFolioSIAFF,FechaPagado,nFolioPagado) " + "VALUES ('" + strLetratipoPago + "'," + nFolioPago + ",'" + recibo + "'," + folioPoliza + ",'" + cTipoPoliza + "','" + usuario + "'," + nFolPolCancelacion + "," + fCancelacion + ",'" + desPoliza + "','" + uniResp + "'," + strfolioSICOP + ",'" + fechaAplicacion + "','" + cRamo + "','" + usuario + "','" + FechaPagado + "','" + FechaPagado + "'," + strsolPago + "," + strnumProceso + "," + strfolioSIAFF + ",'" + FechaPagado + "'," + strFolioPagado + " )";
                        pstmntInsertPag = conn.prepareStatement(encP);
                        intrPag = pstmntInsertPag.executeUpdate();
                    } else {
                        intrPag = 1;
                    }
                    if (intr > 0 && intrPag > 0) {
                        // Buscar detalles con el nFolio de Encabezado
                        // String sqlDet = "SELECT " + strCamposDet + " FROM " +
                        // strTablaDet + " WITH(NOLOCK) WHERE cEvento !=
                        // 'ANTICIPO' AND cEvento != 'ANTICIPO_DIV' AND " +
                        // strNomDetFolio + " = ? ";
                        pstmnSelectDet = conn.prepareStatement("SELECT " + strCamposDet + " FROM " + strTablaDet + " WITH(NOLOCK) WHERE cEvento != 'ANTICIPO' AND cEvento != 'ANTICIPO_DIV' AND " + strNomDetFolio + " = ? " + "order by ndocRenglon");
                        pstmnSelectDet.setInt(1, nFolioPago);
                        rs2 = pstmnSelectDet.executeQuery();
                        int nDocRenglon = 0;
                        int renglonPasivoDiFerido = 0;
                        String cEsPagoCuotas = "";
                        String cRFCCuotas = "";
                        String RFC_PC = "";
                        String PC = "";
                        if (strTablaDet.equals("tRELACIONGASTOSDetalle")) {
                            StringBuilder queryesRFCCuotas = new StringBuilder("SELECT cEsPagoCuotas, cRFCCuotas, PC.cRFC, cSubcuenta AS PC ");
                            queryesRFCCuotas.append("FROM tRELACIONGASTOSDetalle DET WITH (NOLOCK) ");
                            queryesRFCCuotas.append("LEFT JOIN tComprobacionLaudos COMP WITH (NOLOCK) ON DET.nFolioRELACIONGASTOS = COMP.nFolioRELACIONGASTOS ");
                            queryesRFCCuotas.append("LEFT JOIN tpasivosContingentes PC WITH (NOLOCK) ON DET.cPasivo = cSubcuenta ");
                            queryesRFCCuotas.append("WHERE DET.nFolioRELACIONGASTOS = ?");
                            psRFCCuotas = conn.prepareStatement(queryesRFCCuotas.toString());
                            psRFCCuotas.setInt(1, nFolioPago);
                            rsRFCCuotas = psRFCCuotas.executeQuery();
                            if (rsRFCCuotas.next()) {
                                cEsPagoCuotas = rsRFCCuotas.getString("cEsPagoCuotas");
                                cRFCCuotas = rsRFCCuotas.getString("cRFCCuotas");
                                RFC_PC = rsRFCCuotas.getString("cRFC");
                                PC = rsRFCCuotas.getString("PC");
                            }
                        }
                        while (rs2.next()) {
                            if (strTablaDet.equals("tPAGOFEDERALIZADODetalle") || strTablaDet.equals("tRELACIONGASTOSDetalle")) {
                                nDocRenglon = nDocRenglon + 1 + renglonPasivoDiFerido;
                            } else {
                                nDocRenglon = Integer.parseInt(rs2.getString("nDocRenglon"), 10) + renglonPasivoDiFerido;
                            }
                            int cMes = Integer.parseInt(rs2.getString("cMes"), 10);
                            String strcEjercicio = rs2.getString("cEjercicio");
                            String EP = rs2.getString("EP");
                            // String esIngreosPropios = ( EP.substring( 39, 40
                            // ).equals( "4" ) ? "S" : "N" );
                            String cOBGT = EP.substring(31, 36);
                            String cIdCuentaContable = (strTablaDet.equals("tOperAjenasDetalle") || strTablaDet.equals("tPagoPenasConvDetalle")) ? " " : rs2.getString("cIdCuentaContable");
                            String cIdEntidadContable = rs2.getString("cIdEntidadContable");
                            String cIdRelacion = (strTablaDet.equals("tOperAjenasDetalle") || strTablaDet.equals("tPagoPenasConvDetalle")) ? " " : rs2.getString("cIdRelacion");
                            String idTipoMovimiento = (strTablaDet.equals("tOperAjenasDetalle") || strTablaDet.equals("tPagoPenasConvDetalle")) ? " " : rs2.getString("ID_TIPO_MOVIMIENTO");
                            String idTipoConcepto = (strTablaDet.equals("tOperAjenasDetalle") || strTablaDet.equals("tPagoPenasConvDetalle")) ? " " : rs2.getString("ID_TIPO_CONCEPTO");
                            double mComprometido = (strTablaDet.equals("tOperAjenasDetalle") || strTablaDet.equals("tPagoPenasConvDetalle")) ? 0.00 : rs2.getDouble("mComprometido");
                            double mImporteBruto = (strTablaDet.equals("tOperAjenasDetalle") || strTablaDet.equals("tPagoPenasConvDetalle")) ? 0.00 : rs2.getDouble("mImporteBruto");
                            double mImporteIva = (strTablaDet.equals("tOperAjenasDetalle") || strTablaDet.equals("tPagoPenasConvDetalle")) ? 0.00 : rs2.getDouble("mImporteIva");
                            double mImporteMasIva = (strTablaDet.equals("tOperAjenasDetalle")) ? 0.00 : rs2.getDouble("mImporteMasIva");
                            double mSancion = (strTablaDet.equals("tOperAjenasDetalle") || strTablaDet.equals("tPagoPenasConvDetalle")) ? 0.00 : rs2.getDouble("mSancion");
                            double mDevolucion = (strTablaDet.equals("tOperAjenasDetalle") || strTablaDet.equals("tPagoPenasConvDetalle")) ? 0.00 : rs2.getDouble("mDevolucion");
                            double mImporteAmortiza = (strTablaDet.equals("tOperAjenasDetalle") || strTablaDet.equals("tPagoPenasConvDetalle")) ? 0.00 : rs2.getDouble("mImporteAmortiza");
                            double mRetencion = (strTablaDet.equals("tOperAjenasDetalle") || strTablaDet.equals("tPagoPenasConvDetalle")) ? 0.00 : rs2.getDouble("mRetencion");
                            double mPenalizacion = strTablaDet.equals("tOperAjenasDetalle") ? 0.00 : rs2.getDouble("mPenalizacion");
                            double m2Millar = (strTablaDet.equals("tOperAjenasDetalle") || strTablaDet.equals("tPagoPenasConvDetalle")) ? 0.00 : rs2.getDouble("m2Millar");
                            double m23IVA = (strTablaDet.equals("tOperAjenasDetalle") || strTablaDet.equals("tPagoPenasConvDetalle")) ? 0.00 : rs2.getDouble("m23IVA");
                            double mImporteIvaProv = (strTablaDet.equals("tOperAjenasDetalle") || strTablaDet.equals("tPagoPenasConvDetalle")) ? 0.00 : rs2.getDouble("mImporteIvaProv");
                            double mImporteObra = (strTablaDet.equals("tOperAjenasDetalle") || strTablaDet.equals("tPagoPenasConvDetalle")) ? 0.00 : rs2.getDouble("mImporteObra");
                            int nPoliza = (strTablaDet.equals("tOperAjenasDetalle") || strTablaDet.equals("tPagoPenasConvDetalle")) ? 0 : rs2.getInt("nPoliza");
                            String[] cEventoCa = rs2.getString("cEvento").split("_");
                            String cEvento = "P";
                            String cPasivo = "";
                            String ur = rs2.getString("cUnidadResponsable");
                            if ("RELACIONGASTOS".equals(strTipoPago)) {
                                cPasivo = rs2.getString("cPasivo");
                            }
                            for (int i = 1; i < cEventoCa.length; i++) {
                                cEvento += "_";
                                cEvento += cEventoCa[i];
                            }
                            // cEvento = "E_"+cEventoCa[1];
                            String cCentroContable = rs2.getString("cCentroContable");
                            String Rfc = rs2.getString("RFC");
                            String alm = (strTablaDet.equals("tOperAjenasDetalle") || strTablaDet.equals("tPagoPenasConvDetalle")) ? " " : rs2.getString("ALM");
                            String nCapitulo = (strTablaDet.equals("tOperAjenasDetalle") || strTablaDet.equals("tPagoPenasConvDetalle")) ? " " : rs2.getString("nCapitulo");
                            String altaAlmacen = "0";
                            String cEventoPagado = (strTablaDet.equals("tOperAjenasDetalle")) ? "P_AJENA_IP" : cEvento;
                            if (strTipoPago.equals("AJENAS")) {
                                pstmRadicado = conn.prepareStatement("SELECT cRadicado FROM tOperAjenasEncabezado WITH(NOLOCK) WHERE caNoContrarrecibo = ?");
                                pstmRadicado.setString(1, strCXP);
                                rsRadicado = pstmRadicado.executeQuery();
                                if (rsRadicado.next())
                                    strRadicado = rsRadicado.getString("cRadicado");
                                if (strRadicado.equals("S"))
                                    cEventoPagado = "P_AJENA_RAD";
                                if (anioAplicacion != intaEjercicioFiscal) {
                                    cEventoPagado = "P_FA_AJENA_IP";
                                }
                            }
                            if (!strTablaDet.equals("tNOMINADetalle") && !strTablaDet.equals("tOperAjenasDetalle") && !strTablaDet.equals("tPagoPenasConvDetalle")) {
                                altaAlmacen = rs2.getString("altaAlmacen");
                            }
                            if (ejercidoInsertado == 0) {
                                // Guarda Detalle Ejercido
                                pstmntInsertDet = conn.prepareStatement("INSERT INTO tEjercidoDetalle (cTipoPago,nFolioPAGO,nDocRenglon,cMes,cEjercicio,cIdEntidadContable,cIdRelacion,EP,cIdCuentaContable,mComprometido,nPoliza,ID_TIPO_MOVIMIENTO,ID_TIPO_CONCEPTO,cEvento,nMes,cCentroContable,RFC, mImporteNeto,ALM,mImporteBruto,mImporteMasIva,mImporteIva,nCapitulo,mSancion,mDevolucion,mImporteAmortiza,mRetencion,mPenalizacion,m2Millar,m23IVA,mISRHonorarios,mObra5,mImporteFlete4,mISRArrenda,mRetImpuestoCedular,mImporteIvaArrenda,mImporteIvaHonorarios ,mImporteFlete23,mImporteIvaProv ,mImporteObra,mCNIC,mTesofe,altaAlmacen,aEjercicioFiscal,mIMDT,mImporte,nFolioEjercido,mImporteISRLaudos,mISROtros, mImporteIva6, mimporteISRResico, cUnidadResponsable)" + " VALUES('" + strLetratipoPago + "'," + nFolioPago + "," + nDocRenglon + "," + cMes + ",'" + strcEjercicio + "','" + cIdEntidadContable + "','" + cIdRelacion + "','" + EP + "','" + cIdCuentaContable + "'," + mComprometido + "," + nPoliza + ",'" + idTipoMovimiento + "','" + idTipoConcepto + "','EJERCIDO','" + cMes + "','" + cCentroContable + "','" + Rfc + "'," + rs2.getString("mImporteNeto") + ",'" + alm + "'," + mImporteBruto + "," + mImporteMasIva + "," + mImporteIva + ",'" + nCapitulo + "'," + mSancion + "," + mDevolucion + "," + mImporteAmortiza + " ," + mRetencion + "," + mPenalizacion + "," + m2Millar + "," + m23IVA + "," + rs2.getString("mISRHonorarios") + "," + rs2.getString("mObra5") + "       ," + rs2.getString("mImporteFlete4") + "," + rs2.getString("mISRArrenda") + "," + rs2.getString("mRetImpuestoCedular") + "," + rs2.getString("mImporteIvaArrenda") + "," + rs2.getString("mImporteIvaHonorarios") + "	," + rs2.getString("mImporteFlete23") + "," + mImporteIvaProv + "," + mImporteObra + "," + rs2.getString("mCNIC") + "," + rs2.getString("mTesofe") + ",'" + altaAlmacen + "','" + intaEjercicioFiscal + "'," + rs2.getString("mIMDT") + "," + rs2.getString("mImporteNeto") + "," + strFolioEjercido + "," + rs2.getString("mImporteISRLaudos") + "," + rs2.getString("mISROtros") + "," + rs2.getString("mImporteIva6") + "," + rs2.getString("mimporteISRResico") + ",'" + ur + "' )");
                                intDet = pstmntInsertDet.executeUpdate();
                                log.debug("Object: " + String.valueOf("Se insertaron en tEjercidoDetalle: " + intDet));
                            }
                            if (pagadoInsertado == 0) {
                                // Guarda Detalle Pagado
                                BigDecimal montoPasivoDiferdio = rs2.getBigDecimal("montoPasivoDiferdio");
                                String sqlinsetP = "INSERT INTO tPagadoDetalle (cTipoPago,nFolioPAGO,nDocRenglon,cMes,cEjercicio,cIdEntidadContable,cIdRelacion,EP,cIdCuentaContable,mComprometido,nPoliza,ID_TIPO_MOVIMIENTO,ID_TIPO_CONCEPTO,cEvento,nMes,cCentroContable,RFC, mImporteNeto,ALM,mImporteBruto,mImporteMasIva,mImporteIva,nCapitulo,mSancion,mDevolucion,mImporteAmortiza,mRetencion,mPenalizacion,m2Millar,m23IVA,mISRHonorarios,mObra5    ,mImporteFlete4,mISRArrenda,mRetImpuestoCedular,mImporteIvaArrenda,mImporteIvaHonorarios ,mImporteFlete23,mImporteIvaProv ,mImporteObra,mCNIC,mTesofe,altaAlmacen,aEjercicioFiscal,mIMDT,mImporte,nFolioPagado,OBGT,ctab,mImporteISRLaudos,mISROtros, mPasivoDiferido, mImporteIva6, cPasivo, mimporteISRResico, cUnidadResponsable)" + "VALUES('" + strLetratipoPago + "'," + nFolioPago + "," + nDocRenglon + "," + cMes + ",'" + strcEjercicio + "','" + cIdEntidadContable + "','" + cIdRelacion + "','" + EP + "','" + cIdCuentaContable + "'," + mComprometido + "," + nPoliza + ",'" + idTipoMovimiento + "','" + idTipoConcepto + "','" + cEventoPagado + "','" + cMes + "','" + cCentroContable + "','" + Rfc + "'," + rs2.getString("mImporteNeto") + ",'" + alm + "'," + mImporteBruto + "," + mImporteMasIva + "," + mImporteIva + ",'" + nCapitulo + "'," + mSancion + "," + mDevolucion + "," + mImporteAmortiza + " ," + mRetencion + "," + mPenalizacion + "," + m2Millar + "," + m23IVA + "," + rs2.getString("mISRHonorarios") + "," + rs2.getString("mObra5") + "       ," + rs2.getString("mImporteFlete4") + "," + rs2.getString("mISRArrenda") + "," + rs2.getString("mRetImpuestoCedular") + "," + rs2.getString("mImporteIvaArrenda") + "," + rs2.getString("mImporteIvaHonorarios") + "	," + rs2.getString("mImporteFlete23") + "," + mImporteIvaProv + "," + mImporteObra + "," + rs2.getString("mCNIC") + "," + rs2.getString("mTesofe") + ",'" + altaAlmacen + "','" + intaEjercicioFiscal + "'," + rs2.getString("mIMDT") + "," + rs2.getString("mImporteNeto") + "," + strFolioPagado + ",'" + cOBGT + "','" + strCTAB + "'," + rs2.getString("mImporteISRLaudos") + "," + rs2.getString("mISROtros") + ",0 ," + rs2.getString("mImporteIva6") + ", '" + cPasivo + "'," + rs2.getString("mimporteISRResico") + ", '" + ur + "' )";
                                if (montoPasivoDiferdio.compareTo(new BigDecimal(0.0f)) > 0) {
                                    nDocRenglon++;
                                    sqlinsetP = sqlinsetP + ";" + "INSERT INTO tPagadoDetalle (cTipoPago,nFolioPAGO,nDocRenglon,cMes,cEjercicio,cIdEntidadContable,cIdRelacion,EP,cIdCuentaContable,mComprometido,nPoliza,ID_TIPO_MOVIMIENTO,ID_TIPO_CONCEPTO,cEvento,nMes,cCentroContable,RFC, mImporteNeto,ALM,mImporteBruto,mImporteMasIva,mImporteIva,nCapitulo,mSancion,mDevolucion,mImporteAmortiza,mRetencion,mPenalizacion,m2Millar,m23IVA,mISRHonorarios,mObra5    ,mImporteFlete4,mISRArrenda,mRetImpuestoCedular,mImporteIvaArrenda,mImporteIvaHonorarios ,mImporteFlete23,mImporteIvaProv ,mImporteObra,mCNIC,mTesofe,altaAlmacen,aEjercicioFiscal,mIMDT,mImporte,nFolioPagado,OBGT,ctab,mImporteISRLaudos,mISROtros, mPasivoDiferido, mImporteIva6, mimporteISRResico, cUnidadResponsable)" + "VALUES('" + strLetratipoPago + "'," + nFolioPago + "," + nDocRenglon + "," + cMes + ",'" + strcEjercicio + "','" + cIdEntidadContable + "','" + cIdRelacion + "','" + EP + "','" + cIdCuentaContable + "'," + mComprometido + "," + nPoliza + ",'" + idTipoMovimiento + "','" + idTipoConcepto + "','" + cEventoPagado + "','" + cMes + "','" + cCentroContable + "','TESOFE'," + "0,'" + alm + "',0,0,0,'" + nCapitulo + "',0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,'" + altaAlmacen + "','" + intaEjercicioFiscal + "',0,0," + strFolioPagado + ",'" + cOBGT + "','" + strCTAB + "',0,0," + montoPasivoDiferdio + ",0, 0, '" + ur + "' )";
                                    renglonPasivoDiFerido++;
                                }
                                if (!"".equals(cPasivo) && "P_DDNORE01".equals(cEventoPagado)) {
                                    nDocRenglon = nDocRenglon + 1;
                                    sqlinsetP = sqlinsetP + ";" + "INSERT INTO tPagadoDetalle (cTipoPago,nFolioPAGO,nDocRenglon,cMes,cEjercicio,cIdEntidadContable,cIdRelacion,EP,cIdCuentaContable,mComprometido,nPoliza,ID_TIPO_MOVIMIENTO,ID_TIPO_CONCEPTO,cEvento,nMes,cCentroContable,RFC, mImporteNeto,ALM,mImporteBruto,mImporteMasIva,mImporteIva,nCapitulo,mSancion,mDevolucion,mImporteAmortiza,mRetencion,mPenalizacion,m2Millar,m23IVA,mISRHonorarios,mObra5    ,mImporteFlete4,mISRArrenda,mRetImpuestoCedular,mImporteIvaArrenda,mImporteIvaHonorarios ,mImporteFlete23,mImporteIvaProv ,mImporteObra,mTesofe,altaAlmacen,aEjercicioFiscal,mIMDT,mImporte,nFolioPagado,OBGT,ctab,mImporteISRLaudos,mISROtros, mPasivoDiferido, mImporteIva6, mimporteISRResico, cUnidadResponsable,mCNIC) VALUES('" + strLetratipoPago + "'," + nFolioPago + "," + nDocRenglon + "," + cMes + ",'" + strcEjercicio + "','" + cIdEntidadContable + "','" + cIdRelacion + "','" + EP + "','" + cIdCuentaContable + "',0.00,0.00,'','','" + cEventoPagado + "','" + cMes + "','" + cCentroContable + "','LAUD000000000'," + "0,'',0,0,0,'" + nCapitulo + "',0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,'','" + intaEjercicioFiscal + "',0,0," + strFolioPagado + ",'" + cOBGT + "','',0,0,0,0, 0, '" + ur + "'," + rs2.getString("mImporteNeto") + " )";
                                }
                                if (!"".equals(cPasivo) && "S".equals(cEsPagoCuotas)) {
                                    nDocRenglon = nDocRenglon + 1;
                                    sqlinsetP = sqlinsetP + ";" + "INSERT INTO tPagadoDetalle (cTipoPago,nFolioPAGO,nDocRenglon,cMes,cEjercicio,cIdEntidadContable,cIdRelacion,EP,cIdCuentaContable,mComprometido,nPoliza,ID_TIPO_MOVIMIENTO,ID_TIPO_CONCEPTO,cEvento,nMes,cCentroContable,RFC, mImporteNeto,ALM,mImporteBruto,mImporteMasIva,mImporteIva,nCapitulo,mSancion,mDevolucion,mImporteAmortiza,mRetencion,mPenalizacion,m2Millar,m23IVA,mISRHonorarios,mObra5    ,mImporteFlete4,mISRArrenda,mRetImpuestoCedular,mImporteIvaArrenda,mImporteIvaHonorarios ,mImporteFlete23,mImporteIvaProv ,mImporteObra,mTesofe,altaAlmacen,aEjercicioFiscal,mIMDT,mImporte,nFolioPagado,OBGT,ctab,mImporteISRLaudos,mISROtros, mPasivoDiferido, mImporteIva6, mimporteISRResico, cUnidadResponsable, mCNIC, cPasivo) VALUES('" + strLetratipoPago + "'," + nFolioPago + "," + nDocRenglon + "," + cMes + ",'" + strcEjercicio + "','" + cIdEntidadContable + "','" + cIdRelacion + "','" + EP + "','" + cIdCuentaContable + "',0.00,0.00,'','','" + cEventoPagado + "','" + cMes + "','" + cCentroContable + "','" + RFC_PC + "'," + "0,''," + mImporteBruto + ",0,0,'" + nCapitulo + "',0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,'','" + intaEjercicioFiscal + "',0,0," + strFolioPagado + ",'" + cOBGT + "','',0,0,0,0, 0, '" + ur + "',0,'" + PC + "' )";
                                }
                                pstmntInsertDetPag = conn.prepareStatement(sqlinsetP);
                                intDetPag = pstmntInsertDetPag.executeUpdate();
                                log.debug("Object: " + String.valueOf("Se insertaron en tPagadoDetalle: " + intDetPag));
                            }
                        }
                        if ("S".equals(cEsPagoCuotas)) {
                            psUpdateRFCCuotas = conn.prepareStatement("UPDATE tPagadoDetalle SET mImporteBruto = 0 WHERE nFolioPAGO = ? AND nFolioPagado = ? AND RFC = ?");
                            psUpdateRFCCuotas.setInt(1, nFolioPago);
                            psUpdateRFCCuotas.setString(2, strFolioPagado);
                            psUpdateRFCCuotas.setString(3, cRFCCuotas);
                            psUpdateRFCCuotas.executeUpdate();
                        }
                    } else {
                        log.debug("No Guarda Encabezado");
                    }
                    // APLICACION CONTABLE
                    if (("").equals(ejercidoAplicado) || ejercidoAplicado == null)
                        accEng.makeAccountingApplication(conn, "EJERCIDO", strFolioEjercido, "tEjercidoEncabezado", "tEjercidoDetalle", "nFolioEjercido");
                    if (("").equals(pagadoAplicado) || pagadoAplicado == null || intDetPag > 0)
                        accEng.makeAccountingApplication(conn, "PAGADO", strFolioPagado, "tPagadoEncabezado", "tPagadoDetalle", "nFolioPagado");
                    pstmEnviadoSICOP = conn.prepareStatement("UPDATE " + strTabla + " SET nEnviadoSICOP = 1 WHERE caNoContrarrecibo = '" + strCXP + "'");
                    pstmEnviadoSICOP.executeUpdate();
                    conn.commit();
                    valor = "ingresado";
                }
            }
        } catch (Exception e) {
            valor = e.toString();
            log.warn(e.getMessage(), e);
            log.debug("Object: " + String.valueOf(e.getLocalizedMessage()));
            try {
                conn.rollback();
            } catch (Exception ee) {
                log.warn("Error: cerrando rollback ", ee);
            }
        } finally {
            try {
                if (!valor.equals("ingresado")) {
                    conn.rollback();
                }
                CloseObject.closeObject(rs);
                CloseObject.closeObject(rs2);
                CloseObject.closeObject(rsRadicado);
                CloseObject.closeObject(rsbe);
                CloseObject.closeObject(rsbp);
                CloseObject.closeObject(pstmtnSelect);
                CloseObject.closeObject(pstmntInsertEj);
                CloseObject.closeObject(pstmnSelectDet);
                CloseObject.closeObject(pstmntInsertDet);
                CloseObject.closeObject(pstmntInsertPag);
                CloseObject.closeObject(pstmntInsertDetPag);
                CloseObject.closeObject(pstmRadicado);
                CloseObject.closeObject(upd_fAplicacion);
                CloseObject.closeObject(pstmUpEjercido);
                CloseObject.closeObject(pstmSeqEjercido);
                CloseObject.closeObject(pstmUpPagado);
                CloseObject.closeObject(pstmBuscaEjercido);
                CloseObject.closeObject(pstmBuscaPagado);
                CloseObject.closeObject(conn);
            } catch (Exception e) {
                log.warn("Error: cerrando statement", e);
            }
        }
        return valor;
    }
}
