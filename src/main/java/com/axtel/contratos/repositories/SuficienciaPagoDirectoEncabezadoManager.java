package com.axtel.contratos.repositories;


import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

import org.apache.log4j.Logger;

import com.axtel.contratos.core.SuficienciaPagoDirectoEncabezado;


public class SuficienciaPagoDirectoEncabezadoManager {

	private static final Logger log = Logger.getLogger( SuficienciaPagoDirectoEncabezadoManager.class );

	public static boolean finalizarContratoDirecto( Connection conn, int nFolioSuficienciaPagoDirecto ) throws SQLException {
		log.info( "[SP] Iniciando sp_FinalizarContratoDirecto, folio=" + nFolioSuficienciaPagoDirecto );

		final String call = "{ call dbo.sp_FinalizarContratoDirecto(?) }";
		try ( CallableStatement cs = conn.prepareCall( call ) ) {
			cs.setInt( 1, nFolioSuficienciaPagoDirecto );

			boolean hasResults = cs.execute();
			boolean ok = false;

			// Consumimos posibles result sets devolviendo un 'status'
			while ( hasResults ) {
				try ( ResultSet rs = cs.getResultSet() ) {
					if ( rs != null ) {
						ResultSetMetaData md = rs.getMetaData();
						// Si el SP devolvió columnas tipo 'status'/'folio'
						if ( md.getColumnCount() >= 1 ) {
							while ( rs.next() ) {
								String status = null;
								try {
									status = rs.getString( "status" );
								} catch ( SQLException ignore ) {
								}
								if ( status != null && "OK".equalsIgnoreCase( status ) ) {
									ok = true;
								}
							}
						}
					}
				}
				hasResults = cs.getMoreResults();
			}

			log.info( "[SP] sp_FinalizarContratoDirecto ejecutado. ok=" + ok );
			return ok;
		} catch ( SQLException ex ) {
			log.error( "[SP] Error en sp_FinalizarContratoDirecto. SQLState=" + ex.getSQLState() + " Code=" + ex.getErrorCode() + " Msg=" + ex.getMessage(), ex );
			throw ex;
		}
	}

