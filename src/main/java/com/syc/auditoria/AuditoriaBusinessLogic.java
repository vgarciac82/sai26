package com.syc.auditoria;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import com.syc.auditoria.core.Auditoria;
import com.syc.auditoria.core.AuditoriaManager;
import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.core.GestionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class AuditoriaBusinessLogic extends DataSourceManager {

    private static final Logger log = LoggerFactory.getLogger(AuditoriaBusinessLogic.class);

    public AuditoriaBusinessLogic(String jniName) {
        super.init(jniName);
    }

    //Este es para regresar la cadena de originales y la cadena de destino
    public ArrayList getValores_origen_destino(String tabla, String set, String where) throws GestionException {
        ArrayList origen = new ArrayList();
        //ArrayList destino=new ArrayList();
        String regresa_origen = "";
        String regresa_destino = "";
        ArrayList regresa_origen_destino = new ArrayList();
        Connection conn = null;
        try {
            //recuperando valores originales
            conn = getConnection();
            origen = AuditoriaManager.getValores_origen(conn, tabla, where);
            //parsenado valores destino y haciendo match de origen vs destino
            set = set.replace("'", "");
            String[] arreglo = set.split(",");
            for (int i = 0; i < arreglo.length; i++) {
                String campo_dest = arreglo[i].substring(0, arreglo[i].indexOf("=")).trim();
                String valor_dest = arreglo[i].substring(arreglo[i].indexOf("=") + 1).trim();
                for (int j = 0; j < origen.size(); j++) {
                    String campo_origen = origen.get(j).toString().substring(0, origen.get(j).toString().indexOf("="));
                    String valor_origen = origen.get(j).toString().substring(origen.get(j).toString().indexOf("=") + 1);
                    if (campo_dest.toUpperCase().equals(campo_origen.toUpperCase()) && !valor_dest.toUpperCase().equals(valor_origen.toUpperCase())) {
                        regresa_origen += origen.get(j) + "<br>";
                        regresa_destino += arreglo[i] + "<br>";
                        break;
                    }
                }
            }
            regresa_origen_destino.add(regresa_origen.toUpperCase());
            regresa_origen_destino.add(regresa_destino.toUpperCase());
        } catch (SQLException exc) {
            log.error("Error al leer valores originales", exc);
            throw new GestionException(exc);
        } catch (Exception e) {
            log.error("Error al identificar cambios entre valores originales y valores nuevos", e);
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
        return regresa_origen_destino;
    }

    //Este e spara rmar la cadena de valores para el insert
    public String getValores_insert(String query, String valores) throws GestionException {
        String valores_insert = "";
        try {
            //parsenado valores destino y haciendo match de origen vs destino
            String[] arreglo_campos = query.toUpperCase().substring(query.indexOf("("), query.indexOf("VALUES")).replace("(", "").replace(")", "").split(",");
            String[] arreglo_valores = valores.replace("'", "").split(",");
            if (arreglo_campos.length == arreglo_valores.length) {
                for (int i = 0; i < arreglo_campos.length; i++) {
                    valores_insert += arreglo_campos[i].trim() + "=" + arreglo_valores[i].trim() + "<BR>";
                }
            }
        } catch (Exception e) {
            log.error("Error al construir cadena de campo_valor para el insert");
            log.error("Object: {}", "query: " + query);
            log.error("Object: {}", "valores: " + valores);
            e.printStackTrace();
            throw new GestionException(e);
        }
        return valores_insert;
    }

    public ArrayList get(String set) throws GestionException {
        ArrayList destino = new ArrayList();
        try {
            String[] arreglo = set.split(",");
            for (int i = 0; i < arreglo.length; i++) {
                destino.add(arreglo[i]);
            }
        } catch (Exception e) {
            log.error("Error al parsear set para ponerlo en una lista.", e);
            throw new GestionException(e);
        }
        return destino;
    }

    public int agregaAuditoria(String usuario, String area, String aplicacion, String modulo, String accion, String valor_origen, String valor_destino, String valor, String valor_llave) throws GestionException {
        int retVal = -1;
        Connection conn = null;
        try {
            conn = getConnection();
            retVal = AuditoriaManager.insert(conn, usuario, area, aplicacion, modulo, accion, valor_origen, valor_destino, valor, valor_llave);
            conn.commit();
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                log.error("En rollback", ex);
            }
            log.error("Error al agregar Auditoria (insert)", exc);
            throw new GestionException(exc);
        } catch (Exception e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                log.error("En rollback", ex);
            }
            log.error("Error al agregar Auditoria (insert)", e);
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

    public int agregaAuditoriaManto(Connection conn, String usuario, String area, String aplicacion, String modulo, String accion, String valor_origen, String valor_destino, String valor, String valor_llave) throws GestionException {
        int retVal = -1;
        try {
            retVal = AuditoriaManager.insert(conn, usuario, area, aplicacion, modulo, accion, valor_origen, valor_destino, valor, valor_llave);
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                log.error("En rollback", ex);
            }
            log.error("Error al agregar Auditoria (insert)", exc);
            throw new GestionException(exc);
        } catch (Exception e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                log.error("En rollback", ex);
            }
            log.error("Error al agregar Auditoria (insert)", e);
            throw new GestionException(e);
        }
        return retVal;
    }

    public int actualizaAuditoria(Auditoria a) throws GestionException {
        int retVal = -1;
        Connection conn = null;
        try {
            conn = getConnection();
            retVal = AuditoriaManager.update(conn, a);
            conn.commit();
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                log.error("En rollback", ex);
            }
            log.error("Error al actualizar auditoria (update)", exc);
            throw new GestionException(exc);
        } catch (Exception e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                log.error("En rollback", ex);
            }
            log.error("Error al actualizar auditoria (update)", e);
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

    public int borraAuditoria(Auditoria a) throws GestionException {
        int retVal = -1;
        Connection conn = null;
        try {
            conn = getConnection();
            retVal = AuditoriaManager.delete(conn, a.getId_auditoria());
            conn.commit();
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                log.error("En rollback", ex);
            }
            log.error("Error al actualizar auditoria (delete)", exc);
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
