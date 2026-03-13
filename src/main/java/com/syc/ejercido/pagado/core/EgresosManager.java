package com.syc.ejercido.pagado.core;


import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.log4j.Logger;

import com.axtel.egresos.exceptions.EgresoException;
import com.axtel.egresos.exceptions.RelacionRegimenRetencionException;
import com.syc.contable.AccountingEngine;
import com.syc.contable.anteproyecto.EPManager;
import com.syc.contable.core.EgresoImpuestos;
import com.syc.contable.core.SaldoMensual;
import com.syc.egresos.core.Amortizacion;
import com.syc.egresos.core.EgresoDetalle;
import com.syc.egresos.core.EgresoEncabezado;
import com.syc.egresos.core.EgresoRetencion;
import com.syc.egresos.core.PenaConvencional;
import com.syc.egresos.core.impl.EgresoPAGOFEDERALIZADODetalle;
import com.syc.ejercido.pagado.EgresosBusinessLogic;
import com.syc.gestion.core.CFSequenceManager;
import com.syc.gestion.core.Caso;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import com.syc.sai.contabilidad.utils.db.RSToTable;


public class EgresosManager {

	private static final Logger	log		= Logger.getLogger( EgresosManager.class );
	public static final String	RFCFFM	= "BMN930209927";

	public static void actualizaInformacionEmpleados( Connection conn, String document, String nFolio, String headerTable, String detailTable, String field, String usuarioCaptura, String usuarioVoBo, String usuarioAutoriza ) throws Exception {
		StringBuilder query = new StringBuilder();
		query.append( "UPDATE " + headerTable );
		query.append( "   SET nNumEmpleadoVoBo = ?, " );
		query.append( "       nNumEmpleadoAut  = ?, " );
		query.append( "       nNumEmpleadoElab  = ? " );
		query.append( " WHERE	" ).append( field ).append( " = ?" );
		PreparedStatement ps = null;

		try {

			ps = conn.prepareStatement( query.toString() );
			ps.setString( 1, usuarioVoBo );
			ps.setString( 2, usuarioAutoriza );
			ps.setString( 3, usuarioCaptura );
			ps.setString( 4, nFolio );

			int afectados = ps.executeUpdate();
			log.debug( "Se actualizaron " + afectados + " registros con los firmantes Captura: " + usuarioCaptura + " VoBo " + usuarioVoBo + " Autoriza " + usuarioAutoriza + " Para el tramite " + headerTable + " con Folio " + nFolio );

		} finally {
			CloseObject.closeObject( ps );
		}

	}

	public static void aplicaApartadoTramite( Connection conn, int folioPagoApartado ) throws Exception {
		String documento = "PAGOAPARTADO";
		String folio = String.valueOf( folioPagoApartado );
		String campo = "nFolioPagoApartado";
		String tablaEncabezado = "tPagoApartadoEncabezado";
		String tablaDetalle = "tPagoApartadoDetalle";

		AccountingEngine ae = new AccountingEngine();
		ae.setValidaInsuficienciaDeSaldo( true );
		ae.makeAccountingApplication( conn, documento, folio, tablaEncabezado, tablaDetalle, campo );

	}

	public static void aplicaTramite( Connection conn, EgresoEncabezado encabezado ) throws Exception {

		String documento = encabezado.getTipoPago();
		String folio = String.valueOf( encabezado.getFolioPago() );
		String campo = "nFolio" + documento;
		String tablaEncabezado = "t" + documento + "Encabezado";
		String tablaDetalle = "t" + documento + "Detalle";

		AccountingEngine ae = new AccountingEngine();
		ae.setValidaInsuficienciaDeSaldo( true );
		ae.makeAccountingApplication( conn, documento, folio, tablaEncabezado, tablaDetalle, campo );

	}

	private static void cambiaEstatusApartado( Connection conn, int folioApartado, String status ) throws Exception {
		String query = "UPDATE tPagoApartadoEncabezado SET cDocumentohAplicado = ? WHERE nFolioPagoApartado = ?";
		PreparedStatement ps = null;

		try {
			ps = conn.prepareStatement( query );
			ps.setString( 1, status );
			ps.setInt( 2, folioApartado );

			ps.executeUpdate();
		} finally {
			CloseObject.closeObject( ps );
		}

	}

	public static void cancelaApartadoTramite( Connection conn, EgresoEncabezado encabezado ) throws Exception {

		int folioApartado = EgresosManager.getFolioApartado( conn, encabezado.getTipoPago(), encabezado.getFolioPago() );

		if ( folioApartado > 0 ) {
			boolean apartadoAplicado = EgresosManager.isApartadoAplicado( conn, encabezado.getTipoPago(), encabezado.getFolioPago() );
			if ( apartadoAplicado ) {
				String documento = "PAGOAPARTADO";
				String folio = String.valueOf( folioApartado );
				String campo = "nFolioPagoApartado";
				String tablaEncabezado = "tPagoApartadoEncabezado";
				String tablaDetalle = "tPagoApartadoDetalle";

				AccountingEngine ae = new AccountingEngine();
				ae.setValidaInsuficienciaDeSaldo( true );
				ae.cancelAccountingApplication( conn, documento, folio, tablaEncabezado, tablaDetalle, campo, Util.getToday( "yyyy-MM-dd" ) );
			} else
				EgresosManager.cambiaEstatusApartado( conn, folioApartado, "C" );
		}
	}

