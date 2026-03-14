package com.syc.cfdi.db;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CloseObject {

    private static final Logger log = LoggerFactory.getLogger(CloseObject.class);

    public static boolean closeObject(Object obj) {
        boolean cerrado = true;
        try {
            closeObject(obj, false);
        } catch (Exception e) {
            System.out.println(e);
        }
        return cerrado;
    }

    public static boolean closeObject(Object obj, boolean throwException) throws Exception {
        boolean cerrado = true;
        try {
            if (obj != null) {
                log.trace("Objeto recibido para cierre.");
                if (obj instanceof ResultSet) {
                    log.debug("El objeto es un ResultSet. Se cierra");
                    ((ResultSet) obj).close();
                } else if (obj instanceof Statement) {
                    log.debug("El objeto es un Statement. Se cierra");
                    ((Statement) obj).close();
                } else if (obj instanceof CallableStatement) {
                    log.debug("El objeto es un CallableStatement. Se cierra");
                    ((CallableStatement) obj).close();
                } else if (obj instanceof PreparedStatement) {
                    log.debug("El objeto es un PreparedStatement. Se cierra");
                    ((PreparedStatement) obj).close();
                } else if (obj instanceof Connection) {
                    log.debug("El objeto es un Connection. Se cierra");
                    ((Connection) obj).close();
                } else {
                    log.debug("Object: {}", "No se como cerrar objetos del tipo: " + obj.getClass().getName());
                    cerrado = false;
                }
            }
        } catch (Exception e) {
            log.warn("Error cerrando objeto " + e, e);
            if (throwException)
                throw e;
            else
                cerrado = false;
        } finally {
            obj = null;
        }
        return cerrado;
    }

    public static boolean closeObject(Object... obj) {
        for (Object o : obj) closeObject(o);
        return true;
    }

    public static void closeStream(Object stream) {
        try {
            if (stream != null) {
                log.debug("Object: {}", "Closing stream of type: " + stream.getClass().getName());
                if (stream instanceof FileOutputStream) {
                    ((FileOutputStream) stream).flush();
                    ((FileOutputStream) stream).close();
                }
            } else if (stream instanceof FileInputStream) {
                ((FileInputStream) stream).close();
            } else if (stream instanceof FileChannel) {
                ((FileChannel) stream).close();
            } else
                log.error("Object: {}", "No se como cerrar flujos de tipo: " + stream);
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        } finally {
            stream = null;
        }
    }
}
