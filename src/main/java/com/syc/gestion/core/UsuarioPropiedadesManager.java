package com.syc.gestion.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Base64;

public class UsuarioPropiedadesManager {

    public static int delete(Connection conn, String u_login) throws SQLException {
        int retval = -1;
        PreparedStatement pstmnt = null;
        try {
            pstmnt = conn.prepareStatement("DELETE FROM cg_usuario_propiedades with(rowlock) WHERE u_login = ?");
            pstmnt.setString(1, u_login);
            retval = pstmnt.executeUpdate();
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        return retval;
    }

    public static int delete(Connection conn, String u_login, String up_nombre) throws SQLException {
        int retval = -1;
        PreparedStatement pstmnt = null;
        try {
            pstmnt = conn.prepareStatement("DELETE FROM cg_usuario_propiedades with(rowlock) WHERE u_login = ? AND up_nombre = ?");
            pstmnt.setString(1, u_login);
            pstmnt.setString(2, up_nombre);
            retval = pstmnt.executeUpdate();
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        return retval;
    }

    public static void insert(Connection conn, Map m) throws SQLException {
        if (!m.isEmpty())
            for (Iterator iter = m.keySet().iterator(); iter.hasNext(); ) insert(conn, (UsuarioPropiedades) m.get((String) iter.next()));
    }

    public static int insert(Connection conn, UsuarioPropiedades up) throws SQLException {
        int retval = -1;
        PreparedStatement pstmnt = null;
        try {
            pstmnt = conn.prepareStatement("INSERT INTO cg_usuario_propiedades (u_login, up_nombre, up_valor) VALUES (?, ?, ?)");
            pstmnt.setString(1, up.getLogin());
            pstmnt.setString(2, up.getNombre());
            pstmnt.setString(2, up.getValor());
            retval = pstmnt.executeUpdate();
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        return retval;
    }

    public static Map<String, UsuarioPropiedades> select(Connection conn, String u_login) throws SQLException {
        Map<String, UsuarioPropiedades> m = new Hashtable();
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        try {
            pstmnt = conn.prepareStatement("SELECT * FROM cg_usuario_propiedades with(nolock) WHERE u_login = ?");
            pstmnt.setString(1, u_login);
            rs = pstmnt.executeQuery();
            while (rs.next()) {
                UsuarioPropiedades up = new UsuarioPropiedades();
                up.setLogin(rs.getString("u_login"));
                up.setNombre(rs.getString("up_nombre"));
                up.setValor(rs.getString("up_valor"));
                m.put(up.getNombre(), up);
            }
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            rs = null;
            pstmnt = null;
        }
        return m;
    }

    public static UsuarioPropiedades select(Connection conn, UsuarioPropiedades up) throws SQLException {
        UsuarioPropiedades rup = null;
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        try {
            StringBuffer where = new StringBuffer();
            String token = " WHERE ";
            if (up.getLogin() != null) {
                where.append(token + "u_login = ?");
                token = " AND ";
            }
            if (up.getNombre() != null) {
                where.append(token + "up_nombre = ?");
                token = " AND ";
            }
            if (up.getValor() != null) {
                where.append(token + "up_valor = ?");
                token = " AND ";
            }
            pstmnt = conn.prepareStatement("SELECT * FROM cg_usuario_propiedades with(nolock) " + where.toString());
            int i = 1;
            if (up.getLogin() != null)
                pstmnt.setString(i++, up.getLogin());
            if (up.getNombre() != null)
                pstmnt.setString(i++, up.getNombre());
            if (up.getValor() != null)
                pstmnt.setString(i++, up.getValor());
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                rup = new UsuarioPropiedades();
                rup.setLogin(rs.getString("u_login"));
                rup.setNombre(rs.getString("up_nombre"));
                rup.setValor(rs.getString("up_valor"));
            }
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            rs = null;
            pstmnt = null;
        }
        return rup;
    }

    public static int update(Connection conn, Map m) throws SQLException {
        int retval = 0;
        int count = -1;
        if (!m.isEmpty())
            for (Iterator iter = m.keySet().iterator(); iter.hasNext(); ) {
                String name = (String) iter.next();
                count = update(conn, (UsuarioPropiedades) m.get(name));
                retval += count;
            }
        return ((retval == 0) ? -1 : retval);
    }

    // FIXME Solo se actualizan o tambien se insertan?
    public static int update(Connection conn, UsuarioPropiedades up) throws SQLException {
        int retval = -1;
        PreparedStatement pstmnt = null;
        try {
            pstmnt = conn.prepareStatement("UPDATE cg_usuario_propiedades with(rowlock) SET up_valor = ? " + "WHERE u_login = ? AND up_nombre = ?");
            pstmnt.setString(1, up.getValor());
            pstmnt.setString(2, up.getLogin());
            pstmnt.setString(3, up.getNombre());
            retval = pstmnt.executeUpdate();
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        return retval;
    }
}
