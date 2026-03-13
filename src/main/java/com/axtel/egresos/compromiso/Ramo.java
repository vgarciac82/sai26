package com.axtel.egresos.compromiso;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import com.syc.cfdi.db.CloseObject;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.servlet.GestionInterface;


public class Ramo extends DataSourceManager {

	public static final Logger	log	= Logger.getLogger( Ramo.class );
	private String				idRamo;
	private String				dRamo;

	public Ramo( ) {
		try {
			super.init( GestionInterface.ATT_CONEXION );
			instanciaRamo();
		} catch ( Exception e ) {
			throw new RuntimeException( e.toString(), e.getCause() );
		}
	}

	public Ramo( Connection conn ) throws SQLException {
		try {
			instanciaRamo( conn );
		} catch ( Exception e ) {
			throw new RuntimeException( e.toString(), e.getCause() );
		}

	}

	public String getIdRamo() {
		return idRamo;
	}

	public String getDRamo() {
		return dRamo;
	}

	private void instanciaRamo( Connection conn ) throws SQLException {

		PreparedStatement pst = null;
		ResultSet rs = null;

		try {

			pst = conn.prepareStatement( "select * from tRamo" );
			rs = pst.executeQuery();

			if ( rs.next() ) {
				this.idRamo = rs.getString( 1 );
				this.dRamo = rs.getString( 2 );
			}

		} finally {
			CloseObject.closeObject( rs );
			CloseObject.closeObject( pst );
		}

	}

	private void instanciaRamo() throws SQLException {

		Connection conn = getConnection( GestionInterface.ATT_CONEXION );

		try {

			instanciaRamo( conn );

		} finally {
			CloseObject.closeObject( conn );
		}

	}

}
