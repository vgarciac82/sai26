package com.syc.contable;


import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.axtel.presupuesto.AdecuacionCancelacion;
import com.axtel.presupuesto.AdecuacionRespuestaDAO;
import com.axtel.presupuesto.AdecuacionResumen;
import com.axtel.ws.clients.AdecuacionRespuesta;
import com.axtel.ws.clients.NotificaAdecuacionMetasCliente;
import com.axtel.ws.exceptions.WSException;
import com.syc.contable.adecuaciones.Adecuacion;
import com.syc.contable.adecuaciones.AdecuacionEncabezado;
import com.syc.contable.adecuaciones.AdecuacionEncabezadoManager;
import com.syc.contable.adecuaciones.ClasificacionAdecuacion;
import com.syc.contable.adecuaciones.UsuarioSiplan;
import com.syc.contable.adecuaciones.ValidacionAdecuacionesBusinessLogic;
import com.syc.contable.adecuaciones.ValidacionAdecuacionesManager;
import com.syc.contable.adecuaciones.Exception.IADEClavesNeteadasException;
import com.syc.contable.adecuaciones.core.Fap01;
import com.syc.contable.core.AdecuacionCalendario;
import com.syc.contable.core.AdecuacionManager;
import com.syc.contable.core.AplicacionContable;
import com.syc.contable.core.AplicacionContable.AplicarContableReturn;
import com.syc.contable.core.CorreoAdecuacion;
import com.syc.contable.core.CorreosJefatura;
import com.syc.contable.core.FIAFEncabezado;
import com.syc.contable.core.PresupuestoManager;
import com.syc.contable.core.ResultadoSaldos;
import com.syc.contable.core.ResultadoValidacionAdecuacion;
import com.syc.contable.core.Saldo;
import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.core.AlarmaManager;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoDato;
import com.syc.gestion.core.CasoDatoManager;
import com.syc.gestion.core.CasoManager;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.core.Grupo;
import com.syc.gestion.core.GrupoManager;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.core.UsuarioManager;
import com.syc.gestion.reportes.core.ReporteManager;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.obrapublica.core.ConfiguraAplicativoManager;
import com.syc.sai.contabilidad.caja.CajaManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import com.syc.utils.mail.MailSender;


public class AdecuacionBusinessLogic extends DataSourceManager {

	private static Logger			log							= Logger.getLogger( AdecuacionBusinessLogic.class );
	public boolean					correoProduccion			= false;
	private String					jniName						= "";

	private String					WS_ADECUACION_SIPLAN		= null;
	private String					WS_CANCEL_ADECUACION_SIPLAN	= null;

	NotificaAdecuacionMetasCliente	notificaAdecuacionMetasCliente;

	public AdecuacionBusinessLogic( String jniName ) {
		try {
			this.jniName = jniName;
			super.init( jniName );
			setURLAdecuacionService();
			notificaAdecuacionMetasCliente = new NotificaAdecuacionMetasCliente( WS_ADECUACION_SIPLAN );
		} catch ( Exception e ) {
			throw new RuntimeException( e.getMessage(), e.getCause() );
		}
	}

	public AdecuacionBusinessLogic( ) {
		try {
			setURLAdecuacionService();
			notificaAdecuacionMetasCliente = new NotificaAdecuacionMetasCliente( WS_ADECUACION_SIPLAN );

		} catch ( Exception e ) {
			throw new RuntimeException( e.getMessage(), e.getCause() );
		}
	}

	private void setURLAdecuacionService() throws Exception {
		Connection conn = null;
		try {
			conn = getConnection();
			WS_ADECUACION_SIPLAN = ConfiguraAplicativoManager.getSystemSetting( conn, "WS_ADECUACION" );
			WS_CANCEL_ADECUACION_SIPLAN = ConfiguraAplicativoManager.getSystemSetting( conn, "WS_CANCEL_ADECUACION" );
		} finally {
			CloseObject.closeObject( conn );

		}
	}

	public static String[] readfAplicacion( String nFolio, String cCentroContable, String cUR ) {
		String[] fechas = new String [3];// 0=fAplicacion,1=minDate,2=MaxDate
		int APLICACION = 0;
		int MINIMA = 1;
		int MAXIMA = 2;
		String UR = null;

		int mesAbierto = -1;
		try {
			AdecuacionBusinessLogic abl = new AdecuacionBusinessLogic( GestionInterface.ATT_CONEXION );
			int efa = Integer.parseInt( abl.obtenEjercicioFiscal() );

			fechas[APLICACION] = CajaManager.ejecutaQueryRS( "SELECT isnull(convert(varchar, fAplicacion,103),'') fAplicacion  FROM dbo.tAdecuacionEncabezado  with(nolock) where nFolioAdecuacion =" + nFolio );
			UR = CajaManager.ejecutaQueryRS( "SELECT cUnidadResponsable FROM dbo.tAdecuacionEncabezado  with(nolock) where nFolioAdecuacion =" + nFolio );

			if ( "".equals( UR ) ) {
				UR = cUR;
			}
			mesAbierto = CajaManager.ejecutaQueryRI( "select TOP 1 nMes from tMesesContables with(nolock) where mesAbierto='S' and cCentroContable='" + cCentroContable + "' AND cUnidadResponsable='" + UR + "'" );

			String DATE_FORMAT = "dd/MM/yyyy";
			SimpleDateFormat sdf = new SimpleDateFormat( DATE_FORMAT );
			Calendar c1 = Calendar.getInstance();

			if ( efa == c1.get( Calendar.YEAR ) )
				fechas[MAXIMA] = c1.get( Calendar.YEAR ) + "," + ( c1.get( Calendar.MONTH ) ) + "," + c1.getActualMaximum( Calendar.DAY_OF_MONTH );
			else
				fechas[MAXIMA] = efa + "," + ( 11 ) + "," + c1.getActualMaximum( Calendar.DAY_OF_MONTH );

			if ( fechas[APLICACION].equals( "" ) ) {
				if ( efa == c1.get( Calendar.YEAR ) )
					fechas[APLICACION] = sdf.format( c1.getTime() );
				else
					fechas[APLICACION] = "31/12/" + efa;

				int mesActual = c1.get( Calendar.MONTH ) + 1;

				if ( mesAbierto != mesActual ) {
					c1.set( Calendar.MONTH, mesAbierto - 1 );
					c1.set( Calendar.DAY_OF_MONTH, c1.getActualMaximum( Calendar.DAY_OF_MONTH ) );
					if ( efa != c1.get( Calendar.YEAR ) )
						c1.set( Calendar.YEAR, efa );
				}

			} else
				c1.setTime( sdf.parse( fechas[APLICACION] ) );

			fechas[MINIMA] = c1.get( Calendar.YEAR ) + "," + ( mesAbierto - 1 ) + "," + c1.getActualMinimum( Calendar.DAY_OF_MONTH );

		} catch ( Exception e ) {
			log.error( e, e );
		}
		return fechas;
	}

	public static String[] readfAplicacion( String cCentroContable ) {
		String[] fechas = new String [3];// 0=fAplicacion,1=minDate,2=MaxDate
		int APLICACION = 0;
		int MINIMA = 1;
		int MAXIMA = 2;

		int mesAbierto = -1;
		try {
			AdecuacionBusinessLogic abl = new AdecuacionBusinessLogic( GestionInterface.ATT_CONEXION );
			int efa = Integer.parseInt( abl.obtenEjercicioFiscal() );

			mesAbierto = CajaManager.ejecutaQueryRI( "select nMes from tMesesContables with(nolock) where mesAbierto='S' and cCentroContable='" + cCentroContable + "'" );

			String DATE_FORMAT = "dd/MM/yyyy";
			SimpleDateFormat sdf = new SimpleDateFormat( DATE_FORMAT );
			Calendar c1 = Calendar.getInstance();

			if ( efa != c1.get( Calendar.YEAR ) ) {
				c1.set( Calendar.DAY_OF_MONTH, 31 );
				c1.set( Calendar.MONTH, 11 );
				c1.set( Calendar.YEAR, efa );

			}

			fechas[MAXIMA] = c1.get( Calendar.YEAR ) + "," + ( c1.get( Calendar.MONTH ) ) + "," + c1.getActualMaximum( Calendar.DAY_OF_MONTH );

			int mesActual = c1.get( Calendar.MONTH ) + 1;
			fechas[APLICACION] = sdf.format( c1.getTime() );

			if ( mesAbierto != mesActual ) {
				c1.set( Calendar.MONTH, mesAbierto - 1 );
				c1.set( Calendar.DAY_OF_MONTH, c1.getActualMaximum( Calendar.DAY_OF_MONTH ) );
			}

			fechas[MINIMA] = c1.get( Calendar.YEAR ) + "," + ( c1.get( Calendar.MONTH ) ) + "," + c1.getActualMinimum( Calendar.DAY_OF_MONTH );

		} catch ( Exception e ) {
			log.error( e, e );
		}
		return fechas;
	}

	public static String readfCaptura( String nFolio, String cCentroContable ) {
		String fCaptura = "";

		try {
			fCaptura = CajaManager.ejecutaQueryRS( "SELECT isnull(convert(varchar, fAplicacion,103),'') fAplicacion  FROM dbo.tAdecuacionEncabezado  with(nolock) where nFolioAdecuacion =" + nFolio );

			// AdecuacionBusinessLogic abl = new
			// AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);

			if ( "".equals( fCaptura ) ) {
				String DATE_FORMAT = "dd/MM/yyyy";
				SimpleDateFormat sdf = new SimpleDateFormat( DATE_FORMAT );
				Calendar c1 = Calendar.getInstance();

				fCaptura = sdf.format( c1.getTime() );
			}

		} catch ( Exception e ) {
			log.error( e, e );
		}

		return fCaptura;

	}

	public String readfCancelacion( String nFolio ) throws Exception {
		String fCaptura = "";
		String fecha[] = new String [3];

		try {
			fCaptura = CajaManager.ejecutaQueryRS( "SELECT convert(varchar, fCancelacion,103) fCancelacion  FROM dbo.tAdecuacionEncabezado  with(nolock) where nFolioAdecuacion =" + nFolio );
			fecha = fCaptura.split( "/" );
			fCaptura = fecha[2] + "/" + fecha[1] + "/" + fecha[0];
		} catch ( Exception e ) {
			log.error( e, e );
			throw e;
		}

		return fCaptura;

	}

	public ResultadoValidacionAdecuacion validaArchivoExcel( String archivo, Caso c, Usuario usuario, String cSuperReduccion, String cSRInterna ) throws Exception {
		ValidacionAdecuacionesBusinessLogic vabl = new ValidacionAdecuacionesBusinessLogic( this.jniName );
		Adecuacion adecuacion = null;

		Connection conn = null;

		String msjUnidadResp = "";
		String unidadResp = "";
		String usuarioLogin = "";
		String msjRamo = "";
		String msjMonto = "";
		String tipoAdecuacion = "";
		String cEjercicioFiscal;
		// JSONObject json = new JSONObject();

		ArrayList<String> msjSaldos = new ArrayList<String>();
		ArrayList<String> mensajesValidacion = new ArrayList<String>();
		ArrayList<String> mensajesAdvertencia = new ArrayList<String>();

		int nFolioAdecuacion = Integer.parseInt( c.getFolio().substring( c.getFolio().lastIndexOf( '-' ) + 1 ) );
		String folioAdecuacion = "A" + String.valueOf( nFolioAdecuacion );

		try {

			conn = getConnection();
			cEjercicioFiscal = AdecuacionManager.obtenEjercicioFiscal( conn );
			boolean esControlAmbiental = AdecuacionManager.esControlAmbiental( conn );
			boolean esControlFonden = AdecuacionManager.esControlFonden( conn );

			adecuacion = Adecuacion.instanceFromExcel( Integer.parseInt( cEjercicioFiscal ), archivo, c, usuario, cSuperReduccion, cSRInterna );

			vabl.insertaArchivoValidacion( adecuacion, c, usuario, cSuperReduccion, cSRInterna );
			/*
			 * ResultadoSaldos resultadoSaldos = validaEPPlurianual(conn,
			 * nFolioAdecuacion); if (resultadoSaldos != null &&
			 * resultadoSaldos.getError().size() > 0){ //validar cuando esta
			 * vacio resultadoSaldos //entra si la adecuacion contiene EP con 09
			 * que son identificadas como plurianuales.
			 * 
			 * json.put("status", false); json.put("error",
			 * resultadoSaldos.getError()); }
			 */
			/* ======================== VALIDACIONES ======================== */

			// Valida que el usuaruo pertenesca a la unidad ejecutora correcta
			log.debug( "Validando que en el lay out este la unidad ejecutora a la que pertenece el usuario" );
			msjUnidadResp = ValidacionAdecuacionesManager.validaUnidadUsuarioAdecuacion( conn, usuario, adecuacion );
			if ( msjUnidadResp != null && !"".equals( msjUnidadResp ) )
				mensajesValidacion.add( msjUnidadResp );

			// Valida que el ramo capturado en el archivo este en el catalogo
			log.debug( "Validando que el Ramo capturado en el lay out este dado de alta en el catalogo y pertenesca a CONAFOR" );
			msjRamo = ValidacionAdecuacionesManager.validaRamoAdecuacion( conn, adecuacion );
			if ( msjRamo != null && !"".equals( msjRamo ) )
				mensajesValidacion.add( msjRamo );

			// Validacion general para todas las adecuaciones
			log.debug( "Haciendo validaciones generales de la adecuacion" );
			List<String> validacionGeneral = vabl.validacionGeneralAdecuacion( cEjercicioFiscal, folioAdecuacion );
			if ( validacionGeneral != null && validacionGeneral.size() > 0 )
				mensajesValidacion.addAll( validacionGeneral );
			validacionGeneral = null;

			if ( mensajesValidacion != null && mensajesValidacion.size() > 0 ) {
				ResultadoValidacionAdecuacion resultado = new ResultadoValidacionAdecuacion();
				resultado.setError( mensajesValidacion );
				return resultado;
			}
			/*
			 * ================= VALIDACIONES DE SALDOS =======================
			 * Si llego a este punto es por que no hubo errores en el archivo ni
			 * en la adecuacion.
			 */

			// Valida que esten los saldos
			log.debug( "Validando saldos de las EP's en Reduccion" );
			msjSaldos = ValidacionAdecuacionesManager.validaSaldos( conn, folioAdecuacion );
			if ( null != msjSaldos && msjSaldos.size() > 0 ) {
				ResultadoValidacionAdecuacion resultado = new ResultadoValidacionAdecuacion();
				resultado.setError( msjSaldos );
				return resultado;
			}

			/*
			 * 20022020 ARLA Se quita la validacion a peticion de presupuestos
			 */
			/*
			 * log.
			 * debug("Validando programa no sea 00 cuando la partida sea 43301 en Reduccion"
			 * ); msjSaldos = ValidacionAdecuacionesManager.validaPrograma(conn,
			 * folioAdecuacion); if (null != msjSaldos && msjSaldos.size() > 0)
			 * { ResultadoValidacionAdecuacion resultado = new
			 * ResultadoValidacionAdecuacion(); resultado.setError(msjSaldos);
			 * return resultado; }
			 */

			/*
			 * ================= VALIDACIONES SI ES USUARIO FORANEO ===========
			 */

			// Si es usuario externo y no es calendario

			log.debug( "Validando que no sea calendario para hacer las validaciones de usuario externo" );
			unidadResp = adecuacion.getEncabezado().getUnidadEjecutora();
			usuarioLogin = adecuacion.getEncabezado().getuLogin();
			tipoAdecuacion = ValidacionAdecuacionesManager.validaEsCalendario( conn, folioAdecuacion );

			if ( ! ( "A02".equalsIgnoreCase( unidadResp ) || "A03".equalsIgnoreCase( unidadResp )   ) ) {
				if ( "Transferencia".equalsIgnoreCase( tipoAdecuacion ) ) {
					log.debug( "Haciendo validaciones para usarios externos" );
					List<String> resultadoValidaciones = ValidacionAdecuacionesManager.validaUsuarioExterno( conn, folioAdecuacion, usuarioLogin );
					if ( resultadoValidaciones != null && resultadoValidaciones.size() > 0 )
						mensajesValidacion.addAll( resultadoValidaciones );
					resultadoValidaciones = null;
				} else
					mensajesValidacion.add( "No es posible hacer cambios de Calendario, Ampliaciones Liquidas ni Reducciones Liquidas" );

				if ( mensajesValidacion != null && mensajesValidacion.size() > 0 ) {
					ResultadoValidacionAdecuacion resultado = new ResultadoValidacionAdecuacion();
					resultado.setError( mensajesValidacion );
					return resultado;
				}

			}

			/*
			 * ================= VALIDACIONES SI ES USUARIO CENTRALES
			 * =================
			 */
			//
			if ( "A02".equalsIgnoreCase( unidadResp ) || "A03".equalsIgnoreCase( unidadResp ) || ( esControlAmbiental && "A01".equalsIgnoreCase( unidadResp ) ) ) {
				List<String> advertencias = null;
				/*
				 * if ("Transferencia".equalsIgnoreCase(tipoAdecuacion)) {
				 * 
				 * log.
				 * debug("Validando adecuacion para usuario centrales, advetencias para partidas especiales"
				 * ); advertencias =
				 * AdecuacionManager.validaPartidasEspeciales(conn,
				 * folioAdecuacion); if (advertencias != null &&
				 * advertencias.size() > 0)
				 * mensajesAdvertencia.addAll(advertencias);
				 * 
				 * }
				 */
				log.debug( "Validando adecuacion para usuario centrales, advetencias para plurianuales" );
				advertencias = AdecuacionManager.validaPartidasPlurianuales( conn, folioAdecuacion );
				if ( advertencias != null && advertencias.size() > 0 )
					mensajesAdvertencia.addAll( advertencias );

				log.debug( "Validando adecuacion para usuario centrales, EP con METAS" );
				advertencias = AdecuacionManager.validaEPMetas( conn, folioAdecuacion );
				if ( advertencias != null && advertencias.size() > 0 )
					mensajesAdvertencia.addAll( advertencias );
			}

			/*
			 * ======= Clasificacion y Tipificacion de adecuacion =============
			 */

			log.debug( "Clasificando adecuación." );
			ClasificacionAdecuacion clasificacion = vabl.clasificaAdecuacion( folioAdecuacion );

			// Valida que el monto del encabezado sea igual a la suma de
			// reducciones o ampliaciones
			log.debug( "Validando que el Monto Total sea igual a la suma de las Reducciones" );
			msjMonto = ValidacionAdecuacionesManager.validaMontoTotal( conn, adecuacion, folioAdecuacion, ( "Ampliación".equalsIgnoreCase( clasificacion.getTipoAdecuacion() ) ) ? "A" : "R" );
			if ( msjMonto != null && !"".equals( msjMonto ) )
				mensajesValidacion.add( msjMonto );

			adecuacion.getEncabezado().setnNivel( clasificacion.getNivel() );
			adecuacion.getEncabezado().setcTipoAdecuacion( clasificacion.getTipoAdecuacion() );
			adecuacion.getEncabezado().setnFolioAdecuacion( nFolioAdecuacion );

			/*
			 * ================= Inserta adecuacion en Base de Datos
			 * =================
			 */
			int xnInsertados = AdecuacionManager.insertaAdecuacion( conn, adecuacion );

			/*
			 * ================= Inserta la EP de ampliaciones que no esten en
			 * el catalogo =================
			 */
			int xnEpInsertadas = AdecuacionManager.insertaEpsNuevas( conn, folioAdecuacion );

			conn.commit();
			log.debug( "Se insertaron " + xnInsertados + " elementos de la adecuacion" );
			log.debug( "Se insertaron " + xnEpInsertadas + " EP's al catalogo" );

		} catch ( Exception e ) {
			try {
				conn.rollback();
			} catch ( Exception eRB ) {
				log.warn( "Problemas realizando el rollback: " + eRB );
			}

			log.error( e, e );
			mensajesValidacion.add( e.toString() );
		} finally {
			vabl.liberaArchivoValidacionAdecuacion( adecuacion, c, usuario, cSuperReduccion, cSRInterna );
			CloseObject.closeObject( conn, false );
		}

		ResultadoValidacionAdecuacion resultado = new ResultadoValidacionAdecuacion();
		resultado.setError( mensajesValidacion );
		resultado.setAdvertencia( mensajesAdvertencia );
		return resultado;

	}

