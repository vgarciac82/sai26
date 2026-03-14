package com.syc.admin.servlet;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.core.Mensaje;
import com.syc.gestion.core.MensajeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class MensajesBusinessLogic extends DataSourceManager {

    private static Logger log = LoggerFactory.getLogger(MensajesBusinessLogic.class);

    public MensajesBusinessLogic(String jniName) {
        super.init(jniName);
    }

    public synchronized void deleteMensaje(int id_msg, String msg_para_login, int msg_status) throws GestionException {
        Connection conn = null;
        try {
            conn = getConnection();
            MensajeManager.delete(conn, id_msg, msg_para_login, msg_status);
            conn.commit();
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                log.warn("Error en rollback", ex);
            }
            log.error("Borrando Mensaje", exc);
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
    }

    public synchronized boolean EnviaMensaje(String u_login, String[] para, String asunto, String body) throws GestionException {
        boolean retval = false;
        Connection conn = null;
        try {
            conn = getConnection();
            Mensaje msg = new Mensaje();
            msg.setIdMsg(MensajeManager.getNextIdMsg(conn));
            msg.setParaLogin(u_login);
            msg.setDeLogin(u_login);
            msg.setFecha(new Timestamp(System.currentTimeMillis()));
            msg.setAsunto(asunto);
            msg.setBody(body);
            msg.setStatus(MensajeManager.MSG_ENVIADO);
            MensajeManager.insert(conn, msg);
            for (int i = 0; i < para.length; i++) {
                msg.setParaLogin(para[i]);
                msg.setStatus(MensajeManager.MSG_SIN_LEER);
                MensajeManager.insert(conn, msg);
            }
            conn.commit();
            retval = true;
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                log.warn("Error en rollback", ex);
            }
            log.error("Enviando Mensaje", exc);
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

    public Mensaje getMensaje(int id_msg, String msg_para_login, int msg_status) throws GestionException {
        Connection conn = null;
        Mensaje msg = null;
        try {
            conn = getConnection();
            msg = MensajeManager.select(conn, id_msg, msg_para_login, msg_status);
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
        return msg;
    }

    public List getMensajesEnviados(String u_login, int msg_status) throws GestionException {
        List l = new ArrayList();
        Connection conn = null;
        try {
            conn = getConnection();
            Mensaje msg = new Mensaje();
            msg.setParaLogin(u_login);
            msg.setStatus(msg_status);
            l = MensajeManager.select(conn, msg);
        } catch (SQLException exc) {
            log.error("Actualizando mensajes enviados", exc);
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
        return l;
    }

    public synchronized void updateMessage(Mensaje msg) throws GestionException {
        Connection conn = null;
        try {
            conn = getConnection();
            MensajeManager.update(conn, msg, MensajeManager.MSG_LEIDO);
            conn.commit();
        } catch (SQLException exc) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                log.warn("Error en rollback", ex);
            }
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
    }
}