	public static void cancelaTramite( Connection conn, EgresoEncabezado encabezado ) throws Exception {

		String documento = encabezado.getTipoPago();
		String folio = String.valueOf( encabezado.getFolioPago() );
		String campo = "nFolio" + documento;
		String tablaEncabezado = "t" + documento + "Encabezado";
		String tablaDetalle = "t" + documento + "Detalle";

		AccountingEngine ae = new AccountingEngine();
		ae.setValidaInsuficienciaDeSaldo( true );
		ae.cancelAccountingApplication( conn, documento, folio, tablaEncabezado, tablaDetalle, campo, Util.getToday( "yyyy-MM-dd" ) );

	}

	public static List<EgresoCalendario> cargaCalendarioEgreso( Connection conn, String tipoEgreso, int folioEgreso ) throws EgresoException {

		StringBuilder query = new StringBuilder();
		query.append( "SELECT	cTipoPago AS tipoPago, " );
		query.append( "			nFolioPago AS folioPago, " );
		query.append( "			EP AS ep, " );
		query.append( "			nMes AS mesPresupuesto, " );
		query.append( "			idTipoConcepto AS idTipoConcepto, " );
		query.append( "			idTipoMovimiento AS idTipoMovimiento, " );
		query.append( "			mImporteBruto AS importeBruto" );
		query.append( "  FROM	tPagoCalendario WITH(NOLOCK)" );
		query.append( " WHERE	cTipoPago = ? " );
		query.append( "   AND	nFolioPago = ? " );
		query.append( " ORDER BY ep, mImporteBruto DESC" );

		PreparedStatement ps = null;
		ResultSet rs = null;
		List<EgresoCalendario> calendarioPresupuesto = new ArrayList<EgresoCalendario>();
		try {

			ps = conn.prepareStatement( query.toString() );
			ps.setString( 1, tipoEgreso );
			ps.setInt( 2, folioEgreso );

			rs = ps.executeQuery();

			DateConverter converter = new DateConverter( null );
			Map<String, String> resultObj = RSToTable.rsToMapCaseSensitive( rs );
			converter.setPattern( "yyyy-MM-dd" );
			ConvertUtils.register( converter, Date.class );

			while ( resultObj != null ) {

				EgresoCalendario calPresupuesto = new EgresoCalendario();
				BeanUtils.populate( calPresupuesto, resultObj );
				calendarioPresupuesto.add( calPresupuesto );
				resultObj = RSToTable.rsToMapCaseSensitive( rs );

			}
			return calendarioPresupuesto;
		} catch ( Exception e ) {
			throw new EgresoException( e );
		} finally {
			CloseObject.closeObject( rs );
			CloseObject.closeObject( ps );
		}
	}

	public static List<EgresoCalendarioRG> cargaCalendarioEgresoRG( Connection conn, String tipoEgreso, int folioEgreso ) throws EgresoException {

		StringBuilder query = new StringBuilder();
		query.append( "SELECT	cTipoPago AS tipoPago, " );
		query.append( "			nFolioPago AS folioPago, " );
		query.append( "			EP AS ep, " );
		query.append( "			nMes AS mesPresupuesto, " );
		query.append( "			idTipoConcepto AS idTipoConcepto, " );
		query.append( "			idTipoMovimiento AS idTipoMovimiento, " );
		query.append( "			mImporteBruto AS importeBruto," );
		query.append( "			mImporteRetencion AS montoRetencion " );
		query.append( "  FROM	tPagoCalendario WITH(NOLOCK)" );
		query.append( "  WHERE	cTipoPago = ? " );
		query.append( "   AND	nFolioPago = ? " );
		query.append( " ORDER BY ep, mImporteBruto DESC" );

		PreparedStatement ps = null;
		ResultSet rs = null;
		List<EgresoCalendarioRG> calendarioPresupuesto = new ArrayList<EgresoCalendarioRG>();
		try {

			ps = conn.prepareStatement( query.toString() );
			ps.setString( 1, tipoEgreso );
			ps.setInt( 2, folioEgreso );

			rs = ps.executeQuery();

			DateConverter converter = new DateConverter( null );
			Map<String, String> resultObj = RSToTable.rsToMapCaseSensitive( rs );
			converter.setPattern( "yyyy-MM-dd" );
			ConvertUtils.register( converter, Date.class );

			while ( resultObj != null ) {

				EgresoCalendarioRG calPresupuesto = new EgresoCalendarioRG();
				BeanUtils.populate( calPresupuesto, resultObj );
				calendarioPresupuesto.add( calPresupuesto );
				resultObj = RSToTable.rsToMapCaseSensitive( rs );
			}

			return calendarioPresupuesto;

		} catch ( Exception e ) {

			throw new EgresoException( e );

		} finally {
			CloseObject.closeObject( rs );
			CloseObject.closeObject( ps );
		}
	}