	/*
	 * private HSSFWorkbook getExcel( String archivo ) { HSSFWorkbook workBook =
	 * null; try { String cFileExcel = archivo.substring( 0, archivo.length() -
	 * 3 ) + "xls"; File fFileExcel = new File( archivo.substring( 0,
	 * archivo.length() - 3 ) + "xls" ); File fFileOrig = new File( archivo );
	 * FileChannel in = ( new FileInputStream( fFileOrig ) ).getChannel();
	 * FileChannel out = ( new FileOutputStream( fFileExcel ) ).getChannel();
	 * in.transferTo( 0, fFileOrig.length(), out ); in.close(); out.close();
	 * FileInputStream fileInputStream = new FileInputStream( cFileExcel );
	 * POIFSFileSystem fsFileSystem = new POIFSFileSystem( fileInputStream );
	 * workBook = new HSSFWorkbook( fsFileSystem ); } catch ( Exception e ) {
	 * e.printStackTrace(); } return workBook; }
	 */
	public ArrayList<String> aplicaAdecuacion( int nIdCaso, Usuario usuario, String aEjercicioFiscal, String cRamo, String cUR, Caso c, String cCentroContable, String cFechaAplica, Map m, String prefixPath, String cSuperReduccion, String cSRInterna, String notificaAdecuacion ) throws SQLException {

		Connection conn = null;
		ArrayList<String> arrLResult = new ArrayList<String>();
		String UserID = usuario.getLogin();

		try {
			DateFormat dateFormatter = new SimpleDateFormat( "dd/MM/yyyy" );
			java.sql.Date fAplicacion;

			// Este codigo es para regularizar
			if ( ( cFechaAplica == null ) || ( cFechaAplica.trim().length() == 0 ) ) {
				if ( c.getCasoDato( "FECHA_AP_CONT" ).getValor() != null )
					cFechaAplica = c.getCasoDato( "FECHA_AP_CONT" ).getValor();
				else {
					cFechaAplica = dateFormatter.format( new Date() );
				}
			}

			try {
				fAplicacion = new java.sql.Date( dateFormatter.parse( cFechaAplica ).getTime() );
			} catch ( ParseException pex ) {
				fAplicacion = new java.sql.Date( System.currentTimeMillis() );
			}

			CasoBusinessLogic cbl = new CasoBusinessLogic( GestionInterface.ATT_CONEXION );
			conn = cbl.getConnection();

			int retVarUPDADECF = 0;
			retVarUPDADECF = AdecuacionManager.agregaFechaAplicacion( conn, nIdCaso );
			if ( retVarUPDADECF == 0 ) {
				log.error( "ERROR no se ha podido colocar la fecha de aplicacion." );
				nIdCaso = 0;
			}

			if ( nIdCaso > 0 ) {
				AplicacionContable conInt = new AplicacionContable();
				log.debug( "Inicia aplicacion contable" + new Timestamp( System.currentTimeMillis() ) + " Para  Folio:" + nIdCaso );
				if ( "NO".equals( cSuperReduccion ) && ( "SI".equals( cSRInterna ) ) ) {
					cSuperReduccion = "SI";
				}

				AplicarContableReturn acr = conInt.new AplicarContableReturn( new ArrayList<String>(), true );

				if ( !AdecuacionManager.esApartadoAplicado( conn, nIdCaso ) ) {
					acr = conInt.aplicarContableNuevo( conn, c, "", "", "", 0, "", m, prefixPath, UserID, cSuperReduccion );
				}

				arrLResult = ( ArrayList<String> ) acr.getMessageList();

				log.debug( "Termina Aplicacion contable " + new Timestamp( System.currentTimeMillis() ) + " Para  Folio:" + nIdCaso );

				Caso cReloaded = new Caso();
				cReloaded.setIdCaso( c.getIdCaso() );
				cReloaded = CasoManager.select( conn, cReloaded );

				if ( acr.isSuccess() ) {

					boolean esAdecuacionSIPLAN = false;
					String adecuacionReserva = esAadecuacionReserva( conn, nIdCaso );

					if ( "NO".equals( adecuacionReserva ) && "SI".equals( notificaAdecuacion ) )
						esAdecuacionSIPLAN = notificaAdecuacionSIPLAN( conn, nIdCaso );

					// avanzaCaso tiene su propia connection, en caso de fallar
					// de todos modos se conserva la app cont
					if ( esAdecuacionSIPLAN )
						cbl.avanzaCaso( cReloaded, UserID, "", new String [] { "VALIDA_METAS" }, new String [] { "valida_metas" }, m, prefixPath );
					else
						cbl.avanzaCaso( cReloaded, UserID, "", new String [] { "JEFATURA_ADECUACIONES" }, new String [] { "validar_normatividad" }, m, prefixPath );

				} else {
					throw new Exception( "No se logro aplicar el motor contable." );
				}

			} else {
				log.debug( "Error. no determinado se da rollback" );
				conn.rollback();
			}

			conn.commit();
		} catch ( Exception exc ) {
			log.error( exc, exc );
			arrLResult.add( exc.getLocalizedMessage() );
			try {
				conn.rollback();
			} catch ( Exception ex ) {
				log.warn( "En Rollback", ex );
			}
		} finally {
			if ( conn != null )
				conn.close();

			conn = null;
		}
		// se tiene que validar contra catalogos de restricciones para la
		// proxima etapa
		// se tiene que validad que cuadre para la proxima vercion
		// tiene que validar si es calendario para la proxima mas las que se
		// acomulen esta semana
		// si la verificacione es correcta Guarda y aplica contablemente
		return arrLResult;
	}

	public ArrayList buscaAdecuacion( Caso c ) throws Exception {
		ArrayList<Object> arrmMontosCalendario = new ArrayList<>();
		// int id_adecuacion = c.getIdCaso();

		int id_adecuacion = new Integer( c.getFolio().substring( c.getFolio().lastIndexOf( '-' ) + 1 ) ).intValue();
		Connection conn = null;
		try {
			conn = getConnection();
			arrmMontosCalendario = AdecuacionManager.seleccionaAdecuacion( conn, id_adecuacion );

		} catch ( Exception exc ) {
			log.error( exc );
			conn.rollback();
			throw new Exception( exc );
		} finally {
			if ( conn != null )
				conn.close();
			conn = null;
		}

		return arrmMontosCalendario;

	}

	public ArrayList<Adecuacion> buscaAdecuaciones( String listaIdCasos ) throws Exception {

		return null;
	}

	public ArrayList buscaEPAdecuar( String cPPC, String cOGTOC, String cTGC, String cFFC, String cUEC, String cnCodigo, String aEjercicioFiscal ) throws SQLException {
		ArrayList<Object> arrmEpCalendario = new ArrayList<>();
		Connection conn = null;
		try {
			conn = getConnection();
			arrmEpCalendario = AdecuacionManager.buscaEP( cPPC, cOGTOC, cTGC, cFFC, cUEC, conn, cnCodigo, aEjercicioFiscal );
		} finally {
			if ( conn != null )
				conn.close();
			conn = null;
		}

		return arrmEpCalendario;

	}

	@SuppressWarnings( { "rawtypes", "unchecked" } )
	public ArrayList AutorizaAdecuacionNuevo( Caso c, String nNumSicop, String fSicop, String nNumMAP, String fMAP, Map m, String prefixPath, Usuario usuario, String cSuperReduccion, String cCentroContable, String cSRInterna, boolean enviarCorreo ) throws SQLException {

		ArrayList arrLResult = new ArrayList();
		Connection conn = null;

		try {

			CasoBusinessLogic cbl = new CasoBusinessLogic( GestionInterface.ATT_CONEXION );
			conn = cbl.getConnection();

			AdecuacionManager.autorizaAdecuacion( conn, c, nNumSicop, fSicop, nNumMAP, fMAP, cSuperReduccion, prefixPath, usuario.getLogin(), cCentroContable );
			ContableInterface conInt = new AplicacionContable();
			log.debug( "Inicia Autorización aplicacion contable " + new Timestamp( System.currentTimeMillis() ) );

			if ( !"SI".equals( cSuperReduccion ) && "SI".equals( cSRInterna ) ) {
				cSuperReduccion = "SI";
			}

			AplicarContableReturn acr = conInt.aplicarContableNuevo( conn, c, "tADECUACIONAUTEncabezado", "tADECUACIONAUTDetalle", "nFolioAdecuacionaut", new Integer( c.getFolio().substring( c.getFolio().lastIndexOf( '-' ) + 1 ) ).intValue(), "ADECUACIONAUT", m, prefixPath, usuario.getLogin(), cSuperReduccion );

			arrLResult = ( ArrayList<String> ) acr.getMessageList();

			log.debug( "Termina Autorización Aplicacion contable " + new Timestamp( System.currentTimeMillis() ) );

			Caso cReloaded = new Caso();
			cReloaded.setIdCaso( c.getIdCaso() );
			cReloaded = CasoManager.select( conn, cReloaded );

			String to = "";
			String body = "";

			if ( acr.isSuccess() ) {
				/* VGC-20150817 Se cambia la manera de obtener los correos. */
				to = getListaCorreos( c );
				body = ( !correoProduccion ? "CORREO DE PRUEBA <br>" : "" ) + ( !correoProduccion ? "este correo le hubiera llegado a: " + to + "<br> <br>" : "" ) + "Para su conocimiento y efectos correspondientes, se le informa que <b>fue autorizado</b> el folio siguiente:<br>" + c.getTipoCaso().getDescripcion() + " No. SAI: <b>" + c.getFolio() + ( nNumSicop != null ? "</b> con folio SICOP: <b>" + nNumSicop : "" ) + ( nNumMAP != null ? "</b> con folio MAP: <b>" + nNumMAP : "" ) + "</b><br>" + "<br> <b>Mismo que ya cuenta con estatus de autorizado en el SAI. Para obtener el folio de autorización MAP, revisar en consulta su afectación</b> <br>";

				try {

					if ( !correoProduccion )
						to = "Proveedorsai1@conafor.gob.mx";
					if ( enviarCorreo ) {
						/*
						 * VGC-20150817 Se cambia la manera de enviar los
						 * correos. SOLO PARA CONAFOR.
						 */
						AlarmaManager.procesaAlarmaCNF( conn, prefixPath, c.getCasoOperacion( 0 ), c, "", to, body );

					}
				} catch ( Exception exmail ) {
					log.error( "No se logro enviar el correo de autorizacion de adecuacion: " + exmail );
				}

				conn.commit();

				// avanzaCaso tiene su propia connection, en caso de fallar de
				// todos modos se conserva la app cont

				cbl.avanzaCaso( cReloaded, usuario.getLogin(), "", new String [] { "CONSULTA_ADECUACION" }, new String [] { "consulta_adecuacion" }, m, prefixPath );

			} else {
				conn.rollback();

				to = usuario.getLogin();
				body = ( !correoProduccion ? "CORREO DE PRUEBA <br>" : "" ) + ( !correoProduccion ? "este correo le hubiera llegado a: " + to + "<br> <br>" : "" ) + "Para su conocimiento y efectos correspondientes, se le informa que el folio siguiente:<br><b>" + c.getTipoCaso().getDescripcion() + "</b> No. SAI: <b>" + c.getFolio() + ( nNumSicop != null ? "</b> con folio SICOP: <b>" + nNumSicop : "" ) + ( nNumMAP != null ? "</b> con folio MAP: <b>" + nNumMAP : "" ) + "</b><br>" +
				// "Mismo que ya cuenta con estatus de autorizado en el
				// SAI.<br>"+
						"<b>NO PUDO SER AUTORIZADO</b>, debido a:<br>" + arrLResult;
				try {
					if ( !correoProduccion )
						to = "" + usuario.getU_email();
					if ( enviarCorreo ) {
						AlarmaManager.procesaAlarmaCNF( conn, prefixPath, c.getCasoOperacion( 0 ), c, "", to, body );
					}

				} catch ( Exception exmail ) {
					log.error( "No se logro enviar el correo de mensaje de error en autorizacion de adecuacion: " + exmail );
				}
				// avanzaCaso tiene su propia connection, en caso de fallar de
				// todos modos se conserva la app cont
				if ( arrLResult.contains( "sido aplicado" ) )// si ya habia sido
					// aplicado lo
					// dejamos en
					// consulta
					cbl.avanzaCaso( cReloaded, usuario.getLogin(), "", new String [] { "CONSULTA_ADECUACION" }, new String [] { "consulta_adecuacion" }, m, prefixPath );
				else
					cbl.avanzaCaso( cReloaded, usuario.getLogin(), "", new String [] { "JEFATURA_ADECUACIONES" }, new String [] { "validar_normatividad" }, m, prefixPath );
			}
		} catch ( Exception exc ) {
			// conn.rollback();
			log.error( exc );
			arrLResult.add( exc.getLocalizedMessage() );

			try {
				conn.rollback();
			} catch ( Exception ex ) {
				log.warn( "En Rollback", ex );
			}
		} finally {
			if ( conn != null )
				conn.close();
			conn = null;
		}

		return arrLResult;
	}

