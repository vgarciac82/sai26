package com.syc.gestion;

import java.sql.Connection;
import java.sql.SQLException;
import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.core.EmpleadoArea;
import com.syc.gestion.core.EmpleadoAreaManager;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.core.Empleado;
import com.syc.gestion.core.EmpleadoManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class EmpleadoBusinessLogic extends DataSourceManager {

    private static final Logger log = LoggerFactory.getLogger(EmpleadoBusinessLogic.class);

    public EmpleadoBusinessLogic(String jniName) {
        super.init(jniName);
    }

    public Empleado getEmpleado(String id) throws GestionException {
        Empleado e = new Empleado();
        e.setId(id);
        return getEmpleado(e);
    }

    public Empleado getEmpleado(Empleado e) throws GestionException {
        Connection conn = null;
        Empleado retVal = null;
        try {
            conn = getConnection();
            retVal = EmpleadoManager.select(conn, e);
        } catch (SQLException exc) {
            log.error("Actualizando Mensaje", exc);
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

    public int insertEmpleado(Empleado e) throws GestionException {
        Connection conn = null;
        int retVal;
        try {
            conn = getConnection();
            retVal = EmpleadoManager.insert(conn, e);
        } catch (SQLException exc) {
            log.error("Actualizando Mensaje", exc);
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

    public EmpleadoArea getEmpleadoArea(EmpleadoArea ea) throws GestionException {
        Connection conn = null;
        EmpleadoArea retVal = null;
        try {
            conn = getConnection();
            retVal = EmpleadoAreaManager.select(conn, ea);
        } catch (SQLException exc) {
            log.error("Actualizando Mensaje", exc);
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
