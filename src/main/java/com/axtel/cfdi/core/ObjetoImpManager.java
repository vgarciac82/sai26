package com.axtel.cfdi.core;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.axtel.cfdi.ObjetoImp;


public class ObjetoImpManager {

	public static ObjetoImp obtenerObjetoImp( Connection conn, String objetoImp ) throws SQLException {
		String querySelect = "SELECT * FROM c_ObjetoImp WHERE ObjetoImp = ?";

		try ( PreparedStatement ps = conn.prepareStatement( querySelect ) ) {
			ps.setString( 1, objetoImp );
			try ( ResultSet rs = ps.executeQuery() ) {
				if ( rs.next() ) {
					ObjetoImp oi = new ObjetoImp();
					oi.setObjetoImp( rs.getString( "ObjetoImp" ) );
					oi.setDescripcion( rs.getString( "Descripcion" ) );
					return oi;
				} else {
					throw new SQLException( "No ObjetoImp found with code: " + objetoImp );
				}
			}
		}
	}

	public static void guardarObjetoImp( Connection conn, ObjetoImp objetoImp ) throws SQLException {
		String queryInsert = "INSERT INTO c_ObjetoImp (ObjetoImp, Descripcion) VALUES (?, ?)";

		try ( PreparedStatement ps = conn.prepareStatement( queryInsert ) ) {
			ps.setString( 1, objetoImp.getObjetoImp() );
			ps.setString( 2, objetoImp.getDescripcion() );

			int affectedRows = ps.executeUpdate();

			if ( affectedRows == 0 ) {
				throw new SQLException( "Creating ObjetoImp failed, no rows affected." );
			}
		}
	}

	public static void actualizarObjetoImp( Connection conn, ObjetoImp objetoImp ) throws SQLException {
		String queryUpdate = "UPDATE c_ObjetoImp SET Descripcion = ? WHERE ObjetoImp = ?";

		try ( PreparedStatement ps = conn.prepareStatement( queryUpdate ) ) {
			ps.setString( 1, objetoImp.getDescripcion() );
			ps.setString( 2, objetoImp.getObjetoImp() );

			ps.executeUpdate();
		}
	}

	public static void eliminarObjetoImp( Connection conn, String objetoImp ) throws SQLException {
		String queryDelete = "DELETE FROM c_ObjetoImp WHERE ObjetoImp = ?";

		try ( PreparedStatement ps = conn.prepareStatement( queryDelete ) ) {
			ps.setString( 1, objetoImp );
			ps.executeUpdate();
		}
	}
}
