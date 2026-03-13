package com.syc.contable;


import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.syc.contable.core.CompromisoManager;
import com.syc.contable.core.ContratoFederalizadoManager;
import com.syc.contable.core.PagoProgFederalizadosManager;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.custom.FolioGeneratorInterface;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import com.syc.sai.contratos.core.ContratoFederalizadoBean;


public class ProgFederalizadosBussinessLogic extends DataSourceManager {

	private static Logger log = Logger.getLogger( AdecuacionBusinessLogic.class );

	public ProgFederalizadosBussinessLogic( String jniName ) {

		super.init( jniName );
	}

	public ArrayList<String> buscaCompromisos( String listaFolios, String listaCuentaBancaria, String listaFechas, String ListaLeyendas, String sUsuario, String sValorUMA ) throws Exception {
		ArrayList<String> arrListaComp = null;

		Connection conn = null;

		try {
			conn = getConnection();
			//if ("0".equals(sValorUMA)) {
				arrListaComp = PagoProgFederalizadosManager.BuscaCompromisos( conn, listaFolios, listaCuentaBancaria, listaFechas, ListaLeyendas, sUsuario );
			//} else {
				//arrListaComp = PagoProgFederalizadosManager.BuscaCompromisosMenorUMA(conn, listaFolios, listaCuentaBancaria, listaFechas, ListaLeyendas, sUsuario);
			//}	
			
			conn.commit();
			
		} catch ( SQLException e ) {
			if ( conn != null ) {
				e.printStackTrace();
				conn.rollback();
			}
		} finally {
			CloseObject.closeObject( conn );
		}
		return arrListaComp;
	}

	public boolean ActualizaStatus( String listaFolios ) throws Exception {

		Connection conn = null;

		try {
			conn = getConnection();
			PagoProgFederalizadosManager.UpdateStatus( conn, listaFolios );
			
			conn.commit();
			
		} catch ( SQLException e ) {
			if ( conn != null ) {
				e.printStackTrace();
				conn.rollback();
			}
		} finally {
			CloseObject.closeObject( conn );
		}
		return true;
	}

	public ArrayList<String> ArmaDocumentoComprobatorio( String listaIds ) throws Exception {
		ArrayList<String> arrListaComp = null;
		Connection conn = null;

		try {
			conn = getConnection();
			arrListaComp = PagoProgFederalizadosManager.CreaDocumentacionComprobatoria( conn, listaIds );
			PagoProgFederalizadosManager.updateHeaderCompromisos( conn, listaIds );
			
			conn.commit();
		} catch ( SQLException e ) {
			if ( conn != null ) {
				e.printStackTrace();
				conn.rollback();
			}
		} finally {
			CloseObject.closeObject( conn );
		}
		return arrListaComp;
	}

	public boolean actualizaCompromisos( Integer nEnviadoSICOP, String caNoCompromiso ) throws Exception {
		boolean regActualizado = false;

		Connection conn = null;

		try {
			conn = getConnection();
			regActualizado = PagoProgFederalizadosManager.updateHeaderCompromisosRealimentacion( conn, nEnviadoSICOP, caNoCompromiso );
			
			conn.commit();
			
		} catch ( SQLException e ) {
			if ( conn != null ) {
				conn.rollback();
				throw new GestionException( e.getMessage() );
			}
		} finally {
			
			CloseObject.closeObject( conn );
		}
		return regActualizado;
	}

	public boolean insertaLineaLayout( String clave, String cRamo, String cUnidadResponsable, String folioSICOP, String idProceso, String cCentroContable, java.sql.Date fExpedicion, float total, String cTipoPoliza, String nFolioPoliza, String nPolizaCancelacion, String tipoMovimiento, String origenPresupuesto, String cuentaBancaria, String noSolicitud, String tCambio, String tMoneda, String tSolicitud, String volante, String rfc, String caNoCompromiso, String codSemarnat2, String estatus, java.sql.Date fAplicacion, String documento, String nDocumento, String descripcion ) throws SQLException {
		boolean regInsertado = false;

		Connection conn = null;

		try {
			conn = getConnection();
			regInsertado = PagoProgFederalizadosManager.insertRegisterLayout( conn, clave, cRamo, cUnidadResponsable, folioSICOP, idProceso, cCentroContable, fExpedicion, total, cTipoPoliza, nFolioPoliza, nPolizaCancelacion, tipoMovimiento, origenPresupuesto, cuentaBancaria, noSolicitud, tCambio, tMoneda, tSolicitud, volante, rfc, caNoCompromiso, codSemarnat2, estatus, fAplicacion, documento, nDocumento, descripcion );
			
			conn.commit();
			
		} catch ( SQLException e ) {
			if ( conn != null ) {
				e.printStackTrace();
				conn.rollback();
			}
		} finally {
			
			CloseObject.closeObject( conn );
		}
		return regInsertado;
	}

