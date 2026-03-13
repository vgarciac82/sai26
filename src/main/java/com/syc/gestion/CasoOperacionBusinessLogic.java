package com.syc.gestion;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.core.AcumuladoInbox;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoOperacion;
import com.syc.gestion.core.CasoOperacionManager;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.core.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CasoOperacionBusinessLogic extends DataSourceManager {

    private static Logger log = LoggerFactory.getLogger(CasoOperacionBusinessLogic.class);

    public CasoOperacionBusinessLogic(String jniName) {
        super.init(jniName);
    }

    public List getCasoOperacionConsulta(String gavetaAsociada, String noFolio, String fDesde, String fHasta, String documento, String operador, String estatus, String importe, String folioSICOP, String folioMAP, String folioCAL, Usuario u) throws GestionException {
        List l = new ArrayList();
        Connection conn = null;
        try {
            conn = getConnection();
            l = CasoOperacionManager.selectCasoConsulta(conn, gavetaAsociada, noFolio, fDesde, fHasta, documento, operador, estatus, importe, folioSICOP, folioMAP, folioCAL, u);
        } catch (SQLException exc) {
            log.warn("Obteniendo Operacion de Consulta" + gavetaAsociada, exc);
            throw new GestionException(exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return l;
    }

    /**
     * Devuelve el inbox del usuario.
     *
     * <br>
     * <br>
     * El inbox se compone de las operaciones que debe atender el usuario.
     *
     * @param u_login
     *            Login del usuario del sistema
     * @param orderBy
     *            Parametros por los que se ordenara el inbox
     * @param UR
     *            Unida Responsable del usuario
     * @param cCentroContable
     *            centro contable del usuario
     * @return Lista con los tramites (operaciones) pendientes de atender del
     *         usuario.
     *
     * @throws GestionException
     */
    public List<Caso> getInbox(String u_login, String orderBy, String UR, String cCentroContable) throws GestionException {
        List<Caso> l = new ArrayList<Caso>();
        Connection conn = null;
        try {
            conn = getConnection();
            l = CasoOperacionManager.selectInbox(conn, u_login, orderBy, UR, cCentroContable);
        } catch (SQLException exc) {
            log.warn("Obteniendo Caso Operacion por Usuario", exc);
            throw new GestionException(exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return l;
    }

    public List<CasoOperacion> getCasoOperacionPorUsuarioFiltros(String u_login, String orderBy, String UR, String cCentroContable, HttpServletRequest request) throws GestionException, UnsupportedEncodingException {
        List<CasoOperacion> l = new ArrayList<CasoOperacion>();
        Connection conn = null;
        try {
            conn = getConnection();
            l = CasoOperacionManager.selectCasoOperacionFiltros(conn, u_login, orderBy, UR, cCentroContable, request);
        } catch (SQLException exc) {
            log.warn("Obteniendo Caso Operacion por Usuario", exc);
            throw new GestionException(exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return l;
    }

    public List getCasoOperacionPorUsuario(String u_login) throws GestionException {
        List l = new ArrayList();
        Connection conn = null;
        try {
            conn = getConnection();
            l = CasoOperacionManager.selectCasoOperacion(conn, u_login);
        } catch (SQLException exc) {
            log.warn("Obteniendo Caso Operacion por Usuario", exc);
            throw new GestionException(exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return l;
    }

    public void revisaTiempoLimiteDeCasosOperacion(String prefixPath, boolean extMail) throws GestionException {
        // Ethiel,
        // se
        // agrega
        // extMail
        Connection conn = null;
        try {
            conn = getConnection();
            // Ethiel,
            CasoOperacionManager.revisaTiempoLimiteDeCasosOperacion(conn, prefixPath, extMail);
            // se
            // agrega
            // extMail
            conn.commit();
        } catch (IOException exc) {
            log.error("Revisando tiempos limite de casos operacion", exc);
            throw new GestionException(exc);
        } catch (SQLException exc) {
            log.error("Revisando tiempos limite de casos operacion", exc);
            throw new GestionException(exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
    }

    public AcumuladoInbox getTotalCasoOperacionPorUsuarioBandeja(String u_login, String operaciones) throws GestionException {
        AcumuladoInbox inbox = null;
        Connection conn = null;
        try {
            conn = getConnection();
            inbox = CasoOperacionManager.selectTotalCasoOperacionPorUsuarioBandeja(conn, u_login, "N");
        } catch (SQLException exc) {
            log.warn("Obteniendo Total Caso Operacion por Usuario, Bandeja y operaciones", exc);
            throw new GestionException(exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos y operaciones", exc);
            }
            conn = null;
        }
        return inbox;
    }

    public int updateCasoResponsable(CasoOperacion co, String responsableNuevo) throws GestionException {
        Connection conn = null;
        int actualizados = 0;
        try {
            conn = getConnection();
            actualizados = CasoOperacionManager.update(conn, co, responsableNuevo);
            conn.commit();
            return actualizados;
        } catch (SQLException exc) {
            log.warn("Obteniendo Total Caso Operacion por Usuario, Bandeja y operaciones", exc);
            throw new GestionException(exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos y operaciones", exc);
            }
            conn = null;
        }
    }

    public int updateCasoOperacion(CasoOperacion co) throws GestionException {
        Connection conn = null;
        int actualizados = 0;
        try {
            conn = getConnection();
            actualizados = CasoOperacionManager.update(conn, co);
            conn.commit();
            return actualizados;
        } catch (SQLException exc) {
            log.warn("Obteniendo Total Caso Operacion por Usuario, Bandeja y operaciones", exc);
            throw new GestionException(exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos y operaciones", exc);
            }
            conn = null;
        }
    }
}
