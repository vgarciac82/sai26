package com.syc.gestion.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class RoleManager {

	public static int delete(Connection conn, String r_nombre) throws SQLException {

		int retval = -1;
		PreparedStatement pstmnt = null;

		try {
			pstmnt = conn.prepareStatement("DELETE FROM cg_role WHERE r_nombre = ?");

			pstmnt.setString(1, r_nombre);

			retval = pstmnt.executeUpdate();
		} finally {
			if (pstmnt != null)
				pstmnt.close();

			pstmnt = null;
		}

		return retval;
	}

	public static int insert(Connection conn, Role r) throws SQLException {

		int retval = -1;
		PreparedStatement pstmnt = null;

		try {
			pstmnt = conn.prepareStatement("INSERT INTO cg_role (r_nombre, r_descripcion) VALUES (?, ?)");

			pstmnt.setString(1, r.getNombre());
			pstmnt.setString(2, r.getDescripcion());

			retval = pstmnt.executeUpdate();
		} finally {
			if (pstmnt != null)
				pstmnt.close();

			pstmnt = null;
		}

		return retval;
	}

	public static Role select(Connection conn, Role r) throws SQLException {

		Role rr = null;
		PreparedStatement pstmnt = null;
		ResultSet rs = null;

		try {
			StringBuffer where = new StringBuffer();
			String token = " WHERE ";

			if (r.getNombre() != null) {
				where.append(token + "r_nombre = ?");
				token = " AND ";
			}

			if (r.getDescripcion() != null) {
				where.append(token + "r_descripcion = ?");
				token = " AND ";
			}

			pstmnt = conn.prepareStatement("SELECT * FROM cg_role " + where.toString());

			int i = 1;
			if (r.getNombre() != null)
				pstmnt.setString(i++, r.getNombre());

			if (r.getDescripcion() != null)
				pstmnt.setString(i++, r.getDescripcion());

			rs = pstmnt.executeQuery();

			if (rs.next()) {
				rr = new Role();

				rr.setNombre(rs.getString("r_nombre"));
				rr.setDescripcion(rs.getString("r_descripcion"));
			}
		} finally {
			if (rs != null)
				rs.close();

			if (pstmnt != null)
				pstmnt.close();

			rs = null;
			pstmnt = null;
		}

		return rr;
	}

	public static int update(Connection conn, Role r) throws SQLException {

		int retval = -1;
		PreparedStatement pstmnt = null;

		try {
			pstmnt = conn.prepareStatement("UPDATE cg_role " + "SET r_descripcion = ?" + "WHERE r_nombre = ?");

			pstmnt.setString(1, r.getDescripcion());
			pstmnt.setString(2, r.getNombre());

			retval = pstmnt.executeUpdate();
		} finally {
			if (pstmnt != null)
				pstmnt.close();

			pstmnt = null;
		}

		return retval;
	}
}
