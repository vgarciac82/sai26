package com.axtel.cfdi.core;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.axtel.cfdi.UsoCFDI;


public class UsoCFDIManager {

	public static UsoCFDI obtenerUsoCfdi( Connection conn, String usoCfdi ) throws SQLException {
		String querySelect = "SELECT * FROM c_UsoCFDI WHERE UsoCFDI = ?";

		try ( PreparedStatement ps = conn.prepareStatement( querySelect ) ) {
			ps.setString( 1, usoCfdi );
			try ( ResultSet rs = ps.executeQuery() ) {
				if ( rs.next() ) {
					UsoCFDI uso = new UsoCFDI();
					uso.setUsoCFDI( rs.getString( "UsoCFDI" ) );
					uso.setDescripcion( rs.getString( "Descripcion" ) );
					return uso;
				} else {
					throw new SQLException( "No UsoCFDI found with code: " + usoCfdi );
				}
			}
		}
	}
}
