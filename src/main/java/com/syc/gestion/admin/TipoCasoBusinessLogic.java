package com.syc.gestion.admin;

import java.sql.Connection;
import java.sql.SQLException;
import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.core.Operacion;
import com.syc.gestion.core.OperacionManager;
import com.syc.gestion.core.OperacionSiguiente;
import com.syc.gestion.core.OperacionSiguienteManager;
import com.syc.gestion.core.TipoCaso;
import com.syc.gestion.core.TipoCasoManager;
import com.syc.gestion.core.TipoCasoVariable;
import com.syc.gestion.core.TipoCasoVariableManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class TipoCasoBusinessLogic extends DataSourceManager {

    private static Logger log = LoggerFactory.getLogger(TipoCasoBusinessLogic.class);

    public TipoCasoBusinessLogic(String jniName) {
        super.init(jniName);
    }

    public synchronized void agregaTipoCaso(TipoCaso tc) throws GestionAdminException {
        Connection conn = null;
        try {
            conn = getConnection();
            int idTC = TipoCasoManager.getNextId(conn);
            tc.setIdTC(idTC);
            TipoCasoManager.insert(conn, tc);
            conn.commit();
        } catch (Exception exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                log.warn("Error en rollback", ex);
            }
            log.error("Agregando Tipo Caso", exc);
            throw new GestionAdminException(exc);
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

    public void actualizaTipoCaso(TipoCaso tc) throws GestionAdminException {
        Connection conn = null;
        try {
            conn = getConnection();
            TipoCasoManager.update(conn, tc);
            conn.commit();
        } catch (Exception exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                log.warn("Error en rollback", ex);
            }
            log.error("Actualizando Tipo Caso", exc);
            throw new GestionAdminException(exc);
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

    public void borraTipoCaso(int id_tc) throws GestionAdminException {
        Connection conn = null;
        try {
            conn = getConnection();
            if (TipoCasoManager.existenCasos(conn, id_tc))
                throw new GestionAdminException("Ya existen casos para este tipo de caso (" + id_tc + ")");
            TipoCasoManager.delete(conn, id_tc);
            conn.commit();
        } catch (Exception exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                log.warn("Error en rollback", ex);
            }
            log.error("Borrando Tipo Caso", exc);
            throw new GestionAdminException(exc);
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

    public synchronized void agregaTipoCasoVariable(TipoCasoVariable tcv) throws GestionAdminException {
        Connection conn = null;
        try {
            conn = getConnection();
            int id_tcv = TipoCasoVariableManager.getNextId(conn, tcv.getIdTC());
            tcv.setIdTCV(id_tcv);
            TipoCasoVariableManager.insert(conn, tcv);
            conn.commit();
        } catch (Exception exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                log.warn("Error en rollback", ex);
            }
            log.error("Agregando Variable a Tipo Caso", exc);
            throw new GestionAdminException(exc);
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

    public void actualizaTipoCasoVariable(TipoCasoVariable tcv) throws GestionAdminException {
        Connection conn = null;
        try {
            conn = getConnection();
            TipoCasoVariableManager.update(conn, tcv);
            conn.commit();
        } catch (Exception exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                log.warn("Error en rollback", ex);
            }
            log.error("Actualizando Variable de Tipo Caso", exc);
            throw new GestionAdminException(exc);
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

    public void borraTipoCasoVariable(int id_tc, int id_tcv) throws GestionAdminException {
        Connection conn = null;
        try {
            conn = getConnection();
            if (TipoCasoVariableManager.existenVariablesDeCaso(conn, id_tc, id_tcv))
                throw new GestionAdminException("Ya existen casos para esta variable de caso (" + id_tc + ", " + id_tcv + ")");
            TipoCasoVariableManager.delete(conn, id_tc, id_tcv);
            conn.commit();
        } catch (Exception exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                log.warn("Error en rollback", ex);
            }
            log.error("Borrando Variable de Tipo Caso", exc);
            throw new GestionAdminException(exc);
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

    public synchronized void agregaOperacion(Operacion o) throws GestionAdminException {
        Connection conn = null;
        try {
            conn = getConnection();
            int id_oper = OperacionManager.getNextId(conn, o.getIdTC());
            o.setIdOperacion(id_oper);
            OperacionManager.insert(conn, o);
            conn.commit();
        } catch (Exception exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                log.warn("Error en rollback", ex);
            }
            log.error("Agregando Operacion", exc);
            throw new GestionAdminException(exc);
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

    public void actualizaOperacion(Operacion o) throws GestionAdminException {
        Connection conn = null;
        try {
            conn = getConnection();
            OperacionManager.update(conn, o);
            conn.commit();
        } catch (Exception exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                log.warn("Error en rollback", ex);
            }
            log.error("Actualizando Operacion", exc);
            throw new GestionAdminException(exc);
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

    public void borraOperacion(int id_tc, int id_oper) throws GestionAdminException {
        Connection conn = null;
        try {
            conn = getConnection();
            if (OperacionManager.existenOperaciones(conn, id_tc, id_oper))
                throw new GestionAdminException("Ya existen casos para esta operacion del caso (" + id_tc + ", " + id_oper + ")");
            OperacionManager.delete(conn, id_tc, id_oper);
            conn.commit();
        } catch (Exception exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                log.warn("Error en rollback", ex);
            }
            log.error("Borrando Operacion", exc);
            throw new GestionAdminException(exc);
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

    public void actualizaOperacionSiguiente(OperacionSiguiente os) throws GestionAdminException {
        Connection conn = null;
        try {
            conn = getConnection();
            OperacionSiguienteManager.update(conn, os);
            conn.commit();
        } catch (Exception exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                log.warn("Error en rollback", ex);
            }
            log.error("Actualizando Operacion Siguiente", exc);
            throw new GestionAdminException(exc);
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

    public synchronized void agregaOperacionSiguiente(OperacionSiguiente os) throws GestionAdminException {
        Connection conn = null;
        try {
            conn = getConnection();
            int id_oper_sigte = OperacionSiguienteManager.getNextId(conn, os.getIdTC(), os.getIdOperacion());
            os.setIdOperacionSigte(id_oper_sigte);
            OperacionSiguienteManager.insert(conn, os);
            conn.commit();
        } catch (Exception exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                log.warn("Error en rollback", ex);
            }
            log.error("Agregando Operacion Siguiente", exc);
            throw new GestionAdminException(exc);
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

    public void borraOperacionSiguiente(int id_tc, int id_oper, int id_oper_sigte) throws GestionAdminException {
        Connection conn = null;
        try {
            conn = getConnection();
            OperacionSiguienteManager.delete(conn, id_tc, id_oper, id_oper_sigte);
            conn.commit();
        } catch (Exception exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                log.warn("Error en rollback", ex);
            }
            log.error("Borrando Operacion Siguiente", exc);
            throw new GestionAdminException(exc);
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
}
