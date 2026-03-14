package com.syc.contable;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jfree.util.Log;
import com.syc.contable.core.AplicacionContable;
import com.syc.contable.core.AplicacionContable.AplicarContableReturn;
import com.syc.contable.core.CLCNoPagadaException;
import com.syc.contable.core.Evento;
import com.syc.contable.core.Rectificacion;
import com.syc.contable.core.RectificacionDetalle;
import com.syc.contable.core.RectificacionEncabezado;
import com.syc.contable.core.RectificacionPresupuestariaManager;
import com.syc.contable.core.ReintegrosManager;
import com.syc.contable.core.URInaccesibleException;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoManager;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

/**
 * @author Martha Aurora Sánchez Valdivieso para SYC Constructores de Sistemas
 *         SA de CV desarrollo gestion_conagua_sif México D.F. 19/07/2012
 */
public class RectificacionPresupuestariaBusinessLogic extends DataSourceManager {

    public Rectificacion rectificacion = null;

    private static Logger log = LoggerFactory.getLogger(ReintegrosBusinessLogic.class);

    AdecuacionBusinessLogic adecProy = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);

    public RectificacionPresupuestariaBusinessLogic(String jniName) {
        super.init(jniName);
    }

    public Rectificacion filtrar(String clc, String tipoCLC, String folioSICOP, String folioSAI, String folioSIAFF, String CXP) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            if ("".equals(tipoCLC)) {
                if ("".equals(folioSICOP) && "".equals(folioSAI) && "".equals(folioSIAFF))
                    rectificacion = RectificacionPresupuestariaManager.filtraCLC(conn, clc);
                if ("".equals(clc) && "".equals(folioSAI) && "".equals(folioSIAFF)) {
                    rectificacion = RectificacionPresupuestariaManager.filtraFolioSICOP(conn, folioSICOP, CXP);
                }
                if ("".equals(clc) && "".equals(folioSICOP) && "".equals(folioSIAFF)) {
                    rectificacion = RectificacionPresupuestariaManager.filtraFolioSAI(conn, folioSAI);
                }
                if ("".equals(clc) && "".equals(folioSICOP) && "".equals(folioSAI)) {
                    rectificacion = RectificacionPresupuestariaManager.filtraFolioSIAFF(conn, folioSIAFF);
                }
            }
        } catch (SQLException e) {
            if (conn != null) {
                e.printStackTrace();
                conn.rollback();
                // throw new GestionException(e.getMessage());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (conn != null) {
                conn.close();
            }
            conn = null;
        }
        return rectificacion;
    }

    public Rectificacion filtrar(String folioSICOP, String UR, String CXP, String contrarrecibo) throws URInaccesibleException, CLCNoPagadaException, Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            rectificacion = RectificacionPresupuestariaManager.filtraFolioSICOP(conn, folioSICOP, UR, CXP, contrarrecibo);
        } catch (CLCNoPagadaException e) {
            log.error(e.getMessage(), e);
            throw e;
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            throw ex;
        } finally {
            CloseObject.closeObject(conn, false);
        }
        return rectificacion;
    }

    public boolean insertTRectificacionEncabezado(int folio, String nIDCaso, String cEjercicio, String cRamo, String cUnidad, String cCentroContable, java.sql.Date fExp, java.sql.Date fApl, String conceptoRectificacion, String cTipoMovto, String nOrigenPPTO, String nMes, String oficioRectif, String ctr_int, String cTipoRectificacion, String nFolioSICOP, String caNoContrarrecibo, String cTipoPoliza, String cDescripcionPoliza, String u_login, String nFolioPoliza, String cUnidadResponsableContable, String nFolioSIAFF, String totalDebe, String totalDice, String esIP) throws Exception {
        Connection conn = null;
        boolean insertar = false;
        try {
            conn = getConnection();
            DateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
            if ("2013".equals(adecProy.obtenEjercicioFiscal())) {
                fApl = new java.sql.Date(dateFormatter.parse("31/12/2012").getTime());
            }
            insertar = RectificacionPresupuestariaManager.insertarEncabezado(conn, folio, nIDCaso, cEjercicio, cRamo, cUnidad, cCentroContable, fExp, fApl, conceptoRectificacion, cTipoMovto, nOrigenPPTO, nMes, oficioRectif, ctr_int, cTipoRectificacion, nFolioSICOP, caNoContrarrecibo, cTipoPoliza, cDescripcionPoliza, u_login, nFolioPoliza, cUnidadResponsableContable, nFolioSIAFF, totalDebe, totalDice, esIP);
            // FALTABA COMMIT, NUNCA INSERTABA ADEMÁS QUE TENÍA MAL EL NOMBRE DE
            // UNA COLUMNA FMC
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                e.printStackTrace();
                conn.rollback();
                // throw new GestionException(e.getMessage());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (conn != null) {
                conn.close();
            }
            conn = null;
        }
        return insertar;
    }

    public boolean insertRectificacionDetalle(ArrayList<RectificacionDetalle> rd) throws Exception {
        Connection conn = null;
        boolean insertar = false;
        try {
            conn = getConnection();
            insertar = RectificacionPresupuestariaManager.insertarDetalle(conn, rd);
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                e.printStackTrace();
                conn.rollback();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (conn != null) {
                conn.close();
            }
            conn = null;
        }
        return insertar;
    }

    public String aplicaRectificacion(Caso c, Map m, String prefixPath, String uLogin, Usuario usuario, String fApl) throws SQLException {
        int nIdCaso = 0;
        int id_paso = 0;
        ArrayList<String> arrLResult = new ArrayList<String>();
        //ArrayList<String> mensajesValidacion = new ArrayList<String>();
        Connection conn = null;
        String cMensaje = "";
        String cTablaEncabezado = "";
        String cTablaDetalle = "";
        String cFolio = "";
        String cTipoDocumento = "";
        CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
        String mensaje = "";
        String tipo = "Rectificacion";
        String tipoAplicacion = "";
        String validaMes = "";
        try {
            conn = getConnection();
            validaMes = RectificacionPresupuestariaManager.validaMes(conn, c, tipo, fApl);
            if ("S".equals(validaMes)) {
                mensaje = RectificacionPresupuestariaManager.validaEvento(conn, c, tipo);
                arrLResult.add(mensaje);
                if (mensaje == "") {
                    tipoAplicacion = RectificacionPresupuestariaManager.tipoRectificacion(conn, c, tipo);
                    RectificacionPresupuestariaManager.autorizaRectificacion(conn, c, uLogin, prefixPath, tipoAplicacion, fApl);
                    nIdCaso = new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue();
                    id_paso = c.getCasoOperacion(0).getIdOperacion();
                    if (nIdCaso > 0) {
                        ContableInterface conInt = new AplicacionContable();
                        log.debug("Object: {}", "Inicia aplicacion contable" + new Timestamp(System.currentTimeMillis()) + " Para  Folio:" + nIdCaso);
                        // boolean validaSaldo;
                        cTablaEncabezado = "tRectificacionAutEncabezado";
                        cTablaDetalle = "tRectificacionAutDetalle";
                        cFolio = "nFolioRectificacionAut";
                        cTipoDocumento = "RECTIFICACIONAUT";
                        AplicarContableReturn acr = conInt.aplicarContableNuevo(conn, c, cTablaEncabezado, cTablaDetalle, cFolio, nIdCaso, cTipoDocumento, m, prefixPath, uLogin, "");
                        arrLResult.addAll(acr.getMessageList());
                        log.debug("Object: {}", "Termina Aplicacion contable " + new Timestamp(System.currentTimeMillis()) + " Para  Folio:" + nIdCaso);
                        Caso cReloaded = new Caso();
                        cReloaded.setIdCaso(c.getIdCaso());
                        cReloaded = CasoManager.select(conn, cReloaded);
                        if (acr.isSuccess()) {
                            conn.commit();
                            //el boton enviar avanza el caso y esto lo duplica en cg_caso_operacion para la consulta
                            //cbl.avanzaCaso(cReloaded, uLogin, "", new String[] { "CONSULTA_RECTIFICACION" }, new String[] { "cons_rectificacion" }, m, prefixPath);
                        } else {
                            conn.rollback();
                        }
                    } else {
                        log.debug("Error. no determinado se da rollback");
                        conn.rollback();
                    }
                } else {
                    log.debug("Object: {}", mensaje);
                    //cMensaje += mensaje;
                }
            } else {
                arrLResult.add("El mes de aplicacion esta cerrado contablemente, favor de notificar a contabilidad o cambiar la fecha de aplicacion.");
            }
        } catch (Exception exc) {
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

    public String ValidaRectificacion(int nIdCaso, Usuario usuario, String aEjercicioFiscal, String cRamo, String cUR, Caso c, String cCentroContable, String cFechaAplica, Map<?, ?> m, String prefixPath, String uLogin) throws Exception {
        List<String> arrLResult = null;
        Connection conn = null;
        String cMensaje = "";
        try {
            CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
            conn = cbl.getConnection();
            ContableInterface conInt = new AplicacionContable();
            log.debug("Object: {}", "Inicia Autorización aplicacion contable " + new Timestamp(System.currentTimeMillis()));
            AplicarContableReturn acr = conInt.aplicarContableNuevo(conn, c, "tRectificacionEncabezado", "tRectificacionDetalle", "nFolioRectificacion", new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue(), "RECTIFICACION", m, prefixPath, uLogin, "");
            arrLResult = acr.getMessageList();
            log.debug("Object: {}", "Termina Autorización Aplicacion contable " + new Timestamp(System.currentTimeMillis()));
            Caso cReloaded = new Caso();
            cReloaded.setIdCaso(c.getIdCaso());
            cReloaded = CasoManager.select(conn, cReloaded);
            if (acr.isSuccess()) {
                conn.commit();
                cbl.avanzaCaso(cReloaded, uLogin, "", new String[] { "AUTORIZADOR_RECTIFICACION" }, new String[] { "layout_rectificacion" }, m, prefixPath);
            } else {
                conn.rollback();
                // cbl.avanzaCaso(cReloaded, uLogin, "", new String[]
                // {"REVISOR_RECTIFICACION" }, new String[] {
                // "rev_rectificacion" },m, prefixPath);
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

    public String cancelarAppContableNuevo(Caso c, Map<?, ?> m, String prefixPath, String uLogin, String cFecha) throws Exception {
        String retVal = null;
        Connection conn = null;
        try {
            ContableInterface ci = new AplicacionContable();
            // conn = getConnection();
            CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
            conn = cbl.getConnection();
            AplicarContableReturn acr = ci.cancelarAppContableNueva(conn, c, "", "", "", 1, "", m, prefixPath, uLogin, cFecha);
            Caso cReloaded = new Caso();
            cReloaded.setIdCaso(c.getIdCaso());
            cReloaded = CasoManager.select(conn, cReloaded);
            if (acr.isSuccess()) {
                conn.commit();
                cbl.avanzaCaso(cReloaded, uLogin, "", new String[] { "CONSULTA_RECTIFICACION" }, new String[] { "cons_rectificacion" }, m, prefixPath);
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

    public RectificacionEncabezado getRectificacionEncabezado(int folio) throws Exception {
        Connection conn = null;
        RectificacionEncabezado res;
        try {
            conn = getConnection();
            res = RectificacionPresupuestariaManager.getRectificacionEncabezado(conn, folio);
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
            throw new Exception(exc);
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return res;
    }

    public RectificacionEncabezado getRectificacionEncabezadoSicop(int folio) throws Exception {
        Connection conn = null;
        RectificacionEncabezado res = new RectificacionEncabezado();
        try {
            conn = getConnection();
            res = RectificacionPresupuestariaManager.getRectificacionEncabezadoSicop(conn, folio);
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
            throw new Exception(exc);
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return res;
    }

    public StringBuilder getRectificacionDetalleSicop(int folio) throws Exception {
        Connection conn = null;
        StringBuilder res = new StringBuilder();
        try {
            conn = getConnection();
            res = RectificacionPresupuestariaManager.getRectificacionDetalleSicop(conn, folio);
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
            throw new Exception(exc);
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return res;
    }

    public ArrayList<RectificacionDetalle> getRectificacionDetalle(int folio) throws Exception {
        Connection conn = null;
        ArrayList<RectificacionDetalle> res = new ArrayList<RectificacionDetalle>();
        try {
            conn = getConnection();
            res = RectificacionPresupuestariaManager.getRectificacionDetalle(conn, folio);
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
            throw new Exception(exc);
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return res;
    }

    public int secCLCRect(String caNoContrarrecibo, String EP) throws SQLException {
        Connection conn = null;
        int res = 0;
        try {
            CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
            conn = cbl.getConnection();
            res = ReintegrosManager.secCLCRect(conn, caNoContrarrecibo, EP);
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
