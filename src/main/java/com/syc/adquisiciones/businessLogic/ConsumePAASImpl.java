package com.syc.adquisiciones.businessLogic;


import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.syc.adquisiciones.ConsumePAASInterface;
import com.syc.adquisiciones.core.DatosPagoDirectoPAAS;
import com.syc.adquisiciones.core.Respuesta;
import com.syc.adquisiciones.manager.ConsumePAASManager;
import com.syc.adquisiciones.util.Util;
import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.core.Usuario;
import com.syc.sai.contabilidad.utils.db.CloseObject;


public class ConsumePAASImpl extends DataSourceManager implements ConsumePAASInterface {

	private static Logger log = Logger.getLogger( ConsumePAASImpl.class );

	public ConsumePAASImpl( String jniName ) {
		super.init( jniName );
	}

	public Respuesta agregaLineasPAASPagoDirecto( Usuario usuario, ArrayList<List<String>> tabla, DatosPagoDirectoPAAS datosPagoPaas ) throws Exception {
		Connection conn = null;
		Respuesta respuesta = null;
		ConsumePAASManager manager = new ConsumePAASManager();
		boolean resp = false;
		try {
			conn = getConnection();
			// desabilitar constraing
			String query = "ALTER TABLE tPagoDirectoPAASDetalle NOCHECK CONSTRAINT FK_tPagoDirectoPAASDetalle_tPagoDirectoPAAS";
			manager.execQuery( conn, query );
			// inserta detalle
			respuesta = manager.insertDetallePagoDeirectoPAAS( conn, datosPagoPaas, tabla );
			// inserta encabezado
			if ( respuesta.isResp() ) {
				Util.bitacoraMovimientos( "" + datosPagoPaas.getnFolioPago(), "Guarda encabezado pago directo paas", usuario.getLogin(), conn );
				resp = manager.insertEncabezadoPagoDeirectoPAAS( conn, datosPagoPaas );
				if ( resp ) {
					Util.bitacoraMovimientos( "" + datosPagoPaas.getnFolioPago(), "Guarda detalle pago directo paas", usuario.getLogin(), conn );
					// activar constring
					query = "ALTER TABLE tPagoDirectoPAASDetalle CHECK CONSTRAINT FK_tPagoDirectoPAASDetalle_tPagoDirectoPAAS";
					manager.execQuery( conn, query );
				} else {
					respuesta.setMsg( "Error al guardar en el encabezado." );
					respuesta.setResp( resp );
				}
			}

			if ( resp ) {
				conn.commit();
				respuesta.setMsg( "Datos Guardados." );
				respuesta.setResp( resp );
			} else {
				log.warn( respuesta.getMsg() );
				conn.rollback();
			}
		} catch ( Exception e ) {
			if ( respuesta == null ) {
				respuesta = new Respuesta();
			}
			conn.rollback();
			e.printStackTrace();
			respuesta.setMsg( e.getMessage() );
			respuesta.setResp( false );
			log.error( e.getMessage() );
		} finally {
			if ( conn != null ) {
				conn.close();
			}
			conn = null;
		}
		return respuesta;
	}

	public Respuesta actualizaLineasPAASPagoDirecto( Usuario usuario, ArrayList<List<String>> tabla, DatosPagoDirectoPAAS datos ) throws Exception {
		Connection conn = null;
		Respuesta respuesta = new Respuesta();
		ConsumePAASManager manager = new ConsumePAASManager();
		boolean resp = false;
		try {
			conn = getConnection();
			resp = manager.actualizaPagoDeirectoPAAS( conn, datos, tabla );
			Util.bitacoraMovimientos( "" + datos.getnFolioPago(), "Actualiza encabezado y detalle pago directo paas", usuario.getLogin(), conn );
			if ( resp ) {
				conn.commit();
				respuesta.setMsg( "Datos Guardados." );
				respuesta.setResp( resp );
			} else {
				conn.rollback();
			}
		} catch ( Exception e ) {
			conn.rollback();
			e.printStackTrace();
			respuesta.setMsg( e.getMessage() );
			respuesta.setResp( false );
			log.error( e.getMessage() );
		} finally {
			if ( conn != null ) {
				conn.close();
			}
			conn = null;
		}
		return respuesta;
	}