	public static List<EgresoImpuestos> cargaImpuestos( Connection conn, String tipoEgreso, int folioEgreso ) throws Exception {

		StringBuilder query = new StringBuilder();
		query.append( "SELECT	cTipoPago AS tipoPago, " );
		query.append( "      	nFolioPago AS folioPago, " );
		query.append( "      	idContrato AS idContrato, " );
		query.append( "      	recepcion AS recepcion, " );
		query.append( "      	montoSinIVA AS montoSinIVA, " );
		query.append( "      	montoIVA AS montoIVA, " );
		query.append( "      	montoOtrosImpuestos AS montoOtrosImpuestos, " );
		query.append( "      	totalMasIVA AS totalMasIVA, " );
		query.append( "      	total AS total, " );
		query.append( "      	porcentajeIVA AS porcentajeIVA, " );
		query.append( "      	porcentajeOtrosImpuestos AS porcentajeOtrosImpuestos" );
		query.append( "  FROM	vPagoImpuestos WITH(NOLOCK) " );
		query.append( " WHERE	cTipoPago = ?" );
		query.append( "   AND	nFolioPago = ?" );

		PreparedStatement ps = null;
		ResultSet rs = null;
		List<EgresoImpuestos> impuestos = new ArrayList<EgresoImpuestos>();
		try {

			ps = conn.prepareStatement( query.toString() );
			ps.setString( 1, tipoEgreso );
			ps.setInt( 2, folioEgreso );

			rs = ps.executeQuery();

			DateConverter converter = new DateConverter( null );
			Map<String, String> resultObj = RSToTable.rsToMapCaseSensitive( rs );
			converter.setPattern( "yyyy-MM-dd" );
			ConvertUtils.register( converter, Date.class );

			while ( resultObj != null ) {

				EgresoImpuestos impuesto = new EgresoImpuestos();
				BeanUtils.populate( impuesto, resultObj );
				impuestos.add( impuesto );
				resultObj = RSToTable.rsToMapCaseSensitive( rs );
			}
			return impuestos;
		} finally {
			CloseObject.closeObject( rs );
			CloseObject.closeObject( ps );
		}
	}

	public static List<EgresoImpuestos> cargaImpuestosDiversoMasivo( Connection conn, String tipoEgreso, int folioEgreso ) throws Exception {

		StringBuilder query = new StringBuilder();
		query.append( "SELECT	cTipoPago AS tipoPago, " );
		query.append( "      	nFolioPago AS folioPago, " );
		query.append( "      	idContrato AS idContrato, " );
		query.append( "      	recepcion AS recepcion, " );
		query.append( "      	montoSinIVA AS montoSinIVA, " );
		query.append( "      	montoIVA AS montoIVA, " );
		query.append( "      	montoOtrosImpuestos AS montoOtrosImpuestos, " );
		query.append( "      	totalMasIVA AS totalMasIVA, " );
		query.append( "      	total AS total, " );
		query.append( "      	porcentajeIVA AS porcentajeIVA, " );
		query.append( "      	porcentajeOtrosImpuestos AS porcentajeOtrosImpuestos" );
		query.append( "  FROM	vPagoDiversoImpuestos_CFDI WITH(NOLOCK) " );
		query.append( " WHERE	cTipoPago = ?" );
		query.append( "   AND	nFolioPago = ?" );

		PreparedStatement ps = null;
		ResultSet rs = null;
		List<EgresoImpuestos> impuestos = new ArrayList<EgresoImpuestos>();
		try {

			ps = conn.prepareStatement( query.toString() );
			ps.setString( 1, tipoEgreso );
			ps.setInt( 2, folioEgreso );

			rs = ps.executeQuery();

			DateConverter converter = new DateConverter( null );
			Map<String, String> resultObj = RSToTable.rsToMapCaseSensitive( rs );
			converter.setPattern( "yyyy-MM-dd" );
			ConvertUtils.register( converter, Date.class );

			while ( resultObj != null ) {

				EgresoImpuestos impuesto = new EgresoImpuestos();
				BeanUtils.populate( impuesto, resultObj );
				impuestos.add( impuesto );
				resultObj = RSToTable.rsToMapCaseSensitive( rs );
			}
			return impuestos;
		} finally {
			CloseObject.closeObject( rs );
			CloseObject.closeObject( ps );
		}
	}

