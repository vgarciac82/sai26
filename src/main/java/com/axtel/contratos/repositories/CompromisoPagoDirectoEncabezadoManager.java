package com.axtel.contratos.repositories;


import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.syc.gestion.core.Caso;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.util.Util;


public class CompromisoPagoDirectoEncabezadoManager {

	private static final Logger log = Logger.getLogger( CompromisoPagoDirectoEncabezadoManager.class );

	public static void insertaPagoDesdeSuficiencia( Connection conn, Caso c, Usuario user, String suficiencia ) throws SQLException {
		try ( CallableStatement callableStatement = conn.prepareCall( "{call dbo.sp_InsertarPagoDirecto(?, ?, ?)}" ) ) {

			int folio = Util.folio( c );
			log.trace( " Ejecutando  {call dbo.sp_InsertarPagoDirecto(?, ?, ?)} con parametros: \n" + folio + "\n" + user.getLogin() + "\n" + suficiencia );
			callableStatement.setInt( 1, folio );
			callableStatement.setString( 2, user.getLogin() );
			callableStatement.setString( 3, suficiencia );

			callableStatement.execute();

			log.trace( "Registro insertado con éxito." );

		} catch ( SQLException e ) {
			throw e;
		}
	}

}
