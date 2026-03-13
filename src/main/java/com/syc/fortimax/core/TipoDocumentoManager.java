package com.syc.fortimax.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TipoDocumentoManager {

	public static TipoDocumento buscaTipoDocumento(Connection conn, String titulo_aplicacion, int id_tipo_docto)
			throws SQLException {

		TipoDocumento td = null;
		PreparedStatement pstmnt = null;
		ResultSet rs = null;

		try {
			pstmnt = conn.prepareStatement("SELECT * FROM imx_tipo_documento WHERE titulo_aplicacion = ? "
					+ "AND id_tipo_docto = ?");

			pstmnt.setString(1, titulo_aplicacion);
			pstmnt.setInt(2, id_tipo_docto);

			rs = pstmnt.executeQuery();

			while (rs.next()) {
				td = new TipoDocumento();

				td.setTituloAplicacion(rs.getString("titulo_aplicacion"));
				td.setIdTipoDocto(rs.getInt("id_tipo_docto"));
				td.setPrioridad(rs.getInt("prioridad"));
				td.setNombreTipoDocto(rs.getString("nombre_tipo_docto"));
				td.setDescripcion(rs.getString("descripcion"));
			}
		} finally {
			if (rs != null)
				rs.close();

			if (pstmnt != null)
				pstmnt.close();

			rs = null;
			pstmnt = null;
		}

		return td;
	}

	public static List getDocumentos(Connection conn, String titulo_aplicacion) throws SQLException {

		List doctos = new ArrayList();
		PreparedStatement pstmnt = null;
		ResultSet rs = null;

		try {
			pstmnt = conn.prepareStatement("SELECT * FROM imx_tipo_documento WHERE titulo_aplicacion = ? "
					+ "AND id_tipo_docto > 2 ORDER BY nombre_tipo_docto");

			pstmnt.setString(1, titulo_aplicacion);

			rs = pstmnt.executeQuery();

			while (rs.next()) {
				TipoDocumento td = new TipoDocumento();

				td.setTituloAplicacion(rs.getString("titulo_aplicacion"));
				td.setIdTipoDocto(rs.getInt("id_tipo_docto"));
				td.setPrioridad(rs.getInt("prioridad"));
				td.setNombreTipoDocto(rs.getString("nombre_tipo_docto"));
				td.setDescripcion(rs.getString("descripcion"));

				doctos.add(td);
			}
		} finally {
			if (rs != null)
				rs.close();

			if (pstmnt != null)
				pstmnt.close();

			rs = null;
			pstmnt = null;
		}

		return doctos;
	}

	public static synchronized int getIdTipoDocumento(Connection conn, String titulo_aplicacion,
			String nombre_tipo_docto) throws SQLException {

		int retval = -1;
		PreparedStatement pstmnt = null;
		ResultSet rs = null;

		try {
			pstmnt = conn.prepareStatement("SELECT * FROM imx_tipo_documento "
					+ "WHERE titulo_aplicacion = ? AND nombre_tipo_docto = ?");

			pstmnt.setString(1, titulo_aplicacion);
			pstmnt.setString(2, nombre_tipo_docto);

			rs = pstmnt.executeQuery();

			if (rs.next())
				retval = rs.getInt(1);
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
}