	public static List<EgresoImpuestos> cargaImpuestosNomina( Connection conn, String tipoEgreso, int folioEgreso ) throws Exception {

		StringBuilder query = new StringBuilder();
		query.append( "SELECT	cTipoPago AS tipoPago, " );
		query.append( "      	nFolioPago AS folioPago, " );
		query.append( "      	idContrato AS idContrato, " );
		query.append( "      	recepcion AS recepcion, " );
		query.append( "      	montoSinIVA AS montoSinIVA, " );
		query.append( "      	montoIVA AS montoIVA, " );
		query.append( "      	montoOtrosImpuestos AS montoOtrosImpuestos, " );
		query.append( "      	totalMasIVA AS totalMasIVA, " );
		query.append( "      	total AS total, " );
		query.append( "      	porcentajeIVA AS porcentajeIVA, " );
		query.append( "      	porcentajeOtrosImpuestos AS porcentajeOtrosImpuestos" );
		query.append( "  FROM	vPagoImpuestosNomina WITH(NOLOCK) " );
		query.append( " WHERE	cTipoPago = ?" );
		query.append( "   AND	nFolioPago = ?" );

		PreparedStatement ps = null;
		ResultSet rs = null;
		List<EgresoImpuestos> impuestos = new ArrayList<EgresoImpuestos>();
		try {

			ps = conn.prepareStatement( query.toString() );
			ps.setString( 1, tipoEgreso );
			ps.setInt( 2, folioEgreso );

			rs = ps.executeQuery();

			DateConverter converter = new DateConverter( null );
			Map<String, String> resultObj = RSToTable.rsToMapCaseSensitive( rs );
			converter.setPattern( "yyyy-MM-dd" );
			ConvertUtils.register( converter, Date.class );

			while ( resultObj != null ) {

				EgresoImpuestos impuesto = new EgresoImpuestos();
				BeanUtils.populate( impuesto, resultObj );
				impuestos.add( impuesto );
				resultObj = RSToTable.rsToMapCaseSensitive( rs );
			}
			return impuestos;
		} finally {
			CloseObject.closeObject( rs );
			CloseObject.closeObject( ps );
		}
	}

	public static List<PenaConvencional> cargaPenas( Connection conn, String tipoEgreso, int folioEgreso ) throws Exception {

		StringBuilder query = new StringBuilder();
		query.append( "SELECT	cTipoPago AS tipoPago, " );
		query.append( "      	nFolioPago AS folioPago, " );
		query.append( "      	idContrato AS idContrato, " );
		query.append( "      	importeSancionBruto AS importeSancionBruto, " );
		query.append( "      	importeSancionIVA AS importeSancionIVA, " );
		query.append( "      	importeSancionNeto AS importeSancionNeto " );
		query.append( "  FROM	vPagoPenalizaciones WITH(NOLOCK)" );
		query.append( " WHERE	cTipoPago = ?" );
		query.append( "   AND	nFolioPago = ?" );

		PreparedStatement ps = null;
		ResultSet rs = null;
		List<PenaConvencional> penas = new ArrayList<PenaConvencional>();
		try {

			ps = conn.prepareStatement( query.toString() );
			ps.setString( 1, tipoEgreso );
			ps.setInt( 2, folioEgreso );

			rs = ps.executeQuery();

			Map<String, String> resultObj = RSToTable.rsToMapCaseSensitive( rs );

			while ( resultObj != null ) {

				PenaConvencional pena = new PenaConvencional();
				BeanUtils.populate( pena, resultObj );
				penas.add( pena );
				resultObj = RSToTable.rsToMapCaseSensitive( rs );
			}

			return penas;

		} finally {
			CloseObject.closeObject( rs );
			CloseObject.closeObject( ps );
		}
	}

	public static List<EgresoRetencion> cargaRetenciones( Connection conn, String tipoEgreso, int folioEgreso ) throws Exception {

		StringBuilder query = new StringBuilder();
		query.append( "SELECT	cIDContrato AS idContrato, " );
		query.append( "		cTipoPago AS tipoPago, " );
		query.append( "		nFolioPago AS folioPago, " );
		query.append( "		caNoContrarrecibo AS contrarecibo, " );
		query.append( "		mImporteBruto AS importeBruto, " );
		query.append( "		cIdTipoRetencion AS idTipoRetencion, " );
		query.append( "		nPorcRetencion AS porcRetencion, " );
		query.append( "		cTipoRetencion AS tipoRetencion, " );
		query.append( "		importeRetencion AS importeRetencion," );
		query.append( "		componente" );
		query.append( " FROM	vPagoRetencion WITH(NOLOCK) " );
		query.append( " WHERE	cTipoPago = ?" );
		query.append( "  AND	nFolioPago = ?" );
		query.append( "  AND	importeRetencion > 0.00 " );

		PreparedStatement ps = null;
		ResultSet rs = null;
		List<EgresoRetencion> retenciones = new ArrayList<EgresoRetencion>();
		try {

			ps = conn.prepareStatement( query.toString() );
			ps.setString( 1, tipoEgreso );
			ps.setInt( 2, folioEgreso );

			rs = ps.executeQuery();

			DateConverter converter = new DateConverter( null );
			Map<String, String> resultObj = RSToTable.rsToMapCaseSensitive( rs );
			converter.setPattern( "yyyy-MM-dd" );
			ConvertUtils.register( converter, Date.class );

			while ( resultObj != null ) {

				EgresoRetencion retencion = new EgresoRetencion();
				BeanUtils.populate( retencion, resultObj );
				retenciones.add( retencion );
				resultObj = RSToTable.rsToMapCaseSensitive( rs );
			}

			return retenciones;

		} finally {
			CloseObject.closeObject( rs );
			CloseObject.closeObject( ps );
		}
	}

