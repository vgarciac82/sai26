package com.axtel.cfdi.service;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.axtel.cfdi.Receptor;
import com.axtel.cfdi.core.ReceptorManager;
import com.syc.cfdi.db.CloseObject;
import com.syc.crud.dsmngr.DataSourceManager;


public class ReceptorService extends DataSourceManager {

	public ReceptorService( String jniName ) {
		super.init( jniName );
	}

	public List<Receptor> buscarReceptores( String nombre, String rfc ) {
		Connection conn = null;
		try {
			conn = getConnection();

			return ReceptorManager.buscarReceptores( conn, nombre, rfc );

		} catch ( SQLException e ) {
			throw new RuntimeException( "Error al buscar receptores", e );
		} finally {
			CloseObject.closeObject( conn );
		}
	}

	public Receptor obtenerReceptorPorRfc( String rfc ) {
		Connection conn = null;
		try {
			conn = getConnection();

			return ReceptorManager.obtenerCliente( conn, rfc );

		} catch ( SQLException e ) {
			throw new RuntimeException( "Error al buscar receptores", e );
		} finally {
			CloseObject.closeObject( conn );
		}
	}

}
