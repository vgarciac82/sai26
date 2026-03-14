package com.syc.reintcont;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.syc.contable.AdecuacionBusinessLogic;
import com.syc.contable.ContableInterface;
import com.syc.contable.core.AplicacionContable;
import com.syc.contable.core.AplicacionContable.AplicarContableReturn;
import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.core.AlarmaManager;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoManager;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class ReintegroContBussinesLogic extends DataSourceManager {

    private static Logger log = LoggerFactory.getLogger(ReintegroContBussinesLogic.class);

    public boolean correoProduccion = false;

    private String jniName = null;

    public ReintegroContBussinesLogic(String jniName) {
        super.init(jniName);
    }

    // para obtener el ejercicio fiscal en diferentes funciones
    AdecuacionBusinessLogic adecProy = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);

    public boolean actualizaFechaAplicacion(int folio, String fAplicacion) throws SQLException, ParseException {
        Connection conn = null;
        boolean res = false;
        try {
            CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
            conn = cbl.getConnection();
            res = ReintegrosContablesManager.actualizaFechaAplicacion(conn, folio, fAplicacion);
            conn.commit();
        } catch (Exception exc) {
            log.warn(exc.getMessage(), exc);
            exc.printStackTrace();
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return res;
    }

    public String ValidaReintegro(int nIdCaso, Usuario usuario, String aEjercicioFiscal, String cRamo, String cUR, Caso c, String cCentroContable, String cFechaAplica, Map<?, ?> m, String prefixPath, String uLogin) throws Exception {
        List<String> arrLResult = null;
        Connection conn = null;
        String cMensaje = "";
        try {
            String DATE_FORMAT = "dd/MM/yyyy";
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            // today
            Calendar c1 = Calendar.getInstance();
            String today = sdf.format(c1.getTime());
            AdecuacionBusinessLogic adecProy = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);
            aEjercicioFiscal = adecProy.obtenEjercicioFiscal();
            if (Integer.parseInt(aEjercicioFiscal) != c1.get(Calendar.YEAR))
                today = "31/12/" + aEjercicioFiscal;
            final int folio = new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue();
            actualizaFechaAplicacion(folio, today);
            CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
            conn = cbl.getConnection();
            ContableInterface conInt = new AplicacionContable();
            log.debug("Object: {}", "Inicia Autorización aplicacion contable " + new Timestamp(System.currentTimeMillis()));
            AplicarContableReturn acr;
            if (!"2012".equals(adecProy.obtenEjercicioFiscal()))
                acr = conInt.aplicarContableNuevo(conn, c, "tReintegroEncabezado", "tReintegroDetalle", "nFolioReintegro", new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue(), "REINTEGRO", m, prefixPath, uLogin, "");
            else
                acr = conInt.aplicarContableNuevo(conn, c, "tReintegroEncabezado", "tReintegroDetalle", "nFolioReintegro", new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue(), "REINTEGRO", m, prefixPath, uLogin, "SI");
            arrLResult = acr.getMessageList();
            log.debug("Object: {}", "Termina Autorización Aplicacion contable " + new Timestamp(System.currentTimeMillis()));
            Caso cReloaded = new Caso();
            cReloaded.setIdCaso(c.getIdCaso());
            cReloaded = CasoManager.select(conn, cReloaded);
            if (acr.isSuccess()) {
                conn.commit();
                log.debug("Avanza Caso a Autorizacion");
                cbl.avanzaCaso(cReloaded, uLogin, "", new String[] { "AUTORIZA_REINTEGROCONT" }, new String[] { "autoriza_reintegrocont" }, m, prefixPath);
            } else {
                conn.rollback();
            }
        } catch (Exception exc) {
            conn.rollback();
            log.error(exc.getMessage(), exc);
            arrLResult.add(exc.getLocalizedMessage());
            try {
                conn.rollback();
            } catch (Exception ex) {
                log.warn("En Rollback", ex);
            }
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        Iterator<String> iteraMensajes = arrLResult.iterator();
        while (iteraMensajes.hasNext()) {
            cMensaje += iteraMensajes.next();
        }
        return cMensaje;
    }

    public String AutorizaReintegroNuevo(Caso c, String nNumSicop, String cRecMotivSicop, String nNumMAP, String cRecMotivMAP, Map<?, ?> m, String prefixPath, String uLogin, Usuario usuario, String fAcredit) throws SQLException {
        //correoProduccion = true;
        //List<String> arrLResult = null;
        ArrayList<String> arrLResult = new ArrayList<String>();
        Connection conn = null;
        String cMensaje = "";
        String validaMes = "";
        CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
        String year = adecProy.obtenEjercicioFiscal();
        try {
            conn = getConnection();
            int folio = new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue();
            validaMes = ReintegrosContablesManager.validaMes(conn, c, folio, fAcredit);
            if ("S".equals(validaMes)) {
                ReintegrosContablesManager.autorizaReintegro(conn, c, nNumSicop, cRecMotivSicop, nNumMAP, cRecMotivMAP, uLogin, prefixPath, fAcredit, year);
                ReintegroContEncabezado re = ReintegrosContablesManager.getReintegroEncabezadoNuevo(conn, folio);
                //VALIDAR SI ES UN REINTEGRO TIPO 2 EJERCICIOS DIFERENTES Y SI EL PAGO TIENE AMOTIZACION PARA INSERTAR EN LA COLUMNA mAmortizacionAnticipo PARA LA AUTORIZACION
                boolean esAmortizacion = ReintegrosContablesManager.validaSIAmortiza(conn, folio);
                if (esAmortizacion) {
                    ReintegrosContablesManager.actualizaAmortizacion(conn, folio);
                }
                ContableInterface conInt = new AplicacionContable();
                log.debug("Object: {}", "Inicia Autorización aplicacion contable " + new Timestamp(System.currentTimeMillis()));
                AplicarContableReturn acr;
                if (!"2012".equals(adecProy.obtenEjercicioFiscal()))
                    acr = conInt.aplicarContableNuevo(conn, c, "tReintegroAutEncabezado", "tReintegroAutDetalle", "nFolioReintegroaut", new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue(), "REINTEGROAUT", m, prefixPath, uLogin, "");
                else
                    acr = conInt.aplicarContableNuevo(conn, c, "tReintegroAutEncabezado", "tReintegroAutDetalle", "nFolioReintegroaut", new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue(), "REINTEGROAUT", m, prefixPath, uLogin, "SI");
                //arrLResult = acr.getMessageList();
                arrLResult = (ArrayList<String>) acr.getMessageList();
                log.debug("Object: {}", "Termina Autorización Aplicacion contable " + new Timestamp(System.currentTimeMillis()));
                Calendar cal = new GregorianCalendar();
                String mesActual = Util.NOMBRE_MESES_MX[cal.get(Calendar.MONTH)];
                Caso cReloaded = new Caso();
                cReloaded.setIdCaso(c.getIdCaso());
                cReloaded = CasoManager.select(conn, cReloaded);
                if (acr.isSuccess()) {
                    conn.commit();
                    String usuarioRevisor = ReintegrosContablesManager.getCorreoRevisor(conn, c);
                    String usuariosBitacora = ReintegrosContablesManager.getListaCorreos(conn, c);
                    String to = usuarioRevisor;
                    String cc = "";
                    cc += usuariosBitacora;
                    String bcc = "";
                    String from = "";
                    String fromName = "Avisos de Reintegro";
                    String asuntoCorreo = "Aviso de Reintegro " + c.getFolio() + " (Ejercicio " + adecProy.obtenEjercicioFiscal() + ")";
                    String body = "";
                    if ("2013".equals(year))
                        body = (!correoProduccion ? "CORREO DE PRUEBA <br>" : "") + (!correoProduccion ? "este correo le hubiera llegado a: " + to + cc + "<br> <br>" : "") + "Cierre de " + mesActual + " de 2014<br><br>" + "Para su conocimiento y efectos correspondientes, se le informa que ha sido autorizado en SIAFF y SICOP el reintegro " + "por $ " + re.getImporteLC() + "  con el folio siguiente: " + c.getFolio() + "<br>" + "Mismo que ya se encuentra con estatus de autorizado en el SAI con el No. " + c.getFolio() + ", " + "para su consulta de los reportes correspondientes.<br><br>" + "Cabe mencionar que, dentro de la carpeta de 'Comprobante de Pago' deberá de estar adjuntada la siguiente documentación: <br>" + "-       Memorando dirigido  al Lic. Sergio Ramirez Rosales, indicando Ejercicio, Clc y Clave Presupuestal del reintegro<br>" + "-       Comprobante del  pago de cargas financieras, con el nombre, cargo y firma autógrafa del responsable administrativo.<br><br>" + "Y dentro de la carpeta del 'Reportes' el reporte que genera el SAI.";
                    else
                        body = (!correoProduccion ? "CORREO DE PRUEBA <br>" : "") + (!correoProduccion ? "este correo le hubiera llegado a: " + to + cc + "<br> <br>" : "") + "Cierre de " + mesActual + " de " + year + "<br><br>" + "Para su conocimiento y efectos correspondientes, se le informa que ha sido autorizado en SIAFF y SICOP el reintegro " + "por $ " + re.getImporteLC() + "  con el folio siguiente: " + c.getFolio() + "<br>" + "Mismo que ya se encuentra con estatus de autorizado en el SAI con el No. " + c.getFolio() + ", " + "para su consulta de los reportes correspondientes.<br><br>";
                    try {
                        if (!correoProduccion) {
                            to = "arlopeza@axtel.com.mx";
                            cc = "arlopeza@axtel.com.mx";
                        }
                        AlarmaManager.procesaAlarmaCNF(conn, prefixPath, c.getCasoOperacion(0), c, asuntoCorreo, to, cc, bcc, body);
                    } catch (Exception exmail) {
                        log.error("Object: {}", "No se logro enviar el correo de autorizacion de reintegros: " + exmail);
                    }
                    cbl.avanzaCaso(cReloaded, uLogin, "", new String[] { "CONSULTA_REINTEGROCONT" }, new String[] { "consulta_reintegrocont" }, m, prefixPath);
                } else {
                    conn.rollback();
                }
            } else {
                arrLResult.add("El mes de aplicacion esta cerrado contablemente, favor de notificar a contabilidad o cambiar la fecha de aplicacion.");
            }
        } catch (Exception exc) {
            conn.rollback();
            log.error(exc.getMessage(), exc);
            arrLResult.add(exc.getLocalizedMessage());
            try {
                conn.rollback();
            } catch (Exception ex) {
                log.warn("En Rollback", ex);
            }
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        Iterator<String> iteraMensajes = arrLResult.iterator();
        while (iteraMensajes.hasNext()) {
            cMensaje += iteraMensajes.next();
        }
        return cMensaje;
    }

    public String folioDependencia(String caNoContrarrecibo, String EP, int folio) throws SQLException {
        Connection conn = null;
        String res = "";
        try {
            CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
            conn = cbl.getConnection();
            res = ReintegrosContablesManager.folioDependencia(conn, caNoContrarrecibo, EP, folio);
            conn.commit();
        } catch (Exception exc) {
            log.warn(exc.getMessage(), exc);
            exc.printStackTrace();
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return res;
    }

    public String getRFC(String caNoContrarrecibo, String ep) throws SQLException {
        Connection conn = null;
        String res = "";
        try {
            CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
            conn = cbl.getConnection();
            res = ReintegrosContablesManager.getRFC(conn, caNoContrarrecibo, ep);
            conn.commit();
        } catch (Exception exc) {
            log.warn(exc.getMessage(), exc);
            exc.printStackTrace();
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return res;
    }

    public int[] getNDocRenglon(String caNoContrarrecibo, String EP, int cMes, int folio) throws SQLException {
        Connection conn = null;
        int[] res = new int[10];
        try {
            CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
            conn = cbl.getConnection();
            res = ReintegrosContablesManager.getNDocRenglon(conn, caNoContrarrecibo, EP, cMes, folio);
            conn.commit();
        } catch (Exception exc) {
            log.warn(exc.getMessage(), exc);
            exc.printStackTrace();
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return res;
    }

    public String getALM(String caNoContrarrecibo, String EP, int cMes, int folio, int renglon) throws SQLException {
        Connection conn = null;
        String res = "";
        try {
            CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
            conn = cbl.getConnection();
            res = ReintegrosContablesManager.getALM(conn, caNoContrarrecibo, EP, cMes, folio, renglon);
            conn.commit();
        } catch (Exception exc) {
            log.warn(exc.getMessage(), exc);
            exc.printStackTrace();
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return res;
    }

    public String getcPartida(String EP) throws SQLException {
        Connection conn = null;
        String res = "";
        try {
            CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
            conn = cbl.getConnection();
            res = ReintegrosContablesManager.getcPartida(conn, EP);
        } catch (Exception exc) {
            log.warn(exc.getMessage(), exc);
            exc.printStackTrace();
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return res;
    }

    public int secCLC(String caNoContrarrecibo, String EP, int folio) throws SQLException {
        Connection conn = null;
        int res = 0;
        try {
            CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
            conn = cbl.getConnection();
            res = ReintegrosContablesManager.secCLC(conn, caNoContrarrecibo, EP, folio);
        } catch (Exception exc) {
            log.warn(exc.getMessage(), exc);
            exc.printStackTrace();
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return res;
    }

    public void insertaReintegro(ReintegroContEncabezado reinE, ArrayList<ReintegroContDetalle> reinDetalles, int folio, String folioCompleto, Usuario usuario) throws Exception, SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            ReintegrosContablesManager.insertaReintegro(conn, reinE, reinDetalles, folio, folioCompleto, usuario);
            conn.commit();
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
            conn.rollback();
            throw new Exception(exc);
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
    }

    public String cancelarAppContableNuevo(Caso c, Map<?, ?> m, String prefixPath, String uLogin, String cFecha) throws Exception {
        String retVal = null;
        Connection conn = null;
        try {
            ContableInterface ci = new AplicacionContable();
            CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
            conn = cbl.getConnection();
            AplicarContableReturn acr = ci.cancelarAppContableNueva(conn, c, "", "", "", 1, "", m, prefixPath, uLogin, cFecha);
            Caso cReloaded = new Caso();
            cReloaded.setIdCaso(c.getIdCaso());
            cReloaded = CasoManager.select(conn, cReloaded);
            if (acr.isSuccess()) {
                conn.commit();
                cbl.avanzaCaso(cReloaded, uLogin, "", new String[] { "CAPTURA_REINTEGROCONT" }, new String[] { "captura_reintegrocont" }, m, prefixPath);
            } else {
                conn.rollback();
            }
            retVal = acr.getMessageList().get(acr.getMessageList().size() - 1);
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
            conn.rollback();
            throw new Exception(exc);
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return retVal;
    }

    public ReintegroContEncabezado getReintegroEncabezadoNuevo(int nFolio) throws SQLException {
        Connection conn = null;
        ReintegroContEncabezado re = new ReintegroContEncabezado();
        try {
            CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
            conn = cbl.getConnection();
            re = ReintegrosContablesManager.getReintegroEncabezadoNuevo(conn, nFolio);
        } catch (Exception exc) {
            exc.printStackTrace();
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return re;
    }

    public ReintegroContDetalle getReintegroDetalleNuevo(int nFolio) throws SQLException {
        Connection conn = null;
        ReintegroContDetalle rd = new ReintegroContDetalle();
        try {
            CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
            conn = cbl.getConnection();
            rd = ReintegrosContablesManager.getReintegroDetalleNuevo(conn, nFolio);
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
            throw exc;
        } finally {
            CloseObject.closeObject(conn);
        }
        return rd;
    }

    public boolean actualizaCtaBancaria(int nFolio, String ctab) throws SQLException {
        Connection conn = null;
        boolean res = false;
        try {
            CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
            conn = cbl.getConnection();
            res = ReintegrosContablesManager.actualizaCtaBancaria(conn, nFolio, ctab);
            conn.commit();
        } catch (Exception exc) {
            exc.printStackTrace();
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return res;
    }
}
