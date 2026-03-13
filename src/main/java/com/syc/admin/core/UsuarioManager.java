package com.syc.admin.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsuarioManager {

	public static int delete(Connection conn, String u_login) throws SQLException {

		int retval = -1;
		PreparedStatement pstmnt = null;

		try {
			pstmnt = conn.prepareStatement("DELETE FROM cg_usuario WHERE u_login = ?");

			pstmnt.setString(1, u_login);

			//UsuarioPropiedadesManager.delete(conn, u_login);
			//UsuarioGrupoManager.delete(conn, u_login);
			//UsuarioRoleManager.delete(conn, u_login);

			retval = pstmnt.executeUpdate();
		} finally {
			if (pstmnt != null)
				pstmnt.close();

			pstmnt = null;
		}

		return retval;
	}

	public static int insert(Connection conn, Usuario u) throws SQLException {

		int retval = -1;
		PreparedStatement pstmnt = null;

		try {
			pstmnt = conn
				.prepareStatement("INSERT INTO cg_usuario (u_login, u_password, u_nombre, u_descripcion) VALUES (?, ?, ?, ?)");

			pstmnt.setString(1, u.getLogin());
			pstmnt.setString(2, u.getPassword());
			pstmnt.setString(3, u.getNombre());
			pstmnt.setString(4, u.getDescripcion());

			//UsuarioPropiedadesManager.insert(conn, u.getPropiedades());
			//UsuarioGrupoManager.insert(conn, u.getGrupos());
			//UsuarioRoleManager.insert(conn, u.getRoles());

			retval = pstmnt.executeUpdate();
		} finally {
			if (pstmnt != null)
				pstmnt.close();

			pstmnt = null;
		}

		return retval;
	}

	public static String select(Connection conn, String u_login) throws SQLException {

		PreparedStatement pstmnt = null;
		ResultSet rs = null;
		String ru_login = null;

		try {
			pstmnt = conn.prepareStatement("SELECT u_login FROM cg_usuario WHERE u_login = ?");

			pstmnt.setString(1, u_login);
			
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

	public static Usuario select(Connection conn, Usuario u) throws SQLException {

		Usuario ru = null;
		PreparedStatement pstmnt = null;
		ResultSet rs = null;

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

			pstmnt = conn.prepareStatement("SELECT * FROM cg_usuario " + where.toString());

			int i = 1;
			if (u.getLogin() != null)
				pstmnt.setString(i++, u.getLogin());

			if (u.getPassword() != null)
				pstmnt.setString(i++, u.getPassword());

			if (u.getNombre() != null)
				pstmnt.setString(i++, u.getNombre());

			if (u.getDescripcion() != null)
				pstmnt.setString(i++, u.getDescripcion());

			rs = pstmnt.executeQuery();

			if (rs.next()) {
				ru = new Usuario();

				ru.setLogin(rs.getString("u_login"));
				ru.setPassword(rs.getString("u_password"));
				ru.setNombre(rs.getString("u_nombre"));
				ru.setDescripcion(rs.getString("u_descripcion"));
				//ru.setPropiedades(UsuarioPropiedadesManager.select(conn, ru.getLogin()));
				//ru.setGrupos(UsuarioGrupoManager.selectGrupos(conn, ru.getLogin()));
				//ru.setRoles(UsuarioRoleManager.selectRoles(conn, ru.getLogin()));
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
			pstmnt = conn.prepareStatement("SELECT * FROM cg_usuario ORDER BY u_nombre");

			rs = pstmnt.executeQuery();

			while (rs.next()) {
				Usuario u = new Usuario();

				u.setLogin(rs.getString("u_login"));
				u.setPassword(rs.getString("u_password"));
				u.setNombre(rs.getString("u_nombre"));
				u.setDescripcion(rs.getString("u_descripcion"));
				//u.setPropiedades(UsuarioPropiedadesManager.select(conn, u.getLogin()));
				//u.setGrupos(UsuarioGrupoManager.selectGrupos(conn, u.getLogin()));
				//u.setRoles(UsuarioRoleManager.selectRoles(conn, u.getLogin()));

				usrList.add(u);
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

	public static int update(Connection conn, Usuario u) throws SQLException {

		int retval = -1;
		PreparedStatement pstmnt = null;

		try {
			pstmnt = conn.prepareStatement("UPDATE cg_usuario SET u_password = ?, u_nombre = ?"
				+ ", u_descripcion = ? WHERE u_login = ?");

			pstmnt.setString(1, u.getPassword());
			pstmnt.setString(2, u.getNombre());
			pstmnt.setString(3, u.getDescripcion());
			pstmnt.setString(4, u.getLogin());

			//UsuarioPropiedadesManager.update(conn, u.getPropiedades());
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
