package com.syc.admin.servlet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CatalogoCargo {

	public static int delete(Connection conn, int id_cargo) throws SQLException {

		int retval = -1;
		PreparedStatement pstmnt = null;

		try {
			pstmnt = conn.prepareStatement("DELETE FROM cg_cat_cargo WHERE id_cargo = ?");

			pstmnt.setInt(1, id_cargo);

			retval = pstmnt.executeUpdate();
		} finally {
			if (pstmnt != null)
				pstmnt.close();

			pstmnt = null;
		}

		return retval;
	}

	public static int insert(Connection conn, CatCargo cc) throws SQLException {

		int retval = -1;
		PreparedStatement pstmnt = null;

		try {
			pstmnt = conn
				.prepareStatement("INSERT INTO cg_cat_cargo (id_cargo, cc_descripcion) VALUES (?, ?)");

			pstmnt.setInt(1, cc.getId_cargo());
			pstmnt.setString(2, cc.getCc_descripcion());
			/*pstmnt.setString(3, cc.getNombre());
			pstmnt.setString(4, cc.getDescripcion());*/

			/*UsuarioPropiedadesManager.insert(conn, u.getPropiedades());
			UsuarioGrupoManager.insert(conn, u.getGrupos());
			UsuarioRoleManager.insert(conn, u.getRoles());*/

			retval = pstmnt.executeUpdate();
		} finally {
			if (pstmnt != null)
				pstmnt.close();

			pstmnt = null;
		}

		return retval;
	}

	public static String select(Connection conn, int id_cargo) throws SQLException {

		PreparedStatement pstmnt = null;
		ResultSet rs = null;
		String ru_login = null;

		try {
			pstmnt = conn.prepareStatement("SELECT id_cargo FROM cg_cat_cargo WHERE id_cargo = ?");

			pstmnt.setInt(1, id_cargo);
			
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

	public static CatCargo select(Connection conn, CatCargo cc) throws SQLException {

		CatCargo ru = null;
		PreparedStatement pstmnt = null;
		ResultSet rs = null;

		try {
			StringBuffer where = new StringBuffer();
			String token = " WHERE ";

			if (cc.getId_cargo() != -1) {
				where.append(token + "id_cargo = ?");
				token = " AND ";
			}

			if (cc.getCc_descripcion()!= null) {
				where.append(token + "cc_descripcion = ?");
				token = " AND ";
			}

			/*if (u.getNombre() != null) {
				where.append(token + "u_nombre = ?");
				token = " AND ";
			}

			if (u.getDescripcion() != null) {
				where.append(token + "u_descripcion = ?");
				token = " AND ";
			}*/

			pstmnt = conn.prepareStatement("SELECT * FROM cg_cat_cargo " + where.toString());

			int i = 1;
			if (cc.getId_cargo() != -1)
				pstmnt.setInt(i++, cc.getId_cargo());

			if (cc.getCc_descripcion()!= null)
				pstmnt.setString(i++, cc.getCc_descripcion());

			/*if (u.getNombre() != null)
				pstmnt.setString(i++, u.getNombre());

			if (u.getDescripcion() != null)
				pstmnt.setString(i++, u.getDescripcion());*/

			rs = pstmnt.executeQuery();

			if (rs.next()) {
				ru = new CatCargo();

				ru.setId_cargo(rs.getInt("id_cargo"));
				ru.setCc_descripcion(rs.getString("cc_descripcion"));
				/*ru.setNombre(rs.getString("u_nombre"));
				ru.setDescripcion(rs.getString("u_descripcion"));*/
				
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
			pstmnt = conn.prepareStatement("SELECT * FROM cg_cat_cargo ORDER BY cc_descripcion");

			rs = pstmnt.executeQuery();

			while (rs.next()) {
				CatCargo cc = new CatCargo();

				cc.setId_cargo(rs.getInt("id_cargo"));
				cc.setCc_descripcion(rs.getString("cc_descripcion"));
				/*u.setNombre(rs.getString("u_nombre"));
				u.setDescripcion(rs.getString("u_descripcion"));*/
				
				/*u.setPropiedades(UsuarioPropiedadesManager.select(conn, u.getLogin()));
				u.setGrupos(UsuarioGrupoManager.selectGrupos(conn, u.getLogin()));
				u.setRoles(UsuarioRoleManager.selectRoles(conn, u.getLogin()));*/

				usrList.add(cc);
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

	public static int update(Connection conn, CatCargo cc) throws SQLException {

		int retval = -1;
		PreparedStatement pstmnt = null;

		try {
			pstmnt = conn.prepareStatement("UPDATE cg_cat_cargo SET cc_descripcion = ? WHERE id_cargo = ?");

			pstmnt.setString(1, cc.getCc_descripcion());
			pstmnt.setInt(2, cc.getId_cargo());
			/*pstmnt.setString(3, u.getDescripcion());
			pstmnt.setString(4, u.getLogin());*/

			//UsuarioPropiedadesManager.update(conn, cc.getPropiedades());
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
