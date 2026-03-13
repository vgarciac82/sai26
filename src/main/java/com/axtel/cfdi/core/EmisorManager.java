package com.axtel.cfdi.core;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.axtel.cfdi.Emisor;


public class EmisorManager {

	public Emisor obtenerEmisor( Connection conn, String rfcEmisor ) throws SQLException {
		String querySelect = "SELECT * FROM Emisor WHERE RFC = ?";

		try ( PreparedStatement ps = conn.prepareStatement( querySelect ) ) {
			ps.setString( 1, rfcEmisor );
			try ( ResultSet rs = ps.executeQuery() ) {
				if ( rs.next() ) {
					Emisor emisor = new Emisor();
					emisor.setRfc( rs.getString( "RFC" ) );
					emisor.setNombre( rs.getString( "Nombre" ) );
					emisor.setRegimenFiscal( rs.getString( "RegimenFiscal" ) );
					return emisor;
				} else {
					throw new SQLException( "No Emisor found with RFC: " + rfcEmisor );
				}
			}
		}
	}

}
