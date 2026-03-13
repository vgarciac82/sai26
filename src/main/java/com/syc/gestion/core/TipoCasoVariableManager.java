package com.syc.gestion.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public class TipoCasoVariableManager {

	public static int delete(Connection conn, int id_tc, int id_tcv) throws SQLException {

		int retval = -1;
		PreparedStatement pstmnt = null;

		try {
			pstmnt = conn.prepareStatement("DELETE FROM cg_tipo_caso_variable with(rowlock) WHERE id_tc = ? AND id_tcv = ?");

			pstmnt.setInt(1, id_tc);
			pstmnt.setInt(2, id_tcv);

			retval = pstmnt.executeUpdate();
		} finally {
			if (pstmnt != null)
				pstmnt.close();

			pstmnt = null;
		}

		return retval;
	}

	public static int insert(Connection conn, TipoCasoVariable tcv) throws SQLException {

		int retval = -1;
		PreparedStatement pstmnt = null;

		try {
			pstmnt = conn.prepareStatement("INSERT INTO cg_tipo_caso_variable (id_tc, id_tcv, tcv_nombre, "
					+ "tcv_descripcion, tcv_etiqueta, tcv_tipo, tcv_longitud, tcv_indice, tcv_en_gaveta) "
					+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");

			pstmnt.setInt(1, tcv.getIdTC());
			pstmnt.setInt(2, tcv.getIdTCV());
			pstmnt.setString(3, tcv.getNombre());
			pstmnt.setString(4, tcv.getDescripcion());
			pstmnt.setString(5, tcv.getEtiqueta());
			pstmnt.setInt(6, tcv.getTipo());
			pstmnt.setInt(7, tcv.getLongitud());
			pstmnt.setInt(8, tcv.getIndice());
			pstmnt.setString(9, tcv.getEnGaveta());

			retval = pstmnt.executeUpdate();
		} finally {
			if (pstmnt != null)
				pstmnt.close();

			pstmnt = null;
		}

		return retval;
	}

	public static TipoCasoVariable select(Connection conn, int id_tc, int id_tcv) throws SQLException {

		TipoCasoVariable rtcv = null;
		PreparedStatement pstmnt = null;
		ResultSet rs = null;

		try {
			pstmnt = conn.prepareStatement("SELECT * FROM cg_tipo_caso_variable WHERE id_tc = ? AND id_tcv = ?");

			pstmnt.setInt(1, id_tc);
			pstmnt.setInt(2, id_tcv);

			rs = pstmnt.executeQuery();

			if (rs.next()) {
				rtcv = new TipoCasoVariable();

				rtcv.setIdTC(rs.getInt("id_tc"));
				rtcv.setIdTCV(rs.getInt("id_tcv"));
				rtcv.setNombre(rs.getString("tcv_nombre"));
				rtcv.setDescripcion(rs.getString("tcv_descripcion"));
				rtcv.setEtiqueta(rs.getString("tcv_etiqueta"));
				rtcv.setTipo(rs.getInt("tcv_tipo"));
				rtcv.setLongitud(rs.getInt("tcv_longitud"));
				rtcv.setIndice(rs.getInt("tcv_indice"));
				rtcv.setEnGaveta(rs.getString("tcv_en_gaveta"));
			}
		} finally {
			if (rs != null)
				rs.close();

			if (pstmnt != null)
				pstmnt.close();

			rs = null;
			pstmnt = null;
		}

		return rtcv;
	}

	public static Map<String, TipoCasoVariable> select(Connection conn, TipoCasoVariable tcv) throws SQLException {

		Map<String, TipoCasoVariable> m = new LinkedHashMap<>();
		PreparedStatement pstmnt = null;
		ResultSet rs = null;

		try {
			StringBuffer where = new StringBuffer();
			String token = " WHERE ";

			if (tcv.getIdTC() > 0) {
				where.append(token + "id_tc = ?");
				token = " AND ";
			}

			if (tcv.getIdTCV() > 0) {
				where.append(token + "id_tcv = ?");
				token = " AND ";
			}

			if (tcv.getNombre() != null) {
				where.append(token + "tcv_nombre = ?");
				token = " AND ";
			}

			if (tcv.getDescripcion() != null) {
				where.append(token + "tcv_descripcion = ?");
				token = " AND ";
			}

			if (tcv.getEtiqueta() != null) {
				where.append(token + "tcv_etiqueta = ?");
				token = " AND ";
			}

			if (tcv.getTipo() > 0) {
				where.append(token + "tcv_tipo = ?");
				token = " AND ";
			}

			if (tcv.getLongitud() > 0) {
				where.append(token + "tcv_longitud = ?");
				token = " AND ";
			}

			if (tcv.getIndice() > 0) {
				where.append(token + "tcv_indice = ?");
				token = " AND ";
			}

			if (tcv.getEnGaveta() != null) {
				where.append(token + "tcv_en_gaveta = ?");
				token = " AND ";
			}

			pstmnt = conn.prepareStatement("SELECT * FROM cg_tipo_caso_variable " + where.toString());

			int i = 1;
			if (tcv.getIdTC() > 0)
				pstmnt.setInt(i++, tcv.getIdTC());

			if (tcv.getIdTCV() > 0)
				pstmnt.setInt(i++, tcv.getIdTCV());

			if (tcv.getNombre() != null)
				pstmnt.setString(i++, tcv.getNombre());

			if (tcv.getDescripcion() != null)
				pstmnt.setString(i++, tcv.getDescripcion());

			if (tcv.getEtiqueta() != null)
				pstmnt.setString(i++, tcv.getEtiqueta());

			if (tcv.getTipo() > 0)
				pstmnt.setInt(i++, tcv.getTipo());

			if (tcv.getLongitud() > 0)
				pstmnt.setInt(i++, tcv.getLongitud());

			if (tcv.getIndice() > 0)
				pstmnt.setInt(i++, tcv.getIndice());

			if (tcv.getEnGaveta() != null)
				pstmnt.setString(i++, tcv.getEnGaveta());

			rs = pstmnt.executeQuery();

			while (rs.next()) {
				TipoCasoVariable ltcv = new TipoCasoVariable();

				ltcv.setIdTC(rs.getInt("id_tc"));
				ltcv.setIdTCV(rs.getInt("id_tcv"));
				ltcv.setNombre(rs.getString("tcv_nombre"));
				ltcv.setDescripcion(rs.getString("tcv_descripcion"));
				ltcv.setEtiqueta(rs.getString("tcv_etiqueta"));
				ltcv.setTipo(rs.getInt("tcv_tipo"));
				ltcv.setLongitud(rs.getInt("tcv_longitud"));
				ltcv.setIndice(rs.getInt("tcv_indice"));
				ltcv.setEnGaveta(rs.getString("tcv_en_gaveta"));

				m.put(rs.getString("tcv_nombre"), ltcv);
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

	public static int update(Connection conn, TipoCasoVariable tcv) throws SQLException {

		int retval = -1;
		PreparedStatement pstmnt = null;

		try {
			pstmnt = conn.prepareStatement("UPDATE cg_tipo_caso_variable SET tcv_nombre = ?"
					+ ", tcv_descripcion = ?, tcv_etiqueta = ?, tcv_tipo = ?, tcv_longitud = ?"
					+ ", tcv_indice = ?, tcv_en_gaveta = ? WHERE id_tc = ? AND id_tcv = ?");

			pstmnt.setString(1, tcv.getNombre());
			pstmnt.setString(2, tcv.getDescripcion());
			pstmnt.setString(3, tcv.getEtiqueta());
			pstmnt.setInt(4, tcv.getTipo());
			pstmnt.setInt(5, tcv.getLongitud());
			pstmnt.setInt(6, tcv.getIndice());
			pstmnt.setString(7, tcv.getEnGaveta());
			pstmnt.setInt(8, tcv.getIdTC());
			pstmnt.setInt(9, tcv.getIdTCV());

			retval = pstmnt.executeUpdate();
		} finally {
			if (pstmnt != null)
				pstmnt.close();

			pstmnt = null;
		}

		return retval;
	}

	public static int getNextId(Connection conn, int id_tc) throws SQLException {

		int id = 0;
		PreparedStatement pstmnt = null;
		ResultSet rs = null;

		try {
			pstmnt = conn.prepareStatement("SELECT MAX(id_tcv) FROM cg_tipo_caso_variable WHERE id_tc = ?");

			pstmnt.setInt(1, id_tc);

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

	public static boolean existenVariablesDeCaso(Connection conn, int id_tc, int id_tcv) throws SQLException {

		boolean retVal = false;
		PreparedStatement pstmnt = null;
		ResultSet rs = null;

		try {
			pstmnt = conn.prepareStatement("SELECT COUNT(id_tc) FROM cg_caso_dato WHERE id_tc = ? AND id_cd = ?");

			pstmnt.setInt(1, id_tc);
			pstmnt.setInt(2, id_tcv);

			rs = pstmnt.executeQuery();
			if (rs.next())
				retVal = (rs.getInt(1) >= 1);
		} finally {
			if (rs != null)
				rs.close();

			if (pstmnt != null)
				pstmnt.close();
		}

		return retVal;
	}
}
