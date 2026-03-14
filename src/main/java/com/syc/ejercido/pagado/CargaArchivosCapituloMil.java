package com.syc.ejercido.pagado;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;
import com.axtel.egresos.core.CargaMasivaRG;
import com.syc.cfdi.db.CloseObject;
import com.syc.contable.caja.core.CajaManager;
import com.syc.contable.core.RelacionGastosManager;
import com.syc.dsmngr.DataSourceManager;
import com.syc.fortimax.core.Aplicacion;
import com.syc.fortimax.core.AplicacionManager;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoManager;
import com.syc.gestion.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CargaArchivosCapituloMil extends DataSourceManager {

    private static Logger log = LoggerFactory.getLogger(CargaArchivosCapituloMil.class);

    public String enviaRuta(InputStream in, String tipoArchivo, String nFolioCompromisoNomina, String fCarga, String cIdContrato, String cTipoContrato, String fAplicacion2, String cCentroContable, String cRamo, String cUnidadResponsable, String caNoContrarreciboNomina, String nMes, String aEjercicioFiscal, String cUnidadResponsableContable, String idCaso, String fRecepcion, String login, String recibo_ini, String buscarSequence, String compromisoAmpliado, String esAmpliacionReduccion) throws FileNotFoundException {
        BufferedReader brr = new BufferedReader(new InputStreamReader(in));
        String sCadenas = "";
        String valorReturn = "";
        double importeTotal = 0;
        String caNoCompromiso = "";
        String estatus = "";
        Connection conn = null;
        String EP_EjercicioFiscal = "";
        try {
            int primerLinea = 1;
            int nDocRenglon = 0;
            String idRelacion = "";
            String rfc = "";
            conn = getConnection();
            String ErrorEPs = " Se encontraron los siguientes errores: ";
            boolean bError = false;
            String aplica = "";
            if (tipoArchivo.equals("docComprometido")) {
                caNoCompromiso = caNoContrarreciboNomina;
                insertaLineaComprometidoEnc(conn, nFolioCompromisoNomina, fCarga, cIdContrato, cTipoContrato, fAplicacion2, cCentroContable, cRamo, cUnidadResponsable, caNoCompromiso, nMes, aEjercicioFiscal, cUnidadResponsableContable, idCaso, fRecepcion, login, compromisoAmpliado, esAmpliacionReduccion);
            }
            while ((sCadenas = brr.readLine()) != null) {
                nDocRenglon = nDocRenglon + 1;
                if (tipoArchivo.equals("docComprometido")) {
                    EP_EjercicioFiscal = "";
                    String[] celdas = sCadenas.split(",");
                    String EP = celdas[0].trim();
                    String mImporte = celdas[1].trim().replace(",", "");
                    String cMes = celdas[2].trim();
                    EP_EjercicioFiscal = EP.substring(0, 4);
                    if (aEjercicioFiscal.equals(EP_EjercicioFiscal)) {
                        insertaLineaComprometidoDet(conn, nFolioCompromisoNomina, nDocRenglon, EP, mImporte, cMes, cCentroContable, esAmpliacionReduccion);
                        estatus = "aplicado";
                    } else {
                        bError = true;
                        ErrorEPs = ErrorEPs + "\\nLa EP: " + EP + ", no pertene al ejercicio fiscal.";
                    }
                } else {
                    EP_EjercicioFiscal = "";
                    String[] celdas = sCadenas.split(",");
                    idRelacion = cIdContrato;
                    caNoCompromiso = celdas[0].trim();
                    String EP = celdas[1].trim();
                    String cMes = celdas[2].trim();
                    String tipoMovimiento = celdas[3].trim();
                    String tipoConcepto = celdas[4].trim();
                    rfc = celdas[5].trim();
                    String importeNeto = celdas[6].trim();
                    String claveInterna = EP.substring(56, 63);
                    EP_EjercicioFiscal = EP.substring(0, 4);
                    aplica = buscaCompromisoAplicado(conn, caNoCompromiso);
                    if (aplica.equals("aplicado")) {
                        if (!aEjercicioFiscal.equals(EP_EjercicioFiscal)) {
                            bError = true;
                            ErrorEPs = ErrorEPs + "\\nLa EP: " + EP + " no coincide con el ejercicio fiscal.";
                        } else {
                            estatus = insertaLineaNominaCargaArchivo(conn, nFolioCompromisoNomina, EP, claveInterna, tipoConcepto, tipoMovimiento, importeNeto, cMes, idRelacion, rfc, caNoCompromiso);
                            if (estatus.trim().equals("")) {
                                bError = true;
                                ErrorEPs = ErrorEPs + "\\nLa EP: " + EP + " no se encontro ó no pertenece al compromiso.";
                            } else {
                                estatus = "aplicado";
                            }
                        }
                    } else {
                        bError = true;
                        ErrorEPs = ErrorEPs + "\\nEl compromiso " + caNoCompromiso + " se encuentra: " + aplica;
                        break;
                    }
                    importeTotal = importeTotal + Double.parseDouble(importeNeto);
                }
                primerLinea = primerLinea + 1;
            }
            if (!bError) {
                valorReturn = estatus;
                if ("docComprometido".equals(tipoArchivo) && "aplicado".equals(valorReturn) && "R".equals(esAmpliacionReduccion)) {
                    valorReturn = validaReduccionCompromisoMil(conn, nFolioCompromisoNomina, compromisoAmpliado);
                }
                if (tipoArchivo.equals("docNomina") && estatus.equals("aplicado")) {
                    String cIdRelacion = cIdContrato;
                    String res = ejecutaProcedimientos(conn, nFolioCompromisoNomina, nMes, cIdRelacion, rfc, aEjercicioFiscal, caNoCompromiso);
                    if (res.equals("guardado")) {
                        String respuesta = insertaLineaNominaEnc(conn, nFolioCompromisoNomina, fCarga, cIdRelacion, cTipoContrato, fAplicacion2, cCentroContable, cRamo, cUnidadResponsable, caNoContrarreciboNomina, nMes, aEjercicioFiscal, cUnidadResponsableContable, "cDescripcionPoliza", idCaso, login, importeTotal, "idRelacion", rfc, fRecepcion, caNoCompromiso, recibo_ini, buscarSequence);
                        if (respuesta.equals("")) {
                            res = respuesta;
                        }
                    }
                    valorReturn = res;
                }
                if (valorReturn.equals("guardado") || valorReturn.equals("aplicado")) {
                    conn.commit();
                } else {
                    conn.rollback();
                }
            } else {
                conn.rollback();
                valorReturn = ErrorEPs;
            }
        } catch (Exception se) {
            log.error("Error occurred", "Error: " + se);
            se.printStackTrace();
            valorReturn = "Error";
            try {
                conn.rollback();
            } catch (Exception exc) {
                log.warn("Error occurred", "Error: cerrando rollback enviaRuta " + exc);
            }
        } finally {
            try {
                if (brr != null)
                    brr.close();
            } catch (Exception exc) {
                log.warn("Cerrando BufferedReader", exc);
            }
            brr = null;
            CloseObject.closeObject(conn);
        }
        return valorReturn;
    }

    public String insertaLineaComprometidoEnc(Connection conn, String nFolioCompromisoNomina, String fCarga, String cIdContrato, String cTipoContrato, String fAplicacion2, String cCentroContable, String cRamo, String cUnidadResponsable, String caNoCompromiso, String nMes, String aEjercicioFiscal, String cUnidadResponsableContable, String cDescripcionPoliza, String idCaso, String login, String compromisoAmpliado, String esAmpliacionReduccion) {
        String respuesta = "";
        int idBitacora = 0;
        PreparedStatement ps = null, pscaso = null, psInserta = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement("INSERT INTO tCompromisoNominaEncabezado (nFolioCompromisoNomina,fCarga,cIdContrato,cTipoContrato,fAplicacion, cCentroContable,cRamo,cUnidadResponsable,caNoCompromiso,nMes,aEjercicioFiscal,cUnidadResponsableContable,cDescripcionPoliza, cTipoPoliza, caNoCompromisoAmpliado, tipoCompromiso)" + " VALUES (" + nFolioCompromisoNomina + ",'" + fCarga + "','" + cIdContrato + "','" + cTipoContrato + "','" + fAplicacion2 + "','" + cCentroContable + "','" + cRamo + "','" + cUnidadResponsable + "','" + caNoCompromiso + "'," + nMes + ",'" + aEjercicioFiscal + "','" + cUnidadResponsableContable + "','" + cDescripcionPoliza + "', 'CO', '" + compromisoAmpliado + "', '" + esAmpliacionReduccion + "')");
            ps.executeUpdate();
            pscaso = conn.prepareStatement("SELECT MAX(ID_BITACORA) + 1  AS idBitacora FROM CG_BITACORA");
            rs = pscaso.executeQuery();
            String b_c_folio = "COMP-" + cUnidadResponsable + "-" + nFolioCompromisoNomina;
            if (rs.next()) {
                idBitacora = rs.getInt("idBitacora");
                psInserta = conn.prepareStatement("INSERT INTO CG_BITACORA (ID_BITACORA, B_ID_CASO, B_ID_CASO_OPER, B_C_ID_GABINETE, B_C_FOLIO, 		B_C_FECHA_INI, B_C_TIEMPO_LIMITE, B_C_STATUS, B_ID_TC, B_ID_OPER, B_CO_FECHA_INI, B_CO_TIEMPO_LIMITE, B_CO_RESPONSABLE_EJEC, B_CO_ID_CASO_OPER_SIGTE, B_CO_RESPONSABLE_SIGTE, B_CO_OPERACION_SIGTE, B_CO_OBSERVACION, B_CO_STATUS) " + "	VALUES (									  " + idBitacora + "," + idCaso + ", 		2 ,			 -1 ,		'" + b_c_folio + "', '" + fCarga + "', 			-1, 			2, 				7, 		1, 		'" + fCarga + "', 	-1, 					'" + login + "', 			2, 				'CONSULTA_COMPROMISO', 		'consulta_compromiso', null, 1  )");
                psInserta.executeUpdate();
            }
            respuesta = "guardado";
        } catch (Exception e) {
            log.error("Error occurred", "Error Guardar Compromiso Encabezado: " + e);
        } finally {
            try {
                CloseObject.closeObject(ps);
                CloseObject.closeObject(pscaso);
                CloseObject.closeObject(psInserta);
                CloseObject.closeObject(rs);
            } catch (Exception ef) {
                log.warn("Error occurred", "Error: cerrando statement: " + ef);
            }
        }
        return respuesta;
    }

    public String insertaLineaComprometidoDet(Connection conn, String nFolioCompromisoNomina, int nDocRenglon, String EP, String mImporte, String cMes, String cCentroContable, String esAmpliacionReduccion) throws SQLException {
        String respuesta = "";
        PreparedStatement ps = null;
        int cuantos = 0;
        double mImporteNegativo = 0 - Double.parseDouble(mImporte);
        if ("R".equals(esAmpliacionReduccion))
            mImporteNegativo = -mImporteNegativo;
        try {
            ps = conn.prepareStatement("INSERT INTO tCompromisoNominaDetalle (nFolioCompromisoNomina,nDocRenglon,EP,cEvento,mImporte,mImporteNegativo,cMes,cCentroContable)" + " VALUES (" + nFolioCompromisoNomina + "," + nDocRenglon + ",'" + EP + "','CMP001'," + mImporte + "," + mImporteNegativo + "," + cMes + ",'" + cCentroContable + "' )");
            int intr = ps.executeUpdate();
            if (intr > 0) {
                respuesta = "guardado";
                cuantos = cuantos + 1;
            } else {
                respuesta = "error";
            }
        } catch (Exception e) {
            log.error("Error occurred", "Error Guardar tCompromisoNominaDetalle: " + e);
        } finally {
            CloseObject.closeObject(ps);
        }
        return respuesta;
    }

    public String insertaLineaNominaEnc(Connection conn, String nFolioNomina, String fCarga, String cIdRelacion, String cTipoContrato, String fAplicacion2, String cCentroContable, String cRamo, String cUnidadResponsable, String caNoContrarrecibo, String nMes, String aEjercicioFiscal, String cUnidadResponsableContable, String cDescripcionPoliza, String idCaso, String login, double importeTotal, String idRelacion, String rfc, String fRecepcion, String caNoCompromiso, String recibo_ini, String buscarSequence) throws SQLException {
        String respuesta = "";
        int idBitacora = 0;
        PreparedStatement ps = null, pscaso = null, psInserta = null, psInsertaRecibo = null, psRfc = null, psBs = null, psUp = null;
        ResultSet rs = null, rsp = null, rsRfc = null, rsBs = null;
        String cConcepto = "";
        String cnombre = "";
        String recibo_fin = "";
        try {
            String[] fp = fCarga.split("-");
            String fCargaConcepto = fp[0] + "-" + fp[1] + "-" + fp[2];
            cConcepto = "PAGO NOMINA EN FECHA " + fCargaConcepto;
            psUp = conn.prepareStatement("UPDATE CF_SEQUENCE  WITH (ROWLOCK) SET seq_value = seq_value + 1  WHERE seq_name = ? ");
            psUp.setString(1, buscarSequence);
            psUp.executeUpdate();
            psBs = conn.prepareStatement("SELECT seq_value FROM CF_SEQUENCE (NOLOCK) WHERE seq_name = ? ");
            psBs.setString(1, buscarSequence);
            rsBs = psBs.executeQuery();
            if (rsBs.next()) {
                recibo_fin = rsBs.getString("seq_value");
            }
            String recibo = "000000" + recibo_fin;
            recibo_fin = recibo.substring(recibo.length() - 6);
            recibo_fin = "1" + recibo_fin.substring(recibo_fin.length() - 5);
            caNoContrarrecibo = recibo_ini + recibo_fin;
            psRfc = conn.prepareStatement("SELECT cnombre FROM v_PCATALOGORFC where cIDRFC = '" + rfc + "'");
            rsRfc = psRfc.executeQuery();
            if (rsRfc.next()) {
                cnombre = rsRfc.getString("cnombre");
            }
            ps = conn.prepareStatement("INSERT INTO tNOMINAEncabezado (nFolioNOMINA, fAplicacion, 			cRamo, cUnidadResponsable, 		cEjercicio, 	cIdEntidadContable, cIdRelacion, cIdTipoDocumento, cIdTipoRelacion, cIdTipoMontoDesembolso, cIdRFC, fRecepcion, 		fRevision, 			fProgramadaPago, cConcepto, mImporteNeto, 	cIdUnidadAdministrativa, cIdGRegional, cIdGEstatal, cIdDistritoRiego, cIdTipoFondo, caNoContrarrecibo, caNoAP, cIdEstadoRelacion, lContrarreciboImpreso, nIdConcepto, cIdTipoLimiteDlls, cIdUsuarioCaptura, cIdUsuarioImpresion, cIdUsuarioRevision, cIdUsuarioAprobacion, cIdUsuarioRechazo, lSuficienciaAnualValidada, lSuficienciaMensualValidada, ID_DESTINO_GASTO, ID_TIPO_MOVIMIENTO, ID_TIPO_CONCEPTO, cnombre, TIPO_OPERACION, cEvento, aEjercicioFiscal, 		cCentroContable,		 cMes,		 RFC, 	mImporteBruto, mImporteMasIva, cTipoPoliza, cDescripcionPoliza, caNoCompromiso )" + "VALUES 	(" + nFolioNomina + ",		'" + fAplicacion2 + "','" + cRamo + "','" + cUnidadResponsable + "','" + aEjercicioFiscal + "', '00', 	'" + cIdRelacion + "', '3',				 'T', 				'0', 			'" + rfc + "', '" + fRecepcion + "', '" + fRecepcion + "', '" + fRecepcion + "', '" + cConcepto + "', 	" + importeTotal + ", '000', 				'00', 				'00', 		'00', 			 '0', 		'" + caNoContrarrecibo + "', '00', '0',				 0, 				0, 				'0',			 '" + login + "',			 '0', 				'0', 					'0', 			'0',				 0, 						0, 						'NONO', 					'0',				 '0', 	'" + cnombre + "', 	'NOMINA',	 'CD_AL01', '" + aEjercicioFiscal + "', '" + cCentroContable + "', '" + nMes + "', '" + rfc + "', " + importeTotal + ", " + importeTotal + ", 'EG','" + cConcepto + "','" + caNoCompromiso + "'  ) ");
            int intr = ps.executeUpdate();
            pscaso = conn.prepareStatement("SELECT MAX(ID_BITACORA) + 1  AS idBitacora FROM CG_BITACORA WITH (NOLOCK)");
            rs = pscaso.executeQuery();
            String b_c_folio = "NOMI-" + cUnidadResponsable + "-" + nFolioNomina;
            if (rs.next()) {
                idBitacora = rs.getInt("idBitacora");
                psInserta = conn.prepareStatement("INSERT INTO CG_BITACORA (ID_BITACORA,   B_ID_CASO, B_ID_CASO_OPER, B_C_ID_GABINETE, B_C_FOLIO,      B_C_FECHA_INI, B_C_TIEMPO_LIMITE, B_C_STATUS, B_ID_TC, B_ID_OPER, B_CO_FECHA_INI, B_CO_TIEMPO_LIMITE, B_CO_RESPONSABLE_EJEC, B_CO_ID_CASO_OPER_SIGTE, B_CO_RESPONSABLE_SIGTE, B_CO_OPERACION_SIGTE, B_CO_OBSERVACION, B_CO_STATUS) " + "											VALUES (" + idBitacora + "," + idCaso + ",        3 ,        -1 ,         '" + b_c_folio + "', '" + fCarga + "',         -1,             1,           12,       3,       '" + fCarga + "',       -1,               '" + login + "',                4,              'CONSULTA_NOMINA', 'consulta_nomina',    null,           1  )");
                psInserta.executeUpdate();
            }
            if (intr > 0) {
                psInsertaRecibo = conn.prepareStatement("INSERT INTO tContrarrecibo (aEjercicioFiscal, cIdEntidadContable, caNoContrarrecibo,cIdDocumento,cIdTipoDocumento,cIdSubtipoDocumento,cIdTipoOperacion,caNoAP,nPolizaAP, cIdRFC, cIdTipoMoneda,nTipoCambio,mImporteBruto,  mImporteSancion, mImporteDevolucion, mAmortizacionAnticipo,  mImporteIVA, mImporteRetencion,mImportePenalizacion, mImporteNeto,   fProgramadaPago,    fValidacionDisponible, lEsCandidatoCP, lHaySubpartidaExcluidaCP, cIdTipoCLCSiaff,  cIdLeyendaSiaff,cReferencia1,nEsSubsidio,   fRegistro,		cIdEstadoReintegro,cIdTipoReclasificacion,fAplicacionSiaff,fPagoSiaff,cIdEstadoContrarrecibo )" + " VALUES(" + aEjercicioFiscal + "," + cCentroContable + ",'" + caNoContrarrecibo + "','" + idRelacion + "',		'B', 			0,      			1, 				0,  	null, '" + rfc + "', '01', 	0.0000,    " + importeTotal + ",     0.00,			 0.00,				0.00, 					0.00,		0.00,  				0.00,         " + importeTotal + ", '" + fRecepcion + "',  	null, 				   0,                0,      					1,				 1,				null,		0,		 '" + fRecepcion + "',  0,					0, 			 			null, 		 null,			null   ) ");
                psInsertaRecibo.executeUpdate();
                CallableStatement proc = conn.prepareCall("{ CALL sp_regenera_caso (?) }");
                proc.setString(1, caNoContrarrecibo);
                proc.execute();
                // JDS Se agrega para tener un caso nuevo
                Caso c = new Caso();
                c.setFolio(b_c_folio);
                c = CasoManager.select(conn, c);
                if (c.getIdGabinete() < 0) {
                    Aplicacion app = AplicacionManager.select(conn, c.getTipoCaso().getGavetaAsociada());
                    int id_gabinete = AplicacionManager.createExpediente(conn, login, c, app);
                    c.setIdGabinete(id_gabinete);
                    CasoManager.update(conn, c);
                }
                respuesta = "guardado";
            }
        } catch (Exception e) {
            log.warn("Error occurred", "Error: tNominaEncabezado: " + e);
        } finally {
            try {
                CloseObject.closeObject(ps);
                CloseObject.closeObject(rs);
                CloseObject.closeObject(pscaso);
                CloseObject.closeObject(rsp);
                CloseObject.closeObject(psInserta);
                CloseObject.closeObject(psInsertaRecibo);
                CloseObject.closeObject(psRfc);
                CloseObject.closeObject(rsRfc);
            } catch (Exception ef) {
                log.warn("Error: cerrando statement ", ef);
            }
        }
        return respuesta;
    }

    public String insertaLineaNominaDet(String nFolioNomina, int nDocRenglon, String cMes, String aEjercicioFiscal, String cidRelacion, String EP, String importeNeto, String tipoMovimiento, String tipoConcepto, String rfc) throws SQLException {
        String respuesta = "";
        PreparedStatement ps = null;
        Connection conn = null;
        try {
            String evento = "CD_NOM_" + tipoConcepto + "01";
            conn = getConnection();
            ps = conn.prepareStatement("INSERT INTO tNOMINAdetalle (nFolioNOMINA, nDocRenglon, nMes, cEjercicio, cIdEntidadContable, cIdRelacion, EP, cIdCuentaContable, mComprometido, nPoliza, ID_TIPO_MOVIMIENTO, ID_TIPO_CONCEPTO, cEvento, aEjercicioFiscal, cCentroContable, cMes, RFC, mImporteNeto, ALM, mImporteBruto, mImporteMasIva, mImporteIva, nCapitulo, mSancion, mDevolucion, mImporteAmortiza, mRetencion, mPenalizacion, m2Millar, m23IVA, mISRHonorarios, mObra5, mImporteFlete4, mISRArrenda, mRetImpuestoCedular, mBruto, mAmortizacionAnticipo, mIVA, mNeto, m5Millar, mFletes, mCedular, mImporte, mImporteIvaArrenda, mImporteIvaHonorarios, mImporteFlete23, mImporteIvaProv, mImporteObra, mCNIC, mIMDT, mTesofe, mImporteNegativo )" + "VALUES (" + nFolioNomina + "," + nDocRenglon + "," + cMes + ",'" + aEjercicioFiscal + "','00','" + cidRelacion + "','" + EP + "', '', " + importeNeto + ", 1 , '" + tipoMovimiento + "', '" + tipoConcepto + "', '" + evento + "', '" + aEjercicioFiscal + "', 10, '" + cMes + "', '" + rfc + "', " + importeNeto + ", '1', " + importeNeto + ", " + importeNeto + ", " + importeNeto + ", '10000', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 ) ");
            int intr = ps.executeUpdate();
            if (intr > 0) {
                respuesta = "guardado";
                conn.commit();
            }
        } catch (Exception e) {
            log.warn("Error occurred", "Error: tNominaEncabezado: " + e);
            conn.rollback();
        } finally {
            CloseObject.closeObject(ps);
            CloseObject.closeObject(conn);
        }
        return respuesta;
    }

    public double sumarImporteNetoTotalCompromiso(String caNoCompromiso) throws SQLException {
        double valorReturn = 0.00;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = null;
        double importeTotalNomina = 0.00;
        try {
            conn = getConnection();
            ps = conn.prepareStatement("SELECT isnull(SUM(nD.mImporteNeto), 0.00) AS importeTotal " + "FROM tNominaEncabezado nE WITH (NOLOCK) " + "INNER JOIN tNOMINADetalle nD WITH (NOLOCK) ON nE.nFolioNOMINA = nD.nFolioNOMINA " + "WHERE caNoCompromiso = '" + caNoCompromiso + "' " + "AND ( nE.cDocumentoHaplicado = 'S' OR nE.cDocumentoHaplicado is null ) ");
            rs = ps.executeQuery();
            if (rs.next()) {
                importeTotalNomina = rs.getDouble("importeTotal");
            }
            valorReturn = importeTotalNomina;
        } catch (Exception e) {
            log.warn("Error: Sumar Importe Nomina");
        } finally {
            CloseObject.closeObject(ps);
            CloseObject.closeObject(rs);
            CloseObject.closeObject(conn);
        }
        return valorReturn;
    }

    public BigDecimal sumarImporteNetoTotalCompromisoBig(String caNoCompromiso) throws SQLException {
        BigDecimal valorReturn = new BigDecimal(0);
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = null;
        BigDecimal importeTotalNomina = new BigDecimal(0);
        try {
            conn = getConnection();
            ps = conn.prepareStatement("SELECT SUM(IMPORTETOTAL) AS importeTotal FROM ( " + "SELECT isnull(SUM(nD.mImporteNeto), 0.00) AS importeTotal " + "FROM tNominaEncabezado nE WITH (NOLOCK)  " + "INNER JOIN tNOMINADetalle nD WITH (NOLOCK) ON nE.nFolioNOMINA = nD.nFolioNOMINA " + "WHERE caNoCompromiso = '" + caNoCompromiso + "' " + "	AND ( nE.cDocumentoHaplicado = 'S' OR nE.cDocumentoHaplicado is null ) " + "UNION " + "SELECT ISNULL(SUM(MIMPORTE),0) FROM tReintegroAutDetalleMil DET WITH (NOLOCK) " + "INNER JOIN tNOMINAEncabezado NOM WITH (NOLOCK) " + "ON DET.cxp = NOM.caNoContrarrecibo " + "WHERE caNoCompromiso = '" + caNoCompromiso + "' " + ") AS TblTotal");
            rs = ps.executeQuery();
            if (rs.next()) {
                importeTotalNomina = rs.getBigDecimal("importeTotal").setScale(2, RoundingMode.HALF_UP);
            }
            valorReturn = importeTotalNomina.setScale(2, RoundingMode.HALF_UP);
        } catch (Exception e) {
            log.warn("Error: Sumar Importe Nomina");
        } finally {
            CloseObject.closeObject(ps);
            CloseObject.closeObject(rs);
            CloseObject.closeObject(conn);
        }
        return valorReturn;
    }

    public double sumarImporteTotalComprometido(String caNoCompromiso) throws SQLException {
        double valorReturn = 0.00;
        PreparedStatement ps = null, pss = null, psDev = null;
        ResultSet rs = null, rss = null, rsDev = null;
        Connection conn = null;
        double mImporteDetCompromiso = 0.00;
        double mImporteDevCXP = 0.00;
        try {
            conn = getConnection();
            ps = conn.prepareStatement("SELECT nFolioCompromisoNomina FROM tCompromisoNominaEncabezado WITH (NOLOCK) WHERE caNoCompromiso = '" + caNoCompromiso + "'");
            rs = ps.executeQuery();
            if (rs.next()) {
                pss = conn.prepareStatement("SELECT ISNULL(SUM(compDet.mimporte), 0.00) AS mImporteDet " + "FROM tCompromisoNominaEncabezado compEnc WITH (NOLOCK) " + "INNER JOIN tCompromisoNominaDetalle compDet WITH (NOLOCK) " + "	ON compEnc.nFolioCompromisoNomina = compDet.nFolioCompromisoNomina " + "WHERE compEnc.cdocumentohaplicado = 'S' " + "	AND CASE WHEN caNoCompromisoAmpliado IS NULL THEN caNoCompromiso WHEN caNoCompromisoAmpliado = '' THEN caNoCompromiso ELSE caNoCompromisoAmpliado END = '" + caNoCompromiso + "'");
                rss = pss.executeQuery();
                if (rss.next()) {
                    mImporteDetCompromiso = rss.getDouble("mImporteDet");
                    psDev = conn.prepareStatement("SELECT SUM(dnd.mImporteNeto * -1) as mImporteNeto FROM tNOMINADevDetalle dnd WITH (NOLOCK), tNOMINAEncabezado en WITH (NOLOCK) WHERE dnd.caNoContrarrecibo = en.caNoContrarrecibo and en.caNoCompromiso = '" + caNoCompromiso + "'");
                    rsDev = psDev.executeQuery();
                    if (rsDev.next()) {
                        mImporteDevCXP = rsDev.getDouble("mImporteNeto");
                    }
                }
            }
            valorReturn = mImporteDetCompromiso + mImporteDevCXP;
        } catch (Exception e) {
            log.warn("Error: Sumar Importe Compromiso");
        } finally {
            CloseObject.closeObject(ps);
            CloseObject.closeObject(pss);
            CloseObject.closeObject(psDev);
            CloseObject.closeObject(rss);
            CloseObject.closeObject(rs);
            CloseObject.closeObject(rsDev);
            CloseObject.closeObject(conn);
        }
        return valorReturn;
    }

    public BigDecimal sumarImporteTotalComprometidoBig(String caNoCompromiso) throws SQLException {
        BigDecimal valorReturn = new BigDecimal(0);
        PreparedStatement ps = null, pss = null, psDev = null;
        ResultSet rs = null, rss = null, rsDev = null;
        Connection conn = null;
        BigDecimal mImporteDetCompromiso = new BigDecimal(0);
        BigDecimal mImporteDevCXP = new BigDecimal(0);
        try {
            conn = getConnection();
            ps = conn.prepareStatement("SELECT nFolioCompromisoNomina FROM tCompromisoNominaEncabezado WITH (NOLOCK) WHERE caNoCompromiso = '" + caNoCompromiso + "'");
            rs = ps.executeQuery();
            if (rs.next()) {
                pss = conn.prepareStatement("SELECT ISNULL(SUM(compDet.mimporte), 0.00) AS mImporteDet " + "FROM tCompromisoNominaEncabezado compEnc WITH (NOLOCK) " + "INNER JOIN tCompromisoNominaDetalle compDet WITH (NOLOCK) " + "	ON compEnc.nFolioCompromisoNomina = compDet.nFolioCompromisoNomina " + "WHERE compEnc.cdocumentohaplicado = 'S' " + "	AND CASE WHEN caNoCompromisoAmpliado IS NULL THEN caNoCompromiso WHEN caNoCompromisoAmpliado = '' THEN caNoCompromiso ELSE caNoCompromisoAmpliado END = '" + caNoCompromiso + "' " + " AND		compEnc.nFolioSICOP IS NOT NULL ");
                rss = pss.executeQuery();
                if (rss.next()) {
                    mImporteDetCompromiso = rss.getBigDecimal("mImporteDet").setScale(2, RoundingMode.HALF_UP);
                    psDev = conn.prepareStatement("SELECT ISNULL(SUM(dnd.mImporteNeto * -1), 0) as mImporteNeto FROM tNOMINADevDetalle dnd WITH (NOLOCK), tNOMINAEncabezado en WITH (NOLOCK) WHERE dnd.caNoContrarrecibo = en.caNoContrarrecibo and en.caNoCompromiso = '" + caNoCompromiso + "'");
                    rsDev = psDev.executeQuery();
                    if (rsDev.next()) {
                        mImporteDevCXP = rsDev.getBigDecimal("mImporteNeto").setScale(2, RoundingMode.HALF_UP);
                    }
                }
            }
            mImporteDetCompromiso = mImporteDetCompromiso.add(mImporteDevCXP);
            valorReturn = mImporteDetCompromiso.setScale(2, RoundingMode.HALF_UP);
        } catch (Exception e) {
            log.warn("Error: Sumar Importe Compromiso");
        } finally {
            CloseObject.closeObject(ps);
            CloseObject.closeObject(pss);
            CloseObject.closeObject(psDev);
            CloseObject.closeObject(rss);
            CloseObject.closeObject(rs);
            CloseObject.closeObject(rsDev);
            CloseObject.closeObject(conn);
        }
        return valorReturn;
    }

    public String insertaLineaNominaCargaArchivo(Connection conn, String nFolioNomina, String EPClaveSIAFF, String claveInterna, String tipoConcepto, String tipoMovimiento, String importeNeto, String cMes, String idRelacion, String rfc) throws SQLException {
        return insertaLineaNominaCargaArchivo(conn, nFolioNomina, EPClaveSIAFF, claveInterna, tipoConcepto, tipoMovimiento, importeNeto, cMes, idRelacion, rfc, "");
    }

    public String insertaLineaNominaCargaArchivo(Connection conn, String nFolioNomina, String EPClaveSIAFF, String claveInterna, String tipoConcepto, String tipoMovimiento, String importeNeto, String cMes, String idRelacion, String rfc, String caNoCompromiso) throws SQLException {
        String respuesta = "";
        PreparedStatement pssel = null, ps = null;
        ResultSet rssel = null;
        String nClaveCNA = "";
        try {
            pssel = conn.prepareStatement("SELECT nClaveCNA FROM tCatalogoEP WITH (NOLOCK) WHERE EP = '" + EPClaveSIAFF + "'");
            rssel = pssel.executeQuery();
            if (rssel.next()) {
                nClaveCNA = rssel.getString("nClaveCNA");
                pssel = conn.prepareStatement(" SELECT compromisoencabezado.* FROM tCompromisoNominaEncabezado compromisoencabezado WITH (NOLOCK) " + " INNER JOIN tCompromisoNominaDetalle compromisodetalle WITH (NOLOCK) " + " ON (compromisodetalle.nFolioCompromisoNomina = compromisoencabezado.nFolioCompromisoNomina) " + " WHERE (compromisoencabezado.caNoCompromiso = '" + caNoCompromiso + "' OR compromisoencabezado.caNoCompromisoAmpliado = '" + caNoCompromiso + "') AND compromisodetalle.EP = '" + EPClaveSIAFF + "'");
                rssel = pssel.executeQuery();
                if (rssel.next()) {
                    ps = conn.prepareStatement("INSERT INTO tNOMINACargaArchivo (nFolioNOMINA,	ClaveSIAFF, ClaveInterna, ID_TIPO_CONCEPTO, ID_TIPO_MOVIMIENTO, mSaldo, nClaveCNA, cMes )" + "VALUES (" + nFolioNomina + ",'" + EPClaveSIAFF + "','" + claveInterna + "','" + tipoConcepto + "','" + tipoMovimiento + "'," + importeNeto + ",'" + nClaveCNA + "', " + cMes + ") ");
                    ps.executeUpdate();
                    respuesta = "guardado";
                }
            }
        } catch (Exception e) {
            log.warn("Error occurred", "Error: tNominaEncabezadoCarga: " + e);
        } finally {
            CloseObject.closeObject(ps);
            CloseObject.closeObject(pssel);
            CloseObject.closeObject(rssel);
        }
        return respuesta;
    }

    public String ejecutaProcedimientos(Connection conn, String nFolioNomina, String cMes, String idRelacion, String rfc, String aEjercicioFiscal, String caNoCompromiso) throws SQLException {
        CallableStatement proc = null, procc = null;
        PreparedStatement pstmp = null, pstmExisteRfc = null;
        ResultSet rstmp = null, rsExisteRfc = null;
        String respuesta = "";
        try {
            pstmExisteRfc = conn.prepareStatement("SELECT sCodigoEntidad FROM dbo.tBeneficiarioCapituloMil WITH(NOLOCK) WHERE sCodigoEntidad = ?");
            pstmExisteRfc.setString(1, rfc);
            rsExisteRfc = pstmExisteRfc.executeQuery();
            if (rsExisteRfc.next()) {
                proc = conn.prepareCall(" { CALL facturaNomina_v2 (?, ?, ?, ?) } ");
                proc.setString(1, nFolioNomina);
                proc.setString(2, aEjercicioFiscal);
                proc.setString(3, cMes);
                proc.setString(4, caNoCompromiso);
                proc.execute();
                proc.close();
                procc = conn.prepareCall(" { CALL facturaNominaProceso (?, ?, ?, ?, ?, ?, ?) } ");
                procc.setString(1, nFolioNomina);
                procc.setString(2, idRelacion);
                procc.setString(3, "");
                procc.setInt(4, Integer.parseInt(aEjercicioFiscal, 10));
                procc.setString(5, "10");
                procc.setString(6, rfc);
                procc.setString(7, "1");
                procc.execute();
                procc.close();
                pstmp = conn.prepareStatement("SELECT ep, elError, nFolio FROM tmpNominaError WITH (NOLOCK) WHERE nFolio = ? ");
                pstmp.setString(1, nFolioNomina);
                rstmp = pstmp.executeQuery();
                String ep = "";
                String error = "";
                String nFolio = "";
                if (rstmp.next()) {
                    ep = rstmp.getString("ep");
                    error = rstmp.getString("elError");
                    nFolio = rstmp.getString("nFolio");
                    respuesta = error + "/" + ep + "/" + nFolio;
                } else {
                    respuesta = "guardado";
                }
            } else {
                respuesta = "NORFC/" + rfc;
            }
        } catch (Exception e) {
            log.warn("Error: tNominaEncabezadoCargaProcedimientos: " + e, e);
        } finally {
            CloseObject.closeObject(proc);
            CloseObject.closeObject(pstmExisteRfc);
            CloseObject.closeObject(procc);
            CloseObject.closeObject(pstmp);
            CloseObject.closeObject(rstmp);
            CloseObject.closeObject(rsExisteRfc);
        }
        return respuesta;
    }

    public String buscaCompromisoAplicado(Connection conn, String caNoCompromiso) throws SQLException {
        String respuesta = "noExiste";
        PreparedStatement ps = null;
        ResultSet rs = null;
        // Connection conn = null;
        String cDocumentoHaplicado = "";
        String nFolioSicop = "";
        try {
            ps = conn.prepareStatement("SELECT isnull(cDocumentoHaplicado, 'N') AS cDocumentoHaplicado , isnull(nFolioSicop, 'sinFolio') AS nFolioSicop FROM tCompromisoNominaEncabezado WITH (NOLOCK) WHERE caNoCompromiso = ? ");
            ps.setString(1, caNoCompromiso);
            rs = ps.executeQuery();
            if (rs.next()) {
                cDocumentoHaplicado = rs.getString("cDocumentoHaplicado");
                nFolioSicop = rs.getString("nFolioSicop");
            }
            if (cDocumentoHaplicado.equals("S")) {
                respuesta = "aplicado";
            }
            if (cDocumentoHaplicado.equals("C")) {
                respuesta = "cancelado";
            }
            if (nFolioSicop.equals("sinFolio")) {
                respuesta = "sinFolioSicop";
            }
            if (cDocumentoHaplicado.equals("N")) {
                respuesta = "sinAplicar";
            }
        } catch (Exception e) {
            log.warn("Error occurred", "Error: Buscar Compromiso Aplicado: " + e);
        } finally {
            CloseObject.closeObject(ps);
            CloseObject.closeObject(rs);
        }
        return respuesta;
    }

    public String existeTipoMovimiento(String tipoMovimiento) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        // No existe
        String respuesta = tipoMovimiento;
        try {
            conn = getConnection();
            ps = conn.prepareStatement("SELECT * FROM CAT_TIPO_MOVIMIENTO WITH (NOLOCK) WHERE ID_TIPO_MOVIMIENTO = ? ");
            ps.setString(1, tipoMovimiento);
            rs = ps.executeQuery();
            if (rs.next()) {
                respuesta = "existe";
            }
        } catch (Exception e) {
            log.warn("Error occurred", "Error: Buscar Compromiso Aplicado: " + e);
        } finally {
            CloseObject.closeObject(ps);
            CloseObject.closeObject(rs);
        }
        return respuesta;
    }

    public String muestraContraRecibo(String nFolioNOMINA) throws SQLException {
        Connection conn = null;
        PreparedStatement psNom = null;
        ResultSet rsNom = null;
        String caNoContrarrecibo = "";
        try {
            conn = getConnection();
            psNom = conn.prepareStatement("SELECT caNoContrarrecibo FROM tNOMINAEncabezado with(nolock) WHERE nFolioNomina = ? ");
            psNom.setInt(1, Integer.parseInt(nFolioNOMINA, 10));
            rsNom = psNom.executeQuery();
            if (rsNom.next()) {
                caNoContrarrecibo = rsNom.getString("caNoContrarrecibo");
            }
        } catch (Exception e) {
            log.warn("Error: Sequence ", e);
            try {
                conn.rollback();
            } catch (Exception rol) {
                log.warn("Error: ", rol);
            }
        } finally {
            CloseObject.closeObject(psNom);
            CloseObject.closeObject(rsNom);
            CloseObject.closeObject(conn);
        }
        return caNoContrarrecibo;
    }

    public String existeCompromiso(String caNoCompromiso) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        String valor = "noExiste";
        ResultSet rs = null;
        try {
            conn = getConnection();
            ps = conn.prepareStatement("SELECT * FROM tCompromisoNominaEncabezado WITH (NOLOCK) WHERE caNoCompromiso = ? ");
            ps.setString(1, caNoCompromiso);
            rs = ps.executeQuery();
            if (rs.next()) {
                valor = "existe";
            }
        } catch (Exception e) {
            log.warn("Error: Sequence ", e);
            try {
                conn.rollback();
            } catch (Exception rol) {
                log.warn("Error: ", rol);
            }
        } finally {
            CloseObject.closeObject(ps);
            CloseObject.closeObject(rs);
            CloseObject.closeObject(conn);
        }
        return valor;
    }

    /**
     * H.A. Carga Layout Relacion Gastos Masivo **
     */
    public String validarInformacion(InputStream in, String fAplicacion, String ur, String cEjecicicioFiscal, String login, String mesCorriente, String cContable, String sTipoCarga) throws FileNotFoundException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String sCadena = "";
        String valorReturn = "";
        String descripcion = "";
        Connection conn = null;
        PreparedStatement ps = null, pss = null, psf = null, pssf = null, psEnc = null, psNom = null, psDet = null, psE = null, psUp = null, psEv = null;
        PreparedStatement psImp = null, psImps = null;
        ResultSet rs = null, rsf = null, rsNom = null, rsE = null, rsEv = null, rsImp = null, rsImps = null;
        int mes = Integer.parseInt(mesCorriente);
        // Contadores para trazabilidad
        int nLinea = 0;
        try {
            int fTemp = 0;
            int fTempGral = 0;
            int nDocRenglon = 0;
            String rfc = "", valorRfc = "", cIRelacion = "", concepto = "", estatus = "", destinoGasto = "", CTAB = "", valorCTAB = "";
            String ep = "", tipoMovimiento = "", tipoConcepto = "", validaEP = "", validaEPPG = "";
            String separador = Pattern.quote(".");
            int nFolioCaja = -1;
            double mImporteNetoEnc = 0.00;
            String sIdComision = "", valorComision = "";
            conn = getConnection();
            Map<String, Map<Integer, BigDecimal>> saldos = new LinkedHashMap<String, Map<Integer, BigDecimal>>();
            BigDecimal remanente = new BigDecimal(0.00f).setScale(2, RoundingMode.HALF_UP);
            BigDecimal remanenteRetencion = new BigDecimal(0.00f).setScale(2, RoundingMode.HALF_UP);
            BigDecimal acumulado = new BigDecimal(0.00f).setScale(2, RoundingMode.HALF_UP);
            BigDecimal importeNetoDet = new BigDecimal(0.00f).setScale(2, RoundingMode.HALF_UP);
            BigDecimal importeTotal = new BigDecimal(0.00f).setScale(2, RoundingMode.HALF_UP);
            BigDecimal importeRetenciones = new BigDecimal(0.00f).setScale(2, RoundingMode.HALF_UP);
            BigDecimal importeISROtros = new BigDecimal(0.00f).setScale(2, RoundingMode.HALF_UP);
            while ((sCadena = br.readLine()) != null) {
                nLinea++;
                estatus = "";
                CTAB = "";
                rfc = "";
                valorCTAB = "";
                // Log de la línea completa (para rastrear layout)
                log.debug("Object: {}", "validarInformacion - linea[" + nLinea + "]: " + sCadena);
                String[] celdas = sCadena.split(",");
                if (celdas.length > 0) {
                    celdas[0] = celdas[0].replace("\uFEFF", "").trim();
                }
                boolean esHeader = (celdas.length > 0 && "H".equalsIgnoreCase(celdas[0].trim()));
                if (esHeader) {
                    // =========================
                    // HEADER
                    // =========================
                    nDocRenglon = 0;
                    importeTotal = new BigDecimal(0.00f).setScale(2, RoundingMode.HALF_UP);
                    // Lee campos (con logs por columna)
                    cIRelacion = (celdas.length > 1 ? celdas[1].trim() : "");
                    log.debug("Object: {}", "H linea[" + nLinea + "] col[2] Relacion=[" + cIRelacion + "]");
                    rfc = (celdas.length > 2 ? celdas[2].trim() : "");
                    log.debug("Object: {}", "H linea[" + nLinea + "] col[3] RFC=[" + rfc + "]");
                    concepto = (celdas.length > 3 ? celdas[3].trim() : "");
                    log.debug("Object: {}", "H linea[" + nLinea + "] col[4] Concepto=[" + concepto + "]");
                    String mImporteNetoEnct = (celdas.length > 4 ? celdas[4].trim() : "");
                    log.debug("Object: {}", "H linea[" + nLinea + "] col[5] ImporteEnc=[" + mImporteNetoEnct + "]");
                    destinoGasto = (celdas.length > 5 ? celdas[5].trim() : "");
                    log.debug("Object: {}", "H linea[" + nLinea + "] col[6] Destino=[" + destinoGasto + "]");
                    estatus = (celdas.length > 6 ? celdas[6].trim() : "") + " : ";
                    log.debug("Object: {}", "H linea[" + nLinea + "] col[7] EstatusBase=[" + (celdas.length > 6 ? celdas[6].trim() : "") + "]");
                    CTAB = (celdas.length > 7 ? celdas[7].trim() : "");
                    log.debug("Object: {}", "H linea[" + nLinea + "] col[8] CTAB=[" + CTAB + "]");
                    // Valida RFC
                    valorRfc = validaRfc(conn, ps, rs, rfc);
                    log.debug("Object: {}", "H linea[" + nLinea + "] validaRfc RFC=[" + rfc + "] => [" + valorRfc + "]");
                    // Valida CTAB (con log y mensaje con datos)
                    if ("LAUD000000000".equals(rfc)) {
                        valorCTAB = "existe";
                        log.debug("Object: {}", "H linea[" + nLinea + "] RFC es LAUD000000000 => CTAB se marca existe (col[8])");
                    } else {
                        log.debug("Object: {}", "H linea[" + nLinea + "] validaCTAB buscando RFC=[" + rfc + "] CTAB=[" + CTAB + "] (col[3], col[8])");
                        valorCTAB = validaCTAB(conn, ps, rs, rfc, CTAB);
                        log.debug("Object: {}", "H linea[" + nLinea + "] validaCTAB RFC=[" + rfc + "] CTAB=[" + CTAB + "] => [" + valorCTAB + "]");
                    }
                    // Validaciones de campos vacíos con columna
                    if ("".equals(cIRelacion)) {
                        estatus = estatus + " Campo Relacion[COL 2] esta vacio - ";
                        descripcion = "Detalle";
                    }
                    if ("".equals(concepto)) {
                        estatus = estatus + " Campo Concepto[COL 4] esta vacio - ";
                        descripcion = "Detalle";
                    }
                    if (!"CECE".equals(destinoGasto)) {
                        if ("".equals(CTAB)) {
                            estatus = estatus + " Campo Cuenta Bancaria[COL 8] esta vacio - ";
                            descripcion = "Detalle";
                        }
                    }
                    // Campos especiales por tipo de carga
                    if ("3".equals(sTipoCarga)) {
                        sIdComision = (celdas.length > 8 ? celdas[8].trim() : "");
                        valorComision = (celdas.length > 9 ? celdas[9].trim() : "");
                        log.debug("Object: {}", "H linea[" + nLinea + "] col[9] IdComision=[" + sIdComision + "], col[10] InformeComision=[" + valorComision + "]");
                        if ("".equals(sIdComision)) {
                            estatus = estatus + " Campo Comision[COL 9] esta vacio - ";
                            descripcion = "Detalle";
                        } else {
                            String tmp = validaComision(conn, ps, rs, sIdComision);
                            log.debug("Object: {}", "H linea[" + nLinea + "] validaComision nIdComision=[" + sIdComision + "] => [" + tmp + "]");
                            sIdComision = tmp;
                        }
                        if ("".equals(valorComision)) {
                            estatus = estatus + " Campo Informe Comision[COL 10] esta vacio - ";
                            descripcion = "Detalle";
                        }
                    } else if (RelacionGastosManager.CARGA_MASIVA_LAUDOS.equals(sTipoCarga)) {
                        // Folio caja col 9
                        String rawFolioCaja = (celdas.length > 8 ? StringUtils.trimToEmpty(celdas[8]) : "");
                        log.debug("Object: {}", "H linea[" + nLinea + "] col[9] FolioCaja(raw)=[" + rawFolioCaja + "]");
                        try {
                            nFolioCaja = Integer.parseInt(rawFolioCaja);
                        } catch (Exception e) {
                            estatus = estatus + " No se recibio folio de caja o el folio de caja[" + rawFolioCaja + "] no es numerico - (COL 9) - ";
                            descripcion = "Detalle";
                        }
                        if (!"CECE".equals(destinoGasto)) {
                            String rawRet = (celdas.length > 9 ? StringUtils.trimToEmpty(celdas[9]) : "");
                            log.debug("Object: {}", "H linea[" + nLinea + "] col[10] Retenciones(raw)=[" + rawRet + "]");
                            try {
                                importeRetenciones = new BigDecimal(rawRet).setScale(2, RoundingMode.HALF_UP);
                            } catch (Exception e) {
                                estatus = estatus + " No se recibio importe de retenciones o el dato[" + rawRet + "] no es numerico - (COL 10) - ";
                                descripcion = "Detalle";
                            }
                        }
                    } else if ("6".equals(sTipoCarga) && !"CSGE".equals(destinoGasto)) {
                        String rawRet = (celdas.length > 9 ? StringUtils.trimToEmpty(celdas[9]) : "");
                        log.debug("Object: {}", "H linea[" + nLinea + "] col[10] Retenciones(raw)=[" + rawRet + "]");
                        try {
                            importeRetenciones = new BigDecimal(rawRet).setScale(2, RoundingMode.HALF_UP);
                        } catch (Exception e) {
                            estatus = estatus + " No se recibio importe de retenciones o el dato[" + rawRet + "] no es numerico - (COL 10) - ";
                            descripcion = "Detalle";
                        }
                    }
                    // Importe Encabezado numérico (con valor y columna)
                    try {
                        mImporteNetoEnc = Double.parseDouble(mImporteNetoEnct);
                        if ("CPRP".equals(destinoGasto) && mImporteNetoEnc > 10000.00) {
                            estatus = estatus + " El Importe Enc[" + mImporteNetoEnct + "] No Debe Superar $10,000.00 Para Destino CPRP - (COL 5) - ";
                            descripcion = "Detalle";
                        }
                    } catch (Exception num) {
                        log.warn("H linea[" + nLinea + "] ImporteEnc NO numerico col[5] valor=[" + mImporteNetoEnct + "]", num);
                        estatus = estatus + " Campo Importe Enc[" + mImporteNetoEnct + "] No es Numerico - (COL 5) - ";
                        descripcion = "Detalle";
                    }
                    // RFC y CTAB con datos en estatus y log
                    if ("noExiste".equals(valorRfc)) {
                        estatus = estatus + " RFC[" + rfc + "] No Existe o tipo persona invalido - (COL 3) - ";
                        descripcion = "Detalle";
                    } else if (!"CECE".equals(destinoGasto)) {
                        if ("noExiste".equals(valorCTAB)) {
                            log.warn("Object: {}", "H linea[" + nLinea + "] CUENTA BANCARIA NO EXISTE buscando RFC=[" + rfc + "] CTAB=[" + CTAB + "]");
                            estatus = estatus + " CUENTA BANCARIA No Existe (RFC[" + rfc + "] CTAB[" + CTAB + "]) - (COL 3,8) - ";
                            descripcion = "Detalle";
                        }
                    }
                    // Validación comision/caja
                    if ("3".equals(sTipoCarga)) {
                        if ("noExiste".equals(sIdComision)) {
                            estatus = estatus + " Comision[" + (celdas.length > 8 ? celdas[8].trim() : "") + "] No Existe - (COL 9) - ";
                            descripcion = "Detalle";
                        }
                    } else if (RelacionGastosManager.CARGA_MASIVA_LAUDOS.equals(sTipoCarga)) {
                        if (!CajaManager.existeSolicitud(conn, nFolioCaja)) {
                            estatus = estatus + " La solicitud de caja con folio[" + nFolioCaja + "] No Existe - (COL 9) - ";
                            descripcion = "Detalle";
                        }
                    }
                    // Folio temp
                    fTemp = folioTemp(conn, psf, pssf, rsf);
                    if (fTempGral == 0) {
                        fTempGral = fTemp;
                    }
                    // Insert Enc
                    insertaEncabezadoRelacionGastosTemp(conn, psEnc, psNom, rsNom, fTemp, fAplicacion, ur, cEjecicicioFiscal, cIRelacion, rfc, concepto, mImporteNetoEnc, login, destinoGasto, mesCorriente, estatus, fTempGral, cContable, CTAB, sTipoCarga, sIdComision, valorComision, nFolioCaja, importeRetenciones.floatValue());
                    remanente = new BigDecimal(0.00f).setScale(2, RoundingMode.HALF_UP);
                    mes = Integer.parseInt(mesCorriente);
                } else {
                    // =========================
                    // DETALLE
                    // =========================
                    int l = celdas.length;
                    ep = (l > 0 ? celdas[0].trim() : "");
                    log.debug("Object: {}", "D linea[" + nLinea + "] col[1] EP=[" + ep + "]");
                    String rawImporteDet = (l > 1 ? celdas[1].trim() : "");
                    log.debug("Object: {}", "D linea[" + nLinea + "] col[2] ImporteDet=[" + rawImporteDet + "]");
                    tipoMovimiento = (l > 2 ? celdas[2].trim() : "");
                    log.debug("Object: {}", "D linea[" + nLinea + "] col[3] TipoMovimiento=[" + tipoMovimiento + "]");
                    tipoConcepto = (l > 3 ? celdas[3].trim() : "");
                    log.debug("Object: {}", "D linea[" + nLinea + "] col[4] TipoConcepto=[" + tipoConcepto + "]");
                    String nFolioSolicitudRadicado = (l > 4 ? celdas[4].trim() : "");
                    if (StringUtils.isNotBlank(nFolioSolicitudRadicado)) {
                        log.debug("Object: {}", "D linea[" + nLinea + "] col[5] FolioRadicado=[" + nFolioSolicitudRadicado + "]");
                    }
                    // ISR / Retenciones
                    importeRetenciones = new BigDecimal(0.00f).setScale(2, RoundingMode.HALF_UP);
                    importeISROtros = new BigDecimal(0.00f).setScale(2, RoundingMode.HALF_UP);
                    if (l >= 4) {
                        String rawIsr = (l > 5 ? celdas[5].trim() : "");
                        String rawRet = (l > 6 ? celdas[6].trim() : "");
                        if (StringUtils.isNotBlank(rawIsr)) {
                            log.debug("Object: {}", "D linea[" + nLinea + "] col[6] ISR/Otros=[" + rawIsr + "]");
                            try {
                                importeISROtros = new BigDecimal(rawIsr).setScale(2, RoundingMode.HALF_UP);
                            } catch (Exception ex) {
                                estatus = estatus + " Campo ISR/Otros[" + rawIsr + "] No es Numerico - (COL 6) - ";
                                descripcion = "Detalle";
                            }
                        } else if (StringUtils.isNotBlank(rawRet)) {
                            log.debug("Object: {}", "D linea[" + nLinea + "] col[7] Retenciones=[" + rawRet + "]");
                            try {
                                importeRetenciones = new BigDecimal(rawRet).setScale(2, RoundingMode.HALF_UP);
                            } catch (Exception ex) {
                                estatus = estatus + " Campo Retenciones[" + rawRet + "] No es Numerico - (COL 7) - ";
                                descripcion = "Detalle";
                            }
                        }
                    }
                    nDocRenglon = nDocRenglon + 1;
                    String num = ep.substring(31, 32);
                    String obgt = ep.substring(31, 36);
                    int capitulo = 0;
                    if ("1".equals(num))
                        capitulo = 1000;
                    if ("2".equals(num))
                        capitulo = 2000;
                    if ("3".equals(num))
                        capitulo = 3000;
                    if ("4".equals(num))
                        capitulo = 4000;
                    if (RelacionGastosManager.CARGA_MASIVA_LAUDOS.equals(sTipoCarga)) {
                        importeISROtros = importeRetenciones;
                    }
                    // Validar EP (con log)
                    validaEP = validarEP(conn, psE, rsE, ep);
                    log.debug("Object: {}", "D linea[" + nLinea + "] validarEP EP=[" + ep + "] => [" + validaEP + "]");
                    if ("noExiste".equals(validaEP)) {
                        estatus = estatus + " No Existe EP[" + ep + "] - (COL 1) - ";
                        descripcion = "Detalle";
                    } else if ("2".equals(sTipoCarga)) {
                        // validaEPPG
                        String[] parts = ep.split(separador);
                        String PG = (parts.length > 6 ? parts[6] : "");
                        log.debug("Object: {}", "D linea[" + nLinea + "] validaEPPG PG=[" + PG + "] (derivado de EP col[1])");
                        if (!"04".equals(PG)) {
                            validaEPPG = "noExiste";
                            estatus = estatus + "La EP[" + ep + "] no contiene Programa Generar 04 (PG=[" + PG + "]) - (COL 1) - ";
                            descripcion = "Detalle";
                        }
                    }
                    // Buscar evento: log + mensaje con datos buscados
                    log.debug("Object: {}", "D linea[" + nLinea + "] validaEvento buscando Destino=[" + destinoGasto + "](col H6) Concepto=[" + tipoConcepto + "](col D4) EP=[" + ep + "](col D1) OBGT=[" + obgt + "] CTGA(substring38,1)=[" + ep.substring(37, 38) + "] FF(substring40,1)=[" + ep.substring(39, 40) + "]");
                    String validaEvento = validaEvento(conn, psEv, rsEv, tipoConcepto, destinoGasto, ep);
                    log.debug("Object: {}", "D linea[" + nLinea + "] validaEvento => [" + validaEvento + "]");
                    if ("noExiste".equals(validaEvento)) {
                        estatus = estatus + " No Existe Evento (Destino[" + destinoGasto + "] Concepto[" + tipoConcepto + "] EP[" + ep + "]) - (COL H6, D4, D1) - ";
                        descripcion = "Detalle";
                    }
                    // Importe detalle numérico (con valor y columna)
                    try {
                        importeNetoDet = new BigDecimal(rawImporteDet).setScale(2, RoundingMode.HALF_UP);
                        importeTotal = importeTotal.add(importeNetoDet).add(importeISROtros).setScale(2, RoundingMode.HALF_UP);
                    } catch (Exception e) {
                        log.warn("D linea[" + nLinea + "] ImporteDet NO numerico col[2] valor=[" + rawImporteDet + "]", e);
                        estatus = estatus + " Campo Importe Detalle[" + rawImporteDet + "] No es Numerico - (COL 2) - ";
                        descripcion = "Detalle";
                        continue;
                    }
                    // Validar Partida
                    if ("1".equals(sTipoCarga)) {
                        if ("37".equals(ep.substring(31, 33))) {
                            estatus = estatus + " La partida[" + ep.substring(31, 36) + "] no es valida para el tipo de Carga - (derivado EP COL 1) - ";
                            descripcion = "Detalle";
                        }
                    }
                    if ("2".equals(sTipoCarga) || "3".equals(sTipoCarga) || "7".equals(sTipoCarga)) {
                        if (!"37".equals(ep.substring(31, 33)) && destinoGasto.equals("CSGE") && destinoGasto.equals("CEGE")) {
                            estatus = estatus + " La partida[" + ep.substring(31, 36) + "] no es valida para el tipo de Carga - (derivado EP COL 1) - ";
                            descripcion = "Detalle";
                        }
                    }
                    // Guardar detalle / saldos (flujo original)
                    if ("".equals(nFolioSolicitudRadicado)) {
                        int ff = Integer.parseInt(ep.substring(39, 40));
                        boolean esIngresoPropio = (ff == 4);
                        if (ff == 4)
                            mes = 1;
                        Map<Integer, BigDecimal> saldoEP = saldos.get(ep);
                        if (saldoEP == null) {
                            saldoEP = obtenerSaldoEP(conn, ep);
                            saldos.put(ep, saldoEP);
                        }
                        BigDecimal saldoEPMes = saldoEP.get(mes);
                        if (saldoEPMes.compareTo(Util.ZERO) == 0) {
                            mes = obtenerMesConSaldo(saldoEP, mes, esIngresoPropio);
                            if (mes == 0)
                                throw new Exception("No hay saldo suficiente en la EP");
                            saldoEPMes = saldoEP.get(mes);
                        }
                        remanente = importeTotal;
                        remanenteRetencion = importeISROtros;
                        do {
                            BigDecimal factorRetencion = importeISROtros.divide(importeTotal, RoundingMode.HALF_UP);
                            if (saldoEPMes.compareTo(remanente) < 0) {
                                acumulado = acumulado.add(saldoEPMes);
                                saldoEP.put(mes, new BigDecimal(0.00f));
                                remanente = remanente.subtract(saldoEPMes).setScale(2, RoundingMode.HALF_UP);
                                BigDecimal importeRetencionRenglon = saldoEPMes.multiply(factorRetencion).setScale(2, RoundingMode.HALF_UP);
                                BigDecimal importeNeto = saldoEPMes.subtract(importeRetencionRenglon).setScale(2, RoundingMode.HALF_UP);
                                remanenteRetencion = remanenteRetencion.subtract(importeRetencionRenglon).setScale(2, RoundingMode.HALF_UP);
                                if (RelacionGastosManager.CARGA_MASIVA_LAUDOS.equals(sTipoCarga))
                                    insertaDetalleRelacionGastosTemp(conn, psDet, fTemp, nDocRenglon, mes, cEjecicicioFiscal, cIRelacion, ep, importeNeto, tipoMovimiento, tipoConcepto, validaEvento, rfc, capitulo, obgt, fTempGral, cContable, new BigDecimal(0.0f), importeRetencionRenglon, saldoEPMes);
                                else
                                    insertaDetalleRelacionGastosTemp(conn, psDet, fTemp, nDocRenglon, mes, cEjecicicioFiscal, cIRelacion, ep, importeNeto, tipoMovimiento, tipoConcepto, validaEvento, rfc, capitulo, obgt, fTempGral, cContable, importeRetencionRenglon, new BigDecimal(0.0f), saldoEPMes);
                                mes = obtenerMesConSaldo(saldoEP, mes, esIngresoPropio);
                                if (mes == 0)
                                    throw new Exception("No hay saldo suficiente en la EP");
                                saldoEPMes = saldoEP.get(mes);
                            } else {
                                acumulado = acumulado.add(remanente).setScale(2, RoundingMode.HALF_UP);
                                saldoEPMes = saldoEPMes.subtract(remanente).setScale(2, RoundingMode.HALF_UP);
                                saldoEP.put(mes, saldoEPMes);
                                BigDecimal importeNeto = remanente.subtract(remanenteRetencion).setScale(2, RoundingMode.HALF_UP);
                                if (RelacionGastosManager.CARGA_MASIVA_LAUDOS.equals(sTipoCarga))
                                    insertaDetalleRelacionGastosTemp(conn, psDet, fTemp, nDocRenglon, mes, cEjecicicioFiscal, cIRelacion, ep, importeNeto, tipoMovimiento, tipoConcepto, validaEvento, rfc, capitulo, obgt, fTempGral, cContable, new BigDecimal(0.0f), remanenteRetencion, remanente);
                                else
                                    insertaDetalleRelacionGastosTemp(conn, psDet, fTemp, nDocRenglon, mes, cEjecicicioFiscal, cIRelacion, ep, importeNeto, tipoMovimiento, tipoConcepto, validaEvento, rfc, capitulo, obgt, fTempGral, cContable, remanenteRetencion, new BigDecimal(0.0f), remanente);
                                remanente = new BigDecimal(0.00f).setScale(2, RoundingMode.HALF_UP);
                            }
                            nDocRenglon++;
                        } while (remanente.compareTo(Util.ZERO) != 0);
                    }
                    if ("noExiste".equals(validaEP) || "noExiste".equals(validaEvento) || "noExiste".equals(validaEPPG)) {
                        actualizaEstatus(conn, psUp, estatus, fTemp);
                        descripcion = "Detalle";
                    }
                }
            }
            String descripcionImp = importeEncDet(conn, psImp, rsImp, psImps, psUp, rsImps, fTempGral);
            if ("Detalle".equals(descripcionImp)) {
                descripcion = descripcionImp;
            }
            boolean cargaConError = StringUtils.isNotBlank(descripcion);
            CargaMasivaRG cargaMasivaRG = new CargaMasivaRG(fTempGral, (cargaConError) ? CargaMasivaRG.CARGA_ERROR : CargaMasivaRG.CARGA_EXITOSA);
            RelacionGastosManager.saveCargaMasivaRG(conn, cargaMasivaRG);
            conn.commit();
            valorReturn = String.valueOf(descripcion + ":" + fTempGral);
        } catch (Exception se) {
            log.error("Error: " + se, se);
            valorReturn = "Error:Archivo" + se;
            try {
                conn.rollback();
            } catch (Exception e) {
                log.error("Error occurred", "Error al hacer Rollback: " + se);
            }
        } finally {
            CloseObject.closeObject(ps);
            CloseObject.closeObject(pss);
            CloseObject.closeObject(psf);
            CloseObject.closeObject(pssf);
            CloseObject.closeObject(psEnc);
            CloseObject.closeObject(psNom);
            CloseObject.closeObject(psDet);
            CloseObject.closeObject(psUp);
            CloseObject.closeObject(psEv);
            CloseObject.closeObject(psE);
            CloseObject.closeObject(psImp);
            CloseObject.closeObject(psImps);
            CloseObject.closeObject(rs);
            CloseObject.closeObject(rsf);
            CloseObject.closeObject(rsNom);
            CloseObject.closeObject(rsE);
            CloseObject.closeObject(rsEv);
            CloseObject.closeObject(rsImp);
            CloseObject.closeObject(rsImps);
            CloseObject.closeObject(conn);
            try {
                if (br != null)
                    br.close();
            } catch (Exception exc) {
                log.warn("Cerrando BufferedReader", exc);
            }
            br = null;
        }
        return valorReturn;
    }

    public String importeEncDet(Connection conn, PreparedStatement psImp, ResultSet rsImp, PreparedStatement psImps, PreparedStatement psUp, ResultSet rsImps, int fTemp) throws SQLException {
        String valor = "";
        Double importeEnc = 0.00;
        Double importeDet = 0.00;
        int folioRelGas = 0;
        String estatus = "";
        try {
            psImp = conn.prepareStatement("SELECT nFolioRELACIONGASTOS " + " FROM tRELACIONGASTOSEncabezado_temp WITH(NOLOCK) WHERE folioTempGral = ? ");
            psImp.setInt(1, fTemp);
            rsImp = psImp.executeQuery();
            while (rsImp.next()) {
                folioRelGas = rsImp.getInt("nFolioRELACIONGASTOS");
                importeDet = 0.00;
                psImps = conn.prepareStatement("SELECT estatus, tRE.mImporteBruto AS importeNetoEnc, tRD.mImporteBruto AS importeNetoDet " + " FROM tRELACIONGASTOSEncabezado_temp tRE INNER JOIN tRELACIONGASTOSDetalle_temp tRD ON tRE.nFolioRELACIONGASTOS = tRD.nFolioRELACIONGASTOS " + " WHERE tRE.nFolioRELACIONGASTOS = ? ");
                psImps.setInt(1, folioRelGas);
                rsImps = psImps.executeQuery();
                while (rsImps.next()) {
                    importeEnc = rsImps.getDouble("importeNetoEnc");
                    importeDet = importeDet + rsImps.getDouble("importeNetoDet");
                    estatus = rsImps.getString("estatus");
                }
                // Si son diferente Actualiza Estatus
                NumberFormat formatter = new DecimalFormat("###.##");
                importeEnc = Double.parseDouble(formatter.format(importeEnc));
                importeDet = Double.parseDouble(formatter.format(importeDet));
                if (importeEnc > importeDet || importeEnc < importeDet) {
                    estatus = estatus + " Importe de Encabezado[" + importeEnc + "] es Diferente a Suma de Detalles [" + importeDet + "]";
                    actualizaEstatus(conn, psUp, estatus, folioRelGas);
                    valor = "Detalle";
                }
            }
        } catch (Exception e) {
            log.error("Error occurred", "Error: " + e);
            valor = "Error:Archivo";
        }
        return valor;
    }

    public int folioTemp(Connection conn, PreparedStatement ps, PreparedStatement pss, ResultSet rs) throws SQLException {
        int folio = 0;
        try {
            ps = conn.prepareStatement("UPDATE CF_SEQUENCE  WITH (ROWLOCK) SET seq_value = seq_value + 1  WHERE seq_name = 'RELACIONGASTOS_TEMP' ");
            ps.executeUpdate();
            pss = conn.prepareStatement("SELECT seq_value FROM CF_SEQUENCE WITH(NOLOCK) WHERE seq_name = 'RELACIONGASTOS_TEMP' ");
            rs = pss.executeQuery();
            if (rs.next()) {
                folio = Integer.parseInt(rs.getString("seq_value"), 10);
            }
        } catch (Exception e) {
            log.error("Error occurred", "Error: " + e);
            e.printStackTrace();
            folio = 0;
        }
        return folio;
    }

    public String validaRfc(Connection conn, PreparedStatement ps, ResultSet rs, String rfc) throws SQLException {
        String valor = "noExiste";
        try {
            ps = conn.prepareStatement("Select * from tBeneficiario WITH (NOLOCK) where dRFC = ? AND cIdTipoPersonaRFC IN (3,2) ");
            ps.setString(1, rfc);
            rs = ps.executeQuery();
            if (rs.next()) {
                valor = "existe";
            }
        } catch (Exception e) {
            log.error("Error occurred", "Error al Validar RFC : " + e);
        }
        return valor;
    }

    public Map<Integer, BigDecimal> obtenerSaldoEP(Connection conn, String ep) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "SELECT	CONVERT( INT, SUBSTRING(nCuenta, 7,5 ) ) AS Mes, " + "		mSaldoArrastre AS saldo " + "  FROM	tsaldos with (nolock) WHERE nCuenta LIKE '82106-%' " + "   AND	cSubCuenta = ? " + "ORDER BY CONVERT( INT, SUBSTRING(nCuenta, 7,5 ) )";
        Map<Integer, BigDecimal> saldos = new LinkedHashMap<Integer, BigDecimal>();
        for (int i = 1; i <= 12; i++) saldos.put(i, new BigDecimal(0.0f));
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, ep);
            log.debug("Object: {}", query + "[" + ep + "]");
            rs = ps.executeQuery();
            while (rs.next()) {
                int mes = rs.getInt("Mes");
                saldos.put(mes, rs.getBigDecimal("saldo"));
            }
            return saldos;
        } finally {
            CloseObject.closeObject(ps);
            CloseObject.closeObject(rs);
        }
    }

    public double obtenerSaldoRadicado(Connection conn, String ep, String mes, int nFolioSolicitud) throws Exception {
        double saldoEPMes = 0;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement("SELECT mImporte " + "FROM   vSaldosRadicadoxMes  " + "WHERE  Folio = ?  " + "       AND EP = ?  " + "       AND nMes = ?  ");
            ps.setInt(1, nFolioSolicitud);
            ps.setString(2, ep);
            ps.setString(3, mes);
            log.debug("Object: {}", ps.toString());
            log.debug("Object: {}", "[" + nFolioSolicitud + "," + ep + "," + mes + "]");
            rs = ps.executeQuery();
            if (rs.next())
                saldoEPMes = rs.getDouble(1);
            return saldoEPMes;
        } finally {
            CloseObject.closeObject(ps);
            CloseObject.closeObject(rs);
        }
    }

    public void updateRadicado(Connection conn, int fTempGral, String ep, String mes, int nFolioSolicitud) throws Exception {
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement("UPDATE tRELACIONGASTOSEncabezado_temp " + "SET    cRadicado = 'S'  " + "WHERE  folioTempGral = ?  ");
            ps.setInt(1, fTempGral);
            log.debug("Object: {}", ps.toString());
            log.debug("Object: {}", "[" + fTempGral + "]");
            ps.executeUpdate();
            ps = conn.prepareStatement("UPDATE tRELACIONGASTOSDetalle_temp " + "SET    nFolioSolicitud = ? " + "WHERE  folioTempGral = ? " + "       AND nMes = ? " + "       AND EP =?");
            ps.setInt(1, nFolioSolicitud);
            ps.setInt(2, fTempGral);
            ps.setString(3, mes);
            ps.setString(4, ep);
            log.debug("Object: {}", ps.executeUpdate());
        } finally {
            CloseObject.closeObject(ps);
        }
    }

    public int obtenerMesConSaldo(Map<Integer, BigDecimal> saldoEp, int mes, boolean esIngresoPropio) {
        int nMes = 0;
        boolean continuar = true;
        int i = mes;
        while (continuar) {
            if (saldoEp.get(i).compareTo(Util.ZERO) != 0) {
                nMes = i;
                continuar = false;
            } else {
                i = (esIngresoPropio ? i + 1 : i - 1);
                if (i < 1 || i > 12)
                    continuar = false;
            }
        }
        return nMes;
    }

    public String obtenerMesConSaldoRadicado(Connection conn, String ep, String mes, int nFolioSolicitud) throws Exception {
        String nmes = "";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement("SELECT TOP(1) nMes " + "FROM   dbo.vSaldosRadicadoxMes " + "WHERE  Folio = ? " + "       AND nmes <? " + "		AND EP =? " + "ORDER  BY nMes DESC   ");
            ps.setInt(1, nFolioSolicitud);
            ps.setString(2, mes);
            ps.setString(3, ep);
            log.debug("Object: {}", ps.toString());
            log.debug("Object: {}", "[" + nFolioSolicitud + "," + ep + "," + mes + "]");
            rs = ps.executeQuery();
            if (rs.next())
                nmes = rs.getString(1);
            if (nmes.isEmpty())
                nmes = "0";
            return nmes;
        } finally {
            CloseObject.closeObject(ps);
            CloseObject.closeObject(rs);
        }
    }

    public String validarEP(Connection conn, PreparedStatement psE, ResultSet rsE, String ep) throws SQLException {
        String valor = "noExiste";
        try {
            psE = conn.prepareStatement("SELECT * FROM tCatalogoEP WITH (NOLOCK) WHERE EP = ?");
            psE.setString(1, ep);
            rsE = psE.executeQuery();
            if (rsE.next()) {
                valor = "existe";
            }
        } catch (Exception e) {
            log.error("Error occurred", "Error al Validar EP " + e);
        }
        return valor;
    }

    public String validaEvento(Connection conn, PreparedStatement psEv, ResultSet rsEv, String concepto, String destino, String ep) throws SQLException {
        String valor = "noExiste";
        try {
            psEv = conn.prepareStatement("SELECT cevto cEvento FROM tEventoConcepto  WITH(NOLOCK) " + " WHERE cobgini = substring(?, 32,5) AND ctconc = ltrim(rtrim(?)) AND " + " cCTGA = substring(?, 38,1) AND ID_DESTINO_GASTO = ? AND cFuenteFinanciamiento = SUBSTRING(?,40,1) ");
            psEv.setString(1, ep);
            psEv.setString(2, concepto);
            psEv.setString(3, ep);
            psEv.setString(4, destino);
            psEv.setString(5, ep);
            rsEv = psEv.executeQuery();
            if (rsEv.next()) {
                valor = "APD_" + rsEv.getString("cEvento");
            }
        } catch (Exception e) {
            log.error("Error occurred", "Error al Validar EP " + e);
        }
        return valor;
    }

    public String actualizaEstatus(Connection conn, PreparedStatement psUp, String Estatus, int nfolio) throws SQLException {
        String valor = "noActualizado";
        try {
            psUp = conn.prepareStatement("UPDATE tRELACIONGASTOSEncabezado_Temp SET Estatus = SUBSTRING('" + Estatus + "',0,300) WHERE nFolioRELACIONGASTOS = ? ");
            psUp.setInt(1, nfolio);
            psUp.executeUpdate();
            valor = "actualizado";
        } catch (Exception e) {
            log.error("Error occurred", "Error al Actualizar Estatus " + e);
        }
        return valor;
    }

    public String insertaEncabezadoRelacionGastosTemp(Connection conn, PreparedStatement psEnc, PreparedStatement psNom, ResultSet rsNom, int fTemp, String fAplicacion, String ur, String cEjecicicioFiscal, String cIRelacion, String rfc, String concepto, double mImporteNetoEnc, String login, String destinoGasto, String mes, String estatus, int fTempGral, String cContable, String CTAB, String sTipoCarga, String sIdComision, String cInformeComision, int nFolioCaja, float importeRetenciones) throws SQLException {
        String valor = "noGuardado";
        String cNombre = "";
        String sInformeComision = "";
        String tipoPoliza = "";
        try {
            psNom = conn.prepareStatement("SELECT cnombre FROM v_PCATALOGORFC where cIDRFC = ?");
            psNom.setString(1, rfc);
            rsNom = psNom.executeQuery();
            if (rsNom.next()) {
                cNombre = rsNom.getString("cnombre");
            }
            if ("2".equals(sTipoCarga)) {
                sInformeComision = "Pago por concepto de gastos de alimentos por día laborado, a los brigadistas que se encuentren desarrollando tareas de combate, incluyendo los días de traslado al sitio de combate y los días de retorno del combate a su centro de trabajo habitual";
            } else if ("3".equals(sTipoCarga)) {
                sInformeComision = cInformeComision;
            } else if ("7".equals(sTipoCarga)) {
                sInformeComision = "Pago por concepto de gastos de alimentación a participantes de los juegos deportivos de la SEMARNAT";
            }
            if ("".equals(sIdComision)) {
                sIdComision = "NULL";
            }
            /*
			 * if ("6".equals(sTipoCarga)) { tipoPoliza = "DI"; } else
			 */
            tipoPoliza = "EG";
            psEnc = conn.prepareStatement(" INSERT INTO tRELACIONGASTOSEncabezado_Temp " + "	(nFolioRELACIONGASTOS,  " + "  fAplicacion,  " + "  cRamo, " + "  cUnidadResponsable, " + "  cEjercicio, " + "  cIdEntidadContable, " + "  cIdRelacion, " + "  cIdTipoDocumento, " + "  cIdTipoRelacion, " + "  cIdTipoMontoDesembolso, " + "  cIdRFC, " + "  fRecepcion," + "  fRevision," + "  fProgramadaPago," + "  cConcepto," + "  mImporteNeto," + "  cIdUnidadAdministrativa, cIdGRegional, cIdGEstatal, cIdDistritoRiego, cIdTipoFondo, caNoAP, cIdEstadoRelacion, lContrarreciboImpreso, nIdConcepto, cIdTipoLimiteDlls, cIdUsuarioCaptura, cIdUsuarioImpresion, cIdUsuarioRevision, cIdUsuarioAprobacion, cIdUsuarioRechazo, lSuficienciaAnualValidada, lSuficienciaMensualValidada, ID_DESTINO_GASTO, ID_TIPO_MOVIMIENTO, ID_TIPO_CONCEPTO, cnombre, TIPO_OPERACION,    cEvento,     aEjercicioFiscal,   cCentroContable, cMes, RFC,          mImporteBruto,        mImporteMasIva,   cTipoPoliza, nEnviadoSICOP, cUnidadResponsableContable, cDescripcionPoliza, estatus, folioTempGral, CTAB, nTipoCarga, cInformeComision, nIdComision, nFolioCaja ) " + " VALUES( " + fTemp + ", '" + fAplicacion + "', 16,      '" + ur + "',    '" + cEjecicicioFiscal + "' ,  '" + cContable + "' ,  '" + cIRelacion + "',   " + "3 ,             " + "'T',           " + "0,                " + "'" + rfc + "' ,'" + fAplicacion + "','" + fAplicacion + "','" + fAplicacion + "', '" + concepto + "', " + (mImporteNetoEnc - importeRetenciones) + " ,  000, 					00,            00,         00,               0 ,          00,      0,                    0,                  0,             0,                 '" + login + "' ,      0,                    0,                  0,                 0,                      0,                      0,                 		'" + destinoGasto + "',       0,					0,       '" + cNombre + "',1,          'CD_AL01','" + cEjecicicioFiscal + "','" + cContable + "','" + mes + "','" + rfc + "','" + mImporteNetoEnc + "','" + mImporteNetoEnc + "', '" + tipoPoliza + "' ,       0,                   'RHQ',                '" + concepto + "',  '" + estatus + "', " + fTempGral + ", '" + CTAB + "', " + sTipoCarga + ", '" + sInformeComision + "', " + sIdComision + ", " + (nFolioCaja > 0 ? String.valueOf(nFolioCaja) : "NULL") + ") ");
            psEnc.executeUpdate();
            valor = "guardado";
        } catch (Exception e) {
            log.error("Error occurred", "Error Al Guardar Encabezado Relacion Gastos " + e);
        }
        return valor;
    }

    public String insertaDetalleRelacionGastosTemp(Connection conn, PreparedStatement psEnc, int fTemp, int nDocRenglon, int mes, String cEjercicioFiscal, String cIdRelacion, String EP, BigDecimal importeNetoDet, String tipoMovimiento, String tipoConcepto, String cEvento, String rfc, int nCapitulo, String obgt, int fTempGral, String cContable, BigDecimal mimporteISROtros, BigDecimal mimporteISRLaudos, BigDecimal sumImporteNetoDet) throws SQLException {
        String valor = "noGuardado";
        String query = "INSERT INTO tRELACIONGASTOSDetalle_Temp( nFolioRELACIONGASTOS, nDocRenglon, nMes, cEjercicio,  cIdCuentaContable, cIdEntidadContable,  cIdRelacion, EP, mComprometido, " + "nPoliza, ID_TIPO_MOVIMIENTO, ID_TIPO_CONCEPTO, cEvento, aEjercicioFiscal, cCentroContable, cMes, RFC, mImporteNeto, mImporteBruto,  mImporteMasIva,  nCapitulo,  mSancion, " + "mDevolucion, mImporteAmortiza, mRetencion, mPenalizacion, m2Millar, m23IVA, mISRHonorarios, mObra5, mImporteFlete4, mISRArrenda, mRetImpuestoCedular, mBruto, " + "mAmortizacionAnticipo, mIVA, mNeto, m5Millar, mFletes, mCedular, mImporte, mImporteIvaArrenda, mImporteIvaHonorarios, mImporteFlete23, mImporteIvaProv, mImporteObra, " + "mCNIC, mIMDT, mTesofe,  altaAlmacen, Periodo13, ADEFAS, OBGT, folioTempGral, mISROtros,  mISRLaudos) " + "VALUES( ?, ?, ?,  ?, '', '00',  ?,  ?,  ?, 1, ?,  ?,  ?,  ?,  ?,  ?,  ?,  ?,  ?,  ?,  ?, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.00, 0.00, 0.00," + " 0.00, 0.00, 0.00, 0.00, 0.00, '0||', 'N', 'N', ?, ?, ?, ?  )";
        String queryRFC = "SELECT rfc FROM tRELACIONGASTOSEncabezado_temp WITH(NOLOCK) WHERE nFolioRELACIONGASTOS = ?";
        int cnt = 1;
        PreparedStatement psRFC = null;
        ResultSet rs = null;
        try {
            psEnc = conn.prepareStatement(query);
            psRFC = conn.prepareStatement(queryRFC);
            psRFC.setInt(1, fTemp);
            rs = psRFC.executeQuery();
            if (rs.next())
                rfc = rs.getString(1);
            psEnc.setInt(cnt++, fTemp);
            psEnc.setInt(cnt++, nDocRenglon);
            //
            psEnc.setInt(cnt++, mes);
            psEnc.setString(cnt++, cEjercicioFiscal);
            psEnc.setString(cnt++, cIdRelacion);
            //
            psEnc.setString(cnt++, EP);
            //
            psEnc.setBigDecimal(cnt++, sumImporteNetoDet);
            psEnc.setString(cnt++, tipoMovimiento);
            psEnc.setString(cnt++, tipoConcepto);
            psEnc.setString(cnt++, cEvento);
            psEnc.setString(cnt++, cEjercicioFiscal);
            //
            psEnc.setString(cnt++, cContable);
            psEnc.setInt(cnt++, mes);
            psEnc.setString(cnt++, rfc);
            psEnc.setBigDecimal(cnt++, importeNetoDet);
            psEnc.setBigDecimal(cnt++, sumImporteNetoDet);
            //
            psEnc.setBigDecimal(cnt++, sumImporteNetoDet);
            psEnc.setInt(cnt++, nCapitulo);
            psEnc.setString(cnt++, obgt);
            psEnc.setInt(cnt++, fTempGral);
            psEnc.setBigDecimal(cnt++, mimporteISROtros);
            psEnc.setBigDecimal(cnt++, mimporteISRLaudos);
            log.debug("Object: {}", String.format(query + "[%d],[%d],[%d],[%s],[%s],[%s],[%s],[%s],[%s],[%s],[%s],[%s],[%d],[%s],[%s],[%s],[%s],[%d],[%s],[%d],[%s],[%s]", fTemp, nDocRenglon, mes, cEjercicioFiscal, cIdRelacion, EP, sumImporteNetoDet, tipoMovimiento, tipoConcepto, cEvento, cEjercicioFiscal, cContable, mes, rfc, Util.formatNumber(importeNetoDet), Util.formatNumber(sumImporteNetoDet), Util.formatNumber(sumImporteNetoDet), nCapitulo, obgt, fTempGral, Util.formatNumber(mimporteISROtros), Util.formatNumber(mimporteISRLaudos)));
            psEnc.executeUpdate();
            valor = "guardado";
        } catch (Exception e) {
            log.error("Error Al Guardar Detalle Relacion Gastos " + e, e);
            throw new SQLException(e);
        } finally {
            CloseObject.closeObject(rs, psRFC);
        }
        return valor;
    }

    private static boolean isNumeric(String cadena) {
        try {
            Integer.parseInt(cadena, 10);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    public String validaReduccionCompromisoMil(Connection conn, String nFolioCompromisoNomina, String compromisoAmpliado) {
        String valorReturn = "aplicado";
        PreparedStatement psCompromiso = null, psRemanente = null;
        ResultSet rsCompromiso = null, rsRemanente = null;
        String ep = "", mes = "";
        double mImporteCompromiso = 0.00;
        double mImporteRemanente = 0.00;
        try {
            log.debug("Object: {}", "Comienza a validar remanente del compromiso " + compromisoAmpliado + " a reducir.");
            psCompromiso = conn.prepareStatement("SELECT EP, mImporte, cMes FROM tCompromisoNominaDetalle WITH(NOLOCK) WHERE nFolioCompromisoNomina = ?");
            psCompromiso.setString(1, nFolioCompromisoNomina);
            rsCompromiso = psCompromiso.executeQuery();
            while (rsCompromiso.next() && "aplicado".equals(valorReturn)) {
                ep = rsCompromiso.getString("EP");
                mImporteCompromiso = -rsCompromiso.getDouble("mImporte");
                mes = rsCompromiso.getString("cMes");
                psRemanente = conn.prepareStatement("SELECT Remanente AS mRemanenteCompromiso FROM vRemanenteCompromisoCapituloMil WITH(NOLOCK) WHERE caNoCompromiso = '" + compromisoAmpliado + "' AND EP = '" + ep + "' AND cMes = " + mes);
                rsRemanente = psRemanente.executeQuery();
                if (rsRemanente.next()) {
                    mImporteRemanente = rsRemanente.getDouble("mRemanenteCompromiso");
                    if (mImporteRemanente < mImporteCompromiso)
                        valorReturn = "No hay suficiente remanente en la ep " + ep + "; " + mImporteRemanente + " - " + mImporteCompromiso;
                } else
                    valorReturn = "No hay suficiente remanente en la ep " + ep + "; " + mImporteRemanente + " - " + mImporteCompromiso;
            }
            if (!"aplicado".equals(valorReturn))
                log.debug("Object: {}", valorReturn);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error occurred", "Error al validar la reduccion al compromiso " + compromisoAmpliado + ". " + e);
        } finally {
            CloseObject.closeObject(psCompromiso);
            CloseObject.closeObject(psRemanente);
            CloseObject.closeObject(rsCompromiso);
            CloseObject.closeObject(rsRemanente);
        }
        return valorReturn;
    }

    public String validaCTAB(Connection conn, PreparedStatement ps, ResultSet rs, String rfc, String CTAB) throws SQLException {
        String valor = "noExiste";
        try {
            ps = conn.prepareStatement(" SELECT * FROM tBeneficiarioCuentasBancarias WITH (NOLOCK) WHERE dRFC = ? AND subCuentaBancaria = ? ");
            ps.setString(1, rfc);
            ps.setString(2, CTAB);
            rs = ps.executeQuery();
            if (rs.next()) {
                valor = "existe";
            }
        } catch (Exception e) {
            log.error("Error occurred", "Error al Validar CUENTA BANCARIA : " + e);
        }
        return valor;
    }

    public String validaComision(Connection conn, PreparedStatement ps, ResultSet rs, String sIdComision) throws SQLException {
        String valor = "noExiste";
        try {
            ps = conn.prepareStatement(" SELECT *, cConcepto_Comision AS cInformeComision FROM tViaticosComisiones WITH (NOLOCK) WHERE nIdComision = ? AND cEstatus = 'A' ");
            ps.setString(1, sIdComision);
            rs = ps.executeQuery();
            if (rs.next()) {
                valor = rs.getString("nIdComision");
            }
        } catch (Exception e) {
            log.error("Error occurred", "Error al Validar la Comisión : " + e);
        }
        return valor;
    }
}
