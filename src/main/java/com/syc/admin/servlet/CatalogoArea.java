package com.syc.admin.servlet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Base64;

public class CatalogoArea {

    public static int delete(Connection conn, String id_area) throws SQLException {
        int retval = -1;
        PreparedStatement pstmnt = null;
        try {
            pstmnt = conn.prepareStatement("DELETE FROM cg_cat_areas WHERE id_area = ?");
            pstmnt.setString(1, id_area);
            retval = pstmnt.executeUpdate();
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        return retval;
    }

    public static int insert(Connection conn, CatArea ca) throws SQLException {
        int retval = -1;
        PreparedStatement pstmnt = null;
        try {
            pstmnt = conn.prepareStatement("INSERT INTO cg_cat_areas (id_area, d_descripcion, tipo_area, prefijo_folio) VALUES (?, ?, ?, ?)");
            pstmnt.setString(1, ca.getId_area());
            pstmnt.setString(2, ca.getD_descripcion());
            pstmnt.setString(3, ca.getTipo_area());
            pstmnt.setString(4, ca.getPrefijo_folio());
            retval = pstmnt.executeUpdate();
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        return retval;
    }

    public static String select(Connection conn, String id_area) throws SQLException {
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        String ru_login = null;
        try {
            pstmnt = conn.prepareStatement("SELECT id_area FROM cg_cat_areas WHERE id_area = ?");
            pstmnt.setString(1, id_area);
            rs = pstmnt.executeQuery();
            if (rs.next())
                ru_login = rs.getString(1);
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            rs = null;
            pstmnt = null;
        }
        return ru_login;
    }

    public static CatArea select(Connection conn, CatArea ca) throws SQLException {
        CatArea ru = null;
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        try {
            StringBuffer where = new StringBuffer();
            String token = " WHERE ";
            if (ca.getId_area() != null) {
                where.append(token + "id_area = ?");
                token = " AND ";
            }
            if (ca.getD_descripcion() != null) {
                where.append(token + "d_descripcion = ?");
                token = " AND ";
            }
            if (ca.getTipo_area() != null) {
                where.append(token + "tipo_area = ?");
                token = " AND ";
            }
            if (ca.getPrefijo_folio() != null) {
                where.append(token + "prefijo_folio = ?");
                token = " AND ";
            }
            pstmnt = conn.prepareStatement("SELECT * FROM cg_cat_areas " + where.toString());
            int i = 1;
            if (ca.getId_area() != null)
                pstmnt.setString(i++, ca.getId_area());
            if (ca.getD_descripcion() != null)
                pstmnt.setString(i++, ca.getD_descripcion());
            if (ca.getTipo_area() != null)
                pstmnt.setString(i++, ca.getTipo_area());
            if (ca.getPrefijo_folio() != null)
                pstmnt.setString(i++, ca.getPrefijo_folio());
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                ru = new CatArea();
                ru.setId_area(rs.getString("id_area"));
                ru.setD_descripcion(rs.getString("d_descripcion"));
                ru.setTipo_area(rs.getString("tipo_area"));
                ru.setPrefijo_folio(rs.getString("prefijo_folio"));
                /*ru.setPropiedades(UsuarioPropiedadesManager.select(conn, ru.getLogin()));
				ru.setGrupos(UsuarioGrupoManager.selectGrupos(conn, ru.getLogin()));
				ru.setRoles(UsuarioRoleManager.selectRoles(conn, ru.getLogin()));*/
            }
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            rs = null;
            pstmnt = null;
        }
        return ru;
    }

    public static List selectAll(Connection conn) throws SQLException {
        List usrList = new ArrayList();
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        try {
            pstmnt = conn.prepareStatement("SELECT * FROM cg_cat_areas ORDER BY d_descripcion");
            rs = pstmnt.executeQuery();
            while (rs.next()) {
                CatArea ca = new CatArea();
                ca.setId_area(rs.getString("id_area"));
                ca.setD_descripcion(rs.getString("d_descripcion"));
                ca.setTipo_area(rs.getString("tipo_area"));
                ca.setPrefijo_folio(rs.getString("prefijo_folio"));
                /*u.setPropiedades(UsuarioPropiedadesManager.select(conn, u.getLogin()));
				u.setGrupos(UsuarioGrupoManager.selectGrupos(conn, u.getLogin()));
				u.setRoles(UsuarioRoleManager.selectRoles(conn, u.getLogin()));*/
                usrList.add(ca);
            }
        } finally {
            if (rs != null)
                rs.close();
            if (pstmnt != null)
                pstmnt.close();
            rs = null;
            pstmnt = null;
        }
        return usrList;
    }

    public static int update(Connection conn, CatArea ca) throws SQLException {
        int retval = -1;
        PreparedStatement pstmnt = null;
        try {
            //pstmnt = conn.prepareStatement("UPDATE cg_cat_areas SET id_area = ?, d_descripcion = ?"
            //+ ", tipo_area = ? WHERE id_area = ?");
            pstmnt = conn.prepareStatement("UPDATE cg_cat_areas SET d_descripcion = ?, tipo_area = ?, prefijo_folio = ? WHERE id_area = ?");
            pstmnt.setString(1, ca.getD_descripcion());
            pstmnt.setString(2, ca.getTipo_area());
            pstmnt.setString(3, ca.getPrefijo_folio());
            pstmnt.setString(4, ca.getId_area());
            //pstmnt.setString(1, "'"+ca.getD_descripcion()+"'");
            //pstmnt.setString(2, "'"+ca.getTipo_area()+"'");
            //pstmnt.setString(3, "'"+ca.getId_area()+"'");
            //String desc = ca.getD_descripcion();
            //String tipo = ca.getTipo_area();
            //String id = ca.getId_area();
            //String qry =  "UPDATE cg_cat_areas SET d_descripcion = '"+desc+"', tipo_area = '"+tipo+"' WHERE id_area = '"+id;
            //UsuarioPropiedadesManager.update(conn, ca.getPropiedades());
            // FIXME Se deben actualizar
            // UsuarioGrupoManager.update(conn, u.getGrupos());
            // UsuarioRoleManager.update(conn, u.getRoles());
            retval = pstmnt.executeUpdate();
        } finally {
            if (pstmnt != null)
                pstmnt.close();
            pstmnt = null;
        }
        return retval;
    }
}
