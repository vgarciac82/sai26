package com.syc.ejercido.pagado.core;


import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;

import com.axtel.egresos.exceptions.EgresoException;
import com.syc.contable.anteproyecto.EPManager;
import com.syc.egresos.core.EgresoDetalle;
import com.syc.egresos.core.EgresoEncabezado;
import com.syc.ejercido.pagado.EgresosBusinessLogic;
import com.syc.sai.contabilidad.utils.db.CloseObject;


public class EgresoDetalleManager {

	private static final Logger log = Logger.getLogger( EgresoDetalleManager.class );

	public static int borraDetalle( Connection conn, EgresoEncabezado encabezado ) throws Exception {
		return EgresoDetalleManager.borraDetalle( conn, encabezado.getTipoPago(), encabezado.getFolioPago() );
	}

	public static int borraDetalle( Connection conn, String tipoPago, int nFolioPago ) throws Exception {

		String tablaDetalle = "t" + tipoPago + "detalle";
		String nombreComapo = "nFolio" + tipoPago;
		log.info( "Se eliminara detalle Tabla detalle [" + tablaDetalle + "] Nombre de Campo[" + nombreComapo + "] Folio[" + nFolioPago + "]" );

		String query = "DELETE FROM " + tablaDetalle + " WHERE " + nombreComapo + "= ?";
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement( query );
			ps.setInt( 1, nFolioPago );
			int afectados = ps.executeUpdate();
			return afectados;
		} finally {
			CloseObject.closeObject( ps );
		}

	}

	public static String calculaEvento( Connection conn, EgresoEncabezado encabezado, EgresoDetalle detalle ) throws EgresoException {

		String query = "SELECT cEVTO FROM tEventoConcepto WITH(nolock) WHERE cTCONC = ? AND cOBGINI = ? AND ID_DESTINO_GASTO = ? AND cFuenteFinanciamiento = ?";
		PreparedStatement ps = null;
		ResultSet rs = null;
		String evento = null;
		try {
			ps = conn.prepareStatement( query );

			if ( "PAGOFEDERALIZADO".equals( encabezado.getTipoPago() ) ) {
				ps.setString( 1, encabezado.getIdConcepto() );
			} else {
				ps.setString( 1, detalle.getIdTipoConcepto() );
			}

			ps.setString( 2, detalle.getObgt() );
			ps.setString( 3, encabezado.getIdDestinoGasto() );
			ps.setString( 4, EPManager.getComponente( detalle.getEp(), "FUENTE_FINANCIAMIENTO" ) );
			rs = ps.executeQuery();
			if ( rs.next() ) {
				evento = rs.getString( 1 );
			} else if ( "PAGOFEDERALIZADO".equals( encabezado.getTipoPago() ) )
				throw new Exception( String.format( "No se encontro evento para la combinacion: Concepto[%s] Objeto del Gasto[%s] Destino Gasto[%s] Fuente de Financiamiento[%s]", encabezado.getIdConcepto(), detalle.getObgt(), encabezado.getIdDestinoGasto(), EPManager.getComponente( detalle.getEp(), "FUENTE_FINANCIAMIENTO" ) ) );
			else
				throw new Exception( String.format( "No se encontro evento para la combinacion: Concepto[%s] Objeto del Gasto[%s] Destino Gasto[%s] Fuente de Financiamiento[%s]", detalle.getIdTipoConcepto(), detalle.getObgt(), encabezado.getIdDestinoGasto(), EPManager.getComponente( detalle.getEp(), "FUENTE_FINANCIAMIENTO" ) ) );

			return evento;

		} catch ( Exception e ) {
			throw new EgresoException( e );
		} finally {
			CloseObject.closeObject( rs );
			CloseObject.closeObject( ps );
		}
	}

	public static String calculaProgramaFFM( Connection conn, EgresoEncabezado encabezado, EgresoDetalle detalle ) throws EgresoException {

		String query = "SELECT cast(year(faplicacion)  as varchar(4)) + right('00'+  nidprograma,2) + right('00' + csubprograma,2) + '00' FROM tPAGOFEDERALIZADOEncabezado (NOLOCK) where nFolioPAGOFEDERALIZADO = ? ";
		PreparedStatement ps = null;
		ResultSet rs = null;
		String FFM = null;
		try {
			ps = conn.prepareStatement( query );

			ps.setInt( 1, encabezado.getFolioPago() );

			rs = ps.executeQuery();

			if ( rs.next() ) {
				FFM = rs.getString( 1 );
			} else
				FFM = "";

			return FFM;

		} catch ( Exception e ) {
			throw new EgresoException( e );
		} finally {
			CloseObject.closeObject( rs );
			CloseObject.closeObject( ps );
		}
	}

	public static EgresoDetalle generaDetalle( Connection conn, EgresoCalendario importeDetalle, EgresoEncabezado encabezado, BigDecimal pcIVA, BigDecimal pcOtrosImpuestos ) throws Exception {
		EgresoDetalle egresoDetalle = EgresosBusinessLogic.instanciaDetalle( encabezado );
		return egresoDetalle.generaDetalle( conn, importeDetalle, encabezado, pcIVA, pcOtrosImpuestos );

	}

	public static EgresoDetalle generaDetalleRG( Connection conn, EgresoCalendarioRG importeDetalle, EgresoEncabezado encabezado, BigDecimal pcIVA, BigDecimal pcOtrosImpuestos ) throws Exception {
		EgresoDetalle egresoDetalle = EgresosBusinessLogic.instanciaDetalle( encabezado );
		return egresoDetalle.generaDetalleRG( conn, importeDetalle, encabezado, pcIVA, pcOtrosImpuestos );

	}

	public static EgresoDetalle generaDetalle( Connection conn, EgresoCalendario caledarioMes, EgresoEncabezado encabezado, EgresoDetalle muestra, BigDecimal pcIVA, BigDecimal pcOtrosImpuestos ) throws EgresoException {

		EgresoDetalle detalleRenglon = muestra.renglonNuevo();
		detalleRenglon.setRfc( encabezado.getRfc() );
		caledarioMes.setIdTipoConcepto( detalleRenglon.getIdTipoConcepto() );
		caledarioMes.setIdTipoMovimiento( detalleRenglon.getIdTipoMovimiento() );

		try {
			return detalleRenglon.generaDetalle( conn, caledarioMes, encabezado, pcIVA, pcOtrosImpuestos );
		} catch ( Exception e ) {
			throw new EgresoException( e );
		}

	}
}