	public ArrayList AutorizaAdecuacion( Caso c, String nNumSicop, String fSicop, String cRecMotivSicop, String nNumMAP, String fMAP, String cRecMotivMAP, Map m, String prefixPath, String uLogin, String cCentroContable ) throws Exception {
		ArrayList arrLResult = new ArrayList<>();
		Connection conn = null;
		String cSuperReduccion = "NO";
		int nIdCaso = new Integer( c.getFolio().substring( c.getFolio().lastIndexOf( '-' ) + 1 ) ).intValue();

		try {
			conn = getConnection();
			AdecuacionManager.autorizaAdecuacion( conn, c, nNumSicop, fSicop, nNumMAP, fMAP, cSuperReduccion, prefixPath, uLogin, cCentroContable );
			ContableInterface conInt = new AplicacionContable();
			log.debug( "Inicia Autorización aplicacion contable" + new Timestamp( System.currentTimeMillis() ) + " para Folio: " + nIdCaso );
			arrLResult.addAll( conInt.aplicarContable( conn, c, "tADECUACIONAUTEncabezado", "tADECUACIONAUTDetalle", "nFolioAdecuacionaut", new Integer( c.getFolio().substring( c.getFolio().lastIndexOf( '-' ) + 1 ) ).intValue(), "ADECUACIONAUTE", m, prefixPath, uLogin ) );// el

			log.debug( "Termina Autorización Aplicacion contable" + new Timestamp( System.currentTimeMillis() ) );
		} catch ( Exception exc ) {
			log.error( exc );
			conn.rollback();
			throw new Exception( exc );
		} finally {
			if ( conn != null )
				conn.close();
			conn = null;
		}
		return arrLResult;
	}

	public String cancelarAppContableNuevo( Caso c, Map m, String prefixPath, Usuario objUsuario, String cFecha, String motivoRechazo, boolean enviarCorreo ) throws Exception {

		String retVal = null;
		Connection conn = null;

		try {
			ContableInterface ci = new AplicacionContable();
			// conn = getConnection();
			CasoBusinessLogic cbl = new CasoBusinessLogic( GestionInterface.ATT_CONEXION );
			conn = cbl.getConnection();
			actualizaMotivoCancelacion( new String( motivoRechazo.getBytes( "ISO-8859-1" ), "UTF-8" ), new Integer( c.getFolio().substring( c.getFolio().lastIndexOf( '-' ) + 1 ) ).intValue() );

			AplicarContableReturn acr = ci.cancelarAppContableNueva( conn, c, "", "", "", 0, "", m, prefixPath, objUsuario.getLogin(), cFecha );
			String to = "";
			String body = "";
			Caso cReloaded = new Caso();
			cReloaded.setIdCaso( c.getIdCaso() );
			cReloaded = CasoManager.select( conn, cReloaded );
			if ( acr.isSuccess() ) {

				to = getListaCorreos( c );
				body = ( !correoProduccion ? "CORREO DE PRUEBA <br>" : "" ) + ( !correoProduccion ? "este correo le hubiera llegado a: " + to + "<br> <br>" : "" ) + "Para su conocimiento y efectos correspondientes, se les informa que <b>FUE RECHAZADO</b> el folio siguiente:<br><b>" + c.getTipoCaso().getDescripcion() + "</b><br>" + "No. SAI: <b>" + c.getFolio() + "</b><br>" + "<b>Justificación:<br>" + ( motivoRechazo != null ? "Motivo rechazo:" + motivoRechazo + "<BR>" : "" ) + "Con base en lo anterior la afectación fue rechazada en el SAI para que procedan al replanteamiento que consideren pertinente.</b><br>" + "Saludos cordiales.";

				try {
					if ( !correoProduccion )
						to = "" + objUsuario.getU_email();
					if ( enviarCorreo ) {
						AlarmaManager.procesaAlarmaCNF( conn, prefixPath, c.getCasoOperacion( 0 ), c, "", to, body );
					}

					int folioAdecuacion = Util.readNumericID( c.getFolio() );
					log.debug( "Se validara si se cancela la notificaion a SIPLAN" );

					if ( adecuacionMetasNotificada( conn, folioAdecuacion ) ) {
						log.debug( "================== INICIANDO CANCELACION DE SIPLAN =======================================" );
						notificaCancelaAdecuacionSIPLAN( conn, folioAdecuacion, motivoRechazo );
					}

				} catch ( WSException e ) {
					log.error( "No se logro notificar la cancelacion: " + e, e );
					throw e;
				} catch ( Exception exmail ) {
					log.error( "No se logro enviar el correo: " + exmail );
				}
				conn.commit();
				cbl.avanzaCaso( cReloaded, objUsuario.getLogin(), "", new String [] { "CONSULTA_ADECUACION" }, new String [] { "consulta_adecuacion" }, m, prefixPath );
			} else {
				conn.rollback();

				to = objUsuario.getU_email();
				body = ( !correoProduccion ? "CORREO DE PRUEBA <br>" : "" ) + ( !correoProduccion ? "este correo le hubiera llegado a: " + to + "<br> <br>" : "" ) + "Para su conocimiento y efectos correspondientes, se le informa que el folio siguiente:<br><b>" + c.getTipoCaso().getDescripcion() + "</b> No. SAI: <b>" + c.getFolio() + "</b><br>" + "<b>NO PUDO SER CANCELADO</b>, debido a:<br>" + acr.getMessageList().get( 0 );
				try {
					if ( !correoProduccion )
						to = "" + objUsuario.getU_email();
					if ( enviarCorreo ) {
						AlarmaManager.procesaAlarmaCNF( conn, prefixPath, c.getCasoOperacion( 0 ), c, "", to, body );
					}
				} catch ( Exception exmail ) {
					log.error( "No se logro enviar el correo: " + exmail );
				}
				if ( acr.getMessageList().get( 0 ).contains( "sido aplicado" ) || acr.getMessageList().get( 0 ).contains( "sido cancelado" ) )
					cbl.avanzaCaso( cReloaded, objUsuario.getLogin(), "", new String [] { "CONSULTA_ADECUACION" }, new String [] { "consulta_adecuacion" }, m, prefixPath );
				else
					cbl.avanzaCaso( cReloaded, objUsuario.getLogin(), "", new String [] { "JEFATURA_ADECUACIONES" }, new String [] { "validar_normatividad" }, m, prefixPath );
			}
			retVal = acr.getMessageList().get( acr.getMessageList().size() - 1 );
		} catch ( Exception exc ) {
			log.error( exc );
			conn.rollback();
			throw exc;
		} finally {
			if ( conn != null )
				conn.close();

			conn = null;
		}

		return retVal;
	}

	public String cancelarAppContable( Caso c, Map m, String prefixPath, String uLogin, String cFecha ) throws Exception {
		String retVal = "";
		Connection conn = null;
		try {
			ContableInterface ci = new AplicacionContable();
			conn = getConnection();
			retVal = ci.cancelarAppContable( conn, c, "", "", "", 0, "", m, prefixPath, uLogin, cFecha );
		} catch ( Exception exc ) {
			log.error( exc );
			conn.rollback();
			throw new Exception( exc );
		} finally {
			if ( conn != null )
				conn.close();
			conn = null;
		}
		return retVal;
	}

	public ArrayList<Integer> buscaAdecuacionesIntegrar( String cCasos ) throws SQLException {
		ArrayList<Integer> arrResult = new ArrayList<>();
		String cDatosSeleccionados;
		Connection conn = null;
		try {
			conn = getConnection();
			cDatosSeleccionados = cCasos.substring( 0, cCasos.length() - 1 );
			if ( cDatosSeleccionados.substring( 0, 1 ).equals( "," ) ) {
				cDatosSeleccionados = cCasos.substring( 1, cCasos.length() - 1 );
			}
			arrResult = AdecuacionManager.consultaxIntegrar( conn, cDatosSeleccionados, "" );
		} finally {
			if ( conn != null )
				conn.close();
			conn = null;
		}

		return arrResult;
	}

	public void ErrorAdecuacuines( Caso c, String errMesass, String uLogin, int id_oper, Map m, String prefixPath ) throws SQLException {
		Connection conn = null;
		conn = getConnection();

		CasoBusinessLogic cbl = new CasoBusinessLogic( GestionInterface.ATT_CONEXION );
		try {
			Caso cReloaded = cbl.getCaso( c.getIdCaso() );
			// String mensaje=errMesass.replace("'", "");
			String mensaje = Util.encodeJS( errMesass );
			CasoDato cd = new CasoDato();
			cd.setIdCaso( c.getIdCaso() );
			cd.setIdTC( c.getIdTC() );
			cd.setIdCD( 10 );
			cd.setValor( mensaje );

			try {
				CasoDatoManager.update( conn, cd );
				conn.commit();
			} catch ( SQLException se ) {
				log.error( "Error escribiendo el mensaje del motor en la variable de caso:", se );
			}
			if ( id_oper == 5 ) {
				cReloaded = cbl.getCaso( c.getIdCaso() );
				cbl.avanzaCaso( cReloaded, uLogin, "", new String [] { "REVISORES_ADECUACIONES" }, new String [] { "revision_adecuacion" }, new HashMap(), prefixPath );
			}
		} catch ( GestionException ge ) {
			log.error( "Error avanzando el caso automaticamente:", ge );
		} finally {

			if ( conn != null )
				conn.close();

			conn = null;
		}
	}

	public String agregaFolio( int nFolio, String cFolioSICOP, String CFolioMAP ) throws Exception {
		String cReturn = "";
		Connection conn = null;
		try {
			conn = getConnection();
			AdecuacionManager.modificaAdecuacion( conn, nFolio, cFolioSICOP, CFolioMAP );

			conn.commit();
		} catch ( Exception exc ) {
			log.error( exc );
			conn.rollback();
			throw new Exception( exc );

		} finally {

			CloseObject.closeObject( conn );

		}
		return cReturn;
	}

	public String validaClaveEP( Connection conn, String cClaveEP, String aEjercicio, int IterRegElx ) throws SQLException {
		String[] arrayComponentes = cClaveEP.split( "\\." );
		ArrayList<String> arrNewEPResult = new ArrayList<String>();
		String cValor = "";
		String cMensaje = "";
		int i = 0;
		int j = 1;
		int nComponentesEP = PresupuestoManager.maxNumEP( conn, aEjercicio );
		if ( nComponentesEP != arrayComponentes.length ) {
			cMensaje = "Error: En la secuencia " + IterRegElx + ". La EP debe componerse de " + nComponentesEP + " valores separados por punto " + "y la EP recibida tiene " + arrayComponentes.length + " Componentes.";
		}
		while ( i < arrayComponentes.length ) {
			cValor = arrayComponentes[i];
			arrNewEPResult = PresupuestoManager.validaEPDetalle( conn, aEjercicio, j, cValor, IterRegElx );
			if ( arrNewEPResult.size() != 0 ) {
				cMensaje += ( String ) arrNewEPResult.get( 0 ) + "\\n";
				arrNewEPResult = null;
			}
			i++;
			j++;
		}
		return cMensaje;
	}

	public ArrayList<String> validaInvercionaGastoCorr( int nFolioAdecuacion ) throws SQLException {
		ArrayList<String> arrResult = new ArrayList<String>();
		Connection conn = null;
		conn = getConnection();
		arrResult = AdecuacionManager.validaInvercionaGastoCorr( conn, nFolioAdecuacion );

		return arrResult;
	}

	public boolean esModificado( String subCuenta ) throws SQLException {// reviso
		// si es
		// modificado
		// o no
		// para
		// saber
		// si es
		// adecuacion
		// o
		// ampliacion.
		// adecuacion == 0, ampliacion > 0
		Connection conn = null;
		boolean respuesta = false;
		try {
			conn = getConnection();
			int nModificado = -1;
			if ( Util.getCapitulo( subCuenta ) == 6 )
				nModificado = AdecuacionManager.verificaModificacion( conn, subCuenta );
			else
				nModificado = AdecuacionManager.verificaModificacionMeta( conn, subCuenta );
			if ( nModificado > 0 ) {
				respuesta = true;
			}
		} catch ( Exception exc ) {
			log.error( exc );
			throw new SQLException( exc );
		} finally {

			if ( conn != null )
				conn.close();

			conn = null;
		}
		return respuesta; // regresa true si es ampliacion
	}

	// esta funcion la uso para saber si ya han generado antes un layout SICOP
	// para no volver a generarle un consecutivo
	public boolean tieneSicop( int folioAdecuacion ) throws SQLException {
		Connection conn = null;
		boolean tiene = false;
		try {
			conn = getConnection();
			int consecutivo = AdecuacionManager.consultaConsecutivoSicop( conn, folioAdecuacion );
			if ( consecutivo > 0 ) {
				tiene = true;
			}
		} catch ( Exception exc ) {
			log.error( exc );
			throw new SQLException( exc );
		} finally {

			if ( conn != null )
				conn.close();

			conn = null;
		}
		return tiene;
	}