	public Respuesta eliminaLineasPAASPagoDirecto( Usuario usuario, DatosPagoDirectoPAAS datos ) throws Exception {
		Connection conn = null;
		Respuesta respuesta = new Respuesta();
		ConsumePAASManager manager = new ConsumePAASManager();
		boolean resp = false;
		try {
			conn = getConnection();
			int cantLineas = 0;
			// Elimina detalle
			String query = "DELETE dbo.tPagoDirectoPAASDetalle WHERE cEjercicio='" + datos.getcEjercicio() + "' AND cIdUnidadEjecutora='" + datos.getcIdUnidadEjecutora() + "' AND nFolioPagoDirecto=" + datos.getnFolioPago() + " AND nLinea=" + datos.getnLinea();
			resp = manager.execQuery( conn, query );
			if ( resp ) {
				// Elimina encabezado
				query = "DELETE dbo.tPagoDirectoPAAS WHERE cEjercicio='" + datos.getcEjercicio() + "' AND cIdUnidadEjecutora='" + datos.getcIdUnidadEjecutora() + "' AND nFolioPagoDirecto=" + datos.getnFolioPago() + " AND nLinea=" + datos.getnLinea();
				resp = manager.execQuery( conn, query );
				cantLineas = Util.obtieneFolio( conn, "SELECT MAX(nLinea)nLinea FROM dbo.tPagoDirectoPAAS WITH(NOLOCK)WHERE cEjercicio='" + datos.getcEjercicio() + "' AND cIdUnidadEjecutora='" + datos.getcIdUnidadEjecutora() + "' AND nFolioPagoDirecto=" + datos.getnFolioPago() );
				if ( resp && cantLineas > 1 ) {
					// desactivar constring
					query = "ALTER TABLE tPagoDirectoPAASDetalle NOCHECK CONSTRAINT FK_tPagoDirectoPAASDetalle_tPagoDirectoPAAS";
					resp = manager.execQuery( conn, query );
					// Renumerar las lineas
					query = "UPDATE dbo.tPagoDirectoPAAS SET nLinea=(nLinea-1) WHERE cEjercicio='" + datos.getcEjercicio() + "' AND cIdUnidadEjecutora='" + datos.getcIdUnidadEjecutora() + "' AND nFolioPagoDirecto=" + datos.getnFolioPago() + " AND nLinea>" + datos.getnLinea();
					resp = manager.execQuery( conn, query );
					query = "UPDATE dbo.tPagoDirectoPAASDetalle SET nLinea=(nLinea-1) WHERE cEjercicio='" + datos.getcEjercicio() + "' AND cIdUnidadEjecutora='" + datos.getcIdUnidadEjecutora() + "' AND nFolioPagoDirecto=" + datos.getnFolioPago() + " AND nLinea>" + datos.getnLinea();
					resp = manager.execQuery( conn, query );
					if ( !resp ) {
						respuesta.setMsg( "No se renumeraron las lineas" );
						respuesta.setResp( resp );
					}
					// Activar constraing
					query = "ALTER TABLE tPagoDirectoPAASDetalle CHECK CONSTRAINT FK_tPagoDirectoPAASDetalle_tPagoDirectoPAAS";
					manager.execQuery( conn, query );
				} else {
					if ( cantLineas > 1 ) {
						respuesta.setMsg( "No se elimino el encabezado." );
						respuesta.setResp( resp );
					}
				}
			} else {
				respuesta.setMsg( "No se elimino el detalle." );
				respuesta.setResp( resp );
			}

			if ( resp ) {
				conn.commit();
				respuesta.setMsg( "Registro Eliminado." );
				respuesta.setResp( resp );
			} else {
				log.warn( respuesta.getMsg() );
				conn.rollback();
			}
		} catch ( Exception e ) {
			conn.rollback();
			e.printStackTrace();
			respuesta.setMsg( e.getMessage() );
			respuesta.setResp( false );
			log.error( e.getMessage() );
		} finally {
			if ( conn != null ) {
				conn.close();
			}
			conn = null;
		}
		return respuesta;
	}

