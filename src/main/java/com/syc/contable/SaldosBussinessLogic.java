package com.syc.contable;


import java.io.InputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.syc.contable.core.CompromisoManager;
import com.syc.contable.core.SaldoManager;
import com.syc.contable.core.SaldoMensual;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;


public class SaldosBussinessLogic extends DataSourceManager {

	private static final Logger log = Logger.getLogger( SaldosBussinessLogic.class );

	public SaldosBussinessLogic( ) {
	}

	public SaldosBussinessLogic( String jniName ) {

		super.init( jniName );
	}

	public List<String> archivoLeido( InputStream in ) throws Exception {
		List<String> archivoLe = new ArrayList<String>();
		Connection conn = null;
		try {
			conn = getConnection();
			// archivoLe = SaldosInicialesManager.leerGenerales(conn,in);
		} catch ( Exception e ) {
			if ( conn != null ) {
				try {
					conn.rollback();
				} catch ( Exception e2 ) {
					log.warn( "Problemas con rollback: " + e2 );
				}
			}
		} finally {
			CloseObject.closeObject( conn );
		}
		return archivoLe;
	}

	public Map<Integer, SaldoMensual> getSaldoCompromiso( Connection conn, Map<Integer, SaldoMensual> saldos, String numeroContrato, String tipoPago, int folioPago, String ep ) throws Exception {
			return SaldoManager.getSaldoCompromiso( conn, saldos, numeroContrato, tipoPago, folioPago, ep );
	}
	
	public Map<Integer, SaldoMensual> getSaldoCompromiso( Map<Integer, SaldoMensual> saldos, String numeroContrato, String tipoPago, int folioPago, String ep ) throws Exception {
		Connection conn = null;
		try {
			conn = getConnection();
			return SaldoManager.getSaldoCompromiso( conn, saldos, numeroContrato, tipoPago, folioPago, ep );
		} finally {
			CloseObject.closeObject( conn );
		}

	}

	public Map<Integer, SaldoMensual> getSaldoDisponible( Map<Integer, SaldoMensual> saldos, String tipoPago, int folioPago, String ep ) throws Exception {
		Connection conn = null;
		try {
			conn = getConnection();
			return SaldoManager.getSaldoDisponible( conn, saldos, tipoPago, folioPago, ep );
		} finally {
			CloseObject.closeObject( conn );
		}
	}

	public boolean insertaSaldos( StringBuffer archivoFiltrado ) throws Exception {
		boolean cargandoArchivo = false;
		Connection conn = null;
		try {
			conn = getConnection();
			cargandoArchivo = CompromisoManager.insertaFiltrados( conn, archivoFiltrado );
			conn.commit();

		} catch ( Exception e ) {
			if ( conn != null ) {
				try {
					conn.rollback();
				} catch ( Exception e2 ) {
					throw e2;
				}
			}
			throw e;
		} finally {
			CloseObject.closeObject( conn );
		}
		return cargandoArchivo;
	}

}