	public String obtenClaveSicop( boolean movimiento, String tipo ) throws SQLException { // mando
		// llamar
		// a
		// la
		// base
		// para
		// que
		// me
		// regresa
		// la
		// clave
		// para
		// layout
		// SICOP
		Connection conn = null;
		String respuesta = "";
		try {
			conn = getConnection();
			int clave = 0;
			if ( tipo.equals( "R" ) ) {
				clave = 1; // clave reducción
			} else if ( tipo.equals( "A" ) ) {
				if ( movimiento ) {
					clave = 2; // clave ampliación
				} else {
					clave = 3; // clave adicion ******NO ESTA CORRECTA, SE PUSO
					// UNA POR DEFAULT EN LA BASE*****
				}
			}
			respuesta = AdecuacionManager.obtenClavesSicop( conn, clave );
		} catch ( Exception exc ) {
			log.error( exc );
			throw new SQLException( exc );
		} finally {

			if ( conn != null )
				conn.close();

			conn = null;
		}
		return respuesta; // regresa la cadena de claves separada por puntos
	}

	public int obtenFolioSicop( boolean aumenta ) throws SQLException {
		Connection conn = null;
		int retVal = -1;
		try {
			conn = getConnection();
			retVal = AdecuacionManager.obtenFolioSicop( conn, aumenta );

			conn.commit();
		} catch ( Exception exc ) {
			log.error( exc );
			throw new SQLException( exc );
		} finally {

			CloseObject.closeObject( conn );
		}

		return retVal;
	}

	public boolean actualizaFolioSicopEncabezado( String folioSicop, int folioAdecuacion ) throws SQLException {
		Connection conn = null;
		boolean retVal = false;
		try {
			conn = getConnection();
			retVal = AdecuacionManager.actualizaFolioSicopEncabezado( conn, folioSicop, folioAdecuacion );
			conn.commit();
		} catch ( Exception exc ) {
			log.error( exc );
			conn.rollback();
			throw new SQLException( exc );
		} finally {

			if ( conn != null )
				conn.close();

			conn = null;
		}
		return retVal;
	}

	public ArrayList<String> validaPNRGP( int nFolioAdecuacion ) throws SQLException {
		ArrayList<String> arrResult = new ArrayList<String>();
		Connection conn = null;
		conn = getConnection();
		arrResult = AdecuacionManager.validaPNRGP( conn, nFolioAdecuacion );

		return arrResult;
	}

	public ArrayList<String> getResponsableArea( String login ) throws SQLException {
		Connection conn = null;
		ArrayList<String> responsables;
		try {
			conn = getConnection();
			responsables = AdecuacionManager.getResponsableArea( conn, login );
		} catch ( Exception exc ) {
			log.error( exc );
			throw new SQLException( exc );
		} finally {

			if ( conn != null )
				conn.close();

			conn = null;
		}

		return responsables;
	}

	public ArrayList<String> getResponsableIntegrador( String tipo ) throws SQLException {
		Connection conn = null;
		ArrayList<String> responsables;
		try {
			conn = getConnection();
			responsables = AdecuacionManager.getResponsableIntegrador( conn, tipo );
		} catch ( Exception exc ) {
			log.error( exc );
			throw new SQLException( exc );
		} finally {

			if ( conn != null )
				conn.close();

			conn = null;
		}

		return responsables;
	}

	public String obtenEjercicioFiscal() throws SQLException {
		String cEjercicioFiscal = "";
		Connection conn = null;
		try {
			conn = getConnection();
			cEjercicioFiscal = AdecuacionManager.obtenEjercicioFiscal( conn );
		} finally {
			if ( conn != null )
				conn.close();

			conn = null;
		}

		return cEjercicioFiscal;
	}

	public String obtenSuperAdecuacion( Caso c ) throws SQLException {
		String cSuperAdecuacion = "No";
		int id_Caso = 0;
		int nFolio = 0;
		Connection conn = null;
		try {
			conn = getConnection();
			id_Caso = c.getIdCaso();
			nFolio = new Integer( c.getFolio().substring( c.getFolio().lastIndexOf( '-' ) + 1 ) ).intValue();
			cSuperAdecuacion = AdecuacionManager.ObtenSiperAdecuacion( conn, nFolio, id_Caso );
		} catch ( Exception exc ) {
			log.error( exc );
			conn.rollback();
			throw new SQLException( exc );
		} finally {
			if ( conn != null )
				conn.close();

			conn = null;
		}
		return cSuperAdecuacion;
	}

	public ArrayList<String> getUnidades() throws Exception {
		Connection conn = null;
		ArrayList<String> arrUnidades = new ArrayList<String>();
		try {
			conn = getConnection();
			arrUnidades = AdecuacionManager.getUnidades( conn );
		} finally {
			if ( conn != null )
				conn.close();

			conn = null;
		}
		return arrUnidades;

	}

	public ArrayList<?> integraAdecuaciones( int nFolio, String cFoliosAdecuaciones, Usuario u ) throws IADEClavesNeteadasException, Exception {

		String[] arrFolios = cFoliosAdecuaciones.split( "," );
		ArrayList<ArrayList<String>> arrDatsoGuardados = new ArrayList<ArrayList<String>>();
		Connection conn = null;

		try {
			conn = getConnection();

			AdecuacionManager.integraAdecuaciones( conn, nFolio, arrFolios, u );

			arrDatsoGuardados.addAll( AdecuacionManager.buscaIntegrados( conn, nFolio ) );
			conn.commit();

		} catch ( Exception e ) {
			log.error( e, e );
			try {
				log.error( e );
				conn.rollback();
				throw new SQLException( e );
			} catch ( Exception e2 ) {
				log.warn( "No fue posible realizar rollback: " + e2, e2 );
			}
			throw e;
		} finally {
			CloseObject.closeObject( conn, false );
			conn = null;
		}

		return arrDatsoGuardados;
	}

	public ArrayList buscaIntegrada( int nFolio ) throws SQLException {
		ArrayList<ArrayList<String>> arrDatsoGuardados = new ArrayList<>();
		Connection conn = null;
		try {
			conn = getConnection();
			arrDatsoGuardados.addAll( AdecuacionManager.buscaIntegrados( conn, nFolio ) );
		} finally {
			if ( conn != null )
				conn.close();

			conn = null;
		}
		return arrDatsoGuardados;
	}

	public ArrayList buscaIntegracionAdecCveCorta( Caso c, int nFolioConsolidado, String cTipoAdecuacion, String U_LOGIN, String nNivel, String cRamo, String aEjercicioFiscal ) throws Exception {
		ArrayList arrmMontosCalendario = new ArrayList<>();
		// int id_adecuacion = c.getIdCaso();

		// int id_adecuacion = new
		// Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') +
		// 1)).intValue();
		Connection conn = null;
		try {
			conn = getConnection();
			arrmMontosCalendario = AdecuacionManager.seleccionaIntegradaAdecCveCorta( conn, nFolioConsolidado, cTipoAdecuacion, U_LOGIN, nNivel, cRamo, aEjercicioFiscal );
		} catch ( Exception exc ) {
			log.error( exc );
			conn.rollback();
			throw new Exception( exc );
		} finally {
			if ( conn != null )
				conn.close();
			conn = null;
		}

		return arrmMontosCalendario;

	}

	public ArrayList buscaIntegracionAdec( Caso c, int nFolioConsolidado, String cTipoAdecuacion, String U_LOGIN, String nNivel, String cRamo, String aEjercicioFiscal ) throws Exception {
		ArrayList arrmMontosCalendario = new ArrayList<>();

		Connection conn = null;
		try {
			conn = getConnection();
			arrmMontosCalendario = AdecuacionManager.seleccionaIntegradaAdec( conn, nFolioConsolidado, cTipoAdecuacion, U_LOGIN, nNivel, cRamo, aEjercicioFiscal );
		} catch ( Exception exc ) {
			log.error( exc );
			conn.rollback();
			throw new Exception( exc );
		} finally {
			if ( conn != null )
				conn.close();
			conn = null;
		}

		return arrmMontosCalendario;

	}

	public int desIntegra( int nFolio, Caso c, String uLogin, Map m, String prefixPath ) throws Exception {
		int iDesIntegrado = 0;
		Connection conn = null;
		try {
			CasoBusinessLogic cbl = new CasoBusinessLogic( GestionInterface.ATT_CONEXION );
			conn = getConnection();
			iDesIntegrado = AdecuacionManager.desIntegra( conn, nFolio );
			Caso cReloaded = new Caso();
			cReloaded.setIdCaso( c.getIdCaso() );
			cReloaded = CasoManager.select( conn, cReloaded );
			conn.commit();
			// avanzaCaso tiene su propia connection, en caso de fallar de todos
			// modos se conserva la app cont
			cbl.avanzaCaso( cReloaded, uLogin, "", new String [] { "TERMINAR" }, new String [] { "TERMINAR" }, m, prefixPath );
		} catch ( Exception exc ) {
			log.error( exc );
			conn.rollback();
			throw new Exception( exc );
		} finally {
			if ( conn != null )
				conn.close();
			conn = null;
		}
		return iDesIntegrado;
	}

	public ArrayList integraAdecuaciones( int nFolio, String nNumSicop, String nNumMAP, Usuario u, String fFechaSicop, String fFechaMAP, String cMotSicop, String cMotMAP ) throws SQLException {
		ArrayList<ArrayList<String>> arrDatsoGuardados = new ArrayList<>();

		// String cMesnajeValidaInt = "";
		Connection conn = null;
		try {
			conn = getConnection();
			AdecuacionManager.updateIntegracion( conn, nFolio, nNumSicop, nNumMAP, fFechaSicop, fFechaMAP, cMotSicop, cMotMAP );
			conn.commit();
			arrDatsoGuardados.addAll( AdecuacionManager.buscaIntegrados( conn, nFolio ) );

		} finally {
			if ( conn != null )
				conn.close();

			conn = null;
		}
		return arrDatsoGuardados;

	}

	public void actualizaNivelFIAF( int nFolioFIAF, int nNivel, String cTipoAdecuacion ) throws SQLException {
		Connection conn = null;
		try {
			conn = getConnection();
			AdecuacionManager.actualizaNivelFIAF( conn, nFolioFIAF, nNivel, cTipoAdecuacion );
			conn.commit();
		} finally {
			if ( conn != null )
				conn.close();

			conn = null;
		}
	}

	public int autorizaIntegracion( int nFolio, String nNumSicop, String fSicop, String cRecMotivSicop, String nNumMAP, String fMAP, String cRecMotivMAP, Map m, String prefixPath, Usuario usuario, String cSuperReduccion, String cCentroContable, Caso cl ) throws SQLException, GestionException {
		Connection conn = null;
		ArrayList<ArrayList<String>> arrDatsoGuardados = new ArrayList<>();
		ArrayList<String> arrAutorizaIntegrado = new ArrayList<String>();
		String cFolioIntegracion = cl.getFolio();
		String cFolioN = "";
		boolean bAutorizado = false;
		int nAutorizados = 0;
		int i = 0;
		try {
			conn = getConnection();
			arrDatsoGuardados = AdecuacionManager.buscaIntegrados( conn, nFolio );

			while ( i < arrDatsoGuardados.size() ) {
				ArrayList<String> arrPaso = ( ArrayList<String> ) arrDatsoGuardados.get( i );
				cFolioN = ( String ) arrPaso.get( 0 );

				Caso c = new Caso();
				c.setFolio( cFolioN );
				c = CasoManager.select( conn, c );

				arrAutorizaIntegrado.add( cFolioN );
				bAutorizado = AutorizaIntegracion( conn, c, nNumSicop, fSicop, nNumMAP, fMAP, m, prefixPath, usuario.getLogin(), cSuperReduccion, cCentroContable, usuario );
				if ( !bAutorizado ) {
					break;
				}

				i++;
			}
			i = 0;
			CasoBusinessLogic cbl = new CasoBusinessLogic( GestionInterface.ATT_CONEXION );
			if ( bAutorizado ) {
				conn.commit();
				while ( i < arrAutorizaIntegrado.size() ) {
					cFolioN = arrAutorizaIntegrado.get( i );
					Caso cAutoriza = new Caso();
					cAutoriza.setFolio( cFolioN );
					cAutoriza = CasoManager.select( conn, cAutoriza );

					if ( !cFolioN.contains( "FIAF" ) ) {
						try {
							String to = getListaCorreos( cAutoriza );
							String body = ( !correoProduccion ? "CORREO DE PRUEBA <br>" : "" ) + ( !correoProduccion ? "este correo le hubiera llegado a: " + to + "<br> <br>" : "" ) + "Para su conocimiento y efectos correspondientes, se le informa que <b>fue autorizado</b> el folio siguiente:<br>" + cAutoriza.getTipoCaso().getDescripcion() + " No. SAI: <b>" + cAutoriza.getFolio() + ( nNumSicop != null ? "</b> con folio SICOP: <b>" + nNumSicop : "" ) + ( nNumMAP != null ? "</b> con folio MAP: <b>" + nNumMAP : "" ) + "</b><br>" + "Mismo que ya cuenta con estatus de autorizado en el SAI.<br>" + " Para consultar Archivo de Autorización MAP ir a Consulta de Integracion de Adecuaciones con el Folio No. <b>" + cFolioIntegracion + "</b><br>" + "<b>Nota importante<br>" + "Los calendarios del Folio de Adecuación MAP, no necesariamente coinciden con los registrados en las " + "afectaciones del SAI y el SICOP, dado que su política de operación es diferente al de éstos. Por lo anterior, " + "se les recuerda que los calendarios para la operación de sus adecuaciones y pagos, son los registrados tanto " + "en el SAI como en el SICOP.</b>.";

							if ( !correoProduccion )
								to = "" + usuario.getU_email();
							AlarmaManager.procesaAlarmaCNF( conn, prefixPath, cAutoriza.getCasoOperacion( 0 ), cAutoriza, "", to, body );
						} catch ( Exception exmail ) {
							log.error( "No se logro enviar el correo de autorizacion de adecuacion: " + exmail );
						}
						// avanzaCaso tiene su propia connection, en caso de
						// fallar
						// de todos modos se conserva la app cont
						try {
							cbl.avanzaCaso( cAutoriza, usuario.getLogin(), "", new String [] { "CONSULTA_ADECUACION" }, new String [] { "consulta_adecuacion" }, m, prefixPath );
						} catch ( Exception exc ) {
							log.error( "No se logro avanzar el caso de autorización de adecuacion " + cAutoriza.getFolio() + " de la integración " + nFolio + " por: " + exc );
						}
					} else {
						try {
							cbl.avanzaCaso( cAutoriza, usuario.getLogin(), "", new String [] { "CONSULTA_FIAF" }, new String [] { "consulta_fiaf" }, m, prefixPath );
						} catch ( Exception exc ) {
							log.error( "No se logro avanzar el caso de autorización de fiaf " + cAutoriza.getFolio() + " de la integración " + nFolio + " por: " + exc );
						}
					}

					i++;
				}
				try {
					cbl.avanzaCaso( cl, usuario.getLogin(), "", new String [] { "CONSULTA_INTEGRAADECUA" }, new String [] { "consulta_integadec" }, m, prefixPath );
				} catch ( Exception exc ) {
					log.error( "No se logro avanzar el caso de autorización de Integracion de adecuacion " + cl.getFolio() + " por: " + exc );
				}

			} else {
				conn.rollback();
				Usuario u = new Usuario();
				u.setLogin( usuario.getLogin() );
				u = UsuarioManager.select( conn, u );

				String to = u.getU_email();
				String body = ( !correoProduccion ? "CORREO DE PRUEBA <br>" : "" ) + ( !correoProduccion ? "este correo le hubiera llegado a: " + to + "<br> <br>" : "" ) + "Para su conocimiento y efectos correspondientes, se le informa el folio siguiente:<br><b>" + cl.getTipoCaso().getDescripcion() + "</b> No. SAI: <b>" + cl.getFolio() + ( nNumSicop != null ? "</b> con folio SICOP: <b>" + nNumSicop : "" ) + ( nNumMAP != null ? "</b> con folio MAP: <b>" + nNumMAP : "" ) + "</b><br>" + "<b>NO PUDO SER AUTORIZADO</b>, debido a:<br>";
				try {
					if ( !correoProduccion )
						to = "" + usuario.getU_email();
					AlarmaManager.procesaAlarmaCNF( conn, prefixPath, cl.getCasoOperacion( 0 ), cl, "", to, body );
				} catch ( Exception exmail ) {
					log.error( "No se logro enviar el correo de mensaje de error en autorizacion de adecuacion: " + exmail );
				}
				try {
					cbl.avanzaCaso( cl, usuario.getLogin(), "", new String [] { "CAPTURISTA_INTEGADEC" }, new String [] { "capturista_integadec" }, m, prefixPath );
				} catch ( Exception exc ) {
					log.error( "No se logro avanzar el caso de autorización de Integracion de adecuacion " + cl.getFolio() + " de la integración " + nFolio + " por: " + exc );
				}
			}
		} finally {
			if ( conn != null ) {
				conn.close();
			}
			conn = null;
		}
		return nAutorizados;
	}

