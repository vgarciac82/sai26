package com.axtel.cfdi.core;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.axtel.cfdi.MetodoPago;


public class MetodoPagoManager {

	public static MetodoPago obtener( Connection conn, String metodoPago ) throws SQLException {
		String querySelect = "SELECT * FROM c_MetodoPago WHERE MetodoPago = ?";

		try ( PreparedStatement ps = conn.prepareStatement( querySelect ) ) {
			ps.setString( 1, metodoPago );
			try ( ResultSet rs = ps.executeQuery() ) {
				if ( rs.next() ) {
					MetodoPago metodo = new MetodoPago();
					metodo.setMetodoPago( rs.getString( "MetodoPago" ) );
					metodo.setDescripcion( rs.getString( "Descripcion" ) );
					return metodo;
				} else {
					throw new SQLException( "No MetodoPago found with code: " + metodoPago );
				}
			}
		}
	}
}
