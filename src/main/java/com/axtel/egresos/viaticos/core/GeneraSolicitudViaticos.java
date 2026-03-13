package com.axtel.egresos.viaticos.core;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.axtel.egresos.viaticos.Agenda;
import com.axtel.egresos.viaticos.Comision;
import com.axtel.egresos.viaticos.ViaticoRequest;
import com.axtel.egresos.viaticos.ViaticoResponse;
import com.axtel.egresos.viaticos.ViaticosException;
import com.axtel.ws.clients.ViaticoClient;
import com.syc.cfdi.utils.CloseObject;
import com.syc.contable.AccountingEngine;
import com.syc.contable.core.CalculaImpuestosRetencionesManager;
import com.syc.contable.core.EgresoImpuestos;
import com.syc.contable.core.RelacionGastosManager;
import com.syc.egresos.core.ContrareciboManager;
import com.syc.egresos.core.EgresoEncabezado;
import com.syc.egresos.core.EgresoRetencion;
import com.syc.egresos.core.RelacionGastosDetalle;
import com.syc.egresos.core.impl.EgresoRELACIONGASTOSEncabezado;
import com.syc.ejercido.pagado.core.EgresoCalendario;
import com.syc.ejercido.pagado.core.EgresoCalendarioRG;
import com.syc.ejercido.pagado.core.EgresosManager;
import com.syc.fortimax.core.Aplicacion;
import com.syc.fortimax.core.AplicacionManager;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.core.AlarmaManager;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoDatoManager;
import com.syc.gestion.core.CasoManager;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.custom.DefaultFolioGenerator;
import com.syc.gestion.custom.FolioGeneratorInterface;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.obrapublica.ConfiguraAplicativoBusinessLogic;
import com.syc.obrapublica.EjercicioFiscalManager;
import com.syc.obrapublica.core.ConfiguraAplicativoManager;
import com.syc.sai.contabilidad.utils.db.RSToTable;
import com.syc.sai.procesos.CambiaEPRelacionGastos;
import com.syc.utils.mail.MailSender;


public class GeneraSolicitudViaticos {

	private static final int	idTC_Caja					= 42;
	private static final int	idTC_ComisionSinViaticos	= 60;
	private static final int	idTC_RG						= 11;
	private static final Logger	log							= Logger.getLogger( GeneraSolicitudViaticos.class );
	private static String		ejercicioFiscal				= "";
	private static String		cMes						= "0";

	public static int insertComisionViaticos( Connection conn, Agenda agenda, String nombreComision ) throws Exception {
		PreparedStatement pst = null;

		String query = "INSERT INTO tViaticosComisiones (fInicio,fFin,cConcepto_Comision,nIdPais,ID_ESTADO,ID_MUNICIPIO,cLocalidad,cEstatus ) " + " VALUES (?,?,?,?,?,?,?,'A') ";
		try {
			java.sql.Date fechaInicio = new java.sql.Date( agenda.getFechaInicio().getTime() );
			java.sql.Date fechaFin = new java.sql.Date( agenda.getFechaFin().getTime() );

			pst = conn.prepareStatement( query );
			pst.setDate( 1, fechaInicio );
			pst.setDate( 2, fechaFin );
			pst.setString( 3, nombreComision );
			pst.setInt( 4, agenda.getIdPais() );
			pst.setInt( 5, agenda.getIdEstado() );
			pst.setInt( 6, agenda.getIdMunicipio() );
			pst.setString( 7, agenda.getLocalidad() );

			int retorno = pst.executeUpdate();

			if ( retorno > 0 )
				return lastID( conn );
			else
				return retorno;

		} finally {
			CloseObject.closeObject( pst, false );
		}
	}

	public static void actualizarComisionViaticos( Connection conn, Agenda agenda, String nombreComision, int idComision ) throws Exception {
		PreparedStatement pst = null;
		String query = "";

		try {
			java.sql.Date fechaInicio = new java.sql.Date( agenda.getFechaInicio().getTime() );
			java.sql.Date fechaFin = new java.sql.Date( agenda.getFechaFin().getTime() );

			if ( nombreComision == null || nombreComision.equals( "" ) ) {
				query = "UPDATE tViaticosComisiones SET fInicio = ?, fFin = ?,  nIdPais =?, ID_ESTADO = ? , ID_MUNICIPIO = ?, cLocalidad = ? WHERE  nIdComision = ? ";

				pst = conn.prepareStatement( query );
				pst.setDate( 1, fechaInicio );
				pst.setDate( 2, fechaFin );
				pst.setInt( 3, agenda.getIdPais() );
				pst.setInt( 4, agenda.getIdEstado() );
				pst.setInt( 5, agenda.getIdMunicipio() );
				pst.setString( 6, agenda.getLocalidad() );
				pst.setInt( 7, idComision );

				pst.executeUpdate();

			} else {
				query = "UPDATE tViaticosComisiones SET fInicio = ?, fFin = ?, cConcepto_Comision = ?, nIdPais =?, ID_ESTADO = ? , ID_MUNICIPIO = ?, cLocalidad = ? WHERE  nIdComision = ? ";

				pst = conn.prepareStatement( query );
				pst.setDate( 1, fechaInicio );
				pst.setDate( 2, fechaFin );
				pst.setString( 3, nombreComision );
				pst.setInt( 4, agenda.getIdPais() );
				pst.setInt( 5, agenda.getIdEstado() );
				pst.setInt( 6, agenda.getIdMunicipio() );
				pst.setString( 7, agenda.getLocalidad() );
				pst.setInt( 8, idComision );

				pst.executeUpdate();
			}
		} finally {

			CloseObject.closeObject( pst, false );
		}
	}

	public static int lastID( Connection conn ) throws Exception {
		int id = 0;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = conn.prepareStatement( "SELECT MAX(NIDCOMISION) nextCom FROM tViaticosComisiones WITH (NOLOCK)" );
			rs = pst.executeQuery();

			if ( rs.next() ) {
				id = rs.getInt( 1 );
			}
		} finally {
			CloseObject.closeObject( rs, false );
			CloseObject.closeObject( pst, false );
		}