	public Map<String, BigDecimal> parseCalendarDetail( HttpServletRequest req ) {

		Map<String, BigDecimal> calendar = new LinkedHashMap<String, BigDecimal>();
		for ( String nombreMes : Util.NOMBRE_MESES_MX ) {
			nombreMes = nombreMes.toLowerCase();
			String val = "".equals( StringUtils.trimToEmpty( req.getParameter( nombreMes ) ) ) ? "0.00" : req.getParameter( nombreMes );
			calendar.put( nombreMes, new BigDecimal( val ) );
		}

		return calendar;
	}

	public int insertaDetalle( String ep, ContratoFederalizadoBean contrato, Map<String, BigDecimal> calendario ) throws Exception {
		int insertados = 0;
		Connection conn = null;
		StringBuilder logOper = new StringBuilder();
		logOper.append( "Insertando: EP[" ).append( ep ).append( "] del contrato [ " );
		logOper.append( contrato ).append( "]" );

		try {
			conn = getConnection();
			log.info( logOper );
			insertados = ContratoFederalizadoManager.insertDetail( conn, ep, contrato, calendario );
			
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

	public int actualizaDetalle( String ep, ContratoFederalizadoBean contrato, Map<String, BigDecimal> calendario ) throws Exception {
		int insertados = 0;
		Connection conn = null;
		StringBuilder logOper = new StringBuilder();
		logOper.append( "Insertando: EP[" ).append( ep ).append( "] del contrato [ " );
		logOper.append( contrato ).append( "]" );

		try {

			conn = getConnection();
			log.info( "Limpiando detalle de la EP[" + ep + "] del contrato: " + contrato );
			int eliminados = ContratoFederalizadoManager.deleteDetail( conn, ep, contrato );
			log.info( "Se eliminaron " + eliminados + " renglones de detalle " );

			log.info( logOper );
			insertados = ContratoFederalizadoManager.insertDetail( conn, ep, contrato, calendario );
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

	public String creaCompromisoFederalizado( Caso c, Usuario u, FolioGeneratorInterface fg, ContratoFederalizadoBean cfb ) throws Exception {
		Connection conn = null;
		String contrarecibo = null;
		try {
			conn = getConnection();
			/* Crea el tramite de compromiso. */
			Caso casoCompromiso = CompromisoManager.generaCasoCompromiso( conn, u, fg, u.getLogin() );
			int folioCompromiso = Integer.parseInt( casoCompromiso.getFolio().substring( casoCompromiso.getFolio().lastIndexOf( '-' ) + 1 ) );

			/* Genera Contrarecibo */
			contrarecibo = CompromisoManager.generateCaNoCompromiso( cfb.getCentroContable(), String.valueOf( cfb.getEjercicioFiscal() ) );

			/* Inserta encabezado */
			int insertados = CompromisoManager.insertaCompromisoFederalizado( conn, cfb.getIdContrato(), folioCompromiso, contrarecibo, u.getU_Ramo(), cfb.getEjercicioFiscal(), u.getLogin() );
			/* Inserta detalle */
			insertados += CompromisoManager.insertaDetalleCompromisoFederalizado( conn, cfb.getIdContrato(), cfb.getEjercicioFiscal(), cfb.getCentroContable(), folioCompromiso );

			log.info( "Se insertaron " + insertados + " registros de compromiso" );
			/* Aplica Contablemente */
			AccountingEngine ae = new AccountingEngine();
			ae.setValidaInsuficienciaDeSaldo( true );
			ae.makeAccountingApplication( conn, "COMPROMISO", String.valueOf( folioCompromiso ), "tcompromisoencabezado", "tcompromisodetalle", "nFolioCompromiso" );

			/* Si todo fue correcto actualiza enviado SICOP */
			CompromisoManager.actualizaEnvioSICOPCompromiso( conn, folioCompromiso, 2 );

			/* Se avanza el compromiso a consulta. */
			Map<String, String> data = new HashMap<String, String>();
			data.put( "FOLIO", casoCompromiso.getCasoDato( "FOLIO" ).getValor() );
			data.put( "OPERADOR", casoCompromiso.getCasoDato( "OPERADOR" ).getValor() );
			data.put( "FECHA_DOCUMENTO", casoCompromiso.getCasoDato( "FECHA_DOCUMENTO" ).getValor() );
			data.put( "EJERCICIO_FISCAL", casoCompromiso.getCasoDato( "EJERCICIO_FISCAL" ).getValor() );
			CompromisoManager.avanzaCaso( conn, casoCompromiso, u.getLogin(), null, new String [] { "CONSULTA_COMPROMISO" }, new String [] { "consulta_compromiso" }, data, null );

			conn.commit();
			return contrarecibo;
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
}
