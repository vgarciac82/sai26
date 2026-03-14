package com.syc.sai.contabilidad.utils.db;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CloseObject {

    private static Logger log = LoggerFactory.getLogger(CloseObject.class);

    public static boolean closeObject(Object obj) {
        boolean cerrado = true;
        try {
            closeObject(obj, false);
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
        return cerrado;
    }

    public static boolean closeObject(Object... obj) {
        boolean all = false;
        for (Object object : obj) CloseObject.closeObject(object);
        all = true;
        return all;
    }

    public static boolean closeObject(Object obj, boolean throwException) throws Exception {
        boolean cerrado = true;
        try {
            if (obj != null) {
                log.trace("Objeto recibido para cierre.");
                if (obj instanceof ResultSet) {
                    log.trace("El objeto es un ResultSet. Se cierra");
                    ((ResultSet) obj).close();
                } else if (obj instanceof Statement) {
                    log.trace("El objeto es un Statement. Se cierra");
                    ((Statement) obj).close();
                } else if (obj instanceof CallableStatement) {
                    log.trace("El objeto es un CallableStatement. Se cierra");
                    ((CallableStatement) obj).close();
                } else if (obj instanceof PreparedStatement) {
                    log.trace("El objeto es un PreparedStatement. Se cierra");
                    ((PreparedStatement) obj).close();
                } else if (obj instanceof Connection) {
                    log.trace("El objeto es un Connection. Se cierra");
                    ((Connection) obj).close();
                } else {
                    log.info("Object: {}", "No se como cerrar objetos del tipo: " + obj.getClass().getName());
                    cerrado = false;
                }
            }
        } catch (Exception e) {
            log.error("Error cerrando objeto " + e, e);
            if (throwException)
                throw e;
            else
                cerrado = false;
        } finally {
            obj = null;
        }
        return cerrado;
    }
}
