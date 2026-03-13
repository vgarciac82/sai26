package com.syc.contable.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import com.axtel.contabilidad.reintegrosCaja.ReintegrosCajaBusinessLogic;
import com.syc.contable.AccountingEngine;
import com.syc.contable.ContableInterface;
import com.syc.ejercido.pagado.CargaPagosBoletajeManager;
import com.syc.fortimax.core.Aplicacion;
import com.syc.fortimax.core.AplicacionManager;
import com.syc.fortimax.core.Documento;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.TipoCasoInterface;
import com.syc.gestion.core.AlarmaManager;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoDato;
import com.syc.gestion.core.CasoDatoManager;
import com.syc.gestion.core.CasoManager;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.implementacion.tesoreria.EgresosInterface;
import com.syc.obrapublica.EjercicioFiscalManager;
import com.syc.obrapublica.core.ConfiguraAplicativoManager;
import com.syc.sai.contabilidad.IngresoGreenMexManager;
import com.syc.sai.contabilidad.caja.CajaBusinessLogic;
import com.syc.sai.contabilidad.caja.CajaManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AplicacionContable implements ContableInterface, TipoCasoInterface {

    private static Logger log = LoggerFactory.getLogger(AplicacionContable.class);

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public AplicarContableReturn cancelarAppContableNueva(Connection conn, Caso c, String cTablaPadre, String cTablaHija, String cFolio, int nFolioDocumento, String cTipoDocumento, Map m, String prefixPath, String uLogin, String cFecha) throws SQLException {
        boolean success = false;
        List<String> arrayMessageReturn = new ArrayList<String>();
        boolean withDate = false;
        AplicarContableReturn retVal = new AplicarContableReturn(arrayMessageReturn, true);
        boolean tablas_no_gaveta = (!"".equals(cTablaPadre) && cTablaPadre != null && !"".equals(cTablaHija) && cTablaHija != null && !"".equals(cFolio) && cFolio != null);
        try {
            CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
            Caso cReloaded = new Caso();
            String nFolio = c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1);
            if (!tablas_no_gaveta && c != null) {
                cTablaPadre = "t" + c.getTipoCaso().getGavetaAsociada() + "Encabezado";
                cTablaHija = "t" + c.getTipoCaso().getGavetaAsociada() + "Detalle";
                cFolio = "nFolio" + c.getTipoCaso().getGavetaAsociada();
                cTipoDocumento = c.getTipoCaso().getGavetaAsociada();
            }
            if (c.getIdTC() == 13) {
                cTablaPadre = "tDocPolizaEncabezado";
                cTablaHija = "tDocPolizaDetalle";
                cFolio = "nFolioDocPoliza";
                cTipoDocumento = "DOCPOLIZA";
                withDate = true;
            }
            try {
                AccountingEngine accEng = new AccountingEngine();
                cReloaded.setIdCaso(c.getIdCaso());
                cReloaded = CasoManager.select(conn, cReloaded);
                if (!withDate && (success = accEng.cancelAccountingApplication(conn, cTipoDocumento, nFolio, cTablaPadre, cTablaHija, cFolio))) {
                    m.put("CANCELADO_CONT", "true");
                    arrayMessageReturn.add(cTipoDocumento + " APLICADO CONTABLEMENTE CANCELADO");
                } else if (withDate && (success = accEng.cancelAccountingApplication(conn, cTipoDocumento, nFolio, cTablaPadre, cTablaHija, cFolio, cFecha))) {
                    m.put("CANCELADO_CONT", "true");
                    arrayMessageReturn.add(cTipoDocumento + " APLICADO CONTABLEMENTE CANCELADO");
                } else {
                    arrayMessageReturn.add(cTipoDocumento + " NO FUE CANCELADO(A)");
                }
            } catch (Exception exc) {
                log.error(exc);
                arrayMessageReturn.add(cTipoDocumento + " NO FUE CANCELADO(A) DEBIDO A: " + exc.getLocalizedMessage());
            }
            m.put("MENSAJE", retVal.getMessageList().get(0));
            CasoDatoManager.update(conn, c.getIdTC(), c.getIdCaso(), m);
        } catch (Exception exc) {
            log.error("Error avanzando el caso automaticamente:", exc);
            arrayMessageReturn.add(cTipoDocumento + " NO FUE CANCELADO(A) DEBIDO A: " + exc.getLocalizedMessage());
        }
        return new AplicarContableReturn(arrayMessageReturn, success);
    }

    public String cancelarAppContable(Connection conn, Caso c, String cTablaPadre, String cTablaHija, String cFolio, int nFolioDocumento, String cTipoDocumento, Map m, String prefixPath, String uLogin, String cFecha) throws SQLException {
        String retVal = "";
        retVal = cancelarAppContable(conn, c, cTablaPadre, cTablaHija, cFolio, nFolioDocumento, cTipoDocumento, cFecha);
        CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
        try {
            Caso cReloaded = cbl.getCaso(c.getIdCaso());
            String mensaje = retVal;
            CasoDato cd = new CasoDato();
            cd.setIdCaso(c.getIdCaso());
            cd.setIdTC(c.getIdTC());
            cd.setIdCD(10);
            cd.setValor(mensaje);
            CasoDatoManager cdm = new CasoDatoManager();
            try {
                cdm.update(conn, cd);
                conn.commit();
            } catch (Exception se) {
                log.error("Error escribiendo el mensaje del motor en la variable de caso:", se);
            }
            cReloaded = cbl.getCaso(c.getIdCaso());
            if ("true".equals(cReloaded.getCasoDato("CANCELADO_CONT")))
                cbl.avanzaCaso(cReloaded, uLogin, "", new String[] { "CONSULTA_ADECUACION" }, new String[] { "consulta_adecuacion" }, m, prefixPath);
            else
                cbl.avanzaCaso(cReloaded, uLogin, "", new String[] { "JEFATURA_ADECUACIONES" }, new String[] { "validar_normatividad" }, m, prefixPath);
        } catch (GestionException ge) {
            log.error("Error avanzando el caso automaticamente:", ge);
        }
        return retVal;
    }

    public String cancelarAppContable(Connection conn, Caso c, String cTablaPadre, String cTablaHija, String cFolio, int nFolioDocumento, String cTipoDocumento, String cFecha) throws SQLException {
        String retVal = "";
        int iIdTipCaso = -1;
        String fMovimiento = "";
        int nMesApl = 0;
        String cMesApl = "";
        String aEjercicioFiscal = "";
        String dConceptoMovimiento = "";
        String cMoneda = "MXN";
        String cQueryConstructor = "";
        String cRamo;
        String cUnidadResponsable = "RHQ";
        String cDocumentoHaplicado;
        String nFolioPoliza;
        String cTipoPoliza = "";
        // String cUsrLoguin;
        String cCentroContable;
        String nCuenta;
        String nPolizaAutomatica = "";
        String nSubCuenta;
        String cDescripcionPoliza = "Cancelacion de " + cTipoDocumento;
        // Timestamp fCancelacion = new Timestamp(System.currentTimeMillis());
        String fCancelacion;
        double mMovimiento;
        String cTipoMovimiento;
        int nFolio = -1;
        int iIdPoliza = 0;
        int iInsertMovimiento;
        int nDocRenglon;
        int iCambiaPadre;
        int iRegreso;
        String cInsupdateSaldo = "";
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        PreparedStatement pstmntDet = null;
        ResultSet rsDet = null;
        PreparedStatement pstmntf = null;
        ResultSet rsf = null;
        int nFolioPoliza_ori = 0;
        String cFolioDocumentoMovimiento = "";
        String cValidaSaldo = "";
        String cNaturalezaCuenta = "";
        String cAplicacionCuenta = "";
        // Se agrega para validar la fecha de no venir se coloca la fecha de
        // Aplicacion del docuemnto como fecha de cancelacion
        if (!"".equals(cFecha) || cFecha != null) {
            try {
                pstmntf = conn.prepareStatement("select convert(varchar,case substring(?,3,1) WHEN '-' then convert(datetime,replace(?,'-','/'),103) WHEN '/' then convert(datetime,?,103)  else convert(datetime,replace(?,'-','/'),120) end,103) ");
                pstmntf.setString(1, cFecha);
                pstmntf.setString(2, cFecha);
                pstmntf.setString(3, cFecha);
                pstmntf.setString(4, cFecha);
                rsf = pstmntf.executeQuery();
                if (rsf.next()) {
                    fMovimiento = rsf.getString(1);
                }
            } catch (Exception e) {
                retVal = ("No se logró Cancelar la aplicación contable del documento de " + cTipoDocumento + " debido a:" + e.toString());
                log.error("No se logró Cancelar la aplicación contable del documento de " + cTipoDocumento + " debido a:", e);
                try {
                    conn.rollback();
                    return retVal;
                } catch (Exception e2) {
                    return retVal;
                }
            } finally {
                if (rsf != null)
                    rsf.close();
                if (pstmntf != null)
                    pstmntf.close();
                rsf = null;
                pstmntf = null;
            }
            if (fMovimiento != null && !"".equals(fMovimiento)) {
                cMesApl = fMovimiento.substring(3, 5);
            }
        }
        try {
            cMesApl = fMovimiento.substring(3, 5);
            nMesApl = new Integer(cMesApl);
            if ("".equals(cTablaPadre)) {
                iIdTipCaso = c.getIdTC();
                aEjercicioFiscal = c.getCasoDato("EJERCICIO_FISCAL").getValor();
                dConceptoMovimiento = c.getCasoDato("CONCEPTO_MOV").getValor();
                if (c.getCasoDato("MONEDA").getValor() != "" || c.getCasoDato("MONEDA").getValor() != null)
                    cMoneda = c.getCasoDato("MONEDA").getValor();
                nFolio = new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue();
                // numero de Folio del Documento
                nFolioDocumento = nFolio;
                cTablaPadre = "t" + c.getTipoCaso().getGavetaAsociada() + "Encabezado";
                cTablaHija = "t" + c.getTipoCaso().getGavetaAsociada() + "Detalle";
                cFolio = "nFolio" + c.getTipoCaso().getGavetaAsociada();
                cTipoDocumento = c.getTipoCaso().getGavetaAsociada();
            } else {
                if ("".equals(cTablaHija) || "".equals(cFolio) || "".equals(cTipoDocumento) || nFolioDocumento == 0) {
                    retVal = "ERROR: Los nombres de tablas Encabezado y Detalle son requeridos";
                    return retVal;
                }
                dConceptoMovimiento = "Cancelacion de Documento " + cTipoDocumento;
                nFolio = nFolioDocumento;
            }
            try {
                pstmnt = conn.prepareStatement("SELECT  cTipoPoliza, dPolizaAutomatica FROM tDocumentoTipoPoliza  WITH (NOLOCK) WHERE (cDocumento = ?)");
                pstmnt.setString(1, cTipoDocumento);
                rs = pstmnt.executeQuery();
                if (rs.next()) {
                    nPolizaAutomatica = rs.getString("dPolizaAutomatica");
                    cTipoPoliza = rs.getString("cTipoPoliza");
                }
            } catch (Exception e) {
                retVal = ("No se logró Cancelar la aplicación contable del documento de " + cTipoDocumento + " debido a:" + e.toString());
                log.error("No se logró Cancelar la aplicación contable del documento de " + cTipoDocumento + " debido a:", e);
                try {
                    conn.rollback();
                    return retVal;
                } catch (Exception e2) {
                    return retVal;
                }
            } finally {
                try {
                    if (pstmnt != null)
                        pstmnt.close();
                } catch (Exception e) {
                    log.warn("Cerrando PreparedStatement", e);
                }
                try {
                    if (rs != null)
                        rs.close();
                } catch (Exception e) {
                    log.warn("Cerrando ResultSet", e);
                }
                pstmnt = null;
                rs = null;
            }
            // cQueryConstructor =
            // "SELECT distinct h.cRamo, h.cUnidadResponsable,
            // h.cDocumentoHaplicado, h.nFolioPoliza, h.cTipoPoliza,
            // h.aEjercicioFiscal, h.U_LOGIN, d.cCentroContable";
            cQueryConstructor = "SELECT distinct h.cRamo, h.cUnidadResponsable, h.cDocumentoHaplicado, h.nFolioPoliza, h.cTipoPoliza, h.aEjercicioFiscal, d.cCentroContable";
            cQueryConstructor += "  FROM " + cTablaPadre + " h  WITH (NOLOCK) ";
            cQueryConstructor += "     , " + cTablaHija + " d  WITH (NOLOCK) ";
            cQueryConstructor += " WHERE d." + cFolio + " = h." + cFolio;
            cQueryConstructor += "   AND d." + cFolio + " = " + nFolio;
            try {
                pstmnt = conn.prepareStatement(cQueryConstructor);
                rs = pstmnt.executeQuery();
                while (rs.next()) {
                    cRamo = rs.getString("cRamo");
                    cDocumentoHaplicado = rs.getString("cDocumentoHaplicado");
                    nFolioPoliza = rs.getString("nFolioPoliza");
                    cTipoPoliza = rs.getString("cTipoPoliza");
                    aEjercicioFiscal = rs.getString("aEjercicioFiscal");
                    // cUsrLoguin = rs.getString("U_LOGIN");
                    cCentroContable = rs.getString("cCentroContable");
                    if (!"S".equals(cDocumentoHaplicado)) {
                        if (!"C".equals(cDocumentoHaplicado)) {
                            retVal = "Error al Cancelar Documento: El Documento " + cTipoDocumento + " de Folio :" + nFolio + " no esta Aplicado Contablemente. ";
                        } else {
                            retVal = "Error al Cancelar Documento: El Documento " + cTipoDocumento + " de Folio :" + nFolio + " ya esta Cancelado. ";
                        }
                        conn.rollback();
                        return retVal;
                    }
                    iIdPoliza = CreaPoliza(conn, nFolio, cMesApl, cDescripcionPoliza, cTipoDocumento, nPolizaAutomatica, cTipoPoliza, fMovimiento, aEjercicioFiscal, cCentroContable);
                    cQueryConstructor = "select m.nCuenta, m.nSubCuenta, (m.mMovimiento * -1) as mMovimiento, m.cTipoMovimiento, " + "m.nDocRenglon, p.nFolioPoliza, m.cFolioDocumentoMovimiento, c.AplicacionCuenta, c.NaturalezaCuenta, c.VerificaSaldo" + "  from tMovimiento m  WITH (NOLOCK) " + "      , tPoliza p   WITH (NOLOCK) " + "      , tCuentas c  WITH (NOLOCK) " + "where   m.aEjercicioFiscal = p.aEjercicioFiscal " + "   AND m.cCentroContable = p.cCentroContable" + "   AND m.cTipoPoliza = p.cTipoPoliza" + "   AND m.nFolioPoliza = p.nFolioPoliza" + "   AND m.nCuenta      = c.nCuenta" + "   AND m.aEjercicioFiscal= '" + aEjercicioFiscal + "'   AND m.cCentroContable = '" + cCentroContable + "'  AND m.cTipoPoliza = '" + cTipoPoliza + "'  AND p.nFolioDocumento = " + nFolio + "   AND p.cTipoDocumento = '" + cTipoDocumento + "'";
                    try {
                        pstmntDet = conn.prepareStatement(cQueryConstructor);
                        rsDet = pstmntDet.executeQuery();
                        while (rsDet.next()) {
                            String cCentroContable_paso = cCentroContable;
                            nCuenta = rsDet.getString(1);
                            nSubCuenta = rsDet.getString(2);
                            mMovimiento = rsDet.getDouble("mMovimiento");
                            cTipoMovimiento = rsDet.getString(4);
                            nDocRenglon = rsDet.getInt(5);
                            nFolioPoliza_ori = rsDet.getInt(6);
                            cFolioDocumentoMovimiento = rs.getString(7);
                            cValidaSaldo = rsDet.getString("VerificaSaldo");
                            cNaturalezaCuenta = rsDet.getString("NaturalezaCuenta");
                            cAplicacionCuenta = rsDet.getString("AplicacionCuenta");
                            if (nCuenta.substring(0, 1).equals("8")) {
                                cCentroContable_paso = "0";
                            }
                            iInsertMovimiento = CreaMovimientos(conn, nFolioDocumento, nDocRenglon, cMesApl, cRamo, cUnidadResponsable, iIdPoliza, cTipoDocumento, nCuenta, nSubCuenta, mMovimiento, cTipoMovimiento, fMovimiento, cTipoPoliza, aEjercicioFiscal, cMoneda, dConceptoMovimiento, cCentroContable);
                            if (iInsertMovimiento != 1) {
                                retVal = "Error al crear el Movimiento, no se pudo Aplicar la Cancelacion.";
                                conn.rollback();
                                return retVal;
                            }
                            cInsupdateSaldo = CreaCambiaSaldos(conn, cRamo, cUnidadResponsable, nCuenta, nSubCuenta, aEjercicioFiscal, cTipoMovimiento, cMesApl, cMoneda, mMovimiento, cTipoDocumento, cCentroContable_paso, "Cancelacion", cValidaSaldo, cNaturalezaCuenta, cAplicacionCuenta);
                            if (cInsupdateSaldo.length() > 0) {
                                retVal = "Error al Modificar Saldos, no se pudo Aplicar la Cancelacion.";
                                conn.rollback();
                                return retVal;
                            }
                        }
                    } finally {
                        try {
                            if (pstmntDet != null)
                                pstmntDet.close();
                        } catch (Exception e) {
                            log.warn("Cerrando PreparedStatement CancelacionDocumento", e);
                        }
                        try {
                            if (rs != null)
                                rsDet.close();
                        } catch (Exception e) {
                            log.warn("Cerrando ResultSet CancelacionDocumento", e);
                        }
                        pstmntDet = null;
                        rsDet = null;
                    }
                }
                retVal = CancelaMovimientos(conn, cFolioDocumentoMovimiento, nFolioPoliza_ori, cTipoPoliza, aEjercicioFiscal);
                if (retVal.equals("0")) {
                    retVal = "Error al crear el Movimiento, no se pudo Aplicar la Cancelacion.";
                    conn.rollback();
                    return retVal;
                }
                iCambiaPadre = CambiaDocCancelado(conn, nFolioDocumento, fMovimiento, iIdPoliza, cTablaPadre, cTipoPoliza, cFolio);
                if (iCambiaPadre < 1) {
                    retVal = "Error al crear el Movimiento, no se pudo Aplicar la Cancelacion.";
                    conn.rollback();
                    return retVal;
                }
            } catch (Exception e) {
                retVal = ("No se logró Cancelar la aplicación contable del docuemnto de " + cTipoDocumento + " debido a:" + e.toString());
                log.error("No se logró Cancelar la aplicación contable del docuemnto de " + cTipoDocumento + " debido a:", e);
                e.printStackTrace();
                try {
                    if (conn != null) {
                        conn.rollback();
                        return retVal;
                    }
                } catch (Exception e2) {
                    return retVal;
                }
            } finally {
                try {
                    if (pstmnt != null)
                        pstmnt.close();
                } catch (Exception e) {
                    log.warn("Cerrando PreparedStatement", e);
                }
                try {
                    if (rs != null)
                        rs.close();
                } catch (Exception e) {
                    log.warn("Cerrando ResultSet", e);
                }
                pstmnt = null;
                rs = null;
            }
        } finally {
        }
        try {
            if (c != null) {
                CasoDato cd = new CasoDato();
                cd.setIdCaso(c.getIdCaso());
                cd.setIdCD(9);
                cd.setIdTC(c.getIdTC());
                cd.setValor("true");
                CasoDatoManager cdm = new CasoDatoManager();
                cdm.update(conn, cd);
                conn.commit();
                retVal = cTipoDocumento + " APLICADO CONTABLEMENTE CANCELADO";
                log.debug("DOCUMENTO DE " + cTipoDocumento + " APLICADO CONTABLEMENTE CANCELADO");
            } else {
                retVal = cTipoDocumento + " APLICADO CONTABLEMENTE CANCELADO";
                conn.commit();
                log.debug("DOCUMENTO DE " + cTipoDocumento + " APLICADO CONTABLEMENTE CANCELADO");
            }
        } catch (SQLException e) {
            retVal = e.getSQLState();
            log.error("DOCUMENTO DE " + cTipoDocumento + " APLICADO CONTABLEMENTE CANCELADO", e);
            conn.rollback();
        }
        return retVal;
    }

    public AplicarContableReturn aplicarContableNuevo(Connection conn, Caso c, String cTablaPadre, String cTablaHija, String cFolio, int nFolioDocumento, String cTipoDocumento, Map m, String prefixPath, String uLogin, String validaSaldo) {
        ArrayList<String> arrRet = new ArrayList<String>();
        boolean appCont = false;
        boolean autorizar = (c.getCasoDato("APLICADO_CONT").getValor() != null && "true".equals(c.getCasoDato("APLICADO_CONT").getValor()));
        boolean tablas_no_gaveta = (!"".equals(cTablaPadre) && cTablaPadre != null && !"".equals(cTablaHija) && cTablaHija != null && !"".equals(cFolio) && cFolio != null);
        try {
            AccountingEngine accEng = new AccountingEngine();
            // accEng.setBatchSize(batchSize); //por default tiene 1000,
            // incrementar o decrementar si es necesario
            if ("SI".equals(validaSaldo)) {
                // "SI" significa que si es una
                // super adecuacion entonces NO
                // debe
                // verificar saldo
                accEng.setValidaInsuficienciaDeSaldo(false);
            }
            // suficiencia en caso del superusuario que aun no existe
            String nFolio = c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1);
            // if (!autorizar) {
            if (!tablas_no_gaveta && c != null) {
                cTablaPadre = "t" + c.getTipoCaso().getGavetaAsociada() + "Encabezado";
                cTablaHija = "t" + c.getTipoCaso().getGavetaAsociada() + "Detalle";
                cFolio = "nFolio" + c.getTipoCaso().getGavetaAsociada();
                cTipoDocumento = c.getTipoCaso().getGavetaAsociada();
            }
            if (c.getIdTC() == 13)
                appCont = accEng.makeAccountingApplicationWithoutEvent(conn, "DOC" + cTipoDocumento, nFolio, "tDocPolizaEncabezado", "tDocPolizaDetalle", "nFolioDocPoliza");
            else if (c.getIdTC() == 41)
                appCont = accEng.makeAccountingApplicationWithoutEvent(conn, cTipoDocumento, nFolio, cTablaPadre, cTablaHija, cFolio);
            else
                appCont = accEng.makeAccountingApplication(conn, cTipoDocumento, nFolio, cTablaPadre, cTablaHija, cFolio);
            if (appCont) {
                if (autorizar)
                    m.put("AUTORIZADO_CONT", "true");
                else
                    m.put("APLICADO_CONT", "true");
                arrRet.add("DOCUMENTO DE " + cTipoDocumento + " APLICADO CONTABLEMENTE");
            }
        } catch (Exception ex) {
            log.warn(ex.getMessage(), ex);
            arrRet.add(ex.getLocalizedMessage());
        }
        // fin de nuevo motor contable
        // LAS VARIABLES DEL CASO SE ACTUALIZAN EN UNA TRANSACCION DIFERENTE
        // PARA EL avanzaCaso DE ABAJO
        CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
        try {
            // Caso cReloaded = cbl.getCaso(c.getIdCaso());
            StringBuffer mensaje = new StringBuffer();
            if (!arrRet.isEmpty()) {
                for (int i = 0; i < arrRet.size(); i++) {
                    mensaje.append(arrRet.get(i).toString()).append("|");
                }
            }
            m.put("MENSAJE", mensaje.toString());
            Connection cdConn = cbl.getConnection();
            CasoDatoManager.update(cdConn, c.getIdTC(), c.getIdCaso(), m);
            cdConn.commit();
            cdConn.close();
            cdConn = null;
            /*
			 * El avanzado automatico se cambia al adecuacionbusinesslogic Caso
			 * cReloaded = new Caso(); cReloaded.setIdCaso(c.getIdCaso()); //
			 * cReloaded = cbl.getCaso(c.getIdCaso()); cReloaded =
			 * CasoManager.select(conn, cReloaded);
			 * 
			 * if (c.getIdTC() == 3) { if (!arrRet.isEmpty() &&
			 * arrRet.get(0).toString().contains("APLICADO")) { if (!autorizar)
			 * cbl.avanzaCaso(cReloaded, uLogin, "", new String[] {
			 * "JEFATURA_ADECUACIONES" }, new String[] { "validar_normatividad"
			 * }, m, prefixPath); else cbl.avanzaCaso(cReloaded, uLogin, "", new
			 * String[] { "CONSULTA_ADECUACION" }, new String[] {
			 * "consulta_adecuacion" }, m, prefixPath); } else { if (!autorizar)
			 * cbl.avanzaCaso(cReloaded, uLogin, "", new String[] {
			 * "REVISORES_ADECUACIONES" }, new String[] { "revision_adecuacion"
			 * }, m, prefixPath); else cbl.avanzaCaso(cReloaded, uLogin, "", new
			 * String[] { "JEFATURA_ADECUACIONES" }, new String[] {
			 * "validar_normatividad" }, m, prefixPath); } }
			 */
        } catch (Exception ge) {
            // log.error("Error avanzando el caso automaticamente o escribiendo
            // el mensaje del otor en la variable de caso:",
            // ge);
            log.error("Error actulizando variables de caso despues de aplicacion contable:", ge);
        }
        return new AplicarContableReturn(arrRet, appCont);
    }

    public class AplicarContableReturn {

        List<String> messageList;

        boolean success;

        public AplicarContableReturn(List<String> messageList, boolean success) {
            this.messageList = messageList;
            this.success = success;
        }

        public List<String> getMessageList() {
            return messageList;
        }

        public boolean isSuccess() {
            return success;
        }
    }

    public ArrayList<String> aplicarContable(Connection conn, Caso c, String cTablaPadre, String cTablaHija, String cFolio, int nFolioDocumento, String cTipoDocumento, Map m, String prefixPath, String uLogin) {
        ArrayList<String> arrRet = new ArrayList<String>();
        boolean autorizar = (c.getCasoDato("APLICADO_CONT").getValor() != null && "true".equals(c.getCasoDato("APLICADO_CONT").getValor()));
        arrRet = aplicarContable(conn, c, cTablaPadre, cTablaHija, cFolio, nFolioDocumento, cTipoDocumento);
        CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
        try {
            Caso cReloaded = cbl.getCaso(c.getIdCaso());
            StringBuffer mensaje = new StringBuffer();
            if (!arrRet.isEmpty()) {
                for (int i = 0; i < arrRet.size(); i++) {
                    mensaje.append(arrRet.get(i).toString()).append("|");
                }
            }
            CasoDato cd = new CasoDato();
            cd.setIdCaso(c.getIdCaso());
            cd.setIdTC(c.getIdTC());
            cd.setIdCD(10);
            cd.setValor(mensaje.toString());
            CasoDatoManager cdm = new CasoDatoManager();
            try {
                cdm.update(conn, cd);
                conn.commit();
            } catch (SQLException se) {
                log.error("Error escribiendo el mensaje del motor en la variable de caso:", se);
            }
            cReloaded = cbl.getCaso(c.getIdCaso());
            if (!arrRet.isEmpty() && arrRet.get(0).toString().contains("APLICADO")) {
                if (!autorizar)
                    cbl.avanzaCaso(cReloaded, uLogin, "", new String[] { "JEFATURA_ADECUACIONES" }, new String[] { "validar_normatividad" }, m, prefixPath);
                else
                    cbl.avanzaCaso(cReloaded, uLogin, "", new String[] { "CONSULTA_ADECUACION" }, new String[] { "consulta_adecuacion" }, m, prefixPath);
            } else {
                if (!autorizar)
                    cbl.avanzaCaso(cReloaded, uLogin, "", new String[] { "REVISORES_ADECUACIONES" }, new String[] { "revision_adecuacion" }, m, prefixPath);
                else
                    cbl.avanzaCaso(cReloaded, uLogin, "", new String[] { "JEFATURA_ADECUACIONES" }, new String[] { "validar_normatividad" }, m, prefixPath);
            }
        } catch (GestionException ge) {
            log.error("Error avanzando el caso automaticamente:", ge);
        }
        return arrRet;
    }

    public ArrayList<String> aplicarContable(Connection conn, Caso c, String cTablaPadre, String cTablaHija, String cFolio, int nFolioDocumento, String cTipoDocumento) {
        ArrayList<String[]> arrcCuentaDet = new ArrayList<String[]>();
        ArrayList arrcRetAplCont = new ArrayList();
        String[] arrcSubCuentaDet = new String[4];
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        PreparedStatement pstmntf = null;
        ResultSet rsf = null;
        PreparedStatement pstmntDoc = null;
        ResultSet rsDoc = null;
        PreparedStatement pstmntCta = null;
        ResultSet rsCta = null;
        String cTipoOperacion = "";
        int iIdTipCaso = 0;
        int iNumSecComm = 0;
        int iRenglonFolio = 0;
        double ImporteSecuencia = 0;
        int ilongitud_bloque = 0;
        int nMes = 0;
        String nMesApl = "";
        int iInserPoliza = 0;
        int iInsertMovimiento = 0;
        String cMes = "";
        String cSubCuenta = "";
        String cSqlQueryDefinition = "";
        String cRamo = "";
        String cUnidadResponsable = "RHQ";
        String cCentroContableOld = "";
        String cCentroContable = "";
        String nDocAplicado = "";
        String cSubcuentaDetDoc = "";
        String cEventoID = "";
        String cEjericioFApl = "";
        String dComponente = "";
        String cSubcuenta = "";
        String cNumCuentaDetalle = "";
        String cAplicacionCuenta = "";
        String cValidaSaldo = "";
        String cNaturalezaCuenta = "";
        String cDescripcionPoliza = "";
        String cTipoPoliza = "";
        // String cTipoDocumento="";
        String TipoBalance = "";
        String nPolizaAutomatica = "";
        String fAplica = "";
        String cTipoMovimiento = "";
        String fMovimiento = "";
        String aEjercicioFiscal = "";
        String cMoneda = "MXP";
        String dConceptoMovimiento = "";
        int iSecSuma = 0;
        String cInsupdateSaldo = "";
        int iCambiaPadre = 0;
        double mTotalCargo = 0;
        double mTotalAbono = 0;
        double mTotalCargoPoilza = 0;
        double mTotalAbonoPoilza = 0;
        int iTotCuentasConfig = 0;
        int nFolio = 0;
        String DATE_FORMAT = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        try {
            // iIdTipCaso = c.getIdTC();
            Date datePaso = null;
            if (c != null) {
                if (c.getCasoDato("FECHA_AP_CONT").getValor() != null && c.getCasoDato("FECHA_AP_CONT").getValor() != "") {
                    String fFechaPaso = c.getCasoDato("FECHA_AP_CONT").getValor();
                    try {
                        pstmntf = conn.prepareStatement("select convert(varchar,case substring(?,3,1) WHEN '-' then convert(datetime,replace(?,'-','/'),103) WHEN '/' then convert(datetime,?,103)  else convert(datetime,replace(?,'-','/'),120) end,103) ");
                        pstmntf.setString(1, fFechaPaso);
                        pstmntf.setString(2, fFechaPaso);
                        pstmntf.setString(3, fFechaPaso);
                        pstmntf.setString(4, fFechaPaso);
                        rsf = pstmntf.executeQuery();
                        if (rsf.next()) {
                            fMovimiento = rsf.getString(1);
                        }
                    } finally {
                        if (rsf != null)
                            rsf.close();
                        if (pstmntf != null)
                            pstmntf.close();
                        rsf = null;
                        pstmntf = null;
                    }
                    if (fMovimiento != null && !"".equals(fMovimiento)) {
                        nMesApl = fMovimiento.substring(3, 5);
                        cEjericioFApl = fMovimiento.substring(6, 10);
                    }
                }
                aEjercicioFiscal = c.getCasoDato("EJERCICIO_FISCAL").getValor();
                dConceptoMovimiento = c.getCasoDato("CONCEPTO_MOV").getValor();
                if (c.getCasoDato("MONEDA").getValor() != "" || c.getCasoDato("MONEDA").getValor() != null)
                    cMoneda = c.getCasoDato("MONEDA").getValor();
            }
            if (cTablaPadre.equals("")) {
                nFolio = new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue();
                // numero de Folio del Documento
                nFolioDocumento = nFolio;
                cTablaPadre = "t" + c.getTipoCaso().getGavetaAsociada() + "Encabezado";
                cTablaHija = "t" + c.getTipoCaso().getGavetaAsociada() + "Detalle";
                cFolio = "nFolio" + c.getTipoCaso().getGavetaAsociada();
                cTipoDocumento = c.getTipoCaso().getGavetaAsociada();
            } else {
                if (cTablaHija.equals("") || cFolio.equals("") || cTipoDocumento.equals("") || nFolioDocumento == 0) {
                    conn.rollback();
                    arrcRetAplCont.add("ERROR Falta información esencial para identificar el documentó a aplicar");
                    return arrcRetAplCont;
                }
            }
            try {
                pstmnt = conn.prepareStatement("SELECT GP_NOMBRE, GP_VALOR FROM CG_GRUPO_PROPIEDADES  WITH (NOLOCK) WHERE (GP_NOMBRE = 'longitud_bloque')");
                rs = pstmnt.executeQuery();
                if (rs.next()) {
                    ilongitud_bloque = rs.getInt("GP_VALOR");
                }
            } finally {
                if (rs != null)
                    rs.close();
                if (pstmnt != null)
                    pstmnt.close();
                rs = null;
                pstmnt = null;
            }
            try {
                pstmnt = conn.prepareStatement("SELECT  cTipoPoliza, dPolizaAutomatica FROM tDocumentoTipoPoliza  WITH (NOLOCK)  WHERE (cDocumento = ?)");
                pstmnt.setString(1, cTipoDocumento);
                rs = pstmnt.executeQuery();
                if (rs.next()) {
                    nPolizaAutomatica = rs.getString("dPolizaAutomatica");
                    cTipoPoliza = rs.getString("cTipoPoliza");
                } else {
                    conn.rollback();
                    arrcRetAplCont.add("Error no existe la relacion del Documento " + cTipoDocumento + " con el tipo de Poliza en la tDocumentoTipoPoliza ");
                    return arrcRetAplCont;
                }
            } finally {
                if (rs != null)
                    rs.close();
                if (pstmnt != null)
                    pstmnt.close();
                rs = null;
                pstmnt = null;
            }
            try {
                cSqlQueryDefinition = "SELECT * FROM " + cTablaPadre + " WITH (NOLOCK) WHERE " + cFolio + " = " + nFolioDocumento;
                pstmntDoc = conn.prepareStatement(cSqlQueryDefinition);
                rsDoc = pstmntDoc.executeQuery();
                if (rsDoc.next()) {
                    nDocAplicado = rsDoc.getString("cDocumentoHaplicado");
                    cRamo = rsDoc.getString("cRamo");
                    // cUnidadResponsable =
                    // rsDoc.getString("cUnidadResponsable");
                    String fCaso = rsDoc.getString("fAplicacion");
                    if (!"".equals(fCaso) && fCaso != null) {
                        try {
                            pstmntf = conn.prepareStatement("select convert(varchar,case substring(?,3,1) WHEN '-' then convert(datetime,replace(?,'-','/'),103) WHEN '/' then convert(datetime,?,103)  else convert(datetime,replace(?,'-','/'),120) end,103) ");
                            pstmntf.setString(1, rsDoc.getString("fAplicacion"));
                            pstmntf.setString(2, rsDoc.getString("fAplicacion"));
                            pstmntf.setString(3, rsDoc.getString("fAplicacion"));
                            pstmntf.setString(4, rsDoc.getString("fAplicacion"));
                            rsf = pstmntf.executeQuery();
                            if (rsf.next()) {
                                fMovimiento = rsf.getString(1);
                                if (fMovimiento != null && !"".equals(fMovimiento)) {
                                    nMesApl = fMovimiento.substring(3, 5);
                                    cEjericioFApl = fMovimiento.substring(6, 10);
                                }
                            }
                        } finally {
                            if (rsf != null)
                                rsf.close();
                            if (pstmntf != null)
                                pstmntf.close();
                            rsf = null;
                            pstmntf = null;
                        }
                    } else {
                        if ("".equals(fCaso) && "".equals(fMovimiento)) {
                            conn.rollback();
                            arrcRetAplCont.add("Error Falta Fecha de Aplicación Contable");
                            return arrcRetAplCont;
                        }
                    }
                    aEjercicioFiscal = rsDoc.getString("aEjercicioFiscal");
                    if (!cEjericioFApl.equals(aEjercicioFiscal)) {
                        conn.rollback();
                        arrcRetAplCont.add("Error: El Ejercicio Fiscal no corresponde a la fecha de aplicación del Documento: " + cTipoDocumento);
                        return arrcRetAplCont;
                    }
                    if (nMesApl == null || nMesApl == "") {
                        conn.rollback();
                        arrcRetAplCont.add("Error no se indico la fecha de aplicacion del Documento " + cTipoDocumento);
                        return arrcRetAplCont;
                    }
                    if ("S".equals(nDocAplicado)) {
                        conn.rollback();
                        arrcRetAplCont.add("Error: Documento Aplicado Previamente, consulte con Administrador.");
                        return arrcRetAplCont;
                    }
                }
            } finally {
                if (rsDoc != null)
                    rsDoc.close();
                if (pstmntDoc != null)
                    pstmntDoc.close();
                rsDoc = null;
                pstmntDoc = null;
            }
            try {
                if (cTipoDocumento.equals("PRESUPUESTO")) {
                    cSqlQueryDefinition = "SELECT c.cEvento, STUFF ( c.nCuenta , Long-longitud_bloque , longitud_bloque*1 ,dCuentaDinamica ) as dCuentaDinamica, d.EP, d.mImporte, d.cMes " + " , d.nDocRenglon,TipoCuenta, d.cCentroContable  " + " FROM (SELECT DISTINCT a.nCuenta, a.nOrden, a.nDocRenglon, a.dDetalleCuenta, longitud_bloque, cEvento,Long,TipoCuenta  " + " FROM  (SELECT e.nCuenta, c.TipoCuenta, e.nDocRenglon, dDetalleCuenta, nOrden, e.cEvento, (SELECT GP_VALOR*e.nOrden+e.nOrden from CG_GRUPO_PROPIEDADES c WITH (NOLOCK) where GP_NOMBRE = 'longitud_bloque') Long  " + " , (select GP_VALOR from CG_GRUPO_PROPIEDADES c where GP_NOMBRE = 'longitud_bloque') as longitud_bloque   from tEventoConfiguraDetalle e, tEventoConfiguracion c     " + " Where e.aEjercicioFiscal = c.aEjercicioFiscal AND e.nCuenta = c.nCuenta AND e.nDocRenglon = c.nDocRenglon AND e.aEjercicioFiscal = '" + aEjercicioFiscal + "' AND e.cEvento = 'Presupuestacion' AND e.cEvento=c.cEvento)a  " + " where a.dDetalleCuenta != SUBSTRING(a.nCuenta,a.Long-a.longitud_bloque,longitud_bloque*1)) c    " + " , (SELECT EP,mImporte,cEvento,cMes, cCentroContable, Right(replicate('0', 5)+convert(varchar(5), cMes), 5) as dCuentaDinamica ,p.nDocRenglon  " + " FROM tPresupuestoDetalle p  WITH (NOLOCK) WHERE p.nFolioPresupuesto =  " + nFolioDocumento + "  ) d  WHERE c.cEvento = d.cEvento   " + "  UNION    " + " select h.cEvento, h.nCuenta,'' as EP, sum(h.mImporte), 1 as cMes, h.docrenglonCuenta, h.TipoCuenta, h.cCentroContable  " + " from ( SELECT  e.cEvento,e.nCuenta, e.nDocRenglon as docrenglonCuenta, p.EP, p.mImporte,p.cMes, p.cCentroContable, p.nDocRenglon, e.TipoCuenta  " + " FROM  tEventoConfiguracion e, tPresupuestoDetalle p    " + " where e.cEvento = 'CARGA' and substring(nCuenta,1,5) not in (Select substring(STUFF ( c.nCuenta , Long-longitud_bloque , longitud_bloque*1 ,dCuentaDinamica ),1,5) as dCuentaDinamica  " + " FROM(select distinct a.nCuenta, a.nOrden, a.dDetalleCuenta, longitud_bloque, cEvento,Long from (SELECT nCuenta, dDetalleCuenta, nOrden, cEvento,(select GP_VALOR*e.nOrden+e.nOrden from CG_GRUPO_PROPIEDADES c where GP_NOMBRE = 'longitud_bloque') Long  " + " , (select GP_VALOR from CG_GRUPO_PROPIEDADES c where GP_NOMBRE = 'longitud_bloque') as longitud_bloque from tEventoConfiguraDetalle e Where  aEjercicioFiscal = '" + aEjercicioFiscal + "'AND e.cEvento = 'Presupuestacion')a where a.dDetalleCuenta != SUBSTRING(a.nCuenta,a.Long-a.longitud_bloque,longitud_bloque*1)) c,  " + " (SELECT EP,mImporte,cEvento,cMes, cCentroContable, Right(replicate('0', 5)+convert(varchar(5), cMes), 5) as dCuentaDinamica FROM tPresupuestoDetalle p  WITH (NOLOCK) WHERE p.nFolioPresupuesto =  " + nFolioDocumento + "   ) d   where c.cEvento = d.cEvento))h    " + " group by h.cEvento, h.nCuenta, h.docrenglonCuenta, h.TipoCuenta, h.cCentroContable   order by cCentroContable,4,6,3";
                } else {
                    cSqlQueryDefinition = "SELECT * FROM " + cTablaHija + " WITH (NOLOCK) WHERE " + cFolio + " = " + nFolioDocumento + " ORDER BY nDocRenglon asc";
                }
                pstmntDoc = conn.prepareStatement(cSqlQueryDefinition);
                rsDoc = pstmntDoc.executeQuery();
                int iNumeroLinea = 0;
                while (rsDoc.next()) {
                    iNumeroLinea++;
                    iNumSecComm++;
                    if (iNumSecComm == 100 && cTipoDocumento.equals("PRESUPUESTO")) {
                        conn.commit();
                        iNumSecComm = 0;
                    }
                    cEventoID = rsDoc.getString("cEvento");
                    cTipoOperacion = cEventoID.substring(0, 1);
                    iRenglonFolio = rsDoc.getInt("nDocRenglon");
                    cCentroContable = rsDoc.getString("cCentroContable");
                    if (!cTipoDocumento.equals("PRESUPUESTO")) {
                        arrcCuentaDet = ObtieneCuentaContable(conn, cTablaHija, nFolioDocumento, cFolio, iRenglonFolio, cEventoID, ilongitud_bloque, aEjercicioFiscal);
                        // cEventoIDAnt = cEventoID;
                    } else
                        arrcCuentaDet.add(new String[] { cEventoID });
                    iTotCuentasConfig = 0;
                    iSecSuma = 0;
                    log.debug("Numero de Line:" + iNumeroLinea);
                    for (Iterator<String[]> iter = arrcCuentaDet.iterator(); iter.hasNext(); ) {
                        if (!cTipoDocumento.equals("PRESUPUESTO")) {
                            if (arrcCuentaDet.size() == iTotCuentasConfig)
                                break;
                            arrcSubCuentaDet = arrcCuentaDet.get(iTotCuentasConfig);
                            dComponente = arrcSubCuentaDet[0];
                            cSubcuenta = arrcSubCuentaDet[1];
                            cNumCuentaDetalle = arrcSubCuentaDet[2];
                            cTipoMovimiento = arrcSubCuentaDet[3];
                            iTotCuentasConfig++;
                        } else {
                            dComponente = "mImporte";
                            // cSubcuenta="EP";
                            cNumCuentaDetalle = rsDoc.getString("dCuentaDinamica");
                            cTipoMovimiento = rsDoc.getString("TipoCuenta");
                        }
                        if (cSubcuenta.equals(""))
                            cSubcuentaDetDoc = "";
                        else
                            cSubcuentaDetDoc = rsDoc.getString(cSubcuenta);
                        cMes = rsDoc.getString("cMes");
                        ImporteSecuencia = rsDoc.getDouble(dComponente);
                        if (cTipoMovimiento.equals("C")) {
                            mTotalCargoPoilza = mTotalCargoPoilza + ImporteSecuencia;
                            mTotalCargo = mTotalCargo + ImporteSecuencia;
                        } else if (cTipoMovimiento.equals("A")) {
                            mTotalAbono = mTotalAbono + ImporteSecuencia;
                            mTotalAbonoPoilza = mTotalAbonoPoilza + ImporteSecuencia;
                        }
                        try {
                            pstmntCta = conn.prepareStatement("SELECT VerificaSaldo, NaturalezaCuenta, AplicacionCuenta,isnull(cSubcuenta,'') as cSubcuenta,TipoBalance  FROM tCuentas WITH (NOLOCK) where nCuenta=?");
                            pstmntCta.setString(1, cNumCuentaDetalle);
                            rsCta = pstmntCta.executeQuery();
                            if (rsCta.next()) {
                                cAplicacionCuenta = rsCta.getString("AplicacionCuenta");
                                cSubcuenta = rsCta.getString("cSubcuenta");
                                TipoBalance = rsCta.getString("TipoBalance");
                                cValidaSaldo = rsCta.getString("VerificaSaldo");
                                cNaturalezaCuenta = rsCta.getString("NaturalezaCuenta");
                                if (cSubcuenta.equals(""))
                                    cSubcuentaDetDoc = "";
                                else {
                                    cSubcuentaDetDoc = rsDoc.getString(cSubcuenta);
                                    if (cTipoDocumento.equals("PRESUPUESTO")) {
                                        if (!cSubcuentaDetDoc.equals(cSubCuenta)) {
                                            PresupuestoManager.insertaCatalogoEP(conn, cSubcuentaDetDoc, cCentroContable, "gcota", aEjercicioFiscal);
                                            cSubCuenta = cSubcuentaDetDoc;
                                        }
                                    }
                                }
                            }
                            if (!"S".equals(cAplicacionCuenta)) {
                                arrcRetAplCont.add("ERROR en la configuracion de la cuenta existen Cuentas que no son de aplicacion contable cNumCuenta: " + cNumCuentaDetalle);
                                log.error("ERROR en la configuracion de la cuenta existen Cuentas que no son de aplicacion contable");
                                conn.rollback();
                                return arrcRetAplCont;
                            } else {
                                if (iInserPoliza == 0 || !cCentroContableOld.equals(cCentroContable)) {
                                    if (iInserPoliza != 0) {
                                        int iRegreso = CambiaPoliza(conn, nFolioDocumento, fMovimiento, iInserPoliza, cTablaPadre, cTipoPoliza, cFolio, mTotalCargo, mTotalAbono);
                                    }
                                    // log.error("Nueva poliza para " +
                                    // cCentroContable);
                                    iInserPoliza = CreaPoliza(conn, nFolioDocumento, nMesApl, cDescripcionPoliza, cTipoDocumento, nPolizaAutomatica, cTipoPoliza, fMovimiento, aEjercicioFiscal, cCentroContable);
                                    cCentroContableOld = cCentroContable;
                                    mTotalCargoPoilza = 0;
                                    mTotalAbonoPoilza = 0;
                                }
                                iInsertMovimiento = CreaMovimientos(conn, nFolioDocumento, iRenglonFolio, nMesApl, cRamo, cUnidadResponsable, iInserPoliza, cTipoDocumento, cNumCuentaDetalle, cSubcuentaDetDoc, ImporteSecuencia, cTipoMovimiento, fMovimiento, cTipoPoliza, aEjercicioFiscal, cMoneda, dConceptoMovimiento, cCentroContable);
                                if (iInsertMovimiento < 0) {
                                    arrcRetAplCont.add("Error al crear movimiento");
                                    log.error("Al crear movimiento");
                                    conn.rollback();
                                    return arrcRetAplCont;
                                } else {
                                    String cCentroContable_old;
                                    if (!TipoBalance.equals("P")) {
                                        cCentroContable_old = cCentroContable;
                                    } else {
                                        cCentroContable_old = "0";
                                    }
                                    if (ImporteSecuencia != 0) {
                                        cInsupdateSaldo = CreaCambiaSaldos(conn, cRamo, cUnidadResponsable, cNumCuentaDetalle, cSubcuentaDetDoc, aEjercicioFiscal, cTipoMovimiento, nMesApl, cMoneda, ImporteSecuencia, cTipoDocumento, cCentroContable_old, cTipoOperacion, cValidaSaldo, cNaturalezaCuenta, cAplicacionCuenta);
                                    }
                                    if (!cInsupdateSaldo.isEmpty()) {
                                        arrcRetAplCont.add(cInsupdateSaldo);
                                        log.error("Error en Saldos");
                                        conn.rollback();
                                        return arrcRetAplCont;
                                    }
                                }
                            }
                        } finally {
                            if (rsCta != null)
                                rsCta.close();
                            if (pstmntCta != null)
                                pstmntCta.close();
                            rsCta = null;
                            pstmntCta = null;
                        }
                        if (cTipoDocumento.equals("PRESUPUESTO")) {
                            break;
                        }
                    }
                }
            } finally {
                if (rsDoc != null)
                    rsDoc.close();
                if (pstmntDoc != null)
                    pstmntDoc.close();
                rsDoc = null;
                pstmntDoc = null;
            }
            if (arrcRetAplCont.size() == 0) {
                iCambiaPadre = CambiaDocAplicado(conn, nFolioDocumento, fMovimiento, iInserPoliza, cTablaPadre, cTipoPoliza, cFolio, mTotalCargo, mTotalAbono);
                int iRegreso = CambiaPoliza(conn, nFolioDocumento, fMovimiento, iInserPoliza, cTablaPadre, cTipoPoliza, cFolio, mTotalCargo, mTotalAbono);
                if (iCambiaPadre == 0 || iRegreso == 0) {
                    arrcRetAplCont.add("Error al modificar el documento despues de aplicarlo");
                    log.error("Error al modificar el documento despues de aplicarlo");
                    conn.rollback();
                } else {
                    if (com.syc.contable.util.Math.truncate(mTotalCargo, 2) == com.syc.contable.util.Math.truncate(mTotalAbono, 2)) {
                        if (c != null) {
                            CasoDato cd = new CasoDato();
                            cd.setIdCaso(c.getIdCaso());
                            cd.setIdCD(8);
                            cd.setIdTC(c.getIdTC());
                            cd.setValor("true");
                            CasoDatoManager cdm = new CasoDatoManager();
                            cdm.update(conn, cd);
                        }
                        conn.commit();
                        // retVal = cTipoDocumento + " APLICADO CONTABLEMENTE";
                        log.info("DOCUMENTO DE " + cTipoDocumento + " APLICADO CONTABLEMENTE");
                        arrcRetAplCont.add("DOCUMENTO DE " + cTipoDocumento + " APLICADO CONTABLEMENTE");
                    } else {
                        log.warn("Documento no cuadra Cargo:" + mTotalCargo + " == Abono:" + mTotalAbono);
                        conn.rollback();
                        arrcRetAplCont.add("Documento no cuadra  Cargo:" + mTotalCargo + " == Abono:" + mTotalAbono);
                    }
                }
            } else {
                conn.rollback();
            }
        } catch (Exception e) {
            arrcRetAplCont.add("No se logró aplicación contable del docuemnto de " + cTipoDocumento + " debido a:" + e.toString());
            log.error("No se logró aplicación contable del docuemnto de " + cTipoDocumento + " debido a:", e);
            try {
                conn.rollback();
            } catch (Exception e2) {
                log.warn("En rollback", e2);
            }
        } finally {
            if (conn != null)
                try {
                    // conn.close();
                } catch (Exception e2) {
                    log.warn("Cerrando conexion a base de datos", e2);
                }
        }
        log.info("Cerrando la aplicacion contable");
        return arrcRetAplCont;
    }

    public ArrayList<String[]> ObtieneCuentaContable(Connection conn, String cTablaHija, int nFolioDocumento, String cFolio, int iRenglonFolio, String cEventoID, int ilongitud_bloque, String aEjercicioFiscal) throws SQLException {
        ArrayList arrnumCuenta = new ArrayList();
        ArrayList arrnumLocalCuenta = new ArrayList();
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        PreparedStatement pstmntSc = null;
        ResultSet rsSc = null;
        PreparedStatement pstmntAplCont = null;
        ResultSet rsAplCont = null;
        PreparedStatement pstmntAC = null;
        ResultSet rsAC = null;
        String cSubcuenta = "";
        String nCuenta = "";
        int nDocRenglon = 0;
        String dComponente = "";
        String dDetalleCuenta = "";
        String cNumCuentaDetalle = "";
        int nIntWhile = 0;
        int cDinamica = 0;
        String dCuentaDinamica = "";
        String cSqlQueryDefinition = "";
        String cNbloqueCuenta = "";
        String TipoCuenta = "";
        int nBloqueactual = 0;
        int iNumeroCuentas = 0;
        String cSQLSentence = "";
        try {
            cSQLSentence = "SELECT ec.nCuenta,ec.nDocRenglon,ec.TipoCuenta,ec.dComponente, isnull(c.cSubcuenta,'') as cSubcuenta, VerificaSaldo, NaturalezaCuenta, AplicacionCuenta ";
            cSQLSentence += "  FROM tEventoConfiguracion ec WITH (NOLOCK), tCuentas c  WITH (NOLOCK) where ec.nCuenta = c.nCuenta  AND ec.cEvento = '" + cEventoID + "'";
            cSQLSentence += "   AND ec.aEjercicioFiscal = " + aEjercicioFiscal + " ORDER BY nDocRenglon";
            pstmnt = conn.prepareStatement(cSQLSentence);
            rs = pstmnt.executeQuery();
            cSQLSentence = "";
            while (rs.next()) {
                nCuenta = rs.getString("nCuenta");
                nDocRenglon = rs.getInt("nDocRenglon");
                TipoCuenta = rs.getString("TipoCuenta");
                dComponente = rs.getString("dComponente");
                cSubcuenta = rs.getString("cSubcuenta");
                iNumeroCuentas++;
                try {
                    cSQLSentence = "SELECT nOrden,dDetalleCuenta from tEventoConfiguraDetalle WITH (NOLOCK) where cEvento = '" + cEventoID + "' AND nCuenta ='" + nCuenta + "' AND aEjercicioFiscal = '" + aEjercicioFiscal + "' AND nDocRenglon = " + nDocRenglon + "  order by nOrden";
                    pstmntSc = conn.prepareStatement(cSQLSentence);
                    rsSc = pstmntSc.executeQuery();
                    nBloqueactual = 0;
                    nIntWhile = 0;
                    cNumCuentaDetalle = "";
                    while (rsSc.next()) {
                        dDetalleCuenta = rsSc.getString("dDetalleCuenta");
                        nBloqueactual++;
                        cNbloqueCuenta = nCuenta.substring(nIntWhile, ((ilongitud_bloque * nBloqueactual) + (nBloqueactual - 1)));
                        if (dDetalleCuenta.equals(cNbloqueCuenta)) {
                            if (cNumCuentaDetalle.length() > 0)
                                cNumCuentaDetalle = cNumCuentaDetalle + '-' + dDetalleCuenta;
                            else
                                cNumCuentaDetalle = dDetalleCuenta;
                            nIntWhile++;
                        } else {
                            nIntWhile++;
                            try {
                                cSqlQueryDefinition = "Select " + dDetalleCuenta + " as cDinamica, " + dComponente + " as nImporte from " + cTablaHija + " WITH (NOLOCK) where " + cFolio + " = " + nFolioDocumento + " and nDocRenglon = " + iRenglonFolio;
                                pstmntAplCont = conn.prepareStatement(cSqlQueryDefinition);
                                rsAplCont = pstmntAplCont.executeQuery();
                                if (rsAplCont.next()) {
                                    cDinamica = rsAplCont.getInt(1);
                                    cSqlQueryDefinition = "Select Right(replicate('0', " + ilongitud_bloque + ") + convert(varchar(" + ilongitud_bloque + "), " + cDinamica + "), " + ilongitud_bloque + ") as dCuentaDinamica from tEjercicioFiscal WITH (NOLOCK)";
                                    pstmntAC = conn.prepareStatement(cSqlQueryDefinition);
                                    rsAC = pstmntAC.executeQuery();
                                    if (rsAC.next()) {
                                        dCuentaDinamica = rsAC.getString("dCuentaDinamica");
                                        cNumCuentaDetalle = cNumCuentaDetalle + '-' + dCuentaDinamica;
                                    }
                                }
                            } finally {
                                if (rsAC != null)
                                    rsAC.close();
                                if (rsAplCont != null)
                                    rsAplCont.close();
                                if (pstmntAC != null)
                                    pstmntAC.close();
                                if (pstmntAplCont != null)
                                    pstmntAplCont.close();
                                rsAC = null;
                                pstmntAC = null;
                                pstmntAplCont = null;
                                rsAplCont = null;
                            }
                        }
                        nIntWhile += ilongitud_bloque;
                    }
                    if ("".equals(cNumCuentaDetalle)) {
                        cNumCuentaDetalle = nCuenta;
                    }
                    arrnumCuenta.add(new String[] { dComponente, cSubcuenta, cNumCuentaDetalle, TipoCuenta });
                } finally {
                    if (rsSc != null)
                        rsSc.close();
                    if (pstmntSc != null)
                        pstmntSc.close();
                    rsSc = null;
                    pstmntSc = null;
                }
            }
            if (iNumeroCuentas == 0) {
                throw new SQLException("No existen Configuración de Eventos(" + cEventoID + ") para el Documento (" + cTablaHija.replace("Detalle", "") + ")");
            }
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            if (rsSc != null)
                rsSc.close();
            if (pstmntSc != null)
                pstmntSc.close();
            if (rsSc != null)
                rsSc.close();
            if (pstmntSc != null)
                pstmntSc.close();
            if (rsAC != null)
                rsAC.close();
            if (rsAplCont != null)
                rsAplCont.close();
            if (pstmntAC != null)
                pstmntAC.close();
            if (pstmntAplCont != null)
                pstmntAplCont.close();
            rsAC = null;
            pstmntAC = null;
            pstmntAplCont = null;
            rsAplCont = null;
            rsSc = null;
            pstmntSc = null;
            rsSc = null;
            pstmntSc = null;
            rs = null;
            pstmnt = null;
        }
        return arrnumCuenta;
    }

    public int CreaPoliza(Connection conn, int nFolioDoc, String nMes, String cDescripcionPoliza, String cTipoDocumento, String nPolizaAutomatica, String cTipoPoliza, String fAplica, String aEjercicioFiscal, String cCentroContable) throws SQLException {
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        int nFolioPoliza = 0;
        String cSQLStaiment = "";
        try {
            cSQLStaiment = "SELECT max(nFolioPoliza)+1 FROM tPoliza WITH (NOLOCK) where cCentroContable='" + cCentroContable + "' AND cTipoPoliza = '" + cTipoPoliza + "' AND aEjercicioFiscal= '" + aEjercicioFiscal + "'";
            pstmnt = conn.prepareStatement(cSQLStaiment);
            rs = pstmnt.executeQuery();
            if (rs.next())
                nFolioPoliza = rs.getInt(1);
            cSQLStaiment = "";
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            rs = null;
            pstmnt = null;
        }
        try {
            cSQLStaiment = "Insert into tPoliza (nFolioPoliza,cTipoPoliza, cCentroContable,fCreacion,cDescripcionPoliza,fAplicacion,nPolizaAutomatica,cTipoDocumento,nFolioDocumento,aEjercicioFiscal,nMes) values(";
            cSQLStaiment += nFolioPoliza + ",'" + cTipoPoliza + "','" + cCentroContable + "',getDate(),'" + cDescripcionPoliza + "',convert(date,'" + fAplica + "',103),'" + nPolizaAutomatica + "','" + cTipoDocumento + "','" + nFolioDoc + "','" + aEjercicioFiscal + "','" + nMes + "')";
            pstmnt = conn.prepareStatement(cSQLStaiment);
            pstmnt.executeUpdate();
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        return nFolioPoliza;
    }

    public int CreaMovimientos(Connection conn, int nFolioDoc, int nDocRenglon, String nMes, String cRamo, String cUnidadResponsable, int nFolioPoliza, String cTipoDocumento, String nCuenta, String cSubcuenta, double mMovimiento, String cTipoMovimiento, String fMovimiento, String cTipoPoliza, String aEjercicioFiscal, String cMoneda, String dConceptoMovimiento, String cCentroContable) throws SQLException {
        PreparedStatement pstmnt = null;
        int retVar = 0;
        String cFolioDocumentoMovimiento = "";
        String cSQLSentence = "";
        // cFolioDocumentoMovimiento = cRamo.trim() + "." +
        // cUnidadResponsable.trim() + "." + nFolioDoc;
        cFolioDocumentoMovimiento = "" + nFolioDoc;
        try {
            cSQLSentence = "insert into tMovimiento (cTipoDocumento,cRamo,cUnidadResponsable, nCuenta, nSubCuenta,cTipoMovimiento, fOperacionMovimiento, cFolioDocumentoMovimiento,fMovimiento, dConceptoMovimiento, cMoneda, nFolioPoliza, cTipoPoliza, cCentroContable, aEjercicioFiscal,mMovimiento,nDocRenglon) values (";
            cSQLSentence += "'" + cTipoDocumento + "','" + cRamo.trim() + "','" + cUnidadResponsable.trim() + "','" + nCuenta + "','" + cSubcuenta + "','" + cTipoMovimiento + "',getDate(),'" + cFolioDocumentoMovimiento + "',convert(date,'" + fMovimiento + "',103),'" + dConceptoMovimiento + "','" + cMoneda + "'," + nFolioPoliza + ",'" + cTipoPoliza + "','" + cCentroContable + "','" + aEjercicioFiscal + "'," + mMovimiento + ",'" + nDocRenglon + "');";
            pstmnt = conn.prepareStatement(cSQLSentence);
            retVar = pstmnt.executeUpdate();
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        return retVar;
    }

    public int CambiaDocAplicado(Connection conn, int nFolioDoc, String fMovimiento, int nFolioPoliza, String cTablaPadre, String cTipoPoliza, String cFolio, double mTotalCargo, double mTotalAbono) throws SQLException {
        PreparedStatement pstmnt = null;
        int retVar = 0;
        String cSqlQueryDefinition = "";
        try {
            cSqlQueryDefinition = "Update " + cTablaPadre + " WITH (ROWLOCK) set fAplicacion= convert(date,'" + fMovimiento + "',103), cDocumentoHaplicado='S', nFolioPoliza=" + nFolioPoliza + ", cTipoPoliza='" + cTipoPoliza + "' WHERE " + cFolio + "=" + nFolioDoc;
            pstmnt = conn.prepareStatement(cSqlQueryDefinition);
            // pstmnt.setString(1, fMovimiento);
            retVar = pstmnt.executeUpdate();
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        return retVar;
    }

    public int CambiaPoliza(Connection conn, int nFolioDoc, String fMovimiento, int nFolioPoliza, String cTablaPadre, String cTipoPoliza, String cFolio, double mTotalCargo, double mTotalAbono) throws SQLException {
        PreparedStatement pstmnt = null;
        int retVar = 0;
        try {
            pstmnt = conn.prepareStatement("Update tPoliza WITH (ROWLOCK) set mTotalCargo = ?, mTotalAbono=? WHERE nFolioPoliza=? ");
            // log.debug("Update tPoliza set mTotalCargo = ?, mTotalAbono=?
            // WHERE nFolioPoliza=? ["
            // + mTotalCargo + ", " + mTotalAbono + ", " + nFolioPoliza + "]");
            pstmnt.setDouble(1, mTotalCargo);
            pstmnt.setDouble(2, mTotalAbono);
            pstmnt.setInt(3, nFolioPoliza);
            retVar = pstmnt.executeUpdate();
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        return retVar;
    }

    public String CreaCambiaSaldos(Connection conn, String cRamo, String cUnidadResponsable, String nCuenta, String cSubCuenta, String aEjercicioFiscal, String cTipoMovimiento, String nMesApl, String cMoneda, double iImporte, String cTipoDocumento, String cCentroContable, String cTipoOperacion, String cValidaSaldo, String cNaturalezaCuenta, String cAplicacionCuenta) throws SQLException {
        PreparedStatement pstmnt = null;
        String cMesnsaje = "";
        ResultSet rs = null;
        int nExsisteSaldo = 0;
        int nExisteRegSaldo = 0;
        int retVar = 0;
        String VerificaSaldo = "", NaturalezaCuenta = "", AplicacionCuenta = "";
        String cHaberDeber = "";
        String cMesSaldo = "";
        int nMesArrastre = 0;
        int nMes = new Integer(nMesApl).intValue();
        String cSqlQueryDefinition = "";
        if ((!cTipoDocumento.equals("PRESUPUESTO"))) {
            try {
                /*
				 * if (cSubCuenta.isEmpty() || cSubCuenta == null) { pstmnt =
				 * conn.prepareStatement(
				 * "SELECT nMesArrastre FROM tsaldos WITH (NOLOCK) WHERE aEjercicioFiscal = ?"
				 * +
				 * " AND cRamo = ?  AND cUnidadResponsable = ? AND nCuenta = ? AND cCentroContable = ?"
				 * ); pstmnt.setString(1, aEjercicioFiscal); pstmnt.setString(2,
				 * cRamo); pstmnt.setString(3, cUnidadResponsable);
				 * pstmnt.setString(4, nCuenta); pstmnt.setString(5,
				 * cCentroContable);
				 * 
				 * } else { cSqlQueryDefinition =
				 * "SELECT nMesArrastre FROM tsaldos WITH (NOLOCK) WHERE aEjercicioFiscal = '"
				 * +aEjercicioFiscal+"'" + " AND cRamo = '"
				 * +cRamo+"' AND cUnidadResponsable = '"
				 * +cUnidadResponsable+"' AND nCuenta = '"+nCuenta+
				 * "' AND cCentroContable = '"
				 * +cCentroContable+"' AND cSubCuenta = '"+cSubCuenta+"'";
				 * pstmnt = conn.prepareStatement(cSqlQueryDefinition); //pstmnt
				 * = conn.prepareStatement(
				 * "SELECT nMesArrastre FROM tsaldos WITH (NOLOCK) WHERE aEjercicioFiscal = ?"
				 * // +
				 * " AND cRamo = ?  AND cUnidadResponsable = ? AND nCuenta = ? AND cCentroContable = ? AND cSubCuenta = ?"
				 * ); pstmnt.setString(1, aEjercicioFiscal); pstmnt.setString(2,
				 * cRamo); pstmnt.setString(3, cUnidadResponsable);
				 * pstmnt.setString(4, nCuenta); pstmnt.setString(5,
				 * cSubCuenta); pstmnt.setString(6, cCentroContable);
				 */
                cMesSaldo = "mSaldo" + nMes;
                cSqlQueryDefinition = "SELECT CASE nMesArrastre WHEN " + nMes + " THEN CASE 'D' when '" + NaturalezaCuenta + "' THEN  CASE 'C'  when '" + cTipoMovimiento + "' then " + cMesSaldo + " + " + iImporte + " else  " + cMesSaldo + " - " + iImporte + " end ";
                cSqlQueryDefinition += "  when 'A'  THEN  CASE 'A'  when '" + cTipoMovimiento + "' then " + cMesSaldo + " + " + iImporte + " else  " + cMesSaldo + " - " + iImporte + " end  end ";
                cSqlQueryDefinition += " else CASE  '" + NaturalezaCuenta + "'   when 'D'  THEN  CASE '" + cTipoMovimiento + "'  when 'C' then mSaldoArrastre + " + iImporte + "  else  mSaldoArrastre - " + iImporte + " end ";
                cSqlQueryDefinition += " when 'A'  THEN  CASE 'A'  when '" + cTipoMovimiento + "' then mSaldoArrastre + " + iImporte + " else  mSaldoArrastre - " + iImporte + " end  end ";
                cSqlQueryDefinition += " end , nMesArrastre ";
                cSqlQueryDefinition += " FROM tsaldos WITH (NOLOCK) WHERE aEjercicioFiscal = '" + aEjercicioFiscal + "' AND cRamo = '" + cRamo + "' AND cUnidadResponsable = '" + cUnidadResponsable + "'";
                cSqlQueryDefinition += " AND nCuenta = '" + nCuenta + "' AND cSubCuenta = '" + cSubCuenta + "' AND cCentroContable = '" + cCentroContable + "'";
                pstmnt = conn.prepareStatement(cSqlQueryDefinition);
                // }
                rs = pstmnt.executeQuery();
                if (rs.next()) {
                    nExsisteSaldo = rs.getInt(1);
                    nMesArrastre = rs.getInt(2);
                    nExisteRegSaldo = 1;
                } else {
                    nExisteRegSaldo = 0;
                    nExsisteSaldo = 0;
                    nMesArrastre = nMes;
                }
            } finally {
            }
        } else {
            nExisteRegSaldo = 0;
            nExsisteSaldo = 0;
            nMesArrastre = nMes;
        }
        VerificaSaldo = cValidaSaldo;
        NaturalezaCuenta = cNaturalezaCuenta;
        if ("N".equals(cAplicacionCuenta)) {
            log.error("Error en CreaCambiaSaldos: la Cuenta no es de Aplicaci&ocute;n");
            cMesnsaje = "Error en CreaCambiaSaldos: la Cuenta no es de Aplicaci&ocute;n";
            retVar = -1;
        }
        if (nExisteRegSaldo > 0) {
            try {
                if (cAplicacionCuenta.equals("S")) {
                    if (VerificaSaldo.equals("S")) {
                        /*
						 * try { cMesSaldo = "mSaldo" + nMes;
						 * cSqlQueryDefinition =
						 * "SELECT CASE nMesArrastre WHEN "
						 * +nMes+" THEN CASE 'D' when '"
						 * +NaturalezaCuenta+"' THEN  CASE 'C'  when '"
						 * +cTipoMovimiento+"' then "+cMesSaldo+" + " + iImporte
						 * + " else  "+cMesSaldo+" - " + iImporte + " end ";
						 * cSqlQueryDefinition +=
						 * "  when 'A'  THEN  CASE 'A'  when '"
						 * +cTipoMovimiento+"' then "+cMesSaldo+" + " + iImporte
						 * + " else  "+cMesSaldo+" - " + iImporte +
						 * " end  end "; cSqlQueryDefinition +=
						 * " else CASE  '"+NaturalezaCuenta
						 * +"'   when 'D'  THEN  CASE '"
						 * +cTipoMovimiento+"'  when 'C' then mSaldoArrastre + "
						 * + iImporte + "  else  mSaldoArrastre - " + iImporte +
						 * " end "; cSqlQueryDefinition +=
						 * " when 'A'  THEN  CASE 'A'  when '"
						 * +cTipoMovimiento+"' then mSaldoArrastre + " +
						 * iImporte + " else  mSaldoArrastre - " + iImporte +
						 * " end  end "; cSqlQueryDefinition +=
						 * " end , nMesArrastre "; cSqlQueryDefinition +=
						 * " FROM tsaldos WITH (NOLOCK) WHERE aEjercicioFiscal = '"
						 * + aEjercicioFiscal + "' AND cRamo = '" + cRamo +
						 * "' AND cUnidadResponsable = '" + cUnidadResponsable +
						 * "'"; cSqlQueryDefinition += " AND nCuenta = '" +
						 * nCuenta + "' AND cSubCuenta = '" + cSubCuenta +
						 * "' AND cCentroContable = '" + cCentroContable + "'";
						 * pstmnt = conn.prepareStatement(cSqlQueryDefinition);
						 * rs = pstmnt.executeQuery(); if (rs.next()){
						 * nExsisteSaldo = rs.getInt(1); nMesArrastre =
						 * rs.getInt(2); } } finally { }
						 */
                        if ("A".equals(cTipoOperacion) && nExsisteSaldo < 0) {
                            nExsisteSaldo = 1;
                        }
                        if (nExsisteSaldo < 0 && (cTipoOperacion != "Cancelacion")) {
                            cMesnsaje = "Error en CreaCambiaSaldos: Saldo insuficiente en la Clave EP:" + cSubCuenta + " para la Cuenta:" + nCuenta + "por la Cantidad:" + iImporte;
                            log.error("Error en CreaCambiaSaldos: Saldo insuficiente en la Clave EP:" + cSubCuenta + " para la Cuenta:" + nCuenta + "por la Cantidad:" + iImporte);
                            retVar = -1;
                        } else {
                            nExsisteSaldo = 1;
                        }
                    }
                    try {
                        if (nMes == nMesArrastre || nMes > nMesArrastre) {
                            int nMesArrastrar = nMesArrastre + 1;
                            String mSaldoArrastrar = "mSaldo" + nMesArrastre;
                            while (nMesArrastrar <= nMes) {
                                cMesSaldo = "mSaldo" + nMesArrastrar;
                                cSqlQueryDefinition = "UPDATE tSaldos WITH (ROWLOCK) set  ";
                                cSqlQueryDefinition += " mSaldo" + nMesArrastrar + " =  " + mSaldoArrastrar;
                                cSqlQueryDefinition += " WHERE aEjercicioFiscal = '" + aEjercicioFiscal + "' AND cRamo = '" + cRamo + "' AND cUnidadResponsable = '" + cUnidadResponsable + "' AND  nCuenta = '" + nCuenta + "' AND cSubCuenta = '" + cSubCuenta + "' AND cCentroContable = '" + cCentroContable + "'";
                                pstmnt = conn.prepareStatement(cSqlQueryDefinition);
                                retVar = pstmnt.executeUpdate();
                                nMesArrastrar++;
                            }
                            cMesSaldo = "mSaldo" + nMes;
                            cSqlQueryDefinition = "UPDATE tSaldos WITH (ROWLOCK) set  ";
                            if (cTipoMovimiento.equals("A")) {
                                if (NaturalezaCuenta.equals("D")) {
                                    cSqlQueryDefinition += " mSaldo" + nMes + " =  mSaldo" + nMes + " - " + iImporte + ", mSaldoArrastre = mSaldoArrastre - " + iImporte;
                                } else {
                                    cSqlQueryDefinition += " mSaldo" + nMes + " =  mSaldo" + nMes + " + " + iImporte + ", mSaldoArrastre = mSaldoArrastre + " + iImporte;
                                }
                                cSqlQueryDefinition += ", mHaber" + nMes + " = mHaber" + nMes + " + " + iImporte;
                            } else if (cTipoMovimiento.equals("C")) {
                                if (NaturalezaCuenta.equals("D")) {
                                    cSqlQueryDefinition += " mSaldo" + nMes + " =  mSaldo" + nMes + " + " + iImporte + ", mSaldoArrastre = mSaldoArrastre + " + iImporte;
                                } else {
                                    cSqlQueryDefinition += " mSaldo" + nMes + " =  mSaldo" + nMes + " - " + iImporte + ", mSaldoArrastre = mSaldoArrastre - " + iImporte;
                                }
                                cSqlQueryDefinition += ", mDeber" + nMes + " = mDeber" + nMes + " + " + iImporte;
                            }
                            cSqlQueryDefinition += ", nMesArrastre = " + nMes + " WHERE aEjercicioFiscal = '" + aEjercicioFiscal + "' AND cRamo = '" + cRamo + "' AND cUnidadResponsable = '" + cUnidadResponsable + "' AND  nCuenta = '" + nCuenta + "' AND cSubCuenta = '" + cSubCuenta + "' AND cCentroContable = '" + cCentroContable + "'";
                            pstmnt = conn.prepareStatement(cSqlQueryDefinition);
                            retVar = pstmnt.executeUpdate();
                        } else if (nMes < nMesArrastre) {
                            cSqlQueryDefinition = "UPDATE tSaldos WITH (ROWLOCK) set  ";
                            if (cTipoMovimiento.equals("A")) {
                                if (NaturalezaCuenta.equals("D")) {
                                    cSqlQueryDefinition += " mSaldo" + nMes + " =  mSaldo" + nMes + " - " + iImporte + ", mSaldoArrastre = mSaldoArrastre - " + iImporte;
                                } else {
                                    cSqlQueryDefinition += " mSaldo" + nMes + " =  mSaldo" + nMes + " + " + iImporte + ", mSaldoArrastre = mSaldoArrastre + " + iImporte;
                                }
                                cSqlQueryDefinition += ", mHaber" + nMes + " = mHaber" + nMes + " + " + iImporte;
                            } else if (cTipoMovimiento.equals("C")) {
                                if (NaturalezaCuenta.equals("D")) {
                                    cSqlQueryDefinition += " mSaldo" + nMes + " =  mSaldo" + nMes + " + " + iImporte + ", mSaldoArrastre = mSaldoArrastre + " + iImporte;
                                } else {
                                    cSqlQueryDefinition += " mSaldo" + nMes + " =  mSaldo" + nMes + " - " + iImporte + ", mSaldoArrastre = mSaldoArrastre - " + iImporte;
                                }
                                cSqlQueryDefinition += ", mDeber" + nMes + " = mDeber" + nMes + " + " + iImporte;
                            }
                            cSqlQueryDefinition += " WHERE aEjercicioFiscal = '" + aEjercicioFiscal + "' AND cRamo = '" + cRamo + "' AND cUnidadResponsable = '" + cUnidadResponsable + "' AND  nCuenta = '" + nCuenta + "' AND cSubCuenta = '" + cSubCuenta + "' AND cCentroContable = '" + cCentroContable + "'";
                            pstmnt = conn.prepareStatement(cSqlQueryDefinition);
                            retVar = pstmnt.executeUpdate();
                            int nMesArrastrar = nMes + 1;
                            while (nMesArrastrar <= nMesArrastre) {
                                /*
								 * Nos falta validad que los meses siguientes no
								 * se queden en negativo para cuando se aplican
								 * en fecha anteriores 16-04-2012 if
								 * (VerificaSaldo.equals("S")) { try { cMesSaldo
								 * = "mSaldo" + nMes; cSqlQueryDefinition =
								 * "SELECT CASE nMesArrastre WHEN "
								 * +nMes+" THEN CASE 'D' when '"
								 * +NaturalezaCuenta
								 * +"' THEN  CASE 'C'  when '"+cTipoMovimiento
								 * +"' then "+cMesSaldo+" + " + iImporte +
								 * " else  "+cMesSaldo+" - " + iImporte +
								 * " end "; cSqlQueryDefinition +=
								 * "  when 'A'  THEN  CASE 'A'  when '"
								 * +cTipoMovimiento+"' then "+cMesSaldo+" + " +
								 * iImporte + " else  "+cMesSaldo+" - " +
								 * iImporte + " end  end "; cSqlQueryDefinition
								 * += " else CASE  '"+NaturalezaCuenta+
								 * "'   when 'D'  THEN  CASE '"+cTipoMovimiento+
								 * "'  when 'C' then mSaldoArrastre + " +
								 * iImporte + "  else  mSaldoArrastre - " +
								 * iImporte + " end "; cSqlQueryDefinition +=
								 * " when 'A'  THEN  CASE 'A'  when '"
								 * +cTipoMovimiento+"' then mSaldoArrastre + " +
								 * iImporte + " else  mSaldoArrastre - " +
								 * iImporte + " end  end "; cSqlQueryDefinition
								 * += " end , nMesArrastre ";
								 * cSqlQueryDefinition +=
								 * " FROM tsaldos WITH (NOLOCK) WHERE aEjercicioFiscal = '"
								 * + aEjercicioFiscal + "' AND cRamo = '" +
								 * cRamo + "' AND cUnidadResponsable = '" +
								 * cUnidadResponsable + "'"; cSqlQueryDefinition
								 * += " AND nCuenta = '" + nCuenta +
								 * "' AND cSubCuenta = '" + cSubCuenta +
								 * "' AND cCentroContable = '" + cCentroContable
								 * + "'"; pstmnt =
								 * conn.prepareStatement(cSqlQueryDefinition);
								 * rs = pstmnt.executeQuery(); if (rs.next()){
								 * nExsisteSaldo = rs.getInt(1); nMesArrastre =
								 * rs.getInt(2); } } finally { } if
								 * (nExsisteSaldo < 0 && cTipoOperacion !=
								 * "Cancelacion") { cMesnsaje =
								 * "Error en CreaCambiaSaldos: Saldo insuficiente en la Clave EP:"
								 * + cSubCuenta + " para la Cuenta:" + nCuenta +
								 * "por la Cantidad:" + iImporte; log.error(
								 * "Error en CreaCambiaSaldos: Saldo insuficiente en la Clave EP:"
								 * + cSubCuenta + " para la Cuenta:" + nCuenta +
								 * "por la Cantidad:" + iImporte); retVar = -1;
								 * }
								 */
                                cMesSaldo = "mSaldo" + nMesArrastrar;
                                cSqlQueryDefinition = "UPDATE tSaldos WITH (ROWLOCK) set  ";
                                if (cTipoMovimiento.equals("A")) {
                                    if (NaturalezaCuenta.equals("D")) {
                                        cSqlQueryDefinition += " mSaldo" + nMesArrastrar + " =  mSaldo" + nMesArrastrar + " - " + iImporte;
                                    } else {
                                        cSqlQueryDefinition += " mSaldo" + nMesArrastrar + " =  mSaldo" + nMesArrastrar + " + " + iImporte;
                                    }
                                } else if (cTipoMovimiento.equals("C")) {
                                    if (NaturalezaCuenta.equals("D")) {
                                        cSqlQueryDefinition += " mSaldo" + nMesArrastrar + " =  mSaldo" + nMesArrastrar + " + " + iImporte;
                                    } else {
                                        cSqlQueryDefinition += " mSaldo" + nMesArrastrar + " =  mSaldo" + nMesArrastrar + " - " + iImporte;
                                    }
                                }
                                cSqlQueryDefinition += " WHERE aEjercicioFiscal = '" + aEjercicioFiscal + "' AND cRamo = '" + cRamo + "' AND cUnidadResponsable = '" + cUnidadResponsable + "' AND  nCuenta = '" + nCuenta + "' AND cSubCuenta = '" + cSubCuenta + "' AND cCentroContable = '" + cCentroContable + "'";
                                pstmnt = conn.prepareStatement(cSqlQueryDefinition);
                                retVar = pstmnt.executeUpdate();
                                nMesArrastrar++;
                            }
                        }
                    } finally {
                    }
                }
            } finally {
                if (rs != null)
                    rs.close();
                if (pstmnt != null)
                    pstmnt.close();
                rs = null;
                pstmnt = null;
            }
        } else {
            if (cTipoMovimiento.equals("A")) {
                cHaberDeber = "mHaber" + nMes;
            } else if (cTipoMovimiento.equals("C")) {
                cHaberDeber = "mDeber" + nMes;
            }
            if (VerificaSaldo.equals("S")) {
                if (cTipoMovimiento.equals("A")) {
                    if (NaturalezaCuenta.equals("D")) {
                        if ((-1 * iImporte) < 0) {
                            nExsisteSaldo = -1;
                        }
                    }
                } else if (cTipoMovimiento.equals("C")) {
                    if (NaturalezaCuenta.equals("A")) {
                        if ((-1 * iImporte) < 0) {
                            nExsisteSaldo = -1;
                        }
                    }
                }
                if (nExsisteSaldo < 0 && cTipoOperacion != "Cancelacion") {
                    cMesnsaje = "Error en CreaCambiaSaldos: Saldo insuficiente en la Clave EP:" + cSubCuenta + " para la Cuenta:" + nCuenta + " por la Cantidad:" + iImporte;
                    log.error("Error en CreaCambiaSaldos: Saldo insuficiente en la Clave EP:" + cSubCuenta + " para la Cuenta:" + nCuenta + " por la Cantidad:" + iImporte);
                    retVar = -1;
                } else {
                    nExsisteSaldo = 1;
                }
            }
            cMesSaldo = "mSaldo" + nMes;
            try {
                cSqlQueryDefinition = "INSERT INTO tSaldos(cRamo,cUnidadResponsable,nCuenta,cSubCuenta,nMesArrastre,mSaldoArrastre," + cHaberDeber + ", " + cMesSaldo + ",nMesPrimerMovimiento,cMoneda,aEjercicioFiscal, cCentroContable ) values ('" + cRamo.trim() + "', '" + cUnidadResponsable + "', '" + nCuenta + "', '" + cSubCuenta + "', " + nMes + ",";
                if (cTipoMovimiento.equals("A")) {
                    if (NaturalezaCuenta.equals("D")) {
                        cSqlQueryDefinition += (-1 * iImporte) + ", " + iImporte + ", " + (-1 * iImporte);
                    } else {
                        cSqlQueryDefinition += iImporte + ", " + iImporte + ", " + iImporte;
                    }
                } else if (cTipoMovimiento.equals("C")) {
                    if (NaturalezaCuenta.equals("D")) {
                        cSqlQueryDefinition += iImporte + ", " + iImporte + ", " + iImporte;
                    } else {
                        cSqlQueryDefinition += (-1 * iImporte) + ", " + iImporte + ", " + (-1 * iImporte);
                    }
                }
                cSqlQueryDefinition += ", " + nMes + ", '" + cMoneda + "', '" + aEjercicioFiscal + "', '" + cCentroContable + "' )";
                pstmnt = conn.prepareStatement(cSqlQueryDefinition);
                retVar = pstmnt.executeUpdate();
            } finally {
                if (rs != null)
                    rs.close();
                if (pstmnt != null)
                    pstmnt.close();
                rs = null;
                pstmnt = null;
            }
        }
        return cMesnsaje;
    }

    public int CambiaDocCancelado(Connection conn, int nFolioDocumento, String fMovimiento, int iIdPoliza, String cTablaPadre, String cTipoPoliza, String cFolio) throws AplicacionContableException {
        PreparedStatement pstmnt = null;
        int retVar = 0;
        String cSqlQueryDefinition = "";
        try {
            cSqlQueryDefinition = "Update " + cTablaPadre + " WITH (ROWLOCK) set  cDocumentoHaplicado='C', nFolioPoliza=" + iIdPoliza + ", cTipoPoliza='" + cTipoPoliza + "' WHERE " + cFolio + "=" + nFolioDocumento;
            pstmnt = conn.prepareStatement(cSqlQueryDefinition);
            retVar = pstmnt.executeUpdate();
        } catch (SQLException e) {
            throw new AplicacionContableException(e);
        } finally {
            try {
                if (pstmnt != null)
                    pstmnt.close();
            } catch (Exception exc) {
                log.warn("Al cerrar PreparedStatement", exc);
            }
            pstmnt = null;
        }
        return retVar;
    }

    public String CancelaMovimientos(Connection conn, String cFolioDocumentoMovimiento, int nFolioPoliza, String cTipoPoliza, String aEjercicioFiscal) throws AplicacionContableException {
        int retVar = 0;
        String retVal = "";
        String cQueryConstructor;
        PreparedStatement pstmnt = null;
        try {
            cQueryConstructor = "UPDATE  tMovimiento WITH (ROWLOCK) SET  cCancelaMovimiento='C'";
            cQueryConstructor += " WHERE nFolioPoliza = " + nFolioPoliza;
            cQueryConstructor += "   AND cTipoPoliza = '" + cTipoPoliza + "'";
            cQueryConstructor += "   AND aEjercicioFiscal = '" + aEjercicioFiscal + "'";
            pstmnt = conn.prepareStatement(cQueryConstructor);
            retVar = pstmnt.executeUpdate();
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            throw new AplicacionContableException(e);
        } finally {
            try {
                if (pstmnt != null)
                    pstmnt.close();
            } catch (Exception exc) {
                log.warn("Al cerrar PreparedStatement", exc);
            }
            pstmnt = null;
        }
        return retVal;
    }

    public boolean buscaPorExpediente(Caso c, String uLogin) {
        // TODO Auto-generated method stub
        return false;
    }

    public void onAvanzaCaso(Connection conn, String uLogin, Caso c, int idCasoOper) throws SQLException {
    }

    public void onCreateExpediente(Connection conn, String uLogin, Caso c, Aplicacion app) throws SQLException {
        // TODO Auto-generated method stub
    }

    public void onEjecutaCaso(Connection conn, String uLogin, Caso c, int idCasoOper) throws SQLException {
        // TODO Auto-generated method stub
    }

    public void onIniciaCaso(Connection conn, String uLogin, Caso c) throws SQLException {
        try {
            if (c.getIdTC() == 60 && c.getIdGabinete() <= 0) {
                c.getCasoDato("FOLIO").setValor(c.getFolio());
                c.getCasoDato("FECHA_DOCUMENTO").setValor(Util.getToday());
                c.getCasoDato("EJERCICIO_FISCAL").setValor(String.valueOf(EjercicioFiscalManager.getEjercicioFiscal(conn).getEjercicio()));
                c.getCasoDato("OPERADOR").setValor(c.getCasoOperacion(0).getResponsable());
                Aplicacion app = AplicacionManager.select(conn, c.getTipoCaso().getGavetaAsociada());
                int id_gabinete = AplicacionManager.createExpediente(conn, uLogin, c, app);
                if (id_gabinete < 0) {
                    log.error("Identificador de Gabiente invlido (< 0)");
                    throw new SQLException("Identificador de Gabiente inválido (< 0)");
                }
                c.setIdGabinete(id_gabinete);
                CasoManager.update(conn, c);
                onCreateExpediente(conn, uLogin, c, app);
            }
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    public void onRecibeDocumento(Connection conn, Caso c, Documento d, boolean isSaveEvent) throws SQLException {
        // TODO Auto-generated method stub
    }

    public void onTerminaCaso(Connection conn, String uLogin, Caso c, String observ, String[] resp, String[] oper, Map data) throws SQLException {
        if (c.getIdTC() == 4 || c.getIdTC() == 5 || c.getIdTC() == 6 || c.getIdTC() == 11 || c.getIdTC() == 21 || c.getIdTC() == 76) {
            TipoCasoInterface ei = new EgresosInterface();
            ei.onTerminaCaso(conn, uLogin, c, observ, resp, oper, data);
            if (c.getIdTC() == 11) {
                try {
                    IngresoGreenMexManager.deleteEstadoCuentaDetalle(conn, c);
                } catch (Exception e) {
                    throw new SQLException(e);
                }
            }
        } else if (c.getIdTC() == 42) {
            try {
                CajaManager.descartaCaja(conn, uLogin, c);
            } catch (Exception e) {
                throw new SQLException(e);
            }
        } else if (c.getIdTC() == 60) {
            try {
                CargaPagosBoletajeManager vuelosPago = new CargaPagosBoletajeManager();
                int iRegistros = 0;
                int nFolio = Integer.parseInt(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1), 10);
                iRegistros = vuelosPago.updateLayoutVuelosDet(conn, nFolio);
                log.info("Se Actualizaron: " + iRegistros + " Registros de Vuelos.");
            } catch (Exception e) {
                throw new SQLException(e);
            }
        }
    }

    public String cancelarAppContable(Connection conn, Caso c, String cTablaPadre, String cTablaHija, String cFolio, int nFolioDocumento, String cTipoDocumento) {
        // TODO Auto-generated method stub
        return null;
    }

    public void onVenceCaso(Connection conn, String folio, int id_caso, int id_tc, int id_oper, int porc) throws SQLException {
        /* Relación de Gastos/Pago de RG con OC */
        try {
            TipoCasoInterface tci = new EgresosInterface();
            if (id_tc == 11 || id_tc == 43) {
                // Relacion de Gastos
                tci.onVenceCaso(conn, folio, id_caso, id_tc, id_oper, porc);
            } else if (id_tc == 42) {
                /* Solicitud NO Presupuestal */
                String correo = ((EgresosInterface) tci).extraeEmail(conn, id_caso, id_tc);
                /**
                 * En la version 34495 cancelaba los procesos por un error de
                 * registro en bitacora y operacion de CONAFOR se opto por que
                 * solo envie la alerta y no se cancela ningun documento
                 */
                if (StringUtils.isEmpty(correo)) {
                    correo = extraeEmailTabla(conn, id_caso);
                } else {
                    if ("true".equalsIgnoreCase(ConfiguraAplicativoManager.getSystemSetting(conn, "AMBIENTE_DESARROLLO")))
                        correo = ConfiguraAplicativoManager.getSystemSetting(conn, "CORREO_ALERTAS_DESARROLLO");
                }
                String subject, body;
                subject = "Registro de Solicitud No Presupuestal inconcluso";
                body = "<B>Atencion</b></br>" + "Se notifica que el tramite de Solicitud No Presupuestal con folio: " + folio + " esta por vencerse.<br><br>" + "Por lo anterior es necesario que verifique la informacion para concluir o en su defecto descartar el tramite del sistema, " + "ya que si no se atiende se descartara automaticamente perdiendo toda la informacion contenida en dicho tramite. <br><br>";
                log.info("Enviando Correo al siguiente destinatario: " + correo);
                AlarmaManager.procesaAlarmaCNF(conn, "", null, null, subject, correo, body);
            } else if (id_tc == 7) {
                String[] folioSeparado = folio.split("-");
                int nFolio = Integer.parseInt(folioSeparado[2]);
                if (((EgresosInterface) tci).cancelaCaso(conn, id_caso, id_tc, id_oper)) {
                    ((EgresosInterface) tci).cancelaTabla(conn, nFolio, "compromiso");
                } else
                    log.info("No se pudo cancelar el folio de Compromiso. FOLIO: " + nFolio);
            }
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
    }

    public String extraeEmailTabla(Connection conn, int id_caso) {
        String email = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "" + "SELECT u_email from tcajaencabezado caja " + "INNER JOIN CG_USUARIO us  WITH (nolock) " + "			               ON us.U_LOGIN = caja.u_login " + "			WHERE  caja.id_caso = ?";
        try {
            if ("true".equalsIgnoreCase(ConfiguraAplicativoManager.getSystemSetting(conn, "AMBIENTE_DESARROLLO")))
                return ConfiguraAplicativoManager.getSystemSetting(conn, "CORREO_ALERTAS_DESARROLLO");
            ps = conn.prepareStatement(query);
            ps.setInt(1, id_caso);
            log.info(ps);
            rs = ps.executeQuery();
            if (rs.next()) {
                email = rs.getString("U_EMAIL");
            }
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
        return email;
    }

    public void onSolicitaFirmaElectronica(Caso c, Usuario u, String reportPath) throws Exception {
        if (c.getTipoCaso().getIdTC() == 42) {
            CajaBusinessLogic caja = new CajaBusinessLogic(GestionInterface.ATT_CONEXION);
            caja.setReportPath(reportPath);
            caja.solicitaFirmaElectronica(c, u);
        }
        if (c.getTipoCaso().getIdTC() == 71) {
            ReintegrosCajaBusinessLogic reinegroCaja = new ReintegrosCajaBusinessLogic(GestionInterface.ATT_CONEXION);
            reinegroCaja.setReportPath(reportPath);
            reinegroCaja.solicitaFirmaElectronica(c, u);
        }
        return;
    }
}
