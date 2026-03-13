package com.syc.sai.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.syc.gestion.core.UnidadEjecutora;
import com.syc.sai.contabilidad.utils.db.CloseObject;

public class UnidadEjecutoraManager {

	public static UnidadEjecutora selectUnidadEjecutora(Connection conn, String ue) throws Exception {
		PreparedStatement psSearch = null;
		ResultSet rs = null;
		String query = "select	cUnidadResponsable, D_DESCRIPCION, nAlcance " + " from	tCatUnidadResponsable WITH(NOLOCK) " + " WHERE	cUnidadResponsable = ?";
		try {

			psSearch = conn.prepareStatement(query);
			psSearch.setString(1, ue);

			rs = psSearch.executeQuery();

			if (rs.next()) {
				UnidadEjecutora ueRet = new UnidadEjecutora();
				ueRet.setUe(rs.getString("cUnidadResponsable"));
				ueRet.setDescripcion(rs.getString("D_DESCRIPCION"));
				return ueRet;
			} else {
				throw new Exception("No existe la unidad ejecutora [" + ue + "]");
			}

		} finally {
			CloseObject.closeObject(rs);
			CloseObject.closeObject(psSearch);
		}
	}

	public static String selectCentroContableUE(Connection conn, String unidadEjecutora) throws Exception {
		String query = "SELECT TOP 1 ccentrocontable FROM tCatalogoURCC WITH(NOLOCK) WHERE cUnidadResponsable = ?";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(query);
			ps.setString(1, unidadEjecutora);
			rs = ps.executeQuery();

			if (rs.next())
				return rs.getString(1);
			else
				throw new Exception("No se han definido centros contables para la unidad:  " + unidadEjecutora);
		} finally {
			CloseObject.closeObject(rs);
			CloseObject.closeObject(ps);
		}

	}

}
