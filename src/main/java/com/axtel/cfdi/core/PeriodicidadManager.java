package com.axtel.cfdi.core;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.axtel.cfdi.Periodicidad;


public class PeriodicidadManager {

	public static Periodicidad obtenerPeriodicidad( Connection conn, String periodicidad ) throws SQLException {
		String querySelect = "SELECT * FROM c_Periodicidad WHERE Periodicidad = ?";

		try ( PreparedStatement ps = conn.prepareStatement( querySelect ) ) {
			ps.setString( 1, periodicidad );
			try ( ResultSet rs = ps.executeQuery() ) {
				if ( rs.next() ) {
					Periodicidad per = new Periodicidad();
					per.setPeriodicidad( rs.getString( "Periodicidad" ) );
					per.setDescripcion( rs.getString( "Descripcion" ) );
					return per;
				} else {
					throw new SQLException( "No Periodicidad found with code: " + periodicidad );
				}
			}
		}
	}
}