	public static boolean esPagoConLayout( Connection conn, Caso c ) throws Exception {

		PreparedStatement psBuscaLayout = null;
		ResultSet rs = null;

		String queryBuscaLayout = "SELECT dbo.fn_StatusOperacion( ?,?)";
		boolean esPagoConLayout = false;

		try {
			psBuscaLayout = conn.prepareStatement( queryBuscaLayout );

			int idTc = c.getIdTC();
			int nFolioPago = Integer.parseInt( c.getFolio().substring( c.getFolio().lastIndexOf( '-' ) + 1 ) );

			psBuscaLayout.setInt( 1, idTc );
			psBuscaLayout.setInt( 2, nFolioPago );

			rs = psBuscaLayout.executeQuery();

			if ( rs.next() )
				esPagoConLayout = ! ( "DEVENGADO".equalsIgnoreCase( rs.getString( 1 ) ) || "PENDIENTE".equalsIgnoreCase( rs.getString( 1 ) ) );

			return esPagoConLayout;

		} finally {
			CloseObject.closeObject( psBuscaLayout, false );
		}

	}

	public static List<EgresoDetalle> generaDetallePenas( Connection conn, List<SaldoMensual> calendarioPenas, PenaConvencional pena, EgresoEncabezado encabezado, EgresoDetalle muestra, BigDecimal pcIVA, BigDecimal pcOtrosImpuestos ) throws EgresoException {
		List<EgresoDetalle> detallePenas = new ArrayList<EgresoDetalle>();

		for ( SaldoMensual saldoMes : calendarioPenas ) {
			EgresoDetalle detalle = muestra.renglonNuevo();
			detalle.setEp( saldoMes.getEp() );
			detalle.setRfc( "TESOFE" );
			detalle.setImporteComprometido( saldoMes.getMontoSaldo() );
			detalle.setImporteNeto( new BigDecimal( 0.00d ) );
			detalle.setImporteBruto( saldoMes.getMontoSaldo() );
			detalle.setImporteMasIva( saldoMes.getMontoSaldo() );
			detalle.setImporteIva( new BigDecimal( 0.00d ) );
			detalle.setCapitulo( EPManager.getComponente( saldoMes.getEp(), "CAPITULO" ) );
			detalle.setImporteRetencion( Util.ZERO );
			detalle.setImporteIva( new BigDecimal( 0.0 ) );
			detalle.setObgt( EPManager.getComponente( saldoMes.getEp(), "CAPITULO" ) );
			detalle.setMesCalendario( String.valueOf( saldoMes.getMes() ) );
			detalle.setEvento( "D_" + EgresoDetalleManager.calculaEvento( conn, encabezado, detalle ) );
			try {
				BeanUtils.setProperty( detalle, pena.getComponente(), saldoMes.getMontoSaldo() );
			} catch ( IllegalAccessException | InvocationTargetException e ) {
				throw new EgresoException( e );
			}
			detallePenas.add( detalle );
		}

		return detallePenas;
	}

	public static List<EgresoDetalle> generaDetalleRetencion( Connection conn, List<SaldoMensual> calendarioRetencion, EgresoRetencion retencion, EgresoEncabezado encabezado, BigDecimal pcIVA, BigDecimal pcOtrosImpuestos ) throws Exception {
		EgresoDetalle detalle = EgresosBusinessLogic.instanciaDetalle( encabezado );
		return detalle.generaDetalleRetencion( conn, encabezado, calendarioRetencion, retencion, pcIVA, pcOtrosImpuestos );
	}

	public static List<EgresoDetalle> generaDetalleRetencion( Connection conn, List<SaldoMensual> calendarioRetencion, EgresoRetencion retencion, EgresoEncabezado encabezado, EgresoDetalle muestra, BigDecimal pcIVA, BigDecimal pcOtrosImpuestos ) throws EgresoException {

		List<EgresoDetalle> detalleRetenciones = new ArrayList<EgresoDetalle>();
		double porc = pcIVA.doubleValue() + 1;
		double porcentaje = pcIVA.doubleValue() * 100;

		for ( SaldoMensual saldoMes : calendarioRetencion ) {
			BigDecimal importe = saldoMes.getMontoSaldo();
			double bruto = importe.doubleValue() / porc;
			double iva = importe.doubleValue() - bruto;

			EgresoDetalle detalle = muestra.renglonNuevo();
			detalle.setEp( saldoMes.getEp() );
			detalle.setImporteComprometido( saldoMes.getMontoSaldo() );
			detalle.setImporteNeto( new BigDecimal( 0.00d ) );
			detalle.setImporteBruto( new BigDecimal( bruto ).setScale( 2, RoundingMode.HALF_UP ) );
			detalle.setImporteMasIva( saldoMes.getMontoSaldo() );
			detalle.setImporteIVA( new BigDecimal( iva ).setScale( 2, RoundingMode.HALF_UP ) );
			detalle.setIva( new BigDecimal( iva ).setScale( 2, RoundingMode.HALF_UP ) );
			detalle.setCapitulo( EPManager.getComponente( saldoMes.getEp(), "CAPITULO" ) );
			detalle.setImporteRetencion( saldoMes.getMontoSaldo() );
			detalle.setImporteIva( new BigDecimal( porcentaje ).setScale( 2, RoundingMode.HALF_UP ) );
			detalle.setObgt( EPManager.getComponente( saldoMes.getEp(), "CAPITULO" ) );
			detalle.setMesCalendario( String.valueOf( saldoMes.getMes() ) );
			detalle.setEvento( "D_" + EgresoDetalleManager.calculaEvento( conn, encabezado, detalle ) );

			if ( RFCFFM.equals( encabezado.getRfc() ) ) {
				( ( EgresoPAGOFEDERALIZADODetalle ) detalle ).setFFM( detalle.getObgt() + EgresoDetalleManager.calculaProgramaFFM( conn, encabezado, detalle ) );
			}

			try {
				BeanUtils.setProperty( detalle, retencion.getComponente(), saldoMes.getMontoSaldo() );
			} catch ( IllegalAccessException | InvocationTargetException e ) {
				throw new EgresoException( e );
			}

			detalleRetenciones.add( detalle );
		}

		return detalleRetenciones;
	}

