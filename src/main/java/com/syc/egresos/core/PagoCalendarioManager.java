package com.syc.egresos.core;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Types;

import org.apache.commons.lang.StringUtils;

import com.syc.cfdi.db.CloseObject;
import org.apache.log4j.Logger;


public class PagoCalendarioManager {

	private static final Logger log = Logger.getLogger( "InsertaCalendario" );

	public static int insertaCalendario( Connection conn, CalendarioPago calendarioPago ) throws Exception {

		int insertados = 0;
		StringBuilder queryInsert = new StringBuilder();

		queryInsert.append( "INSERT INTO tPagoCalendario( " );
		queryInsert.append( "cTipoPago, nFolioPago, EP, nMes, mImporteBruto, " );
		queryInsert.append( "idTipoConcepto, idTipoMovimiento, mImporteRetencion ) " );
		queryInsert.append( "VALUES(?,?,?,?,?,?,?,?)" );

		PreparedStatement psInsert = null;

		try {
			log.debug( "Preparando inserción en tPagoCalendario..." + calendarioPago );
			log.debug( "Query generada: " + queryInsert.toString() );

			psInsert = conn.prepareStatement( queryInsert.toString() );

			log.debug( "Asignando parámetros para CalendarioPago:" );
			log.debug( "  -> TipoPago: " + calendarioPago.getTipoPago() );
			log.debug( "  -> FolioPago: " + calendarioPago.getFolioPago() );
			log.debug( "  -> EP: " + calendarioPago.getEp() );
			log.debug( "  -> Mes: " + calendarioPago.getMes() );
			log.debug( "  -> ImporteBrutoMes: " + calendarioPago.getImporteBrutoMes() );
			log.debug( "  -> IdTipoConcepto: " + calendarioPago.getIdTipoConcepto() );
			log.debug( "  -> IdTipoMovimiento: " + calendarioPago.getIdTipoMovimiento() );
			log.debug( "  -> ImporteRetencion: " + calendarioPago.getImporteRetencion() );

			psInsert.setString( 1, calendarioPago.getTipoPago() );
			psInsert.setInt( 2, calendarioPago.getFolioPago() );
			psInsert.setString( 3, calendarioPago.getEp() );
			psInsert.setInt( 4, calendarioPago.getMes() );
			psInsert.setBigDecimal( 5, calendarioPago.getImporteBrutoMes() );

			if ( StringUtils.isBlank( calendarioPago.getIdTipoConcepto() ) ) {
				log.debug( "IdTipoConcepto vacío -> seteando NULL" );
				psInsert.setNull( 6, Types.VARCHAR );
			} else {
				psInsert.setString( 6, calendarioPago.getIdTipoConcepto() );
			}

			if ( StringUtils.isBlank( calendarioPago.getIdTipoMovimiento() ) ) {
				log.debug( "IdTipoMovimiento vacío -> seteando NULL" );
				psInsert.setNull( 7, Types.VARCHAR );
			} else {
				psInsert.setString( 7, calendarioPago.getIdTipoMovimiento() );
			}

			psInsert.setBigDecimal( 8, calendarioPago.getImporteRetencion() );

			log.info( "Ejecutando INSERT de calendario de pago..." );
			insertados = psInsert.executeUpdate();

			log.info( "Filas insertadas correctamente: " + insertados );
			return insertados;

		} catch ( Exception e ) {
			log.error( "Ocurrió un error al insertar en tPagoCalendario: " + e.getMessage(), e );
			throw e;
		} finally {
			try {
				CloseObject.closeObject( psInsert );
				log.debug( "PreparedStatement cerrado correctamente." );
			} catch ( Exception ex ) {
				log.warn( "Error al cerrar PreparedStatement: " + ex.getMessage(), ex );
			}
		}
	}

}