	public static void insert( Connection conn, SuficienciaPagoDirectoEncabezado bean ) throws SQLException {
		log.info( "Insertando SuficienciaPagoDirectoEncabezado..." + bean );
		String sql = "INSERT INTO tSuficienciaPagoDirectoEncabezado " + "(nFolioSuficienciaPagoDirecto, fCarga, cIDContrato, cTipoContrato, fAplicacion, fCancelacion, " + "cCentroContable, cRamo, cUnidadResponsable, cDocumentoHAplicado, cTipoPoliza, nFolioPoliza, " + "nFolioPolizaCancelacion, idStatus, aEjercicioFiscal, cUnidadResponsableContable, RFC, " + "cJustificacion, uLoginCaptura, mImporte, mImporteIVA, mTotal, nPorcentajeIVA) " + "VALUES (?, GETDATE(), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		try ( PreparedStatement ps = conn.prepareStatement( sql ) ) {
			ps.setInt( 1, bean.getFolioSuficienciaPagoDirecto() );
			ps.setString( 2, bean.getIdContrato() );
			ps.setString( 3, bean.getTipoContrato() );
			ps.setDate( 4, new java.sql.Date( bean.getFechaAplicacion().getTime() ) );
			ps.setDate( 5, bean.getFechaCancelacion() != null ? new java.sql.Date( bean.getFechaCancelacion().getTime() ) : null );
			ps.setInt( 6, bean.getCentroContable() );
			ps.setString( 7, bean.getRamo() );
			ps.setString( 8, bean.getUnidadResponsable() );
			ps.setString( 9, bean.getDocumentoAplicado() );
			ps.setString( 10, bean.getTipoPoliza() );
			ps.setObject( 11, bean.getFolioPoliza(), Types.INTEGER );
			ps.setObject( 12, bean.getFolioPolizaCancelacion(), Types.INTEGER );
			ps.setInt( 13, bean.getIdStatus() );
			ps.setInt( 14, bean.getEjercicioFiscal() );
			ps.setString( 15, bean.getUnidadResponsableContable() );
			ps.setString( 16, bean.getRfc() );
			ps.setString( 17, bean.getJustificacion() );
			ps.setString( 18, bean.getLoginCaptura() );
			ps.setBigDecimal( 19, bean.getImporte() );
			ps.setBigDecimal( 20, bean.getImporteIVA() );
			ps.setBigDecimal( 21, bean.getTotal() );
			ps.setInt( 22, bean.getPorcentajeIVA() );

			int rows = ps.executeUpdate();
			log.info( "Filas insertadas: " + rows );
		}
	}

	public static SuficienciaPagoDirectoEncabezado findById( Connection conn, int folio ) throws SQLException {
		log.info( "Buscando SuficienciaPagoDirectoEncabezado por folio: " + folio );
		String sql = "SELECT e.*, p.cRazonSocial AS nombre FROM tSuficienciaPagoDirectoEncabezado e inner join mCatalogoProveedor p ON e.RFC = replace(p.cIdRFC, '-','') WHERE nFolioSuficienciaPagoDirecto =?";
		try ( PreparedStatement ps = conn.prepareStatement( sql ) ) {
			ps.setInt( 1, folio );
			try ( ResultSet rs = ps.executeQuery() ) {
				if ( rs.next() ) {
					return map( rs );
				} else {
					return null;
				}
			}
		}
	}

	public static void update( Connection conn, SuficienciaPagoDirectoEncabezado bean ) throws SQLException {
		log.info( "Actualizando SuficienciaPagoDirectoEncabezado folio: " + bean.getFolioSuficienciaPagoDirecto() );
		String sql = "UPDATE tSuficienciaPagoDirectoEncabezado SET " + "fAplicacion = ?, fCancelacion = ?,  cTipoContrato = ?, " + "cCentroContable = ?, cRamo = ?, cUnidadResponsable = ?, cDocumentoHAplicado = ?, " + "cTipoPoliza = ?, nFolioPoliza = ?, nFolioPolizaCancelacion = ?, idStatus = ?, " + "aEjercicioFiscal = ?, cUnidadResponsableContable = ?, RFC = ?, cJustificacion = ?, " + "uLoginCaptura = ?, mImporte = ?, mImporteIVA = ?, mTotal = ?, nPorcentajeIVA = ? " + "WHERE nFolioSuficienciaPagoDirecto = ?";

		try ( PreparedStatement ps = conn.prepareStatement( sql ) ) {
			int i=1;
			ps.setDate( i++, new java.sql.Date( bean.getFechaAplicacion().getTime() ) );
			ps.setDate( i++, bean.getFechaCancelacion() != null ? new java.sql.Date( bean.getFechaCancelacion().getTime() ) : null );
			ps.setString( i++, bean.getTipoContrato() );
			ps.setInt( i++, bean.getCentroContable() );
			ps.setString( i++, bean.getRamo() );
			ps.setString( i++, bean.getUnidadResponsable() );
			ps.setString( i++, bean.getDocumentoAplicado() );
			ps.setString( i++, bean.getTipoPoliza() );
			ps.setObject( i++, bean.getFolioPoliza(), Types.INTEGER );
			ps.setObject( i++, bean.getFolioPolizaCancelacion(), Types.INTEGER );
			ps.setInt( i++, bean.getIdStatus() );
			ps.setInt( i++, bean.getEjercicioFiscal() );
			ps.setString( i++, bean.getUnidadResponsableContable() );
			ps.setString( i++, bean.getRfc() );
			ps.setString( i++, bean.getJustificacion() );
			ps.setString( i++, bean.getLoginCaptura() );
			ps.setBigDecimal( i++, bean.getImporte() );
			ps.setBigDecimal( i++, bean.getImporteIVA() );
			ps.setBigDecimal( i++, bean.getTotal() );
			ps.setInt( i++, bean.getPorcentajeIVA() );
			ps.setInt( i++, bean.getFolioSuficienciaPagoDirecto() );

			int rows = ps.executeUpdate();
			log.info( "Filas actualizadas: " + rows );
		}
	}

	public static void delete( Connection conn, int folio ) throws SQLException {

		log.info( "Eliminando SuficienciaPagoDirectoEncabezado folio: " + folio );

		// String sql = "DELETE FROM tSuficienciaPagoDirectoEncabezado WHERE
		// nFolioSuficienciaPagoDirecto = ?";
		String sql = "UPDATE tSuficienciaPagoDirectoEncabezado SET cDocumentoHAplicado = 'C', idStatus = 3 WHERE nFolioSuficienciaPagoDirecto = ?";

		try ( PreparedStatement ps = conn.prepareStatement( sql ) ) {
			ps.setInt( 1, folio );
			int rows = ps.executeUpdate();
			log.info( "Filas actualizadas: " + rows );
		}
	}

	private static SuficienciaPagoDirectoEncabezado map( ResultSet rs ) throws SQLException {
		SuficienciaPagoDirectoEncabezado bean = new SuficienciaPagoDirectoEncabezado();
		bean.setFolioSuficienciaPagoDirecto( rs.getInt( "nFolioSuficienciaPagoDirecto" ) );
		bean.setFechaCarga( rs.getDate( "fCarga" ) );
		bean.setIdContrato( rs.getString( "cIDContrato" ) );
		bean.setTipoContrato( rs.getString( "cTipoContrato" ) );
		bean.setFechaAplicacion( rs.getDate( "fAplicacion" ) );
		bean.setFechaCancelacion( rs.getDate( "fCancelacion" ) );
		bean.setCentroContable( rs.getInt( "cCentroContable" ) );
		bean.setRamo( rs.getString( "cRamo" ) );
		bean.setUnidadResponsable( rs.getString( "cUnidadResponsable" ) );
		bean.setDocumentoAplicado( rs.getString( "cDocumentoHAplicado" ) );
		bean.setTipoPoliza( rs.getString( "cTipoPoliza" ) );
		bean.setFolioPoliza( ( Integer ) rs.getObject( "nFolioPoliza" ) );
		bean.setFolioPolizaCancelacion( ( Integer ) rs.getObject( "nFolioPolizaCancelacion" ) );
		bean.setIdStatus( rs.getInt( "idStatus" ) );
		bean.setEjercicioFiscal( rs.getInt( "aEjercicioFiscal" ) );
		bean.setUnidadResponsableContable( rs.getString( "cUnidadResponsableContable" ) );
		bean.setRfc( rs.getString( "RFC" ) );
		bean.setJustificacion( rs.getString( "cJustificacion" ) );
		bean.setLoginCaptura( rs.getString( "uLoginCaptura" ) );
		bean.setImporte( rs.getBigDecimal( "mImporte" ) );
		bean.setImporteIVA( rs.getBigDecimal( "mImporteIVA" ) );
		bean.setTotal( rs.getBigDecimal( "mTotal" ) );
		bean.setPorcentajeIVA( rs.getInt( "nPorcentajeIVA" ) );
		bean.setRazonSocial( rs.getString( "nombre" ) );
		return bean;
	}
	

	public static void updateStatusCancelPayment ( Connection conn, int folio ) throws SQLException {
		log.info( "Actualizar status para el folio: " + folio );
		String sql = "UPDATE suf SET idStatus = 2 FROM tPagoDirectoEncabezado pago INNER JOIN tSuficienciaPagoDirectoEncabezado suf ON pago.cIDContrato = suf.cIDContrato WHERE pago.nFolioPagoDirecto = ?";

		try ( PreparedStatement ps = conn.prepareStatement( sql ) ) {
			ps.setInt( 1, folio );
			int rows = ps.executeUpdate();
			log.info( "Filas Actualizadas: " + rows );
		}
	}
}
