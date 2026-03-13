package com.syc.egresos.firmante;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.egresos.firmante.core.FirmanteManager;
import com.syc.egresos.firmante.servlet.Firmante;
import com.syc.egresos.firmante.servlet.FirmanteSuplente;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;


public class FirmanteBussinessLogic extends DataSourceManager {

	private static final Logger log = Logger.getLogger( FirmanteBussinessLogic.class );

	public FirmanteBussinessLogic( String jniName ) {
		super.init( jniName );
		log.trace( " FirmanteBussinessLogic  iniciado" );
	}

	public List<Firmante> readFromRequest( HttpServletRequest req ) throws Exception {
		boolean haySuplenteVoBo = req.getParameter( "VoBoSuplencia" ) != null;
		boolean haySuplenteAut = req.getParameter( "AutSuplencia" ) != null;

		List<Firmante> firmantes = new ArrayList<Firmante>();
		Connection conn = null;

		try {
			conn = getConnection();

			firmantes.add( FirmanteManager.cargaFirmante( conn, Integer.parseInt( req.getParameter( "nombreVoBo" ) ), "VOBO" ) );
			firmantes.add( FirmanteManager.cargaFirmante( conn, Integer.parseInt( req.getParameter( "nombreAut" ) ), "AUT" ) );

			if ( haySuplenteVoBo ) {
				String noOficio = req.getParameter( "noOficioVoBo" );
				Date fechaOficio = Util.stringToDate( req.getParameter( "fechaOficioVobo" ), "dd/MM/yyyy" );
				String tipoSuplencia = req.getParameter( "VoBoSuplenciaMotivo" );

				FirmanteSuplente suplenteVobo = FirmanteManager.cargaFirmanteSuplente( conn, Integer.parseInt( req.getParameter( "nombreVOBOSuplente" ) ), "SUPVOBO" );
				suplenteVobo.setFechaOficio( fechaOficio );
				suplenteVobo.setFolioOficio( noOficio );
				suplenteVobo.setTipoSuplencia( tipoSuplencia );
				firmantes.add( suplenteVobo );

			}
			if ( haySuplenteAut ) {

				String noOficio = req.getParameter( "noOficioAut" );
				Date fechaOficio = Util.stringToDate( req.getParameter( "fechaOficioAut" ), "dd/MM/yyyy" );
				String tipoSuplencia = req.getParameter( "AutSuplenciaMotivo" );

				FirmanteSuplente suplenteAut = FirmanteManager.cargaFirmanteSuplente( conn, Integer.parseInt( req.getParameter( "nombreAUTSuplente" ) ), "SUPAUT" );
				suplenteAut.setFechaOficio( fechaOficio );
				suplenteAut.setFolioOficio( noOficio );
				suplenteAut.setTipoSuplencia( tipoSuplencia );

				firmantes.add( suplenteAut );
			}
			return firmantes;
		} finally {
			CloseObject.closeObject( conn );
		}
	}

	public int saveFirmantes( List<Firmante> firmantes, String tipoPago, int folioPago, boolean esFirmaElectronica ) throws Exception {
		Connection conn = null;
		int insertados = 0;
		try {
			conn = getConnection();
			insertados = saveFirmantes( conn, firmantes, tipoPago, folioPago, esFirmaElectronica );
			conn.commit();
			return insertados;
		} catch ( Exception e ) {
			if ( conn != null )
				try {
					conn.rollback();
				} catch ( Exception e2 ) {
					log.warn( "Problemas en rollback: " + e2 );
				}
			throw e;
		} finally {
			CloseObject.closeObject( conn );
		}

	}

	public int saveFirmantes( Connection conn, List<Firmante> firmantes, String tipoPago, int folioPago, boolean esFirmaElectronica ) throws Exception {

		int insertados = 0;
		for ( Firmante firmante : firmantes ) {
			if ( firmante instanceof FirmanteSuplente ) {
				insertados += FirmanteManager.deleteFirmanteSuplente( conn, ( FirmanteSuplente ) firmante, tipoPago, folioPago );
				insertados += FirmanteManager.saveFirmanteSuplente( conn, ( FirmanteSuplente ) firmante, tipoPago, folioPago );
			} else
				insertados += FirmanteManager.saveFirmante( conn, firmante, tipoPago, folioPago );
		}
		insertados += FirmanteManager.actualizaTipoFirma( conn, tipoPago, folioPago, esFirmaElectronica );
		return insertados;

	}

	public List<Firmante> obtenerFirmantes( String modulo, String tipoFirmante, String ur ) throws SQLException {
		Connection conn = null;
		try {
			log.trace( "Obteniendo firmantes para Modulo: [" + modulo + "], Tipo: [" + tipoFirmante + "], Login: [" + ur + "]" );

			conn = getConnection();
			List<Firmante> firmantes = FirmanteManager.selectByModule( conn, modulo, tipoFirmante, ur );

			log.info( "Se obtuvieron [" + ( firmantes != null ? firmantes.size() : 0 ) + "] firmantes para Modulo: [" + modulo + "]" );
			return firmantes;
		} catch ( SQLException e ) {
			log.error( "Error al obtener firmantes para Modulo: [" + modulo + "], Tipo: [" + tipoFirmante + "], Login: [" + ur + "]", e );
			throw e;
		} finally {
			CloseObject.closeObject( conn );
			log.debug( "Conexión cerrada correctamente." );
		}
	}
}
