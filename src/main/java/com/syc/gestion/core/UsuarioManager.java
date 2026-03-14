package com.syc.gestion.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.axtel.egresos.compromiso.Ramo;
import com.syc.cfdi.db.CloseObject;
import java.util.Base64;

public class UsuarioManager {

    public static int delete(Connection conn, String u_login) throws SQLException {
        int retval = -1;
        PreparedStatement pstmnt = null;
        try {
            pstmnt = conn.prepareStatement("DELETE FROM cg_usuario with(rowlock) WHERE u_login = ?");
            pstmnt.setString(1, u_login);
            UsuarioPropiedadesManager.delete(conn, u_login);
            UsuarioGrupoManager.delete(conn, u_login);
            UsuarioRoleManager.delete(conn, u_login);
            retval = pstmnt.executeUpdate();
        } finally {
            CloseObject.closeObject(pstmnt);
        }
        return retval;
    }

    public static int insert(Connection conn, Usuario u) throws SQLException {
        int retval = -1;
        PreparedStatement pstmnt = null;
        try {
            pstmnt = conn.prepareStatement("INSERT INTO cg_usuario (u_login, u_password, u_nombre, u_descripcion) VALUES (?, ?)");
            pstmnt.setString(1, u.getLogin());
            pstmnt.setString(2, u.getPassword());
            pstmnt.setString(3, u.getNombre());
            pstmnt.setString(4, u.getDescripcion());
            UsuarioPropiedadesManager.insert(conn, u.getPropiedades());
            UsuarioGrupoManager.insert(conn, u.getGrupos());
            UsuarioRoleManager.insert(conn, u.getRoles());
            retval = pstmnt.executeUpdate();
        } finally {
            CloseObject.closeObject(pstmnt);
        }
        return retval;
    }

    public static String select(Connection conn, String u_login) throws SQLException {
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        String ru_login = null;
        try {
            pstmnt = conn.prepareStatement("SELECT u_login FROM cg_usuario with(nolock) WHERE u_login = ?");
            pstmnt.setString(1, u_login);
            rs = pstmnt.executeQuery();
            if (rs.next())
                ru_login = rs.getString(1);
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(pstmnt);
        }
        return ru_login;
    }

