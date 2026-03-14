package com.syc.gestion.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import java.util.Base64;

public class OperacionSiguienteManager {

    public static int delete(Connection conn, int id_tc, int id_oper, int id_oper_sigte) throws SQLException {
        int retval = -1;
        PreparedStatement pstmnt = null;
        try {
            pstmnt = conn.prepareStatement("DELETE FROM cg_operacion_siguiente with(rowlock) " + "WHERE id_tc = ? " + "AND id_oper = ? " + "AND id_oper_sigte = ?");
            pstmnt.setInt(1, id_tc);
            pstmnt.setInt(2, id_oper);
            pstmnt.setInt(3, id_oper_sigte);
            retval = pstmnt.executeUpdate();
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        return retval;
    }

    public static int insert(Connection conn, OperacionSiguiente os) throws SQLException {
        int retval = -1;
        PreparedStatement pstmnt = null;
        try {
            pstmnt = conn.prepareStatement("INSERT INTO cg_operacion_siguiente " + "(id_tc, id_oper, id_oper_sigte, os_responsable, os_operacion) " + "VALUES (?, ?, ?, ?, ?)");
            pstmnt.setInt(1, os.getIdTC());
            pstmnt.setInt(2, os.getIdOperacion());
            pstmnt.setInt(3, os.getIdOperacionSigte());
            pstmnt.setString(4, os.getResponsable());
            pstmnt.setString(5, os.getOperacion());
            retval = pstmnt.executeUpdate();
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        return retval;
    }

    public static Vector select(Connection conn, OperacionSiguiente os) throws SQLException {
        Vector rv = new Vector();
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        try {
            StringBuffer where = new StringBuffer();
            String token = " WHERE ";
            if (os.getIdTC() > 0) {
                where.append(token + "id_tc = ?");
                token = " AND ";
            }
            if (os.getIdOperacion() > 0) {
                where.append(token + "id_oper = ?");
                token = " AND ";
            }
            if (os.getIdOperacionSigte() > 0) {
                where.append(token + "id_oper_sigte = ?");
                token = " AND ";
            }
            if (os.getResponsable() != null) {
                where.append(token + "os_responsable = ?");
                token = " AND ";
            }
            if (os.getOperacion() != null) {
                where.append(token + "os_operacion = ?");
                token = " AND ";
            }
            pstmnt = conn.prepareStatement("SELECT * FROM cg_operacion_siguiente with(nolock) " + where.toString());
            int i = 1;
            if (os.getIdTC() > 0)
                pstmnt.setInt(i++, os.getIdTC());
            if (os.getIdOperacion() > 0)
                pstmnt.setInt(i++, os.getIdOperacion());
            if (os.getIdOperacionSigte() > 0)
                pstmnt.setInt(i++, os.getIdOperacionSigte());
            if (os.getResponsable() != null)
                pstmnt.setString(i++, os.getResponsable());
            if (os.getOperacion() != null)
                pstmnt.setString(i++, os.getOperacion());
            rs = pstmnt.executeQuery();
            while (rs.next()) {
                OperacionSiguiente ros = new OperacionSiguiente();
                ros.setIdTC(rs.getInt("id_tc"));
                ros.setIdOperacion(rs.getInt("id_oper"));
                ros.setIdOperacionSigte(rs.getInt("id_oper_sigte"));
                ros.setResponsable(rs.getString("os_responsable"));
                ros.setOperacion(rs.getString("os_operacion"));
                rv.add(ros);
            }
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            rs = null;
            pstmnt = null;
        }
        return rv;
    }

    public static int update(Connection conn, OperacionSiguiente os) throws SQLException {
        int retval = -1;
        PreparedStatement pstmnt = null;
        try {
            pstmnt = conn.prepareStatement("UPDATE cg_operacion_siguiente with(rowlock) " + "SET os_responsable = ?" + ", os_operacion = ?" + "WHERE id_tc = ? " + "AND id_oper = ? " + "AND id_oper_sigte = ?");
            pstmnt.setString(1, os.getResponsable());
            pstmnt.setString(2, os.getOperacion());
            pstmnt.setInt(3, os.getIdTC());
            pstmnt.setInt(4, os.getIdOperacion());
            pstmnt.setInt(5, os.getIdOperacionSigte());
            retval = pstmnt.executeUpdate();
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        return retval;
    }

    public static int getNextId(Connection conn, int id_tc, int id_oper) throws SQLException {
        int id = 0;
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        try {
            pstmnt = conn.prepareStatement("SELECT MAX(id_oper_sigte) FROM cg_operacion_siguiente with(nolock) " + "WHERE id_tc = ? AND id_oper = ?");
            pstmnt.setInt(1, id_tc);
            pstmnt.setInt(2, id_oper);
            rs = pstmnt.executeQuery();
            if (rs.next())
                id = rs.getInt(1) + 1;
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
        }
        return id;
    }
}
