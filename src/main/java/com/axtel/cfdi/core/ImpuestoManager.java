package com.axtel.cfdi.core;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.axtel.cfdi.Impuesto;


public class ImpuestoManager {

	public Impuesto obtenerImpuesto( Connection conn, String impuesto ) throws SQLException {
		String querySelect = "SELECT * FROM c_Impuesto WHERE Impuesto = ?";

		try ( PreparedStatement ps = conn.prepareStatement( querySelect ) ) {
			ps.setString( 1, impuesto );
			try ( ResultSet rs = ps.executeQuery() ) {
				if ( rs.next() ) {
					Impuesto imp = new Impuesto();
					imp.setImpuesto( rs.getString( "Impuesto" ) );
					imp.setDescripcion( rs.getString( "Descripcion" ) );
					return imp;
				} else {
					throw new SQLException( "No Impuesto found with ID: " + impuesto );
				}
			}
		}
	}
}
