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
import com.syc.contable.core.AplicacionContable;
import com.syc.contable.core.AplicacionContable.AplicarContableReturn;
import com.syc.contable.core.RectificacionAnexo1;
import com.syc.contable.core.RectificacionAnexo1Detalle;
import com.syc.contable.core.RectificacionAnexo1Encabezado;
import com.syc.contable.core.RectificacionAnexo1Manager;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoManager;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

/**
 * @author Martha Aurora Sánchez Valdivieso para SYC Constructores de Sistemas
 *         SA de CV desarrollo gestion_conagua_sif México D.F. 19/07/2012
 */
public class RectificacionAnexo1BusinessLogic extends DataSourceManager {

    public RectificacionAnexo1 rectificacion = null;

    private static Logger log = LoggerFactory.getLogger(RectificacionAnexo1BusinessLogic.class);

    AdecuacionBusinessLogic adecProy = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);

    public RectificacionAnexo1BusinessLogic(String jniName) {
        super.init(jniName);
    }

    public boolean insertTRectificacionEncabezado(int folio, String nIDCaso, String cEjercicio, String cRamo, String cUnidad, String cCentroContable, java.sql.Date fExp, java.sql.Date fApl, String conceptoRectificacion, String cTipoMovto, String nOrigenPPTO, String nMes, String oficioRectif, String ctr_int, String cTipoRectificacion, String nFolioSICOP, String caNoContrarrecibo, String cTipoPoliza, String cDescripcionPoliza, String u_login, String nFolioPoliza, String cUnidadResponsableContable, String nFolioSIAFF, String totalDebe, String totalDice, ArrayList<RectificacionAnexo1Detalle> rd) throws Exception {
        Connection conn = null;
        boolean insertar = false;
        try {
            conn = getConnection();
            DateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
            if ("2013".equals(adecProy.obtenEjercicioFiscal())) {
                fApl = new java.sql.Date(dateFormatter.parse("31/12/2012").getTime());
            }
            //INSERTA ENCABEZADO
            insertar = RectificacionAnexo1Manager.insertarEncabezado(conn, folio, nIDCaso, cEjercicio, cRamo, cUnidad, cCentroContable, fExp, fApl, conceptoRectificacion, cTipoMovto, nOrigenPPTO, nMes, oficioRectif, ctr_int, cTipoRectificacion, nFolioSICOP, caNoContrarrecibo, cTipoPoliza, cDescripcionPoliza, u_login, nFolioPoliza, cUnidadResponsableContable, nFolioSIAFF, totalDebe, totalDice);
            //INSERTA DETALLE
            insertar = RectificacionAnexo1Manager.insertarDetalle(conn, rd, caNoContrarrecibo);
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

    public boolean insertRectificacionDetalle(ArrayList<RectificacionAnexo1Detalle> rd, String caNoContrarrecibo) throws Exception {
        Connection conn = null;
        boolean insertar = false;
        try {
            conn = getConnection();
            insertar = RectificacionAnexo1Manager.insertarDetalle(conn, rd, caNoContrarrecibo);
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

    public RectificacionAnexo1Encabezado getRectificacionEncabezado(int folio) throws Exception {
        Connection conn = null;
        RectificacionAnexo1Encabezado res;
        try {
            conn = getConnection();
            res = RectificacionAnexo1Manager.getRectificacionEncabezado(conn, folio);
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

    public RectificacionAnexo1Encabezado getRectificacionEncabezadoSicop(int folio) throws Exception {
        Connection conn = null;
        RectificacionAnexo1Encabezado res = new RectificacionAnexo1Encabezado();
        try {
            conn = getConnection();
            res = RectificacionAnexo1Manager.getRectificacionEncabezadoSicop(conn, folio);
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

    public String getRectificacionDetalleSicop(int folio) throws Exception {
        Connection conn = null;
        // RectificacionEncabezado res = new RectificacionEncabezado();
        String res = "";
        try {
            conn = getConnection();
            res = RectificacionAnexo1Manager.getRectificacionDetalleSicop(conn, folio);
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

    public ArrayList<RectificacionAnexo1Detalle> getRectificacionDetalle(int folio) throws Exception {
        Connection conn = null;
        ArrayList<RectificacionAnexo1Detalle> res = new ArrayList<RectificacionAnexo1Detalle>();
        try {
            conn = getConnection();
            res = RectificacionAnexo1Manager.getRectificacionDetalle(conn, folio);
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
            res = RectificacionAnexo1Manager.secCLCRect(conn, caNoContrarrecibo, EP);
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

    public int[] getNDocRenglon(String caNoContrarrecibo, String EP, int cMes, int folio) throws SQLException {
        Connection conn = null;
        int[] res = new int[10];
        try {
            CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
            conn = cbl.getConnection();
            res = RectificacionAnexo1Manager.getNDocRenglon(conn, caNoContrarrecibo, EP, cMes, folio);
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

    public void actualizaFechaAplicacion(int folio) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            RectificacionAnexo1Manager.actualizaFechaAplicacion(conn, folio);
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
    }

    public String aplicaRectificacion(int nIdCaso, Usuario usuario, String aEjercicioFiscal, String cRamo, String cUR, Caso c, String cCentroContable, String cFechaAplica, Map<?, ?> m, String prefixPath, String uLogin) throws Exception {
        List<String> arrLResult = null;
        Connection conn = null;
        String cMensaje = "";
        try {
            CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
            conn = cbl.getConnection();
            ContableInterface conInt = new AplicacionContable();
            log.debug("Object: {}", "Inicia aplicacion contable " + new Timestamp(System.currentTimeMillis()));
            AplicarContableReturn acr = conInt.aplicarContableNuevo(conn, c, "tRectificaAnexo1Encabezado", "tRectificaAnexo1Detalle", "nFolioRectificaAnexo1", new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue(), "RECTIFICAANEXO1", m, prefixPath, uLogin, "");
            arrLResult = acr.getMessageList();
            log.debug("Object: {}", "Termina Apartado Aplicacion contable " + new Timestamp(System.currentTimeMillis()));
            Caso cReloaded = new Caso();
            cReloaded.setIdCaso(c.getIdCaso());
            cReloaded = CasoManager.select(conn, cReloaded);
            if (acr.isSuccess()) {
                conn.commit();
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

    public double getRemanente(String EP, String CXP, int secClc) throws SQLException {
        Connection conn = null;
        double res = 0.00;
        try {
            CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
            conn = cbl.getConnection();
            res = RectificacionAnexo1Manager.getRemanente(conn, EP, CXP.trim(), secClc);
        } catch (Exception exc) {
            exc.printStackTrace();
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return res;
    }

    public String getEventoRectificacionAnexo1(String EP, String tipo) throws SQLException {
        Connection conn = null;
        String evento = "";
        try {
            CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
            conn = cbl.getConnection();
            evento = RectificacionAnexo1Manager.getEventoRectificacionAnexo1(conn, EP, tipo);
        } catch (Exception exc) {
            exc.printStackTrace();
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return evento;
    }
}
