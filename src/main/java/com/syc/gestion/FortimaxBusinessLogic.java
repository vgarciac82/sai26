package com.syc.gestion;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Map;
import com.syc.dsmngr.DataSourceManager;
import com.syc.fortimax.core.AplicacionManager;
import com.syc.fortimax.core.Descripcion;
import com.syc.fortimax.core.DescripcionManager;
import com.syc.gestion.core.CasoOperacionManager;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.util.PaginaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class FortimaxBusinessLogic extends DataSourceManager {

    private static Logger log = LoggerFactory.getLogger(FortimaxBusinessLogic.class);

    public FortimaxBusinessLogic(String jniName) {
        super.init(jniName);
    }

    public Descripcion[] getDescripcion(String titulo_aplicacion) throws GestionException {
        Descripcion[] desc = new Descripcion[0];
        Connection conn = null;
        try {
            conn = getConnection();
            Map m = DescripcionManager.select(conn, titulo_aplicacion);
            desc = (Descripcion[]) m.values().toArray(new Descripcion[m.values().size()]);
        } catch (SQLException exc) {
            log.error("Recuperando descripcion", exc);
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
        return desc;
    }

    public Descripcion[] getDescripcionAvanzada(String titulo_aplicacion, String tipoasunto) throws GestionException {
        Descripcion[] desc = new Descripcion[0];
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            //conn.setHoldability(ResultSet.CLOSE_CURSORS_AT_COMMIT);
            Map m = DescripcionManager.selectAvanzada(conn, titulo_aplicacion, tipoasunto, true);
            desc = (Descripcion[]) m.values().toArray(new Descripcion[m.values().size()]);
        } catch (SQLException exc) {
            log.error("Recuperando descripcion", exc);
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
        return desc;
    }

    public String[][] getValoresDescripcion(String titulo_aplicacion, int id_gabinete) throws GestionException {
        Connection conn = null;
        String[][] data = new String[0][0];
        try {
            conn = getConnection();
            data = DescripcionManager.getData(conn, titulo_aplicacion, id_gabinete);
        } catch (SQLException exc) {
            log.error("Error SQL", exc);
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
        return data;
    }

    public String[] insertAplicacion(String titulo_aplicacion, String u_login, String nombre_carpeta, Map map) throws GestionException {
        Connection conn = null;
        String[] retval = new String[2];
        try {
            conn = getConnection();
            retval = AplicacionManager.insert(conn, titulo_aplicacion, u_login, nombre_carpeta, map);
            conn.commit();
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                log.warn("Error en rollback", ex);
            }
            log.error("Agregando expediente", exc);
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
        return retval;
    }

    public boolean deleteAplicacion(String titulo_aplicacion, int id_gabinete) throws GestionException {
        Connection conn = null;
        boolean retval = false;
        try {
            conn = getConnection();
            retval = AplicacionManager.delete(conn, titulo_aplicacion, id_gabinete);
            conn.commit();
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                log.warn("Error en rollback", ex);
            }
            log.error("Borrando expediente", exc);
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
        return retval;
    }

    public boolean updateAplicacion(String titulo_aplicacion, int id_gabinete, Map map) throws GestionException {
        Connection conn = null;
        boolean retval = false;
        try {
            conn = getConnection();
            retval = AplicacionManager.update(conn, titulo_aplicacion, id_gabinete, map);
            conn.commit();
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                log.warn("Error en rollback", ex);
            }
            log.error("Actualizando expediente", exc);
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
        return retval;
    }

    public String[][] getQueryByExampleAplicacionData(String titulo_aplicacion, Map map) throws GestionException {
        Connection conn = null;
        String[][] retval = new String[0][0];
        try {
            conn = getConnection();
            retval = AplicacionManager.getQueryByExampleAplicacionData(conn, titulo_aplicacion, map);
        } catch (SQLException exc) {
            log.error("Consultando expediente", exc);
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
        return retval;
    }

    public String[][] getQueryByExampleAplicacionDataYCasosIniciados(String titulo_aplicacion, Map map) throws GestionException {
        Connection conn = null;
        String[][] retval = new String[0][0];
        try {
            conn = getConnection();
            retval = AplicacionManager.getQueryByExampleAplicacionDataYCasosIniciados(conn, titulo_aplicacion, map);
        } catch (SQLException exc) {
            log.error("Consultando expediente", exc);
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
        return retval;
    }

    public String[][] getQueryByExampleAplicacionDataEnConsulta(String titulo_aplicacion, Map map) throws GestionException {
        //regresa los registros de la gaveta que aun tiene caso vivo pero en la operacion de CONSULTA
        Connection conn = null;
        String[][] retval = new String[0][0];
        try {
            conn = getConnection();
            retval = AplicacionManager.getQueryByExampleAplicacionDataEnConsulta(conn, titulo_aplicacion, map);
        } catch (SQLException exc) {
            log.error("Consultando expediente", exc);
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
        return retval;
    }

    public PaginaData getQueryByExampleAvanzadaData(String titulo_aplicacion, String u_login, Map map, String param_fecha_de, String param_fecha_a, String tipoasunto, PaginaData param_pd) throws GestionException {
        Connection conn = null;
        PaginaData pd = new PaginaData();
        log.info("Object: {}", "u_login=" + u_login);
        try {
            conn = getConnection();
            conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            //conn.setHoldability(ResultSet.CLOSE_CURSORS_AT_COMMIT);
            pd = AplicacionManager.getQueryByExampleAplicacionDataAvanzada(conn, titulo_aplicacion, u_login, map, param_fecha_de, param_fecha_a, tipoasunto, param_pd);
            //conn.commit();
        } catch (SQLException exc) {
            log.error("Consultando expediente", exc);
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
        return pd;
    }

    public Map consultaCasoOperacion(String titulo_aplicacion, int id_gabinete) throws GestionException {
        Connection conn = null;
        Map m = new Hashtable();
        try {
            conn = getConnection();
            m = CasoOperacionManager.consultaCasoOperacion(conn, titulo_aplicacion, id_gabinete);
        } catch (SQLException exc) {
            log.error("Obteniendo caso operacion", exc);
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
        return m;
    }

    public Map getCasoOperacion(String u_login, String titulo_aplicacion, int id_gabinete) throws GestionException {
        Connection conn = null;
        Map m = new Hashtable();
        try {
            conn = getConnection();
            m = CasoOperacionManager.selectCasoOperacion(conn, u_login, titulo_aplicacion, id_gabinete);
        } catch (SQLException exc) {
            log.error("Obteniendo caso operacion", exc);
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
        return m;
    }
}
