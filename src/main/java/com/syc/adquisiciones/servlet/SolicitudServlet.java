package com.syc.adquisiciones.servlet;


import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.syc.adquisiciones.businessLogic.SolicitudBusinessLogic;
import com.syc.adquisiciones.core.DatosRequisicion;
import com.syc.adquisiciones.util.Util;
import com.syc.contable.CompromisoBussinessLogic;
import com.syc.contable.ContableInterface;
import com.syc.contable.core.AplicacionContable;
import com.syc.contable.core.AplicacionContable.AplicarContableReturn;
import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoDatoManager;
import com.syc.gestion.core.CasoManager;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.custom.FolioGeneratorInterface;
import com.syc.gestion.servlet.GestionInterface;


public class SolicitudServlet extends HttpServlet implements GestionInterface {

	private static final long	serialVersionUID	= 1L;
	private static String		jndiName			= null;
	private static Logger		log					= Logger.getLogger( SolicitudServlet.class );
	private Connection			conn				= null;
	private CallableStatement	cmst				= null;
	private JSONArray			arrayObj;
	private JSONObject			jsonObj;
	private PrintWriter			out					= null;
	private String				folioGenerator		= null;
	private Usuario				usuario;
	private String				cEjercicio, today;
	private String				tempDir;

	public void init( ServletConfig config ) throws ServletException {
		// Crea la conexión a BD
		super.init( config );
		try {
			InitialContext ic = new InitialContext();
			jndiName = ( String ) ic.lookup( "java:comp/env/dataSourceRefName" );
			if ( jndiName == null ) {
				jndiName = "jdbc/gestion";
				log.info( "Environment Entry \"dataSourceRefName\" nula usando default \"" + jndiName + "\"" );
			} else
				log.info( "dataSourceRefName=" + jndiName );
		} catch ( NamingException exc ) {
			jndiName = "jdbc/gestion";
			log.info( "Environment Entry \"dataSourceRefName\" no definida usando default \"" + jndiName + "\"" );
		}
		try {
			InitialContext ic = new InitialContext();
			folioGenerator = ( String ) ic.lookup( "java:comp/env/folioGeneratorInterface" );
			if ( folioGenerator == null ) {
				folioGenerator = "com.syc.gestion.custom.DefaultFolioGenerator";
				log.info( "Environment Entry \"folioGeneratorInterface\" nula usando default \"" + folioGenerator + "\"" );
			} else
				log.info( "folioGeneratorInterface=" + folioGenerator );
		} catch ( NamingException exc ) {
			folioGenerator = "com.syc.gestion.custom.DefaultFolioGenerator";
			log.info( "Environment Entry \"folioGeneratorInterface\" no definida usando default \"" + folioGenerator + "\"" );
		}

		tempDir = config.getInitParameter( "tempDir" );
		if ( tempDir == null ) {
			tempDir = config.getServletContext().getRealPath( "/" ) + "upload" + File.separator;

			File fDir = new File( tempDir );
			if ( !fDir.exists() )
				if ( !fDir.mkdirs() )
					throw new ServletException( "No se pudo crear el directorio " + tempDir );
		}
	}

	public void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
		// Inicialización de variables
		arrayObj = new JSONArray();
		jsonObj = new JSONObject();
		HttpSession session = request.getSession( false );
		if ( session == null ) {
			log.warn( "No se logro crear la sesion" );
			throw new ServletException( "No se logro crear la sesion" );
		}
		cEjercicio = ( String ) session.getAttribute( GestionInterface.ATT_ReqEjercicio );
		String DATE_FORMAT = "dd/MM/yyyy";
		SimpleDateFormat sdf = new SimpleDateFormat( DATE_FORMAT );
		Calendar c1 = Calendar.getInstance(); // today
		today = sdf.format( c1.getTime() );

		usuario = ( Usuario ) session.getAttribute( ATT_USER );

		if ( usuario == null ) {
			log.warn( "No hay Usuario en sesion" );
			session.invalidate();
			response.sendRedirect( "../index.jsp" );
			return;
		}
		String strParam = request.getParameter( "Param" );