	public boolean tieneSicopIntegrado( int folioAdecuacion ) throws SQLException {
		Connection conn = null;
		boolean tiene = false;
		try {
			conn = getConnection();
			int consecutivo = AdecuacionManager.consultaConsecutivoSicop( conn, folioAdecuacion );
			if ( consecutivo > 0 ) {
				tiene = true;
			}
		} catch ( Exception exc ) {
			log.error( exc );
			throw new SQLException( exc );
		} finally {

			if ( conn != null )
				conn.close();

			conn = null;
		}
		return tiene;
	}

	public boolean actualizaFolioSicopEncabezadoInt( String folioSicop, int folioAdecuacion ) throws SQLException {
		Connection conn = null;
		boolean retVal = false;
		try {
			conn = getConnection();
			retVal = AdecuacionManager.actualizaFolioSicopEncabezadoInt( conn, folioSicop, folioAdecuacion );
			conn.commit();
		} catch ( Exception exc ) {
			log.error( exc );
			conn.rollback();
			throw new SQLException( exc );
		} finally {

			if ( conn != null )
				conn.close();

			conn = null;
		}
		return retVal;
	}

	public int validaAdecSicop( int nFolio ) throws SQLException {
		Connection conn = null;
		// boolean retVal = false;
		int nConsecutivoSicop = 0;
		try {
			conn = getConnection();
			nConsecutivoSicop = AdecuacionManager.validaAdecSicop( conn, nFolio );

		} catch ( Exception exc ) {
			log.error( exc );
			throw new SQLException( exc );
		} finally {

			if ( conn != null )
				conn.close();

			conn = null;
		}
		return nConsecutivoSicop;

	}

	public String obtienenNumMAP( int nFolio ) throws SQLException {
		Connection conn = null;
		// boolean retVal = false;
		String nNumMAP = "";
		try {
			conn = getConnection();
			nNumMAP = AdecuacionManager.obtienenNumMAP( conn, nFolio );

		} catch ( Exception exc ) {
			log.error( exc );
			throw new SQLException( exc );
		} finally {

			if ( conn != null )
				conn.close();

			conn = null;
		}
		return nNumMAP;

	}

	public String obtienenNumSicop( int nFolio ) throws SQLException {
		Connection conn = null;
		// boolean retVal = false;
		String nNumSicop = "";
		try {
			conn = getConnection();
			nNumSicop = AdecuacionManager.obtienenNumSicop( conn, nFolio );

		} catch ( Exception exc ) {
			log.error( exc );
			throw new SQLException( exc );
		} finally {

			if ( conn != null )
				conn.close();

			conn = null;
		}
		return nNumSicop;

	}

	public ArrayList validaAdecIntegrada( int nFolio ) throws SQLException {
		ArrayList arrConsolidado = new ArrayList<>();
		Connection conn = null;
		// boolean retVal = false;
		// int nConsecutivoSicop = 0;
		try {
			conn = getConnection();
			// arrConsolidado.add(AdecuacionManager.validaAdecIntegrada(conn,
			// nFolio));
			arrConsolidado = AdecuacionManager.validaAdecIntegrada( conn, nFolio );

		} catch ( Exception exc ) {
			log.error( exc );
			throw new SQLException( exc );
		} finally {

			if ( conn != null )
				conn.close();

			conn = null;
		}
		return arrConsolidado;

	}

	/*
	 * public String validaNivelDos(int nFolio) throws SQLException { String
	 * cNivel = ""; Connection conn = null; try { conn = getConnection(); cNivel
	 * = AdecuacionManager.validaNivelDos(conn, nFolio); } finally { if (conn !=
	 * null) { conn.close(); } conn = null; } return cNivel; }
	 */

	public boolean validaRestrictivas( int nFolio ) throws SQLException {
		boolean inibeAPL = false;
		Connection conn = null;
		try {
			conn = getConnection();
			inibeAPL = AdecuacionManager.validaRestrictivas( conn, nFolio );
		} finally {
			if ( conn != null )
				conn.close();

			conn = null;
		}
		return inibeAPL;

	}

	public int buscaConsecutivoSICOP( int nFolio ) throws SQLException {
		int nConsecutivoSICOP = 0;
		Connection conn = null;
		try {
			conn = getConnection();
			nConsecutivoSICOP = AdecuacionManager.buscaConsecutivoSICOP( conn, nFolio );
		} finally {
			if ( conn != null )
				conn.close();

			conn = null;
		}
		return nConsecutivoSICOP;
	}

	public ArrayList<ArrayList> consultaIntegradas( int nFolio ) throws SQLException {
		ArrayList<ArrayList> arrIntegradas = new ArrayList<>();
		Connection conn = null;
		try {
			conn = getConnection();
			arrIntegradas.add( AdecuacionManager.consultaIntegradas( conn, nFolio ) );
		} finally {
			if ( conn != null )
				conn.close();

			conn = null;

		}
		return arrIntegradas;
	}

	public String[] consultaFirmantePuesto( String ur, int nFolio ) throws SQLException {
		String[] arrFirmante = new String [2];
		Connection conn = null;
		try {
			conn = getConnection();
			arrFirmante = ( AdecuacionManager.consultaFirmantePuesto( conn, ur, nFolio ) );
		} finally {
			if ( conn != null )
				conn.close();

			conn = null;

		}
		return arrFirmante;
	}

	public String[] consultaFirmanteFIAFPuesto( String ur, int nFolio ) throws SQLException {
		String[] arrFirmante = new String [2];
		Connection conn = null;
		try {
			conn = getConnection();
			arrFirmante = ( AdecuacionManager.consultaFirmanteFIAFPuesto( conn, ur, nFolio ) );
		} finally {
			if ( conn != null )
				conn.close();

			conn = null;

		}
		return arrFirmante;
	}

	public ArrayList consultaFap02ReduceExcel( int nFolio ) throws SQLException {
		ArrayList<ArrayList<ArrayList<String>>> arrIntegradas = new ArrayList<>();
		Connection conn = null;
		try {
			conn = getConnection();
			arrIntegradas.add( AdecuacionManager.consultaFap02ReduceExcel( conn, nFolio ) );
		} finally {
			if ( conn != null )
				conn.close();

			conn = null;

		}
		return arrIntegradas;
	}

	public ArrayList<ArrayList> consultaFap02ReduceFIAFExcel( int nFolio ) throws SQLException {
		ArrayList<ArrayList> arrIntegradas = new ArrayList<>();
		Connection conn = null;
		try {
			conn = getConnection();
			arrIntegradas.add( AdecuacionManager.consultaFap02ReduceFIAFExcel( conn, nFolio ) );
		} finally {
			if ( conn != null )
				conn.close();

			conn = null;

		}
		return arrIntegradas;
	}

	public ArrayList<ArrayList> consultaFap02AmpliaExcel( int nFolio ) throws SQLException {
		ArrayList<ArrayList> arrIntegradas = new ArrayList<>();
		Connection conn = null;
		try {
			conn = getConnection();
			arrIntegradas.add( AdecuacionManager.consultaFap02AmpliaExcel( conn, nFolio ) );
		} finally {
			if ( conn != null )
				conn.close();

			conn = null;

		}
		return arrIntegradas;
	}

	public ArrayList<ArrayList<ArrayList<String>>> consultaFap02AmpliaFIAFExcel( int nFolio ) throws SQLException {
		ArrayList<ArrayList<ArrayList<String>>> arrIntegradas = new ArrayList<>();
		Connection conn = null;
		try {
			conn = getConnection();
			arrIntegradas.add( AdecuacionManager.consultaFap02AmpliaFIAFExcel( conn, nFolio ) );
		} finally {
			if ( conn != null )
				conn.close();

			conn = null;

		}
		return arrIntegradas;
	}

	public ArrayList<ArrayList<ArrayList<String>>> consultaIntegradasExcel( int nFolio ) throws SQLException {
		ArrayList<ArrayList<ArrayList<String>>> arrIntegradas = new ArrayList<>();
		Connection conn = null;
		try {
			conn = getConnection();
			arrIntegradas.add( AdecuacionManager.consultaIntegradasExcel( conn, nFolio ) );
		} finally {
			if ( conn != null )
				conn.close();

			conn = null;

		}
		return arrIntegradas;
	}

	public ArrayList<ArrayList<ArrayList<String>>> consultaIntegradasFIAFExcel( int nFolio ) throws SQLException {
		ArrayList<ArrayList<ArrayList<String>>> arrIntegradas = new ArrayList<>();
		Connection conn = null;
		try {
			conn = getConnection();
			arrIntegradas.add( AdecuacionManager.consultaIntegradasFIAFExcel( conn, nFolio ) );
		} finally {
			if ( conn != null )
				conn.close();

			conn = null;

		}
		return arrIntegradas;
	}

	// ////////////////////////////////////////////
	public boolean AutorizaIntegracion( Connection conn, Caso c, String nNumSicop, String fSicop, String nNumMAP, String fMAP, Map m, String prefixPath, String uLogin, String cSuperReduccion, String cCentroContable, Usuario u ) throws SQLException {
		boolean bAplicado = false;
		ArrayList<String> arrLResult = new ArrayList<>();

		try {
			AplicarContableReturn acr = null;
			CasoBusinessLogic cbl = new CasoBusinessLogic( GestionInterface.ATT_CONEXION );
			String resultadoFIAF = "";
			log.debug( "Inicia Autorización aplicacion contable " + new Timestamp( System.currentTimeMillis() ) );

			if ( c.getFolio().contains( "FIAF" ) ) {
				resultadoFIAF = autorizaIntegracionFIAF( c, nNumSicop, "", nNumMAP, "", m, prefixPath, uLogin, cCentroContable, u );

				if ( "".equals( resultadoFIAF ) ) {
					String to = getListaCorreos( c );
					String body = ( !correoProduccion ? "CORREO DE PRUEBA <br>" : "" ) + ( !correoProduccion ? "este correo le hubiera llegado a: " + to + "<br> <br>" : "" ) + "Para su conocimiento y efectos correspondientes, se le informa que <b>fue autorizado</b> el folio siguiente:<br>" + c.getTipoCaso().getDescripcion() + " No. SAI: <b>" + c.getFolio() + ( nNumSicop != null ? "</b> con folio SICOP: <b>" + nNumSicop : "" ) + ( nNumMAP != null ? "</b> con folio MAP: <b>" + nNumMAP : "" ) + "</b><br>" + "Mismo que ya cuenta con estatus de autorizado en el SAI. Para obtener el folio de autorización MAP, revisar en consulta su afectación<br>" + "<b>Nota importante<br>" + "Los calendarios del Folio de Adecuación MAP, no necesariamente coinciden con los registrados en las " + "afectaciones del SAI y el SICOP, dado que su política de operación es diferente al de éstos. Por lo anterior, " + "se les recuerda que los calendarios para la operación de sus adecuaciones y pagos, son los registrados tanto " + "en el SAI como en el SICOP.</b>.";
					try {
						if ( !correoProduccion )
							to = "" + u.getU_email();
						AlarmaManager.procesaAlarmaCNF( conn, prefixPath, c.getCasoOperacion( 0 ), c, "", to, body );
						bAplicado = true;
					} catch ( Exception exmail ) {
						log.error( "No se logro enviar el correo de autorizacion de adecuacion: " + exmail );
					}
				} else if ( !"".equals( resultadoFIAF ) ) {
					conn.rollback();

					String to = u.getU_email();
					String body = ( !correoProduccion ? "CORREO DE PRUEBA <br>" : "" ) + ( !correoProduccion ? "este correo le hubiera llegado a: " + to + "<br> <br>" : "" ) + "Para su conocimiento y efectos correspondientes, se le informa que el folio siguiente:<br><b>" + c.getTipoCaso().getDescripcion() + "</b> No. SAI: <b>" + c.getFolio() + "</b><br>" + "<b>NO PUDO SER CANCELADO</b>, debido a:<br>" + acr.getMessageList().get( 0 );
					try {
						if ( !correoProduccion )
							to = "" + u.getU_email();
						AlarmaManager.procesaAlarmaCNF( conn, prefixPath, c.getCasoOperacion( 0 ), c, "", to, body );
					} catch ( Exception exmail ) {
						log.error( "No se logro enviar el correo: " + exmail );
					}
				}

			} else {
				AdecuacionManager.autorizaAdecuacion( conn, c, nNumSicop, fSicop, nNumMAP, fMAP, cSuperReduccion, prefixPath, uLogin, cCentroContable );
				ContableInterface conInt = new AplicacionContable();

				acr = conInt.aplicarContableNuevo( conn, c, "tADECUACIONAUTEncabezado", "tADECUACIONAUTDetalle", "nFolioAdecuacionaut", new Integer( c.getFolio().substring( c.getFolio().lastIndexOf( '-' ) + 1 ) ).intValue(), "ADECUACIONAUT", m, prefixPath, uLogin, cSuperReduccion );// el
				bAplicado = acr.isSuccess();
			}
			// commit
			// se
			// hace
			// aqui
			// adentro

			arrLResult = ( ArrayList<String> ) acr.getMessageList();

			log.debug( "Termina Autorización Aplicacion contable " + new Timestamp( System.currentTimeMillis() ) );

			try {
				cbl.avanzaCaso( c, uLogin, "", new String [] { "CONSULTA_INTEGRAADECUA" }, new String [] { "consulta_integadec" }, m, prefixPath );
			} catch ( Exception exc ) {
				log.error( "No se logro avanzar el caso de autorización de Integracion de adecuacion " + c.getFolio() + " por: " + exc );
			}

		} catch ( Exception exc ) {
			// conn.rollback();
			log.error( exc );
			arrLResult.add( exc.getLocalizedMessage() );

			try {
				// conn.rollback();
			} catch ( Exception ex ) {
				log.warn( "En Rollback", ex );
			}
		} finally {

		}

		return bAplicado;
	}