	@Override
	public BigDecimal montoMaximoTipoPago( String tipoPago ) throws Exception {

		Connection conn = null;
		try {
			conn = getConnection();
			return ConsumePAASManager.montoMaximoTipoPago( conn, tipoPago );
		} finally {
			CloseObject.closeObject( conn );
		}

	}

	@Override
	public BigDecimal getTotalCapturado( int folio ) throws Exception {
		Connection conn = null;
		try {
			conn = getConnection();
			return ConsumePAASManager.getTotalCapturado( conn, folio );
		} finally {
			CloseObject.closeObject( conn );
		}
	}

	@Override
	public int liberaPaasPago( Connection conn, int folio ) throws Exception {
		StringBuilder query = new StringBuilder();
		query.append( "UPDATE tPagoDirectoPAAS " );
		query.append( "   SET cIdEstadoLinea = 'C' " );
		query.append( " WHERE nFolioPagoDirecto = ?" );

		PreparedStatement ps = null;

		try {
			ps = conn.prepareStatement( query.toString() );
			ps.setInt( 1, folio );
			return ps.executeUpdate();
		} finally {
			CloseObject.closeObject( ps );
		}
	}

	@Override
	public String validaPAASvsSuficiencia( Connection conn, int folio ) throws Exception {
		StringBuilder query = new StringBuilder();
		query.append("	SELECT nFolioPagoDirecto, cIDSubpartida, SUM(importe) importe ");
		query.append("	FROM ( ");
		query.append("  	SELECT nFolioPagoDirecto, cIDSubpartida, -SUM(mMontoNeto) importe ");
		query.append("  	FROM tPagoDirectoPAAS WITH (NOLOCK)");
		query.append("  	WHERE nFolioPagoDirecto = ? ");
		query.append("  	GROUP BY nFolioPagoDirecto, cIDSubpartida ");
		query.append("  UNION ALL ");
		query.append("  	SELECT pd.nFolioPagoDirecto, SUBSTRING(EP, 32, 5) AS cIDSubpartida, SUM(det.mImporte) AS importe ");
		query.append("  	FROM tPagoDirectoEncabezado pd WITH (NOLOCK)");
		query.append("  	INNER JOIN tCompromisoEncabezado comp WITH (NOLOCK) ON pd.cIDContrato = comp.cIDContrato ");
		query.append("  	INNER JOIN tCompromisoDetalle det WITH (NOLOCK) ON comp.nFolioCompromiso = det.nFolioCompromiso ");
		query.append("  	WHERE comp.cDocumentoHaplicado = 'S' AND nFolioPagoDirecto = ? ");
		query.append("  	GROUP BY pd.nFolioPagoDirecto, SUBSTRING(EP, 32, 5) ");
		query.append("	) AS TBL2 ");
		query.append("	GROUP BY nFolioPagoDirecto, cIDSubpartida ");
		query.append("	HAVING SUM(importe) <> 0");


		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			ps = conn.prepareStatement( query.toString() );
			ps.setInt( 1, folio );
			ps.setInt( 2, folio );
			
			rs = ps.executeQuery();
			
			while ( rs.next() ) {
				String partida = rs.getString( "cIDSubpartida" );
				BigDecimal monto = rs.getBigDecimal( "importe" );
				if (monto.compareTo(new BigDecimal("-0.01")) < 0) {
					return "El importe del PAAS de la partida " + partida + " es MAYOR al importe de compromiso por " + monto + ". Favor de modificar el PAAS." ;
				}
			}
			return "";
			
		} finally {
			CloseObject.closeObject( rs );
			CloseObject.closeObject( ps );
			
		}
	}
}
