package com.syc.admin.servlet;

import java.sql.Connection;
import java.sql.SQLException;
import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.core.GestionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class SeguridadCatalogoBusinessLogic extends DataSourceManager {

    private static final Logger log = LoggerFactory.getLogger(SeguridadCatalogoBusinessLogic.class);

    public SeguridadCatalogoBusinessLogic(String jniName) {
        super.init(jniName);
    }

    public int agregaEmpleado(CatEmpleado ce) throws GestionException {
        int retVal = -1;
        Connection conn = null;
        try {
            conn = getConnection();
            //u.setPassword(Encripta.code32(u.getPassword()));
            retVal = CatalogoEmpleado.insert(conn, ce);
            conn.commit();
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                log.error("En rollback", ex);
            }
            log.error("Error al agregar usuario (insert)", exc);
            throw new GestionException(exc);
        } catch (Exception e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                log.error("En rollback", ex);
            }
            log.error("Error al agregar usuario (insert)", e);
            throw new GestionException(e);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
            // EJRV No debe de llamarse dentro de un finally
            //return retVal;
        }
        return retVal;
    }

    public int actualizaEmpleado(CatEmpleado ce) throws GestionException {
        int retVal = -1;
        Connection conn = null;
        try {
            conn = getConnection();
            //u.setPassword(Encripta.code32(u.getPassword()));
            retVal = CatalogoEmpleado.update(conn, ce);
            conn.commit();
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                log.error("En rollback", ex);
            }
            log.error("Error al actualizar usuario (update)", exc);
            throw new GestionException(exc);
        } catch (Exception e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                log.error("En rollback", ex);
            }
            log.error("Error al actualizar usuario (update)", e);
            throw new GestionException(e);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return retVal;
    }

    public int borraEmpleado(CatEmpleado ce) throws GestionException {
        int retVal = -1;
        Connection conn = null;
        try {
            conn = getConnection();
            retVal = CatalogoEmpleado.delete(conn, ce.getId_empleado());
            conn.commit();
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                log.error("En rollback", ex);
            }
            log.error("Error al actualizar usuario (delete)", exc);
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
        return retVal;
    }

    public int agregaCargo(CatCargo cc) throws GestionException {
        int retVal = -1;
        Connection conn = null;
        try {
            conn = getConnection();
            retVal = CatalogoCargo.insert(conn, cc);
            conn.commit();
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                log.error("En rollback", ex);
            }
            log.error("Error al agregar usuario grupo (insert)", exc);
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
        return retVal;
    }

    public int actualizaCargo(CatCargo cc) throws GestionException {
        int retVal = -1;
        Connection conn = null;
        try {
            conn = getConnection();
            retVal = CatalogoCargo.update(conn, cc);
            conn.commit();
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                log.error("En rollback", ex);
            }
            log.error("Error al actualizar usuario grupo (update)", exc);
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
        return retVal;
    }

    public int borraCargo(CatCargo cc) throws GestionException {
        int retVal = -1;
        Connection conn = null;
        try {
            conn = getConnection();
            retVal = CatalogoCargo.delete(conn, cc.getId_cargo());
            conn.commit();
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                log.error("En rollback", ex);
            }
            log.error("Error al actualizar usuario grupo (delete)", exc);
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
        return retVal;
    }

    public int agregaArea(CatArea ca) throws GestionException {
        int retVal = -1;
        Connection conn = null;
        try {
            conn = getConnection();
            retVal = CatalogoArea.insert(conn, ca);
            conn.commit();
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                log.error("En rollback", ex);
            }
            log.error("Error al agregar usuario grupo (insert)", exc);
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
        return retVal;
    }

    public int actualizaArea(CatArea ca) throws GestionException {
        int retVal = -1;
        Connection conn = null;
        try {
            conn = getConnection();
            retVal = CatalogoArea.update(conn, ca);
            conn.commit();
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                log.error("En rollback", ex);
            }
            log.error("Error al actualizar usuario grupo (update)", exc);
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
        return retVal;
    }

    public int borraArea(CatArea ca) throws GestionException {
        int retVal = -1;
        Connection conn = null;
        try {
            conn = getConnection();
            retVal = CatalogoArea.delete(conn, ca.getId_area());
            conn.commit();
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                log.error("En rollback", ex);
            }
            log.error("Error al actualizar usuario propiedades (delete)", exc);
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
        return retVal;
    }

    public int agregaRemArea(CatRemArea cra) throws GestionException {
        int retVal = -1;
        Connection conn = null;
        try {
            conn = getConnection();
            retVal = CatalogoRemitenteArea.insert(conn, cra);
            conn.commit();
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                log.error("En rollback", ex);
            }
            log.error("Error al agregar usuario grupo (insert)", exc);
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
        return retVal;
    }

    public int actualizaRemArea(CatRemArea cra) throws GestionException {
        int retVal = -1;
        Connection conn = null;
        try {
            conn = getConnection();
            retVal = CatalogoRemitenteArea.update(conn, cra);
            conn.commit();
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                log.error("En rollback", ex);
            }
            log.error("Error al actualizar usuario grupo (update)", exc);
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
        return retVal;
    }

    public int borraRemArea(CatRemArea cra) throws GestionException {
        int retVal = -1;
        Connection conn = null;
        try {
            conn = getConnection();
            retVal = CatalogoRemitenteArea.delete(conn, cra.getCra_id_area());
            conn.rollback();
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                log.error("En rollback", ex);
            }
            log.error("Error al actualizar usuario propiedades (delete)", exc);
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
        return retVal;
    }

    public int agregaRemPersona(CatRemPersona crp) throws GestionException {
        int retVal = -1;
        Connection conn = null;
        try {
            conn = getConnection();
            retVal = CatalogoRemitentePersona.insert(conn, crp);
            conn.rollback();
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                log.error("En rollback", ex);
            }
            log.error("Error al agregar usuario grupo (insert)", exc);
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
        return retVal;
    }

    public int actualizaRemPersona(CatRemPersona crp) throws GestionException {
        int retVal = -1;
        Connection conn = null;
        try {
            conn = getConnection();
            retVal = CatalogoRemitentePersona.update(conn, crp);
            conn.commit();
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                log.error("En rollback", ex);
            }
            log.error("Error al actualizar usuario grupo (update)", exc);
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
        return retVal;
    }

    public int borraRemPersona(CatRemPersona crp) throws GestionException {
        int retVal = -1;
        Connection conn = null;
        try {
            conn = getConnection();
            retVal = CatalogoRemitentePersona.delete(conn, crp.getCrp_id_persona());
            conn.commit();
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                log.error("En rollback", ex);
            }
            log.error("Error al actualizar usuario propiedades (delete)", exc);
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
        return retVal;
    }
}
