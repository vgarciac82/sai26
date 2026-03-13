package com.syc.gestion.core;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.syc.cfdi.utils.CloseObject;
import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;


/*
CREATE TABLE CF_SEQUENCE (
  seq_name  VARCHAR(80),
  seq_value INTEGER,
  CONSTRAINT PK_CF_SEQUENCE PRIMARY KEY (seq_name)
)
 */
public class CFSequenceManager extends DataSourceManager {

	public static String				RUN_INTERVAL_PROCESS	= "RUN_INTERVAL_PROCESS";
	public static String				SMTP_SERVER_TO_CONNECT	= "SMTP_SERVER_TO_CONNECT";
	public static String				SLEEP_INTERVAL_PROCESS	= "SLEEP_INTERVAL_PROCESS";

	private static Logger				log						= Logger.getLogger( CFSequenceManager.class );

	private static boolean				standAlone				= false;

	private static CFSequenceManager	seqMangr				= null;

	private CFSequenceManager( ) {
		super.init();
	}

	private CFSequenceManager( String jndiName ) {
		if ( jndiName != null ) {
			CFSequenceManager.standAlone = false;
			super.init( jndiName );
		} else {
			CFSequenceManager.standAlone = true;
		}
	}

	public static CFSequenceManager getInstance() {
		if ( seqMangr == null ) {
			seqMangr = new CFSequenceManager();
		}

		return seqMangr;
	}

	public static CFSequenceManager getInstance( String jndiName ) {
		if ( seqMangr == null ) {
			seqMangr = new CFSequenceManager( jndiName );
		}

		return seqMangr;
	}

	public static int delete( Connection conn, String name ) throws SQLException {

		int retVal = 0;
		PreparedStatement psDelete = null;

		try {
			psDelete = conn.prepareStatement( "DELETE FROM cf_sequence WHERE seq_name = ?" );

			psDelete.setString( 1, name );

			retVal = psDelete.executeUpdate();
		} finally {
			if ( psDelete != null )
				psDelete.close();

			psDelete = null;
		}

		return retVal;
	}

	public static CFSequence insert( Connection conn, String name, int value ) throws SQLException {

		CFSequence retVal = null;
		PreparedStatement psInsert = null;

		try {
			psInsert = conn.prepareStatement( "INSERT INTO cf_sequence (seq_name, seq_value) VALUES (?, ?)" );

			psInsert.setString( 1, name );
			psInsert.setInt( 2, value );

			if ( psInsert.executeUpdate() == 1 )
				retVal = new CFSequence( name, value );
		} finally {
			if ( psInsert != null )
				psInsert.close();

			psInsert = null;
		}

		return retVal;
	}

	public static CFSequence update( Connection conn, String name, int value ) throws SQLException {

		CFSequence retVal = null;
		PreparedStatement psUpdate = null;

		try {
			psUpdate = conn.prepareStatement( "UPDATE cf_sequence WITH (ROWLOCK) SET seq_value = ? WHERE seq_name = ?" );

			psUpdate.setInt( 1, value );
			psUpdate.setString( 2, name );

			if ( psUpdate.executeUpdate() == 1 )
				retVal = new CFSequence( name, value );
		} finally {
			if ( psUpdate != null )
				psUpdate.close();

			psUpdate = null;
		}

		return retVal;
	}

	public static CFSequence select( Connection conn, String name ) throws SQLException {

		CFSequence retVal = null;
		PreparedStatement psSelect = null;
		ResultSet rs = null;

		try {
			psSelect = conn.prepareStatement( "SELECT * FROM cf_sequence WITH (NOLOCK) WHERE seq_name = ?" );

			psSelect.setString( 1, name );

			rs = psSelect.executeQuery();
			if ( rs.next() )
				retVal = new CFSequence( rs.getString( "seq_name" ), rs.getInt( "seq_value" ) );
		} finally {
			if ( rs != null )
				rs.close();

			if ( psSelect != null )
				psSelect.close();

			rs = null;
			psSelect = null;
		}

		return retVal;
	}

	public int currVal( String name ) throws SQLException {

		int retVal = 1;
		Connection conn = null;
		PreparedStatement psSelect = null;
		ResultSet rs = null;

		try {
			if ( CFSequenceManager.standAlone )
				try {
					conn = Util.getStandAloneConnection();
				} catch ( Exception e ) {
					throw new SQLException();
				}
			else
				conn = getConnection(GestionInterface.ATT_CONEXION);

			psSelect = conn.prepareStatement( "SELECT seq_value FROM cf_sequence WITH (NOLOCK) WHERE seq_name = ?" );

			psSelect.setString( 1, name );

			rs = psSelect.executeQuery();
			if ( rs.next() )
				retVal = rs.getInt( 1 );
		} finally {
			if ( psSelect != null )
				psSelect.close();

			if ( conn != null )
				conn.close();

			psSelect = null;
			conn = null;
		}

		return retVal;
	}

	public int nextVal( Connection conn, String name ) throws SQLException {

		int retVal = 1;
		PreparedStatement psUpdate = null, psSelect = null;
		ResultSet rs = null;

		try {

			// Bloqueamos el registro incrementando al nuevo valor
			psUpdate = conn.prepareStatement( "UPDATE cf_sequence  WITH (ROWLOCK) SET seq_value = seq_value + 1 WHERE seq_name = ?" );
			psUpdate.setString( 1, name );
			psUpdate.executeUpdate();
			// Recuperamos el nuevo valor
			psSelect = conn.prepareStatement( "SELECT seq_value FROM cf_sequence WITH (NOLOCK) WHERE seq_name = ?" );
			psSelect.setString( 1, name );

			rs = psSelect.executeQuery();
			if ( rs.next() ) {
				retVal = rs.getInt( "seq_value" );
			} else {
				insert( conn, name, retVal );
			}

		} catch ( Exception exc ) {
			log.error( exc );
			throw new SQLException( exc );

		} finally {
			if ( psSelect != null )
				psSelect.close();

			if ( psUpdate != null )
				psUpdate.close();

			psSelect = null;
			psUpdate = null;
		}

		return retVal;
	}

	public int nextVal( String name ) throws SQLException {

		int retVal = 1;
		Connection conn = null;

		try {
			if ( CFSequenceManager.standAlone )
				try {
					conn = Util.getStandAloneConnection();
				} catch ( Exception e ) {
					throw new SQLException( e );
				}
			else
				conn = getConnection(GestionInterface.ATT_CONEXION);

			retVal = nextVal( conn, name );
			conn.commit();
		} catch ( Exception exc ) {
			log.error( exc );
			conn.rollback();
			throw new SQLException( exc );
		} finally {

			if ( conn != null )
				conn.close();

			conn = null;
		}

		return retVal;
	}

}