    public static Usuario select(Connection conn, Usuario u) throws SQLException {
        Usuario ru = null;
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        Ramo r = new Ramo(conn);
        try {
            StringBuffer where = new StringBuffer();
            String token = " WHERE ";
            if (u.getLogin() != null) {
                where.append(token + "u_login = ?");
                token = " AND ";
            }
            if (u.getPassword() != null) {
                where.append(token + "u_password = ?");
                token = " AND ";
            }
            if (u.getNombre() != null) {
                where.append(token + "u_nombre = ?");
                token = " AND ";
            }
            if (u.getDescripcion() != null) {
                where.append(token + "u_descripcion = ?");
                token = " AND ";
            }
            if (u.getU_email() != null) {
                where.append(token + "u_email = ?");
                token = " AND ";
            }
            if (u.getNumeroEmpleado() != null) {
                where.append(token + " cNumeroEmpleado = ?");
                token = " AND ";
            }
            //24-01-2013
            where.append(token + " u_estatus = 'A'");
            pstmnt = conn.prepareStatement("SELECT * FROM cg_usuario with(nolock)" + where.toString());
            int i = 1;
            if (u.getLogin() != null)
                pstmnt.setString(i++, u.getLogin());
            if (u.getPassword() != null)
                pstmnt.setString(i++, u.getPassword());
            if (u.getNombre() != null)
                pstmnt.setString(i++, u.getNombre());
            if (u.getDescripcion() != null)
                pstmnt.setString(i++, u.getDescripcion());
            if (u.getU_email() != null)
                pstmnt.setString(i++, u.getU_email());
            if (u.getNumeroEmpleado() != null)
                pstmnt.setString(i++, u.getNumeroEmpleado());
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                ru = new Usuario();
                ru.setLogin(rs.getString("u_login"));
                ru.setPassword(rs.getString("u_password"));
                ru.setNombre(rs.getString("u_nombre"));
                ru.setDescripcion(rs.getString("u_descripcion"));
                ru.setEstatus(rs.getString("u_estatus"));
                ru.setU_email(rs.getString("u_email"));
                ru.setNumeroEmpleado(rs.getString("cNumeroEmpleado"));
                ru.setuRFC(rs.getString("cRFC"));
                ru.setPropiedades(UsuarioPropiedadesManager.select(conn, ru.getLogin()));
                ru.setGrupos(UsuarioGrupoManager.selectGrupos(conn, ru.getLogin()));
                ru.setRoles(UsuarioRoleManager.selectRoles(conn, ru.getLogin()));
                ru.setU_Ramo(r.getIdRamo());
            }
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(pstmnt);
        }
        return ru;
    }

    public static List<Usuario> selectAll(Connection conn) throws SQLException {
        List<Usuario> usrList = new ArrayList<Usuario>();
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        try {
            pstmnt = conn.prepareStatement("SELECT * FROM cg_usuario with(nolock) ORDER BY u_nombre");
            rs = pstmnt.executeQuery();
            while (rs.next()) {
                Usuario u = new Usuario();
                u.setLogin(rs.getString("u_login"));
                u.setPassword(rs.getString("u_password"));
                u.setNombre(rs.getString("u_nombre"));
                u.setDescripcion(rs.getString("u_descripcion"));
                u.setPropiedades(UsuarioPropiedadesManager.select(conn, u.getLogin()));
                u.setGrupos(UsuarioGrupoManager.selectGrupos(conn, u.getLogin()));
                u.setRoles(UsuarioRoleManager.selectRoles(conn, u.getLogin()));
                usrList.add(u);
            }
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(pstmnt);
        }
        return usrList;
    }

    public static int update(Connection conn, Usuario u) throws SQLException {
        int retval = -1;
        PreparedStatement pstmnt = null;
        try {
            pstmnt = conn.prepareStatement("UPDATE cg_usuario with(rowlock) SET u_password = ?, u_nombre = ?" + ", u_descripcion = ? WHERE u_login = ?");
            pstmnt.setString(1, u.getPassword());
            pstmnt.setString(2, u.getNombre());
            pstmnt.setString(3, u.getDescripcion());
            pstmnt.setString(4, u.getLogin());
            UsuarioPropiedadesManager.update(conn, u.getPropiedades());
            // FIXME Se deben actualizar
            // UsuarioGrupoManager.update(conn, u.getGrupos());
            // UsuarioRoleManager.update(conn, u.getRoles());
            retval = pstmnt.executeUpdate();
        } finally {
            CloseObject.closeObject(pstmnt);
        }
        return retval;
    }

    public static Usuario getRamoUR(Connection conn, Usuario u) throws SQLException {
        PreparedStatement pstmnt = null;
        ResultSet rs = null;
        try {
            pstmnt = conn.prepareStatement("SELECT u.cUnidadResponsable, u.cRamo " + "  FROM tCatalogoURCC c with(nolock) " + "     , tCatalogoUnidadResponsable u with(nolock) " + "     , CG_CAT_EMPLEADO e with(nolock) " + " WHERE cCentroContable = ? " + "   AND c.cUnidadResponsable  = u.cUnidadResponsable " + "   AND u.ID_AREA = e.ID_AREA " + "   AND e.CE_OS_RESPONSABLE = ?");
            pstmnt.setString(1, (u.getPropiedades() != null && u.getPropiedades().containsKey("CCENTROCONTABLE") ? u.getPropiedad("CCENTROCONTABLE").getValor() : ""));
            pstmnt.setString(2, u.getLogin());
            rs = pstmnt.executeQuery();
            if (rs.next()) {
                u.setU_UR(rs.getString("cUnidadResponsable"));
                u.setU_UR_Orig(rs.getString("cUnidadResponsable"));
                u.setU_Ramo(rs.getString("cRamo"));
            }
        } finally {
            CloseObject.closeObject(rs);
            CloseObject.closeObject(pstmnt);
        }
        return u;
    }
}