		// Obtiene la operación que se manda como parámetro en la llamada GET
		int tipoOperacion = Integer.parseInt( request.getParameter( "operacion" ) );
		log.debug( "operacion: " + tipoOperacion );
		try {
			switch ( tipoOperacion ) {
				case 0:
					generaGuardaCaso( request, response, session, ( GestionInterface.IDTC_APARTADO + "" ), "Apartado", new String [] { "ADJUNTO_APARTADO" }, new String [] { "ADJUNTO_APTD" } );
				break;
				case 1:
					aplicaContablemente( strParam, request, response, session, new String [] { "CONSULTA_APARTADO" }, new String [] { "CONSULTA_APTD" } );
				break;
				case 2:

					avanzaCaso( request, response, session, new String [] { "VENTANILLA_APARTADO" }, new String [] { "VIGENCIA_APTD" } );
				break;
				case 3:
					avanzaCaso( request, response, session, new String [] { "CONSULTA_APARTADO" }, new String [] { "CONSULTA_APTD" } );
				break;
				case 4:
					avanzaCaso( request, response, session, new String [] { "ADJUNTO_APARTADO" }, new String [] { "ADJUNTO_APTD" } );
				break;
				case 5:
					devuelveContablemente( strParam, request, response, session, new String [] { "ADJUNTO_APARTADO" }, new String [] { "ADJUNTO_APTD" } );
				break;
				case 6:
					AutorizaApartado( request, response, session, new String [] { "CONSULTA_APARTADO" }, new String [] { "CONSULTA_APTD" } );
				break;
				case 7:
					generaGuardaCaso_RT( request, response, session, ( GestionInterface.IDTC_APARTADO + "" ), "Apartado", new String [] { "ADJUNTO_APARTADO" }, new String [] { "ADJUNTO_APTD" } );
				break;
				case 8: //devuelve el apartado
					devuelveApartado(request, response, session, ( GestionInterface.IDTC_APARTADO + "" ), "Apartado", new String [] { "ADJUNTO_APARTADO" }, new String [] { "ADJUNTO_APTD" } );
					break;	
			}
		} catch ( Exception e ) {
			// TODO: handle exception
			log.error( e.getMessage() );
			e.printStackTrace();
		}

	}

	private void adjuntarArchivoVigenciaRequisicion( HttpServletRequest request, HttpServletResponse response, HttpSession session ) throws ServletException, IOException {

		out = response.getWriter();
		Caso c = null;
		// Connection conn=null;
		CasoBusinessLogic cbl = new CasoBusinessLogic( jndiName );

		//String tipo, ue, idConsolidado;
		//int consecutivo;
		// Datos del consolidado
		// tipo=(String)session.getAttribute(GestionInterface.ATT_ConTipoConsolidado);
		// ue=(String)session.getAttribute(GestionInterface.ATT_ConUnidadEjec);
		// consecutivo=Integer.parseInt((String)session.getAttribute(GestionInterface.ATT_ConConsecutivo));

		/*
		 * if (usuario == null) { response.sendRedirect("../index.jsp"); return;
		 * }
		 * 
		 */
		//String mensaje = "";

		try {

			if ( ( c = ( Caso ) session.getAttribute( GestionInterface.ATT_CASE ) ) == null )
				c = getCaso( session );

			if ( c == null ) {
				log.error( "Error en Aplicacion presupuestal:" );
				return;
			}

			if ( c.getIdGabinete() == -1 )
				c.setIdGabinete( cbl.creaExpediente( usuario.getLogin(), c ) );

			List fileItems = parseRequest( request );
			Iterator i = fileItems.iterator();

			String docName = null;

			while ( i.hasNext() ) {
				FileItem item = ( FileItem ) i.next();

				if ( item.isFormField() )
					if ( "nombre".equals( item.getFieldName() ) ) {
						docName = item.getString();
						break;
					}

			}

			i = fileItems.iterator();

			while ( i.hasNext() ) {

				FileItem item = ( FileItem ) i.next();

				if ( item.isFormField() )
					continue;

				String tmpFile = ( new File( item.getName() ) ).getName();

				int pos = tmpFile.indexOf( "." ) != -1 ? tmpFile.lastIndexOf( '.' ) + 1 : -1;
				int pos1 = 0;
				String ext = pos != -1 ? tmpFile.substring( pos ) : "";

				// Fortimax fimx = new Fortimax(select);

				pos = tmpFile.lastIndexOf( '.' );
				pos1 = tmpFile.lastIndexOf( '\\' ) + 1;
				String filename = tmpFile.substring( pos1, pos );

				cbl.recibeDocumentoGestionRequisicion( c, "Vigencias", ext, new DataInputStream( item.getInputStream() ) );

				item.delete();
			}

		} catch ( Exception exc ) {
			try {
				jsonObj.put( "Devuelve", "false" );
				String destino = arrayObj.put( jsonObj ).toString();
				out.println( destino );
				conn.rollback();
			} catch ( Exception e ) {
				e.printStackTrace();
			}

		} finally {
		}

	}

	private Caso getCaso( HttpSession session ) {
		// Debido a que el Modulo de adquisiciones y servicios no se encuentra
		// en un flujo pero se necesita crear un caso para el precompromiso
		// se creo este método que obtiene el ID de caso desde base de datos y
		// no de sesión
		String sql, tipo, ue, ejercicio, sql1;
		int consecutivo;
		int tieneCaso = 0;
		Connection conn = null;
		PreparedStatement pstmt = null, pstmt1 = null;
		ResultSet rs = null, rs1 = null;

		ejercicio = ( String ) session.getAttribute( GestionInterface.ATT_ReqEjercicio );
		tipo = ( String ) session.getAttribute( GestionInterface.ATT_ReqTipoSolicitud );
		ue = ( String ) session.getAttribute( GestionInterface.ATT_ReqUnidadEjec );
		consecutivo = Integer.parseInt( ( String ) session.getAttribute( GestionInterface.ATT_ReqConsecutivo ) );

		// A partir de los atributos gurdados en sesión se hace un QRY para
		// obtener el ID_CASO

		sql = "SELECT MAX(c.ID_CASO) ID_CASO,MAX(c.ID_TC) ID_TC, MAX(o.ID_CASO_OPER) ID_CASO_OPER " + " FROM mSolicitud s with(Nolock), CG_CASO c with(Nolock), CG_CASO_OPERACION o with(Nolock) " + " where s.C_FOLIO_APA=c.C_FOLIO" + " and c.ID_CASO=o.ID_CASO" + " and s.cEjercicio=?" + " and s.cIdTipoSolicitud = ? " + " and s.cIdUnidadEjecutora = ?" + " and s.nIdConsecutivo = ?";

		sql1 = "SELECT count(c.ID_CASO) as ID_CASO " + " FROM mSolicitud s with(Nolock), CG_CASO c with(Nolock), CG_CASO_OPERACION o with(Nolock) " + " where s.C_FOLIO_APA=c.C_FOLIO" + " and c.ID_CASO=o.ID_CASO" + " and s.cEjercicio=?" + " and s.cIdTipoSolicitud = ? " + " and s.cIdUnidadEjecutora = ?" + " and s.nIdConsecutivo = ?" + " and s.nIdEstado<>5";

		Caso c = null;

		if ( ejercicio.length() > 0 && tipo.length() > 0 && ue.length() > 0 && consecutivo > 0 ) {

			try {

				conn = DataSourceManager.getConnection( jndiName );
				pstmt1 = conn.prepareStatement( sql1 );
				pstmt1.setString( 1, ejercicio );
				pstmt1.setString( 2, tipo );
				pstmt1.setString( 3, ue );
				pstmt1.setInt( 4, consecutivo );
				rs1 = pstmt1.executeQuery();
				if ( rs1.next() ) {
					int idCaso = rs1.getInt( "ID_CASO" );
					if ( idCaso == 0 ) {
						tieneCaso = 0;
					} else
						tieneCaso = 1;
				}

				if ( tieneCaso == 1 ) {
					// conn = DataSourceManager.getConnection(jndiName);
					pstmt = conn.prepareStatement( sql );
					pstmt.setString( 1, ejercicio );
					pstmt.setString( 2, tipo );
					pstmt.setString( 3, ue );
					pstmt.setInt( 4, consecutivo );

					rs = pstmt.executeQuery();
					if ( rs.next() ) {
						String idCaso = rs.getString( "ID_CASO" );
						log.debug( idCaso );
						if ( idCaso == null ) {
							log.error( "Llamada invalida, sin identificador de caso" );
							throw new GestionException( "Llamada inválida, sin identificador de caso" );
						}
						int id_caso = Integer.parseInt( idCaso );
						if ( id_caso <= 0 ) {
							log.error( "Llamada invalida, identificador de caso menor o igual a cero (<= 0)" );
							throw new GestionException( "Llamada inválida, identificador de caso menor o igual a cero (<= 0)" );
						}
						// Una vez que se ha obtenido el ID_CASO se utiliza el
						// CasoManager para obtner el objeto tipo caso
						Caso sc = new Caso();
						sc.setIdCaso( id_caso );
						c = CasoManager.select( conn, sc );
					}

				}

			} catch ( Exception e ) {
				e.printStackTrace();
				log.error( "Error en Aplicacion presupuestal:" + e.getMessage() );
			} finally {
				try {
					if ( conn != null )
						conn.close();
					if ( pstmt != null )
						pstmt.close();
					if ( pstmt1 != null )
						pstmt1.close();
					if ( rs != null )
						rs.close();
					if ( rs1 != null )
						rs1.close();

				} catch ( SQLException exc ) {
					log.warn( "Cerrando conexion a base de datos", exc );
				}
				conn = null;
				pstmt = null;
				pstmt1 = null;
				rs = null;
				rs1 = null;
			}
		}
		return c;
	}

	public boolean creaEncabDetApartado( Connection conn, DatosRequisicion datosRequi, Usuario usuario ) throws Exception {
		boolean resp = false;
		PreparedStatement pstm = null;
		CallableStatement cmst = null;
		try {
			log.info( "Creando encabezado y detalle del apartado." + new Timestamp( System.currentTimeMillis() ) );
			// Crea encabezado
			pstm = conn.prepareStatement( " INSERT INTO tApartadoEncabezado (nFolioApartado, fCarga, fAplicacion, cCentroContable, cRamo,cUnidadResponsable, caNoPreCompromiso," + " cTipoPoliza, nMes, aEjercicioFiscal, nStatusFinanciero, fVigencia, nEnviadoSICOP, cIdSolicitud) " + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)" );
			pstm.setInt( 1, datosRequi.getnFolioApartado() );
			pstm.setString( 2, datosRequi.getfCarga() );
			pstm.setString( 3, datosRequi.getfAplicacion() );
			pstm.setString( 4, usuario.getPropiedad( "CCENTROCONTABLE" ).getValor() );
			pstm.setString( 5, usuario.getU_Ramo() );
			pstm.setString( 6, usuario.getU_UR() );
			pstm.setString( 7, datosRequi.getCaNoPreCompromiso() );
			pstm.setString( 8, datosRequi.getcTipoPoliza() );
			pstm.setInt( 9, datosRequi.getnMes() );
			pstm.setString( 10, datosRequi.getcEjercicio() );
			pstm.setInt( 11, datosRequi.getnStatusFinanciero() );
			pstm.setString( 12, datosRequi.getfVigencia() );
			pstm.setInt( 13, datosRequi.getnEnviadoSICOP() );
			pstm.setString( 14, datosRequi.getcIdSolicitud() );
			pstm.executeUpdate();
			// Crea detalle
			cmst = conn.prepareCall( "{call sp_insertApartadoDetalle (?,?,?,?,?)}" );
			cmst.setInt( 1, datosRequi.getnFolioApartado() );
			cmst.setString( 2, usuario.getPropiedad( "CCENTROCONTABLE" ).getValor() );
			cmst.setString( 3, datosRequi.getcEjercicio() );
			cmst.setString( 4, usuario.getU_UR() );
			cmst.setString( 5, datosRequi.getcIdSolicitud() );
			cmst.execute();
			log.info( "Termina de crear el encabezado y detalle del apartado." + new Timestamp( System.currentTimeMillis() ) );
			resp = true;
		} finally {
			if ( pstm != null ) {
				pstm.close();
			}
			if ( cmst != null ) {
				cmst.close();
			}
			pstm = null;
			cmst = null;
		}
		return resp;
	}

	public DatosRequisicion llenaDatosRequi( HttpServletRequest request ) throws Exception {
		DatosRequisicion datosRequi = new DatosRequisicion();

		datosRequi.setCaNoPreCompromiso( request.getParameter( "caNoPreCompromiso" ) == null ? "" : request.getParameter( "caNoPreCompromiso" ) );
		datosRequi.setcEjercicio( request.getParameter( "cEjercicio" ) == null ? "" : request.getParameter( "cEjercicio" ) );
		datosRequi.setcIdSolicitud( request.getParameter( "cIdSolicitud" ) == null ? "" : request.getParameter( "cIdSolicitud" ) );
		datosRequi.setCidTipoSolicitud( request.getParameter( "cIdTipoSolicitud" ) == null ? "" : request.getParameter( "cIdTipoSolicitud" ) );
		datosRequi.setcTipoPoliza( request.getParameter( "cTipoPoliza" ) == null ? "" : request.getParameter( "cTipoPoliza" ) );
		datosRequi.setfAplicacion( request.getParameter( "fAplicacion" ) == null ? "" : request.getParameter( "fAplicacion" ) );
		datosRequi.setfCarga( request.getParameter( "fCarga" ) == null ? "" : request.getParameter( "fCarga" ) );
		datosRequi.setfVigencia( request.getParameter( "fVigencia" ) == null ? "" : request.getParameter( "fVigencia" ) );
		datosRequi.setnEnviadoSICOP( request.getParameter( "nEnviadoSICOP" ) == null ? -1 : Integer.parseInt( request.getParameter( "nEnviadoSICOP" ) ) );
		datosRequi.setnFolioApartado( request.getParameter( "nFolioApartado" ) == null ? -1 : Integer.parseInt( request.getParameter( "nFolioApartado" ) ) );
		datosRequi.setnMes( request.getParameter( "nMes" ) == null ? -1 : Integer.parseInt( request.getParameter( "nMes" ) ) );
		datosRequi.setnStatusFinanciero( request.getParameter( "nStatusFinanciero" ) == null ? -1 : Integer.parseInt( request.getParameter( "nStatusFinanciero" ) ) );

		return datosRequi;
	}

	private synchronized void AutorizaApartado( HttpServletRequest request, HttpServletResponse response, HttpSession session, String[] responsable, String[] nombre ) throws Exception {
		session = request.getSession( false );
		out = response.getWriter();
		ArrayList<String> arrLResult = new ArrayList<String>();
		PreparedStatement pstm = null;
		CallableStatement cmst = null, cmst1 = null;
		Connection conn = null;
		DatosRequisicion datosRequi = null;
		String prefixPath = getServletContext().getRealPath( "/WEB-INF/mail-bodies/" ) + File.separator;
		String mensaje = "";
		boolean resp = false;
		try {
			CompromisoBussinessLogic cbl = new CompromisoBussinessLogic( GestionInterface.ATT_CONEXION );
			conn = cbl.getConnection();
			// llena objeto
			datosRequi = llenaDatosRequi( request );
			// Crea encabezado y detalle
			resp = creaEncabDetApartado( conn, datosRequi, usuario );
			// Obtener el caso
			Caso c;
			if ( ( c = ( Caso ) session.getAttribute( GestionInterface.ATT_CASE ) ) == null ) {
				c = getCaso( session );
			}
			if ( c == null ) {
				log.error( "Error en Aplicacion presupuestal: No se encontró el caso en sesion" );
				return;
			}
			String ue = c.getFolio().substring( c.getFolio().lastIndexOf( "-" ) - 3, c.getFolio().lastIndexOf( "-" ) );
			// Aplicación contable
			ContableInterface conInt = new AplicacionContable();
			AplicarContableReturn acr = null;
			log.debug( "Inicia aplicacion presupuestal " + new Timestamp( System.currentTimeMillis() ) );
			Map m = CasoDatoManager.readValuesCasoDato( request, c.getCasoDato(), true );
			acr = conInt.aplicarContableNuevo( conn, c, "", "", "", 0, "", m, prefixPath, usuario.getLogin(), "" );
			arrLResult = ( ArrayList<String> ) acr.getMessageList();
			log.debug( "Termina aplicacion presupuestal " + new Timestamp( System.currentTimeMillis() ) );
			// Se valida aplicación contable
			if ( acr.isSuccess() ) {
				// Confirma moviemientos contaboles correctos
				cmst = conn.prepareCall( "{?= call pa_validaAplicacionContable (?,?,?,?,?)}" );
				cmst.registerOutParameter( 1, Types.INTEGER );
				cmst.setInt( 2, datosRequi.getnFolioApartado() );
				cmst.setString( 3, datosRequi.getcEjercicio() );
				cmst.setString( 4, "S" );
				cmst.setString( 5, datosRequi.getcTipoPoliza() );
				cmst.setString( 6, "APARTADO" );
				cmst.execute();
				int outputValue = cmst.getInt( 1 );
				if ( outputValue == 0 ) {
					// actualiza status de la requisicion
					pstm = conn.prepareStatement( "UPDATE msolicitud SET nIdEstado = 3 , nIdEstadoPrecomprometido=3 " + " WHERE cIdSolicitud = ? " + " and cEjercicio=?" );
					pstm.setString( 1, request.getParameter( "cIdSolicitud" ) );
					pstm.setString( 2, request.getParameter( "cEjercicio" ) );
					pstm.executeUpdate();

					// actualiza vigencia de las requisiciones
					cmst1 = conn.prepareCall( "{ call sp_mEstadoVigenciaApartado (?,?,?,?)}" );
					cmst1.setString( 1, request.getParameter( "cEjercicio" ).toString() );
					cmst1.setString( 2, ue );
					cmst1.setString( 3, request.getParameter( "cIdSolicitud" ) );
					cmst1.setString( 4, c.getFolio() );
					cmst1.execute();
					// Avanza el cazo
					Caso sc = new Caso();
					sc.setIdCaso( c.getIdCaso() );
					c = CasoManager.select( conn, sc );
					avanzaCaso( request, c, usuario, prefixPath, responsable, nombre );
					// Bitacora
					Util.bitacoraMovimientos( datosRequi.getcIdSolicitud(), "Apartado Aplicado", usuario.getLogin(), conn );
					log.debug( c.getCasoDato( "APLICADO_CONT" ).getValor() );
					jsonObj.put( "Success", "true" );
					mensaje = "DOCUMENTO DE APARTADO APLICADO PRESUPUESTALMENTE.";

					log.debug( "Termina la autorización del apartado. " + new Timestamp( System.currentTimeMillis() ) );
					resp = true;
				} else {
					resp = false;
					jsonObj.put( "Success", "false" );
					switch ( outputValue ) {
						case 1:
							mensaje = "EL DOCUMENTO NO SE APLICO PRESUPUESTALMENTE (NO SE ENCONTRO EL REGISTRO DEL APARTADO).";
						break;
						case 2:
							mensaje = "EL DOCUMENTO NO SE APLICO PRESUPUESTALMENTE (NO EXISTE NINGUN REGISTRO DE POLIZA DEL DOCUMENTO).";
						break;
						case 3:
							mensaje = "EL DOCUMENTO NO SE APLICO PRESUPUESTALMENTE (LOS MOVIMIENTOS DE LA AFECTACION PRESUPUESTAL NO ESTAN COMPLETOS).";
						break;
						default:
							mensaje = "ERROR INESPERADO EN LA CONFIRMACION DE LA APLICACION PRESUPUESTAL.";
					}
					throw new Exception( mensaje );
				}
			} else {
				mensaje=arrLResult.get( 0 );
				throw new Exception( arrLResult.get( 0 ) );
			}
			conn.commit();
		} catch ( Exception e ) {
			jsonObj.put( "Success", "false" );
			if ( conn != null ) {
				conn.rollback();
			}
			mensaje = e.getMessage().toString();
		} finally {
			if ( cmst != null ) {
				cmst.close();
			}
			if ( cmst1 != null ) {
				cmst1.close();
			}
			if ( conn != null ) {
				conn.close();
			}
			cmst = null;
			cmst1 = null;
			conn = null;
			jsonObj.put( "Contable1", mensaje );
			String destino = arrayObj.put( jsonObj ).toString();
			out.println( destino );
		}
	}

	private synchronized void aplicaContablemente( String strParam, HttpServletRequest request, HttpServletResponse response, HttpSession session, String[] responsable, String[] nombre ) throws ServletException, IOException {
		// Este método es una copia del método de financiero
		// Se hizo una copia para poder recibir el error
		session = request.getSession( false );
		out = response.getWriter();
		ArrayList<String> arrLResult = new ArrayList<String>();
		PreparedStatement pstm = null, pstm1 = null, pstm2 = null, pstm3 = null;
		CallableStatement cmst = null, cmst1 = null;
		Connection conn = null, conn1 = null;

		Caso c;
		if ( ( c = ( Caso ) session.getAttribute( GestionInterface.ATT_CASE ) ) == null )
			c = getCaso( session );
		if ( c == null ) {
			log.error( "Error en Aplicacion presupuestal: No se encontró el caso en sesion" );
			return;
		}

		// Valida Centro de Costos
		// Validaciones de financiero
		String cCentroContable = "";
		String mensaje = "";

		if ( usuario.getPropiedades() != null && usuario.getPropiedades().containsKey( "CCENTROCONTABLE" ) ) {
			cCentroContable = usuario.getPropiedad( "CCENTROCONTABLE" ).getValor();
		}

		if ( cCentroContable.isEmpty() || cCentroContable.equals( "" ) ) {
			mensaje = "Error: El Usuario no tiene Centro Contable asignado y no podra realizar aplicacion presupuestal, Consulte a su administrador.";
		}

		// Inicia aplicacion contable
		ContableInterface conInt = new AplicacionContable();
		log.debug( "Inicia aplicacion presupuestal " + new Timestamp( System.currentTimeMillis() ) );

		CompromisoBussinessLogic cbl = new CompromisoBussinessLogic( GestionInterface.ATT_CONEXION );
		AplicarContableReturn acr = null;
		String prefixPath = getServletContext().getRealPath( "/WEB-INF/mail-bodies/" ) + File.separator;

		try {
			conn = cbl.getConnection();
			conn1 = cbl.getConnection();

			String ue = c.getFolio().substring( c.getFolio().lastIndexOf( "-" ) - 3, c.getFolio().lastIndexOf( "-" ) );
			// int
			// folioApartado=Integer.parseInt(c.getFolio().substring(c.getFolio().lastIndexOf('-')+1));

			Map m = CasoDatoManager.readValuesCasoDato( request, c.getCasoDato(), true );
			acr = conInt.aplicarContableNuevo( conn, c, "", "", "", 0, "", m, prefixPath, usuario.getLogin(), "" );
			arrLResult = ( ArrayList ) acr.getMessageList();
			// Termina aplicación contable

			// Si hubo exito, se hace commit y avanza el caso
			if ( acr.isSuccess() ) {
				// Confirma moviemientos contaboles correctos
				cmst = conn.prepareCall( "{?= call pa_validaAplicacionContable (?,?,?,?,?)}" );
				cmst.registerOutParameter( 1, Types.INTEGER );
				cmst.setInt( 2, Integer.parseInt( request.getParameter( "nFolioApartado" ).toString() ) );
				cmst.setString( 3, request.getParameter( "cEjercicio" ).toString() );
				cmst.setString( 4, "S" );
				cmst.setString( 5, "PR" );
				cmst.setString( 6, "APARTADO" );
				cmst.execute();
				int outputValue = cmst.getInt( 1 );

				if ( outputValue == 0 ) {

					// actualiza status de la requisicion
					pstm = conn.prepareStatement( "UPDATE msolicitud SET nIdEstado = 3 , nIdEstadoPrecomprometido=3 " + " WHERE cIdSolicitud = ? " + " and cEjercicio=?" );
					pstm.setString( 1, request.getParameter( "cIdSolicitud" ) );
					pstm.setString( 2, request.getParameter( "cEjercicio" ) );
					pstm.executeUpdate();

					// actualiza vigencia de las requisiciones
					cmst1 = conn.prepareCall( "{ call sp_mEstadoVigenciaApartado (?,?,?,?)}" );
					cmst1.setString( 1, request.getParameter( "cEjercicio" ).toString() );
					cmst1.setString( 2, ue );
					cmst1.setString( 3, request.getParameter( "cIdSolicitud" ) );
					cmst1.setString( 4, c.getFolio() );
					cmst1.execute();
					// Una vez que ha hecho la aplicación contable avanza el
					// caso
					// Recargando el caso por los cambios de la aplicacion
					// presupuestal (exitosa)
					Caso sc = new Caso();
					sc.setIdCaso( c.getIdCaso() );
					c = CasoManager.select( conn, sc );
					avanzaCaso( request, c, usuario, prefixPath, responsable, nombre );
					log.debug( c.getCasoDato( "APLICADO_CONT" ).getValor() );
					log.debug( "Termina Aplicacion presupuestal " + new Timestamp( System.currentTimeMillis() ) );
					jsonObj.put( "Success", "true" );
					mensaje = "DOCUMENTO DE APARTADO APLICADO PRESUPUESTALMENTE.";
					jsonObj.put( "Success", "true" );
					log.debug( "Termina Aplicacion presupuestal" + new Timestamp( System.currentTimeMillis() ) );
					conn.commit();
				} else {
					conn.rollback();
					jsonObj.put( "Success", "false" );
					switch ( outputValue ) {
						case 1:
							mensaje = "EL DOCUMENTO NO SE APLICO PRESUPUESTALMENTE (NO SE ENCONTRO EL REGISTRO DEL APARTADO).";
						break;
						case 2:
							mensaje = "EL DOCUMENTO NO SE APLICO PRESUPUESTALMENTE (NO EXISTE NINGUN REGISTRO DE POLIZA DEL DOCUMENTO).";
						break;
						case 3:
							mensaje = "EL DOCUMENTO NO SE APLICO PRESUPUESTALMENTE (LOS MOVIMIENTOS DE LA AFECTACION PRESUPUESTAL NO ESTAN COMPLETOS).";
						break;
						default:
							mensaje = "ERROR INESPERADO EN LA CONFIRMACION DE LA APLICACION PRESUPUESTAL.";
					}

					pstm1 = conn1.prepareStatement( "UPDATE msolicitud SET nIdEstado = 2 , nIdEstadoPrecomprometido=2  " + " WHERE cIdSolicitud = ? " + " and cEjercicio=?" );
					pstm1.setString( 1, request.getParameter( "cIdSolicitud" ) );
					pstm1.setString( 2, request.getParameter( "cEjercicio" ) );
					pstm1.executeUpdate();

					// BORRAMOS ENCABEZADO Y DETALLE

					pstm2 = conn1.prepareStatement( "DELETE FROM TAPARTADODETALLE WHERE NFOLIOAPARTADO=?" );
					pstm2.setInt( 1, Integer.parseInt( request.getParameter( "nFolioApartado" ).toString() ) );
					pstm2.executeUpdate();

					pstm3 = conn1.prepareStatement( "DELETE FROM TAPARTADOENCABEZADO WHERE NFOLIOAPARTADO=?" );
					pstm3.setInt( 1, Integer.parseInt( request.getParameter( "nFolioApartado" ).toString() ) );
					pstm3.executeUpdate();
					conn1.commit();

				}
			} else {
				conn.rollback();
				jsonObj.put( "Success", "false" );
				pstm1 = conn1.prepareStatement( "UPDATE msolicitud SET nIdEstado = 2 , nIdEstadoPrecomprometido=2  " + " WHERE cIdSolicitud = ? " + " and cEjercicio=?" );
				pstm1.setString( 1, request.getParameter( "cIdSolicitud" ) );
				pstm1.setString( 2, request.getParameter( "cEjercicio" ) );
				pstm1.executeUpdate();

				// BORRAMOS ENCABEZADO Y DETALLE

				pstm2 = conn1.prepareStatement( "DELETE FROM TAPARTADODETALLE WHERE NFOLIOAPARTADO=?" );
				pstm2.setInt( 1, Integer.parseInt( request.getParameter( "nFolioApartado" ).toString() ) );
				pstm2.executeUpdate();

				pstm3 = conn1.prepareStatement( "DELETE FROM TAPARTADOENCABEZADO WHERE NFOLIOAPARTADO=?" );
				pstm3.setInt( 1, Integer.parseInt( request.getParameter( "nFolioApartado" ).toString() ) );
				pstm3.executeUpdate();
				conn1.commit();
				mensaje = !"".equals( mensaje ) ? mensaje : arrLResult.get( 0 );
			}

		} catch ( Exception e ) {
			try {
				conn.rollback();
				conn1.rollback();
			} catch ( SQLException e1 ) {
				e1.printStackTrace();
			}

			try {
				jsonObj.put( "Error", "true" );
			} catch ( JSONException e1 ) {
				e1.printStackTrace();
			}

			log.error( "Error en Aplicacion presupuestal:" + e.getMessage() );
			mensaje = e.getMessage();

		} finally {
			try {
				if ( conn != null )
					conn.close();
				if ( conn1 != null )
					conn1.close();
				if ( cmst != null )
					cmst.close();
				if ( cmst1 != null )
					cmst1.close();
				if ( pstm1 != null )
					pstm1.close();
				if ( pstm != null )
					pstm.close();
				if ( pstm2 != null )
					pstm2.close();
				if ( pstm3 != null )
					pstm3.close();

			} catch ( SQLException exc ) {
				log.warn( "Cerrando conexion a base de datos", exc );
			}

			try {
				jsonObj.put( "Contable1", mensaje );
				String destino = arrayObj.put( jsonObj ).toString();
				out.println( destino );
			} catch ( JSONException e1 ) {
				e1.printStackTrace();
			}
		}
		conn = null;
		cmst = null;
		conn1 = null;
		cmst1 = null;
		pstm = null;
		pstm1 = null;
		pstm3 = null;
		pstm2 = null;

	}

	private synchronized void devuelveContablemente( String strParam, HttpServletRequest request, HttpServletResponse response, HttpSession session, String[] responsable, String[] nombre ) throws ServletException, IOException {
		// la diferencia con el metodo de 'aplicaContablemente' es que hace una
		// llamada de cancelación al motor contable que recibe diferentes
		// parámetros
		session = request.getSession( false );
		out = response.getWriter();
		CallableStatement cmst = null, cmst1 = null;
		Connection conn = null;

		ArrayList<String> arrLResult = new ArrayList<String>();
		Usuario usuario = ( Usuario ) session.getAttribute( GestionInterface.ATT_USER );
		if ( usuario == null ) {
			response.sendRedirect( "../index.jsp" );
			return;
		}

		// Valida Centro de Costos
		String cCentroContable = "";
		String mensaje = "";
		if ( usuario.getPropiedades() != null && usuario.getPropiedades().containsKey( "CCENTROCONTABLE" ) ) {
			cCentroContable = usuario.getPropiedad( "CCENTROCONTABLE" ).getValor();
		}
		if ( cCentroContable.isEmpty() || cCentroContable.equals( "" ) ) {
			mensaje = "Error: El Usuario no tiene Centro Contable asignado y no podra realizar aplicacion presupuestal, Consulte a su administrador.";
		}

		ContableInterface conInt = new AplicacionContable();
		log.debug( "Inicia aplicacion presupuestal" + new Timestamp( System.currentTimeMillis() ) );
		CompromisoBussinessLogic cbl = new CompromisoBussinessLogic( GestionInterface.ATT_CONEXION );
		AplicarContableReturn acr = null;
		String prefixPath = getServletContext().getRealPath( "/WEB-INF/mail-bodies/" ) + File.separator;
		try {
			conn = cbl.getConnection();
			Caso c;
			if ( ( c = ( Caso ) session.getAttribute( GestionInterface.ATT_CASE ) ) == null )
				c = getCaso( session );
			if ( c == null ) {
				log.error( "Error en Aplicacion presupuestal:" );
				return;
			}
			Map m = CasoDatoManager.readValuesCasoDato( request, c.getCasoDato(), true );
			acr = conInt.cancelarAppContableNueva( conn, c, "", "", "", 0, "", m, prefixPath, usuario.getLogin(), "" );
			arrLResult = ( ArrayList ) acr.getMessageList();

			if ( acr.isSuccess() ) {
				cmst = conn.prepareCall( "{?= call pa_validaAplicacionContable (?,?,?,?,?)}" );
				cmst.registerOutParameter( 1, Types.INTEGER );
				cmst.setInt( 2, Integer.parseInt( request.getParameter( "nFolioApartado" ).toString() ) );
				cmst.setString( 3, request.getParameter( "cEjercicio" ).toString() );
				cmst.setString( 4, "C" );
				cmst.setString( 5, "PR" );
				cmst.setString( 6, "APARTADO" );
				cmst.execute();
				int outputValue = cmst.getInt( 1 );
				log.info( outputValue + "= call pa_validaAplicacionContable (" + Integer.parseInt( request.getParameter( "nFolioApartado" ).toString() ) + ",'" + request.getParameter( "cEjercicio" ).toString() + "','C','PR','APARTADO')" );
				log.info( "outputValue : " + outputValue );
				if ( outputValue == 0 ) {

					// actualiza vigencia de las requisiciones
					cmst1 = conn.prepareCall( "{ call sp_deleteEyDApartado (?)}" );
					cmst1.setString( 1, request.getParameter( "cIdSolicitud" ) );
					cmst1.execute();
					log.info( "call sp_deleteEyDApartado ('" + request.getParameter( "cIdSolicitud" ) + "')" );
					// Recargando el caso
					Caso sc = new Caso();
					sc.setIdCaso( c.getIdCaso() );
					c = CasoManager.select( conn, sc );
					avanzaCaso( request, c, usuario, prefixPath, responsable, nombre );
					log.debug( c.getCasoDato( "APLICADO_CONT" ).getValor() );
					log.debug( "Termina Aplicacion presupuestal " + new Timestamp( System.currentTimeMillis() ) );
					mensaje = "CANCELACION DE APARTADO APLICADA PRESUPUESTALMENTE";
					jsonObj.put( "Success", "true" );
					conn.commit();
				} else {
					conn.rollback();
					jsonObj.put( "Success", "false" );

					switch ( outputValue ) {
						case 1:
							mensaje = "EL DOCUMENTO NO SE APLICO PRESUPUESTALMENTE (NO SE ENCONTRO EL REGISTRO DEL PRECOMPROMISO).";
						break;
						case 2:
							mensaje = "EL DOCUMENTO NO SE APLICO PRESUPUESTALMENTE (NO EXISTE NINGUN REGISTRO DE POLIZA DEL DOCUMENTO).";
						break;
						case 3:
							mensaje = "EL DOCUMENTO NO SE APLICO PRESUPUESTALMENTE (LOS MOVIMIENTOS DE LA AFECTACION PRESUPUESTAL NO ESTAN COMPLETOS).";
						break;
						default:
							mensaje = "ERROR INESPERADO EN LA CONFIRMACION DE LA APLICACION PRESUPUESTAL.";
					}
				}
			} else {
				conn.rollback();
				jsonObj.put( "Success", "false" );
				mensaje = !"".equals( mensaje ) ? mensaje : arrLResult.get( 0 );
			}

		} catch ( Exception e ) {
			try {
				conn.rollback();
			} catch ( SQLException e1 ) {
				e1.printStackTrace();
			}
			log.error( "Error en Aplicacion presupuestal:" + e.getMessage() );
			mensaje = e.getMessage();
		} finally {
			try {
				if ( conn != null )
					conn.close();
				if ( cmst != null )
					cmst.close();
				if ( cmst1 != null )
					cmst1.close();
			} catch ( SQLException exc ) {
				log.warn( "Cerrando conexion a base de datos", exc );
			}

			try {
				jsonObj.put( "Contable1", mensaje );
				String destino = arrayObj.put( jsonObj ).toString();
				out.println( destino );
			} catch ( JSONException e1 ) {
				e1.printStackTrace();
			}
		}
		conn = null;
		cmst = null;
		cmst1 = null;
	}

	private void avanzaCaso( HttpServletRequest request, HttpServletResponse response, HttpSession session, String[] responsable, String[] nombre ) throws ServletException, IOException {
		// al momento de crear el compromiso, se necesita avanzar el caso de
		// precompromiso para que ya no aparezca en el Inbox
		Caso c = null;
		usuario = ( Usuario ) session.getAttribute( ATT_USER );

		log.debug( "Buscando caso en ATT_CASE" );
		c = ( Caso ) session.getAttribute( GestionInterface.ATT_CASE );

		if ( c == null ) {
			log.debug( "fallo. Buscando caso en BD" );
			c = getCaso( session );
		}

		try {
			String prefixPath = getServletContext().getRealPath( "/WEB-INF/mail-bodies/" ) + File.separator;
			// avanzaCaso(request, c, usuario, prefixPath, new String[] {
			// "CONSULTA_PRECOMPROMISO" }, new String[] { "consulta_precomp" });
			avanzaCaso( request, c, usuario, prefixPath, responsable, nombre );
			try {
				out = response.getWriter();
				jsonObj.put( "Success", "true" );
				String destino = arrayObj.put( jsonObj ).toString();
				out.println( destino );
				out.flush();
			} catch ( JSONException e1 ) {
				e1.printStackTrace();
			}
		} catch ( Exception e ) {
			log.error( "Error en Aplicacion presupuestal:" + e.getMessage() );
		}finally {
			if(out !=null) {
				out.close();
			}
			out=null;
		}
	}

	private void avanzaCaso( HttpServletRequest req, Caso c, Usuario u, String prefixPath, String[] responsable, String[] nombre ) throws GestionException, ServletException, IOException {
		// Método para avanzar el caso. Se utliza tanto para pre-compromiso como
		// para compromiso
		if ( c == null ) {
			log.error( "Llamada invalida, sin Caso seleccionado" );
			throw new GestionException( "Llamada inválida, sin Caso seleccionado" );
		}
		if ( c.getIdCaso() <= 0 ) {
			log.error( "Llamada invalida, identificador de caso menor o igual a cero (<= 0)" );
			throw new GestionException( "Llamada inválida, identificador de caso menor o igual a cero (<= 0)" );
		}
		if ( c.getCasoOperacion( 0 ).getIdOperacion() <= 0 ) {
			log.error( "Llamada invalida, sin identificador de caso operacion menor o igual a cero (<= 0)" );
			throw new GestionException( "Llamada inválida, sin identificador de caso operación menor o igual a cero (<= 0)" );
		}
		Map m = CasoDatoManager.readValuesCasoDato( req, c.getCasoDato(), true );
		CasoBusinessLogic cbl = new CasoBusinessLogic( jndiName );
		cbl.avanzaCaso( c, u.getLogin(), "", responsable, nombre, m, prefixPath );
	}

	 
	private synchronized void generaGuardaCaso_RT( HttpServletRequest request, HttpServletResponse response, HttpSession session, String tipoCaso, String CONCEPTO_MOV, String[] responsable, String[] nombre ) throws ServletException, IOException {
		String destino = "";
		SolicitudBusinessLogic business = null;
		DatosRequisicion datosRequi = null;
		try {
			out = response.getWriter();
			business = new SolicitudBusinessLogic();

			// llena objeto
			datosRequi = llenaDatosRequi( request );
			datosRequi.setJndiName( jndiName );
			datosRequi.setTipoCaso( tipoCaso );
			datosRequi.setFolioGenerator( folioGenerator );
			datosRequi.setCONCEPTO_MOV( CONCEPTO_MOV );
			datosRequi.setPrefixPath( getServletContext().getRealPath( "/WEB-INF/mail-bodies/" ) + File.separator );
			datosRequi.setResponsable( responsable );
			datosRequi.setNombre( nombre );
			jsonObj = business.apartaRT( request, datosRequi, session, usuario );

		} catch ( Exception e ) {
			log.error( e );
			try {
				jsonObj.put( "Folio1", -1 );
				jsonObj.put( "Folio2", "" );
				jsonObj.put( "msg", e.getMessage() );
			} catch ( JSONException e1 ) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		} finally {
			datosRequi = null;
			business = null;
			destino = new String( arrayObj.put( jsonObj ).toString().getBytes( "UTF-8" ), "ISO-8859-1" );
			// destino = arrayObj.put(jsonObj).toString();
			out.println( destino );

		}
	}
	private synchronized void devuelveApartado( HttpServletRequest request, HttpServletResponse response, HttpSession session, String tipoCaso, String CONCEPTO_MOV, String[] responsable, String[] nombre ) throws ServletException, IOException {
		String destino = "";
		SolicitudBusinessLogic business = null;
		DatosRequisicion datosRequi = null;
		try {
			out = response.getWriter();
			business = new SolicitudBusinessLogic();

			// llena objeto
			datosRequi = llenaDatosRequi( request );
			datosRequi.setJndiName( jndiName );
			datosRequi.setTipoCaso( tipoCaso );
			datosRequi.setFolioGenerator( folioGenerator );
			datosRequi.setCONCEPTO_MOV( CONCEPTO_MOV );
			datosRequi.setPrefixPath( getServletContext().getRealPath( "/WEB-INF/mail-bodies/" ) + File.separator );
			datosRequi.setResponsable( responsable );
			datosRequi.setNombre( nombre );
			jsonObj.put( "msg", business.devuelveApartado( request, datosRequi, session, usuario ));
		} catch ( Exception e ) {
			log.error( e );
			try {
				jsonObj.put( "msg", (null==e.getMessage()?"Error "+e.getLocalizedMessage():e.getMessage() ) );
			} catch ( JSONException e1 ) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} finally {
			datosRequi = null;
			business = null;
			destino = new String( arrayObj.put( jsonObj ).toString().getBytes( "UTF-8" ), "ISO-8859-1" );
			// destino = arrayObj.put(jsonObj).toString();
			out.println( destino );
			out.flush();
			out.close();
		}
	}
	private synchronized void generaGuardaCaso( HttpServletRequest request, HttpServletResponse response, HttpSession session, String tipoCaso, String CONCEPTO_MOV, String[] responsable, String[] nombre ) throws ServletException, IOException, JSONException {
		// Metodo utilizado para generar los casos de Precompromiso y Compromiso
		out = response.getWriter();
		int folio;
		String folioCaso = null;
		String prefixPath = getServletContext().getRealPath( "/WEB-INF/mail-bodies/" ) + File.separator;
		Caso c = null;
		Connection conn = null;
		String destino="";
		PreparedStatement pstmnt = null;
		try {
			conn = DataSourceManager.getConnection( jndiName );
			CasoBusinessLogic cbl = new CasoBusinessLogic( jndiName );
			c = getCaso( session );
			if ( c == null ) {
				log.debug( "fallo. Creando caso" );
				c = iniciaCaso( request, tipoCaso );
				// casoOrigen = CASO_CREADO;
				if ( c == null ) {
					log.debug( "no se pudo crear" );
					log.error( "Error en Aplicacion presupuestal:" );
					return;
				}
			}
			folioCaso = c.getFolio();
			log.debug( "Caso obtenido: " + folioCaso );
			int indice = folioCaso.lastIndexOf( '-' ) + 1;
			folio = Integer.parseInt( folioCaso.substring( indice ) );
			// Datos que serán usados en el callback del ajax
			jsonObj.put( "Folio1", String.valueOf( folio ) );
			jsonObj.put( "Folio2", folioCaso );
			// Argumentos para llenar la tabla de CG_CASO_DATO y que se muestren
			// en el inbox
			Map<String, String> datos = new HashMap<String, String>();
			datos.put( "FOLIO", folioCaso );
			datos.put( "FECHA_DOCUMENTO", today );
			datos.put( "EJERCICIO_FISCAL", cEjercicio );
			datos.put( "OPERADOR", usuario.getNombre() );
			datos.put( "CONCEPTO_MOV", CONCEPTO_MOV );
			datos.put( "MONEDA", "MXP" );
			datos.put( "APLICADO_CONT", "false" );

			// Actualiza el caso en BD con Map<> datos
			CasoDatoManager.update( conn, c.getIdTC(), c.getIdCaso(), datos );

			// Caso sc solo tiene el id caso para hacer un select de toda su
			// info
			// y actualizar asi los valores de Caso c
			Caso sc = new Caso();

			sc.setIdCaso( c.getIdCaso() );
			c = CasoManager.select( conn, sc );
			avanzaCaso( request, c, usuario, prefixPath, responsable, nombre );
			session.setAttribute( GestionInterface.ATT_CASE, c );

			if ( c.getIdGabinete() == -1 ) {
				c.setIdGabinete( cbl.creaExpediente( usuario.getLogin(), c ) );
				cbl.recibeDocumentoGestion( c, new DataInputStream( request.getInputStream() ) );
			}
			//Actualiza estatus
			pstmnt = conn.prepareStatement( "UPDATE mSolicitud SET nIdEstado = 2, nIdEstadoPrecomprometido = 2,ConsecutivoAPARTADO = ?, C_FOLIO_APA=? WHERE cIdSolicitud = ? " );
			pstmnt.setInt( 1, folio );
			pstmnt.setString( 2, folioCaso );
			pstmnt.setString( 3, request.getParameter( "cIdSolicitud" ) );
			
			pstmnt.executeUpdate();
			// Imprime el jsonObj para pasarlo como respuesta al ajax
			destino = arrayObj.put( jsonObj ).toString();
			conn.commit();
			out.println( destino );
			out.flush();
		} catch ( Exception exc ) {
			log.error( exc );
			try {
				conn.rollback();
			} catch ( SQLException e ) {
				log.error( "Haciendo rollback : ",e );
			}
			jsonObj.put( "Folio1", "-1" );
			jsonObj.put( "MSG", null==exc.getMessage() || "".equalsIgnoreCase( exc.getMessage())?"Error": exc.getMessage().toString() );
			destino = arrayObj.put( jsonObj ).toString();
			out.println( destino );
			out.flush();
		} finally {
			try {
				if ( conn != null ) {
					conn.close();
				}
				if(out != null) {
					out.close();
				}
				conn = null;
				pstmnt = null;
				out =null;
			} catch ( SQLException exc ) {
				exc.printStackTrace();
				log.warn( "Cerrando conexion a base de datos", exc );
			}

			
		}

	}

	public void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {

		HttpSession session = request.getSession( false );

		if ( request.getParameter( "operacion" ) != null ) {
			adjuntarArchivoVigenciaRequisicion( request, response, session );
			// avanzaCaso(request, response,session, new String[] {
			// "VENTANILLA_APARTADO" }, new String[] { "VIGENCIA_APTD" });
			// response.sendRedirect(request.getContextPath()+
			// "/Generador/SAYCIS/Requisiciones.jsp?tab=6");
			return;
		}

		out = response.getWriter();
		String prefixPath = getServletContext().getRealPath( "/WEB-INF/mail-bodies/" ) + File.separator;

		PreparedStatement pstmnt = null;
		Connection conn = null;

		String ejercicio = ( String ) session.getAttribute( GestionInterface.ATT_ReqEjercicio );
		String tipo = ( String ) session.getAttribute( GestionInterface.ATT_ReqTipoSolicitud );
		String ue = ( String ) session.getAttribute( GestionInterface.ATT_ReqUnidadEjec );
		int consecutivo = Integer.parseInt( ( String ) session.getAttribute( GestionInterface.ATT_ReqConsecutivo ) );

		Caso c = null;

		int status = -1;

		try {
			conn = DataSourceManager.getConnection( jndiName );
			CasoBusinessLogic cbl = new CasoBusinessLogic( jndiName );
			c = ( Caso ) session.getAttribute( GestionInterface.ATT_CASE );
			if ( c == null ) {
				status = -1;
				log.debug( "no hay caso" );
				response.sendRedirect( "../Generador/SAICYS/Requisiciones.jsp?tab=6&status=" + status );
				return;
			}

			/*
			 * 
			 * c = getCaso(session); if (c == null) {
			 * log.debug("fallo. Buscando caso en BD"); c = getCaso(session); if
			 * (c == null) { log.debug("no hay caso"); return; } }
			 */

			// Se valida que el request contenga adjunto
			if ( FileUpload.isMultipartContent( request ) ) {
				String tmpFile = null;
				List<?> fileItems = parseRequest( request );
				Iterator<?> i = fileItems.iterator();
				boolean aplicaCuestionario = false;

				while ( i.hasNext() ) {

					FileItem item = ( FileItem ) i.next();

					if ( item.isFormField() ) {

						if ( "aplicaCuestionario".equals( item.getFieldName() ) )
							aplicaCuestionario = "S".equals( item.getString() );

						continue;

					}

					// Subir el archivo
					tmpFile = ( new File( item.getName() ) ).getName();
					int pos = tmpFile.lastIndexOf( '.' );
					String ext = ( pos != -1 ) ? tmpFile.substring( pos + 1 ) : "";

					if( !aplicaCuestionario ) {
						cbl.recibeDocumentoGestion( c, 2, "Comprobante", ext, new DataInputStream( item.getInputStream() ), true );
	
						cmst = conn.prepareCall( "{call pa_mVerificaExtension  (?,?,?,?)}" );
						cmst.setString( 1, "APARTADO" );
						cmst.setInt( 2, c.getIdGabinete() );
						cmst.setInt( 3, 2 );
						cmst.setString( 4, ext );
						cmst.execute();
					}

				}
			}

			// Avanza el caso
			Caso sc = new Caso();
			sc.setIdCaso( c.getIdCaso() );
			c = CasoManager.select( conn, sc );
			avanzaCaso( request, c, usuario, prefixPath, new String [] { "VENTANILLA_APARTADO" }, new String [] { "VENTANILLA_APTD" } );
			pstmnt = conn.prepareStatement( "UPDATE mSolicitud SET nIdEstado = 2, nIdEstadoPrecomprometido = 2  " + " WHERE cIdTipoSolicitud = ? " + " and cIdUnidadEjecutora = ?" + " and nIdConsecutivo = ?" + " and cEjercicio=?" );

			pstmnt.setString( 1, tipo );
			pstmnt.setString( 2, ue );
			pstmnt.setInt( 3, consecutivo );
			pstmnt.setString( 4, ejercicio );
			pstmnt.executeUpdate();
			status = 1;
			conn.commit();

		} catch ( Exception e ) {
			try {
				status = -1;
				conn.rollback();
			} catch ( SQLException e1 ) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		} finally {
			if ( conn != null )
				try {
					conn.close();
				} catch ( SQLException e ) {
					e.printStackTrace();
				}
			if ( pstmnt != null )
				try {
					pstmnt.close();
				} catch ( SQLException e ) {
					e.printStackTrace();
				}
			if ( cmst != null )
				try {
					cmst.close();
				} catch ( SQLException e ) {
					e.printStackTrace();
				}
			pstmnt = null;
			conn = null;
			cmst = null;
		}

		response.sendRedirect( "../Generador/SAICYS/Requisiciones.jsp?tab=6&status=" + status + "&folio=" + c.getFolio() );
	}

	private synchronized Caso iniciaCaso( HttpServletRequest req, String tCaso ) throws GestionException {

		// contrato diverso
		CasoBusinessLogic casoTx = new CasoBusinessLogic( jndiName );
		if ( tCaso == null ) {
			log.error( "Identificador de Tipo de Caso, vacio" );
			throw new GestionException( "Identificador de Tipo de Caso, vacio" );
		}
		int idTC = Integer.parseInt( tCaso );
		if ( idTC <= 0 ) {
			log.error( "Identificador de Tipo de Caso, menor o igual a cero (<= 0)" );
			throw new GestionException( "Identificador de Tipo de Caso, menor o igual a cero (<= 0)" );
		}
		Caso c = null;
		FolioGeneratorInterface fg = null;
		try {
			ClassLoader cl = getClass().getClassLoader();
			Class clase = cl.loadClass( folioGenerator );
			fg = ( FolioGeneratorInterface ) clase.newInstance();
		} catch ( ClassNotFoundException exc ) {
			log.error( "Generador de folios", exc );
			throw new GestionException( exc );
		} catch ( InstantiationException exc ) {
			log.error( "Generador de folios", exc );
			throw new GestionException( exc );
		} catch ( IllegalAccessException exc ) {
			log.error( "Generador de folios", exc );
			throw new GestionException( exc );
		} catch ( Exception exc ) {
			log.error( "algo raro paso", exc );
			exc.printStackTrace();
			throw new GestionException( exc );
		}
		c = casoTx.IniciaCaso( usuario, idTC, fg );
		log.error( usuario + "_" + idTC + "_" + fg );
		if ( c == null ) {
			log.error( "No se logro crear el caso" );
			throw new GestionException( "No se logró crear el caso" );
		}
		return c;
	}

	private List parseRequest( HttpServletRequest req ) throws ServletException {

		DiskFileUpload upload = new DiskFileUpload();

		upload.setRepositoryPath( tempDir );
		// Directorio temporal de carga de archivos

		// Si el archivo excede este tamaño, ocurre un excepcion
		// FileUploadException
		upload.setSizeMax( -1 ); // -1 sin limite

		try {
			return upload.parseRequest( req );
		} catch ( FileUploadException fe ) {
			fe.printStackTrace();
			throw new ServletException( "Error de recepcion " + fe.getMessage() );
		}
	}

}
