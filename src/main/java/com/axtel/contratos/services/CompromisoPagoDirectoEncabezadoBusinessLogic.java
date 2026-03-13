package com.axtel.contratos.services;


import java.sql.Connection;

import org.apache.log4j.Logger;

import com.axtel.contratos.repositories.CompromisoPagoDirectoEncabezadoManager;
import com.syc.cfdi.db.CloseObject;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.util.Util;


public class CompromisoPagoDirectoEncabezadoBusinessLogic extends DataSourceManager {

	private static final Logger log = Logger.getLogger( CompromisoPagoDirectoEncabezadoBusinessLogic.class );

	public CompromisoPagoDirectoEncabezadoBusinessLogic( String jndiName ) {
		super.init( jndiName );
	}

	public void insertaPagoDesdeSuficiencia( Caso c, Usuario user, String suficiencia ) throws Exception {
		log.info( "Insertando pago directo desde suficiencia " + suficiencia );
		Connection conn = null;
		try {
			conn = getConnection();
			CompromisoPagoDirectoEncabezadoManager.insertaPagoDesdeSuficiencia( conn, c, user, suficiencia );
			conn.commit();
		} catch ( Exception e ) {
			Util.rollback( conn );
			throw e;
		} finally {
			CloseObject.closeObject( conn );

		}
	}
}