	public static Amortizacion getAmortizacion( Connection conn, EgresoEncabezado egresoEncabezado ) throws Exception {

		String tableName = egresoEncabezado.getTablaEncabezado();
		String aliasTable = egresoEncabezado.getTipoPago().toUpperCase().replace( "PAGO", "" );
		String idField = "nFolio" + egresoEncabezado.getTipoPago();
		int field = egresoEncabezado.getFolioPago();
		StringBuilder sb = new StringBuilder();
		sb.append( "SELECT  " ).append( aliasTable ).append( ".mAmortizacionAnticipo, " );
		sb.append( "        contratoAnticipo.nPorcAsignacion, " );
		sb.append( "        contratoAnticipo.mTotalAnticipo " );
		sb.append( "  FROM  " ).append( tableName ).append( " " ).append( aliasTable ).append( " WITH(nolock) " );
		sb.append( "        INNER JOIN " );
		sb.append( "        v_ContratoAnticipo contratoAnticipo WITH(NOLOCK) " );
		sb.append( "        ON " );
		sb.append( aliasTable ).append( ".cFolio" ).append( egresoEncabezado.getTipoPago() ).append( " = contratoAnticipo.cIdContrato " );
		sb.append( "        AND  " );
		sb.append( "        contratoAnticipo.cTipo = '" ).append( aliasTable ).append( "' " );
		sb.append( " WHERE  " ).append( idField ).append( " = ? " );

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement( sb.toString() );
			ps.setInt( 1, field );
			rs = ps.executeQuery();

			if ( rs.next() ) {
				Amortizacion amortizacion = new Amortizacion();
				amortizacion.setFolioPago( field );
				amortizacion.setMontoAnticipo( rs.getBigDecimal( "mTotalAnticipo" ) );
				amortizacion.setMontoAmortizacion( rs.getBigDecimal( "mAmortizacionAnticipo" ) );
				amortizacion.setPorcentajeAmortizacion( rs.getBigDecimal( "nPorcAsignacion" ) );
				amortizacion.setTipoPago( egresoEncabezado.getTipoPago() );
				return amortizacion;
			} else
				return null;
		} finally {
			CloseObject.closeObject( rs );
			CloseObject.closeObject( ps );
		}

	}

	private static int getFolioApartado( Connection conn, String tipoPago, int folioPago ) throws Exception {
		StringBuilder query = new StringBuilder();
		query.append( "SELECT nFolioPagoApartado " );
		query.append( "  FROM tPagoApartadoEncabezado (NOLOCK) " );
		query.append( " WHERE cTipoPago = ? " );
		query.append( "   AND nFolioPago = ?" );

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement( query.toString() );
			ps.setString( 1, tipoPago );
			ps.setInt( 2, folioPago );

			rs = ps.executeQuery();
			if ( rs.next() ) {
				return rs.getInt( 1 );
			} else
				return -1;
		} finally {
			CloseObject.closeObject( rs );
			CloseObject.closeObject( ps );
		}
	}

	public static int getNumClavesDetalle( Connection conn, EgresoEncabezado egresoEncabezado ) throws Exception {
		StringBuilder sbTabla = new StringBuilder( "t" ).append( egresoEncabezado.getTipoPago() ).append( "Detalle" );
		StringBuilder sbCampo = new StringBuilder( "nFolio" ).append( egresoEncabezado.getTipoPago() );

		StringBuilder query = new StringBuilder();
		query.append( "SELECT COUNT(*) AS totClaves " );
		query.append( "  FROM " );
		query.append( "       (SELECT DISTINCT ep " );
		query.append( "          FROM " ).append( sbTabla ).append( " WITH(NOLOCK) " );
		query.append( "        WHERE " ).append( sbCampo ).append( " = ?" );
		query.append( "        ) AS claves" );

		PreparedStatement ps = null;
		ResultSet rs = null;
		int totClaves = 0;

		try {
			ps = conn.prepareStatement( query.toString() );
			ps.setInt( 1, egresoEncabezado.getFolioPago() );
			rs = ps.executeQuery();
			if ( rs.next() ) {
				totClaves = rs.getInt( 1 );
			}
			return totClaves;
		} finally {
			CloseObject.closeObject( rs );
			CloseObject.closeObject( ps );
		}
	}

	public static int getTotalEP( Connection conn, String detailTable, String field, int folio ) throws EgresoException {
		String query = "SELECT COUNT(DISTINCT EP) totEP FROM " + detailTable + " WITH(NOLOCK) WHERE " + field + " = ?";
		int totalEP = 0;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement( query );
			ps.setInt( 1, folio );

			rs = ps.executeQuery();
			if ( rs.next() ) {
				totalEP = rs.getInt( 1 );
			}

			return totalEP;
		} catch ( SQLException e ) {
			throw new EgresoException( e );
		} finally {
			CloseObject.closeObject( rs );
			CloseObject.closeObject( ps );
		}
	}

	public static int getTotalInvoices( Connection conn, String documentType, int folio ) throws EgresoException {
		StringBuilder query = new StringBuilder( "SELECT COUNT(DISTINCT cfactura) totFacturas FROM tPagoFactura WITH(NOLOCK) WHERE cTipoPago = ? AND nFolioPago = ?" );
		int totalInvoices = 0;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement( query.toString() );
			ps.setString( 1, documentType );
			ps.setInt( 2, folio );

			rs = ps.executeQuery();
			if ( rs.next() ) {
				totalInvoices = rs.getInt( 1 );
			}

			return totalInvoices;
		} catch ( SQLException e ) {
			throw new EgresoException( e );
		} finally {
			CloseObject.closeObject( rs );
			CloseObject.closeObject( ps );
		}
	}

	public static int getJustification( Connection conn, int folio ) throws EgresoException {
		int justify = 0;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement( "SELECT COUNT(*) from tJustificacionRG WITH (NOLOCK) WHERE nFolioRELACIONGASTOS = ?" );
			ps.setInt( 1, folio );

			rs = ps.executeQuery();
			if ( rs.next() ) {
				justify = rs.getInt( 1 );
			}

			return justify;
		} catch ( SQLException e ) {
			throw new EgresoException( e );
		} finally {
			CloseObject.closeObject( rs );
			CloseObject.closeObject( ps );
		}
	}

	public static int insertaPagoApartado( Connection conn, EgresoEncabezado egresoEncabezado ) throws Exception {
		StringBuilder query = new StringBuilder();
		query.append( "INSERT INTO tpagoapartadoencabezado(nFolioPagoApartado, cTipoPago, nFolioPago, fAplicacion," );
		query.append( "                                    cRamo, caNoContrarrecibo, aEjercicioFiscal, cTipoPoliza," );
		query.append( "                                    cDescripcionPoliza)" );
		query.append( "VALUES(?, ?, ?, GETDATE(), ?, ?, ?, ?, ?)" );

		PreparedStatement ps = null;

		try {

			int i = 1;
			int folioApartado = CFSequenceManager.getInstance( GestionInterface.ATT_CONEXION ).nextVal( "APARTADO" );
			ps = conn.prepareStatement( query.toString() );
			ps.setInt( i++, folioApartado );
			ps.setString( i++, egresoEncabezado.getTipoPago() );
			ps.setInt( i++, egresoEncabezado.getFolioPago() );
			ps.setString( i++, egresoEncabezado.getRamo() );
			ps.setString( i++, egresoEncabezado.getContrarecibo() );
			ps.setString( i++, egresoEncabezado.getEjercicioFiscal() );
			ps.setString( i++, "PR" );
			ps.setString( i++, "Apartado del pago: " + egresoEncabezado.getContrarecibo() );

			ps.executeUpdate();
			insertaPagoApartadoDetalle( conn, egresoEncabezado, folioApartado );
			return folioApartado;
		} finally {
			CloseObject.closeObject( ps );
		}

	}

	private static void insertaPagoApartadoDetalle( Connection conn, EgresoEncabezado egresoEncabezado, int folioApartado ) throws Exception {
		StringBuilder query = new StringBuilder();
		query.append( "INSERT INTO tPagoApartadoDetalle( nFolioPagoApartado, nDocRenglon, cMes, cEvento, cEjercicio," );
		query.append( "                                 cCentroContable, EP, mImporte ) " );
		query.append( "VALUES( ?, ?, ?, ?, ?, ?, ?, ? ) " );
		PreparedStatement ps = null;

		try {
			List<EgresoCalendario> calendario = cargaCalendarioEgreso( conn, egresoEncabezado.getTipoPago(), egresoEncabezado.getFolioPago() );
			int nRenglon = 1;
			int index = 1;
			ps = conn.prepareStatement( query.toString() );

			for ( EgresoCalendario mesEgreso : calendario ) {
				ps.setInt( index++, folioApartado );
				ps.setInt( index++, nRenglon++ );
				ps.setInt( index++, mesEgreso.getMesPresupuesto() );
				ps.setString( index++, "APARTADO" );
				ps.setString( index++, egresoEncabezado.getEjercicioFiscal() );
				ps.setString( index++, egresoEncabezado.getCentroContable() );
				ps.setString( index++, mesEgreso.getEp() );
				ps.setBigDecimal( index++, mesEgreso.getImporteBruto() );

				ps.executeUpdate();
				ps.clearParameters();
				index = 1;
			}
		} finally {
			CloseObject.closeObject( ps );
		}
	}

	public static boolean isApartadoAplicado( Connection conn, String tipoPago, int folioPago ) throws Exception {
		StringBuilder query = new StringBuilder();
		query.append( "SELECT cDocumentoHaplicado " );
		query.append( "  FROM tPagoApartadoEncabezado (NOLOCK) " );
		query.append( " WHERE cTipoPago = ? " );
		query.append( "   AND nFolioPago = ?" );

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement( query.toString() );
			ps.setString( 1, tipoPago );
			ps.setInt( 2, folioPago );

			rs = ps.executeQuery();
			if ( rs.next() ) {
				return "S".equalsIgnoreCase( rs.getString( 1 ) );
			} else
				return false;
		} finally {
			CloseObject.closeObject( rs );
			CloseObject.closeObject( ps );
		}
	}

	public static void isAplica15D( Connection conn, EgresoEncabezado encabezado ) throws SQLException {
		StringBuilder query = new StringBuilder();
		query.append( "SELECT CASE " );
		query.append( "         WHEN Isnull(caplica15d, 'N') = 'S' THEN 'true' " );
		query.append( "         ELSE 'false' " );
		query.append( "       END AS cAplica15D " );
		query.append( "FROM   tcuestionariopagosfiel WITH(nolock) " );
		query.append( "WHERE  ctipopago = ?" );
		query.append( "       AND nfoliopago = ?" );
		Boolean aplica15D = new Boolean( false );
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement( query.toString() );
			ps.setString( 1, encabezado.getTipoPago() );
			ps.setInt( 2, encabezado.getFolioPago() );

			rs = ps.executeQuery();
			if ( rs.next() )
				aplica15D = Boolean.parseBoolean( rs.getString( 1 ) );
			encabezado.setAplica15D( aplica15D );

		} finally {
			CloseObject.closeObject( rs );
			CloseObject.closeObject( ps );
		}

	}

	public static List<String> seleccionaClavesPago( Connection conn, String tipoPago, int folioPago ) throws Exception {

		StringBuilder sb = new StringBuilder();
		sb.append( "SELECT  DISTINCT EP  " );
		sb.append( "  FROM  tPagoCalendario WITH(NOLOCK) " );
		sb.append( " WHERE  cTipoPago = ? " );
		sb.append( "   AND  nFolioPago = ?" );

		PreparedStatement ps = null;
		ResultSet rs = null;
		List<String> epList = new ArrayList<String>();
		try {
			ps = conn.prepareStatement( sb.toString() );
			ps.setString( 1, tipoPago );
			ps.setInt( 2, folioPago );
			rs = ps.executeQuery();
			while ( rs.next() ) {
				epList.add( rs.getString( "EP" ) );
			}

			if ( epList.size() == 0 )
				throw new Exception( String.format( "No se encontraron claves en tpagocalendario para la combinacion [%s][%d]", tipoPago, folioPago ) );
			return epList;
		} finally {
			CloseObject.closeObject( rs );
			CloseObject.closeObject( ps );
		}

	}

	public static void validaRetencionRegimenRESICO( Connection conn, String tipoPago, int folioPago ) throws RelacionRegimenRetencionException, SQLException {
		StringBuilder query = new StringBuilder();
		query.append( "SELECT cFactura AS UUID, " );
		query.append( "	     cregimenfiscal AS idRegimen " );
		query.append( "FROM   v_pagos_retencion_regimen " );
		query.append( "WHERE  cidtiporetencion = 18 " );
		query.append( "       AND cregimenfiscal <> '626' " );
		query.append( "       AND ctipopago = ?" );
		query.append( "       AND nfoliopago = ?  " );

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement( query.toString() );
			ps.setString( 1, tipoPago );
			ps.setInt( 2, folioPago );

			rs = ps.executeQuery();

			if ( rs.next() ) {

				throw new RelacionRegimenRetencionException( "La factura : " + rs.getString( 1 ) + " pertence a un regimen: " + rs.getString( 2 ) + "  que es diferente al esperado (626)" );

			}
		} finally {
			CloseObject.closeObject( ps );
			CloseObject.closeObject( rs );
		}
	}

	public static boolean esPagoPROFOEM( Connection conn, String documento, int folioPago ) throws SQLException {
		StringBuilder query = new StringBuilder();
		query.append( "SELECT  COUNT( * ) AS EXISTE\r\n" + "  FROM  t" ).append( documento )
		.append( "detalle WITH(NOLOCK)\r\n" + " WHERE  substring(EP, 57,7) IN (\r\n"
				+ "        SELECT clave_ue_un \r\n" 
				+ "          FROM tCombinacionAutorizaProfoem with(nolock) \r\n" 
				+ "        )\r\n" 
				+ "   AND  nFolio" + documento + " = ?" );
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			log.debug( "Ejecutando: " + query );
			ps = conn.prepareStatement( query.toString() );
			ps.setInt( 1, folioPago );

			rs = ps.executeQuery();

			return rs.next() && rs.getInt( "EXISTE" ) > 0;
		} finally {
			CloseObject.closeObject( ps );
			CloseObject.closeObject( rs );
		}
	}

}
