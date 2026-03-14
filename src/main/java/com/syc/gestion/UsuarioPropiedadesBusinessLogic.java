package com.syc.gestion;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Map;
import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.core.UsuarioPropiedades;
import com.syc.gestion.core.UsuarioPropiedadesManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class UsuarioPropiedadesBusinessLogic extends DataSourceManager {

    private static Logger log = LoggerFactory.getLogger(UsuarioPropiedadesBusinessLogic.class);

    public UsuarioPropiedadesBusinessLogic(String jniName) {
        super.init(jniName);
    }

    public Map selectAll(String u_login) throws GestionException {
        Map m = new Hashtable();
        Connection conn = null;
        try {
            conn = getConnection();
            m = UsuarioPropiedadesManager.select(conn, u_login);
        } catch (SQLException exc) {
            log.error("Recuperando todas las propiedades del usuario " + u_login, exc);
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

    public UsuarioPropiedades select(UsuarioPropiedades up) throws GestionException {
        //requiere recibir u_login y nombre de la propiedad
        Connection conn = null;
        try {
            conn = getConnection();
            up = UsuarioPropiedadesManager.select(conn, up);
        } catch (SQLException exc) {
            log.error("Recuperando valor de la propiedad " + up.getNombre() + " del usuario " + up.getLogin(), exc);
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
        return up;
    }
}