	public String integraAdecuacionesFIAF( int nFolio, String[] foliosAdec, String cJustificacionA, String cJustificacionR, Usuario u, String cJustificacionNormativa, String aEjercicioFiscal, String cTipoAdecuacion, String nNivel ) throws SQLException {
		Connection conn = null;
		ValidacionAdecuacionesBusinessLogic vabl = new ValidacionAdecuacionesBusinessLogic( this.jniName );
		String mensaje = "";
		try {
			conn = getConnection();
			mensaje = AdecuacionManager.integraAdecuacionesFIAF( conn, nFolio, foliosAdec, cJustificacionA, cJustificacionR, u, cJustificacionNormativa, aEjercicioFiscal, cTipoAdecuacion, nNivel );
			ClasificacionAdecuacion ca = vabl.clasificaFIAF( nFolio );
			actualizaNivelFIAF( nFolio, ca.getNivel(), ca.getTipoAdecuacion() );
			mensaje += " \n El nivel de la FIAF es " + ca.getNivel() + " y su tipo es " + ca.getTipoAdecuacion();
			conn.commit();
		} catch ( Exception e ) {
			e.printStackTrace();
			conn.rollback();
		} finally {
			if ( conn != null )
				conn.close();
			conn = null;
		}
		return mensaje;
	}

	public String desIntegraFIAF( int nFolio, Caso c, String uLogin, Map m, String prefixPath, String[] foliosAdec ) throws Exception {
		Connection conn = null;
		String mensaje = "";
		try {
			// CasoBusinessLogic cbl = new
			// CasoBusinessLogic(GestionInterface.ATT_CONEXION);
			conn = getConnection();
			mensaje = AdecuacionManager.desIntegraFIAF( conn, nFolio, foliosAdec );
			conn.commit();
			/*
			 * Caso cReloaded = new Caso(); cReloaded.setIdCaso(c.getIdCaso());
			 * cReloaded = CasoManager.select(conn, cReloaded);
			 * cbl.avanzaCaso(cReloaded, uLogin, "", new String[] { "TERMINAR"
			 * }, new String[] { "TERMINAR" }, m, prefixPath);
			 */
		} catch ( Exception exc ) {
			log.error( exc );
			conn.rollback();
			throw new Exception( exc );
		} finally {
			if ( conn != null )
				conn.close();
			conn = null;
		}
		return mensaje;
	}

