package com.syc.contable.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.syc.cfdi.db.CloseObject;

public class CatalogoURFIELManager {

	public static boolean permitePagoSinFiel(Connection conn, String ur) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String query = "SELECT cPagoSinFiel FROM tCatalogoURFIEL WITH(NOLOCK) WHERE cUnidadResponsable = ?";
		try {
			ps = conn.prepareStatement(query);
			ps.setString(1, ur);

			rs = ps.executeQuery();

			if (rs.next()) {
				return "S".equalsIgnoreCase(rs.getString(1));
			} else
				throw new Exception("No se tiene registrada informacion para la unidad " + ur + " respecto a la firma electronica.");
		} finally {
			CloseObject.closeObject(rs);
			CloseObject.closeObject(ps);
		}
	}
}
