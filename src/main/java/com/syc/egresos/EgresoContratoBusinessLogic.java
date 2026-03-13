package com.syc.egresos;


import java.sql.Connection;

import org.apache.log4j.Logger;

import com.syc.ejercido.pagado.EgresosBusinessLogic;
import com.syc.sai.contabilidad.utils.db.CloseObject;


public class EgresoContratoBusinessLogic extends EgresosBusinessLogic {

	private static final Logger log = Logger.getLogger( EgresoContratoBusinessLogic.class );

	public EgresoContratoBusinessLogic( ) {
	}

	public EgresoContratoBusinessLogic( String jniName ) {
		super( jniName );
	}

	public String getNumeroContratoEgreso( Connection conn, String tipoPago, int folioPago ) throws Exception {

		log.info( "Buscando numero de contrato para el folio : " + folioPago + " en el pago " + tipoPago );
		return EgresoContratoManager.getNumeroContratoEgreso( conn, tipoPago, folioPago );

	}

	public String getNumeroContratoEgreso( String tipoPago, int folioPago ) throws Exception {
		Connection conn = null;

		try {
			conn = getConnection();
			return getNumeroContratoEgreso( conn, tipoPago, folioPago );
		} finally {
			CloseObject.closeObject( conn );
		}

	}

}
