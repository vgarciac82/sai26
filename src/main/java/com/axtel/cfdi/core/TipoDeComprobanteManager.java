package com.axtel.cfdi.core;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.axtel.cfdi.TipoDeComprobante;


public class TipoDeComprobanteManager {

	public static TipoDeComprobante obtenerTipoComprobante( Connection conn, String tipoDeComprobante ) throws SQLException {
		String querySelect = "SELECT * FROM c_TipoDeComprobante WHERE TipoDeComprobante = ?";

		try ( PreparedStatement ps = conn.prepareStatement( querySelect ) ) {
			ps.setString( 1, tipoDeComprobante );
			try ( ResultSet rs = ps.executeQuery() ) {
				if ( rs.next() ) {
					TipoDeComprobante tipoComprobante = new TipoDeComprobante();
					tipoComprobante.setTipoDeComprobante( rs.getString( "TipoDeComprobante" ) );
					tipoComprobante.setDescripcion( rs.getString( "Descripcion" ) );
					return tipoComprobante;
				} else {
					throw new SQLException( "No TipoDeComprobante found with code: " + tipoDeComprobante );
				}
			}
		}
	}
}