	public String avanzaAdecFIAF( int nFolio, String[] foliosAdec ) throws Exception {
		Connection conn = null;
		String mensaje = "";
		try {
			conn = getConnection();
			mensaje = AdecuacionManager.avanzaAdecFIAF( conn, nFolio, foliosAdec );
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
		return mensaje;
	}

	public String agregaJustificaciones( int nFolio, String justificacionA, String justificacionR, String justificacionNormativa ) throws Exception {
		Connection conn = null;
		String mensaje = "";
		try {
			conn = getConnection();
			mensaje = AdecuacionManager.agregaJustificaciones( conn, nFolio, justificacionA, justificacionR, justificacionNormativa );
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
		return mensaje;
	}

	public String autorizaIntegracionFIAF( Caso c, String nNumSicop, String cRecMotivSicop, String nNumMAP, String cRecMotivMAP, Map m, String prefixPath, String uLogin, String cCentroContable, Usuario u ) throws SQLException {
		String mensaje = "";
		List<String> arrLResult = null;
		Connection conn = null;

		try {
			CasoBusinessLogic cbl = new CasoBusinessLogic( GestionInterface.ATT_CONEXION );
			conn = getConnection();
			AdecuacionManager.autorizaFIAF( conn, c, nNumSicop, cRecMotivSicop, nNumMAP, cRecMotivMAP, u.getLogin(), prefixPath );
			ContableInterface conInt = new AplicacionContable();
			log.debug( "Inicia Autorización aplicacion contable " + new Timestamp( System.currentTimeMillis() ) );

			AplicarContableReturn acr = conInt.aplicarContableNuevo( conn, c, "tFIAFAUTEncabezado", "v_AdecuacionAutDetFIAF", "nFolioFIAFaut", new Integer( c.getFolio().substring( c.getFolio().lastIndexOf( '-' ) + 1 ) ).intValue(), "ADECUACIONAUT", m, prefixPath, uLogin, "NO" );// el

			arrLResult = acr.getMessageList();

			log.debug( "Termina Autorización Aplicacion contable " + new Timestamp( System.currentTimeMillis() ) );

			if ( acr.isSuccess() ) {
				String to = getListaCorreos( c );
				String body = ( !correoProduccion ? "CORREO DE PRUEBA <br>" : "" ) + ( !correoProduccion ? "este correo le hubiera llegado a: " + to + "<br> <br>" : "" ) + "Para su conocimiento y efectos correspondientes, se le informa que <b>fue autorizado</b> el folio siguiente:<br>" + c.getTipoCaso().getDescripcion() + " No. SAI: <b>" + c.getFolio() + ( nNumSicop != null ? "</b> con folio SICOP: <b>" + nNumSicop : "" ) + ( nNumMAP != null ? "</b> con folio MAP: <b>" + nNumMAP : "" ) + "</b><br>" + "Mismo que ya cuenta con estatus de autorizado en el SAI. Para obtener el folio de autorización MAP, revisar en consulta su afectación<br>" + "<b>Nota importante<br>" + "Los calendarios del Folio de Adecuación MAP, no necesariamente coinciden con los registrados en las " + "afectaciones del SAI y el SICOP, dado que su política de operación es diferente al de éstos. Por lo anterior, " + "se les recuerda que los calendarios para la operación de sus adecuaciones y pagos, son los registrados tanto " + "en el SAI como en el SICOP.</b>.";
				try {
					if ( !correoProduccion )
						to = "" + u.getU_email();
					AlarmaManager.procesaAlarmaCNF( conn, prefixPath, c.getCasoOperacion( 0 ), c, "", to, body );
				} catch ( Exception exmail ) {
					log.error( "No se logro enviar el correo de autorizacion de adecuacion: " + exmail );
				}
			} else {
				conn.rollback();

				String to = u.getU_email();
				String body = ( !correoProduccion ? "CORREO DE PRUEBA <br>" : "" ) + ( !correoProduccion ? "este correo le hubiera llegado a: " + to + "<br> <br>" : "" ) + "Para su conocimiento y efectos correspondientes, se le informa que el folio siguiente:<br><b>" + c.getTipoCaso().getDescripcion() + "</b> No. SAI: <b>" + c.getFolio() + "</b><br>" + "<b>NO PUDO SER CANCELADO</b>, debido a:<br>" + acr.getMessageList().get( 0 );
				try {
					if ( !correoProduccion )
						to = "" + u.getU_email();
					AlarmaManager.procesaAlarmaCNF( conn, prefixPath, c.getCasoOperacion( 0 ), c, "", to, body );
				} catch ( Exception exmail ) {
					log.error( "No se logro enviar el correo: " + exmail );
				}
			}

			ArrayList<Integer> idsCaso = AdecuacionManager.getIdCasoAdecuacionFIAFCorreo( conn, new Integer( c.getFolio().substring( c.getFolio().lastIndexOf( '-' ) + 1 ) ).intValue() );
			for ( int j = 0; j < idsCaso.size(); j++ ) {
				Caso caso = new Caso();
				caso.setIdCaso( idsCaso.get( j ) );
				caso = CasoManager.select( conn, caso );

				String to = getListaCorreos( caso );
				String body = ( !correoProduccion ? "CORREO DE PRUEBA <br>" : "" ) + ( !correoProduccion ? "este correo le hubiera llegado a: " + to + "<br> <br>" : "" ) + "Para su conocimiento y efectos correspondientes, se le informa que <b>fue autorizado</b> el folio siguiente:<br>" + c.getTipoCaso().getDescripcion() + " No. SAI: <b>" + caso.getFolio() + ( nNumSicop != null ? "</b> con folio SICOP: <b>" + nNumSicop : "" ) + ( nNumMAP != null ? "</b> con folio MAP: <b>" + nNumMAP : "" ) + "</b><br>" + "Mismo que ya cuenta con estatus de autorizado en el SAI. Para obtener el folio de autorización MAP, revisar en consulta su afectación<br>" + "<b>Nota importante<br>" + "Los calendarios del Folio de Adecuación MAP, no necesariamente coinciden con los registrados en las " + "afectaciones del SAI y el SICOP, dado que su política de operación es diferente al de éstos. Por lo anterior, " + "se les recuerda que los calendarios para la operación de sus adecuaciones y pagos, son los registrados tanto " + "en el SAI como en el SICOP.</b>.";
				try {
					if ( !correoProduccion )
						to = "" + u.getU_email();
					AlarmaManager.procesaAlarmaCNF( conn, prefixPath, c.getCasoOperacion( 0 ), c, "", to, body );
				} catch ( Exception exmail ) {
					log.error( "No se logro enviar el correo de autorizacion de adecuacion: " + exmail );
				}
			}
			try {
				cbl.avanzaCaso( c, uLogin, "", new String [] { "CONSULTA_FIAF" }, new String [] { "consulta_fiaf" }, m, prefixPath );
			} catch ( Exception exc ) {
				log.error( "No se logro avanzar el caso de autorización de Integracion de adecuacion FIAF " + c.getFolio() + " por: " + exc );
			}

			conn.commit();

		} catch ( Exception exc ) {
			conn.rollback();
			log.error( exc );
			arrLResult.add( exc.getLocalizedMessage() );

			try {
				conn.rollback();
			} catch ( Exception ex ) {
				log.warn( "En Rollback", ex );
			}
		} finally {
			if ( conn != null )
				conn.close();
			conn = null;
		}

		Iterator<String> iteraMensajes = arrLResult.iterator();
		while ( iteraMensajes.hasNext() ) {
			mensaje += iteraMensajes.next();
		}

		return mensaje;
	}

	public String aplicaFIAF( int nIdCaso, Usuario usuario, String aEjercicioFiscal, String cRamo, String cUR, Caso c, String cCentroContable, String cFechaAplica, Map m, String prefixPath ) throws SQLException {
		Connection conn = null;
		List<String> arrLResult = null;
		String mensaje = "";
		String userID = usuario.getLogin();

		try {
			DateFormat dateFormatter = new SimpleDateFormat( "dd/MM/yyyy" );
			java.sql.Date fAplicacion;

			// Este codigo es para regularizar
			if ( ( cFechaAplica == null ) || ( cFechaAplica.trim().length() == 0 ) ) {
				if ( c.getCasoDato( "FECHA_AP_CONT" ).getValor() != null )
					cFechaAplica = c.getCasoDato( "FECHA_AP_CONT" ).getValor();
				else {
					cFechaAplica = dateFormatter.format( new Date() );
				}
			}

			try {
				fAplicacion = new java.sql.Date( dateFormatter.parse( cFechaAplica ).getTime() );
			} catch ( ParseException pex ) {
				fAplicacion = new java.sql.Date( System.currentTimeMillis() );
			}

			CasoBusinessLogic cbl = new CasoBusinessLogic( GestionInterface.ATT_CONEXION );
			conn = cbl.getConnection();

			AdecuacionManager.agregaFechaAplicacionFIAF( conn, nIdCaso );

			if ( nIdCaso > 0 ) {
				ContableInterface conInt = new AplicacionContable();
				log.debug( "Inicia aplicacion contable" + new Timestamp( System.currentTimeMillis() ) + " Para  Folio:" + nIdCaso );

				AplicarContableReturn acr = conInt.aplicarContableNuevo( conn, c, "tFIAFEncabezado", "v_AdecuacionDetFIAF", "nFolioFIAF", new Integer( c.getFolio().substring( c.getFolio().lastIndexOf( '-' ) + 1 ) ).intValue(), "ADECUACION", m, prefixPath, userID, "NO" );

				arrLResult = acr.getMessageList();

				log.debug( "Termina Aplicacion contable " + new Timestamp( System.currentTimeMillis() ) + " Para  Folio:" + nIdCaso );

				Caso cReloaded = new Caso();
				cReloaded.setIdCaso( c.getIdCaso() );
				cReloaded = CasoManager.select( conn, cReloaded );

				if ( acr.isSuccess() ) {
					conn.commit();
					// int folio = new Integer( c.getFolio().substring(
					// c.getFolio().lastIndexOf( '-' ) + 1 ) ).intValue();
					// actualizaPolizaFIAF(folio,"false");
					// avanzaCaso tiene su propia connection, en caso de fallar
					// de todos modos se conserva la app cont
					cbl.avanzaCaso( cReloaded, userID, "", new String [] { "JEFATURA_ADECUACIONES" }, new String [] { "validar_normatividad" }, m, prefixPath );
				} else {
					conn.rollback();
					// avanzaCaso tiene su propia connection, en caso de fallar
					// de todos modos se conserva la app cont
					/*
					 * if (arrLResult.get(0).contains("sido aplicado"))
					 * cbl.avanzaCaso(cReloaded, userID, "", new String[] {
					 * "JEFATURA_ADECUACIONES" }, new String[] {
					 * "validar_normatividad" }, m, prefixPath); else
					 * cbl.avanzaCaso(cReloaded, userID, "", new String[] {
					 * "CAPTURISTA_FIAF" }, new String[] { "captura_fiaf" }, m,
					 * prefixPath);
					 */
				}

			} else {
				log.debug( "Error. no determinado se da rollback" );
				conn.rollback();
			}
		} catch ( Exception exc ) {
			log.error( exc );
			arrLResult.add( exc.getLocalizedMessage() );
			try {
				conn.rollback();
			} catch ( Exception ex ) {
				log.warn( "En Rollback", ex );
			}
		} finally {
			if ( conn != null )
				conn.close();

			conn = null;
		}
		// se tiene que validar contra catalogos de restricciones para la
		// proxima etapa
		// se tiene que validad que cuadre para la proxima vercion
		// tiene que validar si es calendario para la proxima mas las que se
		// acomulen esta semana
		// si la verificacione es correcta Guarda y aplica contablemente
		Iterator<String> iteraMensajes = arrLResult.iterator();
		while ( iteraMensajes.hasNext() ) {
			mensaje += iteraMensajes.next();
		}
		return mensaje;
	}

	public String cancelarFIAF( Caso c, Map m, String prefixPath, Usuario objUsuario, String cFecha, String motivoRechazo ) throws Exception {

		String retVal = null;
		Connection conn = null;

		try {
			ContableInterface ci = new AplicacionContable();
			CasoBusinessLogic cbl = new CasoBusinessLogic( GestionInterface.ATT_CONEXION );
			conn = cbl.getConnection();

			AplicarContableReturn acr = ci.cancelarAppContableNueva( conn, c, "tFIAFEncabezado", "v_AdecuacionDetFIAF", "nFolioFIAF", new Integer( c.getFolio().substring( c.getFolio().lastIndexOf( '-' ) + 1 ) ).intValue(), "ADECUACION", m, prefixPath, objUsuario.getLogin(), cFecha );

			Caso cReloaded = new Caso();
			cReloaded.setIdCaso( c.getIdCaso() );
			cReloaded = CasoManager.select( conn, cReloaded );
			if ( acr.isSuccess() ) {
				/* VGC-20150817 Se cambia la manera de obtener los correos. */
				String to = getListaCorreos( c );
				String body = ( !correoProduccion ? "CORREO DE PRUEBA <br>" : "" ) + ( !correoProduccion ? "este correo le hubiera llegado a: " + to + "<br> <br>" : "" ) + "Para su conocimiento y efectos correspondientes, se les informa que <b>FUE RECHAZADO</b> el folio siguiente:<br><b>" + c.getTipoCaso().getDescripcion() + "</b><br>" + "No. SAI: <b>" + c.getFolio() + "</b><br>" + "<b>Justificación:<br>" + ( motivoRechazo != null ? "Motivo rechazo:" + motivoRechazo + "<BR>" : "" ) +

				// (motivosRechazo.get("cRecMotivMAP")!=null?"Motivo rechazo
				// MAP:"+motivosRechazo.get("cRecMotivMAP")+"<BR>":"")+
				// (motivosRechazo.get("cRecMotivSicop")!=null?"Motivo rechazo
				// SICOP:"+motivosRechazo.get("cRecMotivSicop")+"<BR>":"")+

						"Con base en lo anterior la afectación fue rechazada en el SAI para que procedan al replanteamiento que consideren pertinente.</b><br>" + "Saludos cordiales.";

				try {
					if ( !correoProduccion )
						to = "" + objUsuario.getU_email();
					AlarmaManager.procesaAlarmaCNF( conn, prefixPath, c.getCasoOperacion( 0 ), c, "", to, body );
				} catch ( Exception exmail ) {
					log.error( "No se logro enviar el correo: " + exmail );
				}
				conn.commit();
				cbl.avanzaCaso( cReloaded, objUsuario.getLogin(), "", new String [] { "CONSULTA_FIAF" }, new String [] { "consulta_fiaf" }, m, prefixPath );
			} else {
				conn.rollback();

				String to = objUsuario.getU_email();
				String body = ( !correoProduccion ? "CORREO DE PRUEBA <br>" : "" ) + ( !correoProduccion ? "este correo le hubiera llegado a: " + to + "<br> <br>" : "" ) + "Para su conocimiento y efectos correspondientes, se le informa que el folio siguiente:<br><b>" + c.getTipoCaso().getDescripcion() + "</b> No. SAI: <b>" + c.getFolio() + "</b><br>" + "<b>NO PUDO SER CANCELADO</b>, debido a:<br>" + acr.getMessageList().get( 0 );
				try {
					if ( !correoProduccion )
						to = "" + objUsuario.getU_email();
					AlarmaManager.procesaAlarmaCNF( conn, prefixPath, c.getCasoOperacion( 0 ), c, "", to, body );
				} catch ( Exception exmail ) {
					log.error( "No se logro enviar el correo: " + exmail );
				}

				/*
				 * if (acr.getMessageList().get(0).contains("sido aplicado") ||
				 * acr.getMessageList().get(0).contains("sido cancelado"))
				 * cbl.avanzaCaso(cReloaded, objUsuario.getLogin(), "", new
				 * String[] { "CONSULTA_FIAF" }, new String[] { "consulta_fiaf"
				 * }, m, prefixPath); else cbl.avanzaCaso(cReloaded,
				 * objUsuario.getLogin(), "", new String[] {
				 * "JEFATURA_ADECUACIONES" }, new String[] {
				 * "validar_normatividad" }, m, prefixPath);
				 */
			}
			retVal = acr.getMessageList().get( acr.getMessageList().size() - 1 );
		} catch ( Exception exc ) {
			log.error( exc );
			conn.rollback();
			throw new Exception( exc );
		} finally {
			if ( conn != null )
				conn.close();

			conn = null;
		}

		return retVal;
	}

	public int consecutivoSICOPFIAF( int nFolio ) throws SQLException {
		int nConsecutivoSICOP = 0;
		Connection conn = null;
		try {
			conn = getConnection();
			nConsecutivoSICOP = AdecuacionManager.consecutivoSICOPFIAF( conn, nFolio );
		} finally {
			if ( conn != null )
				conn.close();

			conn = null;
		}
		return nConsecutivoSICOP;
	}

	public int nivelFIAF( int nFolio ) throws SQLException {
		int nNivel = 0;
		Connection conn = null;
		try {
			conn = getConnection();
			nNivel = AdecuacionManager.nivelFIAF( conn, nFolio );
		} finally {
			if ( conn != null )
				conn.close();

			conn = null;
		}
		return nNivel;
	}

	public boolean tieneSicopFIAF( int folioAdecuacion ) throws SQLException {
		Connection conn = null;
		boolean tiene = false;
		try {
			conn = getConnection();
			int consecutivo = AdecuacionManager.consultaConsecutivoSicop( conn, folioAdecuacion );
			if ( consecutivo > 0 ) {
				tiene = true;
			}
		} catch ( Exception exc ) {
			log.error( exc );
			throw new SQLException( exc );
		} finally {

			if ( conn != null )
				conn.close();

			conn = null;
		}
		return tiene;
	}

	public boolean actualizaFolioSicopFIAFEncabezado( int folioSicop, int folioFIAF ) throws SQLException {
		Connection conn = null;
		boolean retVal = false;
		try {
			conn = getConnection();
			retVal = AdecuacionManager.actualizaFolioSicopFIAFEncabezado( conn, folioSicop, folioFIAF );
			conn.commit();
		} catch ( Exception exc ) {
			log.error( exc );
			conn.rollback();
			throw new SQLException( exc );
		} finally {

			if ( conn != null )
				conn.close();

			conn = null;
		}
		return retVal;
	}

	public AdecuacionCalendario adecuacionCalendarioFIAF( int folioFIAF ) throws SQLException {
		Connection conn = null;
		AdecuacionCalendario ac = new AdecuacionCalendario();
		// boolean retVal = false;
		try {
			conn = getConnection();
			ac = AdecuacionManager.adecuacionCalendarioFIAF( conn, folioFIAF );
			conn.commit();
		} catch ( Exception exc ) {
			log.error( exc );
			conn.rollback();
			throw new SQLException( exc );
		} finally {

			if ( conn != null )
				conn.close();

			conn = null;
		}

		return ac;
	}

	public FIAFEncabezado getFIAFEncabezado( int nFolio ) throws SQLException {
		FIAFEncabezado fe = new FIAFEncabezado();
		Connection conn = null;
		try {
			conn = getConnection();
			fe = AdecuacionManager.getFIAFEncabezado( conn, nFolio );
		} finally {
			if ( conn != null )
				conn.close();

			conn = null;
		}
		return fe;
	}

	public void actualizaJustificaciones( String justificacionA, String justificacionR, String justificacionN, int folioAdecuacion ) throws SQLException {
		Connection conn = null;
		try {
			conn = getConnection();
			AdecuacionManager.actualizaJustificaciones( conn, justificacionA, justificacionR, justificacionN, folioAdecuacion );
			conn.commit();
		} catch ( Exception exc ) {
			log.error( exc );
			conn.rollback();
			throw new SQLException( exc );
		} finally {

			if ( conn != null )
				conn.close();

			conn = null;
		}
	}

	public int actualizaPolizaFIAF( int folio, String aut ) throws SQLException {
		Connection conn = null;
		int retVal = 0;
		try {
			conn = getConnection();
			retVal = AdecuacionManager.actualizaPolizaFIAF( conn, folio, aut );
			conn.commit();
		} catch ( Exception exc ) {
			log.error( exc );
			conn.rollback();
			throw new SQLException( exc );
		} finally {

			if ( conn != null )
				conn.close();

			conn = null;
		}
		return retVal;
	}

	public ArrayList<Integer> getIdCasoAdecuacionFIAFCorreo( int nFolio ) throws SQLException {
		ArrayList<Integer> folios;
		Connection conn = null;
		try {
			conn = getConnection();
			folios = AdecuacionManager.getIdCasoAdecuacionFIAFCorreo( conn, nFolio );
		} finally {
			if ( conn != null )
				conn.close();

			conn = null;
		}
		return folios;
	}

	public ArrayList<Integer> getFoliosFIAFConsolidacion( int folioConsolidacion ) throws SQLException {
		ArrayList<Integer> folios;
		Connection conn = null;
		try {
			conn = getConnection();
			folios = AdecuacionManager.getFoliosFIAFConsolidacion( conn, folioConsolidacion );
		} finally {
			if ( conn != null )
				conn.close();

			conn = null;
		}
		return folios;
	}

	public List<Saldo> saldosExcelMultiReporte( String sql, String Cuenta, String cOrddeBy, String cGroupBy, String InfoRegMes, String TipoReporte, String Usuario, String nCtasPresup[], String dCtasPresup[] ) throws SQLException {
		List<Saldo> saldoList = new ArrayList<Saldo>();
		Connection conn = null;
		try {
			conn = getConnection();
			saldoList = ReporteManager.callMultiReporte( conn, sql, Cuenta, cOrddeBy, cGroupBy, InfoRegMes, TipoReporte, Usuario, nCtasPresup, dCtasPresup, "EPTRABAJO" );
		} finally {
			if ( conn != null )
				conn.close();

			conn = null;
		}
		return saldoList;
	}

	public Map<String, CorreoAdecuacion> mandaCorreoVigenciaAdecuacion( int id_oper, int dias ) throws SQLException {
		Map<String, CorreoAdecuacion> correos;
		Connection conn = null;
		try {
			conn = getConnection();
			correos = AdecuacionManager.getCorreoUsuarioAdecuacion( conn, id_oper, dias );
			for ( Iterator<String> iterator = correos.keySet().iterator(); iterator.hasNext(); ) {
				String key = iterator.next();
				CorreoAdecuacion cA = correos.get( key );
				String destinatario = cA.getCorreos();

				String mensaje = "";
				if ( id_oper == 1 ) {
					if ( cA.getAlarma() == null ) {
						mensaje = "Su adecuación No. " + key + " se localiza actualmente en captura. " + "Está a punto de terminar su vigencia y aún no se ha enviado a revisión, " + "por tal motivo el día de mañana se Cancelará automáticamente.";
						actualizaAlarma( "1", cA.getIdCaso() );
					}
				} else if ( id_oper == 5 ) {
					mensaje = "Su adecuación No. " + key + " se localiza actualmente en revisión. " + "Está a punto de terminar su vigencia y aún no se ha enviado la aplicación contable, " + "por tal motivo el día de mañana se cancelará automáticamente.";
					actualizaAlarma( "2", cA.getIdCaso() );
				}

				try {
					MailSender.sendMailMultipleRecipients( "mad", destinatario.split( ";" ), mensaje, "Adecuacion proxima a vencer" );
				} catch ( Exception e ) {
					e.printStackTrace();
				}

			}
		} finally {
			if ( conn != null )
				conn.close();

			conn = null;
		}
		return correos;
	}

	public Map<String, String> mandaCorreoCincoDias( int id_oper, int dias ) throws SQLException {
		Map<String, String> correos;
		List<CorreosJefatura> mensajes;
		Connection conn = null;
		try {

			conn = getConnection();
			correos = AdecuacionManager.getCorreoUsuarioAdecuacionCincoDias( conn, id_oper, dias );
			mensajes = AdecuacionManager.getMensaje( conn, id_oper, dias );
			String mensaje = "";
			List<String> correosAdecuacion = new ArrayList<String>();
			for ( Entry<String, String> c : correos.entrySet() ) {
				correosAdecuacion.add( c.getValue() );
			}

			for ( int i = 0; i < mensajes.size(); i++ ) {
				mensaje = "Su adecuación No. " + mensajes.get( i ).getnFolio() + " con fecha de aplicación " + mensajes.get( i ).getfAplicacion() + " ha excedido los cinco dias de su expedición, tomar las medidas necesarias. ";
				actualizaAlarmaCincoDias( 1, mensajes.get( i ).getnFolio() );
				try {
					MailSender.sendMailMultipleRecipients( "mad", correosAdecuacion.toArray( new String [correosAdecuacion.size()] ), mensaje, "Dias expiración" );
				} catch ( Exception e ) {
					e.printStackTrace();
				}
			}

		} finally {
			if ( conn != null )
				conn.close();

			conn = null;
		}
		return correos;
	}

	public Caso cancelaAdecuacionAutomatica( int id_oper, int dias ) throws SQLException, GestionException {
		Caso cReloaded = null;
		Map<String, CorreoAdecuacion> correos;
		Connection conn = null;
		try {
			conn = getConnection();
			correos = AdecuacionManager.getCorreoUsuarioAdecuacion( conn, id_oper, dias );
			for ( Iterator<String> iterator = correos.keySet().iterator(); iterator.hasNext(); ) {
				cReloaded = new Caso();
				String key = iterator.next();
				CorreoAdecuacion cA = correos.get( key );
				String destinatario = cA.getCorreos();
				// destinatario =
				// "vgarciac@axtel.com.mx"; //
				// para
				// pruebas
				String mensaje = "";
				mensaje = "Su adecuación No. " + key + " fue cancelada." + " Caducó su vigencia y no fue enviada a solicitud," + " por tal motivo se Canceló automáticamente.";
				cReloaded.setFolio( key );
				cReloaded.setIdTC( 3 );
				cReloaded = CasoManager.select( conn, cReloaded );
				if ( cReloaded != null ) {
					Date date = Calendar.getInstance().getTime();
					SimpleDateFormat sdf = new SimpleDateFormat( "dd/MM/yyyy" );
					String fecha = sdf.format( date );

					Map<String, String> data = new HashMap<String, String>();
					data.put( "FOLIO", cReloaded.getFolio() );
					data.put( "OPERADOR", "autoCancelacionAdec" );
					data.put( "FECHA_DOCUMENTO", fecha );
					data.put( "EJERCICIO_FISCAL", obtenEjercicioFiscal() );

					CasoBusinessLogic cbl = new CasoBusinessLogic( jniName );

					cbl.avanzaCaso( cReloaded, "autoCancelacionAdec", "Caso cancelado por vigencia", new String [] { "TERMINAR" }, new String [] { "TERMINAR" }, data, "" );
				}
				try {
					MailSender.sendMailMultipleRecipients( "mad", destinatario.split( ";" ), mensaje, "Adecuacion cancelada" );
				} catch ( Exception e ) {
					e.printStackTrace();
				}

			}
		} finally {
			if ( conn != null )
				conn.close();

			conn = null;
		}
		return cReloaded;
	}

	public void actualizaAlarma( String valor, int idCaso ) throws SQLException {
		Connection conn = null;
		try {
			conn = getConnection();
			AdecuacionManager.actualizaAlarma( conn, valor, idCaso );
			conn.commit();
		} catch ( Exception exc ) {
			log.error( exc );
			conn.rollback();
			throw new SQLException( exc );
		} finally {

			if ( conn != null )
				conn.close();

			conn = null;
		}
	}

	public void actualizaAlarmaCincoDias( int valor, int folio ) throws SQLException {
		Connection conn = null;
		try {
			conn = getConnection();
			AdecuacionManager.actualizaAlarmaCincoDias( conn, valor, folio );
			conn.commit();
		} catch ( Exception exc ) {
			log.error( exc );
			conn.rollback();
			throw new SQLException( exc );
		} finally {

			if ( conn != null )
				conn.close();

			conn = null;
		}
	}

	public void actualizaMotivoCancelacion( String motivo, int folio ) throws SQLException {
		Connection conn = null;
		try {
			conn = getConnection();
			AdecuacionManager.actualizaMotivoCancelacion( conn, motivo, folio );
			conn.commit();
		} catch ( Exception exc ) {
			log.error( exc );
			conn.rollback();
			throw new SQLException( exc );
		} finally {

			if ( conn != null )
				conn.close();

			conn = null;
		}
	}

	public int eliminaRegistrosDuplicados() {
		Connection conn = null;
		int eliminados = -1;
		try {
			conn = getConnection();
			eliminados = AdecuacionManager.borraOperacionesRepetidas( conn );
		} catch ( Exception e ) {
			log.error( "Error eliminando operaciones repetidas de adecuaciones " + e, e );
		} finally {
			try {
				CloseObject.closeObject( conn, false );
			} catch ( Exception e ) {
				log.error( e, e );
			}
		}

		return eliminados;
	}

	public AdecuacionEncabezado getAdecuacionEncabezado( String nFolioAdecuacion ) throws Exception {

		Connection conn = null;
		try {
			conn = getConnection();
			return Adecuacion.getAdeacuacionEncabezado( nFolioAdecuacion, conn );
		} finally {
			CloseObject.closeObject( conn, false );
		}
	}

	public boolean esUsuarioAdecuacion( Usuario u ) {

		Map<?, ?> grupos = u.getGrupos();
		boolean esUsuarioAdecuaciones = false;

		if ( grupos != null )
			for ( Iterator<?> i = grupos.keySet().iterator(); i.hasNext(); ) {
				String grupoNombre = ( String ) i.next();
				if ( grupoNombre.contains( "ADECUA" ) ) {
					esUsuarioAdecuaciones = true;
					break;
				}
			}

		return esUsuarioAdecuaciones;

	}

	public Adecuacion cargaAdecuacionProyecto( int nFolioAdecuacion ) throws Exception {
		Connection conn = null;
		Adecuacion adecuacion = null;
		try {
			conn = getConnection();
			adecuacion = AdecuacionManager.cargaAdecuacionProyecto( conn, nFolioAdecuacion );
			return adecuacion;
		} finally {
			CloseObject.closeObject( conn, false );
		}
	}

	public Adecuacion cargaAdecuacion( int nFolioAdecuacion ) throws Exception {
		Connection conn = null;
		Adecuacion adecuacion = null;
		try {
			conn = getConnection();
			adecuacion = AdecuacionManager.cargaAdecuacion( conn, nFolioAdecuacion );
			return adecuacion;
		} finally {
			CloseObject.closeObject( conn, false );
		}
	}

	public int buscaFolioIntegracion() throws SQLException {
		int nFolioIntegra = 0;
		Connection conn = null;
		try {
			conn = getConnection();
			nFolioIntegra = AdecuacionManager.buscaFolioIntegracion( conn );
		} finally {
			if ( conn != null )
				conn.close();

			conn = null;
		}
		return nFolioIntegra;
	}

	public ArrayList integraAdecuaciones2( int nFolio, String cFoliosAdecuaciones, Usuario u ) throws SQLException {
		String[] arrFolios = cFoliosAdecuaciones.split( "," );
		ArrayList<ArrayList<String>> arrDatsoGuardados = new ArrayList<>();
		Connection conn = null;
		try {
			conn = getConnection();
			AdecuacionManager.integraAdecuaciones2( conn, nFolio, arrFolios, u );
			conn.commit();
			arrDatsoGuardados.addAll( AdecuacionManager.buscaIntegrados( conn, nFolio ) );
		} finally {
			if ( conn != null )
				conn.close();

			conn = null;

		}
		return arrDatsoGuardados;
	}

	public ArrayList<Object> consultaFap02IADEAmpliaExcel( int nFolio ) throws Exception {
		ArrayList<Object> arrIntegradas = new ArrayList<Object>();
		Connection conn = null;
		try {
			conn = getConnection();
			arrIntegradas.add( AdecuacionManager.consultaFap02IADEAmpliaExcel( conn, nFolio ) );
		} finally {
			if ( conn != null )
				conn.close();

			conn = null;

		}
		return arrIntegradas;
	}

	public ArrayList<ArrayList> consultaFap02IADEReduceExcel( int nFolio ) throws SQLException {
		ArrayList<ArrayList> arrIntegradas = new ArrayList<>();
		Connection conn = null;
		try {
			conn = getConnection();
			arrIntegradas.add( AdecuacionManager.consultaFap02IADEReduceExcel( conn, nFolio ) );
		} finally {
			if ( conn != null )
				conn.close();

			conn = null;

		}
		return arrIntegradas;
	}

	public List<Fap01> cargaFAP01( int nFolio ) throws Exception {
		Connection conn = null;
		List<Fap01> r = null;
		try {
			conn = getConnection();
			r = AdecuacionManager.cargaFAP01( conn, nFolio );
			return r;
		} finally {
			CloseObject.closeObject( conn, false );
		}
	}

	public String tipoAdecuacion( int nFolio ) throws SQLException {
		Connection conn = null;
		// boolean retVal = false;
		String tipoAdec = "";
		try {
			conn = getConnection();
			tipoAdec = AdecuacionManager.obtieneTipoAdec( conn, nFolio );

		} catch ( Exception exc ) {
			log.error( exc );
			throw new SQLException( exc );
		} finally {

			if ( conn != null )
				conn.close();

			conn = null;
		}
		return tipoAdec;

	}

	public List<String> validaIntegracionNeteo( int nFolioIADE ) throws Exception {
		Connection conn = null;
		try {
			conn = getConnection();
			List<String> errores = AdecuacionManager.validaIntegracionNeteo( conn, nFolioIADE );
			return errores;
		} finally {
			CloseObject.closeObject( conn, false );
		}
	}

	public List<String> obtenInvolucradosAdecuacion( Caso c ) throws Exception {
		Connection conn = null;
		try {
			conn = getConnection();
			return AdecuacionManager.getInvolucradosAdecuacion( conn, c );
		} finally {
			CloseObject.closeObject( conn );
		}
	}

	/**
	 * VGC-20150817 Se cambia la manera de obtener los correos. Al parecer al
	 * hacer join de bitacora vs operacion se generan interbloqueos que causan
	 * lentitud en el sistema. La estrategia seguida es: <br>
	 * 1) Obtener todos los involucrados en el caso, haciendo un select directo
	 * a cg_bitacora y cerrando la conexion inmediatamente. <br>
	 * 2) Si esta activa la notificacion a todo el grupo de jefatura se
	 * incluyen. <br>
	 * 3) Se eliminan duplicados <br>
	 * 4) Se obtienen los correos de los usuarios y se devueleven como una sola
	 * cadena separada por ;
	 * 
	 * @param c
	 *            Caso activo
	 * @return correos de los usuarios y se devueleven como una sola cadena
	 *         separada por ;
	 * @throws Exception
	 */
	public String getListaCorreos( Caso c ) throws Exception {
		String correos = "";
		Connection conn = null;
		boolean incluyeGrupoJefatura = true;

		try {
			ConfiguraAplicativoManager cam = new ConfiguraAplicativoManager();
			incluyeGrupoJefatura = "S".equalsIgnoreCase( cam.getPropiedadSistema( "CORREO_JEFATURA_ADEC" ) );
		} catch ( Exception e ) {
			log.warn( e );
		}

		try {

			/* Elimina duplicados */
			Set<String> involucrados = new HashSet<String>( obtenInvolucradosAdecuacion( c ) );

			/*
			 * Si esta activo el envio al grupo de Jefatura, se agregan, en caso
			 * contrario solo envia correo a los involucrados
			 * Captura-Revisa-autoriza
			 */
			conn = getConnection();

			if ( incluyeGrupoJefatura ) {
				involucrados.addAll( GrupoManager.selectAllMembers( conn, ( new Grupo( "JEFATURA_ADECUACIONES" ) ) ) );
			}

			correos = AdecuacionManager.getListaCorreos( conn, involucrados );
			return correos;
		} finally {
			CloseObject.closeObject( conn );
		}
	}

	public ResultadoSaldos validaApartado( int nFolioAdecuacion, int id_oper ) throws Exception {

		Connection conn = null;
		ArrayList<String> msjSaldos = new ArrayList<String>();
		ResultadoSaldos resultado = new ResultadoSaldos();

		try {

			conn = getConnection();

			msjSaldos = AdecuacionManager.validaSaldoDispobible( conn, nFolioAdecuacion );

			if ( null != msjSaldos && msjSaldos.size() > 0 ) {
				resultado.setError( msjSaldos );
				return resultado;
			}

		} catch ( Exception e ) {
			try {
				conn.rollback();
			} catch ( Exception eRB ) {
				log.warn( "Problemas realizando el rollback: " + eRB );
			}

			log.error( e, e );
		} finally {
			CloseObject.closeObject( conn, false );
		}
		return resultado;
	}

	public ResultadoSaldos validaEPPlurianual( Connection conn, int nFolioAdecuacion ) throws Exception {
		ArrayList<String> msjSaldos = new ArrayList<String>();
		ResultadoSaldos resultado = new ResultadoSaldos();

		try {

			conn = getConnection();

			msjSaldos = AdecuacionManager.validaPlurianueles( conn, nFolioAdecuacion );

			if ( null != msjSaldos && msjSaldos.size() > 0 ) {
				resultado.setError( msjSaldos );
				return resultado;
			}

		} catch ( Exception e ) {
			try {
				conn.rollback();
			} catch ( Exception eRB ) {
				log.warn( "Problemas realizando el rollback: " + eRB );
			}

			log.error( e, e );
		} finally {
			CloseObject.closeObject( conn, false );
		}
		return resultado;
	}

	public ArrayList buscaDatosSIAFFSICOP( String lineaCaptura, int folio ) throws SQLException {
		ArrayList<Object> arrResultado = new ArrayList<>();
		Connection conn = null;
		// boolean retVal = false;
		// int nConsecutivoSicop = 0;
		try {
			conn = getConnection();
			arrResultado = AdecuacionManager.buscaDatosSIAFFSICOP( conn, lineaCaptura, folio );
		} catch ( Exception exc ) {
			log.error( exc );
			throw new SQLException( exc );
		} finally {

			if ( conn != null )
				conn.close();

			conn = null;
		}
		return arrResultado;
	}

	public boolean controlFonden() throws SQLException {
		boolean esControlFonden = false;
		Connection conn = null;
		try {
			conn = getConnection();
			esControlFonden = AdecuacionManager.esControlFonden( conn );
		} catch ( Exception e ) {
			log.error( e );
			throw new SQLException( e );
		} finally {
			if ( conn != null )
				conn.close();
			conn = null;
		}
		return esControlFonden;
	}

	public boolean esServicioActivoSIPLAN( Connection conn ) throws Exception {
		String setting = ConfiguraAplicativoManager.getSystemSetting( conn, "ACTIVA_WS_NOTIFICA_SIPLAN" );

		return setting != null && Boolean.parseBoolean( setting );
	}

	private boolean adecuacionMetasNotificada( Connection conn, int folioAdecuacion ) throws WSException, SQLException {

		return AdecuacionEncabezadoManager.readAdecuacionNotificadaEncabezado( conn, folioAdecuacion ) != null;

	}

	private void notificaCancelaAdecuacionSIPLAN( Connection conn, int folioAdecuacion, String motivoRechazo ) throws WSException {

		AdecuacionCancelacion adecuacionCancelacion = new AdecuacionCancelacion();
		adecuacionCancelacion.setFolio( folioAdecuacion );
		adecuacionCancelacion.setJustificacion( motivoRechazo );

		notificaAdecuacionMetasCliente.setUrlService( WS_CANCEL_ADECUACION_SIPLAN );
		AdecuacionRespuesta respuesta = notificaAdecuacionMetasCliente.enviaNotificacionCancelacion( conn, adecuacionCancelacion );

		if ( respuesta != null && respuesta.getStatus() != 1 )
			throw new WSException( "No fue posible cancelar la adecuacion en SIPLAN: " + respuesta.getMensaje() );

	}

	public boolean notificaAdecuacionSIPLAN( Connection conn, int folioAdecuacion ) throws Exception {

		if ( !esServicioActivoSIPLAN( conn ) )
			return false;

		String adecuacionTipo = StringUtils.trimToEmpty( AdecuacionEncabezadoManager.readAdecuacionTipo( conn, folioAdecuacion ) ).toUpperCase();
		if ( "CALENDARIO".equals( adecuacionTipo ) )
			return false;

		AdecuacionResumen adecuacionNotificar = notificaAdecuacionMetasCliente.readAdecuacionNotificar( conn, folioAdecuacion );
		if ( adecuacionNotificar != null && ( adecuacionNotificar.getDetalle() != null && adecuacionNotificar.getDetalle().size() > 0 ) ) {

			String validaMETA = AdecuacionEncabezadoManager.identificaMovimiento( conn, folioAdecuacion );
			if ( "NO".equals( validaMETA ) )
				return false;

			List<UsuarioSiplan> usuariosCapturistas = AdecuacionRespuestaDAO.leeUsuariosCapturistas( conn, folioAdecuacion );
			adecuacionNotificar.setEnlacesSai( usuariosCapturistas );
			notificaAdecuacionMetasCliente.setUrlService( WS_ADECUACION_SIPLAN );

			AdecuacionRespuesta respuesta = notificaAdecuacionMetasCliente.enviaNotificacion( conn, adecuacionNotificar );

			if ( respuesta.getUsuariosNotificar() != null && respuesta.getUsuariosNotificar().size() > 0 ) {
				AdecuacionRespuestaDAO.insertaRespuesta( conn, folioAdecuacion, respuesta );
				notificaAdecuacionMetasCliente.guardaAdecuacionNotificacion( conn, adecuacionNotificar );

				return true;
			} else {
				return false;
			}

		}
		return false;
	}

	public ArrayList<ArrayList> actividadInstitucional( int folio ) throws Exception {
		ArrayList<ArrayList> arrActividadInstitucional = new ArrayList<>();
		Connection conn = null;
		try {
			conn = getConnection();
			arrActividadInstitucional.add( AdecuacionManager.consultaActividadInstitucional( conn, folio ) );
		} finally {
			if ( conn != null )
				conn.close();

			conn = null;

		}
		return arrActividadInstitucional;
	}

	public ArrayList<ArrayList> actividadInstitucionalIADE( int folio ) throws Exception {
		ArrayList<ArrayList> arrActividadInstitucional = new ArrayList<>();
		Connection conn = null;
		try {
			conn = getConnection();
			arrActividadInstitucional.add( AdecuacionManager.consultaActividadInstitucionalIADE( conn, folio ) );
		} finally {
			if ( conn != null )
				conn.close();

			conn = null;

		}
		return arrActividadInstitucional;
	}

	public void esReserva( int nFolio, String adecuacionReserva ) throws SQLException {
		Connection conn = null;

		try {
			conn = getConnection();
			AdecuacionManager.esReserva( conn, nFolio, adecuacionReserva );
			conn.commit();
		} catch ( Exception exc ) {
			log.error( exc );
			conn.rollback();
			throw new SQLException( exc );
		} finally {

			if ( conn != null )
				conn.close();

			conn = null;
		}
	}

	private String esAadecuacionReserva( Connection conn, int nIdCaso ) throws SQLException {
		String esReserva = "";

		try {
			esReserva = AdecuacionManager.esAdecuacionReserva( conn, nIdCaso );
		} catch ( Exception exc ) {
			log.error( exc );
			throw new SQLException( exc );
		} finally {

		}
		return esReserva;
	}

	public String justificacionAmpIADE( int folio ) throws Exception {
		String justificacionAmp = "";
		Connection conn = null;

		try {

			conn = getConnection();
			justificacionAmp = AdecuacionManager.justificacionAmpIADE( conn, folio );

		} finally {
			if ( conn != null )
				conn.close();

			conn = null;

		}
		return justificacionAmp;
	}

	public String justificacionRedIADE( int folio ) throws Exception {
		String justificacionRed = "";
		Connection conn = null;

		try {
			conn = getConnection();
			justificacionRed = AdecuacionManager.justificacionRedIADE( conn, folio );
		} finally {
			if ( conn != null )
				conn.close();

			conn = null;

		}
		return justificacionRed;
	}

}
