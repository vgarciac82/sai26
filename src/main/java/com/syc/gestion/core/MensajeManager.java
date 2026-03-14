package com.syc.gestion.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Base64;

public class MensajeManager {

    public static final int MSG_NO_ENTREGADO = 1;

    public static final int MSG_ENVIADO = 10;

    public static final int MSG_ENTREGADO = 2;

    public static final int MSG_SIN_LEER = 20;

    public static final int MSG_LEIDO = 21;

    public static int delete(Connection conn, int id_msg, String u_login, int msg_status) throws SQLException {
        int retval = -1;
        PreparedStatement pstmnt = null;
        try {
            pstmnt = conn.prepareStatement("DELETE FROM cg_mensaje WHERE id_msg = ? AND msg_para_login = ? " + "AND msg_status = ?");
            pstmnt.setInt(1, id_msg);
            pstmnt.setString(2, u_login);
            pstmnt.setInt(3, msg_status);
            retval = pstmnt.executeUpdate();
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        return retval;
    }

    public static int insert(Connection conn, Mensaje msg) throws SQLException {
        int retval = -1;
        PreparedStatement pstmnt = null;
        try {
            pstmnt = conn.prepareStatement("INSERT INTO cg_mensaje " + "(id_msg, msg_para_login, msg_de_login, msg_fecha, msg_asunto, msg_body, msg_status) " + "VALUES (?,?,?,?,?,?,?)");
            pstmnt.setInt(1, msg.getIdMsg());
            pstmnt.setString(2, msg.getParaLogin());
            pstmnt.setString(3, msg.getDeLogin());
            pstmnt.setTimestamp(4, msg.getFecha());
            pstmnt.setString(5, msg.getAsunto());
            pstmnt.setString(6, msg.getBody());
            pstmnt.setInt(7, msg.getStatus());
            retval = pstmnt.executeUpdate();
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        return retval;
    }

    public static List select(Connection conn, Mensaje msg) throws SQLException {
        List l = new ArrayList();
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        try {
            String token = " AND ";
            StringBuffer where = new StringBuffer();
            if (msg.getIdMsg() > 0) {
                where.append(token + "m.id_msg = ?");
                token = " AND ";
            }
            if (msg.getParaLogin() != null) {
                where.append(token + "m.msg_para_login = ?");
                token = " AND ";
            }
            if (msg.getDeLogin() != null) {
                where.append(token + "m.msg_de_login = ?");
                token = " AND ";
            }
            if (msg.getFecha() != null) {
                where.append(token + "m.msg_fecha = ?");
                token = " AND ";
            }
            if (msg.getAsunto() != null) {
                where.append(token + "m.msg_asunto = ?");
                token = " AND ";
            }
            if (msg.getBody() != null) {
                where.append(token + "m.msg_body = ?");
                token = " AND ";
            }
            if (msg.getStatus() > 0) {
                where.append(token + "m.msg_status = ?");
                token = " AND ";
            }
            pstmnt = conn.prepareStatement("SELECT m.id_msg, m.msg_para_login, m.msg_de_login, m.msg_fecha, " + "m.msg_asunto, m.msg_body, m.msg_status, up.u_nombre para_nombre, ud.u_nombre de_nombre " + "FROM cg_mensaje m, cg_usuario up, cg_usuario ud " + " WHERE up.u_login = m.msg_para_login AND ud.u_login = m.msg_de_login " + where.toString());
            int i = 1;
            if (msg.getIdMsg() > 0)
                pstmnt.setInt(i++, msg.getIdMsg());
            if (msg.getParaLogin() != null)
                pstmnt.setString(i++, msg.getParaLogin());
            if (msg.getDeLogin() != null)
                pstmnt.setString(i++, msg.getDeLogin());
            if (msg.getFecha() != null)
                pstmnt.setTimestamp(i++, msg.getFecha());
            if (msg.getAsunto() != null)
                pstmnt.setString(i++, msg.getAsunto());
            if (msg.getBody() != null)
                pstmnt.setString(i++, msg.getBody());
            if (msg.getStatus() > 0)
                pstmnt.setInt(i++, msg.getStatus());
            rs = pstmnt.executeQuery();
            while (rs.next()) {
                Mensaje lmsg = new Mensaje();
                lmsg.setIdMsg(rs.getInt("id_msg"));
                lmsg.setParaLogin(rs.getString("msg_para_login"));
                lmsg.setParaNombre(rs.getString("para_nombre"));
                lmsg.setDeLogin(rs.getString("msg_de_login"));
                lmsg.setDeNombre(rs.getString("de_nombre"));
                lmsg.setFecha(rs.getTimestamp("msg_fecha"));
                lmsg.setAsunto(rs.getString("msg_asunto"));
                lmsg.setBody(rs.getString("msg_body"));
                lmsg.setStatus(rs.getInt("msg_status"));
                l.add(lmsg);
            }
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            rs = null;
            pstmnt = null;
        }
        return l;
    }

    public static Mensaje select(Connection conn, int id_msg, String msg_para_login, int msg_status) throws SQLException {
        Mensaje msg = null;
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        try {
            pstmnt = conn.prepareStatement("SELECT m.id_msg, m.msg_para_login, m.msg_de_login, m.msg_fecha, " + "m.msg_asunto, m.msg_body, m.msg_status, up.u_nombre para_nombre, ud.u_nombre de_nombre " + "FROM cg_mensaje m, cg_usuario up, cg_usuario ud " + " WHERE up.u_login = m.msg_para_login AND ud.u_login = m.msg_de_login " + "AND m.id_msg = ? AND m.msg_para_login = ? AND m.msg_status = ?");
            pstmnt.setInt(1, id_msg);
            pstmnt.setString(2, msg_para_login);
            pstmnt.setInt(3, msg_status);
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                msg = new Mensaje();
                msg.setIdMsg(rs.getInt("id_msg"));
                msg.setParaLogin(rs.getString("msg_para_login"));
                msg.setParaNombre(rs.getString("para_nombre"));
                msg.setDeLogin(rs.getString("msg_de_login"));
                msg.setDeNombre(rs.getString("de_nombre"));
                msg.setFecha(rs.getTimestamp("msg_fecha"));
                msg.setAsunto(rs.getString("msg_asunto"));
                msg.setBody(rs.getString("msg_body"));
                msg.setStatus(rs.getInt("msg_status"));
            }
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            rs = null;
            pstmnt = null;
        }
        return msg;
    }

    public static void enviaMensaje(Connection conn, String para, String de, String asunto, String body) throws SQLException {
        Mensaje msg = new Mensaje();
        msg.setIdMsg(getNextIdMsg(conn));
        msg.setParaLogin(para);
        msg.setDeLogin(de);
        msg.setFecha(new Timestamp(System.currentTimeMillis()));
        msg.setAsunto(asunto);
        msg.setBody(body);
        msg.setStatus(MSG_ENVIADO);
        insert(conn, msg);
    }

    public static void enviaMensaje(Connection conn, String para, String de, String asunto, String body, int status) throws SQLException {
        Mensaje msg = new Mensaje();
        msg.setIdMsg(getNextIdMsg(conn));
        msg.setParaLogin(para);
        msg.setDeLogin(de);
        msg.setFecha(new Timestamp(System.currentTimeMillis()));
        msg.setAsunto(asunto);
        msg.setBody(body);
        msg.setStatus(status);
        insert(conn, msg);
    }

    public static int update(Connection conn, Mensaje msg, int new_msg_status) throws SQLException {
        int retval = -1;
        PreparedStatement pstmnt = null;
        try {
            pstmnt = conn.prepareStatement("UPDATE cg_mensaje " + "SET msg_status = ? " + "WHERE id_msg = ? AND msg_para_login = ? AND msg_status = ?");
            pstmnt.setInt(1, new_msg_status);
            pstmnt.setInt(2, msg.getIdMsg());
            pstmnt.setString(3, msg.getParaLogin());
            pstmnt.setInt(4, msg.getStatus());
            retval = pstmnt.executeUpdate();
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        return retval;
    }

    public static int getNextIdMsg(Connection conn) throws SQLException {
        int retval = -1;
        Statement stmnt = null;
        ResultSet rs = null;
        try {
            stmnt = conn.createStatement();
            rs = stmnt.executeQuery("SELECT MAX(id_msg) FROM cg_mensaje");
            if (rs.next())
                retval = rs.getInt(1) + 1;
        } finally {
            if (rs != null)
                rs.close();
            if (stmnt != null)
                stmnt.close();
            rs = null;
            stmnt = null;
        }
        return retval;
    }
}
