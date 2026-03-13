package com.axtel.sisecop.repostories;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.syc.cfdi.db.CloseObject;


public class ProjectObservationsRepository {

	public int insertObservations( Connection conn, int projectId ) throws SQLException {

		StringBuilder query = new StringBuilder();
		query.append( "IF ( SELECT observaciones FROM sisecop_servicios WITH(NOLOCK) WHERE servicioId = ? ) <> NULL  " );
		query.append( "INSERT INTO sisecop_log_observaciones (id_servicio, observaciones)  " );
		query.append( "VALUES ( ?,  " );
		query.append( "              (SELECT observaciones FROM sisecop_servicios WITH(NOLOCK) WHERE servicioId = ? ) " );
		query.append( ")" );

		PreparedStatement ps = null;

		try {

			ps = conn.prepareStatement( query.toString() );
			ps.setInt( 1, projectId );
			ps.setInt( 2, projectId );
			ps.setInt( 3, projectId );

			return ps.executeUpdate();
		} finally {
			CloseObject.closeObject( ps );
		}

	}
}
