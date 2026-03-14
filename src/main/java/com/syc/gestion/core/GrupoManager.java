package com.syc.gestion.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import java.util.Base64;

public class GrupoManager {

    public static int delete(Connection conn, String g_nombre) throws SQLException {
        int retval = -1;
        PreparedStatement pstmnt = null;
        try {
            pstmnt = conn.prepareStatement("DELETE FROM cg_grupo with(rowlock) WHERE g_nombre = ?");
            pstmnt.setString(1, g_nombre);
            GrupoPropiedadesManager.delete(conn, g_nombre);
            retval = pstmnt.executeUpdate();
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        return retval;
    }

    public static int insert(Connection conn, Grupo g) throws SQLException {
        int retval = -1;
        PreparedStatement pstmnt = null;
        try {
            pstmnt = conn.prepareStatement("INSERT INTO cg_grupo (g_nombre, g_descripcion) VALUES (?, ?)");
            pstmnt.setString(1, g.getNombre());
            pstmnt.setString(2, g.getDescripcion());
            if (!g.getPropiedades().isEmpty()) {
                for (Iterator iter = g.getPropiedades().keySet().iterator(); iter.hasNext(); ) {
                    String name = (String) iter.next();
                    GrupoPropiedadesManager.insert(conn, g.getPropiedad(name));
                }
            }
            retval = pstmnt.executeUpdate();
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        return retval;
    }

    public static Grupo select(Connection conn, Grupo g) throws SQLException {
        Grupo rg = null;
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        try {
            StringBuffer where = new StringBuffer();
            String token = " WHERE ";
            if (g.getNombre() != null) {
                where.append(token + "g_nombre = ?");
                token = " AND ";
            }
            if (g.getDescripcion() != null) {
                where.append(token + "g_descripcion = ?");
                token = " AND ";
            }
            pstmnt = conn.prepareStatement("SELECT * FROM cg_grupo with(nolock) " + where.toString());
            int i = 1;
            if (g.getNombre() != null)
                pstmnt.setString(i++, g.getNombre());
            if (g.getDescripcion() != null)
                pstmnt.setString(i++, g.getDescripcion());
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                rg = new Grupo();
                rg.setNombre(rs.getString("g_nombre"));
                rg.setDescripcion(rs.getString("g_descripcion"));
                rg.setPropiedades(GrupoPropiedadesManager.select(conn, rg.getNombre()));
            }
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            rs = null;
            pstmnt = null;
        }
        return rg;
    }

    public static List selectAll(Connection conn) throws SQLException {
        List grpList = new ArrayList();
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        try {
            pstmnt = conn.prepareStatement("SELECT * FROM cg_grupo with(nolock) ORDER BY g_descripcion");
            rs = pstmnt.executeQuery();
            while (rs.next()) {
                Grupo g = new Grupo();
                g.setNombre(rs.getString("g_nombre"));
                g.setDescripcion(rs.getString("g_descripcion"));
                g.setPropiedades(GrupoPropiedadesManager.select(conn, g.getNombre()));
                grpList.add(g);
            }
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            rs = null;
            pstmnt = null;
        }
        return grpList;
    }

    public static int update(Connection conn, Grupo g) throws SQLException {
        int retval = -1;
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        try {
            pstmnt = conn.prepareStatement("UPDATE cg_grupo with(rowlock) " + "SET g_descripcion = ?" + "WHERE g_nombre = ?");
            pstmnt.setString(1, g.getDescripcion());
            pstmnt.setString(2, g.getNombre());
            // FIXME Solo se actualizan o tambien se insertan?
            if (!g.getPropiedades().isEmpty()) {
                for (Iterator iter = g.getPropiedades().keySet().iterator(); iter.hasNext(); ) {
                    String name = (String) iter.next();
                    GrupoPropiedadesManager.update(conn, g.getPropiedad(name));
                }
            }
            retval = pstmnt.executeUpdate();
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            rs = null;
            pstmnt = null;
        }
        return retval;
    }

    public static List<String> selectAllMembers(Connection conn, Grupo grupo) throws Exception {
        String query = "SELECT u_login FROM cg_usuario_grupo WITH(NOLOCK) WHERE g_nombre = ?";
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<String> membersGroup = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, grupo.getNombre());
            rs = ps.executeQuery();
            while (rs.next()) {
                if (membersGroup == null)
                    membersGroup = new ArrayList<String>();
                membersGroup.add(rs.getString(1));
            }
            return membersGroup;
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(ps);
        }
    }
}
