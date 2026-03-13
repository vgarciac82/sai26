package com.syc.gestion.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

public class UsuarioRoleManager {

	public static int delete(Connection conn, String u_login) throws SQLException {

		int retval = -1;
		PreparedStatement pstmnt = null;

		try {
			pstmnt = conn.prepareStatement("DELETE FROM cg_usuario_role with(rowlock) WHERE u_login = ?");

			pstmnt.setString(1, u_login);

			retval = pstmnt.executeUpdate();
		} finally {
			if (pstmnt != null)
				pstmnt.close();

			pstmnt = null;
		}

		return retval;
	}

	public static int delete(Connection conn, String u_login, String r_nombre) throws SQLException {

		int retval = -1;
		PreparedStatement pstmnt = null;

		try {
			pstmnt = conn.prepareStatement("DELETE FROM cg_usuario_role with(rowlock) WHERE u_login = ? AND r_nombre = ?");

			pstmnt.setString(1, u_login);
			pstmnt.setString(2, r_nombre);

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
			for (Iterator iter = m.keySet().iterator(); iter.hasNext();)
				insert(conn, (UsuarioRole) iter.next());
	}

	public static int insert(Connection conn, UsuarioRole ur) throws SQLException {

		int retval = -1;
		PreparedStatement pstmnt = null;

		try {
			pstmnt = conn.prepareStatement("INSERT INTO cg_usuario_role " + "(u_login, r_nombre) VALUES (?, ?)");

			pstmnt.setString(1, ur.getLogin());
			pstmnt.setString(2, ur.getNombre());

			retval = pstmnt.executeUpdate();
		} finally {
			if (pstmnt != null)
				pstmnt.close();

			pstmnt = null;
		}

		return retval;
	}

	public static UsuarioRole select(Connection conn, UsuarioRole ur) throws SQLException {

		UsuarioRole rur = null;
		PreparedStatement pstmnt = null;
		ResultSet rs = null;

		try {
			StringBuffer where = new StringBuffer();
			String token = " WHERE ";

			if (ur.getLogin() != null) {
				where.append(token + "u_login = ?");
				token = " AND ";
			}

			if (ur.getNombre() != null) {
				where.append(token + "r_nombre = ?");
				token = " AND ";
			}

			pstmnt = conn.prepareStatement("SELECT * FROM cg_usuario_role with(nolock) " + where.toString());

			int i = 1;
			if (ur.getLogin() != null)
				pstmnt.setString(i++, ur.getLogin());

			if (ur.getNombre() != null)
				pstmnt.setString(i++, ur.getNombre());

			rs = pstmnt.executeQuery();

			if (rs.next()) {
				rur = new UsuarioRole();

				rur.setLogin(rs.getString("u_login"));
				rur.setNombre(rs.getString("r_nombre"));
			}
		} finally {
			if (rs != null)
				rs.close();

			if (pstmnt != null)
				pstmnt.close();

			rs = null;
			pstmnt = null;
		}

		return rur;
	}

	public static Map<String, Role> selectRoles(Connection conn, String u_login) throws SQLException {

		Map<String, Role> m = new Hashtable<String, Role>();
		PreparedStatement pstmnt = null;
		ResultSet rs = null;

		try {
			pstmnt = conn.prepareStatement("SELECT r.r_nombre, r.r_descripcion "
				+ "FROM cg_role r with(nolock), cg_usuario_role ur with(nolock) WHERE r.r_nombre = ur.r_nombre AND ur.u_login = ?");

			pstmnt.setString(1, u_login);

			rs = pstmnt.executeQuery();

			while (rs.next()) {
				Role r = new Role();

				r.setNombre(rs.getString("r_nombre"));
				r.setDescripcion(rs.getString("r_descripcion"));

				m.put(r.getNombre(), r);
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

	public static Map selectUsuarios(Connection conn, String r_nombre) throws SQLException {

		Map m = new Hashtable();
		PreparedStatement pstmnt = null;
		ResultSet rs = null;

		try {
			pstmnt = conn.prepareStatement("SELECT u.u_login, u.u_password, u.u_nombre, u.u_descripcion "
				+ "FROM cg_usuario u with(nolock), cg_usuario_role ur with(nolock) WHERE u.u_login = ur.u_login AND ur.g_nombre = ?");

			pstmnt.setString(1, r_nombre);

			rs = pstmnt.executeQuery();

			while (rs.next()) {
				Usuario u = new Usuario();

				u.setLogin(rs.getString("u_login"));
				u.setPassword(rs.getString("u_password"));
				u.setNombre(rs.getString("u_nombre"));
				u.setDescripcion(rs.getString("u_descripcion"));
				u.setPropiedades(UsuarioPropiedadesManager.select(conn, u.getLogin()));
				u.setGrupos(UsuarioGrupoManager.selectGrupos(conn, u.getLogin()));
				u.setRoles(selectRoles(conn, u.getLogin()));
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

	public static int update(Connection conn, UsuarioRole ur) throws SQLException {
		return -1;
	}
}
