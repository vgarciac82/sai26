package com.syc.gestion.reportes.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReporteConfManager {

	public static int delete(Connection conn, int id_reporte) throws SQLException {

		int retval = -1;
		PreparedStatement pstmnt = null;

		try {
			pstmnt = conn.prepareStatement("DELETE FROM cg_reporte_conf WHERE id_reporte = ?");

			pstmnt.setInt(1, id_reporte);

			retval = pstmnt.executeUpdate();
		} finally {
			if (pstmnt != null)
				pstmnt.close();

			pstmnt = null;
		}

		return retval;
	}

	public static int insert(Connection conn, ReporteConf r) throws SQLException {

		int retval = -1;
		PreparedStatement pstmnt = null;

		try {
			pstmnt = conn.prepareStatement("INSERT INTO cg_reporte_conf (r_nombre, r_plantilla_formulario, r_plantilla_resultado, r_descripcion) VALUES (?, ?, ?, ?)");

			pstmnt.setString(1, r.getNombre());
			pstmnt.setString(2, r.getPlantillaFormulario());
			pstmnt.setString(3, r.getPlantillaResultado());
			pstmnt.setString(4, r.getDescripcion());

			retval = pstmnt.executeUpdate();
			
		} finally {
			if (pstmnt != null)
				pstmnt.close();

			pstmnt = null;
		}

		return retval;
	}

	public static ReporteConf select(Connection conn, ReporteConf r) throws SQLException {

		ReporteConf nr = null;
		PreparedStatement pstmnt = null;
		ResultSet rs = null;
		
		int a = -1;

		try {
			StringBuffer where = new StringBuffer();
			String token = " WHERE ";

			if (r != null &&
				r.getId() > -1) {
				where.append(token + "id_reporte = ?");
				token = " AND ";
				a = r.getId();
			}

			pstmnt = conn.prepareStatement("SELECT * FROM cg_reporte_conf " + where.toString());

			pstmnt.setInt(1, a);

			rs = pstmnt.executeQuery();

			if (rs.next()) {
				nr = new ReporteConf();

				nr.setId(rs.getInt("id_reporte"));
				nr.setNombre(rs.getString("r_nombre"));
				nr.setPlantillaFormulario(rs.getString("r_plantilla_formulario"));
				nr.setPlantillaResultado(rs.getString("r_plantilla_resultado"));
				nr.setDescripcion(rs.getString("r_descripcion"));
				
			}
		} finally {
			if (rs != null)
				rs.close();

			if (pstmnt != null)
				pstmnt.close();

			rs = null;
			pstmnt = null;
		}

		return nr;
	}

	public static List selectAll(Connection conn) throws SQLException {

		List grpList = new ArrayList();
		PreparedStatement pstmnt = null;
		ResultSet rs = null;

		try {
			pstmnt = conn.prepareStatement("SELECT * FROM cg_reporte_conf ORDER BY r_nombre");

			rs = pstmnt.executeQuery();

			while (rs.next()) {
				ReporteConf r = new ReporteConf();

				r.setId(rs.getInt("id_reporte"));
				r.setNombre(rs.getString("r_nombre"));
				r.setPlantillaFormulario(rs.getString("r_plantilla_formulario"));
				r.setPlantillaResultado(rs.getString("r_plantilla_resultado"));
				r.setDescripcion(rs.getString("r_descripcion"));

				grpList.add(r);
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

	public static int update(Connection conn, ReporteConf r) throws SQLException {

		int retval = -1;
		PreparedStatement pstmnt = null;
		ResultSet rs = null;

		try {
			pstmnt = conn.prepareStatement("UPDATE cg_reporte_conf SET r_nombre = ?, r_plantilla_formulario = ?, r_plantilla_resultado = ?, r_descripcion = ? WHERE id_reporte = ?");

			pstmnt.setString(1, r.getNombre());
			pstmnt.setString(2, r.getPlantillaFormulario());
			pstmnt.setString(3, r.getPlantillaResultado());
			pstmnt.setString(4, r.getDescripcion());
			pstmnt.setInt	(5, r.getId());

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
}

