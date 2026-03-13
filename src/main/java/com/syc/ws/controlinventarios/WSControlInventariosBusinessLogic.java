package com.syc.ws.controlinventarios;


import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.syc.contable.AdecuacionBusinessLogic;
import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.servlet.GestionInterface;


public class WSControlInventariosBusinessLogic extends DataSourceManager {

	private static Logger log = Logger.getLogger( WSControlInventariosBusinessLogic.class );

	public WSControlInventariosBusinessLogic( String jniName ) {

		super.init( jniName );
	}

	public WSControlInventariosBusinessLogic( String jniName, String folioGenerator ) {
		super.init( jniName );
	}

	// para obtener el ejercicio fiscal en diferentes funciones
	AdecuacionBusinessLogic adecProy = new AdecuacionBusinessLogic( GestionInterface.ATT_CONEXION );

	public int sendCtrlInventory( int iMonth, int iYear, int iType ) throws Exception, SQLException {
		Connection conn = null;
		int iResultado = 0;
		try {
			conn = getConnection();
			iResultado = WSControlInventariosManager.sendCtrlInventory( conn, iMonth, iYear, iType );
			conn.commit();
		} catch ( Exception exc ) {
			log.error( exc );
			conn.rollback();
			throw new Exception( exc );
		} finally {
			if ( conn != null )
				conn.close();
			conn = null;
		}

		return iResultado;
	}

}
