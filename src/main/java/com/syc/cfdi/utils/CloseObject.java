package com.syc.cfdi.utils;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class CloseObject {

    private static Logger log = LoggerFactory.getLogger(CloseObject.class);

    public static boolean closeObject(Object obj, boolean throwException) throws Exception {
        boolean cerrado = true;
        try {
            if (obj != null) {
                if (obj instanceof ResultSet)
                    ((ResultSet) obj).close();
                else if (obj instanceof Statement)
                    ((Statement) obj).close();
                else if (obj instanceof CallableStatement)
                    ((CallableStatement) obj).close();
                else if (obj instanceof PreparedStatement)
                    ((PreparedStatement) obj).close();
                else if (obj instanceof Connection)
                    ((Connection) obj).close();
                else {
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
