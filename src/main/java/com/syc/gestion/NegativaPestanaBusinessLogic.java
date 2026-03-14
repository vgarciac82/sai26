package com.syc.gestion;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import com.syc.gestion.core.NegativaPestanaManager;
import com.syc.gestion.core.GestionException;
import com.syc.dsmngr.DataSourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class NegativaPestanaBusinessLogic extends DataSourceManager {

    private static final Logger log = LoggerFactory.getLogger(NegativaPestanaBusinessLogic.class);

    public NegativaPestanaBusinessLogic(String jniName) {
        super.init(jniName);
    }

    public Map<String, String> getPestana(String role, String modulo) throws GestionException {
        Map<String, String> v = null;
        Connection conn = null;
        try {
            conn = getConnection();
            v = NegativaPestanaManager.selectByRol(conn, role, modulo);
        } catch (SQLException exc) {
            log.warn("Obteniendo botones por modulo", exc);
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
        return v;
    }

    // botones
    public Map<String, String> getBotones(String role, String modulo, String pestana) throws GestionException {
        Map<String, String> botones = null;
        Connection conn = null;
        try {
            conn = getConnection();
            botones = NegativaPestanaManager.selectByPestana(conn, role, modulo, pestana);
        } catch (SQLException exc) {
            log.warn("obteniendo botones por pestaña", exc);
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
        return botones;
    }
}
