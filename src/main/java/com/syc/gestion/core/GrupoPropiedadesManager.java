package com.syc.gestion.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Map;

public class GrupoPropiedadesManager {

	public static int delete(Connection conn, String g_nombre) throws SQLException {

		int retval = -1;
		PreparedStatement pstmnt = null;

		try {
			pstmnt = conn.prepareStatement("DELETE FROM cg_grupo_propiedades with(rowlock) WHERE g_nombre = ?");

			pstmnt.setString(1, g_nombre);

			retval = pstmnt.executeUpdate();
		} finally {
			if (pstmnt != null)
				pstmnt.close();

			pstmnt = null;
		}

		return retval;
	}

	public static int delete(Connection conn, String g_nombre, String gp_nombre) throws SQLException {

		int retval = -1;
		PreparedStatement pstmnt = null;

		try {
			pstmnt = conn.prepareStatement("DELETE FROM cg_grupo_propiedades with(rowlock) WHERE g_nombre = ? AND gp_nombre = ?");

			pstmnt.setString(1, g_nombre);
			pstmnt.setString(2, gp_nombre);

			retval = pstmnt.executeUpdate();
		} finally {
			if (pstmnt != null)
				pstmnt.close();

			pstmnt = null;
		}

		return retval;
	}

	public static int insert(Connection conn, GrupoPropiedades gp) throws SQLException {

		int retval = -1;
		PreparedStatement pstmnt = null;

		try {
			pstmnt = conn
				.prepareStatement("INSERT INTO cg_grupo_propiedades (g_nombre, gp_nombre, gp_valor) VALUES (?, ?, ?)");

			pstmnt.setString(1, gp.getGrupo());
			pstmnt.setString(2, gp.getNombre());
			pstmnt.setString(2, gp.getValor());

			retval = pstmnt.executeUpdate();
		} finally {
			if (pstmnt != null)
				pstmnt.close();

			pstmnt = null;
		}

		return retval;
	}

	public static Map select(Connection conn, String g_nombre) throws SQLException {

		Map m = new Hashtable();
		PreparedStatement pstmnt = null;
		ResultSet rs = null;

		try {
			pstmnt = conn.prepareStatement("SELECT * FROM cg_grupo_propiedades with(nolock) WHERE g_nombre = ?");

			pstmnt.setString(1, g_nombre);

			rs = pstmnt.executeQuery();

			while (rs.next()) {

				GrupoPropiedades gp = new GrupoPropiedades();

				gp.setGrupo(rs.getString("g_nombre"));
				gp.setNombre(rs.getString("gp_nombre"));
				gp.setValor(rs.getString("gp_valor"));

				m.put(gp.getNombre(), gp);
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

	public static GrupoPropiedades select(Connection conn, GrupoPropiedades gp) throws SQLException {

		GrupoPropiedades rgp = null;
		PreparedStatement pstmnt = null;
		ResultSet rs = null;

		try {
			StringBuffer where = new StringBuffer();
			String token = " WHERE ";

			if (gp.getGrupo() != null) {
				where.append(token + "g_nombre = ?");
				token = " AND ";
			}

			if (gp.getNombre() != null) {
				where.append(token + "gp_nombre = ?");
				token = " AND ";
			}

			if (gp.getValor() != null) {
				where.append(token + "gp_valor = ?");
				token = " AND ";
			}

			pstmnt = conn.prepareStatement("SELECT * FROM cg_grupo_propiedades with(nolock) " + where.toString());

			int i = 1;
			if (gp.getGrupo() != null)
				pstmnt.setString(i++, gp.getGrupo());

			if (gp.getNombre() != null)
				pstmnt.setString(i++, gp.getNombre());

			if (gp.getValor() != null)
				pstmnt.setString(i++, gp.getValor());

			rs = pstmnt.executeQuery();

			if (rs.next()) {
				rgp = new GrupoPropiedades();

				rgp.setGrupo(rs.getString("g_nombre"));
				rgp.setNombre(rs.getString("gp_nombre"));
				rgp.setValor(rs.getString("gp_valor"));
			}
		} finally {
			if (rs != null)
				rs.close();

			if (pstmnt != null)
				pstmnt.close();

			rs = null;
			pstmnt = null;
		}

		return rgp;
	}

	public static int update(Connection conn, GrupoPropiedades gp) throws SQLException {

		int retval = -1;
		PreparedStatement pstmnt = null;

		try {
			pstmnt = conn.prepareStatement("UPDATE cg_grupo_propiedades " + "SET gp_valor = ?"
				+ "WHERE g_nombre = ?  AND gp_nombre = ?");

			pstmnt.setString(1, gp.getValor());
			pstmnt.setString(2, gp.getGrupo());
			pstmnt.setString(3, gp.getNombre());

			retval = pstmnt.executeUpdate();
		} finally {
			if (pstmnt != null)
				pstmnt.close();

			pstmnt = null;
		}

		return retval;
	}
}