		return id;
	}

	public static Caso generaRelacionGastos( Connection conn, Comision comision, Agenda agenda, Usuario u, String reportPath, int folioPago, BigDecimal retencion, String ejercicio, EgresoEncabezado encabezado, int idComisionViaticos, int folioCaja ) throws Exception {
		String cc = StringUtils.trimToNull( u.getPropiedad( "CCENTROCONTABLE" ).getValor() );
		if ( cc == null )
			throw new Exception( "Se recibio CC nulo. No se puede generar CxP" );

		Caso c = new Caso();
		ejercicioFiscal = ejercicio;
		String tipoTramite = encabezado.getTipoPago();
		BigDecimal total = ( comision.getTotalAgenda().add( comision.getTotalTransporte() ) );

		String cxpPrefijo = getPrefijoCR( conn );
		String contrarecibo = ContrareciboManager.generaContrarecibo( conn, cc, cxpPrefijo );

		c = consultaCaso( conn, folioPago, c );
		c = CasoManager.select( conn, c );
		String[] extraerUR = c.getFolio().split("-");
		String urCaso = extraerUR[1];
		
		if  (encabezado.getUnidadResponsable() != urCaso) {
			encabezado.setUnidadResponsable( urCaso );
		}

		boolean tieneEncabezado = consultaExisteEncabezado( conn, folioPago );
		if ( !tieneEncabezado ) {
			agregarDatos( conn, comision, encabezado, folioCaja );
			encabezado.setContrarecibo( contrarecibo );
			( ( EgresoRELACIONGASTOSEncabezado ) encabezado ).setIdComision( idComisionViaticos );

			encabezado.save( conn );
			conn.commit();
		} else {
			agregarDatos( conn, comision, encabezado, folioCaja );
			encabezado.setContrarecibo( contrarecibo );
			( ( EgresoRELACIONGASTOSEncabezado ) encabezado ).setIdComision( idComisionViaticos );
		}

		int cuantasRet = tieneOtrasRetenciones( conn, tipoTramite, folioPago );

		
		List<EgresoImpuestos> impuestos = EgresosManager.cargaImpuestos( conn, tipoTramite, folioPago );
		if ( impuestos == null ) {
			impuestos = new ArrayList<>();
		}
		List<EgresoRetencion> retenciones = null;

		if ( cuantasRet == 0 ) {
			List<EgresoCalendarioRG> calendarioPago = EgresosManager.cargaCalendarioEgresoRG( conn, tipoTramite, folioPago );
			//retenciones = cargaRetencionesResico( conn, tipoTramite, folioPago );
			/*Modificar el calculo de impuesto de retenciones*/
			CalculaImpuestosRetencionesManager.calculaImpuestosRetencionesRG( conn, encabezado, calendarioPago, impuestos );
		} else {
			List<EgresoCalendario> calendarioPago = EgresosManager.cargaCalendarioEgreso( conn, tipoTramite, folioPago );
			retenciones = EgresosManager.cargaRetenciones( conn, tipoTramite, folioPago );
			CalculaImpuestosRetencionesManager.calculaImpuestosRetenciones( conn, encabezado, calendarioPago, retenciones, impuestos );
		}

		String eventoD = ComisionDAO.consultaEventoViaticos( conn, "RELACIONGASTOS", "D" );

		if ( eventoD.equals( comision.getEvento() ) ) {
			InsertEdoCtaCompDetalle( conn, folioCaja, folioPago, total );
			ActualizaEdoCtaViaticos( conn, total, folioCaja );
		}

		if (validaRetencionEncabezadoDetalle(conn, folioPago)) {
			if (validaEncabezadoVsDetalle(conn, folioPago) ) {
				int nFolioPagoApartado = RelacionGastosManager.insertaPagoApartado( conn, encabezado );
		
				//Validar si tiene justificacion de boletos para despues agregarlos.
				if (TransporteDAO.tieneJustificacionBoletos (conn, folioPago) ) {
					// Guardar boletos de avion en tinfoboleto
					ActualizaBoletosPagados( conn, comision.getIdComision(), folioPago );
					TransporteDAO.actualizarLayout( conn, comision.getIdComision(), "RG", folioPago );
					insertaInfoBoleto( conn, comision.getIdComision(), folioPago );
				}
				
				CambiaEPRelacionGastos.aplicaApartado( conn, nFolioPagoApartado );
				EgresosManager.aplicaTramite( conn, encabezado );
				CambiaEPRelacionGastos.avanzaCasoConsulta( conn, c, u );
			
			} else {
				throw new Exception( "El monto a ejercer de la solicitud en encabezado y el detalle es incorrecto, revise lo agregado en las Claves presupuestales." );
			}
		} else {
			throw new Exception( "El importe de las retenciones del encabezado y el detalle es incorrecto, favor de volver a intentar o contactar al administrador." );
		}
			
		return c;
	}

	private static boolean validaRetencionEncabezadoDetalle( Connection conn, int folioPago ) throws Exception {
		boolean esigual = false;
		PreparedStatement pst = null;
		ResultSet rs = null;
		StringBuilder query = new StringBuilder();
		
		try {
			query.append( "SELECT  mImporteRetencion -SUM( mPenalizacion + m2Millar+ mISRHonorarios+ mObra5 + mImporteFlete4 + mISRArrenda + mRetImpuestoCedular + mImporteIvaArrenda + mImporteIvaHonorarios + mImporteFlete23 + mImporteObra + mImporteISRLaudos + mImporteIva6+ mimporteISRResico) diferencia" );
			query.append( " FROM tRELACIONGASTOSEncabezado rg WITH (NOLOCK) " );
			query.append( " INNER JOIN tRELACIONGASTOSDetalle RGDET WITH (NOLOCK) " );
			query.append( " ON rg.nFolioRELACIONGASTOS = RGDET.nFolioRELACIONGASTOS	" );
			query.append( " WHERE RG.nFolioRELACIONGASTOS = ? " );
			query.append( " GROUP BY mImporteRetencion " );
			
			pst = conn.prepareStatement( query.toString() );
			pst.setInt( 1, folioPago );
			
			rs = pst.executeQuery();
			
			if (rs.next()) {
				double diferencia = rs.getDouble( 1 );
				
				if (diferencia == 0) {
					esigual = true;
				} 
			}
			
		} finally {
			
			CloseObject.closeObject( pst, false );
			CloseObject.closeObject( rs, false );
		}
				
		return esigual;
	}

	private static boolean validaEncabezadoVsDetalle( Connection conn, int folioPago ) throws Exception {
		boolean esigual = false;
		PreparedStatement pst = null;
		ResultSet rs = null;
		StringBuilder query = new StringBuilder();
		
		try {
			query.append( " SELECT rg.mImporteMasIva + rg.mImporteNeto - (SUM(RGDET.mImporteNeto ) + SUM(rgdet.mImporteMasIva)) diferencia " );
			query.append( " FROM tRELACIONGASTOSEncabezado rg WITH (NOLOCK) " );
			query.append( " INNER JOIN tRELACIONGASTOSDetalle RGDET WITH (NOLOCK) " );
			query.append( " ON rg.nFolioRELACIONGASTOS = RGDET.nFolioRELACIONGASTOS	" );
			query.append( " WHERE RG.nFolioRELACIONGASTOS = ? " );
			query.append( " GROUP BY mImporteRetencion , rg.mImporteMasIva , rg.mImporteNeto " );
			
			pst = conn.prepareStatement( query.toString() );
			pst.setInt( 1, folioPago );
			
			rs = pst.executeQuery();
			
			if (rs.next()) {
				double diferencia = rs.getDouble( 1 );
				
				if (diferencia == 0) {
					esigual = true;
				} 
			}
			
		} finally {
			
			CloseObject.closeObject( pst, false );
			CloseObject.closeObject( rs, false );
		}
				
		return esigual;
	}

	public static Caso generaSolicitudCaja( Connection conn, Comision comision, Agenda agenda, Usuario u, int idComision, int firmanteElab, int firmanteVobo, int firmanteAut, String esFiel, String reportPath, int folioPago, String informeComision, BigDecimal retencion, BigDecimal neto, String ejercicioFiscal, int folioCaja ) throws Exception {
		Caso c = new Caso();
		String tipoTramite = "CAJA";
		c = generaCaso( conn, u, idTC_Caja, ejercicioFiscal, comision );
		int folio = Util.folio( c );
		int mes = Util.getCurrentMonth( conn );
		BigDecimal total = ( comision.getTotalAgenda().add( comision.getTotalTransporte() ) );
		String cuentaRFC = ConsultaCuentaBancariaRFC( conn, comision );
		String tipoPoliza = "EG";

		if ( "8_2_2".equals( comision.getEvento() ) ) {
			tipoPoliza = "IN";
			int aplicado = ValidaAnticipoAplicado( conn, folioCaja );
			if ( aplicado == 0 ) {
				throw new Exception( "El anticipo con folio: " + folioCaja + " no ha sido aplicado, por lo que no puede generarse aún la devolución." );
			}
		}

		// Insertar en caja encabezado y detalle
		InsertCajaEncabezado( conn, comision, folio, mes, c.getIdCaso(), agenda.getMotivoComision(), ejercicioFiscal, firmanteElab, firmanteVobo, firmanteAut, esFiel, folioCaja, tipoPoliza );

		ComisionDAO.insertaComprobacionComision( conn, comision, folio, tipoTramite );
		InsertCajaDetalle( conn, comision, folio, cuentaRFC, total, comision.getEvento() );
		InsertRelacionComprobacion( conn, folio, tipoTramite, idComision, total, comision, cuentaRFC );

		return c;

	}

	private static int ValidaAnticipoAplicado( Connection conn, int folioCaja ) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		int aplicado = 0;
		try {
			pst = conn.prepareStatement( "SELECT COUNT(*) FROM tcajaencabezado WITH (NOLOCK) WHERE nFoliocaja = ?" );
			pst.setInt( 1, folioCaja );

			rs = pst.executeQuery();

			if ( rs.next() ) {
				aplicado = rs.getInt( 1 );
			}

			return aplicado;

		} finally {
			CloseObject.closeObject( rs, false );
			CloseObject.closeObject( pst, false );
		}

	}

	public static Caso generaComisionSinViaticos( Connection conn, Comision comision, Agenda agenda, Usuario u, int idComision, int firmanteElab, int firmanteVobo, int firmanteAut, String esFiel, String reportPath, int folioPago, String ejercicioFiscal ) throws Exception {

		Caso c = new Caso();
		int idTC = idTC_ComisionSinViaticos;
		String tipoTramite = "COMSINVIATICOS";
		c = generaCaso( conn, u, idTC, ejercicioFiscal, comision );
		int folio = Util.folio( c );
		BigDecimal total = new BigDecimal( "0.00" );
		String cuentaRFC = ConsultaCuentaBancariaRFC( conn, comision );

		InsertComisionSinViaticosEnc( conn, comision, folio, idComision, total, agenda.getActividades(), firmanteElab, firmanteVobo, firmanteAut, esFiel, u.getLogin() );
		InsertComisionSinViaticosDet( conn, folio, comision.getIdComision() );

		InsertRelacionComprobacion( conn, folio, tipoTramite, idComision, total, comision, cuentaRFC );

		TransporteDAO.actualizaEstatusFolioPago( conn, comision.getIdComision(), folio );
		TransporteDAO.actualizarLayout( conn, comision.getIdComision(), "S", folioPago );
		
		TransporteDAO.insertarRelacionTaxi( conn,  comision.getIdComision(), folio, tipoTramite );
		TransporteDAO.actualizarLayoutTaxis( conn, comision.getIdComision(), 1 );

		// conn.commit();
		return c;

	}

	private static int tieneOtrasRetenciones( Connection conn, String tipoTramite, int folioPago ) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		int cuantos = 0;
		try {
			pst = conn.prepareStatement( "SELECT COUNT(*) FROM tPagoRetencion WITH (NOLOCK) WHERE cTipoDocumento = ? AND nFolioPago = ?" );
			pst.setString( 1, tipoTramite );
			pst.setInt( 2, folioPago );
			rs = pst.executeQuery();

			if ( rs.next() ) {
				cuantos = rs.getInt( 1 );
			}

		} finally {
			CloseObject.closeObject( rs, false );
			CloseObject.closeObject( pst, false );
		}
		return cuantos;
	}

	public static String getPrefijoCR( Connection conn ) throws Exception {

		String cxpPrefijo = ConfiguraAplicativoManager.getSystemSetting( conn, "CXP_PREFIJO" );
		return cxpPrefijo;
	}

	public static RelacionGastosDetalle renglonNuevo() {

		RelacionGastosDetalle renglonNuevo = new RelacionGastosDetalle();
		renglonNuevo.setAltaAlmacen( "0||" );
		renglonNuevo.setPeriodo13( 'N' );
		renglonNuevo.setADEFAS( 'N' );
		renglonNuevo.setCTAB( "N/A" );
		renglonNuevo.setcIdEntidadContable( "00" );
		renglonNuevo.setcIdCuentaContable( "" );
		renglonNuevo.setALM( "" );
		renglonNuevo.setM23IVA( "0" );
		renglonNuevo.setM2Millar( "0.00" );
		renglonNuevo.setM5Millar( "0" );
		renglonNuevo.setmAmortizacionAnticipo( "0" );
		renglonNuevo.setmCedular( "0" );
		renglonNuevo.setmFletes( "0" );
		renglonNuevo.setmImporteFlete23( new BigDecimal( "0.00" ) );
		renglonNuevo.setmImporteFlete4( "0.00" );
		renglonNuevo.setmImporteISRLaudos( new BigDecimal( "0.00" ) );
		renglonNuevo.setmImporteIvaArrenda( new BigDecimal( "0.00" ) );
		renglonNuevo.setmImporteIvaHonorarios( new BigDecimal( "0.00" ) );
		renglonNuevo.setmImporteIvaProv( new BigDecimal( "0.00" ) );
		renglonNuevo.setmImporteObra( new BigDecimal( "0.00" ) );
		renglonNuevo.setmRetImpuestoCedular( "0.00" );
		renglonNuevo.setmImporteIva( new BigDecimal( "0.00" ) );
		renglonNuevo.setmSancion( "0" );
		renglonNuevo.setmDevolucion( "0" );
		renglonNuevo.setmImporteAmortiza( "0" );
		renglonNuevo.setmRetencion( "0" );
		renglonNuevo.setmPenalizacion( "0.00" );
		renglonNuevo.setmObra5( "0.00" );
		renglonNuevo.setmISRHonorarios( "0.00" );
		renglonNuevo.setmISRArrenda( "0.00" );
		renglonNuevo.setmBruto( "0" );
		renglonNuevo.setmIVA( "0" );
		renglonNuevo.setmNeto( "0" );
		renglonNuevo.setmCNIC( new BigDecimal( "0.00" ) );
		renglonNuevo.setmIMDT( new BigDecimal( "0.00" ) );
		renglonNuevo.setmTesofe( new BigDecimal( "0.00" ) );

		return renglonNuevo;
	}

	private static Caso consultaCaso( Connection conn, int folio, Caso c ) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = conn.prepareStatement( "SELECT ID_CASO, ID_TC FROM CG_CASO WITH (NOLOCK) WHERE C_FOLIO like 'RELG%' + '-' + cast( ? AS VARCHAR(6))" );
			pst.setInt( 1, folio );

			rs = pst.executeQuery();

			if ( rs.next() ) {
				c.setIdCaso( rs.getInt( 1 ) );
				c.setIdTC( rs.getInt( 2 ) );
			}
		} finally {
			CloseObject.closeObject( rs, false );
			CloseObject.closeObject( pst, false );
		}

		return c;
	}

	private static Caso generaCaso( Connection conn, Usuario u, int idTC, String ejercicioFiscal, Comision comision ) throws Exception {
		FolioGeneratorInterface fg = new DefaultFolioGenerator();
		AccountingEngine ae = new AccountingEngine();
		ae.setValidaInsuficienciaDeSaldo( true );

		Caso c = CasoManager.nuevoCaso( conn, u, idTC, fg );

		c.getCasoDato( "FOLIO" ).setValor( c.getFolio() );
		c.getCasoDato( "FECHA_DOCUMENTO" ).setValor( Util.getTodayESMX() );
		c.getCasoDato( "EJERCICIO_FISCAL" ).setValor( ejercicioFiscal );
		c.getCasoDato( "OPERADOR" ).setValor( u.getNombre() );
		c.getCasoDato( "MONEDA" ).setValor( "MXP" );
		c.getCasoDato( "APLICADO_CONT" ).setValor( "true" );
		c.getCasoDato( "FECHA_AP_CONT" ).setValor( Util.getTodayESMX() );

		Map<String, String> datos = new HashMap<String, String>();
		datos.put( "FOLIO", c.getFolio() );
		datos.put( "FECHA_DOCUMENTO", Util.getTodayESMX() );
		datos.put( "EJERCICIO_FISCAL", ejercicioFiscal );
		datos.put( "OPERADOR", u.getNombre() );
		datos.put( "CONCEPTO_MOV", "Aplicación Relacion de Gastos" );
		datos.put( "MONEDA", "MXP" );
		datos.put( "FECHA_AP_CONT", Util.getTodayESMX() );
		datos.put( "APLICADO_CONT", "false" );

		Aplicacion app = AplicacionManager.select( conn, c.getTipoCaso().getGavetaAsociada() );
		CasoDatoManager.update( conn, c.getIdTC(), c.getIdCaso(), datos );
		int idGabinete = AplicacionManager.createExpediente( conn, comision.getUsuarioCaptura(), c, app );
		c.setIdGabinete( idGabinete );
		actualizarGabineteCaso( conn, c );

		return c;
	}

	private static void actualizarGabineteCaso( Connection conn, Caso c ) throws Exception {
		PreparedStatement pst = null;

		try {
			pst = conn.prepareStatement( "UPDATE CG_CASO set C_ID_GABINETE = ? where C_FOLIO = ?" );
			pst.setInt( 1, c.getIdGabinete() );
			pst.setString( 2, c.getFolio() );

			pst.executeUpdate();

		} finally {
			CloseObject.closeObject( pst, false );
		}

	}

	private static void ActualizaBoletosPagados( Connection conn, int idComision, int folioPago ) throws Exception {
		PreparedStatement pst = null;
		String query = "UPDATE tTransporteAereo SET cTienePago = 'S' where nidComision = ? and nfoliopago = ?";

		try {
			pst = conn.prepareStatement( query );
			pst.setInt( 1, idComision );
			pst.setInt( 2, folioPago );

			pst.executeUpdate();

		} finally {
			CloseObject.closeObject( pst, false );

		}

	}

	private static void insertaInfoBoleto( Connection conn, int idComision, int folio ) throws Exception {
		PreparedStatement pst = null;
		
		StringBuilder query = new StringBuilder();
		query.append( "INSERT INTO tInfoBoleto (nFolioRelacionGastos, cNumeroBoleto, mImporteBoleto, cPartida,	cRuta,	RFCVuelo, cNombreRFC, bEsVueloVigente) "); 
		query.append( " SELECT nFolioPago, cReferencia,	mImporteBoleto,	cPartida,	cRuta, RFCVuelo, cNombre, 0 ");
		query.append( " FROM tTransporteAereo WITH (NOLOCK) WHERE  nidComision = ? ");
		query.append( " AND ( nFolioPago NOT IN (SELECT nFolioRELACIONGASTOS FROM tRELACIONGASTOSEncabezado WITH (NOLOCK) WHERE cDocumentoHaplicado IN ( 'S', 'N', 'C' ) ) or nFolioPago is null ) ");

		try {
			pst = conn.prepareStatement( query.toString() );
			pst.setInt( 1, idComision );

			pst.executeUpdate();

		} finally {
			CloseObject.closeObject( pst, false );

		}

	}

	private static void InsertComisionSinViaticosEnc( Connection conn, Comision comision, int folio, int idComision, BigDecimal total, String concepto, int firmanteElab, int firmanteVobo, int firmanteAut, String esFiel, String uLogin ) throws Exception {
		PreparedStatement pst = null;
		String query = "INSERT INTO tComisionesSinComprobacionEnc ( nFolioComision,fInforme,nIdComision,RFC,cInformeComision,nAcompanantes,mImporteNeto, " + "cUnidadResponsable,lBoletoVigente,faplicacion,cEsFirmaElectronica, cIdUsuarioCaptura, nNumEmpleadoElab, nNumEmpleadoVoBo, nNumEmpleadoAut) " + "VALUES (?, GETDATE(), ?, ?, ?, 0, ?, ?, 'N', GETDATE(), ?, ?, ?, ?, ?) ";

		try {
			pst = conn.prepareStatement( query );
			pst.setInt( 1, folio );
			pst.setInt( 2, idComision );
			pst.setString( 3, comision.getRFC() );
			pst.setString( 4, concepto );
			pst.setBigDecimal( 5, total );
			pst.setString( 6, comision.getUnidadResponsable() );
			pst.setString( 7, esFiel );
			pst.setString( 8, uLogin );
			pst.setInt( 9, firmanteElab );
			pst.setInt( 10, firmanteVobo );
			pst.setInt( 11, firmanteAut );

			pst.executeUpdate();

			log.debug( query );

		} finally {
			CloseObject.closeObject( pst, false );

		}

	}

	private static void InsertComisionSinViaticosDet( Connection conn, int folio, int idComision ) throws Exception {
		PreparedStatement pst = null;
		String query = "INSERT INTO tComisionesSinComprobacionDet ( nFolioComision , cBoleto, mImporteBoleto, cPartida, cRuta, RFCVuelo, cNombreRFC) " + " SELECT ?,  cReferencia, mImporteBoleto, cPartida, cRuta, RFCVuelo, cNombre FROM tTransporteAereo WITH (NOLOCK) WHERE nidComision = ? AND cTienePago = 'N'";

		try {
			pst = conn.prepareStatement( query );
			pst.setInt( 1, folio );
			pst.setInt( 2, idComision );

			pst.executeUpdate();

			log.debug( query );

		} finally {
			CloseObject.closeObject( pst, false );

		}

	}

	private static void InsertRelacionComprobacion( Connection conn, int folio, String tramite, int idComision, BigDecimal total, Comision comision, String ctaBanco ) throws Exception {
		PreparedStatement pst = null;
		String query = "INSERT INTO tRelacionComprobacionComisiones (nFolioTramite,cTipoTramite,nIdComision,fAplicacion,cCentroContable,RFC,cCuentaBeneficiario,mMontoComision,mRemanenteComision, cStatus, U_LOGIN, nidComisionModulo ) " + "VALUES (?, ?, ?, GETDATE(), 10, ?, ?, ?, 0, 'A', ?, ?)";

		try {
			pst = conn.prepareStatement( query );
			pst.setInt( 1, folio );
			pst.setString( 2, tramite );
			pst.setInt( 3, idComision );
			pst.setString( 4, comision.getRFC() );
			pst.setString( 5, ctaBanco );
			pst.setBigDecimal( 6, total );
			pst.setString( 7, comision.getUsuarioCaptura() );
			pst.setInt( 8, comision.getIdComision() );

			pst.executeUpdate();

		} finally {
			CloseObject.closeObject( pst, false );

		}
	}

	private static void ActualizaEdoCtaViaticos( Connection conn, BigDecimal total, int folio ) throws Exception {
		PreparedStatement pst = null;

		try {
			pst = conn.prepareStatement( "UPDATE tEstadoDeCuentaViaticosEncabezado SET mMontoRemanente = mMontoRemanente - ? WHERE nFolioCaja = ?" );
			pst.setBigDecimal( 1, total );
			pst.setInt( 2, folio );

			log.debug( pst );
			pst.executeUpdate();

		} finally {
			CloseObject.closeObject( pst, false );

		}
	}

	public static void InsertEdoCtaCompDetalle( Connection conn, int folioOrigen, int folio, BigDecimal total ) throws Exception {
		PreparedStatement pst = null;

		try {
			pst = conn.prepareStatement( "INSERT INTO tEstadoDeCuentaViaticosDetalle (nFolioCaja, nFolioComprobacion, nDocRenglon, fFechaComprobacion, mMontoComprobacion) " + "VALUES (?, ?, (SELECT isnull(MAX(NDOCRENGLON),0) + 1 FROM tEstadoDeCuentaViaticosDetalle WITH (NOLOCK) WHERE nFolioCaja = ?), GETDATE(), ?)" );
			pst.setInt( 1, folioOrigen );
			pst.setInt( 2, folio );
			pst.setInt( 3, folio );
			pst.setBigDecimal( 4, total );

			log.debug( pst );
			pst.executeUpdate();

		} finally {
			CloseObject.closeObject( pst, false );

		}
	}

	private static String ConsultaCuentaBancariaRFC( Connection conn, Comision comision ) throws Exception {
		String cuenta = "";
		PreparedStatement pst = null;
		ResultSet rs = null;

		try {
			pst = conn.prepareStatement( "SELECT dCuentaBancaria FROM tBeneficiarioCuentasBancarias WITH (NOLOCK) WHERE dRFC = ? AND subCuentaBancaria = ? " );
			pst.setString( 1, comision.getRFC() );
			pst.setString( 2, comision.getCTAB() );

			rs = pst.executeQuery();

			if ( rs.next() ) {
				cuenta = rs.getString( 1 );
			}

		} finally {
			CloseObject.closeObject( rs, false );
			CloseObject.closeObject( pst, false );

		}

		return cuenta;
	}

	private static void InsertCajaEncabezado( Connection conn, Comision comision, int folio, int mes, int idCaso, String concepto, String ejercicioFiscal, int firmanteElab, int firmanteVobo, int firmanteAut, String esFiel, int folioCaja, String tipoPoliza ) throws Exception {
		PreparedStatement pst = null;
		String query = "INSERT INTO  tcajaencabezado ( nFoliocaja,fcreacion,faplicacion,cMes,cCentrocontable,ctipopoliza,cdescripcionpoliza,u_login, cunidadresponsablecontable" + ", aejerciciofiscal,cramo,cunidadejecutora, id_caso, nidgrupoevento, mMontoSolicitud,nFolioComprobacion,cEsFirmaElectronica, nNumEmpleadoElab, nNumEmpleadoVoBo, nNumEmpleadoAut) " + " VALUES ( ?, GETDATE(), GETDATE(),  ?, 10, ?, ?, ?, 'RHQ', ?, 16, ?, ?, 8 , ?, ?, ?, ?, ?, ?)";

		try {
			pst = conn.prepareStatement( query );
			pst.setInt( 1, folio );
			pst.setInt( 2, mes );
			pst.setString( 3, tipoPoliza );
			pst.setString( 4, concepto );
			pst.setString( 5, comision.getUsuarioCaptura() );
			pst.setString( 6, ejercicioFiscal );
			pst.setString( 7, comision.getUnidadResponsable() );
			pst.setInt( 8, idCaso );
			pst.setBigDecimal( 9, ( comision.getTotalAgenda().add( comision.getTotalTransporte() ) ) );
			pst.setInt( 10, folioCaja );
			pst.setString( 11, esFiel );
			pst.setInt( 12, firmanteElab );
			pst.setInt( 13, firmanteVobo );
			pst.setInt( 14, firmanteAut );

			pst.executeUpdate();

		} finally {
			CloseObject.closeObject( pst, false );
		}
	}

	private static void InsertCajaDetalle( Connection conn, Comision comision, int folio, String cuentaRFC, BigDecimal total, String evento ) throws Exception {
		PreparedStatement pst = null;
		String query = "INSERT INTO TCAJADETALLE (nFoliocaja,ndocRenglon,cEvento,mImporte,mImporteNegativo,CTAB,RFC,nCuentaBeneficiario,ffm,cUnidadResponsable) " + " VALUES (?,1,?, ?,?,?, ?, ?, 'NA', ?)";

		try {
			BigDecimal b1 = new BigDecimal( "-1" );

			pst = conn.prepareStatement( query );
			pst.setInt( 1, folio );
			pst.setString( 2, evento );
			pst.setBigDecimal( 3, total );
			pst.setBigDecimal( 4, total.multiply( b1 ) );
			pst.setString( 5, comision.getCuentaBancariaCNF() );
			pst.setString( 6, comision.getRFC() );
			pst.setString( 7, cuentaRFC );
			pst.setString( 8, comision.getUnidadResponsable() );

			pst.executeUpdate();

		} finally {
			CloseObject.closeObject( pst, false );
		}
	}

	public static boolean existenMasFirmantes( Connection conn, int folioComision ) throws Exception {
		boolean existe = false;
		PreparedStatement pst = null;
		ResultSet rs = null;
		String query = "SELECT COUNT(*) FROM tFirmantesViaticos WITH (NOLOCK) WHERE nFolio = ? AND cAutorizado = 'N' ";

		try {
			pst = conn.prepareStatement( query );
			pst.setInt( 1, folioComision );

			rs = pst.executeQuery();

			if ( rs.next() ) {
				int cuantos = rs.getInt( 1 );

				if ( cuantos > 1 )
					existe = true;
			}

		} finally {
			CloseObject.closeObject( pst, false );
			CloseObject.closeObject( rs, false );
		}

		return existe;
	}

	public static boolean actualizaEstatus( Connection conn, int folioComision ) throws Exception {
		boolean existe = false;
		PreparedStatement pst = null;

		String query = "UPDATE tComision SET nIdEstatus = (SELECT COUNT(*) FROM tFirmantesViaticos WITH (NOLOCK) WHERE nFolio = ? and cAutorizado = 'S')  WHERE nIdComision = ?";

		try {
			pst = conn.prepareStatement( query );
			pst.setInt( 1, folioComision );
			pst.setInt( 2, folioComision );

			int actualizado = pst.executeUpdate();

			if ( actualizado > 0 )
				existe = true;

		} finally {
			CloseObject.closeObject( pst, false );

		}

		return existe;
	}

	public static void enviarCorreoIni( Connection conn, int folio, int idEstatus, String tipoOper, int tieneBoleto, String justBoletos  ) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		String nombre = "", puesto = "", correo = "";
		int idEmpleado = 0;
		String query2 = "SELECT cNombre, cPuesto, d_email, nIdEmpleado FROM tFirmantesViaticos WITH (NOLOCK) WHERE nFolio = ? AND nRenglon = ?";

		try {

			pst = conn.prepareStatement( query2 );
			pst.setInt( 1, folio );
			pst.setInt( 2, idEstatus );
			rs = pst.executeQuery();

			if ( rs.next() ) {
				correo = rs.getString( "d_email" );
				nombre = rs.getString( "cNombre" );
				puesto = rs.getString( "cPuesto" );
				idEmpleado = rs.getInt( "nIdEmpleado" );
			} else {
				throw new Exception( "No existe firmante con turno: " + idEstatus + " en el folio " + folio + " Notificar al administrador." );
			}

			if (correo == null) {
				throw new Exception( "El empleado no tiene correo asignado. Notificar al administrador." );
			}
			
			String cuerpoCorreo = generaCuerpoCorreoAut( conn, folio, nombre, puesto, idEmpleado );

			if ( "EDITAR".equals( tipoOper ) ) {
				MailSender.enviaCorreoCNF( correo, cuerpoCorreo, "Solicitud de Autorización de Viáticos - Modificado" );

			} else {
				if (tieneBoleto >  0) {
					MailSender.enviaCorreoCNF( correo, cuerpoCorreo, "Solicitud de Autorización de Viáticos con boleto de avión" );
				} else 
					MailSender.enviaCorreoCNF( correo, cuerpoCorreo, "Solicitud de Autorización de Viáticos" );
			}

		} finally {

			CloseObject.closeObject( pst, false );
			CloseObject.closeObject( rs, false );

		}
	}

	public static void enviarCorreoAut( Connection conn, int folio, int idEmpleado ) throws Exception {
		PreparedStatement pst = null, pst2 = null;
		ResultSet rs = null, rs2 = null;
		String nombre = "", puesto = "", correo = "";
		int idRenglon = 0;
		String query = "SELECT nRenglon FROM tFirmantesViaticos WITH (NOLOCK) WHERE nFolio = ? AND nIdEmpleado = ?";
		String query2 = "SELECT cNombre, cPuesto, d_email, nIdEmpleado FROM tFirmantesViaticos WITH (NOLOCK) WHERE nFolio = ? AND nRenglon = ?";
		try {
			pst2 = conn.prepareStatement( query );
			pst2.setInt( 1, folio );
			pst2.setInt( 2, idEmpleado );
			rs2 = pst2.executeQuery();

			if ( rs2.next() ) {
				idRenglon = rs2.getInt( 1 );
			}

			pst = conn.prepareStatement( query2 );
			pst.setInt( 1, folio );
			pst.setInt( 2, idRenglon + 1 );
			rs = pst.executeQuery();

			if ( rs.next() ) {
				correo = rs.getString( "d_email" );
				nombre = rs.getString( "cNombre" );
				puesto = rs.getString( "cPuesto" );
				idEmpleado = rs.getInt( "nIdEmpleado" );
			}

			String cuerpoCorreo = generaCuerpoCorreoAut( conn, folio, nombre, puesto, idEmpleado );

			MailSender.enviaCorreoCNF( correo, cuerpoCorreo, "Solicitud de Autorización de Viáticos" );

		} finally {

			CloseObject.closeObject( pst, false );
			CloseObject.closeObject( rs, false );
			CloseObject.closeObject( pst2, false );
			CloseObject.closeObject( rs2, false );
		}
	}

	public static void enviarCorreo( Connection conn, int folio ) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		String nombre = "", puesto = "", correo = "";
		StringBuilder query = new StringBuilder();
		query.append( "SELECT U_NOMBRE cNombre, U_EMAIL , ISNULL( EMP.DESCRIPCION_PUESTO, '') cPuesto" );
		query.append( "		FROM tComision COM WITH (NOLOCK) " );
		query.append( "		INNER JOIN CG_USUARIO USUARIO WITH (NOLOCK) " );
		query.append( "		ON COM.cIdUsuarioCaptura = USUARIO.U_LOGIN" );
		query.append( "		LEFT JOIN v_empleados_giro EMP WITH (NOLOCK) " );
		query.append( "		ON USUARIO.cNumeroEmpleado = EMP.CLAVE" );
		query.append( "		WHERE nIdComision = ? " );

		try {

			pst = conn.prepareStatement( query.toString() );
			pst.setInt( 1, folio );

			rs = pst.executeQuery();

			if ( rs.next() ) {
				correo = rs.getString( "U_EMAIL" );
				nombre = rs.getString( "cNombre" );
				puesto = rs.getString( "cPuesto" );
			}

			String cuerpoCorreo = generaCuerpoCorreo( conn, folio, nombre, puesto );

			MailSender.enviaCorreoCNF( correo, cuerpoCorreo, "Continue tramite de Comisión de Viáticos" );

		} finally {

			CloseObject.closeObject( pst, false );
			CloseObject.closeObject( rs, false );
		}
	}

	public static void enviarCorreoAgencia( Connection conn, int folio ) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		String correo = "", notas= "";		
		try {
			notas = consultarNotas(conn, folio); 
			pst = conn.prepareStatement( " SELECT GP_VALOR EMAIL FROM CG_GRUPO_PROPIEDADES WITH (NOLOCK) WHERE GP_NOMBRE = 'EMAIL_AGENCIA_BOLETOS'");
			rs = pst.executeQuery();

			if ( rs.next() ) {
				correo = rs.getString( "EMAIL" );	
			}

			String cuerpoCorreo = generaCuerpoCorreoAgencia( conn, folio, notas);

			MailSender.enviaCorreoCNF( correo, cuerpoCorreo, "Solicitud de Cotización de Vuelo por Comisión de Viáticos" );

		} finally {

			CloseObject.closeObject( pst, false );
			CloseObject.closeObject( rs, false );
		}
	}
	
	private static String consultarNotas( Connection conn, int folio ) throws Exception{
		PreparedStatement pst = null;
		ResultSet rs = null;
		String nota = "";
		
		try {
			pst = conn.prepareStatement( "SELECT cJustificaBoleto FROM tComision WITH (NOLOCK) WHERE nIdComision = ?" );
			pst.setInt( 1, folio );
			
			rs = pst.executeQuery();
			
			if(rs.next()) {
				nota = rs.getString( 1 );
			}
		
		} finally {
			CloseObject.closeObject( pst, false );
			CloseObject.closeObject( rs, false );
		}
		
		return nota;
	}
	
	public static int consutaTieneBoleto( Connection conn, int folio ) throws Exception{
		PreparedStatement pst = null;
		ResultSet rs = null;
		int hasTicket = 0;
		
		try {
			pst = conn.prepareStatement( "SELECT ISNULL(cTieneBoleto,0) FROM tcomision WITH (NOLOCK) WHERE nIdComision = ?" );
			pst.setInt( 1, folio );
			
			rs = pst.executeQuery();
			
			if(rs.next()) {
				hasTicket = rs.getInt( 1 );
			}
		
		} finally {
			CloseObject.closeObject( pst, false );
			CloseObject.closeObject( rs, false );
		}
		
		return hasTicket;
	}

	public static String generaCuerpoCorreo( Connection conn, int folio, String nombreCompleto, String puesto ) throws Exception {

		PreparedStatement pst = null;
		ResultSet rs = null;
		String nombreComision = "", mTotalAgenda = "", mTotalTransporte = "", nombreComisionado = "";
		StringBuilder query =  new StringBuilder();

		query.append( "SELECT nIdComision, tComision.nIdEmpleado, nombreCompleto, cNombreComision, mTotalAgenda, mTotalTransporte " ); 
		query.append( "	FROM tComision WITH (NOLOCK) " );
		query.append( "	INNER JOIN v_empleados emp WITH (NOLOCK) " ); 
		query.append( "		ON emp.nEmpleado = tComision.nIdEmpleado " );
		query.append( "	WHERE nIdComision = ? " );

		try {
			pst = conn.prepareStatement( query.toString() );
			pst.setInt( 1, folio );

			rs = pst.executeQuery();

			if ( rs.next() ) {
				nombreComision = rs.getString( "cNombreComision" );
				mTotalAgenda = rs.getString( "mTotalAgenda" );
				mTotalTransporte = rs.getString( "mTotalTransporte" );
				nombreComisionado = rs.getString( "nombreCompleto" );
			}

			String mailBody = "<html>";
			mailBody += "\n\t<head>";
			mailBody += "\n\t<meta charset=\"UTF-8\">";
			mailBody += "\n\t<style type=\"text/css\">";
			mailBody += "\n\tbody {";
			mailBody += "\n\t\t	font-family: verdana, arial, sans-serif;";
			mailBody += "\n\t\t	font-size: 12px;";
			mailBody += "\n\t}";

			mailBody += "\n\ttable {";
			mailBody += "\n\t\tfont-size: 12px;";
			mailBody += "\n\t\tcolor: #333333;";
			mailBody += "\n\t\tborder-width: 1px;";
			mailBody += "\n\t\tborder-color: #666666;";
			mailBody += "\n\t\tborder-collapse: collapse;";
			mailBody += "\n\t}";

			mailBody += "\n\ttable th {";
			mailBody += "\n\t\tborder-width: 1px;";
			mailBody += "\n\t\tpadding: 8px;";
			mailBody += "\n\t\tborder-style: solid;";
			mailBody += "\n\t\tborder-color: #666666;";
			mailBody += "\n\t\tbackground-color: #dedede;";
			mailBody += "\n\t}";

			mailBody += "\n\ttable td {";
			mailBody += "\n\t\tborder-width: 1px;";
			mailBody += "\n\t\tpadding: 8px;";
			mailBody += "\n\t\tborder-style: solid;";
			mailBody += "\n\t\tborder-color: #666666;";
			mailBody += "\n\t\tbackground-color: #ffffff;";
			mailBody += "\n\t}";
			mailBody += "\n\t</style>";
			mailBody += "</head>";
			mailBody += "\n\t<body>";
			mailBody += "\n\t\t<form id=\"Form\" name=\"FormViaticos\" >";
			mailBody += "	<b> C." + nombreCompleto + "</b>";
			mailBody += "	<br>";
			mailBody += "	<b>" + puesto + "</b>";
			mailBody += "	<br />";
			mailBody += "	<p>";
			mailBody += "		La siguiente comisión de Viáticos ya se autorizó:";
			mailBody += "	</p>";

			mailBody += "	<table>";
			mailBody += "		<thead>";
			mailBody += "			<tr>";
			mailBody += "				<th>Folio</th>";
			mailBody += "				<th>Nombre Comisionado</th>";
			mailBody += "				<th>Nombre de la comisión</th>";
			mailBody += "				<th>Monto Agenda</th>";
			mailBody += "				<th>Monto Transporte</th>";
			mailBody += "			</tr>";
			mailBody += "		</thead>";
			mailBody += "		<tbody>";

			mailBody += "<tr>";
			mailBody += "\n<td>" + folio + "</td>";
			mailBody += "\n<td>" + nombreComisionado + "</td>";
			mailBody += "\n<td>" + nombreComision + "</td>";
			mailBody += "\n<td>" + mTotalAgenda + "</td>";
			mailBody += "\n<td>" + mTotalTransporte + "</td>";
			mailBody += "</tr>";
			mailBody += "		</tbody>";
			mailBody += "	</table>";
			mailBody += "	<br />";
			mailBody += "	<br />";
			mailBody += "\n <b>Se realizó la comprobación de ASISTENCIA PROVISIONAL en el reloj Checador. La cual será anulada sino se comprueba la comisión 5 dias despues de la fecha final de la Agenda</b>";
			mailBody += "	<br />";
			mailBody += "	<br />";
			mailBody += "\n Si desea consultar o comprobar el trámite lo podra realizar desde el <b>modulo de Consultas con el tipo de documento Consulta Viaticos.</b>";
			mailBody += "	<br />";
			mailBody += "	<br />";
			mailBody += "	<p>";
			mailBody += "		Notificaciones Automáticas<br />Sistema de Administración Integral<br />" + Util.getToday();
			mailBody += "	</p>";
			mailBody += "	</form>";
			mailBody += "</body>";
			mailBody += "</html>";

			return mailBody;

		} finally {

			CloseObject.closeObject( pst, false );
			CloseObject.closeObject( rs, false );
		}
	}

	public static String generaCuerpoCorreoAut( Connection conn, int folio, String nombreCompleto, String puesto, int idEmpleado ) throws Exception {
		ConfiguraAplicativoBusinessLogic cabl = new ConfiguraAplicativoBusinessLogic( GestionInterface.ATT_CONEXION );
		String urlAutorizacion = cabl.getSystemSetting( "URL_SAI" ) + "/egresos/AutViaticos";

		PreparedStatement pst = null, pst2 = null;
		ResultSet rs = null, rs2 = null;
		String nombreComision = "", mTotalAgenda = "", mTotalTransporte = "", nombreComisionado = "";
		StringBuilder query = new StringBuilder();
		query.append(  "SELECT nIdComision, tComision.nIdEmpleado, nombreCompleto, cNombreComision, mTotalAgenda, mTotalTransporte " ); 
		query.append( "		FROM tComision WITH (NOLOCK) "  );
		query.append( "		INNER JOIN v_empleados emp WITH (NOLOCK) " ); 
		query.append( "			ON emp.nEmpleado = tComision.nIdEmpleado " );
		query.append( "		WHERE nIdComision = ?" );
		
		String query2 = "SELECT nIdEmpleado FROM tFirmantesViaticos WITH (NOLOCK) WHERE nFolio = ? and  nIdEmpleado = ?";

		try {
			pst = conn.prepareStatement( query.toString() );
			pst.setInt( 1, folio );

			rs = pst.executeQuery();

			if ( rs.next() ) {
				nombreComision = rs.getString( "cNombreComision" );
				mTotalAgenda = rs.getString( "mTotalAgenda" );
				mTotalTransporte = rs.getString( "mTotalTransporte" );
				nombreComisionado = rs.getString( "nombreCompleto" );
			}

			pst2 = conn.prepareStatement( query2 );
			pst2.setInt( 1, folio );
			pst2.setInt( 2, idEmpleado );

			rs2 = pst2.executeQuery();

			if ( rs2.next() ) {

				idEmpleado = rs2.getInt( "nIdEmpleado" );
			}

			String mailBody = "<html>";
			mailBody += "\n\t<head>";
			mailBody += "\n\t<meta charset=\"UTF-8\">";
			mailBody += "\n\t<style type=\"text/css\">";
			mailBody += "\n\tbody {";
			mailBody += "\n\t\t	font-family: verdana, arial, sans-serif;";
			mailBody += "\n\t\t	font-size: 12px;";
			mailBody += "\n\t}";

			mailBody += "\n\ttable {";
			mailBody += "\n\t\tfont-size: 12px;";
			mailBody += "\n\t\tcolor: #333333;";
			mailBody += "\n\t\tborder-width: 1px;";
			mailBody += "\n\t\tborder-color: #666666;";
			mailBody += "\n\t\tborder-collapse: collapse;";
			mailBody += "\n\t}";

			mailBody += "\n\ttable th {";
			mailBody += "\n\t\tborder-width: 1px;";
			mailBody += "\n\t\tpadding: 8px;";
			mailBody += "\n\t\tborder-style: solid;";
			mailBody += "\n\t\tborder-color: #666666;";
			mailBody += "\n\t\tbackground-color: #dedede;";
			mailBody += "\n\t}";

			mailBody += "\n\ttable td {";
			mailBody += "\n\t\tborder-width: 1px;";
			mailBody += "\n\t\tpadding: 8px;";
			mailBody += "\n\t\tborder-style: solid;";
			mailBody += "\n\t\tborder-color: #666666;";
			mailBody += "\n\t\tbackground-color: #ffffff;";
			mailBody += "\n\t}";
			mailBody += "\n\t</style>";
			mailBody += "</head>";
			mailBody += "\n\t<body>";
			mailBody += "\n\t\t<form id=\"Form\" name=\"FormViaticos\" >";
			mailBody += "	<b> C." + nombreCompleto + "</b>";
			mailBody += "	<br>";
			mailBody += "	<b>" + puesto + "</b>";
			mailBody += "	<br />";
			mailBody += "	<p>";
			mailBody += "		Se solicita de su autorización de la siguiente Comisión de Viáticos:";
			mailBody += "	</p>";

			mailBody += "	<table>";
			mailBody += "		<thead>";
			mailBody += "			<tr>";
			mailBody += "				<th>Folio</th>";
			mailBody += "				<th>Nombre Comisionado</th>";
			mailBody += "				<th>Nombre de la comisión</th>";
			mailBody += "				<th>Monto Agenda</th>";
			mailBody += "				<th>Monto Transporte</th>";
			mailBody += "			</tr>";
			mailBody += "		</thead>";
			mailBody += "		<tbody>";

			mailBody += "<tr>";
			mailBody += "\n<td>" + folio + "</td>";
			mailBody += "\n<td>" + nombreComisionado + "</td>";
			mailBody += "\n<td>" + nombreComision + "</td>";
			mailBody += "\n<td>" + mTotalAgenda + "</td>";
			mailBody += "\n<td>" + mTotalTransporte + "</td>";
			mailBody += "</tr>";
			mailBody += "		</tbody>";
			mailBody += "	</table>";
			mailBody += "	<br />";
			mailBody += "	<br />";
			mailBody += "\n<b>Para autorizar o rechazar esta solicitud por favor de click <a href=\"" + urlAutorizacion + generaAccessoAutToken( idEmpleado, "VIATICOS", folio ) + "\" > aquí </a>.</b>";
			mailBody += "	<br />";
			mailBody += "	<br />";
			mailBody += "	<p>";
			mailBody += "		Notificaciones Automáticas<br />Sistema de Administración Integral<br />" + Util.getToday();
			mailBody += "	</p>";
			mailBody += "	</form>";
			mailBody += "</body>";
			mailBody += "</html>";

			return mailBody;

		} finally {

			CloseObject.closeObject( pst, false );
			CloseObject.closeObject( rs, false );
			CloseObject.closeObject( pst2, false );
			CloseObject.closeObject( rs2, false );
		}
	}
	
	public static String generaCuerpoCorreoAgencia( Connection conn, int folio, String notasAgencia ) throws Exception {

		PreparedStatement pst = null, pst2 = null;
		ResultSet rs = null, rs2 = null;
		String nombreComision = "", mTotalAgenda = "", mTotalTransporte = "", nombreComisionado = "", RFC ="";

		ArrayList<String> agenda = new ArrayList<String>();

		StringBuilder query = new StringBuilder();
		query.append( " SELECT comision.nIdComision, comision.nIdEmpleado, cRFC, nombreCompleto, cNombreComision, mTotalAgenda, mTotalTransporte " ); 
		query.append( "	FROM tComision comision WITH (NOLOCK) " );
		query.append( "  	INNER JOIN v_empleados emp WITH (NOLOCK) " ); 
		query.append( "		ON emp.nEmpleado = comision.nIdEmpleado " ); 
		query.append( "	WHERE comision.nIdComision = ? " );

		StringBuilder query2 = new StringBuilder();
		query2.append( " SELECT  fInicio, fFin, destino, cMotivoComision, dias, nPorcentaje ");
		query2.append( " FROM v_AgendaViaticos WHERE nIdComision = ?" );
		
		try {
			pst = conn.prepareStatement( query.toString() );
			pst.setInt( 1, folio );

			rs = pst.executeQuery();

			if ( rs.next() ) {
				nombreComision = rs.getString( "cNombreComision" );
				mTotalAgenda = rs.getString( "mTotalAgenda" );
				mTotalTransporte = rs.getString( "mTotalTransporte" );
				nombreComisionado = rs.getString( "nombreCompleto" );
				RFC = rs.getString( "cRFC" );
				//int idEmp = rs.getInt( "nIdEmpleado" );
				
			}
			
			pst2 = conn.prepareStatement( query2.toString() );
			pst2.setInt( 1, folio );
			
			rs2 = pst2.executeQuery();
			
			while (rs2.next()) {
				agenda.add( rs2.getString( "fInicio" ) );
				agenda.add( rs2.getString( "fFin" ) );
				agenda.add( rs2.getString( "destino" ) );
				agenda.add( rs2.getString( "cMotivoComision" ) );
				agenda.add( rs2.getString( "dias" ) );
				agenda.add( rs2.getString( "nPorcentaje" ) );
			}

			String mailBody = "<html>";
			mailBody += "\n\t<head>";
			mailBody += "\n\t<meta charset=\"UTF-8\">";
			mailBody += "\n\t<style type=\"text/css\">";
			mailBody += "\n\tbody {";
			mailBody += "\n\t\t	font-family: verdana, arial, sans-serif;";
			mailBody += "\n\t\t	font-size: 12px;";
			mailBody += "\n\t}";

			mailBody += "\n\ttable {";
			mailBody += "\n\t\tfont-size: 12px;";
			mailBody += "\n\t\tcolor: #333333;";
			mailBody += "\n\t\tborder-width: 1px;";
			mailBody += "\n\t\tborder-color: #666666;";
			mailBody += "\n\t\tborder-collapse: collapse;";
			mailBody += "\n\t}";

			mailBody += "\n\ttable th {";
			mailBody += "\n\t\tborder-width: 1px;";
			mailBody += "\n\t\tpadding: 8px;";
			mailBody += "\n\t\tborder-style: solid;";
			mailBody += "\n\t\tborder-color: #666666;";
			mailBody += "\n\t\tbackground-color: #dedede;";
			mailBody += "\n\t}";

			mailBody += "\n\ttable td {";
			mailBody += "\n\t\tborder-width: 1px;";
			mailBody += "\n\t\tpadding: 8px;";
			mailBody += "\n\t\tborder-style: solid;";
			mailBody += "\n\t\tborder-color: #666666;";
			mailBody += "\n\t\tbackground-color: #ffffff;";
			mailBody += "\n\t}";
			mailBody += "\n\t</style>";
			mailBody += "</head>";
			mailBody += "\n\t<body>";
			mailBody += "\n\t\t<form id=\"Form\" name=\"FormViaticos\" >";
			mailBody += "	<b> A quien corresponda: </b>";
			mailBody += "	<br />";
			mailBody += "	<p>";
			mailBody += "		Se solicita la cotización de un boleto de avión para la siguiente comisión autorizada:";
			mailBody += "	</p>";
			mailBody += "	<br />";
			mailBody += "	<table>";
			mailBody += "		<thead>";
			mailBody += "			<tr>";
			mailBody += "				<th>Folio</th>";
			mailBody += "				<th>RFC</th>";
			mailBody += "				<th>Nombre del Comisionado</th>";
			mailBody += "				<th>Nombre de la comisión</th>";
			mailBody += "				<th>Monto Agenda</th>";
			mailBody += "				<th>Monto Transporte</th>";
			mailBody += "			</tr>";
			mailBody += "		</thead>";
			mailBody += "		<tbody>";
			mailBody += "<tr>";
			mailBody += "\n<td>" + folio + "</td>";
			mailBody += "\n<td>" + RFC + "</td>";
			mailBody += "\n<td>" + nombreComisionado + "</td>";
			mailBody += "\n<td>" + nombreComision + "</td>";
			mailBody += "\n<td>" + mTotalAgenda + "</td>";
			mailBody += "\n<td>" + mTotalTransporte + "</td>";
			mailBody += "</tr>";
			mailBody += "		</tbody>";
			mailBody += "	</table>";
			mailBody += "	<br />";
			mailBody += "	<table>";
			mailBody += "		<thead>";
			mailBody += "			<tr>";
			mailBody += "				<th>Fecha Inicial</th>";
			mailBody += "				<th>Fecha Fin</th>";
			mailBody += "				<th>Destino</th>";
			mailBody += "				<th>Motivo Comisión</th>";
			mailBody += "				<th>dias</th>";
			mailBody += "				<th>Porcentaje</th>";
			mailBody += "			</tr>";
			mailBody += "		</thead>";
			mailBody += "		<tbody>";
			mailBody += "<tr>";
			mailBody += "\n<td>" + agenda.get( 0 ) + "</td>";
			mailBody += "\n<td>" + agenda.get( 1 ) + "</td>";
			mailBody += "\n<td>" + agenda.get( 2 ) + "</td>";
			mailBody += "\n<td>" + agenda.get( 3 ) + "</td>";
			mailBody += "\n<td>" + agenda.get( 4 ) + "</td>";
			mailBody += "\n<td>" + agenda.get( 5 ) + "</td>";
			mailBody += "</tr>";
			mailBody += "		</tbody>";
			mailBody += "	</table>";
			mailBody += "	<br />";
			mailBody += "	<br />";
			mailBody += "\n Notas:" + notasAgencia;
			mailBody += "	<br />";
			mailBody += "	<br />";
			mailBody += "	<p>";
			mailBody += "		Notificaciones Automáticas<br />Sistema de Administración Integral<br />" + Util.getToday();
			mailBody += "	</p>";
			mailBody += "	</form>";
			mailBody += "</body>";
			mailBody += "</html>";

			return mailBody;

		} finally {

			CloseObject.closeObject( pst, false );
			CloseObject.closeObject( rs, false );
		}
	}

	public static void enviarCorreoCancela( Connection conn, int folio ) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		String nombre = "", puesto = "", correo ="";
		
		if ( "true".equalsIgnoreCase( ConfiguraAplicativoManager.getSystemSetting( conn, "AMBIENTE_DESARROLLO" ) ) || "S".equalsIgnoreCase( ConfiguraAplicativoManager.getSystemSetting( conn, "AMBIENTE_LOCAL" ) ) )
			correo = ConfiguraAplicativoManager.getSystemSetting( conn, "CORREO_ALERTAS_DESARROLLO" );
		else {
		
			correo = ConfiguraAplicativoManager.getSystemSetting( conn, "CORREO_CANCELACION_VIATICOS" );
		
			StringBuilder query = new StringBuilder();
			query.append( " SELECT U_NOMBRE cNombre, U_EMAIL , ISNULL( EMP.DESCRIPCION_PUESTO, '') cPuesto" );
			query.append( "		FROM tComision COM WITH (NOLOCK) " );
			query.append( "		INNER JOIN CG_USUARIO USUARIO WITH (NOLOCK) " );
			query.append( "		ON COM.cIdUsuarioCaptura = USUARIO.U_LOGIN" );
			query.append( "		LEFT JOIN v_empleados_giro EMP WITH (NOLOCK) " );
			query.append( "		ON USUARIO.cNumeroEmpleado = EMP.CLAVE" );
			query.append( "		WHERE nIdComision = ? " );
			
			try {
				pst = conn.prepareStatement( query.toString() );
				pst.setInt( 1, folio );
	
				rs = pst.executeQuery();
	
				if ( rs.next() ) {
					correo += rs.getString( "U_EMAIL" );
					nombre = rs.getString( "cNombre" );
					puesto = rs.getString( "cPuesto" );
				}
				
			} finally {
	
				CloseObject.closeObject( pst, false );
				CloseObject.closeObject( rs, false );
			}
		
		}
			
			String cuerpoCorreo = generaCuerpoCorreoCancela( conn, folio, nombre, puesto );

			MailSender.enviaCorreoCNF( correo, cuerpoCorreo, "Cancelación de Comisión" );
		
	}

	public static String generaCuerpoCorreoCancela( Connection conn, int folio, String nombreCompleto, String puesto) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		String nombreComision = "", mTotalAgenda = "", mTotalTransporte = "", nombreComisionado = "", finicio = "", ffin = "";
		int idEmpleado = 0;
		StringBuilder query = new StringBuilder();
		query.append( "SELECT tComision.nIdComision, tComision.nIdEmpleado, nombreCompleto, cNombreComision, mTotalAgenda, mTotalTransporte, agenda.FINICIO, agenda.FFIN " );
		query.append( "	FROM tComision WITH (NOLOCK) " );
		query.append( "	INNER JOIN v_AgendaDiasAcumulados agenda WITH (NOLOCK) ON agenda.nIdComision = tComision.nIdComision " );
		query.append( "	INNER JOIN v_empleados emp WITH (NOLOCK) ON emp.nEmpleado = tComision.nIdEmpleado WHERE tComision.nIdComision = ? " );

		try {
			pst = conn.prepareStatement( query.toString() );
			pst.setInt( 1, folio );

			rs = pst.executeQuery();

			if ( rs.next() ) {
				nombreComision = rs.getString( "cNombreComision" );
				mTotalAgenda = rs.getString( "mTotalAgenda" );
				mTotalTransporte = rs.getString( "mTotalTransporte" );
				nombreComisionado = rs.getString( "nombreCompleto" );
				finicio = rs.getString( "FINICIO" );
				ffin = rs.getString( "FFIN" );
				idEmpleado = rs.getInt( "nIdEmpleado" );
			}

			String mailBody = "<html>";
			mailBody += "\n\t<head>";
			mailBody += "\n\t<meta charset=\"UTF-8\">";
			mailBody += "\n\t<style type=\"text/css\">";
			mailBody += "\n\tbody {";
			mailBody += "\n\t\t	font-family: verdana, arial, sans-serif;";
			mailBody += "\n\t\t	font-size: 12px;";
			mailBody += "\n\t}";

			mailBody += "\n\ttable {";
			mailBody += "\n\t\tfont-size: 12px;";
			mailBody += "\n\t\tcolor: #333333;";
			mailBody += "\n\t\tborder-width: 1px;";
			mailBody += "\n\t\tborder-color: #666666;";
			mailBody += "\n\t\tborder-collapse: collapse;";
			mailBody += "\n\t}";

			mailBody += "\n\ttable th {";
			mailBody += "\n\t\tborder-width: 1px;";
			mailBody += "\n\t\tpadding: 8px;";
			mailBody += "\n\t\tborder-style: solid;";
			mailBody += "\n\t\tborder-color: #666666;";
			mailBody += "\n\t\tbackground-color: #dedede;";
			mailBody += "\n\t}";

			mailBody += "\n\ttable td {";
			mailBody += "\n\t\tborder-width: 1px;";
			mailBody += "\n\t\tpadding: 8px;";
			mailBody += "\n\t\tborder-style: solid;";
			mailBody += "\n\t\tborder-color: #666666;";
			mailBody += "\n\t\tbackground-color: #ffffff;";
			mailBody += "\n\t}";
			mailBody += "\n\t</style>";
			mailBody += "</head>";
			mailBody += "\n\t<body>";
			mailBody += "\n\t\t<form id=\"Form\" name=\"FormViaticos\" >";
			mailBody += "	<b> C." + nombreCompleto + "</b>";
			mailBody += "	<br>";
			mailBody += "	<b>" + puesto + "</b>";
			mailBody += "	<br />";
			mailBody += "	<p>";
			mailBody += "		Se canceló la siguiente Agenda de Viáticos, en caso que sean dias anteriores favor de justificarlos en el control de asistencia:";
			mailBody += "	</p>";

			mailBody += "	<table>";
			mailBody += "		<thead>";
			mailBody += "			<tr>";
			mailBody += "				<th>Folio</th>";
			mailBody += "				<th>Num Empleado</th>";
			mailBody += "				<th>Nombre Comisionado</th>";
			mailBody += "				<th>Nombre de la comisión</th>";
			mailBody += "				<th>Fecha Inicio</th>";
			mailBody += "				<th>Fecha Fin</th>";
			mailBody += "				<th>Monto Agenda</th>";
			mailBody += "				<th>Monto Transporte</th>";
			mailBody += "			</tr>";
			mailBody += "		</thead>";
			mailBody += "		<tbody>";

			mailBody += "<tr>";
			mailBody += "\n<td>" + folio + "</td>";
			mailBody += "\n<td>" + idEmpleado + "</td>";
			mailBody += "\n<td>" + nombreComisionado + "</td>";
			mailBody += "\n<td>" + nombreComision + "</td>";
			mailBody += "\n<td>" + finicio + "</td>";
			mailBody += "\n<td>" + ffin + "</td>";
			mailBody += "\n<td>" + mTotalAgenda + "</td>";
			mailBody += "\n<td>" + mTotalTransporte + "</td>";
			mailBody += "</tr>";
			mailBody += "		</tbody>";
			mailBody += "	</table>";
			mailBody += "	<br />";
			mailBody += "	<br />";
			mailBody += "	<p>";
			mailBody += "		Notificaciones Automáticas<br/>Sistema de Administración Integral<br/> " + Util.getToday();
			mailBody += "	</p>";
			mailBody += "	</form>";
			mailBody += "</body>";
			mailBody += "</html>";

			return mailBody;

		} finally {

			CloseObject.closeObject( pst, false );
			CloseObject.closeObject( rs, false );

		}
	}

	public static void enviarCorreoTramitesRetraso( Connection conn, int folio, String estatus ) throws Exception {
		PreparedStatement pst = null, pst2 = null;
		ResultSet rs = null, rs2 = null;
		String nombre = "", puesto = "", correo = "";
		StringBuilder query = new StringBuilder();

		query.append( "SELECT nombreCompleto, CARGO, CASE WHEN D_EMAIL = U_EMAIL THEN D_email else d_Email + ';' + U_EMAIL end correo" ); 
		query.append(  "	FROM tComision WITH (NOLOCK) " ); 
		query.append(  "		INNER JOIN v_empleados emp WITH (NOLOCK) " ); 
		query.append(  "		ON emp.nEmpleado = tComision.nIdEmpleado " ); 
		query.append(  "		INNER JOIN CG_USUARIO Usuario with ( NOLOCK )" ); 
		query.append(  "		ON Usuario.U_LOGIN = tComision.cIdUsuarioCaptura" ); 
		query.append(  "		WHERE nIdComision = ? "); 

		try {
			pst = conn.prepareStatement( query.toString() );
			pst.setInt( 1, folio );
			rs = pst.executeQuery();

			if ( rs.next() ) {
				correo = rs.getString( "correo" );
				nombre = rs.getString( "nombreCompleto" );
				puesto = rs.getString( "CARGO" );

			}

			String cuerpoCorreo = generaCuerpoCorreoTramitesPendientes( conn, folio, nombre, puesto, estatus );
			AlarmaManager.procesaAlarmaCNF( conn, null, null, null, "Solicitud de Autorización de Viáticos", correo, cuerpoCorreo );

		} finally {

			CloseObject.closeObject( pst, false );
			CloseObject.closeObject( rs, false );
			CloseObject.closeObject( pst2, false );
			CloseObject.closeObject( rs2, false );
		}
	}

	public static String generaCuerpoCorreoTramitesPendientes( Connection conn, int folio, String nombre, String puesto, String estatus ) throws Exception {
		StringBuilder query = new StringBuilder();
		PreparedStatement pst = null;
		ResultSet rs = null;
		String nombreComision = "", mTotalAgenda = "", mTotalTransporte = "", nombreComisionado = "", fechaInicio = "", fechaFin = "";
		int idEmpleado = 0;
		query.append( " SELECT agenda.nIdComision, tComision.nIdEmpleado, cNombreComision, mTotalAgenda, mTotalTransporte, nombreCompleto " );
		query.append( "		, agenda.finicio, agenda.ffin " );
		query.append( "	FROM tComision WITH (NOLOCK) " );
		query.append( "		INNER JOIN V_AGENDADIASACUMULADOS agenda" );
		query.append( "		ON agenda.nidcomision = tComision.nIdComision" );
		query.append( "		INNER JOIN v_empleados emp WITH (NOLOCK) " );
		query.append( "		ON emp.nEmpleado = tComision.nIdEmpleado " );
		query.append( "		WHERE agenda.nIdComision = ?" );

		try {
			pst = conn.prepareStatement( query.toString() );
			pst.setInt( 1, folio );

			rs = pst.executeQuery();

			if ( rs.next() ) {
				nombreComision = rs.getString( "cNombreComision" );
				mTotalAgenda = rs.getString( "mTotalAgenda" );
				mTotalTransporte = rs.getString( "mTotalTransporte" );
				nombreComisionado = rs.getString( "nombreCompleto" );
				fechaInicio = rs.getString( "finicio" );
				fechaFin = rs.getString( "ffin" );
				idEmpleado = rs.getInt( "nIdEmpleado" );
			}

			String mailBody = "<html>";
			mailBody += "\n\t<head>";
			mailBody += "\n\t<meta charset=\"UTF-8\">";
			mailBody += "\n\t<style type=\"text/css\">";
			mailBody += "\n\tbody {";
			mailBody += "\n\t\t	font-family: verdana, arial, sans-serif;";
			mailBody += "\n\t\t	font-size: 12px;";
			mailBody += "\n\t}";

			mailBody += "\n\ttable {";
			mailBody += "\n\t\tfont-size: 12px;";
			mailBody += "\n\t\tcolor: #333333;";
			mailBody += "\n\t\tborder-width: 1px;";
			mailBody += "\n\t\tborder-color: #666666;";
			mailBody += "\n\t\tborder-collapse: collapse;";
			mailBody += "\n\t}";

			mailBody += "\n\ttable th {";
			mailBody += "\n\t\tborder-width: 1px;";
			mailBody += "\n\t\tpadding: 8px;";
			mailBody += "\n\t\tborder-style: solid;";
			mailBody += "\n\t\tborder-color: #666666;";
			mailBody += "\n\t\tbackground-color: #dedede;";
			mailBody += "\n\t}";

			mailBody += "\n\ttable td {";
			mailBody += "\n\t\tborder-width: 1px;";
			mailBody += "\n\t\tpadding: 8px;";
			mailBody += "\n\t\tborder-style: solid;";
			mailBody += "\n\t\tborder-color: #666666;";
			mailBody += "\n\t\tbackground-color: #ffffff;";
			mailBody += "\n\t}";
			mailBody += "\n\t</style>";
			mailBody += "</head>";
			mailBody += "\n\t<body>";
			mailBody += "\n\t\t<form id=\"Form\" name=\"FormViaticos\" >";
			mailBody += "	<b> C." + nombre + "</b>";
			mailBody += "	<br>";
			mailBody += "	<b>" + puesto + "</b>";
			mailBody += "	<br />";
			mailBody += "	<p>";
			mailBody += "		Se informa que tiene la siguiente comisión <b>" + estatus + "</b>";
			mailBody += "	</p>";

			mailBody += "	<table>";
			mailBody += "		<thead>";
			mailBody += "			<tr>";
			mailBody += "				<th>Folio</th>";
			mailBody += "				<th>Num Empleado</th>";
			mailBody += "				<th>Nombre Comisionado</th>";
			mailBody += "				<th>Nombre de la comisión</th>";
			mailBody += "				<th>Fecha Inicio</th>";
			mailBody += "				<th>Fecha Fin</th>";
			mailBody += "				<th>Monto Agenda</th>";
			mailBody += "				<th>Monto Transporte</th>";
			mailBody += "			</tr>";
			mailBody += "		</thead>";
			mailBody += "		<tbody>";

			mailBody += "<tr>";
			mailBody += "\n<td>" + folio + "</td>";
			mailBody += "\n<td>" + idEmpleado + "</td>";
			mailBody += "\n<td>" + nombreComisionado + "</td>";
			mailBody += "\n<td>" + nombreComision + "</td>";
			mailBody += "\n<td>" + fechaInicio + "</td>";
			mailBody += "\n<td>" + fechaFin + "</td>";
			mailBody += "\n<td>" + mTotalAgenda + "</td>";
			mailBody += "\n<td>" + mTotalTransporte + "</td>";
			mailBody += "</tr>";
			mailBody += "		</tbody>";
			mailBody += "	</table>";
			mailBody += "	<br />";
			mailBody += "	<br />";
			mailBody += "\nSe le recuerda que <b>cuenta con hasta 5 días hábiles </b> para realizar su trámite de pago y/o comprobación para concluir con su proceso.";
			mailBody += "	<br/>";
			mailBody += "	<br/>";
			mailBody += "\nDe no finalizar el trámite, se tomará como una comisión no realizada y será eliminada del reloj checador. Para evitar un <b>descuento de nómina por falta no justificada</b>, se le invita a dar seguimiento a su proceso de comprobación y/o pago.";
			mailBody += "	<br />";
			mailBody += "	<p>";
			mailBody += "		Notificaciones Automáticas<br />Sistema de Administración Integral<br />" + Util.getToday();
			mailBody += "	</p>";
			mailBody += "	</form>";
			mailBody += "</body>";
			mailBody += "</html>";

			return mailBody;

		} finally {

			CloseObject.closeObject( pst, false );
			CloseObject.closeObject( rs, false );

		}
	}

	public static String generaAccessoAutToken( int numeroEmpleado, String document, int folio ) {

		StringBuffer parametrosReales = null;
		parametrosReales = new StringBuffer( "?" );
		parametrosReales.append( "u=" ).append( StringUtils.reverse( String.valueOf( numeroEmpleado ) ) );
		parametrosReales.append( "&" );
		parametrosReales.append( "d=" ).append( String.valueOf( document ) );
		parametrosReales.append( "&" );
		parametrosReales.append( "f=" ).append( StringUtils.reverse( String.valueOf( folio ) ) );
		log.debug( "Cadena generada: " + parametrosReales );

		return parametrosReales.toString();
	}

	public static void ActualizaBitacoraFirmantes( Connection conn, int folioComision, int idEmpleado ) throws Exception {
		PreparedStatement pst = null;
		int id = 0; 
		
		try {
			id = consultaIdFirmantes (conn, folioComision, idEmpleado);
			
			pst = conn.prepareStatement( "UPDATE tFirmantesViaticos SET cAutorizado = 'S', dfechaAutoriza = GETDATE() WHERE nFolio = ? and nIdEmpleado = ? AND nRenglon = ?" );
			pst.setInt( 1, folioComision );
			pst.setInt( 2, idEmpleado );
			pst.setInt( 3, id );

			pst.executeUpdate();

		} finally {
			CloseObject.closeObject( pst, false );

		}

	}
	
	public static void ActualizaFirmantesVacaciones( Connection conn, int folioComision, int idEmpleado ) throws Exception {
		PreparedStatement pst = null; 
		
		try {
			pst = conn.prepareStatement( "UPDATE tFirmantesViaticos SET cAutorizado = 'S', dfechaAutoriza = GETDATE(), cNota = 'VACACIONES' WHERE nFolio = ? and nIdEmpleado = ?" );
			pst.setInt( 1, folioComision );
			pst.setInt( 2, idEmpleado );

			pst.executeUpdate();

		} finally {
			CloseObject.closeObject( pst, false );

		}

	}
	
	public static int consultaIdFirmantes( Connection conn, int folioComision, int idEmpleado ) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		int id = 0;
		
		try {

			pst = conn.prepareStatement( "SELECT TOP 1 nRenglon FROM tFirmantesViaticos WITH (NOLOCK) WHERE nFolio = ? AND nIdEmpleado = ? AND cAutorizado = 'N' ORDER BY nRenglon" );
			pst.setInt( 1, folioComision );
			pst.setInt( 2, idEmpleado );

			rs= pst.executeQuery();
			
			if(rs.next()) {
				id = rs.getInt( 1 );
			}

		} finally {
			CloseObject.closeObject( pst, false );
			CloseObject.closeObject( rs, false );
		}

		return id;
	}

	public static void RechazaBitacoraFirmantes( Connection conn, int folioComision ) throws Exception {
		PreparedStatement pst = null;

		try {
			pst = conn.prepareStatement( "UPDATE tFirmantesViaticos SET cAutorizado = 'C', dfechaCancelacion = GETDATE() WHERE nFolio = ? " );
			pst.setInt( 1, folioComision );

			pst.executeUpdate();

		} finally {
			CloseObject.closeObject( pst, false );

		}

	}

	public static int estatusSiguiente( Connection conn, int folio, int idEmpleado ) throws Exception {
		int idEstatus = 0;
		PreparedStatement pst = null;
		ResultSet rs = null;

		try {
			pst = conn.prepareStatement( "SELECT nRenglon FROM tFirmantesViaticos WITH (NOLOCK) where nFolio = ? and nIdEmpleado = ?" );
			pst.setInt( 1, folio );
			pst.setInt( 2, idEmpleado );

			rs = pst.executeQuery();

			if ( rs.next() ) {
				idEstatus = rs.getInt( 1 );
			}

		} finally {
			CloseObject.closeObject( pst, false );
			CloseObject.closeObject( rs, false );

		}

		return idEstatus;
	}


	public static void avanzarCaso( Connection conn, int folioComision, String login, String documento, String estatus ) throws Exception {

		CasoBusinessLogic cbl = new CasoBusinessLogic( GestionInterface.ATT_CONEXION );
		Caso c = findCaso( conn, documento, String.valueOf( folioComision ) , login );
		Map<String, String> datos = Util.readValuesCasoDato( c.getCasoDato() );
		
		cbl.avanzaCaso( conn, c, login, "", new String [] { estatus }, new String [] { estatus }, datos, null );
	}

	
	public static Caso findCaso( Connection conn, String tituloAplicacion, String folio , String login) throws Exception {
		Caso c = null;
		String query = "SELECT folio, id_gabinete " + "  FROM IMX" + tituloAplicacion + " WITH(NOLOCK) " + " WHERE folio LIKE '%-%-' + ?";
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement( query );
			ps.setString( 1, folio );

			rs = ps.executeQuery();

			int encontrados = 0;
			while ( rs.next() ) {
				encontrados++;
				String folioCompleto = rs.getString( "folio" );
				int idGabinete = rs.getInt( "id_gabinete" );
				
				c = new Caso();
				c.setFolio( folioCompleto );
				c.setIdGabinete(idGabinete);
				CasoManager.actualizarGabinete(conn, idGabinete, folioCompleto);
				
				c = CasoManager.select(conn, c);
				
			}
			
			if ( encontrados > 1 || c == null ) {
				ResultSet rs2 = null;
				PreparedStatement ps2 = null, ps3 = null;
				
				ps2 = conn.prepareStatement( "select ID_CASO, c_folio from CG_CASO WITH (NOLOCK) WHERE C_FOLIO LIKE 'VIAT-%-' + ? " );
				ps2.setString( 1, folio );

				rs2 = ps2.executeQuery();
				
				if (rs2.next()) {
					int idCaso = rs2.getInt( 1 );	
					String folioCaso = rs2.getString( 2 );
					String year = String.valueOf( Year.now().getValue() );
					LocalDate fecha = LocalDate.now(); 			
					HashMap<String, String> datos =new HashMap<String, String>();
					
					datos.put( "1", folioCaso );
					datos.put( "2", String.valueOf( fecha ) );
					datos.put( "3", year );
					datos.put( "4", login );
					datos.put( "5", "Documentaci&oacuten Comisión Viaticos" );
					datos.put( "6", "MXP" );
					datos.put( "7", String.valueOf( fecha ) );
					datos.put( "8", "false");
					
					for (Map.Entry<String, String> entry : datos.entrySet()) {
						  ps3 = conn.prepareStatement( "UPDATE CG_CASO_DATO SET CD_VALOR = ? WHERE ID_CASO = ? AND ID_CD = ? AND ID_TC = 73" );
						  ps3.setString( 1, entry.getValue() );
						  ps3.setInt( 2, idCaso );
						  ps3.setString( 3, entry.getKey() );
						  
						  ps3.executeUpdate();
					}
					
					conn.commit();
					
					c = new Caso();
					c.setIdCaso( idCaso );
					c = CasoManager.select( conn, c );
	
					if ( c == null )
						throw new Exception( "No se pudo encontrar caso con el folio terminacion " + folio + " en la aplicacion " + tituloAplicacion );
					
					if ( c.getIdGabinete() == -1 ) {
	
						Aplicacion app = AplicacionManager.select( conn, c.getTipoCaso().getGavetaAsociada() );
						int id_gabinete = AplicacionManager.createExpediente( conn, "SAI", c, app );
	
						if ( id_gabinete < 0 ) {
							throw new SQLException( "No se pudo generar el gabinete del caso, favor de consultar al administrador." );
						}
	
						c.setIdGabinete( id_gabinete );
						CasoManager.update( conn, c );
					}
					
				}
			}
			
			return c;
		} finally {
			CloseObject.closeObject( ps, false );
			CloseObject.closeObject( rs, false );
		}

	}

	public static Caso iniciaRg( Connection conn, Comision comision, Agenda agenda, Usuario u, String idEvento, int idComisionViaticos ) throws Exception {
		Caso c = null;
		String ejercicioFiscal = "";
		String tipoPago = "RELACIONGASTOS";
		try {
			ejercicioFiscal = EjercicioFiscalManager.getEjercicioFiscalActivo( conn ).getaEjercicioFiscal();
		} catch ( Exception e ) {
			throw new ViaticosException( "Problemas encontrando el ejercicio fiscal: " + e.toString() );
		}

		c = generaCaso( conn, u, idTC_RG, ejercicioFiscal, comision );

		BigDecimal total = ( comision.getTotalAgenda().add( comision.getTotalTransporte() ) );
		String cuentaRFC = ConsultaCuentaBancariaRFC( conn, comision );
		int folio = Util.folio( c );

		InsertRelacionComprobacion( conn, folio, tipoPago, idComisionViaticos, total, comision, cuentaRFC );

		ComisionDAO.insertaComprobacionComision( conn, comision, folio, tipoPago );

		// Si tiene reemplazo borra las facturas del pago origen
		if ( comision.getFolioReemplazo() > 0 ) {

			borrarFacturasOrigen( conn, comision.getFolioReemplazo(), tipoPago );
			borrarBoletoAvionOrigen( conn, comision.getFolioReemplazo(), tipoPago );
			insertaBoletoAvionOrigen( conn, comision.getFolioReemplazo(), folio, comision.getIdComision() );
			insertaRelacionPagos( conn, comision, folio );
			TransporteDAO.actualizarLayout( conn, comision.getIdComision(), "RG", folio );
		}

		TransporteDAO.actualizaFolioPago( conn, comision.getIdComision(), folio );
		
		//Taxis insertados
		int insertados = TransporteDAO.insertarRelacionTaxi( conn, comision.getIdComision(), folio, "RELACIONGASTOS" );

		if ( insertados > 0 )
			TransporteDAO.actualizarLayoutTaxis( conn, comision.getIdComision(), 1 );

		return c;
	}

	private static void borrarBoletoAvionOrigen( Connection conn, int folioReemplazo, String tipoPago ) throws Exception {
		PreparedStatement pst = null;

		try {
			pst = conn.prepareStatement( "DELETE tTransporteAereo WHERE nFolioPago = ? AND cTipoPago = ? " );
			pst.setInt( 1, folioReemplazo );
			pst.setString( 2, tipoPago );

			pst.executeUpdate();

		} finally {
			CloseObject.closeObject( pst, false );

		}

	}

	private static void insertaRelacionPagos( Connection conn, Comision comision, int folio ) throws Exception {
		PreparedStatement pst = null;
		String query = "INSERT INTO tRelacionGastosRelacionReemplazo(nFolioOrigen, nFolioReemplazada, nIdComision) VALUES ( ?, ?, ?)";

		try {
			pst = conn.prepareStatement( query );
			pst.setInt( 1, comision.getFolioReemplazo() );
			pst.setInt( 2, folio );
			pst.setInt( 3, comision.getIdComision() );

			pst.executeUpdate();

		} finally {
			CloseObject.closeObject( pst, false );

		}

	}

	private static void insertaBoletoAvionOrigen( Connection conn, int folioReemplazo, int folioPago, int idComision ) throws Exception {
		PreparedStatement pst = null;
		String query = "INSERT INTO tTransporteAereo (nidComision, nFolioPago, cReferencia,mImporteBoleto , cPartida, cRuta, RFCVuelo, cNombre, cTipoPago ) " ;
		query += " 	SELECT ?,  ?, cNumeroBoleto, mImporteBoleto, cPartida, cRuta, RFCVuelo, cNombreRFC,  'RELACIONGASTOS' FROM tInfoBoleto where nFolioRelacionGastos = ? ";

		try {
			pst = conn.prepareStatement( query );
			pst.setInt( 1, idComision );
			pst.setInt( 2, folioPago );
			pst.setInt( 3, folioReemplazo );

			pst.executeUpdate();

		} finally {
			CloseObject.closeObject( pst, false );

		}

	}

	private static void borrarFacturasOrigen( Connection conn, int folioReemplazo, String tipoPago ) throws Exception {
		PreparedStatement pst = null;

		try {
			pst = conn.prepareStatement( "DELETE tPagoFactura WHERE nFolioPago = ? AND cTipoPago = ? " );
			pst.setInt( 1, folioReemplazo );
			pst.setString( 2, tipoPago );

			pst.executeUpdate();

		} finally {
			CloseObject.closeObject( pst, false );

		}

	}

	public static boolean validaIncidenciasNomina( Connection conn, Comision comision, Agenda agenda ) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		int tieneInicidencia = 0;
		boolean existe = false;
		StringBuilder query  = new StringBuilder();
		query.append( "SELECT COUNT(*) tieneInicidencia FROM v_inicidenciasNomina  WITH (NOLOCK) WHERE c_empleado = ? " );
		query.append( "	AND  (( ? between fFechaInicio and fFechaFin or ? between fFechaInicio and fFechaFin)" ); 
		query.append( "OR ( FFECHAINICIO BETWEEN ? AND ? OR FFECHAFIN BETWEEN ? AND ?))");
		
		java.sql.Date fechaInicio = new java.sql.Date( agenda.getFechaInicio().getTime() );
		java.sql.Date fechaFin = new java.sql.Date( agenda.getFechaFin().getTime() );
		
		try {
			pst = conn.prepareStatement( query.toString()  );
			pst.setInt( 1, comision.getEmpleado().getNoEmpleado() );
			pst.setDate( 2, fechaInicio );
			pst.setDate( 3, fechaFin );
			pst.setDate( 4, fechaInicio );
			pst.setDate( 5, fechaFin );
			pst.setDate( 6, fechaInicio );
			pst.setDate( 7, fechaFin );

			rs = pst.executeQuery();
			
			if ( rs.next()) {
				tieneInicidencia = rs.getInt( 1 );
				if (tieneInicidencia > 0)
					existe = true;
			}

			return existe;
			
		} finally {
			
			CloseObject.closeObject( pst, false );
			
		}
	}
	
	public static boolean actualizarAsistencia( Connection conn, Agenda agenda, int idEmpleado, String concepto ) throws Exception {
		String urlServicio = ConfiguraAplicativoManager.getSystemSetting( conn, "URL_WS_ACTUALIZAR_ASISTENCIA" );

		ViaticoClient client = new ViaticoClient( urlServicio );
		ViaticoRequest request = new ViaticoRequest( idEmpleado, Util.dateToString( agenda.getFechaInicio(), "yyyy-MM-dd" ), Util.dateToString( agenda.getFechaFin(), "yyyy-MM-dd" ), concepto );

		ViaticoResponse response = client.actualizarViaticos( request, urlServicio );

		if ( !response.isSuccess() )
			throw new RuntimeException( "Error al actualizar la asistencia en el sistema de control," + response.getMessage() );

		return true;
	}

	public static boolean revertirAsistencia( Connection conn, Agenda agenda, int idEmpleado ) throws Exception {
		String urlServicio = ConfiguraAplicativoManager.getSystemSetting( conn, "URL_WS_REVERTIR_VIATICOS" );

		ViaticoClient client = new ViaticoClient( urlServicio );
		ViaticoRequest request = new ViaticoRequest( idEmpleado, Util.dateToString( agenda.getFechaInicio(), "yyyy-MM-dd" ), Util.dateToString( agenda.getFechaFin(), "yyyy-MM-dd" ), null );

		ViaticoResponse response = client.revertirAsistencia( request , urlServicio );

		if ( !response.isSuccess() )
			throw new RuntimeException( "Error al actualizar la asistencia en el sistema de control," + response.getMessage() );

		return true;
	}
	
	public static int getFolioRG( Connection conn, int folioViaticos ) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		int folioRG = 0;
		String query = "SELECT nFolioTramite FROM tRelacionComprobacionComisiones WITH (NOLOCK) WHERE nidComisionModulo = ? "; 
		query += " AND cTipoTramite = 'RELACIONGASTOS' AND nFolioTramite NOT IN (SELECT nFolioRELACIONGASTOS FROM tRELACIONGASTOSEncabezado WITH (NOLOCK))";

		try {
			pst = conn.prepareStatement( query );
			pst.setInt( 1, folioViaticos );

			rs = pst.executeQuery();

			if ( rs.next() ) {
				folioRG = rs.getInt( 1 );
			}

			return folioRG;

		} finally {

			CloseObject.closeObject( pst, false );
			CloseObject.closeObject( rs, false );
		}

	}

	public static EgresoEncabezado agregarDatos( Connection conn, Comision comision, EgresoEncabezado encabezado, int folioCaja ) throws Exception {
		PreparedStatement ps2 = null;
		ResultSet rs2 = null;
		String tipoPoliza = "";
		String nombre = "";
		String tipo_oper = "2";
		Date fecha = null;
		
		StringBuilder query = new StringBuilder();
		query.append( "SELECT ben.dNombre + ' ' + ben.dApellidoPaterno + ' ' + ben.dApellidoMaterno Nombre, GETDATE() fecha, MONTH(GETDATE()) mes "); 
		query.append(  " FROM tComision Comision WITH (NOLOCK) INNER JOIN tBeneficiario ben WITH (NOLOCK) " );
		query.append(  " ON Comision.cRFC = ben.dRFC WHERE nIdComision = ? ");
		
		try {
			int tieneFactura = tieneFacturas( conn, comision.getIdComision(), encabezado.getTipoPago() );

			String eventoDevengado = ComisionDAO.consultaEventoViaticos( conn, "RELACIONGASTOS", "X" );

			if ( eventoDevengado.equals( comision.getEvento() ) ) {
				tipoPoliza = "EG";
				folioCaja = 0;
			} else {
				tipoPoliza = "DI";
				tipo_oper = "1";
				encabezado.setCTAB( "" ); //Borrar la cuenta bancaria cuando es comprobación
			}

			ps2 = conn.prepareStatement( query.toString()  );
			ps2.setInt( 1, comision.getIdComision() );
			rs2 = ps2.executeQuery();

			if ( rs2.next() ) {
				nombre = rs2.getString( "Nombre" );
				fecha = rs2.getDate( "fecha" );
				cMes = rs2.getString( "mes" );
			}

			encabezado.setTipoPoliza( tipoPoliza );
			( ( EgresoRELACIONGASTOSEncabezado ) encabezado ).setContieneFacturas( tieneFactura );
			( ( EgresoRELACIONGASTOSEncabezado ) encabezado ).setFolioCaja( folioCaja );
			( ( EgresoRELACIONGASTOSEncabezado ) encabezado ).setNombre( nombre );
			( ( EgresoRELACIONGASTOSEncabezado ) encabezado ).setMes( cMes );
			( ( EgresoRELACIONGASTOSEncabezado ) encabezado ).setFechaAplicacion( fecha );
			( ( EgresoRELACIONGASTOSEncabezado ) encabezado ).setFechaRevision( fecha );
			( ( EgresoRELACIONGASTOSEncabezado ) encabezado ).setFechaProgramadaPago( fecha );
			( ( EgresoRELACIONGASTOSEncabezado ) encabezado ).setIdDestinoGasto( comision.getEvento() );
			encabezado.setIdTipoOperacion( tipo_oper );

		} finally {
			CloseObject.closeObject( ps2, false );
			CloseObject.closeObject( rs2, false );
		}
		return encabezado;
	}

	public static boolean comisionExiste( Connection conn, int folio ) throws Exception {
		int cuantos = 0;
		boolean existe = false;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {

			pst = conn.prepareStatement( "SELECT COUNT(*) FROM tComision WITH (NOLOCK) WHERE nIdComision = ?" );
			pst.setInt( 1, folio );

			rs = pst.executeQuery();

			if ( rs.next() ) {
				cuantos = rs.getInt( 1 );
			}

			if ( cuantos > 0 )
				existe = true;

			return existe;
		} finally {
			CloseObject.closeObject( pst, false );
			CloseObject.closeObject( rs, false );
		}
	}
	
	public static boolean comisionMesesAnteriores( Connection conn, Agenda agenda ) throws Exception {
		boolean existe = false;
		
		Date fechaActual =  new Date(System.currentTimeMillis());
		long fechaInicialMs = agenda.getFechaInicio().getTime();
		long fechaActualMs = fechaActual.getTime();
		long diferencia = fechaActualMs - fechaInicialMs;
		double dias = Math.floor(diferencia / (1000 * 60 * 60 * 24));

		if ( dias > 20 )
			existe = true;

		return existe;
		
	}

	private static boolean consultaExisteEncabezado( Connection conn, int nFolio ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean tieneRG = false;
		try {
			ps = conn.prepareStatement( "SELECT COUNT(*) FROM tRELACIONGASTOSEncabezado WITH (NOLOCK) WHERE nFolioRELACIONGASTOS = ?" );
			ps.setInt( 1, nFolio );

			rs = ps.executeQuery();

			if ( rs.next() ) {
				int aplicado = rs.getInt( 1 );
				if ( aplicado > 0 )
					tieneRG = true;
			}
		} finally {
			CloseObject.closeObject( ps, false );
			CloseObject.closeObject( rs, false );
		}

		return tieneRG;
	}

	private static int tieneFacturas( Connection conn, int folio, String tipoPago ) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		int cantidad = 0;
		try {
			pst = conn.prepareStatement( "SELECT count(*) cantFactura FROM tPagoFactura WITH (NOLOCK) where cRFCFactura not in ('OFICIOTRANSITO', 'OFICIOPAGO') and nFolioPago = ? and cTipoPago = ?" );
			pst.setInt( 1, folio );
			pst.setString( 2, tipoPago );

			rs = pst.executeQuery();

			if ( rs.next() ) {
				cantidad = rs.getInt( 1 );

				if ( cantidad > 0 )
					cantidad = 1;
			}

			return cantidad;

		} finally {
			CloseObject.closeObject( pst, false );
			CloseObject.closeObject( rs, false );
		}
	}

	public static void consultaTransporteLocal( Connection conn, int idComision ) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		BigDecimal monto = new BigDecimal( "0.00" );
		try {
			pst = conn.prepareStatement( "SELECT SUM(mMonto) mMonto FROM tTransporteLocal WITH (NOLOCK) WHERE nidcomision = ?" );
			pst.setInt( 1, idComision );

			rs = pst.executeQuery();

			if ( rs.next() ) {
				monto = rs.getBigDecimal( 1 );
			}

			monto = ( monto == null ? new BigDecimal( "0.00" ) : monto );
			actualizarComision( conn, idComision, monto, new BigDecimal( "0.00" ), "EDITADO" );

		} finally {
			CloseObject.closeObject( pst, false );
			CloseObject.closeObject( rs, false );
		}
	}


	public static void actualizaTotales( Connection conn, int idComision, String estatus ) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		BigDecimal monto = new BigDecimal( "0.00" );

		try {
			monto = ComisionDAO.consultaTotalComision( conn, idComision );
			monto = ( monto == null ? new BigDecimal( "0.00" ) : monto );
			actualizarComision( conn, idComision, new BigDecimal( "0.00" ), monto, estatus);
			ComisionDAO.actualizarDiasComision( conn, idComision );

		} finally {
			CloseObject.closeObject( pst, false );
			CloseObject.closeObject( rs, false );
		}
	}

	public static void actualizarComision( Connection conn, int idComision, BigDecimal montoTransporte, BigDecimal montoAgenda, String tipoOper ) throws Exception {
		PreparedStatement  pst2 = null;
		ResultSet rs = null;

		try {
			if ( montoTransporte.compareTo( BigDecimal.ZERO ) > 0 ) {
				pst2 = conn.prepareStatement( "UPDATE tComision SET mTotalTransporte = ?, nOrigen=? WHERE nidComision = ?" );
				pst2.setBigDecimal( 1, montoTransporte );
				pst2.setString( 2, tipoOper );
				pst2.setInt( 3, idComision );

			} else if ( montoAgenda.compareTo( BigDecimal.ZERO ) > 0 ) {
				pst2 = conn.prepareStatement( "UPDATE tComision SET mTotalAgenda = ?, nOrigen=? WHERE nidComision = ?" );
				pst2.setBigDecimal( 1, montoAgenda );
				pst2.setString( 2, tipoOper );
				pst2.setInt( 3, idComision );

			} else {
				pst2 = conn.prepareStatement( "UPDATE tComision SET mTotalTransporte = ?, mTotalAgenda = ?, nOrigen=? WHERE nidComision = ?" );
				pst2.setBigDecimal( 1, montoTransporte );
				pst2.setBigDecimal( 2, montoAgenda );
				pst2.setString( 3, tipoOper );
				pst2.setInt( 4, idComision );

			}

			pst2.executeUpdate();

		} finally {
			CloseObject.closeObject( pst2, false );
			CloseObject.closeObject( rs, false );
		}
	}

	public static int estaFirmado( Connection conn, int idComision ) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		String query = "SELECT COUNT(*) from tFirmantesViaticos WITH (NOLOCK) where nFolio = ? and cAutorizado = 'S'";
		int firmantes = 0;
		try {
			pst = conn.prepareStatement( query );
			pst.setInt( 1, idComision );

			rs = pst.executeQuery();

			if ( rs.next() ) {
				firmantes = rs.getInt( 1 );
			}

		} finally {

			CloseObject.closeObject( pst, false );
			CloseObject.closeObject( rs, false );
		}

		return firmantes;
	}

	public static void borraFirmantes( Connection conn, int idComision, int noEmpleado ) throws Exception {
		CallableStatement call = null;
		PreparedStatement pst = null;
		int internacional = 0;
		try {
			internacional = ComisionDAO.esComisionInternacional( conn, idComision );

			pst = conn.prepareStatement( "INSERT INTO tFirmantesViaticosBitacora (nFolio, nRenglon, nIdEmpleado, cNombre,  cPuesto, d_email, cAutorizado,dfechaAutoriza, dFechaModificacion) " 
			+ "				SELECT nFolio, nRenglon, nIdEmpleado, cNombre,  cPuesto,  d_email, cAutorizado,dfechaAutoriza, GETDATE() " 
			+ "				from tFirmantesViaticos WITH (NOLOCK) where nfolio = " + idComision );
			pst.execute();

			call = conn.prepareCall( "{call sp_cadenaFirmantes( ?, ?, ? )}" );
			call.setInt( 1, idComision );
			call.setInt( 2, noEmpleado );
			call.setInt( 3, internacional );

			call.execute();

		} finally {

			CloseObject.closeObject( call, false );
			CloseObject.closeObject( pst, false );
		}
	}

	public static String consultaOrigen( Connection conn, int folioComision ) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		String origen = "";

		try {
			pst = conn.prepareStatement( "SELECT nOrigen FROM tComision WITH (NOLOCK) WHERE nIdComision = ?" );
			pst.setInt( 1, folioComision );
			rs = pst.executeQuery();

			if ( rs.next() ) {
				origen = rs.getString( 1 );
			}

			return origen;

		} finally {

			CloseObject.closeObject( pst, false );
			CloseObject.closeObject( rs, false );
		}

	}

	public static void eliminarSolicitud( Connection conn, int idComision, int folioPago ) throws Exception {
		PreparedStatement pst = null, pst1 = null, pst2 = null, pst3 = null, pst4 = null, pst5 = null, pst6 = null, pst7 = null, pst8 = null, pst9 = null,
				pst10 = null, pst11 = null, pst12 = null;
		String tipoPago = "RELACIONGASTOS";
		try {
			// Boletos de Avion
			TransporteDAO.actualizarLayout( conn, idComision, "A", folioPago );
			TransporteDAO.borrarTransporteAereo( conn, idComision, folioPago, tipoPago );
			TransporteDAO.borrarTaxi( conn, folioPago, tipoPago );
			
			// Facturas
			pst = conn.prepareStatement( "DELETE tPagoFactura where nFolioPago = ? and cTipoPago = ?" );
			pst.setInt( 1, folioPago );
			pst.setString( 2, tipoPago );
			pst.executeUpdate();

			pst1 = conn.prepareStatement( "DELETE tPagoFacturaImpuestos where nFolioPago = ? and cTipoPago = ?" );
			pst1.setInt( 1, folioPago );
			pst1.setString( 2, tipoPago );
			pst1.executeUpdate();

			pst2 = conn.prepareStatement( "DELETE tPagoFacturaRetencion where nFolioPago = ? and cTipoPago = ?" );
			pst2.setInt( 1, folioPago );
			pst2.setString( 2, tipoPago );
			pst2.executeUpdate();

			// Relacion Comprobacion
			pst3 = conn.prepareStatement( "DELETE tRelacionComprobacionComisiones where nidComisionModulo = ? and nFolioTramite = ? and cTipoTramite = ?" );
			pst3.setInt( 1, idComision );
			pst3.setInt( 2, folioPago );
			pst3.setString( 3, tipoPago );
			pst3.executeUpdate();

			// Calendario
			pst4 = conn.prepareStatement( "DELETE tPagoCalendario where nFolioPago = ? and cTipoPago = ?" );
			pst4.setInt( 1, folioPago );
			pst4.setString( 2, tipoPago );
			pst4.executeUpdate();

			// caso
			int idCaso = consultarCaso( conn, folioPago );
			pst5 = conn.prepareStatement( "DELETE CG_CASO_OPERACION WHERE ID_CASO = ?" );
			pst5.setInt( 1, idCaso );
			pst5.executeUpdate();

			pst11 = conn.prepareStatement( "DELETE CG_CASO_DATO WHERE ID_CASO = ?" );
			pst11.setInt( 1, idCaso );
			pst11.executeUpdate();

			pst7 = conn.prepareStatement( "DELETE CG_CASO WHERE ID_CASO = ?" );
			pst7.setInt( 1, idCaso );
			pst7.executeUpdate();

			pst6 = conn.prepareStatement( "DELETE tComisionComprobacion where nIdComision = ? and nFolioRelacion = ? and cTipoPago = ?" );
			pst6.setInt( 1, idComision );
			pst6.setInt( 2, folioPago );
			pst6.setString( 3, tipoPago );
			pst6.executeUpdate();

			// Pago
			pst8 = conn.prepareStatement( "DELETE tRELACIONGASTOSDetalle where nFolioRELACIONGASTOS = ?" );
			pst8.setInt( 1, folioPago );
			pst8.executeUpdate();

			pst9 = conn.prepareStatement( "DELETE tRELACIONGASTOSEncabezado where nFolioRELACIONGASTOS = ?" );
			pst9.setInt( 1, folioPago );
			pst9.executeUpdate();

			pst10 = conn.prepareStatement( "DELETE TRELACIONGASTOSRELACIONREEMPLAZO where nfolioReemplazada =  ?" );
			pst10.setInt( 1, folioPago );
			pst10.executeUpdate();

			pst12 = conn.prepareStatement( "DELETE tJustificacionRG where nFolioRELACIONGASTOS =  ?" );
			pst12.setInt( 1, folioPago );
			pst12.executeUpdate(); 
			
			
		} finally {
			CloseObject.closeObject( pst, false );
			CloseObject.closeObject( pst1, false );
			CloseObject.closeObject( pst2, false );
			CloseObject.closeObject( pst3, false );
			CloseObject.closeObject( pst4, false );
			CloseObject.closeObject( pst5, false );
			CloseObject.closeObject( pst6, false );
			CloseObject.closeObject( pst7, false );
			CloseObject.closeObject( pst8, false );
			CloseObject.closeObject( pst9, false );
			CloseObject.closeObject( pst10, false );
			CloseObject.closeObject( pst11, false );
			CloseObject.closeObject( pst12, false );
		}

	}

	private static int consultarCaso( Connection conn, int folioPago ) throws Exception {
		int caso = 0;
		PreparedStatement pst = null;
		ResultSet rs = null;

		try {
			pst = conn.prepareStatement( "SELECT ID_CASO from CG_CASO WITH (NOLOCK) where C_FOLIO like 'RELG-%-%' + ? AND ID_TC = " + idTC_RG );
			pst.setString( 1, String.valueOf( folioPago ) );
			rs = pst.executeQuery();

			if ( rs.next() ) {
				caso = rs.getInt( 1 );
			}

		} finally {
			CloseObject.closeObject( pst, false );
			CloseObject.closeObject( rs, false );
		}

		return caso;
	}

	public static int obtieneFolioCaja( Connection conn, int idComision ) throws Exception {
		int folio = 0;
		PreparedStatement pst = null;
		ResultSet rs = null;
		String query = "SELECT  MIN(nFolioTramite) nFolioTramite FROM tRelacionComprobacionComisiones  comp WITH (NOLOCK) "; 
		query += "		INNER JOIN tcajaencabezado caja WITH (NOLOCK) ON caja.nFoliocaja = comp.nFolioTramite" ; 
		query += "		WHERE cTipoTramite = 'CAJA' AND nidComisionModulo = ? AND (cdocumentohaplicado IS NULL  OR cdocumentohaplicado = 'S')";

		try {
			pst = conn.prepareStatement( query );
			pst.setInt( 1, idComision );

			rs = pst.executeQuery();
			log.debug( query );

			if ( rs.next() ) {
				folio = rs.getInt( "nFolioTramite" );
			}

			return folio;

		} finally {
			CloseObject.closeObject( pst, false );
			CloseObject.closeObject( rs, false );

		}
	}

	public static List<EgresoRetencion> cargaRetencionesResico( Connection conn, String tipoEgreso, int folioEgreso ) throws Exception {

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
		query.append( " FROM	vPagoRetencionResico WITH(NOLOCK) " );
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
			CloseObject.closeObject( rs, false );
			CloseObject.closeObject( ps, false );
		}
	}

	public static void agregarRetenciones( Connection conn, int folioPago, int ejercicio, BigDecimal iva, BigDecimal isr, String tipoPago, int tipoRetIva, int tipoRetIsr, int idConcepto ) throws Exception {
		PreparedStatement pst = null, pst2 = null;

		try {
			if ( iva.compareTo( BigDecimal.ZERO ) > 0 ) {
				pst = conn.prepareStatement( "INSERT INTO tPagoRetencion (cEjercicio, cTipoDocumento, cIdEntidadContable, nFolioPago, cIdTipoRetencion, mImporteRetencion, idConcepto) VALUES ( ?, ?, 10, ?, ?, ?, ?)" );
				pst.setInt( 1, ejercicio );
				pst.setString( 2, tipoPago );
				pst.setInt( 3, folioPago );
				pst.setInt( 4, tipoRetIva );
				pst.setBigDecimal( 5, iva );
				pst.setInt( 6, idConcepto );
				pst.executeUpdate();
			}

			if ( isr.compareTo( BigDecimal.ZERO ) > 0 ) {
				pst2 = conn.prepareStatement( "INSERT INTO tPagoRetencion (cEjercicio, cTipoDocumento, cIdEntidadContable, nFolioPago, cIdTipoRetencion, mImporteRetencion, idConcepto) VALUES ( ?, ?, 10, ?, ?, ?, ?)" );
				pst2.setInt( 1, ejercicio );
				pst2.setString( 2, tipoPago );
				pst2.setInt( 3, folioPago );
				pst2.setInt( 4, tipoRetIsr );
				pst2.setBigDecimal( 5, isr );
				pst2.setInt( 6, idConcepto );
				pst2.executeUpdate();
			}

		} finally {
			CloseObject.closeObject( pst, false );
			CloseObject.closeObject( pst2, false );
		}

	}

	public static void actualizaNombreComision( Connection conn, int idComision, String nombreNuevo, int idNombre ) throws Exception {
		PreparedStatement pst = null;

		String query = "UPDATE tComision SET cNombreComision = ?, nIdNombre = ? WHERE nIdComision = ?";

		try {
			pst = conn.prepareStatement( query );
			pst.setString( 1, nombreNuevo );
			pst.setInt( 2, idNombre );
			pst.setInt( 3, idComision );

			pst.executeUpdate();

		} finally {
			CloseObject.closeObject( pst, false );

		}

	}

	public static void actualizarNombreComisionViaticos( Connection conn, int idComision, String nombreNuevo ) throws Exception {
		PreparedStatement pst = null;
		String query = "";

		try {
			query = "UPDATE tViaticosComisiones SET cConcepto_Comision = ? WHERE  nIdComision = ? ";

			pst = conn.prepareStatement( query );
			pst.setString( 1, nombreNuevo );
			pst.setInt( 2, idComision );

			pst.executeUpdate();

		} finally {

			CloseObject.closeObject( pst, false );
		}

	}

	public static String consultaFechasCA( Connection conn, Agenda agenda, int empleado ) throws Exception {
		String mensaje = "";
		String url = ConfiguraAplicativoManager.getSystemSetting( conn, "URL_WS_CONSULTA_FORMATOS" );

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		String fechaInicio = sdf.format(agenda.getFechaInicio());
		String fechaFin = sdf.format(agenda.getFechaFin());

		ViaticoClient client = new ViaticoClient(url);
    	ViaticoRequest request = new ViaticoRequest(empleado, fechaInicio, fechaFin );

    	boolean existeFormato = client.verificarFormato(request, url);

    	if (existeFormato) {
    		mensaje = "No se puede guardar la Agenda ya que existe un formato en CONTROL DE ASISTENCIA en esas fechas.";
    	} 
		
		return mensaje;
	}

	public static int enviaConsultaFechas( Connection conn, int numEmpleado, Date fechaIni, Date fechaFin ) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		int tieneFormato = 0;
		StringBuilder query = new StringBuilder();

		try {
			query.append( "SELECT COUNT(*) tieneOtroFormato FROM formato  " );
			query.append( " WHERE numero_empleado = ? " );
			query.append( " AND ( ( ? between fecha_inicio and fecha_fin OR ? between fecha_inicio and fecha_fin)  " );
			query.append( " 		 OR ( fecha_inicio BETWEEN ? AND ? OR fecha_fin BETWEEN ? AND ? ) ) " );
			//query.append( " AND cat_tipo_formato_id <> 7" );
			query.append( " AND (bAceptado IS NULL OR bAceptado = 1) " );
			query.append( " AND (bAutoriza = 1 OR bAutoriza IS NULL ) " );

			pst = conn.prepareStatement( query.toString() );
			pst.setInt( 1, numEmpleado );
			pst.setDate( 2, fechaIni );
			pst.setDate( 3, fechaFin );
			pst.setDate( 4, fechaIni );
			pst.setDate( 5, fechaFin );
			pst.setDate( 6, fechaIni );
			pst.setDate( 7, fechaFin );

			rs = pst.executeQuery();

			if ( rs.next() ) {
				tieneFormato = rs.getInt( 1 );
			}

			return tieneFormato;

		} finally {
			CloseObject.closeObject( pst, false );
			CloseObject.closeObject( rs, false );
		}
	}

	public static int existeComisionGral( Connection conn, int idComision ) throws Exception {
		int folio = 0;
		PreparedStatement pst = null;
		ResultSet rs = null;
		StringBuilder query = new StringBuilder(); 
		query.append( " SELECT TOP 1 RCC.nIdComision , fInicio, ID_MUNICIPIO FROM tRelacionComprobacionComisiones RCC WITH (NOLOCK) " ); 
		query.append( "	INNER JOIN tViaticosComisiones COM WITH (NOLOCK) " );
		query.append( "		ON RCC.nIdComision = COM.nIdComision " );
		query.append( "	WHERE nidcomisionModulo = ?  " );
		query.append( "	ORDER BY RCC.nIdComision DESC " );

		try {
			pst = conn.prepareStatement( query.toString() );
			pst.setInt( 1, idComision );

			rs = pst.executeQuery();
			log.debug( query );

			if ( rs.next() ) {
				folio = rs.getInt( "nIdComision" );
			}

			return folio;

		} finally {
			CloseObject.closeObject( pst, false );
			CloseObject.closeObject( rs, false );

		}
	}

	public static ArrayList<Integer> viaticosConSaldo( Connection conn ) throws Exception {
		ArrayList<Integer> folios = new ArrayList<Integer>();
		PreparedStatement pst = null;
		ResultSet rs = null;

		StringBuilder query = new StringBuilder();
		query.append( " SELECT nidComisionModulo FROM ( SELECT nidComisionModulo," );
		query.append( "		CASE WHEN TIPO = 'A' THEN SUM(total) WHEN TIPO = 'X' THEN SUM(TOTALVIATICOS) ELSE 0 END ANTICIPOS," );
		query.append( "		CASE WHEN TIPO = 'D' THEN SUM(total) WHEN TIPO = 'X' THEN SUM(TOTALVIATICOS) ELSE 0 END DEV " );
		query.append( "	FROM v_RelacionComision WITH (NOLOCK) " );
		query.append( "	GROUP BY nidComisionModulo, TIPO ) AS TBL " );
		query.append( " INNER JOIN tComision COM WITH (NOLOCK) " );
		query.append( "	ON nIdComision = nidComisionModulo " );
		query.append( "	INNER JOIN v_AgendaDiasAcumulados AGENDA WITH (NOLOCK) " );
		query.append( "	ON COM.nIdComision = AGENDA.NIDCOMISION " );
		query.append( "	WHERE (cDocHaplicado NOT IN ('C', 'N', 'F')) and cNombreComision not like '%MAYA%' " );
		query.append( "	and  dbo.SUMA_DIAS_HABILES ( AGENDA.FFIN, 6) < GETDATE() " );
		query.append( "	GROUP BY nidComisionModulo, cRFC, cDocHaplicado,formato_id_ca, FINICIO, FFIN " );
		query.append( "	HAVING SUM(ANTICIPOS) - SUM(DEV) > 0 " );

		try {
			pst = conn.prepareStatement( query.toString() );

			rs = pst.executeQuery();
			log.debug( query );

			while ( rs.next() ) {
				folios.add( rs.getInt( "nidComisionModulo" ) );
			}

			return folios;

		} finally {
			CloseObject.closeObject( pst, false );
			CloseObject.closeObject( rs, false );

		}
	}

	public static void enviarCorreoTramitesRetraso( Connection conn, ArrayList<Integer> foliosPendientes, String estatus ) throws Exception {

		for ( int i = 0; i < foliosPendientes.size(); i++ ) {
			enviarCorreoTramitesRetraso( conn, foliosPendientes.get( i ), estatus );
			/*
			 * Duerme entre 0 y 1 seg. antes de enviar el siguiente correo para
			 * evitar la saturacion o que detecte como ataque el envio masivo.
			 */
			long sleep = Math.round( ( Math.random() * 1000 ) );
			System.out.println( "Durmiendo" + Math.round( ( Math.random() * 1000 ) ) + " ms antes de enviar correo siguiente" );
			Thread.sleep( sleep );
		}

	}

	public static int consultaPolizaTramite( Connection conn, int folioPago ) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		int nPoliza = 0;
		try {
			pst = conn.prepareStatement( "SELECT nFolioPoliza FROM tRELACIONGASTOSEncabezado WITH (NOLOCK) WHERE nFolioRELACIONGASTOS = ?" );
			pst.setInt( 1, folioPago );

			rs = pst.executeQuery();

			if ( rs.next() ) {
				nPoliza = rs.getInt( 1 );
			}

		} finally {
			CloseObject.closeObject( pst, false );
			CloseObject.closeObject( rs, false );
		}

		return nPoliza;
	}

	public static int revisarPagos( Connection conn, int folio ) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		int nPagos = 0;
		try {
			pst = conn.prepareStatement( "SELECT COUNT(*) FROM v_RelacionComision WHERE cTipoTramite IN ('CAJA', 'RELACIONGASTOS') AND nidComisionModulo = ?" );
			pst.setInt( 1, folio );

			rs = pst.executeQuery();

			if ( rs.next() ) {
				nPagos = rs.getInt( 1 );
			}

		} finally {
			CloseObject.closeObject( pst, false );
			CloseObject.closeObject( rs, false );
		}

		return nPagos;
	}

	public static boolean validaVacaciones( Connection conn, int idEmpleado ) throws Exception {
		String url = ConfiguraAplicativoManager.getSystemSetting( conn, "URL_WS_CONSULTA_VACACIONES" );
		
		ViaticoClient client2 = new ViaticoClient(url);
    	boolean tieneVacaciones = client2.verificarVacaciones(idEmpleado, url);
		
		return tieneVacaciones;
	}

	public static int consultaEmpleadoFirmante( Connection conn, int folioComision ) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		int idEmpleado = 0;
		try {
			pst = conn.prepareStatement( "SELECT TOP 1 nidEmpleado FROM tfirmantesviaticos WITH (NOLOCK) WHERE nfolio = ? AND cAutorizado = 'N' order by nRenglon" );
			pst.setInt( 1, folioComision );

			rs = pst.executeQuery();

			if ( rs.next() ) {
				idEmpleado = rs.getInt( 1 );
			}

		} finally {
			CloseObject.closeObject( pst, false );
			CloseObject.closeObject( rs, false );
		}

		return idEmpleado;
	}
	
	public static int tieneCertificado( Connection conn, int idAgenda ) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		int certificado = 0;
		try {
			pst = conn.prepareStatement( "SELECT COUNT(*) FROM tViaticos viat WITH (NOLOCK) left join tCatPaquetesComision paquete WITH (NOLOCK)  on viat.nIdPaquete = paquete.nIdPaquete where  cNombre like 'CERTIFICADO%TRANSITO%' AND viat.nidAgenda = ?" );
			pst.setInt( 1, idAgenda );

			rs = pst.executeQuery();

			if ( rs.next() ) {
				certificado = rs.getInt( 1 );
			}

		} finally {
			CloseObject.closeObject( pst, false );
			CloseObject.closeObject( rs, false );
		}

		return certificado;
		
	}

	public static void actualizarJustificacionTicket (Connection conn, String folio,  String mensaje, String tieneTicket, String tieneBoleto ) throws Exception {
		PreparedStatement pst = null;
		
		try {
			int existe = tieneJustificacion(conn, folio);
			
			if (existe > 0) {
				pst = conn.prepareStatement( "UPDATE tJustificacionRG SET cJustificaTicket = ?, cTieneAdjuntoTicket = ? WHERE nFolioRELACIONGASTOS = ?" );
				pst.setString( 1, mensaje );
				pst.setString( 2, tieneTicket );
				pst.setString( 3, folio );	
				
				pst.executeUpdate();
			
			} else {
				insertaJustificacion (conn, folio, mensaje, "" , tieneTicket, tieneBoleto, "");
			}
			
		} finally {
			CloseObject.closeObject( pst, false );
			
		}
	}
	
	public static void actualizarOtraJustificacion (Connection conn, String folio,  String mensaje ) throws Exception {
		PreparedStatement pst = null;
		
		try {
			int existe = tieneJustificacion(conn, folio);
			
			if (existe > 0) {
				pst = conn.prepareStatement( "UPDATE tJustificacionRG SET cOtraJustificacion = ? WHERE nFolioRELACIONGASTOS = ?" );
				pst.setString( 1, mensaje );
				pst.setString( 2, folio );	
				
				pst.executeUpdate();
			
			} else {
				insertaJustificacion (conn, folio, "", "",  "0", "0", mensaje);
			}
			
		} finally {
			CloseObject.closeObject( pst, false );
			
		}
	}
	public static void actualizarJustificacionBoleto (Connection conn, String folio,  String mensaje, String tieneTicket, String tieneBoleto ) throws Exception {
		PreparedStatement pst = null;
		
		try {
			int existe = tieneJustificacion(conn, folio);
			
			if (existe > 0) {
				pst = conn.prepareStatement( "UPDATE tJustificacionRG SET cJustificaBoletos = ?, cTieneAdjuntoBoletos = ? WHERE nFolioRELACIONGASTOS = ?" );
				pst.setString( 1, mensaje );
				pst.setString( 2, tieneBoleto );
				pst.setString( 3, folio );
				
				
				pst.executeUpdate();
			
			} else {
				insertaJustificacion (conn, folio, "" , mensaje, tieneTicket, tieneBoleto, "");
			}
			
		} finally {
			CloseObject.closeObject( pst, false );
			
		}
	}
	
	
	public static void insertaJustificacion (Connection conn, String folio, String mensajeTicket, String mensajeBoleto, String tieneTicket, String tieneBoleto , String otraJust) throws Exception {
		PreparedStatement pst = null;
		
		try {
			pst = conn.prepareStatement( "INSERT INTO tJustificacionRG (nFolioRELACIONGASTOS, cJustificaTicket , cJustificaBoletos, cTieneAdjuntoTicket, cTieneAdjuntoBoletos, cOtraJustificacion ) VALUES ( ?, ?, ?, ?, ?, ? )" );
			pst.setString( 1, folio );
			pst.setString( 2, mensajeTicket );
			pst.setString( 3, mensajeBoleto );
			pst.setString( 4, tieneTicket );
			pst.setString( 5, tieneBoleto );
			pst.setString( 6, otraJust );
			
			pst.executeUpdate();
					
		} finally {
			CloseObject.closeObject( pst, false );
			
		}
	}
	
	public static void insertaJustificacionGasolina (Connection conn, String folio, String mensaje, int kmInicial, int kmFinal, String litros, String rendimiento) throws Exception {
		PreparedStatement pst = null;
		
		try {
			pst = conn.prepareStatement( "INSERT INTO tJustificacionRG (nFolioRELACIONGASTOS, cJustificacionGasolina, mKmInicial, mKmFinal, mLitros, mRendimiento ) VALUES ( ?, ?, ?, ?, ?, ? )" );
			pst.setString( 1, folio );
			pst.setString( 2, mensaje );
			pst.setInt( 3, kmInicial );
			pst.setInt( 4, kmFinal );
			pst.setString( 5, litros );
			pst.setString( 6, rendimiento );
			
			pst.executeUpdate();
					
		} finally {
			CloseObject.closeObject( pst, false );
			
		}
	}
	
	public static int tieneJustificacion (Connection conn, String folio) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		int tieneJustificacion = 0;
		
		try {
			pst = conn.prepareStatement( "SELECT COUNT(*) FROM tJustificacionRG WITH (NOLOCK) where nfolioRelacionGastos = ?" );
			pst.setString( 1, folio );
			
			rs = pst.executeQuery();
			
			if (rs.next()) {
				tieneJustificacion =  rs.getInt( 1 );
			}
			
			return tieneJustificacion;
			
		} finally {
			CloseObject.closeObject( pst, false );
			
		}
	}
	
	public static String generarFolioCertificado( Connection conn, int idComision ) throws Exception {
		PreparedStatement pst = null, pst2 = null;
		ResultSet rs = null;
		String folioCer = "";
		
		try {
			pst = conn.prepareStatement( "SELECT  seq_name + '-'+ RIGHT('000' + CAST( seq_value + 1 AS varchar(4)), 3) + '-' + CAST((SELECT aEjercicioFiscal FROM tEjercicioFiscal WITH (NOLOCK) WHERE cActivo = 1) AS varchar(4)) folio FROM CF_SEQUENCE SEQ WITH (NOLOCK) "
					+ " WHERE seq_name = (SELECT 'CERT-' + cUnidadResponsable FROM tComision WITH (NOLOCK) WHERE nIdComision = ? )" );
			pst.setInt( 1, idComision );

			rs = pst.executeQuery();

			if ( rs.next() ) {
				folioCer = rs.getString( 1 );
			}
			
			//Actualizar el folio del certificado
			pst2 = conn.prepareStatement( "UPDATE tcomision set cFolioCertificado = ? WHERE nIdComision = ?" );
			pst2.setString( 1, folioCer );
			pst2.setInt( 2, idComision );
			
			pst2.executeUpdate();
			
			actualizarCFSequenceCertificado(conn, idComision);

		} finally {
			CloseObject.closeObject( pst, false );
			CloseObject.closeObject( rs, false );
			CloseObject.closeObject( pst2, false );
		}

		return folioCer;
		
	}

	private static void actualizarCFSequenceCertificado( Connection conn, int idComision ) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try {
			pst = conn.prepareStatement( " UPDATE CF_SEQUENCE SET seq_value = seq_value + 1  WHERE seq_name = (SELECT 'CERT-' + cUnidadResponsable FROM tComision WITH (NOLOCK) WHERE nIdComision = ? ) " );
			pst.setInt( 1, idComision );

			pst.executeUpdate();			
			
		} finally {
			CloseObject.closeObject( pst, false );
			CloseObject.closeObject( rs, false );
		
		}
		
	}

	public static boolean certificadoGuardado( Connection conn, int idComision ) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		boolean tieneCer =false;
		
		try {
			pst = conn.prepareStatement( "SELECT cFolioCertificado FROM tComision WITH (NOLOCK) WHERE nIdComision = ? " );
			pst.setInt( 1, idComision );

			rs = pst.executeQuery();

			if ( rs.next() ) {
				String folio = rs.getString( 1 );
				
				if (folio != null ) {
					tieneCer = true;
				}
			}
			
		} finally {
			CloseObject.closeObject( pst, false );
			CloseObject.closeObject( rs, false );
		
		}

		return tieneCer;
	}

	public static String  validaPartidasvsComprobacion( Connection conn, int folio ) throws Exception{		
		PreparedStatement pst = null;
		ResultSet rs = null;
		String tipo = "";
		BigDecimal diferencia = new BigDecimal("0.00");
		StringBuilder resultado = new StringBuilder();
		
		try {
			resultado.setLength(0);
			resultado.append( "" );
			pst = conn.prepareStatement( "SELECT cTipo FROM tCatalogoConceptosComprobacion WITH (NOLOCK) " );
			rs = pst.executeQuery();
			
			while (rs.next()) {
                tipo = rs.getString("cTipo");
                
                //Consulta que conceptos tiene para iniciar las validaciones
                if (consultaPorTipoComprobacion ( conn, folio, tipo) ) {
                
                	//Valida el importe del Detalle del viatico con las partidas
                	 diferencia = validarImportesComprobacion (conn, folio, tipo);
                	 
                	 if (diferencia.compareTo(BigDecimal.ZERO) != 0) {
                	     BigDecimal redondeado = diferencia.setScale(2, RoundingMode.HALF_UP);
                		 resultado.append( "La diferencia es de " + redondeado.toString() + " en el tipo: "  + tipo + ". ");
                     } 
                }
            }
			
			return resultado.toString();
			
		} finally {
			CloseObject.closeObject( pst, false );
			CloseObject.closeObject( rs, false );
		
		}
		
	}
	
	private static BigDecimal validarImportesComprobacion( Connection conn, int folio, String concepto ) throws Exception{
		PreparedStatement pst = null, pst2 = null;
		ResultSet rs = null, rs2 = null;
		BigDecimal diferencia = new BigDecimal("0.00");
		String totalComprobacion = "", totalPartidas = "";
		BigDecimal gasolina = new BigDecimal("0.00");
		BigDecimal bdTotalGasolina = new BigDecimal("0.00");
		
		try {
			String columnas = consultaColumnas (conn, concepto);
			String query = "SELECT ("  + columnas + ") mTotal FROM tComisionComprobacion  WITH (NOLOCK) WHERE nFolioRelacion = ? AND cTipoPago = 'RELACIONGASTOS'";
			
			pst = conn.prepareStatement( query );
			pst.setInt( 1, folio );
			
			rs = pst.executeQuery();
			
			if (rs.next()) {
				totalComprobacion = rs.getString( "mTotal" );
			}
			
			StringBuilder consulta = new StringBuilder();
			consulta.append( " SELECT ISNULL(SUM(mImporteBruto - mImporteRetencion),0) Importes " );
			consulta.append( " FROM tPagoCalendario WITH (NOLOCK) WHERE cTipoPago = 'RELACIONGASTOS' and nFolioPago = ?" );
			consulta.append( " AND SUBSTRING(ep,32,5) IN ( SELECT cPartidas FROM tPartidasComprobacion  WITH (NOLOCK) WHERE cTipo = ? ) " );
			
			pst2 = conn.prepareStatement( consulta.toString() );
			pst2.setInt( 1, folio );
			pst2.setString( 2, concepto );
			
			rs2 = pst2.executeQuery();
			
			if (rs2.next()) {
				totalPartidas = rs2.getString( "Importes" );
			}			
			
			BigDecimal bdTotalComprobacion = new BigDecimal(totalComprobacion);
			BigDecimal bdTotalPartidas = new BigDecimal(totalPartidas);
			
			int tieneGas = tienePartidaNoViaticos (conn, folio);
			
			/* Si tiene importe de Gasolina y la partida 33602 hay que restarlo porque comparten las partidas de Viaticos*/
			if ("Viaticos".equals( concepto )) {
				if (tieneGas == 1 ) {
					gasolina = consultaImporteGasolina (conn, folio);
					bdTotalGasolina =  (BigDecimal) gasolina;
					bdTotalComprobacion = bdTotalComprobacion.subtract( bdTotalGasolina );
				}
			}
			
			/* Si tiene importe de Gasolina y solo esta en las partidas de viaticos hay que restarlo para que no se dupliquen*/
			if ("Gasolina".equals( concepto )) {
				int tieneOtrosConceptos = tieneOtrosConceptosPasaje (conn, folio);
				BigDecimal importeViat = consultaImporteViaticos(conn, folio);
				if (tieneGas == 0 && tieneOtrosConceptos == 0 ) {
					bdTotalComprobacion = BigDecimal.ZERO;
				} else if ( (tieneGas == 1 && tieneOtrosConceptos == 1) || (tieneGas == 0 && tieneOtrosConceptos == 1 ) ||  (importeViat.compareTo(BigDecimal.ZERO) > 0 ) ) {
					bdTotalComprobacion = consultaImporteViaticosGasolina (conn, folio);
					bdTotalPartidas = sumaTotalViaticos(conn, folio);
				}
			}
	        
	        diferencia = bdTotalPartidas.subtract(bdTotalComprobacion);
			
			return diferencia;
			
		} finally {
			CloseObject.closeObject( pst, false );
			CloseObject.closeObject( rs, false );
			CloseObject.closeObject( pst2, false );
			CloseObject.closeObject( rs2, false );
		}
	}
	
	private static String consultaColumnas( Connection conn, String concepto ) throws Exception{
		PreparedStatement pst = null;
		ResultSet rs = null;
		String resultado = "";
		try {
			pst = conn.prepareStatement( "SELECT  cConcepto FROM tCatalogoConceptosComprobacion  WITH (NOLOCK) where cTipo = ?" );
			pst.setString( 1, concepto );
			
			rs = pst.executeQuery();
			
			if (rs.next()) {
				resultado = rs.getString( "cConcepto" );
			}
			
			return resultado;
			
		} finally {
			CloseObject.closeObject( pst, false );
			CloseObject.closeObject( rs, false );
		
		}
	}
	

	private static BigDecimal consultaImporteGasolina( Connection conn, int folio) throws Exception{
		PreparedStatement pst = null;
		ResultSet rs = null;
		BigDecimal resultado = new BigDecimal("0.00");
		
		try {
			int tieneGas = tienePartidaNoViaticos (conn, folio);
				
			if (tieneGas >  0 ) {
					StringBuilder query2 = new StringBuilder();
					query2.append( " SELECT ISNULL(SUM(mImporteBruto - mImporteRetencion),0) importeGasolina " );
					query2.append( " FROM tPagoCalendario WITH (NOLOCK)" );
					query2.append( " WHERE cTipoPago = 'RELACIONGASTOS' AND nFolioPago = ? " );
					query2.append( " AND SUBSTRING(ep,32,5) IN ( SELECT cPartidas FROM tPartidasComprobacion  WITH (NOLOCK) WHERE cTipo = 'Gasolina' ) " );
					
					pst = conn.prepareStatement( query2.toString() );
					pst.setInt( 1, folio );
					
					rs = pst.executeQuery();
					
					if (rs.next()) {
						resultado = rs.getBigDecimal( 1 ) ; 
					}
			}
			
			return resultado;
			
		} finally {
			CloseObject.closeObject( pst, false );
			CloseObject.closeObject( rs, false );
			
		}
	}


	private static BigDecimal consultaImporteViaticos( Connection conn, int folio) throws Exception{
		PreparedStatement pst = null;
		ResultSet rs = null;
		BigDecimal resultado = new BigDecimal("0.00");
		
		try {
			int tieneGas = tienePartidaNoViaticos (conn, folio);
				
			if (tieneGas >  0 ) {
					StringBuilder query2 = new StringBuilder();
					query2.append( " SELECT ISNULL(SUM(mImporteBruto - mImporteRetencion),0) importe " );
					query2.append( " FROM tPagoCalendario WITH (NOLOCK)" );
					query2.append( " WHERE cTipoPago = 'RELACIONGASTOS' AND nFolioPago = ? " );
					query2.append( " AND SUBSTRING(ep,32,5) IN ( SELECT cPartidas FROM tPartidasComprobacion  WITH (NOLOCK) WHERE cTipo = 'Viaticos' ) " );
					
					pst = conn.prepareStatement( query2.toString() );
					pst.setInt( 1, folio );
					
					rs = pst.executeQuery();
					
					if (rs.next()) {
						resultado = rs.getBigDecimal( 1 ) ; 
					}
			}
			
			return resultado;
			
		} finally {
			CloseObject.closeObject( pst, false );
			CloseObject.closeObject( rs, false );
			
		}
	}
	public static int tienePartidaNoViaticos( Connection conn, int folio) throws Exception{
		PreparedStatement pst = null;
		ResultSet rs = null;
		int resultado = 0;
		StringBuilder query = new StringBuilder();
		
		try {
			query.append( "SELECT COUNT(*) tieneGasolina FROM tPagoCalendario WITH (NOLOCK)" );
			query.append( " WHERE cTipoPago = 'RELACIONGASTOS' AND nFolioPago = ? " );
			query.append( " AND SUBSTRING(ep,32,5) IN ( SELECT cPartidas FROM tPartidasComprobacion  WITH (NOLOCK) WHERE cTipo = 'Gasolina' ) " );
			
			pst = conn.prepareStatement( query.toString() );
			pst.setInt( 1, folio );
			
			rs = pst.executeQuery();
			
			if (rs.next()) {
				resultado = rs.getInt( "tieneGasolina" );
			}
			
			return resultado;
			
		} finally {
			CloseObject.closeObject( pst, false );
			CloseObject.closeObject( rs, false );
			
		}
	}
	

	private static BigDecimal consultaImporteViaticosGasolina( Connection conn, int folio) throws Exception{
		PreparedStatement pst = null;
		ResultSet rs = null;
		BigDecimal resultado = new BigDecimal("0.00");
		
		try {
			StringBuilder query2 = new StringBuilder();
			query2.append( " SELECT ISNULL(SUM(mImporteBruto - mImporteRetencion),0) importeGasolina " );
			query2.append( " FROM tPagoCalendario WITH (NOLOCK)" );
			query2.append( " WHERE cTipoPago = 'RELACIONGASTOS' AND nFolioPago = ? " );
			query2.append( " AND SUBSTRING(ep,32,5) IN ( SELECT cPartidas FROM tPartidasComprobacion  WITH (NOLOCK) WHERE cTipo in ('Gasolina', 'Viaticos') )  " );
			
			pst = conn.prepareStatement( query2.toString() );
			pst.setInt( 1, folio );
			
			rs = pst.executeQuery();
			
			if (rs.next()) {
				resultado = rs.getBigDecimal( 1 ) ; 
			}
			
			return resultado;
			
		} finally {
			CloseObject.closeObject( pst, false );
			CloseObject.closeObject( rs, false );
			
		}
	}
	
	public static int tieneOtrosConceptosPasaje( Connection conn, int folio) throws Exception{
		PreparedStatement pst = null;
		ResultSet rs = null;
		int resultado = 0;
		StringBuilder query = new StringBuilder();
		
		try {
			query.append( " SELECT CASE WHEN ( mPasaje + mTaxi + mTaxiLocal + mPasajelocal ) > 0 THEN 1 ELSE 0 END tieneOtrosConceptos " );
			query.append( " FROM tcomisioncomprobacion WITH (NOLOCK )  " );
			query.append( " WHERE nfoliorelacion = ? " );
			
			pst = conn.prepareStatement( query.toString() );
			pst.setInt( 1, folio );
			
			rs = pst.executeQuery();
			
			if (rs.next()) {
				resultado = rs.getInt( "tieneOtrosConceptos" );
			}
			
			return resultado;
			
		} finally {
			CloseObject.closeObject( pst, false );
			CloseObject.closeObject( rs, false );
			
		}
	}
	
	
	public static BigDecimal sumaTotalViaticos( Connection conn, int folio) throws Exception{
		PreparedStatement pst = null;
		ResultSet rs = null;
		BigDecimal resultado = new BigDecimal("0.00");
		StringBuilder query = new StringBuilder();
		
		try {
			query.append( " SELECT ( mPasaje + mTaxi + mTaxiLocal + mPasajelocal + mPeaje + mGasolinaLocal + mPeajeLocal + mMaritimoLocal + mAereoLocal ) suma " );
			query.append( " FROM tComisionComprobacion WITH (NOLOCK )  " );
			query.append( " WHERE nFolioRelacion = ? " );
			
			pst = conn.prepareStatement( query.toString() );
			pst.setInt( 1, folio );
			
			rs = pst.executeQuery();
			
			if (rs.next()) {
				resultado = rs.getBigDecimal( "suma" );
			}
			
			return resultado;
			
		} finally {
			CloseObject.closeObject( pst, false );
			CloseObject.closeObject( rs, false );
			
		}
	}
	
	public static boolean consultaPorTipoComprobacion( Connection conn, int folio, String Tipo ) throws Exception{		
		PreparedStatement pst = null;
		ResultSet rs = null;
		StringBuilder query = new StringBuilder();
		boolean resultado = false;
		try {
			query.append ( " DECLARE @output int" );
			query.append ( " EXEC sp_tipoComprobacion  ?, ?, @output OUTPUT " );
			query.append ( " SELECT @output resultado" );
			
			pst = conn.prepareStatement( query.toString() );
			pst.setInt( 1, folio );
			pst.setString( 2, Tipo );
			
			rs = pst.executeQuery();
			
			if (rs.next()) {
				String res = rs.getString( "resultado" );
				if ( "1".equals( res ) ) {
					resultado = true;
				}
			}
			
			return resultado;
			
		} finally {
			CloseObject.closeObject( pst, false );
			CloseObject.closeObject( rs, false );
		
		}
		
	}

	public static void actualizarJustificacionGasolina( Connection conn, String folio, int kmIni, int kmFin, String litros, String rendimiento, String mensaje ) throws Exception {
		PreparedStatement pst = null;
		
		try {
			int existe = tieneJustificacion(conn, folio);
			
			if (existe > 0) {
				pst = conn.prepareStatement( "UPDATE tJustificacionRG SET mKmInicial = ?, mKmFinal = ?, mLitros = ?, mRendimiento = ?, cJustificacionGasolina = ? WHERE nFolioRELACIONGASTOS = ?" );
				pst.setInt( 1, kmIni );
				pst.setInt( 2, kmFin );
				pst.setString( 3, litros );
				pst.setString( 4, rendimiento );
				pst.setString( 5, mensaje );
				pst.setString( 6, folio );	
				
				pst.executeUpdate();
			
			} else {
				insertaJustificacionGasolina (conn, folio, mensaje, kmIni, kmFin, litros, rendimiento );
			}
			
			recalcularRendimiento(conn, folio);
			
		} finally {
			CloseObject.closeObject( pst, false );
			
		}
		
	}
	
	private static void recalcularRendimiento(Connection conn, String folio) throws Exception {
		PreparedStatement pst = null;
		
		try {
			pst = conn.prepareStatement( "UPDATE tJustificacionRG SET  mRendimiento = ( mKmFinal - mKmInicial )  / mLitros WHERE nFolioRELACIONGASTOS = ?" );
			pst.setString( 1, folio );	
			
			pst.executeUpdate();
			
		} finally {
			CloseObject.closeObject( pst, false );
			
		}
	}
}
